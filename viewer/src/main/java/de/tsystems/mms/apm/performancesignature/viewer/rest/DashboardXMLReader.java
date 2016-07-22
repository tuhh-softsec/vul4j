/*
 * Copyright (c) 2008-2015, DYNATRACE LLC
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright notice,
 *       this list of conditions and the following disclaimer in the documentation
 *       and/or other materials provided with the distribution.
 *     * Neither the name of the dynaTrace software nor the names of its contributors
 *       may be used to endorse or promote products derived from this software without
 *       specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 */

package de.tsystems.mms.apm.performancesignature.viewer.rest;

import de.tsystems.mms.apm.performancesignature.dynatrace.model.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class DashboardXMLReader {
    private final List<DashboardReport> dashboardReports;

    public DashboardXMLReader() {
        dashboardReports = new ArrayList<DashboardReport>();
    }

    //Get JDOM document from SAX Parser
    private static Document useSAXParser(final String xml) throws JDOMException, IOException {
        SAXBuilder saxBuilder = new SAXBuilder();
        return saxBuilder.build(new StringReader(xml));
    }

    public void parseXML(final String xml) throws IOException, JDOMException {
        Document jdomDoc = useSAXParser(xml);
        Element root = jdomDoc.getRootElement();
        List<Element> actions = root.getChildren("action");
        for (Element actionElement : actions) {
            List<Element> dashboardReportElements = actionElement.getChildren("dashboardReport");
            for (Element dashBoardReportElement : dashboardReportElements) {
                DashboardReport dashboardReport = new DashboardReport(dashBoardReportElement.getChildText("name"));
                dashboardReport.setUnitTest(Boolean.parseBoolean(dashBoardReportElement.getChildText("unitTest")));

                List<Element> chartDashletElements = dashBoardReportElement.getChildren("chartDashlet");
                for (Element chartDashletElement : chartDashletElements) {
                    ChartDashlet chartDashlet = new ChartDashlet(chartDashletElement);

                    List<Element> measureElements = chartDashletElement.getChildren("measure");
                    for (Element measureElement : measureElements) {
                        Measure measure = new Measure(measureElement);

                        List<Element> measurementElements = measureElement.getChildren("measurement");
                        for (Element mearsurementElement : measurementElements) {
                            Measurement measurement = new Measurement(mearsurementElement);
                            measure.addMeasurement(measurement);
                        }
                        chartDashlet.addMeasure(measure);
                    }
                    dashboardReport.addChartDashlet(chartDashlet);
                }

                List<Element> incidentElements = dashBoardReportElement.getChildren("incident");
                for (Element incidentElement : incidentElements) {
                    IncidentChart incidentChart = new IncidentChart(incidentElement);

                    List<Element> violationElements = incidentElement.getChildren("violation");
                    for (Element violationElement : violationElements) {
                        IncidentViolation incidentViolation = new IncidentViolation(violationElement);
                        incidentChart.add(incidentViolation);
                    }
                    dashboardReport.addIncident(incidentChart);
                }

                dashboardReports.add(dashboardReport);
            }
        }
    }

    public List<DashboardReport> getParsedObjects() {
        return this.dashboardReports;
    }
}
