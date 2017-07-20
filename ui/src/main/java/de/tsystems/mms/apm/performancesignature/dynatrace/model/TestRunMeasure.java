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

@ExportedBean
public class TestRunMeasure implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String metricGroup, name, unit;
    private final int numDegradedRuns, numFailingOrInvalidatedRuns, numImprovedRuns, numValidRuns;
    private final double expectedMax, expectedMin, value, violationPercentage;

    public TestRunMeasure(final Object attr) {
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

    @Exported
    public double getExpectedMax() {
        return this.expectedMax;
    }

    @Exported
    public double getExpectedMin() {
        return this.expectedMin;
    }

    @Exported
    public double getValue() {
        return this.value;
    }

    @Exported
    public double getViolationPercentage() {
        return violationPercentage;
    }

    @Exported
    public String getMetricGroup() {
        return metricGroup;
    }

    @Exported
    public String getName() {
        return name;
    }

    @Exported
    public String getUnit() {
        return unit;
    }

    @Exported
    public int getNumDegradedRuns() {
        return numDegradedRuns;
    }

    @Exported
    public int getNumFailingOrInvalidatedRuns() {
        return numFailingOrInvalidatedRuns;
    }

    @Exported
    public int getNumImprovedRuns() {
        return numImprovedRuns;
    }

    @Exported
    public int getNumValidRuns() {
        return numValidRuns;
    }
}
