package net.onrc.onos.ofcontroller.proxyarp;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import net.floodlightcontroller.util.MACAddress;
import net.onrc.onos.ofcontroller.util.SwitchPort;

public class ArpMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final Type type;
	private final InetAddress forAddress;
	private final byte[] packetData;
	
	//ARP reply message needs MAC info
	private final MACAddress mac;
	//only send the ARP request message to the device attachment needs the attachment switch and port. 
	private final long outSwitch; 
	private final short outPort;
	
	
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
		this.mac = null;
		this.outSwitch = -1;
		this.outPort = -1;
	}
	
	private ArpMessage(Type type, InetAddress address) {
		this.type = type;
		this.forAddress = address;
		this.packetData = null;
		this.mac = null;
		this.outSwitch = -1;
		this.outPort = -1;
		
	}
	// the ARP reply message with MAC
	private ArpMessage(Type type, InetAddress address, MACAddress mac) {
		this.type = type;
		this.forAddress = address;
		this.packetData = null;
		this.mac = mac;
		this.outSwitch = -1;
		this.outPort = -1;
	}
	
	// construct ARP request message with attachment switch and port
	private ArpMessage(Type type, InetAddress address, byte[] arpRequest,
			long outSwitch, short outPort) {
		this.type = type;
		this.forAddress = address;
		this.packetData = arpRequest; 	
		this.mac = null;
		this.outSwitch = outSwitch; 
		this.outPort = outPort;	
	}

	public static ArpMessage newRequest(InetAddress forAddress, byte[] arpRequest) {
		return new ArpMessage(Type.REQUEST, forAddress, arpRequest);
	}
	
	public static ArpMessage newReply(InetAddress forAddress) {
		return new ArpMessage(Type.REPLY, forAddress);
	}
	//ARP reply message with MAC
	public static ArpMessage newReply(InetAddress forAddress, MACAddress mac) {
		return new ArpMessage(Type.REPLY, forAddress, mac);

	}
	//ARP reqsuest message with attachment switch and port
	public static ArpMessage newRequest(InetAddress forAddress, byte[] arpRequest, long outSwitch, short outPort ) {
		return new ArpMessage(Type.REQUEST, forAddress, arpRequest, outSwitch, outPort);

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
	public MACAddress getMAC() {
		return mac;
	}

	public long getOutSwitch() {
		return outSwitch;
	}

	public short getOutPort() {
		return outPort;
	}

}
