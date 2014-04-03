package net.onrc.onos.ofcontroller.floodlightlistener;

import java.net.InetAddress;
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
import net.onrc.onos.core.main.IOFSwitchPortListener;
import net.onrc.onos.ofcontroller.devicemanager.IOnosDeviceListener;
import net.onrc.onos.ofcontroller.devicemanager.IOnosDeviceService;
import net.onrc.onos.ofcontroller.devicemanager.OnosDevice;
import net.onrc.onos.ofcontroller.linkdiscovery.ILinkDiscoveryListener;
import net.onrc.onos.ofcontroller.linkdiscovery.ILinkDiscoveryService;
import net.onrc.onos.ofcontroller.networkgraph.DeviceEvent;
import net.onrc.onos.ofcontroller.networkgraph.INetworkGraphService;
import net.onrc.onos.ofcontroller.networkgraph.LinkEvent;
import net.onrc.onos.ofcontroller.networkgraph.NetworkGraph;
import net.onrc.onos.ofcontroller.networkgraph.NetworkGraphDiscoveryInterface;
import net.onrc.onos.ofcontroller.networkgraph.PortEvent;
import net.onrc.onos.ofcontroller.networkgraph.PortEvent.SwitchPort;
import net.onrc.onos.ofcontroller.networkgraph.Switch;
import net.onrc.onos.ofcontroller.networkgraph.SwitchEvent;
import net.onrc.onos.registry.controller.IControllerRegistryService;
import net.onrc.onos.registry.controller.IControllerRegistryService.ControlChangeCallback;
import net.onrc.onos.registry.controller.RegistryException;

import org.openflow.protocol.OFPhysicalPort;
import org.openflow.util.HexString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.InetAddresses;

/**
 * The NetworkGraphPublisher subscribes to topology network events from the
 * discovery modules. These events are reformatted and relayed to the topology
 * part of the network graph
 *
 */
public class NetworkGraphPublisher implements /*IOFSwitchListener,*/
                                    IOFSwitchPortListener,
                                    ILinkDiscoveryListener,
                                    IFloodlightModule,
                                    IOnosDeviceListener {
    private static final Logger log =
            LoggerFactory.getLogger(NetworkGraphPublisher.class);

    private IFloodlightProviderService floodlightProvider;
    private ILinkDiscoveryService linkDiscovery;
    private IControllerRegistryService registryService;
    private INetworkGraphService networkGraphService;

    private IOnosDeviceService onosDeviceService;

    private NetworkGraph networkGraph;
    private NetworkGraphDiscoveryInterface networkGraphDiscoveryInterface;

    private static final String ENABLE_CLEANUP_PROPERTY = "EnableCleanup";
    private boolean cleanupEnabled = true;
    private static final int CLEANUP_TASK_INTERVAL = 60; // in seconds
    private SingletonTask cleanupTask;

    /**
     * Cleanup old switches from the network graph. Old switches are those
     * which have no controller in the registry.
     */
    private class SwitchCleanup implements ControlChangeCallback, Runnable {
        @Override
        public void run() {
            String old = Thread.currentThread().getName();
            Thread.currentThread().setName("SwitchCleanup@" + old);

            try {
                if (log.isTraceEnabled()) {
                    log.trace("Running cleanup thread");
                }
                switchCleanup();
            } finally {
                cleanupTask.reschedule(CLEANUP_TASK_INTERVAL,
                        TimeUnit.SECONDS);
                Thread.currentThread().setName(old);
            }
        }

        /**
         * First half of the switch cleanup operation. This method will attempt
         * to get control of any switch it sees without a controller via the
         * registry.
         */
        private void switchCleanup() {
            Iterable<Switch> switches = networkGraph.getSwitches();

            if (log.isTraceEnabled()) {
                log.trace("Checking for inactive switches");
            }
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

        /**
         * Second half of the switch cleanup operation. If the registry grants
         * control of a switch, we can be sure no other instance is writing
         * this switch to the network graph, so we can remove it now.
         * @param dpid the dpid of the switch we requested control for
         * @param hasControl whether we got control or not
         */
        @Override
        public void controlChanged(long dpid, boolean hasControl) {
            if (hasControl) {
                log.debug("Got control to set switch {} INACTIVE",
                        HexString.toHexString(dpid));

                SwitchEvent switchEvent = new SwitchEvent(dpid);
                networkGraphDiscoveryInterface.
                        removeSwitchDiscoveryEvent(switchEvent);
                registryService.releaseControl(dpid);
            }
        }
    }

    @Override
    public void linkDiscoveryUpdate(LDUpdate update) {
        LinkEvent linkEvent = new LinkEvent(update.getSrc(),
                (long) update.getSrcPort(), update.getDst(),
                (long) update.getDstPort());

        switch (update.getOperation()) {
        case LINK_ADDED:
            networkGraphDiscoveryInterface.putLinkDiscoveryEvent(linkEvent);
            break;
        case LINK_UPDATED:
            // We don't use the LINK_UPDATED event (unsure what it means)
            break;
        case LINK_REMOVED:
            networkGraphDiscoveryInterface.removeLinkDiscoveryEvent(linkEvent);
            break;
        default:
            break;
        }
    }

    @Override
    public void switchPortAdded(Long switchId, OFPhysicalPort port) {
        PortEvent portEvent = new PortEvent(switchId, (long) port.getPortNumber());
        networkGraphDiscoveryInterface.putPortDiscoveryEvent(portEvent);
        linkDiscovery.RemoveFromSuppressLLDPs(switchId, port.getPortNumber());
    }

    @Override
    public void switchPortRemoved(Long switchId, OFPhysicalPort port) {
        PortEvent portEvent = new PortEvent(switchId, (long) port.getPortNumber());
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
            portEvents.add(new PortEvent(sw.getId(), (long) port.getPortNumber()));
        }
        networkGraphDiscoveryInterface
        .putSwitchDiscoveryEvent(switchEvent, portEvents);

        for (OFPhysicalPort port : sw.getPorts()) {
            // Allow links to be discovered on this port now that it's
            // in the database
            linkDiscovery.RemoveFromSuppressLLDPs(sw.getId(), port.getPortNumber());
        }
    }

    @Override
    public void removedSwitch(IOFSwitch sw) {
        // We don't use this event - switch remove is done by cleanup thread
    }

    @Override
    public void switchPortChanged(Long switchId) {
        // We don't use this event
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
    public Collection<Class<? extends IFloodlightService>>
                getModuleDependencies() {
        Collection<Class<? extends IFloodlightService>> l =
                new ArrayList<Class<? extends IFloodlightService>>();
        l.add(IFloodlightProviderService.class);
        l.add(ILinkDiscoveryService.class);
        l.add(IThreadPoolService.class);
        l.add(IControllerRegistryService.class);
        l.add(INetworkGraphService.class);
        l.add(IOnosDeviceService.class);
        return l;
    }

    @Override
    public void init(FloodlightModuleContext context)
            throws FloodlightModuleException {
        floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
        linkDiscovery = context.getServiceImpl(ILinkDiscoveryService.class);
        registryService = context.getServiceImpl(IControllerRegistryService.class);
        onosDeviceService = context.getServiceImpl(IOnosDeviceService.class);

        networkGraphService = context.getServiceImpl(INetworkGraphService.class);
    }

    @Override
    public void startUp(FloodlightModuleContext context) {
        floodlightProvider.addOFSwitchListener(this);
        linkDiscovery.addListener(this);
        onosDeviceService.addOnosDeviceListener(this);

        networkGraph = networkGraphService.getNetworkGraph();
        networkGraphDiscoveryInterface =
                networkGraphService.getNetworkGraphDiscoveryInterface();

        // Run the cleanup thread
        String enableCleanup =
                context.getConfigParams(this).get(ENABLE_CLEANUP_PROPERTY);
        if (enableCleanup != null
                && enableCleanup.equalsIgnoreCase("false")) {
            cleanupEnabled = false;
        }

        log.debug("Cleanup thread is {}enabled", (cleanupEnabled) ? "" : "not ");

        if (cleanupEnabled) {
            IThreadPoolService threadPool =
                    context.getServiceImpl(IThreadPoolService.class);
            cleanupTask = new SingletonTask(threadPool.getScheduledExecutor(),
                    new SwitchCleanup());
            // Run the cleanup task immediately on startup
            cleanupTask.reschedule(0, TimeUnit.SECONDS);
        }
    }

    @Override
    public void onosDeviceAdded(OnosDevice device) {
        log.debug("Called onosDeviceAdded mac {}", device.getMacAddress());

        SwitchPort sp = new SwitchPort(device.getSwitchDPID(),
                (long) device.getSwitchPort());
        List<SwitchPort> spLists = new ArrayList<SwitchPort>();
        spLists.add(sp);
        DeviceEvent event = new DeviceEvent(device.getMacAddress());
        event.setAttachmentPoints(spLists);
        event.setLastSeenTime(device.getLastSeenTimestamp().getTime());
        if (device.getIpv4Address() != null) {
            InetAddress ip = InetAddresses.fromInteger(device.getIpv4Address());
            event.addIpAddress(ip);
        }
        // Does not use vlan info now.

        networkGraphDiscoveryInterface.putDeviceDiscoveryEvent(event);
    }

    @Override
    public void onosDeviceRemoved(OnosDevice device) {
        log.debug("Called onosDeviceRemoved");
        DeviceEvent event = new DeviceEvent(device.getMacAddress());
        networkGraphDiscoveryInterface.removeDeviceDiscoveryEvent(event);
    }
}
