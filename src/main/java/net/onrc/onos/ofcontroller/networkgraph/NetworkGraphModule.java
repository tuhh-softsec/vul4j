package net.onrc.onos.ofcontroller.networkgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.restserver.IRestApiService;
import net.onrc.onos.ofcontroller.networkgraph.web.NetworkGraphWebRoutable;

public class NetworkGraphModule implements IFloodlightModule, INetworkGraphService {

	// This is initialized as a module for now
	// private RCNetworkGraphPublisher eventListener;
	
	private NetworkGraphImpl networkGraph;
	private SouthboundNetworkGraph southboundNetworkGraph;

	private IRestApiService restApi;

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		List<Class<? extends IFloodlightService>> services = 
				new ArrayList<Class<? extends IFloodlightService>>();
		services.add(INetworkGraphService.class);
		return services;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> 
			getServiceImpls() {
		Map<Class<? extends IFloodlightService>, IFloodlightService> impls = 
				new HashMap<Class<? extends IFloodlightService>, IFloodlightService>();
		impls.put(INetworkGraphService.class, this);
		return impls;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		List<Class<? extends IFloodlightService>> dependencies = 
				new ArrayList<Class<? extends IFloodlightService>>();
		dependencies.add(IRestApiService.class);
		return dependencies;
	}
	
	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException {
		restApi = context.getServiceImpl(IRestApiService.class);
		
		networkGraph = new NetworkGraphImpl();
		southboundNetworkGraph = new SouthboundNetworkGraph(networkGraph);
	}

	@Override
	public void startUp(FloodlightModuleContext context) {
		restApi.addRestletRoutable(new NetworkGraphWebRoutable());
	}

	@Override
	public NetworkGraph getNetworkGraph() {
		return networkGraph;
	}

	@Override
	public SouthboundNetworkGraph getSouthboundNetworkGraph() {
		return southboundNetworkGraph;
	}

}
