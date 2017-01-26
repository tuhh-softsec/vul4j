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
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.util.Date;

@ExportedBean
public class IncidentViolation {
    private final String rule, description;
    private final Severity severity;
    private final long duration;
    private final Date start, end;

    public IncidentViolation(final Object attr) {
        this.rule = AttributeUtils.getStringAttribute("rule", attr);
        this.severity = Severity.fromString(AttributeUtils.getStringAttribute("severity", attr));

        if (StringUtils.isEmpty(AttributeUtils.getStringAttribute("start", attr))) {
            this.start = AttributeUtils.getDateAttribute("start", attr);
        } else {
            this.start = new Date();
        }
        if (StringUtils.isEmpty(AttributeUtils.getStringAttribute("end", attr))) {
            this.end = AttributeUtils.getDateAttribute("end", attr);
        } else {
            this.end = new Date();
        }

        this.duration = AttributeUtils.getLongAttribute("duration", attr);
        this.description = AttributeUtils.getStringAttribute("description", attr);
    }

    @Exported
    public String getRule() {
        return rule;
    }

    @Exported
    public Severity getSeverity() {
        return severity;
    }

    @Exported
    public Date getStart() {
        return (Date) start.clone();
    }

    @Exported
    public Date getEnd() {
        return (Date) end.clone();
    }

    @Exported
    public long getDuration() {
        return duration;
    }

    @Exported
    public String getDescription() {
        return description;
    }
}
