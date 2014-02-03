package net.onrc.onos.datastore.topology;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.Kryo;

import edu.stanford.ramcloud.JRamCloud;
import net.onrc.onos.datastore.RCObject;
import net.onrc.onos.datastore.RCTable;
import net.onrc.onos.datastore.utils.ByteArrayComparator;
import net.onrc.onos.datastore.utils.ByteArrayUtil;

public class RCPort extends RCObject {
    private static final Logger log = LoggerFactory.getLogger(RCPort.class);

    private static final ThreadLocal<Kryo> portKryo = new ThreadLocal<Kryo>() {
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

    public static final String GLOBAL_PORT_TABLE_NAME = "G:Port";

    // FIXME these should be Enum or some number, not String
    private static final String PROP_DPID = "dpid";
    private static final String PROP_NUMBER = "number";
    private static final String PROP_STATUS = "status";
    private static final String PROP_LINK_IDS = "link-ids";
    private static final String PROP_DEVICE_IDS = "device-ids";

    // must not re-order enum members, ordinal will be sent over wire
    public enum STATUS {
	INACTIVE, ACTIVE;
    }

    private final Long dpid;
    private final Long number;

    private STATUS status;
    // XXX These 2 set of Ids can be removed from DataStore, if In-Memory cache
    // build the indexing info from Link.
    private TreeSet<byte[]> linkIds;
    transient boolean isLinkIdsModified;
    private TreeSet<byte[]> deviceIds;
    transient boolean isDeviceIdsModified;

    public static final int PORTID_BYTES = RCSwitch.SWITCHID_BYTES + 2 + 8;

    public static byte[] getPortID(Long dpid, Long number) {
	if (dpid == null) {
	    throw new IllegalArgumentException("dpid cannot be null");
	}
	if (number == null) {
	    throw new IllegalArgumentException("number cannot be null");
	}
	return ByteBuffer.allocate(PORTID_BYTES).putChar('S').putLong(dpid)
	        .putChar('P').putLong(number).array();
    }

    public static long[] getPortPairFromKey(byte[] key) {
	return getPortPairFromKey(ByteBuffer.wrap(key));

    }

    public static long[] getPortPairFromKey(ByteBuffer keyBuf) {
	long[] pair = new long[2];
	if (keyBuf.getChar() != 'S') {
	    throw new IllegalArgumentException("Invalid Port key:" + keyBuf
		    + " "
		    + ByteArrayUtil.toHexStringBuffer(keyBuf.array(), ":"));
	}
	pair[0] = keyBuf.getLong();
	if (keyBuf.getChar() != 'P') {
	    throw new IllegalArgumentException("Invalid Port key:" + keyBuf
		    + " "
		    + ByteArrayUtil.toHexStringBuffer(keyBuf.array(), ":"));
	}
	pair[1] = keyBuf.getLong();
	return pair;

    }

    public static long getDpidFromKey(byte[] key) {
	return getPortPairFromKey(key)[0];
    }

    public static long getNumberFromKey(byte[] key) {
	return getPortPairFromKey(key)[1];
    }

    // FIXME specify DPID,number here, or Should caller specify the key it self?
    // In other words, should layer above have the control of the ID?
    public RCPort(Long dpid, Long number) {
	super(RCTable.getTable(GLOBAL_PORT_TABLE_NAME), getPortID(dpid, number));

	// TODO Auto-generated constructor stub

	this.dpid = dpid;
	this.number = number;
	this.status = STATUS.INACTIVE;
	this.linkIds = new TreeSet<>(ByteArrayComparator.BYTEARRAY_COMPARATOR);
	this.isLinkIdsModified = true;
	this.deviceIds = new TreeSet<>(ByteArrayComparator.BYTEARRAY_COMPARATOR);
	this.isDeviceIdsModified = true;
    }

    /**
     * Get an instance from Key.
     *
     * @note You need to call `read()` to get the DB content.
     * @param key
     * @return RCPort instance
     */
    public static <P extends RCObject> P createFromKey(byte[] key) {
	long[] pair = getPortPairFromKey(key);
	@SuppressWarnings("unchecked")
	P p = (P) new RCPort(pair[0], pair[1]);
	return p;
    }

    public static Iterable<RCPort> getAllPorts() {
	return new PortEnumerator();
    }

    public static class PortEnumerator implements Iterable<RCPort> {

	@Override
	public Iterator<RCPort> iterator() {
	    return new PortIterator();
	}
    }

    public static class PortIterator extends ObjectIterator<RCPort> {

	public PortIterator() {
	    super(RCTable.getTable(GLOBAL_PORT_TABLE_NAME));
	}

	@Override
	public RCPort next() {
	    JRamCloud.Object o = enumerator.next();
	    RCPort e = RCPort.createFromKey(o.key);
	    e.setValueAndDeserialize(o.value, o.version);
	    return e;
	}
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

    public Long getNumber() {
	return number;
    }

    public byte[] getId() {
	return getKey();
    }

    public void addLinkId(byte[] linkId) {
	isLinkIdsModified |= linkIds.add(linkId);
    }

    public void removeLinkId(byte[] linkId) {
	isLinkIdsModified |= linkIds.remove(linkId);
    }

    public void emptyLinkIds() {
	linkIds.clear();
	isLinkIdsModified = true;
    }

    public void addAllToLinkIds(Collection<byte[]> linkIds) {
	isLinkIdsModified |= this.linkIds.addAll(linkIds);
    }

    /**
     *
     * @return Unmodifiable Set view of all the LinkIds;
     */
    public Set<byte[]> getAllLinkIds() {
	return Collections.unmodifiableSet(linkIds);
    }

    public void addDeviceId(byte[] deviceId) {
	isDeviceIdsModified |= deviceIds.add(deviceId);
    }

    public void removeDeviceId(byte[] deviceId) {
	isDeviceIdsModified |= deviceIds.remove(deviceId);
    }

    public void emptyDeviceIds() {
	deviceIds.clear();
	isDeviceIdsModified = true;
    }

    public void addAllToDeviceIds(Collection<byte[]> deviceIds) {
	isDeviceIdsModified |= this.deviceIds.addAll(deviceIds);
    }

    /**
     *
     * @return Unmodifiable Set view of all the LinkIds;
     */
    public Set<byte[]> getAllDeviceIds() {
	return Collections.unmodifiableSet(deviceIds);
    }

    @Override
    public void serializeAndSetValue() {
	Map<Object, Object> map = getObjectMap();

	map.put(PROP_DPID, this.dpid);
	map.put(PROP_NUMBER, this.number);
	if (isLinkIdsModified) {
	    byte[] linkIdArray[] = new byte[linkIds.size()][];
	    map.put(PROP_LINK_IDS, linkIds.toArray(linkIdArray));
	    isLinkIdsModified = false;
	}
	if (isDeviceIdsModified) {
	    byte[] deviceIdArray[] = new byte[deviceIds.size()][];
	    map.put(PROP_DEVICE_IDS, deviceIds.toArray(deviceIdArray));
	    isDeviceIdsModified = false;
	}
	if (log.isWarnEnabled() && (linkIds.size() * deviceIds.size()) != 0) {
	    log.warn("Either #LinkIds:{} or #DeviceIds:{} is expected to be 0",
		    linkIds.size(), deviceIds.size());
	}

	serializeAndSetValue(portKryo.get(), map);
    }

    @Override
    public Map<Object, Object> deserializeObjectFromValue() {
	Map<Object, Object> map = deserializeObjectFromValue(portKryo.get());

	this.status = (STATUS) map.get(PROP_STATUS);

	if (this.linkIds == null) {
	    this.linkIds = new TreeSet<>(
		    ByteArrayComparator.BYTEARRAY_COMPARATOR);
	}
	byte[] linkIdArray[] = (byte[][]) map.get(PROP_LINK_IDS);
	if (linkIdArray != null) {
	    this.linkIds.clear();
	    this.linkIds.addAll(Arrays.asList(linkIdArray));
	    isLinkIdsModified = false;
	} else {
	    // trigger write on next serialize
	    isLinkIdsModified = true;
	}

	if (this.deviceIds == null) {
	    this.deviceIds = new TreeSet<>(
		    ByteArrayComparator.BYTEARRAY_COMPARATOR);
	}
	byte[] deviceIdArray[] = (byte[][]) map.get(PROP_DEVICE_IDS);
	if (deviceIdArray != null) {
	    this.deviceIds.clear();
	    this.deviceIds.addAll(Arrays.asList(deviceIdArray));
	    isDeviceIdsModified = false;
	} else {
	    // trigger write on next serialize
	    isDeviceIdsModified = true;
	}

	if (log.isWarnEnabled() && (linkIds.size() * deviceIds.size()) != 0) {
	    log.warn("Either #LinkIds:{} or #DeviceIds:{} is expected to be 0",
		    linkIds.size(), deviceIds.size());
	}

	return map;
    }

    @Override
    public String toString() {
	// TODO OUTPUT ALL?
	return "[RCPort 0x" + Long.toHexString(dpid) + "@" + number
	        + " STATUS:" + status + "]";
    }

    public static void main(String[] args) {
	// TODO Auto-generated method stub

    }

}
