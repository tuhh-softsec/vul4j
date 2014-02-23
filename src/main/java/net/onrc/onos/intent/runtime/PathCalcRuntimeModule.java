package net.onrc.onos.intent.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

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
	private PathCalcRuntime runtime;
	private IDatagridService datagridService;
	private INetworkGraphService networkGraphService;
	private IntentMap highLevelIntents;
	private PathIntentMap pathIntents;
	private IControllerRegistryService controllerRegistry;
	private PersistIntent persistIntent;

	private IEventChannel<Long, IntentOperationList> opEventChannel;
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
		for (Intent pathIntent : oldPaths) {
			reroutingOperation.add(Operator.ADD, ((PathIntent) pathIntent).getParentIntent());
		}
		executeIntentOperations(reroutingOperation);
	}

	private void log(String step) {
		log.error("Step:{}, Time:{}", step, System.nanoTime());
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
		// update the map of high-level intents
		log("begin_updateInMemoryIntents");
		highLevelIntents.executeOperations(list);

		// change states of high-level intents
		IntentStateList states = new IntentStateList();
		for (IntentOperation op : list) {
			if (op.intent.getState().equals(IntentState.INST_ACK))
				states.put(op.intent.getId(), IntentState.REROUTE_REQ);
		}
		highLevelIntents.changeStates(states);
		log("end_updateInMemoryIntents");

		// calculate path-intents (low-level operations)
		log("begin_calcPathIntents");
		IntentOperationList pathIntentOperations = runtime.calcPathIntents(list, highLevelIntents, pathIntents);
		log("end_calcPathIntents");

		// persist calculated low-level operations into data store
		log("begin_persistPathIntents");
		long key = persistIntent.getKey();
		persistIntent.persistIfLeader(key, pathIntentOperations);
		log("end_persistPathIntents");

		// remove error-intents and reflect them to high-level intents
		log("begin_removeErrorIntents");
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
		log("end_removeErrorIntents");

		// update the map of path intents and publish the path operations
		log("begin_updateInMemoryPathIntents");
		pathIntents.executeOperations(pathIntentOperations);
		log("end_updateInMemoryPathIntents");

		// Demo special: add a complete path to remove operation
		log("begin_addPathToRemoveOperation");
		for (IntentOperation op: pathIntentOperations) {
			if(op.operator.equals(Operator.REMOVE)) {
				op.intent = pathIntents.getIntent(op.intent.getId());
			}
		}
		log("end_addPathToRemoveOperation");

		// send notification
		log("begin_sendNotification");
		opEventChannel.addEntry(key, pathIntentOperations);
		log("end_sendNotification");
		return pathIntentOperations;
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

		log("called_networkGraphEvents");
		HashSet<Intent> affectedPaths = new HashSet<>();

		if (addedLinkEvents.size() > 0 ||
				addedPortEvents.size() > 0 ||
				addedSwitchEvents.size() > 0) {
			log("begin_getAllIntents");
			affectedPaths.addAll(getPathIntents().getAllIntents());
			log("end_getAllIntents");
		}
		else {
			log("begin_getIntentsByLink");
			for (LinkEvent linkEvent: removedLinkEvents)
				affectedPaths.addAll(pathIntents.getIntentsByLink(linkEvent));
			log("end_getIntentsByLink");

			log("begin_getIntentsByPort");
			for (PortEvent portEvent: removedPortEvents)
				affectedPaths.addAll(pathIntents.getIntentsByPort(portEvent.getDpid(), portEvent.getNumber()));
			log("end_getIntentsByPort");

			log("begin_getIntentsByDpid");
			for (SwitchEvent switchEvent: removedSwitchEvents)
				affectedPaths.addAll(pathIntents.getIntentsByDpid(switchEvent.getDpid()));
			log("end_getIntentsByDpid");
		}
		reroutePaths(affectedPaths);
		log("finished_networkGraphEvents");
	}

	// ================================================================================
	// IEventChannelListener implementations
	// ================================================================================

	@Override
	public void entryAdded(IntentStateList value) {
		log("called_EntryAdded");
		entryUpdated(value);
	}

	@Override
	public void entryRemoved(IntentStateList value) {
		// do nothing
	}

	@Override
	public void entryUpdated(IntentStateList value) {
		// TODO draw state transition diagram in multiple ONOS instances and update this method

		log("called_EntryUpdated");
		// reflect state changes of path-level intent into application-level intents
		log("begin_changeStateByNotification");
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
			case INST_ACK:
			case INST_NACK:
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
		log("end_changeStateByNotification");
	}
}
