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

package de.tsystems.mms.apm.performancesignature.jenkins.model;

import de.tsystems.mms.apm.performancesignature.dynatrace.rest.DTServerConnection;
import de.tsystems.mms.apm.performancesignature.jenkins.util.DTPerfSigUtils;
import hudson.Extension;
import hudson.RelativePath;
import hudson.util.ListBoxModel;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rapi on 07.05.2014.
 */
public class GeneralTestCase extends ConfigurationTestCase {
    private final String xmlDashboard;
    private final List<Dashboard> singleDashboards;
    private final List<Dashboard> comparisonDashboards;

    @DataBoundConstructor
    public GeneralTestCase(final String name, final List<Dashboard> singleDashboards,
                           final List<Dashboard> comparisonDashboards, final String xmlDashboard) {
        super(name);
        this.singleDashboards = singleDashboards;
        this.comparisonDashboards = comparisonDashboards;
        this.xmlDashboard = xmlDashboard;
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

    @Override
    public boolean validate() {
        return super.validate() && StringUtils.isNotBlank(xmlDashboard);
    }

    @Extension
    public static final class DescriptorImpl extends ConfigurationTestCaseDescriptor {
        @Override
        public String getDisplayName() {
            return "other TestCases";
        }

        public ListBoxModel doFillXmlDashboardItems(@RelativePath("..") @QueryParameter("protocol") final String protocol, @RelativePath("..") @QueryParameter("host") final String host,
                                                    @RelativePath("..") @QueryParameter("port") final int port, @RelativePath("..") @QueryParameter("credentialsId") final String credentialsId,
                                                    @RelativePath("..") @QueryParameter("verifyCertificate") final boolean verifyCertificate, @RelativePath("..") @QueryParameter("useJenkinsProxy") final boolean useJenkinsProxy,
                                                    @RelativePath("..") @QueryParameter("proxyServer") final String proxyServer, @RelativePath("..") @QueryParameter("proxyPort") final int proxyPort,
                                                    @RelativePath("..") @QueryParameter("proxyUser") final String proxyUser, @RelativePath("..") @QueryParameter("proxyPassword") final String proxyPassword) {

            ProxyBlock proxy = null;
            if (StringUtils.isNotBlank(proxyServer) && proxyPort > 0 && StringUtils.isNotBlank(credentialsId)) {
                proxy = new ProxyBlock(proxyServer, proxyPort, proxyUser, proxyPassword);
            }
            final DTServerConnection newConnection = new DTServerConnection(protocol, host, port, credentialsId, verifyCertificate, useJenkinsProxy, proxy);
            return DTPerfSigUtils.listToListBoxModel(newConnection.getDashboards());
        }
    }
}
