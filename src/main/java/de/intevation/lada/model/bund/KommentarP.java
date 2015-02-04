/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.model.bund;

import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the kommentar_p database table.
 */
@Entity
@Table(name="kommentar_p")
public class KommentarP extends de.intevation.lada.model.KommentarP {
    private static final long serialVersionUID = 1L;

}
