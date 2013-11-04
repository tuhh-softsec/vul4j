/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.esigate.http;

import java.util.Properties;

import junit.framework.TestCase;

import org.esigate.Parameters;

public class HeaderManagerTest extends TestCase {
    private Properties properties;
    private HeaderManager headerManager;

    public void testIsBlackListed() {
        // by default only DriverConfiguration#DEFAULT_BLACK_LISTED_HEADERS are
        // blacklisted
        properties = new Properties();
        headerManager = new HeaderManager(properties);

        assertRequestHeaderIsBlacklisted("Content-Length", true);
        assertRequestHeaderIsBlacklisted("Content-Length".toUpperCase(), true);
        assertRequestHeaderIsBlacklisted("Content-Length".toLowerCase(), true);
        assertRequestHeaderIsBlacklisted("Transfer-Encoding", true);
        assertResponseHeaderIsBlacklisted("Set-Cookie", true);
        assertRequestHeaderIsBlacklisted("Cookie", true);
        assertRequestHeaderIsBlacklisted("Connection", true);
        assertResponseHeaderIsBlacklisted("Keep-Alive", true);
        assertResponseHeaderIsBlacklisted("Proxy-Authenticate", true);
        assertRequestHeaderIsBlacklisted("Proxy-Authorization", true);
        assertRequestHeaderIsBlacklisted("TE", true);
        assertResponseHeaderIsBlacklisted("Trailer", true);
        assertRequestHeaderIsBlacklisted("Upgrade", true);

        // blacklisted headers are specified via 'blackListedHeaders' property
        // -> they are merged with default
        properties = new Properties();
        properties.setProperty(Parameters.DISCARD_REQUEST_HEADERS.getName(), "header");
        headerManager = new HeaderManager(properties);
        assertRequestHeaderIsBlacklisted("Content-Length", true);
        assertRequestHeaderIsBlacklisted("Content-Length".toUpperCase(), true);
        assertRequestHeaderIsBlacklisted("Content-Length".toLowerCase(), true);
        assertRequestHeaderIsBlacklisted("Transfer-Encoding", true);
        assertResponseHeaderIsBlacklisted("Set-Cookie", true);
        assertRequestHeaderIsBlacklisted("Cookie", true);
        assertRequestHeaderIsBlacklisted("Connection", true);
        assertResponseHeaderIsBlacklisted("Keep-Alive", true);
        assertResponseHeaderIsBlacklisted("Proxy-Authenticate", true);
        assertRequestHeaderIsBlacklisted("Proxy-Authorization", true);
        assertRequestHeaderIsBlacklisted("TE", true);
        assertResponseHeaderIsBlacklisted("Trailer", true);
        assertRequestHeaderIsBlacklisted("Upgrade", true);
        assertRequestHeaderIsBlacklisted("header", true);
        assertRequestHeaderIsBlacklisted("header".toUpperCase(), true);
    }

    private void assertRequestHeaderIsBlacklisted(String header, boolean blacklisted) {
        assertEquals("'" + header + "' header should " + (blacklisted ? "" : "not ") + "be blacklisted",
                !headerManager.isForwardedRequestHeader(header), blacklisted);
    }

    private void assertResponseHeaderIsBlacklisted(String header, boolean blacklisted) {
        assertEquals("'" + header + "' header should " + (blacklisted ? "" : "not ") + "be blacklisted",
                !headerManager.isForwardedResponseHeader(header), blacklisted);
    }

    public void testDiscardRequestHeader() {
        properties = new Properties();
        properties.put(Parameters.DISCARD_REQUEST_HEADERS.getName(), "dummy1,dummy2");
        headerManager = new HeaderManager(properties);
        assertFalse("Header should be discarded", headerManager.isForwardedRequestHeader("dummy1"));
        assertFalse("Header should be discarded", headerManager.isForwardedRequestHeader("dummy2"));
        assertTrue("Header should be forwarded", headerManager.isForwardedRequestHeader("dummy3"));
    }

    public void testForwardRequestHeader() {
        properties = new Properties();
        properties.put(Parameters.FORWARD_REQUEST_HEADERS.getName(), "Authorization");
        headerManager = new HeaderManager(properties);
        assertTrue("Header should be forwarded", headerManager.isForwardedRequestHeader("Authorization"));
    }

    public void testDiscardResponseHeader() {
        properties = new Properties();
        properties.put(Parameters.DISCARD_RESPONSE_HEADERS.getName(), "dummy1,dummy2");
        headerManager = new HeaderManager(properties);
        assertFalse("Header should be discarded", headerManager.isForwardedResponseHeader("dummy1"));
        assertFalse("Header should be discarded", headerManager.isForwardedResponseHeader("dummy2"));
        assertTrue("Header should be forwarded", headerManager.isForwardedResponseHeader("dummy3"));
    }

    public void testForwardResponseHeader() {
        properties = new Properties();
        properties.put(Parameters.FORWARD_RESPONSE_HEADERS.getName(), "WWW-Authenticate");
        headerManager = new HeaderManager(properties);
        assertTrue("Header should be forwarded", headerManager.isForwardedResponseHeader("WWW-Authenticate"));
    }

}
