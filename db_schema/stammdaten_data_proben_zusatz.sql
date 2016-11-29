\set ON_ERROR_STOP on

--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.1
-- Dumped by pg_dump version 9.5.0

-- Started on 2016-03-31 11:38:13

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = stammdaten, pg_catalog;

--
-- TOC entry 4710 (class 0 OID 535810)
-- Dependencies: 276
-- Data for Name: proben_zusatz; Type: TABLE DATA; Schema: stammdaten; Owner: lada
--

COPY proben_zusatz (id, meh_id, beschreibung, zusatzwert, eudf_keyword) FROM stdin;
A74	32	Volumenstrom	VOLSTR	\N
A75	2	A00-Horizont (lose Blätter oder Nadelteppich)	A00-HZT	\N
A76	2	A0-Horizont (Humusauflage)	A0-HZT	\N
A77	2	A1-Horizont (humoser Mineralboden)	A1-HZT	\N
A78	2	B-Horizont (verdichteter mineralischer Boden)	B-HZT	\N
A79	2	Mächtigkeit	MAECHT	\N
A35	95	Versorgte Einwohner pro Wasserwerk	VERSE/W	\N
A36	33	Abgabemenge pro Wasserwerk	WASSABM	\N
A38	\N	PH-Wert	PHWERT	\N
A39	3	Entnahmetiefe	ENTTIE	\N
A40	2	Entnahmetiefe von	ENTTIEV	\N
A41	2	Entnahmetiefe bis	ENTTIEB	\N
A45	33	Abwasser - Trockenwetterdurchsatz	ABW-TWD	\N
A46	33	Abwasser - Regenwetterdurchsatz	ABW-RWD	\N
A47	30	Verbrennungsanlagen - Luftdurchsatz	VBA-LUD	\N
A48	53	Verbrennungsanlagen - Durchsatzmenge d.verbr.Gutes	VBA-DMV	\N
A49	53	Verbrennungsanlagen - Verbrennungsrückstände	VBA-VRS	\N
A50	32	Verbrennungsanlagen - durchgesetzte Wassermenge	VBA-DWR	\N
A51	19	Deponie - Inbetriebnahme	DEP-IBN	\N
A52	19	Deponie - Schließung	DEP-SLG	\N
A53	54	Kompostierungsanlage - Durchsatz	KOMP-DS	\N
A55	16	Luftfilter - Betriebsdauer	LF-BDAU	\N
A56	16	Luftfilter - Einsatzdauer	LF-EDAU	\N
A57	\N	Oberfläche	OBFLÄCH	\N
A59	13	Messzeit	MEßZEIT	\N
A60	1	Bodenaktivitätsverteilung	BODAKTV	\N
A61	\N	Abschirmfaktor	ABSCHIF	\N
A62	2	Relaxationslänge	RELAX	\N
A01	36	Strömungsgeschwindigkeit	STRGSCH	\N
A02	91	Strömungsrichtung	STRRICH	\N
A03	93	Windstärke	WINDST	\N
A04	91	Windrichtung 0 - 360°(Kurs)	WINDR	WIND_DIRECTION
A05	11	Wassermenge als Volumen	WASMENG	\N
A06	92	Wassertemperatur	WASTEMP	\N
A07	3	Wassertiefe	WASTIEF	\N
A08	94	Salinität	SALIN	\N
A09	92	Lufttemperatur	LUFTEMP	\N
A10	2	Schneehöhe	SCHNEEH	\N
A11	2	Schichttiefe	SCHICHT	\N
A12	2	Bodentiefe	BODTIEF	\N
A13	3	Messtiefe	MESTIEF	\N
A17	47	Schwebstoffkonzentration	SCHWEKO	\N
A18	51	Probenmenge feucht	PRMENGF	\N
A19	50	Probenmenge trocken	PRMENGT	\N
A20	3	Wegstrecke von Startposition	WEG	\N
A21	\N	Niederschlag: Regen	NIEDREG	PRECIPITATION_OCCURRENCE
A22	\N	Niederschlag: Hagel	NIEDHAG	\N
A23	\N	Niederschlag: Schnee	NIEDSCH	SNOW_OCCURRENCE
A25	1	Niederschlagshöhe	NIEDHÖH	PRECIPITATION
A26	14	Niederschlagsdauer	NIEDDAU	\N
A27	30	Abflussmenge eines Flusses	ABFLMEN	\N
A29	4	Flusskilometer	FLUSSKM	\N
A30	45	Bewuchsdichte	BEWUDI	\N
A32	\N	Niederschlag: Graupel	NIEDGRA	\N
A33	\N	Nebel	NEBEL	\N
A34	45	Umrechnungsfaktor von Bq/kg in Bq/m²	UMRFAKT	\N
A72	11	Wasserabgabe	W-ABGAB	\N
A73	11	Fortluftmenge	F-LUFT-	\N
A70	227	Thermische Leistung	T-LEIST	\N
A71	227	Elektrische Leistung	E-LEIST	\N
E74	187	Luftdruck in hPa	LF-DR	PRESSURE
E78	188	Niederschlagsmenge Regen pro Stunde	NIEDR/H	PRECIPITATION_INTENSITY
E54	11	Luftfilter - Luftdurchsatz	LF-LUDS	AIR_VOLUME
E75	\N	Dichte der Wolkendecke	WoDe	CLOUD_COVER
E41	1	Entnahmetiefe bis	ENTTIEB	DEPTH_MAX
E40	1	Entnahmetiefe von	ENTTIEV	DEPTH_MIN
E76	92	Taupunkttemperatur	TauPunk	DEW_POINT_TEMPERATURE
E77	36	vertikale Bewegung der Luft	DOP-RAD	DOPPLER_RADAR
E24	1	Niederschlagsmenge - Schnee	NIEDSCH	SNOW
E80	90	relative Feuchtigkeit	RelFeu	RELATIVE_HUMIDITY
E79	\N	Sonneneinstrahlung	SOLAR	SOLAR_RADIATION
E81	92	Temperatur	TEMP	TEMPERATURE
E82	\N	Regenwahrscheinlichkeit	RegVor	WEATHER_RADAR
E83	91	Veränderung der Windrichtung	WinVerR	WIND_DIRECTION_FLUCTUATION
E03	36	Windstärke (m/s)	WINDST	WIND_SPEED
E84	36	Veränderung der Windstärke	WinVerG	WIND_SPEED_FLUCTUATION
A80	21	Warenumfang in kg für §7	WARENKG	\N
A81	27	Warenumfang in l für §7	WAREN-L	\N
A54	30	Luftfilter - Luftdurchsatz - Rate	LF-LUDR	\N
\.
