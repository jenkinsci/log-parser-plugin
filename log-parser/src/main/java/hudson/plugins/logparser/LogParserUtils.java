package hudson.plugins.logparser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public final class LogParserUtils {


	public static String[] readParsingRules(final String parsingRulesPath) throws FileNotFoundException , IOException {
		final StringBuffer result = new StringBuffer("");
		final BufferedReader reader = new BufferedReader(new FileReader(parsingRulesPath));
        String line = null;
        while ((line=reader.readLine()) != null) {
        	result.append(line);
        	result.append('\n');
        }
        reader.close();
		return result.toString().split("\n");
	}

	
    public static boolean skipParsingRule(final String parsingRule){
    	boolean skip = false;
		if (parsingRule == null ||
    			parsingRule.equals("") ||
    			parsingRule.charAt(0) == '#' ||
    			parsingRule.startsWith("\\s") ||
    			parsingRule.startsWith("\r") ||// Carriage return
    			parsingRule.contains("$header")) { // for now - disregard rules with header in them 
        		skip = true;
    		}
		return skip;
    	
    }
    
    public static String standardizeStatus(final String status){
    	String result = status;
		if (result.equalsIgnoreCase("ok")) {
			result = LogParserConsts.NONE;
		}
		else if (result.equalsIgnoreCase("end")	) {
			result = LogParserConsts.INFO;
		}
		else if (result.equalsIgnoreCase("warn") || 
			result.equalsIgnoreCase("end")	) {
			result = LogParserConsts.WARNING;
		}
		else {
			result = result.toUpperCase(Locale.ENGLISH);
		}   
	
	    // If some non-existent status is in the configuration - disregard it
		final List<String> legals = LogParserConsts.LEGAL_STATUS;
		if (!legals.contains(result)) {
			result = LogParserConsts.DEFAULT;
		}

		return result;
    	
    }
 
    public static  CompiledPatterns compilePatterns(final String[] parsingRulesArray, final Logger logger) {
    	
    	Pattern[] result = new Pattern[parsingRulesArray.length];
    	final StringBuffer badParsingRules = new StringBuffer(); 
    	
    	for (int i=0;i<parsingRulesArray.length;i++){
    		final String parsingRule = parsingRulesArray[i];
    		result[i] = null;
    		if (!skipParsingRule(parsingRule)) {
    			try {
		    		final String ruleParts[] = parsingRule.split("\\s");
		    		String regexp = ruleParts[1];
		    		
		    		final int firstDash = parsingRule.indexOf('/');
		    		final int lastDash = parsingRule.lastIndexOf('/');
		    		if (firstDash != -1 && firstDash != -1){
		        		regexp = parsingRule.substring(firstDash+1, lastDash);
		        		final Pattern p = Pattern.compile(regexp);
		        		result[i] = p;
		   			
		    		} 
    			} catch (Exception e){
    				// Could not use rule for some reason - ignore rule and log it
    				final String errorMsg ="Bad parsing rule:"+parsingRule+", Error:"+e.getMessage();
        			logger.log(Level.SEVERE,errorMsg);
        			badParsingRules.append('\n');
        			badParsingRules.append(errorMsg);
    			}
    		}
    	}
    	
    	final CompiledPatterns fullResult = new CompiledPatterns();
    	fullResult.setCompiledPatters(result);
    	fullResult.setError(badParsingRules.toString());
    	return fullResult;
    }
    
	public static String getSectionCountKey(final String status,final int sectionNumber) {
		return Integer.toString(sectionNumber)+"-"+status;
	}

	
  	public static int getNumThreads(){
  		int result = LogParserConsts.MAX_THREADS; 
  		final String maxThreadsByEnvStr = System.getenv("HUDSON_LOG_PARSER_THREADS");
  		if (maxThreadsByEnvStr != null) {
  			try {
  				result = (Integer.valueOf(maxThreadsByEnvStr)).intValue();
  			} catch (Exception e) {
  				// Do nothing - use the default;
  				Logger.getLogger("getNumThreads").log(Level.FINEST,"HUDSON_LOG_PARSER_THREADS"+LogParserConsts.NOT_INT);
  			}
  		}
  		return result;
  	}

  	public static int getLinesPerThread(){
  		int result = LogParserConsts.LINES_PER_THREAD; 
  		final String linesByEnvStr = System.getenv("HUDSON_LOG_PARSER_LINES_PER_THREAD");
  		if (linesByEnvStr != null) {
  			try {
  				result = (Integer.valueOf(linesByEnvStr)).intValue();
  			} catch (Exception e) {
  				// Do nothing - use the default; 
  				Logger.getLogger("getLinesPerThread").log(Level.FINEST,"HUDSON_LOG_PARSER_LINES_PER_THREAD"+LogParserConsts.NOT_INT);
  			}
  		}
  		return result;
  	}

  	public static int countLines(final String filename) throws IOException { 
  	    final LineNumberReader reader  = new LineNumberReader(new FileReader(filename)); 
  	    int count = 0; 
  	    while (reader.readLine() != null) {
  	    	// Read the whole file to count the lines.
  	    	count++;
  	    } 
  	    count = reader.getLineNumber();  
  	    reader.close(); 
  	    return count; 
  	}
  	
	private LogParserUtils() {
		// PMD warning to use singleton or bypass by private empty constructor 
	}

}
