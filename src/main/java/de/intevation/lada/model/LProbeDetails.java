package de.intevation.lada.model;

import java.io.Serializable;
import java.util.List;


public class LProbeDetails extends LProbe
{

    private List<LOrt> lorts;
    private List<LKommentarP> lkommentar;

    public void setLprobe(LProbe lprobe) {
      this.setProbeId(lprobe.getProbeId());
      this.setMstId(lprobe.getMstId());
      this.setUmwId(lprobe.getUmwId());
      this.setTest(lprobe.isTest());
      this.setHauptprobenNr(lprobe.getHauptprobenNr());
      this.setBaId(lprobe.getBaId());
      this.setMediaDesk(lprobe.getMediaDesk());
      this.setMedia(lprobe.getMedia());
      this.setProbenartId(lprobe.getProbenartId());
      this.setDatenbasisId(lprobe.getDatenbasisId());
      this.setProbeentnahmeBeginn(lprobe.getProbeentnahmeBeginn());
      this.setProbeentnahmeEnde(lprobe.getProbeentnahmeEnde());
      this.setMittelungsdauer(lprobe.getMittelungsdauer());
      this.setLetzteAenderung(lprobe.getLetzteAenderung());
      this.setErzeugerId(lprobe.getErzeugerId());
      this.setProbeNehmerId(lprobe.getProbeNehmerId());
      this.setMpKat(lprobe.getMpKat());
      this.setMplId(lprobe.getMplId());
      this.setMprId(lprobe.getMprId());
      this.setSolldatumBeginn(lprobe.getSolldatumBeginn());
      this.setSolldatumEnde(lprobe.getSolldatumEnde());
    }

    public List<LOrt> getLort() {
        return lorts;
    }

    public void setLorts(List<LOrt> lorts) {
        this.lorts = lorts;
    }

    public List<LKommentarP> getLkommentar() {
        return lkommentar;
    }

    public void setLkommentar(List<LKommentarP> lkommentar) {
        this.lkommentar = lkommentar;
    }
}
