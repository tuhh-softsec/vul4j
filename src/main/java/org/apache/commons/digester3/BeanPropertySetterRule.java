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
import org.apache.commons.digester3.Rule;

/**
 * <p> Rule implements sets a bean property on the top object
 * to the body text.</p>
 *
 * <p> The property set:</p>
 * <ul><li>can be specified when the rule is created</li>
 * <li>or can match the current element when the rule is called.</li></ul>
 *
 * <p> Using the second method and the {@link ExtendedBaseRules} child match
 * pattern, all the child elements can be automatically mapped to properties
 * on the parent object.</p>
 */
public class BeanPropertySetterRule extends Rule {

    /**
     * Set this property on the top object.
     */
    private final String propertyName;

    /**
     * The body text used to set the property.
     */
    private String bodyText = null;

    /**
     * <p>Construct rule that automatically sets a property from the body text.
     *
     * <p> This construct creates a rule that sets the property
     * on the top object named the same as the current element.
     */
    public BeanPropertySetterRule() {
        this(null);
    }

    /**
     * <p>Construct rule that sets the given property from the body text.</p>
     *
     * @param propertyName name of property to set
     */
    public BeanPropertySetterRule(String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * Process the body text of this element.
     *
     * @param namespace the namespace URI of the matching element, or an 
     *   empty string if the parser is not namespace aware or the element has
     *   no namespace
     * @param name the local name if the parser is namespace aware, or just 
     *   the element name otherwise
     * @param text The text of the body of this element
     */
    @Override
    public void body(String namespace, String name, String text) throws Exception {
        // log some debugging information
        if (this.getDigester().getLog().isDebugEnabled()) {
            this.getDigester().getLog().debug(String.format("[BeanPropertySetterRule]{%s} Called with text '%s'",
                    this.getDigester().getMatch(),
                    text));
        }

        this.bodyText = text.trim();
    }

    /**
     * Process the end of this element.
     *
     * @param namespace the namespace URI of the matching element, or an 
     *   empty string if the parser is not namespace aware or the element has
     *   no namespace
     * @param name the local name if the parser is namespace aware, or just 
     *   the element name otherwise
     *
     * @exception NoSuchMethodException if the bean does not
     *  have a writeable property of the specified name
     */
    @Override
    public void end(String namespace, String name) throws Exception {
        String property = this.propertyName;

        if (property == null) {
            // If we don't have a specific property name,
            // use the element name.
            property = name;
        }

        // Get a reference to the top object
        Object top = this.getDigester().peek();

        // log some debugging information
        if (this.getDigester().getLog().isDebugEnabled()) {
            this.getDigester().getLog().debug(String.format("[BeanPropertySetterRule]{%s} Set %s property %s with text %s",
                this.getDigester().getMatch(),
                top.getClass().getName(),
                property,
                this.bodyText));
        }

        // Force an exception if the property does not exist
        // (BeanUtils.setProperty() silently returns in this case)
        if (top instanceof DynaBean) {
            DynaProperty desc = ((DynaBean) top).getDynaClass().getDynaProperty(property);
            if (desc == null) {
                throw new NoSuchMethodException("Bean has no property named "
                        + property);
            }
        } else /* this is a standard JavaBean */ {
            PropertyDescriptor desc = PropertyUtils.getPropertyDescriptor(top, property);
            if (desc == null) {
                throw new NoSuchMethodException("Bean has no property named "
                        + property);
            }
        }

        // Set the property (with conversion as necessary)
        BeanUtils.setProperty(top, property, this.bodyText);
    }

    /**
     * Clean up after parsing is complete.
     */
    @Override
    public void finish() throws Exception {
        this.bodyText = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("BeanPropertySetterRule[%s]", this.propertyName);
    }

}
