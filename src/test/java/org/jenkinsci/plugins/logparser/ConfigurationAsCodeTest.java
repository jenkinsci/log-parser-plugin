package org.jenkinsci.plugins.logparser;

import jenkins.model.Jenkins;
import hudson.plugins.logparser.LogParserPublisher;
import hudson.plugins.logparser.ParserRuleFile;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import io.jenkins.plugins.casc.ConfigurationAsCode;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@WithJenkins
class ConfigurationAsCodeTest {

    @Test
    void legacyFormattingTest(JenkinsRule r) {
        final LogParserPublisher.DescriptorImpl descriptor = (LogParserPublisher.DescriptorImpl) Jenkins.get().getDescriptor(LogParserPublisher.class);
        ConfigurationAsCode.get().configure(ConfigurationAsCodeTest.class.getResource("configuration-as-code-legacy-formatting.yaml").toString());
        assertTrue(descriptor.getLegacyFormatting());
    }

    @Test
    void parsingRulesTest(JenkinsRule r) {
        final LogParserPublisher.DescriptorImpl descriptor = (LogParserPublisher.DescriptorImpl) Jenkins.get().getDescriptor(LogParserPublisher.class);
        ConfigurationAsCode.get().configure(ConfigurationAsCodeTest.class.getResource("configuration-as-code-parsing-rules.yaml").toString());
        List<ParserRuleFile> parseRuleFiles = descriptor.getParsingRulesGlobal();

        assertEquals(1, parseRuleFiles.size());
        assertEquals("Test Global Rules", parseRuleFiles.get(0).getName());
        assertEquals("./maven-project1.zip", parseRuleFiles.get(0).getPath());
    }

    @Disabled("Not finished")
    @Test
    void export_configuration(JenkinsRule r) throws Exception {
        ConfigurationAsCode.get().configure(ConfigurationAsCodeTest.class.getResource("configuration-as-code.yaml").toString());
        ConfigurationAsCode.get().export(System.out);
    }
}
