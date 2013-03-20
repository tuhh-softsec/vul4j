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

package org.esigate.filter;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.esigate.http.GenericHttpRequest;

/**
 * Interface for request filter.
 * @deprecated use Extension instead and register to pre and post-fetch events notifications.
 * .
 * @author Nicolas Richeton
 * 
 */
@Deprecated
public interface Filter  {

	void preRequest(GenericHttpRequest request, HttpContext httpContext, HttpRequest originalRequest);

	void postRequest(GenericHttpRequest httpRequest, HttpResponse response, HttpContext httpContext, HttpRequest originalRequest);

	Filter EMPTY = new Filter() {
		@Override
		public void preRequest(GenericHttpRequest request, HttpContext httpContext, HttpRequest originalRequest) {
		}

		@Override
		public void postRequest(GenericHttpRequest httpRequest, HttpResponse response, HttpContext httpContext, HttpRequest originalRequest) {
		}

	};
}
