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


import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSet;
import org.xml.sax.SAXException;


/**
 * This class manages the creation of Digester instances from XML digester
 * rules files.
 *
 * @author David H. Martin - Initial Contribution
 * @author Scott Sanders   - Added ASL, removed external dependencies
 * @author Henri Chen - Added rulesDigester
 */

public class DigesterLoader {

    /**
     * Creates a new digester and initializes it from the specified XML file
     * @param rulesXml URL to the XML file defining the digester rules
     * @return a new Digester initialized with the rules
     */
    public static Digester createDigester(URL rulesXml) {
        RuleSet ruleSet = new FromXmlRuleSet(rulesXml);
        Digester digester = new Digester();
        digester.addRuleSet(ruleSet);
        return digester;
    }

    /**
     * Creates a new digester and initializes it from the specified XML file.
     * This constructor allows specifing a rulesDigester to do the XML file
     * loading; thus no matter the XML files is packed into a jar, a war, or a
     * ear, the rulesDigester can always find the XML files with properly set
     * ClassLoader.
     *
     * @param rulesXml URL to the XML file defining the digester rules
     * @param rulesDigester digester to load the specified XML file.
     * @return a new Digester initialized with the rules
     */
    public static Digester createDigester(URL rulesXml, Digester rulesDigester) {
        RuleSet ruleSet = new FromXmlRuleSet(rulesXml, rulesDigester);
        Digester digester = new Digester();
        digester.addRuleSet(ruleSet);
        return digester;
    }

    /**
     * Given the digester rules XML file, a class loader, and an XML input file,
     * this method parses the input file into Java objects. The class loader
     * is used by the digester to create the Java objects.
     * @param digesterRules URL to the XML document defining the digester rules
     * @param classLoader the ClassLoader to register with the digester
     * @param fileURL URL to the XML file to parse into Java objects
     * @return an Object which is the root of the network of Java objects
     * created by digesting fileURL
     */
    public static Object load(URL digesterRules, ClassLoader classLoader,
                              URL fileURL) throws IOException, SAXException, DigesterLoadingException {
        return load(digesterRules, classLoader, fileURL.openStream());
    }

    /**
     * Given the digester rules XML file, a class loader, and an input stream,
     * this method parses the input into Java objects. The class loader
     * is used by the digester to create the Java objects.
     * @param digesterRules URL to the XML document defining the digester rules
     * @param classLoader the ClassLoader to register with the digester
     * @param input InputStream over the XML file to parse into Java objects
     * @return an Object which is the root of the network of Java objects
     * created by digesting fileURL
     */
    public static Object load(URL digesterRules, ClassLoader classLoader,
                              InputStream input) throws IOException, SAXException, DigesterLoadingException {
        Digester digester = createDigester(digesterRules);
        digester.setClassLoader(classLoader);
        try {
            return digester.parse(input);
        } catch (XmlLoadException ex) {
            // This is a runtime exception that can be thrown by
            // FromXmlRuleSet#addRuleInstances, which is called by the Digester
            // before it parses the file.
            throw new DigesterLoadingException(ex.getMessage(), ex);
        }
    }
    
    /**
     * Given the digester rules XML file, a class loader, and an input stream,
     * this method parses the input into Java objects. The class loader
     * is used by the digester to create the Java objects.
     * @param digesterRules URL to the XML document defining the digester rules
     * @param classLoader the ClassLoader to register with the digester
     * @param reader Reader over the XML file to parse into Java objects
     * @return an Object which is the root of the network of Java objects
     * created by digesting fileURL
     */
    public static Object load(
                                URL digesterRules, 
                                ClassLoader classLoader,
                                Reader reader) 
                                    throws 
                                        IOException, 
                                        SAXException, 
                                        DigesterLoadingException {
        Digester digester = createDigester(digesterRules);
        digester.setClassLoader(classLoader);
        try {
            return digester.parse(reader);
        } catch (XmlLoadException ex) {
            // This is a runtime exception that can be thrown by
            // FromXmlRuleSet#addRuleInstances, which is called by the Digester
            // before it parses the file.
            throw new DigesterLoadingException(ex.getMessage(), ex);
        }
    }


    /**
     * Given the digester rules XML file, a class loader, and an XML input file,
     * this method parses the input file into Java objects. The class loader
     * is used by the digester to create the Java objects.
     * @param digesterRules URL to the XML document defining the digester rules
     * @param classLoader the ClassLoader to register with the digester
     * @param fileURL URL to the XML file to parse into Java objects
     * @param rootObject an Object to push onto the digester's stack, prior
     * to parsing the input
     * @return an Object which is the root of the network of Java objects.
     * Usually, this will be the same object as rootObject
     * created by digesting fileURL
     */
    public static Object load(URL digesterRules, ClassLoader classLoader,
                              URL fileURL, Object rootObject) throws IOException, SAXException,
            DigesterLoadingException {
        return load(digesterRules, classLoader, fileURL.openStream(), rootObject);
    }

    /**
     * Given the digester rules XML file, a class loader, and an input stream,
     * this method parses the input into Java objects. The class loader
     * is used by the digester to create the Java objects.
     * @param digesterRules URL to the XML document defining the digester rules
     * @param classLoader the ClassLoader to register with the digester
     * @param input InputStream over the XML file to parse into Java objects
     * @param rootObject an Object to push onto the digester's stack, prior
     * to parsing the input
     * @return an Object which is the root of the network of Java objects
     * created by digesting fileURL
     */
    public static Object load(URL digesterRules, ClassLoader classLoader,
                              InputStream input, Object rootObject) throws IOException, SAXException,
            DigesterLoadingException {
        Digester digester = createDigester(digesterRules);
        digester.setClassLoader(classLoader);
        digester.push(rootObject);
        try {
            return digester.parse(input);
        } catch (XmlLoadException ex) {
            // This is a runtime exception that can be thrown by
            // FromXmlRuleSet#addRuleInstances, which is called by the Digester
            // before it parses the file.
            throw new DigesterLoadingException(ex.getMessage(), ex);
        }
    }
    
    /**
     * Given the digester rules XML file, a class loader, and an input stream,
     * this method parses the input into Java objects. The class loader
     * is used by the digester to create the Java objects.
     * @param digesterRules URL to the XML document defining the digester rules
     * @param classLoader the ClassLoader to register with the digester
     * @param input Reader over the XML file to parse into Java objects
     * @param rootObject an Object to push onto the digester's stack, prior
     * to parsing the input
     * @return an Object which is the root of the network of Java objects
     * created by digesting fileURL
     */
    public static Object load(
                                URL digesterRules, 
                                ClassLoader classLoader,
                                Reader input, 
                                Object rootObject) 
                                    throws 
                                        IOException, 
                                        SAXException,
                                        DigesterLoadingException {
        Digester digester = createDigester(digesterRules);
        digester.setClassLoader(classLoader);
        digester.push(rootObject);
        try {
            return digester.parse(input);
        } catch (XmlLoadException ex) {
            // This is a runtime exception that can be thrown by
            // FromXmlRuleSet#addRuleInstances, which is called by the Digester
            // before it parses the file.
            throw new DigesterLoadingException(ex.getMessage(), ex);
        }
    }
}
