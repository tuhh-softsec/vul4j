package de.intevation.lada.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;


/**
 * The persistent class for the ortszuordnung database table.
 */
@MappedSuperclass
@Table(name="ortszuordnung")
public class Ortszuordnung implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    @Column(name="letzte_aenderung")
    private Timestamp letzteAenderung;

    @Column(name="ort_id")
    private Long ortId;

    @Column(name="ortszuordnung_typ")
    private String ortszuordnungTyp;

    private String ortszusatztext;

    @Column(name="probe_id")
    private Integer probeId;

    public Ortszuordnung() {
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

    public Long getOrtId() {
        return this.ortId;
    }

    public void setOrtId(Long ortId) {
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

    public Integer getProbeId() {
        return this.probeId;
    }

    public void setProbeId(Integer probeId) {
        this.probeId = probeId;
    }

}
