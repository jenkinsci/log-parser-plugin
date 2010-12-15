package hudson.plugins.logparser;

import hudson.Extension;
import hudson.Plugin;

public class PluginImpl extends Plugin {

    @Extension
    public static final LogParserPublisher.DescriptorImpl LOG_PARSER_DESCRIPTOR = LogParserPublisher.DescriptorImpl.DESCRIPTOR;

    public static LogParserPublisher.DescriptorImpl getDescriptor() {
        return LOG_PARSER_DESCRIPTOR;
    }

}
