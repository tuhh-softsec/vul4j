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
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.DTServerConnection;
import de.tsystems.mms.apm.performancesignature.model.ConfigurationTestCase;
import de.tsystems.mms.apm.performancesignature.model.ConfigurationTestCase.ConfigurationTestCaseDescriptor;
import de.tsystems.mms.apm.performancesignature.model.Dashboard;
import de.tsystems.mms.apm.performancesignature.model.GeneralTestCase;
import de.tsystems.mms.apm.performancesignature.model.ProxyBlock;
import de.tsystems.mms.apm.performancesignature.util.PerfSigUtils;
import hudson.Extension;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.cloudbees.plugins.credentials.CredentialsMatchers.instanceOf;

public class PerfSigRecorder extends Recorder {
    private final String protocol, host, profile;
    private final boolean verifyCertificate, exportSessions, useJenkinsProxy, modifyBuildResult;
    private final int delay, retryCount, port;
    private final List<ConfigurationTestCase> configurationTestCases;
    private final ProxyBlock customProxy;
    private String credentialsId;
    private List<String> availableSessions;

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public PerfSigRecorder(final String protocol, final String host, final String credentialsId, final int port, final String profile,
                           final boolean verifyCertificate, final boolean exportSessions, final int delay, final int retryCount,
                           final List<ConfigurationTestCase> configurationTestCases, final ProxyBlock customProxy,
                           final boolean useJenkinsProxy, final boolean modifyBuildResult) {
        this.protocol = protocol;
        this.host = host;
        this.credentialsId = credentialsId;
        this.port = port;
        this.modifyBuildResult = modifyBuildResult;
        this.profile = profile;
        this.verifyCertificate = verifyCertificate;
        this.exportSessions = exportSessions;
        this.delay = delay;
        this.retryCount = retryCount;
        this.configurationTestCases = configurationTestCases;
        this.customProxy = customProxy;
        this.useJenkinsProxy = useJenkinsProxy;
    }

    @Override
    public boolean perform(final AbstractBuild build, final Launcher launcher, final BuildListener listener) throws InterruptedException, IOException {
        // This is where you 'build' the project.
        final PrintStream logger = listener.getLogger();

        final DTServerConnection connection = new DTServerConnection(this.getProtocol(), this.getHost(), this.getPort(), credentialsId,
                verifyCertificate, useJenkinsProxy, customProxy);
        logger.println(Messages.DTPerfSigRecorder_VerifyDTConnection());
        if (!connection.validateConnection()) {
            logger.println(Messages.DTPerfSigRecorder_DTConnectionError());
            checkForUnstableResult(build);
            return !isModifyBuildResult();
        }

        if (configurationTestCases == null) {
            logger.println(Messages.DTPerfSigRecorder_MissingTestCases());
        }

        if (this.delay != 0) {
            logger.println(Messages.DTPerfSigRecorder_SleepingDelay() + " " + this.getDelay() + " sec");
            Thread.sleep(this.getDelay() * 1000);
        }
        logger.println(Messages.DTPerfSigRecorder_ReportDirectory() + " " + PerfSigUtils.getReportDirectory(build));

        String sessionName, comparisonSessionName = null, singleFilename, comparisonFilename;
        int comparisonBuildNumber = 0;
        final int buildNumber = build.getNumber();
        final List<DashboardReport> dashboardReports = new ArrayList<DashboardReport>();

        final Run previousBuildRun = build.getPreviousNotFailedBuild();
        if (previousBuildRun != null) {
            comparisonBuildNumber = previousBuildRun.getNumber();
            logger.println(Messages.DTPerfSigRecorder_LastSuccessfulBuild() + " " + comparisonBuildNumber);
        } else {
            logger.println("No previous not failed build found! No comparison possible!");
        }

        for (ConfigurationTestCase configurationTestCase : getConfigurationTestCases()) {
            if (!configurationTestCase.validate()) {
                logger.println(Messages.DTPerfSigRecorder_TestCaseValidationError());
                checkForUnstableResult(build);
                return !isModifyBuildResult();
            }
            logger.println(String.format(Messages.DTPerfSigRecorder_ConnectionSuccessful(), configurationTestCase.getName()));

            PerfSigRegisterEnvVars buildEnvVars = getBuildEnvVars(build, configurationTestCase.getName());
            if (buildEnvVars != null) {
                sessionName = buildEnvVars.getSessionName();
            } else {
                logger.println("No sessionname found, aborting ...");
                checkForUnstableResult(build);
                return !isModifyBuildResult();
            }

            if (comparisonBuildNumber != 0) {
                PerfSigRegisterEnvVars otherEnvVars = getBuildEnvVars(previousBuildRun, configurationTestCase.getName());
                if (otherEnvVars != null) {
                    comparisonSessionName = otherEnvVars.getSessionName();
                } else {
                    comparisonBuildNumber = 0;
                }
            }

            try {
                availableSessions = connection.getSessions();
                int retryCount = 0;
                while ((!validateSessionName(sessionName)) && (retryCount < getRetryCount())) {
                    retryCount++;
                    availableSessions = connection.getSessions();
                    logger.println(String.format(Messages.DTPerfSigRecorder_WaitingForSession(), retryCount, getRetryCount()));
                    Thread.sleep(10000);
                }
            } catch (Exception e) {
                logger.println(e);
                return !isModifyBuildResult();
            }

            if (!validateSessionName(sessionName)) {
                logger.println(String.format(Messages.DTPerfSigRecorder_SessionNotAvailable(), sessionName));
                checkForUnstableResult(build);
                continue;
            }
            if (comparisonBuildNumber != 0 && !validateSessionName(comparisonSessionName)) {
                logger.println(String.format(Messages.DTPerfSigRecorder_ComparisonNotPossible(), comparisonSessionName));
            }

            try {
                for (Dashboard singleDashboard : ((GeneralTestCase) configurationTestCase).getSingleDashboards()) {
                    singleFilename = "Singlereport_" + sessionName + "_" + singleDashboard.getName() + ".pdf";
                    logger.println(Messages.DTPerfSigRecorder_GettingPDFReport() + " " + singleFilename);
                    boolean singleResult = connection.getPDFReport(sessionName, null, singleDashboard.getName(),
                            new File(PerfSigUtils.getReportDirectory(build) + File.separator + singleFilename));
                    if (!singleResult) {
                        logger.println(Messages.DTPerfSigRecorder_SingleReportError());
                        if (isModifyBuildResult()) build.setResult(Result.FAILURE);
                    }
                }
                for (Dashboard comparisonDashboard : ((GeneralTestCase) configurationTestCase).getComparisonDashboards()) {
                    if (comparisonBuildNumber != 0 && getBuildResult(build).isBetterThan(Result.FAILURE)) {
                        comparisonFilename = "Comparisonreport_" + comparisonSessionName.replace(comparisonBuildNumber + "_",
                                buildNumber + "_" + comparisonBuildNumber + "_") + "_" + comparisonDashboard.getName() + ".pdf";
                        logger.println(Messages.DTPerfSigRecorder_GettingPDFReport() + " " + comparisonFilename);
                        boolean comparisonResult = connection.getPDFReport(sessionName, comparisonSessionName, comparisonDashboard.getName(),
                                new File(PerfSigUtils.getReportDirectory(build) + File.separator + comparisonFilename));
                        if (!comparisonResult) {
                            logger.println(Messages.DTPerfSigRecorder_ComparisonReportError());
                            if (isModifyBuildResult()) build.setResult(Result.FAILURE);
                        }
                    }
                }
                logger.println(Messages.DTPerfSigRecorder_ParseXMLReport());
                DashboardReport dashboardReport = connection.getDashboardReportFromXML(((GeneralTestCase) configurationTestCase).getXmlDashboard(), sessionName, configurationTestCase.getName());
                if (dashboardReport == null || dashboardReport.getChartDashlets() == null || dashboardReport.getChartDashlets().isEmpty()) {
                    logger.println(Messages.DTPerfSigRecorder_XMLReportError());
                    if (isModifyBuildResult()) build.setResult(Result.FAILURE);
                } else {
                    dashboardReport.setConfigurationTestCase(configurationTestCase);
                    dashboardReports.add(dashboardReport);
                    logger.println(String.format(Messages.DTPerfSigRecorder_XMLReportResults(), dashboardReport.getChartDashlets().size(), " " + configurationTestCase.getName()));
                }

                if (exportSessions) {
                    boolean exportedSession = connection.downloadSession(sessionName, new File(PerfSigUtils.getReportDirectory(build) + File.separator + sessionName + ".dts"));
                    if (!exportedSession) {
                        logger.println(Messages.DTPerfSigRecorder_SessionDownloadError());
                        if (isModifyBuildResult()) build.setResult(Result.FAILURE);
                    } else {
                        logger.println(Messages.DTPerfSigRecorder_SessionDownloadSuccessful());
                    }
                }
            } catch (Exception e) {
                logger.println(e);
                return !isModifyBuildResult();
            }
        }

        PerfSigBuildAction action = new PerfSigBuildAction(build, dashboardReports);
        build.addAction(action);
        return true;
    }

    private PerfSigRegisterEnvVars getBuildEnvVars(final Run build, final String testCase) {
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

    private void checkForUnstableResult(final AbstractBuild build) {
        if (getBuildResult(build).isBetterOrEqualTo(Result.UNSTABLE) && isModifyBuildResult()) {
            build.setResult(Result.FAILURE);
        }
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

    public int getRetryCount() {
        return retryCount;
    }

    public boolean isUseJenkinsProxy() {
        return useJenkinsProxy;
    }

    public ProxyBlock getCustomProxy() {
        return customProxy;
    }

    public boolean isVerifyCertificate() {
        return verifyCertificate;
    }

    public boolean isExportSessions() {
        return exportSessions;
    }

    public boolean isModifyBuildResult() {
        return modifyBuildResult;
    }

    private Result getBuildResult(final AbstractBuild build) {
        Result result = build.getResult();
        if (result == null) {
            throw new IllegalStateException("build is ongoing");
        }
        return result;
    }

    @Nonnull
    public List<ConfigurationTestCase> getConfigurationTestCases() {
        return configurationTestCases == null ? Collections.<ConfigurationTestCase>emptyList() : configurationTestCases;
    }

    public String getCredentialsId() {
        return credentialsId;
    }

    /**
     * Descriptor for {@link PerfSigRecorder}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     * <p>
     * <p>
     * See <tt>src/main/resources/hudson/plugins/hello_world/PerfSigRecorder/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */
    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        /**
         * In order to load the persisted global configuration, you have to
         * call load() in the constructor.
         */
        public DescriptorImpl() {
            load();
        }

        public static String getDefaultHost() {
            return Messages.DTPerfSigRecorder_DefaultAddress();
        }

        public static int getDefaultPort() {
            return Integer.parseInt(Messages.DTPerfSigRecorder_DefaultPort());
        }

        @SuppressWarnings("unused")
        public static int getDefaultDelay() {
            return Integer.parseInt(Messages.DTPerfSigRecorder_DefaultDelay());
        }

        @SuppressWarnings("unused")
        public static int getDefaultRetryCount() {
            return Integer.parseInt(Messages.DTPerfSigRecorder_DefaultRetryCount());
        }

        @SuppressWarnings("unused")
        public static boolean getDefaultVerifyCertificate() {
            return Boolean.valueOf("false");
        }

        @SuppressWarnings("unused")
        public static boolean getDefaultExportSessions() {
            return Boolean.valueOf(Messages.DTPerfSigRecorder_DefaultExportSessions());
        }

        @SuppressWarnings("unused")
        public static boolean getDefaultModifyBuildResult() {
            return Boolean.valueOf(Messages.DTPerfSigRecorder_DefaultModifyBuildResult());
        }

        protected static boolean checkNotNullOrEmpty(final String string) {
            return StringUtils.isNotBlank(string);
        }

        protected static boolean checkNotEmptyAndIsNumber(final String number) {
            return StringUtils.isNotBlank(number) && NumberUtils.isNumber(number);
        }

        @SuppressWarnings("unused")
        public FormValidation doCheckHost(@QueryParameter final String host) {
            FormValidation validationResult;
            if (checkNotNullOrEmpty(host)) {
                validationResult = FormValidation.ok();
            } else {
                validationResult = FormValidation.error(Messages.DTPerfSigRecorder_DTHostNotValid());
            }
            return validationResult;
        }

        @SuppressWarnings("unused")
        public FormValidation doCheckPort(@QueryParameter final String port) {
            FormValidation validationResult;
            if (checkNotEmptyAndIsNumber(port)) {
                validationResult = FormValidation.ok();
            } else {
                validationResult = FormValidation.error(Messages.DTPerfSigRecorder_DTPortNotValid());
            }
            return validationResult;
        }

        @SuppressWarnings("unused")
        public FormValidation doCheckCredentialsId(@QueryParameter final String credentialsId) {
            FormValidation validationResult;
            if (checkNotNullOrEmpty(credentialsId)) {
                validationResult = FormValidation.ok();
            } else {
                validationResult = FormValidation.error(Messages.DTPerfSigRecorder_DTUserEmpty());
            }
            return validationResult;
        }

        @SuppressWarnings("unused")
        public FormValidation doCheckProfile(@QueryParameter final String profile) {
            FormValidation validationResult;
            if (checkNotNullOrEmpty(profile)) {
                validationResult = FormValidation.ok();
            } else {
                validationResult = FormValidation.error(Messages.DTPerfSigRecorder_DTProfileNotValid());
            }
            return validationResult;
        }

        @SuppressWarnings("unused")
        public FormValidation doCheckDelay(@QueryParameter final String delay) {
            FormValidation validationResult;
            if (checkNotEmptyAndIsNumber(delay)) {
                validationResult = FormValidation.ok();
            } else {
                validationResult = FormValidation.error(Messages.DTPerfSigRecorder_DelayNotValid());
            }
            return validationResult;
        }

        @SuppressWarnings("unused")
        public FormValidation doCheckRetryCount(@QueryParameter final String retryCount) {
            FormValidation validationResult;
            if (checkNotEmptyAndIsNumber(retryCount)) {
                validationResult = FormValidation.ok();
            } else {
                validationResult = FormValidation.error(Messages.DTPerfSigRecorder_RetryCountNotValid());
            }
            return validationResult;
        }

        @SuppressWarnings("unused")
        public FormValidation doTestDynaTraceConnection(@QueryParameter("protocol") final String protocol, @QueryParameter("host") final String host,
                                                        @QueryParameter("port") final int port, @QueryParameter("credentialsId") final String credentialsId,
                                                        @QueryParameter("verifyCertificate") final boolean verifyCertificate, @QueryParameter("useJenkinsProxy") final boolean useJenkinsProxy,
                                                        @QueryParameter("proxyServer") final String proxyServer, @QueryParameter("proxyPort") final int proxyPort,
                                                        @QueryParameter("proxyUser") final String proxyUser, @QueryParameter("proxyPassword") final String proxyPassword) {

            ProxyBlock proxy = null;
            if (StringUtils.isNotBlank(proxyServer) && proxyPort > 0) {
                proxy = new ProxyBlock(proxyServer, proxyPort, proxyUser, proxyPassword);
            }
            final DTServerConnection connection = new DTServerConnection(protocol, host, port, credentialsId, verifyCertificate, useJenkinsProxy, proxy);

            if (connection.validateConnection()) {
                return FormValidation.ok(Messages.DTPerfSigRecorder_TestConnectionSuccessful());
            } else {
                return FormValidation.warning(Messages.DTPerfSigRecorder_TestConnectionNotSuccessful());
            }
        }

        public boolean isApplicable(final Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return Messages.DTPerfSigRecorder_DisplayName();
        }

        @SuppressWarnings("unused")
        public ListBoxModel doFillProtocolItems() {
            return new ListBoxModel(new Option("https"), new Option("http"));
        }

        @SuppressWarnings("unused")
        public ListBoxModel doFillProfileItems(@QueryParameter("protocol") final String protocol, @QueryParameter("host") final String host,
                                               @QueryParameter("port") final int port, @QueryParameter("credentialsId") final String credentialsId,
                                               @QueryParameter("verifyCertificate") final boolean verifyCertificate, @QueryParameter("useJenkinsProxy") final boolean useJenkinsProxy,
                                               @QueryParameter("proxyServer") final String proxyServer, @QueryParameter("proxyPort") final int proxyPort,
                                               @QueryParameter("proxyUser") final String proxyUser, @QueryParameter("proxyPassword") final String proxyPassword) {

            ProxyBlock proxy = null;
            if (StringUtils.isNotBlank(proxyServer) && proxyPort > 0) {
                proxy = new ProxyBlock(proxyServer, proxyPort, proxyUser, proxyPassword);
            }
            final DTServerConnection newConnection = new DTServerConnection(protocol, host, port, credentialsId, verifyCertificate, useJenkinsProxy, proxy);
            return PerfSigUtils.listToListBoxModel(newConnection.getProfiles());
        }

        public ListBoxModel doFillCredentialsIdItems(@AncestorInPath final Project project) {
            return new StandardListBoxModel()
                    .withEmptySelection()
                    .withMatching(instanceOf(UsernamePasswordCredentials.class),
                            CredentialsProvider.lookupCredentials(
                                    StandardUsernameCredentials.class, project, ACL.SYSTEM, Collections.<DomainRequirement>emptyList()));
        }

        @SuppressWarnings("unchecked")
        public List<ConfigurationTestCaseDescriptor> getTestCaseTypes(final AbstractProject<?, ?> project) {
            return ConfigurationTestCaseDescriptor.all((Class<? extends AbstractProject<?, ?>>) project.getClass());
        }

        public List<ConfigurationTestCaseDescriptor> getTestCaseTypes() {
            return ConfigurationTestCaseDescriptor.all();
        }
    }
}
