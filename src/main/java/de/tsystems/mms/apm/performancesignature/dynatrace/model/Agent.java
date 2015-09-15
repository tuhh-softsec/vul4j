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

package de.tsystems.mms.apm.performancesignature.dynatrace.model;

/**
 * Created by rapi on 27.10.2014.
 */
public class Agent {
    private String agentGroup, configuration, host, licenseInformation;
    private String licenseOk, name, startupTimeUTC, systemProfile, technologyType;
    private boolean capture, connected, supportsHotsensorPlacement;
    private int agentId, classLoadCount, eventCount, processId, skippedEvents;
    private int skippedPurePaths, totalClassLoadCount, totalPurePathCount;
    private double totalCpuTime, totalExecutionTime;
    private Collector collector;

    public void setValue(final String property, final String parentProperty, final String value) {
        if (property.equalsIgnoreCase(Messages.Agent_PropAgentId())) {
            if (Messages.Agent_PropAgentInformation().equalsIgnoreCase(parentProperty)) {
                setAgentId(Integer.parseInt(value));
            }
        } else if (property.equalsIgnoreCase(Messages.Agent_PropAgentGroup())) {
            this.agentGroup = value;
        } else if (property.equalsIgnoreCase(Messages.Agent_PropCapture())) {
            setCapture(Boolean.parseBoolean(value));
        } else if (property.equalsIgnoreCase(Messages.Agent_PropClassLoadCount())) {
            setClassLoadCount(Integer.parseInt(value));
        } else if (property.equalsIgnoreCase(Messages.Agent_PropConfiguration())) {
            setConfiguration(value);
        } else if (property.equalsIgnoreCase(Messages.Agent_PropConnected())) {
            setConnected(Boolean.parseBoolean(value));
        } else if (property.equalsIgnoreCase(Messages.Agent_PropEventCount())) {
            setEventCount(Integer.parseInt(value));
        } else if (property.equalsIgnoreCase(Messages.Agent_PropHost())) {
            setHost(value);
        } else if (property.equalsIgnoreCase(Messages.Agent_PropLicenseInformation())) {
            setLicenseInformation(value);
        } else if (property.equalsIgnoreCase(Messages.Agent_PropLicenseOk())) {
            setLicenseOk(value);
        } else if (property.equalsIgnoreCase(Messages.Agent_PropName())) {
            setName(value);
        } else if (property.equalsIgnoreCase(Messages.Agent_PropProcessId())) {
            setProcessId(Integer.parseInt(value));
        } else if (property.equalsIgnoreCase(Messages.Agent_PropSkippedEvents())) {
            setSkippedEvents(Integer.parseInt(value));
        } else if (property.equalsIgnoreCase(Messages.Agent_PropSkippedPurePaths())) {
            setSkippedPurePaths(Integer.parseInt(value));
        } else if (property.equalsIgnoreCase(Messages.Agent_PropStartupTimeUtc())) {
            setStartupTimeUTC(value);
        } else if (property.equalsIgnoreCase(Messages.Agent_PropSupportsHotSensorPlacement())) {
            setSupportsHotsensorPlacement(Boolean.parseBoolean(value));
        } else if (property.equalsIgnoreCase(Messages.Agent_PropSystemProfile())) {
            setSystemProfile(value);
        } else if (property.equalsIgnoreCase(Messages.Agent_PropTechnologyType())) {
            setTechnologyType(value);
        } else if (property.equalsIgnoreCase(Messages.Agent_PropTotalClassLoadCount())) {
            setTotalClassLoadCount(Integer.parseInt(value));
        } else if (property.equalsIgnoreCase(Messages.Agent_PropTotalCpuTime())) {
            setTotalCpuTime(Double.parseDouble(value));
        } else if (property.equalsIgnoreCase(Messages.Agent_PropTotalExecutionTime())) {
            setTotalExecutionTime(Double.parseDouble(value));
        } else if (property.equalsIgnoreCase(Messages.Agent_PropTotalPurePathCount())) {
            setTotalPurePathCount(Integer.parseInt(value));
        }
    }

    public int getAgentId() {
        return this.agentId;
    }

    public void setAgentId(final int agentId) {
        this.agentId = agentId;
    }

    public String getAgentGroup() {
        return this.agentGroup;
    }

    public void setAgentGroup(final String agentGroup) {
        this.agentGroup = agentGroup;
    }

    public String getLicenseInformation() {
        return this.licenseInformation;
    }

    public void setLicenseInformation(final String licenseInformation) {
        this.licenseInformation = licenseInformation;
    }

    public String getLicenseOk() {
        return this.licenseOk;
    }

    public void setLicenseOk(final String licenseOk) {
        this.licenseOk = licenseOk;
    }

    public String getStartupTimeUTC() {
        return this.startupTimeUTC;
    }

    public void setStartupTimeUTC(final String startupTimeUTC) {
        this.startupTimeUTC = startupTimeUTC;
    }

    public String getSystemProfile() {
        return this.systemProfile;
    }

    public void setSystemProfile(final String systemProfile) {
        this.systemProfile = systemProfile;
    }

    public String getTechnologyType() {
        return this.technologyType;
    }

    public void setTechnologyType(final String technologyType) {
        this.technologyType = technologyType;
    }

    public boolean isSupportsHotsensorPlacement() {
        return this.supportsHotsensorPlacement;
    }

    public void setSupportsHotsensorPlacement(final boolean supportsHotsensorPlacement) {
        this.supportsHotsensorPlacement = supportsHotsensorPlacement;
    }

    public boolean isConnected() {
        return this.connected;
    }

    public void setConnected(final boolean connected) {
        this.connected = connected;
    }

    public boolean isCapture() {
        return this.capture;
    }

    public void setCapture(final boolean capture) {
        this.capture = capture;
    }

    public int getClassLoadCount() {
        return this.classLoadCount;
    }

    public void setClassLoadCount(final int classLoadCount) {
        this.classLoadCount = classLoadCount;
    }

    public int getEventCount() {
        return this.eventCount;
    }

    public void setEventCount(final int eventCount) {
        this.eventCount = eventCount;
    }

    public int getProcessId() {
        return this.processId;
    }

    public void setProcessId(final int processId) {
        this.processId = processId;
    }

    public int getSkippedEvents() {
        return this.skippedEvents;
    }

    public void setSkippedEvents(final int skippedEvents) {
        this.skippedEvents = skippedEvents;
    }

    public int getSkippedPurePaths() {
        return this.skippedPurePaths;
    }

    public void setSkippedPurePaths(final int skippedPurePaths) {
        this.skippedPurePaths = skippedPurePaths;
    }

    public int getTotalClassLoadCount() {
        return this.totalClassLoadCount;
    }

    public void setTotalClassLoadCount(final int totalClassLoadCount) {
        this.totalClassLoadCount = totalClassLoadCount;
    }

    public int getTotalPurePathCount() {
        return this.totalPurePathCount;
    }

    public void setTotalPurePathCount(final int totalPurePathCount) {
        this.totalPurePathCount = totalPurePathCount;
    }

    public double getTotalExecutionTime() {
        return this.totalExecutionTime;
    }

    public void setTotalExecutionTime(final double totalExecutionTime) {
        this.totalExecutionTime = totalExecutionTime;
    }

    public double getTotalCpuTime() {
        return this.totalCpuTime;
    }

    public void setTotalCpuTime(final double totalCpuTime) {
        this.totalCpuTime = totalCpuTime;
    }

    public String getConfiguration() {
        return this.configuration;
    }

    public void setConfiguration(final String configuration) {
        this.configuration = configuration;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Collector getCollector() {
        return this.collector;
    }

    public void setCollector(final Collector collector) {
        this.collector = collector;
    }

    public String toString() {
        return "AgentId=" + this.agentId + ";AgentGroup" + this.agentGroup + ";Name=" + this.name + ";Host=" +
                this.host + ";ProcessId=" + this.processId + ";SystemProfile=" + this.systemProfile;
    }
}
