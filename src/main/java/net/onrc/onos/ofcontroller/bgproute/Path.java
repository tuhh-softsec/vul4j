package net.onrc.onos.ofcontroller.bgproute;

import java.net.InetAddress;
import java.util.Collections;
import java.util.List;

/*
 * A path is always assumed to be from all other interfaces (external-facing
 * switchports) to the destination interface.
 */

public class Path {

	private Interface dstInterface;
	private InetAddress dstIpAddress;
	private int numUsers = 0;
	
	private List<PushedFlowMod> flowMods = null;
	private boolean permanent = false;
	
	public Path(Interface dstInterface, InetAddress dstIpAddress) {
		this.dstInterface = dstInterface;
		this.dstIpAddress = dstIpAddress;
	}

	public Interface getDstInterface() {
		return dstInterface;
	}

	public InetAddress getDstIpAddress() {
		return dstIpAddress;
	}
	
	public void incrementUsers() {
		numUsers++;
	}
	
	public void decrementUsers() {
		numUsers--;
	}
	
	public int getUsers() {
		return numUsers;
	}
	
	public List<PushedFlowMod> getFlowMods() {
		return Collections.unmodifiableList(flowMods);
	}
	
	public void setFlowMods(List<PushedFlowMod> flowMods) {
		this.flowMods = flowMods;
	}
	
	public boolean isPermanent() {
		return permanent;
	}
	
	public void setPermanent() {
		permanent = true;
	}
}
