/* Copyright (C) 2017 by Bundesamt fuer Strahlenschutz
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
 * The persistent class for the importer_config database table.
 *
 */
@Entity
@Table(name = "importer_config")
public class ImporterConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    private String action;

    private String attribute;

    @Column(name = "from_value")
    private String fromValue;

    @Column(name = "mst_id")
    private String mstId;

    private String name;

    @Column(name = "to_value")
    private String toValue;

    public ImporterConfig() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAttribute() {
        return this.attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getFromValue() {
        return this.fromValue;
    }

    public void setFromValue(String fromValue) {
        this.fromValue = fromValue;
    }

    public String getMstId() {
        return this.mstId;
    }

    public void setMstId(String mstId) {
        this.mstId = mstId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToValue() {
        return this.toValue;
    }

    public void setToValue(String toValue) {
        this.toValue = toValue;
    }

}
