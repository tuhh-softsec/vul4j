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
-- TOC entry 4689 (class 0 OID 535738)
-- Dependencies: 254
-- Data for Name: lada_user; Type: TABLE DATA; Schema: stammdaten; Owner: lada
--

COPY lada_user (id, name) FROM stdin;
0	default
\.


--
-- TOC entry 4681 (class 0 OID 535715)
-- Dependencies: 246
-- Data for Name: favorite; Type: TABLE DATA; Schema: stammdaten; Owner: lada
--

COPY favorite (id, user_id, query_id) FROM stdin;
\.


--
-- TOC entry 4685 (class 0 OID 535725)
-- Dependencies: 250
-- Data for Name: filter_value; Type: TABLE DATA; Schema: stammdaten; Owner: lada
--

COPY filter_value (id, user_id, filter_id, value) FROM stdin;
\.
