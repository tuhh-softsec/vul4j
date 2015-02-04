/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.model.land;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the messprogramm database table.
 */
@Entity
@Table(name="messprogramm")
public class Messprogramm implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    private Timestamp bis;

    @Column(name="media_desk")
    private String mediaDesk;

    private String name;

    @Column(name="ort_id")
    private Long ortId;

    @Column(name="umw_id")
    private String umwId;

    private Timestamp von;

    @Column(name="intervall_id")
    private Integer messprogrammIntervall;

    public Messprogramm() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Timestamp getBis() {
        return this.bis;
    }

    public void setBis(Timestamp bis) {
        this.bis = bis;
    }

    public String getMediaDesk() {
        return this.mediaDesk;
    }

    public void setMediaDesk(String mediaDesk) {
        this.mediaDesk = mediaDesk;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOrtId() {
        return this.ortId;
    }

    public void setOrtId(Long ortId) {
        this.ortId = ortId;
    }

    public String getUmwId() {
        return this.umwId;
    }

    public void setUmwId(String umwId) {
        this.umwId = umwId;
    }

    public Timestamp getVon() {
        return this.von;
    }

    public void setVon(Timestamp von) {
        this.von = von;
    }

    public Integer getMessprogrammIntervall() {
        return this.messprogrammIntervall;
    }

    public void setMessprogrammIntervall(Integer messprogrammIntervall) {
        this.messprogrammIntervall = messprogrammIntervall;
    }
}
