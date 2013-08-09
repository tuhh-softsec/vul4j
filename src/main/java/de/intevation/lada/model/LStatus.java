package de.intevation.lada.model;

// Generated 21.05.2013 16:58:30 by Hibernate Tools 3.4.0.CR1

import java.util.Date;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * LStatus generated by hbm2java
 */
@Entity
@Table(name = "l_status", schema = "public")
@SequenceGenerator(name = "STATUS_ID_STORE", sequenceName = "status_id_seq")
public class LStatus implements java.io.Serializable {

	private LStatusId id;
	private Integer sId;
	private String probeId;
	private String erzeuger;
	private Integer messungsId;
	private Short status;
	private Date SDatum;
	private String SKommentar;

	public LStatus() {
	}

	public LStatus(LStatusId id, String probeId, String erzeuger,
	    Integer messungsId, Date SDatum) {
		this.id = id;
		this.setProbeId(probeId);
		this.erzeuger = erzeuger;
		this.messungsId = messungsId;
		this.SDatum = SDatum;
	}

	public LStatus(LStatusId id, String probeId, String erzeuger,
	    Integer messungsId, Short status, Date SDatum, String SKommentar
	) {
		this.id = id;
		this.setProbeId(probeId);
		this.erzeuger = erzeuger;
		this.messungsId = messungsId;
		this.status = status;
		this.SDatum = SDatum;
		this.SKommentar = SKommentar;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "probeId", column = @Column(name = "probe_id", nullable = false, length = 20)),
			@AttributeOverride(name = "messungsId", column = @Column(name = "messungs_id", nullable = false)),
			@AttributeOverride(name = "SId", column = @Column(name = "s_id", nullable = false)) })
	public LStatusId getId() {
		return this.id;
	}

	public void setId(LStatusId id) {
		this.id = id;
	}

    @Column(name = "s_id", nullable = false, insertable = false, updatable = false)
	public Integer getSId() {
        return sId;
    }

    public void setSId(Integer sId) {
        this.sId = sId;
    }

    @Column(name = "probe_id", nullable = false, insertable = false, updatable = false)
	public String getProbeId() {
        return probeId;
    }

    public void setProbeId(String probeId) {
        this.probeId = probeId;
    }

    @Column(name = "erzeuger", nullable = false)
	public String getErzeuger() {
		return this.erzeuger;
	}

	public void setErzeuger(String erzeuger) {
		this.erzeuger = erzeuger;
	}

	@Column(name = "messungs_id", nullable = false, insertable = false, updatable = false)
	public Integer getMessungsId() {
		return this.messungsId;
	}

	public void setMessungsId(Integer messungsId) {
		this.messungsId = messungsId;
	}

	@Column(name = "status")
	public Short getStatus() {
		return this.status;
	}

	public void setStatus(Short status) {
		this.status = status;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "s_datum", nullable = false, length = 35)
	public Date getSDatum() {
		return this.SDatum;
	}

	public void setSDatum(Date SDatum) {
		this.SDatum = SDatum;
	}

	@Column(name = "s_kommentar", length = 1024)
	public String getSKommentar() {
		return this.SKommentar;
	}

	public void setSKommentar(String SKommentar) {
		this.SKommentar = SKommentar;
	}

}
