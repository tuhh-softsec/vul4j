package net.onrc.onos.datastore.topology;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import net.onrc.onos.datastore.RCObject;
import net.onrc.onos.datastore.RCTable;
import net.onrc.onos.ofcontroller.networkgraph.SwitchEvent;

import org.openflow.util.HexString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.Kryo;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import edu.stanford.ramcloud.JRamCloud;
import edu.stanford.ramcloud.JRamCloud.ObjectDoesntExistException;
import edu.stanford.ramcloud.JRamCloud.ObjectExistsException;
import edu.stanford.ramcloud.JRamCloud.WrongVersionException;
import net.onrc.onos.datastore.RCProtos.SwitchProperty;

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

    // must not re-order enum members, ordinal will be sent over wire
    public enum STATUS {
	INACTIVE, ACTIVE;
    }

    private final Long dpid;
    private STATUS status;

    public static byte[] getSwitchID(Long dpid) {
        return SwitchEvent.getSwitchID(dpid);
    }

    public static StringBuilder keysToSB(Collection<byte[]> keys) {
	StringBuilder sb = new StringBuilder();
	sb.append("[");
	boolean hasWritten = false;
	for (byte[] key : keys) {
	    if (hasWritten) {
		sb.append(", ");
	    }
	    sb.append(keyToString(key));
	    hasWritten = true;
	}
	sb.append("]");
	return sb;
    }

    public static String keyToString(byte[] key) {
	// For debug log
	return "S" + HexString.toHexString(getDpidFromKey(key));
    }

    public static long getDpidFromKey(byte[] key) {
	return getDpidFromKey(ByteBuffer.wrap(key));
    }

    public static long getDpidFromKey(ByteBuffer keyBuf) {
	if (keyBuf.getChar() != 'S') {
	    throw new IllegalArgumentException("Invalid Switch key");
	}
	return keyBuf.getLong();
    }

    // FIXME specify DPID here, or Should caller specify the key it self?
    // In other words, should layer above have the control of the ID?
    public RCSwitch(Long dpid) {
	super(RCTable.getTable(GLOBAL_SWITCH_TABLE_NAME), getSwitchID(dpid));

	this.dpid = dpid;
	this.status = STATUS.INACTIVE;
    }

    /**
     * Get an instance from Key.
     *
     * @note You need to call `read()` to get the DB content.
     * @param key
     * @return RCSwitch instance
     */
    public static <SW extends RCObject> SW createFromKey(byte[] key) {
	@SuppressWarnings("unchecked")
	SW sw = (SW) new RCSwitch(getDpidFromKey(key));
	return sw;
    }

    public static Iterable<RCSwitch> getAllSwitches() {
	return new SwitchEnumerator();
    }

    public static class SwitchEnumerator implements Iterable<RCSwitch> {

	@Override
	public Iterator<RCSwitch> iterator() {
	    return new SwitchIterator();
	}
    }

    public static class SwitchIterator extends ObjectIterator<RCSwitch> {

	public SwitchIterator() {
	    super(RCTable.getTable(GLOBAL_SWITCH_TABLE_NAME));
	}

	@Override
	public RCSwitch next() {
	    JRamCloud.Object o = enumerator.next();
	    RCSwitch e = RCSwitch.createFromKey(o.key);
	    e.setValueAndDeserialize(o.value, o.version);
	    return e;
	}
    }

    public STATUS getStatus() {
	return status;
    }

    public void setStatus(STATUS status) {
	this.status = status;
    }

    public Long getDpid() {
	return dpid;
    }

    public byte[] getId() {
	return getKey();
    }

    @Override
    public void serializeAndSetValue() {
	Map<Object, Object> map = getObjectMap();

	SwitchProperty.Builder sw = SwitchProperty.newBuilder();
	sw.setDpid(dpid);
	sw.setStatus(status.ordinal());
	
	if (!map.isEmpty()) {
	    serializeAndSetValue(switchKryo.get(), map);
	    sw.setValue(ByteString.copyFrom(this.getSerializedValue()));
	}
	
	this.value = sw.build().toByteArray();
    }

    @Override
    public Map<Object, Object> deserializeObjectFromValue() {
	SwitchProperty sw = null;
	Map<Object, Object> map = null;
	try {
	    sw = SwitchProperty.parseFrom(this.value);
	    this.value = sw.getValue().toByteArray();
	    if (this.value.length >= 1) {
		map = deserializeObjectFromValue(switchKryo.get());
	    } else {
		map = new HashMap<>();
	    }
	    this.status = STATUS.values()[sw.getStatus()];
	    return map;
	} catch (InvalidProtocolBufferException e) {
	    log.error("{" + toString() + "}: Read Switch: ", e);
	    return null;
	}
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

	// update 0x1
	swRead.setStatus(STATUS.INACTIVE);
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
	try {
	    swRead2.delete();
	} catch (ObjectDoesntExistException | WrongVersionException e) {
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

    @Deprecated
    private static void topology_setup() {
	log.debug("topology_setup start.");

	// d1 - s1p1 - s1 - s1p2 - s2p1 - s2 - s2p2

	RCSwitch sw1 = new RCSwitch(0x1L);
	sw1.setStatus(STATUS.ACTIVE);
	try {
	    sw1.create();
	    log.debug("Create {}", sw1);
	} catch (ObjectExistsException e) {
	    log.error("Switch creation failed", e);
	}

	RCPort sw1p1 = new RCPort(0x1L, 1L);
	sw1p1.setStatus(RCPort.STATUS.ACTIVE);
	RCPort sw1p2 = new RCPort(0x1L, 2L);
	sw1p2.setStatus(RCPort.STATUS.ACTIVE);
	try {
	    sw1p1.create();
	    log.debug("Create {}", sw1p1);
	    sw1p2.create();
	    log.debug("Create {}", sw1p2);
	} catch (ObjectExistsException e) {
	    log.error("Port creation failed", e);
	}

	try {
	    sw1.update();
	    log.debug("Update {}", sw1);
	} catch (ObjectDoesntExistException | WrongVersionException e) {
	    log.error("Switch update failed", e);
	}

	RCDevice d1 = new RCDevice(new byte[] { 0, 1, 2, 3, 4, 5, 6 });
	d1.addPortId(sw1p1.getId());

	try {
	    d1.create();
	    log.debug("Create {}", d1);
	    try {
		sw1p1.update();
	    } catch (ObjectDoesntExistException | WrongVersionException e) {
		log.error("Link update failed", e);
	    }
	    log.debug("Create {}", sw1p1);
	} catch (ObjectExistsException e) {
	    log.error("Device creation failed", e);
	}

	RCSwitch sw2 = new RCSwitch(0x2L);
	sw2.setStatus(STATUS.ACTIVE);
	RCPort sw2p1 = new RCPort(0x2L, 1L);
	sw2p1.setStatus(RCPort.STATUS.ACTIVE);
	RCPort sw2p2 = new RCPort(0x2L, 2L);
	sw2p2.setStatus(RCPort.STATUS.ACTIVE);

	RCDevice d2 = new RCDevice(new byte[] { 6, 5, 4, 3, 2, 1, 0 });
	d2.addPortId(sw2p2.getId());

	// XXX Collection created by Arrays.asList needs to be stored, so that
	// which operation failed
	Collection<WriteOp> groupOp = Arrays.asList(
		RCObject.WriteOp.Create(sw2), RCObject.WriteOp.Create(sw2p1),
		RCObject.WriteOp.Create(sw2p2), RCObject.WriteOp.Create(d2));
	boolean failed = RCObject.multiWrite(groupOp);
	if (failed) {
	    log.error("Some of Switch/Port/Device creation failed");
	    for ( WriteOp op : groupOp ) {
		log.debug("{} - Result:{}", op.getObject(), op.getStatus() );
	    }
	} else {
	    log.debug("Create {} Version:{}", sw2, sw2.getVersion());
	    log.debug("Create {} Version:{}", sw2p1, sw2p1.getVersion());
	    log.debug("Create {} Version:{}", sw2p2, sw2p2.getVersion());
	    log.debug("Create {} Version:{}", d2, d2.getVersion());
	}

	RCLink l1 = new RCLink(0x1L, 2L, 0x2L, 1L);
	l1.setStatus(RCLink.STATUS.ACTIVE);

	try {
	    l1.create();
	    log.debug("Create {}", l1);
	    try {
		sw1p2.update();
		log.debug("Update {}", sw1p2);
		sw2p1.update();
		log.debug("Update {}", sw2p1);
	    } catch (ObjectDoesntExistException | WrongVersionException e) {
		log.error("Port update failed", e);
	    }
	} catch (ObjectExistsException e) {
	    log.error("Link creation failed", e);
	}

	log.debug("topology_setup end.");
    }

    @Deprecated
    private static void topology_walk() {
	log.debug("topology_walk start.");

	Iterable<RCSwitch> swIt = RCSwitch.getAllSwitches();
	log.debug("Enumerating Switches start");
	for (RCSwitch sw : swIt) {
	    log.debug("{}", sw);
	}
	log.debug("Enumerating Switches end");

	RCSwitch sw1 = new RCSwitch(0x1L);
	try {
	    sw1.read();
	    log.debug("{}", sw1);
	} catch (ObjectDoesntExistException e) {
	    log.error("Reading switch failed", e);
	}

	assert (sw1.getDpid() == 0x1L);
	assert (sw1.getStatus() == STATUS.ACTIVE);
	for (RCPort port : RCPort.getAllPorts()) {
	    if (port.getDpid() != 0x1L) {
		continue;
	    }
	    log.debug("{}", port);

	    for (RCDevice device : RCDevice.getAllDevices()) {
		if (!device.getAllPortIds().contains(port.getId())) {
		    continue;
		}
		log.debug("{} - PortIDs:{}", device,
			RCPort.keysToSB(device.getAllPortIds()));
	    }

	    for (RCLink link : RCLink.getAllLinks()) {
		if (!Arrays.equals(link.getSrc().getPortID(), port.getId())) {
		    continue;
		}
		log.debug("Link {}", link);
	    }
	}

	RCSwitch sw2 = new RCSwitch(0x2L);
	try {
	    sw2.read();
	    log.debug("{}", sw2);
	} catch (ObjectDoesntExistException e) {
	    log.error("Reading switch failed", e);
	}

	assert (sw2.getDpid() == 0x2L);
	assert (sw2.getStatus() == STATUS.ACTIVE);
	for (RCPort port : RCPort.getAllPorts()) {
	    if (port.getDpid() != 0x2L) {
		continue;
	    }
	    log.debug("{}", port);

	    for (RCDevice device : RCDevice.getAllDevices()) {
		if (!device.getAllPortIds().contains(port.getId())) {
		    continue;
		}
		log.debug("{} - PortIDs:{}", device,
			RCPort.keysToSB(device.getAllPortIds()));
	    }

	    for (RCLink link : RCLink.getAllLinks()) {
		if (!Arrays.equals(link.getSrc().getPortID(), port.getId())) {
		    continue;
		}
		log.debug("Link {}", link);
	    }

	}

	log.debug("topology_walk end.");
    }

    @Deprecated
    private static void topology_delete() {
	log.debug("topology_delete start.");

	for (RCSwitch sw : RCSwitch.getAllSwitches()) {
	    try {
		sw.read();
		sw.delete();
	    } catch (ObjectDoesntExistException | WrongVersionException e) {
		log.debug("Delete Switch Failed", e);
	    }
	}

	for (RCPort p : RCPort.getAllPorts()) {
	    try {
		p.read();
		p.delete();
	    } catch (ObjectDoesntExistException | WrongVersionException e) {
		log.debug("Delete Port Failed", e);
	    }
	}

	for (RCDevice d : RCDevice.getAllDevices()) {
	    try {
		d.read();
		d.delete();
	    } catch (ObjectDoesntExistException | WrongVersionException e) {
		log.debug("Delete Device Failed", e);
	    }
	}

	for (RCLink l : RCLink.getAllLinks()) {
	    try {
		l.read();
		l.delete();
	    } catch (ObjectDoesntExistException | WrongVersionException e) {
		log.debug("Delete Link Failed", e);
	    }
	}

	log.debug("topology_delete end.");
    }

}
