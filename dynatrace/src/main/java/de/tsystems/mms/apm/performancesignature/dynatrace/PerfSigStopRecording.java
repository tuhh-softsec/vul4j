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

import de.tsystems.mms.apm.performancesignature.dynatrace.model.TestRun;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.DTServerConnection;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.xml.RESTErrorException;
import de.tsystems.mms.apm.performancesignature.util.PerfSigUtils;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.ListBoxModel;
import jenkins.tasks.SimpleBuildStep;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.List;

public class PerfSigStopRecording extends Builder implements SimpleBuildStep {
    private final String dynatraceProfile;

    @DataBoundConstructor
    public PerfSigStopRecording(final String dynatraceProfile) {
        this.dynatraceProfile = dynatraceProfile;
    }

    @Override
    public void perform(@Nonnull final Run<?, ?> run, @Nonnull final FilePath workspace, @Nonnull final Launcher launcher, @Nonnull final TaskListener listener)
            throws InterruptedException, IOException {
        DTServerConnection connection = PerfSigUtils.createDTServerConnection(dynatraceProfile);
        final List<PerfSigEnvInvisAction> envVars = run.getActions(PerfSigEnvInvisAction.class);

        PerfSigEnvInvisAction buildEnvVars = null;
        String sessionId = null;
        String testRunId = null;
        Date timeframeStop = new Date();
        PrintStream logger = listener.getLogger();

        logger.println(Messages.PerfSigStopRecording_StoppingSessionRecording());
        if (!envVars.isEmpty()) {
            buildEnvVars = envVars.get(envVars.size() - 1);
            buildEnvVars.setTimeframeStop(timeframeStop);
            sessionId = buildEnvVars.getSessionId();
            testRunId = buildEnvVars.getTestRunId();
        }

        if (testRunId != null) {
            TestRun testRun = connection.finishTestRun(testRunId);
            logger.println("finished test run " + testRun.getId());
        }

        if (buildEnvVars != null && sessionId == null) {
            Date timeframeStart = buildEnvVars.getTimeframeStart();
            logger.println(Messages.PerfSigStopRecording_TimeframeStart(timeframeStart));
            logger.println(Messages.PerfSigStopRecording_TimeframeStop(timeframeStop));
            sessionId = connection.storeSession(buildEnvVars.getSessionName(), timeframeStart, timeframeStop,
                    PerfSigStartRecording.DescriptorImpl.defaultRecordingOption, PerfSigStartRecording.DescriptorImpl.defaultLockSession, false);
            buildEnvVars.setSessionId(sessionId);
        } else {
            sessionId = connection.stopRecording();
        }

        if (StringUtils.isBlank(sessionId)) {
            throw new RESTErrorException(Messages.PerfSigStopRecording_InternalError());
        }
        logger.println(Messages.PerfSigStopRecording_StoppedSessionRecording(connection.getCredProfilePair().getProfile(), sessionId));
    }

    public String getDynatraceProfile() {
        return dynatraceProfile;
    }

    @Symbol("stopSession")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        public ListBoxModel doFillDynatraceProfileItems() {
            return PerfSigUtils.listToListBoxModel(PerfSigUtils.getDTConfigurations());
        }

        public boolean isApplicable(final Class<? extends AbstractProject> aClass) {
            return true;
        }

        public String getDisplayName() {
            return Messages.PerfSigStopRecording_DisplayName();
        }
    }
}
