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

import de.tsystems.mms.apm.performancesignature.dynatrace.model.Measure;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.Measurement;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.ChartDashlet;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.CharArrayWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rapi on 27.10.2014.
 */

public class DashboardXMLHandler extends DefaultHandler {
    private final List<ChartDashlet> chartDashlets = new ArrayList<ChartDashlet>();
    private final CharArrayWriter contents = new CharArrayWriter();
    private ChartDashlet chartDashlet;
    private Measure measure;

    public List<ChartDashlet> getParsedObjects() {
        return this.chartDashlets;
    }

    public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes attr) {
        if (localName.equals(Messages.DashboardXMLHandler_AttrChartDashlet())) {
            chartDashlet = new ChartDashlet(attr);
        } else if (localName.equals(Messages.DashboardXMLHandler_AttrMeasure())) {
            measure = new Measure(attr);
        } else if (localName.equals(Messages.DashboardXMLHandler_AttrMeasurement())) {
            measure.addMeasurement(new Measurement(attr));
        }
        this.contents.reset();
    }

    public void endElement(final String uri, final String localName, final String qName) {
        if (localName.equals(Messages.DashboardXMLHandler_AttrChartDashlet())) {
            chartDashlets.add(chartDashlet);
        } else if (localName.equals(Messages.DashboardXMLHandler_AttrMeasure())) {
            chartDashlet.addMeasure(measure);
        }
    }

    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        this.contents.write(ch, start, length);
    }
}
