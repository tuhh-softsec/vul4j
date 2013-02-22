package net.onrc.onos.registry.controller;

public interface IMastershipHelper {
	
	// Callback for all mastership changes. 
	// Change callback is called when mastership is acquired or released
	public interface MastershipCallback {
		public void changeCallback(long dpid);
	}
	
	// Set/get mastership identifier. This is used to set the unique identifier of the controller that is asking for mastership.
	// It needs to be set first before any mastership call can be made
	public void setMastershipId (String id);
	public String getMastershipId ();
	
	// Request mastership for a switch. Our request for mastership remains in a queue. If we win mastership, the callback
	// is called. This call is non-blocking and can be called from the packet processing context as well.
	public void requestMastership(long dpid, MastershipCallback cb);
	
	// Release mastership for a switch. If we are the master, then the mastership will be released and given to the next
	// controller who had requested mastership. If we are not the master our request for mastership will be 
	// removed from the queue.
	public void releaseMastership(long dpid);
	
	// Check if I am the master of a switch and return true if I am the master. 
	public boolean amMaster(long dpid);	
}
