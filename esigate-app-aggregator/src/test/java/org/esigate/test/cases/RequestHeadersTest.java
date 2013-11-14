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

package org.esigate.test.cases;

import junit.framework.TestCase;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

/**
 * Request headers forwarding or discarding tests. Standard HTTP headers are
 * defined in:
 * <ul>
 * <li><a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html">RFC
 * 2616</a></li>
 * <li><a href="http://www.ietf.org/rfc/rfc2817.txt">RFC 2817</a></li>
 * </ul>
 * 
 * @author Nicolas Richeton
 * @author Francois-Xavier Bonnet
 */
public class RequestHeadersTest extends TestCase {
	private final static String APPLICATION_PATH = "http://localhost:8080/esigate-app-aggregator/";

	public void assertHeaderDiscarded(String name) throws Exception {
		String resp = sendRequestWithHeader(name, "dummy");
		assertEquals("HTTP header " + name + " should not be forwarded", "", resp);
	}

	public void assertHeaderForwarded(String name) throws Exception {
		String resp = sendRequestWithHeader(name, "dummy");
		assertEquals("HTTP header " + name + " should be forwarded", name.toLowerCase() + ": dummy", resp);
	}

	public String sendRequestWithHeader(String name, String value) throws Exception {
		WebConversation webConversation = new WebConversation();
		WebRequest req = new GetMethodWebRequest(APPLICATION_PATH + "nocache/ag1/request-headers.jsp?name=" + name);
		req.setHeaderField(name, value);
		WebResponse resp = webConversation.getResponse(req);
		return resp.getText();
	}

	public void testAccept() throws Exception {
		assertHeaderForwarded("Accept");
	}

	public void testAcceptCharset() throws Exception {
		assertHeaderForwarded("Accept-Charset");
	}

	public void testAcceptEncoding() throws Exception {
		assertHeaderForwarded("Accept-Encoding");
	}

	public void testAcceptLanguage() throws Exception {
		assertHeaderForwarded("Accept-Language");
	}

	/**
	 * "Authorization" header should not be forwarded because authentication
	 * must be handled by an authentication handler
	 * 
	 * @throws Exception
	 */
	public void testAuthorization() throws Exception {
		assertHeaderDiscarded("Authorization");
	}

	public void testConnection() throws Exception {
		String resp = sendRequestWithHeader("Connection", "dummy");
		assertEquals(
				"HTTP header Connection should always be set to 'Keep-Alive' by the Http client for better performance",
				"connection: keep-alive", resp.toLowerCase());
	}

	/**
	 * It looks like a gzip request body will not be unzipped by the application
	 * server so we have to forward "Content-Encoding" header
	 * 
	 * @throws Exception
	 */
	public void testContentEncoding() throws Exception {
		String resp = sendRequestWithHeader("Content-Encoding", "gzip");
		assertEquals("Content-Encoding request header should be forwarded", "content-encoding: gzip",
				resp.toLowerCase());
	}

	/**
	 * We never change the request body so we have to forward "Content-Language"
	 * header
	 * 
	 * @throws Exception
	 */
	public void testContentLanguage() throws Exception {
		assertHeaderForwarded("Content-Language");
	}

	/**
	 * We never change request body (unlike for responses) The only case when
	 * the request body may be changed is when it has a chunked
	 * "Transfer-Encoding" but in this case the "Content-Length" is not set
	 * 
	 * @throws Exception
	 */
	public void testContentLength() throws Exception {
		// This header will be set by http client automatically for POST
		// requests so we will find it in the request even if it is not
		// forwarded
		WebConversation webConversation = new WebConversation();
		WebRequest req = new PostMethodWebRequest(APPLICATION_PATH
				+ "nocache/ag1/request-headers.jsp?name=Content-Length");
		WebResponse resp = webConversation.getResponse(req);
		String result = resp.getText();
		assertEquals("Content-Length request header should be set for POST requests", "content-length: 0",
				result.toLowerCase());
	}

	/**
	 * We never change the request body so we have to forward "Content-MD5"
	 * header
	 * 
	 * @throws Exception
	 */
	public void testContentMD5() throws Exception {
		assertHeaderForwarded("Content-MD5");
	}

	/**
	 * We never change the request body so we have to forward "Content-Range"
	 * header. For responses it is different because the cache does not support
	 * range requests.
	 * 
	 * @throws Exception
	 */
	public void testContentRange() throws Exception {
		assertHeaderForwarded("Content-Range");
	}

	public void testContentType() throws Exception {
		assertHeaderForwarded("Content-Type");
	}

	public void testCookie() throws Exception {
		// In driver.properties, cookie "test0" is supposed to be forwarded but
		// not test4
		String resp = sendRequestWithHeader("Cookie", "test0=dummy,test4=dummy");
		assertEquals("HTTP header Cookie should not be forwarded as is due to cookies filtering",
				"cookie: test0=dummy", resp.toLowerCase());
	}

	public void testDate() throws Exception {
		assertHeaderForwarded("Date");
	}

	public void testExpect() throws Exception {
		// FIXME It looks like Expect header is not sent by HttpUnit when run
		// under maven. There must be a dependency to an old version of HttpUnit
		// because it works otherwise.
		// WebConversation webConversation = new WebConversation();
		// WebRequest req = new GetMethodWebRequest(APPLICATION_PATH
		// + "request-headers.jsp?name=expect");
		// req.setHeaderField("Expect", "dummy");
		// try {
		// webConversation.getResponse(req);
		// fail("There should have been an HttpException");
		// } catch (HttpException e) {
		// assertEquals(
		// "Expect header is not supported, should send a '417 Expectation failed'",
		// 417, e.getResponseCode());
		// }
	}

	public void testFrom() throws Exception {
		assertHeaderForwarded("From");
	}

	public void testHost() throws Exception {
		WebConversation webConversation = new WebConversation();
		WebRequest req = new GetMethodWebRequest(APPLICATION_PATH.replace("localhost", "127.0.0.1")
				+ "nocache/ag1/request-headers.jsp?name=host");
		req.setHeaderField("Host", "127.0.0.1:8080");
		WebResponse resp = webConversation.getResponse(req);
		String result = resp.getText();
		assertEquals("As 'preserveHost' is not set to 'true', Host should be the one defined in "
				+ "driver.properties, not the one defined in the request", "host: localhost:8080", result.toLowerCase());
		req = new GetMethodWebRequest(APPLICATION_PATH.replace("localhost", "127.0.0.1")
				+ "preservehost/request-headers.jsp?name=host");
		req.setHeaderField("Host", "127.0.0.1:8080");
		resp = webConversation.getResponse(req);
		result = resp.getText();
		assertEquals("As 'preserveHost' is set to 'true', Host should be forwarded", "host: 127.0.0.1:8080",
				result.toLowerCase());
	}

	/**
	 * "If-Match" header is used mainly by PUT requests to perform an action
	 * only if the resource has not been modified. We can forward this header.
	 * 
	 * @throws Exception
	 */
	public void testIfMatch() throws Exception {
		assertHeaderForwarded("If-Match");
	}

	/**
	 * Ranges are not supported, we will not forward this header and send the
	 * entire resource in the response.
	 * 
	 * @throws Exception
	 */
	public void testIfRange() throws Exception {
		assertHeaderForwarded("If-Range");
	}

	public void testIfUnmodifiedSince() throws Exception {
		assertHeaderForwarded("If-Unmodified-Since");
	}

	public void testMaxForwards() throws Exception {
		assertHeaderDiscarded("Max-Forwards");
	}

	public void testPragma() throws Exception {
		assertHeaderDiscarded("Pragma");
	}

	public void testProxyAuthorization() throws Exception {
		assertHeaderDiscarded("Proxy-Authorization");
	}

	/**
	 * Ranges are not supported, we will not forward this header and send the
	 * entire resource in the response.
	 * 
	 * @throws Exception
	 */
	public void testRange() throws Exception {
		assertHeaderForwarded("Range");
	}

	/**
	 * Referer header should be rewritten
	 * 
	 * @throws Exception
	 */
	public void testReferer() throws Exception {
		String result = sendRequestWithHeader("Referer", APPLICATION_PATH + "nocache/ag1/dummy");
		assertEquals("Referer should be rewritten ('aggregator' replaced with 'aggregated1')",
				"referer: http://localhost:8080/esigate-app-aggregated1/dummy", result.toLowerCase());
	}

	public void testTe() throws Exception {
		assertHeaderDiscarded("TE");
	}

	/**
	 * Trailer header field is used for chunked requests, it should not be
	 * forwarded as the server will automatically unchunk requests.
	 * 
	 * @throws Exception
	 */
	public void testTrailer() throws Exception {
		assertHeaderDiscarded("Trailer");
	}

	/**
	 * It looks like servers like Tomcat will decode the stream so we should not
	 * forward it. It is not described in servlet API specification but
	 * according to RFC 2616 it is mandatory for servers: "All HTTP/1.1
	 * applications MUST be able to receive and decode the chunked
	 * transfer-coding" (<a
	 * href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.6.1">RFC
	 * 2616 sec 3.6.1</a>)
	 * 
	 * In this test, we send a POST request with a chunked body. The application
	 * server will decode it and the target server will receive the data
	 * unchunked. If EsiGate was forwarding the "Transfer-Encoding" header, it
	 * would break the request as it is not chunked anymore when it arrives to
	 * final destination.
	 * 
	 * @throws Exception
	 */
	public void testTransferEncoding() throws Exception {
		// Cannot test it
	}

	/**
	 * Any unknown http header should be forwarded to to the server
	 * 
	 * @throws Exception
	 */
	public void testUnknownHeader() throws Exception {
		assertHeaderForwarded("Unknown-Header");
	}

	public void testUpgrade() throws Exception {
		assertHeaderDiscarded("Upgrade");
	}

	public void testUserAgent() throws Exception {
		assertHeaderForwarded("User-Agent");
	}

	public void testWarning() throws Exception {
		assertHeaderForwarded("Warning");
	}

	public void testXForwardedFor() throws Exception {
		String name = "X-Forwarded-For";
		String resp = sendRequestWithHeader(name, "dummy");
		assertEquals("HTTP header " + name + " should be forwarded", name.toLowerCase() + ": dummy, 127.0.0.1", resp);
	}

	public void testXForwardedForNotPresent() throws Exception {
		WebConversation webConversation = new WebConversation();
		WebRequest req = new GetMethodWebRequest(APPLICATION_PATH
				+ "nocache/ag1/request-headers.jsp?name=X-Forwarded-For");
		WebResponse resp = webConversation.getResponse(req);
		if (!resp.getText().toLowerCase().startsWith("x-forwarded-for: 127.0.0.1")) {
			fail("X-Forwarded-For header should be generated if not present in request. Header value: '"
					+ resp.getText() + "'");
		}
	}

}
