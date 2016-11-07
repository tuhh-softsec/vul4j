package de.intevation.lada.model.stammdaten;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the ortszusatz database table.
 * 
 */
@Entity
@Table(name="ortszusatz")
public class Ortszusatz implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="ozs_id")
    private String ozsId;

    private String ortszusatz;

    public Ortszusatz() {
    }

    public String getOzsId() {
        return this.ozsId;
    }

    public void setOzsId(String ozsId) {
        this.ozsId = ozsId;
    }

    public String getOrtszusatz() {
        return this.ortszusatz;
    }

    public void setOrtszusatz(String ortszusatz) {
        this.ortszusatz = ortszusatz;
    }

}
