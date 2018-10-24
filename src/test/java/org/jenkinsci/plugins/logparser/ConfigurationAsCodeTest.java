package org.jenkinsci.plugins.logparser;

import org.junit.Rule;
import org.junit.rules.RuleChain;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import io.jenkins.plugins.casc.ConfigurationAsCode;
import static org.junit.Assert.assertTrue;

public class ConfigurationAsCodeTest {

    @Rule public JenkinsRule r = new JenkinsRule();

    @Test public void should_support_configuration_as_code() throws Exception {
        ConfigurationAsCode.get().configure(ConfigurationAsCodeTest.class.getResource("configuration-as-code.yaml").toString());
        
        assertTrue(true);
    }
    
    @Test public void export_configuration() throws Exception {
      ConfigurationAsCode.get().configure(ConfigurationAsCodeTest.class.getResource("configuration-as-code.yaml").toString());
      ConfigurationAsCode.get().export(System.out);
    }
}
