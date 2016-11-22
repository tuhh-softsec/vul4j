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

import de.tsystems.mms.apm.performancesignature.dynatrace.model.DashboardReport;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.Measure;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.Measurement;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.TestRun;
import de.tsystems.mms.apm.performancesignature.model.PerfSigTestDataWrapper;
import de.tsystems.mms.apm.performancesignature.util.PerfSigUIUtils;
import hudson.FilePath;
import hudson.model.Api;
import hudson.model.ModelObject;
import hudson.model.Run;
import hudson.util.ChartUtil;
import hudson.util.XStream2;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.lang.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import javax.servlet.ServletException;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@ExportedBean
public class PerfSigBuildActionResultsDisplay implements ModelObject {
    private final transient PerfSigBuildAction buildAction;
    private final transient List<DashboardReport> currentDashboardReports;

    public PerfSigBuildActionResultsDisplay(final PerfSigBuildAction buildAction) {
        this.buildAction = buildAction;
        this.currentDashboardReports = this.buildAction.getDashboardReports();
    }

    public String getDisplayName() {
        return "Performance Signature results";
    }

    public Class getPerfSigUIUtils() {
        return PerfSigUIUtils.class;
    }

    public Run<?, ?> getBuild() {
        return this.buildAction.getBuild();
    }

    /**
     * Exposes this object to the remote API.
     */
    public Api getApi() {
        return new Api(this);
    }

    @Exported(name = "dashboardReports")
    public List<DashboardReport> getCurrentDashboardReports() {
        return this.currentDashboardReports;
    }

    @Exported
    public List<TestRun> getTestRuns() {
        PerfSigTestDataWrapper wrapper = getBuild().getAction(PerfSigTestDataWrapper.class);
        return wrapper != null ? wrapper.getTestRuns() : Collections.<TestRun>emptyList();
    }

    public DashboardReport getPreviousDashboardReport(final String dashboard) {
        Run<?, ?> previousBuild = getBuild().getPreviousNotFailedBuild();
        if (previousBuild == null) {
            return null;
        }
        PerfSigBuildAction prevBuildAction = previousBuild.getAction(PerfSigBuildAction.class);
        if (prevBuildAction == null) {
            return null;
        }
        PerfSigBuildActionResultsDisplay previousBuildActionResults = prevBuildAction.getBuildActionResultsDisplay();
        return previousBuildActionResults.getDashBoardReport(dashboard);
    }

    public DashboardReport getDashBoardReport(final String reportName) {
        if (currentDashboardReports == null) return null;
        for (DashboardReport dashboardReport : currentDashboardReports) {
            if (dashboardReport.getName().equals(reportName))
                return dashboardReport;
        }
        return null;
    }

    public void doSummarizerGraph(final StaplerRequest request, final StaplerResponse response) throws IOException {
        if (ChartUtil.awtProblemCause != null) {
            // not available. send out error message
            response.sendRedirect2(request.getContextPath() + "/images/headless.png");
            return;
        }

        if (getBuild() != null && request.checkIfModified(getBuild().getTimestamp(), response))
            return;

        ChartUtil.generateGraph(request, response, createTimeSeriesChart(request, buildTimeSeriesDataSet(request)), PerfSigUIUtils.calcDefaultSize());
    }

    private XYDataset buildTimeSeriesDataSet(final StaplerRequest request) {
        String measure = request.getParameter("measure");
        String chartDashlet = request.getParameter("chartdashlet");
        String testCase = request.getParameter("testcase");
        TimeSeries timeSeries = new TimeSeries(chartDashlet, Second.class);

        DashboardReport dashboardReport = getDashBoardReport(testCase);
        Measure m = dashboardReport.getMeasure(chartDashlet, measure);
        if (m == null || m.getMeasurements() == null) return null;

        for (Measurement measurement : m.getMeasurements()) {
            timeSeries.add(new Second(new Date(measurement.getTimestamp())), measurement.getMetricValue(m.getAggregation()));
        }
        return new TimeSeriesCollection(timeSeries);
    }

    private JFreeChart createTimeSeriesChart(final StaplerRequest request, final XYDataset dataset) throws UnsupportedEncodingException {
        String measure = request.getParameter("measure");
        String chartDashlet = request.getParameter("chartdashlet");
        String testCase = request.getParameter("testcase");

        final DashboardReport dashboardReport = getDashBoardReport(testCase);
        final Measure m = dashboardReport.getMeasure(chartDashlet, measure);
        if (m == null) return null;

        String color = m.getColor();
        String unit = m.getUnit();

        JFreeChart chart;
        if (unit.equalsIgnoreCase("num")) {
            chart = ChartFactory.createXYBarChart(PerfSigUIUtils.generateTitle(measure, chartDashlet), // title
                    "time", // domain axis label
                    true,
                    unit,
                    (IntervalXYDataset) dataset, // data
                    PlotOrientation.VERTICAL, // orientation
                    false, // include legend
                    false, // tooltips
                    false // urls
            );
        } else {
            chart = ChartFactory.createTimeSeriesChart(PerfSigUIUtils.generateTitle(measure, chartDashlet), // title
                    "time", // domain axis label
                    unit,
                    dataset, // data
                    false, // include legend
                    false, // tooltips
                    false // urls
            );
        }

        XYPlot xyPlot = chart.getXYPlot();
        xyPlot.setForegroundAlpha(0.8f);
        xyPlot.setRangeGridlinesVisible(true);
        xyPlot.setRangeGridlinePaint(Color.black);
        xyPlot.setOutlinePaint(null);

        XYItemRenderer xyitemrenderer = xyPlot.getRenderer();
        if (xyitemrenderer instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyitemrenderer;
            xylineandshaperenderer.setBaseShapesVisible(true);
            xylineandshaperenderer.setBaseShapesFilled(true);
        }
        DateAxis dateAxis = (DateAxis) xyPlot.getDomainAxis();
        dateAxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
        dateAxis.setDateFormatOverride(new SimpleDateFormat("HH:mm:ss"));
        xyitemrenderer.setSeriesPaint(0, Color.decode(color));
        xyitemrenderer.setSeriesStroke(0, new BasicStroke(2));

        chart.setBackgroundPaint(Color.white);
        return chart;
    }

    public void doGetSingleReport(final StaplerRequest request, final StaplerResponse response) throws IOException, InterruptedException {
        serveFile("Singlereport", request, response);
    }

    public void doGetComparisonReport(final StaplerRequest request, final StaplerResponse response) throws IOException, InterruptedException {
        serveFile("Comparisonreport", request, response);
    }

    public void doGetSession(final StaplerRequest request, final StaplerResponse response) throws IOException, InterruptedException {
        serveFile("", request, response);
    }

    public void doGetSingleReportList(final StaplerRequest request, final StaplerResponse response) throws IOException, InterruptedException {
        getReportList("Singlereport", request, response);
    }

    public void doGetComparisonReportList(final StaplerRequest request, final StaplerResponse response) throws IOException, InterruptedException {
        getReportList("Comparisonreport", request, response);
    }

    private void getReportList(final String type, final StaplerRequest request, final StaplerResponse response) throws IOException, InterruptedException {
        String testCase = request.getParameter("testCase");
        if (StringUtils.isBlank(testCase)) testCase = "";

        FilePath reportDir = new FilePath(PerfSigUIUtils.getReportDirectory(getBuild()));
        List<FilePath> files = reportDir.list(new RegexFileFilter(type + ".*" + testCase + ".*.pdf"));
        List<String> fileNames = new ArrayList<String>();
        for (FilePath fp : files) {
            fileNames.add(PerfSigUIUtils.removeExtension(fp.getName()));
        }
        XStream2 xstream = new XStream2();
        xstream.toXMLUTF8(fileNames, response.getOutputStream());
    }

    private void serveFile(final String type, final StaplerRequest request, final StaplerResponse response) throws IOException, InterruptedException {
        String testCase = request.getParameter("testCase");
        if (StringUtils.isBlank(testCase)) testCase = "";

        String numberString = request.getParameter("number");
        int number = 0;
        try {
            number = Integer.parseInt(numberString);
        } catch (NumberFormatException ignored) {
        }

        FilePath filePath = new FilePath(PerfSigUIUtils.getReportDirectory(getBuild()));
        String extension = StringUtils.isBlank(type) ? ".dts" : ".pdf";
        List<FilePath> files = filePath.list(new RegexFileFilter(type + ".*" + testCase + ".*" + extension));
        if (files.isEmpty()) {
            response.sendError(404, "requested resource not found");
            return;
        }

        FilePath requestedFile = number > 0 ? files.get(number) : files.get(0);
        if (requestedFile == null) {
            response.sendError(404, "requested resource not found");
            return;
        }
        InputStream inStream = requestedFile.read();
        // gets MIME type of the file
        String mimeType = extension.equals("pdf") ? "application/pdf" : "application/octet-stream";// set to binary type if MIME mapping not found

        try {
            // forces download
            String headerKey = "Content-Disposition";
            String headerValue = String.format("attachment; filename=\"%s\"", requestedFile.getName());
            response.setHeader(headerKey, headerValue);
            response.serveFile(request, inStream, requestedFile.lastModified(), requestedFile.length(), "mime-type:" + mimeType);
        } catch (ServletException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inStream);
        }
    }
}
