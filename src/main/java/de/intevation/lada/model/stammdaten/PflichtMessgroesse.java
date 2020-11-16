/* Copyright (C) 2015 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.model.stammdaten;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the pflicht_messgroesse database table.
 *
 */
@Entity
@Table(name="pflicht_messgroesse")
public class PflichtMessgroesse implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    @Column(name="messgroesse_id")
    private Integer messgroesseId;

    @Column(name="datenbasis_id")
    private Integer datenbasisId;

    @Column(name="mmt_id")
    private String messMethodeId;

    @Column(name="umw_id")
    private String umwId;

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

    public Integer getDatenbasisId() {
        return this.datenbasisId;
    }

    public void setDatenbasisId(Integer datenbasisId) {
        this.datenbasisId = datenbasisId;
    }

    public String getMessMethodeId() {
        return this.messMethodeId;
    }

    public void setMessMethodeId(String messMethodeId) {
        this.messMethodeId = messMethodeId;
    }

    public String getUmwId() {
        return this.umwId;
    }

    public void setUmwId(String umwId) {
        this.umwId = umwId;
    }

}
