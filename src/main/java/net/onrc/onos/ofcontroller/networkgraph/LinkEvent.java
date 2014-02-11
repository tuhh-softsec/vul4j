package net.onrc.onos.ofcontroller.networkgraph;

import net.onrc.onos.ofcontroller.networkgraph.PortEvent.SwitchPort;

/**
 * Self-contained Link object for event
 *
 * TODO: We probably want common base class/interface for Self-Contained Event Object
 *
 */
public class LinkEvent {
    private final SwitchPort src;
    private final SwitchPort dst;

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

}
