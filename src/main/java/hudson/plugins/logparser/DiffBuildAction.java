package hudson.plugins.logparser;

import java.util.ArrayList;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Run;
import hudson.util.ListBoxModel;
import hudson.util.ListBoxModel.Option;

/**
 * @since FIXME
 * 
 * DiffBuildAction is the entry point of the functionality of diff build.
 */
public class DiffBuildAction implements Action, Describable<DiffBuildAction> {
	
	/**
	 * The current build
	 */
    private final Run<?, ?> build;
    
    /**
     * The descriptor of DiffBuildAction class
     */
    private static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();
    
    /**
     * All build numbers in the current job
     */
    private static ArrayList<Integer> allBuildNum;

    /**
     * The constructor for initializing fields and also get all build numbers in the current job 
     */
    public DiffBuildAction(final Run<?, ?> build) throws Exception {
        this.build = build;

        /**
         * Get all previous builds from current build and put the results in the ArrayList
         */
        allBuildNum = new ArrayList<Integer>();
        allBuildNum.add(build.number);
        Run<?, ?> tmpBuild = build;	
        while (tmpBuild.getPreviousBuild() != null) {
            tmpBuild = tmpBuild.getPreviousBuild();
            allBuildNum.add(tmpBuild.number);
        }
    }
    
    /**
     * Get current build
     */
    public Run<?, ?> getOwner() {
        return this.build;
    }

    /**
     * To invoke Console Line Diff output page
     */
    public ConsoleLineDiffDisplay getConsoleLineDiffDisplay() {
        return new ConsoleLineDiffDisplay(build);
    }

    /** * {@inheritDoc} */ @Override
    public String getIconFileName() {
        return "document.gif";
    }

    /** * {@inheritDoc} */ @Override
    public String getDisplayName() {
        return "Diff Against Other Build";
    }

    /** * {@inheritDoc} */ @Override
    public String getUrlName() {
        return "diffbuild";
    }

    /** * {@inheritDoc} */ @Override
    public Descriptor<DiffBuildAction> getDescriptor() {
        return DESCRIPTOR;
    }

    /**
     * The descriptor class for DiffBuildAction class
     * @author chanon
     *
     */
    @Extension
    public static class DescriptorImpl extends Descriptor<DiffBuildAction> {

    	/**
    	 * Display name for Choose Type of Diff dropdown
    	 */
        private static final String TYPE_DIFF_DISPLAY[] = { "Console Output Line Diff" };

        /**
         * Value for Choose Type of Diff dropdown, and these values have to match with the url or each output page
         */
        private static final String TYPE_DIFF_VALUE[] = { "consoleLineDiffDisplay" };

        /**
         * Fill data in Choose Another Build dropdown
         */
        public ListBoxModel doFillAllBuildItems() {
            ListBoxModel items = new ListBoxModel();
            for (int i = 0; i < allBuildNum.size(); i++) {
                if (i == 0) {
                    items.add(new Option("build" + " " + allBuildNum.get(i), allBuildNum.get(i) + "", true));
                } else {
                    items.add(new Option("build" + " " + allBuildNum.get(i), allBuildNum.get(i) + "", false));
                }
            }
            return items;
        }

        /**
         * Fill data in Choose Type of Diff dropdown
         */
        public ListBoxModel doFillTypeDiffItems() {
            ListBoxModel items = new ListBoxModel();
            for (int i = 0; i < TYPE_DIFF_DISPLAY.length; i++) {
                if (i == 0) {
                    items.add(new Option(TYPE_DIFF_DISPLAY[i], TYPE_DIFF_VALUE[i], true));
                } else {
                    items.add(new Option(TYPE_DIFF_DISPLAY[i], TYPE_DIFF_VALUE[i], false));
                }
            }
            return items;
        }

        /** * {@inheritDoc} */ @Override
        public String getDisplayName() {
            return "Diff Build Action";
        }
    }
}
