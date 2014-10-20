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

package org.esigate.servlet.impl;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.esigate.http.BasicCloseableHttpResponse;
import org.esigate.servlet.MockHttpServletResponse;

public class ResponseSenderTest extends TestCase {
    private SimpleDateFormat format;
    private ResponseSender renderer;

    @Override
    protected void setUp() throws Exception {
        renderer = new ResponseSender();
        format = new SimpleDateFormat(DateUtils.PATTERN_RFC1123, Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        super.setUp();
    }

    public void testRewriteCookieExpires() throws Exception {
        CookieSpec cookieSpec = new BrowserCompatSpec();
        String expires = format.format(new Date(System.currentTimeMillis() + 86400000));
        Header header =
                new BasicHeader("Set-Cookie", "K_lm_66638=121203111217326896; Domain=.foo.com; Expires=" + expires
                        + "; Path=/");
        CookieOrigin origin = new CookieOrigin("www.foo.com", 80, "/", false);
        Cookie src = cookieSpec.parse(header, origin).get(0);
        javax.servlet.http.Cookie result = renderer.rewriteCookie(src);
        assertTrue("maxAge should be greater than 86395, actual value " + result.getMaxAge(),
                result.getMaxAge() > 86395);
        assertTrue("maxAge should be lower than 86401, actual value " + result.getMaxAge(), result.getMaxAge() < 86401);
    }

    public void testRewriteCookieExpiresLongTime() throws Exception {
        CookieSpec cookieSpec = new BrowserCompatSpec();
        String expires = format.format(new Date(System.currentTimeMillis() + 15552000000L));
        Header header =
                new BasicHeader("Set-Cookie", "K_66638=121203111217326896; Domain=.foo.com; Expires=" + expires
                        + "; Path=/");
        CookieOrigin origin = new CookieOrigin("www.foo.com", 80, "/", false);
        Cookie src = cookieSpec.parse(header, origin).get(0);
        javax.servlet.http.Cookie result = renderer.rewriteCookie(src);
        assertTrue("maxAge should be greater than 15551995, actual value " + result.getMaxAge(),
                result.getMaxAge() > 15551995);
        assertTrue("maxAge should be lower than 15552001, actual value " + result.getMaxAge(),
                result.getMaxAge() < 15552001);
    }

    public void testSendResponseAlreadySent() throws Exception {
        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
        PrintWriter writer = httpServletResponse.getWriter();
        writer.write("Test");
        writer.close();
        CloseableHttpResponse httpClientResponse =
                BasicCloseableHttpResponse.adapt(new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1,
                        HttpStatus.SC_OK, "OK")));
        httpClientResponse.setEntity(new StringEntity("Abcdefg"));
        renderer.sendResponse(httpClientResponse, null, httpServletResponse);
    }

}
