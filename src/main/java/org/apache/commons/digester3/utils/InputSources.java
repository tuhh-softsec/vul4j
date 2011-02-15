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
package org.apache.commons.digester3.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;

import org.xml.sax.InputSource;

/**
 * Utilities class to manipulate {@code org.xml.sax.InputSource}.
 */
public final class InputSources {

    /**
     * This class can't be instantiated
     */
    private InputSources() {
        // do nothing
    }

    /**
     * Opens a new {@code org.xml.sax.InputSource} given the XML file.
     *
     * @param file The XML file
     * @return The {@code org.xml.sax.InputSource} to read the given XML file
     * @throws IOException if any error occurs while opening the file
     */
    public static InputSource createInputSourceFromFile(File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("File to parse must be not null");
        }

        return createInputSourceFromURL(file.toURI().toURL());
    }

    /**
     * Opens a new {@code org.xml.sax.InputSource} given the URI that contains the XML to parse.
     *
     * @param uri The URI that contains the XML to parse
     * @return The {@code org.xml.sax.InputSource} to read the given URI
     * @throws IOException if any error occurs while opening the URI
     */
    public static InputSource createInputSourceFromUri(String uri) throws IOException {
        if (uri == null) {
            throw new IllegalArgumentException("String URI to parse must be not null");
        }

        return createInputSourceFromURL(new URL(uri));
    }

    /**
     * Given a URL, return an InputSource that reads from that URL.
     * <p>
     * Ideally this function would not be needed and code could just use
     * <code>new InputSource(entityURL)</code>. Unfortunately it appears
     * that when the entityURL points to a file within a jar archive a
     * caching mechanism inside the InputSource implementation causes a
     * file-handle to the jar file to remain open. On Windows systems
     * this then causes the jar archive file to be locked on disk
     * ("in use") which makes it impossible to delete the jar file -
     * and that really stuffs up "undeploy" in webapps in particular.
     * <p>
     * In JDK1.4 and later, Apache XercesJ is used as the xml parser.
     * The InputSource object provided is converted into an XMLInputSource,
     * and eventually passed to an instance of XMLDocumentScannerImpl to
     * specify the source data to be converted into tokens for the rest
     * of the XMLReader code to handle. XMLDocumentScannerImpl calls
     * fEntityManager.startDocumentEntity(source), where fEntityManager
     * is declared in ancestor class XMLScanner to be an XMLEntityManager. In
     * that class, if the input source stream is null, then:
     * <pre>
     *  URL location = new URL(expandedSystemId);
     *  URLConnection connect = location.openConnection();
     *  if (connect instanceof HttpURLConnection) {
     *    setHttpProperties(connect,xmlInputSource);
     *  }
     *  stream = connect.getInputStream();
     * </pre>
     * This method pretty much duplicates the standard behaviour, except
     * that it calls URLConnection.setUseCaches(false) before opening
     * the connection.
     */
    public static InputSource createInputSourceFromURL(URL url) throws IOException {
        if (url == null) {
            throw new IllegalArgumentException("Parameter 'url' must be not null");
        }
        URLConnection connection = url.openConnection();
        connection.setUseCaches(false);
        InputStream stream = connection.getInputStream();
        InputSource source = new InputSource(stream);
        source.setSystemId(url.toExternalForm());
        return source;
    }

    /**
     * Opens a new {@code org.xml.sax.InputSource} given the XML in textual form.
     *
     * @param xmlText The XML in textual form
     * @return The {@code org.xml.sax.InputSource} to read the given XML in textual form
     */
    public static InputSource createInputSourceFromInputStream(InputStream input) {
        if (input == null) {
            throw new IllegalArgumentException("Parameter 'input' must be not null");
        }
        return new InputSource(input);
    }

    /**
     * Opens a new {@code org.xml.sax.InputSource} given the XML in textual form.
     *
     * @param xmlText The XML in textual form
     * @return The {@code org.xml.sax.InputSource} to read the given XML in textual form
     */
    public static InputSource createInputSourceFromString(String xmlText) {
        if (xmlText == null) {
            throw new IllegalArgumentException("Parameter 'xmlText' must be not null");
        }
        return createInputSourceFromReader(new StringReader(xmlText));
    }

    /**
     * Opens a new {@code org.xml.sax.InputSource} given a {@code java.io.Reader}.
     *
     * @param xmlText The XML {@code java.io.Reader}
     * @return The {@code org.xml.sax.InputSource} to read the given XML {@code java.io.Reader}
     */
    public static InputSource createInputSourceFromReader(Reader reader) {
        if (reader == null) {
            throw new IllegalArgumentException("Parameter 'reader' must be not null");
        }
        return new InputSource(reader);
    }

}
