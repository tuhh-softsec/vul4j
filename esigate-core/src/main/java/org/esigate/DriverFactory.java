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

package org.esigate;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * Factory class used to configure and retrieve {@linkplain Driver} INSTANCIES.
 * 
 * @author Stanislav Bernatskyi
 * @author Francois-Xavier Bonnet
 */
public class DriverFactory {
	private static final Map<String, Driver> INSTANCES = new HashMap<String, Driver>();
	private static final String DEFAULT_INSTANCE_NAME = "default";

	static {
		// Load default settings
		configure();
	}

	private DriverFactory() {

	}

	/** Loads all instancies according to default configuration file */
	public final static void configure() {
		InputStream inputStream = Driver.class.getResourceAsStream("driver.properties");

		// load driver-ext.properties if exists

		InputStream extInputStream = DriverFactory.class.getClassLoader().getResourceAsStream("driver-ext.properties");

		try {
			Properties merged = new Properties();
			if (inputStream != null) {
				Properties props = new Properties();
				props.load(inputStream);
				merged.putAll(props);
			}

			if (extInputStream != null) {
				Properties extProps = new Properties();
				extProps.load(extInputStream);
				merged.putAll(extProps);
			}

			configure(merged);
		} catch (IOException e) {
			throw new ConfigurationException("Error loading configuration", e);
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}

				if (extInputStream != null) {
					extInputStream.close();
				}
			} catch (IOException e) {
				throw new ConfigurationException("failed to close stream", e);
			}
		}
	}

	/**
	 * Loads all instancies according to the properties parameter
	 * 
	 * @param props
	 *            properties to use for configuration
	 */
	public final static void configure(Properties props) {
		Properties defaultProperties = new Properties();
		HashMap<String, Properties> driversProps = new HashMap<String, Properties>();
		for (Enumeration<?> enumeration = props.propertyNames(); enumeration.hasMoreElements();) {
			String propertyName = (String) enumeration.nextElement();
			String value = props.getProperty(propertyName);
			int idx = propertyName.lastIndexOf('.');
			if (idx < 0) {
				defaultProperties.put(propertyName, value);
			} else {
				String prefix = propertyName.substring(0, idx);
				String name = propertyName.substring(idx + 1);
				Properties driverProperties = driversProps.get(prefix);
				if (driverProperties == null) {
					driverProperties = new Properties();
					driversProps.put(prefix, driverProperties);
				}
				driverProperties.put(name, value);
			}
		}
		// Merge with default properties
		synchronized (INSTANCES) {
			INSTANCES.clear();
			for (Entry<String, Properties> entry : driversProps.entrySet()) {
				String name = entry.getKey();
				Properties properties = new Properties();
				properties.putAll(defaultProperties);
				properties.putAll(entry.getValue());
				configure(name, properties);
			}
			if (INSTANCES.get(DEFAULT_INSTANCE_NAME) == null) {
				configure(DEFAULT_INSTANCE_NAME, defaultProperties);
			}
		}
	}

	/**
	 * Registers new {@linkplain Driver} under provided name with specified properties.
	 */
	public static void configure(String name, Properties props) {
		INSTANCES.put(name, new Driver(name, props));
	}

	/**
	 * Retrieves the default instance of this class that is configured according to the properties file (driver.properties)
	 * 
	 * @param instanceName
	 *            The name of the instance (corresponding to the prefix in the driver.properties file)
	 * 
	 * @return the named instance
	 */
	public final static Driver getInstance(String instanceName) {
		synchronized (INSTANCES) {
			if (instanceName == null)
				instanceName = DEFAULT_INSTANCE_NAME;
			if (INSTANCES.isEmpty()) {
				throw new ConfigurationException("Driver has not been configured and driver.properties file was not found");
			}
			Driver instance = INSTANCES.get(instanceName);
			if (instance == null) {
				throw new ConfigurationException("No configuration properties found for factory : " + instanceName);
			}
			return instance;
		}
	}

	/**
	 * Retrieves the default instance of this class that is configured according to the properties file (driver.properties)
	 * 
	 * @return the default instance
	 */
	public final static Driver getInstance() {
		return getInstance(DEFAULT_INSTANCE_NAME);
	}

	/**
	 * Method used to inject providers. Usefull mainly for unit testing purpose
	 * 
	 * @param instanceName
	 *            The name of the provider
	 * @param instance
	 *            The instance
	 */
	public final static void put(String instanceName, Driver instance) {
		synchronized (INSTANCES) {
			INSTANCES.put(instanceName, instance);
		}
	}
}
