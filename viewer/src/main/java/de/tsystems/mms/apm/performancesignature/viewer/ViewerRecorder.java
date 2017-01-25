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

import de.tsystems.mms.apm.performancesignature.dynatrace.model.DashboardReport;
import de.tsystems.mms.apm.performancesignature.ui.PerfSigBuildAction;
import de.tsystems.mms.apm.performancesignature.util.PerfSigUIUtils;
import de.tsystems.mms.apm.performancesignature.viewer.model.JenkinsServerConfiguration;
import de.tsystems.mms.apm.performancesignature.viewer.rest.JenkinsServerConnection;
import de.tsystems.mms.apm.performancesignature.viewer.rest.RESTErrorException;
import de.tsystems.mms.apm.performancesignature.viewer.util.ViewerUtils;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.util.ListBoxModel;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class ViewerRecorder extends Recorder implements SimpleBuildStep {
    private final String jenkinsJob;
    private int nonFunctionalFailure;

    @DataBoundConstructor
    public ViewerRecorder(final String jenkinsJob) {
        this.jenkinsJob = jenkinsJob;
    }

    @Override
    public void perform(@Nonnull final Run<?, ?> run, @Nonnull final FilePath workspace, @Nonnull final Launcher launcher, @Nonnull final TaskListener listener)
            throws InterruptedException, IOException {
        PrintStream logger = listener.getLogger();
        JenkinsServerConnection serverConnection = ViewerUtils.createJenkinsServerConnection(jenkinsJob);

        ViewerEnvInvisAction envInvisAction = run.getAction(ViewerEnvInvisAction.class);
        int buildNumber;
        if (envInvisAction != null) {
            buildNumber = envInvisAction.getCurrentBuild();
        } else {
            buildNumber = serverConnection.getJenkinsJob().details().getLastBuild().getNumber();
        }

        logger.println("parsing xml data from job " + serverConnection.getJenkinsJob().getName() + " #" + buildNumber);
        final List<DashboardReport> dashboardReports = serverConnection.getDashboardReportsFromXML(buildNumber);
        if (dashboardReports == null) {
            throw new RESTErrorException(Messages.ViewerRecorder_XMLReportError());
        }

        for (DashboardReport dashboardReport : dashboardReports) {
            boolean exportedSession = serverConnection.downloadSession(buildNumber, PerfSigUIUtils.getReportDirectory(run), dashboardReport.getName(), logger);
            if (!exportedSession) {
                logger.println(Messages.ViewerRecorder_SessionDownloadError(dashboardReport.getName()));
            } else {
                logger.println(Messages.ViewerRecorder_SessionDownloadSuccessful(dashboardReport.getName()));
            }

            PerfSigUIUtils.handleIncidents(run, dashboardReport.getIncidents(), logger, nonFunctionalFailure);
        }

        boolean exportedPDFReports = serverConnection.downloadPDFReports(buildNumber, PerfSigUIUtils.getReportDirectory(run), logger);
        if (!exportedPDFReports) {
            logger.println(Messages.ViewerRecorder_ReportDownloadError());
        } else {
            logger.println(Messages.ViewerRecorder_ReportDownloadSuccessful());
        }

        PerfSigBuildAction action = new PerfSigBuildAction(dashboardReports);
        run.addAction(action);
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    public int getNonFunctionalFailure() {
        return nonFunctionalFailure;
    }

    @DataBoundSetter
    public void setNonFunctionalFailure(final int nonFunctionalFailure) {
        this.nonFunctionalFailure = nonFunctionalFailure < 0 ? DescriptorImpl.defaultNonFunctionalFailure : nonFunctionalFailure;
    }

    public String getJenkinsJob() {
        return jenkinsJob;
    }

    @Symbol("pullPerfSigReports")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        public static final int defaultNonFunctionalFailure = 0;
        @Deprecated
        private transient List<JenkinsServerConfiguration> configurations = new ArrayList<JenkinsServerConfiguration>();

        public DescriptorImpl() {
            load();
        }

        @SuppressWarnings("deprecation")
        protected Object readResolve() {
            if (configurations != null && !configurations.isEmpty()) {
                ViewerUtils.getJenkinsConfigurations().addAll(configurations);
            }
            return this;
        }

        public ListBoxModel doFillJenkinsJobItems() {
            return ViewerUtils.listToListBoxModel(ViewerUtils.getJenkinsConfigurations());
        }

        public boolean isApplicable(final Class<? extends AbstractProject> aClass) {
            return true;
        }

        public String getDisplayName() {
            return Messages.ViewerRecorder_DisplayName();
        }
    }
}
