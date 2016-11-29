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
-- TOC entry 4674 (class 0 OID 535689)
-- Dependencies: 237
-- Data for Name: datenbasis; Type: TABLE DATA; Schema: stammdaten; Owner: lada
--

COPY datenbasis (id, beschreibung, datenbasis) FROM stdin;
9	Europa	Europa
13	SPARSE NETWORK	SPARSE
14	DENSE NETWORK	DENSE
1	§2-Daten	§2
2	§3-Daten	§3
3	REI-Emissionsdaten	REI-E
4	REI-Immissionsdaten	REI-I
5	§7-Daten	§7
6	KFÜ	KFÜ
7	Landesdaten	Land
\.
