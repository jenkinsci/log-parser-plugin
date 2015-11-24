package hudson.plugins.logparser;

import jenkins.model.Jenkins;

import java.util.Arrays;
import java.util.List;

public class LogParserConsts {

    public static final String ERROR = "ERROR";
    public static final String WARNING = "WARNING";
    public static final String INFO = "INFO";
    public static final String DEBUG = "DEBUG";
    public static final String NONE = "NONE";
    public static final String START = "START"; // marks a beginning of a section
    public static final String DEFAULT = NONE;

    // Error messages
    public static final String CANNOT_PARSE = "log-parser plugin ERROR: Cannot parse log ";
    public static final String NOT_INT = " is not an integer - using default";

    public static final List<String> LEGAL_STATUS = Arrays.asList(ERROR, WARNING, INFO, DEBUG, NONE, START);
    public static final List<String> STATUSES_WITH_LINK_FILES = Arrays.asList(ERROR, WARNING, INFO, DEBUG);
    public static final List<String> STATUSES_WITH_SECTIONS_IN_LINK_FILES = Arrays.asList(ERROR, WARNING, DEBUG);

    public static String getHtmlOpeningTags() {
        final String hudsonRoot = Jenkins.getActiveInstance().getRootUrl();
        return "<!DOCTYPE html>\n" + "<html>\n" + "\t<head>\n"
                + "\t\t<title>log-parser plugin page</title>\n"
                + "\t\t<link type=\"text/css\" rel=\"stylesheet\" href=\""
                + hudsonRoot + "css/style.css\" />\n"
                + "\t\t<link type=\"text/css\" rel=\"stylesheet\" href=\""
                + hudsonRoot + "css/color.css\" />\n" + "\t</head>\n"
                + "\t<body>\n";
    }

    public static final String getHtmlClosingTags() {
        return "\t</body>\n" + "</html>\n";
    }

    // Parsing in threads for performance
    public static final int LINES_PER_THREAD = 10000; // How many lines to parse
                                                      // in each thread
    public static final int MAX_THREADS = 2; // How many concurrent threads to
                                             // run (unused when implementing
                                             // cached thread pool)

}
