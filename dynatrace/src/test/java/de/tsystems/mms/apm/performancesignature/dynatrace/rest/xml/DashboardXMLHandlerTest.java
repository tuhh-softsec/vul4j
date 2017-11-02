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

package de.tsystems.mms.apm.performancesignature.dynatrace.rest.xml;

import de.tsystems.mms.apm.performancesignature.dynatrace.model.ChartDashlet;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.DashboardReport;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.Measure;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
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
    public void testXMLParser() throws JAXBException {
        File file = new File("src/test/resources/dashboardXMLHandlerTest.xml");
        JAXBContext jaxbContext = JAXBContext.newInstance(DashboardReport.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        DashboardReport dashboardReport = (DashboardReport) jaxbUnmarshaller.unmarshal(file);

        //assertEquals(dashboardReport.getIncidents().size(), 2);
        assertEquals(dashboardReport.getChartDashlets().size(), 6);

        assertTrue(containsMeasure(dashboardReport.getChartDashlets(), "Synthetic Web Requests by Timer Name - PurePath Response Time"));
        assertTrue(containsMeasure(dashboardReport.getChartDashlets(), "Total GC Utilization"));
    }
}