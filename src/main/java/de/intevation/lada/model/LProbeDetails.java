package de.intevation.lada.model;

import java.io.Serializable;
import java.util.List;


public class LProbeDetails implements Serializable
{

    private LProbe lprobe;
    private List<LOrt> lorts;

    public LProbeDetails() {
    }

    public LProbeDetails(LProbe lprobe, List<LOrt> lorts) {
        this.lprobe = lprobe;
        this.lorts = lorts;
    }

    public LProbe getLprobe() {
        return lprobe;
    }

    public void setLprobe(LProbe lprobe) {
        this.lprobe = lprobe;
    }

    public List<LOrt> getLort() {
        return lorts;
    }

    public void setLorts(List<LOrt> lorts) {
        this.lorts = lorts;
    }
}
