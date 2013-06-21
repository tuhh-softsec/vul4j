package net.onrc.onos.ofcontroller.devicemanager.web;

import java.util.Iterator;

import net.onrc.onos.graph.GraphDBOperation;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IDeviceObject;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class TopoDevicesResource extends ServerResource {
	
	@Get("json")
	public Iterator<IDeviceObject> retrieve() {
		GraphDBOperation op = new GraphDBOperation("");
		
		return op.getDevices().iterator();
	}
}
