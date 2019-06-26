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

import java.util.List;

public class ConfigurationAsCodeTest {

    @Rule
    public JenkinsRule r = new JenkinsRule();

    @Test
    public void LegacyFormattingTest() throws Exception {
        final Jenkins jenkins = Jenkins.getInstance();
        final LogParserPublisher.DescriptorImpl descriptor = (LogParserPublisher.DescriptorImpl) jenkins.getDescriptor(LogParserPublisher.class);
        ConfigurationAsCode.get().configure(ConfigurationAsCodeTest.class.getResource("configuration-as-code-legacy-formatting.yaml").toString());
        assertEquals(true, descriptor.getLegacyFormatting());
    }

    @Test
    public void ParsingRulesTest() throws Exception {
        final Jenkins jenkins = Jenkins.getInstance();
        final LogParserPublisher.DescriptorImpl descriptor = (LogParserPublisher.DescriptorImpl) jenkins.getDescriptor(LogParserPublisher.class);

        ConfigurationAsCode.get().configure(ConfigurationAsCodeTest.class.getResource("configuration-as-code-parsing-rules.yaml").toString());
        List<ParserRuleFile> parseRuleFiles = descriptor.getParsingRulesGlobal();

        assertEquals(1, parseRuleFiles.size());
        assertEquals("Test Global Rules", parseRuleFiles.get(0).getName());
        assertEquals("./maven-project1.zip", parseRuleFiles.get(0).getPath());
    }

    @Ignore("Not finished")
    @Test
    public void export_configuration() throws Exception {
        ConfigurationAsCode.get().configure(ConfigurationAsCodeTest.class.getResource("configuration-as-code.yaml").toString());
        ConfigurationAsCode.get().export(System.out);
    }
}
