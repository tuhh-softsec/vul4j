package de.intevation.lada.model.stammdaten;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


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
