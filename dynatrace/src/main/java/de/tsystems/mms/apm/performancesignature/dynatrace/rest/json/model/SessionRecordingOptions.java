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

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.tsystems.mms.apm.performancesignature.util.PerfSigUIUtils;
import io.swagger.annotations.ApiModelProperty;

import java.io.IOException;

/**
 * SessionRecordingOptions
 */

public class SessionRecordingOptions {
    @SerializedName("sessionname")
    private final String sessionname;
    @SerializedName("description")
    private final String description;
    @SerializedName("appendtimestamp")
    private final boolean appendtimestamp;
    @SerializedName("recordingoption")
    private final RecordingoptionEnum recordingoption;
    @SerializedName("locksession")
    private final boolean locksession;

    public SessionRecordingOptions(String sessionname, String description, boolean appendtimestamp, String recordingoption, boolean locksession) {
        this.sessionname = sessionname;
        this.description = description;
        this.appendtimestamp = appendtimestamp;
        this.recordingoption = RecordingoptionEnum.fromValue(recordingoption);
        this.locksession = locksession;
    }

    /**
     * User-readable presentable name for the session to be stored
     *
     * @return sessionname
     **/
    @ApiModelProperty(value = "User-readable presentable name for the session to be stored")
    public String getSessionname() {
        return sessionname;
    }

    /**
     * Description for the session to be stored
     *
     * @return description
     **/
    @ApiModelProperty(value = "Description for the session to be stored")
    public String getDescription() {
        return description;
    }

    /**
     * true to append timestamp information to recorded session name, otherwise false (default is false)
     *
     * @return appendtimestamp
     **/
    @ApiModelProperty(example = "false", value = "true to append timestamp information to recorded session name, otherwise false (default is false)")
    public boolean getAppendtimestamp() {
        return appendtimestamp;
    }

    /**
     * Recording option, possible values: &#39;all&#39;, &#39;violations&#39;, &#39;timeseries&#39;
     *
     * @return recordingoption
     **/
    @ApiModelProperty(value = "Recording option, possible values: 'all', 'violations', 'timeseries'")
    public RecordingoptionEnum getRecordingoption() {
        return recordingoption;
    }

    /**
     * true to lock session, otherwise false (default is false)
     *
     * @return locksession
     **/
    @ApiModelProperty(example = "false", value = "true to lock session, otherwise false (default is false)")
    public boolean getLocksession() {
        return locksession;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class SessionRecordingOptions {\n");

        sb.append("    sessionname: ").append(PerfSigUIUtils.toIndentedString(sessionname)).append("\n");
        sb.append("    description: ").append(PerfSigUIUtils.toIndentedString(description)).append("\n");
        sb.append("    appendtimestamp: ").append(PerfSigUIUtils.toIndentedString(appendtimestamp)).append("\n");
        sb.append("    recordingoption: ").append(PerfSigUIUtils.toIndentedString(recordingoption)).append("\n");
        sb.append("    locksession: ").append(PerfSigUIUtils.toIndentedString(locksession)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Recording option, possible values: &#39;all&#39;, &#39;violations&#39;, &#39;timeseries&#39;
     */
    @JsonAdapter(RecordingoptionEnum.Adapter.class)
    public enum RecordingoptionEnum {
        ALL("all"),

        VIOLATIONS("violations"),

        TIMESERIES("timeseries");

        private final String value;

        RecordingoptionEnum(String value) {
            this.value = value;
        }

        public static RecordingoptionEnum fromValue(String text) {
            for (RecordingoptionEnum b : RecordingoptionEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static class Adapter extends TypeAdapter<RecordingoptionEnum> {
            @Override
            public void write(final JsonWriter jsonWriter, final RecordingoptionEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public RecordingoptionEnum read(final JsonReader jsonReader) throws IOException {
                String value = jsonReader.nextString();
                return RecordingoptionEnum.fromValue(String.valueOf(value));
            }
        }
    }
}
