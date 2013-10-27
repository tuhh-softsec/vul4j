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

import java.util.Collection;
import java.util.Properties;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.esigate.Parameters;

public class ContentTypeHelper {
	private Collection<String> parsableContentTypes;

	public ContentTypeHelper(Properties properties) {
		parsableContentTypes = Parameters.PARSABLE_CONTENT_TYPES.getValueList(properties);
	}

	/**
	 * Check whether the given request's content-type corresponds to "parseable"
	 * text.
	 * 
	 * @param httpResponse
	 *            the response to analyze depending on its content-type
	 * @return true if this represents text or false if not
	 */
	public boolean isTextContentType(HttpResponse httpResponse) {
		String contentType = HttpResponseUtils.getFirstHeader(HttpHeaders.CONTENT_TYPE, httpResponse);
		return isTextContentType(contentType);
	}

	/**
	 * Check whether the given content-type corresponds to "parseable" text.
	 * 
	 * @param contentType
	 *            the input content-type
	 * @return true if this represents text or false if not
	 */
	public boolean isTextContentType(String contentType) {
		if (contentType != null) {
			String lowerContentType = contentType.toLowerCase();
			for (String textContentType : this.parsableContentTypes) {
				if (lowerContentType.startsWith(textContentType)) {
					return true;
				}
			}
		}
		return false;
	}

}
