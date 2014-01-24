package net.onrc.onos.ofcontroller.flowmanager;

import java.util.HashMap;
import java.util.Map;

import net.onrc.onos.graph.DBOperation;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowPath;

public class FlowPathProperty {
	private Map<String, Object> map = new HashMap<>();

	public void setType(String typeStr) {
		map.put("type", typeStr);
	}

	public void setFlowId(String flowId) {
		map.put("flow_id", flowId);
	}

	public void setInstallerId(String installerId) {
		map.put("installer_id", installerId);
	}

	public void setFlowPathType(String flowPathType) {
		map.put("flow_path_type", flowPathType);
	}

	public void setFlowPathUserState(String userState) {
		map.put("user_state", userState);
	}

	public void setFlowPathFlags(Long flowPathFlags) {
		map.put("flow_path_flags", flowPathFlags);
	}

	public void setIdleTimeout(Integer idleTimeout) {
		map.put("idle_timeout", idleTimeout);
	}

	public void setHardTimeout(Integer hardTimeout) {
		map.put("hard_timeout", hardTimeout);
	}

	public void setSrcSwitch(String srcSwitch) {
		map.put("src_switch", srcSwitch);
	}

	public void setSrcPort(Short srcPort) {
		map.put("src_port", srcPort);
	}

	public void setDstSwitch(String dstSwitch) {
		map.put("dst_switch", dstSwitch);
	}

	public void setDstPort(Short dstPort) {
		map.put("dst_port", dstPort);
	}

	public void setDataPathSummary(String dataPathSummary) {
		map.put("data_path_summary", dataPathSummary);
	}

	public void setMatchSrcMac(String matchSrcMac) {
		map.put("matchSrcMac", matchSrcMac);
	}

	public void setMatchDstMac(String matchDstMac) {
		map.put("matchDstMac", matchDstMac);
	}

	public void setMatchEthernetFrameType(Short matchEthernetFrameType) {
		map.put("matchEthernetFrameType", matchEthernetFrameType);
	}

	public void setMatchVlanId(Short matchVlanId) {
		map.put("matchVlanId", matchVlanId);
	}

	public void setMatchVlanPriority(Byte matchVlanPriority) {
		map.put("matchVlanPriority", matchVlanPriority);
	}

	public void setMatchSrcIPv4Net(String matchSrcIPv4Net) {
		map.put("matchSrcIPv4Net", matchSrcIPv4Net);
	}

	public void setMatchDstIPv4Net(String matchDstIPv4Net) {
		map.put("matchDstIPv4Net", matchDstIPv4Net);
	}

	public void setMatchIpProto(Byte matchIpProto) {
		map.put("matchIpProto", matchIpProto);
	}

	public void setMatchIpToS(Byte matchIpToS) {
		map.put("matchIpToS", matchIpToS);
	}

	public void setMatchSrcTcpUdpPort(Short matchSrcTcpUdpPort) {
		map.put("matchSrcTcpUdpPort", matchSrcTcpUdpPort);
	}

	public void setMatchDstTcpUdpPort(Short matchDstTcpUdpPort) {
		map.put("matchDstTcpUdpPort", matchDstTcpUdpPort);
	}

	public void setActions(String actionsStr) {
		map.put("actions", actionsStr);
	}
    
    /**
     *
     * @param dbhandler
     */
    public void commitProperties(DBOperation dbhandler, IFlowPath flowPath) {
        dbhandler.setVertexProperties(flowPath.asVertex() ,map);
    }
}
