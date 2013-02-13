package net.floodlightcontroller.mastership;

import net.floodlightcontroller.core.module.IFloodlightService;

public interface IMastershipService extends IFloodlightService {
	
	// Acquire mastership for a switch. 
	public void acquireMastership(long dpid, boolean blockOk);
	
	// Release mastership for a switch
	public void releaseMastership(long dpid);
	
	// Check if I am the master of a switch. This is a nonblocking call that checks if the caller is a 
	public boolean amMaster(long dpid);
	
	// Set/Get mastership identifier.
	// This is typically a unique identifier of the controller that does not change across restarts
	public void setMastershipId (String id);
	public String getMastershipId ();
	
}
