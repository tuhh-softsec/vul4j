/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.model.stamm;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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

    //bi-directional many-to-one association to StatusKombi
    @ManyToOne
    @JoinColumn(name="von_id")
    private StatusKombi von;

    //bi-directional many-to-one association to StatusKombi
    @ManyToOne
    @JoinColumn(name="zu_id")
    private StatusKombi zu;

    public StatusReihenfolge() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public StatusKombi getVon() {
        return this.von;
    }

    public void setVon(StatusKombi von) {
        this.von = von;
    }

    public StatusKombi getZu() {
        return this.zu;
    }

    public void setZu(StatusKombi zu) {
        this.zu = zu;
    }

}
