package net.onrc.onos.datastore;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.ramcloud.JRamCloud;
import edu.stanford.ramcloud.JRamCloud.ObjectDoesntExistException;
import edu.stanford.ramcloud.JRamCloud.ObjectExistsException;
import edu.stanford.ramcloud.JRamCloud.RejectRules;
import edu.stanford.ramcloud.JRamCloud.WrongVersionException;

/**
 * Class to represent a Table in RAMCloud
 *
 */
public class RCTable {
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

    // Reject if exist
    public long create(final byte[] key, final byte[] value)
	    throws ObjectExistsException {

	JRamCloud rcClient = RCClient.getClient();

	RejectRules rules = rcClient.new RejectRules();
	rules.setExists();

	long updated_version = rcClient.writeRule(this.rcTableId, key, value,
	        rules);
	return updated_version;
    }

    // read
    public Entry read(final byte[] key) throws ObjectDoesntExistException {

	JRamCloud rcClient = RCClient.getClient();

	// FIXME underlying JRamCloud cannot detect "not exist"
	// RejectRules rules = rcClient.new RejectRules();
	// rules.setDoesntExists();
	// JRamCloud.Object rcObj = rcClient.read(this.rcTableId, key, rules);
	JRamCloud.Object rcObj = rcClient.read(this.rcTableId, key);

	return new Entry(rcObj.key, rcObj.value, rcObj.version);
    }

    // Reject if version neq
    public long update(final byte[] key, final byte[] value, final long version)
	    throws ObjectDoesntExistException, WrongVersionException {

	JRamCloud rcClient = RCClient.getClient();

	RejectRules rules = rcClient.new RejectRules();
	rules.setDoesntExists();
	rules.setNeVersion(version);

	long updated_version = rcClient.writeRule(this.rcTableId, key, value,
	        rules);
	return updated_version;
    }

    // Reject if not exist
    public long update(final byte[] key, final byte[] value)
	    throws ObjectDoesntExistException {

	JRamCloud rcClient = RCClient.getClient();

	RejectRules rules = rcClient.new RejectRules();
	rules.setDoesntExists();

	long updated_version = rcClient.writeRule(this.rcTableId, key, value,
	        rules);
	return updated_version;

    }

    // Reject if not exist
    public long delete(final byte[] key) throws ObjectDoesntExistException {
	JRamCloud rcClient = RCClient.getClient();

	// FIXME underlying JRamCloud does not support cond remove
	RejectRules rules = rcClient.new RejectRules();
	rules.setDoesntExists();

	long removed_version = rcClient.remove(this.rcTableId, key, rules);
	return removed_version;
    }

}
