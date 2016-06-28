/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.model.stamm;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;


/**
 * The persistent class for the probenehmer database table.
 * 
 */
@Entity
public class Probenehmer implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id", unique=true, nullable=false)
    private Integer id;

    private String bearbeiter;

    private String bemerkung;

    private String betrieb;

    private String bezeichnung;

    @Column(name="kurz_bezeichnung")
    private String kurzBezeichnung;

    @Column(name="letzte_aenderung")
    private Timestamp letzteAenderung;

    @Column(name="netzbetreiber_id")
    private String netzbetreiberId;

    private String ort;

    private String plz;

    @Column(name="prn_id")
    private String prnId;

    private String strasse;

    private String telefon;

    private String tp;

    private String typ;

    @Transient
    private boolean readonly;

    public Probenehmer() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBearbeiter() {
        return this.bearbeiter;
    }

    public void setBearbeiter(String bearbeiter) {
        this.bearbeiter = bearbeiter;
    }

    public String getBemerkung() {
        return this.bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    public String getBetrieb() {
        return this.betrieb;
    }

    public void setBetrieb(String betrieb) {
        this.betrieb = betrieb;
    }

    public String getBezeichnung() {
        return this.bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public String getKurzBezeichnung() {
        return this.kurzBezeichnung;
    }

    public void setKurzBezeichnung(String kurzBezeichnung) {
        this.kurzBezeichnung = kurzBezeichnung;
    }

    public Timestamp getLetzteAenderung() {
        return this.letzteAenderung;
    }

    public String getNetzbetreiberId() {
        return this.netzbetreiberId;
    }

    public void setNetzbetreiberId(String netzbetreiberId) {
        this.netzbetreiberId = netzbetreiberId;
    }

    public String getOrt() {
        return this.ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    public String getPlz() {
        return this.plz;
    }

    public void setPlz(String plz) {
        this.plz = plz;
    }

    public String getPrnId() {
        return this.prnId;
    }

    public void setPrnId(String prnId) {
        this.prnId = prnId;
    }

    public String getStrasse() {
        return this.strasse;
    }

    public void setStrasse(String strasse) {
        this.strasse = strasse;
    }

    public String getTelefon() {
        return this.telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getTp() {
        return this.tp;
    }

    public void setTp(String tp) {
        this.tp = tp;
    }

    public String getTyp() {
        return this.typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

}
