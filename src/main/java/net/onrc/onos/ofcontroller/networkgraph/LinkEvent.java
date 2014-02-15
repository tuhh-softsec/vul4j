package net.onrc.onos.ofcontroller.networkgraph;

import java.nio.ByteBuffer;

import net.onrc.onos.ofcontroller.networkgraph.PortEvent.SwitchPort;

/**
 * Self-contained Link event Object
 *
 * TODO: We probably want common base class/interface for Self-Contained Event Object
 *
 */
public class LinkEvent {
    private final SwitchPort src;
    private final SwitchPort dst;

    /**
     * Default constructor.
     */
    public LinkEvent() {
	src = null;
	dst = null;
    }

    public LinkEvent(Long src_dpid, Long src_port_no, Long dst_dpid,
	    Long dst_port_no) {

	src = new SwitchPort(src_dpid, src_port_no);
	dst = new SwitchPort(dst_dpid, dst_port_no);

    }

    public SwitchPort getSrc() {
        return src;
    }

    public SwitchPort getDst() {
        return dst;
    }

    @Override
    public String toString() {
	return "[LinkEvent " + src + "->" + dst + "]";
    }

    public static final int LINKID_BYTES = 2 + PortEvent.PORTID_BYTES * 2;

    public static byte[] getLinkID(Long src_dpid, Long src_port_no,
	    Long dst_dpid, Long dst_port_no) {
	return ByteBuffer.allocate(LinkEvent.LINKID_BYTES).putChar('L')
		.put(PortEvent.getPortID(src_dpid, src_port_no))
		.put(PortEvent.getPortID(dst_dpid, dst_port_no)).array();
    }

    public byte[] getID() {
	return getLinkID(src.getDpid(), src.getNumber(),
			 dst.getDpid(), dst.getNumber());
    }
}
