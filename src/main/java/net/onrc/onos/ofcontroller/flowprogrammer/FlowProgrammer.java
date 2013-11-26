package net.onrc.onos.ofcontroller.flowprogrammer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.openflow.protocol.OFFlowRemoved;
import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFPacketIn;
import org.openflow.protocol.OFType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.minlog.Log;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.IListener.Command;
import net.floodlightcontroller.core.IOFSwitchListener;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.onrc.onos.registry.controller.IControllerRegistryService;

public class FlowProgrammer implements IFloodlightModule, 
				       IOFMessageListener,
				       IOFSwitchListener {
    
    protected static Logger log = LoggerFactory.getLogger(FlowProgrammer.class);
    protected volatile IFloodlightProviderService floodlightProvider;
    protected volatile IControllerRegistryService registryService;


    protected FlowPusher pusher;
    private static final int NUM_PUSHER_THREAD = 1;

    protected FlowSynchronizer synchronizer;
        
    public FlowProgrammer() {
	pusher = new FlowPusher(NUM_PUSHER_THREAD);
	synchronizer = new FlowSynchronizer();
    }
    
    @Override
    public void init(FloodlightModuleContext context)
	    throws FloodlightModuleException {
	floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
	registryService = context.getServiceImpl(IControllerRegistryService.class);
	pusher.init(null, floodlightProvider.getOFMessageFactory(), null);
	synchronizer.init(pusher);
    }

    @Override
    public void startUp(FloodlightModuleContext context) {
	pusher.start();
	floodlightProvider.addOFMessageListener(OFType.FLOW_REMOVED, this);
	floodlightProvider.addOFSwitchListener(this);
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleServices() {
	Collection<Class<? extends IFloodlightService>> l = 
		new ArrayList<Class<? extends IFloodlightService>>();
	l.add(IFlowPusherService.class);
	l.add(IFlowSyncService.class);
	return l;
    }

    @Override
    public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
	Map<Class<? extends IFloodlightService>,
	    IFloodlightService> m =
	    new HashMap<Class<? extends IFloodlightService>,
	    IFloodlightService>();
	m.put(IFlowPusherService.class, pusher);
	m.put(IFlowSyncService.class, synchronizer);	
	return m;
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
	Collection<Class<? extends IFloodlightService>> l =
		new ArrayList<Class<? extends IFloodlightService>>();
	l.add(IFloodlightProviderService.class);
	return l;
    }

    @Override
    public String getName() {
	// TODO Auto-generated method stub
	return "FlowProgrammer";
    }

    @Override
    public boolean isCallbackOrderingPrereq(OFType type, String name) {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public boolean isCallbackOrderingPostreq(OFType type, String name) {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
	switch (msg.getType()) {
	case FLOW_REMOVED:
	    OFFlowRemoved flowMsg = (OFFlowRemoved) msg;
	    log.debug("Got flow removed from "+ sw.getId() +": "+ flowMsg.getCookie());
	    break;
	default:
	    break;
	}

	return Command.CONTINUE;
    }

    @Override
    public void addedSwitch(IOFSwitch sw) {
	log.debug("Switch added: {}", sw.getId());

	if (registryService.hasControl(sw.getId())) {
	    synchronizer.synchronize(sw);
	}
    }

    @Override
    public void removedSwitch(IOFSwitch sw) {
	log.debug("Switch removed: {}", sw.getId());
	
	synchronizer.interrupt(sw);
    }

    @Override
    public void switchPortChanged(Long switchId) {
	// TODO Auto-generated method stub
    }
    

}
