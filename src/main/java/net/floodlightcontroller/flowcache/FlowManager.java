package net.floodlightcontroller.flowcache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.INetMapStorage;
import net.floodlightcontroller.core.INetMapTopologyObjects.IFlowEntry;
import net.floodlightcontroller.core.INetMapTopologyObjects.IFlowPath;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.flowcache.IFlowService;
import net.floodlightcontroller.flowcache.web.FlowWebRoutable;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.util.CallerId;
import net.floodlightcontroller.util.DataPath;
import net.floodlightcontroller.util.Dpid;
import net.floodlightcontroller.util.DataPathEndpoints;
import net.floodlightcontroller.util.FlowEntry;
import net.floodlightcontroller.util.FlowEntryId;
import net.floodlightcontroller.util.FlowEntrySwitchState;
import net.floodlightcontroller.util.FlowEntryUserState;
import net.floodlightcontroller.util.FlowId;
import net.floodlightcontroller.util.FlowPath;
import net.floodlightcontroller.util.OFMessageDamper;
import net.floodlightcontroller.util.Port;
import net.onrc.onos.util.GraphDBConnection;
import net.onrc.onos.util.GraphDBConnection.Transaction;

import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.OFMatch;
import org.openflow.protocol.OFPacketOut;
import org.openflow.protocol.OFType;
import org.openflow.protocol.action.OFAction;
import org.openflow.protocol.action.OFActionOutput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlowManager implements IFloodlightModule, IFlowService, INetMapStorage {

    public GraphDBConnection conn;

    protected IRestApiService restApi;
    protected IFloodlightProviderService floodlightProvider;

    protected OFMessageDamper messageDamper;

    protected static int OFMESSAGE_DAMPER_CAPACITY = 50000; // TODO: find sweet spot
    protected static int OFMESSAGE_DAMPER_TIMEOUT = 250;	// ms
    public static short FLOWMOD_DEFAULT_IDLE_TIMEOUT = 0;	// infinity
    public static short FLOWMOD_DEFAULT_HARD_TIMEOUT = 0;	// infinite

    /** The logger. */
    private static Logger log = LoggerFactory.getLogger(FlowManager.class);

    // The periodic task(s)
    private final ScheduledExecutorService scheduler =
	Executors.newScheduledThreadPool(1);
    final Runnable reader = new Runnable() {
	    public void run() {
		// log.debug("Reading Flow Entries from the Network Map...");
		if (floodlightProvider == null) {
		    log.debug("FloodlightProvider service not found!");
		    return;
		}

		Map<Long, IOFSwitch> mySwitches = floodlightProvider.getSwitches();

		// Fetch all Flow Entries
		Iterable<IFlowEntry> flowEntries = conn.utils().getAllFlowEntries(conn);
		for (IFlowEntry flowEntryObj : flowEntries) {
		    FlowEntryId flowEntryId =
			new FlowEntryId(flowEntryObj.getFlowEntryId());
		    String userState = flowEntryObj.getUserState();
		    String switchState = flowEntryObj.getSwitchState();

		    log.debug("Found Flow Entry {}: ", flowEntryId.toString());
		    log.debug("User State {}:", userState);
		    log.debug("Switch State {}:", switchState);

		    if (! switchState.equals("FE_SWITCH_NOT_UPDATED")) {
			// Ignore the entry: nothing to do
			continue;
		    }

		    Dpid dpid = new Dpid(flowEntryObj.getSwitchDpid());
		    IOFSwitch mySwitch = mySwitches.get(dpid.value());
		    if (mySwitch == null) {
			log.debug("Flow Entry ignored: not my switch");
			continue;
		    }

		    //
		    // Create the Open Flow Flow Modification Entry to push
		    //
		    OFFlowMod fm =
			(OFFlowMod) floodlightProvider.getOFMessageFactory()
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
			continue;
		    }

		    OFMatch match = new OFMatch();
		    match.setInputPort(flowEntryObj.getInPort());

		    OFActionOutput action = new OFActionOutput();
		    action.setMaxLength((short)0xffff);
		    action.setPort(flowEntryObj.getOutPort());
		    List<OFAction> actions = new ArrayList<OFAction>();
		    actions.add(action);

		    fm.setIdleTimeout(FLOWMOD_DEFAULT_IDLE_TIMEOUT)
			.setHardTimeout(FLOWMOD_DEFAULT_HARD_TIMEOUT)
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
		    try {
			messageDamper.write(mySwitch, fm, null);
			mySwitch.flush();
			flowEntryObj.setSwitchState("FE_SWITCH_UPDATED");
			if (userState.equals("FE_USER_DELETE")) {
			    // Delete the entry
			    IFlowPath flowObj = null;
			    flowObj = conn.utils().getFlowPathByFlowEntry(conn,
					flowEntryObj);
			    if (flowObj != null)
				log.debug("Found FlowPath to be deleted");
			    else
				log.debug("Did not find FlowPath to be deleted");
			    flowObj.removeFlowEntry(flowEntryObj);
			    conn.utils().removeFlowEntry(conn, flowEntryObj);

			    // Test whether the last flow entry
			    Iterable<IFlowEntry> tmpflowEntries =
				flowObj.getFlowEntries();
			    boolean found = false;
			    for (IFlowEntry tmpflowEntryObj : tmpflowEntries) {
				found = true;
				break;
			    }
			    if (! found) {
				// Remove the Flow Path as well
				conn.utils().removeFlowPath(conn, flowObj);
			    }
			}
		    } catch (IOException e) {
			log.error("Failure writing flow mod from network map", e);
		    }
		}
		conn.endTx(Transaction.COMMIT);
	    }
	};
    final ScheduledFuture<?> readerHandle =
	scheduler.scheduleAtFixedRate(reader, 3, 3, TimeUnit.SECONDS);

    @Override
    public void init(String conf) {
	conn = GraphDBConnection.getInstance(conf);
    }

    public void finalize() {
	close();
    }

    @Override
    public void close() {
	conn.close();
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
	floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
	restApi = context.getServiceImpl(IRestApiService.class);
	messageDamper = new OFMessageDamper(OFMESSAGE_DAMPER_CAPACITY,
					    EnumSet.of(OFType.FLOW_MOD),
					    OFMESSAGE_DAMPER_TIMEOUT);
	// TODO: An ugly hack!
	String conf = "/tmp/cassandra.titan";
	this.init(conf);
    }

    @Override
    public void startUp(FloodlightModuleContext context) {
	restApi.addRestletRoutable(new FlowWebRoutable());
    }

    /**
     * Add a flow.
     *
     * Internally, ONOS will automatically register the installer for
     * receiving Flow Path Notifications for that path.
     *
     * @param flowPath the Flow Path to install.
     * @param flowId the return-by-reference Flow ID as assigned internally.
     * @return true on success, otherwise false.
     */
    @Override
    public boolean addFlow(FlowPath flowPath, FlowId flowId) {

	//
	// Assign the FlowEntry IDs
	// TODO: This is an ugly hack!
	// The Flow Entry IDs are set to 1000*FlowId + Index
	//
	int i = 1;
	for (FlowEntry flowEntry : flowPath.dataPath().flowEntries()) {
	    long id = flowPath.flowId().value() * 1000 + i;
	    ++i;
	    flowEntry.setFlowEntryId(new FlowEntryId(id));
	}

	IFlowPath flowObj = null;
	try {
	    if ((flowObj = conn.utils().searchFlowPath(conn, flowPath.flowId()))
		!= null) {
		log.debug("Adding FlowPath with FlowId {}: found existing FlowPath",
			  flowPath.flowId().toString());
	    } else {
		flowObj = conn.utils().newFlowPath(conn);
		log.debug("Adding FlowPath with FlowId {}: creating new FlowPath",
			  flowPath.flowId().toString());
	    }
	} catch (Exception e) {
	    // TODO: handle exceptions
	    conn.endTx(Transaction.ROLLBACK);
	    log.error(":addFlow FlowId:{} failed",
		      flowPath.flowId().toString());
	}
	if (flowObj == null)
	    return false;

	//
	// Set the Flow key:
	// - flowId
	//
	flowObj.setFlowId(flowPath.flowId().toString());
	flowObj.setType("flow");

	//
	// Set the Flow attributes:
	// - flowPath.installerId()
	// - flowPath.dataPath().srcPort()
	// - flowPath.dataPath().dstPort()
	//
	flowObj.setInstallerId(flowPath.installerId().toString());
	flowObj.setSrcSwitch(flowPath.dataPath().srcPort().dpid().toString());
	flowObj.setSrcPort(flowPath.dataPath().srcPort().port().value());
	flowObj.setDstSwitch(flowPath.dataPath().dstPort().dpid().toString());
	flowObj.setDstPort(flowPath.dataPath().dstPort().port().value());

	// Flow edges:
	//   HeadFE


	//
	// Flow Entries:
	// flowPath.dataPath().flowEntries()
	//
	for (FlowEntry flowEntry : flowPath.dataPath().flowEntries()) {
	    IFlowEntry flowEntryObj = null;
	    boolean found = false;
	    try {
		if ((flowEntryObj = conn.utils().searchFlowEntry(conn, flowEntry.flowEntryId())) != null) {
		    log.debug("Adding FlowEntry with FlowEntryId {}: found existing FlowEntry",
			      flowEntry.flowEntryId().toString());
		    found = true;
		} else {
		    flowEntryObj = conn.utils().newFlowEntry(conn);
		    log.debug("Adding FlowEntry with FlowEntryId {}: creating new FlowEntry",
			      flowEntry.flowEntryId().toString());
		}
	    } catch (Exception e) {
		// TODO: handle exceptions
		conn.endTx(Transaction.ROLLBACK);
		log.error(":addFlow FlowEntryId:{} failed",
			  flowEntry.flowEntryId().toString());
	    }
	    if (flowEntryObj == null)
		return false;

	    //
	    // Set the Flow Entry key:
	    // - flowEntry.flowEntryId()
	    //
	    flowEntryObj.setFlowEntryId(flowEntry.flowEntryId().toString());
	    flowEntryObj.setType("flow_entry");

	    // 
	    // Set the Flow Entry attributes:
	    // - flowEntry.flowEntryMatch()
	    // - flowEntry.flowEntryActions()
	    // - flowEntry.dpid()
	    // - flowEntry.inPort()
	    // - flowEntry.outPort()
	    // - flowEntry.flowEntryUserState()
	    // - flowEntry.flowEntrySwitchState()
	    // - flowEntry.flowEntryErrorState()
	    //
	    flowEntryObj.setSwitchDpid(flowEntry.dpid().toString());
	    flowEntryObj.setInPort(flowEntry.inPort().value());
	    flowEntryObj.setOutPort(flowEntry.outPort().value());
	    // TODO: Hacks with hard-coded state names!
	    if (found)
		flowEntryObj.setUserState("FE_USER_MODIFY");
	    else
		flowEntryObj.setUserState("FE_USER_ADD");
	    flowEntryObj.setSwitchState("FE_SWITCH_NOT_UPDATED");
	    //
	    // TODO: Take care of the FlowEntryMatch, FlowEntryActions,
	    // and FlowEntryErrorState.
	    //

	    // Flow Entries edges:
	    //   Flow
	    //   NextFE
	    //   InPort
	    //   OutPort
	    //   Switch
	    if (! found)
		flowObj.addFlowEntry(flowEntryObj);
	}
	conn.endTx(Transaction.COMMIT);

	//
	// TODO: We need a proper Flow ID allocation mechanism.
	//
	flowId.setValue(flowPath.flowId().value());
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
	IFlowPath flowObj = null;
	//
	// We just mark the entries for deletion,
	// and let the switches remove each individual entry after
	// it has been removed from the switches.
	//
	try {
	    if ((flowObj = conn.utils().searchFlowPath(conn, flowId))
		!= null) {
		log.debug("Deleting FlowPath with FlowId {}: found existing FlowPath",
			  flowId.toString());
	    } else {
		log.debug("Deleting FlowPath with FlowId {}:  FlowPath not found",
			  flowId.toString());
	    }
	} catch (Exception e) {
	    // TODO: handle exceptions
	    conn.endTx(Transaction.ROLLBACK);
	    log.error(":deleteFlow FlowId:{} failed", flowId.toString());
	}
	if (flowObj == null)
	    return true;		// OK: No such flow

	//
	// Find and mark for deletion all Flow Entries
	//
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
	    conn.utils().removeFlowPath(conn, flowObj);
	conn.endTx(Transaction.COMMIT);

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
	    if ((flowObj = conn.utils().searchFlowPath(conn, flowId))
		!= null) {
		log.debug("Get FlowPath with FlowId {}: found existing FlowPath",
			  flowId.toString());
	    } else {
		log.debug("Get FlowPath with FlowId {}:  FlowPath not found",
			  flowId.toString());
	    }
	} catch (Exception e) {
	    // TODO: handle exceptions
	    conn.endTx(Transaction.ROLLBACK);
	    log.error(":getFlow FlowId:{} failed", flowId.toString());
	}
	if (flowObj == null)
	    return null;		// Flow not found

	//
	// Extract the Flow state
	//
	FlowPath flowPath = new FlowPath();
	flowPath.setFlowId(new FlowId(flowObj.getFlowId()));
	flowPath.setInstallerId(new CallerId(flowObj.getInstallerId()));
	flowPath.dataPath().srcPort().setDpid(new Dpid(flowObj.getSrcSwitch()));
	flowPath.dataPath().srcPort().setPort(new Port(flowObj.getSrcPort()));
	flowPath.dataPath().dstPort().setDpid(new Dpid(flowObj.getDstSwitch()));
	flowPath.dataPath().dstPort().setPort(new Port(flowObj.getDstPort()));

	//
	// Extract all Flow Entries
	//
	Iterable<IFlowEntry> flowEntries = flowObj.getFlowEntries();
	for (IFlowEntry flowEntryObj : flowEntries) {
	    FlowEntry flowEntry = new FlowEntry();
	    flowEntry.setFlowEntryId(new FlowEntryId(flowEntryObj.getFlowEntryId()));
	    flowEntry.setDpid(new Dpid(flowEntryObj.getSwitchDpid()));
	    flowEntry.setInPort(new Port(flowEntryObj.getInPort()));
	    flowEntry.setOutPort(new Port(flowEntryObj.getOutPort()));
	    String userState = flowEntryObj.getUserState();
	    flowEntry.setFlowEntryUserState(FlowEntryUserState.valueOf(userState));
	    String switchState = flowEntryObj.getSwitchState();
	    flowEntry.setFlowEntrySwitchState(FlowEntrySwitchState.valueOf(switchState));
	    //
	    // TODO: Take care of the FlowEntryMatch, FlowEntryActions,
	    // and FlowEntryErrorState.
	    //
	    flowPath.dataPath().flowEntries().add(flowEntry);
	}
	conn.endTx(Transaction.COMMIT);

	return flowPath;
    }

    /**
     * Get a previously added flow by a specific installer for given
     * data path endpoints.
     *
     * @param installerId the Caller ID of the installer of the flow to get.
     * @param dataPathEndpoints the data path endpoints of the flow to get.
     * @return the Flow Path if found, otherwise null.
     */
    @Override
    public FlowPath getFlow(CallerId installerId,
			    DataPathEndpoints dataPathEndpoints) {
	// TODO
	return null;
    }

    /**
     * Get all installed flows by all installers for given data path endpoints.
     *
     * @param dataPathEndpoints the data path endpoints of the flows to get.
     * @return the Flow Paths if found, otherwise null.
     */
    @Override
    public ArrayList<FlowPath> getAllFlows(DataPathEndpoints dataPathEndpoints) {
	// TODO
	return null;
    }

    /**
     * Get all installed flows by all installers.
     *
     * @return the Flow Paths if found, otherwise null.
     */
    @Override
    public ArrayList<FlowPath> getAllFlows() {
	// TODO
	return null;
    }
}
