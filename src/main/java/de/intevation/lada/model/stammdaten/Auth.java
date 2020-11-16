/* Copyright (C) 2015 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.model.stammdaten;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the auth database table.
 *
 */
@Entity
@Table(name="auth")
public class Auth implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    @Column(name="funktion_id")
    private Integer funktionId;

    @Column(name="labor_mst_id")
    private String laborMstId;

    @Column(name="ldap_group")
    private String ldapGroup;

    @Column(name="mst_id")
    private String mstId;

    @Column(name="netzbetreiber_id")
    private String netzbetreiberId;

    public Auth() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFunktionId() {
        return this.funktionId;
    }

    public void setFunktionId(Integer funktionId) {
        this.funktionId = funktionId;
    }

    public String getLaborMstId() {
        return this.laborMstId;
    }

    public void setLaborMstId(String laborMstId) {
        this.laborMstId = laborMstId;
    }

    public String getLdapGroup() {
        return this.ldapGroup;
    }

    public void setLdapGroup(String ldapGroup) {
        this.ldapGroup = ldapGroup;
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
