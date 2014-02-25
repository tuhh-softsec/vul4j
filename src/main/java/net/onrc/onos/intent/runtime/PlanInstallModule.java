package net.onrc.onos.intent.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.onrc.onos.datagrid.IDatagridService;
import net.onrc.onos.datagrid.IEventChannel;
import net.onrc.onos.datagrid.IEventChannelListener;
import net.onrc.onos.intent.FlowEntry;
import net.onrc.onos.intent.Intent.IntentState;
import net.onrc.onos.intent.IntentOperation;
import net.onrc.onos.intent.IntentOperationList;
import net.onrc.onos.ofcontroller.flowprogrammer.IFlowPusherService;
import net.onrc.onos.ofcontroller.networkgraph.INetworkGraphService;
//import net.onrc.onos.ofcontroller.networkgraph.NetworkGraph;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlanInstallModule implements IFloodlightModule {
    protected volatile IFloodlightProviderService floodlightProvider;
    protected volatile INetworkGraphService networkGraph;
    protected volatile IDatagridService datagridService;
    protected volatile IFlowPusherService flowPusher;
    private PlanCalcRuntime planCalc;
    private PlanInstallRuntime planInstall;
    private EventListener eventListener;
    private IEventChannel<Long, IntentStateList> intentStateChannel;
    private final static Logger log = LoggerFactory.getLogger(PlanInstallModule.class);


    private static final String PATH_INTENT_CHANNEL_NAME = "onos.pathintent";
    private static final String INTENT_STATE_EVENT_CHANNEL_NAME = "onos.pathintent_state";

    
    @Override
    public void init(FloodlightModuleContext context)
	    throws FloodlightModuleException {
	floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
	networkGraph = context.getServiceImpl(INetworkGraphService.class);
	datagridService = context.getServiceImpl(IDatagridService.class);
	flowPusher = context.getServiceImpl(IFlowPusherService.class);
//	NetworkGraph graph = networkGraph.getNetworkGraph();
	planCalc = new PlanCalcRuntime();
	planInstall = new PlanInstallRuntime(floodlightProvider, flowPusher);
	eventListener = new EventListener();
    }

    class EventListener extends Thread
    	implements IEventChannelListener<Long, IntentOperationList> {
	
	private BlockingQueue<IntentOperationList> intentQueue = new LinkedBlockingQueue<>();
	private Long key = Long.valueOf(0);
	
	@Override
	public void run() {
	    while(true) {
		try {
		    IntentOperationList intents = intentQueue.take();
		    //TODO: consider draining the remaining intent lists 
		    //      and processing in one big batch
//		    List<IntentOperationList> remaining = new LinkedList<>();
//		    intentQueue.drainTo(remaining);
		    
		    processIntents(intents);
		} catch (InterruptedException e) {
		    log.warn("Error taking from intent queue: {}", e.getMessage());
		}
	    }
	}
	
	private void processIntents(IntentOperationList intents) {
	    log.debug("Processing OperationList {}", intents);
	    List<Set<FlowEntry>> plan = planCalc.computePlan(intents);
	    log.debug("Plan: {}", plan);
	    boolean success = planInstall.installPlan(plan);
	    
	    sendNotifications(intents, true, success);
	}
	
	private void sendNotifications(IntentOperationList intents, boolean installed, boolean success) {
	    IntentStateList states = new IntentStateList();
	    for(IntentOperation i : intents) {
		IntentState newState;
		switch(i.operator) {
		case REMOVE:
		    if(installed) {
			newState = success ? IntentState.DEL_ACK : IntentState.DEL_PENDING;
		    }
		    else {
			newState = IntentState.DEL_REQ;
		    }
		    break;
		case ADD:
		default:
		    if(installed) {
			newState = success ? IntentState.INST_ACK : IntentState.INST_NACK;
		    }
		    else {
			newState = IntentState.INST_REQ;
		    }
		    break;
		}
		states.put(i.intent.getId(), newState);
	    }
	    intentStateChannel.addEntry(key, states);
	    key += 1;
	}
	
	@Override
	public void entryAdded(IntentOperationList value) {
	    sendNotifications(value, false, false);
	    
	    log.debug("Added OperationList {}", value);
	    try {
		intentQueue.put(value);
	    } catch (InterruptedException e) {
		log.warn("Error putting to intent queue: {}", e.getMessage());
	    }
	}

	@Override
	public void entryRemoved(IntentOperationList value) {
	    // This channel is a queue, so this method is not needed
	}

	@Override
	public void entryUpdated(IntentOperationList value) {
	    // This channel is a queue, so this method is not needed
	}
    }
    @Override
    public void startUp(FloodlightModuleContext context) {
	// start subscriber
	datagridService.addListener(PATH_INTENT_CHANNEL_NAME, 
				    	      eventListener, 
				              Long.class, 
				              IntentOperationList.class);
	eventListener.start();
	// start publisher
	intentStateChannel = datagridService.createChannel(INTENT_STATE_EVENT_CHANNEL_NAME, 
						Long.class, 
						IntentStateList.class);
    }
    
    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
	Collection<Class<? extends IFloodlightService>> l =
		new ArrayList<Class<? extends IFloodlightService>>();
	l.add(IFloodlightProviderService.class);
	l.add(INetworkGraphService.class);
	l.add(IDatagridService.class);
	l.add(IFlowPusherService.class);
	return l;
    }
    
    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleServices() {
	// no services, for now
	return null;
    }

    @Override
    public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
	// no services, for now
	return null;
    }

}
