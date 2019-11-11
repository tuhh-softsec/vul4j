package de.intevation.lada.model.land;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.ws.rs.core.MultivaluedMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * The persistent class for the messwert database table.
 * 
 */
@Entity
public class Messwert implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    private Boolean grenzwertueberschreitung;

    @Column(name="letzte_aenderung", insertable=false)
    private Timestamp letzteAenderung;

    @Column(name="meh_id")
    private Integer mehId;

    private Float messfehler;

    @Column(name="messgroesse_id")
    private Integer messgroesseId;

    @Column(name="messungs_id")
    private Integer messungsId;

    private Double messwert;

    @Column(name="messwert_nwg")
    private String messwertNwg;

    @Column(name="nwg_zu_messwert")
    private Double nwgZuMesswert;

    @Column(name="tree_modified", insertable=false, updatable=false)
    private Timestamp treeModified;

    @OneToOne
    @JoinColumn(name="messungs_id", insertable=false, updatable=false)
    private Messung messung;

    @Transient
    private boolean owner;

    @Transient
    private boolean readonly;

    @Transient
    private Timestamp parentModified;

    @Transient
    @JsonIgnore
    private MultivaluedMap<String, Integer> errors;

    @Transient
    @JsonIgnore
    private MultivaluedMap<String, Integer> warnings;

    public Messwert() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getGrenzwertueberschreitung() {
        return this.grenzwertueberschreitung;
    }

    public void setGrenzwertueberschreitung(Boolean grenzwertueberschreitung) {
        this.grenzwertueberschreitung = grenzwertueberschreitung;
    }

    public Timestamp getLetzteAenderung() {
        return this.letzteAenderung;
    }

    public void setLetzteAenderung(Timestamp letzteAenderung) {
        this.letzteAenderung = letzteAenderung;
    }

    public Integer getMehId() {
        return this.mehId;
    }

    public void setMehId(Integer mehId) {
        this.mehId = mehId;
    }

    public Float getMessfehler() {
        return this.messfehler;
    }

    public void setMessfehler(Float messfehler) {
        this.messfehler = messfehler;
    }

    public Integer getMessgroesseId() {
        return this.messgroesseId;
    }

    public void setMessgroesseId(Integer messgroesseId) {
        this.messgroesseId = messgroesseId;
    }

    public Integer getMessungsId() {
        return this.messungsId;
    }

    public void setMessungsId(Integer messungsId) {
        this.messungsId = messungsId;
    }

    public Double getMesswert() {
        return this.messwert;
    }

    public void setMesswert(Double messwert) {
        this.messwert = messwert;
    }

    public String getMesswertNwg() {
        return this.messwertNwg;
    }

    public void setMesswertNwg(String messwertNwg) {
        this.messwertNwg = messwertNwg;
    }

    public Double getNwgZuMesswert() {
        return this.nwgZuMesswert;
    }

    public void setNwgZuMesswert(Double nwgZuMesswert) {
        this.nwgZuMesswert = nwgZuMesswert;
    }

    public Timestamp getTreeModified() {
        return this.treeModified;
    }

    public void setTreeModified(Timestamp treeModified) {
        this.treeModified = treeModified;
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

    public Timestamp getParentModified() {
        if (this.parentModified == null && this.messung != null) {
            return this.messung.getTreeModified();
        }
        return this.parentModified;
    }

    public void setParentModified(Timestamp parentModified) {
        this.parentModified = parentModified;
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
}
