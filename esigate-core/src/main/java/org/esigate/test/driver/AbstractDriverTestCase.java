package org.esigate.test.driver;

import java.io.IOException;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.conn.HttpClientConnectionManager;
import org.esigate.Driver;
import org.esigate.DriverFactory;
import org.esigate.HttpErrorPage;
import org.esigate.Renderer;
import org.esigate.http.HttpClientRequestExecutor;
import org.esigate.http.IncomingRequest;
import org.esigate.impl.UrlRewriter;
import org.esigate.test.TestUtils;
import org.esigate.test.conn.IResponseHandler;
import org.esigate.test.conn.MockConnectionManager;
import org.esigate.test.http.HttpResponseBuilder;
import org.esigate.util.UriUtils;
import org.mockito.Mockito;

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
     * @return new Driver object
     */
    protected static Driver createMockDriver(Properties properties, IResponseHandler responseHandler) {
        MockConnectionManager connManager = new MockConnectionManager();
        connManager.setResponseHandler(responseHandler);
        return createMockDriver(properties, connManager);
    }

    /**
     * Create a Driver instance which will always return the same response from backend.
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
     * @return new Driver object
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
     * @return new Driver object
     */
    protected static Driver createMockDriver(Properties properties, HttpClientConnectionManager connectionManager,
            String name) {
        UrlRewriter urlRewriter = Mockito.mock(UrlRewriter.class);
        Driver driver =
                Driver.builder()
                        .setName(name)
                        .setProperties(properties)
                        .setRequestExecutorBuilder(
                                HttpClientRequestExecutor.builder().setConnectionManager(connectionManager)
                                        .setUrlRewriter(urlRewriter)).build();
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

    public static IncomingRequest.Builder createRequest(String uri) {
        return TestUtils.createIncomingRequest(uri);
    }

    /**
     * Execute {@link Driver#proxy(String, org.esigate.http.IncomingRequest, Renderer...)} on an HttpRequest.
     * 
     * 
     * @param d
     *            esigate driver
     * @param request
     *            Request
     * @param renderers
     *            Renderers which will be applied on response entity.
     * @return the response
     * @throws IOException
     * @throws HttpErrorPage
     */
    public static CloseableHttpResponse driverProxy(Driver d, IncomingRequest request, Renderer... renderers)
            throws IOException, HttpErrorPage {
        String uri = request.getRequestLine().getUri();
        return d.proxy(UriUtils.getPath(uri), request, renderers);
    }

}
