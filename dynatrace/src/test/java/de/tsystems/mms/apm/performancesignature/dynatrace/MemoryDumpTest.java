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

import de.tsystems.mms.apm.performancesignature.dynatrace.util.TestUtils;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.junit.Assert.*;

public class MemoryDumpTest {

    @ClassRule
    public static JenkinsRule j = new JenkinsRule();
    private static ListBoxModel dynatraceConfigurations;

    @BeforeClass
    public static void setUp() throws Exception {
        dynatraceConfigurations = TestUtils.prepareDTConfigurations();
    }

    @Test
    public void testJenkinsConfiguration() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();
        PerfSigMemoryDump memoryDump = new PerfSigMemoryDump(dynatraceConfigurations.get(0).name, "CustomerFrontend_easyTravel_8080", "wum192202");
        memoryDump.setType("extended");
        project.getBuildersList().add(memoryDump);
        FreeStyleBuild build = j.assertBuildStatusSuccess(project.scheduleBuild2(0));

        String s = FileUtils.readFileToString(build.getLogFile());
        assertTrue(s.contains("successfully created memory dump on"));
    }

    @Test
    public void testFillAgentItems() {
        PerfSigMemoryDump.DescriptorImpl descriptor = new PerfSigMemoryDump.DescriptorImpl();
        ListBoxModel listBoxModel = descriptor.doFillAgentItems(dynatraceConfigurations.get(0).name);

        assertFalse(listBoxModel.isEmpty());
        assertTrue(TestUtils.containsOption(listBoxModel, "BusinessBackend_easyTravel"));
        assertTrue(TestUtils.containsOption(listBoxModel, "CreditCardAuthorization_easyTravel"));
    }

    @Test
    public void testFillHostItems() {
        PerfSigMemoryDump.DescriptorImpl descriptor = new PerfSigMemoryDump.DescriptorImpl();
        ListBoxModel listBoxModel = descriptor.doFillHostItems(dynatraceConfigurations.get(0).name, "CreditCardAuthorization_easyTravel");

        assertFalse(listBoxModel.isEmpty());
        assertTrue(TestUtils.containsOption(listBoxModel, "wum192202"));
    }

    @Test
    public void testCheckAgent() {
        PerfSigMemoryDump.DescriptorImpl descriptor = new PerfSigMemoryDump.DescriptorImpl();

        assertEquals(descriptor.doCheckAgent("BusinessBackend_easyTravel"), (FormValidation.ok()));
        assertNotEquals(descriptor.doCheckHost(""), FormValidation.ok());
    }

    @Test
    public void testCheckHost() {
        PerfSigMemoryDump.DescriptorImpl descriptor = new PerfSigMemoryDump.DescriptorImpl();

        assertEquals(descriptor.doCheckHost("wum192202"), (FormValidation.ok()));
        assertNotEquals(descriptor.doCheckHost(""), FormValidation.ok());
    }
}
