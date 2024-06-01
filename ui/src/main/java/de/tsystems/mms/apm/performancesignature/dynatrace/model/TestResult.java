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

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.tsystems.mms.apm.performancesignature.util.PerfSigUIUtils;
import io.swagger.annotations.ApiModelProperty;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * TestResult
 */

@ExportedBean
public class TestResult {
    @SerializedName("name")
    private String name;

    @SerializedName("status")
    private StatusEnum status;

    @SerializedName("exectime")
    private Date exectime;

    @SerializedName("package")
    private String _package;

    @Deprecated
    private transient String packageName;

    @SerializedName("platform")
    private String platform;

    @SerializedName("measures")
    private List<TestMeasure> measures;

    /**
     * Get name
     *
     * @return name
     **/
    @Exported
    public String getName() {
        return name;
    }

    /**
     * Get status
     *
     * @return status
     **/
    @Exported
    public StatusEnum getStatus() {
        return status;
    }

    /**
     * Start time of the test in ISO 8601 compatible date/time of format: yyyy-MM-dd&#39;T&#39;HH:mm:ss.SSSXXX
     *
     * @return exectime
     **/
    @Exported
    @ApiModelProperty(example = "2016-07-18T16:44:00.055+02:00", value = "Start time of the test in ISO 8601 compatible date/time of format: yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    public Date getExectime() {
        return exectime;
    }

    /**
     * Get _package
     *
     * @return _package
     **/
    @Exported
    public String getPackage() {
        return _package;
    }

    /**
     * Get platform
     *
     * @return platform
     **/
    @Exported
    public String getPlatform() {
        return platform;
    }

    public TestResult measures(List<TestMeasure> measures) {
        this.measures = measures;
        return this;
    }

    public TestResult addMeasuresItem(TestMeasure measuresItem) {
        if (this.measures == null) {
            this.measures = new ArrayList<>();
        }
        this.measures.add(measuresItem);
        return this;
    }

    public TestMeasure getMeasure(final String metricGroup, final String metric) {
        for (TestMeasure measure : measures) {
            if (measure.getMetricGroup().equals(metricGroup) && measure.getName().equals(metric))
                return measure;
        }
        return null;
    }

    /**
     * Get measures
     *
     * @return measures
     **/
    @Exported
    public List<TestMeasure> getMeasures() {
        return measures;
    }

    public void setMeasures(List<TestMeasure> measures) {
        this.measures = measures;
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

    @SuppressWarnings("deprecation")
    protected Object readResolve() {
        if (packageName != null) {
            _package = packageName;
        }
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class TestResult {\n");

        sb.append("    name: ").append(PerfSigUIUtils.toIndentedString(name)).append("\n");
        sb.append("    status: ").append(PerfSigUIUtils.toIndentedString(status)).append("\n");
        sb.append("    exectime: ").append(PerfSigUIUtils.toIndentedString(exectime)).append("\n");
        sb.append("    _package: ").append(PerfSigUIUtils.toIndentedString(_package)).append("\n");
        sb.append("    platform: ").append(PerfSigUIUtils.toIndentedString(platform)).append("\n");
        sb.append("    measures: ").append(PerfSigUIUtils.toIndentedString(measures)).append("\n");
        sb.append("}");
        return sb.toString();
    }
    /**
     * Gets or Sets category
     */
    @JsonAdapter(TestResult.StatusEnum.Adapter.class)
    public enum StatusEnum {
        FAILED("Failed"),
        VOLATILE("Volatile"),
        DEGRADED("Degraded"),
        IMPROVED("Improved"),
        PASSED("Passed"),
        NONE("None");

        private final String value;

        StatusEnum(String value) {
            this.value = value;
        }

        public static TestResult.StatusEnum fromValue(String text) {
            for (TestResult.StatusEnum b : TestResult.StatusEnum.values()) {
                if (String.valueOf(b.value).equalsIgnoreCase(text)) {
                    return b;
                }
            }
            return null;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static class Adapter extends TypeAdapter<TestResult.StatusEnum> {
            @Override
            public void write(final JsonWriter jsonWriter, final TestResult.StatusEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public TestResult.StatusEnum read(final JsonReader jsonReader) throws IOException {
                String value = jsonReader.nextString();
                return TestResult.StatusEnum.fromValue(String.valueOf(value));
            }
        }
    }
}
