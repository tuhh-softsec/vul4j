package net.onrc.onos.ofcontroller.core.internal;

import java.util.Arrays;
import java.util.List;

import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.SwitchPort;
import net.onrc.onos.graph.GraphDBOperation;
import net.onrc.onos.ofcontroller.core.IDeviceStorage;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IDeviceObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IIpv4Address;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IPortObject;

import org.openflow.util.HexString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.thinkaurelius.titan.core.TitanException;

/**
 * This is the class for storing the information of devices into CassandraDB
 * @author Pankaj
 */
public class DeviceStorageImpl implements IDeviceStorage {
	protected final static Logger log = LoggerFactory.getLogger(SwitchStorageImpl.class);
	
	private GraphDBOperation ope;

	/***
	 * Initialize function. Before you use this class, please call this method
	 * @param conf configuration file for Cassandra DB
	 */
	@Override
	public void init(String conf) {
		try {
			ope = new GraphDBOperation(conf);
		} catch (TitanException e) {
			log.error("Couldn't open graph operation", e);
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
 				log.debug("Adding device {}: found existing device", device.getMACAddressString());
            } else {
            	obj = ope.newDevice();
                log.debug("Adding device {}: creating new device", device.getMACAddressString());
            }
 			
            changeDeviceAttachments(device, obj);
	        
            changeDeviceIpv4Addresses(device, obj);
            
 			obj.setMACAddress(device.getMACAddressString());
 			obj.setType("device");
 			obj.setState("ACTIVE");
 			ope.commit();
		
 			//log.debug("Adding device {}",device.getMACAddressString());
		} catch (TitanException e) {
			ope.rollback();
			log.error("Adding device {} failed", device.getMACAddressString(), e);
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
				for (IIpv4Address ipv4AddressVertex : dev.getIpv4Addresses()) {
					ope.removeIpv4Address(ipv4AddressVertex);
				}
				
             	ope.removeDevice(dev);
             	ope.commit();
            	log.error("DeviceStorage:removeDevice mac:{} done", device.getMACAddressString());
            }
		} catch (TitanException e) {
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
	public IDeviceObject getDeviceByIP(int ipv4Address) {
		try {
			IIpv4Address ipv4AddressVertex = ope.searchIpv4Address(ipv4Address);
			if (ipv4AddressVertex != null) {
				return ipv4AddressVertex.getDevice();
			}
			return null;
		}
		catch (TitanException e) {
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
                log.debug("Changing device ports {}: found existing device", device.getMACAddressString());
                changeDeviceAttachments(device, obj);
                ope.commit();
            } else {
   				log.debug("failed to search device...now adding {}", device.getMACAddressString());
   				addDevice(device);
            }
		} catch (TitanException e) {
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
			//Check if there is the port
			IPortObject port = ope.searchPort(HexString.toHexString(ap.getSwitchDPID()),
					(short) ap.getPort());
			log.debug("New Switch Port is {},{}", 
					HexString.toHexString(ap.getSwitchDPID()), (short) ap.getPort());

			if (port != null){
				if (attachedPorts.contains(port)) {
					log.debug("This is the port you already attached {}: do nothing", device.getMACAddressString());
					//This port will be remained, so remove from the removed port lists.
					attachedPorts.remove(port);
				} else {
					log.debug("Adding device {}: attaching to port", device.getMACAddressString());
					port.setDevice(obj);  
				}

				log.debug("port number is {}", port.getNumber().toString());
				log.debug("port desc is {}", port.getDesc());  
			}
		}      		 

		for (IPortObject port: attachedPorts) {
			log.debug("Detaching the device {}: detaching from port", device.getMACAddressString());
			port.removeDevice(obj);
		}
	}

	/***
	 * This function is for changing the Device IPv4 address.
	 * @param device The new device you want change the ipaddress
	 */
	@Override
	public void changeDeviceIPv4Address(IDevice device) {
		log.debug("Changing IP address for {} to {}", device.getMACAddressString(),
				device.getIPv4Addresses());
		IDeviceObject obj;
  		try {
  			if ((obj = ope.searchDevice(device.getMACAddressString())) != null) {
  				changeDeviceIpv4Addresses(device, obj);
  	            
              	ope.commit();
  			} else {
            	log.error(":changeDeviceIPv4Address mac:{} failed", device.getMACAddressString());
            }		
  		} catch (TitanException e) {
  			ope.rollback();
			log.error(":changeDeviceIPv4Address mac:{} failed due to exception {}", device.getMACAddressString(), e);
		}
	}
	
	private void changeDeviceIpv4Addresses(IDevice device, IDeviceObject deviceObject) {
		for (int ipv4Address : device.getIPv4Addresses()) {
			if (deviceObject.getIpv4Address(ipv4Address) == null) {
				IIpv4Address dbIpv4Address = ope.ensureIpv4Address(ipv4Address);
				deviceObject.addIpv4Address(dbIpv4Address);
			}
		}
			
		List<Integer> deviceIpv4Addresses = Arrays.asList(device.getIPv4Addresses());
		for (IIpv4Address dbIpv4Address : deviceObject.getIpv4Addresses()) {
			if (!deviceIpv4Addresses.contains(dbIpv4Address.getIpv4Address())) {
				deviceObject.removeIpv4Address(dbIpv4Address);
			}
		}
	}

}
