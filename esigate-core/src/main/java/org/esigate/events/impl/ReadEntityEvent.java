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
public class ReadEntityEvent extends Event {
	/**
	 * The response returned by the remote server or by a cache subsystem.
	 */
	public HttpResponse httpResponse;
	/**
	 * Response mime type
	 */
	public String mimeType;

	/**
	 * Declared or detected charset.
	 * <p>
	 * The charset can be modified by an extension.
	 * <p>
	 * Note : if charset is modified, entityContent should be updated too.
	 */
	public Charset charset;

	/**
	 * The raw entity content, without any character set applied. It can be used
	 * to re-decode the entity content if the default charset was incorrect.
	 */
	public byte[] rawEntityContent;
	/**
	 * The current, decoded entity content.
	 * <p>
	 * An extension can update this content if is incorrect.
	 * <p>
	 * Note : if entityContent is modified, charset should be updated too.
	 */
	public String entityContent;
}
