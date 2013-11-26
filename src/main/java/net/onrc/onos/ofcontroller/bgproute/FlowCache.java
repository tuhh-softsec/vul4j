package net.onrc.onos.ofcontroller.bgproute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFSwitch;

import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFPort;
import org.openflow.util.HexString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlowCache {
	private final static Logger log = LoggerFactory.getLogger(FlowCache.class);
	
	private IFloodlightProviderService floodlightProvider;
	
	private Map<Long, List<OFFlowMod>> flowCache;
	
	private Comparator<OFFlowMod> cookieComparator = new Comparator<OFFlowMod>() {
		@Override
		public int compare(OFFlowMod fm1, OFFlowMod fm2) {
			long difference = fm2.getCookie() - fm1.getCookie(); 
			
			if (difference > 0) {
				return 1;
			}
			else if (difference < 0) {
				return -1;
			}
			else {
				return 0;
			}
		}
	};
	
	public FlowCache(IFloodlightProviderService floodlightProvider) {
		this.floodlightProvider = floodlightProvider;
		
		flowCache = new HashMap<Long, List<OFFlowMod>>();
	}

	public synchronized void write(long dpid, OFFlowMod flowMod) {
		List<OFFlowMod> flowModList = new ArrayList<OFFlowMod>(1);
		flowModList.add(flowMod);
		write(dpid, flowModList);
	}
	
	public synchronized void write(long dpid, List<OFFlowMod> flowMods) {
		ensureCacheForSwitch(dpid);
		
		List<OFFlowMod> clones = new ArrayList<OFFlowMod>(flowMods.size());
		
		//Somehow the OFFlowMods we get passed in will change later on.
		//No idea how this happens, but we can just clone to prevent problems
		try {
			for (OFFlowMod fm : flowMods) {
				clones.add(fm.clone());
			}
		} catch (CloneNotSupportedException e) {
			log.debug("Clone exception", e);
		}
		
		flowCache.get(dpid).addAll(clones);
		
		IOFSwitch sw = floodlightProvider.getSwitches().get(dpid);
		
		if (sw == null) {
			log.debug("Switch not found when writing flow mods");
			return;
		}

		List<OFMessage> msgList = new ArrayList<OFMessage>(clones.size());
		msgList.addAll(clones);
		
		try {
			sw.write(msgList, null);
		} catch (IOException e) {
			log.error("Error writing to switch", e);
		}
		

	}
	
	public synchronized void delete(long dpid, OFFlowMod flowMod) {
		List<OFFlowMod> flowModList = new ArrayList<OFFlowMod>(1);
		flowModList.add(flowMod);
		delete(dpid, flowModList);
	}
	
	public synchronized void delete(long dpid, List<OFFlowMod> flowMods) {
		ensureCacheForSwitch(dpid);
		
		//Remove the flow mods from the cache first before we alter them
		flowCache.get(dpid).removeAll(flowMods);
		
		//Alter the original flow mods to make them delete flow mods
		for (OFFlowMod fm : flowMods) {
			fm.setCommand(OFFlowMod.OFPFC_DELETE_STRICT)
			.setOutPort(OFPort.OFPP_NONE)
			.setLengthU(OFFlowMod.MINIMUM_LENGTH);
			
			fm.getActions().clear();
		}
		
		IOFSwitch sw = floodlightProvider.getSwitches().get(dpid);
		if (sw == null) {
			log.debug("Switch not found when writing flow mods");
			return;
		}
		
		List<OFMessage> msgList = new ArrayList<OFMessage>(flowMods.size());
		msgList.addAll(flowMods);
		
		try {
			sw.write(msgList, null);
		} catch (IOException e) {
			log.error("Error writing to switch", e);
		}
	}
	
	//TODO can the Prontos handle being sent all flow mods in one message?
	public synchronized void switchConnected(IOFSwitch sw) {
		log.debug("Switch connected: {}", sw);
		
		ensureCacheForSwitch(sw.getId());
		
		List<OFFlowMod> flowMods = flowCache.get(sw.getId());

		Collections.sort(flowMods, cookieComparator);
		
		sw.clearAllFlowMods();
		
		List<OFMessage> messages = new ArrayList<OFMessage>(flowMods.size());
		messages.addAll(flowMods);
		
		try {
			sw.write(messages, null);
		} catch (IOException e) {
			log.error("Failure writing flow mods to switch {}",
					HexString.toHexString(sw.getId()));
		}		
	}
	
	private void ensureCacheForSwitch(long dpid) {
		if (!flowCache.containsKey(dpid)) {
			flowCache.put(dpid, new ArrayList<OFFlowMod>());
		}
	}
}
