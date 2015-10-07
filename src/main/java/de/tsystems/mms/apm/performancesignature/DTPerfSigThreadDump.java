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
import de.tsystems.mms.apm.performancesignature.util.DTPerfSigUtils;
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
public class DTPerfSigThreadDump extends Builder {
    private final String agent, host;
    private final boolean lockSession;
    private int waitForDumpTimeout = 60000;
    private int waitForDumpPollingInterval = 5000;

    @DataBoundConstructor
    public DTPerfSigThreadDump(final String agent, final String host, final boolean lockSession) {
        this.agent = agent;
        this.host = host;
        this.lockSession = lockSession;
    }

    @Override
    public boolean perform(final AbstractBuild build, final Launcher launcher, final BuildListener listener) {
        final PrintStream logger = listener.getLogger();
        final DTPerfSigRecorder dtRecorder = DTPerfSigUtils.getRecorder(build);

        if (dtRecorder == null) {
            logger.println(Messages.DTPerfSigThreadDump_NoRecorderFailure());
            return false;
        }

        final DTServerConnection connection = new DTServerConnection(dtRecorder.getProtocol(), dtRecorder.getHost(), dtRecorder.getPort(),
                dtRecorder.getCredentialsId(), dtRecorder.isVerifyCertificate(), dtRecorder.isUseJenkinsProxy(), dtRecorder.getCustomProxy());

        try {
            for (Agent agent : connection.getAgents()) {
                if (agent.getName().equalsIgnoreCase(this.agent) && agent.getSystemProfile().equalsIgnoreCase(dtRecorder.getProfile()) && agent.getHost().equalsIgnoreCase(this.host)) {
                    logger.println("Creating Memory Dump for " + agent.getSystemProfile() + "-" + agent.getName() + "-" + agent.getHost() + "-" + agent.getProcessId());

                    String threadDump = connection.threadDump(agent.getSystemProfile(), agent.getName(), agent.getHost(), agent.getProcessId(), getLockSession());
                    int timeout = this.waitForDumpTimeout;
                    boolean dumpFinished = connection.threadDumpStatus(agent.getSystemProfile(), threadDump).isResultValueTrue();
                    while ((!dumpFinished) && (timeout > 0)) {
                        Thread.sleep(this.waitForDumpPollingInterval);
                        timeout -= this.waitForDumpPollingInterval;
                        dumpFinished = connection.threadDumpStatus(agent.getSystemProfile(), threadDump).isResultValueTrue();
                    }
                    if (dumpFinished) {
                        logger.println(Messages.DTPerfSigThreadDump_SuccessfullyCreatedThreadDump() + agent.getName());
                        return true;
                    } else {
                        throw new RESTErrorException("Timeout is raised");
                    }
                }
            }
        } catch (Exception e) {
            logger.println(e);
            return !dtRecorder.isModifyBuildResult();
        }
        logger.println(String.format(Messages.DTPerfSigThreadDump_AgentNotConnected(), agent));
        return !dtRecorder.isModifyBuildResult();
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

    @SuppressWarnings("unused")
    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public DescriptorImpl() {
            load();
        }

        public static boolean getDefaultLockSession() {
            return false;
        }

        public FormValidation doCheckAgent(@QueryParameter final String agent) {
            FormValidation validationResult;
            if (StringUtils.isNotBlank(agent)) {
                validationResult = FormValidation.ok();
            } else {
                validationResult = FormValidation.error(Messages.DTPerfSigThreadDump_AgentNotValid());
            }
            return validationResult;
        }

        public FormValidation doCheckHost(@QueryParameter final String host) {
            FormValidation validationResult;
            if (StringUtils.isNotBlank(host)) {
                validationResult = FormValidation.ok();
            } else {
                validationResult = FormValidation.error(Messages.DTPerfSigThreadDump_AgentNotValid());
            }
            return validationResult;
        }

        public boolean isApplicable(final Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types
            return true;
        }

        /**
         * This human readable name is used in the agent screen.
         */
        public String getDisplayName() {
            return Messages.DTPerfSigThreadDump_DisplayName();
        }
    }
}
