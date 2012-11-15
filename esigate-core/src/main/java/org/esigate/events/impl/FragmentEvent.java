package org.esigate.events.impl;

import org.apache.http.protocol.HttpContext;
import org.esigate.api.HttpRequest;
import org.esigate.events.Event;
import org.esigate.http.GenericHttpRequest;

/**
 * Fetch event : when a new HTTP call is made to get a new block/template.
 * 
 * @author Nicolas Richeton
 * 
 */
public class FragmentEvent extends Event {

	/**
	 * The response data.
	 * <p>
	 * May be null if the request has not been executed yet. If this case,
	 * setting a response cancels the HTTP call and use the given object
	 * instead.
	 * 
	 */
	public org.apache.http.HttpResponse httpResponse;
	/**
	 * The request context
	 */
	public HttpContext httpContext;
	/**
	 * The new HTTP call details.
	 */
	public GenericHttpRequest httpRequest;
	
	/**
	 * The request which was received by ESIgate.
	 */
	public HttpRequest originalRequest;
}
