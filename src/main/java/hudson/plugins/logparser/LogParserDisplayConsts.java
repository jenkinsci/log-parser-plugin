package hudson.plugins.logparser;

import java.util.HashMap;

public class LogParserDisplayConsts {
	
	final private HashMap<String,String> colorTable 			= new HashMap<String,String>();
	final private HashMap<String,String> iconTable 				= new HashMap<String,String>();
	final private HashMap<String,String> linkListDisplay 		= new HashMap<String,String>();
	final private HashMap<String,String> linkListDisplayPlural 	= new HashMap<String,String>();

	public LogParserDisplayConsts() {
		// Color of each status
		colorTable.put(LogParserConsts.ERROR, "red");
		colorTable.put(LogParserConsts.WARNING, "orange");
		colorTable.put(LogParserConsts.INFO, "blue");
		colorTable.put(LogParserConsts.START, "blue");

		// Icon for each status in the summary
		iconTable.put(LogParserConsts.ERROR, "red.gif");
		iconTable.put(LogParserConsts.WARNING, "yellow.gif");
		iconTable.put(LogParserConsts.INFO, "blue.gif");

		// How to display in link summary html
		linkListDisplay.put(LogParserConsts.ERROR, "Error");
		linkListDisplay.put(LogParserConsts.WARNING, "Warning");
		linkListDisplay.put(LogParserConsts.INFO, "Info");

		linkListDisplayPlural.put(LogParserConsts.ERROR, "Errors");
		linkListDisplayPlural.put(LogParserConsts.WARNING, "Warnings");
		linkListDisplayPlural.put(LogParserConsts.INFO, "Infos");
	}

	public HashMap<String,String> getColorTable() {
		return colorTable;
	}

	public HashMap<String,String> getIconTable() {
		return iconTable;
	}

	public HashMap<String,String> getLinkListDisplay() {
		return linkListDisplay;
	}

	public HashMap<String,String> getLinkListDisplayPlural() {
		return linkListDisplayPlural;
	}

}
