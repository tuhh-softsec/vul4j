/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.model.land;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.intevation.lada.model.Messung;


/**
 * The persistent class for the messung database table.
 */
@Entity
@Table(name="messung")
public class LMessung extends Messung {
    private static final long serialVersionUID = 1L;

    @Column(name="nebenproben_nr")
    private String nebenprobenNr;

    @OneToOne(mappedBy="messungs")
    private MessungTranslation messungTranslation;

    private Boolean geplant;

    public LMessung() {
    }

    public Boolean getGeplant() {
        return this.geplant;
    }

    public void setGeplant(Boolean geplant) {
        this.geplant = geplant;
    }

    public String getNebenprobenNr() {
        return nebenprobenNr;
    }

    public void setNebenprobenNr(String nebenprobenNr) {
        this.nebenprobenNr = nebenprobenNr;
    }

    @JsonIgnore
    public MessungTranslation getMessungTranslation() {
        return this.messungTranslation;
    }

    public void setMessungsTranslation(MessungTranslation messungTranslation) {
        this.messungTranslation = messungTranslation;
    }

    public Integer getMessungsIdAlt() {
        return this.messungTranslation.getMessungsIdAlt();
    }

    public void setMessungsIdAlt(Integer messungsIdAlt) {}
}
