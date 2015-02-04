/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.model.stamm;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the auth database table.
 */
@Entity
@Table(name="auth")
public class Auth implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    @Column(name="ldap_group")
    private String ldapGroup;

    @Column(name="mst_id")
    private String messStelle;

    private String netzBetreiber;

    public Auth() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLdapGroup() {
        return this.ldapGroup;
    }

    public void setLdapGroup(String ldapGroup) {
        this.ldapGroup = ldapGroup;
    }

    public String getMessStelle() {
        return this.messStelle;
    }

    public void setMessStelle(String messStelle) {
        this.messStelle = messStelle;
    }

    public String getNetzBetreiber() {
        return this.netzBetreiber;
    }

    public void setNetzBetreiber(String netzBetreiber) {
        this.netzBetreiber = netzBetreiber;
    }

}
