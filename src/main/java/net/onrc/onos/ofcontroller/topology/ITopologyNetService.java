package net.onrc.onos.ofcontroller.topology;

import java.util.Map;

import net.floodlightcontroller.core.module.IFloodlightService;
import net.onrc.onos.ofcontroller.util.DataPath;
import net.onrc.onos.ofcontroller.util.SwitchPort;

/**
 * Interface for providing Topology Network Service to other modules.
 */
public interface ITopologyNetService extends IFloodlightService {
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
    Topology newDatabaseTopology();

    /**
     * Release the topology that was populated by
     * method @ref newDatabaseTopology().
     *
     * See the documentation for method @ref newDatabaseTopology()
     * for additional information and usage.
     *
     * @param topology the topology to release.
     */
    void dropTopology(Topology topology);

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
    DataPath getTopologyShortestPath(Topology topology,
				     SwitchPort src, SwitchPort dest);

    /**
     * Get the shortest path from a source to a destination by using
     * the underlying database.
     *
     * @param src the source in the shortest path computation.
     * @param dest the destination in the shortest path computation.
     * @return the data path with the computed shortest path if
     * found, otherwise null.
     */
    DataPath getDatabaseShortestPath(SwitchPort src, SwitchPort dest);

    /**
     * Test whether a route exists from a source to a destination.
     *
     * @param src the source node for the test.
     * @param dest the destination node for the test.
     * @return true if a route exists, otherwise false.
     */
    Boolean routeExists(SwitchPort src, SwitchPort dest);
}
