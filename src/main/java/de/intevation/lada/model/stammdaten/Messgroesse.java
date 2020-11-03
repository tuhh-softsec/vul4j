package de.intevation.lada.model.stammdaten;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;


/**
 * The persistent class for the messgroesse database table.
 *
 */
@Entity
public class Messgroesse implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    private String beschreibung;

    @Column(name="default_farbe")
    private String defaultFarbe;

    @Column(name="eudf_nuklid_id")
    private Long eudfNuklidId;

    @Column(name="idf_nuklid_key")
    private String idfNuklidKey;

    @Column(name="ist_leitnuklid")
    private Boolean istLeitnuklid;

    @Column(name="kennung_bvl")
    private String kennungBvl;

    private String messgroesse;

    public Messgroesse() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBeschreibung() {
        return this.beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public String getDefaultFarbe() {
        return this.defaultFarbe;
    }

    public void setDefaultFarbe(String defaultFarbe) {
        this.defaultFarbe = defaultFarbe;
    }

    public Long getEudfNuklidId() {
        return this.eudfNuklidId;
    }

    public void setEudfNuklidId(Long eudfNuklidId) {
        this.eudfNuklidId = eudfNuklidId;
    }

    public String getIdfNuklidKey() {
        return this.idfNuklidKey;
    }

    public void setIdfNuklidKey(String idfNuklidKey) {
        this.idfNuklidKey = idfNuklidKey;
    }

    public Boolean getIstLeitnuklid() {
        return this.istLeitnuklid;
    }

    public void setIstLeitnuklid(Boolean istLeitnuklid) {
        this.istLeitnuklid = istLeitnuklid;
    }

    public String getKennungBvl() {
        return this.kennungBvl;
    }

    public void setKennungBvl(String kennungBvl) {
        this.kennungBvl = kennungBvl;
    }

    public String getMessgroesse() {
        return this.messgroesse;
    }

    public void setMessgroesse(String messgroesse) {
        this.messgroesse = messgroesse;
    }
}
