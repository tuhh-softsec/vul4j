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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.IOException;
import java.util.Date;

/**
 * Deployment event record description
 */
@ApiModel(description = "Deployment event record description")

public class DeploymentEvent {
    @SerializedName("severity")
    private final SeverityEnum severity = null;
    @SerializedName("state")
    private final StateEnum state = null;
    @SerializedName("message")
    private final String message = null;
    @SerializedName("description")
    private final String description = null;
    @SerializedName("start")
    private final Date start = null;
    @SerializedName("end")
    private final Date end = null;
    @SerializedName("systemprofile")
    private final String systemprofile = null;
    @SerializedName("application")
    private final String application = null;

    /**
     * The severity of the event
     *
     * @return severity
     **/
    @ApiModelProperty(value = "The severity of the event")
    public SeverityEnum getSeverity() {
        return severity;
    }

    /**
     * The state of the event
     *
     * @return state
     **/
    @ApiModelProperty(value = "The state of the event")
    public StateEnum getState() {
        return state;
    }

    /**
     * Get message
     *
     * @return message
     **/
    @ApiModelProperty(required = true)
    public String getMessage() {
        return message;
    }

    /**
     * Get description
     *
     * @return description
     **/

    public String getDescription() {
        return description;
    }

    /**
     * Start time in ISO 8601 compatible date/time of format: yyyy-MM-dd&#39;T&#39;HH:mm:ss.SSSXXX
     *
     * @return start
     **/
    @ApiModelProperty(example = "2016-05-11T11:35:31.170+02:00", value = "Start time in ISO 8601 compatible date/time of format: yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    public Date getStart() {
        return start;
    }

    /**
     * End time in ISO 8601 compatible date/time of format: yyyy-MM-dd&#39;T&#39;HH:mm:ss.SSSXXX
     *
     * @return end
     **/
    @ApiModelProperty(example = "2016-05-11T11:35:31.170+02:00", value = "End time in ISO 8601 compatible date/time of format: yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    public Date getEnd() {
        return end;
    }

    /**
     * System Profile name
     *
     * @return systemprofile
     **/
    @ApiModelProperty(required = true, value = "System Profile name")
    public String getSystemprofile() {
        return systemprofile;
    }

    /**
     * Application name
     *
     * @return application
     **/
    @ApiModelProperty(value = "Application name")
    public String getApplication() {
        return application;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class DeploymentEvent {\n");

        sb.append("    severity: ").append(PerfSigUIUtils.toIndentedString(severity)).append("\n");
        sb.append("    state: ").append(PerfSigUIUtils.toIndentedString(state)).append("\n");
        sb.append("    message: ").append(PerfSigUIUtils.toIndentedString(message)).append("\n");
        sb.append("    description: ").append(PerfSigUIUtils.toIndentedString(description)).append("\n");
        sb.append("    start: ").append(PerfSigUIUtils.toIndentedString(start)).append("\n");
        sb.append("    end: ").append(PerfSigUIUtils.toIndentedString(end)).append("\n");
        sb.append("    systemprofile: ").append(PerfSigUIUtils.toIndentedString(systemprofile)).append("\n");
        sb.append("    application: ").append(PerfSigUIUtils.toIndentedString(application)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * The severity of the event
     */
    @JsonAdapter(SeverityEnum.Adapter.class)
    public enum SeverityEnum {
        INFORMATIONAL("informational"),

        WARNING("warning"),

        SEVERE("severe");

        private final String value;

        SeverityEnum(String value) {
            this.value = value;
        }

        public static SeverityEnum fromValue(String text) {
            for (SeverityEnum b : SeverityEnum.values()) {
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

        public static class Adapter extends TypeAdapter<SeverityEnum> {
            @Override
            public void write(final JsonWriter jsonWriter, final SeverityEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public SeverityEnum read(final JsonReader jsonReader) throws IOException {
                String value = jsonReader.nextString();
                return SeverityEnum.fromValue(String.valueOf(value));
            }
        }
    }

    /**
     * The state of the event
     */
    @JsonAdapter(StateEnum.Adapter.class)
    public enum StateEnum {
        CREATED("Created"),

        INPROGRESS("InProgress"),

        CONFIRMED("Confirmed");

        private final String value;

        StateEnum(String value) {
            this.value = value;
        }

        public static StateEnum fromValue(String text) {
            for (StateEnum b : StateEnum.values()) {
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

        public static class Adapter extends TypeAdapter<StateEnum> {
            @Override
            public void write(final JsonWriter jsonWriter, final StateEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public StateEnum read(final JsonReader jsonReader) throws IOException {
                String value = jsonReader.nextString();
                return StateEnum.fromValue(String.valueOf(value));
            }
        }
    }
}
