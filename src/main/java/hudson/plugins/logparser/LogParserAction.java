package hudson.plugins.logparser;

import hudson.Functions;
import hudson.model.Action;
import hudson.model.AbstractBuild;
import hudson.model.Run;
import hudson.util.Area;
import hudson.util.ChartUtil;
import hudson.util.ColorPalette;
import hudson.util.DataSetBuilder;
import hudson.util.ShiftedCategoryAxis;
import hudson.util.StackedAreaRenderer2;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StackedAreaRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.RectangleInsets;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

public class LogParserAction implements Action {

    final private Run<?, ?> build;
    final private LogParserResult result;

    private static String urlName = "parsed_console";

    @Deprecated
    public LogParserAction(final AbstractBuild<?, ?> build, final LogParserResult result) {
        this((Run<?, ?>) build, result);
    }

    public LogParserAction(final Run<?, ?> build, final LogParserResult result) {
        this.build = build;
        this.result = result;
    }

    public String getIconFileName() {
        return "clipboard.gif";
    }

    public String getDisplayName() {
        return "Parsed Console Output";
    }

    public String getUrlName() {
        return urlName;
    }

    public static String getUrlNameStat() {
        return urlName;
    }

    public Run<?, ?> getOwner() {
        return build;
    }

    // Used by the summary.jelly of this class to show some totals from the result
    public LogParserResult getResult() {
        return result;
    }

    public LogParserAction getPreviousAction() {
        Run<?, ?> build = this.getOwner();

        while (true) {
            build = build.getPreviousBuild();

            if (build == null) {
                return null;
            }
            LogParserAction action = build.getAction(LogParserAction.class);
            if (action != null) {
                return action;
            }
        }
    }

    public void doDynamic(final StaplerRequest req, final StaplerResponse rsp)
            throws IOException, ServletException, InterruptedException {

        final String dir = result.getHtmlLogPath();
        final String file = req.getRestOfPath();
        final String fileArray[] = file.split("/");
        final String lastFileInPath = fileArray[fileArray.length - 1];
        final File f = new File(dir + "/" + lastFileInPath);
        rsp.serveFile(req, f.toURI().toURL());

    }

    public void doGraph(StaplerRequest req, StaplerResponse rsp) throws IOException {
        if (ChartUtil.awtProblemCause != null) {
            // not available. send out error message
            rsp.sendRedirect2(req.getContextPath() + "/images/headless.png");
            return;
        }

        if (req.checkIfModified(getOwner().getTimestamp(), rsp)) {
            return;
        }

        ChartUtil.generateGraph(req, rsp, createChart(req, buildDataSet()), calcDefaultSize());
    }

    public void doGraphMap(StaplerRequest req, StaplerResponse rsp) throws IOException {
        if (req.checkIfModified(this.getOwner().getTimestamp(), rsp)) {
            return;
        }
        ChartUtil.generateClickableMap(req, rsp, createChart(req, buildDataSet()), calcDefaultSize());
    }

    private Area calcDefaultSize() {
        Area res = Functions.getScreenResolution();
        if (res != null && res.width <= 800) {
            return new Area(250, 100);
        } else {
            return new Area(500, 200);
        }
    }

    private CategoryDataset buildDataSet() {
        DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dsb = new DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel>();

        for (LogParserAction a = this; a != null; a = a.getPreviousAction()) {
            dsb.add(a.result.getTotalErrors(), "errors", new ChartUtil.NumberOnlyBuildLabel(a.getOwner()));
            dsb.add(a.result.getTotalWarnings(), "warnings", new ChartUtil.NumberOnlyBuildLabel(a.getOwner()));
            dsb.add(a.result.getTotalInfos(), "infos", new ChartUtil.NumberOnlyBuildLabel(a.getOwner()));
        }
        return dsb.build();
    }

    private JFreeChart createChart(StaplerRequest req, CategoryDataset dataset) {

        final String relPath = getRelPath(req);

        final JFreeChart chart = ChartFactory.createStackedAreaChart(
                null, // chart title
                null, // unused
                "count", // range axis label
                dataset, // data
                PlotOrientation.VERTICAL, // orientation
                false, // include legend
                true, // tooltips
                false // urls
                );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

        // set the background color for the chart...

        //final StandardLegend legend = (StandardLegend) chart.getLegend();
        //legend.setAnchor(StandardLegend.SOUTH);

        chart.setBackgroundPaint(Color.white);

        final CategoryPlot plot = chart.getCategoryPlot();

        //plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlinePaint(null);
        plot.setForegroundAlpha(0.8f);
        //plot.setDomainGridlinesVisible(true);
        //plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.black);

        CategoryAxis domainAxis = new ShiftedCategoryAxis(null);
        plot.setDomainAxis(domainAxis);
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
        domainAxis.setLowerMargin(0.0);
        domainAxis.setUpperMargin(0.0);
        domainAxis.setCategoryMargin(0.0);

        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        StackedAreaRenderer ar = new StackedAreaRenderer2() {

            private static final long serialVersionUID = 1L;

            @Override
            public String generateURL(CategoryDataset dataset, int row, int column) {
                ChartUtil.NumberOnlyBuildLabel label = (ChartUtil.NumberOnlyBuildLabel) dataset.getColumnKey(column);
                return relPath + label.build.getNumber() + "/testReport/";
            }

            @Override
            public String generateToolTip(CategoryDataset dataset, int row, int column) {
                switch (row) {
                case 0:
                    return "Errors: " + result.getTotalErrors();
                case 1:
                    return "Warnings: " + result.getTotalWarnings();
                default:
                    return "Infos: " + result.getTotalInfos();
                }
            }
        };
        plot.setRenderer(ar);
        ar.setSeriesPaint(0, ColorPalette.RED);    // error
        ar.setSeriesPaint(1, ColorPalette.BLUE);   // info
        ar.setSeriesPaint(2, ColorPalette.YELLOW); // warning

        // crop extra space around the graph
        plot.setInsets(new RectangleInsets(0, 0, 0, 5.0));

        return chart;
    }

    private String getRelPath(StaplerRequest req) {
        String relPath = req.getParameter("rel");
        if (relPath == null) {
            return "";
        }
        return relPath;
    }

}
