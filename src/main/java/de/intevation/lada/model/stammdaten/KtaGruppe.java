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
 * The persistent class for the kta_gruppe database table.
 *
 */
@Entity
@Table(name="kta_gruppe")
public class KtaGruppe implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    private String beschreibung;

    @Column(name="kta_gruppe")
    private String ktaGruppe;

    public KtaGruppe() {
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

    public String getKtaGruppe() {
        return this.ktaGruppe;
    }

    public void setKtaGruppe(String ktaGruppe) {
        this.ktaGruppe = ktaGruppe;
    }

}
