/* $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.digester3.plugins;

import static org.apache.commons.digester3.DigesterLoader.newLoader;

import org.apache.commons.digester3.AbstractRulesModule;
import org.apache.commons.digester3.Digester;

public class Slider implements Widget {

    private String label = "nolabel";

    private int min = 0;

    private int max = 0;

    // define rules on this class
    public static void addRules(final Digester digester, final String pattern) {
        newLoader(new AbstractRulesModule() {

            @Override
            protected void configure() {
                forPattern(pattern).setProperties();
                forPattern(pattern + "/min")
                    .callMethod("setMin").withParamTypes(Integer.class).usingElementBodyAsArgument();
                forPattern(pattern + "/max")
                    .callMethod("setMax").withParamTypes(Integer.class).usingElementBodyAsArgument();
            }

        }).decorate(digester.getRules());
    }

    // define different rules on this class
    public static void addRangeRules(final Digester digester, final String pattern) {
        newLoader(new AbstractRulesModule() {

            @Override
            protected void configure() {
                // note: deliberately no addSetProperties rule
                forPattern(pattern + "/range")
                    .callMethod("setRange").withParamTypes(Integer.class, Integer.class)
                    .then()
                    .callParam().ofIndex(0).fromAttribute("min")
                    .then()
                    .callParam().ofIndex(1).fromAttribute("max");
            }

        }).decorate(digester.getRules());
    }

    public Slider() {}

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMin() {
        return min;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMax() {
        return max;
    }

    public void setRange(int min, int max) {
        this.min = min;
        this.max = max;
    }

}
