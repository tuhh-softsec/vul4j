package org.esigate.api;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

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
     * Either this method or {@link #getWriter} may
     * be called to write the body, not both.
     *
     * @return a {@link OutputStream} for writing binary data
     *
     * @exception IllegalStateException if the <code>getWriter</code> method
     * has been called on this response
     *
     * @exception IOException if an input or output exception occurred
     *
     * @see #getWriter
     */
    public OutputStream getOutputStream() throws IOException;

    /**
     * Returns a <code>Writer</code> object that
     * can send character text to the client.
     * If the response's character encoding has not been
     * specified as described in <code>getCharacterEncoding</code>
     * (i.e., the method just returns the default value
     * <code>ISO-8859-1</code>), <code>getWriter</code>
     * updates it to <code>ISO-8859-1</code>.
     * <p>Calling flush() on the <code>PrintWriter</code>
     * commits the response.
     * <p>Either this method or {@link #getOutputStream} may be called
     * to write the body, not both.
     *
     * @return a <code>PrintWriter</code> object that
     * can return character data to the client
     *
     * @exception UnsupportedEncodingException if the character encoding
     * returned by <code>getCharacterEncoding</code> cannot be used
     *
     * @exception IllegalStateException if the <code>getOutputStream</code>
     * method has already been called for this response object
     *
     * @exception IOException if an input or output exception occurred
     *
     * @see #getOutputStream
     * @see #setCharacterEncoding
     */
    public Writer getWriter() throws IOException;

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

    /**
     * Sets the character encoding (MIME charset) of the response
     * being sent to the client, for example, to UTF-8.
     * If the character encoding has already been set by
     * {@link #setContentType}, this method overrides it.
     * Calling {@link #setContentType} with the <code>String</code>
     * of <code>text/html</code> and calling
     * this method with the <code>String</code> of <code>UTF-8</code>
     * is equivalent with calling
     * <code>setContentType</code> with the <code>String</code> of
     * <code>text/html; charset=UTF-8</code>.
     * <p>This method can be called repeatedly to change the character
     * encoding.
     * This method has no effect if it is called after
     * <code>getWriter</code> has been
     * called or after the response has been committed.
     * <p>Containers must communicate the character encoding used for
     * the servlet response's writer to the client if the protocol
     * provides a way for doing so. In the case of HTTP, the character
     * encoding is communicated as part of the <code>Content-Type</code>
     * header for text media types. Note that the character encoding
     * cannot be communicated via HTTP headers if the servlet does not
     * specify a content type; however, it is still used to encode text
     * written via the servlet response's writer.
     *
     * @param charset a String specifying only the character set
     * defined by IANA Character Sets
     * (http://www.iana.org/assignments/character-sets)
     *
     * @see #setContentType
     *
     * @since Servlet 2.4
     */
    public void setCharacterEncoding(String charset);

    /**
     * Sets the length of the content body in the response
     * In HTTP servlets, this method sets the HTTP Content-Length header.
     *
     * @param len an integer specifying the length of the
     * content being returned to the client; sets
     * the Content-Length header
     */
    public void setContentLength(int len);

    /**
     * Sets the content type of the response being sent to
     * the client, if the response has not been committed yet.
     * The given content type may include a character encoding
     * specification, for example, <code>text/html;charset=UTF-8</code>.
     * The response's character encoding is only set from the given
     * content type if this method is called before <code>getWriter</code>
     * is called.
     * <p>This method may be called repeatedly to change content type and
     * character encoding.
     * This method has no effect if called after the response
     * has been committed. It does not set the response's character
     * encoding if it is called after <code>getWriter</code>
     * has been called or after the response has been committed.
     * <p>Containers must communicate the content type and the character
     * encoding used for the servlet response's writer to the client if
     * the protocol provides a way for doing so. In the case of HTTP,
     * the <code>Content-Type</code> header is used.
     *
     * @param type a <code>String</code> specifying the MIME
     * type of the content
     *
     * @see #setCharacterEncoding
     * @see #getOutputStream
     * @see #getWriter
     */
    public void setContentType(String type);
}
