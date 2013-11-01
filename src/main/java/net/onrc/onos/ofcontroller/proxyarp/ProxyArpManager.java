package net.onrc.onos.ofcontroller.proxyarp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
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
import net.floodlightcontroller.devicemanager.IDeviceService;
import net.floodlightcontroller.packet.ARP;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.topology.ITopologyService;
import net.floodlightcontroller.util.MACAddress;
import net.onrc.onos.ofcontroller.bgproute.Interface;
import net.onrc.onos.ofcontroller.core.IDeviceStorage;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IDeviceObject;
import net.onrc.onos.ofcontroller.core.config.IConfigInfoService;
import net.onrc.onos.ofcontroller.core.internal.DeviceStorageImpl;

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

public class ProxyArpManager implements IProxyArpService, IOFMessageListener {
	private final static Logger log = LoggerFactory.getLogger(ProxyArpManager.class);
	
	private final long ARP_TIMER_PERIOD = 60000; //ms (== 1 min) 
	
	private static final int ARP_REQUEST_TIMEOUT = 2000; //ms
			
	private IFloodlightProviderService floodlightProvider;
	private ITopologyService topology;
	private IDeviceService deviceService;
	private IConfigInfoService configService;
	private IRestApiService restApi;
	
	private IDeviceStorage deviceStorage;
	
	private short vlan;
	private static final short NO_VLAN = 0;
	
	private ArpCache arpCache;

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
	
	/*
	public ProxyArpManager(IFloodlightProviderService floodlightProvider,
				ITopologyService topology, IConfigInfoService configService,
				IRestApiService restApi){

	}
	*/
	
	public void init(IFloodlightProviderService floodlightProvider,
			ITopologyService topology, IDeviceService deviceService,
			IConfigInfoService config, IRestApiService restApi){
		this.floodlightProvider = floodlightProvider;
		this.topology = topology;
		this.deviceService = deviceService;
		this.configService = config;
		this.restApi = restApi;
		
		arpCache = new ArpCache();

		arpRequests = Multimaps.synchronizedSetMultimap(
				HashMultimap.<InetAddress, ArpRequest>create());
	}
	
	public void startUp() {
		this.vlan = configService.getVlan();
		log.info("vlan set to {}", this.vlan);
		
		restApi.addRestletRoutable(new ArpWebRoutable());
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
		
		deviceStorage = new DeviceStorageImpl();
		deviceStorage.init("");
		
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
			log.debug("Current have {} outstanding requests", 
					arpRequests.size());
			
			Iterator<Map.Entry<InetAddress, ArpRequest>> it 
				= arpRequests.entries().iterator();
			
			while (it.hasNext()) {
				Map.Entry<InetAddress, ArpRequest> entry
						= it.next();
				ArpRequest request = entry.getValue();
				if (request.isExpired()) {
					log.debug("Cleaning expired ARP request for {}", 
							entry.getKey().getHostAddress());
		
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
			return "devicemanager".equals(name);
		}
		else {
			return false;
		}
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		return false;
	}

	@Override
	public Command receive(
			IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
		
		if (msg.getType() != OFType.PACKET_IN){
			return Command.CONTINUE;
		}
		
		OFPacketIn pi = (OFPacketIn) msg;
		
		Ethernet eth = IFloodlightProviderService.bcStore.get(cntx, 
                IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
		
		if (eth.getEtherType() == Ethernet.TYPE_ARP){
			ARP arp = (ARP) eth.getPayload();
			
			if (arp.getOpCode() == ARP.OP_REQUEST) {
				//TODO check what the DeviceManager does about propagating
				//or swallowing ARPs. We want to go after DeviceManager in the
				//chain but we really need it to CONTINUE ARP packets so we can
				//get them.
				handleArpRequest(sw, pi, arp);
			}
			else if (arp.getOpCode() == ARP.OP_REPLY) {
				//handleArpReply(sw, pi, arp);
			}
		}
		
		//TODO should we propagate ARP or swallow it?
		//Always propagate for now so DeviceManager can learn the host location
		return Command.CONTINUE;
	}
	
	private void handleArpRequest(IOFSwitch sw, OFPacketIn pi, ARP arp) {
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
		
		//IDevice dstDevice = deviceService.fcStore.get(cntx, IDeviceService.CONTEXT_DST_DEVICE);
		//Iterator<? extends IDevice> it = deviceService.queryDevices(
				//null, null, InetAddresses.coerceToInteger(target), null, null);
		
		//IDevice targetDevice = null;
		//if (it.hasNext()) {
			//targetDevice = it.next();
		//}
		IDeviceObject targetDevice = 
				deviceStorage.getDeviceByIP(InetAddresses.coerceToInteger(target));
		
		if (targetDevice != null) {
			//We have the device in our database, so send a reply
			MACAddress macAddress = MACAddress.valueOf(targetDevice.getMACAddress());
			
			if (log.isTraceEnabled()) {
				log.trace("Sending reply: {} => {} to host at {}/{}", new Object [] {
						inetAddressToString(arp.getTargetProtocolAddress()),
						macAddress.toString(),
						HexString.toHexString(sw.getId()), pi.getInPort()});
			}
			
			sendArpReply(arp, sw.getId(), pi.getInPort(), macAddress);
		}
		
		/*if (macAddress == null){
			//MAC address is not in our ARP cache.
			
			//Record where the request came from so we know where to send the reply
			//arpRequests.put(target, new ArpRequest(
					//new HostArpRequester(arp, sw.getId(), pi.getInPort()), false));
						
			//Flood the request out edge ports
			//sendArpRequestToSwitches(target, pi.getPacketData(), sw.getId(), pi.getInPort());
		}
		else {
			//We know the address, so send a reply
			if (log.isTraceEnabled()) {
				log.trace("Sending reply: {} => {} to host at {}/{}", new Object [] {
						inetAddressToString(arp.getTargetProtocolAddress()),
						macAddress.toString(),
						HexString.toHexString(sw.getId()), pi.getInPort()});
			}
			
			sendArpReply(arp, sw.getId(), pi.getInPort(), macAddress);
		}*/
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
		
		arpCache.update(senderIpAddress, senderMacAddress);
		
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
		
		try {
			sw.write(po, null);
			sw.flush();
		} catch (IOException e) {
			log.error("Failure writing packet out to switch", e);
		}
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
		
		try {
			sw.write(msgList, null);
			sw.flush();
		} catch (IOException e) {
			log.error("Failure writing packet out to switch", e);
		}
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
		return arpCache.lookup(ipAddress);
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
		return arpCache.getMappings();
	}
}
