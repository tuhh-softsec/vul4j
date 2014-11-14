/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.esigate.impl;

import junit.framework.TestCase;

/**
 * Tests for UriMapping parsing, success and failure.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public class UriBuilderTest extends TestCase {

    public void testConstructor() {
        UriBuilder uriBuilder = new UriBuilder("/");
        assertNull(uriBuilder.getScheme());
        assertNull(uriBuilder.getHost());
        assertEquals(-1, uriBuilder.getPort());
        assertEquals("/", uriBuilder.getPath());
        assertNull(uriBuilder.getFile());
        assertNull(uriBuilder.getQueryString());
        assertEquals("/", uriBuilder.toString());
    }

    public void testConstructorWithFile() {
        UriBuilder uriBuilder = new UriBuilder("/test.html");
        assertNull(uriBuilder.getScheme());
        assertNull(uriBuilder.getHost());
        assertEquals(-1, uriBuilder.getPort());
        assertEquals("/", uriBuilder.getPath());
        assertEquals("test.html", uriBuilder.getFile());
        assertNull(uriBuilder.getQueryString());
        assertEquals("/test.html", uriBuilder.toString());
    }

    public void testConstructorEmptyString() {
        UriBuilder uriBuilder = new UriBuilder("");
        assertNull(uriBuilder.getScheme());
        assertNull(uriBuilder.getHost());
        assertEquals(-1, uriBuilder.getPort());
        assertNull(uriBuilder.getPath());
        assertNull(uriBuilder.getFile());
        assertNull(uriBuilder.getQueryString());
        assertEquals("", uriBuilder.toString());
    }

    public void testConstructorQueryString() {
        UriBuilder uriBuilder = new UriBuilder("?test");
        assertNull(uriBuilder.getScheme());
        assertNull(uriBuilder.getHost());
        assertEquals(-1, uriBuilder.getPort());
        assertNull(uriBuilder.getPath());
        assertNull(uriBuilder.getFile());
        assertEquals("test", uriBuilder.getQueryString());
        assertEquals("?test", uriBuilder.toString());
    }

    public void testConstructorSchemeHostPort() {
        UriBuilder uriBuilder = new UriBuilder("http://localhost:8080");
        assertEquals("http", uriBuilder.getScheme());
        assertEquals("localhost", uriBuilder.getHost());
        assertEquals(8080, uriBuilder.getPort());
        assertNull(uriBuilder.getPath());
        assertNull(uriBuilder.getFile());
        assertNull(uriBuilder.getQueryString());
        assertEquals("http://localhost:8080", uriBuilder.toString());
    }

    public void testConstructorSchemeHostPortSlash() {
        UriBuilder uriBuilder = new UriBuilder("http://localhost:8080/");
        assertEquals("http", uriBuilder.getScheme());
        assertEquals("localhost", uriBuilder.getHost());
        assertEquals(8080, uriBuilder.getPort());
        assertEquals("/", uriBuilder.getPath());
        assertNull(uriBuilder.getFile());
        assertNull(uriBuilder.getQueryString());
        assertEquals("http://localhost:8080/", uriBuilder.toString());
    }

    public void testSetPath() {
        UriBuilder uriBuilder = new UriBuilder();
        uriBuilder.setPath((String[]) null);
        assertNull(uriBuilder.getPath());
        uriBuilder.setPath("");
        assertNull(uriBuilder.getPath());
        uriBuilder.setPath("/");
        assertEquals("/", uriBuilder.getPath());
        uriBuilder.setPath("/", "");
        assertEquals("/", uriBuilder.getPath());
        uriBuilder.setPath("/", "/");
        assertEquals("/", uriBuilder.getPath());
        uriBuilder.setPath("/", "/test");
        assertEquals("/test/", uriBuilder.getPath());
        uriBuilder.setPath("//", "//test//");
        assertEquals("/test/", uriBuilder.getPath());
        uriBuilder.setPath("", "/test");
        assertEquals("/test/", uriBuilder.getPath());
    }
}
