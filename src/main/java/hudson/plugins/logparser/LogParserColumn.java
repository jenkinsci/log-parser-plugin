package hudson.plugins.logparser;

import hudson.Extension;
import hudson.model.Job;
import hudson.model.Run;
import hudson.views.ListViewColumn;
import hudson.views.ListViewColumnDescriptor;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

public class LogParserColumn extends ListViewColumn {
    public int[] getResult(Job job) {
        if (job == null) {
            return null;
        }
	Run build = job.getLastCompletedBuild();
        if (build == null) {
            return null;
        }
        LogParserAction action = build.getAction(LogParserAction.class);
	if (action == null) {
            return null;
	}
        LogParserResult result = action.getResult();
	if (result == null) {
            return null;
	}

	return new int[]{result.getTotalErrors(), result.getTotalWarnings(), result.getTotalInfos(), result.getTotalDebugs()};
    }

    public String getUrl(Job job) {
        if (job == null) {
            return null;
        }
        Run build = job.getLastCompletedBuild();
        if (build == null) {
            return null;
        }
        return build.getUrl() + LogParserAction.getUrlNameStat();
    }

    @Extension
    public static class LogParserColumnDescriptor extends ListViewColumnDescriptor {
        @Override
        public ListViewColumn newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            return new LogParserColumn();
        }

        @Override
        public String getDisplayName() {
            return Messages.LogParserColumn_Header();
        }

        @Override
        public boolean shownByDefault() {
            return false;
        }
    }
}
