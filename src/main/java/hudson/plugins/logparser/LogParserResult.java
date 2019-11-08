package hudson.plugins.logparser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LogParserResult {

    private int totalErrors = 0;
    private int totalWarnings = 0;
    private int totalInfos = 0;
    private int totalDebugs = 0;
    private Map<String, Integer> totalCountsByExtraTag = new HashMap<String, Integer>();

    private String htmlLogFile;
    private String errorLinksFile;
    private String warningLinksFile;
    private String infoLinksFile;
    private String debugLinksFile;
    private Map<String, String> linkedFilesByExtraTag = new HashMap<String, String>();
    private Set<String> extraTags = new HashSet<String>();

    private String parsedLogURL;
    private String htmlLogPath;

    private String failedToParseError;
    private String badParsingRulesError;

    protected Object readResolve() {
        if (extraTags == null) { // avoid NPE when deserializing old results
        	extraTags = new HashSet<>();
        }
        return this;
    }

    public String getBadParsingRulesError() {
        return badParsingRulesError;
    }

    public String getBadParsingRulesErrorDisplay() {
        return badParsingRulesError.replaceAll("\n", "<br/>");
    }

    public void setBadParsingRulesError(final String badParsingRulesError) {
        this.badParsingRulesError = badParsingRulesError;
    }

    public String getFailedToParseError() {
        return failedToParseError;
    }

    public void setFailedToParseError(final String failedToParseError) {
        this.failedToParseError = failedToParseError;
    }

    public int getTotalErrors() {
        return totalErrors;
    }

    public int getTotalWarnings() {
        return totalWarnings;
    }

    public int getTotalInfos() {
        return totalInfos;
    }

    public int getTotalDebugs() {
        return totalDebugs;
    }

    public int getTotalCountsByExtraTag(String tag) {
        return totalCountsByExtraTag.get(tag);
    }

    public String getHtmlLogFile() {
        return htmlLogFile;
    }

    public String getHtmlLogPath() {
        return htmlLogPath;
    }

    public String getErrorLinksFile() {
        return errorLinksFile;
    }

    public String getWarningLinksFile() {
        return warningLinksFile;
    }

    public String getInfoLinksFile() {
        return infoLinksFile;
    }

    public String getDebugLinksFile() {
        return debugLinksFile;
    }

    public String getLinksFileByExtraTag(String tag) {
        return linkedFilesByExtraTag.get(tag);
    }

    public String getParsedLogURL() {
        return parsedLogURL;
    }

    public Reader getReader(final String filePath) throws IOException {
        final File logFile = new File(filePath);
        if (logFile.exists()) {
            return new FileReader(logFile);
        }
        return null;
    }

    public Reader getLogReader() throws IOException {
        return getReader(getHtmlLogFile());
    }

    public Reader getErrorLinksReader() throws IOException {
        return getReader(getErrorLinksFile());
    }

    public Reader getWarningLinksReader() throws IOException {
        return getReader(getWarningLinksFile());
    }

    public Reader getInfoLinksReader() throws IOException {
        return getReader(getInfoLinksFile());
    }

    public Reader getDebugLinkedReader() throws IOException {
        return getReader(getDebugLinksFile());
    }

    public Reader getLinkedReaderByExtraTag(String tag) throws IOException {
        return getReader(getLinksFileByExtraTag(tag));
    }

    public void setHtmlLogFile(final String file) {
        this.htmlLogFile = file;
    }

    public void setHtmlLogPath(final String dir) {
        this.htmlLogPath = dir;
    }

    public void setErrorLinksFile(final String file) {
        this.errorLinksFile = file;
    }

    public void setWarningLinksFile(final String file) {
        this.warningLinksFile = file;
    }

    public void setInfoLinksFile(final String file) {
        this.infoLinksFile = file;
    }

    public void setDebugLinksFile(final String file) {
        this.debugLinksFile = file;
    }

    public void putLinksFileByExtraTag(final String tag, final String file) {
        this.linkedFilesByExtraTag.put(tag, file);
    }

    public void setTotalErrors(final int totalErrors) {
        this.totalErrors = totalErrors;
    }

    public void setTotalWarnings(final int totalWarnings) {
        this.totalWarnings = totalWarnings;
    }

    public void setTotalInfos(final int totalInfos) {
        this.totalInfos = totalInfos;
    }

    public void setTotalDebugs(final int totalDebugs) {
        this.totalDebugs = totalDebugs;
    }

    public void putTotalCountsByExtraTag(final String tag, final int totalCounts) {
        this.totalCountsByExtraTag.put(tag, totalCounts);
    }

    public void setParsedLogURL(final String parsedLogURL) {
        this.parsedLogURL = parsedLogURL;
    }

    public File getHtmlLogFileToRead() {
        return new File(this.htmlLogFile);
    }

    public void setExtraTags(Collection<String> extraTags) {
        this.extraTags.addAll(extraTags);
    }

    public Set<String> getExtraTags() {
        return this.extraTags;
    }

    public String getHtmlContent() {
        final StringBuffer result = new StringBuffer("");
        String line = "";
        try {
            File file = null;
            RandomAccessFile f = null;
            try {
                file = this.getHtmlLogFileToRead();
                f = new RandomAccessFile(file, "r");

                while ((line = f.readLine()) != null) {
                    result.append(line);
                    result.append("<br/>");
                }
            } finally {
                f.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

}
