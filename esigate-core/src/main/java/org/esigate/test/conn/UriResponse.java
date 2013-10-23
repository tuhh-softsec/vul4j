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
package org.esigate.test.conn;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

/**
 * 
 * A response handler, which returns different HTTP reponses according to the
 * requested URI.
 * 
 * Sends IllegalStateException if no reponse has been defined for the requested
 * uri.
 * 
 * @author Nicolas Richeton
 * 
 */
public class UriResponse implements IResponseHandler {
	private Map<String, HttpResponse> responses = new HashMap<String, HttpResponse>();

	@Override
	public HttpResponse execute(HttpRequest request) throws IOException {
		String uri = request.getRequestLine().getUri();
		HttpResponse result = this.responses.get(uri);
		if (result == null) {
			throw new IllegalStateException("No response for uri: " + uri);
		}
		return result;
	}

	/**
	 * Add a Http reponse.
	 * 
	 * <p>
	 * <pre>
	 * response( "http://host/path1", httpResponse )
	 * </pre>
	 * @param uri
	 *            Full uri including protocol, host, port if any, path and query
	 *            params.
	 *            
	 *            
	 * @param response
	 * @return this object
	 */
	public UriResponse response(String uri, HttpResponse response) {
		this.responses.put(uri, response);
		return this;
	}

}
