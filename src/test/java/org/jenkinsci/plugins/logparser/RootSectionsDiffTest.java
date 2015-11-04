package org.jenkinsci.plugins.logparser;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import hudson.plugins.logparser.*;

public class RootSectionsDiffTest {

	@Test
	public void testAdded()
	{
		RootSections section1 = new RootSections(1);
		RootSections section2 = new RootSections(2);
		
		section1.setData(LogParserConsts.ERROR, Arrays.asList(
				"abc"
		));
		section2.setData(LogParserConsts.ERROR, Arrays.asList(
				"abc",
				"bcd"
		));
		RootSectionsDiff diff = new RootSectionsDiff(section1, section2);
		assertEquals(Arrays.asList("bcd"),diff.getDiffSections(true, LogParserConsts.ERROR));
	}
	

	@Test
	public void testRemoved()
	{
		RootSections section1 = new RootSections(1);
		RootSections section2 = new RootSections(2);
		
		section1.setData(LogParserConsts.ERROR, Arrays.asList(
				"abc",
				"bcd"
		));
		section2.setData(LogParserConsts.ERROR, Arrays.asList(
				"abc"
		));
		RootSectionsDiff diff = new RootSectionsDiff(section1, section2);
		System.out.println();
		assertEquals(Arrays.asList("bcd"),diff.getDiffSections(false, LogParserConsts.ERROR));
	}
	
	
}
