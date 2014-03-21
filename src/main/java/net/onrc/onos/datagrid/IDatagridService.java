package net.onrc.onos.datagrid;

import java.util.Collection;

import net.floodlightcontroller.core.module.IFloodlightService;
import net.onrc.onos.intent.Intent;
import net.onrc.onos.ofcontroller.devicemanager.IDeviceEventHandler;
import net.onrc.onos.ofcontroller.devicemanager.OnosDevice;
import net.onrc.onos.ofcontroller.proxyarp.ArpReplyNotification;
import net.onrc.onos.ofcontroller.proxyarp.IArpReplyEventHandler;
import net.onrc.onos.ofcontroller.proxyarp.IPacketOutEventHandler;
import net.onrc.onos.ofcontroller.proxyarp.PacketOutNotification;

/**
 * Interface for providing Datagrid Service to other modules.
 */
public interface IDatagridService extends IFloodlightService {
    /**
     * Create an event channel.
     *
     * If the channel already exists, just return it.
     * NOTE: The channel is started automatically.
     *
     * @param channelName the event channel name.
     * @param typeK the type of the Key in the Key-Value store.
     * @param typeV the type of the Value in the Key-Value store.
     * @return the event channel for the channel name.
     */
    <K, V> IEventChannel<K, V> createChannel(String channelName,
					     Class<K> typeK, Class<V> typeV);

    /**
     * Add event channel listener.
     *
     * NOTE: The channel is started automatically right after the listener
     * is added.
     *
     * @param channelName the event channel name.
     * @param listener the listener to add.
     * @param typeK the type of the Key in the Key-Value store.
     * @param typeV the type of the Value in the Key-Value store.
     * @return the event channel for the channel name.
     */
    <K, V> IEventChannel<K, V> addListener(String channelName,
			   IEventChannelListener<K, V> listener,
			   Class<K> typeK, Class<V> typeV);

    /**
     * Remove event channel listener.
     *
     * @param channelName the event channel name.
     * @param listener the listener to remove.
     */
    <K, V> void removeListener(String channelName,
			      IEventChannelListener<K, V> listener);

    /*
     * register all the intents as one batch
     */
    void registerIntent(Collection<Intent> intents);

    /**
     * Register event handler for packet-out events.
     * 
     * @param packetOutEventHandler The packet-out event handler to register.
     */
    public void registerPacketOutEventHandler(IPacketOutEventHandler packetOutEventHandler);
    
    /**
     * Deregister event handler service for packet-out events.
     * 
     * @param packetOutEventHandler The packet-out event handler to deregister.
     */
    public void deregisterPacketOutEventHandler(IPacketOutEventHandler packetOutEventHandler);
    
    /**
     * Register event handler for ARP reply events.
     * 
     * @param packetOutEventHandler The ARP reply event handler to register.
     */
    public void registerArpReplyEventHandler(IArpReplyEventHandler arpReplyEventHandler);
    
    /**
     * Deregister event handler service for ARP reply events.
     * 
     * @param packetOutEventHandler The ARP reply event handler to deregister.
     */
    public void deregisterArpReplyEventHandler(IArpReplyEventHandler arpReplyEventHandler);

    /**
     * Send a packet-out notification to other ONOS instances. This informs
     * other instances that they should send this packet out some of the ports
     * they control. Not all notifications are applicable to all instances 
     * (i.e. some notifications specify a single port to send the packet out),
     * so each instance must determine whether it needs to take action when it
     * receives the notification.
     * 
     * @param packetOutNotification The packet notification to send
     */
    public void sendPacketOutNotification(PacketOutNotification packetOutNotification);
    
    /**
     * Send notification to other ONOS instances that an ARP reply has been 
     * received.
     * @param arpReply The notification of the ARP reply
     */
    public void sendArpReplyNotification(ArpReplyNotification arpReply);

	void sendNotificationDeviceAdded(Long mac, OnosDevice dev);

	void sendNotificationDeviceDeleted(OnosDevice dev);

	void registerMapDeviceEventHandler(IDeviceEventHandler deviceEventHandler);

	void deregisterMapDeviceEventHandler(IDeviceEventHandler deviceEventHandler);

}
