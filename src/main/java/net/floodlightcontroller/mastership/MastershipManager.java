package net.floodlightcontroller.mastership;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.mastership.IMastershipService;

public class MastershipManager implements IFloodlightModule, IMastershipService {

	protected static Logger log = LoggerFactory.getLogger(MastershipManager.class);
	protected String mastershipId;
	
	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IMastershipService.class);
		return l;
	}
	
	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		Map<Class<? extends IFloodlightService>, IFloodlightService> m = 
				new HashMap<Class<? extends IFloodlightService>, IFloodlightService>();
		m.put(IMastershipService.class,  this);
		return m;
	}
	
	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		// no module dependencies
		return null;
	}
	
	@Override
	public void init (FloodlightModuleContext context) throws FloodlightModuleException {
		//TODO
		return;
	}
	
	@Override
	public void startUp (FloodlightModuleContext context) {
		//TODO
		return;
	}

	@Override
	public void acquireMastership(long dpid, boolean blockOk) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void releaseMastership(long dpid) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean amMaster(long dpid) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setMastershipId(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getMastershipId() {
		// TODO Auto-generated method stub
		return null;
	}
}
