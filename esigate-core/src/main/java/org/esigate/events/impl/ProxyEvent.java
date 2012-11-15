package org.esigate.events.impl;

import org.esigate.api.HttpRequest;
import org.esigate.events.Event;

/**
 * Proxy Event : Requests received by ESIGate in proxy mode ( standalone application). 
 * 
 * @author Nicolas Richeton
 * 
 */
public class ProxyEvent extends Event {
	/**
	 * The request which was received by ESIgate.
	 */
	public HttpRequest originalRequest;
}
