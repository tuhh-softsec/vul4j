/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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


import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.HashMap;
import java.beans.PropertyDescriptor;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.PropertyUtils;

import org.xml.sax.Attributes;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * <p>Rule implementation that sets properties on the object at the top of the
 * stack, based on child elements with names matching properties on that 
 * object.</p>
 *
 * <p>Example input that can be processed by this rule:</p>
 * <pre>
 *   [point]
 *    [x]7[/x]
 *    [y]9[/y]
 *   [/point]
 * </pre>
 *
 * <p>This rule supports custom mapping of attribute names to property names.
 * The default mapping for particular attributes can be overridden by using 
 * {@link #SetNestedPropertiesRule(String[] elementNames,
 *                                 String[] propertyNames)}.
 * This allows child elements to be mapped to properties with different names.
 * Certain elements can also be marked to be ignored.</p>
 *
 * <p>A very similar effect can be achieved using a combination of the 
 * <code>BeanPropertySetterRule</code> and the <code>ExtendedBaseRules</code> 
 * rules manager; this <code>Rule</code>, however, works fine with the default 
 * <code>RulesBase</code> rules manager.</p>
 */

public class SetNestedPropertiesRule extends Rule {

    private static final String PROP_IGNORE = "ignore-me";
    
    private Log log = null;
    
    private AnyChildRule anyChildRule = new AnyChildRule();
    private AnyChildRules newRules = new AnyChildRules(anyChildRule);
    private Rules oldRules = null;

    private boolean trimData = true;
    private boolean allowUnknownChildElements = false;
    
    private HashMap elementNames = new HashMap();

    // ----------------------------------------------------------- Constructors

    /**
     * Base constructor.
     */
    public SetNestedPropertiesRule() {
        // nothing to set up 
    }
    
    /** 
     * <p>Convenience constructor overrides the mapping for just one property.</p>
     *
     * <p>For details about how this works, see
     * {@link #SetNestedPropertiesRule(String[] elementNames, 
     * String[] propertyNames)}.</p>
     *
     * @param elementName map the child element to match 
     * @param propertyName to a property with this name
     */
    public SetNestedPropertiesRule(String elementName, String propertyName) {
        elementNames.put(elementName, propertyName);
    }
    
    /** 
     * <p>Constructor allows element->property mapping to be overriden.</p>
     *
     * <p>Two arrays are passed in. 
     * One contains the element names and the other the property names.
     * The element name / property name pairs are match by position
     * In order words, the first string in the element name list matches
     * to the first string in the property name list and so on.</p>
     *
     * <p>If a property name is null or the element name has no matching
     * property name, then this indicates that the element should be ignored.</p>
     * 
     * <h5>Example One</h5>
     * <p> The following constructs a rule that maps the <code>alt-city</code>
     * element to the <code>city</code> property and the <code>alt-state</code>
     * to the <code>state</code> property. 
     * All other child elements are mapped as usual using exact name matching.
     * <code><pre>
     *      SetNestedPropertiesRule(
     *                new String[] {"alt-city", "alt-state"}, 
     *                new String[] {"city", "state"});
     * </pre></code>
     *
     * <h5>Example Two</h5>
     * <p> The following constructs a rule that maps the <code>class</code>
     * element to the <code>className</code> property.
     * The element <code>ignore-me</code> is not mapped.
     * All other elements are mapped as usual using exact name matching.
     * <code><pre>
     *      SetPropertiesRule(
     *                new String[] {"class", "ignore-me"}, 
     *                new String[] {"className"});
     * </pre></code>
     *
     * @param elementNames names of elements to map
     * @param propertyNames names of properties mapped to
     */
    public SetNestedPropertiesRule(String[] elementNames, String[] propertyNames) {
        for (int i=0, size=elementNames.length; i<size; i++) {
            String propName = null;
            if (i < propertyNames.length) {
                propName = propertyNames[i];
            }
            
            if (propName == null) {
                this.elementNames.put(elementNames[i], PROP_IGNORE);
            }
            else {
                this.elementNames.put(elementNames[i], propName);
            }
        }
    }
        
    // --------------------------------------------------------- Public Methods


    public void setDigester(Digester digester) {
        super.setDigester(digester);
        log = digester.getLogger();
        anyChildRule.setDigester(digester);
    }
    
    /**
     * When set to true, any text within child elements will have leading
     * and trailing whitespace removed before assignment to the target
     * object. The default value for this attribute is true.
     */
    public void setTrimData(boolean trimData) {
        this.trimData = trimData;
    }
    
    /** See {@link #setTrimData}. */
     public boolean getTrimData() {
        return trimData;
    }
    
    /**
     * When set to true, any child element for which there is no
     * corresponding object property will cause an error to be reported.
     * The default value of this attribute is false (not allowed).
     */
    public void setAllowUnknownChildElements(boolean allowUnknownChildElements) {
        this.allowUnknownChildElements = allowUnknownChildElements;
    }
    
    /** See {@link #setAllowUnknownChildElements}. */
     public boolean getAllowUnknownChildElements() {
        return allowUnknownChildElements;
    }
    
    /**
     * Process the beginning of this element.
     *
     * @param namespace is the namespace this attribute is in, or null
     * @param name is the name of the current xml element
     * @param attributes is the attribute list of this element
     */
    public void begin(String namespace, String name, Attributes attributes) 
                      throws Exception {
        oldRules = digester.getRules();
        newRules.init(digester.getMatch()+"/", oldRules);
        digester.setRules(newRules);
    }
    
    /**
     * This is only invoked after all child elements have been processed,
     * so we can remove the custom Rules object that does the 
     * child-element-matching.
     */
    public void body(String bodyText) throws Exception {
        digester.setRules(oldRules);
    }

    /**
     * <p>Add an additional element name to property name mapping.
     * This is intended to be used from the xml rules.
     */
    public void addAlias(String elementName, String propertyName) {
        if (propertyName == null) {
            elementNames.put(elementName, PROP_IGNORE);
        }
        else {
            elementNames.put(elementName, propertyName);
        }
    }
  
    /**
     * Render a printable version of this Rule.
     */
    public String toString() {

        return ("SetNestedPropertiesRule");
    }

    //----------------------------------------- local classes 

    /** Private Rules implementation */
    private class AnyChildRules implements Rules {
        private String matchPrefix = null;
        private Rules decoratedRules = null;
        
        private ArrayList rules = new ArrayList(1);
        private AnyChildRule rule;
        
        public AnyChildRules(AnyChildRule rule) {
            this.rule = rule;
            rules.add(rule); 
        }
        
        public Digester getDigester() { return null; }
        public void setDigester(Digester digester) {}
        public String getNamespaceURI() {return null;}
        public void setNamespaceURI(String namespaceURI) {}
        public void add(String pattern, Rule rule) {}
        public void clear() {}
        
        public List match(String matchPath) { 
            return match(null,matchPath); 
        }
        
        public List match(String namespaceURI, String matchPath) {
            List match = decoratedRules.match(namespaceURI, matchPath);
            
            if ((matchPath.startsWith(matchPrefix)) &&
                (matchPath.indexOf('/', matchPrefix.length()) == -1)) {
                    
                // The current element is a direct child of the element
                // specified in the init method, so include it as the
                // first rule in the matches list. The way that
                // SetNestedPropertiesRule is used, it is in fact very
                // likely to be the only match, so we optimise that
                // solution by keeping a list with only the AnyChildRule
                // instance in it.
                
                if ((match == null || match.size()==0)) {
                    return rules;
                }
                else {
                    // it might not be safe to modify the returned list,
                    // so clone it first.
                    LinkedList newMatch = new LinkedList(match);
                    //newMatch.addFirst(rule);
                    newMatch.addLast(rule);
                    return newMatch;
                }
            }            
            else {
                return match;
            }
        }
        
        public List rules() {
            // This is not actually expected to be called.
            throw new RuntimeException(
                "AnyChildRules.rules not implemented.");
        }
        
        public void init(String prefix, Rules rules) {
            matchPrefix = prefix;
            decoratedRules = rules;
        }
    }
    
    private class AnyChildRule extends Rule {
        private String currChildNamespaceURI = null;
        private String currChildElementName = null;
        
        public void begin(String namespaceURI, String name, 
                              Attributes attributes) throws Exception {
    
            currChildNamespaceURI = namespaceURI;
            currChildElementName = name;
        }
        
        public void body(String value) throws Exception {
            boolean debug = log.isDebugEnabled();

            String propName = (String) elementNames.get(currChildElementName);
            if (propName == PROP_IGNORE) {
                // note: above deliberately tests for IDENTITY, not EQUALITY
                return;
            }
            if (propName == null) {
                propName = currChildElementName;
            }
    
            if (digester.log.isDebugEnabled()) {
                digester.log.debug("[SetNestedPropertiesRule]{" + digester.match +
                        "} Setting property '" + propName + "' to '" +
                        value + "'");
            }
    
            // Populate the corresponding properties of the top object
            Object top = digester.peek();
            if (digester.log.isDebugEnabled()) {
                if (top != null) {
                    digester.log.debug("[SetNestedPropertiesRule]{" + digester.match +
                                       "} Set " + top.getClass().getName() +
                                       " properties");
                } else {
                    digester.log.debug("[SetPropertiesRule]{" + digester.match +
                                       "} Set NULL properties");
                }
            }
 
            if (trimData) {
                value = value.trim();
            }

            if (!allowUnknownChildElements) {
                // Force an exception if the property does not exist
                // (BeanUtils.setProperty() silently returns in this case)
                if (top instanceof DynaBean) {
                    DynaProperty desc =
                        ((DynaBean) top).getDynaClass().getDynaProperty(propName);
                    if (desc == null) {
                        throw new NoSuchMethodException
                            ("Bean has no property named " + propName);
                    }
                } else /* this is a standard JavaBean */ {
                    PropertyDescriptor desc =
                        PropertyUtils.getPropertyDescriptor(top, propName);
                    if (desc == null) {
                        throw new NoSuchMethodException
                            ("Bean has no property named " + propName);
                    }
                }
            }
            
            BeanUtils.setProperty(top, propName, value);
        }
    
        public void end(String namespace, String name) throws Exception {
            currChildElementName = null;
        }
    }
}
