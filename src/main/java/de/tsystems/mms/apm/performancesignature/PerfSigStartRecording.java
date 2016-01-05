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
import de.tsystems.mms.apm.performancesignature.model.ConfigurationTestCase;
import de.tsystems.mms.apm.performancesignature.model.GeneralTestCase;
import de.tsystems.mms.apm.performancesignature.model.UnitTestCase;
import de.tsystems.mms.apm.performancesignature.util.PerfSigUtils;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Failure;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import java.io.PrintStream;

/**
 * Created by rapi on 17.05.2014.
 */
public class PerfSigStartRecording extends Builder {
    private final String testCase, recordingOption;
    private final boolean lockSession;

    @DataBoundConstructor
    public PerfSigStartRecording(final String testCase, final String recordingOption, final boolean lockSession) {
        this.testCase = StringUtils.deleteWhitespace(testCase);
        this.recordingOption = recordingOption;
        this.lockSession = lockSession;
    }

    @Override
    public boolean perform(final AbstractBuild build, final Launcher launcher, final BuildListener listener) {
        final PrintStream logger = listener.getLogger();
        final PerfSigRecorder dtRecorder = PerfSigUtils.getRecorder(build);

        if (dtRecorder == null) {
            logger.println(Messages.PerfSigStartRecording_MissingConfiguration());
            return false;
        }

        logger.println("starting session recording ...");

        final DTServerConnection connection = new DTServerConnection(dtRecorder.getProtocol(), dtRecorder.getHost(), dtRecorder.getPort(),
                dtRecorder.getCredentialsId(), dtRecorder.isVerifyCertificate(), dtRecorder.getCustomProxy());
        if (!connection.validateConnection()) {
            logger.println(Messages.PerfSigRecorder_DTConnectionError());
            return !dtRecorder.isTechnicalFailure();
        }

        try {
            String testRunId = null;
            for (ConfigurationTestCase tc : dtRecorder.getConfigurationTestCases()) {
                if (tc.getName().equals(this.testCase) && tc instanceof UnitTestCase) {
                    logger.println("registering new TestRun");

                    testRunId = connection.registerTestRun(dtRecorder.getProfile(), build.getNumber());
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

            final String testCase = build.getEnvironment(listener).expand(this.testCase);
            String sessionName = dtRecorder.getProfile() + "_" + build.getProject().getName() + "_Build-" + build.getNumber() + "_" + testCase;
            sessionName = sessionName.replace("/", "_");
            final String result = connection.startRecording(dtRecorder.getProfile(), sessionName, "This Session is triggered by Jenkins", this.recordingOption, lockSession, false);

            if (result != null && result.equals(sessionName)) {
                logger.println(String.format(Messages.PerfSigStartRecording_StartedSessionRecording(), dtRecorder.getProfile(), result));
                build.addAction(new PerfSigRegisterEnvVars(sessionName, testCase, testRunId));
                return true;
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            logger.println(String.format(Messages.PerfSigStartRecording_SessionRecordingError(), dtRecorder.getProfile(), e.getMessage()));
            if (e.getMessage().contains("already started")) {
                try {
                    PerfSigStopRecording stopRecording = new PerfSigStopRecording(false);
                    stopRecording.perform(build, launcher, listener);
                    Thread.sleep(10000);
                    this.perform(build, launcher, listener);
                } catch (Exception ex) {
                    logger.println(ex);
                    return !dtRecorder.isTechnicalFailure();
                }
            }
            return !dtRecorder.isTechnicalFailure();
        }
        logger.println(String.format(Messages.PerfSigStartRecording_SessionRecordingError(), dtRecorder.getProfile(), ""));
        return !dtRecorder.isTechnicalFailure();
    }

    public String getTestCase() {
        return testCase;
    }

    public String getRecordingOption() {
        return recordingOption;
    }

    public boolean getLockSession() {
        return lockSession;
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public DescriptorImpl() {
            load();
        }

        public static boolean getDefaultLockSession() {
            return false;
        }

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
