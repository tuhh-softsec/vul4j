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
-- TOC entry 4697 (class 0 OID 535760)
-- Dependencies: 262
-- Data for Name: messgroessen_gruppe; Type: TABLE DATA; Schema: stammdaten; Owner: lada
--

COPY messgroessen_gruppe (id, bezeichnung, ist_leitnuklidgruppe) FROM stdin;
57	Messgrössen für nuklidspezifische Gammamessung	0
97	Gesamt-Alpha und Beta	0
30	Sr-Isotope (insbes. Sr 90)	0
31	Iod-Isotope (insbes. I 131)	0
32	Alpha-Teilchen emittierende Pu-Isotope (insbes. Pu 239, Am 241)	0
33	sonst. Nuklide mit Halbwertzeiten > 10d (insbes. Cs 134, Cs 137)	0
34	Cs-Isotope (Cs 134, Cs 137)	0
5	Alphaspektrometrie Messgrößen	0
6	Berechnete Größen	0
7	Beta Größen	0
8	Elementbestimmungsmessgrößen	0
10	Gamma-OD	0
11	Gamma-ODL	0
12	Gamma-Spektrometrie Iod 131	0
13	Gammaspektrometrie Messgrößen	0
14	Gesamt-Alpha-Aktivität	0
15	Gesamt-Alpha-Aktivität, Handmonitor	0
16	Gesamt-Alpha-Aktivität, verzögert	0
17	Gesamt-Beta-Aktivität	0
18	Gesamt-Beta-Aktivität, Handmonitor	0
19	Gesamt-Beta-Aktivität, verzögert / Künstlich Gesamt-Beta	0
20	Gesamt-Cäsium-Aktivität	0
21	Gesamt-Gamma-Aktivität	0
22	Neutronen-Dosisleistung	0
23	Neutronen-Ortsdosis	0
24	Niederschlagsintensität	0
25	Niederschlagsmenge	0
26	nuklidspezifische Dosisleistung	0
3	PARK - Alpha-Strahler	0
2	PARK - Beta-Strahler	0
4	PARK - Edelgase	0
1	PARK - Gamma-Strahler (aerosolgebunden vorkommend)	0
27	Rest-Beta	0
28	Schneehöhe	0
29	sonstige radiologische Meßmethode	0
35	Cs 137	0
36	I 131	0
37	Sr 89/90	0
55	H 3	0
56	C 14	0
77	Edelgas-Messgrößen	0
119	Gamma-ODL (Handmessung)	0
117	Alphaspektrometrie Uran	0
118	Alphaspektrometrie Plutonium	0
\.


--
-- TOC entry 4701 (class 0 OID 535771)
-- Dependencies: 266
-- Data for Name: mg_grp; Type: TABLE DATA; Schema: stammdaten; Owner: lada
--

COPY mg_grp (messgroessengruppe_id, messgroesse_id) FROM stdin;
1	2
1	10
1	28
1	51
1	56
1	60
1	64
1	66
1	68
1	85
1	166
1	167
1	176
1	177
1	184
1	185
1	193
1	196
1	201
1	214
1	220
1	222
1	250
1	303
1	304
1	307
1	310
1	316
1	321
1	322
1	323
1	325
1	340
1	342
1	343
1	345
1	369
1	372
1	373
1	384
1	393
1	394
1	402
1	403
1	404
1	423
1	437
1	543
1	658
1	670
1	672
1	673
1	684
1	686
1	725
1	739
1	749
1	761
2	1
2	164
2	165
3	732
3	746
3	747
3	750
3	759
3	768
3	769
3	781
3	793
3	795
4	26
4	140
4	142
4	143
4	354
4	355
4	356
4	357
5	717
5	727
5	728
5	730
5	732
5	744
5	745
5	746
5	747
5	750
5	759
5	766
5	768
5	769
5	770
5	781
5	793
5	794
5	795
5	850
5	978
6	950
7	1
7	5
7	164
7	165
7	215
7	717
8	28
8	958
10	925
10	926
11	900
11	909
11	910
11	911
11	998
11	999
12	259
12	273
12	338
12	340
12	860
12	870
12	880
13	2
13	10
13	26
13	28
13	40
13	51
13	56
13	60
13	64
13	66
13	67
13	68
13	85
13	90
13	92
13	117
13	140
13	141
13	142
13	143
13	144
13	162
13	166
13	167
13	171
13	176
13	177
13	184
13	185
13	193
13	196
13	201
13	214
13	220
13	221
13	222
13	247
13	248
13	250
13	251
13	252
13	257
13	267
13	281
13	303
13	304
13	307
13	310
13	316
13	321
13	322
13	323
13	325
13	335
13	338
13	340
13	342
13	343
13	345
13	354
13	355
13	356
13	357
13	358
13	359
13	369
13	372
13	373
13	380
13	384
13	392
13	393
13	394
13	401
13	402
13	403
13	404
13	423
13	432
13	435
13	437
13	445
13	456
13	457
13	458
13	529
13	543
13	560
13	582
13	644
13	653
13	654
13	657
13	658
13	670
13	671
13	672
13	673
13	683
13	684
13	686
13	705
13	714
13	715
13	717
13	719
13	720
13	724
13	725
13	727
13	728
13	730
13	731
13	732
13	733
13	737
13	739
13	740
13	741
13	747
13	749
13	750
13	761
13	781
13	860
13	861
13	862
13	955
14	904
14	905
14	923
15	904
16	915
16	916
16	917
16	918
17	902
17	903
17	924
18	902
19	919
19	920
19	921
19	922
20	906
21	901
21	908
22	914
22	929
22	930
22	1000
22	1001
23	913
23	927
23	928
25	950
25	951
25	952
26	2
26	10
26	26
26	28
26	51
26	56
26	60
26	64
26	66
26	68
26	85
26	140
26	141
26	142
26	143
26	144
26	166
26	167
26	176
26	177
26	184
26	185
26	193
26	196
26	201
26	214
26	220
26	222
26	250
26	303
26	304
26	307
26	310
26	316
26	321
26	322
26	323
26	325
26	340
26	342
26	343
26	345
26	354
26	355
26	356
26	357
26	358
26	359
26	369
26	372
26	373
26	384
26	393
26	394
26	402
26	403
26	404
26	423
26	437
26	543
26	658
26	670
26	672
26	673
26	684
26	686
26	717
26	725
26	739
26	747
26	749
26	761
27	907
28	951
29	2
29	28
29	59
29	68
29	74
29	165
29	222
29	340
29	369
29	373
29	670
29	715
29	717
29	719
29	725
29	728
29	740
29	746
29	747
29	750
29	900
29	902
29	904
29	909
29	955
30	164
30	165
30	166
30	167
31	340
31	342
31	343
31	345
32	769
32	781
32	850
33	369
33	373
33	384
34	369
34	373
35	373
36	340
37	164
37	165
55	1
56	5
57	340
57	369
57	373
77	141
77	353
77	355
77	357
97	902
97	904
8	750
8	769
8	781
8	747
8	746
12	342
13	353
13	531
117	745
117	750
117	746
117	747
117	744
118	768
118	850
118	766
118	769
118	770
119	900
\.


--
-- TOC entry 4702 (class 0 OID 535774)
-- Dependencies: 267
-- Data for Name: mmt_messgroesse_grp; Type: TABLE DATA; Schema: stammdaten; Owner: lada
--

COPY mmt_messgroesse_grp (messgroessengruppe_id, mmt_id) FROM stdin;
5	A1
6	S1
7	B1
8	E1
10	D1
11	O1
12	G3
12	GI
13	G1
13	I1
13	I2
14	A2
15	A3
16	A4
17	B2
18	B3
19	B4
21	G4
22	O2
23	D2
24	M2
25	M1
26	I3
27	B5
28	M3
29	S4
37	BS
37	BX
55	BH
56	BC
57	G2
77	BE
97	AB
117	AU
118	AP
119	O3
\.

