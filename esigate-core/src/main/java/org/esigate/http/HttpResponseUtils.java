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

package org.esigate.http;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.esigate.api.HttpStatusConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponseUtils {
	private static final Logger LOG = LoggerFactory.getLogger(HttpResponseUtils.class);

	public static boolean isError(HttpResponse httpResponse) {
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		return statusCode != HttpStatusConstants.SC_OK && statusCode != HttpStatusConstants.SC_MOVED_TEMPORARILY && statusCode != HttpStatusConstants.SC_MOVED_PERMANENTLY
				&& statusCode != HttpStatusConstants.SC_NOT_MODIFIED;
	}

	public static String getFirstHeader(String headerName, HttpResponse httpResponse) {
		Header header = httpResponse.getFirstHeader(headerName);
		if (header != null)
			return header.getValue();
		return null;
	}

	public static String getContentCharset(HttpResponse httpResponse) {
		HttpEntity httpEntity = httpResponse.getEntity();
		String charset = null;
		if (httpEntity != null) {
			charset = EntityUtils.getContentCharSet(httpEntity);
		}
		return charset;
	}

	public static void release(HttpResponse httpResponse) {
		HttpEntity httpEntity = httpResponse.getEntity();
		if (httpEntity != null)
			try {
				EntityUtils.consume(httpEntity);
			} catch (IOException e) {
				LOG.debug("Could not release request. Usualy this is due to a client abort.");
			}
	}
}
