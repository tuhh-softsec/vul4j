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
import de.tsystems.mms.apm.performancesignature.PerfSigRecorder;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.*;
import de.tsystems.mms.apm.performancesignature.model.ProxyBlock;
import de.tsystems.mms.apm.performancesignature.util.PerfSigUtils;
import hudson.ProxyConfiguration;
import hudson.util.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.net.ssl.*;
import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.net.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * Created by rapi on 25.04.2014.
 */
public class DTServerConnection {
    private static final Logger logger = Logger.getLogger(DTServerConnection.class.getName());

    private final String address;
    private final boolean verifyCertificate;
    private final UsernamePasswordCredentials credentials;
    // Dynatrace is unable to provide proper Certs to trust by default
    // Create a trust manager that does not validate certificate chains
    private final HostnameVerifier allHostsValid = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
    private Proxy proxy;
    private SSLContext sc;

    public DTServerConnection(final String protocol, final String host, final int port, final String credentialsId,
                              boolean verifyCertificate, final boolean useJenkinsProxy, final ProxyBlock proxyBlock) {
        this.address = protocol + "://" + (host != null ? host : PerfSigRecorder.DescriptorImpl.getDefaultHost()) + ":" +
                (port != 0 ? port : PerfSigRecorder.DescriptorImpl.getDefaultPort());
        this.credentials = PerfSigUtils.getCredentials(credentialsId);
        this.verifyCertificate = verifyCertificate;

        // Install the all-trusting trust manager
        try {
            sc = SSLContext.getInstance("TLSv1.2");
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
            e.printStackTrace();
        }

        if (useJenkinsProxy && PerfSigUtils.getInstanceOrDie().proxy != null) {
            final ProxyConfiguration proxyConfiguration = PerfSigUtils.getInstanceOrDie().proxy;
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
                logger.info("using proxy: " + proxyConfiguration.name + ":" + proxyConfiguration.port);
            }
        } else if (proxyBlock != null) {
            if (StringUtils.isNotBlank(proxyBlock.getProxyServer()) && proxyBlock.getProxyPort() > 0) {
                this.proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyBlock.getProxyServer(), proxyBlock.getProxyPort()));
                if (StringUtils.isNotBlank(proxyBlock.getProxyUser())) {
                    Authenticator authenticator = new Authenticator() {
                        public PasswordAuthentication getPasswordAuthentication() {
                            return (new PasswordAuthentication(proxyBlock.getProxyUser(), proxyBlock.getProxyPassword().toCharArray()));
                        }
                    };
                    Authenticator.setDefault(authenticator);
                }
                logger.info("using proxy: " + proxyBlock.getProxyServer() + ":" + proxyBlock.getProxyPort());
            }
        } else {
            this.proxy = Proxy.NO_PROXY;
        }
    }

    public TestRun getTestRunFromXML(final String profileName, final String uuid) {
        ManagementURLBuilder builder = new ManagementURLBuilder();
        builder.setServerAddress(this.address);
        URL url = builder.testRunDetailsURL(profileName, uuid);
        TestRun testRun;
        try {
            XMLReader xr = XMLReaderFactory.createXMLReader();
            TestRunDetailsXMLHandler handler = new TestRunDetailsXMLHandler();
            xr.setContentHandler(handler);
            xr.parse(new InputSource(getInputStream(url)));
            testRun = handler.getParsedObjects();
        } catch (Exception ex) {
            throw new ContentRetrievalException("Could not retrieve records from Dynatrace server: " + url.toString(), ex);
        }

        return testRun;
    }

    public DashboardReport getDashboardReportFromXML(final String dashBoardName, final String sessionName, final String testCaseName) {
        DashboardReport dashboardReport = new DashboardReport(testCaseName);
        ReportURLBuilder builder = new ReportURLBuilder();
        builder.setServerAddress(this.address).setDashboardName(dashBoardName).setSource(sessionName);
        URL url = builder.buildURL(false);
        List<ChartDashlet> chartDashlets;
        try {
            XMLReader xr = XMLReaderFactory.createXMLReader();
            DashboardXMLHandler handler = new DashboardXMLHandler();
            xr.setContentHandler(handler);
            xr.parse(new InputSource(getInputStream(url)));
            chartDashlets = handler.getParsedObjects();
        } catch (Exception ex) {
            throw new ContentRetrievalException("Could not retrieve records from Dynatrace server: " + url.toString(), ex);
        }

        dashboardReport.setChartDashlets(chartDashlets);
        return dashboardReport;
    }

    private InputStream getInputStream(final URL documentURL) throws IOException {
        URLConnection conn = documentURL.openConnection(proxy);
        addAuthenticationHeader(conn);
        return handleInputStream((HttpURLConnection) conn);
    }

    private void addAuthenticationHeader(final URLConnection conn) throws UnsupportedEncodingException {
        String userPassword = this.credentials.getUsername() + Messages.DTServerConnection_SEPARATORColon() + this.credentials.getPassword().getPlainText();
        String token = DatatypeConverter.printBase64Binary(userPassword.getBytes("UTF-8"));
        conn.setRequestProperty(Messages.DTServerConnection_PROPERTYAuthorization(), Messages.DTServerConnection_PROPERTYBasic() + " " + token);
        conn.setUseCaches(false);
        conn.setRequestProperty("Accept-Encoding", "gzip, deflate");

        conn.setConnectTimeout(60 * 1000);
        conn.setReadTimeout(60 * 10000);

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
        OutputStreamWriter wr = null;
        try {
            wr = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            wr.write(parameters);
        } catch (IOException ignored) {
        } finally {
            IOUtils.closeQuietly(wr);
        }
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

    private RESTResultXMLHandler getResultXMLHandler(final URLConnection conn) throws RESTErrorException, IOException, SAXException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) conn;
        handleHTTPResponseCode(httpURLConnection);

        RESTResultXMLHandler handler = new RESTResultXMLHandler();
        XMLReader xr = XMLReaderFactory.createXMLReader();
        xr.setContentHandler(handler);
        xr.parse(new InputSource(handleInputStream(httpURLConnection)));

        return handler;
    }

    private ProfileXMLHandler getProfileXMLHandler(final URLConnection conn) throws RESTErrorException, IOException, SAXException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) conn;
        handleHTTPResponseCode(httpURLConnection);

        ProfileXMLHandler handler = new ProfileXMLHandler();
        XMLReader xr = XMLReaderFactory.createXMLReader();
        xr.setContentHandler(handler);
        xr.parse(new InputSource(handleInputStream(httpURLConnection)));

        return handler;
    }

    private AgentXMLHandler getAgentXMLHandler(final URLConnection conn) throws RESTErrorException, IOException, SAXException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) conn;
        handleHTTPResponseCode(httpURLConnection);

        AgentXMLHandler handler = new AgentXMLHandler();
        XMLReader xr = XMLReaderFactory.createXMLReader();
        xr.setContentHandler(handler);
        xr.parse(new InputSource(handleInputStream(httpURLConnection)));

        return handler;
    }

    private RESTStringArrayXMLHandler getStringArrayXMLHandler(final URLConnection conn) throws RESTErrorException, IOException, SAXException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) conn;
        handleHTTPResponseCode(httpURLConnection);

        RESTStringArrayXMLHandler handler = new RESTStringArrayXMLHandler();
        XMLReader xr = XMLReaderFactory.createXMLReader();
        xr.setContentHandler(handler);
        xr.parse(new InputSource(handleInputStream(httpURLConnection)));

        return handler;
    }

    private RESTDumpStatusXMLHandler getDumpStatusXMLHandler(final URLConnection conn) throws RESTErrorException, IOException, SAXException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) conn;
        handleHTTPResponseCode(httpURLConnection);

        RESTDumpStatusXMLHandler handler = new RESTDumpStatusXMLHandler();
        XMLReader xr = XMLReaderFactory.createXMLReader();
        xr.setContentHandler(handler);
        xr.parse(new InputSource(handleInputStream(httpURLConnection)));

        return handler;
    }

    private void handleHTTPResponseCode(final HttpURLConnection httpURLConnection) throws RESTErrorException, IOException, SAXException {
        XMLReader xr = XMLReaderFactory.createXMLReader();
        if (httpURLConnection.getResponseCode() >= 300) {
            if (httpURLConnection.getResponseCode() == 401) {
                throw new RESTErrorException("Invalid username/password. ResponseCode " + httpURLConnection.getResponseCode());
            }
            RESTErrorXMLHandler handler = new RESTErrorXMLHandler();
            xr.setContentHandler(handler);
            httpURLConnection.setReadTimeout(15000);
            try {
                xr.parse(new InputSource(httpURLConnection.getErrorStream()));
            } catch (RuntimeException e) {
                throw new RESTErrorException("Unexpected response code HTTP " + httpURLConnection.getResponseCode());
            }
            throw new RESTErrorException(handler.getReasonString());
        }
    }

    public boolean validateConnection() {
        try {
            getServerVersion();
            return true;
        } catch (CommandExecutionException e) {
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
            throw new CommandExecutionException("Error getting version of server: " + ex.getMessage(), ex);
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
            throw new CommandExecutionException("Error reanalyzing session: " + ex.getMessage(), ex);
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
            throw new CommandExecutionException("Error reanalyzing session: " + ex.getMessage(), ex);
        }
    }

    public String startRecording(final String profileName, final String sessionName, final String description, final String recordingOption,
                                 final boolean sessionLocked, final boolean isNoTimestamp) throws RESTErrorException {
        try {
            ManagementURLBuilder builder = new ManagementURLBuilder();
            builder.setServerAddress(this.address);
            URL commandURL = builder.startRecordingURL(profileName, sessionName, description, recordingOption, sessionLocked, isNoTimestamp);
            URLConnection conn = commandURL.openConnection(proxy);

            addAuthenticationHeader(conn);
            addPostHeaders(conn, builder.getPostParameters());

            RESTResultXMLHandler handler = getResultXMLHandler(conn);
            return handler.getResultString();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String stopRecording(final String profileName) throws RESTErrorException {
        try {
            ManagementURLBuilder builder = new ManagementURLBuilder();
            builder.setServerAddress(this.address);
            URL commandURL = builder.stopRecordingURL(profileName);
            URLConnection conn = commandURL.openConnection(proxy);
            addAuthenticationHeader(conn);

            RESTResultXMLHandler handler = getResultXMLHandler(conn);
            return handler.getResultString();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
            throw new CommandExecutionException("Error listing sessions: " + ex.getMessage(), ex);
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
            throw new CommandExecutionException("Error listing profiles: " + ex.getMessage(), ex);
        }
    }

    public List<BaseConfiguration> getProfiles() {
        try {
            ManagementURLBuilder builder = new ManagementURLBuilder();
            builder.setServerAddress(this.address);
            URL commandURL = builder.listProfilesURL();
            URLConnection conn = commandURL.openConnection(proxy);
            addAuthenticationHeader(conn);

            ProfileXMLHandler handler = getProfileXMLHandler(conn);
            return handler.getConfigurationObjects();
        } catch (Exception ex) {
            throw new CommandExecutionException("Error listing profiles: " + ex.getMessage(), ex);
        }
    }

    //ToDo implement getConfigurations in activate ProfileConfiguration Builder
    public List<BaseConfiguration> getConfigurations(final String profileName) {
        try {
            ManagementURLBuilder builder = new ManagementURLBuilder();
            builder.setServerAddress(this.address);
            URL commandURL = builder.listConfigurationsURL(profileName);
            URLConnection conn = commandURL.openConnection(proxy);
            addAuthenticationHeader(conn);

            ProfileXMLHandler handler = getProfileXMLHandler(conn);
            return handler.getConfigurationObjects();
        } catch (Exception ex) {
            throw new CommandExecutionException("Error listing configurations of profile " + profileName + ": " + ex.getMessage(), ex);
        }
    }

    public boolean activateConfiguration(final String profileName, final String configuration) {
        try {
            ManagementURLBuilder builder = new ManagementURLBuilder();
            builder.setServerAddress(this.address);
            URL commandURL = builder.activateConfigurationURL(profileName, configuration);
            URLConnection conn = commandURL.openConnection(proxy);
            addAuthenticationHeader(conn);

            RESTResultXMLHandler handler = getResultXMLHandler(conn);
            return handler.isResultTrue();
        } catch (Exception ex) {
            throw new CommandExecutionException("Error activating configuration: " + ex.getMessage());
        }
    }

    public List<Agent> getAgents() {
        try {
            ManagementURLBuilder builder = new ManagementURLBuilder();
            builder.setServerAddress(this.address);
            URL commandURL = builder.listAgentsURL();
            URLConnection conn = commandURL.openConnection(proxy);
            addAuthenticationHeader(conn);

            AgentXMLHandler handler = getAgentXMLHandler(conn);
            return handler.getAgents();
        } catch (Exception ex) {
            throw new CommandExecutionException("Error listing agents: " + ex.getMessage(), ex);
        }
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
            throw new CommandExecutionException("Error doing hot sensor placement: " + ex.getMessage(), ex);
        }
    }

    public boolean getPDFReport(final String sessionName, final String comparedSessionName, final String dashboard, final File file) {
        InputStream is = null;
        try {
            ReportURLBuilder builder = new ReportURLBuilder();
            builder.setServerAddress(this.address)
                    .setDashboardName(dashboard)
                    .setSource(sessionName)
                    .setType("PDF");
            if (comparedSessionName != null) builder.setComparison(comparedSessionName);
            is = getInputStream(builder.buildURL(true));
            IOUtils.copy(is, file);
            return true;
        } catch (Exception ex) {
            throw new CommandExecutionException("Error downloading PDF Report: " + ex.getMessage(), ex);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    public boolean downloadSession(final String sessionName, final File outputFile) {
        InputStream is = null;
        try {
            ManagementURLBuilder builder = new ManagementURLBuilder();
            builder.setServerAddress(this.address);

            is = getInputStream(builder.downloadSessionURL(sessionName));
            IOUtils.copy(is, outputFile);
            return true;
        } catch (Exception ex) {
            throw new CommandExecutionException("Error downloading session: " + ex.getMessage(), ex);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    public String threadDump(final String profileName, final String agentName, final String hostName, final int processId, final boolean sessionLocked) {
        try {
            ManagementURLBuilder builder = new ManagementURLBuilder();
            builder.setServerAddress(this.address);
            URL commandURL = builder.threadDumpURL(profileName, agentName, hostName, processId, sessionLocked);
            URLConnection conn = commandURL.openConnection(proxy);
            addAuthenticationHeader(conn);
            addPostHeaders(conn, builder.getPostParameters());

            RESTResultXMLHandler handler = getResultXMLHandler(conn);
            return handler.getResultString();
        } catch (Exception ex) {
            throw new CommandExecutionException("Error with thread dump: " + ex.getMessage(), ex);
        }
    }

    public DumpStatus threadDumpStatus(final String profileName, final String threadDump) {
        try {
            ManagementURLBuilder builder = new ManagementURLBuilder();
            builder.setServerAddress(this.address);
            URL commandURL = builder.threadDumpStatusURL(profileName, threadDump);
            URLConnection conn = commandURL.openConnection(proxy);
            addAuthenticationHeader(conn);

            RESTDumpStatusXMLHandler handler = getDumpStatusXMLHandler(conn);
            return handler.getDumpStatus();
        } catch (Exception ex) {
            throw new CommandExecutionException("Error with thread dump status: " + ex.getMessage(), ex);
        }
    }

    public String memoryDump(final String profileName, final String agentName, final String hostName, final int processId, final String dumpType,
                             final boolean sessionLocked, final boolean captureStrings, final boolean capturePrimitives, final boolean autoPostProcess, final boolean dogC) {
        try {
            ManagementURLBuilder builder = new ManagementURLBuilder();
            builder.setServerAddress(this.address);
            URL commandURL = builder.memoryDumpURL(profileName, agentName, hostName, processId, dumpType, sessionLocked, captureStrings, capturePrimitives, autoPostProcess, dogC);
            URLConnection conn = commandURL.openConnection(proxy);
            addAuthenticationHeader(conn);
            addPostHeaders(conn, builder.getPostParameters());

            RESTResultXMLHandler handler = getResultXMLHandler(conn);
            return handler.getResultString();
        } catch (Exception ex) {
            throw new CommandExecutionException("Error with memory dump: " + ex.getMessage(), ex);
        }
    }

    public DumpStatus memoryDumpStatus(final String profileName, final String memoryDump) {
        try {
            ManagementURLBuilder builder = new ManagementURLBuilder();
            builder.setServerAddress(this.address);
            URL commandURL = builder.memoryDumpStatusURL(profileName, memoryDump);
            URLConnection conn = commandURL.openConnection(proxy);
            addAuthenticationHeader(conn);

            RESTDumpStatusXMLHandler handler = getDumpStatusXMLHandler(conn);
            return handler.getDumpStatus();
        } catch (Exception ex) {
            throw new CommandExecutionException("Error with memory dump status: " + ex.getMessage(), ex);
        }
    }

    public String registerTestRun(final String systemProfile, final int versionBuild) {
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
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(testMetaDataPostXml);
            wr.flush();
            IOUtils.closeQuietly(wr);

            handleHTTPResponseCode(conn);
            TestMetaDataXMLHandler handler = new TestMetaDataXMLHandler();
            XMLReader xr = XMLReaderFactory.createXMLReader();
            xr.setContentHandler(handler);
            xr.parse(new InputSource(handleInputStream(conn)));

            return handler.getTestMetaDataUUID();
        } catch (Exception ex) {
            throw new CommandExecutionException(ex.toString() + " Error setting testdata in startTest: " + ex.getMessage(), ex);
        }
    }
}
