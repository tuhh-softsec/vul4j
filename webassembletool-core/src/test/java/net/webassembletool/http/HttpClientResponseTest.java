package net.webassembletool.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.protocol.HttpContext;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;

public class HttpClientResponseTest extends TestCase {
	private HttpClientResponse tested;

	private IMocksControl control;
	private HttpHost httpHost;
	private HttpRequest httpRequest;
	private HttpClient httpClient;
	private HttpContext httpContext;
	private HttpResponse httpResponse;

	@Override
	protected void setUp() {
		control = EasyMock.createControl();
		httpRequest = control.createMock(HttpRequest.class);
		httpClient = control.createMock(HttpClient.class);
		httpContext = control.createMock(HttpContext.class);
		httpResponse = control.createMock(HttpResponse.class);

		httpHost = new HttpHost("httpHost", 1234);
	}

	@Override
	protected void tearDown() {
		control = null;
		httpRequest = null;
		httpClient = null;
		httpContext = null;
		httpResponse = null;
		httpHost = null;

		tested = null;
	}

	public void testGetHeaderNames() throws IOException {
		// initial set up
		StatusLine statusLine = new BasicStatusLine(new ProtocolVersion("p", 1, 2), HttpServletResponse.SC_MOVED_PERMANENTLY,
				"reason");
		EasyMock.expect(httpClient.execute(httpHost, httpRequest, httpContext)).andReturn(httpResponse);
		EasyMock.expect(httpResponse.getStatusLine()).andReturn(statusLine);
		EasyMock.expect(httpResponse.getStatusLine()).andReturn(statusLine);
		EasyMock.expect(httpResponse.getEntity()).andReturn(null);
		EasyMock.expect(httpResponse.getFirstHeader(HttpHeaders.LOCATION)).andReturn(new BasicHeader("h", "value"));
		// getHeaderNames behaviour
		Header[] headers = new Header[] { new BasicHeader("h1", "value 1"), new BasicHeader("h1", "value 2"),
				new BasicHeader("h2", "value 3") };
		EasyMock.expect(httpResponse.getAllHeaders()).andReturn(headers);
		control.replay();

		tested = new HttpClientResponse(httpHost, httpRequest, httpClient, httpContext);
		Collection<String> actual = tested.getHeaderNames();

		assertNotNull(actual);
		String names[] = toArray(actual);
		assertEquals(2, names.length);
		assertEquals("h1", names[0]);
		assertEquals("h2", names[1]);
		control.verify();
	}

	private String[] toArray(Collection<String> src) {
		List<String> result = new ArrayList<String>();
		for (String s : src) {
			result.add(s);
		}
		Collections.sort(result);
		return result.toArray(new String[result.size()]);
	}

}
