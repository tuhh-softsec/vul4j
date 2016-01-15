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

import de.tsystems.mms.apm.performancesignature.PerfSigRecorder;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.DTServerConnection;
import de.tsystems.mms.apm.performancesignature.util.PerfSigUtils;
import hudson.Extension;
import hudson.RelativePath;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.ListBoxModel;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import java.io.Serializable;

public class Dashboard extends AbstractDescribableImpl<Dashboard> implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String name;

    @DataBoundConstructor
    public Dashboard(final String dashboard) {
        this.name = dashboard;
    }

    public String getName() {
        return name;
    }

    public String getDashboard() {
        return name;
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<Dashboard> {
        public DescriptorImpl() {
            super(Dashboard.class);
            load();
        }

        public PerfSigRecorder.DescriptorImpl getPublisherDescriptor() {
            return PerfSigUtils.getInstanceOrDie().getDescriptorByType(PerfSigRecorder.DescriptorImpl.class);
        }

        @Override
        public String getDisplayName() {
            return "Single/Comparison Report Dashboards";
        }

        public ListBoxModel doFillDashboardItems(@RelativePath("../..") @QueryParameter final String dynatraceServer) {
            DynatraceServerConfiguration serverConfiguration = PerfSigUtils.getServerConfiguration(dynatraceServer);
            if (serverConfiguration != null) {
                final DTServerConnection connection = new DTServerConnection(serverConfiguration);
                return PerfSigUtils.listToListBoxModel(connection.getDashboards());
            }
            return null;
        }
    }
}
