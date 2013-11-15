package net.onrc.onos.ofcontroller.flowprogrammer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.OFMatch;
import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFPacketOut;
import org.openflow.protocol.OFPort;
import org.openflow.protocol.OFStatisticsRequest;
import org.openflow.protocol.action.OFAction;
import org.openflow.protocol.action.OFActionDataLayerDestination;
import org.openflow.protocol.action.OFActionDataLayerSource;
import org.openflow.protocol.action.OFActionEnqueue;
import org.openflow.protocol.action.OFActionNetworkLayerDestination;
import org.openflow.protocol.action.OFActionNetworkLayerSource;
import org.openflow.protocol.action.OFActionNetworkTypeOfService;
import org.openflow.protocol.action.OFActionOutput;
import org.openflow.protocol.action.OFActionStripVirtualLan;
import org.openflow.protocol.action.OFActionTransportLayerDestination;
import org.openflow.protocol.action.OFActionTransportLayerSource;
import org.openflow.protocol.action.OFActionVirtualLanIdentifier;
import org.openflow.protocol.action.OFActionVirtualLanPriorityCodePoint;
import org.openflow.protocol.statistics.OFFlowStatisticsReply;
import org.openflow.protocol.statistics.OFFlowStatisticsRequest;
import org.openflow.protocol.statistics.OFStatistics;
import org.openflow.protocol.statistics.OFStatisticsType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.tinkerpop.blueprints.Direction;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.IOFSwitchListener;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.restserver.IRestApiService;
import net.onrc.onos.datagrid.IDatagridService;
import net.onrc.onos.graph.GraphDBOperation;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowEntry;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.onrc.onos.ofcontroller.core.module.IOnosService;
import net.onrc.onos.ofcontroller.floodlightlistener.INetworkGraphService;
import net.onrc.onos.ofcontroller.util.Dpid;
import net.onrc.onos.ofcontroller.util.FlowEntryAction;
import net.onrc.onos.ofcontroller.util.FlowEntryActions;
import net.onrc.onos.ofcontroller.util.FlowEntryId;
import net.onrc.onos.ofcontroller.util.FlowEntryAction.ActionEnqueue;
import net.onrc.onos.ofcontroller.util.FlowEntryAction.ActionOutput;
import net.onrc.onos.ofcontroller.util.FlowEntryAction.ActionSetEthernetAddr;
import net.onrc.onos.ofcontroller.util.FlowEntryAction.ActionSetIPv4Addr;
import net.onrc.onos.ofcontroller.util.FlowEntryAction.ActionSetIpToS;
import net.onrc.onos.ofcontroller.util.FlowEntryAction.ActionSetTcpUdpPort;
import net.onrc.onos.ofcontroller.util.FlowEntryAction.ActionSetVlanId;
import net.onrc.onos.ofcontroller.util.FlowEntryAction.ActionSetVlanPriority;
import net.onrc.onos.ofcontroller.util.FlowEntryAction.ActionStripVlan;
import net.onrc.onos.registry.controller.IControllerRegistryService;

public class FlowSynchronizer implements IFlowSyncService, IOFSwitchListener {

    protected static Logger log = LoggerFactory.getLogger(FlowSynchronizer.class);
    protected IFloodlightProviderService floodlightProvider;
    protected IControllerRegistryService registryService;
    protected IFlowPusherService pusher;

    private GraphDBOperation dbHandler;
    private Map<IOFSwitch, Thread> switchThread = new HashMap<IOFSwitch, Thread>();

    public FlowSynchronizer() {
	dbHandler = new GraphDBOperation("");
    }

    public void synchronize(IOFSwitch sw) {
	Synchroizer sync = new Synchroizer(sw);
	Thread t = new Thread(sync);
	t.start();
	switchThread.put(sw, t);
    }

    @Override
    public void addedSwitch(IOFSwitch sw) {
	log.debug("Switch added: {}", sw.getId());

	if (registryService.hasControl(sw.getId())) {
	    synchronize(sw);
	}
    }

    @Override
    public void removedSwitch(IOFSwitch sw) {
	log.debug("Switch removed: {}", sw.getId());

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
	return "FlowSynchronizer";
    }

    //@Override
    public void init(FloodlightModuleContext context)
	    throws FloodlightModuleException {
	floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
	registryService = context.getServiceImpl(IControllerRegistryService.class);
	pusher = context.getServiceImpl(IFlowPusherService.class);
    }

    //@Override
    public void startUp(FloodlightModuleContext context) {
	floodlightProvider.addOFSwitchListener(this);
    }

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
	    Set<FlowEntryWrapper> graphEntries = getFlowEntriesFromGraph();
	    Set<FlowEntryWrapper> switchEntries = getFlowEntriesFromSwitch();
	    compare(graphEntries, switchEntries);
	}

	private void compare(Set<FlowEntryWrapper> graphEntries, Set<FlowEntryWrapper> switchEntries) {
	    int added = 0, removed = 0, skipped = 0;
	    for(FlowEntryWrapper entry : switchEntries) {
		if(graphEntries.contains(entry)) {
		    graphEntries.remove(entry);
		    skipped++;
		}
		else {
		    // remove flow entry from the switch
		    entry.removeFromSwitch(sw);
		    removed++;
		}
	    }
	    for(FlowEntryWrapper entry : graphEntries) {
		// add flow entry to switch
		entry.addToSwitch(sw);
		added++;
	    }	  
	    log.debug("Flow entries added "+ added + ", " +
		      "Flow entries removed "+ removed + ", " +
		      "Flow entries skipped " + skipped);
	}

	private Set<FlowEntryWrapper> getFlowEntriesFromGraph() {
	    Set<FlowEntryWrapper> entries = new HashSet<FlowEntryWrapper>();
	    for(IFlowEntry entry : swObj.getFlowEntries()) {
		FlowEntryWrapper fe = new FlowEntryWrapper(entry);
		entries.add(fe);
	    }
	    return entries;	    
	}

	private Set<FlowEntryWrapper> getFlowEntriesFromSwitch() {

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

	    Set<FlowEntryWrapper> results = new HashSet<FlowEntryWrapper>();
	    for(OFStatistics result : entries){
		OFFlowStatisticsReply entry = (OFFlowStatisticsReply) result;
		FlowEntryWrapper fe = new FlowEntryWrapper(entry);
		results.add(fe);
	    }
	    return results;
	}

    }

    class FlowEntryWrapper {
	FlowEntryId id;
	IFlowEntry iflowEntry;
	OFFlowStatisticsReply statisticsReply;

	public FlowEntryWrapper(IFlowEntry entry) {
	    iflowEntry = entry;
	    id = new FlowEntryId(entry.getFlowEntryId());
	}

	public FlowEntryWrapper(OFFlowStatisticsReply entry) {
	    statisticsReply = entry;
	    id = new FlowEntryId(entry.getCookie());
	}

	public void addToSwitch(IOFSwitch sw) {
	    if(iflowEntry != null) {
		pusher.add(sw, iflowEntry.getFlow(), iflowEntry);
	    }
	    else if(statisticsReply != null) {
		log.error("Adding existing flow entry {} to sw {}", 
			  statisticsReply.getCookie(), sw.getId());
	    }
	}
	
	public void removeFromSwitch(IOFSwitch sw){
	    if(iflowEntry != null) {
		log.error("Removing non-existent flow entry {} from sw {}", 
			  iflowEntry.getFlowEntryId(), sw.getId());

	    }
	    else if(statisticsReply != null) {
		// Convert Statistics Reply to Flow Mod, then write it
		OFFlowMod fm = new OFFlowMod();
		fm.setCookie(statisticsReply.getCookie());
		fm.setCommand(OFFlowMod.OFPFC_DELETE_STRICT);
		fm.setLengthU(OFFlowMod.MINIMUM_LENGTH);
		fm.setMatch(statisticsReply.getMatch());
		fm.setPriority(statisticsReply.getPriority());
		fm.setOutPort(OFPort.OFPP_NONE);
		pusher.add(sw, fm);
	    }
	}

	/**
	 * Return the hash code of the Flow Entry ID
	 */
	@Override
	public int hashCode() {
	    return id.hashCode();
	}

	/**
	 * Returns true of the object is another Flow Entry ID with 
	 * the same value; otherwise, returns false.
	 * 
	 * @param Object to compare
	 */
	@Override
	public boolean equals(Object obj){
	    if(obj.getClass() == this.getClass()) {
		FlowEntryWrapper entry = (FlowEntryWrapper) obj;
		// TODO: we need to actually compare the match + actions
		return this.id.equals(entry.id);
	    }
	    return false;
	}

	@Override
	public String toString() {
	    return id.toString();
	}
    }
}


