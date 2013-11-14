package net.onrc.onos.ofcontroller.devicemanager.internal;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.SwitchPort;
import net.onrc.onos.graph.GraphDBConnection;
import net.onrc.onos.graph.GraphDBOperation;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IDeviceObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IIpv4Address;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IPortObject;
import net.onrc.onos.ofcontroller.core.internal.DeviceStorageImpl;
import net.onrc.onos.ofcontroller.core.internal.SwitchStorageImpl;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflow.util.HexString;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.InetAddresses;
import com.thinkaurelius.titan.core.TitanFactory;

//Add Powermock preparation
@RunWith(PowerMockRunner.class)
@PrepareForTest({TitanFactory.class, GraphDBConnection.class, GraphDBOperation.class, DeviceStorageImpl.class})
public class DeviceStorageImplTest{
	
	protected final static Logger log = LoggerFactory.getLogger(SwitchStorageImpl.class);
	
	String conf;
	DeviceStorageImpl deviceImpl;
    private GraphDBConnection mockConn;
    private GraphDBOperation mockOpe;
    
	@Before
	public void setUp() throws Exception {
	    deviceImpl = new DeviceStorageImpl();	
		conf = "/dummy/path/to/db";
				
		PowerMock.mockStatic(GraphDBConnection.class);
		mockConn = createMock(GraphDBConnection.class);
		PowerMock.suppress(PowerMock.constructor(GraphDBConnection.class));
		EasyMock.expect(GraphDBConnection.getInstance((String)EasyMock.anyObject())).andReturn(mockConn);
		PowerMock.replay(GraphDBConnection.class);
			
		//PowerMock.mockStatic(GraphDBOperation.class);
		mockOpe = PowerMock.createMock(GraphDBOperation.class);
		PowerMock.expectNew(GraphDBOperation.class, new Class<?>[]{String.class}, conf).andReturn(mockOpe);
		//mockOpe.close();
		PowerMock.replay(GraphDBOperation.class);
        // Replace the conf to dummy conf
		// String conf = "/tmp/cassandra.titan";

		deviceImpl.init(conf);

	}

	@After
	public void tearDown() throws Exception {	
		verify(mockOpe);
	}
	
	private IPortObject getMockPort(long dpid, short port) {
		IPortObject mockPortObject = createMock(IPortObject.class);
		expect(mockPortObject.getNumber()).andReturn(port).anyTimes();
		expect(mockPortObject.getDesc()).andReturn("test port").anyTimes();
		return mockPortObject;
	}
	
	private IDevice getMockDevice(String strMacAddress, long attachmentDpid, 
			short attachmentPort, int ipv4Address) {
		IDevice mockIDevice = createMock(IDevice.class);
		
		
		long longMacAddress = HexString.toLong(strMacAddress);
		
		SwitchPort[] attachmentSwitchPorts = {new SwitchPort(attachmentDpid, attachmentPort)};
		
		expect(mockIDevice.getMACAddress()).andReturn(longMacAddress).anyTimes();
		expect(mockIDevice.getMACAddressString()).andReturn(strMacAddress).anyTimes();
		expect(mockIDevice.getAttachmentPoints()).andReturn(attachmentSwitchPorts).anyTimes();
		expect(mockIDevice.getIPv4Addresses()).andReturn(new Integer[] {ipv4Address}).anyTimes();
		
		replay(mockIDevice);
		
		return mockIDevice;
	}
	
	/**
	 * Description:
	 *  Test method for addDevice method.
	 * Condition:
	 *  The device does not already exist in the database
	 * Expect:
	 * 	Get proper IDeviceObject
	 */
	@Test
	public void testAddNewDevice() {
		String strMacAddress = "99:99:99:99:99:99";
		long attachmentDpid = HexString.toLong("00:00:00:00:00:00:0a:01");
		short attachmentPort = 2;
		int intIpv4Address = InetAddresses.coerceToInteger(InetAddresses.forString("192.168.100.1"));
		
		IDevice device = getMockDevice(strMacAddress, attachmentDpid, attachmentPort, intIpv4Address);
		
		IDeviceObject mockDeviceObject = createMock(IDeviceObject.class);
		IPortObject mockPortObject = getMockPort(attachmentDpid, attachmentPort); 
		IIpv4Address mockIpv4Address = createMock(IIpv4Address.class);
		
		expect(mockOpe.searchDevice(strMacAddress)).andReturn(null);
		expect(mockOpe.newDevice()).andReturn(mockDeviceObject);
		expect(mockDeviceObject.getAttachedPorts()).andReturn(Collections.<IPortObject>emptyList());
		expect(mockOpe.searchPort(HexString.toHexString(attachmentDpid), attachmentPort)).andReturn(mockPortObject);
		mockPortObject.setDevice(mockDeviceObject);
		expect(mockDeviceObject.getIpv4Address(intIpv4Address)).andReturn(null);
		expect(mockOpe.ensureIpv4Address(intIpv4Address)).andReturn(mockIpv4Address);
		mockDeviceObject.addIpv4Address(mockIpv4Address);
		expect(mockDeviceObject.getIpv4Addresses()).andReturn(Collections.singleton(mockIpv4Address));
		expect(mockIpv4Address.getIpv4Address()).andReturn(intIpv4Address);
		
		mockDeviceObject.setMACAddress(strMacAddress);
		mockDeviceObject.setType("device");
		mockDeviceObject.setState("ACTIVE");
		mockOpe.commit();
		
		replay(mockDeviceObject);
		replay(mockPortObject);
		replay(mockIpv4Address);
		replay(mockOpe);
		
		IDeviceObject addedObject = deviceImpl.addDevice(device);
		assertNotNull(addedObject);
		
		verify(mockDeviceObject);
	}
	
	/**
	 * Description:
	 * 	Test method for addDevice method.
	 * Condition:
	 *  The device already exists in the database.
	 * Expect:
	 * 	Get proper IDeviceObject still.
	 *  Check the IDeviceObject properties set expectedly. 
	 */
	@Test
	public void testAddExistingDevice() {
		String strMacAddress = "99:99:99:99:99:99";
		long attachmentDpid = HexString.toLong("00:00:00:00:00:00:0a:01");
		short attachmentPort = 2;
		int intIpv4Address = InetAddresses.coerceToInteger(InetAddresses.forString("192.168.100.1"));
		
		IDevice device = getMockDevice(strMacAddress, attachmentDpid, attachmentPort, intIpv4Address);
		
		IDeviceObject mockDeviceObject = createMock(IDeviceObject.class);
		IPortObject mockPortObject = getMockPort(attachmentDpid, attachmentPort); 
		IIpv4Address mockIpv4Address = createMock(IIpv4Address.class);
		
		expect(mockOpe.searchDevice(strMacAddress)).andReturn(mockDeviceObject);
		expect(mockDeviceObject.getAttachedPorts()).andReturn(Collections.singleton(mockPortObject));
		expect(mockOpe.searchPort(HexString.toHexString(attachmentDpid), attachmentPort)).andReturn(mockPortObject);
		expect(mockDeviceObject.getIpv4Address(intIpv4Address)).andReturn(mockIpv4Address);
		expect(mockDeviceObject.getIpv4Addresses()).andReturn(Collections.singleton(mockIpv4Address));
		expect(mockIpv4Address.getIpv4Address()).andReturn(intIpv4Address);
		
		mockDeviceObject.setMACAddress(strMacAddress);
		mockDeviceObject.setType("device");
		mockDeviceObject.setState("ACTIVE");
		mockOpe.commit();
		
		replay(mockDeviceObject);
		replay(mockPortObject);
		replay(mockIpv4Address);
		replay(mockOpe);
		
		IDeviceObject addedObject = deviceImpl.addDevice(device);
		assertNotNull(addedObject);
		
		verify(mockDeviceObject);
	}
	
	/**
	 * Description:
	 * 	Test method for updateDevice method.
	 *  NB. this is the same test as testAddExistingDevice
	 * Condition:
	 * 	The MAC address and attachment point are the same. 
	 *  All of the other parameter are different.
	 * Expect:
	 * 	Changed parameters are set expectedly.
	 */
	@Test
	public void testAddUpdateDevice() {
		String strMacAddress = "99:99:99:99:99:99";
		long attachmentDpid = HexString.toLong("00:00:00:00:00:00:0a:01");
		short attachmentPort = 2;
		int intIpv4Address = InetAddresses.coerceToInteger(InetAddresses.forString("192.168.100.1"));
		
		IDevice device = getMockDevice(strMacAddress, attachmentDpid, attachmentPort, intIpv4Address);
		
		IDeviceObject mockDeviceObject = createMock(IDeviceObject.class);
		IPortObject mockPortObject = getMockPort(attachmentDpid, attachmentPort); 
		IIpv4Address mockIpv4Address = createMock(IIpv4Address.class);
		
		expect(mockOpe.searchDevice(strMacAddress)).andReturn(mockDeviceObject);
		expect(mockDeviceObject.getAttachedPorts()).andReturn(Collections.singleton(mockPortObject));
		expect(mockOpe.searchPort(HexString.toHexString(attachmentDpid), attachmentPort)).andReturn(mockPortObject);
		expect(mockDeviceObject.getIpv4Address(intIpv4Address)).andReturn(mockIpv4Address);
		expect(mockDeviceObject.getIpv4Addresses()).andReturn(Collections.singleton(mockIpv4Address));
		expect(mockIpv4Address.getIpv4Address()).andReturn(intIpv4Address);
		
		mockDeviceObject.setMACAddress(strMacAddress);
		mockDeviceObject.setType("device");
		mockDeviceObject.setState("ACTIVE");
		mockOpe.commit();
		
		replay(mockDeviceObject);
		replay(mockPortObject);
		replay(mockIpv4Address);
		replay(mockOpe);
		
		IDeviceObject addedObject = deviceImpl.updateDevice(device);
		assertNotNull(addedObject);
		
		verify(mockDeviceObject);
	}

	/**
	 * Description:
	 * 	Test method for testRemoveDevice method.
	 * Condition:
	 * 	1. Unregistered IDeviceObject argument is put. 
	 * Expect:
	 *  1. Nothing happen when unregistered IDeviceObject is put
	 * 	2. IDeviceObject will be removed.
	 */
	@Test
	public void testRemoveDevice() {
		String strMacAddress = "99:99:99:99:99:99";
		long attachmentDpid = HexString.toLong("00:00:00:00:00:00:0a:01");
		short attachmentPort = 2;
		int intIpv4Address = InetAddresses.coerceToInteger(InetAddresses.forString("192.168.100.1"));
		
		IIpv4Address ipv4AddressObject = createMock(IIpv4Address.class);
		IDeviceObject deviceObject = createMock(IDeviceObject.class);
		expect(deviceObject.getIpv4Addresses()).andReturn(Collections.singleton(ipv4AddressObject));
		replay(deviceObject);
		
		expect(mockOpe.searchDevice(strMacAddress)).andReturn(deviceObject);
		mockOpe.removeIpv4Address(ipv4AddressObject);
		mockOpe.removeDevice(deviceObject);
		mockOpe.commit();
		replay(mockOpe);
		
		IDevice device = getMockDevice(strMacAddress, attachmentDpid, attachmentPort, intIpv4Address);

		deviceImpl.removeDevice(device);
		
		verify(mockOpe);
	}

	/**
	 * Description:
	 * 	Test method for getDeviceByMac
	 * Condition:
	 * 	1. Unregistered MAC address argument is set
	 * Expect:
	 * 	1.Nothing happen when you put unregistered MAC address
	 *  2.Get the proper IDeviceObject.
	 *  3.Check the IDeviceObject properties set expectedly.
	 */
	@Test
	public void testGetDeviceByMac() {
		String mac = "99:99:99:99:99:99";
		
		IDeviceObject mockDevice = createMock(IDeviceObject.class);
		
		expect(mockOpe.searchDevice(mac)).andReturn(mockDevice);
		
		replay(mockOpe);
		
		IDeviceObject result = deviceImpl.getDeviceByMac(mac);
		assertNotNull(result);
		
		verify(mockOpe);
	}
	
	/**
	 * Description:
	 * 	Test method for getDeviceByIP method.
	 * Condition:
	 * 	1. Unregistered IP address argument is set
	 * Expect:
	 * 	1. Nothing happen when you put unregistered IP address
	 * 	2. Get the proper IDeviceObject.
	 *  3. Check the IDeviceObject properties set expectedly.
	 */
	@Test
	public void testGetDeviceByIP() {
		int nonExistingIp = InetAddresses.coerceToInteger(InetAddresses.forString("192.168.10.50"));
		int existingIp = InetAddresses.coerceToInteger(InetAddresses.forString("10.5.12.128"));
		
		IDeviceObject mockDevice = createMock(IDeviceObject.class);
		IIpv4Address mockExistingIp = createMock(IIpv4Address.class);
		expect(mockExistingIp.getDevice()).andReturn(mockDevice);
		
		expect(mockOpe.searchIpv4Address(nonExistingIp)).andReturn(null);
		expect(mockOpe.searchIpv4Address(existingIp)).andReturn(mockExistingIp);
		
		replay(mockExistingIp);
		replay(mockOpe);
		
		IDeviceObject result = deviceImpl.getDeviceByIP(nonExistingIp);
		assertNull(result);
		
		result = deviceImpl.getDeviceByIP(existingIp);
		assertNotNull(result);
		
		verify(mockOpe);
	}

	/**
	 * Description:
	 * 	Test method for testChangeDeviceAttachmentsIDevice
	 * Condition:
	 * 	1. The device is not currently attached to any point.
	 * Expect:
	 * 	1. Nothing happen when you put nonexistent attachment point.
	 * 	2. Set the attachment point expectedly;
	 */
	@Test
	public void testChangeDeviceAttachementsWhenUnattached() {
		String strMacAddress = "99:99:99:99:99:99";
		long attachmentDpid = HexString.toLong("00:00:00:00:00:00:0a:01");
		short attachmentPort = 2;
		int intIpv4Address = InetAddresses.coerceToInteger(InetAddresses.forString("192.168.100.1"));
		
		IDevice device = getMockDevice(strMacAddress, attachmentDpid, attachmentPort, intIpv4Address);
		
		IDeviceObject mockDeviceObject = createMock(IDeviceObject.class);
		IPortObject mockPortObject = getMockPort(attachmentDpid, attachmentPort); 
		
		expect(mockOpe.searchDevice(strMacAddress)).andReturn(mockDeviceObject);
		expect(mockDeviceObject.getAttachedPorts()).andReturn(Collections.<IPortObject>emptyList());
		expect(mockOpe.searchPort(HexString.toHexString(attachmentDpid), attachmentPort)).andReturn(mockPortObject);
		mockPortObject.setDevice(mockDeviceObject);
		mockOpe.commit();
		
		replay(mockDeviceObject);
		replay(mockPortObject);
		replay(mockOpe);
		
		deviceImpl.changeDeviceAttachments(device);
		
		verify(mockDeviceObject);
		verify(mockPortObject);
		verify(mockOpe);
	}
	
	/**
	 * Description:
	 * 	Test method for testChangeDeviceAttachmentsIDevice
	 * Condition:
	 * 	1. The device is currently attached to a switch, but this attachment point
	 *     has now changed.
	 * Expect:
	 * 	1. The device should be removed from the old attachment point.
	 * 	2. Set the attachment point expectedly;
	 */
	@Test
	public void testChangeDeviceAttachementsWhenAttached() {
		String strMacAddress = "99:99:99:99:99:99";
		long attachmentDpid = HexString.toLong("00:00:00:00:00:00:0a:01");
		short attachmentPort = 2;
		int intIpv4Address = InetAddresses.coerceToInteger(InetAddresses.forString("192.168.100.1"));
		
		//Details for the port the device will be moved from
		long alreadyAttachedDpid = HexString.toLong("00:00:00:00:00:00:0b:01");
		short alreadyAttachedPort = 5;
		
		IDevice device = getMockDevice(strMacAddress, attachmentDpid, attachmentPort, intIpv4Address);
		
		IDeviceObject mockDeviceObject = createMock(IDeviceObject.class);
		IPortObject mockPortObject = getMockPort(attachmentDpid, attachmentPort);
		IPortObject alreadyAttachedPortObject = getMockPort(alreadyAttachedDpid, alreadyAttachedPort);
		
		expect(mockOpe.searchDevice(strMacAddress)).andReturn(mockDeviceObject);
		expect(mockDeviceObject.getAttachedPorts()).andReturn(Collections.singletonList(alreadyAttachedPortObject));
		expect(mockOpe.searchPort(HexString.toHexString(attachmentDpid), attachmentPort)).andReturn(mockPortObject);
		mockPortObject.setDevice(mockDeviceObject);
		alreadyAttachedPortObject.removeDevice(mockDeviceObject);
		mockOpe.commit();
		
		replay(mockDeviceObject);
		replay(alreadyAttachedPortObject);
		replay(mockPortObject);
		replay(mockOpe);
		
		deviceImpl.changeDeviceAttachments(device);
		
		verify(mockDeviceObject);
		verify(alreadyAttachedPortObject);
		verify(mockPortObject);
		verify(mockOpe);
	}

	@Ignore
	@Test
	public void testChangeDeviceAttachmentsIDeviceIDeviceObject() {
		//It is tested by the testChangeDeviceAttachmentsIDevice
	}

	/**
	 * Description:
	 * 	Test method for testChangeDeviceIPv4Address
	 * Condition:
	 * 	N/A
	 * Expect:
	 *  1. Set the IP address expectedly.
	 */
	@Test
	public void testChangeDeviceIpv4Address() {
		String strMacAddress = "99:99:99:99:99:99";
		long attachmentDpid = HexString.toLong("00:00:00:00:00:00:0a:01");
		short attachmentPort = 2;
		int intIpv4Address = InetAddresses.coerceToInteger(InetAddresses.forString("192.168.100.1"));
		
		IDevice device = getMockDevice(strMacAddress, attachmentDpid, attachmentPort, intIpv4Address);
		
		IDeviceObject mockDeviceObject = createMock(IDeviceObject.class);
		IIpv4Address mockIpv4Address = createMock(IIpv4Address.class);
		
		expect(mockOpe.searchDevice(strMacAddress)).andReturn(mockDeviceObject);
		expect(mockDeviceObject.getIpv4Address(intIpv4Address)).andReturn(null);
		expect(mockOpe.ensureIpv4Address(intIpv4Address)).andReturn(mockIpv4Address);
		mockDeviceObject.addIpv4Address(mockIpv4Address);
		expect(mockDeviceObject.getIpv4Addresses()).andReturn(Collections.singletonList(mockIpv4Address));
		expect(mockIpv4Address.getIpv4Address()).andReturn(intIpv4Address);
		mockOpe.commit();
		
		replay(mockDeviceObject);
		replay(mockIpv4Address);
		replay(mockOpe);
		
		deviceImpl.changeDeviceIPv4Address(device);
		
		verify(mockDeviceObject);
		verify(mockIpv4Address);
		verify(mockOpe);
	}
	
	/**
	 * Description:
	 * 	Test method for testChangeDeviceIPv4Address
	 * Condition:
	 * 	1. The device had an old IP address which has now changed.
	 * Expect:
	 *  1. The old IP address should be removed from the device.
	 *  2. Set the IP address expectedly.
	 */
	@Test
	public void testChangeDeviceIpv4AddressAndRemoveExisting() {
		String strMacAddress = "99:99:99:99:99:99";
		long attachmentDpid = HexString.toLong("00:00:00:00:00:00:0a:01");
		short attachmentPort = 2;
		int intIpv4Address = InetAddresses.coerceToInteger(InetAddresses.forString("192.168.100.1"));
		
		IDevice device = getMockDevice(strMacAddress, attachmentDpid, attachmentPort, intIpv4Address);
		
		IDeviceObject mockDeviceObject = createMock(IDeviceObject.class);
		
		IIpv4Address mockIpv4Address = createMock(IIpv4Address.class);
		IIpv4Address mockDeletingIpv4Address = createMock(IIpv4Address.class);
		List<IIpv4Address> ipv4Vertices = new ArrayList<IIpv4Address>(2);
		ipv4Vertices.add(mockIpv4Address);
		ipv4Vertices.add(mockDeletingIpv4Address);
		
		expect(mockOpe.searchDevice(strMacAddress)).andReturn(mockDeviceObject);
		expect(mockDeviceObject.getIpv4Address(intIpv4Address)).andReturn(null);
		expect(mockOpe.ensureIpv4Address(intIpv4Address)).andReturn(mockIpv4Address);
		mockDeviceObject.addIpv4Address(mockIpv4Address);
		expect(mockDeviceObject.getIpv4Addresses()).andReturn(ipv4Vertices);
		expect(mockIpv4Address.getIpv4Address()).andReturn(intIpv4Address);
		expect(mockDeletingIpv4Address.getIpv4Address()).andReturn(1);
		mockDeviceObject.removeIpv4Address(mockDeletingIpv4Address);
		mockOpe.commit();
		
		replay(mockDeviceObject);
		replay(mockIpv4Address);
		replay(mockOpe);
		
		deviceImpl.changeDeviceIPv4Address(device);
		
		verify(mockDeviceObject);
		verify(mockIpv4Address);
		verify(mockOpe);
	}

}
