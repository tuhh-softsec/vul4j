package org.esigate.events.impl;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.esigate.events.Event;

/**
 * Fetch event : when a new HTTP call is made to get a new block/template (Cache miss).
 * 
 * @author Nicolas Richeton
 * 
 */
public class FetchEvent extends Event {
	public HttpResponse httpResponse;
	/**
	 * The request context
	 */
	public HttpContext httpContext;
	/**
	 * The new HTTP call details.
	 */
	public HttpRequest httpRequest;
}
