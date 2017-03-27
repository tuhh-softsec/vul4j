\set ON_ERROR_STOP on
--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.2
-- Dumped by pg_dump version 9.6.2

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = stammdaten, pg_catalog;

--
-- Data for Name: query; Type: TABLE DATA; Schema: stammdaten; Owner: postgres
--

COPY query (id, name, type, sql, description) FROM stdin;
10	Orte	3		Abfrage der Orte
11	Probenehmer	4		Abfrage der Probenehmer
12	Datensatzerzeuger	5		Abfrage der Datensatzerzeuger
14	Messprogrammkategorie	6		Abfrage der Messprogrammkategorien
7	Proben pro Land und UMW (Multiselect)	0	SELECT probe.id AS id, probe.hauptproben_nr AS hpNr, datenbasis.datenbasis AS dBasis, stammdaten.mess_stelle.netzbetreiber_id AS netzId, probe.mst_id AS mstId, probe.umw_id AS umwId, probenart.probenart AS pArt, to_char(probe.probeentnahme_beginn, 'dd.mm.YYYY hh24:MI') AS peBegin, to_char(probe.probeentnahme_ende, 'dd.mm.YYYY hh24:MI') AS peEnd, ort.ort_id AS ortId, ort.gem_id AS eGemId, verwaltungseinheit.bezeichnung AS eGem, probe.id_alt AS probeId FROM land.probe LEFT JOIN stammdaten.mess_stelle ON (probe.mst_id = stammdaten.mess_stelle.id) LEFT JOIN stammdaten.datenbasis ON (probe.datenbasis_id = datenbasis.id) LEFT JOIN stammdaten.probenart ON (probe.probenart_id = probenart.id) LEFT OUTER JOIN land.ortszuordnung ON ( probe.id = ortszuordnung.probe_id AND ortszuordnung.ortszuordnung_typ = 'E' ) LEFT OUTER JOIN stammdaten.ort ON (ortszuordnung.ort_id = ort.id) LEFT OUTER JOIN stammdaten.verwaltungseinheit ON (ort.gem_id = verwaltungseinheit.id) WHERE (stammdaten.mess_stelle.netzbetreiber_id = :netzIdFilter OR '' =:netzIdFilter) AND (probe.umw_id SIMILAR TO (:umwIdFilter) OR '' = :umwIdFilter)	Abfrage aller Proben gefiltert pro Land und Umweltbereich (mit Mehrfachauswahl)
15	kein Filter	1	SELECT messung.id, probe.id AS probeId, probe.hauptproben_nr AS hpNr, messung.nebenproben_nr AS npNr, to_char(status_protokoll.datum, 'dd.mm.YYYY hh24:MI') AS statusD, stammdaten.status_stufe.stufe AS statusSt, stammdaten.status_wert.wert AS statusW, datenbasis.datenbasis AS dBasis, stammdaten.mess_stelle.netzbetreiber_id AS netzId, probe.mst_id AS mstId, probe.umw_id AS umwId, probenart.probenart AS pArt, to_char(probe.probeentnahme_beginn, 'dd.mm.YYYY hh24:MI') AS peBegin, to_char(probe.probeentnahme_ende, 'dd.mm.YYYY hh24:MI') AS peEnd, ort.ort_id AS ortId, ort.gem_id AS eGemId, verwaltungseinheit.bezeichnung AS eGem FROM land.probe LEFT JOIN stammdaten.mess_stelle ON (probe.mst_id = stammdaten.mess_stelle.id) INNER JOIN land.messung ON probe.id = messung.probe_id INNER JOIN land.status_protokoll ON messung.STATUS = status_protokoll.id LEFT JOIN stammdaten.status_kombi ON status_protokoll.status_kombi = stammdaten.status_kombi.id LEFT JOIN stammdaten.status_wert ON stammdaten.status_wert.id = stammdaten.status_kombi.wert_id LEFT JOIN stammdaten.status_stufe ON stammdaten.status_stufe.id = stammdaten.status_kombi.stufe_id LEFT JOIN stammdaten.datenbasis ON (probe.datenbasis_id = datenbasis.id) LEFT JOIN stammdaten.probenart ON (probe.probenart_id = probenart.id) LEFT JOIN land.ortszuordnung ON ( probe.id = ortszuordnung.probe_id AND ortszuordnung.ortszuordnung_typ = 'E' ) LEFT JOIN stammdaten.ort ON (ortszuordnung.ort_id = ort.id) LEFT JOIN stammdaten.verwaltungseinheit ON (ort.gem_id = verwaltungseinheit.id) 	kein Filter
13	nach Status	1	SELECT messung.id, probe.id AS probeId, probe.hauptproben_nr AS hpNr, messung.nebenproben_nr AS npNr, to_char(status_protokoll.datum, 'dd.mm.YYYY hh24:MI') AS statusD, stammdaten.status_stufe.stufe AS statusSt, stammdaten.status_wert.wert AS statusW, datenbasis.datenbasis AS dBasis, stammdaten.mess_stelle.netzbetreiber_id AS netzId, probe.mst_id AS mstId, probe.umw_id AS umwId, probenart.probenart AS pArt, to_char(probe.probeentnahme_beginn, 'dd.mm.YYYY hh24:MI') AS peBegin, to_char(probe.probeentnahme_ende, 'dd.mm.YYYY hh24:MI') AS peEnd, ort.ort_id AS ortId, ort.gem_id AS eGemId, verwaltungseinheit.bezeichnung AS eGem FROM land.probe LEFT JOIN stammdaten.mess_stelle ON (probe.mst_id = stammdaten.mess_stelle.id) INNER JOIN land.messung ON probe.id = messung.probe_id INNER JOIN land.status_protokoll ON messung.STATUS = status_protokoll.id LEFT JOIN stammdaten.status_kombi ON status_protokoll.status_kombi = stammdaten.status_kombi.id LEFT JOIN stammdaten.status_wert ON stammdaten.status_wert.id = stammdaten.status_kombi.wert_id LEFT JOIN stammdaten.status_stufe ON stammdaten.status_stufe.id = stammdaten.status_kombi.stufe_id LEFT JOIN stammdaten.datenbasis ON (probe.datenbasis_id = datenbasis.id) LEFT JOIN stammdaten.probenart ON (probe.probenart_id = probenart.id) LEFT JOIN land.ortszuordnung ON ( probe.id = ortszuordnung.probe_id AND ortszuordnung.ortszuordnung_typ = 'E' ) LEFT JOIN stammdaten.ort ON (ortszuordnung.ort_id = ort.id) LEFT JOIN stammdaten.verwaltungseinheit ON (ort.gem_id = verwaltungseinheit.id) WHERE CAST(stammdaten.status_wert.id AS text) SIMILAR TO CASE WHEN :statusFilter = '' THEN '%' ELSE :statusFilter END	Messungen nach Status
1	Proben	0	SELECT probe.id AS id,\n  probe.hauptproben_nr AS hpNr,\n  datenbasis.datenbasis AS dBasis,\n  stammdaten.mess_stelle.netzbetreiber_id AS netzId,\n  probe.mst_id AS mstId,\n  probe.umw_id AS umwId,\n  probenart.probenart AS pArt,\n  to_char(probe.probeentnahme_beginn, 'dd.mm.YYYY hh24:MI') AS peBegin,\n  to_char(probe.probeentnahme_ende, 'dd.mm.YYYY hh24:MI') AS peEnd,\n  ort.ort_id AS ortId,\n  ort.gem_id AS eGemId,\n  verwaltungseinheit.bezeichnung AS eGem,\n  probe.id_alt AS probeId\nFROM land.probe\nLEFT JOIN stammdaten.mess_stelle\n  ON (probe.mst_id = stammdaten.mess_stelle.id)\nLEFT JOIN stammdaten.datenbasis\n  ON (probe.datenbasis_id = datenbasis.id)\nLEFT JOIN stammdaten.probenart\n  ON (probe.probenart_id = probenart.id)\nLEFT JOIN land.ortszuordnung\n  ON (\n      probe.id = ortszuordnung.probe_id\n      AND ortszuordnung.ortszuordnung_typ = 'E'\n      )\nLEFT JOIN stammdaten.ort\n  ON (ortszuordnung.ort_id = ort.id)\nLEFT JOIN stammdaten.verwaltungseinheit\n  ON (ort.gem_id = verwaltungseinheit.id)\nWHERE (\n    probe.id_alt LIKE CASE \n      WHEN :probeIdFilter = ''\n        THEN '%'\n      ELSE :probeIdFilter\n      END\n    )\n  AND (\n    probe.hauptproben_nr LIKE CASE \n      WHEN :hpNrFilter = ''\n        THEN '%'\n      ELSE :hpNrFilter\n      END\n    )\n  AND (\n    stammdaten.mess_stelle.netzbetreiber_id = :netzIdFilter\n    OR '' = :netzIdFilter\n    )\n  AND (\n    probe.mst_id = :mstIdFilter\n    OR '' = :mstIdFilter\n    )\n  AND (\n    probe.umw_id = :umwIdFilter\n    OR '' = :umwIdFilter\n    )\n	Abfrage aller  Proben ohne Filter
9	Messprogramm pro Land	2	SELECT messprogramm.id,\n  messprogramm.id AS mpNr,\n  stammdaten.mess_stelle.netzbetreiber_id AS netzId,\n  CASE \n    WHEN messprogramm.mst_id = messprogramm.labor_mst_id\n      THEN messprogramm.mst_id\n    ELSE messprogramm.mst_id || '-' || messprogramm.labor_mst_id\n    END AS mstLaborId,\n  datenbasis.datenbasis AS dBasis,\n  CASE \n    WHEN messprogramm.ba_id = '1'\n      THEN 'RB'\n    ELSE 'IB'\n    END AS messRegime,\n  probenart.probenart AS pArt,\n  messprogramm.umw_id AS umwId,\n  messprogramm.media_desk AS deskriptoren,\n  messprogramm.probenintervall AS intervall,\n  ort.ort_id AS ortId,\n  ort.gem_id AS eGemId,\n  verwaltungseinheit.bezeichnung AS eGem\nFROM land.messprogramm\nLEFT JOIN stammdaten.mess_stelle\n  ON (messprogramm.mst_id = stammdaten.mess_stelle.id)\nLEFT JOIN stammdaten.datenbasis\n  ON (messprogramm.datenbasis_id = datenbasis.id)\nLEFT JOIN stammdaten.probenart\n  ON (messprogramm.probenart_id = probenart.id)\nLEFT JOIN land.ortszuordnung_mp\n  ON (\n      messprogramm.id = ortszuordnung_mp.messprogramm_id\n      AND ortszuordnung_mp.ortszuordnung_typ = 'E'\n      )\nLEFT JOIN stammdaten.ort\n  ON (ortszuordnung_mp.ort_id = ort.id)\nLEFT JOIN stammdaten.verwaltungseinheit\n  ON (ort.gem_id = verwaltungseinheit.id)\nWHERE (\n    mess_stelle.netzbetreiber_id = :netzIdFilter\n    OR '' = :netzIdFilter\n    )\n  AND (\n    messprogramm.umw_id SIMILAR TO (:umwIdFilter)\n    OR '' = :umwIdFilter\n    )	Abfrage der Messprogramme ohne Filter
\.


--
-- Data for Name: filter; Type: TABLE DATA; Schema: stammdaten; Owner: postgres
--

COPY filter (id, query_id, data_index, type, label, multiselect) FROM stdin;
8	7	netzIdFilter	2	Land	f
9	7	umwIdFilter	3	Umweltbereich	t
10	9	netzIdFilter	2	Land	f
14	14	netzbetreiberId	2	Land	t
15	13	statusFilter	4	Status	t
16	10	netzbetreiberId	2	Land	f
17	11	netzbetreiberId	2	Land	f
18	12	netzbetreiberId	2	Land	f
2	1	hpNrFilter	0	HP-Nr-Filter	f
1	1	probeIdFilter	0	Probe_id-Filter	f
3	1	netzIdFilter	2	Land	f
4	1	mstIdFilter	1	Messstelle	f
5	1	umwIdFilter	3	Umweltbereich	f
11	9	umwIdFilter	3	Umweltbereich	t
\.


--
-- Name: filter_id_seq; Type: SEQUENCE SET; Schema: stammdaten; Owner: postgres
--

SELECT pg_catalog.setval('filter_id_seq', 19, true);


--
-- Name: query_id_seq; Type: SEQUENCE SET; Schema: stammdaten; Owner: postgres
--

SELECT pg_catalog.setval('query_id_seq', 15, true);


--
-- Data for Name: result; Type: TABLE DATA; Schema: stammdaten; Owner: postgres
--

COPY result (id, query_id, data_index, header, width, flex, index) FROM stdin;
1	1	hpNr	Proben-Nr	100	f	0
2	1	dBasis	DB	50	f	1
3	1	netzId	Land	50	f	2
4	1	mstId	MST	60	f	3
5	1	umwId	Umw-ID	55	f	4
6	1	pArt	PA	50	f	5
7	1	peBegin	Entnahme_von	120	f	6
8	1	peEnd	Entnahme_bis	120	f	7
9	1	ortId	Ort_id	100	f	8
10	1	eGemId	E-Gem Id	80	f	9
11	1	eGem	E-Gemeinde	200	f	10
12	1	probeId	Probe_Id	100	f	11
128	9	mpNr	MPR-ID	70	f	0
90	9	deskriptoren	Deskriptoren	220	f	7
84	9	netzId	Land	50	f	1
61	7	hpNr	Proben-Nr	100	f	0
64	7	dBasis	DB	50	f	1
66	7	netzId	Land	50	f	2
67	7	mstId	MST	60	f	3
70	7	umwId	Umw-ID	55	f	4
71	7	pArt	PA	50	f	5
73	7	peBegin	Entnahme_von	120	f	6
76	7	peEnd	Entnahme_bis	120	f	7
77	7	ortId	Ort_id	100	f	8
80	7	eGemId	E-Gem Id	80	f	9
81	7	eGem	E-Gemeinde	200	f	10
82	7	probeId	Probe Id	100	f	11
85	9	mstLaborId	MST/Labor	80	f	2
86	9	dBasis	DB	50	f	3
87	9	messRegime	MR	50	f	4
88	9	pArt	PA	50	f	5
89	9	umwId	Umw-ID	50	f	6
91	9	intervall	PI	50	f	8
92	9	ortId	Ort_id	100	f	9
93	9	eGemId	Gem-Id	80	f	10
94	9	eGem	Gemeinde	200	f	11
95	13	hpNr	Proben-Nr	100	f	1
96	13	npNr	NP-Nr	50	f	2
97	13	statusD	Status-Zeit	120	f	3
98	13	statusSt	Stufe	40	f	4
99	13	statusW	Status	100	f	5
100	13	dBasis	DB	50	f	6
101	13	netzId	Land	50	f	7
102	13	mstId	MST	60	f	8
103	13	umwId	Umw-ID	55	f	9
104	13	pArt	PA	50	f	10
105	13	peBegin	Entnahme_von	120	f	11
106	13	peEnd	Entnahme_bis	120	f	12
107	13	ortId	Ort_id	100	f	13
108	13	eGemId	E-Gem Id	80	f	14
109	13	eGem	E-Gemeinde	200	f	15
110	13	probeId	Probe_Id	100	f	0
111	15	hpNr	Proben-Nr	100	f	1
112	15	npNr	NP-Nr	50	f	2
113	15	statusD	Status-Zeit	120	f	3
114	15	statusSt	Stufe	40	f	4
115	15	statusW	Status	100	f	5
116	15	dBasis	DB	50	f	6
117	15	netzId	Land	50	f	7
118	15	mstId	MST	60	f	8
119	15	umwId	Umw-ID	55	f	9
120	15	pArt	PA	50	f	10
121	15	peBegin	Entnahme_von	120	f	11
122	15	peEnd	Entnahme_bis	120	f	12
123	15	ortId	Ort_id	100	f	13
124	15	eGemId	E-Gem Id	80	f	14
125	15	eGem	E-Gemeinde	200	f	15
126	15	probeId	Probe_Id	100	f	0
\.


--
-- Name: result_id_seq; Type: SEQUENCE SET; Schema: stammdaten; Owner: postgres
--

SELECT pg_catalog.setval('result_id_seq', 128, true);


--
-- PostgreSQL database dump complete
--

