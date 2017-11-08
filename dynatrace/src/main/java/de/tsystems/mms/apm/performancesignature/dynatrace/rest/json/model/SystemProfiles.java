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
 * SystemProfiles
 */

public class SystemProfiles {
    @SerializedName("systemprofiles")
    private List<SystemProfileReference> systemprofiles = null;

    public SystemProfiles systemprofiles(List<SystemProfileReference> systemprofiles) {
        this.systemprofiles = systemprofiles;
        return this;
    }

    public SystemProfiles addSystemprofilesItem(SystemProfileReference systemprofilesItem) {
        if (this.systemprofiles == null) {
            this.systemprofiles = new ArrayList<>();
        }
        this.systemprofiles.add(systemprofilesItem);
        return this;
    }

    /**
     * List of System Profiles
     *
     * @return systemprofiles
     **/
    @ApiModelProperty(value = "List of System Profiles")
    public List<SystemProfileReference> getSystemprofiles() {
        return systemprofiles;
    }

    public void setSystemprofiles(List<SystemProfileReference> systemprofiles) {
        this.systemprofiles = systemprofiles;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class SystemProfiles {\n");

        sb.append("    systemprofiles: ").append(PerfSigUIUtils.toIndentedString(systemprofiles)).append("\n");
        sb.append("}");
        return sb.toString();
    }
}

