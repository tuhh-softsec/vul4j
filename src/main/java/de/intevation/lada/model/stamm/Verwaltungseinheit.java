/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.model.stamm;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the verwaltungseinheit database table.
 */
@Entity
@Table(name="verwaltungseinheit")
public class Verwaltungseinheit implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String bezeichnung;

    private String bundesland;

    @Column(name="is_bundesland")
    private String isBundesland;

    @Column(name="is_gemeinde")
    private String isGemeinde;

    @Column(name="is_landkreis")
    private String isLandkreis;

    @Column(name="is_regbezirk")
    private String isRegbezirk;

    @Column(name="koord_x_extern")
    private String koordXExtern;

    @Column(name="koord_y_extern")
    private String koordYExtern;

    private String kreis;

    private Double latitude;

    private Double longitude;

    private String nuts;

    private String plz;

    private String regbezirk;

    @Column(name="kda_id")
    private Integer koordinatenartId;

    public Verwaltungseinheit() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBezeichnung() {
        return this.bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public String getBundesland() {
        return this.bundesland;
    }

    public void setBundesland(String bundesland) {
        this.bundesland = bundesland;
    }

    public String getIsBundesland() {
        return this.isBundesland;
    }

    public void setIsBundesland(String isBundesland) {
        this.isBundesland = isBundesland;
    }

    public String getIsGemeinde() {
        return this.isGemeinde;
    }

    public void setIsGemeinde(String isGemeinde) {
        this.isGemeinde = isGemeinde;
    }

    public String getIsLandkreis() {
        return this.isLandkreis;
    }

    public void setIsLandkreis(String isLandkreis) {
        this.isLandkreis = isLandkreis;
    }

    public String getIsRegbezirk() {
        return this.isRegbezirk;
    }

    public void setIsRegbezirk(String isRegbezirk) {
        this.isRegbezirk = isRegbezirk;
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

    public String getKreis() {
        return this.kreis;
    }

    public void setKreis(String kreis) {
        this.kreis = kreis;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getNuts() {
        return this.nuts;
    }

    public void setNuts(String nuts) {
        this.nuts = nuts;
    }

    public String getPlz() {
        return this.plz;
    }

    public void setPlz(String plz) {
        this.plz = plz;
    }

    public String getRegbezirk() {
        return this.regbezirk;
    }

    public void setRegbezirk(String regbezirk) {
        this.regbezirk = regbezirk;
    }

    public Integer getKoordinatenartId() {
        return this.koordinatenartId;
    }

    public void setKoordinatenArt(Integer koordinatenartId) {
        this.koordinatenartId = koordinatenartId;
    }

}
