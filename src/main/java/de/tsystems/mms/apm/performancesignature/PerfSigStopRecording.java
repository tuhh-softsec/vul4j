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
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.PrintStream;

/**
 * Created by rapi on 17.05.2014.
 */
public class PerfSigStopRecording extends Builder {
    private static final int reanalyzeSessionTimeout = 60000; //==1 minute
    private static final int reanalyzeSessionPollingInterval = 5000; //==5 seconds
    private final boolean reanalyzeSession;

    @DataBoundConstructor
    public PerfSigStopRecording(final boolean reanalyzeSession) {
        this.reanalyzeSession = reanalyzeSession;
    }

    public boolean getReanalyzeSession() {
        return reanalyzeSession;
    }

    @Override
    public boolean perform(final AbstractBuild build, final Launcher launcher, final BuildListener listener) {
        final PrintStream logger = listener.getLogger();

        logger.println(Messages.PerfSigStopRecording_StopSessionRecording());
        final PerfSigRecorder dtRecorder = PerfSigUtils.getRecorder(build);
        if (dtRecorder == null) {
            logger.println(Messages.PerfSigStopRecording_MissingConfiguration());
            return false;
        }

        final DTServerConnection connection = new DTServerConnection(dtRecorder.getProtocol(), dtRecorder.getHost(), dtRecorder.getPort(),
                dtRecorder.getCredentialsId(), dtRecorder.isVerifyCertificate(), dtRecorder.getCustomProxy());

        try {
            String sessionName = connection.stopRecording(dtRecorder.getProfile());
            if (sessionName == null) throw new RESTErrorException(Messages.PerfSigStopRecording_InternalError());
            logger.println(String.format("Stopped recording on %s with SessionName %s", dtRecorder.getProfile(), sessionName));

            if (this.reanalyzeSession) {
                logger.println("reanalyze session ...");
                boolean reanalyzeFinished = connection.reanalyzeSessionStatus(sessionName);
                if (connection.reanalyzeSession(sessionName)) {
                    int timeout = reanalyzeSessionTimeout;
                    while ((!reanalyzeFinished) && (timeout > 0)) {
                        logger.println("querying session analysis status");
                        try {
                            Thread.sleep(reanalyzeSessionPollingInterval);
                            timeout -= reanalyzeSessionPollingInterval;
                        } catch (InterruptedException ignored) {
                        }
                        reanalyzeFinished = connection.reanalyzeSessionStatus(sessionName);
                    }
                    if (reanalyzeFinished) {
                        logger.println("session reanalysis finished");
                        return true;
                    } else {
                        throw new RESTErrorException("Timeout raised");
                    }
                }
            }
            return true;
        } catch (RESTErrorException e) {
            logger.println(String.format("Failed to stop Dynatrace Session recording on %s: %s", dtRecorder.getProfile(), e));
            return !dtRecorder.isTechnicalFailure();
        }
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public DescriptorImpl() {
            load();
        }

        public static boolean getDefaultReanalyzeSession() {
            return false;
        }

        public boolean isApplicable(final Class<? extends AbstractProject> aClass) {
            return true;
        }

        public String getDisplayName() {
            return Messages.PerfSigStopRecording_DisplayName();
        }
    }
}
