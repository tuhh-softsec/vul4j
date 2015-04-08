/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.model.land;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;


/**
 * The persistent class for the messwert database table.
 */
@Entity
@Table(name="messwert")
public class LMesswert extends de.intevation.lada.model.Messwert {
    private static final long serialVersionUID = 1L;

    @Column(name="tree_modified", insertable=false, updatable=false)
    private Timestamp treeModified;

    @OneToOne
    @JoinColumn(name="messungs_id", insertable=false, updatable=false)
    private LMessung messung;

    @Transient
    private Timestamp parentModified;

    public Timestamp getTreeModified() {
        return treeModified;
    }

    public void setTreeModified(Timestamp treeModified) {
        this.treeModified = treeModified;
    }

    public Timestamp getParentModified() {
        if (this.parentModified == null && this.messung != null) {
            return this.messung.getTreeModified();
        }
        return this.parentModified;
    }

    public void setParentModified(Timestamp parentModified) {
        this.parentModified = parentModified;
    }
}
