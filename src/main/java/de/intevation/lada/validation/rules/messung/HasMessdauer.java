/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.validation.rules.messung;

import javax.inject.Inject;

import de.intevation.lada.model.land.Messung;
import de.intevation.lada.model.land.Probe;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationRule;
import de.intevation.lada.validation.rules.Rule;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.annotation.RepositoryConfig;
/**
 * Validation rule for messungen.
 * Validates if the messung has a "nebenprobennummer"
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@ValidationRule("Messung")
public class HasMessdauer implements Rule {

    @Inject
    @RepositoryConfig(type = RepositoryType.RO)
    private Repository repository;

    @Override
    public Violation execute(Object object) {
        Messung messung = (Messung)object;
	Probe probe = repository.getByIdPlain(Probe.class, messung.getProbeId(), "land");

        if (messung.getMessdauer() == null ||
            messung.getMessdauer().equals("")) {
		//Exception for continous samples or Datenbasis = §161
		if (probe.getProbenartId()!=null && probe.getProbenartId() == 9 || probe.getDatenbasisId()!=null && probe.getDatenbasisId()==1){
                        Violation violation = new Violation();
                        violation.addNotification("messdauer", 631);
                        return violation;

		} else {
            		Violation violation = new Violation();
            		violation.addWarning("messdauer#"+messung.getNebenprobenNr(), 631);
            		return violation;
		}
        }
        return null;
    }

}
