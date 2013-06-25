package net.onrc.onos.ofcontroller.core.internal;

import net.onrc.onos.graph.GraphDBOperation;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IPortObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyService.ITopoSwitchService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopoSwitchServiceImpl implements ITopoSwitchService {
	
	private GraphDBOperation op;
	protected static Logger log = LoggerFactory.getLogger(TopoSwitchServiceImpl.class);

	public TopoSwitchServiceImpl(String conf) {
		op = new GraphDBOperation(conf);
	}

	public TopoSwitchServiceImpl() {
		this("/tmp/cassandra.titan");
	}
	
	public void finalize() {
		close();
	}
	
	@Override
	public void close() {
		op.close();
	}
	
	@Override
	public Iterable<ISwitchObject> getActiveSwitches() {
		// TODO Auto-generated method stub
		op.close(); //Commit to ensure we see latest data
		return op.getActiveSwitches();
	}

	@Override
	public Iterable<ISwitchObject> getAllSwitches() {
		// TODO Auto-generated method stub
		op.close(); //Commit to ensure we see latest data
		return op.getAllSwitches();
	}

	@Override
	public Iterable<ISwitchObject> getInactiveSwitches() {
		// TODO Auto-generated method stub
		op.close(); //Commit to ensure we see latest data
		return op.getInactiveSwitches();
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
