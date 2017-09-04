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
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import java.util.List;

public class DynatraceServerConfiguration extends AbstractDescribableImpl<DynatraceServerConfiguration> {
    private final String name;
    private String serverUrl;
    @Deprecated
    private transient String protocol, host;
    @Deprecated
    private transient int port;
    private final boolean verifyCertificate;
    private final int delay, retryCount;
    private final CustomProxy customProxy;
    private final List<CredProfilePair> credProfilePairs;

    @Deprecated
    public DynatraceServerConfiguration(final String name, final String protocol, final String host, final int port, final List<CredProfilePair> credProfilePairs,
                                        final boolean verifyCertificate, final int delay, final int retryCount, final boolean proxy, final int proxySource,
                                        final String proxyServer, final int proxyPort, final String proxyUser, final String proxyPassword) {
        this(name, protocol + "://" + host + ":" + port, credProfilePairs, verifyCertificate, delay, retryCount, proxy, proxySource,
                proxyServer, proxyPort, proxyUser, proxyPassword);
    }

    @DataBoundConstructor
    public DynatraceServerConfiguration(final String name, final String serverUrl, final List<CredProfilePair> credProfilePairs,
                                        final boolean verifyCertificate, final int delay, final int retryCount, final boolean proxy, final int proxySource,
                                        final String proxyServer, final int proxyPort, final String proxyUser, final String proxyPassword) {
        this.name = name;
        this.serverUrl = serverUrl;
        this.credProfilePairs = credProfilePairs;
        this.verifyCertificate = verifyCertificate;
        this.delay = delay;
        this.retryCount = retryCount;
        this.customProxy = proxy ? new CustomProxy(proxyServer, proxyPort, proxyUser, proxyPassword, proxySource) : null;
    }

    public String getName() {
        return name;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public CredProfilePair getCredProfilePair(final String profile) {
        String systemProfile = profile.replaceAll("\\(.*", "").trim();
        for (CredProfilePair pair : credProfilePairs) {
            if (pair.getProfile().equals(systemProfile))
                return pair;
        }
        return null;
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

    @SuppressWarnings("deprecation")
    protected Object readResolve() {
        if (protocol != null && host != null && port != 0 && serverUrl == null) {
            serverUrl = protocol + "://" + host + ":" + port;
            protocol = null;
            host = null;
            port = 0;
        }
        return this;
    }

    @Extension
    public static final class DescriptorImpl extends Descriptor<DynatraceServerConfiguration> {
        public static final String defaultServerUrl = "https://dynatrace.server:8021/";
        public static final int defaultDelay = 10;
        public static final int defaultRetryCount = 5;
        public static final boolean defaultVerifyCertificate = false;

        @Override
        public String getDisplayName() {
            return "";
        }

        public FormValidation doCheckServerUrl(@QueryParameter final String serverUrl) {
            if (PerfSigUIUtils.checkNotNullOrEmpty(serverUrl)) {
                return FormValidation.ok();
            } else {
                return FormValidation.error(Messages.PerfSigRecorder_DTServerUrlNotValid());
            }
        }

        public FormValidation doCheckDelay(@QueryParameter final String delay) {
            if (PerfSigUIUtils.checkNotEmptyAndIsNumber(delay)) {
                return FormValidation.ok();
            } else {
                return FormValidation.error(Messages.PerfSigRecorder_DelayNotValid());
            }
        }

        public FormValidation doCheckRetryCount(@QueryParameter final String retryCount) {
            if (PerfSigUIUtils.checkNotEmptyAndIsNumber(retryCount)) {
                return FormValidation.ok();
            } else {
                return FormValidation.error(Messages.PerfSigRecorder_RetryCountNotValid());
            }
        }
    }
}
