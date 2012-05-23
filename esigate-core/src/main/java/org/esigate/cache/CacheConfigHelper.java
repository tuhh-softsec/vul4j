/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.esigate.cache;

import java.util.Properties;

import org.apache.http.impl.client.cache.CacheConfig;
import org.esigate.Parameters;

public class CacheConfigHelper {
	public final static CacheConfig createCacheConfig(Properties properties) {

		// Heuristic caching
		boolean heuristicCachingEnabled = Parameters.HEURISTIC_CACHING_ENABLED.getValueBoolean(properties);
		float heuristicCoefficient = Parameters.HEURISTIC_COEFFICIENT.getValueFloat(properties);
		long heuristicDefaultLifetimeSecs = Parameters.HEURISTIC_DEFAULT_LIFETIME_SECS.getValueLong(properties);
		int maxCacheEntries = Parameters.MAX_CACHE_ENTRIES.getValueInt(properties);
		int maxObjectSize = Parameters.MAX_OBJECT_SIZE.getValueInt(properties);

		// Asynchronous revalidation
		int minAsynchronousWorkers = Parameters.MIN_ASYNCHRONOUS_WORKERS.getValueInt(properties);
		int maxAsynchronousWorkers = Parameters.MAX_ASYNCHRONOUS_WORKERS.getValueInt(properties);
		int asynchronousWorkerIdleLifetimeSecs = Parameters.ASYNCHRONOUS_WORKER_IDLE_LIFETIME_SECS.getValueInt(properties);
		int maxUpdateRetries = Parameters.MAX_UPDATE_RETRIES.getValueInt(properties);
		int revalidationQueueSize = Parameters.REVALIDATION_QUEUE_SIZE.getValueInt(properties);

		CacheConfig cacheConfig = new CacheConfig();
		cacheConfig.setHeuristicCachingEnabled(heuristicCachingEnabled);
		cacheConfig.setHeuristicCoefficient(heuristicCoefficient);
		cacheConfig.setHeuristicDefaultLifetime(heuristicDefaultLifetimeSecs);
		cacheConfig.setMaxCacheEntries(maxCacheEntries);
		if (maxObjectSize > 0)
			cacheConfig.setMaxObjectSizeBytes(maxObjectSize);
		else
			cacheConfig.setMaxObjectSizeBytes(Integer.MAX_VALUE);
		cacheConfig.setAsynchronousWorkersCore(minAsynchronousWorkers);
		cacheConfig.setAsynchronousWorkersMax(maxAsynchronousWorkers);
		cacheConfig.setAsynchronousWorkerIdleLifetimeSecs(asynchronousWorkerIdleLifetimeSecs);
		cacheConfig.setMaxUpdateRetries(maxUpdateRetries);
		cacheConfig.setRevalidationQueueSize(revalidationQueueSize);
		return cacheConfig;
	}

}
