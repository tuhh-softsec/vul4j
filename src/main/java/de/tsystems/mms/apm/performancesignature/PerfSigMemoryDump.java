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
import hudson.util.ListBoxModel;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import java.io.PrintStream;

/**
 * Created by rapi on 20.10.2014.
 */
@SuppressWarnings("unused")
public class PerfSigMemoryDump extends Builder {
    private final String agent, host, type;
    private final boolean lockSession, captureStrings, capturePrimitives, autoPostProcess, dogc;
    private int waitForDumpTimeout = 60000;
    private int waitForDumpPollingInterval = 5000;

    @DataBoundConstructor
    public PerfSigMemoryDump(final String agent, final String host, final String type, final boolean lockSession, final boolean captureStrings,
                             final boolean capturePrimitives, final boolean autoPostProcess, final boolean dogc) {
        this.agent = agent;
        this.host = host;
        this.type = type;
        this.lockSession = lockSession;
        this.captureStrings = captureStrings;
        this.capturePrimitives = capturePrimitives;
        this.autoPostProcess = autoPostProcess;
        this.dogc = dogc;
    }

    @Override
    public boolean perform(final AbstractBuild build, final Launcher launcher, final BuildListener listener) {
        final PrintStream logger = listener.getLogger();
        final PerfSigRecorder dtRecorder = PerfSigUtils.getRecorder(build);

        if (dtRecorder == null) {
            logger.println(Messages.DTPerfSigMemoryDump_NoRecorderFailure());
            return false;
        }

        final DTServerConnection connection = new DTServerConnection(dtRecorder.getProtocol(), dtRecorder.getHost(), dtRecorder.getPort(),
                dtRecorder.getCredentialsId(), dtRecorder.isVerifyCertificate(), dtRecorder.isUseJenkinsProxy(), dtRecorder.getCustomProxy());

        try {
            for (Agent agent : connection.getAgents()) {
                if (agent.getName().equalsIgnoreCase(this.agent) && agent.getSystemProfile().equalsIgnoreCase(dtRecorder.getProfile()) && agent.getHost().equalsIgnoreCase(this.host)) {
                    logger.println("Creating Memory Dump for " + agent.getSystemProfile() + "-" + agent.getName() + "-" + agent.getHost() + "-" + agent.getProcessId());

                    String memoryDump = connection.memoryDump(agent.getSystemProfile(), agent.getName(), agent.getHost(), agent.getProcessId(), this.type, this.lockSession, this.captureStrings, this.capturePrimitives, this.autoPostProcess, this.dogc);
                    if ((memoryDump == null) || (memoryDump.length() == 0)) {
                        throw new RESTErrorException("Memory Dump wasnt taken");
                    }
                    int timeout = waitForDumpTimeout;
                    boolean dumpFinished = connection.memoryDumpStatus(agent.getSystemProfile(), memoryDump).isResultValueTrue();
                    while ((!dumpFinished) && (timeout > 0)) {
                        Thread.sleep(waitForDumpPollingInterval);
                        timeout -= waitForDumpPollingInterval;
                        dumpFinished = connection.memoryDumpStatus(agent.getSystemProfile(), memoryDump).isResultValueTrue();
                    }
                    if (dumpFinished) {
                        logger.println(Messages.DTPerfSigMemoryDump_SuccessfullyCreatedMemoryDump() + agent.getName());
                        return true;
                    } else {
                        throw new RESTErrorException("Timeout raised");
                    }
                }
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            logger.println(e);
            return !dtRecorder.isModifyBuildResult();
        }
        logger.println(String.format(Messages.DTPerfSigMemoryDump_AgentNotConnected(), agent));
        return !dtRecorder.isModifyBuildResult();
    }

    public String getAgent() {
        return agent;
    }

    public String getHost() {
        return host;
    }

    public String getType() {
        return type;
    }

    public boolean getLockSession() {
        return lockSession;
    }

    public boolean getCaptureStrings() {
        return captureStrings;
    }

    public boolean getCapturePrimitives() {
        return capturePrimitives;
    }

    public boolean getAutoPostProcess() {
        return autoPostProcess;
    }

    public boolean getDogc() {
        return dogc;
    }

    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public DescriptorImpl() {
            load();
        }

        public static boolean getDefaultLockSession() {
            return false;
        }

        public static boolean getDefaultCaptureStrings() {
            return false;
        }

        public static boolean getDefaultCapturePrimitives() {
            return false;
        }

        public static boolean getDefaultAutoPostProcess() {
            return false;
        }

        public static boolean getDefaultDogc() {
            return false;
        }

        public ListBoxModel doFillTypeItems() {
            return new ListBoxModel(new ListBoxModel.Option("simple"), new ListBoxModel.Option("extended"), new ListBoxModel.Option("selective"));
        }

        public FormValidation doCheckAgent(@QueryParameter final String agent) {
            FormValidation validationResult;
            if (StringUtils.isNotBlank(agent)) {
                validationResult = FormValidation.ok();
            } else {
                validationResult = FormValidation.error(Messages.DTPerfSigMemoryDump_AgentNotValid());
            }
            return validationResult;
        }

        public FormValidation doCheckHost(@QueryParameter final String host) {
            FormValidation validationResult;
            if (StringUtils.isNotBlank(host)) {
                validationResult = FormValidation.ok();
            } else {
                validationResult = FormValidation.error(Messages.DTPerfSigMemoryDump_AgentNotValid());
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
            return Messages.DTPerfSigMemoryDump_DisplayName();
        }
    }
}
