package net.onrc.onos.apps.proxyarp;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.util.MACAddress;
import net.onrc.onos.apps.bgproute.Interface;
import net.onrc.onos.core.datagrid.IDatagridService;
import net.onrc.onos.core.datagrid.IEventChannel;
import net.onrc.onos.core.datagrid.IEventChannelListener;
import net.onrc.onos.core.devicemanager.IOnosDeviceService;
import net.onrc.onos.core.flowprogrammer.IFlowPusherService;
import net.onrc.onos.core.main.config.IConfigInfoService;
import net.onrc.onos.core.util.Dpid;
import net.onrc.onos.core.util.Port;
import net.onrc.onos.core.util.SwitchPort;
import net.onrc.onos.ofcontroller.networkgraph.Device;
import net.onrc.onos.ofcontroller.networkgraph.INetworkGraphService;
import net.onrc.onos.ofcontroller.networkgraph.NetworkGraph;
import net.onrc.onos.ofcontroller.networkgraph.Switch;
import net.onrc.onos.packet.ARP;
import net.onrc.onos.packet.Ethernet;
import net.onrc.onos.packet.IPv4;

import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFPacketIn;
import org.openflow.protocol.OFPacketOut;
import org.openflow.protocol.OFPort;
import org.openflow.protocol.OFType;
import org.openflow.protocol.action.OFAction;
import org.openflow.protocol.action.OFActionOutput;
import org.openflow.util.HexString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;

public class ProxyArpManager implements IProxyArpService, IOFMessageListener,
					IFloodlightModule {
    private static final Logger log = LoggerFactory
            .getLogger(ProxyArpManager.class);

    private static final long ARP_TIMER_PERIOD = 100; // ms

    private static final int ARP_REQUEST_TIMEOUT = 2000; // ms

    private IFloodlightProviderService floodlightProvider;
    private IDatagridService datagrid;
    private IEventChannel<Long, ArpReplyNotification> arpReplyEventChannel;
    private IEventChannel<Long, BroadcastPacketOutNotification> broadcastPacketOutEventChannel;
    private IEventChannel<Long, SinglePacketOutNotification> singlePacketOutEventChannel;
    private static final String ARP_REPLY_CHANNEL_NAME = "onos.arp_reply";
    private static final String BROADCAST_PACKET_OUT_CHANNEL_NAME = "onos.broadcast_packet_out";
    private static final String SINGLE_PACKET_OUT_CHANNEL_NAME = "onos.single_packet_out";
    private ArpReplyEventHandler arpReplyEventHandler = new ArpReplyEventHandler();
    private BroadcastPacketOutEventHandler broadcastPacketOutEventHandler = new BroadcastPacketOutEventHandler();  
    private SinglePacketOutEventHandler singlePacketOutEventHandler = new SinglePacketOutEventHandler();

    private IConfigInfoService configService;
    private IRestApiService restApi;
    private IFlowPusherService flowPusher;
    
	private INetworkGraphService networkGraphService;
	private NetworkGraph networkGraph;
	private IOnosDeviceService onosDeviceService;

    private short vlan;
    private static final short NO_VLAN = 0;

    private SetMultimap<InetAddress, ArpRequest> arpRequests;

    private class BroadcastPacketOutEventHandler implements
    IEventChannelListener<Long, BroadcastPacketOutNotification> {

		@Override
		public void entryAdded(BroadcastPacketOutNotification value) {
			if(log.isTraceEnabled()) {
				log.trace("entryAdded ip{}, sw {}, port {}, packet {}", value.getTargetAddress(), value.getInSwitch(), value.getInPort(), value.packet.length);
			}
			BroadcastPacketOutNotification notification = (BroadcastPacketOutNotification) value;
			broadcastArpRequestOutMyEdge(notification.packet,
						     notification.getInSwitch(),
						     notification.getInPort());
		
			// set timestamp
			ByteBuffer buffer = ByteBuffer.allocate(4);
			buffer.putInt(notification.getTargetAddress());
			InetAddress addr = null;
			try {
				addr = InetAddress.getByAddress(buffer.array());
			} catch (UnknownHostException e) {
				log.error("Exception:", e);
			}
			
			if (addr != null) {
			    for (ArpRequest request : arpRequests.get(addr)) {
			    	request.setRequestTime();
			    }
			}			
		}
		
		@Override
		public void entryUpdated(BroadcastPacketOutNotification value) {
			log.debug("entryUpdated");
		    // TODO: For now, entryUpdated() is processed as entryAdded()
		    entryAdded(value);
		}
		
		@Override
		public void entryRemoved(BroadcastPacketOutNotification value) {
			log.debug("entryRemoved");
		    // TODO: Not implemented. Revisit when this module is refactored
		}
    }
    
    private class SinglePacketOutEventHandler implements
		IEventChannelListener<Long, SinglePacketOutNotification> {
		@Override
		public void entryAdded(SinglePacketOutNotification packetOutNotification) {
			log.debug("entryAdded");
			SinglePacketOutNotification notification =
			    (SinglePacketOutNotification) packetOutNotification;
			sendArpRequestOutPort(notification.packet,
					      notification.getOutSwitch(),
					      notification.getOutPort());
	
			// set timestamp
			ByteBuffer buffer = ByteBuffer.allocate(4);
			buffer.putInt(notification.getTargetAddress());
			InetAddress addr = null;
			try {
				addr = InetAddress.getByAddress(buffer.array());
			} catch (UnknownHostException e) {
				log.error("Exception:", e);
			}
			
			if (addr != null) {
			    for (ArpRequest request : arpRequests.get(addr)) {
			    	request.setRequestTime();
			    }
			}		
		}
	
		@Override
		public void entryUpdated(SinglePacketOutNotification packetOutNotification) {
			log.debug("entryUpdated");
		    // TODO: For now, entryUpdated() is processed as entryAdded()
		    entryAdded(packetOutNotification);
		}
	
		@Override
		public void entryRemoved(SinglePacketOutNotification packetOutNotification) {
			log.debug("entryRemoved");
		    // TODO: Not implemented. Revisit when this module is refactored
		}
    }

    private class ArpReplyEventHandler implements
	IEventChannelListener<Long, ArpReplyNotification> {
    	
	@Override
	public void entryAdded(ArpReplyNotification arpReply) {
	    log.debug("Received ARP reply notification for ip {}, mac {}",
	    		arpReply.getTargetAddress(), arpReply.getTargetMacAddress());
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putInt(arpReply.getTargetAddress());
		InetAddress addr = null;
		try {
			addr = InetAddress.getByAddress(buffer.array());
		} catch (UnknownHostException e) {
			log.error("Exception:", e);
		}
	   
		if(addr != null) {
			sendArpReplyToWaitingRequesters(addr,
				    arpReply.getTargetMacAddress());
		}
	}

	@Override
	public void entryUpdated(ArpReplyNotification arpReply) {
	    // TODO: For now, entryUpdated() is processed as entryAdded()
	    entryAdded(arpReply);
	}

	@Override
	public void entryRemoved(ArpReplyNotification arpReply) {
	    // TODO: Not implemented. Revisit when this module is refactored
	}
    }

    private static class ArpRequest {
        private final IArpRequester requester;
        private final boolean retry;
        private boolean sent = false;
        private long requestTime;

        public ArpRequest(IArpRequester requester, boolean retry) {
            this.requester = requester;
            this.retry = retry;
        }

        public ArpRequest(ArpRequest old) {
            this.requester = old.requester;
            this.retry = old.retry;
        }

        public boolean isExpired() {
            return sent
                    && ((System.currentTimeMillis() - requestTime) > ARP_REQUEST_TIMEOUT);
        }

        public boolean shouldRetry() {
            return retry;
        }

        public void dispatchReply(InetAddress ipAddress,
                MACAddress replyMacAddress) {
            requester.arpResponse(ipAddress, replyMacAddress);
        }

        public void setRequestTime() {
            this.requestTime = System.currentTimeMillis();
            this.sent = true;
        }
    }

    private class HostArpRequester implements IArpRequester {
        private final ARP arpRequest;
        private final long dpid;
        private final short port;

        public HostArpRequester(ARP arpRequest, long dpid, short port) {
            this.arpRequest = arpRequest;
            this.dpid = dpid;
            this.port = port;
        }

        @Override
        public void arpResponse(InetAddress ipAddress, MACAddress macAddress) {
            ProxyArpManager.this.sendArpReply(arpRequest, dpid, port,
                    macAddress);
        }
        
		public ARP getArpRequest() {
			return arpRequest;
		}
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleServices() {
        Collection<Class<? extends IFloodlightService>> l =
                new ArrayList<Class<? extends IFloodlightService>>();
        l.add(IProxyArpService.class);
        return l;
    }

    @Override
    public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
        Map<Class<? extends IFloodlightService>, IFloodlightService> m =
                new HashMap<Class<? extends IFloodlightService>, IFloodlightService>();
        m.put(IProxyArpService.class, this);
        return m;
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
        Collection<Class<? extends IFloodlightService>> dependencies =
                new ArrayList<Class<? extends IFloodlightService>>();
        dependencies.add(IFloodlightProviderService.class);
        dependencies.add(IRestApiService.class);
        dependencies.add(IDatagridService.class);
        dependencies.add(IConfigInfoService.class);
        dependencies.add(IFlowPusherService.class);
        dependencies.add(INetworkGraphService.class);
        dependencies.add(IOnosDeviceService.class);
        return dependencies;
    }

    @Override
    public void init(FloodlightModuleContext context) {
        this.floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class); 
        this.configService = context.getServiceImpl(IConfigInfoService.class);
        this.restApi = context.getServiceImpl(IRestApiService.class);
        this.datagrid = context.getServiceImpl(IDatagridService.class);
        this.flowPusher = context.getServiceImpl(IFlowPusherService.class);
        this.networkGraphService = context.getServiceImpl(INetworkGraphService.class);
        this.onosDeviceService = context.getServiceImpl(IOnosDeviceService.class);

        // arpCache = new ArpCache();

        arpRequests = Multimaps.synchronizedSetMultimap(HashMultimap
                .<InetAddress, ArpRequest>create());

    }

    @Override
    public void startUp(FloodlightModuleContext context) {
        this.vlan = configService.getVlan();
        log.info("vlan set to {}", this.vlan);

        restApi.addRestletRoutable(new ArpWebRoutable());
        floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
		networkGraph = networkGraphService.getNetworkGraph();
		
	//
	// Event notification setup: channels and event handlers
	//	
	broadcastPacketOutEventChannel = datagrid.addListener(BROADCAST_PACKET_OUT_CHANNEL_NAME,
			     broadcastPacketOutEventHandler,
			     Long.class,
			     BroadcastPacketOutNotification.class);
	
	singlePacketOutEventChannel = datagrid.addListener(SINGLE_PACKET_OUT_CHANNEL_NAME,
			     singlePacketOutEventHandler,
			     Long.class,
			     SinglePacketOutNotification.class);
	
	arpReplyEventChannel = datagrid.addListener(ARP_REPLY_CHANNEL_NAME,
						    arpReplyEventHandler,
						    Long.class,
						    ArpReplyNotification.class);

        Timer arpTimer = new Timer("arp-processing");
        arpTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                doPeriodicArpProcessing();
            }
        }, 0, ARP_TIMER_PERIOD);
    }

    /*
     * Function that runs periodically to manage the asynchronous request mechanism.
     * It basically cleans up old ARP requests if we don't get a response for them.
     * The caller can designate that a request should be retried indefinitely, and
     * this task will handle that as well.
     */
    private void doPeriodicArpProcessing() {
        SetMultimap<InetAddress, ArpRequest> retryList = HashMultimap
                .<InetAddress, ArpRequest>create();

        // Have to synchronize externally on the Multimap while using an
        // iterator,
        // even though it's a synchronizedMultimap
        synchronized (arpRequests) {
            Iterator<Map.Entry<InetAddress, ArpRequest>> it = arpRequests
                    .entries().iterator();

            while (it.hasNext()) {
                Map.Entry<InetAddress, ArpRequest> entry = it.next();
                ArpRequest request = entry.getValue();
                if (request.isExpired()) {
                    log.debug("Cleaning expired ARP request for {}", entry
                            .getKey().getHostAddress());

					// If the ARP request is expired and then delete the device
					// TODO check whether this is OK from this thread
					HostArpRequester requester = (HostArpRequester) request.requester;
					ARP req = requester.getArpRequest();
					Device targetDev = networkGraph.getDeviceByMac(MACAddress.valueOf(req.getTargetHardwareAddress()));
					if(targetDev != null) {
						onosDeviceService.deleteOnosDeviceByMac(MACAddress.valueOf(req.getTargetHardwareAddress()));
						if (log.isDebugEnabled()) {
							log.debug("RemoveDevice: {} due to no have not recieve the ARP reply", targetDev.getMacAddress());
						}
					}

                    it.remove();

                    if (request.shouldRetry()) {
                        retryList.put(entry.getKey(), request);
                    }
                }
            }
        }

        for (Map.Entry<InetAddress, Collection<ArpRequest>> entry : retryList
                .asMap().entrySet()) {

            InetAddress address = entry.getKey();

            log.debug("Resending ARP request for {}", address.getHostAddress());

            // Only ARP requests sent by the controller will have the retry flag
            // set, so for now we can just send a new ARP request for that
            // address.
            sendArpRequestForAddress(address);

            for (ArpRequest request : entry.getValue()) {
                arpRequests.put(address, new ArpRequest(request));
            }
        }
    }

    @Override
    public String getName() {
        return "proxyarpmanager";
    }

    @Override
    public boolean isCallbackOrderingPrereq(OFType type, String name) {
        if (type == OFType.PACKET_IN) {
            return "devicemanager".equals(name)
                    || "onosdevicemanager".equals(name);
        } else {
            return false;
        }
    }

    @Override
    public boolean isCallbackOrderingPostreq(OFType type, String name) {
        return type == OFType.PACKET_IN && "onosforwarding".equals(name);
    }

    @Override
    public Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {

        OFPacketIn pi = (OFPacketIn) msg;

        Ethernet eth = IFloodlightProviderService.bcStore.get(cntx,
                IFloodlightProviderService.CONTEXT_PI_PAYLOAD);

        if (eth.getEtherType() == Ethernet.TYPE_ARP) {
            ARP arp = (ARP) eth.getPayload();
            if (arp.getOpCode() == ARP.OP_REQUEST) {
                handleArpRequest(sw, pi, arp, eth);
            } else if (arp.getOpCode() == ARP.OP_REPLY) {
                // For replies we simply send a notification via Hazelcast
                sendArpReplyNotification(eth, pi);

                // handleArpReply(sw, pi, arp);
            }

            // Stop ARP packets here
            return Command.STOP;
        }

        // Propagate everything else
        return Command.CONTINUE;
    }

    private void handleArpRequest(IOFSwitch sw, OFPacketIn pi, ARP arp,
            Ethernet eth) {
        if (log.isTraceEnabled()) {
            log.trace("ARP request received for {}",
                    inetAddressToString(arp.getTargetProtocolAddress()));
        }

        InetAddress target;
        try {
            target = InetAddress.getByAddress(arp.getTargetProtocolAddress());
        } catch (UnknownHostException e) {
            log.debug("Invalid address in ARP request", e);
            return;
        }

        if (configService.fromExternalNetwork(sw.getId(), pi.getInPort())) {
            // If the request came from outside our network, we only care if
            // it was a request for one of our interfaces.
            if (configService.isInterfaceAddress(target)) {
                log.trace(
                        "ARP request for our interface. Sending reply {} => {}",
                        target.getHostAddress(),
                        configService.getRouterMacAddress());

                sendArpReply(arp, sw.getId(), pi.getInPort(),
                        configService.getRouterMacAddress());
            }

            return;
        }

        // MACAddress macAddress = arpCache.lookup(target);

		arpRequests.put(target, new ArpRequest(
				new HostArpRequester(arp, sw.getId(), pi.getInPort()), false));
		
		Device targetDevice = networkGraph.getDeviceByMac(MACAddress.valueOf(arp.getTargetHardwareAddress()));

		if (targetDevice == null) {
			if (log.isTraceEnabled()) {
				log.trace("No device info found for {} - broadcasting",
						target.getHostAddress());
			}
			
			// We don't know the device so broadcast the request out
			BroadcastPacketOutNotification key =
					new BroadcastPacketOutNotification(eth.serialize(),
							ByteBuffer.wrap(arp.getTargetProtocolAddress()).getInt(), sw.getId(), pi.getInPort());
			log.debug("broadcastPacketOutEventChannel mac {}, ip {}, dpid {}, port {}, paket {}", eth.getSourceMAC().toLong(), 
					ByteBuffer.wrap(arp.getTargetProtocolAddress()).getInt(), sw.getId(), pi.getInPort(), eth.serialize().length);
			broadcastPacketOutEventChannel.addTransientEntry(eth.getDestinationMAC().toLong(), key);
		}
		else {
			// Even if the device exists in our database, we do not reply to
			// the request directly, but check whether the device is still valid
			MACAddress macAddress = MACAddress.valueOf(arp.getTargetHardwareAddress());

			if (log.isTraceEnabled()) {
				log.trace("The target Device Record in DB is: {} => {} from ARP request host at {}/{}",
						new Object [] {
						inetAddressToString(arp.getTargetProtocolAddress()),
						macAddress,
						HexString.toHexString(sw.getId()), pi.getInPort()});
			}

			// sendArpReply(arp, sw.getId(), pi.getInPort(), macAddress);

			Iterable<net.onrc.onos.ofcontroller.networkgraph.Port> outPorts = targetDevice.getAttachmentPoints();

			if (!outPorts.iterator().hasNext()){
				if (log.isTraceEnabled()) {
					log.trace("Device {} exists but is not connected to any ports" + 
							" - broadcasting", macAddress);
				}
				
//				BroadcastPacketOutNotification key =
//						new BroadcastPacketOutNotification(eth.serialize(), 
//								target, sw.getId(), pi.getInPort());
//				broadcastPacketOutEventChannel.addTransientEntry(eth.getDestinationMAC().toLong(), key);
			} 
			else {
				for (net.onrc.onos.ofcontroller.networkgraph.Port portObject : outPorts) {
					//long outSwitch = 0;
					//short outPort = 0;

					if(portObject.getOutgoingLink() != null || portObject.getIncomingLink() != null) {
						continue;
					}
					
					short outPort = portObject.getNumber().shortValue();
					Switch outSwitchObject = portObject.getSwitch();
					long outSwitch = outSwitchObject.getDpid();
					
					if (log.isTraceEnabled()) {
						log.trace("Probing device {} on port {}/{}", 
								new Object[] {macAddress, 
								HexString.toHexString(outSwitch), outPort});
					}
					
					SinglePacketOutNotification key =
						    new SinglePacketOutNotification(eth.serialize(), 
						    		ByteBuffer.wrap(target.getAddress()).getInt(), outSwitch, outPort);
					singlePacketOutEventChannel.addTransientEntry(eth.getDestinationMAC().toLong(), key);
				}
			}
		}
    }

    // Not used because device manager currently updates the database
    // for ARP replies. May be useful in the future.
    private void handleArpReply(IOFSwitch sw, OFPacketIn pi, ARP arp) {
        if (log.isTraceEnabled()) {
            log.trace("ARP reply recieved: {} => {}, on {}/{}", new Object[] {
                    inetAddressToString(arp.getSenderProtocolAddress()),
                    HexString.toHexString(arp.getSenderHardwareAddress()),
                    HexString.toHexString(sw.getId()), pi.getInPort()});
        }

        InetAddress senderIpAddress;
        try {
            senderIpAddress = InetAddress.getByAddress(arp
                    .getSenderProtocolAddress());
        } catch (UnknownHostException e) {
            log.debug("Invalid address in ARP reply", e);
            return;
        }

        MACAddress senderMacAddress = MACAddress.valueOf(arp
                .getSenderHardwareAddress());

        // See if anyone's waiting for this ARP reply
        Set<ArpRequest> requests = arpRequests.get(senderIpAddress);

        // Synchronize on the Multimap while using an iterator for one of the
        // sets
        List<ArpRequest> requestsToSend = new ArrayList<ArpRequest>(
                requests.size());
        synchronized (arpRequests) {
            Iterator<ArpRequest> it = requests.iterator();
            while (it.hasNext()) {
                ArpRequest request = it.next();
                it.remove();
                requestsToSend.add(request);
            }
        }

        // Don't hold an ARP lock while dispatching requests
        for (ArpRequest request : requestsToSend) {
            request.dispatchReply(senderIpAddress, senderMacAddress);
        }
    }

    private void sendArpRequestForAddress(InetAddress ipAddress) {
        // TODO what should the sender IP address and MAC address be if no
        // IP addresses are configured? Will there ever be a need to send
        // ARP requests from the controller in that case?
        // All-zero MAC address doesn't seem to work - hosts don't respond to it

        byte[] zeroIpv4 = {0x0, 0x0, 0x0, 0x0};
        byte[] zeroMac = {0x0, 0x0, 0x0, 0x0, 0x0, 0x0};
        byte[] genericNonZeroMac = {0x0, 0x0, 0x0, 0x0, 0x0, 0x01};
        byte[] broadcastMac = {(byte) 0xff, (byte) 0xff, (byte) 0xff,
                (byte) 0xff, (byte) 0xff, (byte) 0xff};

        ARP arpRequest = new ARP();

        arpRequest
                .setHardwareType(ARP.HW_TYPE_ETHERNET)
                .setProtocolType(ARP.PROTO_TYPE_IP)
                .setHardwareAddressLength(
                        (byte) Ethernet.DATALAYER_ADDRESS_LENGTH)
                .setProtocolAddressLength((byte) IPv4.ADDRESS_LENGTH)
                .setOpCode(ARP.OP_REQUEST).setTargetHardwareAddress(zeroMac)
                .setTargetProtocolAddress(ipAddress.getAddress());

        MACAddress routerMacAddress = configService.getRouterMacAddress();
        // TODO hack for now as it's unclear what the MAC address should be
        byte[] senderMacAddress = genericNonZeroMac;
        if (routerMacAddress != null) {
            senderMacAddress = routerMacAddress.toBytes();
        }
        arpRequest.setSenderHardwareAddress(senderMacAddress);

        byte[] senderIPAddress = zeroIpv4;
        Interface intf = configService.getOutgoingInterface(ipAddress);
        if (intf != null) {
            senderIPAddress = intf.getIpAddress().getAddress();
        }

        arpRequest.setSenderProtocolAddress(senderIPAddress);

        Ethernet eth = new Ethernet();
        eth.setSourceMACAddress(senderMacAddress)
                .setDestinationMACAddress(broadcastMac)
                .setEtherType(Ethernet.TYPE_ARP).setPayload(arpRequest);

        if (vlan != NO_VLAN) {
            eth.setVlanID(vlan).setPriorityCode((byte) 0);
        }

        // sendArpRequestToSwitches(ipAddress, eth.serialize());
		SinglePacketOutNotification key =
		    new SinglePacketOutNotification(eth.serialize(), ByteBuffer.wrap(ipAddress.getAddress()).getInt(),
						    intf.getDpid(), intf.getPort());
		singlePacketOutEventChannel.addTransientEntry(MACAddress.valueOf(senderMacAddress).toLong(), key);
    }
    
    private void sendArpRequestToSwitches(InetAddress dstAddress, byte[] arpRequest) {
    		sendArpRequestToSwitches(dstAddress, arpRequest, 0,
    		OFPort.OFPP_NONE.getValue());
    }

    private void sendArpRequestToSwitches(InetAddress dstAddress,
            byte[] arpRequest, long inSwitch, short inPort) {

        if (configService.hasLayer3Configuration()) {
            Interface intf = configService.getOutgoingInterface(dstAddress);
            if (intf == null) {
                // TODO here it should be broadcast out all non-interface edge
                // ports.
                // I think we can assume that if it's not a request for an
                // external
                // network, it's an ARP for a host in our own network. So we
                // want to
                // send it out all edge ports that don't have an interface
                // configured
                // to ensure it reaches all hosts in our network.
                log.debug("No interface found to send ARP request for {}",
                        dstAddress.getHostAddress());
            } else {
                sendArpRequestOutPort(arpRequest, intf.getDpid(),
                        intf.getPort());
            }
        } else {
            // broadcastArpRequestOutEdge(arpRequest, inSwitch, inPort);
            broadcastArpRequestOutMyEdge(arpRequest, inSwitch, inPort);
        }
    }

    private void sendArpReplyNotification(Ethernet eth, OFPacketIn pi) {
        ARP arp = (ARP) eth.getPayload();

        if (log.isTraceEnabled()) {
            log.trace("Sending ARP reply for {} to other ONOS instances",
                    inetAddressToString(arp.getSenderProtocolAddress()));
        }

        InetAddress targetAddress;

        try {
            targetAddress = InetAddress.getByAddress(arp
                    .getSenderProtocolAddress());
        } catch (UnknownHostException e) {
            log.error("Unknown host", e);
            return;
        }

        MACAddress mac = new MACAddress(arp.getSenderHardwareAddress());

		ArpReplyNotification key =
		    new ArpReplyNotification(ByteBuffer.wrap(targetAddress.getAddress()).getInt(), mac);
		log.debug("ArpReplyNotification ip {}, mac{}", ByteBuffer.wrap(targetAddress.getAddress()).getInt(), mac);
		arpReplyEventChannel.addTransientEntry(mac.toLong(), key);
    }

    private void broadcastArpRequestOutMyEdge(byte[] arpRequest, long inSwitch,
            short inPort) {
        List<SwitchPort> switchPorts = new ArrayList<SwitchPort>();

        for (IOFSwitch sw : floodlightProvider.getSwitches().values()) {

            OFPacketOut po = new OFPacketOut();
            po.setInPort(OFPort.OFPP_NONE).setBufferId(-1)
                    .setPacketData(arpRequest);

            List<OFAction> actions = new ArrayList<OFAction>();

			Switch graphSw = networkGraph.getSwitch(sw.getId());
			Collection<net.onrc.onos.ofcontroller.networkgraph.Port> ports = graphSw.getPorts();
			
			if (ports == null) {
				continue;
			}
			
			for (net.onrc.onos.ofcontroller.networkgraph.Port portObject : ports) {
				if (portObject.getOutgoingLink() == null && portObject.getNumber() > 0) {
					Long portNumber = portObject.getNumber();
					
					if (sw.getId() == inSwitch && portNumber.shortValue() == inPort) {
						// This is the port that the ARP message came in,
						// so don't broadcast out this port
						continue;
					}		
					switchPorts.add(new SwitchPort(new Dpid(sw.getId()), 
							new net.onrc.onos.core.util.Port(portNumber.shortValue())));
					actions.add(new OFActionOutput(portNumber.shortValue()));
				}
			}

            po.setActions(actions);
            short actionsLength = (short) (actions.size() * OFActionOutput.MINIMUM_LENGTH);
            po.setActionsLength(actionsLength);
            po.setLengthU(OFPacketOut.MINIMUM_LENGTH + actionsLength
                    + arpRequest.length);

            flowPusher.add(sw, po);
        }

        if (log.isTraceEnabled()) {
            log.trace("Broadcast ARP request to: {}", switchPorts);
        }
    }

    private void sendArpRequestOutPort(byte[] arpRequest, long dpid, short port) {
        if (log.isTraceEnabled()) {
            log.trace("Sending ARP request out {}/{}",
                    HexString.toHexString(dpid), port);
        }

        OFPacketOut po = new OFPacketOut();
        po.setInPort(OFPort.OFPP_NONE).setBufferId(-1)
                .setPacketData(arpRequest);

        List<OFAction> actions = new ArrayList<OFAction>();
        actions.add(new OFActionOutput(port));
        po.setActions(actions);
        short actionsLength = (short) (actions.size() * OFActionOutput.MINIMUM_LENGTH);
        po.setActionsLength(actionsLength);
        po.setLengthU(OFPacketOut.MINIMUM_LENGTH + actionsLength
                + arpRequest.length);

        IOFSwitch sw = floodlightProvider.getSwitches().get(dpid);

        if (sw == null) {
            log.warn("Switch not found when sending ARP request");
            return;
        }

        flowPusher.add(sw, po);
    }

    private void sendArpReply(ARP arpRequest, long dpid, short port,
            MACAddress targetMac) {
        if (log.isTraceEnabled()) {
            log.trace(
                    "Sending reply {} => {} to {}",
                    new Object[] {
                            inetAddressToString(arpRequest
                                    .getTargetProtocolAddress()),
                            targetMac,
                            inetAddressToString(arpRequest
                                    .getSenderProtocolAddress())});
        }

        ARP arpReply = new ARP();
        arpReply.setHardwareType(ARP.HW_TYPE_ETHERNET)
                .setProtocolType(ARP.PROTO_TYPE_IP)
                .setHardwareAddressLength(
                        (byte) Ethernet.DATALAYER_ADDRESS_LENGTH)
                .setProtocolAddressLength((byte) IPv4.ADDRESS_LENGTH)
                .setOpCode(ARP.OP_REPLY)
                .setSenderHardwareAddress(targetMac.toBytes())
                .setSenderProtocolAddress(arpRequest.getTargetProtocolAddress())
                .setTargetHardwareAddress(arpRequest.getSenderHardwareAddress())
                .setTargetProtocolAddress(arpRequest.getSenderProtocolAddress());

        Ethernet eth = new Ethernet();
        eth.setDestinationMACAddress(arpRequest.getSenderHardwareAddress())
                .setSourceMACAddress(targetMac.toBytes())
                .setEtherType(Ethernet.TYPE_ARP).setPayload(arpReply);

        if (vlan != NO_VLAN) {
            eth.setVlanID(vlan).setPriorityCode((byte) 0);
        }

        List<OFAction> actions = new ArrayList<OFAction>();
        actions.add(new OFActionOutput(port));

        OFPacketOut po = new OFPacketOut();
        po.setInPort(OFPort.OFPP_NONE)
                .setBufferId(-1)
                .setPacketData(eth.serialize())
                .setActions(actions)
                .setActionsLength((short) OFActionOutput.MINIMUM_LENGTH)
                .setLengthU(
                        OFPacketOut.MINIMUM_LENGTH
                                + OFActionOutput.MINIMUM_LENGTH
                                + po.getPacketData().length);

        List<OFMessage> msgList = new ArrayList<OFMessage>();
        msgList.add(po);

        IOFSwitch sw = floodlightProvider.getSwitches().get(dpid);

        if (sw == null) {
            log.warn("Switch {} not found when sending ARP reply",
                    HexString.toHexString(dpid));
            return;
        }

        flowPusher.add(sw, po);
    }

    private String inetAddressToString(byte[] bytes) {
        try {
            return InetAddress.getByAddress(bytes).getHostAddress();
        } catch (UnknownHostException e) {
            log.debug("Invalid IP address", e);
            return "";
        }
    }

    /*
     * IProxyArpService methods
     */

    @Override
    public MACAddress getMacAddress(InetAddress ipAddress) {
        // return arpCache.lookup(ipAddress);
        return null;
    }

    @Override
    public void sendArpRequest(InetAddress ipAddress, IArpRequester requester,
            boolean retry) {
        arpRequests.put(ipAddress, new ArpRequest(requester, retry));

        // Sanity check to make sure we don't send a request for our own address
        if (!configService.isInterfaceAddress(ipAddress)) {
            sendArpRequestForAddress(ipAddress);
        }
    }

    @Override
    public List<String> getMappings() {
        return new ArrayList<String>();
    }

    private void sendArpReplyToWaitingRequesters(InetAddress address,
            MACAddress mac) {
        log.debug("Sending ARP reply for {} to requesters",
                address.getHostAddress());

        // See if anyone's waiting for this ARP reply
        Set<ArpRequest> requests = arpRequests.get(address);

        // Synchronize on the Multimap while using an iterator for one of the
        // sets
        List<ArpRequest> requestsToSend = new ArrayList<ArpRequest>(
                requests.size());
        synchronized (arpRequests) {
            Iterator<ArpRequest> it = requests.iterator();
            while (it.hasNext()) {
                ArpRequest request = it.next();
                it.remove();
                requestsToSend.add(request);
            }
        }

        //TODO here, comment outed from long time ago. I will check if we need it later.
        /*IDeviceObject deviceObject = deviceStorage.getDeviceByIP(
        		InetAddresses.coerceToInteger(address));

        MACAddress mac = MACAddress.valueOf(deviceObject.getMACAddress());

        log.debug("Found {} at {} in network map",
        		address.getHostAddress(), mac);*/

        // Don't hold an ARP lock while dispatching requests
        for (ArpRequest request : requestsToSend) {
            request.dispatchReply(address, mac);
        }
    }
}