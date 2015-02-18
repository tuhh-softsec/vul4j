/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the status database table.
 */
@MappedSuperclass
@Table(name="status")
public class Status implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id", nullable=false, insertable=false)
    private Integer id;

    private String erzeuger;

    @Column(name="messungs_id")
    private Integer messungsId;

    @Column(name="s_datum")
    private Timestamp sDatum;

    @Column(name="s_kommentar")
    private String sKommentar;

    private Integer status;

    public Status() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getErzeuger() {
        return this.erzeuger;
    }

    public void setErzeuger(String erzeuger) {
        this.erzeuger = erzeuger;
    }

    public Integer getMessungsId() {
        return this.messungsId;
    }

    public void setMessungsId(Integer messungsId) {
        this.messungsId = messungsId;
    }

    public Timestamp getSDatum() {
        return this.sDatum;
    }

    public void setSDatum(Timestamp sDatum) {
        this.sDatum = sDatum;
    }

    public String getSKommentar() {
        return this.sKommentar;
    }

    public void setSKommentar(String sKommentar) {
        this.sKommentar = sKommentar;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
