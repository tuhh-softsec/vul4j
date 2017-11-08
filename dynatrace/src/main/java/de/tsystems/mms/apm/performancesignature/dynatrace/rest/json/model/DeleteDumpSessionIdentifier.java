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
 * DeleteDumpSessionIdentifier
 */

public class DeleteDumpSessionIdentifier {
    @SerializedName("type")
    private final TypeEnum type = null;
    @SerializedName("id")
    private final String id = null;
    @SerializedName("systemprofile")
    private final String systemprofile = null;

    /**
     * Stored session type
     *
     * @return type
     **/
    @ApiModelProperty(value = "Stored session type")
    public TypeEnum getType() {
        return type;
    }

    /**
     * Unique id of stored session
     *
     * @return id
     **/
    @ApiModelProperty(value = "Unique id of stored session")
    public String getId() {
        return id;
    }

    /**
     * Name of the system profile the stored session belonged to
     *
     * @return systemprofile
     **/
    @ApiModelProperty(value = "Name of the system profile the stored session belonged to")
    public String getSystemprofile() {
        return systemprofile;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class DeleteDumpSessionIdentifier {\n");

        sb.append("    type: ").append(PerfSigUIUtils.toIndentedString(type)).append("\n");
        sb.append("    id: ").append(PerfSigUIUtils.toIndentedString(id)).append("\n");
        sb.append("    systemprofile: ").append(PerfSigUIUtils.toIndentedString(systemprofile)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Stored session type
     */
    @JsonAdapter(TypeEnum.Adapter.class)
    public enum TypeEnum {
        PUREPATH("purepath"),

        MEMDUMP_SIMPLE("memdump_simple"),

        MEMDUMP_EXTENDED("memdump_extended"),

        MEMDUMP_SELECTIVE("memdump_selective"),

        THREADDUMP("threaddump"),

        SAMPLING("sampling");

        private final String value;

        TypeEnum(String value) {
            this.value = value;
        }

        public static TypeEnum fromValue(String text) {
            for (TypeEnum b : TypeEnum.values()) {
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

        public static class Adapter extends TypeAdapter<TypeEnum> {
            @Override
            public void write(final JsonWriter jsonWriter, final TypeEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public TypeEnum read(final JsonReader jsonReader) throws IOException {
                String value = jsonReader.nextString();
                return TypeEnum.fromValue(String.valueOf(value));
            }
        }
    }
}
