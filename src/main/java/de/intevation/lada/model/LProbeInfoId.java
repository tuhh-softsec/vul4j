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

    public LProbeInfoId() {
    }

    public LProbeInfoId(String probeId) {
        this.probeId = probeId;
    }

    public String getProbeId() {
        return this.probeId;
    }

    public void setProbeId(String probeId) {
        this.probeId = probeId;
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
                .getProbeId().equals(castOther.getProbeId())));
    }

    @Override
    public int hashCode() {
        int result = 17;

        result = 37 * result
            + (getProbeId() == null ? 0 : this.getProbeId().hashCode());
        result = 37 * result;
        return result;
    }
}
