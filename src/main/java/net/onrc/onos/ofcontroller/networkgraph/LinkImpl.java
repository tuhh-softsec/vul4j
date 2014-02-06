package net.onrc.onos.ofcontroller.networkgraph;

/**
 * Link Object stored in In-memory Topology.
 *
 * TODO REMOVE following design memo: This object itself may hold the DBObject,
 * but this Object itself will not issue any read/write to the DataStore.
 */
public class LinkImpl extends NetworkGraphObject implements Link {

	private Switch srcSwitch;
	private Port srcPort;
	private Switch dstSwitch;
	private Port dstPort;

	private static final int DEFAULT_COST = 1;
	private int cost = DEFAULT_COST;

	public LinkImpl(NetworkGraph graph) {
		super(graph);
	}

	@Override
	public Port getSourcePort() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Port getDestinationPort() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Switch getSourceSwitch() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Switch getDestinationSwitch() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getLastSeenTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	@Override
	public long getSourceSwitchDpid() {
		return srcSwitch.getDpid();
	}

	public void setSrcSwitch(Switch srcSwitch) {
	    // TODO null check
		this.srcSwitch = srcSwitch;
	}

	@Override
	public short getSourcePortNumber() {
		return srcPort.getNumber();
	}

	public void setSrcPort(Port srcPort) {
	    // TODO null check
		this.srcPort = srcPort;
	}

	@Override
	public long getDestinationSwitchDpid() {
		return dstSwitch.getDpid();
	}

	public void setDstSwitch(Switch dstSwitch) {
	    // TODO null check
		this.dstSwitch = dstSwitch;
	}

	@Override
	public short getDestinationPortNumber() {
		return dstPort.getNumber();
	}

	public void setDstPort(Port dstPort) {
	    // TODO null check
		this.dstPort = dstPort;
	}


}
