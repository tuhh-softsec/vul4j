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
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import org.apache.commons.lang3.StringUtils;
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
        this.testCase = testCase;
        this.recordingOption = recordingOption;
        this.lockSession = lockSession;
    }

    @Override
    public boolean perform(final AbstractBuild build, final Launcher launcher, final BuildListener listener) {
        // This is where you 'build' the project.
        final PrintStream logger = listener.getLogger();

        final PerfSigRecorder dtRecorder = PerfSigUtils.getRecorder(build);
        final PerfSigStopRecording dtStopRecording = PerfSigUtils.getDTPerfSigBuilder(build, PerfSigStopRecording.class);

        if (dtRecorder == null) {
            logger.println(Messages.DTPerfSigStartRecording_MissingConfiguration());
            return false;
        }
        if (dtStopRecording == null) {
            logger.println(Messages.DTPerfSigStartRecording_MissingStopRecording());
            return !dtRecorder.isModifyBuildResult();
        }

        logger.println("starting session recording ...");

        final DTServerConnection connection = new DTServerConnection(dtRecorder.getProtocol(), dtRecorder.getHost(), dtRecorder.getPort(),
                dtRecorder.getCredentialsId(), dtRecorder.isVerifyCertificate(), dtRecorder.isUseJenkinsProxy(), dtRecorder.getCustomProxy());
        if (!connection.validateConnection()) {
            logger.println(Messages.DTPerfSigRecorder_DTConnectionError());
            return !dtRecorder.isModifyBuildResult();
        }

        try {
            String testRunId = null;
            for (ConfigurationTestCase tc : dtRecorder.getConfigurationTestCases()) {
                if (tc.getName().equals(this.testCase) && tc instanceof UnitTestCase) {
                    logger.println("registering new TestRun");

                    testRunId = connection.registerTestRun(dtRecorder.getProfile(), build.getNumber());
                    if (testRunId != null) {
                        logger.println(String.format(Messages.DTPerfSigStartRecording_StartedTestRun(), dtRecorder.getProfile(), testRunId));
                        logger.println("Dynatrace: registered test run " + testRunId + "" +
                                " (available in the environment as " + PerfSigRegisterEnvVars.TESTRUN_ID_KEY +
                                " and " + PerfSigRegisterEnvVars.SESSIONCOUNT + ")");
                    } else {
                        logger.println("Warning: Could not register TestRun");
                    }
                    break;
                }
            }

            String testCase = build.getEnvironment(listener).expand(this.testCase);
            String sessionName = dtRecorder.getProfile() + "_" + build.getProject().getName() + "_Build-" + build.getNumber() + "_" + testCase;
            sessionName = sessionName.replace("/", "_");
            String result = connection.startRecording(dtRecorder.getProfile(), sessionName, "This Session is triggered by Jenkins", this.recordingOption, lockSession, false);

            if (result != null && result.equals(sessionName)) {
                logger.println(String.format(Messages.DTPerfSigStartRecording_StartedSessionRecording(), dtRecorder.getProfile(), result));
                build.addAction(new PerfSigRegisterEnvVars(sessionName, testCase, testRunId));
                return true;
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            logger.println(String.format(Messages.DTPerfSigStartRecording_SessionRecordingError(), dtRecorder.getProfile(), e.getMessage()));
            if (e.getMessage().contains("already started")) {
                try {
                    dtStopRecording.perform(build, launcher, listener);
                    Thread.sleep(10000);
                    this.perform(build, launcher, listener);
                } catch (Exception ex) {
                    logger.println(ex);
                    return !dtRecorder.isModifyBuildResult();
                }
            }
            return !dtRecorder.isModifyBuildResult();
        }
        logger.println(String.format(Messages.DTPerfSigStartRecording_SessionRecordingError(), dtRecorder.getProfile(), ""));
        return !dtRecorder.isModifyBuildResult();
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

    @SuppressWarnings("unused")
    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public DescriptorImpl() {
            load();
        }

        public static boolean getDefaultRegisterTestRun() {
            return false;
        }

        public static boolean getDefaultLockSession() {
            return false;
        }

        public ListBoxModel doFillRecordingOptionItems() {
            return new ListBoxModel(new ListBoxModel.Option("all"), new ListBoxModel.Option("violations"), new ListBoxModel.Option("timeseries"));
        }

        public FormValidation doCheckTestCase(@QueryParameter final String testCase) {
            FormValidation validationResult;
            if (StringUtils.isNotBlank(testCase)) {
                GeneralTestCase.DescriptorImpl.addTestCases(testCase);
                validationResult = FormValidation.ok();
            } else {
                validationResult = FormValidation.error(Messages.DTPerfSigStartRecording_SessionSuffixNotValid());
            }
            return validationResult;
        }

        public boolean isApplicable(final Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return Messages.DTPerfSigStartRecording_DisplayName();
        }
    }
}
