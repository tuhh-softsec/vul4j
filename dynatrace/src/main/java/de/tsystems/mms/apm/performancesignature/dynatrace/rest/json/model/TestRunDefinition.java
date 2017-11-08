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

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.tsystems.mms.apm.performancesignature.util.PerfSigUIUtils;
import io.swagger.annotations.ApiModelProperty;

import java.io.IOException;

/**
 * TestRunDefinition
 */

public class TestRunDefinition {
    @SerializedName("versionBuild")
    private final String versionBuild;
    @SerializedName("versionMajor")
    private final String versionMajor = null;
    @SerializedName("versionMilestone")
    private final String versionMilestone = null;
    @SerializedName("versionMinor")
    private final String versionMinor = null;
    @SerializedName("versionRevision")
    private final String versionRevision = null;
    @SerializedName("marker")
    private final String marker = null;
    @SerializedName("platform")
    private final String platform = null;
    @SerializedName("category")
    private final CategoryEnum category;

    public TestRunDefinition(int versionBuild, String performance) {
        this.versionBuild = String.valueOf(versionBuild);
        this.category = CategoryEnum.fromValue(performance);
    }

    /**
     * Get versionBuild
     *
     * @return versionBuild
     **/
    @ApiModelProperty()
    public String getVersionBuild() {
        return versionBuild;
    }

    /**
     * Get versionMajor
     *
     * @return versionMajor
     **/
    @ApiModelProperty()
    public String getVersionMajor() {
        return versionMajor;
    }

    /**
     * Get versionMilestone
     *
     * @return versionMilestone
     **/
    @ApiModelProperty()
    public String getVersionMilestone() {
        return versionMilestone;
    }

    /**
     * Get versionMinor
     *
     * @return versionMinor
     **/
    @ApiModelProperty()
    public String getVersionMinor() {
        return versionMinor;
    }

    /**
     * Get versionRevision
     *
     * @return versionRevision
     **/
    @ApiModelProperty()
    public String getVersionRevision() {
        return versionRevision;
    }

    /**
     * Get marker
     *
     * @return marker
     **/
    @ApiModelProperty()
    public String getMarker() {
        return marker;
    }

    /**
     * Get platform
     *
     * @return platform
     **/
    @ApiModelProperty()
    public String getPlatform() {
        return platform;
    }

    /**
     * Get category
     *
     * @return category
     **/
    @ApiModelProperty(example = "unit")
    public CategoryEnum getCategory() {
        return category;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class TestRunDefinition {\n");

        sb.append("    versionBuild: ").append(PerfSigUIUtils.toIndentedString(versionBuild)).append("\n");
        sb.append("    versionMajor: ").append(PerfSigUIUtils.toIndentedString(versionMajor)).append("\n");
        sb.append("    versionMilestone: ").append(PerfSigUIUtils.toIndentedString(versionMilestone)).append("\n");
        sb.append("    versionMinor: ").append(PerfSigUIUtils.toIndentedString(versionMinor)).append("\n");
        sb.append("    versionRevision: ").append(PerfSigUIUtils.toIndentedString(versionRevision)).append("\n");
        sb.append("    marker: ").append(PerfSigUIUtils.toIndentedString(marker)).append("\n");
        sb.append("    platform: ").append(PerfSigUIUtils.toIndentedString(platform)).append("\n");
        sb.append("    category: ").append(PerfSigUIUtils.toIndentedString(category)).append("\n");
        sb.append("}");
        return sb.toString();
    }
    /**
     * Gets or Sets category
     */
    @JsonAdapter(CategoryEnum.Adapter.class)
    public enum CategoryEnum {
        UNIT("unit"),

        UIDRIVEN("uidriven"),

        PERFORMANCE("performance"),

        WEBAPI("webapi"),

        EXTERNAL("external");

        private final String value;

        CategoryEnum(String value) {
            this.value = value;
        }

        public static CategoryEnum fromValue(String text) {
            for (CategoryEnum b : CategoryEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
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

        public static class Adapter extends TypeAdapter<CategoryEnum> {
            @Override
            public void write(final JsonWriter jsonWriter, final CategoryEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public CategoryEnum read(final JsonReader jsonReader) throws IOException {
                String value = jsonReader.nextString();
                return CategoryEnum.fromValue(String.valueOf(value));
            }
        }
    }
}
