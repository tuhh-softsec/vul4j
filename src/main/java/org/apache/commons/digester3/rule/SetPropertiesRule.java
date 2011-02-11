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
package org.apache.commons.digester3.rule;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.digester3.Rule;
import org.xml.sax.Attributes;

/**
 * <p>Rule implementation that sets properties on the object at the top of the
 * stack, based on attributes with corresponding names.</p>
 *
 * <p>This rule supports custom mapping of attribute names to property names.
 * The default mapping for particular attributes can be overridden by using 
 * {@link #SetPropertiesRule(String[] attributeNames, String[] propertyNames)}.
 * This allows attributes to be mapped to properties with different names.
 * Certain attributes can also be marked to be ignored.</p>
 */
public class SetPropertiesRule extends Rule {

    private final Map<String, String> aliases;

    /**
     * Used to determine whether the parsing should fail if an property specified
     * in the XML is missing from the bean. Default is true for backward compatibility.
     */
    private boolean ignoreMissingProperty;

    /**
     * 
     *
     * @param aliases
     * @param ignoreMissingProperty
     */
    public SetPropertiesRule(Map<String, String> aliases, boolean ignoreMissingProperty) {
        this.aliases = aliases;
        this.ignoreMissingProperty = ignoreMissingProperty;
    }

    /**
     * {@inheritDoc}
     */
    public void begin(String namespace, String elementName, Attributes attributes) throws Exception {
        // Build a set of attribute names and corresponding values
        Map<String, String> values = new HashMap<String, String>();

        for (int i = 0; i < attributes.getLength(); i++) {
            String name = attributes.getLocalName(i);
            if ("".equals(name)) {
                name = attributes.getQName(i);
            }
            String value = attributes.getValue(i);

            if (this.aliases.containsKey(name)) {
                name = this.aliases.get(name);
            }

            if (this.getDigester().getLog().isDebugEnabled()) {
                this.getDigester().getLog().debug(String.format("[SetPropertiesRule]{%s} Setting property '%s' to '%s'",
                        this.getDigester().getMatch(),
                        name,
                        value));
            }

            if ((!ignoreMissingProperty) && (name != null)) {
                // The BeanUtils.populate method silently ignores items in
                // the map (ie xml entities) which have no corresponding
                // setter method, so here we check whether each xml attribute
                // does have a corresponding property before calling the
                // BeanUtils.populate method.
                //
                // Yes having the test and set as separate steps is ugly and 
                // inefficient. But BeanUtils.populate doesn't provide the 
                // functionality we need here, and changing the algorithm which 
                // determines the appropriate setter method to invoke is 
                // considered too risky.
                //
                // Using two different classes (PropertyUtils vs BeanUtils) to
                // do the test and the set is also ugly; the codepaths
                // are different which could potentially lead to trouble.
                // However the BeanUtils/ProperyUtils code has been carefully 
                // compared and the PropertyUtils functionality does appear 
                // compatible so we'll accept the risk here.

                Object top = this.getDigester().peek();
                boolean test =  PropertyUtils.isWriteable(top, name);
                if (!test) {
                    throw new NoSuchMethodException("Property " + name + " can't be set");
                }
            }

            if (name != null) {
                values.put(name, value);
            } 
        }

        // Populate the corresponding properties of the top object
        Object top = this.getDigester().peek();
        if (this.getDigester().getLog().isDebugEnabled()) {
            this.getDigester().getLog().debug(String.format("[SetPropertiesRule]{%s} Set %s properties",
                        this.getDigester().getMatch(),
                        (top != null ? top.getClass().getName() : "NULL")));
        }
        BeanUtils.populate(top, values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("SetPropertiesRule[aliases=%s, ignoreMissingProperty=%s]",
                this.aliases,
                this.ignoreMissingProperty);
    }

}
