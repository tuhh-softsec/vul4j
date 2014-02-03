package net.onrc.onos.ofcontroller.networkgraph;



public interface Port {
	public short getNumber();
	public String getName();
	public long getHardwareAddress();
	
	public Switch getSwitch();
	
	public Link getLink();
}
