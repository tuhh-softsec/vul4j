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
 * The persistent class for the status_reihenfolge database table.
 *
 */
@Entity
@Table(name="status_reihenfolge")
public class StatusReihenfolge implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    @Column(name="von_id")
    private Integer vonId;

    @Column(name="zu_id")
    private Integer zuId;

    public StatusReihenfolge() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVonId() {
        return this.vonId;
    }

    public void setVonId(Integer vonId) {
        this.vonId = vonId;
    }

    public Integer getZuId() {
        return this.zuId;
    }

    public void setZuId(Integer zuId) {
        this.zuId = zuId;
    }

}
