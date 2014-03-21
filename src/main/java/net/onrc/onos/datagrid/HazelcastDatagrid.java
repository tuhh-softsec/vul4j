package net.onrc.onos.datagrid;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.restserver.IRestApiService;
import net.onrc.onos.datagrid.web.DatagridWebRoutable;
import net.onrc.onos.ofcontroller.devicemanager.IDeviceEventHandler;
import net.onrc.onos.ofcontroller.devicemanager.OnosDevice;
import net.onrc.onos.ofcontroller.proxyarp.ArpReplyNotification;
import net.onrc.onos.ofcontroller.proxyarp.IArpReplyEventHandler;
import net.onrc.onos.ofcontroller.proxyarp.IPacketOutEventHandler;
import net.onrc.onos.ofcontroller.proxyarp.PacketOutNotification;
import net.onrc.onos.ofcontroller.util.serializers.KryoFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.hazelcast.config.Config;
import com.hazelcast.config.FileSystemXmlConfig;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IList;
import com.hazelcast.core.IMap;
import com.hazelcast.instance.GroupProperties;
import net.onrc.onos.intent.Intent;

/**
 * A datagrid service that uses Hazelcast as a datagrid.
 * The relevant data is stored in the Hazelcast datagrid and shared as
 * appropriate in a multi-node cluster.
 */
public class HazelcastDatagrid implements IFloodlightModule, IDatagridService {
    private static final int MAX_BUFFER_SIZE = 64 * 1024;

    static final Logger log = LoggerFactory.getLogger(HazelcastDatagrid.class);
    private IRestApiService restApi;

    static final String HAZELCAST_CONFIG_FILE = "datagridConfig";
    private HazelcastInstance hazelcastInstance;
    private Config hazelcastConfig;

    private final KryoFactory kryoFactory = new KryoFactory();

    //
    // NOTE: eventChannels is kept thread safe by using explicit "synchronized"
    // blocks below. Those are needed to protect the integrity of each entry
    // instance, and avoid preemption during channel creation/startup.
    //
    private final Map<String, IEventChannel<?, ?>> eventChannels = new HashMap<>();

    // State related to the packet out map
    private static final String PACKET_OUT_MAP_NAME = "packetOutMap";
    private IMap<PacketOutNotification, byte[]> packetOutMap;
    private final List<IPacketOutEventHandler> packetOutEventHandlers = new ArrayList<>();

    private final byte[] dummyByte = {0};

    // State related to the ARP reply map
    private static final String ARP_REPLY_MAP_NAME = "arpReplyMap";
    private IMap<ArpReplyNotification, byte[]> arpReplyMap;
    private final List<IArpReplyEventHandler> arpReplyEventHandlers = new ArrayList<>();


    private static final String INTENT_LIST_NAME = "intentList";
    private IList<Intent> intentList;

    @Override
    public void registerIntent(Collection<Intent> intents) {
        intentList.addAll(intents);
    }


    // State related to the Network Device map
    private static final String MAP_DEVICE_NAME = "mapDevice";
    private IMap<Long, OnosDevice> mapDevice;
    private final List<IDeviceEventHandler> deviceEventHandlers = new ArrayList<>();

    /**
     * MapDeviceListener - reacts to Device related events.
     */
    class MapDeviceListener implements EntryListener<Long, OnosDevice> {

        @Override
        public void entryAdded(EntryEvent<Long, OnosDevice> event) {
            for (IDeviceEventHandler deviceEventHandler : deviceEventHandlers) {
                deviceEventHandler.addDeviceEvent(event.getKey(), event.getValue());
            }
        }

        @Override
        public void entryRemoved(EntryEvent<Long, OnosDevice> event) {
            for (IDeviceEventHandler deviceEventHandler : deviceEventHandlers) {
                deviceEventHandler.deleteDeviceEvent(event.getKey(), event.getValue());
            }
        }

        @Override
        public void entryUpdated(EntryEvent<Long, OnosDevice> event) {
            for (IDeviceEventHandler deviceEventHandler : deviceEventHandlers) {
                deviceEventHandler.updateDeviceEvent(event.getKey(), event.getValue());
            }
        }

        @Override
        public void entryEvicted(EntryEvent<Long, OnosDevice> arg0) {
            //Not used.
        }
    }

    /**
     * Class for receiving notifications for sending packet-outs.
     * <p/>
     * The datagrid map is:
     * - Key: Packet-out to send (PacketOutNotification)
     * - Value: dummy value (we only need the key) (byte[])
     */
    class PacketOutMapListener implements EntryListener<PacketOutNotification, byte[]> {
        /**
         * Receive a notification that an entry is added.
         *
         * @param event the notification event for the entry.
         */
        @Override
        public void entryAdded(EntryEvent<PacketOutNotification, byte[]> event) {
            for (IPacketOutEventHandler packetOutEventHandler : packetOutEventHandlers) {
                packetOutEventHandler.packetOutNotification(event.getKey());
            }
        }

        /**
         * Receive a notification that an entry is removed.
         *
         * @param event the notification event for the entry.
         */
        @Override
        public void entryRemoved(EntryEvent<PacketOutNotification, byte[]> event) {
            // Not used
        }

        /**
         * Receive a notification that an entry is updated.
         *
         * @param event the notification event for the entry.
         */
        @Override
        public void entryUpdated(EntryEvent<PacketOutNotification, byte[]> event) {
            // Not used
        }

        /**
         * Receive a notification that an entry is evicted.
         *
         * @param event the notification event for the entry.
         */
        @Override
        public void entryEvicted(EntryEvent<PacketOutNotification, byte[]> event) {
            // Not used
        }
    }

    /**
     * Class for receiving notifications for sending packet-outs.
     * <p/>
     * The datagrid map is:
     * - Key: Packet-out to send (PacketOutNotification)
     * - Value: dummy value (we only need the key) (byte[])
     */
    class ArpReplyMapListener implements EntryListener<ArpReplyNotification, byte[]> {
        /**
         * Receive a notification that an entry is added.
         *
         * @param event the notification event for the entry.
         */
        @Override
        public void entryAdded(EntryEvent<ArpReplyNotification, byte[]> event) {
            triggerEventHandler(event.getKey());
        }

        @Override
        public void entryUpdated(EntryEvent<ArpReplyNotification, byte[]> event) {
            triggerEventHandler(event.getKey());
        }

        @Override
        public void entryRemoved(EntryEvent<ArpReplyNotification, byte[]> event) {
            // Not used for ARP replies
        }

        @Override
        public void entryEvicted(EntryEvent<ArpReplyNotification, byte[]> event) {
            // Not used for ARP replies
        }

        /**
         * Handle an event.
         * @param notification notification
         */
        private void triggerEventHandler(ArpReplyNotification notification) {
            for (IArpReplyEventHandler arpReplyEventHandler : arpReplyEventHandlers) {
                arpReplyEventHandler.arpReplyEvent(notification);
            }
        }
    }

    /**
     * Initialize the Hazelcast Datagrid operation.
     *
     * @param configFilename the configuration filename.
     */
    public void init(String configFilename) {
    /*
        System.setProperty("hazelcast.socket.receive.buffer.size", "32");
        System.setProperty("hazelcast.socket.send.buffer.size", "32");
        */
        // System.setProperty("hazelcast.heartbeat.interval.seconds", "100");

        // Init from configuration file
        try {
            hazelcastConfig = new FileSystemXmlConfig(configFilename);
        } catch (FileNotFoundException e) {
            log.error("Error opening Hazelcast XML configuration. File not found: " + configFilename, e);
        }
        /*
        hazelcastConfig.setProperty(GroupProperties.PROP_IO_THREAD_COUNT, "1");
        hazelcastConfig.setProperty(GroupProperties.PROP_OPERATION_THREAD_COUNT, "1");
        hazelcastConfig.setProperty(GroupProperties.PROP_EVENT_THREAD_COUNT, "1");
        */
        //
        hazelcastConfig.setProperty(GroupProperties.PROP_EVENT_QUEUE_CAPACITY, "4000000");
        hazelcastConfig.setProperty(GroupProperties.PROP_SOCKET_RECEIVE_BUFFER_SIZE, "4096");
        hazelcastConfig.setProperty(GroupProperties.PROP_SOCKET_SEND_BUFFER_SIZE, "4096");
    }

    /**
     * Shutdown the Hazelcast Datagrid operation.
     */
    @Override
    protected void finalize() {
        close();
    }

    /**
     * Shutdown the Hazelcast Datagrid operation.
     */
    public void close() {
        Hazelcast.shutdownAll();
    }

    /**
     * Get the collection of offered module services.
     *
     * @return the collection of offered module services.
     */
    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleServices() {
        Collection<Class<? extends IFloodlightService>> l =
                new ArrayList<Class<? extends IFloodlightService>>();
        l.add(IDatagridService.class);
        return l;
    }

    /**
     * Get the collection of implemented services.
     *
     * @return the collection of implemented services.
     */
    @Override
    public Map<Class<? extends IFloodlightService>, IFloodlightService>
    getServiceImpls() {
        Map<Class<? extends IFloodlightService>,
                IFloodlightService> m =
                new HashMap<Class<? extends IFloodlightService>,
                        IFloodlightService>();
        m.put(IDatagridService.class, this);
        return m;
    }

    /**
     * Get the collection of modules this module depends on.
     *
     * @return the collection of modules this module depends on.
     */
    @Override
    public Collection<Class<? extends IFloodlightService>>
    getModuleDependencies() {
        Collection<Class<? extends IFloodlightService>> l =
                new ArrayList<Class<? extends IFloodlightService>>();
        l.add(IFloodlightProviderService.class);
        l.add(IRestApiService.class);
        return l;
    }

    /**
     * Initialize the module.
     *
     * @param context the module context to use for the initialization.
     * @throws FloodlightModuleException on error
     */
    @Override
    public void init(FloodlightModuleContext context)
            throws FloodlightModuleException {
        restApi = context.getServiceImpl(IRestApiService.class);

        // Get the configuration file name and configure the Datagrid
        Map<String, String> configMap = context.getConfigParams(this);
        String configFilename = configMap.get(HAZELCAST_CONFIG_FILE);
        this.init(configFilename);
    }

    /**
     * Startup module operation.
     *
     * @param context the module context to use for the startup.
     */
    @Override
    public void startUp(FloodlightModuleContext context) {
        hazelcastInstance = Hazelcast.newHazelcastInstance(hazelcastConfig);

        restApi.addRestletRoutable(new DatagridWebRoutable());

        packetOutMap = hazelcastInstance.getMap(PACKET_OUT_MAP_NAME);
        packetOutMap.addEntryListener(new PacketOutMapListener(), true);

        arpReplyMap = hazelcastInstance.getMap(ARP_REPLY_MAP_NAME);
        arpReplyMap.addEntryListener(new ArpReplyMapListener(), true);
        intentList = hazelcastInstance.getList(INTENT_LIST_NAME);

        mapDevice = hazelcastInstance.getMap(MAP_DEVICE_NAME);
        mapDevice.addEntryListener(new MapDeviceListener(), true);
    }

    /**
     * Create an event channel.
     * <p/>
     * If the channel already exists, just return it.
     * NOTE: The channel is started automatically.
     *
     * @param channelName the event channel name.
     * @param <K>         the type of the Key in the Key-Value store.
     * @param <V>         the type of the Value in the Key-Value store.
     * @param typeK       the type of the Key in the Key-Value store.
     * @param typeV       the type of the Value in the Key-Value store.
     * @return the event channel for the channel name.
     */
    @Override
    public <K, V> IEventChannel<K, V> createChannel(String channelName,
                                                    Class<K> typeK, Class<V> typeV) {
        synchronized (eventChannels) {
            IEventChannel<K, V> eventChannel =
                createChannelImpl(channelName, typeK, typeV);
            eventChannel.startup();
            return eventChannel;
	}
    }

    /**
     * Create an event channel implementation.
     * <p/>
     * If the channel already exists, just return it.
     * NOTE: The caller must call IEventChannel.startup() to startup the
     * channel operation.
     * NOTE: The caller must own the lock on "eventChannels".
     *
     * @param channelName the event channel name.
     * @param <K>         the type of the Key in the Key-Value store.
     * @param <V>         the type of the Value in the Key-Value store.
     * @param typeK       the type of the Key in the Key-Value store.
     * @param typeV       the type of the Value in the Key-Value store.
     * @return the event channel for the channel name.
     */
    private <K, V> IEventChannel<K, V> createChannelImpl(
            String channelName,
            Class<K> typeK, Class<V> typeV) {
        IEventChannel<K, V> castedEventChannel;
        IEventChannel<?, ?> genericEventChannel =
                eventChannels.get(channelName);

        // Add the channel if the first listener
        if (genericEventChannel == null) {
            castedEventChannel =
                    new HazelcastEventChannel<K, V>(hazelcastInstance,
                            channelName, typeK, typeV);
            eventChannels.put(channelName, castedEventChannel);
        } else {
            //
            // TODO: Find if we can use Java internal support to check for
            // type mismatch.
            //
            if (!genericEventChannel.verifyKeyValueTypes(typeK, typeV)) {
                throw new ClassCastException("Key-value type mismatch for event channel " + channelName);
            }
            castedEventChannel = (IEventChannel<K, V>) genericEventChannel;
        }

        return castedEventChannel;
    }

    /**
     * Add event channel listener.
     *
     * NOTE: The channel is started automatically right after the listener
     * is added.
     *
     * @param channelName the event channel name.
     * @param listener    the listener to add.
     * @param <K>         the type of the Key in the Key-Value store.
     * @param <V>         the type of the Value in the Key-Value store.
     * @param typeK       the type of the Key in the Key-Value store.
     * @param typeV       the type of the Value in the Key-Value store.
     * @return the event channel for the channel name.
     */
    @Override
    public <K, V> IEventChannel<K, V> addListener(String channelName,
                                                  IEventChannelListener<K, V> listener,
                                                  Class<K> typeK, Class<V> typeV) {
        synchronized (eventChannels) {
            IEventChannel<K, V> eventChannel =
                createChannelImpl(channelName, typeK, typeV);
            eventChannel.addListener(listener);
            eventChannel.startup();

            return eventChannel;
	}
    }

    /**
     * Remove event channel listener.
     *
     * @param <K>         the type of the Key in the Key-Value store.
     * @param <V>         the type of the Value in the Key-Value store.
     * @param channelName the event channel name.
     * @param listener    the listener to remove.
     */
    @Override
    public <K, V> void removeListener(String channelName,
                                      IEventChannelListener<K, V> listener) {
	synchronized (eventChannels) {
            IEventChannel<?, ?> genericEventChannel =
                eventChannels.get(channelName);

            if (genericEventChannel != null) {
                //
                // TODO: Find if we can use Java internal support to check for
                // type mismatch.
                // NOTE: Using "ClassCastException" exception below doesn't
                // work.
                //
                IEventChannel<K, V> castedEventChannel =
                    (IEventChannel<K, V>) genericEventChannel;
                castedEventChannel.removeListener(listener);
            }
        }
    }

    @Override
    public void registerPacketOutEventHandler(IPacketOutEventHandler packetOutEventHandler) {
        if (packetOutEventHandler != null) {
            packetOutEventHandlers.add(packetOutEventHandler);
        }
    }

    @Override
    public void deregisterPacketOutEventHandler(IPacketOutEventHandler packetOutEventHandler) {
        packetOutEventHandlers.remove(packetOutEventHandler);
    }

    @Override
    public void registerArpReplyEventHandler(IArpReplyEventHandler arpReplyEventHandler) {
        if (arpReplyEventHandler != null) {
            arpReplyEventHandlers.add(arpReplyEventHandler);
        }
    }

    @Override
    public void deregisterArpReplyEventHandler(IArpReplyEventHandler arpReplyEventHandler) {
        arpReplyEventHandlers.remove(arpReplyEventHandler);
    }

    @Override
    public void registerMapDeviceEventHandler(IDeviceEventHandler deviceEventHandler) {
        if (deviceEventHandler != null) {
            deviceEventHandlers.add(deviceEventHandler);
        }
    }

    @Override
    public void deregisterMapDeviceEventHandler(IDeviceEventHandler deviceEventHandler) {
        deviceEventHandlers.remove(deviceEventHandler);
    }

    @Override
    public void sendPacketOutNotification(PacketOutNotification packetOutNotification) {
        packetOutMap.putAsync(packetOutNotification, dummyByte, 1L, TimeUnit.MILLISECONDS);
    }

    @Override
    public void sendArpReplyNotification(ArpReplyNotification arpReply) {
        arpReplyMap.putAsync(arpReply, dummyByte, 1L, TimeUnit.MILLISECONDS);
    }

    @Override
    public void sendNotificationDeviceAdded(Long mac, OnosDevice dev) {
        log.debug("DeviceAdded in datagrid. mac {}", dev.getMacAddress());
        mapDevice.putAsync(mac, dev);
    }

    @Override
    public void sendNotificationDeviceDeleted(OnosDevice dev) {
        long mac = dev.getMacAddress().toLong();
        if (mapDevice.containsKey(mac)) {
            log.debug("DeviceDeleted in datagrid. mac {}", dev.getMacAddress());
            mapDevice.removeAsync(mac);
        }
    }
}
