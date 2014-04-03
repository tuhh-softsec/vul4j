package net.onrc.onos.ofcontroller.proxyarp;

import java.io.Serializable;

import net.floodlightcontroller.util.MACAddress;

/**
 * Inter-instance notification that an ARP reply has been received. The
 * notification contains both the IP address and the MAC address.
 */
public class ArpReplyNotification implements Serializable {

    private static final long serialVersionUID = 1L;

    private int targetAddress;
    private MACAddress targetMacAddress;

    protected ArpReplyNotification() {}
    /**
     * Class constructor.
     * @param targetAddress IP address received from the ARP reply
     * @param targetMacAddress MAC address received from the ARP reply
     */
    public ArpReplyNotification(int targetAddress,
            MACAddress targetMacAddress) {
        this.targetAddress = targetAddress;
        this.targetMacAddress = targetMacAddress;
    }

    /**
     * Returns the IP address of the ARP reply.
     * @return the IP address
     */
    public int getTargetAddress() {
        return targetAddress;
    }

    /**
     * Returns the MAC address of the ARP reply.
     * @return the MAC address
     */
    public MACAddress getTargetMacAddress() {
        return targetMacAddress;
    }

}
