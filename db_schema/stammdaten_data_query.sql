--
-- PostgreSQL database dump
--

-- Dumped from database version 10.4
-- Dumped by pg_dump version 10.3

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Data for Name: base_query; Type: TABLE DATA; Schema: stamm; Owner: postgres
--

COPY stamm.base_query (id, sql) FROM stdin;
1	SELECT probe.id AS probeId,\n  probe.hauptproben_nr AS hpNr,\n  datenbasis.datenbasis AS dBasis,\n  stamm.mess_stelle.netzbetreiber_id AS netzId,\n  probe.mst_id AS mstId,\n  probe.umw_id AS umwId,\n  probenart.probenart AS pArt,\n  probe.solldatum_beginn AS sollBegin,\n  probe.solldatum_ende AS sollEnd,\n  probe.probeentnahme_beginn AS peBegin,\n  probe.probeentnahme_ende AS peEnd,\n  ort.ort_id AS ortId,\n  ort.gem_id AS eGemId,\n  verwaltungseinheit.bezeichnung AS eGem,\n  probe.ext_id AS extPId,\n  land.messprogramm.id AS mprId\nFROM land.probe\nLEFT JOIN stamm.mess_stelle ON (probe.mst_id = stamm.mess_stelle.id)\nLEFT JOIN stamm.datenbasis ON (probe.datenbasis_id = datenbasis.id)\nLEFT JOIN stamm.probenart ON (probe.probenart_id = probenart.id)\nLEFT JOIN land.ortszuordnung ON (\n    probe.id = ortszuordnung.probe_id\n    AND ortszuordnung.ortszuordnung_typ IN ('E', 'R')\n    )\nLEFT JOIN stamm.ort ON (ortszuordnung.ort_id = ort.id)\nLEFT JOIN stamm.verwaltungseinheit ON (ort.gem_id = verwaltungseinheit.id)\nLEFT JOIN land.messprogramm\n  ON (probe.mpr_id = land.messprogramm.id)
31	SELECT ort.id,\n  ort.netzbetreiber_id AS netzbetreiberId,\n  ort.ort_id AS ortId,\n  ort_typ.code AS ortTyp,\n  ort.kurztext,\n  ort.langtext,\n  staat.staat_iso AS staat,\n  verwaltungseinheit.bezeichnung AS verwaltungseinheit,\n  nuts_code AS nutsCode,\n  oz_id AS ozId,\n  kta_gruppe AS anlageId,\n  mp_art AS mpArt,\n  zone,\n  sektor,\n  zustaendigkeit,\n  berichtstext,\n  unscharf,\n  koordinaten_art.koordinatenart AS koordinatenArt,\n  ort.koord_x_extern AS koordXExtern,\n  ort.koord_y_extern AS koordYExtern,\n  PUBLIC.ST_X(ort.geom) AS longitude,\n  PUBLIC.ST_Y(ort.geom) AS latitude,\n  hoehe_ueber_nn AS hoeheUeberNn,\n  hoehe_land AS hoeheLand,\n  aktiv,\n  letzte_aenderung AS letzteAenderung\nFROM stamm.ort\nLEFT JOIN stamm.verwaltungseinheit\n  ON ort.gem_id = verwaltungseinheit.id\nLEFT JOIN stamm.staat\n  ON stamm.staat.id = ort.staat_id\nINNER JOIN stamm.koordinaten_art\n  ON stamm.koordinaten_art.id = ort.kda_id\nLEFT JOIN stamm.ort_typ\n  ON ort.ort_typ = ort_typ.id\nLEFT JOIN stamm.kta_gruppe\n  ON kta_gruppe.id = ort.kta_gruppe_id\n
21	SELECT messprogramm.id AS mpNr,\n  stamm.mess_stelle.netzbetreiber_id AS netzId,\n  CASE \n    WHEN messprogramm.mst_id = messprogramm.labor_mst_id\n      THEN messprogramm.mst_id\n    ELSE messprogramm.mst_id || '-' || messprogramm.labor_mst_id\n    END AS mstLaborId,\n  messprogramm.aktiv AS aktiv,\n  datenbasis.datenbasis AS dBasis,\n  CASE \n    WHEN messprogramm.ba_id = '1'\n      THEN 'RB'\n    ELSE 'IB'\n    END AS messRegime,\n  probenart.probenart AS pArt,\n  messprogramm.umw_id AS umwId,\n  messprogramm.media_desk AS deskriptoren,\n  messprogramm.probenintervall AS intervall,\n  ort.ort_id AS ortId,\n  ort.gem_id AS eGemId,\n  verwaltungseinheit.bezeichnung AS eGem\nFROM land.messprogramm\nLEFT JOIN stamm.mess_stelle\n  ON (messprogramm.mst_id = stamm.mess_stelle.id)\nLEFT JOIN stamm.datenbasis\n  ON (messprogramm.datenbasis_id = datenbasis.id)\nLEFT JOIN stamm.probenart\n  ON (messprogramm.probenart_id = probenart.id)\nLEFT JOIN land.ortszuordnung_mp\n  ON (\n      messprogramm.id = ortszuordnung_mp.messprogramm_id\n      AND ortszuordnung_mp.ortszuordnung_typ IN ('E', 'R')\n      )\nLEFT JOIN stamm.ort\n  ON (ortszuordnung_mp.ort_id = ort.id)\nLEFT JOIN stamm.verwaltungseinheit\n  ON (ort.gem_id = verwaltungseinheit.id)\n
33	SELECT id, netzbetreiber_id AS netzbetreiberId, datensatz_erzeuger_id AS datensatzErzeugerId, mst_id AS mstId, bezeichnung, letzte_aenderung AS letzteAenderung FROM stamm.datensatz_erzeuger
32	SELECT id, netzbetreiber_id AS netzbetreiberId, prn_id AS prnId, bearbeiter, bemerkung, betrieb, bezeichnung, kurz_bezeichnung AS kurzBezeichnung, ort, plz, strasse, telefon, tp, typ, letzte_aenderung AS letzteAenderung FROM stamm.probenehmer
34	SELECT id, netzbetreiber_id AS netzbetreiberId, code, bezeichnung, letzte_aenderung AS letzteAenderung FROM stamm.messprogramm_kategorie
11	SELECT messung.id,\n  probe.id AS probeId,\n  probe.hauptproben_nr AS hpNr,\n  messung.nebenproben_nr AS npNr,\n  status_stufe.stufe AS statusSt,\n  status_wert.wert AS statusW,\n  status_protokoll.datum AS statusD,\n  datenbasis.datenbasis AS dBasis,\n  mess_stelle.netzbetreiber_id AS netzId,\n  probe.mst_id AS mstId,\n  probe.umw_id AS umwId,\n  probenart.probenart AS pArt,\n  probe.probeentnahme_beginn AS peBegin,\n  probe.probeentnahme_ende AS peEnd,\n  ort.gem_id AS eGemId,\n  verwaltungseinheit.bezeichnung AS eGem,\n  coalesce(h3.messwert_nwg, ' ') || to_char(h3.messwert, '0.99eeee') AS h3,\n  coalesce(k40.messwert_nwg, ' ') || to_char(k40.messwert, '0.99eeee') AS k40,\n  coalesce(co60.messwert_nwg, ' ') || to_char(co60.messwert, '0.99eeee') AS co60,\n  coalesce(sr89.messwert_nwg, ' ') || to_char(sr89.messwert, '0.99eeee') AS sr89,\n  coalesce(sr90.messwert_nwg, ' ') || to_char(sr90.messwert, '0.99eeee') AS sr90,\n  coalesce(ru103.messwert_nwg, ' ') || to_char(ru103.messwert, '0.99eeee') AS ru103,\n  coalesce(i131.messwert_nwg, ' ') || to_char(i131.messwert, '0.99eeee') AS i131,\n  coalesce(cs134.messwert_nwg, ' ') || to_char(cs134.messwert, '0.99eeee') AS cs134,\n  coalesce(cs137.messwert_nwg, ' ') || to_char(cs137.messwert, '0.99eeee') AS cs137,\n  coalesce(ce144.messwert_nwg, ' ') || to_char(ce144.messwert, '0.99eeee') AS ce144,\n  coalesce(u234.messwert_nwg, ' ') || to_char(u234.messwert, '0.99eeee') AS u234,\n  coalesce(u235.messwert_nwg, ' ') || to_char(u235.messwert, '0.99eeee') AS u235,\n  coalesce(u238.messwert_nwg, ' ') || to_char(u238.messwert, '0.99eeee') AS u238,\n  coalesce(pu238.messwert_nwg, ' ') || to_char(pu238.messwert, '0.99eeee') AS pu238,\n  coalesce(pu239.messwert_nwg, ' ') || to_char(pu239.messwert, '0.99eeee') AS pu239,\n  coalesce(pu23940.messwert_nwg, ' ') || to_char(pu23940.messwert, '0.99eeee') AS pu23940,\n  coalesce(te132.messwert_nwg, ' ') || to_char(te132.messwert, '0.99eeee') AS te132,\n  coalesce(pb212.messwert_nwg, ' ') || to_char(pb212.messwert, '0.99eeee') AS pb212,\n  coalesce(pb214.messwert_nwg, ' ') || to_char(pb214.messwert, '0.99eeee') AS pb214,\n  coalesce(bi212.messwert_nwg, ' ') || to_char(bi212.messwert, '0.99eeee') AS bi212,\n  coalesce(bi214.messwert_nwg, ' ') || to_char(bi214.messwert, '0.99eeee') AS bi214,\n  messung.mmt_id AS mmtId,\n  probe.ext_id AS extPId,\n  messung.ext_id AS extMId\nFROM land.probe\nLEFT JOIN stamm.mess_stelle\n  ON (probe.mst_id = stamm.mess_stelle.id)\nINNER JOIN land.messung\n  ON probe.id = messung.probe_id\nINNER JOIN land.status_protokoll\n  ON messung.STATUS = status_protokoll.id\nLEFT JOIN stamm.status_kombi\n  ON status_protokoll.status_kombi = stamm.status_kombi.id\nLEFT JOIN stamm.status_wert\n  ON stamm.status_wert.id = stamm.status_kombi.wert_id\nLEFT JOIN stamm.status_stufe\n  ON stamm.status_stufe.id = stamm.status_kombi.stufe_id\nLEFT JOIN stamm.datenbasis\n  ON (probe.datenbasis_id = datenbasis.id)\nLEFT JOIN stamm.probenart\n  ON (probe.probenart_id = probenart.id)\nLEFT JOIN land.ortszuordnung\n  ON (\n      probe.id = ortszuordnung.probe_id\n      AND ortszuordnung.ortszuordnung_typ IN ('E', 'R')\n      )\nLEFT JOIN stamm.ort\n  ON (ortszuordnung.ort_id = ort.id)\nLEFT JOIN stamm.verwaltungseinheit\n  ON (ort.gem_id = verwaltungseinheit.id)\nLEFT JOIN land.messwert h3\n  ON (h3.messungs_id = messung.id AND h3.messgroesse_id = 1)\nLEFT JOIN land.messwert k40\n  ON (k40.messungs_id = messung.id AND k40.messgroesse_id = 28)\nLEFT JOIN land.messwert co60\n  ON (co60.messungs_id = messung.id AND co60.messgroesse_id = 68)\nLEFT JOIN land.messwert sr89\n  ON (sr89.messungs_id = messung.id AND sr89.messgroesse_id = 164)\nLEFT JOIN land.messwert sr90\n  ON (sr90.messungs_id = messung.id AND sr90.messgroesse_id = 165)\nLEFT JOIN land.messwert ru103\n  ON (ru103.messungs_id = messung.id AND ru103.messgroesse_id = 220)\nLEFT JOIN land.messwert i131\n  ON (i131.messungs_id = messung.id AND i131.messgroesse_id = 340)\nLEFT JOIN land.messwert cs134\n  ON (cs134.messungs_id = messung.id AND cs134.messgroesse_id = 369)\nLEFT JOIN land.messwert cs137\n  ON (cs137.messungs_id = messung.id AND cs137.messgroesse_id = 373)\nLEFT JOIN land.messwert ce144\n  ON (ce144.messungs_id = messung.id AND ce144.messgroesse_id = 404)\nLEFT JOIN land.messwert u234\n  ON (u234.messungs_id = messung.id AND u234.messgroesse_id = 746)\nLEFT JOIN land.messwert u235\n  ON (u235.messungs_id = messung.id AND u235.messgroesse_id = 747)\nLEFT JOIN land.messwert u238\n  ON (u238.messungs_id = messung.id AND u238.messgroesse_id = 750)\nLEFT JOIN land.messwert pu238\n  ON (pu238.messungs_id = messung.id AND pu238.messgroesse_id = 768)\nLEFT JOIN land.messwert pu239\n  ON (pu239.messungs_id = messung.id AND pu239.messgroesse_id = 769)\nLEFT JOIN land.messwert pu23940\n  ON (pu23940.messungs_id = messung.id AND pu23940.messgroesse_id = 850)\nLEFT JOIN land.messwert te132\n  ON (te132.messungs_id = messung.id AND te132.messgroesse_id = 325)\nLEFT JOIN land.messwert pb212\n  ON (pb212.messungs_id = messung.id AND pb212.messgroesse_id = 672)\nLEFT JOIN land.messwert pb214\n  ON (pb214.messungs_id = messung.id AND pb214.messgroesse_id = 673)\nLEFT JOIN land.messwert bi212\n  ON (bi212.messungs_id = messung.id AND bi212.messgroesse_id = 684)\nLEFT JOIN land.messwert bi214\n  ON (bi214.messungs_id = messung.id AND bi214.messgroesse_id = 686)
\.


--
-- Data for Name: filter; Type: TABLE DATA; Schema: stamm; Owner: postgres
--

COPY stamm.filter (id, sql, parameter, type, name) FROM stdin;
1	probe.ext_id LIKE :extPId	extPId	1	probe_ext_id
2	probe.hauptproben_nr LIKE :hauptprobenNr	hauptprobenNr	0	probe_hauptproben_nr
3	probe.mst_id IN ( :mstId )	mstId	4	probe_mst_id
4	probe.umw_id IN ( :umwId )	umwId	4	probe_umw_id
5	probe.test = cast(:test AS boolean)	test	2	probe_test
6	probe.probeentnahme_beginn >= to_timestamp(cast(:timeBegin AS DOUBLE PRECISION))	timeBegin	3	probe_entnahme_beginn
7	probe.probeentnahme_ende <= to_timestamp(cast(:timeEnd AS DOUBLE PRECISION))	timeEnd	3	probe_entnahme_beginn
8	datenbasis.id IN (:datenbasis)	datenbasis	5	datenbasis
9	probenart.id IN (:probenart)	probenart	5	probenart
10	ort.gem_id LIKE :gemId	gemId	0	ort_gem_id
11	ort.ort_id LIKE :ortId	ortId	0	ort_ort_id
12	verwaltungseinheit.bezeichnung LIKE :bezeichnung	bezeichnung	0	verwaltungseinheit_bezeichnung
15	probe.probeentnahme_ende BETWEEN to_timestamp(cast(:fromPeEnd AS DOUBLE PRECISION)) AND to_timestamp(cast(:toPeEnd AS DOUBLE PRECISION))	fromPeEnd,toPeEnd	6	Entnahmeende von-bis
16	probe.letzte_aenderung BETWEEN to_timestamp(cast(probeLetzteAenderungFrom AS DOBULE PRECISION)) AND to_timestamp(cast(probeLetzteAenderungTo AS DOUBLE PRECISION))	probeLetzteAenderungFrom,probeLetzteAenderungTo	6	Letzte Aenderung von-bis
17	probe.id = :probeId	probeId	0	ProbeID
18	:genTextParam LIKE :genTextValue	genText	7	generic_text_filter
19	status_stufe.id IN :statusStufe	statusStufe	5	statusStufe_filter
28	status_wert.id IN :statusWert	statusWert	5	statusWert_filter
14	probe.probeentnahme_beginn BETWEEN to_timestamp(cast(:fromPeBegin AS DOUBLE PRECISION)) AND to_timestamp(cast(:toPeBegin AS DOUBLE PRECISION))	fromPeBegin,toPeBegin	6	Entnahmebeginn von-bis
21	probe.solldatum_ende BETWEEN to_timestamp(cast(:fromSollEnde AS DOUBLE PRECISION)) AND to_timestamp(cast(:toSollEnde AS DOUBLE PRECISION))	fromSollEnde,toSollEnde	6	Sollend von-bis
20	probe.solldatum_beginn BETWEEN to_timestamp(cast(:fromSollBegin AS DOUBLE PRECISION)) AND to_timestamp(cast(:toSollBegin AS DOUBLE PRECISION))	fromSollBegin,toSollBegin	6	Sollbeginn von-bis
22	messung.nebenproben_nr LIKE :nebenproben_nr	nebenproben_nr	0	messung_nebenproben_nr
13	mess_stelle.netzbetreiber_id IN ( :netzbetreiberId )	netzbetreiberId	4	netzbetreiber_id
23	ort.netzbetreiber_id IN ( :netzbetreiberId )	netzbetreiberId	4	netzbetreiber_id
24	probenehmer.netzbetreiber_id IN ( :netzbetreiberId )	netzbetreiberId	4	netzbetreiber_id
25	datensatz_erzeuger.netzbetreiber_id IN ( :netzbetreiberId )	netzbetreiberId	4	netzbetreiber_id
26	messprogramm_kategorie.netzbetreiber_id IN ( :netzbetreiberId )	netzbetreiberId	4	netzbetreiber_id
27	(messprogramm.mst_id IN ( :mstId ) OR messprogramm.labor_mst_id IN ( :mstId ))	mstId	4	messprogramm_mst_id
\.


--
-- Data for Name: result_type; Type: TABLE DATA; Schema: stamm; Owner: postgres
--

COPY stamm.result_type (id, name, format) FROM stdin;
1	text	\N
2	date	d.m.Y H:i
3	number	\N
4	probeId	\N
5	messungId	\N
6	ortId	\N
7	geom	\N
8	mpId	\N
9	land	\N
10	messstelle	\N
11	boolean	\N
12	umwbereich	\N
13	statusstfe	\N
14	status	\N
15	number	e
16	egem	\N
17	datenbasis	\N
18	netzbetr	\N
19	probenart	\N
20	staat	\N
21	pnehmer	\N
22	dsatzerz	\N
23	mprkat	\N
\.


--
-- Data for Name: grid_column; Type: TABLE DATA; Schema: stamm; Owner: postgres
--

COPY stamm.grid_column (id, base_query, name, data_index, "position", filter, data_type) FROM stdin;
101	1	interne PID	probeId	1	17	4
102	1	HP-Nr.	hpNr	2	2	1
103	1	Datenbasis	dBasis	3	8	17
104	1	Netzbetreiber	netzId	4	13	18
105	1	MST	mstId	5	3	10
106	1	Umw-ID	umwId	6	4	12
107	1	Probenart	pArt	7	9	19
110	1	Probenahme von	peBegin	10	14	2
111	1	Probenahme bis	peEnd	11	15	2
113	1	E-Gem-ID	eGemId	13	10	16
114	1	E-Gemeinde	eGem	14	18	1
115	1	externe PID	extPId	15	1	1
116	1	MPR-ID	mprId	16	\N	8
1101	11	id	id	1	\N	5
1102	11	interne PID	probeId	2	17	4
1103	11	HP-Nr	hpNr	3	2	1
1106	11	Statuswert	statusW	6	28	14
1107	11	Status Datum	statusD	7	\N	2
1108	11	Datenbasis	dBasis	8	8	1
1109	11	Netzbetreiber	netzId	9	13	1
1110	11	MST	mstId	10	3	10
1111	11	Umw-ID	umwId	11	4	12
1112	11	Probenart	pArt	12	\N	1
1113	11	Probenahme von	peBegin	13	14	2
1114	11	Probenahme bis	peEnd	14	15	2
1115	11	E-Gem-ID	eGemId	15	16	1
1116	11	E-Gemeinde	eGem	16	10	1
1117	11	H-3	h3	17	\N	15
1118	11	K-40	k40	18	\N	15
1119	11	Co-60	co60	19	\N	15
1120	11	Sr-89	sr89	20	\N	15
1121	11	Sr-90	sr90	21	\N	15
1122	11	Ru-103	ru103	22	\N	15
1123	11	I-131	i131	23	\N	15
1124	11	Cs-134	cs134	24	\N	15
1125	11	Cs-137	cs137	25	\N	15
1126	11	Ce-144	ce144	26	\N	15
1127	11	U-234	u234	27	\N	15
1128	11	U-235	u235	28	\N	15
1129	11	U-238	u238	29	\N	15
1130	11	Pu-238	pu238	30	\N	15
1131	11	Pu-239	pu239	31	\N	15
1132	11	Pu-239/240	pu23940	32	\N	15
1133	11	Te-132	te132	33	\N	15
1134	11	Pb-212	pb212	34	\N	15
1135	11	Pb-214	pb214	35	\N	15
1136	11	Bi-212	bi212	36	\N	15
1137	11	Bi-214	bi214	37	\N	15
1138	11	MMT-ID	mmtId	38	\N	1
1139	11	externe PID	extPId	39	\N	1
1140	11	externe MID	extMId	40	\N	1
2101	21	MPR-ID	mpNr	1	\N	8
2102	21	Netzbetreiber	netzId 	2	13	18
2103	21	MST/Labor	mstLaborId	3	27	10
2113	21	Aktiv	aktiv	4	\N	11
2104	21	Datenbasis	dBasis	5	8	17
2105	21	Messregime	messRegime	6	\N	1
2106	21	Probenart	pArt	7	9	19
2107	21	Umw-ID	umwId	8	4	12
2108	21	Deskriptoren	deskriptoren	9	\N	1
2109	21	Probenintervall	intervall	10	\N	1
2111	21	E-Gem-ID	eGemId	11	10	1
2112	21	E-Gemeinde	eGem	12	\N	1
3101	31	Id	id	1	11	6
3104	31	Ortstyp	ortTyp	4	\N	1
3105	31	Kurtext	kurztext	5	18	1
3106	31	Langtext	langtext	6	18	1
3107	31	Staat	staat	7	\N	20
3108	31	Verwaltungseinheit	verwaltungseinheit	8	12	1
3109	31	NUTS-Code	nutsCode	9	18	1
3110	31	OZ-ID	ozId	10	\N	1
3111	31	KTA Gruppe	anlageId	11	\N	1
3112	31	mpArt	mpArt	12	9	1
3113	31	Koordinatenart	koordinatenArt	13	\N	1
3114	31	X-Koordinate	koordXExtern	14	\N	3
3116	31	Y-Koordinate	koordYExtern	16	\N	3
3117	31	Longitude	longitude	17	\N	3
3118	31	Latitude	latitude	18	\N	3
3119	31	Höhe über NN	hoeheUeberNn	19	\N	3
3121	31	Höhe	hoeheLand	21	\N	3
3122	31	Aktiv	aktiv	22	\N	11
3123	31	letzte Änderung	letzteAenderung	23	\N	2
3201	32	Id	id	1	\N	21
3203	32	prn	prnId	3	\N	1
3204	32	Bearbeiter	bearbeiter	4	\N	1
3205	32	Bemerkung	bemerkung	5	\N	1
3206	32	Betrieb	betrieb	6	\N	1
3207	32	Bezeichnung	bezeichnung	7	\N	1
3208	32	Kurzbezeichnung	kurzBezeichnung	8	\N	1
3209	32	Ort	ort	9	\N	1
3210	32	PLZ	plz	10	18	3
3211	32	Strasse	strasse	11	18	1
3212	32	Telefon	telefon	12	18	1
3213	32	tp	tp	13	\N	1
3214	32	Typ	typ	14	\N	1
3216	32	letzte Änderung	letzteAenderung	16	\N	2
3301	33	Id	id	1	\N	22
3401	34	Id	id	1	\N	23
3403	34	Code	code	3	\N	1
3404	34	Bezeichnung	bezeichnung	4	\N	1
3405	34	letzte Änderung	letzteAenderung	5	\N	2
2110	21	Ort-ID	ortId	10	\N	1
112	1	Ort-ID	ortId	12	1	1
109	1	Solldatum bis	sollEnd	9	21	2
108	1	Solldatum von	sollBegin	8	20	2
1104	11	NP-Nr	npNr	4	22	1
1105	11	Statusstufe	statusSt	5	19	13
3103	31	Ort-ID	ortId	3	2	1
3305	33	Bezeichnung	bezeichnung	5	\N	1
3306	33	letzte Änderung	letzteAenderung	6	\N	2
3202	32	Netzbetreiber	netzbetreiberId	2	24	18
3102	31	Netzbetreiber	netzbetreiberId	2	23	18
3402	34	Netzbetreiber	netzbetreiberId	2	26	18
3302	33	Netzbetreiber	netzId	2	25	18
3304	33	Datensatzerzeuger	datensatzErzeugerId	4	\N	1
3303	33	MST	mstId	3	3	10
\.


--
-- Data for Name: query_user; Type: TABLE DATA; Schema: stamm; Owner: postgres
--

COPY stamm.query_user (id, name, user_id, base_query, description) FROM stdin;
4	Orte	0	31	Abfrage der Orte
5	Probenehmer	0	32	Abfrage der Probenehmer
6	Datensatzerzeuger	0	33	Abfrage der Datensatzerzeuger
7	Messprogrammkategorie	0	34	Abfrage der Messprogrammkategorien
2	Messungen	0	11	Vorlage für Messungsselektion
1	Proben	0	1	Vorlage für Probenselektion
3	Messprogramme	0	21	Vorlage für Messprogrammselektion
\.


--
-- Data for Name: grid_column_values; Type: TABLE DATA; Schema: stamm; Owner: postgres
--

COPY stamm.grid_column_values (id, user_id, grid_column, query_user, sort, sort_index, filter_value, filter_active, visible, column_index, width) FROM stdin;
1	0	102	1	\N	\N	\N	f	t	1	\N
2	0	103	1	\N	\N	\N	f	t	2	\N
6	0	107	1	\N	\N	\N	f	t	6	\N
9	0	1103	2	\N	\N	\N	f	t	1	\N
10	0	1104	2	\N	\N	\N	f	t	2	\N
76	0	1138	2	\N	\N	\N	f	t	3	\N
11	0	1105	2	\N	\N	\N	t	t	4	\N
12	0	1106	2	\N	\N	\N	t	t	5	\N
13	0	1107	2	\N	\N	\N	f	t	6	\N
14	0	1108	2	\N	\N	\N	f	t	7	\N
15	0	1109	2	\N	\N	\N	f	t	8	\N
16	0	1111	2	\N	\N	\N	t	t	9	\N
17	0	1112	2	\N	\N	\N	f	t	10	\N
18	0	1113	2	\N	\N	\N	t	t	11	\N
19	0	1114	2	\N	\N	\N	f	t	12	\N
20	0	1115	2	\N	\N	\N	f	t	13	\N
21	0	1116	2	\N	\N	\N	f	t	14	\N
22	0	1117	2	\N	\N	\N	f	t	15	\N
23	0	1118	2	\N	\N	\N	f	t	16	\N
24	0	2101	3	\N	\N	\N	f	t	1	\N
25	0	2102	3	\N	\N	\N	t	t	2	\N
26	0	2103	3	\N	\N	\N	t	t	3	\N
27	0	2104	3	\N	\N	\N	f	t	4	\N
28	0	2105	3	\N	\N	\N	f	t	5	\N
29	0	2106	3	\N	\N	\N	f	t	6	\N
30	0	2107	3	\N	\N	\N	f	t	7	\N
31	0	2108	3	\N	\N	\N	f	t	8	\N
32	0	2109	3	\N	\N	\N	f	t	9	\N
33	0	2110	3	\N	\N	\N	f	t	10	\N
34	0	2111	3	\N	\N	\N	f	t	11	\N
35	0	2112	3	\N	\N	\N	f	t	12	\N
36	0	2113	3	\N	\N	\N	f	t	13	\N
8	0	111	1	\N	\N	\N	f	t	8	\N
7	0	110	1	\N	\N	\N	f	t	7	\N
4	0	105	1	\N	\N	\N	t	t	4	\N
3	0	104	1	\N	\N	\N	t	t	3	\N
5	0	106	1	\N	\N	\N	t	t	5	\N
58	0	3103	4	\N	\N	\N	f	t	2	\N
57	0	3102	4	\N	\N	\N	t	t	1	\N
60	0	3105	4	\N	\N	\N	f	t	4	\N
59	0	3104	4	\N	\N	\N	f	t	3	\N
62	0	3107	4	\N	\N	\N	f	t	6	\N
61	0	3106	4	\N	\N	\N	f	t	5	\N
63	0	3108	4	\N	\N	\N	f	t	7	\N
65	0	3203	5	\N	\N	\N	f	t	2	\N
66	0	3207	5	\N	\N	\N	f	t	3	\N
68	0	3303	6	\N	\N	\N	f	t	2	\N
71	0	3304	6	\N	\N	\N	f	t	3	\N
72	0	3305	6	\N	\N	\N	f	t	4	\N
73	0	3402	7	\N	\N	\N	t	t	1	\N
74	0	3403	7	\N	\N	\N	f	t	2	\N
64	0	3202	5	\N	\N	\N	t	t	1	\N
67	0	3302	6	\N	\N	\N	t	t	1	\N
75	0	3404	7	\N	\N	\N	f	t	3	\N
\.


--
-- Name: base_query_id_seq; Type: SEQUENCE SET; Schema: stamm; Owner: postgres
--

SELECT pg_catalog.setval('stamm.base_query_id_seq', 34, true);


--
-- Name: filter_id_seq; Type: SEQUENCE SET; Schema: stamm; Owner: postgres
--

SELECT pg_catalog.setval('stamm.filter_id_seq', 27, true);


--
-- Name: grid_column_id_seq; Type: SEQUENCE SET; Schema: stamm; Owner: postgres
--

SELECT pg_catalog.setval('stamm.grid_column_id_seq', 3405, true);


--
-- Name: grid_column_values_id_seq; Type: SEQUENCE SET; Schema: stamm; Owner: postgres
--

SELECT pg_catalog.setval('stamm.grid_column_values_id_seq', 76, true);


--
-- Name: query_user_id_seq; Type: SEQUENCE SET; Schema: stamm; Owner: postgres
--

SELECT pg_catalog.setval('stamm.query_user_id_seq', 7, true);


--
-- Name: result_type_id_seq; Type: SEQUENCE SET; Schema: stamm; Owner: postgres
--

SELECT pg_catalog.setval('stamm.result_type_id_seq', 1, false);


--
-- PostgreSQL database dump complete
--

