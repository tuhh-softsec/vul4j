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
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * The persistent class for the ort database table.
 */
@Entity
@Table(name="ort")
public class SOrt implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    private String beschreibung;

    private String bezeichnung;

    @Column(name="hoehe_land")
    private float hoeheLand;

    @Column(name="koord_x_extern")
    private String koordXExtern;

    @Column(name="koord_y_extern")
    private String koordYExtern;

    private double latitude;

    @Column(name="letzte_aenderung")
    private Timestamp letzteAenderung;

    private double longitude;

    @Column(name="nuts_code")
    private String nutsCode;

    @Column(name="o_typ")
    private String oTyp;

    private String unscharf;

    @ManyToOne
    @JoinColumn(name="kda_id")
    private KoordinatenArt koordinatenArt;

    @ManyToOne
    private NetzBetreiber netzBetreiber;

    @ManyToOne
    private Staat staat;

    @ManyToOne
    @JoinColumn(name="gem_id")
    private Verwaltungseinheit verwaltungseinheit;

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

    public float getHoeheLand() {
        return this.hoeheLand;
    }

    public void setHoeheLand(float hoeheLand) {
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

    public double getLatitude() {
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

    public double getLongitude() {
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

    public KoordinatenArt getKoordinatenArt() {
        return this.koordinatenArt;
    }

    public void setKoordinatenArt(KoordinatenArt koordinatenArt) {
        this.koordinatenArt = koordinatenArt;
    }

    public NetzBetreiber getNetzBetreiber() {
        return this.netzBetreiber;
    }

    public void setNetzBetreiber(NetzBetreiber netzBetreiber) {
        this.netzBetreiber = netzBetreiber;
    }

    public Staat getStaat() {
        return this.staat;
    }

    public void setStaat(Staat staat) {
        this.staat = staat;
    }

    public Verwaltungseinheit getVerwaltungseinheit() {
        return this.verwaltungseinheit;
    }

    public void setVerwaltungseinheit(Verwaltungseinheit verwaltungseinheit) {
        this.verwaltungseinheit = verwaltungseinheit;
    }

}
