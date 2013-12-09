package net.onrc.onos.ofcontroller.core.config;

import java.net.InetAddress;

import net.floodlightcontroller.util.MACAddress;
import net.onrc.onos.ofcontroller.bgproute.Interface;

import org.openflow.util.HexString;

public class DefaultConfiguration implements IConfigInfoService {

	@Override
	public boolean isInterfaceAddress(InetAddress address) {
		return false;
	}

	@Override
	public boolean inConnectedNetwork(InetAddress address) {
		return false;
	}

	@Override
	public boolean fromExternalNetwork(long inDpid, short inPort) {
		return false;
	}

	@Override
	public Interface getOutgoingInterface(InetAddress dstIpAddress) {
		return null;
	}

	@Override
	public boolean hasLayer3Configuration() {
		return false;
	}
	
	@Override
	public MACAddress getRouterMacAddress() {
		return MACAddress.valueOf(HexString.fromHexString("000000000001"));
	}

	@Override
	public short getVlan() {
		return 0;
	}

}
