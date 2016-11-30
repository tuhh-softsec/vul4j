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

package de.tsystems.mms.apm.performancesignature.ui;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thoughtworks.xstream.XStream;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.ChartDashlet;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.DashboardReport;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.Measure;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.TestRun;
import de.tsystems.mms.apm.performancesignature.model.JSONDashlet;
import de.tsystems.mms.apm.performancesignature.model.PerfSigTestDataWrapper;
import de.tsystems.mms.apm.performancesignature.util.PerfSigUIUtils;
import hudson.XmlFile;
import hudson.model.Job;
import hudson.model.ProminentProjectAction;
import hudson.model.Run;
import hudson.tasks.junit.TestResult;
import hudson.tasks.junit.TestResultAction;
import hudson.tasks.test.TestResultProjectAction;
import hudson.util.*;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
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
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PerfSigProjectAction extends PerfSigBaseAction implements ProminentProjectAction {
    private static final String JSON_FILENAME = "gridconfig.xml";
    private static final String UNITTEST_DASHLETNAME = "unittest_overview";
    private static final XStream XSTREAM = new XStream2();
    private static final Logger logger = Logger.getLogger(PerfSigProjectAction.class.getName());
    private final Job<?, ?> job;
    private transient Map<String, JSONDashlet> jsonDashletMap;

    public PerfSigProjectAction(final Job<?, ?> job) {
        this.job = job;
    }

    @Override
    protected String getTitle() {
        return job.getDisplayName() + " PerfSig";
    }

    public Job<?, ?> getJob() {
        return job;
    }

    public TestResultProjectAction getTestResultProjectAction() {
        return job.getAction(TestResultProjectAction.class);
    }

    public Class<PerfSigUIUtils> getPerfSigUIUtils() {
        return PerfSigUIUtils.class;
    }

    private synchronized Map<String, JSONDashlet> getJsonDashletMap() {
        if (this.jsonDashletMap == null) {
            this.jsonDashletMap = new ConcurrentHashMap<String, JSONDashlet>();
            this.jsonDashletMap.putAll(readConfiguration());
        }
        return this.jsonDashletMap;
    }

    public void doSummarizerGraph(final StaplerRequest request, final StaplerResponse response) throws IOException, InterruptedException {
        if (ChartUtil.awtProblemCause != null) {
            // not available. send out error message
            response.sendRedirect2(request.getContextPath() + "/images/headless.png");
            return;
        }

        String id = request.getParameter("id");

        if (request.getParameter("customName") == null && request.getParameter("customBuildCount") == null
                && request.getParameter("aggregation") == null) { //dashlet from stored configuration
            JSONDashlet jsonDashlet = getJsonDashletMap().get(id);
            ChartUtil.generateGraph(request, response, createChart(jsonDashlet, buildDataSet(jsonDashlet)), PerfSigUIUtils.calcDefaultSize());
        } else { //new dashlet
            JSONDashlet jsonDashlet = createJSONConfiguration(false).get(id);
            jsonDashlet.setAggregation(request.getParameter("aggregation"));
            jsonDashlet.setCustomName(request.getParameter("customName"));
            jsonDashlet.setCustomBuildCount(request.getParameter("customBuildCount"));
            ChartUtil.generateGraph(request, response, createChart(jsonDashlet, buildDataSet(jsonDashlet)), PerfSigUIUtils.calcDefaultSize());
        }
    }

    private CategoryDataset buildDataSet(final JSONDashlet jsonDashlet) throws IOException {
        String dashboard = jsonDashlet.getDashboard();
        String chartDashlet = jsonDashlet.getChartDashlet();
        String measure = jsonDashlet.getMeasure();
        String buildCount = jsonDashlet.getCustomBuildCount();
        String aggregation = jsonDashlet.getAggregation();
        int customBuildCount = 0, i = 0;

        if (StringUtils.isNotBlank(buildCount)) customBuildCount = Integer.parseInt(buildCount);

        Map<Run<?, ?>, DashboardReport> dashboardReports = getDashboardReports(dashboard);
        DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dsb = new DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel>();

        for (Map.Entry<Run<?, ?>, DashboardReport> dashboardReport : dashboardReports.entrySet()) {
            double metricValue = 0;
            if (dashboardReport.getValue().getChartDashlets() != null) {
                Measure m = dashboardReport.getValue().getMeasure(chartDashlet, measure);
                if (m != null) metricValue = StringUtils.isBlank(aggregation) ? m.getMetricValue() : m.getMetricValue(aggregation);
            }
            i++;
            dsb.add(metricValue, chartDashlet, new ChartUtil.NumberOnlyBuildLabel(dashboardReport.getKey()));
            if (customBuildCount != 0 && i == customBuildCount) break;
        }
        return dsb.build();
    }

    private JFreeChart createChart(final JSONDashlet jsonDashlet, final CategoryDataset dataset) throws UnsupportedEncodingException {
        final String measure = jsonDashlet.getMeasure();
        final String chartDashlet = jsonDashlet.getChartDashlet();
        final String dashboard = jsonDashlet.getDashboard();
        final String customMeasureName = jsonDashlet.getCustomName();
        final String aggregation = jsonDashlet.getAggregation();

        String unit = "", color = "#FF5555";

        for (DashboardReport dr : getLastDashboardReports()) {
            if (dr.getName().equals(dashboard)) {
                final Measure m = dr.getMeasure(chartDashlet, measure);
                if (m != null) {
                    unit = aggregation.equalsIgnoreCase("Count") ? "num" : m.getUnit();
                    color = m.getColor();
                }
                break;
            }
        }

        String title = StringUtils.isBlank(customMeasureName) ? PerfSigUIUtils.generateTitle(measure, chartDashlet) : customMeasureName;

        final JFreeChart chart = ChartFactory.createBarChart(title, // title
                Messages.PerfSigProjectAction_Build(), // category axis label
                unit, // value axis label
                dataset, // data
                PlotOrientation.VERTICAL, // orientation
                false, // include legend
                false, // tooltips
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

    public void doTestRunGraph(final StaplerRequest request, final StaplerResponse response) throws IOException, InterruptedException {
        if (ChartUtil.awtProblemCause != null) {
            // not available. send out error message
            response.sendRedirect2(request.getContextPath() + "/images/headless.png");
            return;
        }

        //get customName and customBuildCount from persisted xml
        if (request.getParameter("customName") == null && request.getParameter("customBuildCount") == null) {
            JSONDashlet jsonDashlet = getJsonDashletMap().get(UNITTEST_DASHLETNAME);
            if (jsonDashlet != null) {
                ChartUtil.generateGraph(request, response, createTestRunChart(buildTestRunDataSet(String.valueOf(jsonDashlet.getCustomBuildCount())),
                        jsonDashlet.getCustomName()), PerfSigUIUtils.calcDefaultSize());
            }
        } else { //generate test run graph with GET parameters
            ChartUtil.generateGraph(request, response, createTestRunChart(buildTestRunDataSet(request.getParameter("customBuildCount")),
                    request.getParameter("customName")), PerfSigUIUtils.calcDefaultSize());
        }
    }

    private CategoryDataset buildTestRunDataSet(final String customBuildCount) {
        final DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dsb = new DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel>();
        int buildCount = 0, i = 0;
        if (StringUtils.isNotBlank(customBuildCount))
            buildCount = Integer.parseInt(customBuildCount);

        for (Run<?, ?> run : job.getBuilds()) {
            PerfSigTestDataWrapper testDataWrapper = run.getAction(PerfSigTestDataWrapper.class);
            if (testDataWrapper != null && testDataWrapper.getTestRuns() != null) {
                TestRun testRun = TestRun.mergeTestRuns(testDataWrapper.getTestRuns());
                if (testRun != null) {
                    dsb.add(testRun.getNumFailed(), "failed", new ChartUtil.NumberOnlyBuildLabel(run));
                    dsb.add(testRun.getNumDegraded(), "degraded", new ChartUtil.NumberOnlyBuildLabel(run));
                    dsb.add(testRun.getNumImproved(), "improved", new ChartUtil.NumberOnlyBuildLabel(run));
                    dsb.add(testRun.getNumPassed(), "passed", new ChartUtil.NumberOnlyBuildLabel(run));
                    dsb.add(testRun.getNumVolatile(), "volatile", new ChartUtil.NumberOnlyBuildLabel(run));
                    dsb.add(testRun.getNumInvalidated(), "invalidated", new ChartUtil.NumberOnlyBuildLabel(run));
                }
            }
            i++;
            if (buildCount != 0 && i == buildCount) break;
        }
        return dsb.build();
    }

    private JFreeChart createTestRunChart(final CategoryDataset dataset, final String customName) {
        String title = StringUtils.isNotBlank(customName) ? customName : "UnitTest overview";

        final JFreeChart chart = ChartFactory.createBarChart(title, // title
                "build", // category axis label
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

    public List<DashboardReport> getLastDashboardReports() {
        final Run<?, ?> tb = job.getLastSuccessfulBuild();

        Run<?, ?> b = job.getLastBuild();
        while (b != null) {
            PerfSigBuildAction a = b.getAction(PerfSigBuildAction.class);
            if (a != null && (!b.isBuilding())) {
                return a.getDashboardReports();
            }
            if (b == tb) {
                return new ArrayList<DashboardReport>();
            }
            b = b.getPreviousBuild();
        }
        return new ArrayList<DashboardReport>();
    }

    public TestRun getTestRun(final Run<?, ?> run) {
        if (run != null) {
            PerfSigTestDataWrapper testDataWrapper = run.getAction(PerfSigTestDataWrapper.class);
            if (testDataWrapper != null) {
                return TestRun.mergeTestRuns(testDataWrapper.getTestRuns());
            }
        }
        return null;
    }

    public TestResult getTestAction(final Run<?, ?> run) {
        if (run != null) {
            TestResultAction testResultAction = run.getAction(TestResultAction.class);
            if (testResultAction != null) {
                return testResultAction.getResult();
            }
        }
        return null;
    }

    public Map<Run<?, ?>, DashboardReport> getDashboardReports(final String name) {
        final Map<Run<?, ?>, DashboardReport> dashboardReports = new HashMap<Run<?, ?>, DashboardReport>();
        if (job == null) {
            return dashboardReports;
        }
        for (Run<?, ?> currentRun : job.getBuilds()) {
            final PerfSigBuildAction perfSigBuildAction = currentRun.getAction(PerfSigBuildAction.class);
            if (perfSigBuildAction != null) {
                DashboardReport dashboardReport = perfSigBuildAction.getBuildActionResultsDisplay().getDashBoardReport(name);
                if (dashboardReport == null) {
                    dashboardReport = new DashboardReport(name);
                }
                dashboardReports.put(currentRun, dashboardReport);
            }
        }
        return dashboardReports;
    }

    @JavaScriptMethod
    public String getDashboardConfiguration(final String dashboard) throws IOException, InterruptedException {
        List<JSONDashlet> jsonDashletList = new ArrayList<JSONDashlet>();
        for (JSONDashlet jsonDashlet : getJsonDashletMap().values()) {
            if (jsonDashlet.getDashboard().equals(dashboard)) {
                jsonDashletList.add(jsonDashlet);
            }
        }

        return new Gson().toJson(jsonDashletList);
    }

    private Map<String, JSONDashlet> createJSONConfiguration(final boolean useRandomId) {
        int col = 1, row = 1;

        logger.fine(addTimeStampToLog("grid configuration generation started"));
        Map<String, JSONDashlet> jsonDashletMap = new HashMap<String, JSONDashlet>();
        for (DashboardReport dashboardReport : getLastDashboardReports()) {
            if (dashboardReport.isUnitTest()) {
                JSONDashlet dashlet = new JSONDashlet(col++, row, UNITTEST_DASHLETNAME, dashboardReport.getName());
                jsonDashletMap.put(UNITTEST_DASHLETNAME, dashlet);
            }
            for (ChartDashlet chartDashlet : dashboardReport.getChartDashlets()) {
                for (Measure measure : chartDashlet.getMeasures()) {
                    JSONDashlet dashlet = new JSONDashlet(col++, row, dashboardReport.getName(), chartDashlet.getName(), measure.getName(),
                            measure.getAggregation(), chartDashlet.getDescription());
                    if (useRandomId) {
                        dashlet.setId(dashlet.generateID());
                    }

                    jsonDashletMap.put(dashlet.getId(), dashlet);

                    if (col > 3) {
                        col = 1;
                        row++;
                    }
                }
            }
        }
        logger.fine(addTimeStampToLog("grid configuration generation finished"));
        return jsonDashletMap;
    }

    /*
    only for debug purposes
     */
    private String addTimeStampToLog(final String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        return sdf.format(new Date()) + ": " + this.getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this)) +
                ", threadId:" + Thread.currentThread().getId() + " " + message;
    }

    @JavaScriptMethod
    public void setDashboardConfiguration(final String dashboard, final String data) {
        Map<String, JSONDashlet> defaultConfiguration = createJSONConfiguration(false);
        HashSet<String> idsFromJson = new HashSet<String>();

        String json = StringEscapeUtils.unescapeJava(data);
        if (!json.startsWith("[")) json = json.substring(1, json.length() - 1);

        List<JSONDashlet> jsonDashletList = new Gson().fromJson(json, new TypeToken<List<JSONDashlet>>() {
        }.getType());
        for (JSONDashlet jsonDashlet : jsonDashletList) {
            idsFromJson.add(jsonDashlet.getId());
        }

        try {
            for (JSONDashlet jsonDashlet : getJsonDashletMap().values()) {
                if (!jsonDashlet.getDashboard().equals(dashboard)) continue; //filter out dashlets from other dashboards
                if (!idsFromJson.contains(jsonDashlet.getId())) { //remove dashlet, if it's not present in gridConfiguration
                    getJsonDashletMap().remove(jsonDashlet.getId());
                }
            }

            for (JSONDashlet modifiedDashlet : jsonDashletList) {
                JSONDashlet unmodifiedDashlet = defaultConfiguration.get(modifiedDashlet.getId());
                JSONDashlet originalDashlet = getJsonDashletMap().get(modifiedDashlet.getId());
                if (modifiedDashlet.getId().equals(UNITTEST_DASHLETNAME)) {
                    if (originalDashlet != null) {
                        modifiedDashlet.setCustomBuildCount(originalDashlet.getCustomBuildCount());
                        modifiedDashlet.setCustomName(originalDashlet.getCustomName());
                    }
                    getJsonDashletMap().put(modifiedDashlet.getId(), modifiedDashlet);
                } else if (unmodifiedDashlet != null) { //newly added dashlets
                    modifiedDashlet.setDashboard(unmodifiedDashlet.getDashboard());
                    modifiedDashlet.setChartDashlet(unmodifiedDashlet.getChartDashlet());
                    modifiedDashlet.setMeasure(unmodifiedDashlet.getMeasure());
                    modifiedDashlet.setDescription(unmodifiedDashlet.getDescription());
                    modifiedDashlet.setId(modifiedDashlet.generateID());

                    getJsonDashletMap().put(modifiedDashlet.getId(), modifiedDashlet);
                } else if (originalDashlet != null) { //old dashlets
                    modifiedDashlet.setDashboard(originalDashlet.getDashboard());
                    modifiedDashlet.setChartDashlet(originalDashlet.getChartDashlet());
                    modifiedDashlet.setMeasure(originalDashlet.getMeasure());
                    modifiedDashlet.setDescription(originalDashlet.getDescription());
                    modifiedDashlet.setAggregation(originalDashlet.getAggregation());
                    modifiedDashlet.setCustomBuildCount(originalDashlet.getCustomBuildCount());
                    modifiedDashlet.setCustomName(originalDashlet.getCustomName());

                    modifiedDashlet.setId(modifiedDashlet.generateID());
                    getJsonDashletMap().remove(originalDashlet.getId());
                    getJsonDashletMap().put(modifiedDashlet.getId(), modifiedDashlet);
                }
            }
            writeConfiguration(getJsonDashletMap());
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, Messages.PerfSigProjectAction_FailedToSaveGrid(), e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, Messages.PerfSigProjectAction_FailedToSaveGrid(), e);
        }
    }

    @JavaScriptMethod
    public Map<String, String> getAvailableMeasures(final String dashboard, final String dashlet) throws IOException {
        Map<String, String> availableMeasures = new LinkedHashMap<String, String>();
        List<JSONDashlet> jsonDashlets = new ArrayList<JSONDashlet>(createJSONConfiguration(false).values());
        Collections.sort(jsonDashlets, new Comparator<JSONDashlet>() {
            @Override
            public int compare(final JSONDashlet o1, final JSONDashlet o2) {
                return o1.getMeasure().compareTo(o2.getMeasure());
            }
        });
        for (JSONDashlet jsonDashlet : jsonDashlets) {
            if (jsonDashlet.getDashboard().equals(dashboard) && jsonDashlet.getChartDashlet().equals(dashlet)) {
                availableMeasures.put(jsonDashlet.getId(), jsonDashlet.getMeasure());
            }
        }
        return availableMeasures;
    }

    @JavaScriptMethod
    public String getAggregationFromMeasure(final String dashboard, final String dashlet, final String measure) throws IOException {
        for (DashboardReport dashboardReport : getLastDashboardReports()) {
            if (dashboardReport.getName().equals(dashboard)) {
                Measure m = dashboardReport.getMeasure(dashlet, measure);
                if (m != null) {
                    return m.getAggregation();
                }
            }
        }
        return "";
    }

    public Map<JSONDashlet, Measure> getFilteredChartDashlets(final DashboardReport dashboardReport) throws IOException, InterruptedException {
        Map<JSONDashlet, Measure> filteredChartDashlets = new TreeMap<JSONDashlet, Measure>(new Comparator<JSONDashlet>() {
            @Override
            public int compare(final JSONDashlet o1, final JSONDashlet o2) {
                if (o1.getRow() > o2.getRow() || o1.getRow() == o2.getRow() && o1.getCol() > o2.getCol()) {
                    return 1;
                }
                return -1;
            }
        });

        if (dashboardReport.getChartDashlets() == null) {
            return filteredChartDashlets;
        }

        for (JSONDashlet jsonDashlet : getJsonDashletMap().values()) {
            if (!jsonDashlet.getDashboard().equals(dashboardReport.getName())) continue;
            boolean chartDashletFound = false;

            for (ChartDashlet dashlet : dashboardReport.getChartDashlets()) {
                if (dashlet.getName().equals(jsonDashlet.getChartDashlet())) {
                    for (Measure m : dashlet.getMeasures()) {
                        if (m.getName().equals(jsonDashlet.getMeasure())) {
                            filteredChartDashlets.put(jsonDashlet, m);
                            chartDashletFound = true;
                            break;
                        }
                    }
                }
            }
            if (!chartDashletFound && !jsonDashlet.getId().equals(UNITTEST_DASHLETNAME)) {
                filteredChartDashlets.put(jsonDashlet, new Measure(null));
            }
        }
        return filteredChartDashlets;
    }

    private synchronized XmlFile getConfigFile() {
        return new XmlFile(XSTREAM, new File(job.getConfigFile().getFile().getParent(), JSON_FILENAME));
    }

    @SuppressWarnings("unchecked")
    private Map<String, JSONDashlet> readConfiguration() {
        logger.fine(addTimeStampToLog("grid configuration read started"));
        try {
            if (getConfigFile().exists()) {
                Map<String, JSONDashlet> configuration = (Map<String, JSONDashlet>) getConfigFile().read();
                logger.fine(addTimeStampToLog("grid configuration read finished (config file exists)"));
                return configuration;
            } else {
                Map<String, JSONDashlet> newConfiguration = createJSONConfiguration(true);
                writeConfiguration(newConfiguration);
                logger.fine(addTimeStampToLog("grid configuration read finished (config file created)"));
                return newConfiguration;
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, Messages.PerfSigProjectAction_FailedToLoadConfigFile(getConfigFile()), e);
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, Messages.PerfSigProjectAction_FailedToLoadConfigFile(getConfigFile()), e);
        }
        return new HashMap<String, JSONDashlet>();
    }

    private void writeConfiguration(final Map<String, JSONDashlet> jsonDashletMap) throws IOException, InterruptedException {
        try {
            logger.fine(addTimeStampToLog("grid configuration write started"));
            getConfigFile().write(jsonDashletMap);
            logger.fine(addTimeStampToLog("grid configuration write finished"));
        } catch (IOException e) {
            logger.log(Level.SEVERE, Messages.PerfSigProjectAction_FailedToSaveGrid(), e);
        }
    }
}
