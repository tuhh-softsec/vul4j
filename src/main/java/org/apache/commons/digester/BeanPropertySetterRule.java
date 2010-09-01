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


package org.apache.commons.digester;


import java.beans.PropertyDescriptor;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.PropertyUtils;


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


    // ----------------------------------------------------------- Constructors


    /**
     * <p>Construct rule that sets the given property from the body text.</p>
     *
     * @param digester associated <code>Digester</code>
     * @param propertyName name of property to set
     *
     * @deprecated The digester instance is now set in the {@link Digester#addRule} method. 
     * Use {@link #BeanPropertySetterRule(String propertyName)} instead.
     */
    @Deprecated
    public BeanPropertySetterRule(Digester digester, String propertyName) {

        this(propertyName);

    }

    /**
     * <p>Construct rule that automatically sets a property from the body text.
     *
     * <p> This construct creates a rule that sets the property
     * on the top object named the same as the current element.
     *
     * @param digester associated <code>Digester</code>
     *     
     * @deprecated The digester instance is now set in the {@link Digester#addRule} method. 
     * Use {@link #BeanPropertySetterRule()} instead.
     */
    @Deprecated
    public BeanPropertySetterRule(Digester digester) {

        this();

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
     * <p>Construct rule that automatically sets a property from the body text.
     *
     * <p> This construct creates a rule that sets the property
     * on the top object named the same as the current element.
     */
    public BeanPropertySetterRule() {

        this((String)null);

    }
    
    // ----------------------------------------------------- Instance Variables


    /**
     * Set this property on the top object.
     */
    protected String propertyName = null;


    /**
     * The body text used to set the property.
     */
    protected String bodyText = null;


    // --------------------------------------------------------- Public Methods


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
    public void body(String namespace, String name, String text)
        throws Exception {

        // log some debugging information
        if (digester.log.isDebugEnabled()) {
            digester.log.debug("[BeanPropertySetterRule]{" +
                    digester.match + "} Called with text '" + text + "'");
        }

        bodyText = text.trim();

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

        String property = propertyName;

        if (property == null) {
            // If we don't have a specific property name,
            // use the element name.
            property = name;
        }

        // Get a reference to the top object
        Object top = digester.peek();

        // log some debugging information
        if (digester.log.isDebugEnabled()) {
            digester.log.debug("[BeanPropertySetterRule]{" + digester.match +
                    "} Set " + top.getClass().getName() + " property " +
                               property + " with text " + bodyText);
        }

        // Force an exception if the property does not exist
        // (BeanUtils.setProperty() silently returns in this case)
        if (top instanceof DynaBean) {
            DynaProperty desc =
                ((DynaBean) top).getDynaClass().getDynaProperty(property);
            if (desc == null) {
                throw new NoSuchMethodException
                    ("Bean has no property named " + property);
            }
        } else /* this is a standard JavaBean */ {
            PropertyDescriptor desc =
                PropertyUtils.getPropertyDescriptor(top, property);
            if (desc == null) {
                throw new NoSuchMethodException
                    ("Bean has no property named " + property);
            }
        }

        // Set the property (with conversion as necessary)
        BeanUtils.setProperty(top, property, bodyText);

    }


    /**
     * Clean up after parsing is complete.
     */
    @Override
    public void finish() throws Exception {

        bodyText = null;

    }


    /**
     * Render a printable version of this Rule.
     */
    @Override
    public String toString() {

        StringBuffer sb = new StringBuffer("BeanPropertySetterRule[");
        sb.append("propertyName=");
        sb.append(propertyName);
        sb.append("]");
        return (sb.toString());

    }

}
