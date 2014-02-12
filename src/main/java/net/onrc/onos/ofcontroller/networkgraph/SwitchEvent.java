package net.onrc.onos.ofcontroller.networkgraph;

import java.util.ArrayList;
import java.util.List;

/**
 * Self-contained Switch and Port event Object
 *
 * TODO: We probably want common base class/interface for Self-Contained Event Object
 *
 */
public class SwitchEvent {
    private final Long dpid;

    private List<PortEvent> ports;

    public SwitchEvent(Long dpid) {
	this(dpid, new ArrayList<PortEvent>());
    }

    public SwitchEvent(Long dpid, List<PortEvent> ports) {
	this.dpid = dpid;
	this.ports = ports;
    }

    public Long getDpid() {
	return dpid;
    }

    public List<PortEvent> getPorts() {
	return ports;
    }

    public void setPorts(List<PortEvent> ports) {
	this.ports = ports;
    }

    @Override
    public String toString() {
	return "[SwitchEvent 0x" + Long.toHexString(dpid) + "]";
    }

}
