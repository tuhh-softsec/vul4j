package net.onrc.onos.ofcontroller.core.internal;

import net.onrc.onos.graph.DBOperation;
import net.onrc.onos.graph.GraphDBManager;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IPortObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyService.ITopoSwitchService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopoSwitchServiceImpl implements ITopoSwitchService {

	private DBOperation op;
	protected final static Logger log = LoggerFactory.getLogger(TopoSwitchServiceImpl.class);

	public TopoSwitchServiceImpl(final String dbStore, String conf) {
		op = GraphDBManager.getDBOperation();
	}

	public TopoSwitchServiceImpl() {
		this("","");
	}

	@Override
	protected void finalize() {
		close();
	}

	@Override
	public void close() {
		op.close();
	}

	@Override
	public Iterable<ISwitchObject> getActiveSwitches() {
		// TODO Auto-generated method stub
		//op.close(); //Commit to ensure we see latest data
		return op.getActiveSwitches();
	}

	@Override
	public Iterable<ISwitchObject> getAllSwitches() {
		// TODO Auto-generated method stub
		//op.close(); //Commit to ensure we see latest data
		return op.getAllSwitches();
	}

	@Override
	public Iterable<ISwitchObject> getInactiveSwitches() {
		// TODO Auto-generated method stub
		//op.close(); //Commit to ensure we see latest data
		return op.getInactiveSwitches();
	}

	@Override
	public Iterable<IPortObject> getPortsOnSwitch(String dpid) {
		//op.close(); //Commit to ensure we see latest data
		ISwitchObject switchObject = op.searchSwitch(dpid);
		if (switchObject != null) {
			return switchObject.getPorts();
		}
		return null;
	}

	@Override
	public IPortObject getPortOnSwitch(String dpid, short port_num) {
		// TODO Auto-generated method stub
		return null;
	}
}
