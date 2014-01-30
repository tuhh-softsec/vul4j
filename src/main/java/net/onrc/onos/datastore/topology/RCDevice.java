package net.onrc.onos.datastore.topology;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.Kryo;

import net.onrc.onos.datastore.RCObject;
import net.onrc.onos.datastore.RCTable;
import net.onrc.onos.datastore.topology.RCLink.STATUS;
import net.onrc.onos.datastore.utils.ByteArrayComparator;

public class RCDevice extends RCObject {
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(RCDevice.class);

    private static final ThreadLocal<Kryo> deviceKryo = new ThreadLocal<Kryo>() {
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

    public static final String GLOBAL_DEVICE_TABLE_NAME = "G:Device";

    // FIXME these should be Enum or some number, not String
    private static final String PROP_MAC = "mac";
    private static final String PROP_PORT_IDS = "port-ids";

    private final byte[] mac;
    private TreeSet<byte[]> portIds;
    transient private boolean isPortIdsModified;

    // Assuming mac is unique cluster-wide
    public static byte[] getDeviceID(final byte[] mac) {
	return ByteBuffer.allocate(2 + mac.length).putChar('D').put(mac)
	        .array();
    }

    public static byte[] getMacFromKey(byte[] key) {
	ByteBuffer keyBuf = ByteBuffer.wrap(key);
	if (keyBuf.getChar() != 'D') {
	    throw new IllegalArgumentException("Invalid Device key");
	}
	byte[] mac = new byte[keyBuf.remaining()];
	keyBuf.get(mac);
	return mac;
    }

    public RCDevice(byte[] mac) {
	super(RCTable.getTable(GLOBAL_DEVICE_TABLE_NAME), getDeviceID(mac));

	this.mac = mac;
	this.portIds = new TreeSet<>(ByteArrayComparator.BYTEARRAY_COMPARATOR);
	this.isPortIdsModified = true;
    }

    public static RCDevice createFromKey(byte[] key) {
	return new RCDevice(getMacFromKey(key));
    }

    public byte[] getMac() {
	// TODO may need to clone() to be sure this object will be immutable.
	return mac;
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

	map.put(PROP_MAC, mac);
	if (isPortIdsModified) {
	    byte[] portIdArray[] = new byte[portIds.size()][];
	    map.put(PROP_PORT_IDS, portIds.toArray(portIdArray));
	    isPortIdsModified = false;
	}

	serializeAndSetValue(deviceKryo.get(), map);
    }

    @Override
    public Map<Object, Object> deserializeObjectFromValue() {
	Map<Object, Object> map = deserializeObjectFromValue(deviceKryo.get());

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

    public static void main(String[] args) {
	// TODO Auto-generated method stub

    }

}
