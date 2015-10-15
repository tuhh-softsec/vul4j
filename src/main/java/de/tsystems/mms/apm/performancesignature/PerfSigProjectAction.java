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

package de.tsystems.mms.apm.performancesignature;

import de.tsystems.mms.apm.performancesignature.dynatrace.model.ChartDashlet;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.DashboardReport;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.Measure;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.TestRun;
import de.tsystems.mms.apm.performancesignature.util.PerfSigUtils;
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
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rapi on 25.04.2014.
 */

public class PerfSigProjectAction implements ProminentProjectAction {
    private final AbstractProject<?, ?> project;

    public PerfSigProjectAction(final AbstractProject<?, ?> project) {
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

    public PerfSigUtils getDTPerfSigUtils() {
        return new PerfSigUtils();
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

        final String id = request.getParameter("id");

        final JSONArray jsonArray = JSONArray.fromObject(getDashboardConfiguration());
        JSONObject jsonObject = null;

        if (StringUtils.isBlank(request.getParameter("customName")) && StringUtils.isBlank(request.getParameter("customBuildCount"))) {
            for (int i = 0; i < jsonArray.size(); i++) {
                final JSONObject obj = jsonArray.getJSONObject(i);
                if (obj.getString("id").equals(id)) {
                    jsonObject = obj;
                    break;
                }
            }
        } else {
            for (DashboardReport dashboardReport : getLastDashboardReports())
                for (ChartDashlet chartDashlet : dashboardReport.getChartDashlets())
                    for (Measure measure : chartDashlet.getMeasures())
                        if (id.equals(DigestUtils.md5Hex(dashboardReport.getName() + chartDashlet.getName() + measure.getName()))) {
                            jsonObject = new JSONObject();
                            jsonObject.put("id", id);
                            jsonObject.put("dashboard", request.getParameter("dashboard"));
                            jsonObject.put("chartDashlet", chartDashlet.getName());
                            jsonObject.put("measure", measure.getName());
                            jsonObject.put("customName", request.getParameter("customName"));
                            jsonObject.put("customBuildCount", request.getParameter("customBuildCount"));

                            ChartUtil.generateGraph(request, response, createChart(jsonObject, buildDataSet(jsonObject)), calcDefaultSize());
                        }
        }

        ChartUtil.generateGraph(request, response, createChart(jsonObject, buildDataSet(jsonObject)), calcDefaultSize());
    }

    private CategoryDataset buildDataSet(final JSONObject jsonObject) throws IOException {
        final String dashboard = jsonObject.getString("dashboard");
        final String chartDashlet = jsonObject.getString("chartDashlet");
        final String measure = jsonObject.getString("measure");
        int customBuildCount, i = 0;
        if (StringUtils.isBlank(jsonObject.getString("customBuildCount")))
            customBuildCount = 0;
        else
            customBuildCount = Integer.parseInt(jsonObject.getString("customBuildCount"));

        final List<DashboardReport> dashboardReports = getDashBoardReports(dashboard);
        final DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dsb = new DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel>();

        for (DashboardReport dashboardReport : dashboardReports) {
            double metricValue = 0;
            if (dashboardReport.getChartDashlets() != null) {
                Measure m = dashboardReport.getMeasure(chartDashlet, measure);
                if (m != null) metricValue = m.getMetricValue();
            }
            i++;
            dsb.add(metricValue, chartDashlet, new ChartUtil.NumberOnlyBuildLabel((Run<?, ?>) dashboardReport.getBuild()));
            if (customBuildCount != 0 && i == customBuildCount) break;
        }
        return dsb.build();
    }

    private JFreeChart createChart(final JSONObject jsonObject, final CategoryDataset dataset) throws UnsupportedEncodingException {
        final String measure = jsonObject.getString(Messages.DTPerfSigProjectAction_ReqParamMeasure());
        final String chartDashlet = jsonObject.getString("chartDashlet");
        final String testCase = jsonObject.getString("dashboard");
        final String customMeasureName = jsonObject.getString("customName");

        String unit = "", color = "";
        final Measure m = getMeasure(testCase, chartDashlet, measure);
        if (m != null) {
            unit = m.getUnit();
            color = URLDecoder.decode(m.getColor(), "UTF-8");
        }
        if (StringUtils.isBlank(color)) color = Messages.DTPerfSigProjectAction_DefaultColor();

        String title = customMeasureName;
        if (StringUtils.isBlank(customMeasureName))
            title = PerfSigUtils.generateTitle(measure, chartDashlet);

        final JFreeChart chart = ChartFactory.createBarChart(title, // title
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
            PerfSigTestDataWrapper testDataWrapper = run.getAction(PerfSigTestDataWrapper.class);
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
        String title = "UnitTest Overview";

        final JFreeChart chart = ChartFactory.createBarChart(title, // title
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
        if (lastRun != null && lastRun.getAction(PerfSigBuildAction.class) != null) {
            for (DashboardReport dr : lastRun.getAction(PerfSigBuildAction.class).getDashboardReports())
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
            PerfSigBuildAction a = b.getAction(PerfSigBuildAction.class);
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
            PerfSigTestDataWrapper testDataWrapper = run.getAction(PerfSigTestDataWrapper.class);
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
            final PerfSigBuildAction performanceBuildAction = currentBuild.getAction(PerfSigBuildAction.class);
            if (performanceBuildAction != null) {
                DashboardReport dashboardReport = performanceBuildAction.getBuildActionResultsDisplay().getDashBoardReport(tc);
                if (dashboardReport == null) {
                    dashboardReport = new DashboardReport(tc);
                    dashboardReport.setBuildAction(new PerfSigBuildAction(currentBuild, null));
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
            PerfSigUtils.downloadFile(request, response, project.getBuildByNumber(id));
        }
    }

    private FilePath getJsonConfigFilePath() {
        final FilePath configPath = new FilePath(project.getConfigFile().getFile());
        return configPath.getParent();
    }

    @SuppressWarnings("unused")
    @JavaScriptMethod
    public String getDashboardConfiguration() throws IOException {
        File input = new File(getJsonConfigFilePath() + File.separator + "gridconfig.json");
        if (!input.exists() || PerfSigUtils.getInstanceOrDie().getPluginManager().getPlugin("performance-signature").getVersionNumber().isOlderThan(new VersionNumber("1.5.2"))) {
            FileUtils.writeStringToFile(input, createJSONConfigString());
        }
        FileInputStream fileInputStream = new FileInputStream(input);
        try {
            return IOUtils.toString(fileInputStream, "UTF-8");
        } finally {
            IOUtils.closeQuietly(fileInputStream);
        }
    }

    private String createJSONConfigString() {
        int col = 1, row = 1;
        JSONArray array = new JSONArray();
        for (DashboardReport dashboardReport : getLastDashboardReports()) {
            if (dashboardReport.isUnitTest()) {
                JSONObject obj = new JSONObject();
                obj.put("id", "unittest_overview");
                obj.put("col", col++);
                obj.put("row", row);
                obj.put("dashboard", dashboardReport.getName());
                obj.put("chartDashlet", "");
                obj.put("measure", "");
                obj.put("show", true);
                obj.put("customName", "");
                obj.put("customBuildCount", 0);

                array.add(obj);
            }
            for (ChartDashlet chartDashlet : dashboardReport.getChartDashlets()) {
                for (Measure measure : chartDashlet.getMeasures()) {
                    JSONObject obj = new JSONObject();
                    obj.put("id", DigestUtils.md5Hex(dashboardReport.getName() + chartDashlet.getName() + measure.getName()));
                    obj.put("col", col++);
                    obj.put("row", row);
                    obj.put("dashboard", dashboardReport.getName());
                    obj.put("chartDashlet", chartDashlet.getName());
                    obj.put("measure", measure.getName());
                    obj.put("show", true);
                    obj.put("customName", "");
                    obj.put("customBuildCount", 0);

                    array.add(obj);

                    if (col > 3) {
                        col = 1;
                        row++;
                    }
                }
            }
        }
        return array.toString();
    }

    @SuppressWarnings("unused")
    @JavaScriptMethod
    public void setDashboardConfiguration(final String dashboard, final String data) throws IOException {
        final HashMap<String, MeasureNameHelper> map = new HashMap<String, MeasureNameHelper>();
        for (DashboardReport dashboardReport : getLastDashboardReports())
            if (dashboardReport.getName().equals(dashboard))
                for (ChartDashlet chartDashlet : dashboardReport.getChartDashlets())
                    for (Measure measure : chartDashlet.getMeasures())
                        map.put(DigestUtils.md5Hex(dashboardReport.getName() + chartDashlet.getName() + measure.getName()),
                                new MeasureNameHelper(chartDashlet.getName(), measure.getName()));

        String json = StringEscapeUtils.unescapeJson(data);
        json = json.substring(1, json.length() - 1);

        final JSONArray jsonArray = JSONArray.fromObject(json);
        for (int i = 0; i < jsonArray.size(); i++) {
            final JSONObject obj = jsonArray.getJSONObject(i);
            final MeasureNameHelper tmp = map.get(obj.getString("id"));
            obj.put("id", DigestUtils.md5Hex(dashboard + tmp.chartDashlet + tmp.measure + obj.getString("customName")));
            obj.put("chartDashlet", tmp.chartDashlet);
            obj.put("measure", tmp.measure);
        }

        final JSONArray oldJsonArray = JSONArray.fromObject(getDashboardConfiguration());
        for (int i = 0; i < oldJsonArray.size(); i++) {
            final JSONObject obj = oldJsonArray.getJSONObject(i);
            if (!obj.getString("dashboard").equals(dashboard))
                jsonArray.add(obj);
        }

        File output = new File(getJsonConfigFilePath() + File.separator + "gridconfig" + ".json");
        PrintWriter out = new PrintWriter(output, "UTF-8");
        out.print(jsonArray.toString());
        IOUtils.closeQuietly(out);
    }

    @SuppressWarnings("unused")
    @JavaScriptMethod
    public Map<String, String> getAvailableMeasures(final String dashboard, final String dashlet) throws IOException {
        if (StringUtils.isBlank(dashlet)) return null;
        final Map<String, String> availableMeasures = new HashMap<String, String>();
        for (DashboardReport dashboardReport : getLastDashboardReports()) {
            if (dashboardReport.getName().equals(dashboard)) {
                for (ChartDashlet chartDashlet : dashboardReport.getChartDashlets()) {
                    if (chartDashlet.getName().equals(dashlet)) {
                        for (Measure measure : chartDashlet.getMeasures())
                            availableMeasures.put(DigestUtils.md5Hex(dashboardReport.getName() + chartDashlet.getName() + measure.getName()), measure.getName());
                        return availableMeasures;
                    }
                }
            }
        }
        return availableMeasures;
    }

    @SuppressWarnings("unused")
    public List<ChartDashlet> getFilteredChartDashlets(final DashboardReport dashboardReport) throws IOException {
        final List<ChartDashlet> chartDashlets = new ArrayList<ChartDashlet>();
        final String json = getDashboardConfiguration();
        if (StringUtils.isBlank(json) || dashboardReport.getChartDashlets() == null) return chartDashlets;
        final JSONArray jsonArray = JSONArray.fromObject(json);

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            String measure = obj.getString("measure");
            String chartDashlet = obj.getString("chartDashlet");

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

class MeasureNameHelper {
    public String chartDashlet, measure;

    public MeasureNameHelper(String chartDashlet, String measure) {
        this.chartDashlet = chartDashlet;
        this.measure = measure;
    }
}
