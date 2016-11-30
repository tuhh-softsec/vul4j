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

package de.tsystems.mms.apm.performancesignature.dynatrace.rest;

import de.tsystems.mms.apm.performancesignature.dynatrace.model.*;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class DashboardXMLHandler extends DefaultHandler {
    private final DashboardReport dashboardReport;
    private ChartDashlet chartDashlet;
    private Measure measure;
    private IncidentChart incidentChart;

    public DashboardXMLHandler(String testCaseName) {
        dashboardReport = new DashboardReport(testCaseName);
    }

    public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes attr) {
        if (localName.equals("incidentchart") && attr.getLength() == 3) {
            incidentChart = new IncidentChart(attr);
        } else if (localName.equals("incidentchart") && attr.getLength() > 3) {
            incidentChart.add(new IncidentViolation(attr));
        } else if (localName.equals("chartdashlet")) {
            chartDashlet = new ChartDashlet(attr);
        } else if (localName.equals("measure")) {
            measure = new Measure(attr);
        } else if (localName.equals("measurement")) {
            measure.addMeasurement(new Measurement(attr));
        }
    }

    public void endElement(final String uri, final String localName, final String qName) {
        if (localName.equals("incidentchart") && incidentChart != null && incidentChart.getViolations() != null) {
            dashboardReport.addIncident(incidentChart);
        } else if (localName.equals("chartdashlet")) {
            dashboardReport.addChartDashlet(chartDashlet);
        } else if (localName.equals("measure")) {
            chartDashlet.addMeasure(measure);
        }
    }

    public DashboardReport getParsedObjects() {
        return this.dashboardReport;
    }
}
