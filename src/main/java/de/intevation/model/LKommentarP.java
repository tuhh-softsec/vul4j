package de.intevation.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the l_kommentar_p database table.
 * 
 */
@Entity
@Table(name="l_kommentar_p")
public class LKommentarP implements Serializable {
	private static final long serialVersionUID = 1L;
	private LKommentarPPK id;
	private String erzeuger;
	private Timestamp kDatum;
	private String kText;
	private LProbe LProbe;

	public LKommentarP() {
	}


	@EmbeddedId
	public LKommentarPPK getId() {
		return this.id;
	}

	public void setId(LKommentarPPK id) {
		this.id = id;
	}


	public String getErzeuger() {
		return this.erzeuger;
	}

	public void setErzeuger(String erzeuger) {
		this.erzeuger = erzeuger;
	}


	@Column(name="k_datum")
	public Timestamp getKDatum() {
		return this.kDatum;
	}

	public void setKDatum(Timestamp kDatum) {
		this.kDatum = kDatum;
	}


	@Column(name="k_text")
	public String getKText() {
		return this.kText;
	}

	public void setKText(String kText) {
		this.kText = kText;
	}


	//bi-directional many-to-one association to LProbe
	@ManyToOne
	@JoinColumn(name="probe_id", insertable=false, updatable=false)
	public LProbe getLProbe() {
		return this.LProbe;
	}

	public void setLProbe(LProbe LProbe) {
		this.LProbe = LProbe;
	}

}