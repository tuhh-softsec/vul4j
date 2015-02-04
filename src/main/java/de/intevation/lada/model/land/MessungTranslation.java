/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.model.land;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the messung_translation database table.
 */
@Entity
@Table(name="messung_translation")
public class MessungTranslation implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    @Column(name="messungs_id")
    private Integer messungsId;

    @Column(name="messungs_id_alt")
    private Integer messungsIdAlt;

    @Column(name="nebenproben_nr")
    private String nebenprobenNr;

    @Column(name="probe_id_alt")
    private String probeIdAlt;

    public MessungTranslation() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMessungsId() {
        return this.messungsId;
    }

    public void setMessungsId(Integer messungsId) {
        this.messungsId = messungsId;
    }

    public Integer getMessungsIdAlt() {
        return this.messungsIdAlt;
    }

    public void setMessungsIdAlt(Integer messungsIdAlt) {
        this.messungsIdAlt = messungsIdAlt;
    }

    public String getNebenprobenNr() {
        return this.nebenprobenNr;
    }

    public void setNebenprobenNr(String nebenprobenNr) {
        this.nebenprobenNr = nebenprobenNr;
    }

    public String getProbeIdAlt() {
        return this.probeIdAlt;
    }

    public void setProbeIdAlt(String probeIdAlt) {
        this.probeIdAlt = probeIdAlt;
    }

}
