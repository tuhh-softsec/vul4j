/*
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
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


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A Digester rule set where the rules come from an XML file.
 *
 * @author David H. Martin - Initial Contribution
 * @author Scott Sanders   - Added ASL, removed external dependencies
 */
public class FromXmlRuleSet extends RuleSetBase {

    public static final String DIGESTER_DTD_PATH = "org/apache/commons/digester/xmlrules/digester-rules.dtd";

    /**
     * The file containing the Digester rules, in XML.
     */
    private URL xmlRules;

    /**
     * The rule set for parsing the Digester rules
     */
    private DigesterRuleParser parser;
    
    /**
     * Constructs a FromXmlRuleSet using the default DigesterRuleParser
     * @param rulesXml the path to the XML document defining the Digester rules
     */
    public FromXmlRuleSet(URL rulesXml) {
        this(rulesXml, new DigesterRuleParser());
    }

    /**
     * @param rulesXml the path to the XML document defining the Digester rules
     * @param parser an instance of DigesterRuleParser, for parsing the rules from XML
     */
    public FromXmlRuleSet(URL rulesXml, DigesterRuleParser parser) {
        xmlRules = rulesXml;
        this.parser = parser;
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
        
        Digester rulesDigester = new Digester();
        rulesDigester.addRuleSet(parser);
        rulesDigester.push(parser);

        try {
            rulesDigester.parse(xmlRules.openStream());
        } catch (Exception ex) {
            throw new XmlLoadException(ex);
        }
    }

}

