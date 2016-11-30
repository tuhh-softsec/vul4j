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
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.math.BigDecimal;

@ExportedBean
public abstract class MeasureBaseModel {
    private final double avg, min, max, sum;
    private final int count;

    MeasureBaseModel(final Object attr) {
        this.avg = AttributeUtils.getDoubleAttribute("avg", attr);
        this.max = AttributeUtils.getDoubleAttribute("max", attr);
        this.min = AttributeUtils.getDoubleAttribute("min", attr);
        this.count = AttributeUtils.getIntAttribute("count", attr);
        this.sum = AttributeUtils.getDoubleAttribute("sum", attr);
    }

    /**
     * Exposes this object to the remote API.
     */
    public Api getApi() {
        return new Api(this);
    }

    @Exported
    public double getSum() {
        return sum;
    }

    @Exported
    public int getCount() {
        return count;
    }

    @Exported
    public double getAvg() {
        return this.avg;
    }

    public BigDecimal getStrAvg() {
        return PerfSigUIUtils.round(this.avg, 2);
    }

    @Exported
    public double getMin() {
        return this.min;
    }

    @Exported
    public double getMax() {
        return this.max;
    }

    /**
     * used by PerfSigBuildActionResultsDisplay
     * get the avg value of a metric
     */
    public double getMetricValue(final String aggregation) {
        if (aggregation == null) {
            return this.getAvg();
        } else if (aggregation.equalsIgnoreCase("count")) {
            return this.getCount();
        } else if (aggregation.equalsIgnoreCase("average")) {
            return this.getAvg();
        } else if (aggregation.equalsIgnoreCase("sum")) {
            return this.getSum();
        } else if (aggregation.equalsIgnoreCase("maximum")) {
            return this.getMax();
        } else if (aggregation.equalsIgnoreCase("minimum")) {
            return this.getMin();
        } else {
            return this.getAvg();
        }
    }
}
