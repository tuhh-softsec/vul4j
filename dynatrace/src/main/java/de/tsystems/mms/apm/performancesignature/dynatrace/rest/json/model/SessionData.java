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
 * SessionData
 */

public class SessionData {
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class SessionData {\n");

        sb.append("    id: ").append(PerfSigUIUtils.toIndentedString(id)).append("\n");
        sb.append("    storedsessiontype: ").append(PerfSigUIUtils.toIndentedString(storedsessiontype)).append("\n");
        sb.append("    sessiontype: ").append(PerfSigUIUtils.toIndentedString(sessiontype)).append("\n");
        sb.append("    systemprofile: ").append(PerfSigUIUtils.toIndentedString(systemprofile)).append("\n");
        sb.append("    href: ").append(PerfSigUIUtils.toIndentedString(href)).append("\n");
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

}

