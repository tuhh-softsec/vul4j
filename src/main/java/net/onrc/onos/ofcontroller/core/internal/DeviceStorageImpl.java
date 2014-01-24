package net.onrc.onos.ofcontroller.core.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.SwitchPort;
import net.onrc.onos.graph.DBOperation;
import net.onrc.onos.graph.GraphDBManager;
import net.onrc.onos.ofcontroller.core.IDeviceStorage;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IDeviceObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IIpv4Address;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IPortObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.onrc.onos.ofcontroller.devicemanager.OnosDevice;

import org.openflow.util.HexString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.net.InetAddresses;
import com.thinkaurelius.titan.core.TitanException;
/**
 * This is the class for storing the information of devices into CassandraDB
 * @author Pankaj
 */
public class DeviceStorageImpl implements IDeviceStorage {
	protected final static Logger log = LoggerFactory.getLogger(DeviceStorageImpl.class);

	private DBOperation ope;
	/***
	 * Initialize function. Before you use this class, please call this method
	 * @param conf configuration file for Cassandra DB
	 */
	@Override
	public void init(final String dbStore, final String conf) {
		try {
			ope = GraphDBManager.getDBOperation("ramcloud", "/tmp/ramcloud.conf");
		} catch (Exception e) {
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
	protected void finalize() {
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
		for (int i = 0; i < 6; i++) {
	 		try {
	 			if (i > 0) {
	 				log.debug("Retrying add device: i is {}", i);
	 			}
	 			if ((obj = ope.searchDevice(device.getMACAddressString())) != null) {
	 				log.debug("Adding device {}: found existing device", device.getMACAddressString());
	            } else {
	            	obj = ope.newDevice();
	                log.debug("Adding device {}: creating new device", device.getMACAddressString());
	            }

	            if (obj == null) {
	            	return null;
	            }

	            changeDeviceAttachments(device, obj);

	            changeDeviceIpv4Addresses(device, obj);

	 			obj.setMACAddress(device.getMACAddressString());
	 			obj.setType("device");
	 			obj.setState("ACTIVE");
	 			ope.commit();

	 			break;
	 			//log.debug("Adding device {}",device.getMACAddressString());
			} catch (TitanException e) {
				ope.rollback();
				log.error("Adding device {} failed", device.getMACAddressString(), e);
				obj = null;
			}
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

		if ((dev = ope.searchDevice(device.getMACAddressString())) != null) {
			removeDevice(dev);
		}
	}

	@Override
	public void removeDevice(IDeviceObject deviceObject) {
		String deviceMac = deviceObject.getMACAddress();

		removeDeviceImpl(deviceObject);

		try {
			ope.commit();
			log.debug("DeviceStorage:removeDevice mac:{} done", deviceMac);
		} catch (TitanException e) {
			ope.rollback();
			log.error("DeviceStorage:removeDevice mac:{} failed", deviceMac);
		}
	}

	public void removeDeviceImpl(IDeviceObject deviceObject) {
		for (IIpv4Address ipv4AddressVertex : deviceObject.getIpv4Addresses()) {
			ope.removeIpv4Address(ipv4AddressVertex);
		}

		ope.removeDevice(deviceObject);
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

				log.debug("port number is {}", port.getNumber());
				log.debug("port desc is {}", port.getDesc());
			}
		}

		for (IPortObject port: attachedPorts) {
			log.debug("Detaching the device {}: detaching from port", device.getMACAddressString());
			port.removeDevice(obj);

			if (!obj.getAttachedPorts().iterator().hasNext()) {
				// XXX If there are no more ports attached to the device,
				// delete it. Otherwise we have a situation where the
				// device remains forever with an IP address attached.
				// When we implement device probing we should get rid of this.
				removeDevice(obj);
			}
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
		List<String> dbIpv4Addresses = new ArrayList<String>();
		List<Integer> intDbIpv4Addresses = new ArrayList<Integer>();
		for (IIpv4Address ipv4Vertex : deviceObject.getIpv4Addresses()) {
			dbIpv4Addresses.add(InetAddresses.fromInteger(ipv4Vertex.getIpv4Address()).getHostAddress());
			intDbIpv4Addresses.add(ipv4Vertex.getIpv4Address());
		}

		List<String> memIpv4Addresses = new ArrayList<String>();
		for (int addr : device.getIPv4Addresses()) {
			memIpv4Addresses.add(InetAddresses.fromInteger(addr).getHostAddress());
		}

		log.debug("Device IP addresses {}, database IP addresses {}",
				memIpv4Addresses, dbIpv4Addresses);

		for (int ipv4Address : device.getIPv4Addresses()) {
			//if (deviceObject.getIpv4Address(ipv4Address) == null) {
			if (!intDbIpv4Addresses.contains(ipv4Address)) {
				IIpv4Address dbIpv4Address = ope.ensureIpv4Address(ipv4Address);

				/*
				IDeviceObject oldDevice = dbIpv4Address.getDevice();
				if (oldDevice != null) {
					oldDevice.removeIpv4Address(dbIpv4Address);
				}
				*/

				log.debug("Adding IP address {}",
						InetAddresses.fromInteger(ipv4Address).getHostAddress());
				deviceObject.addIpv4Address(dbIpv4Address);
			}
		}

		List<Integer> deviceIpv4Addresses = Arrays.asList(device.getIPv4Addresses());
		for (IIpv4Address dbIpv4Address : deviceObject.getIpv4Addresses()) {
			if (!deviceIpv4Addresses.contains(dbIpv4Address.getIpv4Address())) {
				log.debug("Removing IP address {}",
						InetAddresses.fromInteger(dbIpv4Address.getIpv4Address())
						.getHostAddress());
				deviceObject.removeIpv4Address(dbIpv4Address);
			}
		}
	}

	/**
	 * Takes an {@link OnosDevice} and adds it into the database. There is no
	 * checking of the database data and removing old data - an
	 * {@link OnosDevice} basically corresponds to a packet we've just seen,
	 * and we need to add that information into the database.
	 */
	@Override
	public void addOnosDevice(OnosDevice onosDevice) {
		String macAddress = HexString.toHexString(onosDevice.getMacAddress().toBytes());

		//if the switch port we try to attach a new device already has a link, then stop adding device
		IPortObject portObject1 = ope.searchPort(HexString.toHexString(
				onosDevice.getSwitchDPID()), onosDevice.getSwitchPort());

		if ((portObject1 != null) && portObject1.getLinkedPorts().iterator().hasNext()) {
			if (log.isDebugEnabled()) {
				log.debug("stop adding OnosDevice: {} due to there is a link to: {}",
						onosDevice, portObject1.getLinkedPorts().iterator().next().getPortId());
			}
			return;
		}

		log.debug("addOnosDevice: {}", onosDevice);

		try {
			IDeviceObject device = ope.searchDevice(macAddress);

			if (device == null) {
				device = ope.newDevice();
				device.setType("device");
				device.setState("ACTIVE");
				device.setMACAddress(macAddress);
			}

			// Check if the device has the IP address, add it if it doesn't
			if (onosDevice.getIpv4Address() != null) {
				boolean hasIpAddress = false;
				for (IIpv4Address ipv4Address : device.getIpv4Addresses()) {
					if (ipv4Address.getIpv4Address() == onosDevice.getIpv4Address().intValue()) {
						hasIpAddress = true;
						break;
					}
				}

				if (!hasIpAddress) {
					IIpv4Address ipv4Address = ope.ensureIpv4Address(onosDevice.getIpv4Address().intValue());
					IDeviceObject oldDevice = ipv4Address.getDevice();
					if (oldDevice != null && oldDevice.getMACAddress() != macAddress) {
						oldDevice.removeIpv4Address(ipv4Address);
					}
					device.addIpv4Address(ipv4Address);
				}
			}

			// Check if the device has the attachment point, add it if not
			// TODO single attachment point for now, extend to multiple later
			String switchDpid = HexString.toHexString(onosDevice.getSwitchDPID());
			boolean hasAttachmentPoint = false;
			Iterator<IPortObject> it = device.getAttachedPorts().iterator();
			if (it.hasNext()) {
				IPortObject existingPort = it.next();
				if (existingPort != null) {
					ISwitchObject existingSwitch = existingPort.getSwitch();
					if (!existingSwitch.getDPID().equals(switchDpid) ||
							existingPort.getNumber() != onosDevice.getSwitchPort()) {
						existingPort.removeDevice(device);
					}
					else {
						hasAttachmentPoint = true;
					}
				}
			}

			/*
			for (IPortObject portObject : device.getAttachedPorts()) {
				ISwitchObject switchObject = portObject.getSwitch();
				if (switchObject.getDPID().equals(switchDpid)
						&& portObject.getNumber() == onosDevice.getSwitchPort()) {
					hasAttachmentPoint = true;
					break;
				}
			}
			*/

			if (!hasAttachmentPoint) {
				IPortObject portObject = ope.searchPort(switchDpid, onosDevice.getSwitchPort());
				if (portObject != null) {
					portObject.setDevice(device);
				}
			}

			ope.commit();
		}
		catch (TitanException e) {
			log.error("addOnosDevice {} failed:", macAddress, e);
			ope.rollback();
		}
	}

}
