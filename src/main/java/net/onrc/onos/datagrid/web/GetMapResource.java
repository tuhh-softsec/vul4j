package net.onrc.onos.datagrid.web;

import java.util.Collection;

import net.onrc.onos.datagrid.IDatagridService;
import net.onrc.onos.ofcontroller.topology.TopologyElement;
import net.onrc.onos.ofcontroller.util.FlowEntry;
import net.onrc.onos.ofcontroller.util.FlowPath;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Datagrid REST API implementation: Get the state of a map.
 *
 * Valid map names:
 *  - "all"        : Get all maps
 *  - "flow"       : Get the Flows
 *  - "flow-entry" : Get the Flow Entries
 *  - "topology"   : Get the Topology
 *
 *   GET /wm/datagrid/get/map/{map-name}/json
 */
public class GetMapResource extends ServerResource {
    protected final static Logger log = LoggerFactory.getLogger(GetMapResource.class);

    /**
     * Implement the API.
     *
     * @return a string with the state of the map(s).
     */
    @Get("json")
    public String retrieve() {
	String result = "";

        IDatagridService datagridService =
                (IDatagridService)getContext().getAttributes().
                get(IDatagridService.class.getCanonicalName());

        if (datagridService == null) {
	    log.debug("ONOS Datagrid Service not found");
            return result;
	}

	// Extract the arguments
	String mapNameStr = (String)getRequestAttributes().get("map-name");

	log.debug("Get Datagrid Map: " + mapNameStr);

	//
	// Get the Flows
	//
	if (mapNameStr.equals("flow") || mapNameStr.equals("all")) {
	    Collection<FlowPath> flowPaths = datagridService.getAllFlows();
	    result += "Flows:\n";
	    for (FlowPath flowPath : flowPaths) {
		result += flowPath.toString() + "\n";
	    }
	}

	//
	// Get the Flow Entries
	//
	if (mapNameStr.equals("flow-entry") || mapNameStr.equals("all")) {
	    Collection<FlowEntry> flowEntries = datagridService.getAllFlowEntries();
	    result += "Flow Entries:\n";
	    for (FlowEntry flowEntry : flowEntries) {
		result += flowEntry.toString() + "\n";
	    }
	}

	if (mapNameStr.equals("topology") || mapNameStr.equals("all")) {
	    Collection<TopologyElement> topologyElements = datagridService.getAllTopologyElements();
	    result += "Topology:\n";
	    for (TopologyElement topologyElement : topologyElements) {
		result += topologyElements.toString() + "\n";
	    }
	}

        return result;
    }
}
