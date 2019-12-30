package de.intevation.lada.model.stammdaten;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vividsolutions.jts.geom.Point;


/**
 * The persistent class for the verwaltungseinheit database table.
 * 
 */
@Entity
public class Verwaltungseinheit implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String bezeichnung;

    private String bundesland;

    @Column(name="is_bundesland")
    private Boolean isBundesland;

    @Column(name="is_gemeinde")
    private Boolean isGemeinde;

    @Column(name="is_landkreis")
    private Boolean isLandkreis;

    @Column(name="is_regbezirk")
    private Boolean isRegbezirk;

    private String kreis;

    private String nuts;

    private String plz;

    private String regbezirk;


    @Type(type = "jts_geometry")
    @Column(columnDefinition="geometry(Point, 4326)")
    private Point mittelpunkt;

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

    public Boolean getIsBundesland() {
        return this.isBundesland;
    }

    public void setIsBundesland(Boolean isBundesland) {
        this.isBundesland = isBundesland;
    }

    public Boolean getIsGemeinde() {
        return this.isGemeinde;
    }

    public void setIsGemeinde(Boolean isGemeinde) {
        this.isGemeinde = isGemeinde;
    }

    public Boolean getIsLandkreis() {
        return this.isLandkreis;
    }

    public void setIsLandkreis(Boolean isLandkreis) {
        this.isLandkreis = isLandkreis;
    }

    public Boolean getIsRegbezirk() {
        return this.isRegbezirk;
    }

    public void setIsRegbezirk(Boolean isRegbezirk) {
        this.isRegbezirk = isRegbezirk;
    }

    public String getKreis() {
        return this.kreis;
    }

    public void setKreis(String kreis) {
        this.kreis = kreis;
    }

    public Double getLatitude() {
        return this.mittelpunkt != null
            ? this.mittelpunkt.getY()
            : null;
    }

    public Double getLongitude() {
        return this.mittelpunkt != null
            ? this.mittelpunkt.getX()
            : null;
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

    @JsonIgnore
    public Point getMittelpunkt() {
        return mittelpunkt;
    }

    @JsonIgnore
    public void setMittelpunkt(Point mittelpunkt) {
        this.mittelpunkt = mittelpunkt;
    }

}
