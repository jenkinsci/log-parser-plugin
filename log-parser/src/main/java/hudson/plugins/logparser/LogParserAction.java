package hudson.plugins.logparser;

import java.io.IOException;
import java.io.File;
import javax.servlet.ServletException;
import org.kohsuke.stapler.StaplerRequest; 
import org.kohsuke.stapler.StaplerResponse; 
import hudson.model.Action;
import hudson.model.AbstractBuild;

public class LogParserAction implements Action {

	final private AbstractBuild<?,?> build;
	final private LogParserResult result;
	
	private static String urlName = "parsed_console";
	
	public LogParserAction(final AbstractBuild<?,?> build, final LogParserResult result) {
        this.build = build;
        this.result = result;
	
	}
    public String getIconFileName(){
    	return "clipboard.gif";
    }
    public String getDisplayName(){
    	return "Parsed Console Output";
    }
    
    public String getUrlName(){
    	return urlName;
    }
    
    public static String getUrlNameStat(){
    	return urlName;
    }

    public AbstractBuild<?, ?> getOwner() {
		return build;
	}

    // Used by the summary.jelly of this class to show some totals from the result 
    public LogParserResult getResult() {
		return result;
	}

    public void doDynamic(final StaplerRequest req, final StaplerResponse rsp) throws IOException, ServletException, InterruptedException { 
        final String dir = result.getHtmlLogPath();
        final String file = req.getRestOfPath();
        final String fileArray[] = file.split("/");
        final String lastFileInPath = fileArray[fileArray.length-1];
        final File f = new File (dir+"/"+lastFileInPath);
        rsp.serveFile(req, f.toURI().toURL());
        
} 

    
}
