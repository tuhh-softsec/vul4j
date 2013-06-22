package net.onrc.onos.ofcontroller.core.internal;

import java.util.ArrayList;
import java.util.List;
import org.openflow.util.HexString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.thinkaurelius.titan.core.TitanException;
import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.SwitchPort;
import net.floodlightcontroller.packet.IPv4;
import net.onrc.onos.graph.GraphDBOperation;
import net.onrc.onos.ofcontroller.core.IDeviceStorage;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IDeviceObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IPortObject;
import net.onrc.onos.ofcontroller.core.internal.SwitchStorageImpl;

/**
 * This is the class for storing the information of devices into CassandraDB
 * @author Pankaj
 */
public class DeviceStorageImpl implements IDeviceStorage {
	
	private GraphDBOperation ope;
	protected static Logger log = LoggerFactory.getLogger(SwitchStorageImpl.class);

	/***
	 * Initialize function. Before you use this class, please call this method
	 * @param conf configuration file for Cassandra DB
	 */
	@Override
	public void init(String conf) {
		try{
			ope = new GraphDBOperation(conf);
		} catch(Exception e) {
			log.error(e.getMessage());
		}
	}	
	
	/***
	 * Finalize/close function. After you use this class, please call this method.
	 * It will close the DB connection.
	 */
	@Override
	public void close() {
		ope.close();
	}
	
	/***
	 * Finalize/close function. After you use this class, please call this method.
	 * It will close the DB connection. This is for Java garbage collection.
	 */
	@Override
	public void finalize() {
		close();
	}

	/***
	 * This function is for adding the device into the DB.
	 * @param device The device you want to add into the DB.
	 * @return IDeviceObject which was added in the DB. 
	 */
	@Override
	public IDeviceObject addDevice(IDevice device) {
		IDeviceObject obj = null;
 		try {
 			if ((obj = ope.searchDevice(device.getMACAddressString())) != null) {
 				log.debug("Adding device {}: found existing device",device.getMACAddressString());
            } else {
            	obj = ope.newDevice();
                log.debug("Adding device {}: creating new device",device.getMACAddressString());
            }
	            changeDeviceAttachments(device, obj);
	
				String multiIntString = "";
				for(Integer intValue : device.getIPv4Addresses()) {
				   if (multiIntString == null || multiIntString.isEmpty()){
				     multiIntString = IPv4.fromIPv4Address(intValue);
				     multiIntString = "[" + IPv4.fromIPv4Address(intValue);
				   }else{
				        multiIntString += "," + IPv4.fromIPv4Address(intValue);
				   }
				}
	 	            
	            if(multiIntString.toString() != null && !multiIntString.toString().isEmpty()){
	            	obj.setIPAddress(multiIntString + "]");
	            }
	            
	 			obj.setMACAddress(device.getMACAddressString());
	 			obj.setType("device");
	 			obj.setState("ACTIVE");
	 			ope.commit();
 			
	 			log.debug("Adding device {}",device.getMACAddressString());
		} catch (Exception e) {
			ope.rollback();
			log.error(":addDevice mac:{} failed", device.getMACAddressString());
			obj = null;
		}	
 			return obj;		
	}

	/***
	 * This function is for updating the Device properties.
	 * @param device The device you want to add into the DB.
	 * @return IDeviceObject which was added in the DB. 
	 */
	@Override
	public IDeviceObject updateDevice(IDevice device) {
		return addDevice(device);
	}

	/***
	 * This function is for removing the Device from the DB.
	 * @param device The device you want to delete from the DB.
	 */
	@Override
	public void removeDevice(IDevice device) {
		IDeviceObject dev;
		try {
			if ((dev = ope.searchDevice(device.getMACAddressString())) != null) {
             	ope.removeDevice(dev);
             	ope.commit();
            	log.error("DeviceStorage:removeDevice mac:{} done", device.getMACAddressString());
            }
		} catch (Exception e) {
			ope.rollback();
			log.error("DeviceStorage:removeDevice mac:{} failed", device.getMACAddressString());
		}
	}

	/***
	 * This function is for getting the Device from the DB by Mac address of the device.
	 * @param mac The device mac address you want to get from the DB.
	 * @return IDeviceObject you want to get.
	 */
	@Override
	public IDeviceObject getDeviceByMac(String mac) {
		return ope.searchDevice(mac);
	}

	/***
	 * This function is for getting the Device from the DB by IP address of the device.
	 * @param ip The device ip address you want to get from the DB.
	 * @return IDeviceObject you want to get.
	 */
	@Override
	public IDeviceObject getDeviceByIP(String ip) {
		try
		{
			for(IDeviceObject dev : ope.getDevices()){
				String ips;
				if((ips = dev.getIPAddress()) != null){
					String nw_addr_wob = ips.replace("[", "").replace("]", "");
					ArrayList<String> iplists = Lists.newArrayList(nw_addr_wob.split(","));	
					if(iplists.contains(ip)){
						return dev;
					}
				}
			}
			return null;
		}
		catch (Exception e)
		{
			log.error("DeviceStorage:getDeviceByIP:{} failed");
			return null;
		}
	}

	/***
	 * This function is for changing the Device attachment point.
	 * @param device The device you want change the attachment point
	 */
	@Override
	public void changeDeviceAttachments(IDevice device) {
		IDeviceObject obj = null;
 		try {
            if ((obj = ope.searchDevice(device.getMACAddressString())) != null) {
                log.debug("Changing device ports {}: found existing device",device.getMACAddressString());
                changeDeviceAttachments(device, obj);
                ope.commit();
            } else {
   				log.debug("failed to search device...now adding {}",device.getMACAddressString());
   				addDevice(device);
            }            			
		} catch (Exception e) {
			ope.rollback();
			log.error(":addDevice mac:{} failed", device.getMACAddressString());
		}	
	}
	
	/***
	 * This function is for changing the Device attachment point.
	 * @param device The new device you want change the attachment point
	 * @param obj The old device IDeviceObject that is going to change the attachment point.
	 */
	public void changeDeviceAttachments(IDevice device, IDeviceObject obj) {
		SwitchPort[] attachmentPoints = device.getAttachmentPoints();
		List<IPortObject> attachedPorts = Lists.newArrayList(obj.getAttachedPorts());
		
        for (SwitchPort ap : attachmentPoints) {
        	//Check weather there is the port
          	 IPortObject port = ope.searchPort( HexString.toHexString(ap.getSwitchDPID()),
												(short) ap.getPort());
          	 log.debug("New Switch Port is {},{}", HexString.toHexString(ap.getSwitchDPID()),(short) ap.getPort());
	       	 
	       	 if(port != null){
	   			if(attachedPorts.contains(port))
	       		{
		       		log.debug("This is the port you already attached {}: do nothing",device.getMACAddressString());
		       		//This port will be remained, so remove from the removed port lists.
		       		attachedPorts.remove(port);
		       	} else {
	     		   log.debug("Adding device {}: attaching to port",device.getMACAddressString());
		       		port.setDevice(obj);  
		        }
	    	
		       	log.debug("port number is {}", port.getNumber().toString());
		        log.debug("port desc is {}", port.getDesc());  
	       	 }
        }      		 
	            
	    for (IPortObject port: attachedPorts) {
            log.debug("Detouching the device {}: detouching from port",device.getMACAddressString());
       		port.removeDevice(obj);
        }
	}

	/***
	 * This function is for changing the Device IPv4 address.
	 * @param device The new device you want change the ipaddress
	 */
	@Override
	public void changeDeviceIPv4Address(IDevice device) {
		IDeviceObject obj;
  		try {
  			if ((obj = ope.searchDevice(device.getMACAddressString())) != null) {

  				String multiIntString = "";
  				for(Integer intValue : device.getIPv4Addresses()){
  				   if (multiIntString == null || multiIntString.isEmpty()){
  				     multiIntString = "[" + IPv4.fromIPv4Address(intValue);
  				   } else {
  				        multiIntString += "," + IPv4.fromIPv4Address(intValue);
  				   }
  				}
  				
  	            if(multiIntString != null && !multiIntString.isEmpty()){
  	            	obj.setIPAddress(multiIntString + "]");
  	            }
  	            
              	ope.commit();
  			} else {
            	log.error(":changeDeviceIPv4Address mac:{} failed", device.getMACAddressString());
            }		
  		} catch (TitanException e) {
  			ope.rollback();
			log.error(":changeDeviceIPv4Address mac:{} failed due to exception {}", device.getMACAddressString(),e);
		}
	}

}
