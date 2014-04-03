package net.onrc.onos.core.datastore;


/**
 * Interface for a class to represent a Table in a Key-Value store
 */
public interface IKVTable {

    /**
     * Version number which represents that the object does not exist, or has
     * never been read the DB before.
     */
    public long VERSION_NONEXISTENT();

    /**
     * Interface for a class to represent an entry in Table.
     */
    public static interface IKVEntry {

        public byte[] getKey();

        public byte[] getValue();

        public long getVersion();

    }

    /**
     * @return ID to identify this table.
     */
    public IKVTableID getTableId();

    /**
     * Create a Key-Value entry on table.
     *
     * @param key
     * @param value
     * @return version of the created entry
     * @throws ObjectExistsException
     */
    public long create(byte[] key, byte[] value) throws ObjectExistsException;

    /**
     * Create a Key-Value entry on table, without existence checking.
     *
     * @param key
     * @param value
     * @return version of the created entry
     */
    public long forceCreate(byte[] key, byte[] value);

    /**
     * Read a Key-Value entry from table.
     *
     * @param key
     * @return Corresponding {@link IKVEntry}
     * @throws ObjectDoesntExistException
     */
    public IKVEntry read(byte[] key) throws ObjectDoesntExistException;

    /**
     * Update an existing Key-Value entry in table.
     *
     * @param key
     * @param value
     * @param version expected version in the data store
     * @return version after update
     * @throws ObjectDoesntExistException
     * @throws WrongVersionException
     */
    public long update(byte[] key, byte[] value, long version)
            throws ObjectDoesntExistException, WrongVersionException;

    /**
     * Update an existing Key-Value entry in table, without checking version.
     *
     * @param key
     * @param value
     * @return version after update
     * @throws ObjectDoesntExistException
     */
    public long update(byte[] key, byte[] value)
            throws ObjectDoesntExistException;

    /**
     * Remove an existing Key-Value entry in table
     *
     * @param key
     * @param version expected version in the data store
     * @return version of removed object
     * @throws ObjectDoesntExistException
     * @throws WrongVersionException
     */
    public long delete(byte[] key, long version)
            throws ObjectDoesntExistException, WrongVersionException;

    /**
     * Remove a Key-Value entry in table
     *
     * @param key
     * @return version of removed object or VERSION_NONEXISTENT, if it did not exist.
     */
    public long forceDelete(byte[] key);

    /**
     * Get all the entries in table.
     *
     * @return entries in this table.
     */
    public Iterable<IKVEntry> getAllEntries();
}