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

package de.tsystems.mms.apm.performancesignature.dynatrace.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by rapi on 12.04.2015.
 */
@SuppressFBWarnings(value = "URF_UNREAD_FIELD", justification = "we need this class to communicate with the Dynatrace REST Interface")
@XmlRootElement(name = "testRun")
public class RegisterTestRunRequest {
    @XmlAttribute(name = "versionBuild")
    private String versionBuild;
    @XmlAttribute(name = "versionMajor")
    private String versionMajor;
    @XmlAttribute(name = "versionMilestone")
    private String versionMilestone;
    @XmlAttribute(name = "versionMinor")
    private String versionMinor;
    @XmlAttribute(name = "versionRevision")
    private String versionRevision;
    @XmlAttribute(name = "category")
    private String category;

    public final void setVersionBuild(final String versionBuild) {
        this.versionBuild = versionBuild;
    }

    public final void setVersionMajor(final String versionMajor) {
        this.versionMajor = versionMajor;
    }

    public final void setVersionMilestone(final String versionMilestone) {
        this.versionMilestone = versionMilestone;
    }

    public final void setVersionMinor(final String versionMinor) {
        this.versionMinor = versionMinor;
    }

    public final void setVersionRevision(final String versionRevision) {
        this.versionRevision = versionRevision;
    }

    public final void setCategory(final String category) {
        this.category = category;
    }
}
