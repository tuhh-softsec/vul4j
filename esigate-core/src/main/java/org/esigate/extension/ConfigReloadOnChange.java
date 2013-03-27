package org.esigate.extension;

import java.io.File;
import java.util.Properties;

import org.esigate.Driver;
import org.esigate.DriverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This extension reloads configuration when esigate.properties is updated.
 * <p>
 * This only works on configuration defined using "esigate.config" system
 * property.
 * <p>
 * The polling frequency can be set by adding the following property to esigate configuration : 
 * <code>
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
	private static final String CONFIG_RELOAD_DELAY = "configReloadDelay";

	// Do not poll too fast.  (ms).
	private static final int SPEED_LIMIT = 100;
	private static final Logger LOG = LoggerFactory
			.getLogger(ConfigReloadOnChange.class);

	private static File configuration = null;
	private static long lastModified = -1;
	private static long delay = 5000;

	// this variable will be used in the future, when extension supports
	// shutdown event.
	private static boolean stop = false;

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

	public void init(Driver driver, Properties properties) {

		// Do nothing if configuration is loaded from the classpath
		if( configuration == null ){
			LOG.warn("Cannot reload configuration from classpath. Please use -Desigate.config");
			return;
		}
		
		// Load configuration
		String delayAsString = properties.getProperty(CONFIG_RELOAD_DELAY);
		if (delayAsString != null) {
			try {
				// Try to convert as long
				long configDelay = Long.parseLong(delayAsString);

				// Do not watch faster than SPEED_LIMIT
				if (configDelay < SPEED_LIMIT) {
					delay = SPEED_LIMIT;
				}
			} catch (NumberFormatException e) {
				LOG.warn("Unable to convert {}={} as number",
						CONFIG_RELOAD_DELAY, delayAsString);
			}
		}

		LOG.info("Will reload configuration every {}ms if {} is modified",
				delay, configuration.getAbsoluteFile());
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
