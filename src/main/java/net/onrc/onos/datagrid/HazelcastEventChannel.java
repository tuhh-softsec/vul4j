package net.onrc.onos.datagrid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import net.onrc.onos.ofcontroller.util.serializers.KryoFactory;

/**
 * A datagrid event channel that uses Hazelcast as a datagrid.
 */
public class HazelcastEventChannel<K, V> implements IEventChannel<K, V> {
    private HazelcastInstance hazelcastInstance; // The Hazelcast instance
    private String channelName;			// The event channel name
    private Class<?> typeK;			// The class type of the key
    private Class<?> typeV;			// The class type of the value
    private IMap<K, byte[]> channelMap = null;	// The Hazelcast channel map
    private List<IEventChannelListener<K, V>> listeners = // Channel listeners
	new ArrayList<IEventChannelListener<K, V>>();

    // The map entry listener
    private MapEntryListener mapEntryListener = new MapEntryListener<K>();
    private String mapListenerId = null;	// The map listener ID

    // TODO: We should use a single global KryoFactory instance
    private KryoFactory kryoFactory = new KryoFactory();

    // Maximum serialized event size
    private final static int MAX_BUFFER_SIZE = 64*1024;

    /**
     * Constructor for a given event channel name.
     *
     * @param hazelcastInstance the Hazelcast instance to use.
     * @param channelName the event channel name.
     * @param typeK the type of the Key in the Key-Value store.
     * @param typeV the type of the Value in the Key-Value store.
     */
    public HazelcastEventChannel(HazelcastInstance hazelcastInstance,
				 String channelName, Class<K> typeK,
				 Class<V> typeV) {
	this.hazelcastInstance = hazelcastInstance;
	this.channelName = channelName;
	this.typeK = typeK;
	this.typeV = typeV;
    }

    /**
     * Verify the key and value types of a channel.
     *
     * @param typeK the type of the key to verify.
     * @param typeV the type of the value to verify.
     * @return true if the key and value types of the channel match,
     * otherwise false.
     */
    @Override
    public boolean verifyKeyValueTypes(Class typeK, Class typeV) {
	return (this.typeK == typeK) && (this.typeV == typeV);
    }

    /**
     * Cleanup and destroy the channel.
     */
    @Override
    protected void finalize() {
	shutdown();
    }

    /**
     * Startup the channel operation.
     */
    @Override
    public void startup() {
	if (channelMap == null) {
	    channelMap = hazelcastInstance.getMap(channelName);
	    mapListenerId = channelMap.addEntryListener(mapEntryListener,
							true);
	}
    }

    /**
     * Shutdown the channel operation.
     */
    @Override
    public void shutdown() {
	if (channelMap != null) {
	    channelMap.removeEntryListener(mapListenerId);
	    channelMap = null;
	    mapListenerId = null;
	}
    }

    /**
     * Add event channel listener.
     *
     * @param listener the listener to add.
     */
    @Override
    public void addListener(IEventChannelListener<K, V> listener) {
	if (listeners.contains(listener))
	    return;		// Nothing to do: already a listener
	listeners.add(listener);
    }

    /**
     * Remove event channel listener.
     *
     * @param listener the listener to remove.
     */
    @Override
    public void removeListener(IEventChannelListener<K, V> listener) {
	listeners.remove(listener);
    }

    /**
     * Add an entry to the channel.
     *
     * @param key the key of the entry to add.
     * @param value the value of the entry to add.
     */
    @Override
    public void addEntry(K key, V value) {
	//
	// Encode the value
	//
	byte[] buffer = new byte[MAX_BUFFER_SIZE];
	Kryo kryo = kryoFactory.newKryo();
	Output output = new Output(buffer, -1);
	kryo.writeObject(output, value);
	byte[] valueBytes = output.toBytes();
	kryoFactory.deleteKryo(kryo);

	//
	// Put the entry in the map:
	//  - Key : Type <K>
	//  - Value : Serialized Value (byte[])
	//
	channelMap.putAsync(key, valueBytes);
    }

    /**
     * Remove an entry from the channel.
     *
     * @param key the key of the entry to remove.
     */
    @Override
    public void removeEntry(K key) {
	//
	// Remove the entry:
	//  - Key : Type <K>
	//  - Value : Serialized Value (byte[])
	//
	channelMap.removeAsync(key);
    }

    /**
     * Update an entry in the channel.
     *
     * @param key the key of the entry to update.
     * @param value the value of the entry to update.
     */
    @Override
    public void updateEntry(K key, V value) {
	// NOTE: Adding an entry with an existing key automatically updates it
	addEntry(key, value);
    }

    /**
     * Get an entry from the channel.
     *
     * @param key the key of the entry to get.
     * @return the entry if found, otherwise null.
     */
    @Override
    @Deprecated
    public V getEntry(K key) {
	byte[] valueBytes = channelMap.get(key);
	if (valueBytes == null)
	    return null;

	Kryo kryo = kryoFactory.newKryo();
	//
	// Decode the value
	//
	Input input = new Input(valueBytes);
	V value = (V)kryo.readObject(input, typeV);
	kryoFactory.deleteKryo(kryo);

	return value;
    }

    /**
     * Get all entries in the channel.
     *
     * @return all entries that are currently in the channel.
     */
    @Override
    @Deprecated
    public Collection<V> getAllEntries() {
	Collection<V> allEntries = new LinkedList<V>();

	if (channelMap == null)
	    return allEntries;		// Nothing found

	//
	// Get all entries
	//
	Collection<byte[]> values = channelMap.values();
	Kryo kryo = kryoFactory.newKryo();
	for (byte[] valueBytes : values) {
	    //
	    // Decode the value
	    //
	    Input input = new Input(valueBytes);
	    V value = (V)kryo.readObject(input, typeV);
	    allEntries.add(value);
	}
	kryoFactory.deleteKryo(kryo);

	return allEntries;
    }

    /**
     * Remove all entries in the channel.
     */
    @Override
    @Deprecated
    public void removeAllEntries() {
	//
	// Remove all entries
	//
	// NOTE: We remove the entries one-by-one so the per-entry
	// notifications will be delivered.
	//
	// channelMap.clear();
	Set<K> keySet = channelMap.keySet();
	for (K key : keySet) {
	    channelMap.removeAsync(key);
	}
    }

    /**
     * Class for receiving event notifications for the channel.
     *
     * The datagrid map is:
     *  - Key: Type K
     *  - Value: Serialized V (byte[])
     */
    private class MapEntryListener<K> implements EntryListener<K, byte[]> {
	/**
	 * Receive a notification that an entry is added.
	 *
	 * @param event the notification event for the entry.
	 */
	@Override
	public void entryAdded(EntryEvent<K, byte[]> event) {
	    //
	    // Decode the value
	    //
	    byte[] valueBytes = event.getValue();
	    Kryo kryo = kryoFactory.newKryo();
	    Input input = new Input(valueBytes);
	    V value = (V)kryo.readObject(input, typeV);

	    //
	    // Deliver the notification
	    //
	    int index = 0;
	    for (IEventChannelListener listener : listeners) {
		V copyValue = value;
		if (index++ > 0) {
		    // Each listener should get a deep copy of the value
		    copyValue = kryo.copy(value);
		}
		listener.entryAdded(copyValue);
	    }
	    kryoFactory.deleteKryo(kryo);
	}

	/**
	 * Receive a notification that an entry is removed.
	 *
	 * @param event the notification event for the entry.
	 */
	@Override
	public void entryRemoved(EntryEvent<K, byte[]> event) {
	    //
	    // Decode the value
	    //
	    byte[] valueBytes = event.getValue();
	    Kryo kryo = kryoFactory.newKryo();
	    Input input = new Input(valueBytes);
	    V value = (V)kryo.readObject(input, typeV);

	    //
	    // Deliver the notification
	    //
	    int index = 0;
	    for (IEventChannelListener listener : listeners) {
		V copyValue = value;
		if (index++ > 0) {
		    // Each listener should get a deep copy of the value
		    copyValue = kryo.copy(value);
		}
		listener.entryRemoved(copyValue);
	    }
	    kryoFactory.deleteKryo(kryo);
	}

	/**
	 * Receive a notification that an entry is updated.
	 *
	 * @param event the notification event for the entry.
	 */
	@Override
	public void entryUpdated(EntryEvent<K, byte[]> event) {
	    //
	    // Decode the value
	    //
	    byte[] valueBytes = event.getValue();
	    Kryo kryo = kryoFactory.newKryo();
	    Input input = new Input(valueBytes);
	    V value = (V)kryo.readObject(input, typeV);

	    //
	    // Deliver the notification
	    //
	    int index = 0;
	    for (IEventChannelListener listener : listeners) {
		V copyValue = value;
		if (index++ > 0) {
		    // Each listener should get a deep copy of the value
		    copyValue = kryo.copy(value);
		}
		listener.entryUpdated(copyValue);
	    }
	    kryoFactory.deleteKryo(kryo);
	}

	/**
	 * Receive a notification that an entry is evicted.
	 *
	 * @param event the notification event for the entry.
	 */
	@Override
	public void entryEvicted(EntryEvent<K, byte[]> event) {
	    // NOTE: We don't use eviction for this map
	}
    }
}
