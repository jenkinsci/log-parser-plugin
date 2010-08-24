package hudson.plugins.logparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogParserReader {

	  final private BufferedReader reader;
	  int logPartNum=0;
	  boolean endOfFile = false;
	  

	public LogParserReader(final BufferedReader reader) {
		  this.reader = reader;
	  }
	  
	  public synchronized LogParserLogPart readLogPart(final int threadNum) throws IOException {
		  	final Logger logger = Logger.getLogger(this.getClass().getName());
			logger.log(Level.INFO,"Start reading log part "+logPartNum+" in thread #"+threadNum);
			final int numLines = LogParserUtils.getLinesPerThread();
			String[] lines = new String[numLines];
			final LogParserLogPart result = new LogParserLogPart();
			
			int counter = 0;
			String line;
	        while (counter<numLines && ((line=reader.readLine()) != null)) {        	
	        	lines[counter++]=line;
	        }
			logger.log(Level.INFO,"Done reading log part "+logPartNum);
			result.setLines(lines);
			result.setLogPartNum(logPartNum);
			
			if (result.isEmpty()) {
				this.endOfFile = true;
			}
			logPartNum++; // increment counter for next call of method by another thread
			
			return result;
	  }
	  
	  public boolean isEndOfFile() {
			return endOfFile;
		}

	  public void setEndOfFile(final boolean endOfFile) {
			this.endOfFile = endOfFile;
	  }

		
}
