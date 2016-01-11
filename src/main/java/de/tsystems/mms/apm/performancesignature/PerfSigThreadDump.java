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
import jenkins.tasks.SimpleBuildStep;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.PrintStream;

public class PerfSigThreadDump extends Builder implements SimpleBuildStep {
    private static final int waitForDumpTimeout = 60000;
    private static final int waitForDumpPollingInterval = 5000;
    private final String agent, host;
    private boolean lockSession;

    @DataBoundConstructor
    public PerfSigThreadDump(final String agent, final String host) {
        this.agent = agent;
        this.host = host;
    }

    @Deprecated
    public PerfSigThreadDump(final String agent, final String host, final boolean lockSession) {
        this(agent, host);
        setLockSession(lockSession);
    }

    @Override
    public void perform(@Nonnull final Run<?, ?> run, @Nonnull final FilePath workspace, @Nonnull final Launcher launcher, @Nonnull final TaskListener listener)
            throws InterruptedException, IOException {
        final PrintStream logger = listener.getLogger();
        final PerfSigRecorder dtRecorder = PerfSigUtils.getRecorder(run);

        if (dtRecorder == null) {
            throw new AbortException(Messages.PerfSigThreadDump_NoRecorderFailure());
        }

        final DTServerConnection connection = new DTServerConnection(dtRecorder.getProtocol(), dtRecorder.getHost(), dtRecorder.getPort(),
                dtRecorder.getCredentialsId(), dtRecorder.isVerifyCertificate(), dtRecorder.getCustomProxy());

        for (Agent agent : connection.getAgents()) {
            if (agent.getName().equalsIgnoreCase(this.agent) && agent.getSystemProfile().equalsIgnoreCase(dtRecorder.getProfile()) && agent.getHost().equalsIgnoreCase(this.host)) {
                logger.println("Creating Memory Dump for " + agent.getSystemProfile() + "-" + agent.getName() + "-" + agent.getHost() + "-" + agent.getProcessId());

                String threadDump = connection.threadDump(agent.getSystemProfile(), agent.getName(), agent.getHost(), agent.getProcessId(), getLockSession());
                if (StringUtils.isBlank(threadDump))
                    throw new RESTErrorException("Thread Dump wasnt taken");
                int timeout = waitForDumpTimeout;
                boolean dumpFinished = connection.threadDumpStatus(agent.getSystemProfile(), threadDump).isResultValueTrue();
                while ((!dumpFinished) && (timeout > 0)) {
                    Thread.sleep(waitForDumpPollingInterval);
                    timeout -= waitForDumpPollingInterval;
                    dumpFinished = connection.threadDumpStatus(agent.getSystemProfile(), threadDump).isResultValueTrue();
                }
                if (dumpFinished) {
                    logger.println(Messages.PerfSigThreadDump_SuccessfullyCreatedThreadDump() + agent.getName());
                    return;
                } else {
                    throw new RESTErrorException("Timeout is raised");
                }
            }
        }
        throw new AbortException(String.format(Messages.PerfSigThreadDump_AgentNotConnected(), agent));
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
                validationResult = FormValidation.error(Messages.PerfSigThreadDump_AgentNotValid());
            }
            return validationResult;
        }

        public boolean isApplicable(final Class<? extends AbstractProject> aClass) {
            return true;
        }

        public String getDisplayName() {
            return Messages.PerfSigThreadDump_DisplayName();
        }
    }
}
