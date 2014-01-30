package net.onrc.onos.datastore.topology;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.Kryo;

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
	    return RCSwitch.getSwichID(dpid);
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

    public RCLink(Long src_dpid, Long src_port_no, Long dst_dpid,
	    Long dst_port_no) {
	super(RCTable.getTable(GLOBAL_LINK_TABLE_NAME), getLinkID(src_dpid,
	        src_port_no, dst_dpid, dst_port_no));

	src = new SwitchPort(src_dpid, src_port_no);
	dst = new SwitchPort(dst_dpid, dst_port_no);
	status = STATUS.INACTIVE;
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

    public static void main(String[] args) {
	// TODO Auto-generated method stub

    }

}
