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

package de.tsystems.mms.apm.performancesignature.model;

import de.tsystems.mms.apm.performancesignature.util.DTPerfSigUtils;
import hudson.model.AbstractProject;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.util.ListBoxModel;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by rapi on 13.04.2015.
 */
public abstract class ConfigurationTestCase implements Describable<ConfigurationTestCase>, Serializable {
    private final String name;

    public ConfigurationTestCase(final String name) {
        this.name = name;
    }

    public boolean validate() {
        return StringUtils.isNotBlank(name);
    }

    public String getName() {
        ConfigurationTestCaseDescriptor.addTestCases(name);
        return name;
    }

    public ConfigurationTestCaseDescriptor getDescriptor() {
        return (ConfigurationTestCaseDescriptor) DTPerfSigUtils.getInstanceOrDie().getDescriptorOrDie(getClass());
    }

    public abstract static class ConfigurationTestCaseDescriptor extends Descriptor<ConfigurationTestCase> {
        private static final Set<String> testCases = new LinkedHashSet<String>(); //avoid duplicates

        public static void addTestCases(final String testCase) {
            if (StringUtils.isNotBlank(testCase)) testCases.add(testCase);
        }

        public static List<ConfigurationTestCaseDescriptor> all(final Class<? extends AbstractProject<?, ?>> jobType) {
            return DTPerfSigUtils.getInstanceOrDie().getDescriptorList(ConfigurationTestCase.class);
        }

        public static List<ConfigurationTestCaseDescriptor> all() {
            return all(null);
        }

        public boolean isApplicable(final Class<? extends AbstractProject<?, ?>> jobType) {
            return true;
        }

        public ListBoxModel doFillNameItems() {
            final ListBoxModel out = new ListBoxModel();
            for (String s : testCases)
                out.add(s);
            return out;
        }
    }
}
