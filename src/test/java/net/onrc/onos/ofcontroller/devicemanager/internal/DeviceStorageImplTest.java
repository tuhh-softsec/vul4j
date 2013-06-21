package net.onrc.onos.ofcontroller.devicemanager.internal;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.SwitchPort;
import net.floodlightcontroller.packet.IPv4;
import net.onrc.onos.graph.GraphDBConnection;
import net.onrc.onos.graph.GraphDBOperation;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IDeviceObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IPortObject;
import net.onrc.onos.ofcontroller.core.internal.DeviceStorageImpl;
import net.onrc.onos.ofcontroller.core.internal.SwitchStorageImpl;
import net.floodlightcontroller.devicemanager.internal.Device;
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
import com.thinkaurelius.titan.core.TitanFactory;

//Add Powermock preparation
@RunWith(PowerMockRunner.class)
@PrepareForTest({TitanFactory.class, GraphDBConnection.class, GraphDBOperation.class, DeviceStorageImpl.class})
public class DeviceStorageImplTest{ //extends FloodlightTestCase{
	
	protected static Logger log = LoggerFactory.getLogger(SwitchStorageImpl.class);
	
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
		mockOpe.close();
		PowerMock.replay(GraphDBOperation.class);
        // Replace the conf to dummy conf
		// String conf = "/tmp/cassandra.titan";

		

	}

	@After
	public void tearDown() throws Exception {	
		deviceImpl.close();
		deviceImpl = null;
		
		verify(mockOpe);
	}
	
	private String makeIPStringFromArray(Integer[] ipaddresses){
        String multiIntString = "";
        for(Integer intValue : ipaddresses)
        {
        	if (multiIntString == null || multiIntString.isEmpty()){
        		multiIntString = "[" + IPv4.fromIPv4Address(intValue);
        	}
        	else{
           		multiIntString += "," + IPv4.fromIPv4Address(intValue);
        	}
        }
        return multiIntString + "]";
	}
	

	/**
	 * Desc:
	 *  Test method for addDevice method.
	 * Codition:
	 *  N/A
	 * Expect:
	 * 	Get proper IDeviceObject
	 */
	//@Ignore
	@Test
	public void testAddNewDevice() {
		try 
		{	   
			//Make mockDevice
			IDevice mockDev = createMock(Device.class);
			// Mac addr for test device.
			String macAddr = "99:99:99:99:99:99";
			// IP addr for test device
			String ip = "192.168.100.1";
			Integer[] ipaddrs = {IPv4.toIPv4Address(ip)};
			// Mac addr for attached switch
			String switchMacAddr = "00:00:00:00:00:00:0a:01";		
			long switchMacAddrL = HexString.toLong(switchMacAddr);
			// Port number for attached switch
			short portNum = 2; 
			SwitchPort sp1 = new SwitchPort(switchMacAddrL, portNum);
			SwitchPort[] sps = {sp1};
		
			expect(mockDev.getMACAddressString()).andReturn(macAddr).anyTimes();
			expect(mockDev.getIPv4Addresses()).andReturn(ipaddrs).anyTimes();
			expect(mockDev.getAttachmentPoints()).andReturn(sps).anyTimes();
			replay(mockDev);
			
			//Mock IPortObject 1 with dpid "00:00:00:00:00:00:0a:01" and port "1"
			IPortObject mockIPort = createMock(IPortObject.class);
			mockIPort.setNumber(portNum);
			mockIPort.setType("port");
			String iPortDesc = "port 1 at SEA Switch";
			expect(mockIPort.getNumber()).andReturn(portNum).anyTimes();
			expect(mockIPort.getDesc()).andReturn(iPortDesc).anyTimes();
			replay(mockIPort);
			
			//Make Iterator for mockIport
			List<IPortObject> portList = new ArrayList<IPortObject>();
			portList.add(mockIPort);
			
			//Expectation for mockIDeviceObject
			IDeviceObject mockIDev = createMock(IDeviceObject.class);	
			expect(mockIDev.getAttachedPorts()).andReturn(portList);
			mockIDev.setIPAddress(makeIPStringFromArray(ipaddrs));
			mockIDev.setMACAddress(macAddr);
			mockIDev.setType("device");
			mockIDev.setState("ACTIVE");
			replay(mockIDev);	
			
			//Expectation for mockOpe
			expect(mockOpe.searchDevice(macAddr)).andReturn(null);
			expect(mockOpe.newDevice()).andReturn(mockIDev);
			expect(mockOpe.searchPort(switchMacAddr, portNum)).andReturn(mockIPort);
			mockOpe.commit();
			replay(mockOpe);				
			
			deviceImpl.init(conf);
			
			//Add the device
	        IDeviceObject obj = deviceImpl.addDevice(mockDev);	
			assertNotNull(obj);
			
			verify(mockIDev);
			
		} catch(Exception e) {
			fail(e.getMessage());
		}
	}
	
	/**
	 * Desc:
	 * 	Test method for addDevice method.
	 * Condition:
	 * 	Already added device is existed.
	 * Expect:
	 * 	Get proper IDeviceObject still.
	 *  Check the IDeviceObject properties set expectedly. 
	 */
	//@Ignore
	@Test
	public void testAddDeviceExisting() {
		try 
		{	   
			IDevice mockDev = createMock(Device.class);
			String macAddr = "99:99:99:99:99:99";
			String ip = "192.168.100.1";
			Integer[] ipaddrs = {IPv4.toIPv4Address(ip)};
			String switchMacAddr = "00:00:00:00:00:00:0a:01";		
			long switchMacAddrL = HexString.toLong(switchMacAddr);
			short portNum = 2; 
			SwitchPort sp1 = new SwitchPort(switchMacAddrL, portNum);
			SwitchPort[] sps = {sp1};
		
			expect(mockDev.getMACAddressString()).andReturn(macAddr).anyTimes();
			expect(mockDev.getIPv4Addresses()).andReturn(ipaddrs).times(2);
			expect(mockDev.getAttachmentPoints()).andReturn(sps).times(2);
			replay(mockDev);
			
			//Mock IPortObject 1 with dpid "00:00:00:00:00:00:0a:01" and port "1"
			IPortObject mockIPort = createMock(IPortObject.class);
			mockIPort.setNumber(portNum);
			mockIPort.setType("port");
			String iPortDesc = "port 1 at SEA Switch";
			expect(mockIPort.getNumber()).andReturn(portNum).anyTimes();
			expect(mockIPort.getDesc()).andReturn(iPortDesc).anyTimes();
			replay(mockIPort);
			
			//Make Iterator for mockIport
			List<IPortObject> portList = new ArrayList<IPortObject>();
			portList.add(mockIPort);
			
			//Expectation for mockIDeviceObject
			IDeviceObject mockIDev = createMock(IDeviceObject.class);	
			expect(mockIDev.getAttachedPorts()).andReturn(portList).anyTimes();
			mockIDev.setIPAddress(makeIPStringFromArray(ipaddrs));
			mockIDev.setMACAddress(macAddr);
			mockIDev.setType("device");
			mockIDev.setState("ACTIVE");
			mockIDev.setIPAddress(makeIPStringFromArray(ipaddrs));
			mockIDev.setMACAddress(macAddr);
			mockIDev.setType("device");
			mockIDev.setState("ACTIVE");
			replay(mockIDev);	
			
			//Expectation for mockOpe
			expect(mockOpe.searchDevice(macAddr)).andReturn(null);
			expect(mockOpe.newDevice()).andReturn(mockIDev);
			expect(mockOpe.searchPort(switchMacAddr, portNum)).andReturn(mockIPort);
			mockOpe.commit();
			expect(mockOpe.searchDevice(macAddr)).andReturn(mockIDev);
			expect(mockOpe.searchPort(switchMacAddr, portNum)).andReturn(mockIPort);
			mockOpe.commit();
			replay(mockOpe);				
			
			deviceImpl.init(conf);
			
			//Add the device
	        IDeviceObject obj = deviceImpl.addDevice(mockDev);	
			assertNotNull(obj);
			
			//Add the same device
	        IDeviceObject obj2 = deviceImpl.addDevice(mockDev);
			assertNotNull(obj2);

		} catch(Exception e) {
			fail(e.getMessage());
		}
	}
	
	/**
	 * Desc:
	 * 	Test method for updateDevice method.
	 * Condition:
	 * 	The mac address and attachment point are the same. 
	 *  All of the other parameter are different.
	 * Expect:
	 * 	Changed parameters are set expectedly.
	 */
	//@Ignore
	@Test
	public void testUpdateDevice() {
		try
		{
			IDevice mockDev = createMock(Device.class);
			String macAddr = "99:99:99:99:99:99";
			String ip = "192.168.100.1";
			Integer ipInt = IPv4.toIPv4Address(ip);
			Integer[] ipaddrs = {ipInt};
			String switchMacAddr = "00:00:00:00:00:00:0a:01";		
			long switchMacAddrL = HexString.toLong(switchMacAddr);
			short portNum = 2; 
			SwitchPort sp1 = new SwitchPort(switchMacAddrL, portNum);
			SwitchPort[] sps = {sp1};
			
			expect(mockDev.getMACAddressString()).andReturn(macAddr).anyTimes();
			expect(mockDev.getIPv4Addresses()).andReturn(ipaddrs);
			expect(mockDev.getAttachmentPoints()).andReturn(sps);
			replay(mockDev);
			
			//Dev2 (attached port is the same)
			IDevice mockDev2 = createMock(Device.class);
			String ip2 = "192.168.100.2";
			Integer ipInt2 = IPv4.toIPv4Address(ip2);
			Integer[] ipaddrs2 = {ipInt2};
			
			expect(mockDev2.getMACAddressString()).andReturn(macAddr).anyTimes();
			expect(mockDev2.getIPv4Addresses()).andReturn(ipaddrs2);
			expect(mockDev2.getAttachmentPoints()).andReturn(sps);
			replay(mockDev2);
			
			//Mock IPortObject 1 with dpid "00:00:00:00:00:00:0a:01" and port "1"
			IPortObject mockIPort = createMock(IPortObject.class);
			mockIPort.setNumber(portNum);
			mockIPort.setType("port");
			String iPortDesc = "port 1 at SEA Switch";
			expect(mockIPort.getNumber()).andReturn(portNum).anyTimes();
			expect(mockIPort.getDesc()).andReturn(iPortDesc).anyTimes();
			replay(mockIPort);
			
			//Make Iterator for mockIport
			List<IPortObject> portList = new ArrayList<IPortObject>();
			portList.add(mockIPort);
			
			//Expectation for mockIDeviceObject
			IDeviceObject mockIDev = createMock(IDeviceObject.class);	
			expect(mockIDev.getAttachedPorts()).andReturn(portList).anyTimes();
			mockIDev.setIPAddress(makeIPStringFromArray(ipaddrs));
			mockIDev.setMACAddress(macAddr);
			mockIDev.setType("device");
			mockIDev.setState("ACTIVE");
			mockIDev.setIPAddress(makeIPStringFromArray(ipaddrs2));
			mockIDev.setMACAddress(macAddr);
			mockIDev.setType("device");
			mockIDev.setState("ACTIVE");
			replay(mockIDev);	
			
			//Expectation for mockOpe
			expect(mockOpe.searchDevice(macAddr)).andReturn(null);
			expect(mockOpe.newDevice()).andReturn(mockIDev);
			expect(mockOpe.searchPort(switchMacAddr, portNum)).andReturn(mockIPort);
			mockOpe.commit();
			expect(mockOpe.searchDevice(macAddr)).andReturn(mockIDev);
			expect(mockOpe.searchPort(switchMacAddr, portNum)).andReturn(mockIPort);
			mockOpe.commit();
			replay(mockOpe);				
			
			deviceImpl.init(conf);
			
	        IDeviceObject obj = deviceImpl.addDevice(mockDev);	
			assertNotNull(obj);
			
			//update theDevice
			IDeviceObject obj2 = deviceImpl.updateDevice(mockDev2);
			assertNotNull(obj2);
			
			verify(mockIDev);
			
		} catch(Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Desc:
	 * 	Test method for testRemoveDevice method.
	 * Condition:
	 * 	1. Unregistered IDeviceObject argument is put. 
	 * Expect:
	 *  1. Nothing happen when unregistered IDeviceObject is put
	 * 	2. IDeviceObject will be removed.
	 */
	//@Ignore
	@Test
	public void testRemoveDevice() {
		try
		{
			IDevice mockDev = createMock(Device.class);
			String macAddr = "99:99:99:99:99:99";
			String ip = "192.168.100.1";
			Integer ipInt = IPv4.toIPv4Address(ip);
			Integer[] ipaddrs = {ipInt};
			String switchMacAddr = "00:00:00:00:00:00:0a:01";		
			long switchMacAddrL = HexString.toLong(switchMacAddr);
			short portNum = 2; 
			SwitchPort sp1 = new SwitchPort(switchMacAddrL, portNum);
			SwitchPort[] sps = {sp1};
			
			expect(mockDev.getMACAddressString()).andReturn(macAddr).anyTimes();
			expect(mockDev.getAttachmentPoints()).andReturn(sps);
			expect(mockDev.getIPv4Addresses()).andReturn(ipaddrs);
			replay(mockDev);
			
			//Dev2 (attached port is the same)
			IDevice mockDev2 = createMock(Device.class);
			String macAddr2 = "33:33:33:33:33:33";
			expect(mockDev2.getMACAddressString()).andReturn(macAddr2).anyTimes();
			expect(mockDev2.getIPv4Addresses()).andReturn(ipaddrs);
			expect(mockDev2.getAttachmentPoints()).andReturn(sps);
			replay(mockDev2);
			
			//Mock IPortObject 1 with dpid "00:00:00:00:00:00:0a:01" and port "1"
			IPortObject mockIPort = createMock(IPortObject.class);
			mockIPort.setNumber(portNum);
			mockIPort.setType("port");
			String iPortDesc = "port 1 at SEA Switch";
			expect(mockIPort.getNumber()).andReturn(portNum).anyTimes();
			expect(mockIPort.getDesc()).andReturn(iPortDesc).anyTimes();
			replay(mockIPort);
			
			//Make Iterator for mockIport
			List<IPortObject> portList = new ArrayList<IPortObject>();
			portList.add(mockIPort);
			
			//Expectation for mockIDeviceObject
			IDeviceObject mockIDev = createMock(IDeviceObject.class);	
			expect(mockIDev.getAttachedPorts()).andReturn(portList);
			mockIDev.setIPAddress(makeIPStringFromArray(ipaddrs));
			mockIDev.setMACAddress(macAddr);
			mockIDev.setType("device");
			mockIDev.setState("ACTIVE");
			replay(mockIDev);	
			
			//Expectation for mockOpe
			expect(mockOpe.searchDevice(macAddr)).andReturn(null);
			expect(mockOpe.newDevice()).andReturn(mockIDev);
			expect(mockOpe.searchPort(switchMacAddr, portNum)).andReturn(mockIPort);
			mockOpe.commit();
			expect(mockOpe.searchDevice(macAddr2)).andReturn(null);
			expect(mockOpe.searchDevice(macAddr)).andReturn(mockIDev);
			expect(mockOpe.searchDevice(macAddr)).andReturn(mockIDev);
			mockOpe.removeDevice(mockIDev);	
			mockOpe.commit();
			expect(mockOpe.searchDevice(macAddr)).andReturn(null);
			replay(mockOpe);				
			
			deviceImpl.init(conf);
			
	        IDeviceObject obj = deviceImpl.addDevice(mockDev);	
			assertNotNull(obj);

			deviceImpl.removeDevice(mockDev2);
		    IDeviceObject dev = deviceImpl.getDeviceByMac(macAddr);
		    assertNotNull(dev);
			
			deviceImpl.removeDevice(mockDev);		
		    IDeviceObject dev2 = deviceImpl.getDeviceByMac(macAddr);
		    assertNull(dev2);
		    
			verify(mockIDev);

		} catch(Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Desc:
	 * 	Test method for getDeviceByMac
	 * Condition:
	 * 	1. Unregistered mac address argument is set
	 * Expect:
	 * 	1.Nothing happen when you put unregistered mac address
	 *  2.Get the proper IDeviceObject.
	 *  3.Check the IDeviceObject properties set expectedly.
	 */
	//@Ignore
	@Test
	public void testGetDeviceByMac() {
		try
		{
			IDevice mockDev = createMock(Device.class);
			String macAddr = "99:99:99:99:99:99";
			String ip = "192.168.100.1";
			Integer ipInt = IPv4.toIPv4Address(ip);
			Integer[] ipaddrs = {ipInt};
			String switchMacAddr = "00:00:00:00:00:00:0a:01";		
			long switchMacAddrL = HexString.toLong(switchMacAddr);
			short portNum = 2; 
			SwitchPort sp1 = new SwitchPort(switchMacAddrL, portNum);
			SwitchPort[] sps = {sp1};
			
			String dummyMac = "33:33:33:33:33:33";
			
			expect(mockDev.getMACAddressString()).andReturn(macAddr).anyTimes();
			expect(mockDev.getIPv4Addresses()).andReturn(ipaddrs);
			expect(mockDev.getAttachmentPoints()).andReturn(sps);
			replay(mockDev);
			
			//Mock IPortObject 1 with dpid "00:00:00:00:00:00:0a:01" and port "1"
			IPortObject mockIPort = createMock(IPortObject.class);
			mockIPort.setNumber(portNum);
			mockIPort.setType("port");
			String iPortDesc = "port 1 at SEA Switch";
			expect(mockIPort.getNumber()).andReturn(portNum).anyTimes();
			expect(mockIPort.getDesc()).andReturn(iPortDesc).anyTimes();
			replay(mockIPort);
			
			//Make Iterator for mockIport
			List<IPortObject> portList = new ArrayList<IPortObject>();
			portList.add(mockIPort);
			
			//Expectation for mockIDeviceObject
			IDeviceObject mockIDev = createMock(IDeviceObject.class);	
			expect(mockIDev.getAttachedPorts()).andReturn(portList);
			mockIDev.setIPAddress(makeIPStringFromArray(ipaddrs));
			mockIDev.setMACAddress(macAddr);
			mockIDev.setType("device");
			mockIDev.setState("ACTIVE");
			replay(mockIDev);	
			
			//Expectation for mockOpe
			expect(mockOpe.searchDevice(macAddr)).andReturn(null);
			expect(mockOpe.newDevice()).andReturn(mockIDev);
			expect(mockOpe.searchPort(switchMacAddr, portNum)).andReturn(mockIPort);
			mockOpe.commit();
			expect(mockOpe.searchDevice(dummyMac)).andReturn(null);
			expect(mockOpe.searchDevice(macAddr)).andReturn(mockIDev);
			replay(mockOpe);				
			
			deviceImpl.init(conf);
			
	        IDeviceObject obj = deviceImpl.addDevice(mockDev);	
			assertNotNull(obj);
			
		    IDeviceObject dummyDev = deviceImpl.getDeviceByMac(dummyMac);
		    assertNull(dummyDev);	
			
		    IDeviceObject dev = deviceImpl.getDeviceByMac(macAddr);
		    assertNotNull(dev);
		    
			verify(mockIDev);

		} catch(Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Desc:
	 * 	Test method for getDeviceByIP method.
	 * Condition:
	 * 	1. Unregistered ip address argument is set
	 * Expect:
	 * 	1. Nothing happen when you put unregistered mac address
	 * 	2. Get the proper IDeviceObject.
	 *  3. Check the IDeviceObject properties set expectedly.
	 */
	//@Ignore
	@Test
	public void testGetDeviceByIP() {
		try
		{
			IDevice mockDev = createMock(Device.class);
			String macAddr = "99:99:99:99:99:99";
			String ip = "192.168.100.1";
			String ip2 = "192.168.100.2";
			Integer ipInt = IPv4.toIPv4Address(ip);
			Integer ipInt2 = IPv4.toIPv4Address(ip2);
			Integer[] ipaddrs = {ipInt, ipInt2};
			String switchMacAddr = "00:00:00:00:00:00:0a:01";		
			long switchMacAddrL = HexString.toLong(switchMacAddr);
			short portNum = 2; 
			SwitchPort sp1 = new SwitchPort(switchMacAddrL, portNum);
			SwitchPort[] sps = {sp1};
			
			String dummyIP = "222.222.222.222";
			
			expect(mockDev.getMACAddressString()).andReturn(macAddr).anyTimes();
			expect(mockDev.getIPv4Addresses()).andReturn(ipaddrs);
			expect(mockDev.getAttachmentPoints()).andReturn(sps);
			replay(mockDev);
			
			//Mock IPortObject 1 with dpid "00:00:00:00:00:00:0a:01" and port "1"
			IPortObject mockIPort = createMock(IPortObject.class);
			mockIPort.setNumber(portNum);
			mockIPort.setType("port");
			String iPortDesc = "port 1 at SEA Switch";
			expect(mockIPort.getNumber()).andReturn(portNum).anyTimes();
			expect(mockIPort.getDesc()).andReturn(iPortDesc).anyTimes();
			replay(mockIPort);
			
			//Make Iterator for mockIport
			List<IPortObject> portList = new ArrayList<IPortObject>();
			portList.add(mockIPort);
			
			//Expectation for mockIDeviceObject
			IDeviceObject mockIDev = createMock(IDeviceObject.class);	
			expect(mockIDev.getAttachedPorts()).andReturn(portList);
			expect(mockIDev.getIPAddress()).andReturn(makeIPStringFromArray(ipaddrs)).times(2);
			mockIDev.setIPAddress(makeIPStringFromArray(ipaddrs));
			mockIDev.setMACAddress(macAddr);
			mockIDev.setType("device");
			mockIDev.setState("ACTIVE");
			replay(mockIDev);	
			
			
			//Make mock Iterator for IDeviceObject
			List<IDeviceObject> deviceList = new ArrayList<IDeviceObject>();
			deviceList.add(mockIDev);	
			
			//Expectation for mockOpe
			expect(mockOpe.searchDevice(macAddr)).andReturn(null);
			expect(mockOpe.newDevice()).andReturn(mockIDev);
			expect(mockOpe.searchPort(switchMacAddr, portNum)).andReturn(mockIPort);
			mockOpe.commit();
			expect(mockOpe.getDevices()).andReturn(deviceList).times(2);
			replay(mockOpe);				
			
			deviceImpl.init(conf);
	
	        IDeviceObject obj = deviceImpl.addDevice(mockDev);	
			assertNotNull(obj);
			
		    IDeviceObject dummyDev = deviceImpl.getDeviceByIP(dummyIP);
		    assertNull(dummyDev);
			
		    IDeviceObject dev = deviceImpl.getDeviceByIP(ip);
		    assertNotNull(dev);
		    
			verify(mockIDev);

			
		} catch(Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Desc:
	 * 	Test method for testChangeDeviceAttachmentsIDevice
	 * Condition:
	 * 	1. Unexisting attachment point argument is set
	 * Expect:
	 * 	1. Nothing happen when you put unexisting attachment point.
	 * 	2. Set the attachment point expectedly;
	 */
	//@Ignore
	@Test
	public void testChangeDeviceAttachmentsIDevice() {
		try
		{
			IDevice mockDev = createMock(Device.class);
			String macAddr = "99:99:99:99:99:99";
			String ip = "192.168.100.1";
			Integer ipInt = IPv4.toIPv4Address(ip);
			Integer[] ipaddrs = {ipInt};
			String switchMacAddr = "00:00:00:00:00:00:0a:01";		
			long switchMacAddrL = HexString.toLong(switchMacAddr);
			short portNum = 2; 
			SwitchPort sp1 = new SwitchPort(switchMacAddrL, portNum);
			SwitchPort[] sps = {sp1};
			
			expect(mockDev.getMACAddressString()).andReturn(macAddr).anyTimes();
			expect(mockDev.getIPv4Addresses()).andReturn(ipaddrs);
			expect(mockDev.getAttachmentPoints()).andReturn(sps);
			replay(mockDev);
			
			//Dev2
			IDevice mockDev2 = createMock(Device.class);
			String switchMacAddr2 = "00:00:00:00:00:00:0a:02";
			long lSwitchMacAddr2 = HexString.toLong(switchMacAddr2);
			short portNum2 = 2; 
			SwitchPort sp2 = new SwitchPort(lSwitchMacAddr2, portNum2);
			SwitchPort sps2[] = {sp2};
			
			expect(mockDev2.getMACAddressString()).andReturn(macAddr).anyTimes();
			expect(mockDev2.getIPv4Addresses()).andReturn(ipaddrs);
			expect(mockDev2.getAttachmentPoints()).andReturn(sps2);
			replay(mockDev2);
			
			//Dev3
			IDevice mockDev3 = createMock(Device.class);
			String switchMacAddr3 = "00:00:00:00:00:00:00:00";
			long lSwitchMacAddr3 = HexString.toLong(switchMacAddr3);
			short portNum3 = 1; 
			SwitchPort sp3 = new SwitchPort(lSwitchMacAddr3, portNum3);
			SwitchPort sps3[] = {sp3};
			
			expect(mockDev3.getMACAddressString()).andReturn(macAddr).anyTimes();
			expect(mockDev3.getIPv4Addresses()).andReturn(ipaddrs);
			expect(mockDev3.getAttachmentPoints()).andReturn(sps3);
			replay(mockDev3);
			
			IDeviceObject mockIDev = createMock(IDeviceObject.class);	
			
			//Mock IPortObject 1 with dpid "00:00:00:00:00:00:0a:01" and port "1"
			IPortObject mockIPort = createMock(IPortObject.class);
			mockIPort.setNumber(portNum);
			mockIPort.setType("port");
			String iPortDesc = "port 1 at SEA Switch";
			expect(mockIPort.getNumber()).andReturn(portNum).anyTimes();
			expect(mockIPort.getDesc()).andReturn(iPortDesc).anyTimes();
			mockIPort.removeDevice(mockIDev);
			mockIPort.removeDevice(mockIDev);
			replay(mockIPort);
			
			//Make Iterator for mockIport
			List<IPortObject> portList = new ArrayList<IPortObject>();
			portList.add(mockIPort);
			
			//Expectation for mockIDeviceObject
			expect(mockIDev.getAttachedPorts()).andReturn(portList).anyTimes();
			mockIDev.setIPAddress(makeIPStringFromArray(ipaddrs));
			mockIDev.setMACAddress(macAddr);
			mockIDev.setType("device");
			mockIDev.setState("ACTIVE");
			replay(mockIDev);	
			
			//Mock IPortObject 2 with dpid "00:00:00:00:00:00:0a:02" and port "2"
			IPortObject mockIPort2 = createMock(IPortObject.class);
			mockIPort2.setNumber(portNum2);
			mockIPort2.setType("port");
			String iPortDesc2 = "port 2 at LAX Switch";
			expect(mockIPort2.getNumber()).andReturn(portNum2).anyTimes();
			expect(mockIPort2.getDesc()).andReturn(iPortDesc2).anyTimes();
			mockIPort2.setDevice(mockIDev);
			replay(mockIPort2);
			
			//Make Iterator for mockIport
			List<IPortObject> portList2 = new ArrayList<IPortObject>();
			portList2.add(mockIPort2);
			
			//Mock IPortObject 3 with dpid "00:00:00:00:00:00:00:00" and port "1"
			IPortObject mockIPort3 = createMock(IPortObject.class);
			mockIPort3.setNumber(portNum3);
			mockIPort3.setType("port");
			String iPortDesc3 = "n/a";
			expect(mockIPort3.getNumber()).andReturn(portNum3).anyTimes();
			expect(mockIPort3.getDesc()).andReturn(iPortDesc3).anyTimes();
			mockIPort3.setDevice(mockIDev);
			replay(mockIPort3);
			
			//Expectation for mockOpe
			expect(mockOpe.searchDevice(macAddr)).andReturn(null);
			expect(mockOpe.newDevice()).andReturn(mockIDev);
			expect(mockOpe.searchPort(switchMacAddr, portNum)).andReturn(mockIPort);
			mockOpe.commit();
			expect(mockOpe.searchDevice(macAddr)).andReturn(mockIDev);
			expect(mockOpe.searchPort(switchMacAddr2, portNum2)).andReturn(mockIPort2);
			mockOpe.commit();
			expect(mockOpe.searchDevice(macAddr)).andReturn(mockIDev);
			expect(mockOpe.searchPort(switchMacAddr3, portNum3)).andReturn(null);
			mockOpe.commit();
			replay(mockOpe);				
			
			deviceImpl.init(conf);
	
	        IDeviceObject obj = deviceImpl.addDevice(mockDev);	
			assertNotNull(obj);
			
		    deviceImpl.changeDeviceAttachments(mockDev2);
			
		    deviceImpl.changeDeviceAttachments(mockDev3);
		    
			verify(mockIDev);

			
		} catch(Exception e) {
			fail(e.getMessage());
		}
	}

	@Ignore
	@Test
	public void testChangeDeviceAttachmentsIDeviceIDeviceObject() {
		//It is tested by the testChangeDeviceAttachmentsIDevice
	}

	/**
	 * Desc:
	 * 	Test method for testChangeDeviceIPv4Address
	 * Condition:
	 * 	N/A
	 * Expect:
	 *  1. Set the ipadress expectedly.
	 */
	//@Ignore
	@Test
	public void testChangeDeviceIPv4Address() {
		try
		{
			//Dev1
			IDevice mockDev = createMock(Device.class);
			String macAddr = "99:99:99:99:99:99";
			String ip = "192.168.100.1";
			Integer ipInt = IPv4.toIPv4Address(ip);
			Integer[] ipaddrs = {ipInt};
			String switchMacAddr = "00:00:00:00:00:00:0a:01";		
			long switchMacAddrL = HexString.toLong(switchMacAddr);
			short portNum = 2; 
			SwitchPort sp1 = new SwitchPort(switchMacAddrL, portNum);
			SwitchPort[] sps = {sp1};
			
			expect(mockDev.getMACAddressString()).andReturn(macAddr).anyTimes();
			expect(mockDev.getIPv4Addresses()).andReturn(ipaddrs);
			expect(mockDev.getAttachmentPoints()).andReturn(sps);
			replay(mockDev);
			
			//Dev2
			IDevice mockDev2 = createMock(Device.class);
			String ip2 = "192.168.100.2";
			Integer ipInt2 = IPv4.toIPv4Address(ip2);
			Integer[] ipaddrs2 = {ipInt2};
			expect(mockDev2.getMACAddressString()).andReturn(macAddr);
			expect(mockDev2.getIPv4Addresses()).andReturn(ipaddrs2);
			replay(mockDev2);
			
			//Mock IPortObject 1 with dpid "00:00:00:00:00:00:0a:01" and port "1"
			IPortObject mockIPort = createMock(IPortObject.class);
			mockIPort.setNumber(portNum);
			mockIPort.setType("port");
			String iPortDesc = "port 1 at SEA Switch";
			expect(mockIPort.getNumber()).andReturn(portNum).anyTimes();
			expect(mockIPort.getDesc()).andReturn(iPortDesc).anyTimes();
			replay(mockIPort);
			
			//Make Iterator for mockIport
			List<IPortObject> portList = new ArrayList<IPortObject>();
			portList.add(mockIPort);
			
			//Expectation for mockIDeviceObject
			IDeviceObject mockIDev = createMock(IDeviceObject.class);	
			expect(mockIDev.getAttachedPorts()).andReturn(portList);
			mockIDev.setIPAddress(makeIPStringFromArray(ipaddrs));
			mockIDev.setMACAddress(macAddr);
			mockIDev.setType("device");
			mockIDev.setState("ACTIVE");
			mockIDev.setIPAddress(makeIPStringFromArray(ipaddrs2));
			replay(mockIDev);	
			
			//Expectation for mockOpe
			expect(mockOpe.searchDevice(macAddr)).andReturn(null);
			expect(mockOpe.newDevice()).andReturn(mockIDev);
			expect(mockOpe.searchPort(switchMacAddr, portNum)).andReturn(mockIPort);
			mockOpe.commit();
			expect(mockOpe.searchDevice(macAddr)).andReturn(mockIDev);
			mockOpe.commit();
			replay(mockOpe);				
			
			deviceImpl.init(conf);
			
	        IDeviceObject obj = deviceImpl.addDevice(mockDev);	
			assertNotNull(obj);

	        deviceImpl.changeDeviceIPv4Address(mockDev2);	
	        
			verify(mockIDev);

					
		} 
		catch(Exception e) {
			fail(e.getMessage());
		}
	}

}
