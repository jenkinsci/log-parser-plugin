package hudson.plugins.logparser;

import hudson.PluginWrapper;
import hudson.model.Hudson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public final class LogParserWriter {

	public static void writeHeaderTemplateToAllLinkFiles(final HashMap writers,final int sectionCounter) throws IOException {
		final List<String> statuses = LogParserConsts.STATUSES_WITH_SECTIONS_IN_LINK_FILES;
		final int statusesSize=statuses.size();
		for (int i=0;i<statusesSize;i++){
			final String currentStatus = (String)statuses.get(i);
			final BufferedWriter linkWriter = (BufferedWriter)writers.get(currentStatus);	
			String str = "HEADER HERE: #NUMBER";
			str = str.replaceFirst("NUMBER", ((Integer)sectionCounter).toString());
			linkWriter.write(str+"\n");
		}
		
	}

    public static void writeWrapperHtml(final String buildWrapperPath)  throws IOException {
    	final String wrapperHtml = 	"<frameset cols=\"270,*\">\n"+
    	  						"<frame src=\"log_ref.html\" scrolling=auto name=\"sidebar\">\n"+
    	  						"<frame src=\"log_content.html\" scrolling=auto name=\"content\">\n"+
    	  						"<noframes>\n"+
    	  						"<p>Viewing the build report requires a Frames-enabled browser</p>\n"+
    	  						"<a href='build.log'>build log</a>\n"+
    	  						"</noframes>\n"+
    	  						"</frameset>\n";   	
    	
    	final BufferedWriter writer = new BufferedWriter(new FileWriter(buildWrapperPath));
        writer.write(wrapperHtml);
        writer.close();
    }

    public static void writeReferenceHtml(final String buildRefPath,
			final ArrayList headerForSection,
			final HashMap statusCountPerSection,
			final HashMap iconTable,
			final HashMap linkListDisplay,
			final HashMap linkListDisplayPlural,
			final HashMap statusCount,
			final HashMap linkFiles
) throws IOException {
    	
    	final String refStart = 	"<base target=\"content\">\n" +
    						"<script language=\"JavaScript\" type=\"text/javascript\">\n"+
    							"\tfunction toggleList(list){\n"+
    								"\t\telement = document.getElementById(list).style;\n"+
    								"\t\telement.display == 'none' ? element.display='block' : element.display='none';\n"+
    							"\t}\n"+
 	   						"</script>\n";

    	
    	final BufferedWriter writer = new BufferedWriter(new FileWriter(buildRefPath));
    	writer.write(LogParserConsts.htmlOpen); // Hudson stylesheets
    	writer.write(refStart); // toggle links javascript
    	// Write Errors
    	writeLinks(writer,LogParserConsts.ERROR, headerForSection, statusCountPerSection, iconTable, linkListDisplay, linkListDisplayPlural, statusCount, linkFiles);
    	// Write Warnings
    	writeLinks(writer,LogParserConsts.WARNING,  headerForSection, statusCountPerSection, iconTable, linkListDisplay, linkListDisplayPlural, statusCount, linkFiles);
    	// Write Info
    	writeLinks(writer,LogParserConsts.INFO, headerForSection, statusCountPerSection, iconTable, linkListDisplay, linkListDisplayPlural, statusCount, linkFiles);
    	writer.write(LogParserConsts.htmlClose);
    	writer.close();  // Close to unlock and flush to disk.
 
    }

    private static void writeLinks(final BufferedWriter writer, 
    						final String status,
    						final ArrayList headerForSection,
    						final HashMap statusCountPerSection,
    						final HashMap iconTable,
    						final HashMap linkListDisplay,
    						final HashMap linkListDisplayPlural,
    						final HashMap statusCount,
    						final HashMap linkFiles
    						)  throws IOException {
    	final String statusIcon = (String)iconTable.get(status);
       	final String linkListDisplayStr = (String)linkListDisplay.get(status);
       	final String linkListDisplayStrPlural = (String)linkListDisplayPlural.get(status);
       	final String linkListCount = ((Integer)statusCount.get(status)).toString();
       	
       	final String hudsonRoot = Hudson.getInstance().getRootUrl();
		final PluginWrapper wrapper = Hudson.getInstance().getPluginManager().getPlugin(PluginImpl.class);
		final String iconLocation  = "/plugin/" + wrapper.getShortName() + "/images/" ; 

		final String linksStart = "<img src=\""+hudsonRoot+"/"+iconLocation+statusIcon+"\" style=\"margin: 2px;\" width=\"24\" alt=\"\" height=\"24\"></img>\n"+
  							"<a href=\"javascript:toggleList('"+linkListDisplayStr+"')\" target=\"_self\"><STRONG>"+linkListDisplayStr+" ("+linkListCount+")</STRONG></a><br/>\n"+
							"<ul id=\""+linkListDisplayStr+"\" type=\"disc\" style=\"display:none\">\n";

    	writer.write(linksStart); 
    	
    	// Read the links file and insert here
        final BufferedReader reader = new BufferedReader(new FileReader((String)linkFiles.get(status)));
    	String line = null;
    	final String summaryLine = "<br/>(SUMMARY_INT_HERE LINK_LIST_DISPLAY_STR in this section)<br/>";

    	final String headerTemplateRegexp = "HEADER HERE:";
    	final String headerTemplateSplitBy = "#";

    	// If it's a header line - put the header of the section
        while ((line=reader.readLine()) != null) {
        	String curSummaryLine = null;
        	if (line.startsWith(headerTemplateRegexp)) {
        		final String headerNum = line.split(headerTemplateSplitBy)[1];
        		line = (String)headerForSection.get(Integer.parseInt(headerNum));
        		final String key = LogParserUtils.getSectionCountKey(status,Integer.valueOf(headerNum));
        		final Integer summaryInt = (Integer)statusCountPerSection.get(key); 
        		if (summaryInt == null || summaryInt == 0) {
        			line = null; // Don't write the header if there are no relevant lines for this section
        		} else {
        			String linkListDisplayStrWithPlural = linkListDisplayStr;
        			if (summaryInt > 1) {
        				linkListDisplayStrWithPlural = linkListDisplayStrPlural;
        			}
        			curSummaryLine = summaryLine.replace("SUMMARY_INT_HERE", summaryInt.toString()).replace("LINK_LIST_DISPLAY_STR", linkListDisplayStrWithPlural);
        		}
        		 
        	} 
        	
        	if (line != null) {
		    	writer.write(line);
		        writer.newLine();   // Write system dependent end of line.
        	}
        	if (curSummaryLine != null) {
		    	writer.write(curSummaryLine);
		        writer.newLine();   // Write system dependent end of line.
        	}
        }
        reader.close();  // Close to unlock.
    	
        final String linksEnd = 	"</ul>\n";
    	writer.write(linksEnd);
    	
    }

	private LogParserWriter() {
		// PMD warning to use singleton or bypass by private empty constructor 
	}
 
}
