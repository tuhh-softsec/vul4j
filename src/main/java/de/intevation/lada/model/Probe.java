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


/**
 * The persistent class for the probe database table.
 */
@MappedSuperclass
@Table(name="probe")
public class Probe implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id", unique=true, nullable=false)
    private Integer id;

    @Column(name="ba_id")
    private Integer baId;

    @Column(name="datenbasis_id")
    private Integer datenbasisId;

    @Column(name="letzte_aenderung")
    private Timestamp letzteAenderung;

    private String media;

    @Column(name="media_desk")
    private String mediaDesk;

    private Long mittelungsdauer;

    @Column(name="mst_id")
    private String mstId;

    @Column(name="labor_mst_id")
    private String laborMstId;

    @Column(name="netzbetreiber_id")
    private String netzbetreiberId;

    @Column(name="probeentnahme_beginn")
    private Timestamp probeentnahmeBeginn;

    @Column(name="probeentnahme_ende")
    private Timestamp probeentnahmeEnde;

    @Column(name="probenart_id")
    private Integer probenartId;

    private Boolean test;

    @Column(name="umw_id")
    private String umwId;

    public Probe() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBaId() {
        return this.baId;
    }

    public void setBaId(Integer baId) {
        this.baId = baId;
    }

    public Integer getDatenbasisId() {
        return this.datenbasisId;
    }

    public void setDatenbasisId(Integer datenbasisId) {
        this.datenbasisId = datenbasisId;
    }

    public Timestamp getLetzteAenderung() {
        return this.letzteAenderung;
    }

    public String getMedia() {
        return this.media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getMediaDesk() {
        return this.mediaDesk;
    }

    public void setMediaDesk(String mediaDesk) {
        this.mediaDesk = mediaDesk;
    }

    public Long getMittelungsdauer() {
        return this.mittelungsdauer;
    }

    public void setMittelungsdauer(Long mittelungsdauer) {
        this.mittelungsdauer = mittelungsdauer;
    }

    public String getMstId() {
        return this.mstId;
    }

    public void setMstId(String mstId) {
        this.mstId = mstId;
    }

    /**
     * @return the laborMstId
     */
    public String getLaborMstId() {
        return laborMstId;
    }

    /**
     * @param laborMstId the laborMstId to set
     */
    public void setLaborMstId(String laborMstId) {
        this.laborMstId = laborMstId;
    }

    public String getNetzbetreiberId() {
        return this.netzbetreiberId;
    }

    public void setNetzbetreiberId(String netzbetreiberId) {
        this.netzbetreiberId = netzbetreiberId;
    }

    public Timestamp getProbeentnahmeBeginn() {
        return this.probeentnahmeBeginn;
    }

    public void setProbeentnahmeBeginn(Timestamp probeentnahmeBeginn) {
        this.probeentnahmeBeginn = probeentnahmeBeginn;
    }

    public Timestamp getProbeentnahmeEnde() {
        return this.probeentnahmeEnde;
    }

    public void setProbeentnahmeEnde(Timestamp probeentnahmeEnde) {
        this.probeentnahmeEnde = probeentnahmeEnde;
    }

    public Integer getProbenartId() {
        return this.probenartId;
    }

    public void setProbenartId(Integer probenartId) {
        this.probenartId = probenartId;
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
