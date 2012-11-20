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

package org.esigate.util;

import java.net.URI;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.esigate.Driver;
import org.esigate.UserContext;
import org.esigate.api.HttpRequest;
import org.esigate.api.HttpResponse;

public class HttpRequestHelper {
	private final static String HTTP_RESPONSE = HttpResponse.class.getName();
	private final static String USER_CONTEXT = UserContext.class.getName();
	private final static String BASEURLASURL = URL.class.getName();
	private final static String DRIVER = Driver.class.getName();
	private final static String PARAMETERS = "parameters";
	private final static String CHARACTER_ENCODING = "character encoding";

	public final static HttpResponse getResponse(HttpRequest request) {
		return (HttpResponse) request.getParams().getParameter(HTTP_RESPONSE);
	}

	public final static void setResponse(HttpRequest request, HttpResponse response) {
		request.getParams().setParameter(HTTP_RESPONSE, response);
	}

	public final static UserContext getUserContext(HttpRequest request) {
		return (UserContext) request.getParams().getParameter(USER_CONTEXT);
	}

	public final static void setUserContext(HttpRequest request, UserContext userContext) {
		request.getParams().setParameter(USER_CONTEXT, userContext);
	}

	public final static URL getBaseUrl(HttpRequest request) {
		return (URL) request.getParams().getParameter(BASEURLASURL);
	}

	public final static void setBaseUrl(HttpRequest request, URL url) {
		request.getParams().setParameter(BASEURLASURL, url);
	}

	public final static Driver getDriver(HttpRequest request) {
		return (Driver) request.getParams().getParameter(DRIVER);
	}

	public final static void setDriver(HttpRequest request, Driver driver) {
		request.getParams().setParameter(DRIVER, driver);
	}

	@SuppressWarnings("unchecked")
	public final static Map<String, String> getParameters(HttpRequest request) {
		return (Map<String, String>) request.getParams().getParameter(PARAMETERS);
	}

	public final static void setParameters(HttpRequest request, Map<String, String> parameters) {
		request.getParams().setParameter(PARAMETERS, parameters);
	}

	public final static String getFirstHeader(String name, HttpRequest request) {
		Header[] values = request.getHeaders(name);
		if (values.length > 0)
			return values[0].getValue();
		return null;
	}

	public final static String getCharacterEncoding(HttpRequest request) {
		return (String) request.getParams().getParameter(CHARACTER_ENCODING);
	}

	public final static void setCharacterEncoding(HttpRequest request, String characterEncoding) {
		request.getParams().setParameter(CHARACTER_ENCODING, characterEncoding);
	}

	public final static String getParameter(HttpRequest request, String name) {
		String characterEncoding = getCharacterEncoding(request);
		if (characterEncoding == null)
			characterEncoding = "ISO-8859-1";
		URI uri = UriUtils.createUri(request.getRequestLine().getUri());
		List<NameValuePair> parameters = URLEncodedUtils.parse(uri, characterEncoding);
		Iterator<NameValuePair> it = parameters.iterator();
		while (it.hasNext()) {
			NameValuePair nameValuePair = (NameValuePair) it.next();
			if (nameValuePair.getName().equals(name))
				return nameValuePair.getValue();
		}
		return null;
	}

}
