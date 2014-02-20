package net.onrc.onos.ofcontroller.networkgraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

import net.onrc.onos.datastore.RCObject;
import net.onrc.onos.datastore.RCObject.WriteOp;
import net.onrc.onos.datastore.topology.RCLink;
import net.onrc.onos.datastore.topology.RCPort;
import net.onrc.onos.datastore.topology.RCPort.STATUS;
import net.onrc.onos.datastore.topology.RCSwitch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	/**
	 * Add a switch to the database.
	 *
	 * @param sw the switch to add.
	 * @param portEvents the corresponding switch ports to add.
	 * @return true on success, otherwise false.
	 */
	public boolean addSwitch(SwitchEvent sw,
				 Collection<PortEvent> portEvents) {
		log.debug("Adding switch {}", sw);
		ArrayList<WriteOp> groupOp = new ArrayList<>();

		RCSwitch rcSwitch = new RCSwitch(sw.getDpid());
		rcSwitch.setStatus(RCSwitch.STATUS.ACTIVE);

		// XXX Is ForceCreating Switch on DB OK here?
		// If ForceCreating, who ever is calling this method needs
		// to assure that DPID is unique cluster-wide, etc.
		groupOp.add(WriteOp.ForceCreate(rcSwitch));

		for (PortEvent portEvent : portEvents) {
			RCPort rcPort = new RCPort(sw.getDpid(), portEvent.getNumber());
			rcPort.setStatus(RCPort.STATUS.ACTIVE);

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
		return !failed;
	}

	/**
	 * Update a switch as inactive in the database.
	 *
	 * @param sw the switch to update.
	 * @param portEvents the corresponding switch ports to update.
	 * @return true on success, otherwise false.
	 */
	public boolean deactivateSwitch(SwitchEvent sw,
					Collection<PortEvent> portEvents) {
		log.debug("Deactivating switch {}", sw);
		RCSwitch rcSwitch = new RCSwitch(sw.getDpid());

		List<WriteOp> groupOp = new ArrayList<>();
		rcSwitch.setStatus(RCSwitch.STATUS.INACTIVE);

		groupOp.add(WriteOp.ForceCreate(rcSwitch));

		for (PortEvent portEvent : portEvents) {
			RCPort rcPort = new RCPort(sw.getDpid(), (long)portEvent.getNumber());
			rcPort.setStatus(RCPort.STATUS.INACTIVE);

			groupOp.add(WriteOp.ForceCreate(rcPort));
		}

		boolean failed = RCObject.multiWrite(groupOp);

		return !failed;
	}

	/**
	 * Add a port to the database.
	 *
	 * @param port the port to add.
	 * @return true on success, otherwise false.
	 */
	public boolean addPort(PortEvent port) {
		log.debug("Adding port {}", port);

		RCPort rcPort = new RCPort(port.getDpid(), port.getNumber());
		rcPort.setStatus(RCPort.STATUS.ACTIVE);
		rcPort.forceCreate();
		// TODO add description into RCPort
		//rcPort.setDescription(port.getDescription());

		return true;
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
		rcPort.setStatus(STATUS.INACTIVE);
		
		rcPort.forceCreate();

		return true;
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

	public boolean removeLink(LinkEvent linkEvent) {
		log.debug("Removing link {}", linkEvent);
		
		RCLink rcLink = new RCLink(linkEvent.getSrc().getDpid(), linkEvent.getSrc().getNumber(),
				linkEvent.getDst().getDpid(), linkEvent.getDst().getNumber());
		rcLink.forceDelete();

		return true;
	}

	/**
	 * Add a device to the database.
	 *
	 * @param device the device to add.
	 * @return true on success, otherwise false.
	 */
	public boolean addDevice(DeviceEvent device) {
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
}
