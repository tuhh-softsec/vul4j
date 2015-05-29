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
import javax.persistence.Table;


/**
 * The persistent class for the deskriptoren database table.
 */
@Entity
@Table(name="deskriptoren")
public class Deskriptoren implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    private String bedeutung;

    private String beschreibung;

    private Integer ebene;

    @Column(name="s_xx")
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

    public void setSXx(Integer sXx) {
        this.sXx = sXx;
    }

    public Integer getSn() {
        return this.sn;
    }

    public void setSn(Integer sn) {
        this.sn = sn;
    }

    /*
     public Deskriptoren getDeskriptoren() {
     return this.deskriptoren;
     }

     public void setDeskriptoren(Deskriptoren deskriptoren) {
     this.deskriptoren = deskriptoren;
     }

     public List<Deskriptoren> getDeskriptorens() {
     return this.deskriptorens;
     }

     public void setDeskriptorens(List<Deskriptoren> deskriptorens) {
     this.deskriptorens = deskriptorens;
     }

     public Deskriptoren addDeskriptoren(Deskriptoren deskriptoren) {
     getDeskriptorens().add(deskriptoren);
     deskriptoren.setDeskriptoren(this);

     return deskriptoren;
     }

     public Deskriptoren removeDeskriptoren(Deskriptoren deskriptoren) {
     getDeskriptorens().remove(deskriptoren);
     deskriptoren.setDeskriptoren(null);

     return deskriptoren;
     }
     */

    /**
     * @return the vorgaenger
     */
    public Integer getVorgaenger() {
        return vorgaenger;
    }

    /**
     * @param vorgaenger the vorgaenger to set
     */
    public void setVorgaenger(Integer vorgaenger) {
        this.vorgaenger = vorgaenger;
    }
}
