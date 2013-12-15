package net.onrc.onos.ofcontroller.devicemanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.IUpdate;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.packet.ARP;
import net.floodlightcontroller.packet.DHCP;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.packet.UDP;
import net.floodlightcontroller.util.MACAddress;
import net.onrc.onos.ofcontroller.core.IDeviceStorage;
import net.onrc.onos.ofcontroller.core.internal.DeviceStorageImpl;

import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFPacketIn;
import org.openflow.protocol.OFType;

public class OnosDeviceManager implements IFloodlightModule, IOFMessageListener,
										IOnosDeviceService {
	private IDeviceStorage deviceStorage;
	
	private IFloodlightProviderService floodlightProvider;

	public class OnosDeviceUpdate implements IUpdate {
		private OnosDevice device;
		
		public OnosDeviceUpdate(OnosDevice device) {
			this.device = device;
		}
		
		@Override
		public void dispatch() {
			deviceStorage.addOnosDevice(device);
		}
	}
	
	@Override
	public String getName() {
		return "onosdevicemanager";
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		return type == OFType.PACKET_IN && 
				("proxyarpmanager".equals(name) || "onosforwarding".equals(name));
	}

	@Override
	public Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
		if (msg.getType().equals(OFType.PACKET_IN)) {
			OFPacketIn pi = (OFPacketIn) msg;
			
			Ethernet eth = IFloodlightProviderService.bcStore.
					get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
			
			return processPacketIn(sw, pi, eth);
		}
		
		return Command.CONTINUE;
	}
	
	private Command processPacketIn(IOFSwitch sw, OFPacketIn pi, Ethernet eth) {
		
        // Extract source entity information
        OnosDevice srcDevice =
                getSourceDeviceFromPacket(eth, sw.getId(), pi.getInPort());
        if (srcDevice == null)
            return Command.STOP;
        
        floodlightProvider.publishUpdate(new OnosDeviceUpdate(srcDevice));
        
        return Command.CONTINUE;
	}
	
    /**
     * Get IP address from packet if the packet is either an ARP 
     * or a DHCP packet
     * @param eth
     * @param dlAddr
     * @return
     */
    private int getSrcNwAddr(Ethernet eth, long dlAddr) {
        if (eth.getPayload() instanceof ARP) {
            ARP arp = (ARP) eth.getPayload();
            if ((arp.getProtocolType() == ARP.PROTO_TYPE_IP) &&
                    (Ethernet.toLong(arp.getSenderHardwareAddress()) == dlAddr)) {
                return IPv4.toIPv4Address(arp.getSenderProtocolAddress());
            }
        } else if (eth.getPayload() instanceof IPv4) {
            IPv4 ipv4 = (IPv4) eth.getPayload();
            if (ipv4.getPayload() instanceof UDP) {
                UDP udp = (UDP)ipv4.getPayload();
                if (udp.getPayload() instanceof DHCP) {
                    DHCP dhcp = (DHCP)udp.getPayload();
                    if (dhcp.getOpCode() == DHCP.OPCODE_REPLY) {
                        return ipv4.getSourceAddress();
                    }
                }
            }
        }
        return 0;
    }

    /**
     * Parse an entity from an {@link Ethernet} packet.
     * @param eth the packet to parse
     * @param sw the switch on which the packet arrived
     * @param pi the original packetin
     * @return the entity from the packet
     */
    private OnosDevice getSourceDeviceFromPacket(Ethernet eth,
                                             long swdpid,
                                             short port) {
        byte[] dlAddrArr = eth.getSourceMACAddress();
        long dlAddr = Ethernet.toLong(dlAddrArr);

        // Ignore broadcast/multicast source
        if ((dlAddrArr[0] & 0x1) != 0)
            return null;

        short vlan = eth.getVlanID();
        int nwSrc = getSrcNwAddr(eth, dlAddr);
        return new OnosDevice(MACAddress.valueOf(dlAddr),
                          ((vlan >= 0) ? vlan : null),
                          ((nwSrc != 0) ? nwSrc : null),
                          swdpid,
                          port,
                          new Date());
    }

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		List<Class<? extends IFloodlightService>> services = 
				new ArrayList<Class<? extends IFloodlightService>>();
		services.add(IOnosDeviceService.class);
		return services;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		Map<Class<? extends IFloodlightService>, IFloodlightService> impls = 
				new HashMap<Class<? extends IFloodlightService>, IFloodlightService>();
		impls.put(IOnosDeviceService.class, this);
		return impls;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		List<Class<? extends IFloodlightService>> dependencies = 
				new ArrayList<Class<? extends IFloodlightService>>();
		dependencies.add(IFloodlightProviderService.class);
		return dependencies;
	}

	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException {
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
		
		deviceStorage = new DeviceStorageImpl();
		deviceStorage.init("");
	}

	@Override
	public void startUp(FloodlightModuleContext context) {
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
	}

}
