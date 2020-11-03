package de.intevation.lada.model.stammdaten;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the status_erreichbar database table.
 *
 */
@Entity
@Table(name="status_erreichbar")
public class StatusErreichbar implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    @Column(name="cur_stufe")
    private Integer curStufe;

    @Column(name="cur_wert")
    private Integer curWert;

    @Column(name="stufe_id")
    private Integer stufeId;

    @Column(name="wert_id")
    private Integer wertId;

    public StatusErreichbar() {
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCurStufe() {
        return this.curStufe;
    }

    public void setCurStufe(Integer curStufe) {
        this.curStufe = curStufe;
    }

    public Integer getCurWert() {
        return this.curWert;
    }

    public void setCurWert(Integer curWert) {
        this.curWert = curWert;
    }

    public Integer getStufeId() {
        return this.stufeId;
    }

    public void setStufeId(Integer stufeId) {
        this.stufeId = stufeId;
    }

    public Integer getWertId() {
        return this.wertId;
    }

    public void setWertId(Integer wertId) {
        this.wertId = wertId;
    }

}
