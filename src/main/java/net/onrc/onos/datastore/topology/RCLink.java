package net.onrc.onos.datastore.topology;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.openflow.util.HexString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.Kryo;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import edu.stanford.ramcloud.JRamCloud;
import net.onrc.onos.datastore.RCProtos.LinkProperty;
import net.onrc.onos.datastore.RCObject;
import net.onrc.onos.datastore.RCTable;
import net.onrc.onos.ofcontroller.networkgraph.LinkEvent;
import net.onrc.onos.ofcontroller.networkgraph.PortEvent;

public class RCLink extends RCObject {
    private static final Logger log = LoggerFactory.getLogger(RCLink.class);

    private static final ThreadLocal<Kryo> linkKryo = new ThreadLocal<Kryo>() {
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

	public SwitchPort(Long dpid, Long number) {
	    this.dpid = dpid;
	    this.number = number;
	}

	public byte[] getPortID() {
	    return RCPort.getPortID(dpid, number);
	}

	public byte[] getSwitchID() {
	    return RCSwitch.getSwitchID(dpid);
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

    public static byte[] getLinkID(Long src_dpid, Long src_port_no,
	    Long dst_dpid, Long dst_port_no) {
	return LinkEvent.getLinkID(src_dpid, src_port_no, dst_dpid,
		dst_port_no).array();
    }

    public static StringBuilder keysToSB(Collection<byte[]> keys) {
	StringBuilder sb = new StringBuilder();
	sb.append("[");
	boolean hasWritten = false;
	for (byte[] key : keys) {
	    if (hasWritten) {
		sb.append(", ");
	    }
	    sb.append(keyToString(key));
	    hasWritten = true;
	}
	sb.append("]");
	return sb;
    }

    public static String keyToString(byte[] key) {
	// For debug log
	long[] tuple = getLinkTupleFromKey(key);
	return "L" + "S" + HexString.toHexString(tuple[0]) + "P" + tuple[1]
	        + "S" + HexString.toHexString(tuple[2]) + "P" + tuple[3];
    }

    public static long[] getLinkTupleFromKey(byte[] key) {
	return getLinkTupleFromKey(ByteBuffer.wrap(key));
    }

    public static long[] getLinkTupleFromKey(ByteBuffer keyBuf) {
	long tuple[] = new long[4];
	if (keyBuf.getChar() != 'L') {
	    throw new IllegalArgumentException("Invalid Link key");
	}
	long src_port_pair[] = RCPort.getPortPairFromKey(keyBuf.slice());
	keyBuf.position(2 + PortEvent.PORTID_BYTES);
	long dst_port_pair[] = RCPort.getPortPairFromKey(keyBuf.slice());

	tuple[0] = src_port_pair[0];
	tuple[1] = src_port_pair[1];
	tuple[2] = dst_port_pair[0];
	tuple[3] = dst_port_pair[1];

	return tuple;
    }

    public RCLink(Long src_dpid, Long src_port_no, Long dst_dpid,
	    Long dst_port_no) {
	super(RCTable.getTable(GLOBAL_LINK_TABLE_NAME), getLinkID(src_dpid,
	        src_port_no, dst_dpid, dst_port_no));

	src = new SwitchPort(src_dpid, src_port_no);
	dst = new SwitchPort(dst_dpid, dst_port_no);
	status = STATUS.INACTIVE;
    }

    /**
     * Get an instance from Key.
     *
     * @note You need to call `read()` to get the DB content.
     * @param key
     * @return RCLink instance
     */
    public static <L extends RCObject> L createFromKey(byte[] key) {
	long linkTuple[] = getLinkTupleFromKey(key);
	@SuppressWarnings("unchecked")
	L l = (L) new RCLink(linkTuple[0], linkTuple[1], linkTuple[2],
	        linkTuple[3]);
	return l;
    }

    public static Iterable<RCLink> getAllLinks() {
	return new LinkEnumerator();
    }

    public static class LinkEnumerator implements Iterable<RCLink> {

	@Override
	public Iterator<RCLink> iterator() {
	    return new LinkIterator();
	}
    }

    public static class LinkIterator extends ObjectIterator<RCLink> {

	public LinkIterator() {
	    super(RCTable.getTable(GLOBAL_LINK_TABLE_NAME));
	}

	@Override
	public RCLink next() {
	    JRamCloud.Object o = enumerator.next();
	    RCLink e = RCLink.createFromKey(o.key);
	    e.setValueAndDeserialize(o.value, o.version);
	    return e;
	}
    }

    public STATUS getStatus() {
	return status;
    }

    public void setStatus(STATUS status) {
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
    public void serializeAndSetValue() {
	Map<Object, Object> map = getObjectMap();

	LinkProperty.Builder link = LinkProperty.newBuilder();
	link.setSrcSwId(ByteString.copyFrom(src.getSwitchID()));
	link.setSrcPortId(ByteString.copyFrom(src.getPortID()));
	link.setDstSwId(ByteString.copyFrom(dst.getSwitchID()));
	link.setDstPortId(ByteString.copyFrom(dst.getPortID()));
	link.setStatus(status.ordinal());

	if (!map.isEmpty()) {
	    serializeAndSetValue(linkKryo.get(), map);
	    link.setValue(ByteString.copyFrom(this.getSerializedValue()));
	}

	this.value = link.build().toByteArray();
    }

    @Override
    public Map<Object, Object> deserializeObjectFromValue() {
	LinkProperty link = null;
	Map<Object, Object> map = null;
	try {
	    link = LinkProperty.parseFrom(this.value);
	    this.value = link.getValue().toByteArray();
	    if (this.value.length >= 1) {
		map = deserializeObjectFromValue(linkKryo.get());
	    } else {
		map = new HashMap<>();
	    }
	    this.status = STATUS.values()[link.getStatus()];
	    return map;
	} catch (InvalidProtocolBufferException e) {
	    log.error("{" + toString() + "}: Read Link: ", e);
	    return null;
	}
    }

    @Override
    public String toString() {
	return "[RCLink " + src + "->" + dst + " STATUS:" + status + "]";
    }

    public static void main(String[] args) {
	// TODO Auto-generated method stub

    }

}
