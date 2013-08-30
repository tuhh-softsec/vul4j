package de.intevation.lada.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the s_pflicht_messgroesse database table.
 * 
 */
@Entity
@Table(name="s_pflicht_messgroesse")
public class SPflichtMessgroesse implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Integer id;

	@Column(name="datenbasis_id")
	private Integer datenbasisId;

	@Column(name="messgroesse_id")
	private Integer messgroesseId;

	@Column(name="mmt_id")
	private String mmtId;

	@Column(name="umw_id")
	private String umwId;

	public SPflichtMessgroesse() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getDatenbasisId() {
		return this.datenbasisId;
	}

	public void setDatenbasisId(Integer datenbasisId) {
		this.datenbasisId = datenbasisId;
	}

	public Integer getMessgroesseId() {
		return this.messgroesseId;
	}

	public void setMessgroesseId(Integer messgroesseId) {
		this.messgroesseId = messgroesseId;
	}

	public String getMmtId() {
		return this.mmtId;
	}

	public void setMmtId(String mmtId) {
		this.mmtId = mmtId;
	}

	public String getUmwId() {
		return this.umwId;
	}

	public void setUmwId(String umwId) {
		this.umwId = umwId;
	}

}