package org.esigate.api;

import java.net.URI;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.protocol.HttpContext;

/**
 * Interface for redirect strategies used by driver when retrieving content from providers.
 * 
 * <p>
 * This interface is based on {@link RedirectStrategy} and expose one additional method :
 * 
 * @see RedirectStrategy2#getLocationURI(HttpRequest, HttpResponse, HttpContext)
 * 
 * @author Nicolas Richeton
 * 
 */
public interface RedirectStrategy2 extends RedirectStrategy {

    URI getLocationURI(final HttpRequest request, final HttpResponse response, final HttpContext context)
            throws ProtocolException;
}
