package org.jenkinsci.plugins.logparser;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.ClassRule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import hudson.FilePath;
import hudson.plugins.logparser.LogParserAction;
import hudson.plugins.logparser.LogParserLineDiff;

public class LogParserLineDiffTest {
	
    @ClassRule
    public static JenkinsRule jenkinsRule = new JenkinsRule();
	
	@Test
	public void automatedTest() throws Exception{
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "logParserPublisherWorkflowStep");
        FilePath workspace = jenkinsRule.jenkins.getWorkspaceFor(job);
        job.setDefinition(new CpsFlowDefinition("node{step([$class: 'LogParserPublisher', projectRulePath: 'logparser-rules.txt', useProjectRule: true])}"));
        jenkinsRule.assertBuildStatusSuccess(job.scheduleBuild2(0));
        LogParserAction result = job.getLastBuild().getAction(LogParserAction.class);
        
        assertEquals(0, result.getResult().getTotalDeltas());
	}
	
	@Test
	public void testLineDiff1(){
		List<String> original = new ArrayList<String>();
		List<String> revised = new ArrayList<String>();
		
		original.add("first line");
		original.add("second line");
		original.add("third line");
		original.add("fourth line");
		
		revised.add("firt line");
		revised.add("third line");
		revised.add("fouth line");
		
		LogParserLineDiff d = new LogParserLineDiff();
		int numOfDeltas = d.lineDiff(original, revised);
		assertEquals(2, numOfDeltas);
	}
	
	@Test
	public void testLineDiff2(){
		List<String> original = new ArrayList<String>();
		List<String> revised = new ArrayList<String>();
		
		original.add("first line");
		original.add("second line");
		original.add("third line");
		original.add("fourth line");
		
		revised.add("first line");
		revised.add("second line");
		revised.add("third line");
		revised.add("fouth line");
		
		LogParserLineDiff d = new LogParserLineDiff();
		int numOfDeltas = d.lineDiff(original, revised);
		assertEquals(1, numOfDeltas);
	}
	
	@Test
	public void testLineDiff3(){
		List<String> original = new ArrayList<String>();
		List<String> revised = new ArrayList<String>();
		
		original.add("first line");
		original.add("second line");
		original.add("fourth line");
		original.add("fifth line");
		
		revised.add("first line");
		revised.add("second line");
		revised.add("third line");
		revised.add("fourth line");
		revised.add("four point five");
		revised.add("fifth line");
		revised.add("sixth line");
		
		LogParserLineDiff d = new LogParserLineDiff();
		int numOfDeltas = d.lineDiff(original, revised);
		assertEquals(3, numOfDeltas);
	}

}
