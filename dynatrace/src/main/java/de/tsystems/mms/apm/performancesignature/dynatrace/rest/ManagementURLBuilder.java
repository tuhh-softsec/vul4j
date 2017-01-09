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

import de.tsystems.mms.apm.performancesignature.util.PerfSigUIUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

public class ManagementURLBuilder {
    private static final Logger LOGGER = Logger.getLogger(ManagementURLBuilder.class.getName());
    private String serverAddress;
    private String parameters;

    public String getPostParameters() {
        return this.parameters;
    }

    public void setServerAddress(final String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public URL serverVersionURL() {
        try {
            final String s = String.format("%1$s/rest/management/version", this.serverAddress);
            return new URL(s);
        } catch (MalformedURLException e) {
            LOGGER.severe(ExceptionUtils.getFullStackTrace(e));
        }
        return null;
    }

    public URL reanalyzeSessionURL(final String sessionName) {
        try {
            return new URL(String.format("%1$s/rest/management/sessions/%2$s/reanalyze", this.serverAddress,
                    PerfSigUIUtils.encodeString(sessionName)));
        } catch (MalformedURLException e) {
            LOGGER.severe(ExceptionUtils.getFullStackTrace(e));
        }
        return null;
    }

    public URL reanalyzeSessionStatusURL(final String sessionName) {
        try {
            return new URL(String.format("%1$s/rest/management/sessions/%2$s/reanalyze/finished", this.serverAddress,
                    PerfSigUIUtils.encodeString(sessionName)));
        } catch (MalformedURLException e) {
            LOGGER.severe(ExceptionUtils.getFullStackTrace(e));
        }
        return null;
    }


    public URL storePurePathsURL(final String profileName, final String sessionName, final String timeframeStart, final String timeframeEnd,
                                 String recordingOption, final boolean sessionLocked, final boolean appendTimestamp) {
        if (StringUtils.isBlank(recordingOption)) {
            recordingOption = "all";
        }
        try {
            return new URL(String.format("%1$s/rest/management/profiles/%2$s/storepurepaths?storedSessionName=%3$s&timeframeStart=%4$s&timeframeEnd=%5$s&" +
                            "recordingOption=%6$s&isSessionLocked=%7$s&appendTimestamp=%8$s",
                    this.serverAddress, PerfSigUIUtils.encodeString(profileName), PerfSigUIUtils.encodeString(sessionName), timeframeStart, timeframeEnd,
                    recordingOption, sessionLocked, appendTimestamp));
        } catch (MalformedURLException e) {
            LOGGER.severe(ExceptionUtils.getFullStackTrace(e));
        }
        return null;
    }

    public URL startRecordingURL(final String profileName, final String sessionName, final String description,
                                 String recordingOption, final boolean sessionLocked, final boolean isTimeStampAllowed) {
        if (StringUtils.isBlank(recordingOption)) {
            recordingOption = "all";
        }
        try {
            this.parameters = String.format("recordingOption=%1$s&isSessionLocked=%2$s&isTimeStampAllowed=%3$s&description=%4$s&presentableName=%5$s",
                    recordingOption, sessionLocked, isTimeStampAllowed, StringUtils.isBlank(description) ? "" : PerfSigUIUtils.encodeString(description),
                    StringUtils.isBlank(sessionName) ? PerfSigUIUtils.encodeString(profileName) : PerfSigUIUtils.encodeString(sessionName));
            final String url = String.format("%1$s/rest/management/profiles/%2$s/startrecording", this.serverAddress,
                    PerfSigUIUtils.encodeString(profileName));
            return new URL(url);
        } catch (MalformedURLException e) {
            LOGGER.severe(ExceptionUtils.getFullStackTrace(e));
        }
        return null;
    }

    public URL stopRecordingURL(final String profileName) {
        try {
            final String s = String.format("%1$s/rest/management/profiles/%2$s/stoprecording", this.serverAddress,
                    PerfSigUIUtils.encodeString(profileName));
            return new URL(s);
        } catch (MalformedURLException e) {
            LOGGER.severe(ExceptionUtils.getFullStackTrace(e));
        }
        return null;
    }

    public URL listProfilesURL() {
        try {
            final String s = String.format("%1$s/rest/management/profiles/", this.serverAddress);
            return new URL(s);
        } catch (MalformedURLException e) {
            LOGGER.severe(ExceptionUtils.getFullStackTrace(e));
        }
        return null;
    }

    public URL listConfigurationsURL(final String profileName) {
        try {
            final String s = String.format("%1$s/rest/management/profiles/%2$s/configurations", this.serverAddress,
                    PerfSigUIUtils.encodeString(profileName));
            return new URL(s);
        } catch (MalformedURLException e) {
            LOGGER.severe(ExceptionUtils.getFullStackTrace(e));
        }
        return null;
    }

    public URL activateConfigurationURL(final String profileName, final String configuration) {
        try {
            final String s = String.format("%1$s/rest/management/profiles/%2$s/configurations/%3$s/activate", this.serverAddress,
                    PerfSigUIUtils.encodeString(profileName), PerfSigUIUtils.encodeString(configuration));
            return new URL(s);
        } catch (MalformedURLException e) {
            LOGGER.severe(ExceptionUtils.getFullStackTrace(e));
        }
        return null;
    }

    public URL listAgentsURL() {
        try {
            final String s = String.format("%1$s/rest/management/agents", this.serverAddress);
            return new URL(s);
        } catch (MalformedURLException e) {
            LOGGER.severe(ExceptionUtils.getFullStackTrace(e));
        }
        return null;
    }

    public URL hotSensorPlacementURL(final int agentId) {
        try {
            final String s = String.format("%1$s/rest/management/agents/%2$s/hotsensorplacement", this.serverAddress,
                    PerfSigUIUtils.encodeString(String.valueOf(agentId)));
            return new URL(s);
        } catch (MalformedURLException e) {
            LOGGER.severe(ExceptionUtils.getFullStackTrace(e));
        }
        return null;
    }

    public URL listSessionsURL() {
        try {
            final String s = String.format("%1$s/rest/management/sessions?type=purepath", this.serverAddress);
            return new URL(s);
        } catch (MalformedURLException e) {
            LOGGER.severe(ExceptionUtils.getFullStackTrace(e));
        }
        return null;
    }

    public URL listDashboardsURL() {
        try {
            final String s = String.format("%1$s/rest/management/dashboards", this.serverAddress);
            return new URL(s);
        } catch (MalformedURLException e) {
            LOGGER.severe(ExceptionUtils.getFullStackTrace(e));
        }
        return null;
    }

    public URL downloadSessionURL(final String sessionName) {
        try {
            final String s = String.format("%1$s/rest/management/sessions/%2$s/export", this.serverAddress, PerfSigUIUtils.encodeString(sessionName));
            return new URL(s);
        } catch (MalformedURLException e) {
            LOGGER.severe(ExceptionUtils.getFullStackTrace(e));
        }
        return null;
    }

    public StringBuilder resourceDumpURL(final String agentName, final String hostName, final int processId, final boolean sessionLocked) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("agentName=%1$s&isSessionLocked=%2$s&hostName=%3$s&processId=%4$s",
                agentName, sessionLocked ? "true" : "false", hostName, String.valueOf(processId)));

        return builder;
    }

    public URL memoryDumpStatusURL(final String profileName, final String memoryDumpName) {
        try {
            return new URL(String.format("%1$s/rest/management/profiles/%2$s/memorydumpcreated/%3$s", this.serverAddress,
                    PerfSigUIUtils.encodeString(profileName), PerfSigUIUtils.encodeString(memoryDumpName)));
        } catch (MalformedURLException e) {
            LOGGER.severe(ExceptionUtils.getFullStackTrace(e));
        }
        return null;
    }

    public URL memoryDumpURL(final String profileName, final String agentName, final String hostName, final int processId,
                             String dumpType, final boolean sessionLocked, final boolean captureStrings, final boolean capturePrimitives,
                             final boolean autoPostProcess, final boolean dogc) {
        if (StringUtils.isBlank(dumpType)) {
            dumpType = "simple";
        }
        try {
            StringBuilder builder = resourceDumpURL(agentName, hostName, processId, sessionLocked);
            builder.append("&type=").append(dumpType);
            builder.append(captureStrings ? "&capturestrings=true" : "");
            builder.append(capturePrimitives ? "&captureprimitives=true" : "");
            builder.append(autoPostProcess ? "&autopostprocess=true" : "");
            builder.append(dogc ? "&dogc=true" : "");

            this.parameters = builder.toString();

            return new URL(String.format("%1$s/rest/management/profiles/%2$s/memorydump",
                    this.serverAddress, PerfSigUIUtils.encodeString(profileName)));
        } catch (MalformedURLException e) {
            LOGGER.severe(ExceptionUtils.getFullStackTrace(e));
        }
        return null;
    }

    public URL threadDumpStatusURL(final String profileName, final String threadDumpName) {
        try {
            return new URL(String.format("%1$s/rest/management/profiles/%2$s/threaddumpcreated/%3$s", this.serverAddress,
                    PerfSigUIUtils.encodeString(profileName), PerfSigUIUtils.encodeString(threadDumpName)));
        } catch (MalformedURLException e) {
            LOGGER.severe(ExceptionUtils.getFullStackTrace(e));
        }
        return null;
    }

    public URL threadDumpURL(final String profileName, final String agentName, final String hostName, final int processId, final boolean sessionLocked) {
        try {
            this.parameters = String.format("agentName=%1$s&isSessionLocked=%2$s&hostName=%3$s&processId=%4$s", agentName, sessionLocked, hostName, processId);
            return new URL(String.format("%1$s/rest/management/profiles/%2$s/threaddump", this.serverAddress, PerfSigUIUtils.encodeString(profileName)));
        } catch (MalformedURLException e) {
            LOGGER.severe(ExceptionUtils.getFullStackTrace(e));
        }
        return null;
    }

    public URL registerTestRunURL(final String profileName) {
        try {
            return new URL(String.format("%1$s/rest/management/profiles/%2$s/testruns", this.serverAddress, PerfSigUIUtils.encodeString(profileName)));
        } catch (MalformedURLException e) {
            LOGGER.severe(ExceptionUtils.getFullStackTrace(e));
        }
        return null;
    }

    public URL testRunDetailsURL(final String profileName, final String testRunID) {
        try {
            return new URL(String.format("%1$s/rest/management/profiles/%2$s/testruns/%3$s.xml", this.serverAddress,
                    PerfSigUIUtils.encodeString(profileName), testRunID));
        } catch (MalformedURLException e) {
            LOGGER.severe(ExceptionUtils.getFullStackTrace(e));
        }
        return null;
    }
}
