package net.onrc.onos.ofcontroller.bgproute;

import java.net.InetAddress;

import org.codehaus.jackson.annotate.JsonProperty;

import com.google.common.net.InetAddresses;

public class BgpPeer {
	private final String interfaceName;
	private final InetAddress ipAddress;
	
	public BgpPeer(@JsonProperty("interface") String interfaceName,
				   @JsonProperty("ipAddress") String ipAddress) {
		this.interfaceName = interfaceName;
		this.ipAddress = InetAddresses.forString(ipAddress);
	}
	
	public String getInterfaceName() {
		return interfaceName;
	}

	public InetAddress getIpAddress() {
		return ipAddress;
	}
}
