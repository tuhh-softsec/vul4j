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

@Entity
@Table(name = "status_protokoll")
public class StatusProtokoll extends de.intevation.lada.model.StatusProtokoll {
    private static final long serialVersionUID = 1L;
}
