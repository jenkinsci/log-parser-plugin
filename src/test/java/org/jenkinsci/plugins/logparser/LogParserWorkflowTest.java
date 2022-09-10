package org.jenkinsci.plugins.logparser;

import hudson.FilePath;
import hudson.Functions;
import hudson.plugins.logparser.LogParserAction;
import hudson.plugins.logparser.LogParserPublisher;
import hudson.slaves.DumbSlave;
import hudson.tasks.Maven;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.BuildWatcher;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.ToolInstallations;

import static org.junit.Assert.assertEquals;

/**
 * In this test suite we initialize the Job workspaces with a resource (maven-project1.zip) that contains a Maven
 * project.
 */
public class LogParserWorkflowTest {

    @ClassRule public static BuildWatcher buildWatcher = new BuildWatcher();

    @Rule public JenkinsRule jenkinsRule = new JenkinsRule();

    private LogParserAction result;

    @Before
    public void setup() throws Exception
    {
        Maven.MavenInstallation mavenInstallation = ToolInstallations.configureMaven35();
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "logParserPublisherWorkflowStep");
        DumbSlave agent = jenkinsRule.createOnlineSlave();
        FilePath workspace = agent.getWorkspaceFor(job);
        workspace.unzipFrom(LogParserWorkflowTest.class.getResourceAsStream("./maven-project1.zip"));
        job.setDefinition(new CpsFlowDefinition(""
                       + "node('" + agent.getNodeName() + "') {\n"
                       + "  def mvnHome = tool '" + mavenInstallation.getName() + "'\n"
                       + "  " + (Functions.isWindows() ? "bat" : "sh") + " \"${mvnHome}/bin/mvn clean install\"\n"
                       + "  step([$class: 'LogParserPublisher', projectRulePath: 'logparser-rules.txt', useProjectRule: true])\n"
                       + "}\n", true)
        );
        jenkinsRule.assertBuildStatusSuccess(job.scheduleBuild2(0));
        result = job.getLastBuild().getAction(LogParserAction.class);
    }

    /**
     * Run a workflow job using {@link LogParserPublisher} and check for success.
     */
    @Test
    public void logParserPublisherWorkflowStep() throws Exception {
       assertEquals(0, result.getResult().getTotalErrors());
       assertEquals(2, result.getResult().getTotalWarnings());
       assertEquals(0, result.getResult().getTotalInfos());
    }

    /**
     * Run a workflow job using {@link LogParserPublisher} and check for number of debug tags
     */
    @Test
    public void logParserPublisherWorkflowStepDebugTags() throws Exception {
        assertEquals(0, result.getResult().getTotalDebugs());
    }

    /**
     * Run a workflow job using {@link LogParserPublisher} and check for number of example arbitrary tags
     */
    @Test
    public void logParserPublisherWorkflowStepArbitraryTags() throws Exception {
        assertEquals(0, result.getResult().getTotalCountsByExtraTag("jenkins"));
        assertEquals(1, result.getResult().getTotalCountsByExtraTag("logParserPublisherWorkflowStep"));
    }
}
