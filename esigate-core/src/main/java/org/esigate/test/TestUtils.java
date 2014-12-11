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

package org.esigate.test;

import java.io.IOException;
import java.util.Properties;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.RequestLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.message.BasicRequestLine;
import org.esigate.Driver;
import org.esigate.DriverFactory;
import org.esigate.HttpErrorPage;
import org.esigate.Renderer;
import org.esigate.api.ContainerRequestContext;
import org.esigate.http.Http;
import org.esigate.http.HttpClientRequestExecutor;
import org.esigate.http.IncomingRequest;
import org.esigate.impl.DriverRequest;
import org.esigate.impl.UrlRewriter;
import org.esigate.test.conn.IResponseHandler;
import org.esigate.test.conn.MockConnectionManager;
import org.esigate.test.http.HttpResponseBuilder;
import org.esigate.util.UriUtils;

/**
 * Helper class for unit or integration tests.
 * 
 * @author Francois-Xavier Bonnet
 * @author Nicolas Richeton
 * 
 */
public final class TestUtils {

    private TestUtils() {

    }

    /**
     * Creates a mock {@link IncomingRequest}.
     * 
     * @param uri
     *            the uri
     * @return the {@link IncomingRequest}
     */
    public static IncomingRequest.Builder createIncomingRequest(String uri) {
        HttpHost httpHost = UriUtils.extractHost(uri);
        String scheme = httpHost.getSchemeName();
        String host = httpHost.getHostName();
        int port = httpHost.getPort();
        RequestLine requestLine = new BasicRequestLine("GET", uri, HttpVersion.HTTP_1_1);
        IncomingRequest.Builder builder = IncomingRequest.builder(requestLine);
        builder.setContext(new ContainerRequestContext() {
        });
        // Remove default ports
        if (port == -1 || (port == Http.DEFAULT_HTTP_PORT && "http".equals(scheme))
                || (port == Http.DEFAULT_HTTPS_PORT && "https".equals(scheme))) {
            builder.addHeader("Host", host);
        } else {
            builder.addHeader("Host", host + ":" + port);
        }
        builder.setSession(new MockSession());
        return builder;
    }

    /**
     * Creates a mock {@link IncomingRequest}.
     * 
     * @return the {@link IncomingRequest}
     */
    public static IncomingRequest.Builder createIncomingRequest() {
        return createIncomingRequest("http://localhost:8080");
    }

    /**
     * Creates a mock {@link DriverRequest}.
     * 
     * @param uri
     *            the uri
     * @param driver
     *            the {@link Driver}
     * @return the {@link DriverRequest}
     * @throws HttpErrorPage
     *             if an error occurs
     */
    public static DriverRequest createDriverRequest(String uri, Driver driver) throws HttpErrorPage {
        IncomingRequest request = createIncomingRequest(uri).build();
        return new DriverRequest(request, driver, "/");
    }

    /**
     * Creates a mock {@link DriverRequest}.
     * 
     * @param driver
     *            the {@link Driver}
     * @return the {@link DriverRequest}
     * @throws HttpErrorPage
     *             if an error occurs
     */
    public static DriverRequest createDriverRequest(Driver driver) throws HttpErrorPage {
        IncomingRequest request = createIncomingRequest().build();
        return new DriverRequest(request, driver, "/");
    }

    /**
     * Creates a {@link Driver} instance with a custom response handler.
     * 
     * @param properties
     *            the {@link Properties}
     * @param responseHandler
     *            the {@link IResponseHandler}
     * @return new {@link Driver} object
     */
    public static Driver createMockDriver(Properties properties, IResponseHandler responseHandler) {
        MockConnectionManager connManager = new MockConnectionManager();
        connManager.setResponseHandler(responseHandler);
        return createMockDriver(properties, connManager);
    }

    /**
     * Creates a {@link Driver} instance with a mock {@link HttpResponse}.
     * 
     * @param properties
     *            the {@link Properties}
     * @param response
     *            the {@link HttpResponse}
     * @return new {@link Driver} object
     */
    public static Driver createMockDriver(Properties properties, HttpResponse response) {
        MockConnectionManager connManager = new MockConnectionManager();
        connManager.setResponse(response);
        return createMockDriver(properties, connManager);
    }

    /**
     * Create a Driver instance with a custom connection Manager.
     * 
     * @param properties
     *            the {@link Properties}
     * @param connectionManager
     *            the {@link HttpClientConnectionManager}
     * @return new {@link Driver} object
     */
    public static Driver createMockDriver(Properties properties, HttpClientConnectionManager connectionManager) {
        return createMockDriver(properties, connectionManager, "tested");
    }

    /**
     * Create a Driver instance with a custom connection Manager.
     * 
     * @param properties
     *            the {@link Properties}
     * @param connectionManager
     *            the {@link HttpClientConnectionManager}
     * @param name
     *            name of the Driver instance
     * @return new {@link Driver} object
     */
    public static Driver createMockDriver(Properties properties, HttpClientConnectionManager connectionManager,
            String name) {
        UrlRewriter urlRewriter = new UrlRewriter(new Properties());
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
     * Create a fluent-style builder for {@link HttpResponse}.
     * 
     * @return a {@link HttpResponseBuilder}
     */
    public static HttpResponseBuilder createHttpResponse() {
        return new HttpResponseBuilder();
    }

    /**
     * Create a fluent-style builder for {@link IncomingRequest}.
     * 
     * @param uri
     *            the request uri
     * @return the {@link IncomingRequest.Builder}
     */
    public static IncomingRequest.Builder createRequest(String uri) {
        return createIncomingRequest(uri);
    }

    /**
     * Execute {@link Driver#proxy(String, org.esigate.http.IncomingRequest, Renderer...)} on an HttpRequest.
     * 
     * 
     * @param d
     *            {@link Driver}
     * @param request
     *            Request
     * @param renderers
     *            {@link Renderer} which will be applied on response entity.
     * @return the {@link HttpResponse}
     * @throws IOException
     *             if an {@link IOException} occurs
     * @throws HttpErrorPage
     *             if any other Exception
     */
    public static CloseableHttpResponse driverProxy(Driver d, IncomingRequest request, Renderer... renderers)
            throws IOException, HttpErrorPage {
        String uri = request.getRequestLine().getUri();
        return d.proxy(UriUtils.getPath(uri), request, renderers);
    }

}
