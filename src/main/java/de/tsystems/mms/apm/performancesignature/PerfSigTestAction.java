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

package de.tsystems.mms.apm.performancesignature;

import de.tsystems.mms.apm.performancesignature.dynatrace.model.TestResult;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.TestRun;
import de.tsystems.mms.apm.performancesignature.model.PerfSigTestData;
import hudson.tasks.junit.TestAction;

import java.util.Date;

/**
 * Created by rapi on 27.05.2015.
 */

public class PerfSigTestAction extends TestAction {
    private static long timestamp;
    private final PerfSigTestData testData;
    private TestRun matchingTestRun;
    private TestResult matchingTestResult;

    public PerfSigTestAction(final PerfSigTestData testData, final String packageName, final String fullName) {
        this.testData = testData;

        for (TestRun testRun : testData.getTestRuns()) {
            for (TestResult testResult : testRun.getTestResults()) {
                if (testResult.getPackageName().equalsIgnoreCase(packageName) && testResult.getName().equalsIgnoreCase(fullName)) {
                    this.matchingTestRun = testRun;
                    this.matchingTestResult = testResult;
                }
            }
        }
    }

    public static boolean getResourcesLoaded() {
        long tmp = new Date().getTime();
        if (timestamp + 2000 < tmp) {
            timestamp = tmp;
            return false;
        }
        return true;
    }

    public TestResult getPreviousTestResult() {
        PerfSigTestData previousData = testData.getPreviousData();
        if (previousData != null) {
            for (TestRun testRun : previousData.getTestRuns()) {
                for (TestResult testResult : testRun.getTestResults()) {
                    if (testResult.getPackageName().equals(matchingTestResult.getPackageName()) && testResult.getName().equals(matchingTestResult.getName())) {
                        return testResult;
                    }
                }
            }
        }
        return null;
    }

    public PerfSigTestData getTestData() {
        return testData;
    }

    public TestRun getMatchingTestRun() {
        return matchingTestRun;
    }

    public TestResult getMatchingTestResult() {
        return matchingTestResult;
    }

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public String getUrlName() {
        return null;
    }
}
