package net.onrc.onos.ofcontroller.bgproute;

import java.net.InetAddress;

import net.onrc.onos.ofcontroller.util.Dpid;
import net.onrc.onos.ofcontroller.util.Port;
import net.onrc.onos.ofcontroller.util.SwitchPort;

import org.codehaus.jackson.annotate.JsonProperty;
import org.openflow.util.HexString;

import com.google.common.net.InetAddresses;

public class Interface {
	private SwitchPort switchPort = null;
	private long dpid;
	private short port;
	private InetAddress ipAddress;
	private int prefixLength;
	
	public synchronized SwitchPort getSwitchPort() {
		if (switchPort == null){
			switchPort = new SwitchPort(new Dpid(dpid), new Port(port));
		}
		return switchPort;
	}
	
	public long getDpid() {
		return dpid;
	}

	@JsonProperty("dpid")
	public void setDpid(String dpid) {
		this.dpid = HexString.toLong(dpid);
	}

	public short getPort() {
		return port;
	}

	@JsonProperty("port")
	public void setPort(short port) {
		this.port = port;
	}

	public InetAddress getIpAddress() {
		return ipAddress;
	}

	@JsonProperty("ipAddress")
	public void setIpAddress(String ipAddress) {
		this.ipAddress = InetAddresses.forString(ipAddress);
	}

	public int getPrefixLength() {
		return prefixLength;
	}

	@JsonProperty("prefixLength")
	public void setPrefixLength(int prefixLength) {
		this.prefixLength = prefixLength;
	}
}
