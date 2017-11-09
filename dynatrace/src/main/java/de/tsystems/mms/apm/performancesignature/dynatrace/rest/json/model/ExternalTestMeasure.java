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

package de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model;

import com.google.gson.annotations.SerializedName;
import de.tsystems.mms.apm.performancesignature.util.PerfSigUIUtils;
import io.swagger.annotations.ApiModelProperty;

/**
 * ExternalTestMeasure
 */

public class ExternalTestMeasure {
    @SerializedName("name")
    private final String name = null;

    @SerializedName("value")
    private final Double value = null;

    @SerializedName("timestamp")
    private final String timestamp = null;

    @SerializedName("unit")
    private final String unit = null;

    @SerializedName("minValue")
    private final Double minValue = null;

    @SerializedName("maxValue")
    private final Double maxValue = null;

    @SerializedName("color")
    private final String color = null;

    /**
     * Get name
     *
     * @return name
     **/
    @ApiModelProperty(required = true)
    public String getName() {
        return name;
    }

    /**
     * Get value
     *
     * @return value
     **/
    @ApiModelProperty(required = true)
    public Double getValue() {
        return value;
    }

    /**
     * Timestamp in ISO 8601 compatible date/time of format: yyyy-MM-dd&#39;T&#39;HH:mm:ss.SSSXXX
     *
     * @return timestamp
     **/
    @ApiModelProperty(example = "2016-05-11T11:35:31.170+02:00", value = "Timestamp in ISO 8601 compatible date/time of format: yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Get unit
     *
     * @return unit
     **/

    public String getUnit() {
        return unit;
    }

    /**
     * Get minValue
     *
     * @return minValue
     **/

    public Double getMinValue() {
        return minValue;
    }

    /**
     * Get maxValue
     *
     * @return maxValue
     **/

    public Double getMaxValue() {
        return maxValue;
    }

    /**
     * Get color
     *
     * @return color
     **/
    @ApiModelProperty(example = "#FF0000")
    public String getColor() {
        return color;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ExternalTestMeasure {\n");

        sb.append("    name: ").append(PerfSigUIUtils.toIndentedString(name)).append("\n");
        sb.append("    value: ").append(PerfSigUIUtils.toIndentedString(value)).append("\n");
        sb.append("    timestamp: ").append(PerfSigUIUtils.toIndentedString(timestamp)).append("\n");
        sb.append("    unit: ").append(PerfSigUIUtils.toIndentedString(unit)).append("\n");
        sb.append("    minValue: ").append(PerfSigUIUtils.toIndentedString(minValue)).append("\n");
        sb.append("    maxValue: ").append(PerfSigUIUtils.toIndentedString(maxValue)).append("\n");
        sb.append("    color: ").append(PerfSigUIUtils.toIndentedString(color)).append("\n");
        sb.append("}");
        return sb.toString();
    }
}
