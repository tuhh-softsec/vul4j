package net.onrc.onos.ofcontroller.util;

import static org.junit.Assert.*;
import net.onrc.onos.ofcontroller.core.internal.TestableGraphDBOperation.TestFlowEntry;
import net.onrc.onos.ofcontroller.core.internal.TestableGraphDBOperation.TestFlowPath;

import org.junit.Before;
import org.junit.Test;

public class FlowPathTest {

	FlowPath flowPath;
	
	@Before
	public void setUp() throws Exception{
		TestFlowPath iFlowPath = new TestFlowPath();
		iFlowPath.setFlowIdForTest("0x1234");
		iFlowPath.setInstallerIdForTest("installerId");
		iFlowPath.setFlowPathFlagsForTest(0L);
		iFlowPath.setSrcSwForTest("CA:FE");
		iFlowPath.setSrcPortForTest((short)1);
		iFlowPath.setDstSwForTest("BA:BE");
		iFlowPath.setDstPortForTest((short)2);
		
		iFlowPath.setActionsForTest("[[type=ACTION_OUTPUT action=[port=10 maxLen=11]];[type=ACTION_OUTPUT action=[port=12 maxLen=13]];]");
		
		TestFlowEntry iFlowEntry = new TestFlowEntry();
		iFlowEntry.setEntryIdForTest("0x14");
		iFlowEntry.setDpidForTest("BE:EF");
		iFlowEntry.setActionsForTest("[[type=ACTION_OUTPUT action=[port=23 maxLen=24]];[type=ACTION_OUTPUT action=[port=25 maxLen=26]];]");
		iFlowEntry.setUserStateForTest("FE_USER_MODIFY");
		iFlowEntry.setSwitchStateForTest("FE_SWITCH_UPDATE_IN_PROGRESS");
		iFlowPath.addFlowEntryForTest(iFlowEntry);
		
		flowPath = new FlowPath(iFlowPath);
	}

	@Test
	public void testFlowPath(){
		FlowPath flowPath = new FlowPath();
		assertFalse( flowPath.flowPathFlags().isDiscardFirstHopEntry() );
		assertFalse( flowPath.flowPathFlags().isKeepOnlyFirstHopEntry() );
		assertTrue( flowPath.flowEntryActions().isEmpty() );
	}

	@Test
	public void testFlowPathIFlowPath(){
		TestFlowPath iFlowPath = new TestFlowPath();
		iFlowPath.setFlowIdForTest("0x1234");
		iFlowPath.setInstallerIdForTest("installerId");
		iFlowPath.setFlowPathFlagsForTest(0L);
		iFlowPath.setSrcSwForTest("CA:FE");
		iFlowPath.setSrcPortForTest((short)1);
		iFlowPath.setDstSwForTest("BA:BE");
		iFlowPath.setDstPortForTest((short)2);
		
		iFlowPath.setMatchSrcMacForTest("01:02:03:04:05:06");
		iFlowPath.setMatchDstMacForTest("06:05:04:03:02:01");
		iFlowPath.setMatchEthernetFrameTypeForTest((short)3);
		iFlowPath.setMatchVlanIdForTest((short)4);
		iFlowPath.setMatchVlanPriorityForTest((byte)5);
		iFlowPath.setMatchSrcIpaddrForTest("127.0.0.1/32");
		iFlowPath.setMatchDstIpaddrForTest("127.0.0.2/32");
		iFlowPath.setMatchIpProtoForTest((byte)6);
		iFlowPath.setMatchIpToSForTest((byte)7);
		iFlowPath.setMatchSrcTcpUdpPortForTest((short)8);
		iFlowPath.setMatchDstTcpUdpPortForTest((short)9);
		
		iFlowPath.setActionsForTest("[[type=ACTION_OUTPUT action=[port=10 maxLen=11]];[type=ACTION_OUTPUT action=[port=12 maxLen=13]];]");
		
		TestFlowEntry iFlowEntry = new TestFlowEntry();
		iFlowEntry.setEntryIdForTest("0x14");
		iFlowEntry.setDpidForTest("BE:EF");
		iFlowEntry.setMatchInPortForTest((short)15);
		iFlowEntry.setMatchSrcMacForTest("11:22:33:44:55:66");
		iFlowEntry.setMatchDstMacForTest("66:55:44:33:22:11");
		iFlowEntry.setMatchEtherFrameTypeForTest((short)16);
		iFlowEntry.setMatchVlanIdForTest((short)17);
		iFlowEntry.setMatchVlanPriorityForTest((byte)18);
		iFlowEntry.setMatchSrcIpaddrForTest("127.0.0.3/32");
		iFlowEntry.setMatchDstIpaddrForTest("127.0.0.4/32");
		iFlowEntry.setMatchIpProtoForTest((byte)19);
		iFlowEntry.setMatchIpToSForTest((byte)20);
		iFlowEntry.setMatchSrcTcpUdpPortForTest((short)21);
		iFlowEntry.setMatchDstTcpUdpPortForTest((short)22);
		iFlowEntry.setActionsForTest("[[type=ACTION_OUTPUT action=[port=23 maxLen=24]];[type=ACTION_OUTPUT action=[port=25 maxLen=26]];]");
		iFlowEntry.setUserStateForTest("FE_USER_MODIFY");
		iFlowEntry.setSwitchStateForTest("FE_SWITCH_UPDATE_IN_PROGRESS");
		iFlowPath.addFlowEntryForTest(iFlowEntry);
		
		FlowPath flowPath = new FlowPath(iFlowPath);
		assertEquals(flowPath.flowId().value(), 0x1234);
		assertEquals(flowPath.installerId().value(), "installerId");
		assertEquals(flowPath.flowPathFlags().flags(), 0);
		assertEquals(flowPath.dataPath().srcPort().dpid().value(), 0xCAFE);
		assertEquals(flowPath.dataPath().srcPort().port().value(), 1);
		assertEquals(flowPath.dataPath().dstPort().dpid().value(), 0xBABE);
		assertEquals(flowPath.dataPath().dstPort().port().value(), 2);
		
		assertEquals(flowPath.flowEntryMatch().srcMac().toString(), "01:02:03:04:05:06");
		assertEquals(flowPath.flowEntryMatch().dstMac().toString(), "06:05:04:03:02:01");
		assertEquals(flowPath.flowEntryMatch().ethernetFrameType().shortValue(), 3);
		assertEquals(flowPath.flowEntryMatch().vlanId().shortValue(), 4);
		assertEquals(flowPath.flowEntryMatch().vlanPriority().shortValue(), 5);
		assertEquals(flowPath.flowEntryMatch().srcIPv4Net().address().toString(), "127.0.0.1");
		assertEquals(flowPath.flowEntryMatch().srcIPv4Net().prefixLen() , 32);
		assertEquals(flowPath.flowEntryMatch().dstIPv4Net().address().toString(), "127.0.0.2");
		assertEquals(flowPath.flowEntryMatch().dstIPv4Net().prefixLen() , 32);
		assertEquals(flowPath.flowEntryMatch().ipProto().byteValue(), 6);
		assertEquals(flowPath.flowEntryMatch().ipToS().byteValue(), 7);
		assertEquals(flowPath.flowEntryMatch().srcTcpUdpPort().shortValue(), 8);
		assertEquals(flowPath.flowEntryMatch().dstTcpUdpPort().shortValue(), 9);
		
		assertEquals(flowPath.flowEntryActions().toString(),"[[type=ACTION_OUTPUT action=[port=10 maxLen=11]];[type=ACTION_OUTPUT action=[port=12 maxLen=13]];]");
		
		assertEquals(0x14, flowPath.dataPath().flowEntries().get(0).flowEntryId().value() );
		assertEquals(0xBEEF, flowPath.dataPath().flowEntries().get(0).dpid().value() );
		assertEquals(15, flowPath.dataPath().flowEntries().get(0).flowEntryMatch().inPort().value() );
		assertEquals("11:22:33:44:55:66", flowPath.dataPath().flowEntries().get(0).flowEntryMatch().srcMac().toString());
		assertEquals("66:55:44:33:22:11", flowPath.dataPath().flowEntries().get(0).flowEntryMatch().dstMac().toString());
		assertEquals(16, flowPath.dataPath().flowEntries().get(0).flowEntryMatch().ethernetFrameType().shortValue());
		assertEquals(17, flowPath.dataPath().flowEntries().get(0).flowEntryMatch().vlanId().shortValue());
		assertEquals(18, flowPath.dataPath().flowEntries().get(0).flowEntryMatch().vlanPriority().byteValue());
		assertEquals("127.0.0.3", flowPath.dataPath().flowEntries().get(0).flowEntryMatch().srcIPv4Net().address().toString());
		assertEquals(32, flowPath.dataPath().flowEntries().get(0).flowEntryMatch().srcIPv4Net().prefixLen());
		assertEquals("127.0.0.4", flowPath.dataPath().flowEntries().get(0).flowEntryMatch().dstIPv4Net().address().toString());
		assertEquals(32, flowPath.dataPath().flowEntries().get(0).flowEntryMatch().dstIPv4Net().prefixLen());
		assertEquals(19, flowPath.dataPath().flowEntries().get(0).flowEntryMatch().ipProto().byteValue());
		assertEquals(20, flowPath.dataPath().flowEntries().get(0).flowEntryMatch().ipToS().byteValue());
		assertEquals(21, flowPath.dataPath().flowEntries().get(0).flowEntryMatch().srcTcpUdpPort().shortValue());
		assertEquals(22, flowPath.dataPath().flowEntries().get(0).flowEntryMatch().dstTcpUdpPort().shortValue());
		assertEquals("[[type=ACTION_OUTPUT action=[port=23 maxLen=24]];[type=ACTION_OUTPUT action=[port=25 maxLen=26]];]", flowPath.dataPath().flowEntries().get(0).flowEntryActions().toString());
		assertEquals("FE_USER_MODIFY", flowPath.dataPath().flowEntries().get(0).flowEntryUserState().toString());
		assertEquals("FE_SWITCH_UPDATE_IN_PROGRESS", flowPath.dataPath().flowEntries().get(0).flowEntrySwitchState().toString());
	}


	@Test
	public void testFlowPathFlags(){
		FlowPath flowPath = new FlowPath();
		FlowPathFlags flags = new FlowPathFlags();
		flags.setFlags(0);
		flowPath.setFlowPathFlags( flags );
		assertFalse( flowPath.flowPathFlags().isDiscardFirstHopEntry() );
		assertFalse( flowPath.flowPathFlags().isKeepOnlyFirstHopEntry() );
	}

	@Test
	public void testSetFlowPathFlags(){
		FlowPath flowPath = new FlowPath();
		FlowPathFlags flags = new FlowPathFlags("DISCARD_FIRST_HOP_ENTRY");
		flags.setFlagsStr("KEEP_ONLY_FIRST_HOP_ENTRY");
		flowPath.setFlowPathFlags( flags );
		assertFalse( flowPath.flowPathFlags().isDiscardFirstHopEntry() );
		assertTrue( flowPath.flowPathFlags().isKeepOnlyFirstHopEntry() );
	}

	@Test
	public void testSetDataPath(){
		FlowPath flowPath = new FlowPath();
		DataPath dataPath = new DataPath();
		flowPath.setDataPath( dataPath );
		assertEquals(flowPath.dataPath(), dataPath );
	}

	@Test
	public void testToString(){

		assertEquals("[flowId=0x1234 installerId=installerId flowPathFlags=[flags=] dataPath=[src=00:00:00:00:00:00:ca:fe/1 flowEntry=[flowEntryId=0x14 flowEntryMatch=[] flowEntryActions=[[type=ACTION_OUTPUT action=[port=23 maxLen=24]];[type=ACTION_OUTPUT action=[port=25 maxLen=26]];] dpid=00:00:00:00:00:00:be:ef flowEntryUserState=FE_USER_MODIFY flowEntrySwitchState=FE_SWITCH_UPDATE_IN_PROGRESS] dst=00:00:00:00:00:00:ba:be/2] flowEntryMatch=[] flowEntryActions=[[type=ACTION_OUTPUT action=[port=10 maxLen=11]];[type=ACTION_OUTPUT action=[port=12 maxLen=13]];]]", flowPath.toString());
	}

	@Test
	public void testCompareTo(){
		FlowPath flowPath1 = new FlowPath();
		flowPath1.setFlowId( new FlowId(1));
		FlowPath flowPath2 = new FlowPath();
		flowPath2.setFlowId( new FlowId(2));
		
		assertTrue( flowPath1.compareTo(flowPath2) < 0);
	}

}
