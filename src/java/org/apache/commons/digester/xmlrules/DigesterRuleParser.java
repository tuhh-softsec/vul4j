/*
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */


package org.apache.commons.digester.xmlrules;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.collections.ArrayStack;
import org.apache.commons.digester.AbstractObjectCreationFactory;
import org.apache.commons.digester.BeanPropertySetterRule;
import org.apache.commons.digester.CallMethodRule;
import org.apache.commons.digester.CallParamRule;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.FactoryCreateRule;
import org.apache.commons.digester.ObjectCreateRule;
import org.apache.commons.digester.Rule;
import org.apache.commons.digester.RuleSetBase;
import org.apache.commons.digester.Rules;
import org.apache.commons.digester.SetNextRule;
import org.apache.commons.digester.SetPropertiesRule;
import org.apache.commons.digester.SetPropertyRule;
import org.apache.commons.digester.SetTopRule;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


/**
 * This is a RuleSet that parses XML into Digester rules, and then
 * adds those rules to a 'target' Digester.
 *
 * @author David H. Martin - Initial Contribution
 * @author Scott Sanders   - Added ASL, removed external dependencies
 * @author Bradley M. Handy - Bean Property Setter Rule addition
 * 
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
    
    
    /**
     * A stack whose toString method returns a '/'-separated concatenation
     * of all the elements in the stack.
     */
    protected class PatternStack extends ArrayStack {
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
    protected PatternStack patternStack;
    
    /**
     * Used to detect circular includes
     */
    private Set includedFiles = new HashSet();
    
    /**
     * Constructs a DigesterRuleParser. This object will be inoperable
     * until the target digester is set, via <code>setTarget(Digester)</code>
     */
    public DigesterRuleParser() {
        patternStack = new PatternStack();
    }
    
    /**
     * Constructs a rule set for converting XML digester rule descriptions
     * into Rule objects, and adding them to the given Digester
     * @param targetDigester the Digester to add the rules to
     */
    public DigesterRuleParser(Digester targetDigester) {
        this.targetDigester = targetDigester;
        patternStack = new PatternStack();
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
                                PatternStack stack, Set includedFiles) {
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
        targetDigester.addRule(patternStack.toString(), rule);
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
        
        digester.addFactoryCreate("*/call-param-rule", new CallParamRuleFactory());
        digester.addRule("*/call-param-rule", new PatternRule("pattern"));
        digester.addSetNext("*/call-param-rule", "add", ruleClassName);
        
        digester.addFactoryCreate("*/factory-create-rule", new FactoryCreateRuleFactory());
        digester.addRule("*/factory-create-rule", new PatternRule("pattern"));
        digester.addSetNext("*/factory-create-rule", "add", ruleClassName);
        
        digester.addFactoryCreate("*/object-create-rule", new ObjectCreateRuleFactory());
        digester.addRule("*/object-create-rule", new PatternRule("pattern"));
        digester.addSetNext("*/object-create-rule", "add", ruleClassName);
        
        digester.addFactoryCreate("*/set-properties-rule", new SetPropertiesRuleFactory());
        digester.addRule("*/set-properties-rule", new PatternRule("pattern"));
        digester.addSetNext("*/set-properties-rule", "add", ruleClassName);
        
        digester.addRule("*/set-properties-rule/alias", new SetPropertiesAliasRule());
        
        digester.addFactoryCreate("*/set-property-rule", new SetPropertyRuleFactory());
        digester.addRule("*/set-property-rule", new PatternRule("pattern"));
        digester.addSetNext("*/set-property-rule", "add", ruleClassName);
        
        digester.addFactoryCreate("*/set-top-rule", new SetTopRuleFactory());
        digester.addRule("*/set-top-rule", new PatternRule("pattern"));
        digester.addSetNext("*/set-top-rule", "add", ruleClassName);
        
        digester.addFactoryCreate("*/set-next-rule", new SetNextRuleFactory());
        digester.addRule("*/set-next-rule", new PatternRule("pattern"));
        digester.addSetNext("*/set-next-rule", "add", ruleClassName);
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
         * @param digester the Digester used to parse the rules XML file
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
            URL fileURL = DigesterRuleParser.this.getClass().getClassLoader().getResource(fileName);
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
            
            Class cls = Class.forName(className);
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
            delegate.add(prefix + pattern, rule);
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
        public List match(String pattern) {
            return delegate.match(pattern);
        }
        
        /**
         * This method passes through to the underlying Rules object.
         */
        public List match(String namespaceURI, String pattern) {
            return delegate.match(namespaceURI, pattern);
        }
        
        /**
         * This method passes through to the underlying Rules object.
         */
        public List rules() {
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
        public Object createObject(Attributes attributes) {
            Rule callMethodRule = null;
            String methodName = attributes.getValue("methodname");
            if (attributes.getValue("paramcount") == null) {
                // call against empty method
                callMethodRule = new CallMethodRule(methodName);
            
            } else {
                int paramCount = Integer.parseInt(attributes.getValue("paramcount"));
                
                String paramTypesAttr = attributes.getValue("paramtypes");
                if (paramTypesAttr == null || paramTypesAttr.length() == 0) {
                    callMethodRule = new CallMethodRule(methodName, paramCount);
                } else {
                    // Process the comma separated list or paramTypes
                    // into an array of String class names
                    ArrayList paramTypes = new ArrayList();
                    StringTokenizer tokens = new StringTokenizer(paramTypesAttr, " \t\n\r,");
                    while (tokens.hasMoreTokens()) {
                            paramTypes.add(tokens.nextToken());
                    }
                    callMethodRule = new CallMethodRule( methodName,
                                                        paramCount,
                                                        (String[])paramTypes.toArray(new String[0]));
                }
            }
            return callMethodRule;
        }
    }
    
    /**
     * Factory for creating a CallParamRule.
     */
    protected class CallParamRuleFactory extends AbstractObjectCreationFactory {
    
        public Object createObject(Attributes attributes) {
            // create callparamrule
            int paramIndex = Integer.parseInt(attributes.getValue("paramnumber"));
            String attributeName = attributes.getValue("attrname");
            String fromStack = attributes.getValue("from-stack");
            Rule callParamRule = null;
            if (attributeName == null) {
                if (fromStack == null) {
                
                    callParamRule = new CallParamRule( paramIndex );
                
                } else {

                    callParamRule = new CallParamRule( paramIndex, Boolean.valueOf(fromStack).booleanValue());
                    
                }
            } else {
                if (fromStack == null) {
                    
                    callParamRule = new CallParamRule( paramIndex, attributeName );
                    
                    
                } else {
                    // specifying both from-stack and attribute name is not allowed
                    throw new RuntimeException("Attributes from-stack and attrname cannot both be present.");
                }
            }
            return callParamRule;
        }
    }
    
    /**
     * Factory for creating a FactoryCreateRule
     */
    protected class FactoryCreateRuleFactory extends AbstractObjectCreationFactory {
        public Object createObject(Attributes attributes) {
            String className = attributes.getValue("classname");
            String attrName = attributes.getValue("attrname");
            boolean ignoreExceptions = 
                "true".equalsIgnoreCase(attributes.getValue("ignore-exceptions"));
            return (attrName == null || attrName.length() == 0) ?
                new FactoryCreateRule( className, ignoreExceptions)
                :
                new FactoryCreateRule( className, attrName, ignoreExceptions);
        }
    }
    
    /**
     * Factory for creating a ObjectCreateRule
     */
    protected class ObjectCreateRuleFactory extends AbstractObjectCreationFactory {
        public Object createObject(Attributes attributes) {
            String className = attributes.getValue("classname");
            String attrName = attributes.getValue("attrname");
            return (attrName == null || attrName.length() == 0) ?
                new ObjectCreateRule( className)
                :
                new ObjectCreateRule( className, attrName);
        }
    }
    
    /**
     * Factory for creating a SetPropertiesRule
     */
    protected class SetPropertiesRuleFactory extends AbstractObjectCreationFactory {
        public Object createObject(Attributes attributes) {
                return new SetPropertiesRule();
        }
    }
    
    /**
     * Factory for creating a SetPropertyRule
     */
    protected class SetPropertyRuleFactory extends AbstractObjectCreationFactory {
        public Object createObject(Attributes attributes) {
            String name = attributes.getValue("name");
            String value = attributes.getValue("value");
            return new SetPropertyRule( name, value);
        }
    }
    
    /**
     * Factory for creating a SetTopRuleFactory
     */
    protected class SetTopRuleFactory extends AbstractObjectCreationFactory {
        public Object createObject(Attributes attributes) {
            String methodName = attributes.getValue("methodname");
            String paramType = attributes.getValue("paramtype");
            return (paramType == null || paramType.length() == 0) ?
                new SetTopRule( methodName)
                :
                new SetTopRule( methodName, paramType);
        }
    }
    
    /**
     * Factory for creating a SetNextRuleFactory
     */
    protected class SetNextRuleFactory extends AbstractObjectCreationFactory {
        public Object createObject(Attributes attributes) {
            String methodName = attributes.getValue("methodname");
            String paramType = attributes.getValue("paramtype");
            return (paramType == null || paramType.length() == 0) ?
                new SetNextRule( methodName)
                :
                new SetNextRule( methodName, paramType);
        }
    }
    
    /**
     * A rule for adding a attribute-property alias to the custom alias mappings of
     * the containing SetPropertiesRule rule.
     */
    protected class SetPropertiesAliasRule extends Rule {
        
        /**
         * <p>Base constructor.
         *
         * @param digester the Digester used to parse the rules XML file
         */
        public SetPropertiesAliasRule() {
            super();
        }
        
        /**
         * Add the alias to the SetPropertiesRule object created by the
         * enclosing <set-properties-rule> tag.
         */
        public void begin(Attributes attributes) {
            String attrName = attributes.getValue("attr-name");
            String propName = attributes.getValue("prop-name");
    
            SetPropertiesRule rule = (SetPropertiesRule) digester.peek();
            rule.addAlias(attrName, propName);
        }
    }
}
