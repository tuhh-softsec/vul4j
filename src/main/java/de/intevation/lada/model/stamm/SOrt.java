/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.model.stamm;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the ort database table.
 */
@Entity
@Table(name="ort")
public class SOrt implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id", nullable=false, insertable=false)
    private Integer id;

    private String beschreibung;

    private String bezeichnung;

    @Column(name="hoehe_land")
    private Float hoeheLand;

    @Column(name="koord_x_extern")
    private String koordXExtern;

    @Column(name="koord_y_extern")
    private String koordYExtern;

    private Double latitude;

    @Column(name="letzte_aenderung")
    private Timestamp letzteAenderung;

    private Double longitude;

    @Column(name="nuts_code")
    private String nutsCode;

    @Column(name="o_typ")
    private String oTyp;

    private String unscharf;

    @Column(name="kda_id")
    private Integer koordinatenartId;

    @Column(name="netzbetreiber_id")
    private String netzbetreiberId;

    @Column(name="staat_id")
    private Integer staatId;

    @Column(name="gem_id")
    private String verwaltungseinheitId;

    public SOrt() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBeschreibung() {
        return this.beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public String getBezeichnung() {
        return this.bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public Float getHoeheLand() {
        return this.hoeheLand;
    }

    public void setHoeheLand(Float hoeheLand) {
        this.hoeheLand = hoeheLand;
    }

    public String getKoordXExtern() {
        return this.koordXExtern;
    }

    public void setKoordXExtern(String koordXExtern) {
        this.koordXExtern = koordXExtern;
    }

    public String getKoordYExtern() {
        return this.koordYExtern;
    }

    public void setKoordYExtern(String koordYExtern) {
        this.koordYExtern = koordYExtern;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Timestamp getLetzteAenderung() {
        return this.letzteAenderung;
    }

    public void setLetzteAenderung(Timestamp letzteAenderung) {
        this.letzteAenderung = letzteAenderung;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getNutsCode() {
        return this.nutsCode;
    }

    public void setNutsCode(String nutsCode) {
        this.nutsCode = nutsCode;
    }

    public String getOTyp() {
        return this.oTyp;
    }

    public void setOTyp(String oTyp) {
        this.oTyp = oTyp;
    }

    public String getUnscharf() {
        return this.unscharf;
    }

    public void setUnscharf(String unscharf) {
        this.unscharf = unscharf;
    }

    public Integer getKoordinatenartId() {
        return this.koordinatenartId;
    }

    public void setKoordinatenartId(Integer koordinatenartId) {
        this.koordinatenartId = koordinatenartId;
    }

    public String getNetzbetreiberId() {
        return this.netzbetreiberId;
    }

    public void setNetzbetreiberId(String netzbetreiberId) {
        this.netzbetreiberId = netzbetreiberId;
    }

    public Integer getStaatId() {
        return this.staatId;
    }

    public void setStaatId(Integer staatId) {
        this.staatId = staatId;
    }

    public String getVerwaltungseinheitId() {
        return this.verwaltungseinheitId;
    }

    public void setVerwaltungseinheitId(String verwaltungseinheitId) {
        this.verwaltungseinheitId = verwaltungseinheitId;
    }

}
