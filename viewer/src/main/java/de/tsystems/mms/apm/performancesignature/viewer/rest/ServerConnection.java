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

package de.tsystems.mms.apm.performancesignature.viewer.rest;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Job;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.DashboardReport;
import de.tsystems.mms.apm.performancesignature.viewer.model.CredJobPair;
import de.tsystems.mms.apm.performancesignature.viewer.model.JenkinsServerConfiguration;
import hudson.FilePath;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.mail.internet.ContentDisposition;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.logging.Logger;

public class ServerConnection {
    private static final Logger LOGGER = Logger.getLogger(ServerConnection.class.getName());
    private final String jenkinsJob;
    private JenkinsServer jenkinsServer = null;

    public ServerConnection(final String protocol, final String host, final int port, final CredJobPair pair) {
        URI uri = URI.create("");
        try {
            uri = new URI(protocol + "://" + host + ":" + port);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        if (StringUtils.isBlank(pair.getCredentialsId()))
            this.jenkinsServer = new JenkinsServer(uri);
        else
            this.jenkinsServer = new JenkinsServer(uri, pair.getCredentials().getUsername(), pair.getCredentials().getPassword().getPlainText());
        this.jenkinsJob = pair.getJenkinsJob();
    }

    public ServerConnection(final JenkinsServerConfiguration config, final CredJobPair pair) throws IOException {
        this(config.getProtocol(), config.getHost(), config.getPort(), pair);
    }

    public List<DashboardReport> getDashboardReportsFromXML() throws IOException {
        URL url = new URL(getJenkinsJob().details().getLastBuild().getUrl() + "api/xml?depth=10");
        String xml = getJenkinsJob().getClient().get(url.toString());
        try {
            DashboardXMLReader reader = new DashboardXMLReader();
            reader.parseXML(xml);
            return reader.getParsedObjects();
        } catch (Exception ex) {
            throw new ContentRetrievalException(ExceptionUtils.getStackTrace(ex) + "could not retrieve records from remote Jenkins: " + xml, ex);
        }
    }

    public boolean validateConnection() throws IOException {
        try {
            return jenkinsServer.isRunning() && getJenkinsJob() != null;
        } catch (CommandExecutionException e) {
            LOGGER.severe(ExceptionUtils.getFullStackTrace(e));
            return false;
        }
    }

    //ToDo: iterate through reports
    public boolean downloadPDFReports(final File dir, final String testCase) {
        try {
            URL url = new URL(getJenkinsJob().details().getLastBuild().getUrl() + "performance-signature/getSingleReport?testCase=" + testCase + "&number=0");
            downloadArtifact(dir, url);

            url = new URL(getJenkinsJob().details().getLastBuild().getUrl() + "performance-signature/getComparisonReport?testCase=" + testCase + "&number=0");
            downloadArtifact(dir, url);
            return true;
        } catch (Exception ex) {
            throw new CommandExecutionException("error downloading PDF Report: " + ex.getMessage(), ex);
        }
    }

    public boolean downloadSessions(final File dir, final String testCase) {
        try {
            URL url = new URL(getJenkinsJob().details().getLastBuild().getUrl() + "performance-signature/getSession?testCase=" + testCase + "&number=0");
            return downloadArtifact(dir, url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean downloadArtifact(final File dir, final URL url) {
        try {
            URLConnection conn = url.openConnection();
            ContentDisposition cd = new ContentDisposition(conn.getHeaderField("Content-Disposition"));
            String fileName = dir + File.separator + cd.getParameter("filename");

            final FilePath out = new FilePath(new File(fileName));
            out.copyFrom(conn.getInputStream());
            return true;
        } catch (Exception ex) {
            throw new CommandExecutionException("error downloading session: " + ex.getMessage(), ex);
        }
    }

    public Job getJenkinsJob() throws IOException {
        return jenkinsServer.getJob(this.jenkinsJob);
    }
}
