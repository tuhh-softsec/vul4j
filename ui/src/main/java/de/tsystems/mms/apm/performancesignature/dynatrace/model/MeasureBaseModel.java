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

import hudson.model.Api;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

@XmlTransient
@ExportedBean
public abstract class MeasureBaseModel {
    @XmlAttribute
    private double avg;
    @XmlAttribute
    private double min;
    @XmlAttribute
    private double max;
    @XmlAttribute
    private double sum;
    @XmlAttribute
    private long count;

    /**
     * Exposes this object to the remote API.
     */
    public Api getApi() {
        return new Api(this);
    }

    /**
     * Ruft den Wert der avg-Eigenschaft ab.
     */
    @Exported
    public double getAvg() {
        return avg;
    }

    /**
     * Legt den Wert der avg-Eigenschaft fest.
     */
    public void setAvg(double value) {
        this.avg = value;
    }

    /**
     * Ruft den Wert der min-Eigenschaft ab.
     */
    @Exported
    public double getMin() {
        return min;
    }

    /**
     * Legt den Wert der min-Eigenschaft fest.
     */
    public void setMin(double value) {
        this.min = value;
    }

    /**
     * Ruft den Wert der max-Eigenschaft ab.
     */
    @Exported
    public double getMax() {
        return max;
    }

    /**
     * Legt den Wert der max-Eigenschaft fest.
     */
    public void setMax(double value) {
        this.max = value;
    }

    /**
     * Ruft den Wert der sum-Eigenschaft ab.
     */
    @Exported
    public double getSum() {
        return sum;
    }

    /**
     * Legt den Wert der sum-Eigenschaft fest.
     */
    public void setSum(double value) {
        this.sum = value;
    }

    /**
     * Ruft den Wert der count-Eigenschaft ab.
     *
     * @return possible object is
     * {@link long }
     */
    @Exported
    public long getCount() {
        return count;
    }

    /**
     * Legt den Wert der count-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link long }
     */
    @Exported
    public void setCount(long value) {
        this.count = value;
    }

    /**
     * used by PerfSigBuildActionResultsDisplay
     * get the avg value of a metric
     */
    public double getMetricValue(final String aggregation) {
        if (aggregation == null || aggregation.equalsIgnoreCase("average")) {
            return this.getAvg();
        } else if (aggregation.equalsIgnoreCase("count")) {
            return this.getCount();
        } else if (aggregation.equalsIgnoreCase("sum")) {
            return this.getSum();
        } else if (aggregation.equalsIgnoreCase("maximum")) {
            return this.getMax();
        } else if (aggregation.equalsIgnoreCase("minimum")) {
            return this.getMin();
        }
        return 0;
    }
}
