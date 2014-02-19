package net.onrc.onos.ofcontroller.networkgraph;

import java.util.ArrayList;
import java.util.List;

import net.onrc.onos.datastore.RCObject;
import net.onrc.onos.datastore.RCObject.WriteOp;
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
 * Modification to the Network Map by this module will:
 * 1. Writes to Cluster-wide DataStore.
 * 2. Update ONOS instance In-memory Network Map.
 * 3. Send-out Notification. (TBD)
 *    (XXX: To update other instances In-memory Network Map,
 *          notification should be triggered here.
 *          But if we want to aggregate notification to minimize notification,
 *          It might be better for the caller to trigger notification.)
 *
 */
public class NetworkGraphDatastore {
	private static final Logger log = LoggerFactory.getLogger(NetworkGraphDatastore.class);

	private static final int NUM_RETRIES = 10;

	private final TopologyManager graph;

	public NetworkGraphDatastore(TopologyManager graph) {
		this.graph = graph;
	}

	/**
	 * Add a switch to the database.
	 *
	 * @param sw the switch to add.
	 * @return true on success, otherwise false.
	 */
	public boolean addSwitch(SwitchEvent sw) {
		log.debug("Adding switch {}", sw);
		ArrayList<WriteOp> groupOp = new ArrayList<>();

		RCSwitch rcSwitch = new RCSwitch(sw.getDpid());
		rcSwitch.setStatus(RCSwitch.STATUS.ACTIVE);

		// XXX Is ForceCreating Switch on DB OK here?
		// If ForceCreating, who ever is calling this method needs
		// to assure that DPID is unique cluster-wide, etc.
		groupOp.add(WriteOp.ForceCreate(rcSwitch));

		//for (Port port : sw.getPorts()) {
		for (PortEvent portEvent : sw.getPorts()) {
			RCPort rcPort = new RCPort(sw.getDpid(), portEvent.getNumber());
			rcPort.setStatus(RCPort.STATUS.ACTIVE);
			//rcSwitch.addPortId(rcPort.getId());

			groupOp.add(WriteOp.ForceCreate(rcPort));
		}

		boolean failed = RCObject.multiWrite(groupOp);

		if (failed) {
		    log.error("Adding Switch {} and its ports failed.", sw.getDpid());
		    for (WriteOp op : groupOp) {
			log.debug("Operation:{} for {} - Result:{}", op.getOp(), op.getObject(), op.getStatus() );

			// If we changed the operation from ForceCreate to
			// Conditional operation (Create/Update) then we should retry here.
		    }
		}
		return (! failed);
	}

	/**
	 * Update a switch as inactive in the database.
	 *
	 * @param sw the switch to update.
	 * @return true on success, otherwise false.
	 */
	public boolean deactivateSwitch(SwitchEvent sw) {
		log.debug("Deactivating switch {}", sw);
		RCSwitch rcSwitch = new RCSwitch(sw.getDpid());

		List<RCObject> objectsToDeactive = new ArrayList<RCObject>();

		for (int i = 0; i < NUM_RETRIES; i++) {
			try {
				rcSwitch.read();
				rcSwitch.setStatus(RCSwitch.STATUS.INACTIVE);
				objectsToDeactive.add(rcSwitch);

//				for (Port p : sw.getPorts()) {
//					RCPort rcPort = new RCPort(sw.getDpid(), (long)p.getNumber());
//					rcPort.read();
//					rcPort.setStatus(RCPort.STATUS.INACTIVE);
//					objectsToDeactive.add(rcPort);
//				}
			} catch (ObjectDoesntExistException e) {
				log.warn("Trying to deactivate an object that doesn't exist", e);
				// We don't care to much if the object wasn't there, it's
				// being deactivated anyway
				return true;	// Success: no object in the DB
			}

			try {
				for (RCObject rcObject : objectsToDeactive) {
					rcObject.update();
				}
				return true;		// Success
			} catch (ObjectDoesntExistException e) {
				// Unlikely, and we don't care anyway.
				// TODO But, this will cause everything else to fail
				log.warn("Trying to deactivate object that doesn't exist", e);
				return true;	// Success: no object in the DB
			} catch (WrongVersionException e) {
				// Need to re-read and retry
			}
		}

		return false;				// Failure
	}

	/**
	 * Add a port to the database.
	 *
	 * @param port the port to add.
	 * @return true on success, otherwise false.
	 */
	public boolean addPort(PortEvent port) {
		log.debug("Adding port {}", port);
		//RCSwitch rcSwitch = new RCSwitch(sw.getDpid());

		//try {
			//rcSwitch.read();
		//} catch (ObjectDoesntExistException e) {
			//log.warn("Add port failed because switch {} doesn't exist", sw.getDpid(), e);
			//return false;
		//}

		RCPort rcPort = new RCPort(port.getDpid(), port.getNumber());
		rcPort.setStatus(RCPort.STATUS.ACTIVE);
		// TODO add description into RCPort
		//rcPort.setDescription(port.getDescription());
		//rcSwitch.addPortId(rcPort.getId());

		boolean success = writeObject(rcPort);
		// success &= writeObject(rcSwitch);
		return success;
	}

	/**
	 * Update a port as inactive in the database.
	 *
	 * @param port the port to update.
	 * @return true on success, otherwise false.
	 */
	public boolean deactivatePort(PortEvent port) {
		log.debug("Deactivating port {}", port);
		RCPort rcPort = new RCPort(port.getDpid(), port.getNumber());

		for (int i = 0; i < NUM_RETRIES; i++) {
			try {
			    rcPort.read();
			} catch (ObjectDoesntExistException e) {
				// oh well, we were deactivating anyway
				log.warn("Trying to deactivate a port that doesn't exist: {}", port);
				return true;	// Success: no object in the DB
			}

			rcPort.setStatus(RCPort.STATUS.INACTIVE);

			try {
				rcPort.update();
				return true;		// Success
			} catch (ObjectDoesntExistException | WrongVersionException e) {
				// retry
			}
		}

		return false;				// Failure
	}

	/**
	 * Add a link to the database.
	 *
	 * @param link the link to add.
	 * @return true on success, otherwise false.
	 */
	public boolean addLink(LinkEvent link) {
		log.debug("Adding link {}", link);

		RCLink rcLink = new RCLink(link.getSrc().getDpid(),
					   link.getSrc().getNumber(),
					   link.getDst().getDpid(),
					   link.getDst().getNumber());

		// XXX This method is called only by discovery,
		// which means what we are trying to write currently is the truth
		// so we can force write here
		//
		// TODO: We need to check for errors
		rcLink.setStatus(RCLink.STATUS.ACTIVE);
		rcLink.forceCreate();

		return true;					// Success
	}

	/**
	 * Remove a link from the database.
	 *
	 * @param link the link to remove.
	 * @return true on success, otherwise false.
	 */
	public boolean removeLink(LinkEvent link) {
		log.debug("Removing link {}", link);
		RCLink rcLink = new RCLink(link.getSrc().getDpid(),
					   link.getSrc().getNumber(),
					   link.getDst().getDpid(),
					   link.getDst().getNumber());

		//RCPort rcSrcPort = new RCPort(link.getSourceSwitchDpid(), (long)link.getSourcePortNumber());
		//RCPort rcDstPort = new RCPort(link.getDestinationSwitchDpid(), (long)link.getDestinationPortNumber());

		for (int i = 0; i < NUM_RETRIES; i++) {
			try {
				//rcSrcPort.read();
				//rcDstPort.read();
				rcLink.read();
			} catch (ObjectDoesntExistException e) {
			    // XXX Note: This error might be harmless, if triggered by out-dated remove Link event
				log.error("Remove link failed {}", link, e);
				return true;	// Success: no object in the DB
			}

			//rcSrcPort.removeLinkId(rcLink.getId());
			//rcDstPort.removeLinkId(rcLink.getId());

			try {
				//rcSrcPort.update();
				//rcDstPort.update();
				rcLink.delete();
				return true;			// Success
			} catch (ObjectDoesntExistException e) {
				log.error("Remove link failed {}", link, e);
				return true;	// Success: no object in the DB
			} catch (WrongVersionException e) {
				// retry
			}
		}

		return false;					// Failure
	}

	/**
	 * Add a device to the database.
	 *
	 * @param device the device to add.
	 * @return true on success, otherwise false.
	 */
	public boolean updateDevice(DeviceEvent device) {
		// TODO implement
		return false;			// Failure: not implemented yet
	}

	/**
	 * Remove a device from the database.
	 *
	 * @param device the device to remove.
	 * @return true on success, otherwise false.
	 */
	public boolean removeDevice(DeviceEvent device) {
		// TODO implement
		return false;			// Failure: not implemented yet
	}

	/**
	 * Write an object to the database.
	 *
	 * @param object the object to write.
	 * @return true on success, otherwise false.
	 */
	private boolean writeObject(RCObject object) {
		for (int i = 0; i < NUM_RETRIES; i++) {
			try {
				object.create();
			} catch (ObjectExistsException e) {
				try {
					object.read();
				} catch (ObjectDoesntExistException e1) {
					// TODO Auto-generated catch block
					log.error(" ", e);
					return false;		// Failure
				}
			}

			try {
				// TODO check API for writing without caring what's there
				object.update();
				return true;			// Success
			} catch (ObjectDoesntExistException | WrongVersionException e) {
				log.debug(" ", e);
				// re-read and retry
			}
		}

		return false;					// Failure
	}
}
