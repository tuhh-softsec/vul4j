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

package org.esigate.http;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.message.BasicHttpResponse;
import org.esigate.ConfigurationException;
import org.esigate.Driver;
import org.esigate.HttpErrorPage;
import org.esigate.Parameters;
import org.esigate.RequestExecutor;
import org.esigate.cache.CacheConfigHelper;
import org.esigate.cookie.CookieManager;
import org.esigate.events.EventManager;
import org.esigate.events.impl.FragmentEvent;
import org.esigate.extension.ExtensionFactory;
import org.esigate.http.cookie.CustomBrowserCompatSpecFactory;
import org.esigate.impl.DriverRequest;
import org.esigate.impl.UrlRewriter;
import org.esigate.util.HttpRequestHelper;
import org.esigate.util.UriUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HttpClientHelper is responsible for creating Apache HttpClient requests from incoming requests. It can copy a request
 * with its method and entity or simply create a new GET request to the same URI. Some parameters enable to control
 * which http headers have to be copied and whether or not to preserve the original host header.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public final class HttpClientRequestExecutor implements RequestExecutor {
    private static final Logger LOG = LoggerFactory.getLogger(HttpClientRequestExecutor.class);
    private static final Set<String> SIMPLE_METHODS = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
            "GET", "HEAD", "OPTIONS", "TRACE", "DELETE")));
    private static final Set<String> ENTITY_METHODS = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
            "POST", "PUT", "PROPFIND", "PROPPATCH", "MKCOL", "COPY", "MOVE", "LOCK", "UNLOCK")));
    private boolean preserveHost;
    private CookieManager cookieManager;
    private HttpClient httpClient;
    private EventManager eventManager = null;
    private HeaderManager headerManager;
    private int connectTimeout;
    private int socketTimeout;
    private HttpHost firstBaseUrlHost;

    /**
     * Builder class used to produce an immutable instance.
     * 
     * @author Francois-Xavier Bonnet
     * 
     */
    public static final class HttpClientHelperBuilder implements RequestExecutorBuilder {
        private EventManager eventManager;
        private Properties properties;
        private Driver driver;
        private HttpClientConnectionManager connectionManager;
        private CookieManager cookieManager;
        private UrlRewriter urlRewriter;

        @Override
        public HttpClientHelperBuilder setDriver(Driver pDriver) {
            this.driver = pDriver;
            return this;
        }

        @Override
        public HttpClientHelperBuilder setProperties(Properties pProperties) {
            this.properties = pProperties;
            return this;
        }

        @Override
        public HttpClientRequestExecutor build() {
            if (eventManager == null) {
                throw new ConfigurationException("eventManager is mandatory");
            }
            if (driver == null) {
                throw new ConfigurationException("driver is mandatory");
            }
            if (properties == null) {
                throw new ConfigurationException("properties is mandatory");
            }
            if (urlRewriter == null) {
                throw new ConfigurationException("urlRewriter is mandatory");
            }
            HttpClientRequestExecutor httpClientHelper = new HttpClientRequestExecutor();
            httpClientHelper.eventManager = eventManager;
            httpClientHelper.preserveHost = Parameters.PRESERVE_HOST.getValue(properties);
            httpClientHelper.headerManager = new HeaderManager(urlRewriter);
            if (cookieManager == null) {
                cookieManager = ExtensionFactory.getExtension(properties, Parameters.COOKIE_MANAGER, driver);
            }
            httpClientHelper.cookieManager = cookieManager;
            httpClientHelper.connectTimeout = Parameters.CONNECT_TIMEOUT.getValue(properties);
            httpClientHelper.socketTimeout = Parameters.SOCKET_TIMEOUT.getValue(properties);
            httpClientHelper.httpClient = buildHttpClient(driver, properties, eventManager, connectionManager);
            String firstBaseURL = Parameters.REMOTE_URL_BASE.getValue(properties)[0];
            httpClientHelper.firstBaseUrlHost = UriUtils.extractHost(firstBaseURL);
            return httpClientHelper;
        }

        @Override
        public HttpClientHelperBuilder setContentTypeHelper(ContentTypeHelper contentTypeHelper) {
            return this;
        }

        public HttpClientHelperBuilder setConnectionManager(HttpClientConnectionManager pConnectionManager) {
            this.connectionManager = pConnectionManager;
            return this;
        }

        @Override
        public HttpClientHelperBuilder setEventManager(EventManager pEventManager) {
            this.eventManager = pEventManager;
            return this;
        }

        public HttpClientHelperBuilder setCookieManager(CookieManager pCookieManager) {
            this.cookieManager = pCookieManager;
            return this;
        }

        public HttpClientHelperBuilder setUrlRewriter(UrlRewriter urlRewriter) {
            this.urlRewriter = urlRewriter;
            return this;
        }
    }

    private static HttpClient buildHttpClient(Driver driver, Properties properties, EventManager eventManager,
            HttpClientConnectionManager connectionManager) {
        HttpHost proxyHost = null;
        Credentials proxyCredentials = null;
        // Proxy settings
        String proxyHostParameter = Parameters.PROXY_HOST.getValue(properties);
        if (proxyHostParameter != null) {
            int proxyPort = Parameters.PROXY_PORT.getValue(properties);
            proxyHost = new HttpHost(proxyHostParameter, proxyPort);
            String proxyUser = Parameters.PROXY_USER.getValue(properties);
            if (proxyUser != null) {
                String proxyPassword = Parameters.PROXY_PASSWORD.getValue(properties);
                proxyCredentials = new UsernamePasswordCredentials(proxyUser, proxyPassword);
            }
        }

        ProxyingHttpClientBuilder httpClientBuilder = new ProxyingHttpClientBuilder();

        httpClientBuilder.setProperties(properties);

        httpClientBuilder.setMaxConnPerRoute(Parameters.MAX_CONNECTIONS_PER_HOST.getValue(properties));
        httpClientBuilder.setMaxConnTotal(Parameters.MAX_CONNECTIONS_PER_HOST.getValue(properties));
        httpClientBuilder.setRedirectStrategy(driver.getRedirectStrategy());

        // Proxy settings
        if (proxyHost != null) {
            httpClientBuilder.setProxy(proxyHost);
            if (proxyCredentials != null) {
                CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(new AuthScope(proxyHost), proxyCredentials);
                httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            }
        }

        // Cache settings
        boolean useCache = Parameters.USE_CACHE.getValue(properties);
        httpClientBuilder.setUseCache(Parameters.USE_CACHE.getValue(properties));
        if (useCache) {
            httpClientBuilder.setHttpCacheStorage(CacheConfigHelper.createCacheStorage(properties));
            httpClientBuilder.setCacheConfig(CacheConfigHelper.createCacheConfig(properties));
        }

        // Event manager
        httpClientBuilder.setEventManager(eventManager);

        // Used for tests to skip connection manager and return hard coded
        // responses
        if (connectionManager != null) {
            httpClientBuilder.setConnectionManager(connectionManager);
        }

        Registry<CookieSpecProvider> r =
                RegistryBuilder
                        .<CookieSpecProvider>create()
                        .register(CookieSpecs.BEST_MATCH, new BestMatchSpecFactory())
                        .register(CookieSpecs.BROWSER_COMPATIBILITY, new BrowserCompatSpecFactory())
                        .register(CustomBrowserCompatSpecFactory.CUSTOM_BROWSER_COMPATIBILITY,
                                new CustomBrowserCompatSpecFactory()).build();

        RequestConfig config =
                RequestConfig.custom().setCookieSpec(CustomBrowserCompatSpecFactory.CUSTOM_BROWSER_COMPATIBILITY)
                        .build();

        httpClientBuilder.setDefaultCookieSpecRegistry(r).setDefaultRequestConfig(config);
        return httpClientBuilder.build();
    }

    public static HttpClientHelperBuilder builder() {
        return new HttpClientHelperBuilder();
    }

    private HttpClientRequestExecutor() {
    }

    public OutgoingRequest createHttpRequest(DriverRequest originalRequest, String uri, boolean proxy) {
        // Extract the host in the URI. This is the host we have to send the
        // request to physically.
        HttpHost physicalHost = UriUtils.extractHost(uri);

        if (!originalRequest.isExternal()) {
            if (preserveHost) {
                // Preserve host if required
                HttpHost virtualHost = HttpRequestHelper.getHost(originalRequest);
                // Rewrite the uri with the virtualHost
                uri = UriUtils.rewriteURI(uri, virtualHost);
            } else {
                uri = UriUtils.rewriteURI(uri, firstBaseUrlHost);
            }
        }

        RequestConfig.Builder builder = RequestConfig.custom();
        builder.setConnectTimeout(connectTimeout);
        builder.setSocketTimeout(socketTimeout);
        builder.setCircularRedirectsAllowed(true);

        // Use browser compatibility cookie policy. This policy is the closest
        // to the behavior of a real browser.
        builder.setCookieSpec(CustomBrowserCompatSpecFactory.CUSTOM_BROWSER_COMPATIBILITY);

        builder.setRedirectsEnabled(!proxy);
        RequestConfig config = builder.build();

        OutgoingRequestContext context = new OutgoingRequestContext();

        String method = "GET";
        if (proxy) {
            method = originalRequest.getRequestLine().getMethod().toUpperCase();
        }
        OutgoingRequest httpRequest =
                new OutgoingRequest(method, uri, originalRequest.getProtocolVersion(), originalRequest, config, context);
        if (ENTITY_METHODS.contains(method)) {
            httpRequest.setEntity(originalRequest.getEntity());
        } else if (!SIMPLE_METHODS.contains(method)) {
            throw new UnsupportedHttpMethodException(method + " " + uri);
        }

        // We use the same user-agent and accept headers that the one sent by
        // the browser as some web sites generate different pages and scripts
        // depending on the browser
        headerManager.copyHeaders(originalRequest, httpRequest);

        context.setPhysicalHost(physicalHost);
        context.setOutgoingRequest(httpRequest);
        context.setProxy(proxy);

        return httpRequest;
    }

    /**
     * Execute a HTTP request
     * <p>
     * No special handling.
     * 
     * @param httpRequest
     *            HTTP request to execute.
     * @return HTTP response.
     */
    @Override
    public CloseableHttpResponse execute(OutgoingRequest httpRequest) {
        OutgoingRequestContext context = httpRequest.getContext();
        IncomingRequest originalRequest = httpRequest.getOriginalRequest().getOriginalRequest();

        if (cookieManager != null) {
            CookieStore cookieStore = new RequestCookieStore(cookieManager, httpRequest.getOriginalRequest());
            context.setCookieStore(cookieStore);
        }
        HttpResponse result;
        // Create request event
        FragmentEvent event = new FragmentEvent(originalRequest, httpRequest, context);
        // EVENT pre
        eventManager.fire(EventManager.EVENT_FRAGMENT_PRE, event);
        // If exit : stop immediately.
        if (!event.isExit()) {
            // Proceed to request only if extensions did not inject a response.
            if (event.getHttpResponse() == null) {
                if (httpRequest.containsHeader(HttpHeaders.EXPECT)) {
                    event.setHttpResponse(HttpErrorPage.generateHttpResponse(HttpStatus.SC_EXPECTATION_FAILED,
                            "'Expect' request header is not supported"));
                } else {
                    try {
                        HttpHost physicalHost = context.getPhysicalHost();
                        HttpResponse response = httpClient.execute(physicalHost, httpRequest, context);
                        result = new BasicHttpResponse(response.getStatusLine());
                        headerManager.copyHeaders(httpRequest, originalRequest, response, result);
                        result.setEntity(response.getEntity());
                    } catch (IOException e) {
                        result = HttpErrorPage.generateHttpResponse(e);
                        LOG.warn(httpRequest.getRequestLine() + " -> " + result.getStatusLine().toString());
                    }
                    event.setHttpResponse(BasicCloseableHttpResponse.adapt(result));
                }
            }
            // EVENT post
            eventManager.fire(EventManager.EVENT_FRAGMENT_POST, event);
        }
        return event.getHttpResponse();
    }

    /**
     * Execute a HTTP request and handle errors as HttpErrorPage exceptions.
     * 
     * @param originalRequest
     *            HTTP original request.
     * @return HTTP response
     * @throws HttpErrorPage
     *             if server returned no response or if the response as an error status code.
     */
    @Override
    public CloseableHttpResponse
            createAndExecuteRequest(DriverRequest originalRequest, String targetUrl, boolean proxy)
                    throws HttpErrorPage {
        OutgoingRequest httpRequest = createHttpRequest(originalRequest, targetUrl, proxy);
        CloseableHttpResponse httpResponse = execute(httpRequest);
        if (httpResponse == null) {
            throw new HttpErrorPage(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Request was cancelled by server",
                    "Request was cancelled by server");
        }
        if (HttpResponseUtils.isError(httpResponse)) {
            throw new HttpErrorPage(httpResponse);
        }
        return httpResponse;
    }

}
