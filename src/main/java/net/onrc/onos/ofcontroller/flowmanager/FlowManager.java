package net.onrc.onos.ofcontroller.flowmanager;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.util.MACAddress;
import net.floodlightcontroller.util.OFMessageDamper;
import net.onrc.onos.graph.GraphDBOperation;
import net.onrc.onos.ofcontroller.core.INetMapStorage;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowEntry;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowPath;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IPortObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyService.ITopoRouteService;
import net.onrc.onos.ofcontroller.flowmanager.web.FlowWebRoutable;
import net.onrc.onos.ofcontroller.routing.TopoRouteService;
import net.onrc.onos.ofcontroller.util.CallerId;
import net.onrc.onos.ofcontroller.util.DataPath;
import net.onrc.onos.ofcontroller.util.DataPathEndpoints;
import net.onrc.onos.ofcontroller.util.Dpid;
import net.onrc.onos.ofcontroller.util.FlowEntry;
import net.onrc.onos.ofcontroller.util.FlowEntryAction;
import net.onrc.onos.ofcontroller.util.FlowEntryId;
import net.onrc.onos.ofcontroller.util.FlowEntryMatch;
import net.onrc.onos.ofcontroller.util.FlowEntrySwitchState;
import net.onrc.onos.ofcontroller.util.FlowEntryUserState;
import net.onrc.onos.ofcontroller.util.FlowId;
import net.onrc.onos.ofcontroller.util.FlowPath;
import net.onrc.onos.ofcontroller.util.FlowPathFlags;
import net.onrc.onos.ofcontroller.util.IPv4Net;
import net.onrc.onos.ofcontroller.util.Port;
import net.onrc.onos.ofcontroller.util.SwitchPort;

import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.OFMatch;
import org.openflow.protocol.OFPacketOut;
import org.openflow.protocol.OFPort;
import org.openflow.protocol.OFType;
import org.openflow.protocol.action.OFAction;
import org.openflow.protocol.action.OFActionOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FlowManager implements IFloodlightModule, IFlowService, INetMapStorage {

    protected GraphDBOperation op;

    protected IRestApiService restApi;
    protected volatile IFloodlightProviderService floodlightProvider;
    protected volatile ITopoRouteService topoRouteService;
    protected FloodlightModuleContext context;

    protected OFMessageDamper messageDamper;

    //
    // TODO: Values copied from elsewhere (class LearningSwitch).
    // The local copy should go away!
    //
    protected static final int OFMESSAGE_DAMPER_CAPACITY = 50000; // TODO: find sweet spot
    protected static final int OFMESSAGE_DAMPER_TIMEOUT = 250;	// ms
    public static final short FLOWMOD_DEFAULT_IDLE_TIMEOUT = 0;	// infinity
    public static final short FLOWMOD_DEFAULT_HARD_TIMEOUT = 0;	// infinite
    public static final short PRIORITY_DEFAULT = 100;

    // Flow Entry ID generation state
    private static Random randomGenerator = new Random();
    private static int nextFlowEntryIdPrefix = 0;
    private static int nextFlowEntryIdSuffix = 0;
    private static long nextFlowEntryId = 0;

    // State for measurement purpose
    private static long measurementFlowId = 100000;
    private static String measurementFlowIdStr = "0x186a0";	// 100000
    private long modifiedMeasurementFlowTime = 0;
    //
    private LinkedList<FlowPath> measurementStoredPaths = new LinkedList<FlowPath>();
    private long measurementStartTimeProcessingPaths = 0;
    private long measurementEndTimeProcessingPaths = 0;
    Map<Long, ?> measurementShortestPathTopo = null;
    private String measurementPerFlowStr = new String();

    /** The logger. */
    private static Logger log = LoggerFactory.getLogger(FlowManager.class);

    // The periodic task(s)
    private ScheduledExecutorService mapReaderScheduler;
    private ScheduledExecutorService shortestPathReconcileScheduler;

    final Runnable mapReader = new Runnable() {
	    public void run() {
		try {
		    runImpl();
		} catch (Exception e) {
		    log.debug("Exception processing All Flow Entries from the Network MAP: ", e);
		    op.rollback();
		    return;
		}
	    }

	    private void runImpl() {
		long startTime = System.nanoTime();
		int counterAllFlowEntries = 0;
		int counterMyNotUpdatedFlowEntries = 0;

		if (floodlightProvider == null) {
		    log.debug("FloodlightProvider service not found!");
		    return;
		}
		Map<Long, IOFSwitch> mySwitches =
		    floodlightProvider.getSwitches();
		LinkedList<IFlowEntry> addFlowEntries =
		    new LinkedList<IFlowEntry>();
		LinkedList<IFlowEntry> deleteFlowEntries =
		    new LinkedList<IFlowEntry>();

		//
		// Fetch all Flow Entries which need to be updated and select only my Flow Entries
		// that need to be updated into the switches.
		//
		boolean processed_measurement_flow = false;
		Iterable<IFlowEntry> allFlowEntries =
		    op.getAllSwitchNotUpdatedFlowEntries();
		for (IFlowEntry flowEntryObj : allFlowEntries) {
		    counterAllFlowEntries++;

		    String dpidStr = flowEntryObj.getSwitchDpid();
		    if (dpidStr == null)
			continue;
		    Dpid dpid = new Dpid(dpidStr);
		    IOFSwitch mySwitch = mySwitches.get(dpid.value());
		    if (mySwitch == null)
			continue;	// Ignore the entry: not my switch

		    IFlowPath flowObj =
			op.getFlowPathByFlowEntry(flowEntryObj);
		    if (flowObj == null)
			continue;		// Should NOT happen
		    if (flowObj.getFlowId() == null)
			continue;		// Invalid entry

		    //
		    // NOTE: For now we process the DELETE before the ADD
		    // to cover the more common scenario.
		    // TODO: This is error prone and needs to be fixed!
		    //
		    String userState = flowEntryObj.getUserState();
		    if (userState == null)
			continue;
		    if (userState.equals("FE_USER_DELETE")) {
			// An entry that needs to be deleted.
			deleteFlowEntries.add(flowEntryObj);
			installFlowEntry(mySwitch, flowObj, flowEntryObj);
		    } else {
			addFlowEntries.add(flowEntryObj);
		    }
		    counterMyNotUpdatedFlowEntries++;
		    // Code for measurement purpose
		    // TODO: Commented-out for now
		    /*
		    {
			if (flowObj.getFlowId().equals(measurementFlowIdStr)) {
			    processed_measurement_flow = true;
			}
		    }
		    */
		}

		//
		// Process the Flow Entries that need to be added
		//
		for (IFlowEntry flowEntryObj : addFlowEntries) {
		    IFlowPath flowObj =
			op.getFlowPathByFlowEntry(flowEntryObj);
		    if (flowObj == null)
			continue;		// Should NOT happen
		    if (flowObj.getFlowId() == null)
			continue;		// Invalid entry

		    Dpid dpid = new Dpid(flowEntryObj.getSwitchDpid());
		    IOFSwitch mySwitch = mySwitches.get(dpid.value());
		    if (mySwitch == null)
			continue;		// Shouldn't happen
		    installFlowEntry(mySwitch, flowObj, flowEntryObj);
		}

		//
		// Delete all Flow Entries marked for deletion from the
		// Network MAP.
		//
		// TODO: We should use the OpenFlow Barrier mechanism
		// to check for errors, and delete the Flow Entries after the
		// Barrier message is received.
		//
		while (! deleteFlowEntries.isEmpty()) {
		    IFlowEntry flowEntryObj = deleteFlowEntries.poll();
		    IFlowPath flowObj =
			op.getFlowPathByFlowEntry(flowEntryObj);
		    if (flowObj == null) {
			log.debug("Did not find FlowPath to be deleted");
			continue;
		    }
		    flowObj.removeFlowEntry(flowEntryObj);
		    op.removeFlowEntry(flowEntryObj);
		}

		op.commit();

		if (processed_measurement_flow) {
		    long estimatedTime =
			System.nanoTime() - modifiedMeasurementFlowTime;
		    String logMsg = "MEASUREMENT: Pushed Flow delay: " +
			(double)estimatedTime / 1000000000 + " sec";
		    log.debug(logMsg);
		}

		long estimatedTime = System.nanoTime() - startTime;
		double rate = 0.0;
		if (estimatedTime > 0)
		    rate = ((double)counterAllFlowEntries * 1000000000) / estimatedTime;
		String logMsg = "MEASUREMENT: Processed AllFlowEntries: " +
		    counterAllFlowEntries + " MyNotUpdatedFlowEntries: " +
		    counterMyNotUpdatedFlowEntries + " in " +
		    (double)estimatedTime / 1000000000 + " sec: " +
		    rate + " paths/s";
		log.debug(logMsg);
	    }
	};

    final Runnable shortestPathReconcile = new Runnable() {
	    public void run() {
		try {
		    runImpl();
		} catch (Exception e) {
		    log.debug("Exception processing All Flows from the Network MAP: ", e);
		    op.rollback();
		    return;
		}
	    }

	    private void runImpl() {
		long startTime = System.nanoTime();
		int counterAllFlowPaths = 0;
		int counterMyFlowPaths = 0;

		if (floodlightProvider == null) {
		    log.debug("FloodlightProvider service not found!");
		    return;
		}
		Map<Long, IOFSwitch> mySwitches =
		    floodlightProvider.getSwitches();
		LinkedList<IFlowPath> deleteFlows = new LinkedList<IFlowPath>();

		boolean processed_measurement_flow = false;

		//
		// Fetch and recompute the Shortest Path for those
		// Flow Paths this controller is responsible for.
		//
		Map<Long, ?> shortestPathTopo =
		    topoRouteService.prepareShortestPathTopo();
		Iterable<IFlowPath> allFlowPaths = op.getAllFlowPaths();
		for (IFlowPath flowPathObj : allFlowPaths) {
		    counterAllFlowPaths++;
		    if (flowPathObj == null)
			continue;

		    String srcDpidStr = flowPathObj.getSrcSwitch();
		    if (srcDpidStr == null)
			continue;
		    Dpid srcDpid = new Dpid(srcDpidStr);
		    //
		    // Use the source DPID as a heuristic to decide
		    // which controller is responsible for maintaining the
		    // shortest path.
		    // NOTE: This heuristic is error-prone: if the switch
		    // goes away and no controller is responsible for that
		    // switch, then the original Flow Path is not cleaned-up
		    //
		    IOFSwitch mySwitch = mySwitches.get(srcDpid.value());
		    if (mySwitch == null)
			continue;	// Ignore: not my responsibility

		    // Test the Data Path Summary string
		    String dataPathSummaryStr = flowPathObj.getDataPathSummary();
		    if (dataPathSummaryStr == null)
			continue;	// Could be invalid entry?
		    if (dataPathSummaryStr.isEmpty())
			continue;	// No need to maintain this flow

		    //
		    // Test whether we need to complete the Flow cleanup,
		    // if the Flow has been deleted by the user.
		    //
		    String flowUserState = flowPathObj.getUserState();
		    if ((flowUserState != null)
			&& flowUserState.equals("FE_USER_DELETE")) {
			Iterable<IFlowEntry> flowEntries = flowPathObj.getFlowEntries();
			boolean empty = true;	// TODO: an ugly hack
			for (IFlowEntry flowEntryObj : flowEntries) {
			    empty = false;
			    break;
			}
			if (empty)
			    deleteFlows.add(flowPathObj);
		    }

		    // Fetch the fields needed to recompute the shortest path
		    Short srcPortShort = flowPathObj.getSrcPort();
		    String dstDpidStr = flowPathObj.getDstSwitch();
		    Short dstPortShort = flowPathObj.getDstPort();
		    Long flowPathFlagsLong = flowPathObj.getFlowPathFlags();
		    if ((srcPortShort == null) ||
			(dstDpidStr == null) ||
			(dstPortShort == null) ||
			(flowPathFlagsLong == null)) {
			continue;
		    }

		    Port srcPort = new Port(srcPortShort);
		    Dpid dstDpid = new Dpid(dstDpidStr);
		    Port dstPort = new Port(dstPortShort);
		    SwitchPort srcSwitchPort = new SwitchPort(srcDpid, srcPort);
		    SwitchPort dstSwitchPort = new SwitchPort(dstDpid, dstPort);
		    FlowPathFlags flowPathFlags = new FlowPathFlags(flowPathFlagsLong);

		    counterMyFlowPaths++;

		    //
		    // NOTE: Using here the regular getShortestPath() method
		    // won't work here, because that method calls internally
		    //  "conn.endTx(Transaction.COMMIT)", and that will
		    // invalidate all handlers to the Titan database.
		    // If we want to experiment with calling here
		    // getShortestPath(), we need to refactor that code
		    // to avoid closing the transaction.
		    //
		    DataPath dataPath =
			topoRouteService.getTopoShortestPath(shortestPathTopo,
							     srcSwitchPort,
							     dstSwitchPort);
		    if (dataPath == null) {
			// We need the DataPath to compare the paths
			dataPath = new DataPath();
			dataPath.setSrcPort(srcSwitchPort);
			dataPath.setDstPort(dstSwitchPort);
		    }
		    dataPath.applyFlowPathFlags(flowPathFlags);

		    String newDataPathSummaryStr = dataPath.dataPathSummary();
		    if (dataPathSummaryStr.equals(newDataPathSummaryStr))
			continue;	// Nothing changed

		    reconcileFlow(flowPathObj, dataPath);
		}

		//
		// Delete all leftover Flows marked for deletion from the
		// Network MAP.
		//
		while (! deleteFlows.isEmpty()) {
		    IFlowPath flowPathObj = deleteFlows.poll();
		    op.removeFlowPath(flowPathObj);
		}

		topoRouteService.dropShortestPathTopo(shortestPathTopo);

		op.commit();

		if (processed_measurement_flow) {
		    long estimatedTime =
			System.nanoTime() - modifiedMeasurementFlowTime;
		    String logMsg = "MEASUREMENT: Pushed Flow delay: " +
			(double)estimatedTime / 1000000000 + " sec";
		    log.debug(logMsg);
		}

		long estimatedTime = System.nanoTime() - startTime;
		double rate = 0.0;
		if (estimatedTime > 0)
		    rate = ((double)counterAllFlowPaths * 1000000000) / estimatedTime;
		String logMsg = "MEASUREMENT: Processed AllFlowPaths: " +
		    counterAllFlowPaths + " MyFlowPaths: " +
		    counterMyFlowPaths + " in " +
		    (double)estimatedTime / 1000000000 + " sec: " +
		    rate + " paths/s";
		log.debug(logMsg);
	    }
	};

    //final ScheduledFuture<?> mapReaderHandle =
	//mapReaderScheduler.scheduleAtFixedRate(mapReader, 3, 3, TimeUnit.SECONDS);

    //final ScheduledFuture<?> shortestPathReconcileHandle =
	//shortestPathReconcileScheduler.scheduleAtFixedRate(shortestPathReconcile, 3, 3, TimeUnit.SECONDS);

    @Override
    public void init(String conf) {
    	op = new GraphDBOperation(conf);
	topoRouteService = new TopoRouteService(conf);
    }

    public void finalize() {
    	close();
    }

    @Override
    public void close() {
    	op.close();
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleServices() {
        Collection<Class<? extends IFloodlightService>> l = 
            new ArrayList<Class<? extends IFloodlightService>>();
        l.add(IFlowService.class);
        return l;
    }

    @Override
    public Map<Class<? extends IFloodlightService>, IFloodlightService> 
			       getServiceImpls() {
        Map<Class<? extends IFloodlightService>,
        IFloodlightService> m = 
            new HashMap<Class<? extends IFloodlightService>,
                IFloodlightService>();
        m.put(IFlowService.class, this);
        return m;
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> 
                                                    getModuleDependencies() {
	Collection<Class<? extends IFloodlightService>> l =
	    new ArrayList<Class<? extends IFloodlightService>>();
	l.add(IFloodlightProviderService.class);
	l.add(IRestApiService.class);
        return l;
    }

    @Override
    public void init(FloodlightModuleContext context)
	throws FloodlightModuleException {
	this.context = context;
	floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
	restApi = context.getServiceImpl(IRestApiService.class);
	messageDamper = new OFMessageDamper(OFMESSAGE_DAMPER_CAPACITY,
					    EnumSet.of(OFType.FLOW_MOD),
					    OFMESSAGE_DAMPER_TIMEOUT);

	// TODO: An ugly hack!
	String conf = "/tmp/cassandra.titan";
	this.init(conf);
	
		mapReaderScheduler = Executors.newScheduledThreadPool(1);
		shortestPathReconcileScheduler = Executors.newScheduledThreadPool(1);
    }

    private synchronized long getNextFlowEntryId() {
	//
	// Generate the next Flow Entry ID.
	// NOTE: For now, the higher 32 bits are random, and
	// the lower 32 bits are sequential.
	// In the future, we need a better allocation mechanism.
	//
	if ((nextFlowEntryIdSuffix & 0xffffffffL) == 0xffffffffL) {
	    nextFlowEntryIdPrefix = randomGenerator.nextInt();
	    nextFlowEntryIdSuffix = 0;
	} else {
	    nextFlowEntryIdSuffix++;
	}
	long result = (long)nextFlowEntryIdPrefix << 32;
	result = result | (0xffffffffL & nextFlowEntryIdSuffix);
	return result;
    }

    @Override
    public void startUp(FloodlightModuleContext context) {
		restApi.addRestletRoutable(new FlowWebRoutable());
	
		// Initialize the Flow Entry ID generator
		nextFlowEntryIdPrefix = randomGenerator.nextInt();
		
		mapReaderScheduler.scheduleAtFixedRate(
				mapReader, 3, 3, TimeUnit.SECONDS);
		shortestPathReconcileScheduler.scheduleAtFixedRate(
				shortestPathReconcile, 3, 3, TimeUnit.SECONDS);
    }

    /**
     * Add a flow.
     *
     * Internally, ONOS will automatically register the installer for
     * receiving Flow Path Notifications for that path.
     *
     * @param flowPath the Flow Path to install.
     * @param flowId the return-by-reference Flow ID as assigned internally.
     * @param dataPathSummaryStr the data path summary string if the added
     * flow will be maintained internally, otherwise null.
     * @return true on success, otherwise false.
     */
    @Override
    public boolean addFlow(FlowPath flowPath, FlowId flowId,
			   String dataPathSummaryStr) {
	/*
	 * TODO: Commented-out for now
	if (flowPath.flowId().value() == measurementFlowId) {
	    modifiedMeasurementFlowTime = System.nanoTime();
	}
	*/

	IFlowPath flowObj = null;
	boolean found = false;
	try {
	    if ((flowObj = op.searchFlowPath(flowPath.flowId()))
		!= null) {
		log.debug("Adding FlowPath with FlowId {}: found existing FlowPath",
			  flowPath.flowId().toString());
		found = true;
	    } else {
		flowObj = op.newFlowPath();
		log.debug("Adding FlowPath with FlowId {}: creating new FlowPath",
			  flowPath.flowId().toString());
	    }
	} catch (Exception e) {
	    // TODO: handle exceptions
	    op.rollback();

	    StringWriter sw = new StringWriter();
	    e.printStackTrace(new PrintWriter(sw));
	    String stacktrace = sw.toString();

	    log.error(":addFlow FlowId:{} failed: {}",
		      flowPath.flowId().toString(),
		      stacktrace);
	}
	if (flowObj == null) {
	    log.error(":addFlow FlowId:{} failed: Flow object not created",
		      flowPath.flowId().toString());
	    op.rollback();
	    return false;
	}

	//
	// Set the Flow key:
	// - flowId
	//
	flowObj.setFlowId(flowPath.flowId().toString());
	flowObj.setType("flow");

	//
	// Set the Flow attributes:
	// - flowPath.installerId()
	// - flowPath.flowPathFlags()
	// - flowPath.dataPath().srcPort()
	// - flowPath.dataPath().dstPort()
	// - flowPath.matchSrcMac()
	// - flowPath.matchDstMac()
	// - flowPath.matchEthernetFrameType()
	// - flowPath.matchVlanId()
	// - flowPath.matchVlanPriority()
	// - flowPath.matchSrcIPv4Net()
	// - flowPath.matchDstIPv4Net()
	// - flowPath.matchIpProto()
	// - flowPath.matchIpToS()
	// - flowPath.matchSrcTcpUdpPort()
	// - flowPath.matchDstTcpUdpPort()
	//
	flowObj.setInstallerId(flowPath.installerId().toString());
	flowObj.setFlowPathFlags(flowPath.flowPathFlags().flags());
	flowObj.setSrcSwitch(flowPath.dataPath().srcPort().dpid().toString());
	flowObj.setSrcPort(flowPath.dataPath().srcPort().port().value());
	flowObj.setDstSwitch(flowPath.dataPath().dstPort().dpid().toString());
	flowObj.setDstPort(flowPath.dataPath().dstPort().port().value());
	if (flowPath.flowEntryMatch().matchSrcMac()) {
	    flowObj.setMatchSrcMac(flowPath.flowEntryMatch().srcMac().toString());
	}
	if (flowPath.flowEntryMatch().matchDstMac()) {
	    flowObj.setMatchDstMac(flowPath.flowEntryMatch().dstMac().toString());
	}
	if (flowPath.flowEntryMatch().matchEthernetFrameType()) {
	    flowObj.setMatchEthernetFrameType(flowPath.flowEntryMatch().ethernetFrameType());
	}
	if (flowPath.flowEntryMatch().matchVlanId()) {
	    flowObj.setMatchVlanId(flowPath.flowEntryMatch().vlanId());
	}
	if (flowPath.flowEntryMatch().matchVlanPriority()) {
	    flowObj.setMatchVlanPriority(flowPath.flowEntryMatch().vlanPriority());
	}
	if (flowPath.flowEntryMatch().matchSrcIPv4Net()) {
	    flowObj.setMatchSrcIPv4Net(flowPath.flowEntryMatch().srcIPv4Net().toString());
	}
	if (flowPath.flowEntryMatch().matchDstIPv4Net()) {
	    flowObj.setMatchDstIPv4Net(flowPath.flowEntryMatch().dstIPv4Net().toString());
	}
	if (flowPath.flowEntryMatch().matchIpProto()) {
	    flowObj.setMatchIpProto(flowPath.flowEntryMatch().ipProto());
	}
	if (flowPath.flowEntryMatch().matchIpToS()) {
	    flowObj.setMatchIpToS(flowPath.flowEntryMatch().ipToS());
	}
	if (flowPath.flowEntryMatch().matchSrcTcpUdpPort()) {
	    flowObj.setMatchSrcTcpUdpPort(flowPath.flowEntryMatch().srcTcpUdpPort());
	}
	if (flowPath.flowEntryMatch().matchDstTcpUdpPort()) {
	    flowObj.setMatchDstTcpUdpPort(flowPath.flowEntryMatch().dstTcpUdpPort());
	}

	if (dataPathSummaryStr != null) {
	    flowObj.setDataPathSummary(dataPathSummaryStr);
	} else {
	    flowObj.setDataPathSummary("");
	}

	if (found)
	    flowObj.setUserState("FE_USER_MODIFY");
	else
	    flowObj.setUserState("FE_USER_ADD");

	// Flow edges:
	//   HeadFE


	//
	// Flow Entries:
	// flowPath.dataPath().flowEntries()
	//
	for (FlowEntry flowEntry : flowPath.dataPath().flowEntries()) {
	    if (addFlowEntry(flowObj, flowEntry) == null) {
		op.rollback();
		return false;
	    }
	}
	op.commit();

	//
	// TODO: We need a proper Flow ID allocation mechanism.
	//
	flowId.setValue(flowPath.flowId().value());

	return true;
    }

    /**
     * Add a flow entry to the Network MAP.
     *
     * @param flowObj the corresponding Flow Path object for the Flow Entry.
     * @param flowEntry the Flow Entry to install.
     * @return the added Flow Entry object on success, otherwise null.
     */
    private IFlowEntry addFlowEntry(IFlowPath flowObj, FlowEntry flowEntry) {
	// Flow edges
	//   HeadFE (TODO)

	//
	// Assign the FlowEntry ID.
	//
	if ((flowEntry.flowEntryId() == null) ||
	    (flowEntry.flowEntryId().value() == 0)) {
	    long id = getNextFlowEntryId();
	    flowEntry.setFlowEntryId(new FlowEntryId(id));
	}

	IFlowEntry flowEntryObj = null;
	boolean found = false;
	try {
	    if ((flowEntryObj =
		 op.searchFlowEntry(flowEntry.flowEntryId())) != null) {
		log.debug("Adding FlowEntry with FlowEntryId {}: found existing FlowEntry",
			  flowEntry.flowEntryId().toString());
		found = true;
	    } else {
		flowEntryObj = op.newFlowEntry();
		log.debug("Adding FlowEntry with FlowEntryId {}: creating new FlowEntry",
			  flowEntry.flowEntryId().toString());
	    }
	} catch (Exception e) {
	    log.error(":addFlow FlowEntryId:{} failed",
		      flowEntry.flowEntryId().toString());
	    return null;
	}
	if (flowEntryObj == null) {
	    log.error(":addFlow FlowEntryId:{} failed: FlowEntry object not created",
		      flowEntry.flowEntryId().toString());
	    return null;
	}

	//
	// Set the Flow Entry key:
	// - flowEntry.flowEntryId()
	//
	flowEntryObj.setFlowEntryId(flowEntry.flowEntryId().toString());
	flowEntryObj.setType("flow_entry");

	// 
	// Set the Flow Entry Edges and attributes:
	// - Switch edge
	// - InPort edge
	// - OutPort edge
	//
	// - flowEntry.flowEntryMatch()
	// - flowEntry.flowEntryActions()
	// - flowEntry.dpid()
	// - flowEntry.flowEntryUserState()
	// - flowEntry.flowEntrySwitchState()
	// - flowEntry.flowEntryErrorState()
	// - flowEntry.matchInPort()
	// - flowEntry.matchSrcMac()
	// - flowEntry.matchDstMac()
	// - flowEntry.matchEthernetFrameType()
	// - flowEntry.matchVlanId()
	// - flowEntry.matchVlanPriority()
	// - flowEntry.matchSrcIPv4Net()
	// - flowEntry.matchDstIPv4Net()
	// - flowEntry.matchIpProto()
	// - flowEntry.matchIpToS()
	// - flowEntry.matchSrcTcpUdpPort()
	// - flowEntry.matchDstTcpUdpPort()
	// - flowEntry.actionOutput()
	//
	ISwitchObject sw =
	    op.searchSwitch(flowEntry.dpid().toString());
	flowEntryObj.setSwitchDpid(flowEntry.dpid().toString());
	flowEntryObj.setSwitch(sw);
	if (flowEntry.flowEntryMatch().matchInPort()) {
	    IPortObject inport =
		op.searchPort(flowEntry.dpid().toString(),
					flowEntry.flowEntryMatch().inPort().value());
	    flowEntryObj.setMatchInPort(flowEntry.flowEntryMatch().inPort().value());
	    flowEntryObj.setInPort(inport);
	}
	if (flowEntry.flowEntryMatch().matchSrcMac()) {
	    flowEntryObj.setMatchSrcMac(flowEntry.flowEntryMatch().srcMac().toString());
	}
	if (flowEntry.flowEntryMatch().matchDstMac()) {
	    flowEntryObj.setMatchDstMac(flowEntry.flowEntryMatch().dstMac().toString());
	}
	if (flowEntry.flowEntryMatch().matchEthernetFrameType()) {
	    flowEntryObj.setMatchEthernetFrameType(flowEntry.flowEntryMatch().ethernetFrameType());
	}
	if (flowEntry.flowEntryMatch().matchVlanId()) {
	    flowEntryObj.setMatchVlanId(flowEntry.flowEntryMatch().vlanId());
	}
	if (flowEntry.flowEntryMatch().matchVlanPriority()) {
	    flowEntryObj.setMatchVlanPriority(flowEntry.flowEntryMatch().vlanPriority());
	}
	if (flowEntry.flowEntryMatch().matchSrcIPv4Net()) {
	    flowEntryObj.setMatchSrcIPv4Net(flowEntry.flowEntryMatch().srcIPv4Net().toString());
	}
	if (flowEntry.flowEntryMatch().matchDstIPv4Net()) {
	    flowEntryObj.setMatchDstIPv4Net(flowEntry.flowEntryMatch().dstIPv4Net().toString());
	}
	if (flowEntry.flowEntryMatch().matchIpProto()) {
	    flowEntryObj.setMatchIpProto(flowEntry.flowEntryMatch().ipProto());
	}
	if (flowEntry.flowEntryMatch().matchIpToS()) {
	    flowEntryObj.setMatchIpToS(flowEntry.flowEntryMatch().ipToS());
	}
	if (flowEntry.flowEntryMatch().matchSrcTcpUdpPort()) {
	    flowEntryObj.setMatchSrcTcpUdpPort(flowEntry.flowEntryMatch().srcTcpUdpPort());
	}
	if (flowEntry.flowEntryMatch().matchDstTcpUdpPort()) {
	    flowEntryObj.setMatchDstTcpUdpPort(flowEntry.flowEntryMatch().dstTcpUdpPort());
	}

	for (FlowEntryAction fa : flowEntry.flowEntryActions()) {
	    if (fa.actionOutput() != null) {
		IPortObject outport =
		    op.searchPort(flowEntry.dpid().toString(),
					      fa.actionOutput().port().value());
		flowEntryObj.setActionOutput(fa.actionOutput().port().value());
		flowEntryObj.setOutPort(outport);
	    }
	}
	// TODO: Hacks with hard-coded state names!
	if (found)
	    flowEntryObj.setUserState("FE_USER_MODIFY");
	else
	    flowEntryObj.setUserState("FE_USER_ADD");
	flowEntryObj.setSwitchState("FE_SWITCH_NOT_UPDATED");
	//
	// TODO: Take care of the FlowEntryErrorState.
	//

	// Flow Entries edges:
	//   Flow
	//   NextFE (TODO)
	if (! found) {
	    flowObj.addFlowEntry(flowEntryObj);
	    flowEntryObj.setFlow(flowObj);
	}

	return flowEntryObj;
    }

    /**
     * Delete all previously added flows.
     *
     * @return true on success, otherwise false.
     */
    @Override
    public boolean deleteAllFlows() {
	List<Thread> threads = new LinkedList<Thread>();
	final ConcurrentLinkedQueue<FlowId> concurrentAllFlowIds =
	    new ConcurrentLinkedQueue<FlowId>();

	// Get all Flow IDs
	Iterable<IFlowPath> allFlowPaths = op.getAllFlowPaths();
	for (IFlowPath flowPathObj : allFlowPaths) {
	    if (flowPathObj == null)
		continue;
	    String flowIdStr = flowPathObj.getFlowId();
	    if (flowIdStr == null)
		continue;
	    FlowId flowId = new FlowId(flowIdStr);
	    concurrentAllFlowIds.add(flowId);
	}

	// Delete all flows one-by-one
	for (FlowId flowId : concurrentAllFlowIds)
	    deleteFlow(flowId);

	/*
	 * TODO: A faster mechanism to delete the Flow Paths by using
	 * a number of threads. Commented-out for now.
	 */
	/*
	//
	// Create the threads to delete the Flow Paths
	//
	for (int i = 0; i < 10; i++) {
	    Thread thread = new Thread(new Runnable() {
		@Override
		public void run() {
		    while (true) {
			FlowId flowId = concurrentAllFlowIds.poll();
			if (flowId == null)
			    return;
			deleteFlow(flowId);
		    }
		}}, "Delete All Flow Paths");
	    threads.add(thread);
	}

	// Start processing
	for (Thread thread : threads) {
	    thread.start();
	}

	// Wait for all threads to complete
	for (Thread thread : threads) {
	    try {
		thread.join();
	    } catch (InterruptedException e) {
		log.debug("Exception waiting for a thread to delete a Flow Path: ", e);
	    }
	}
	*/

	return true;
    }

    /**
     * Delete a previously added flow.
     *
     * @param flowId the Flow ID of the flow to delete.
     * @return true on success, otherwise false.
     */
    @Override
    public boolean deleteFlow(FlowId flowId) {
	/*
	 * TODO: Commented-out for now
	if (flowId.value() == measurementFlowId) {
	    modifiedMeasurementFlowTime = System.nanoTime();
	}
	*/

	IFlowPath flowObj = null;
	//
	// We just mark the entries for deletion,
	// and let the switches remove each individual entry after
	// it has been removed from the switches.
	//
	try {
	    if ((flowObj = op.searchFlowPath(flowId))
		!= null) {
		log.debug("Deleting FlowPath with FlowId {}: found existing FlowPath",
			  flowId.toString());
	    } else {
		log.debug("Deleting FlowPath with FlowId {}:  FlowPath not found",
			  flowId.toString());
	    }
	} catch (Exception e) {
	    // TODO: handle exceptions
	    op.rollback();
	    log.error(":deleteFlow FlowId:{} failed", flowId.toString());
	}
	if (flowObj == null) {
	    op.commit();
	    return true;		// OK: No such flow
	}

	//
	// Find and mark for deletion all Flow Entries,
	// and the Flow itself.
	//
	flowObj.setUserState("FE_USER_DELETE");
	Iterable<IFlowEntry> flowEntries = flowObj.getFlowEntries();
	boolean empty = true;	// TODO: an ugly hack
	for (IFlowEntry flowEntryObj : flowEntries) {
	    empty = false;
	    // flowObj.removeFlowEntry(flowEntryObj);
	    // conn.utils().removeFlowEntry(conn, flowEntryObj);
	    flowEntryObj.setUserState("FE_USER_DELETE");
	    flowEntryObj.setSwitchState("FE_SWITCH_NOT_UPDATED");
	}
	// Remove from the database empty flows
	if (empty)
	    op.removeFlowPath(flowObj);
	op.commit();

	return true;
    }

    /**
     * Clear the state for all previously added flows.
     *
     * @return true on success, otherwise false.
     */
    @Override
    public boolean clearAllFlows() {
	List<FlowId> allFlowIds = new LinkedList<FlowId>();

	// Get all Flow IDs
	Iterable<IFlowPath> allFlowPaths = op.getAllFlowPaths();
	for (IFlowPath flowPathObj : allFlowPaths) {
	    if (flowPathObj == null)
		continue;
	    String flowIdStr = flowPathObj.getFlowId();
	    if (flowIdStr == null)
		continue;
	    FlowId flowId = new FlowId(flowIdStr);
	    allFlowIds.add(flowId);
	}

	// Clear all flows one-by-one
	for (FlowId flowId : allFlowIds) {
	    clearFlow(flowId);
	}

	return true;
    }

    /**
     * Clear the state for a previously added flow.
     *
     * @param flowId the Flow ID of the flow to clear.
     * @return true on success, otherwise false.
     */
    @Override
    public boolean clearFlow(FlowId flowId) {
	IFlowPath flowObj = null;
	try {
	    if ((flowObj = op.searchFlowPath(flowId))
		!= null) {
		log.debug("Clearing FlowPath with FlowId {}: found existing FlowPath",
			  flowId.toString());
	    } else {
		log.debug("Clearing FlowPath with FlowId {}:  FlowPath not found",
			  flowId.toString());
	    }
	} catch (Exception e) {
	    // TODO: handle exceptions
	    op.rollback();
	    log.error(":clearFlow FlowId:{} failed", flowId.toString());
	}
	if (flowObj == null) {
	    op.commit();
	    return true;		// OK: No such flow
	}

	//
	// Remove all Flow Entries
	//
	Iterable<IFlowEntry> flowEntries = flowObj.getFlowEntries();
	for (IFlowEntry flowEntryObj : flowEntries) {
	    flowObj.removeFlowEntry(flowEntryObj);
	    op.removeFlowEntry(flowEntryObj);
	}
	// Remove the Flow itself
	op.removeFlowPath(flowObj);
	op.commit();

	return true;
    }

    /**
     * Get a previously added flow.
     *
     * @param flowId the Flow ID of the flow to get.
     * @return the Flow Path if found, otherwise null.
     */
    @Override
    public FlowPath getFlow(FlowId flowId) {
	IFlowPath flowObj = null;
	try {
	    if ((flowObj = op.searchFlowPath(flowId))
		!= null) {
		log.debug("Get FlowPath with FlowId {}: found existing FlowPath",
			  flowId.toString());
	    } else {
		log.debug("Get FlowPath with FlowId {}:  FlowPath not found",
			  flowId.toString());
	    }
	} catch (Exception e) {
	    // TODO: handle exceptions
	    op.rollback();
	    log.error(":getFlow FlowId:{} failed", flowId.toString());
	}
	if (flowObj == null) {
	    op.commit();
	    return null;		// Flow not found
	}

	//
	// Extract the Flow state
	//
	FlowPath flowPath = extractFlowPath(flowObj);
	op.commit();

	return flowPath;
    }

    /**
     * Get all previously added flows by a specific installer for a given
     * data path endpoints.
     *
     * @param installerId the Caller ID of the installer of the flow to get.
     * @param dataPathEndpoints the data path endpoints of the flow to get.
     * @return the Flow Paths if found, otherwise null.
     */
    @Override
    public ArrayList<FlowPath> getAllFlows(CallerId installerId,
					   DataPathEndpoints dataPathEndpoints) {
	//
	// TODO: The implementation below is not optimal:
	// We fetch all flows, and then return only the subset that match
	// the query conditions.
	// We should use the appropriate Titan/Gremlin query to filter-out
	// the flows as appropriate.
	//
	ArrayList<FlowPath> allFlows = getAllFlows();
	ArrayList<FlowPath> flowPaths = new ArrayList<FlowPath>();

	if (allFlows == null) {
	    log.debug("Get FlowPaths for installerId{} and dataPathEndpoints{}: no FlowPaths found", installerId, dataPathEndpoints);
	    return flowPaths;
	}

	for (FlowPath flow : allFlows) {
	    //
	    // TODO: String-based comparison is sub-optimal.
	    // We are using it for now to save us the extra work of
	    // implementing the "equals()" and "hashCode()" methods.
	    //
	    if (! flow.installerId().toString().equals(installerId.toString()))
		continue;
	    if (! flow.dataPath().srcPort().toString().equals(dataPathEndpoints.srcPort().toString())) {
		continue;
	    }
	    if (! flow.dataPath().dstPort().toString().equals(dataPathEndpoints.dstPort().toString())) {
		continue;
	    }
	    flowPaths.add(flow);
	}

	if (flowPaths.isEmpty()) {
	    log.debug("Get FlowPaths for installerId{} and dataPathEndpoints{}: no FlowPaths found", installerId, dataPathEndpoints);
	} else {
	    log.debug("Get FlowPaths for installerId{} and dataPathEndpoints{}: FlowPaths are found", installerId, dataPathEndpoints);
	}

	return flowPaths;
    }

    /**
     * Get all installed flows by all installers for given data path endpoints.
     *
     * @param dataPathEndpoints the data path endpoints of the flows to get.
     * @return the Flow Paths if found, otherwise null.
     */
    @Override
    public ArrayList<FlowPath> getAllFlows(DataPathEndpoints dataPathEndpoints) {
	//
	// TODO: The implementation below is not optimal:
	// We fetch all flows, and then return only the subset that match
	// the query conditions.
	// We should use the appropriate Titan/Gremlin query to filter-out
	// the flows as appropriate.
	//
    ArrayList<FlowPath> flowPaths = new ArrayList<FlowPath>();
    ArrayList<FlowPath> allFlows = getAllFlows();

	if (allFlows == null) {
	    log.debug("Get FlowPaths for dataPathEndpoints{}: no FlowPaths found", dataPathEndpoints);
	    return flowPaths;
	}

	for (FlowPath flow : allFlows) {
	    //
	    // TODO: String-based comparison is sub-optimal.
	    // We are using it for now to save us the extra work of
	    // implementing the "equals()" and "hashCode()" methods.
	    //
	    if (! flow.dataPath().srcPort().toString().equals(dataPathEndpoints.srcPort().toString())) {
		continue;
	    }
	    if (! flow.dataPath().dstPort().toString().equals(dataPathEndpoints.dstPort().toString())) {
		continue;
	    }
	    flowPaths.add(flow);
	}

	if (flowPaths.isEmpty()) {
	    log.debug("Get FlowPaths for dataPathEndpoints{}: no FlowPaths found", dataPathEndpoints);
	} else {
	    log.debug("Get FlowPaths for dataPathEndpoints{}: FlowPaths are found", dataPathEndpoints);
	}

	return flowPaths;
    }

    /**
     * Get summary of all installed flows by all installers in a given range
     *
     * @param flowId the data path endpoints of the flows to get.
     * @param maxFlows: the maximum number of flows to be returned
     * @return the Flow Paths if found, otherwise null.
     */
    @Override
    public ArrayList<IFlowPath> getAllFlowsSummary(FlowId flowId, int maxFlows) {

		// TODO: The implementation below is not optimal:
		// We fetch all flows, and then return only the subset that match
		// the query conditions.
		// We should use the appropriate Titan/Gremlin query to filter-out
		// the flows as appropriate.
		//
    	//ArrayList<FlowPath> flowPaths = new ArrayList<FlowPath>();
    	
    	ArrayList<IFlowPath> flowPathsWithoutFlowEntries = getAllFlowsWithoutFlowEntries();
    	
    	Collections.sort(flowPathsWithoutFlowEntries, 
    			new Comparator<IFlowPath>(){
					@Override
					public int compare(IFlowPath first, IFlowPath second) {
						// TODO Auto-generated method stub
						long result = new FlowId(first.getFlowId()).value()
								- new FlowId(second.getFlowId()).value();
						if (result > 0) return 1;
						else if (result < 0) return -1;
						else return 0;
					}
    			}
    	);
    	
    	return flowPathsWithoutFlowEntries;
    	
    	/*
    	ArrayList<FlowPath> allFlows = getAllFlows();

		if (allFlows == null) {
		    log.debug("Get FlowPathsSummary for {} {}: no FlowPaths found", flowId, maxFlows);
		    return flowPaths;
		}
	
		Collections.sort(allFlows);
		
		for (FlowPath flow : allFlows) {
		    flow.setFlowEntryMatch(null);
			
		    // start from desired flowId
		    if (flow.flowId().value() < flowId.value()) {
			continue;
		    }
			
			// Summarize by making null flow entry fields that are not relevant to report
			for (FlowEntry flowEntry : flow.dataPath().flowEntries()) {
				flowEntry.setFlowEntryActions(null);
				flowEntry.setFlowEntryMatch(null);
			}
			
		    flowPaths.add(flow);
		    if (maxFlows != 0 && flowPaths.size() >= maxFlows) {
		    	break;
		    }
		}
	
		if (flowPaths.isEmpty()) {
		    log.debug("Get FlowPathsSummary {} {}: no FlowPaths found", flowId, maxFlows);
		} else {
		    log.debug("Get FlowPathsSummary for {} {}: FlowPaths were found", flowId, maxFlows);
		}
	
		return flowPaths;
		*/
    }
    
    /**
     * Get all installed flows by all installers.
     *
     * @return the Flow Paths if found, otherwise null.
     */
    @Override
    public ArrayList<FlowPath> getAllFlows() {
	Iterable<IFlowPath> flowPathsObj = null;
	ArrayList<FlowPath> flowPaths = new ArrayList<FlowPath>();

	try {
	    if ((flowPathsObj = op.getAllFlowPaths()) != null) {
		log.debug("Get all FlowPaths: found FlowPaths");
	    } else {
		log.debug("Get all FlowPaths: no FlowPaths found");
	    }
	} catch (Exception e) {
	    // TODO: handle exceptions
	    op.rollback();
	    log.error(":getAllFlowPaths failed");
	}
	if ((flowPathsObj == null) || (flowPathsObj.iterator().hasNext() == false)) {
	    op.commit();
	    return flowPaths;	// No Flows found
	}

	for (IFlowPath flowObj : flowPathsObj) {
	    //
	    // Extract the Flow state
	    //
	    FlowPath flowPath = extractFlowPath(flowObj);
	    if (flowPath != null)
		flowPaths.add(flowPath);
	}

	op.commit();

	return flowPaths;
    }
    
    public ArrayList<IFlowPath> getAllFlowsWithoutFlowEntries(){
    	Iterable<IFlowPath> flowPathsObj = null;
    	ArrayList<IFlowPath> flowPathsObjArray = new ArrayList<IFlowPath>();
    	ArrayList<FlowPath> flowPaths = new ArrayList<FlowPath>();

    	op.commit();
    	
    	try {
    	    if ((flowPathsObj = op.getAllFlowPaths()) != null) {
    		log.debug("Get all FlowPaths: found FlowPaths");
    	    } else {
    		log.debug("Get all FlowPaths: no FlowPaths found");
    	    }
    	} catch (Exception e) {
    	    // TODO: handle exceptions
    	    op.rollback();
    	    log.error(":getAllFlowPaths failed");
    	}
    	if ((flowPathsObj == null) || (flowPathsObj.iterator().hasNext() == false)) {
    	    return new ArrayList<IFlowPath>();	// No Flows found
    	}
    	
    	for (IFlowPath flowObj : flowPathsObj){
    		flowPathsObjArray.add(flowObj);
    	}
    	/*
    	for (IFlowPath flowObj : flowPathsObj) {
    	    //
    	    // Extract the Flow state
    	    //
    	    FlowPath flowPath = extractFlowPath(flowObj);
    	    if (flowPath != null)
    		flowPaths.add(flowPath);
    	}
    	*/

    	//conn.endTx(Transaction.COMMIT);

    	return flowPathsObjArray;
    }

    /**
     * Extract Flow Path State from a Titan Database Object @ref IFlowPath.
     *
     * @param flowObj the object to extract the Flow Path State from.
     * @return the extracted Flow Path State.
     */
    private FlowPath extractFlowPath(IFlowPath flowObj) {
	//
	// Extract the Flow state
	//
	String flowIdStr = flowObj.getFlowId();
	String installerIdStr = flowObj.getInstallerId();
	Long flowPathFlags = flowObj.getFlowPathFlags();
	String srcSwitchStr = flowObj.getSrcSwitch();
	Short srcPortShort = flowObj.getSrcPort();
	String dstSwitchStr = flowObj.getDstSwitch();
	Short dstPortShort = flowObj.getDstPort();

	if ((flowIdStr == null) ||
	    (installerIdStr == null) ||
	    (flowPathFlags == null) ||
	    (srcSwitchStr == null) ||
	    (srcPortShort == null) ||
	    (dstSwitchStr == null) ||
	    (dstPortShort == null)) {
	    // TODO: A work-around, becauuse of some bogus database objects
	    return null;
	}

	FlowPath flowPath = new FlowPath();
	flowPath.setFlowId(new FlowId(flowIdStr));
	flowPath.setInstallerId(new CallerId(installerIdStr));
	flowPath.setFlowPathFlags(new FlowPathFlags(flowPathFlags));
	flowPath.dataPath().srcPort().setDpid(new Dpid(srcSwitchStr));
	flowPath.dataPath().srcPort().setPort(new Port(srcPortShort));
	flowPath.dataPath().dstPort().setDpid(new Dpid(dstSwitchStr));
	flowPath.dataPath().dstPort().setPort(new Port(dstPortShort));
	//
	// Extract the match conditions common for all Flow Entries
	//
	{
	    FlowEntryMatch match = new FlowEntryMatch();
	    String matchSrcMac = flowObj.getMatchSrcMac();
	    if (matchSrcMac != null)
		match.enableSrcMac(MACAddress.valueOf(matchSrcMac));
	    String matchDstMac = flowObj.getMatchDstMac();
	    if (matchDstMac != null)
		match.enableDstMac(MACAddress.valueOf(matchDstMac));
	    Short matchEthernetFrameType = flowObj.getMatchEthernetFrameType();
	    if (matchEthernetFrameType != null)
		match.enableEthernetFrameType(matchEthernetFrameType);
	    Short matchVlanId = flowObj.getMatchVlanId();
	    if (matchVlanId != null)
		match.enableVlanId(matchVlanId);
	    Byte matchVlanPriority = flowObj.getMatchVlanPriority();
	    if (matchVlanPriority != null)
		match.enableVlanPriority(matchVlanPriority);
	    String matchSrcIPv4Net = flowObj.getMatchSrcIPv4Net();
	    if (matchSrcIPv4Net != null)
		match.enableSrcIPv4Net(new IPv4Net(matchSrcIPv4Net));
	    String matchDstIPv4Net = flowObj.getMatchDstIPv4Net();
	    if (matchDstIPv4Net != null)
		match.enableDstIPv4Net(new IPv4Net(matchDstIPv4Net));
	    Byte matchIpProto = flowObj.getMatchIpProto();
	    if (matchIpProto != null)
		match.enableIpProto(matchIpProto);
	    Byte matchIpToS = flowObj.getMatchIpToS();
	    if (matchIpToS != null)
		match.enableIpToS(matchIpToS);
	    Short matchSrcTcpUdpPort = flowObj.getMatchSrcTcpUdpPort();
	    if (matchSrcTcpUdpPort != null)
		match.enableSrcTcpUdpPort(matchSrcTcpUdpPort);
	    Short matchDstTcpUdpPort = flowObj.getMatchDstTcpUdpPort();
	    if (matchDstTcpUdpPort != null)
		match.enableDstTcpUdpPort(matchDstTcpUdpPort);

	    flowPath.setFlowEntryMatch(match);
	}

	//
	// Extract all Flow Entries
	//
	Iterable<IFlowEntry> flowEntries = flowObj.getFlowEntries();
	for (IFlowEntry flowEntryObj : flowEntries) {
	    FlowEntry flowEntry = extractFlowEntry(flowEntryObj);
	    if (flowEntry == null)
		continue;
	    flowPath.dataPath().flowEntries().add(flowEntry);
	}

	return flowPath;
    }

    /**
     * Extract Flow Entry State from a Titan Database Object @ref IFlowEntry.
     *
     * @param flowEntryObj the object to extract the Flow Entry State from.
     * @return the extracted Flow Entry State.
     */
    private FlowEntry extractFlowEntry(IFlowEntry flowEntryObj) {
	String flowEntryIdStr = flowEntryObj.getFlowEntryId();
	String switchDpidStr = flowEntryObj.getSwitchDpid();
	String userState = flowEntryObj.getUserState();
	String switchState = flowEntryObj.getSwitchState();

	if ((flowEntryIdStr == null) ||
	    (switchDpidStr == null) ||
	    (userState == null) ||
	    (switchState == null)) {
	    // TODO: A work-around, becauuse of some bogus database objects
	    return null;
	}

	FlowEntry flowEntry = new FlowEntry();
	flowEntry.setFlowEntryId(new FlowEntryId(flowEntryIdStr));
	flowEntry.setDpid(new Dpid(switchDpidStr));

	//
	// Extract the match conditions
	//
	FlowEntryMatch match = new FlowEntryMatch();
	Short matchInPort = flowEntryObj.getMatchInPort();
	if (matchInPort != null)
	    match.enableInPort(new Port(matchInPort));
	String matchSrcMac = flowEntryObj.getMatchSrcMac();
	if (matchSrcMac != null)
	    match.enableSrcMac(MACAddress.valueOf(matchSrcMac));
	String matchDstMac = flowEntryObj.getMatchDstMac();
	if (matchDstMac != null)
	    match.enableDstMac(MACAddress.valueOf(matchDstMac));
	Short matchEthernetFrameType = flowEntryObj.getMatchEthernetFrameType();
	if (matchEthernetFrameType != null)
	    match.enableEthernetFrameType(matchEthernetFrameType);
	Short matchVlanId = flowEntryObj.getMatchVlanId();
	if (matchVlanId != null)
	    match.enableVlanId(matchVlanId);
	Byte matchVlanPriority = flowEntryObj.getMatchVlanPriority();
	if (matchVlanPriority != null)
	    match.enableVlanPriority(matchVlanPriority);
	String matchSrcIPv4Net = flowEntryObj.getMatchSrcIPv4Net();
	if (matchSrcIPv4Net != null)
	    match.enableSrcIPv4Net(new IPv4Net(matchSrcIPv4Net));
	String matchDstIPv4Net = flowEntryObj.getMatchDstIPv4Net();
	if (matchDstIPv4Net != null)
	    match.enableDstIPv4Net(new IPv4Net(matchDstIPv4Net));
	Byte matchIpProto = flowEntryObj.getMatchIpProto();
	if (matchIpProto != null)
	    match.enableIpProto(matchIpProto);
	Byte matchIpToS = flowEntryObj.getMatchIpToS();
	if (matchIpToS != null)
	    match.enableIpToS(matchIpToS);
	Short matchSrcTcpUdpPort = flowEntryObj.getMatchSrcTcpUdpPort();
	if (matchSrcTcpUdpPort != null)
	    match.enableSrcTcpUdpPort(matchSrcTcpUdpPort);
	Short matchDstTcpUdpPort = flowEntryObj.getMatchDstTcpUdpPort();
	if (matchDstTcpUdpPort != null)
	    match.enableDstTcpUdpPort(matchDstTcpUdpPort);
	flowEntry.setFlowEntryMatch(match);

	//
	// Extract the actions
	//
	ArrayList<FlowEntryAction> actions = new ArrayList<FlowEntryAction>();
	Short actionOutputPort = flowEntryObj.getActionOutput();
	if (actionOutputPort != null) {
	    FlowEntryAction action = new FlowEntryAction();
	    action.setActionOutput(new Port(actionOutputPort));
	    actions.add(action);
	}
	flowEntry.setFlowEntryActions(actions);
	flowEntry.setFlowEntryUserState(FlowEntryUserState.valueOf(userState));
	flowEntry.setFlowEntrySwitchState(FlowEntrySwitchState.valueOf(switchState));
	//
	// TODO: Take care of the FlowEntryMatch, FlowEntryAction set,
	// and FlowEntryErrorState.
	//
	return flowEntry;
    }

    /**
     * Add and maintain a shortest-path flow.
     *
     * NOTE: The Flow Path argument does NOT contain flow entries.
     *
     * @param flowPath the Flow Path with the endpoints and the match
     * conditions to install.
     * @return the added shortest-path flow on success, otherwise null.
     */
    @Override
    public FlowPath addAndMaintainShortestPathFlow(FlowPath flowPath) {
	//
	// Don't do the shortest path computation here.
	// Instead, let the Flow reconciliation thread take care of it.
	//

	// We need the DataPath to populate the Network MAP
	DataPath dataPath = new DataPath();
	dataPath.setSrcPort(flowPath.dataPath().srcPort());
	dataPath.setDstPort(flowPath.dataPath().dstPort());

	//
	// Prepare the computed Flow Path
	//
	FlowPath computedFlowPath = new FlowPath();
	computedFlowPath.setFlowId(new FlowId(flowPath.flowId().value()));
	computedFlowPath.setInstallerId(new CallerId(flowPath.installerId().value()));
	computedFlowPath.setFlowPathFlags(new FlowPathFlags(flowPath.flowPathFlags().flags()));
	computedFlowPath.setDataPath(dataPath);
	computedFlowPath.setFlowEntryMatch(new FlowEntryMatch(flowPath.flowEntryMatch()));

	FlowId flowId = new FlowId();
	String dataPathSummaryStr = dataPath.dataPathSummary();
	if (! addFlow(computedFlowPath, flowId, dataPathSummaryStr))
	    return null;

	// TODO: Mark the flow for maintenance purpose

	return (computedFlowPath);
    }

    /**
     * Reconcile a flow.
     *
     * @param flowObj the flow that needs to be reconciliated.
     * @param newDataPath the new data path to use.
     * @return true on success, otherwise false.
     */
    public boolean reconcileFlow(IFlowPath flowObj, DataPath newDataPath) {
	Map<Long, IOFSwitch> mySwitches = floodlightProvider.getSwitches();

	//
	// Set the incoming port matching and the outgoing port output
	// actions for each flow entry.
	//
	for (FlowEntry flowEntry : newDataPath.flowEntries()) {
	    // Set the incoming port matching
	    FlowEntryMatch flowEntryMatch = new FlowEntryMatch();
	    flowEntry.setFlowEntryMatch(flowEntryMatch);
	    flowEntryMatch.enableInPort(flowEntry.inPort());

	    // Set the outgoing port output action
	    ArrayList<FlowEntryAction> flowEntryActions = flowEntry.flowEntryActions();
	    if (flowEntryActions == null) {
		flowEntryActions = new ArrayList<FlowEntryAction>();
		flowEntry.setFlowEntryActions(flowEntryActions);
	    }
	    FlowEntryAction flowEntryAction = new FlowEntryAction();
	    flowEntryAction.setActionOutput(flowEntry.outPort());
	    flowEntryActions.add(flowEntryAction);
	}

	//
	// Remove the old Flow Entries, and add the new Flow Entries
	//
	Iterable<IFlowEntry> flowEntries = flowObj.getFlowEntries();
	LinkedList<IFlowEntry> deleteFlowEntries = new LinkedList<IFlowEntry>();
	for (IFlowEntry flowEntryObj : flowEntries) {
	    flowEntryObj.setUserState("FE_USER_DELETE");
	    flowEntryObj.setSwitchState("FE_SWITCH_NOT_UPDATED");
	}
	for (FlowEntry flowEntry : newDataPath.flowEntries()) {
	    addFlowEntry(flowObj, flowEntry);
	}

	//
	// Set the Data Path Summary
	//
	String dataPathSummaryStr = newDataPath.dataPathSummary();
	flowObj.setDataPathSummary(dataPathSummaryStr);

	return true;
    }

    /**
     * Reconcile all flows in a set.
     *
     * @param flowObjSet the set of flows that need to be reconciliated.
     */
    public void reconcileFlows(Iterable<IFlowPath> flowObjSet) {
	if (! flowObjSet.iterator().hasNext())
	    return;
	// TODO: Not implemented/used yet.
    }

    /**
     * Install a Flow Entry on a switch.
     *
     * @param mySwitch the switch to install the Flow Entry into.
     * @param flowObj the flow path object for the flow entry to install.
     * @param flowEntryObj the flow entry object to install.
     * @return true on success, otherwise false.
     */
    public boolean installFlowEntry(IOFSwitch mySwitch, IFlowPath flowObj,
				    IFlowEntry flowEntryObj) {
	String flowEntryIdStr = flowEntryObj.getFlowEntryId();
	if (flowEntryIdStr == null)
	    return false;
	FlowEntryId flowEntryId = new FlowEntryId(flowEntryIdStr);
	String userState = flowEntryObj.getUserState();
	if (userState == null)
	    return false;

	//
	// Create the Open Flow Flow Modification Entry to push
	//
	OFFlowMod fm = (OFFlowMod) floodlightProvider.getOFMessageFactory()
	    .getMessage(OFType.FLOW_MOD);
	long cookie = flowEntryId.value();

	short flowModCommand = OFFlowMod.OFPFC_ADD;
	if (userState.equals("FE_USER_ADD")) {
	    flowModCommand = OFFlowMod.OFPFC_ADD;
	} else if (userState.equals("FE_USER_MODIFY")) {
	    flowModCommand = OFFlowMod.OFPFC_MODIFY_STRICT;
	} else if (userState.equals("FE_USER_DELETE")) {
	    flowModCommand = OFFlowMod.OFPFC_DELETE_STRICT;
	} else {
	    // Unknown user state. Ignore the entry
	    log.debug("Flow Entry ignored (FlowEntryId = {}): unknown user state {}",
		      flowEntryId.toString(), userState);
	    return false;
	}

	//
	// Fetch the match conditions.
	//
	// NOTE: The Flow matching conditions common for all Flow Entries are
	// used ONLY if a Flow Entry does NOT have the corresponding matching
	// condition set.
	//
	OFMatch match = new OFMatch();
	match.setWildcards(OFMatch.OFPFW_ALL);

	// Match the Incoming Port
	Short matchInPort = flowEntryObj.getMatchInPort();
	if (matchInPort != null) {
	    match.setInputPort(matchInPort);
	    match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_IN_PORT);
	}

	// Match the Source MAC address
	String matchSrcMac = flowEntryObj.getMatchSrcMac();
	if (matchSrcMac == null)
	    matchSrcMac = flowObj.getMatchSrcMac();
	if (matchSrcMac != null) {
	    match.setDataLayerSource(matchSrcMac);
	    match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_DL_SRC);
	}

	// Match the Destination MAC address
	String matchDstMac = flowEntryObj.getMatchDstMac();
	if (matchDstMac == null)
	    matchDstMac = flowObj.getMatchDstMac();
	if (matchDstMac != null) {
	    match.setDataLayerDestination(matchDstMac);
	    match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_DL_DST);
	}

	// Match the Ethernet Frame Type
	Short matchEthernetFrameType = flowEntryObj.getMatchEthernetFrameType();
	if (matchEthernetFrameType == null)
	    matchEthernetFrameType = flowObj.getMatchEthernetFrameType();
	if (matchEthernetFrameType != null) {
	    match.setDataLayerType(matchEthernetFrameType);
	    match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_DL_TYPE);
	}

	// Match the VLAN ID
	Short matchVlanId = flowEntryObj.getMatchVlanId();
	if (matchVlanId == null)
	    matchVlanId = flowObj.getMatchVlanId();
	if (matchVlanId != null) {
	    match.setDataLayerVirtualLan(matchVlanId);
	    match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_DL_VLAN);
	}

	// Match the VLAN priority
	Byte matchVlanPriority = flowEntryObj.getMatchVlanPriority();
	if (matchVlanPriority == null)
	    matchVlanPriority = flowObj.getMatchVlanPriority();
	if (matchVlanPriority != null) {
	    match.setDataLayerVirtualLanPriorityCodePoint(matchVlanPriority);
	    match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_DL_VLAN_PCP);
	}

	// Match the Source IPv4 Network prefix
	String matchSrcIPv4Net = flowEntryObj.getMatchSrcIPv4Net();
	if (matchSrcIPv4Net == null)
	    matchSrcIPv4Net = flowObj.getMatchSrcIPv4Net();
	if (matchSrcIPv4Net != null) {
	    match.setFromCIDR(matchSrcIPv4Net, OFMatch.STR_NW_SRC);
	}

	// Natch the Destination IPv4 Network prefix
	String matchDstIPv4Net = flowEntryObj.getMatchDstIPv4Net();
	if (matchDstIPv4Net == null)
	    matchDstIPv4Net = flowObj.getMatchDstIPv4Net();
	if (matchDstIPv4Net != null) {
	    match.setFromCIDR(matchDstIPv4Net, OFMatch.STR_NW_DST);
	}

	// Match the IP protocol
	Byte matchIpProto = flowEntryObj.getMatchIpProto();
	if (matchIpProto == null)
	    matchIpProto = flowObj.getMatchIpProto();
	if (matchIpProto != null) {
	    match.setNetworkProtocol(matchIpProto);
	    match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_NW_PROTO);
	}

	// Match the IP ToS (DSCP field, 6 bits)
	Byte matchIpToS = flowEntryObj.getMatchIpToS();
	if (matchIpToS == null)
	    matchIpToS = flowObj.getMatchIpToS();
	if (matchIpToS != null) {
	    match.setNetworkTypeOfService(matchIpToS);
	    match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_NW_TOS);
	}

	// Match the Source TCP/UDP port
	Short matchSrcTcpUdpPort = flowEntryObj.getMatchSrcTcpUdpPort();
	if (matchSrcTcpUdpPort == null)
	    matchSrcTcpUdpPort = flowObj.getMatchSrcTcpUdpPort();
	if (matchSrcTcpUdpPort != null) {
	    match.setTransportSource(matchSrcTcpUdpPort);
	    match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_TP_SRC);
	}

	// Match the Destination TCP/UDP port
	Short matchDstTcpUdpPort = flowEntryObj.getMatchDstTcpUdpPort();
	if (matchDstTcpUdpPort == null)
	    matchDstTcpUdpPort = flowObj.getMatchDstTcpUdpPort();
	if (matchDstTcpUdpPort != null) {
	    match.setTransportDestination(matchDstTcpUdpPort);
	    match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_TP_DST);
	}

	//
	// Fetch the actions
	//
	// TODO: For now we support only the "OUTPUT" actions.
	//
	List<OFAction> actions = new ArrayList<OFAction>();
	Short actionOutputPort = flowEntryObj.getActionOutput();
	if (actionOutputPort != null) {
	    OFActionOutput action = new OFActionOutput();
	    // XXX: The max length is hard-coded for now
	    action.setMaxLength((short)0xffff);
	    action.setPort(actionOutputPort);
	    actions.add(action);
	}

	fm.setIdleTimeout(FLOWMOD_DEFAULT_IDLE_TIMEOUT)
	    .setHardTimeout(FLOWMOD_DEFAULT_HARD_TIMEOUT)
	    .setPriority(PRIORITY_DEFAULT)
	    .setBufferId(OFPacketOut.BUFFER_ID_NONE)
	    .setCookie(cookie)
	    .setCommand(flowModCommand)
	    .setMatch(match)
	    .setActions(actions)
	    .setLengthU(OFFlowMod.MINIMUM_LENGTH+OFActionOutput.MINIMUM_LENGTH);
	fm.setOutPort(OFPort.OFPP_NONE.getValue());
	if ((flowModCommand == OFFlowMod.OFPFC_DELETE) ||
	    (flowModCommand == OFFlowMod.OFPFC_DELETE_STRICT)) {
	    if (actionOutputPort != null)
		fm.setOutPort(actionOutputPort);
	}

	//
	// TODO: Set the following flag
	// fm.setFlags(OFFlowMod.OFPFF_SEND_FLOW_REM);
	// See method ForwardingBase::pushRoute()
	//

	//
	// Write the message to the switch
	//
	log.debug("MEASUREMENT: Installing flow entry " + userState +
		  " into switch DPID: " +
		  mySwitch.getStringId() +
		  " flowEntryId: " + flowEntryId.toString() +
		  " srcMac: " + matchSrcMac + " dstMac: " + matchDstMac +
		  " inPort: " + matchInPort + " outPort: " + actionOutputPort
		  );
	try {
	    messageDamper.write(mySwitch, fm, null);
	    mySwitch.flush();
	    //
	    // TODO: We should use the OpenFlow Barrier mechanism
	    // to check for errors, and update the SwitchState
	    // for a flow entry after the Barrier message is
	    // is received.
	    //
	    flowEntryObj.setSwitchState("FE_SWITCH_UPDATED");
	} catch (IOException e) {
	    log.error("Failure writing flow mod from network map", e);
	    return false;
	}

	return true;
    }

    /**
     * Install a Flow Entry on a switch.
     *
     * @param mySwitch the switch to install the Flow Entry into.
     * @param flowPath the flow path for the flow entry to install.
     * @param flowEntry the flow entry to install.
     * @return true on success, otherwise false.
     */
    public boolean installFlowEntry(IOFSwitch mySwitch, FlowPath flowPath,
				    FlowEntry flowEntry) {
	//
	// Create the OpenFlow Flow Modification Entry to push
	//
	OFFlowMod fm = (OFFlowMod) floodlightProvider.getOFMessageFactory()
	    .getMessage(OFType.FLOW_MOD);
	long cookie = flowEntry.flowEntryId().value();

	short flowModCommand = OFFlowMod.OFPFC_ADD;
	if (flowEntry.flowEntryUserState() == FlowEntryUserState.FE_USER_ADD) {
	    flowModCommand = OFFlowMod.OFPFC_ADD;
	} else if (flowEntry.flowEntryUserState() == FlowEntryUserState.FE_USER_MODIFY) {
	    flowModCommand = OFFlowMod.OFPFC_MODIFY_STRICT;
	} else if (flowEntry.flowEntryUserState() == FlowEntryUserState.FE_USER_DELETE) {
	    flowModCommand = OFFlowMod.OFPFC_DELETE_STRICT;
	} else {
	    // Unknown user state. Ignore the entry
	    log.debug("Flow Entry ignored (FlowEntryId = {}): unknown user state {}",
		      flowEntry.flowEntryId().toString(),
		      flowEntry.flowEntryUserState());
	    return false;
	}

	//
	// Fetch the match conditions.
	//
	// NOTE: The Flow matching conditions common for all Flow Entries are
	// used ONLY if a Flow Entry does NOT have the corresponding matching
	// condition set.
	//
	OFMatch match = new OFMatch();
	match.setWildcards(OFMatch.OFPFW_ALL);
	FlowEntryMatch flowPathMatch = flowPath.flowEntryMatch();
	FlowEntryMatch flowEntryMatch = flowEntry.flowEntryMatch();

	// Match the Incoming Port
	Port matchInPort = flowEntryMatch.inPort();
	if (matchInPort != null) {
	    match.setInputPort(matchInPort.value());
	    match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_IN_PORT);
	}

	// Match the Source MAC address
	MACAddress matchSrcMac = flowEntryMatch.srcMac();
	if ((matchSrcMac == null) && (flowPathMatch != null)) {
	    matchSrcMac = flowPathMatch.srcMac();
	}
	if (matchSrcMac != null) {
	    match.setDataLayerSource(matchSrcMac.toString());
	    match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_DL_SRC);
	}

	// Match the Destination MAC address
	MACAddress matchDstMac = flowEntryMatch.dstMac();
	if ((matchDstMac == null) && (flowPathMatch != null)) {
	    matchDstMac = flowPathMatch.dstMac();
	}
	if (matchDstMac != null) {
	    match.setDataLayerDestination(matchDstMac.toString());
	    match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_DL_DST);
	}

	// Match the Ethernet Frame Type
	Short matchEthernetFrameType = flowEntryMatch.ethernetFrameType();
	if ((matchEthernetFrameType == null) && (flowPathMatch != null)) {
	    matchEthernetFrameType = flowPathMatch.ethernetFrameType();
	}
	if (matchEthernetFrameType != null) {
	    match.setDataLayerType(matchEthernetFrameType);
	    match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_DL_TYPE);
	}

	// Match the VLAN ID
	Short matchVlanId = flowEntryMatch.vlanId();
	if ((matchVlanId == null) && (flowPathMatch != null)) {
	    matchVlanId = flowPathMatch.vlanId();
	}
	if (matchVlanId != null) {
	    match.setDataLayerVirtualLan(matchVlanId);
	    match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_DL_VLAN);
	}

	// Match the VLAN priority
	Byte matchVlanPriority = flowEntryMatch.vlanPriority();
	if ((matchVlanPriority == null) && (flowPathMatch != null)) {
	    matchVlanPriority = flowPathMatch.vlanPriority();
	}
	if (matchVlanPriority != null) {
	    match.setDataLayerVirtualLanPriorityCodePoint(matchVlanPriority);
	    match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_DL_VLAN_PCP);
	}

	// Match the Source IPv4 Network prefix
	IPv4Net matchSrcIPv4Net = flowEntryMatch.srcIPv4Net();
	if ((matchSrcIPv4Net == null) && (flowPathMatch != null)) {
	    matchSrcIPv4Net = flowPathMatch.srcIPv4Net();
	}
	if (matchSrcIPv4Net != null) {
	    match.setFromCIDR(matchSrcIPv4Net.toString(), OFMatch.STR_NW_SRC);
	}

	// Natch the Destination IPv4 Network prefix
	IPv4Net matchDstIPv4Net = flowEntryMatch.dstIPv4Net();
	if ((matchDstIPv4Net == null) && (flowPathMatch != null)) {
	    matchDstIPv4Net = flowPathMatch.dstIPv4Net();
	}
	if (matchDstIPv4Net != null) {
	    match.setFromCIDR(matchDstIPv4Net.toString(), OFMatch.STR_NW_DST);
	}

	// Match the IP protocol
	Byte matchIpProto = flowEntryMatch.ipProto();
	if ((matchIpProto == null) && (flowPathMatch != null)) {
	    matchIpProto = flowPathMatch.ipProto();
	}
	if (matchIpProto != null) {
	    match.setNetworkProtocol(matchIpProto);
	    match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_NW_PROTO);
	}

	// Match the IP ToS (DSCP field, 6 bits)
	Byte matchIpToS = flowEntryMatch.ipToS();
	if ((matchIpToS == null) && (flowPathMatch != null)) {
	    matchIpToS = flowPathMatch.ipToS();
	}
	if (matchIpToS != null) {
	    match.setNetworkTypeOfService(matchIpToS);
	    match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_NW_TOS);
	}

	// Match the Source TCP/UDP port
	Short matchSrcTcpUdpPort = flowEntryMatch.srcTcpUdpPort();
	if ((matchSrcTcpUdpPort == null) && (flowPathMatch != null)) {
	    matchSrcTcpUdpPort = flowPathMatch.srcTcpUdpPort();
	}
	if (matchSrcTcpUdpPort != null) {
	    match.setTransportSource(matchSrcTcpUdpPort);
	    match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_TP_SRC);
	}

	// Match the Destination TCP/UDP port
	Short matchDstTcpUdpPort = flowEntryMatch.dstTcpUdpPort();
	if ((matchDstTcpUdpPort == null) && (flowPathMatch != null)) {
	    matchDstTcpUdpPort = flowPathMatch.dstTcpUdpPort();
	}
	if (matchDstTcpUdpPort != null) {
	    match.setTransportDestination(matchDstTcpUdpPort);
	    match.setWildcards(match.getWildcards() & ~OFMatch.OFPFW_TP_DST);
	}

	//
	// Fetch the actions
	//
	// TODO: For now we support only the "OUTPUT" actions.
	//
	fm.setOutPort(OFPort.OFPP_NONE.getValue());
	List<OFAction> actions = new ArrayList<OFAction>();
	ArrayList<FlowEntryAction> flowEntryActions =
	    flowEntry.flowEntryActions();
	for (FlowEntryAction flowEntryAction : flowEntryActions) {
	    FlowEntryAction.ActionOutput actionOutput =
		flowEntryAction.actionOutput();
	    if (actionOutput != null) {
		short actionOutputPort = actionOutput.port().value();
		OFActionOutput action = new OFActionOutput();
		// XXX: The max length is hard-coded for now
		action.setMaxLength((short)0xffff);
		action.setPort(actionOutputPort);
		actions.add(action);
		if ((flowModCommand == OFFlowMod.OFPFC_DELETE) ||
		    (flowModCommand == OFFlowMod.OFPFC_DELETE_STRICT)) {
		    fm.setOutPort(actionOutputPort);
		}
	    }
	}

	fm.setIdleTimeout(FLOWMOD_DEFAULT_IDLE_TIMEOUT)
	    .setHardTimeout(FLOWMOD_DEFAULT_HARD_TIMEOUT)
	    .setPriority(PRIORITY_DEFAULT)
	    .setBufferId(OFPacketOut.BUFFER_ID_NONE)
	    .setCookie(cookie)
	    .setCommand(flowModCommand)
	    .setMatch(match)
	    .setActions(actions)
	    .setLengthU(OFFlowMod.MINIMUM_LENGTH+OFActionOutput.MINIMUM_LENGTH);

	//
	// TODO: Set the following flag
	// fm.setFlags(OFFlowMod.OFPFF_SEND_FLOW_REM);
	// See method ForwardingBase::pushRoute()
	//

	//
	// Write the message to the switch
	//
	try {
	    messageDamper.write(mySwitch, fm, null);
	    mySwitch.flush();
	    //
	    // TODO: We should use the OpenFlow Barrier mechanism
	    // to check for errors, and update the SwitchState
	    // for a flow entry after the Barrier message is
	    // is received.
	    //
	    // TODO: The FlowEntry Object in Titan should be set
	    // to FE_SWITCH_UPDATED.
	    //
	} catch (IOException e) {
	    log.error("Failure writing flow mod from network map", e);
	    return false;
	}
	return true;
    }

    /**
     * Remove a Flow Entry from a switch.
     *
     * @param mySwitch the switch to remove the Flow Entry from.
     * @param flowPath the flow path for the flow entry to remove.
     * @param flowEntry the flow entry to remove.
     * @return true on success, otherwise false.
     */
    public boolean removeFlowEntry(IOFSwitch mySwitch, FlowPath flowPath,
				   FlowEntry flowEntry) {
	//
	// The installFlowEntry() method implements both installation
	// and removal of flow entries.
	//
	return (installFlowEntry(mySwitch, flowPath, flowEntry));
    }

    /**
     * Install a Flow Entry on a remote controller.
     *
     * TODO: We need it now: Jono
     * - For now it will make a REST call to the remote controller.
     * - Internally, it needs to know the name of the remote controller.
     *
     * @param flowPath the flow path for the flow entry to install.
     * @param flowEntry the flow entry to install.
     * @return true on success, otherwise false.
     */
    public boolean installRemoteFlowEntry(FlowPath flowPath,
					  FlowEntry flowEntry) {
	// TODO: We need it now: Jono
	//  - For now it will make a REST call to the remote controller.
	//  - Internally, it needs to know the name of the remote controller.
	return true;
    }

    /**
     * Remove a flow entry on a remote controller.
     *
     * @param flowPath the flow path for the flow entry to remove.
     * @param flowEntry the flow entry to remove.
     * @return true on success, otherwise false.
     */
    public boolean removeRemoteFlowEntry(FlowPath flowPath,
					 FlowEntry flowEntry) {
	//
	// The installRemoteFlowEntry() method implements both installation
	// and removal of flow entries.
	//
	return (installRemoteFlowEntry(flowPath, flowEntry));
    }

    /**
     * Store a path flow for measurement purpose.
     *
     * NOTE: The Flow Path argument does NOT contain flow entries.
     * The Shortest Path is computed, and the corresponding Flow Entries
     * are stored in the Flow Path.
     *
     * @param flowPath the Flow Path with the endpoints and the match
     * conditions to store.
     * @return the stored shortest-path flow on success, otherwise null.
     */
    @Override
    public synchronized FlowPath measurementStorePathFlow(FlowPath flowPath) {
	//
	// Prepare the Shortest Path computation if the first Flow Path
	//
	if (measurementStoredPaths.isEmpty())
	    measurementShortestPathTopo = topoRouteService.prepareShortestPathTopo();

	//
	// Compute the Shortest Path
	//
	DataPath dataPath =
	    topoRouteService.getTopoShortestPath(measurementShortestPathTopo,
						 flowPath.dataPath().srcPort(),
						 flowPath.dataPath().dstPort());
	if (dataPath == null) {
	    // We need the DataPath to populate the Network MAP
	    dataPath = new DataPath();
	    dataPath.setSrcPort(flowPath.dataPath().srcPort());
	    dataPath.setDstPort(flowPath.dataPath().dstPort());
	}
	dataPath.applyFlowPathFlags(flowPath.flowPathFlags());

	//
	// Set the incoming port matching and the outgoing port output
	// actions for each flow entry.
	//
	for (FlowEntry flowEntry : dataPath.flowEntries()) {
	    // Set the incoming port matching
	    FlowEntryMatch flowEntryMatch = new FlowEntryMatch();
	    flowEntry.setFlowEntryMatch(flowEntryMatch);
	    flowEntryMatch.enableInPort(flowEntry.inPort());

	    // Set the outgoing port output action
	    ArrayList<FlowEntryAction> flowEntryActions = flowEntry.flowEntryActions();
	    if (flowEntryActions == null) {
		flowEntryActions = new ArrayList<FlowEntryAction>();
		flowEntry.setFlowEntryActions(flowEntryActions);
	    }
	    FlowEntryAction flowEntryAction = new FlowEntryAction();
	    flowEntryAction.setActionOutput(flowEntry.outPort());
	    flowEntryActions.add(flowEntryAction);
	}

	//
	// Prepare the computed Flow Path
	//
	FlowPath computedFlowPath = new FlowPath();
	computedFlowPath.setFlowId(new FlowId(flowPath.flowId().value()));
	computedFlowPath.setInstallerId(new CallerId(flowPath.installerId().value()));
	computedFlowPath.setFlowPathFlags(new FlowPathFlags(flowPath.flowPathFlags().flags()));
	computedFlowPath.setDataPath(dataPath);
	computedFlowPath.setFlowEntryMatch(new FlowEntryMatch(flowPath.flowEntryMatch()));

	//
	// Add the computed Flow Path to the internal storage
	//
	measurementStoredPaths.add(computedFlowPath);

	log.debug("Measurement storing path {}",
		  computedFlowPath.flowId().toString());

	return (computedFlowPath);
    }

    /**
     * Install path flows for measurement purpose.
     *
     * @param numThreads the number of threads to use to install the path
     * flows.
     * @return true on success, otherwise false.
     */
    @Override
    public boolean measurementInstallPaths(Integer numThreads) {
	// Create a copy of the Flow Paths to install
	final ConcurrentLinkedQueue<FlowPath> measurementProcessingPaths =
	    new ConcurrentLinkedQueue<FlowPath>(measurementStoredPaths);

	/**
	 * A Thread-wrapper class for executing the threads and collecting
	 * the measurement data.
	 */
	class MyThread extends Thread {
	    public long[] execTime = new long[2000];
	    public int samples = 0;
	    public int threadId = -1;
	    @Override
	    public void run() {
		while (true) {
		    FlowPath flowPath = measurementProcessingPaths.poll();
		    if (flowPath == null)
			return;
		    // Install the Flow Path
		    FlowId flowId = new FlowId();
		    String dataPathSummaryStr =
			flowPath.dataPath().dataPathSummary();
		    long startTime = System.nanoTime();
		    addFlow(flowPath, flowId, dataPathSummaryStr);
		    long endTime = System.nanoTime();
		    execTime[samples] = endTime - startTime;
		    samples++;
		}
	    }
	};

	List<MyThread> threads = new LinkedList<MyThread>();

	log.debug("Measurement Installing {} flows",
		  measurementProcessingPaths.size());

	//
	// Create the threads to install the Flow Paths
	//
	for (int i = 0; i < numThreads; i++) {
	    MyThread thread = new MyThread();
	    thread.threadId = i;
	    threads.add(thread);
	}

	//
	// Start processing
	//
	measurementEndTimeProcessingPaths = 0;
	measurementStartTimeProcessingPaths = System.nanoTime();
	for (Thread thread : threads) {
	    thread.start();
	}

	// Wait for all threads to complete
	for (Thread thread : threads) {
	    try {
		thread.join();
	    } catch (InterruptedException e) {
		log.debug("Exception waiting for a thread to install a Flow Path: ", e);
	    }
	}

	// Record the end of processing
	measurementEndTimeProcessingPaths = System.nanoTime();

	//
	// Prepare the string with measurement data per each Flow Path
	// installation.
	// The string is multiple lines: one line per Flow Path installation:
	//    ThreadAndTimePerFlow <ThreadId> <TotalThreads> <Time(ns)>
	//
	measurementPerFlowStr = new String();
	String eol = System.getProperty("line.separator");
	for (MyThread thread : threads) {
	    for (int i = 0; i < thread.samples; i++) {
		measurementPerFlowStr += "ThreadAndTimePerFlow " + thread.threadId + " " + numThreads + " " + thread.execTime[i] + eol;
	    }
	}

	return true;
    }

    /**
     * Get the measurement time that took to install the path flows.
     *
     * @return the measurement time (in nanoseconds) it took to install
     * the path flows.
     */
    @Override
    public Long measurementGetInstallPathsTimeNsec() {
	return new Long(measurementEndTimeProcessingPaths -
			measurementStartTimeProcessingPaths);
    }

    /**
     * Get the measurement install time per Flow.
     *
     * @return a multi-line string with the following format per line:
     * ThreadAndTimePerFlow <ThreadId> <TotalThreads> <Time(ns)>
     */
    @Override
    public String measurementGetPerFlowInstallTime() {
	return new String(measurementPerFlowStr);
    }

    /**
     * Clear the path flows stored for measurement purpose.
     *
     * @return true on success, otherwise false.
     */
    @Override
    public boolean measurementClearAllPaths() {
	measurementStoredPaths.clear();
	topoRouteService.dropShortestPathTopo(measurementShortestPathTopo);
	measurementStartTimeProcessingPaths = 0;
	measurementEndTimeProcessingPaths = 0;
	measurementPerFlowStr = new String();

	return true;
    }
}
