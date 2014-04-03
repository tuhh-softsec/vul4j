package net.onrc.onos.core.datastore;

import java.util.Collection;
import java.util.List;

import net.onrc.onos.core.datastore.IKVTable.IKVEntry;

/**
 * Interface for a client class used to access the Key-Value store
 */
public interface IKVClient {

    public IKVTable getTable(final String tableName);

    /**
     * Drop table.
     *
     * Behavior of IKVTable instances accessing dropped table is undefined.
     *
     * @param table IKVTable to drop.
     */
    public void dropTable(IKVTable table);

    /**
     * Create a Key-Value entry on table.
     *
     * @param tableId
     * @param key
     * @param value
     * @return version of the created entry
     * @throws ObjectExistsException
     */
    public long create(IKVTableID tableId, byte[] key, byte[] value) throws ObjectExistsException;

    /**
     * Create a Key-Value entry on table, without existence checking.
     *
     * @param tableId
     * @param key
     * @param value
     * @return version of the created entry
     */
    public long forceCreate(IKVTableID tableId, byte[] key, byte[] value);

    /**
     * Read a Key-Value entry from table.
     *
     * @param tableId
     * @param key
     * @return Corresponding {@link IKVEntry}
     * @throws ObjectDoesntExistException
     */
    public IKVEntry read(IKVTableID tableId, byte[] key) throws ObjectDoesntExistException;

    /**
     * Update an existing Key-Value entry in table.
     *
     * @param tableId
     * @param key
     * @param value
     * @param version
     *            expected version in the data store
     * @return version after update
     * @throws ObjectDoesntExistException
     * @throws WrongVersionException
     */
    public long update(IKVTableID tableId, byte[] key, byte[] value, long version)
            throws ObjectDoesntExistException, WrongVersionException;

    /**
     * Update an existing Key-Value entry in table, without checking version.
     *
     * FIXME remove this method and use forceCreate for this purpose?
     * @param tableId
     * @param key
     * @param value
     * @return version after update
     * @throws ObjectDoesntExistException
     */
    @Deprecated
    public long update(IKVTableID tableId, byte[] key, byte[] value)
            throws ObjectDoesntExistException;

    // TODO Adding serialized value as parameter to this interface may
    // give an option to improve performance on some backends.
    /**
     * Remove an existing Key-Value entry in table
     *
     * @param tableId
     * @param key
     * @param version
     *            expected version in the data store
     * @return version of removed object
     * @throws ObjectDoesntExistException
     * @throws WrongVersionException
     */
    public long delete(IKVTableID tableId, byte[] key, long version)
            throws ObjectDoesntExistException, WrongVersionException;

    /**
     * Remove a Key-Value entry in table
     *
     * @param tableId
     * @param key
     * @return version of removed object or -1, if it did not exist.
     */
    public long forceDelete(IKVTableID tableId, byte[] key);

    /**
     * Get all the entries in table.
     * @param tableId
     * @return entries in this table.
     */
    public Iterable<IKVEntry> getAllEntries(IKVTableID tableId);

    /**
     *
     * @see #create(IKVTableID, byte[], byte[])
     *
     * @return IMultiOpEntry for this operation
     *
     */
    public IMultiEntryOperation createOp(IKVTableID tableId, byte[] key, byte[] value);

    public IMultiEntryOperation forceCreateOp(IKVTableID tableId, byte[] key,
            byte[] value);

    public IMultiEntryOperation readOp(IKVTableID tableId, byte[] key);

    public IMultiEntryOperation updateOp(IKVTableID tableId, byte[] key, byte[] value,
            long version);

    public IMultiEntryOperation deleteOp(IKVTableID tableId, byte[] key, byte[] value,
            long version);

    public IMultiEntryOperation forceDeleteOp(IKVTableID tableId, byte[] key);

    /**
     * Batch delete operation
     * @param ops delete operations
     * @return true if failed operation exists
     */
    public boolean multiDelete(final Collection<IMultiEntryOperation> ops);

    /**
     * Batch write operation
     * @param ops write operations
     * @return true if failed operation exists
     */
    public boolean multiWrite(final List<IMultiEntryOperation> ops);

    /**
     * Batch read operation
     * @param ops read operations
     * @return true if failed operation exists
     */
    public boolean multiRead(final Collection<IMultiEntryOperation> ops);

    /**
     * Version number which represents that the object does not exist, or has
     * never been read the DB before.
     */
    public long VERSION_NONEXISTENT();

}
