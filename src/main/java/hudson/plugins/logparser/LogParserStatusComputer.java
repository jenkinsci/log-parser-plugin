package hudson.plugins.logparser;

import hudson.FilePath;
import hudson.remoting.Callable;
import hudson.remoting.VirtualChannel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;


public class LogParserStatusComputer implements Serializable {

//	private VirtualChannel channel;
	final private String[] parsingRulesArray;
    final private Pattern[] compiledPatterns;
    final private HashMap<String,String> computedStatusMatches;

	public LogParserStatusComputer(final VirtualChannel channel, final FilePath filePath,final String[] parsingRulesArray, final Pattern[] compiledPatterns, final int linesInLog, final String signature) throws IOException, InterruptedException {
    	this.parsingRulesArray = parsingRulesArray;
    	this.compiledPatterns = compiledPatterns;
    	this.computedStatusMatches = computeStatusMatches(filePath,linesInLog,channel,signature);
    }
    
	private HashMap<String,String> computeStatusMatches(final FilePath filePath,final int linesInLog, final VirtualChannel channel,final String signature) throws IOException, InterruptedException {
		HashMap<String,String> result = null;
		
		try {
				result = channel.call(new Callable<HashMap<String,String>,RuntimeException>(){
			    public HashMap<String,String> call() {
			    	HashMap<String,String> result = null;
			    	try {
			    		result = computeStatusMatches(filePath,linesInLog,signature);
			    	}catch (Exception e) {
			    		e.printStackTrace();
			    	}
			    	return result;
			    }
			});
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	private HashMap<String,String> computeStatusMatches(final FilePath filePath, final int linesInLog, final String signature) throws FileNotFoundException, IOException, InterruptedException {
    	// SLAVE PART START

		final Logger logger = Logger.getLogger(this.getClass().getName());
		
		// Copy remote file to temp local location 
		String tempDir = System.getProperty("java.io.tmpdir");
		if (!tempDir.endsWith(File.separator)) {
			final StringBuffer tempDirBuffer = new StringBuffer(tempDir);
			tempDirBuffer.append(File.separator) ;
			tempDir = tempDirBuffer.toString();
		}
		
		final String tempFileLocation = tempDir+"log-parser_"+signature; 
	    final File tempFile = new File(tempFileLocation);
	    final FilePath tempFilePath = new FilePath(tempFile);
	    filePath.copyTo(tempFilePath);

		logger.log(Level.INFO,"Local temp file:"+tempFileLocation);

        final BufferedReader reader = new BufferedReader(new InputStreamReader(tempFilePath.read()));
    	int counter = 0;
		int threadCounter = 0;
  	  	
  	  	final ArrayList<LogParserThread> runners = new ArrayList<LogParserThread>();
  	  	final LogParserReader logParserReader = new LogParserReader(reader);
  	  	
//  	  	ExecutorService execSvc = Executors.newFixedThreadPool( LogParserUtils.getNumThreads() );
  	  	final ExecutorService execSvc = Executors.newCachedThreadPool();
  	  	final int threadsNeeded = linesInLog / LogParserUtils.getLinesPerThread() + 1;
  	  	
  	  	// Read and parse the log parts
  	  	// Keep the threads and results in an array for future reference when writing
  		for (int i=0;i<threadsNeeded;i++) {
	    	//logger.log(Level.INFO,"LogParserParser: Open thread #"+threadCounter);
	    	final LogParserThread logParserThread = new LogParserThread(logParserReader,parsingRulesArray,compiledPatterns,threadCounter);
	    	//logParserThread.start();
	    	runners.add(logParserThread);
	    	execSvc.execute(logParserThread);
	    	threadCounter++;
  		}

  	  	// Wait for all threads to finish before sequentially writing the outcome
  	    execSvc.shutdown();
  	    execSvc.awaitTermination(3600, TimeUnit.SECONDS);
  	    
  	    // Sort the threads in the order of the log parts they read 
  	    // It could be that thread #1 read log part #2 and thread #2 read log part #1

  	    final int runnersSize = runners.size();
  	    LogParserThread[] sortedRunners = new LogParserThread[runnersSize];
  	    for (LogParserThread logParserThread : runners) {
  	    	final LogParserLogPart logPart = logParserThread.getLogPart();
  	    	if (logPart != null) {
	  	    	final int logPartNum = logPart.getLogPartNum();
	  	    	sortedRunners[logPartNum]=logParserThread;
  	    	}
  	    }
  	    
		final HashMap<String,String> result = new HashMap<String,String>();
 	    HashMap<String,String> moreLineStatusMatches;
		for (int i = 0; i < runnersSize; i++) {
			final LogParserThread logParserThread = sortedRunners[i];
			if (logParserThread != null) {
				moreLineStatusMatches = getLineStatusMatches(logParserThread.getLineStatuses(),i);
				result.putAll(moreLineStatusMatches);
			    final int newLines = logParserThread.getNumOfLines();
			    counter += newLines;
			}
		}

        reader.close();  // Close to unlock.

		// Delete temp file
		tempFilePath.delete();

        return result;
        // SLAVE PART END
	}

	private HashMap<String,String> getLineStatusMatches(final String[] statuses, final int logPart) {
		final HashMap<String,String> result = new HashMap<String,String>();
		String status;
		int line_num;
		final int linesPerThread = LogParserUtils.getLinesPerThread();
		for (int i=0;i<statuses.length;i++) {
			status = statuses[i];
			line_num = i + logPart * linesPerThread;
			result.put(String.valueOf(line_num), status);
		}
		return result;
	}

    public HashMap<String, String> getComputedStatusMatches() {
		return computedStatusMatches;
	}
	
}
