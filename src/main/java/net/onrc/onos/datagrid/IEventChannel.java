package net.onrc.onos.datagrid;

import java.util.Collection;

/**
 * Event Channel Interface.
 */
public interface IEventChannel<K, V> {
    /**
     * Startup the channel operation.
     */
    void startup();

    /**
     * Shutdown the channel operation.
     */
    void shutdown();

    /**
     * Verify the key and value types of a channel.
     *
     * @param typeK the type of the key to verify.
     * @param typeV the type of the value to verify.
     * @return true if the key and value types of the channel match,
     * otherwise false.
     */
    boolean verifyKeyValueTypes(Class typeK, Class typeV);

    /**
     * Add event channel listener.
     *
     * @param listener the listener to add.
     */
    void addListener(IEventChannelListener<K, V> listener);

    /**
     * Remove event channel listener.
     *
     * @param listener the listener to remove.
     */
    void removeListener(IEventChannelListener<K, V> listener);

    /**
     * Add an entry to the channel.
     *
     * @param key the key of the entry to add.
     * @param value the value of the entry to add.
     */
    void addEntry(K key, V value);

    /**
     * Remove an entry from the channel.
     *
     * @param key the key of the entry to remove.
     */
    void removeEntry(K key);

    /**
     * Update an entry in the channel.
     *
     * @param key the key of the entry to update.
     * @param value the value of the entry to update.
     */
    void updateEntry(K key, V value);

    /**
     * Get an entry from the channel.
     *
     * @param key the key of the entry to get.
     * @return the entry if found, otherwise null.
     */
    @Deprecated
    V getEntry(K key);

    /**
     * Get all entries in the channel.
     *
     * @return all entries that are currently in the channel.
     */
    @Deprecated
    Collection<V> getAllEntries();

    /**
     * Remove all entries in the channel.
     */
    @Deprecated
    void removeAllEntries();
}
