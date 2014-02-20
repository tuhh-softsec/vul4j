package net.onrc.onos.intent.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.onrc.onos.datagrid.IDatagridService;
import net.onrc.onos.datagrid.IEventChannelListener;
import net.onrc.onos.intent.FlowEntry;
import net.onrc.onos.intent.IntentOperationList;
import net.onrc.onos.ofcontroller.flowprogrammer.IFlowPusherService;
import net.onrc.onos.ofcontroller.networkgraph.INetworkGraphService;
import net.onrc.onos.ofcontroller.networkgraph.NetworkGraph;

public class PlanInstallModule implements IFloodlightModule {
    protected volatile IFloodlightProviderService floodlightProvider;
    protected volatile INetworkGraphService networkGraph;
    protected volatile IDatagridService datagridService;
    protected volatile IFlowPusherService flowPusher;
    private PlanCalcRuntime planCalc;
    private PlanInstallRuntime planInstall;
    private EventListener eventListener;
    private final static Logger log = LoggerFactory.getLogger(PlanInstallModule.class);


    private static final String PATH_INTENT_CHANNEL_NAME = "onos.pathintent";
    
    @Override
    public void init(FloodlightModuleContext context)
	    throws FloodlightModuleException {
	floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
	networkGraph = context.getServiceImpl(INetworkGraphService.class);
	datagridService = context.getServiceImpl(IDatagridService.class);
	flowPusher = context.getServiceImpl(IFlowPusherService.class);
	NetworkGraph graph = networkGraph.getNetworkGraph();
	planCalc = new PlanCalcRuntime(graph);
	planInstall = new PlanInstallRuntime(graph, floodlightProvider, flowPusher);
	eventListener = new EventListener();
    }

    class EventListener extends Thread
    	implements IEventChannelListener<Long, IntentOperationList> {
	
	private BlockingQueue<IntentOperationList> intentQueue = new LinkedBlockingQueue<>();
	
	@Override
	public void run() {
	    while(true) {
		try {
		    IntentOperationList intents = intentQueue.take();
		    //TODO: drain the remaining intent lists
		    processIntents(intents);
		} catch (InterruptedException e) {
		    //TODO: log the exception
		}
	    }
	}
	
	private void processIntents(IntentOperationList intents) {
	    log.debug("Processing OperationList {}", intents);
	    List<Set<FlowEntry>> plan = planCalc.computePlan(intents);
	    log.debug("Plan: {}", plan);
	    planInstall.installPlan(plan);
	}
	
	@Override
	public void entryAdded(IntentOperationList value) {
	    log.debug("Added OperationList {}", value);
	    intentQueue.add(value);
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
	eventListener.start();
	datagridService.addListener(PATH_INTENT_CHANNEL_NAME, 
				    new EventListener(), 
				    Long.class, 
				    IntentOperationList.class);
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
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
	// TODO Auto-generated method stub
	return null;
    }

}
