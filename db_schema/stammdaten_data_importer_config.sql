--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.2
-- Dumped by pg_dump version 10.1

-- Started on 2017-11-23 10:33:56 CET

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
-- TOC entry 5304 (class 0 OID 17026474)
-- Dependencies: 314
-- Data for Name: importer_config; Type: TABLE DATA; Schema: stammdaten; Owner: postgres
--

COPY importer_config (id, name, attribute, mst_id, from_value, to_value, action) FROM stdin;
1	messwert	messEinheit	06010	BQ	Bq	convert
2	messwert	messEinheit	06010	BQ/KG	Bq/kg	convert
3	messwert	messEinheit	06010	Bq/kgFM	Bq/kg(FM)	convert
4	messwert	messEinheit	06010	BQ/kgFM	Bq/kg(FM)	convert
5	messwert	messEinheit	06010	Bq/KGFM	Bq/kg(FM)	convert
6	messwert	messEinheit	06010	Bq/kgGR	Bq/kg(GR)	convert
7	messwert	messEinheit	06010	BQ/KGGR	Bq/kg(GR)	convert
8	messwert	messEinheit	06010	Bq/kgTM	Bq/kg(TM)	convert
9	messwert	messEinheit	06010	BQ/KGTM	Bq/kg(TM)	convert
10	messwert	messEinheit	06010	Bq/m2	Bq/m²	convert
11	messwert	messEinheit	06010	Bq/m2	Bq/m³	convert
12	messwert	messEinheit	06010	KBQ/Kg	kBq/kg	convert
13	messwert	messEinheit	06010	KBQ/KG	kBq/kg	convert
14	messwert	messEinheit	06010	KBQ/Kgtm	kBq/kg(TM)	convert
15	probe	mstId	06010	\N	06010	default
16	messwert	messgroesse	06010	20	2d	transform
17	probe	mediaDesk	06010	2d	30	transform
18	probe	mstId	06060	\N	06060	default
19	messwert	messgroesse	06060	20	2d	transform
20	messwert	messEinheit	11010	Bq/d*P	Bq/(d*p)	convert
21	messwert	messEinheit	11010	Bq/ d*P	Bq/(d*p)	convert
22	messwert	messEinheit	11010	Bq/ (d*P)	Bq/(d*p)	convert
23	messwert	messEinheit	11010	Bq/(d*P)	Bq/(d*p)	convert
24	probe	mstId	11010	\N	11010	default
25	messwert	messgroesse	11010	20	2d	transform
26	probe	mstId	12010	\N	12010	default
27	messwert	messgroesse	12010	20	2d	transform
28	probe	mstId	12020	\N	12020	default
29	messwert	messgroesse	12020	20	2d	transform
\.


--
-- TOC entry 5309 (class 0 OID 0)
-- Dependencies: 313
-- Name: importer_config_id_seq; Type: SEQUENCE SET; Schema: stammdaten; Owner: postgres
--

SELECT pg_catalog.setval('importer_config_id_seq', (SELECT max(id) FROM importer_config), true);


-- Completed on 2017-11-23 10:33:59 CET

--
-- PostgreSQL database dump complete
--

