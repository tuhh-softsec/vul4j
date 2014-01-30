package net.onrc.onos.datastore.topology;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import net.onrc.onos.datastore.RCObject;
import net.onrc.onos.datastore.RCTable;
import net.onrc.onos.datastore.utils.ByteArrayComparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.Kryo;

import edu.stanford.ramcloud.JRamCloud.ObjectDoesntExistException;
import edu.stanford.ramcloud.JRamCloud.ObjectExistsException;
import edu.stanford.ramcloud.JRamCloud.WrongVersionException;

/**
 * Switch Object in RC.
 *
 * @note This class will not maintain invariants. e.g. It will NOT automatically
 *       remove Ports on Switch, when deleting a Switch.
 *
 */
public class RCSwitch extends RCObject {
    private static final Logger log = LoggerFactory.getLogger(RCSwitch.class);

    private static final ThreadLocal<Kryo> switchKryo = new ThreadLocal<Kryo>() {
	@Override
	protected Kryo initialValue() {
	    Kryo kryo = new Kryo();
	    kryo.setRegistrationRequired(true);
	    kryo.setReferences(false);
	    kryo.register(byte[].class);
	    kryo.register(byte[][].class);
	    kryo.register(HashMap.class);
	    // TODO check if we should explicitly specify EnumSerializer
	    kryo.register(STATUS.class);
	    return kryo;
	}
    };

    public static final String GLOBAL_SWITCH_TABLE_NAME = "G:Switch";

    // FIXME these should be Enum or some number, not String
    private static final String PROP_DPID = "dpid";
    private static final String PROP_STATUS = "status";
    private static final String PROP_PORT_IDS = "port-ids";

    // must not re-order enum members, ordinal will be sent over wire
    public enum STATUS {
	INACTIVE, ACTIVE;
    }

    private final Long dpid;
    private STATUS status;
    private TreeSet<byte[]> portIds;
    transient private boolean isPortIdsModified;

    public static final int SWITCHID_BYTES = 2 + 8;

    public static byte[] getSwichID(Long dpid) {
	if (dpid == null) {
	    throw new IllegalArgumentException("dpid cannot be null");
	}
	return ByteBuffer.allocate(SWITCHID_BYTES).putChar('S').putLong(dpid)
	        .array();
    }

    public static long getDpidFromKey(byte[] key) {
	ByteBuffer keyBuf = ByteBuffer.wrap(key);
	if (keyBuf.getChar() != 'S') {
	    throw new IllegalArgumentException("Invalid Switch key");
	}
	return keyBuf.getLong();
    }

    // FIXME specify DPID here, or Should caller specify the key it self?
    // In other words, should layer above have the control of the ID?
    public RCSwitch(Long dpid) {
	super(RCTable.getTable(GLOBAL_SWITCH_TABLE_NAME), getSwichID(dpid));

	this.dpid = dpid;
	this.status = STATUS.INACTIVE;
	this.portIds = new TreeSet<>(ByteArrayComparator.BYTEARRAY_COMPARATOR);
	this.isPortIdsModified = true;
    }

    public static RCSwitch createFromKey(byte[] key) {
	return new RCSwitch(getDpidFromKey(key));
    }

    public STATUS getStatus() {
	return status;
    }

    public void setStatus(STATUS status) {
	this.status = status;
	getObjectMap().put(PROP_STATUS, status);
    }

    public Long getDpid() {
	return dpid;
    }

    public byte[] getId() {
	return getKey();
    }

    public void addPortId(byte[] portId) {
	// TODO: Should we copy portId, or reference is OK.
	isPortIdsModified |= portIds.add(portId);
    }

    public void removePortId(byte[] portId) {
	isPortIdsModified |= portIds.remove(portId);
    }

    public void emptyPortIds() {
	portIds.clear();
	this.isPortIdsModified = true;
    }

    public void addAllToPortIds(Collection<byte[]> portIds) {
	// TODO: Should we copy portId, or reference is OK.
	isPortIdsModified |= this.portIds.addAll(portIds);
    }

    /**
     *
     * @return Unmodifiable Set view of all the PortIds;
     */
    public Set<byte[]> getAllPortIds() {
	return Collections.unmodifiableSet(portIds);
    }

    @Override
    public void serializeAndSetValue() {
	Map<Object, Object> map = getObjectMap();

	map.put(PROP_DPID, this.dpid);
	if (isPortIdsModified) {
	    byte[] portIdArray[] = new byte[portIds.size()][];
	    map.put(PROP_PORT_IDS, portIds.toArray(portIdArray));
	    isPortIdsModified = false;
	}

	serializeAndSetValue(switchKryo.get(), map);
    }

    @Override
    public Map<Object, Object> deserializeObjectFromValue() {
	Map<Object, Object> map = deserializeObjectFromValue(switchKryo.get());

	this.status = (STATUS) map.get(PROP_STATUS);

	if (this.portIds == null) {
	    this.portIds = new TreeSet<>(
		    ByteArrayComparator.BYTEARRAY_COMPARATOR);
	}
	byte[] portIdArray[] = (byte[][]) map.get(PROP_PORT_IDS);
	if (portIdArray != null) {
	    this.portIds.clear();
	    this.portIds.addAll(Arrays.asList(portIdArray));
	    isPortIdsModified = false;
	} else {
	    // trigger write on next serialize
	    isPortIdsModified = true;
	}
	return map;
    }

    @Override
    public String toString() {
	// TODO OUTPUT ALL?
	return "[RCSwitch 0x" + Long.toHexString(dpid) + " STATUS:" + status
	        + "]";
    }

    public static void main(String argv[]) {
	// create active switch 0x1 with 2 ports
	RCSwitch sw = new RCSwitch(0x1L);
	sw.setStatus(STATUS.ACTIVE);
	sw.addPortId("SW0x0001P001".getBytes());
	sw.addPortId("SW0x0001P002".getBytes());

	try {
	    sw.create();
	} catch (ObjectExistsException e) {
	    log.debug("Create Switch Failed", e);
	    e.printStackTrace();
	}

	// read switch 0x1
	RCSwitch swRead = new RCSwitch(0x1L);
	try {
	    swRead.read();
	} catch (ObjectDoesntExistException e) {
	    log.debug("Reading Switch Failed", e);
	}
	assert (swRead.getStatus() == STATUS.ACTIVE);
	for (byte[] portId : swRead.getAllPortIds()) {
	    // bad example code, portId is not expected to be ASCII string
	    log.debug("PortId: {}", new String(portId));
	}
	assert (swRead.getAllPortIds().size() == 2);

	// update 0x1
	swRead.setStatus(STATUS.INACTIVE);
	swRead.removePortId("SW0x0001P001".getBytes());
	try {
	    swRead.update();
	} catch (ObjectDoesntExistException | WrongVersionException e) {
	    log.debug("Updating Switch Failed", e);
	}

	// read 0x1 again and delete
	RCSwitch swRead2 = new RCSwitch(0x1L);
	try {
	    swRead2.read();
	} catch (ObjectDoesntExistException e) {
	    log.debug("Reading Switch Again Failed", e);
	}
	assert (swRead2.getStatus() == STATUS.INACTIVE);
	for (byte[] portId : swRead2.getAllPortIds()) {
	    // bad example code, portId is not expected to be ASCII string
	    log.debug("PortId: {}", new String(portId));
	}
	assert (swRead2.getAllPortIds().size() == 1);
	try {
	    swRead2.delete();
	} catch (ObjectDoesntExistException e) {
	    log.debug("Deleting Switch Failed", e);
	}

	RCSwitch swRead3 = new RCSwitch(0x1L);
	try {
	    swRead3.read();
	} catch (ObjectDoesntExistException e) {
	    log.debug("Switch not found as expected");
	}

	topology_setup();
	topology_walk();
	topology_delete();
    }

    private static void topology_setup() {
	log.debug("topology_setup start.");

	RCSwitch sw1 = new RCSwitch(0x1L);
	sw1.setStatus(STATUS.ACTIVE);
	try {
	    sw1.create();
	} catch (ObjectExistsException e) {
	    log.error("Switch creation failed", e);
	}

	RCPort sw1p1 = new RCPort(0x1L, 1L);
	sw1p1.setStatus(RCPort.STATUS.ACTIVE);
	RCPort sw1p2 = new RCPort(0x1L, 2L);
	sw1p2.setStatus(RCPort.STATUS.ACTIVE);
	try {
	    sw1p1.create();
	    sw1p2.create();
	} catch (ObjectExistsException e) {
	    log.error("Port creation failed", e);
	}

	sw1.emptyPortIds();
	sw1.addPortId(sw1p1.getId());
	sw1.addPortId(sw1p2.getId());
	try {
	    sw1.update();
	} catch (ObjectDoesntExistException | WrongVersionException e) {
	    log.error("Switch update failed", e);
	}

	RCDevice d1 = new RCDevice(new byte[] { 0, 1, 2, 3, 4, 5, 6 });
	d1.addPortId(sw1p1.getId());
	try {
	    d1.create();
	} catch (ObjectExistsException e) {
	    log.error("Device creation failed", e);
	}

	RCSwitch sw2 = new RCSwitch(0x2L);
	sw2.setStatus(STATUS.ACTIVE);
	RCPort sw2p1 = new RCPort(0x2L, 1L);
	sw2p1.setStatus(RCPort.STATUS.ACTIVE);
	RCPort sw2p2 = new RCPort(0x2L, 2L);
	sw2p2.setStatus(RCPort.STATUS.ACTIVE);

	sw2.addPortId(sw2p1.getId());
	sw2.addPortId(sw2p2.getId());
	sw2.addAllToPortIds(Arrays.asList(sw2p1.getId(), sw2p2.getId()));
	assert (sw2.getAllPortIds().size() == 2);

	RCDevice d2 = new RCDevice(new byte[] { 6, 5, 4, 3, 2, 1, 0 });
	d2.addPortId(sw2p2.getId());

	try {
	    sw2.create();
	    sw2p1.create();
	    sw2p2.create();
	    d2.create();
	} catch (ObjectExistsException e) {
	    log.error("One of Switch/Port/Device creation failed", e);
	}

	RCLink l1 = new RCLink(0x1L, 2L, 0x2L, 1L);
	l1.setStatus(RCLink.STATUS.ACTIVE);
	try {
	    l1.create();
	} catch (ObjectExistsException e) {
	    log.error("Link creation failed", e);
	}
	log.debug("topology_setup end.");
    }

    private static void topology_walk() {
	log.debug("topology_walk start.");
	RCSwitch sw1 = new RCSwitch(0x1L);
	try {
	    sw1.read();
	} catch (ObjectDoesntExistException e) {
	    log.error("Reading switch failed", e);
	}

	assert (sw1.getDpid() == 0x1L);
	assert (sw1.getStatus() == STATUS.ACTIVE);
	assert (sw1.getAllPortIds().size() == 2);
	for (byte[] portId : sw1.getAllPortIds()) {
	}

	log.debug("topology_walk end.");
    }

    private static void topology_delete() {
	log.debug("topology_delete start.");
	log.debug("topology_delete end.");
    }

}
