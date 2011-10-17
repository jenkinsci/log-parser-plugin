package hudson.plugins.logparser.action;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.plugins.logparser.LogParserAction;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: evilpupu
 * Date: 3.10.2011
 * Time: 0:14
 * To change this template use File | Settings | File Templates.
 */
public class LogParserProjectAction implements Action {

    public final AbstractProject<?,?> project;

    public LogParserProjectAction(AbstractProject<?, ?> project) {
        this.project = project;
    }

    public String getIconFileName() {
        return null;
    }

    public String getDisplayName() {
        return "logparser";
    }

    public String getUrlName() {
        return "logparser";
    }

    public LogParserAction getLastLogParserAction() {
        final AbstractBuild<?,?> tb = project.getLastSuccessfulBuild();

        AbstractBuild<?,?> b=project.getLastBuild();
        while(b!=null) {
            LogParserAction a = b.getAction(LogParserAction.class);
            if(a!=null) return a;
            if(b==tb)
                // if even the last successful build didn't produce the test result,
                // that means we just don't have any tests configured.
                return null;
            b = b.getPreviousBuild();
        }

        return null;
    }

    public void doTrend(StaplerRequest req, StaplerResponse rsp ) throws IOException, ServletException {

        LogParserAction a = this.getLastLogParserAction();
        if (a != null)
            a.doGraph(req, rsp);
        else
            rsp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    public void doTrendMap( StaplerRequest req, StaplerResponse rsp ) throws IOException, ServletException {
        LogParserAction a = this.getLastLogParserAction();
        if(a!=null)
            a.doGraphMap(req,rsp);
        else
            rsp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

}
