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
 * Lists successfully deleted sessions and sessions failed to be deleted
 */
@ApiModel(description = "Lists successfully deleted sessions and sessions failed to be deleted")

public class DeletedStoredSessions {
    @SerializedName("totaldeletedbytes")
    private final Long totaldeletedbytes = null;

    @SerializedName("successfullydeleted")
    private List<DeleteDumpSessionIdentifier> successfullydeleted = null;

    @SerializedName("failedtodelete")
    private List<SessionData> failedtodelete = null;

    /**
     * Size of deleted sessions in bytes
     *
     * @return totaldeletedbytes
     **/
    @ApiModelProperty(value = "Size of deleted sessions in bytes")
    public Long getTotaldeletedbytes() {
        return totaldeletedbytes;
    }

    public DeletedStoredSessions successfullydeleted(List<DeleteDumpSessionIdentifier> successfullydeleted) {
        this.successfullydeleted = successfullydeleted;
        return this;
    }

    public DeletedStoredSessions addSuccessfullydeletedItem(DeleteDumpSessionIdentifier successfullydeletedItem) {
        if (this.successfullydeleted == null) {
            this.successfullydeleted = new ArrayList<>();
        }
        this.successfullydeleted.add(successfullydeletedItem);
        return this;
    }

    /**
     * Information about successfully deleted sessions
     *
     * @return successfullydeleted
     **/
    @ApiModelProperty(value = "Information about successfully deleted sessions")
    public List<DeleteDumpSessionIdentifier> getSuccessfullydeleted() {
        return successfullydeleted;
    }

    public void setSuccessfullydeleted(List<DeleteDumpSessionIdentifier> successfullydeleted) {
        this.successfullydeleted = successfullydeleted;
    }

    public DeletedStoredSessions failedtodelete(List<SessionData> failedtodelete) {
        this.failedtodelete = failedtodelete;
        return this;
    }

    public DeletedStoredSessions addFailedtodeleteItem(SessionData failedtodeleteItem) {
        if (this.failedtodelete == null) {
            this.failedtodelete = new ArrayList<>();
        }
        this.failedtodelete.add(failedtodeleteItem);
        return this;
    }

    /**
     * Information about sessions failed to be deleted
     *
     * @return failedtodelete
     **/
    @ApiModelProperty(value = "Information about sessions failed to be deleted")
    public List<SessionData> getFailedtodelete() {
        return failedtodelete;
    }

    public void setFailedtodelete(List<SessionData> failedtodelete) {
        this.failedtodelete = failedtodelete;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class DeletedStoredSessions {\n");

        sb.append("    totaldeletedbytes: ").append(PerfSigUIUtils.toIndentedString(totaldeletedbytes)).append("\n");
        sb.append("    successfullydeleted: ").append(PerfSigUIUtils.toIndentedString(successfullydeleted)).append("\n");
        sb.append("    failedtodelete: ").append(PerfSigUIUtils.toIndentedString(failedtodelete)).append("\n");
        sb.append("}");
        return sb.toString();
    }
}
