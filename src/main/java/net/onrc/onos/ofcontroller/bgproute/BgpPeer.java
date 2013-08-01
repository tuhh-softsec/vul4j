package net.onrc.onos.ofcontroller.bgproute;

import java.net.InetAddress;

import net.floodlightcontroller.util.MACAddress;

import org.codehaus.jackson.annotate.JsonProperty;

import com.google.common.net.InetAddresses;

public class BgpPeer {
	private String interfaceName;
	private InetAddress ipAddress;
	private MACAddress macAddress;
	
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
	
	public MACAddress getMacAddress() {
		return macAddress;
	}
	
	@JsonProperty("macAddress")
	public void setMacAddress(String macAddress) {
		this.macAddress = MACAddress.valueOf(macAddress);
	}
}
