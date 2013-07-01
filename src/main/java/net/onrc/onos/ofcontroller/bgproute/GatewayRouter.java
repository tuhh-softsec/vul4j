package net.onrc.onos.ofcontroller.bgproute;

import net.floodlightcontroller.util.MACAddress;
import net.onrc.onos.ofcontroller.util.Dpid;
import net.onrc.onos.ofcontroller.util.IPv4;
import net.onrc.onos.ofcontroller.util.Port;
import net.onrc.onos.ofcontroller.util.SwitchPort;

import org.codehaus.jackson.annotate.JsonProperty;
import org.openflow.util.HexString;

public class GatewayRouter {
	private SwitchPort attachmentPoint = null;
	private long dpid;
	private short port;
	private MACAddress routerMac;
	private IPv4 routerIp;
	private IPv4 myIpAddress;
	
	
	public SwitchPort getAttachmentPoint() {
		if (attachmentPoint == null){
			attachmentPoint = new SwitchPort(new Dpid(dpid), new Port(port));
		}
		return attachmentPoint;
	}
	
	public long getDpid() {
		return dpid;
	}

	@JsonProperty("attachmentDpid")
	public void setDpid(String dpid) {
		this.dpid = HexString.toLong(dpid);
	}

	public short getPort() {
		return port;
	}

	@JsonProperty("attachmentPort")
	public void setPort(short port) {
		this.port = port;
	}

	public MACAddress getRouterMac() {
		return routerMac;
	}
	
	@JsonProperty("macAddress")
	public void setRouterMac(String routerMac) {
		this.routerMac = MACAddress.valueOf(routerMac);;
	}

	public IPv4 getRouterIp() {
		return routerIp;
	}
	
	@JsonProperty("ipAddress")
	public void setRouterIp(String routerIp) {
		this.routerIp = new IPv4(routerIp);
	}
	
	public IPv4 getMyIpAddress() {
		return myIpAddress;
	}
	
	@JsonProperty("myIpAddress")
	public void setMyIpAddress(String myIpAddress) {
		this.myIpAddress = new IPv4(myIpAddress);
	}
}
