package org.esigate.api;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.http.cookie.Cookie;

public interface HttpResponse {
    /**
     * Adds the specified cookie to the response.  This method can be called
     * multiple times to set more than one cookie.
     *
     * @param cookie the Cookie to return to the client
     */
    public void addCookie(Cookie cookie);

    /**
     * Sets the status code for this response.  This method is used to
     * set the return status code when there is no error (for example,
     * for the status codes SC_OK or SC_MOVED_TEMPORARILY).  If there
     * is an error, and the caller wishes to invoke an error-page defined
     * in the web applicaion, the <code>sendError</code> method should be used
     * instead.
     * <p> The container clears the buffer and sets the Location header, preserving
     * cookies and other headers.
     *
     * @param sc the status code
     *
     */
    public void setStatus(int sc);

    /**
     * Returns a {@link OutputStream} suitable for writing binary
     * data in the response. The servlet container does not encode the
     * binary data.
     *
     * <p> Calling flush() on the OutputStream commits the response.
     *
     * @return a {@link OutputStream} for writing binary data
     *
     * @exception IllegalStateException if the <code>getWriter</code> method
     * has been called on this response
     *
     * @exception IOException if an input or output exception occurred
     *
     */
    public OutputStream getOutputStream() throws IOException;

    /**
     * Adds a response header with the given name and value.
     * This method allows response headers to have multiple values.
     *
     * @param name the name of the header
     * @param value the additional header value If it contains octet string,
     * it should be encoded according to RFC 2047
     * (http://www.ietf.org/rfc/rfc2047.txt)
     *
     */
    public void addHeader(String name, String value);
}
