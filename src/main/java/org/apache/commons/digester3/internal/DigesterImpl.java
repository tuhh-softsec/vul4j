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
package org.apache.commons.digester3.internal;

import static org.apache.commons.digester3.utils.InputSources.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.Rule;
import org.apache.commons.digester3.spi.Rules;
import org.apache.commons.digester3.spi.Substitutor;
import org.apache.commons.digester3.spi.TypeConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * {@link Digester} concrete implementation.
 */
public final class DigesterImpl implements Digester {

    /**
     * The Log to which most logging calls will be made.
     */
    private final Log log = LogFactory.getLog("org.apache.commons.digester3.Digester");

    /**
     * The Log to which all SAX event related logging calls will be made.
     */
    private final Log saxLog = LogFactory.getLog("org.apache.commons.digester3.Digester.sax");

    /**
     * Registered namespaces we are currently processing.  The key is the
     * namespace prefix that was declared in the document.  The value is an
     * Stack of the namespace URIs this prefix has been mapped to --
     * the top Stack element is the most current one.  (This architecture
     * is required because documents can declare nested uses of the same
     * prefix for different Namespace URIs).
     */
    private final HashMap<String, Stack<String>> namespaces = new HashMap<String, Stack<String>>();

    /**
     * The object stack being constructed.
     */
    private final Stack<Object> stack = new Stack<Object>();

    /**
     * The parameters stack being utilized by CallMethodRule and
     * CallParamRule rules.
     */
    private final Stack<Object> params = new Stack<Object>();

    /**
     * Stack whose elements are List objects, each containing a list of
     * Rule objects as returned from Rules.getMatch(). As each xml element
     * in the input is entered, the matching rules are pushed onto this
     * stack. After the end tag is reached, the matches are popped again.
     * The depth of is stack is therefore exactly the same as the current
     * "nesting" level of the input xml.
     */
    private final Stack<List<Rule>> matches = new Stack<List<Rule>>();

    /**
     * The stack of body text string buffers for surrounding elements.
     */
    private final Stack<StringBuilder> bodyTexts = new Stack<StringBuilder>();

    /**
     * Stacks used for interrule communication, indexed by name String
     */
    private final Map<String, Stack<Object>> stacksByName = new HashMap<String, Stack<Object>>();

    /**
     * <p><code>List</code> of <code>InputSource</code> instances
     * created by a <code>createInputSourceFromURL()</code> method
     * call.  These represent open input streams that need to be
     * closed to avoid resource leaks, as well as potentially locked
     * JAR files on Windows.</p>
     */
    private final List<InputSource> inputSources = new ArrayList<InputSource>();

    /**
     * The class loader to use for instantiating application objects.
     */
    private final ClassLoader classLoader;

    /**
     * An optional class that substitutes values in attributes and body text.
     * This may be null and so a null check is always required before use.
     */
    private final Substitutor substitutor;

    /**
     * The URLs of entityValidator that have been registered, keyed by the public
     * identifier that corresponds.
     */
    private final Map<String, URL> entityValidator;

    private final XMLReader reader;

    /**
     * The <code>Rules</code> containing our collection of
     * <code>Rule</code> instances and associated matching policy.
     */
    private Rules rules;

    /**
     * The Locator associated with our parser.
     */
    private Locator locator;

    // ---- internals

    /**
     * The body text of the current element.
     */
    private StringBuilder bodyText = new StringBuilder();

    /**
     * The current match pattern for nested element processing.
     */
    private String match = "";

    /**
     * The "root" element of the stack (in other words, the last object
     * that was popped.
     */
    private Object root = null;

    public DigesterImpl(XMLReader reader,
            Rules rules,
            ClassLoader classLoader,
            Substitutor substitutor,
            Map<String, URL> entityValidator) {
        this.reader = reader;
        this.reader.setDTDHandler(this);
        this.reader.setContentHandler(this);
        this.reader.setEntityResolver(this);
        this.reader.setErrorHandler(this);

        this.rules = rules;
        this.classLoader = classLoader;
        this.substitutor = substitutor;
        this.entityValidator = entityValidator;

        this.rules.setDigester(this);
    }

    /**
     * {@inheritDoc}
     */
    public Rules getRules() {
        return this.rules;
    }

    /**
     * {@inheritDoc}
     */
    public void setRules(Rules rules) {
        this.rules = rules;
        this.rules.setDigester(this);
    }

    /**
     * {@inheritDoc}
     */
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    /**
     * {@inheritDoc}
     */
    public Log getLog() {
        return this.log;
    }

    /**
     * {@inheritDoc}
     */
    public Log getSAXLog() {
        return this.saxLog;
    }

    /**
     * {@inheritDoc}
     */
    public String getMatch() {
        return this.match;
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        this.match = "";
        this.bodyTexts.clear();
        this.params.clear();
        // publicId = null;
        this.stack.clear();
        this.stacksByName.clear();
    }

    // ---- Stack methods

    /**
     * {@inheritDoc}
     */
    public int getCount() {
        return this.stack.size();
    }

    /**
     * {@inheritDoc}
     */
    public void push(Object object) {
        if (this.stack.size() == 0) {
            this.root = object;
        }
        this.stack.push(object);
    }

    /**
     * {@inheritDoc}
     */
    public void push(String stackName, Object value) {
        Stack<Object> namedStack = this.stacksByName.get(stackName);
        if (namedStack == null) {
            namedStack = new Stack<Object>();
            this.stacksByName.put(stackName, namedStack);
        }
        namedStack.push(value);
    }

    /**
     * {@inheritDoc}
     */
    public Object pop() {
        try {
            return this.stack.pop();
        } catch (EmptyStackException e) {
            this.log.warn("Empty stack (returning null)");
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object pop(String stackName) {
        Object result = null;

        Stack<Object> namedStack = stacksByName.get(stackName);
        if (namedStack == null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("Stack '" + stackName + "' is empty");
            }
            throw new EmptyStackException();
        }

        result = namedStack.pop();

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Object peek() {
        try {
            return this.stack.peek();
        } catch (EmptyStackException e) {
            this.log.warn("Empty stack (returning null)");
            return (null);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object peek(String stackName) {
        return this.peek(stackName, 0);
    }

    /**
     * {@inheritDoc}
     */
    public Object peek(int n) {
        int index = (this.stack.size() - 1) - n;
        if (index < 0) {
            this.log.warn("Empty stack (returning null)");
            return null;
        }
        try {
            return (this.stack.get(index));
        } catch (EmptyStackException e) {
            this.log.warn("Empty stack (returning null)");
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object peek(String stackName, int n) {
        Object result = null;
        Stack<Object> namedStack = this.stacksByName.get(stackName);
        if (namedStack == null ) {
            if (log.isDebugEnabled()) {
                log.debug("Stack '"
                        + stackName
                        + "' is empty");
            }
            throw new EmptyStackException();
        } else {
            int index = (namedStack.size() - 1) - n;
            if (index < 0) {
                throw new EmptyStackException();
            }
            result = namedStack.get(index);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty(String stackName) {
        boolean result = true;
        Stack<Object> namedStack = this.stacksByName.get(stackName);
        if (namedStack != null ) {
            result = namedStack.isEmpty();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Object getRoot() {
        return this.root;
    }

    /**
     * {@inheritDoc}
     */
    public void resetRoot() {
        this.root = null;
    }

    /**
     * {@inheritDoc}
     */
    public Object peekParams() {
        try {
            return this.params.peek();
        } catch (EmptyStackException e) {
            this.log.warn("Empty stack (returning null)");
            return (null);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object peekParams(int n) {
        int index = (this.params.size() - 1) - n;
        if (index < 0) {
            this.log.warn("Empty stack (returning null)");
            return (null);
        }
        try {
            return this.params.get(index);
        } catch (EmptyStackException e) {
            this.log.warn("Empty stack (returning null)");
            return (null);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void pushParams(Object object) {
        if (this.log.isTraceEnabled()) {
            this.log.trace("Pushing params");
        }
        this.params.push(object);
    }

    /**
     * {@inheritDoc}
     */
    public Object popParams() {
        try {
            if (this.log.isTraceEnabled()) {
                this.log.trace("Popping params");
            }
            return this.params.pop();
        } catch (EmptyStackException e) {
            this.log.warn("Empty stack (returning null)");
            return null;
        }
    }

    // ------ ContentHandler Methods

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
    public void characters(char buffer[], int start, int length) throws SAXException {
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug("characters(" + new String(buffer, start, length) + ")");
        }

        this.bodyText.append(buffer, start, length);
    }

    /**
     * Process notification of the end of the document being reached.
     *
     * @exception SAXException if a parsing error is to be reported
     */
    public void endDocument() throws SAXException {
        if (this.saxLog.isDebugEnabled()) {
            if (this.stack.size() > 1) {
                this.saxLog.debug("endDocument():  "
                        + this.stack.size()
                        + " elements left");
            } else {
                this.saxLog.debug("endDocument()");
            }
        }

        // Fire "finish" events for all defined rules
        for (Rule rule : this.rules.rules()) {
            try {
                rule.finish();
            } catch (Exception e) {
                this.log.error("Finish event threw exception", e);
                throw this.createSAXException(e);
            } catch (Error e) {
                this.log.error("Finish event threw error", e);
                throw e;
            }
        }

        // Perform final cleanup
        clear();
    }

    /**
     * Process notification of the end of an XML element being reached.
     *
     * @param namespaceURI - The Namespace URI, or the empty string if the
     *   element has no Namespace URI or if Namespace processing is not
     *   being performed.
     * @param localName - The local name (without prefix), or the empty
     *   string if Namespace processing is not being performed.
     * @param qName - The qualified XML 1.0 name (with prefix), or the
     *   empty string if qualified names are not available.
     * @exception SAXException if a parsing error is to be reported
     */
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        boolean debug = this.log.isDebugEnabled();

        if (debug) {
            if (this.saxLog.isDebugEnabled()) {
                this.saxLog.debug("endElement("
                        + namespaceURI
                        + ","
                        + localName
                        + ","
                        + qName
                        + ")");
            }
            this.log.debug("  match='"
                    + this.match
                    + "'");
            this.log.debug("  bodyText='"
                    + this.bodyText
                    + "'");
        }

        // the actual element name is either in localName or qName, depending 
        // on whether the parser is namespace aware
        String name = localName;
        if ((name == null) || (name.length() < 1)) {
            name = qName;
        }

        // Fire "body" events for all relevant rules
        List<Rule> rules = this.matches.pop();
        if ((rules != null) && (rules.size() > 0)) {
            String bodyText = this.bodyText.toString();
            Substitutor substitutor = this.substitutor;
            if (substitutor!= null) {
                bodyText = substitutor.substitute(bodyText);
            }
            for (int i = 0; i < rules.size(); i++) {
                try {
                    Rule rule = rules.get(i);
                    if (debug) {
                        this.log.debug("  Fire body() for " + rule);
                    }
                    rule.body(namespaceURI, name, bodyText);
                } catch (Exception e) {
                    this.log.error("Body event threw exception", e);
                    throw createSAXException(e);
                } catch (Error e) {
                    this.log.error("Body event threw error", e);
                    throw e;
                }
            }
        } else {
            if (debug) {
                this.log.debug("  No rules found matching '"
                        + this.match
                        + "'.");
            }
        }

        // Recover the body text from the surrounding element
        this.bodyText = this.bodyTexts.pop();
        if (debug) {
            this.log.debug("  Popping body text '"
                    + this.bodyText.toString()
                    + "'");
        }

        // Fire "end" events for all relevant rules in reverse order
        if (rules != null) {
            for (int i = 0; i < rules.size(); i++) {
                int j = (rules.size() - i) - 1;
                try {
                    Rule rule = rules.get(j);
                    if (debug) {
                        this.log.debug("  Fire end() for "
                                + rule);
                    }
                    rule.end(namespaceURI, name);
                } catch (Exception e) {
                    this.log.error("End event threw exception", e);
                    throw this.createSAXException(e);
                } catch (Error e) {
                    this.log.error("End event threw error", e);
                    throw e;
                }
            }
        }

        // Recover the previous match expression
        int slash = this.match.lastIndexOf('/');
        if (slash >= 0) {
            this.match = this.match.substring(0, slash);
        } else {
            this.match = "";
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
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug("endPrefixMapping("
                    + prefix
                    + ")");
        }

        // Deregister this prefix mapping
        Stack<String> stack = namespaces.get(prefix);
        if (stack == null) {
            return;
        }
        try {
            stack.pop();
            if (stack.empty()) {
                this.namespaces.remove(prefix);
            }
        } catch (EmptyStackException e) {
            throw this.createSAXException("endPrefixMapping popped too many times");
        }
    }

    /**
     * Process notification of ignorable whitespace received from the body of
     * an XML element.
     *
     * @param buffer The characters from the XML document
     * @param start Starting offset into the buffer
     * @param len Number of characters from the buffer
     *
     * @exception SAXException if a parsing error is to be reported
     */
    public void ignorableWhitespace(char buffer[], int start, int len) throws SAXException {
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug("ignorableWhitespace("
                    + new String(buffer, start, len)
                    + ")");
        }

        // No processing required
    }

    /**
     * Process notification of a processing instruction that was encountered.
     *
     * @param target The processing instruction target
     * @param data The processing instruction data (if any)
     *
     * @exception SAXException if a parsing error is to be reported
     */
    public void processingInstruction(String target, String data) throws SAXException {
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug("processingInstruction('"
                    + target
                    + "','"
                    + data
                    + "')");
        }

        // No processing is required
    }

    /**
     * Gets the document locator associated with our parser.
     *
     * @return the Locator supplied by the document parser
     */
    public Locator getDocumentLocator() {
        return locator;
    }

    /**
     * Sets the document locator associated with our parser.
     *
     * @param locator The new locator
     */
    public void setDocumentLocator(Locator locator) {
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug("setDocumentLocator("
                    + locator
                    + ")");
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
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug("skippedEntity("
                    + name
                    + ")");
        }

        // No processing required
    }

    /**
     * Process notification of the beginning of the document being reached.
     *
     * @exception SAXException if a parsing error is to be reported
     */
    public void startDocument() throws SAXException {
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug("startDocument()");
        }

        // No processing required
    }

    /**
     * Process notification of the start of an XML element being reached.
     *
     * @param namespaceURI The Namespace URI, or the empty string if the element
     *   has no Namespace URI or if Namespace processing is not being performed.
     * @param localName The local name (without prefix), or the empty
     *   string if Namespace processing is not being performed.
     * @param qName The qualified name (with prefix), or the empty
     *   string if qualified names are not available.\
     * @param list The attributes attached to the element. If there are
     *   no attributes, it shall be an empty Attributes object. 
     * @exception SAXException if a parsing error is to be reported
     */
    public void startElement(String namespaceURI, String localName, String qName, Attributes list) throws SAXException {
        boolean debug = this.log.isDebugEnabled();

        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug("startElement("
                    + namespaceURI
                    + ","
                    + localName
                    + ","
                    + qName
                    + ")");
        }

        // Save the body text accumulated for our surrounding element
        this.bodyTexts.push(this.bodyText);
        if (debug) {
            this.log.debug("  Pushing body text '"
                    + this.bodyText.toString()
                    + "'");
        }
        this.bodyText = new StringBuilder();

        // the actual element name is either in localName or qName, depending 
        // on whether the parser is namespace aware
        String name = localName;
        if (name == null || name.length() < 1) {
            name = qName;
        }

        // Compute the current matching rule
        StringBuilder sb = new StringBuilder(this.match);
        if (this.match.length() > 0) {
            sb.append('/');
        }
        sb.append(name);
        this.match = sb.toString();
        if (debug) {
            this.log.debug("  New match='"
                    + this.match
                    + "'");
        }

        // Fire "begin" events for all relevant rules
        List<Rule> rules = this.rules.match(namespaceURI, this.match);
        this.matches.push(rules);
        if (rules != null && !rules.isEmpty()) {
            Substitutor substitutor = this.substitutor;
            if (substitutor!= null) {
                list = substitutor.substitute(list);
            }
            for (int i = 0; i < rules.size(); i++) {
                try {
                    Rule rule = rules.get(i);
                    if (debug) {
                        this.log.debug("  Fire begin() for "
                                + rule);
                    }
                    rule.begin(namespaceURI, name, list);
                } catch (Exception e) {
                    this.log.error("Begin event threw exception", e);
                    throw this.createSAXException(e);
                } catch (Error e) {
                    this.log.error("Begin event threw error", e);
                    throw e;
                }
            }
        } else if (debug) {
            this.log.debug("  No rules found matching '"
                    + this.match
                    + "'.");
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
    public void startPrefixMapping(String prefix, String namespaceURI) throws SAXException {
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug("startPrefixMapping("
                    + prefix
                    + ","
                    + namespaceURI
                    + ")");
        }

        // Register this prefix mapping
        Stack<String> stack = this.namespaces.get(prefix);
        if (stack == null) {
            stack = new Stack<String>();
            this.namespaces.put(prefix, stack);
        }
        stack.push(namespaceURI);
    }

    // ---- DTDHandler Methods

    /**
     * Receive notification of a notation declaration event.
     *
     * @param name The notation name
     * @param publicId The public identifier (if any)
     * @param systemId The system identifier (if any)
     */
    public void notationDecl(String name, String publicId, String systemId) {
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug("notationDecl("
                    + name
                    + ","
                    + publicId
                    + ","
                    + systemId
                    + ")");
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
    public void unparsedEntityDecl(String name, String publicId, String systemId, String notation) {
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug("unparsedEntityDecl("
                    + name
                    + ","
                    + publicId
                    + ","
                    + systemId
                    + ","
                    + notation
                    + ")");
        }
    }

    // ---- EntityResolver Methods

    /**
     * Set the <code>EntityResolver</code> used by SAX when resolving
     * public id and system id.
     * This must be called before the first call to <code>parse()</code>.
     * @param entityResolver a class that implement the <code>EntityResolver</code> interface.
     */
    public void setEntityResolver(EntityResolver entityResolver){
        this.reader.setEntityResolver(entityResolver);
    }

    /**
     * Return the Entity Resolver used by the SAX parser.
     * @return Return the Entity Resolver used by the SAX parser.
     */
    public EntityResolver getEntityResolver(){
        return this.reader.getEntityResolver();
    }

    /**
     * Resolve the requested external entity.
     *
     * @param publicId The public identifier of the entity being referenced
     * @param systemId The system identifier of the entity being referenced
     *
     * @exception SAXException if a parsing exception occurs
     */
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug("resolveEntity('"
                    + publicId
                    + "', '"
                    + systemId
                    + "')");
        }

        // Has this system identifier been registered?
        URL entityURL = null;
        if (publicId != null) {
            entityURL = this.entityValidator.get(publicId);
        }

        // Redirect the schema location to a local destination
        if (entityURL == null && systemId != null){
            entityURL = this.entityValidator.get(systemId);
        } 

        if (entityURL == null) { 
            if (systemId == null) {
                // cannot resolve
                if (this.log.isDebugEnabled()) {
                    this.log.debug(" Cannot resolve null entity, returning null InputSource");
                }
                return null;
            } else {
                // try to resolve using system ID
                if (this.log.isDebugEnabled()) {
                    this.log.debug(" Trying to resolve using system ID '"
                            + systemId
                            + "'");
                }
                try {
                    entityURL = new URL(systemId);
                } catch (MalformedURLException e) {
                    throw new IllegalArgumentException("Malformed URL '"
                            + systemId
                            + "' : "
                            + e.getMessage());
                }
            }
        }

        // Return an input source to our alternative URL
        if (this.log.isDebugEnabled()) {
            this.log.debug(" Resolving to alternate DTD '"
                    + entityURL
                    + "'");
        }  

        try {
            InputSource source = createInputSourceFromURL(entityURL);
            this.inputSources.add(source);
            return source;
        } catch (Exception e) {
            throw this.createSAXException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setErrorHandler(ErrorHandler errorHandler) {
        this.reader.setErrorHandler(errorHandler);
    }

    /**
     * {@inheritDoc}
     */
    public ErrorHandler getErrorHandler() {
        return this.reader.getErrorHandler();
    }

    // ---- ErrorHandler Methods

    /**
     * {@inheritDoc}
     */
    public void error(SAXParseException e) throws SAXException {
        this.log.error("Parse Error at line "
                + e.getLineNumber()
                + " column "
                + e.getColumnNumber()
                + ": "
                + e.getMessage(), e);
    }

    /**
     * {@inheritDoc}
     */
    public void fatalError(SAXParseException e) throws SAXException {
        this.log.error("Parse Fatal Error at line "
                + e.getLineNumber()
                + " column "
                + e.getColumnNumber()
                + ": "
                + e.getMessage(), e);
    }

    /**
     * {@inheritDoc}
     */
    public void warning(SAXParseException e) throws SAXException {
        this.log.warn("Parse Warning Error at line "
                + e.getLineNumber()
                + " column "
                + e.getColumnNumber()
                + ": "
                + e.getMessage(), e);
    }

    // ---- Parsing & I/O

    /**
     * {@inheritDoc}
     */
    public Object parse(File file) throws IOException, SAXException {
        return this.parse(createInputSourceFromFile(file));
    }

    /**
     * {@inheritDoc}
     */
    public Object parse(InputSource input) throws IOException, SAXException {
        if (input == null) {
            throw new IllegalArgumentException("InputSource to parse must be not null");
        }

        this.inputSources.add(input);

        try {
            this.reader.parse(input);
            return this.root;
        } finally {
            this.cleanup();
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object parse(InputStream input) throws IOException, SAXException {
        return this.parse(createInputSourceFromInputStream(input));
    }

    /**
     * {@inheritDoc}
     */
    public Object parse(Reader reader) throws IOException, SAXException {
        return this.parse(createInputSourceFromReader(reader));
    }

    /**
     * {@inheritDoc}
     */
    public Object parse(String uri) throws IOException, SAXException {
        return this.parse(createInputSourceFromUri(uri));
    }

    /**
     * {@inheritDoc}
     */
    public Object parse(URL url) throws IOException, SAXException {
        return this.parse(createInputSourceFromURL(url));
    }

    /**
     * Clean up allocated resources after parsing is complete.
     *
     * The default method closes input streams that have been created by
     * Digester itself.
     */
    private void cleanup() {
        // If we created any InputSource objects in this instance,
        // they each have an input stream that should be closed
        for (InputSource source : this.inputSources) {
            InputStream stream = source.getByteStream();

            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    // Fall through so we get them all
                }
            }
        }
        this.inputSources.clear();
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getCurrentNamespaces() {
        Map<String, String> currentNamespaces = new HashMap<String, String>();
        for (Map.Entry<String, Stack<String>> nsEntry : this.namespaces.entrySet()) {
             try {
                currentNamespaces.put(nsEntry.getKey(), nsEntry.getValue().peek());
            } catch (RuntimeException e) {
                // rethrow, after logging
                this.log.error(e.getMessage(), e);
                throw e;
            }
        }
        return currentNamespaces;
    }

    // ---- SAX exception

    /**
     * {@inheritDoc}
     */
    public SAXException createSAXException(String message, Exception e) {
        if ((e != null) &&
            (e instanceof InvocationTargetException)) {
            Throwable t = ((InvocationTargetException) e).getTargetException();
            if ((t != null) && (t instanceof Exception)) {
                e = (Exception) t;
            }
        }
        if (this.locator != null) {
            String error = "Error at line "
                + this.locator.getLineNumber()
                + " char "
                + this.locator.getColumnNumber()
                + ": "
                + message;
            if (e != null) {
                return new SAXParseException(error, this.locator, e);
            } else {
                return new SAXParseException(error, this.locator);
            }
        }
        this.log.error("No Locator!");
        if (e != null) {
            return new SAXException(message, e);
        } else {
            return new SAXException(message);
        }
    }

    /**
     * {@inheritDoc}
     */
    public SAXException createSAXException(Exception e) {
        if (e instanceof InvocationTargetException) {
            Throwable t = ((InvocationTargetException) e).getTargetException();
            if ((t != null) && (t instanceof Exception)) {
                e = (Exception) t;
            }
        }
        return createSAXException(e.getMessage(), e);
    }

    /**
     * {@inheritDoc}
     */
    public SAXException createSAXException(String message) {
        return createSAXException(message, null);
    }

    /**
     * {@inheritDoc}
     */
    public <T> TypeConverter<T> lookupConverter(final Class<T> type) {
        // TODO empty implementation, will be changed for final version
        return null;
    }

}
