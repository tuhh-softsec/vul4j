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
package org.esigate.extension;

import java.io.File;
import java.util.Properties;

import org.esigate.Driver;
import org.esigate.DriverFactory;
import org.esigate.util.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This extension reloads configuration when esigate.properties is updated.
 * <p>
 * This only works on configuration defined using "esigate.config" system
 * property.
 * <p>
 * The polling frequency can be set by adding the following property to esigate
 * configuration : <code>
 * &lt;driverid&gt;.configReloadDelay
 * </code>
 * <p>
 * Default polling frequency is 5 seconds.
 * <p>
 * This class is not intended to use in production.
 * 
 * @author Nicolas Richeton
 * 
 */
public class ConfigReloadOnChange implements Extension {
	private static final long DEFAULT_RELOAD_DELAY = 5000;
	/**
	 * The wait time (ms) between to check for configuration change.
	 * 
	 */
	public static Parameter CONFIG_RELOAD_DELAY = new Parameter("configReloadDelay", String.valueOf(DEFAULT_RELOAD_DELAY));

	// Do not poll too fast. (ms).
	private static final int SPEED_LIMIT = 100;
	protected static final Logger LOG = LoggerFactory.getLogger(ConfigReloadOnChange.class);

	protected static File configuration = null;
	protected static long lastModified = -1;
	protected static long delay = DEFAULT_RELOAD_DELAY;

	// this variable will be used in the future, when extension supports
	// shutdown event.
	protected static boolean stop = false;

	static Thread fileWatcher = new Thread() {
		@Override
		public void run() {
			while (!stop) {
				if (configuration != null && configuration.exists()) {
					if (configuration.lastModified() != lastModified) {
						lastModified = configuration.lastModified();

						// Reload
						LOG.warn("Configuration file changed : reloading.");
						DriverFactory.configure();
					}
				}

				// Wait before checking again
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					stop = true;
					LOG.warn("Watching interrupted.");
				}
			}

			LOG.info("Stopped watching {}.", configuration.getAbsoluteFile());
		}
	};

	@Override
	public void init(Driver driver, Properties properties) {

		// Do nothing if configuration is loaded from the classpath
		if (configuration == null) {
			LOG.warn("Cannot reload configuration from classpath. Please use -Desigate.config");
			return;
		}

		// Load configuration
		try {
			// Try to convert as long
			long configDelay = CONFIG_RELOAD_DELAY.getValueLong(properties);

			// Do not watch faster than SPEED_LIMIT
			if (configDelay < SPEED_LIMIT) {
				delay = SPEED_LIMIT;
			}
		} catch (NumberFormatException e) {
			LOG.warn("Unable to convert {}={} as number", CONFIG_RELOAD_DELAY.name,
					CONFIG_RELOAD_DELAY.getValueString(properties));
		}

		LOG.info("Will reload configuration every {}ms if {} is modified", Long.valueOf(delay), configuration.getAbsoluteFile());
	}

	// This static block ensure thread is started only once.
	static {
		String envPath = System.getProperty("esigate.config");
		if (envPath != null) {
			configuration = new File(envPath);
		}

		// Start watcher
		fileWatcher.start();
	}

}
