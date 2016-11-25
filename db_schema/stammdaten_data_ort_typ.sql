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
-- TOC entry 4706 (class 0 OID 535799)
-- Dependencies: 272
-- Data for Name: ort_typ; Type: TABLE DATA; Schema: stammdaten; Owner: lada
--

COPY ort_typ (id, ort_typ) FROM stdin;
1	dynamischer Messpunkt (nicht vordefiniert)
2	vordefinierter Messpunkt
3	REI-Messpunkit
4	Fluss
5	See
6	Wasserwerk
7	Kläranlage
8	Deponie
9	Müllverbrennungsanlage
10	Messstation
11	KTA
\.

