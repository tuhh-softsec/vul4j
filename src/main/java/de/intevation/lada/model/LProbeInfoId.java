package de.intevation.lada.model;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;


@Embeddable
public class LProbeInfoId
implements Serializable
{
    @Column(name = "probe_id", nullable = false)
    private String probeId;
    @Column(name = "nebenproben_nr", nullable = false)
    private String nebenprobenNr;

    public LProbeInfoId() {
    }

    public LProbeInfoId(String probeId, String nebenprobenNr) {
        this.probeId = probeId;
        this.nebenprobenNr = nebenprobenNr;
    }

    public String getProbeId() {
        return this.probeId;
    }

    public void setProbeId(String probeId) {
        this.probeId = probeId;
    }

    public String getNebenprobenNr() {
        return this.nebenprobenNr;
    }

    public void setNebenprobenNr(String nebenprobenNr) {
        this.nebenprobenNr = nebenprobenNr;
    }

    @Override
    public boolean equals(Object other) {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof LProbeInfoId))
            return false;
        LProbeInfoId castOther = (LProbeInfoId) other;

        return ((this.getProbeId() == castOther.getProbeId()) || (this
                .getProbeId() != null && castOther.getProbeId() != null && this
                .getProbeId().equals(castOther.getProbeId())))
                && (this.getNebenprobenNr() == castOther.getNebenprobenNr());
    }

    @Override
    public int hashCode() {
        int result = 17;

        result = 37 * result
            + (getProbeId() == null ? 0 : this.getProbeId().hashCode());
        result = 37 * result + this.getNebenprobenNr().hashCode();
        return result;
    }
}
