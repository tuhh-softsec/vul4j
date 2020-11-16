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
 * The persistent class for the rei_progpunkt_grp_umw_zuord database table.
 *
 */
@Entity
@Table(name="rei_progpunkt_grp_umw_zuord")
public class ReiProgpunktGrpUmwZuord implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    @Column(name="rei_progpunkt_grp_id")
    private Integer reiProgpunktGrpId;

    @Column(name="umw_id")
    private String umwId;

    public ReiProgpunktGrpUmwZuord() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getReiProgpunktGrpId() {
        return this.reiProgpunktGrpId;
    }

    public void setReiProgpunktGrpId(Integer reiProgpunktGrpId) {
        this.reiProgpunktGrpId = reiProgpunktGrpId;
    }

    public String getUmwId() {
        return this.umwId;
    }

    public void setUmwId(String umwId) {
        this.umwId = umwId;
    }

}
