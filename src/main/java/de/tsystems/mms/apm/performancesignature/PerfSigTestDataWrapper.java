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
import de.tsystems.mms.apm.performancesignature.model.PerfSigTestData;
import hudson.model.InvisibleAction;

import java.util.List;

/**
 * Created by rapi on 09.06.2015.
 */
public class PerfSigTestDataWrapper extends InvisibleAction {
    private final PerfSigTestData data;

    public PerfSigTestDataWrapper(final PerfSigTestData data) {
        this.data = data;
    }

    public List<TestRun> getTestRuns() {
        return data.getTestRuns();
    }

    public PerfSigTestData getData() {
        return data;
    }
}
