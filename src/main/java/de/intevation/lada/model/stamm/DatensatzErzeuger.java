package de.intevation.lada.model.stamm;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the datensatz_erzeuger database table.
 * 
 */
@Entity
@Table(name="datensatz_erzeuger")
public class DatensatzErzeuger implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id", unique=true, nullable=false)
    private Integer id;

    private String bezeichnung;

    @Column(name="da_erzeuger_id")
    private String daErzeugerId;

    @Column(name="letzte_aenderung")
    private Timestamp letzteAenderung;

    @Column(name="mst_id")
    private String mstId;

    @Column(name="netzbetreiber_id")
    private String netzbetreiberId;

    public DatensatzErzeuger() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBezeichnung() {
        return this.bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public String getDaErzeugerId() {
        return this.daErzeugerId;
    }

    public void setDaErzeugerId(String daErzeugerId) {
        this.daErzeugerId = daErzeugerId;
    }

    public Timestamp getLetzteAenderung() {
        return this.letzteAenderung;
    }

    public void setLetzteAenderung(Timestamp letzteAenderung) {
        this.letzteAenderung = letzteAenderung;
    }

    public String getMstId() {
        return this.mstId;
    }

    public void setMstId(String mstId) {
        this.mstId = mstId;
    }

    public String getNetzbetreiberId() {
        return this.netzbetreiberId;
    }

    public void setNetzbetreiberId(String netzbetreiberId) {
        this.netzbetreiberId = netzbetreiberId;
    }

}
