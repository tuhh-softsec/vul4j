package net.onrc.onos.ofcontroller.proxyarp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
import net.floodlightcontroller.packet.ARP;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.topology.ITopologyService;
import net.floodlightcontroller.util.MACAddress;
import net.onrc.onos.datagrid.IDatagridService;
import net.onrc.onos.ofcontroller.bgproute.Interface;
import net.onrc.onos.ofcontroller.core.IDeviceStorage;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IDeviceObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IPortObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyService.ITopoSwitchService;
import net.onrc.onos.ofcontroller.core.config.IConfigInfoService;
import net.onrc.onos.ofcontroller.core.internal.DeviceStorageImpl;
import net.onrc.onos.ofcontroller.core.internal.TopoSwitchServiceImpl;
import net.onrc.onos.ofcontroller.flowprogrammer.IFlowPusherService;
import net.onrc.onos.ofcontroller.util.Dpid;
import net.onrc.onos.ofcontroller.util.Port;
import net.onrc.onos.ofcontroller.util.SwitchPort;

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
import com.google.common.net.InetAddresses;

public class ProxyArpManager implements IProxyArpService, IOFMessageListener,
										IPacketOutEventHandler, IArpReplyEventHandler, 
										IFloodlightModule {
	private final static Logger log = LoggerFactory.getLogger(ProxyArpManager.class);
	
	private final long ARP_TIMER_PERIOD = 100; //ms  

	private static final int ARP_REQUEST_TIMEOUT = 2000; //ms
			
	private IFloodlightProviderService floodlightProvider;
	private ITopologyService topology;
	private IDatagridService datagrid;
	private IConfigInfoService configService;
	private IRestApiService restApi;
	private IFlowPusherService flowPusher;
	
	private IDeviceStorage deviceStorage;
	private volatile ITopoSwitchService topoSwitchService;
	
	private short vlan;
	private static final short NO_VLAN = 0;
	
	//private ArpCache arpCache;

	private SetMultimap<InetAddress, ArpRequest> arpRequests;
	
	private static class ArpRequest {
		private final IArpRequester requester;
		private final boolean retry;
		private long requestTime;
		
		public ArpRequest(IArpRequester requester, boolean retry){
			this.requester = requester;
			this.retry = retry;
			this.requestTime = System.currentTimeMillis();
		}
		
		public ArpRequest(ArpRequest old) {
			this.requester = old.requester;
			this.retry = old.retry;
			this.requestTime = System.currentTimeMillis();
		}
		
		public boolean isExpired() {
			return (System.currentTimeMillis() - requestTime) > ARP_REQUEST_TIMEOUT;
		}
		
		public boolean shouldRetry() {
			return retry;
		}
		
		public void dispatchReply(InetAddress ipAddress, MACAddress replyMacAddress) {
			requester.arpResponse(ipAddress, replyMacAddress);
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
			ProxyArpManager.this.sendArpReply(arpRequest, dpid, port, macAddress);
		}
	}
	
	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		Collection<Class<? extends IFloodlightService>> l 
			= new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IProxyArpService.class);
		return l;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		Map<Class<? extends IFloodlightService>, IFloodlightService> m 
			= new HashMap<Class<? extends IFloodlightService>, IFloodlightService>();
		m.put(IProxyArpService.class, this);
		return m;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		Collection<Class<? extends IFloodlightService>> dependencies 
			= new ArrayList<Class<? extends IFloodlightService>>();
		dependencies.add(IFloodlightProviderService.class);
		dependencies.add(ITopologyService.class);
		dependencies.add(IRestApiService.class);
		dependencies.add(IDatagridService.class);
		dependencies.add(IConfigInfoService.class);
		dependencies.add(IFlowPusherService.class);
		return dependencies;
	}
	
	@Override
	public void init(FloodlightModuleContext context){
		this.floodlightProvider = 
				context.getServiceImpl(IFloodlightProviderService.class);
		this.topology = context.getServiceImpl(ITopologyService.class);
		this.datagrid = context.getServiceImpl(IDatagridService.class);
		this.configService = context.getServiceImpl(IConfigInfoService.class);
		this.restApi = context.getServiceImpl(IRestApiService.class);
		this.flowPusher = context.getServiceImpl(IFlowPusherService.class);
		
		//arpCache = new ArpCache();

		arpRequests = Multimaps.synchronizedSetMultimap(
				HashMultimap.<InetAddress, ArpRequest>create());
		
		topoSwitchService = new TopoSwitchServiceImpl();
	}
	
	@Override
	public void startUp(FloodlightModuleContext context) {
		this.vlan = configService.getVlan();
		log.info("vlan set to {}", this.vlan);
		
		restApi.addRestletRoutable(new ArpWebRoutable());
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
		
		datagrid.registerPacketOutEventHandler(this);
		datagrid.registerArpReplyEventHandler(this);
		
		deviceStorage = new DeviceStorageImpl();
		deviceStorage.init("","");
		
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
		SetMultimap<InetAddress, ArpRequest> retryList 
				= HashMultimap.<InetAddress, ArpRequest>create();

		//Have to synchronize externally on the Multimap while using an iterator,
		//even though it's a synchronizedMultimap
		synchronized (arpRequests) {			
			Iterator<Map.Entry<InetAddress, ArpRequest>> it 
				= arpRequests.entries().iterator();
			
			while (it.hasNext()) {
				Map.Entry<InetAddress, ArpRequest> entry
						= it.next();
				ArpRequest request = entry.getValue();
				if (request.isExpired()) {
					log.debug("Cleaning expired ARP request for {}", 
							entry.getKey().getHostAddress());
		
					// If the ARP request is expired and then delete the device
					// TODO check whether this is OK from this thread
					IDeviceObject targetDevice = 
							deviceStorage.getDeviceByIP(InetAddresses.coerceToInteger(entry.getKey()));
					
					if (targetDevice != null) {
						deviceStorage.removeDevice(targetDevice);
						if (log.isDebugEnabled()) {
							log.debug("RemoveDevice: {} due to no have not recieve the ARP reply", targetDevice);
						}
					}
					
					it.remove();
					
					if (request.shouldRetry()) {
						retryList.put(entry.getKey(), request);
					}
				}
			}
		}
		
		for (Map.Entry<InetAddress, Collection<ArpRequest>> entry 
				: retryList.asMap().entrySet()) {
			
			InetAddress address = entry.getKey();
			
			log.debug("Resending ARP request for {}", address.getHostAddress());
			
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
			return "devicemanager".equals(name) || "onosdevicemanager".equals(name);
		}
		else {
			return false;
		}
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		return type == OFType.PACKET_IN && "onosforwarding".equals(name);
	}

	@Override
	public Command receive(
			IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
		
		OFPacketIn pi = (OFPacketIn) msg;
		
		Ethernet eth = IFloodlightProviderService.bcStore.get(cntx, 
                IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
		
		if (eth.getEtherType() == Ethernet.TYPE_ARP){
			ARP arp = (ARP) eth.getPayload();	
			if (arp.getOpCode() == ARP.OP_REQUEST) {
				handleArpRequest(sw, pi, arp, eth);
			}
			else if (arp.getOpCode() == ARP.OP_REPLY) {
				// For replies we simply send a notification via Hazelcast
				sendArpReplyNotification(eth, pi);
				
				//handleArpReply(sw, pi, arp);
			}
			
			// Stop ARP packets here
			return Command.STOP;
		}
		
		// Propagate everything else
		return Command.CONTINUE;
	}
	
	private void handleArpRequest(IOFSwitch sw, OFPacketIn pi, ARP arp, Ethernet eth) {
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
			//If the request came from outside our network, we only care if
			//it was a request for one of our interfaces.
			if (configService.isInterfaceAddress(target)) {
				log.trace("ARP request for our interface. Sending reply {} => {}",
						target.getHostAddress(), configService.getRouterMacAddress());

				sendArpReply(arp, sw.getId(), pi.getInPort(), 
						configService.getRouterMacAddress());
			}

			return;
		}
		
		//MACAddress macAddress = arpCache.lookup(target);

		arpRequests.put(target, new ArpRequest(
				new HostArpRequester(arp, sw.getId(), pi.getInPort()), false));

		IDeviceObject targetDevice = 
				deviceStorage.getDeviceByIP(InetAddresses.coerceToInteger(target));
		
		if (targetDevice == null) {
			if (log.isTraceEnabled()) {
				log.trace("No device info found for {} - broadcasting",
						target.getHostAddress());
			}
			
			// We don't know the device so broadcast the request out
			datagrid.sendPacketOutNotification(
					new BroadcastPacketOutNotification(eth.serialize(), 
							sw.getId(), pi.getInPort()));
		}
		else {
			// Even if the device exists in our database, we do not reply to
			// the request directly, but check whether the device is still valid
			MACAddress macAddress = MACAddress.valueOf(targetDevice.getMACAddress());

			if (log.isTraceEnabled()) {
				log.trace("The target Device Record in DB is: {} => {} from ARP request host at {}/{}",
						new Object [] {
						inetAddressToString(arp.getTargetProtocolAddress()),
						macAddress,
						HexString.toHexString(sw.getId()), pi.getInPort()});
			}

			// sendArpReply(arp, sw.getId(), pi.getInPort(), macAddress);

			Iterable<IPortObject> outPorts = targetDevice.getAttachedPorts();	

			if (!outPorts.iterator().hasNext()){
				if (log.isTraceEnabled()) {
					log.trace("Device {} exists but is not connected to any ports" + 
							" - broadcasting", macAddress);
				}
				
				datagrid.sendPacketOutNotification(
						new BroadcastPacketOutNotification(eth.serialize(), 
								sw.getId(), pi.getInPort()));
			} 
			else {
				for (IPortObject portObject : outPorts) {
					//long outSwitch = 0;
					//short outPort = 0;

					/*
					if (!portObject.getLinkedPorts().iterator().hasNext()) {
						outPort = portObject.getNumber();					
					}*/
					if (portObject.getLinkedPorts().iterator().hasNext()) {
						continue;
					}

					short outPort = portObject.getNumber();
					ISwitchObject outSwitchObject = portObject.getSwitch();
					long outSwitch = HexString.toLong(outSwitchObject.getDPID());

					if (log.isTraceEnabled()) {
						log.trace("Probing device {} on port {}/{}", 
								new Object[] {macAddress, 
								HexString.toHexString(outSwitch), outPort});
					}
					
					datagrid.sendPacketOutNotification(
							new SinglePacketOutNotification(eth.serialize(), 
									outSwitch, outPort));
				}
			}
		}
	}
	
	private void handleArpReply(IOFSwitch sw, OFPacketIn pi, ARP arp){
		if (log.isTraceEnabled()) {
			log.trace("ARP reply recieved: {} => {}, on {}/{}", new Object[] { 
					inetAddressToString(arp.getSenderProtocolAddress()),
					HexString.toHexString(arp.getSenderHardwareAddress()),
					HexString.toHexString(sw.getId()), pi.getInPort()});
		}
		
		InetAddress senderIpAddress;
		try {
			senderIpAddress = InetAddress.getByAddress(arp.getSenderProtocolAddress());
		} catch (UnknownHostException e) {
			log.debug("Invalid address in ARP reply", e);
			return;
		}
		
		MACAddress senderMacAddress = MACAddress.valueOf(arp.getSenderHardwareAddress());
		
		//arpCache.update(senderIpAddress, senderMacAddress);
		
		//See if anyone's waiting for this ARP reply
		Set<ArpRequest> requests = arpRequests.get(senderIpAddress);
		
		//Synchronize on the Multimap while using an iterator for one of the sets
		List<ArpRequest> requestsToSend = new ArrayList<ArpRequest>(requests.size());
		synchronized (arpRequests) {
			Iterator<ArpRequest> it = requests.iterator();
			while (it.hasNext()) {
				ArpRequest request = it.next();
				it.remove();
				requestsToSend.add(request);
			}
		}
		
		//Don't hold an ARP lock while dispatching requests
		for (ArpRequest request : requestsToSend) {
			request.dispatchReply(senderIpAddress, senderMacAddress);
		}
	}
	
	private void sendArpRequestForAddress(InetAddress ipAddress) {
		//TODO what should the sender IP address and MAC address be if no
		//IP addresses are configured? Will there ever be a need to send
		//ARP requests from the controller in that case?
		//All-zero MAC address doesn't seem to work - hosts don't respond to it
		
		byte[] zeroIpv4 = {0x0, 0x0, 0x0, 0x0};
		byte[] zeroMac = {0x0, 0x0, 0x0, 0x0, 0x0, 0x0};
		byte[] genericNonZeroMac = {0x0, 0x0, 0x0, 0x0, 0x0, 0x01};
		byte[] broadcastMac = {(byte)0xff, (byte)0xff, (byte)0xff, 
				(byte)0xff, (byte)0xff, (byte)0xff};
		
		ARP arpRequest = new ARP();
		
		arpRequest.setHardwareType(ARP.HW_TYPE_ETHERNET)
			.setProtocolType(ARP.PROTO_TYPE_IP)
			.setHardwareAddressLength((byte)Ethernet.DATALAYER_ADDRESS_LENGTH)
			.setProtocolAddressLength((byte)IPv4.ADDRESS_LENGTH)
			.setOpCode(ARP.OP_REQUEST)
			.setTargetHardwareAddress(zeroMac)
			.setTargetProtocolAddress(ipAddress.getAddress());

		MACAddress routerMacAddress = configService.getRouterMacAddress();
		//TODO hack for now as it's unclear what the MAC address should be
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
			.setEtherType(Ethernet.TYPE_ARP)
			.setPayload(arpRequest);
		
		if (vlan != NO_VLAN) {
			eth.setVlanID(vlan)
			   .setPriorityCode((byte)0);
		}
		
		sendArpRequestToSwitches(ipAddress, eth.serialize());
	}
	
	private void sendArpRequestToSwitches(InetAddress dstAddress, byte[] arpRequest) {
		sendArpRequestToSwitches(dstAddress, arpRequest, 
				0, OFPort.OFPP_NONE.getValue());
	}
	
	private void sendArpRequestToSwitches(InetAddress dstAddress, byte[] arpRequest,
			long inSwitch, short inPort) {

		if (configService.hasLayer3Configuration()) {
			Interface intf = configService.getOutgoingInterface(dstAddress);
			if (intf != null) {
				sendArpRequestOutPort(arpRequest, intf.getDpid(), intf.getPort());
			}
			else {
				//TODO here it should be broadcast out all non-interface edge ports.
				//I think we can assume that if it's not a request for an external 
				//network, it's an ARP for a host in our own network. So we want to 
				//send it out all edge ports that don't have an interface configured
				//to ensure it reaches all hosts in our network.
				log.debug("No interface found to send ARP request for {}",
						dstAddress.getHostAddress());
			}
		}
		else {
			broadcastArpRequestOutEdge(arpRequest, inSwitch, inPort);
		}
	}
	
	private void sendArpReplyNotification(Ethernet eth, OFPacketIn pi) {
		ARP arp = (ARP) eth.getPayload();
		
		if (log.isTraceEnabled()) {
			log.trace("Sending ARP reply for {} to other ONOS instances",
					inetAddressToString(arp.getSenderProtocolAddress()));
		}
		
		InetAddress targetAddress;		
		MACAddress mac = new MACAddress(arp.getSenderHardwareAddress());
		
		try {
			targetAddress = InetAddress.getByAddress(arp.getSenderProtocolAddress());
		} catch (UnknownHostException e) {
			log.error("Unknown host", e);
			return;
		}

		datagrid.sendArpReplyNotification(new ArpReplyNotification(targetAddress, mac));
	}
	
	// This remains from the older single-instance ARP code. It used Floodlight
	// APIs to find the edge of the network, but only worked on a single instance.
	// We now do this using ONOS network graph APIs.
	@Deprecated
	private void broadcastArpRequestOutEdge(byte[] arpRequest, long inSwitch, short inPort) {
		for (IOFSwitch sw : floodlightProvider.getSwitches().values()){
			Collection<Short> enabledPorts = sw.getEnabledPortNumbers();
			Set<Short> linkPorts = topology.getPortsWithLinks(sw.getId());
			
			if (linkPorts == null){
				//I think this means the switch doesn't have any links.
				//continue;
				linkPorts = new HashSet<Short>();
			}
			
			
			OFPacketOut po = new OFPacketOut();
			po.setInPort(OFPort.OFPP_NONE)
				.setBufferId(-1)
				.setPacketData(arpRequest);
				
			List<OFAction> actions = new ArrayList<OFAction>();
			
			for (short portNum : enabledPorts){
				if (linkPorts.contains(portNum) || 
						(sw.getId() == inSwitch && portNum == inPort)){
					//If this port isn't an edge port or is the ingress port
					//for the ARP, don't broadcast out it
					continue;
				}
				
				actions.add(new OFActionOutput(portNum));
			}
			
			po.setActions(actions);
			short actionsLength = (short) (actions.size() * OFActionOutput.MINIMUM_LENGTH);
			po.setActionsLength(actionsLength);
			po.setLengthU(OFPacketOut.MINIMUM_LENGTH + actionsLength 
					+ arpRequest.length);
			
			List<OFMessage> msgList = new ArrayList<OFMessage>();
			msgList.add(po);
			
			try {
				sw.write(msgList, null);
				sw.flush();
			} catch (IOException e) {
				log.error("Failure writing packet out to switch", e);
			}
		}
	}
	
	private void broadcastArpRequestOutMyEdge(byte[] arpRequest,
			long inSwitch, short inPort) {
		List<SwitchPort> switchPorts = new ArrayList<SwitchPort>();
		
		for (IOFSwitch sw : floodlightProvider.getSwitches().values()) {
			
			OFPacketOut po = new OFPacketOut();
			po.setInPort(OFPort.OFPP_NONE)
			.setBufferId(-1)
			.setPacketData(arpRequest);
			
			List<OFAction> actions = new ArrayList<OFAction>();
			
			Iterable<IPortObject> ports 
				= topoSwitchService.getPortsOnSwitch(sw.getStringId());
			if (ports == null) {
				continue;
			}
			
			for (IPortObject portObject : ports) {
				if (!portObject.getLinkedPorts().iterator().hasNext()) {
					short portNumber = portObject.getNumber();
					
					if (sw.getId() == inSwitch && portNumber == inPort) {
						// This is the port that the ARP message came in,
						// so don't broadcast out this port
						continue;
					}
					
					switchPorts.add(new SwitchPort(new Dpid(sw.getId()), 
							new Port(portNumber)));
					actions.add(new OFActionOutput(portNumber));
				}
			}
			
			po.setActions(actions);
			short actionsLength = (short) 
					(actions.size() * OFActionOutput.MINIMUM_LENGTH);
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
		po.setInPort(OFPort.OFPP_NONE)
			.setBufferId(-1)
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
	
	private void sendArpReply(ARP arpRequest, long dpid, short port, MACAddress targetMac) {
		if (log.isTraceEnabled()) {
			log.trace("Sending reply {} => {} to {}", new Object[] {
					inetAddressToString(arpRequest.getTargetProtocolAddress()),
					targetMac,
					inetAddressToString(arpRequest.getSenderProtocolAddress())});
		}
		
		ARP arpReply = new ARP();
		arpReply.setHardwareType(ARP.HW_TYPE_ETHERNET)
			.setProtocolType(ARP.PROTO_TYPE_IP)
			.setHardwareAddressLength((byte)Ethernet.DATALAYER_ADDRESS_LENGTH)
			.setProtocolAddressLength((byte)IPv4.ADDRESS_LENGTH)
			.setOpCode(ARP.OP_REPLY)
			.setSenderHardwareAddress(targetMac.toBytes())
			.setSenderProtocolAddress(arpRequest.getTargetProtocolAddress())
			.setTargetHardwareAddress(arpRequest.getSenderHardwareAddress())
			.setTargetProtocolAddress(arpRequest.getSenderProtocolAddress());
		

		Ethernet eth = new Ethernet();
		eth.setDestinationMACAddress(arpRequest.getSenderHardwareAddress())
			.setSourceMACAddress(targetMac.toBytes())
			.setEtherType(Ethernet.TYPE_ARP)
			.setPayload(arpReply);
		
		if (vlan != NO_VLAN) {
			eth.setVlanID(vlan)
			   .setPriorityCode((byte)0);
		}
		
		List<OFAction> actions = new ArrayList<OFAction>();
		actions.add(new OFActionOutput(port));
		
		OFPacketOut po = new OFPacketOut();
		po.setInPort(OFPort.OFPP_NONE)
			.setBufferId(-1)
			.setPacketData(eth.serialize())
			.setActions(actions)
			.setActionsLength((short)OFActionOutput.MINIMUM_LENGTH)
			.setLengthU(OFPacketOut.MINIMUM_LENGTH + OFActionOutput.MINIMUM_LENGTH
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
		//return arpCache.lookup(ipAddress);
		return null;
	}

	@Override
	public void sendArpRequest(InetAddress ipAddress, IArpRequester requester,
			boolean retry) {
		arpRequests.put(ipAddress, new ArpRequest(requester, retry));
		
		//Sanity check to make sure we don't send a request for our own address
		if (!configService.isInterfaceAddress(ipAddress)) {
			sendArpRequestForAddress(ipAddress);
		}
	}
	
	@Override
	public List<String> getMappings() {
		//return arpCache.getMappings();
		return new ArrayList<String>();
	}

	/*
	@Override
	public void arpRequestNotification(ArpMessage arpMessage) {
		log.debug("Received ARP notification from other instances");

		switch (arpMessage.getType()){
		case REQUEST:
			if(arpMessage.getOutSwitch() == -1 || arpMessage.getOutPort() == -1){	
				broadcastArpRequestOutMyEdge(arpMessage.getPacket(),
						arpMessage.getInSwitch(), arpMessage.getInPort());					
			}else{					
				sendArpRequestOutPort(arpMessage.getPacket(),arpMessage.getOutSwitch(),arpMessage.getOutPort());
				log.debug("OutSwitch in ARP request message is: {}; OutPort in ARP request message is: {}",arpMessage.getOutSwitch(),arpMessage.getOutPort());
			}
			break;
		case REPLY:
			log.debug("Received ARP reply notification for {}",
					arpMessage.getAddress());
			sendArpReplyToWaitingRequesters(arpMessage.getAddress(),arpMessage.getMAC());
			break;
		}
	}
	*/
	
	private void sendArpReplyToWaitingRequesters(InetAddress address, MACAddress mac) {
		log.debug("Sending ARP reply for {} to requesters", 
				address.getHostAddress());
		
		//See if anyone's waiting for this ARP reply
		Set<ArpRequest> requests = arpRequests.get(address);
		
		//Synchronize on the Multimap while using an iterator for one of the sets
		List<ArpRequest> requestsToSend = new ArrayList<ArpRequest>(requests.size());
		synchronized (arpRequests) {
			Iterator<ArpRequest> it = requests.iterator();
			while (it.hasNext()) {
				ArpRequest request = it.next();
				it.remove();
				requestsToSend.add(request);
			}
		}
		
		/*IDeviceObject deviceObject = deviceStorage.getDeviceByIP(
				InetAddresses.coerceToInteger(address));
		
		MACAddress mac = MACAddress.valueOf(deviceObject.getMACAddress());
		
		log.debug("Found {} at {} in network map", 
				address.getHostAddress(), mac);*/
		
		//Don't hold an ARP lock while dispatching requests
		for (ArpRequest request : requestsToSend) {
			request.dispatchReply(address, mac);
		}
	}

	@Override
	public void arpReplyEvent(ArpReplyNotification arpReply) {
		log.debug("Received ARP reply notification for {}",
				arpReply.getTargetAddress());
		sendArpReplyToWaitingRequesters(arpReply.getTargetAddress(), 
				arpReply.getTargetMacAddress());
	}

	@Override
	public void packetOutNotification(
			PacketOutNotification packetOutNotification) {
		
		if (packetOutNotification instanceof SinglePacketOutNotification) {
			SinglePacketOutNotification notification = 
					(SinglePacketOutNotification) packetOutNotification;
			sendArpRequestOutPort(notification.packet, notification.getOutSwitch(), 
					notification.getOutPort());
		}
		else if (packetOutNotification instanceof BroadcastPacketOutNotification) {
			BroadcastPacketOutNotification notification = 
					(BroadcastPacketOutNotification) packetOutNotification;
			broadcastArpRequestOutMyEdge(notification.packet, 
					notification.getInSwitch(), notification.getInPort());
		}
		else {
			log.warn("Unknown packet out notification received");
		}
	}
}
