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

package org.esigate.url;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpRequest;
import org.esigate.api.BaseUrlRetrieveStrategy;

public class RoundRobinBaseUrlRetrieveStrategy implements BaseUrlRetrieveStrategy {
	private final AtomicInteger counter = new AtomicInteger(0);
	private final String[] urls;

	public RoundRobinBaseUrlRetrieveStrategy(String[] urls) {
		this.urls = urls;
	}

	@Override
	public String getBaseURL(HttpRequest originalRequest) {
		int incremented = this.counter.incrementAndGet();
		int index = incremented % this.urls.length;
		return this.urls[Math.abs(index)];
	}
}
