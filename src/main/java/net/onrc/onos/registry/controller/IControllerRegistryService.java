package net.onrc.onos.registry.controller;

import java.util.Collection;
import java.util.Map;

import net.floodlightcontroller.core.module.IFloodlightService;

public interface IControllerRegistryService extends IFloodlightService {
	
	// Callback for all mastership changes. 
	// Change callback is called when mastership is acquired or released
	public interface ControlChangeCallback {
		public void controlChanged(long dpid, boolean hasControl);
	}
	
	// Acquire mastership for a switch. 
	public void requestControl(long dpid, ControlChangeCallback cb) throws Exception;
	
	// Release mastership for a switch
	public void releaseControl(long dpid);
	
	// Check if I am the master of a switch. This is a nonblocking call that checks if the caller is a 
	public boolean hasControl(long dpid);
	
	// Set/Get mastership identifier.
	// This is typically a unique identifier of the controller that does not change across restarts
	public void setMastershipId (String id);
	public String getMastershipId ();
	
	/**
	 * Register a controller to the ONOS cluster
	 * @param controller A string identifying the controller
	 */
	public void registerController(String controllerId) throws RegistryException;
	
	/**
	 * Get all controllers in the cluster
	 * @return
	 */
	public Collection<String> getAllControllers() throws RegistryException;
	
	
	public String getControllerForSwitch(long dpid) throws RegistryException;
	
	public Collection<Map<String, String>> getAllSwitches();
	
	public Collection<Long> getSwitchesControlledByController(String controllerId);
}
