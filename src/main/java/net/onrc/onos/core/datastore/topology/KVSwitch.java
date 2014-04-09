package net.onrc.onos.core.datastore.topology;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.onrc.onos.core.datastore.DataStoreClient;
import net.onrc.onos.core.datastore.IKVTable.IKVEntry;
import net.onrc.onos.core.datastore.serializers.Topology.SwitchProperty;
import net.onrc.onos.core.datastore.utils.KVObject;
import net.onrc.onos.core.topology.SwitchEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.Kryo;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * Switch object in data store.
 * <p/>
 * Note: This class will not maintain invariants.
 * e.g., It will NOT automatically remove Ports on Switch,
 * when deleting a Switch.
 */
public class KVSwitch extends KVObject {
    private static final Logger log = LoggerFactory.getLogger(KVSwitch.class);

    private static final ThreadLocal<Kryo> SWITCH_KRYO = new ThreadLocal<Kryo>() {
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

    public static final String GLOBAL_SWITCH_TABLE_NAME = "G:Switch";

    // must not re-order enum members, ordinal will be sent over wire
    public enum STATUS {
        INACTIVE, ACTIVE;
    }

    private final Long dpid;
    private STATUS status;

    public static byte[] getSwitchID(final Long dpid) {
        return SwitchEvent.getSwitchID(dpid).array();
    }

    public static long getDpidFromKey(final byte[] key) {
        return getDpidFromKey(ByteBuffer.wrap(key));
    }

    public static long getDpidFromKey(final ByteBuffer keyBuf) {
        if (keyBuf.getChar() != 'S') {
            throw new IllegalArgumentException("Invalid Switch key");
        }
        return keyBuf.getLong();
    }

    // FIXME specify DPID here, or Should caller specify the key it self?
    // In other words, should layer above have the control of the ID?
    public KVSwitch(final Long dpid) {
        super(DataStoreClient.getClient().getTable(GLOBAL_SWITCH_TABLE_NAME), getSwitchID(dpid));

        this.dpid = dpid;
        this.status = STATUS.INACTIVE;
    }

    /**
     * Get an instance from Key.
     *
     * @param key
     * @return KVSwitch instance
     * @note You need to call `read()` to get the DB content.
     */
    public static KVSwitch createFromKey(final byte[] key) {
        return new KVSwitch(getDpidFromKey(key));
    }

    public static Iterable<KVSwitch> getAllSwitches() {
        return new SwitchEnumerator();
    }

    public static class SwitchEnumerator implements Iterable<KVSwitch> {

        @Override
        public Iterator<KVSwitch> iterator() {
            return new SwitchIterator();
        }
    }

    public static class SwitchIterator extends AbstractObjectIterator<KVSwitch> {

        public SwitchIterator() {
            super(DataStoreClient.getClient().getTable(GLOBAL_SWITCH_TABLE_NAME));
        }

        @Override
        public KVSwitch next() {
            IKVEntry o = enumerator.next();
            KVSwitch e = KVSwitch.createFromKey(o.getKey());
            e.deserialize(o.getValue(), o.getVersion());
            return e;
        }
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(final STATUS status) {
        this.status = status;
    }

    public Long getDpid() {
        return dpid;
    }

    public byte[] getId() {
        return getKey();
    }

    @Override
    public byte[] serialize() {
        Map<Object, Object> map = getPropertyMap();

        SwitchProperty.Builder sw = SwitchProperty.newBuilder();
        sw.setDpid(dpid);
        sw.setStatus(status.ordinal());

        if (!map.isEmpty()) {
            byte[] propMaps = serializePropertyMap(SWITCH_KRYO.get(), map);
            sw.setValue(ByteString.copyFrom(propMaps));
        }

        return sw.build().toByteArray();
    }

    @Override
    protected boolean deserialize(final byte[] bytes) {
        try {
            boolean success = true;

            SwitchProperty sw = SwitchProperty.parseFrom(bytes);
            byte[] props = sw.getValue().toByteArray();
            success &= deserializePropertyMap(SWITCH_KRYO.get(), props);
            this.status = STATUS.values()[sw.getStatus()];

            return success;
        } catch (InvalidProtocolBufferException e) {
            log.error("Deserializing Switch: " + this + " failed.", e);
            return false;
        }
    }

    @Override
    public String toString() {
        // TODO output all properties?
        return "[" + this.getClass().getSimpleName()
                + " 0x" + Long.toHexString(dpid) + " STATUS:" + status + "]";
    }

}
