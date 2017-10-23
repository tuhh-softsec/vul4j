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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "agentinformation")
public class Agent {
    @XmlElement
    private String agentGroup;
    @XmlElement
    private String configuration;
    @XmlElement
    private String host;
    @XmlElement
    private String licenseInformation;
    @XmlElement
    private String licenseOk;
    @XmlElement
    private String name;
    @XmlElement
    private String startupTimeUTC;
    @XmlElement
    private String systemProfile;
    @XmlElement
    private String technologyType;
    @XmlElement
    private boolean capture;
    @XmlElement
    private boolean connected;
    @XmlElement
    private boolean supportsHotsensorPlacement;
    @XmlElement
    private int agentId;
    @XmlElement
    private int classLoadCount;
    @XmlElement
    private int eventCount;
    @XmlElement
    private int processId;
    @XmlElement
    private int skippedEvents;
    @XmlElement
    private int skippedPurePaths;
    @XmlElement
    private int totalClassLoadCount;
    @XmlElement
    private int totalPurePathCount;
    @XmlElement
    private double totalCpuTime;
    @XmlElement
    private double totalExecutionTime;

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
}
