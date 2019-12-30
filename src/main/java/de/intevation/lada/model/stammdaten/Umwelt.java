package de.intevation.lada.model.stammdaten;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the umwelt database table.
 * 
 */
@Entity
@Table(name="umwelt", schema="stamm")
public class Umwelt implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String beschreibung;

    @Column(name="meh_id")
    private Integer mehId;

    @Column(name="meh_id_2")
    private Integer secMehId;

    @Column(name="umwelt_bereich")
    private String umweltBereich;

    public Umwelt() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBeschreibung() {
        return this.beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public Integer getMehId() {
        return this.mehId;
    }

    public void setMehId(Integer mehId) {
        this.mehId = mehId;
    }

    public Integer getSecMehId() {
        return this.secMehId;
    }

    public void setSecMehId(Integer secMehId) {
        this.secMehId = secMehId;
    }

    public String getUmweltBereich() {
        return this.umweltBereich;
    }

    public void setUmweltBereich(String umweltBereich) {
        this.umweltBereich = umweltBereich;
    }
}
