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

package de.tsystems.mms.apm.performancesignature.viewer;

import com.offbytwo.jenkins.model.Job;
import de.tsystems.mms.apm.performancesignature.viewer.model.CredJobPair;
import de.tsystems.mms.apm.performancesignature.viewer.model.JenkinsServerConfiguration;
import de.tsystems.mms.apm.performancesignature.viewer.rest.JenkinsServerConnection;
import de.tsystems.mms.apm.performancesignature.viewer.rest.RESTErrorException;
import de.tsystems.mms.apm.performancesignature.viewer.util.ViewerUtils;
import hudson.AbortException;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.ListBoxModel;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.PrintStream;

public class ViewerStartJob extends Builder implements SimpleBuildStep {
    private final String jenkinsJob;

    @DataBoundConstructor
    public ViewerStartJob(final String jenkinsJob) {
        this.jenkinsJob = jenkinsJob;
    }

    @Override
    public void perform(@Nonnull final Run<?, ?> run, @Nonnull final FilePath workspace, @Nonnull final Launcher launcher, @Nonnull final TaskListener listener)
            throws InterruptedException, IOException {
        final PrintStream logger = listener.getLogger();

        JenkinsServerConfiguration serverConfiguration = ViewerUtils.getServerConfiguration(jenkinsJob);
        if (serverConfiguration == null) {
            throw new AbortException(Messages.ViewerRecorder_FailedToLookupServer());
        }

        CredJobPair pair = serverConfiguration.getCredJobPair(jenkinsJob);
        if (pair == null) {
            throw new AbortException(Messages.ViewerRecorder_FailedToLookupJob());
        }

        JenkinsServerConnection serverConnection = new JenkinsServerConnection(serverConfiguration, pair);
        if (!serverConnection.validateConnection()) {
            throw new RESTErrorException(Messages.ViewerRecorder_ConnectionError());
        }

        logger.println(Messages.ViewerStartJob_TriggeringJenkinsJob(pair.getJenkinsJob()));
        Job perfSigJob = serverConnection.getJenkinsJob();
        perfSigJob.build(true);

        boolean buildInQueue = perfSigJob.details().isInQueue();
        while (buildInQueue) {
            Thread.sleep(ViewerWaitForJob.waitForPollingInterval);
            buildInQueue = perfSigJob.details().isInQueue();
        }
        Thread.sleep(20000);

        int buildNumber = perfSigJob.details().getLastBuild().getNumber();
        run.addAction(new ViewerEnvInvisAction(buildNumber));
        logger.println(Messages.ViewerStartJob_JenkinsJobStarted(perfSigJob.getName(), String.valueOf(buildNumber)));
    }

    public String getJenkinsJob() {
        return jenkinsJob;
    }

    @Symbol("triggerJob")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        public ListBoxModel doFillJenkinsJobItems() {
            return ViewerUtils.listToListBoxModel(ViewerUtils.getJenkinsConfigurations());
        }

        public boolean isApplicable(final Class<? extends AbstractProject> aClass) {
            return true;
        }

        public String getDisplayName() {
            return Messages.ViewerStartJob_DisplayName();
        }
    }
}
