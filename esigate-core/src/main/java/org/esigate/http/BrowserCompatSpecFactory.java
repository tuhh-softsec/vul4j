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

import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecFactory;
import org.apache.http.params.HttpParams;

/**
 * FIXME This is a temporary fix for
 * BrowserCompatSpec: cookies values containing spaces are forwarded without quotes
 * https://issues.apache.org/jira/browse/HTTPCLIENT-1269
 * 
 * Should be removed when migrating to HttpClient 4.3
 * @author Francois-Xavier Bonnet
 *
 */
public class BrowserCompatSpecFactory implements CookieSpecFactory {

	public CookieSpec newInstance(HttpParams params) {
		return new BrowserCompatSpec();
	}

}
