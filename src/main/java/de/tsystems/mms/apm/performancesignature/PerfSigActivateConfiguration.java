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
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.CommandExecutionException;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.DTServerConnection;
import de.tsystems.mms.apm.performancesignature.model.DynatraceServerConfiguration;
import de.tsystems.mms.apm.performancesignature.util.PerfSigUtils;
import hudson.AbortException;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.tasks.SimpleBuildStep;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.PrintStream;

public class PerfSigActivateConfiguration extends Builder implements SimpleBuildStep {
    private final String dynatraceServer, configuration;

    @DataBoundConstructor
    public PerfSigActivateConfiguration(final String dynatraceServer, final String configuration) {
        this.dynatraceServer = dynatraceServer;
        this.configuration = configuration;
    }

    @Override
    public void perform(@Nonnull final Run<?, ?> run, @Nonnull final FilePath workspace, @Nonnull final Launcher launcher, @Nonnull final TaskListener listener)
            throws InterruptedException, IOException {
        PrintStream logger = listener.getLogger();

        DynatraceServerConfiguration serverConfiguration = PerfSigUtils.getServerConfiguration(dynatraceServer);
        if (serverConfiguration == null)
            throw new AbortException("failed to lookup Dynatrace server configuration");

        logger.println(Messages.PerfSigActivateConfiguration_ActivatingProfileConfiguration());
        final DTServerConnection connection = new DTServerConnection(serverConfiguration);

        boolean result = connection.activateConfiguration(serverConfiguration.getProfile(), this.configuration);
        if (!result)
            throw new CommandExecutionException(Messages.PerfSigActivateConfiguration_InternalError());
        logger.println(Messages.PerfSigActivateConfiguration_SuccessfullyActivated() + serverConfiguration.getProfile());

        for (Agent agent : connection.getAgents()) {
            if (agent.getSystemProfile().equalsIgnoreCase(serverConfiguration.getProfile())) {
                boolean hotSensorPlacement = connection.hotSensorPlacement(agent.getAgentId());
                if (hotSensorPlacement) {
                    logger.println(Messages.PerfSigActivateConfiguration_HotSensorPlacementDone() + " " + agent.getName());
                } else {
                    logger.println(Messages.PerfSigActivateConfiguration_FailureActivation() + " " + agent.getName());
                }
            }
        }
    }

    public String getDynatraceServer() {
        return dynatraceServer;
    }

    public String getConfiguration() {
        return configuration;
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        public FormValidation doCheckConfiguration(@QueryParameter final String configuration) {
            FormValidation validationResult;
            if (StringUtils.isNotBlank(configuration)) {
                validationResult = FormValidation.ok();
            } else {
                validationResult = FormValidation.error(Messages.PerfSigActivateConfiguration_ConfigurationNotValid());
            }
            return validationResult;
        }

        public ListBoxModel doFillDynatraceServerItems() {
            return PerfSigUtils.listToListBoxModel(PerfSigUtils.getDTConfigurations());
        }

        public ListBoxModel doFillConfigurationItems(@QueryParameter final String dynatraceServer) {
            DynatraceServerConfiguration serverConfiguration = PerfSigUtils.getServerConfiguration(dynatraceServer);
            if (serverConfiguration != null) {
                final DTServerConnection connection = new DTServerConnection(serverConfiguration);
                return PerfSigUtils.listToListBoxModel(connection.getProfileConfigurations(serverConfiguration.getProfile()));
            }
            return null;
        }

        public boolean isApplicable(final Class<? extends AbstractProject> aClass) {
            return true;
        }

        public String getDisplayName() {
            return Messages.PerfSigActivateConfiguration_DisplayName();
        }
    }
}
