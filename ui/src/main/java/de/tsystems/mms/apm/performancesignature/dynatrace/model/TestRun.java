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
 * TestRun
 */

@ExportedBean
public class TestRun {
    @SerializedName("id")
    private String id;
    @SerializedName("category")
    private CategoryEnum category;
    @SerializedName("versionBuild")
    private String versionBuild;
    @SerializedName("versionMajor")
    private String versionMajor;
    @SerializedName("versionMilestone")
    private String versionMilestone;
    @SerializedName("versionMinor")
    private String versionMinor;
    @SerializedName("versionRevision")
    private String versionRevision;
    @SerializedName("platform")
    private String platform;
    @SerializedName("startTime")
    private Date startTime;
    @SerializedName("sessionId")
    private String sessionId;
    @SerializedName("session")
    private String session;
    @SerializedName("systemProfile")
    private String systemProfile;
    @SerializedName("marker")
    private String marker;
    @SerializedName("message")
    private String message;
    @SerializedName("href")
    private String href;
    @SerializedName("creationMode")
    private CreationModeEnum creationMode;
    @SerializedName("finished")
    private boolean finished;
    @SerializedName("numDegraded")
    private int numDegraded;
    @SerializedName("numFailed")
    private int numFailed;
    @SerializedName("numImproved")
    private int numImproved;
    @SerializedName("numInvalidated")
    private int numInvalidated;
    @SerializedName("numPassed")
    private int numPassed;
    @SerializedName("numVolatile")
    private int numVolatile;
    @SerializedName("testResults")
    private List<TestResult> testResults;

    @Deprecated
    private transient Date timestamp;

    /**
     * Get id
     *
     * @return id
     **/
    @Exported
    public String getId() {
        return id;
    }

    /**
     * Get category
     *
     * @return category
     **/
    @Exported
    @ApiModelProperty(example = "unit")
    public CategoryEnum getCategory() {
        return category;
    }

    /**
     * Get versionBuild
     *
     * @return versionBuild
     **/
    @Exported
    public String getVersionBuild() {
        return versionBuild;
    }

    /**
     * Get versionMajor
     *
     * @return versionMajor
     **/
    @Exported
    public String getVersionMajor() {
        return versionMajor;
    }

    /**
     * Get versionMilestone
     *
     * @return versionMilestone
     **/
    @Exported
    public String getVersionMilestone() {
        return versionMilestone;
    }

    /**
     * Get versionMinor
     *
     * @return versionMinor
     **/
    @Exported
    public String getVersionMinor() {
        return versionMinor;
    }

    /**
     * Get versionRevision
     *
     * @return versionRevision
     **/
    @Exported
    public String getVersionRevision() {
        return versionRevision;
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

    /**
     * Test run start time in ISO 8601 compatible date/time of format: yyyy-MM-dd&#39;T&#39;HH:mm:ss.SSSXXX
     *
     * @return startTime
     **/
    @Exported
    @ApiModelProperty(example = "2016-05-11T11:35:31.170+02:00", value = "Test run start time in ISO 8601 compatible date/time of format: yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    public Date getStartTime() {
        return startTime;
    }

    /**
     * Get sessionId
     *
     * @return sessionId
     **/
    @Exported
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Get session
     *
     * @return session
     **/
    @Exported
    public String getSession() {
        return session;
    }

    /**
     * Get systemProfile
     *
     * @return systemProfile
     **/
    @Exported
    public String getSystemProfile() {
        return systemProfile;
    }

    /**
     * Get marker
     *
     * @return marker
     **/
    @Exported
    public String getMarker() {
        return marker;
    }

    /**
     * Get message
     *
     * @return message
     **/
    @Exported
    public String getMessage() {
        return message;
    }

    /**
     * Base URL of the REST resource. Further information can be retrieved from this URL or its subresources
     *
     * @return href
     **/
    @Exported
    @ApiModelProperty(value = "Base URL of the REST resource. Further information can be retrieved from this URL or its subresources")
    public String getHref() {
        return href;
    }

    /**
     * Get creationMode
     *
     * @return creationMode
     **/
    @Exported
    @ApiModelProperty(example = "MANUAL")
    public CreationModeEnum getCreationMode() {
        return creationMode;
    }

    public static TestRun mergeTestRuns(final List<TestRun> testRuns) {
        TestRun newTestRun = new TestRun();
        if (testRuns != null && !testRuns.isEmpty()) {
            for (TestRun otherTestRun : testRuns) {
                newTestRun.numDegraded += otherTestRun.numDegraded;
                newTestRun.numFailed += otherTestRun.numFailed;
                newTestRun.numImproved += otherTestRun.numImproved;
                newTestRun.numInvalidated += otherTestRun.numInvalidated;
                newTestRun.numPassed += otherTestRun.numPassed;
                newTestRun.numVolatile += otherTestRun.numVolatile;
                newTestRun.getTestResults().addAll(otherTestRun.getTestResults());
            }
        }
        return newTestRun;
    }

    /**
     * Get numDegraded
     *
     * @return numDegraded
     **/
    @Exported
    public int getNumDegraded() {
        return numDegraded;
    }

    /**
     * Get numFailed
     *
     * @return numFailed
     **/
    @Exported
    public int getNumFailed() {
        return numFailed;
    }

    /**
     * Get numImproved
     *
     * @return numImproved
     **/
    @Exported
    public int getNumImproved() {
        return numImproved;
    }

    /**
     * Get numInvalidated
     *
     * @return numInvalidated
     **/
    @Exported
    public int getNumInvalidated() {
        return numInvalidated;
    }

    /**
     * Get numPassed
     *
     * @return numPassed
     **/
    @Exported
    public int getNumPassed() {
        return numPassed;
    }

    /**
     * Get numVolatile
     *
     * @return numVolatile
     **/
    @Exported
    public int getNumVolatile() {
        return numVolatile;
    }

    public TestRun testResults(List<TestResult> testResults) {
        this.testResults = testResults;
        return this;
    }

    public TestRun addTestResultsItem(TestResult testResultsItem) {
        if (this.testResults == null) {
            this.testResults = new ArrayList<>();
        }
        this.testResults.add(testResultsItem);
        return this;
    }

    /**
     * Get finished
     *
     * @return finished
     **/
    @Exported
    public boolean getFinished() {
        return finished;
    }

    public void setTestResults(List<TestResult> testResults) {
        this.testResults = testResults;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class TestRun {\n");

        sb.append("    id: ").append(PerfSigUIUtils.toIndentedString(id)).append("\n");
        sb.append("    category: ").append(PerfSigUIUtils.toIndentedString(category)).append("\n");
        sb.append("    versionBuild: ").append(PerfSigUIUtils.toIndentedString(versionBuild)).append("\n");
        sb.append("    versionMajor: ").append(PerfSigUIUtils.toIndentedString(versionMajor)).append("\n");
        sb.append("    versionMilestone: ").append(PerfSigUIUtils.toIndentedString(versionMilestone)).append("\n");
        sb.append("    versionMinor: ").append(PerfSigUIUtils.toIndentedString(versionMinor)).append("\n");
        sb.append("    versionRevision: ").append(PerfSigUIUtils.toIndentedString(versionRevision)).append("\n");
        sb.append("    platform: ").append(PerfSigUIUtils.toIndentedString(platform)).append("\n");
        sb.append("    startTime: ").append(PerfSigUIUtils.toIndentedString(startTime)).append("\n");
        sb.append("    sessionId: ").append(PerfSigUIUtils.toIndentedString(sessionId)).append("\n");
        sb.append("    session: ").append(PerfSigUIUtils.toIndentedString(session)).append("\n");
        sb.append("    systemProfile: ").append(PerfSigUIUtils.toIndentedString(systemProfile)).append("\n");
        sb.append("    marker: ").append(PerfSigUIUtils.toIndentedString(marker)).append("\n");
        sb.append("    message: ").append(PerfSigUIUtils.toIndentedString(message)).append("\n");
        sb.append("    href: ").append(PerfSigUIUtils.toIndentedString(href)).append("\n");
        sb.append("    creationMode: ").append(PerfSigUIUtils.toIndentedString(creationMode)).append("\n");
        sb.append("    numDegraded: ").append(PerfSigUIUtils.toIndentedString(numDegraded)).append("\n");
        sb.append("    numFailed: ").append(PerfSigUIUtils.toIndentedString(numFailed)).append("\n");
        sb.append("    numImproved: ").append(PerfSigUIUtils.toIndentedString(numImproved)).append("\n");
        sb.append("    numInvalidated: ").append(PerfSigUIUtils.toIndentedString(numInvalidated)).append("\n");
        sb.append("    numPassed: ").append(PerfSigUIUtils.toIndentedString(numPassed)).append("\n");
        sb.append("    numVolatile: ").append(PerfSigUIUtils.toIndentedString(numVolatile)).append("\n");
        sb.append("    finished: ").append(PerfSigUIUtils.toIndentedString(finished)).append("\n");
        sb.append("    testResults: ").append(PerfSigUIUtils.toIndentedString(testResults)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Get testResults
     *
     * @return testResults
     **/
    @Exported
    public List<TestResult> getTestResults() {
        if (testResults == null) {
            testResults = new ArrayList<>();
        }
        return testResults;
    }

    @SuppressWarnings("deprecation")
    protected Object readResolve() {
        if (timestamp != null) {
            startTime = timestamp;
        }
        return this;
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

    /**
     * Gets or Sets creationMode
     */
    @JsonAdapter(CreationModeEnum.Adapter.class)
    public enum CreationModeEnum {
        MANUAL("MANUAL"),

        AUTO("AUTO");

        private final String value;

        CreationModeEnum(String value) {
            this.value = value;
        }

        public static CreationModeEnum fromValue(String text) {
            for (CreationModeEnum b : CreationModeEnum.values()) {
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

        public static class Adapter extends TypeAdapter<CreationModeEnum> {
            @Override
            public void write(final JsonWriter jsonWriter, final CreationModeEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public CreationModeEnum read(final JsonReader jsonReader) throws IOException {
                String value = jsonReader.nextString();
                return CreationModeEnum.fromValue(String.valueOf(value));
            }
        }
    }
}
