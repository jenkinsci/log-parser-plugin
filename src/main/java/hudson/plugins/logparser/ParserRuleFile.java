package hudson.plugins.logparser;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

public class ParserRuleFile extends AbstractDescribableImpl<ParserRuleFile> {

    private String name = null;
    private String path = null;

    public ParserRuleFile() {
        // Empty constructor
    }

    @DataBoundConstructor
    public ParserRuleFile(final String name, final String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    @DataBoundSetter
    public void setName(final String name) {
        this.name = name;
    }

    @DataBoundSetter
    public void setPath(final String path) {
        this.path = path;
    }

    @Extension @Symbol("rule")
    public static class DescriptorImpl extends Descriptor<ParserRuleFile> {
    }
}
