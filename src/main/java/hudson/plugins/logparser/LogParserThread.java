package hudson.plugins.logparser;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

class LogParserThread extends Thread {

    private LogParserLogPart logPart;
    private final String[] parsingRulesArray;
    private final Pattern[] compiledPatterns;
    private final int threadNum;
    private String[] logPartStatuses;
    private int numOfLines;
    private final LogParserReader logParserReader;

    public LogParserThread(final LogParserReader logParserReader,
            final String[] parsingRulesArray, final Pattern[] compiledPatterns,
            final int threadNum) {
        this.parsingRulesArray = parsingRulesArray;
        this.compiledPatterns = compiledPatterns;
        this.threadNum = threadNum;
        this.logParserReader = logParserReader;
    }

    @Override
    public void run() {
        try {
            // Synchronized method so as not to read from the same file from
            // several threads.
            logPart = logParserReader.readLogPart(this.threadNum);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        logPartStatuses = getLineStatuses(logPart.getLines());
        
        /*
        try {
            logPartStatuses = channel
                    .call(new Callable<String[], RuntimeException>() {
                        public String[] call() {
                            return getLineStatuses(logPart.getLines());
                        }
                    });
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RuntimeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        */

    }

    public String[] getLineStatuses() {
        return this.logPartStatuses;
    }

    public LogParserLogPart getLogPart() {
        return this.logPart;
    }

    public int getNumOfLines() {
        return this.numOfLines;
    }

    private String[] getLineStatuses(final String[] logPart) {

        final Logger logger = Logger.getLogger(this.getClass().getName());
        logger.log(Level.INFO, "LogParserThread: Start parsing log part "
                + this.logPart.getLogPartNum());

        numOfLines = 0;
        String[] result = new String[logPart.length];
        for (int i = 0; i < logPart.length; i++) {
            final String line = logPart[i];
            if (line == null) {
                continue;
            }
            numOfLines++;
            final String status = getLineStatus(line);
            result[i] = status;
        }

        logger.log(Level.INFO, "LogParserThread: Done parsing log part "
                + this.logPart.getLogPartNum());

        return result;
    }

    private String getLineStatus(String line) {
        List<ParsingRulePattern> patterns = new LinkedList<>();
        for (int i = 0; i < parsingRulesArray.length; i++) {
            String rule = parsingRulesArray[i];
            Pattern pattern = compiledPatterns[i];
            patterns.add(new ParsingRulePattern(rule, pattern));
        }
        LineToStatus toStatus = new LineToStatus(patterns);
        return toStatus.apply(line);
    }

    public int getThreadNum() {
        return threadNum;
    }

}
