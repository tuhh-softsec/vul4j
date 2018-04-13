package de.intevation.lada.model.stammdaten;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the netz_betreiber database table.
 * 
 */
@Entity
@Table(name="netz_betreiber")
@NamedQuery(name="NetzBetreiber.findAll", query="SELECT n FROM NetzBetreiber n")
public class NetzBetreiber implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Column(insertable=false, updatable=false)
    private Boolean aktiv;

    @Column(name="idf_netzbetreiber")
    private String idfNetzbetreiber;

    @Column(name="is_bmn")
    private Boolean isBmn;

    private String mailverteiler;

    private String netzbetreiber;

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

}
