package net.floodlightcontroller.core.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.core.INetMapTopologyObjects.IPortObject;
import net.floodlightcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.floodlightcontroller.core.INetMapTopologyService.ITopoSwitchService;
import net.onrc.onos.util.GraphDBConnection;
import net.onrc.onos.util.GraphDBConnection.Transaction;

public class TopoSwitchServiceImpl implements ITopoSwitchService {
	
	private GraphDBConnection conn;
	protected static Logger log = LoggerFactory.getLogger(TopoSwitchServiceImpl.class);


	public void finalize() {
		close();
	}
	
	@Override
	public void close() {
		conn.endTx(Transaction.COMMIT);
		conn.close();
	}
	
	@Override
	public Iterable<ISwitchObject> getActiveSwitches() {
		// TODO Auto-generated method stub
		conn = GraphDBConnection.getInstance("");
		return conn.utils().getActiveSwitches(conn);
	}

	@Override
	public Iterable<ISwitchObject> getAllSwitches() {
		// TODO Auto-generated method stub
		conn = GraphDBConnection.getInstance("");
		return conn.utils().getAllSwitches(conn);
	}

	@Override
	public Iterable<ISwitchObject> getInactiveSwitches() {
		// TODO Auto-generated method stub
		conn = GraphDBConnection.getInstance("");
		return conn.utils().getInactiveSwitches(conn);
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
