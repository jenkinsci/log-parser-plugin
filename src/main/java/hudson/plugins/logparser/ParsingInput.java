package hudson.plugins.logparser;

import java.io.InputStream;
import java.util.regex.Pattern;

class ParsingInput {
    private final String[] parsingRulesArray;
    private final Pattern[] compiledPatterns;
    private final InputStream log;
    private final String signature;

    ParsingInput(String[] parsingRulesArray, Pattern[] compiledPatterns, InputStream log, String signature) {
        this.parsingRulesArray = parsingRulesArray;
        this.compiledPatterns = compiledPatterns;
        this.log = log;
        this.signature = signature;
    }

    public String[] getParsingRulesArray() {
        return parsingRulesArray;
    }

    public Pattern[] getCompiledPatterns() {
        return compiledPatterns;
    }

    public InputStream getLog() {
        return log;
    }

    public String getSignature() {
        return signature;
    }
}
