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
import net.onrc.onos.ofcontroller.flowmanager.IFlowEventHandlerService;
import net.onrc.onos.ofcontroller.flowmanager.PerformanceMonitor.Measurement;
import net.onrc.onos.ofcontroller.proxyarp.ArpMessage;
import net.onrc.onos.ofcontroller.proxyarp.IArpEventHandler;
import net.onrc.onos.ofcontroller.topology.TopologyElement;
import net.onrc.onos.ofcontroller.util.Dpid;
import net.onrc.onos.ofcontroller.util.FlowEntry;
import net.onrc.onos.ofcontroller.util.FlowEntryId;
import net.onrc.onos.ofcontroller.util.FlowId;
import net.onrc.onos.ofcontroller.util.FlowPath;
import net.onrc.onos.ofcontroller.util.Pair;
import net.onrc.onos.ofcontroller.util.serializers.KryoFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo2.Kryo;
import com.esotericsoftware.kryo2.io.Input;
import com.esotericsoftware.kryo2.io.Output;
import com.hazelcast.config.Config;
import com.hazelcast.config.FileSystemXmlConfig;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.instance.GroupProperties;

import net.onrc.onos.ofcontroller.flowmanager.PerformanceMonitor;

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

    // State related to the Flow ID map
    protected static final String mapFlowIdName = "mapFlowId";
    private IMap<Long, byte[]> mapFlowId = null;
    private MapFlowIdListener mapFlowIdListener = null;
    private String mapFlowIdListenerId = null;

    // State related to the Flow Entry ID map
    protected static final String mapFlowEntryIdName = "mapFlowEntryId";
    private IMap<Long, byte[]> mapFlowEntryId = null;
    private MapFlowEntryIdListener mapFlowEntryIdListener = null;
    private String mapFlowEntryIdListenerId = null;

    // State related to the Network Topology map
    protected static final String mapTopologyName = "mapTopology";
    private IMap<String, byte[]> mapTopology = null;
    private MapTopologyListener mapTopologyListener = null;
    private String mapTopologyListenerId = null;

    // State related to the ARP map
    protected static final String arpMapName = "arpMap";
    private IMap<ArpMessage, byte[]> arpMap = null;
    private List<IArpEventHandler> arpEventHandlers = new ArrayList<IArpEventHandler>();
    private final byte[] dummyByte = {0};

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
	@Override
	public void entryAdded(EntryEvent<Long, byte[]> event) {
	    byte[] valueBytes = event.getValue();

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
	@Override
	public void entryRemoved(EntryEvent<Long, byte[]> event) {
	    byte[] valueBytes = event.getValue();

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
	@Override
	public void entryUpdated(EntryEvent<Long, byte[]> event) {
	    byte[] valueBytes = event.getValue();

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
	@Override
	public void entryEvicted(EntryEvent<Long, byte[]> event) {
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
	@Override
	public void entryAdded(EntryEvent<Long, byte[]> event) {
	    byte[] valueBytes = event.getValue();

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
	@Override
	public void entryRemoved(EntryEvent<Long, byte[]> event) {
	    byte[] valueBytes = event.getValue();

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
	@Override
	public void entryUpdated(EntryEvent<Long, byte[]> event) {
	    byte[] valueBytes = event.getValue();

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
	@Override
	public void entryEvicted(EntryEvent<Long, byte[]> event) {
	    // NOTE: We don't use eviction for this map
	}
    }

    /**
     * Class for receiving notifications for FlowId state.
     *
     * The datagrid map is:
     *  - Key : FlowId (Long)
     *  - Value : Serialized Switch Dpid (byte[])
     */
    class MapFlowIdListener implements EntryListener<Long, byte[]> {
	/**
	 * Receive a notification that an entry is added.
	 *
	 * @param event the notification event for the entry.
	 */
	public void entryAdded(EntryEvent<Long, byte[]> event) {
	    Long keyLong = event.getKey();
	    FlowId flowId = new FlowId(keyLong);

	    byte[] valueBytes = event.getValue();

	    //
	    // Decode the value and deliver the notification
	    //
	    Kryo kryo = kryoFactory.newKryo();
	    Input input = new Input(valueBytes);
	    Dpid dpid = kryo.readObject(input, Dpid.class);
	    kryoFactory.deleteKryo(kryo);
	    flowEventHandlerService.notificationRecvFlowIdAdded(flowId, dpid);
	}

	/**
	 * Receive a notification that an entry is removed.
	 *
	 * @param event the notification event for the entry.
	 */
	public void entryRemoved(EntryEvent<Long, byte[]> event) {
	    Long keyLong = event.getKey();
	    FlowId flowId = new FlowId(keyLong);

	    byte[] valueBytes = event.getValue();

	    //
	    // Decode the value and deliver the notification
	    //
	    Kryo kryo = kryoFactory.newKryo();
	    Input input = new Input(valueBytes);
	    Dpid dpid = kryo.readObject(input, Dpid.class);
	    kryoFactory.deleteKryo(kryo);
	    flowEventHandlerService.notificationRecvFlowIdRemoved(flowId, dpid);
	}

	/**
	 * Receive a notification that an entry is updated.
	 *
	 * @param event the notification event for the entry.
	 */
	public void entryUpdated(EntryEvent<Long, byte[]> event) {
	    Long keyLong = event.getKey();
	    FlowId flowId = new FlowId(keyLong);

	    byte[] valueBytes = event.getValue();

	    //
	    // Decode the value and deliver the notification
	    //
	    Kryo kryo = kryoFactory.newKryo();
	    Input input = new Input(valueBytes);
	    Dpid dpid = kryo.readObject(input, Dpid.class);
	    kryoFactory.deleteKryo(kryo);
	    flowEventHandlerService.notificationRecvFlowIdUpdated(flowId, dpid);
	}

	/**
	 * Receive a notification that an entry is evicted.
	 *
	 * @param event the notification event for the entry.
	 */
	public void entryEvicted(EntryEvent<Long, byte[]> event) {
	    // NOTE: We don't use eviction for this map
	}
    }

    /**
     * Class for receiving notifications for FlowEntryId state.
     *
     * The datagrid map is:
     *  - Key : FlowEntryId (Long)
     *  - Value : Serialized Switch Dpid (byte[])
     */
    class MapFlowEntryIdListener implements EntryListener<Long, byte[]> {
	/**
	 * Receive a notification that an entry is added.
	 *
	 * @param event the notification event for the entry.
	 */
	public void entryAdded(EntryEvent<Long, byte[]> event) {
	    Long keyLong = event.getKey();
	    FlowEntryId flowEntryId = new FlowEntryId(keyLong);

	    byte[] valueBytes = event.getValue();

	    //
	    // Decode the value and deliver the notification
	    //
	    Kryo kryo = kryoFactory.newKryo();
	    Input input = new Input(valueBytes);
	    Dpid dpid = kryo.readObject(input, Dpid.class);
	    kryoFactory.deleteKryo(kryo);
	    flowEventHandlerService.notificationRecvFlowEntryIdAdded(flowEntryId, dpid);
	}

	/**
	 * Receive a notification that an entry is removed.
	 *
	 * @param event the notification event for the entry.
	 */
	public void entryRemoved(EntryEvent<Long, byte[]> event) {
	    Long keyLong = event.getKey();
	    FlowEntryId flowEntryId = new FlowEntryId(keyLong);

	    byte[] valueBytes = event.getValue();

	    //
	    // Decode the value and deliver the notification
	    //
	    Kryo kryo = kryoFactory.newKryo();
	    Input input = new Input(valueBytes);
	    Dpid dpid = kryo.readObject(input, Dpid.class);
	    kryoFactory.deleteKryo(kryo);
	    flowEventHandlerService.notificationRecvFlowEntryIdRemoved(flowEntryId, dpid);
	}

	/**
	 * Receive a notification that an entry is updated.
	 *
	 * @param event the notification event for the entry.
	 */
	public void entryUpdated(EntryEvent<Long, byte[]> event) {
	    Long keyLong = event.getKey();
	    FlowEntryId flowEntryId = new FlowEntryId(keyLong);

	    byte[] valueBytes = event.getValue();

	    //
	    // Decode the value and deliver the notification
	    //
	    Kryo kryo = kryoFactory.newKryo();
	    Input input = new Input(valueBytes);
	    Dpid dpid = kryo.readObject(input, Dpid.class);
	    kryoFactory.deleteKryo(kryo);
	    flowEventHandlerService.notificationRecvFlowEntryIdUpdated(flowEntryId, dpid);
	}

	/**
	 * Receive a notification that an entry is evicted.
	 *
	 * @param event the notification event for the entry.
	 */
	public void entryEvicted(EntryEvent<Long, byte[]> event) {
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
	@Override
	public void entryAdded(EntryEvent<String, byte[]> event) {
	    byte[] valueBytes = event.getValue();

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
	@Override
	public void entryRemoved(EntryEvent<String, byte[]> event) {
//	    String tag = "TopologyEntryRemoved.NotificationReceived." + event.getKey();
	    String tag = "TopologyEntryRemoved.NotificationReceived";
	    PerformanceMonitor.Measurement m = PerformanceMonitor.start(tag);
	    byte[] valueBytes = event.getValue();

	    //
	    // Decode the value and deliver the notification
	    //
	    Kryo kryo = kryoFactory.newKryo();
	    Input input = new Input(valueBytes);
	    TopologyElement topologyElement =
		kryo.readObject(input, TopologyElement.class);
	    kryoFactory.deleteKryo(kryo);
	    flowEventHandlerService.notificationRecvTopologyElementRemoved(topologyElement);
//	    PerformanceMonitor.stop(tag);
	    m.stop();
//	    PerformanceMonitor.report(tag);
	}

	/**
	 * Receive a notification that an entry is updated.
	 *
	 * @param event the notification event for the entry.
	 */
	@Override
	public void entryUpdated(EntryEvent<String, byte[]> event) {
	    byte[] valueBytes = event.getValue();

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
	@Override
	public void entryEvicted(EntryEvent<String, byte[]> event) {
	    // NOTE: We don't use eviction for this map
	}
    }

    /**
     * Class for receiving notifications for ARP requests.
     *
     * The datagrid map is:
     *  - Key: Request ID (String)
     *  - Value: ARP request packet (byte[])
     */
    class ArpMapListener implements EntryListener<ArpMessage, byte[]> {
		/**
		 * Receive a notification that an entry is added.
		 *
		 * @param event the notification event for the entry.
		 */
		@Override
		public void entryAdded(EntryEvent<ArpMessage, byte[]> event) {
		    for (IArpEventHandler arpEventHandler : arpEventHandlers) {
		    	arpEventHandler.arpRequestNotification(event.getKey());
		    }

		    //
		    // Decode the value and deliver the notification
		    //
		    /*
		    Kryo kryo = kryoFactory.newKryo();
		    Input input = new Input(valueBytes);
		    TopologyElement topologyElement =
			kryo.readObject(input, TopologyElement.class);
		    kryoFactory.deleteKryo(kryo);
		    flowEventHandlerService.notificationRecvTopologyElementAdded(topologyElement);
		    */
		}

		/**
		 * Receive a notification that an entry is removed.
		 *
		 * @param event the notification event for the entry.
		 */
		@Override
		public void entryRemoved(EntryEvent<ArpMessage, byte[]> event) {
			// Not used
		}

		/**
		 * Receive a notification that an entry is updated.
		 *
		 * @param event the notification event for the entry.
		 */
		@Override
		public void entryUpdated(EntryEvent<ArpMessage, byte[]> event) {
			// Not used
		}

		/**
		 * Receive a notification that an entry is evicted.
		 *
		 * @param event the notification event for the entry.
		 */
		@Override
		public void entryEvicted(EntryEvent<ArpMessage, byte[]> event) {
		    // Not used
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

	arpMap = hazelcastInstance.getMap(arpMapName);
	arpMap.addEntryListener(new ArpMapListener(), true);
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

	// Initialize the FlowId-related map state
	mapFlowIdListener = new MapFlowIdListener();
	mapFlowId = hazelcastInstance.getMap(mapFlowIdName);
	mapFlowIdListenerId = mapFlowId.addEntryListener(mapFlowIdListener, true);

	// Initialize the FlowEntryId-related map state
	mapFlowEntryIdListener = new MapFlowEntryIdListener();
	mapFlowEntryId = hazelcastInstance.getMap(mapFlowEntryIdName);
	mapFlowEntryIdListenerId = mapFlowEntryId.addEntryListener(mapFlowEntryIdListener, true);

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

	// Clear the FlowId-related map state
	mapFlowId.removeEntryListener(mapFlowIdListenerId);
	mapFlowId = null;
	mapFlowIdListener = null;

	// Clear the FlowEntryId-related map state
	mapFlowEntryId.removeEntryListener(mapFlowEntryIdListenerId);
	mapFlowEntryId = null;
	mapFlowEntryIdListener = null;

	// Clear the Topology-related map state
	mapTopology.removeEntryListener(mapTopologyListenerId);
	mapTopology = null;
	mapTopologyListener = null;

	this.flowEventHandlerService = null;
    }

    @Override
    public void registerArpEventHandler(IArpEventHandler arpEventHandler) {
    	if (arpEventHandler != null) {
    		arpEventHandlers.add(arpEventHandler);
    	}
    }

    @Override
    public void deregisterArpEventHandler(IArpEventHandler arpEventHandler) {
    	arpEventHandlers.remove(arpEventHandler);
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
     * Get a Flow for a given Flow ID.
     *
     * @param flowId the Flow ID of the Flow to get.
     * @return the Flow if found, otherwise null.
     */
    @Override
    public FlowPath getFlow(FlowId flowId) {
	byte[] valueBytes = mapFlow.get(flowId.value());
	if (valueBytes == null)
	    return null;

	Kryo kryo = kryoFactory.newKryo();
	//
	// Decode the value
	//
	Input input = new Input(valueBytes);
	FlowPath flowPath = kryo.readObject(input, FlowPath.class);
	kryoFactory.deleteKryo(kryo);

	return flowPath;
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
     * Get a Flow Entry for a given Flow Entry ID.
     *
     * @param flowEntryId the Flow Entry ID of the Flow Entry to get.
     * @return the Flow Entry if found, otherwise null.
     */
    @Override
    public FlowEntry getFlowEntry(FlowEntryId flowEntryId) {
	byte[] valueBytes = mapFlowEntry.get(flowEntryId.value());
	if (valueBytes == null)
	    return null;

	Kryo kryo = kryoFactory.newKryo();
	//
	// Decode the value
	//
	Input input = new Input(valueBytes);
	FlowEntry flowEntry = kryo.readObject(input, FlowEntry.class);
	kryoFactory.deleteKryo(kryo);

	return flowEntry;
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
     * Get all Flow IDs that are currently in the datagrid.
     *
     * @return all Flow IDs that are currently in the datagrid.
     */
    @Override
	public Collection<Pair<FlowId, Dpid>> getAllFlowIds() {
	Collection<Pair<FlowId, Dpid>> allFlowIds =
	    new LinkedList<Pair<FlowId, Dpid>>();

	//
	// Get all current entries
	//
	Kryo kryo = kryoFactory.newKryo();
	for (Map.Entry<Long, byte[]> entry : mapFlowId.entrySet()) {
	    Long key = entry.getKey();
	    byte[] valueBytes = entry.getValue();

	    FlowId flowId = new FlowId(key);

	    //
	    // Decode the value
	    //
	    Input input = new Input(valueBytes);
	    Dpid dpid = kryo.readObject(input, Dpid.class);

	    Pair<FlowId, Dpid> pair = new Pair(flowId, dpid);
	    allFlowIds.add(pair);
	}
	kryoFactory.deleteKryo(kryo);

	return allFlowIds;
    }

    /**
     * Get all Flow Entry IDs that are currently in the datagrid.
     *
     * @return all Flow Entry IDs that ae currently in the datagrid.
     */
    @Override
    public Collection<Pair<FlowEntryId, Dpid>> getAllFlowEntryIds() {
	Collection<Pair<FlowEntryId, Dpid>> allFlowEntryIds =
	    new LinkedList<Pair<FlowEntryId, Dpid>>();

	//
	// Get all current entries
	//
	Kryo kryo = kryoFactory.newKryo();
	for (Map.Entry<Long, byte[]> entry : mapFlowEntryId.entrySet()) {
	    Long key = entry.getKey();
	    byte[] valueBytes = entry.getValue();

	    FlowEntryId flowEntryId = new FlowEntryId(key);

	    //
	    // Decode the value
	    //
	    Input input = new Input(valueBytes);
	    Dpid dpid = kryo.readObject(input, Dpid.class);

	    Pair<FlowEntryId, Dpid> pair = new Pair(flowEntryId, dpid);
	    allFlowEntryIds.add(pair);
	}
	kryoFactory.deleteKryo(kryo);

	return allFlowEntryIds;
    }

    /**
     * Send a notification that a FlowId is added.
     *
     * @param flowId the FlowId that is added.
     * @param dpid the Source Switch Dpid.
     */
    @Override
    public void notificationSendFlowIdAdded(FlowId flowId, Dpid dpid) {
	//
	// Encode the value
	//
	byte[] buffer = new byte[MAX_BUFFER_SIZE];
	Kryo kryo = kryoFactory.newKryo();
	Output output = new Output(buffer, -1);
	kryo.writeObject(output, dpid);
	byte[] valueBytes = output.toBytes();
	kryoFactory.deleteKryo(kryo);

	//
	// Put the entry:
	//  - Key : FlowId (Long)
	//  - Value : Serialized Switch Dpid (byte[])
	//
	mapFlowId.putAsync(flowId.value(), valueBytes);
    }

    /**
     * Send a notification that a FlowId is removed.
     *
     * @param flowId the FlowId that is removed.
     */
    @Override
    public void notificationSendFlowIdRemoved(FlowId flowId) {
	//
	// Remove the entry:
	//  - Key : FlowId (Long)
	//  - Value : Serialized Switch Dpid (byte[])
	//
	mapFlowId.removeAsync(flowId.value());
    }

    /**
     * Send a notification that a FlowId is updated.
     *
     * @param flowId the FlowId that is updated.
     * @param dpid the Source Switch Dpid.
     */
    @Override
    public void notificationSendFlowIdUpdated(FlowId flowId, Dpid dpid) {
	// NOTE: Adding an entry with an existing key automatically updates it
	notificationSendFlowIdAdded(flowId, dpid);
    }

    /**
     * Send a notification that all Flow IDs are removed.
     */
    @Override
    public void notificationSendAllFlowIdsRemoved() {
	//
	// Remove all entries
	// NOTE: We remove the entries one-by-one so the per-entry
	// notifications will be delivered.
	//
	// mapFlowId.clear();
	Set<Long> keySet = mapFlowId.keySet();
	for (Long key : keySet) {
	    mapFlowId.removeAsync(key);
	}
    }

    /**
     * Send a notification that a FlowEntryId is added.
     *
     * @param flowEntryId the FlowEntryId that is added.
     * @param dpid the Switch Dpid.
     */
    @Override
    public void notificationSendFlowEntryIdAdded(FlowEntryId flowEntryId,
						 Dpid dpid) {
	//
	// Encode the value
	//
	byte[] buffer = new byte[MAX_BUFFER_SIZE];
	Kryo kryo = kryoFactory.newKryo();
	Output output = new Output(buffer, -1);
	kryo.writeObject(output, dpid);
	byte[] valueBytes = output.toBytes();
	kryoFactory.deleteKryo(kryo);

	//
	// Put the entry:
	//  - Key : FlowEntryId (Long)
	//  - Value : Serialized Switch Dpid (byte[])
	//
	mapFlowEntryId.putAsync(flowEntryId.value(), valueBytes);
    }

    /**
     * Send a notification that a FlowEntryId is removed.
     *
     * @param flowEntryId the FlowEntryId that is removed.
     */
    @Override
    public void notificationSendFlowEntryIdRemoved(FlowEntryId flowEntryId) {
	//
	// Remove the entry:
	//  - Key : FlowEntryId (Long)
	//  - Value : Serialized Switch Dpid (byte[])
	//
	mapFlowEntryId.removeAsync(flowEntryId.value());
    }

    /**
     * Send a notification that a FlowEntryId is updated.
     *
     * @param flowEntryId the FlowEntryId that is updated.
     * @param dpid the Switch Dpid.
     */
    @Override
    public void notificationSendFlowEntryIdUpdated(FlowEntryId flowEntryId,
						   Dpid dpid) {
	// NOTE: Adding an entry with an existing key automatically updates it
	notificationSendFlowEntryIdAdded(flowEntryId, dpid);
    }

    /**
     * Send a notification that all Flow Entry IDs are removed.
     */
    @Override
    public void notificationSendAllFlowEntryIdsRemoved() {
	//
	// Remove all entries
	// NOTE: We remove the entries one-by-one so the per-entry
	// notifications will be delivered.
	//
	// mapFlowEntryId.clear();
	Set<Long> keySet = mapFlowEntryId.keySet();
	for (Long key : keySet) {
	    mapFlowEntryId.removeAsync(key);
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

    @Override
    public void sendArpRequest(ArpMessage arpMessage) {
    	//log.debug("ARP bytes: {}", HexString.toHexString(arpRequest));
     	arpMap.putAsync(arpMessage, dummyByte, 1L, TimeUnit.MILLISECONDS);
    }
}
