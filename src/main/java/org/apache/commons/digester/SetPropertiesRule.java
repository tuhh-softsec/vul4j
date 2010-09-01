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


import java.util.HashMap;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
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


    // ----------------------------------------------------------- Constructors


    /**
     * Default constructor sets only the the associated Digester.
     *
     * @param digester The digester with which this rule is associated
     *
     * @deprecated The digester instance is now set in the {@link Digester#addRule} method. 
     * Use {@link #SetPropertiesRule()} instead.
     */
    @Deprecated
    public SetPropertiesRule(Digester digester) {

        this();

    }
    

    /**
     * Base constructor.
     */
    public SetPropertiesRule() {

        // nothing to set up 

    }
    
    /** 
     * <p>Convenience constructor overrides the mapping for just one property.</p>
     *
     * <p>For details about how this works, see
     * {@link #SetPropertiesRule(String[] attributeNames, String[] propertyNames)}.</p>
     *
     * @param attributeName map this attribute 
     * @param propertyName to a property with this name
     */
    public SetPropertiesRule(String attributeName, String propertyName) {
        
        attributeNames = new String[1];
        attributeNames[0] = attributeName;
        propertyNames = new String[1];
        propertyNames[0] = propertyName;
    }
    
    /** 
     * <p>Constructor allows attribute->property mapping to be overriden.</p>
     *
     * <p>Two arrays are passed in. 
     * One contains the attribute names and the other the property names.
     * The attribute name / property name pairs are match by position
     * In order words, the first string in the attribute name list matches
     * to the first string in the property name list and so on.</p>
     *
     * <p>If a property name is null or the attribute name has no matching
     * property name, then this indicates that the attibute should be ignored.</p>
     * 
     * <h5>Example One</h5>
     * <p> The following constructs a rule that maps the <code>alt-city</code>
     * attribute to the <code>city</code> property and the <code>alt-state</code>
     * to the <code>state</code> property. 
     * All other attributes are mapped as usual using exact name matching.
     * <code><pre>
     *      SetPropertiesRule(
     *                new String[] {"alt-city", "alt-state"}, 
     *                new String[] {"city", "state"});
     * </pre></code>
     *
     * <h5>Example Two</h5>
     * <p> The following constructs a rule that maps the <code>class</code>
     * attribute to the <code>className</code> property.
     * The attribute <code>ignore-me</code> is not mapped.
     * All other attributes are mapped as usual using exact name matching.
     * <code><pre>
     *      SetPropertiesRule(
     *                new String[] {"class", "ignore-me"}, 
     *                new String[] {"className"});
     * </pre></code>
     *
     * @param attributeNames names of attributes to map
     * @param propertyNames names of properties mapped to
     */
    public SetPropertiesRule(String[] attributeNames, String[] propertyNames) {
        // create local copies
        this.attributeNames = new String[attributeNames.length];
        for (int i=0, size=attributeNames.length; i<size; i++) {
            this.attributeNames[i] = attributeNames[i];
        }
        
        this.propertyNames = new String[propertyNames.length];
        for (int i=0, size=propertyNames.length; i<size; i++) {
            this.propertyNames[i] = propertyNames[i];
        } 
    }
        
    // ----------------------------------------------------- Instance Variables
    
    /** 
     * Attribute names used to override natural attribute->property mapping
     */
    private String [] attributeNames;
    /** 
     * Property names used to override natural attribute->property mapping
     */    
    private String [] propertyNames;

    /**
     * Used to determine whether the parsing should fail if an property specified
     * in the XML is missing from the bean. Default is true for backward compatibility.
     */
    private boolean ignoreMissingProperty = true;


    // --------------------------------------------------------- Public Methods


    /**
     * Process the beginning of this element.
     *
     * @param attributes The attribute list of this element
     */
    @Override
    public void begin(Attributes attributes) throws Exception {
        
        // Build a set of attribute names and corresponding values
        HashMap<String, String> values = new HashMap<String, String>();
        
        // set up variables for custom names mappings
        int attNamesLength = 0;
        if (attributeNames != null) {
            attNamesLength = attributeNames.length;
        }
        int propNamesLength = 0;
        if (propertyNames != null) {
            propNamesLength = propertyNames.length;
        }
        
        
        for (int i = 0; i < attributes.getLength(); i++) {
            String name = attributes.getLocalName(i);
            if ("".equals(name)) {
                name = attributes.getQName(i);
            }
            String value = attributes.getValue(i);
            
            // we'll now check for custom mappings
            for (int n = 0; n<attNamesLength; n++) {
                if (name.equals(attributeNames[n])) {
                    if (n < propNamesLength) {
                        // set this to value from list
                        name = propertyNames[n];
                    
                    } else {
                        // set name to null
                        // we'll check for this later
                        name = null;
                    }
                    break;
                }
            } 
            
            if (digester.log.isDebugEnabled()) {
                digester.log.debug("[SetPropertiesRule]{" + digester.match +
                        "} Setting property '" + name + "' to '" +
                        value + "'");
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
                
                Object top = digester.peek();
                boolean test =  PropertyUtils.isWriteable(top, name);
                if (!test)
                    throw new NoSuchMethodException("Property " + name + " can't be set");
            }
            
            if (name != null) {
                values.put(name, value);
            } 
        }

        // Populate the corresponding properties of the top object
        Object top = digester.peek();
        if (digester.log.isDebugEnabled()) {
            if (top != null) {
                digester.log.debug("[SetPropertiesRule]{" + digester.match +
                                   "} Set " + top.getClass().getName() +
                                   " properties");
            } else {
                digester.log.debug("[SetPropertiesRule]{" + digester.match +
                                   "} Set NULL properties");
            }
        }
        BeanUtils.populate(top, values);


    }


    /**
     * <p>Add an additional attribute name to property name mapping.
     * This is intended to be used from the xml rules.
     */
    public void addAlias(String attributeName, String propertyName) {
        
        // this is a bit tricky.
        // we'll need to resize the array.
        // probably should be synchronized but digester's not thread safe anyway
        if (attributeNames == null) {
            
            attributeNames = new String[1];
            attributeNames[0] = attributeName;
            propertyNames = new String[1];
            propertyNames[0] = propertyName;        
            
        } else {
            int length = attributeNames.length;
            String [] tempAttributes = new String[length + 1];
            for (int i=0; i<length; i++) {
                tempAttributes[i] = attributeNames[i];
            }
            tempAttributes[length] = attributeName;
            
            String [] tempProperties = new String[length + 1];
            for (int i=0; i<length && i< propertyNames.length; i++) {
                tempProperties[i] = propertyNames[i];
            }
            tempProperties[length] = propertyName;
            
            propertyNames = tempProperties;
            attributeNames = tempAttributes;
        }        
    }
  

    /**
     * Render a printable version of this Rule.
     */
    @Override
    public String toString() {

        StringBuffer sb = new StringBuffer("SetPropertiesRule[");
        sb.append("]");
        return (sb.toString());

    }

    /**
     * <p>Are attributes found in the xml without matching properties to be ignored?
     * </p><p>
     * If false, the parsing will interrupt with an <code>NoSuchMethodException</code>
     * if a property specified in the XML is not found. The default is true.
     * </p>
     * @return true if skipping the unmatched attributes.
     */
    public boolean isIgnoreMissingProperty() {

        return this.ignoreMissingProperty;
    }

    /**
     * Sets whether attributes found in the xml without matching properties 
     * should be ignored.
     * If set to false, the parsing will throw an <code>NoSuchMethodException</code>
     * if an unmatched
     * attribute is found. This allows to trap misspellings in the XML file.
     * @param ignoreMissingProperty false to stop the parsing on unmatched attributes.
     */
    public void setIgnoreMissingProperty(boolean ignoreMissingProperty) {

        this.ignoreMissingProperty = ignoreMissingProperty;
    }


}
