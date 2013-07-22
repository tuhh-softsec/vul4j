package org.esigate.test.driver;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.conn.HttpClientConnectionManager;
import org.esigate.Driver;
import org.esigate.DriverFactory;
import org.esigate.HttpErrorPage;
import org.esigate.Parameters;
import org.esigate.Renderer;
import org.esigate.cookie.CookieManager;
import org.esigate.events.EventManager;
import org.esigate.extension.ExtensionFactory;
import org.esigate.http.HttpClientHelper;
import org.esigate.test.TestUtils;
import org.esigate.test.conn.IResponseHandler;
import org.esigate.test.conn.MockConnectionManager;
import org.esigate.test.http.HttpRequestBuilder;
import org.esigate.test.http.HttpResponseBuilder;

/**
 * Base class for end-to-end testing of Esigate.
 * 
 * <p>
 * Provides methods to create Driver, HttpRequest and HttpResponses.
 * 
 * @author Nicolas Richeton
 * 
 */
public abstract class AbstractDriverTestCase extends TestCase {

	/**
	 * Create a Driver instance with a custom response handler.
	 * 
	 * @param properties
	 * @param responseHandler
	 * @return
	 */
	protected static Driver createMockDriver(Properties properties, IResponseHandler responseHandler) {
		MockConnectionManager connManager = new MockConnectionManager();
		connManager.setResponseHandler(responseHandler);
		return createMockDriver(properties, connManager);
	}

	/**
	 * Create a Driver instance which will always return the same response from
	 * backend.
	 * 
	 * @param properties
	 * @param response
	 * @return
	 */
	protected static Driver createMockDriver(Properties properties, HttpResponse response) {
		MockConnectionManager connManager = new MockConnectionManager();
		connManager.setResponse(response);
		return createMockDriver(properties, connManager);
	}

	/**
	 * Create a Driver instance with a custom connection Manager.
	 * 
	 * @param properties
	 * @param connectionManager
	 * @return
	 */
	protected static Driver createMockDriver(Properties properties, HttpClientConnectionManager connectionManager) {
		return createMockDriver(properties, connectionManager, "tested");
	}

	/**
	 * Create a Driver instance with a custom connection Manager.
	 * 
	 * @param properties
	 * @param connectionManager
	 * @param name
	 *            name of the Driver instance
	 * @return
	 */
	protected static Driver createMockDriver(Properties properties, HttpClientConnectionManager connectionManager,
			String name) {
		CookieManager cookieManager = ExtensionFactory.getExtension(properties, Parameters.COOKIE_MANAGER, null);

		HttpClientHelper httpClientHelper = new HttpClientHelper(new EventManager(name), cookieManager, properties,
				connectionManager);
		Driver driver = new Driver(name, properties, httpClientHelper);
		DriverFactory.put(name, driver);
		return driver;
	}

	/**
	 * Create a fluent-style builder for HttpResponse.
	 * 
	 * @return a HttpResponseBuilder
	 */
	public static HttpResponseBuilder createHttpResponse() {
		return new HttpResponseBuilder();
	}

	/**
	 * Create a fluent-style builder for HttpRequest.
	 * 
	 * @return a HttpRequestBuilder
	 */
	public static HttpRequestBuilder createHttpRequest() {
		return new HttpRequestBuilder();
	}

	/**
	 * Execute
	 * {@link Driver#proxy(String, HttpEntityEnclosingRequest, Renderer...)} on
	 * an HttpRequest
	 * 
	 * 
	 * @param d
	 * @param request
	 *            Request must have been created with a mediator.
	 * @return the response
	 * @throws IOException
	 * @throws HttpErrorPage
	 * @throws URISyntaxException
	 */
	public static HttpResponse driverProxy(Driver d, HttpEntityEnclosingRequest request, Renderer... renderers)
			throws IOException, HttpErrorPage, URISyntaxException {
		String uri = request.getRequestLine().getUri();
		d.proxy(new URI(uri).getPath(), request, renderers);

		return TestUtils.getResponse(request);

		// This is work in progress. Commenting right now.
		// return d.proxy(new URI(uri).getPath(), request);
	}

}
