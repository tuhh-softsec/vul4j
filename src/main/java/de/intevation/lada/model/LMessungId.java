package de.intevation.lada.model;

// Generated 21.05.2013 16:58:30 by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * LMessungId generated by hbm2java
 */
@Embeddable
public class LMessungId implements java.io.Serializable {

	private String probeId;
	private Integer messungsId;

	public LMessungId() {
	}

	public LMessungId(String probeId, Integer messungsId) {
		this.probeId = probeId;
		this.messungsId = messungsId;
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

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof LMessungId))
			return false;
		LMessungId castOther = (LMessungId) other;

		return ((this.getProbeId() == castOther.getProbeId()) || (this
				.getProbeId() != null && castOther.getProbeId() != null && this
				.getProbeId().equals(castOther.getProbeId())))
				&& (this.getMessungsId() == castOther.getMessungsId());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getProbeId() == null ? 0 : this.getProbeId().hashCode());
		result = 37 * result + this.getMessungsId();
		return result;
	}

}
