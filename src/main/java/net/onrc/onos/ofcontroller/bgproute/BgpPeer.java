package net.onrc.onos.ofcontroller.bgproute;

import java.net.InetAddress;

import org.codehaus.jackson.annotate.JsonProperty;

import com.google.common.net.InetAddresses;

public class BgpPeer {
	private String interfaceName;
	private InetAddress ipAddress;
	
	public String getInterfaceName() {
		return interfaceName;
	}
	
	@JsonProperty("interface")
	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
	
	public InetAddress getIpAddress() {
		return ipAddress;
	}
	
	@JsonProperty("ipAddress")
	public void setIpAddress(String ipAddress) {
		this.ipAddress = InetAddresses.forString(ipAddress);
	}
}
