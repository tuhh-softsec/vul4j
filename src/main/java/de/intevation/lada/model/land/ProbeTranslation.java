/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.model.land;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * The persistent class for the probe_translation database table.
 */
@Entity
@Table(name="probe_translation")
public class ProbeTranslation implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id", unique=true)
    private Integer id;

    @OneToOne
    private LProbe probe;

    @GeneratedValue(strategy=GenerationType.IDENTITY)
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

    @JsonIgnore
    public LProbe getProbeId() {
        return this.probe;
    }

    public void setProbeId(LProbe probe) {
        this.probe = probe;
    }

    public String getProbeIdAlt() {
        return this.probeIdAlt;
    }

    public void setProbeIdAlt(String probeIdAlt) {
        this.probeIdAlt = probeIdAlt;
    }

}
