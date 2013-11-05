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
import net.floodlightcontroller.devicemanager.IDeviceService;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.topology.ITopologyService;
import net.onrc.onos.ofcontroller.core.config.DefaultConfiguration;
import net.onrc.onos.ofcontroller.core.config.IConfigInfoService;
import net.onrc.onos.ofcontroller.flowmanager.IFlowService;
import net.onrc.onos.ofcontroller.forwarding.Forwarding;
import net.onrc.onos.ofcontroller.proxyarp.IProxyArpService;
import net.onrc.onos.ofcontroller.proxyarp.ProxyArpManager;

public class OnosModuleLoader implements IFloodlightModule {
	private IFloodlightProviderService floodlightProvider;
	private ITopologyService topology;
	private IDeviceService deviceService;
	private IConfigInfoService config;
	private IRestApiService restApi;
	private IFlowService flowService;

	private ProxyArpManager arpManager;
	private Forwarding forwarding;
	
	public OnosModuleLoader() {
		arpManager = new ProxyArpManager();
		forwarding = new Forwarding();
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
		dependencies.add(IDeviceService.class);
		//dependencies.add(IConfigInfoService.class);
		dependencies.add(IRestApiService.class);
		dependencies.add(IFlowService.class);
		return dependencies;
	}

	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException {
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
		topology = context.getServiceImpl(ITopologyService.class);
		deviceService = context.getServiceImpl(IDeviceService.class);
		restApi = context.getServiceImpl(IRestApiService.class);
		flowService = context.getServiceImpl(IFlowService.class);
		
		//This could be null because it's not mandatory to have an
		//IConfigInfoService loaded.
		config = context.getServiceImpl(IConfigInfoService.class);
		if (config == null) {
			config = new DefaultConfiguration();
		}

		arpManager.init(floodlightProvider, topology, deviceService, config, restApi);
		forwarding.init(floodlightProvider, flowService);
	}

	@Override
	public void startUp(FloodlightModuleContext context) {
		arpManager.startUp();
		forwarding.startUp();
	}

}
