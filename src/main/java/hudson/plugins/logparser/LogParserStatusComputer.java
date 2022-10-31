package hudson.plugins.logparser;

import hudson.FilePath;
import hudson.remoting.RemoteInputStream;
import jenkins.security.MasterToSlaveCallable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class LogParserStatusComputer extends MasterToSlaveCallable<HashMap<String, String>, RuntimeException> {

    private static final long serialVersionUID = -6025098995519544527L;
    final private String[] parsingRulesArray;
    final private Pattern[] compiledPatterns;
    private final InputStream remoteLog;
    private final String signature;

    public LogParserStatusComputer(
            final InputStream log, final String[] parsingRulesArray,
            final Pattern[] compiledPatterns,
            final String signature) throws IOException, InterruptedException {
        this.parsingRulesArray = parsingRulesArray;
        this.compiledPatterns = compiledPatterns;
        this.remoteLog = new RemoteInputStream(log, RemoteInputStream.Flag.GREEDY);
        this.signature = signature;
    }

    public HashMap<String, String> call() {
        try {
            return computeStatusMatches(remoteLog, signature);
            // rethrow any exception here to report why the
            // parsing failed
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private HashMap<String, String> computeStatusMatches(
            final InputStream log,
            final String signature) throws IOException, InterruptedException {
        // SLAVE PART START

        final Logger logger = Logger.getLogger(this.getClass().getName());

        // Copy remote file to temp local location
        String tempDir = System.getProperty("java.io.tmpdir");
        if (!tempDir.endsWith(File.separator)) {
            final StringBuffer tempDirBuffer = new StringBuffer(tempDir);
            tempDirBuffer.append(File.separator);
            tempDir = tempDirBuffer.toString();
        }

        final String tempFileLocation = tempDir + "log-parser_" + signature;
        final File tempFile = new File(tempFileLocation);
        final FilePath tempFilePath = new FilePath(tempFile);
        tempFilePath.copyFrom(log);

        logger.log(Level.INFO, "Local temp file:" + tempFileLocation);
        ParsingStrategyLocator locator = ParsingStrategyLocator.create();
        ParsingStrategy strategy = locator.get();

        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(tempFilePath.read()))) {
            ParsingInput input = new ParsingInput(reader, tempFileLocation, parsingRulesArray, compiledPatterns);
            return strategy.parse(input);
        } finally {
            // Delete temp file
            tempFilePath.delete();
        }
        // SLAVE PART END
    }

}
