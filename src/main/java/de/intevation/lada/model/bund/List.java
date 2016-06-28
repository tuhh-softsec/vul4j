/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.model.bund;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the list database table.
 */
@Entity
@Table(name="list")
public class List implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    private String beschreibuing;

    private String bezeichnung;

    @Column(name="gueltig_bis")
    private Timestamp gueltigBis;

    @Column(name="letzte_aenderung")
    private Timestamp letzteAenderung;

    private String typ;

    public List() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBeschreibuing() {
        return this.beschreibuing;
    }

    public void setBeschreibuing(String beschreibuing) {
        this.beschreibuing = beschreibuing;
    }

    public String getBezeichnung() {
        return this.bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public Timestamp getGueltigBis() {
        return this.gueltigBis;
    }

    public void setGueltigBis(Timestamp gueltigBis) {
        this.gueltigBis = gueltigBis;
    }

    public Timestamp getLetzteAenderung() {
        return this.letzteAenderung;
    }

    public String getTyp() {
        return this.typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

}
