package de.intevation.lada.model;

// Generated 21.05.2013 16:58:30 by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * SProbenZusatz generated by hbm2java
 */
@Entity
@Table(name = "s_proben_zusatz", schema = "public", uniqueConstraints = @UniqueConstraint(columnNames = "eudf_keyword"))
public class SProbenZusatz implements java.io.Serializable {

	private String pzsId;
	private Short mehId;
	private String beschreibung;
	private String zusatzwert;
	private String eudfKeyword;

	public SProbenZusatz() {
	}

	public SProbenZusatz(String pzsId, String beschreibung, String zusatzwert) {
		this.pzsId = pzsId;
		this.beschreibung = beschreibung;
		this.zusatzwert = zusatzwert;
	}

	public SProbenZusatz(String pzsId, Short mehId, String beschreibung,
			String zusatzwert, String eudfKeyword) {
		this.pzsId = pzsId;
		this.mehId = mehId;
		this.beschreibung = beschreibung;
		this.zusatzwert = zusatzwert;
		this.eudfKeyword = eudfKeyword;
	}

	@Id
	@Column(name = "pzs_id", unique = true, nullable = false, length = 3)
	public String getPzsId() {
		return this.pzsId;
	}

	public void setPzsId(String pzsId) {
		this.pzsId = pzsId;
	}

	@Column(name = "meh_id")
	public Short getMehId() {
		return this.mehId;
	}

	public void setMehId(Short mehId) {
		this.mehId = mehId;
	}

	@Column(name = "beschreibung", nullable = false, length = 50)
	public String getBeschreibung() {
		return this.beschreibung;
	}

	public void setBeschreibung(String beschreibung) {
		this.beschreibung = beschreibung;
	}

	@Column(name = "zusatzwert", nullable = false, length = 7)
	public String getZusatzwert() {
		return this.zusatzwert;
	}

	public void setZusatzwert(String zusatzwert) {
		this.zusatzwert = zusatzwert;
	}

	@Column(name = "eudf_keyword", unique = true, length = 40)
	public String getEudfKeyword() {
		return this.eudfKeyword;
	}

	public void setEudfKeyword(String eudfKeyword) {
		this.eudfKeyword = eudfKeyword;
	}

}
