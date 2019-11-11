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
import javax.persistence.PrePersist;
import javax.persistence.Transient;
import javax.ws.rs.core.MultivaluedMap;


/**
 * The persistent class for the messprogramm database table.
 * 
 */
@Entity
public class Messprogramm implements Serializable {
    private static final long serialVersionUID = 1L;

    // Has to be kept in sync with database schema
    @PrePersist
    void setDefaults() {
        if (baId == null) {
            baId = 1;
        }
        if (intervallOffset == null) {
            intervallOffset = 0;
        }
    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @Column(name="ba_id")
    private Integer baId;

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

    private String kommentar;

    @Column(name="labor_mst_id")
    private String laborMstId;

    @Column(name="letzte_aenderung", insertable=false)
    private Timestamp letzteAenderung;

    @Column(name="media_desk")
    private String mediaDesk;

    @Column(name="mst_id")
    private String mstId;

    @Column(name="mpl_id")
    private Integer mplId;

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

    @Column(name="rei_progpunkt_grp_id")
    private Integer reiProgpunktGrpId;

    @Column(name="kta_gruppe_id")
    private Integer ktaGruppeId;

    @Column(name="aktiv")
    private Boolean aktiv;

    @Column(name="meh_id")
    private Integer mehId;

    @Column(name="probenahmemenge")
    private String probenahmeMenge;

    @Transient
    private MultivaluedMap<String, Integer> errors;

    @Transient
    private MultivaluedMap<String, Integer> warnings;

    @Transient
    private boolean readonly;

    public Messprogramm() {
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

    public String getKommentar() {
        return this.kommentar;
    }

    public void setKommentar(String kommentar) {
        this.kommentar = kommentar;
    }

    public String getLaborMstId() {
        return this.laborMstId;
    }

    public void setLaborMstId(String laborMstId) {
        this.laborMstId = laborMstId;
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

    public Integer getMplId() {
        return this.mplId;
    }

    public void setMplId(Integer mplId) {
        this.mplId = mplId;
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

    public Integer getReiProgpunktGrpId() {
        return reiProgpunktGrpId;
    }

    public void setReiProgpunktGrpId(Integer reiProgpunktGrpId) {
        this.reiProgpunktGrpId = reiProgpunktGrpId;
    }

    public Integer getKtaGruppeId() {
        return ktaGruppeId;
    }

    public void setKtaGruppeId(Integer ktaGruppeId) {
        this.ktaGruppeId = ktaGruppeId;
    }

    public Boolean getAktiv() {
        return aktiv;
    }

    public void setAktiv(Boolean aktiv) {
        this.aktiv = aktiv;
    }

    public Integer getMehId() {
        return this.mehId;
    }

    public void setMehId(Integer mehId) {
        this.mehId = mehId;
    }

    public String getProbenahmeMenge() {
        return this.probenahmeMenge;
    }

    public void setProbenahmeMenge(String probenahmeMenge) {
        this.probenahmeMenge = probenahmeMenge;
    }

    public MultivaluedMap<String, Integer> getErrors() {
        return this.errors;
    }

    public void setErrors(MultivaluedMap<String, Integer> errors) {
        this.errors = errors;
    }

    public MultivaluedMap<String, Integer> getWarnings() {
        return this.warnings;
    }

    public void setWarnings(MultivaluedMap<String, Integer> warnings) {
        this.warnings = warnings;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

}
