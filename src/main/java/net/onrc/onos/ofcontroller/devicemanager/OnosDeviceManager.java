package net.onrc.onos.ofcontroller.devicemanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
import net.onrc.onos.datagrid.IDatagridService;
import net.onrc.onos.ofcontroller.core.IDeviceStorage;
import net.onrc.onos.ofcontroller.core.internal.DeviceStorageImpl;
import net.onrc.onos.ofcontroller.topology.TopologyElement;
import net.onrc.onos.ofcontroller.topology.TopologyElement.Type;

import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFPacketIn;
import org.openflow.protocol.OFType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OnosDeviceManager implements IFloodlightModule, IOFMessageListener,
										IOnosDeviceService {
	protected final static Logger log = LoggerFactory.getLogger(OnosDeviceManager.class);
	private static final int CLEANUP_SECOND = 60*60;
	private static final int AGEING_MILLSEC = 60*60*1000;
	
	private IDeviceStorage deviceStorage;
	private IFloodlightProviderService floodlightProvider;
	private final static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	
	private IDatagridService datagrid;
	private Map<Long, OnosDevice> mapDevice = new ConcurrentHashMap<Long, OnosDevice>();
	
    public enum OnosDeviceUpdateType {
        ADD, DELETE, UPDATE;
    }
	
	private class OnosDeviceUpdate implements IUpdate {
		private OnosDevice device;
		private OnosDeviceUpdateType type;
		
		public OnosDeviceUpdate(OnosDevice device, OnosDeviceUpdateType type) {
			this.device = device;
			this.type = type;
		}
		
		@Override
		public void dispatch() {
			if(type == OnosDeviceUpdateType.ADD) {
				deviceStorage.addOnosDevice(device);		
			} else if (type == OnosDeviceUpdateType.DELETE){
				deviceStorage.deleteOnosDevice(device);		
			}
		}
	}
	
	@Override
	public String getName() {
		return "onosdevicemanager";
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		// We want link discovery to consume LLDP first otherwise we'll
		// end up reading bad device info from LLDP packets
		return type == OFType.PACKET_IN && "linkdiscovery".equals(name);
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
        long dpid =sw.getId();
        short portId = pi.getInPort();
        Long mac = eth.getSourceMAC().toLong();

        OnosDevice srcDevice =
                getSourceDeviceFromPacket(eth, dpid, portId);

        if (srcDevice == null){
        	return Command.STOP;
        }
        
        //We check if it is the same device in datagrid to suppress the device update
        OnosDevice exDev = null;
        if((exDev = mapDevice.get(mac)) != null ){
	    	if(exDev.equals(srcDevice)) {
	    		//There is the same existing device. Update only ActiveSince time.
	        	exDev.setLastSeenTimestamp(new Date());
	        	if(log.isTraceEnabled()) {
			        log.debug("In the datagrid, there is the same device."
							+ "Only update last seen time. dpid {}, port {}, mac {}, ip {}, lastSeenTime {}",
			        		dpid, portId, srcDevice.getMacAddress(), srcDevice.getIpv4Address(), srcDevice.getLastSeenTimestamp().getTime());
	        	}
		        return Command.CONTINUE;
	    	} else if (srcDevice.getIpv4Address() == null && 
	    			exDev.getSwitchDPID() == srcDevice.getSwitchDPID() &&
	    			exDev.getSwitchPort() == srcDevice.getSwitchPort() &&
	    			exDev.getVlan() == srcDevice.getVlan()) {
	    		//Device attachment point and mac address are the same 
	    		//but the packet does not have an ip address.
	        	exDev.setLastSeenTimestamp(new Date());
	        	if(log.isTraceEnabled()) {
		        	log.debug("In the datagrid, there is the same device with no ip."
							+ "Keep ip and update last seen time. dpid {}, port {}, mac {}, ip {}, lastSeenTime {}",
							dpid, portId, srcDevice.getMacAddress(), srcDevice.getIpv4Address(), srcDevice.getLastSeenTimestamp().getTime());
	        	}
	        	return Command.CONTINUE;
	    	}
        }
        
        //If the switch port we try to attach a new device already has a link, then stop adding device
        Collection<TopologyElement> list = datagrid.getAllTopologyElements();
        for(TopologyElement elem: list) {
        	if(elem.getType() == Type.ELEMENT_LINK) {
        		if((elem.getFromPort() == portId && elem.getFromSwitch() == dpid) ||
        				(elem.getToPort() == portId && elem.getToSwitch() == dpid)) {
        			if(log.isTraceEnabled()) {
	        			log.debug("Stop adding OnosDevice {} due to there is a link to: dpid {} port {}",
	    						srcDevice.getMacAddress(), dpid, portId);
        			}
        			return Command.CONTINUE;
        		}
        	}
        }
        
        addOnosDevice(mac, srcDevice);
        
        if(log.isTraceEnabled()) {
	        log.debug("Add device info in the set. dpid {}, port {}, mac {}, ip {}, lastSeenTime {}",
	       		dpid, portId, srcDevice.getMacAddress(), srcDevice.getIpv4Address(), srcDevice.getLastSeenTimestamp().getTime());
        }
        return Command.CONTINUE;
	}
	
     //Thread to delete devices periodically. 
	 //Remove all devices from the map first and then finally delete devices from the DB.
	private class CleanDevice implements Runnable {
		@Override
		public void run() {
			log.debug("called CleanDevice");
			try{
		        	Set<OnosDevice> deleteSet = new HashSet<OnosDevice>();
			        for (OnosDevice dev : mapDevice.values() ) {
			        	long now = new Date().getTime();
			        	if((now - dev.getLastSeenTimestamp().getTime() > AGEING_MILLSEC)) {
			        		if(log.isTraceEnabled()) {
			        			log.debug("Remove device info in the datagrid. dpid {}, port {}, mac {}, ip {}, lastSeenTime {}, diff {}",
				        				dev.getSwitchDPID(), dev.getSwitchPort(), dev.getMacAddress(), dev.getIpv4Address(),
				        				dev.getLastSeenTimestamp().getTime(), now - dev.getLastSeenTimestamp().getTime());
			        		}
			        		deleteSet.add(dev);
			        	}
			        }
			     
			        for(OnosDevice dev : deleteSet) {
			        	deleteOnosDevice(dev);        	
			        }
			 } catch(Exception e) {
		    	 log.error("Error:", e);
		     }
		}
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
		executor.scheduleAtFixedRate(new CleanDevice(), 30 ,CLEANUP_SECOND, TimeUnit.SECONDS);
		deviceStorage = new DeviceStorageImpl();
		deviceStorage.init("","");
		
		datagrid = context.getServiceImpl(IDatagridService.class);
	}

	@Override
	public void startUp(FloodlightModuleContext context) {
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
		datagrid.registerMapDeviceEventHandler(new MapDevListener());
	}

	@Override
	public void deleteOnosDevice(OnosDevice dev) {
		datagrid.sendNotificationDeviceDeleted(dev);
		floodlightProvider.publishUpdate(new OnosDeviceUpdate(dev, OnosDeviceUpdateType.DELETE));
	}
	
	@Override
	public void addOnosDevice(Long mac, OnosDevice dev) {
        datagrid.sendNotificationDeviceAdded(mac, dev);
        floodlightProvider.publishUpdate(new OnosDeviceUpdate(dev, OnosDeviceUpdateType.ADD));
	}
	
	//This is listener for datagrid mapDevice change.
    class MapDevListener implements IDeviceEventHandler {

		@Override
		public void addDeviceEvent(Long mac, OnosDevice dev) {	
			mapDevice.put(mac, dev);
			log.debug("addDeviceMap: device mac {}", mac);
		}

		@Override
		public void deleteDeviceEvent(Long mac, OnosDevice dev) {	
			mapDevice.remove(mac);
			log.debug("deleteDeviceMap: device mac {}", mac);	
		}

		@Override
		public void updateDeviceEvent(Long mac, OnosDevice dev) {	
			mapDevice.put(mac, dev);
			log.debug("updateDeviceMap: device mac {}", mac);
		}
    }
}
