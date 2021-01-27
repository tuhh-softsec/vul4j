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
 * The persistent class for the probenart database table.
 *
 */
@Entity
public class Probenart implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    private String beschreibung;

    private String probenart;

    @Column(name = "probenart_eudf_id")
    private String probenartEudfId;

    public Probenart() {
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

    public String getProbenart() {
        return this.probenart;
    }

    public void setProbenart(String probenart) {
        this.probenart = probenart;
    }

    public String getProbenartEudfId() {
        return this.probenartEudfId;
    }

    public void setProbenartEudfId(String probenartEudfId) {
        this.probenartEudfId = probenartEudfId;
    }

}
