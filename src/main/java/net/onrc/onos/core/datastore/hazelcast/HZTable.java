package net.onrc.onos.core.datastore.hazelcast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import net.onrc.onos.core.datastore.IKVTable;
import net.onrc.onos.core.datastore.IKVTableID;
import net.onrc.onos.core.datastore.ObjectDoesntExistException;
import net.onrc.onos.core.datastore.ObjectExistsException;
import net.onrc.onos.core.datastore.WrongVersionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.IMap;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public class HZTable implements IKVTable, IKVTableID {
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(HZTable.class);

    // not sure how strict this should be managed
    private static final AtomicLong INITIAL_VERSION = new AtomicLong(HZClient.VERSION_NONEXISTENT);

    /**
     * generate a new initial version for an entry.
     *
     * @return initial value
     */
    protected static long getInitialVersion() {
        long version = INITIAL_VERSION.incrementAndGet();
        if (version == HZClient.VERSION_NONEXISTENT) {
            // used up whole 64bit space?
            version = INITIAL_VERSION.incrementAndGet();
        }
        return version;
    }

    /**
     * increment version, avoiding versionNonexistant.
     *
     * @param version
     * @return
     */
    protected static long getNextVersion(final long version) {
        long nextVersion = version + 1;
        if (nextVersion == HZClient.VERSION_NONEXISTENT) {
            ++nextVersion;
        }
        return nextVersion;
    }

    static class VersionedValue implements IdentifiedDataSerializable {
        private static final long serialVersionUID = -3149375966890712708L;

        private byte[] value;
        private long version;

        protected VersionedValue() {
            value = new byte[0];
            version = HZClient.VERSION_NONEXISTENT;
        }

        public VersionedValue(final byte[] value, final long version) {
            this.value = value;
            this.version = version;
        }

        public byte[] getValue() {
            return value;
        }

        public long getVersion() {
            return version;
        }

        public void setValue(final byte[] value) {
            this.value = value;
        }

        public void setNextVersion() {
            this.version = getNextVersion(this.version);
        }

        @Override
        public void writeData(final ObjectDataOutput out) throws IOException {
            out.writeLong(version);
            out.writeInt(value.length);
            if (value.length > 0) {
                out.write(value);
            }
        }

        @Override
        public void readData(final ObjectDataInput in) throws IOException {
            version = in.readLong();
            final int valueLen = in.readInt();
            value = new byte[valueLen];
            in.readFully(value);
        }

        @Override
        public int getFactoryId() {
            return VersionedValueSerializableFactory.FACTORY_ID;
        }

        @Override
        public int getId() {
            return VersionedValueSerializableFactory.VERSIONED_VALUE_ID;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (int) (version ^ (version >>> 32));
            result = prime * result + Arrays.hashCode(value);
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            VersionedValue other = (VersionedValue) obj;
            if (version != other.version) {
                return false;
            }
            if (!Arrays.equals(value, other.value)) {
                return false;
            }
            return true;
        }
    }

    // TODO Refactor and extract common parts
    public static class Entry implements IKVEntry {
        final byte[] key;
        byte[] value;
        long version;

        // @SuppressFBWarnings(value = "EI_EXPOSE_REP2",
        //                     justification = "TODO: Store a copy of the object?")
        public Entry(final byte[] key, final byte[] value, final long version) {
            this.key = key;
            this.setValue(value);
            this.setVersion(version);
        }

        public Entry(final byte[] key) {
            this(key, null, HZClient.VERSION_NONEXISTENT);
        }

        @Override
        // @SuppressFBWarnings(value = "EI_EXPOSE_REP",
        //                     justification = "TODO: Return a copy of the object?")
        public byte[] getKey() {
            return key;
        }

        @Override
        // @SuppressFBWarnings(value = "EI_EXPOSE_REP",
        //                     justification = "TODO: Return a copy of the object?")
        public byte[] getValue() {
            return value;
        }

        @Override
        public long getVersion() {
            return version;
        }

        void setValue(final byte[] value) {
            this.value = value;
        }

        void setVersion(final long version) {
            this.version = version;
        }
    }


    private final String mapName;
    private final IMap<byte[], VersionedValue> map;

    public HZTable(final String mapName, final IMap<byte[], VersionedValue> map) {
        this.mapName = mapName;
        this.map = map;
    }

    @Override
    public String getTableName() {
        return mapName;
    }

    @Override
    public IKVTableID getTableId() {
        return this;
    }

    @Override
    public long create(final byte[] key, final byte[] value) throws ObjectExistsException {
        final long version = getInitialVersion();
        VersionedValue existing = map.putIfAbsent(key, new VersionedValue(value, version));
        if (existing != null) {
            throw new ObjectExistsException(this, key);
        }
        return version;
    }

    @Override
    public long forceCreate(final byte[] key, final byte[] value) {
        final long version = getInitialVersion();
        map.set(key, new VersionedValue(value, version));
        return version;
    }

    @Override
    public IKVEntry read(final byte[] key) throws ObjectDoesntExistException {
        final VersionedValue value = map.get(key);
        if (value == null) {
            throw new ObjectDoesntExistException(this, key);
        }
        return new Entry(key, value.getValue(), value.getVersion());
    }

    @Override
    public long update(final byte[] key, final byte[] value, final long version)
            throws ObjectDoesntExistException, WrongVersionException {

        try {
            map.lock(key);
            final VersionedValue oldValue = map.get(key);
            if (oldValue == null) {
                throw new ObjectDoesntExistException(this, key);
            }
            if (oldValue.getVersion() != version) {
                throw new WrongVersionException(this, key, version, oldValue.getVersion());
            }
            final long nextVersion = getNextVersion(version);
            map.set(key, new VersionedValue(value, nextVersion));
            return nextVersion;
        } finally {
            map.unlock(key);
        }
    }

    @Override
    public long update(final byte[] key, final byte[] value)
            throws ObjectDoesntExistException {

        try {
            map.lock(key);
            final VersionedValue valueInMap = map.get(key);
            if (valueInMap == null) {
                throw new ObjectDoesntExistException(this, key);
            }
            valueInMap.setValue(value);
            valueInMap.setNextVersion();
            map.set(key, valueInMap);
            return valueInMap.getVersion();
        } finally {
            map.unlock(key);
        }
    }

    @Override
    public long delete(final byte[] key, final long version)
            throws ObjectDoesntExistException, WrongVersionException {

        try {
            map.lock(key);
            final VersionedValue oldValue = map.get(key);
            if (oldValue == null) {
                throw new ObjectDoesntExistException(this, key);
            }
            if (oldValue.getVersion() != version) {
                throw new WrongVersionException(this, key, version, oldValue.getVersion());
            }
            map.delete(key);
            return oldValue.getVersion();
        } finally {
            map.unlock(key);
        }
    }

    @Override
    public long forceDelete(final byte[] key) {
        final VersionedValue valueInMap = map.remove(key);
        if (valueInMap == null) {
            return HZClient.VERSION_NONEXISTENT;
        }
        return valueInMap.getVersion();
    }

    @Override
    public Iterable<IKVEntry> getAllEntries() {
        final Set<IMap.Entry<byte[], VersionedValue>> entries = map.entrySet();
        List<IKVEntry> entryList = new ArrayList<IKVTable.IKVEntry>(entries.size());
        for (IMap.Entry<byte[], VersionedValue> entry : entries) {
            entryList.add(new Entry(entry.getKey(), entry.getValue().getValue(), entry.getValue().getVersion()));
        }
        return entryList;
    }

    @Override
    public String toString() {
        return "[HZTable " + mapName + "]";
    }

    IMap<byte[], VersionedValue> getBackendMap() {
        return this.map;
    }

    @Override
    public long getVersionNonexistant() {
        return HZClient.VERSION_NONEXISTENT;
    }
}