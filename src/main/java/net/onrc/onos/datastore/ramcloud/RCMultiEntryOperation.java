package net.onrc.onos.datastore.ramcloud;

import net.onrc.onos.datastore.IKVTableID;
import net.onrc.onos.datastore.IMultiEntryOperation;
import net.onrc.onos.datastore.internal.IModifiableMultiEntryOperation;
import net.onrc.onos.datastore.ramcloud.RCTable.Entry;

// FIXME move or extract this
public class RCMultiEntryOperation implements IMultiEntryOperation, IModifiableMultiEntryOperation {
    protected final RCTableID tableId;
    protected final Entry entry;
    protected final OPERATION operation;
    protected STATUS status;

    @Override
    public boolean hasSucceeded() {
	return this.status == STATUS.SUCCESS;
    }

    @Override
    public STATUS getStatus() {
	return status;
    }

    @Override
    public IKVTableID getTableId() {
	return tableId;
    }

    @Override
    public byte[] getKey() {
	return entry.key;
    }

    @Override
    public byte[] getValue() {
	return entry.value;
    }

    @Override
    public long getVersion() {
	return entry.version;
    }

    @Override
    public OPERATION getOperation() {
	return operation;
    }

    @Override
    public void setStatus(final STATUS status) {
	this.status = status;
    }

    @Override
    public void setValue(byte[] value, final long version) {
	this.entry.setValue(value);
	setVersion(version);
    }

    @Override
    public void setVersion(final long version) {
	this.entry.setVersion(version);
    }


    public RCMultiEntryOperation(final IKVTableID tableId, final Entry entry, final OPERATION operation) {
	this.tableId = (RCTableID) tableId;
	this.operation = operation;

	this.entry = entry;
	this.status = STATUS.NOT_EXECUTED;
    }

    public static IMultiEntryOperation create(final IKVTableID tableId, final byte[] key, final byte[] value) {
	return  new RCMultiEntryOperation(tableId, new Entry(key,value, RCClient.VERSION_NONEXISTENT), OPERATION.CREATE);
    }

    public static IMultiEntryOperation forceCreate(final IKVTableID tableId, final byte[] key, final byte[] value) {
	return  new RCMultiEntryOperation(tableId, new Entry(key,value, RCClient.VERSION_NONEXISTENT), OPERATION.FORCE_CREATE);
    }

    /**
     * Constructor for READ operation.
     *
     * @param tableId table to read from
     * @param key key of an Entry to read
     */
    public static IMultiEntryOperation read(final IKVTableID tableId, final byte[] key) {
	return new RCMultiEntryOperation(tableId, new Entry(key), OPERATION.READ);
    }

    public static IMultiEntryOperation update(final IKVTableID tableId, final byte[] key, final byte[] value, final long version) {
	return  new RCMultiEntryOperation(tableId, new Entry(key,value, version), OPERATION.UPDATE);
    }

    public static IMultiEntryOperation delete(final IKVTableID tableId, final byte[] key, final byte[] value, final long version) {
	return  new RCMultiEntryOperation(tableId, new Entry(key,value, version), OPERATION.DELETE);
    }

    public static IMultiEntryOperation forceDelete(final IKVTableID tableId, final byte[] key) {
	return  new RCMultiEntryOperation(tableId, new Entry(key), OPERATION.FORCE_DELETE);
    }

    @Override
    public IModifiableMultiEntryOperation getActualOperation() {
	return this;
    }

    @Override
    public String toString() {
	return "[RCMultiEntryOperation tableId=" + tableId + ", entry=" + entry
	        + ", operation=" + operation + ", status=" + status + "]";
    }
}
