package net.onrc.onos.ofcontroller.topology;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;

import net.onrc.onos.datagrid.IDatagridService;
import net.onrc.onos.graph.GraphDBOperation;
import net.onrc.onos.ofcontroller.floodlightlistener.INetworkGraphService;
import net.onrc.onos.ofcontroller.util.DataPath;
import net.onrc.onos.ofcontroller.util.SwitchPort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class for implementing Topology Network Service.
 */
public class TopologyManager implements IFloodlightModule,
					ITopologyNetService {
    private final static Logger log = LoggerFactory.getLogger(TopologyManager.class);
    protected IFloodlightProviderService floodlightProvider;

    protected GraphDBOperation dbHandler;


    /**
     * Default constructor.
     */
    public TopologyManager() {
    }

    /**
     * Constructor for given database configuration file.
     *
     * @param config the database configuration file to use for
     * the initialization.
     */
    public TopologyManager(String config) {
	this.init(config);
    }

    /**
     * Constructor for a given database operation handler.
     *
     * @param dbHandler the database operation handler to use for the
     * initialization.
     */
    public TopologyManager(GraphDBOperation dbHandler) {
	this.dbHandler = dbHandler;
    }

    /**
     * Init the module.
     *
     * @param config the database configuration file to use for
     * the initialization.
     */
    public void init(String config) {
	try {
	    dbHandler = new GraphDBOperation(config);
	} catch (Exception e) {
	    log.error(e.getMessage());
	}
    }

    /**
     * Shutdown the Topology Manager operation.
     */
    public void finalize() {
	close();
    }

    /**
     * Close the service. It will close the corresponding database connection.
     */
    public void close() {
	dbHandler.close();
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
        l.add(ITopologyNetService.class);
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
        m.put(ITopologyNetService.class, this);
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
	l.add(INetworkGraphService.class);
	l.add(IDatagridService.class);
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

	String conf = "";
	this.init(conf);
    }

    /**
     * Startup module operation.
     *
     * @param context the module context to use for the startup.
     */
    @Override
    public void startUp(FloodlightModuleContext context) {

    }

    /**
     * Fetch the Switch and Ports info from the Titan Graph
     * and return it for fast access during the shortest path
     * computation.
     *
     * After fetching the state, method @ref getTopologyShortestPath()
     * can be used for fast shortest path computation.
     *
     * Note: There is certain cost to fetch the state, hence it should
     * be used only when there is a large number of shortest path
     * computations that need to be done on the same topology.
     * Typically, a single call to @ref newDatabaseTopology()
     * should be followed by a large number of calls to
     * method @ref getTopologyShortestPath().
     * After the last @ref getTopologyShortestPath() call,
     * method @ref dropTopology() should be used to release
     * the internal state that is not needed anymore:
     *
     *       Topology topology = topologyManager.newDatabaseTopology();
     *       for (int i = 0; i < 10000; i++) {
     *           dataPath = topologyManager.getTopologyShortestPath(topology, ...);
     *           ...
     *        }
     *        topologyManager.dropTopology(shortestPathTopo);
     *
     * @return the allocated topology handler.
     */
    public Topology newDatabaseTopology() {
	Topology topology = new Topology();
	topology.readFromDatabase(dbHandler);

	return topology;
    }

    /**
     * Release the topology that was populated by
     * method @ref newDatabaseTopology().
     *
     * See the documentation for method @ref newDatabaseTopology()
     * for additional information and usage.
     *
     * @param topology the topology to release.
     */
    public void dropTopology(Topology topology) {
	topology = null;
    }

    /**
     * Get the shortest path from a source to a destination by
     * using the pre-populated local topology state prepared
     * by method @ref newDatabaseTopology().
     *
     * See the documentation for method @ref newDatabaseTopology()
     * for additional information and usage.
     *
     * @param topology the topology handler to use.
     * @param src the source in the shortest path computation.
     * @param dest the destination in the shortest path computation.
     * @return the data path with the computed shortest path if
     * found, otherwise null.
     */
    public DataPath getTopologyShortestPath(Topology topology,
					    SwitchPort src, SwitchPort dest) {
	return ShortestPath.getTopologyShortestPath(topology, src, dest);
    }

    /**
     * Get the shortest path from a source to a destination by using
     * the underlying database.
     *
     * @param src the source in the shortest path computation.
     * @param dest the destination in the shortest path computation.
     * @return the data path with the computed shortest path if
     * found, otherwise null.
     */
    @Override
    public DataPath getDatabaseShortestPath(SwitchPort src, SwitchPort dest) {
	return ShortestPath.getDatabaseShortestPath(dbHandler, src, dest);
    }

    /**
     * Test whether a route exists from a source to a destination.
     *
     * @param src the source node for the test.
     * @param dest the destination node for the test.
     * @return true if a route exists, otherwise false.
     */
    @Override
    public Boolean routeExists(SwitchPort src, SwitchPort dest) {
	DataPath dataPath = getDatabaseShortestPath(src, dest);
	return (dataPath != null);
    }
}
