package net.onrc.onos.core.datastore.topology;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.onrc.onos.core.datastore.DataStoreClient;
import net.onrc.onos.core.datastore.IKVTable.IKVEntry;
import net.onrc.onos.core.datastore.RCProtos.PortProperty;
import net.onrc.onos.core.datastore.utils.ByteArrayUtil;
import net.onrc.onos.core.datastore.utils.KVObject;
import net.onrc.onos.core.topology.PortEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.Kryo;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * Port object in data store.
 *
 * Note: This class will not maintain invariants.
 *       e.g., It will NOT automatically remove Links or Devices on Port,
 *       when deleting a Port.
 */
public class KVPort extends KVObject {
    private static final Logger log = LoggerFactory.getLogger(KVPort.class);

    private static final ThreadLocal<Kryo> portKryo = new ThreadLocal<Kryo>() {
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

    public static final String GLOBAL_PORT_TABLE_NAME = "G:Port";

    // must not re-order enum members, ordinal will be sent over wire
    public enum STATUS {
        INACTIVE, ACTIVE;
    }

    private final Long dpid;
    private final Long number;

    private STATUS status;

    public static byte[] getPortID(final Long dpid, final Long number) {
        return PortEvent.getPortID(dpid, number).array();
    }

    public static long[] getPortPairFromKey(final byte[] key) {
        return getPortPairFromKey(ByteBuffer.wrap(key));
    }

    public static long[] getPortPairFromKey(final ByteBuffer keyBuf) {
        if (keyBuf.getChar() != 'S') {
            throw new IllegalArgumentException("Invalid Port key:" + keyBuf
                    + " "
                    + ByteArrayUtil.toHexStringBuffer(keyBuf.array(), ":"));
        }
        long[] pair = new long[2];
        pair[0] = keyBuf.getLong();
        if (keyBuf.getChar() != 'P') {
            throw new IllegalArgumentException("Invalid Port key:" + keyBuf
                    + " "
                    + ByteArrayUtil.toHexStringBuffer(keyBuf.array(), ":"));
        }
        pair[1] = keyBuf.getLong();
        return pair;

    }

    public static long getDpidFromKey(final byte[] key) {
        return getPortPairFromKey(key)[0];
    }

    public static long getNumberFromKey(final byte[] key) {
        return getPortPairFromKey(key)[1];
    }

    // FIXME specify DPID,number here, or Should caller specify the key it self?
    // In other words, should layer above have the control of the ID?
    public KVPort(final Long dpid, final Long number) {
        super(DataStoreClient.getClient().getTable(GLOBAL_PORT_TABLE_NAME), getPortID(dpid, number));

        // TODO Auto-generated constructor stub

        this.dpid = dpid;
        this.number = number;
        this.status = STATUS.INACTIVE;
    }

    /**
     * Get an instance from Key.
     *
     * @note You need to call `read()` to get the DB content.
     * @param key
     * @return KVPort instance
     */
    public static KVPort createFromKey(final byte[] key) {
        long[] pair = getPortPairFromKey(key);
        return new KVPort(pair[0], pair[1]);
    }

    public static Iterable<KVPort> getAllPorts() {
        return new PortEnumerator();
    }

    public static class PortEnumerator implements Iterable<KVPort> {

        @Override
        public Iterator<KVPort> iterator() {
            return new PortIterator();
        }
    }

    public static class PortIterator extends AbstractObjectIterator<KVPort> {

        public PortIterator() {
            super(DataStoreClient.getClient().getTable(GLOBAL_PORT_TABLE_NAME));
        }

        @Override
        public KVPort next() {
            IKVEntry o = enumerator.next();
            KVPort e = KVPort.createFromKey(o.getKey());
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

    public Long getNumber() {
        return number;
    }

    public byte[] getId() {
        return getKey();
    }

    @Override
    public byte[] serialize() {
        Map<Object, Object> map = getPropertyMap();

        PortProperty.Builder port = PortProperty.newBuilder();
        port.setDpid(dpid);
        port.setNumber(number);
        port.setStatus(status.ordinal());

        if (!map.isEmpty()) {
            byte[] propMaps = serializePropertyMap(portKryo.get(), map);
            port.setValue(ByteString.copyFrom(propMaps));
        }

        return port.build().toByteArray();
    }

    @Override
    protected boolean deserialize(final byte[] bytes) {
        try {
            boolean success = true;

            PortProperty port = PortProperty.parseFrom(bytes);
            byte[] props = port.getValue().toByteArray();
            success &= deserializePropertyMap(portKryo.get(), props);
            this.status = STATUS.values()[port.getStatus()];

            return success;
        } catch (InvalidProtocolBufferException e) {
            log.error("Deserializing Port: " + this + " failed.", e);
            return false;
        }
    }

    @Override
    public String toString() {
        // TODO output all properties?
        return "[" + this.getClass().getSimpleName()
                + " 0x" + Long.toHexString(dpid) + "@" + number
                + " STATUS:" + status + "]";
    }
}
