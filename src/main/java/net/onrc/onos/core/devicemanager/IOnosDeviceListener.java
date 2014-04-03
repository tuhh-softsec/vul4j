package net.onrc.onos.core.devicemanager;

public interface IOnosDeviceListener {

    public void onosDeviceAdded(OnosDevice device);

    public void onosDeviceRemoved(OnosDevice device);
}
