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

import java.util.Properties;

import org.apache.http.impl.client.cache.CachingHttpClientBuilder;
import org.apache.http.impl.execchain.ClientExecChain;
import org.esigate.cache.CacheAdapter;
import org.esigate.events.EventManager;

public class ProxyingHttpClientBuilder extends CachingHttpClientBuilder {
	private Properties properties;
	private EventManager eventManager;
	private boolean useCache = true;

	@Override
	protected ClientExecChain decorateMainExec(ClientExecChain mainExec) {
		if (!useCache)
			return mainExec;
		ClientExecChain result = mainExec;
		CacheAdapter cacheAdapter = new CacheAdapter();
		cacheAdapter.init(properties);
		result = cacheAdapter.wrapBackendHttpClient(eventManager, result);
		result = super.decorateMainExec(result);
		result = cacheAdapter.wrapCachingHttpClient(result);
		return result;
	}

	public void setUseCache(boolean useCache) {
		this.useCache = useCache;
	}

	public void setEventManager(EventManager eventManager) {
		this.eventManager = eventManager;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public EventManager getEventManager() {
		return eventManager;
	}

	public boolean isUseCache() {
		return useCache;
	}

}
