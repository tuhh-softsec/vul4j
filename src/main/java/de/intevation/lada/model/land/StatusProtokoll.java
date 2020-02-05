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
 * The persistent class for the status_protokoll database table.
 * 
 */
@Entity
@Table(name="status_protokoll", schema="land")
public class StatusProtokoll implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @Column(name="datum", insertable=false, updatable=false)
    private Timestamp datum;

    @Column(name="messungs_id")
    private Integer messungsId;

    @Column(name="mst_id")
    private String mstId;

    @Column(name="status_kombi")
    private Integer statusKombi;

    private String text;

    @Column(name="tree_modified", insertable=false, updatable=false)
    private Timestamp treeModified;

    @Transient
    private boolean owner;

    @Transient
    private boolean readonly;

    @Transient
    private Timestamp parentModified;

    @Transient
    private Integer statusStufe;

    @Transient
    private Integer statusWert;

    @Transient
    @JsonIgnore
    private MultivaluedMap<String, Integer> errors;

    @Transient
    @JsonIgnore
    private MultivaluedMap<String, Integer> warnings;

    public StatusProtokoll() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Timestamp getDatum() {
        return this.datum;
    }

    public void setDatum(Timestamp datum) {
        this.datum = datum;
    }

    public Integer getMessungsId() {
        return this.messungsId;
    }

    public void setMessungsId(Integer messungsId) {
        this.messungsId = messungsId;
    }

    public String getMstId() {
        return this.mstId;
    }

    public void setMstId(String mstId) {
        this.mstId = mstId;
    }

    public Integer getStatusKombi() {
        return this.statusKombi;
    }

    public void setStatusKombi(Integer statusKombi) {
        this.statusKombi = statusKombi;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
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

    /**
     * @return the parentModified
     */
    public Timestamp getParentModified() {
        return parentModified;
    }

    /**
     * @param parentModified the parentModified to set
     */
    public void setParentModified(Timestamp parentModified) {
        this.parentModified = parentModified;
    }

    /**
     * @return the statusStufe
     */
    public Integer getStatusStufe() {
        return statusStufe;
    }

    /**
     * @param statusStufe the statusStufe to set
     */
    public void setStatusStufe(Integer statusStufe) {
        this.statusStufe = statusStufe;
    }

    /**
     * @return the statusWert
     */
    public Integer getStatusWert() {
        return statusWert;
    }

    /**
     * @param statusWert the statusWert to set
     */
    public void setStatusWert(Integer statusWert) {
        this.statusWert = statusWert;
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
