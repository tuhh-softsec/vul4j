package net.onrc.onos.ofcontroller.util.serializers;

import java.util.ArrayList;
import java.util.TreeMap;

import com.esotericsoftware.kryo2.Kryo;

import net.floodlightcontroller.util.MACAddress;

import net.onrc.onos.ofcontroller.util.*;
import net.onrc.onos.ofcontroller.topology.TopologyElement;

/**
 * Class factory for allocating Kryo instances for
 * serialization/deserialization of classes.
 */
public class KryoFactory {
    private ArrayList<Kryo> kryoList = new ArrayList<Kryo>();

    /**
     * Default constructor.
     */
    public KryoFactory() {
	Kryo kryo;
	// Preallocate
	for (int i = 0; i < 100; i++) {
	    kryo = newKryoImpl();
	    kryoList.add(kryo);
	}
    }

    /**
     * Create and initialize a new Kryo object.
     *
     * @return the created Kryo object.
     */
    public Kryo newKryo() {
	return newDeleteKryo(null);
    }

    /**
     * Delete an existing Kryo object.
     *
     * @param deleteKryo the object to delete.
     */
    public void deleteKryo(Kryo deleteKryo) {
	newDeleteKryo(deleteKryo);
    }

    /**
     * Create or delete a Kryo object.
     *
     * @param deleteKryo if null, then allocate and return a new object,
     * otherwise delete the provided object.
     * @return a new Kryo object if needed, otherwise null.
     */
    synchronized private Kryo newDeleteKryo(Kryo deleteKryo) {
	if (deleteKryo != null) {
	    // Delete an entry by moving it back to the buffer
	    kryoList.add(deleteKryo);
	    return null;
	} else {
	    Kryo kryo = null;
	    if (kryoList.isEmpty()) {
		// Preallocate
		for (int i = 0; i < 100; i++) {
		    kryo = newKryoImpl();
		    kryoList.add(kryo);
		}
	    }

	    kryo = kryoList.remove(kryoList.size() - 1);
	    return kryo;
	}
    }

    /**
     * Create and initialize a new Kryo object.
     *
     * @return the created Kryo object.
     */
    private Kryo newKryoImpl() {
	Kryo kryo = new Kryo();
	kryo.setRegistrationRequired(true);
	// kryo.setReferences(false);
	//
	kryo.register(ArrayList.class);

	// FlowPath and related classes
	kryo.register(CallerId.class);
	kryo.register(DataPath.class);
	kryo.register(DataPathEndpoints.class);
	kryo.register(Dpid.class);
	kryo.register(FlowEntryAction.class);
	kryo.register(FlowEntryAction.ActionEnqueue.class);
	kryo.register(FlowEntryAction.ActionOutput.class);
	kryo.register(FlowEntryAction.ActionSetEthernetAddr.class);
	kryo.register(FlowEntryAction.ActionSetIpToS.class);
	kryo.register(FlowEntryAction.ActionSetIPv4Addr.class);
	kryo.register(FlowEntryAction.ActionSetTcpUdpPort.class);
	kryo.register(FlowEntryAction.ActionSetVlanId.class);
	kryo.register(FlowEntryAction.ActionSetVlanPriority.class);
	kryo.register(FlowEntryAction.ActionStripVlan.class);
	kryo.register(FlowEntryAction.ActionValues.class);
	kryo.register(FlowEntryActions.class);
	kryo.register(FlowEntryErrorState.class);
	kryo.register(FlowEntryId.class);
	kryo.register(FlowEntry.class);
	kryo.register(FlowEntryMatch.class);
	kryo.register(FlowEntryMatch.Field.class);
	kryo.register(FlowEntrySwitchState.class);
	kryo.register(FlowEntryUserState.class);
	kryo.register(FlowId.class);
	kryo.register(FlowPath.class);
	kryo.register(FlowPathFlags.class);
	kryo.register(IPv4.class);
	kryo.register(IPv4Net.class);
	kryo.register(IPv6.class);
	kryo.register(IPv6Net.class);
	kryo.register(byte[].class);
	kryo.register(MACAddress.class);
	kryo.register(Port.class);
	kryo.register(Switch.class);
	kryo.register(SwitchPort.class);

	// Topology-related classes
	kryo.register(TopologyElement.class);
	kryo.register(TopologyElement.Type.class);
	kryo.register(TreeMap.class);

	return kryo;
    }
}
