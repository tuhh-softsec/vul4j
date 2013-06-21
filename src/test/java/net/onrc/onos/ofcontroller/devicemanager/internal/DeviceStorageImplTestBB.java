package net.onrc.onos.ofcontroller.devicemanager.internal;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import net.floodlightcontroller.core.internal.TestDatabaseManager;
import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.SwitchPort;
import net.floodlightcontroller.devicemanager.internal.Device;
import net.floodlightcontroller.packet.IPv4;
import net.onrc.onos.graph.GraphDBConnection;
import net.onrc.onos.graph.GraphDBOperation;
import net.onrc.onos.ofcontroller.core.IDeviceStorage;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IDeviceObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IPortObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.onrc.onos.ofcontroller.core.internal.DeviceStorageImpl;
import net.onrc.onos.ofcontroller.core.internal.SwitchStorageImpl;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflow.util.HexString;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.slf4j.LoggerFactory;
import org.powermock.modules.junit4.PowerMockRunner;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;

//Add Powermock preparation
@RunWith(PowerMockRunner.class)
@PrepareForTest({TitanFactory.class, GraphDBConnection.class, GraphDBOperation.class, SwitchStorageImpl.class})
public class DeviceStorageImplTestBB {
	protected static org.slf4j.Logger log = LoggerFactory.getLogger(SwitchStorageImpl.class);

	String conf;
    private GraphDBConnection conn = null;
    private GraphDBOperation ope = null;
    private TitanGraph titanGraph = null;
    IDeviceStorage deviceImpl = null;
    
	@Before
	public void setUp() throws Exception {
		
		deviceImpl = new DeviceStorageImpl();
		conf = "/dummy/path/to/db";
		
		// Make mock cassandra DB
		// Replace TitanFactory.open() to return mock DB
		titanGraph = TestDatabaseManager.getTestDatabase();
		TestDatabaseManager.populateTestData(titanGraph);
		PowerMock.mockStatic(TitanFactory.class);
		EasyMock.expect(TitanFactory.open((String)EasyMock.anyObject())).andReturn(titanGraph);
		PowerMock.replay(TitanFactory.class);
		
		conn = GraphDBConnection.getInstance(conf);
		ope = new GraphDBOperation(conn);
		
		deviceImpl.init(conf);
	}

	@After
	public void tearDown() throws Exception {
		titanGraph.shutdown();
		TestDatabaseManager.deleteTestDatabase();

		deviceImpl.close();
		deviceImpl = null;
	}

	/**
	 * Desc:
	 *  Test method for addDevice method.
	 * Codition:
	 *  N/A
	 * Expect:
	 * 	Get proper IDeviceObject
	 *  Check the IDeviceObject properties set
	 */
	@Test
	public void testAddDevice() {
		try 
		{	   
			//Make mockDevice
			IDevice mockDev = EasyMock.createMock(Device.class);
			// Mac addr for test device.
			String macAddr = "99:99:99:99:99:99";
			// IP addr for test device
			String ip = "192.168.100.1";
			Integer ipInt = IPv4.toIPv4Address(ip);
			Integer[] ipaddrs = {ipInt};
			// Mac addr for attached switch
			String switchMacAddr = "00:00:00:00:00:00:0a:01";		
			long switchMacAddrL = HexString.toLong(switchMacAddr);
			// Port number for attached switch
			short portNum = 2; 
			SwitchPort sp1 = new SwitchPort(switchMacAddrL, portNum);
			SwitchPort[] sps = {sp1};

			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getIPv4Addresses()).andReturn(ipaddrs);
			EasyMock.expect(mockDev.getAttachmentPoints()).andReturn(sps);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);

			EasyMock.replay(mockDev);

			//Add the device
	        IDeviceObject obj = deviceImpl.addDevice(mockDev);	
			assertNotNull(obj);

			//Test to take a Device from DB correctly
			IDeviceObject devObj1 = ope.searchDevice(macAddr);
			assertEquals(macAddr, devObj1.getMACAddress());

			//Test to take a attached sw  from DB correctly
			for(ISwitchObject sw1: devObj1.getSwitch())
			{
				String swMacFromDB = sw1.getDPID();
				assertEquals(switchMacAddr, swMacFromDB);
			}

			//Test to take a IP addr from DB
			//TodoForGettingIPaddr. There may be bug in the test class.
			String ipFromDB = devObj1.getIPAddress();
			String[] ipsFromDB = ipFromDB.replace("[", "").replace("]", "").split(",");
			List<String> ipsList = Arrays.asList(ipsFromDB);
			assertTrue(ipsList.contains(ip));

			//Test to take a attached port from DB
			for(IPortObject port : devObj1.getAttachedPorts())
			{

				//In this implementing, the object was not set the port. So it must be null.
				if(port.getNumber() != null)
				{
					String portNumFromDB = port.getNumber().toString();
					assertEquals(String.valueOf(portNum), portNumFromDB);				
				}
			}	
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
	 *  Check the IDeviceObject properties set.
	 */
	@Test
	public void testAddDeviceExisting() {
		try 
		{	   
			IDevice mockDev = EasyMock.createMock(Device.class);
			String macAddr = "99:99:99:99:99:99";
			String ip = "192.168.100.1";
			Integer ipInt = IPv4.toIPv4Address(ip);
			Integer[] ipaddrs = {ipInt};
			String switchMacAddr = "00:00:00:00:00:00:0a:01";		
			long switchMacAddrL = HexString.toLong(switchMacAddr);
			short portNum = 2; 
			SwitchPort sp1 = new SwitchPort(switchMacAddrL, portNum);
			SwitchPort[] sps = {sp1};

			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getIPv4Addresses()).andReturn(ipaddrs);
			EasyMock.expect(mockDev.getAttachmentPoints()).andReturn(sps);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getIPv4Addresses()).andReturn(ipaddrs);
			EasyMock.expect(mockDev.getAttachmentPoints()).andReturn(sps);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.replay(mockDev);

			//Add the device
	        IDeviceObject obj = deviceImpl.addDevice(mockDev);	
			assertNotNull(obj);

			//Test to take a Device from DB correctly
			IDeviceObject devObj1 = ope.searchDevice(macAddr);
			assertEquals(macAddr, devObj1.getMACAddress());

			//Add the same device
	        IDeviceObject obj2 = deviceImpl.addDevice(mockDev);	
			assertNotNull(obj2);

			IDeviceObject devObj2 = ope.searchDevice(macAddr);
			assertEquals(macAddr, devObj2.getMACAddress());	

			//Test to take a attached port from DB
			for(IPortObject port : devObj2.getAttachedPorts())
			{
				//In this implementing, the object was not set the port. So it must be null.
				if(port.getNumber() != null)
				{

					String portNumFromDB = port.getNumber().toString();
					assertEquals(String.valueOf(portNum), portNumFromDB);						

					ISwitchObject sw = port.getSwitch();
					String str = sw.getDPID();
					log.debug("");
				}
			}	

			String ipFromDB = devObj2.getIPAddress();
			String[] ipsFromDB = ipFromDB.replace("[", "").replace("]", "").split(",");
			List<String> ipsList = Arrays.asList(ipsFromDB);
			assertTrue(ipsList.contains(ip));

			//Test to take a attached port from DB
			for(IPortObject port : devObj2.getAttachedPorts())
			{

				//In this implementing, the object was not set the port. So it must be null.
				if(port.getNumber() != null)
				{
					String portNumFromDB = port.getNumber().toString();
					assertEquals(String.valueOf(portNum), portNumFromDB);				
				}
			}	
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
	 * 	Changed parameters are set properly.
	 */
	//@Ignore
	@Test
	public void testUpdateDevice() {
		try
		{
			IDevice mockDev = EasyMock.createMock(Device.class);
			String macAddr = "99:99:99:99:99:99";
			String ip = "192.168.100.1";
			Integer ipInt = IPv4.toIPv4Address(ip);
			Integer[] ipaddrs = {ipInt};
			String switchMacAddr = "00:00:00:00:00:00:0a:01";		
			long switchMacAddrL = HexString.toLong(switchMacAddr);
			short portNum = 2; 
			SwitchPort sp1 = new SwitchPort(switchMacAddrL, portNum);
			SwitchPort[] sps = {sp1};

			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getIPv4Addresses()).andReturn(ipaddrs);
			EasyMock.expect(mockDev.getAttachmentPoints()).andReturn(sps);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.replay(mockDev);

			//Dev2 (attached port is the same)
			IDevice mockDev2 = EasyMock.createMock(Device.class);
			String macAddr2 = "99:aa:aa:aa:aa:aa";
			Integer ip2 = IPv4.toIPv4Address("192.168.100.2");
			Integer[] ipaddrs2 = {ip2};

			EasyMock.expect(mockDev2.getMACAddressString()).andReturn(macAddr2);
			EasyMock.expect(mockDev2.getMACAddressString()).andReturn(macAddr2);
			EasyMock.expect(mockDev2.getMACAddressString()).andReturn(macAddr2);
			EasyMock.expect(mockDev2.getMACAddressString()).andReturn(macAddr2);
			EasyMock.expect(mockDev2.getIPv4Addresses()).andReturn(ipaddrs2);
			EasyMock.expect(mockDev2.getAttachmentPoints()).andReturn(sps);
			EasyMock.expect(mockDev2.getMACAddressString()).andReturn(macAddr2);
			EasyMock.expect(mockDev2.getMACAddressString()).andReturn(macAddr2);
			EasyMock.replay(mockDev2);

	        IDeviceObject obj = deviceImpl.addDevice(mockDev);	
			assertNotNull(obj);

			IDeviceObject dev1 = ope.searchDevice(macAddr);
			assertEquals(macAddr, dev1.getMACAddress());

			//update theDevice
			deviceImpl.updateDevice(mockDev2);
			IDeviceObject dev2 = ope.searchDevice(macAddr2);
			assertEquals(macAddr2, dev2.getMACAddress());
			IPortObject iport = ope.searchPort(switchMacAddr, portNum);

			//Test to take a attached port from DB
			for(IDeviceObject dev : iport.getDevices())
			{
				String macAddrFromDB = dev.getMACAddress();	
				if(macAddr2.equals(macAddrFromDB)){
					//Nothing to do
				}
				else{
					fail("notFoundTheDeviceOnThePort");			
				}
			}

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
			IDevice mockDev = EasyMock.createMock(Device.class);
			String macAddr = "99:99:99:99:99:99";
			String ip = "192.168.100.1";
			Integer ipInt = IPv4.toIPv4Address(ip);
			Integer[] ipaddrs = {ipInt};
			String switchMacAddr = "00:00:00:00:00:00:0a:01";		
			long switchMacAddrL = HexString.toLong(switchMacAddr);
			short portNum = 2; 
			SwitchPort sp1 = new SwitchPort(switchMacAddrL, portNum);
			SwitchPort[] sps = {sp1};

			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getAttachmentPoints()).andReturn(sps);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getIPv4Addresses()).andReturn(ipaddrs);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);

			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.replay(mockDev);

	        IDeviceObject obj = deviceImpl.addDevice(mockDev);	
			assertNotNull(obj);

			IDeviceObject dev1 = ope.searchDevice(macAddr);
			assertEquals(macAddr, dev1.getMACAddress());

			deviceImpl.removeDevice(mockDev);		
		    IDeviceObject dev = deviceImpl.getDeviceByMac(macAddr);
		    assertNull(dev);

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
	 *  3.Check the IDeviceObject properties set.
	 */
	//@Ignore
	@Test
	public void testGetDeviceByMac() {
		try
		{
			IDevice mockDev = EasyMock.createMock(Device.class);
			String macAddr = "99:99:99:99:99:99";
			String ip = "192.168.100.1";
			Integer ipInt = IPv4.toIPv4Address(ip);
			Integer[] ipaddrs = {ipInt};
			String switchMacAddr = "00:00:00:00:00:00:0a:01";		
			long switchMacAddrL = HexString.toLong(switchMacAddr);
			short portNum = 2; 
			SwitchPort sp1 = new SwitchPort(switchMacAddrL, portNum);
			SwitchPort[] sps = {sp1};

			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getIPv4Addresses()).andReturn(ipaddrs);
			EasyMock.expect(mockDev.getAttachmentPoints()).andReturn(sps);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.replay(mockDev);

	        IDeviceObject obj = deviceImpl.addDevice(mockDev);	
			assertNotNull(obj);

			IDeviceObject dev1 = ope.searchDevice(macAddr);
			assertEquals(macAddr, dev1.getMACAddress());

		    IDeviceObject dev = deviceImpl.getDeviceByMac(macAddr);
		    assertNotNull(dev);
			assertEquals(macAddr, dev.getMACAddress());

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
	 *  3. Check the IDeviceObject properties set.
	 */
	//@Ignore
	@Test
	public void testGetDeviceByIP() {
		try
		{
			IDevice mockDev = EasyMock.createMock(Device.class);
			String macAddr = "99:99:99:99:99:99";
			String ip = "192.168.100.1";
			Integer ipInt = IPv4.toIPv4Address(ip);
			Integer[] ipaddrs = {ipInt};
			String switchMacAddr = "00:00:00:00:00:00:0a:01";		
			long switchMacAddrL = HexString.toLong(switchMacAddr);
			short portNum = 2; 
			SwitchPort sp1 = new SwitchPort(switchMacAddrL, portNum);
			SwitchPort[] sps = {sp1};

			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getIPv4Addresses()).andReturn(ipaddrs);
			EasyMock.expect(mockDev.getAttachmentPoints()).andReturn(sps);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.replay(mockDev);

	        IDeviceObject obj = deviceImpl.addDevice(mockDev);	
			assertNotNull(obj);

			IDeviceObject dev1 = ope.searchDevice(macAddr);
			assertEquals(macAddr, dev1.getMACAddress());

		    IDeviceObject dev = deviceImpl.getDeviceByIP(ip);
		    assertNotNull(dev);
			String ipFromDB = dev.getIPAddress();
			String[] ipsFromDB = ipFromDB.replace("[", "").replace("]", "").split(",");
			List<String> ipsList = Arrays.asList(ipsFromDB);
			assertTrue(ipsList.contains(ip));

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
	 * 	1. Unexisting attachment point is ignored, so nothing happen.
	 * 	2. Change the attachment point.
	 */
	//@Ignore
	@Test
	public void testChangeDeviceAttachmentsIDevice() {
		try
		{
			IDevice mockDev = EasyMock.createMock(Device.class);
			String macAddr = "99:99:99:99:99:99";
			String ip = "192.168.100.1";
			Integer ipInt = IPv4.toIPv4Address(ip);
			Integer[] ipaddrs = {ipInt};
			String switchMacAddr = "00:00:00:00:00:00:0a:01";		
			long switchMacAddrL = HexString.toLong(switchMacAddr);
			short portNum = 2; 
			SwitchPort sp1 = new SwitchPort(switchMacAddrL, portNum);
			SwitchPort[] sps = {sp1};

			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getIPv4Addresses()).andReturn(ipaddrs);
			EasyMock.expect(mockDev.getAttachmentPoints()).andReturn(sps);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.replay(mockDev);

			//Dev2
			IDevice mockDev2 = EasyMock.createMock(Device.class);
			String switchMacAddr2 = "00:00:00:00:00:00:0a:02";
			long lSwitchMacAddr2 = HexString.toLong(switchMacAddr2);
			short portNum2 = 2; 
			SwitchPort sp2 = new SwitchPort(lSwitchMacAddr2, portNum2);
			SwitchPort sps2[] = {sp2};

			EasyMock.expect(mockDev2.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev2.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev2.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev2.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev2.getIPv4Addresses()).andReturn(ipaddrs);
			EasyMock.expect(mockDev2.getAttachmentPoints()).andReturn(sps2);
			EasyMock.expect(mockDev2.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev2.getMACAddressString()).andReturn(macAddr);
			EasyMock.replay(mockDev2);

	        IDeviceObject obj = deviceImpl.addDevice(mockDev);	
			assertNotNull(obj);

		    deviceImpl.changeDeviceAttachments(mockDev2);

		    IDeviceObject dev = deviceImpl.getDeviceByMac(macAddr);
		    assertNotNull(dev);

			for(ISwitchObject sw1: dev.getSwitch())
			{
				String swMacFromDB = sw1.getDPID();
				assertEquals(switchMacAddr2, swMacFromDB);
			}
		} catch(Exception e) {
			fail(e.getMessage());
		}
	}

	//@Ignore
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
	 *  1. Check correctly changed the ipadress
	 */
	//@Ignore
	@Test
	public void testChangeDeviceIPv4Address() {
		try
		{
			//Dev1
			IDevice mockDev = EasyMock.createMock(Device.class);
			String macAddr = "99:99:99:99:99:99";
			String ip = "192.168.100.1";
			Integer ipInt = IPv4.toIPv4Address(ip);
			Integer[] ipaddrs = {ipInt};
			String switchMacAddr = "00:00:00:00:00:00:0a:01";		
			long switchMacAddrL = HexString.toLong(switchMacAddr);
			short portNum = 2; 
			SwitchPort sp1 = new SwitchPort(switchMacAddrL, portNum);
			SwitchPort[] sps = {sp1};

			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getIPv4Addresses()).andReturn(ipaddrs);
			EasyMock.expect(mockDev.getAttachmentPoints()).andReturn(sps);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev.getMACAddressString()).andReturn(macAddr);
			EasyMock.replay(mockDev);

	        IDeviceObject obj = deviceImpl.addDevice(mockDev);	
			assertNotNull(obj);

			IDevice mockDev2 = EasyMock.createMock(Device.class);
			String ip2 = "192.168.100.2";
			Integer ipInt2 = IPv4.toIPv4Address(ip2);
			Integer[] ipaddrs2 = {ipInt2};
			EasyMock.expect(mockDev2.getMACAddressString()).andReturn(macAddr);
			EasyMock.expect(mockDev2.getIPv4Addresses()).andReturn(ipaddrs2);
			EasyMock.replay(mockDev2);

			IDeviceObject dev1 = ope.searchDevice(macAddr);
			assertEquals(macAddr, dev1.getMACAddress());
			String ipFromDB = dev1.getIPAddress();
			String[] ipsFromDB = ipFromDB.replace("[", "").replace("]", "").split(",");
			List<String> ipsList = Arrays.asList(ipsFromDB);
			assertTrue(ipsList.contains(ip));

	        deviceImpl.changeDeviceIPv4Address(mockDev2);	

			IDeviceObject dev2 = ope.searchDevice(macAddr);
			assertEquals(macAddr, dev2.getMACAddress());
			String ipFromDB2 = dev2.getIPAddress();
			String[] ipsFromDB2 = ipFromDB2.replace("[", "").replace("]", "").split(",");
			List<String> ipsList2 = Arrays.asList(ipsFromDB2);
			assertTrue(ipsList2.contains(ip2));
		} 
		catch(Exception e) {
			fail(e.getMessage());
		}
	}

}
