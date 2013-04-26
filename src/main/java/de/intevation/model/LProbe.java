package de.intevation.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the l_probe database table.
 * 
 */
@Entity
@Table(name="l_probe")
public class LProbe implements Serializable {
	private static final long serialVersionUID = 1L;
	private String probeId;
	private String baId;
	private Integer datenbasisId;
	private Integer erzeugerId;
	private String hauptprobenNr;
	private Timestamp letzteAenderung;
	private String media;
	private String mediaDesk;
	private Long mittelungsdauer;
	private String mpKat;
	private String mplId;
	private Integer mprId;
	private String mstId;
	private String netzbetreiberId;
	private Integer probeNehmerId;
	private Timestamp probeentnahmeBeginn;
	private Timestamp probeentnahmeEnde;
	private Integer probenartId;
	private Timestamp solldatumBeginn;
	private Timestamp solldatumEnde;
	private Boolean test;
	private String umwId;
	private List<LKommentarP> LKommentarPs;

	public LProbe() {
	}


	@Id
	@Column(name="probe_id")
	public String getProbeId() {
		return this.probeId;
	}

	public void setProbeId(String probeId) {
		this.probeId = probeId;
	}


	@Column(name="ba_id")
	public String getBaId() {
		return this.baId;
	}

	public void setBaId(String baId) {
		this.baId = baId;
	}


	@Column(name="datenbasis_id")
	public Integer getDatenbasisId() {
		return this.datenbasisId;
	}

	public void setDatenbasisId(Integer datenbasisId) {
		this.datenbasisId = datenbasisId;
	}


	@Column(name="erzeuger_id")
	public Integer getErzeugerId() {
		return this.erzeugerId;
	}

	public void setErzeugerId(Integer erzeugerId) {
		this.erzeugerId = erzeugerId;
	}


	@Column(name="hauptproben_nr")
	public String getHauptprobenNr() {
		return this.hauptprobenNr;
	}

	public void setHauptprobenNr(String hauptprobenNr) {
		this.hauptprobenNr = hauptprobenNr;
	}


	@Column(name="letzte_aenderung")
	public Timestamp getLetzteAenderung() {
		return this.letzteAenderung;
	}

	public void setLetzteAenderung(Timestamp letzteAenderung) {
		this.letzteAenderung = letzteAenderung;
	}


	public String getMedia() {
		return this.media;
	}

	public void setMedia(String media) {
		this.media = media;
	}


	@Column(name="media_desk")
	public String getMediaDesk() {
		return this.mediaDesk;
	}

	public void setMediaDesk(String mediaDesk) {
		this.mediaDesk = mediaDesk;
	}


	public Long getMittelungsdauer() {
		return this.mittelungsdauer;
	}

	public void setMittelungsdauer(Long mittelungsdauer) {
		this.mittelungsdauer = mittelungsdauer;
	}


	@Column(name="mp_kat")
	public String getMpKat() {
		return this.mpKat;
	}

	public void setMpKat(String mpKat) {
		this.mpKat = mpKat;
	}


	@Column(name="mpl_id")
	public String getMplId() {
		return this.mplId;
	}

	public void setMplId(String mplId) {
		this.mplId = mplId;
	}


	@Column(name="mpr_id")
	public Integer getMprId() {
		return this.mprId;
	}

	public void setMprId(Integer mprId) {
		this.mprId = mprId;
	}


	@Column(name="mst_id")
	public String getMstId() {
		return this.mstId;
	}

	public void setMstId(String mstId) {
		this.mstId = mstId;
	}


	@Column(name="netzbetreiber_id")
	public String getNetzbetreiberId() {
		return this.netzbetreiberId;
	}

	public void setNetzbetreiberId(String netzbetreiberId) {
		this.netzbetreiberId = netzbetreiberId;
	}


	@Column(name="probe_nehmer_id")
	public Integer getProbeNehmerId() {
		return this.probeNehmerId;
	}

	public void setProbeNehmerId(Integer probeNehmerId) {
		this.probeNehmerId = probeNehmerId;
	}


	@Column(name="probeentnahme_beginn")
	public Timestamp getProbeentnahmeBeginn() {
		return this.probeentnahmeBeginn;
	}

	public void setProbeentnahmeBeginn(Timestamp probeentnahmeBeginn) {
		this.probeentnahmeBeginn = probeentnahmeBeginn;
	}


	@Column(name="probeentnahme_ende")
	public Timestamp getProbeentnahmeEnde() {
		return this.probeentnahmeEnde;
	}

	public void setProbeentnahmeEnde(Timestamp probeentnahmeEnde) {
		this.probeentnahmeEnde = probeentnahmeEnde;
	}


	@Column(name="probenart_id")
	public Integer getProbenartId() {
		return this.probenartId;
	}

	public void setProbenartId(Integer probenartId) {
		this.probenartId = probenartId;
	}


	@Column(name="solldatum_beginn")
	public Timestamp getSolldatumBeginn() {
		return this.solldatumBeginn;
	}

	public void setSolldatumBeginn(Timestamp solldatumBeginn) {
		this.solldatumBeginn = solldatumBeginn;
	}


	@Column(name="solldatum_ende")
	public Timestamp getSolldatumEnde() {
		return this.solldatumEnde;
	}

	public void setSolldatumEnde(Timestamp solldatumEnde) {
		this.solldatumEnde = solldatumEnde;
	}


	public Boolean getTest() {
		return this.test;
	}

	public void setTest(Boolean test) {
		this.test = test;
	}


	@Column(name="umw_id")
	public String getUmwId() {
		return this.umwId;
	}

	public void setUmwId(String umwId) {
		this.umwId = umwId;
	}


	//bi-directional many-to-one association to LKommentarP
	@OneToMany(mappedBy="LProbe", cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
	public List<LKommentarP> getLKommentarPs() {
		return this.LKommentarPs;
	}

	public void setLKommentarPs(List<LKommentarP> LKommentarPs) {
		this.LKommentarPs = LKommentarPs;
	}

	public LKommentarP addLKommentarP(LKommentarP LKommentarP) {
		getLKommentarPs().add(LKommentarP);
		LKommentarP.setLProbe(this);

		return LKommentarP;
	}

	public LKommentarP removeLKommentarP(LKommentarP LKommentarP) {
		getLKommentarPs().remove(LKommentarP);
		LKommentarP.setLProbe(null);

		return LKommentarP;
	}

}