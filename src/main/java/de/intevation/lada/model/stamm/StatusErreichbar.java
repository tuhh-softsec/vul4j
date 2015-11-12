/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.model.stamm;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the status_erreichbar database table.
 * 
 */
@Entity
@Table(name="status_erreichbar")
public class StatusErreichbar implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name="cur_stufe")
    private Integer curStufe;

    @Column(name="cur_wert")
    private Integer curWert;

    @Id
    @Column(name="wert_id")
    private Integer wertId;

    public StatusErreichbar() {
    }

    public Integer getCurStufe() {
        return this.curStufe;
    }

    public void setCurStufe(Integer curStufe) {
        this.curStufe = curStufe;
    }

    public Integer getCurWert() {
        return this.curWert;
    }

    public void setCurWert(Integer curWert) {
        this.curWert = curWert;
    }

    public Integer getWertId() {
        return this.wertId;
    }

    public void setWertId(Integer wertId) {
        this.wertId = wertId;
    }

}
