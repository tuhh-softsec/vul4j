package net.onrc.onos.core.datastore.ramcloud;

import java.util.Objects;

import net.onrc.onos.core.datastore.IKVTableID;

public class RCTableID implements IKVTableID {
    private final String tableName;
    private long tableID;

    public RCTableID(String tableName) {
        this.tableName = tableName;
        this.tableID = 0;
    }

    @Override
    public String getTableName() {
        return tableName;
    }

    // following is RAMCloud specific

    public long getTableID() {
        if (tableID != 0) {
            return tableID;
        }
        tableID = RCClient.getJRamCloudClient().createTable(tableName);
        return tableID;
    }

    void resetTableID() {
        this.tableID = 0;
    }

    @Override
    public String toString() {
        return "[" + tableName + "]@" + getTableID();
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableName, getTableID());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        RCTableID other = (RCTableID) obj;
        return Objects.equals(tableName, other.tableName)
                && Objects.equals(getTableID(), other.getTableID());
    }
}
