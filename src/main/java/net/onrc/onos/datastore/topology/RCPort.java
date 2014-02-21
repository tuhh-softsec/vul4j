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

import org.openflow.util.HexString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.Kryo;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import edu.stanford.ramcloud.JRamCloud;
import net.onrc.onos.datastore.RCProtos.PortProperty;
import net.onrc.onos.datastore.RCObject;
import net.onrc.onos.datastore.RCTable;
import net.onrc.onos.datastore.utils.ByteArrayComparator;
import net.onrc.onos.datastore.utils.ByteArrayUtil;
import net.onrc.onos.ofcontroller.networkgraph.PortEvent;

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
    @Deprecated
    private TreeSet<byte[]> linkIds;
    @Deprecated
    transient boolean isLinkIdsModified;
    @Deprecated
    private TreeSet<byte[]> deviceIds;
    @Deprecated
    transient boolean isDeviceIdsModified;

    public static byte[] getPortID(Long dpid, Long number) {
        return PortEvent.getPortID(dpid, number);
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
	long[] pair = getPortPairFromKey(key);
	return "S" + HexString.toHexString(pair[0]) + "P" + pair[1];
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

    @Deprecated
    public void addLinkId(byte[] linkId) {
	isLinkIdsModified |= linkIds.add(linkId);
    }

    @Deprecated
    public void removeLinkId(byte[] linkId) {
	isLinkIdsModified |= linkIds.remove(linkId);
    }

    @Deprecated
    public void emptyLinkIds() {
	linkIds.clear();
	isLinkIdsModified = true;
    }

    @Deprecated
    public void addAllToLinkIds(Collection<byte[]> linkIds) {
	isLinkIdsModified |= this.linkIds.addAll(linkIds);
    }

    /**
     *
     * @return Unmodifiable Set view of all the LinkIds;
     */
    @Deprecated
    public Set<byte[]> getAllLinkIds() {
	return Collections.unmodifiableSet(linkIds);
    }

    @Deprecated
    public void addDeviceId(byte[] deviceId) {
	isDeviceIdsModified |= deviceIds.add(deviceId);
    }

    @Deprecated
    public void removeDeviceId(byte[] deviceId) {
	isDeviceIdsModified |= deviceIds.remove(deviceId);
    }

    @Deprecated
    public void emptyDeviceIds() {
	deviceIds.clear();
	isDeviceIdsModified = true;
    }

    @Deprecated
    public void addAllToDeviceIds(Collection<byte[]> deviceIds) {
	isDeviceIdsModified |= this.deviceIds.addAll(deviceIds);
    }

    /**
     *
     * @return Unmodifiable Set view of all the LinkIds;
     */
    @Deprecated
    public Set<byte[]> getAllDeviceIds() {
	return Collections.unmodifiableSet(deviceIds);
    }

    @Override
    public void serializeAndSetValue() {	
	Map<Object, Object> map = getObjectMap();
	
	PortProperty.Builder port = PortProperty.newBuilder();
	port.setDpid(dpid);
	port.setNumber(number);
	port.setStatus(status.ordinal());
	
	if (!map.isEmpty()) {
	    serializeAndSetValue(portKryo.get(), map);
	    port.setValue(ByteString.copyFrom(this.getSerializedValue()));
	}
	
	this.value = port.build().toByteArray();
    }

    @Override
    public Map<Object, Object> deserializeObjectFromValue() {
	PortProperty port = null;
	Map<Object, Object> map = null;
	try {
	    port = PortProperty.parseFrom(this.value);
	    this.value = port.getValue().toByteArray();
	    if (this.value.length >= 1) {
		map = deserializeObjectFromValue(portKryo.get());
	    } else {
		map = new HashMap<>();
	    }
	    this.status = STATUS.values()[port.getStatus()];
	    return map;
	} catch (InvalidProtocolBufferException e) {
	    log.error("{" + toString() + "}: Read Port: ", e);
	    return null;
	}	
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
