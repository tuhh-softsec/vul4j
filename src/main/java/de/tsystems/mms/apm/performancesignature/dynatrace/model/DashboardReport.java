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

import de.tsystems.mms.apm.performancesignature.DTPerfSigBuildAction;
import de.tsystems.mms.apm.performancesignature.model.ConfigurationTestCase;
import de.tsystems.mms.apm.performancesignature.model.UnitTestCase;
import hudson.model.AbstractBuild;

import java.util.Date;
import java.util.List;

/**
 * Created by rapi on 19.05.2014.
 */
public class DashboardReport {
    private String name;
    private List<ChartDashlet> chartDashlets;
    private DTPerfSigBuildAction buildAction;
    private DashboardReport lastDashboardReport;
    private ConfigurationTestCase configurationTestCase;

    public DashboardReport(final String testCaseName) {
        this.name = testCaseName;
    }

    public List<ChartDashlet> getChartDashlets() {
        return chartDashlets;
    }

    public void setChartDashlets(final List<ChartDashlet> chartDashlets) {
        this.chartDashlets = chartDashlets;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public DTPerfSigBuildAction getBuildAction() {
        return this.buildAction;
    }

    public void setBuildAction(final DTPerfSigBuildAction buildAction) {
        this.buildAction = buildAction;
    }

    public ConfigurationTestCase getConfigurationTestCase() {
        return configurationTestCase;
    }

    public void setConfigurationTestCase(final ConfigurationTestCase configurationTestCase) {
        this.configurationTestCase = configurationTestCase;
    }

    @SuppressWarnings("unused")
    public DashboardReport getLastDashboardReport() {
        return lastDashboardReport;
    }

    public void setLastDashboardReport(final DashboardReport lastDashboardReport) {
        this.lastDashboardReport = lastDashboardReport;
    }

    public AbstractBuild<?, ?> getBuild() {
        return this.buildAction.getBuild();
    }

    @SuppressWarnings("unused")
    public Date getBuildTime() {
        return getBuild().getTime();
    }

    public boolean isUnitTest() {
        if (this.configurationTestCase instanceof UnitTestCase)
            return true;
        return false;
    }

    @Override
    public String toString() {
        return "DashboardReport{" +
                "name='" + name + '\'' +
                ", chartDashlets=" + chartDashlets +
                ", buildAction=" + buildAction +
                ", lastDashboardReport=" + lastDashboardReport +
                ", configurationTestCase=" + configurationTestCase +
                '}';
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
