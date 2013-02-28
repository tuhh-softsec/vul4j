package net.onrc.onos.registry.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.restserver.IRestApiService;

import org.openflow.util.HexString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of a registry that doesn't rely on any external registry
 * service. This is designed to be used only in single-node setups (e.g. for
 * development). All registry data is stored in local memory.
 * @author jono
 *
 */
public class StandaloneRegistry implements IFloodlightModule,
		IControllerRegistryService {
	protected static Logger log = LoggerFactory.getLogger(StandaloneRegistry.class);
	
	protected IRestApiService restApi;
	
	protected String controllerId = null;
	protected Map<String, ControlChangeCallback> switchCallbacks;
	

	@Override
	public void requestControl(long dpid, ControlChangeCallback cb)
			throws RegistryException {
		if (controllerId == null) {
			throw new RuntimeException(
					"Must register a controller before calling requestControl");
		}
		
		switchCallbacks.put(HexString.toHexString(dpid), cb);
		
		log.debug("Control granted for {}", HexString.toHexString(dpid));
		
		//Immediately grant request for control
		if (cb != null) {
			cb.controlChanged(dpid, true);
		}
	}

	@Override
	public void releaseControl(long dpid) {
		ControlChangeCallback cb = switchCallbacks.remove(HexString.toHexString(dpid));
		
		log.debug("Control released for {}", HexString.toHexString(dpid));
		
		if (cb != null){
			cb.controlChanged(dpid, false);
		}
	}

	@Override
	public boolean hasControl(long dpid) {
		return switchCallbacks.containsKey(HexString.toHexString(dpid));
	}

	@Override
	public String getControllerId() {
		return controllerId;
	}

	@Override
	public void registerController(String controllerId)
			throws RegistryException {
		if (this.controllerId != null) {
			throw new RegistryException(
					"Controller already registered with id " + this.controllerId);
		}
		this.controllerId = controllerId;
	}

	@Override
	public Collection<String> getAllControllers() throws RegistryException {
		List<String> l = new ArrayList<String>();
		l.add(controllerId);
		return l;
	}

	@Override
	public String getControllerForSwitch(long dpid) throws RegistryException {
		return controllerId;
	}

	@Override
	public Map<String, List<ControllerRegistryEntry>> getAllSwitches() {
		Map<String, List<ControllerRegistryEntry>> switches = 
				new HashMap<String, List<ControllerRegistryEntry>>();
		
		for (String strSwitch : switchCallbacks.keySet()){
			log.debug("Swtich _{}", strSwitch);
			List<ControllerRegistryEntry> list = new ArrayList<ControllerRegistryEntry>();
			list.add(new ControllerRegistryEntry(controllerId, 0));
			
			switches.put(strSwitch, list);
		}
		
		return switches;
	}

	@Override
	public Collection<Long> getSwitchesControlledByController(
			String controllerId) {
		throw new RuntimeException("Not yet implemented");
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		Collection<Class<? extends IFloodlightService>> l = 
				new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IControllerRegistryService.class);
		return l;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		Map<Class<? extends IFloodlightService>, IFloodlightService> m = 
				new HashMap<Class<? extends IFloodlightService>, IFloodlightService>();
		m.put(IControllerRegistryService.class,  this);
		return m;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		Collection<Class<? extends IFloodlightService>> l =
                new ArrayList<Class<? extends IFloodlightService>>();
        l.add(IRestApiService.class);
		return l;
	}

	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException {
		restApi = context.getServiceImpl(IRestApiService.class);
		
		switchCallbacks = new HashMap<String, ControlChangeCallback>();
	}

	@Override
	public void startUp(FloodlightModuleContext context) {
		restApi.addRestletRoutable(new RegistryWebRoutable());
	}

}
