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

/**
 * Error message
 */
@ApiModel(description = "Error message")

public class Error {
    @SerializedName("code")
    private final Integer code = null;

    @SerializedName("message")
    private final String message = null;

    /**
     * HTTP error code
     *
     * @return code
     **/
    @ApiModelProperty(required = true, value = "HTTP error code")
    public Integer getCode() {
        return code;
    }

    /**
     * Error message
     *
     * @return message
     **/
    @ApiModelProperty(value = "Error message")
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Error {\n");

        sb.append("    code: ").append(PerfSigUIUtils.toIndentedString(code)).append("\n");
        sb.append("    message: ").append(PerfSigUIUtils.toIndentedString(message)).append("\n");
        sb.append("}");
        return sb.toString();
    }
}
