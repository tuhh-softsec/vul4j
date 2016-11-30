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
import de.tsystems.mms.apm.performancesignature.util.PerfSigUIUtils;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@ExportedBean
public class Measure extends MeasureBaseModel {
    private static final Logger LOGGER = Logger.getLogger(Measure.class.getName());
    private final String name;
    private final List<Measurement> measurements;
    private String color, unit, aggregation;

    public Measure(final Object attr) {
        super(attr);
        this.measurements = new ArrayList<Measurement>();
        this.name = AttributeUtils.getStringAttribute("measure", attr);
        this.color = AttributeUtils.getStringAttribute("color", attr);
        this.unit = AttributeUtils.getStringAttribute("unit", attr);
        this.aggregation = AttributeUtils.getStringAttribute("aggregation", attr);

        if (this.isPercentile()) {
            LOGGER.warning(Messages.Measure_PercentileNotSupported(this.name));
        }
    }

    @Exported
    public List<Measurement> getMeasurements() {
        return measurements;
    }

    public void addMeasurement(final Measurement tm) {
        this.measurements.add(tm);
    }

    @Exported(name = "measure")
    public String getName() {
        return this.name;
    }

    @Exported
    public String getColor() {
        return this.color;
    }

    @Exported
    public String getUnit() {
        return getUnit(aggregation);
    }

    public String getUnit(final String aggregation) {
        if (StringUtils.isNotBlank(aggregation) && aggregation.equalsIgnoreCase("count")) return "num";
        return this.unit;
    }

    @Exported
    public String getAggregation() {
        return aggregation;
    }

    public void setAggregation(final String aggregation) {
        this.aggregation = aggregation;
    }

    private boolean isPercentile() {
        return StringUtils.isNotBlank(this.aggregation) && this.aggregation.equalsIgnoreCase("percentiles");
    }

    public double getMetricValue() {
        return getMetricValue(this.getAggregation());
    }

    public BigDecimal getStrMetricValue() {
        return PerfSigUIUtils.round(getMetricValue(), 2);
    }
}
