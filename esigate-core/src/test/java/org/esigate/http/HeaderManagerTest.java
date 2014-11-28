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

import junit.framework.TestCase;

import org.esigate.impl.UrlRewriter;
import org.mockito.Mockito;

public class HeaderManagerTest extends TestCase {
    private HeaderManager headerManager;

    public void testIsBlackListed() {
        UrlRewriter urlRewriter = Mockito.mock(UrlRewriter.class);
        headerManager = new HeaderManager(urlRewriter);

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
    }

    private void assertRequestHeaderIsBlacklisted(String header, boolean blacklisted) {
        String not = "not ";
        if (blacklisted) {
            not = "";
        } else {
            not = "not ";
        }
        assertEquals("'" + header + "' header should " + not + "be blacklisted",
                !headerManager.isForwardedRequestHeader(header), blacklisted);
    }

    private void assertResponseHeaderIsBlacklisted(String header, boolean blacklisted) {
        String not = "not ";
        if (blacklisted) {
            not = "";
        } else {
            not = "not ";
        }
        assertEquals("'" + header + "' header should " + not + "be blacklisted",
                !headerManager.isForwardedResponseHeader(header), blacklisted);
    }

}
