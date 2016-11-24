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

package de.tsystems.mms.apm.performancesignature.dynatrace.configuration;

import de.tsystems.mms.apm.performancesignature.util.PerfSigUIUtils;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import java.util.List;

public class DynatraceServerConfiguration extends AbstractDescribableImpl<DynatraceServerConfiguration> {
    private final String name, protocol, host;
    private final int port;
    private final boolean verifyCertificate;
    private final int delay, retryCount;
    private final CustomProxy customProxy;
    private final List<CredProfilePair> credProfilePairs;

    @DataBoundConstructor
    public DynatraceServerConfiguration(final String name, final String protocol, final String host, final int port, final List<CredProfilePair> credProfilePairs,
                                        final boolean verifyCertificate, final int delay, final int retryCount, final boolean proxy, final int proxySource,
                                        final String proxyServer, final int proxyPort, final String proxyUser, final String proxyPassword) {
        this.name = name;
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.credProfilePairs = credProfilePairs;
        this.verifyCertificate = verifyCertificate;
        this.delay = delay;
        this.retryCount = retryCount;
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

    public CredProfilePair getCredProfilePair(final String profile) {
        String systemProfile = profile.replaceAll("\\(.*", "").trim();
        for (CredProfilePair pair : credProfilePairs) {
            if (pair.getProfile().equals(systemProfile))
                return pair;
        }
        return null;
    }

    public int getPort() {
        return port;
    }

    public List<CredProfilePair> getCredProfilePairs() {
        return credProfilePairs;
    }

    public boolean isVerifyCertificate() {
        return verifyCertificate;
    }

    public int getDelay() {
        return delay;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public CustomProxy getCustomProxy() {
        return customProxy;
    }

    @Extension
    public static final class DescriptorImpl extends Descriptor<DynatraceServerConfiguration> {
        public static final String defaultProtocol = "https";
        public static final String defaultHost = "localhost";
        public static final int defaultPort = 8021;
        public static final int defaultDelay = 10;
        public static final int defaultRetryCount = 5;
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
                validationResult = FormValidation.error(Messages.PerfSigRecorder_DTHostNotValid());
            }
            return validationResult;
        }

        public FormValidation doCheckPort(@QueryParameter final String port) {
            FormValidation validationResult;
            if (PerfSigUIUtils.checkNotEmptyAndIsNumber(port)) {
                validationResult = FormValidation.ok();
            } else {
                validationResult = FormValidation.error(Messages.PerfSigRecorder_DTPortNotValid());
            }
            return validationResult;
        }

        public FormValidation doCheckDelay(@QueryParameter final String delay) {
            FormValidation validationResult;
            if (PerfSigUIUtils.checkNotEmptyAndIsNumber(delay)) {
                validationResult = FormValidation.ok();
            } else {
                validationResult = FormValidation.error(Messages.PerfSigRecorder_DelayNotValid());
            }
            return validationResult;
        }

        public FormValidation doCheckRetryCount(@QueryParameter final String retryCount) {
            FormValidation validationResult;
            if (PerfSigUIUtils.checkNotEmptyAndIsNumber(retryCount)) {
                validationResult = FormValidation.ok();
            } else {
                validationResult = FormValidation.error(Messages.PerfSigRecorder_RetryCountNotValid());
            }
            return validationResult;
        }
    }
}
