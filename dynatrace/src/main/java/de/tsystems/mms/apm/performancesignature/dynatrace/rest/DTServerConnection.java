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

package de.tsystems.mms.apm.performancesignature.dynatrace.rest;

import de.tsystems.mms.apm.performancesignature.dynatrace.configuration.CredProfilePair;
import de.tsystems.mms.apm.performancesignature.dynatrace.configuration.CustomProxy;
import de.tsystems.mms.apm.performancesignature.dynatrace.configuration.DynatraceServerConfiguration;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.Alert;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.DashboardReport;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.TestRun;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.ApiClient;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.ApiException;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.api.*;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model.*;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.xml.CommandExecutionException;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.xml.ContentRetrievalException;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.xml.model.*;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.xml.model.Result;
import de.tsystems.mms.apm.performancesignature.util.PerfSigUIUtils;
import hudson.FilePath;
import hudson.ProxyConfiguration;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class DTServerConnection {
    private static final Logger LOGGER = Logger.getLogger(DTServerConnection.class.getName());
    private final String systemProfile;
    private final ApiClient apiClient;
    private final CredProfilePair credProfilePair;
    private DynatraceServerConfiguration configuration;

    public DTServerConnection(final DynatraceServerConfiguration config, final CredProfilePair pair) {
        this(config.getServerUrl(), pair, config.isVerifyCertificate(), config.getCustomProxy());
        this.configuration = config;
    }

    public DTServerConnection(final String serverUrl, final CredProfilePair pair, final boolean verifyCertificate, final CustomProxy customProxy) {
        this.systemProfile = pair.getProfile();
        this.credProfilePair = pair;

        this.apiClient = new ApiClient();
        apiClient.setVerifyingSsl(verifyCertificate);
        apiClient.setBasePath(serverUrl);
        apiClient.setUsername(pair.getCredentials().getUsername());
        apiClient.setPassword(pair.getCredentials().getPassword().getPlainText());
        //apiClient.setDebugging(true);
        //ToDo: make this configurable
        apiClient.getHttpClient().setReadTimeout(300, TimeUnit.SECONDS);

        Proxy proxy = Proxy.NO_PROXY;
        if (customProxy != null) {
            Jenkins jenkins = PerfSigUIUtils.getInstance();
            ProxyConfiguration proxyConfiguration = jenkins.proxy;
            if (customProxy.isUseJenkinsProxy() && proxyConfiguration != null) {
                proxy = createProxy(proxyConfiguration.name, proxyConfiguration.port, proxyConfiguration.getUserName(), proxyConfiguration.getPassword());
            } else {
                proxy = createProxy(customProxy.getProxyServer(), customProxy.getProxyPort(), customProxy.getProxyUser(), customProxy.getProxyPassword());
            }
        }
        apiClient.getHttpClient().setProxy(proxy);
    }

    private Proxy createProxy(String host, int port, final String proxyUser, final String proxyPassword) {
        Proxy proxy = Proxy.NO_PROXY;
        if (StringUtils.isNotBlank(host) && port > 0) {
            proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
            if (StringUtils.isNotBlank(proxyUser)) {
                Authenticator authenticator = new Authenticator() {
                    public PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(proxyUser, proxyPassword.toCharArray());
                    }
                };
                Authenticator.setDefault(authenticator);
            }
        }
        return proxy;
    }

    public DynatraceServerConfiguration getConfiguration() {
        return configuration;
    }

    public CredProfilePair getCredProfilePair() {
        return credProfilePair;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public DashboardReport getDashboardReportFromXML(final String dashBoardName, final String sessionId, final String testCaseName) {
        CustomXMLApi api = new CustomXMLApi(apiClient);
        try {
            DashboardReport dashboardReport = api.getXMLDashboard(dashBoardName, sessionId);
            dashboardReport.setName(testCaseName);
            return dashboardReport;
        } catch (Exception ex) {
            throw new ContentRetrievalException(ExceptionUtils.getStackTrace(ex) + "could not retrieve records from Dynatrace server: " + dashBoardName, ex);
        }
    }

    public boolean validateConnection() {
        try {
            getServerVersion();
            return true;
        } catch (CommandExecutionException e) {
            LOGGER.severe(ExceptionUtils.getFullStackTrace(e));
            return false;
        }
    }

    public String getServerVersion() {
        ServerManagementApi api = new ServerManagementApi(apiClient);
        try {
            return api.getVersion().getResult();
        } catch (ApiException ex) {
            throw new CommandExecutionException("error getting version of server: " + ex.getMessage(), ex);
        }
    }

    public String storeSession(final String sessionName, final Date timeframeStart, final Date timeframeEnd, final String recordingOption,
                               final boolean sessionLocked, final boolean appendTimestamp) {
        LiveSessionsApi api = new LiveSessionsApi(apiClient);
        try {
            SessionStoringOptions options = new SessionStoringOptions(sessionName, "Session recorded by Jenkins", appendTimestamp,
                    recordingOption, sessionLocked, apiClient.formatDatetime(timeframeStart), apiClient.formatDatetime(timeframeEnd));

            return api.storeSession(systemProfile, options);
        } catch (ApiException ex) {
            throw new CommandExecutionException("error while storing purepaths: " + ex.getMessage(), ex);
        }
    }

    public String startRecording(final String sessionName, final String description, final String recordingOption,
                                 final boolean sessionLocked, final boolean appendTimestamp) {
        LiveSessionsApi api = new LiveSessionsApi(apiClient);
        try {
            SessionRecordingOptions options = new SessionRecordingOptions(sessionName, description, appendTimestamp, recordingOption, sessionLocked);
            return api.postRecording(systemProfile, options);
        } catch (ApiException ex) {
            throw new CommandExecutionException("error while starting session recording: " + ex.getMessage(), ex);
        }
    }

    public String stopRecording() {
        LiveSessionsApi api = new LiveSessionsApi(apiClient);
        try {
            return api.stopRecording(systemProfile, new RecordingStatus(false));
        } catch (ApiException ex) {
            throw new CommandExecutionException("error while stopping session recording: " + ex.getMessage(), ex);
        }
    }

    public boolean getRecordingStatus() {
        LiveSessionsApi api = new LiveSessionsApi(apiClient);
        try {
            return api.getRecording(systemProfile).getRecording();
        } catch (ApiException ex) {
            throw new CommandExecutionException("error querying session recording status: " + ex.getMessage(), ex);
        }
    }

    public Sessions getSessions() {
        StoredSessionsApi api = new StoredSessionsApi(apiClient);
        try {
            return api.listStoredSessions();
        } catch (Exception ex) {
            throw new CommandExecutionException("error while querying sessions: " + ex.getMessage(), ex);
        }
    }

    public List<Dashboard> getDashboards() {
        CustomXMLApi api = new CustomXMLApi(apiClient);
        try {
            DashboardList dashboardList = api.listDashboards();
            return dashboardList.getDashboards();
        } catch (Exception ex) {
            throw new CommandExecutionException("error while querying dashboards: " + ex.getMessage(), ex);
        }
    }

    public SystemProfiles getSystemProfiles() {
        SystemProfilesApi api = new SystemProfilesApi(apiClient);
        try {
            return api.getProfiles();
        } catch (Exception ex) {
            throw new CommandExecutionException("error while querying profiles: " + ex.getMessage(), ex);
        }
    }

    public SystemProfileConfigurations getProfileConfigurations() {
        SystemProfilesApi api = new SystemProfilesApi(apiClient);
        try {
            return api.getSystemProfileConfigurations(systemProfile);
        } catch (Exception ex) {
            throw new CommandExecutionException("error while querying configurations of profile " + systemProfile + ": " + ex.getMessage(), ex);
        }
    }

    public void activateConfiguration(final String configuration) {
        SystemProfilesApi api = new SystemProfilesApi(apiClient);
        try {
            api.putSystemProfileConfigurationStatus(systemProfile, configuration, new ActivationStatus("ENABLED"));
        } catch (Exception ex) {
            throw new CommandExecutionException("error while activating configuration: " + ex.getMessage());
        }
    }

    public List<Agent> getAllAgents() {
        CustomXMLApi api = new CustomXMLApi(apiClient);
        try {
            AgentList agentList = api.getAgents();
            return agentList.getAgents();
        } catch (Exception ex) {
            throw new CommandExecutionException("error while querying agents: " + ex.getMessage(), ex);
        }
    }

    public List<Agent> getAgents() {
        List<Agent> agents = getAllAgents();
        List<Agent> filteredAgents = new ArrayList<>();
        for (Agent agent : agents) {
            if (agent.getSystemProfile().equals(systemProfile))
                filteredAgents.add(agent);
        }
        return filteredAgents;
    }

    public boolean hotSensorPlacement(final int agentId) {
        CustomXMLApi api = new CustomXMLApi(apiClient);
        try {
            Result result = api.hotSensorPlacement(agentId);
            return result.isResultTrue();
        } catch (Exception ex) {
            throw new CommandExecutionException("error while doing hot sensor placement: " + ex.getMessage(), ex);
        }
    }

    public boolean getPDFReport(final String sessionName, final String comparedSessionName, final String dashboard, final FilePath file) {
        CustomXMLApi api = new CustomXMLApi(apiClient);
        try {
            File tmpFile = api.getPDFReport(dashboard, sessionName, comparedSessionName, "PDF");
            file.copyFrom(new FilePath(tmpFile));
            return true;
        } catch (Exception ex) {
            throw new CommandExecutionException("error while downloading PDF Report: " + ex.getMessage(), ex);
        }
    }

    public boolean downloadSession(final String sessionId, final FilePath outputFile) {
        StoredSessionsApi api = new StoredSessionsApi(apiClient);
        try {
            File tmpFile = api.getStoredSession(sessionId, true, null, null);
            outputFile.copyFrom(new FilePath(tmpFile));
            return true;
        } catch (Exception ex) {
            throw new CommandExecutionException("error while downloading session: " + ex.getMessage(), ex);
        }
    }

    public String threadDump(final String agentName, final String hostName, final int processId, final boolean sessionLocked) {
        CustomXMLApi api = new CustomXMLApi(apiClient);
        try {
            Result result = api.createThreadDump(systemProfile, agentName, hostName, processId, sessionLocked);
            return result.getValue();
        } catch (Exception ex) {
            throw new CommandExecutionException("error while creating thread dump: " + ex.getMessage(), ex);
        }
    }

    public boolean threadDumpStatus(final String threadDump) {
        CustomXMLApi api = new CustomXMLApi(apiClient);
        try {
            Result result = api.getThreadDumpStatus(systemProfile, threadDump);
            return result.isSuccessTrue();
        } catch (Exception ex) {
            throw new CommandExecutionException("error while querying thread dump status: " + ex.getMessage(), ex);
        }
    }

    public String memoryDump(final String agentName, final String hostName, final int processId, final String dumpType,
                             final boolean sessionLocked, final boolean captureStrings, final boolean capturePrimitives, final boolean autoPostProcess,
                             final boolean doGC) {
        CustomXMLApi api = new CustomXMLApi(apiClient);
        try {
            Result result = api.createMemoryDump(systemProfile, agentName, hostName, processId, dumpType, sessionLocked, captureStrings, capturePrimitives,
                    autoPostProcess, doGC);
            return result.getValue();
        } catch (Exception ex) {
            throw new CommandExecutionException("error while creating memory dump: " + ex.getMessage(), ex);
        }
    }

    public boolean memoryDumpStatus(final String memoryDump) {
        CustomXMLApi api = new CustomXMLApi(apiClient);
        try {
            Result result = api.getMemoryDumpStatus(systemProfile, memoryDump);
            return result.isSuccessTrue();
        } catch (Exception ex) {
            throw new CommandExecutionException("error while querying memory dump status: " + ex.getMessage(), ex);
        }
    }

    public String registerTestRun(final int versionBuild) {
        TestAutomationApi api = new TestAutomationApi(apiClient);
        try {
            TestRunDefinition body = new TestRunDefinition(versionBuild, "performance");
            TestRun testRun = api.postTestRun(systemProfile, body);
            return testRun.getId();
        } catch (Exception ex) {
            throw new CommandExecutionException("error registering test run: " + ex.getMessage(), ex);
        }
    }

    public TestRun finishTestRun(String testRunID) {
        TestAutomationApi api = new TestAutomationApi(apiClient);
        try {
            return api.finishTestRun(systemProfile, testRunID);
        } catch (Exception ex) {
            throw new CommandExecutionException("error finishing test run: " + ex.getMessage(), ex);
        }
    }

    public TestRun getTestRun(String testRunId) {
        TestAutomationApi api = new TestAutomationApi(apiClient);
        try {
            return api.getTestrunById(systemProfile, testRunId);
        } catch (ApiException ex) {
            throw new CommandExecutionException("error while getting test run details: " + ex.getMessage(), ex);
        }
    }

    public List<Alert> getIncidents(Date from, Date to) throws InterruptedException {
        AlertsIncidentsAndEventsApi api = new AlertsIncidentsAndEventsApi(apiClient);
        try {
            List<Alert> incidents = new ArrayList<>();
            Alerts alerts = api.getIncidents(systemProfile, null, "Created", apiClient.formatDatetime(from), apiClient.formatDatetime(to));
            for (AlertReference alertReference : alerts.getAlerts()) {
                incidents.add(api.getIncident(alertReference.getId()));
            }
            return incidents;
        } catch (ApiException ex) {
            throw new CommandExecutionException("error while getting incident details: " + ex.getMessage(), ex);
        }
    }
}
