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

import de.tsystems.mms.apm.performancesignature.dynatrace.model.TestRun;
import hudson.model.Run;
import hudson.tasks.junit.CaseResult;
import hudson.tasks.junit.TestAction;
import hudson.tasks.junit.TestObject;
import hudson.tasks.junit.TestResultAction;

import java.util.Collections;
import java.util.List;

/**
 * Created by rapi on 27.05.2015.
 */
public class DTPerfSigTestData extends TestResultAction.Data {
    private final Run<?, ?> run;
    private final List<TestRun> testRuns;

    public DTPerfSigTestData(final Run<?, ?> run, final List<TestRun> testRuns) {
        this.run = run;
        this.testRuns = testRuns;
    }

    public List<TestRun> getTestRuns() {
        return testRuns == null ? Collections.<TestRun>emptyList() : testRuns;
    }

    public DTPerfSigTestData getPreviousData() {
        DTPerfSigTestData previousData = null;
        Run previousRun = run.getPreviousNotFailedBuild();
        if (previousRun != null) {
            DTPerfSigTestDataWrapper wrapper = previousRun.getAction(DTPerfSigTestDataWrapper.class);
            if (wrapper != null) {
                previousData = wrapper.getData();
            }
        }
        return previousData;
    }

    public Run<?, ?> getRun() {
        return run;
    }

    @Override
    public List<? extends TestAction> getTestAction(final TestObject testObject) {
        if (testObject instanceof CaseResult) {
            CaseResult caseResult = (CaseResult) testObject;
            String packageName = caseResult.getPackageName();
            String fullName = caseResult.getSimpleName() + "." + caseResult.getSearchName();
            return Collections.singletonList(new DTPerfSigTestAction(this, packageName, fullName));
        } else {
            return Collections.emptyList();
        }
    }
}
