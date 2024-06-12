package net.onrc.onos.core.datastore.hazelcast;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import net.onrc.onos.core.datastore.IKVTableID;
import net.onrc.onos.core.datastore.IMultiEntryOperation;
import net.onrc.onos.core.datastore.hazelcast.HZTable.VersionedValue;
import net.onrc.onos.core.datastore.internal.IModifiableMultiEntryOperation;
import net.onrc.onos.core.datastore.utils.ByteArrayUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HZMultiEntryOperation implements IMultiEntryOperation, IModifiableMultiEntryOperation {
    private static final Logger log = LoggerFactory.getLogger(HZMultiEntryOperation.class);

    private final HZTable table;
    private final byte[] key;
    protected final OPERATION operation;
    private STATUS status;

    // for read op
    private Future<VersionedValue> future;
    // for write op
    private VersionedValue writeValue;

    /**
     * Constructor for Read/ForceDelete Operation.
     *
     * @param table
     * @param key
     * @param operation
     */
    @SuppressFBWarnings(value = "EI_EXPOSE_REP2",
                        justification = "TODO: Store a copy of the object?")
    public HZMultiEntryOperation(final HZTable table, final byte[] key, final OPERATION operation) {
        this.table = table;
        this.key = key;
        this.status = STATUS.NOT_EXECUTED;
        this.operation = operation;

        this.future = null;
        this.writeValue = null;
    }

    /**
     * Constructor for Other Operations.
     *
     * @param table
     * @param key
     * @param value
     * @param version
     * @param operation
     */
    @SuppressFBWarnings(value = "EI_EXPOSE_REP2",
                        justification = "TODO: Store a copy of the object?")
    public HZMultiEntryOperation(final HZTable table, final byte[] key, final byte[] value, final long version, final OPERATION operation) {
        this.table = table;
        this.key = key;
        this.status = STATUS.NOT_EXECUTED;
        this.operation = operation;

        this.future = null;
        this.writeValue = new VersionedValue(value, version);
    }

    @Override
    public boolean hasSucceeded() {

        VersionedValue value = get();
        return (value != null) && (this.status == STATUS.SUCCESS);
    }

    @Override
    public STATUS getStatus() {
        get();
        return status;
    }

    @Override
    public IKVTableID getTableId() {
        return table;
    }

    @Override
    @SuppressFBWarnings(value = "EI_EXPOSE_REP",
                        justification = "TODO: Return a copy of the object?")
    public byte[] getKey() {
        return key;
    }

    @Override
    public byte[] getValue() {
        if (future != null) {
            VersionedValue value = get();
            return value.getValue();
        }
        return writeValue.getValue();
    }

    @Override
    public long getVersion() {
        if (future != null) {
            VersionedValue value = get();
            return value.getVersion();
        }
        return writeValue.getVersion();
    }

    @Override
    public OPERATION getOperation() {
        return operation;
    }

    /**
     * Evaluate Future object and set Status and Value+Version.
     *
     * @return the value read or null on failure.
     */
    private VersionedValue get() {
        try {
            VersionedValue value = future.get();
            if (value == null) {
                setStatus(STATUS.FAILED);
                return null;
            } else {
                setValue(value.getValue(), value.getVersion());
                setStatus(STATUS.SUCCESS);
                return value;
            }
        } catch (CancellationException | InterruptedException | ExecutionException e) {
            log.error(this + " has failed.", e);
            setStatus(STATUS.FAILED);
            return null;
        }
    }

    @Override
    public void setValue(final byte[] value, final long version) {
        writeValue = new VersionedValue(value, version);
        setVersion(version);
    }

    @Override
    public void setVersion(final long version) {
        if (writeValue == null) {
            writeValue = new VersionedValue(null, version);
        }
    }

    @Override
    public void setStatus(final STATUS status) {
        this.status = status;
    }

    @Override
    public IModifiableMultiEntryOperation getActualOperation() {
        return this;
    }

    void setFuture(final Future<VersionedValue> future) {
        this.future = future;
    }

    @Override
    public String toString() {
        return "[HZMultiEntryOperation table=" + table + ", key="
                + ByteArrayUtil.toHexStringBuffer(key, ":") + ", operation=" + operation
                + ", status=" + status + ", writeValue=" + writeValue + "]";
    }
}