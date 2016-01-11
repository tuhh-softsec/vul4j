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

import de.tsystems.mms.apm.performancesignature.dynatrace.rest.DTServerConnection;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.RESTErrorException;
import de.tsystems.mms.apm.performancesignature.model.ConfigurationTestCase;
import de.tsystems.mms.apm.performancesignature.model.GeneralTestCase;
import de.tsystems.mms.apm.performancesignature.model.UnitTestCase;
import de.tsystems.mms.apm.performancesignature.util.PerfSigUtils;
import hudson.AbortException;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Failure;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import jenkins.tasks.SimpleBuildStep;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.PrintStream;

public class PerfSigStartRecording extends Builder implements SimpleBuildStep {
    private final String testCase;
    private String recordingOption;
    private boolean lockSession;

    @DataBoundConstructor
    public PerfSigStartRecording(final String testCase) {
        this.testCase = StringUtils.deleteWhitespace(testCase);
    }

    @Deprecated
    public PerfSigStartRecording(final String testCase, final String recordingOption, final boolean lockSession) {
        this(testCase);
        setRecordingOption(recordingOption);
        setLockSession(lockSession);
    }

    @Override
    public void perform(@Nonnull final Run<?, ?> run, @Nonnull final FilePath workspace, @Nonnull final Launcher launcher, @Nonnull final TaskListener listener)
            throws InterruptedException, IOException {
        final PrintStream logger = listener.getLogger();
        final PerfSigRecorder dtRecorder = PerfSigUtils.getRecorder(run);

        if (dtRecorder == null) {
            throw new AbortException(Messages.PerfSigStartRecording_MissingConfiguration());
        }

        logger.println("starting session recording ...");
        final DTServerConnection connection = new DTServerConnection(dtRecorder.getProtocol(), dtRecorder.getHost(), dtRecorder.getPort(),
                dtRecorder.getCredentialsId(), dtRecorder.isVerifyCertificate(), dtRecorder.getCustomProxy());
        if (!connection.validateConnection()) {
            throw new RESTErrorException(Messages.PerfSigRecorder_DTConnectionError());
        }

        String testRunId = null;
        for (ConfigurationTestCase tc : dtRecorder.getConfigurationTestCases()) {
            if (tc.getName().equals(this.testCase) && tc instanceof UnitTestCase) {
                logger.println("registering new TestRun");

                testRunId = connection.registerTestRun(dtRecorder.getProfile(), run.getNumber());
                if (testRunId != null) {
                    logger.println(String.format(Messages.PerfSigStartRecording_StartedTestRun(), dtRecorder.getProfile(), testRunId));
                    logger.println("Dynatrace: registered test run " + testRunId + "" +
                            " (available in the environment as " + PerfSigRegisterEnvVars.TESTRUN_ID_KEY +
                            " and " + PerfSigRegisterEnvVars.SESSIONCOUNT + ")");
                } else {
                    logger.println("Warning: Could not register TestRun");
                }
                break;
            }
        }

        final String testCase = run.getEnvironment(listener).expand(this.testCase);
        String sessionName = dtRecorder.getProfile() + "_" + run.getParent().getName() + "_Build-" + run.getNumber() + "_" + testCase;
        sessionName = sessionName.replace("/", "_");
        try {
            final String result = connection.startRecording(dtRecorder.getProfile(), sessionName, "This Session is triggered by Jenkins", getRecordingOption(), lockSession, false);

            if (result != null && result.equals(sessionName)) {
                logger.println(String.format(Messages.PerfSigStartRecording_StartedSessionRecording(), dtRecorder.getProfile(), result));
                run.addAction(new PerfSigRegisterEnvVars(sessionName, testCase, testRunId));
            } else {
                throw new RESTErrorException(String.format(Messages.PerfSigStartRecording_SessionRecordingError(), dtRecorder.getProfile()));
            }
        } catch (RESTErrorException e) {
            logger.println(String.format(Messages.PerfSigStartRecording_SessionRecordingError(), dtRecorder.getProfile(), ExceptionUtils.getStackTrace(e)));
            if (e.getMessage().contains("already started")) {
                PerfSigStopRecording stopRecording = new PerfSigStopRecording();
                stopRecording.perform(run, workspace, launcher, listener);
                Thread.sleep(10000);
                this.perform(run, workspace, launcher, listener);
            }
        }
    }

    public String getTestCase() {
        return testCase;
    }

    public String getRecordingOption() {
        return recordingOption == null ? DescriptorImpl.defaultRecordingOption : recordingOption;
    }

    @DataBoundSetter
    public void setRecordingOption(final String recordingOption) {
        this.recordingOption = recordingOption;
    }

    public boolean getLockSession() {
        return lockSession;
    }

    @DataBoundSetter
    public void setLockSession(final boolean lockSession) {
        this.lockSession = lockSession;
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        public static final boolean defaultLockSession = false;
        public static final String defaultRecordingOption = "all";

        public ListBoxModel doFillRecordingOptionItems() {
            return new ListBoxModel(new ListBoxModel.Option("all"), new ListBoxModel.Option("violations"), new ListBoxModel.Option("timeseries"));
        }

        public FormValidation doCheckTestCase(@QueryParameter final String testCase) {
            try {
                Jenkins.checkGoodName(testCase);
                GeneralTestCase.DescriptorImpl.addTestCases(testCase);
                return FormValidation.ok();
            } catch (Failure e) {
                return FormValidation.error(e.getMessage());
            }
        }

        public boolean isApplicable(final Class<? extends AbstractProject> aClass) {
            return true;
        }

        public String getDisplayName() {
            return Messages.PerfSigStartRecording_DisplayName();
        }
    }
}
