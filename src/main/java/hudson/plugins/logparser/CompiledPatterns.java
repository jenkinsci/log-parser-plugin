package hudson.plugins.logparser;

import java.util.regex.Pattern;

public class CompiledPatterns {

    private String errorMsg = null;
    private Pattern[] compiledPatterns = null;

    public CompiledPatterns() {
    }

    public String getError() {
        return errorMsg;
    }

    public void setError(final String errorMsg) {
        if (errorMsg == null || errorMsg.trim().length() == 0) {
            this.errorMsg = null;
        } else {
            this.errorMsg = errorMsg.trim();
        }
    }

    public Pattern[] getCompiledPatterns() {
        return compiledPatterns;
    }

    public void setCompiledPatters(final Pattern[] compiledPatterns) {
        this.compiledPatterns = compiledPatterns;
    }

}
