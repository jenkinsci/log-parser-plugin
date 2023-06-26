package hudson.plugins.logparser;

import hudson.remoting.RemoteInputStream;
import jenkins.security.MasterToSlaveCallable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.regex.Pattern;

public class LogParserStatusComputer extends MasterToSlaveCallable<HashMap<String, String>, RuntimeException> {

    private static final long serialVersionUID = 646211554890510833L;
    final private String[] parsingRulesArray;
    final private Pattern[] compiledPatterns;
    private final InputStream remoteLog;
    private final String signature;
    private final String charsetName;

    public LogParserStatusComputer(
            final InputStream log,
            final String[] parsingRulesArray,
            final Pattern[] compiledPatterns,
            final String signature,
            final Charset charset) {
        this.parsingRulesArray = parsingRulesArray;
        this.compiledPatterns = compiledPatterns;
        this.remoteLog = new RemoteInputStream(log, RemoteInputStream.Flag.GREEDY);
        this.signature = signature;
        this.charsetName = charset.name();
    }

    /**
     * Prefer the other constructor that allows for passing in the {@link Charset}.
     * This constructor relies on the default charset.
     * @param log
     * @param parsingRulesArray
     * @param compiledPatterns
     * @param signature
     * @throws IOException
     * @throws InterruptedException
     */
    @Deprecated
    public LogParserStatusComputer(
            final InputStream log, final String[] parsingRulesArray,
            final Pattern[] compiledPatterns,
            final String signature) throws IOException, InterruptedException {
        this(log, parsingRulesArray, compiledPatterns, signature, Charset.defaultCharset());
    }

    public HashMap<String, String> call() {
        try {
            return computeStatusMatches(remoteLog, signature, charsetName);
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
            final String signature,
            final String charsetName) throws IOException, InterruptedException {
        // SLAVE PART START
        ParsingStrategyLocator locator = ParsingStrategyLocator.create();
        ParsingStrategy strategy = locator.get();

        ParsingInput input = new ParsingInput(parsingRulesArray, compiledPatterns, log, signature, charsetName);
        return strategy.parse(input);
        // SLAVE PART END
    }

}
