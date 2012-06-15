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

package org.esigate.authentication;

import java.io.IOException;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.esigate.ResourceContext;
import org.esigate.extension.Extension;
import org.esigate.http.GenericHttpRequest;

/**
 * An AuthenticationHandler is called before any request and can modify it to add authentication information such as request parameters or HTTP headers. After the request has been executed, it can ask
 * for a new request as many times as needed. This can be used to implement challenge authentication schemes.
 * 
 * There is only one instance of this class for a driver instance so it must be thread-safe.
 * 
 * @author Francois-Xavier Bonnet
 * @author Nicolas Richeton
 * 
 */
public interface AuthenticationHandler extends Extension {

	public void init(Properties properties);

	/**
	 * Method called before proxying a request
	 * 
	 * This method can ask the users credentials by sending an authentication page or a 401 code or redirect to a login page. If so the method must return false in order to stop further processing.
	 * 
	 * @param requestContext
	 * @return true if the processing must continue, false if the response has already been sent to the client.
	 */
	public boolean beforeProxy(ResourceContext requestContext) throws IOException;

	/**
	 * Method called before sending a request to the destination server.
	 * 
	 * This method can be used to add user credentials to the request
	 * 
	 * @param request
	 * @param requestContext
	 */
	public void preRequest(GenericHttpRequest request, ResourceContext requestContext);

	/**
	 * Method called after the response has been obtained from the destination server.
	 * 
	 * This method can be used to ask for a new request if the destination server uses a challenge-based authentication mechanism with an arbitrary number of steps.
	 * 
	 * @param response
	 * @param requestContext
	 * @return true if a new request is needed
	 */
	public boolean needsNewRequest(HttpResponse response, ResourceContext requestContext);

}
