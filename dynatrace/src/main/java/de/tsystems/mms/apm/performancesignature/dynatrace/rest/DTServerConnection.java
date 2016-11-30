/*
 * Copyright (c) 2008-2015, DYNATRACE LLC
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright notice,
 *       this list of conditions and the following disclaimer in the documentation
 *       and/or other materials provided with the distribution.
 *     * Neither the name of the dynaTrace software nor the names of its contributors
 *       may be used to endorse or promote products derived from this software without
 *       specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 */

package de.tsystems.mms.apm.performancesignature.dynatrace.rest;

import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials;
import de.tsystems.mms.apm.performancesignature.dynatrace.configuration.CredProfilePair;
import de.tsystems.mms.apm.performancesignature.dynatrace.configuration.CustomProxy;
import de.tsystems.mms.apm.performancesignature.dynatrace.configuration.DynatraceServerConfiguration;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.DashboardReport;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.TestRun;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.model.Agent;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.model.BaseConfiguration;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.model.DumpStatus;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.model.RegisterTestRunRequest;
import de.tsystems.mms.apm.performancesignature.util.PerfSigUIUtils;
import de.tsystems.mms.apm.performancesignature.util.PerfSigUtils;
import hudson.FilePath;
import hudson.ProxyConfiguration;
import jenkins.model.Jenkins;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.net.ssl.*;
import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class DTServerConnection {
    private static final Logger LOGGER = Logger.getLogger(DTServerConnection.class.getName());
    private final String address;
    private final boolean verifyCertificate;
    private final UsernamePasswordCredentials credentials;
    /* Dynatrace is unable to provide proper Certs to trust by default
     Create a trust manager that does not validate certificate chains */
    private final HostnameVerifier allHostsValid = new HostnameVerifier() {
        public boolean verify(final String hostname, final SSLSession session) {
            return true;
        }
    };
    private final String systemProfile;
    private Proxy proxy;
    private SSLContext sc;

    public DTServerConnection(final String protocol, final String host, final int port, final CredProfilePair pair,
                              final boolean verifyCertificate, final CustomProxy customProxy) {
        this.address = protocol + "://" + host + ":" + port;
        this.credentials = PerfSigUtils.getCredentials(pair.getCredentialsId());
        this.verifyCertificate = verifyCertificate;
        this.proxy = Proxy.NO_PROXY;
        this.systemProfile = pair.getProfile();

        // Install the all-trusting trust manager
        try {
            if (SystemUtils.IS_JAVA_1_6) {
                sc = SSLContext.getInstance("TLSv1");
            } else {
                sc = SSLContext.getInstance("TLSv1.2");
            }
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(final X509Certificate[] certs, final String authType) {
                }

                public void checkServerTrusted(final X509Certificate[] certs, final String authType) {
                }
            }
            };
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            LOGGER.severe(ExceptionUtils.getFullStackTrace(e));
        }

        if (customProxy != null) {
            Jenkins jenkins = PerfSigUIUtils.getInstance();
            if (customProxy.isUseJenkinsProxy() && jenkins.proxy != null) {
                final ProxyConfiguration proxyConfiguration = jenkins.proxy;
                if (StringUtils.isNotBlank(proxyConfiguration.name) && proxyConfiguration.port > 0) {
                    this.proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyConfiguration.name, proxyConfiguration.port));
                    if (StringUtils.isNotBlank(proxyConfiguration.getUserName())) {
                        Authenticator authenticator = new Authenticator() {
                            public PasswordAuthentication getPasswordAuthentication() {
                                return (new PasswordAuthentication(proxyConfiguration.getUserName(), proxyConfiguration.getPassword().toCharArray()));
                            }
                        };
                        Authenticator.setDefault(authenticator);
                    }
                }
            } else {
                if (StringUtils.isNotBlank(customProxy.getProxyServer()) && customProxy.getProxyPort() > 0) {
                    this.proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(customProxy.getProxyServer(), customProxy.getProxyPort()));
                    if (StringUtils.isNotBlank(customProxy.getProxyUser())) {
                        Authenticator authenticator = new Authenticator() {
                            public PasswordAuthentication getPasswordAuthentication() {
                                return (new PasswordAuthentication(customProxy.getProxyUser(), customProxy.getProxyPassword().toCharArray()));
                            }
                        };
                        Authenticator.setDefault(authenticator);
                    }
                }
            }
        }
    }

    public DTServerConnection(final DynatraceServerConfiguration config, final CredProfilePair pair) {
        this(config.getProtocol(), config.getHost(), config.getPort(), pair, config.isVerifyCertificate(), config.getCustomProxy());
    }

    public TestRun getTestRunFromXML(final String uuid) {
        ManagementURLBuilder builder = new ManagementURLBuilder();
        builder.setServerAddress(this.address);
        URL url = builder.testRunDetailsURL(systemProfile, uuid);
        try {
            XMLReader xr = XMLReaderFactory.createXMLReader();
            TestRunDetailsXMLHandler handler = new TestRunDetailsXMLHandler();
            xr.setContentHandler(handler);
            xr.parse(new InputSource(getInputStream(url)));
            return handler.getParsedObjects();
        } catch (Exception ex) {
            throw new ContentRetrievalException(ExceptionUtils.getStackTrace(ex) + "Could not retrieve records from Dynatrace server: " + url.toString(), ex);
        }
    }

    public DashboardReport getDashboardReportFromXML(final String dashBoardName, final String sessionName, final String testCaseName) {
        ReportURLBuilder builder = new ReportURLBuilder();
        builder.setServerAddress(this.address).setDashboardName(dashBoardName).setSource(sessionName);
        URL url = builder.buildURL(false);
        try {
            XMLReader xr = XMLReaderFactory.createXMLReader();
            DashboardXMLHandler handler = new DashboardXMLHandler(testCaseName);
            xr.setContentHandler(handler);
            xr.parse(new InputSource(getInputStream(url)));
            return handler.getParsedObjects();
        } catch (Exception ex) {
            throw new ContentRetrievalException(ExceptionUtils.getStackTrace(ex) + "could not retrieve records from Dynatrace server: " + url.toString(), ex);
        }
    }

    private InputStream getInputStream(final URL documentURL) throws IOException {
        URLConnection conn = documentURL.openConnection(proxy);
        addAuthenticationHeader(conn);
        return handleInputStream((HttpURLConnection) conn);
    }

    private void addAuthenticationHeader(final URLConnection conn) throws UnsupportedEncodingException {
        String userPassword = this.credentials.getUsername() + ":" + this.credentials.getPassword().getPlainText();
        String token = DatatypeConverter.printBase64Binary(userPassword.getBytes(CharEncoding.UTF_8));
        conn.setRequestProperty("Authorization", "Basic" + " " + token);
        conn.setUseCaches(false);
        conn.setRequestProperty("Accept-Encoding", "gzip, deflate");

        conn.setConnectTimeout(60 * 1000);
        conn.setReadTimeout(120 * 1000);

        if (conn instanceof HttpsURLConnection && !verifyCertificate) {
            HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
            httpsConn.setHostnameVerifier(allHostsValid);
            httpsConn.setSSLSocketFactory(sc.getSocketFactory());
        }
    }

    private void addPostHeaders(final URLConnection conn, final String parameters) throws IOException {
        if (parameters == null) {
            return;
        }
        conn.setDoOutput(true);
        IOUtils.write(parameters, conn.getOutputStream());
    }

    private InputStream handleInputStream(final HttpURLConnection conn) throws IOException {
        InputStream resultingInputStream;
        String encoding = conn.getContentEncoding();
        if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
            resultingInputStream = new GZIPInputStream(conn.getInputStream());
        } else if (encoding != null && encoding.equalsIgnoreCase("deflate")) {
            resultingInputStream = new InflaterInputStream(conn.getInputStream(), new Inflater(true));
        } else {
            resultingInputStream = conn.getInputStream();
        }
        return resultingInputStream;
    }

    private RESTResultXMLHandler getResultXMLHandler(final URLConnection conn) throws IOException, SAXException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) conn;
        handleHTTPResponseCode(httpURLConnection);

        RESTResultXMLHandler handler = new RESTResultXMLHandler();
        XMLReader xr = XMLReaderFactory.createXMLReader();
        xr.setContentHandler(handler);
        xr.parse(new InputSource(handleInputStream(httpURLConnection)));

        return handler;
    }

    private ProfileXMLHandler getProfileXMLHandler(final URLConnection conn) throws IOException, SAXException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) conn;
        handleHTTPResponseCode(httpURLConnection);

        ProfileXMLHandler handler = new ProfileXMLHandler();
        XMLReader xr = XMLReaderFactory.createXMLReader();
        xr.setContentHandler(handler);
        xr.parse(new InputSource(handleInputStream(httpURLConnection)));

        return handler;
    }

    private AgentXMLHandler getAgentXMLHandler(final URLConnection conn) throws IOException, SAXException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) conn;
        handleHTTPResponseCode(httpURLConnection);

        AgentXMLHandler handler = new AgentXMLHandler();
        XMLReader xr = XMLReaderFactory.createXMLReader();
        xr.setContentHandler(handler);
        xr.parse(new InputSource(handleInputStream(httpURLConnection)));

        return handler;
    }

    private RESTStringArrayXMLHandler getStringArrayXMLHandler(final URLConnection conn) throws IOException, SAXException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) conn;
        handleHTTPResponseCode(httpURLConnection);

        RESTStringArrayXMLHandler handler = new RESTStringArrayXMLHandler();
        XMLReader xr = XMLReaderFactory.createXMLReader();
        xr.setContentHandler(handler);
        xr.parse(new InputSource(handleInputStream(httpURLConnection)));

        return handler;
    }

    private RESTDumpStatusXMLHandler getDumpStatusXMLHandler(final URLConnection conn) throws IOException, SAXException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) conn;
        handleHTTPResponseCode(httpURLConnection);

        RESTDumpStatusXMLHandler handler = new RESTDumpStatusXMLHandler();
        XMLReader xr = XMLReaderFactory.createXMLReader();
        xr.setContentHandler(handler);
        xr.parse(new InputSource(handleInputStream(httpURLConnection)));

        return handler;
    }

    private void handleHTTPResponseCode(final HttpURLConnection httpURLConnection) throws IOException, SAXException {
        XMLReader xr = XMLReaderFactory.createXMLReader();
        if (httpURLConnection.getResponseCode() >= 300) {
            if (httpURLConnection.getResponseCode() == 401) {
                throw new RESTErrorException("invalid username/password. ResponseCode " + httpURLConnection.getResponseCode());
            }
            RESTErrorXMLHandler handler = new RESTErrorXMLHandler();
            xr.setContentHandler(handler);
            httpURLConnection.setReadTimeout(15000);
            try {
                xr.parse(new InputSource(httpURLConnection.getErrorStream()));
            } catch (RuntimeException e) {
                throw new RESTErrorException("unexpected response code HTTP " + httpURLConnection.getResponseCode());
            }
            throw new RESTErrorException(handler.getReasonString());
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

    public String getServerVersion() throws CommandExecutionException {
        try {
            ManagementURLBuilder builder = new ManagementURLBuilder();
            builder.setServerAddress(this.address);
            URL commandURL = builder.serverVersionURL();
            URLConnection conn = commandURL.openConnection(proxy);
            addAuthenticationHeader(conn);

            RESTResultXMLHandler handler = getResultXMLHandler(conn);
            return handler.getResultString();
        } catch (Exception ex) {
            throw new CommandExecutionException("error getting version of server: " + ex.getMessage(), ex);
        }
    }

    public boolean reanalyzeSession(final String sessionName) {
        try {
            ManagementURLBuilder builder = new ManagementURLBuilder();
            builder.setServerAddress(this.address);
            URL commandURL = builder.reanalyzeSessionURL(sessionName);
            URLConnection conn = commandURL.openConnection(proxy);
            addAuthenticationHeader(conn);
            RESTResultXMLHandler handler = getResultXMLHandler(conn);
            return handler.isResultTrue();
        } catch (Exception ex) {
            throw new CommandExecutionException("error reanalyzing session: " + ex.getMessage(), ex);
        }
    }

    public boolean reanalyzeSessionStatus(final String sessionName) {
        try {
            ManagementURLBuilder builder = new ManagementURLBuilder();
            builder.setServerAddress(this.address);
            URL commandURL = builder.reanalyzeSessionStatusURL(sessionName);
            URLConnection conn = commandURL.openConnection(proxy);
            addAuthenticationHeader(conn);

            RESTResultXMLHandler handler = getResultXMLHandler(conn);
            return handler.isResultTrue();
        } catch (Exception ex) {
            throw new CommandExecutionException("error reanalyzing session: " + ex.getMessage(), ex);
        }
    }

    public String storePurePaths(final String sessionName, final Date timeframeStart, final Date timeframeEnd, final String recordingOption,
                                 final boolean sessionLocked, final boolean appendTimestamp) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S");
        try {
            ManagementURLBuilder builder = new ManagementURLBuilder();
            builder.setServerAddress(this.address);
            URL commandURL = builder.storePurePathsURL(systemProfile, sessionName, df.format(timeframeStart), df.format(timeframeEnd), recordingOption,
                    sessionLocked, appendTimestamp);
            URLConnection conn = commandURL.openConnection(proxy);
            addAuthenticationHeader(conn);

            RESTResultXMLHandler handler = getResultXMLHandler(conn);
            return handler.getResultString();
        } catch (Exception ex) {
            throw new CommandExecutionException("error storing purepaths: " + ex.getMessage(), ex);
        }
    }

    public String startRecording(final String sessionName, final String description, final String recordingOption,
                                 final boolean sessionLocked, final boolean isNoTimestamp) throws RESTErrorException {
        try {
            ManagementURLBuilder builder = new ManagementURLBuilder();
            builder.setServerAddress(this.address);
            URL commandURL = builder.startRecordingURL(systemProfile, sessionName, description, recordingOption, sessionLocked, isNoTimestamp);
            URLConnection conn = commandURL.openConnection(proxy);
            addAuthenticationHeader(conn);
            addPostHeaders(conn, builder.getPostParameters());

            RESTResultXMLHandler handler = getResultXMLHandler(conn);
            return handler.getResultString();
        } catch (Exception ex) {
            throw new CommandExecutionException("error start recording session: " + ex.getMessage(), ex);
        }
    }

    public String stopRecording() throws RESTErrorException {
        try {
            ManagementURLBuilder builder = new ManagementURLBuilder();
            builder.setServerAddress(this.address);
            URL commandURL = builder.stopRecordingURL(systemProfile);
            URLConnection conn = commandURL.openConnection(proxy);
            addAuthenticationHeader(conn);

            RESTResultXMLHandler handler = getResultXMLHandler(conn);
            return handler.getResultString();
        } catch (Exception ex) {
            throw new CommandExecutionException("error stop recording session: " + ex.getMessage(), ex);
        }
    }

    public List<String> getSessions() {
        try {
            ManagementURLBuilder builder = new ManagementURLBuilder();
            builder.setServerAddress(this.address);
            URL commandURL = builder.listSessionsURL();
            URLConnection conn = commandURL.openConnection(proxy);
            addAuthenticationHeader(conn);

            RESTStringArrayXMLHandler handler = getStringArrayXMLHandler(conn);
            return handler.getObjects();
        } catch (Exception ex) {
            throw new CommandExecutionException("error listing sessions: " + ex.getMessage(), ex);
        }
    }

    public List<String> getDashboards() {
        try {
            ManagementURLBuilder builder = new ManagementURLBuilder();
            builder.setServerAddress(this.address);
            URL commandURL = builder.listDashboardsURL();
            URLConnection conn = commandURL.openConnection(proxy);
            addAuthenticationHeader(conn);

            RESTStringArrayXMLHandler handler = getStringArrayXMLHandler(conn);
            return handler.getObjects();
        } catch (Exception ex) {
            throw new CommandExecutionException("error listing profiles: " + ex.getMessage(), ex);
        }
    }

    public List<BaseConfiguration> getSystemProfiles() {
        try {
            ManagementURLBuilder builder = new ManagementURLBuilder();
            builder.setServerAddress(this.address);
            URL commandURL = builder.listProfilesURL();
            URLConnection conn = commandURL.openConnection(proxy);
            addAuthenticationHeader(conn);

            ProfileXMLHandler handler = getProfileXMLHandler(conn);
            return handler.getConfigurationObjects();
        } catch (Exception ex) {
            throw new CommandExecutionException("error listing profiles: " + ex.getMessage(), ex);
        }
    }

    public List<BaseConfiguration> getProfileConfigurations() {
        try {
            ManagementURLBuilder builder = new ManagementURLBuilder();
            builder.setServerAddress(this.address);
            URL commandURL = builder.listConfigurationsURL(systemProfile);
            URLConnection conn = commandURL.openConnection(proxy);
            addAuthenticationHeader(conn);

            ProfileXMLHandler handler = getProfileXMLHandler(conn);
            return handler.getConfigurationObjects();
        } catch (Exception ex) {
            throw new CommandExecutionException("error listing configurations of profile " + systemProfile + ": " + ex.getMessage(), ex);
        }
    }

    public boolean activateConfiguration(final String configuration) {
        try {
            ManagementURLBuilder builder = new ManagementURLBuilder();
            builder.setServerAddress(this.address);
            URL commandURL = builder.activateConfigurationURL(systemProfile, configuration);
            URLConnection conn = commandURL.openConnection(proxy);
            addAuthenticationHeader(conn);

            RESTResultXMLHandler handler = getResultXMLHandler(conn);
            return handler.isResultTrue();
        } catch (Exception ex) {
            throw new CommandExecutionException("error activating configuration: " + ex.getMessage());
        }
    }

    public List<Agent> getAllAgents() {
        try {
            ManagementURLBuilder builder = new ManagementURLBuilder();
            builder.setServerAddress(this.address);
            URL commandURL = builder.listAgentsURL();
            URLConnection conn = commandURL.openConnection(proxy);
            addAuthenticationHeader(conn);

            AgentXMLHandler handler = getAgentXMLHandler(conn);
            return handler.getAgents();
        } catch (Exception ex) {
            throw new CommandExecutionException("error listing agents: " + ex.getMessage(), ex);
        }
    }

    public List<Agent> getAgents() {
        List<Agent> agents = getAllAgents();
        List<Agent> filteredAgents = new ArrayList<Agent>();
        for (Agent agent : agents) {
            if (agent.getSystemProfile().equals(systemProfile))
                filteredAgents.add(agent);
        }
        return filteredAgents;
    }

    public boolean hotSensorPlacement(final int agentId) {
        try {
            ManagementURLBuilder builder = new ManagementURLBuilder();
            builder.setServerAddress(this.address);
            URL commandURL = builder.hotSensorPlacementURL(agentId);
            URLConnection conn = commandURL.openConnection(proxy);
            addAuthenticationHeader(conn);

            RESTResultXMLHandler handler = getResultXMLHandler(conn);
            return handler.isResultTrue();
        } catch (Exception ex) {
            throw new CommandExecutionException("error doing hot sensor placement: " + ex.getMessage(), ex);
        }
    }

    public boolean getPDFReport(final String sessionName, final String comparedSessionName, final String dashboard, final FilePath file) {
        try {
            ReportURLBuilder builder = new ReportURLBuilder();
            builder.setServerAddress(this.address)
                    .setDashboardName(dashboard)
                    .setSource(sessionName)
                    .setType("PDF");
            if (comparedSessionName != null) builder.setComparison(comparedSessionName);
            file.copyFrom(getInputStream(builder.buildURL(true)));
            return true;
        } catch (Exception ex) {
            throw new CommandExecutionException("error downloading PDF Report: " + ex.getMessage(), ex);
        }
    }

    public boolean downloadSession(final String sessionName, final FilePath outputFile) {
        try {
            ManagementURLBuilder builder = new ManagementURLBuilder();
            builder.setServerAddress(this.address);

            outputFile.copyFrom(getInputStream(builder.downloadSessionURL(sessionName)));
            return true;
        } catch (Exception ex) {
            throw new CommandExecutionException("error downloading session: " + ex.getMessage(), ex);
        }
    }

    public String threadDump(final String agentName, final String hostName, final int processId, final boolean sessionLocked) {
        try {
            ManagementURLBuilder builder = new ManagementURLBuilder();
            builder.setServerAddress(this.address);
            URL commandURL = builder.threadDumpURL(systemProfile, agentName, hostName, processId, sessionLocked);
            URLConnection conn = commandURL.openConnection(proxy);
            addAuthenticationHeader(conn);
            addPostHeaders(conn, builder.getPostParameters());

            RESTResultXMLHandler handler = getResultXMLHandler(conn);
            return handler.getResultString();
        } catch (Exception ex) {
            throw new CommandExecutionException("error with thread dump: " + ex.getMessage(), ex);
        }
    }

    public DumpStatus threadDumpStatus(final String threadDump) {
        try {
            ManagementURLBuilder builder = new ManagementURLBuilder();
            builder.setServerAddress(this.address);
            URL commandURL = builder.threadDumpStatusURL(systemProfile, threadDump);
            URLConnection conn = commandURL.openConnection(proxy);
            addAuthenticationHeader(conn);

            RESTDumpStatusXMLHandler handler = getDumpStatusXMLHandler(conn);
            return handler.getDumpStatus();
        } catch (Exception ex) {
            throw new CommandExecutionException("error with thread dump status: " + ex.getMessage(), ex);
        }
    }

    public String memoryDump(final String agentName, final String hostName, final int processId, final String dumpType,
                             final boolean sessionLocked, final boolean captureStrings, final boolean capturePrimitives, final boolean autoPostProcess, final boolean dogC) {
        try {
            ManagementURLBuilder builder = new ManagementURLBuilder();
            builder.setServerAddress(this.address);
            URL commandURL = builder.memoryDumpURL(systemProfile, agentName, hostName, processId, dumpType, sessionLocked, captureStrings, capturePrimitives, autoPostProcess, dogC);
            URLConnection conn = commandURL.openConnection(proxy);
            addAuthenticationHeader(conn);
            addPostHeaders(conn, builder.getPostParameters());

            RESTResultXMLHandler handler = getResultXMLHandler(conn);
            return handler.getResultString();
        } catch (Exception ex) {
            throw new CommandExecutionException("error with memory dump: " + ex.getMessage(), ex);
        }
    }

    public DumpStatus memoryDumpStatus(final String memoryDump) {
        try {
            ManagementURLBuilder builder = new ManagementURLBuilder();
            builder.setServerAddress(this.address);
            URL commandURL = builder.memoryDumpStatusURL(systemProfile, memoryDump);
            URLConnection conn = commandURL.openConnection(proxy);
            addAuthenticationHeader(conn);

            RESTDumpStatusXMLHandler handler = getDumpStatusXMLHandler(conn);
            return handler.getDumpStatus();
        } catch (Exception ex) {
            throw new CommandExecutionException("error with memory dump status: " + ex.getMessage(), ex);
        }
    }

    public String registerTestRun(final int versionBuild) {
        try {
            RegisterTestRunRequest requestContent = new RegisterTestRunRequest();
            requestContent.setVersionBuild(String.valueOf(versionBuild));
            requestContent.setCategory("performance");

            /*requestContent.setVersionMajor(versionMajor);
            requestContent.setVersionMinor(versionMinor);
            requestContent.setVersionRevision(versionRevision);
            requestContent.setVersionMilestone(versionMilestone); */

            StringWriter writer = new StringWriter();
            JAXBContext context = JAXBContext.newInstance(requestContent.getClass());
            Marshaller m = context.createMarshaller();
            m.marshal(requestContent, writer);
            writer.flush();
            String testMetaDataPostXml = writer.toString();

            ManagementURLBuilder builder = new ManagementURLBuilder();
            builder.setServerAddress(this.address);
            URL commandURL = builder.registerTestRunURL(systemProfile);
            HttpURLConnection conn = (HttpURLConnection) commandURL.openConnection(proxy);
            conn.setRequestMethod("POST");
            addAuthenticationHeader(conn);
            conn.setRequestProperty("Content-Type", "text/xml");

            conn.setDoOutput(true);
            IOUtils.write(testMetaDataPostXml, conn.getOutputStream());

            handleHTTPResponseCode(conn);
            TestMetaDataXMLHandler handler = new TestMetaDataXMLHandler();
            XMLReader xr = XMLReaderFactory.createXMLReader();
            xr.setContentHandler(handler);
            xr.parse(new InputSource(handleInputStream(conn)));

            return handler.getTestMetaDataUUID();
        } catch (Exception ex) {
            throw new CommandExecutionException("error setting testdata in startTest: " + ex.getMessage(), ex);
        }
    }
}
