package net.onrc.onos.datagrid;

/**
 * Event Channel Listener Interface.
 */
public interface IEventChannelListener<K, V> {
    /**
     * Receive a notification that an entry is added.
     *
     * @param value the value for the entry.
     */
    void entryAdded(V value);

    /**
     * Receive a notification that an entry is removed.
     *
     * @param value the value for the entry.
     */
    void entryRemoved(V value);

    /**
     * Receive a notification that an entry is updated.
     *
     * @param value the value for the entry.
     */
    void entryUpdated(V value);
}
