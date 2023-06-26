package hudson.plugins.logparser;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

class ParsingInput {
    private final String[] parsingRulesArray;
    private final Pattern[] compiledPatterns;
    private final InputStream log;
    private final String signature;
    private final String charsetName;

    ParsingInput(String[] parsingRulesArray, Pattern[] compiledPatterns, InputStream log, String signature, String charsetName) {
        this.parsingRulesArray = parsingRulesArray;
        this.compiledPatterns = compiledPatterns;
        this.log = log;
        this.signature = signature;
        this.charsetName = charsetName;
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

    public Charset getCharset() {
        return Charset.forName(charsetName);
    }
}
