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

import de.tsystems.mms.apm.performancesignature.dynatrace.rest.CommandExecutionException;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.DTServerConnection;
import de.tsystems.mms.apm.performancesignature.dynatrace.util.TestUtils;
import de.tsystems.mms.apm.performancesignature.util.PerfSigUtils;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.util.ListBoxModel;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StartRecordingTest {

    @ClassRule
    public static JenkinsRule j = new JenkinsRule();
    private static ListBoxModel dynatraceConfigurations;

    @BeforeClass
    public static void setUp() throws Exception {
        dynatraceConfigurations = TestUtils.prepareDTConfigurations();
    }

    @Test
    public void testContinuousSessionRecording() throws IOException {
        DTServerConnection connection = PerfSigUtils.createDTServerConnection(dynatraceConfigurations.get(0).name);

        String result = null;
        try {
            result = connection.startRecording("testContinuousSessionRecording", "triggered by UnitTest",
                    PerfSigStartRecording.DescriptorImpl.defaultRecordingOption, false, false);
        } catch (CommandExecutionException e) {
            assertTrue(e.getMessage().contains("continuous"));
        } finally {
            System.out.println("Result: " + result);
        }
    }

    @Test
    public void testDisabledContinuousSessionRecording() throws IOException {
        DTServerConnection connection = PerfSigUtils.createDTServerConnection(dynatraceConfigurations.get(1).name);

        try {
            String result = connection.startRecording("testDisabledContinuousSessionRecording", "triggered by UnitTest",
                    PerfSigStartRecording.DescriptorImpl.defaultRecordingOption, false, true);

            assertTrue(result.contains("testDisabledContinuousSessionRecording"));
        } finally {
            connection.stopRecording();
        }
    }

    @Test
    public void testJenkinsConfiguration() throws Exception {
        final String testCase = "unittest";

        FreeStyleProject project = j.createFreeStyleProject();
        project.getBuildersList().add(new PerfSigStartRecording(dynatraceConfigurations.get(0).name, testCase));
        FreeStyleBuild build = j.assertBuildStatusSuccess(project.scheduleBuild2(0));

        PerfSigEnvInvisAction invisAction = build.getAction(PerfSigEnvInvisAction.class);

        assertTrue(invisAction != null);
        assertTrue(invisAction.getSessionName().matches("easy Travel_test0_Build-\\d+_unittest"));
        assertTrue(invisAction.getTestCase().equals(testCase));
        assertFalse(invisAction.getTestRunID().isEmpty());
        assertTrue(invisAction.getTimeframeStart() != null);
    }
}
