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
-- TOC entry 4687 (class 0 OID 535733)
-- Dependencies: 252
-- Data for Name: koordinaten_art; Type: TABLE DATA; Schema: stammdaten; Owner: lada
--

COPY koordinaten_art (id, koordinatenart, idf_geo_key) FROM stdin;
1	Gauß-Krüger (Bessel)	G
2	geografisch-gradiell (WGS84)	D
3	UTM-MGRS (WGS84)	M
4	geografisch-dezimal (WGS84)	g
5	UTM (WGS84)	U
8	UTM (Hayford)	u
9	UTM-MGRS (Hayford)	m
\.
