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

    @Inject Logger logger;

    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository repository;

	int count = 0;

    @Override
    public Violation execute(Object object) {
        StatusProtokoll status = (StatusProtokoll)object;

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
        List<Messwert> messwerte = (List<Messwert>)response.getData();

        Violation violation = new Violation();

	Timestamp ts = new Timestamp(System.currentTimeMillis());

	/*init Orte*/
	QueryBuilder<Ortszuordnung> builder_ort = 
		new QueryBuilder<Ortszuordnung>(
		repository.entityManager(Strings.LAND), Ortszuordnung.class);
	builder_ort.and("probeId", probe.getId());
	Response response_ort = repository.filter(builder_ort.getQuery(), Strings.LAND);
	@SuppressWarnings("unchecked")
	List<Ortszuordnung> orte = (List<Ortszuordnung>)response_ort.getData();


	/*Messzeitpunk 1) vorhanden 2) nicht in der Zukunft bezgogen auf sysTime 3) nicht vor Probenentnahme */
	if ( messung.getMesszeitpunkt() == null && (probe.getDatenbasisId() == 1 || probe.getDatenbasisId() == 2 || probe.getDatenbasisId() == 4 ) && probe.getProbenartId() == 9 ) {
		violation.addError("Messzeitpunk#fehlt", 631);
	}

        else if ( ts.before(messung.getMesszeitpunkt()) ) {
            violation.addError("Messzeitpunkt#Zukunft", 631);
        }

	else if ( messung.getMesszeitpunkt().before(probe.getProbeentnahmeBeginn()) ){
		violation.addError("Messzeitpunkt#vorProbenentnahme", 631);
	}

	/*Messwerte vorhanden*/
	if ( messwerte.isEmpty() ){
	 	violation.addError("Messwert#fehlt", 631);
	}

	/*Messmethode angegeben wenn §162 Probe bzw. REI-Probe*/
	if ( messung.getMmtId() == null && ( probe.getDatenbasisId()==1 || probe.getDatenbasisId()==4 ) ) {
		violation.addError("Messmethode#fehlt", 631);
	}

	/*Messdauer angegeben*/
	if ( messung.getMessdauer() == null && ( probe.getDatenbasisId() == 1 || probe.getDatenbasisId() == 2 || probe.getDatenbasisId() == 3) && probe.getProbenartId() == 1  ) {
		violation.addError("Messdauer#fehlt", 631);
	}

	/*ProbenentnahmeBeginn vor ProbenentnahmeEnde*/
	if ( probe.getProbeentnahmeEnde() != null && probe.getProbeentnahmeBeginn().after(probe.getProbeentnahmeEnde()) ) {
		violation.addError("Probeentnahmeende#vorProbeentnahmeBeginn",631);
	}

	/*ProbeentnahmeEnde bei kontinuierlichen Proben oder Sammelproben*/
	if ( probe.getDatenbasisId() != null && ( probe.getProbenartId() == 9 || probe.getProbenartId() == 3 ) && probe.getProbeentnahmeBeginn() == probe.getProbeentnahmeEnde() ){
		violation.addError("ProbenentnahmeBeginn == ProbenentnahmeEnde", 631);
	}

	/*ProbenentnahmeEnde bei kontinuierlichen Proben oder Sammelproben*/
	if ( (probe.getProbenartId() == 3 || probe.getProbenartId() == 9) && probe.getProbeentnahmeEnde() == null ) {
		violation.addError("ProbenentnahmeEnde#nicht gesetzt", 631);
	}

	/*ProbeentnahmeBeginn und ProbeentnahmeEnde bei Kontinuierlichen REI-Proben*/
	if ( probe.getDatenbasisId() == 4 && probe.getProbenartId()== 9 && (probe.getProbeentnahmeBeginn() == null || probe.getProbeentnahmeEnde() == null) ) {
		violation.addError("ProbeentnahmeBeginn oder -Ende nicht gesetzt", 631);
	}

	/*ProbenentnahmeDatum nicht in der Zukunft oder ProbeentnahmeBeginn fehlt*/
	if ( probe.getProbeentnahmeBeginn() == null || ts.before(probe.getProbeentnahmeBeginn()) ){
		violation.addError("ProbeentnahmeBeginn#fehlt oder Zukunft", 631);
	}


	/*Umweltbereichs-ID vorh. §161, §162
        /*init Umweltbereich*/

	if ( probe.getUmwId() == null && ( probe.getDatenbasisId() == 1 || probe.getDatenbasisId() == 2) ) {
		violation.addError("UmweltbereichsId#fehlt", 631);
	}

	/*Datenbasis gesetzt*/
	if ( probe.getDatenbasisId() == null ){
		violation.addError("Datenbasis#fehlt", 631);
	}

	/*Hauptprobennummer bei nicht kontinuierlichen §161 und §162 Proben*/
	if ( probe.getHauptprobenNr() == null && probe.getProbenartId() == 1  && (probe.getDatenbasisId() == 1 || probe.getDatenbasisId() == 2) ){
		violation.addError("Hauptproben-Nr#fehlt", 631);
	}

	/*Messeinheit gem. Umweltbereich*/
	if ( !messwerte.isEmpty() && probe.getUmwId() != null) {
	Umwelt umwelt = repository.getByIdPlain(Umwelt.class, probe.getUmwId(), "stamm");
	messwerte.forEach(item ->{
		if (item.getMehId() != umwelt.getMehId() || umwelt.getMehId() == null) {
			violation.addError("Messeinheit#nicht gem. Umweltbereichs-Id", 631);
		}
		});
	}



	/*Messstelle gesetzt*/
	if ( probe.getMstId() == null) {
		violation.addError("Messstelle#fehlt", 631);
	}

	/*Messlabor gesetzt*/
	if (probe.getLaborMstId() == null) {
		violation.addError("Messlabor#fehlt", 631);
	}

	/*Ort angegeben WIP*/
	if (orte.isEmpty()) {
	violation.addError("Ortszuordnung#fehlt", 631);
	}

	/*
	if (!orte.isEmpty()){

		orte.forEach(item ->{
			if (item.getOrtszuordnungTyp() == "U"){
			count++;
				}
		});

		if (count == 0){
			violation.addError("Messpunkt#mehrals1", 631);
		}else if (count == 1 ){
			count = 0;
			orte.forEach(item ->{
	                        if (item.getOrtszuordnungTyp() == "U"){
        	                count++;
                	                }
                	});
			if (count > 1) {
				violation.addError("ursprungsort#mehrals1", 631);
			}
		}

	}

	*/

	if (violation.hasErrors()) {
            return violation;
	}

	return null;
    }
}
