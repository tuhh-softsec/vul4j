package net.onrc.onos.ofcontroller.networkgraph.web;

import java.io.IOException;

import net.onrc.onos.ofcontroller.networkgraph.INetworkGraphService;
import net.onrc.onos.ofcontroller.networkgraph.NetworkGraph;
import net.onrc.onos.ofcontroller.networkgraph.serializers.PortSerializer;
import net.onrc.onos.ofcontroller.networkgraph.serializers.SwitchSerializer;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkGraphSwitchesResource extends ServerResource {
	
	private static final Logger log = LoggerFactory.getLogger(NetworkGraphSwitchesResource.class);

	@Get("json")
	public String retrieve() {
		INetworkGraphService networkGraphService = (INetworkGraphService) getContext().getAttributes().
				get(INetworkGraphService.class.getCanonicalName());
		
		NetworkGraph graph = networkGraphService.getNetworkGraph();
		
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule("module", new Version(1, 0, 0, null));
		module.addSerializer(new SwitchSerializer());
		module.addSerializer(new PortSerializer());
		mapper.registerModule(module);
		
		try {
			return mapper.writeValueAsString(graph.getSwitches());
		} catch (IOException e) {
			log.error("Error writing switch list to JSON", e);
			return "";
		}
	}

}
