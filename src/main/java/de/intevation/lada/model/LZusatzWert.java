package de.intevation.lada.model;

// Generated 21.05.2013 16:58:30 by Hibernate Tools 3.4.0.CR1

import java.util.Date;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * LZusatzWert generated by hbm2java
 */
@Entity
@Table(name = "l_zusatz_wert", schema = "public")
public class LZusatzWert implements java.io.Serializable {

	private LZusatzWertId id;
	private String probeId;
	private String pzsId;
	private Float nwgZuMesswert;
	private Float messwertPzs;
	private Float messfehler;
	private Date letzteAenderung;

	public LZusatzWert() {
	}

	public LZusatzWert(LZusatzWertId id, String probeId,
			String pzsId) {
		this.id = id;
		this.probeId = probeId;
		this.pzsId = pzsId;
	}

	public LZusatzWert(LZusatzWertId id, String probeId,
			SProbenZusatz SProbenZusatz, Float nwgZuMesswert, Float messwertPzs,
			Float messfehler, Date letzteAenderung) {
		this.id = id;
		this.probeId = probeId;
		this.pzsId = pzsId;
		this.nwgZuMesswert = nwgZuMesswert;
		this.messwertPzs = messwertPzs;
		this.messfehler = messfehler;
		this.letzteAenderung = letzteAenderung;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "probeId", column = @Column(name = "probe_id", nullable = false, length = 20)),
			@AttributeOverride(name = "pzsId", column = @Column(name = "pzs_id", nullable = false, length = 3)) })
	public LZusatzWertId getId() {
		return this.id;
	}

	public void setId(LZusatzWertId id) {
		this.id = id;
	}

	@Column(name = "probe_id", nullable = false, insertable = false, updatable = false)
	public String getProbeId() {
		return this.probeId;
	}

	public void setProbeId(String probeId) {
		this.probeId = probeId;
	}

	@Column(name = "pzs_id", nullable = false, insertable = false, updatable = false)
	public String getPzsId() {
		return this.pzsId;
	}

	public void setPzsId(String pzsId) {
	    this.pzsId = pzsId;
	}

	@Column(name = "nwg_zu_messwert", precision = 8, scale = 8)
	public Float getNwgZuMesswert() {
		return this.nwgZuMesswert;
	}

	public void setNwgZuMesswert(Float nwgZuMesswert) {
		this.nwgZuMesswert = nwgZuMesswert;
	}

	@Column(name = "messwert_pzs", precision = 8, scale = 8)
	public Float getMesswertPzs() {
		return this.messwertPzs;
	}

	public void setMesswertPzs(Float messwertPzs) {
		this.messwertPzs = messwertPzs;
	}

	@Column(name = "messfehler", precision = 8, scale = 8)
	public Float getMessfehler() {
		return this.messfehler;
	}

	public void setMessfehler(Float messfehler) {
		this.messfehler = messfehler;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "letzte_aenderung", length = 29)
	public Date getLetzteAenderung() {
		return this.letzteAenderung;
	}

	public void setLetzteAenderung(Date letzteAenderung) {
		this.letzteAenderung = letzteAenderung;
	}

}
