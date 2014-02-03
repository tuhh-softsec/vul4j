package net.onrc.onos.ofcontroller.networkgraph;

public interface Link {
	public Port getSourcePort();
	public Port getDestinationPort();
	public Switch getSourceSwitch();
	public Switch getDestinationSwitch();
	
	public long getLastSeenTime();
	public int getCost();
	
	// Not sure if we want to expose these northbound
	public long getSourceSwitchDpid();
	public short getSourcePortNumber();
	public long getDestinationSwitchDpid();
	public short getDestinationPortNumber();
}
