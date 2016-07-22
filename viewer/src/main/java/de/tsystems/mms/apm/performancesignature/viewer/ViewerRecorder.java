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
import de.tsystems.mms.apm.performancesignature.dynatrace.model.IncidentChart;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.IncidentViolation;
import de.tsystems.mms.apm.performancesignature.ui.PerfSigBuildAction;
import de.tsystems.mms.apm.performancesignature.viewer.model.CredJobPair;
import de.tsystems.mms.apm.performancesignature.viewer.model.JenkinsServerConfiguration;
import de.tsystems.mms.apm.performancesignature.viewer.rest.RESTErrorException;
import de.tsystems.mms.apm.performancesignature.viewer.rest.ServerConnection;
import de.tsystems.mms.apm.performancesignature.viewer.util.ViewerUtils;
import hudson.AbortException;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.util.ListBoxModel;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;

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

        JenkinsServerConfiguration serverConfiguration = ViewerUtils.getServerConfiguration(jenkinsJob);
        if (serverConfiguration == null)
            throw new AbortException("failed to lookup Jenkins server configuration");

        CredJobPair pair = serverConfiguration.getCredJobPair(jenkinsJob);
        if (pair == null)
            throw new AbortException("failed to lookup Jenkins job");

        ServerConnection serverConnection = new ServerConnection(serverConfiguration, pair);
        if (!serverConnection.validateConnection()) {
            throw new RESTErrorException(Messages.PerfSigRecorder_DTConnectionError());
        }

        logger.println(Messages.PerfSigRecorder_ParseXMLReport());
        final List<DashboardReport> dashboardReports = serverConnection.getDashboardReportsFromXML();
        if (dashboardReports == null)
            throw new RESTErrorException(Messages.PerfSigRecorder_XMLReportError());

        for (DashboardReport dashboardReport : dashboardReports) {
            List<IncidentChart> incidents = dashboardReport.getIncidents();
            int numWarning = 0, numSevere = 0;
            if (incidents != null && incidents.size() > 0) {
                logger.println("following incidents occured:");
                for (IncidentChart incident : incidents) {
                    for (IncidentViolation violation : incident.getViolations()) {
                        switch (violation.getSeverity()) {
                            case SEVERE:
                                logger.println("severe incident:     " + incident.getRule() + " " + violation.getRule() + " " + violation.getDescription());
                                numSevere++;
                                break;
                            case WARNING:
                                logger.println("warning incident:    " + incident.getRule() + " " + violation.getRule() + " " + violation.getDescription());
                                numWarning++;
                                break;
                            default:
                                break;
                        }
                    }
                }
            }

            switch (nonFunctionalFailure) {
                case 1:
                    if (numSevere > 0) {
                        logger.println("build's status was set to 'failed' due to severe incidents");
                        run.setResult(Result.FAILURE);
                    }
                    break;
                case 2:
                    if (numSevere > 0 || numWarning > 0) {
                        logger.println("build's status was set to 'failed' due to warning/severe incidents");
                        run.setResult(Result.FAILURE);
                    }
                    break;
                case 3:
                    if (numSevere > 0) {
                        logger.println("build's status was set to 'unstable' due to severe incidents");
                        run.setResult(Result.UNSTABLE);
                    }
                    break;
                case 4:
                    if (numSevere > 0 || numWarning > 0) {
                        logger.println("build's status was set to 'unstable' due to warning/severe incidents");
                        run.setResult(Result.UNSTABLE);
                    }
                    break;
                default:
                    break;
            }

            boolean exportedPDFReports = serverConnection.downloadPDFReports(ViewerUtils.getReportDirectory(run), dashboardReport.getName());
            if (!exportedPDFReports) {
                throw new RESTErrorException("failed to download Dynatrace PDF report, build status set to failed");
            } else {
                logger.println("PDF Report successfully downloaded");
            }

            boolean exportedSession = serverConnection.downloadSessions(ViewerUtils.getReportDirectory(run), dashboardReport.getName());
            if (!exportedSession) {
                throw new RESTErrorException(Messages.PerfSigRecorder_SessionDownloadError());
            } else {
                logger.println(Messages.PerfSigRecorder_SessionDownloadSuccessful());
            }
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

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        public static final int defaultNonFunctionalFailure = 0;
        private List<JenkinsServerConfiguration> configurations = new ArrayList<JenkinsServerConfiguration>();

        public DescriptorImpl() {
            load();
        }

        @Override
        public boolean configure(final StaplerRequest req, final JSONObject formData) throws FormException {
            configurations = req.bindJSONToList(JenkinsServerConfiguration.class, formData.get("configurations"));
            save();
            return false;
        }

        public ListBoxModel doFillJenkinsJobItems() {
            return ViewerUtils.listToListBoxModel(ViewerUtils.getJenkinsConfigurations());
        }

        public List<JenkinsServerConfiguration> getConfigurations() {
            return configurations;
        }

        public boolean isApplicable(final Class<? extends AbstractProject> aClass) {
            return true;
        }

        public String getDisplayName() {
            return Messages.PerfSigRecorder_DisplayName();
        }
    }
}
