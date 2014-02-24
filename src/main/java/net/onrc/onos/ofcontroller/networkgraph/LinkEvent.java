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
    protected final SwitchPort src;
    protected final SwitchPort dst;

    /**
     * Default constructor for Serializer to use.
     */
    @Deprecated
    public LinkEvent() {
	src = null;
	dst = null;
    }

    public LinkEvent(Long src_dpid, Long src_port_no, Long dst_dpid,
	    Long dst_port_no) {
	src = new SwitchPort(src_dpid, src_port_no);
	dst = new SwitchPort(dst_dpid, dst_port_no);
    }

    public LinkEvent(Link link) {
	src = new SwitchPort(link.getSrcSwitch().getDpid(),
		link.getSrcPort().getNumber());
	dst = new SwitchPort(link.getDstSwitch().getDpid(),
		link.getDstPort().getNumber());
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

    public static ByteBuffer getLinkID(Long src_dpid, Long src_port_no,
	    Long dst_dpid, Long dst_port_no) {
	return (ByteBuffer) ByteBuffer.allocate(LinkEvent.LINKID_BYTES).putChar('L')
		.put(PortEvent.getPortID(src_dpid, src_port_no))
		.put(PortEvent.getPortID(dst_dpid, dst_port_no)).flip();
    }

    public byte[] getID() {
	return getLinkID(src.getDpid(), src.getNumber(),
		dst.getDpid(), dst.getNumber()).array();
    }

    public ByteBuffer getIDasByteBuffer() {
	return getLinkID(src.getDpid(), src.getNumber(),
		dst.getDpid(), dst.getNumber());
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((dst == null) ? 0 : dst.hashCode());
	result = prime * result + ((src == null) ? 0 : src.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	LinkEvent other = (LinkEvent) obj;
	if (dst == null) {
	    if (other.dst != null)
		return false;
	} else if (!dst.equals(other.dst))
	    return false;
	if (src == null) {
	    if (other.src != null)
		return false;
	} else if (!src.equals(other.src))
	    return false;
	return true;
    }
}