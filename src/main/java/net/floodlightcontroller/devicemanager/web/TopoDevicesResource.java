package net.floodlightcontroller.devicemanager.web;

import java.util.Iterator;

import net.floodlightcontroller.core.INetMapTopologyObjects.IDeviceObject;
import net.onrc.onos.util.GraphDBConnection;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class TopoDevicesResource extends ServerResource {
	
	@Get("json")
	public Iterator<IDeviceObject> retrieve() {
		
		GraphDBConnection conn = GraphDBConnection.getInstance("");
		
		return conn.utils().getDevices(conn).iterator();
		
	}
	
}
