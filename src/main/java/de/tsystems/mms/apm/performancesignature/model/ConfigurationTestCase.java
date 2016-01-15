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

import de.tsystems.mms.apm.performancesignature.dynatrace.rest.DTServerConnection;
import de.tsystems.mms.apm.performancesignature.util.PerfSigUtils;
import hudson.DescriptorExtensionList;
import hudson.RelativePath;
import hudson.model.AbstractProject;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.util.ListBoxModel;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.QueryParameter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public abstract class ConfigurationTestCase implements Describable<ConfigurationTestCase>, Serializable {
    private final String name, xmlDashboard;
    private final List<Dashboard> singleDashboards;
    private final List<Dashboard> comparisonDashboards;

    public ConfigurationTestCase(final String name, final List<Dashboard> singleDashboards,
                                 final List<Dashboard> comparisonDashboards, final String xmlDashboard) {
        this.name = StringUtils.deleteWhitespace(name);
        this.singleDashboards = singleDashboards;
        this.comparisonDashboards = comparisonDashboards;
        this.xmlDashboard = xmlDashboard;
    }

    public String getName() {
        ConfigurationTestCaseDescriptor.addTestCases(name);
        return name;
    }

    public String getXmlDashboard() {
        return xmlDashboard;
    }

    public List<Dashboard> getSingleDashboards() {
        if (singleDashboards == null)
            return new ArrayList<Dashboard>();
        return singleDashboards;
    }

    public List<Dashboard> getComparisonDashboards() {
        if (comparisonDashboards == null)
            return new ArrayList<Dashboard>();
        return comparisonDashboards;
    }

    public boolean validate() {
        return StringUtils.isNotBlank(name) && StringUtils.isNotBlank(xmlDashboard);
    }

    public ConfigurationTestCaseDescriptor getDescriptor() {
        return (ConfigurationTestCaseDescriptor) PerfSigUtils.getInstanceOrDie().getDescriptorOrDie(getClass());
    }

    public abstract static class ConfigurationTestCaseDescriptor extends Descriptor<ConfigurationTestCase> {
        private static final Set<String> testCases = new LinkedHashSet<String>(); //avoid duplicates

        public static void addTestCases(final String testCase) {
            if (StringUtils.isNotBlank(testCase)) testCases.add(testCase);
        }

        public static DescriptorExtensionList<ConfigurationTestCase, Descriptor<ConfigurationTestCase>> all() {
            return PerfSigUtils.getInstanceOrDie().getDescriptorList(ConfigurationTestCase.class);
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

        public ListBoxModel doFillXmlDashboardItems(@RelativePath("..") @QueryParameter final String dynatraceServer) {
            DynatraceServerConfiguration serverConfiguration = PerfSigUtils.getServerConfiguration(dynatraceServer);
            if (serverConfiguration != null) {
                final DTServerConnection connection = new DTServerConnection(serverConfiguration);
                return PerfSigUtils.listToListBoxModel(connection.getDashboards());
            }
            return null;
        }
    }
}
