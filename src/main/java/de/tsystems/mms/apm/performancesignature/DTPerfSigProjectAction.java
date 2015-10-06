/*
 * Copyright (c) 2014 T-Systems Multimedia Solutions GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.tsystems.mms.apm.performancesignature.jenkins;

import de.tsystems.mms.apm.performancesignature.dynatrace.model.ChartDashlet;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.DashboardReport;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.Measure;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.TestRun;
import de.tsystems.mms.apm.performancesignature.jenkins.util.DTPerfSigUtils;
import hudson.FilePath;
import hudson.Functions;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.ProminentProjectAction;
import hudson.model.Run;
import hudson.tasks.junit.TestResult;
import hudson.tasks.junit.TestResultAction;
import hudson.tasks.test.TestResultProjectAction;
import hudson.util.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.bind.JavaScriptMethod;

import java.awt.*;
import java.io.*;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rapi on 25.04.2014.
 */

public class DTPerfSigProjectAction implements ProminentProjectAction {
    private final AbstractProject<?, ?> project;

    public DTPerfSigProjectAction(final AbstractProject<?, ?> project) {
        this.project = project;
    }

    public String getIconFileName() {
        return "/plugin/" + Messages.DTPerfSigProjectAction_UrlName() + "/images/icon.png";
    }

    public String getDisplayName() {
        return Messages.DTPerfSigProjectAction_DisplayName();
    }

    public String getUrlName() {
        return Messages.DTPerfSigProjectAction_UrlName();
    }

    public AbstractProject<?, ?> getProject() {
        return this.project;
    }

    public TestResultProjectAction getTestResultProjectAction() {
        return project.getAction(TestResultProjectAction.class);
    }

    public DTPerfSigUtils getDTPerfSigUtils() {
        return new DTPerfSigUtils();
    }

    @SuppressWarnings("unused")
    public void doSummarizerGraph(final StaplerRequest request, final StaplerResponse response) throws IOException {
        if (ChartUtil.awtProblemCause != null) {
            // not available. send out error message
            response.sendRedirect2(request.getContextPath() + "/images/headless.png");
            return;
        }

        final Run<?, ?> latestRun = project.getLastSuccessfulBuild();
        if (latestRun != null && request.checkIfModified(latestRun.getTimestamp(), response))
            return;

        ChartUtil.generateGraph(request, response, createChart(request, buildDataSet(request)), calcDefaultSize());
    }

    private CategoryDataset buildDataSet(final StaplerRequest request) throws UnsupportedEncodingException {
        final String measure = request.getParameter(Messages.DTPerfSigProjectAction_ReqParamMeasure());
        final String chartDashlet = request.getParameter(Messages.DTPerfSigProjectAction_ReqParamChartDashlet());
        final String testCase = request.getParameter(Messages.DTPerfSigProjectAction_ReqParamTestCase());
        final List<DashboardReport> dashboardReports = getDashBoardReports(testCase);

        final DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dsb = new DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel>();

        for (DashboardReport dashboardReport : dashboardReports) {
            double metricValue = 0;
            if (dashboardReport.getChartDashlets() != null) {
                Measure m = dashboardReport.getMeasure(chartDashlet, measure);
                if (m != null) metricValue = m.getMetricValue();
            }
            dsb.add(metricValue, chartDashlet, new ChartUtil.NumberOnlyBuildLabel((Run<?, ?>) dashboardReport.getBuild()));
        }
        return dsb.build();
    }

    private JFreeChart createChart(final StaplerRequest req, final CategoryDataset dataset) throws UnsupportedEncodingException {
        final String measure = req.getParameter(Messages.DTPerfSigProjectAction_ReqParamMeasure());
        final String chartDashlet = req.getParameter(Messages.DTPerfSigProjectAction_ReqParamChartDashlet());
        final String testCase = req.getParameter(Messages.DTPerfSigProjectAction_ReqParamTestCase());

        String unit = "", color = "";
        final Measure m = getMeasure(testCase, chartDashlet, measure);
        if (m != null) {
            unit = m.getUnit();
            color = URLDecoder.decode(m.getColor(), "UTF-8");
        }
        if (StringUtils.isBlank(color)) color = Messages.DTPerfSigProjectAction_DefaultColor();

        final JFreeChart chart = ChartFactory.createBarChart(DTPerfSigUtils.generateTitle(measure, chartDashlet), // title
                "Build", // category axis label
                unit, // value axis label
                dataset, // data
                PlotOrientation.VERTICAL, // orientation
                false, // include legend
                true, // tooltips
                false // urls
        );

        chart.setBackgroundPaint(Color.white);

        final CategoryPlot plot = chart.getCategoryPlot();

        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlinePaint(null);
        plot.setForegroundAlpha(0.8f);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.black);

        final CategoryAxis domainAxis = new ShiftedCategoryAxis(null);
        plot.setDomainAxis(domainAxis);
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
        //domainAxis.setLowerMargin(0.0);
        //domainAxis.setUpperMargin(0.0);
        //domainAxis.setCategoryMargin(0.0);

        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        final BarRenderer renderer = (BarRenderer) chart.getCategoryPlot().getRenderer();
        renderer.setSeriesPaint(0, Color.decode(color));

        return chart;
    }

    @SuppressWarnings("unused")
    public void doTestRunGraph(final StaplerRequest request, final StaplerResponse response) throws IOException {
        if (ChartUtil.awtProblemCause != null) {
            // not available. send out error message
            response.sendRedirect2(request.getContextPath() + "/images/headless.png");
            return;
        }

        final Run<?, ?> latestRun = project.getLastSuccessfulBuild();
        if (latestRun != null && request.checkIfModified(latestRun.getTimestamp(), response))
            return;

        ChartUtil.generateGraph(request, response, createTestRunChart(buildTestRunDataSet()), calcDefaultSize());
    }

    private CategoryDataset buildTestRunDataSet() throws UnsupportedEncodingException {
        final DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dsb = new DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel>();

        for (Run run : project.getBuilds()) {
            DTPerfSigTestDataWrapper testDataWrapper = run.getAction(DTPerfSigTestDataWrapper.class);
            if (testDataWrapper != null && testDataWrapper.getTestRuns() != null) {
                TestRun testRun = TestRun.mergeTestRuns(testDataWrapper.getTestRuns());
                if (testRun == null) continue;
                dsb.add(testRun.getNumFailed(), "failed", new ChartUtil.NumberOnlyBuildLabel(run));
                dsb.add(testRun.getNumDegraded(), "degraded", new ChartUtil.NumberOnlyBuildLabel(run));
                dsb.add(testRun.getNumImproved(), "improved", new ChartUtil.NumberOnlyBuildLabel(run));
                dsb.add(testRun.getNumPassed(), "passed", new ChartUtil.NumberOnlyBuildLabel(run));
                dsb.add(testRun.getNumVolatile(), "volatile", new ChartUtil.NumberOnlyBuildLabel(run));
                dsb.add(testRun.getNumInvalidated(), "invalidated", new ChartUtil.NumberOnlyBuildLabel(run));
            }
        }
        return dsb.build();
    }

    private JFreeChart createTestRunChart(final CategoryDataset dataset) throws UnsupportedEncodingException {
        final JFreeChart chart = ChartFactory.createBarChart("TestRun Results", // title
                "Build", // category axis label
                "num", // value axis label
                dataset, // data
                PlotOrientation.VERTICAL, // orientation
                true, // include legend
                true, // tooltips
                false // urls
        );

        chart.setBackgroundPaint(Color.white);

        final CategoryPlot plot = chart.getCategoryPlot();

        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlinePaint(null);
        plot.setForegroundAlpha(0.8f);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.black);

        final CategoryAxis domainAxis = new ShiftedCategoryAxis(null);
        plot.setDomainAxis(domainAxis);
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
        //domainAxis.setLowerMargin(0.0);
        //domainAxis.setUpperMargin(0.0);
        //domainAxis.setCategoryMargin(0.0);

        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        final StackedBarRenderer br = new StackedBarRenderer();
        plot.setRenderer(br);
        br.setSeriesPaint(0, new Color(0xFF, 0x99, 0x99)); // degraded
        br.setSeriesPaint(1, ColorPalette.RED); // failed
        br.setSeriesPaint(2, new Color(0x00, 0xFF, 0x00)); // improved
        br.setSeriesPaint(3, ColorPalette.GREY); // invalidated
        br.setSeriesPaint(4, ColorPalette.BLUE); // passed
        br.setSeriesPaint(5, ColorPalette.YELLOW); // volatile

        return chart;
    }

    private Area calcDefaultSize() {
        Area res = Functions.getScreenResolution();
        if (res != null && res.width <= 800)
            return new Area(250, 100);
        else
            return new Area(500, 200);
    }

    private Measure getMeasure(final String testCase, final String chartDashlet, final String measure) {
        final Run lastRun = project.getLastSuccessfulBuild();
        if (lastRun != null && lastRun.getAction(DTPerfSigBuildAction.class) != null) {
            for (DashboardReport dr : lastRun.getAction(DTPerfSigBuildAction.class).getDashboardReports())
                if (dr.getName().equals(testCase))
                    for (ChartDashlet cd : dr.getChartDashlets())
                        if (cd.getName().equals(chartDashlet))
                            for (Measure m : cd.getMeasures())
                                if (m.getName().equals(measure))
                                    return m;
        }
        return null;
    }

    public List<DashboardReport> getLastDashboardReports() {
        final Run<?, ?> tb = project.getLastSuccessfulBuild();

        Run<?, ?> b = project.getLastBuild();
        while (b != null) {
            DTPerfSigBuildAction a = b.getAction(DTPerfSigBuildAction.class);
            if (a != null && (!b.isBuilding())) return a.getDashboardReports();
            if (b == tb)
                return null;
            b = b.getPreviousBuild();
        }
        return null;
    }

    public TestRun getTestRun(final int buildNumber) {
        final Run run = project.getBuildByNumber(buildNumber);
        if (run != null) {
            DTPerfSigTestDataWrapper testDataWrapper = run.getAction(DTPerfSigTestDataWrapper.class);
            if (testDataWrapper != null) {
                return TestRun.mergeTestRuns(testDataWrapper.getTestRuns());
            }
        }
        return null;
    }

    public TestResult getTestAction(final int buildNumber) {
        final Run run = project.getBuildByNumber(buildNumber);
        if (run != null) {
            TestResultAction testResultAction = run.getAction(TestResultAction.class);
            if (testResultAction != null) {
                return testResultAction.getResult();
            }
        }
        return null;
    }

    public List<DashboardReport> getDashBoardReports(final String tc) {
        final List<DashboardReport> dashboardReportList = new ArrayList<DashboardReport>();
        if (project == null) {
            return dashboardReportList;
        }
        final List<? extends AbstractBuild> builds = project.getBuilds();
        for (AbstractBuild currentBuild : builds) {
            final DTPerfSigBuildAction performanceBuildAction = currentBuild.getAction(DTPerfSigBuildAction.class);
            if (performanceBuildAction != null) {
                DashboardReport dashboardReport = performanceBuildAction.getBuildActionResultsDisplay().getDashBoardReport(tc);
                if (dashboardReport == null) {
                    dashboardReport = new DashboardReport(tc);
                    dashboardReport.setBuildAction(new DTPerfSigBuildAction(currentBuild, null));
                }
                dashboardReportList.add(dashboardReport);
            }
        }
        return dashboardReportList;
    }

    @SuppressWarnings("unused")
    public void doDownloadFile(final StaplerRequest request, final StaplerResponse response) throws IOException {
        final Pattern pattern = Pattern.compile("-\\d+");
        final Matcher matcher = pattern.matcher(request.getParameter("f"));
        if (matcher.find()) {
            final int id = Integer.parseInt(matcher.group().substring(1));
            DTPerfSigUtils.downloadFile(request, response, project.getBuildByNumber(id));
        }
    }

    private FilePath getJsonConfigFilePath() {
        final FilePath configPath = new FilePath(project.getConfigFile().getFile());
        return configPath.getParent();
    }

    @SuppressWarnings("unused")
    @JavaScriptMethod
    public String getDashboardConfiguration(final String testCase) throws IOException {
        if (StringUtils.isBlank(testCase)) return "";
        File input = new File(getJsonConfigFilePath() + File.separator + testCase + "-config.json");
        if (!input.exists()) {
            input = new File(DTPerfSigUtils.getInstanceOrDie().getRootDir() + "/plugins/" + Messages.DTPerfSigProjectAction_UrlName() + "/defaultConfig.json");
        }
        FileInputStream fileInputStream = new FileInputStream(input);
        try {
            return IOUtils.toString(fileInputStream, "UTF-8");
        } finally {
            IOUtils.closeQuietly(fileInputStream);
        }
    }

    @SuppressWarnings("unused")
    @JavaScriptMethod
    public void setDashboardConfiguration(final String testCase, final String data) throws IOException {
        if (StringUtils.isBlank(testCase)) return;
        File output = new File(getJsonConfigFilePath() + File.separator + testCase + "-config" + ".json");
        String json = StringEscapeUtils.unescapeJson(data);
        json = json.substring(1, json.length() - 1);
        PrintWriter out = new PrintWriter(output, "UTF-8");
        out.print(json);
        IOUtils.closeQuietly(out);
    }

    @SuppressWarnings("unused")
    @JavaScriptMethod
    public List<String> getAvailableMeasures(final String dashlet) throws IOException {
        if (StringUtils.isBlank(dashlet)) return null;
        final List<String> availableMeasures = new ArrayList<String>();
        for (DashboardReport dashboardReport : getLastDashboardReports()) {
            for (ChartDashlet chartDashlet : dashboardReport.getChartDashlets()) {
                if (chartDashlet.getName().equals(dashlet)) {
                    for (Measure measure : chartDashlet.getMeasures())
                        availableMeasures.add(measure.getName());
                    return availableMeasures;
                }
            }
        }
        return availableMeasures;
    }

    @SuppressWarnings("unused")
    public List<ChartDashlet> getFilteredChartDashlets(final DashboardReport dashboardReport) throws IOException {
        final List<ChartDashlet> chartDashlets = new ArrayList<ChartDashlet>();
        final String json = getDashboardConfiguration(dashboardReport.getName());
        if (StringUtils.isBlank(json) || dashboardReport.getChartDashlets() == null) return chartDashlets;
        final JSONArray jsonArray = JSONArray.fromObject(json);

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            String html = obj.getString("html");
            String measure = URLDecoder.decode(DTPerfSigUtils.extractXMLAttribute(html, "measure"), "UTF-8");
            String chartDashlet = URLDecoder.decode(DTPerfSigUtils.extractXMLAttribute(html, "chartdashlet"), "UTF-8");

            for (ChartDashlet dashlet : dashboardReport.getChartDashlets()) {
                if (dashlet.getName().equals(chartDashlet)) {
                    for (Measure m : dashlet.getMeasures()) {
                        if (m.getName().equals(measure)) {
                            ChartDashlet d = new ChartDashlet(dashlet.getName());
                            d.addMeasure(m);
                            chartDashlets.add(d);
                            break;
                        }
                    }
                }
            }
        }
        return chartDashlets;
    }
}
