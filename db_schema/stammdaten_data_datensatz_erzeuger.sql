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
-- TOC entry 4676 (class 0 OID 535694)
-- Dependencies: 239
-- Data for Name: datensatz_erzeuger; Type: TABLE DATA; Schema: stammdaten; Owner: lada
--

COPY datensatz_erzeuger (id, netzbetreiber_id, datensatz_erzeuger_id, mst_id, bezeichnung, letzte_aenderung) FROM stdin;
1	06	KS	06010	Messstelle HLUG Kassel	2000-01-01 00:00:00
2	06	DA	06060	Messstelle HLUG Darmstadt	2000-01-01 00:00:00
\.
