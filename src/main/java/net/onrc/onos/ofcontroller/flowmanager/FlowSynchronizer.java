package net.onrc.onos.ofcontroller.flowmanager;

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

public class FlowSynchronizer implements IOFSwitchListener,
					 IFlowSyncService {

    protected GraphDBOperation dbHandler = new GraphDBOperation(""); //TODO: conf
    protected static Logger log = LoggerFactory.getLogger(FlowSynchronizer.class);
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
	    Set<FlowEntryWrapper> graphEntries = getFlowEntriesFromGraph();
	    Set<FlowEntryWrapper> switchEntries = getFlowEntriesFromSwitch();
	    compare(graphEntries, switchEntries);
	}
	
	private void compare(Set<FlowEntryWrapper> graphEntries, Set<FlowEntryWrapper> switchEntries) {
	    
	    /* old impl
	    System.out.println("graph entries: " + graphEntries);
            System.out.println("switch entries: " + switchEntries);
	    Set<FlowEntryWrapper> entriesToAdd = new HashSet<FlowEntryWrapper>(graphEntries);
	    entriesToAdd.removeAll(switchEntries);
	    Set<FlowEntryWrapper> entriesToRemove = switchEntries;
	    entriesToRemove.removeAll(graphEntries);
	    System.out.println("add: " + entriesToAdd);
	    System.out.println("remove: " + entriesToRemove);
	    //FlowDatabaseOperation for converting flowentries
	    */
	    
	    /* TODO: new implementation with graph */
	    int added = 0, removed = 0, skipped = 0;
	    for(FlowEntryWrapper entry : switchEntries) {
	    	if(graphEntries.contains(entry)) {
	    	    graphEntries.remove(entry);
	    	    System.out.println("** skipping entry " + entry.id);
	    	    skipped++;
	    	}
	    	else {
	    	    // remove fid from the switch
	    	    System.out.println("** remove entry " + entry.id);
	    	    // TODO: use remove strict message
	    	    writeToSwitch(entry.getOFMessage());
	    	    removed++;
	    	}
	    }
	    for(FlowEntryWrapper entry : graphEntries) {
	    	// add fid to switch
		System.out.println("** add entry " + entry.id);
		// TODO: use modify strict message
		writeToSwitch(entry.getOFMessage());
		added++;
	    }	  
	    log.debug("Flow entries added "+ added + ", " +
		      "Flow entries removed "+ removed + ", " +
		      "Flow entries skipped " + skipped);
	}
	
	private void writeToSwitch(OFMessage msg) {
	    try {
		sw.write(msg, null); // TODO: what is context?
		sw.flush();
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		System.out.println("ERROR*****");
		e.printStackTrace();
	    } 
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
		//System.out.println(result.getClass());
		OFFlowStatisticsReply entry = (OFFlowStatisticsReply) result;
		FlowEntryWrapper fe = new FlowEntryWrapper(entry);
		results.add(fe);
	    }
	    return results;
	}
	
    }
    
    public void synchronize(IOFSwitch sw) {
	Synchroizer sync = new Synchroizer(sw);
	Thread t = new Thread(sync);
	t.start();
	switchThread.put(sw, t);
    }
    
    @Override
    public void addedSwitch(IOFSwitch sw) {
	// TODO Auto-generated method stub
	System.out.println("added switch in flow sync: " + sw);
	
	// TODO: look at how this is spawned
	synchronize(sw);
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

    /*
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
    */

}

class FlowEntryWrapper {
    FlowEntryId id;
    IFlowEntry iflow;
    OFFlowStatisticsReply stat;
    
    public FlowEntryWrapper(IFlowEntry entry) {
	// TODO Auto-generated constructor stub
	iflow = entry;
	id = new FlowEntryId(entry.getFlowEntryId());
    }
    
    public FlowEntryWrapper(OFFlowStatisticsReply entry) {
	stat = entry;
	id = new FlowEntryId(entry.getCookie());
    }
    
    public OFMessage getOFMessage() {
	if(iflow != null) {
	    //convert iflow
	    OFFlowMod fm = new OFFlowMod();
	    fm.setCommand(OFFlowMod.OFPFC_MODIFY_STRICT);
	    
	    // ************* COPIED
	    OFMatch match = new OFMatch();
		match.setWildcards(OFMatch.OFPFW_ALL);

		// Match the Incoming Port
		Short matchInPort = iflow.getMatchInPort();
		if (matchInPort != null) {
		    match.setInputPort(matchInPort);
		    match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_IN_PORT);
		}

		// Match the Source MAC address
		String matchSrcMac = iflow.getMatchSrcMac();
		if (matchSrcMac != null) {
		    match.setDataLayerSource(matchSrcMac);
		    match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_DL_SRC);
		}

		// Match the Destination MAC address
		String matchDstMac = iflow.getMatchDstMac();
		if (matchDstMac != null) {
		    match.setDataLayerDestination(matchDstMac);
		    match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_DL_DST);
		}

		// Match the Ethernet Frame Type
		Short matchEthernetFrameType = iflow.getMatchEthernetFrameType();
		if (matchEthernetFrameType != null) {
		    match.setDataLayerType(matchEthernetFrameType);
		    match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_DL_TYPE);
		}

		// Match the VLAN ID
		Short matchVlanId = iflow.getMatchVlanId();
		if (matchVlanId != null) {
		    match.setDataLayerVirtualLan(matchVlanId);
		    match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_DL_VLAN);
		}

		// Match the VLAN priority
		Byte matchVlanPriority = iflow.getMatchVlanPriority();
		if (matchVlanPriority != null) {
		    match.setDataLayerVirtualLanPriorityCodePoint(matchVlanPriority);
		    match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_DL_VLAN_PCP);
		}

		// Match the Source IPv4 Network prefix
		String matchSrcIPv4Net = iflow.getMatchSrcIPv4Net();
		if (matchSrcIPv4Net != null) {
		    match.setFromCIDR(matchSrcIPv4Net, OFMatch.STR_NW_SRC);
		}

		// Natch the Destination IPv4 Network prefix
		String matchDstIPv4Net = iflow.getMatchDstIPv4Net();
		if (matchDstIPv4Net != null) {
		    match.setFromCIDR(matchDstIPv4Net, OFMatch.STR_NW_DST);
		}

		// Match the IP protocol
		Byte matchIpProto = iflow.getMatchIpProto();
		if (matchIpProto != null) {
		    match.setNetworkProtocol(matchIpProto);
		    match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_NW_PROTO);
		}

		// Match the IP ToS (DSCP field, 6 bits)
		Byte matchIpToS = iflow.getMatchIpToS();
		if (matchIpToS != null) {
		    match.setNetworkTypeOfService(matchIpToS);
		    match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_NW_TOS);
		}

		// Match the Source TCP/UDP port
		Short matchSrcTcpUdpPort = iflow.getMatchSrcTcpUdpPort();
		if (matchSrcTcpUdpPort != null) {
		    match.setTransportSource(matchSrcTcpUdpPort);
		    match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_TP_SRC);
		}

		// Match the Destination TCP/UDP port
		Short matchDstTcpUdpPort = iflow.getMatchDstTcpUdpPort();
		if (matchDstTcpUdpPort != null) {
		    match.setTransportDestination(matchDstTcpUdpPort);
		    match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_TP_DST);
		}
		
		//
		// Fetch the actions
		//
//		Short actionOutputPort = null;
		List<OFAction> openFlowActions = new ArrayList<OFAction>();
		int actionsLen = 0;
		FlowEntryActions flowEntryActions = null;
		String actionsStr = iflow.getActions();
		if (actionsStr != null)
		    flowEntryActions = new FlowEntryActions(actionsStr);
		for (FlowEntryAction action : flowEntryActions.actions()) {
//		    ActionOutput actionOutput = action.actionOutput();
		    ActionSetVlanId actionSetVlanId = action.actionSetVlanId();
		    ActionSetVlanPriority actionSetVlanPriority = action.actionSetVlanPriority();
		    ActionStripVlan actionStripVlan = action.actionStripVlan();
		    ActionSetEthernetAddr actionSetEthernetSrcAddr = action.actionSetEthernetSrcAddr();
		    ActionSetEthernetAddr actionSetEthernetDstAddr = action.actionSetEthernetDstAddr();
		    ActionSetIPv4Addr actionSetIPv4SrcAddr = action.actionSetIPv4SrcAddr();
		    ActionSetIPv4Addr actionSetIPv4DstAddr = action.actionSetIPv4DstAddr();
		    ActionSetIpToS actionSetIpToS = action.actionSetIpToS();
		    ActionSetTcpUdpPort actionSetTcpUdpSrcPort = action.actionSetTcpUdpSrcPort();
		    ActionSetTcpUdpPort actionSetTcpUdpDstPort = action.actionSetTcpUdpDstPort();
		    ActionEnqueue actionEnqueue = action.actionEnqueue();

//		    if (actionOutput != null) {
//			actionOutputPort = actionOutput.port().value();
//			// XXX: The max length is hard-coded for now
//			OFActionOutput ofa =
//			    new OFActionOutput(actionOutput.port().value(),
//					       (short)0xffff);
//			openFlowActions.add(ofa);
//			actionsLen += ofa.getLength();
//		    }

		    if (actionSetVlanId != null) {
			OFActionVirtualLanIdentifier ofa =
			    new OFActionVirtualLanIdentifier(actionSetVlanId.vlanId());
			openFlowActions.add(ofa);
			actionsLen += ofa.getLength();
		    }

		    if (actionSetVlanPriority != null) {
			OFActionVirtualLanPriorityCodePoint ofa =
			    new OFActionVirtualLanPriorityCodePoint(actionSetVlanPriority.vlanPriority());
			openFlowActions.add(ofa);
			actionsLen += ofa.getLength();
		    }

		    if (actionStripVlan != null) {
			if (actionStripVlan.stripVlan() == true) {
			    OFActionStripVirtualLan ofa = new OFActionStripVirtualLan();
			    openFlowActions.add(ofa);
			    actionsLen += ofa.getLength();
			}
		    }

		    if (actionSetEthernetSrcAddr != null) {
			OFActionDataLayerSource ofa = 
			    new OFActionDataLayerSource(actionSetEthernetSrcAddr.addr().toBytes());
			openFlowActions.add(ofa);
			actionsLen += ofa.getLength();
		    }

		    if (actionSetEthernetDstAddr != null) {
			OFActionDataLayerDestination ofa =
			    new OFActionDataLayerDestination(actionSetEthernetDstAddr.addr().toBytes());
			openFlowActions.add(ofa);
			actionsLen += ofa.getLength();
		    }

		    if (actionSetIPv4SrcAddr != null) {
			OFActionNetworkLayerSource ofa =
			    new OFActionNetworkLayerSource(actionSetIPv4SrcAddr.addr().value());
			openFlowActions.add(ofa);
			actionsLen += ofa.getLength();
		    }

		    if (actionSetIPv4DstAddr != null) {
			OFActionNetworkLayerDestination ofa =
			    new OFActionNetworkLayerDestination(actionSetIPv4DstAddr.addr().value());
			openFlowActions.add(ofa);
			actionsLen += ofa.getLength();
		    }

		    if (actionSetIpToS != null) {
			OFActionNetworkTypeOfService ofa =
			    new OFActionNetworkTypeOfService(actionSetIpToS.ipToS());
			openFlowActions.add(ofa);
			actionsLen += ofa.getLength();
		    }

		    if (actionSetTcpUdpSrcPort != null) {
			OFActionTransportLayerSource ofa =
			    new OFActionTransportLayerSource(actionSetTcpUdpSrcPort.port());
			openFlowActions.add(ofa);
			actionsLen += ofa.getLength();
		    }

		    if (actionSetTcpUdpDstPort != null) {
			OFActionTransportLayerDestination ofa =
			    new OFActionTransportLayerDestination(actionSetTcpUdpDstPort.port());
			openFlowActions.add(ofa);
			actionsLen += ofa.getLength();
		    }

		    if (actionEnqueue != null) {
			OFActionEnqueue ofa =
			    new OFActionEnqueue(actionEnqueue.port().value(),
						actionEnqueue.queueId());
			openFlowActions.add(ofa);
			actionsLen += ofa.getLength();
		    }
		}

		fm.setIdleTimeout((short) 0)
		    .setHardTimeout((short) 0)
		    .setPriority((short) 100)
		    .setBufferId(OFPacketOut.BUFFER_ID_NONE);
		fm
		    .setCookie(id.value())
		    .setMatch(match)
		    .setActions(openFlowActions)
		    .setLengthU(OFFlowMod.MINIMUM_LENGTH + actionsLen);
		fm.setOutPort(OFPort.OFPP_NONE.getValue());
		
	    // ********* END COPIED
	    
	    return fm;
	}
	else if(stat != null) {
	    // convert stat
	    OFFlowMod fm = new OFFlowMod();
	    fm.setCookie(stat.getCookie());
	    fm.setCommand(OFFlowMod.OFPFC_DELETE_STRICT);
	    fm.setLengthU(OFFlowMod.MINIMUM_LENGTH);
	    fm.setMatch(stat.getMatch());
	    fm.setPriority(stat.getPriority());
	    fm.setOutPort(OFPort.OFPP_NONE);
//	    fm.setActions(stat.getActions());
//	    fm.setIdleTimeout(stat.getIdleTimeout());
//	    fm.setHardTimeout(stat.getHardTimeout());
	    return fm;
	}
	return null;
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
	    return this.id.equals(entry.id);
	}
	return false;
    }
    
    @Override
    public String toString() {
	return id.toString();
    }
}
