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
package org.esigate.extension.parallelesi;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

class InlineCache {
	private static final Map<String, InlineCache> CACHE = new HashMap<String, InlineCache>();

	private final Date outdate;
	private final boolean fetchable;
	private final String originalUrl;
	private final String fragment;

	public static void storeFragment(String uri, Date outdate,
			boolean fetchable, String originalUrl, String fragment) {
		InlineCache ic = new InlineCache(outdate, fetchable, originalUrl, fragment);
		CACHE.put(uri, ic);
	}

	public static InlineCache getFragment(String uri) {
		return CACHE.get(uri);
	}

	private InlineCache(Date outdate, boolean fetchable, String originalUrl, String fragment) {
		this.outdate = outdate;
		this.fetchable = fetchable;
		this.originalUrl = originalUrl;
		this.fragment = fragment;
	}

	public boolean isExpired() {
		return (outdate != null) && (outdate.getTime() < System.currentTimeMillis());
	}

	public Date getOutdate() {
		return outdate;
	}

	public boolean isFetchable() {
		return fetchable;
	}

	public String getOriginalUrl() {
		return originalUrl;
	}

	public String getFragment() {
		return fragment;
	}

}
