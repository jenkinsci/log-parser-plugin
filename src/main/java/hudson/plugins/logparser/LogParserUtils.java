package hudson.plugins.logparser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
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
 
    public static  Pattern[] compilePatterns(final String[] parsingRulesArray) {
    	
    	Pattern[] result = new Pattern[parsingRulesArray.length];
    	
    	for (int i=0;i<parsingRulesArray.length;i++){
    		final String parsingRule = parsingRulesArray[i];
    		result[i] = null;
    		if (!skipParsingRule(parsingRule)) {
	    		final String ruleParts[] = parsingRule.split("\\s");
	    		String regexp = ruleParts[1];
	    		
	    		final int firstDash = parsingRule.indexOf('/');
	    		final int lastDash = parsingRule.lastIndexOf('/');
	    		if (firstDash != -1 && firstDash != -1){
	        		regexp = parsingRule.substring(firstDash+1, lastDash);
	        		final Pattern p = Pattern.compile(regexp);
	        		result[i] = p;
	   			
	    		} 
    		}
    	}
    	return result;
    }
    
	public static String getSectionCountKey(final String status,final int sectionNumber) {
		return Integer.toString(sectionNumber)+"-"+status;
	}

	private LogParserUtils() {
		// PMD warning to use singleton or bypass by private empty constructor 
	}

}
