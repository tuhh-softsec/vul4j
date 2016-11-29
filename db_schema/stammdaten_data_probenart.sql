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
-- TOC entry 4711 (class 0 OID 535813)
-- Dependencies: 277
-- Data for Name: probenart; Type: TABLE DATA; Schema: stammdaten; Owner: lada
--

COPY probenart (id, beschreibung, probenart, probenart_eudf_id) FROM stdin;
1	Einzelprobe	E	A
2	Mischprobe	M	A
3	Sammelprobe	S	A
9	kontinuierliche Probe	X	A
\.
