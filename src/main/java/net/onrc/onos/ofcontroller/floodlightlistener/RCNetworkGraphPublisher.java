package net.onrc.onos.ofcontroller.floodlightlistener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.core.util.SingletonTask;
import net.floodlightcontroller.threadpool.IThreadPoolService;
import net.onrc.onos.datagrid.IDatagridService;
import net.onrc.onos.ofcontroller.core.IOFSwitchPortListener;
import net.onrc.onos.ofcontroller.linkdiscovery.ILinkDiscoveryListener;
import net.onrc.onos.ofcontroller.linkdiscovery.ILinkDiscoveryService;
import net.onrc.onos.ofcontroller.networkgraph.INetworkGraphService;
import net.onrc.onos.ofcontroller.networkgraph.LinkEvent;
import net.onrc.onos.ofcontroller.networkgraph.NetworkGraph;
import net.onrc.onos.ofcontroller.networkgraph.NetworkGraphDiscoveryInterface;
import net.onrc.onos.ofcontroller.networkgraph.PortEvent;
import net.onrc.onos.ofcontroller.networkgraph.Switch;
import net.onrc.onos.ofcontroller.networkgraph.SwitchEvent;
import net.onrc.onos.registry.controller.IControllerRegistryService;
import net.onrc.onos.registry.controller.IControllerRegistryService.ControlChangeCallback;
import net.onrc.onos.registry.controller.RegistryException;

import org.openflow.protocol.OFPhysicalPort;
import org.openflow.util.HexString;
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
	private NetworkGraphDiscoveryInterface networkGraphDiscoveryInterface;
	
	private static final String ENABLE_CLEANUP_PROPERTY = "EnableCleanup";
	private boolean cleanupEnabled = true;
	private static final int CLEANUP_TASK_INTERVAL = 60; // in seconds
	private SingletonTask cleanupTask;

    /**
     *  Cleanup and synch switch state from registry
     */
    private class SwitchCleanup implements ControlChangeCallback, Runnable {
        @Override
        public void run() {
            String old = Thread.currentThread().getName();
            Thread.currentThread().setName("SwitchCleanup@" + old);
            
            try {
            	log.debug("Running cleanup thread");
                switchCleanup();
            }
            catch (Exception e) {
                log.error("Error in cleanup thread", e);
            } finally {
                cleanupTask.reschedule(CLEANUP_TASK_INTERVAL,
                                          TimeUnit.SECONDS);
                Thread.currentThread().setName(old);
            }
        }
        
        private void switchCleanup() {
        	Iterable<Switch> switches = networkGraph.getSwitches();

        	log.debug("Checking for inactive switches");
        	// For each switch check if a controller exists in controller registry
        	for (Switch sw: switches) {
    			try {
    				String controller = 
    						registryService.getControllerForSwitch(sw.getDpid());
    				if (controller == null) {
    					log.debug("Requesting control to set switch {} INACTIVE", 
    							HexString.toHexString(sw.getDpid()));
    					registryService.requestControl(sw.getDpid(), this);
    				}
    			} catch (RegistryException e) {
    				log.error("Caught RegistryException in cleanup thread", e);
    			}
    		}
        }

		@Override
		public void controlChanged(long dpid, boolean hasControl) {
			if (hasControl) {
				log.debug("Got control to set switch {} INACTIVE", HexString.toHexString(dpid));
				/*
				// Get the affected ports
				List<Short> ports = swStore.getPorts(HexString.toHexString(dpid));
				// Get the affected links
				List<Link> links = linkStore.getLinks(HexString.toHexString(dpid));
				// Get the affected reverse links
				List<Link> reverseLinks = linkStore.getReverseLinks(HexString.toHexString(dpid));
				links.addAll(reverseLinks);
				*/
				SwitchEvent switchEvent = new SwitchEvent(dpid);
				networkGraphDiscoveryInterface.removeSwitchDiscoveryEvent(switchEvent);
			    registryService.releaseControl(dpid);

			    // TODO publish UPDATE_SWITCH event here
			    //
			    // NOTE: Here we explicitly send
			    // notification to remove the
			    // switch, because it is inactive
			    //
			    /*
			    TopologyElement topologyElement =
				new TopologyElement(dpid);
			    datagridService.notificationSendTopologyElementRemoved(topologyElement);

			    // Publish: remove the affected ports
			    for (Short port : ports) {
				TopologyElement topologyElementPort =
				    new TopologyElement(dpid, port);
				datagridService.notificationSendTopologyElementRemoved(topologyElementPort);
			    }
			    // Publish: remove the affected links
			    for (Link link : links) {
				TopologyElement topologyElementLink =
				    new TopologyElement(link.getSrc(),
							link.getSrcPort(),
							link.getDst(),
							link.getDstPort());
				datagridService.notificationSendTopologyElementRemoved(topologyElementLink);
			    }
			    */
			}
		}
    }

	@Override
	public void linkDiscoveryUpdate(LDUpdate update) {
		LinkEvent linkEvent = new LinkEvent(update.getSrc(), 
				(long)update.getSrcPort(), update.getDst(), 
				(long)update.getDstPort());
		
		switch (update.getOperation()) {
		case LINK_ADDED:
			networkGraphDiscoveryInterface.putLinkDiscoveryEvent(linkEvent);
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
			networkGraphDiscoveryInterface.removeLinkDiscoveryEvent(linkEvent);
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
		PortEvent portEvent = new PortEvent(switchId, (long)port.getPortNumber());
		networkGraphDiscoveryInterface.putPortDiscoveryEvent(portEvent);
		linkDiscovery.RemoveFromSuppressLLDPs(switchId, port.getPortNumber());
	}

	@Override
	public void switchPortRemoved(Long switchId, OFPhysicalPort port) {
		PortEvent portEvent = new PortEvent(switchId, (long)port.getPortNumber());
		networkGraphDiscoveryInterface.removePortDiscoveryEvent(portEvent);
	}

	@Override
	public void addedSwitch(IOFSwitch sw) {
		// TODO Not very robust
		if (!registryService.hasControl(sw.getId())) {
			return;
		}

		SwitchEvent switchEvent = new SwitchEvent(sw.getId());
		
		List<PortEvent> portEvents = new ArrayList<PortEvent>();
		for (OFPhysicalPort port : sw.getPorts()) {
			portEvents.add(new PortEvent(sw.getId(), (long)port.getPortNumber()));
		}
		networkGraphDiscoveryInterface.putSwitchDiscoveryEvent(switchEvent, portEvents);

		/*
		// TODO publish ADD_SWITCH event here
	    TopologyElement topologyElement =
		new TopologyElement(sw.getId());
	    datagridService.notificationSendTopologyElementAdded(topologyElement);
		*/
		
	    // Publish: add the ports
	    // TODO: Add only ports that are UP?
	    for (OFPhysicalPort port : sw.getPorts()) {
			//TopologyElement topologyElementPort =
			    //new TopologyElement(sw.getId(), port.getPortNumber());
			//datagridService.notificationSendTopologyElementAdded(topologyElementPort);

			// Allow links to be discovered on this port now that it's
			// in the database
			linkDiscovery.RemoveFromSuppressLLDPs(sw.getId(), port.getPortNumber());
	    }

	    /*
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
		// TODO move to cleanup thread
		//SwitchEvent switchEvent = new SwitchEvent(sw.getId());
		//networkGraphDiscoveryInterface.removeSwitchDiscoveryEvent(switchEvent);
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
        l.add(IThreadPoolService.class);
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
		networkGraphDiscoveryInterface = 
				networkGraphService.getNetworkGraphDiscoveryInterface();
		
		// Run the cleanup thread
		String enableCleanup = 
				context.getConfigParams(this).get(ENABLE_CLEANUP_PROPERTY);
		if (enableCleanup != null && enableCleanup.toLowerCase().equals("false")) {
			cleanupEnabled = false;
		}
		
		log.debug("Cleanup thread is {}enabled", (cleanupEnabled)? "" : "not ");
		
		if (cleanupEnabled) {
			IThreadPoolService threadPool = 
					context.getServiceImpl(IThreadPoolService.class);
			cleanupTask = new SingletonTask(threadPool.getScheduledExecutor(), 
					new SwitchCleanup());
			// Run the cleanup task immediately on startup
			cleanupTask.reschedule(0, TimeUnit.SECONDS);
		}
	}
}
