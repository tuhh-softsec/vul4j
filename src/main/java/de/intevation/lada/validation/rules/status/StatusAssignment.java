/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.validation.rules.status;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.util.List;

import de.intevation.lada.model.land.Messung;
import de.intevation.lada.model.land.Probe;
import de.intevation.lada.model.land.Messwert;
import de.intevation.lada.model.land.StatusProtokoll;
import de.intevation.lada.model.land.Ortszuordnung;
import de.intevation.lada.model.stammdaten.Umwelt;

import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.rest.Response;
import de.intevation.lada.util.data.Strings;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationRule;
import de.intevation.lada.validation.rules.Rule;

/**
 * Validation rule for status.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@ValidationRule("Status")
public class StatusAssignment implements Rule {

    @Inject
    private Logger logger;

    @Inject
    @RepositoryConfig(type = RepositoryType.RO)
    private Repository repository;

    int count = 0;

    @Override
    public Violation execute(Object object) {
        StatusProtokoll status = (StatusProtokoll) object;

        /*init Messung*/
        Messung messung = repository.getByIdPlain(Messung.class, status.getMessungsId(), "land");

        /*init Probe*/
        Probe probe = repository.getByIdPlain(Probe.class, messung.getProbeId(), "land");

	/*init Umweltbereich
	Umwelt umwelt = repository.getByIdPlain(Umwelt.class, probe.getUmwId(), "stamm");
	*/

        /*init Messwerte*/
        QueryBuilder<Messwert> builder =
                new QueryBuilder<Messwert>(
                        repository.entityManager(Strings.LAND), Messwert.class);
        builder.and("messungsId", status.getMessungsId());
        Response response = repository.filter(builder.getQuery(), Strings.LAND);
        @SuppressWarnings("unchecked")
        List<Messwert> messwerte = (List<Messwert>) response.getData();

        Violation violation = new Violation();

        Timestamp ts = new Timestamp(System.currentTimeMillis());

        /*init Orte*/
        QueryBuilder<Ortszuordnung> builder_ort =
                new QueryBuilder<Ortszuordnung>(
                        repository.entityManager(Strings.LAND), Ortszuordnung.class);
        builder_ort.and("probeId", probe.getId());
        Response response_ort = repository.filter(builder_ort.getQuery(), Strings.LAND);
        @SuppressWarnings("unchecked")
        List<Ortszuordnung> orte = (List<Ortszuordnung>) response_ort.getData();

        /*Messzeitpunk 1) vorhanden  2) nicht in der Zukunft bezgogen auf sysTime 3) nicht vor Probenentnahme  -- datenbasis aus*/
        if (messung.getMesszeitpunkt() == null && probe.getProbenartId() != 9) {
            violation.addError("messzeitpunkt", 631);
        } else if (messung.getMesszeitpunkt() != null && ts.before(messung.getMesszeitpunkt())) {
            violation.addError("messzeitpunkt", 641);
        } else if (messung.getMesszeitpunkt() != null && probe.getProbeentnahmeBeginn() != null &&
                messung.getMesszeitpunkt().before(probe.getProbeentnahmeBeginn())) {
            violation.addError("messzeitpunkt", 642);
        }

        /* 4) Messwerte vorhanden*/
        if (messwerte.isEmpty()) {
            violation.addError("messwert", 631);
        }

        /* 5) Messmethode angegeben*/
        if (messung.getMmtId() == null) {
            violation.addError("messmethode", 631);
        }

        /* 6) Messdauer angegeben - auszer kontinuierlich*/
        if (messung.getMessdauer() == null && probe.getProbenartId() != 9) {
            violation.addError("messdauer", 631);
        }

        /* 7) ProbenentnahmeBeginn vor ProbenentnahmeEnde*/
        if ((probe.getProbeentnahmeBeginn() != null && probe.getProbeentnahmeEnde() != null) &&
                probe.getProbeentnahmeBeginn().after(probe.getProbeentnahmeEnde())) {
            violation.addError("probeentnahmeEnde", 643);
        }

        /* 8) ProbenentnahmeBeginn  gesetzt kontinuierlichen Proben*/
        if (probe.getProbeentnahmeBeginn() == null && probe.getProbenartId() == 9) {
            violation.addError("probeentnahmeBeginn", 631);
        }

        /* 9) ProbenentnahmeEnde gesetzt kontinuierlichen Proben*/
        if (probe.getProbeentnahmeEnde() == null && probe.getProbenartId() == 9) {
            violation.addError("probeentnahmeEnde", 631);
        }

        /* 10) ProbeentnahmeEnde bei kontinuierlichen und Sammel-Proben ob Entnahme Beginn ungleich Entnahme Ende ist*/
        if (probe.getDatenbasisId() != null &&
                probe.getProbeentnahmeEnde() != null && probe.getProbeentnahmeBeginn() != null &&
                (probe.getProbenartId() == 9 || probe.getProbenartId() == 3) &&
                probe.getProbeentnahmeBeginn() == probe.getProbeentnahmeEnde()) {
            violation.addError("probeentnahmeEnde", 643);
        }

        /* 9) Umweltbereichs-ID vorh. §161, §162, 162SPARSE, REI*/
        if (probe.getUmwId() == null &&
                (probe.getDatenbasisId() == null || probe.getDatenbasisId() == 1 ||
                        probe.getDatenbasisId() == 2 || probe.getDatenbasisId() == 4 || probe.getDatenbasisId() == 10)) {
            violation.addError("umwId", 631);
        }

        /* 10) Datenbasis gesetzt*/
        if (probe.getDatenbasisId() == null) {
            violation.addError("datenbasisId", 631);
        }

        /* 12) Hauptprobennummer bei nicht kontinuierlichen §162 Proben + 162SPARSE*/
        if (probe.getHauptprobenNr() == null && (probe.getProbenartId() == 1 || probe.getDatenbasisId() == 2 || probe.getDatenbasisId() == 10)) {
            violation.addError("hauptprobenNr", 631);
        }

        /* Messeinheit gem. Umweltbereich*/
        if (!messwerte.isEmpty() && probe.getUmwId() != null) {
            Umwelt umwelt = repository.getByIdPlain(Umwelt.class, probe.getUmwId(), "stamm");
            messwerte.forEach(item -> {
                if (item.getMehId() != umwelt.getMehId() || umwelt.getMehId() == null) {
                    violation.addError("mehId", 644);
                }
            });
        }

        /* 15) ProbeentnahmeBeginn gesetzt*/
        if (probe.getProbeentnahmeBeginn() == null) {
            violation.addError("probeentnahmeBeginn", 631);
        }

        /* 16) ProbenentnahmeBeginn nicht in Zukunft */
        if (probe.getProbeentnahmeBeginn() != null && ts.before(probe.getProbeentnahmeBeginn())) {
            violation.addError("messzeitpunkt", 642);
        }

        /* 13) Entnahme-Ort gesetzt*/
        if (orte.isEmpty()) {
            violation.addError("ort", 631);
        } else {
            count = 0;
            orte.forEach(item -> {
                if (item.getOrtszuordnungTyp().equals("E") || item.getOrtszuordnungTyp().equals("R")) {
                    count++;
                }
            });
            if (count == 0) {
                violation.addError("entnahmeOrt", 631);
            } else if (count > 1) {
                violation.addError("entnahmeOrt", 672);
            }
        }

        if (violation.hasErrors()) {
            return violation;
        }

        return null;
    }
}
