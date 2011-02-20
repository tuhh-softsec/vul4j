/*
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
package org.apache.commons.digester3.examples.documentmarkup;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.Rule;
import org.apache.commons.digester3.spi.Rules;
import org.apache.commons.logging.Log;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * This is a subclass of digester which supports rules which implement
 * the TextSegmentHandler interface, causing the "textSegment" method
 * on each matching rule (of the appropriate type) to be invoked when
 * an element contains a segment of text followed by a child element.
 * <p>
 * See the readme file included with this example for more information.
 */
public class MarkupDigester implements Digester {

    /**
     * The text found in the current element since the last child element.
     */
    private final  StringBuffer currTextSegment = new StringBuffer();

    private final Digester wrapped;

    public MarkupDigester(Digester wrapped) {
        this.wrapped = wrapped;
    }

    public void warning(SAXParseException exception) throws SAXException {
        this.wrapped.warning(exception);
    }

    public void error(SAXParseException exception) throws SAXException {
        this.wrapped.error(exception);
    }

    public void fatalError(SAXParseException exception) throws SAXException {
        this.wrapped.fatalError(exception);
    }

    public void setDocumentLocator(Locator locator) {
        this.wrapped.setDocumentLocator(locator);
    }

    public void startDocument() throws SAXException {
        this.wrapped.startDocument();
    }

    public void endDocument() throws SAXException {
        this.wrapped.endDocument();
    }

    public void startPrefixMapping(String prefix, String uri)
            throws SAXException {
        this.wrapped.startPrefixMapping(prefix, uri);
    }

    public void endPrefixMapping(String prefix) throws SAXException {
        this.wrapped.endPrefixMapping(prefix);
    }

    public void startElement(String uri, String localName, String qName,
            Attributes atts) throws SAXException {
        handleTextSegments();

        // Unlike bodyText, which accumulates despite intervening child
        // elements, currTextSegment gets cleared here. This means that
        // we don't need to save it on a stack either.
        currTextSegment.setLength(0);
        this.wrapped.startElement(uri, localName, qName, atts);
    }

    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        handleTextSegments();
        currTextSegment.setLength(0);
        this.wrapped.endElement(uri, localName, qName);
    }

    public void characters(char[] ch, int start, int length)
            throws SAXException {
        this.wrapped.characters(ch, start, length);
        this.currTextSegment.append(ch, start, length);
    }

    public void ignorableWhitespace(char[] ch, int start, int length)
            throws SAXException {
        this.wrapped.ignorableWhitespace(ch, start, length);
    }

    public void processingInstruction(String target, String data)
            throws SAXException {
        this.wrapped.processingInstruction(target, data);
    }

    public void skippedEntity(String name) throws SAXException {
        this.wrapped.skippedEntity(name);
    }

    public InputSource resolveEntity(String publicId, String systemId)
            throws SAXException, IOException {
        return this.wrapped.resolveEntity(publicId, systemId);
    }

    public void notationDecl(String name, String publicId, String systemId)
            throws SAXException {
        this.wrapped.notationDecl(name, publicId, systemId);
    }

    public void unparsedEntityDecl(String name, String publicId,
            String systemId, String notationName) throws SAXException {
        this.wrapped.unparsedEntityDecl(name, publicId, systemId, notationName);
    }

    public ClassLoader getClassLoader() {
        return this.wrapped.getClassLoader();
    }

    public Log getLog() {
        return this.wrapped.getLog();
    }

    public Log getSAXLog() {
        return this.wrapped.getSAXLog();
    }

    public String getMatch() {
        return this.wrapped.getMatch();
    }

    public Object parse(File file) throws IOException, SAXException {
        return this.wrapped.parse(file);
    }

    public Object parse(InputSource input) throws IOException, SAXException {
        return this.wrapped.parse(input);
    }

    public Object parse(InputStream input) throws IOException, SAXException {
        return this.wrapped.parse(input);
    }

    public Object parse(Reader reader) throws IOException, SAXException {
        return this.wrapped.parse(reader);
    }

    public Object parse(String uri) throws IOException, SAXException {
        return this.wrapped.parse(uri);
    }

    public Object parse(URL url) throws IOException, SAXException {
        return this.wrapped.parse(url);
    }

    public Rules getRules() {
        return this.wrapped.getRules();
    }

    public void setRules(Rules rules) {
        this.wrapped.setRules(rules);
    }

    public void clear() {
        this.wrapped.clear();
    }

    public int getCount() {
        return this.wrapped.getCount();
    }

    public void push(Object object) {
        this.wrapped.push(object);
    }

    public void push(String stackName, Object value) {
        this.wrapped.push(stackName, value);
    }

    public Object pop() {
        return this.wrapped.pop();
    }

    public Object pop(String stackName) {
        return this.wrapped.pop(stackName);
    }

    public Object peek() {
        return this.wrapped.peek();
    }

    public Object peek(String stackName) {
        return this.wrapped.peek(stackName);
    }

    public Object peek(int n) {
        return this.wrapped.peek(n);
    }

    public Object peek(String stackName, int n) {
        return this.wrapped.peek(stackName, n);
    }

    public boolean isEmpty(String stackName) {
        return this.wrapped.isEmpty(stackName);
    }

    public Object getRoot() {
        return this.wrapped.getRoot();
    }

    public void resetRoot() {
        this.wrapped.resetRoot();
    }

    public Object peekParams() {
        return this.wrapped.peekParams();
    }

    public Object peekParams(int n) {
        return this.wrapped.peekParams(n);
    }

    public void pushParams(Object object) {
        this.wrapped.pushParams(object);
    }

    public Object popParams() {
        return this.wrapped.popParams();
    }

    public Map<String, String> getCurrentNamespaces() {
        return this.wrapped.getCurrentNamespaces();
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.wrapped.setErrorHandler(errorHandler);
    }

    public ErrorHandler getErrorHandler() {
        return this.wrapped.getErrorHandler();
    }

    public SAXException createSAXException(String message, Exception e) {
        return this.wrapped.createSAXException(message, e);
    }

    public SAXException createSAXException(Exception e) {
        return this.wrapped.createSAXException(e);
    }

    public SAXException createSAXException(String message) {
        return this.wrapped.createSAXException(message);
    }

    /**
     * Iterate over the list of rules most recently matched, and
     * if any of them implement the TextSegmentHandler interface then
     * invoke that rule's textSegment method passing the current
     * segment of text from the xml element body.
     */
    private void handleTextSegments() throws SAXException {
        if (currTextSegment.length() > 0) {
            String segment = currTextSegment.toString();
            List<Rule> parentMatches;
            try {
                parentMatches = (List<Rule>) ((Stack<Rule>) this.wrapped.getClass().getField("matches").get(this.wrapped)).peek();
            } catch (Exception e) {
                throw new SAXException(e);
            }
            int len = parentMatches.size();
            for (Rule r : parentMatches) {
                if (r instanceof TextSegmentHandler) {
                    TextSegmentHandler h = (TextSegmentHandler) r;
                    try {
                        h.textSegment(segment);
                    } catch(Exception e) {
                        throw createSAXException(e);
                    }
                }
            }
        }
    }

}
