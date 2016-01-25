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
import de.tsystems.mms.apm.performancesignature.util.PerfSigUtils;
import org.apache.commons.lang.StringUtils;
import org.xml.sax.Attributes;

import java.math.BigDecimal;

public class Measurement {
    private double avg, min, max, sum;
    private long timestamp;
    private int count;

    public Measurement(final Attributes attr) {
        this.avg = AttributeUtils.getDoubleAttribute(Messages.Measurement_AttrAvg(), attr);
        this.max = AttributeUtils.getDoubleAttribute(Messages.Measurement_AttrMax(), attr);
        this.min = AttributeUtils.getDoubleAttribute(Messages.Measurement_AttrMin(), attr);
        this.count = AttributeUtils.getIntAttribute(Messages.Measurement_AttrCount(), attr);
        this.sum = AttributeUtils.getDoubleAttribute(Messages.Measurement_AttrSum(), attr);
        this.timestamp = AttributeUtils.getLongAttribute(Messages.Measurement_AttrTimestamp(), attr);
    }

    public double getSum() {
        return sum;
    }

    public int getCount() {
        return count;
    }

    public double getAvg() {
        return this.avg;
    }


    public BigDecimal getStrAvg() {
        return PerfSigUtils.round(this.avg, 2);
    }

    public double getMin() {
        return this.min;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public double getMax() {
        return this.max;
    }

    /**
     * used by PerfSigBuildActionResultsDisplay
     * get the avg value of a metric
     */
    public double getMetricValue(final String aggregation) {
        if (aggregation.equalsIgnoreCase("Count"))
            return this.getCount();
        else if (aggregation.equalsIgnoreCase("Average") || StringUtils.isBlank(aggregation))
            return this.getAvg();
        else if (aggregation.equalsIgnoreCase("Sum"))
            return this.getSum();
        else if (aggregation.equalsIgnoreCase("Max"))
            return this.getMax();
        else if (aggregation.equalsIgnoreCase("Min"))
            return this.getMin();
        else
            return 0;
    }
}
