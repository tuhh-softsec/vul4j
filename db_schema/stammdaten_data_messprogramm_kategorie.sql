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
-- TOC entry 4699 (class 0 OID 535766)
-- Dependencies: 264
-- Data for Name: messprogramm_kategorie; Type: TABLE DATA; Schema: stammdaten; Owner: lada
--

COPY messprogramm_kategorie (id, netzbetreiber_id, code, bezeichnung, letzte_aenderung) FROM stdin;
56	12	AK	Kontrolle Eigenüberwachung,  Probenahme durch Kraftwerk	2000-01-01 00:00:00
57	12	AU	zu MB=S, Altlast Auer	2000-01-01 00:00:00
58	12	BAS	zu MB=S, Altlasten BASF	2000-01-01 00:00:00
59	12	BD	zu MB=S, von Bereitschaftsdienst-Einsätzen	2000-01-01 00:00:00
60	12	BE	Umgebungsüberwachung,  eigene Probenahme	2000-01-01 00:00:00
61	12	BK	Umgebungsüberwachung,  Probenahme durch Kraftwerk	2000-01-01 00:00:00
62	12	G02	2. Eintrag G02 bezieht sich auf die gleiche Probe	2000-01-01 00:00:00
63	12	G03	2. Eintrag G03 bezieht sich auf die gleiche Probe	2000-01-01 00:00:00
64	12	G04	2. Eintrag G04 bezieht sich auf die gleiche Probe	2000-01-01 00:00:00
65	12	G05	2. Eintrag G05 bezieht sich auf die gleiche Probe	2000-01-01 00:00:00
66	12	G22	2. Eintrag G22 bezieht sich auf die gleiche Probe	2000-01-01 00:00:00
67	12	G27	im Januar u. Juli, 2.Eintrag bezieht sich auf die gl.Probe	2000-01-01 00:00:00
68	12	G28	für Sr -90 Best.  Monatsmischpr. aus Wochenpr. G28 ziehen	2000-01-01 00:00:00
69	12	IMP	zu MB=S, Importe von Grenze FFo nach Anruf	2000-01-01 00:00:00
70	12	KKR	zu MB=S, Freimessung KKW Rheinsberg	2000-01-01 00:00:00
71	12	KOH	zu MB=S, Kohle Jänschwalde	2000-01-01 00:00:00
72	12	LM	zu MB=S, LMIS-ähnliche Proben als LMIS-Ersatz o.zusätzl. Probe von IMIS/LMIS-Probenahmeorten	2000-01-01 00:00:00
73	12	NDF	zu MB=S, Tritium-Messung Orbg für Neuendorf	2000-01-01 00:00:00
74	12	NUK	zu MB=S, Nuklearmedizin	2000-01-01 00:00:00
75	12	ODL	zu MB=S, ODL-Messung auf einer in-situ-Fläche	2000-01-01 00:00:00
76	12	PIL	zu MB=S, von Pilzsachverständigen	2000-01-01 00:00:00
77	12	P7	zu MB=S, §7-Daten	2000-01-01 00:00:00
78	12	RAD	zu MB=S, LMIS-Nebenprobe Ra zu einer IMIS-Probe	2000-01-01 00:00:00
79	12	SON	zu MB=S, Sonstige, alles was nirgendwo sonst paßt	2000-01-01 00:00:00
80	12	SR	zu MB=S, LMIS-Nebenprobe Sr zu einer IMIS-Probe	2000-01-01 00:00:00
81	12	VET	zu MB=S, Pilze/Wild von Veterinären (MELF: 600-Bq-Überw. Inland)	2000-01-01 00:00:00
82	12	WGT	zu MB=S, Altlasten WGT-Liegenschaften	2000-01-01 00:00:00
83	12	WIS	zu MB=S, sonst. "akadem." Fragestellungen (Holz, ZALF, Baust...)	2000-01-01 00:00:00
195	06	A	Niederschlag	2000-01-01 00:00:00
475	06	001	Routinemessprogramm	2000-01-01 00:00:00
556	12	G10	2. Eintrag G10 bezieht sich auf die gleiche Probe	2000-01-01 00:00:00
557	12	G19	2. Eintrag G19 bezieht sich auf die gleiche Probe	2000-01-01 00:00:00
558	12	G12	2. Eintrag G12 bezieht sich auf die gleiche Probe	2000-01-01 00:00:00
559	12	G17	2. Eintrag G17 bezieht sich auf die gleiche Probe	2000-01-01 00:00:00
560	12	G16	2. Eintrag G16 bezieht sich auf die gleiche Probe	2000-01-01 00:00:00
561	12	G13	2. Eintrag G13 bezieht sich auf die gleiche Probe	2000-01-01 00:00:00
562	12	G11	2. Eintrag G11 bezieht sich auf die gleiche Probe	2000-01-01 00:00:00
563	12	G15	2. Eintrag G15 bezieht sich auf die gleiche Probe	2000-01-01 00:00:00
564	12	G01	2. Eintrag G01 bezieht sich auf die gleiche Probe	2000-01-01 00:00:00
565	12	G09	2. Eintrag G09 bezieht sich auf die gleiche Probe	2000-01-01 00:00:00
566	12	G20	2. Eintrag G20 bezieht sich auf die gleiche Probe	2000-01-01 00:00:00
567	12	G25	2. Eintrag G25 bezieht sich auf die gleiche Probe	2000-01-01 00:00:00
716	06	LHL	Lebensmittelüberwachung	2000-01-01 00:00:00
776	12	G29	für Sr-90 Bestimmung Quartalsmischprobe aus 14tägigen Stichproben  G29 ziehen	2000-01-01 00:00:00
946	06	RP	Futtermittelüberwachung	2011-07-11 12:26:00
950	11	3	REI - bestimmungsgemäßer Betrieb	2013-10-10 11:07:00
951	11	0	IMIS-Routinemessprogramm	2014-01-29 09:33:00
952	11	1	IMIS-Intensivmessprogramm	2013-10-10 11:07:00
953	11	4	REI-Übung Störfall	2013-10-10 11:07:00
954	11	5	REI-Störfall/Unfall	2013-10-10 11:07:00
955	11	6	Importkontrolle	2013-10-10 11:07:00
956	11	7	Harmonisiertes Messprogramm	2013-10-10 11:07:00
957	11	8	Altlasten	2013-10-10 11:07:00
958	11	9	Sondermessprogramm	2013-10-10 11:07:00
959	11	10	Qualitätskontrolle	2013-10-10 11:07:00
960	11	11	Richtlinie Kontrolle der Eigenüberwachung radioaktiver Emissionen	2013-10-14 11:48:00
968	12	SPA	Sparse Network	2015-05-08 08:59:00
\.
