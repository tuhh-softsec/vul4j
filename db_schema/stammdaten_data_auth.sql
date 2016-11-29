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
-- TOC entry 4670 (class 0 OID 535676)
-- Dependencies: 232
-- Data for Name: auth; Type: TABLE DATA; Schema: stammdaten; Owner: lada
--

COPY auth (id, ldap_group, netzbetreiber_id, mst_id, labor_mst_id, funktion_id) FROM stdin;
1	Imis_world	\N	\N	\N	0
2	mst_06010	06	06010	06010	0
3	mst_06060	06	06060	06060	0
4	mst_11010	11	11010	11010	0
5	mst_12010	12	12010	12010	0
6	mst_12020	12	12020	12020	0
7	mst_06_status	06	06010	06010	1
8	mst_06-status	06	06060	06060	1
9	mst_11010_status	11	11010	11010	1
10	mst_12010_status	12	12010	12010	1
11	mst_12020_status	12	12020	12020	1
16	land_06_status	06	06112	\N	2
17	land_11_status	11	11042	\N	2
21	land_12_status	12	12032	\N	2
22	lst_kiel	\N	20050	\N	3
23	lst_fisch	\N	20040	\N	3
24	land_06_stamm	06	\N	\N	4
\.
