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

package de.tsystems.mms.apm.performancesignature.dynatrace.rest;

import de.tsystems.mms.apm.performancesignature.dynatrace.model.ChartDashlet;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.Measure;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.*;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DashboardXMLHandlerTest {

    private static boolean containsMeasure(List<ChartDashlet> chartDashlets, String searchString) {
        for (ChartDashlet cd : chartDashlets) {
            for (Measure m : cd.getMeasures()) {
                if (m.getName().equalsIgnoreCase(searchString)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Test
    public void testXMLParser63() throws SAXException, IOException {
        XMLReader xr = XMLReaderFactory.createXMLReader();
        DashboardXMLHandler handler = new DashboardXMLHandler("test");
        xr.setContentHandler(handler);

        File file = new File("src/test/resources/dashboardXMLHandler63Test.xml");
        InputStream inputStream = new FileInputStream(file);
        xr.parse(new InputSource(new InputStreamReader(inputStream, "UTF-8")));

        assertEquals(handler.getParsedObjects().getIncidents().size(), 2);
        assertEquals(handler.getParsedObjects().getChartDashlets().size(), 7);

        assertTrue(containsMeasure(handler.getParsedObjects().getChartDashlets(), "Synthetic Web Requests by Timer Name - PurePath Response Time"));
        assertTrue(containsMeasure(handler.getParsedObjects().getChartDashlets(), "Response Time - Synchronous_Read"));
    }

    @Test
    public void testXMLParser65() throws SAXException, IOException {
        XMLReader xr = XMLReaderFactory.createXMLReader();
        DashboardXMLHandler handler = new DashboardXMLHandler("test");
        xr.setContentHandler(handler);

        File file = new File("src/test/resources/dashboardXMLHandler65Test.xml");
        InputStream inputStream = new FileInputStream(file);
        xr.parse(new InputSource(new InputStreamReader(inputStream, "UTF-8")));

        assertEquals(handler.getParsedObjects().getIncidents().size(), 2);
        assertEquals(handler.getParsedObjects().getChartDashlets().size(), 6);

        assertTrue(containsMeasure(handler.getParsedObjects().getChartDashlets(), "Synthetic Web Requests by Timer Name - PurePath Response Time"));
        assertTrue(containsMeasure(handler.getParsedObjects().getChartDashlets(), "Total GC Utilization"));
    }
}