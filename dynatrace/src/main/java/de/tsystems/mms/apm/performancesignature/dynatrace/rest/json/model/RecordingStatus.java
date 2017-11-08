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
 * RecordingStatus
 */

public class RecordingStatus {
    @SerializedName("recording")
    private final boolean recording;

    public RecordingStatus(boolean recording) {
        this.recording = recording;
    }

    /**
     * Status of recording, true if recording is ongoing
     *
     * @return recording
     **/
    @ApiModelProperty(required = true, value = "Status of recording, true if recording is ongoing")
    public boolean getRecording() {
        return recording;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class RecordingStatus {\n");

        sb.append("    recording: ").append(PerfSigUIUtils.toIndentedString(recording)).append("\n");
        sb.append("}");
        return sb.toString();
    }
}
