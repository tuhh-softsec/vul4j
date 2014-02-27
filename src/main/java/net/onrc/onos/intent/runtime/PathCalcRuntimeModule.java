package net.onrc.onos.intent.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.onrc.onos.datagrid.IDatagridService;
import net.onrc.onos.datagrid.IEventChannel;
import net.onrc.onos.datagrid.IEventChannelListener;
import net.onrc.onos.intent.Intent;
import net.onrc.onos.intent.Intent.IntentState;
import net.onrc.onos.intent.IntentMap;
import net.onrc.onos.intent.IntentOperation;
import net.onrc.onos.intent.IntentOperation.Operator;
import net.onrc.onos.intent.IntentOperationList;
import net.onrc.onos.intent.PathIntent;
import net.onrc.onos.intent.PathIntentMap;
import net.onrc.onos.intent.ShortestPathIntent;
import net.onrc.onos.intent.persist.PersistIntent;
import net.onrc.onos.ofcontroller.networkgraph.DeviceEvent;
import net.onrc.onos.ofcontroller.networkgraph.INetworkGraphListener;
import net.onrc.onos.ofcontroller.networkgraph.INetworkGraphService;
import net.onrc.onos.ofcontroller.networkgraph.LinkEvent;
import net.onrc.onos.ofcontroller.networkgraph.PortEvent;
import net.onrc.onos.ofcontroller.networkgraph.SwitchEvent;
import net.onrc.onos.registry.controller.IControllerRegistryService;

/**
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class PathCalcRuntimeModule implements IFloodlightModule, IPathCalcRuntimeService, INetworkGraphListener, IEventChannelListener<Long, IntentStateList> {
	class PerfLog {
		private String step;
		private long time;

		public PerfLog(String step) {
			this.step = step;
			this.time = System.nanoTime();
		}

		public void logThis() {
			log.error("Time:{}, Step:{}", time, step);
		}
	}
	class PerfLogger {
		private LinkedList<PerfLog> logData = new LinkedList<>();

		public PerfLogger(String logPhase) {
			log("start_" + logPhase);
		}

		public void log(String step) {
			logData.add(new PerfLog(step));
		}

		public void flushLog() {
			log("finish");
			for (PerfLog log: logData) {
				log.logThis();
			}
			logData.clear();
		}
	}
	private PathCalcRuntime runtime;
	private IDatagridService datagridService;
	private INetworkGraphService networkGraphService;
	private IntentMap highLevelIntents;
	private PathIntentMap pathIntents;
	private IControllerRegistryService controllerRegistry;
	private PersistIntent persistIntent;

	private IEventChannel<Long, IntentOperationList> opEventChannel;
	private final ReentrantLock lock = new ReentrantLock();
	private HashSet<LinkEvent> unmatchedLinkEvents = new HashSet<>();
	private static final String INTENT_OP_EVENT_CHANNEL_NAME = "onos.pathintent";
	private static final String INTENT_STATE_EVENT_CHANNEL_NAME = "onos.pathintent_state";
	private static final Logger log = LoggerFactory.getLogger(PathCalcRuntimeModule.class);

	// ================================================================================
	// private methods
	// ================================================================================

	private void reroutePaths(Collection<Intent> oldPaths) {
		if (oldPaths == null || oldPaths.isEmpty())
			return;

		IntentOperationList reroutingOperation = new IntentOperationList();
		for (Intent intent : oldPaths) {
			PathIntent pathIntent = (PathIntent) intent;
			if (pathIntent.getState().equals(IntentState.INST_ACK) && // XXX: path intents in flight
					!reroutingOperation.contains(pathIntent.getParentIntent())) { 
				reroutingOperation.add(Operator.ADD, pathIntent.getParentIntent());
			}
		}
		executeIntentOperations(reroutingOperation);
	}


	// ================================================================================
	// IFloodlightModule implementations
	// ================================================================================

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<>(1);
		l.add(IPathCalcRuntimeService.class);
		return l;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		Map<Class<? extends IFloodlightService>, IFloodlightService> m = new HashMap<>();
		m.put(IPathCalcRuntimeService.class, this);
		return m;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<>(2);
		l.add(IDatagridService.class);
		l.add(INetworkGraphService.class);
		return l;
	}

	@Override
	public void init(FloodlightModuleContext context) throws FloodlightModuleException {
		datagridService = context.getServiceImpl(IDatagridService.class);
		networkGraphService = context.getServiceImpl(INetworkGraphService.class);
		controllerRegistry = context.getServiceImpl(IControllerRegistryService.class);
	}

	@Override
	public void startUp(FloodlightModuleContext context) {
		highLevelIntents = new IntentMap();
		runtime = new PathCalcRuntime(networkGraphService.getNetworkGraph());
		pathIntents = new PathIntentMap();
		opEventChannel = datagridService.createChannel(INTENT_OP_EVENT_CHANNEL_NAME, Long.class, IntentOperationList.class);
		datagridService.addListener(INTENT_STATE_EVENT_CHANNEL_NAME, this, Long.class, IntentStateList.class);
		networkGraphService.registerNetworkGraphListener(this);
		persistIntent = new PersistIntent(controllerRegistry, networkGraphService);
	}

	// ================================================================================
	// IPathCalcRuntimeService implementations
	// ================================================================================

	@Override
	public IntentOperationList executeIntentOperations(IntentOperationList list) {
		if (list == null || list.size() == 0)
			return null;
		PerfLogger p = new PerfLogger("executeIntentOperations_" + list.get(0).operator);

		lock.lock(); // TODO optimize locking using smaller steps
		try {
			// update the map of high-level intents
			p.log("begin_updateInMemoryIntents");
			highLevelIntents.executeOperations(list);

			// change states of high-level intents
			IntentStateList states = new IntentStateList();
			for (IntentOperation op : list) {
				if (op.intent.getState().equals(IntentState.INST_ACK))
					states.put(op.intent.getId(), IntentState.REROUTE_REQ);
			}
			highLevelIntents.changeStates(states);
			p.log("end_updateInMemoryIntents");

			// calculate path-intents (low-level operations)
			p.log("begin_calcPathIntents");
			IntentOperationList pathIntentOperations = runtime.calcPathIntents(list, highLevelIntents, pathIntents);
			p.log("end_calcPathIntents");

			// persist calculated low-level operations into data store
			p.log("begin_persistPathIntents");
			long key = persistIntent.getKey();
			persistIntent.persistIfLeader(key, pathIntentOperations);
			p.log("end_persistPathIntents");

			// remove error-intents and reflect them to high-level intents
			p.log("begin_removeErrorIntents");
			states.clear();
			Iterator<IntentOperation> i = pathIntentOperations.iterator();
			while (i.hasNext()) {
				IntentOperation op = i.next();
				if (op.operator.equals(Operator.ERROR)) {
					states.put(op.intent.getId(), IntentState.INST_NACK);
					i.remove();
				}
			}
			highLevelIntents.changeStates(states);
			p.log("end_removeErrorIntents");

			// update the map of path intents and publish the path operations
			p.log("begin_updateInMemoryPathIntents");
			pathIntents.executeOperations(pathIntentOperations);
			p.log("end_updateInMemoryPathIntents");

			// Demo special: add a complete path to remove operation
			p.log("begin_addPathToRemoveOperation");
			for (IntentOperation op: pathIntentOperations) {
				if(op.operator.equals(Operator.REMOVE)) {
					op.intent = pathIntents.getIntent(op.intent.getId());
				}
				if (op.intent instanceof PathIntent) {
					log.debug("operation: {}, intent:{}", op.operator, op.intent);
				}
			}
			p.log("end_addPathToRemoveOperation");

			// send notification
			p.log("begin_sendNotification");
			opEventChannel.addEntry(key, pathIntentOperations);
			p.log("end_sendNotification");
			opEventChannel.removeEntry(key);
			return pathIntentOperations;
		}
		finally {
			p.flushLog();
			lock.unlock();
		}
	}

	@Override
	public IntentMap getHighLevelIntents() {
		return highLevelIntents;
	}

	@Override
	public IntentMap getPathIntents() {
		return pathIntents;
	}

	@Override
	public void purgeIntents() {
		highLevelIntents.purge();
		pathIntents.purge();
	}

	// ================================================================================
	// INetworkGraphListener implementations
	// ================================================================================

	@Override
	public void networkGraphEvents(Collection<SwitchEvent> addedSwitchEvents,
			Collection<SwitchEvent> removedSwitchEvents,
			Collection<PortEvent> addedPortEvents,
			Collection<PortEvent> removedPortEvents,
			Collection<LinkEvent> addedLinkEvents,
			Collection<LinkEvent> removedLinkEvents,
			Collection<DeviceEvent> addedDeviceEvents,
			Collection<DeviceEvent> removedDeviceEvents) {

		PerfLogger p = new PerfLogger("networkGraphEvents");
		HashSet<Intent> affectedPaths = new HashSet<>();
		
		boolean rerouteAll = false;
		for(LinkEvent le : addedLinkEvents) {
		    LinkEvent rev = new LinkEvent(le.getDst().getDpid(), le.getDst().getNumber(), le.getSrc().getDpid(), le.getSrc().getNumber());
		    if(unmatchedLinkEvents.contains(rev)) {
			rerouteAll = true;
			unmatchedLinkEvents.remove(rev);
			log.debug("Found matched LinkEvent: {} {}", rev, le);
		    }
		    else {
			unmatchedLinkEvents.add(le);
			log.debug("Adding unmatched LinkEvent: {}", le);
		    }
		}
		for(LinkEvent le : removedLinkEvents) {
		    if (unmatchedLinkEvents.contains(le)) {
			unmatchedLinkEvents.remove(le);
			log.debug("Removing LinkEvent: {}", le);
		    }
		}
		if(unmatchedLinkEvents.size() > 0) {
		    log.debug("Unmatched link events: {} events", unmatchedLinkEvents.size());
		}

		if ( rerouteAll ) {//addedLinkEvents.size() > 0) { // ||
//				addedPortEvents.size() > 0 ||
//				addedSwitchEvents.size() > 0) {
			p.log("begin_getAllIntents");
			affectedPaths.addAll(getPathIntents().getAllIntents());
			p.log("end_getAllIntents");
		}
		else if (removedSwitchEvents.size() > 0 ||
			 removedLinkEvents.size() > 0 ||
			 removedPortEvents.size() > 0) {
			p.log("begin_getIntentsByLink");
			for (LinkEvent linkEvent: removedLinkEvents)
				affectedPaths.addAll(pathIntents.getIntentsByLink(linkEvent));
			p.log("end_getIntentsByLink");

			p.log("begin_getIntentsByPort");
			for (PortEvent portEvent: removedPortEvents)
				affectedPaths.addAll(pathIntents.getIntentsByPort(portEvent.getDpid(), portEvent.getNumber()));
			p.log("end_getIntentsByPort");

			p.log("begin_getIntentsByDpid");
			for (SwitchEvent switchEvent: removedSwitchEvents)
				affectedPaths.addAll(pathIntents.getIntentsByDpid(switchEvent.getDpid()));
			p.log("end_getIntentsByDpid");
		}
		p.log("begin_reroutePaths");
		reroutePaths(affectedPaths);
		p.log("end_reroutePaths");
		p.flushLog();
	}

	// ================================================================================
	// IEventChannelListener implementations
	// ================================================================================

	@Override
	public void entryAdded(IntentStateList value) {
		entryUpdated(value);
	}

	@Override
	public void entryRemoved(IntentStateList value) {
		// do nothing
	}

	@Override
	public void entryUpdated(IntentStateList value) {
		// TODO draw state transition diagram in multiple ONOS instances and update this method
		PerfLogger p = new PerfLogger("entryUpdated");
		lock.lock(); // TODO optimize locking using smaller steps
		try {
			// reflect state changes of path-level intent into application-level intents
			p.log("begin_changeStateByNotification");
			IntentStateList parentStates = new IntentStateList();
			for (Entry<String, IntentState> entry: value.entrySet()) {
				PathIntent pathIntent = (PathIntent) pathIntents.getIntent(entry.getKey());
				if (pathIntent == null) continue;

				Intent parentIntent = pathIntent.getParentIntent();
				if (parentIntent == null ||
						!(parentIntent instanceof ShortestPathIntent) ||
						!((ShortestPathIntent) parentIntent).getPathIntentId().equals(pathIntent.getId()))
					continue;

				IntentState state = entry.getValue();
				switch (state) {
				case INST_REQ:
				case INST_ACK:
				case INST_NACK:
				case DEL_REQ:
				case DEL_ACK:
				case DEL_PENDING:
					parentStates.put(parentIntent.getId(), state);
					break;
				default:
					break;
				}
			}
			highLevelIntents.changeStates(parentStates);
			pathIntents.changeStates(value);
			p.log("end_changeStateByNotification");
		}
		finally {
			p.flushLog();
			lock.unlock();
		}
	}
}
