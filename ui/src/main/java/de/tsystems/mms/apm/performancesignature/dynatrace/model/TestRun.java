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
public class TestRun implements Serializable {
    private static final long serialVersionUID = 1L;
    private final List<TestResult> testResults;
    private String category, versionMilestone, versionBuild, versionMajor, versionMinor, versionRevision, testRunID, marker;
    private int numPassed, numFailed, numVolatile, numImproved, numDegraded, numInvalidated;
    private Date timestamp;

    public TestRun(final Object attr) {
        this.category = AttributeUtils.getStringAttribute("category", attr);
        this.versionBuild = AttributeUtils.getStringAttribute("versionBuild", attr);
        this.versionMajor = AttributeUtils.getStringAttribute("versionMajor", attr);
        this.versionMilestone = AttributeUtils.getStringAttribute("versionMilestone", attr);
        this.versionMinor = AttributeUtils.getStringAttribute("versionMinor", attr);
        this.versionRevision = AttributeUtils.getStringAttribute("versionRevision", attr);
        this.timestamp = AttributeUtils.getDateAttribute("startTime", attr);
        this.testRunID = AttributeUtils.getStringAttribute("id", attr);
        this.numPassed = AttributeUtils.getIntAttribute("numPassed", attr);
        this.numFailed = AttributeUtils.getIntAttribute("numFailed", attr);
        this.numVolatile = AttributeUtils.getIntAttribute("numVolatile", attr);
        this.numImproved = AttributeUtils.getIntAttribute("numImproved", attr);
        this.numDegraded = AttributeUtils.getIntAttribute("numDegraded", attr);
        this.numInvalidated = AttributeUtils.getIntAttribute("numInvalidated", attr);
        this.marker = AttributeUtils.getStringAttribute("marker", attr);
        this.testResults = new ArrayList<>();
    }

    public TestRun() {
        testResults = new ArrayList<>();
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
                newTestRun.testResults.addAll(otherTestRun.getTestResults());
            }
        }
        return newTestRun;
    }

    @Exported
    public String getCategory() {
        return category;
    }

    @Exported
    public String getVersionMilestone() {
        return versionMilestone;
    }

    @Exported
    public String getVersionBuild() {
        return versionBuild;
    }

    @Exported
    public String getVersionMajor() {
        return versionMajor;
    }

    @Exported
    public String getVersionMinor() {
        return versionMinor;
    }

    @Exported
    public String getVersionRevision() {
        return versionRevision;
    }

    @Exported
    public String getTestRunID() {
        return testRunID;
    }

    @Exported
    public String getMarker() {
        return marker;
    }

    @Exported
    public int getNumPassed() {
        return numPassed;
    }

    @Exported
    public int getNumFailed() {
        return numFailed;
    }

    @Exported
    public int getNumVolatile() {
        return numVolatile;
    }

    @Exported
    public int getNumImproved() {
        return numImproved;
    }

    @Exported
    public int getNumDegraded() {
        return numDegraded;
    }

    @Exported
    public int getNumInvalidated() {
        return numInvalidated;
    }

    @Exported
    public Date getTimestamp() {
        return (Date) timestamp.clone();
    }

    @Exported
    public List<TestResult> getTestResults() {
        return testResults;
    }

    public void addTestResults(final TestResult testResult) {
        testResults.add(testResult);
    }
}
