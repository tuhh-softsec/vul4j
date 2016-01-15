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

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernameCredentials;
import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import de.tsystems.mms.apm.performancesignature.Messages;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.CommandExecutionException;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.DTServerConnection;
import de.tsystems.mms.apm.performancesignature.util.PerfSigUtils;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.model.Project;
import hudson.security.ACL;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import java.util.Collections;

import static com.cloudbees.plugins.credentials.CredentialsMatchers.instanceOf;

public class DynatraceServerConfiguration extends AbstractDescribableImpl<DynatraceServerConfiguration> {
    private final String name, protocol, host, profile, credentialsId;
    private final int port;
    private boolean verifyCertificate;
    private int delay, retryCount;
    private CustomProxy customProxy;

    @DataBoundConstructor
    public DynatraceServerConfiguration(final String name, final String protocol, final String host, final int port, final String credentialsId, final String profile,
                                        final boolean verifyCertificate, final int delay, final int retryCount, final boolean proxy, final CustomProxy proxySource) {
        this.name = name;
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.credentialsId = credentialsId;
        this.profile = profile;
        this.verifyCertificate = verifyCertificate;
        this.delay = delay;
        this.retryCount = retryCount;
        this.customProxy = proxy ? proxySource : null;
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

    public String getProfile() {
        return profile;
    }

    public String getCredentialsId() {
        return credentialsId;
    }

    public int getPort() {
        return port;
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

        private static boolean checkNotNullOrEmpty(final String string) {
            return StringUtils.isNotBlank(string);
        }

        private static boolean checkNotEmptyAndIsNumber(final String number) {
            return StringUtils.isNotBlank(number) && NumberUtils.isNumber(number);
        }

        @Override
        public String getDisplayName() {
            return "";
        }

        public ListBoxModel doFillProtocolItems() {
            return new ListBoxModel(new ListBoxModel.Option("http"), new ListBoxModel.Option("https"));
        }

        public FormValidation doCheckHost(@QueryParameter final String host) {
            FormValidation validationResult;
            if (checkNotNullOrEmpty(host)) {
                validationResult = FormValidation.ok();
            } else {
                validationResult = FormValidation.error(Messages.PerfSigRecorder_DTHostNotValid());
            }
            return validationResult;
        }

        public FormValidation doCheckPort(@QueryParameter final String port) {
            FormValidation validationResult;
            if (checkNotEmptyAndIsNumber(port)) {
                validationResult = FormValidation.ok();
            } else {
                validationResult = FormValidation.error(Messages.PerfSigRecorder_DTPortNotValid());
            }
            return validationResult;
        }

        public FormValidation doCheckCredentialsId(@QueryParameter final String credentialsId) {
            FormValidation validationResult;
            if (checkNotNullOrEmpty(credentialsId)) {
                validationResult = FormValidation.ok();
            } else {
                validationResult = FormValidation.error(Messages.PerfSigRecorder_DTUserEmpty());
            }
            return validationResult;
        }

        public FormValidation doCheckProfile(@QueryParameter final String profile) {
            FormValidation validationResult;
            if (checkNotNullOrEmpty(profile)) {
                validationResult = FormValidation.ok();
            } else {
                validationResult = FormValidation.error(Messages.PerfSigRecorder_DTProfileNotValid());
            }
            return validationResult;
        }

        public FormValidation doCheckDelay(@QueryParameter final String delay) {
            FormValidation validationResult;
            if (checkNotEmptyAndIsNumber(delay)) {
                validationResult = FormValidation.ok();
            } else {
                validationResult = FormValidation.error(Messages.PerfSigRecorder_DelayNotValid());
            }
            return validationResult;
        }

        public FormValidation doCheckRetryCount(@QueryParameter final String retryCount) {
            FormValidation validationResult;
            if (checkNotEmptyAndIsNumber(retryCount)) {
                validationResult = FormValidation.ok();
            } else {
                validationResult = FormValidation.error(Messages.PerfSigRecorder_RetryCountNotValid());
            }
            return validationResult;
        }

        public FormValidation doTestDynaTraceConnection(@QueryParameter final String protocol, @QueryParameter final String host,
                                                        @QueryParameter final int port, @QueryParameter final String credentialsId,
                                                        @QueryParameter final boolean verifyCertificate, @QueryParameter final boolean proxy,
                                                        @QueryParameter final int proxySource,
                                                        @QueryParameter final String proxyServer, @QueryParameter final int proxyPort,
                                                        @QueryParameter final String proxyUser, @QueryParameter final String proxyPassword) {

            CustomProxy customProxyServer = null;
            if (proxy) {
                customProxyServer = new CustomProxy(proxyServer, proxyPort, proxyUser, proxyPassword, proxySource == 0);
            }
            final DTServerConnection connection = new DTServerConnection(protocol, host, port, credentialsId, verifyCertificate, customProxyServer);

            if (connection.validateConnection()) {
                return FormValidation.ok(Messages.PerfSigRecorder_TestConnectionSuccessful());
            } else {
                return FormValidation.warning(Messages.PerfSigRecorder_TestConnectionNotSuccessful());
            }
        }

        public ListBoxModel doFillProfileItems(@QueryParameter final String protocol, @QueryParameter final String host,
                                               @QueryParameter final int port, @QueryParameter final String credentialsId,
                                               @QueryParameter final boolean verifyCertificate, @QueryParameter final boolean proxy,
                                               @QueryParameter final int proxySource,
                                               @QueryParameter final String proxyServer, @QueryParameter final int proxyPort,
                                               @QueryParameter final String proxyUser, @QueryParameter final String proxyPassword) {

            CustomProxy customProxyServer = null;
            if (proxy) {
                customProxyServer = new CustomProxy(proxyServer, proxyPort, proxyUser, proxyPassword, proxySource == 0);
            }
            try {
                final DTServerConnection connection = new DTServerConnection(protocol, host, port, credentialsId, verifyCertificate, customProxyServer);
                return PerfSigUtils.listToListBoxModel(connection.getSystemProfiles());
            } catch (CommandExecutionException ignored) {
                return null;
            }
        }

        public ListBoxModel doFillCredentialsIdItems(@AncestorInPath final Project project) {
            return new StandardListBoxModel()
                    .withEmptySelection()
                    .withMatching(instanceOf(UsernamePasswordCredentials.class),
                            CredentialsProvider.lookupCredentials(
                                    StandardUsernameCredentials.class, project, ACL.SYSTEM, Collections.<DomainRequirement>emptyList()));
        }
    }
}
