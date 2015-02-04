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
import javax.persistence.Table;


/**
 * The persistent class for the mess_einheit database table.
 */
@Entity
@Table(name="mess_einheit")
public class MessEinheit implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    private String beschreibung;

    private String einheit;

    @Column(name="eudf_messeinheit_id")
    private String eudfMesseinheitId;

    @Column(name="umrechnungs_faktor_eudf")
    private Long umrechnungsFaktorEudf;

    public MessEinheit() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBeschreibung() {
        return this.beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public String getEinheit() {
        return this.einheit;
    }

    public void setEinheit(String einheit) {
        this.einheit = einheit;
    }

    public String getEudfMesseinheitId() {
        return this.eudfMesseinheitId;
    }

    public void setEudfMesseinheitId(String eudfMesseinheitId) {
        this.eudfMesseinheitId = eudfMesseinheitId;
    }

    public Long getUmrechnungsFaktorEudf() {
        return this.umrechnungsFaktorEudf;
    }

    public void setUmrechnungsFaktorEudf(Long umrechnungsFaktorEudf) {
        this.umrechnungsFaktorEudf = umrechnungsFaktorEudf;
    }
}
