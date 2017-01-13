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

package de.tsystems.mms.apm.performancesignature.dynatrace.rest.model;

public class Agent {
    private String agentGroup, configuration, host, licenseInformation;
    private String licenseOk, name, startupTimeUTC, systemProfile, technologyType;
    private boolean capture, connected, supportsHotsensorPlacement;
    private int agentId, classLoadCount, eventCount, processId, skippedEvents;
    private int skippedPurePaths, totalClassLoadCount, totalPurePathCount;
    private double totalCpuTime, totalExecutionTime;
    private Collector collector;

    public void setValue(final String property, final String parentProperty, final String value) {
        if (property.equalsIgnoreCase("agentid")) {
            if (parentProperty.equalsIgnoreCase("agentinformation")) {
                this.agentId = Integer.parseInt(value);
            }
        } else if (property.equalsIgnoreCase("agentgroup")) {
            this.agentGroup = value;
        } else if (property.equalsIgnoreCase("capture")) {
            this.capture = Boolean.parseBoolean(value);
        } else if (property.equalsIgnoreCase("classloadcount")) {
            this.classLoadCount = Integer.parseInt(value);
        } else if (property.equalsIgnoreCase("configuration")) {
            this.configuration = value;
        } else if (property.equalsIgnoreCase("connected")) {
            this.connected = Boolean.parseBoolean(value);
        } else if (property.equalsIgnoreCase("eventcount")) {
            this.eventCount = Integer.parseInt(value);
        } else if (property.equalsIgnoreCase("host")) {
            this.host = value;
        } else if (property.equalsIgnoreCase("licenseinformation")) {
            this.licenseInformation = value;
        } else if (property.equalsIgnoreCase("licenseok")) {
            this.licenseOk = value;
        } else if (property.equalsIgnoreCase("name")) {
            this.name = value;
        } else if (property.equalsIgnoreCase("processid")) {
            this.processId = Integer.parseInt(value);
        } else if (property.equalsIgnoreCase("skippedevents")) {
            this.skippedEvents = Integer.parseInt(value);
        } else if (property.equalsIgnoreCase("skippedpurepaths")) {
            this.skippedPurePaths = Integer.parseInt(value);
        } else if (property.equalsIgnoreCase("startuptimeutc")) {
            this.startupTimeUTC = value;
        } else if (property.equalsIgnoreCase("hotUpdateable")) {
            this.supportsHotsensorPlacement = Boolean.parseBoolean(value);
        } else if (property.equalsIgnoreCase("systemProfile")) {
            this.systemProfile = value;
        } else if (property.equalsIgnoreCase("technologyType")) {
            this.technologyType = value;
        } else if (property.equalsIgnoreCase("totalClassLoadCount")) {
            this.totalClassLoadCount = Integer.parseInt(value);
        } else if (property.equalsIgnoreCase("totalCpuTime")) {
            this.totalCpuTime = Double.parseDouble(value);
        } else if (property.equalsIgnoreCase("totalExecutionTime")) {
            this.totalExecutionTime = Double.parseDouble(value);
        } else if (property.equalsIgnoreCase("totalPurePathCount")) {
            this.totalPurePathCount = Integer.parseInt(value);
        }
    }

    public int getAgentId() {
        return this.agentId;
    }

    public String getAgentGroup() {
        return this.agentGroup;
    }

    public String getLicenseInformation() {
        return this.licenseInformation;
    }

    public String getLicenseOk() {
        return this.licenseOk;
    }

    public String getStartupTimeUTC() {
        return this.startupTimeUTC;
    }

    public String getSystemProfile() {
        return this.systemProfile;
    }

    public String getTechnologyType() {
        return this.technologyType;
    }

    public boolean isSupportsHotsensorPlacement() {
        return this.supportsHotsensorPlacement;
    }

    public boolean isConnected() {
        return this.connected;
    }

    public boolean isCapture() {
        return this.capture;
    }

    public int getClassLoadCount() {
        return this.classLoadCount;
    }

    public int getEventCount() {
        return this.eventCount;
    }

    public int getProcessId() {
        return this.processId;
    }

    public int getSkippedEvents() {
        return this.skippedEvents;
    }

    public int getSkippedPurePaths() {
        return this.skippedPurePaths;
    }

    public int getTotalClassLoadCount() {
        return this.totalClassLoadCount;
    }

    public int getTotalPurePathCount() {
        return this.totalPurePathCount;
    }

    public double getTotalExecutionTime() {
        return this.totalExecutionTime;
    }

    public double getTotalCpuTime() {
        return this.totalCpuTime;
    }

    public String getConfiguration() {
        return this.configuration;
    }

    public String getHost() {
        return this.host;
    }

    public String getName() {
        return this.name;
    }

    public Collector getCollector() {
        return this.collector;
    }

    public void setCollector(Collector collector) {
        this.collector = collector;
    }
}
