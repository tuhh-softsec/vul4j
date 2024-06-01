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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Comprehensive metadata of a session
 */
@ApiModel(description = "Comprehensive metadata of a session")

public class SessionMetadata {
    @SerializedName("id")
    private final String id = null;
    @SerializedName("storedsessiontype")
    private final StoredsessiontypeEnum storedsessiontype = null;
    @SerializedName("sessiontype")
    private final SessiontypeEnum sessiontype = null;
    @SerializedName("systemprofile")
    private final String systemprofile = null;
    @SerializedName("href")
    private final String href = null;
    @SerializedName("name")
    private final String name = null;
    @SerializedName("description")
    private final String description = null;
    @SerializedName("size")
    private final Long size = null;
    @SerializedName("deletionlocked")
    private final Boolean deletionlocked = false;
    @SerializedName("directorypath")
    private final String directorypath = null;
    @SerializedName("state")
    private final StateEnum state = null;
    @SerializedName("capturingstart")
    private final Date capturingstart = null;
    @SerializedName("capturingduration")
    private final Long capturingduration = null;
    @SerializedName("version")
    private final String version = null;
    @SerializedName("recordingtype")
    private final String recordingtype = null;
    @SerializedName("agent")
    private final String agent = null;
    @SerializedName("numberofpurepaths")
    private final Integer numberofpurepaths = null;
    @SerializedName("continuoussession")
    private final Boolean continuoussession = false;
    @SerializedName("labels")
    private List<String> labels = null;

    /**
     * Unique id of the session
     *
     * @return id
     **/
    @ApiModelProperty(value = "Unique id of the session")
    public String getId() {
        return id;
    }

    /**
     * Stored session type
     *
     * @return storedsessiontype
     **/
    @ApiModelProperty(value = "Stored session type")
    public StoredsessiontypeEnum getStoredsessiontype() {
        return storedsessiontype;
    }

    /**
     * Session type
     *
     * @return sessiontype
     **/
    @ApiModelProperty(value = "Session type")
    public SessiontypeEnum getSessiontype() {
        return sessiontype;
    }

    /**
     * Name of the system profile the session belongs to
     *
     * @return systemprofile
     **/
    @ApiModelProperty(value = "Name of the system profile the session belongs to")
    public String getSystemprofile() {
        return systemprofile;
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
     * Session name
     *
     * @return name
     **/
    @ApiModelProperty(value = "Session name")
    public String getName() {
        return name;
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
     * Size in bytes
     *
     * @return size
     **/
    @ApiModelProperty(value = "Size in bytes")
    public Long getSize() {
        return size;
    }

    /**
     * Get deletionlocked
     *
     * @return deletionlocked
     **/

    public Boolean getDeletionlocked() {
        return deletionlocked;
    }

    /**
     * Get directorypath
     *
     * @return directorypath
     **/

    public String getDirectorypath() {
        return directorypath;
    }

    /**
     * Get state
     *
     * @return state
     **/

    public StateEnum getState() {
        return state;
    }

    /**
     * The start time of the session capturing in ISO8601 format
     *
     * @return capturingstart
     **/
    @ApiModelProperty(example = "2016-05-11T11:35:31.170+02:00", value = "The start time of the session capturing in ISO8601 format")
    public Date getCapturingstart() {
        return capturingstart;
    }

    /**
     * Capturing duration in milliseconds
     *
     * @return capturingduration
     **/
    @ApiModelProperty(value = "Capturing duration in milliseconds")
    public Long getCapturingduration() {
        return capturingduration;
    }

    /**
     * Get version
     *
     * @return version
     **/

    public String getVersion() {
        return version;
    }

    public SessionMetadata labels(List<String> labels) {
        this.labels = labels;
        return this;
    }

    public SessionMetadata addLabelsItem(String labelsItem) {
        if (this.labels == null) {
            this.labels = new ArrayList<>();
        }
        this.labels.add(labelsItem);
        return this;
    }

    /**
     * Get labels
     *
     * @return labels
     **/

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    /**
     * Get recordingtype
     *
     * @return recordingtype
     **/

    public String getRecordingtype() {
        return recordingtype;
    }

    /**
     * Get agent
     *
     * @return agent
     **/

    public String getAgent() {
        return agent;
    }

    /**
     * Get numberofpurepaths
     *
     * @return numberofpurepaths
     **/

    public Integer getNumberofpurepaths() {
        return numberofpurepaths;
    }

    /**
     * Get continuoussession
     *
     * @return continuoussession
     **/

    public Boolean getContinuoussession() {
        return continuoussession;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class SessionMetadata {\n");

        sb.append("    id: ").append(PerfSigUIUtils.toIndentedString(id)).append("\n");
        sb.append("    storedsessiontype: ").append(PerfSigUIUtils.toIndentedString(storedsessiontype)).append("\n");
        sb.append("    sessiontype: ").append(PerfSigUIUtils.toIndentedString(sessiontype)).append("\n");
        sb.append("    systemprofile: ").append(PerfSigUIUtils.toIndentedString(systemprofile)).append("\n");
        sb.append("    href: ").append(PerfSigUIUtils.toIndentedString(href)).append("\n");
        sb.append("    name: ").append(PerfSigUIUtils.toIndentedString(name)).append("\n");
        sb.append("    description: ").append(PerfSigUIUtils.toIndentedString(description)).append("\n");
        sb.append("    size: ").append(PerfSigUIUtils.toIndentedString(size)).append("\n");
        sb.append("    deletionlocked: ").append(PerfSigUIUtils.toIndentedString(deletionlocked)).append("\n");
        sb.append("    directorypath: ").append(PerfSigUIUtils.toIndentedString(directorypath)).append("\n");
        sb.append("    state: ").append(PerfSigUIUtils.toIndentedString(state)).append("\n");
        sb.append("    capturingstart: ").append(PerfSigUIUtils.toIndentedString(capturingstart)).append("\n");
        sb.append("    capturingduration: ").append(PerfSigUIUtils.toIndentedString(capturingduration)).append("\n");
        sb.append("    version: ").append(PerfSigUIUtils.toIndentedString(version)).append("\n");
        sb.append("    labels: ").append(PerfSigUIUtils.toIndentedString(labels)).append("\n");
        sb.append("    recordingtype: ").append(PerfSigUIUtils.toIndentedString(recordingtype)).append("\n");
        sb.append("    agent: ").append(PerfSigUIUtils.toIndentedString(agent)).append("\n");
        sb.append("    numberofpurepaths: ").append(PerfSigUIUtils.toIndentedString(numberofpurepaths)).append("\n");
        sb.append("    continuoussession: ").append(PerfSigUIUtils.toIndentedString(continuoussession)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Stored session type
     */
    @JsonAdapter(StoredsessiontypeEnum.Adapter.class)
    public enum StoredsessiontypeEnum {
        PUREPATH("purepath"),

        MEMDUMP_SIMPLE("memdump_simple"),

        MEMDUMP_EXTENDED("memdump_extended"),

        MEMDUMP_SELECTIVE("memdump_selective"),

        THREADDUMP("threaddump"),

        SAMPLING("sampling");

        private final String value;

        StoredsessiontypeEnum(String value) {
            this.value = value;
        }

        public static StoredsessiontypeEnum fromValue(String text) {
            for (StoredsessiontypeEnum b : StoredsessiontypeEnum.values()) {
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

        public static class Adapter extends TypeAdapter<StoredsessiontypeEnum> {
            @Override
            public void write(final JsonWriter jsonWriter, final StoredsessiontypeEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public StoredsessiontypeEnum read(final JsonReader jsonReader) throws IOException {
                String value = jsonReader.nextString();
                return StoredsessiontypeEnum.fromValue(String.valueOf(value));
            }
        }
    }

    /**
     * Session type
     */
    @JsonAdapter(SessiontypeEnum.Adapter.class)
    public enum SessiontypeEnum {
        LIVE("live"),

        SERVER("server"),

        STORED("stored"),

        UNTYPED("untyped");

        private final String value;

        SessiontypeEnum(String value) {
            this.value = value;
        }

        public static SessiontypeEnum fromValue(String text) {
            for (SessiontypeEnum b : SessiontypeEnum.values()) {
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

        public static class Adapter extends TypeAdapter<SessiontypeEnum> {
            @Override
            public void write(final JsonWriter jsonWriter, final SessiontypeEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public SessiontypeEnum read(final JsonReader jsonReader) throws IOException {
                String value = jsonReader.nextString();
                return SessiontypeEnum.fromValue(String.valueOf(value));
            }
        }
    }

    /**
     * Gets or Sets state
     */
    @JsonAdapter(StateEnum.Adapter.class)
    public enum StateEnum {
        INPROGRESS("inprogress"),

        FINISHED("finished"),

        CORRUPT("corrupt"),

        INCOMPLETE("incomplete");

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
