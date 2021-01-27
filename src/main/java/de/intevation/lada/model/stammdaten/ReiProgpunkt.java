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
 * The persistent class for the rei_progpunkt database table.
 *
 */
@Entity
@Table(name = "rei_progpunkt")
public class ReiProgpunkt implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    @Column(name = "rei_prog_punkt")
    private String reiProgPunkt;

    private String reiid;

    public ReiProgpunkt() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReiProgPunkt() {
        return this.reiProgPunkt;
    }

    public void setReiProgPunkt(String reiProgPunkt) {
        this.reiProgPunkt = reiProgPunkt;
    }

    public String getReiid() {
        return this.reiid;
    }

    public void setReiid(String reiid) {
        this.reiid = reiid;
    }

}
