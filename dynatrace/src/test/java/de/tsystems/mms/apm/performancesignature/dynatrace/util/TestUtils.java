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

package de.tsystems.mms.apm.performancesignature.dynatrace.util;

import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.SystemCredentialsProvider;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;
import de.tsystems.mms.apm.performancesignature.dynatrace.PerfSigGlobalConfiguration;
import de.tsystems.mms.apm.performancesignature.dynatrace.configuration.CredProfilePair;
import de.tsystems.mms.apm.performancesignature.dynatrace.configuration.DynatraceServerConfiguration;
import de.tsystems.mms.apm.performancesignature.util.PerfSigUtils;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestUtils {

    private TestUtils() {
    }

    public static ListBoxModel prepareDTConfigurations() throws IOException {
        List<DynatraceServerConfiguration> configurations = new ArrayList<DynatraceServerConfiguration>();
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

        PerfSigGlobalConfiguration.get().setConfigurations(configurations);
        Jenkins.getInstance().save();

        for (ListBoxModel.Option option : PerfSigUtils.listToListBoxModel(PerfSigUtils.getDTConfigurations())) {
            System.out.println(option.name);
        }

        assertEquals(PerfSigUtils.getDTConfigurations().size(), 2);
        ListBoxModel dynatraceConfigurations = PerfSigUtils.listToListBoxModel(PerfSigUtils.getDTConfigurations());
        assertEquals(dynatraceConfigurations.get(0).name, "easy Travel (admin) @ PoC PerfSig");

        return dynatraceConfigurations;
    }

    public static boolean containsOption(ListBoxModel listBoxModel, String search) {
        for (ListBoxModel.Option option : listBoxModel) {
            if (option.name.equals(search)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isWindows() {
        return System.getProperty("os.name").startsWith("Windows");
    }
}
