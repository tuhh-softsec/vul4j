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

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.esigate.HttpErrorPage;
import org.esigate.events.Event;

/**
 * Proxy Event : Requests received by ESIGate in proxy mode ( standalone
 * application).
 * 
 * @author Nicolas Richeton
 * 
 */
public class ProxyEvent extends Event {
	/**
	 * The request which was received by ESIgate.
	 */
	public HttpRequest originalRequest;

	/**
	 * The current response. May be null if no reponse has be created yet or in
	 * case of error.
	 */
	public HttpResponse response = null;

	/**
	 * The current error page. If not null, an error as occured and the error
	 * page will be sent instead of the response.
	 */
	public HttpErrorPage errorPage = null;
}
