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

package org.esigate.cookie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.http.cookie.ClientCookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.esigate.Driver;
import org.esigate.HttpErrorPage;
import org.esigate.MockRequestExecutor;
import org.esigate.Parameters;
import org.esigate.http.IncomingRequest;
import org.esigate.impl.DriverRequest;
import org.esigate.test.TestUtils;

public class DefaultCookieManagerTest extends TestCase {
    private DefaultCookieManager cookieManager;
    private DriverRequest request;
    private Driver driver;

    @Override
    protected void setUp() throws HttpErrorPage {
        Properties properties = new Properties();
        properties.setProperty(Parameters.STORE_COOKIES_IN_SESSION.getName(), "b, f");
        properties.setProperty(Parameters.DISCARD_COOKIES.getName(), "D,e");
        cookieManager = new DefaultCookieManager();
        cookieManager.init(null, properties);
        driver = MockRequestExecutor.createDriver();
        request = TestUtils.createDriverRequest(driver);
    }

    @Override
    protected void tearDown() {
        cookieManager = null;
    }

    public void testAddCookie() {
        assertNotNull(cookieManager.getCookies(request));
        assertEquals(0, cookieManager.getCookies(request).size());
        cookieManager.addCookie(new org.apache.http.impl.cookie.BasicClientCookie("b", "value"), request);
        assertNotNull(cookieManager.getCookies(request));
        assertEquals(1, cookieManager.getCookies(request).size());
        org.apache.http.cookie.Cookie cookie = cookieManager.getCookies(request).get(0);
        assertNotNull(cookie);
        assertEquals("b", cookie.getName());
        assertEquals("value", cookie.getValue());
        assertEquals(org.apache.http.impl.cookie.BasicClientCookie.class, cookie.getClass());

        // Test httponly attribute
        org.apache.http.impl.cookie.BasicClientCookie httponlycookie =
                new org.apache.http.impl.cookie.BasicClientCookie("f", "httponly_insession");
        httponlycookie.setAttribute("HttpOnly", "");
        cookieManager.addCookie(httponlycookie, request);

        assertEquals(2, cookieManager.getCookies(request).size());
        cookie = cookieManager.getCookies(request).get(1);
        assertEquals("f", cookie.getName());

        assertTrue(((ClientCookie) cookie).containsAttribute("HttpOnly"));

    }

    public void testRewriteForBrowser() {
        // Test httponly attribute
        BasicClientCookie cookie = new org.apache.http.impl.cookie.BasicClientCookie("a", "httponly_sendtobrowser");
        cookie.setDomain("localhost");
        cookie.setPath("test");
        cookie.setComment("comment");

        ClientCookie cookieRewrited = (ClientCookie) DefaultCookieManager.rewriteForBrowser(cookie, request);

        assertEquals(cookieRewrited.getComment(), cookie.getComment());
        assertFalse(cookieRewrited.containsAttribute("httponly"));

        cookie.setAttribute("httponly", "");

        cookieRewrited = (ClientCookie) DefaultCookieManager.rewriteForBrowser(cookie, request);
        assertTrue(cookieRewrited.containsAttribute("httponly"));
    }

    public void testInit() {
        assertNotNull(cookieManager.getStoredCookies());
        assertEquals(2, cookieManager.getStoredCookies().size());
        List<String> cookieNames = new ArrayList<String>(cookieManager.getStoredCookies());
        Collections.sort(cookieNames);
        assertEquals("b", cookieNames.get(0));
        assertEquals("f", cookieNames.get(1));
    }

    public void testFilter() throws HttpErrorPage {
        IncomingRequest incomingRequest =
                TestUtils.createIncomingRequest().addCookie(new BasicClientCookie("a", "value a"))
                        .addCookie(new BasicClientCookie("b", "value b"))
                        .addCookie(new BasicClientCookie("c", "value c"))
                        .addCookie(new BasicClientCookie("e", "value e")).build();
        request = new DriverRequest(incomingRequest, driver, "/");
        List<org.apache.http.cookie.Cookie> cookies = cookieManager.getCookies(request);
        assertNotNull(cookies);
        assertFalse(cookies.isEmpty());
        assertEquals(2, cookies.size());
        assertEquals("a", cookies.get(0).getName());
        assertEquals("c", cookies.get(1).getName());
    }

    public void testRewriteDomain() {
        assertEquals(null, DefaultCookieManager.rewriteDomain("provider1", "provider1", "www.foo.com"));
        assertEquals(".foo.com",
                DefaultCookieManager.rewriteDomain(".provider1.net", "www.provider1.net", "www.foo.com"));
        assertEquals(".subdomain.foo.com", DefaultCookieManager.rewriteDomain(".subdomain1.provider1.net",
                "www.subdomain1.provider1.net", "www.subdomain.foo.com"));
        assertEquals(".foo.com",
                DefaultCookieManager.rewriteDomain(".provider1.net", "www.provider1.net", "www.subdomain.foo.com"));
        assertEquals(null, DefaultCookieManager.rewriteDomain("www.provider1.net", "www.provider1.net", "www.foo.com"));
        assertEquals(null, DefaultCookieManager.rewriteDomain("localhost", "localhost", "www.foo.com"));
        assertEquals(null, DefaultCookieManager.rewriteDomain("127.0.0.1", "127.0.0.1", "www.foo.com"));
        assertEquals(null, DefaultCookieManager.rewriteDomain("provider1.subdomain.net", "www.provider1.subdomain.net",
                "www.foo.com"));
    }

    /**
     * 0000232: Cookie management : ESIGate should replace in user's cookie store new cookies received instead of always
     * adding them.
     * 
     * @see "http://sourceforge.net/apps/mantisbt/webassembletool/view.php?id=232"
     * 
     */
    public void testReplace() {
        assertEquals(0, cookieManager.getCookies(request).size());

        // Add cookie
        cookieManager.addCookie(new BasicClientCookie("b", "value1"), request);
        assertEquals(1, cookieManager.getCookies(request).size());

        // Replace cookie value
        cookieManager.addCookie(new BasicClientCookie("b", "value2"), request);

        // Ensure cookie is replaced
        assertEquals(1, cookieManager.getCookies(request).size());
        assertEquals("value2", cookieManager.getCookies(request).get(0).getValue());

    }

}
