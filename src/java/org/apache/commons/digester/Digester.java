/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//digester/src/java/org/apache/commons/digester/Digester.java,v 1.66 2002/08/20 02:51:46 patrickl Exp $
 * $Revision: 1.66 $
 * $Date: 2002/08/20 02:51:46 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
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


package org.apache.commons.digester;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.collections.ArrayStack;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;




/**
 * <p>A <strong>Digester</strong> processes an XML input stream by matching a
 * series of element nesting patterns to execute Rules that have been added
 * prior to the start of parsing.  This package was inspired by the
 * <code>XmlMapper</code> class that was part of Tomcat 3.0 and 3.1,
 * but is organized somewhat differently.</p>
 *
 * <p>See the <a href="package-summary.html#package_description">Digester
 * Developer Guide</a> for more information.</p>
 *
 * <p><strong>IMPLEMENTATION NOTE</strong> - A single Digester instance may
 * only be used within the context of a single thread at a time, and a call
 * to <code>parse()</code> must be completed before another can be initiated
 * even from the same thread.</p>
 *
 * <p><strong>IMPLEMENTATION NOTE</strong> - A bug in Xerces 2.0.2 prevents
 * the support of XML schema. You need Xerces 2.1 or JAXP 1.2.1 to make
 * that class working with XML schema</p>
 *
 * @author Craig McClanahan
 * @author Scott Sanders
 * @author Jean-Francois Arcand
 * @version $Revision: 1.66 $ $Date: 2002/08/20 02:51:46 $
 */

public class Digester extends DefaultHandler {


    // --------------------------------------------------------- Constructors


    /**
     * Construct a new Digester with default properties.
     */
    public Digester() {

        super();

    }


    /**
     * Construct a new Digester, allowing a SAXParser to be passed in.  This
     * allows Digester to be used in environments which are unfriendly to
     * JAXP1.1 (such as WebLogic 6.0).  Thanks for the request to change go to
     * James House (james@interobjective.com).  This may help in places where
     * you are able to load JAXP 1.1 classes yourself.
     */
    public Digester(SAXParser parser) {

        super();

        this.parser = parser;

    }


    /**
     * Construct a new Digester, allowing an XMLReader to be passed in.  This
     * allows Digester to be used in environments which are unfriendly to
     * JAXP1.1 (such as WebLogic 6.0).  Note that if you use this option you
     * have to configure namespace and validation support yourself, as these
     * properties only affect the SAXParser and emtpy constructor.
     */
    public Digester(XMLReader reader) {

        super();

        this.reader = reader;

    }


    // --------------------------------------------------- Instance Variables


    /**
     * The body text of the current element.
     */
    protected StringBuffer bodyText = new StringBuffer();


    /**
     * The stack of body text string buffers for surrounding elements.
     */
    protected ArrayStack bodyTexts = new ArrayStack();


    /**
     * The class loader to use for instantiating application objects.
     * If not specified, the context class loader, or the class loader
     * used to load Digester itself, is used, based on the value of the
     * <code>useContextClassLoader</code> variable.
     */
    protected ClassLoader classLoader = null;


    /**
     * Has this Digester been configured yet.
     */
    protected boolean configured = false;


    /**
     * The EntityResolver used by the SAX parser. By default it use this class
     */
    protected EntityResolver entityResolver;
    
    /**
     * The URLs of entityValidator that have been registered, keyed by the public
     * identifier that corresponds.
     */
    protected HashMap entityValidator = new HashMap();


    /**
     * The application-supplied error handler that is notified when parsing
     * warnings, errors, or fatal errors occur.
     */
    protected ErrorHandler errorHandler = null;


    /**
     * The SAXParserFactory that is created the first time we need it.
     */
    protected SAXParserFactory factory = null;


    /**
     * The JAXP 1.2 property required to set up the schema location.
     */
    private static final String JAXP_SCHEMA_SOURCE =
        "http://java.sun.com/xml/jaxp/properties/schemaSource";

    /**
     * The JAXP 1.2 property to set up the schemaLanguage used.
     */
    protected String JAXP_SCHEMA_LANGUAGE =
        "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

    
    /**
     * The Locator associated with our parser.
     */
    protected Locator locator = null;


    /**
     * The current match pattern for nested element processing.
     */
    protected String match = "";


    /**
     * Do we want a "namespace aware" parser.
     */
    protected boolean namespaceAware = false;


    /**
     * Registered namespaces we are currently processing.  The key is the
     * namespace prefix that was declared in the document.  The value is an
     * ArrayStack of the namespace URIs this prefix has been mapped to --
     * the top Stack element is the most current one.  (This architecture
     * is required because documents can declare nested uses of the same
     * prefix for different Namespace URIs).
     */
    protected HashMap namespaces = new HashMap();


    /**
     * The parameters stack being utilized by CallMethodRule and
     * CallParamRule rules.
     */
    protected ArrayStack params = new ArrayStack();


    /**
     * The SAXParser we will use to parse the input stream.
     */
    protected SAXParser parser = null;


    /**
     * The public identifier of the DTD we are currently parsing under
     * (if any).
     */
    protected String publicId = null;


    /**
     * The XMLReader used to parse digester rules.
     */
    protected XMLReader reader = null;


    /**
     * The "root" element of the stack (in other words, the last object
     * that was popped.
     */
    protected Object root = null;


    /**
     * The <code>Rules</code> implementation containing our collection of
     * <code>Rule</code> instances and associated matching policy.  If not
     * established before the first rule is added, a default implementation
     * will be provided.
     */
    protected Rules rules = null;

   /**
     * The XML schema language to use for validating an XML instance. By
     * default this value is set to <code>W3C_XML_SCHEMA</code>
     */
    protected String schemaLanguage = W3C_XML_SCHEMA;
    
        
    /**
     * The XML schema to use for validating an XML instance.
     */
    protected String schemaLocation = null;
    
    
    /**
     * The object stack being constructed.
     */
    protected ArrayStack stack = new ArrayStack();


    /**
     * Do we want to use the Context ClassLoader when loading classes
     * for instantiating new objects.  Default is <code>false</code>.
     */
    protected boolean useContextClassLoader = false;


    /**
     * Do we want to use a validating parser.
     */
    protected boolean validating = false;


    /**
     * The Log to which most logging calls will be made.
     */
    protected Log log =
        LogFactory.getLog("org.apache.commons.digester.Digester");


    /**
     * The Log to which all SAX event related logging calls will be made.
     */
    protected Log saxLog =
        LogFactory.getLog("org.apache.commons.digester.Digester.sax");
    
        
    /**
     * The schema language supported. By default, we use this one.
     */
    protected static final String W3C_XML_SCHEMA =
        "http://www.w3.org/2001/XMLSchema";
    

    
    // ------------------------------------------------------------- Properties

    /**
     * Return the currently mapped namespace URI for the specified prefix,
     * if any; otherwise return <code>null</code>.  These mappings come and
     * go dynamically as the document is parsed.
     *
     * @param prefix Prefix to look up
     */
    public String findNamespaceURI(String prefix) {
        
        ArrayStack stack = (ArrayStack) namespaces.get(prefix);
        if (stack == null) {
            return (null);
        }
        try {
            return ((String) stack.peek());
        } catch (EmptyStackException e) {
            return (null);
        }

    }


    /**
     * Return the class loader to be used for instantiating application objects
     * when required.  This is determined based upon the following rules:
     * <ul>
     * <li>The class loader set by <code>setClassLoader()</code>, if any</li>
     * <li>The thread context class loader, if it exists and the
     *     <code>useContextClassLoader</code> property is set to true</li>
     * <li>The class loader used to load the Digester class itself.
     * </ul>
     */
    public ClassLoader getClassLoader() {

        if (this.classLoader != null) {
            return (this.classLoader);
        }
        if (this.useContextClassLoader) {
            ClassLoader classLoader =
                    Thread.currentThread().getContextClassLoader();
            if (classLoader != null) {
                return (classLoader);
            }
        }
        return (this.getClass().getClassLoader());

    }


    /**
     * Set the class loader to be used for instantiating application objects
     * when required.
     *
     * @param classLoader The new class loader to use, or <code>null</code>
     *  to revert to the standard rules
     */
    public void setClassLoader(ClassLoader classLoader) {

        this.classLoader = classLoader;

    }


    /**
     * Return the current depth of the element stack.
     */
    public int getCount() {

        return (stack.size());

    }


    /**
     * Return the name of the XML element that is currently being processed.
     */
    public String getCurrentElementName() {

        String elementName = match;
        int lastSlash = elementName.lastIndexOf('/');
        if (lastSlash >= 0) {
            elementName = elementName.substring(lastSlash + 1);
        }
        return (elementName);

    }


    /**
     * Return the debugging detail level of our currently enabled logger.
     *
     * @deprecated Configure the logger using standard mechanisms
     *  for your implementation
     */
    public int getDebug() {

        return (0);

    }


    /**
     * Set the debugging detail level of our currently enabled logger.
     *
     * @param debug New debugging detail level (0=off, increasing integers
     *  for more detail)
     *
     * @deprecated Configure the logger using standard mechanisms
     *  for your implementation
     */
    public void setDebug(int debug) {

        ; // No action is taken

    }


    /**
     * Return the error handler for this Digester.
     */
    public ErrorHandler getErrorHandler() {

        return (this.errorHandler);

    }


    /**
     * Set the error handler for this Digester.
     *
     * @param errorHandler The new error handler
     */
    public void setErrorHandler(ErrorHandler errorHandler) {

        this.errorHandler = errorHandler;

    }


    /**
     * Return the SAXParserFactory we will use, creating one if necessary.
     */
    public SAXParserFactory getFactory() {

        if (factory == null) {
            factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(namespaceAware);
            factory.setValidating(validating);
        }
        return (factory);

    }


    /**
     * Returns a flag indicating whether the requested feature is supported
     * by the underlying implementation of <code>org.xml.sax.XMLReader</code>.
     * See <a href="http://www.saxproject.org/apidoc/xml/sax/package-summary.html#package-description"
     * http://www.saxproject.org/apidoc/xml/sax/package-summary.html#package-description</a>
     * for information about the standard SAX2 feature flags.
     *
     * @param feature Name of the feature to inquire about
     *
     * @exception ParserConfigurationException if a parser configuration error
     *  occurs
     * @exception SAXNotRecognizedException if the property name is
     *  not recognized
     * @exception SAXNotSupportedException if the property name is
     *  recognized but not supported
     */
    public boolean getFeature(String feature)
        throws ParserConfigurationException, SAXNotRecognizedException,
        SAXNotSupportedException {

        return (getFactory().getFeature(feature));

    }


    /**
     * Sets a flag indicating whether the requested feature is supported
     * by the underlying implementation of <code>org.xml.sax.XMLReader</code>.
     * See <a href="http://www.saxproject.org/apidoc/xml/sax/package-summary.html#package-description"
     * http://www.saxproject.org/apidoc/xml/sax/package-summary.html#package-description</a>
     * for information about the standard SAX2 feature flags.  In order to be
     * effective, this method must be called <strong>before</strong> the
     * <code>getParser()</code> method is called for the first time, either
     * directly or indirectly.
     *
     * @param feature Name of the feature to set the status for
     * @param value The new value for this feature
     *
     * @exception ParserConfigurationException if a parser configuration error
     *  occurs
     * @exception SAXNotRecognizedException if the property name is
     *  not recognized
     * @exception SAXNotSupportedException if the property name is
     *  recognized but not supported
     */
    public void setFeature(String feature, boolean value)
        throws ParserConfigurationException, SAXNotRecognizedException,
        SAXNotSupportedException {

        getFactory().setFeature(feature, value);

    }


    /**
     * Return the current Logger associated with this instance of the Digester
     */
    public Log getLogger() {

        return log;

    }


    /**
     * Set the current logger for this Digester.
     */
    public void setLogger(Log log) {

        this.log = log;

    }


    /**
     * Return the current rule match path
     */
    public String getMatch() {

        return match;

    }


    /**
     * Return the "namespace aware" flag for parsers we create.
     */
    public boolean getNamespaceAware() {

        return (this.namespaceAware);

    }


    /**
     * Set the "namespace aware" flag for parsers we create.
     *
     * @param namespaceAware The new "namespace aware" flag
     */
    public void setNamespaceAware(boolean namespaceAware) {

        this.namespaceAware = namespaceAware;

    }

    
    /**
     * Set the publid id of the current file being parse.
     * @param publicId the DTD/Schema public's id.
     */
    public void setPublicId(String publicId){
        this.publicId = publicId;
    }
    
    
    /**
     * Return the public identifier of the DTD we are currently
     * parsing under, if any.
     */
    public String getPublicId() {

        return (this.publicId);

    }


    /**
     * Return the namespace URI that will be applied to all subsequently
     * added <code>Rule</code> objects.
     */
    public String getRuleNamespaceURI() {

        return (getRules().getNamespaceURI());

    }


    /**
     * Set the namespace URI that will be applied to all subsequently
     * added <code>Rule</code> objects.
     *
     * @param ruleNamespaceURI Namespace URI that must match on all
     *  subsequently added rules, or <code>null</code> for matching
     *  regardless of the current namespace URI
     */
    public void setRuleNamespaceURI(String ruleNamespaceURI) {

        getRules().setNamespaceURI(ruleNamespaceURI);

    }


    /**
     * Return the SAXParser we will use to parse the input stream.  If there
     * is a problem creating the parser, return <code>null</code>.
     */
    public SAXParser getParser() {

        // Return the parser we already created (if any)
        if (parser != null) {
            return (parser);
        }

        // Create a new parser
        try {
            parser = getFactory().newSAXParser();         
        } catch (Exception e) {
            log.error("Digester.getParser: ", e);
            return (null);
        }

        // Configure standard properties and return the new instance
        try {
            if (schemaLocation != null) {
                setProperty(JAXP_SCHEMA_LANGUAGE, schemaLanguage);
                setProperty(JAXP_SCHEMA_SOURCE, schemaLocation);
            }
        } catch (Exception e) {
            log.warn("" + e);
        }
        return (parser);

    }


    /**
     * Return the current value of the specified property for the underlying
     * <code>XMLReader</code> implementation.
     * See <a href="http://www.saxproject.org/apidoc/xml/sax/package-summary.html#package-description"
     * http://www.saxproject.org/apidoc/xml/sax/package-summary.html#package-description</a>
     * for information about the standard SAX2 properties.
     *
     * @param property Property name to be retrieved
     *
     * @exception SAXNotRecognizedException if the property name is
     *  not recognized
     * @exception SAXNotSupportedException if the property name is
     *  recognized but not supported
     */
    public Object getProperty(String property)
        throws SAXNotRecognizedException, SAXNotSupportedException {

        return (getParser().getProperty(property));

    }


    /**
     * Set the current value of the specified property for the underlying
     * <code>XMLReader</code> implementation.
     * See <a href="http://www.saxproject.org/apidoc/xml/sax/package-summary.html#package-description"
     * http://www.saxproject.org/apidoc/xml/sax/package-summary.html#package-description</a>
     * for information about the standard SAX2 properties.
     *
     * @param property Property name to be set
     * @param value Property value to be set
     *
     * @exception SAXNotRecognizedException if the property name is
     *  not recognized
     * @exception SAXNotSupportedException if the property name is
     *  recognized but not supported
     */
    public void setProperty(String property, Object value)
        throws SAXNotRecognizedException, SAXNotSupportedException {

        getParser().setProperty(property, value);

    }


    /**
     * By setting the reader in the constructor, you can bypass JAXP and
     * be able to use digester in Weblogic 6.0.  
     *
     * @deprecated Use getXMLReader() instead, which can throw a
     *  SAXException if the reader cannot be instantiated
     */
    public XMLReader getReader() {

        try {
            return (getXMLReader());
        } catch (SAXException e) {
            log.error("Cannot get XMLReader", e);
            return (null);
        }

    }


    /**
     * Return the <code>Rules</code> implementation object containing our
     * rules collection and associated matching policy.  If none has been
     * established, a default implementation will be created and returned.
     */
    public Rules getRules() {

        if (this.rules == null) {
            this.rules = new RulesBase();
            this.rules.setDigester(this);
        }
        return (this.rules);

    }

    
    /**
     * Set the <code>Rules</code> implementation object containing our
     * rules collection and associated matching policy.
     *
     * @param rules New Rules implementation
     */
    public void setRules(Rules rules) {

        this.rules = rules;
        this.rules.setDigester(this);

    }


    /**
     * Return the XML Schema URI used for validating an XML instance.
     */
    public String getSchema() {

        return (this.schemaLocation);

    }


    /**
     * Set the XML Schema URI used for validating a XML Instance.
     *
     * @param schemaLocation a URI to the schema.
     */
    public void setSchema(String schemaLocation){

        this.schemaLocation = schemaLocation;

    }   
    

    /**
     * Return the XML Schema language used when parsing.
     */
    public String getSchemaLanguage() {

        return (this.schemaLanguage);

    }


    /**
     * Set the XML Schema language used when parsing. By default, we use W3C.
     *
     * @param schemaLanguage a URI to the schema language.
     */
    public void setSchemaLanguage(String schemaLanguage){

        this.schemaLanguage = schemaLanguage;

    }   


    /**
     * Return the boolean as to whether the context classloader should be used.
     */
    public boolean getUseContextClassLoader() {

        return useContextClassLoader;

    }


    /**
     * Determine whether to use the Context ClassLoader (the one found by
     * calling <code>Thread.currentThread().getContextClassLoader()</code>)
     * to resolve/load classes that are defined in various rules.  If not
     * using Context ClassLoader, then the class-loading defaults to
     * using the calling-class' ClassLoader.
     *
     * @param boolean determines whether to use Context ClassLoader.
     */
    public void setUseContextClassLoader(boolean use) {

        useContextClassLoader = use;

    }


    /**
     * Return the validating parser flag.
     */
    public boolean getValidating() {

        return (this.validating);

    }


    /**
     * Set the validating parser flag.  This must be called before
     * <code>parse()</code> is called the first time.
     *
     * @param validating The new validating parser flag.
     */
    public void setValidating(boolean validating) {

        this.validating = validating;

    }


    /**
     * Return the XMLReader to be used for parsing the input document.
     *
     * FIX ME: there is a bug in JAXP/XERCES that prevent the use of a 
     * parser that contains a schema with a DTD.
     * @exception SAXException if no XMLReader can be instantiated
     */
    public XMLReader getXMLReader() throws SAXException {
        if (reader == null){
            reader = getParser().getXMLReader();
        }        
                               
        reader.setDTDHandler(this);           
        reader.setContentHandler(this);        
        
        if (entityResolver == null){
            reader.setEntityResolver(this);
        } else {
            reader.setEntityResolver(entityResolver);           
        }
        
        reader.setErrorHandler(this);
        return reader;
    }


    // ------------------------------------------------- ContentHandler Methods


    /**
     * Process notification of character data received from the body of
     * an XML element.
     *
     * @param buffer The characters from the XML document
     * @param start Starting offset into the buffer
     * @param length Number of characters from the buffer
     *
     * @exception SAXException if a parsing error is to be reported
     */
    public void characters(char buffer[], int start, int length)
            throws SAXException {

        if (saxLog.isDebugEnabled()) {
            saxLog.debug("characters(" + new String(buffer, start, length) + ")");
        }

        bodyText.append(buffer, start, length);

    }


    /**
     * Process notification of the end of the document being reached.
     *
     * @exception SAXException if a parsing error is to be reported
     */
    public void endDocument() throws SAXException {

        if (saxLog.isDebugEnabled()) {
            if (getCount() > 1) {
                saxLog.debug("endDocument():  " + getCount() +
                             " elements left");
            } else {
                saxLog.debug("endDocument()");
            }
        }

        while (getCount() > 1) {
            pop();
        }

        // Fire "finish" events for all defined rules
        Iterator rules = getRules().rules().iterator();
        while (rules.hasNext()) {
            Rule rule = (Rule) rules.next();
            try {
                rule.finish();
            } catch (Exception e) {
                log.error("Finish event threw exception", e);
                throw createSAXException(e);
            } catch (Error e) {
                log.error("Finish event threw error", e);
                throw e;
            }
        }

        // Perform final cleanup
        clear();

    }


    /**
     * Process notification of the end of an XML element being reached.
     *
     * @param uri - The Namespace URI, or the empty string if the
     *   element has no Namespace URI or if Namespace processing is not
     *   being performed.
     * @param localName - The local name (without prefix), or the empty
     *   string if Namespace processing is not being performed.
     * @param qName - The qualified XML 1.0 name (with prefix), or the
     *   empty string if qualified names are not available.
     * @exception SAXException if a parsing error is to be reported
     */
    public void endElement(String namespaceURI, String localName,
                           String qName) throws SAXException {

        boolean debug = log.isDebugEnabled();

        if (debug) {
            if (saxLog.isDebugEnabled()) {
                saxLog.debug("endElement(" + namespaceURI + "," + localName +
                        "," + qName + ")");
            }
            log.debug("  match='" + match + "'");
            log.debug("  bodyText='" + bodyText + "'");
        }

        // Fire "body" events for all relevant rules
        List rules = getRules().match(namespaceURI, match);
        if ((rules != null) && (rules.size() > 0)) {
            String bodyText = this.bodyText.toString();
            for (int i = 0; i < rules.size(); i++) {
                try {
                    Rule rule = (Rule) rules.get(i);
                    if (debug) {
                        log.debug("  Fire body() for " + rule);
                    }
                    rule.body(bodyText);
                } catch (Exception e) {
                    log.error("Body event threw exception", e);
                    throw createSAXException(e);
                } catch (Error e) {
                    log.error("Body event threw error", e);
                    throw e;
                }
            }
        } else {
            if (debug) {
                log.debug("  No rules found matching '" + match + "'.");
            }
        }

        // Recover the body text from the surrounding element
        bodyText = (StringBuffer) bodyTexts.pop();
        if (debug) {
            log.debug("  Popping body text '" + bodyText.toString() + "'");
        }

        // Fire "end" events for all relevant rules in reverse order
        if (rules != null) {
            for (int i = 0; i < rules.size(); i++) {
                int j = (rules.size() - i) - 1;
                try {
                    Rule rule = (Rule) rules.get(j);
                    if (debug) {
                        log.debug("  Fire end() for " + rule);
                    }
                    rule.end();
                } catch (Exception e) {
                    log.error("End event threw exception", e);
                    throw createSAXException(e);
                } catch (Error e) {
                    log.error("End event threw error", e);
                    throw e;
                }
            }
        }

        // Recover the previous match expression
        int slash = match.lastIndexOf('/');
        if (slash >= 0) {
            match = match.substring(0, slash);
        } else {
            match = "";
        }

    }


    /**
     * Process notification that a namespace prefix is going out of scope.
     *
     * @param prefix Prefix that is going out of scope
     *
     * @exception SAXException if a parsing error is to be reported
     */
    public void endPrefixMapping(String prefix) throws SAXException {

        if (saxLog.isDebugEnabled()) {
            saxLog.debug("endPrefixMapping(" + prefix + ")");
        }

        // Deregister this prefix mapping
        ArrayStack stack = (ArrayStack) namespaces.get(prefix);
        if (stack == null) {
            return;
        }
        try {
            stack.pop();
            if (stack.empty())
                namespaces.remove(prefix);
        } catch (EmptyStackException e) {
            throw createSAXException("endPrefixMapping popped too many times");
        }

    }


    /**
     * Process notification of ignorable whitespace received from the body of
     * an XML element.
     *
     * @param buffer The characters from the XML document
     * @param start Starting offset into the buffer
     * @param length Number of characters from the buffer
     *
     * @exception SAXException if a parsing error is to be reported
     */
    public void ignorableWhitespace(char buffer[], int start, int len)
            throws SAXException {

        if (saxLog.isDebugEnabled()) {
            saxLog.debug("ignorableWhitespace(" +
                    new String(buffer, start, len) + ")");
        }

        ;   // No processing required

    }


    /**
     * Process notification of a processing instruction that was encountered.
     *
     * @param target The processing instruction target
     * @param data The processing instruction data (if any)
     *
     * @exception SAXException if a parsing error is to be reported
     */
    public void processingInstruction(String target, String data)
            throws SAXException {

        if (saxLog.isDebugEnabled()) {
            saxLog.debug("processingInstruction('" + target + "','" + data + "')");
        }

        ;   // No processing is required

    }


    /**
     * Set the document locator associated with our parser.
     *
     * @param locator The new locator
     */
    public void setDocumentLocator(Locator locator) {

        if (saxLog.isDebugEnabled()) {
            saxLog.debug("setDocumentLocator(" + locator + ")");
        }

        this.locator = locator;

    }


    /**
     * Process notification of a skipped entity.
     *
     * @param name Name of the skipped entity
     *
     * @exception SAXException if a parsing error is to be reported
     */
    public void skippedEntity(String name) throws SAXException {

        if (saxLog.isDebugEnabled()) {
            saxLog.debug("skippedEntity(" + name + ")");
        }

        ; // No processing required

    }


    /**
     * Process notification of the beginning of the document being reached.
     *
     * @exception SAXException if a parsing error is to be reported
     */
    public void startDocument() throws SAXException {

        if (saxLog.isDebugEnabled()) {
            saxLog.debug("startDocument()");
        }

        // ensure that the digester is properly configured, as 
        // the digester could be used as a SAX ContentHandler
        // rather than via the parse() methods.
        configure();
    }


    /**
     * Process notification of the start of an XML element being reached.
     *
     * @param uri The Namespace URI, or the empty string if the element
     *   has no Namespace URI or if Namespace processing is not being performed.
     * @param localName The local name (without prefix), or the empty
     *   string if Namespace processing is not being performed.
     * @param qName The qualified name (with prefix), or the empty
     *   string if qualified names are not available.\
     * @param list The attributes attached to the element. If there are
     *   no attributes, it shall be an empty Attributes object. 
     * @exception SAXException if a parsing error is to be reported
     */
    public void startElement(String namespaceURI, String localName,
                             String qName, Attributes list)
            throws SAXException {
        boolean debug = log.isDebugEnabled();
        
        if (saxLog.isDebugEnabled()) {
            saxLog.debug("startElement(" + namespaceURI + "," + localName + "," +
                    qName + ")");
        }
        
        // Save the body text accumulated for our surrounding element
        bodyTexts.push(bodyText);
        if (debug) {
            log.debug("  Pushing body text '" + bodyText.toString() + "'");
        }
        bodyText = new StringBuffer();

        // Compute the current matching rule
        StringBuffer sb = new StringBuffer(match);
        if (match.length() > 0) {
            sb.append('/');
        }
        if ((localName == null) || (localName.length() < 1)) {
            sb.append(qName);
        } else {
            sb.append(localName);
        }
        match = sb.toString();
        if (debug) {
            log.debug("  New match='" + match + "'");
        }

        // Fire "begin" events for all relevant rules
        List rules = getRules().match(namespaceURI, match);
        if ((rules != null) && (rules.size() > 0)) {
            String bodyText = this.bodyText.toString();
            for (int i = 0; i < rules.size(); i++) {
                try {
                    Rule rule = (Rule) rules.get(i);
                    if (debug) {
                        log.debug("  Fire begin() for " + rule);
                    }
                    rule.begin(list);
                } catch (Exception e) {
                    log.error("Begin event threw exception", e);
                    throw createSAXException(e);
                } catch (Error e) {
                    log.error("Begin event threw error", e);
                    throw e;
                }
            }
        } else {
            if (debug) {
                log.debug("  No rules found matching '" + match + "'.");
            }
        }

    }


    /**
     * Process notification that a namespace prefix is coming in to scope.
     *
     * @param prefix Prefix that is being declared
     * @param namespaceURI Corresponding namespace URI being mapped to
     *
     * @exception SAXException if a parsing error is to be reported
     */
    public void startPrefixMapping(String prefix, String namespaceURI)
            throws SAXException {

        if (saxLog.isDebugEnabled()) {
            saxLog.debug("startPrefixMapping(" + prefix + "," + namespaceURI + ")");
        }

        // Register this prefix mapping
        ArrayStack stack = (ArrayStack) namespaces.get(prefix);
        if (stack == null) {
            stack = new ArrayStack();
            namespaces.put(prefix, stack);
        }
        stack.push(namespaceURI);

    }


    // ----------------------------------------------------- DTDHandler Methods


    /**
     * Receive notification of a notation declaration event.
     *
     * @param name The notation name
     * @param publicId The public identifier (if any)
     * @param systemId The system identifier (if any)
     */
    public void notationDecl(String name, String publicId, String systemId) {

        if (saxLog.isDebugEnabled()) {
            saxLog.debug("notationDecl(" + name + "," + publicId + "," +
                    systemId + ")");
        }

    }


    /**
     * Receive notification of an unparsed entity declaration event.
     *
     * @param name The unparsed entity name
     * @param publicId The public identifier (if any)
     * @param systemId The system identifier (if any)
     * @param notation The name of the associated notation
     */
    public void unparsedEntityDecl(String name, String publicId,
                                   String systemId, String notation) {

        if (saxLog.isDebugEnabled()) {
            saxLog.debug("unparsedEntityDecl(" + name + "," + publicId + "," +
                    systemId + "," + notation + ")");
        }

    }


    // ----------------------------------------------- EntityResolver Methods

    /**
     * Set the <code>EntityResolver</code> used by SAX when resolving
     * public id and system id.
     * This must be called before the first call to <code>parse()</code>.
     * @param entityResolver a class that implement the <code>EntityResolver</code> interface.
     */
    public void setEntityResolver(EntityResolver entityResolver){
        this.entityResolver = entityResolver;
    }
    
    
    /**
     * Return the Entity Resolver used by the SAX parser.
     * @return Return the Entity Resolver used by the SAX parser.
     */
    public EntityResolver getEntityResolver(){
        return entityResolver;
    }

    /**
     * Resolve the requested external entity.
     *
     * @param publicId The public identifier of the entity being referenced
     * @param systemId The system identifier of the entity being referenced
     *
     * @exception SAXException if a parsing exception occurs
     * <
     */
      public InputSource resolveEntity(String publicId, String systemId)
            throws SAXException {     
                
        boolean debug = log.isDebugEnabled();
        
        if (saxLog.isDebugEnabled()) {
            saxLog.debug("resolveEntity('" + publicId + "', '" + systemId + "')");
        }
        
        if (publicId != null)
            this.publicId = publicId;
                                       
        // Has this system identifier been registered?
        String entityURL = null;
        if (publicId != null) {
            entityURL = (String) entityValidator.get(publicId);
        }
         
        // Redirect the schema location to a local destination
        if (schemaLocation != null && entityURL == null && systemId != null){
            entityURL = (String)entityValidator.get(systemId);
        } 

        if (entityURL == null){ 
           return (null); 
        }
        
        // Return an input source to our alternative URL
        if (debug) {
            log.debug(" Resolving to alternate DTD '" + entityURL + "'");
        }  
        
        try {
            return (new InputSource(entityURL));
        } catch (Exception e) {
            throw createSAXException(e);
        }
    }


    // ------------------------------------------------- ErrorHandler Methods


    /**
     * Forward notification of a parsing error to the application supplied
     * error handler (if any).
     *
     * @param exception The error information
     *
     * @exception SAXException if a parsing exception occurs
     */
    public void error(SAXParseException exception) throws SAXException {

        log.error("Parse Error at line " + exception.getLineNumber() +
                " column " + exception.getColumnNumber() + ": " +
                exception.getMessage(), exception);
        if (errorHandler != null) {
            errorHandler.error(exception);
        }

    }


    /**
     * Forward notification of a fatal parsing error to the application
     * supplied error handler (if any).
     *
     * @param exception The fatal error information
     *
     * @exception SAXException if a parsing exception occurs
     */
    public void fatalError(SAXParseException exception) throws SAXException {

        log.error("Parse Fatal Error at line " + exception.getLineNumber() +
                " column " + exception.getColumnNumber() + ": " +
                exception.getMessage(), exception);
        if (errorHandler != null) {
            errorHandler.fatalError(exception);
        }

    }


    /**
     * Forward notification of a parse warning to the application supplied
     * error handler (if any).
     *
     * @param exception The warning information
     *
     * @exception SAXException if a parsing exception occurs
     */
    public void warning(SAXParseException exception) throws SAXException {
         if (errorHandler != null) {
            log.warn("Parse Warning Error at line " + exception.getLineNumber() +
                " column " + exception.getColumnNumber() + ": " +
                exception.getMessage(), exception);
            
            errorHandler.warning(exception);
        }

    }


    // ------------------------------------------------------- Public Methods


    /**
     * Log a message to our associated logger.
     *
     * @param message The message to be logged
     * @deprecated Call getLogger() and use it's logging methods
     */
    public void log(String message) {

        log.info(message);

    }


    /**
     * Log a message and exception to our associated logger.
     *
     * @param message The message to be logged
     * @deprecated Call getLogger() and use it's logging methods
     */
    public void log(String message, Throwable exception) {

        log.error(message, exception);

    }


    /**
     * Parse the content of the specified file using this Digester.  Returns
     * the root element from the object stack (if any).
     *
     * @param file File containing the XML data to be parsed
     *
     * @exception IOException if an input/output error occurs
     * @exception SAXException if a parsing exception occurs
     */
    public Object parse(File file) throws IOException, SAXException {

        configure();
        InputSource input = new InputSource(new FileInputStream(file));
        input.setSystemId("file://" + file.getAbsolutePath());
        getXMLReader().parse(input);
        return (root);

    }   
    /**
     * Parse the content of the specified input source using this Digester.
     * Returns the root element from the object stack (if any).
     *
     * @param input Input source containing the XML data to be parsed
     *
     * @exception IOException if an input/output error occurs
     * @exception SAXException if a parsing exception occurs
     */
    public Object parse(InputSource input) throws IOException, SAXException {
 
        configure();
        getXMLReader().parse(input);
        return (root);

    }


    /**
     * Parse the content of the specified input stream using this Digester.
     * Returns the root element from the object stack (if any).
     *
     * @param input Input stream containing the XML data to be parsed
     *
     * @exception IOException if an input/output error occurs
     * @exception SAXException if a parsing exception occurs
     */
    public Object parse(InputStream input) throws IOException, SAXException {

        configure();
        InputSource is = new InputSource(input);
        getXMLReader().parse(is);
        return (root);

    }


    /**
     * Parse the content of the specified reader using this Digester.
     * Returns the root element from the object stack (if any).
     *
     * @param reader Reader containing the XML data to be parsed
     *
     * @exception IOException if an input/output error occurs
     * @exception SAXException if a parsing exception occurs
     */
    public Object parse(Reader reader) throws IOException, SAXException {

        configure();
        InputSource is = new InputSource(reader);
        getXMLReader().parse(is);
        return (root);

    }


    /**
     * Parse the content of the specified URI using this Digester.
     * Returns the root element from the object stack (if any).
     *
     * @param uri URI containing the XML data to be parsed
     *
     * @exception IOException if an input/output error occurs
     * @exception SAXException if a parsing exception occurs
     */
    public Object parse(String uri) throws IOException, SAXException {

        configure();
        InputSource is = new InputSource(uri);
        getXMLReader().parse(is);
        return (root);

    }


    /**
     * Register the specified DTD URL for the specified public identifier.
     * This must be called before the first call to <code>parse()</code>.
     *
     * @param publicId Public identifier of the DTD to be resolved
     * @param entityURL The URL to use for reading this DTD
     */
    public void register(String publicId, String entityURL) {

        if (log.isDebugEnabled()) {
            log.debug("register('" + publicId + "', '" + entityURL + "'");
        }
        entityValidator.put(publicId, entityURL);

    }


    // --------------------------------------------------------- Rule Methods


    /**
     * <p>Register a new Rule matching the specified pattern.
     * This method sets the <code>Digester</code> property on the rule.</p>
     *
     * @param pattern Element matching pattern
     * @param rule Rule to be registered
     */
    public void addRule(String pattern, Rule rule) {

        rule.setDigester(this);
        getRules().add(pattern, rule);

    }


    /**
     * Register a set of Rule instances defined in a RuleSet.
     *
     * @param ruleSet The RuleSet instance to configure from
     */
    public void addRuleSet(RuleSet ruleSet) {

        String oldNamespaceURI = getRuleNamespaceURI();
        String newNamespaceURI = ruleSet.getNamespaceURI();
        if (log.isDebugEnabled()) {
            if (newNamespaceURI == null) {
                log.debug("addRuleSet() with no namespace URI");
            } else {
                log.debug("addRuleSet() with namespace URI " + newNamespaceURI);
            }
        }
        setRuleNamespaceURI(newNamespaceURI);
        ruleSet.addRuleInstances(this);
        setRuleNamespaceURI(oldNamespaceURI);

    }


    /**
     * Add a "bean property setter" rule for the specified parameters.
     *
     * @param pattern Element matching pattern
     */
    public void addBeanPropertySetter(String pattern) {

        addRule(pattern,
                new BeanPropertySetterRule());

    }


    /**
     * Add a "bean property setter" rule for the specified parameters.
     *
     * @param pattern Element matching pattern
     * @param propertyName Name of property to set
     */
    public void addBeanPropertySetter(String pattern,
                                      String propertyName) {

        addRule(pattern,
                new BeanPropertySetterRule(propertyName));

    }

    /**
     * Add an "call method" rule for a method which accepts no arguments.
     *
     * @param pattern Element matching pattern
     * @param methodName Method name to be called
     */
    public void addCallMethod(String pattern, String methodName) {

        addRule(
                pattern,
                new CallMethodRule(methodName));

    }

    /**
     * Add an "call method" rule for the specified parameters.
     *
     * @param pattern Element matching pattern
     * @param methodName Method name to be called
     * @param paramCount Number of expected parameters (or zero
     *  for a single parameter from the body of this element)
     */
    public void addCallMethod(String pattern, String methodName,
                              int paramCount) {

        addRule(pattern,
                new CallMethodRule(methodName, paramCount));

    }


    /**
     * Add an "call method" rule for the specified parameters.
     * If <code>paramCount</code> is set to zero the rule will use
     * the body of the matched element as the single argument of the
     * method, unless <code>paramTypes</code> is null or empty, in this
     * case the rule will call the specified method with no arguments.
     *
     * @param pattern Element matching pattern
     * @param methodName Method name to be called
     * @param paramCount Number of expected parameters (or zero
     *  for a single parameter from the body of this element)
     * @param paramTypes Set of Java class names for the types
     *  of the expected parameters
     *  (if you wish to use a primitive type, specify the corresonding
     *  Java wrapper class instead, such as <code>java.lang.Boolean</code>
     *  for a <code>boolean</code> parameter)
     */
    public void addCallMethod(String pattern, String methodName,
                              int paramCount, String paramTypes[]) {

        addRule(pattern,
                new CallMethodRule(
                                    methodName,
                                    paramCount, 
                                    paramTypes));

    }


    /**
     * Add an "call method" rule for the specified parameters.
     * If <code>paramCount</code> is set to zero the rule will use
     * the body of the matched element as the single argument of the
     * method, unless <code>paramTypes</code> is null or empty, in this
     * case the rule will call the specified method with no arguments.
     *
     * @param pattern Element matching pattern
     * @param methodName Method name to be called
     * @param paramCount Number of expected parameters (or zero
     *  for a single parameter from the body of this element)
     * @param paramTypes The Java class names of the arguments
     *  (if you wish to use a primitive type, specify the corresonding
     *  Java wrapper class instead, such as <code>java.lang.Boolean</code>
     *  for a <code>boolean</code> parameter)
     */
    public void addCallMethod(String pattern, String methodName,
                              int paramCount, Class paramTypes[]) {

        addRule(pattern,
                new CallMethodRule(
                                    methodName,
                                    paramCount, 
                                    paramTypes));

    }


    /**
     * Add a "call parameter" rule for the specified parameters.
     *
     * @param pattern Element matching pattern
     * @param paramIndex Zero-relative parameter index to set
     *  (from the body of this element)
     */
    public void addCallParam(String pattern, int paramIndex) {

        addRule(pattern,
                new CallParamRule(paramIndex));

    }


    /**
     * Add a "call parameter" rule for the specified parameters.
     *
     * @param pattern Element matching pattern
     * @param paramIndex Zero-relative parameter index to set
     *  (from the specified attribute)
     * @param attributeName Attribute whose value is used as the
     *  parameter value
     */
    public void addCallParam(String pattern, int paramIndex,
                             String attributeName) {

        addRule(pattern,
                new CallParamRule(paramIndex, attributeName));

    }


    /**
     * Add a "factory create" rule for the specified parameters.
     *
     * @param pattern Element matching pattern
     * @param className Java class name of the object creation factory class
     */
    public void addFactoryCreate(String pattern, String className) {

        addRule(pattern,
                new FactoryCreateRule(className));

    }


    /**
     * Add a "factory create" rule for the specified parameters.
     *
     * @param pattern Element matching pattern
     * @param clazz Java class of the object creation factory class
     */
    public void addFactoryCreate(String pattern, Class clazz) {

        addRule(pattern,
                new FactoryCreateRule(clazz));

    }


    /**
     * Add a "factory create" rule for the specified parameters.
     *
     * @param pattern Element matching pattern
     * @param className Java class name of the object creation factory class
     * @param attributeName Attribute name which, if present, overrides the
     *  value specified by <code>className</code>
     */
    public void addFactoryCreate(String pattern, String className,
                                 String attributeName) {

        addRule(pattern,
                new FactoryCreateRule(className, attributeName));

    }


    /**
     * Add a "factory create" rule for the specified parameters.
     *
     * @param pattern Element matching pattern
     * @param clazz Java class of the object creation factory class
     * @param attributeName Attribute name which, if present, overrides the
     *  value specified by <code>className</code>
     */
    public void addFactoryCreate(String pattern, Class clazz,
                                 String attributeName) {

        addRule(pattern,
                new FactoryCreateRule(clazz, attributeName));

    }


    /**
     * Add a "factory create" rule for the specified parameters.
     *
     * @param pattern Element matching pattern
     * @param creationFactory Previously instantiated ObjectCreationFactory
     *  to be utilized
     */
    public void addFactoryCreate(String pattern,
                                 ObjectCreationFactory creationFactory) {

        creationFactory.setDigester(this);
        addRule(pattern,
                new FactoryCreateRule(creationFactory));

    }


    /**
     * Add an "object create" rule for the specified parameters.
     *
     * @param pattern Element matching pattern
     * @param className Java class name to be created
     */
    public void addObjectCreate(String pattern, String className) {

        addRule(pattern,
                new ObjectCreateRule(className));

    }


    /**
     * Add an "object create" rule for the specified parameters.
     *
     * @param pattern Element matching pattern
     * @param clazz Java class to be created
     */
    public void addObjectCreate(String pattern, Class clazz) {

        addRule(pattern,
                new ObjectCreateRule(clazz));

    }


    /**
     * Add an "object create" rule for the specified parameters.
     *
     * @param pattern Element matching pattern
     * @param className Default Java class name to be created
     * @param attributeName Attribute name that optionally overrides
     *  the default Java class name to be created
     */
    public void addObjectCreate(String pattern, String className,
                                String attributeName) {

        addRule(pattern,
                new ObjectCreateRule(className, attributeName));

    }


    /**
     * Add an "object create" rule for the specified parameters.
     *
     * @param pattern Element matching pattern
     * @param attributeName Attribute name that optionally overrides
     * @param clazz Default Java class to be created
     *  the default Java class name to be created
     */
    public void addObjectCreate(String pattern,
                                String attributeName,
                                Class clazz) {

        addRule(pattern,
                new ObjectCreateRule(attributeName, clazz));

    }


    /**
     * Add a "set next" rule for the specified parameters.
     *
     * @param pattern Element matching pattern
     * @param methodName Method name to call on the parent element
     */
    public void addSetNext(String pattern, String methodName) {

        addRule(pattern,
                new SetNextRule(methodName));

    }


    /**
     * Add a "set next" rule for the specified parameters.
     *
     * @param pattern Element matching pattern
     * @param methodName Method name to call on the parent element
     * @param paramType Java class name of the expected parameter type
     *  (if you wish to use a primitive type, specify the corresonding
     *  Java wrapper class instead, such as <code>java.lang.Boolean</code>
     *  for a <code>boolean</code> parameter)
     */
    public void addSetNext(String pattern, String methodName,
                           String paramType) {

        addRule(pattern,
                new SetNextRule(methodName, paramType));

    }


    /**
     * Add {@link SetRootRule} with the specified parameters.
     *
     * @param pattern Element matching pattern
     * @param methodName Method name to call on the root object
     */
    public void addSetRoot(String pattern, String methodName) {

        addRule(pattern,
                new SetRootRule(methodName));

    }


    /**
     * Add {@link SetRootRule} with the specified parameters.
     *
     * @param pattern Element matching pattern
     * @param methodName Method name to call on the root object
     * @param paramType Java class name of the expected parameter type
     */
    public void addSetRoot(String pattern, String methodName,
                           String paramType) {

        addRule(pattern,
                new SetRootRule(methodName, paramType));

    }

    /**
     * Add a "set properties" rule for the specified parameters.
     *
     * @param pattern Element matching pattern
     */
    public void addSetProperties(String pattern) {

        addRule(pattern,
                new SetPropertiesRule());

    }

    /**
     * Add a "set properties" rule with a single overridden parameter.
     * See {@link SetPropertiesRule#SetPropertiesRule(String attributeName, String propertyName)}
     *
     * @param pattern Element matching pattern
     * @param attributeName map this attribute
     * @param propertyNames to this property
     */
    public void addSetProperties(
                                String pattern, 
                                String attributeName,
                                String propertyName) {

        addRule(pattern,
                new SetPropertiesRule(attributeName, propertyName));

    }

    /**
     * Add a "set properties" rule with overridden parameters.
     * See {@link SetPropertiesRule#SetPropertiesRule(String [] attributeNames, String [] propertyNames)}
     *
     * @param pattern Element matching pattern
     * @param attributeNames names of attributes with custom mappings
     * @param propertyNames property names these attributes map to
     */
    public void addSetProperties(
                                String pattern, 
                                String [] attributeNames,
                                String [] propertyNames) {

        addRule(pattern,
                new SetPropertiesRule(attributeNames, propertyNames));

    }


    /**
     * Add a "set property" rule for the specified parameters.
     *
     * @param pattern Element matching pattern
     * @param name Attribute name containing the property name to be set
     * @param value Attribute name containing the property value to set
     */
    public void addSetProperty(String pattern, String name, String value) {

        addRule(pattern,
                new SetPropertyRule(name, value));

    }


    /**
     * Add a "set top" rule for the specified parameters.
     *
     * @param pattern Element matching pattern
     * @param methodName Method name to call on the parent element
     */
    public void addSetTop(String pattern, String methodName) {

        addRule(pattern,
                new SetTopRule(methodName));

    }


    /**
     * Add a "set top" rule for the specified parameters.
     *
     * @param pattern Element matching pattern
     * @param methodName Method name to call on the parent element
     * @param paramType Java class name of the expected parameter type
     *  (if you wish to use a primitive type, specify the corresonding
     *  Java wrapper class instead, such as <code>java.lang.Boolean</code>
     *  for a <code>boolean</code> parameter)
     */
    public void addSetTop(String pattern, String methodName,
                          String paramType) {

        addRule(pattern,
                new SetTopRule(methodName, paramType));

    }


    // --------------------------------------------------- Object Stack Methods


    /**
     * Clear the current contents of the object stack.
     */
    public void clear() {

        match = "";
        bodyTexts.clear();
        params.clear();
        publicId = null;
        stack.clear();
    }


    /**
     * Return the top object on the stack without removing it.  If there are
     * no objects on the stack, return <code>null</code>.
     */
    public Object peek() {

        try {
            return (stack.peek());
        } catch (EmptyStackException e) {
            log.warn("Empty stack (returning null)");
            return (null);
        }

    }


    /**
     * Return the n'th object down the stack, where 0 is the top element
     * and [getCount()-1] is the bottom element.  If the specified index
     * is out of range, return <code>null</code>.
     *
     * @param n Index of the desired element, where 0 is the top of the stack,
     *  1 is the next element down, and so on.
     */
    public Object peek(int n) {

        try {
            return (stack.peek(n));
        } catch (EmptyStackException e) {
            log.warn("Empty stack (returning null)");
            return (null);
        }

    }


    /**
     * Pop the top object off of the stack, and return it.  If there are
     * no objects on the stack, return <code>null</code>.
     */
    public Object pop() {

        try {
            return (stack.pop());
        } catch (EmptyStackException e) {
            log.warn("Empty stack (returning null)");
            return (null);
        }

    }


    /**
     * Push a new object onto the top of the object stack.
     *
     * @param object The new object
     */
    public void push(Object object) {

        if (stack.size() == 0) {
            root = object;
        }
        stack.push(object);

    }


    /**
     * When the Digester is being used as a SAXContentHandler, 
     * this method allows you to access the root object that has been
     * created after parsing.
     * 
     * @return the root object that has been created after parsing
     *  or null if the digester has not parsed any XML yet.
     */
    public Object getRoot() {
        return root;
    }
    

    // ------------------------------------------------ Parameter Stack Methods


    // ------------------------------------------------------ Protected Methods


    /**
     * Provide a hook for lazy configuration of this <code>Digester</code>
     * instance.  The default implementation does nothing, but subclasses
     * can override as needed.
     */
    protected void configure() {

        // Do not configure more than once
        if (configured) {
            return;
        }

        // Perform lazy configuration as needed
        ; // Nothing required by default

        // Set the configuration flag to avoid repeating
        configured = true;

    }


    // -------------------------------------------------------- Package Methods


    /**
     * Return the set of DTD URL registrations, keyed by public identifier.
     */
    Map getRegistrations() {

        return (entityValidator);

    }


    /**
     * Return the set of rules that apply to the specified match position.
     * The selected rules are those that match exactly, or those rules
     * that specify a suffix match and the tail of the rule matches the
     * current match position.  Exact matches have precedence over
     * suffix matches, then (among suffix matches) the longest match
     * is preferred.
     *
     * @param match The current match position
     *
     * @deprecated Call <code>match()</code> on the <code>Rules</code>
     *  implementation returned by <code>getRules()</code>
     */
    List getRules(String match) {

        return (getRules().match(match));

    }


    /**
     * <p>Return the top object on the parameters stack without removing it.  If there are
     * no objects on the stack, return <code>null</code>.</p>
     *
     * <p>The parameters stack is used to store <code>CallMethodRule</code> parameters. 
     * See {@link #params}.</p>
     */
    Object peekParams() {

        try {
            return (params.peek());
        } catch (EmptyStackException e) {
            log.warn("Empty stack (returning null)");
            return (null);
        }

    }


    /**
     * <p>Return the n'th object down the parameters stack, where 0 is the top element
     * and [getCount()-1] is the bottom element.  If the specified index
     * is out of range, return <code>null</code>.</p>
     *
     * <p>The parameters stack is used to store <code>CallMethodRule</code> parameters. 
     * See {@link #params}.</p>
     *
     * @param n Index of the desired element, where 0 is the top of the stack,
     *  1 is the next element down, and so on.
     */
    Object peekParams(int n) {

        try {
            return (params.peek(n));
        } catch (EmptyStackException e) {
            log.warn("Empty stack (returning null)");
            return (null);
        }

    }


    /**
     * <p>Pop the top object off of the parameters stack, and return it.  If there are
     * no objects on the stack, return <code>null</code>.</p>
     *
     * <p>The parameters stack is used to store <code>CallMethodRule</code> parameters. 
     * See {@link #params}.</p>
     */
    Object popParams() {

        try {
            if (log.isTraceEnabled()) {
                log.trace("Popping params");
            }
            return (params.pop());
        } catch (EmptyStackException e) {
            log.warn("Empty stack (returning null)");
            return (null);
        }

    }


    /**
     * <p>Push a new object onto the top of the parameters stack.</p>
     *
     * <p>The parameters stack is used to store <code>CallMethodRule</code> parameters. 
     * See {@link #params}.</p>
     *
     * @param object The new object
     */
    void pushParams(Object object) {
        if (log.isTraceEnabled()) {
            log.trace("Pushing params");
        }
        params.push(object);

    }

    /**
     * Create a SAX exception which also understands about the location in
     * the digester file where the exception occurs
     *
     * @return the new exception
     */
    protected SAXException createSAXException(String message, Exception e) {
        if (locator != null) {
            String error = "Error at (" + locator.getLineNumber() + ", "
                    + locator.getColumnNumber() + ": " + message;
            if (e != null) {
                return new SAXParseException(error, locator, e);
            } else {
                return new SAXParseException(error, locator);
            }
        }
        log.error("No Locator!");
        if (e != null) {
            return new SAXException(message, e);
        } else {
            return new SAXException(message);
        }
    }

    /**
     * Create a SAX exception which also understands about the location in
     * the digester file where the exception occurs
     *
     * @return the new exception
     */
    protected SAXException createSAXException(Exception e) {
        return createSAXException(e.getMessage(), e);
    }

    /**
     * Create a SAX exception which also understands about the location in
     * the digester file where the exception occurs
     *
     * @return the new exception
     */
    protected SAXException createSAXException(String message) {
        return createSAXException(message, null);
    }
    
}
