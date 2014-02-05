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

import edu.stanford.ramcloud.JRamCloud;
import net.onrc.onos.datastore.RCObject;
import net.onrc.onos.datastore.RCTable;

public class RCLink extends RCObject {
    @SuppressWarnings("unused")
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

    // FIXME these should be Enum or some number, not String
    private static final String PROP_STATUS = "status";
    private static final String PROP_SRC_SW_ID = "src-sw-id";
    private static final String PROP_SRC_PORT_ID = "src-port-id";
    private static final String PROP_DST_SW_ID = "dst-sw-id";
    private static final String PROP_DST_PORT_ID = "dst-port-id";

    // must not re-order enum members, ordinal will be sent over wire
    public enum STATUS {
	INACTIVE, ACTIVE;
    }

    private final SwitchPort src;
    private final SwitchPort dst;
    private STATUS status;

    public static final int LINKID_BYTES = 2 + RCPort.PORTID_BYTES * 2;

    public static byte[] getLinkID(Long src_dpid, Long src_port_no,
	    Long dst_dpid, Long dst_port_no) {
	return ByteBuffer.allocate(LINKID_BYTES).putChar('L')
	        .put(RCPort.getPortID(src_dpid, src_port_no))
	        .put(RCPort.getPortID(dst_dpid, dst_port_no)).array();
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
	keyBuf.position(2 + RCPort.PORTID_BYTES);
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
	getObjectMap().put(PROP_STATUS, status);
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

	map.put(PROP_SRC_SW_ID, src.getSwitchID());
	map.put(PROP_SRC_PORT_ID, src.getPortID());
	map.put(PROP_DST_SW_ID, dst.getSwitchID());
	map.put(PROP_DST_PORT_ID, dst.getPortID());

	serializeAndSetValue(linkKryo.get(), map);
    }

    @Override
    public Map<Object, Object> deserializeObjectFromValue() {
	Map<Object, Object> map = deserializeObjectFromValue(linkKryo.get());

	this.status = (STATUS) map.get(PROP_STATUS);
	return map;
    }

    @Override
    public String toString() {
	return "[RCLink " + src + "->" + dst + " STATUS:" + status + "]";
    }

    public static void main(String[] args) {
	// TODO Auto-generated method stub

    }

}
