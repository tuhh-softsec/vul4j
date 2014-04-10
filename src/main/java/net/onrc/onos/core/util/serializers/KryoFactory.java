package net.onrc.onos.core.util.serializers;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import net.floodlightcontroller.util.MACAddress;
import net.onrc.onos.apps.proxyarp.ArpReplyNotification;
import net.onrc.onos.core.devicemanager.OnosDevice;
import net.onrc.onos.core.intent.ConstrainedShortestPathIntent;
import net.onrc.onos.core.intent.ErrorIntent;
import net.onrc.onos.core.intent.Intent;
import net.onrc.onos.core.intent.IntentOperation;
import net.onrc.onos.core.intent.IntentOperationList;
import net.onrc.onos.core.intent.PathIntent;
import net.onrc.onos.core.intent.ShortestPathIntent;
import net.onrc.onos.core.intent.runtime.IntentStateList;
import net.onrc.onos.core.packetservice.BroadcastPacketOutNotification;
import net.onrc.onos.core.packetservice.SinglePacketOutNotification;
import net.onrc.onos.core.topology.DeviceEvent;
import net.onrc.onos.core.topology.LinkEvent;
import net.onrc.onos.core.topology.Path;
import net.onrc.onos.core.topology.PortEvent;
import net.onrc.onos.core.topology.SwitchEvent;
import net.onrc.onos.core.topology.TopologyEvent;
import net.onrc.onos.core.util.CallerId;
import net.onrc.onos.core.util.DataPath;
import net.onrc.onos.core.util.DataPathEndpoints;
import net.onrc.onos.core.util.Dpid;
import net.onrc.onos.core.util.FlowEntry;
import net.onrc.onos.core.util.FlowEntryAction;
import net.onrc.onos.core.util.FlowEntryActions;
import net.onrc.onos.core.util.FlowEntryErrorState;
import net.onrc.onos.core.util.FlowEntryId;
import net.onrc.onos.core.util.FlowEntryMatch;
import net.onrc.onos.core.util.FlowEntrySwitchState;
import net.onrc.onos.core.util.FlowEntryUserState;
import net.onrc.onos.core.util.FlowId;
import net.onrc.onos.core.util.FlowPath;
import net.onrc.onos.core.util.FlowPathFlags;
import net.onrc.onos.core.util.FlowPathType;
import net.onrc.onos.core.util.FlowPathUserState;
import net.onrc.onos.core.util.IPv4;
import net.onrc.onos.core.util.IPv4Net;
import net.onrc.onos.core.util.IPv6;
import net.onrc.onos.core.util.IPv6Net;
import net.onrc.onos.core.util.Port;
import net.onrc.onos.core.util.Switch;
// import net.onrc.onos.core.util.SwitchPort;


import com.esotericsoftware.kryo.Kryo;

/**
 * Class factory for allocating Kryo instances for
 * serialization/deserialization of classes.
 */
public class KryoFactory {
    private static final int DEFAULT_PREALLOCATIONS = 100;
    private ArrayList<Kryo> kryoList = new ArrayList<Kryo>();

    /**
     * Default constructor.
     * <p/>
     * Preallocates {@code DEFAULT_PREALLOCATIONS} Kryo instances.
     */
    public KryoFactory() {
        this(DEFAULT_PREALLOCATIONS);
    }

    /**
     * Constructor to explicitly specify number of Kryo instances to pool.
     *
     * @param initialCapacity number of Kryo instance to preallocate
     */
    public KryoFactory(final int initialCapacity) {
        // Preallocate
        kryoList.ensureCapacity(initialCapacity);
        for (int i = 0; i < initialCapacity; i++) {
            Kryo kryo = newKryoImpl();
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
     *                   otherwise delete the provided object.
     * @return a new Kryo object if needed, otherwise null.
     */
    private synchronized Kryo newDeleteKryo(Kryo deleteKryo) {
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
        //
        // WARNING: Order of register() calls affects serialized bytes.
        //  - Do no insert new entry in the middle, always add to the end.
        //  - Do not simply remove existing entry
        //

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
        kryo.register(FlowPathType.class);
        kryo.register(FlowPathUserState.class);
        kryo.register(IPv4.class);
        kryo.register(IPv4Net.class);
        kryo.register(IPv6.class);
        kryo.register(IPv6Net.class);
        kryo.register(byte[].class);
        kryo.register(MACAddress.class);
        kryo.register(Port.class);
        kryo.register(Switch.class);
        // kryo.register(SwitchPort.class);

        // New data model-related classes
        kryo.register(DeviceEvent.class);
        kryo.register(InetAddress.class);
        kryo.register(LinkEvent.class);
        kryo.register(PortEvent.class);
        kryo.register(PortEvent.SwitchPort.class);
        kryo.register(SwitchEvent.class);
        kryo.register(TopologyEvent.class);

        // Intent-related classes
        kryo.register(Path.class);
        kryo.register(Intent.class);
        kryo.register(Intent.IntentState.class);
        kryo.register(PathIntent.class);
        kryo.register(ShortestPathIntent.class);
        kryo.register(ConstrainedShortestPathIntent.class);
        kryo.register(ErrorIntent.class);
        kryo.register(ErrorIntent.ErrorType.class);
        kryo.register(IntentOperation.class);
        kryo.register(IntentOperation.Operator.class);
        kryo.register(IntentOperationList.class);
        kryo.register(IntentStateList.class);

        // Device-related classes
        kryo.register(HashSet.class);
        kryo.register(Inet4Address.class);
        kryo.register(OnosDevice.class);
        kryo.register(Date.class);

        // ProxyArp-related classes
        kryo.register(BroadcastPacketOutNotification.class);
        kryo.register(SinglePacketOutNotification.class);
        kryo.register(ArpReplyNotification.class);

        return kryo;
    }
}
