package net.onrc.onos.core.datagrid;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.restserver.IRestApiService;
import net.onrc.onos.core.datagrid.web.DatagridWebRoutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.config.Config;
import com.hazelcast.config.FileSystemXmlConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.GroupProperties;

/**
 * A datagrid service that uses Hazelcast as a datagrid.
 * The relevant data is stored in the Hazelcast datagrid and shared as
 * appropriate in a multi-node cluster.
 */
public class HazelcastDatagrid implements IFloodlightModule, IDatagridService {
    static final Logger log = LoggerFactory.getLogger(HazelcastDatagrid.class);
    private IRestApiService restApi;

    static final String HAZELCAST_CONFIG_FILE = "datagridConfig";
    private HazelcastInstance hazelcastInstance;
    private Config hazelcastConfig;

    //
    // NOTE: eventChannels is kept thread safe by using explicit "synchronized"
    // blocks below. Those are needed to protect the integrity of each entry
    // instance, and avoid preemption during channel creation/startup.
    //
    private final Map<String, IEventChannel<?, ?>> eventChannels = new HashMap<>();

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
        IEventChannel<?, ?> genericEventChannel =
                eventChannels.get(channelName);

        // Add the channel if the first listener
        if (genericEventChannel == null) {
            IEventChannel<K, V> castedEventChannel =
                    new HazelcastEventChannel<K, V>(hazelcastInstance,
                            channelName, typeK, typeV);
            eventChannels.put(channelName, castedEventChannel);
            return castedEventChannel;
        }

        //
        // TODO: Find if we can use Java internal support to check for
        // type mismatch.
        //
        if (!genericEventChannel.verifyKeyValueTypes(typeK, typeV)) {
            throw new ClassCastException("Key-value type mismatch for event channel " + channelName);
        }
        @SuppressWarnings("unchecked")
        IEventChannel<K, V> castedEventChannel =
                (IEventChannel<K, V>) genericEventChannel;
        return castedEventChannel;
    }

    /**
     * Add event channel listener.
     * <p/>
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
                @SuppressWarnings("unchecked")
                IEventChannel<K, V> castedEventChannel =
                        (IEventChannel<K, V>) genericEventChannel;
                castedEventChannel.removeListener(listener);
            }
        }
    }
}
