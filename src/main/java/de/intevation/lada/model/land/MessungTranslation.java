/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.model.land;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * The persistent class for the messung_translation database table.
 */
@Entity
@Table(name="messung_translation")
public class MessungTranslation implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id", unique=true)
    private Integer id;

    @OneToOne
    private LMessung messungs;

    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="messungs_id_alt")
    private Integer messungsIdAlt;

    public MessungTranslation() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @JsonIgnore
    public LMessung getMessungsId() {
        return this.messungs;
    }

    public void setMessungsId(LMessung messung) {
        this.messungs = messung;
    }

    public Integer getMessungsIdAlt() {
        return this.messungsIdAlt;
    }

    public void setMessungsIdAlt(Integer messungsIdAlt) {
        this.messungsIdAlt = messungsIdAlt;
    }
}
