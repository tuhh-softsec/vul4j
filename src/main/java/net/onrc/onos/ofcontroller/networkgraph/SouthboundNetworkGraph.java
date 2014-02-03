package net.onrc.onos.ofcontroller.networkgraph;

import java.util.ArrayList;
import java.util.List;

import net.onrc.onos.datastore.RCObject;
import net.onrc.onos.datastore.topology.RCLink;
import net.onrc.onos.datastore.topology.RCPort;
import net.onrc.onos.datastore.topology.RCSwitch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.ramcloud.JRamCloud.ObjectDoesntExistException;
import edu.stanford.ramcloud.JRamCloud.ObjectExistsException;
import edu.stanford.ramcloud.JRamCloud.WrongVersionException;

/**
 * The southbound interface to the network graph which allows clients to 
 * mutate the graph. This class will maintain the invariants of the network
 * graph. The southbound discovery modules will use this interface to update
 * the network graph as they learn about the state of the network.
 *
 */
public class SouthboundNetworkGraph {
	private static final Logger log = LoggerFactory.getLogger(SouthboundNetworkGraph.class);
	
	private static final int NUM_RETRIES = 10;
	
	
	public void addSwitch(Switch sw) {
		RCSwitch rcSwitch = new RCSwitch(sw.getDpid());
		rcSwitch.setStatus(RCSwitch.STATUS.ACTIVE);
		
		for (Port port : sw.getPorts()) {
			RCPort rcPort = new RCPort(sw.getDpid(), (long)port.getNumber());
			rcPort.setStatus(RCPort.STATUS.ACTIVE);
			rcSwitch.addPortId(rcPort.getId());
			
			// TODO check how to write switch+ports together
			writeObject(rcPort);
		}
		
		writeObject(rcSwitch);
	}
	
	public void deactivateSwitch(Switch sw) {
		RCSwitch rcSwitch = new RCSwitch(sw.getDpid());
		
		List<RCObject> objectsToDeactive = new ArrayList<RCObject>();
		
		for (int i = 0; i < NUM_RETRIES; i++) {
			try {
				rcSwitch.read();
				rcSwitch.setStatus(RCSwitch.STATUS.INACTIVE);
				objectsToDeactive.add(rcSwitch);
				
				for (Port p : sw.getPorts()) {
					RCPort rcPort = new RCPort(sw.getDpid(), (long)p.getNumber());
					rcPort.read();
					rcPort.setStatus(RCPort.STATUS.INACTIVE);
					objectsToDeactive.add(rcPort);
				}
			} catch (ObjectDoesntExistException e) {
				log.warn("Trying to deactivate an object that doesn't exist", e);
				// We don't care to much if the object wasn't there, it's
				// being deactivated anyway
			}
			
			try {
				for (RCObject rcObject : objectsToDeactive) {	
					rcObject.update();
				}
				break;
			} catch (ObjectDoesntExistException e) {
				// Unlikely, and we don't care anyway. 
				// TODO But, this will cause everything else to fail
				log.warn("Trying to deactivate object that doesn't exist", e);
			} catch (WrongVersionException e) {
				// Need to re-read and retry
			}
		}
	}
	
	public void addPort(Switch sw, Port port) {
		RCSwitch rcSwitch = new RCSwitch(sw.getDpid());
		
		try {
			rcSwitch.read();
		} catch (ObjectDoesntExistException e) {
			log.warn("Add port failed because switch {} doesn't exist", sw.getDpid(), e);
			return;
		}
		
		RCPort rcPort = new RCPort(port.getSwitch().getDpid(), (long)port.getNumber());
		rcPort.setStatus(RCPort.STATUS.ACTIVE);
		rcSwitch.addPortId(rcPort.getId());
		
		writeObject(rcPort);
		writeObject(rcSwitch);
	}
	
	public void deactivatePort(Port port) {
		RCPort rcPort = new RCPort(port.getSwitch().getDpid(), (long)port.getNumber());
		
		for (int i = 0; i < NUM_RETRIES; i++) {
			try {
				rcPort.read();
			} catch (ObjectDoesntExistException e) {
				// oh well, we were deactivating anyway
				log.warn("Trying to deactivate a port that doesn't exist: {}", port);
				return;
			}
			
			rcPort.setStatus(RCPort.STATUS.INACTIVE);
			
			try {
				rcPort.update();
				break;
			} catch (ObjectDoesntExistException | WrongVersionException e) {
				// retry
			}
		}
	}
	
	public void addLink(Link link) {
		RCLink rcLink = new RCLink(link.getSourceSwitchDpid(), (long)link.getSourcePortNumber(),
				link.getDestinationSwitchDpid(), (long)link.getDestinationPortNumber());
		
		RCPort rcSrcPort = new RCPort(link.getSourceSwitchDpid(), (long)link.getSourcePortNumber());
		RCPort rcDstPort = new RCPort(link.getDestinationSwitchDpid(), (long)link.getDestinationPortNumber());
		
		for (int i = 0; i < NUM_RETRIES; i++) {
			try {
				rcSrcPort.read();
				rcDstPort.read();
				rcLink.create();
			} catch (ObjectDoesntExistException e) {
				// port doesn't exist
				log.error("Add link failed {}", link, e);
				return;
			} catch (ObjectExistsException e) {
				log.debug("Link already exists {}", link);
				return;
			}
			
			rcSrcPort.addLinkId(rcLink.getId());
			rcDstPort.addLinkId(rcLink.getId());
			
			rcLink.setStatus(RCLink.STATUS.ACTIVE);
			
			try {
				rcLink.update();
				rcSrcPort.update();
				rcDstPort.update();
				break;
			} catch (ObjectDoesntExistException | WrongVersionException e) {
				log.debug(" ", e);
				// retry
			}
		}
	}
	
	public void removeLink(Link link) {
		RCLink rcLink = new RCLink(link.getSourceSwitchDpid(), (long)link.getSourcePortNumber(),
				link.getDestinationSwitchDpid(), (long)link.getDestinationPortNumber());
		
		RCPort rcSrcPort = new RCPort(link.getSourceSwitchDpid(), (long)link.getSourcePortNumber());
		RCPort rcDstPort = new RCPort(link.getDestinationSwitchDpid(), (long)link.getDestinationPortNumber());
		
		for (int i = 0; i < NUM_RETRIES; i++) {
			try {
				rcSrcPort.read();
				rcDstPort.read();
				rcLink.read();
			} catch (ObjectDoesntExistException e) {
				log.error("Remove link failed {}", link, e);
				return;
			}
			
			rcSrcPort.removeLinkId(rcLink.getId());
			rcDstPort.removeLinkId(rcLink.getId());
			
			try {
				rcSrcPort.update();
				rcDstPort.update();
				rcLink.delete();
			} catch (ObjectDoesntExistException e) {
				log.error("Remove link failed {}", link, e);
				return;
			} catch (WrongVersionException e) {
				// retry
			}
		}
	}
	
	public void updateDevice(Device device) {
		// TODO implement
	}
	
	public void removeDevice(Device device) {
		// TODO implement
	}
	
	// TODO what happens if this fails? why could it fail?
	private void writeObject(RCObject object) {
		for (int i = 0; i < NUM_RETRIES; i++) {
			try {
				object.create();
			} catch (ObjectExistsException e) {
				try {
					object.read();
				} catch (ObjectDoesntExistException e1) {
					// TODO Auto-generated catch block
					log.error(" ", e);
					return;
				}
			}
			
			try {
				// TODO check API for writing without caring what's there
				object.update();
				break;
			} catch (ObjectDoesntExistException | WrongVersionException e) {
				log.debug(" ", e);
				// re-read and retry
			}
		}
	}
}
