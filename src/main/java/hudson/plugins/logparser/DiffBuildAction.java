package hudson.plugins.logparser;

import hudson.model.Action;

/**
 * DiffBuildAction is the entry point of the functionality
 * of diff build.
 */
public class DiffBuildAction implements Action {

    @Override
    public String getIconFileName() {
        return "document.gif";
    }

    @Override
    public String getDisplayName() {
        return "Diff Against Other Build";
    }

    @Override
    public String getUrlName() {
        return "diffbuild";
    }
}
