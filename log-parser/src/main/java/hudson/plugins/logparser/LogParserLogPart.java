package hudson.plugins.logparser;

public class LogParserLogPart {
	
	private String[] lines;
	private int logPartNum;
	

	// Intentional - first object is created, then fields are set later on.
	public LogParserLogPart() {
	}
	
	public String[] getLines() {
		return lines;
	}
	
	public void setLines(final String[] lines) {
		this.lines = lines;
	}
	
	public int getLogPartNum() {
		return logPartNum;
	}
	
	public void setLogPartNum(final int logPartNum) {
		this.logPartNum = logPartNum;
	}
	
	public boolean isEmpty() {
		return (lines[0] == null);  
	}
	
	
}
