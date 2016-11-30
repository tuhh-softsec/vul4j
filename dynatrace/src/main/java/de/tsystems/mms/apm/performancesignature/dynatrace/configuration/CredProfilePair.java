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

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernameListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.CommandExecutionException;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.DTServerConnection;
import de.tsystems.mms.apm.performancesignature.util.PerfSigUIUtils;
import de.tsystems.mms.apm.performancesignature.util.PerfSigUtils;
import hudson.Extension;
import hudson.RelativePath;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.security.ACL;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import java.util.Collections;

public class CredProfilePair extends AbstractDescribableImpl<CredProfilePair> {
    private final String profile, credentialsId;

    @DataBoundConstructor
    public CredProfilePair(final String profile, final String credentialsId) {
        this.profile = profile;
        this.credentialsId = credentialsId;
    }

    public String getCredentialsId() {
        return credentialsId;
    }

    public UsernamePasswordCredentials getCredentials() {
        return PerfSigUtils.getCredentials(credentialsId);
    }

    public String getProfile() {
        return profile;
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<CredProfilePair> {
        @Override
        public String getDisplayName() {
            return "";
        }

        public ListBoxModel doFillCredentialsIdItems(@QueryParameter String credentialsId) {
            if (!PerfSigUIUtils.getInstance().hasPermission(Jenkins.ADMINISTER)) {
                return new StandardListBoxModel().includeCurrentValue(credentialsId);
            }
            return new StandardUsernameListBoxModel()
                    .includeEmptyValue()
                    .includeMatchingAs(ACL.SYSTEM,
                            PerfSigUIUtils.getInstance(),
                            StandardUsernamePasswordCredentials.class,
                            Collections.<DomainRequirement>emptyList(),
                            CredentialsMatchers.always())
                    .includeCurrentValue(credentialsId);
        }

        public FormValidation doCheckCredentialsId(@QueryParameter String value) {
            if (!PerfSigUIUtils.getInstance().hasPermission(Jenkins.ADMINISTER)) {
                return FormValidation.ok();
            }
            for (ListBoxModel.Option o : CredentialsProvider.listCredentials(StandardUsernamePasswordCredentials.class,
                    Jenkins.getInstance(),
                    ACL.SYSTEM,
                    Collections.<DomainRequirement>emptyList(),
                    CredentialsMatchers.always())) {
                if (StringUtils.equals(value, o.value)) {
                    return FormValidation.ok();
                }
            }
            return FormValidation.error("The selected credentials cannot be found");
        }

        public ListBoxModel doFillProfileItems(@RelativePath("..") @QueryParameter final String protocol, @RelativePath("..") @QueryParameter final String host,
                                               @RelativePath("..") @QueryParameter final int port, @QueryParameter final String credentialsId,
                                               @RelativePath("..") @QueryParameter final boolean verifyCertificate, @RelativePath("..") @QueryParameter final boolean proxy,
                                               @RelativePath("..") @QueryParameter final String proxyServer, @RelativePath("..") @QueryParameter final int proxyPort,
                                               @RelativePath("..") @QueryParameter final String proxyUser, @RelativePath("..") @QueryParameter final String proxyPassword) {

            CustomProxy customProxyServer = null;
            if (proxy) {
                customProxyServer = new CustomProxy(proxyServer, proxyPort, proxyUser, proxyPassword, StringUtils.isBlank(proxyServer));
            }
            try {
                CredProfilePair pair = new CredProfilePair("", credentialsId);
                final DTServerConnection connection = new DTServerConnection(protocol, host, port, pair, verifyCertificate, customProxyServer);
                return PerfSigUtils.listToListBoxModel(connection.getSystemProfiles());
            } catch (CommandExecutionException ignored) {
                return null;
            }
        }

        public FormValidation doCheckProfile(@QueryParameter final String profile) {
            FormValidation validationResult;
            if (PerfSigUIUtils.checkNotNullOrEmpty(profile)) {
                validationResult = FormValidation.ok();
            } else {
                validationResult = FormValidation.error(Messages.PerfSigRecorder_DTProfileNotValid());
            }
            return validationResult;
        }

        public FormValidation doTestDynaTraceConnection(@QueryParameter final String protocol, @QueryParameter final String host,
                                                        @QueryParameter final int port, @QueryParameter final String credentialsId,
                                                        @QueryParameter final boolean verifyCertificate, @QueryParameter final boolean proxy,
                                                        @QueryParameter final String proxyServer, @QueryParameter final int proxyPort,
                                                        @QueryParameter final String proxyUser, @QueryParameter final String proxyPassword) {

            CustomProxy customProxyServer = null;
            if (proxy) {
                customProxyServer = new CustomProxy(proxyServer, proxyPort, proxyUser, proxyPassword, StringUtils.isBlank(proxyServer));
            }
            CredProfilePair pair = new CredProfilePair("", credentialsId);
            final DTServerConnection connection = new DTServerConnection(protocol, host, port, pair, verifyCertificate, customProxyServer);

            if (connection.validateConnection()) {
                return FormValidation.ok(Messages.PerfSigRecorder_TestConnectionSuccessful());
            } else {
                return FormValidation.warning(Messages.PerfSigRecorder_TestConnectionNotSuccessful());
            }
        }
    }
}
