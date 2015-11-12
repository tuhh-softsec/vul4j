/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.model.stamm;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * The persistent class for the status_kombi database table.
 * 
 */
@Entity
@Table(name="status_kombi")
public class StatusKombi implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    @Column(name="stufe_id")
    private Integer stufeId;

    @Column(name="wert_id")
    private Integer wertId;

    //bi-directional many-to-one association to StatusReihenfolge
    @OneToMany(mappedBy="von")
    private List<StatusReihenfolge> statusReihenfolges1;

    //bi-directional many-to-one association to StatusReihenfolge
    @OneToMany(mappedBy="zu")
    private List<StatusReihenfolge> statusReihenfolges2;

    public StatusKombi() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public List<StatusReihenfolge> getStatusReihenfolges1() {
        return this.statusReihenfolges1;
    }

    public void setStatusReihenfolges1(List<StatusReihenfolge> statusReihenfolges1) {
        this.statusReihenfolges1 = statusReihenfolges1;
    }

    public StatusReihenfolge addStatusReihenfolges1(StatusReihenfolge statusReihenfolges1) {
        getStatusReihenfolges1().add(statusReihenfolges1);
        statusReihenfolges1.setVon(this);

        return statusReihenfolges1;
    }

    public StatusReihenfolge removeStatusReihenfolges1(StatusReihenfolge statusReihenfolges1) {
        getStatusReihenfolges1().remove(statusReihenfolges1);
        statusReihenfolges1.setVon(null);

        return statusReihenfolges1;
    }

    public List<StatusReihenfolge> getStatusReihenfolges2() {
        return this.statusReihenfolges2;
    }

    public void setStatusReihenfolges2(List<StatusReihenfolge> statusReihenfolges2) {
        this.statusReihenfolges2 = statusReihenfolges2;
    }

    public StatusReihenfolge addStatusReihenfolges2(StatusReihenfolge statusReihenfolges2) {
        getStatusReihenfolges2().add(statusReihenfolges2);
        statusReihenfolges2.setZu(this);

        return statusReihenfolges2;
    }

    public StatusReihenfolge removeStatusReihenfolges2(StatusReihenfolge statusReihenfolges2) {
        getStatusReihenfolges2().remove(statusReihenfolges2);
        statusReihenfolges2.setZu(null);

        return statusReihenfolges2;
    }

}
