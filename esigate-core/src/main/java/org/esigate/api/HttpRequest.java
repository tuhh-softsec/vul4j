/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.esigate.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.apache.http.RequestLine;
import org.apache.http.cookie.Cookie;
import org.apache.http.params.HttpParams;

public interface HttpRequest {
	/**
	 * Returns the value of a request parameter as a <code>String</code>, or
	 * <code>null</code> if the parameter does not exist. Request parameters are
	 * extra information sent with the request. For HTTP servlets, parameters
	 * are contained in the query string or posted form data.
	 * 
	 * <p>
	 * If you use this method with a multivalued parameter, the value returned
	 * is equal to the first value in the array returned by
	 * <code>getParameterValues</code>.
	 * 
	 * <p>
	 * If the parameter data was sent in the request body, such as occurs with
	 * an HTTP POST request, then reading the body directly via
	 * {@link #getInputStream} can interfere with the execution of this method.
	 * 
	 * @param name
	 *            a <code>String</code> specifying the name of the parameter
	 * 
	 * @return a <code>String</code> representing the single value of the
	 *         parameter
	 * 
	 */
	public String getParameter(String name);

	/**
	 * Returns an array containing all of the <code>Cookie</code> objects the
	 * client sent with this request. This method returns <code>null</code> if
	 * no cookies were sent.
	 * 
	 * @return an array of all the <code>Cookies</code> included with this
	 *         request, or <code>null</code> if the request has no cookies
	 */
	public Cookie[] getCookies();

	/**
	 * Returns the Internet Protocol (IP) address of the client or last proxy
	 * that sent the request. For HTTP servlets, same as the value of the CGI
	 * variable <code>REMOTE_ADDR</code>.
	 * 
	 * @return a <code>String</code> containing the IP address of the client
	 *         that sent the request
	 */
	public String getRemoteAddr();

	/**
	 * Retrieves the body of the request as binary data using a
	 * {@link InputStream}. This method may be called to read the body, not
	 * both.
	 * 
	 * @return a {@link InputStream} object containing the body of the request
	 * 
	 * @exception IOException
	 *                if an input or output exception occurred
	 */
	public InputStream getInputStream() throws IOException;

	/**
	 * Returns the MIME type of the body of the request, or <code>null</code> if
	 * the type is not known. For HTTP servlets, same as the value of the CGI
	 * variable CONTENT_TYPE.
	 * 
	 * @return a <code>String</code> containing the name of the MIME type of the
	 *         request, or null if the type is not known
	 */
	public String getContentType();

	/**
	 * Returns a boolean indicating whether this request was made using a secure
	 * channel, such as HTTPS.
	 * 
	 * @return a boolean indicating if the request was made using a secure
	 *         channel
	 */
	public boolean isSecure();

	/**
	 * Returns the name of the character encoding used in the body of this
	 * request. This method returns <code>null</code> if the request does not
	 * specify a character encoding
	 * 
	 * @return a <code>String</code> containing the name of the chararacter
	 *         encoding, or <code>null</code> if the request does not specify a
	 *         character encoding
	 */
	public String getCharacterEncoding();

	public void setCharacterEncoding(String env) throws UnsupportedEncodingException;

	/**
	 * Returns the login of the user making this request, if the user has been
	 * authenticated, or <code>null</code> if the user has not been
	 * authenticated. Whether the user name is sent with each subsequent request
	 * depends on the browser and type of authentication. Same as the value of
	 * the CGI variable REMOTE_USER.
	 * 
	 * @return a <code>String</code> specifying the login of the user making
	 *         this request, or <code>null</code> if the user login is not known
	 */
	public String getRemoteUser();

	/**
	 * Returns a <code>java.security.Principal</code> object containing the name
	 * of the current authenticated user. If the user has not been
	 * authenticated, the method returns <code>null</code>.
	 * 
	 * @return a <code>java.security.Principal</code> containing the name of the
	 *         user making this request; <code>null</code> if the user has not
	 *         been authenticated
	 */
	public java.security.Principal getUserPrincipal();

	/**
	 * Returns the current <code>HttpSession</code> associated with this request
	 * or, if there is no current session and <code>create</code> is true,
	 * returns a new session.
	 * 
	 * <p>
	 * If <code>create</code> is <code>false</code> and the request has no valid
	 * <code>HttpSession</code>, this method returns <code>null</code>.
	 * 
	 * <p>
	 * To make sure the session is properly maintained, you must call this
	 * method before the response is committed. If the container is using
	 * cookies to maintain session integrity and is asked to create a new
	 * session when the response is committed, an IllegalStateException is
	 * thrown.
	 * 
	 * @param create
	 *            <code>true</code> to create a new session for this request if
	 *            necessary; <code>false</code> to return <code>null</code> if
	 *            there's no current session
	 * 
	 * @return the <code>HttpSession</code> associated with this request or
	 *         <code>null</code> if <code>create</code> is <code>false</code>
	 *         and the request has no valid session
	 * 
	 */
	public HttpSession getSession(boolean create);

	public InputStream getResourceAsStream(String path);

	public HttpParams getParams();
	
	public RequestLine getRequestLine();
	
	public Header[] getHeaders(String name);
	
	public Header[] getAllHeaders();


}
