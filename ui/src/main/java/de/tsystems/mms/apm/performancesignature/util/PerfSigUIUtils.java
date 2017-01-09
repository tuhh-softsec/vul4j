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

import de.tsystems.mms.apm.performancesignature.dynatrace.model.ChartDashlet;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.IncidentChart;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.IncidentViolation;
import hudson.FilePath;
import hudson.Functions;
import hudson.model.Result;
import hudson.model.Run;
import hudson.util.Area;
import jenkins.model.Jenkins;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class PerfSigUIUtils {
    private PerfSigUIUtils() {
    }

    public static BigDecimal round(final double d, final int decimalPlace) {
        if (d == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal bd = BigDecimal.valueOf(d);
        bd = bd.setScale(d % 1 == 0 ? 0 : decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    }

    public static FilePath getReportDirectory(final Run<?, ?> run) throws IOException, InterruptedException {
        FilePath reportDirectory = new FilePath(new File(run.getRootDir(), "performance-signature"));
        if (!reportDirectory.exists()) {
            reportDirectory.mkdirs();
        }
        return reportDirectory;
    }

    public static List<FilePath> getDownloadFiles(final String testCase, final Run<?, ?> build) throws IOException, InterruptedException {
        FilePath filePath = PerfSigUIUtils.getReportDirectory(build);
        return filePath.list(new RegexFileFilter(testCase));
    }

    public static String removeExtension(final String fileName) {
        return FilenameUtils.removeExtension(fileName);
    }

    /*
    Change this in case of new Dashboardstuff
    Used for rewriting diagram titles from Time to WebService-Time etc.
    */
    public static String generateTitle(final String measure, final String chartDashlet, String aggregation) {
        String chartDashletName;
        if (StringUtils.deleteWhitespace(measure).equalsIgnoreCase(StringUtils.deleteWhitespace(chartDashlet))) {
            chartDashletName = chartDashlet;
        } else {
            chartDashletName = chartDashlet + " - " + measure;
        }
        return chartDashletName + " (" + aggregation + ")";
    }

    public static List<ChartDashlet> sortChartDashletList(final List<ChartDashlet> list) {
        Collections.sort(list, new Comparator<ChartDashlet>() {
            @Override
            public int compare(final ChartDashlet o1, final ChartDashlet o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return list;
    }

    public static String encodeString(final String value) {
        if (StringUtils.isBlank(value)) {
            return "";
        }
        try {
            return URLEncoder.encode(value, CharEncoding.UTF_8).replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(Messages.PerfSigUIUtils_EncodingFailure(), e);
        }
    }

    public static String getDurationString(final float seconds) {
        int minutes = (int) ((seconds % 3600) / 60);
        float rest = seconds % 60;
        return minutes + " min " + (int) rest + " s";
    }

    public static Area calcDefaultSize() {
        Area res = Functions.getScreenResolution();
        if (res != null && res.width <= 800) {
            return new Area(250, 100);
        } else {
            return new Area(500, 200);
        }
    }

    /**
     * gets removed if jenkins version hits 1.653
     */
    @Nonnull
    public static Jenkins getInstance() throws IllegalStateException {
        Jenkins instance = Jenkins.getInstance();
        if (instance == null) {
            throw new IllegalStateException("Jenkins has not been started, or was already shut down");
        } else {
            return instance;
        }
    }

    public static void handleIncidents(final Run<?, ?> run, final List<IncidentChart> incidents, final PrintStream logger, final int nonFunctionalFailure) {
        int numWarning = 0, numSevere = 0;
        if (incidents != null && !incidents.isEmpty()) {
            logger.println(Messages.PerfSigUIUtils_FollowingIncidents());
            for (IncidentChart incident : incidents) {
                for (IncidentViolation violation : incident.getViolations()) {
                    switch (violation.getSeverity()) {
                        case SEVERE:
                            logger.println(Messages.PerfSigUIUtils_SevereIncident(incident.getRule(), violation.getRule(), violation.getDescription()));
                            numSevere++;
                            break;
                        case WARNING:
                            logger.println(Messages.PerfSigUIUtils_WarningIncident(incident.getRule(), violation.getRule(), violation.getDescription()));
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
                        logger.println(Messages.PerfSigUIUtils_BuildsStatusSevereIncidentsFailed());
                        run.setResult(Result.FAILURE);
                    }
                    break;
                case 2:
                    if (numSevere > 0 || numWarning > 0) {
                        logger.println(Messages.PerfSigUIUtils_BuildsStatusWarningIncidentsFailed());
                        run.setResult(Result.FAILURE);
                    }
                    break;
                case 3:
                    if (numSevere > 0) {
                        logger.println(Messages.PerfSigUIUtils_BuildsStatusSevereIncidentsUnstable());
                        run.setResult(Result.UNSTABLE);
                    }
                    break;
                case 4:
                    if (numSevere > 0 || numWarning > 0) {
                        logger.println(Messages.PerfSigUIUtils_BuildsStatusWarningIncidentsUnstable());
                        run.setResult(Result.UNSTABLE);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public static boolean checkNotNullOrEmpty(final String string) {
        return StringUtils.isNotBlank(string);
    }

    public static boolean checkNotEmptyAndIsNumber(final String number) {
        return StringUtils.isNotBlank(number) && NumberUtils.isNumber(number);
    }
}
