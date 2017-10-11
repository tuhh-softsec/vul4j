package org.esigate.impl;

import java.net.URI;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.protocol.HttpContext;
import org.esigate.api.RedirectStrategy2;
import org.esigate.http.OutgoingRequest;
import org.esigate.http.ResourceUtils;
import org.esigate.util.UriUtils;

/**
 * The redirect strategy for fetching fragments.
 * 
 * <p>
 * This strategy is based on {@link DefaultRedirectStrategy} but differs for local redirections:
 * 
 * <p>
 * If a local (same provider) redirection is detected, the URI is returned as relative. This ensure that a public
 * (visible) url is not used instead of the provider url for the next requests.
 * 
 * @author Nicolas Richeton
 * 
 */
public class FragmentRedirectStrategy implements RedirectStrategy2 {
    private final DefaultRedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    /**
     * @see DefaultRedirectStrategy#isRedirected(HttpRequest, HttpResponse, HttpContext)
     */
    @Override
    public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context)
            throws ProtocolException {
        return this.redirectStrategy.isRedirected(request, response, context);
    }

    /**
     * @see DefaultRedirectStrategy#getRedirect(HttpRequest, HttpResponse, HttpContext)
     */
    @Override
    public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context)
            throws ProtocolException {

        // Code from DefaultRedirectStrategy
        // To ensure usage of the local getLocationURI implementation.

        final URI uri = getLocationURI(request, response, context);
        final String method = request.getRequestLine().getMethod();
        if (method.equalsIgnoreCase(HttpHead.METHOD_NAME)) {
            return new HttpHead(uri);
        } else if (method.equalsIgnoreCase(HttpGet.METHOD_NAME)) {
            return new HttpGet(uri);
        } else {
            final int status = response.getStatusLine().getStatusCode();
            if (status == HttpStatus.SC_TEMPORARY_REDIRECT) {
                return RequestBuilder.copy(request).setUri(uri).build();
            } else {
                return new HttpGet(uri);
            }
        }
    }

    /**
     * For local redirects, converts to relative urls.
     * 
     * @param request
     *            must be an {@link OutgoingRequest}.
     */
    @Override
    public URI getLocationURI(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
        URI uri = this.redirectStrategy.getLocationURI(request, response, context);

        String resultingPageUrl = uri.toString();

        DriverRequest driverRequest = ((OutgoingRequest) request).getOriginalRequest();

        // Remove context if present
        if (StringUtils.startsWith(resultingPageUrl, driverRequest.getVisibleBaseUrl())) {
            resultingPageUrl =
                    "/"
                            + StringUtils.stripStart(
                                    StringUtils.replace(resultingPageUrl, driverRequest.getVisibleBaseUrl(), ""), "/");
        }
        resultingPageUrl = ResourceUtils.getHttpUrlWithQueryString(resultingPageUrl, driverRequest, false);

        return UriUtils.createURI(ResourceUtils.getHttpUrlWithQueryString(resultingPageUrl, driverRequest, false));
    }

}
