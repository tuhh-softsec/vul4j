package net.onrc.onos.ofcontroller.networkgraph;

/**
 * Port Object stored in In-memory Topology.
 *
 * TODO REMOVE following design memo: This object itself may hold the DBObject,
 * but this Object itself will not issue any read/write to the DataStore.
 */
public class PortImpl extends NetworkGraphObject implements Port {

	//private long dpid;
	private Switch sw;
	private short number;

	public PortImpl(NetworkGraph graph) {
		super(graph);
	}

	public void setPortNumber(short portNumber) {
		number = portNumber;
	}

	@Override
	public short getNumber() {
		return number;
	}

	/*
	public void setDpid(long dpid) {
		this.dpid = dpid;
	}

	public long getDpid() {
		return dpid;
	}
	*/

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getHardwareAddress() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Switch getSwitch() {
		return sw;
	}

	public void setSwitch(Switch sw) {
		this.sw = sw;
	}

	@Override
	public Link getLink() {
		// TODO Auto-generated method stub
		return null;
	}

}
