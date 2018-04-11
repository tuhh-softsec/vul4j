--
-- PostgreSQL database dump
--

-- Dumped from database version 10.1
-- Dumped by pg_dump version 10.1

-- Started on 2018-01-22 13:18:10 CET

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

SET search_path = stamm, pg_catalog;

--
-- TOC entry 5408 (class 0 OID 3061575)
-- Dependencies: 246
-- Data for Name: query; Type: TABLE DATA; Schema: stamm; Owner: postgres
--

COPY query (id, name, groups, owner, sql, description) FROM stdin;
1	Proben	\N	\N	SELECT probe.id AS probeId, probe.hauptproben_nr AS hauptprobenNr, probe.id_alt AS idAlt, datenbasis.datenbasis AS dBasis, stamm.mess_stelle.netzbetreiber_id AS netzId, probe.mst_id AS mstId, probe.umw_id AS umwId, probenart.probenart AS pArt, probe.probeentnahme_beginn AS peBegin, probe.probeentnahme_ende AS peEnd, ort.ort_id AS ortId, ort.gem_id AS eGemId, verwaltungseinheit.bezeichnung AS eGem, probe.id_alt AS idAlt FROM land.probe LEFT JOIN stamm.mess_stelle ON (probe.mst_id = stamm.mess_stelle.id) LEFT JOIN stamm.datenbasis ON (probe.datenbasis_id = datenbasis.id) LEFT JOIN stamm.probenart ON (probe.probenart_id = probenart.id) LEFT JOIN land.ortszuordnung ON (probe.id = ortszuordnung.probe_id AND ortszuordnung.ortszuordnung_typ = 'E') LEFT JOIN stamm.ort ON (ortszuordnung.ort_id = ort.id) LEFT JOIN stamm.verwaltungseinheit ON (ort.gem_id = verwaltungseinheit.id)	Proben mit Ort
\.


COPY filter (id, sql, parameter, type, name) FROM stdin;
1	probe.id_alt LIKE :idAlt	idAlt	0	probe_id_alt
2	probe.hauptproben_nr LIKE :hauptprobenNr	hauptprobenNr	0	probe_hauptproben_nr
3	probe.mst_id = :mstId	mstId	5	probe_mst_id
4	probe.umw_id = :umwId	umwId	9	probe_umw_id
5	probe.test = cast(:test AS boolean)	test	2	probe_test
6	probe.probeentnahme_beginn >= to_timestamp(cast(:timeBegin AS double_precision))	timeBegin	3	probe_entnahme_beginn
7	probe.probeentnahme_ende <= to_timestamp(cast(:timeEnd AS double_precision))	timeEnd	3	probe_entnahme_beginn
8	datenbasis.datenbasis = :datenbasis	datenbasis	0	datenbasis
9	probenart.probenart = :probenart	probenart	0	probenart
10	ort.gem_id = :gemId	gemId	0	ort_gem_id
11	ort.ort_id = :ortId	ortId	0	ort_ort_id
12	verwaltungseinheit.bezeichnung LIKE :bezeichnung	bezeichnung	0	verwaltungseinheit_bezeichnung
13	stamm.mess_stelle.netzbetreiber_id = :netzbetreiberId	netzbetreiberId	7	netzbetreiber_id
\.

--
-- TOC entry 5410 (class 0 OID 3061619)
-- Dependencies: 252
-- Data for Name: filter; Type: TABLE DATA; Schema: stamm; Owner: postgres
--

--
-- TOC entry 5412 (class 0 OID 3061892)
-- Dependencies: 284
-- Data for Name: result_type; Type: TABLE DATA; Schema: stamm; Owner: postgres
--

COPY result_type (id, name, format) FROM stdin;
1	text	\N
2	date	d.m.Y H:i
3	number	\N
4	probeId	\N
5	messungId	\N
6	ortId	\N
7	geom	\N
\.


--
-- TOC entry 5414 (class 0 OID 3061900)
-- Dependencies: 286
-- Data for Name: result; Type: TABLE DATA; Schema: stamm; Owner: postgres
--

COPY grid_column (id, query, name, data_index, sort, filter, filter_value, filter_active, visible, data_type, column_index, width) FROM stdin;
1	1	Id	probeId	\N	\N	\N	f	f	3	\N	\N
2	1	Hauptproben Nummer	hauptprobenNr	\N	2	\N	f	f	1	0	100
3	1	Datenbasis	dBasis	\N	8	\N	f	f	1	1	100
4	1	Land	netzId	\N	13	\N	f	f	1	2	100
5	1	Messstelle	mstId	\N	3	\N	f	f	1	3	100
6	1	Umweltbereich	umwId	\N	9	\N	f	f	1	4	100
7	1	Probenart	pArt	\N	1	\N	f	f	1	5	100
8	1	Entnahme von	peBegin	\N	3	\N	f	f	2	6	150
9	1	Entnahme bis	peEnd	\N	3	\N	f	f	2	7	150
10	1	Ort	ortId	\N	1	\N	f	f	1	8	100
11	1	Gemeinde Id	eGemId	\N	1	\N	f	f	1	9	100
12	1	Gemeinde	eGem	\N	1	\N	f	f	1	10	150
13	1	Probennummer	idAlt	\N	1	\N	f	f	1	11	100
\.


--
-- TOC entry 5419 (class 0 OID 0)
-- Dependencies: 251
-- Name: filter_id_seq; Type: SEQUENCE SET; Schema: stamm; Owner: postgres
--

SELECT pg_catalog.setval('filter_id_seq', 26, true);


--
-- TOC entry 5420 (class 0 OID 0)
-- Dependencies: 245
-- Name: query_id_seq; Type: SEQUENCE SET; Schema: stamm; Owner: postgres
--

SELECT pg_catalog.setval('query_id_seq', 18, true);


--
-- TOC entry 5421 (class 0 OID 0)
-- Dependencies: 285
-- Name: result_id_seq; Type: SEQUENCE SET; Schema: stamm; Owner: postgres
--

SELECT pg_catalog.setval('result_id_seq', 291, true);


--
-- TOC entry 5422 (class 0 OID 0)
-- Dependencies: 283
-- Name: result_type_id_seq; Type: SEQUENCE SET; Schema: stamm; Owner: postgres
--

SELECT pg_catalog.setval('result_type_id_seq', 1, false);


-- Completed on 2018-01-22 13:18:15 CET

--
-- PostgreSQL database dump complete
--

