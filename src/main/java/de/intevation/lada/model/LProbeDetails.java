package de.intevation.lada.model;

import java.io.Serializable;
import java.util.List;


public class LProbeDetails implements Serializable
{

    private LProbe lprobe;
    private List<LOrt> lorts;
    private List<LKommentarP> lkommentar;

    public LProbeDetails() {
    }

    public LProbeDetails(
        LProbe lprobe,
        List<LOrt> lorts,
        List<LKommentarP> lkommentar
    ) {
        this.lprobe = lprobe;
        this.lorts = lorts;
        this.setLkommentar(lkommentar);
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

    public List<LKommentarP> getLkommentar() {
        return lkommentar;
    }

    public void setLkommentar(List<LKommentarP> lkommentar) {
        this.lkommentar = lkommentar;
    }
}
