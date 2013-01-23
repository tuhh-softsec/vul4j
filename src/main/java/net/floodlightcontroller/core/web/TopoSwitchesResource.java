package net.floodlightcontroller.core.web;

import java.util.Iterator;

import net.floodlightcontroller.core.internal.TopoSwitchServiceImpl;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class TopoSwitchesResource extends ServerResource {
	
	@Get("json")
	public Iterator<String> retrieve() {
		TopoSwitchServiceImpl impl = new TopoSwitchServiceImpl();
		
		String filter = (String) getRequestAttributes().get("filter");
		
		if (filter.equals("active")) {
			return (Iterator<String>) impl.GetActiveSwitches().iterator();
		}
		if (filter.equals("inactive")) {
			return (Iterator<String>) impl.GetInactiveSwitches().iterator();
		}
		return (Iterator<String>) impl.GetAllSwitches().iterator();				
	}

}
