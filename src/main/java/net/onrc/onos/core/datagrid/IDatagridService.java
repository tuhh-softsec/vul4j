package net.onrc.onos.core.datagrid;

import net.floodlightcontroller.core.module.IFloodlightService;

/**
 * Interface for providing Datagrid Service to other modules.
 */
public interface IDatagridService extends IFloodlightService {
    /**
     * Create an event channel.
     * <p/>
     * If the channel already exists, just return it.
     * NOTE: The channel is started automatically.
     *
     * @param channelName the event channel name.
     * @param typeK       the type of the Key in the Key-Value store.
     * @param typeV       the type of the Value in the Key-Value store.
     * @return the event channel for the channel name.
     */
    <K, V> IEventChannel<K, V> createChannel(String channelName,
                                             Class<K> typeK, Class<V> typeV);

    /**
     * Add event channel listener.
     * <p/>
     * NOTE: The channel is started automatically right after the listener
     * is added.
     *
     * @param channelName the event channel name.
     * @param listener    the listener to add.
     * @param typeK       the type of the Key in the Key-Value store.
     * @param typeV       the type of the Value in the Key-Value store.
     * @return the event channel for the channel name.
     */
    <K, V> IEventChannel<K, V> addListener(String channelName,
                                           IEventChannelListener<K, V> listener,
                                           Class<K> typeK, Class<V> typeV);

    /**
     * Remove event channel listener.
     *
     * @param channelName the event channel name.
     * @param listener    the listener to remove.
     */
    <K, V> void removeListener(String channelName,
                               IEventChannelListener<K, V> listener);
}
