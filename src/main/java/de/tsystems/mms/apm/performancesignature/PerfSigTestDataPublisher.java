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
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.DTServerConnection;
import de.tsystems.mms.apm.performancesignature.model.PerfSigTestData;
import de.tsystems.mms.apm.performancesignature.util.PerfSigUtils;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.junit.TestDataPublisher;
import hudson.tasks.junit.TestResult;
import hudson.tasks.junit.TestResultAction;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rapi on 27.05.2015.
 */
public class PerfSigTestDataPublisher extends TestDataPublisher {

    @DataBoundConstructor
    public PerfSigTestDataPublisher() {
    }

    @Override
    public TestResultAction.Data contributeTestData(final Run<?, ?> run, @Nonnull final FilePath workspace, final Launcher launcher,
                                                    final TaskListener listener, final TestResult testResult) {
        final PrintStream logger = listener.getLogger();
        final PerfSigRecorder dtRecorder = PerfSigUtils.getRecorder((AbstractBuild) run);

        if (dtRecorder == null) {
            logger.println("Unable to find Dynatrace Configuration Post Build Step!");
            return null;
        }

        final DTServerConnection connection = new DTServerConnection(dtRecorder.getProtocol(), dtRecorder.getHost(), dtRecorder.getPort(),
                dtRecorder.getCredentialsId(), dtRecorder.isVerifyCertificate(), dtRecorder.getCustomProxy());

        logger.println(Messages.PerfSigRecorder_VerifyDTConnection());
        if (!connection.validateConnection()) {
            logger.println(Messages.PerfSigRecorder_DTConnectionError());
            checkForUnstableResult(run);
        }

        final List<TestRun> testRuns = new ArrayList<TestRun>();
        final List<PerfSigRegisterEnvVars> envVars = run.getActions(PerfSigRegisterEnvVars.class);
        for (PerfSigRegisterEnvVars registerEnvVars : envVars) {
            if (StringUtils.isNotBlank(registerEnvVars.getTestRunID())) {
                try {
                    TestRun testRun = connection.getTestRunFromXML(dtRecorder.getProfile(), registerEnvVars.getTestRunID());
                    if (testRun == null || testRun.getTestResults() == null || testRun.getTestResults().isEmpty()) {
                        logger.println(Messages.PerfSigRecorder_XMLReportError());
                        if (dtRecorder.isTechnicalFailure()) run.setResult(Result.FAILURE);
                    } else {
                        testRuns.add(testRun);
                        logger.println(String.format(Messages.PerfSigRecorder_XMLReportResults(), testRun.getTestResults().size(), " " + testRun.getTestRunID()));
                    }
                } catch (Exception e) {
                    logger.println(e);
                    if (!dtRecorder.isTechnicalFailure()) return null;
                }
            }
        }

        PerfSigTestData perfSigTestData = new PerfSigTestData(run, testRuns);
        run.addAction(new PerfSigTestDataWrapper(perfSigTestData));
        return perfSigTestData;
    }

    private void checkForUnstableResult(final Run run) {
        PerfSigRecorder recorder = PerfSigUtils.getRecorder((AbstractBuild) run);
        if (recorder.isModifyBuildResult()) run.setResult(Result.FAILURE);
    }

    private Result getBuildResult(final Run run) {
        Result result = run.getResult();
        if (result == null) {
            throw new IllegalStateException("run is ongoing");
        }
        return result;
    }

    @Extension
    public static final class PerfSigTestDataPublisherDescriptor extends Descriptor<TestDataPublisher> {
        public PerfSigTestDataPublisherDescriptor() {
            load();
        }

        @Override
        public String getDisplayName() {
            return "Add Dynatrace Performance Data to each test result";
        }
    }

}
