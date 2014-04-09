package net.onrc.onos.core.datastore.topology;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.onrc.onos.core.datastore.DataStoreClient;
import net.onrc.onos.core.datastore.IKVTable.IKVEntry;
import net.onrc.onos.core.datastore.serializers.Topology.LinkProperty;
import net.onrc.onos.core.datastore.utils.KVObject;
import net.onrc.onos.core.topology.LinkEvent;
import net.onrc.onos.core.topology.PortEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.Kryo;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * Link object in data store.
 */
public class KVLink extends KVObject {
    private static final Logger log = LoggerFactory.getLogger(KVLink.class);

    private static final ThreadLocal<Kryo> LINK_KRYO = new ThreadLocal<Kryo>() {
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

    public static class SwitchPort {
        public final Long dpid;
        public final Long number;

        public SwitchPort(final Long dpid, final Long number) {
            this.dpid = dpid;
            this.number = number;
        }

        public byte[] getPortID() {
            return KVPort.getPortID(dpid, number);
        }

        public byte[] getSwitchID() {
            return KVSwitch.getSwitchID(dpid);
        }

        @Override
        public String toString() {
            return "(" + Long.toHexString(dpid) + "@" + number + ")";
        }

    }

    public static final String GLOBAL_LINK_TABLE_NAME = "G:Link";

    // must not re-order enum members, ordinal will be sent over wire
    public enum STATUS {
        INACTIVE, ACTIVE;
    }

    private final SwitchPort src;
    private final SwitchPort dst;
    private STATUS status;

    public static byte[] getLinkID(final Long src_dpid, final Long src_port_no,
                                   final Long dst_dpid, final Long dst_port_no) {
        return LinkEvent.getLinkID(src_dpid, src_port_no, dst_dpid,
                dst_port_no).array();
    }

    public static long[] getLinkTupleFromKey(final byte[] key) {
        return getLinkTupleFromKey(ByteBuffer.wrap(key));
    }

    public static long[] getLinkTupleFromKey(final ByteBuffer keyBuf) {
        if (keyBuf.getChar() != 'L') {
            throw new IllegalArgumentException("Invalid Link key");
        }
        final long[] srcPortPair = KVPort.getPortPairFromKey(keyBuf.slice());
        keyBuf.position(2 + PortEvent.PORTID_BYTES);
        final long[] dstPortPair = KVPort.getPortPairFromKey(keyBuf.slice());

        long[] tuple = new long[4];
        tuple[0] = srcPortPair[0];
        tuple[1] = srcPortPair[1];
        tuple[2] = dstPortPair[0];
        tuple[3] = dstPortPair[1];

        return tuple;
    }

    public KVLink(final Long src_dpid, final Long src_port_no,
                  final Long dst_dpid, final Long dst_port_no) {
        super(DataStoreClient.getClient().getTable(GLOBAL_LINK_TABLE_NAME), getLinkID(src_dpid,
                src_port_no, dst_dpid, dst_port_no));

        src = new SwitchPort(src_dpid, src_port_no);
        dst = new SwitchPort(dst_dpid, dst_port_no);
        status = STATUS.INACTIVE;
    }

    /**
     * Get an instance from Key.
     *
     * @param key
     * @return KVLink instance
     * @note You need to call `read()` to get the DB content.
     */
    public static KVLink createFromKey(final byte[] key) {
        long[] linkTuple = getLinkTupleFromKey(key);
        return new KVLink(linkTuple[0], linkTuple[1], linkTuple[2],
                linkTuple[3]);
    }

    public static Iterable<KVLink> getAllLinks() {
        return new LinkEnumerator();
    }

    public static class LinkEnumerator implements Iterable<KVLink> {

        @Override
        public Iterator<KVLink> iterator() {
            return new LinkIterator();
        }
    }

    public static class LinkIterator extends AbstractObjectIterator<KVLink> {

        public LinkIterator() {
            super(DataStoreClient.getClient().getTable(GLOBAL_LINK_TABLE_NAME));
        }

        @Override
        public KVLink next() {
            IKVEntry o = enumerator.next();
            KVLink e = KVLink.createFromKey(o.getKey());
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

    public SwitchPort getSrc() {
        return src;
    }

    public SwitchPort getDst() {
        return dst;
    }

    public byte[] getId() {
        return getKey();
    }

    @Override
    public byte[] serialize() {
        Map<Object, Object> map = getPropertyMap();

        LinkProperty.Builder link = LinkProperty.newBuilder();
        link.setSrcSwId(ByteString.copyFrom(src.getSwitchID()));
        link.setSrcPortId(ByteString.copyFrom(src.getPortID()));
        link.setDstSwId(ByteString.copyFrom(dst.getSwitchID()));
        link.setDstPortId(ByteString.copyFrom(dst.getPortID()));
        link.setStatus(status.ordinal());

        if (!map.isEmpty()) {
            byte[] propMaps = serializePropertyMap(LINK_KRYO.get(), map);
            link.setValue(ByteString.copyFrom(propMaps));
        }

        return link.build().toByteArray();
    }

    @Override
    protected boolean deserialize(final byte[] bytes) {
        try {
            boolean success = true;

            LinkProperty link = LinkProperty.parseFrom(bytes);
            byte[] props = link.getValue().toByteArray();
            success &= deserializePropertyMap(LINK_KRYO.get(), props);
            this.status = STATUS.values()[link.getStatus()];

            return success;
        } catch (InvalidProtocolBufferException e) {
            log.error("Deserializing Link: " + this + " failed.", e);
            return false;
        }
    }

    @Override
    public String toString() {
        // TODO output all properties?
        return "[" + this.getClass().getSimpleName()
                + " " + src + "->" + dst + " STATUS:" + status + "]";
    }
}
