package net.onrc.onos.registry.controller;



//@JsonRootName("controller")
public class ControllerService {

	private String controllerId;
	
	public ControllerService(){
		this("");
	}
	
	public ControllerService(String controllerId) {
		this.controllerId = controllerId;
	}

    public void setControllerId(String controllerId) {
        this.controllerId = controllerId;
    }

    public String getControllerId() {
        return controllerId;
    }

}
