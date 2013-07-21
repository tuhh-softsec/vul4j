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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.http.Header;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.message.BasicHeader;
import org.esigate.http.MockHttpServletRequestBuilder;
import org.esigate.test.http.HttpResponseBuilder;

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
		Header header = new BasicHeader("Set-Cookie", "K_lm_66638=121203111217326896; Domain=.foo.com; Expires="
				+ expires + "; Path=/");
		CookieOrigin origin = new CookieOrigin("www.foo.com", 80, "/", false);
		Cookie src = cookieSpec.parse(header, origin).get(0);
		javax.servlet.http.Cookie result = HttpServletMediator.rewriteCookie(src);
		assertTrue(result.getMaxAge() > 86398);
		assertTrue(result.getMaxAge() < 86401);
	}

	public void testRewriteCookieExpiresLongTime() throws Exception {
		CookieSpec cookieSpec = new BrowserCompatSpec();
		String expires = format.format(new Date(System.currentTimeMillis() + 15552000000l));
		Header header = new BasicHeader("Set-Cookie", "K_66638=121203111217326896; Domain=.foo.com; Expires=" + expires
				+ "; Path=/");
		CookieOrigin origin = new CookieOrigin("www.foo.com", 80, "/", false);
		Cookie src = cookieSpec.parse(header, origin).get(0);
		javax.servlet.http.Cookie result = HttpServletMediator.rewriteCookie(src);
		assertTrue(result.getMaxAge() > 15551998);
		assertTrue(result.getMaxAge() < 15552001);
	}

	/**
	 * Ensure there is no exception when trying to create a session outside of a
	 * request (during background revalidation). Expected behavior is no
	 * exception, but value not set.
	 * 
	 * @see "https://sourceforge.net/apps/mantisbt/webassembletool/view.php?id=229"
	 * @throws Exception
	 */
	public void testSetAttributeNoSession() throws Exception {
		HttpServletRequest request = new MockHttpServletRequestBuilder().protocolVersion("HTTP/1.0").method("GET")
				.session(null).build();
		HttpServletResponse response = mock(HttpServletResponse.class);
		ServletOutputStream outputStream = new ServletOutputStream() {

			ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();

			@Override
			public void write(int b) throws IOException {
				this.byteOutputStream.write(b);
			}

			public ByteArrayOutputStream getOutputStream() {
				return this.byteOutputStream;
			}
		};
		when(response.getOutputStream()).thenReturn(outputStream);
		ServletContext context = mock(ServletContext.class);

		HttpServletMediator mediator = new HttpServletMediator(request, response, context);
		mediator.sendResponse(new HttpResponseBuilder().entity("Response").build());

		mediator.setSessionAttribute("test", "value");

		// Previous method should have no effect since session cannot be
		// created.
		Assert.assertNull(mediator.getSessionAttribute("test"));
	}

}
