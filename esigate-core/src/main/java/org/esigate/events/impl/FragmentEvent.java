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

package org.esigate.events.impl;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.esigate.events.Event;
import org.esigate.http.GenericHttpRequest;

/**
 * Fragment event : when a fragment is required for rendering. This may start a
 * fetch event in case of a cache miss. Else the fragment is retrived from the
 * cache.
 * 
 * @author Nicolas Richeton
 * 
 */
public class FragmentEvent extends Event {

	/**
	 * The response data.
	 * <p>
	 * May be null if the request has not been executed yet. If this case,
	 * setting a response cancels the HTTP call and use the given object
	 * instead.
	 * 
	 */
	public HttpResponse httpResponse;
	/**
	 * The request context
	 */
	public HttpContext httpContext;
	/**
	 * The new HTTP call details.
	 */
	public GenericHttpRequest httpRequest;

	/**
	 * The request which was received by ESIgate.
	 */
	public HttpEntityEnclosingRequest originalRequest;
}
