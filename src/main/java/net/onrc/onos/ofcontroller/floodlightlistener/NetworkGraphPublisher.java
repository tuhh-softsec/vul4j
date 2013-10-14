package net.onrc.onos.ofcontroller.floodlightlistener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.openflow.protocol.OFPhysicalPort;
import org.openflow.util.HexString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.IOFSwitchListener;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.core.util.SingletonTask;
import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.IDeviceListener;
import net.floodlightcontroller.devicemanager.IDeviceService;
import net.floodlightcontroller.routing.Link;
import net.floodlightcontroller.threadpool.IThreadPoolService;
import net.onrc.onos.graph.GraphDBConnection;
import net.onrc.onos.graph.GraphDBOperation;
import net.onrc.onos.graph.IDBConnection;
import net.onrc.onos.graph.LocalTopologyEventListener;
import net.onrc.onos.ofcontroller.core.IDeviceStorage;
import net.onrc.onos.ofcontroller.core.ILinkStorage;
import net.onrc.onos.ofcontroller.core.IOFSwitchPortListener;
import net.onrc.onos.ofcontroller.core.ISwitchStorage;
import net.onrc.onos.ofcontroller.core.INetMapStorage.DM_OPERATION;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.onrc.onos.ofcontroller.core.ISwitchStorage.SwitchState;
import net.onrc.onos.ofcontroller.core.internal.DeviceStorageImpl;
import net.onrc.onos.ofcontroller.core.internal.LinkStorageImpl;
import net.onrc.onos.ofcontroller.core.internal.SwitchStorageImpl;
import net.onrc.onos.ofcontroller.linkdiscovery.ILinkDiscoveryListener;
import net.onrc.onos.ofcontroller.linkdiscovery.ILinkDiscoveryService;
import net.onrc.onos.registry.controller.IControllerRegistryService;
import net.onrc.onos.registry.controller.IControllerRegistryService.ControlChangeCallback;
import net.onrc.onos.registry.controller.RegistryException;

public class NetworkGraphPublisher implements IDeviceListener, IOFSwitchListener, IOFSwitchPortListener,
		ILinkDiscoveryListener, IFloodlightModule {
	
	protected IDeviceStorage devStore;
	protected ISwitchStorage swStore;
	protected ILinkStorage linkStore;
	protected static Logger log;
	protected IDeviceService deviceService;
	protected IControllerRegistryService registryService;
	protected GraphDBOperation op;
	
	protected static final String DBConfigFile = "dbconf";
	protected static final String CleanupEnabled = "EnableCleanup";
	protected IThreadPoolService threadPool;
	protected IFloodlightProviderService floodlightProvider;
	
	protected final int CLEANUP_TASK_INTERVAL = 60; // 1 min
	protected SingletonTask cleanupTask;
	protected ILinkDiscoveryService linkDiscovery;
	
	/**
     *  Cleanup and synch switch state from registry
     */
    protected class SwitchCleanup implements ControlChangeCallback, Runnable {
        @Override
        public void run() {
            try {
            	log.debug("Running cleanup thread");
                switchCleanup();
            }
            catch (Exception e) {
                log.error("Error in cleanup thread", e);
            } finally {
            	op.close();
                    cleanupTask.reschedule(CLEANUP_TASK_INTERVAL,
                                              TimeUnit.SECONDS);
            }
        }

		@Override
		public void controlChanged(long dpid, boolean hasControl) {
			// TODO Auto-generated method stub
			
			if (hasControl) {
				log.debug("got control to set inactive sw {}", HexString.toHexString(dpid));
				swStore.update(HexString.toHexString(dpid),SwitchState.INACTIVE, DM_OPERATION.UPDATE);
			    registryService.releaseControl(dpid);	
			}						
		}
    }

    protected void switchCleanup() {
    	op.close();
    	Iterable<ISwitchObject> switches = op.getActiveSwitches();
    	
    	log.debug("Checking for inactive switches");
    	// For each switch check if a controller exists in controller registry
    	for (ISwitchObject sw: switches) {
			//log.debug("checking if switch is inactive: {}", sw.getDPID());
			try {
				long dpid = HexString.toLong(sw.getDPID());
				String controller = registryService.getControllerForSwitch(dpid);
				if (controller == null) {
					log.debug("request Control to set inactive sw {}", HexString.toHexString(dpid));
					registryService.requestControl(dpid, new SwitchCleanup());
				//} else {
				//	log.debug("sw {} is controlled by controller: {}",HexString.toHexString(dpid),controller);
				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RegistryException e) {
				log.debug("Caught RegistryException trying to requestControl in cleanup thread");
				e.printStackTrace();
			}			
		}
    	op.close();
    }

	@Override
	public void linkDiscoveryUpdate(LDUpdate update) {
		// TODO Auto-generated method stub
		Link lt = new Link(update.getSrc(),update.getSrcPort(),update.getDst(),update.getDstPort());
		//log.debug("{}:LinkDicoveryUpdate(): Updating Link {}",this.getClass(), lt);
		
		switch (update.getOperation()) {
			case LINK_REMOVED:
				log.debug("LinkDiscoveryUpdate(): Removing link {}", lt);
				linkStore.update(lt, DM_OPERATION.DELETE);
				// TODO: Move network map link removal here
				// reconcile paths here
//				IPortObject srcPort = conn.utils().searchPort(conn, HexString.toHexString(update.getSrc()), update.getSrcPort());
				break;
			case LINK_UPDATED:
				log.debug("LinkDiscoveryUpdate(): Updating link {}", lt);
				linkStore.update(lt, DM_OPERATION.UPDATE);
				break;
			case LINK_ADDED:
				log.debug("LinkDiscoveryUpdate(): Adding link {}", lt);
				linkStore.update(lt, DM_OPERATION.INSERT);
				break;
					
			default:
				break;
		}

	}

	@Override
	public void addedSwitch(IOFSwitch sw) {

		if (registryService.hasControl(sw.getId())) {
	        	swStore.addSwitch(sw);
		}

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
	public void switchPortAdded(Long switchId, OFPhysicalPort port) {
		// TODO Auto-generated method stub
		swStore.addPort(HexString.toHexString(switchId), port);
	}

	@Override
	public void switchPortRemoved(Long switchId, OFPhysicalPort port) {
		// TODO Auto-generated method stub
		swStore.deletePort(HexString.toHexString(switchId), port.getPortNumber());		
	}

	@Override
	public String getName() {
		return "NetworkGraphPublisher";
	}

	@Override
	public void deviceAdded(IDevice device) {
		// TODO Auto-generated method stub
		log.debug("{}:deviceAdded(): Adding device {}",this.getClass(),device.getMACAddressString());
		devStore.addDevice(device);
	}

	@Override
	public void deviceRemoved(IDevice device) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deviceMoved(IDevice device) {
		// TODO Auto-generated method stub
		devStore.changeDeviceAttachments(device);

	}

	@Override
	public void deviceIPV4AddrChanged(IDevice device) {
		// TODO Auto-generated method stub
		devStore.changeDeviceIPv4Address(device);

	}

	@Override
	public void deviceVlanChanged(IDevice device) {
		// TODO Auto-generated method stub
	}
	

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
	    Collection<Class<? extends IFloodlightService>> l =
	            new ArrayList<Class<? extends IFloodlightService>>();
	        l.add(IFloodlightProviderService.class);
	        l.add(IDeviceService.class);
	        l.add(IThreadPoolService.class);
	        return l;
	}

	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException {
		// TODO Auto-generated method stub
		Map<String, String> configMap = context.getConfigParams(this);
		String conf = configMap.get(DBConfigFile);
		op = new GraphDBOperation(conf);
		
		log = LoggerFactory.getLogger(NetworkGraphPublisher.class);
		floodlightProvider =
	            context.getServiceImpl(IFloodlightProviderService.class);
		deviceService = context.getServiceImpl(IDeviceService.class);
		linkDiscovery = context.getServiceImpl(ILinkDiscoveryService.class);
		threadPool = context.getServiceImpl(IThreadPoolService.class);
		registryService = context.getServiceImpl(IControllerRegistryService.class);
		
		devStore = new DeviceStorageImpl();
		devStore.init(conf);
		
		swStore = new SwitchStorageImpl();
		swStore.init(conf);
		
		linkStore = new LinkStorageImpl();
		linkStore.init(conf);
				
		log.debug("Initializing NetworkGraphPublisher module with {}", conf);
		
	}

	@Override
	public void startUp(FloodlightModuleContext context) {
		// TODO Auto-generated method stub
		Map<String, String> configMap = context.getConfigParams(this);
		String cleanupNeeded = configMap.get(CleanupEnabled);

		deviceService.addListener(this);
		floodlightProvider.addOFSwitchListener(this);
		linkDiscovery.addListener(this);
		
		log.debug("Adding EventListener");
		IDBConnection conn = op.getDBConnection();
		conn.addEventListener(new LocalTopologyEventListener((GraphDBConnection) conn));
	       // Setup the Cleanup task. 
		if (cleanupNeeded == null || !cleanupNeeded.equals("False")) {
				ScheduledExecutorService ses = threadPool.getScheduledExecutor();
				cleanupTask = new SingletonTask(ses, new SwitchCleanup());
				cleanupTask.reschedule(CLEANUP_TASK_INTERVAL, TimeUnit.SECONDS);
		}
	}

}
