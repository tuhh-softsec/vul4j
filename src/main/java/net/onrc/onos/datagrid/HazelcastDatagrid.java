package net.onrc.onos.datagrid;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import com.esotericsoftware.kryo2.Kryo;
import com.esotericsoftware.kryo2.io.Input;
import com.esotericsoftware.kryo2.io.Output;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;

import net.onrc.onos.ofcontroller.flowmanager.IFlowService;
import net.onrc.onos.ofcontroller.util.FlowId;
import net.onrc.onos.ofcontroller.util.FlowPath;
import net.onrc.onos.ofcontroller.util.serializers.KryoFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.config.Config;
import com.hazelcast.config.FileSystemXmlConfig;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.instance.GroupProperties;

/**
 * A datagrid service that uses Hazelcast as a datagrid.
 * The relevant data is stored in the Hazelcast datagrid and shared as
 * appropriate in a multi-node cluster.
 */
public class HazelcastDatagrid implements IFloodlightModule, IDatagridService {
    private final static int MAX_BUFFER_SIZE = 64*1024;

    protected final static Logger log = LoggerFactory.getLogger(HazelcastDatagrid.class);
    protected IFloodlightProviderService floodlightProvider;

    protected static final String HazelcastConfigFile = "datagridConfig";
    private HazelcastInstance hazelcastInstance = null;
    private Config hazelcastConfig = null;

    private KryoFactory kryoFactory = new KryoFactory();

    // State related to the Flow map
    protected static final String mapFlowName = "mapFlow";
    private IFlowService flowService = null;
    private IMap<Long, byte[]> mapFlow = null;
    private MapFlowListener mapFlowListener = null;
    private String mapFlowListenerId = null;

    /**
     * Class for receiving notifications for Flow state.
     *
     * The datagrid map is:
     *  - Key : Flow ID (Long)
     *  - Value : Serialized Flow (byte[])
     */
    class MapFlowListener implements EntryListener<Long, byte[]> {
	/**
	 * Receive a notification that an entry is added.
	 *
	 * @param event the notification event for the entry.
	 */
	public void entryAdded(EntryEvent event) {
	    Long keyLong = (Long)event.getKey();
	    byte[] valueBytes = (byte[])event.getValue();

	    //
	    // Decode the value and deliver the notification
	    //
	    Kryo kryo = kryoFactory.newKryo();
	    Input input = new Input(valueBytes);
	    FlowPath flowPath = kryo.readObject(input, FlowPath.class);
	    kryoFactory.deleteKryo(kryo);
	    flowService.notificationRecvFlowAdded(flowPath);
	}

	/**
	 * Receive a notification that an entry is removed.
	 *
	 * @param event the notification event for the entry.
	 */
	public void entryRemoved(EntryEvent event) {
	    Long keyLong = (Long)event.getKey();
	    byte[] valueBytes = (byte[])event.getValue();

	    //
	    // Decode the value and deliver the notification
	    //
	    Kryo kryo = kryoFactory.newKryo();
	    Input input = new Input(valueBytes);
	    FlowPath flowPath = kryo.readObject(input, FlowPath.class);
	    kryoFactory.deleteKryo(kryo);
	    flowService.notificationRecvFlowRemoved(flowPath);
	}

	/**
	 * Receive a notification that an entry is updated.
	 *
	 * @param event the notification event for the entry.
	 */
	public void entryUpdated(EntryEvent event) {
	    Long keyLong = (Long)event.getKey();
	    byte[] valueBytes = (byte[])event.getValue();

	    //
	    // Decode the value and deliver the notification
	    //
	    Kryo kryo = kryoFactory.newKryo();
	    Input input = new Input(valueBytes);
	    FlowPath flowPath = kryo.readObject(input, FlowPath.class);
	    kryoFactory.deleteKryo(kryo);
	    flowService.notificationRecvFlowUpdated(flowPath);
	}

	/**
	 * Receive a notification that an entry is evicted.
	 *
	 * @param event the notification event for the entry.
	 */
	public void entryEvicted(EntryEvent event) {
	    // NOTE: We don't use eviction for this map
	}
    }

    /**
     * Initialize the Hazelcast Datagrid operation.
     *
     * @param conf the configuration filename.
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
    public void finalize() {
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
        return l;
    }

    /**
     * Initialize the module.
     *
     * @param context the module context to use for the initialization.
     */
    @Override
    public void init(FloodlightModuleContext context)
	throws FloodlightModuleException {
	floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);

	// Get the configuration file name and configure the Datagrid
	Map<String, String> configMap = context.getConfigParams(this);
	String configFilename = configMap.get(HazelcastConfigFile);
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
    }

    /**
     * Register Flow Service for receiving Flow-related notifications.
     *
     * NOTE: Only a single Flow Service can be registered.
     *
     * @param flowService the Flow Service to register.
     */
    @Override
    public void registerFlowService(IFlowService flowService) {
	this.flowService = flowService;
	mapFlowListener = new MapFlowListener();
	mapFlow = hazelcastInstance.getMap(mapFlowName);
	mapFlowListenerId = mapFlow.addEntryListener(mapFlowListener, true);
    }

    /**
     * De-register Flow Service for receiving Flow-related notifications.
     *
     * NOTE: Only a single Flow Service can be registered.
     *
     * @param flowService the Flow Service to de-register.
     */
    @Override
    public void deregisterFlowService(IFlowService flowService) {
	mapFlow.removeEntryListener(mapFlowListenerId);
	mapFlow = null;
	mapFlowListener = null;
	this.flowService = null;
    }

    /**
     * Get all Flows that are currently in the datagrid.
     *
     * @return all Flows that are currently in the datagrid.
     */
    @Override
    public Collection<FlowPath> getAllFlows() {
	Collection<FlowPath> allFlows = new LinkedList<FlowPath>();

	//
	// Get all current entries
	//
	Collection<byte[]> values = mapFlow.values();
	Kryo kryo = kryoFactory.newKryo();
	for (byte[] valueBytes : values) {
	    //
	    // Decode the value
	    //
	    Input input = new Input(valueBytes);
	    FlowPath flowPath = kryo.readObject(input, FlowPath.class);
	    allFlows.add(flowPath);
	}
	kryoFactory.deleteKryo(kryo);

	return allFlows;
    }

    /**
     * Send a notification that a Flow is added.
     *
     * @param flowPath the flow that is added.
     */
    @Override
    public void notificationSendFlowAdded(FlowPath flowPath) {
	//
	// Encode the value
	//
	byte[] buffer = new byte[MAX_BUFFER_SIZE];
	Kryo kryo = kryoFactory.newKryo();
	Output output = new Output(buffer, -1);
	kryo.writeObject(output, flowPath);
	byte[] valueBytes = output.toBytes();
	kryoFactory.deleteKryo(kryo);

	//
	// Put the entry:
	//  - Key : Flow ID (Long)
	//  - Value : Serialized Flow (byte[])
	//
	mapFlow.putAsync(flowPath.flowId().value(), valueBytes);
    }

    /**
     * Send a notification that a Flow is removed.
     *
     * @param flowId the Flow ID of the flow that is removed.
     */
    @Override
    public void notificationSendFlowRemoved(FlowId flowId) {
	//
	// Remove the entry:
	//  - Key : Flow ID (Long)
	//  - Value : Serialized Flow (byte[])
	//
	mapFlow.removeAsync(flowId.value());
    }

    /**
     * Send a notification that a Flow is updated.
     *
     * @param flowPath the flow that is updated.
     */
    @Override
    public void notificationSendFlowUpdated(FlowPath flowPath) {
	// NOTE: Adding an entry with an existing key automatically updates it
	notificationSendFlowAdded(flowPath);
    }

    /**
     * Send a notification that all Flows are removed.
     */
    @Override
    public void notificationSendAllFlowsRemoved() {
	//
	// Remove all entries
	// NOTE: We remove the entries one-by-one so the per-entry
	// notifications will be delivered.
	//
	// mapFlow.clear();
	Set<Long> keySet = mapFlow.keySet();
	for (Long key : keySet) {
	    mapFlow.removeAsync(key);
	}
    }
}
