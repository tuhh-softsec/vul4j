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
-- TOC entry 4693 (class 0 OID 535748)
-- Dependencies: 258
-- Data for Name: mess_methode; Type: TABLE DATA; Schema: stammdaten; Owner: lada
--

COPY mess_methode (id, beschreibung, messmethode) FROM stdin;
GI	\N	Iod, Gamma-Spektrometrie
AB	\N	Gesamt-Alpha und Beta
A2	\N	Gesamt-Alpha
A3	\N	Gesamt-Alpha (Handmonitor)
A4	\N	Gesamt-Alpha  verzögert
B1	\N	nukl.-spez. Beta-Messung
B2	\N	Gesamt-Beta
B3	\N	Gesamt-Beta (Handmonitor)
B4	\N	Gesamt-Beta verzögert
B5	\N	Rest-Beta
D1	\N	Gamma-OD
D2	\N	Neutronen-OD
E1	\N	Elementbestimmung
G1	\N	Gamma-Spektrometrie
G2	\N	nuklidspezifische Gammamessung
G3	\N	Iod-Messung (Monitor)
G4	\N	Gesamt-Gamma
I1	\N	in-situ Spektrometrie brutto
I2	\N	in-situ Spektrometrie netto
I3	\N	nuklidspezifische Dosisleistung brutto
M1	\N	Niederschlagsmenge
M2	\N	Niederschlagsintensität
M3	\N	Schneehöhe
O1	\N	Gamma-ODL
O2	\N	Neutronen-ODL
S1	\N	Berechneter Wert
S4	\N	sonstige radiologische Messmethode
A1	\N	Alpha-Spektrometrie
BS	\N	Sr 90-Bestimmung
BH	\N	H3-Bestimmung
BC	\N	C14-Bestimmung
M0	Markiert eine Messung als Meteo-Messung	Meteo-Messung
BE	\N	Beta-Messung Edelgase
BX	\N	Sr-Schnellbestimmung
AP	\N	Plutonium-Bestimmung
AU	\N	Uran-Bestimmung
O3	\N	Gamma-ODL (Handmessung)
\.
