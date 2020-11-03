package de.intevation.lada.model.stammdaten;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the proben_zusatz database table.
 *
 */
@Entity
@Table(name="proben_zusatz")
public class ProbenZusatz implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String beschreibung;

    @Column(name="eudf_keyword")
    private String eudfKeyword;

    private String zusatzwert;

    @Column(name="meh_id")
    private Integer messEinheitId;

    public ProbenZusatz() {
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

    public String getEudfKeyword() {
        return this.eudfKeyword;
    }

    public void setEudfKeyword(String eudfKeyword) {
        this.eudfKeyword = eudfKeyword;
    }

    public String getZusatzwert() {
        return this.zusatzwert;
    }

    public void setZusatzwert(String zusatzwert) {
        this.zusatzwert = zusatzwert;
    }

    public Integer getMessEinheitId() {
        return this.messEinheitId;
    }

    public void setMessEinheitId(Integer messEinheitId) {
        this.messEinheitId = messEinheitId;
    }

}
