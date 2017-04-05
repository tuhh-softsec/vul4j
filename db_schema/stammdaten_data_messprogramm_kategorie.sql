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
498	13	K01	KGR - REI	2000-01-01 00:00:00
499	13	K02	KGR - Störfalltraining	2000-01-01 00:00:00
500	13	K03	KGR - EIG	2000-01-01 00:00:00
516	08	000	nicht vergeben	2000-01-01 00:00:00
555	03	308	Auftragsuntersuchungen	2000-01-01 00:00:00
877	13	011	FMStrVVwV - Fumi	2000-01-01 00:00:00
916	09	400	Lebensmitteluntersuchung für LGL	2000-01-01 00:00:00
936	10	S10	§3-Messungen - LUA SB, Stand 2010	2010-12-01 15:37:39
937	10	C10	Cattenom-Messprogramm (REI-I) Teil Saarland, Stand 2010	2010-12-01 15:38:13
938	10	E10	EURATOM Oberflächenwasser, Stand 2010	2010-12-01 08:34:50
939	10	T10	Trinkwasserprogramm, Stand 2010	2010-12-01 08:34:50
940	10	H16	§3-Messungen - ehemals HOM, Stand 2016	2016-12-12 09:43:51
942	09	703	Vergleichsmessung für Verkehrsfähigkeit von Wildbret	2011-02-16 14:45:46
943	08	350	Japan Störfall 2011	2011-03-28 15:33:23
856	08	335	Wildmessungen (nicht von CVUA erstellte Messwerte)	2000-01-01 00:00:00
857	08	336	Wildmessungen (Lebensmittelüberwachung)	2000-01-01 00:00:00
876	13	010	LALLF - LeMi	2000-01-01 00:00:00
947	08	990	Übung	2012-03-06 13:50:53
948	10	§7	§7-Messungen - Uni HOM, Stand 2012	2012-03-13 10:50:03
950	11	3	REI - bestimmungsgemäßer Betrieb	2013-10-10 11:07:30
951	11	0	IMIS-Routinemessprogramm	2014-01-29 09:33:21
952	11	1	IMIS-Intensivmessprogramm	2013-10-10 11:07:30
953	11	4	REI-Übung Störfall	2013-10-10 11:07:30
954	11	5	REI-Störfall/Unfall	2013-10-10 11:07:30
955	11	6	Importkontrolle	2013-10-10 11:07:30
956	11	7	Harmonisiertes Messprogramm	2013-10-10 11:07:30
957	11	8	Altlasten	2013-10-10 11:07:30
958	11	9	Sondermessprogramm	2013-10-10 11:07:30
959	11	10	Qualitätskontrolle	2013-10-10 11:07:30
960	11	11	Richtlinie Kontrolle der Eigenüberwachung radioaktiver Emissionen	2013-10-14 11:48:26
964	03	551	Exportuntersuchungen LAVES Braunschweig	2015-01-08 10:17:59
965	03	552	Exportuntersuchungen LAVES Hannover	2015-01-08 10:17:59
966	03	553	Exportuntersuchungen LAVES Oldenburg	2015-01-08 10:17:59
968	12	SPA	Sparse Network	2015-05-08 08:59:54
816	13	HRO	Proben, die durch das LALLF geliefert werden	2000-01-01 00:00:00
836	07	110	Intensivmessprogramm In-Situ StrVG	2000-01-01 00:00:00
896	19	BE	Test-Programm für CRQ-4599	2000-01-01 00:00:00
808	08	272	KTA - KWO,Messungen außerhalb der REI	2000-01-01 00:00:00
537	03	209	Sonstiges	2000-01-01 00:00:00
558	12	G12	2. Eintrag G12 bezieht sich auf die gleiche Probe	2000-01-01 00:00:00
2	03	111	Sonderprogramm Honig	2000-01-01 00:00:00
6	03	130	Sonderprogramm Ostimporte	2000-01-01 00:00:00
11	03	204	Wild, Problemgebiete	2000-01-01 00:00:00
15	03	212	Sonderprogramm Wild	2000-01-01 00:00:00
19	03	330	Sonderprogroramm Ostimporte	2000-01-01 00:00:00
24	03	450	Technologieprogramm Brauereien	2000-01-01 00:00:00
30	05	004	Sondermessung Pilze	2000-01-01 00:00:00
34	07	202	KKW-Überwachung Biblis	2000-01-01 00:00:00
946	06	RP	Futtermittelüberwachung	2011-07-11 12:26:24
917	09	500	Futtermitteluntersuchung für LGL	2000-01-01 00:00:00
941	19	007	geheimes Landesmessprogramm	2010-12-15 10:42:34
945	04	J	Japan_Fukushima	2011-06-16 08:35:48
364	08	801	LUBW - Ra-Zwischenfall Städt. Krankenhaus Konstanz	2000-01-01 00:00:00
365	08	720	LUBW - Emissionskontrollmessungen	2000-01-01 00:00:00
367	08	102	Q - Gitterkammern	2000-01-01 00:00:00
370	08	105	Q - Liquidcounter	2000-01-01 00:00:00
373	08	108	Q - Insitu-Gamma-Spektrometer	2000-01-01 00:00:00
374	08	109	Q - Tragbare Strahlenschutzmessgeräte	2000-01-01 00:00:00
376	08	111	Q - Gamma-Spektrometer im Labor: Nulleffekt	2000-01-01 00:00:00
377	08	112	Q - Ringversuche	2000-01-01 00:00:00
378	08	201	LUBW - UW bei KIT - CN	2000-01-01 00:00:00
379	08	202	LUBW - UW bei KWO	2000-01-01 00:00:00
381	08	204	LUBW - UW bei KKP	2000-01-01 00:00:00
382	08	205	LUBW - UW bei KKB, KKL und PSI 	2000-01-01 00:00:00
383	08	206	LUBW - UW bei DKFZ	2000-01-01 00:00:00
384	08	207	LUBW - UW Menzenschwand	2000-01-01 00:00:00
175	01	KE	KFÜ Emission	2000-01-01 00:00:00
363	08	802	LUBW - Polizeiliche Ermittlungen	2000-01-01 00:00:00
385	08	208	LUBW - UW bei KWS	2000-01-01 00:00:00
386	08	209	LUBW - sonst. Ergänzungsmessg. kerntech. Anlagen	2000-01-01 00:00:00
389	08	303	IMIS - Intensivmessprogramm, Fall X	2000-01-01 00:00:00
392	08	310	Programme anderer IMIS-Teilnehmer 	2000-01-01 00:00:00
393	08	311	N - Referenzmessungen KIT - CN	2000-01-01 00:00:00
394	08	312	N - Niederschläge aus dem RAM	2000-01-01 00:00:00
396	08	321	Messstation Aalen	2000-01-01 00:00:00
397	08	322	Messstation Heidelberg	2000-01-01 00:00:00
402	08	327	Messstation Karlsruhe 	2000-01-01 00:00:00
403	08	328	Messstation Waldshut/Dogern 	2000-01-01 00:00:00
405	08	330	Messstation Philippsburg 302 	2000-01-01 00:00:00
406	08	331	Messstation Neckarwestheim 512 	2000-01-01 00:00:00
407	08	401	LUBW - Arbeitsgem. Gewässer: ARGE Rhein	2000-01-01 00:00:00
408	08	402	LUBW - Arbeitsgem. Gewässer: ARGE Neckar	2000-01-01 00:00:00
410	08	404	LUBW - Sr-90	2000-01-01 00:00:00
411	08	405	LUBW - Pu-Isotope	2000-01-01 00:00:00
414	08	408	LUBW - Wischtestmessprogramme	2000-01-01 00:00:00
415	08	409	LUBW - Ortsdosisleistungsmessprogramme	2000-01-01 00:00:00
416	08	410	LUBW - Radonmessprogramme	2000-01-01 00:00:00
417	08	411	LUBW - Abwasserzweckverb. Heidelberg Neue Kläranlage	2000-01-01 00:00:00
418	08	412	LUBW - Arbeitsgem. Gewässer: ARGE Donau	2000-01-01 00:00:00
419	08	413	LUBW - Probenahmeort LfU	2000-01-01 00:00:00
420	08	414	LUBW - Stillegungsprogramm Menzenschwand	2000-01-01 00:00:00
423	08	417	LUBW - StrlVG-Ergänzungsmessungen	2000-01-01 00:00:00
425	08	419	LUBW - sonstige Insitu-Messungen	2000-01-01 00:00:00
427	08	421	LUBW - sonstige Alpha-Beta-Strahler	2000-01-01 00:00:00
428	08	501	LUBW - Radioaktive Abfälle	2000-01-01 00:00:00
430	08	504	LUBW - Altlast: Fa. Junghans, Schramberg	2000-01-01 00:00:00
431	08	505	LUBW - Altlast: Verdachtsfälle und (nicht gesondert spezifizierte) Einzelfälle	2000-01-01 00:00:00
433	08	602	LUBW - H-3 in Papier und Verpackungsmaterial	2000-01-01 00:00:00
434	08	603	LUBW - Überpr. ext. Messstellen	2000-01-01 00:00:00
435	08	604	LUBW - Messungen in Unis/Forschungseinrichtungen	2000-01-01 00:00:00
438	08	607	LUBW - Proben aus Gewerbe- und Wirtschaftsbetrieben	2000-01-01 00:00:00
439	08	608	LUBW - Messungen Gelände KTA ( ausser FZ-KA u. WAK)	2000-01-01 00:00:00
440	08	609	LUBW - Kontaminationskontrolle, Arbeitsplatzüberwachung	2000-01-01 00:00:00
441	08	650	LUBW - Inkorporationsmessprogramme	2000-01-01 00:00:00
442	08	701	LUBW - KFÜ Baden-Württemberg	2000-01-01 00:00:00
455	08	314	N - Strahlenpegelmessnetz RAM	2000-01-01 00:00:00
456	08	332	Messstation Obrigheim 404 	2000-01-01 00:00:00
475	06	001	Routinemessprogramm	2000-01-01 00:00:00
495	13	Z01	ZLN - REI	2000-01-01 00:00:00
556	12	G10	2. Eintrag G10 bezieht sich auf die gleiche Probe	2000-01-01 00:00:00
557	12	G19	2. Eintrag G19 bezieht sich auf die gleiche Probe	2000-01-01 00:00:00
559	12	G17	2. Eintrag G17 bezieht sich auf die gleiche Probe	2000-01-01 00:00:00
560	12	G16	2. Eintrag G16 bezieht sich auf die gleiche Probe	2000-01-01 00:00:00
561	12	G13	2. Eintrag G13 bezieht sich auf die gleiche Probe	2000-01-01 00:00:00
562	12	G11	2. Eintrag G11 bezieht sich auf die gleiche Probe	2000-01-01 00:00:00
563	12	G15	2. Eintrag G15 bezieht sich auf die gleiche Probe	2000-01-01 00:00:00
564	12	G01	2. Eintrag G01 bezieht sich auf die gleiche Probe	2000-01-01 00:00:00
565	12	G09	2. Eintrag G09 bezieht sich auf die gleiche Probe	2000-01-01 00:00:00
576	07	103	KKW-Überwachung Cattenom LUWG 1	2000-01-01 00:00:00
616	08	423	Wildmonitorring	2000-01-01 00:00:00
617	08	424	Wild - Freimessung von Ort	2000-01-01 00:00:00
618	08	251	KTA - UW bei KIT - CN	2000-01-01 00:00:00
619	08	252	KTA - UW bei KWO	2000-01-01 00:00:00
620	08	253	KTA - UW bei GKN	2000-01-01 00:00:00
621	08	254	KTA - UW bei KKP	2000-01-01 00:00:00
622	08	255	KTA - UW bei KKB, KKL und PSI	2000-01-01 00:00:00
623	08	256	KTA - UW bei DKFZ	2000-01-01 00:00:00
624	08	260	KTA - UW bei KKW FSH	2000-01-01 00:00:00
676	19	111	Test-Programm für CRQ-2981	2000-01-01 00:00:00
696	19	122	Test-Programm für CRQ-2828	2000-01-01 00:00:00
716	06	LHL	Lebensmittelüberwachung	2000-01-01 00:00:00
736	07	104	KKW-Überwachung Mühlheim-Kärlich-LUWG 1	2000-01-01 00:00:00
756	07	302	KKW Überwachung Biblis ILCSP	2000-01-01 00:00:00
757	07	303	KKW-Überwachung Cattenom ILCSP	2000-01-01 00:00:00
758	07	304	KKW-Überwachung Mühlheim-Kärlich ILCSP	2000-01-01 00:00:00
759	07	305	KKW-Überwachung Philippsburg ILCSP	2000-01-01 00:00:00
760	07	101	Ellweiler-Überwachung LUWG 1	2000-01-01 00:00:00
761	07	102	KKW-Überwachung Biblis LUWG 1	2000-01-01 00:00:00
762	07	105	KKW-Überwachung Philippsburg LUWG 1	2000-01-01 00:00:00
776	12	G29	für Sr-90 Bestimmung Quartalsmischprobe aus 14tägigen Stichproben  G29 ziehen	2000-01-01 00:00:00
796	08	270	KTA - KKW FSH, REI-Zusatzmessungen	2000-01-01 00:00:00
797	08	266	KTA - DKFZ, REI-Zusatzmessungen	2000-01-01 00:00:00
798	08	265	KTA - KKB, KKl und PSI, REI-Zusatzmessungen	2000-01-01 00:00:00
799	08	264	KTA - KKP, REI-Zusatzmessungen	2000-01-01 00:00:00
800	08	263	KTA - GKN, REI-Zusatzmessungen	2000-01-01 00:00:00
801	08	262	KTA - KWO,REI-Zusatzmessungen	2000-01-01 00:00:00
802	08	261	KTA - KIT - CN, REI-Zusatzmessungen	2000-01-01 00:00:00
803	08	280	KTA - KKW FSH,Messungen außerhalb der REI	2000-01-01 00:00:00
804	08	276	KTA - DKFZ, Messungen außerhalb der REI	2000-01-01 00:00:00
805	08	275	KTA - KKB, KKL und PSI, Messungen außerhalb der REI	2000-01-01 00:00:00
806	08	274	KTA - KKP, Messungen außerhalb der REI	2000-01-01 00:00:00
807	08	273	KTA - GKN, Messungen außerhalb der REI	2000-01-01 00:00:00
809	08	271	KTA - KIT - CN, Messungen außerhalb der REI	2000-01-01 00:00:00
429	08	503	LUBW - Altlast: Fa. Kienzle, Schwenningen	2000-01-01 00:00:00
515	08	422	Messungen der Fachhochschule Ravensburg	2000-01-01 00:00:00
535	03	309	sonstiges aus Cuxhaven (Exporte)	2000-01-01 00:00:00
536	03	306	Fisch, Problemgebiete	2000-01-01 00:00:00
538	03	203	Sonderprogramm Importe	2000-01-01 00:00:00
539	03	202	Warenkorb-Analyse	2000-01-01 00:00:00
596	08	998	Übung - Kronos	2000-01-01 00:00:00
636	08	333	Wildmessungen (CVUA)	2000-01-01 00:00:00
637	08	334	Wildmessungen (Überprüfung von Messwerten der Eigenkontrolle durch CVUA)	2000-01-01 00:00:00
763	07	108	Spontanproben LUWG 1	2000-01-01 00:00:00
566	12	G20	2. Eintrag G20 bezieht sich auf die gleiche Probe	2000-01-01 00:00:00
567	12	G25	2. Eintrag G25 bezieht sich auf die gleiche Probe	2000-01-01 00:00:00
1	03	110	Sonderprogramm Wildpilze u. Wildbeeren	2000-01-01 00:00:00
3	03	115	Sonderprogramm Bioindikatoren	2000-01-01 00:00:00
4	03	120	Sonderprogramm Trinkwasser (allg.)	2000-01-01 00:00:00
5	03	121	Sonderprogramm Trinkwasser aus Helmstedt	2000-01-01 00:00:00
7	03	140	Sonderprogramm Humanmilch	2000-01-01 00:00:00
8	03	150	Technologieprogramm Konserven- u. Saftindustrie	2000-01-01 00:00:00
9	03	190	Überwachung Kerntechn. Anlage	2000-01-01 00:00:00
10	03	191	Umgebungsüberwachung KTA Grohnde	2000-01-01 00:00:00
12	03	208	Privatproben	2000-01-01 00:00:00
13	03	210	Sonderprogramm Milchmonitoring	2000-01-01 00:00:00
14	03	211	Sonderprogramm Wiederkäuerleber	2000-01-01 00:00:00
16	03	250	Technologieprogramm Fleischverarbeitung	2000-01-01 00:00:00
17	03	251	Technologieprogramm Molkereien	2000-01-01 00:00:00
18	03	310	Sonderprogramm Fisch und Weichtiere	2000-01-01 00:00:00
20	03	350	Technologieprogramm Mühlenbetriebe	2000-01-01 00:00:00
21	03	399	Auftragsuntersuchungen (Private Aufträge)	2000-01-01 00:00:00
22	03	430	Sonderprogramm Hafenimporte	2000-01-01 00:00:00
23	03	441	Sonderprogramm Arzneimittel u. -rohstoffe	2000-01-01 00:00:00
25	03	540	Sonderprogramm Transfer Boden-Pflanze	2000-01-01 00:00:00
26	03	550	Technologie Zuckerindustrie	2000-01-01 00:00:00
27	05	001	Erzeugerstufe	2000-01-01 00:00:00
28	05	002	Handelsstufe	2000-01-01 00:00:00
29	05	003	Sondermessung Wild	2000-01-01 00:00:00
31	05	005	Zollprobe	2000-01-01 00:00:00
32	05	999	Sondermessung ohne Angabe	2000-01-01 00:00:00
33	07	201	Ellweiler-Überwachung	2000-01-01 00:00:00
35	07	203	KKW-Überwachung Cattenom	2000-01-01 00:00:00
36	07	204	KKW-Überwachung Mülheim-Kärlich	2000-01-01 00:00:00
37	07	205	KKW-Überwachung Philippsburg	2000-01-01 00:00:00
38	07	206	Erzeugerproben Land	2000-01-01 00:00:00
39	07	207	Handelsproben Land	2000-01-01 00:00:00
40	07	208	Spontanproben Land	2000-01-01 00:00:00
41	07	306	Erzeugerproben  Land ILCSP	2000-01-01 00:00:00
42	07	307	Handelsproben Land ILCSP	2000-01-01 00:00:00
43	07	308	Spontanproben ILCSP	2000-01-01 00:00:00
44	07	401	Landesprogramm Wasser	2000-01-01 00:00:00
45	07	402	KKW-Überwachung Biblis	2000-01-01 00:00:00
46	07	403	KKW-Überwachung Cattenom	2000-01-01 00:00:00
47	07	404	KKW-Überwachung Mülheim-Kärlich	2000-01-01 00:00:00
48	07	405	KKW-Überwachung Philippsburg	2000-01-01 00:00:00
49	08	999	Übung Sept. 2003	2000-01-01 00:00:00
50	09	003	Müllverbrennung Reststoffe (LfU)	2000-01-01 00:00:00
51	09	005	Gesamtnahrung (LUA-S, LUA-N)	2000-01-01 00:00:00
52	09	006	Sondermeßprogramm Klärschlamm (LGA, GSF, LfU)	2000-01-01 00:00:00
53	09	007	Luft und Niederschlag (LfU, LGA)	2000-01-01 00:00:00
54	09	008	Sammelmilch (LUA-S, LUA-N)	2000-01-01 00:00:00
55	09	009	Boden, Bewuchs, Milch, In-Situ-Messungen an DWD-Stationen (LUA-S, LUA-N, LBP, LfU)	2000-01-01 00:00:00
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
84	13	001	Sonderprogramm Seen Mecklenburg-Vorpommern	2000-01-01 00:00:00
85	13	002	Grundwasser Mecklenburg-Vorpommern	2000-01-01 00:00:00
86	13	003	Programm Pilze Mecklenburg-Vorpommern	2000-01-01 00:00:00
87	13	004	Proben des Landwirtschaftsministeriums	2000-01-01 00:00:00
88	13	005	Wald M-V (Blätter, Nadeln, zugehöriger Boden etc.)	2000-01-01 00:00:00
89	13	006	Programm Boden Mecklenburg-Vorpommern	2000-01-01 00:00:00
90	13	007	Messprogramm Katastrophenschutz	2000-01-01 00:00:00
91	13	008	Sonstige Proben	2000-01-01 00:00:00
92	13	009	Strandaufspülung Lubmin	2000-01-01 00:00:00
93	15	A	Spontanproben Land gemäß IMIS Übung	2000-01-01 00:00:00
94	08	301	IMIS - Routinemessprogramm	2000-01-01 00:00:00
95	09	093	Landesmessprogramm  093	2000-01-01 00:00:00
96	09	094	Dauerbeobachtungsparzellen Boden (GLA, FVA, LBP) 	2000-01-01 00:00:00
97	09	095	In-situ-Spektrometrie (100 Messpunkte - 20 pro Jahr)	2000-01-01 00:00:00
98	09	098	Landesmessprogramm  098	2000-01-01 00:00:00
368	08	103	Q - Alpha-Spektrometer	2000-01-01 00:00:00
369	08	104	Q - Beta-Messplätze	2000-01-01 00:00:00
371	08	106	Q - Gamma-Spektrometer im Labor: Kalibrierung	2000-01-01 00:00:00
372	08	107	Q - Gamma-Spektrometer in Stationen	2000-01-01 00:00:00
195	06	A	Niederschlag	2000-01-01 00:00:00
366	08	101	Q - Gesamt-Alpha-Messplätze	2000-01-01 00:00:00
375	08	110	Q - Gamma-Spektrometer im Labor: Qualität.	2000-01-01 00:00:00
380	08	203	LUBW - UW bei GKN	2000-01-01 00:00:00
387	08	210	LUBW - UW bei KKW FSH	2000-01-01 00:00:00
388	08	302	IMIS - Zusatzmessungen	2000-01-01 00:00:00
390	08	308	N - Bodenproben in-situ-Messungen	2000-01-01 00:00:00
391	08	309	N - Farne 	2000-01-01 00:00:00
395	08	313	N - Aerosolüberwachung im RAM	2000-01-01 00:00:00
398	08	323	Messstation Kehl 	2000-01-01 00:00:00
399	08	324	Messstation Ravensburg 	2000-01-01 00:00:00
400	08	325	Messstation Sigmaringen (abgebaut) 	2000-01-01 00:00:00
401	08	326	Messstation Tauberbischofsheim 	2000-01-01 00:00:00
404	08	329	Messstation Bremgarten 	2000-01-01 00:00:00
409	08	403	LUBW - Referenzmessorte	2000-01-01 00:00:00
412	08	406	LUBW - natürliche Radionuklide	2000-01-01 00:00:00
413	08	407	LUBW - Kontaminationsmessprogramme	2000-01-01 00:00:00
421	08	415	LUBW - Hausmülldeponie	2000-01-01 00:00:00
422	08	416	LUBW - J-131 in Verbrennungsanlagen	2000-01-01 00:00:00
424	08	418	LUBW - Deponie Tuningen	2000-01-01 00:00:00
426	08	420	LUBW - Proben aus dem Ausland u. Importproben	2000-01-01 00:00:00
432	08	601	LUBW - Umgang mit Gluehstrümpfen	2000-01-01 00:00:00
436	08	605	LUBW - Messungen in medizinischen Betrieben	2000-01-01 00:00:00
437	08	606	LUBW - Messungen im Gelände FZ-Karlsruhe und WAK	2000-01-01 00:00:00
443	08	702	LUBW - sonstige Messnetze	2000-01-01 00:00:00
444	08	710	LUBW - Emissionsüberwachung	2000-01-01 00:00:00
496	13	Z02	ZLN - Störfalltraining	2000-01-01 00:00:00
497	13	Z03	ZLN - EIG	2000-01-01 00:00:00
\.


--
-- Name: messprogramm_kategorie_id_seq; Type: SEQUENCE SET; Schema: stammdaten; Owner: postgres
--

SELECT pg_catalog.setval('messprogramm_kategorie_id_seq', 968, true);


--
-- PostgreSQL database dump complete
--
