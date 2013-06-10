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
package org.esigate.servlet;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.apache.http.Header;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.impl.cookie.DateUtils;
import org.apache.http.message.BasicHeader;

public class HttpServletMediatorTest extends TestCase {
	private SimpleDateFormat format;
	
	@Override
	protected void setUp() throws Exception {
	    format = new SimpleDateFormat(DateUtils.PATTERN_RFC1123, Locale.US);
	    format.setTimeZone(TimeZone.getTimeZone("GMT"));
		super.setUp();
	}

	public void testRewriteCookieExpires() throws Exception {
		CookieSpec cookieSpec = new BrowserCompatSpec();
		String expires = format.format(new Date(System.currentTimeMillis() + 86400000));
		Header header = new BasicHeader("Set-Cookie", "K_lm_66638=121203111217326896; Domain=.foo.com; Expires=" + expires + "; Path=/");
		CookieOrigin origin = new CookieOrigin("www.foo.com", 80, "/", false);
		Cookie src = cookieSpec.parse(header, origin).get(0);
		javax.servlet.http.Cookie result = HttpServletMediator.rewriteCookie(src);
		assertTrue(result.getMaxAge() > 86398);
		assertTrue(result.getMaxAge() < 86401);
	}

	public void testRewriteCookieExpiresLongTime() throws Exception {
		CookieSpec cookieSpec = new BrowserCompatSpec();
		String expires = format.format(new Date(System.currentTimeMillis() + 15552000000l));
		Header header = new BasicHeader("Set-Cookie", "K_66638=121203111217326896; Domain=.foo.com; Expires=" + expires + "; Path=/");
		CookieOrigin origin = new CookieOrigin("www.foo.com", 80, "/", false);
		Cookie src = cookieSpec.parse(header, origin).get(0);
		javax.servlet.http.Cookie result = HttpServletMediator.rewriteCookie(src);
		assertTrue(result.getMaxAge() > 15551998);
		assertTrue(result.getMaxAge() < 15552001);
	}

}
