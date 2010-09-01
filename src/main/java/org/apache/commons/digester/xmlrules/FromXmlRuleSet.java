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


import java.net.URL;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;

import org.xml.sax.InputSource;

/**
 * A Digester rule set where the rules come from an XML file.
 *
 * @since 1.2
 */
public class FromXmlRuleSet extends RuleSetBase {

    public static final String DIGESTER_DTD_PATH = "org/apache/commons/digester/xmlrules/digester-rules.dtd";

    /**
     * The file containing the Digester rules, in XML.
     */
    private XMLRulesLoader rulesLoader;

    /**
     * The rule set for parsing the Digester rules
     */
    private DigesterRuleParser parser;

    /**
        * The digester for loading the rules xml.
        */
    private Digester rulesDigester;

    /**
     * Constructs a FromXmlRuleSet using the default DigesterRuleParser and
     * rulesDigester.
     * @param rulesXml the path to the XML document defining the Digester rules
     */
    public FromXmlRuleSet(URL rulesXml) {
        this(rulesXml, new DigesterRuleParser(), new Digester());
    }

    /**
     * Constructs a FromXmlRuleSet using the default DigesterRuleParser and
     * a ruleDigester for loading the rules xml.
     * @param rulesXml the path to the XML document defining the Digester rules
     * @param rulesDigester the digester to read the rules xml.
     */
    public FromXmlRuleSet(URL rulesXml, Digester rulesDigester) {
        this(rulesXml, new DigesterRuleParser(), rulesDigester);
    }

    /**
     * @param rulesXml the path to the XML document defining the Digester rules
     * @param parser an instance of DigesterRuleParser, for parsing the rules from XML
     */
    public FromXmlRuleSet(URL rulesXml, DigesterRuleParser parser) {
        this(rulesXml, parser, new Digester());
    }

    /**
     * @param rulesXml the path to the XML document defining the Digester rules
     * @param parser an instance of DigesterRuleParser, for parsing the rules from XML
     * @param rulesDigester the digester used to load the Xml rules.
     */
    public FromXmlRuleSet(URL rulesXml, DigesterRuleParser parser, Digester rulesDigester) {
        init(new URLXMLRulesLoader(rulesXml), parser, rulesDigester);
    }

    /**
     * Constructs a FromXmlRuleSet using the default DigesterRuleParser and
     * rulesDigester.
     * @param inputSource load the xml rules from this InputSource
     */
    public FromXmlRuleSet(InputSource inputSource) {
        this(inputSource, new DigesterRuleParser(), new Digester());
    }
    
    /**
     * Constructs a FromXmlRuleSet using the default DigesterRuleParser and
     * a ruleDigester for loading the rules xml.
     * @param inputSource load the xml rules from this InputSource
     * @param rulesDigester the digester to read the rules xml.
     */
    public FromXmlRuleSet(InputSource inputSource, Digester rulesDigester) {
        this(inputSource, new DigesterRuleParser(), rulesDigester);
    }

    /**
     * @param inputSource load the xml rules from this InputSource
     * @param parser an instance of DigesterRuleParser, for parsing the rules from XML
     */
    public FromXmlRuleSet(InputSource inputSource, DigesterRuleParser parser) {
        this(inputSource, parser, new Digester());
    }

    /**
     * @param inputSource load the xml rules from this InputSource
     * @param parser an instance of DigesterRuleParser, for parsing the rules from XML
     * @param rulesDigester the digester used to load the Xml rules.
     */
    public FromXmlRuleSet(InputSource inputSource, DigesterRuleParser parser, Digester rulesDigester) {
        init(new InputSourceXMLRulesLoader(inputSource), parser, rulesDigester);
    }
    
    /**
     * Base constructor
     */
    private void init(XMLRulesLoader rulesLoader, DigesterRuleParser parser, Digester rulesDigester) {
        this.rulesLoader = rulesLoader;
        this.parser = parser;
        this.rulesDigester = rulesDigester;
    }
    
    /**
     * Adds to the digester the set of Rule instances defined in the
     * XML file for this rule set.
     * @see org.apache.commons.digester.RuleSetBase
     */
    @Override
    public void addRuleInstances(org.apache.commons.digester.Digester digester) throws XmlLoadException {
        addRuleInstances(digester, null);
    }
    
    /**
     * Adds to the digester the set of Rule instances defined in the
     * XML file for this rule set.
     * <p>
     * Note that this method doesn't have a matching one on the DigesterLoader
     * class, because it is not expected to be widely used, and DigesterLoader's
     * load method is already heavily overloaded.
     *
     * @param digester is the digester that rules will be added to.
     * @param basePath is a path that will be prefixed to every
     * pattern string defined in the xmlrules input file.
     *
     * @see org.apache.commons.digester.RuleSetBase
     * @since 1.6
     */
    public void addRuleInstances(
    org.apache.commons.digester.Digester digester,
    String basePath) 
    throws XmlLoadException {
        
        URL dtdURL = getClass().getClassLoader().getResource(DIGESTER_DTD_PATH);
        if (dtdURL == null) {
            throw new XmlLoadException("Cannot find resource \"" +
                    DIGESTER_DTD_PATH + "\"");
        }
        parser.setDigesterRulesDTD(dtdURL.toString());
        parser.setTarget(digester);
        parser.setBasePath(basePath);

        rulesDigester.addRuleSet(parser);
        rulesDigester.push(parser);

        rulesLoader.loadRules();
    }
    
    /** 
     * Worker class encapsulates loading mechanisms.
     * Private until some reason is found to make it public.
     */
    private abstract static class XMLRulesLoader {
        /** Load rules now */
        public abstract void loadRules()  throws XmlLoadException;
    }
    
    /** Loads XMLRules from an URL */
    private class URLXMLRulesLoader extends XMLRulesLoader {
        private URL url;
        public URLXMLRulesLoader(URL url) {
            this.url = url;
        }
        
        @Override
        public void loadRules() throws XmlLoadException {
            try {
                rulesDigester.parse(url.openStream());
            } catch (Exception ex) {
                throw new XmlLoadException(ex);
            }
        }
    }

    /** Loads XMLRules from an InputSource */
    private class InputSourceXMLRulesLoader extends XMLRulesLoader {
        private InputSource inputSource;
        public InputSourceXMLRulesLoader(InputSource inputSource) {
            this.inputSource = inputSource;
        }
        
        @Override
        public void loadRules() throws XmlLoadException {
            try {
                rulesDigester.parse(inputSource);
            } catch (Exception ex) {
                throw new XmlLoadException(ex);
            }
        }
    }
}

