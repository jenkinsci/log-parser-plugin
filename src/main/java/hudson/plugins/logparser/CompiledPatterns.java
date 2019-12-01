package hudson.plugins.logparser;

import javax.annotation.CheckForNull;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CompiledPatterns {

    private String errorMsg;
    private Pattern[] compiledPatterns;
    private List<String> extraTags;

    public CompiledPatterns() {
        this.errorMsg = null;
        this.compiledPatterns = null;
        this.extraTags = new ArrayList<>();
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

    @CheckForNull
    public Pattern[] getCompiledPatterns() {
        return compiledPatterns;
    }

    public void setCompiledPatters(final Pattern[] compiledPatterns) {
        this.compiledPatterns = compiledPatterns;
    }

    public List<String> getExtraTags() {
        return extraTags;
    }

    public void setExtraTags(List<String> extraTags) {
        this.extraTags = extraTags;
    }
}
