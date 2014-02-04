package net.onrc.onos.datastore;

import edu.stanford.ramcloud.JRamCloud;

public class RCClient {

    // Value taken from RAMCloud's Status.h
    // FIXME These constants should be defined by JRamCloud
    public static final int STATUS_OK = 0;

    // FIXME come up with a proper way to retrieve configuration
    public static final int MAX_MULTI_READS = Integer.valueOf(System
	    .getProperty("ramcloud.max_multi_reads", "400"));

    public static final int MAX_MULTI_WRITES = Integer.valueOf(System
	    .getProperty("ramcloud.max_multi_writes", "800"));

    private static final ThreadLocal<JRamCloud> tlsRCClient = new ThreadLocal<JRamCloud>() {
	@Override
	protected JRamCloud initialValue() {
	    // FIXME come up with a proper way to retrieve configuration
	    return new JRamCloud(System.getProperty("ramcloud.coordinator",
		    "fast+udp:host=127.0.0.1,port=12246"));
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

}
