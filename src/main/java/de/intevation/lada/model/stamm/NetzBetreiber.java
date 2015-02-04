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
 * The persistent class for the netz_betreiber database table.
 */
@Entity
@Table(name="netz_betreiber")
public class NetzBetreiber implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private Boolean aktiv;

    @Column(name="idf_netzbetreiber")
    private String idfNetzbetreiber;

    @Column(name="is_bmn")
    private Boolean isBmn;

    private String mailverteiler;

    private String netzbetreiber;

    @Column(name="zust_mst_id")
    private String zustMstId;

    public NetzBetreiber() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getAktiv() {
        return this.aktiv;
    }

    public void setAktiv(Boolean aktiv) {
        this.aktiv = aktiv;
    }

    public String getIdfNetzbetreiber() {
        return this.idfNetzbetreiber;
    }

    public void setIdfNetzbetreiber(String idfNetzbetreiber) {
        this.idfNetzbetreiber = idfNetzbetreiber;
    }

    public Boolean getIsBmn() {
        return this.isBmn;
    }

    public void setIsBmn(Boolean isBmn) {
        this.isBmn = isBmn;
    }

    public String getMailverteiler() {
        return this.mailverteiler;
    }

    public void setMailverteiler(String mailverteiler) {
        this.mailverteiler = mailverteiler;
    }

    public String getNetzbetreiber() {
        return this.netzbetreiber;
    }

    public void setNetzbetreiber(String netzbetreiber) {
        this.netzbetreiber = netzbetreiber;
    }

    public String getZustMstId() {
        return this.zustMstId;
    }

    public void setZustMstId(String zustMstId) {
        this.zustMstId = zustMstId;
    }
}
