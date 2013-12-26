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
    public CloseableHttpResponse httpResponse;
    /**
     * The request context.
     */
    public OutgoingRequestContext httpContext;
    /**
     * The new HTTP call details.
     */
    public HttpRequestWrapper httpRequest = null;

}
