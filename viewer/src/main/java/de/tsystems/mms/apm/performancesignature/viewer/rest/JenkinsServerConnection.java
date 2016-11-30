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

package de.tsystems.mms.apm.performancesignature.viewer.rest;

import com.google.common.base.Optional;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpClient;
import com.offbytwo.jenkins.model.FolderJob;
import com.offbytwo.jenkins.model.Job;
import com.offbytwo.jenkins.model.JobWithDetails;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.DashboardReport;
import de.tsystems.mms.apm.performancesignature.viewer.model.CredJobPair;
import de.tsystems.mms.apm.performancesignature.viewer.model.CustomProxy;
import de.tsystems.mms.apm.performancesignature.viewer.model.JenkinsServerConfiguration;
import de.tsystems.mms.apm.performancesignature.viewer.rest.model.CustomJenkinsHttpClient;
import hudson.FilePath;
import hudson.util.XStream2;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.jdom2.JDOMException;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class JenkinsServerConnection {
    private static final Logger LOGGER = Logger.getLogger(JenkinsServerConnection.class.getName());
    private JobWithDetails jenkinsJob;
    private JenkinsServer jenkinsServer;

    public JenkinsServerConnection(final String protocol, final String host, final int port, final CredJobPair pair, final boolean verifyCertificate,
                                   final CustomProxy customProxyServer) {
        try {
            URI uri = new URI(protocol + "://" + host + ":" + port);
            JenkinsHttpClient client;
            if (pair.getCredentials() == null) {
                client = new CustomJenkinsHttpClient(uri, null, null, verifyCertificate, customProxyServer);
            } else {
                client = new CustomJenkinsHttpClient(uri, pair.getCredentials().getUsername(), pair.getCredentials().getPassword().getPlainText(),
                        verifyCertificate, customProxyServer);
            }
            this.jenkinsServer = new JenkinsServer(client);
            String job = pair.getJenkinsJob();

            //handle folders wait for https://github.com/jenkinsci/java-client-api/pull/205
            if (job.contains("/")) {
                String[] parts = job.split("/");
                Job folderJob = jenkinsServer.getJob(parts[0]);
                Optional<FolderJob> folder = jenkinsServer.getFolderJob(folderJob);
                if (folder.isPresent()) {
                    this.jenkinsJob = folder.get().getJob(parts[1]).details();
                } else {
                    throw new CommandExecutionException("the given folder/job name does not match");
                }
            } else {
                this.jenkinsJob = jenkinsServer.getJob(pair.getJenkinsJob());
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public JenkinsServerConnection(final JenkinsServerConfiguration config, final CredJobPair pair) {
        this(config.getProtocol(), config.getHost(), config.getPort(), pair, config.isVerifyCertificate(), config.getCustomProxy());
    }

    public List<DashboardReport> getDashboardReportsFromXML(int buildNumber) {
        try {
            URL url = new URL(getJenkinsJob().getUrl() + buildNumber + "/performance-signature/api/xml?depth=10");
            String xml = getJenkinsJob().getClient().get(url.toString());
            DashboardXMLReader reader = new DashboardXMLReader();
            reader.parseXML(xml);
            return reader.getParsedObjects();
        } catch (IOException | JDOMException e) {
            throw new ContentRetrievalException(ExceptionUtils.getStackTrace(e) + "could not retrieve records from remote Jenkins: ", e);
        }
    }

    public boolean validateConnection() {
        try {
            return jenkinsServer.isRunning() && getJenkinsJob() != null;
        } catch (CommandExecutionException e) {
            LOGGER.severe(ExceptionUtils.getFullStackTrace(e));
            return false;
        }
    }

    private List getReportList(final ReportType type, final int buildNumber) throws IOException {
        URL url = new URL(getJenkinsJob().getUrl() + buildNumber + "/performance-signature/get" + type + "ReportList");
        String xml = getJenkinsJob().getClient().get(url.toString());
        XStream2 xStream = new XStream2();
        List obj = (List) xStream.fromXML(xml);
        return obj != null ? obj : Collections.emptyList();
    }

    public boolean downloadPDFReports(final int buildNumber, final FilePath dir, final PrintStream logger) {
        boolean result = true;
        try {
            for (ReportType reportType : ReportType.values()) {
                List reportlist = getReportList(reportType, buildNumber);
                for (Object report : reportlist) {
                    URL url = new URL(getJenkinsJob().getUrl() + buildNumber + "/performance-signature/get" + reportType + "Report?number="
                            + reportlist.indexOf(report));
                    result &= downloadArtifact(new FilePath(dir, report + ".pdf"), url, logger);
                }
            }
            return result;
        } catch (IOException e) {
            throw new CommandExecutionException("error downloading PDF Reports: " + e.getMessage(), e);
        }
    }

    public boolean downloadSession(final int buildNumber, final FilePath dir, final String testCase, final PrintStream logger) {
        try {
            URL url = new URL(getJenkinsJob().getUrl() + "/" + buildNumber + "/performance-signature/getSession?testCase=" + testCase);
            String sessionFileName = getJenkinsJob().getName() + "_Build_" + buildNumber + "_" + testCase + ".dts";
            return downloadArtifact(new FilePath(dir, sessionFileName), url, logger);
        } catch (IOException e) {
            throw new CommandExecutionException("error downloading sessions: " + e.getMessage(), e);
        }
    }

    private boolean downloadArtifact(final FilePath file, final URL url, final PrintStream logger) {
        try {
            InputStream inputStream = getJenkinsJob().getClient().getFile(url.toURI());
            file.copyFrom(inputStream);
            return true;
        } catch (IOException | InterruptedException | URISyntaxException e) {
            logger.println("Could not download artifact: " + FilenameUtils.getBaseName(url.toString()));
            return false;
        }
    }

    public Job getJenkinsJob() {
        return this.jenkinsJob;
    }

    public void triggerInputStep(final int buildNumber, final String triggerId) {
        try {
            String url = getJenkinsJob().getUrl() + buildNumber + "/input/" + triggerId + "/proceedEmpty";
            getJenkinsJob().getClient().post(url, true);
            getJenkinsJob().getClient().get("url");
        } catch (IOException e) {
            throw new CommandExecutionException("error triggering input step: " + e.getMessage(), e);
        }
    }

    private enum ReportType {
        Single, Comparison
    }
}
