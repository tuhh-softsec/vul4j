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
public class TestRun implements Serializable {
    private static final long serialVersionUID = 1L;
    private String category, versionMilestone, versionBuild, versionMajor, versionMinor, versionRevision;
    private String testRunID, marker;
    private int numPassed, numFailed, numVolatile, numImproved, numDegraded, numInvalidated;
    private Date timestamp;

    private List<TestResult> testResults;

    public TestRun(final Attributes attr) {
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
    }

    public TestRun() {
        testResults = new ArrayList<TestResult>();
    }

    public static TestRun mergeTestRuns(final List<TestRun> testRuns) {
        TestRun newTestRun = new TestRun();
        if (testRuns != null && !testRuns.isEmpty()) {
            for (int i = 0; i < testRuns.size(); i++) {
                TestRun otherTestRun = testRuns.get(i);
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

    public String getCategory() {
        return category;
    }

    public String getVersionMilestone() {
        return versionMilestone;
    }

    public String getVersionBuild() {
        return versionBuild;
    }

    public String getVersionMajor() {
        return versionMajor;
    }

    public String getVersionMinor() {
        return versionMinor;
    }

    public String getVersionRevision() {
        return versionRevision;
    }

    public String getTestRunID() {
        return testRunID;
    }

    public String getMarker() {
        return marker;
    }

    public int getNumPassed() {
        return numPassed;
    }

    public int getNumFailed() {
        return numFailed;
    }

    public int getNumVolatile() {
        return numVolatile;
    }

    public int getNumImproved() {
        return numImproved;
    }

    public int getNumDegraded() {
        return numDegraded;
    }

    public int getNumInvalidated() {
        return numInvalidated;
    }

    public Date getTimestamp() {
        return new Date(this.timestamp.getTime());
    }

    public List<TestResult> getTestResults() {
        if (testResults != null)
            return testResults;
        return new ArrayList<TestResult>();
    }

    public void addTestResults(final TestResult testResult) {
        if (testResults == null) {
            testResults = new ArrayList<TestResult>();
        }
        testResults.add(testResult);
    }

    @Override
    public String toString() {
        return "TestRun{" +
                "category='" + category + '\'' +
                ", versionMilestone='" + versionMilestone + '\'' +
                ", versionBuild='" + versionBuild + '\'' +
                ", versionMajor='" + versionMajor + '\'' +
                ", versionMinor='" + versionMinor + '\'' +
                ", versionRevision='" + versionRevision + '\'' +
                ", testRunID='" + testRunID + '\'' +
                ", marker='" + marker + '\'' +
                ", numPassed=" + numPassed +
                ", numFailed=" + numFailed +
                ", numVolatile=" + numVolatile +
                ", numImproved=" + numImproved +
                ", numDegraded=" + numDegraded +
                ", numInvalidated=" + numInvalidated +
                ", timestamp=" + timestamp +
                ", testResults=" + testResults +
                '}';
    }
}
