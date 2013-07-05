package net.onrc.onos.ofcontroller.bgproute;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;
import org.openflow.util.HexString;

public class Configuration {
	private long bgpdAttachmentDpid;
	private short bgpdAttachmentPort;
	private List<String> switches;
	private List<Interface> interfaces;
	private List<BgpPeer> peers;
	//private Map<String, GatewayRouter> gateways;
	
	public Configuration() {
		// TODO Auto-generated constructor stub
	}

	public long getBgpdAttachmentDpid() {
		return bgpdAttachmentDpid;
	}

	@JsonProperty("bgpdAttachmentDpid")
	public void setBgpdAttachmentDpid(String bgpdAttachmentDpid) {
		this.bgpdAttachmentDpid = HexString.toLong(bgpdAttachmentDpid);
	}

	public short getBgpdAttachmentPort() {
		return bgpdAttachmentPort;
	}

	@JsonProperty("bgpdAttachmentPort")
	public void setBgpdAttachmentPort(short bgpdAttachmentPort) {
		this.bgpdAttachmentPort = bgpdAttachmentPort;
	}

	public List<String> getSwitches() {
		return switches;
	}

	@JsonProperty("switches")
	public void setSwitches(List<String> switches) {
		this.switches = switches;
	}

	public List<Interface> getInterfaces() {
		return interfaces;
	}

	@JsonProperty("interfaces")
	public void setInterfaces(List<Interface> interfaces) {
		this.interfaces = interfaces;
	}
	
	public List<BgpPeer> getPeers() {
		return peers;
	}

	@JsonProperty("bgpPeers")
	public void setPeers(List<BgpPeer> peers) {
		this.peers = peers;
	}

	/*
	public Map<String, GatewayRouter> getGateways() {
		return gateways;
	}

	@JsonProperty("gateways")
	public void setGateways(Map<String, GatewayRouter> gateways) {
		this.gateways = gateways;
	}*/

}
