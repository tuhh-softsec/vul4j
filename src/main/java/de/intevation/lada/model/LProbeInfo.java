package de.intevation.lada.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the l_probe_info database table.
 * 
 */
@Entity
@Table(name="l_probe_info")
public class LProbeInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private LProbeInfoId lprobeInfoId;

	@Column(name="ba_id")
	private String baId;

	@Column(name="datenbasis_id")
	private Integer datenbasisId;

	@Column(name="erzeuger_id")
	private Integer erzeugerId;

	@Column(name="hauptproben_nr")
	private String hauptprobenNr;

	@Column(name="letzte_aenderung")
	private Timestamp letzteAenderung;

	@Column(name="media")
	private String media;

	@Column(name="media_desk")
	private String mediaDesk;

	@Column(name="mittelungsdauer")
	private Long mittelungsdauer;

	@Column(name="mp_kat")
	private Character mpKat;

	@Column(name="mpl_id")
	private String mplId;

	@Column(name="mpr_id")
	private Integer mprId;

	@Column(name="mst_id")
	private String mstId;

	@Column(name="nebenproben_nr", insertable = false, updatable = false)
	private String nebenprobenNr;

	@Column(name="netzbetreiber_id")
	private String netzbetreiberId;

	@Column(name="probe_id", insertable = false, updatable = false)
	private String probeId;

	@Column(name="probe_nehmer_id")
	private Integer probeNehmerId;

	@Column(name="probeentnahme_beginn")
	private Timestamp probeentnahmeBeginn;

	@Column(name="probeentnahme_ende")
	private Timestamp probeentnahmeEnde;

	@Column(name="probenart_id")
	private Integer probenartId;

	@Column(name="solldatum_beginn")
	private Timestamp solldatumBeginn;

	@Column(name="solldatum_ende")
	private Timestamp solldatumEnde;

	@Column(name="test")
	private Boolean test;

	@Column(name="umw_id")
	private String umwId;

	@Column(name="messmethode")
	private String messmethode;

	@Column(name="fertig")
	private Boolean fertig;

	@Column(name="readonly")
	private Boolean readonly;

	public LProbeInfo() {
	}

	public String getBaId() {
		return this.baId;
	}

	public void setBaId(String baId) {
		this.baId = baId;
	}

	public Integer getDatenbasisId() {
		return this.datenbasisId;
	}

	public void setDatenbasisId(Integer datenbasisId) {
		this.datenbasisId = datenbasisId;
	}

	public Integer getErzeugerId() {
		return this.erzeugerId;
	}

	public void setErzeugerId(Integer erzeugerId) {
		this.erzeugerId = erzeugerId;
	}

	public String getHauptprobenNr() {
		return this.hauptprobenNr;
	}

	public void setHauptprobenNr(String hauptprobenNr) {
		this.hauptprobenNr = hauptprobenNr;
	}

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

	public Character getMpKat() {
		return this.mpKat;
	}

	public void setMpKat(Character mpKat) {
		this.mpKat = mpKat;
	}

	public String getMplId() {
		return this.mplId;
	}

	public void setMplId(String mplId) {
		this.mplId = mplId;
	}

	public Integer getMprId() {
		return this.mprId;
	}

	public void setMprId(Integer mprId) {
		this.mprId = mprId;
	}

	public String getMstId() {
		return this.mstId;
	}

	public void setMstId(String mstId) {
		this.mstId = mstId;
	}

	public String getNebenprobenNr() {
		return this.nebenprobenNr;
	}

	public void setNebenprobenNr(String nebenprobenNr) {
		this.nebenprobenNr = nebenprobenNr;
	}

	public String getNetzbetreiberId() {
		return this.netzbetreiberId;
	}

	public void setNetzbetreiberId(String netzbetreiberId) {
		this.netzbetreiberId = netzbetreiberId;
	}

	public String getProbeId() {
		return this.probeId;
	}

	public void setProbeId(String probeId) {
		this.probeId = probeId;
	}

	public Integer getProbeNehmerId() {
		return this.probeNehmerId;
	}

	public void setProbeNehmerId(Integer probeNehmerId) {
		this.probeNehmerId = probeNehmerId;
	}

	public Timestamp getProbeentnahmeBeginn() {
		return this.probeentnahmeBeginn;
	}

	public void setProbeentnahmeBeginn(Timestamp probeentnahmeBeginn) {
		this.probeentnahmeBeginn = probeentnahmeBeginn;
	}

	public Timestamp getProbeentnahmeEnde() {
		return this.probeentnahmeEnde;
	}

	public void setProbeentnahmeEnde(Timestamp probeentnahmeEnde) {
		this.probeentnahmeEnde = probeentnahmeEnde;
	}

	public Integer getProbenartId() {
		return this.probenartId;
	}

	public void setProbenartId(Integer probenartId) {
		this.probenartId = probenartId;
	}

	public Timestamp getSolldatumBeginn() {
		return this.solldatumBeginn;
	}

	public void setSolldatumBeginn(Timestamp solldatumBeginn) {
		this.solldatumBeginn = solldatumBeginn;
	}

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

	public String getUmwId() {
		return this.umwId;
	}

	public void setUmwId(String umwId) {
		this.umwId = umwId;
	}

    public String getMessmethode() {
        return messmethode;
    }

    public void setMessmethode(String messmethode) {
        this.messmethode = messmethode;
    }

    public Boolean getFertig() {
        return fertig;
    }

    public void setFertig(Boolean fertig) {
        this.fertig = fertig;
    }

    public Boolean getReadonly() {
        return readonly;
    }

    public void setReadonly(Boolean readonly) {
        this.readonly = readonly;
    }

    public LProbe toLProbe() {
        LProbe probe = new LProbe();
        probe.setMstId(mstId);
        probe.setUmwId(umwId);
        probe.setTest(test);
        probe.setHauptprobenNr(hauptprobenNr);
        probe.setBaId(baId);
        probe.setMediaDesk(mediaDesk);
        probe.setMedia(media);
        probe.setProbenartId(probenartId);
        probe.setDatenbasisId(datenbasisId);
        probe.setProbeentnahmeBeginn(probeentnahmeBeginn);
        probe.setProbeentnahmeEnde(probeentnahmeEnde);
        probe.setMittelungsdauer(mittelungsdauer);
        probe.setLetzteAenderung(letzteAenderung);
        probe.setErzeugerId(erzeugerId);
        probe.setProbeNehmerId(probeNehmerId);
        probe.setMpKat(mpKat);
        probe.setMplId(mplId);
        probe.setMprId(mprId);
        probe.setSolldatumBeginn(solldatumBeginn);
        probe.setSolldatumEnde(solldatumEnde);
        probe.setNetzbetreiberId(netzbetreiberId);

        return probe;
    }
}