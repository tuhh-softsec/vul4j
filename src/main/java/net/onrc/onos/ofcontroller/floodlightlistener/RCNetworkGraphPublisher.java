package net.onrc.onos.ofcontroller.floodlightlistener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.onrc.onos.datagrid.IDatagridService;
import net.onrc.onos.ofcontroller.core.IOFSwitchPortListener;
import net.onrc.onos.ofcontroller.linkdiscovery.ILinkDiscoveryListener;
import net.onrc.onos.ofcontroller.linkdiscovery.ILinkDiscoveryService;
import net.onrc.onos.ofcontroller.networkgraph.FloodlightToOnosMappers;
import net.onrc.onos.ofcontroller.networkgraph.INetworkGraphService;
import net.onrc.onos.ofcontroller.networkgraph.LinkImpl;
import net.onrc.onos.ofcontroller.networkgraph.NetworkGraph;
import net.onrc.onos.ofcontroller.networkgraph.SouthboundNetworkGraph;
import net.onrc.onos.ofcontroller.networkgraph.Switch;
import net.onrc.onos.ofcontroller.util.Dpid;
import net.onrc.onos.registry.controller.IControllerRegistryService;

import org.openflow.protocol.OFPhysicalPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * I've created a copy of the NetworkGraphPublisher so I can integrate
 * the new API with ONOS while still having the old NetworkGraphPublisher
 * to reference. I've renamed to RCNetworkGraphPublisher.
 * TODO Remove old NetworkGraphPublisher once the integration of the new
 * API is complete.
 * For now, we just write to the database and don't worry about sending
 * notifications.
 * TODO Send notification after each database write
 */
public class RCNetworkGraphPublisher implements /*IOFSwitchListener,*/
												IOFSwitchPortListener,
												ILinkDiscoveryListener,
												IFloodlightModule {
	private static final Logger log = LoggerFactory.getLogger(RCNetworkGraphPublisher.class);
	
	private IFloodlightProviderService floodlightProvider;
	private ILinkDiscoveryService linkDiscovery;
	private IControllerRegistryService registryService;
	private IDatagridService datagridService;
	private INetworkGraphService networkGraphService;

	private NetworkGraph networkGraph;
	private SouthboundNetworkGraph southboundNetworkGraph;


	@Override
	public void linkDiscoveryUpdate(LDUpdate update) {

		// TODO Move this sanity check when retrieving port to common place?
		Switch srcSw = networkGraph.getSwitch(update.getSrc());
		if (srcSw == null) {
			log.error("Switch {} missing when adding Link {}",
					new Dpid(update.getSrc()), update);
			return;
		}

		Switch dstSw = networkGraph.getSwitch(update.getDst());
		if (dstSw == null) {
			log.error("Switch {} missing when adding Link {}",
					new Dpid(update.getDst()), update);
			return;
		}

		// XXX Is it correct to add Link object created with networkGraph to southboundNetworkGraph?
		LinkImpl link = new LinkImpl(networkGraph,
			srcSw.getPort((long) update.getSrcPort()),
			dstSw.getPort((long) update.getDstPort()));

		switch (update.getOperation()) {
		case LINK_ADDED:
			southboundNetworkGraph.addLink(link);
			/*
			TopologyElement topologyElement =
					new TopologyElement(update.getSrc(),
							update.getSrcPort(),
							update.getDst(),
							update.getDstPort());
			datagridService.notificationSendTopologyElementAdded(topologyElement);
			*/
			break;
		case LINK_UPDATED:
			// I don't know what a LINK_UPDATED event is.
			// We never use it.
			break;
		case LINK_REMOVED:
			southboundNetworkGraph.removeLink(link);
			/*
			TopologyElement topologyElement =
					new TopologyElement(update.getSrc(),
							update.getSrcPort(),
							update.getDst(),
							update.getDstPort());
			datagridService.notificationSendTopologyElementRemoved(topologyElement);
			*/
			break;
		default:
			break;
		}
	}

	@Override
	public void switchPortAdded(Long switchId, OFPhysicalPort port) {
		// TODO Auto-generated method stub

	}

	@Override
	public void switchPortRemoved(Long switchId, OFPhysicalPort port) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addedSwitch(IOFSwitch sw) {
		// TODO Not very robust
		if (!registryService.hasControl(sw.getId())) {
			return;
		}

		Switch onosSwitch = FloodlightToOnosMappers.map(networkGraph, sw);
		southboundNetworkGraph.addSwitch(onosSwitch);

		/*
		// TODO publish ADD_SWITCH event here
	    TopologyElement topologyElement =
		new TopologyElement(sw.getId());
	    datagridService.notificationSendTopologyElementAdded(topologyElement);

	    // Publish: add the ports
	    // TODO: Add only ports that are UP?
	    for (OFPhysicalPort port : sw.getPorts()) {
			TopologyElement topologyElementPort =
			    new TopologyElement(sw.getId(), port.getPortNumber());
			datagridService.notificationSendTopologyElementAdded(topologyElementPort);

			// Allow links to be discovered on this port now that it's
			// in the database
			linkDiscovery.RemoveFromSuppressLLDPs(sw.getId(), port.getPortNumber());
	    }

	    // Add all links that might be connected already
	    List<Link> links = linkStore.getLinks(HexString.toHexString(sw.getId()));
	    // Add all reverse links as well
	    List<Link> reverseLinks = linkStore.getReverseLinks(HexString.toHexString(sw.getId()));
	    links.addAll(reverseLinks);

	    // Publish: add the links
	    for (Link link : links) {
		TopologyElement topologyElementLink =
		    new TopologyElement(link.getSrc(),
					link.getSrcPort(),
					link.getDst(),
					link.getDstPort());
		datagridService.notificationSendTopologyElementAdded(topologyElementLink);
		*/
	}

	@Override
	public void removedSwitch(IOFSwitch sw) {
		// TODO Auto-generated method stub

	}

	@Override
	public void switchPortChanged(Long switchId) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* *****************
	 * IFloodlightModule
	 * *****************/

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		return null;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService>
			getServiceImpls() {
		return null;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		Collection<Class<? extends IFloodlightService>> l =
	            new ArrayList<Class<? extends IFloodlightService>>();
        l.add(IFloodlightProviderService.class);
        l.add(ILinkDiscoveryService.class);
        l.add(IControllerRegistryService.class);
        l.add(IDatagridService.class);
        l.add(INetworkGraphService.class);
        return l;
	}

	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException {
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
		linkDiscovery = context.getServiceImpl(ILinkDiscoveryService.class);
		registryService = context.getServiceImpl(IControllerRegistryService.class);
		datagridService = context.getServiceImpl(IDatagridService.class);

		networkGraphService = context.getServiceImpl(INetworkGraphService.class);
	}

	@Override
	public void startUp(FloodlightModuleContext context) {
		// TODO enable cleanup thread
		floodlightProvider.addOFSwitchListener(this);
		linkDiscovery.addListener(this);

		networkGraph = networkGraphService.getNetworkGraph();
		southboundNetworkGraph = networkGraphService.getSouthboundNetworkGraph();
	}
}
