package net.onrc.onos.ofcontroller.networkgraph;

/**
 * Port Object stored in In-memory Topology.
 *
 * TODO REMOVE following design memo: This object itself may hold the DBObject,
 * but this Object itself will not issue any read/write to the DataStore.
 */
public class PortImpl extends NetworkGraphObject implements Port {

	private Switch sw;
	private Long number;
	protected Link outgoingLink;
	protected Link incomingLink;

	public PortImpl(NetworkGraph graph, Switch parentSwitch, Long number) {
		super(graph);
		this.sw = parentSwitch;
		this.number = number;
	}

	@Override
	public Long getNumber() {
		return number;
	}

	@Override
	public Long getHardwareAddress() {
		// TODO Auto-generated method stub
		return 0L;
	}

	@Override
	public Switch getSwitch() {
		return sw;
	}

	@Override
	public Link getOutgoingLink() {
		return outgoingLink;
	}

	@Override
	public Link getIncomingLink() {
		return incomingLink;
	}

	public void setOutgoingLink(Link link) {
		outgoingLink = link;
	}

	public void setIncomingLink(Link link) {
		incomingLink = link;
	}

	@Override
	public String toString() {
		return String.format("%d:%d",
				getSwitch().getDpid(),
				getNumber());
	}
}
