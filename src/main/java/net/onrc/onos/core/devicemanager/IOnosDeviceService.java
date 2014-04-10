package net.onrc.onos.core.devicemanager;

import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.util.MACAddress;

/**
 * {@link OnosDeviceManager} doesn't yet provide any API to fellow modules,
 * however making it export a dummy service means we can specify it as
 * a dependency of Forwarding.
 *
 * @author jono
 */
public interface IOnosDeviceService extends IFloodlightService {

    public void addOnosDeviceListener(IOnosDeviceListener listener);

    public void deleteOnosDeviceListener(IOnosDeviceListener listener);

    public void deleteOnosDevice(OnosDevice dev);

    public void deleteOnosDeviceByMac(MACAddress mac);

    public void addOnosDevice(Long mac, OnosDevice dev);
}
