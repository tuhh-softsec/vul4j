package net.onrc.onos.ofcontroller.proxyarp;

/**
 * Listener interface for ARP reply event callbacks.
 */
public interface IArpReplyEventHandler {
    /**
     * An ARP reply has been received.
     * @param arpReply data about the received ARP reply
     */
    public void arpReplyEvent(ArpReplyNotification arpReply);
}
