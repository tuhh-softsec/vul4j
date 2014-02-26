package net.onrc.onos.ofcontroller.networkgraph.web;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import net.onrc.onos.intent.ConstrainedBFSTree;
import net.onrc.onos.ofcontroller.networkgraph.INetworkGraphService;
import net.onrc.onos.ofcontroller.networkgraph.Link;
import net.onrc.onos.ofcontroller.networkgraph.LinkEvent;
import net.onrc.onos.ofcontroller.networkgraph.NetworkGraph;
import net.onrc.onos.ofcontroller.networkgraph.Path;
import net.onrc.onos.ofcontroller.networkgraph.Switch;
import net.onrc.onos.ofcontroller.networkgraph.serializers.LinkSerializer;
import net.onrc.onos.ofcontroller.util.Dpid;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkGraphShortestPathResource extends ServerResource {

    private static final Logger log = LoggerFactory.getLogger(NetworkGraphShortestPathResource.class);

    @Get("json")
    public String retrieve() {
	INetworkGraphService networkGraphService =
	    (INetworkGraphService)getContext().getAttributes().
	    get(INetworkGraphService.class.getCanonicalName());

	NetworkGraph graph = networkGraphService.getNetworkGraph();

	ObjectMapper mapper = new ObjectMapper();
	SimpleModule module = new SimpleModule("module", new Version(1, 0, 0, null));
	module.addSerializer(new LinkSerializer());
	mapper.registerModule(module);

	//
	// Fetch the attributes
	//
	String srcDpidStr = (String)getRequestAttributes().get("src-dpid");
	String dstDpidStr = (String)getRequestAttributes().get("dst-dpid");
	Dpid srcDpid = new Dpid(srcDpidStr);
	Dpid dstDpid = new Dpid(dstDpidStr);
	log.debug("Getting Shortest Path {}--{}", srcDpidStr, dstDpidStr);

	//
	// Do the Shortest Path computation and return the result: list of
	// links.
	//
	try {
	    graph.acquireReadLock();
	    Switch srcSwitch = graph.getSwitch(srcDpid.value());
	    Switch dstSwitch = graph.getSwitch(dstDpid.value());
	    if ((srcSwitch == null) || (dstSwitch == null))
		return "";
	    ConstrainedBFSTree bfsTree = new ConstrainedBFSTree(srcSwitch);
	    Path path = bfsTree.getPath(dstSwitch);
	    List<Link> links = new LinkedList<>();
	    for (LinkEvent linkEvent : path) {
		Link link = graph.getLink(linkEvent.getSrc().getDpid(),
					  linkEvent.getSrc().getNumber(),
					  linkEvent.getDst().getDpid(),
					  linkEvent.getDst().getNumber());
		if (link == null)
		    return "";
		links.add(link);
	    }
	    return mapper.writeValueAsString(links);
	} catch (IOException e) {
	    log.error("Error writing Shortest Path to JSON", e);
	    return "";
	} finally {
	    graph.releaseReadLock();
	}
    }
}
