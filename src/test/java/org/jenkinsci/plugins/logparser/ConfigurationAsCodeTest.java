package org.jenkinsci.plugins.logparser;

import jenkins.model.Jenkins;
import hudson.plugins.logparser.LogParserPublisher;
import hudson.plugins.logparser.ParserRuleFile;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import io.jenkins.plugins.casc.ConfigurationAsCode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;

public class ConfigurationAsCodeTest {

    @Rule public JenkinsRule r = new JenkinsRule();

    @Test public void should_support_configuration_as_code_legacy_formatter() throws Exception {
        final Jenkins jenkins = Jenkins.getInstance();
        final LogParserPublisher.DescriptorImpl descriptor = (LogParserPublisher.DescriptorImpl) jenkins.getDescriptor(LogParserPublisher.class);

        ConfigurationAsCode.get().configure(ConfigurationAsCodeTest.class.getResource("configuration-as-code-legacy-formatting.yaml").toString());
        
        assertEquals(true, descriptor.getLegacyFormatting());
    }

    @Ignore("WIP")
    @Test public void should_support_configuration_as_code_parsing_rules() throws Exception {
        final Jenkins jenkins = Jenkins.getInstance();
        final LogParserPublisher.DescriptorImpl descriptor = (LogParserPublisher.DescriptorImpl) jenkins.getDescriptor(LogParserPublisher.class);

        ConfigurationAsCode.get().configure(ConfigurationAsCodeTest.class.getResource("configuration-as-code-parsing-rules.yaml").toString());
        System.out.println(descriptor.getParsingRulesGlobal());
        //ParserRuleFile[] parseRuleFile = descriptor.getParsingRulesGlobal();
        
        //assertEquals("Test Global Rules", parseRuleFile[0]);
    }
    
    @Ignore("Not finished")
    @Test public void export_configuration() throws Exception {
      ConfigurationAsCode.get().configure(ConfigurationAsCodeTest.class.getResource("configuration-as-code.yaml").toString());
      ConfigurationAsCode.get().export(System.out);
    }
}
