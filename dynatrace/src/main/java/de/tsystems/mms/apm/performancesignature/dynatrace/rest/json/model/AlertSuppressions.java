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
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * AlertSuppressions
 */

public class AlertSuppressions {
    @SerializedName("alertsuppressions")
    private List<ResponseReferenceBase> alertsuppressions = null;

    public AlertSuppressions alertsuppressions(List<ResponseReferenceBase> alertsuppressions) {
        this.alertsuppressions = alertsuppressions;
        return this;
    }

    public AlertSuppressions addAlertsuppressionsItem(ResponseReferenceBase alertsuppressionsItem) {
        if (this.alertsuppressions == null) {
            this.alertsuppressions = new ArrayList<>();
        }
        this.alertsuppressions.add(alertsuppressionsItem);
        return this;
    }

    /**
     * Alert Suppression references
     *
     * @return alertsuppressions
     **/
    @ApiModelProperty(value = "Alert Suppression references")
    public List<ResponseReferenceBase> getAlertsuppressions() {
        return alertsuppressions;
    }

    public void setAlertsuppressions(List<ResponseReferenceBase> alertsuppressions) {
        this.alertsuppressions = alertsuppressions;
    }
}

