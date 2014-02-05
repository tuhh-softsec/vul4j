package net.onrc.onos.ofcontroller.networkgraph;

/**
 * Link Object stored in In-memory Topology.
 *
 * TODO REMOVE following design memo: This object itself may hold the DBObject,
 * but this Object itself will not issue any read/write to the DataStore.
 */
public class LinkImpl extends NetworkGraphObject implements Link {

	private long srcSwitch;
	private short srcPort;
	private long dstSwitch;
	private short dstPort;

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
		return srcSwitch;
	}

	public void setSrcSwitch(long srcSwitch) {
		this.srcSwitch = srcSwitch;
	}

	@Override
	public short getSourcePortNumber() {
		return srcPort;
	}

	public void setSrcPort(short srcPort) {
		this.srcPort = srcPort;
	}

	@Override
	public long getDestinationSwitchDpid() {
		return dstSwitch;
	}

	public void setDstSwitch(long dstSwitch) {
		this.dstSwitch = dstSwitch;
	}

	@Override
	public short getDestinationPortNumber() {
		return dstPort;
	}

	public void setDstPort(short dstPort) {
		this.dstPort = dstPort;
	}


}
