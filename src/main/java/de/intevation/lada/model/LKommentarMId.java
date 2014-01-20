/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3) 
 * and comes with ABSOLUTELY NO WARRANTY! Check out 
 * the documentation coming with IMIS-Labordaten-Application for details. 
 */
package de.intevation.lada.model;

// Generated 21.05.2013 16:58:30 by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * LKommentarMId generated by hbm2java
 */
@Embeddable
public class LKommentarMId implements java.io.Serializable {

	private String probeId;
	private Integer messungsId;
	private int KId;

	public LKommentarMId() {
	}

	public LKommentarMId(String probeId, Integer messungsId, int KId) {
		this.probeId = probeId;
		this.messungsId = messungsId;
		this.KId = KId;
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

	@Column(name = "k_id", nullable = false)
	public int getKId() {
		return this.KId;
	}

	public void setKId(int KId) {
		this.KId = KId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof LKommentarMId))
			return false;
		LKommentarMId castOther = (LKommentarMId) other;

		return ((this.getProbeId() == castOther.getProbeId()) || (this
				.getProbeId() != null && castOther.getProbeId() != null && this
				.getProbeId().equals(castOther.getProbeId())))
				&& (this.getMessungsId() == castOther.getMessungsId())
				&& (this.getKId() == castOther.getKId());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getProbeId() == null ? 0 : this.getProbeId().hashCode());
		result = 37 * result + this.getMessungsId();
		result = 37 * result + this.getKId();
		return result;
	}

}
