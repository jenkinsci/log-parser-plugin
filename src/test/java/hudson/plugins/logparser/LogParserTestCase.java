package hudson.plugins.logparser;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.Rule;
import org.jvnet.hudson.test.JenkinsRule;

import hudson.Functions;
import hudson.model.AbstractProject;
import hudson.model.Cause;
import hudson.model.CauseAction;
import hudson.model.FreeStyleProject;
import hudson.model.Job;
import hudson.model.Result;
import hudson.model.Run;
import hudson.plugins.logparser.action.LogParserProjectAction;
import hudson.tasks.BatchFile;
import hudson.tasks.Shell;
import jenkins.triggers.SCMTriggerItem;

public abstract class LogParserTestCase {
    
    @Rule
    public JenkinsRule j = new JenkinsRule();
    
    int timeout = j.timeout = 0;
    
    protected Run<?, ?> build(Job<?, ?> project) throws Exception {
        Cause.UserIdCause cause = new Cause.UserIdCause();
        Run<?, ?> build = null;
        if (project instanceof AbstractProject) {
            build = ((AbstractProject<?, ?>) project).scheduleBuild2(0, cause).get();
        } else if (project instanceof SCMTriggerItem) {
            build = (Run<?, ?>) ((SCMTriggerItem) project).scheduleBuild2(0, new CauseAction(cause)).get();
        }
        return build;
    }
    
    protected void assertLogParserCorrect(Run<?, ?> build, Job<?, ?> project, Result result) throws IOException {
        // Check that preparation has been logged
        assertThat(JenkinsRule.getLog(build)).contains("This is a LogParserTest");
        
        assertThat(build.getResult()).isEqualTo(result);
        assertThat(build.getAction(LogParserAction.class)).as(LogParserAction.class.getSimpleName() + " not found")
                .isNotNull();
                
        boolean aborted = Result.ABORTED.equals(build.getResult());
        
        LogParserResult parserResult = build.getAction(LogParserAction.class).getResult();
        assertThat(parserResult).isNotNull();
        
        if (!aborted) {
            assertThat(parserResult.getTotalErrors()).isEqualTo(1);
            assertThat(parserResult.getTotalWarnings()).isEqualTo(1);
            assertThat(parserResult.getTotalInfos()).isEqualTo(1);
        }
        
        if (!(project instanceof AbstractProject) || aborted) {
            assertThat(project.getAction(LogParserProjectAction.class))
                    .as(LogParserProjectAction.class.getSimpleName() + " found").isNull();
        } else {
            assertThat(project.getAction(LogParserProjectAction.class))
                    .as(LogParserProjectAction.class.getSimpleName() + " not found").isNotNull();
        }
    }
    
    protected WorkflowJob setupWorkflowProject(boolean broken) throws IOException {
        WorkflowJob project = j.jenkins.createProject(WorkflowJob.class, "p");
        
        project.setDefinition(new CpsFlowDefinition(""
            + "node { \n"
            + "  echo 'This is a LogParserTest'\n"
            + "  echo 'INFO'\n"
            + "  echo 'WARN'\n"
            + "  echo 'ERROR'\n"
            + "}\n"
            // FIXME this splitting into two nodes is because the normal log file does not contain the messages
            // until the block is done, they are in the numbered log filed for each workflow step though
            + "node { \n"
            + "  writeFile file: 'temp.rules', text: \"\"\"error /ERROR/\nwarn /WARN/\ninfo /INFO/\n\"\"\"\n"
            + "  step([$class: 'LogParserPublisher'" + (broken ? "" : ", failBuildOnError: true, unstableOnWarning: true, showGraphs: true, useProjectRule: true, projectRulePath: 'temp.rules'") + "])\n"
            + "}\n"
        ));
        return project;
    }
    
    protected FreeStyleProject setupFreeStyleProject(boolean broken) throws URISyntaxException, IOException {
        LogParserPublisher logParser = null;
        if (broken) {
            logParser = new LogParserPublisher();
        } else {
            logParser = new LogParserPublisher();
            logParser.setFailBuildOnError(true);
            logParser.setUnstableOnWarning(true);
            logParser.setShowGraphs(true);
            logParser.setParsingRulesPath(new File(getClass().getResource("/hudson/plugins/logparser/LogParserTestCase/temp.rules").toURI()).getAbsolutePath());
        }
        FreeStyleProject project = j.createFreeStyleProject("FreeStyleProject");
        // Setup SCM
        // project.setScm(new NullSCM());
        if (Functions.isWindows()) {
            project.getBuildersList().add(new BatchFile("@echo \"This is a LogParserTest\" && echo \"INFO\" && echo \"WARN\" && echo \"ERROR\""));
        } else {
            project.getBuildersList().add(new Shell("echo \"This is a LogParserTest\" && echo \"INFO\" && echo \"WARN\" && echo \"ERROR\""));
        }
        // Setup SonarQube step
        project.getPublishersList().add(logParser);
        
        return project;
    }
}
