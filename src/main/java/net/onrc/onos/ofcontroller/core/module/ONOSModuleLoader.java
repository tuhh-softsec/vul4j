package net.onrc.onos.ofcontroller.core.module;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.topology.ITopologyService;
import net.onrc.onos.ofcontroller.bgproute.IConfigInfoService;
import net.onrc.onos.ofcontroller.proxyarp.IProxyArpService;
import net.onrc.onos.ofcontroller.proxyarp.ProxyArpManager;

public class ONOSModuleLoader implements IFloodlightModule {
	private IFloodlightProviderService floodlightProvider;
	private ITopologyService topology;
	private IConfigInfoService config;
	private IRestApiService restApi;

	private ProxyArpManager arpManager;
	
	public ONOSModuleLoader() {
		arpManager = new ProxyArpManager();
	}
	
	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		List<Class<? extends IFloodlightService>> services = 
				new ArrayList<Class<? extends IFloodlightService>>();
		services.add(IProxyArpService.class);
		return services;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		Map<Class<? extends IFloodlightService>, IFloodlightService> impls = 
				new HashMap<Class<? extends IFloodlightService>, IFloodlightService>();
		impls.put(IProxyArpService.class, arpManager);
		return impls;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		List<Class<? extends IFloodlightService>> dependencies = 
				new ArrayList<Class<? extends IFloodlightService>>();
		dependencies.add(IFloodlightProviderService.class);
		dependencies.add(ITopologyService.class);
		//dependencies.add(IConfigInfoService.class);
		dependencies.add(IRestApiService.class);
		return dependencies;
	}

	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException {
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
		topology = context.getServiceImpl(ITopologyService.class);
		restApi = context.getServiceImpl(IRestApiService.class);
		
		//This could be null because it's not mandatory to have an
		//IConfigInfoService loaded.
		config = context.getServiceImpl(IConfigInfoService.class);

		arpManager.init(floodlightProvider, topology, config, restApi);
	}

	@Override
	public void startUp(FloodlightModuleContext context) {
		arpManager.startUp();
	}

}
