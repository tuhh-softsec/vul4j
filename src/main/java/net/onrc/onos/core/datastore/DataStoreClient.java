package net.onrc.onos.core.datastore;

import net.onrc.onos.core.datastore.hazelcast.HZClient;
import net.onrc.onos.core.datastore.ramcloud.RCClient;

// This class probably need to be a service
public class DataStoreClient {
    private static final String BACKEND = System.getProperty("net.onrc.onos.core.datastore.backend", "hazelcast");

    // Suppresses default constructor, ensuring non-instantiability.
    private DataStoreClient() {}

    public static IKVClient getClient() {
        // TODO read config and return appropriate IKVClient
        switch (BACKEND) {
        case "ramcloud":
            return RCClient.getClient();
        case "hazelcast":
            return HZClient.getClient();
        default:
            return HZClient.getClient();
        }
    }


}
