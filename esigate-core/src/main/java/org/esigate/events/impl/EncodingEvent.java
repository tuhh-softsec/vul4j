package org.esigate.events.impl;

import java.nio.charset.Charset;

import org.apache.http.HttpResponse;
import org.esigate.events.Event;

/**
 * Encoding event : when a HTTP response is read as String.
 * 
 * @author Nicolas Richeton
 * 
 */
public class EncodingEvent extends Event {
	public HttpResponse httpResponse;
	public String mimeType;
	public Charset charset;
	public byte[] rawEntityContent;
	public String entityContent;
}


