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
-- TOC entry 4508 (class 0 OID 1992976)
-- Dependencies: 283
-- Data for Name: kta; Type: TABLE DATA; Schema: stammdaten; Owner: postgres
--

COPY kta (id, code, bezeichnung) FROM stdin;
1	U01A	Helmholtz-Zentrum Geesthacht
2	U01B	KKW Krümmel
3	U01C	KKW Brunsbüttel
4	U01D	KKW Brokdorf
5	U01I	Interimslager Krümmel
6	U01K	Standortzwischenlager Krümmel
7	U01L	Standortzwischenlager Brunsbüttel
8	U01M	Standortzwischenlager Brokdorf
9	U03A	Standortzwischenlager Grohnde
10	U03B	Brennelementefertigungsanl. Lingen
11	U03C	Standortzwischenlager Unterweser
12	U03E	KKW Emsland
13	U03F	Forschungsbergwerk Asse
14	U03G	KKW Grohnde
15	U03K	Endlager Konrad
16	U03L	KKW Lingen
17	U03P	GNS - Werk Gorleben -
18	U03S	KKW Stade
19	U03U	KKW Unterweser
20	U03Z	Standortzwischenlager Lingen
21	U05B	Brennelement-Zwischenl. Ahaus
22	U05F	Forschungszentrum Jülich
23	U05G	AVR-Versuchskernkraftwerk Jülich
24	U05K	KKW Würgassen
25	U05T	Thorium-Hochtemp.reakt. Hamm-Uentrop
26	U05U	Urananreicherungsanlage Gronau
27	U06B	KKW Biblis und BE-Zwischenlager
28	U07M	KKW Mülheim-Kärlich
29	U07U	Uni Mainz
30	U08H	DKFZ Heidelberg
31	U08K	Karlsruher Institut für Technologie - Campus Nord
32	U08M	Abraumhalde Menz.
33	U08N	EnKK Neckarwestheim
34	U08O	EnKK Obrigheim
35	U08P	EnKK Philippsburg
36	U08W	KKW Wyhl
37	U09A	KKW Isar 1+2
38	U09B	KKW Isar1
39	U09C	KKW Isar2
40	U09D	KKW Grafenrheinfeld
41	U09E	KKW Gundremmingen Block B/C
42	U09F	Versuchs-AKW Kahl a.M.
43	U09G	Forschungsreaktor München
44	U09H	Siemens Brennelementewerk Hanau, Standort Karlstein
45	U09I	Siemens AG - AREVA NP GmbH, Standort Karlstein
46	U09J	AREVA NP GmbH, Standort Erlangen
47	U09K	Forschungsneutronenquelle Heinz Maier-Leibnitz
48	U11B	Experimentierreakt. II Berlin
49	U12R	KKW Rheinsberg
50	U13A	KKW Lubmin/Greifswald
51	U13B	Zwischenlager Nord
52	U14R	Forschungszentrum Rossendorf
53	U15M	nicht benutzen, jetzt UELM, Endlager für radioaktive Abfälle Morsleben (ERAM)
54	UCHL	KTA Leibstadt mit Beznau und Villigen
55	UELA	Endlager für radioaktive Abfälle Asse
56	UELM	Endlager für radioaktive Abfälle Morsleben (ERAM)
57	UFRC	KKW Cattenom
58	UFRF	KKW Fessenheim
\.
