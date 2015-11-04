package hudson.plugins.logparser;
import java.util.*;

public class RootSections {
	int build;
	HashMap<String, List<String>> data;
	public RootSections(int build)
	{
		this.build = build;
		data = new HashMap<String, List<String>>();
	}
	public void setData(String sectionName, List<String> list)
	{
		data.put(sectionName, list);
	}
	public RootSections(int build, List<String> list)
	{
//		LogParserConsts.ERROR,
//		LogParserConsts.WARNING,
//		LogParserConsts.INFO
		this(build);
		List<String> errorList = new ArrayList<String>();
		List<String> warningList = new ArrayList<String>();
		List<String> infoList = new ArrayList<String>();
		for(String s : list)
		{
			if(s.startsWith("["+LogParserConsts.ERROR))
				errorList.add(s);
			else if(s.startsWith("["+LogParserConsts.INFO))
				infoList.add(s);
			else if(s.startsWith("["+LogParserConsts.WARNING))
				warningList.add(s);
			
		}
		setData(LogParserConsts.ERROR, errorList);
		setData(LogParserConsts.INFO, infoList);
		setData(LogParserConsts.WARNING, warningList);
		
	}
	
}
