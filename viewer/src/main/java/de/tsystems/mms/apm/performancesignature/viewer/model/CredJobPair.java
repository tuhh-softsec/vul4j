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

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernameListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import de.tsystems.mms.apm.performancesignature.util.PerfSigUIUtils;
import de.tsystems.mms.apm.performancesignature.viewer.rest.JenkinsServerConnection;
import de.tsystems.mms.apm.performancesignature.viewer.util.ViewerUtils;
import hudson.Extension;
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

public class CredJobPair extends AbstractDescribableImpl<CredJobPair> {
    private final String jenkinsJob;
    private final String credentialsId;

    @DataBoundConstructor
    public CredJobPair(final String jenkinsJob, final String credentialsId) {
        this.jenkinsJob = jenkinsJob;
        this.credentialsId = credentialsId;
    }

    public String getCredentialsId() {
        return credentialsId;
    }

    public UsernamePasswordCredentials getCredentials() {
        return ViewerUtils.getCredentials(credentialsId);
    }

    public String getJenkinsJob() {
        return jenkinsJob;
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<CredJobPair> {
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

        public FormValidation doCheckJenkinsJob(@QueryParameter final String jenkinsJob) {
            FormValidation validationResult;
            if (PerfSigUIUtils.checkNotNullOrEmpty(jenkinsJob)) {
                validationResult = FormValidation.ok();
            } else {
                validationResult = FormValidation.error(Messages.CredJobPair_JenkinsJobNotValid());
            }
            return validationResult;
        }

        public FormValidation doTestDynaTraceConnection(@QueryParameter final String protocol, @QueryParameter final String host,
                                                        @QueryParameter final int port, @QueryParameter final String credentialsId,
                                                        @QueryParameter final String jenkinsJob, @QueryParameter boolean verifyCertificate,
                                                        @QueryParameter final boolean proxy, @QueryParameter final String proxyServer,
                                                        @QueryParameter final int proxyPort, @QueryParameter final String proxyUser,
                                                        @QueryParameter final String proxyPassword) {

            CustomProxy customProxyServer = null;
            if (proxy) {
                customProxyServer = new CustomProxy(proxyServer, proxyPort, proxyUser, proxyPassword, StringUtils.isBlank(proxyServer));
            }
            CredJobPair pair = new CredJobPair(jenkinsJob, credentialsId);
            final JenkinsServerConnection connection = new JenkinsServerConnection(protocol, host, port, pair, verifyCertificate, customProxyServer);

            if (connection.validateConnection()) {
                return FormValidation.ok(Messages.CredJobPair_TestConnectionSuccessful());
            } else {
                return FormValidation.warning(Messages.CredJobPair_TestConnectionNotSuccessful());
            }
        }
    }
}
