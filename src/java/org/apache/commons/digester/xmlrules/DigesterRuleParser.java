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


package org.apache.commons.digester.xmlrules;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.digester.AbstractObjectCreationFactory;
import org.apache.commons.digester.BeanPropertySetterRule;
import org.apache.commons.digester.CallMethodRule;
import org.apache.commons.digester.CallParamRule;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.FactoryCreateRule;
import org.apache.commons.digester.NodeCreateRule;
import org.apache.commons.digester.ObjectCreateRule;
import org.apache.commons.digester.ObjectParamRule;
import org.apache.commons.digester.Rule;
import org.apache.commons.digester.RuleSetBase;
import org.apache.commons.digester.Rules;
import org.apache.commons.digester.SetNestedPropertiesRule;
import org.apache.commons.digester.SetNextRule;
import org.apache.commons.digester.SetPropertiesRule;
import org.apache.commons.digester.SetPropertyRule;
import org.apache.commons.digester.SetRootRule;
import org.apache.commons.digester.SetTopRule;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


/**
 * This is a RuleSet that parses XML into Digester rules, and then
 * adds those rules to a 'target' Digester.
 *
 * @since 1.2
 */

public class DigesterRuleParser extends RuleSetBase {
    
    public static final String DIGESTER_PUBLIC_ID = "-//Jakarta Apache //DTD digester-rules XML V1.0//EN";
    
    /**
     * path to the DTD
     */
    private String digesterDtdUrl;
    
    /**
     * This is the digester to which we are adding the rules that we parse
     * from the Rules XML document.
     */
    protected Digester targetDigester;

    /** See {@link #setBasePath}. */
    protected String basePath = "";
    
    /**
     * A stack whose toString method returns a '/'-separated concatenation
     * of all the elements in the stack.
     */
    protected class PatternStack<E> extends Stack<E> {

        private static final long serialVersionUID = 1L;

        @Override
        public String toString() {
            StringBuffer str = new StringBuffer();
            for (int i = 0; i < size(); i++) {
                String elem = get(i).toString();
                if (elem.length() > 0) {
                    if (str.length() > 0) {
                        str.append('/');
                    }
                    str.append(elem);
                }
            }
            return str.toString();
        }
    }
    
    /**
     * A stack used to maintain the current pattern. The Rules XML document
     * type allows nesting of patterns. If an element defines a matching
     * pattern, the resulting pattern is a concatenation of that pattern with
     * all the ancestor elements' patterns. Hence the need for a stack.
     */
    protected PatternStack<String> patternStack;
    
    /**
     * Used to detect circular includes
     */
    private Set<String> includedFiles = new HashSet<String>();
    
    /**
     * Constructs a DigesterRuleParser. This object will be inoperable
     * until the target digester is set, via <code>setTarget(Digester)</code>
     */
    public DigesterRuleParser() {
        patternStack = new PatternStack<String>();
    }
    
    /**
     * Constructs a rule set for converting XML digester rule descriptions
     * into Rule objects, and adding them to the given Digester
     * @param targetDigester the Digester to add the rules to
     */
    public DigesterRuleParser(Digester targetDigester) {
        this.targetDigester = targetDigester;
        patternStack = new PatternStack<String>();
    }
    
    /**
     * Constructs a rule set for parsing an XML digester rule file that
     * has been included within an outer XML digester rule file. In this
     * case, we must pass the pattern stack and the target digester
     * to the rule set, as well as the list of files that have already
     * been included, for cycle detection.
     * @param targetDigester the Digester to add the rules to
     * @param stack Stack containing the prefix pattern string to be prepended
     * to any pattern parsed by this rule set.
     */
    private DigesterRuleParser(Digester targetDigester,
                                PatternStack<String> stack, Set<String> includedFiles) {
        this.targetDigester = targetDigester;
        patternStack = stack;
        this.includedFiles = includedFiles;
    }
    
    /**
     * Sets the digester into which to add the parsed rules
     * @param d the Digester to add the rules to
     */
    public void setTarget(Digester d) {
        targetDigester = d;
    }
    
    /**
     * Set a base pattern beneath which all the rules loaded by this
     * object will be registered. If this string is not empty, and does
     * not end in a "/", then one will be added.
     *
     * @since 1.6
     */
    public void setBasePath(String path) {
        if (path == null) {
            basePath = "";
        }
        else if ((path.length() > 0) && !path.endsWith("/")) {
            basePath = path + "/";
        } else {
            basePath = path;
        }
    }

    /**
     * Sets the location of the digester rules DTD. This is the DTD used
     * to validate the rules XML file.
     */
    public void setDigesterRulesDTD(String dtdURL) {
        digesterDtdUrl = dtdURL;
    }
    
    /**
     * Returns the location of the DTD used to validate the digester rules
     * XML document.
     */
    protected String getDigesterRulesDTD() {
        //ClassLoader classLoader = getClass().getClassLoader();
        //URL url = classLoader.getResource(DIGESTER_DTD_PATH);
        //return url.toString();
        return digesterDtdUrl;
    }
    
    /**
     * Adds a rule the the target digester. After a rule has been created by
     * parsing the XML, it is added to the digester by calling this method.
     * Typically, this method is called via reflection, when executing
     * a SetNextRule, from the Digester that is parsing the rules XML.
     * @param rule a Rule to add to the target digester.
     */
    public void add(Rule rule) {
        targetDigester.addRule(
            basePath + patternStack.toString(), rule);
    }
    
    
    /**
     * Add to the given digester the set of Rule instances used to parse an XML
     * document defining Digester rules. When the digester parses an XML file,
     * it will add the resulting rules & patterns to the 'target digester'
     * that was passed in this RuleSet's constructor.<P>
     * If you extend this class to support additional rules, your implementation
     * should of this method should call this implementation first: i.e.
     * <code>super.addRuleInstances(digester);</code>
     */
    @Override
    public void addRuleInstances(Digester digester) {
        final String ruleClassName = Rule.class.getName();
        digester.register(DIGESTER_PUBLIC_ID, getDigesterRulesDTD());
        
        digester.addRule("*/pattern", new PatternRule("value"));
        
        digester.addRule("*/include", new IncludeRule());
        
        digester.addFactoryCreate("*/bean-property-setter-rule", new BeanPropertySetterRuleFactory());
        digester.addRule("*/bean-property-setter-rule", new PatternRule("pattern"));
        digester.addSetNext("*/bean-property-setter-rule", "add", ruleClassName);
        
        digester.addFactoryCreate("*/call-method-rule", new CallMethodRuleFactory());
        digester.addRule("*/call-method-rule", new PatternRule("pattern"));
        digester.addSetNext("*/call-method-rule", "add", ruleClassName);

        digester.addFactoryCreate("*/object-param-rule", new ObjectParamRuleFactory());
        digester.addRule("*/object-param-rule", new PatternRule("pattern"));
        digester.addSetNext("*/object-param-rule", "add", ruleClassName);
        
        digester.addFactoryCreate("*/call-param-rule", new CallParamRuleFactory());
        digester.addRule("*/call-param-rule", new PatternRule("pattern"));
        digester.addSetNext("*/call-param-rule", "add", ruleClassName);
        
        digester.addFactoryCreate("*/factory-create-rule", new FactoryCreateRuleFactory());
        digester.addRule("*/factory-create-rule", new PatternRule("pattern"));
        digester.addSetNext("*/factory-create-rule", "add", ruleClassName);
        
        digester.addFactoryCreate("*/object-create-rule", new ObjectCreateRuleFactory());
        digester.addRule("*/object-create-rule", new PatternRule("pattern"));
        digester.addSetNext("*/object-create-rule", "add", ruleClassName);
        
        digester.addFactoryCreate("*/node-create-rule", new NodeCreateRuleFactory());
        digester.addRule("*/node-create-rule", new PatternRule("pattern"));
        digester.addSetNext("*/node-create-rule", "add", ruleClassName);
        
        digester.addFactoryCreate("*/set-properties-rule", new SetPropertiesRuleFactory());
        digester.addRule("*/set-properties-rule", new PatternRule("pattern"));
        digester.addSetNext("*/set-properties-rule", "add", ruleClassName);
        
        digester.addRule("*/set-properties-rule/alias", new SetPropertiesAliasRule());
        
        digester.addFactoryCreate("*/set-property-rule", new SetPropertyRuleFactory());
        digester.addRule("*/set-property-rule", new PatternRule("pattern"));
        digester.addSetNext("*/set-property-rule", "add", ruleClassName);
        
        digester.addFactoryCreate("*/set-nested-properties-rule", new SetNestedPropertiesRuleFactory());
        digester.addRule("*/set-nested-properties-rule", new PatternRule("pattern"));
        digester.addSetNext("*/set-nested-properties-rule", "add", ruleClassName);
        
        digester.addRule("*/set-nested-properties-rule/alias", new SetNestedPropertiesAliasRule());
        
        digester.addFactoryCreate("*/set-top-rule", new SetTopRuleFactory());
        digester.addRule("*/set-top-rule", new PatternRule("pattern"));
        digester.addSetNext("*/set-top-rule", "add", ruleClassName);
        
        digester.addFactoryCreate("*/set-next-rule", new SetNextRuleFactory());
        digester.addRule("*/set-next-rule", new PatternRule("pattern"));
        digester.addSetNext("*/set-next-rule", "add", ruleClassName);
        digester.addFactoryCreate("*/set-root-rule", new SetRootRuleFactory());
        digester.addRule("*/set-root-rule", new PatternRule("pattern"));
        digester.addSetNext("*/set-root-rule", "add", ruleClassName);
    }
    
    
    /**
     * A rule for extracting the pattern matching strings from the rules XML.
     * In the digester-rules document type, a pattern can either be declared
     * in the 'value' attribute of a <pattern> element (in which case the pattern
     * applies to all rules elements contained within the <pattern> element),
     * or it can be declared in the optional 'pattern' attribute of a rule
     * element.
     */
    private class PatternRule extends Rule {
        
        private String attrName;
        private String pattern = null;
        
        /**
         * @param attrName The name of the attribute containing the pattern
         */
        public PatternRule(String attrName) {
            super();
            this.attrName = attrName;
        }
        
        /**
         * If a pattern is defined for the attribute, push it onto the
         * pattern stack.
         */
        @Override
        public void begin(Attributes attributes) {
            pattern = attributes.getValue(attrName);
            if (pattern != null) {
                patternStack.push(pattern);
            }
        }
        
        /**
         * If there was a pattern for this element, pop it off the pattern
         * stack.
         */
        @Override
        public void end() {
            if (pattern != null) {
                patternStack.pop();
            }
        }
    }
    
    /**
     * A rule for including one rules XML file within another. Included files
     * behave as if they are 'macro-expanded' within the includer. This means
     * that the values of the pattern stack are prefixed to every pattern
     * in the included rules. <p>This rule will detect 'circular' includes,
     * which would result in infinite recursion. It throws a
     * CircularIncludeException when a cycle is detected, which will terminate
     * the parse.
     */
    private class IncludeRule extends Rule {
        public IncludeRule() {
            super();
        }
        
        /**
         * To include a rules xml file, we instantiate another Digester, and
         * another DigesterRulesRuleSet. We pass the
         * pattern stack and the target Digester to the new rule set, and
         * tell the Digester to parse the file.
         */
        @Override
        public void begin(Attributes attributes) throws Exception {
            // The path attribute gives the URI to another digester rules xml file
            String fileName = attributes.getValue("path");
            if (fileName != null && fileName.length() > 0) {
                includeXMLRules(fileName);
            }
            
            // The class attribute gives the name of a class that implements
            // the DigesterRulesSource interface
            String className = attributes.getValue("class");
            if (className != null && className.length() > 0) {
                includeProgrammaticRules(className);
            }
        }
        
        /**
         * Creates another DigesterRuleParser, and uses it to extract the rules
         * out of the give XML file. The contents of the current pattern stack
         * will be prepended to all of the pattern strings parsed from the file.
         */
        private void includeXMLRules(String fileName)
                        throws IOException, SAXException, CircularIncludeException {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl == null) {
                cl = DigesterRuleParser.this.getClass().getClassLoader();
            }
            URL fileURL = cl.getResource(fileName);
            if (fileURL == null) {
                throw new FileNotFoundException("File \"" + fileName + "\" not found.");
            }
            fileName = fileURL.toExternalForm();
            if (includedFiles.add(fileName) == false) {
                // circular include detected
                throw new CircularIncludeException(fileName);
            }
            // parse the included xml file
            DigesterRuleParser includedSet =
                        new DigesterRuleParser(targetDigester, patternStack, includedFiles);
            includedSet.setDigesterRulesDTD(getDigesterRulesDTD());
            Digester digester = new Digester();
            digester.addRuleSet(includedSet);
            digester.push(DigesterRuleParser.this);
            digester.parse(fileName);
            includedFiles.remove(fileName);
        }
        
        /**
         * Creates an instance of the indicated class. The class must implement
         * the DigesterRulesSource interface. Passes the target digester to
         * that instance. The DigesterRulesSource instance is supposed to add
         * rules into the digester. The contents of the current pattern stack
         * will be automatically prepended to all of the pattern strings added
         * by the DigesterRulesSource instance.
         */
        private void includeProgrammaticRules(String className)
                        throws ClassNotFoundException, ClassCastException,
                        InstantiationException, IllegalAccessException {
            
            Class<?> cls = Class.forName(className);
            DigesterRulesSource rulesSource = (DigesterRulesSource) cls.newInstance();
            
            // wrap the digester's Rules object, to prepend pattern
            Rules digesterRules = targetDigester.getRules();
            Rules prefixWrapper =
                    new RulesPrefixAdapter(patternStack.toString(), digesterRules);
            
            targetDigester.setRules(prefixWrapper);
            try {
                rulesSource.getRules(targetDigester);
            } finally {
                // Put the unwrapped rules back
                targetDigester.setRules(digesterRules);
            }
        }
    }
    
    
    /**
     * Wraps a Rules object. Delegates all the Rules interface methods
     * to the underlying Rules object. Overrides the add method to prepend
     * a prefix to the pattern string.
     */
    private class RulesPrefixAdapter implements Rules {
        
        private Rules delegate;
        private String prefix;
        
        /**
         * @param patternPrefix the pattern string to prepend to the pattern
         * passed to the add method.
         * @param rules The wrapped Rules object. All of this class's methods
         * pass through to this object.
         */
        public RulesPrefixAdapter(String patternPrefix, Rules rules) {
            prefix = patternPrefix;
            delegate = rules;
        }
        
        /**
         * Register a new Rule instance matching a pattern which is constructed
         * by concatenating the pattern prefix with the given pattern.
         */
        public void add(String pattern, Rule rule) {
            StringBuffer buffer = new StringBuffer();
            buffer.append(prefix);
            if (!pattern.startsWith("/")) {
                buffer.append('/'); 
            }
            buffer.append(pattern);
            delegate.add(buffer.toString(), rule);
        }
        
        /**
         * This method passes through to the underlying Rules object.
         */
        public void clear() {
            delegate.clear();
        }
        
        /**
         * This method passes through to the underlying Rules object.
         */
        public Digester getDigester() {
            return delegate.getDigester();
        }
        
        /**
         * This method passes through to the underlying Rules object.
         */
        public String getNamespaceURI() {
            return delegate.getNamespaceURI();
        }
        
        /**
         * @deprecated Call match(namespaceURI,pattern) instead.
         */
        @Deprecated
        public List<Rule> match(String pattern) {
            return delegate.match(pattern);
        }
        
        /**
         * This method passes through to the underlying Rules object.
         */
        public List<Rule> match(String namespaceURI, String pattern) {
            return delegate.match(namespaceURI, pattern);
        }
        
        /**
         * This method passes through to the underlying Rules object.
         */
        public List<Rule> rules() {
            return delegate.rules();
        }
        
        /**
         * This method passes through to the underlying Rules object.
         */
        public void setDigester(Digester digester) {
            delegate.setDigester(digester);
        }
        
        /**
         * This method passes through to the underlying Rules object.
         */
        public void setNamespaceURI(String namespaceURI) {
            delegate.setNamespaceURI(namespaceURI);
        }
    }
    
    
    ///////////////////////////////////////////////////////////////////////
    // Classes beyond this point are ObjectCreationFactory implementations,
    // used to create Rule objects and initialize them from SAX attributes.
    ///////////////////////////////////////////////////////////////////////
    
    /**
     * Factory for creating a BeanPropertySetterRule.
     */
    private class BeanPropertySetterRuleFactory extends AbstractObjectCreationFactory {
        @Override
        public Object createObject(Attributes attributes) throws Exception {
            Rule beanPropertySetterRule = null;
            String propertyname = attributes.getValue("propertyname");
                
            if (propertyname == null) {
                // call the setter method corresponding to the element name.
                beanPropertySetterRule = new BeanPropertySetterRule();
            } else {
                beanPropertySetterRule = new BeanPropertySetterRule(propertyname);
            }
            
            return beanPropertySetterRule;
        }
        
    }

    /**
     * Factory for creating a CallMethodRule.
     */
    protected class CallMethodRuleFactory extends AbstractObjectCreationFactory {
        @Override
        public Object createObject(Attributes attributes) {
            Rule callMethodRule = null;
            String methodName = attributes.getValue("methodname");

            // Select which element is to be the target. Default to zero,
            // ie the top object on the stack.
            int targetOffset = 0;
            String targetOffsetStr = attributes.getValue("targetoffset");
            if (targetOffsetStr != null) {
                targetOffset = Integer.parseInt(targetOffsetStr);
            }

            if (attributes.getValue("paramcount") == null) {
                // call against empty method
                callMethodRule = new CallMethodRule(targetOffset, methodName);
            
            } else {
                int paramCount = Integer.parseInt(attributes.getValue("paramcount"));
                
                String paramTypesAttr = attributes.getValue("paramtypes");
                if (paramTypesAttr == null || paramTypesAttr.length() == 0) {
                    callMethodRule = new CallMethodRule(targetOffset, methodName, paramCount);
                } else {
                    String[] paramTypes = getParamTypes(paramTypesAttr);
                    callMethodRule = new CallMethodRule(
                        targetOffset, methodName, paramCount, paramTypes);
                }
            }
            return callMethodRule;
        }

        /**
         * Process the comma separated list of paramTypes
         * into an array of String class names
         */
        private String[] getParamTypes(String paramTypes) {
            String[] paramTypesArray;
            if( paramTypes != null ) {
                ArrayList<String> paramTypesList = new ArrayList<String>();
                StringTokenizer tokens = new StringTokenizer(
                        paramTypes, " \t\n\r,");
                while (tokens.hasMoreTokens()) {
                    paramTypesList.add(tokens.nextToken());
                }
                paramTypesArray = (String[])paramTypesList.toArray(new String[0]);
            } else {
                paramTypesArray = new String[0];
            }
            return paramTypesArray;
        }
    }
    
    /**
     * Factory for creating a CallParamRule.
     */
    protected class CallParamRuleFactory extends AbstractObjectCreationFactory {
    
        @Override
        public Object createObject(Attributes attributes) {
            // create callparamrule
            int paramIndex = Integer.parseInt(attributes.getValue("paramnumber"));
            String attributeName = attributes.getValue("attrname");
            String fromStack = attributes.getValue("from-stack");
            String stackIndex = attributes.getValue("stack-index");
            Rule callParamRule = null;

            if (attributeName == null) {
                if (stackIndex != null) {                    
                    callParamRule = new CallParamRule(
                        paramIndex, Integer.parseInt(stackIndex));                
                } else if (fromStack != null) {                
                    callParamRule = new CallParamRule(
                        paramIndex, Boolean.valueOf(fromStack).booleanValue());                
                } else {
                    callParamRule = new CallParamRule(paramIndex);     
                }
            } else {
                if (fromStack == null) {
                    callParamRule = new CallParamRule(paramIndex, attributeName);                    
                } else {
                    // specifying both from-stack and attribute name is not allowed
                    throw new RuntimeException(
                        "Attributes from-stack and attrname cannot both be present.");
                }
            }
            return callParamRule;
        }
    }
    
    /**
     * Factory for creating a ObjectParamRule
     */
    protected class ObjectParamRuleFactory extends AbstractObjectCreationFactory {
        @Override
        public Object createObject(Attributes attributes) throws Exception {
            // create callparamrule
            int paramIndex = Integer.parseInt(attributes.getValue("paramnumber"));
            String attributeName = attributes.getValue("attrname");
            String type = attributes.getValue("type");
            String value = attributes.getValue("value");

            Rule objectParamRule = null;

            // type name is requried
            if (type == null) {
                throw new RuntimeException("Attribute 'type' is required.");
            }

            // create object instance
            Object param = null;
            Class<?> clazz = Class.forName(type);
            if (value == null) {
                param = clazz.newInstance();
            } else {
                param = ConvertUtils.convert(value, clazz);
            }

            if (attributeName == null) {
                objectParamRule = new ObjectParamRule(paramIndex, param);
            } else {
                objectParamRule = new ObjectParamRule(paramIndex, attributeName, param);
            }
            return objectParamRule;
        }
     }
    
        /**
         * Factory for creating a NodeCreateRule
         */
    protected class NodeCreateRuleFactory extends AbstractObjectCreationFactory {

        @Override
        public Object createObject(Attributes attributes) throws Exception {

            String nodeType = attributes.getValue("type");
            if (nodeType == null || "".equals(nodeType)) {

                // uses Node.ELEMENT_NODE
                return new NodeCreateRule();
            } else if ("element".equals(nodeType)) {

                return new NodeCreateRule(Node.ELEMENT_NODE);
            } else if ("fragment".equals(nodeType)) {

                return new NodeCreateRule(Node.DOCUMENT_FRAGMENT_NODE);
            } else {

                throw new RuntimeException(
                        "Unrecognized node type: "
                                + nodeType
                                + ".  This attribute is optional or can have a value of element|fragment.");
            }
        }
    }    
    
    /**
     * Factory for creating a FactoryCreateRule
     */
    protected class FactoryCreateRuleFactory extends AbstractObjectCreationFactory {
        @Override
        public Object createObject(Attributes attributes) {
            String className = attributes.getValue("classname");
            String attrName = attributes.getValue("attrname");
            boolean ignoreExceptions = 
                "true".equalsIgnoreCase(attributes.getValue("ignore-exceptions"));
            return (attrName == null || attrName.length() == 0) ?
                new FactoryCreateRule( className, ignoreExceptions) :
                new FactoryCreateRule( className, attrName, ignoreExceptions);
        }
    }
    
    /**
     * Factory for creating a ObjectCreateRule
     */
    protected class ObjectCreateRuleFactory extends AbstractObjectCreationFactory {
        @Override
        public Object createObject(Attributes attributes) {
            String className = attributes.getValue("classname");
            String attrName = attributes.getValue("attrname");
            return (attrName == null || attrName.length() == 0) ?
                new ObjectCreateRule( className) :
                new ObjectCreateRule( className, attrName);
        }
    }
    
    /**
     * Factory for creating a SetPropertiesRule
     */
    protected class SetPropertiesRuleFactory extends AbstractObjectCreationFactory {
        @Override
        public Object createObject(Attributes attributes) {
                return new SetPropertiesRule();
        }
    }
    
    /**
     * Factory for creating a SetPropertyRule
     */
    protected class SetPropertyRuleFactory extends AbstractObjectCreationFactory {
        @Override
        public Object createObject(Attributes attributes) {
            String name = attributes.getValue("name");
            String value = attributes.getValue("value");
            return new SetPropertyRule( name, value);
        }
    }
    
    /**
     * Factory for creating a SetNestedPropertiesRule
     */
    protected class SetNestedPropertiesRuleFactory extends AbstractObjectCreationFactory {
        @Override
        public Object createObject(Attributes attributes) {
           boolean allowUnknownChildElements = 
                "true".equalsIgnoreCase(attributes.getValue("allow-unknown-child-elements"));
                SetNestedPropertiesRule snpr = new SetNestedPropertiesRule();
                snpr.setAllowUnknownChildElements( allowUnknownChildElements );
                return snpr;
        }
    }
    
    /**
     * Factory for creating a SetTopRuleFactory
     */
    protected class SetTopRuleFactory extends AbstractObjectCreationFactory {
        @Override
        public Object createObject(Attributes attributes) {
            String methodName = attributes.getValue("methodname");
            String paramType = attributes.getValue("paramtype");
            return (paramType == null || paramType.length() == 0) ?
                new SetTopRule( methodName) :
                new SetTopRule( methodName, paramType);
        }
    }
    
    /**
     * Factory for creating a SetNextRuleFactory
     */
    protected class SetNextRuleFactory extends AbstractObjectCreationFactory {
        @Override
        public Object createObject(Attributes attributes) {
            String methodName = attributes.getValue("methodname");
            String paramType = attributes.getValue("paramtype");
            return (paramType == null || paramType.length() == 0) ?
                new SetNextRule( methodName) :
                new SetNextRule( methodName, paramType);
        }
    }
    
    /**
     * Factory for creating a SetRootRuleFactory
     */
    protected class SetRootRuleFactory extends AbstractObjectCreationFactory {
        @Override
        public Object createObject(Attributes attributes) {
            String methodName = attributes.getValue("methodname");
            String paramType = attributes.getValue("paramtype");
            return (paramType == null || paramType.length() == 0) ?
                new SetRootRule( methodName) :
                new SetRootRule( methodName, paramType);
        }
    }
    
    /**
     * A rule for adding a attribute-property alias to the custom alias mappings of
     * the containing SetPropertiesRule rule.
     */
    protected class SetPropertiesAliasRule extends Rule {
        
        /**
         * <p>Base constructor.</p>
         */
        public SetPropertiesAliasRule() {
            super();
        }
        
        /**
         * Add the alias to the SetPropertiesRule object created by the
         * enclosing <set-properties-rule> tag.
         */
        @Override
        public void begin(Attributes attributes) {
            String attrName = attributes.getValue("attr-name");
            String propName = attributes.getValue("prop-name");
    
            SetPropertiesRule rule = (SetPropertiesRule) digester.peek();
            rule.addAlias(attrName, propName);
        }
    }

    /**
     * A rule for adding a attribute-property alias to the custom alias mappings of
     * the containing SetNestedPropertiesRule rule.
     */
    protected class SetNestedPropertiesAliasRule extends Rule {
        
        /**
         * <p>Base constructor.</p>
         */
        public SetNestedPropertiesAliasRule() {
            super();
        }
        
        /**
         * Add the alias to the SetNestedPropertiesRule object created by the
         * enclosing <set-nested-properties-rule> tag.
         */
        @Override
        public void begin(Attributes attributes) {
            String attrName = attributes.getValue("attr-name");
            String propName = attributes.getValue("prop-name");
    
            SetNestedPropertiesRule rule = (SetNestedPropertiesRule) digester.peek();
            rule.addAlias(attrName, propName);
        }
    }
        
}
