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
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Alerts
 */

public class Alerts {
    @SerializedName("alerts")
    private List<AlertReference> alerts = null;

    public Alerts alerts(List<AlertReference> alerts) {
        this.alerts = alerts;
        return this;
    }

    public Alerts addAlertsItem(AlertReference alertsItem) {
        if (this.alerts == null) {
            this.alerts = new ArrayList<>();
        }
        this.alerts.add(alertsItem);
        return this;
    }

    /**
     * List of alert references
     *
     * @return alerts
     **/
    @ApiModelProperty(value = "List of alert references")
    public List<AlertReference> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<AlertReference> alerts) {
        this.alerts = alerts;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Alerts {\n");

        sb.append("    alerts: ").append(PerfSigUIUtils.toIndentedString(alerts)).append("\n");
        sb.append("}");
        return sb.toString();
    }
}

