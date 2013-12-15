package net.onrc.onos.ofcontroller.devicemanager;

import net.floodlightcontroller.core.module.IFloodlightService;

/**
 * {@link OnosDeviceManager} doesn't yet provide any API to fellow modules,
 * however making it export a dummy service means we can specify it as 
 * a dependency of Forwarding
 * @author jono
 *
 */
public interface IOnosDeviceService extends IFloodlightService {

}
