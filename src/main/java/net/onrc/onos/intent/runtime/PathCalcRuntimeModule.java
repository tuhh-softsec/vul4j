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
import net.onrc.onos.ofcontroller.networkgraph.NetworkGraph;

public class PathCalcRuntimeModule implements IFloodlightModule {
	private PathCalcRuntime runtime;
	private IDatagridService datagridService;
	private NetworkGraph networkGraph;
	private IntentMap highLevelIntents;
	private PathIntentMap lowLevelIntents;
	
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
		m.put(PathCalcRuntime.class, runtime);
		return m;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<>();
		l.add(IDatagridService.class);
		return l;
	}

	@Override
	public void init(FloodlightModuleContext context) throws FloodlightModuleException {
		datagridService = context.getServiceImpl(IDatagridService.class);
		//networkGraph = new MockNetworkGraph(); // TODO give pointer to the correct NetworkGraph
		runtime = new PathCalcRuntime(networkGraph);
		highLevelIntents = new IntentMap();
		lowLevelIntents = new PathIntentMap(networkGraph);
	}

	@Override
	public void startUp(FloodlightModuleContext context) {
		eventChannel = datagridService.createChannel(
				EVENT_CHANNEL_NAME,
				byte[].class,
				IntentOperationList.class);
	}
	
	public void executeIntentOperations(IntentOperationList list) {
		highLevelIntents.executeOperations(list);
		lowLevelIntents = runtime.calcPathIntents(
				highLevelIntents.getAllIntents(),
				new PathIntentMap(networkGraph));
		// TODO publishPathIntentOperationList(IntentOperationList list)
	}
	
	protected void publishPathIntentOperationList(IntentOperationList list) {
		eventChannel.addEntry(new byte[1], list); // TODO make key bytes		
	}
	
	public IntentMap getIntents() {
		return highLevelIntents;
	}
	
	public void purgeIntents() {
		highLevelIntents.purge();
		lowLevelIntents.purge();
	}
}
