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

SELECT pg_catalog.setval('kta_id_seq', (SELECT max(id) FROM kta), true);


COPY kta_gruppe (id, kta_gruppe, beschreibung) FROM stdin;
1	U01A	Helmholtz-Zentrum Geesthacht
2	U01A/B	Helmholtz-Zentrum Geesthacht / KKW Krümmel
3	U01B	KKW Krümmel
4	U01B/K	KKW Krümmel / Standortzwischenlager Krümmel
5	U01C	KKW Brunsbüttel
6	U01C/D	KKW Brunsbüttel / KKW Brokdorf
7	U01D	KKW Brokdorf
8	U01D/M	KKW Brokdorf / Standortzwischenlager Brokdorf
9	U01I	Interimslager Krümmel
10	U01I/K	Interimslager Krümmel /Standortzwischenlager Brokdorf
11	U01K	Standortzwischenlager Krümmel
12	U01L	Standortzwischenlager Brunsbüttel
13	U01M	Standortzwischenlager Brokdorf
14	U03A	Standortzwischenlager Grohnde
15	U03B	Brennelementefertigungsanl. Lingen
16	U03C	Standortzwischenlager Unterweser
17	U03C/U	KKW Unterweser / Standortzwischenlager Unterweser / 
18	U03E	KKW Emsland
19	U03F	Forschungsbergwerk Asse
20	U03G	KKW Grohnde
21	U03K	Endlager Konrad
22	U03L	KKW Lingen
23	U03P	GNS - Werk Gorleben -
24	U03S	KKW Stade
25	U03U	KKW Unterweser
26	U03Z	Standortzwischenlager Lingen
27	U05B	Brennelement-Zwischenl. Ahaus
28	U05F	Forschungszentrum Jülich
29	U05G	AVR-Versuchskernkraftwerk Jülich
30	U05K	KKW Würgassen
31	U05T	Thorium-Hochtemp.reakt. Hamm-Uentrop
32	U05U	Urananreicherungsanlage Gronau
33	U06B	KKW Biblis und BE-Zwischenlager
34	U07M	KKW Mülheim-Kärlich
35	U07U	Uni Mainz
36	U08H	DKFZ Heidelberg
37	U08K	Karlsruher Institut für Technologie - Campus Nord
38	U08M	Abraumhalde Menz.
39	U08N	EnKK Neckarwestheim
40	U08O	EnKK Obrigheim
41	U08P	EnKK Philippsburg
42	U08W	KKW Wyhl
43	U09A	KKW Isar 1+2
44	U09A/B	KKW Isar 1+2 / KKW Isar 1
45	U09B	KKW Isar1
46	U09C	KKW Isar2
47	U09D	KKW Grafenrheinfeld
48	U09E	KKW Gundremmingen Block B/C
49	U09F	Versuchs-AKW Kahl a.M.
50	U09G	Forschungsreaktor München
51	U09H	Siemens Brennelementewerk Hanau, Standort Karlstein
52	U09I	Siemens AG - AREVA NP GmbH, Standort Karlstein
53	U09J	AREVA NP GmbH, Standort Erlangen
54	U09K	Forschungsneutronenquelle Heinz Maier-Leibnitz
55	U09K/G	Forschungsreaktor München / Forschungsneutronenquelle Heinz Maier-Leibnitz
56	U11B	Experimentierreakt. II Berlin
57	U12R	KKW Rheinsberg
58	U13A	KKW Lubmin/Greifswald
59	U13B	Zwischenlager Nord
60	U14R	Forschungszentrum Rossendorf
61	U15M	nicht benutzen, jetzt UELM, Endlager für radioaktive Abfälle Morsleben (ERAM)
62	UCHL	KTA Leibstadt mit Beznau und Villigen
63	UELA	Endlager für radioaktive Abfälle Asse
64	UELM	Endlager für radioaktive Abfälle Morsleben (ERAM)
65	UFRC	KKW Cattenom
66	UFRF	KKW Fessenheim
\.

SELECT pg_catalog.setval('kta_gruppe_id_seq', (SELECT max(id) FROM kta_gruppe), true);


COPY kta_grp_zuord (id, kta_grp_id, kta_id) FROM stdin;
1	1	1
2	3	2
3	5	3
4	7	4
5	9	5
6	11	6
7	12	7
8	13	8
9	14	9
10	15	10
11	16	11
12	18	12
13	19	13
14	20	14
15	21	15
16	22	16
17	23	17
18	24	18
19	25	19
20	26	20
21	27	21
22	28	22
23	29	23
24	30	24
25	31	25
26	32	26
27	33	27
28	34	28
29	35	29
30	36	30
31	37	31
32	38	32
33	39	33
34	40	34
35	41	35
36	42	36
37	43	37
38	45	38
39	46	39
40	47	40
41	48	41
42	49	42
43	50	43
44	51	44
45	52	45
46	53	46
47	54	47
48	56	48
49	57	49
50	58	50
51	59	51
52	60	52
53	61	53
54	62	54
55	63	55
56	64	56
57	65	57
58	66	58
59	2	1
60	2	2
61	4	2
62	4	6
63	6	3
64	6	4
65	8	4
66	8	8
67	10	5
68	10	6
69	17	11
70	17	19
71	44	37
72	44	38
73	55	43
74	55	47
\.

SELECT pg_catalog.setval('kta_grp_zuord_id_seq', (SELECT max(id) FROM kta_grp_zuord), true);
