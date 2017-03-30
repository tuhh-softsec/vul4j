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

import de.tsystems.mms.apm.performancesignature.dynatrace.util.AttributeUtils;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.util.ArrayList;
import java.util.List;

@ExportedBean
public class ChartDashlet {
    private final String name;
    private final List<Measure> measures;
    private String description;

    public ChartDashlet(final String name) {
        this.name = name;
        this.measures = new ArrayList<>();
    }

    public ChartDashlet(final Object attr) {
        this(AttributeUtils.getStringAttribute("name", attr));
        this.description = AttributeUtils.getStringAttribute("description", attr);
    }

    @Exported
    public String getName() {
        return name;
    }

    @Exported
    public String getDescription() {
        return description;
    }

    @Exported
    public List<Measure> getMeasures() {
        return measures;
    }

    public void addMeasure(final Measure tm) {
        this.measures.add(tm);
    }
}
