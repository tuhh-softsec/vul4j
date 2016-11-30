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

package de.tsystems.mms.apm.performancesignature.dynatrace;

import de.tsystems.mms.apm.performancesignature.dynatrace.configuration.CredProfilePair;
import de.tsystems.mms.apm.performancesignature.dynatrace.configuration.DynatraceServerConfiguration;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.DTServerConnection;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.RESTErrorException;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.model.Agent;
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
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

public class PerfSigThreadDump extends Builder implements SimpleBuildStep {
    private static final int waitForDumpTimeout = 60000;
    private static final int waitForDumpPollingInterval = 5000;
    private final String dynatraceProfile, agent, host;
    private boolean lockSession;

    @DataBoundConstructor
    public PerfSigThreadDump(final String dynatraceProfile, final String agent, final String host) {
        this.dynatraceProfile = dynatraceProfile;
        this.agent = agent;
        this.host = host;
    }

    @Override
    public void perform(@Nonnull final Run<?, ?> run, @Nonnull final FilePath workspace, @Nonnull final Launcher launcher, @Nonnull final TaskListener listener)
            throws InterruptedException, IOException {
        PrintStream logger = listener.getLogger();

        DynatraceServerConfiguration serverConfiguration = PerfSigUtils.getServerConfiguration(dynatraceProfile);
        if (serverConfiguration == null) {
            throw new AbortException(Messages.PerfSigRecorder_FailedToLookupServer());
        }

        CredProfilePair pair = serverConfiguration.getCredProfilePair(dynatraceProfile);
        if (pair == null) {
            throw new AbortException(Messages.PerfSigRecorder_FailedToLookupProfile());
        }

        logger.println(Messages.PerfSigStartRecording_StartingSession());
        final DTServerConnection connection = new DTServerConnection(serverConfiguration, pair);
        if (!connection.validateConnection()) {
            throw new RESTErrorException(Messages.PerfSigRecorder_DTConnectionError());
        }

        List<Agent> agents = connection.getAgents();
        for (Agent agent : agents) {
            if (agent.getName().equals(this.agent) && agent.getSystemProfile().equals(pair.getProfile()) && agent.getHost().equals(this.host)) {
                logger.println(Messages.PerfSigThreadDump_CreatingThreadDump(agent.getSystemProfile(), agent.getName(), agent.getHost(),
                        String.valueOf(agent.getProcessId())));

                String threadDump = connection.threadDump(agent.getName(), agent.getHost(), agent.getProcessId(), getLockSession());
                if (StringUtils.isBlank(threadDump)) {
                    throw new RESTErrorException(Messages.PerfSigThreadDump_ThreadDumpWasntTaken());
                }
                int timeout = waitForDumpTimeout;
                boolean dumpFinished = connection.threadDumpStatus(threadDump).isResultValueTrue();
                while ((!dumpFinished) && (timeout > 0)) {
                    Thread.sleep(waitForDumpPollingInterval);
                    timeout -= waitForDumpPollingInterval;
                    dumpFinished = connection.threadDumpStatus(threadDump).isResultValueTrue();
                }
                if (dumpFinished) {
                    logger.println(Messages.PerfSigThreadDump_SuccessfullyCreatedThreadDump(agent.getName()));
                    return;
                } else {
                    throw new RESTErrorException(Messages.PerfSigStopRecording_TimeoutRaised());
                }
            }
        }
        throw new AbortException(Messages.PerfSigThreadDump_AgentNotConnected(agent));
    }

    public String getDynatraceProfile() {
        return dynatraceProfile;
    }

    public String getAgent() {
        return agent;
    }

    public String getHost() {
        return host;
    }

    public boolean getLockSession() {
        return lockSession;
    }

    @DataBoundSetter
    public void setLockSession(final boolean lockSession) {
        this.lockSession = lockSession;
    }

    @Symbol("createThreadDump")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        public static final boolean defaultLockSession = false;

        public FormValidation doCheckAgent(@QueryParameter final String agent) {
            FormValidation validationResult;
            if (StringUtils.isNotBlank(agent)) {
                validationResult = FormValidation.ok();
            } else {
                validationResult = FormValidation.error(Messages.PerfSigThreadDump_AgentNotValid());
            }
            return validationResult;
        }

        public FormValidation doCheckHost(@QueryParameter final String host) {
            FormValidation validationResult;
            if (StringUtils.isNotBlank(host)) {
                validationResult = FormValidation.ok();
            } else {
                validationResult = FormValidation.error(Messages.PerfSigThreadDump_HostNotValid());
            }
            return validationResult;
        }

        public ListBoxModel doFillDynatraceProfileItems() {
            return PerfSigUtils.listToListBoxModel(PerfSigUtils.getDTConfigurations());
        }

        public ListBoxModel doFillAgentItems(@QueryParameter final String dynatraceProfile) {
            return PerfSigUtils.fillAgentItems(dynatraceProfile);
        }

        public ListBoxModel doFillHostItems(@QueryParameter final String dynatraceProfile, @QueryParameter final String agent) {
            return PerfSigUtils.fillHostItems(dynatraceProfile, agent);
        }

        public boolean isApplicable(final Class<? extends AbstractProject> aClass) {
            return true;
        }

        public String getDisplayName() {
            return Messages.PerfSigThreadDump_DisplayName();
        }
    }
}
