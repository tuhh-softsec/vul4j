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
import net.floodlightcontroller.restserver.IRestApiService;

import net.onrc.onos.datagrid.web.DatagridWebRoutable;
import net.onrc.onos.ofcontroller.flowmanager.IFlowEventHandlerService;
import net.onrc.onos.ofcontroller.topology.TopologyElement;
import net.onrc.onos.ofcontroller.util.FlowEntry;
import net.onrc.onos.ofcontroller.util.FlowEntryId;
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
    protected IRestApiService restApi;

    protected static final String HazelcastConfigFile = "datagridConfig";
    private HazelcastInstance hazelcastInstance = null;
    private Config hazelcastConfig = null;

    private KryoFactory kryoFactory = new KryoFactory();
    private IFlowEventHandlerService flowEventHandlerService = null;

    // State related to the Flow map
    protected static final String mapFlowName = "mapFlow";
    private IMap<Long, byte[]> mapFlow = null;
    private MapFlowListener mapFlowListener = null;
    private String mapFlowListenerId = null;

    // State related to the Flow Entry map
    protected static final String mapFlowEntryName = "mapFlowEntry";
    private IMap<Long, byte[]> mapFlowEntry = null;
    private MapFlowEntryListener mapFlowEntryListener = null;
    private String mapFlowEntryListenerId = null;

    // State related to the Network Topology map
    protected static final String mapTopologyName = "mapTopology";
    private IMap<String, byte[]> mapTopology = null;
    private MapTopologyListener mapTopologyListener = null;
    private String mapTopologyListenerId = null;

    /**
     * Class for receiving notifications for Flow state.
     *
     * The datagrid map is:
     *  - Key : Flow ID (Long)
     *  - Value : Serialized FlowPath (byte[])
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
	    flowEventHandlerService.notificationRecvFlowAdded(flowPath);
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
	    flowEventHandlerService.notificationRecvFlowRemoved(flowPath);
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
	    flowEventHandlerService.notificationRecvFlowUpdated(flowPath);
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
     * Class for receiving notifications for FlowEntry state.
     *
     * The datagrid map is:
     *  - Key : FlowEntry ID (Long)
     *  - Value : Serialized FlowEntry (byte[])
     */
    class MapFlowEntryListener implements EntryListener<Long, byte[]> {
	/**
	 * Receive a notification that an entry is added.
	 *
	 * @param event the notification event for the entry.
	 */
	public void entryAdded(EntryEvent event) {
	    //
	    // NOTE: Ignore Flow Entries Events originated by this instance
	    //
	    if (event.getMember().localMember())
		return;

	    Long keyLong = (Long)event.getKey();
	    byte[] valueBytes = (byte[])event.getValue();

	    //
	    // Decode the value and deliver the notification
	    //
	    Kryo kryo = kryoFactory.newKryo();
	    Input input = new Input(valueBytes);
	    FlowEntry flowEntry = kryo.readObject(input, FlowEntry.class);
	    kryoFactory.deleteKryo(kryo);
	    flowEventHandlerService.notificationRecvFlowEntryAdded(flowEntry);
	}

	/**
	 * Receive a notification that an entry is removed.
	 *
	 * @param event the notification event for the entry.
	 */
	public void entryRemoved(EntryEvent event) {
	    //
	    // NOTE: Ignore Flow Entries Events originated by this instance
	    //
	    if (event.getMember().localMember())
		return;

	    Long keyLong = (Long)event.getKey();
	    byte[] valueBytes = (byte[])event.getValue();

	    //
	    // Decode the value and deliver the notification
	    //
	    Kryo kryo = kryoFactory.newKryo();
	    Input input = new Input(valueBytes);
	    FlowEntry flowEntry = kryo.readObject(input, FlowEntry.class);
	    kryoFactory.deleteKryo(kryo);
	    flowEventHandlerService.notificationRecvFlowEntryRemoved(flowEntry);
	}

	/**
	 * Receive a notification that an entry is updated.
	 *
	 * @param event the notification event for the entry.
	 */
	public void entryUpdated(EntryEvent event) {
	    //
	    // NOTE: Ignore Flow Entries Events originated by this instance
	    //
	    if (event.getMember().localMember())
		return;

	    Long keyLong = (Long)event.getKey();
	    byte[] valueBytes = (byte[])event.getValue();

	    //
	    // Decode the value and deliver the notification
	    //
	    Kryo kryo = kryoFactory.newKryo();
	    Input input = new Input(valueBytes);
	    FlowEntry flowEntry = kryo.readObject(input, FlowEntry.class);
	    kryoFactory.deleteKryo(kryo);
	    flowEventHandlerService.notificationRecvFlowEntryUpdated(flowEntry);
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
     * Class for receiving notifications for Network Topology state.
     *
     * The datagrid map is:
     *  - Key: TopologyElement ID (String)
     *  - Value: Serialized TopologyElement (byte[])
     */
    class MapTopologyListener implements EntryListener<String, byte[]> {
	/**
	 * Receive a notification that an entry is added.
	 *
	 * @param event the notification event for the entry.
	 */
	public void entryAdded(EntryEvent event) {
	    String keyString = (String)event.getKey();
	    byte[] valueBytes = (byte[])event.getValue();

	    //
	    // Decode the value and deliver the notification
	    //
	    Kryo kryo = kryoFactory.newKryo();
	    Input input = new Input(valueBytes);
	    TopologyElement topologyElement =
		kryo.readObject(input, TopologyElement.class);
	    kryoFactory.deleteKryo(kryo);
	    flowEventHandlerService.notificationRecvTopologyElementAdded(topologyElement);
	}

	/**
	 * Receive a notification that an entry is removed.
	 *
	 * @param event the notification event for the entry.
	 */
	public void entryRemoved(EntryEvent event) {
	    String keyString = (String)event.getKey();
	    byte[] valueBytes = (byte[])event.getValue();

	    //
	    // Decode the value and deliver the notification
	    //
	    Kryo kryo = kryoFactory.newKryo();
	    Input input = new Input(valueBytes);
	    TopologyElement topologyElement =
		kryo.readObject(input, TopologyElement.class);
	    kryoFactory.deleteKryo(kryo);
	    flowEventHandlerService.notificationRecvTopologyElementRemoved(topologyElement);
	}

	/**
	 * Receive a notification that an entry is updated.
	 *
	 * @param event the notification event for the entry.
	 */
	public void entryUpdated(EntryEvent event) {
	    String keyString = (String)event.getKey();
	    byte[] valueBytes = (byte[])event.getValue();

	    //
	    // Decode the value and deliver the notification
	    //
	    Kryo kryo = kryoFactory.newKryo();
	    Input input = new Input(valueBytes);
	    TopologyElement topologyElement =
		kryo.readObject(input, TopologyElement.class);
	    kryoFactory.deleteKryo(kryo);
	    flowEventHandlerService.notificationRecvTopologyElementUpdated(topologyElement);
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
	l.add(IRestApiService.class);
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
	restApi = context.getServiceImpl(IRestApiService.class);

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

	restApi.addRestletRoutable(new DatagridWebRoutable());
    }

    /**
     * Register Flow Event Handler Service for receiving Flow-related
     * notifications.
     *
     * NOTE: Only a single Flow Event Handler Service can be registered.
     *
     * @param flowEventHandlerService the Flow Event Handler Service to register.
     */
    @Override
    public void registerFlowEventHandlerService(IFlowEventHandlerService flowEventHandlerService) {
	this.flowEventHandlerService = flowEventHandlerService;

	// Initialize the Flow-related map state
	mapFlowListener = new MapFlowListener();
	mapFlow = hazelcastInstance.getMap(mapFlowName);
	mapFlowListenerId = mapFlow.addEntryListener(mapFlowListener, true);

	// Initialize the FlowEntry-related map state
	mapFlowEntryListener = new MapFlowEntryListener();
	mapFlowEntry = hazelcastInstance.getMap(mapFlowEntryName);
	mapFlowEntryListenerId = mapFlowEntry.addEntryListener(mapFlowEntryListener, true);

	// Initialize the Topology-related map state
	mapTopologyListener = new MapTopologyListener();
	mapTopology = hazelcastInstance.getMap(mapTopologyName);
	mapTopologyListenerId = mapTopology.addEntryListener(mapTopologyListener, true);
    }

    /**
     * De-register Flow Event Handler Service for receiving Flow-related
     * notifications.
     *
     * NOTE: Only a single Flow Event Handler Service can be registered.
     *
     * @param flowEventHandlerService the Flow Event Handler Service to
     * de-register.
     */
    @Override
    public void deregisterFlowEventHandlerService(IFlowEventHandlerService flowEventHandlerService) {
	// Clear the Flow-related map state
	mapFlow.removeEntryListener(mapFlowListenerId);
	mapFlow = null;
	mapFlowListener = null;

	// Clear the FlowEntry-related map state
	mapFlowEntry.removeEntryListener(mapFlowEntryListenerId);
	mapFlowEntry = null;
	mapFlowEntryListener = null;

	// Clear the Topology-related map state
	mapTopology.removeEntryListener(mapTopologyListenerId);
	mapTopology = null;
	mapTopologyListener = null;

	this.flowEventHandlerService = null;
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
     * @param flowPath the Flow that is added.
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
     * @param flowId the Flow ID of the Flow that is removed.
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
     * @param flowPath the Flow that is updated.
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

    /**
     * Get all Flow Entries that are currently in the datagrid.
     *
     * @return all Flow Entries that are currently in the datagrid.
     */
    @Override
    public Collection<FlowEntry> getAllFlowEntries() {
	Collection<FlowEntry> allFlowEntries = new LinkedList<FlowEntry>();

	//
	// Get all current entries
	//
	Collection<byte[]> values = mapFlowEntry.values();
	Kryo kryo = kryoFactory.newKryo();
	for (byte[] valueBytes : values) {
	    //
	    // Decode the value
	    //
	    Input input = new Input(valueBytes);
	    FlowEntry flowEntry = kryo.readObject(input, FlowEntry.class);
	    allFlowEntries.add(flowEntry);
	}
	kryoFactory.deleteKryo(kryo);

	return allFlowEntries;
    }

    /**
     * Send a notification that a FlowEntry is added.
     *
     * @param flowEntry the FlowEntry that is added.
     */
    @Override
    public void notificationSendFlowEntryAdded(FlowEntry flowEntry) {
	//
	// Encode the value
	//
	byte[] buffer = new byte[MAX_BUFFER_SIZE];
	Kryo kryo = kryoFactory.newKryo();
	Output output = new Output(buffer, -1);
	kryo.writeObject(output, flowEntry);
	byte[] valueBytes = output.toBytes();
	kryoFactory.deleteKryo(kryo);

	//
	// Put the entry:
	//  - Key : FlowEntry ID (Long)
	//  - Value : Serialized FlowEntry (byte[])
	//
	mapFlowEntry.putAsync(flowEntry.flowEntryId().value(), valueBytes);
    }

    /**
     * Send a notification that a FlowEntry is removed.
     *
     * @param flowEntryId the FlowEntry ID of the FlowEntry that is removed.
     */
    @Override
    public void notificationSendFlowEntryRemoved(FlowEntryId flowEntryId) {
	//
	// Remove the entry:
	//  - Key : FlowEntry ID (Long)
	//  - Value : Serialized FlowEntry (byte[])
	//
	mapFlowEntry.removeAsync(flowEntryId.value());
    }

    /**
     * Send a notification that a FlowEntry is updated.
     *
     * @param flowEntry the FlowEntry that is updated.
     */
    @Override
    public void notificationSendFlowEntryUpdated(FlowEntry flowEntry) {
	// NOTE: Adding an entry with an existing key automatically updates it
	notificationSendFlowEntryAdded(flowEntry);
    }

    /**
     * Send a notification that all Flow Entries are removed.
     */
    @Override
    public void notificationSendAllFlowEntriesRemoved() {
	//
	// Remove all entries
	// NOTE: We remove the entries one-by-one so the per-entry
	// notifications will be delivered.
	//
	// mapFlowEntry.clear();
	Set<Long> keySet = mapFlowEntry.keySet();
	for (Long key : keySet) {
	    mapFlowEntry.removeAsync(key);
	}
    }

    /**
     * Get all Topology Elements that are currently in the datagrid.
     *
     * @return all Topology Elements that are currently in the datagrid.
     */
    @Override
    public Collection<TopologyElement> getAllTopologyElements() {
	Collection<TopologyElement> allTopologyElements =
	    new LinkedList<TopologyElement>();

	//
	// Get all current entries
	//
	Collection<byte[]> values = mapTopology.values();
	Kryo kryo = kryoFactory.newKryo();
	for (byte[] valueBytes : values) {
	    //
	    // Decode the value
	    //
	    Input input = new Input(valueBytes);
	    TopologyElement topologyElement =
		kryo.readObject(input, TopologyElement.class);
	    allTopologyElements.add(topologyElement);
	}
	kryoFactory.deleteKryo(kryo);

	return allTopologyElements;
    }

    /**
     * Send a notification that a Topology Element is added.
     *
     * @param topologyElement the Topology Element that is added.
     */
    @Override
    public void notificationSendTopologyElementAdded(TopologyElement topologyElement) {
	//
	// Encode the value
	//
	byte[] buffer = new byte[MAX_BUFFER_SIZE];
	Kryo kryo = kryoFactory.newKryo();
	Output output = new Output(buffer, -1);
	kryo.writeObject(output, topologyElement);
	byte[] valueBytes = output.toBytes();
	kryoFactory.deleteKryo(kryo);

	//
	// Put the entry:
	//  - Key : TopologyElement ID (String)
	//  - Value : Serialized TopologyElement (byte[])
	//
	mapTopology.putAsync(topologyElement.elementId(), valueBytes);
    }

    /**
     * Send a notification that a Topology Element is removed.
     *
     * @param topologyElement the Topology Element that is removed.
     */
    @Override
    public void notificationSendTopologyElementRemoved(TopologyElement topologyElement) {
	//
	// Remove the entry:
	//  - Key : TopologyElement ID (String)
	//  - Value : Serialized TopologyElement (byte[])
	//
	mapTopology.removeAsync(topologyElement.elementId());
    }

    /**
     * Send a notification that a Topology Element is updated.
     *
     * @param topologyElement the Topology Element that is updated.
     */
    @Override
    public void notificationSendTopologyElementUpdated(TopologyElement topologyElement) {
	// NOTE: Adding an entry with an existing key automatically updates it
	notificationSendTopologyElementAdded(topologyElement);
    }

    /**
     * Send a notification that all Topology Elements are removed.
     */
    @Override
    public void notificationSendAllTopologyElementsRemoved() {
	//
	// Remove all entries
	// NOTE: We remove the entries one-by-one so the per-entry
	// notifications will be delivered.
	//
	// mapTopology.clear();
	Set<String> keySet = mapTopology.keySet();
	for (String key : keySet) {
	    mapTopology.removeAsync(key);
	}
    }
}
