package net.onrc.onos.ofcontroller.networkgraph;

import java.net.InetAddress;

import net.floodlightcontroller.util.MACAddress;
import net.onrc.onos.datastore.topology.RCPort;
import net.onrc.onos.datastore.topology.RCSwitch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.ramcloud.JRamCloud.ObjectDoesntExistException;

/**
 * The "NB" read-only Network Map.
 *
 * TODO Current implementation directly read from DB, but
 * eventually, it should read from In-memory shared Network Map instance within ONOS process.
 *
 */
public class NetworkGraphImpl implements NetworkGraph {

	private static final Logger log = LoggerFactory.getLogger(NetworkGraphImpl.class);

	@Override
	public Switch getSwitch(long dpid) {
		SwitchImpl sw = new SwitchImpl(this);

		RCSwitch rcSwitch = new RCSwitch(dpid);
		try {
			rcSwitch.read();
		} catch (ObjectDoesntExistException e) {
			log.warn("Tried to get a switch that doesn't exist {}", dpid);
			return null;
		}

		sw.setDpid(rcSwitch.getDpid());

		for (byte[] portId : rcSwitch.getAllPortIds()) {
			RCPort rcPort = RCPort.createFromKey(portId);
			try {
				rcPort.read();

				PortImpl port = new PortImpl(this);
				//port.setDpid(dpid);

				// TODO why are port numbers long?
				//port.setPortNumber((short)rcPort.getNumber());

				port.setSwitch(sw);
				sw.addPort(port);

			} catch (ObjectDoesntExistException e) {
				log.warn("Tried to read port that doesn't exist", rcPort);
			}
		}

		return sw;
	}

	@Override
	public Iterable<Switch> getSwitches() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<Link> getLinks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<Link> getLinksFromSwitch(long dpid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<Device> getDeviceByIp(InetAddress ipAddress) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<Device> getDeviceByMac(MACAddress address) {
		// TODO Auto-generated method stub
		return null;
	}

}
