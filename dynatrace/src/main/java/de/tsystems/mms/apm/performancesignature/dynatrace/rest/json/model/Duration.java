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
 * Duration
 */

public class Duration {
    @SerializedName("unit")
    private final UnitEnum unit = null;
    @SerializedName("value")
    private final Long value = null;

    /**
     * Timeunit of duration
     *
     * @return unit
     **/
    @ApiModelProperty(value = "Timeunit of duration")
    public UnitEnum getUnit() {
        return unit;
    }

    /**
     * Duration value
     *
     * @return value
     **/
    @ApiModelProperty(value = "Duration value")
    public Long getValue() {
        return value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Duration {\n");

        sb.append("    unit: ").append(PerfSigUIUtils.toIndentedString(unit)).append("\n");
        sb.append("    value: ").append(PerfSigUIUtils.toIndentedString(value)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Timeunit of duration
     */
    @JsonAdapter(UnitEnum.Adapter.class)
    public enum UnitEnum {
        MILLISECONDS("MILLISECONDS"),

        SECONDS("SECONDS"),

        MINUTES("MINUTES"),

        HOURS("HOURS"),

        DAYS("DAYS"),

        WEEKS("WEEKS"),

        MONTHS("MONTHS"),

        YEARS("YEARS");

        private final String value;

        UnitEnum(String value) {
            this.value = value;
        }

        public static UnitEnum fromValue(String text) {
            for (UnitEnum b : UnitEnum.values()) {
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

        public static class Adapter extends TypeAdapter<UnitEnum> {
            @Override
            public void write(final JsonWriter jsonWriter, final UnitEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public UnitEnum read(final JsonReader jsonReader) throws IOException {
                String value = jsonReader.nextString();
                return UnitEnum.fromValue(String.valueOf(value));
            }
        }
    }
}
