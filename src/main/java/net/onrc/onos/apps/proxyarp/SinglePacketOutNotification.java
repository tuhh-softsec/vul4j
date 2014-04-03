package net.onrc.onos.apps.proxyarp;



// TODO This class is too generic to be handled by ProxyArpService.
/**
 * Notification to another ONOS instance to send a packet out a single port.
 */
public class SinglePacketOutNotification extends PacketOutNotification {

    private static final long serialVersionUID = 1L;

    private final int address;
    private final long outSwitch;
    private final short outPort;

    /**
     * Class constructor.
     * @param packet the packet data to send in the packet-out
     * @param address target IP address if the packet is an ARP packet
     * @param outSwitch the dpid of the switch to send the packet on
     * @param outPort the port number of the port to send the packet out
     */
    public SinglePacketOutNotification(byte[] packet, int address,
            long outSwitch, short outPort) {
        super(packet);

        this.address = address;
        this.outSwitch = outSwitch;
        this.outPort = outPort;
    }

    /**
     * Get the dpid of the switch the packet will be sent out.
     * @return the switch's dpid
     */
    public long getOutSwitch() {
        return outSwitch;
    }

    /**
     * Get the port number of the port the packet will be sent out.
     * @return the port number
     */
    public short getOutPort() {
        return outPort;
    }

    /**
     * Get the target IP address if the packet is an ARP packet.
     * @return the target IP address for ARP packets, or null if the packet is
     *         not an ARP packet
     */
    public int getTargetAddress() {
        return address;
    }
}
