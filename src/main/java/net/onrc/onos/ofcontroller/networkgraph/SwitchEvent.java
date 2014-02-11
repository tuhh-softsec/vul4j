package net.onrc.onos.ofcontroller.networkgraph;

/**
 * Self-contained Switch object for event
 *
 * TODO: We probably want common base class/interface for Self-Contained Event Object
 *
 */
public class SwitchEvent {
    private final Long dpid;

    public SwitchEvent(Long dpid) {
	this.dpid = dpid;
    }

    public Long getDpid() {
	return dpid;
    }

    @Override
    public String toString() {
	return "[SwitchEvent 0x" + Long.toHexString(dpid) + "]";
    }

}
