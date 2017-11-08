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

package de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model;

import com.google.gson.annotations.SerializedName;
import de.tsystems.mms.apm.performancesignature.util.PerfSigUIUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Lists selected Incident rules for a System Profile
 */
@ApiModel(description = "Lists selected Incident rules for a System Profile")

public class AlertSuppressionProfileConfig {
    @SerializedName("systemprofile")
    private final String systemprofile = null;

    @SerializedName("incidentrules")
    private List<String> incidentrules = null;

    /**
     * System Profile name
     *
     * @return systemprofile
     **/
    @ApiModelProperty(value = "System Profile name")
    public String getSystemprofile() {
        return systemprofile;
    }

    public AlertSuppressionProfileConfig incidentrules(List<String> incidentrules) {
        this.incidentrules = incidentrules;
        return this;
    }

    public AlertSuppressionProfileConfig addIncidentrulesItem(String incidentrulesItem) {
        if (this.incidentrules == null) {
            this.incidentrules = new ArrayList<>();
        }
        this.incidentrules.add(incidentrulesItem);
        return this;
    }

    /**
     * Incident Rule names
     *
     * @return incidentrules
     **/
    @ApiModelProperty(value = "Incident Rule names")
    public List<String> getIncidentrules() {
        return incidentrules;
    }

    public void setIncidentrules(List<String> incidentrules) {
        this.incidentrules = incidentrules;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AlertSuppressionProfileConfig {\n");

        sb.append("    systemprofile: ").append(PerfSigUIUtils.toIndentedString(systemprofile)).append("\n");
        sb.append("    incidentrules: ").append(PerfSigUIUtils.toIndentedString(incidentrules)).append("\n");
        sb.append("}");
        return sb.toString();
    }
}

