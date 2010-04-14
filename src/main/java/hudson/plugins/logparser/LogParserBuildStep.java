package hudson.plugins.logparser;


import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Action;
import hudson.tasks.BuildStep;
import hudson.tasks.BuildStepMonitor;
import java.io.IOException;

public class LogParserBuildStep implements BuildStep  {

    public boolean prebuild(final AbstractBuild<?,?> build, final BuildListener listener) {
    	return true;
    }

	public boolean perform(final AbstractBuild<?, ?> build, final Launcher launcher, final BuildListener listener) throws InterruptedException, IOException {
        return true;
    }
 
    private static final long serialVersionUID = 1L;

	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE ;
	}


	public Action getProjectAction(final AbstractProject<?, ?> project) {
		return null;
	}
}

