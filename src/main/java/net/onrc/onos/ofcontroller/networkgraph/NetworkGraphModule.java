package net.onrc.onos.ofcontroller.networkgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.restserver.IRestApiService;
import net.onrc.onos.datagrid.IDatagridService;
import net.onrc.onos.ofcontroller.networkgraph.web.NetworkGraphWebRoutable;
import net.onrc.onos.registry.controller.IControllerRegistryService;

public class NetworkGraphModule implements IFloodlightModule, INetworkGraphService {

	// This is initialized as a module for now
	// private RCNetworkGraphPublisher eventListener;

	private TopologyManager networkGraph;
	//private NetworkGraphDatastore southboundNetworkGraph;
	private IDatagridService datagridService;
	private IControllerRegistryService registryService;

	private CopyOnWriteArrayList<INetworkGraphListener> networkGraphListeners;

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
		dependencies.add(IDatagridService.class);
		dependencies.add(IRestApiService.class);
		return dependencies;
	}

	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException {
		restApi = context.getServiceImpl(IRestApiService.class);
		datagridService = context.getServiceImpl(IDatagridService.class);
		registryService = context.getServiceImpl(IControllerRegistryService.class);

		networkGraphListeners = new CopyOnWriteArrayList<>();
		networkGraph = new TopologyManager(registryService, networkGraphListeners);
		//southboundNetworkGraph = new NetworkGraphDatastore(networkGraph);
	}

	@Override
	public void startUp(FloodlightModuleContext context) {
		restApi.addRestletRoutable(new NetworkGraphWebRoutable());
		networkGraph.startup(datagridService);
	}

	@Override
	public NetworkGraph getNetworkGraph() {
		return networkGraph;
	}

	@Override
	public NetworkGraphDiscoveryInterface getNetworkGraphDiscoveryInterface() {
		return networkGraph;
	}

	@Override
	public void registerNetworkGraphListener(INetworkGraphListener listener) {
	    networkGraphListeners.addIfAbsent(listener);
	}

	@Override
	public void deregisterNetworkGraphListener(INetworkGraphListener listener) {
	    networkGraphListeners.remove(listener);
	}

}
