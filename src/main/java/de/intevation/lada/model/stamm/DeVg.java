/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.model.stamm;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;


/**
 * The persistent class for the de_vg database table.
 */
@Entity
@Table(name="de_vg")
public class DeVg implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    private String ags;

    private String bemerk;

    @Column(name="debkg_id")
    private String debkgId;

    private String des;

    private String gen;

    @Column(name="geom", columnDefinition="geometry")
    @Type(type = "org.hibernate.spatial.GeometryType")
    private MultiPolygon geom;

    private double gf;

    private double isn;

    private BigDecimal length;

    private String nambild;

    @Column(name="rau_rs")
    private String rauRs;

    private String rs;

    @Column(name="rs_alt")
    private String rsAlt;

    @Column(name="shape_area")
    private BigDecimal shapeArea;

    private double use;

    @Temporal(TemporalType.DATE)
    private Date wirksamkei;

    public DeVg() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAgs() {
        return this.ags;
    }

    public void setAgs(String ags) {
        this.ags = ags;
    }

    public String getBemerk() {
        return this.bemerk;
    }

    public void setBemerk(String bemerk) {
        this.bemerk = bemerk;
    }

    public String getDebkgId() {
        return this.debkgId;
    }

    public void setDebkgId(String debkgId) {
        this.debkgId = debkgId;
    }

    public String getDes() {
        return this.des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getGen() {
        return this.gen;
    }

    public void setGen(String gen) {
        this.gen = gen;
    }

    public MultiPolygon getGeom() {
        return this.geom;
    }

    public void setGeom(MultiPolygon geom) {
        this.geom = geom;
    }

    public double getGf() {
        return this.gf;
    }

    public void setGf(double gf) {
        this.gf = gf;
    }

    public double getIsn() {
        return this.isn;
    }

    public void setIsn(double isn) {
        this.isn = isn;
    }

    public BigDecimal getLength() {
        return this.length;
    }

    public void setLength(BigDecimal length) {
        this.length = length;
    }

    public String getNambild() {
        return this.nambild;
    }

    public void setNambild(String nambild) {
        this.nambild = nambild;
    }

    public String getRauRs() {
        return this.rauRs;
    }

    public void setRauRs(String rauRs) {
        this.rauRs = rauRs;
    }

    public String getRs() {
        return this.rs;
    }

    public void setRs(String rs) {
        this.rs = rs;
    }

    public String getRsAlt() {
        return this.rsAlt;
    }

    public void setRsAlt(String rsAlt) {
        this.rsAlt = rsAlt;
    }

    public BigDecimal getShapeArea() {
        return this.shapeArea;
    }

    public void setShapeArea(BigDecimal shapeArea) {
        this.shapeArea = shapeArea;
    }

    public double getUse() {
        return this.use;
    }

    public void setUse(double use) {
        this.use = use;
    }

    public Date getWirksamkei() {
        return this.wirksamkei;
    }

    public void setWirksamkei(Date wirksamkei) {
        this.wirksamkei = wirksamkei;
    }

}
