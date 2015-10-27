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
import javax.persistence.Table;


/**
 * The persistent class for the status_stufe database table.
 */
@Entity
@Table(name="status_stufe")
public class StatusStufe implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    private String stufe;

    public StatusStufe() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStufe() {
        return this.stufe;
    }

    public void setStufe(String stufe) {
        this.stufe = stufe;
    }

}
