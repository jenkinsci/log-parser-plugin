package hudson.plugins.logparser;

import java.util.Arrays;   
import java.util.List;   

public class LogParserConsts  {

	public static final String ERROR 	= "ERROR"; 
	public static final String WARNING 	= "WARNING"; 
	public static final String INFO 	= "INFO"; 
	public static final String NONE 	= "NONE"; 
	public static final String START 	= "START"; // marks a beginning of a section 
	public static final String DEFAULT 	= NONE; 

	public static final String CANNOT_PARSE 	= "log-parser plugin ERROR: Cannot parse log"; 

	public static final List<String> LEGAL_STATUS = Arrays.asList(ERROR, WARNING, INFO, NONE, START);   
	public static final List<String> STATUSES_WITH_LINK_FILES = Arrays.asList(ERROR, WARNING, INFO);
	public static final List<String> STATUSES_WITH_SECTIONS_IN_LINK_FILES = Arrays.asList(ERROR, WARNING);

	// Used for getting Hudson stylesheets for uniformity of look and feel .
	public static final String htmlOpen = 	"<html>\n"+
								"\t<head>\n"+
								"\t\t<link type=\"text/css\" rel=\"stylesheet\" href=\"/hudson/css/style.css\"></link>\n"+
								"\t\t<link type=\"text/css\" rel=\"stylesheet\" href=\"/hudson/css/color.css\"></link>\n"+
								"\t</head>\n"+
								"\t<body>\n";

	public static final String htmlClose = "\t</body>\n"+
								"</html>\n";


}
