/* Copyright (C) 2015 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.model.stammdaten;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;


/**
 * The persistent class for the deskriptoren database table.
 *
 */
@Entity
public class Deskriptoren implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    private String bedeutung;

    private String beschreibung;

    private Integer ebene;

    @Column(name = "s_xx")
    private Integer sXx;

    private Integer sn;

    private Integer vorgaenger;

    public Deskriptoren() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBedeutung() {
        return this.bedeutung;
    }

    public void setBedeutung(String bedeutung) {
        this.bedeutung = bedeutung;
    }

    public String getBeschreibung() {
        return this.beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public Integer getEbene() {
        return this.ebene;
    }

    public void setEbene(Integer ebene) {
        this.ebene = ebene;
    }

    public Integer getSXx() {
        return this.sXx;
    }

    public void setSXx(Integer s) {
        this.sXx = s;
    }

    public Integer getSn() {
        return this.sn;
    }

    public void setSn(Integer sn) {
        this.sn = sn;
    }

    public Integer getVorgaenger() {
        return this.vorgaenger;
    }

    public void setVorgaenger(Integer vorgaenger) {
        this.vorgaenger = vorgaenger;
    }

}
