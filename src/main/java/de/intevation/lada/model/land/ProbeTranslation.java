/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.model.land;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the probe_translation database table.
 */
@Entity
@Table(name="probe_translation")
public class ProbeTranslation implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    @Column(name="hauptproben_nr")
    private String hauptprobenNr;

    @Column(name="probe_id")
    private Integer probeId;

    @Column(name="probe_id_alt")
    private String probeIdAlt;

    public ProbeTranslation() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHauptprobenNr() {
        return this.hauptprobenNr;
    }

    public void setHauptprobenNr(String hauptprobenNr) {
        this.hauptprobenNr = hauptprobenNr;
    }

    public Integer getProbeId() {
        return this.probeId;
    }

    public void setProbeId(Integer probeId) {
        this.probeId = probeId;
    }

    public String getProbeIdAlt() {
        return this.probeIdAlt;
    }

    public void setProbeIdAlt(String probeIdAlt) {
        this.probeIdAlt = probeIdAlt;
    }

}
