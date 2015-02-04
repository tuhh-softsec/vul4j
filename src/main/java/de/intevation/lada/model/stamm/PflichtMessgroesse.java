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
 * The persistent class for the pflicht_messgroesse database table.
 */
@Entity
@Table(name="pflicht_messgroesse")
public class PflichtMessgroesse implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    @Column(name="messgroesse_id")
    private Integer messgroesseId;

    @ManyToOne
    @JoinColumn(name="datenbasis_id")
    private Datenbasis datenbasis;

    @ManyToOne
    @JoinColumn(name="mmt_id")
    private MessMethode messMethode;

    //bi-directional many-to-one association to Umwelt
    @ManyToOne
    @JoinColumn(name="umw_id")
    private Umwelt umwelt;

    public PflichtMessgroesse() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMessgroesseId() {
        return this.messgroesseId;
    }

    public void setMessgroesseId(Integer messgroesseId) {
        this.messgroesseId = messgroesseId;
    }

    public Datenbasis getDatenbasi() {
        return this.datenbasis;
    }

    public void setDatenbasi(Datenbasis datenbasi) {
        this.datenbasis = datenbasi;
    }

    public MessMethode getMessMethode() {
        return this.messMethode;
    }

    public void setMessMethode(MessMethode messMethode) {
        this.messMethode = messMethode;
    }

    public Umwelt getUmwelt() {
        return this.umwelt;
    }

    public void setUmwelt(Umwelt umwelt) {
        this.umwelt = umwelt;
    }

}
