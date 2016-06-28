/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import javax.persistence.Transient;


/**
 * The persistent class for the messung database table.
 */
@MappedSuperclass
@Table(name="messung")
public class Messung implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id", unique=true, nullable=false)
    private Integer id;

    private Boolean fertig;

    @Column(name="letzte_aenderung")
    private Timestamp letzteAenderung;

    private Integer messdauer;

    private Timestamp messzeitpunkt;

    @Column(name="mmt_id")
    private String mmtId;

    @Column(name="probe_id")
    private Integer probeId;

    private Integer status;

    @Transient
    private boolean owner;

    @Transient
    private boolean readonly;

    public Messung() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getFertig() {
        return this.fertig;
    }

    public void setFertig(Boolean fertig) {
        this.fertig = fertig;
    }

    public Timestamp getLetzteAenderung() {
        return this.letzteAenderung;
    }

    public Integer getMessdauer() {
        return this.messdauer;
    }

    public void setMessdauer(Integer messdauer) {
        this.messdauer = messdauer;
    }

    public Timestamp getMesszeitpunkt() {
        return this.messzeitpunkt;
    }

    public void setMesszeitpunkt(Timestamp messzeitpunkt) {
        this.messzeitpunkt = messzeitpunkt;
    }

    public String getMmtId() {
        return this.mmtId;
    }

    public void setMmtId(String mmtId) {
        this.mmtId = mmtId;
    }

    public Integer getProbeId() {
        return probeId;
    }

    public void setProbeId(Integer probeId) {
        this.probeId = probeId;
    }

    /**
     * @return the status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    public boolean isOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }
}
