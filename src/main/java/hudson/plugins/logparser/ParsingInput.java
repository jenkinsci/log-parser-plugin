package hudson.plugins.logparser;

import java.io.BufferedReader;
import java.util.regex.Pattern;

class ParsingInput {
    private final BufferedReader reader;
    private final String logPath;
    private final String[] parsingRulesArray;
    private final Pattern[] compiledPatterns;

    ParsingInput(BufferedReader reader, String logPath, String[] parsingRulesArray, Pattern[] compiledPatterns) {
        this.reader = reader;
        this.logPath = logPath;
        this.parsingRulesArray = parsingRulesArray;
        this.compiledPatterns = compiledPatterns;
    }

    public BufferedReader getReader() {
        return reader;
    }

    public String getLogPath() {
        return logPath;
    }

    public String[] getParsingRulesArray() {
        return parsingRulesArray;
    }

    public Pattern[] getCompiledPatterns() {
        return compiledPatterns;
    }
}
