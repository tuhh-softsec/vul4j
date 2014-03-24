package net.onrc.onos.ofcontroller.devicemanager;

public interface IOnosDeviceListener {

    public void onosDeviceAdded(OnosDevice device);
    public void onosDeviceRemoved(OnosDevice device);
}
