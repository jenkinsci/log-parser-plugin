package hudson.plugins.logparser;


import hudson.model.AbstractBuild;
import java.io.*;
import java.util.HashMap;
import java.util.regex.*;
import java.util.ArrayList;   

public class LogParserParser  {

	final private HashMap statusCount 	= new HashMap();
	final private HashMap writers 		= new HashMap();
	final private HashMap linkFiles 		= new HashMap();

    final private String[] parsingRulesArray;
    final private Pattern[] compiledPatterns;
    
    // if key is 3-ERROR it shows how many errors are in section 3
	final private HashMap statusCountPerSection = new HashMap(); 
	final private ArrayList headerForSection 		= new ArrayList();
	private int sectionCounter = 0;
	
	final private LogParserDisplayConsts displayConstants = new LogParserDisplayConsts();
	
	public LogParserParser(final String parsingRulesPath) throws FileNotFoundException , IOException {
		
		// Count of lines in this status
		statusCount.put(LogParserConsts.ERROR, 0);
		statusCount.put(LogParserConsts.WARNING, 0);
		statusCount.put(LogParserConsts.INFO, 0);

		this.parsingRulesArray = LogParserUtils.readParsingRules(parsingRulesPath);
		
		// This causes each regular expression to be compiled once for better performance
		this.compiledPatterns = LogParserUtils.compilePatterns(this.parsingRulesArray);
		
	} 
	
	
	/*
	 * This method creates the parsed log file : log.html 
	 * It also creates the lists of links to these errors/warnings/info messages respectively : 
	 * errorLinks.html, warningLinks.html, infoLinks.html 
	 */
    public LogParserResult parseLog(final AbstractBuild build) throws FileNotFoundException , IOException {
        
	
		// Get console log file
    	final File logFile = build.getLogFile();
        final String logDirectory = logFile.getParent();
        final String filePath = logFile.getAbsolutePath();
        
		// Determine parsed log files
        final String parsedFilePath 		 = logDirectory+"/log_content.html"; 	
        final String errorLinksFilePath 	 = logDirectory+"/logerrorLinks.html"; 	
        final String warningLinksFilePath 	 = logDirectory+"/logwarningLinks.html";
        final String infoLinksFilePath 		 = logDirectory+"/loginfoLinks.html";	
        final String buildRefPath 			 = logDirectory+"/log_ref.html"; 		
        final String buildWrapperPath 	 	 = logDirectory+"/log.html"; 			

        // Record file paths in hash
        linkFiles.put(LogParserConsts.ERROR, errorLinksFilePath);
        linkFiles.put(LogParserConsts.WARNING, warningLinksFilePath);
        linkFiles.put(LogParserConsts.INFO, infoLinksFilePath);
        
        // Open console log for reading and all other files for writing
        final BufferedReader reader = new BufferedReader(new FileReader(filePath));
        final BufferedWriter writer = new BufferedWriter(new FileWriter(parsedFilePath));

        // Record writers to links files in hash
        writers.put(LogParserConsts.ERROR, new BufferedWriter(new FileWriter(errorLinksFilePath)));
		writers.put(LogParserConsts.WARNING, new BufferedWriter(new FileWriter(warningLinksFilePath)));
		writers.put(LogParserConsts.INFO, new BufferedWriter(new FileWriter(infoLinksFilePath)));

        //Loop on the console log as long as there are input lines and parse line by line
		//At the end of this loop, we will have:
		// - a parsed log with colored lines
		// - 3 links files which will be consolidated into one referencing html file.
		
		// Create dummy header and section for beginning of log
		final String shortLink = " <a target=\"content\" href=\"log_content.html\">Beginning of log</a>";
		LogParserWriter.writeHeaderTemplateToAllLinkFiles(writers,sectionCounter); // This enters a line which will later be replaced by the actual header and count for this header
		headerForSection.add(shortLink);

		writer.write(LogParserConsts.htmlOpen);
        String line = null;
        while ((line=reader.readLine()) != null) {
        	final String status = getLineStatus(line);
        	final String parsedLine = parseLine(line,status);
        	// This is for displaying sections in the links part
        	writer.write(parsedLine);
            writer.newLine();   // Write system dependent end of line.
        }
		writer.write(LogParserConsts.htmlClose);
        
        //... Close reader and writer.
        reader.close();  // Close to unlock.
        writer.close();  // Close to unlock and flush to disk.

        ((BufferedWriter)writers.get(LogParserConsts.ERROR)).close();
		((BufferedWriter)writers.get(LogParserConsts.WARNING)).close();
		((BufferedWriter)writers.get(LogParserConsts.INFO)).close();

        
        // Build the reference html from the warnings/errors/info html files created in the loop above
        LogParserWriter.writeReferenceHtml(	buildRefPath,
        									headerForSection,
        									statusCountPerSection,
        									displayConstants.getIconTable(),
        									displayConstants.getLinkListDisplay(),
        									displayConstants.getLinkListDisplayPlural(),
        									statusCount,
        									linkFiles);       
        // Write the wrapping html for the reference page and the parsed log page
        LogParserWriter.writeWrapperHtml(buildWrapperPath);
        
        
        //String hudsonRoot = Hudson.getInstance().getRootUrl() ; // hudson link
        final String buildUrlPath = build.getUrl() ; //job/cat_log/58
        final String buildActionPath = LogParserAction.getUrlNameStat(); //"parsed_console";
        final String parsedLogURL = buildUrlPath+buildActionPath+"/log.html";   
        
        // Create result class
        final LogParserResult result = new LogParserResult();
        result.setHtmlLogFile(parsedFilePath);
        result.setTotalErrors((Integer)statusCount.get(LogParserConsts.ERROR));
        result.setTotalWarnings((Integer)statusCount.get(LogParserConsts.WARNING));
        result.setTotalInfos((Integer)statusCount.get(LogParserConsts.INFO));
        result.setErrorLinksFile(errorLinksFilePath);
        result.setWarningLinksFile(warningLinksFilePath);
        result.setInfoLinksFile(infoLinksFilePath);
        result.setParsedLogURL(parsedLogURL);
        result.setHtmlLogPath(logDirectory);
        return result;
        
    }

    

    
    

    
    
    public String getLineStatus(final String line) {
    	
    	for (int i=0;i<this.parsingRulesArray.length;i++){
    		final String parsingRule = this.parsingRulesArray[i];
    		if (!LogParserUtils.skipParsingRule(parsingRule) && 
    			this.compiledPatterns[i] != null && 
    			this.compiledPatterns[i].matcher(line).find()) { 
	 	    		 final String status = parsingRule.split("\\s")[0]; 
	    			 return LogParserUtils.standardizeStatus(status);
    		}
    	}

    	return LogParserConsts.NONE;
    }

    
	public String parseLine(final String line) throws IOException {
		return parseLine(line,null);
	}

	public String parseLine(final String line, final String status) throws IOException {
		String parsedLine = line;
		String effectiveStatus = status;
		if (status.equals(LogParserConsts.START)) {
			effectiveStatus = LogParserConsts.INFO;
		}
		parsedLine = parsedLine.replaceAll("<", "&lt;"); // Allows < to be seen in log which is html
		parsedLine = parsedLine.replaceAll(">", "&gt;"); // Allows > to be seen in log which is html
		if (effectiveStatus != null && !effectiveStatus.equals(LogParserConsts.NONE)) {
			// Increment count of the status 
			incrementCounter(effectiveStatus);
			incrementCounterPerSection(status,sectionCounter);
			// Color line according to the status
			final String parsedLineColored = colorLine(parsedLine,effectiveStatus);
			
			// Mark line and add to left side links of highlighted lines
			final String parsedLineColoredAndMarked = addMarkerAndLink(parsedLineColored,effectiveStatus,status);
			parsedLine = parsedLineColoredAndMarked;
		}
		final StringBuffer result = new StringBuffer(parsedLine);
		result.append("<br/>\n");
		return result.toString() ;
	}
	
	public void incrementCounter(final String status) {
		final int currentVal = (Integer)statusCount.get(status);
		statusCount.put(status, currentVal+1);
	}

	public void incrementCounterPerSection(final String status, final int sectionNumber) {
		final String key = LogParserUtils.getSectionCountKey(status,sectionNumber);
		Integer currentValInteger = (Integer)statusCountPerSection.get(key);
		// No value - entered yet - initialize with 0
		if (currentValInteger == null) {
			currentValInteger = new Integer(0);
		}
		final int newVal = currentValInteger+1;
		statusCountPerSection.put(key, newVal);
	}
	
	
	private String colorLine(final String line,final String status) {
		final String color = (String)displayConstants.getColorTable().get(status);
		final StringBuffer result = new StringBuffer("<font color=\"");
		result.append(color);
		result.append("\">");
		result.append(line);
		result.append("</font>");
		return result.toString();
	}

	private String addMarkerAndLink(final String line,final String effectiveStatus,final String status) throws IOException {
		// Add marker
		final String statusCountStr =  ((Integer)statusCount.get(effectiveStatus)).toString();
		final String marker = effectiveStatus + statusCountStr;
		
		// Add link
		final StringBuffer shortLink = new StringBuffer(" <a target=\"content\" href=\"log_content.html#");
		shortLink.append(marker);
		shortLink.append("\">");
		shortLink.append(line);
		shortLink.append("</a>");
		
		final StringBuffer link = new StringBuffer("<li>");
		link.append(statusCountStr);
		link.append(shortLink);
		link.append("</li><br/>");
		
		final BufferedWriter linkWriter = (BufferedWriter)writers.get(effectiveStatus);
		linkWriter.write(link.toString());
		linkWriter.newLine();   // Write system dependent end of line.
		
		// Mark the line
		final StringBuffer markedLine = new StringBuffer("<a name=\"");
		markedLine.append(marker);
		markedLine.append("\"></a>");
		markedLine.append(line);
		
		// Handle case where we are entering a new section
    	if (status.equals(LogParserConsts.START)) { 
			sectionCounter++;
			LogParserWriter.writeHeaderTemplateToAllLinkFiles(writers,sectionCounter); // This enters a line which will later be replaced by the actual header and count for this header
			final StringBuffer brShortLink = new StringBuffer("<br/>");
			brShortLink.append(shortLink);
			headerForSection.add(brShortLink.toString());
    	}
		
		return markedLine.toString();
	}
	


}
