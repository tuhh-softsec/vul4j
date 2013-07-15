package de.intevation.lada.model;

// Generated 21.05.2013 16:58:30 by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

/**
 * LStatusId generated by hbm2java
 */
@Embeddable
public class LStatusId implements java.io.Serializable {

	private String probeId;
	private Integer messungsId;
	private short SId;

	public LStatusId() {
	}

	public LStatusId(String probeId, Integer messungsId, short SId) {
		this.probeId = probeId;
		this.messungsId = messungsId;
		this.SId = SId;
	}

	@Column(name = "probe_id", nullable = false, length = 20)
	public String getProbeId() {
		return this.probeId;
	}

	public void setProbeId(String probeId) {
		this.probeId = probeId;
	}

	@Column(name = "messungs_id", nullable = false)
	public Integer getMessungsId() {
		return this.messungsId;
	}

	public void setMessungsId(Integer messungsId) {
		this.messungsId = messungsId;
	}

	@Column(name = "s_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "STATUS_ID_STORE")
	public short getSId() {
		return this.SId;
	}

	public void setSId(short SId) {
		this.SId = SId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof LStatusId))
			return false;
		LStatusId castOther = (LStatusId) other;

		return ((this.getProbeId() == castOther.getProbeId()) || (this
				.getProbeId() != null && castOther.getProbeId() != null && this
				.getProbeId().equals(castOther.getProbeId())))
				&& (this.getMessungsId() == castOther.getMessungsId())
				&& (this.getSId() == castOther.getSId());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getProbeId() == null ? 0 : this.getProbeId().hashCode());
		result = 37 * result + this.getMessungsId();
		result = 37 * result + this.getSId();
		return result;
	}

}
