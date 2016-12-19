package de.intevation.lada.model.stammdaten;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the betriebsart database table.
 * 
 */
@Entity
@Table(name="betriebsart")
public class Betriebsart implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    private String name;

    public Betriebsart() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
