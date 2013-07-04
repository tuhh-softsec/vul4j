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
package org.esigate.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.esigate.Driver;

/**
 * This class is used by DriverFactory to hold both the user instances and a
 * list of the global mappings.
 * 
 * <p>
 * This object is replaced atomically in the DriverFactory allowing a
 * synchronization-less update of the configuration.
 * 
 * @author Nicolas Richeton
 */
public class IndexedInstances {
	private final Map<String, Driver> instances;
	private final Map<UriMapping, String> uriMappings;

	public IndexedInstances(Map<String, Driver> instances) {
		this.instances = instances;
		this.uriMappings = buildUriMappings(this.instances);
	}

	private Map<UriMapping, String> buildUriMappings(Map<String, Driver> instances2) {
		Map<UriMapping, String> result = new LinkedHashMap<UriMapping, String>();

		Map<UriMapping, String> unsortedResult = new LinkedHashMap<UriMapping, String>();

		if (this.instances != null) {
			for (String instanceId : this.instances.keySet()) {
				List<UriMapping> driverMappings = this.instances.get(instanceId).getConfiguration().getUriMappings();

				for (UriMapping mapping : driverMappings) {
					unsortedResult.put(mapping, instanceId);
				}
			}
		}

		// Order according to weight. 
		SortedSet<UriMapping> keys = new TreeSet<UriMapping>( new UriMappingComparator());
		keys.addAll(unsortedResult.keySet());
		for (UriMapping key : keys) { 
		  result.put(key, unsortedResult.get(key));
		}		
		
		return result;
	}

	/**
	 * A map containing all instances with their ids.
	 * 
	 * @return Map content (instanceName, instance)
	 */
	public Map<String, Driver> getInstances() {
		return this.instances;
	}

	/**
	 * A map containing all URI mappings and the associated driver instance
	 * name.
	 * 
	 * @return Map content (mapping, instanceName)
	 */
	public Map<UriMapping, String> getUrimappings() {
		return this.uriMappings;
	}

}