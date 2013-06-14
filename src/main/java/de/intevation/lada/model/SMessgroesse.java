package de.intevation.lada.model;

// Generated 21.05.2013 16:58:30 by Hibernate Tools 3.4.0.CR1

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * SMessgroesse generated by hbm2java
 */
@Entity
@Table(name = "s_messgroesse", schema = "public")
public class SMessgroesse implements java.io.Serializable {

	private int messgroesseId;
	private String beschreibung;
	private String messgro0esse;
	private String defaultFarbe;
	private String idfNuklidKey;
	private Character istLeitnuklid;
	private Long eudfNuklidId;
	private String kennungBvl;

	public SMessgroesse() {
	}

	public SMessgroesse(int messgroesseId, String messgro0esse) {
		this.messgroesseId = messgroesseId;
		this.messgro0esse = messgro0esse;
	}

	public SMessgroesse(int messgroesseId, String beschreibung,
			String messgro0esse, String defaultFarbe, String idfNuklidKey,
			Character istLeitnuklid, Long eudfNuklidId, String kennungBvl) {
		this.messgroesseId = messgroesseId;
		this.beschreibung = beschreibung;
		this.messgro0esse = messgro0esse;
		this.defaultFarbe = defaultFarbe;
		this.idfNuklidKey = idfNuklidKey;
		this.istLeitnuklid = istLeitnuklid;
		this.eudfNuklidId = eudfNuklidId;
		this.kennungBvl = kennungBvl;
	}

	@Id
	@Column(name = "messgroesse_id", unique = true, nullable = false)
	public int getMessgroesseId() {
		return this.messgroesseId;
	}

	public void setMessgroesseId(int messgroesseId) {
		this.messgroesseId = messgroesseId;
	}

	@Column(name = "beschreibung", length = 300)
	public String getBeschreibung() {
		return this.beschreibung;
	}

	public void setBeschreibung(String beschreibung) {
		this.beschreibung = beschreibung;
	}

	@Column(name = "messgro0esse", nullable = false, length = 50)
	public String getMessgro0esse() {
		return this.messgro0esse;
	}

	public void setMessgro0esse(String messgro0esse) {
		this.messgro0esse = messgro0esse;
	}

	@Column(name = "default_farbe", length = 9)
	public String getDefaultFarbe() {
		return this.defaultFarbe;
	}

	public void setDefaultFarbe(String defaultFarbe) {
		this.defaultFarbe = defaultFarbe;
	}

	@Column(name = "idf_nuklid_key", length = 6)
	public String getIdfNuklidKey() {
		return this.idfNuklidKey;
	}

	public void setIdfNuklidKey(String idfNuklidKey) {
		this.idfNuklidKey = idfNuklidKey;
	}

	@Column(name = "ist_leitnuklid", length = 1)
	public Character getIstLeitnuklid() {
		return this.istLeitnuklid;
	}

	public void setIstLeitnuklid(Character istLeitnuklid) {
		this.istLeitnuklid = istLeitnuklid;
	}

	@Column(name = "eudf_nuklid_id")
	public Long getEudfNuklidId() {
		return this.eudfNuklidId;
	}

	public void setEudfNuklidId(Long eudfNuklidId) {
		this.eudfNuklidId = eudfNuklidId;
	}

	@Column(name = "kennung_bvl", length = 7)
	public String getKennungBvl() {
		return this.kennungBvl;
	}

	public void setKennungBvl(String kennungBvl) {
		this.kennungBvl = kennungBvl;
	}

}
