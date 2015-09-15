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
import org.xml.sax.Attributes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by rapi on 13.04.2015.
 */
public class TestResult implements Serializable {
    private static final long serialVersionUID = 1L;
    private Date exectime;
    private String name, packageName;
    private TestResultStatus status;
    private List<TestRunMeasure> measures;

    public TestResult(final Attributes attr) {
        this.exectime = AttributeUtils.getDateAttribute("exectime", attr);
        this.name = AttributeUtils.getStringAttribute("name", attr);
        this.packageName = AttributeUtils.getStringAttribute("package", attr);
        this.status = TestResultStatus.fromString(AttributeUtils.getStringAttribute("status", attr));
    }

    public Date getExectime() {
        return new Date(exectime.getTime());
    }

    public void setExectime(final Date exectime) {
        this.exectime = new Date(exectime.getTime());
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(final String packageName) {
        this.packageName = packageName;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public TestRunMeasure getMeasure(final String metricGroup, final String metric) {
        for (TestRunMeasure measure : measures) {
            if (measure.getMetricGroup().equals(metricGroup) && measure.getName().equals(metric))
                return measure;
        }
        return null;
    }

    public List<TestRunMeasure> getTestRunMeasures() {
        if (measures != null)
            return measures;
        return new ArrayList<TestRunMeasure>();
    }

    public void addTestRunMeasure(final TestRunMeasure measure) {
        if (measures == null) {
            measures = new ArrayList<TestRunMeasure>();
        }
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
                return "#00FF00";
            case FAILED:
                return "#FF0000";
            case IMPROVED:
                return "#00FF00";
            case DEGRADED:
                return "#FF0000";
            case VOLATILE:
                return "#FFFF00";
            default:
                return "";
        }
    }

    public TestResultStatus getStatus() {
        return status;
    }

    public void setStatus(final TestResultStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "TestResult{" +
                "exectime=" + exectime +
                ", name='" + name + '\'' +
                ", packageName='" + packageName + '\'' +
                ", status=" + status +
                ", measures=" + measures +
                '}';
    }
}
