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
import java.util.concurrent.ConcurrentHashMap;

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

public class ProxyArpManager implements IProxyArpService, IOFMessageListener {
	private static Logger log = LoggerFactory.getLogger(ProxyArpManager.class);
	
	private final long ARP_ENTRY_TIMEOUT = 600000; //ms (== 10 mins)
	
	private final long ARP_REQUEST_TIMEOUT_THREAD_PERIOD = 60000; //ms (== 1 min) 
			
	protected IFloodlightProviderService floodlightProvider;
	protected ITopologyService topology;
	protected IDeviceService devices;
	
	protected Map<InetAddress, ArpTableEntry> arpTable;
	
	//protected ConcurrentHashMap<InetAddress, Set<ArpRequest>> arpRequests;
	protected ConcurrentHashMap<InetAddress, ArpRequest> arpRequests;
	
	private class ArpRequest {
		private Set<IArpRequester> requesters;
		private long requestTime;
		
		public ArpRequest(){
			this.requesters = new HashSet<IArpRequester>();
			this.requestTime = System.currentTimeMillis();
		}
		
		public synchronized void addRequester(IArpRequester requester){
			requestTime = System.currentTimeMillis();
			requesters.add(requester);
		}
		
		public boolean isExpired(){
			return (System.currentTimeMillis() - requestTime) 
					> IProxyArpService.ARP_REQUEST_TIMEOUT;
		}
		
		public synchronized void dispatchReply(byte[] replyMacAddress){
			for (IArpRequester requester : requesters){
				requester.arpResponse(replyMacAddress);
			}
		}
	}
	
	public ProxyArpManager(IFloodlightProviderService floodlightProvider,
				ITopologyService topology, IDeviceService devices){
		this.floodlightProvider = floodlightProvider;
		this.topology = topology;
		this.devices = devices;
		
		arpTable = new HashMap<InetAddress, ArpTableEntry>();
		//arpRequests = new ConcurrentHashMap<InetAddress, Set<ArpRequest>>();
		arpRequests = new ConcurrentHashMap<InetAddress, ArpRequest>();
		
		Timer arpRequestTimeoutTimer = new Timer();
		arpRequestTimeoutTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				synchronized (arpRequests) {
					log.debug("Current have {} outstanding requests", 
							arpRequests.size());
					
					Iterator<Map.Entry<InetAddress, ArpRequest>> it 
							= arpRequests.entrySet().iterator();
					
					while (it.hasNext()){
						Map.Entry<InetAddress, ArpRequest> entry
								= it.next();
						
						if (entry.getValue().isExpired()){
							log.debug("Cleaning expired ARP request for {}", 
									entry.getKey().getHostAddress());
							it.remove();
						}
					}
				}
			}
		}, 0, ARP_REQUEST_TIMEOUT_THREAD_PERIOD);
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
			
			synchronized (arpRequests) {
				//arpRequests.putIfAbsent(target, 
						//Collections.synchronizedSet(new HashSet<ArpRequest>()));
				//		new ArpRequest());
				//Set<ArpRequest> requesters = arpRequests.get(target);
				if (arpRequests.get(target) == null) {
					arpRequests.put(target, new ArpRequest());
				}
				ArpRequest request = arpRequests.get(target);
								
				request.addRequester(new HostArpRequester(this, arp, 
						sw.getId(), pi.getInPort()));
			}
			
			
			//Flood the request out edge ports
			broadcastArpRequestOutEdge(pi, sw.getId(), pi.getInPort());
		}
		else {
			//We know the address, so send a reply
			log.debug("Sending reply of {}", MACAddress.valueOf(mac).toString());
			//sendArpReply(arp, pi, mac, sw);
			sendArpReply(arp, sw.getId(), pi.getInPort(), mac);
		}
	}
	
	protected void handleArpReply(IOFSwitch sw, OFPacketIn pi, ARP arp){
		log.debug("ARP reply recieved for {}", 
				bytesToStringAddr(arp.getSenderProtocolAddress()));
		
		updateArpTable(arp);
		
		//See if anyone's waiting for this ARP reply
		InetAddress addr;
		try {
			addr = InetAddress.getByAddress(arp.getSenderProtocolAddress());
		} catch (UnknownHostException e) {
			return;
		}
		
		ArpRequest request = null;
		synchronized (arpRequests) {
			request = arpRequests.get(addr);
			if (request != null) {
				arpRequests.remove(addr);
			}
		}
		if (request != null && !request.isExpired()) {
			request.dispatchReply(arp.getSenderHardwareAddress());
		}
		
		/*
		Set<ArpRequest> requests = arpRequests.get(addr);
		if (requests != null){
			
			synchronized (requests) {
				for (ArpRequest request : requests) {
					if (!request.isExpired()){
						request.getRequester().arpResponse(
								arp.getSenderHardwareAddress());
					}
				}
			}
		}*/
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
			return null;
		}
		
		if (System.currentTimeMillis() - arpEntry.getTimeLastSeen() 
				> ARP_ENTRY_TIMEOUT){
			//Entry has timed out so we'll remove it and return null
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
	
	private void broadcastArpRequestOutEdge(OFPacketIn pi, long inSwitch, short inPort){
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
				.setPacketData(pi.getPacketData());
				
			List<OFAction> actions = new ArrayList<OFAction>();
			
			for (short portNum : enabledPorts){
				if (linkPorts.contains(portNum) || 
						(sw.getId() == inSwitch && portNum == inPort)){
					//If this port isn't an edge port or is the ingress port
					//for the ARP, don't broadcast out it
					continue;
				}
				
				actions.add(new OFActionOutput(portNum));
				log.debug("Broadcasting out {}/{}", HexString.toHexString(sw.getId()), portNum);
			}
			
			po.setActions(actions);
			short actionsLength = (short) (actions.size() * OFActionOutput.MINIMUM_LENGTH);
			po.setActionsLength(actionsLength);
			po.setLengthU(OFPacketOut.MINIMUM_LENGTH + actionsLength 
					+ pi.getPacketData().length);
			
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
	//private void sendArpReply(ARP arpRequest, OFPacketIn pi, byte[] macRequested, IOFSwitch sw){
		ARP arpReply = new ARP();
		arpReply.setHardwareType(ARP.HW_TYPE_ETHERNET)
			.setProtocolType(ARP.PROTO_TYPE_IP)
			.setHardwareAddressLength((byte)Ethernet.DATALAYER_ADDRESS_LENGTH)
			.setProtocolAddressLength((byte)4) //can't find the constant anywhere
			.setOpCode(ARP.OP_REPLY)
			//.setSenderHardwareAddress(macRequested)
			.setSenderHardwareAddress(targetMac)
			.setSenderProtocolAddress(arpRequest.getTargetProtocolAddress())
			.setTargetHardwareAddress(arpRequest.getSenderHardwareAddress())
			.setTargetProtocolAddress(arpRequest.getSenderProtocolAddress());
		
		Ethernet eth = new Ethernet();
		eth.setDestinationMACAddress(arpRequest.getSenderHardwareAddress())
			//.setSourceMACAddress(macRequested)
			.setSourceMACAddress(targetMac)
			.setEtherType(Ethernet.TYPE_ARP)
			.setPayload(arpReply);
		
		List<OFAction> actions = new ArrayList<OFAction>();
		//actions.add(new OFActionOutput(pi.getInPort()));
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
	private String bytesToStringAddr(byte[] bytes){
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
	
	
	public byte[] lookupMac(InetAddress ipAddress){
		//TODO implement
		return null;
	}
	public byte[] sendArpRequest(InetAddress ipAddress, IArpRequester requester){
		//TODO implement
		return null;
	}
}
