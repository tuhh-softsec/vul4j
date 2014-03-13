package net.onrc.onos.datastore.internal;

import net.onrc.onos.datastore.IMultiEntryOperation;

/**
 * Interface for backend to realize IMultiEntryOperation.
 *
 * Backend implementation must use these interfaces to update IMultiEntryOperation
 * in order to support KVObject.
 */
public interface IModifiableMultiEntryOperation extends IMultiEntryOperation {

    /**
     * Set value and version.
     *
     * Expected to be called on multiRead implementations.
     * @param value
     * @param version
     */
    public void setValue(final byte[] value, final long version);

    /**
     * Update version of the value.
     *
     * Expected to be called on multiWrite, multiRead implementations.
     * @param version
     */
    public void setVersion(long version);

    /**
     * Update status.
     *
     * Backend implementation is expected to update to SUCCESS or FAILED after
     * datastore operation.
     * @param status
     */
    public void setStatus(STATUS status);

    /**
     * Return actual IModifiableMultiEntryOperation if is a wrapper, this otherwise.
     * @return actual IModifiableMultiEntryOperation directly interact with data store
     */
    public IModifiableMultiEntryOperation getActualOperation();
}
