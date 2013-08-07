package net.onrc.onos.ofcontroller.proxyarp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
import net.floodlightcontroller.devicemanager.IDeviceService;
import net.floodlightcontroller.packet.ARP;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.topology.ITopologyService;
import net.floodlightcontroller.util.MACAddress;

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

//TODO have L2 and also L3 mode, where it takes into account interface addresses
public class ProxyArpManager implements IProxyArpService, IOFMessageListener {
	private static Logger log = LoggerFactory.getLogger(ProxyArpManager.class);
	
	private final long ARP_ENTRY_TIMEOUT = 600000; //ms (== 10 mins)
	
	private final long ARP_TIMER_PERIOD = 60000; //ms (== 1 min) 
			
	protected IFloodlightProviderService floodlightProvider;
	protected ITopologyService topology;
	protected IDeviceService devices;
	
	protected Map<InetAddress, ArpTableEntry> arpTable;

	protected SetMultimap<InetAddress, ArpRequest> arpRequests;
	
	private class ArpRequest {
		private IArpRequester requester;
		private boolean retry;
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
			return (System.currentTimeMillis() - requestTime) 
					> IProxyArpService.ARP_REQUEST_TIMEOUT;
		}
		
		public boolean shouldRetry() {
			return retry;
		}
		
		public void dispatchReply(InetAddress ipAddress, byte[] replyMacAddress) {
			log.debug("Dispatching reply for {} to {}", ipAddress.getHostAddress(), 
					requester);
			requester.arpResponse(ipAddress, replyMacAddress);
		}
	}
	
	public ProxyArpManager(IFloodlightProviderService floodlightProvider,
				ITopologyService topology, IDeviceService devices){
		this.floodlightProvider = floodlightProvider;
		this.topology = topology;
		this.devices = devices;
		
		arpTable = new HashMap<InetAddress, ArpTableEntry>();

		arpRequests = Multimaps.synchronizedSetMultimap(
				HashMultimap.<InetAddress, ArpRequest>create());
		
		Timer arpTimer = new Timer();
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
		return "ProxyArpManager";
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		return false;
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
				handleArpRequest(sw, pi, arp);
			}
			else if (arp.getOpCode() == ARP.OP_REPLY) {
				handleArpReply(sw, pi, arp);
			}
		}
		
		//TODO should we propagate ARP or swallow it?
		//Always propagate for now so DeviceManager can learn the host location
		return Command.CONTINUE;
	}
	
	protected void handleArpRequest(IOFSwitch sw, OFPacketIn pi, ARP arp) {
		log.debug("ARP request received for {}", 
				bytesToStringAddr(arp.getTargetProtocolAddress()));
		
		byte[] mac = lookupArpTable(arp.getTargetProtocolAddress());
		
		if (mac == null){
			//Mac address is not in our arp table.
			
			//TODO check what the DeviceManager thinks
			
			//Record where the request came from so we know where to send the reply
			InetAddress target;
			try {
				 target = InetAddress.getByAddress(arp.getTargetProtocolAddress());
			} catch (UnknownHostException e) {
				log.debug("Invalid address in ARP request", e);
				//return Command.CONTINUE; //Continue or stop?
				return;
			}
			
			boolean shouldBroadcastRequest = false;
			synchronized (arpRequests) {
				if (!arpRequests.containsKey(target)) {
					shouldBroadcastRequest = true;
				}
				arpRequests.put(target, new ArpRequest(
						new HostArpRequester(this, arp, sw.getId(), pi.getInPort()), false));
			}
						
			//Flood the request out edge ports
			if (shouldBroadcastRequest) {
				broadcastArpRequestOutEdge(pi.getPacketData(), sw.getId(), pi.getInPort());
			}
		}
		else {
			//We know the address, so send a reply
			log.debug("Sending reply of {}", MACAddress.valueOf(mac).toString());
			sendArpReply(arp, sw.getId(), pi.getInPort(), mac);
		}
	}
	
	protected void handleArpReply(IOFSwitch sw, OFPacketIn pi, ARP arp){
		log.debug("ARP reply recieved for {}, is {}, on {}/{}", new Object[] { 
				bytesToStringAddr(arp.getSenderProtocolAddress()),
				HexString.toHexString(arp.getSenderHardwareAddress()),
				HexString.toHexString(sw.getId()), pi.getInPort()});
		
		updateArpTable(arp);
		
		//See if anyone's waiting for this ARP reply
		InetAddress addr;
		try {
			addr = InetAddress.getByAddress(arp.getSenderProtocolAddress());
		} catch (UnknownHostException e) {
			return;
		}
		
		Set<ArpRequest> requests = arpRequests.get(addr);
		
		//Synchronize on the Multimap while using an iterator for one of the sets
		synchronized (arpRequests) {
			Iterator<ArpRequest> it = requests.iterator();
			while (it.hasNext()) {
				ArpRequest request = it.next();
				it.remove();
				request.dispatchReply(addr, arp.getSenderHardwareAddress());
			}
		}
	}

	private synchronized byte[] lookupArpTable(byte[] ipAddress){
		InetAddress addr;
		try {
			addr = InetAddress.getByAddress(ipAddress);
		} catch (UnknownHostException e) {
			log.warn("Unable to create InetAddress", e);
			return null;
		}
		
		ArpTableEntry arpEntry = arpTable.get(addr);
		
		if (arpEntry == null){
			log.debug("MAC for {} unknown", bytesToStringAddr(ipAddress));
			return null;
		}
		
		if (System.currentTimeMillis() - arpEntry.getTimeLastSeen() 
				> ARP_ENTRY_TIMEOUT){
			//Entry has timed out so we'll remove it and return null
			log.debug("Timing out old ARP entry for {}", bytesToStringAddr(ipAddress));
			arpTable.remove(addr);
			return null;
		}
		
		return arpEntry.getMacAddress();
	}
	
	private synchronized void updateArpTable(ARP arp){
		InetAddress addr;
		try {
			addr = InetAddress.getByAddress(arp.getSenderProtocolAddress());
		} catch (UnknownHostException e) {
			log.warn("Unable to create InetAddress", e);
			return;
		}
		
		ArpTableEntry arpEntry = arpTable.get(addr);
		
		if (arpEntry != null 
				&& arpEntry.getMacAddress() == arp.getSenderHardwareAddress()){
			arpEntry.setTimeLastSeen(System.currentTimeMillis());
		}
		else {
			arpTable.put(addr, 
					new ArpTableEntry(arp.getSenderHardwareAddress(), 
										System.currentTimeMillis()));
		}
	}
	
	private void sendArpRequestForAddress(InetAddress ipAddress) {
		//TODO what should the sender IP address be? Probably not 0.0.0.0
		byte[] zeroIpv4 = {0x0, 0x0, 0x0, 0x0};
		byte[] zeroMac = {0x0, 0x0, 0x0, 0x0, 0x0, 0x0};
		byte[] bgpdMac = {0x0, 0x0, 0x0, 0x0, 0x0, 0x01};
		byte[] broadcastMac = {(byte)0xff, (byte)0xff, (byte)0xff, 
				(byte)0xff, (byte)0xff, (byte)0xff};
		
		ARP arpRequest = new ARP();
		
		arpRequest.setHardwareType(ARP.HW_TYPE_ETHERNET)
			.setProtocolType(ARP.PROTO_TYPE_IP)
			.setHardwareAddressLength((byte)Ethernet.DATALAYER_ADDRESS_LENGTH)
			.setProtocolAddressLength((byte)4) //can't find the constant anywhere
			.setOpCode(ARP.OP_REQUEST)
			.setSenderHardwareAddress(bgpdMac)
			.setSenderProtocolAddress(zeroIpv4)
			.setTargetHardwareAddress(zeroMac)
			.setTargetProtocolAddress(ipAddress.getAddress());
	
		Ethernet eth = new Ethernet();
		//eth.setDestinationMACAddress(arpRequest.getSenderHardwareAddress())
		eth.setSourceMACAddress(bgpdMac)
			.setDestinationMACAddress(broadcastMac)
			.setEtherType(Ethernet.TYPE_ARP)
			.setPayload(arpRequest);
		
		broadcastArpRequestOutEdge(eth.serialize(), 0, OFPort.OFPP_NONE.getValue());
	}
	
	private void broadcastArpRequestOutEdge(byte[] arpRequest, long inSwitch, short inPort) {
		for (IOFSwitch sw : floodlightProvider.getSwitches().values()){
			Collection<Short> enabledPorts = sw.getEnabledPortNumbers();
			Set<Short> linkPorts = topology.getPortsWithLinks(sw.getId());
			
			if (linkPorts == null){
				//I think this means the switch isn't known to topology yet.
				//Maybe it only just joined.
				continue;
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
				//log.debug("Broadcasting out {}/{}", HexString.toHexString(sw.getId()), portNum);
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
	
	public void sendArpReply(ARP arpRequest, long dpid, short port, byte[] targetMac) {
		ARP arpReply = new ARP();
		arpReply.setHardwareType(ARP.HW_TYPE_ETHERNET)
			.setProtocolType(ARP.PROTO_TYPE_IP)
			.setHardwareAddressLength((byte)Ethernet.DATALAYER_ADDRESS_LENGTH)
			.setProtocolAddressLength((byte)4) //can't find the constant anywhere
			.setOpCode(ARP.OP_REPLY)
			.setSenderHardwareAddress(targetMac)
			.setSenderProtocolAddress(arpRequest.getTargetProtocolAddress())
			.setTargetHardwareAddress(arpRequest.getSenderHardwareAddress())
			.setTargetProtocolAddress(arpRequest.getSenderProtocolAddress());
		
		Ethernet eth = new Ethernet();
		eth.setDestinationMACAddress(arpRequest.getSenderHardwareAddress())
			.setSourceMACAddress(targetMac)
			.setEtherType(Ethernet.TYPE_ARP)
			.setPayload(arpReply);
		
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
			return;
		}
		
		try {
			log.debug("Sending ARP reply to {}/{}", HexString.toHexString(sw.getId()), port);
			sw.write(msgList, null);
			sw.flush();
		} catch (IOException e) {
			log.warn("Failure writing packet out to switch", e);
		}
	}

	//TODO this should be put somewhere more central. I use it in BgpRoute as well.
	//We need a HexString.toHexString() equivalent.
	private String bytesToStringAddr(byte[] bytes) {
		InetAddress addr;
		try {
			addr = InetAddress.getByAddress(bytes);
		} catch (UnknownHostException e) {
			log.warn(" ", e);
			return "";
		}
		if (addr == null) return "";
		else return addr.getHostAddress();
	}
	
	@Override
	public byte[] getMacAddress(InetAddress ipAddress) {
		return lookupArpTable(ipAddress.getAddress());
	}

	@Override
	public void sendArpRequest(InetAddress ipAddress, IArpRequester requester,
			boolean retry) {
		arpRequests.put(ipAddress, new ArpRequest(requester, retry));
		//storeRequester(ipAddress, requester, retry);
		
		sendArpRequestForAddress(ipAddress);
	}
}
