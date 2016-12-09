package de.intevation.lada.model.stammdaten;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;


/**
 * The persistent class for the staat database table.
 * 
 */
@Entity
public class Staat implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    private Boolean eu;

    @Column(name="hkl_id")
    private Integer hklId;

    @Column(name="kda_id")
    private Integer kdaId;

    @Column(name="koord_x_extern")
    private String koordXExtern;

    @Column(name="koord_y_extern")
    private String koordYExtern;

    private String staat;

    @Column(name="staat_iso")
    private String staatIso;

    @Column(name="staat_kurz")
    private String staatKurz;

    public Staat() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getEu() {
        return this.eu;
    }

    public void setEu(Boolean eu) {
        this.eu = eu;
    }

    public Integer getHklId() {
        return this.hklId;
    }

    public void setHklId(Integer hklId) {
        this.hklId = hklId;
    }

    public Integer getKdaId() {
        return this.kdaId;
    }

    public void setKdaId(Integer kdaId) {
        this.kdaId = kdaId;
    }

    public String getKoordXExtern() {
        return this.koordXExtern;
    }

    public void setKoordXExtern(String koordXExtern) {
        this.koordXExtern = koordXExtern;
    }

    public String getKoordYExtern() {
        return this.koordYExtern;
    }

    public void setKoordYExtern(String koordYExtern) {
        this.koordYExtern = koordYExtern;
    }

    public String getStaat() {
        return this.staat;
    }

    public void setStaat(String staat) {
        this.staat = staat;
    }

    public String getStaatIso() {
        return this.staatIso;
    }

    public void setStaatIso(String staatIso) {
        this.staatIso = staatIso;
    }

    public String getStaatKurz() {
        return this.staatKurz;
    }

    public void setStaatKurz(String staatKurz) {
        this.staatKurz = staatKurz;
    }

}
