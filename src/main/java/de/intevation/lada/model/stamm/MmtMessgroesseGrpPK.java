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
 * The primary key class for the mmt_messgroesse_grp database table.
 */
@Embeddable
public class MmtMessgroesseGrpPK implements Serializable {
    //default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    @Column(name="messgroessengruppe_id")
    private Integer messgroessengruppeId;

    @Column(name="mmt_id")
    private String mmtId;

    public MmtMessgroesseGrpPK() {
    }
    public Integer getMessgroessengruppeId() {
        return this.messgroessengruppeId;
    }
    public void setMessgroessengruppeId(Integer messgroessengruppeId) {
        this.messgroessengruppeId = messgroessengruppeId;
    }
    public String getMmtId() {
        return this.mmtId;
    }
    public void setMmtId(String mmtId) {
        this.mmtId = mmtId;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MmtMessgroesseGrpPK)) {
            return false;
        }
        MmtMessgroesseGrpPK castOther = (MmtMessgroesseGrpPK)other;
        return 
            this.messgroessengruppeId.equals(castOther.messgroessengruppeId)
            && this.mmtId.equals(castOther.mmtId);
    }

    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + this.messgroessengruppeId.hashCode();
        hash = hash * prime + this.mmtId.hashCode();
        return hash;
    }
}
