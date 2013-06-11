package de.intevation.lada.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the s_netz_betreiber database table.
 * 
 */
@Entity
@Table(name="s_netz_betreiber")
public class SNetzBetreiber implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="netzbetreiber_id")
	private String netzbetreiberId;

	private String aktiv;

	@Column(name="idf_netzbetreiber")
	private String idfNetzbetreiber;

	@Column(name="is_bmn")
	private String isBmn;

	private String mailverteiler;

	private String netzbetreiber;

	@Column(name="zust_mst_id")
	private String zustMstId;

	public SNetzBetreiber() {
	}

	public String getNetzbetreiberId() {
		return this.netzbetreiberId;
	}

	public void setNetzbetreiberId(String netzbetreiberId) {
		this.netzbetreiberId = netzbetreiberId;
	}

	public String getAktiv() {
		return this.aktiv;
	}

	public void setAktiv(String aktiv) {
		this.aktiv = aktiv;
	}

	public String getIdfNetzbetreiber() {
		return this.idfNetzbetreiber;
	}

	public void setIdfNetzbetreiber(String idfNetzbetreiber) {
		this.idfNetzbetreiber = idfNetzbetreiber;
	}

	public String getIsBmn() {
		return this.isBmn;
	}

	public void setIsBmn(String isBmn) {
		this.isBmn = isBmn;
	}

	public String getMailverteiler() {
		return this.mailverteiler;
	}

	public void setMailverteiler(String mailverteiler) {
		this.mailverteiler = mailverteiler;
	}

	public String getNetzbetreiber() {
		return this.netzbetreiber;
	}

	public void setNetzbetreiber(String netzbetreiber) {
		this.netzbetreiber = netzbetreiber;
	}

	public String getZustMstId() {
		return this.zustMstId;
	}

	public void setZustMstId(String zustMstId) {
		this.zustMstId = zustMstId;
	}

}