package de.intevation.lada.model.land;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.mapping.Array;


/**
 * The persistent class for the messprogramm_mmt database table.
 */
@Entity
@Table(name="messprogramm_mmt")
public class MessprogrammMmt implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    @Column(name="letzte_aenderung")
    private Timestamp letzteAenderung;

    private Array messgroessen;

    @Column(name="mmt_id")
    private String mmtId;

    @Column(name="messprogramm_id")
    private Integer messprogrammId;

    public MessprogrammMmt() {
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

    public Array getMessgroessen() {
        return this.messgroessen;
    }

    public void setMessgroessen(Array messgroessen) {
        this.messgroessen = messgroessen;
    }

    public String getMmtId() {
        return this.mmtId;
    }

    public void setMmtId(String mmtId) {
        this.mmtId = mmtId;
    }

    public Integer getMessprogrammId() {
        return this.messprogrammId;
    }

    public void setMessprogramm(Integer messprogrammId) {
        this.messprogrammId = messprogrammId;
    }
}
