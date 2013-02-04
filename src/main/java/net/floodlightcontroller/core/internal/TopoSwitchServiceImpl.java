package net.floodlightcontroller.core.internal;

import java.util.List;

import net.floodlightcontroller.core.INetMapTopologyService.ITopoSwitchService;
import net.floodlightcontroller.core.ISwitchStorage.ISwitchObject;

public class TopoSwitchServiceImpl implements ITopoSwitchService {
	
	ThreadLocal<SwitchStorageImpl> store = new ThreadLocal<SwitchStorageImpl>() {
		@Override
		protected SwitchStorageImpl initialValue() {
			SwitchStorageImpl swStore = new SwitchStorageImpl();
			//TODO: Get the file path from global properties
			swStore.init("/tmp/cassandra.titan");
			return swStore;
		}
	};
	
	SwitchStorageImpl swStore = store.get();
	
	@Override
	public Iterable<ISwitchObject> GetActiveSwitches() {
		// TODO Auto-generated method stub
		return swStore.getActiveSwitches();
	}

	@Override
	public Iterable<ISwitchObject> GetAllSwitches() {
		// TODO Auto-generated method stub		
		return swStore.getAllSwitches();
	}

	@Override
	public Iterable<ISwitchObject> GetInactiveSwitches() {
		// TODO Auto-generated method stub
		return swStore.getInactiveSwitches();
	}

	@Override
	public List<String> GetPortsOnSwitch(String dpid) {
		// TODO Auto-generated method stub
		return null;
	}	
}
