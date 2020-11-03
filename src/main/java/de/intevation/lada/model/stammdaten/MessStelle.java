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
import javax.persistence.Table;


/**
 * The persistent class for the mess_stelle database table.
 *
 */
@Entity
@Table(name="mess_stelle")
public class MessStelle implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String amtskennung;

    private String beschreibung;

    @Column(name="mess_stelle")
    private String messStelle;

    @Column(name="mst_typ")
    private String mstTyp;

    @Column(name="netzbetreiber_id")
    private String netzbetreiberId;

    public MessStelle() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAmtskennung() {
        return this.amtskennung;
    }

    public void setAmtskennung(String amtskennung) {
        this.amtskennung = amtskennung;
    }

    public String getBeschreibung() {
        return this.beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public String getMessStelle() {
        return this.messStelle;
    }

    public void setMessStelle(String messStelle) {
        this.messStelle = messStelle;
    }

    public String getMstTyp() {
        return this.mstTyp;
    }

    public void setMstTyp(String mstTyp) {
        this.mstTyp = mstTyp;
    }

    public String getNetzbetreiberId() {
        return this.netzbetreiberId;
    }
}
