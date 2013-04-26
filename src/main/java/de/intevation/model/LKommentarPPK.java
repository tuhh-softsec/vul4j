package de.intevation.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the l_kommentar_p database table.
 * 
 */
@Embeddable
public class LKommentarPPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	private String probeId;
	private Integer kId;

	public LKommentarPPK() {
	}

	@Column(name="probe_id")
	public String getProbeId() {
		return this.probeId;
	}
	public void setProbeId(String probeId) {
		this.probeId = probeId;
	}

	@Column(name="k_id")
	public Integer getKId() {
		return this.kId;
	}
	public void setKId(Integer kId) {
		this.kId = kId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof LKommentarPPK)) {
			return false;
		}
		LKommentarPPK castOther = (LKommentarPPK)other;
		return 
			this.probeId.equals(castOther.probeId)
			&& this.kId.equals(castOther.kId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.probeId.hashCode();
		hash = hash * prime + this.kId.hashCode();
		
		return hash;
	}
}