package net.onrc.onos.datagrid;

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
    protected final static Logger log = LoggerFactory.getLogger(HazelcastDatagrid.class);
    protected IFloodlightProviderService floodlightProvider;

    protected static final String HazelcastConfigFile = "datagridConfig";
    private HazelcastInstance hazelcast = null;
    private Config hazelcastConfig = null;

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
	hazelcast = Hazelcast.newHazelcastInstance(hazelcastConfig);
    }
}
