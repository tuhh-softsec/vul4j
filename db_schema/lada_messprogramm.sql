\set ON_ERROR_STOP on

--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.2
-- Dumped by pg_dump version 9.5.2

-- Started on 2016-05-04 09:10:49 CEST

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = land, pg_catalog;

--
-- TOC entry 4684 (class 0 OID 1170769)
-- Dependencies: 286
-- Data for Name: messprogramm; Type: TABLE DATA; Schema: land; Owner: postgres
--

ALTER TABLE messprogramm DISABLE TRIGGER ALL;

COPY messprogramm (id, test, aktiv, mst_id, labor_mst_id, datenbasis_id, ba_id, gem_id, media_desk, mpl_Id, umw_id, probenart_id, probenintervall, teilintervall_von, teilintervall_bis, intervall_offset, gueltig_von, gueltig_bis, probe_nehmer_id, probe_kommentar, letzte_aenderung) FROM stdin;
\.


ALTER TABLE messprogramm ENABLE TRIGGER ALL;

--
-- TOC entry 4701 (class 0 OID 0)
-- Dependencies: 285
-- Name: messprogramm_id_seq; Type: SEQUENCE SET; Schema: land; Owner: postgres
--

SELECT pg_catalog.setval('messprogramm_id_seq', 1, false);


--
-- TOC entry 4685 (class 0 OID 1170813)
-- Dependencies: 287
-- Data for Name: messprogramm_mmt; Type: TABLE DATA; Schema: land; Owner: postgres
--

ALTER TABLE messprogramm_mmt DISABLE TRIGGER ALL;

COPY messprogramm_mmt (id, messprogramm_id, mmt_id, messgroessen, letzte_aenderung) FROM stdin;
\.


ALTER TABLE messprogramm_mmt ENABLE TRIGGER ALL;

--
-- TOC entry 4702 (class 0 OID 0)
-- Dependencies: 288
-- Name: messprogramm_mmt_id_seq; Type: SEQUENCE SET; Schema: land; Owner: postgres
--

SELECT pg_catalog.setval('messprogramm_mmt_id_seq', 1, false);

