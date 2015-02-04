/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.model.stamm;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * The persistent class for the proben_zusatz database table.
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

    @ManyToOne
    @JoinColumn(name="meh_id")
    private MessEinheit messEinheit;

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

    public MessEinheit getMessEinheit() {
        return this.messEinheit;
    }

    public void setMessEinheit(MessEinheit messEinheit) {
        this.messEinheit = messEinheit;
    }

}
