package net.onrc.onos.intent.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.restserver.IRestApiService;
import net.onrc.onos.ofcontroller.networkgraph.INetworkGraphService;

public class IntentRuntime implements IFloodlightModule {
    protected volatile IFloodlightProviderService floodlightProvider;
    protected volatile INetworkGraphService networkGraph;
    protected volatile IRestApiService restApi;

    @Override
    public void init(FloodlightModuleContext context)
	    throws FloodlightModuleException {
	floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
	networkGraph = context.getServiceImpl(INetworkGraphService.class);
	restApi = context.getServiceImpl(IRestApiService.class);
    }

    @Override
    public void startUp(FloodlightModuleContext context) {
	restApi.addRestletRoutable(new IntentWebRoutable());
    }
    
    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
	Collection<Class<? extends IFloodlightService>> l =
		new ArrayList<Class<? extends IFloodlightService>>();
	l.add(IFloodlightProviderService.class);
	l.add(INetworkGraphService.class);
	l.add(IRestApiService.class);
	return l;
    }
    
    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleServices() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
	// TODO Auto-generated method stub
	return null;
    }

}
