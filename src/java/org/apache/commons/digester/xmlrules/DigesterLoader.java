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


package org.apache.commons.digester.xmlrules;


import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSet;

import org.xml.sax.SAXException;
import org.xml.sax.InputSource;


/**
 * This class manages the creation of Digester instances from XML digester
 * rules files.
 *
 * @since 1.2
 */

public class DigesterLoader {

    /**
     * Creates a new digester and initializes it from the specified InputSource
     * @param rulesSource load the xml rules from this InputSource
     * @return a new Digester initialized with the rules
     */
    public static Digester createDigester(InputSource rulesSource) {
        RuleSet ruleSet = new FromXmlRuleSet(rulesSource);
        Digester digester = new Digester();
        digester.addRuleSet(ruleSet);
        return digester;
    }

    /**
     * Creates a new digester and initializes it from the specified InputSource.
     * This constructor allows the digester to be used to load the rules to be specified.
     * This allows properties to be configured on the Digester instance before it is used.
     *
     * @param rulesSource load the xml rules from this InputSource
     * @param rulesDigester digester to load the specified XML file.
     * @return a new Digester initialized with the rules
     */
    public static Digester createDigester(InputSource rulesSource, Digester rulesDigester) {
        RuleSet ruleSet = new FromXmlRuleSet(rulesSource, rulesDigester);
        Digester digester = new Digester();
        digester.addRuleSet(ruleSet);
        return digester;
    }

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
