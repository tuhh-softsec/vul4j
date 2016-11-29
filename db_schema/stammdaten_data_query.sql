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
-- TOC entry 4715 (class 0 OID 535823)
-- Dependencies: 281
-- Data for Name: query; Type: TABLE DATA; Schema: stammdaten; Owner: lada
--

COPY query (id, name, type, sql, description) FROM stdin;
10	Orte	3		Abfrage der Orte
11	Probenehmer	4		Abfrage der Probenehmer
12	Datensatzerzeuger	5		Abfrage der Datensatzerzeuger
14	Messprogrammkategorie	6		Abfrage der Messprogrammkategorien
9	kein Filter	2	SELECT messprogramm.id, stammdaten.mess_stelle.netzbetreiber_id AS netzId, CASE WHEN messprogramm.mst_id = messprogramm.labor_mst_id THEN messprogramm.mst_id ELSE messprogramm.mst_id || '-' || messprogramm.labor_mst_id END AS mstLaborId, datenbasis.datenbasis AS dBasis, CASE WHEN messprogramm.ba_id = '1' THEN 'RB' ELSE 'IB' END AS messRegime, probenart.probenart AS pArt, messprogramm.umw_id AS umwId, messprogramm.media_desk AS deskriptoren, messprogramm.probenintervall AS intervall, ort.ort_id AS ortId, ort.gem_id AS eGemId, verwaltungseinheit.bezeichnung AS eGem FROM land.messprogramm LEFT JOIN stammdaten.mess_stelle ON (messprogramm.mst_id = stammdaten.mess_stelle.id) LEFT JOIN stammdaten.datenbasis ON (messprogramm.datenbasis_id = datenbasis.id) LEFT JOIN stammdaten.probenart ON (messprogramm.probenart_id = probenart.id) LEFT JOIN stammdaten.ort ON messprogramm.ort_id = ort.id LEFT JOIN stammdaten.verwaltungseinheit ON (ort.gem_id = verwaltungseinheit.id) 	Abfrage der Messprogramme ohne Filter
1	kein Filter	0	SELECT probe.id AS id, probe.hauptproben_nr AS hpNr, datenbasis.datenbasis AS dBasis, stammdaten.mess_stelle.netzbetreiber_id AS netzId, probe.mst_id AS mstId, probe.umw_id AS umwId, probenart.probenart AS pArt, to_char(probe.probeentnahme_beginn, 'dd.mm.YYYY hh24:MI') AS peBegin, to_char(probe.probeentnahme_ende, 'dd.mm.YYYY hh24:MI') AS peEnd, ort.ort_id AS ortId, ort.gem_id AS eGemId, verwaltungseinheit.bezeichnung AS eGem, probe.id_alt AS probeId FROM land.probe LEFT JOIN stammdaten.mess_stelle ON (probe.mst_id = stammdaten.mess_stelle.id) LEFT JOIN stammdaten.datenbasis ON (probe.datenbasis_id = datenbasis.id) LEFT JOIN stammdaten.probenart ON (probe.probenart_id = probenart.id) LEFT OUTER JOIN land.ortszuordnung ON ( probe.id = ortszuordnung.probe_id AND ortszuordnung.ortszuordnung_typ = 'E' ) LEFT OUTER JOIN stammdaten.ort ON (ortszuordnung.ort_id = ort.id) LEFT OUTER JOIN stammdaten.verwaltungseinheit ON (ort.gem_id = verwaltungseinheit.id)	Abfrage aller  Proben ohne Filter
2	HP-Nr-Filter	0	SELECT probe.id AS id, probe.hauptproben_nr AS hpNr, datenbasis.datenbasis AS dBasis, stammdaten.mess_stelle.netzbetreiber_id AS netzId, probe.mst_id AS mstId, probe.umw_id AS umwId, probenart.probenart AS pArt, to_char(probe.probeentnahme_beginn, 'dd.mm.YYYY hh24:MI') AS peBegin, to_char(probe.probeentnahme_ende, 'dd.mm.YYYY hh24:MI') AS peEnd, ort.ort_id AS ortId, ort.gem_id AS eGemId, verwaltungseinheit.bezeichnung AS eGem, probe.id_alt AS probeId FROM land.probe LEFT JOIN stammdaten.mess_stelle ON (probe.mst_id = stammdaten.mess_stelle.id) LEFT JOIN stammdaten.datenbasis ON (probe.datenbasis_id = datenbasis.id) LEFT JOIN stammdaten.probenart ON (probe.probenart_id = probenart.id) LEFT OUTER JOIN land.ortszuordnung ON ( probe.id = ortszuordnung.probe_id AND ortszuordnung.ortszuordnung_typ = 'E' ) LEFT OUTER JOIN stammdaten.ort ON (ortszuordnung.ort_id = ort.id) LEFT OUTER JOIN stammdaten.verwaltungseinheit ON (ort.gem_id = verwaltungseinheit.id) WHERE (probe.hauptproben_nr LIKE CASE WHEN :hpNrFilter = '' THEN '%' ELSE :hpNrFilter END)	Abfrage der Proben mit Wildcardfilter für die Hauptproben-Nr.
3	Probe-ID-Filter	0	SELECT probe.id AS id, probe.hauptproben_nr AS hpNr, datenbasis.datenbasis AS dBasis, stammdaten.mess_stelle.netzbetreiber_id AS netzId, probe.mst_id AS mstId, probe.umw_id AS umwId, probenart.probenart AS pArt, to_char(probe.probeentnahme_beginn, 'dd.mm.YYYY hh24:MI') AS peBegin, to_char(probe.probeentnahme_ende, 'dd.mm.YYYY hh24:MI') AS peEnd, ort.ort_id AS ortId, ort.gem_id AS eGemId, verwaltungseinheit.bezeichnung AS eGem, probe.id_alt AS probeId FROM land.probe LEFT JOIN stammdaten.datenbasis ON (probe.datenbasis_id = datenbasis.id) LEFT JOIN stammdaten.probenart ON (probe.probenart_id = probenart.id) LEFT JOIN stammdaten.mess_stelle ON (probe.mst_id = stammdaten.mess_stelle.id) LEFT OUTER JOIN land.ortszuordnung ON ( probe.id = ortszuordnung.probe_id AND ortszuordnung.ortszuordnung_typ = 'E' ) LEFT OUTER JOIN stammdaten.ort ON (ortszuordnung.ort_id = ort.id) LEFT OUTER JOIN stammdaten.verwaltungseinheit ON (ort.gem_id = verwaltungseinheit.id) WHERE (probe.id_alt LIKE CASE WHEN :probeIdFilter = '' THEN '%' ELSE :probeIdFilter END)	Abfrage der Proben mit Wildcardfilter für die Probe_id
4	MST und UMW	0	SELECT probe.id AS id, probe.hauptproben_nr AS hpNr, datenbasis.datenbasis AS dBasis, stammdaten.mess_stelle.netzbetreiber_id AS netzId, probe.mst_id AS mstId, probe.umw_id AS umwId, probenart.probenart AS pArt, to_char(probe.probeentnahme_beginn, 'dd.mm.YYYY hh24:MI') AS peBegin, to_char(probe.probeentnahme_ende, 'dd.mm.YYYY hh24:MI') AS peEnd, ort.ort_id AS ortId, ort.gem_id AS eGemId, verwaltungseinheit.bezeichnung AS eGem, probe.id_alt AS probeId FROM land.probe LEFT JOIN stammdaten.mess_stelle ON (probe.mst_id = stammdaten.mess_stelle.id) LEFT JOIN stammdaten.datenbasis ON (probe.datenbasis_id = datenbasis.id) LEFT JOIN stammdaten.probenart ON (probe.probenart_id = probenart.id) LEFT OUTER JOIN land.ortszuordnung ON ( probe.id = ortszuordnung.probe_id AND ortszuordnung.ortszuordnung_typ = 'E' ) LEFT OUTER JOIN stammdaten.ort ON (ortszuordnung.ort_id = ort.id) LEFT OUTER JOIN stammdaten.verwaltungseinheit ON (ort.gem_id = verwaltungseinheit.id) WHERE (probe.mst_id = :mstIdFilter OR '' = :mstIdFilter) AND (probe.umw_id = :umwIdFilter OR '' = :umwIdFilter)	Abfrage der Proben gefiltert nach Messtellen ID und ID des Umweltbereichs
5	Proben pro Land	0	SELECT probe.id AS id, probe.hauptproben_nr AS hpNr, datenbasis.datenbasis AS dBasis, stammdaten.mess_stelle.netzbetreiber_id AS netzId, probe.mst_id AS mstId, probe.umw_id AS umwId, probenart.probenart AS pArt, to_char(probe.probeentnahme_beginn, 'dd.mm.YYYY hh24:MI') AS peBegin, to_char(probe.probeentnahme_ende, 'dd.mm.YYYY hh24:MI') AS peEnd, ort.ort_id AS ortId, ort.gem_id AS eGemId, verwaltungseinheit.bezeichnung AS eGem, probe.id_alt AS probeId FROM land.probe LEFT JOIN stammdaten.mess_stelle ON (probe.mst_id = stammdaten.mess_stelle.id) LEFT JOIN stammdaten.datenbasis ON (probe.datenbasis_id = datenbasis.id) LEFT JOIN stammdaten.probenart ON (probe.probenart_id = probenart.id) LEFT OUTER JOIN land.ortszuordnung ON ( probe.id = ortszuordnung.probe_id AND ortszuordnung.ortszuordnung_typ = 'E' ) LEFT OUTER JOIN stammdaten.ort ON (ortszuordnung.ort_id = ort.id) LEFT OUTER JOIN stammdaten.verwaltungseinheit ON (ort.gem_id = verwaltungseinheit.id) WHERE (stammdaten.mess_stelle.netzbetreiber_id = :netzIdFilter OR '' = :netzIdFilter)	Proben gefiltert nach Ländern
7	Proben pro Land und UMW (Multiselect)	0	SELECT probe.id AS id, probe.hauptproben_nr AS hpNr, datenbasis.datenbasis AS dBasis, stammdaten.mess_stelle.netzbetreiber_id AS netzId, probe.mst_id AS mstId, probe.umw_id AS umwId, probenart.probenart AS pArt, to_char(probe.probeentnahme_beginn, 'dd.mm.YYYY hh24:MI') AS peBegin, to_char(probe.probeentnahme_ende, 'dd.mm.YYYY hh24:MI') AS peEnd, ort.ort_id AS ortId, ort.gem_id AS eGemId, verwaltungseinheit.bezeichnung AS eGem, probe.id_alt AS probeId FROM land.probe LEFT JOIN stammdaten.mess_stelle ON (probe.mst_id = stammdaten.mess_stelle.id) LEFT JOIN stammdaten.datenbasis ON (probe.datenbasis_id = datenbasis.id) LEFT JOIN stammdaten.probenart ON (probe.probenart_id = probenart.id) LEFT OUTER JOIN land.ortszuordnung ON ( probe.id = ortszuordnung.probe_id AND ortszuordnung.ortszuordnung_typ = 'E' ) LEFT OUTER JOIN stammdaten.ort ON (ortszuordnung.ort_id = ort.id) LEFT OUTER JOIN stammdaten.verwaltungseinheit ON (ort.gem_id = verwaltungseinheit.id) WHERE (stammdaten.mess_stelle.netzbetreiber_id = :netzIdFilter OR '' =:netzIdFilter) AND (probe.umw_id SIMILAR TO (:umwIdFilter) OR '' = :umwIdFilter)	Abfrage aller Proben gefiltert pro Land und Umweltbereich (mit Mehrfachauswahl)
15	kein Filter	1	SELECT messung.id, probe.id AS probeId, probe.hauptproben_nr AS hpNr, messung.nebenproben_nr AS npNr, to_char(status_protokoll.datum, 'dd.mm.YYYY hh24:MI') AS statusD, stammdaten.status_stufe.stufe AS statusSt, stammdaten.status_wert.wert AS statusW, datenbasis.datenbasis AS dBasis, stammdaten.mess_stelle.netzbetreiber_id AS netzId, probe.mst_id AS mstId, probe.umw_id AS umwId, probenart.probenart AS pArt, to_char(probe.probeentnahme_beginn, 'dd.mm.YYYY hh24:MI') AS peBegin, to_char(probe.probeentnahme_ende, 'dd.mm.YYYY hh24:MI') AS peEnd, ort.ort_id AS ortId, ort.gem_id AS eGemId, verwaltungseinheit.bezeichnung AS eGem FROM land.probe LEFT JOIN stammdaten.mess_stelle ON (probe.mst_id = stammdaten.mess_stelle.id) INNER JOIN land.messung ON probe.id = messung.probe_id INNER JOIN land.status_protokoll ON messung.STATUS = status_protokoll.id LEFT JOIN stammdaten.status_kombi ON status_protokoll.status_kombi = stammdaten.status_kombi.id LEFT JOIN stammdaten.status_wert ON stammdaten.status_wert.id = stammdaten.status_kombi.wert_id LEFT JOIN stammdaten.status_stufe ON stammdaten.status_stufe.id = stammdaten.status_kombi.stufe_id LEFT JOIN stammdaten.datenbasis ON (probe.datenbasis_id = datenbasis.id) LEFT JOIN stammdaten.probenart ON (probe.probenart_id = probenart.id) LEFT JOIN land.ortszuordnung ON ( probe.id = ortszuordnung.probe_id AND ortszuordnung.ortszuordnung_typ = 'E' ) LEFT JOIN stammdaten.ort ON (ortszuordnung.ort_id = ort.id) LEFT JOIN stammdaten.verwaltungseinheit ON (ort.gem_id = verwaltungseinheit.id) 	kein Filter
13	nach Status	1	SELECT messung.id, probe.id AS probeId, probe.hauptproben_nr AS hpNr, messung.nebenproben_nr AS npNr, to_char(status_protokoll.datum, 'dd.mm.YYYY hh24:MI') AS statusD, stammdaten.status_stufe.stufe AS statusSt, stammdaten.status_wert.wert AS statusW, datenbasis.datenbasis AS dBasis, stammdaten.mess_stelle.netzbetreiber_id AS netzId, probe.mst_id AS mstId, probe.umw_id AS umwId, probenart.probenart AS pArt, to_char(probe.probeentnahme_beginn, 'dd.mm.YYYY hh24:MI') AS peBegin, to_char(probe.probeentnahme_ende, 'dd.mm.YYYY hh24:MI') AS peEnd, ort.ort_id AS ortId, ort.gem_id AS eGemId, verwaltungseinheit.bezeichnung AS eGem FROM land.probe LEFT JOIN stammdaten.mess_stelle ON (probe.mst_id = stammdaten.mess_stelle.id) INNER JOIN land.messung ON probe.id = messung.probe_id INNER JOIN land.status_protokoll ON messung.STATUS = status_protokoll.id LEFT JOIN stammdaten.status_kombi ON status_protokoll.status_kombi = stammdaten.status_kombi.id LEFT JOIN stammdaten.status_wert ON stammdaten.status_wert.id = stammdaten.status_kombi.wert_id LEFT JOIN stammdaten.status_stufe ON stammdaten.status_stufe.id = stammdaten.status_kombi.stufe_id LEFT JOIN stammdaten.datenbasis ON (probe.datenbasis_id = datenbasis.id) LEFT JOIN stammdaten.probenart ON (probe.probenart_id = probenart.id) LEFT JOIN land.ortszuordnung ON ( probe.id = ortszuordnung.probe_id AND ortszuordnung.ortszuordnung_typ = 'E' ) LEFT JOIN stammdaten.ort ON (ortszuordnung.ort_id = ort.id) LEFT JOIN stammdaten.verwaltungseinheit ON (ort.gem_id = verwaltungseinheit.id) WHERE CAST(stammdaten.status_wert.id AS text) SIMILAR TO CASE WHEN :statusFilter = '' THEN '%' ELSE :statusFilter END	Messungen nach Status
\.


--
-- TOC entry 4683 (class 0 OID 535720)
-- Dependencies: 248
-- Data for Name: filter; Type: TABLE DATA; Schema: stammdaten; Owner: lada
--

COPY filter (id, query_id, data_index, type, label, multiselect) FROM stdin;
2	2	hpNrFilter	0	HP-Nr-Filter	f
3	3	probeIdFilter	0	Probe_id-Filter	f
5	4	mstIdFilter	1	Messstelle	f
6	4	umwIdFilter	3	Umweltbereich	f
7	5	netzIdFilter	2	Land	f
9	7	netzIdFilter	2	Land	f
10	7	umwIdFilter	3	Umweltbereich	t
14	14	netzbetreiberId	2	Land	t
15	13	statusFilter	4	Status	t
16	10	netzbetreiberId	2	Land	f
17	11	netzbetreiberId	2	Land	f
18	12	netzbetreiberId	2	Land	f
\.


--
-- TOC entry 4717 (class 0 OID 535831)
-- Dependencies: 283
-- Data for Name: result; Type: TABLE DATA; Schema: stammdaten; Owner: lada
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
13	2	hpNr	Proben-Nr	100	f	0
14	2	dBasis	DB	50	f	1
15	2	netzId	Land	50	f	2
16	2	mstId	MST	60	f	3
17	2	umwId	Umw-ID	55	f	4
18	2	pArt	PA	50	f	5
19	2	peBegin	Entnahme_von	120	f	6
20	2	peEnd	Entnahme_bis	120	f	7
21	2	ortId	Ort_id	100	f	8
22	2	eGemId	E-Gem Id	80	f	9
23	2	eGem	E-Gemeinde	200	f	10
24	2	probeId	Probe Id	100	f	11
25	3	hpNr	Proben-Nr	100	f	0
26	3	dBasis	DB	50	f	1
27	3	netzId	Land	50	f	2
28	3	mstId	MST	60	f	3
29	3	umwId	Umw-ID	55	f	4
30	3	pArt	PA	50	f	5
31	3	peBegin	Entnahme_von	120	f	6
32	3	peEnd	Entnahme_bis	120	f	7
33	3	ortId	Ort_id	100	f	8
34	3	eGemId	E-Gem Id	80	f	9
35	3	eGem	E-Gemeinde	200	f	10
36	3	probeId	Probe Id	100	f	11
37	4	hpNr	Proben-Nr	100	f	0
38	4	dBasis	DB	50	f	1
39	4	netzId	Land	50	f	2
40	4	mstId	MST	60	f	3
41	4	umwId	Umw-ID	55	f	4
42	4	pArt	PA	50	f	5
43	4	peBegin	Entnahme_von	120	f	6
44	4	peEnd	Entnahme_bis	120	f	7
45	4	ortId	Ort_id	100	f	8
46	4	eGemId	E-Gem Id	80	f	9
47	4	eGem	E-Gemeinde	200	f	10
48	4	probeId	Probe Id	100	f	11
49	5	hpNr	Proben-Nr	100	f	0
50	5	dBasis	DB	50	f	1
51	5	netzId	Land	50	f	2
52	5	mstId	MST	60	f	3
53	5	umwId	Umw-ID	55	f	4
54	5	pArt	PA	50	f	5
55	5	peBegin	Entnahme_von	120	f	6
56	5	peEnd	Entnahme_bis	120	f	7
57	5	ortId	Ort_id	100	f	8
58	5	eGemId	E-Gem Id	80	f	9
59	5	eGem	E-Gemeinde	200	f	10
60	5	probeId	Probe Id	100	f	11
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
84	9	netzId	Land	100	f	1
85	9	mstLaborId	MST/Labor	80	f	2
86	9	dBasis	DB	50	f	3
87	9	messRegime	MR	50	f	4
88	9	pArt	PA	50	f	5
89	9	umwId	Umw-ID	50	f	6
90	9	deskriptoren	Deskriptoren	120	f	7
91	9	intervall	PI	50	f	8
92	9	ortId	Ort-Id	100	f	9
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

