/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.model.land;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the messprogramm database table.
 */
@Entity
@Table(name="messprogramm")
public class Messprogramm implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id", unique=true, nullable=false)
    private Integer id;

    @Column(name="ba_id")
    private String baId;

    @Column(name="datenbasis_id")
    private Integer datenbasisId;

    @Column(name="gem_id")
    private String gemId;

    @Column(name="gueltig_bis")
    private Integer gueltigBis;

    @Column(name="gueltig_von")
    private Integer gueltigVon;

    @Column(name="intervall_offset")
    private Integer intervallOffset;

    @Column(name="letzte_aenderung")
    private Timestamp letzteAenderung;

    @Column(name="media_desk")
    private String mediaDesk;

    @Column(name="mst_id")
    private String mstId;

    private String name;

    @Column(name="netzbetreiber_id")
    private String netzbetreiberId;

    @Column(name="ort_id")
    private Integer ortId;

    @Column(name="probe_kommentar")
    private String probeKommentar;

    @Column(name="probe_nehmer_id")
    private Integer probeNehmerId;

    @Column(name="probenart_id")
    private Integer probenartId;

    private String probenintervall;

    @Column(name="teilintervall_bis")
    private Integer teilintervallBis;

    @Column(name="teilintervall_von")
    private Integer teilintervallVon;

    private Boolean test;

    @Column(name="umw_id")
    private String umwId;

    public Messprogramm() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBaId() {
        return this.baId;
    }

    public void setBaId(String baId) {
        this.baId = baId;
    }

    public Integer getDatenbasisId() {
        return this.datenbasisId;
    }

    public void setDatenbasisId(Integer datenbasisId) {
        this.datenbasisId = datenbasisId;
    }

    public String getGemId() {
        return this.gemId;
    }

    public void setGemId(String gemId) {
        this.gemId = gemId;
    }

    public Integer getGueltigBis() {
        return this.gueltigBis;
    }

    public void setGueltigBis(Integer gueltigBis) {
        this.gueltigBis = gueltigBis;
    }

    public Integer getGueltigVon() {
        return this.gueltigVon;
    }

    public void setGueltigVon(Integer gueltigVon) {
        this.gueltigVon = gueltigVon;
    }

    public Integer getIntervallOffset() {
        return this.intervallOffset;
    }

    public void setIntervallOffset(Integer intervallOffset) {
        this.intervallOffset = intervallOffset;
    }

    public Timestamp getLetzteAenderung() {
        return this.letzteAenderung;
    }

    public void setLetzteAenderung(Timestamp letzteAenderung) {
        this.letzteAenderung = letzteAenderung;
    }

    public String getMediaDesk() {
        return this.mediaDesk;
    }

    public void setMediaDesk(String mediaDesk) {
        this.mediaDesk = mediaDesk;
    }

    public String getMstId() {
        return this.mstId;
    }

    public void setMstId(String mstId) {
        this.mstId = mstId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNetzbetreiberId() {
        return this.netzbetreiberId;
    }

    public void setNetzbetreiberId(String netzbetreiberId) {
        this.netzbetreiberId = netzbetreiberId;
    }

    public Integer getOrtId() {
        return this.ortId;
    }

    public void setOrtId(Integer ortId) {
        this.ortId = ortId;
    }

    public String getProbeKommentar() {
        return this.probeKommentar;
    }

    public void setProbeKommentar(String probeKommentar) {
        this.probeKommentar = probeKommentar;
    }

    public Integer getProbeNehmerId() {
        return this.probeNehmerId;
    }

    public void setProbeNehmerId(Integer probeNehmerId) {
        this.probeNehmerId = probeNehmerId;
    }

    public Integer getProbenartId() {
        return this.probenartId;
    }

    public void setProbenartId(Integer probenartId) {
        this.probenartId = probenartId;
    }

    public String getProbenintervall() {
        return this.probenintervall;
    }

    public void setProbenintervall(String probenintervall) {
        this.probenintervall = probenintervall;
    }

    public Integer getTeilintervallBis() {
        return this.teilintervallBis;
    }

    public void setTeilintervallBis(Integer teilintervallBis) {
        this.teilintervallBis = teilintervallBis;
    }

    public Integer getTeilintervallVon() {
        return this.teilintervallVon;
    }

    public void setTeilintervallVon(Integer teilintervallVon) {
        this.teilintervallVon = teilintervallVon;
    }

    public Boolean getTest() {
        return this.test;
    }

    public void setTest(Boolean test) {
        this.test = test;
    }

    public String getUmwId() {
        return this.umwId;
    }

    public void setUmwId(String umwId) {
        this.umwId = umwId;
    }
}
