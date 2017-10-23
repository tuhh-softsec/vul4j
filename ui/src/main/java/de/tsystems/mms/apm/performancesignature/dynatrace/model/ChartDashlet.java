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

import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "chartdashlet")
@ExportedBean
public class ChartDashlet implements Comparable<ChartDashlet> {

    @XmlAttribute
    private String name;
    @XmlAttribute
    private String description;

    @XmlElementWrapper(name = "measures")
    @XmlElement(name = "measure")
    private List<Measure> measures;

    /**
     * Gets the value of the measure property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the measure property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMeasure().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Measure }
     */
    @Exported
    public List<Measure> getMeasures() {
        if (measures == null) {
            measures = new ArrayList<>();
        }
        return this.measures;
    }

    public void addMeasure(final Measure tm) {
        this.measures.add(tm);
    }

    /**
     * Ruft den Wert der name-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    @Exported
    public String getName() {
        return name;
    }

    /**
     * Legt den Wert der name-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Ruft den Wert der description-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    @Exported
    public String getDescription() {
        return description;
    }

    /**
     * Legt den Wert der description-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setDescription(String value) {
        this.description = value;
    }

    public int compareTo(@Nonnull final ChartDashlet that) {
        if (this == that) {
            return 0;
        }
        int r = this.getName().compareToIgnoreCase(that.getName());
        if (r != 0) {
            return r;
        }
        // Only equals is exact reference
        return System.identityHashCode(this) >= System.identityHashCode(that) ? 1 : -1;
    }

    // Method overridden to provide explicit declaration of the equivalence relation used
    // as Comparable is also implemented
    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    // Method overridden to provide explicit declaration of the equivalence relation used
    // as Comparable is also implemented
    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }
}
