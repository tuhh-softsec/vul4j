package net.onrc.onos.intent.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.onrc.onos.datagrid.IDatagridService;
import net.onrc.onos.datagrid.IEventChannel;
import net.onrc.onos.intent.Intent.IntentState;
import net.onrc.onos.intent.IntentMap;
import net.onrc.onos.intent.IntentOperation;
import net.onrc.onos.intent.IntentOperation.Operator;
import net.onrc.onos.intent.IntentOperationList;
import net.onrc.onos.intent.PathIntent;
import net.onrc.onos.intent.PathIntentMap;
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
public class PathCalcRuntimeModule implements IFloodlightModule, IPathCalcRuntimeService, INetworkGraphListener {
	private PathCalcRuntime runtime;
	private IDatagridService datagridService;
	private INetworkGraphService networkGraphService;
	private IntentMap highLevelIntents;
	private PathIntentMap pathIntents;
	private IControllerRegistryService controllerRegistry;
	private PersistIntent persistIntent;

	private IEventChannel<Long, IntentOperationList> eventChannel;
	private static final String EVENT_CHANNEL_NAME = "onos.pathintent";

	// ================================================================================
	// private methods
	// ================================================================================

	private void reroutePaths(Collection<LinkEvent> removedLinkEvents) {
		HashSet<PathIntent> oldPaths = new HashSet<>();
		for (LinkEvent linkEvent : removedLinkEvents) {
			Collection<PathIntent> intents = pathIntents.getIntentsByLink(linkEvent);
			if (intents == null)
				continue;
			oldPaths.addAll(intents);
		}

		if (oldPaths.isEmpty())
			return;

		IntentOperationList reroutingOperation = new IntentOperationList();
		for (PathIntent pathIntent : oldPaths) {
			reroutingOperation.add(Operator.ADD, pathIntent.getParentIntent());
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
		Map<Class<? extends IFloodlightService>, IFloodlightService> m = new HashMap<>(1);
		m.put(IPathCalcRuntimeService.class, this);
		return m;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<>();
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
		eventChannel = datagridService.createChannel(EVENT_CHANNEL_NAME, Long.class, IntentOperationList.class);
		networkGraphService.registerNetworkGraphListener(this);
		persistIntent = new PersistIntent(controllerRegistry, networkGraphService);

	}

	// ================================================================================
	// IPathCalcRuntimeService implementations
	// ================================================================================

	@Override
	public IntentOperationList executeIntentOperations(IntentOperationList list) {
		// update the map of high-level intents
		highLevelIntents.executeOperations(list);

		// change states of high-level intents
		HashMap<String, IntentState> states = new HashMap<>();
		for (IntentOperation op : list) {
			String id = op.intent.getId();
			if (op.intent.getState().equals(IntentState.INST_ACK))
				states.put(id, IntentState.REROUTE_REQ);
			else
				states.put(id, IntentState.INST_REQ);
		}
		highLevelIntents.changeStates(states);

		// calculate path-intents (low-level operations)
		IntentOperationList pathIntentOperations = runtime.calcPathIntents(list, pathIntents);

		// persist calculated low-level operations into data store
		long key = persistIntent.getKey();
		persistIntent.persistIfLeader(key, pathIntentOperations);

		// remove error-intents and reflect them to high-level intents
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

		// update the map of low-level intents and publish the low-level
		// operations
		pathIntents.executeOperations(pathIntentOperations);
		eventChannel.addEntry(key, pathIntentOperations);
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
		// TODO need optimization.
		// Only high-level intents that affected by link down are rerouted.
		reroutePaths(removedLinkEvents);
	}
}