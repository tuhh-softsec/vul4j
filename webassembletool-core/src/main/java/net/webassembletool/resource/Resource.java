package net.webassembletool.resource;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import net.webassembletool.output.Output;

/**
 * An HTML page or resource (image, stylesheet...) that can be rendered to an
 * HttpServletResponse.<br />
 * A resource can come directly from a proxied request but also from a cache or
 * a file.<br />
 * A resource must be released after using it in order to release open files or
 * network connections.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public abstract class Resource {
	/**
	 * Renders the Resource to an Output
	 * 
	 * @param output
	 *            The output to render the resource to.
	 * @throws IOException
	 *             If an exception occurs while rendering to the output
	 */
	public abstract void render(Output output) throws IOException;

	/**
	 * Releases underlying open files or network connections
	 */
	public abstract void release();

	/**
	 * Returns the HTTP status code for this resource
	 * 
	 * @return status code
	 */
	public abstract int getStatusCode();

	public boolean isError() {
		int statusCode = getStatusCode();
		return statusCode != HttpServletResponse.SC_OK
				&& statusCode != HttpServletResponse.SC_MOVED_TEMPORARILY
				&& statusCode != HttpServletResponse.SC_MOVED_PERMANENTLY
				&& statusCode != HttpServletResponse.SC_NOT_MODIFIED;
	}

	/**
	 * Get ressource header.
	 * 
	 * @param name
	 * @return
	 */
	public abstract String getHeader(String name);

	/**
	 * Get header of the request which was used to get this resource. This can
	 * be used to check cache matching especially when the "Vary" header is
	 * used.
	 * 
	 * <p>
	 * This method is intended to be overridden when saving request header is
	 * supported by the implementation.
	 * 
	 * @return Map or null if request header was not saved or found.
	 */
	public String getRequestHeader(String name) {
		return null;
	}

	/**
	 * 
	 * Naive implementation of hasResponseBody : return true if responseCode was
	 * 200.
	 * 
	 * <p>
	 * This method is intended to be overridden by implementations.
	 * 
	 */
	public boolean hasResponseBody() {
		switch (getStatusCode()) {
		case HttpServletResponse.SC_OK:
		case HttpServletResponse.SC_PARTIAL_CONTENT:
			return true;

		}
		return false;
	}

	/**
	 * Returns local resource date (creation date).
	 * <p>
	 * null by default. This is intended to be overridden by implementation.
	 * 
	 * @return date or null if unavailable / not supported.
	 */
	public Date getLocalDate() {
		return null;
	}
}
