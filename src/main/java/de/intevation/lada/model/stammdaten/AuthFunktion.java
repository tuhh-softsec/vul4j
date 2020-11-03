package de.intevation.lada.model.stammdaten;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the auth_funktion database table.
 *
 */
@Entity
@Table(name="auth_funktion")
public class AuthFunktion implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    private String funktion;

    public AuthFunktion() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFunktion() {
        return this.funktion;
    }

    public void setFunktion(String funktion) {
        this.funktion = funktion;
    }

}
