package net.onrc.onos.datastore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.onrc.onos.datastore.RCObject.WriteOp.STATUS;
import net.onrc.onos.datastore.RCTable.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import edu.stanford.ramcloud.JRamCloud;
import edu.stanford.ramcloud.JRamCloud.MultiReadObject;
import edu.stanford.ramcloud.JRamCloud.MultiWriteObject;
import edu.stanford.ramcloud.JRamCloud.MultiWriteRspObject;
import edu.stanford.ramcloud.JRamCloud.ObjectDoesntExistException;
import edu.stanford.ramcloud.JRamCloud.ObjectExistsException;
import edu.stanford.ramcloud.JRamCloud.RejectRules;
import edu.stanford.ramcloud.JRamCloud.TableEnumerator;
import edu.stanford.ramcloud.JRamCloud.TableEnumerator2;
import edu.stanford.ramcloud.JRamCloud.WrongVersionException;

/**
 * Class to represent an Object represented as a single K-V pair Value blob.
 *
 */
public class RCObject {
    private static final Logger log = LoggerFactory.getLogger(RCObject.class);
    /**
     * Version number which represents that the object doesnot exist, or hase
     * never read the DB before.
     */
    public static final long VERSION_NONEXISTENT = 0L;

    // Each Object should prepare their own serializer, which has required
    // objects registered.
    private static final ThreadLocal<Kryo> defaultKryo = new ThreadLocal<Kryo>() {
	@Override
	protected Kryo initialValue() {
	    Kryo kryo = new Kryo();
	    // kryo.setRegistrationRequired(true);
	    // TODO TreeMap or just Map
	    // kryo.register(TreeMap.class);
	    kryo.setReferences(false);
	    return kryo;
	}
    };

    private final RCTable table;
    private final byte[] key;
    private byte[] value;
    private long version;

    private Map<Object, Object> propertyMap;

    public RCObject(RCTable table, byte[] key) {
	this(table, key, null, VERSION_NONEXISTENT);
    }

    public RCObject(RCTable table, byte[] key, byte[] value, long version) {
	if (table == null) {
	    throw new IllegalArgumentException("table cannot be null");
	}
	if (key == null) {
	    throw new IllegalArgumentException("key cannot be null");
	}
	this.table = table;
	this.key = key;
	this.value = value;
	this.version = version;
	this.propertyMap = new HashMap<Object, Object>();

	if (this.value != null) {
	    deserializeObjectFromValue();
	}
    }

    public static <T extends RCObject> T createFromKey(byte[] key) {
	// Equivalent of this method is expected to be implemented by SubClasses
	throw new UnsupportedOperationException(
	        "createFromKey() is not expected to be called for RCObject");
    }

    public RCTable getTable() {
	return table;
    }

    public long getTableId() {
	return table.getTableId();
    }

    public byte[] getKey() {
	return key;
    }

    /**
     * Get the byte array value of this object
     *
     * @note will trigger serialization, if value was null.
     * @return
     */
    protected byte[] getValue() {
	if (value == null) {
	    serializeAndSetValue();
	}
	return value;
    }

    public long getVersion() {
	return version;
    }

    /**
     * Return serialized Value.
     *
     * @note will not trigger serialization
     * @return Will return null, if never been read, or was not serialized
     */
    public byte[] getSerializedValue() {
	return value;
    }

    /**
     * Return Object as a Map.
     *
     * @note Will not trigger deserialization
     * @return Will return null, if never been set, or was not deserialized
     */
    protected Map<Object, Object> getObjectMap() {
	return this.propertyMap;
    }

    protected Map<Object, Object> setObjectMap(Map<Object, Object> new_map) {
	Map<Object, Object> old_map = this.propertyMap;
	this.propertyMap = new_map;
	return old_map;
    }

    public void serializeAndSetValue() {
	serializeAndSetValue(defaultKryo.get(), this.propertyMap);
    }

    protected void serializeAndSetValue(Kryo kryo,
	    Map<Object, Object> javaObject) {

	// value
	byte[] rcTemp = new byte[1024 * 1024];
	Output output = new Output(rcTemp);
	kryo.writeObject(output, javaObject);
	this.value = output.toBytes();
    }

    /**
     * Deserialize
     *
     * @return
     */
    public Map<Object, Object> deserializeObjectFromValue() {
	return deserializeObjectFromValue(defaultKryo.get());
    }

    protected HashMap<Object, Object> deserializeObjectFromValue(Kryo kryo) {
	return deserializeObjectFromValue(kryo, HashMap.class);
    }

    protected <T extends Map> T deserializeObjectFromValue(Kryo kryo,
	    Class<T> type) {
	if (this.value == null)
	    return null;

	Input input = new Input(this.value);
	T map = kryo.readObject(input, type);
	this.propertyMap = map;

	return map;
    }

    protected void setValueAndDeserialize(byte[] value, long version) {
	this.value = value;
	this.version = version;
	deserializeObjectFromValue();
    }

    /**
     * Create an Object in DataStore.
     *
     * Fails if the Object with same key already exists.
     *
     * @throws ObjectExistsException
     */
    public void create() throws ObjectExistsException {

	if (this.propertyMap == null) {
	    log.warn("No object map was set. Setting empty Map.");
	    setObjectMap(new HashMap<Object, Object>());
	}
	serializeAndSetValue();

	this.version = table.create(key, value);
    }

    public void forceCreate() {

	if (this.propertyMap == null) {
	    log.warn("No object map was set. Setting empty Map.");
	    setObjectMap(new HashMap<Object, Object>());
	}
	serializeAndSetValue();

	this.version = table.forceCreate(key, value);
    }

    /**
     * Read an Object from DataStore.
     *
     * Fails if the Object with the key does not exist.
     *
     * @throws ObjectDoesntExistException
     *
     */
    public void read() throws ObjectDoesntExistException {
	Entry e = table.read(key);
	// TODO should we deserialize immediately?
	setValueAndDeserialize(e.value, e.version);
    }

    /**
     * Update an existing Object in DataStore checking versions.
     *
     * Fails if the Object with key does not exists, or conditional failure.
     *
     * @throws WrongVersionException
     * @throws ObjectDoesntExistException
     */
    public void update() throws ObjectDoesntExistException,
	    WrongVersionException {
	if (this.propertyMap == null) {
	    setObjectMap(new HashMap<Object, Object>());
	}
	serializeAndSetValue();

	this.version = table.update(key, value, version);
    }

    /**
     * Remove an existing Object in DataStore.
     *
     * Fails if the Object with key does not exists.
     *
     * @throws ObjectDoesntExistException
     * @throws WrongVersionException
     */
    public void delete() throws ObjectDoesntExistException,
	    WrongVersionException {
	this.version = table.delete(key, this.version);
    }

    public void forceDelete() {
	this.version = table.forceDelete(key);
    }

    /**
     * Multi-read RCObjects.
     *
     * If the blob value was read successfully, RCObject will deserialize them.
     *
     * @param objects
     *            RCObjects to read
     * @return true if there exist a failed read.
     */
    public static boolean multiRead(Collection<RCObject> objects) {
	boolean fail_exists = false;

	ArrayList<RCObject> req = new ArrayList<>();
	Iterator<RCObject> it = objects.iterator();
	while (it.hasNext()) {

	    req.add(it.next());

	    if (req.size() >= RCClient.MAX_MULTI_READS) {
		// dispatch multiRead
		fail_exists |= multiReadInternal(req);
		req.clear();
	    }
	}

	if (!req.isEmpty()) {
	    // dispatch multiRead
	    fail_exists |= multiReadInternal(req);
	    req.clear();
	}

	return fail_exists;
    }

    private static boolean multiReadInternal(ArrayList<RCObject> req) {
	boolean fail_exists = false;
	JRamCloud rcClient = RCClient.getClient();

	final int reqs = req.size();
	
	MultiReadObject multiReadObjects = new MultiReadObject(req.size());

	// setup multi-read operation
	for (int i = 0; i < reqs; ++i) {
	    RCObject obj = req.get(i);
            multiReadObjects.setObject(i, obj.getTableId(), obj.getKey());
	}

	// execute
	JRamCloud.Object results[] = rcClient.multiRead(multiReadObjects.tableId, multiReadObjects.key, multiReadObjects.keyLength, reqs);
	assert (results.length <= req.size());

	// reflect changes to RCObject
	for (int i = 0; i < results.length; ++i) {
	    RCObject obj = req.get(i);
	    if (results[i] == null) {
		log.error("MultiRead error, skipping {}, {}", obj.getTable(),
		        obj);
		fail_exists = true;
		continue;
	    }
	    assert (Arrays.equals(results[i].key, obj.getKey()));

	    obj.value = results[i].value;
	    obj.version = results[i].version;
	    if (obj.version == VERSION_NONEXISTENT) {
		fail_exists = true;
	    } else {
		obj.deserializeObjectFromValue();
	    }
	}

	return fail_exists;
    }

    public static class WriteOp {
	public enum STATUS {
	    NOT_EXECUTED, SUCCESS, FAILED
	}

	public enum OPS {
	    CREATE, UPDATE, FORCE_CREATE
	}

	private RCObject obj;
	private OPS op;
	private STATUS status;

	public static WriteOp Create(RCObject obj) {
	    return new WriteOp(obj, OPS.CREATE);
	}

	public static WriteOp Update(RCObject obj) {
	    return new WriteOp(obj, OPS.UPDATE);
	}

	public static WriteOp ForceCreate(RCObject obj) {
	    return new WriteOp(obj, OPS.FORCE_CREATE);
	}

	public WriteOp(RCObject obj, OPS op) {
	    this.obj = obj;
	    this.op = op;
	    this.status = STATUS.NOT_EXECUTED;
	}

	public boolean hasSucceed() {
	    return status == STATUS.SUCCESS;
	}

	public RCObject getObject() {
	    return obj;
	}

	public OPS getOp() {
	    return op;
	}

	public STATUS getStatus() {
	    return status;
	}
    }

    public static boolean multiWrite(Collection<WriteOp> objects) {
	boolean fail_exists = false;

	ArrayList<WriteOp> req = new ArrayList<>();
	Iterator<WriteOp> it = objects.iterator();
	while (it.hasNext()) {

	    req.add(it.next());

	    if (req.size() >= RCClient.MAX_MULTI_WRITES) {
		// dispatch multiWrite
		fail_exists |= multiWriteInternal(req);
		req.clear();
	    }
	}

	if (!req.isEmpty()) {
	    // dispatch multiWrite
	    fail_exists |= multiWriteInternal(req);
	    req.clear();
	}

	return fail_exists;
    }

    private static boolean multiWriteInternal(ArrayList<WriteOp> ops) {

	boolean fail_exists = false;
	MultiWriteObject multiWriteObjects = new MultiWriteObject(ops.size());
	JRamCloud rcClient = RCClient.getClient();

	for (int i = 0; i < ops.size(); ++i) {
	    WriteOp op = ops.get(i);
	    RCObject obj = op.getObject();

	    // FIXME JRamCloud.RejectRules definition is messed up
	    RejectRules rules = rcClient.new RejectRules();

	    switch (op.getOp()) {
	    case CREATE:
		rules.setExists();
		break;
	    case FORCE_CREATE:
		// no reject rule
		break;
	    case UPDATE:
		rules.setDoesntExists();
		rules.setNeVersion(obj.getVersion());
		break;
	    }
	    multiWriteObjects.setObject(i, obj.getTableId(), obj.getKey(), obj.getValue(), rules);
	}

	MultiWriteRspObject[] results = rcClient.multiWrite(multiWriteObjects.tableId, multiWriteObjects.key, multiWriteObjects.keyLength, multiWriteObjects.value, multiWriteObjects.valueLength, ops.size(), multiWriteObjects.rules);
	assert (results.length == ops.size());

	for (int i = 0; i < results.length; ++i) {
	    WriteOp op = ops.get(i);

	    if (results[i] != null
		    && results[i].getStatus() == RCClient.STATUS_OK) {
		op.status = STATUS.SUCCESS;

		RCObject obj = op.getObject();
		obj.version = results[i].getVersion();
	    } else {
		op.status = STATUS.FAILED;
		fail_exists = true;
	    }

	}

	return fail_exists;
    }
   
    public static abstract class ObjectIterator<E extends RCObject> implements
	    Iterator<E> {

	protected TableEnumerator2 enumerator;

	public ObjectIterator(RCTable table) {
	    // FIXME workaround for JRamCloud bug. It should have been declared
	    // as static class
	    JRamCloud c = RCClient.getClient();
	    this.enumerator = c.new TableEnumerator2(table.getTableId());
	}

	@Override
	public boolean hasNext() {
	    return enumerator.hasNext();
	}

// Implement something similar to below to realize Iterator
//	@Override
//	public E next() {
//	    JRamCloud.Object o = enumerator.next();
//	    E obj = E.createFromKey(o.key);
//	    obj.setValueAndDeserialize(o.value, o.version);
//	    return obj;
//	}
	
	@Deprecated
	@Override
	public void remove() {
	    // TODO Not implemented, as I cannot find a use-case for it.
	    throw new UnsupportedOperationException("Not implemented yet");
	}

    }

}
