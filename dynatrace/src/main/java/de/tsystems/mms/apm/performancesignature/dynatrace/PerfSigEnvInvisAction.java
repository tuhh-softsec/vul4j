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

package de.tsystems.mms.apm.performancesignature.dynatrace;

import hudson.model.InvisibleAction;

import java.util.Date;

public class PerfSigEnvInvisAction extends InvisibleAction {
    @Deprecated
    private transient String testRunID;
    private String testRunId;
    private final String testCase;
    private final Date timeframeStart;
    private final String sessionName;
    private String sessionId;
    private Date timeframeStop;

    PerfSigEnvInvisAction(final String sessionId, final Date timeframeStart, final String testCase, final String testRunId, final String sessionName) {
        this.sessionId = sessionId;
        this.timeframeStart = timeframeStart != null ? (Date) timeframeStart.clone() : null;
        this.testCase = testCase;
        this.testRunId = testRunId;
        this.sessionName = sessionName;
    }

    public String getSessionName() {
        return sessionName;
    }

    public String getSessionId() {
        return sessionId;
    }

    void setSessionId(final String sessionId) {
        this.sessionId = sessionId;
    }

    public String getTestRunId() {
        return testRunId;
    }

    public String getTestCase() {
        return testCase;
    }

    public Date getTimeframeStart() {
        return timeframeStart != null ? (Date) timeframeStart.clone() : null;
    }

    public Date getTimeframeStop() {
        return timeframeStop != null ? (Date) timeframeStop.clone() : null;
    }

    void setTimeframeStop(Date timeframeStop) {
        this.timeframeStop = timeframeStop;
    }

    protected Object readResolve() {
        if (testRunID != null) {
            testRunId = testRunID;
        }
        return this;
    }
}
