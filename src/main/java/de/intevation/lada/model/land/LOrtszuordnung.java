package de.intevation.lada.model.land;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the ortszuordnung database table.
 */
@Entity
@Table(name="ortszuordnung")
public class LOrtszuordnung extends de.intevation.lada.model.Ortszuordnung {
    private static final long serialVersionUID = 1L;

    @Column(name="tree_modified")
    private Timestamp treeModified;

    public LOrtszuordnung() {
    }

    public Timestamp getTreeModified() {
        return this.treeModified;
    }

    public void setTreeModified(Timestamp treeModified) {
        this.treeModified = treeModified;
    }
}
