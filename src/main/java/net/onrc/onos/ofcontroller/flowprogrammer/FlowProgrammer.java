package net.onrc.onos.ofcontroller.flowprogrammer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;

public class FlowProgrammer implements IFloodlightModule {
	private static final boolean enableFlowSync = false;
	
    protected volatile IFloodlightProviderService floodlightProvider;

    protected FlowPusher pusher;
    private static final int NUM_PUSHER_THREAD = 1;

    protected FlowSynchronizer synchronizer;
        
    public FlowProgrammer() {
	pusher = new FlowPusher(NUM_PUSHER_THREAD);
	if (enableFlowSync) {
	synchronizer = new FlowSynchronizer();
	}
    }
    
    @Override
    public void init(FloodlightModuleContext context)
	    throws FloodlightModuleException {
	floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
	pusher.init(null, floodlightProvider.getOFMessageFactory(), null);
	if (enableFlowSync) {
	synchronizer.init(context);
	}
    }

    @Override
    public void startUp(FloodlightModuleContext context) {
	pusher.start();
	if (enableFlowSync) {
	synchronizer.startUp(context);
	}
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleServices() {
	Collection<Class<? extends IFloodlightService>> l = 
		new ArrayList<Class<? extends IFloodlightService>>();
	l.add(IFlowPusherService.class);
	if (enableFlowSync) {
	l.add(IFlowSyncService.class);
	}
	return l;
    }

    @Override
    public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
	Map<Class<? extends IFloodlightService>,
	    IFloodlightService> m =
	    new HashMap<Class<? extends IFloodlightService>,
	    IFloodlightService>();
	m.put(IFlowPusherService.class, pusher);
	if (enableFlowSync) {
	m.put(IFlowSyncService.class, synchronizer);
	}
	return m;
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
	Collection<Class<? extends IFloodlightService>> l =
		new ArrayList<Class<? extends IFloodlightService>>();
	l.add(IFloodlightProviderService.class);
	return l;
    }
    

}
