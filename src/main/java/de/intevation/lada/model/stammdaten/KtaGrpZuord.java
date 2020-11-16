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
 * The persistent class for the kta_grp_zuord database table.
 *
 */
@Entity
@Table(name="kta_grp_zuord")
public class KtaGrpZuord implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    @Column(name="kta_grp_id")
    private Integer ktaGrpId;

    @Column(name="kta_id")
    private Integer ktaId;

    public KtaGrpZuord() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getKtaGrpId() {
        return this.ktaGrpId;
    }

    public void setKtaGrpId(Integer ktaGrpId) {
        this.ktaGrpId = ktaGrpId;
    }

    public Integer getKtaId() {
        return this.ktaId;
    }

    public void setKtaId(Integer ktaId) {
        this.ktaId = ktaId;
    }

}
