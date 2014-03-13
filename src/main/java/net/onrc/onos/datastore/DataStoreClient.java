package net.onrc.onos.datastore;

import net.onrc.onos.datastore.ramcloud.RCClient;

public class DataStoreClient {
    public static IKVClient getClient() {
	// TODO read config and return appropriate IKVClient
	return RCClient.getClient();
    }
}
