package net.onrc.onos.graph.web;

import java.util.Iterator;

import net.onrc.onos.graph.DBOperation;
import net.onrc.onos.graph.GraphDBManager;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IDeviceObject;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class TopoDevicesResource extends ServerResource {
	
	@Get("json")
	public Iterator<IDeviceObject> retrieve() {
		DBOperation op = GraphDBManager.getDBOperation();
		
		return op.getDevices().iterator();
	}
}
