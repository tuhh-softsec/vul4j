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

package de.tsystems.mms.apm.performancesignature;

import de.tsystems.mms.apm.performancesignature.dynatrace.model.Agent;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.DTServerConnection;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.RESTErrorException;
import de.tsystems.mms.apm.performancesignature.util.PerfSigUtils;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import java.io.PrintStream;

/**
 * Created by rapi on 20.10.2014.
 */
@SuppressWarnings("unused")
public class PerfSigActivateConfiguration extends Builder {
    private final String configuration;

    @DataBoundConstructor
    public PerfSigActivateConfiguration(final String configuration) {
        this.configuration = configuration;
    }

    @Override
    public boolean perform(final AbstractBuild build, final Launcher launcher, final BuildListener listener) {
        final PrintStream logger = listener.getLogger();
        final PerfSigRecorder dtRecorder = PerfSigUtils.getRecorder(build);

        if (dtRecorder == null) {
            logger.println(Messages.DTPerfSigActivateConfiguration_NoRecorderFailure());
            return false;
        }

        logger.println(Messages.DTPerfSigActivateConfiguration_ActivatingProfileConfiguration());
        final DTServerConnection connection = new DTServerConnection(dtRecorder.getProtocol(), dtRecorder.getHost(), dtRecorder.getPort(),
                dtRecorder.getCredentialsId(), dtRecorder.isVerifyCertificate(), dtRecorder.isUseJenkinsProxy(), dtRecorder.getCustomProxy());

        try {
            boolean result = connection.activateConfiguration(dtRecorder.getProfile(), this.configuration);
            if (!result) throw new RESTErrorException(Messages.DTPerfSigActivateConfiguration_InternalError());
            logger.println(Messages.DTPerfSigActivateConfiguration_SuccessfullyActivated() + dtRecorder.getProfile());

            for (Agent agent : connection.getAgents()) {
                if (agent.getSystemProfile().equalsIgnoreCase(dtRecorder.getProfile())) {
                    boolean hotSensorPlacement = connection.hotSensorPlacement(agent.getAgentId());
                    logger.println(Messages.DTPerfSigActivateConfiguration_HotSensorPlacementDone() + agent.getName());
                }
            }
            return true;
        } catch (RESTErrorException e) {
            logger.println(Messages.DTPerfSigActivateConfiguration_FailureActivation() + dtRecorder.getProfile() + " " + e);
            return !dtRecorder.isModifyBuildResult();
        }
    }

    public String getConfiguration() {
        return configuration;
    }

    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public DescriptorImpl() {
            load();
        }

        public FormValidation doCheckConfiguration(@QueryParameter final String configuration) {
            FormValidation validationResult;
            if (StringUtils.isNotBlank(configuration)) {
                validationResult = FormValidation.ok();
            } else {
                validationResult = FormValidation.error(Messages.DTPerfSigActivateConfiguration_ConfigurationNotValid());
            }
            return validationResult;
        }

        public boolean isApplicable(final Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return Messages.DTPerfSigActivateConfiguration_DisplayName();
        }
    }
}
