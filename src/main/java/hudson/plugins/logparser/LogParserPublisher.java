package hudson.plugins.logparser;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.*;
import hudson.plugins.logparser.action.LogParserProjectAction;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;

import org.apache.log4j.spi.LoggerFactory;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.Nonnull;

public class LogParserPublisher extends Recorder implements SimpleBuildStep, Serializable {
    private static final long serialVersionUID = 1L;
    public boolean unstableOnWarning;
    public boolean failBuildOnError;
    public boolean showGraphs;
    public String parsingRulesPath;
    public boolean useProjectRule;
    public String projectRulePath;

    /**
     * Create new LogParserPublisher.
     * 
     * @param unstableOnWarning
     *            mark build unstable if warnings found.
     * @param failBuildOnError
     *            mark build failed if errors found.
     * @param showGraphs
     *            show graphs on job page.
     * @param parsingRulesPath
     *            path to the global parsing rules.
     * @param useProjectRule
     *            true if we use a project specific rule.
     * @param projectRulePath
     *            path to project specific rules relative to workspace root.
     */
    private LogParserPublisher(final boolean unstableOnWarning,
            final boolean failBuildOnError, final boolean showGraphs,
            final String parsingRulesPath, final boolean useProjectRule,
            final String projectRulePath) {

        this.unstableOnWarning = unstableOnWarning;
        this.failBuildOnError = failBuildOnError;
        this.showGraphs = showGraphs;
        this.parsingRulesPath = parsingRulesPath;
        this.useProjectRule = useProjectRule;
        this.projectRulePath = projectRulePath;
    }

    /**
     * Constructor used from methods like {@link StaplerRequest#bindJSON(Class, JSONObject)} and
     * {@link StaplerRequest#bindParameters(Class, String)}.
     */
    @DataBoundConstructor
    public LogParserPublisher() {
        super();
    }

    @Override
    public boolean prebuild(final AbstractBuild<?, ?> build, final BuildListener listener) {
        return true;
    }

    @Deprecated
    public boolean perform(final AbstractBuild<?, ?> build, final Launcher launcher, final BuildListener listener)
            throws InterruptedException, IOException {

        if (build.getResult() == Result.SUCCESS) {
            this.perform(build, build.getWorkspace(), launcher, listener);
        } else {
            listener.error("Skipping publisher since build result is " + build.getResult());
        }
        return true;
    }

    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath workspace, @Nonnull Launcher launcher, @Nonnull
            TaskListener listener) throws InterruptedException, IOException {

        Logger logger = Logger.getLogger(getClass().getName());
        LogParserResult result = new LogParserResult();
        try {
            // Create a parser with the parsing rules as configured : colors, regular expressions, etc.
            boolean preformattedHtml = !((DescriptorImpl) getDescriptor()).getLegacyFormatting();
            final FilePath parsingRulesFile;
            if (useProjectRule) {
                parsingRulesFile = new FilePath(workspace, projectRulePath);
            } else {
                parsingRulesFile = new FilePath(new File(parsingRulesPath));
            }
            final LogParserParser parser = new LogParserParser(parsingRulesFile, preformattedHtml, launcher.getChannel());
            // Parse the build's log according to these rules and get the result
            result = parser.parseLog(run);

            // Mark build as failed/unstable if necessary
            if (this.failBuildOnError && result.getTotalErrors() > 0) {
                run.setResult(Result.FAILURE);
            } else if (this.unstableOnWarning && result.getTotalWarnings() > 0) {
                run.setResult(Result.UNSTABLE);
            }
        } catch (IOException e) {
            // Failure to parse should not fail the build - but should be handled as a serious error.
            // This should catch all process problems during parsing, including parser file not found..
            logger.log(Level.SEVERE, LogParserConsts.CANNOT_PARSE + run, e);
            result.setFailedToParseError(e.toString());
        } catch (NullPointerException e) {
            // in case the rules path is null
            logger.log(Level.SEVERE, LogParserConsts.CANNOT_PARSE + run, e);
            result.setFailedToParseError(e.toString());
            run.setResult(Result.ABORTED);
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, LogParserConsts.CANNOT_PARSE + run, e);
            result.setFailedToParseError(e.toString());
            run.setResult(Result.ABORTED);
        }

        // Add an action created with the above results
        LogParserAction action = new LogParserAction(run, result);
        run.addAction(action);
    }

    @Override
    public BuildStepDescriptor<Publisher> getDescriptor() {
        return DescriptorImpl.DESCRIPTOR;
    }

    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();
        private volatile ParserRuleFile[] parsingRulesGlobal = new ParserRuleFile[0];
        private boolean useLegacyFormatting = false;

        private DescriptorImpl() {
            super(LogParserPublisher.class);
            load();
        }

        @Override
        public String getDisplayName() {
            return "Console output (build log) parsing";
        }

        @Override
        public String getHelpFile() {
            return "/plugin/log-parser/help.html";
        }

        @Override
        public boolean isApplicable(final Class<? extends AbstractProject> jobType) {
            return true;
        }

        public ParserRuleFile[] getParsingRulesGlobal() {
            return parsingRulesGlobal;
        }

        public boolean getLegacyFormatting() {
            return useLegacyFormatting;
        }

        @Override
        public boolean configure(final StaplerRequest req, final JSONObject json) throws FormException {
            parsingRulesGlobal = req.bindJSONToList(ParserRuleFile.class, "log-parser.").toArray(new ParserRuleFile[0]);
            useLegacyFormatting = json.getJSONObject("log-parser").getBoolean("useLegacyFormatting");
            save();
            return true;
        }

        /**
         * Cannot use simple DataBoundConstructor due to radioBlock usage where
         * a JSON object is returned holding the selected value of the block.
         * 
         * {@inheritDoc}
         */
        @Override
        public LogParserPublisher newInstance(StaplerRequest req, JSONObject json) throws FormException {

            String configuredParsingRulesPath = null;
            String configuredProjectRulePath = null;
            boolean configuredUseProjectRule = false;
            final JSONObject useProjectRuleJSON = json.getJSONObject("useProjectRule");

            if (useProjectRuleJSON != null) {
                configuredUseProjectRule = useProjectRuleJSON.getBoolean("value");

                if (!configuredUseProjectRule && useProjectRuleJSON.containsKey("parsingRulesPath")) {
                    configuredParsingRulesPath = useProjectRuleJSON.getString("parsingRulesPath");
                } else if (configuredUseProjectRule && useProjectRuleJSON.containsKey("projectRulePath")) {
                    configuredProjectRulePath = useProjectRuleJSON.getString("projectRulePath");
                }
            }
            return new LogParserPublisher(json.getBoolean("unstableOnWarning"),
                    json.getBoolean("failBuildOnError"),
                    json.getBoolean("showGraphs"),
                    configuredParsingRulesPath,
                    configuredUseProjectRule,
                    configuredProjectRulePath);
        }
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    /*
     * This is read by the config.jelly : ${instance.parserRuleChoices} and
     * displays the available choices of parsing rules which were configured in
     * the global configurations
     */
    public ParserRuleFile[] getParserRuleChoices() {
        // Get the descriptor which holds the global configurations and extract
        // the available parsing rules from there
        return ((DescriptorImpl) this.getDescriptor()).getParsingRulesGlobal();
    }

    @Override
    public Action getProjectAction(AbstractProject<?, ?> project) {

        if (showGraphs) {
            return new LogParserProjectAction(project);
        } else {
            return null;
        }
    }

}
