package net.floodlightcontroller.core;

public interface IUpdate {
	
    /** 
     * Calls the appropriate listeners
     */
    public void dispatch();

}
