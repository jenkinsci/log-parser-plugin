package org.jenkinsci.plugins.logparser;

import jenkins.model.Jenkins;
import hudson.plugins.logparser.LogParserPublisher;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import io.jenkins.plugins.casc.ConfigurationAsCode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;

public class ConfigurationAsCodeTest {

    @Rule public JenkinsRule r = new JenkinsRule();

    @Test public void should_support_configuration_as_code() throws Exception {
        final Jenkins jenkins = Jenkins.getInstance();
        final LogParserPublisher.DescriptorImpl descriptor = (LogParserPublisher.DescriptorImpl) jenkins.getDescriptor(LogParserPublisher.class);

        ConfigurationAsCode.get().configure(ConfigurationAsCodeTest.class.getResource("configuration-as-code.yaml").toString());
        
        assertEquals(true, descriptor.getLegacyFormatting());
    }
    
    @Ignore
    @Test public void export_configuration() throws Exception {
      ConfigurationAsCode.get().configure(ConfigurationAsCodeTest.class.getResource("configuration-as-code.yaml").toString());
      ConfigurationAsCode.get().export(System.out);
    }
}
