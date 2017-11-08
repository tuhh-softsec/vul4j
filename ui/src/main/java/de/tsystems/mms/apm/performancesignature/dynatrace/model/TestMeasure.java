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

import com.google.gson.annotations.SerializedName;
import de.tsystems.mms.apm.performancesignature.util.PerfSigUIUtils;
import io.swagger.annotations.ApiModelProperty;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.util.Objects;

/**
 * TestMeasure
 */

@ExportedBean
public class TestMeasure {
    @SerializedName("name")
    private final String name = null;

    @SerializedName("metricGroup")
    private final String metricGroup = null;

    @SerializedName("value")
    private final double value = 0;

    @SerializedName("unit")
    private final String unit = null;

    @SerializedName("expectedMin")
    private final double expectedMin = 0;

    @SerializedName("expectedMax")
    private final double expectedMax = 0;

    @SerializedName("numFailingOrInvalidatedRuns")
    private final int numFailingOrInvalidatedRuns = 0;

    @SerializedName("numValidRuns")
    private final int numValidRuns = 0;

    @SerializedName("numImprovedRuns")
    private final int numImprovedRuns = 0;

    @SerializedName("numDegradedRuns")
    private final int numDegradedRuns = 0;

    @SerializedName("violationPercentage")
    private final double violationPercentage = 0;

    /**
     * Get name
     *
     * @return name
     **/
    @Exported
    @ApiModelProperty()
    public String getName() {
        return name;
    }

    /**
     * Get metricGroup
     *
     * @return metricGroup
     **/
    @Exported
    @ApiModelProperty()
    public String getMetricGroup() {
        return metricGroup;
    }

    /**
     * Get value
     *
     * @return value
     **/
    @Exported
    @ApiModelProperty()
    public double getValue() {
        return value;
    }

    /**
     * Get unit
     *
     * @return unit
     **/
    @Exported
    @ApiModelProperty()
    public String getUnit() {
        return unit;
    }

    /**
     * Get expectedMin
     *
     * @return expectedMin
     **/
    @Exported
    @ApiModelProperty()
    public double getExpectedMin() {
        return expectedMin;
    }

    /**
     * Get expectedMax
     *
     * @return expectedMax
     **/
    @Exported
    @ApiModelProperty()
    public double getExpectedMax() {
        return expectedMax;
    }

    /**
     * Get numFailingOrInvalidatedRuns
     *
     * @return numFailingOrInvalidatedRuns
     **/
    @Exported
    @ApiModelProperty()
    public int getNumFailingOrInvalidatedRuns() {
        return numFailingOrInvalidatedRuns;
    }

    /**
     * Get numValidRuns
     *
     * @return numValidRuns
     **/
    @Exported
    @ApiModelProperty()
    public int getNumValidRuns() {
        return numValidRuns;
    }

    /**
     * Get numImprovedRuns
     *
     * @return numImprovedRuns
     **/
    @Exported
    @ApiModelProperty()
    public int getNumImprovedRuns() {
        return numImprovedRuns;
    }

    /**
     * Get numDegradedRuns
     *
     * @return numDegradedRuns
     **/
    @Exported
    @ApiModelProperty()
    public int getNumDegradedRuns() {
        return numDegradedRuns;
    }

    /**
     * Get violationPercentage
     *
     * @return violationPercentage
     **/
    @Exported
    @ApiModelProperty()
    public double getViolationPercentage() {
        return violationPercentage;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TestMeasure testMeasure = (TestMeasure) o;
        return Objects.equals(this.name, testMeasure.name) &&
                Objects.equals(this.metricGroup, testMeasure.metricGroup) &&
                Objects.equals(this.value, testMeasure.value) &&
                Objects.equals(this.unit, testMeasure.unit) &&
                Objects.equals(this.expectedMin, testMeasure.expectedMin) &&
                Objects.equals(this.expectedMax, testMeasure.expectedMax) &&
                Objects.equals(this.numFailingOrInvalidatedRuns, testMeasure.numFailingOrInvalidatedRuns) &&
                Objects.equals(this.numValidRuns, testMeasure.numValidRuns) &&
                Objects.equals(this.numImprovedRuns, testMeasure.numImprovedRuns) &&
                Objects.equals(this.numDegradedRuns, testMeasure.numDegradedRuns) &&
                Objects.equals(this.violationPercentage, testMeasure.violationPercentage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, metricGroup, value, unit, expectedMin, expectedMax, numFailingOrInvalidatedRuns, numValidRuns, numImprovedRuns, numDegradedRuns, violationPercentage);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class TestMeasure {\n");

        sb.append("    name: ").append(PerfSigUIUtils.toIndentedString(name)).append("\n");
        sb.append("    metricGroup: ").append(PerfSigUIUtils.toIndentedString(metricGroup)).append("\n");
        sb.append("    value: ").append(PerfSigUIUtils.toIndentedString(value)).append("\n");
        sb.append("    unit: ").append(PerfSigUIUtils.toIndentedString(unit)).append("\n");
        sb.append("    expectedMin: ").append(PerfSigUIUtils.toIndentedString(expectedMin)).append("\n");
        sb.append("    expectedMax: ").append(PerfSigUIUtils.toIndentedString(expectedMax)).append("\n");
        sb.append("    numFailingOrInvalidatedRuns: ").append(PerfSigUIUtils.toIndentedString(numFailingOrInvalidatedRuns)).append("\n");
        sb.append("    numValidRuns: ").append(PerfSigUIUtils.toIndentedString(numValidRuns)).append("\n");
        sb.append("    numImprovedRuns: ").append(PerfSigUIUtils.toIndentedString(numImprovedRuns)).append("\n");
        sb.append("    numDegradedRuns: ").append(PerfSigUIUtils.toIndentedString(numDegradedRuns)).append("\n");
        sb.append("    violationPercentage: ").append(PerfSigUIUtils.toIndentedString(violationPercentage)).append("\n");
        sb.append("}");
        return sb.toString();
    }
}
