/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.model.bund;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the list_zuordnung database table.
 */
@Entity
@Table(name="list_zuordnung")
public class ListZuordnung implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="list_id")
    private Integer listId;

    @Column(name="probe_id")
    private Integer probeId;

    public ListZuordnung() {
    }

    public Integer getListId() {
        return this.listId;
    }

    public void setListId(Integer listId) {
        this.listId = listId;
    }

    public Integer getProbeId() {
        return this.probeId;
    }

    public void setProbeId(Integer probeId) {
        this.probeId = probeId;
    }
}
