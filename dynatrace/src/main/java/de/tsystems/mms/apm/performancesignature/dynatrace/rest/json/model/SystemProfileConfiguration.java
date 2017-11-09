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

/**
 * SystemProfileConfiguration
 */

public class SystemProfileConfiguration {
    @SerializedName("id")
    private final String id = null;

    @SerializedName("href")
    private final String href = null;

    @SerializedName("isactive")
    private final Boolean isactive = false;

    /**
     * ID of the reference
     *
     * @return id
     **/
    @ApiModelProperty(value = "ID of the reference")
    public String getId() {
        return id;
    }

    /**
     * Base URL of the REST resource. Further information can be retrieved from this URL or its subresources
     *
     * @return href
     **/
    @ApiModelProperty(value = "Base URL of the REST resource. Further information can be retrieved from this URL or its subresources")
    public String getHref() {
        return href;
    }

    /**
     * Get isactive
     *
     * @return isactive
     **/

    public Boolean getIsactive() {
        return isactive;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class SystemProfileConfiguration {\n");

        sb.append("    id: ").append(PerfSigUIUtils.toIndentedString(id)).append("\n");
        sb.append("    href: ").append(PerfSigUIUtils.toIndentedString(href)).append("\n");
        sb.append("    isactive: ").append(PerfSigUIUtils.toIndentedString(isactive)).append("\n");
        sb.append("}");
        return sb.toString();
    }
}
