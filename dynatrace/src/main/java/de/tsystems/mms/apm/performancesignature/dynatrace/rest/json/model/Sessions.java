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
 * List of available sessions
 */
@ApiModel(description = "List of available sessions")

public class Sessions {
    @SerializedName("sessions")
    private List<SessionData> sessions = null;

    public Sessions sessions(List<SessionData> sessions) {
        this.sessions = sessions;
        return this;
    }

    public Sessions addSessionsItem(SessionData sessionsItem) {
        if (this.sessions == null) {
            this.sessions = new ArrayList<>();
        }
        this.sessions.add(sessionsItem);
        return this;
    }

    /**
     * List of sessions
     *
     * @return sessions
     **/
    @ApiModelProperty(value = "List of sessions")
    public List<SessionData> getSessions() {
        return sessions;
    }

    public void setSessions(List<SessionData> sessions) {
        this.sessions = sessions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Sessions {\n");

        sb.append("    sessions: ").append(PerfSigUIUtils.toIndentedString(sessions)).append("\n");
        sb.append("}");
        return sb.toString();
    }
}
