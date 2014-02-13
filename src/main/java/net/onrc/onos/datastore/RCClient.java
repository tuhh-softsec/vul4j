package net.onrc.onos.datastore;

import java.io.File;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import edu.stanford.ramcloud.JRamCloud;

public class RCClient {

    private static final String DB_CONFIG_FILE = "conf/ramcloud.conf";
    public static final Configuration config = getConfiguration();

    // Value taken from RAMCloud's Status.h
    // FIXME These constants should be defined by JRamCloud
    public static final int STATUS_OK = 0;

    // FIXME come up with a proper way to retrieve configuration
    public static final int MAX_MULTI_READS = Math.max(1, Integer
	    .valueOf(System.getProperty("ramcloud.max_multi_reads", "400")));

    public static final int MAX_MULTI_WRITES = Math.max(1, Integer
	    .valueOf(System.getProperty("ramcloud.max_multi_writes", "800")));

    private static final ThreadLocal<JRamCloud> tlsRCClient = new ThreadLocal<JRamCloud>() {
	@Override
	protected JRamCloud initialValue() {
	    return new JRamCloud(getCoordinatorUrl(config));
	}
    };

    /**
     * @return JRamCloud instance intended to be used only within the
     *         SameThread.
     * @note Do not store the returned instance in a member variable, etc. which
     *       may be accessed later by another thread.
     */
    static JRamCloud getClient() {
	return tlsRCClient.get();
    }

    public static final Configuration getConfiguration() {
	    final File configFile = new File(System.getProperty("ramcloud.config.path", DB_CONFIG_FILE));
	    return getConfiguration(configFile);
    }

    public static final Configuration getConfiguration(final File configFile) {
	if (configFile == null) {
	    throw new IllegalArgumentException("Need to specify a configuration file or storage directory");
	}

	if (!configFile.isFile()) {
	    throw new IllegalArgumentException("Location of configuration must be a file");
	}

	try {
	    return new PropertiesConfiguration(configFile);
	} catch (ConfigurationException e) {
	    throw new IllegalArgumentException("Could not load configuration at: " + configFile, e);
	}
    }

    public static String getCoordinatorUrl(final Configuration configuration) {
	final String coordinatorIp = configuration.getString("ramcloud.coordinatorIp", "fast+udp:host=127.0.0.1");
	final String coordinatorPort = configuration.getString("ramcloud.coordinatorPort", "port=12246");
	final String coordinatorURL = coordinatorIp + "," + coordinatorPort;
	return coordinatorURL;
    }
}
