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

import de.intevation.lada.util.data.MathUtil;


/**
 * The persistent class for the zusatz_wert database table.
 */
@MappedSuperclass
@Table(name="zusatz_wert")
public class ZusatzWert implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id", nullable=false, insertable=false)
    private Integer id;

    @Column(name="letzte_aenderung")
    private Timestamp letzteAenderung;

    private Float messfehler;

    @Column(name="messwert_pzs")
    private Double messwertPzs;

    @Column(name="nwg_zu_messwert")
    private Double nwgZuMesswert;

    @Column(name="probe_id")
    private Integer probeId;

    @Column(name="pzs_id")
    private String pzsId;

    @Transient
    private boolean owner;

    @Transient
    private boolean readonly;

    public ZusatzWert() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Timestamp getLetzteAenderung() {
        return this.letzteAenderung;
    }

    public Float getMessfehler() {
        return this.messfehler;
    }

    public void setMessfehler(Float messfehler) {
        this.messfehler = messfehler;
    }

    public Double getMesswertPzs() {
        return this.messwertPzs;
    }

    public void setMesswertPzs(Double messwertPzs) {
        this.messwertPzs = MathUtil.roundDoubleToThree(messwertPzs);
    }

    public Double getNwgZuMesswert() {
        return this.nwgZuMesswert;
    }

    public void setNwgZuMesswert(Double nwgZuMesswert) {
        this.nwgZuMesswert = MathUtil.roundDoubleToThree(nwgZuMesswert);
    }

    public Integer getProbeId() {
        return this.probeId;
    }

    public void setProbeId(Integer probeId) {
        this.probeId = probeId;
    }

    public String getPzsId() {
        return this.pzsId;
    }

    public void setPzsId(String pzsId) {
        this.pzsId = pzsId;
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
