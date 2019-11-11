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

import org.hibernate.annotations.DynamicInsert;


/**
 * The persistent class for the messung database table.
 * 
 */
// The DynamicInsert Annotation has the effect, that the persisted object still
// has all the "null"-values. There is no reloading after the persistence
// process!
@Entity
@DynamicInsert(true)
public class Messung implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    private Boolean fertig;

    private Boolean geplant;

    @Column(name="ext_id")
    private Integer externeMessungsId;

    @Column(name="letzte_aenderung", insertable=false)
    private Timestamp letzteAenderung;

    private Integer messdauer;

    private Timestamp messzeitpunkt;

    @Column(name="mmt_id")
    private String mmtId;

    @Column(name="nebenproben_nr")
    private String nebenprobenNr;

    @Column(name="probe_id")
    private Integer probeId;

    @OneToOne
    @JoinColumn(name="probe_id", insertable=false, updatable=false)
    private Probe probe;

    private Integer status;

    @OneToOne
    @JoinColumn(name="status", insertable=false, updatable=false)
    private StatusProtokoll statusProtokoll;

    @Column(name="tree_modified", insertable=false, updatable=false)
    private Timestamp treeModified;

    @Transient
    private Boolean statusEdit;

    @Transient
    private Boolean statusEditMst;

    @Transient
    private Boolean statusEditLand;

    @Transient
    private Boolean statusEditLst;

    @Transient
    private Timestamp parentModified;

    @Transient
    private MultivaluedMap<String, Integer> errors;

    @Transient
    private MultivaluedMap<String, Integer> warnings;

    @Transient
    private boolean owner;

    @Transient
    private boolean readonly;

    public Messung() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getFertig() {
        return this.fertig;
    }

    public void setFertig(Boolean fertig) {
        this.fertig = fertig;
    }

    public Boolean getGeplant() {
        return this.geplant;
    }

    public void setGeplant(Boolean geplant) {
        this.geplant = geplant;
    }

    public Integer getExterneMessungsId() {
        return this.externeMessungsId;
    }

    public void setExterneMessungsId(Integer externeMessungsId) {
        this.externeMessungsId = externeMessungsId;
    }

    public Timestamp getLetzteAenderung() {
        return this.letzteAenderung;
    }

    public void setLetzteAenderung(Timestamp letzteAenderung) {
        this.letzteAenderung = letzteAenderung;
    }

    public Integer getMessdauer() {
        return this.messdauer;
    }

    public void setMessdauer(Integer messdauer) {
        this.messdauer = messdauer;
    }

    public Timestamp getMesszeitpunkt() {
        return this.messzeitpunkt;
    }

    public void setMesszeitpunkt(Timestamp messzeitpunkt) {
        this.messzeitpunkt = messzeitpunkt;
    }

    public String getMmtId() {
        return this.mmtId;
    }

    public void setMmtId(String mmtId) {
        this.mmtId = mmtId;
    }

    public String getNebenprobenNr() {
        return this.nebenprobenNr;
    }

    public void setNebenprobenNr(String nebenprobenNr) {
        this.nebenprobenNr = (nebenprobenNr == "") ? null : nebenprobenNr;
    }

    public Integer getProbeId() {
        return this.probeId;
    }

    public void setProbeId(Integer probeId) {
        this.probeId = probeId;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Timestamp getTreeModified() {
        return this.treeModified;
    }

    public void setTreeModified(Timestamp treeModified) {
        this.treeModified = treeModified;
    }

    /**
     * @return the statusEdit
     */
    public Boolean getStatusEdit() {
        return statusEdit;
    }
    public Boolean getStatusEditMst() {
        return statusEditMst;
    }
    public Boolean getStatusEditLand() {
        return statusEditLand;
    }
    public Boolean getStatusEditLst() {
        return statusEditLst;
    }

    /**
     * @param statusEdit the statusEdit to set
     */
    public void setStatusEdit(Boolean statusEdit) {
        this.statusEdit = statusEdit;
    }
    public void setStatusEditMst(Boolean statusEditMst) {
        this.statusEditMst = statusEditMst;
    }
    public void setStatusEditLand(Boolean statusEditLand) {
        this.statusEditLand = statusEditLand;
    }
    public void setStatusEditLst(Boolean statusEditLst) {
        this.statusEditLst = statusEditLst;
    }

    /**
     * @return the parentModified
     */
    public Timestamp getParentModified() {
        if (this.parentModified == null && this.probe != null) {
            return this.probe.getTreeModified();
        }
        return parentModified;
    }

    /**
     * @param parentModified the parentModified to set
     */
    public void setParentModified(Timestamp parentModified) {
        this.parentModified = parentModified;
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

    public MultivaluedMap<String, Integer> getErrors() {
        return this.errors;
    }

    public void setErrors(MultivaluedMap<String, Integer> errors) {
        this.errors = errors;
    }

    public MultivaluedMap<String, Integer> getWarnings() {
        return this.warnings;
    }

    public void setWarnings(MultivaluedMap<String, Integer> warnings) {
        this.warnings = warnings;
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

    public StatusProtokoll getStatusProtokoll() {
        return this.statusProtokoll;
    }

    public void setStatusProtokoll (StatusProtokoll statusProtokoll) {
        this.statusProtokoll = statusProtokoll;
    }

}
