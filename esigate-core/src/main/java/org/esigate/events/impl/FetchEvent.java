package org.esigate.events.impl;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.esigate.events.Event;

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
