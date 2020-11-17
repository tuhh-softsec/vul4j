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
 * The persistent class for the messgroessen_gruppe database table.
 *
 */
@Entity
@Table(name = "messgroessen_gruppe")
public class MessgroessenGruppe implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    private String bezeichnung;

    @Column(name = "ist_leitnuklidgruppe")
    private String istLeitnuklidgruppe;

    public MessgroessenGruppe() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBezeichnung() {
        return this.bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public String getIstLeitnuklidgruppe() {
        return this.istLeitnuklidgruppe;
    }

    public void setIstLeitnuklidgruppe(String istLeitnuklidgruppe) {
        this.istLeitnuklidgruppe = istLeitnuklidgruppe;
    }
}
