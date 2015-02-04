/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.model.land;

import javax.persistence.Entity;
import javax.persistence.Table;

import de.intevation.lada.model.KommentarM;


/**
 * The persistent class for the kommentar_m database table.
 */
@Entity
@Table(name="kommentar_m")
public class LKommentarM extends KommentarM {
    private static final long serialVersionUID = 1L;

}
