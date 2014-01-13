package net.onrc.onos.ofcontroller.flowmanager;

import java.util.HashMap;
import java.util.Map;

import net.onrc.onos.graph.DBOperation;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowEntry;

public class FlowEntryProperty {
    private Map<String, Object> map = new HashMap<>();
    
    public void setFlowId(String value) {
        map.put("flow_id", value);
    }
    
    public void setUserState(String value) {
        map.put("user_state", value);
    }
    
    public void setFlowEntryId(String value) {
        map.put("flow_entry_id", value);
    }
    
    public void setType(String value) {
        map.put("type", value);
    }
    
    public void setInstallerId(String value) {
        map.put("installer_id", value);
    }
    
    public void setFlowPathType(String value) {
        map.put("flow_path_type", value);
    }
    
    public void setFlowPathUserState(String value) {
        map.put("user_state", value);
    }
    
    public void flow_path_flags(Long value) {
        map.put("flow_path_flags", value);
    }
    
    public void setIdleTimeout(Integer value) {
        map.put("idle_timeout", value);
    }
    
    public void setHardTimeout(Integer value) {
        map.put("hard_timeout", value);
    }
    
    public void setSwitchDpid(String value) {
        map.put("switch_dpid", value);
    }
    
    public void setSwitchState(String value) {
        map.put("switch_state", value);
    }
    
    public void setSrcSwitch(String value) {
        map.put("src_switch", value);
    }
    
    public void setSrcPort(Short value) {
        map.put("src_port", value);
    }
    
    public void setDstSwitch(String value) {
        map.put("dst_switch", value);
    }
    
    public void setDstPort(Short value) {
        map.put("dst_port", value);
    }
    
    public void setMatchSrcMac(String value) {
        map.put("matchSrcMac", value);
    }
    
    public void setMatchDstMac(String value) {
        map.put("matchDstMac", value);
    }
    
    public void setMatchEthernetFrameType(Short value) {
        map.put("matchEthernetFrameType", value);
    }
    
    public void setMatchVlanId(Short value) {
        map.put("matchVlanId", value);
    }
    
    public void setMatchVlanPriority(Byte value) {
        map.put("matchVlanPriority", value);
    }
    
    public void setMatchSrcIPv4Net(String value) {
        map.put("matchSrcIPv4Net", value);
    }
    
    public void setMatchDstIPv4Net(String value) {
        map.put("matchDstIPv4Net", value);
    }
    
    public void setMatchIpProto(Byte value) {
        map.put("matchIpProto", value);
    }
    
    public void setMatchIpToS(Byte value) {
        map.put("matchIpToS", value);
    }
    
    public void setMatchInPort(Short value) {
        map.put("matchInPort", value);
    }
    
    public void setMatchSrcTcpUdpPort(Short value) {
        map.put("matchSrcTcpUdpPort", value);
    }
    
    public void setMatchDstTcpUdpPort(Short value) {
        map.put("matchDstTcpUdpPort", value);
    }
    
    public void setActions(String value) {
        map.put("actions", value);
    }
    
    public void setActionOutputPort(Short value) {
        map.put("actionOutputPort", value);
    }
    
    public void setDataPathSummary(String value) {
        map.put("data_path_summary", value);
    }
    
    /**
     *
     * @param dbhandler
     */
    public void commitProperties(DBOperation dbhandler, IFlowEntry flowEntry) {
        dbhandler.setVertexProperties(flowEntry.asVertex(), map);
    }
}
