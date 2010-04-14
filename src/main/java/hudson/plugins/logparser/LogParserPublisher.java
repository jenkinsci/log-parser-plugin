package hudson.plugins.logparser;


import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;



public class LogParserPublisher extends Recorder implements Serializable {
    
	public final boolean failBuildOnError;
    public final String parsingRulesPath;
  
    @DataBoundConstructor
    public LogParserPublisher(final boolean failBuildOnError, final String parsingRulesPath) {
    	this.failBuildOnError = failBuildOnError;
    	this.parsingRulesPath =  parsingRulesPath;
   }

    public boolean prebuild(final AbstractBuild<?,?> build, final BuildListener listener) {
    	return true;
    }
    
    public boolean perform(final AbstractBuild<?, ?> build, final Launcher launcher, final BuildListener listener) throws InterruptedException, IOException {
    	try {
    		final PrintStream logger = listener.getLogger();
    		final File logParsingRulesFile = new File(this.parsingRulesPath);
    		if (logParsingRulesFile.isFile()) { 
    		
	        	// Create a parser with the parsing rules as configured : colors, regular expressions, etc.
	        	final LogParserParser parser = new LogParserParser(this.parsingRulesPath);
	        	// Parse the build's log according to these rules and get the result 
	    		final LogParserResult result = parser.parseLog(build);
	        
	    		// Add an action created with the above results
	    		final LogParserAction action = new LogParserAction(build,result);
	    		build.getActions().add(action);
	    		
	    		// Mark build as failed if necessary
	    		if (this.failBuildOnError && result.getTotalErrors() > 0) {
	    			build.setResult(Result.FAILURE);
	    		}
    		} else {
    			// Parsing rules file cannot be found
    			logger.println(LogParserConsts.CANNOT_PARSE+": Can't read parsing rules file:"+this.parsingRulesPath);
    		}

    		
    	} catch (Exception e) {
    		// failure to parse should not fail the build
    		e.printStackTrace();
    	}
        
        return true;
    }

    /**
     * Indicates an orderly abortion of the processing.
     */
    private static final class AbortException extends RuntimeException {
    }



    public BuildStepDescriptor<Publisher> getDescriptor() {
        return DescriptorImpl.DESCRIPTOR;
    }

    
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();
        private volatile ParserRuleFile[] parsingRulesGlobal = new ParserRuleFile[0];

        private DescriptorImpl() {
            super(LogParserPublisher.class);
			load();
      }
        
        public String getDisplayName() {
            return "Console output (build log) parsing";
        }

        public String getHelpFile() {
            return "/plugin/log-parser/help.html";
        }

        public boolean isApplicable(final Class<? extends AbstractProject> jobType) {
            return true;
        }
        
        public  ParserRuleFile[] getParsingRulesGlobal() {
        	return parsingRulesGlobal;
        }

		@Override
		public boolean configure(final StaplerRequest req, final JSONObject json) throws FormException {
			parsingRulesGlobal = req.bindParametersToList(ParserRuleFile.class, "log-parser.")
				.toArray(new ParserRuleFile[0]);
			save();			
	        return true;
		}

    }

    private static final long serialVersionUID = 1L;

	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE ;
	}
	
	/*
	 * This is read by the config.jelly : ${instance.parserRuleChoices}
	 * and displays the available choices of parsing rules which were configured in the global configurations
	 * 
	 */
	public ParserRuleFile[] getParserRuleChoices() {
		// Get the descriptor which holds the global configurations and extract the available parsing rules from there 
		return ((DescriptorImpl)this.getDescriptor()).getParsingRulesGlobal();
	}
	
}

