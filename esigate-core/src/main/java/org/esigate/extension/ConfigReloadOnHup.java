package org.esigate.extension;

import java.io.File;
import java.util.Properties;

import org.esigate.Driver;
import org.esigate.DriverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * This extension reloads configuration when signal HUP is received. On POSIX
 * systems, this signal is sent using : <code>
 * kill -1 &lt;esigatepid&gt;
 * </code>
 * <p>
 * This only works on configuration defined using "esigate.config" system
 * property.
 * <p>
 * This class relies on the sun.misc package and may not work on all JVM.
 * 
 * @author Nicolas Richeton
 * 
 */
@SuppressWarnings("restriction")
public class ConfigReloadOnHup implements Extension {
	protected static final Logger LOG = LoggerFactory.getLogger(ConfigReloadOnHup.class);
	static String signalName = "HUP";

	private static File configuration = null;

	@Override
	public void init(Driver driver, Properties properties) {
		// Initialization is done is static block.
	}

	static SignalHandler sh = new SignalHandler() {
		@Override
		public void handle(Signal signal) {
			if (signalName.equals(signal.getName())) {
				LOG.warn("Signal " + signalName + " received. Reloading configuration.");
				DriverFactory.configure();
			}

		}
	};

	// Static block to ensure signal handler is added only once.
	static {
		String envPath = System.getProperty("esigate.config");
		if (envPath != null) {
			configuration = new File(envPath);

			// Register for signal
			Signal signal = new Signal(signalName);
			Signal.handle(signal, sh);

			LOG.info("Will reload configuration from {} on signal {}", configuration.getAbsoluteFile(),
					Integer.valueOf(signal.getNumber()));
		} else {
			// Do nothing if configuration is loaded from the classpath
			LOG.warn("Cannot reload configuration from classpath. Please use -Desigate.config");
		}
	}
}
