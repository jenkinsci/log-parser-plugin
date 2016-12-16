package org.jenkinsci.plugins.logparser;

import hudson.FilePath;
import hudson.Functions;
import hudson.model.Run;
import hudson.plugins.logparser.LogParserAction;
import hudson.plugins.logparser.LogParserPublisher;
import hudson.tasks.Maven;
import org.apache.commons.io.FileUtils;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.ToolInstallations;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * In this test suite we initialize the Job workspaces with a resource (maven-project1.zip) that contains a Maven
 * project.
 */
public class LogParserWorkflowTest {

    @ClassRule
    public static JenkinsRule jenkinsRule = new JenkinsRule();

    private static Maven.MavenInstallation mavenInstallation;

    @BeforeClass
    public static void init() throws Exception {
        mavenInstallation = ToolInstallations.configureMaven3();
    }

    /**
     * Run a workflow job using {@link LogParserPublisher} and check for success.
     */
    @Test
    public void logParserPublisherWorkflowStep() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "logParserPublisherWorkflowStep");
        FilePath workspace = jenkinsRule.jenkins.getWorkspaceFor(job);
        workspace.unzipFrom(getClass().getResourceAsStream("./maven-project1.zip"));
        job.setDefinition(new CpsFlowDefinition(""
                        + "node {\n"
                        + "  def mvnHome = tool '" + mavenInstallation.getName() + "'\n"
                        + "  " + (Functions.isWindows() ? "bat" : "sh") +  "\"${mvnHome}/bin/mvn clean install\"\n"
                        + "  step([$class: 'LogParserPublisher', projectRulePath: 'logparser-rules.txt', useProjectRule: true])\n"
                        + "}\n", true)
        );
        jenkinsRule.assertBuildStatusSuccess(job.scheduleBuild2(0));
        LogParserAction result = job.getLastBuild().getAction(LogParserAction.class);
        assertEquals(0, result.getResult().getTotalErrors());
        assertEquals(2, result.getResult().getTotalWarnings());
        assertEquals(0, result.getResult().getTotalInfos());
    }

    @Test
    public void logParserPublisherPipelineStep() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "logParserPublisherWorkflowStep");
        FilePath workspace = jenkinsRule.jenkins.getWorkspaceFor(job);
        workspace.unzipFrom(getClass().getResourceAsStream("./maven-project1.zip"));
        job.setDefinition(new CpsFlowDefinition(""
                + "node {\n"
                + "  def mvnHome = tool '" + mavenInstallation.getName() + "'\n"
                + "  " + (Functions.isWindows() ? "bat" : "sh") + " \"${mvnHome}/bin/mvn clean install\"\n"
                + "  def res = logparser(projectRulePath: 'logparser-rules.txt', useProjectRule: true)\n"
                + "  echo \"Total Errors: ${res.totalErrors}\"\n"
                + "  echo \"Total Warnings: ${res.totalWarnings}\"\n"
                + "  echo \"Total Infos: ${res.totalInfos}\"\n"
                + "}\n", true)
        );
        Run<?,?> build = job.scheduleBuild2(0).get();
        jenkinsRule.assertBuildStatusSuccess(build);
        LogParserAction result = build.getAction(LogParserAction.class);
        assertEquals(0, result.getResult().getTotalErrors());
        assertEquals(2, result.getResult().getTotalWarnings());
        assertEquals(0, result.getResult().getTotalInfos());
        String log = FileUtils.readFileToString(build.getLogFile());
        assertTrue(log.contains("Total Errors: 0"));
        assertTrue(log.contains("Total Warnings: 2"));
        assertTrue(log.contains("Total Infos: 0"));
    }
}
