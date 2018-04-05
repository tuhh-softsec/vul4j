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

import org.apache.http.Header;
import org.esigate.Driver;
import org.esigate.Parameters;
import org.esigate.impl.DriverRequest;
import org.esigate.impl.UrlRewriter;
import org.esigate.test.PropertiesBuilder;
import org.esigate.test.TestUtils;
import org.mockito.Mockito;

public class HeaderManagerTest extends TestCase {
    private HeaderManager headerManager;

    @Override
    protected void setUp() throws Exception {
        UrlRewriter urlRewriter = Mockito.mock(UrlRewriter.class);
        headerManager = new HeaderManager(urlRewriter);
    }

    public void testBlackListed() {
        assertRequestHeaderIsBlacklisted("MyHeader", false);
        assertResponseHeaderIsBlacklisted("MyHeader", false);
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
        String not;
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

    /**
     * Test that we set a X-Forwarded-Proto header in backend requests.
     * 
     * @throws Exception
     */
    public void testXForwardedProtoHeader() throws Exception {

        Properties props = new PropertiesBuilder() //
                .set(Parameters.REMOTE_URL_BASE, "http://www.foo.com/") //
                .build();

        Driver driver = Driver.builder().setName("test").setProperties(props).build();
        DriverRequest driverRequest = TestUtils.createDriverRequest("https://wwww.foo.com", driver);
        OutgoingRequest outgoingRequest =
                new OutgoingRequest(driverRequest.getOriginalRequest().getRequestLine().getMethod(), driverRequest
                        .getOriginalRequest().getRequestLine().getUri(), driverRequest.getOriginalRequest()
                        .getRequestLine().getProtocolVersion(), driverRequest, null, null);
        headerManager.copyHeaders(driverRequest, outgoingRequest);
        Header[] headers = outgoingRequest.getHeaders("X-Forwarded-Proto");
        assertEquals("We should have 1 X-Forwarded-Proto header", 1, headers.length);
        assertEquals("Wrong X-Forwarded-Proto header", "https", headers[0].getValue());
    }

}
