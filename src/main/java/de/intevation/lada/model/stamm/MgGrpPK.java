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
import javax.persistence.Embeddable;


/**
 * The primary key class for the mg_grp database table.
 */
@Embeddable
public class MgGrpPK implements Serializable {
    //default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    @Column(name="messgroessengruppe_id")
    private Integer messgroessengruppeId;

    @Column(name="messgroesse_id")
    private Integer messgroesseId;

    public MgGrpPK() {
    }
    public Integer getMessgroessengruppeId() {
        return this.messgroessengruppeId;
    }
    public void setMessgroessengruppeId(Integer messgroessengruppeId) {
        this.messgroessengruppeId = messgroessengruppeId;
    }
    public Integer getMessgroesseId() {
        return this.messgroesseId;
    }
    public void setMessgroesseId(Integer messgroesseId) {
        this.messgroesseId = messgroesseId;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MgGrpPK)) {
            return false;
        }
        MgGrpPK castOther = (MgGrpPK)other;
        return 
            this.messgroessengruppeId.equals(castOther.messgroessengruppeId)
            && this.messgroesseId.equals(castOther.messgroesseId);
    }

    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + this.messgroessengruppeId.hashCode();
        hash = hash * prime + this.messgroesseId.hashCode();
        return hash;
    }
}
