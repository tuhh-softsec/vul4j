\set ON_ERROR_STOP on
--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.2
-- Dumped by pg_dump version 9.6.2

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = stammdaten, pg_catalog;

--
-- Data for Name: probenehmer; Type: TABLE DATA; Schema: stammdaten; Owner: postgres
--

COPY probenehmer (id, netzbetreiber_id, prn_id, bearbeiter, bemerkung, betrieb, bezeichnung, kurz_bezeichnung, ort, plz, strasse, telefon, tp, typ, letzte_aenderung) FROM stdin;
566	14	352	\N	\N	\N	alt - LÜVA Stollberg	LÜVA STL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
567	14	353	\N	\N	\N	alt - LÜVA Torgau-Oschatz	LÜVA TO	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
569	14	355	\N	\N	\N	alt - LÜVA Weißeritzkreis	LÜVA DW	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
570	14	356	\N	\N	\N	alt - LÜVA Zwickau	LÜVA Z	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
571	14	357	\N	\N	\N	alt - LÜVA Zwickauer Land	LÜVA ZL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
572	14	900	\N	\N	\N	externer Probenehmer	extern	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
573	14	999	\N	\N	\N	keine echte Probenahme	keiner	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
574	15	001	\N	\N	\N	LAU Halle, Landesmeßstelle Süd in Halle	Lmst Süd	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
576	15	003	\N	\N	\N	ERAM in Morsleben	ERAM	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
577	15	101	\N	\N	\N	Staatliches Amt für Umweltschutz, Magdeburg	STAU MD	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
578	15	102	\N	\N	\N	Landesveterinär- und Lebensmitteluntersuchungsamt Stendal	LVLUA SDL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
60	06	001	\N	\N	\N	HLUG - Dezernat W2, Probenahme	HLUG-W2 WI	Wiesbaden	65203	Rheingaustraße 186	0611-6939-0	\N	\N	2013-02-20 08:30:04
61	06	002	\N	\N	\N	HLUG - Dezernat I5, Darmstadt	HLUG-I5 DA	Darmstadt	64293	Kasinostraße 60	06151-9279-0	\N	\N	2013-02-20 08:43:50
579	08	031	\N	\N	\N	Universität Hohenheim, Institut für Physik und Meteorologie	Uni_HH	Stuttgart	70599	Garbenstr. 30	(0711) 459-2150	\N	\N	2000-01-01 00:00:00
1	01	001	\N	\N	\N	LUFA, Kiel nach StrVG	LUFA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
2	01	002	\N	\N	\N	HZG, Messstelle  nach StrVG	HZG	\N	\N	\N	\N	\N	\N	2011-05-09 14:04:14
6	01	006	\N	\N	\N	sonstige nach StrVG	sonstige	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
7	01	010	\N	\N	\N	HZG, Meßstelle nach REI	HZG	\N	\N	\N	\N	\N	\N	2011-05-09 14:04:14
8	01	011	\N	\N	\N	KTA HZG	KTA HZG	\N	\N	\N	\N	\N	\N	2011-05-09 14:04:14
9	01	020	\N	\N	\N	LUFA Meßstelle nach REI	LUFA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
65	06	501	\N	vorher PRN_ID 040	\N	Landwirtschaftliche Untersuchungs- und Forschungsanstalt Speyer	LUFA-LMSt5	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
66	06	010	\N	vorher PRN_ID 101	\N	Kernkraftwerk Biblis	KKW-Biblis	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
67	06	LL1	\N	\N	\N	LLH Bildungs- und Beratungszentrum Griesheim	LLH Grshm	Griesheim	64347	Pfützenstraße 67	06155-79800-0	\N	\N	2000-01-01 00:00:00
68	06	10	\N	\N	\N	Lahn-Dill Kreis - HA Amt f.d.ländl. Raum	AlR Wetzla	Wetzlar	35573	Karl-Kellner-Ring 51	06441-407-0	\N	\N	2016-09-23 13:18:13
69	06	11	\N	\N	\N	LK Limburg-Weilburg - HA Amt f.d.ländl. Raum	AlR Limbur	Limburg a,d, Lahn	65549	Schiede 43	06431-296-0	\N	\N	2015-10-30 12:25:18
70	06	12	\N	\N	\N	LK Darmstadt-Dieburg - HA Amt f.d. ländl. Raum	AlR DaDieb	Darmstadt	64289	Jägertorstr. 207	06151-881-0/-2090	\N	\N	2015-10-30 12:25:18
71	06	13	\N	\N	\N	Hochtaunuskreis - HA Amt f.d.ländl. Raum	AlR BHomb	Bad Homburg vd Höhe	61289	Ludwig-Erhard-Anlage 1-5	06172-999-0	\N	\N	2015-11-16 11:19:27
72	06	14	\N	\N	\N	Main-Kinzig Kreis - Amt 70 Umwelt, Naturschutz u. ländl. Raum	AlR Gelnha	Gelnhausen	63571	Barbarossastraße 16-24	06051-85156-18/-84	\N	\N	2015-10-30 12:25:18
73	06	15	\N	\N	\N	Werra-Meißner Kreis - HA Amt f.d.ländl. Raum	AlR Eschwe	Eschwege	37269	Schlossplatz 1	05651-302-0	\N	\N	2000-01-01 00:00:00
74	06	16	\N	\N	\N	Vogelsbergkreis - HA Amt f.d.ländl. Raum	AlR Lauter	Lauterbach (Hessen)	36341	Adolf-Spiess-Straße 34	06631-792700	\N	\N	2015-10-30 12:25:18
75	06	17	\N	\N	\N	LK Waldeck-Frankenberg - Fachdienst Landwirtschaft	AlR Korbac	Korbach	34497	Am Lülingskreuz 60	05631-9540	\N	\N	2015-08-26 13:42:26
76	06	18	\N	\N	\N	Odenwaldkreis - Amt für den ländlichen Raum	AlR Reiche	Reichelsheim (Odenwa	64385	Scheffelstraße 11	06164-505-0	\N	\N	2000-01-01 00:00:00
77	06	19	\N	alt	\N	HLVA Kassel	hlva ks	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
78	06	LL2	\N	\N	\N	LLH Gartenbauberatungsschwerpunkt Friedberg	LLH Fb	Friedberg (Hessen)	61169	Homburger Straße 17	06031-83-0	\N	\N	2000-01-01 00:00:00
79	06	20	\N	\N	\N	Schwalm-Eder Kreis - HA Amt f.d.ländl. Raum	AlR Fritzl	Fritzlar	34560	Schladenweg 39	05622-994-0	\N	\N	2000-01-01 00:00:00
80	06	21	\N	\N	\N	LK Marburg-Biedenkopf - HA Amt f.d.ländl. Raum	AlR Marbur	Marburg	35039	Herrmann-Jacobsohn-Weg 1	06421-4056-0	\N	\N	2015-10-22 13:56:22
81	06	22	\N	\N	\N	Wetteraukreis - Fachdienst 4.2 Landwirtschaft	AlR Friedb	Friedberg (Hessen)	61169	Homburger Str. 17	06031-834240	\N	\N	2000-01-01 00:00:00
82	06	23	\N	\N	\N	LK Bergstraße - HA Amt f.d. ländl. Raum	AlR Heppen	Heppenheim (Bergstra	64646	Gräffstraße 5	06252-155103	\N	\N	2015-10-30 12:25:18
83	06	24	\N	alt	\N	HLRL Wetzlar Dezernat 29	hlrl we	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
84	06	25	\N	\N	\N	Fischerei Tümmler, Erfelden	tümmle erf	\N	\N	\N	\N	\N	E	2000-01-01 00:00:00
85	06	26	\N	\N	\N	Fischer- u. Schifferzunft Frankfurt	fische ffm	\N	\N	\N	\N	\N	E	2000-01-01 00:00:00
86	06	27	\N	\N	\N	Angelsportverein Dorlar	asv dorlar	\N	\N	\N	\N	\N	E	2000-01-01 00:00:00
87	06	28	\N	\N	\N	Angelsportverein Marbachsee	asv marbac	\N	\N	\N	\N	\N	E	2000-01-01 00:00:00
88	06	29	\N	\N	\N	Talsperrenfischerei Edersee	tf edersee	\N	\N	\N	\N	\N	E	2000-01-01 00:00:00
89	06	LL3	\N	\N	\N	LLH Gartenbauberatungsschwerpunkt Wiesbaden	LLH Wi	Wiesbaden	65185	Mainzer Straße 17	0611-39236-0	\N	\N	2000-01-01 00:00:00
90	06	30	\N	alt	\N	Hessische Landesanstalt für Umwelt Dezernat 3.1	hlfu 3.1	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
91	06	LL4	\N	\N	\N	LLH Gartenbauberatungsschwerpunkt Wetzlar	LLH Wz	Wetzlar	35578	Schanzenfeldstraße 8	06441-9289-410/200	\N	\N	2000-01-01 00:00:00
92	06	LL5	\N	\N	\N	LLH Gartenbauberatungsschwerpunkt Kassel	LLH Ks	Kassel	34117	Kölnische Straße 48-50	0561-7299-0	\N	\N	2000-01-01 00:00:00
93	06	6	\N	alt	\N	Lehr- und Versuchsgut Groß-Umstadt	lvg gu	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
94	06	7	\N	\N	\N	LK Kassel - Amt für den ländlichen Raum	AlR Hofgei	Hofgeismar	34369	Manteuffel-Anlage 5	05671-8001-0	\N	\N	2000-01-01 00:00:00
95	06	8	\N	\N	\N	LK Hersfeld Rotenburg - Fachdienst ländlicher Raum	AlR Hersf	Bad Hersfeld	36251	Friedloser Straße 17	06621-8722-01	\N	\N	2015-10-30 12:25:18
96	06	9	\N	\N	\N	LK Fulda - Fachdienst Landwirtschaft	AlR Fulda	Fulda	36037	Wörthstraße 15	0661-6006-0	\N	\N	2000-01-01 00:00:00
97	07	101	\N	\N	\N	Landesamt für Umwelt, Wasserwirtschaft und Gewerbeaufsicht Rheinland-Pfalz	LUWG Mainz	\N	\N	\N	\N	\N	\N	2016-07-05 12:56:58
98	07	201	\N	\N	\N	Landesuntersuchungsamt , Institut für Lebensmittelchemie Speyer	ILC Speyer	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
99	07	301	\N	\N	\N	Landesuntersuchungsamt , institut für Lebensmittelchemie Trier	ILC Trier	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
100	07	302	\N	\N	\N	Stadtverwaltung Trier	SVTR	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
101	07	303	\N	\N	\N	Kreisverwaltung Wittlich	KVWIL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
102	07	401	\N	\N	\N	Landesamt für Umwelt Rheinland-Pfalz (Ref.67 - vorher LUWG, LfW)	LfU RLP	\N	\N	\N	\N	\N	\N	2016-03-04 07:11:21
103	07	402	\N	\N	\N	Stadtwerke Trier (Wasserwerk Riveris)	SW TR	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
104	07	501	\N	\N	\N	Landwirtschaftliche Untersuchungs-und Forschungsanstalt Speyer	LUFA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
105	07	405	\N	\N	\N	Kernkraftwerk Philippsburg	KKW Ph	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
111	08	010	\N	\N	\N	Landesanstalt fuer Umwelt, Messungen und Naturschutz Baden-Wuerttemberg	LUBW	Karlsruhe	76187	Hertzstr. 173	(0721) 5600-0	\N	\N	2000-01-01 00:00:00
112	08	020	\N	\N	\N	Chemisches- und Veterinäruntersuchungsamt Stuttgart	CVUA S	Fellbach	70736	Schaflandstr. 2/3	(0711) 957-1234	\N	\N	2000-01-01 00:00:00
113	08	050	\N	\N	\N	Landwirtschaftliche Untersuchungs- u.Forschungsanstalt	LUFA	Karlsruhe	76227	Neßlerstr. 23-31	(0721) 9468-0	\N	\N	2000-01-01 00:00:00
114	08	070	\N	\N	\N	Chemisches- und Veterinäruntersuchungsamt Freiburg 	CVUA FR 	Freiburg	79114	Bissierstr. 5	(0761) 8855-0	\N	\N	2000-01-01 00:00:00
115	09	001	\N	\N	\N	Bayer. Forstliche Versuchs- und Forschungsanstalt	FVA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
116	09	002	\N	\N	\N	Forschungszentrum für Umwelt und Gesundheit	GSF	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
117	09	003	\N	\N	\N	Bayer. Geologisches Landesamt	GLA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
118	09	004	\N	\N	\N	Bayer. Landesamt für Umwelt	LfU	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
119	09	005	\N	\N	\N	Bayer. Landesamt für Umwelt, Außenstelle Kulmbach	LfU-K	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
120	09	006	\N	\N	\N	Bayer. Landesamt für Umwelt Labor für Umweltradioaktivität I	LfU I	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
121	09	007	\N	\N	\N	Bayer. Landesanstalt für Landwirtschaft	LfL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
122	09	008	\N	\N	\N	Bayer. Landesanstalt für Ernährung München (aufgelöst)	LfE-M	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
123	09	009	\N	\N	\N	Bayer. Landesamt für Umwelt Labor für Umweltradioaktivität II	LfU II	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
124	09	013	\N	\N	\N	Landesgewerbeanstalt Bayern, Erlangen	LGA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
125	09	014	\N	\N	\N	Landesgewerbeanstalt Bayern, Würzburg	LGA-W	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
126	09	017	\N	\N	\N	Milchwirtschaftliche Untersuchungs- und Versuchsanstalt	MUVA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
127	09	020	\N	\N	\N	Bayer. Landesamt für Gesundheit und Lebensmittelsicherheit, Oberschleißheim	LGL-O	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
128	09	021	\N	\N	\N	Bayer. Landesamt für Gesundheit und Lebensmittelsicherheit, Erlangen	LGL-E	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
129	09	030	\N	\N	\N	Kernkraftwerk Isar 1	KKI 1	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
130	09	031	\N	\N	\N	Kernkraftwerk Isar 2	KKI 2	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
131	09	032	\N	\N	\N	Kernkraftwerk Grafenrheinfeld	KKG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
132	09	033	\N	\N	\N	Kernkraftwerk Gundremmingen GmbH	KGG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
133	09	034	\N	\N	\N	Versuchsatomkraftwerk Kahl GmbH	VAK	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
134	09	035	\N	\N	\N	Institut für Radiochemie und Forschungsreaktor	TUM-Rad	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
135	09	036	\N	\N	\N	Siemens Brennelementwerk Hanau, Standort Karlstein	SBWK	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
136	09	037	\N	\N	\N	Siemens AG, Standort Karlstein	SAGK	\N	\N	\N	\N	\N	\N	2015-03-03 13:00:24
137	09	038	\N	\N	\N	AREVA GmbH, Standort Erlangen	AREVA 	\N	\N	\N	\N	\N	\N	2015-03-30 08:57:32
138	09	039	\N	\N	\N	Bundesamt für Strahlenschutz, Institut für Strahlenhygiene	BfS/ISH	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
139	09	050	\N	\N	\N	Wasserwirtschaftsamt Amberg	WWA AM	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
140	09	051	\N	\N	\N	Wasserwirtschaftsamt Ansbach	WWA AN	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
141	09	052	\N	\N	\N	Wasserwirtschaftsamt Aschaffenburg	WWA AB	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
142	09	053	\N	\N	\N	Wasserwirtschaftsamt Bamberg	WWA BA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
143	09	054	\N	\N	\N	Wasserwirtschaftsamt Bayreuth	WWA BT	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
144	09	055	\N	\N	\N	Wasserwirtschaftsamt Deggendorf	WWA DEG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
145	09	056	\N	\N	\N	Wasserwirtschaftsamt Donauwörth	WWA DON	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
146	09	057	\N	\N	\N	Wasserwirtschaftsamt Freising	WWA FS	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
147	09	058	\N	\N	\N	Wasserwirtschaftsamt Hof	WWA HO	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
148	09	059	\N	\N	\N	Wasserwirtschaftsamt Ingolstadt	WWA IN	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
149	09	060	\N	\N	\N	Wasserwirtschaftsamt Kempten/Allgäu	WWA KE	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
150	09	061	\N	\N	\N	Wasserwirtschaftsamt Krumbach	WWA KRU	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
151	09	062	\N	\N	\N	Wasserwirtschaftsamt Landshut	WWA LA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
152	09	063	\N	\N	\N	Wasserwirtschaftsamt München	WWA M	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
153	09	064	\N	\N	\N	Wasserwirtschaftsamt Nürnberg	WWA N	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
154	09	065	\N	\N	\N	Wasserwirtschaftsamt Passau	WWA PA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
155	09	066	\N	\N	\N	Straßen- und Wasserbauamt Pfarrkirchen	SWA PAN	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
51	03	738	\N	\N	Institut für Boden und Umwelt	LUFA Nord-West, Institut für Boden und Umwelt, Hameln	LUFA Ham.	Hameln	\N	gibt es nicht mehr !	\N	\N	\N	2000-01-01 00:00:00
52	03	739	\N	\N	Landwirtschaftskammer Niedersachsen	Bezirksstelle Northeim	Bez. North	Northeim	37154	\N	\N	\N	\N	2000-01-01 00:00:00
53	03	740	\N	\N	Landwirtschaftskammer Niedersachsen	Bezirksstelle Nienburg	Bez. Nienb	Nienburg	31582	\N	\N	\N	\N	2000-01-01 00:00:00
54	03	741	\N	\N	Landwirtschaftskammer Niedersachsen	Bezirksstelle Oldenburg-Süd	Bez. Ol-S	Cloppenburg	49661	\N	\N	\N	\N	2000-01-01 00:00:00
55	03	742	\N	\N	Landwirtschaftskammer Niedersachsen	Bezirksstelle Bremervörde	Bez. Bremv	Bremervörde	27432	\N	\N	\N	\N	2000-01-01 00:00:00
56	03	743	\N	\N	Landwirtschaftskammer Niedersachsen	Außenstelle Wesermarsch, Bezirksstelle Oldenburg-Nord	Aust.Wema	Ovelgönne	26939	\N	\N	\N	\N	2000-01-01 00:00:00
57	03	744	\N	\N	Landwirtschaftskammer Niedersachsen	Bezirksstelle Oldenburg-Nord	Bez. OL-N	Westerstede	26655	\N	\N	\N	\N	2000-01-01 00:00:00
58	03	800	\N	\N	\N	800 - 899 reserviert für LAVES Oldenburg	LUA Ol.	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
59	03	900	\N	\N	\N	900 - 999 reserviert für LAVES Cuxhaven	VUA Cux.	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
62	06	101	\N	vorher PRN_ID 010	\N	Landesamt für Umweltschutz, Wasserwirtschaft und Gewerbeaufsicht	LUWG-LMSt1	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
63	06	401	\N	vorher PRN_ID 020	\N	Landesamt für Umweltschutz, Wasserwirtschaft und Gewerbeaufsicht	LUWG-LMSt4	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
64	06	201	\N	vorher PRN_ID 030	\N	Landesuntersuchungsamt, Institut für Lebensmittelchemie Speyer	LUA-LMSt2	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
156	09	067	\N	\N	\N	Wasserwirtschaftsamt Regensburg	WWA R	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
157	09	068	\N	\N	\N	Wasserwirtschaftsamt Rosenheim	WWA RO	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
158	09	069	\N	\N	\N	Wasserwirtschaftsamt Schweinfurt	WWA SW	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
159	09	070	\N	\N	\N	Wasserwirtschaftsamt Traunstein	WWA TS	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
160	09	071	\N	\N	\N	Wasserwirtschaftsamt Weiden	WWA WEN	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
161	09	072	\N	\N	\N	Wasserwirtschaftsamt Weilheim	WWA WM	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
162	09	073	\N	\N	\N	Wasserwirtschaftsamt Würzburg	WWA WÜ	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
163	09	100	\N	\N	\N	Landratsamt Aichach-Friedberg	LRA AIC	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
164	09	101	\N	\N	\N	Landratsamt Altötting	LRA AÖ	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
165	09	102	\N	\N	\N	Landratsamt Amberg-Sulzbach	LRA AS	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
166	09	103	\N	\N	\N	Landratsamt Ansbach	LRA AN	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
167	09	104	\N	\N	\N	Landratsamt Aschaffenburg	LRA AB	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
168	09	105	\N	\N	\N	Landratsamt Augsburg	LRA A	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
169	09	106	\N	\N	\N	Landratsamt Bad Kissingen	LRA KG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
170	09	107	\N	\N	\N	Landratsamt Bad Tölz-Wolfratshausen	LRA TÖL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
171	09	108	\N	\N	\N	Landratsamt Bamberg	LRA BA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
581	07	111	\N	\N	\N	Frau Sans 	LfU Mainz	\N	\N	\N	\N	\N	\N	2016-07-05 12:56:58
582	07	121	\N	\N	\N	Herr Ziß	LfU Mainz	\N	\N	\N	\N	\N	\N	2016-07-05 12:56:58
583	07	141	\N	\N	\N	Frau Marceta	LfU Mainz	\N	\N	\N	\N	\N	\N	2016-07-05 12:56:58
584	07	131	\N	\N	\N	Herr  Steinmetz	LfUMainz	\N	\N	\N	\N	\N	\N	2016-07-05 12:56:58
585	07	403	\N	\N	\N	Wasserwerk St. Sebastian	WW StS	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
666	13	MV1	\N	\N	Landesamt für Umwelt, Naturschutz und Geologie	LUNG M-V	MV1	Güstrow	\N	\N	\N	\N	\N	2000-01-01 00:00:00
707	07	404	\N	\N	\N	Kernkraftwerk Mülheim-Kärlich	KKW MK	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
709	07	406	\N	\N	\N	Bundesanstalt für Gewässerkunde, Koblenz	BafG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
710	07	407	\N	\N	\N	BASF AG, Ludwigshafen	BASF	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
711	07	408	\N	\N	\N	WVE Kaiserslautern	WVE KL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
712	06	061105	\N	\N	\N	Kreis Bergstraße - Amt f. Veterinärwesen u. Verbraucherschutz	AVV Heppen	Heppenheim (Bergst.)	64646	Odenwaldstraße 5	06252-15-5977	\N	O	2015-03-18 11:44:00
713	06	061108	\N	\N	\N	Odenwaldkreis - Abt. f. Veterinärwesen u. Verbraucherschutz	AVV Reichh	Reichelsheim (Odw.)	64385	Scheffelstraße 11	06164-505-1201	\N	O	2015-03-18 12:03:04
714	06	061110	\N	\N	\N	LK Darmstadt-Dieburg - Fachd. 420 Ländl. Raum, Veterinärwesen, Verbraucherschutz	AVV DaDieb	Darmstadt	64295	Rheinstraße 67	06151-95161-0	\N	O	2015-03-18 12:03:04
715	06	061111	\N	\N	\N	Kreis Groß-Gerau - Fachdienst Veterinärwesen, Lebensmittelüb. u. Verbrauchersch.	AVV Groß-G	Groß-Gerau	64521	Wilhelm-Seipp-Straße 4	06152-989-643, -427	\N	O	2012-08-27 11:45:18
716	06	061101	\N	\N	\N	Stadt Darmstadt - Amt f. Veterinärwesen u. Verbraucherschutz	AVV Darmst	Darmstadt	64295	Rheinstraße 67	06151-785 885-0	\N	O	2013-03-26 13:19:49
717	06	061117	\N	\N	\N	Landkreis Limburg-Weilburg - Amt f. Ländl. R., Umwelt, Veterinärw. u. Verbr.sch.	AVV Hadama	Hadamar	65589	Gymnasiumstraße 4	06431-296-0	\N	O	2012-08-27 11:45:18
718	06	061116	\N	\N	\N	Lahn-Dill Kreis - Amt f. Veterinärwesen u. Verbraucherschutz	AVV Herbor	Herborn	35745	Schlossstraße 20	06441-407-7711	\N	O	2013-03-26 13:39:06
719	06	061109	\N	\N	\N	Wetteraukreis - Fachdienst Veterinärwesen u. Verbraucherschutz	AVV Friedb	Friedberg (Hessen)	61169	Ockstädter Straße 3-5	06031-802401/-802402	\N	O	2013-03-26 13:26:48
720	06	061106	\N	\N	\N	Hochtaunuskreis - Fachbereich 60.50 - Veterinärwesen u. Verbraucherschutz	AVV BdHomb	Bad Homburg	61289	Ludwig-Erhard-Anlage 1-5	06172-999-6599	\N	O	2015-11-16 11:19:27
721	06	061102	\N	\N	\N	Stadt Frankfurt - Ordnungsamt, Abt. Veterinärwesen	AVV Frankf	Frankfurt am Main	60326	Kleyerstraße 86	069-212-47099	\N	O	2012-08-27 11:45:18
722	06	061113	\N	\N	\N	Kreis Offenbach - Fachdienst Veterinärwesen u. lebmr. Verbraucherschutz	AVV KrsOff	Dietzenbach	63128	Gottlieb-Daimler-Straße 10	06074 - 8180-63900	\N	O	2015-03-18 12:03:04
723	06	061103	\N	\N	\N	Stadt Offenbach - Amt f. Veterinärwesen u. Verbraucherschutz	AVV Offenb	Offenbach am Main	63065	Berliner Straße 60 -Stadthaus-	069-8065-4910	\N	O	2013-03-26 13:21:26
724	06	061120	\N	\N	\N	Stadt Kassel - Lebensmittelüberwachung und Tiergesundheit	AVV Kassel	Kassel	34117	Kurt Schumacher Straße 31	0561-787-3336	\N	O	2015-03-18 12:03:04
725	06	061126	\N	\N	\N	Landkreis Kassel - Amt f. Veterinärwesen u. Verbraucherschutz	AVV Wolfha	Wolfhagen	34466	Liemeckestraße 2	05692-987-0	\N	O	2012-08-27 11:50:49
726	06	061104	\N	\N	\N	Stadt Wiesbaden - Amt f. Veterinärwesen u. Verbraucherschutz	AVV Wiesb	Wiesbaden	65187	Teutonenstraße 1	0611-89077-0	\N	O	2013-03-26 13:23:13
727	06	061114	\N	\N	\N	Rheingau-Taunus Kreis - RD III.8 Verbraucherschutz u. Veterinärwesen	AVV RhgTns	Bad Schwalbach	65307	Heimbacher Straße 7	06124-510-0	\N	O	2012-08-27 11:50:49
728	06	061112	\N	\N	\N	Main-Taunus Kreis - Amt f. Veterinärwesen u. Verbraucherschutz	AVV Hofhm	Hofheim am Taunus	65719	Am Kreishaus 1-5	06192-201-1312	\N	O	2012-08-27 11:50:49
729	06	061121	\N	\N	\N	Landkreis Fulda - Sachgebiet Veterinärwesen u. Verbraucherschutz	AVV Fulda	Fulda	36037	Wörthstraße 15	0661-6006-0	\N	O	2012-08-27 11:45:18
730	06	061115	\N	\N	\N	Landkreis Gießen - Amt f. Veterinärwesen u. Verbraucherschutz	AVV Gießen	Gießen	35398	Rodheimer Straße 33	0641-9390-6200	\N	O	2013-03-26 13:36:28
731	06	061122	\N	\N	\N	Landkreis Hersfeld-Rothenburg - Fachdienst Veterinärwesen u. Verbraucherschutz	AVV Hersf	Bad Hersfeld	36251	Hubertusweg 19	06621-87-2302	\N	O	2013-03-26 13:44:08
732	06	061107	\N	\N	\N	Main-Kinzig Kreis - Amt f. Veterinärwesen u. Verbraucherschutz	AVV Gelnh	Gelnhausen	63571	Gutenbergstraße 2	06051-85155-10	\N	O	2012-08-27 11:45:18
733	06	061119	\N	\N	\N	Landkreis Marburg-Biedenkopf - Fachdienst Veterinärwesen und Verbraucherschutz	AVV Marbur	Marburg	35039	Herrmann-Jacobsohn-Weg 1	06421-4056-0	\N	O	2015-03-18 12:03:04
734	06	061123	\N	\N	\N	Schwalm-Eder Kreis - Fachbereich 53, Gesundheit und Verbraucherschutz	AVV Homber	Homberg (Efze)	34576	Waßmuthshäuser Str. 52, Geb. 5	05681-775-0	\N	O	2015-03-18 12:03:04
310	09	447	\N	\N	\N	Landwirtschaftsamt Passau-Rotthalmünster	LwA PA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
311	09	448	\N	\N	\N	Landwirtschaftsamt Pfaffenhofen a.d. Ilm	LwA PAF	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
312	09	449	\N	\N	\N	Landwirtschaftsamt Regen	LwA REG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
313	09	450	\N	\N	\N	Landwirtschaftsamt Regensburg	LwA R	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
314	09	451	\N	\N	\N	Landwirtschaftsamt Roth	LwA RH	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
315	09	452	\N	\N	\N	Landwirtschaftsamt Schrobenhausen	LwA ND	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
316	09	453	\N	\N	\N	Landwirtschaftsamt Schweinfurt	LwA SW	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
317	09	454	\N	\N	\N	Landwirtschaftsamt Staffelstein	LwA LIF	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
318	09	455	\N	\N	\N	Landwirtschaftsamt Straubing-Bogen	LwA SR	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
319	09	456	\N	\N	\N	Landwirtschaftsamt Tirschenreuth	LwA TIR	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
320	09	457	\N	\N	\N	Landwirtschaftsamt Traunstein	LwA TS	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
321	09	458	\N	\N	\N	Landwirtschaftsamt Uffenheim	LwA NEA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
322	09	459	\N	\N	\N	Landwirtschaftsamt Waldkirchen	LwA FRG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
323	09	460	\N	\N	\N	Landwirtschaftsamt Wasserburg	LwA RO	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
324	09	461	\N	\N	\N	Landwirtschaftsamt Weiden	LwA WEN	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
325	09	462	\N	\N	\N	Landwirtschaftsamt Weilheim	LwA WM	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
326	09	463	\N	\N	\N	Landwirtschaftsamt Weissenhorn	LwA NU	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
327	09	464	\N	\N	\N	Landwirtschaftsamt Weißenburg	LwA WUG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
328	09	465	\N	\N	\N	Landwirtschaftsamt Wolfratshausen	LwA TÖL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
329	09	466	\N	\N	\N	Landwirtschaftsamt Würzburg	LwA WÜ	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
330	09	467	\N	\N	\N	Landwirtschaftsamt Wunsiedel	LwA WUN	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
331	09	480	\N	\N	\N	Tiergesundheitsdienst Bayern Geschäftsstelle Grub	TGD EBE	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
332	09	481	\N	\N	\N	Tiergesundheitsdienst Bayern Geschäftsstelle Traunstein	TGD TS	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
333	09	482	\N	\N	\N	Tiergesundheitsdienst Bayern Geschäftsstelle Landshut	TGD LA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
334	09	483	\N	\N	\N	Tiergesundheitsdienst Bayern Geschäftsstelle Deggendorf	TGD DEG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
335	09	484	\N	\N	\N	Tiergesundheitsdienst Bayern Geschäftsstelle Weiden	TGD WEN	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
336	09	485	\N	\N	\N	Tiergesundheitsdienst Bayern Geschäftsstelle Nürnberg	TGD N	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
337	09	486	\N	\N	\N	Tiergesundheitsdienst Bayern Geschäftsstelle Kempten	TGD KE	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
338	09	487	\N	\N	\N	Tiergesundheitsdienst Bayern Geschäftsstelle Günzburg	TGD GZ	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
339	09	488	\N	\N	\N	Tiergesundheitsdienst Bayern Geschäftsstelle Regensburg	TGD R	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
340	09	489	\N	\N	\N	Tiergesundheitsdienst Bayern Geschäftsstelle Würzburg	TGD WÜ	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
341	09	500	\N	\N	\N	Staatl. Gesundheitsamt Aichach	GA AIC	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
342	09	501	\N	\N	\N	Staatl. Gesundheitsamt Altötting	GA AÖ	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
343	09	502	\N	\N	\N	Staatl. Gesundheitsamt Amberg	GA AM	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
344	09	503	\N	\N	\N	Staatl. Gesundheitsamt Ansbach	GA AN	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
345	09	504	\N	\N	\N	Staatl. Gesundheitsamt Aschaffenburg	GA AB	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
346	09	505	\N	\N	\N	Staatl. Gesundheitsamt Augsburg	GA A	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
347	09	506	\N	\N	\N	Staatl. Gesundheitsamt Bad Kissingen	GA KG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
348	09	507	\N	\N	\N	Staatl. Gesundheitsamt Bad Neustadt a.d. Saale	GA NES	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
349	09	508	\N	\N	\N	Staatl. Gesundheitsamt Bad-Reichenhall	GA BGL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
350	09	509	\N	\N	\N	Staatl. Gesundheitsamt Bad-Reichenhall -Dienstst. Berchtesg.	GA BGL-B	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
351	09	510	\N	\N	\N	Staatl. Gesundheitsamt Bad-Tölz	GA TÖL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
352	09	511	\N	\N	\N	Staatl. Gesundheitsamt Bamberg	GA BA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
353	09	512	\N	\N	\N	Staatl. Gesundheitsamt Bayreuth	GA BT	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
354	09	513	\N	\N	\N	Staatl. Gesundheitsamt Bayreuth -Dienstst. Pegnitz	GA BT-P	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
355	09	514	\N	\N	\N	Staatl. Gesundheitsamt Cham	GA CHA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
356	09	515	\N	\N	\N	Staatl. Gesundheitsamt Cham -Dienstst. Kötzing	GA CHA-K	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
357	09	516	\N	\N	\N	Staatl. Gesundheitsamt Coburg	GA CO	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
358	09	517	\N	\N	\N	Staatl. Gesundheitsamt Dachau	GA DAH	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
359	09	518	\N	\N	\N	Staatl. Gesundheitsamt Deggendorf	GA DEG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
360	09	519	\N	\N	\N	Staatl. Gesundheitsamt Dillingen a.d. Donau	GA DLG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
361	09	520	\N	\N	\N	Staatl. Gesundheitsamt Dingolfing	GA DGF	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
362	09	521	\N	\N	\N	Staatl. Gesundheitsamt Donauwörth	GA DON	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
363	09	522	\N	\N	\N	Staatl. Gesundheitsamt Donauwörth -Dienstst. Nördlingen	GA DON-N	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
364	09	523	\N	\N	\N	Staatl. Gesundheitsamt Ebersberg	GA EBE	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
365	09	524	\N	\N	\N	Staatl. Gesundheitsamt Eichstätt	GA EI	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
366	09	525	\N	\N	\N	Staatl. Gesundheitsamt Eichstätt -Dienstst. Ingolstadt	GA EI-I	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
367	09	526	\N	\N	\N	Staatl. Gesundheitsamt Erding	GA ED	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
368	09	527	\N	\N	\N	Staatl. Gesundheitsamt Erlangen	GA ER	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
369	09	528	\N	\N	\N	Staatl. Gesundheitsamt Forchheim	GA FO	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
370	09	529	\N	\N	\N	Staatl. Gesundheitsamt Freising	GA FS	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
371	09	530	\N	\N	\N	Staatl. Gesundheitsamt Freyung	GA FRG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
372	09	531	\N	\N	\N	Staatl. Gesundheitsamt Fürstenfeldbruck	GA FFB	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
276	09	413	\N	\N	\N	Landwirtschaftsamt Deggendorf	LwA DEG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
219	09	156	\N	\N	\N	Landratsamt Rhön-Grabfeld	LRA NES	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
220	09	157	\N	\N	\N	Landratsamt Rosenheim	LRA RO	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
221	09	158	\N	\N	\N	Landratsamt Roth	LRA RH	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
222	09	159	\N	\N	\N	Landratsamt Rottal-Inn	LRA PAN	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
223	09	160	\N	\N	\N	Landratsamt Schwandorf	LRA SAD	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
224	09	161	\N	\N	\N	Landratsamt Schweinfurt	LRA SW	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
225	09	162	\N	\N	\N	Landratsamt Starnberg	LRA STA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
226	09	163	\N	\N	\N	Landratsamt Straubing-Bogen	LRA SR	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
227	09	164	\N	\N	\N	Landratsamt Tirschenreuth	LRA TIR	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
228	09	165	\N	\N	\N	Landratsamt Traunstein	LRA TS	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
229	09	166	\N	\N	\N	Landratsamt Unterallgäu	LRA MN	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
230	09	167	\N	\N	\N	Landratsamt Weilheim-Schongau	LRA WM	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
231	09	168	\N	\N	\N	Landratsamt Weißenburg-Gunzenhausen	LRA WUG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
232	09	169	\N	\N	\N	Landratsamt Wunsiedel i. Fichtelgebirge	LRA WUN	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
233	09	170	\N	\N	\N	Landratsamt Würzburg	LRA WÜ	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
234	09	180	\N	\N	\N	Stadt Amberg	ST AM	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
235	09	181	\N	\N	\N	Stadt Ansbach	ST AN	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
236	09	182	\N	\N	\N	Stadt Aschaffenburg	ST AB	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
237	09	183	\N	\N	\N	Stadt Augsburg	ST A	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
238	09	184	\N	\N	\N	Stadt Bamberg	ST BA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
239	09	185	\N	\N	\N	Stadt Bayreuth	ST BT	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
240	09	186	\N	\N	\N	Stadt Coburg	ST CO	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
241	09	187	\N	\N	\N	Stadt Erlangen	ST ER	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
242	09	188	\N	\N	\N	Stadt Fürth	ST FÜ	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
243	09	189	\N	\N	\N	Stadt Hof	ST HO	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
244	09	190	\N	\N	\N	Stadt Ingolstadt	ST IN	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
245	09	191	\N	\N	\N	Stadt Kaufbeuren	ST KF	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
246	09	192	\N	\N	\N	Stadt Kempten	ST KE	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
247	09	193	\N	\N	\N	Stadt Landshut	ST LA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
248	09	194	\N	\N	\N	Stadt Memmingen	ST MM	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
249	09	195	\N	\N	\N	Landeshauptstadt München	LHS M	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
250	09	196	\N	\N	\N	Stadt Nürnberg	ST N	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
251	09	197	\N	\N	\N	Stadt Passau	ST PA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
252	09	198	\N	\N	\N	Stadt Regensburg	ST R	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
253	09	199	\N	\N	\N	Stadt Rosenheim	ST RO	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
254	09	200	\N	\N	\N	Stadt Schwabach	ST SC	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
255	09	201	\N	\N	\N	Stadt Schweinfurt	ST SW	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
256	09	202	\N	\N	\N	Stadt Straubing	ST SR	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
257	09	203	\N	\N	\N	Stadt Weiden i.d.Opf.	ST WEN	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
258	09	204	\N	\N	\N	Stadt Würzburg	ST WÜ	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
259	09	250	\N	\N	\N	Stadtwerke Nürnberg	STW N	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
260	09	251	\N	\N	\N	Stadtwerke München	STW M	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
261	09	252	\N	\N	\N	Stadtwerke Rosenheim	STW RO	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
262	09	253	\N	\N	\N	Stadtwerke Landshut	STW LA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
263	09	400	\N	\N	\N	Landwirtschaftsamt Abensberg	LwA KEH	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
264	09	401	\N	\N	\N	Landwirtschaftsamt Altötting	LwA AÖ	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
265	09	402	\N	\N	\N	Landwirtschaftsamt Amberg	LwA AM	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
266	09	403	\N	\N	\N	Landwirtschaftsamt Ansbach	LwA AN	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
267	09	404	\N	\N	\N	Landwirtschaftsamt Aschaffenburg	LwA AB	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
268	09	405	\N	\N	\N	Landwirtschaftsamt Bad Kissingen	LwA KG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
269	09	406	\N	\N	\N	Landwirtschaftsamt Augsburg	LwA A	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
270	09	407	\N	\N	\N	Landwirtschaftsamt Bad Neustadt a.d. Saale	LwA NES	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
271	09	408	\N	\N	\N	Landwirtschaftsamt Bamberg	LwA BA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
272	09	409	\N	\N	\N	Landwirtschaftsamt Bayreuth	LwA BT	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
273	09	410	\N	\N	\N	Landwirtschaftsamt Cham	LwA CHA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
274	09	411	\N	\N	\N	Landwirtschaftsamt Coburg	LwA CO	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
275	09	412	\N	\N	\N	Landwirtschaftsamt Dachau	LwA DAH	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
290	09	427	\N	\N	\N	Landwirtschaftsamt Kempten	LwA KE	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
291	09	428	\N	\N	\N	Landwirtschaftsamt Kitzingen	LwA KT	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
292	09	429	\N	\N	\N	Landwirtschaftsamt Kronach	LwA KC	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
293	09	430	\N	\N	\N	Landwirtschaftsamt Krumbach	LwA GZ	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
294	09	431	\N	\N	\N	Landwirtschaftsamt Kulmbach	LwA KU	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
295	09	432	\N	\N	\N	Landwirtschaftsamt Landau a.d. Isar	LwA DGF	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
296	09	433	\N	\N	\N	Landwirtschaftsamt Landsberg	LwA LL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
297	09	434	\N	\N	\N	Landwirtschaftsamt Landshut	LwA LA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
298	09	435	\N	\N	\N	Landwirtschaftsamt Laufen	LwA BGL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
299	09	436	\N	\N	\N	Landwirtschaftsamt Lauingen	LwA DLG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
300	09	437	\N	\N	\N	Landwirtschaftsamt Lindau	LwA LI	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
301	09	438	\N	\N	\N	Landwirtschaftsamt Miesbach	LwA MB	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
302	09	439	\N	\N	\N	Landwirtschaftsamt Mindelheim	LwA MN	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
303	09	440	\N	\N	\N	Landwirtschaftsamt Moosburg	LwA FS	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
304	09	441	\N	\N	\N	Landwirtschaftsamt Mühldorf	LwA MÜ	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
305	09	442	\N	\N	\N	Landwirtschaftsamt Münchberg	LwA HO	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
306	09	443	\N	\N	\N	Landwirtschaftsamt München	LwA M	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
307	09	444	\N	\N	\N	Landwirtschaftsamt Nördlingen	LwA DON	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
308	09	445	\N	\N	\N	Landwirtschaftsamt Nabburg	LwA SAD	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
309	09	446	\N	\N	\N	Landwirtschaftsamt Neumarkt	LwA NM	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
388	09	547	\N	\N	\N	Staatl. Gesundheitsamt Marktoberdorf	GA OAL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
389	09	548	\N	\N	\N	Staatl. Gesundheitsamt Marktoberdorf -Dienstst. Kaufbeuren	GA OAL-K	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
390	09	549	\N	\N	\N	Staatl. Gesundheitsamt Miesbach	GA MB	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
391	09	550	\N	\N	\N	Staatl. Gesundheitsamt Miltenberg	GA MIL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
392	09	551	\N	\N	\N	Staatl. Gesundheitsamt Miltenberg -Dienstst. Obernburg	GA MIL-O	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
393	09	552	\N	\N	\N	Staatl. Gesundheitsamt Mindelheim	GA MN	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
394	09	553	\N	\N	\N	Staatl. Gesundheitsamt Mindelheim -Dienstst. Memmingen	GA MN-M	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
395	09	554	\N	\N	\N	Staatl. Gesundheitsamt Mühldorf	GA MÜ	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
396	09	555	\N	\N	\N	Staatl. Gesundheitsamt München	GA M	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
397	09	556	\N	\N	\N	Gesundheitsbehörde LHS München	GB M	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
398	09	557	\N	\N	\N	Staatl. Gesundheitsamt Neu-Ulm	GA NU	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
399	09	558	\N	\N	\N	Staatl. Gesundheitsamt Neuburg a.d. Donau	GA ND	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
400	09	559	\N	\N	\N	Staatl. Gesundheitsamt Neumarkt	GA NM	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
401	09	560	\N	\N	\N	Staatl. Gesundheitsamt Neustadt a.d. Aisch	GA NEA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
402	09	561	\N	\N	\N	Staatl. Gesundheitsamt Passau	GA PA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
403	09	562	\N	\N	\N	Staatl. Gesundheitsamt Pfaffenhofen a.d. Ilm	GA PAF	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
404	09	563	\N	\N	\N	Staatl. Gesundheitsamt Pfarrkirchen	GA PAN	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
405	09	564	\N	\N	\N	Staatl. Gesundheitsamt Regen	GA REG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
406	09	565	\N	\N	\N	Staatl. Gesundheitsamt Regensburg	GA R	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
407	09	566	\N	\N	\N	Staatl. Gesundheitsamt Rosenheim	GA RO	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
408	09	567	\N	\N	\N	Staatl. Gesundheitsamt Roth	GA RH	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
409	09	568	\N	\N	\N	Staatl. Gesundheitsamt Roth -Dienstst. Hiltpoltstein	GA RH-H	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
410	09	569	\N	\N	\N	Staatl. Gesundheitsamt Schwabach	GA SC	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
411	09	570	\N	\N	\N	Staatl. Gesundheitsamt Schwandorf	GA SAD	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
412	09	571	\N	\N	\N	Staatl. Gesundheitsamt Schwandorf -Dienstst. Nabburg	GA SAD-N	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
413	09	572	\N	\N	\N	Staatl. Gesundheitsamt Schweinfurt	GA SW	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
414	09	573	\N	\N	\N	Staatl. Gesundheitsamt Sonthofen	GA OA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
415	09	574	\N	\N	\N	Staatl. Gesundheitsamt Sonthofen -Dienstst. Kempten	GA OA-K	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
416	09	575	\N	\N	\N	Staatl. Gesundheitsamt Starnberg	GA STA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
417	09	576	\N	\N	\N	Staatl. Gesundheitsamt Straubing	GA SR	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
418	09	577	\N	\N	\N	Staatl. Gesundheitsamt Tirschenreuth	GA TIR	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
420	09	579	\N	\N	\N	Staatl. Gesundheitsamt Weiden	GA WEN	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
421	09	580	\N	\N	\N	Staatl. Gesundheitsamt Weilheim	GA WM	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
422	09	581	\N	\N	\N	Staatl. Gesundheitsamt Weißenburg	GA WUG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
423	09	582	\N	\N	\N	Staatl. Gesundheitsamt Wunsiedel	GA WUN	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
424	09	583	\N	\N	\N	Staatl. Gesundheitsamt Würzburg	GA WÜ	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
425	09	900	\N	\N	\N	Fernwasserversorgung Franken	FWF	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
426	09	901	\N	\N	\N	Fernwasserversorgung Oberfranken	FWO	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
428	09	940	\N	\N	\N	Städt. Krankenhaus München Bogenhausen	KH M-BOG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
429	09	941	\N	\N	\N	Bayer. Bereitschaftspolizei Nürnberg	BBP N	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
430	09	950	\N	\N	\N	Zweckverband Müllverbrennungsanlage Ingolstadt	ZV MVA IN	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
431	09	951	\N	\N	\N	Zweckverband Müllkraftwerk Schwandorf	ZV MKW SAD	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
432	09	952	\N	\N	\N	Zweckverband Müllverbrennungsanlage Kempten	ZV MVA KE	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
433	09	953	\N	\N	\N	Zweckverband Müllheizkraftwerk Würzburg	ZV MHKW WÜ	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
434	09	954	\N	\N	\N	Zweckverband Müllheizkraftwerk Bamberg	ZV MHKW BA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
435	09	955	\N	\N	\N	Zweckverband Klärwerk Steinhäule (Neu-Ulm)	ZV KW STEI	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
436	09	956	\N	\N	\N	Hausmülldeponie Außernzell	HMD Außern	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
437	09	957	\N	\N	\N	Abfallwirtschaftsverband Isar-Inn	AWV Isar-I	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
438	09	958	\N	\N	\N	Zweckverband zur Abfallbeseitigung Ansbach Stadt und Landkreis Ansbach	ZV zA AN	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
439	09	959	\N	\N	\N	Fa. Großraummülldeponie Gallenbach	GMD Gallen	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
440	09	960	\N	\N	\N	Mülldeponie KG Fa. Bär	MD KG Bär	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
441	09	970	\N	\N	\N	Vereinigte Aluminiumwerke Nabwerke (Schwandorf)	VAW NABW	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
442	09	990	\N	\N	\N	Pilzverein Augsburg	PKV A	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
443	09	991	\N	\N	\N	Pilzkundlicher Arbeitskreis Coburg	PKV CO	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
444	09	992	\N	\N	\N	Pilzverein München	PKV M	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
445	09	993	\N	\N	\N	Pilzverein Nürnberg	PKV N	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
446	09	994	\N	\N	\N	Pilzkundlicher Arbeitskreis Weiden i.d.Opf.	PKV WEN	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
447	09	998	\N	\N	\N	Probenehmer nicht codiert oder festgelegt	Proben.	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
448	09	999	\N	\N	\N	Probenehmer aus Datenschutzgründen anonymisiert	DS	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
449	12	AAS	\N	\N	\N	Amt für Arbeitsschutz	AAS	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
450	12	AFI	\N	\N	\N	Amt für Immissionsschutz	AFI	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
451	12	AFW	\N	\N	\N	Amt für Forstwirtschaft	Forstamt	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
452	12	AKS	\N	\N	\N	Aqua-Kommunal-Service FfO	Aqua-K-S	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
453	12	BPL	\N	\N	\N	Baustoffprüflabor	Baust.prüf	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
454	12	EM	\N	\N	\N	eigene Messung	eigMessung	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
455	12	EP	\N	\N	\N	eigene Probenahme - ungültig !!!	eigPrnahme	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
456	12	ERZ	\N	\N	\N	Erzeuger	Erzeuger	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
457	12	FA	\N	\N	\N	Firma entspr. Adreß-Code	Firma	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
458	12	GVA	\N	\N	\N	Grenzveterinäramt	Grenzv.amt	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
459	12	H4	\N	\N	\N	s. Q4 (10/97)	Hauptlabor	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
460	12	KH	\N	\N	\N	Krankenhaus Oranienburg	KH Obg	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
461	12	KKR	\N	\N	\N	KKW Rheinsberg	KKR	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
462	12	LUA	\N	\N	\N	Mitarbeiter LUA Abt.S	LUA/S	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
463	12	MIW	\N	\N	\N	Milchwerk, Molkerei	Milchwerk	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
464	12	MLU	\N	\N	\N	Milchwirtschaftliche Untersuchungsanstalt Oranienburg	MLUA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
465	12	NDF	\N	\N	\N	Landesmessstelle Neuendorf	LMST Ndf	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
466	12	NUK	\N	\N	\N	Nuklearmedizin Cottbus	Nukl.med.	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
467	12	OBG	\N	\N	\N	Landesmessstelle Oranienburg	LMST Obg	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
468	12	PB	\N	\N	\N	Pilzberater - ungültig !!!	Pilzberat.	\N	\N	\N	\N	\N	\N	2016-10-27 11:49:28
469	12	PSD	\N	\N	\N	Pflanzenschutzdienst	Pfl.schutz	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
470	12	PWA	\N	\N	\N	Potsdamer Wasser- und Abwasserunternehmen	Pdm. W/A	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
471	12	Q3	\N	\N	\N	Bodenanalytik Dr. Süssenbach	Boden/Abfa	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
472	12	Q4	\N	\N	\N	Hauptlabor Frankfurt, Q4	Gewä-Überw	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
473	12	REP	\N	\N	\N	Referat Fleischhygiene Perleberg	Fl.hygiene	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
474	12	VAC	\N	\N	\N	Veterinäramt Cottbus	Veter.amt	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
475	12	VLA	\N	\N	\N	Veterinär- und Lebensmittelüberwachungsamt	Vet/Lbm.üw	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
476	13	MV2	\N	\N	Landesamt für Landwirtschaft, Lebensmittelsicherheit und Fischerei	LALLF M-V	MV2	Rostock	\N	\N	\N	\N	\N	2000-01-01 00:00:00
477	14	010	\N	\N	\N	UBG 1.LMSt/FB 20	1.LMSt	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
478	14	011	\N	\N	\N	Wismut - 1.LMSt als Abholer	Wis_21	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
479	14	012	\N	\N	\N	VKTA - 1.LMSt als Abholer	VKTA_21	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
480	14	013	\N	\N	\N	ext. Probenehmer 1.LMSt als Abholer	ext_21	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
373	09	532	\N	\N	\N	Staatl. Gesundheitsamt Fürth	GA FÜ	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
374	09	533	\N	\N	\N	Staatl. Gesundheitsamt Garmisch-Partenkirchen	GA GAP	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
375	09	534	\N	\N	\N	Staatl. Gesundheitsamt Günzburg	GA GZ	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
376	09	535	\N	\N	\N	Staatl. Gesundheitsamt Haßfurt	GA HAS	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
377	09	536	\N	\N	\N	Staatl. Gesundheitsamt Hof	GA HO	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
378	09	537	\N	\N	\N	Staatl. Gesundheitsamt Hof -Dienstst. Münchberg	GA HO-M	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
379	09	538	\N	\N	\N	Staatl. Gesundheitsamt Karlstadt	GA MSP	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
380	09	539	\N	\N	\N	Staatl. Gesundheitsamt Kelheim	GA KEH	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
381	09	540	\N	\N	\N	Staatl. Gesundheitsamt Kitzingen	GA KT	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
382	09	541	\N	\N	\N	Staatl. Gesundheitsamt Kronach	GA KC	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
383	09	542	\N	\N	\N	Staatl. Gesundheitsamt Kulmbach	GA KU	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
384	09	543	\N	\N	\N	Staatl. Gesundheitsamt Landsberg a. Lech	GA LL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
385	09	544	\N	\N	\N	Staatl. Gesundheitsamt Landshut	GA LA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
386	09	545	\N	\N	\N	Staatl. Gesundheitsamt Lichtenfels	GA LIF	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
387	09	546	\N	\N	\N	Staatl. Gesundheitsamt Lindau (Bodensee)	GA LI	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
481	14	020	\N	\N	\N	UBG 2.LMSt/FB 22	2.LMSt	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
483	14	100	\N	\N	\N	Wismut	Wismut	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
484	14	101	\N	\N	\N	VKTA	VKTA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
485	14	102	\N	\N	\N	TÜV	TÜV	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
486	14	103	\N	\N	\N	SMUL	SMUL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
487	14	104	\N	\N	\N	LfUG	LfUG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
488	14	105	\N	\N	\N	alt - UBG GB 1	GB1	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
490	14	110	\N	\N	\N	RPD Umweltfachbereich Außenstelle Radebeul	RPD UFB R	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
491	14	111	\N	\N	\N	RPD Umweltfachbereich Außenstelle Bautzen	RPD UFB B	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
492	14	112	\N	\N	\N	RPD Umweltfachbereich Leipzig	RPD UFB L	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
493	14	113	\N	\N	\N	RPD Umweltfachbereich Außenstelle Plauen	RPD UFB P	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
495	14	201	\N	\N	\N	alt - AfL Plauen	AfL Pl	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
549	14	334	\N	\N	\N	alt - LÜVA Delitzsch	LÜVA DZ	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
550	14	335	\N	\N	\N	alt - LÜVA Döbeln	LÜVA DL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
551	14	336	\N	\N	\N	alt - LÜVA Dresden	LÜVA DD	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
552	14	337	\N	\N	\N	alt - LÜVA Freiberg	LÜVA FG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
553	14	339	\N	\N	\N	alt - LÜVA Hoyerswerda	LÜVA HY	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
554	14	340	\N	\N	\N	alt - LÜVA Kamenz	LÜVA KM	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
556	14	342	\N	\N	\N	alt - LÜVA Leipziger Land	LÜVA LL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
557	14	343	\N	\N	\N	alt - LÜVA Löbau-Zittau	LÜVAZI	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
558	14	344	\N	\N	\N	alt - LÜVA Meißen	LÜVA MEI	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
559	14	345	\N	\N	\N	alt - LÜVA Mittlerer Erzgebirgskreis	LÜVA MEK	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
560	14	346	\N	\N	\N	alt - LÜVA Mittweida	LÜVA MW	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
562	14	348	\N	\N	\N	alt - LÜVA Niederschlesischer Oberlausitzkreis	LÜVA NOL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
563	14	349	\N	\N	\N	alt - LÜVA Plauen	LÜVA PL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
564	14	350	\N	\N	\N	alt - LÜVA Riesa-Großenhain	LÜVA RG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
565	14	351	\N	\N	\N	alt - LÜVA Sächsische Schweiz	LÜVA PIR	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1357	09	029	\N	\N	\N	Universität Regensburg, UmweltRadioAktivität-Laboratorium	URA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
792	12	LVL	\N	\N	\N	LVLF FF Frankfurt (Oder)	LVLF FF	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
793	12	KUR	\N	\N	\N	Kurierdienst	Kurier	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
852	10	SLS	Volker Franz	Fax: 444-367; Auch Norbert Gehl, -11	\N	Landkreis Saarlouis, Veterinärwesen und Lebensmittelüberwachung	LK SLS	Saarlouis	66740	Kaiser-Friedrich-Ring 21	06831/94971-14	\N	P	2000-01-01 00:00:00
853	10	MZG	Frau Schnubel	Fax: -113;	\N	Landkreis Merzig-Wadern, Veterinäramt und GLKD	LK MZG	Merzig	66663	Bahnhofstr. 44	06861/80-280	\N	P	2000-01-01 00:00:00
854	10	HOM	Herr Nerschbach, Frau Sch	Fax: -699	\N	Saarpfalz-Kreis, Veterinäramt und GLKD -- abgelöst durch RS-OST	LK HOM	Homburg	66406	Postfach 1550	06841/104-683	\N	P	2011-06-29 09:02:55
855	10	NK	Herr Martin	Fax: -324; Auch Herr Klos: -334, Herr Herme: -335	\N	Landkreis Neunkirchen, Veterinäramt -- abgelöst durch RS-OST	LK NK	Ottweiler	66564	Seminarstr. 25	06824/906-320	\N	P	2011-06-29 09:02:55
856	10	WND	Franz Ost	Fax: -389	\N	Landkreis St. Wendel, Lebensmittelüberwachungs- und Veterinäramt	LK WND	St. Wendel	66606	Mommstr. 25a	06851/801-204	\N	P	2000-01-01 00:00:00
877	10	RMS	\N	\N	\N	Radioaktivitätsmessstelle	RMS HOM	\N	\N	\N	\N	\N	P	2000-01-01 00:00:00
917	19	001	\N	(Milchproben)	\N	Test-Probenahmeinstitution	TPI	\N	\N	\N	\N	\N	P	2000-01-01 00:00:00
918	19	002	\N	Amtshilfe	\N	Institut für Wasserwirtschaft Hamburg	IfWW-HH	\N	\N	\N	\N	\N	P	2000-01-01 00:00:00
919	19	E001	Schmidt	\N	\N	Bauernhof Müller	E001	Berlin	10318	\N	\N	\N	E	2000-01-01 00:00:00
920	19	003	\N	\N	\N	Lebensmitteluntersuchungsamt	LUA-Bln	\N	\N	\N	\N	\N	P	2000-01-01 00:00:00
938	05	000	Sandtner	\N	\N	Landesumweltamt NRW	LUA	Düsseldorf	40221	Auf dem Draap	0211/1590-2341	\N	\N	2000-01-01 00:00:00
939	05	001	\N	\N	\N	Dezernat Fischerei der LÖBF  NRW	LÖBF	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
940	05	002	\N	\N	\N	StUA Aachen	StUAAC	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
941	05	003	\N	\N	\N	StUA Köln	StUAK	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
942	05	013	\N	\N	\N	Stadt Aachen	AC	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
943	05	014	\N	\N	\N	Stadt Bonn	BN	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
944	05	015	\N	\N	\N	Stadt Köln	K	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
945	05	016	\N	\N	\N	Stadt Leverkusen	LEV	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
946	05	054	\N	\N	\N	Kreis Aachen	KAC	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
947	05	058	\N	\N	\N	Kreis Düren	DN	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
948	05	062	\N	\N	\N	Erftkreis	BM	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
949	05	066	\N	\N	\N	Kreis Euskirchen	EU	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
950	05	070	\N	\N	\N	Kreis Heinsberg	HS	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
951	05	074	\N	\N	\N	Oberbergischer Kreis	GM	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
952	05	078	\N	\N	\N	Rheinisch-Bergischer Kreis	GL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
953	05	082	\N	\N	\N	Rhein-Sieg-Kreis	SU	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
954	05	004	\N	\N	\N	Forstamt	FA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
955	05	999	\N	\N	\N	Sonstige	Sonstige	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1043	05	6	\N	\N	\N	Ennepe - Ruhr - Kreis	EN	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1044	05	7	\N	\N	\N	Hochsauerlandkreis	HSK	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1045	05	8	\N	\N	\N	Märkischer Kreis	MK	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1046	05	9	\N	\N	\N	Olpe Kreis	OL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1047	05	10	\N	\N	\N	Siegen - Wittgenstein Kreis	SG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1048	05	11	\N	\N	\N	Soest Kreis	SO	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1057	08	111	\N	\N	EnBW Kernkraft GmbH, Kernkraftwerk Neckarwestheim	Kernkraftwerk Neckarwestheim	GKN	Neckarwestheim	74382	Im Steinbruch	(07133) 13-0	\N	\N	2000-01-01 00:00:00
1058	08	101	\N	\N	EnBW Kernkraft GmbH, Kernkraft Obrigheim	Kernkraftwerk Obrigheim	KWO	Obrigheim	74847	Kraftwerkstr. 1	(06261) 65-0	\N	\N	2000-01-01 00:00:00
1059	08	061	\N	\N	\N	Staatl. milchwirtschaftl. Lehr- und Forschungsanstalt	MLF_Wan	Wangen	88239	Am Maierhof 7	(07522) 71-5011	\N	\N	2000-01-01 00:00:00
1060	08	041	\N	\N	\N	Karlsruher Institut für Technologie - Campus Nord	KIT - CN	Eggenstein-Leo.	76344	Hermann-von-Helmholtz-Platz 1	(07247) 825070)	\N	\N	2000-01-01 00:00:00
1076	08	706	LMÜ/Vet	veterinaeramt@landkreis-goeppingen.de 	Amt für Veterinärwesen und Verbraucherschutz 	Landratsamt Göppingen	LRA_GP	Göppingen	73037	Pappelallee 10	(07161) 202-701	\N	\N	2015-03-04 15:21:27
1077	08	705	LMÜ/Vet	veterinaeramt@lra-es.de 	Veterinär- und Lebensmittelüberwachungsamt .	Landratsamt Esslingen	LRA_ES	Esslingen am Neckar	73728	Pulverwiesen 11	(0711) 3902-1510	\N	\N	2015-03-04 15:13:22
1078	08	704	LMÜ/Vet	vetamt@lrabb.de 	Amt für Veterinärwesen und Verbraucherschutz 	Landratsamt Böblingen	LRA_BB	Böblingen	71034	Parkstr. 16	(07031) 663-1468	\N	\N	2015-03-04 14:55:33
1079	08	703	LMÜ/Vet	veterinaeramt@main-tauber-kreis.de	Veterinäramt	Landratsamt Main-Tauber-Kreis	LRA_TBB	Bad Mergentheim	97980	Wachbacher Str. 52	(07931) 4827-6257	\N	\N	2015-03-04 15:55:10
1080	08	702	LMÜ/Vet	veterinaeramt@rems-murr-kreis.de 	Geschäftsbereich 32, Verbraucherschutz und tierärztlicher Dienst 	Landratsamt Rems-Murr-Kreis	LRA_WN	Backnang	71522	Erbstetter Str. 58	(07191) 895-4062	\N	\N	2015-03-04 15:55:44
1081	08	701	LMÜ/Vet	verbraucherschutz@ostalbkreis.de 	Veterinärwesen und Lebensmittelüberwachung 	Landratsamt Ostalbkreis	LRA_AA	Aalen	73430	Julius-Bausch-Str. 122	(07361) 503,1830311	\N	\N	2015-03-04 15:55:44
1082	08	700	\N	\N	\N	Landratsamt	LRA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1118	08	778	LMÜ/Vet	veterinaeramt@ulm.de 	Bürgerdienste  - Veterinäramt 	Stadt Ulm	LRA_ULS	Ulm	89079	Steinbeisstr. 13	(0731) 	\N	\N	2015-03-04 15:56:37
1119	08	777	LMÜ/Vet	veterinaeramt@alb-donau-kreis.de 	Fachdienst Verbraucherschutz 	Landratsamt Alb-Donau-Kreis	LRA_ULL	Ulm	89077	Schillerstr. 30	(0731) 185-1740	\N	\N	2015-03-04 15:54:07
1377	08	231	\N	\N	\N	Bundesanstalt für Gewässerkunde	BfG	Koblenz	56068	Am Mainzer Tor 1	(0261) 13060	\N	\N	2000-01-01 00:00:00
752	18	003	\N	\N	\N	Endlager für radioaktive Abfälle Morsleben	ERAM	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
753	18	002	\N	\N	\N	Landesamt für Umweltschutz Sachsen-Anhalt Landesmessstelle Nord (Osterburg)	LAU-Nord	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
754	18	001	\N	\N	\N	Landesamt für Umweltschutz Sachsen-Anhalt Landesmessstelle Süd (Halle)	LAU-Süd	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
772	12	AUF	\N	\N	\N	Auftraggeber	AUF	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
773	12	PD1	\N	\N	\N	Pflanzenschutzdienst Frankfurt (Oder)	PSD1	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
774	12	PD2	\N	\N	\N	Pflanzenschutzdienst Prenzlau	PSD2	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
775	12	PD3	\N	\N	\N	Pflanzenschutzdienst Manschnow	PSD3	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
776	12	PD4	\N	\N	\N	Pflanzenschutzdienst Neuruppin	PSD4	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
777	12	PD5	\N	\N	\N	Pflanzenschutzdienst Wünsdorf	PSD5	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
778	12	PD6	\N	\N	\N	Pflanzenschutzdienst Cottbus	PSD6	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
779	12	PD7	\N	\N	\N	Pflanzengesundheitskontrolle Cottbus (ehem. Forst)	PSD7	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
780	12	V01	\N	\N	\N	VLÜA UM Prenzlau	VLÜA UM 1	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
781	12	V02	\N	\N	\N	VLÜA UM Templin	VLÜA UM 2	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
782	12	V03	\N	\N	\N	VLÜA BAR Eberswalde	VLÜA BAR	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
783	12	V04	\N	\N	\N	VLÜA MOL Seelow	VLÜA MOL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
784	12	V05	\N	\N	\N	VLÜA FF Frankfurt (Oder)	VLÜA FF	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
785	12	V06	\N	\N	\N	VLÜA LOS Beeskow	VLÜA LOS	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
786	12	V07	\N	\N	\N	VLÜA LDS Lübben	VLÜA LDS	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
787	12	V08	\N	\N	\N	VLÜA SPN Forst	VLÜA SPN	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
788	12	V09	\N	\N	\N	VLÜA CB Cottbus	VLÜA CB	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
789	12	V10	\N	\N	\N	VLÜA OSL Senftenberg	VLÜA OSL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
790	12	V11	\N	\N	\N	VLÜA EE Herzberg	VLÜA EE 1	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
791	12	V12	\N	\N	\N	VLÜA EE Bad Liebenwerda	VLÜA EE 2	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
813	06	001b	\N	\N	\N	HLUG - Dezernat W2, Probenahme	HLUG-W2 KS	Kassel	34121	Ludwig-Mond-Straße 33	0561-2000-0	\N	\N	2013-02-20 08:30:04
814	06	001c	\N	\N	\N	HLUG - Dezernat I5, Landessammelstelle	HLUG-LSST	Ebsdorfergrund	35085	Postfach 47	06424-1316	\N	\N	2013-02-20 08:43:50
833	10	SB	Erhard Altmeyer	Fax: -3990; Auch Herr Sauer, -3911	\N	Stadtverband Saarbrücken, Amt für Veterinärwesen und Lebensmittelüberwachung  	SV SB	Saarbrücken	66030	Heuduckstr. 1	0681/506-3903	\N	P	2011-06-29 09:02:55
977	06	062106	\N	\N	Standort Wiesbaden, Umweltanalytik, Lebensmittel, Kosmetik u. Bedarfsgegenstände	LHL, Wiesbaden, Landesbetrieb Hessisches Landeslabor	LHL	Wiesbaden	65302	Glarusstraße 6	\N	\N	O	2013-02-08 13:34:01
997	05	05020-05	\N	\N	\N	Klinikum Aachen	KLAAC	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1017	05	05020-06	\N	\N	\N	Wasserwerk Roetgen	WWRO	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1018	05	05020-07	\N	\N	\N	Wasserwerk Erlenhagen/Aggerverband	WWER	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1019	05	05020-08	\N	\N	\N	Wasserwerk Köln/GEW	WWKÖLN	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1020	05	05020-09	\N	\N	\N	Wahnbachtalsperrenverband	VBWAHN	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1021	05	05020-10	\N	\N	\N	Ruhrverband Essen	VBRUHR	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1022	05	05020-11	\N	\N	\N	Lippeverband Wesel	VBLIPPE	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
2218	17	Kamenik	Kamenik	\N	ZINST KIE	Karla Kamenik	KK	Kronshagen	24119	Kopperpahler Allee 120	\N	\N	E	2011-10-17 13:36:21
2219	17	Bäßler	Bäßler	\N	ZINST KOB	Claudia Bäßler	CB	Koblenz	56070	Andernacher Strasse 100	\N	\N	E	2011-10-17 13:36:21
2232	12	S14	\N	\N	\N	Frank Rosenau	PB14	Berlin	13505	Dohlenstraße 15-17	030 4364455	\N	\N	2012-05-08 06:25:17
2303	12	S18	\N	\N	\N	Hansjörg Beyer	PB18	Berlin	14050	Haeselerstraße 7a	030/48098430	\N	\N	2012-10-29 12:40:42
2320	07	071110	\N	\N	\N	Kreisverwaltung des Rhein-Lahn-Kreises	KV EMS	Bad Ems	56130	Insel Silberau 1	\N	\N	O	2013-04-08 08:18:31
2321	07	071109	\N	\N	\N	Kreisverwaltung Rhein-Hunsrück-Kreis	KV SIM	Simmern	55469	Ludwigstr. 3-5	\N	\N	O	2013-04-08 08:18:31
2322	07	071108	\N	\N	\N	Kreisverwaltung Neuwied	KV NR	Neuwied	56564	Ringstr. 70	\N	\N	O	2013-04-08 08:18:31
2323	07	071107	\N	\N	\N	Kreisverwaltung Mayen-Koblenz	KV MYK	Koblenz	56068	Bahnhofstr. 9	\N	\N	O	2013-04-08 08:18:31
2324	07	071118	\N	\N	\N	Kreisverwaltung Alzey-Worms	KV AZ	Alzey	55232	Ernst-Ludwig-Str. 36	\N	\N	O	2013-04-08 08:18:31
2325	07	071116	\N	\N	\N	Kreisverwaltung Trier-Saarburg	KV TR	Trier	54292	Paulinstr. 60	\N	\N	O	2013-04-08 08:18:31
2326	07	071115	\N	\N	\N	Kreisverwaltung Vulkaneifel	KV DAU	Daun	54550	Mainzerstr. 25	\N	\N	O	2013-04-08 08:18:31
2327	07	071114	\N	\N	\N	Kreisverwaltung des Eifelkreises Bitburg-Prüm	KV BIT	Bitburg	54634	Trierer Str. 1	\N	\N	O	2013-04-08 08:18:31
2328	07	071113	\N	\N	\N	Kreisverwaltung Bernkastel-Wittlich	KV WIL	Wittlich	54516	Kurfürstenstr. 16	\N	\N	O	2013-04-08 08:18:31
2329	07	071123	\N	\N	\N	Kreisverwaltung Kusel	KV KUS	Kusel	66869	Trierer Str. 49	\N	\N	O	2013-04-08 08:18:31
2330	07	071122	\N	\N	\N	Kreisverwaltung Kaiserslautern	KV KL	Kaiserslautern	67657	Lauterstr. 8	\N	\N	O	2013-04-08 08:18:31
2331	07	071121	\N	\N	\N	Kreisverwaltung Germersheim	KV GER	Germersheim	76726	Luitpoldplatz 1	\N	\N	O	2013-04-08 08:18:31
2332	07	071120	\N	\N	\N	Kreisverwaltung Donnersbergkreis	KV KIB	Kirchheimbolanden	67292	Uhlandstr. 2	\N	\N	O	2013-04-08 08:18:31
2333	07	071119	\N	\N	\N	Kreisverwaltung Bad Dürkheim	KV DÜW	Bad Dürkheim	67098	Philipp-Fauth-Str. 11	\N	\N	O	2013-04-08 08:18:31
2334	07	071128	\N	\N	\N	Stadtverwaltung Koblenz	SV KO	Koblenz	56068	Jesuitenplatz	\N	\N	O	2013-04-08 08:18:31
2335	07	071127	\N	\N	\N	Kreisverwaltung Südwestpfalz	KV PS	Pirmasens	66953	Unterer Sommerwaldweg 40-42	\N	\N	O	2013-04-08 08:18:31
2336	07	071126	\N	\N	\N	Kreisverwaltung Mainz-Bingen	KV MZ	Mainz	55116	Große Langgasse 29	\N	\N	O	2013-04-08 08:18:31
2337	07	071125	\N	\N	\N	Kreisverwaltung Rhein-Pfalz-Kreis	KV RP	Ludwigshafen	67063	Europaplatz 5	\N	\N	O	2013-04-08 08:18:31
2338	07	071124	\N	\N	\N	Kreisverwaltung Südliche Weinstraße	KV SÜW	Landau	76829	An der Kreuzmühle 2	\N	\N	O	2013-04-08 08:18:31
2339	07	071134	\N	\N	\N	Stadtverwaltung Mainz	SV MZ	Mainz	55116	Kaiserstr. 3-5	\N	\N	O	2013-04-08 08:18:31
2340	07	071133	\N	\N	\N	Stadtverwaltung Ludwigshafen	SV LU	Ludwigshafen	67059	Bismarckstr. 29	\N	\N	O	2013-04-08 08:18:31
2341	07	071131	\N	\N	\N	Stadtverwaltung Kaiserslautern	SV KL	Kaiserslautern	67657	Rathaus	\N	\N	O	2013-04-08 08:18:31
2342	07	071129	\N	\N	\N	Stadtverwaltung Trier	SV TR	Trier	54290	Augustinerhof	\N	\N	O	2013-04-08 08:18:31
2344	02	021001	\N	\N	\N	Behörde für Gesundheit und Verbraucherschutz	BGV-FuMi	Hamburg	20539	Billstraße 30	\N	\N	O	2013-04-22 12:01:21
2345	02	021011	\N	\N	\N	Bezirksamt Hamburg-Mitte	BA-M	Hamburg	20095	Klosterwall 2	\N	\N	O	2013-04-22 12:01:21
2346	02	021015	\N	\N	\N	Bezirksamt Altona	BA-A	Hamburg	22767	Jessenstraße 1-3	\N	\N	O	2013-04-22 12:01:21
2347	02	021017	\N	\N	\N	Bezirksamt Eimsbüttel	BA-E	Hamburg	20139	Grindelberg 66	\N	\N	O	2013-04-22 12:01:21
2348	02	021020	\N	\N	\N	Bezirksamt Hamburg-Nord	BA-N	Hamburg	20249	Kümmellstraße 7	\N	\N	O	2013-04-22 12:01:21
2349	02	021023	\N	\N	\N	Bezirksamt Wandsbek	BA-W	Hamburg	22041	Schloßgarten 9	\N	\N	O	2013-04-22 12:01:21
2350	02	021028	\N	\N	\N	Bezirksamt Bergedorf	BA-B	Hamburg	21029	Wentorfer Straße 38a	\N	\N	O	2013-04-22 12:01:21
2351	02	021030	\N	\N	\N	Bezirksamt Harburg	BA-H	Hamburg	21073	Knoopstraße 37	\N	\N	O	2013-04-22 12:01:21
2352	02	022020	\N	\N	\N	Institut für Hygiene und Umwelt	HU	Hamburg	20539	Marckmannstraße 129	\N	\N	O	2013-04-22 12:01:21
2355	09	800	\N	\N	\N	ABC-Erkundungsfahrzeug FW	ABC	\N	\N	\N	\N	\N	\N	2014-04-10 11:30:27
2356	12	O10	\N	\N	LFB	Oberförsterei Brieselang	Obf Bries.	Brieselang	14656	Forstweg 55	033232/36005	\N	\N	2014-12-15 13:10:59
2208	14	464	\N	\N	\N	Umweltamt Meißen	UA MEI	\N	\N	\N	\N	\N	\N	2011-06-27 07:52:48
2361	08	971	\N	\N	KHG	Kerntechnische Hilfsdienst GmbH	KHG	Eggenstein-Leo	76344	Am Schröcker Tor 1	0 72 47 / 81  0	\N	\N	2016-10-27 11:45:22
2427	08	741	\N	gesundheitsamt@mannheim.de	Fachbereich Gesundheit	Stadt Mannheim	LRA_MA_GA	Mannheim	68161	R1, 12	(0621) 293 2239	\N	\N	2016-12-14 12:20:12
2363	08	973	\N	\N	KHG	_KHG KA-E 483 (Probensammelfahrzeug)	PSF3	Eggenstein-Leo	76344	Am Schröcker Tor 1	0 72 47 / 81  0	\N	\N	2016-10-27 11:47:57
2428	08	742	\N	gafds@landkreis-freudenstadt.de	Gesundheitsamt	Landratsamt Freudenstadt	LRA_FDS_GA	Freudenstadt	72250	Reichsstraße 11	(07441) 920 4107	\N	\N	2016-12-14 12:20:12
2429	08	743	\N	amt23@landkreis-rastatt.de	Gesundheitsamt	Stadt Baden-Baden	LRA_BAD_GA	Baden-Baden	76532	Briegelackerstraße 38	(07221) 302468-0	\N	\N	2016-12-14 12:20:12
2430	08	744	\N	landwirtschaft@landkreis-freudenstadt.de	Landwirtschaftsamt	Landratsamt Freudenstadt	LRA_FDS_L	Horb a. Neckar	72160	Ihlinger Straße 79	(07441) 920-5401	\N	\N	2016-12-14 12:20:12
2431	08	745	\N	landwirtschaftsamt@landratsamt-karlsruhe.de	Landwirtschaftsamt	Landratsamt Karlsruhe	LRA_KA_L	Karlsruhe	76137	Beiertheimer Allee 2	(0721) 936-88010	\N	\N	2016-12-14 12:20:12
2432	08	746	\N	amt35@landkreis-rastatt.de	Landwirtschaft	Landratsamt Rastatt	LRA_RA_L	Rastatt	76437	Am Schlossplatz 5	(07222) 381-4500	\N	\N	2016-12-14 12:20:12
2375	08	402	\N	CBRN-Erkunder	\N	FF Herrenberg (Böblingen)	BB-8505	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
2376	08	403	\N	CBRN-Erkunder	\N	FF Ostfildern Abt. Nellingen (Esslingen)	ES-8010	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
2377	08	404	\N	CBRN-Erkunder	\N	FF Göppingen (Göppingen)	GP-8072	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
2378	08	405	\N	CBRN-Erkunder	\N	FF Heidenheim (Heidenheim)	HDH-8051	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
2379	08	406	\N	CBRN-Erkunder	\N	FF Heilbronn (Stadt Heilbronn)	HN-80021	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
2380	08	407	\N	CBRN-Erkunder	\N	FF Weinsberg (Heilbronn)	HN-8536	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
2381	08	408	\N	CBRN-Erkunder	\N	FF Öhringen ( Hohenlohekreis)	KÜN-8000	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
2382	08	409	\N	CBRN-Erkunder	\N	FF Ludwigsburg (Ludwigsburg)	LB-8092	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
2383	08	410	\N	CBRN-Erkunder	\N	FF Stuttgart FF-Abt. Rohracker (Stuttgart-Brandir.)	S-8011	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
2384	08	411	\N	CBRN-Erkunder	\N	FF Schwäbisch Hall (Schwäbisch Hall)	SHA-8000	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
2385	08	412	\N	CBRN-Erkunder	\N	FF Bad Mergentheim (Main-Tauber-Kreis)	TBB-8005	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
2386	08	413	\N	CBRN-Erkunder	\N	FF Backnang (Rems-Murr-Kreis)	WN-8068	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
2387	08	431	\N	CBRN-Erkunder	\N	FF Wildberg (Calw)	CW-8200	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
2388	08	432	\N	CBRN-Erkunder	\N	FF Horb (Freudenstadt)	FDS-8156	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
2389	08	433	\N	CBRN-Erkunder	\N	BF Heidelberg (Heidelberg)	HD-80058	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
2231	19	113101	\N	\N	\N	Bundesamt für Verbraucherschutz und Lebensmittelsicherheit	BVL (Test)	\N	\N	\N	\N	\N	O	2012-03-01 10:50:03
2301	12	S16	\N	\N	\N	Dörte Wernick	PB16	Schwielochsee	15913	Zauer Dorfstraße 15	035478 178338	\N	\N	2012-10-24 08:22:02
2302	12	S17	\N	\N	\N	Winfried Wernick	PB17	Fürstenberg/ Havel	16798	Grüner Winkel 6	033093 38912	\N	\N	2012-10-24 08:23:29
2313	12	OB0	Herr Krüger	\N	LFB	Oberförsterei Cottbus	Obf CB 	Peitz	03185	August-Bebel-Straße 27	035601 37134	\N	\N	2013-02-27 08:24:43
2374	08	401	\N	CBRN-Erkunder	\N	FF Aalen (Ostalbkreis)	AA-8046	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
2390	08	434	\N	CBRN-Erkunder	\N	FF Ladenburg (Rhein-Neckar-Kreis)	HD-8400	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
2391	08	435	\N	CBRN-Erkunder	\N	Landesfeuerwehrschule Bruchsal (Messfahrzeug Lds-Feuerw.Schule)	KA-6185	\N	\N	\N	\N	\N	\N	2016-12-16 10:26:20
2392	08	436	\N	CBRN-Erkunder	\N	FF Karlsruhe (Karlsruhe)	KA-80105	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
2393	08	437	\N	CBRN-Erkunder	\N	FF Bretten (Karlsruhe)	KA-8060	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
2394	08	438	\N	CBRN-Erkunder	\N	FF Mannheim(Mannheim)	MA-8161	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
2395	08	439	\N	CBRN-Erkunder	\N	FF Buchen (Neckar-Odenwald-Kreis)	MOS-8066	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
2396	08	440	\N	CBRN-Erkunder	\N	BF Pforzheim (Pforzheim)	PF-80112	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
2397	08	441	\N	CBRN-Erkunder	\N	FF Illingen (Enzkreis)	PF-8052	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
2398	08	442	\N	CBRN-Erkunder	\N	FF Gaggenau (Rastatt)	RA-8050	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
2399	08	451	\N	CBRN-Erkunder	\N	FF Kenzingen (Emmendingen)	EM-8046	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
2400	08	452	\N	CBRN-Erkunder	\N	FW Freiburg i. Br. (Stadt Freiburg)	FR-80410	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
2401	08	453	\N	CBRN-Erkunder	\N	FF Ihringen (Breisgau-Hochschwarzwald)	FR-8056	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
2402	08	454	\N	CBRN-Erkunder	\N	FF Konstanz (Konstanz)	KN-8050	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
2403	08	455	\N	CBRN-Erkunder	\N	FF Lörrach (Lörrach)	LÖ-8018	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
2404	08	456	\N	CBRN-Erkunder	\N	FF Lahr (Ortenau)	OG-8001	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
2405	08	457	\N	CBRN-Erkunder	\N	FF Schramberg (Rottweil)	RW-8020	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
2406	08	458	\N	CBRN-Erkunder	\N	FF Gosheim (Tuttlingen)	TUT-8001	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
2407	08	459	\N	CBRN-Erkunder	\N	FF VS-Schwenningen (Schwarzwald-Baar-Kreis)	VS-8022	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
2408	08	460	\N	CBRN-Erkunder	\N	FF Waldshut-Tiengen (Waldshut)	WT-8095	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
2409	08	471	\N	CBRN-Erkunder	\N	FF Biberach (Biberach)	BC-8006	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
2410	08	472	\N	CBRN-Erkunder	\N	FF Balingen-Engstlatt (Zollernalbkreis)	BL-8004	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
2411	08	473	\N	CBRN-Erkunder	\N	FF Überlingen (Bodenseekreis)	FN-8045	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
2412	08	474	\N	CBRN-Erkunder	\N	FF Reutlingen (Reutlingen)	RT-8091	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
2413	08	475	\N	CBRN-Erkunder	\N	FF Ravensburg (Ravensburg)	RV-8077	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
2414	08	476	\N	CBRN-Erkunder	\N	FF Sigmaringen (Sigmaringen)	SIG-8011	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
2415	08	477	\N	CBRN-Erkunder	\N	FF Tübingen (Tübingen)	TÜ-8069	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
2416	08	478	\N	CBRN-Erkunder	\N	FF Ulm (Stadt Ulm)	UL-80072	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
2417	08	479	\N	CBRN-Erkunder	\N	FF Ehingen (Alb-Donau-Kreis)	UL-8010	\N	\N	\N	\N	\N	\N	2016-12-14 11:01:56
1062	08	221	\N	\N	\N	Kerntechnischer Hilfsdienst	KHG	Eggenstein-Leo.	76344	Steinacker 47	(07247) 81-0	\N	\N	2000-01-01 00:00:00
1063	08	210	\N	\N	\N	Landesfeuerwehrschule Baden-Württemberg	LFS_BW	Bruchsal	76646	Steinackerstr. 47	(07251) 933-0	\N	\N	2000-01-01 00:00:00
1064	08	201	\N	\N	\N	Fachhochschule Ravensburg-Weingarten	FH_RV	Weingarten	88250	Doggenriedstr.	(0751) 501-0	\N	\N	2000-01-01 00:00:00
1065	08	131	\N	\N	\N	Deutsches Krebsforschungszentrum	DKFZ	Heidelberg	69120	Im Neuenheimer Feld 280	(06221) 420	\N	\N	2000-01-01 00:00:00
1066	08	121	\N	\N	EnBW Kernkraft GmbH, Kernkraftwerk Philippsburg	Kernkraftwerk Philippsburg	KKP	Philippsburg	76661	Rheinschanzinsel	(07256) 95-0	\N	\N	2000-01-01 00:00:00
1067	08	080	\N	\N	\N	Chemisches- und Veterinäruntersuchungsamt Karlsruhe	CVUA KA	Karlsruhe	76187	Weißenburgerstr. 3	(0721) 926-3549	\N	\N	2000-01-01 00:00:00
1072	08	0	\N	\N	\N	nicht vergeben	000	\N	\N	\N	\N	\N	\N	2016-12-16 10:28:24
1073	08	890	\N	\N	\N	LUBW - Abt. 6 und 7 (ehemals UMEG)	UMEG	Karlsruhe	76135	Großoberfeld 3	/0721) 7505-0	\N	\N	2000-01-01 00:00:00
1074	08	880	\N	\N	\N	LUBW - Kernreaktorfernüberwachungssystem (KFÜ)	KFÜ	Karlsruhe	76187	Hertzstr. 173	(0721) 983-0	\N	\N	2000-01-01 00:00:00
1075	08	870	\N	\N	\N	LUBW - Radioaktivitätsmessnetz (RAM)	RAM	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1083	08	712	LMÜ/Vet	veterinaeramt@lrasha.de 	Amt für Veterinärwesen und Verbraucherschutz 	Landratsamt Schwäbisch Hall	LRA_SHA	Ilshofen	74523	Eckartshäuser Str. 41	(07904)7007-240	\N	\N	2015-03-04 13:55:48
1084	08	711	LMÜ/Vet	vetamt@hohenlohekreis.de 	Veterinäramt	Landratsamt Hohenlohekreis	LRA_KÜN	Öhringen	74613	Hindenburgstr. 58	(07941) 9209-0	\N	\N	2015-03-04 15:23:33
1085	08	710	LMÜ/Vet	veterinaerangelegenheiten.verbraucherschutz@landkreis-ludwig	Fachbereich für Veterinärangelegenheiten und Verbraucherschutz	Landratsamt Ludwigsburg	LRA_LB	Ludwigsburg	71638	Hindenburgstr. 20/1	(07141) 144-1112	\N	\N	2016-12-16 10:58:46
1086	08	709	LMÜ/Vet	vet@stadt-heilbronn.de 	Ordnungsamt, Veterinärwesen und Lebensmittelüberwachung 	Stadt Heilbronn	LRA_HNS	Heilbronn	74072	Bahnhofstr. 2	(07131) 56-2395	\N	\N	2015-03-04 15:44:38
1087	08	708	LMÜ/Vet	veterinaeramt@landratsamt-heilbronn.de 	Veterinäramt	Landratsamt Heilbronn	LRA_HNL	Heilbronn	74072	Lerchenstr. 40	(07131) 994-607	\N	\N	2015-03-04 15:21:27
1088	08	707	LMÜ/Vet	veterinaeramt@landkreis-heidenheim.de 	Fachbereich für Veterinärwesen und Verbraucherschutz 	Landratsamt Heidenheim	LRA_HDH	Heidenheim 	89518	Felsenstr. 36	(07321) 321-601	\N	\N	2015-03-04 15:21:27
1089	08	713	LMÜ/Vet	lebensmittelueberwachung.veterinaerwesen@stuttgart.de 	Lebensmittelüberwachung und Veterinärwesen 	Stadt Stuttgart	LRA_ST	Stuttgart	70178	Hauptstätter Str. 58	(0711) 216-88590	\N	\N	2015-03-04 15:51:27
1090	08	736	LMÜ/Vet	verbraucherschutz@mannheim.de 	Fachbereich Sicherheit u. Ordnung, Verbraucherschutz	Stadt Mannheim	LRA_MA	Mannheim	68159	K7	(0621) 293-2525	\N	\N	2015-03-04 15:46:33
1091	08	735	LMÜ/Vet	luv@karlsruhe.de	Ordnungs- und Bürgeramt	Stadt Karlsruhe	LRA_KAS	Karlsruhe	76131	Alter Schlachthof 5	(0721) 133-7100	\N	\N	2015-03-04 15:44:38
1092	08	734	LMÜ/Vet	lebensmittelueberwachung@landratsamt-karlsruhe.de 	Amt für Veterinärwesen und Lebensmittelüberwachung 	Landratsamt Karlsruhe	LRA_KAL	Karlsruhe	76137	Beiertheimer Allee 2	(0721) 936-5640	\N	\N	2015-03-04 15:27:32
1093	08	733	LMÜ/Vet	vetamt@landkreis-freudenstadt.de 	Veterinär- und Verbraucherschutzamt 	Landratsamt Freudenstadt	LRA_FDS	Freudenstadt	72250	Reichsstr. 11	(07441) 920-458	\N	\N	2015-03-04 15:15:25
1094	08	732	LMÜ/Vet	21.info@kreis-calw.de 	Abt. Verbraucherschutz und Veterinärdienst 	Landratsamt Calw	LRA_CW	Calw	75365	Vogteistr. 42-46	(07051) 160-121	\N	\N	2015-03-04 15:07:04
1095	08	731	LMÜ/Vet	veterinaeramt@neckar-odenwald-kreis.de 	Fachbereich 2 - Veterinärwesen	Landratsamt Neckar-Odenwald-Kreis	LRA_MOS	Buchen	74722	St.-Rochus-Str. 12	(06281) 5212-1450	\N	\N	2015-03-04 15:24:29
1096	08	730	LMÜ/Vet	vetamt@baden-baden.de 	Veterinärbehörde und Lebensmittelüberwachung 	Stadt Baden-Baden	LRA_BAD	Baden-Baden	76532	Briegelackerstr. 8	(07221) 93-1592	\N	\N	2015-03-04 15:39:13
1097	08	751	LMÜ/Vet	veterinaeramt@landkreis-emmendingen.de 	Veterinäramt und Lebensmittelüberwachungsamt 	Landratsamt Emmendingen	LRA_EM	Emmendingen	79312	Adolf-Sexauer-Str. 3/1	(07641) 451-541	\N	\N	2015-03-04 15:11:34
1098	08	750	LMÜ/Vet	veta@lrasbk.de 	Amt für Veterinärwesen und Lebensmittelüberwachung 	Landratsamt Schwarzwald-Baar-Kreis	LRA_VS	Donaueschingen	78166	Humboldtstr. 11	(07721) 913-5050	\N	\N	2015-03-04 13:58:09
1099	08	740	LMÜ/Vet	veterinaeramt@rhein-neckar-kreis.de 	Veterinäramt und Lebensmittelüberwachung	Landratsamt Rhein-Neckar-Kreis	LRA_HDL	Wiesloch	69168	Adelsförsterpfad 7	(06222) 3073-4265	\N	\N	2015-12-15 08:50:57
1100	08	739	LMÜ/Vet	Amt24@landkreis-rastatt.de 	Amt f. Veterinärwesen und Lebensmittelüberw.	Landratsamt Rastatt	LRA_RA	Rastatt	76437	Am Schlossplatz 5	(07222) 381-3307	\N	\N	2015-03-04 13:29:24
1101	08	738	LMÜ/Vet	veterinaerdienst@stadt-pforzheim.de 	Amt f. öffentl. Ordnung, Lebensmittelüberwachung	Stadt Pforzheim	LRA_PFS	Pforzheim	75177	Kleiststr. 2	(07231) 39-2394	\N	\N	2015-03-04 15:51:27
1102	08	737	LMÜ/Vet	veterinaeramt@enzkreis.de 	Verbraucherschutz- und Veterinäramt 	Landratsamt Enzkreis	LRA_PFL	Pforzheim	75177	Zähringerallee 3	(07231) 308-401	\N	\N	2015-03-04 15:11:34
1108	08	771	LMÜ/Vet	vetamt@biberach.de 	Kreisveterinäramt	Landratsamt Biberach	LRA_BC	Biberach an der Riß	88400	Rollinstr. 17	(07351) 82-6180220	\N	\N	2015-03-04 14:51:51
1109	08	770	LMÜ/Vet	veterinaeramt@zollernalbkreis.de 	Amt für Veterinärwesen und Verbraucherschutz 	Landratsamt Zollernalbkreis	LRA_BL	Balingen	72336	Hirschbergstr. 29	(07433) 92-1920	\N	\N	2015-03-04 15:34:18
1110	08	759	LMÜ/Vet	veterinaeramt@landkreis-waldshut.de 	Amt für Veterinärwesen und Lebensmittelüberwachung 	Landratsamt Waldshut	LRA_WT	Waldshut-Tiengen	79761	Im Wallgraben 34	(07751) 86-5201	\N	\N	2015-03-04 15:34:18
1111	08	758	LMÜ/Vet	veterinaeramt@landkreis-tuttlingen.de 	Amt für Veterinärwesen und Verbraucherschutz 	Landratsamt Tuttlingen	LRA_TUT	Tuttlingen	78532	Luginsfeldweg 15	(07461) 926-5403	\N	\N	2015-03-04 15:34:18
1112	08	757	LMÜ/Vet	veta@landkreis-rottweil.de 	Veterinär- und Verbraucherschutzamt 	Landratsamt Rottweil	LRA_RW	Rottweil	78628	Königstr. 36	(0741) 244-383, 457	\N	\N	2015-03-04 13:52:01
1113	08	776	LMÜ/Vet	veterinaerwesen@kreis-tuebingen.de 	Abteilung 32, Veterinärwesen und Lebensmittelüberwachung 	Landratsamt Tübingen	LRA_TÜ	Tübingen	72072	Wilhelm-Keil-Str. 50	(07071) 207-3202	\N	\N	2015-03-04 14:03:47
1114	08	775	LMÜ/Vet	post.veterinaer@lrasig.de 	Fachbereich Verbraucherschutz und Veterinärwesen 	Landratsamt Sigmaringen	LRA_SIG	Sigmaringen	72488	Gorheimer Allee  4	(07571) 102-7513	\N	\N	2015-03-04 14:00:52
1115	08	774	LMÜ/Vet	vetamt@Kreis-Reutlingen.de 	 Kreisveterinär- und Lebensmittelüberwachungsamt 	Landratsamt Reutlingen	LRA_RT	Reutlingen	72764	Aulberstr. 32	(07121) 480-2410 	\N	\N	2015-03-04 13:43:22
1116	08	773	LMÜ/Vet	vet@landkreis-ravensburg.de 	Veterinäramt	Landratsamt Ravensburg	LRA_RV	Ravensburg	88212	Friedenstr. 2	(0751) 85-5473	\N	\N	2015-03-04 13:32:02
1117	08	772	LMÜ/Vet	vet@bodenseekreis.de 	Veterinäramt	Landratsamt Bodenseekreis	LRA_FN	Friedrichshafen	88045	Albrechtstr. 67	(07541) 204-5177	\N	\N	2015-03-04 15:01:21
1038	05	1	\N	\N	\N	Stadt Bochum	BO	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1039	05	2	\N	\N	\N	Stadt Dortmund	DO	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1040	05	3	\N	\N	\N	Stadt Hagen	HA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1041	05	4	\N	\N	\N	Stadt Hamm	HAM	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1042	05	5	\N	\N	\N	Stadt Herne	HE	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1049	05	12	\N	\N	\N	Unna Kreis	UN	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1050	05	13	\N	\N	\N	Gryszka Ulrike	GR	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1051	05	14	\N	\N	\N	Stübing Monika	ST	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1068	08	800	\N	\N	\N	Landesanstalt für Umwelt, Messungen und Naturschutz Ba-Wü (nicht IMIS)	LUBW	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1069	08	251	\N	\N	\N	Firma MURA GmbH	MURA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1070	08	230	\N	\N	\N	Bundesamt für Strahlenschutz	BfS	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1071	08	105	\N	\N	\N	Wiederaufarbeitungsanlage Karlsruhe	WAK	Eggenstein-Leo.	76339	\N	(07247) 88-0	\N	\N	2000-01-01 00:00:00
2209	14	465	\N	\N	\N	Umweltamt Mittelsachsen	UA FG	\N	\N	\N	\N	\N	\N	2011-06-27 07:52:48
2210	14	466	\N	\N	\N	Umweltamt Nordsachsen 	UA TDO	\N	\N	\N	\N	\N	\N	2011-06-27 07:52:48
1103	08	756	LMÜ/Vet	vetamt@ortenaukreis.de 	Amt für Veterinärwesen und Lebensmittelüberwachung 	Landratsamt Ortenaukreis	LRA_OG	Offenburg	77652	Kronenstr. 29	(0781) 805-9091	\N	\N	2015-03-04 13:32:02
1104	08	755	LMÜ/Vet	verbraucherschutz@loerrach-landkreis.de 	Fachbereich Verbraucherschutz	Landratsamt Lörrach	LRA_LÖ	Lörrach	79539	Palmstr. 3	(07621) 410-2222	\N	\N	2015-03-04 15:29:36
1105	08	754	LMÜ/Vet	veterinaeramt@LRAKN.de 	Amt für Verbraucherschutz und Veterinärwesen 	Landratsamt Konstanz	LRA_KN	Radolfzell	78315	Otto-Blesch-Str. 51	(07531) 800 2010	\N	\N	2015-03-04 15:27:32
1106	08	753	LMÜ/Vet	veterinaerbehoerde@stadt.freiburg.de 	Amt für öffentliche Ordnung, Veterinärbehörde 	Stadt Freiburg	LRA_FRS	Freiburg	79100	Baslerstr. 2	(0761) 201-4965	\N	\N	2015-03-04 15:39:13
1107	08	752	LMÜ/Vet	vetamt@lkbh.de 	Veterinärdienst	Landratsamt Breisgau-Hochschwarzwald	LRA_FRL	Freiburg im Breisgau	79104	Sautierstr. 30	(0761) 2187-4800	\N	\N	2015-03-04 15:01:21
1120	08	930	\N	\N	\N	Gewerbeaufsichtsämter	GAA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1121	08	920	\N	\N	\N	Wirtschaftskontrolldienste	WKD	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1122	08	910	\N	\N	\N	Wasserwirtschaftsämter	WBA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1677	14	107	\N	\N	\N	alt - UBG GB 4	GB4	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1678	14	108	\N	\N	\N	Sachsenforst	SNFo	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1717	03	192	\N	\N	\N	Kernkraftwerk Emsland Messfahrzeug	KKE Messf	Lingen	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1757	12	LBF	\N	\N	\N	Landesmessstelle Frankfurt (Oder) und LVLF Frankfurt (Oder)	LLB-LVLF	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1837	10	OST	\N	\N	\N	LAV Regionalstelle Ost	RS-OST	\N	\N	\N	\N	\N	P	2016-12-12 10:40:40
1838	10	MITTE	\N	\N	\N	LAV Regionalstelle Mitte	RS-MITTE	\N	\N	\N	\N	\N	P	2016-12-12 10:40:40
1839	10	WEST	\N	\N	\N	LAV Regionalstelle West	RS-WEST	\N	\N	\N	\N	\N	P	2016-12-12 10:40:40
1840	10	ZENTRAL	\N	\N	\N	LAV Zentralstelle	RS-Zentral	\N	\N	\N	\N	\N	P	2016-12-12 10:40:40
1858	12	PR2	Klaus Boese	\N	LELF	Zentraler Technischer Prüfdienst - Prenzlau	ZTPD2	\N	\N	\N	03984 7187 25	\N	\N	2011-12-14 10:01:17
1860	12	PR4	Klaus Struppek	\N	LELF	Zentraler Technischer Prüfdienst - Cottbus	ZTPD4	\N	\N	\N	0355 4991 7169	\N	\N	2011-12-14 10:01:17
1917	12	A01	\N	\N	\N	LVLF Abteilung 4	Ackerboden	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1918	12	A04	\N	\N	\N	LVLF Abteilung 4	Ackerboden	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1919	12	A03	\N	\N	\N	LVLF Abteilung 4	Ackerboden	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1920	12	A02	\N	\N	\N	LVLF Abteilung 4	Ackerboden	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1964	14	400	\N	\N	\N	LÜVA Bautzen	LÜVA BZ	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1965	14	401	\N	\N	\N	LÜVA Erzgebirgskreis	LÜVA ERZ	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1966	14	402	\N	\N	\N	LÜVA Görlitz	LÜVA GR	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1967	14	403	\N	\N	\N	LÜVA Leipzig (Landkreis)	LÜVA L(L)	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1968	14	404	\N	\N	\N	LÜVA Meißen	LÜVA MEI	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1969	14	405	\N	\N	\N	LÜVA Mittelsachsen	LÜVA FG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1970	14	406	\N	\N	\N	LÜVA Nordsachsen	LÜVA TDO	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1971	14	407	\N	\N	\N	LÜVA Säschsische Schweiz - Osterzgebirge	LÜVA PIR	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1972	14	408	\N	\N	\N	LÜVA Vogtlandkreis	LÜVA V	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1973	14	409	\N	\N	\N	LÜVA Zwickau	LÜVA Z	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1974	14	410	\N	\N	\N	LÜVA Chemnitz	LÜVA C	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1975	14	411	\N	\N	\N	LÜVA Dresden	LÜVA DD	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1976	14	412	\N	\N	\N	LÜVA Leipzig (Stadt)	LÜVA L	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1977	14	420	\N	\N	\N	GA Bautzen	GA BZ	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1978	14	421	\N	\N	\N	GA Erzgebirgskreis	GA ERZ	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1979	14	422	\N	\N	\N	GA Görlitz	GA GR	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1980	14	423	\N	\N	\N	GA Leipzig (Landkreis)	GA L(L)	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1981	14	424	\N	\N	\N	GA Meißen	GA MEI	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1982	14	425	\N	\N	\N	GA Mittelsachsen	GA FG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1983	14	426	\N	\N	\N	GA Nordsachsen	GA TDO	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1984	14	427	\N	\N	\N	GA Sächsische Schweiz - Osterzgebirge	GA PIR	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1985	14	428	\N	\N	\N	GA Vogtlandkreis	GA V	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1986	14	429	\N	\N	\N	GA Zwickau	GA Z	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1987	14	430	\N	\N	\N	GA Chemnitz	GA C	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1988	14	431	\N	\N	\N	GA Dresden	GA DD	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1989	14	432	\N	\N	\N	GA Leipzig (Stadt)	GA L	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
2177	19	123456789	Hr. Tunichtgut	2. Probenehmer zum Testen	ZdB-Test	Test Feldlängen	1234567890	Osterinseln	12345	Feldweg	12345678901234567890	123	A	2010-11-01 16:40:19
2137	17	Zimmerm	Zimmerm	\N	ZINST KIE	Helga Zimmermann	HZ	Kronshagen	24119	Kopperpahler Allee 120	\N	\N	E	2000-01-01 00:00:00
2138	17	Balfanz	Balfanz	\N	ZINST KIE	Danny Balfanz	DB	Kronshagen	24119	Kopperpahler Allee 120	\N	\N	E	2011-10-17 13:36:21
2139	17	Küsters	Küsters	\N	ZINST KOB	Markus Küsters	MK	Koblenz	56070	Andernacher Str. 100	\N	\N	E	2000-01-01 00:00:00
2140	17	Reeser	Reeser	\N	ZINST KOB	Sebastian Reeser	SR	Koblenz	56070	Andernacher Str. 100	\N	\N	E	2011-10-17 13:36:21
2157	17	Hahn	Hahn	\N	ZINST KIE	Oliver Hahn	OH	Kronshagen	24119	Kopperpahler Allee 120	\N	\N	E	2000-01-01 00:00:00
2202	01	025	\N	\N	\N	FUGRO-HGN GmbH Greifswald	FUGRO	\N	\N	\N	\N	\N	\N	2011-05-10 06:11:04
1737	07	636	\N	\N	\N	Frau Rockenfeller	KKW MK 	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1777	07	409	\N	\N	\N	TWL Ludwigshafen	TWL LU	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1861	12	PR5	Roland Ratsch, M. Gülow 	\N	LELF	Zentraler Technischer Prüfdienst - Neuruppin	ZTPD5	\N	\N	\N	03391 838 232	\N	\N	2011-12-14 10:01:17
2204	14	460	\N	\N	\N	Umweltamt Bautzen	UA BZ	\N	\N	\N	\N	\N	\N	2011-06-24 13:01:09
419	09	578	\N	\N	\N	Staatl. Gesundheitsamt Traunstein	GA TS	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
427	09	902	\N	\N	\N	Fernwasserversorgung Bayer. Wald	WFW	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
482	14	021	\N	\N	\N	Wismut - 2.LMSt als Abholer	Wis_22	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
489	14	106	\N	\N	\N	alt - UBG GB 3	GB3	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
494	14	114	\N	\N	\N	RPC Umweltfachbereich Chemnitz	RPD UFB C	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
555	14	341	\N	\N	\N	alt - LÜVA Leipzig	LÜVA L	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
561	14	347	\N	\N	\N	alt - LÜVA Muldentalkreis	LÜVA MTL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
568	14	354	\N	\N	\N	alt - LÜVA Vogtlandkreis	LÜVA V	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
575	15	002	\N	\N	\N	LAU Halle, Landesmeßstelle Nord in Osterburg	Lmst Nord	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
4	01	004	\N	\N	\N	Landesamt für Gesundheit und Arbeitssicherheit nach StrVG	LGA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
214	09	151	\N	\N	\N	Landratsamt Ostallgäu	LRA OAL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
536	14	321	\N	\N	\N	alt - GA Riesa-Großenhain	GA RG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
542	14	327	\N	\N	\N	alt - GA Zwickau	GA Z	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
548	14	333	\N	\N	\N	alt - LÜVA Chemnitzer Land	LÜVA CL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1157	12	LMO	\N	\N	\N	Eintrag folgt	LMO	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1477	15	EL-001	\N	\N	\N	LAU Halle, Landesmeßstelle Süd in Halle	Lmst Süd	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
2205	14	461	\N	\N	\N	Umweltamt Erzgebirgskreis	UA ERZ	\N	\N	\N	\N	\N	\N	2011-06-24 13:01:09
2206	14	462	\N	\N	\N	Umweltamt Görlitz	UA GR	\N	\N	\N	\N	\N	\N	2011-06-24 13:20:48
2207	14	463	\N	\N	\N	Umweltamt Leipzig Landkreis	UA L(L)	\N	\N	\N	\N	\N	\N	2011-06-24 13:21:42
2211	14	467	\N	\N	\N	Umweltamt Sächsische Schweiz - Osterzgebirge	UA PIR	\N	\N	\N	\N	\N	\N	2011-06-27 07:52:48
2212	14	468	\N	\N	\N	Umweltamt Vogtlandkreis	UA V	\N	\N	\N	\N	\N	\N	2011-06-27 07:52:48
2213	14	469	\N	\N	\N	Umweltamt Zwickau	UA Z	\N	\N	\N	\N	\N	\N	2011-06-27 07:52:48
2214	14	470	\N	\N	\N	Umweltamt Chemnitz	UA C	\N	\N	\N	\N	\N	\N	2011-06-27 07:52:48
2215	14	471	\N	\N	\N	Umweltamt Dresden	UA DD	\N	\N	\N	\N	\N	\N	2011-06-27 07:52:48
2216	14	472	\N	\N	\N	Umweltamt Leipzig (Stadt)	UA L	\N	\N	\N	\N	\N	\N	2011-06-27 07:52:48
1857	12	PR1	Beate Patke	\N	LELF	Zentraler Technischer Prüfdienst - Frankfurt (O)	ZTPD1	\N	\N	\N	0335 5217 542	\N	\N	2016-10-27 11:50:53
1957	14	030	\N	\N	\N	BfUL GB 3	BfUL GB 3	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1958	14	032	\N	\N	\N	BfUL FB 32	BfUL FB 32	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1959	14	033	\N	\N	\N	BfUL FB 33	BfUL FB 33	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1960	14	034	\N	\N	\N	BfUL FB 34	BfUL FB 34	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1961	14	040	\N	\N	\N	BfUL GB 4	BfUL GB 4	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1962	14	050	\N	\N	\N	BfUL GB 5	BfUL GB 5	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1963	14	053	\N	\N	\N	BfUL FB 53	BfUL FB 53	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1990	14	440	\N	\N	\N	LfULG Außenstelle Kamenz	AS K	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1991	14	441	\N	\N	\N	LfULG Außenstelle Zwönitz	AS Zwö	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1992	14	442	\N	\N	\N	LfULG Außenstelle Löbau	AS L	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1993	14	443	\N	\N	\N	LfULG Außenstelle Rötha	AS R	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1994	14	444	\N	\N	\N	LfULG Außenstelle Großenhain	AS G	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1995	14	445	\N	\N	\N	LfULG Außenstelle Döbeln	AS D	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1996	14	446	\N	\N	\N	LfULG Außenstelle Mockrehna	AS M	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1997	14	447	\N	\N	\N	LfULG Außenstelle Pirna	AS Pi	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1998	14	448	\N	\N	\N	LfULG Außenstelle Plauen	AS Pl	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1999	14	449	\N	\N	\N	LfULG Außenstelle Zwickau	AS Zwi	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
2017	12	U43	\N	\N	\N	Mitarbeiter LLBB U 4.3	LLBB U 4.3	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
2037	14	120	\N	\N	\N	Landesdirektion Chemnitz - Ref. 41B	LDC 41B	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
2038	14	121	\N	\N	\N	Landesdirektion Dresden - Ref. 41A	LDC 41A	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
2039	14	122	\N	\N	\N	Landesdirektion Leipzig - Ref. 41	LDL 41	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
2117	14	130	\N	\N	\N	Landestalsperrenverwaltung	LTV	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
2198	10	LUA6.4	\N	\N	\N	Fachbereich 5.4, Radioanalytik	LUA5.4	Saarbrücken	66119	Don Bosco Str.1	0681/8500-1510	\N	P	2016-10-21 09:22:43
2199	10	WW	diverse	\N	\N	zuständiges Wasserwerk	Wasswerk	\N	\N	\N	\N	\N	P	2011-01-13 07:34:21
1817	12	S12	\N	\N	\N	Marcus Borchert	PB12	Rathenow	14712	Horstenweg 5	0178 4794361	\N	\N	2000-01-01 00:00:00
2077	19	OBG	\N	\N	\N	Landesmessstelle Oranienburg	LMSt-Obg	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1897	12	LV1	Herr Lange	\N	LUGV	LUGV Referat V3	LUGV V3	Frankfurt (Oder)	\N	\N	0335 560 3363	\N	\N	2011-12-14 09:55:42
1859	12	PR3	Jan Schmidt	\N	LELF	Zentraler Technischer Prüfdienst - Luckau	ZTPD3	\N	\N	\N	03544 403108	\N	\N	2011-12-14 10:01:17
1937	06	001d	\N	\N	\N	HLUG - Dezernat I5, Kassel	HLUG-I5 KS	Kassel	34121	Ludwig Mond Straße 33	0561 2000-0	\N	\N	2013-02-20 08:43:50
2097	18	EL-004	\N	\N	\N	Endlager für radioaktive Abfälle Asse in Remlingen	Asse	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
2200	10	FORST	\N	\N	\N	Saarforst	FORST	\N	\N	\N	\N	\N	P	2011-04-28 06:40:07
2217	12	S13	\N	\N	\N	Martina Laade	PB13	Schöneiche b. Berlin	15566	Heuweg 67a	030 6492565	\N	\N	2011-08-30 13:21:31
10	01	051	\N	\N	\N	KKW KKK	KKW KKK	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
11	01	071	\N	\N	\N	KKW KKB	KKW KKB	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
12	01	081	\N	\N	\N	KKW KBR	KKW KBR	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
13	03	002	\N	\N	\N	Nieders. Ministerium f. Ernährung, Landwirtschaft u. Forsten	ML	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
14	03	010	\N	\N	\N	LAVES, Lbsm.Institut Braunschweig	LMST5-StVG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
15	03	020	\N	\N	\N	Universität Göttingen, Isotopenlabor f.biol.u.med.Forschung	Uni Gött.	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
16	03	040	\N	\N	\N	NLWKN, Messstelle 1	LMST1-StVG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
17	03	050	\N	\N	\N	LAVES, Vet.Institut Hannover	LMST6-StVG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
18	03	060	\N	\N	\N	NLWKN, Messstelle 2	LMST2-StVG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
19	03	070	\N	\N	Institut für Futtermittel	LUFA Nord-West, Institut für Futtermittel, Oldenburg	LMST7-StVG	Oldenburg	26121	\N	\N	\N	\N	2000-01-01 00:00:00
20	03	080	\N	\N	\N	LAVES, Lbsm.Institut Oldenburg	LMST3-StVG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
21	03	090	\N	\N	\N	LAVES, Vet.Institut Cuxhaven	LMST4-StVG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
22	03	102	\N	\N	\N	NLWKN, Landesdatenzentrale	LDZ-StrVG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
23	03	122	\N	\N	\N	Meßstellen-Zentrale LAVES, Lbsm.Institut Braunschweig	MstZ-StrVG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
24	03	132	\N	\N	\N	Nieders. Umweltministerium	MU	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
25	03	141	\N	\N	\N	Kernkraftwerk Lingen -stillgelegt-	KWL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
26	03	151	\N	\N	\N	Kernkraftwerk Stade	KKS	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
27	03	161	\N	\N	\N	Kernkraftwerk Unterweser	KKU	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
28	03	171	\N	\N	\N	Kernkraftwerk Grohnde	KWG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
29	03	181	\N	\N	\N	Versuchs-Endlager Schachtanlage Asse	Asse	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
30	03	191	\N	\N	\N	Kernkraftwerk Emsland	KKE	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
31	03	201	\N	\N	\N	Zwischenlager Gorleben	TBL/PKA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
32	03	211	\N	\N	\N	Endlager Konrad	Konrad	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
33	03	221	\N	\N	\N	Advanced Nuclear Fuels GmbH Lingen	ANF	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
34	03	231	\N	\N	\N	Standortzwischenlager KKE	SZL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
35	03	291	\N	\N	\N	Kerntechnischer Hilfszug GmbH, Leopoldshaven Karlsruhe	KTH	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
36	03	300	\N	\N	\N	300 - 399 reserviert f. LAVES Braunschweig	LUA Bs.	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
37	03	400	\N	\N	\N	400 - 499 reserviert f. NLWKN Hannover	NLWKN Han	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
38	03	500	\N	\N	\N	500 - 599 reserviert f. LAVES Hannover	VUA Hann.	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
39	03	600	\N	\N	\N	600 - 699 reserviert f. NLWKN Hildesheim	NLWKN Hi.	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
40	03	601	\N	\N	\N	Umweltbehörde Hamburg	UWBeh-HH	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
41	03	602	\N	\N	\N	Gesellschaft f. Kernkraft u. Seeschiffahrt	GKSS	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
42	03	713	\N	\N	Landwirtschaftskammer Niedersachsen 	Bezirksstelle Hannover	Bez. Hann.	Hannover	30453	\N	\N	\N	\N	2000-01-01 00:00:00
43	03	714	\N	\N	Landwirtschaftskammer Niedersachsen	Bezirksstelle Braunschweig	Bez.BS	Braunschweig	38122	\N	\N	\N	\N	2000-01-01 00:00:00
44	03	722	\N	\N	Landwirtschaftskammer Niedersachsen	Außenstelle Leer, Bezirksstelle Ostfriesland	Aust. Leer	Leer	26789	\N	\N	\N	\N	2000-01-01 00:00:00
45	03	729	\N	\N	Landwirtschaftskammer Niedersachsen	Bezirksstelle Oldenburg-Nord	Bez. Ol-N	Jever	26441	\N	\N	\N	\N	2000-01-01 00:00:00
46	03	732	\N	\N	Landwirtschaftskammer Niedersachsen	Bezirksstelle Ostfriesland	Bez. Ostf.	Aurich	26603	\N	\N	\N	\N	2000-01-01 00:00:00
47	03	733	\N	\N	Landwirtschaftskammer Niedersachsen	Außenstelle Grafschaft Bentheim, Bezirksstelle Emsland	Aust.GrBh.	Neuenhaus	49828	\N	\N	\N	\N	2000-01-01 00:00:00
48	03	734	\N	\N	Landwirtschaftskammer Niedersachsen	Außenstelle Bersenbrück, Bezirksstelle Osnabrück	Aust.Bsbr	Bersenbrück	49593	\N	\N	\N	\N	2000-01-01 00:00:00
49	03	735	\N	\N	Landwirtschaftskammer Niedersachsen	Bezirksstelle Osnabrück	Bez. Osna	Osnabrück	49082	\N	\N	\N	\N	2000-01-01 00:00:00
50	03	736	\N	\N	Landwirtschaftskammer Niedersachsen	Bezirksstelle Uelzen	Bez. Uelz.	Uelzen	29525	\N	\N	\N	\N	2000-01-01 00:00:00
496	14	202	\N	\N	\N	alt - AfL Plauen - Auerbach	AfL Pl2	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
497	14	203	\N	\N	\N	alt - AfL Zwickau	AfL Zwi	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
498	14	204	\N	\N	\N	alt - AfL Zwönitz	AfL Zwö	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
499	14	205	\N	\N	\N	alt - AfL Zwönitz - Marienberg	AfL Zwö2	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
500	14	206	\N	\N	\N	alt - AfL Freiberg-Zug	AfL Fg	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
501	14	207	\N	\N	\N	alt - AfLuG Döbeln-Mittweida	AfL DbMw	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
502	14	208	\N	\N	\N	alt - AfL Mockrehna	AfL Mo	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
503	14	209	\N	\N	\N	alt - AfLuG Rötha	AfL Rö	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
504	14	210	\N	\N	\N	alt - AfL Wurzen	AfL Wu	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
505	14	211	\N	\N	\N	alt - AfL Döbeln	AfL Db	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
506	14	212	\N	\N	\N	alt - AfL Döbeln - Oschatz	AfL Db2	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
507	14	213	\N	\N	\N	alt - AfLuG Großenhain	AfL Grh	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
508	14	214	\N	\N	\N	alt - AfLuG Großenhain - Coswig	AfL Grh2	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
509	14	215	\N	\N	\N	alt - AfL Prina	AfL Pir	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
510	14	216	\N	\N	\N	alt - AfL Prina - Reinhardtsgrimma	AfL Pir2	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
511	14	217	\N	\N	\N	alt - AfL Niesky - Kamenz	AfL NieKm	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
512	14	218	\N	\N	\N	alt - AfL Niesky	AfL Nie	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
513	14	219	\N	\N	\N	alt - AfLuG Löbau	AfL Lö	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
514	14	220	\N	\N	\N	LUA (ehem. LfULG - Referat 35a)	LUA-FuMi	\N	\N	\N	\N	\N	\N	2012-02-06 12:40:19
515	14	300	\N	\N	\N	alt - GA Annaberg	GA ANA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
516	14	301	\N	\N	\N	alt - GA Aue-Schwarzenberg	GA ASZ	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
517	14	302	\N	\N	\N	alt - GA Bautzen	GA BZ	\N	\N	\N	\N	\N	\N	2012-02-06 12:42:20
518	14	303	\N	\N	\N	alt - GA Chemnitz	GA C	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
519	14	304	\N	\N	\N	alt - GA Chemnitzer Land	GA CL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
520	14	305	\N	\N	\N	alt - GA Delitzsch	GA DZ	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
521	14	306	\N	\N	\N	alt - GA Döbeln	GA DL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
522	14	307	\N	\N	\N	alt - GA Dresden	GA DD	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
523	14	308	\N	\N	\N	alt - GA Freiberg	GA FG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
524	14	309	\N	\N	\N	alt - GA Görlitz	GA GR	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
525	14	310	\N	\N	\N	alt - GA Hoyerswerda	GA HY	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
526	14	311	\N	\N	\N	alt - GA Kamenz	GA KM	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
527	14	312	\N	\N	\N	alt - GA Leipzig	GA L	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
528	14	313	\N	\N	\N	alt - GA Landratsamt Leipziger Land	GA LL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
529	14	314	\N	\N	\N	alt - GA Löbau-Zittau	GA ZI	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
530	14	315	\N	\N	\N	alt - GA Meißen	GA MEI	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
531	14	316	\N	\N	\N	alt - GA Mittlerer Erzgebirgskreis	GA MEK	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
532	14	317	\N	\N	\N	alt - GA Mittweida	GA MW	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
533	14	318	\N	\N	\N	alt - GA Muldentalkreis	GA MTL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
534	14	319	\N	\N	\N	alt - GA Niederschlesischer Oberlausitzkreis	GA NOL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
535	14	320	\N	\N	\N	alt - GA Plauen	GA PL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
537	14	322	\N	\N	\N	alt - GA Sächsische Schweiz	GA PIR	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
538	14	323	\N	\N	\N	alt - GA Stollberg	GA STL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
539	14	324	\N	\N	\N	alt - GA Torgau-Oschatz	GA TO	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
540	14	325	\N	\N	\N	alt - GA Vorgtlandkreis	GA V	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
541	14	326	\N	\N	\N	alt - GA Weißeritzkreis	GA DW	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
543	14	328	\N	\N	\N	alt - GA Zwickauer-Land	GA ZL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
544	14	329	\N	\N	\N	alt - LÜVA Annaberg	LÜVA ANA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
545	14	330	\N	\N	\N	alt - LÜVA Aue-Schwarzenberg	LÜVA ASZ	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
546	14	331	\N	\N	\N	alt - LÜVA Bautzen	LÜVA BZ	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
547	14	332	\N	\N	\N	alt - LÜVA Chemnitz	LÜVA C	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
735	06	061118	\N	\N	\N	Vogelsbergkreis - Amt f. Veterinärwesen u. Verbraucherschutz	AVV Lauter	Lauterbach (Hessen)	36341	Vogelsbergstraße 32	06641-97768-0	\N	O	2015-03-18 12:03:04
736	06	061124	\N	\N	\N	LK Waldeck-Frankenberg - 8.3 Lebensmittelüberw., Tierschutz und Veterinärwesen	AVV Frankb	Frankenberg (Eder)	35066	Osterweg 20	06451-743-753	\N	O	2012-08-27 11:45:18
737	06	061125	\N	\N	\N	Werra-Meißner Kreis - Fachdienst Lebensmittelüberwachung	AVV Eschwe	Eschwege	37269	Luisenstraße 23c	05651-9592-0	\N	O	2013-03-28 09:56:53
738	06	062201	Frau Pitroff	\N	\N	Regierungspräsidium Gießen - Dezernat 51.3 - Futtermittelüberwachung	RP Gießen	Wetzlar	35578	Schanzenfeldstraße 8	0641-303-0	\N	O	2015-03-18 12:03:04
1137	19	4	\N	\N	\N	Test Cache	4	\N	\N	\N	\N	\N	P	2000-01-01 00:00:00
1138	19	5	\N	\N	\N	Test-Cache 2	5	\N	\N	\N	\N	\N	P	2000-01-01 00:00:00
1206	12	LÖ3	\N	\N	\N	Mitarbeiter LUA Ö3	LUA Ö3	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1237	19	191	\N	\N	\N	Kernkraftwerk Emsland	KKE	\N	\N	\N	\N	\N	E	2000-01-01 00:00:00
1257	03	723	\N	\N	Landwirtschaftskammer Niedersachsen	Bezirksstelle Emsland	Bez. Ems	Meppen/Ems	49716	\N	\N	\N	\N	2000-01-01 00:00:00
1277	19	E002	PD	\N	\N	Pilzsammelstelle	E002	Forst	98765	Waldweg 3	\N	P10	E	2000-01-01 00:00:00
1478	15	EL-002	\N	\N	\N	LAU Halle, Landesmeßstelle Nord in Osterburg	Lmst Nord	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1457	07	010	\N	\N	\N	RWE KKW BIblis	KKW Biblis	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1479	15	EL-003	\N	\N	\N	ERAM in Morsleben	ERAM	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1517	18	EL-001	\N	\N	\N	LAU Halle, Landesmeßstelle Süd in Halle	Lmst Süd	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1518	18	EL-002	\N	\N	\N	LAU Halle, Landesmeßstelle Nord in Osterburg	Lmst Nord	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
2225	16	2	\N	\N	TLUG	Referat 24	R24	Jena	07745	Prüssingstraße	\N	\N	\N	2011-11-15 10:03:32
2226	16	1	\N	\N	TLUG	Referat 21	R21	Jena	07745	Göschwitzer Straße	\N	\N	\N	2011-11-15 10:03:32
2227	14	131	\N	\N	\N	LTV - USt Paulsdorf	LTV - Pau	\N	\N	\N	\N	\N	\N	2012-02-06 12:41:10
2228	14	132	\N	\N	\N	LTV - USt Saidenbach	LTV - Sai	\N	\N	\N	\N	\N	\N	2012-02-06 12:41:10
2229	14	133	\N	\N	\N	LTV - USt Plauen	LTV - Pl	\N	\N	\N	\N	\N	\N	2012-02-06 12:41:10
2230	14	134	\N	\N	\N	LTV - USt Radeburg	LTV - Ra	\N	\N	\N	\N	\N	\N	2012-02-06 12:41:10
2233	05	051113	\N	\N	\N	051113 §7-Stadt Bochum Veterinäramt 	051113	\N	\N	\N	\N	\N	O	2012-08-14 09:18:22
2234	05	051112	\N	\N	\N	051112 §7-Kreis Unna	051112	\N	\N	\N	\N	\N	O	2012-08-14 09:18:22
2235	05	051111	\N	\N	\N	051111 §7-Kreis Soest 	051111	\N	\N	\N	\N	\N	O	2012-08-14 09:18:22
2236	05	051110	\N	\N	\N	051110 §7-Kreis Siegen-Wittgenstein	051110	\N	\N	\N	\N	\N	O	2012-08-14 09:18:22
2237	05	051109	\N	\N	\N	051109 §7-Kreis Olpe 	051109	\N	\N	\N	\N	\N	O	2012-08-14 09:18:22
2238	05	051108	\N	\N	\N	051108 §7-Märkischer Kreis 	051108	\N	\N	\N	\N	\N	O	2012-08-14 09:18:22
2239	05	051107	\N	\N	\N	051107 §7-Hochsauerlandkreis	051107	\N	\N	\N	\N	\N	O	2012-08-14 09:18:22
2240	05	051106	\N	\N	\N	051106 §7-Ennepe-Ruhr-Kreis	051106	\N	\N	\N	\N	\N	O	2012-08-14 09:18:22
2241	05	051105	\N	\N	\N	051105 §7-Stadt Herne	051105	\N	\N	\N	\N	\N	O	2012-08-14 09:18:22
2242	05	051104	\N	\N	\N	051104 §7-Stadt Hamm	051104	\N	\N	\N	\N	\N	O	2012-08-14 09:18:22
2243	05	051103	\N	\N	\N	051103 §7-Stadt Hagen	051103	\N	\N	\N	\N	\N	O	2012-08-14 09:18:22
2244	05	051102	\N	\N	\N	051102 §7-Stadt Dortmund	051102	\N	\N	\N	\N	\N	O	2012-08-14 09:18:22
2245	05	051101	\N	\N	\N	051101 §7-Stadt Bochum Ordnungsamt	051101	\N	\N	\N	\N	\N	O	2012-08-14 09:18:22
2253	05	051306	\N	\N	\N	051306 §7-Stadt Mülheim an der Ruhr 	051306	\N	\N	\N	\N	\N	O	2012-08-14 09:19:29
2254	05	051305	\N	\N	\N	051305 §7-Stadt Mönchengladbach 	051305	\N	\N	\N	\N	\N	O	2012-08-14 09:18:22
2255	05	051304	\N	\N	\N	051304 §7-Stadt Krefeld 	051304	\N	\N	\N	\N	\N	O	2012-08-14 09:18:22
2256	05	051303	\N	\N	\N	051303 §7-Stadt Essen	051303	\N	\N	\N	\N	\N	O	2012-08-14 09:18:22
2257	05	051302	\N	\N	\N	051302 §7-Stadt Duisburg 	051302	\N	\N	\N	\N	\N	O	2012-08-14 09:18:22
2258	05	051301	\N	\N	\N	051301 §7-Stadt Düsseldorf 	051301	\N	\N	\N	\N	\N	O	2012-08-14 09:18:22
2259	05	051207	\N	\N	\N	051207 §7-Kreis Paderborn 	051207	\N	\N	\N	\N	\N	O	2012-08-14 09:18:22
2260	05	051206	\N	\N	\N	051206 §7-Kreis Minden-Lübbecke 	051206	\N	\N	\N	\N	\N	O	2012-08-14 09:18:22
2261	05	051205	\N	\N	\N	051205 §7-Kreis Lippe	051205	\N	\N	\N	\N	\N	O	2012-08-14 09:18:22
2262	05	051204	\N	\N	\N	051204 §7-Kreis Höxter 	051204	\N	\N	\N	\N	\N	O	2012-08-14 09:18:22
2263	05	051203	\N	\N	\N	051203 §7-Kreis Herford	051203	\N	\N	\N	\N	\N	O	2012-08-14 09:18:22
2264	05	051202	\N	\N	\N	051202 §7-Kreis Gütersloh	051202	\N	\N	\N	\N	\N	O	2012-08-14 09:18:22
2265	05	051201	\N	\N	\N	051201 §7-Stadt Bielefeld 	051201	\N	\N	\N	\N	\N	O	2012-08-14 09:18:22
2266	05	051410	\N	\N	\N	051410 §7-Oberbergischer Kreis 	051410	\N	\N	\N	\N	\N	O	2012-08-14 09:19:29
2267	05	051409	\N	\N	\N	051409 §7-Kreis Heinsberg 	051409	\N	\N	\N	\N	\N	O	2012-08-14 09:19:29
2268	05	051408	\N	\N	\N	051408 §7-Kreis Euskirchen	051408	\N	\N	\N	\N	\N	O	2012-08-14 09:19:29
2269	05	051407	\N	\N	\N	051407 §7-Rhein-Erft-Kreis 	051407	\N	\N	\N	\N	\N	O	2012-08-14 09:19:29
2270	05	051406	\N	\N	\N	051406 §7-Kreis Düren 	051406	\N	\N	\N	\N	\N	O	2012-08-14 09:19:29
2354	18	EL-005	\N	\N	\N	LUFA-ITL Kiel	LUFA-ITL	\N	\N	\N	\N	\N	\N	2016-05-25 14:50:01
2272	05	051404	\N	\N	\N	051404 §7-Stadt Leverkusen 	051404	\N	\N	\N	\N	\N	O	2012-08-14 09:19:29
2273	05	051403	\N	\N	\N	051403 §7-Stadt Köln 	051403	\N	\N	\N	\N	\N	O	2012-08-14 09:19:29
2274	05	051402	\N	\N	\N	051402 §7-Stadt Bonn 	051402	\N	\N	\N	\N	\N	O	2012-08-14 09:19:29
2276	05	051316	\N	\N	\N	051316 §7-Stadt Solingen 	051316	\N	\N	\N	\N	\N	O	2012-08-14 09:19:29
2277	05	051315	\N	\N	\N	051315 §7-Kreis Wesel 	051315	\N	\N	\N	\N	\N	O	2012-08-14 09:19:29
2278	05	051314	\N	\N	\N	051314 §7-Kreis Viersen 	051314	\N	\N	\N	\N	\N	O	2012-08-14 09:19:29
2279	05	051313	\N	\N	\N	051313 §7-Rhein-Kreis Neuss	051313	\N	\N	\N	\N	\N	O	2012-08-14 09:19:29
2280	05	051312	\N	\N	\N	051312 §7-Kreis Mettmann 	051312	\N	\N	\N	\N	\N	O	2012-08-14 09:19:29
2281	05	051311	\N	\N	\N	051311 §7-Kreis Kleve 	051311	\N	\N	\N	\N	\N	O	2012-08-14 09:19:29
2298	06	061002	\N	\N	Tierärztliche Grenzkontrollstelle Hessen Flughafen Frankfurt	LHL, TGSH, Landesbetrieb Hessisches Landeslabor	LHL TGSH	Frankfurt am Main	60549	Perishable Center Gebäude 454	\N	\N	O	2013-02-08 13:34:01
2285	05	051307	\N	\N	\N	051307 §7-Stadt Oberhausen 	051307	\N	\N	\N	\N	\N	O	2012-08-14 09:19:29
2286	05	051508	\N	\N	\N	051508 §7-Kreis Warendorf 	051508	\N	\N	\N	\N	\N	O	2012-08-14 09:20:00
2287	05	051507	\N	\N	\N	051507 §7-Kreis Steinfurt 	051507	\N	\N	\N	\N	\N	O	2012-08-14 09:20:00
2288	05	051506	\N	\N	\N	051506 §7-Kreis Recklinghausen	051506	\N	\N	\N	\N	\N	O	2012-08-14 09:20:00
2289	05	051505	\N	\N	\N	051505 §7-Kreis Coesfeld 	051505	\N	\N	\N	\N	\N	O	2012-08-14 09:20:00
2290	05	051504	\N	\N	\N	051504 §7-Kreis Borken 	051504	\N	\N	\N	\N	\N	O	2012-08-14 09:20:00
2291	05	051503	\N	\N	\N	051503 §7-Stadt Münster 	051503	\N	\N	\N	\N	\N	O	2012-08-14 09:20:00
2292	05	051502	\N	\N	\N	051502 §7-Stadt Gelsenkirchen	051502	\N	\N	\N	\N	\N	O	2012-08-14 09:19:29
2293	05	051501	\N	\N	\N	051501 §7-Stadt Bottrop	051501	\N	\N	\N	\N	\N	O	2012-08-14 09:19:29
2294	05	051413	\N	\N	\N	051413 §7-Städte Region Aachen	051413	\N	\N	\N	\N	\N	O	2012-08-14 09:19:29
2295	05	051412	\N	\N	\N	051412 §7-Rhein-Sieg-Kreis 	051412	\N	\N	\N	\N	\N	O	2012-08-14 09:19:29
2296	05	051411	\N	\N	\N	051411 §7-Rheinisch-Bergischer Kreis 	051411	\N	\N	\N	\N	\N	O	2012-08-14 09:19:29
2300	12	S15	\N	\N	\N	Hubertus Menge	PB15	Berlin	12055	Lahnstraße 87	030/ 6844990	\N	\N	2012-10-18 07:24:42
2299	17	Ernst	Ernst	\N	ZINST MCH	Markus Ernst	ME	Garching	85748	Ingolstädter Landstraße 102	\N	\N	E	2012-07-11 06:45:43
2304	12	OB1	Herr Volk	\N	LFB	Oberförsterei Milmersdorf	Obf Mi	Milmersdorf	17268	Forstweg 2	039886 3066	\N	\N	2013-02-18 09:54:55
2305	12	OB2	Fr Lehmbeck, Hr Holzapfel	\N	LFB	Oberförsterei Lehnin	Obf Le	Kloster Lehnin	14797	Am Fischersberg 6	03382 310	\N	\N	2013-02-18 09:54:55
2306	12	OB3	Herr Plantz	\N	LFB	Oberförsterei Waldsieversdorf	Obf Walds	Waldsieversdorf	15377	Eberswalder Chaussee 3	033433 1515104	\N	\N	2013-02-18 09:54:55
2307	12	OB4	Herr Pachtmann	\N	LFB	Oberförsterei Calau	Obf Calau	Calau	03205	Lindenstraße 7	03541 2219	\N	\N	2013-02-18 09:54:55
2308	12	OB5	Herr Woll	\N	LFB	Oberförsterei Wünsdorf	Obf Wünsd	Zossen	15806	Steinplatz 1	033702 73200	\N	\N	2013-02-18 09:54:55
2309	12	OB6	Herr Neumann	\N	LFB	Oberförsterei Herzberg	Obf Herzbg	Herzberg	04916	Am Sender 1	03535 22576	\N	\N	2013-02-18 09:54:55
2310	12	OB7	Herr Paustian	\N	LFB	Oberförsterei Erkner	Obf Erkner	Erkner	15537	Oberförstereiweg 1	03362 3135	\N	\N	2013-02-18 09:54:55
2311	12	OB8	Herr Koschenz	\N	LFB	Oberförsterei Neuruppin	Obf Neur	Alt Ruppin	16827	Friedrich-Engels-Straße 33a	03391 40378-0	\N	\N	2013-02-18 09:54:55
2312	12	OB9	Herr Kluth	\N	LFB	Oberförsterei Gadow	Obf Gadow	Lanz	19309	Lindenallee (Forsthof)	038780 7320	\N	\N	2013-02-18 09:54:55
2314	07	071106	\N	\N	\N	Kreisverwaltung Cochem-Zell	KV COC	Cochem	56812	Endertplatz 2	\N	\N	O	2013-04-08 08:18:31
2315	07	071105	\N	\N	\N	Kreisverwaltung Birkenfeld	KV BIR	Birkenfeld	55765	Schneewiesenstr. 25	\N	\N	O	2013-04-08 08:18:31
2316	07	071104	\N	\N	\N	Kreisverwaltung Bad Kreuznach	KV KH	Bad Kreuznach	55543	Salinenstr. 7	\N	\N	O	2013-04-08 08:18:31
2317	07	071103	\N	\N	\N	Kreisverwaltung Altenkirchen	KV AK	Altenkirchen	57610	Parkstr.	\N	\N	O	2013-04-08 08:18:31
2318	07	071102	\N	\N	\N	Kreisverwaltung Ahrweiler	KV AH	Bad Neuenahr-Ahrweil	53474	Wilhelmstr. 24-30	\N	\N	O	2013-04-08 08:18:31
2319	07	071111	\N	\N	\N	Kreisverwaltung des Westerwaldkreises	KV WW	Montabaur	56410	Peter-Altmeier-Platz 1	\N	\N	O	2013-04-08 08:18:31
277	09	414	\N	\N	\N	Landwirtschaftsamt Ebersberg	LwA EBE	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
278	09	415	\N	\N	\N	Landwirtschaftsamt Eggenfelden	LwA PAN	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
279	09	416	\N	\N	\N	Landwirtschaftsamt Erding	LwA ED	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
280	09	417	\N	\N	\N	Landwirtschaftsamt Forchheim	LwA FO	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
281	09	418	\N	\N	\N	Landwirtschaftsamt Fürth	LwA FÜ	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
282	09	419	\N	\N	\N	Landwirtschaftsamt Friedberg	LwA AIC	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
283	09	420	\N	\N	\N	Landwirtschaftsamt Fürstenfeldbruck	LwA FFB	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
284	09	421	\N	\N	\N	Landwirtschaftsamt Höchstadt a.d. Aisch	LwA ERH	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
285	09	422	\N	\N	\N	Landwirtschaftsamt Hersbruck	LwA N	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
286	09	423	\N	\N	\N	Landwirtschaftsamt Hofheim	LwA HAS	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
287	09	424	\N	\N	\N	Landwirtschaftsamt Ingolstadt	LwA IN	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
288	09	425	\N	\N	\N	Landwirtschaftsamt Karlstadt	LwA MSP	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
289	09	426	\N	\N	\N	Landwirtschaftsamt Kaufbeuren	LwA KF	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
172	09	109	\N	\N	\N	Landratsamt Bayreuth	LRA BT	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
173	09	110	\N	\N	\N	Landratsamt Berchtesgadener Land	LRA BGL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
174	09	111	\N	\N	\N	Landratsamt Cham	LRA CHA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
175	09	112	\N	\N	\N	Landratsamt Coburg	LRA CO	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
176	09	113	\N	\N	\N	Landratsamt Dachau	LRA DAH	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
177	09	114	\N	\N	\N	Landratsamt Deggendorf	LRA DEG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
178	09	115	\N	\N	\N	Landratsamt Dillingen	LRA DLG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
179	09	116	\N	\N	\N	Landratsamt Dingolfing-Landau	LRA DGF	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
180	09	117	\N	\N	\N	Landratsamt Donau-Ries	LRA DON	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
181	09	118	\N	\N	\N	Landratsamt Ebersberg	LRA EBE	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
182	09	119	\N	\N	\N	Landratsamt Eichstätt	LRA EI	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
183	09	120	\N	\N	\N	Landratsamt Erding	LRA ED	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
184	09	121	\N	\N	\N	Landratsamt Erlangen-Höchstadt	LRA ERH	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
185	09	122	\N	\N	\N	Landratsamt Forchheim	LRA FO	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
186	09	123	\N	\N	\N	Landratsamt Freising	LRA FS	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
187	09	124	\N	\N	\N	Landratsamt Freyung-Grafenau	LRA FRG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
188	09	125	\N	\N	\N	Landratsamt Fürstenfeldbruck	LRA FFB	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
189	09	126	\N	\N	\N	Landratsamt Fürth	LRA FÜ	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
190	09	127	\N	\N	\N	Landratsamt Garmisch-Partenkirchen	LRA GAP	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
191	09	128	\N	\N	\N	Landratsamt Günzburg	LRA GZ	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
192	09	129	\N	\N	\N	Landratsamt Haßberge	LRA HAS	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
193	09	130	\N	\N	\N	Landratsamt Hof	LRA HO	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
194	09	131	\N	\N	\N	Landratsamt Kelheim	LRA KEH	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
195	09	132	\N	\N	\N	Landratsamt Kitzingen	LRA KT	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
196	09	133	\N	\N	\N	Landratsamt Kronach	LRA KC	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
197	09	134	\N	\N	\N	Landratsamt Kulmbach	LRA KU	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
198	09	135	\N	\N	\N	Landratsamt Landsberg am Lech	LRA LL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
199	09	136	\N	\N	\N	Landratsamt Landshut	LRA LA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
200	09	137	\N	\N	\N	Landratsamt Lichtenfels	LRA LIF	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
201	09	138	\N	\N	\N	Landratsamt Lindau	LRA LI	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
202	09	139	\N	\N	\N	Landratsamt Main-Spessart	LRA MSP	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
203	09	140	\N	\N	\N	Landratsamt Miesbach	LRA MB	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
204	09	141	\N	\N	\N	Landratsamt Miltenberg	LRA MIL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
205	09	142	\N	\N	\N	Landratsamt Mühldorf am Inn	LRA MÜ	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
206	09	143	\N	\N	\N	Landratsamt München	LRA M	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
207	09	144	\N	\N	\N	Landratsamt Neu-Ulm	LRA NU	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
208	09	145	\N	\N	\N	Landratsamt Neuburg-Schrobenhausen	LRA ND	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
209	09	146	\N	\N	\N	Landratsamt Neumarkt i.d.Opf.	LRA NM	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
210	09	147	\N	\N	\N	Landratsamt Neustadt a.d. Aisch	LRA NEA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
211	09	148	\N	\N	\N	Landratsamt Neustadt a.d. Waldnaab	LRA NEW	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
212	09	149	\N	\N	\N	Landratsamt Nürnberger Land	LRA LAU	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
213	09	150	\N	\N	\N	Landratsamt Oberallgäu	LRA OA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
215	09	152	\N	\N	\N	Landratsamt Passau	LRA PA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
216	09	153	\N	\N	\N	Landratsamt Pfaffenhofen a.d. Ilm	LRA PAF	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
217	09	154	\N	\N	\N	Landratsamt Regen	LRA REG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
218	09	155	\N	\N	\N	Landratsamt Regensburg	LRA R	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1519	18	EL-003	\N	\N	\N	ERAM in Morsleben	ERAM	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1557	12	S01	\N	dienstl. 0331 316281	\N	Wolfgang Bivour	PB 01	Satzkorn	14476	Tulpenweg 13	033208 51605	\N	\N	2000-01-01 00:00:00
1558	12	S02	\N	\N	\N	Karin Drechsler	PB 02	Schwedt/Oder	16303	Dr.-Wilhelm-Külz-Viertel 14	03331 32204	\N	\N	2000-01-01 00:00:00
1559	12	S03	\N	\N	\N	Gerhard Grune	PB 03	Frankfurt (Oder)	15230	Karl-Marx-Straße 20	0335 321748	\N	\N	2000-01-01 00:00:00
1560	12	S04	\N	\N	\N	Konrad Hannemann	PB 04	Eisenhüttenstadt	15890	Bergstraße 15	03364 415400	\N	\N	2000-01-01 00:00:00
1561	12	S05	\N	\N	\N	Ekkehard Jesse	PB 05	Perleberg	19348	Wittenberger Straße 45a	03876 616698	\N	\N	2000-01-01 00:00:00
1562	12	S06	\N	\N	\N	Irma Köppe	PB 06	Schöneiche b. Berlin	15566	Heuweg 67a	030 6492565	\N	\N	2000-01-01 00:00:00
1563	12	S07	\N	\N	\N	Jutta Meseck	PB 07	Berge	19348	Grüner Weg 6	\N	\N	\N	2000-01-01 00:00:00
1564	12	S08	\N	\N	\N	Renè Schumacher	PB 08	Fürstenwalde/Spree	15517	Hölderlinstraße 25	03361 306062	\N	\N	2000-01-01 00:00:00
1565	12	S09	\N	\N	\N	Peter Wöhl	PB 09	Lieberose	15868	Trebitzer Dorfstraße 14a	\N	\N	\N	2000-01-01 00:00:00
1566	12	S10	\N	\N	\N	Ingeborg Rogèe	PB 10	Fürstenwalde/Spree	15517	Lützowring 40	03361 340613	\N	\N	2000-01-01 00:00:00
1567	12	S11	\N	\N	\N	Gerhard Ehlers	PB 11	Wittenberge	19322	Drosselweg 5	\N	\N	\N	2000-01-01 00:00:00
1577	05	171	\N	\N	\N	Kernkraftwerk Grohnde (für MPA)	KWG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1597	05	201	\N	\N	\N	Zwischenlager Gorleben (für MPA)	TBL/PKA	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1598	05	231	\N	\N	\N	Standortzwischenlager KKE (für MPA)	SZL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1599	05	191	\N	\N	\N	Kernkraftwerk Emsland (für MPA)	KKE	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1600	05	151	\N	\N	\N	Kernkraftwerk Stade (für MPA)	KKS	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1617	19	1	Fr. Müller	Probenehmer zum Testen des Probenbegleitscheins	LfMKw	Landesamt für Milchwirtschaft und Kuhweideflächennutzung Bayern im Hinterhof 7	1	Vorderniederaichbach	80122	Kuhweidenstraße 7 im Hinterhof	088/8888	kpl	P	2010-11-01 16:40:19
1658	09	074	\N	\N	\N	Wasserwirtschaftsamt Bad Kissingen	WWA KG	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1659	09	075	\N	\N	\N	Wasserwirtschaftsamt Kronach	WWA KC	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1177	12	PRÜ	\N	\N	\N	LVLF Referat 21 (Dr. Naumann)	R21	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1197	12	V13	\N	\N	\N	VLÜA OHV Gransee	VLÜA OHV	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1198	12	V14	\N	\N	\N	VLÜA OPR Neuruppin	VLÜA OPR	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1199	12	V15	\N	\N	\N	VLÜA PR Perleberg	VLÜA PR	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1200	12	V16	\N	\N	\N	VLÜA HVL Nauen	VLÜA HVL	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1201	12	V17	\N	\N	\N	VLÜA BRB Brandenburg a. d. Havel	VLÜA BRB	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1202	12	V18	\N	\N	\N	VLÜA PM Belzig	VLÜA PM	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1203	12	V19	\N	\N	\N	VLÜA TF Luckenwalde	VLÜA TF	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1204	12	V20	\N	\N	\N	VLÜA P Potsdam	VLÜA P	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1397	17	Eckert	Eckert	\N	ZINST MCH	Guido Eckert	GE	Garching	85748	Ingolstädter Landstraße 102	\N	\N	E	2000-01-01 00:00:00
1398	17	Zimmer	Zimmer	\N	ZINST MCH	Carsten Zimmer	CZ	Garching	85748	Ingolstädter Landstraße 102	\N	\N	E	2000-01-01 00:00:00
1399	17	Jenczer	Jenczer	\N	ZINST MCH	Frank Jenczer	FJ	Garching	85748	Ingolstädter Landstraße 102	\N	\N	E	2000-01-01 00:00:00
1437	07	151	\N	\N	\N	Frau Burghardt	LfU Mainz	\N	\N	\N	\N	\N	\N	2016-07-05 12:56:58
1497	05	EL-001	\N	\N	\N	LAU Halle, Landesmeßstelle Süd in Halle	Lmst Süd	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1638	17	Vethers	Vethers	\N	ZINST MCH	Konstantin Vethers	KV	Garching	85748	Ingolstädter Landstraße 102	\N	\N	E	2011-10-17 13:36:21
1205	12	FFO	\N	\N	\N	Landesmessstelle Frankfurt (Oder)	LMST FFO	\N	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1337	13	MV3	\N	\N	Energiewerke Nord GmbH - Kernkraftwerk Lubmin/Greifswald	Kernkraftwerk Greifswald (KGR)	MV3	Lubmin	\N	\N	\N	\N	\N	2000-01-01 00:00:00
1338	13	MV4	\N	\N	Energiewerke Nord GmbH - Zwischenlager Nord	Zwischenlager Nord (ZLN)	MV4	Lubmin	\N	\N	\N	\N	\N	2000-01-01 00:00:00
2445	08	630	\N	landwirtschaft-naturschutz@rhein-neckar-kreis.de	\N	Landratsamt Rhein-Neckar-Kreis	LRA_HD_L	Sinsheim	74889	Muthstr. 4	(07261) 9466-5300	\N	\N	2017-01-11 06:59:43
2433	08	747	\N	susanne.Dalcolmo@kreis-calw.de	Landwirtschaft und Naturschutz	Landratsamt Calw	LRA_CW_L	Calw	75365	Vogteistraße 42-46	(07051) 160-951	\N	\N	2016-12-14 12:37:58
2434	08	748	\N	landwirtschaftsamt@enzkreis.de	Landwirtschaftsamt	Landratsamt Enzkreis	LRA_PF_L	Pforzheim	75177	Zähringerallee 3	(07231) 308 1800	\N	\N	2016-12-14 12:37:58
2435	08	749	\N	landwirtschaft@neckar-odenwaldkreis.de	Fachdienst Landwirtschaft	Landratsamt Neckar-Odenwald-Kreis	LRA_BU_L	Buchen	74722	Präsident-Wittemann-Str 9 + 14	(06281) 5212-1600	\N	\N	2017-01-11 06:59:12
2436	08	762	\N	landwirtschaftsamt@lrakn.de	Amt für Landwirtschaft	Landratsamt Konstanz	LRA_KN_L	Stockach	78333	Winterspürer Str. 25	(07531) 800-2966	\N	\N	2016-12-14 12:37:58
2437	08	763	\N	landwirtschaftsamt@ortenaukreis.de	Amt für Landwirtschaft	Landratsamt Ortenaukreis, Offenburg	LRA_OG_L	Offenburg	77654	Prinz-Eugen-Str. 2	(0781) 805-7100	\N	\N	2016-12-14 12:37:58
2439	08	764	\N	landwirtschaftsamt@landkreis-waldshut.de	Landwirtschaftsamt	Landratsamt Waldshut	LRA_WT_L	Waldshut-Tiengen	79761	Gartenstr. 7	(07751) 86-5301	\N	\N	2016-12-14 13:18:04
2440	08	765	\N	landwirtschaftsamt@lrasbk.de	Landwirtschaftsamt	Landratsamt Schwarzwald-Baar-Kreis	LRA_VS_L	Donaueschingen	78166	Humboldtstr. 11	(07721) 913-5300	\N	\N	2016-12-14 13:18:04
2441	08	766	\N	landwirtschaftsamt@landkreis-emmendingen.de	Landwirtschaftsamt	Landratsamt Emmendingen	LRA_EM_L	Emmendingen	79312	Schwarzwaldstr. 4	(07641) 451-9110	\N	\N	2016-12-14 13:18:04
2442	08	767	\N	landwirtschaft@loerrach-landkreis.de	Landwirtschaft und Naturschutz	Landratsamt Lörrach	LRA_LÖ_L	Lörrach	79539	Palmstr. 3	(07621) 410-0	\N	\N	2016-12-14 13:18:04
2443	08	760	\N	gesundheitsamt@breisgau-hochschwarzwald.de	Gesundheitsamt	Landratsamt Breisgau-Hochschwarzwald	LRA_FR_GA	Freiburg	79104	Sautierstr. 30	(0761) 2187-3800	\N	\N	2017-01-11 06:57:31
2444	08	761	\N	wasserwirtschaft-boden@ortenaukreis.de	Wasserwirtschaft und Bodenschutz	Landratsamt Ortenaukreis, Offenburg	LRA_OG_WW	Offenburg	77652	Badstr. 20	(0781) 805-9650	\N	\N	2016-12-14 13:18:04
2446	08	631	\N	veterinaeramt@heidelberg.de	\N	Stadt Heidelberg	LRA_HD	Heidelberg	69115	Bergheimer Str. 69	(06221) 58-17110	\N	\N	2016-12-14 13:23:23
2447	08	768	\N	landwirtschaft@lkbh.de	Fachbereich Landwirtschaft	Landratsamt Breisgau-Hochschwarzwald	LRA_FR_L	Breisach	79206	Europaplatz 3	(0761) 2187-9580	\N	\N	2017-01-11 07:14:23
2451	07	410	\N	\N	\N	Kläranlage Koblenz	KA KO	Koblenz	56070	Kammertsweg 82	\N	\N	\N	2017-01-30 09:17:31
2422	12	S19	\N	\N	\N	Jürgen Neuendorf	PB19	Bad Belzig	\N	\N	\N	\N	\N	2016-11-30 11:36:54
2450	08	632	\N	\N	Futtermittelüberwachung	Regierungsräsidium Freiburg	RP_FR	\N	\N	\N	\N	\N	\N	2017-01-05 12:19:53
2452	07	411	\N	\N	\N	MHKW Pirmasens	MHKW	Pirmasens	66954	Staffelberg 2	\N	\N	\N	2017-03-13 08:18:18
2453	19	971	\N	\N	KHG	Kerntechnischer Hilfsdienst	KHG	Eggenstein-Lea	76334	Am Schröcker Tor 1	0 72 47 / 81  0	\N	\N	2017-03-17 08:07:33
2423	12	F41	Müller	\N	\N	Mitarbeiter Landeslabor Fachbereich IV-1	LLBB IV-1	\N	\N	\N	\N	\N	\N	2016-12-01 08:52:36
2421	09	971	\N	\N	\N	Kerntechnischer Hilfsdienst	KHG	\N	\N	\N	\N	\N	\N	2016-10-04 10:29:28
\.


--
-- Name: probenehmer_id_seq; Type: SEQUENCE SET; Schema: stammdaten; Owner: postgres
--

SELECT pg_catalog.setval('probenehmer_id_seq', 1859, true);


--
-- PostgreSQL database dump complete
--

