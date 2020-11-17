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
import javax.persistence.Embeddable;

/**
 * The MmtMessgroesse primary key.
 */
@Embeddable
public class MmtMessgroessePK implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "messgroesse_id")
    private Integer messgroesseId;

    @Column(name = "mmt_id")
    private String mmtId;

    public MmtMessgroessePK() {
    }
    public Integer getMessgroessengruppeId() {
        return this.messgroesseId;
    }
    public void setMessgroessengruppeId(Integer mgId) {
        this.messgroesseId = mgId;
    }
    public String getMmtId() {
        return this.mmtId;
    }
    public void setMmtId(String mmtId) {
        this.mmtId = mmtId;
    }

    /**
     * equals operator.
     * @param other the other object
     * @return true if the objects are equal
     */
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MmtMessgroessePK)) {
            return false;
        }
        MmtMessgroessePK castOther = (MmtMessgroessePK) other;
        return
            this.messgroesseId.equals(castOther.messgroesseId)
            && this.mmtId.equals(castOther.mmtId);
    }

    /**
     * get the objects hash code.
     * @return the hash
     */
    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + this.messgroesseId.hashCode();
        hash = hash * prime + this.mmtId.hashCode();
        return hash;
    }
}
