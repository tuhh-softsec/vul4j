package de.intevation.lada.model;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.Type;

import com.vividsolutions.jts.geom.MultiPolygon;

import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the de_vg database table.
 * 
 */
@Entity
@Table(name="de_vg")
public class DeVg implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer gid;
	private String ags;
	private String bemerk;
	private String debkgId;
	private String des;
	private String gen;
	private MultiPolygon geom;
	private double gf;
	private double isn;
	private BigDecimal length;
	private String nambild;
	private String rauRs;
	private String rs;
	private String rsAlt;
	private BigDecimal shapeArea;
	private double use;
	private Date wirksamkei;

	public DeVg() {
	}

	@Id
	@Column(name = "gid")
	public Integer getGid() {
		return this.gid;
	}

	public void setGid(Integer gid) {
		this.gid = gid;
	}

	@Column(name = "ags")
	public String getAgs() {
		return this.ags;
	}

	public void setAgs(String ags) {
		this.ags = ags;
	}

	@Column(name = "bemerk")
	public String getBemerk() {
		return this.bemerk;
	}

	public void setBemerk(String bemerk) {
		this.bemerk = bemerk;
	}

	@Column(name = "debkg_id")
	public String getDebkgId() {
		return this.debkgId;
	}

	public void setDebkgId(String debkgId) {
		this.debkgId = debkgId;
	}

	@Column(name = "des")
	public String getDes() {
		return this.des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	@Column(name = "gen")
	public String getGen() {
		return this.gen;
	}

	public void setGen(String gen) {
		this.gen = gen;
	}

	@Column(name = "geom")
	@Type(type = "org.hibernate.spatial.GeometryType")
	public MultiPolygon getGeom() {
		return this.geom;
	}

	public void setGeom(MultiPolygon geom) {
		this.geom = geom;
	}

	@Column(name = "gf")
	public double getGf() {
		return this.gf;
	}

	public void setGf(double gf) {
		this.gf = gf;
	}

	@Column(name = "isn")
	public double getIsn() {
		return this.isn;
	}

	public void setIsn(double isn) {
		this.isn = isn;
	}

	@Column(name = "length")
	public BigDecimal getLength() {
		return this.length;
	}

	public void setLength(BigDecimal length) {
		this.length = length;
	}

	@Column(name = "nambild")
	public String getNambild() {
		return this.nambild;
	}

	public void setNambild(String nambild) {
		this.nambild = nambild;
	}

	@Column(name = "rau_rs")
	public String getRauRs() {
		return this.rauRs;
	}

	public void setRauRs(String rauRs) {
		this.rauRs = rauRs;
	}

	@Column(name = "rs")
	public String getRs() {
		return this.rs;
	}

	public void setRs(String rs) {
		this.rs = rs;
	}

	@Column(name = "rs_alt")
	public String getRsAlt() {
		return this.rsAlt;
	}

	public void setRsAlt(String rsAlt) {
		this.rsAlt = rsAlt;
	}

	@Column(name = "shape_area")
	public BigDecimal getShapeArea() {
		return this.shapeArea;
	}

	public void setShapeArea(BigDecimal shapeArea) {
		this.shapeArea = shapeArea;
	}

	@Column(name = "use")
	public double getUse() {
		return this.use;
	}

	public void setUse(double use) {
		this.use = use;
	}

	@Column(name = "wirksamkei")
	public Date getWirksamkei() {
		return this.wirksamkei;
	}

	public void setWirksamkei(Date wirksamkei) {
		this.wirksamkei = wirksamkei;
	}

}