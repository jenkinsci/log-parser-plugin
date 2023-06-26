package hudson.plugins.logparser;

import hudson.FilePath;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * This was the only available {@link ParsingStrategy} in 2.3.0 and earlier.
 * <p>
 * For each build, this strategy will:
 * <ol>
 *     <li>Copy the log into {@code java.io.tmpdir}</li>
 *     <li>Stream the copied log file to count lines via {@link LogParserUtils#countLines(String)}</li>
 *     <li>Create an {@link ExecutorService} via {@link Executors#newCachedThreadPool()}</li>
 *     <li>Determine number of {@link LogParserThread} tasks from lines / ({@link LogParserUtils#getLinesPerThread()} + 1)</li>
 *     <li>Submit and wait for all tasks to finish</li>
 *     <li>Aggregate lines into status from each task</li>
 * </ol>
 *
 * @since 2.4.0
 * @see StreamParsingStrategy
 */
class ClassicParsingStrategy implements ParsingStrategy {
    @Override
    public HashMap<String, String> parse(ParsingInput input) {
        final Logger logger = Logger.getLogger(this.getClass().getName());

        // Copy remote file to temp local location
        String tempDir = System.getProperty("java.io.tmpdir");
        if (!tempDir.endsWith(File.separator)) {
            final StringBuffer tempDirBuffer = new StringBuffer(tempDir);
            tempDirBuffer.append(File.separator);
            tempDir = tempDirBuffer.toString();
        }

        final String tempFileLocation = tempDir + "log-parser_" + input.getSignature();
        final File tempFile = new File(tempFileLocation);
        final FilePath tempFilePath = new FilePath(tempFile);

        try {
            tempFilePath.copyFrom(input.getLog());

            logger.log(Level.INFO, "Local temp file:" + tempFileLocation);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(tempFilePath.read(), input.getCharset()))) {
                String[] parsingRulesArray = input.getParsingRulesArray();
                Pattern[] compiledPatterns = input.getCompiledPatterns();
                int threadCounter = 0;

                final ArrayList<LogParserThread> runners = new ArrayList<>();
                final LogParserReader logParserReader = new LogParserReader(reader);

                final ExecutorService execSvc = Executors.newCachedThreadPool();
                int linesInLog = LogParserUtils.countLines(tempFileLocation);
                final int threadsNeeded = linesInLog
                        / LogParserUtils.getLinesPerThread() + 1;

                // Read and parse the log parts.  Keep the threads and results in an
                // array for future reference when writing
                for (int i = 0; i < threadsNeeded; i++) {
                    final LogParserThread logParserThread = new LogParserThread(
                            logParserReader, parsingRulesArray, compiledPatterns,
                            threadCounter);
                    runners.add(logParserThread);
                    execSvc.execute(logParserThread);
                    threadCounter++;
                }

                // Wait for all threads to finish before sequentially writing the
                // outcome
                execSvc.shutdown();
                execSvc.awaitTermination(3600, TimeUnit.SECONDS);

                // Sort the threads in the order of the log parts they read
                // It could be that thread #1 read log part #2 and thread #2 read log
                // part #1

                final int runnersSize = runners.size();
                LogParserThread[] sortedRunners = new LogParserThread[runnersSize];
                for (LogParserThread logParserThread : runners) {
                    final LogParserLogPart logPart = logParserThread.getLogPart();
                    if (logPart != null) {
                        final int logPartNum = logPart.getLogPartNum();
                        sortedRunners[logPartNum] = logParserThread;
                    }
                }

                final HashMap<String, String> result = new HashMap<>();
                HashMap<String, String> moreLineStatusMatches;
                for (int i = 0; i < runnersSize; i++) {
                    final LogParserThread logParserThread = sortedRunners[i];
                    if (logParserThread != null) {
                        moreLineStatusMatches = getLineStatusMatches(
                                logParserThread.getLineStatuses(), i);
                        result.putAll(moreLineStatusMatches);
                    }
                }
                return result;
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // Delete temp file
            try {
                tempFilePath.delete();
            } catch (IOException | InterruptedException e) {
                logger.log(Level.WARNING, "Failed to delete " + tempFilePath, e);
            }
        }
    }

    private HashMap<String, String> getLineStatusMatches(
            final String[] statuses, final int logPart) {
        final HashMap<String, String> result = new HashMap<>();
        String status;
        int line_num;
        final int linesPerThread = LogParserUtils.getLinesPerThread();
        for (int i = 0; i < statuses.length; i++) {
            status = statuses[i];
            line_num = i + logPart * linesPerThread;
            result.put(String.valueOf(line_num), status);
        }
        return result;
    }
}
