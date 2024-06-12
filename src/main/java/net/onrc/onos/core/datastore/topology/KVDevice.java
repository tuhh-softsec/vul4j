package net.onrc.onos.core.datastore.topology;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import net.onrc.onos.core.datastore.DataStoreClient;
import net.onrc.onos.core.datastore.IKVTable.IKVEntry;
import net.onrc.onos.core.datastore.topology.KVLink.STATUS;
import net.onrc.onos.core.datastore.utils.ByteArrayComparator;
import net.onrc.onos.core.datastore.utils.ByteArrayUtil;
import net.onrc.onos.core.datastore.utils.KVObject;
import net.onrc.onos.core.topology.DeviceEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.Kryo;

/**
 * Device object.
 * <p/>
 * TODO switch to ProtoBuf, etc.
 */
public class KVDevice extends KVObject {
    private static final Logger log = LoggerFactory.getLogger(KVDevice.class);

    private static final ThreadLocal<Kryo> DEVICE_KRYO = new ThreadLocal<Kryo>() {
        @Override
        protected Kryo initialValue() {
            Kryo kryo = new Kryo();
            kryo.setRegistrationRequired(true);
            kryo.setReferences(false);
            kryo.register(byte[].class);
            kryo.register(byte[][].class);
            kryo.register(HashMap.class);
            // TODO check if we should explicitly specify EnumSerializer
            kryo.register(STATUS.class);
            return kryo;
        }
    };

    public static final String GLOBAL_DEVICE_TABLE_NAME = "G:Device";

    // FIXME these should be Enum or some number, not String
    private static final String PROP_MAC = "mac";
    private static final String PROP_PORT_IDS = "port-ids";

    private final byte[] mac;
    private TreeSet<byte[]> portIds;
    private transient boolean isPortIdsModified;

    // Assume there is only one ip on a device now.
    private int ip;
    private long lastSeenTime;

    // Assuming mac is unique cluster-wide
    public static byte[] getDeviceID(final byte[] mac) {
        return DeviceEvent.getDeviceID(mac).array();
    }

    public static byte[] getMacFromKey(final byte[] key) {
        ByteBuffer keyBuf = ByteBuffer.wrap(key);
        if (keyBuf.getChar() != 'D') {
            throw new IllegalArgumentException("Invalid Device key");
        }
        byte[] mac = new byte[keyBuf.remaining()];
        keyBuf.get(mac);
        return mac;
    }

    // @SuppressFBWarnings(value = "EI_EXPOSE_REP2",
    //                     justification = "TODO: Store a copy of the object?")
    public KVDevice(final byte[] mac) {
        super(DataStoreClient.getClient().getTable(GLOBAL_DEVICE_TABLE_NAME), getDeviceID(mac));

        this.mac = mac;
        this.portIds = new TreeSet<>(ByteArrayComparator.BYTEARRAY_COMPARATOR);
        this.isPortIdsModified = true;
    }

    /**
     * Get an instance from Key.
     *
     * @param key
     * @return
     * @note You need to call `read()` to get the DB content.
     */
    public static KVDevice createFromKey(final byte[] key) {
        return new KVDevice(getMacFromKey(key));
    }

    public static Iterable<KVDevice> getAllDevices() {
        return new DeviceEnumerator();
    }

    public static class DeviceEnumerator implements Iterable<KVDevice> {

        @Override
        public Iterator<KVDevice> iterator() {
            return new DeviceIterator();
        }
    }

    public static class DeviceIterator extends AbstractObjectIterator<KVDevice> {

        public DeviceIterator() {
            super(DataStoreClient.getClient().getTable(GLOBAL_DEVICE_TABLE_NAME));
        }

        @Override
        public KVDevice next() {
            IKVEntry o = enumerator.next();
            KVDevice e = KVDevice.createFromKey(o.getKey());
            e.deserialize(o.getValue(), o.getVersion());
            return e;
        }
    }

    // @SuppressFBWarnings(value = "EI_EXPOSE_REP",
    //                     justification = "TODO: Return a copy of the object?")
    public byte[] getMac() {
        // TODO may need to clone() to be sure this object will be immutable.
        return mac;
    }

    public byte[] getId() {
        return getKey();
    }

    public void addPortId(final byte[] portId) {
        // TODO: Should we copy portId, or reference is OK.
        isPortIdsModified |= portIds.add(portId);
    }

    public void removePortId(final byte[] portId) {
        isPortIdsModified |= portIds.remove(portId);
    }

    public void emptyPortIds() {
        portIds.clear();
        this.isPortIdsModified = true;
    }

    public void addAllToPortIds(final Collection<byte[]> portIds) {
        // TODO: Should we copy portId, or reference is OK.
        isPortIdsModified |= this.portIds.addAll(portIds);
    }

    /**
     * @return Unmodifiable Set view of all the PortIds;
     */
    public Set<byte[]> getAllPortIds() {
        return Collections.unmodifiableSet(portIds);
    }

    @Override
    public byte[] serialize() {
        Map<Object, Object> map = getPropertyMap();

        map.put(PROP_MAC, mac);
        if (isPortIdsModified) {
            byte[][] portIdArray = new byte[portIds.size()][];
            map.put(PROP_PORT_IDS, portIds.toArray(portIdArray));
            isPortIdsModified = false;
        }

        return serializePropertyMap(DEVICE_KRYO.get(), map);
    }

    @Override
    protected boolean deserialize(final byte[] bytes) {
        boolean success = deserializePropertyMap(DEVICE_KRYO.get(), bytes);
        if (!success) {
            log.error("Deserializing Link: " + this + " failed.");
            return false;
        }
        Map<Object, Object> map = this.getPropertyMap();

        if (this.portIds == null) {
            this.portIds = new TreeSet<>(
                    ByteArrayComparator.BYTEARRAY_COMPARATOR);
        }
        byte[][] portIdArray = (byte[][]) map.get(PROP_PORT_IDS);
        if (portIdArray != null) {
            this.portIds.clear();
            this.portIds.addAll(Arrays.asList(portIdArray));
            isPortIdsModified = false;
        } else {
            // trigger write on next serialize
            isPortIdsModified = true;
        }

        return success;
    }

    @Override
    public String toString() {
        // TODO output all properties?
        return "[" + this.getClass().getSimpleName()
                + " " + ByteArrayUtil.toHexStringBuffer(mac, ":") + "]";
    }

    public int getIp() {
        return ip;
    }

    public void setIp(int ip) {
        this.ip = ip;
    }

    public long getLastSeenTime() {
        return lastSeenTime;
    }

    public void setLastSeenTime(long lastSeenTime) {
        this.lastSeenTime = lastSeenTime;
    }
}