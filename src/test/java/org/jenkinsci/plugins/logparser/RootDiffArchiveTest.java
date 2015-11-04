package org.jenkinsci.plugins.logparser;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import static org.junit.Assert.*;

import hudson.model.Job;
import hudson.plugins.logparser.RootDiffArchive;

public class RootDiffArchiveTest {

	@Test
	public void testDiffArchiveGetJobs() throws Exception{
		RootDiffArchive diffArchive = new RootDiffArchive();
		ArrayList<String> difArchiveJobs=diffArchive.getJobs();
		File rootFolder=diffArchive.getRootFolder();
		File[] rootFolderFiles=rootFolder.listFiles(); 
		ArrayList<String> rootFolderFilesString= new ArrayList<String>();
		for(File f:rootFolderFiles){
			rootFolderFilesString.add(f.getName());
		}
		assertArrayEquals(rootFolderFilesString.toArray(),difArchiveJobs.toArray());
	}

	//Job Class
	@Test 
	public void testDiffArchiveGetJobPath() throws Exception{
		RootDiffArchive diffArchive = new RootDiffArchive();
		
		String jobPath=diffArchive.job("logparserplugin").getJobPath();
		assertEquals("diffArchive"+File.separator+"logparserplugin", jobPath);	

	}
	
	//Build Class
	
	@Test 
	public void testDiffArchiveGetConfigFile() throws Exception{
		
		//might need to execute perform function first
		String userHome=System.getProperty("user.home");
		final String JENKINSHOME=userHome+"/"+".jenkins";
		
		RootDiffArchive diffArchive = new RootDiffArchive();
		File original = new File(JENKINSHOME+"/jobs/"+diffArchive.job("logparserplugin").getJobPath()+"/config.xml");
		File archive = new File(JENKINSHOME+"/jobs/diffArchive/logparserplugin/build1/config.xml");
		
		assertTrue(FileUtils.contentEquals(original, archive));
	}
	
	
	@Test 
	public void testDiffArchiveGetPomFile() throws Exception{
		String userHome=System.getProperty("user.home");
		final String JENKINSHOME=userHome+"/"+".jenkins";
		
		RootDiffArchive diffArchive = new RootDiffArchive();
		File original = new File(JENKINSHOME+"/jobs/"+diffArchive.job("logparserplugin").getJobPath()+"/workspace/pom.xml");
		File archive = new File(JENKINSHOME+"/jobs/diffArchive/logparserplugin/build1/pom.xml");
		
		assertTrue(FileUtils.contentEquals(original, archive));
	}

	
	@Test 
	public void testDiffArchiveGetConsoleFile() throws Exception{
		String userHome=System.getProperty("user.home");
		final String JENKINSHOME=userHome+"/"+".jenkins";
		
		RootDiffArchive diffArchive = new RootDiffArchive();
		File original = new File(JENKINSHOME+"/jobs/"+diffArchive.job("logparserplugin").getJobPath()+"/builds/1/log");
		File archive = new File(JENKINSHOME+"/jobs/diffArchive/logparserplugin/build1/console.txt");
		
		assertTrue(FileUtils.contentEquals(original, archive));
	}
	
}
