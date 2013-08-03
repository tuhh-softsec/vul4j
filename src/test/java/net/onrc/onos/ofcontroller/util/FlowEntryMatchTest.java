package net.onrc.onos.ofcontroller.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.floodlightcontroller.util.MACAddress;

import org.junit.Before;
import org.junit.Test;

public class FlowEntryMatchTest {

	FlowEntryMatch match;
	
	Port inport = new Port((short)1);
	byte[] byte1 = { 1, 2, 3, 4, 5, 6 };
	byte[] byte2 = { 6, 5, 4, 3, 2, 1 };
	MACAddress mac1 = new MACAddress(byte1);
	MACAddress mac2 = new MACAddress(byte2);
	Short ether = Short.valueOf((short)2);
	Short vlanid = Short.valueOf((short)3);
	Byte vlanprio = Byte.valueOf((byte)4);
	IPv4Net ip1 = new IPv4Net("127.0.0.1/32");
	IPv4Net ip2 = new IPv4Net("127.0.0.2/32");
	Byte ipproto = Byte.valueOf((byte)5);
	Byte ipToS = Byte.valueOf((byte)6);
	Short tport1 = Short.valueOf((short)7);
	Short tport2 = Short.valueOf((short)8);
	
	@Before
	public void setUp() throws Exception{
		match = new FlowEntryMatch();
		match.enableInPort( inport);
		match.enableSrcMac( mac1 );
		match.enableDstMac( mac2 );
		match.enableEthernetFrameType( ether );
		match.enableVlanId( vlanid );
		match.enableVlanPriority( vlanprio );
		match.enableSrcIPv4Net( ip1 );
		match.enableDstIPv4Net( ip2 );
		match.enableIpProto( ipproto );
		match.enableIpToS( ipToS );
		match.enableSrcTcpUdpPort( tport1 );
		match.enableDstTcpUdpPort( tport2 );
	}

	@Test
	public void testFlowEntryMatch(){
		FlowEntryMatch def = new FlowEntryMatch();
		
		assertEquals("default null", null, def.inPort() );
		assertEquals("default null", null, def.srcMac() );
		assertEquals("default null", null, def.dstMac() );
		assertEquals("default null", null, def.ethernetFrameType() );
		assertEquals("default null", null, def.vlanId() );
		assertEquals("default null", null, def.vlanPriority() );
		assertEquals("default null", null, def.srcIPv4Net() );
		assertEquals("default null", null, def.dstIPv4Net() );
		assertEquals("default null", null, def.ipToS() );
		assertEquals("default null", null, def.srcTcpUdpPort() );
		assertEquals("default null", null, def.dstTcpUdpPort() );
	}

	@Test
	public void testFlowEntryMatchFlowEntryMatch(){
		FlowEntryMatch def_base = new FlowEntryMatch();
		FlowEntryMatch def = new FlowEntryMatch(def_base);

		assertEquals("default null", null, def.inPort() );
		assertEquals("default null", null, def.srcMac() );
		assertEquals("default null", null, def.dstMac() );
		assertEquals("default null", null, def.ethernetFrameType() );
		assertEquals("default null", null, def.vlanId() );
		assertEquals("default null", null, def.vlanPriority() );
		assertEquals("default null", null, def.srcIPv4Net() );
		assertEquals("default null", null, def.dstIPv4Net() );
		assertEquals("default null", null, def.ipProto() );
		assertEquals("default null", null, def.ipToS() );
		assertEquals("default null", null, def.srcTcpUdpPort() );
		assertEquals("default null", null, def.dstTcpUdpPort() );
		
		FlowEntryMatch copy = new FlowEntryMatch( match );
		
		assertEquals("inport", inport, copy.inPort() );
		assertEquals("mac1", mac1, copy.srcMac() );
		assertEquals("mac2", mac2, copy.dstMac() );
		assertEquals("ether", ether, copy.ethernetFrameType() );
		assertEquals("vlan id", vlanid, copy.vlanId() );
		assertEquals("vlan prio", vlanprio, copy.vlanPriority() );
		assertEquals("ip1", ip1, copy.srcIPv4Net() );
		assertEquals("ip2", ip2, copy.dstIPv4Net() );
		assertEquals("ip proto", ipproto, copy.ipProto() );
		assertEquals("tos", ipToS, copy.ipToS() );
		assertEquals("src port", tport1, copy.srcTcpUdpPort() );
		assertEquals("dst port", tport2, copy.dstTcpUdpPort() );

	}

	@Test
	public void testInPort(){
		assertEquals("inport", inport, match.inPort() );
	}

	@Test
	public void testDisableInPort(){
		match.disableInPort();
		assertEquals("inport", null, match.inPort() );
		assertFalse( match.matchInPort() );
	}

	@Test
	public void testMatchInPort(){
		assertTrue( match.matchInPort() );
	}

	@Test
	public void testSrcMac(){
		assertEquals("mac1", mac1, match.srcMac() );
	}

	@Test
	public void testDisableSrcMac(){
		match.disableSrcMac();
		assertEquals("srcMac", null, match.srcMac() );
		assertFalse( match.matchSrcMac() );
	}

	@Test
	public void testMatchSrcMac(){
		assertTrue( match.matchSrcMac() );
	}

	@Test
	public void testDstMac(){
		assertEquals("mac2", mac2, match.dstMac() );
	}

	@Test
	public void testDisableDstMac(){
		match.disableDstMac();
		assertEquals("dstMac", null, match.dstMac() );
		assertFalse( match.matchDstMac() );
	}

	@Test
	public void testMatchDstMac(){
		assertTrue( match.matchDstMac() );
	}

	@Test
	public void testEthernetFrameType(){
		assertEquals("ether", ether, match.ethernetFrameType() );
	}

	@Test
	public void testDisableEthernetFrameType(){
		match.disableEthernetFrameType();
		assertEquals("ethernetFrameType", null, match.ethernetFrameType() );
		assertFalse( match.matchEthernetFrameType() );
	}

	@Test
	public void testMatchEthernetFrameType(){
		assertTrue( match.matchEthernetFrameType() );
	}

	@Test
	public void testVlanId(){
		assertEquals("vlan id", vlanid, match.vlanId() );
	}

	@Test
	public void testDisableVlanId(){
		match.disableVlanId();
		assertEquals("vlanId", null, match.vlanId() );
		assertFalse( match.matchVlanId() );
	}

	@Test
	public void testMatchVlanId(){
		assertTrue( match.matchVlanId() );
	}

	@Test
	public void testVlanPriority(){
		assertEquals("vlan prio", vlanprio, match.vlanPriority() );
	}

	@Test
	public void testDisableVlanPriority(){
		match.disableVlanPriority();
		assertEquals("vlanPriority", null, match.vlanPriority() );
		assertFalse( match.matchVlanPriority() );
	}

	@Test
	public void testMatchVlanPriority(){
		assertTrue( match.matchVlanPriority() );
	}

	@Test
	public void testSrcIPv4Net(){
		assertEquals("ip1", ip1, match.srcIPv4Net() );
	}

	@Test
	public void testDisableSrcIPv4Net(){
		match.disableSrcIPv4Net();
		assertEquals("srcIPv4Net", null, match.srcIPv4Net() );
		assertFalse( match.matchSrcIPv4Net() );
	}

	@Test
	public void testMatchSrcIPv4Net(){
		assertTrue( match.matchSrcIPv4Net() );
	}

	@Test
	public void testDstIPv4Net(){
		assertEquals("ip2", ip2, match.dstIPv4Net() );
	}

	@Test
	public void testDisableDstIPv4Net(){
		match.disableDstIPv4Net();
		assertEquals("dstIPv4Net", null, match.dstIPv4Net() );
		assertFalse( match.matchDstIPv4Net() );
	}

	@Test
	public void testMatchDstIPv4Net(){
		assertTrue( match.matchDstIPv4Net() );
	}

	@Test
	public void testIpProto(){
		assertEquals("ip proto", ipproto, match.ipProto() );
	}

	@Test
	public void testDisableIpProto(){
		match.disableIpProto();
		assertEquals("ipProto", null, match.ipProto() );
		assertFalse( match.matchIpProto() );
	}

	@Test
	public void testMatchIpProto(){
		assertTrue( match.matchIpProto() );
	}

	@Test
	public void testIpToS(){
		assertEquals("tos", ipToS, match.ipToS() );
	}

	@Test
	public void testDisableIpToS(){
		match.disableIpToS();
		assertEquals("ipToS", null, match.ipToS() );
		assertFalse( match.matchIpToS() );
	}

	@Test
	public void testMatchIpToS(){
		assertTrue( match.matchIpToS() );
	}

	@Test
	public void testSrcTcpUdpPort(){
		assertEquals("src port", tport1, match.srcTcpUdpPort() );
	}

	@Test
	public void testDisableSrcTcpUdpPort(){
		match.disableSrcTcpUdpPort();
		assertEquals("srcTcpUdpPort", null, match.srcTcpUdpPort() );
		assertFalse( match.matchSrcTcpUdpPort() );
	}

	@Test
	public void testMatchSrcTcpUdpPort(){
		assertTrue( match.matchSrcTcpUdpPort() );
	}

	@Test
	public void testDstTcpUdpPort(){
		assertEquals("dst port", tport2, match.dstTcpUdpPort() );
	}

	@Test
	public void testDisableDstTcpUdpPort(){
		match.disableDstTcpUdpPort();
		assertEquals("dstTcpUdpPort", null, match.dstTcpUdpPort() );
		assertFalse( match.matchDstTcpUdpPort() );
	}

	@Test
	public void testMatchDstTcpUdpPort(){
		assertTrue( match.matchDstTcpUdpPort() );
	}

	@Test
	public void testToString(){
		FlowEntryMatch def = new FlowEntryMatch();
		assertEquals("match default", def.toString(), "[]");
		
		assertEquals("match set", match.toString(), "[inPort=1 srcMac=01:02:03:04:05:06 dstMac=06:05:04:03:02:01 ethernetFrameType=2 vlanId=3 vlanPriority=4 srcIPv4Net=127.0.0.1/32 dstIPv4Net=127.0.0.2/32 ipProto=5 ipToS=6 srcTcpUdpPort=7 dstTcpUdpPort=8]");
	}

}
