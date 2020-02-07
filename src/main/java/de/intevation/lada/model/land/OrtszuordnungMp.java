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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.ws.rs.core.MultivaluedMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * The persistent class for the ortszuordnung_mp database table.
 * 
 */
@Entity
@Table(name="ortszuordnung_mp", schema="land")
public class OrtszuordnungMp implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @Column(name="letzte_aenderung", insertable=false)
    private Timestamp letzteAenderung;

    @Column(name="messprogramm_id")
    private Integer messprogrammId;

    @Column(name="ort_id")
    private Integer ortId;

    @Column(name="ortszuordnung_typ")
    private String ortszuordnungTyp;

    private String ortszusatztext;

    @Column(name="tree_modified", insertable=false, updatable=false)
    private Timestamp treeModified;

    @Transient
    @JsonIgnore
    private MultivaluedMap<String, Integer> errors;

    @Transient
    @JsonIgnore
    private MultivaluedMap<String, Integer> warnings;

    @Transient
    private boolean owner;

    @Transient
    private boolean readonly;

    public OrtszuordnungMp() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Timestamp getLetzteAenderung() {
        return this.letzteAenderung;
    }

    public void setLetzteAenderung(Timestamp letzteAenderung) {
        this.letzteAenderung = letzteAenderung;
    }

    public Integer getMessprogrammId() {
        return this.messprogrammId;
    }

    public void setMessprogrammId(Integer messprogrammId) {
        this.messprogrammId = messprogrammId;
    }

    public Integer getOrtId() {
        return this.ortId;
    }

    public void setOrtId(Integer ortId) {
        this.ortId = ortId;
    }

    public String getOrtszuordnungTyp() {
        return this.ortszuordnungTyp;
    }

    public void setOrtszuordnungTyp(String ortszuordnungTyp) {
        this.ortszuordnungTyp = ortszuordnungTyp;
    }

    public String getOrtszusatztext() {
        return this.ortszusatztext;
    }

    public void setOrtszusatztext(String ortszusatztext) {
        this.ortszusatztext = ortszusatztext;
    }

    public Timestamp getTreeModified() {
        return this.treeModified;
    }

    public void setTreeModified(Timestamp treeModified) {
        this.treeModified = treeModified;
    }

    @JsonProperty
    public MultivaluedMap<String, Integer> getErrors() {
        return this.errors;
    }

    @JsonIgnore
    public void setErrors(MultivaluedMap<String, Integer> errors) {
        this.errors = errors;
    }

    @JsonProperty
    public MultivaluedMap<String, Integer> getWarnings() {
        return this.warnings;
    }

    @JsonIgnore
    public void setWarnings(MultivaluedMap<String, Integer> warnings) {
        this.warnings = warnings;
    }

    /**
     * @return the owner
     */
    public boolean isOwner() {
        return owner;
    }

    /**
     * @param owner the owner to set
     */
    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    /**
     * @return the readonly
     */
    public boolean isReadonly() {
        return readonly;
    }

    /**
     * @param readonly the readonly to set
     */
    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }
}
