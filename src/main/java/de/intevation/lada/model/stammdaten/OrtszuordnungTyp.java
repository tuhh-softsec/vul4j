/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.model.stammdaten;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the ortszuordnung_typ database table.
 * 
 */
@Entity
@Table(name="ortszuordnung_typ")
public class OrtszuordnungTyp implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String ortstyp;

    public OrtszuordnungTyp() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrtstyp() {
        return this.ortstyp;
    }

    public void setOrtstyp(String ortstyp) {
        this.ortstyp = ortstyp;
    }

}
