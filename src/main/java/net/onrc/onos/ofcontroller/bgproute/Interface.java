package net.onrc.onos.ofcontroller.bgproute;

import java.net.InetAddress;

import net.onrc.onos.ofcontroller.util.Dpid;
import net.onrc.onos.ofcontroller.util.Port;
import net.onrc.onos.ofcontroller.util.SwitchPort;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.openflow.util.HexString;

import com.google.common.net.InetAddresses;

public class Interface {
	private final String name;
	private final SwitchPort switchPort;
	private final long dpid;
	private final short port;
	private final InetAddress ipAddress;
	private final int prefixLength;
	
	@JsonCreator
	public Interface (@JsonProperty("name") String name,
					  @JsonProperty("dpid") String dpid,
					  @JsonProperty("port") short port,
					  @JsonProperty("ipAddress") String ipAddress,
					  @JsonProperty("prefixLength") int prefixLength) {
		this.name = name;
		this.dpid = HexString.toLong(dpid);
		this.port = port;
		this.ipAddress = InetAddresses.forString(ipAddress);
		this.prefixLength = prefixLength;
		this.switchPort = new SwitchPort(new Dpid(this.dpid), new Port(this.port));
	}
	
	public String getName() {
		return name;
	}

	public SwitchPort getSwitchPort() {
		//TODO SwitchPort, Dpid and Port are mutable, but they could probably
		//be made immutable which would prevent the need to copy
		return new SwitchPort(new Dpid(dpid), new Port(port));
	}
	
	public long getDpid() {
		return dpid;
	}

	public short getPort() {
		return port;
	}

	public InetAddress getIpAddress() {
		return ipAddress;
	}

	public int getPrefixLength() {
		return prefixLength;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof Interface)) {
			return false;
		}
		
		Interface otherInterface = (Interface)other;
		
		//Don't check switchPort as it's comprised of dpid and port
		return (name.equals(otherInterface.name)) &&
				(dpid == otherInterface.dpid) &&
				(port == otherInterface.port) &&
				(ipAddress.equals(otherInterface.ipAddress)) &&
				(prefixLength == otherInterface.prefixLength);
	}
	
	@Override
	public int hashCode() {
		int hash = 17;
		hash = 31 * hash + name.hashCode();
		hash = 31 * hash + (int)(dpid ^ dpid >>> 32);
		hash = 31 * hash + (int)port;
		hash = 31 * hash + ipAddress.hashCode();
		hash = 31 * hash + prefixLength;
		return hash;
	}
}
