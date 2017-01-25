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

import de.tsystems.mms.apm.performancesignature.dynatrace.util.AttributeUtils;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ExportedBean
public class TestResult implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Date exectime;
    private final String name, packageName;
    private final TestResultStatus status;
    private final List<TestRunMeasure> measures;

    public TestResult(final Object attr) {
        this.exectime = AttributeUtils.getDateAttribute("exectime", attr);
        this.name = AttributeUtils.getStringAttribute("name", attr);
        this.packageName = AttributeUtils.getStringAttribute("package", attr);
        this.status = TestResultStatus.fromString(AttributeUtils.getStringAttribute("status", attr));
        this.measures = new ArrayList<TestRunMeasure>();
    }

    @Exported
    public Date getExectime() {
        return (Date) exectime.clone();
    }

    @Exported
    public String getPackageName() {
        return packageName;
    }

    @Exported
    public String getName() {
        return name;
    }

    public TestRunMeasure getMeasure(final String metricGroup, final String metric) {
        for (TestRunMeasure measure : measures) {
            if (measure.getMetricGroup().equals(metricGroup) && measure.getName().equals(metric))
                return measure;
        }
        return null;
    }

    @Exported
    public List<TestRunMeasure> getTestRunMeasures() {
        return measures;
    }

    public void addTestRunMeasure(final TestRunMeasure measure) {
        measures.add(measure);
    }

    public String getStatusIcon() {
        switch (status) {
            case PASSED:
                return "glyphicon-ok";
            case FAILED:
                return "glyphicon-remove-sign";
            case IMPROVED:
                return "glyphicon-arrow-up";
            case DEGRADED:
                return "glyphicon-arrow-down";
            case VOLATILE:
                return "glyphicon-sort";
            default:
                return "";
        }
    }

    public String getStatusColor() {
        switch (status) {
            case PASSED:
                return "#2AB06F";
            case FAILED:
                return "#DC172A";
            case IMPROVED:
                return "#2AB6F4";
            case DEGRADED:
                return "#EF651F";
            case VOLATILE:
                return "#FFE11C";
            default:
                return "";
        }
    }

    @Exported
    public TestResultStatus getStatus() {
        return status;
    }
}
