package de.intevation.lada.model.stamm;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the ort_typ database table.
 * 
 */
@Entity
@Table(name="ort_typ")
public class OrtTyp implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    @Column(name="ort_typ")
    private String ortTyp;

    public OrtTyp() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrtTyp() {
        return this.ortTyp;
    }

    public void setOrtTyp(String ortTyp) {
        this.ortTyp = ortTyp;
    }

}
