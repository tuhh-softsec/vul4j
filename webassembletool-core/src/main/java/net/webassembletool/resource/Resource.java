package net.webassembletool.resource;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import net.webassembletool.output.Output;

/**
 * An HTML page or resource (image, stylesheet...) that can be rendered to an
 * HttpServletResponse.<br /> A resource can come directly from a proxied
 * request but also from a cache or a file.<br /> A resource must be released
 * after using it in ordre to release open files or network connections.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public abstract class Resource {
    /**
     * Renders the Resource to an Output
     * 
     * @param output The output to render the resource to.
     * @throws IOException If an exception occurs while rendering to the output
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
		&& statusCode != HttpServletResponse.SC_MOVED_PERMANENTLY;
    }
    
    public abstract String getHeader(String name);

}
