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
package org.apache.commons.digester3;

import java.beans.PropertyDescriptor;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.PropertyUtils;
import org.xml.sax.Attributes;

/**
 * Rule implementation that sets an individual property on the object at the
 * top of the stack, based on attributes with specified names.
 */
public class SetPropertyRule extends Rule {

    /**
     * The attribute that will contain the property name.
     */
    private final String name;

    /**
     * The attribute that will contain the property value.
     */
    private final String value;

    /**
     * Construct a "set property" rule with the specified name and value
     * attributes.
     *
     * @param name Name of the attribute that will contain the name of the
     *  property to be set
     * @param value Name of the attribute that will contain the value to which
     *  the property should be set
     */
    public SetPropertyRule(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        if (attributes.getLength() == 0) {
            return;
        }

        // Identify the actual property name and value to be used
        String actualName = null;
        String actualValue = null;
        for (int i = 0; i < attributes.getLength(); i++) {
            String localName = attributes.getLocalName(i);
            if ("".equals(localName)) {
                localName = attributes.getQName(i);
            }
            String value = attributes.getValue(i);
            if (localName.equals(this.name)) {
                actualName = value;
            } else if (localName.equals(this.value)) {
                actualValue = value;
            }
        }

        // Get a reference to the top object
        Object top = this.getDigester().peek();

        // Log some debugging information
        if (this.getDigester().getLog().isDebugEnabled()) {
            this.getDigester().getLog().debug(String.format("[SetPropertyRule]{%s} Set %s property %s to %s",
                    this.getDigester().getMatch(),
                    top.getClass().getName(),
                    actualName,
                    actualValue));
        }

        // Force an exception if the property does not exist
        // (BeanUtils.setProperty() silently returns in this case)
        //
        // This code should probably use PropertyUtils.isWriteable(), 
        // like SetPropertiesRule does.
        if (top instanceof DynaBean) {
            DynaProperty desc =
                ((DynaBean) top).getDynaClass().getDynaProperty(actualName);
            if (desc == null) {
                throw new NoSuchMethodException("Bean has no property named "
                        + actualName);
            }
        } else /* this is a standard JavaBean */ {
            PropertyDescriptor desc = PropertyUtils.getPropertyDescriptor(top, actualName);
            if (desc == null) {
                throw new NoSuchMethodException("Bean has no property named "
                        + actualName);
            }
        }

        // Set the property (with conversion as necessary)
        BeanUtils.setProperty(top, actualName, actualValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return String.format("SetPropertyRule[name=%s, value=%s]",
                        this.name,
                        this.value);
    }

}
