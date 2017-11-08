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

package de.tsystems.mms.apm.performancesignature.dynatrace.model;

import de.tsystems.mms.apm.performancesignature.model.ClientLinkGenerator;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "dashboardreport")
@ExportedBean
public class DashboardReport {

    @XmlElementWrapper(name = "data")
    @XmlElement(name = "chartdashlet")
    private List<ChartDashlet> chartDashlets;
    private String name;
    private List<Alert> incidents;
    private boolean unitTest;
    private ClientLinkGenerator clientLink;

    public DashboardReport(final String testCaseName) {
        this.name = testCaseName;
        this.chartDashlets = new ArrayList<>();
        this.incidents = new ArrayList<>();
    }

    public DashboardReport() {
        this(null);
    }

    @Exported
    public List<Alert> getIncidents() {
        return incidents;
    }

    public void addIncident(final Alert incident) {
        this.incidents.add(incident);
    }

    /**
     * Gets the value of the chartdashlet property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the chartdashlet property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getChartdashlet().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ChartDashlet }
     */
    @Exported
    public List<ChartDashlet> getChartDashlets() {
        return this.chartDashlets;
    }

    /**
     * Ruft den Wert der name-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    @Exported
    public String getName() {
        return name;
    }

    /**
     * Legt den Wert der name-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setName(String value) {
        this.name = value;
    }

    public void addChartDashlet(final ChartDashlet chartDashlet) {
        this.chartDashlets.add(chartDashlet);
    }

    @Exported
    public boolean isUnitTest() {
        return unitTest;
    }

    public void setUnitTest(final boolean unitTest) {
        this.unitTest = unitTest;
    }

    public ClientLinkGenerator getClientLink() {
        return clientLink;
    }

    public void setClientLink(final ClientLinkGenerator clientLink) {
        this.clientLink = clientLink;
    }

    public Measure getMeasure(final String chartDashlet, final String measure) {
        for (ChartDashlet cd : this.chartDashlets) {
            if (cd.getName().equalsIgnoreCase(chartDashlet) && cd.getMeasures() != null) {
                for (Measure m : cd.getMeasures()) {
                    if (m.getName().equalsIgnoreCase(measure))
                        return m;
                }
            }
        }
        return null;
    }
}
