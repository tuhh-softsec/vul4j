package net.onrc.onos.datastore;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.ramcloud.JRamCloud;
import edu.stanford.ramcloud.JRamCloud.ObjectDoesntExistException;
import edu.stanford.ramcloud.JRamCloud.ObjectExistsException;
import edu.stanford.ramcloud.JRamCloud.RejectRules;
import edu.stanford.ramcloud.JRamCloud.RejectRulesException;
import edu.stanford.ramcloud.JRamCloud.WrongVersionException;

/**
 * Class to represent a Table in RAMCloud
 *
 */
public class RCTable {
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(RCTable.class);

    private static final ConcurrentHashMap<String, RCTable> table_map = new ConcurrentHashMap<>();

    /**
     *
     * @param table
     *            Table to drop.
     * @note Instance passed must not be use after successful call.
     *
     */
    public static void dropTable(RCTable table) {
	JRamCloud rcClient = RCClient.getClient();
	// TODO mark the table instance as dropped?
	rcClient.dropTable(table.getTableName());
	table_map.remove(table.getTableName());
    }

    public static RCTable getTable(String tableName) {
	RCTable table = table_map.get(tableName);
	if (table == null) {
	    RCTable new_table = new RCTable(tableName);
	    RCTable existing_table = table_map
		    .putIfAbsent(tableName, new_table);
	    if (existing_table != null) {
		return existing_table;
	    } else {
		return new_table;
	    }
	}
	return table;
    }

    public static class Entry {
	public final byte[] key;
	public byte[] value;
	public long version;

	public Entry(byte[] key, byte[] value, long version) {
	    this.key = key;
	    this.value = value;
	    this.version = version;
	}
    }

    // finally the Table itself

    private final long rcTableId;
    private final String rcTableName;

    // private boolean isDropped = false;

    /**
     *
     * @note rcTableName must be unique cluster wide.
     * @param rcTableName
     */
    private RCTable(String rcTableName) {
	JRamCloud rcClient = RCClient.getClient();

	this.rcTableName = rcTableName;

	// FIXME Is it better to create table here or at getTable
	this.rcTableId = rcClient.createTable(rcTableName);
    }

    public long getTableId() {
	return this.rcTableId;
    }

    public String getTableName() {
	return this.rcTableName;
    }

    // TODO: Enumerate whole table?

    /**
     * Create a Key-Value entry on table.
     *
     * @param key
     * @param value
     * @return version of the created entry
     * @throws ObjectExistsException
     */
    public long create(final byte[] key, final byte[] value)
	    throws ObjectExistsException {

	JRamCloud rcClient = RCClient.getClient();

	RejectRules rules = new RejectRules();
	rules.rejectIfExists();

	try {
	    return rcClient.write(this.rcTableId, key, value, rules);
	} catch (ObjectExistsException e) {
	    throw e;
	} catch (RejectRulesException e) {
	    log.error("Unexpected RejectRulesException", e);
	    return JRamCloud.VERSION_NONEXISTENT;
	}
    }

    /**
     * Create a Key-Value entry on table, without existence checking.
     *
     * @param key
     * @param value
     * @return version of the created entry
     */
    public long forceCreate(final byte[] key, final byte[] value) {
	JRamCloud rcClient = RCClient.getClient();

	long updated_version = rcClient.write(this.rcTableId, key, value);
	return updated_version;

    }

    /**
     * Read a Key-Value entry from table.
     *
     * @param key
     * @return Corresponding {@link Entry}
     * @throws ObjectDoesntExistException
     */
    public Entry read(final byte[] key) throws ObjectDoesntExistException {

	JRamCloud rcClient = RCClient.getClient();

	RejectRules rules = new RejectRules();
	rules.rejectIfDoesntExists();
	try {
	    JRamCloud.Object rcObj = rcClient.read(this.rcTableId, key, rules);
	    return new Entry(rcObj.key, rcObj.value, rcObj.version);
	} catch (ObjectDoesntExistException e) {
	    throw e;
	} catch (RejectRulesException e) {
	    log.error("Unexpected RejectRulesException", e);
	    return null;
	}
    }

    /**
     * Update an existing Key-Value entry in table.
     *
     * @param key
     * @param value
     * @param version
     *            expected version in the data store
     * @return version after update
     * @throws ObjectDoesntExistException
     * @throws WrongVersionException
     */
    public long update(final byte[] key, final byte[] value, final long version)
	    throws ObjectDoesntExistException, WrongVersionException {

	JRamCloud rcClient = RCClient.getClient();

	RejectRules rules = new RejectRules();
	rules.rejectIfDoesntExists();
	rules.rejectIfNeVersion(version);

	try {
	    return rcClient.write(this.rcTableId, key, value, rules);
	} catch (ObjectDoesntExistException|WrongVersionException e) {
	    throw e;
	} catch (RejectRulesException e) {
	    log.error("Unexpected RejectRulesException", e);
	    return JRamCloud.VERSION_NONEXISTENT;
	}
    }

    /**
     * Update an existing Key-Value entry in table, without checking version.
     *
     * @param key
     * @param value
     * @return version after update
     * @throws ObjectDoesntExistException
     */
    public long update(final byte[] key, final byte[] value)
	    throws ObjectDoesntExistException {

	JRamCloud rcClient = RCClient.getClient();

	RejectRules rules = new RejectRules();
	rules.rejectIfDoesntExists();

	try {
	    return rcClient.write(this.rcTableId, key, value, rules);
	} catch (ObjectDoesntExistException e) {
	    throw e;
	} catch (RejectRulesException e) {
	    log.error("Unexpected RejectRulesException", e);
	    return JRamCloud.VERSION_NONEXISTENT;
	}
    }

    /**
     * Remove an existing Key-Value entry in table
     *
     * @param key
     * @param version
     *            expected version in the data store
     * @return version of removed object
     * @throws ObjectDoesntExistException
     * @throws WrongVersionException
     */
    public long delete(final byte[] key, final long version)
	    throws ObjectDoesntExistException, WrongVersionException {
	JRamCloud rcClient = RCClient.getClient();

	RejectRules rules = new RejectRules();
	rules.rejectIfDoesntExists();
	rules.rejectIfNeVersion(version);

	try {
	    return rcClient.remove(this.rcTableId, key, rules);
	} catch (ObjectDoesntExistException|WrongVersionException e) {
	    throw e;
	} catch (RejectRulesException e) {
	    log.error("Unexpected RejectRulesException", e);
	    return JRamCloud.VERSION_NONEXISTENT;
	}
    }

    /**
     * Remove a Key-Value entry in table
     *
     * @param key
     * @return version of removed object or -1, if it did not exist.
     */
    public long forceDelete(final byte[] key) {
	JRamCloud rcClient = RCClient.getClient();
	long removed_version = rcClient.remove(this.rcTableId, key);
	return removed_version;
    }
}
