package hudson.plugins.logparser;

import java.util.HashMap;

public class LogParserDisplayConsts {
	
	final private HashMap colorTable 			= new HashMap();
	final private HashMap iconTable 			= new HashMap();
	final private HashMap linkListDisplay 		= new HashMap();
	final private HashMap linkListDisplayPlural = new HashMap();

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

	public HashMap getColorTable() {
		return colorTable;
	}

	public HashMap getIconTable() {
		return iconTable;
	}

	public HashMap getLinkListDisplay() {
		return linkListDisplay;
	}

	public HashMap getLinkListDisplayPlural() {
		return linkListDisplayPlural;
	}

}
