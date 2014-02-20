package net.onrc.onos.ofcontroller.networkgraph;

/**
 * Link Object stored in In-memory Topology.
 *
 * TODO REMOVE following design memo: This object itself may hold the DBObject,
 * but this Object itself will not issue any read/write to the DataStore.
 */
public class LinkImpl extends NetworkGraphObject implements Link {
	protected Port srcPort;
	protected Port dstPort;

	protected static final Double DEFAULT_CAPACITY = Double.POSITIVE_INFINITY;
	protected Double capacity = DEFAULT_CAPACITY;

	protected static final int DEFAULT_COST = 1;
	protected int cost = DEFAULT_COST;

	/**
	 * Constructor for when a link is read from the database and the Ports
	 * already exist in the in-memory network graph.
	 * @param graph
	 * @param srcPort
	 * @param dstPort
	 */
	public LinkImpl(NetworkGraph graph, Port srcPort, Port dstPort) {
		super(graph);
		this.srcPort = srcPort;
		this.dstPort = dstPort;
		setToPorts();
	}

	@Override
	public Switch getSrcSwitch() {
		return srcPort.getSwitch();
	}

	@Override
	public Port getSrcPort() {
		return srcPort;
	}

	@Override
	public Switch getDstSwitch() {
		return dstPort.getSwitch();
	}

	@Override
	public Port getDstPort() {
		return dstPort;
	}

	protected void setToPorts() {
		((PortImpl)srcPort).setOutgoingLink(this);
		((PortImpl)dstPort).setIncomingLink(this);
	}

	protected void unsetFromPorts() {
		((PortImpl)srcPort).setOutgoingLink(null);
		((PortImpl)dstPort).setIncomingLink(null);
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
	public Double getCapacity() {
		return capacity;
	}

	public void setCapacity(Double capacity) {
		this.capacity = capacity;
	}

	@Override
	public String toString() {
		return String.format("%s --(cap:%f Mbps)--> %s",
				getSrcPort().toString(),
				getCapacity(),
				getDstPort().toString());
	}
}
