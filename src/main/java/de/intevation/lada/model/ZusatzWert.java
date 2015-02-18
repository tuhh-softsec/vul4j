/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


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
    private Float messwertPzs;

    @Column(name="nwg_zu_messwert")
    private Float nwgZuMesswert;

    @Column(name="probe_id")
    private Integer probeId;

    @Column(name="pzs_id")
    private String pzsId;

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

    public void setLetzteAenderung(Timestamp letzteAenderung) {
        this.letzteAenderung = letzteAenderung;
    }

    public Float getMessfehler() {
        return this.messfehler;
    }

    public void setMessfehler(Float messfehler) {
        this.messfehler = messfehler;
    }

    public Float getMesswertPzs() {
        return this.messwertPzs;
    }

    public void setMesswertPzs(Float messwertPzs) {
        this.messwertPzs = messwertPzs;
    }

    public Float getNwgZuMesswert() {
        return this.nwgZuMesswert;
    }

    public void setNwgZuMesswert(Float nwgZuMesswert) {
        this.nwgZuMesswert = nwgZuMesswert;
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

}
