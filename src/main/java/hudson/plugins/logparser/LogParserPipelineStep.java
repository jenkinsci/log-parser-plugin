package hudson.plugins.logparser;

import com.google.inject.Inject;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

/**
 * Created by acearl on 12/15/2016.
 */
public class LogParserPipelineStep extends AbstractStepImpl {

    private final boolean useProjectRule;
    private final String projectRulePath;
    private final String parsingRulesPath;
    private boolean unstableOnWarning;
    private boolean failBuildOnError;
    private boolean showGraphs;

    @DataBoundConstructor
    public LogParserPipelineStep(boolean useProjectRule, String projectRulePath, String parsingRulesPath) {
        this.useProjectRule = useProjectRule;
        this.projectRulePath = projectRulePath;
        this.parsingRulesPath = parsingRulesPath;
    }

    @DataBoundSetter
    public void setUnstableOnWarning(boolean unstableOnWarning) {
        this.unstableOnWarning = unstableOnWarning;
    }

    @DataBoundSetter
    public void setFailBuildOnError(boolean failBuildOnError) {
        this.failBuildOnError = failBuildOnError;
    }

    @DataBoundSetter
    public void setShowGraphs(boolean showGraphs) {
        this.showGraphs = showGraphs;
    }

    public static class LogParserStepExecution extends AbstractSynchronousStepExecution<LogParserResult> {
        private static final long serialVersionUID = 1L;

        @Inject
        private transient LogParserPipelineStep step;

        @StepContextParameter
        private transient Run<?,?> run;

        @StepContextParameter
        private transient FilePath workspace;

        @StepContextParameter
        private transient TaskListener listener;

        @StepContextParameter
        private transient Launcher launcher;

        @Override
        protected LogParserResult run() throws Exception {
            LogParserPublisher publisher = new LogParserPublisher(step.useProjectRule, step.projectRulePath, step.parsingRulesPath);
            publisher.setFailBuildOnError(step.failBuildOnError);
            publisher.setUnstableOnWarning(step.unstableOnWarning);
            publisher.setShowGraphs(step.showGraphs);

            publisher.perform(run, workspace, launcher, listener);
            LogParserAction action = run.getAction(LogParserAction.class);
            if(action != null) {
                return action.getResult();
            }
            return null;
        }
    }

    @Extension(optional=true)
    public static final class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
            super(LogParserStepExecution.class);
        }

        @Override
        public String getFunctionName() {
            return "logparser";
        }

        @Override
        public String getDisplayName() {
            return "Log Parser";
        }
    }

}
