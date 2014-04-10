package net.onrc.onos.api.packet;

import java.util.List;

import net.floodlightcontroller.core.module.IFloodlightService;
import net.onrc.onos.core.packet.Ethernet;
import net.onrc.onos.core.topology.Port;

public interface IPacketService extends IFloodlightService {
    // packet ins
    public void registerPacketListener(IPacketListener listener);

    // packet outs
    public void sendPacket(Port port, Ethernet eth);

    public void sendPacket(List<Port> ports, Ethernet eth);

    public void broadcastPacket(Ethernet eth);

    public void broadcastPacket(Ethernet eth, Port inPort);
}
