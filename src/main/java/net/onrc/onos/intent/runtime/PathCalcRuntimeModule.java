package net.onrc.onos.intent.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.onrc.onos.datagrid.IDatagridService;
import net.onrc.onos.datagrid.IEventChannel;
import net.onrc.onos.intent.IntentMap;
import net.onrc.onos.intent.IntentOperationList;
import net.onrc.onos.intent.PathIntentMap;
import net.onrc.onos.ofcontroller.networkgraph.INetworkGraphService;
import net.onrc.onos.ofcontroller.networkgraph.NetworkGraph;

public class PathCalcRuntimeModule implements IFloodlightModule, IPathCalcRuntimeService {
	private PathCalcRuntime runtime;
	private IDatagridService datagridService;
	private INetworkGraphService networkGraphService;
	private IntentMap highLevelIntents;
	private PathIntentMap pathIntents;

	private IEventChannel<byte[], IntentOperationList> eventChannel;
	private static final String EVENT_CHANNEL_NAME = "onos.pathintent";


	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<>(1);
		l.add(PathCalcRuntime.class);
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
		runtime = new PathCalcRuntime(networkGraphService.getNetworkGraph());
		highLevelIntents = new IntentMap();
		pathIntents = new PathIntentMap(networkGraphService.getNetworkGraph());
	}

	@Override
	public void startUp(FloodlightModuleContext context) {
		eventChannel = datagridService.createChannel(
				EVENT_CHANNEL_NAME,
				byte[].class,
				IntentOperationList.class);
	}

	protected void publishPathIntentOperationList(IntentOperationList list) {
		eventChannel.addEntry(new byte[1], list); // TODO make key bytes		
	}

	@Override
	public IntentOperationList executeIntentOperations(IntentOperationList list) {
		highLevelIntents.executeOperations(list);
		IntentOperationList pathIntentOperations = runtime.calcPathIntents(list, pathIntents);
		pathIntents.executeOperations(pathIntentOperations);
		publishPathIntentOperationList(pathIntentOperations);
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
}
