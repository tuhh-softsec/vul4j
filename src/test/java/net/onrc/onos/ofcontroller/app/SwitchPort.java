package net.onrc.onos.ofcontroller.app;

/**
 * This code is valid for the architectural study purpose only.
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class SwitchPort extends NetworkGraphEntity {
	protected Switch parentSwitch;
	protected Integer portNumber;
	protected Link outgoingLink;
	protected Link incomingLink;

	public SwitchPort(Switch parentSwitch, Integer portNumber) {
		super(parentSwitch.getNetworkGraph());
		this.parentSwitch = parentSwitch;
		this.portNumber = portNumber;
	}

	public Switch getSwitch() {
		return parentSwitch;
	}

	public void setOutgoingLink(Link link) {
		outgoingLink = link;
	}

	public Link getOutgointLink() {
		return outgoingLink;
	}

	public void setIncomingLink(Link link) {
		incomingLink = link;
	}
	
	public Link getIncomingLink() {
		return incomingLink;
	}

	public Integer getPortNumber() {
		return portNumber;
	}
	
	@Override
	public String toString() {
		return String.format("%s:%d",
				getSwitch().getName(),
				getPortNumber());
	}
}
