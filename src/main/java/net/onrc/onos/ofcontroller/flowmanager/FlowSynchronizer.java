package net.onrc.onos.ofcontroller.flowmanager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.openflow.protocol.OFFeaturesReply;
import org.openflow.protocol.OFMatch;
import org.openflow.protocol.OFStatisticsRequest;
import org.openflow.protocol.statistics.OFFlowStatisticsReply;
import org.openflow.protocol.statistics.OFFlowStatisticsRequest;
import org.openflow.protocol.statistics.OFStatistics;
import org.openflow.protocol.statistics.OFStatisticsType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.IOFSwitchListener;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.onrc.onos.graph.GraphDBOperation;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowEntry;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.onrc.onos.ofcontroller.floodlightlistener.NetworkGraphPublisher;
import net.onrc.onos.ofcontroller.util.Dpid;
import net.onrc.onos.ofcontroller.util.FlowEntry;
import net.onrc.onos.ofcontroller.util.FlowEntryId;

public class FlowSynchronizer implements IOFSwitchListener,
					 IFloodlightModule {

    protected GraphDBOperation dbHandler = new GraphDBOperation(""); //TODO: conf
    private static Logger log = LoggerFactory.getLogger(FlowSynchronizer.class);
    protected IFloodlightProviderService floodlightProvider;
    protected Map<IOFSwitch, Thread> switchThread = new HashMap<IOFSwitch, Thread>();
    
    protected class Synchroizer implements Runnable {
	IOFSwitch sw;
	ISwitchObject swObj;
	
	public Synchroizer(IOFSwitch sw) {
	    this.sw = sw;
	    Dpid dpid = new Dpid(sw.getId());
	    this.swObj = dbHandler.searchSwitch(dpid.toString());
	}
	
	@Override
	public void run() {
	    //TODO: use a FlowEntryId, FlowEntry HashMap
	    Set<FlowEntryId> graphEntries = getFlowEntriesFromGraph();
	    Set<FlowEntryId> switchEntries = getFlowEntriesFromSwitch();
	    compare(graphEntries, switchEntries);
	}
	
	private void compare(Set<FlowEntryId> graphEntries, Set<FlowEntryId> switchEntries) {
	    System.out.println("graph entries: " + graphEntries);
            System.out.println("switch entries: " + switchEntries);
	    Set<FlowEntryId> entriesToAdd = new HashSet<FlowEntryId>(graphEntries);
	    entriesToAdd.removeAll(switchEntries);
	    Set<FlowEntryId> entriesToRemove = switchEntries;
	    entriesToRemove.removeAll(graphEntries);
	    System.out.println("add: " + entriesToAdd);
	    System.out.println("remove: " + entriesToRemove);
	    //FlowDatabaseOperation for converting flowentries
	    
	    /* TODO: new implementation with graph
	    for(FlowEntryId fid : switchEntries,keys()) {
	    	if(graphEntries.contains(fid)) {
	    	    graphEntries.remove(fid);
	    	}
	    	else {
	    	    // remove fid from the switch
	    	}
	    }
	    for(FlowEntry fe : graphEntries.values()) {
	    	// add fid to switch
	    }
	     */
	    
	}
	
	private Set<FlowEntryId> getFlowEntriesFromGraph() {
	    Set<FlowEntryId> entryIds = new HashSet<FlowEntryId>();
	    for(IFlowEntry entry : swObj.getFlowEntries()) {
		FlowEntryId flowEntryId = new FlowEntryId(entry.getFlowEntryId());
		entryIds.add(flowEntryId);
	    }
	    return entryIds;	    
	}
	
	private Set<FlowEntryId> getFlowEntriesFromSwitch() {

	    int lengthU = 0;
	    OFMatch match = new OFMatch();
	    match.setWildcards(OFMatch.OFPFW_ALL);

	    OFFlowStatisticsRequest stat = new OFFlowStatisticsRequest();
	    stat.setOutPort((short) 0xffff); //TODO: OFPort.OFPP_NONE
	    stat.setTableId((byte) 0xff); // TODO: fix this with enum (ALL TABLES)
	    stat.setMatch(match);
	    List<OFStatistics> stats = new ArrayList<OFStatistics>();
	    stats.add(stat);
	    lengthU += stat.getLength();

	    OFStatisticsRequest req = new OFStatisticsRequest();
	    req.setStatisticType(OFStatisticsType.FLOW);
	    req.setStatistics(stats);
	    lengthU += req.getLengthU();
	    req.setLengthU(lengthU);

	    List<OFStatistics> entries = null;
	    try {
		Future<List<OFStatistics>> dfuture = sw.getStatistics(req);
		entries = dfuture.get();
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (ExecutionException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    
	    Set<FlowEntryId> entryIds = new HashSet<FlowEntryId>();
	    for(OFStatistics result : entries){
		//System.out.println(result.getClass());
		OFFlowStatisticsReply entry = (OFFlowStatisticsReply) result;
		FlowEntryId flowEntryId = new FlowEntryId(entry.getCookie());
		entryIds.add(flowEntryId);
	    }
	    return entryIds;
	}
	
    }
    
    @Override
    public void addedSwitch(IOFSwitch sw) {
	// TODO Auto-generated method stub
	System.out.println("added switch in flow sync: " + sw);
	
	// TODO: look at how this is spawned
	Synchroizer sync = new Synchroizer(sw);
	Thread t = new Thread(sync);
	t.start();
	switchThread.put(sw, t);
    }

    @Override
    public void removedSwitch(IOFSwitch sw) {
	// TODO Auto-generated method stub
	System.out.println("removed switch in flow sync: " + sw);
	Thread t = switchThread.remove(sw);
	if(t != null) {
	    t.interrupt();
	}

    }

    @Override
    public void switchPortChanged(Long switchId) {
	// TODO Auto-generated method stub

    }

    @Override
    public String getName() {
	// TODO Auto-generated method stub
	return "FlowSynchronizer";
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

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void init(FloodlightModuleContext context)
	    throws FloodlightModuleException {
	// TODO Auto-generated method stub
	System.out.println("********* Starting flow sync....");
	floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
	System.out.println(context.getAllServices());	
    }

    @Override
    public void startUp(FloodlightModuleContext context) {
	// TODO Auto-generated method stub
	floodlightProvider.addOFSwitchListener(this);
	
    }

}
