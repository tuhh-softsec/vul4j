/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//digester/src/java/org/apache/commons/digester/Digester.java,v 1.3 2001/05/22 04:06:27 craigmcc Exp $
 * $Revision: 1.3 $
 * $Date: 2001/05/22 04:06:27 $
 *
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


package org.apache.commons.digester;


import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.collections.ArrayStack;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


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
 * @author Craig McClanahan
 * @version $Revision: 1.3 $ $Date: 2001/05/22 04:06:27 $
 */

public class Digester extends DefaultHandler {


    // --------------------------------------------------------- Constructors


    /**
     * Construct a new Digester with default properties.
     */
    public Digester() {

	super();

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
     * The debugging detail level of this component.
     */
    protected int debug = 0;


    /**
     * The URLs of DTDs that have been registered, keyed by the public
     * identifier that corresponds.
     */
    protected HashMap dtds = new HashMap();


    /**
     * The application-supplied error handler that is notified when parsing
     * warnings, errors, or fatal errors occur.
     */
    protected ErrorHandler errorHandler = null;


    /**
     * The SAXParserFactory that is created the first time we need it.
     */
    protected static SAXParserFactory factory = null;


    /**
     * The Locator associated with our parser.
     */
    protected Locator locator = null;


    /**
     * The current match pattern for nested element processing.
     */
    protected String match = "";


    /**
     * Do we want a "namespace aware" parser?
     */
    protected boolean namespaceAware = false;


    /**
     * The SAXParser we will use to parse the input stream.
     */
    protected SAXParser parser = null;


    /**
     * The "root" element of the stack (in other words, the last object
     * that was popped.
     */
    protected Object root = null;


    /**
     * The set of Rules that have been registered with this Digester.  The
     * key is the matching pattern against the current element stack, and
     * the value is a List containing the Rules for that pattern, in the
     * order that they were registered.
     */
    protected HashMap rules = new HashMap();


    /**
     * The object stack being constructed.
     */
    protected ArrayStack stack = new ArrayStack();


    /**
     * Do we want to use a validating parser?
     */
    protected boolean validating = false;


    // ----------------------------------------------------------- Properties


    /**
     * Return the current depth of the element stack.
     */
    public int getCount() {

	return (stack.size());

    }


    /**
     * Return the debugging detail level of this Digester.
     */
    public int getDebug() {

	return (this.debug);

    }


    /**
     * Set the debugging detail level of this Digester.
     *
     * @param debug The new debugging detail level
     */
    public void setDebug(int debug) {

	this.debug = debug;

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
     * Return the SAXParser we will use to parse the input stream.  If there
     * is a problem creating the parser, return <code>null</code>.
     */
    public SAXParser getParser() {

	// Return the parser we already created (if any)
	if (parser != null)
	    return (parser);

	// Create and return a new parser
        synchronized (this) {
            try {
                if (factory == null)
                    factory = SAXParserFactory.newInstance();
                factory.setNamespaceAware(namespaceAware);
                factory.setValidating(validating);
                parser = factory.newSAXParser();
                return (parser);
            } catch (Exception e) {
                log("Digester.getParser: ", e);
                return (null);
            }
        }

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


    // ---------------------------------------------- DocumentHandler Methods


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

	//	if (debug >= 3)
	//	    log("characters(" + new String(buffer, start, length) + ")");

	bodyText.append(buffer, start, length);

    }


    /**
     * Process notification of the end of the document being reached.
     *
     * @exception SAXException if a parsing error is to be reported
     */
    public void endDocument() throws SAXException {

	//	if (debug >= 3)
	//	    log("endDocument()");

	if (getCount() > 1)
	    log("endDocument():  " + getCount() + " elements left");
	while (getCount() > 1)
	    pop();

	// Fire "finish" events for all defined rules
	Iterator keys = this.rules.keySet().iterator();
	while (keys.hasNext()) {
	    String key = (String) keys.next();
	    List rules = (List) this.rules.get(key);
	    for (int i = 0; i < rules.size(); i++) {
		try {
		    ((Rule) rules.get(i)).finish();
		} catch (Exception e) {
		    log("Finish event threw exception", e);
		    throw new SAXException(e);
		}
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

	//	if (debug >= 3)
	//	    log("endElement(" + match + ")");
	List rules = getRules(match);

	// Fire "body" events for all relevant rules
	if (rules != null) {
	    //	    if (debug >= 3)
	    //		log("  Firing 'body' events for " + rules.size() + " rules");
	    String bodyText = this.bodyText.toString();
	    for (int i = 0; i < rules.size(); i++) {
		try {
		    ((Rule) rules.get(i)).body(bodyText);
		} catch (Exception e) {
		    log("Body event threw exception", e);
		    throw new SAXException(e);
		}
	    }
	}

	// Recover the body text from the surrounding element
	bodyText = (StringBuffer) bodyTexts.pop();

	// Fire "end" events for all relevant rules in reverse order
	if (rules != null) {
	    //	    if (debug >= 3)
	    //		log("  Firing 'end' events for " + rules.size() + " rules");
	    for (int i = 0; i < rules.size(); i++) {
		int j = (rules.size() - i) - 1;
		try {
		    ((Rule) rules.get(j)).end();
		} catch (Exception e) {
		    log("End event threw exception", e);
		    throw new SAXException(e);
		}
	    }
	}

	// Recover the previous match expression
	int slash = match.lastIndexOf('/');
	if (slash >= 0)
	    match = match.substring(0, slash);
	else
	    match = "";

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

	//	if (debug >= 3)
	//	    log("ignorableWhitespace(" +
	//		new String(buffer, start, len) + ")");

	;	// No processing required

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

	//	if (debug >= 3)
	//	    log("processingInstruction('" + target + "', '" + data + "')");

	;	// No processing is required

    }


    /**
     * Set the document locator associated with our parser.
     *
     * @param locator The new locator
     */
    public void setDocumentLocator(Locator locator) {

	//	if (debug >= 3)
	//	    log("setDocumentLocator()");

	this.locator = locator;

    }


    /**
     * Process notification of the beginning of the document being reached.
     *
     * @exception SAXException if a parsing error is to be reported
     */
    public void startDocument() throws SAXException {

	//	if (debug >= 3)
	//	    log("startDocument()");

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

	// Save the body text accumulated for our surrounding element
	bodyTexts.push(bodyText);
	bodyText.setLength(0);

	// Compute the current matching rule
	if (match.length() > 0)
	    match += "/" + localName;
	else
	    match = localName;
	//	if (debug >= 3)
	//	    log("startElement(" + match + ")");


	// Fire "begin" events for all relevant rules
	List rules = getRules(match);
	if (rules != null) {
	    //	    if (debug >= 3)
	    //		log("  Firing 'begin' events for " + rules.size() + " rules");
	    String bodyText = this.bodyText.toString();
	    for (int i = 0; i < rules.size(); i++) {
		try {
		    ((Rule) rules.get(i)).begin(list);
		} catch (Exception e) {
		    log("Begin event threw exception", e);
		    throw new SAXException(e);
		}
	    }
	}

    }


    // --------------------------------------------------- DTDHandler Methods


    /**
     * Receive notification of a notation declaration event.
     *
     * @param name The notation name
     * @param publicId The public identifier (if any)
     * @param systemId The system identifier (if any)
     */
    public void notationDecl(String name, String publicId, String systemId) {

	if (debug >= 1)
	    log("notationDecl('" + name + "', '" + publicId + "', '" +
		systemId + "')");

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

	if (debug >= 1)
	    log("unparsedEntityDecl('" + name + "', '" + publicId + "', '" +
		systemId + "', '" + notation + "')");

    }


    // ----------------------------------------------- EntityResolver Methods


    /**
     * Resolve the requested external entity.
     *
     * @param publicId The public identifier of the entity being referenced
     * @param systemId The system identifier of the entity being referenced
     *
     * @exception SAXException if a parsing exception occurs
     */
    public InputSource resolveEntity(String publicId, String systemId)
	throws SAXException {

	if (debug >= 1)
	    log("resolveEntity('" + publicId + "', '" + systemId + "')");

	// Has this system identifier been registered?
	String dtdURL = null;
        if (publicId != null)
            dtdURL = (String) dtds.get(publicId);
	if (dtdURL == null) {
	    if (debug >= 1)
		log(" Not registered, use system identifier");
	    return (null);
	}

	// Return an input source to our alternative URL
	if (debug >= 1)
	    log(" Resolving to alternate DTD '" + dtdURL + "'");
        try {
            URL url = new URL(dtdURL);
            InputStream stream = url.openStream();
            return (new InputSource(stream));
        } catch (Exception e) {
            throw new SAXException(e);
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

	log("Parse Error at line " + exception.getLineNumber() +
	    " column " + exception.getColumnNumber() + ": " +
	    exception.getMessage(), exception);
        if (errorHandler != null)
            errorHandler.error(exception);

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

	log("Parse Fatal Error at line " + exception.getLineNumber() +
	    " column " + exception.getColumnNumber() + ": " +
	    exception.getMessage(), exception);
        if (errorHandler != null)
            errorHandler.fatalError(exception);

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

	log("Parse Warning at line " + exception.getLineNumber() +
	    " column " + exception.getColumnNumber() + ": " +
	    exception.getMessage(), exception);
        if (errorHandler != null)
            errorHandler.warning(exception);

    }


    // ------------------------------------------------------ Logging Methods


    /**
     * Log a message to the log writer associated with this context.
     *
     * @param message The message to be logged
     */
    public void log(String message) {

	System.out.println(message);

    }


    /**
     * Log a message and associated exception to the log writer
     * associated with this context.
     *
     * @param message The message to be logged
     * @param exception The associated exception to be logged
     */
    public void log(String message, Throwable exception) {

	System.out.println(message);
	exception.printStackTrace(System.out);

    }


    // ------------------------------------------------------- Public Methods


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

	getParser().parse(file, this);
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

	getParser().parse(input, this);
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

	getParser().parse(input, this);
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

	getParser().parse(uri, this);
	return (root);

    }


    /**
     * Register the specified DTD URL for the specified public identifier.
     * This must be called before the first call to <code>parse()</code>.
     *
     * @param publicId Public identifier of the DTD to be resolved
     * @param dtdURL The URL to use for reading this DTD
     */
    public void register(String publicId, String dtdURL) {

        if (debug >= 1)
            log("register('" + publicId + "', '" + dtdURL + "'");
	dtds.put(publicId, dtdURL);

    }


    // --------------------------------------------------------- Rule Methods


    /**
     * Register a new Rule matching the specified pattern.
     *
     * @param pattern Element matching pattern
     * @param rule Rule to be registered
     */
    public void addRule(String pattern, Rule rule) {

	List list = (List) rules.get(pattern);
	if (list == null) {
	    list = new ArrayList();
	    rules.put(pattern, list);
	}
	list.add(rule);

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
	        new CallMethodRule(this, methodName, paramCount));

    }


    /**
     * Add an "call method" rule for the specified parameters.
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
	        new CallMethodRule(this, methodName,
	        		   paramCount, paramTypes));

    }


    /**
     * Add an "call method" rule for the specified parameters.
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
	        new CallMethodRule(this, methodName,
	        		   paramCount, paramTypes));

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
	        new CallParamRule(this, paramIndex));

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
	        new CallParamRule(this, paramIndex, attributeName));

    }


    /**
     * Add an "object create" rule for the specified parameters.
     *
     * @param pattern Element matching pattern
     * @param className Java class name to be created
     */
    public void addObjectCreate(String pattern, String className) {

	addRule(pattern,
	        new ObjectCreateRule(this, className));

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
	        new ObjectCreateRule(this, className, attributeName));

    }


    /**
     * Add a "set next" rule for the specified parameters.
     *
     * @param pattern Element matching pattern
     * @param methodName Method name to call on the parent element
     */
    public void addSetNext(String pattern, String methodName) {

	addRule(pattern,
	        new SetNextRule(this, methodName));

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
	        new SetNextRule(this, methodName, paramType));

    }


    /**
     * Add a "set properties" rule for the specified parameters.
     *
     * @param pattern Element matching pattern
     */
    public void addSetProperties(String pattern) {

	addRule(pattern,
	        new SetPropertiesRule(this));

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
		new SetPropertyRule(this, name, value));

    }


    /**
     * Add a "set top" rule for the specified parameters.
     *
     * @param pattern Element matching pattern
     * @param methodName Method name to call on the parent element
     */
    public void addSetTop(String pattern, String methodName) {

	addRule(pattern,
	        new SetTopRule(this, methodName));

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
	        new SetTopRule(this, methodName, paramType));

    }


    // -------------------------------------------------------- Stack Methods


    /**
     * Clear the current contents of the object stack.
     */
    public void clear() {

	match = "";
        bodyTexts.clear();
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
	    return (null);
	}

    }


    /**
     * Push a new object onto the top of the object stack.
     *
     * @param object The new object
     */
    public void push(Object object) {

        if (stack.size() == 0)
            root = object;
	stack.push(object);

    }


    // -------------------------------------------------------- Package Methods


    /**
     * Return the set of DTD URL registrations, keyed by public identifier.
     */
    Map getRegistrations() {

        return (dtds);

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
     */
    List getRules(String match) {

        List rulesList = (List) this.rules.get(match);
	if (rulesList == null) {
            // Find the longest key, ie more discriminant
            String longKey = "";
	    Iterator keys = this.rules.keySet().iterator();
	    while (keys.hasNext()) {
	        String key = (String) keys.next();
		if (key.startsWith("*/")) {
		    if (match.endsWith(key.substring(1))) {
                        if (key.length() > longKey.length()) {
                            rulesList = (List) this.rules.get(key);
                            longKey = key;
                        }
		    }
		}
	    }
	}
	return (rulesList);

    }


}
