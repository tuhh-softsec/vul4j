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

package de.tsystems.mms.apm.performancesignature.dynatrace.rest.xml.model;

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
