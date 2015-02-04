/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.model.stamm;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the mg_grp database table.
 */
@Entity
@Table(name="mg_grp")
public class MgGrp implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private MgGrpPK id;

    public MgGrp() {
    }

    public MgGrpPK getId() {
        return this.id;
    }

    public void setId(MgGrpPK id) {
        this.id = id;
    }

}
