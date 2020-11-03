/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the messprogramm_transfer database table.
 *
 */
@Entity
@Table(name="messprogramm_transfer")
public class MessprogrammTransfer implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @Column(name="ba_id")
    private Integer baId;

    @Column(name="datenbasis_id")
    private Integer datenbasisId;

    @Column(name="messprogramm_c")
    private String messprogrammC;

    @Column(name="messprogramm_s")
    private String messprogrammS;

    public MessprogrammTransfer() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBaId() {
        return this.baId;
    }

    public void setBaId(Integer baId) {
        this.baId = baId;
    }

    public Integer getDatenbasisId() {
        return this.datenbasisId;
    }

    public void setDatenbasisId(Integer datenbasisId) {
        this.datenbasisId = datenbasisId;
    }

    public String getMessprogrammC() {
        return this.messprogrammC;
    }

    public void setMessprogrammC(String messprogrammC) {
        this.messprogrammC = messprogrammC;
    }

    public String getMessprogrammS() {
        return this.messprogrammS;
    }

    public void setMessprogrammS(String messprogrammS) {
        this.messprogrammS = messprogrammS;
    }

}
