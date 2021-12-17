package hudson.plugins.logparser;

import hudson.FilePath;
import hudson.console.ConsoleNote;
import hudson.model.AbstractBuild;
import hudson.model.Run;
import hudson.remoting.VirtualChannel;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class LogParserParser {

    final private HashMap<String, Integer> statusCount = new HashMap<String, Integer>();
    final private HashMap<String, BufferedWriter> writers = new HashMap<String, BufferedWriter>();
    final private HashMap<String, String> linkFiles = new HashMap<String, String>();

    final private String[] parsingRulesArray;
    final private Pattern[] compiledPatterns;
    final private CompiledPatterns compiledPatternsPlusError;
    final private List<String> extraTags;

    // if key is 3-ERROR it shows how many errors are in section 3
    final private HashMap<String, Integer> statusCountPerSection = new HashMap<String, Integer>();
    final private ArrayList<String> headerForSection = new ArrayList<String>();
    private int sectionCounter = 0;

    final private LogParserDisplayConsts displayConstants = new LogParserDisplayConsts();

    final private VirtualChannel channel;
    final private boolean preformattedHtml;

    public LogParserParser(final FilePath parsingRulesFile,
            final boolean preformattedHtml, final VirtualChannel channel)
            throws IOException {

        // init logger
        final Logger logger = Logger.getLogger(getClass().getName());

        this.parsingRulesArray = LogParserUtils
                .readParsingRules(parsingRulesFile);

        // This causes each regular expression to be compiled once for better
        // performance
        this.compiledPatternsPlusError = LogParserUtils.compilePatterns(
                this.parsingRulesArray, logger);
        this.compiledPatterns = this.compiledPatternsPlusError
                .getCompiledPatterns();
        this.extraTags = this.compiledPatternsPlusError.getExtraTags();

        this.preformattedHtml = preformattedHtml;
        this.channel = channel;

        // Count of lines in this status
        statusCount.put(LogParserConsts.ERROR, 0);
        statusCount.put(LogParserConsts.WARNING, 0);
        statusCount.put(LogParserConsts.INFO, 0);
        statusCount.put(LogParserConsts.DEBUG, 0);
        for (String extraTag : this.extraTags) {
            statusCount.put(extraTag, 0);
        }
    }

    /*
     * This method creates the parsed log file : log.html It also creates the
     * lists of links to these errors/warnings/info messages respectively :
     * errorLinks.html, warningLinks.html, infoLinks.html
     */
    @Deprecated
    public LogParserResult parseLog(final AbstractBuild build) throws IOException, InterruptedException {
        return this.parseLog((Run<?, ?>) build);
    }

    public LogParserResult parseLog(final Run<?, ?> build) throws IOException, InterruptedException {

        // init logger
        final Logger logger = Logger.getLogger(getClass().getName());

        // Get console log file
        final InputStream log = build.getLogInputStream();
        final String logDirectory = build.getRootDir().getAbsolutePath();

        // Determine parsed log files
        final String parsedFilePath = logDirectory + "/log_content.html";
        final String errorLinksFilePath = logDirectory + "/logerrorLinks.html";
        final String warningLinksFilePath = logDirectory + "/logwarningLinks.html";
        final String infoLinksFilePath = logDirectory + "/loginfoLinks.html";
        final String debugLinksFilePath = logDirectory + "/logdebugLinks.html";
        final Map<String, String> linksFilePathByExtraTags = new HashMap<String, String>();
        for (String extraTag : this.extraTags) {
            linksFilePathByExtraTags.put(extraTag, logDirectory + "/log" + extraTag + "Links.html");
        }
        final String buildRefPath = logDirectory + "/log_ref.html";
        final String buildWrapperPath = logDirectory + "/log.html";

        // Record file paths in HashMap
        linkFiles.put(LogParserConsts.ERROR, errorLinksFilePath);
        linkFiles.put(LogParserConsts.WARNING, warningLinksFilePath);
        linkFiles.put(LogParserConsts.INFO, infoLinksFilePath);
        linkFiles.put(LogParserConsts.DEBUG, debugLinksFilePath);
        for (String extraTag : this.extraTags) {
            linkFiles.put(extraTag, linksFilePathByExtraTags.get(extraTag));
        }

        // Open console log for reading and all other files for writing
        final BufferedWriter writer = new BufferedWriter(new FileWriter(
                parsedFilePath));

        // Record writers to links files in hash
        writers.put(LogParserConsts.ERROR, new BufferedWriter(new FileWriter(
                errorLinksFilePath)));
        writers.put(LogParserConsts.WARNING, new BufferedWriter(new FileWriter(
                warningLinksFilePath)));
        writers.put(LogParserConsts.INFO, new BufferedWriter(new FileWriter(
                infoLinksFilePath)));
        writers.put(LogParserConsts.DEBUG, new BufferedWriter(new FileWriter(
                debugLinksFilePath)));
        for (String extraTag : this.extraTags) {
            writers.put(extraTag, new BufferedWriter(new FileWriter(
                    linksFilePathByExtraTags.get(extraTag))));
        }

        // Loop on the console log as long as there are input lines and parse
        // line by line
        // At the end of this loop, we will have:
        // - a parsed log with colored lines
        // - 4 links files which will be consolidated into one referencing html
        // file.

        // Create dummy header and section for beginning of log
        final String shortLink = " <a target=\"content\" href=\"log_content.html\">Beginning of log</a>";
        LogParserWriter.writeHeaderTemplateToAllLinkFiles(writers, sectionCounter); // This enters a line which will later be
                                 // replaced by the actual header and count for
                                 // this header
        headerForSection.add(shortLink);
        writer.write(LogParserConsts.getHtmlOpeningTags());
		
		// write styles for log body
        final String styles = "<style>\n"
			+ "  body {margin-left:.5em; }\n"
			+ "  pre {font-family: Consolas, \"Courier New\"; word-wrap: break-word; }\n"
			+ "  pre span {word-wrap: break-word; } \n"
			+ "</style>\n";
        writer.write(styles);

        if (this.preformattedHtml)
            writer.write("<pre>");
        // Read bulks of lines, parse
        parseLogBody(build, writer, log,
                logger);

        // Write parsed output, links, etc.
        //writeLogBody();

        // Close html footer
        if (this.preformattedHtml)
            writer.write("</pre>");
        writer.write(LogParserConsts.getHtmlClosingTags());
        writer.close(); // Close to unlock and flush to disk.

        ((BufferedWriter) writers.get(LogParserConsts.ERROR)).close();
        ((BufferedWriter) writers.get(LogParserConsts.WARNING)).close();
        ((BufferedWriter) writers.get(LogParserConsts.INFO)).close();
        ((BufferedWriter) writers.get(LogParserConsts.DEBUG)).close();
        for (String extraTag : this.extraTags) {
            ((BufferedWriter) writers.get(extraTag)).close();
        }

        // Build the reference html from the warnings/errors/info html files
        // created in the loop above
        LogParserWriter.writeReferenceHtml(buildRefPath, headerForSection,
                statusCountPerSection, displayConstants.getIconTable(),
                displayConstants.getLinkListDisplay(),
                displayConstants.getLinkListDisplayPlural(), statusCount,
                linkFiles, extraTags);
        // Write the wrapping html for the reference page and the parsed log page
        LogParserWriter.writeWrapperHtml(buildWrapperPath);

        final String buildUrlPath = build.getUrl(); // job/cat_log/58
        final String buildActionPath = LogParserAction.getUrlNameStat(); // "parsed_console";
        final String parsedLogURL = buildUrlPath + buildActionPath + "/log.html";

        // Create result class
        final LogParserResult result = new LogParserResult();
        result.setHtmlLogFile(parsedFilePath);
        result.setTotalErrors((Integer) statusCount.get(LogParserConsts.ERROR));
        result.setTotalWarnings((Integer) statusCount.get(LogParserConsts.WARNING));
        result.setTotalInfos((Integer) statusCount.get(LogParserConsts.INFO));
        result.setTotalDebugs((Integer) statusCount.get(LogParserConsts.DEBUG));
        for (String extraTag : this.extraTags) {
            result.putTotalCountsByExtraTag(extraTag, (Integer) statusCount.get(extraTag));
        }
        result.setErrorLinksFile(errorLinksFilePath);
        result.setWarningLinksFile(warningLinksFilePath);
        result.setInfoLinksFile(infoLinksFilePath);
        result.setDebugLinksFile(debugLinksFilePath);
        for (String extraTag : this.extraTags) {
            result.putLinksFileByExtraTag(extraTag, linksFilePathByExtraTags.get(extraTag));
        }
        result.setParsedLogURL(parsedLogURL);
        result.setHtmlLogPath(logDirectory);
        result.setBadParsingRulesError(this.compiledPatternsPlusError.getError());
        result.setExtraTags(this.extraTags);

        return result;

    }

    public String parseLine(final String line) throws IOException {
        return parseLine(line, null);
    }

    public String parseLine(final String line, final String status)
            throws IOException {
        String parsedLine = line;
        String effectiveStatus = status;
        if (status == null) {
            effectiveStatus = LogParserConsts.NONE;
        } else if (status.equals(LogParserConsts.START)) {
            effectiveStatus = LogParserConsts.INFO;
        }

        // need to strip out for display also (in addition to parsing).
        parsedLine = ConsoleNote.removeNotes(parsedLine);
        // Allows < to be seen in log which is html
        parsedLine = parsedLine.replaceAll("<", "&lt;");
        // Allows > to be seen in log which is html
        parsedLine = parsedLine.replaceAll(">", "&gt;");

        if (effectiveStatus != null
                && !effectiveStatus.equals(LogParserConsts.NONE)) {
            // Increment count of the status
            incrementCounter(effectiveStatus);
            incrementCounterPerSection(effectiveStatus, sectionCounter);
            // Color line according to the status
            final String parsedLineColored = colorLine(parsedLine,
                    effectiveStatus);

            // Mark line and add to left side links of highlighted lines
            final String parsedLineColoredAndMarked = addMarkerAndLink(
                    parsedLineColored, effectiveStatus, status);
            parsedLine = parsedLineColoredAndMarked;
        }
        final StringBuffer result = new StringBuffer(parsedLine);
        if (!preformattedHtml)
            result.append("<br/>\n");
        return result.toString();
    }

    public void incrementCounter(final String status) {
        final int currentVal = (Integer) statusCount.get(status);
        statusCount.put(status, currentVal + 1);
    }

    public void incrementCounterPerSection(final String status,
            final int sectionNumber) {
        final String key = LogParserUtils.getSectionCountKey(status,
                sectionNumber);
        Integer currentValInteger = (Integer) statusCountPerSection.get(key);
        // No value - entered yet - initialize with 0
        if (currentValInteger == null) {
            currentValInteger = new Integer(0);
        }
        final int newVal = currentValInteger + 1;
        statusCountPerSection.put(key, newVal);
    }

    private String colorLine(final String line, final String status) {
        String color = (String) displayConstants.getColorTable().get(status);
        if (color == null) {
            color = LogParserDisplayConsts.DEFAULT_COLOR;
        }
        final StringBuffer result = new StringBuffer("<span class=\"");
        result.append(status.toLowerCase());
        result.append("\" style=\"color: ");
        result.append(color);
        result.append("\">");
        result.append(line);
        result.append("</span>");
        return result.toString();
    }

    private String addMarkerAndLink(final String line,
            final String effectiveStatus, final String status)
            throws IOException {
        // Add marker
        final String statusCountStr = ((Integer) statusCount
                .get(effectiveStatus)).toString();
        final String marker = effectiveStatus + statusCountStr;

        // Add link
        final StringBuffer shortLink = new StringBuffer(
                " <a target=\"content\" href=\"log_content.html#");
        shortLink.append(marker);
        shortLink.append("\">");
        shortLink.append(line);
        shortLink.append("</a>");

        final StringBuffer link = new StringBuffer("<li>");
        link.append(statusCountStr);
        link.append(shortLink);
        link.append("</li>");

        final BufferedWriter linkWriter = (BufferedWriter) writers
                .get(effectiveStatus);
        linkWriter.write(link.toString());
        linkWriter.newLine(); // Write system dependent end of line.

        // Mark the line
        final StringBuffer markedLine = new StringBuffer("<p><a name=\"");
        markedLine.append(marker);
        markedLine.append("\"></a></p>");
        markedLine.append(line);

        // Handle case where we are entering a new section
        if (status.equals(LogParserConsts.START)) {
            sectionCounter++;
            // This enters a line which will later be replaced by the actual
            // header and count for this header
            LogParserWriter.writeHeaderTemplateToAllLinkFiles(writers, sectionCounter); 

            final StringBuffer brShortLink = new StringBuffer("<br/>");
            brShortLink.append(shortLink);
            headerForSection.add(brShortLink.toString());
        }

        return markedLine.toString();
    }

    private void parseLogBody(final Run<?, ?> build, final BufferedWriter writer, final InputStream log,
                        final Logger logger) throws IOException, InterruptedException {

        // Logging information - start
        final String signature = build.getParent().getName() + "_build_"
                + build.getNumber();
        logger.log(Level.INFO, "LogParserParser: Start parsing : " + signature);
        final Calendar calendarStart = Calendar.getInstance();

        final HashMap<String, String> lineStatusMatches = channel.call(
                new LogParserStatusComputer(log, parsingRulesArray, compiledPatterns, signature));

        // Read log file from start - line by line and apply the statuses as
        // found by the threads.
        final InputStreamReader streamReader = new InputStreamReader(
                build.getLogInputStream(),
                build.getCharset() );
        final BufferedReader reader = new BufferedReader( streamReader );
        String line;
        String status;
        int line_num = 0;
        while ((line = reader.readLine()) != null) {
            status = (String) lineStatusMatches.get(String.valueOf(line_num));
            final String parsedLine = parseLine(line, status);
            // This is for displaying sections in the links part
            writer.write(parsedLine);
            writer.newLine(); // Write system dependent end of line.
            line_num++;
        }
        reader.close();

        // Logging information - end
        final Calendar calendarEnd = Calendar.getInstance();
        final long diffSeconds = (calendarEnd.getTimeInMillis() - calendarStart
                .getTimeInMillis()) / 1000;
        final long diffMinutes = diffSeconds / 60;
        logger.log(Level.INFO, "LogParserParser: Parsing took " + diffMinutes
                + " minutes (" + diffSeconds + ") seconds.");

    }

}
