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
package org.apache.commons.digester3;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.EmptyStackException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A <strong>Digester</strong> processes an XML input stream by matching a
 * series of element nesting patterns to execute Rules that have been added
 * prior to the start of parsing.
 */
public interface Digester extends ContentHandler, DTDHandler, EntityResolver, ErrorHandler {

    /**
     * Return the class loader to be used for instantiating application objects
     * when required.
     *
     * @return the class loader to be used for instantiating application objects
     *         when required
     */
    ClassLoader getClassLoader();

    /**
     * The Log to which most logging calls will be made.
     *
     * @return the Log to which most logging calls will be made.
     */
    Log getLog();

    /**
     * Gets the logger used for logging SAX-related information.
     * <strong>Note</strong> the output is finely grained.
     *
     * @return the logger used for logging SAX-related information.
     */
    Log getSAXLog();

    /**
     * The current match pattern for nested element processing.
     *
     * @return the current match pattern for nested element processing.
     */
    String getMatch();

    /**
     * Parse the content of the specified file using this Digester.  Returns
     * the root element from the object stack (if any).
     *
     * @param file File containing the XML data to be parsed
     *
     * @exception IOException if an input/output error occurs
     * @exception SAXException if a parsing exception occurs
     */
    Object parse(File file) throws IOException, SAXException;

    /**
     * Parse the content of the specified input source using this Digester.
     * Returns the root element from the object stack (if any).
     *
     * @param input Input source containing the XML data to be parsed
     *
     * @exception IOException if an input/output error occurs
     * @exception SAXException if a parsing exception occurs
     */
    Object parse(InputSource input) throws IOException, SAXException;

    /**
     * Parse the content of the specified input stream using this Digester.
     * Returns the root element from the object stack (if any).
     *
     * @param input Input stream containing the XML data to be parsed
     *
     * @exception IOException if an input/output error occurs
     * @exception SAXException if a parsing exception occurs
     */
    Object parse(InputStream input) throws IOException, SAXException;

    /**
     * Parse the content of the specified reader using this Digester.
     * Returns the root element from the object stack (if any).
     *
     * @param reader Reader containing the XML data to be parsed
     *
     * @exception IOException if an input/output error occurs
     * @exception SAXException if a parsing exception occurs
     */
    Object parse(Reader reader) throws IOException, SAXException;

    /**
     * Parse the content of the specified URI using this Digester.
     * Returns the root element from the object stack (if any).
     *
     * @param uri URI containing the XML data to be parsed
     *
     * @exception IOException if an input/output error occurs
     * @exception SAXException if a parsing exception occurs
     */
    Object parse(String uri) throws IOException, SAXException;

    /**
     * Parse the content of the specified URL using this Digester.
     * Returns the root element from the object stack (if any).
     *
     * @param url URL containing the XML data to be parsed
     *
     * @exception IOException if an input/output error occurs
     * @exception SAXException if a parsing exception occurs
     */
    Object parse(URL url) throws IOException, SAXException;

    /**
     * Clear the current contents of the default object stack, the param stack,
     * all named stacks, and other internal variables.
     */
    void clear();

    /**
     * Return the current depth of the element stack.
     */
    int getCount();

    /**
     * Push a new object onto the top of the object stack.
     *
     * @param object The new object
     */
    void push(Object object);

    /**
     * Pushes the given object onto the stack with the given name.
     * If no stack already exists with the given name then one will be created.
     *
     * @param stackName the name of the stack onto which the object should be pushed
     * @param value the Object to be pushed onto the named stack.
     */
    void push(String stackName, Object value);

    /**
     * Pop the top object off of the stack, and return it.  If there are
     * no objects on the stack, return <code>null</code>.
     */
    Object pop();

    /**
     * <p>Pops (gets and removes) the top object from the stack with the given name.</p>
     *
     * <p><strong>Note:</strong> a stack is considered empty
     * if no objects have been pushed onto it yet.</p>
     *
     * @param stackName the name of the stack from which the top value is to be popped.
     * @return the top <code>Object</code> on the stack or or null if the stack is either 
     * empty or has not been created yet
     * @throws EmptyStackException if the named stack is empty
     */
    Object pop(String stackName);

    /**
     * Return the top object on the stack without removing it.  If there are
     * no objects on the stack, return <code>null</code>.
     */
    Object peek();

    /**
     * <p>Gets the top object from the stack with the given name.
     * This method does not remove the object from the stack.
     * </p>
     * <p><strong>Note:</strong> a stack is considered empty
     * if no objects have been pushed onto it yet.</p>
     *
     * @param stackName the name of the stack to be peeked
     * @return the top <code>Object</code> on the stack or null if the stack is either 
     * empty or has not been created yet
     * @throws EmptyStackException if the named stack is empty
     */
    Object peek(String stackName);

    /**
     * Return the n'th object down the stack, where 0 is the top element
     * and [getCount()-1] is the bottom element.  If the specified index
     * is out of range, return <code>null</code>.
     *
     * @param n Index of the desired element, where 0 is the top of the stack,
     *  1 is the next element down, and so on.
     */
    Object peek(int n);

    /**
     * <p>Gets the top object from the stack with the given name.
     * This method does not remove the object from the stack.
     * </p>
     * <p><strong>Note:</strong> a stack is considered empty
     * if no objects have been pushed onto it yet.</p>
     *
     * @param stackName the name of the stack to be peeked
     * @param n Index of the desired element, where 0 is the top of the stack,
     *  1 is the next element down, and so on.
     * @return the specified <code>Object</code> on the stack.
     * @throws EmptyStackException if the named stack is empty.
     */
    Object peek(String stackName, int n);

    /**
     * <p>Is the stack with the given name empty?</p>
     * <p><strong>Note:</strong> a stack is considered empty
     * if no objects have been pushed onto it yet.</p>
     * @param stackName the name of the stack whose emptiness 
     * should be evaluated
     * @return true if the given stack if empty
     */
    boolean isEmpty(String stackName);

    /**
     * Returns the root element of the tree of objects created as a result
     * of applying the rule objects to the input XML.
     * <p>
     * If the digester stack was "primed" by explicitly pushing a root
     * object onto the stack before parsing started, then that root object
     * is returned here.
     * <p>
     * Alternatively, if a Rule which creates an object (eg ObjectCreateRule)
     * matched the root element of the xml, then the object created will be
     * returned here.
     * <p>
     * In other cases, the object most recently pushed onto an empty digester
     * stack is returned. This would be a most unusual use of digester, however;
     * one of the previous configurations is much more likely.
     * <p>
     * Note that when using one of the Digester.parse methods, the return
     * value from the parse method is exactly the same as the return value
     * from this method. However when the Digester is being used as a 
     * SAXContentHandler, no such return value is available; in this case, this
     * method allows you to access the root object that has been created 
     * after parsing has completed.
     *
     * @return the root object that has been created after parsing
     *  or null if the digester has not parsed any XML yet.
     */
    Object getRoot();

    /**
     * This method allows the "root" variable to be reset to null.
     * <p>
     * It is not considered safe for a digester instance to be reused
     * to parse multiple xml documents. However if you are determined to
     * do so, then you should call both clear() and resetRoot() before
     * each parse.
     */
    void resetRoot();

    /**
     * <p>Return the top object on the parameters stack without removing it.  If there are
     * no objects on the stack, return <code>null</code>.</p>
     *
     * <p>The parameters stack is used to store <code>CallMethodRule</code> parameters. 
     * See {@link #params}.</p>
     */
    Object peekParams();

    /**
     * <p>Return the n'th object down the parameters stack, where 0 is the top element
     * and [getCount()-1] is the bottom element.  If the specified index
     * is out of range, return <code>null</code>.</p>
     *
     * <p>The parameters stack is used to store <code>CallMethodRule</code> parameters.
     *
     * @param n Index of the desired element, where 0 is the top of the stack,
     *  1 is the next element down, and so on.
     */
    Object peekParams(int n);

    /**
     * <p>Push a new object onto the top of the parameters stack.</p>
     *
     * <p>The parameters stack is used to store <code>CallMethodRule</code> parameters. 
     * See {@link #params}.</p>
     *
     * @param object The new object
     */
    void pushParams(Object object);

    /**
     * <p>Pop the top object off of the parameters stack, and return it.  If there are
     * no objects on the stack, return <code>null</code>.</p>
     *
     * <p>The parameters stack is used to store <code>CallMethodRule</code> parameters. 
     * See {@link #params}.</p>
     */
    Object popParams();

    /**
     * Get the most current namespaces for all prefixes.
     *
     * @return Map A map with namespace prefixes as keys and most current
     *             namespace URIs for the corresponding prefixes as values
     */
    Map<String, String> getCurrentNamespaces();

    /**
     * Set the custom error handler for this Digester.
     *
     * @param errorHandler The new error handler
     */
    void setErrorHandler(ErrorHandler errorHandler);

    /**
     * Return the error handler for this Digester.
     */
    ErrorHandler getErrorHandler();

    /**
     * Create a SAX exception which also understands about the location in
     * the digester file where the exception occurs
     *
     * @return the new exception
     */
    SAXException createSAXException(String message, Exception e);

    /**
     * Create a SAX exception which also understands about the location in
     * the digester file where the exception occurs
     *
     * @return the new exception
     */
    SAXException createSAXException(Exception e);

    /**
     * Create a SAX exception which also understands about the location in
     * the digester file where the exception occurs
     *
     * @return the new exception
     */
    SAXException createSAXException(String message);

}
