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
-- TOC entry 4713 (class 0 OID 535818)
-- Dependencies: 279
-- Data for Name: probenehmer; Type: TABLE DATA; Schema: stammdaten; Owner: lada
--

COPY probenehmer (id, netzbetreiber_id, prn_id, bearbeiter, bemerkung, betrieb, bezeichnung, kurz_bezeichnung, ort, plz, strasse, telefon, tp, typ, letzte_aenderung) FROM stdin;
724	06	AV18	\N	\N	\N	Stadt Kassel - Veterinärdienst und Lebensmittelüberwachung	AVV Kassel	Kassel	34117	Kurt Schumacher Straße 31	\N	\N	\N	2000-01-01 00:00:00
726	06	AV24	\N	\N	\N	Stadt Wiesbaden - Amt f. Veterinärwesen u. Verbraucherschutz	AVV Wiesb	Wiesbaden	65187	Teutonenstraße 1	\N	\N	\N	2000-01-01 00:00:00
727	06	AV0	\N	\N	\N	Rheingau-Taunus Kreis - RD III.8 Verbraucherschutz u. Veterinärwesen	AVV RhgTns	Bad Schwalbach	65307	Heimbacher Straße 7	\N	\N	\N	2000-01-01 00:00:00
729	06	AV17	\N	\N	\N	Landkreis Fulda - Sachgebiet Veterinärwesen u. Verbraucherschutz	AVV Fulda	Fulda	36037	Wörthstraße 15	\N	\N	\N	2000-01-01 00:00:00
734	06	AV16	\N	\N	\N	Schwalm-Eder Kreis - Fachbereich 39, Veterinärwesen und Verbraucherschutz	AVV Homber	Homberg (Efze)	34576	Waßmuthshäuser Str. 52, Geb. 5	\N	\N	\N	2000-01-01 00:00:00
60	06	001	\N	\N	\N	HLUG Wiesbaden  - Dezernat W2, Probenahme	HLUG-W2 WI	Wiesbaden	65203	Rheingaustraße 186	\N	\N	\N	2000-01-01 00:00:00
78	06	LL2	\N	\N	\N	LLH Gartenbauberatungsschwerpunkt Friedberg	LLH Fb	Friedberg (Hessen)	61169	Homburger Straße 17	\N	\N	\N	2000-01-01 00:00:00
92	06	LL5	\N	\N	\N	LLH Gartenbauberatungsschwerpunkt Kassel	LLH Ks	Kassel	34117	Kölnische Straße 48-50	\N	\N	\N	2000-01-01 00:00:00
714	06	AV1	\N	\N	\N	LK Darmstadt-Dieburg - Amt f. Veterinärwesen u. Verbraucherschutz	AVV DaDieb	Darmstadt	64295	Haardtring 369	\N	\N	\N	2000-01-01 00:00:00
784	12	V05	\N	\N	\N	VLÜA FF Frankfurt (Oder)	VLÜA FF	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
813	06	001b	\N	\N	\N	HLUG Kassel  - Dezernat W2, Probenahme	HLUG-W2 KS	Kassel	34121	Ludwig-Mond-Straße 33	\N	\N	\N	2000-01-01 00:00:00
977	06	LH1	\N	\N	\N	Landesbetrieb Hessisches Landeslabor	LHL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1859	12	PR3	Jan Schmidt	\N	LVLF	Zentraler Technischer Prüfdienst - Luckau	ZTPD3	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1199	12	V15	\N	\N	\N	VLÜA PR Perleberg	VLÜA PR	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
\.
