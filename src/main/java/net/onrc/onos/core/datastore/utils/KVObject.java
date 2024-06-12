package net.onrc.onos.core.datastore.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import net.onrc.onos.core.datastore.DataStoreClient;
import net.onrc.onos.core.datastore.IKVClient;
import net.onrc.onos.core.datastore.IKVTable;
import net.onrc.onos.core.datastore.IKVTable.IKVEntry;
import net.onrc.onos.core.datastore.IKVTableID;
import net.onrc.onos.core.datastore.IMultiEntryOperation;
import net.onrc.onos.core.datastore.IMultiObjectOperation;
import net.onrc.onos.core.datastore.ObjectDoesntExistException;
import net.onrc.onos.core.datastore.ObjectExistsException;
import net.onrc.onos.core.datastore.WrongVersionException;
import net.onrc.onos.core.datastore.internal.IModifiableMultiEntryOperation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Class to represent an Object represented as a single K-V pair Value blob.
 */
public class KVObject {
    private static final Logger log = LoggerFactory.getLogger(KVObject.class);

    // Default Kryo serializer.
    // each sub-class should prepare their own serializer, which has required
    // objects registered for better performance.
    private static final ThreadLocal<Kryo> DEFAULT_KRYO = new ThreadLocal<Kryo>() {
        @Override
        protected Kryo initialValue() {
            Kryo kryo = new Kryo();
            // kryo.setRegistrationRequired(true);
            // kryo.setReferences(false);
            return kryo;
        }
    };

    private final IKVTable table;
    private final byte[] key;

    /**
     * serialized value version stored on data store or
     * {@link IKVTable.getVersionNonexistant()} if is a new object.
     */
    private long version;

    /**
     * Map to store user-defined properties.
     */
    private Map<Object, Object> propertyMap;

    public KVObject(final IKVTable table, final byte[] key) {
        this(table, key, null, table.getVersionNonexistant());
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2",
                        justification = "TODO: Store a copy of the object?")
    public KVObject(final IKVTable table, final byte[] key, final byte[] value, final long version) {
        if (table == null) {
            throw new IllegalArgumentException("table cannot be null");
        }
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        this.table = table;
        this.key = key;
        this.version = version;
        this.propertyMap = new HashMap<Object, Object>();

        if (value != null) {
            deserialize(value);
        }
    }

    protected static KVObject createFromKey(final byte[] key) {
        // Equivalent of this method is expected to be implemented by SubClasses
        throw new UnsupportedOperationException(
                "createFromKey() is not expected to be called for RCObject");
    }

    public IKVTable getTable() {
        return table;
    }

    public IKVTableID getTableId() {
        return table.getTableId();
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP",
                        justification = "TODO: Return a copy of the object?")
    public byte[] getKey() {
        return key;
    }

    public long getVersion() {
        return version;
    }

    /**
     * Return user-defined object properties.
     *
     * @return Will return null, if never been set, or was not deserialized
     * @note Will not trigger deserialization
     */
    protected Map<Object, Object> getPropertyMap() {
        return this.propertyMap;
    }

    protected Map<Object, Object> replacePropertyMap(final Map<Object, Object> newMap) {
        Map<Object, Object> oldMap = this.propertyMap;
        this.propertyMap = newMap;
        return oldMap;
    }

    /**
     * Serialize object.
     * <p/>
     * sub-classes should override this method to customize serialization.
     *
     * @return serialized byte array
     */
    public byte[] serialize() {
        return serializePropertyMap(DEFAULT_KRYO.get(), this.propertyMap);
    }

    protected byte[] serializePropertyMap(final Kryo kryo,
                                          final Map<Object, Object> propMap) {


        // value
        byte[] rcTemp = new byte[1024 * 1024];
        Output output = new Output(rcTemp);
        kryo.writeObject(output, propMap);
        return output.toBytes();
    }


    /**
     * Deserialize using value and version stored in data store.
     *
     * @param bytes   serialized bytes
     * @param version version of this {@code bytes}
     * @return true if success
     */
    public boolean deserialize(final byte[] bytes, final long version) {
        this.version = version;
        return deserialize(bytes);
    }

    /**
     * Deserialize object.
     * <p/>
     * sub-classes should override this method to customize deserialization.
     *
     * @param bytes serialized byte array
     * @return true if success
     */
    protected boolean deserialize(final byte[] bytes) {
        deserializePropertyMap(DEFAULT_KRYO.get(), bytes);
        return true;
    }

    /**
     * Deserialize and set {@link propertyMap}.
     *
     * @param kryo  serializer to use
     * @param bytes Kryo serialized Map object
     * @return true if success
     */
    protected boolean deserializePropertyMap(final Kryo kryo, final byte[] bytes) {
        @SuppressWarnings("unchecked")
        Map<Object, Object> map = deserializePropertyMap(kryo, bytes, HashMap.class);
        if (map == null) {
            map = new HashMap<>();
        }
        this.propertyMap = map;
        return true;
    }

    protected <T extends Map<?, ?>> T deserializePropertyMap(final Kryo kryo,
                                                             final byte[] bytes, final Class<T> type) {

        if (bytes == null || bytes.length == 0) {
            return null;
        }

        Input input = new Input(bytes);
        T map = kryo.readObject(input, type);

        return map;
    }


    /**
     * Create an Object in DataStore.
     * <p/>
     * Fails if the Object with same key already exists.
     *
     * @throws ObjectExistsException
     */
    public void create() throws ObjectExistsException {

        if (this.propertyMap == null) {
            log.warn("No object map was set. Setting empty Map.");
            replacePropertyMap(new HashMap<Object, Object>());
        }

        this.version = table.create(key, this.serialize());
    }

    public void forceCreate() {

        if (this.propertyMap == null) {
            log.warn("No object map was set. Setting empty Map.");
            replacePropertyMap(new HashMap<Object, Object>());
        }

        this.version = table.forceCreate(key, this.serialize());
    }

    /**
     * Read an Object from DataStore.
     * <p/>
     * Fails if the Object with the key does not exist.
     *
     * @throws ObjectDoesntExistException
     */
    public void read() throws ObjectDoesntExistException {
        IKVEntry e = table.read(key);
        deserialize(e.getValue(), e.getVersion());
    }

    /**
     * Update an existing Object in DataStore checking versions.
     * <p/>
     * Fails if the Object with key does not exists, or conditional failure.
     *
     * @throws WrongVersionException
     * @throws ObjectDoesntExistException
     */
    public void update() throws ObjectDoesntExistException,
            WrongVersionException {
        if (this.propertyMap == null) {
            replacePropertyMap(new HashMap<Object, Object>());
        }

        this.version = table.update(key, this.serialize(), version);
    }

    /**
     * Remove an existing Object in DataStore.
     * <p/>
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

    public WriteOp forceCreateOp(IKVClient client) {
        return new WriteOp(client.forceCreateOp(getTableId(), getKey(), serialize()), this);
    }

    public WriteOp createOp(IKVClient client) {
        return new WriteOp(client.createOp(getTableId(), getKey(), serialize()), this);
    }

    // this might not be needed?
    public WriteOp readOp(IKVClient client) {
        return new WriteOp(client.readOp(getTableId(), getKey()), this);
    }

    public WriteOp updateOp(IKVClient client) {
        return new WriteOp(client.updateOp(getTableId(), getKey(), serialize(), getVersion()), this);
    }

    public WriteOp deleteOp(IKVClient client) {
        return new WriteOp(client.deleteOp(getTableId(), getKey(), serialize(), getVersion()), this);
    }

    public WriteOp forceDeleteOp(IKVClient client) {
        return new WriteOp(client.forceDeleteOp(getTableId(), getKey()), this);
    }

    /**
     * Multi-read RCObjects.
     * <p/>
     * If the blob value was read successfully, RCObject will deserialize them.
     *
     * @param objects RCObjects to read
     * @return true if there exist a failed read.
     */
    public static boolean multiRead(final List<? extends KVObject> objects) {

        final IKVClient client = DataStoreClient.getClient();

        final ArrayList<IMultiEntryOperation> readOps = new ArrayList<>(objects.size());
        for (KVObject o : objects) {
            readOps.add(o.readOp(client));
        }

        boolean failExists = client.multiRead(readOps);

        for (int i = 0; i < readOps.size(); ++i) {
            KVObject obj = objects.get(i);
            IMultiEntryOperation entry = readOps.get(i);
            if (entry.hasSucceeded()) {
                if (!obj.deserialize(entry.getValue(), entry.getVersion())) {
                    //deserialize return true on success
                    failExists = true;
                    log.error("MultiRead error, failed to deserialize {}, {}", obj.getTable(), obj);
                }
            } else {
                log.error("MultiRead error, skipping {}, {}", obj.getTable(), obj);
                obj.version = obj.getTable().getVersionNonexistant();
                failExists = true;
            }
        }

        return failExists;
    }

    /**
     * TODO Extract common interface.
     */
    public static class WriteOp implements IMultiObjectOperation, IModifiableMultiEntryOperation {

        private final IModifiableMultiEntryOperation base;
        private final KVObject obj;

        public WriteOp(IMultiEntryOperation base, final KVObject obj) {
            this.base = (IModifiableMultiEntryOperation) base;
            this.obj = obj;

            //      switch (base.getOperation()) {
            //      case CREATE:
            //      case FORCE_CREATE:
            //      case UPDATE:
            //          break;
            //      default:
            //          throw new UnsupportedOperationException("Unexpected OPERATION:"+base.getOperation());
            //      }
        }

        @Override
        public KVObject getObject() {
            return obj;
        }

        @Deprecated
        public OPERATION getOp() {
            return this.getOperation();
        }

        @Override
        public boolean hasSucceeded() {
            return base.hasSucceeded();
        }

        @Override
        public STATUS getStatus() {
            return base.getStatus();
        }

        @Override
        public IKVTableID getTableId() {
            return base.getTableId();
        }

        @Override
        public byte[] getKey() {
            return base.getKey();
        }

        @Override
        public byte[] getValue() {
            return base.getValue();
        }

        @Override
        public long getVersion() {
            return base.getVersion();
        }

        @Override
        public OPERATION getOperation() {
            return base.getOperation();
        }

        @Override
        public void setStatus(STATUS status) {
            base.setStatus(status);
        }

        @Override
        public void setValue(byte[] value, long version) {
            base.setValue(value, version);
        }

        @Override
        public void setVersion(long version) {
            base.setVersion(version);
            this.obj.version = version;
        }

        @Override
        public IModifiableMultiEntryOperation getActualOperation() {
            return base;
        }
    }

    public static boolean multiWrite(final List<WriteOp> objects) {

        final IKVClient client = DataStoreClient.getClient();

        final ArrayList<IMultiEntryOperation> writeOps = new ArrayList<>(objects.size());
        for (WriteOp o : objects) {
            writeOps.add(o);
        }

        return client.multiWrite(writeOps);
    }

    public abstract static class AbstractObjectIterator<E extends KVObject> implements
            Iterator<E> {

        protected Iterator<IKVEntry> enumerator;

        public AbstractObjectIterator(final IKVTable table) {
            this.enumerator = table.getAllEntries().iterator();
        }

        @Override
        public boolean hasNext() {
            return enumerator.hasNext();
        }

        // Implement something similar to below to realize Iterator
        //      @Override
        //      public E next() {
        //          IKVTable.IKVEntry o = enumerator.next();
        //          E obj = E.createFromKey(o.getKey());
        //          obj.deserialize(o.getValue(), o.getVersion());
        //          return obj;
        //      }

        @Deprecated
        @Override
        public void remove() {
            // TODO Not implemented, as I cannot find a use-case for it.
            throw new UnsupportedOperationException("Not implemented yet");
        }

    }

}