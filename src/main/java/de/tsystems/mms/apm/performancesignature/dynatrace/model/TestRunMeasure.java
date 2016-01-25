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
import de.tsystems.mms.apm.performancesignature.util.PerfSigUtils;
import org.xml.sax.Attributes;

import java.io.Serializable;
import java.math.BigDecimal;

public class TestRunMeasure implements Serializable {
    private static final long serialVersionUID = 1L;
    private double expectedMax, expectedMin, value, violationPercentage;
    private String metricGroup, name, unit;
    private int numDegradedRuns, numFailingOrInvalidatedRuns, numImprovedRuns, numValidRuns;

    public TestRunMeasure(final Attributes attr) {
        this.expectedMax = AttributeUtils.getDoubleAttribute("expectedMax", attr);
        this.expectedMin = AttributeUtils.getDoubleAttribute("expectedMin", attr);
        this.metricGroup = AttributeUtils.getStringAttribute("metricGroup", attr);
        this.name = AttributeUtils.getStringAttribute("name", attr);
        this.numDegradedRuns = AttributeUtils.getIntAttribute("numDegradedRuns", attr);
        this.numFailingOrInvalidatedRuns = AttributeUtils.getIntAttribute("numFailingOrInvalidatedRuns", attr);
        this.numImprovedRuns = AttributeUtils.getIntAttribute("numImprovedRuns", attr);
        this.numValidRuns = AttributeUtils.getIntAttribute("numValidRuns", attr);
        this.unit = AttributeUtils.getStringAttribute("unit", attr);
        this.value = AttributeUtils.getDoubleAttribute("value", attr);
        this.violationPercentage = AttributeUtils.getDoubleAttribute("violationPercentage", attr);
    }

    public BigDecimal getExpectedMax() {
        return PerfSigUtils.round(this.expectedMax, 2);
    }

    public BigDecimal getExpectedMin() {
        return PerfSigUtils.round(this.expectedMin, 2);
    }

    public BigDecimal getValue() {
        return PerfSigUtils.round(this.value, 2);
    }

    public double getViolationPercentage() {
        return violationPercentage;
    }

    public String getMetricGroup() {
        return metricGroup;
    }

    public String getName() {
        return name;
    }

    public String getUnit() {
        return unit;
    }

    public int getNumDegradedRuns() {
        return numDegradedRuns;
    }

    public int getNumFailingOrInvalidatedRuns() {
        return numFailingOrInvalidatedRuns;
    }

    public int getNumImprovedRuns() {
        return numImprovedRuns;
    }

    public int getNumValidRuns() {
        return numValidRuns;
    }
}
