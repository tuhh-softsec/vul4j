/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
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
 * The persistent class for the rei_progpunkt_gruppe database table.
 *
 */
@Entity
@Table(name = "rei_progpunkt_gruppe")
public class ReiProgpunktGruppe implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    private String beschreibung;

    @Column(name = "rei_prog_punkt_gruppe")
    private String reiProgPunktGruppe;

    public ReiProgpunktGruppe() {
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

    public String getReiProgPunktGruppe() {
        return this.reiProgPunktGruppe;
    }

    public void setReiProgPunktGruppe(String reiProgPunktGruppe) {
        this.reiProgPunktGruppe = reiProgPunktGruppe;
    }

}
