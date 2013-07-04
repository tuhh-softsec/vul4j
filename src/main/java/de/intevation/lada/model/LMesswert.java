package de.intevation.lada.model;

// Generated 21.05.2013 16:58:30 by Hibernate Tools 3.4.0.CR1

import java.util.Date;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * LMesswert generated by hbm2java
 */
@Entity
@Table(name = "l_messwert", schema = "public")
public class LMesswert implements java.io.Serializable {

	private LMesswertId id;
	private String probeId;
	private Integer messungsId;
	private Integer mehId;
	private Integer messgroesseId;
	private String messwertNwg;
	private float messwert;
	private Float messfehler;
	private Float nwgZuMesswert;
	private Boolean grenzwertueberschreitung;
	private Date letzteAenderung;

	public LMesswert() {
	}

	public LMesswert(LMesswertId id, String probeId, Integer messungsId,
	    Integer mehId, Integer messgroesseId, float messwert
	) {
		this.id = id;
		this.probeId = probeId;
		this.messungsId = messungsId;
		this.mehId = mehId;
		this.messgroesseId = messgroesseId;
		this.messwert = messwert;
	}

	public LMesswert(LMesswertId id, String probeId, Integer messungsId,
	    Integer mehId, Integer messgroesseId, String messwertNwg,
		float messwert, Float messfehler, Float nwgZuMesswert,
		Boolean grenzwertueberschreitung, Date letzteAenderung
	) {
		this.id = id;
		this.probeId = probeId;
		this.messungsId = messungsId;
		this.mehId = mehId;
		this.messgroesseId = messgroesseId;
		this.messwertNwg = messwertNwg;
		this.messwert = messwert;
		this.messfehler = messfehler;
		this.nwgZuMesswert = nwgZuMesswert;
		this.grenzwertueberschreitung = grenzwertueberschreitung;
		this.letzteAenderung = letzteAenderung;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "probeId", column = @Column(name = "probe_id", nullable = false, length = 20)),
			@AttributeOverride(name = "messungsId", column = @Column(name = "messungs_id", nullable = false)),
			@AttributeOverride(name = "messgroesseId", column = @Column(name = "messgroesse_id", nullable = false)) })
	public LMesswertId getId() {
		return this.id;
	}

	public void setId(LMesswertId id) {
		this.id = id;
	}

	@Column(name = "probe_id", nullable = false, insertable = false, updatable = false)
	public String getProbeId() {
	    return this.probeId;
	}
	
	public void setProbeId(String probeId) {
	    this.probeId = probeId;
	}
	
	@Column(name = "messungs_id", nullable = false, insertable = false, updatable= false)
	public Integer getMessungsId() {
	    return this.messungsId;
	}
	
	public void setMessungsId(Integer messungsId) {
	    this.messungsId = messungsId;
	}
	
	@Column(name = "meh_id", nullable = false)
	public Integer getMehId() {
		return this.mehId;
	}

	public void setMehId(Integer mehId) {
	    this.mehId = mehId;
	}

	@Column(name = "messgroesse_id", nullable = false, insertable = false, updatable = false)
	public Integer getMessgroesseId() {
		return this.messgroesseId;
	}

	public void setMessgroesseId(Integer messgroesseId) {
		this.messgroesseId = messgroesseId;
	}

	@Column(name = "messwert_nwg", length = 1)
	public String getMesswertNwg() {
		return this.messwertNwg;
	}

	public void setMesswertNwg(String messwertNwg) {
		this.messwertNwg = messwertNwg;
	}

	@Column(name = "messwert", nullable = false, precision = 8, scale = 8)
	public float getMesswert() {
		return this.messwert;
	}

	public void setMesswert(float messwert) {
		this.messwert = messwert;
	}

	@Column(name = "messfehler", precision = 8, scale = 8)
	public Float getMessfehler() {
		return this.messfehler;
	}

	public void setMessfehler(Float messfehler) {
		this.messfehler = messfehler;
	}

	@Column(name = "nwg_zu_messwert", precision = 8, scale = 8)
	public Float getNwgZuMesswert() {
		return this.nwgZuMesswert;
	}

	public void setNwgZuMesswert(Float nwgZuMesswert) {
		this.nwgZuMesswert = nwgZuMesswert;
	}

	@Column(name = "grenzwertueberschreitung")
	public Boolean getGrenzwertueberschreitung() {
		return this.grenzwertueberschreitung;
	}

	public void setGrenzwertueberschreitung(Boolean grenzwertueberschreitung) {
		this.grenzwertueberschreitung = grenzwertueberschreitung;
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
