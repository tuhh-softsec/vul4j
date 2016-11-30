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

package de.tsystems.mms.apm.performancesignature.viewer.model;

import de.tsystems.mms.apm.performancesignature.util.PerfSigUIUtils;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import java.util.List;

public class JenkinsServerConfiguration extends AbstractDescribableImpl<JenkinsServerConfiguration> {
    private final String name, protocol, host;
    private final int port;
    private final boolean verifyCertificate;
    private final List<CredJobPair> credJobPairs;
    private final CustomProxy customProxy;

    @DataBoundConstructor
    public JenkinsServerConfiguration(final String name, final String protocol, final String host, final int port, final List<CredJobPair> credJobPairs,
                                      final boolean verifyCertificate, final boolean proxy, final int proxySource, final String proxyServer,
                                      final int proxyPort, final String proxyUser, final String proxyPassword) {
        this.name = name;
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.credJobPairs = credJobPairs;
        this.verifyCertificate = verifyCertificate;

        this.customProxy = proxy ? new CustomProxy(proxyServer, proxyPort, proxyUser, proxyPassword, proxySource) : null;
    }

    public String getName() {
        return name;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public CredJobPair getCredJobPair(final String job) {
        String jenkinsJob = job.replaceAll("\\(.*", "").trim();
        for (CredJobPair pair : credJobPairs) {
            if (pair.getJenkinsJob().equals(jenkinsJob))
                return pair;
        }
        return null;
    }

    public int getPort() {
        return port;
    }

    public List<CredJobPair> getCredJobPairs() {
        return credJobPairs;
    }

    public boolean isVerifyCertificate() {
        return verifyCertificate;
    }

    public CustomProxy getCustomProxy() {
        return customProxy;
    }

    @Extension
    public static final class DescriptorImpl extends Descriptor<JenkinsServerConfiguration> {
        public static final String defaultProtocol = "https";
        public static final String defaultHost = "localhost";
        public static final int defaultPort = 8080;
        public static final boolean defaultVerifyCertificate = false;

        @Override
        public String getDisplayName() {
            return "";
        }

        public ListBoxModel doFillProtocolItems() {
            return new ListBoxModel(new ListBoxModel.Option("http"), new ListBoxModel.Option("https"));
        }

        public FormValidation doCheckHost(@QueryParameter final String host) {
            FormValidation validationResult;
            if (PerfSigUIUtils.checkNotNullOrEmpty(host)) {
                validationResult = FormValidation.ok();
            } else {
                validationResult = FormValidation.error(Messages.JenkinsServerConfiguration_ServerNotValid());
            }
            return validationResult;
        }

        public FormValidation doCheckPort(@QueryParameter final String port) {
            FormValidation validationResult;
            if (PerfSigUIUtils.checkNotEmptyAndIsNumber(port)) {
                validationResult = FormValidation.ok();
            } else {
                validationResult = FormValidation.error(Messages.JenkinsServerConfiguration_PortNotValid());
            }
            return validationResult;
        }
    }
}
