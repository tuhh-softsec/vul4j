/* Copyright (C) 2015 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.model.stammdaten;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the mess_methode database table.
 *
 */
@Entity
@Table(name="mess_methode")
public class MessMethode implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String beschreibung;

    private String messmethode;

    public MessMethode() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBeschreibung() {
        return this.beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public String getMessmethode() {
        return this.messmethode;
    }

    public void setMessmethode(String messmethode) {
        this.messmethode = messmethode;
    }
}
