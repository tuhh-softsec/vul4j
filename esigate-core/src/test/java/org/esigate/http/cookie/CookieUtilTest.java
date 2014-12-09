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

package org.esigate.http.cookie;

import java.net.HttpCookie;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.apache.http.Header;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;

public class CookieUtilTest extends TestCase {

    private static final int ONE_DAY = 86400000;
    private SimpleDateFormat format;
    private CookieSpec cookieSpec;

    @Override
    protected void setUp() throws Exception {

        format = new SimpleDateFormat(DateUtils.PATTERN_RFC1123, Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        cookieSpec = new CustomBrowserCompatSpecFactory().create(null);
        super.setUp();
    }

    public void testHttpOnlyCookie() throws Exception {

        String expires = format.format(new Date(System.currentTimeMillis() + ONE_DAY));
        Header header =
                new BasicHeader("Set-Cookie", "K_lm_66638=121203111217326896; Domain=.foo.com; Expires=" + expires
                        + "; HttpOnly;Secure;Path=/");
        CookieOrigin origin = new CookieOrigin("www.foo.com", 80, "/", false);
        Cookie src = cookieSpec.parse(header, origin).get(0);
        String result = CookieUtil.encodeCookie(src);
        HttpCookie httpcookie = HttpCookie.parse(result).get(0);
        assertTrue("Should be an httponly cookie", httpcookie.isHttpOnly());
        assertTrue("Should be a secure cookie", httpcookie.getSecure());
    }

    public void testRewriteCookieExpires() throws Exception {
        String expires = format.format(new Date(System.currentTimeMillis() + ONE_DAY));
        Header header =
                new BasicHeader("Set-Cookie", "K_lm_66638=121203111217326896; Domain=.foo.com; Expires=" + expires
                        + "; Path=/");
        CookieOrigin origin = new CookieOrigin("www.foo.com", 80, "/", false);
        Cookie src = cookieSpec.parse(header, origin).get(0);
        String result = CookieUtil.encodeCookie(src);
        HttpCookie httpcookie = HttpCookie.parse(result).get(0);
        assertTrue("maxAge should be greater than 86395, actual value " + httpcookie.getMaxAge(),
                httpcookie.getMaxAge() > 86395);
        assertTrue("maxAge should be lower than 86401, actual value " + httpcookie.getMaxAge(),
                httpcookie.getMaxAge() < 86401);
    }

    public void testRewriteCookieExpiresLongTime() throws Exception {
        String expires = format.format(new Date(System.currentTimeMillis() + 15552000000L));
        Header header =
                new BasicHeader("Set-Cookie", "K_66638=121203111217326896; Domain=.foo.com; Expires=" + expires
                        + "; Path=/");
        CookieOrigin origin = new CookieOrigin("www.foo.com", 80, "/", false);
        Cookie src = cookieSpec.parse(header, origin).get(0);
        String result = CookieUtil.encodeCookie(src);
        HttpCookie httpcookie = HttpCookie.parse(result).get(0);
        assertTrue("maxAge should be greater than 15551995, actual value " + httpcookie.getMaxAge(),
                httpcookie.getMaxAge() > 15551995);
        assertTrue("maxAge should be lower than 15552001, actual value " + httpcookie.getMaxAge(),
                httpcookie.getMaxAge() < 15552001);
    }

    public void testCookieValueWithoutSpacesIsNotQuoted() throws Exception {
        Cookie cookie = new BasicClientCookie("name", "valuewithoutspaces");
        String result = CookieUtil.encodeCookie(cookie);
        assertEquals("name=valuewithoutspaces", result);
    }

    public void testCookieValueWithSpacesVersion1IsQuoted() throws Exception {
        String cookieString = "myCookie=\"value with spaces\"; Domain=www.foo.com; Path=/; Version=1";
        Header header = new BasicHeader("Set-Cookie", cookieString);
        CookieOrigin origin = new CookieOrigin("www.foo.com", 80, "/", false);
        Cookie cookie = cookieSpec.parse(header, origin).get(0);
        cookieSpec.validate(cookie, origin);
        String result = CookieUtil.encodeCookie(cookie);
        assertEquals(cookieString, result);
    }

    public void testCookieValueWithoutQuotesIsNotQuoted() throws Exception {
        String cookieString = "myCookie=value; Domain=www.foo.com; Path=/";
        Header header = new BasicHeader("Set-Cookie", cookieString);
        CookieOrigin origin = new CookieOrigin("www.foo.com", 80, "/", false);
        Cookie cookie = cookieSpec.parse(header, origin).get(0);
        String result = CookieUtil.encodeCookie(cookie);
        assertEquals(cookieString, result);
    }

    public void testCookieValueWithoutDotsAndSlashIsNotQuoted() throws Exception {
        String cookieString = "myCookie=value./; Domain=www.foo.com; Path=/";
        Header header = new BasicHeader("Set-Cookie", cookieString);
        CookieOrigin origin = new CookieOrigin("www.foo.com", 80, "/", false);
        Cookie cookie = cookieSpec.parse(header, origin).get(0);
        String result = CookieUtil.encodeCookie(cookie);
        assertEquals(cookieString, result);
    }
}
