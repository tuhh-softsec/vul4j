/* Copyright (C) 2019 by Bundesamt fuer Strahlenschutz
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
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the mess_einheit_umrechung database table
 */
@Entity
@Table(name="mass_einheit_umrechnung")
public class MassEinheitUmrechnung implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="meh_id_von")
    private MessEinheit mehVon;

    @Column(name="meh_id_zu")
    private Integer mehIdZu;

    private Double faktor;

    public MassEinheitUmrechnung() {

    }

    public Integer getId() {
        return this.id;
    }

    public MessEinheit getMehVon() {
        return this.mehVon;
    }

    public Integer getMehIdZu() {
        return this.mehIdZu;
    }

    public Double getFaktor() {
        return this.faktor;
    }
}
