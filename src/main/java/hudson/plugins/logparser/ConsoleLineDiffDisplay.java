package hudson.plugins.logparser;

import java.io.IOException;

import org.kohsuke.stapler.Stapler;

import hudson.model.Action;
import hudson.model.Run;

public class ConsoleLineDiffDisplay implements Action{
	
	final private Run<?, ?> currentBuild;
	private String prevBuild;
	private String html;
	
	public ConsoleLineDiffDisplay(Run<?, ?> build){
		this.currentBuild = build;
		this.prevBuild = Stapler.getCurrentRequest().getParameter("prevBuild");
		int currBuildNum = build.getNumber();
		int prevBuildNum = Integer.parseInt(prevBuild);
		
		Run<?, ?> previousBuild = currentBuild.getParent().getBuildByNumber(prevBuildNum);
		
		String currLogFileLocation = currentBuild.getLogFile().getAbsolutePath();
		String prevLogFileLocation = previousBuild.getLogFile().getAbsolutePath();
		
		LogParserLineDiff d = new LogParserLineDiff();
		try {
			d.lineDiff(prevLogFileLocation, currLogFileLocation);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		DiffToHtmlGenerator d2h = new DiffToHtmlGenerator(d.getDeltas(), d.getPrevConsoleOutput(), currBuildNum, prevBuildNum);
		
		html = d2h.generateHtmlString();
	}
	
	public Run<?, ?> getOwner() {
        return this.currentBuild;
	}
	
	public String getPrevBuild(){
		return this.prevBuild;
	}
	
	public String getHtml(){
		return this.html;
	}

	@Override
	public String getDisplayName() {
		return "Console Line Diff Result Page";
	}

	@Override
	public String getIconFileName() {
		return "";
	}

	@Override
	public String getUrlName() {
		return "consoleLineDiffDisplay";
	}

}
