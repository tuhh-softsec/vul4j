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

package de.tsystems.mms.apm.performancesignature.jenkins.util;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardUsernameCredentials;
import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.SystemProfile;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.CommandExecutionException;
import de.tsystems.mms.apm.performancesignature.jenkins.DTPerfSigRecorder;
import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.Item;
import hudson.model.Project;
import hudson.security.ACL;
import hudson.tasks.Builder;
import hudson.tasks.Publisher;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rapi on 18.05.2014.
 */
public class DTPerfSigUtils {
    /**
     * @return {@link jenkins.model.Jenkins#getInstance()} if that isn't null, or die.
     */
    public static Jenkins getInstanceOrDie() {
        final Jenkins jenkins = Jenkins.getInstance();
        if (jenkins == null) {
            throw new IllegalStateException("Jenkins is not running");
        }
        return jenkins;
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(final Object obj) {
        return (T) obj;
    }

    public static BigDecimal round(final double d, final int decimalPlace) {
        if (d == 0) return BigDecimal.valueOf(0);
        BigDecimal bd = new BigDecimal(d);
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    }

    public static ListBoxModel listToListBoxModel(final List<?> arrayList) {
        final ListBoxModel listBoxModel = new ListBoxModel();
        for (Object item : arrayList) {
            if (item instanceof SystemProfile)
                listBoxModel.add(((SystemProfile) item).getId());
            else if (item instanceof String)
                listBoxModel.add((String) item);
        }
        return listBoxModel;
    }

    public static DTPerfSigRecorder getRecorder(final AbstractBuild build) {
        final Project<?, ?> project = DTPerfSigUtils.cast(build.getProject());
        final List<Publisher> publishers = project.getPublishersList().toList();
        DTPerfSigRecorder dtRecorder = null;

        //ToDo: add Flexible Publish Plugin compatibility
        for (Publisher publisher : publishers) {
            if (publisher instanceof DTPerfSigRecorder) {
                dtRecorder = (DTPerfSigRecorder) publisher;
                break;
            }
        }
        return dtRecorder;
    }

    public static <T extends Builder> T getDTPerfSigBuilder(final AbstractBuild build, final Class<T> c) {
        final Project<?, ?> project = DTPerfSigUtils.cast(build.getProject());
        final List<Builder> builders = project.getBuilders();

        for (Builder builder : builders) {
            if (c.isInstance(builder)) {
                return c.cast(builder);
            }
        }
        return null;
    }

    public static FilePath getReportDirectory(final AbstractBuild<?, ?> build) {
        final FilePath filePath = new FilePath(new File(build.getRootDir(), Messages.DTPerfSigUtils_ReportDirectory()));
        try {
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return filePath;
    }

    public static List<FilePath> getDownloadFiles(final String testCase, final AbstractBuild<?, ?> build) {
        try {
            final FilePath filePath = DTPerfSigUtils.getReportDirectory(build);
            return filePath.list(new TestCaseFileFilter(testCase));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void downloadFile(final StaplerRequest request, final StaplerResponse response, final AbstractBuild<?, ?> build) throws IOException {
        final String file = request.getParameter("f");
        if (file.matches("[^a-zA-Z0-9\\._-]+")) return;
        File downloadFile = new File(DTPerfSigUtils.getReportDirectory(build) + File.separator + file);
        FileInputStream inStream = new FileInputStream(downloadFile);

        // gets MIME type of the file
        String mimeType;
        if (file.contains("pdf")) mimeType = "application/pdf";
        else mimeType = "application/octet-stream"; // set to binary type if MIME mapping not found

        try {
            // forces download
            String headerKey = "Content-Disposition";
            String headerValue = String.format("attachment; filename=\"%s\"", file);
            response.setHeader(headerKey, headerValue);
            response.serveFile(request, inStream, downloadFile.lastModified(), 604800000, (int) downloadFile.length(), "mime-type:" + mimeType);
        } catch (ServletException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inStream);
        }
    }

    /*
    Change this in case of new Dashboardstuff
    Used for rewriting diagram titles from Time to WebService-Time etc.
    */
    public static String generateTitle(final String measure, final String chartDashlet) {
        if (StringUtils.deleteWhitespace(measure).equalsIgnoreCase(StringUtils.deleteWhitespace(chartDashlet)))
            return chartDashlet;
        else
            return chartDashlet + " - " + measure;
    }

    public static String encodeString(final String value) {
        if (value == null) return null;
        try {
            return URLEncoder.encode(value, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new CommandExecutionException(Messages.DTPerfSigUtils_EncodingFailure(), e);
        }
    }

    public static String extractXMLAttribute(final String xml, final String attribute) {
        Pattern pattern = Pattern.compile(attribute + "=(.*?)[&|\"]", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = pattern.matcher(xml);
        if (m.find())
            return m.group(1);
        else
            return "";
    }

    public static String getDurationString(final float seconds) {
        int minutes = (int) ((seconds % 3600) / 60);
        float rest = seconds % 60;
        return minutes + " min " + (int) rest + " s";
    }

    public static UsernamePasswordCredentials getCredentials(final String credsId) {
        List<StandardUsernameCredentials> credentialsList = CredentialsProvider.lookupCredentials(
                StandardUsernameCredentials.class, (Item) null, ACL.SYSTEM, Collections.<DomainRequirement>emptyList());
        for (StandardUsernameCredentials c : credentialsList) {
            if (credsId.equals(c.getId())) {
                return (UsernamePasswordCredentials) c;
            }
        }
        return null;
    }
}
