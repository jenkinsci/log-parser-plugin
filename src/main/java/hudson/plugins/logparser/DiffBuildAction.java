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
 * DiffBuildAction is the entry point of the functionality of diff build.
 */
public class DiffBuildAction implements Action, Describable<DiffBuildAction> {

    final private Run<?, ?> build;
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();
    public static ArrayList<Integer> prevBuildNum;

    public DiffBuildAction(final Run<?, ?> build) throws Exception {
        this.build = build;

        // get all previous builds from current build and put the results in the
        // ArrayList
        prevBuildNum = new ArrayList<Integer>();
        prevBuildNum.add(build.number);
        Run<?, ?> tmpBuild = build;
        while (tmpBuild.getPreviousBuild() != null) {
            tmpBuild = tmpBuild.getPreviousBuild();
            prevBuildNum.add(tmpBuild.number);
        }
    }

    public Run<?, ?> getOwner() {
        return this.build;
    }

    // to invoke Console Line Diff output page
    public ConsoleLineDiffDisplay getConsoleOutputLineDiff() {
        return new ConsoleLineDiffDisplay(build);
    }

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

    @Override
    public Descriptor<DiffBuildAction> getDescriptor() {
        // TODO Auto-generated method stub
        return DESCRIPTOR;
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<DiffBuildAction> {

        // display name for Choose Type of Diff dropdown
        private static final String typeDiffDisplay[] = { "Console Output Line Diff" };

        // value for Choose Type of Diff dropdown, and these values have to
        // match with the url or each output page
        private static final String typeDiffValue[] = { "consoleOutputLineDiff" };

        // fill data in Choose Another Build dropdown
        public ListBoxModel doFillPrevBuildItems() {
            ListBoxModel items = new ListBoxModel();
            for (int i = 0; i < prevBuildNum.size(); i++) {
                if (i == 0) {
                    items.add(new Option("build" + " " + prevBuildNum.get(i), prevBuildNum.get(i) + "", true));
                } else {
                    items.add(new Option("build" + " " + prevBuildNum.get(i), prevBuildNum.get(i) + "", false));
                }
            }
            return items;
        }

        // fill data in Choose Type of Diff dropdown
        public ListBoxModel doFillTypeDiffItems() {
            ListBoxModel items = new ListBoxModel();
            for (int i = 0; i < typeDiffDisplay.length; i++) {
                if (i == 0) {
                    items.add(new Option(typeDiffDisplay[i], typeDiffValue[i], true));
                } else {
                    items.add(new Option(typeDiffDisplay[i], typeDiffValue[i], false));
                }
            }
            return items;
        }

        @Override
        public String getDisplayName() {
            return "Diff Build Action";
        }
    }
}
