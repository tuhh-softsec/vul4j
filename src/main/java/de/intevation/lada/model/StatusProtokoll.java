/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import javax.persistence.Transient;


/**
 * The persistent class for the status database table.
 */
@MappedSuperclass
@Table(name="status_protokoll")
public class StatusProtokoll implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id", nullable=false, insertable=false)
    private Integer id;

    @Column(name="messungs_id")
    private Integer messungsId;

    @Column(name="status_stufe")
    private Integer statusStufe;

    @Column(name="status_wert")
    private Integer statusWert;

    private String erzeuger;

    @Column(name="datum")
    private Timestamp datum;

    @Column(name="text")
    private String text;

    @Column(name="tree_modified")
    private Timestamp treeModified;

    @Transient
    private boolean owner;

    @Transient
    private boolean readonly;

    public StatusProtokoll() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStatusStufe() {
        return this.statusStufe;
    }

    public void setStatusStufe(Integer statusStufe) {
        this.statusStufe = statusStufe;
    }

    public Integer getStatusWert() {
        return this.statusWert;
    }

    public void setStatusWert(Integer statusWert) {
        this.statusWert = statusWert;
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

    public Timestamp getDatum() {
        return this.datum;
    }

    public void setDatum(Timestamp datum) {
        this.datum = datum;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Timestamp getTreeModified() {
        return treeModified;
    }

    public void setTreeModified(Timestamp treeModified) {
        this.treeModified = treeModified;
    }

    public boolean isOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }
}
