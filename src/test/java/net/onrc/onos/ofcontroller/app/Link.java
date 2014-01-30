package net.onrc.onos.ofcontroller.app;

import java.util.Collection;
import java.util.HashSet;

/**
 * This code is valid for the architectural study purpose only.
 * Base class for Link representation
 *
 * @author Toshio Koide (t-koide@onlab.us)
 * 
 */
public class Link extends NetworkGraphEntity {
	protected SwitchPort srcPort;
	protected SwitchPort dstPort;
	protected HashSet<Flow> flows;
	protected Double capacity;

	public Link(SwitchPort srcPort, SwitchPort dstPort) {
		super(srcPort.getNetworkGraph());
		this.srcPort = srcPort;
		this.dstPort = dstPort;
		this.flows = new HashSet<Flow>();
		this.capacity = Double.POSITIVE_INFINITY;
		setToPorts();
	}
	
	public void setToPorts() {
		srcPort.setOutgoingLink(this);
		dstPort.setIncomingLink(this);		
	}
	
	public void unsetFromPorts() {
		srcPort.setOutgoingLink(null);
		dstPort.setIncomingLink(null);
	}
	
	public void setCapacity(Double capacity) {
		this.capacity = capacity;
	}
	
	public Double getCapacity() {
		return capacity;
	}

	public boolean addFlow(Flow flow) {
		return flows.add(flow);
	}
	
	public boolean removeFlow(Flow flow) {
		return flows.remove(flow);
	}

	public Collection<Flow> getFlows() {
		return flows;
	}

	public SwitchPort getSrcPort() {
		return srcPort;
	}

	public SwitchPort getDstPort() {
		return dstPort;
	}
	
	@Override
	public String toString() {
		return String.format("%s --(%f Mbps, %d flows)--> %s",
				getSrcPort().toString(),
				getCapacity(),
				getFlows().size(),
				getDstPort().toString());
	}
}
