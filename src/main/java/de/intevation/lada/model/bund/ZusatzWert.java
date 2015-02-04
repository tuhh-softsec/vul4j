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
 * The persistent class for the zusatz_wert database table.
 */
@Entity
@Table(name="zusatz_wert")
public class ZusatzWert extends de.intevation.lada.model.ZusatzWert {
    private static final long serialVersionUID = 1L;

}
