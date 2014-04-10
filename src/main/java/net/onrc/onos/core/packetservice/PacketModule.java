package net.onrc.onos.core.packetservice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.onrc.onos.api.packet.IPacketListener;
import net.onrc.onos.api.packet.IPacketService;
import net.onrc.onos.core.packet.Ethernet;
import net.onrc.onos.core.topology.INetworkGraphService;
import net.onrc.onos.core.topology.NetworkGraph;
import net.onrc.onos.core.topology.Port;
import net.onrc.onos.core.topology.Switch;

import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFPacketIn;
import org.openflow.protocol.OFType;

public class PacketModule implements IOFMessageListener, IPacketService,
                                     IFloodlightModule {

    private final CopyOnWriteArrayList<IPacketListener> listeners;

    private IFloodlightProviderService floodlightProvider;
    private NetworkGraph networkGraph;

    public PacketModule() {
        listeners = new CopyOnWriteArrayList<>();
    }

    @Override
    public void registerPacketListener(IPacketListener listener) {
        listeners.addIfAbsent(listener);
    }

    @Override
    public void sendPacket(Port port, Ethernet eth) {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendPacket(List<Port> ports, Ethernet eth) {
        // TODO Auto-generated method stub

    }

    @Override
    public void broadcastPacket(Ethernet eth) {
        // TODO Auto-generated method stub

    }

    @Override
    public void broadcastPacket(Ethernet eth, Port inPort) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isCallbackOrderingPrereq(OFType type, String name) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isCallbackOrderingPostreq(OFType type, String name) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Command receive(IOFSwitch sw, OFMessage msg,
            FloodlightContext cntx) {
        if (!(msg instanceof OFPacketIn)) {
            return Command.CONTINUE;
        }

        OFPacketIn pi = (OFPacketIn) msg;

        Ethernet eth = IFloodlightProviderService.bcStore.
                get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);

        Switch networkGraphSwitch;
        Port inPort;
        try {
            networkGraph.acquireReadLock();
            networkGraphSwitch = networkGraph.getSwitch(sw.getId());
            inPort = networkGraph.getPort(sw.getId(), (long) pi.getInPort());
        } finally {
            networkGraph.releaseReadLock();
        }

        if (networkGraphSwitch == null || inPort == null) {
            return Command.CONTINUE;
        }

        for (IPacketListener listener : listeners) {
            listener.receive(networkGraphSwitch, inPort, eth);
        }

        return Command.CONTINUE;
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleServices() {
        List<Class<? extends IFloodlightService>> services = new ArrayList<>();
        services.add(IPacketService.class);
        return services;
    }

    @Override
    public Map<Class<? extends IFloodlightService>, IFloodlightService>
            getServiceImpls() {
        Map<Class<? extends IFloodlightService>, IFloodlightService>
                serviceImpls = new HashMap<>();
        serviceImpls.put(IPacketService.class, this);
        return serviceImpls;
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
        List<Class<? extends IFloodlightService>> dependencies = new ArrayList<>();
        dependencies.add(IFloodlightProviderService.class);
        dependencies.add(INetworkGraphService.class);
        return dependencies;
    }

    @Override
    public void init(FloodlightModuleContext context)
            throws FloodlightModuleException {
        floodlightProvider =
                context.getServiceImpl(IFloodlightProviderService.class);
        networkGraph = context.getServiceImpl(INetworkGraphService.class)
                .getNetworkGraph();
    }

    @Override
    public void startUp(FloodlightModuleContext context) {
        floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
    }
}
