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

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernameCredentials;
import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.DashboardReport;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.IncidentChart;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.IncidentViolation;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.DTServerConnection;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.RESTErrorException;
import de.tsystems.mms.apm.performancesignature.model.ConfigurationTestCase;
import de.tsystems.mms.apm.performancesignature.model.ConfigurationTestCase.ConfigurationTestCaseDescriptor;
import de.tsystems.mms.apm.performancesignature.model.CustomProxy;
import de.tsystems.mms.apm.performancesignature.model.Dashboard;
import de.tsystems.mms.apm.performancesignature.util.PerfSigUtils;
import hudson.AbortException;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.*;
import hudson.security.ACL;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import hudson.util.ListBoxModel.Option;
import jenkins.tasks.SimpleBuildStep;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.cloudbees.plugins.credentials.CredentialsMatchers.instanceOf;

public class PerfSigRecorder extends Recorder implements SimpleBuildStep {
    private final String protocol, host, profile, credentialsId;
    private final int port;
    private final List<ConfigurationTestCase> configurationTestCases;
    private boolean verifyCertificate, exportSessions, proxy;
    private int delay, retryCount, nonFunctionalFailure;
    private CustomProxy customProxy;
    private transient List<String> availableSessions;

    @DataBoundConstructor
    public PerfSigRecorder(final String protocol, final String host, final int port, final String credentialsId, final String profile,
                           final List<ConfigurationTestCase> configurationTestCases) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.credentialsId = credentialsId;
        this.profile = profile;
        this.configurationTestCases = configurationTestCases;
        setExportSessions(DescriptorImpl.defaultExportSessions);
    }

    @Deprecated
    public PerfSigRecorder(final String protocol, final String host, final int port, final String credentialsId, final String profile,
                           final boolean verifyCertificate, final boolean exportSessions, final int delay, final int retryCount,
                           final List<ConfigurationTestCase> configurationTestCases,
                           final boolean proxy, final CustomProxy proxySource, final int nonFunctionalFailure) {
        this(protocol, host, port, credentialsId, profile, configurationTestCases);
        setVerifyCertificate(verifyCertificate);
        setExportSessions(exportSessions);
        setDelay(delay);
        setRetryCount(retryCount);
        setNonFunctionalFailure(nonFunctionalFailure);
        setProxy(proxy);
        setCustomProxy(proxySource);
    }

    @Override
    public void perform(@Nonnull final Run<?, ?> run, @Nonnull final FilePath workspace, @Nonnull final Launcher launcher, @Nonnull final TaskListener listener)
            throws InterruptedException, IOException {

        PrintStream logger = listener.getLogger();
        DTServerConnection connection = new DTServerConnection(this.getProtocol(), this.getHost(), this.getPort(), credentialsId,
                verifyCertificate, customProxy);
        logger.println(Messages.PerfSigRecorder_VerifyDTConnection());
        if (!connection.validateConnection()) {
            throw new RESTErrorException(Messages.PerfSigRecorder_DTConnectionError());
        }

        if (configurationTestCases == null) {
            throw new AbortException(Messages.PerfSigRecorder_MissingTestCases());
        }

        if (this.delay != 0) {
            logger.println(Messages.PerfSigRecorder_SleepingDelay() + " " + getDelay() + " sec");
            Thread.sleep(getDelay() * 1000);
        }
        logger.println(Messages.PerfSigRecorder_ReportDirectory() + " " + PerfSigUtils.getReportDirectory(run));

        String sessionName, comparisonSessionName = null, singleFilename, comparisonFilename;
        int comparisonBuildNumber = 0;
        final int buildNumber = run.getNumber();
        final List<DashboardReport> dashboardReports = new ArrayList<DashboardReport>();

        Run<?, ?> previousBuildRun = run.getPreviousNotFailedBuild();
        if (previousBuildRun != null) {
            if (!previousBuildRun.getResult().isCompleteBuild() && run.getPreviousCompletedBuild() != null) {
                previousBuildRun = run.getPreviousCompletedBuild();
            }
            comparisonBuildNumber = previousBuildRun.getNumber();
            logger.println(Messages.PerfSigRecorder_LastSuccessfulBuild() + " #" + comparisonBuildNumber);
        } else {
            logger.println("No previous build found! No comparison possible!");
        }

        for (ConfigurationTestCase configurationTestCase : getConfigurationTestCases()) {
            if (!configurationTestCase.validate()) {
                throw new AbortException(Messages.PerfSigRecorder_TestCaseValidationError());
            }
            logger.println(String.format(Messages.PerfSigRecorder_ConnectionSuccessful(), configurationTestCase.getName()));

            final PerfSigRegisterEnvVars buildEnvVars = getBuildEnvVars(run, configurationTestCase.getName());
            if (buildEnvVars != null) {
                sessionName = buildEnvVars.getSessionName();
            } else {
                throw new RESTErrorException("No sessionname found, aborting ...");
            }

            if (comparisonBuildNumber != 0) {
                final PerfSigRegisterEnvVars otherEnvVars = getBuildEnvVars(previousBuildRun, configurationTestCase.getName());
                if (otherEnvVars != null) {
                    comparisonSessionName = otherEnvVars.getSessionName();
                }
            }

            availableSessions = connection.getSessions();
            int retryCount = 0;
            while ((!validateSessionName(sessionName)) && (retryCount < getRetryCount())) {
                retryCount++;
                availableSessions = connection.getSessions();
                logger.println(String.format(Messages.PerfSigRecorder_WaitingForSession(), retryCount, getRetryCount()));
                Thread.sleep(10000);
            }

            if (!validateSessionName(sessionName)) {
                throw new RESTErrorException(String.format(Messages.PerfSigRecorder_SessionNotAvailable(), sessionName));
            }
            if (comparisonBuildNumber != 0 && !validateSessionName(comparisonSessionName)) {
                logger.println(String.format(Messages.PerfSigRecorder_ComparisonNotPossible(), comparisonSessionName));
            }

            for (Dashboard singleDashboard : configurationTestCase.getSingleDashboards()) {
                singleFilename = "Singlereport_" + sessionName + "_" + singleDashboard.getName() + ".pdf";
                logger.println(Messages.PerfSigRecorder_GettingPDFReport() + " " + singleFilename);
                boolean singleResult = connection.getPDFReport(sessionName, null, singleDashboard.getName(),
                        new File(PerfSigUtils.getReportDirectory(run), File.separator + singleFilename));
                if (!singleResult) {
                    throw new RESTErrorException(Messages.PerfSigRecorder_SingleReportError());
                }
            }
            for (Dashboard comparisonDashboard : configurationTestCase.getComparisonDashboards()) {
                if (comparisonBuildNumber != 0 && comparisonSessionName != null && run.getResult().isBetterThan(Result.FAILURE)) {
                    comparisonFilename = "Comparisonreport_" + comparisonSessionName.replace(comparisonBuildNumber + "_",
                            buildNumber + "_" + comparisonBuildNumber + "_") + "_" + comparisonDashboard.getName() + ".pdf";
                    logger.println(Messages.PerfSigRecorder_GettingPDFReport() + " " + comparisonFilename);
                    boolean comparisonResult = connection.getPDFReport(sessionName, comparisonSessionName, comparisonDashboard.getName(),
                            new File(PerfSigUtils.getReportDirectory(run), File.separator + comparisonFilename));
                    if (!comparisonResult) {
                        throw new RESTErrorException(Messages.PerfSigRecorder_ComparisonReportError());
                    }
                }
            }
            logger.println(Messages.PerfSigRecorder_ParseXMLReport());
            final DashboardReport dashboardReport = connection.getDashboardReportFromXML(configurationTestCase.getXmlDashboard(), sessionName, configurationTestCase.getName());
            if (dashboardReport == null || dashboardReport.getChartDashlets() == null || dashboardReport.getChartDashlets().isEmpty()) {
                throw new RESTErrorException(Messages.PerfSigRecorder_XMLReportError());
            } else {
                dashboardReport.setConfigurationTestCase(configurationTestCase);
                dashboardReports.add(dashboardReport);

                List<IncidentChart> incidents = dashboardReport.getIncidents();
                int numWarning = 0, numSevere = 0;
                if (incidents != null && incidents.size() > 0) {
                    logger.println("Following incidents occured:");
                    for (IncidentChart incident : incidents) {
                        for (IncidentViolation violation : incident.getViolations()) {
                            switch (violation.getSeverity()) {
                                case SEVERE:
                                    logger.println("Severe Incident:     " + incident.getRule() + " " + violation.getRule() + " " + violation.getDescription());
                                    numSevere++;
                                    break;
                                case WARNING:
                                    logger.println("Warning Incident:    " + incident.getRule() + " " + violation.getRule() + " " + violation.getDescription());
                                    numWarning++;
                                    break;
                                default:
                                    break;
                            }
                        }
                    }

                    switch (nonFunctionalFailure) {
                        case 1:
                            if (numSevere > 0) {
                                logger.println("builds status was set to 'failed' due to severe incidents");
                                run.setResult(Result.FAILURE);
                            }
                            break;
                        case 2:
                            if (numSevere > 0 || numWarning > 0) {
                                logger.println("builds status was set to 'failed' due to warning/severe incidents");
                                run.setResult(Result.FAILURE);
                            }
                            break;
                        case 3:
                            if (numSevere > 0) {
                                logger.println("builds status was set to 'unstable' due to severe incidents");
                                run.setResult(Result.UNSTABLE);
                            }
                            break;
                        case 4:
                            if (numSevere > 0 || numWarning > 0) {
                                logger.println("builds status was set to 'unstable' due to warning/severe incidents");
                                run.setResult(Result.UNSTABLE);
                            }
                            break;
                        default:
                            break;
                    }
                }
            }

            if (exportSessions) {
                boolean exportedSession = connection.downloadSession(sessionName, new File(PerfSigUtils.getReportDirectory(run) + File.separator + sessionName + ".dts"));
                if (!exportedSession) {
                    throw new RESTErrorException(Messages.PerfSigRecorder_SessionDownloadError());
                } else {
                    logger.println(Messages.PerfSigRecorder_SessionDownloadSuccessful());
                }
            }
        }

        PerfSigBuildAction action = new PerfSigBuildAction(run, dashboardReports);
        run.addAction(action);
    }

    private PerfSigRegisterEnvVars getBuildEnvVars(final Run<?, ?> build, final String testCase) {
        final List<PerfSigRegisterEnvVars> envVars = build.getActions(PerfSigRegisterEnvVars.class);
        for (PerfSigRegisterEnvVars vars : envVars) {
            if (vars.getTestCase().equals(testCase))
                return vars;
        }
        return null;
    }

    private boolean validateSessionName(final String name) {
        return availableSessions.contains(name);
    }

    @Override
    public Action getProjectAction(final AbstractProject<?, ?> project) {
        return new PerfSigProjectAction(project);
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    public String getProtocol() {
        return this.protocol;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public String getProfile() {
        return this.profile;
    }

    public int getDelay() {
        return this.delay;
    }

    @DataBoundSetter
    public void setDelay(final int delay) {
        this.delay = delay <= 0 ? DescriptorImpl.defaultDelay : delay;
    }

    public int getRetryCount() {
        return retryCount;
    }

    @DataBoundSetter
    public void setRetryCount(final int retryCount) {
        this.retryCount = retryCount <= 0 ? DescriptorImpl.defaultRetryCount : retryCount;
    }

    public CustomProxy getCustomProxy() {
        return customProxy;
    }

    @DataBoundSetter
    public void setCustomProxy(final CustomProxy customProxy) {
        this.customProxy = isProxy() ? customProxy : null;
    }

    public boolean isVerifyCertificate() {
        return verifyCertificate;
    }

    @DataBoundSetter
    public void setVerifyCertificate(final boolean verifyCertificate) {
        this.verifyCertificate = verifyCertificate;
    }

    public boolean isExportSessions() {
        return exportSessions;
    }

    @DataBoundSetter
    public void setExportSessions(final boolean exportSessions) {
        this.exportSessions = exportSessions;
    }

    public boolean isProxy() {
        return proxy;
    }

    @DataBoundSetter
    public void setProxy(final boolean proxy) {
        this.proxy = proxy;
    }

    public List<ConfigurationTestCase> getConfigurationTestCases() {
        return configurationTestCases == null ? Collections.<ConfigurationTestCase>emptyList() : configurationTestCases;
    }

    public String getCredentialsId() {
        return credentialsId;
    }

    public int getNonFunctionalFailure() {
        return nonFunctionalFailure;
    }

    @DataBoundSetter
    public void setNonFunctionalFailure(final int nonFunctionalFailure) {
        this.nonFunctionalFailure = nonFunctionalFailure <= 0 ? DescriptorImpl.defaultNonFunctionalFailure : nonFunctionalFailure;
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        public static final String defaultHost = "localhost";
        public static final int defaultPort = 8021;
        public static final int defaultDelay = 10;
        public static final int defaultRetryCount = 5;
        public static final boolean defaultVerifyCertificate = false;
        public static final boolean defaultExportSessions = true;
        public static final int defaultNonFunctionalFailure = 0;

        protected static boolean checkNotNullOrEmpty(final String string) {
            return StringUtils.isNotBlank(string);
        }

        protected static boolean checkNotEmptyAndIsNumber(final String number) {
            return StringUtils.isNotBlank(number) && NumberUtils.isNumber(number);
        }

        public FormValidation doCheckHost(@QueryParameter final String host) {
            FormValidation validationResult;
            if (checkNotNullOrEmpty(host)) {
                validationResult = FormValidation.ok();
            } else {
                validationResult = FormValidation.error(Messages.PerfSigRecorder_DTHostNotValid());
            }
            return validationResult;
        }

        public FormValidation doCheckPort(@QueryParameter final String port) {
            FormValidation validationResult;
            if (checkNotEmptyAndIsNumber(port)) {
                validationResult = FormValidation.ok();
            } else {
                validationResult = FormValidation.error(Messages.PerfSigRecorder_DTPortNotValid());
            }
            return validationResult;
        }

        public FormValidation doCheckCredentialsId(@QueryParameter final String credentialsId) {
            FormValidation validationResult;
            if (checkNotNullOrEmpty(credentialsId)) {
                validationResult = FormValidation.ok();
            } else {
                validationResult = FormValidation.error(Messages.PerfSigRecorder_DTUserEmpty());
            }
            return validationResult;
        }

        public FormValidation doCheckProfile(@QueryParameter final String profile) {
            FormValidation validationResult;
            if (checkNotNullOrEmpty(profile)) {
                validationResult = FormValidation.ok();
            } else {
                validationResult = FormValidation.error(Messages.PerfSigRecorder_DTProfileNotValid());
            }
            return validationResult;
        }

        public FormValidation doCheckDelay(@QueryParameter final String delay) {
            FormValidation validationResult;
            if (checkNotEmptyAndIsNumber(delay)) {
                validationResult = FormValidation.ok();
            } else {
                validationResult = FormValidation.error(Messages.PerfSigRecorder_DelayNotValid());
            }
            return validationResult;
        }

        public FormValidation doCheckRetryCount(@QueryParameter final String retryCount) {
            FormValidation validationResult;
            if (checkNotEmptyAndIsNumber(retryCount)) {
                validationResult = FormValidation.ok();
            } else {
                validationResult = FormValidation.error(Messages.PerfSigRecorder_RetryCountNotValid());
            }
            return validationResult;
        }

        public FormValidation doTestDynaTraceConnection(@QueryParameter final String protocol, @QueryParameter final String host,
                                                        @QueryParameter final int port, @QueryParameter final String credentialsId,
                                                        @QueryParameter final boolean verifyCertificate, @QueryParameter final boolean proxy,
                                                        @QueryParameter final int proxySource,
                                                        @QueryParameter final String proxyServer, @QueryParameter final int proxyPort,
                                                        @QueryParameter final String proxyUser, @QueryParameter final String proxyPassword) {

            CustomProxy customProxyServer = null;
            if (proxy) {
                customProxyServer = new CustomProxy(proxyServer, proxyPort, proxyUser, proxyPassword, proxySource == 0);
            }
            final DTServerConnection connection = new DTServerConnection(protocol, host, port, credentialsId, verifyCertificate, customProxyServer);

            if (connection.validateConnection()) {
                return FormValidation.ok(Messages.PerfSigRecorder_TestConnectionSuccessful());
            } else {
                return FormValidation.warning(Messages.PerfSigRecorder_TestConnectionNotSuccessful());
            }
        }

        public boolean isApplicable(final Class<? extends AbstractProject> aClass) {
            return true;
        }

        public String getDisplayName() {
            return Messages.PerfSigRecorder_DisplayName();
        }

        public ListBoxModel doFillProtocolItems() {
            return new ListBoxModel(new Option("https"), new Option("http"));
        }

        public ListBoxModel doFillProfileItems(@QueryParameter final String protocol, @QueryParameter final String host,
                                               @QueryParameter final int port, @QueryParameter final String credentialsId,
                                               @QueryParameter final boolean verifyCertificate, @QueryParameter final boolean proxy,
                                               @QueryParameter final int proxySource,
                                               @QueryParameter final String proxyServer, @QueryParameter final int proxyPort,
                                               @QueryParameter final String proxyUser, @QueryParameter final String proxyPassword) {

            CustomProxy customProxyServer = null;
            if (proxy) {
                customProxyServer = new CustomProxy(proxyServer, proxyPort, proxyUser, proxyPassword, proxySource == 0);
            }
            final DTServerConnection connection = new DTServerConnection(protocol, host, port, credentialsId, verifyCertificate, customProxyServer);
            return PerfSigUtils.listToListBoxModel(connection.getSystemProfiles());
        }

        public ListBoxModel doFillCredentialsIdItems(@AncestorInPath final Project project) {
            return new StandardListBoxModel()
                    .withEmptySelection()
                    .withMatching(instanceOf(UsernamePasswordCredentials.class),
                            CredentialsProvider.lookupCredentials(
                                    StandardUsernameCredentials.class, project, ACL.SYSTEM, Collections.<DomainRequirement>emptyList()));
        }

        public List<ConfigurationTestCaseDescriptor> getTestCaseTypes(final AbstractProject<?, ?> project) {
            return ConfigurationTestCaseDescriptor.all((Class<? extends AbstractProject<?, ?>>) project.getClass());
        }

        public List<ConfigurationTestCaseDescriptor> getTestCaseTypes() {
            return ConfigurationTestCaseDescriptor.all();
        }
    }
}
