package net.onrc.onos.ofcontroller.networkgraph;

import net.floodlightcontroller.core.module.IFloodlightService;

/**
 * Interface for providing the Network Graph Service to other modules.
 */
public interface INetworkGraphService extends IFloodlightService {
	/**
	 * Allows a module to get a reference to the global network graph object.
	 * @return
	 */
    public NetworkGraph getNetworkGraph();
    
    /**
     * Allows a module to get a reference to the southbound interface to
     * the network graph.
     * TODO Figure out how to hide the southbound interface from 
     * applications/modules that shouldn't touch it
     * @return
     */
    public NetworkGraphDatastore getSouthboundNetworkGraph();
}
