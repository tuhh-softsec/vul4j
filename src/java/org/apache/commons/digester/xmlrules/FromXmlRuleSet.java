/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//digester/src/java/org/apache/commons/digester/xmlrules/FromXmlRuleSet.java,v 1.11 2004/01/10 17:45:07 rdonkin Exp $
 * $Revision: 1.11 $
 * $Date: 2004/01/10 17:45:07 $
 *
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2004 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgement:  
 *       "This product includes software developed by the 
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "Apache", "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their names without prior 
 *    written permission of the Apache Software Foundation.
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


import java.net.URL;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;

import org.xml.sax.InputSource;

/**
 * A Digester rule set where the rules come from an XML file.
 *
 * @author David H. Martin - Initial Contribution
 * @author Scott Sanders   - Added ASL, removed external dependencies
 * @author Henri Chen - Added rulesDigester
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
    public void addRuleInstances(org.apache.commons.digester.Digester digester) throws XmlLoadException {
        URL dtdURL = getClass().getClassLoader().getResource(DIGESTER_DTD_PATH);
        if (dtdURL == null) {
            throw new XmlLoadException("Cannot find resource \"" +
                    DIGESTER_DTD_PATH + "\"");
        }
        parser.setDigesterRulesDTD(dtdURL.toString());
        parser.setTarget(digester);

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
        
        public void loadRules() throws XmlLoadException {
            try {
                rulesDigester.parse(inputSource);
            } catch (Exception ex) {
                throw new XmlLoadException(ex);
            }
        }
    }
}

