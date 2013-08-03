package net.onrc.onos.ofcontroller.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import net.floodlightcontroller.util.MACAddress;

import org.junit.Before;
import org.junit.Test;

public class FlowEntryTest {

	FlowEntry entry;
	
	FlowId flowId = new FlowId(0x1234);
	FlowEntryId flowEntryId = new FlowEntryId(0x5678);
	FlowEntryMatch match;
	FlowEntryActions actions;
	
	Dpid dpid = new Dpid(0xCAFE);
	
	Port inport = new Port((short)1);
	byte[] byte1 = { 1, 2, 3, 4, 5, 6 };
	byte[] byte2 = { 6, 5, 4, 3, 2, 1 };
	MACAddress mac1 = new MACAddress(byte1);
	MACAddress mac2 = new MACAddress(byte2);
	Short ether = Short.valueOf((short)2);
	Short vlanid = Short.valueOf((short)3);
	Byte vlanprio = Byte.valueOf((byte)4);
	IPv4Net ip1 = new IPv4Net("127.0.0.1/32");
	IPv4Net ip2 = new IPv4Net( new IPv4("127.0.0.2"), (short)32);
	IPv4 ipaddr1 = new IPv4("127.0.0.3");
	IPv4 ipaddr2 = new IPv4("127.0.0.4");
	Byte ipproto = Byte.valueOf((byte)5);
	Byte ipToS = Byte.valueOf((byte)6);
	Short tport1 = Short.valueOf((short)7);
	Short tport2 = Short.valueOf((short)8);
	Port outport = new Port((short)9);
	Port queueport = new Port((short)10);
	int queueId = 11;
	
	FlowEntryErrorState errorState = new FlowEntryErrorState( (short)12, (short)13);

	
	@Before
	public void setUp() throws Exception{
		entry = new FlowEntry();

		flowId = new FlowId("0x1234");
		entry.setFlowId( flowId );

		flowEntryId = new FlowEntryId("0x5678");
		entry.setFlowEntryId(flowEntryId);
		
		dpid = new Dpid("CA:FE");
		entry.setDpid( dpid );
		
		entry.setInPort( inport );
		entry.setOutPort( outport );

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
		
		entry.setFlowEntryMatch( match );
		
		FlowEntryAction action = null;
		actions = entry.flowEntryActions();
		
		action = new FlowEntryAction();
		action.setActionOutput(outport);
		actions.addAction(action);

		action = new FlowEntryAction();
		action.setActionOutputToController((short)0);
		actions.addAction(action);

		action = new FlowEntryAction();
		action.setActionSetVlanId(vlanid);
		actions.addAction(action);

		action = new FlowEntryAction();
		action.setActionSetVlanPriority(vlanprio);
		actions.addAction(action);

		action = new FlowEntryAction();
		action.setActionStripVlan(true);
		actions.addAction(action);

		action = new FlowEntryAction();
		action.setActionSetEthernetSrcAddr(mac1);
		actions.addAction(action);

		action = new FlowEntryAction();
		action.setActionSetEthernetDstAddr(mac2);
		actions.addAction(action);

		action = new FlowEntryAction();
		action.setActionSetIPv4SrcAddr(ipaddr1);
		actions.addAction(action);

		action = new FlowEntryAction();
		action.setActionSetIPv4DstAddr(ipaddr2);
		actions.addAction(action);

		action = new FlowEntryAction();
		action.setActionSetIpToS(ipToS);
		actions.addAction(action);

		action = new FlowEntryAction();
		action.setActionSetTcpUdpSrcPort(tport1);
		actions.addAction(action);

		action = new FlowEntryAction();
		action.setActionSetTcpUdpDstPort(tport2);
		actions.addAction(action);

		action = new FlowEntryAction();
		action.setActionEnqueue(queueport, queueId);
		actions.addAction(action);
		
		entry.setFlowEntryUserState( FlowEntryUserState.FE_USER_ADD );
		entry.setFlowEntrySwitchState( FlowEntrySwitchState.FE_SWITCH_UPDATED );
		entry.setFlowEntryErrorState( errorState );

	}

	@Test
	public void testFlowEntry(){
		FlowEntry e = new FlowEntry();
		
		assertTrue( e.flowEntryActions().isEmpty() );
		assertEquals("flowEntryUserState", FlowEntryUserState.FE_USER_UNKNOWN, e.flowEntryUserState() );
		assertEquals("flowEntrySwitchState", FlowEntrySwitchState.FE_SWITCH_UNKNOWN, e.flowEntrySwitchState() );
	}

	@Test
	public void testGetFlowId(){
		assertEquals("flowId", flowId, entry.getFlowId() );
	}

	@Test
	public void testFlowEntryId(){
		assertEquals("flowEntryId", flowEntryId, entry.flowEntryId() );
	}

	@Test
	public void testFlowEntryMatch(){
		assertEquals("flowEntryMatch", match, entry.flowEntryMatch() );
	}

	@Test
	public void testFlowEntryActions(){
		assertEquals("flowEntryActions", actions, entry.flowEntryActions() );
	}

	@Test
	public void testSetFlowEntryActions(){
		FlowEntryActions actions = new FlowEntryActions();
		entry.setFlowEntryActions( actions );
		assertEquals("flowEntryActions", actions, entry.flowEntryActions() );
	}

	@Test
	public void testDpid(){
		assertEquals("dpid", dpid, entry.dpid() );
	}

	@Test
	public void testInPort(){
		assertEquals("inPort", inport, entry.inPort() );
	}

	@Test
	public void testOutPort(){
		assertEquals("outPort", outport, entry.outPort() );
	}

	@Test
	public void testFlowEntryUserState(){
		assertEquals("flowEntryUserState", FlowEntryUserState.FE_USER_ADD, entry.flowEntryUserState() );
	}

	@Test
	public void testFlowEntrySwitchState(){
		assertEquals("flowEntrySwitchState", FlowEntrySwitchState.FE_SWITCH_UPDATED, entry.flowEntrySwitchState() );
	}

	@Test
	public void testFlowEntryErrorState(){
		assertEquals("flowEntryErrorState", errorState, entry.flowEntryErrorState() );
	}

	@Test
	public void testToString(){
		FlowEntry def = new FlowEntry();
		assertEquals( def.toString(), "[flowEntryId=null flowEntryMatch=null flowEntryActions=[] dpid=null inPort=null outPort=null flowEntryUserState=FE_USER_UNKNOWN flowEntrySwitchState=FE_SWITCH_UNKNOWN flowEntryErrorState=null]" );
		assertEquals( entry.toString(), "[flowEntryId=0x5678 flowEntryMatch=[inPort=1 srcMac=01:02:03:04:05:06 dstMac=06:05:04:03:02:01 ethernetFrameType=2 vlanId=3 vlanPriority=4 srcIPv4Net=127.0.0.1/32 dstIPv4Net=127.0.0.2/32 ipProto=5 ipToS=6 srcTcpUdpPort=7 dstTcpUdpPort=8] flowEntryActions=[[type=ACTION_OUTPUT action=[port=9 maxLen=0]];[type=ACTION_OUTPUT action=[port=-3 maxLen=0]];[type=ACTION_SET_VLAN_VID action=[vlanId=3]];[type=ACTION_SET_VLAN_PCP action=[vlanPriority=4]];[type=ACTION_STRIP_VLAN action=[stripVlan=true]];[type=ACTION_SET_DL_SRC action=[addr=01:02:03:04:05:06]];[type=ACTION_SET_DL_DST action=[addr=06:05:04:03:02:01]];[type=ACTION_SET_NW_SRC action=[addr=127.0.0.3]];[type=ACTION_SET_NW_DST action=[addr=127.0.0.4]];[type=ACTION_SET_NW_TOS action=[ipToS=6]];[type=ACTION_SET_TP_SRC action=[port=7]];[type=ACTION_SET_TP_DST action=[port=8]];[type=ACTION_ENQUEUE action=[port=10 queueId=11]];] dpid=00:00:00:00:00:00:ca:fe inPort=1 outPort=9 flowEntryUserState=FE_USER_ADD flowEntrySwitchState=FE_SWITCH_UPDATED flowEntryErrorState=[type=12 code=13]]" );
	}

}
