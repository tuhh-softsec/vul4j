package net.onrc.onos.ofcontroller.networkgraph;

import java.nio.ByteBuffer;

/**
 * Self-contained Switch Object
 *
 * TODO: We probably want common base class/interface for Self-Contained Event Object
 *
 */
public class SwitchEvent {
    protected final Long dpid;

    /**
     * Default constructor.
     */
    public SwitchEvent() {
	dpid = null;
    }

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

    public static final int SWITCHID_BYTES = 2 + 8;

    public static ByteBuffer getSwitchID(Long dpid) {
	if (dpid == null) {
	    throw new IllegalArgumentException("dpid cannot be null");
	}
	return (ByteBuffer) ByteBuffer.allocate(SwitchEvent.SWITCHID_BYTES).putChar('S').putLong(dpid).flip();
    }

    public byte[] getID() {
	return getSwitchID(dpid).array();
    }

    public ByteBuffer getIDasByteBuffer() {
	return getSwitchID(dpid);
    }
}
