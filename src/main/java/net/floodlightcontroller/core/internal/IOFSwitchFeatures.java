package net.floodlightcontroller.core.internal;

import net.floodlightcontroller.core.IOFSwitch;

import org.openflow.protocol.statistics.OFDescriptionStatistics;

public interface IOFSwitchFeatures {
    public void setFromDescription(IOFSwitch sw, OFDescriptionStatistics description);
}
