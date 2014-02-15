package net.onrc.onos.ofcontroller.networkgraph;

import java.nio.ByteBuffer;

/**
 * Self-contained Port event Object
 *
 * TODO: We probably want common base class/interface for Self-Contained Event Object
 *
 */
public class PortEvent {
    public static class SwitchPort {
        public final Long dpid;
        public final Long number;

        public SwitchPort(Long dpid, Long number) {
            this.dpid = dpid;
            this.number = number;
        }

        public Long getDpid() {
            return dpid;
        }

        public Long getNumber() {
            return number;
        }

        @Override
        public String toString() {
            return "(" + Long.toHexString(dpid) + "@" + number + ")";
        }

    }

    private final SwitchPort id;
    // TODO Add Hardware Address
    // TODO Add Description

    public PortEvent(Long dpid, Long number) {
	this.id = new SwitchPort(dpid, number);
    }

    public Long getDpid() {
	return id.dpid;
    }

    public Long getNumber() {
	return id.number;
    }

    @Override
    public String toString() {
	return "[PortEvent 0x" + Long.toHexString(id.dpid) + "@" + id.number + "]";
    }

    public static final int PORTID_BYTES = SwitchEvent.SWITCHID_BYTES + 2 + 8;

    public static byte[] getPortID(Long dpid, Long number) {
	if (dpid == null) {
	    throw new IllegalArgumentException("dpid cannot be null");
	}
	if (number == null) {
	    throw new IllegalArgumentException("number cannot be null");
	}
	return ByteBuffer.allocate(PortEvent.PORTID_BYTES).putChar('S').putLong(dpid)
		.putChar('P').putLong(number).array();
    }

}
