package net.onrc.onos.api.packet;

import net.onrc.onos.core.packet.Ethernet;
import net.onrc.onos.core.topology.Port;
import net.onrc.onos.core.topology.Switch;

public interface IPacketListener {
    public void receive(Switch sw, Port inPort, Ethernet payload);
}
