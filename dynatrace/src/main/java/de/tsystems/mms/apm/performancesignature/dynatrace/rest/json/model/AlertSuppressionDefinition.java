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
 * Alert Suppression record definition
 */
@ApiModel(description = "Alert Suppression record definition")

public class AlertSuppressionDefinition {
    @SerializedName("start")
    private final String start = null;

    @SerializedName("endby")
    private final String endby = null;
    @SerializedName("cron")
    private final String cron = null;
    @SerializedName("once")
    private final Boolean once = false;
    @SerializedName("businesshours")
    private final String businesshours = null;
    @SerializedName("duration")
    private Duration duration = null;
    @SerializedName("systemprofiles")
    private List<AlertSuppressionProfileConfig> systemprofiles = null;

    /**
     * ISO 8601 compatible date/time of format: yyyy-MM-dd&#39;T&#39;HH:mm:ss.SSSXXX, seconds and milliseconds will be ignored
     *
     * @return start
     **/
    @ApiModelProperty(example = "2016-05-11T11:35:00.000+02:00", value = "ISO 8601 compatible date/time of format: yyyy-MM-dd'T'HH:mm:ss.SSSXXX, seconds and milliseconds will be ignored")
    public String getStart() {
        return start;
    }

    /**
     * End of the scheduling period, will be ignored for non-repeating downtimes. ISO 8601 compatible date/time of format: yyyy-MM-dd&#39;T&#39;HH:mm:ss.SSSXXX, seconds and milliseconds will be ignored.
     *
     * @return endby
     **/
    @ApiModelProperty(example = "2016-05-11T11:35:00.000+02:00", value = "End of the scheduling period, will be ignored for non-repeating downtimes. ISO 8601 compatible date/time of format: yyyy-MM-dd'T'HH:mm:ss.SSSXXX, seconds and milliseconds will be ignored.")
    public String getEndby() {
        return endby;
    }

    public AlertSuppressionDefinition duration(Duration duration) {
        this.duration = duration;
        return this;
    }

    /**
     * Get duration
     *
     * @return duration
     **/

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    /**
     * Scheduling expression for Quartz cron trigger
     *
     * @return cron
     **/
    @ApiModelProperty(value = "Scheduling expression for Quartz cron trigger")
    public String getCron() {
        return cron;
    }

    /**
     * True if the Alert Suppression is only scheduled once. If true, the cron expression will be ignored. If false, a cron expression has to be provided.
     *
     * @return once
     **/
    @ApiModelProperty(value = "True if the Alert Suppression is only scheduled once. If true, the cron expression will be ignored. If false, a cron expression has to be provided.")
    public Boolean getOnce() {
        return once;
    }

    public AlertSuppressionDefinition systemprofiles(List<AlertSuppressionProfileConfig> systemprofiles) {
        this.systemprofiles = systemprofiles;
        return this;
    }

    public AlertSuppressionDefinition addSystemprofilesItem(AlertSuppressionProfileConfig systemprofilesItem) {
        if (this.systemprofiles == null) {
            this.systemprofiles = new ArrayList<>();
        }
        this.systemprofiles.add(systemprofilesItem);
        return this;
    }

    /**
     * System Profiles and Incident rules the Alert Suppression relates to
     *
     * @return systemprofiles
     **/
    @ApiModelProperty(value = "System Profiles and Incident rules the Alert Suppression relates to")
    public List<AlertSuppressionProfileConfig> getSystemprofiles() {
        return systemprofiles;
    }

    public void setSystemprofiles(List<AlertSuppressionProfileConfig> systemprofiles) {
        this.systemprofiles = systemprofiles;
    }

    /**
     * Name of defined business hours
     *
     * @return businesshours
     **/
    @ApiModelProperty(value = "Name of defined business hours")
    public String getBusinesshours() {
        return businesshours;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AlertSuppressionDefinition {\n");

        sb.append("    start: ").append(PerfSigUIUtils.toIndentedString(start)).append("\n");
        sb.append("    endby: ").append(PerfSigUIUtils.toIndentedString(endby)).append("\n");
        sb.append("    duration: ").append(PerfSigUIUtils.toIndentedString(duration)).append("\n");
        sb.append("    cron: ").append(PerfSigUIUtils.toIndentedString(cron)).append("\n");
        sb.append("    once: ").append(PerfSigUIUtils.toIndentedString(once)).append("\n");
        sb.append("    systemprofiles: ").append(PerfSigUIUtils.toIndentedString(systemprofiles)).append("\n");
        sb.append("    businesshours: ").append(PerfSigUIUtils.toIndentedString(businesshours)).append("\n");
        sb.append("}");
        return sb.toString();
    }
}

