package net.floodlightcontroller.onoslistener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.IOFSwitchListener;
import net.floodlightcontroller.core.ISwitchStorage;
import net.floodlightcontroller.core.internal.SwitchStorageImpl;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.IDeviceListener;
import net.floodlightcontroller.devicemanager.IDeviceService;
import net.floodlightcontroller.devicemanager.IDeviceStorage;
import net.floodlightcontroller.devicemanager.internal.DeviceStorageImpl;
import net.floodlightcontroller.linkdiscovery.ILinkDiscoveryListener;

public class OnosPublisher implements IDeviceListener, IOFSwitchListener,
		ILinkDiscoveryListener, IFloodlightModule {
	
	protected IDeviceStorage devStore;
	protected ISwitchStorage swStore;
	protected static Logger log;
	protected IDeviceService deviceService;
	
	protected static final String DBConfigFile = "dbconf";

	@Override
	public void linkDiscoveryUpdate(LDUpdate update) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addedSwitch(IOFSwitch sw) {
		// TODO Auto-generated method stub

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
		return "OnosPublisher";
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
	        return l;
	}

	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException {
		// TODO Auto-generated method stub
		Map<String, String> configMap = context.getConfigParams(this);
		String conf = configMap.get(DBConfigFile);
		
		log = LoggerFactory.getLogger(OnosPublisher.class);
		deviceService = context.getServiceImpl(IDeviceService.class);
		
		swStore = new SwitchStorageImpl();
		swStore.init(conf);
		devStore = new DeviceStorageImpl();
		devStore.init(conf);
		
		log.debug("Initializing OnosPublisher module with {}", conf);
		
	}

	@Override
	public void startUp(FloodlightModuleContext context) {
		// TODO Auto-generated method stub
		deviceService.addListener(this);		
	}

}
