package de.intevation.lada.model.stammdaten;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the mmt_messgroesse database table.
 *
 */
@Entity
@Table(name="mmt_messgroesse")
public class MmtMessgroesse implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private MmtMessgroessePK id;

    @Column(name="messgroesse_id", insertable=false, updatable=false)
    private Integer messgroesseId;

    @Column(name="mmt_id", insertable=false, updatable=false)
    private String mmtId;

    public MmtMessgroesse() {
    }

    public MmtMessgroessePK getMmtMessgroessePK() {
        return this.id;
    }

    public void setMmtMessgroessePK(MmtMessgroessePK id) {
        this.id = id;
    }

    public Integer getMessgroesseId() {
        return this.messgroesseId;
    }

    public void setMessgroesseId(Integer messgroesseId) {
        this.messgroesseId = messgroesseId;
    }

    public String getMmtId() {
        return this.mmtId;
    }

    public void setMmtId(String mmtId) {
        this.mmtId = mmtId;
    }

}
