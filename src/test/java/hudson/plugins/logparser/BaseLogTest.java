package hudson.plugins.logparser;

import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.Test;

import hudson.model.FreeStyleProject;
import hudson.model.Result;
import hudson.model.Run;

public class BaseLogTest extends LogParserTestCase {
    
    @Test
    public void testWorkflowWithLogParserStepBadConfig() throws Exception {
        WorkflowJob project = setupWorkflowProject(true);
        
        Run<?, ?> build = build(project);
        
        assertLogParserCorrect(build, project, Result.ABORTED);
    }
    
    @Test
    public void testWorkflowWithLogParserStep() throws Exception {
        WorkflowJob project = setupWorkflowProject(false);
        
        Run<?, ?> build = build(project);
        
        assertLogParserCorrect(build, project, Result.FAILURE);
    }
    
    @Test
    public void testFreeStyleProjectWithLogParserStepBadConfig() throws Exception {
        FreeStyleProject project = setupFreeStyleProject(true);
        
        Run<?, ?> build = build(project);
        
        assertLogParserCorrect(build, project, Result.ABORTED);
    }
    
    @Test
    public void testFreeStyleProjectWithLogParserStep() throws Exception {
        FreeStyleProject project = setupFreeStyleProject(false);
        
        Run<?, ?> build = build(project);
        
        assertLogParserCorrect(build, project, Result.FAILURE);
    }
    
}
