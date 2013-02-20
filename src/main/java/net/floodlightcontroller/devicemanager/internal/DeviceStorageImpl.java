package net.floodlightcontroller.devicemanager.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openflow.util.HexString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.thinkaurelius.titan.core.TitanException;
import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.TransactionalGraph.Conclusion;
import com.tinkerpop.frames.FramedGraph;

import net.floodlightcontroller.core.INetMapTopologyObjects.IDeviceObject;
import net.floodlightcontroller.core.INetMapTopologyObjects.IPortObject;
import net.floodlightcontroller.core.INetMapTopologyService.ITopoSwitchService;
import net.floodlightcontroller.core.internal.SwitchStorageImpl;
import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.IDeviceStorage;
import net.floodlightcontroller.devicemanager.SwitchPort;

public class DeviceStorageImpl implements IDeviceStorage {
	
	public TitanGraph graph;
	protected static Logger log = LoggerFactory.getLogger(SwitchStorageImpl.class);
	public ITopoSwitchService svc;

	@Override
	public void init(String conf) {
	       graph = TitanFactory.open(conf);
	        
	        // FIXME: Creation on Indexes should be done only once
	        Set<String> s = graph.getIndexedKeys(Vertex.class);
	        if (!s.contains("dpid")) {
	           graph.createKeyIndex("dpid", Vertex.class);
	           graph.stopTransaction(Conclusion.SUCCESS);
	        }
	        if (!s.contains("type")) {
	        	graph.createKeyIndex("type", Vertex.class);
	        	graph.stopTransaction(Conclusion.SUCCESS);
	        }
	        if (!s.contains("dl_address")) {
	        	graph.createKeyIndex("dl_address", Vertex.class);
	        	graph.stopTransaction(Conclusion.SUCCESS);
	        }
	}	

	public void finalize() {
		close();
	}
	
	@Override
	public void close() {
		graph.shutdown();
	}

	@Override
	public IDeviceObject addDevice(IDevice device) {
		// TODO Auto-generated method stub
		FramedGraph<TitanGraph> fg = new FramedGraph<TitanGraph>(graph);;
		IDeviceObject obj = null;
       	SwitchPort[] attachmentPoints = device.getAttachmentPoints();
       	List<IPortObject> attachedPorts; 
 		try {
            if (fg.getVertices("dl_address",device.getMACAddressString()).iterator().hasNext()) {
            	obj = fg.getVertices("dl_address",device.getMACAddressString(),
            			IDeviceObject.class).iterator().next();
                attachedPorts = Lists.newArrayList(obj.getAttachedPorts());

            } else {
            	obj = fg.addVertex(null,IDeviceObject.class);
            	attachedPorts = new ArrayList<IPortObject>();
            }
            for (SwitchPort ap : attachmentPoints) {
            	 IPortObject port = svc.getPortOnSwitch(HexString.toHexString(ap.getSwitchDPID()),
            												(short) ap.getPort());
            	if (attachedPorts.contains(port)) {
            		attachedPorts.remove(port);
            	} else {
            		obj.setHostPort(port);
            	}            		
            }
            for (IPortObject port: attachedPorts) {
            		obj.removeHostPort(port);
            }
 			obj.setIPAddress(device.getIPv4Addresses().toString());
 			obj.setMACAddress(device.getMACAddressString());
 			obj.setType("device");
 			obj.setState("ACTIVE");
 			graph.stopTransaction(Conclusion.SUCCESS);
		} catch (TitanException e) {
            // TODO: handle exceptions
			log.error(":addDevice mac:{} failed", device.getMACAddressString());
		}	
		
		return obj;
	}

	@Override
	public IDeviceObject updateDevice(IDevice device) {
		// TODO Auto-generated method stub
		return addDevice(device);
	}

	@Override
	public IDeviceObject removeDevice(IDevice device) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDeviceObject getDeviceByMac(String mac) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDeviceObject getDeviceByIP(String ip) {
		// TODO Auto-generated method stub
		return null;
	}

}
