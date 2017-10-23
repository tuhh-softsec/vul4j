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

class DashboardXMLReader {
    private final List<DashboardReport> dashboardReports;

    DashboardXMLReader() {
        dashboardReports = new ArrayList<>();
    }

    //Get JDOM document from SAX Parser
    private static Document useSAXParser(final String xml) throws JDOMException, IOException {
        SAXBuilder saxBuilder = new SAXBuilder();
        return saxBuilder.build(new StringReader(xml));
    }

    void parseXML(final String xml) throws IOException, JDOMException {
        Document jdomDoc = useSAXParser(xml);
        Element root = jdomDoc.getRootElement();
        List<Element> xmlDashboardReports = root.getChildren("dashboardReport");
        for (Element xmlDashboardReport : xmlDashboardReports) {
            DashboardReport dashboardReport = new DashboardReport(xmlDashboardReport.getChildText("name"));
            List<Element> chartDashletElements = xmlDashboardReport.getChildren("chartDashlet");
            for (Element chartDashletElement : chartDashletElements) {
                //ToDo: fix me!
                ChartDashlet chartDashlet = new ChartDashlet();

                List<Element> measureElements = chartDashletElement.getChildren("measure");
                for (Element measureElement : measureElements) {
                    //ToDo: fix me!
                    Measure measure = new Measure();

                    List<Element> measurementElements = measureElement.getChildren("measurement");
                    for (Element mearsurementElement : measurementElements) {
                        //ToDo: fix me!
                        Measurement measurement = new Measurement();
                        measure.addMeasurement(measurement);
                    }
                    chartDashlet.addMeasure(measure);
                }
                dashboardReport.addChartDashlet(chartDashlet);
            }

            List<Element> incidentElements = xmlDashboardReport.getChildren("incident");
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

    List<DashboardReport> getParsedObjects() {
        return this.dashboardReports;
    }
}
