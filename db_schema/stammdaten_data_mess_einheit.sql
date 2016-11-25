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
-- TOC entry 4691 (class 0 OID 535743)
-- Dependencies: 256
-- Data for Name: mess_einheit; Type: TABLE DATA; Schema: stammdaten; Owner: lada
--

COPY mess_einheit (id, beschreibung, einheit, eudf_messeinheit_id, umrechnungs_faktor_eudf) FROM stdin;
207	Becquerel pro Stunde	Bq/h	\N	\N
227	Megawatt	MW	\N	\N
120	Dezimeter	dm	\N	\N
139	Kilo-Becquerel pro Kilogramm Trockenmasse	kBq/kg(TM)	KBQ/KG	\N
99	Milli-Becquerel pro Liter	mBq/l	MBQ/L	\N
59	Kilogramm pro Person	kg/Person	\N	\N
84	Nano-Sievert pro Stunde	nSv/h	NSV/H	\N
88	Milli-Bequerel pro Kubikmeter	mBq/m³	MBQ/M3	\N
71	Mikro-Sievert	µSv	\N	\N
72	Milli-Sievert	mSv	\N	\N
73	Nano-Gray	nGy	\N	\N
74	Mikro-Gray	µGy	\N	\N
75	Milli-Gray	mGy	\N	\N
76	Mikro-Sievert pro Stunde	µSv/h	USV/H	\N
1	Millimeter	mm	MM	\N
2	Zentimeter	cm	CM	\N
3	Meter	m	M	\N
4	Kilometer	km	\N	\N
6	Quadratzentimeter	cm²	\N	\N
7	Quadratmeter	m²	M2	\N
8	Quadratkilometer	km²	\N	\N
9	Hektar	ha	\N	\N
10	Kubikzentimeter	cm³	\N	\N
11	Kubikmeter	m³	\N	\N
13	Sekunde	s	\N	\N
14	Minute	min	\N	\N
15	Stunde	h	\N	\N
16	Tag	d	\N	\N
17	Woche	Wo	\N	\N
18	Monat	Mo	\N	\N
19	Jahr	a	\N	\N
20	Gramm	g	G	\N
21	Kilogramm	kg	KG	\N
22	Tonne	t	\N	\N
25	Milliliter	ml	\N	\N
26	Zentiliter	cl	\N	\N
27	Liter	l	L	\N
28	Hektoliter	hl	\N	\N
30	Kubikmeter pro Sekunde	m³/s	\N	\N
31	Kubikmeter pro Minute	m³/min	\N	\N
32	Kubikmeter pro Stunde	m³/h	\N	\N
33	Kubikmeter pro Tag	m³/d	\N	\N
35	Zentimeter pro Sekunde	cm/s	\N	\N
36	Meter pro Sekunde	m/s	\N	\N
37	Meter pro Minute	m/min	\N	\N
69	Becquerel pro Tag und Person	Bq/(d*p)	BQ/D.P	\N
70	Nano-Sievert	nSv	\N	\N
98	Becquerel pro Quadratmeter und Tag	Bq/(m²*d)	BQ/M2/D	\N
40	Liter pro Quadratmeter	l/m²	L/M2	\N
42	Liter pro Minute	l/min	\N	\N
43	Liter pro Stunde	l/h	\N	\N
44	Liter pro Tag	l/d	\N	\N
45	Kilogramm pro Quadratmeter	kg/m²	KG/M2	\N
46	Kilogramm pro Kubikmeter	kg/m³	KG/M3	\N
47	Milligramm pro Liter	mg/l	MG/L	\N
48	Tonne pro Hektar	t/ha	\N	\N
49	Kilogramm pro Tag	kg/d	\N	\N
53	Tonnen pro Tag	t/d	\N	\N
54	Tonnen pro Jahr	t/a	\N	\N
55	Kilogramm pro Tag und Person	kg/(d*p)	\N	\N
50	Kilogramm Trockenmasse	kg(TM)	\N	\N
51	Kilogramm Feuchtmasse	kg(FM)	\N	\N
52	Kilogramm Trockenmasse pro Kubikmeter	kg(TM)/m³	\N	\N
60	Becquerel	Bq	BQ	\N
61	Becquerel pro Quadratmeter	Bq/m²	BQ/M2	\N
62	Becquerel pro Kubikmeter	Bq/m³	BQ/M3	\N
63	Becquerel pro Liter	Bq/l	BQ/L	\N
64	Becquerel pro Kilogramm	Bq/kg	BQ/KG	\N
65	Becquerel pro Kilogramm Trockenmasse	Bq/kg(TM)	\N	\N
66	Becquerel pro Kilogramm Glührückstand	Bq/kg(GR)	\N	\N
67	Becquerel pro Kilogramm Feuchtmasse	Bq/kg(FM)	KBQ/KG	\N
38	Millimeter pro Tag	mm/d	\N	\N
77	Mikro-Sievert pro Tag	µSv/d	\N	\N
78	Mikro-Gray pro Stunde	µGy/h	\N	\N
79	Mikro-Gray pro Tag	µGy/d	\N	\N
80	Sievert	Sv	\N	\N
81	Gray	Gy	\N	\N
82	Sievert pro Stunde	Sv/h	\N	\N
83	Gray pro Stunde	Gy/h	\N	\N
85	Impulse pro Sekunde	Ips	\N	\N
90	Prozent	%	%	\N
91	Kurs in Winkelgraden	Grad	\N	\N
92	Grad Celsius	Grad C	\N	\N
93	Windstärke in Beaufort	Bft	\N	\N
94	Salinität	So/oo	\N	\N
95	Anzahl der Personen/Einwohner	PERS	\N	\N
96	Milli-Siemens pro Zentimeter	mS/cm	\N	\N
97	Kilo-Becquerel pro Quadratmeter	kBq/m²	KBQ/M2	\N
68	Becquerel pro Tag	Bq/d	BQ/D	\N
86	Milli-Sievert pro Jahr	mSv/a	\N	\N
100	Nano-Gray pro Stunde	nGy/h	\N	\N
101	Becquerel pro Gramm Feuchtmasse	Bq/g(FM)	\N	\N
102	Kilo-Becquerel pro Kubikmeter	kBq/m³	KBQ/M3	\N
103	Kilo-Becquerel pro Liter	kBq/l	KBQ/L	\N
104	Kilo-Becquerel pro Kilogramm Feuchtmasse	kBq/kg(FM)	\N	\N
105	Mikro-Becquerel pro Kubikmeter	µBq/m³	UBQ/M3	\N
106	Mikro-Becquerel pro Kilogramm Feuchtmasse 	µBq/kg(FM)	\N	\N
107	Nano-Becquerel pro Kubikmeter	nBq/m³	NBQ/M3	\N
108	Nano-Becquerel pro Liter	nBq/l	NBQ/L	\N
109	Milli-Becquerel pro Kilogramm Feuchtmasse	mBq/kg(FM)	\N	\N
110	Milli-Becquerel pro Quadratmeter	mBq/m²	MBQ/M2	\N
111	Mega-Becquerel pro Quadratmeter	MBq/m²	KKBQ/M2	\N
112	Mega-Becquerel pro Quadratkilometer	MBq/km²	KKBQ/KM2	\N
113	Pico-Curie pro Gramm	pCi/g(FM)	PCI/G	\N
114	Pico-Curie pro Kubikmeter	pCi/m³	PCI/M3	\N
115	Pico-Curie pro Liter	pCi/l	PCI/L	\N
116	Nano-Curie pro Kilogramm	nCi/kg(FM)	\N	\N
117	Nano-Curie pro Quadratmeter	nCi/m²	NCI/M2	\N
118	Curie pro Quadratmeter	Ci/m²	\N	\N
159	Mikro-Becquerel pro Kilogramm	µBq/kg	UBQ/KG	\N
160	Becquerel pro Gramm	Bq/g	BQ/G	\N
161	Kilo-Becquerel pro Kilogramm	kBq/kg	KBQ/KG	\N
162	Milli-Becquerel pro Kilogramm	mBq/kg	MBQ/KG	\N
163	Mikro-Becquerel pro Kilogramm Trockenmasse	µBq/kg(TM)	\N	\N
164	Becquerel pro Gramm Trockenmasse	Bq/g(TM)	\N	\N
166	Milli-Becquerel pro Kilogramm Trockenmasse	mBq/kg(TM)	\N	\N
187	Hekto-Pascal	hPa	\N	\N
188	Millimeter pro Stunde	mm/h	\N	\N
247	Becquerel pro Quadratzentimeter	Bq/cm²	\N	\N
269	Probe	Probe	\N	\N
268	Becquerel pro Probe	Bq/Probe	\N	\N
267	Becquerel  pro Milliliter	Bq/ml	\N	\N
270	Becquerel pro Impulse pro Sekunde	Bq/Ips	\N	\N
271	Watt pro Quadratmeter	W/m²	\N	\N
272	Millimeter pro Minute	mm/min	\N	\N
\.
