package net.floodlightcontroller.core.internal;

import java.util.List;

import net.floodlightcontroller.core.INetMapTopologyObjects.IPortObject;
import net.floodlightcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.floodlightcontroller.core.INetMapTopologyService.ITopoSwitchService;

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
	public Iterable<ISwitchObject> getActiveSwitches() {
		// TODO Auto-generated method stub
		return swStore.getActiveSwitches();
	}

	@Override
	public Iterable<ISwitchObject> getAllSwitches() {
		// TODO Auto-generated method stub		
		return swStore.getAllSwitches();
	}

	@Override
	public Iterable<ISwitchObject> getInactiveSwitches() {
		// TODO Auto-generated method stub
		return swStore.getInactiveSwitches();
	}

	@Override
	public Iterable<IPortObject> getPortsOnSwitch(String dpid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPortObject getPortOnSwitch(String dpid, short port_num) {
		// TODO Auto-generated method stub
		return null;
	}	
}
