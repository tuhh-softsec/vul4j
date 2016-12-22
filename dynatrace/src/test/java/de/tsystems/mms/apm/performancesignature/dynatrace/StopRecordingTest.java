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

import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.SystemCredentialsProvider;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;
import de.tsystems.mms.apm.performancesignature.dynatrace.configuration.CredProfilePair;
import de.tsystems.mms.apm.performancesignature.dynatrace.configuration.DynatraceServerConfiguration;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.CommandExecutionException;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.DTServerConnection;
import de.tsystems.mms.apm.performancesignature.dynatrace.util.TestUtils;
import de.tsystems.mms.apm.performancesignature.util.PerfSigUtils;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import hudson.tasks.BatchFile;
import hudson.tasks.Shell;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

public class StopRecordingTest {

    @ClassRule
    public static JenkinsRule j = new JenkinsRule();
    private static ListBoxModel dynatraceConfigurations;
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void setUp() throws Exception {
        List<DynatraceServerConfiguration> configurations = PerfSigUtils.getDTConfigurations();
        SystemCredentialsProvider.getInstance().getCredentials().add(new UsernamePasswordCredentialsImpl(CredentialsScope.GLOBAL,
                "myCreds", null, "admin", "admin"));
        SystemCredentialsProvider.getInstance().save();
        CredProfilePair credProfilePair = new CredProfilePair("easy Travel", "myCreds");
        List<CredProfilePair> credProfilePairs = new ArrayList<CredProfilePair>();
        credProfilePairs.add(credProfilePair);

        configurations.add(new DynatraceServerConfiguration("PoC PerfSig", "https", "192.168.192.202", 8021, credProfilePairs,
                false, DynatraceServerConfiguration.DescriptorImpl.defaultDelay, DynatraceServerConfiguration.DescriptorImpl.defaultRetryCount,
                false, 0, null, 0, null, null));

        configurations.add(new DynatraceServerConfiguration("PoC mobile Apps", "https", "192.168.194.209", 8021, credProfilePairs,
                false, DynatraceServerConfiguration.DescriptorImpl.defaultDelay, DynatraceServerConfiguration.DescriptorImpl.defaultRetryCount,
                false, 0, null, 0, null, null));

        Jenkins.getInstance().save();

        for (ListBoxModel.Option option : PerfSigUtils.listToListBoxModel(PerfSigUtils.getDTConfigurations())) {
            System.out.println(option.name);
        }

        assertEquals(PerfSigUtils.getDTConfigurations().size(), 2);
        dynatraceConfigurations = PerfSigUtils.listToListBoxModel(PerfSigUtils.getDTConfigurations());
        assertEquals(dynatraceConfigurations.get(0).name, "easy Travel (admin) @ PoC PerfSig");
    }

    @Test
    public void testStopContinuousSessionRecording() throws IOException {
        DTServerConnection connection = TestUtils.createDTServerConnection(dynatraceConfigurations.get(0).name);

        exception.expect(CommandExecutionException.class);
        exception.expectMessage("error stop recording session:");
        connection.stopRecording();
    }

    @Test
    public void testStopDisabledContinuousSessionRecording1() throws IOException {
        DTServerConnection connection = TestUtils.createDTServerConnection(dynatraceConfigurations.get(1).name);

        exception.expect(CommandExecutionException.class);
        exception.expectMessage("error stop recording session: Failed to stop session recording");
        connection.stopRecording();
    }

    @Test
    public void testStopDisabledContinuousSessionRecording2() throws IOException {
        DTServerConnection connection = TestUtils.createDTServerConnection(dynatraceConfigurations.get(1).name);

        String result = connection.startRecording("testDisabledContinuousSessionRecording", "triggered by UnitTest",
                PerfSigStartRecording.DescriptorImpl.defaultRecordingOption, false, true);

        String result2 = connection.stopRecording();

        assertEquals(result, result2);
        assertTrue(result.contains("testDisabledContinuousSessionRecording"));
    }

    @Test
    public void testJenkinsConfiguration() throws IOException, ExecutionException, InterruptedException {
        final String testCase = "unittest";

        FreeStyleProject project = j.createFreeStyleProject();
        project.getBuildersList().add(new PerfSigStartRecording(dynatraceConfigurations.get(0).name, testCase));
        //wait some time to get some data into the session
        if (isWindows()) {
            project.getBuildersList().add(new BatchFile("ping -n 10 127.0.0.1 > NUL"));
        } else {
            project.getBuildersList().add(new Shell("sleep 10"));
        }
        project.getBuildersList().add(new PerfSigStopRecording(dynatraceConfigurations.get(0).name));
        FreeStyleBuild build = project.scheduleBuild2(0).get();

        Thread.sleep(100000);
        PerfSigEnvInvisAction invisAction = build.getAction(PerfSigEnvInvisAction.class);

        assertEquals(build.getResult(), Result.SUCCESS);
        assertTrue(invisAction != null);
        assertTrue(invisAction.getSessionName().matches("easy Travel_test0_Build-\\d+_unittest"));
        assertTrue(invisAction.getTestCase().equals(testCase));
        assertFalse(invisAction.getTestRunID().isEmpty());
        assertTrue(invisAction.getTimeframeStart() != null);

        DTServerConnection connection = TestUtils.createDTServerConnection(dynatraceConfigurations.get(0).name);
        assertTrue(connection.getSessions().contains(invisAction.getSessionName()));
    }

    private boolean isWindows() {
        return System.getProperty("os.name").startsWith("Windows");
    }
}
