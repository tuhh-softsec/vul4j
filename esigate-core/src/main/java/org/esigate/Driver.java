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

package org.esigate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.message.BasicHttpResponse;
import org.esigate.RequestExecutor.RequestExecutorBuilder;
import org.esigate.events.EventManager;
import org.esigate.events.impl.ProxyEvent;
import org.esigate.events.impl.RenderEvent;
import org.esigate.extension.ExtensionFactory;
import org.esigate.http.BasicCloseableHttpResponse;
import org.esigate.http.ContentTypeHelper;
import org.esigate.http.HeaderManager;
import org.esigate.http.HttpClientRequestExecutor;
import org.esigate.http.HttpResponseUtils;
import org.esigate.http.IncomingRequest;
import org.esigate.http.OutgoingRequest;
import org.esigate.http.ResourceUtils;
import org.esigate.impl.DriverRequest;
import org.esigate.impl.UrlRewriter;
import org.esigate.vars.VariablesResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class used to retrieve data from a provider application using HTTP requests. Data can be retrieved as binary
 * streams or as String for text data. To improve performance, the Driver uses a cache that can be configured depending
 * on the needs.
 * 
 * @author Francois-Xavier Bonnet
 * @author Nicolas Richeton
 * @author Sylvain Sicard
 */
public final class Driver {
    private static final String CACHE_RESPONSE_PREFIX = "response_";
    private static final Logger LOG = LoggerFactory.getLogger(Driver.class);
    private static final int MAX_REDIRECTS = 50;
    private DriverConfiguration config;
    private EventManager eventManager;
    private RequestExecutor requestExecutor;
    private ContentTypeHelper contentTypeHelper;
    private UrlRewriter urlRewriter;
    private HeaderManager headerManager;
    private final DefaultRedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    public static class DriverBuilder {
        private Driver driver = new Driver();
        private String name;
        private Properties properties;
        private RequestExecutorBuilder requestExecutorBuilder;

        public Driver build() {
            if (name == null) {
                throw new ConfigurationException("name is mandatory");
            }
            if (properties == null) {
                throw new ConfigurationException("properties is mandatory");
            }
            if (requestExecutorBuilder == null) {
                requestExecutorBuilder = HttpClientRequestExecutor.builder();
            }
            driver.eventManager = new EventManager(name);
            driver.config = new DriverConfiguration(name, properties);
            driver.contentTypeHelper = new ContentTypeHelper(properties);
            // Load extensions.
            ExtensionFactory.getExtensions(properties, Parameters.EXTENSIONS, driver);
            UrlRewriter urlRewriter = new UrlRewriter(properties);
            driver.requestExecutor =
                    requestExecutorBuilder.setDriver(driver).setEventManager(driver.eventManager)
                            .setProperties(properties).setContentTypeHelper(driver.contentTypeHelper).build();
            driver.urlRewriter = urlRewriter;
            driver.headerManager = new HeaderManager(urlRewriter);
            return driver;
        }

        public DriverBuilder setName(String n) {
            this.name = n;
            return this;
        }

        public DriverBuilder setProperties(Properties p) {
            this.properties = p;
            return this;
        }

        public DriverBuilder setRequestExecutorBuilder(RequestExecutorBuilder builder) {
            this.requestExecutorBuilder = builder;
            return this;
        }

    }

    protected Driver() {
    }

    public static DriverBuilder builder() {
        return new DriverBuilder();
    }

    /**
     * Get current event manager for this driver instance.
     * 
     * @return event manager.
     */
    public EventManager getEventManager() {
        return this.eventManager;
    }

    /**
     * Perform rendering on a single url content, and append result to "writer". Automatically follows redirects
     * 
     * @param pageUrl
     *            Address of the page containing the template
     * @param incomingRequest
     *            originating request object
     * @param renderers
     *            the renderers to use in order to transform the output
     * @return The resulting response
     * @throws IOException
     *             If an IOException occurs while writing to the writer
     * @throws HttpErrorPage
     *             If an Exception occurs while retrieving the template
     */
    public CloseableHttpResponse render(String pageUrl, IncomingRequest incomingRequest, Renderer... renderers)
            throws IOException, HttpErrorPage {
        DriverRequest driverRequest = new DriverRequest(incomingRequest, this, pageUrl);

        // Replace ESI variables in URL
        // TODO: should be performed in the ESI extension
        String resultingPageUrl = VariablesResolver.replaceAllVariables(pageUrl, driverRequest);

        String targetUrl = ResourceUtils.getHttpUrlWithQueryString(resultingPageUrl, driverRequest, false);

        String currentValue;
        CloseableHttpResponse response;

        // Retrieve URL
        // Get from cache to prevent multiple request to the same url if
        // multiple fragments are used.

        String cacheKey = CACHE_RESPONSE_PREFIX + targetUrl;
        Pair<String, CloseableHttpResponse> cachedValue = incomingRequest.getAttribute(cacheKey);
        // content and response were not in cache
        if (cachedValue == null) {
            OutgoingRequest outgoingRequest = requestExecutor.createOutgoingRequest(driverRequest, targetUrl, false);
            headerManager.copyHeaders(driverRequest, outgoingRequest);
            response = requestExecutor.execute(outgoingRequest);
            int redirects = MAX_REDIRECTS;
            try {
                while (redirects > 0
                        && redirectStrategy.isRedirected(outgoingRequest, response, outgoingRequest.getContext())) {
                    redirects--;
                    System.err.println("redirect");
                    outgoingRequest =
                            requestExecutor.createOutgoingRequest(
                                    driverRequest,
                                    redirectStrategy.getLocationURI(outgoingRequest, response,
                                            outgoingRequest.getContext()).toString(), false);
                    headerManager.copyHeaders(driverRequest, outgoingRequest);
                    response = requestExecutor.execute(outgoingRequest);
                }
            } catch (ProtocolException e) {
                throw new HttpErrorPage(HttpStatus.SC_BAD_GATEWAY, "Invalid response from server", e);
            }
            response = headerManager.copyHeaders(outgoingRequest, incomingRequest, response);
            currentValue = HttpResponseUtils.toString(response, this.eventManager);
            // Cache
            cachedValue = new ImmutablePair<String, CloseableHttpResponse>(currentValue, response);
            incomingRequest.setAttribute(cacheKey, cachedValue);
        }
        currentValue = cachedValue.getKey();
        response = cachedValue.getValue();

        logAction("render", pageUrl, renderers);

        // Apply renderers
        currentValue = performRendering(pageUrl, driverRequest, response, currentValue, renderers);

        response.setEntity(new StringEntity(currentValue, HttpResponseUtils.getContentType(response)));

        return response;
    }

    /**
     * Log current provider, page and renderers that will be applied.
     * <p>
     * This methods log at the INFO level.
     * <p>
     * You should only call this method if INFO level is enabled.
     * 
     * <pre>
     * if (LOG.isInfoEnabled()) {
     *     logAction(pageUrl, renderers);
     * }
     * </pre>
     * 
     * @param action
     *            Action name (eg. "proxy" or "render")
     * @param onUrl
     *            current page url.
     * @param renderers
     *            array of renderers
     * 
     */
    private void logAction(String action, String onUrl, Renderer[] renderers) {
        if (LOG.isInfoEnabled()) {
            List<String> rendererNames = new ArrayList<String>(renderers.length);
            for (Renderer renderer : renderers) {
                rendererNames.add(renderer.getClass().getName());
            }
            LOG.info("{} provider={} page= {} renderers={}", action, this.config.getInstanceName(), onUrl,
                    rendererNames);
        }
    }

    /**
     * Retrieves a resource from the provider application and transforms it using the Renderer passed as a parameter.
     * 
     * @param relUrl
     *            the relative URL to the resource
     * @param incomingRequest
     *            the request
     * @param renderers
     *            the renderers to use to transform the output
     * @return The resulting response.
     * @throws IOException
     *             If an IOException occurs while writing to the response
     * @throws HttpErrorPage
     *             If the page contains incorrect tags
     */
    public CloseableHttpResponse proxy(String relUrl, IncomingRequest incomingRequest, Renderer... renderers)
            throws IOException, HttpErrorPage {
        DriverRequest driverRequest = new DriverRequest(incomingRequest, this, relUrl);
        driverRequest.setCharacterEncoding(this.config.getUriEncoding());

        // This is used to ensure EVENT_PROXY_POST is called once and only once.
        // there are 3 different cases
        // - Success -> the main code
        // - Error page -> the HttpErrorPage exception
        // - Unexpected error -> Other Exceptions
        boolean postProxyPerformed = false;

        // Create Proxy event
        ProxyEvent e = new ProxyEvent(incomingRequest);

        // Event pre-proxy
        this.eventManager.fire(EventManager.EVENT_PROXY_PRE, e);
        // Return immediately if exit is requested by extension
        if (e.isExit()) {
            return e.getResponse();
        }

        logAction("proxy", relUrl, renderers);

        String url = ResourceUtils.getHttpUrlWithQueryString(relUrl, driverRequest, true);
        OutgoingRequest outgoingRequest = requestExecutor.createOutgoingRequest(driverRequest, url, true);
        headerManager.copyHeaders(driverRequest, outgoingRequest);

        try {
            CloseableHttpResponse response = requestExecutor.execute(outgoingRequest);

            response = headerManager.copyHeaders(outgoingRequest, incomingRequest, response);

            e.setResponse(response);

            // Perform rendering
            e.setResponse(performRendering(relUrl, driverRequest, e.getResponse(), renderers));

            // Event post-proxy
            // This must be done before calling sendResponse to ensure response
            // can still be changed.
            postProxyPerformed = true;
            this.eventManager.fire(EventManager.EVENT_PROXY_POST, e);

            // Send request to the client.
            return e.getResponse();

        } catch (HttpErrorPage errorPage) {
            e.setErrorPage(errorPage);

            // On error returned by the proxy request, perform rendering on the
            // error page.
            CloseableHttpResponse response = e.getErrorPage().getHttpResponse();
            response = headerManager.copyHeaders(outgoingRequest, incomingRequest, response);
            e.setErrorPage(new HttpErrorPage(performRendering(relUrl, driverRequest, response, renderers)));

            // Event post-proxy
            // This must be done before throwing exception to ensure response
            // can still be changed.
            postProxyPerformed = true;
            this.eventManager.fire(EventManager.EVENT_PROXY_POST, e);

            throw e.getErrorPage();
        } finally {
            if (!postProxyPerformed) {
                this.eventManager.fire(EventManager.EVENT_PROXY_POST, e);
            }
        }
    }

    /**
     * Performs rendering on an HttpResponse.
     * <p>
     * Rendering is only performed if page can be parsed.
     * 
     * @param pageUrl
     *            The remove url from which the body was retrieved.
     * @param originalRequest
     *            The request received by esigate.
     * @param response
     *            The response which will be rendered.
     * @param renderers
     *            list of renderers to apply.
     * @return The rendered response, or the original response if if was not parsed.
     * @throws HttpErrorPage
     * @throws IOException
     */
    private CloseableHttpResponse performRendering(String pageUrl, DriverRequest originalRequest,
            CloseableHttpResponse response, Renderer[] renderers) throws HttpErrorPage, IOException {

        if (!contentTypeHelper.isTextContentType(response)) {
            LOG.debug("'{}' is binary on no transformation to apply: was forwarded without modification.", pageUrl);
            return response;
        }

        LOG.debug("'{}' is text : will apply renderers.", pageUrl);

        // Get response body
        String currentValue = HttpResponseUtils.toString(response, this.eventManager);

        // Perform rendering
        currentValue = performRendering(pageUrl, originalRequest, response, currentValue, renderers);

        // Generate the new response.
        HttpEntity transformedHttpEntity = new StringEntity(currentValue, ContentType.get(response.getEntity()));
        CloseableHttpResponse transformedResponse =
                BasicCloseableHttpResponse.adapt(new BasicHttpResponse(response.getStatusLine()));
        transformedResponse.setHeaders(response.getAllHeaders());
        transformedResponse.setEntity(transformedHttpEntity);
        return transformedResponse;

    }

    /**
     * Performs rendering (apply a render list) on an http response body (as a String).
     * 
     * @param pageUrl
     *            The remove url from which the body was retrieved.
     * @param originalRequest
     *            The request received by esigate.
     * @param response
     *            The Http Reponse.
     * @param body
     *            The body of the Http Response which will be rendered.
     * @param renderers
     *            list of renderers to apply.
     * @return The rendered response body.
     * @throws HttpErrorPage
     * @throws IOException
     */
    private String performRendering(String pageUrl, DriverRequest originalRequest, CloseableHttpResponse response,
            String body, Renderer[] renderers) throws IOException, HttpErrorPage {
        // Start rendering
        RenderEvent renderEvent = new RenderEvent(pageUrl, originalRequest, response);
        // Create renderer list from parameters.
        renderEvent.getRenderers().addAll(Arrays.asList(renderers));

        String currentBody = body;

        this.eventManager.fire(EventManager.EVENT_RENDER_PRE, renderEvent);
        for (Renderer renderer : renderEvent.getRenderers()) {
            StringBuilderWriter stringWriter = new StringBuilderWriter(Parameters.DEFAULT_BUFFER_SIZE);
            renderer.render(originalRequest, currentBody, stringWriter);
            stringWriter.close();
            currentBody = stringWriter.toString();
        }
        this.eventManager.fire(EventManager.EVENT_RENDER_POST, renderEvent);

        return currentBody;
    }

    /**
     * Get current driver configuration.
     * <p>
     * This method is not intended to get a WRITE access to the configuration.
     * <p>
     * This may be supported in future versions (testing is needed). For the time being, changing configuration settings
     * after getting access through this method is <b>UNSUPPORTED</b> and <b>SHOULD NOT</b> be used.
     * 
     * @return current configuration
     */
    public DriverConfiguration getConfiguration() {
        return this.config;
    }

    public RequestExecutor getRequestExecutor() {
        return requestExecutor;
    }

    @Override
    public String toString() {
        return "driver:" + config.getInstanceName();
    }

    public ContentTypeHelper getContentTypeHelper() {
        return contentTypeHelper;
    }

    public UrlRewriter getUrlRewriter() {
        return urlRewriter;
    }

}
