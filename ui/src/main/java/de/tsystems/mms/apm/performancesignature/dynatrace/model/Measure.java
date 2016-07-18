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
import hudson.model.Api;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ExportedBean
public class Measure {
    private final String name;
    private String color, unit, aggregation;
    private double max, avg, min, sum;
    private List<Measurement> measurements;
    private int count;

    public Measure(final String name) {
        this.name = name;
    }

    public Measure(final Object attr) {
        this.name = AttributeUtils.getStringAttribute(Messages.Measure_AttrMeasure(), attr);
        this.avg = AttributeUtils.getDoubleAttribute(Messages.Measure_AttrAvg(), attr);
        this.max = AttributeUtils.getDoubleAttribute(Messages.Measure_AttrMax(), attr);
        this.min = AttributeUtils.getDoubleAttribute(Messages.Measure_AttrMin(), attr);
        this.color = AttributeUtils.getStringAttribute(Messages.Measure_AttrColor(), attr);
        this.count = AttributeUtils.getIntAttribute(Messages.Measure_AttrCount(), attr);
        this.unit = AttributeUtils.getStringAttribute(Messages.Measure_AttrUnit(), attr);
        this.sum = AttributeUtils.getDoubleAttribute(Messages.Measure_AttrSum(), attr);
        this.aggregation = AttributeUtils.getStringAttribute(Messages.Measure_AttrAggregation(), attr);
    }

    /**
     * Exposes this object to the remote API.
     */
    public Api getApi() {
        return new Api(this);
    }

    @Exported(visibility = 999)
    public List<Measurement> getMeasurements() {
        return measurements;
    }

    public void addMeasurement(final Measurement tm) {
        if (this.measurements == null)
            this.measurements = new ArrayList<Measurement>();
        this.measurements.add(tm);
    }

    @Exported(visibility = 999)
    public double getSum() {
        return this.sum;
    }

    @Exported(visibility = 999)
    public int getCount() {
        return count;
    }

    public String getName() {
        return this.name;
    }

    @Exported(visibility = 999)
    public String getMeasure() {
        return getName();
    }

    @Exported(visibility = 999)
    public String getColor() {
        return PerfSigUIUtils.encodeString(this.color);
    }

    @Exported(visibility = 999)
    public double getAvg() {
        return this.avg;
    }

    @Exported(visibility = 999)
    public double getMin() {
        return this.min;
    }

    @Exported(visibility = 999)
    public double getMax() {
        return this.max;
    }

    @Exported(visibility = 999)
    public String getUnit() {
        if (this.aggregation != null && this.aggregation.equalsIgnoreCase("count")) return "num";
        return PerfSigUIUtils.encodeString(this.unit);
    }

    @Exported(visibility = 999)
    public String getAggregation() {
        return aggregation;
    }

    public boolean isPercentile() {
        return StringUtils.isNotBlank(this.getAggregation()) && this.getAggregation().equalsIgnoreCase("percentiles");
    }

    public double getMetricValue() {
        return getMetricValue(this.getAggregation());
    }

    /**
     * get the avg value of a metric
     * in case of a percentile measure return the 95th percentile value
     *
     * @return metric Value
     */
    public double getMetricValue(final String aggregation) {
        if (this.isPercentile()) {
            List<Measurement> measurements = this.getMeasurements();
            if (measurements == null) return 0;
            Measurement measurement95th = measurements.get(95);
            if (measurement95th == null) return 0;
            return measurement95th.getAvg();
        } else if (aggregation.equalsIgnoreCase("count"))
            return this.getCount();
        else if (aggregation.equalsIgnoreCase("average") || aggregation.equalsIgnoreCase("last"))
            return this.getAvg();
        else if (aggregation.equalsIgnoreCase("sum"))
            return this.getSum();
        else if (aggregation.equalsIgnoreCase("maximum"))
            return this.getMax();
        else if (aggregation.equalsIgnoreCase("minimum"))
            return this.getMin();
        else
            return this.getAvg();
    }

    public BigDecimal getStrMetricValue() {
        return PerfSigUIUtils.round(getMetricValue(), 2);
    }
}
