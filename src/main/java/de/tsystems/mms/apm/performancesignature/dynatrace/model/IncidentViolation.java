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

package de.tsystems.mms.apm.performancesignature.dynatrace.model;

import de.tsystems.mms.apm.performancesignature.dynatrace.model.IncidentChart.Severity;
import de.tsystems.mms.apm.performancesignature.dynatrace.util.AttributeUtils;
import org.apache.commons.lang.StringUtils;
import org.xml.sax.Attributes;

import javax.xml.bind.DatatypeConverter;
import java.util.Date;

/**
 * Created by rapi on 02.11.2015.
 */
public class IncidentViolation {
    private final String rule, description;
    private final Severity severity;
    private final long duration;
    private Date start, end;

    public IncidentViolation(final Attributes attr) {
        this.rule = AttributeUtils.getStringAttribute("rule", attr);
        this.severity = Severity.fromString(AttributeUtils.getStringAttribute("severity", attr));
        if (StringUtils.isNotBlank(attr.getValue("start")))
            this.start = DatatypeConverter.parseDateTime(attr.getValue("start")).getTime();
        if (StringUtils.isNotBlank(attr.getValue("end")))
            this.end = DatatypeConverter.parseDateTime(attr.getValue("end")).getTime();
        this.duration = AttributeUtils.getLongAttribute("duration", attr);
        this.description = AttributeUtils.getStringAttribute("description", attr);
    }

    public String getRule() {
        return rule;
    }

    public Severity getSeverity() {
        return severity;
    }

    public Date getStart() {
        return new Date(start.getTime());
    }

    public Date getEnd() {
        return new Date(end.getTime());
    }

    public long getDuration() {
        return duration;
    }

    public String getDescription() {
        return description;
    }
}
