package net.onrc.onos.datastore;

/**
 * Interface for a class to specify which K-V pair to batch read/write/delete.
 */
public interface IMultiEntryOperation {

    public enum STATUS {
	NOT_EXECUTED, SUCCESS, FAILED
    }

    public enum OPERATION {
	CREATE, FORCE_CREATE, UPDATE, READ, DELETE, FORCE_DELETE
    }

    public boolean hasSucceeded();

    public STATUS getStatus();

    public IKVTableID getTableId();

    public byte[] getKey();

    public byte[] getValue();

    public long getVersion();

    public OPERATION getOperation();

}
