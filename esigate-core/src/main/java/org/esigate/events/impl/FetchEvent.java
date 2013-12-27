package org.esigate.events.impl;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.esigate.events.Event;
import org.esigate.http.OutgoingRequestContext;

/**
 * Fetch event : when a new HTTP call is made to get a new block/template (Cache miss).
 * 
 * @author Nicolas Richeton
 * 
 */
public class FetchEvent extends Event {
    /**
     * The response returned by the remote server.
     * <p>
     * May be null if the request has not been executed yet. If this case, setting a response cancels the HTTP call and
     * use the given object instead.
     */
    private CloseableHttpResponse httpResponse;
    /**
     * The request context.
     */
    private final OutgoingRequestContext httpContext;
    /**
     * The new HTTP call details.
     */
    private final HttpRequestWrapper httpRequest;

    public FetchEvent(OutgoingRequestContext httpContext, HttpRequestWrapper httpRequest) {
        this.httpContext = httpContext;
        this.httpRequest = httpRequest;
    }

    public CloseableHttpResponse getHttpResponse() {
        return httpResponse;
    }

    public void setHttpResponse(CloseableHttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    public OutgoingRequestContext getHttpContext() {
        return httpContext;
    }

    public HttpRequestWrapper getHttpRequest() {
        return httpRequest;
    }

}
