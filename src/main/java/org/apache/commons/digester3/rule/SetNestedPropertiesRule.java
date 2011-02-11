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

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.Rule;
import org.apache.commons.digester3.spi.Rules;
import org.xml.sax.Attributes;

/**
 * <p>Rule implementation that sets properties on the object at the top of the
 * stack, based on child elements with names matching properties on that 
 * object.</p>
 *
 * <p>Example input that can be processed by this rule:</p>
 * <pre>
 *   [widget]
 *    [height]7[/height]
 *    [width]8[/width]
 *    [label]Hello, world[/label]
 *   [/widget]
 * </pre>
 *
 * <p>For each child element of [widget], a corresponding setter method is 
 * located on the object on the top of the digester stack, the body text of
 * the child element is converted to the type specified for the (sole) 
 * parameter to the setter method, then the setter method is invoked.</p>
 *
 * <p>This rule supports custom mapping of xml element names to property names.
 * The default mapping for particular elements can be overridden by using 
 * {@link #SetNestedPropertiesRule(String[] elementNames,
 *                                 String[] propertyNames)}.
 * This allows child elements to be mapped to properties with different names.
 * Certain elements can also be marked to be ignored.</p>
 *
 * <p>A very similar effect can be achieved using a combination of the 
 * <code>BeanPropertySetterRule</code> and the <code>ExtendedBaseRules</code> 
 * rules manager; this <code>Rule</code>, however, works fine with the default 
 * <code>RulesBase</code> rules manager.</p>
 *
 * <p>Note that this rule is designed to be used to set only "primitive"
 * bean properties, eg String, int, boolean. If some of the child xml elements
 * match ObjectCreateRule rules (ie cause objects to be created) then you must
 * use one of the more complex constructors to this rule to explicitly skip
 * processing of that xml element, and define a SetNextRule (or equivalent) to
 * handle assigning the child object to the appropriate property instead.</p>
 *
 * <p><b>Implementation Notes</b></p>
 *
 * <p>This class works by creating its own simple Rules implementation. When
 * begin is invoked on this rule, the digester's current rules object is
 * replaced by a custom one. When end is invoked for this rule, the original
 * rules object is restored. The digester rules objects therefore behave in
 * a stack-like manner.</p>
 *
 * <p>For each child element encountered, the custom Rules implementation
 * ensures that a special AnyChildRule instance is included in the matches 
 * returned to the digester, and it is this rule instance that is responsible 
 * for setting the appropriate property on the target object (if such a property 
 * exists). The effect is therefore like a "trailing wildcard pattern". The 
 * custom Rules implementation also returns the matches provided by the 
 * underlying Rules implementation for the same pattern, so other rules
 * are not "disabled" during processing of a SetNestedPropertiesRule.</p> 
 *
 * <p>TODO: Optimize this class. Currently, each time begin is called,
 * new AnyChildRules and AnyChildRule objects are created. It should be
 * possible to cache these in normal use (though watch out for when a rule
 * instance is invoked re-entrantly!).</p>
 */
public class SetNestedPropertiesRule extends Rule {

    private final Map<String, String> elementNames;

    private final boolean trimData;

    private final boolean allowUnknownChildElements;

    /**
     * Constructor which allows element->property mapping to be overridden.
     *
     * @param elementNames
     * @param trimData
     * @param allowUnknownChildElements
     */
    public SetNestedPropertiesRule(Map<String, String> elementNames, boolean trimData, boolean allowUnknownChildElements) {
        this.elementNames = elementNames;
        this.trimData = trimData;
        this.allowUnknownChildElements = allowUnknownChildElements;
    }

    /**
     * Process the beginning of this element.
     *
     * @param namespace is the namespace this attribute is in, or null
     * @param name is the name of the current xml element
     * @param attributes is the attribute list of this element
     */
    @Override
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        Rules oldRules = this.getDigester().getRules();
        AnyChildRule anyChildRule = new AnyChildRule();
        anyChildRule.setDigester(this.getDigester());
        AnyChildRules newRules = new AnyChildRules(anyChildRule);
        newRules.init(this.getDigester().getMatch() + "/", oldRules);
        this.getDigester().setRules(newRules);
    }

    /**
     * This is only invoked after all child elements have been processed,
     * so we can remove the custom Rules object that does the 
     * child-element-matching.
     */
    @Override
    public void body(String namespace, String name, String text) throws Exception {
        AnyChildRules newRules = (AnyChildRules) this.getDigester().getRules();
        this.getDigester().setRules(newRules.getOldRules());
    }

    /**
     * Render a printable version of this Rule.
     */
    @Override
    public String toString() {
        return String.format("SetNestedPropertiesRule[allowUnknownChildElements=%s, trimData=%s, elementNames=%s]",
                this.allowUnknownChildElements,
                this.trimData,
                this.elementNames);
    }

    //----------------------------------------- local classes 

    /** Private Rules implementation */
    private class AnyChildRules implements Rules {

        private String matchPrefix = null;

        private Rules decoratedRules = null;

        private List<Rule> rules = new ArrayList<Rule>(1);

        private AnyChildRule rule;

        public AnyChildRules(AnyChildRule rule) {
            this.rule = rule;
            rules.add(rule); 
        }

        public Digester getDigester() { return null; }

        public void setDigester(Digester digester) {}

        public String getNamespaceURI() { return null; }

        public void setNamespaceURI(String namespaceURI) {}

        public void add(String pattern, Rule rule) {}

        public void clear() {}

        public List<Rule> match(String namespaceURI, String matchPath) {
            List<Rule> match = decoratedRules.match(namespaceURI, matchPath);

            if ((matchPath.startsWith(matchPrefix))
                    && (matchPath.indexOf('/', matchPrefix.length()) == -1)) {

                // The current element is a direct child of the element
                // specified in the init method, so we want to ensure that
                // the rule passed to this object's constructor is included
                // in the returned list of matching rules.
                if ((match == null || match.size()==0)) {
                    // The "real" rules class doesn't have any matches for
                    // the specified path, so we return a list containing
                    // just one rule: the one passed to this object's
                    // constructor.
                    return rules;
                } else {
                    // The "real" rules class has rules that match the current
                    // node, so we return this list *plus* the rule passed to
                    // this object's constructor.
                    //
                    // It might not be safe to modify the returned list,
                    // so clone it first.
                    LinkedList<Rule> newMatch = new LinkedList<Rule>(match);
                    newMatch.addLast(rule);
                    return newMatch;
                }
            } else {
                return match;
            }
        }

        public List<Rule> rules() {
            // This is not actually expected to be called during normal
            // processing.
            //
            // There is only one known case where this is called; when a rule
            // returned from AnyChildRules.getMatch() is invoked and throws a
            // SAXException then method Digester.endDocument will be called
            // without having "uninstalled" the AnyChildRules ionstance. That
            // method attempts to invoke the "finish" method for every Rule
            // instance - and thus needs to call rules() on its Rules object,
            // which is this one. Actually, java 1.5 and 1.6beta2 have a
            // bug in their xml implementation such that endDocument is not 
            // called after a SAXException, but other parsers (eg Aelfred)
            // do call endDocument. Here, we therefore need to return the
            // rules registered with the underlying Rules object.
            if (this.getDigester().getLog().isDebugEnabled()) {
                this.getDigester().getLog().debug("AnyChildRules.rules invoked.");
            }
            return decoratedRules.rules();
        }

        public void init(String prefix, Rules rules) {
            matchPrefix = prefix;
            decoratedRules = rules;
        }

        public Rules getOldRules() {
            return decoratedRules;
        }
    }

    private class AnyChildRule extends Rule {

        private String currChildNamespaceURI = null;

        private String currChildElementName = null;

        @Override
        public void begin(String namespaceURI, String name, Attributes attributes) throws Exception {
            currChildNamespaceURI = namespaceURI;
            currChildElementName = name;
        }

        @Override
        public void body(String namespace, String name, String text) throws Exception {
            String propName = currChildElementName;
            if (elementNames.containsKey(currChildElementName)) {
                // overide propName
                propName = elementNames.get(currChildElementName);
                if (propName == null) {
                    // user wants us to ignore this element
                    return;
                }
            }
    
            boolean debug = this.getDigester().getLog().isDebugEnabled();

            if (debug) {
                this.getDigester().getLog().debug(
                        String.format("[SetNestedPropertiesRule]{%s} Setting property '%s' to '%s'",
                                this.getDigester().getMatch(),
                                propName,
                                text));
            }
    
            // Populate the corresponding properties of the top object
            Object top = this.getDigester().peek();
            if (debug) {
                this.getDigester().getLog().debug(String.format("[SetNestedPropertiesRule]{%s} Set %s properties",
                        this.getDigester().getMatch(),
                        (top != null ? top.getClass().getName() : "NULL")));
            }
 
            if (trimData) {
                text = text.trim();
            }

            if (!allowUnknownChildElements) {
                // Force an exception if the property does not exist
                // (BeanUtils.setProperty() silently returns in this case)
                if (top instanceof DynaBean) {
                    DynaProperty desc =
                        ((DynaBean) top).getDynaClass().getDynaProperty(propName);
                    if (desc == null) {
                        throw new NoSuchMethodException("Bean has no property named " + propName);
                    }
                } else /* this is a standard JavaBean */ {
                    PropertyDescriptor desc = PropertyUtils.getPropertyDescriptor(top, propName);
                    if (desc == null) {
                        throw new NoSuchMethodException("Bean has no property named " + propName);
                    }
                }
            }

            try {
                BeanUtils.setProperty(top, propName, text);
            } catch(NullPointerException e) {
                this.getDigester().getLog().error(String.format("NullPointerException: top=%s, propName=%s, value=%s!",
                        top,
                        propName,
                        text));
                 throw e;
            }
        }

        @Override
        public void end(String namespace, String name) throws Exception {
            this.currChildElementName = null;
        }

    }

}
