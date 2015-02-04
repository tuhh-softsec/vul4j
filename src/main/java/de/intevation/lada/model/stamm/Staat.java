/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.model.stamm;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * The persistent class for the staat database table.
 */
@Entity
@Table(name="staat")
public class Staat implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    private String eu;

    @Column(name="hkl_id")
    private Integer hklId;

    @Column(name="koord_x_extern")
    private String koordXExtern;

    @Column(name="koord_y_extern")
    private String koordYExtern;

    private String staat;

    @Column(name="staat_iso")
    private String staatIso;

    @Column(name="staat_kurz")
    private String staatKurz;

    //bi-directional many-to-one association to KoordinatenArt
    @ManyToOne
    @JoinColumn(name="kda_id")
    private KoordinatenArt koordinatenArt;

    public Staat() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEu() {
        return this.eu;
    }

    public void setEu(String eu) {
        this.eu = eu;
    }

    public Integer getHklId() {
        return this.hklId;
    }

    public void setHklId(Integer hklId) {
        this.hklId = hklId;
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

    public KoordinatenArt getKoordinatenArt() {
        return this.koordinatenArt;
    }

    public void setKoordinatenArt(KoordinatenArt koordinatenArt) {
        this.koordinatenArt = koordinatenArt;
    }

}
