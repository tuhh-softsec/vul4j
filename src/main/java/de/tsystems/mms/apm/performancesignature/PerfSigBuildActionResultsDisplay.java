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

import de.tsystems.mms.apm.performancesignature.dynatrace.model.DashboardReport;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.Measure;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.Measurement;
import de.tsystems.mms.apm.performancesignature.util.PerfSigUtils;
import hudson.Functions;
import hudson.model.AbstractBuild;
import hudson.model.ModelObject;
import hudson.util.Area;
import hudson.util.ChartUtil;
import org.apache.commons.lang.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.awt.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by rapi on 19.05.2014.
 */
public class PerfSigBuildActionResultsDisplay implements ModelObject {
    private static AbstractBuild<?, ?> currentBuild = null;
    private final transient PerfSigBuildAction buildAction;
    private final List<DashboardReport> currentDashboardReports;

    public PerfSigBuildActionResultsDisplay(final PerfSigBuildAction buildAction) {
        this.buildAction = buildAction;

        this.currentDashboardReports = this.buildAction.getDashboardReports();
        if (currentDashboardReports != null) {
            for (DashboardReport dashboardReport : currentDashboardReports) {
                dashboardReport.setBuildAction(buildAction);
                addPreviousBuildTestCaseToExistingTestCase();
            }
        }
    }

    public String getDisplayName() {
        return Messages.PerfSigBuildActionResultsDisplay_DisplayName();
    }

    public PerfSigUtils getPerfSigUtils() {
        return new PerfSigUtils();
    }

    public AbstractBuild<?, ?> getBuild() {
        return this.buildAction.getBuild();
    }

    public List<DashboardReport> getCurrentDashboardReports() {
        return this.currentDashboardReports;
    }

    private void addPreviousBuildTestCaseToExistingTestCase() {
        if (currentBuild == null) {
            currentBuild = getBuild();
        } else if (currentBuild != getBuild()) {
            currentBuild = null;
            return;
        }

        AbstractBuild<?, ?> previousBuild = getBuild().getPreviousNotFailedBuild();
        if (previousBuild == null) {
            return;
        }
        PerfSigBuildAction prevBuildAction = previousBuild.getAction(PerfSigBuildAction.class);
        if (prevBuildAction == null) {
            return;
        }
        PerfSigBuildActionResultsDisplay previousBuildActionResults = prevBuildAction.getBuildActionResultsDisplay();

        for (DashboardReport currentDashboardReport : getCurrentDashboardReports()) {
            currentDashboardReport.setLastDashboardReport(previousBuildActionResults.getDashBoardReport(currentDashboardReport.getName()));
        }
    }

    public DashboardReport getDashBoardReport(final String report) {
        if (currentDashboardReports == null) return null;
        for (DashboardReport dashboardReport : currentDashboardReports) {
            if (dashboardReport.getName().equals(report))
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

        final String chartDashlet = request.getParameter(Messages.PerfSigBuildActionResultsDisplay_ReqParamChartDashlet());
        final boolean percentile = chartDashlet.contains(Messages.PerfSigBuildActionResultsDisplay_Percentile());

        if (percentile) {
            ChartUtil.generateGraph(request, response, createXYLineChart(request, buildXYDataSet(request)), calcDefaultSize());
        } else {
            ChartUtil.generateGraph(request, response, createBarChart(request, buildIntervalDataSet(request)), calcDefaultSize());
        }
    }

    private XYDataset buildXYDataSet(final StaplerRequest request) {
        final String measure = request.getParameter(Messages.PerfSigBuildActionResultsDisplay_ReqParamMeasure());
        final String chartDashlet = request.getParameter(Messages.PerfSigBuildActionResultsDisplay_ReqParamChartDashlet());
        final String testCase = request.getParameter(Messages.PerfSigBuildActionResultsDisplay_ReqParamTestCase());
        final XYSeries xySeries = new XYSeries(chartDashlet);

        final DashboardReport dashboardReport = getDashBoardReport(testCase);
        final Measure m = dashboardReport.getMeasure(chartDashlet, measure);
        if (m == null || m.getMeasurements() == null) return null;

        for (Measurement measurement : m.getMeasurements()) {
            xySeries.add(measurement.getTimestamp(), measurement.getAvg());
        }

        return new XYSeriesCollection(xySeries);
    }

    private IntervalXYDataset buildIntervalDataSet(final StaplerRequest request) {
        final String measure = request.getParameter(Messages.PerfSigBuildActionResultsDisplay_ReqParamMeasure());
        final String chartDashlet = request.getParameter(Messages.PerfSigBuildActionResultsDisplay_ReqParamChartDashlet());
        final String testCase = request.getParameter(Messages.PerfSigBuildActionResultsDisplay_ReqParamTestCase());
        final TimeSeries timeSeries = new TimeSeries(chartDashlet, Second.class);

        final DashboardReport dashboardReport = getDashBoardReport(testCase);
        final Measure m = dashboardReport.getMeasure(chartDashlet, measure);
        if (m == null || m.getMeasurements() == null) return null;

        for (Measurement measurement : m.getMeasurements()) {
            timeSeries.add(new Second(new Date(measurement.getTimestamp())), measurement.getMetricValue(m.getAggregation()));
        }
        final TimeSeriesCollection data = new TimeSeriesCollection(timeSeries);
        data.setDomainIsPointsInTime(false);
        return data;
    }

    private JFreeChart createXYLineChart(final StaplerRequest req, final XYDataset dataset) throws UnsupportedEncodingException {
        final String chartDashlet = req.getParameter(Messages.PerfSigBuildActionResultsDisplay_ReqParamChartDashlet());
        final String measure = req.getParameter(Messages.PerfSigBuildActionResultsDisplay_ReqParamMeasure());
        final String unit = req.getParameter(Messages.PerfSigBuildActionResultsDisplay_ReqParamUnit());
        String color = req.getParameter(Messages.PerfSigBuildActionResultsDisplay_ReqParamColor());
        if (StringUtils.isBlank(color))
            color = Messages.PerfSigBuildActionResultsDisplay_DefaultColor();
        else
            URLDecoder.decode(req.getParameter(Messages.PerfSigBuildActionResultsDisplay_ReqParamColor()), "UTF-8");

        final JFreeChart chart = ChartFactory.createXYLineChart(
                PerfSigUtils.generateTitle(measure, chartDashlet).replaceAll("\\d+\\w", ""), // title
                "%", // category axis label
                unit, // value axis label
                dataset, // data
                PlotOrientation.VERTICAL, // orientation
                false, // include legend
                true, // tooltips
                false // urls
        );
        final XYPlot xyPlot = chart.getXYPlot();
        xyPlot.setForegroundAlpha(0.8f);
        xyPlot.setRangeGridlinesVisible(true);
        xyPlot.setRangeGridlinePaint(Color.black);
        xyPlot.setOutlinePaint(null);

        final XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) xyPlot.getRenderer();
        renderer.setSeriesPaint(0, Color.decode(color));
        renderer.setSeriesStroke(0, new BasicStroke(2));

        chart.setBackgroundPaint(Color.white);
        return chart;
    }

    private JFreeChart createBarChart(final StaplerRequest req, final IntervalXYDataset dataset) throws UnsupportedEncodingException {
        final String chartDashlet = req.getParameter(Messages.PerfSigBuildActionResultsDisplay_ReqParamChartDashlet());
        final String measure = req.getParameter(Messages.PerfSigBuildActionResultsDisplay_ReqParamMeasure());
        final String unit = req.getParameter(Messages.PerfSigBuildActionResultsDisplay_ReqParamUnit());
        String color = req.getParameter(Messages.PerfSigBuildActionResultsDisplay_ReqParamColor());
        if (StringUtils.isBlank(color))
            color = Messages.PerfSigBuildActionResultsDisplay_DefaultColor();
        else
            URLDecoder.decode(req.getParameter(Messages.PerfSigBuildActionResultsDisplay_ReqParamColor()), "UTF-8");

        final JFreeChart chart = ChartFactory.createXYBarChart(PerfSigUtils.generateTitle(measure, chartDashlet), // title
                "time", // domain axis label
                true,
                unit,
                dataset, // data
                PlotOrientation.VERTICAL, // orientation
                false, // include legend
                true, // tooltips
                false // urls
        );

        final XYPlot xyPlot = chart.getXYPlot();
        xyPlot.setForegroundAlpha(0.8f);
        xyPlot.setRangeGridlinesVisible(true);
        xyPlot.setRangeGridlinePaint(Color.black);
        xyPlot.setOutlinePaint(null);

        DateAxis dateAxis = (DateAxis) xyPlot.getDomainAxis();
        dateAxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
        dateAxis.setDateFormatOverride(new SimpleDateFormat("HH:mm:ss"));
        dateAxis.setAutoRange(true);
        dateAxis.setTickLabelsVisible(true);

        XYBarRenderer renderer = (XYBarRenderer) xyPlot.getRenderer();
        StandardXYToolTipGenerator generator = new StandardXYToolTipGenerator("{1} = {2}", new SimpleDateFormat("yyyy"), new DecimalFormat("0"));
        renderer.setBaseToolTipGenerator(generator);
        renderer.setSeriesPaint(0, Color.decode(color));

        chart.setBackgroundPaint(Color.white);
        return chart;
    }

    private Area calcDefaultSize() {
        Area res = Functions.getScreenResolution();
        if (res != null && res.width <= 800)
            return new Area(250, 100);
        else
            return new Area(500, 200);
    }

    public void doDownloadFile(final StaplerRequest request, final StaplerResponse response) throws IOException {
        PerfSigUtils.downloadFile(request, response, getBuild());
    }
}
