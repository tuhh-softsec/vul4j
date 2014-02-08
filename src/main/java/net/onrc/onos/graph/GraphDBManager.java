/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.onrc.onos.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphDBManager {
    private static ThreadLocal<HashMap<String, DBConnection>> connections = new ThreadLocal<HashMap<String, DBConnection>>();
    private static DBOperation operation = null;
    private final static String DB_CONFIG_FILE = "conf/ramcloud.conf";

    static Map<String, DBConnection> getConnectionMap() {
        if (connections.get() == null) {
            connections.set(new HashMap<String, DBConnection>());
        }
        return connections.get();
    }

    public static DBOperation getDBOperation() {
        return getDBOperation("ramcloud");
    }

    public static DBOperation getDBOperation(final String dbStore) {
	if (dbStore.equals("ramcloud")) {
	    operation = new RamCloudDBOperation();
	} else if (dbStore.equals("titan")) {
	    operation = new TitanDBOperation();
	}
	if (operation != null) {
	    operation.conn = GraphDBManager.getConnection(dbStore, DB_CONFIG_FILE);
	}
        return operation;
    }

    public static DBConnection getConnection(final String dbStore, final String dbConfigFile) {
        DBConnection conn = getConnectionMap().get(dbStore);
        if (conn == null) {
            if (dbStore.equals("ramcloud")) {
                conn = new RamCloudDBConnection(dbConfigFile);
            } else if (dbStore.equals("titan")) {
                conn = new TitanDBConnection(dbConfigFile);
            }

            GraphDBManager.getConnectionMap().put(dbStore, conn);
        } else {
            GraphDBManager.getConnectionMap().get(dbStore);
        }
        return conn;
    }

    static List<DBConnection> getConnections() {
        return new ArrayList<DBConnection>(getConnectionMap().values());
    }
}
