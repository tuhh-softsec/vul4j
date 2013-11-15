package net.onrc.onos.ofcontroller.proxyarp;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import net.onrc.onos.ofcontroller.util.SwitchPort;

public class ArpMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final Type type;
	private final InetAddress forAddress;
	private final byte[] packetData;
	
	private final List<SwitchPort> switchPorts = new ArrayList<SwitchPort>();
	
	public enum Type {
		REQUEST,
		REPLY
	}
	
	private ArpMessage(Type type, InetAddress address, byte[] eth) {
		// TODO Auto-generated constructor stub
		this.type = type;
		this.forAddress = address;
		this.packetData = eth;
	}
	
	private ArpMessage(Type type, InetAddress address) {
		this.type = type;
		this.forAddress = address;
		this.packetData = null;
	}
	
	public static ArpMessage newRequest(InetAddress forAddress, byte[] arpRequest) {
		return new ArpMessage(Type.REQUEST, forAddress, arpRequest);
	}
	
	public static ArpMessage newReply(InetAddress forAddress) {
		return new ArpMessage(Type.REPLY, forAddress);
	}

	public Type getType() {
		return type;
	}
	
	public InetAddress getAddress() {
		return forAddress;
	}
	
	public byte[] getPacket() {
		return packetData;
	}
}
