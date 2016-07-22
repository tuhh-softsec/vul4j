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

package de.tsystems.mms.apm.performancesignature.util;

import hudson.FilePath;
import hudson.Functions;
import hudson.model.Run;
import hudson.util.Area;
import jenkins.model.Jenkins;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.List;

public final class PerfSigUIUtils {
    private PerfSigUIUtils() {
    }

    public static BigDecimal round(final double d, final int decimalPlace) {
        if (d == 0) return BigDecimal.valueOf(0);
        BigDecimal bd = new BigDecimal(d);
        bd = bd.setScale(d % 1 == 0 ? 0 : decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    }

    public static File getReportDirectory(final Run<?, ?> run) throws IOException {
        File reportDirectory = new File(run.getRootDir(), Messages.PerfSigUtils_ReportDirectory());
        if (!reportDirectory.exists()) {
            if (!reportDirectory.mkdirs()) throw new IOException("failed to create report directory");
        }
        return reportDirectory;
    }

    public static List<FilePath> getDownloadFiles(final String testCase, final Run<?, ?> build) throws IOException, InterruptedException {
        FilePath filePath = new FilePath(PerfSigUIUtils.getReportDirectory(build));
        return filePath.list(new RegexFileFilter(testCase));
    }

    public static String removeExtension(final String fileName) {
        return FilenameUtils.removeExtension(fileName);
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
        if (StringUtils.isBlank(value)) return "";
        try {
            return URLEncoder.encode(value, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(Messages.PerfSigUtils_EncodingFailure(), e);
        }
    }

    public static String getDurationString(final float seconds) {
        int minutes = (int) ((seconds % 3600) / 60);
        float rest = seconds % 60;
        return minutes + " min " + (int) rest + " s";
    }

    public static Area calcDefaultSize() {
        Area res = Functions.getScreenResolution();
        if (res != null && res.width <= 800)
            return new Area(250, 100);
        else
            return new Area(500, 200);
    }

    @Nonnull
    public static Jenkins getActiveInstance() throws IllegalStateException {
        Jenkins instance = Jenkins.getInstance();
        if (instance == null) {
            throw new IllegalStateException("Jenkins has not been started, or was already shut down");
        } else {
            return instance;
        }
    }
}
