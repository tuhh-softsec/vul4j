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
-- TOC entry 4679 (class 0 OID 535710)
-- Dependencies: 244
-- Data for Name: deskriptoren; Type: TABLE DATA; Schema: stammdaten; Owner: lada
--

COPY deskriptoren (id, vorgaenger, ebene, s_xx, sn, beschreibung, bedeutung) FROM stdin;
2	\N	0	1	1	ZEBS-Medien	\N
3	\N	0	11682	2	Futtermittel	\N
4	\N	0	11940	3	Tierische u. pflanzliche Produkte	\N
5	\N	0	11996	4	Boden	\N
6	\N	0	12375	5	Düngemittel	\N
7	\N	0	12483	6	Wasser	\N
8	\N	0	12677	7	Luft und Niederschlag	\N
9	\N	0	12715	8	Abwasser und Schlamm	\N
10	\N	0	12758	9	Abfälle und Reststoffe	\N
11	\N	0	12911	10	Baustoffe und Bauten	\N
12	\N	0	12982	11	Lebewesen	\N
13	\N	0	12993	12	Ortsdosis	\N
14	\N	0	13029	13	Arzneimittel u. deren Ausgangsstoffe	\N
15	\N	0	13159	14	Dosisleistung	\N
16	\N	0	13163	15	Neutronen-Ortsdosis	\N
17	\N	0	13167	16	Neutronen-Ortsdosisleistung	\N
18	\N	0	13170	17	Bodenschätze	\N
19	\N	0	13171	99	Sonstiges	\N
20	2	1	2	1	Milch	\N
21	2	1	77	2	Milchprodukte ausgenommen 030000 u. 040000	\N
22	2	1	305	3	Käse	\N
23	2	1	613	4	Butter	\N
24	2	1	662	5	Eier und Eiprodukte	\N
25	2	1	765	6	Fleisch warmblütiger Tiere auch tiefgefroren	\N
26	2	1	1352	7	Fleischerzeugnisse warmblütiger Tiere ausgenommen 080000	\N
27	2	1	1858	8	Wurstwaren	\N
28	2	1	2853	10	Fische und Fischzuschnitte	\N
29	2	1	3309	11	Fischerzeugnisse	\N
30	2	1	3720	12	Krusten- Schalen- Weichtiere sonstige Tiere u. Erzeugnisse daraus	\N
31	2	1	3913	13	Fette und Öle ausgenommen 040000	\N
32	2	1	4046	14	Suppen und Soßen ausgenommen 200000 und 520100	\N
33	2	1	4221	15	Getreide	\N
34	2	1	4259	16	Getreideprodukte Backvormischungen Brotteige Massen und Teige für Backwaren	\N
35	2	1	4459	17	Brote und Kleingebäcke	\N
36	2	1	4668	18	Feine Backwaren	\N
37	2	1	4906	20	Mayonnaisen emulgierte Soßen kalte Fertigsoßen Feinkostsalate	\N
38	2	1	5150	21	Puddinge Kremspeisen Desserts süße Soßen	\N
39	2	1	5258	22	Teigwaren	\N
40	2	1	5303	23	Hülsenfrüchte Ölsamen Schalenobst	\N
41	2	1	5449	24	Kartoffeln und stärkereiche Pflanzenteile	\N
42	2	1	5534	25	Frischgemüse ausgenommen Rhabarber	\N
43	2	1	5646	26	Gemüseerzeugn. und Gemüsezuber. ausgen. Rhabarber u. 200700 u. 201700	\N
44	2	1	5913	27	Pilze	\N
45	2	1	6038	28	Pilzerzeugnisse	\N
46	2	1	6143	29	Frischobst einschließlich Rhabarber	\N
47	2	1	6241	30	Obstprodukte ausgenommen 310000 und 410000 einschl. Rhabarber	\N
48	2	1	6441	31	Fruchtsäfte Fruchtnektare Fruchtsirupe Fruchtsäfte getrocknet	\N
49	2	1	6659	32	Alkoholfreie Getränke Getränkeansätze Getränkepulver auch brennwertreduziert	\N
50	2	1	6797	33	Weine und Traubenmoste	\N
51	2	1	7472	34	Erzeugnisse aus Wein auch Vor- und Nebenprodukte der Weinbereitung	\N
52	2	1	7519	35	Weinähnliche Getränke sowie deren Weiterverarbeitungserzeugnisse auch alkoholreduziert o.alkoholfrei	\N
53	2	1	7620	36	Biere bierähnliche Getränke und Rohstoffe für die Bierherstellung	\N
54	2	1	7776	37	Spirituosen und spirituosenhaltige Getränke	\N
55	2	1	8029	39	Zucker	\N
56	2	1	8072	40	Honige Imkereierzeugnisse und Brotaufstriche auch brennwertvermindert ausgenommen 410000	\N
57	2	1	8177	41	Konfitüren Gelees Marmeladen Fruchtzubereitungen auch brennwertreduziert	\N
58	2	1	8342	42	Speiseeis und Speiseeishalberzeugnisse	\N
59	2	1	8563	43	Süßwaren ausgenommen 440000	\N
60	2	1	8726	44	Schokoladen und Schokoladenwaren	\N
61	2	1	8795	45	Kakao	\N
62	2	1	8832	46	Kaffee Kaffeeersatzstoffe Kaffeezusätze	\N
63	2	1	8880	47	Tees und teeähnliche Erzeugnisse	\N
64	2	1	8979	48	Säuglings- und Kleinkindernahrungen	\N
65	2	1	9044	49	Diätetische Lebensmittel	\N
66	2	1	9122	50	Fertiggerichte und zubereitete Speisen ausgenommen 480000	\N
67	2	1	9309	51	Nahrungsergänzungsmittel	\N
68	2	1	9349	52	Würzmittel	\N
69	2	1	9503	53	Gewürze	\N
70	2	1	9614	54	Aromastoffe	\N
71	2	1	9645	56	Hilfsmittel aus Zusatzstoffen u./o. LM und Convenience-Produkte	\N
72	2	1	9704	57	Zusatzstoffe und wie Zusatzstoffe verwendete Lebensmittel und Vitamine	\N
73	2	1	10069	59	Trinkwasser Mineralwasser Tafelwasser Quellwasser Brauchwasser	\N
74	2	1	10187	60	Rohtabake Tabakerzeugnisse Tabakersatz sowie Stoffe u. Gegenstände für die Herstellung von Tabakerz.	\N
75	2	1	10262	81	Verpackungsmaterialien für kosmetische Mittel und für Tabakerzeugnisse	\N
76	2	1	10295	82	Bedarfsgegenstände mit Körperkontakt und zur Körperpflege	\N
77	2	1	10414	83	Bedarfsgegenstände zur Reinigung und Pflege sowie sonst. Haushaltschemikalien	\N
78	2	1	10500	84	Kosmetische Mittel und Stoffe zu deren Herstellung	\N
79	2	1	10685	85	Spielwaren und Scherzartikel	\N
80	2	1	10733	86	Bedarfsgegenstände mit Lebensmittelkontakt (BgLm)	\N
81	2	1	10815	91	Morphologisch unveränderte Gewebe	\N
82	2	1	11051	92	Morphologisch veränderte Gewebe	\N
83	2	1	11301	93	Neoplastische, hyperplastische Gewebe	\N
84	2	1	11494	94	Nekrotische Gewebe	\N
85	2	1	13213	89	Kein Erzeugnis nach LMBG/LFGB	\N
86	3	1	11683	0	Futtermittel	\N
87	3	1	11684	1	Grünfutter	\N
88	3	1	11729	2	Silagen	\N
89	3	1	11761	3	Heu, Cobs, Stroh	\N
90	3	1	11790	4	Haupterzeugnisse trocken	\N
91	3	1	11835	5	Haupterzeugnisse feucht	\N
92	3	1	11859	6	Nebenerzeugnisse trocken	\N
93	3	1	11904	7	Nebenerzeugnisse feucht	\N
94	3	1	11925	8	Kraftfuttermischungen	\N
95	3	1	13221	98	Weide- und Wiesenbewuchs - Störfall	\N
96	3	1	13222	99	Grünfutterpflanzen (außer Weide- und Wiesenbewuchs) - Störfall	\N
97	4	1	11941	0	Tierische u. pflanzliche Produkte	\N
98	4	1	11942	1	Faserpflanzen	\N
99	4	1	11943	2	Sämereien, Grassamen	\N
100	4	1	11944	3	Ölerzeugnisse	\N
101	4	1	11945	4	Wolle, Haare, Federn	\N
102	4	1	11946	5	Blumen	\N
103	4	1	11957	6	Bäume	\N
104	4	1	11977	7	Sträucher	\N
105	4	1	11982	8	Stauden	\N
106	4	1	11984	9	Pflanzliche Indikatoren	\N
107	5	1	11997	0	Boden	\N
108	5	1	11998	1	Unbearbeiteter Boden	\N
109	5	1	12353	2	Ackerboden	\N
110	5	1	12354	3	Bodenauflage	\N
111	5	1	12367	4	Boden in-situ	\N
112	5	1	13218	97	Bodenoberfläche, unversiegelt - Störfall	\N
113	5	1	13219	98	Bodenoberfläche, versiegelt - Störfall	\N
114	5	1	13220	99	Störfall	\N
115	6	1	12376	0	Düngemittel	\N
116	6	1	12377	1	Mineralische Düngemittel	\N
117	6	1	12462	2	Organische Düngemittel	\N
118	7	1	12484	0	Wasser	\N
119	7	1	12485	1	Meerwasser	\N
120	7	1	12526	2	Oberflächenwasser	\N
121	7	1	12546	3	Grundwasser	\N
122	7	1	12556	4	Sediment	\N
123	7	1	12582	5	Schwebstoffe	\N
124	7	1	12590	6	Sickerwasser	\N
125	7	1	12621	7	Wasserpflanzen	\N
126	7	1	12672	8	Plankton	\N
127	7	1	12676	9	Seston	\N
128	7	1	13223	97	Wasser in Fließgewässer (Oberflächenwasser) - Störfall	\N
129	7	1	13224	98	Wasser in stehenden Gewässern (Oberfläche) - Störfall	\N
130	7	1	13225	99	Wasser zur Viehtränke - Störfall	\N
131	8	1	12678	0	Luft und Niederschlag	\N
132	8	1	12679	1	Luft	\N
133	8	1	12703	2	Niederschlag	\N
134	8	1	12714	3	Prognostizierte Aktivität Luft gesamt	\N
135	8	1	13193	4	Spurenmessungen	\N
136	8	1	13216	98	Luft/Aerosole - Störfall	\N
137	8	1	13217	99	Luft/gasförmiges Iod - Störfall	\N
138	9	1	12716	0	Abwasser und Schlamm	\N
139	9	1	12717	1	Abwasser	\N
140	9	1	12734	2	Schlamm aus Abwässern	\N
141	10	1	12759	0	Abfälle und Reststoffe	\N
142	10	1	12760	1	Reststoffe aus Verbrennungsanlagen	\N
143	10	1	12799	2	Deponie	\N
144	10	1	12834	3	Kompostierung	\N
145	10	1	12858	4	Spezielle Reststoffe und Abfälle	\N
146	10	1	12873	5	Filter	\N
147	10	1	12884	6	Schutzkleidung	\N
148	10	1	12885	7	Ionentauscher	\N
149	10	1	12889	8	Folien	\N
150	10	1	12890	9	Reststoffe aus der Trinkwasseraufbereitung	\N
151	11	1	12912	0	Baustoffe und Bauten	\N
152	11	1	12913	1	Baustoffe	\N
153	11	1	12958	2	Bauten	\N
154	11	1	12979	3	organische Baustoffe	\N
155	11	1	13173	4	Bauschutt	\N
156	12	1	12983	0	Lebewesen	\N
157	12	1	12984	1	Lebewesen (sonstige)	\N
158	12	1	12985	2	tierische Indikatoren	\N
159	13	1	12994	0	Ortsdosis	\N
160	13	1	12995	1	Luft	\N
161	13	1	13000	2	Boden	\N
162	13	1	13027	3	Wasser	\N
163	13	1	13028	4	Jahres-Ortsdosis über Boden	\N
164	13	1	13215	99	Luft - Störfall	\N
165	14	1	13030	0	Arzneimittel u. deren Ausgangsstoffe	\N
166	14	1	13031	1	Pflanzliche Ausgangsstoffe	\N
167	14	1	13140	2	Tierische Ausgangsstoffe	\N
168	14	1	13154	3	Mineralische Ausgangsstoffe	\N
169	15	1	13160	0	Dosisleistung	\N
170	15	1	13161	1	nicht nuklidspezifisch	\N
171	15	1	13162	2	in-situ-Messung, nuklidspezifisch	\N
172	15	1	13214	99	nicht nuklidspezifisch - Störfall	\N
173	16	1	13164	0	Neutronen-Ortsdosis	\N
174	16	1	13165	1	Luft	\N
175	16	1	13166	4	Jahres-Ortsdosis über Boden	\N
176	17	1	13168	0	Neutronen-Ortsdosisleistung	\N
177	17	1	13169	1	nicht nuklidspezifisch	\N
178	18	1	13171	0	Bodenschätze	\N
179	18	1	13172	1	Erdgas	\N
180	20	2	3	1	Milch unbearbeitete/Rohmilch	\N
181	20	2	9	2	Milch bearbeitete auch eiweißangereicherte	\N
182	20	2	23	3	Milch anderer Tiere unbearbeitete/Rohmilch	\N
183	20	2	34	5	Milch anderer Tiere bearbeitet	\N
184	20	2	41	6	Milchimitate	\N
185	21	2	78	1	Sauermilcherzeugnisse auch eiweißangereicherte	\N
186	21	2	90	2	Joghurterzeugnisse auch eiweißangereicherte	\N
187	21	2	99	3	Kefirerzeugnisse auch eiweißangereicherte	\N
188	21	2	108	4	Buttermilcherzeugnisse auch eiweißangereicherte	\N
189	21	2	111	5	Sahneerzeugnisse auch eiweißangereicherte	\N
190	21	2	123	6	Kondensmilcherzeugnisse	\N
191	21	2	134	7	Trockenmilcherzeugnisse	\N
192	21	2	154	8	Molken	\N
193	21	2	171	9	Milcheiweißerzeugnisse	\N
194	21	2	179	10	Milchmischerzeugnisse	\N
195	21	2	273	11	Milchprodukte aus Milch anderer Tiere	\N
196	21	2	281	12	Trockenmilcherzeugnisse aus Milch anderer Tiere	\N
197	21	2	283	15	Milchfetterzeugnisse	\N
198	21	2	288	16	Milchstreichfetterzeugnisse	\N
199	21	2	291	17	Milchzuckererzeugnisse	\N
200	21	2	293	18	Zaziki und ähnliche Erzeugnisse	\N
201	21	2	295	90	Milchproduktimitate	\N
202	21	2	298	91	Milcheiweiß und pflanzliche Fette und/oder andere LM	\N
203	21	2	299	92	Magermilchpulver und pflanzliche Fette und/oder andere LM	\N
204	21	2	300	93	Mischdesserts mit Früchten	\N
205	21	2	301	94	Magermilch und pflanzliche Fette und/oder andere LM	\N
206	21	2	303	95	Fermentierte Milcherzeugnisse und pflanzliche Fette und/oder andere LM	\N
207	22	2	306	1	Hartkäse Standardsorten Rahmstufe	\N
208	22	2	308	2	Hartkäse Standardsorten Vollfettstufe	\N
209	22	2	312	3	Hartkäse andere	\N
210	22	2	323	4	Schnittkäse Standardsorten Doppelrahmstufe	\N
211	22	2	325	5	Schnittkäse Standardsorten Rahmstufe	\N
212	22	2	331	6	Schnittkäse Standardsorten Vollfettstufe	\N
213	22	2	336	7	Schnittkäse Standardsorten Fettstufe	\N
214	22	2	340	8	Schnittkäse Standardsorten Dreiviertelfettstufe	\N
215	22	2	344	9	Schnittkäse andere	\N
216	22	2	358	10	Schnittkäse halbfeste Standardsorten Doppelrahmstufe	\N
217	22	2	361	11	Schnittkäse halbfeste Standardsorten Rahmstufe	\N
218	22	2	366	12	Schnittkäse halbfeste Standardsorten Vollfettstufe	\N
219	22	2	371	13	Schnittkäse halbfeste Standardsorten Fettstufe	\N
220	22	2	373	14	Schnittkäse halbfeste Standardsorten Dreiviertelfettstufe	\N
221	22	2	375	15	Schnittkäse halbfeste andere	\N
222	22	2	382	16	Weichkäse Standardsorten Doppelrahmstufe	\N
223	22	2	386	17	Weichkäse Standardsorten Rahmstufe	\N
224	22	2	392	18	Weichkäse Standardsorten Vollfettstufe	\N
225	22	2	398	19	Weichkäse Standardsorten Fettstufe	\N
226	22	2	402	20	Weichkäse Standardsorten Dreiviertelfettstufe	\N
227	22	2	406	21	Weichkäse Standardsorten Halbfettstufe	\N
228	22	2	410	22	Weichkäse andere	\N
229	22	2	415	23	Frischkäse Standardsorten Doppelrahmstufe auch mit Gewürzen/Kräutern	\N
230	22	2	419	24	Frischkäse Standardsorten Rahmstufe auch mit Gewürzen/Kräutern	\N
231	22	2	423	25	Frischkäse Standardsorten Vollfettstufe auch mit Gewürzen/Kräutern	\N
232	22	2	427	26	Frischkäse Standardsorten Fettstufe auch mit Gewürzen/Kräutern	\N
233	22	2	431	27	Frischkäse Standardsort. Dreiviertelfettst. auch mit Gewürzen/Kräutern	\N
234	22	2	435	28	Frischkäse Standardsorten Halbfettstufe auch mit Gewürzen/Kräutern	\N
235	22	2	439	29	Frischkäse Standardsorten Viertelfettstufe auch mit Gewürzen/Kräutern	\N
236	22	2	443	30	Frischkäse Standardsorten Magerstufe auch mit Gewürzen/Kräutern	\N
237	22	2	446	31	Frischkäse andere	\N
238	22	2	450	32	Sauermilchkäse Sauermilchquarkerzeugnisse	\N
239	22	2	464	33	Molkenkäse	\N
240	22	2	466	34	Käsezubereitungen Doppelrahmstufe	\N
241	22	2	471	35	Käsezubereitungen Rahmstufe	\N
242	22	2	476	36	Käsezubereitungen Vollfettstufe	\N
243	22	2	481	37	Käsezubereitungen Fettstufe	\N
244	22	2	486	38	Käsezubereitungen Dreiviertelfettstufe	\N
245	22	2	491	39	Käsezubereitungen Halbfettstufe	\N
246	22	2	496	40	Käsezubereitungen Viertelfettstufe	\N
247	22	2	501	41	Käsezubereitungen Magerstufe	\N
248	22	2	506	42	Schmelzkäse und -zubereitungen Doppelrahmstufe	\N
249	22	2	514	43	Schmelzkäse und -zubereitungen Rahmstufe	\N
250	22	2	524	44	Schmelzkäse und -zubereitungen Vollfettstufe	\N
251	22	2	532	45	Schmelzkäse und -zubereitungen Fettstufe	\N
252	22	2	540	46	Schmelzkäse und -zubereitungen Dreiviertelfettstufe	\N
253	22	2	548	47	Schmelzkäse und -zubereitungen Halbfettstufe	\N
254	22	2	556	48	Schmelzkäse und -zubereitungen Viertelfettstufe	\N
255	22	2	564	49	Schmelzkäse und -zubereitungen Magerstufe	\N
256	22	2	572	50	Mischungen versch. Käsesorten auch mit Käsezubereitungen	\N
257	22	2	577	51	Kochkäse auch mit Gewürzen/Kräutern	\N
258	22	2	586	52	Käse und -zubereitungen aus Milch anderer Tiere	\N
259	22	2	594	53	Käse in Lake	\N
260	22	2	599	54	Käse eingelegt	\N
261	22	2	603	56	Zaziki und ähnliche Erzeugnisse aus Frischkäse	\N
262	22	2	605	57	Käseimitate	\N
263	22	2	607	58	Käse gerieben gewürfelt gestiftelt	\N
264	22	2	13537	60	Käse küchenm. vorbereitet auch tiefgefroren	\N
265	23	2	614	1	Sauerrahmbutter	\N
266	23	2	623	2	Süßrahmbutter	\N
267	23	2	632	3	Butter andere auch mild gesäuert	\N
268	23	2	648	5	Butter mit Zusätzen	\N
269	23	2	661	6	Butter aus Milch anderer Tiere	\N
270	24	2	663	1	Hühnereier	\N
271	24	2	694	2	Eiprodukte aus Hühnereiern	\N
272	24	2	711	3	Eier von anderen Geflügelarten und sonstigen Vögeln	\N
273	24	2	725	4	Eiprodukte von anderen Geflügelarten und sonstigen Vögeln	\N
274	24	2	741	5	Eizubereitungen aus Hühnereiern	\N
275	24	2	752	6	Eizubereitungen aus Eiern anderer Geflügelarten und sonstigen Vögeln	\N
276	25	2	766	1	Rind auch tiefgefroren	\N
277	25	2	771	2	Fleischteilstücke Rind auch tiefgefroren	\N
278	25	2	812	3	Innereien Rind auch tiefgefroren	\N
279	25	2	824	4	Fettgewebe Rind auch tiefgefroren	\N
280	25	2	828	5	Bindegewebe Rind auch tiefgefroren	\N
281	25	2	831	6	Knochen Rind auch tiefgefroren	\N
282	25	2	832	7	Blut Rind auch tiefgefroren	\N
283	25	2	837	8	Kalb auch tiefgefroren	\N
284	25	2	842	9	Fleischteilstücke Kalb auch tiefgefroren	\N
285	25	2	866	10	Innereien Kalb auch tiefgefroren	\N
286	25	2	879	11	Fettgewebe Kalb auch tiefgefroren	\N
287	25	2	883	12	Bindegewebe Kalb auch tiefgefroren	\N
288	25	2	886	13	Knochen Kalb auch tiefgefroren	\N
289	25	2	887	14	Blut Kalb auch tiefgefroren	\N
290	25	2	890	15	Schwein auch tiefgefroren	\N
291	25	2	894	16	Fleischteilstücke Schwein auch tiefgefroren	\N
292	25	2	925	17	Innereien Schwein auch tiefgefroren	\N
293	25	2	935	18	Fettgewebe Schwein auch tiefgefroren	\N
294	25	2	942	19	Bindegewebe Schwein auch tiefgefroren	\N
295	25	2	946	20	Knochen Schwein auch tiefgefroren	\N
296	25	2	947	21	Blut Schwein auch tiefgefroren	\N
297	25	2	953	22	Lamm/Schaf auch tiefgefroren	\N
298	25	2	956	23	Fleischteilstücke Lamm/Schaf auch tiefgefroren	\N
299	25	2	966	24	Innereien Lamm/Schaf auch tiefgefroren	\N
300	25	2	975	25	Fettgewebe und andere Gewebe Lamm/Schaf auch tiefgefroren	\N
301	25	2	980	26	Fohlen/Pferd auch tiefgefroren	\N
302	25	2	985	27	Fleischteilstücke Fohlen/Pferd auch tiefgefroren	\N
303	25	2	986	28	Innereien Fohlen/Pferd auch tiefgefroren	\N
304	25	2	990	29	Fettgewebe und andere Gewebe Fohlen/Pferd auch tiefgefroren	\N
305	25	2	995	30	Schlachtbare Haussäugetiere anderer Art auch tiefgefroren	\N
306	25	2	998	31	Fleisch gewürfeltes auch Mischungen auch tiefgefroren	\N
307	25	2	1010	32	Hackfleisch roh ohne Zusätze auch tiefgefroren	\N
308	25	2	1033	33	Hüllen natürliche für Fleischerzeugnisse auch gesalzen	\N
309	25	2	1046	34	Hauskaninchen auch tiefgefroren	\N
310	25	2	1056	35	Hühner auch tiefgefroren	\N
311	25	2	1079	36	Enten auch tiefgefroren	\N
312	25	2	1091	37	Gänse auch tiefgefroren	\N
313	25	2	1106	38	Puten auch tiefgefroren	\N
314	25	2	1124	39	Hausgeflügel anderer Art auch tiefgefroren	\N
315	25	2	1143	40	Haarwild auch tiefgefroren	\N
316	25	2	1192	41	Innereien vom Haarwild auch tiefgefroren	\N
317	25	2	1265	42	Federwild auch tiefgefroren	\N
318	25	2	1287	43	Innereien vom Federwild auch tiefgefroren	\N
319	25	2	1324	44	Fleisch mariner Säugetiere auch tiefgefroren	\N
320	25	2	1326	45	Fleisch dehydratisiert	\N
321	26	2	1353	1	Pökelwaren Rind roh ungeräuchert	\N
322	26	2	1358	2	Pökelwaren Rind roh geräuchert	\N
323	26	2	1361	3	Pökelwaren Rind gegart ungeräuchert	\N
324	26	2	1370	4	Pökelwaren Rind gegart geräuchert	\N
325	26	2	1373	5	Fleischkonserven Rind einschließlich Corned Beef	\N
326	26	2	1387	6	Fleischerzeugnisse Rind getrocknet u./o. gesalzen	\N
327	26	2	1391	7	Fleisch Rind gegart ausgenommen 070500	\N
328	26	2	1409	8	Pökelwaren Schwein roh ungeräuchert	\N
329	26	2	1419	9	Pökelwaren Schwein roh geräuchert	\N
330	26	2	1451	10	Pökelwaren Schwein gegart ungeräuchert	\N
331	26	2	1464	11	Pökelwaren Schwein gegart geräuchert	\N
332	26	2	1489	12	Fleischkonserven Schwein	\N
333	26	2	1502	13	Fleischerzeugnisse Schwein getrocknet u./o. gesalzen	\N
334	26	2	1503	14	Fleisch Schwein gegart ausgenommen 071200	\N
335	26	2	1533	15	Fleisch Kalb Konserven	\N
336	26	2	1545	16	Fleisch Kalb gegart ausgenommen 071500	\N
337	26	2	1572	17	Fleisch Lamm/Schaf gegart ausgenommen 076000	\N
338	26	2	1586	18	Hackfleischerzeugnisse roh auch Brühwursthalbfabrikate/Fleischzubereitungen auch tiefgefroren	\N
339	26	2	1647	19	Hackfleischerzeugnisse gegart	\N
340	26	2	1676	20	Pökelwaren Geflügel roh ungeräuchert	\N
341	26	2	1677	21	Pökelwaren Geflügel roh geräuchert	\N
342	26	2	1682	22	Pökelwaren Geflügel gegart ungeräuchert	\N
343	26	2	1686	23	Pökelwaren Geflügel gegart geräuchert	\N
344	26	2	1692	24	Fleischkonserven Geflügel	\N
345	26	2	1699	25	Fleischerzeugnisse Geflügel getrocknet u./o. gesalzen	\N
346	26	2	1700	26	Fleisch Geflügel gegart auch tiefgefroren ausgenommen 072400	\N
347	26	2	1721	27	Pökelwaren Wild roh ungeräuchert	\N
348	26	2	1722	28	Pökelwaren Wild roh geräuchert	\N
349	26	2	1729	29	Pökelwaren Wild gegart ungeräuchert	\N
350	26	2	1730	30	Pökelwaren Wild gegart geräuchert	\N
351	26	2	1736	31	Fleischkonserven Wild	\N
352	26	2	1750	32	Fleischerzeugnisse Wild getrocknet u./o. gesalzen	\N
353	26	2	1751	33	Fleischteilstücke Wild gegart ausgenommen 073100	\N
354	26	2	1776	34	Fleisch anderes roh ungeräuchert	\N
355	26	2	1777	35	Fleisch anderes roh geräuchert	\N
356	26	2	1778	36	Fleisch anderes gegart ungeräuchert	\N
357	26	2	1779	37	Fleisch anderes gepökelt gegart	\N
358	26	2	1780	38	Fleisch anderes gepökelt	\N
359	26	2	1781	39	Fleisch anderes Konserven	\N
360	26	2	1782	40	Fleisch anderes getrocknet u./o. gesalzen	\N
361	26	2	1783	41	Fleisch anderes gegart ausgenommen 073900	\N
362	26	2	1791	42	Separatorenfleisch Knochenputz	\N
363	26	2	1801	43	Fleischextrakte	\N
364	26	2	1802	45	Fleischstücke gefüllt gegart	\N
365	26	2	1803	46	Fleischstücke gefüllt Konserven	\N
366	26	2	1804	47	Fleischmischungen gegart	\N
367	26	2	1805	48	Fleischmischungen Konserven	\N
368	26	2	1816	50	Fleischerzeugnisse mariner Säugetiere	\N
369	26	2	1818	55	Fleischteilstücke Rind roh küchenm. vorber. auch tiefgefroren	\N
370	26	2	1822	56	Fleischteilstücke Kalb roh küchenm. vorbereitet auch tiefgefroren	\N
371	26	2	1826	57	Fleischteilstücke Schwein roh küchenm. vorbereitet auch tiefgefroren	\N
372	26	2	1834	58	Fleisch Geflügel roh küchenm. vorber. auch tiefgefroren	\N
373	26	2	1846	59	Fleischteilstücke Lamm/Schaf roh küchenm. vorbereitet auch tiefgefroren	\N
374	26	2	1848	60	Fleisch Lamm/Schaf Konserve	\N
375	26	2	1850	61	Fleisch Fohlen/Pferd Konserve	\N
376	26	2	1852	62	Fleischteilstücke Fohlen/Pferd roh küchenm. vorbereitet	\N
377	26	2	1855	65	Fleischteilstück Wild küchenmäßig vorbereitet auch tiefgefroren	\N
378	26	2	13235	66	Fleischteilstücke roh auch tiefgefroren verzehrsfertig zubereitet ausgenommen 071800	\N
379	26	2	13236	67	Fleischerzeugnisse Geflügel	\N
380	27	2	1859	1	Rohwürste schnittfest	\N
381	27	2	1924	2	Rohwürste schnittfest anderer Tierarten	\N
382	27	2	1933	3	Rohwürste streichfähig	\N
383	27	2	1954	4	Rohwürste streichfähig anderer Tierarten	\N
384	27	2	1963	5	Brühwürstchen umgerötet	\N
385	27	2	2001	6	Brühwürstchen umgerötet anderer Tierarten	\N
386	27	2	2018	7	Brühwürstchen nicht umgerötet	\N
387	27	2	2043	8	Brühwürstchen nicht umgerötet anderer Tierarten	\N
388	27	2	2046	9	Brühwürste umgerötet feingekuttert	\N
389	27	2	2066	10	Brühwürste umgerötet feingekuttert anderer Tierarten	\N
390	27	2	2081	11	Brühwürste umgerötet grob	\N
391	27	2	2126	12	Brühwürste umgerötet grob anderer Tierarten	\N
392	27	2	2137	13	Brühwürste umgerötet mit Fleischeinlagen	\N
393	27	2	2151	14	Brühwürste umgerötet mit Fleischeinlagen anderer Tierarten	\N
394	27	2	2154	15	Brühwürste nicht umgerötet feingekuttert	\N
395	27	2	2161	16	Brühwürste nicht umgerötet feingekuttert anderer Tierarten	\N
396	27	2	2166	17	Brühwürste nicht umgerötet grob	\N
397	27	2	2170	18	Brühwürste nicht umgerötet grob anderer Tierarten	\N
398	27	2	2171	19	Brühwürste nicht umgerötet mit Fleischeinlagen	\N
399	27	2	2174	20	Brühwürste nicht umgerötet mit Fleischeinlage anderer Tierarten	\N
400	27	2	2176	21	Brühwürste mit Einlagen anderer LM	\N
401	27	2	2195	22	Brühwürste anderer Tierarten mit Einlagen anderer LM	\N
402	27	2	2196	23	Brühwurstpasteten/-rouladen	\N
403	27	2	2206	24	Brühwurstpasteten/-rouladen m. Einlagen anderer LM	\N
404	27	2	2214	25	Brühwurstpasteten/-rouladen anderer Tierarten	\N
405	27	2	2218	26	Leberwürste fein gekörnt	\N
406	27	2	2242	27	Leberwürste grob gekörnt	\N
407	27	2	2288	28	Kochstreichwürste ohne Leber	\N
408	27	2	2310	29	Rotwürste/Blutwürste	\N
409	27	2	2353	30	Zungenwürste/Filetwürste	\N
410	27	2	2365	31	Sülzwürste	\N
411	27	2	2401	32	Sülzen und Aspikwaren	\N
412	27	2	2451	33	Kochwürste mit beigegebenen LM	\N
413	27	2	2486	34	Kochwurstpasteten -rouladen -cremes -pates Galantinen anderer Tierarten	\N
414	27	2	2502	35	Kochwurstpasteten -rouladen -cremes -pates Galantinen	\N
415	27	2	2511	36	Brühwürstchen umgerötet in Lake Konserven	\N
416	27	2	2548	37	Brühwürstchen nicht umgerötet in Lake Konserven	\N
417	27	2	2552	38	Brühwürstchen umgerötet anderer Tierarten in Lake Konserven	\N
418	27	2	2553	39	Brühwürstchen nicht umgerötet anderer Tierarten in Lake Konserven	\N
419	27	2	2554	40	Brühwürste umgerötet feingekuttert Konserven	\N
420	27	2	2572	41	Brühwürste umgerötet feingekuttert anderer Tierarten Konserven	\N
421	27	2	2573	42	Brühwürste umgerötet grob Konserven	\N
422	27	2	2609	43	Brühwürste umgerötet grob anderer Tierarten Konserven	\N
423	27	2	2610	44	Brühwürste umgerötet mit Fleischeinlagen Konserven	\N
424	27	2	2619	45	Brühwürste umgerötet mit Fleischeinlagen anderer Tierarten Konserven	\N
425	27	2	2620	46	Brühwürste nicht umgerötet feingekuttert Konserven	\N
426	27	2	2621	47	Brühwürste nicht umgerötet feingekuttert anderer Tierarten Konserven	\N
427	27	2	2622	48	Brühwürste nicht umgerötet grob Konserven	\N
428	27	2	2626	49	Brühwürste nicht umgerötet grob anderer Tierarten Konserven	\N
429	27	2	2627	50	Brühwürste nicht umgerötet mit Fleischeinlagen Konserven	\N
430	27	2	2628	51	Brühwürste nicht umgerötet mit Fleischeinlage anderer Tierarten Konserven	\N
431	27	2	2629	52	Brühwürste mit Einlagen anderer LM Konserven	\N
432	27	2	2630	53	Brühwürste anderer Tierarten mit Einlagen anderer LM Konserven	\N
433	27	2	2631	54	Brühwurstpasteten -rouladen Konserven	\N
434	27	2	2641	55	Brühwurstpasteten -rouladen m. Einlagen anderer LM Konserven	\N
435	27	2	2642	56	Brühwurstpasteten -rouladen anderer Tierarten Konserven	\N
436	27	2	2645	57	Leberwürste fein gekörnt Konserven	\N
437	27	2	2664	58	Leberwürste grob gekörnt Konserven	\N
438	27	2	2685	59	Kochstreichwürste ohne Leber Konserven	\N
439	27	2	2694	60	Rotwürste/Blutwürste Konserven	\N
440	27	2	2713	61	Zungenwürste/Filetwürste Konserven	\N
441	27	2	2718	62	Sülzwürste Konserven	\N
442	27	2	2731	63	Sülzen und Aspikwaren Konserven	\N
443	27	2	2760	64	Kochwürste mit beigegebenen LM Konserven	\N
444	27	2	2761	65	Kochwurstpasteten -rouladen -cremes -pates Galantinen anderer Tierarten Konserven	\N
445	27	2	2776	66	Kochwurstpasteten -rouladen -cremes -pates Galantinen Konserven	\N
446	27	2	2785	70	Rohwürste schnittfest fettreduziert	\N
447	27	2	2786	71	Rohwürste streichfähig fettreduziert	\N
448	27	2	2789	72	Brühwürstchen umgerötet fettreduziert	\N
449	27	2	2794	73	Brühwürste umgerötet feingekuttert fettreduziert	\N
450	27	2	2800	74	Brühwürste umgerötet grob fettreduziert	\N
451	27	2	2806	75	Brühwürste umgerötet mit Fleischeinlagen fettreduziert	\N
452	27	2	2808	76	Leberwürste fein- und grob gekörnt fettreduziert	\N
453	27	2	2811	77	Rotwürste/Blutwürste fettreduziert	\N
454	27	2	2812	78	Brühwürste umgerötet anderer Tiere fettreduziert	\N
455	27	2	2816	79	Brühwürstchen umgerötet fettreduziert Konserven	\N
456	27	2	2821	80	Brühwürste umgerötet feingekuttert fettreduziert Konserven	\N
457	27	2	2827	81	Brühwürste umgerötet grob fettreduziert Konserven	\N
458	27	2	2833	82	Brühwürste umgerötet mit Fleischeinlagen fettreduziert Konserven	\N
459	27	2	2835	83	Leberwürste fein- und grobgekörnt fettreduziert Konserven	\N
460	27	2	2839	85	Brühwürste umgerötet anderer Tiere fettreduziert Konserven	\N
461	27	2	13535	86	Brühwürste nicht umgerötet fettreduziert	\N
462	27	2	2843	90	Brotaufstriche nach Art einer Rohwurst mit pflanzl. Fetten/Ölen	\N
463	27	2	2844	91	Brotaufstriche nach Art einer Brühwurst mit pflanzl. Fetten/Ölen	\N
464	27	2	2845	92	Brotaufstriche nach Art einer Kochwurst mit pflanzl. Fetten/Ölen	\N
465	27	2	2846	99	Mischungen aus verschiedenen Wurstwaren	\N
466	28	2	2855	3	Neunaugen Seefische	\N
467	28	2	2857	4	Haie Seefische	\N
468	28	2	2864	5	Rochen Seefische	\N
469	28	2	2866	6	Heringsfische Seefische	\N
470	28	2	2874	7	Lachsähnliche Fische Seefische	\N
471	28	2	2881	8	Seeteufel Seefische	\N
472	28	2	2883	9	Aalartige Fische Seefische	\N
473	28	2	2885	10	Dorschfische Seefische	\N
474	28	2	2899	11	Hornhechte Seefische	\N
475	28	2	2901	12	Panzerwangenfische Seefische	\N
476	28	2	2905	13	Barschartige Fische Seefische	\N
477	28	2	2924	14	Plattfische Seefische	\N
478	28	2	13335	15	Schleimkopffische Seefische	\N
479	28	2	2939	16	Seefische andere	\N
480	28	2	2947	25	Neunaugen Süßwasserfische	\N
481	28	2	2950	26	Lachsähnliche Fische Süßwasserfische	\N
482	28	2	2963	27	Heringsfische Süßwasserfische	\N
483	28	2	2966	28	Hechtartige Fische Süßwasserfische	\N
484	28	2	2968	29	Karpfenfische Süßwasserfische	\N
485	28	2	2987	31	Aalartige Fische Süßwasserfische	\N
486	28	2	2989	32	Barschartige Fische Süßwasserfische	\N
487	28	2	2996	33	Dorschartige Fische Süßwasserfische	\N
488	28	2	2998	34	Störartige Fische Süßwasserfische	\N
489	28	2	3000	45	Neunaugen Seefische Zuschnitte	\N
490	28	2	3002	46	Haie Seefische Zuschnitte	\N
491	28	2	3021	47	Rochen Seefische Zuschnitte	\N
492	28	2	3024	48	Heringsfische Seefische Zuschnitte	\N
493	28	2	3036	49	Lachsähnliche Fische Seefische Zuschnitte	\N
494	28	2	3048	50	Seeteufel Seefische Zuschnitte	\N
495	28	2	3052	51	Aalartige Fische Seefische Zuschnitte	\N
496	28	2	3054	52	Dorschfische Seefische Zuschnitte	\N
497	28	2	3106	53	Hornhechte Seefische Zuschnitte	\N
498	28	2	3109	54	Panzerwangenfische Seefische Zuschnitte	\N
499	28	2	3116	55	Barschartige Fische Seefische Zuschnitte	\N
500	28	2	3155	56	Plattfische Seefische Zuschnitte	\N
501	28	2	3174	57	Seefische andere Zuschnitte	\N
502	28	2	3188	60	Lachsähnliche Fische Süßwasserfische Zuschnitte	\N
503	28	2	3206	61	Hechtartige Fische Süßwasserfische Zuschnitte	\N
504	28	2	3211	62	Karpfenfische Süßwasserfische Zuschnitte	\N
505	28	2	3221	64	Aalartige Fische Süßwasserfische Zuschnitte	\N
506	28	2	3223	65	Barschartige Fische Süßwasserfische Zuschnitte	\N
507	28	2	3232	66	Störartige Fische Süßwasserfische Zuschnitte	\N
508	28	2	3237	90	Innereien Seefische Rogen	\N
509	28	2	3249	91	Innereien Seefische Leber	\N
510	28	2	3269	94	Innereien Süßwasserfische Rogen	\N
511	28	2	3278	95	Innereien Süßwasserfische Leber	\N
512	28	2	3284	99	Mischungen aus Fischteilen	\N
513	29	2	3310	1	Fische getrocknet	\N
514	29	2	3313	2	Fische geräuchert	\N
515	29	2	3364	3	Fische gesalzen	\N
516	29	2	3382	4	Fischerzeugnisse aus gesalzenen Fischen	\N
517	29	2	3412	5	Anchosen ausgenommen 111000 und 111100	\N
518	29	2	3427	6	Marinaden ausgenommen 111000 und 111100	\N
519	29	2	3445	7	Bratfischwaren ausgenommen 111000 und 111100	\N
520	29	2	3457	8	Kochfischwaren ausgenommen 111000 und 111100	\N
521	29	2	3466	9	Fischerzeugnisse in Gelee ausgenommen 110800 111000 und 111100	\N
522	29	2	3475	10	Fischerzeugnisse pasteurisiert Präserven	\N
523	29	2	3560	11	Fischdauerkonserven	\N
524	29	2	3643	12	Fische küchenmäßig vorbereitet auch tiefgefroren	\N
525	29	2	3715	15	Fischdauerkonserven brennwertreduziert	\N
526	29	2	13237	20	Fisch roh auch tiefgefroren verzehrsfertig zubereitet	\N
527	29	2	13238	30	Fisch gegart verzehrsfertig zubereitet	\N
528	30	2	3721	1	Krebstiere	\N
529	30	2	3749	2	Krebstiererzeugnisse	\N
530	30	2	3779	3	Muscheltiere	\N
531	30	2	3788	4	Muscheltiererzeugnisse	\N
532	30	2	3795	5	Tintenfische	\N
533	30	2	3799	6	Tintenfischerzeugnisse	\N
534	30	2	3806	9	Schnecken	\N
535	30	2	3812	10	Schneckenerzeugnisse auch mit Kräuterbutter	\N
536	30	2	3816	11	Frösche	\N
537	30	2	3818	12	Froscherzeugnisse	\N
538	30	2	3831	15	Reptilien	\N
539	30	2	3834	16	Insekten	\N
540	30	2	3839	20	Krebstiererzeugnisse pasteurisiert Präserven	\N
541	30	2	3858	21	Krebstiererzeugnisse Konserven	\N
542	30	2	3873	22	Muscheltiererzeugnisse pasteurisiert Präserven	\N
543	30	2	3880	23	Muscheltiererzeugnisse Konserven	\N
544	30	2	3886	24	Tintenfischerzeugnisse pasteurisiert Präserven	\N
545	30	2	3889	25	Tintenfischerzeugnisse Konserven	\N
546	30	2	3892	26	Schneckenerzeugnisse pasteurisiert Präserven	\N
547	30	2	3893	27	Schneckenerzeugnisse Konserven	\N
548	30	2	3896	28	Froscherzeugnisse pasteurisiert Präserven	\N
549	30	2	3903	29	Froscherzeugnisse Konserven	\N
550	30	2	3909	30	Krebs- Krustentierfleischimitate	\N
551	30	2	3911	31	Meeresfrüchte Präserven	\N
552	30	2	3912	32	Meeresfrüchte Konserven	\N
553	30	2	13239	33	Meeresfrüchte frisch lose Ware	\N
554	31	2	3914	1	Tierische Fette	\N
555	31	2	3929	2	Tierische Öle	\N
556	31	2	3935	3	Pflanzliche Fette	\N
557	31	2	3943	4	Pflanzliche Öle	\N
558	31	2	3992	5	Margarine	\N
559	31	2	4007	6	Halbfettmargarine	\N
560	31	2	4014	7	Fettmischungen Fettzubereitungen	\N
561	31	2	4040	8	Fettmischungen mit Milchfett	\N
562	31	2	4045	9	Speiseöl mit Gewürzen	\N
563	31	2	13395	10	Speiseöl mit Aromen	\N
564	32	2	4047	1	Brüherzeugnisse Fleischbrüherzeugnisse	\N
565	32	2	4053	2	Suppen trockene nicht süße auch mit Fleischeinlage	\N
566	32	2	4100	3	Suppenpasten nicht süße	\N
567	32	2	4101	4	Suppen flüssige nicht süße auch mit Fleischeinlage	\N
568	32	2	4154	5	Suppen trockene süße	\N
569	32	2	4159	6	Suppenpasten süße	\N
570	32	2	4160	7	Suppen flüssige süße	\N
571	32	2	4165	8	Soßenpulver trockene nicht süße	\N
572	32	2	4188	9	Soßenpasten nicht süße	\N
573	32	2	4189	10	Soßen flüssige nicht süße	\N
574	33	2	4222	1	Weizen	\N
575	33	2	4229	2	Roggen	\N
576	33	2	4231	3	Gerste	\N
577	33	2	4233	4	Hafer	\N
578	33	2	4235	5	Mais	\N
579	33	2	4237	6	Reis	\N
580	33	2	4247	7	Buchweizen	\N
581	33	2	4249	8	Getreidearten andere	\N
582	33	2	4251	9	Getreidemischungen	\N
583	33	2	4257	10	Triticale	\N
584	34	2	4260	1	Getreidemehle und Mehle aus anderen Pflanzenkörnern	\N
585	34	2	4297	2	Getreidegrieße	\N
586	34	2	4306	3	Getreideschrote	\N
587	34	2	4321	4	Getreidestärken	\N
588	34	2	4330	5	Getreideeiweiße	\N
589	34	2	4338	6	Getreidegrits und Frühstückscerealien	\N
590	34	2	4351	8	Getreidekleie	\N
591	34	2	4357	9	Getreideflocken und Grütze	\N
592	34	2	4377	10	Gepuffte Getreideprodukte	\N
593	34	2	4381	11	Getreideerzeugnisse mit Zusätzen	\N
594	34	2	4402	12	Brotvormischungen	\N
595	34	2	4416	13	Grundmischungen für Backwaren	\N
596	34	2	4421	14	Brotteige auch vorgebackene	\N
597	34	2	4430	15	Feinteige für feine Backwaren	\N
598	34	2	4438	16	Massen für feine Backwaren	\N
599	34	2	4449	25	Backmalz	\N
600	34	2	4454	26	Backmischungen für Backwaren	\N
601	35	2	4460	1	Weizenbrote ausgenommen 170400	\N
602	35	2	4467	2	Roggenbrote ausgenommen 170400	\N
603	35	2	4472	3	Mischbrote ausgenommen 170400	\N
604	35	2	4483	4	Toastbrote	\N
605	35	2	4503	5	Spezialbrote aus besonderen Mahlerzeugnissen	\N
606	35	2	4515	6	Spezialbrote mit besonderen Zusätzen	\N
607	35	2	4552	7	Spezialbrote mit besonderen Backverfahren	\N
608	35	2	4563	8	Spezialbrote mit besonderen Backverfahren und bes. Zusätzen	\N
609	35	2	4580	9	Spezialbrote im Nährwert verändert	\N
610	35	2	4584	10	Spezialbrote im Nährwert verändert mit besonderen Zusätzen	\N
611	35	2	4585	11	Weizenkleingebäcke Wasserwaren	\N
612	35	2	4597	12	Weizenkleingebäcke Milchwaren	\N
613	35	2	4604	13	Weizenkleingebäcke mit Fettzusätzen	\N
614	35	2	4608	14	Weizenkleingebäcke mit besonderen Zusätzen	\N
615	35	2	4627	15	Roggenkleingebäcke Wasserwaren	\N
616	35	2	4634	16	Roggenkleingebäcke mit Fettzusätzen	\N
617	35	2	4635	17	Roggenkleingebäcke mit besonderen Zusätzen	\N
618	35	2	4644	18	Kleingebäcke aus Mehlmischungen Wasserwaren	\N
619	35	2	4653	19	Kleingebäcke aus Mehlmischungen mit bes. Zusätzen	\N
620	35	2	4660	20	Broterzeugnisse	\N
621	36	2	4669	1	Feine Backwaren aus Baumkuchenmasse	\N
622	36	2	4673	2	Feine Backwaren aus Biskuitmasse	\N
623	36	2	4684	3	Feine Backwaren aus Wiener Masse	\N
624	36	2	4688	4	Feine Backwaren aus Rührmasse	\N
625	36	2	4701	5	Feine Backwaren aus Sandmasse	\N
626	36	2	4714	6	Feine Backwaren aus Eiweiß und Schaummasse	\N
627	36	2	4722	7	Feine Backwaren aus Ölsamenmasse	\N
628	36	2	4731	8	Feine Backwaren aus Waffelmasse	\N
629	36	2	4739	9	Feine Backwaren aus Brandmasse	\N
630	36	2	4745	10	Feine Backwaren aus Mürbeteig	\N
631	36	2	4764	11	Feine Backwaren aus Blätterteig	\N
632	36	2	4774	12	Feine Backwaren aus leichtem Feinteig mit Hefe	\N
633	36	2	4791	13	Feine Backwaren aus zutatenreichem Feinteig mit Hefe	\N
634	36	2	4817	14	Zwieback	\N
635	36	2	4831	15	Knabbererzeugnisse (Extruderprodukte) aus Getreide	\N
636	36	2	4838	17	Kräcker	\N
637	36	2	4845	18	Laugendauergebäcke	\N
638	36	2	4848	19	Lebkuchen lebkuchenhaltige Gebäcke	\N
639	36	2	4878	20	Mischungen verschiedener feiner Backwaren	\N
640	36	2	4881	21	Feine Backwaren aus Teigen und Massen	\N
641	36	2	4904	22	Backoblaten	\N
642	36	2	13497	23	Feine Backware aus Quark-Ölteig	\N
643	37	2	4907	1	Mayonnaisen und -erzeugnisse	\N
644	37	2	4915	2	Emulgierte Soßen	\N
645	37	2	4919	3	Fertigsoßen kalte	\N
646	37	2	4920	4	Salate fleischhaltige	\N
647	37	2	4934	5	Salate fischhaltige u./o. mit anderen Meeresfrüchten	\N
648	37	2	4947	6	Salate obsthaltige	\N
649	37	2	4951	7	Salate gemüsehaltige	\N
650	37	2	4990	8	Salate kartoffelhaltige	\N
651	37	2	4998	9	Salate eihaltige	\N
652	37	2	5001	10	Salate käsehaltige	\N
653	37	2	5003	11	Salate andere	\N
654	37	2	5008	15	Salate fleischhaltige Präserven	\N
655	37	2	5021	16	Salate fischhaltige u./o. mit anderen Meeresfrüchten Präserven	\N
656	37	2	5033	17	Salate gemüsehaltige Präserven	\N
657	37	2	5061	18	Salate kartoffelhaltige Präserven	\N
658	37	2	5066	19	Salate eihaltige Präserven	\N
659	37	2	5068	20	Salate käsehaltige Präserven	\N
660	37	2	5070	21	Salate andere Präserven	\N
661	37	2	5074	25	Salate fleischhaltige Konserven	\N
662	37	2	5078	26	Salate fischh. u./o. mit and. Meeresfrüchten Konserven	\N
663	37	2	5082	27	Salate gemüsehaltige Konserven	\N
664	37	2	5095	28	Salate kartoffelhaltige Konserven	\N
665	37	2	5097	29	Salate eihaltige Konserven	\N
666	37	2	5098	35	Salate fleischhaltige brennwertreduziert	\N
667	37	2	5110	36	Salate fischhaltige u./o. mit anderen Meeresfrüchten brennwertreduziert	\N
668	37	2	5115	37	Salate obsthaltige brennwertreduziert	\N
669	37	2	5116	38	Salate gemüsehaltige brennwertreduziert	\N
670	37	2	5117	39	Salate kartoffelhaltige brennwertreduziert	\N
671	37	2	5118	40	Salate eihaltige brennwertreduziert	\N
672	37	2	5119	41	Salate käsehaltige brennwertreduziert	\N
673	37	2	5120	42	Salate andere brennwertreduziert	\N
674	37	2	5124	43	Salate fleischhaltige brennwertreduziert Präserven	\N
675	37	2	5136	44	Salate fischhaltige u./o. mit anderen Meeresfrüchten brennwertreduziert Präserven	\N
676	37	2	5141	45	Salate obsthaltige brennwertreduziert Präserven	\N
677	37	2	5142	46	Salate gemüsehaltige brennwertreduziert Präserven	\N
1050	56	2	8073	1	Blütenhonige	\N
678	37	2	5143	47	Salate kartoffelhaltige brennwertreduziert Präserven	\N
679	37	2	5144	48	Salate eihaltige brennwertreduziert Präserven	\N
680	37	2	5145	49	Salate käsehaltige brennwertreduziert Präserven	\N
681	37	2	5146	50	Salate andere brennwertreduziert Präserven	\N
682	38	2	5151	1	Puddinge mit und ohne Belag oder Soße	\N
683	38	2	5168	2	Puddingpulver auch mit Eipulver	\N
684	38	2	5186	3	Kremspeisen Desserts	\N
685	38	2	5200	4	Kremspeisenpulver Dessertpulver auch mit Eipulver	\N
686	38	2	5214	5	Geleespeisen	\N
687	38	2	5218	6	Geleespeisenpulver	\N
688	38	2	5223	8	Milchreis gekocht aromatisiert	\N
689	38	2	5227	9	Grießbrei Plumpudding	\N
690	38	2	5230	10	Dessertpulver	\N
691	38	2	5232	15	Soßenpulver süße	\N
692	38	2	5238	16	Soßenpasten süße	\N
693	38	2	5239	17	Soßen flüssige süße	\N
694	39	2	5259	1	Teigwaren eifreie	\N
695	39	2	5260	2	Teigwaren mit normalem Eigehalt	\N
696	39	2	5261	3	Teigwaren mit hohem Eigehalt	\N
697	39	2	5262	4	Teigwaren mit besonders hohem Eigehalt	\N
698	39	2	5263	5	Teigwaren besonderer Art	\N
699	39	2	5284	6	Nudelteige	\N
700	39	2	5290	7	Teigwaren besonderer Herstellung	\N
701	39	2	5296	8	Suppeneinlagen	\N
702	39	2	5300	10	Vormischungen für Teigwaren	\N
703	40	2	5304	1	Hülsenfrüchte	\N
704	40	2	5329	2	Sojaerzeugnisse ausgenommen 130415 und 520103	\N
705	40	2	5346	3	Konserven aus vorbereiteten Hülsenfrüchten	\N
706	40	2	5354	4	Ölsamen	\N
707	40	2	5367	5	Schalenobst	\N
708	40	2	5389	6	Edelkastanien	\N
709	40	2	5393	7	Schalenobst und Mischungen auch mit anderen LM	\N
710	40	2	5407	8	Erzeugnisse aus Ölsamen Schalenobst	\N
711	40	2	5436	9	Erzeugnisse aus Hülsenfrüchten	\N
712	41	2	5450	1	Kartoffeln	\N
713	41	2	5455	2	Kartoffelnassprodukte auch Konserven	\N
714	41	2	5461	3	Kartoffeln gegarte auch tiefgefroren	\N
715	41	2	5478	4	Kartoffelvorprodukte auch tiefgefroren	\N
716	41	2	5493	5	Kartoffeltrockenprodukte	\N
717	41	2	5502	6	Kartoffelteige	\N
718	41	2	5509	7	Kartoffelknabbererzeugnisse	\N
719	41	2	5515	8	Kartoffelstärke und Nebenprodukte	\N
720	41	2	5520	9	Stärkereiche Pflanzenteile	\N
721	41	2	5528	10	Erzeugnisse stärkereicher Pflanzenteile	\N
722	41	2	5533	11	Kartoffelsäfte	\N
723	42	2	5535	1	Blattgemüse	\N
724	42	2	5578	2	Sprossgemüse	\N
725	42	2	5603	3	Fruchtgemüse	\N
726	42	2	5626	4	Wurzelgemüse	\N
727	42	2	5641	5	Frischgemüsemischungen	\N
728	42	2	5645	6	Algen	\N
729	42	2	13475	7	Exotische Gemüse	\N
730	43	2	5647	1	Blattgemüse Konserven	\N
731	43	2	5659	2	Blattgemüse tiefgefrorene	\N
732	43	2	5673	3	Blattgemüse getrocknete	\N
733	43	2	5690	4	Blattgemüse gesäuerte u./o. Sauergemüse Konserven	\N
734	43	2	5695	5	Blattgemüse vor- und zubereitete	\N
735	43	2	5712	6	Sprossgemüse Konserven	\N
736	43	2	5729	7	Sprossgemüse tiefgefrorene	\N
737	43	2	5740	8	Sprossgemüse getrocknete	\N
738	43	2	5746	9	Sprossgemüse gesäuerte u./o. Sauergemüse Konserven	\N
739	43	2	5750	10	Sprossgemüse vor- und zubereitete	\N
740	43	2	5761	11	Fruchtgemüse Konserven	\N
741	43	2	5783	12	Fruchtgemüse tiefgefrorene	\N
742	43	2	5793	13	Fruchtgemüse getrocknete ausgenommen 230100	\N
743	43	2	5795	14	Fruchtgemüse gesäuerte u./o. Sauergemüse Konserven	\N
744	43	2	5806	15	Fruchtgemüse vor- und zubereitete	\N
745	43	2	5821	16	Wurzelgemüse Konserven	\N
746	43	2	5831	17	Wurzelgemüse tiefgefrorene	\N
747	43	2	5839	18	Wurzelgemüse getrocknete	\N
748	43	2	5846	19	Wurzelgemüse gesäuerte u./o. Sauergemüse Konserven	\N
749	43	2	5852	20	Wurzelgemüse vor- und zubereitete	\N
750	43	2	5860	21	Gemüsemischungen Konserven	\N
751	43	2	5866	22	Gemüsemischungen tiefgefrorene	\N
752	43	2	5874	23	Gemüsemischung getrocknet	\N
753	43	2	5875	24	Gemüsemischungen gesäuerte u./o. Sauergemüse Konserven	\N
754	43	2	5879	25	Gemüsemischungen vor- und zubereitete	\N
755	43	2	5884	26	Gemüsesäfte	\N
756	43	2	5894	27	Gemüsetrunke	\N
757	43	2	5904	28	Gemüsepulver	\N
758	43	2	5910	29	Gemüsesaftkonzentrate	\N
759	43	2	5911	30	Algen getrocknet	\N
760	43	2	13495	31	Algen vor- und zubereitet	\N
761	43	2	5912	35	Gemüsemark auch konzentriertes	\N
762	44	2	5914	1	Kulturpilze	\N
763	44	2	5921	2	Wild-Blätterpilze	\N
764	44	2	5973	3	Wild-Röhrenpilze	\N
765	44	2	5990	4	Wild-Stachelpilze	\N
766	44	2	5994	5	Wild-Leisten- und Trompetenpilze	\N
767	44	2	5998	6	Keulen- und Korallenpilze	\N
768	44	2	6004	7	Wild-Bauchpilze	\N
769	44	2	6007	8	Wild-Schlauchpilze	\N
770	44	2	6020	9	Wildpilzmischungen	\N
771	44	2	6021	10	Pilze bedingt verwendbar	\N
772	44	2	6025	11	Porlinge und andere Nichtblätterpilze	\N
773	45	2	6039	1	Kulturpilze Konserven	\N
774	45	2	6046	2	Kulturpilze tiefgefrorene	\N
775	45	2	6048	3	Kulturpilze getrocknete pulverisierte	\N
776	45	2	6053	4	Kulturpilze in Essig	\N
777	45	2	6055	5	Kulturpilze vor- und zubereitete	\N
778	45	2	6058	6	Wildpilze Konserven	\N
779	45	2	6073	7	Wildpilze tiefgefrorene	\N
780	45	2	6081	8	Wildpilze getrocknete pulverisierte	\N
781	45	2	6120	9	Wildpilze in Essig	\N
782	45	2	6126	10	Wildpilze vor- und zubereitete	\N
783	45	2	6132	11	Pilzextrakte und -konzentrate	\N
784	45	2	6136	12	Wildpilze in Salzlake	\N
785	45	2	6137	13	Kulturpilze in Salzlake	\N
786	45	2	6138	14	Kultur- u. Wildpilzmischungen	\N
787	45	2	6139	15	Kultur- u. Wildpilzmischungen Konserven	\N
788	45	2	6140	16	Kultur- u. Wildpilzmischungen getrocknet	\N
789	45	2	6141	17	Kultur- u. Wildpilzmischungen in Essig	\N
790	45	2	6142	18	Kultur- u. Wildpilzmischungen vor- und zubereitet	\N
791	45	2	13496	19	Kultur- u. Wildpilzmischungen tiefgefroren	\N
792	46	2	6144	1	Beerenobst	\N
793	46	2	6166	2	Kernobst	\N
794	46	2	6171	3	Steinobst	\N
795	46	2	6182	4	Zitrusfrüchte	\N
796	46	2	6198	5	Früchte Pflanzenteile exotisch und Rhabarber	\N
797	46	2	6240	6	Frischobstmischungen	\N
798	47	2	6242	1	Beerenobst Konserven	\N
799	47	2	6256	2	Beerenobst tiefgefroren	\N
800	47	2	6266	3	Beerenobst getrocknet	\N
801	47	2	6279	4	Beerenobst vor- und zubereitet	\N
802	47	2	6282	5	Beerenobst in Alkohol	\N
803	47	2	6286	6	Beerenobst in Essig	\N
804	47	2	6287	7	Beerenobst in Sirup	\N
805	47	2	6289	8	Kernobst Konserven	\N
806	47	2	6295	9	Kernobst tiefgefroren	\N
807	47	2	6299	10	Kernobst getrocknet	\N
808	47	2	6303	11	Kernobst vor- und zubereitet	\N
809	47	2	6307	12	Kernobst in Alkohol	\N
810	47	2	6311	13	Kernobst in Essig	\N
811	47	2	6315	14	Kernobst in Sirup	\N
812	47	2	6320	15	Steinobst Konserven	\N
813	47	2	6331	16	Steinobst tiefgefroren	\N
814	47	2	6338	17	Steinobst getrocknet	\N
815	47	2	6342	18	Steinobst vor- und zubereitet	\N
816	47	2	6345	19	Steinobst in Alkohol	\N
817	47	2	6350	20	Steinobst in Essig	\N
818	47	2	6351	21	Steinobst in Sirup	\N
819	47	2	6354	22	Zitrusfrüchte Konserven	\N
820	47	2	6359	23	Zitrusfrüchte vor- und zubereitet	\N
821	47	2	6360	24	Zitrusfrüchte getrocknet	\N
822	47	2	6365	25	Zitrusfrüchte in Alkohol	\N
823	47	2	6366	26	Zitrusfrüchte in Essig	\N
824	47	2	6367	27	Zitrusfrüchte in Sirup	\N
825	47	2	6368	28	Früchte Pflanzenteile exotisch und Rhabarber Konserven	\N
826	47	2	6383	29	Früchte Pflanzenteile exotisch und Rhabarber tiefgefroren	\N
827	47	2	6384	30	Früchte Pflanzenteile exotisch und Rhabarber getrocknet	\N
828	47	2	6392	31	Früchte Pflanzenteile exotisch vor- und zubereitet	\N
829	47	2	6393	32	Früchte Pflanzenteile exotisch in Alkohol	\N
830	47	2	6397	33	Früchte Pflanzenteile exotisch in Essig	\N
831	47	2	6398	34	Früchte Pflanzenteile exotisch in Sirup	\N
832	47	2	6401	35	Obstmischungen Konserven	\N
833	47	2	6404	36	Obstmischungen tiefgefrorene	\N
834	47	2	6405	37	Obstmischungen getrocknete	\N
835	47	2	6407	38	Obstmischungen vor- und zubereitet	\N
836	47	2	6408	39	Obstmischungen in Alkohol	\N
837	47	2	6410	40	Obstmischungen in Essig	\N
838	47	2	6411	41	Obstmischungen in Sirup	\N
839	47	2	6412	42	Fruchtmark auch konzentriert	\N
840	47	2	6430	43	Pulpen	\N
841	48	2	6442	1	Beerenfruchtsäfte	\N
842	48	2	6455	2	Beerenfruchtnektare	\N
843	48	2	6470	3	Beerenfruchtsaftkonzentrate	\N
844	48	2	6485	4	Beerenfruchtsirupe	\N
845	48	2	6497	5	Beerenfruchtsäfte getrocknet	\N
846	48	2	6507	6	Kernfruchtsäfte	\N
847	48	2	6511	7	Kernfruchtnektare	\N
848	48	2	6515	8	Kernfruchtsaftkonzentrate	\N
849	48	2	6519	9	Kernfruchtsirupe	\N
850	48	2	6520	10	Kernfruchtsäfte getrocknet	\N
851	48	2	6524	11	Steinfruchtsäfte	\N
852	48	2	6532	12	Steinfruchtnektare	\N
853	48	2	6539	13	Steinfruchtsaftkonzentrate	\N
854	48	2	6546	14	Steinfruchtsirupe	\N
855	48	2	6550	15	Steinfruchtsäfte getrocknet	\N
856	48	2	6555	16	Zitrusfruchtsäfte	\N
857	48	2	6562	17	Zitrusfruchtnektare	\N
858	48	2	6568	18	Zitrusfruchtsaftkonzentrate	\N
859	48	2	6575	19	Zitrusfruchtsirupe	\N
860	48	2	6581	20	Zitrusfruchtsäfte getrocknet	\N
861	48	2	6586	21	Fruchtsäfte aus exotischen Früchten und Rhabarbersaft	\N
862	48	2	6595	22	Fruchtnektare aus exotischen Früchten und Rhabarbertrunk	\N
863	48	2	6614	23	Fruchtsaftkonzentrate aus exotischen Früchten und Rhabarbersaftkonzentrat	\N
864	48	2	6625	24	Fruchtsirupe aus exotischen Früchten	\N
865	48	2	6629	25	Mehrfruchtsäfte	\N
866	48	2	6631	26	Mehrfruchtnektare	\N
867	48	2	6633	27	Mehrfruchtsirupe	\N
868	48	2	6636	29	Mehrfruchtsaftkonzentrate	\N
869	48	2	6639	31	Fruchtsäfte getrocknet aus exotischen Früchten und Rhabarber	\N
870	48	2	6653	33	Beerenfruchtsafthalbware	\N
871	48	2	6654	34	Steinfruchtsafthalbware	\N
872	48	2	6655	35	Citruspulpwash	\N
873	48	2	6656	36	Fruchtsafthalbware aus exotischen Früchten und Rhabarber	\N
874	48	2	6657	37	Fruchtsäfte mit Vitaminzusätzen	\N
875	48	2	6658	38	Fruchtnektare mit Vitaminzusätzen	\N
876	48	2	13241	39	Fruchtnektare mit Süßungsmitteln	\N
877	49	2	6660	1	Fruchtsaftgetränke aus Beerenobst	\N
878	49	2	6666	2	Fruchtsaftgetränke aus Kernobst	\N
879	49	2	6669	3	Fruchtsaftgetränke aus Steinobst	\N
880	49	2	6674	4	Fruchtsaftgetränke aus Zitrusfrüchten	\N
881	49	2	6681	5	Fruchtsaftgetränke aus exotischen Früchten und Pflanzenteilen	\N
882	49	2	6685	6	Fruchtsaftgetränke aus mehreren Früchten und Pflanzenteilen	\N
883	49	2	6686	7	Limonaden mit Fruchtsäften	\N
884	49	2	6696	8	Limonaden mit Fruchtgeschmack	\N
885	49	2	6702	9	Colalimonaden	\N
1458	81	2	10954	65	Endokrine Drüsen	\N
886	49	2	6707	10	Limonaden chininhaltig u./o. mit anderen Bitterstoffen	\N
887	49	2	6712	11	Brausen künstliche Kaltgetränke	\N
888	49	2	6719	12	Brausen künstl. Kaltget. chininhaltig u./o. mit a. Bitterstoffen	\N
889	49	2	6720	13	Brausen künstl. Kaltget. coffeinhaltige	\N
890	49	2	6723	14	Erfrischungsgetränke fermentiert	\N
891	49	2	6726	15	Ansätze und Grundstoffe für Fruchtsaft-/fruchthaltige Getränke	\N
892	49	2	6732	16	Ansätze und Grundstoffe für Limonaden	\N
893	49	2	6733	17	Ansätze und Grundstoffe für Brausen künstl. Kaltgetr.	\N
894	49	2	6734	18	Ansätze und Grundstoffe für coffeinhaltige Getränke	\N
895	49	2	6735	19	Ansätze u. Grundstoffe f. chininhaltige u./o. andere bitterstoffh. Getränke	\N
896	49	2	6736	20	Getränkepulver für Fruchtsaftgetränke	\N
897	49	2	6742	21	Getränkepulver für Limonaden auch mit Fruchtsaftanteilen	\N
898	49	2	6751	22	Getränkepulver für Brausen künstliche Kaltgetränke	\N
899	49	2	6756	23	Getränkepulver für sonst. Getränke ausgenommen kakaohaltige Getränke	\N
900	49	2	6762	24	Getränke aus Trockenobst	\N
901	49	2	6764	25	Getränke mit Essig und/oder anderen Zutaten	\N
902	49	2	6773	26	Fruchtsaftgetränke aus Beerenobst brennwertreduziert	\N
903	49	2	6774	27	Fruchtsaftgetränke aus Kernobst brennwertreduziert	\N
904	49	2	6775	28	Fruchtsaftgetränke aus Steinobst brennwertreduziert	\N
905	49	2	6776	29	Fruchtsaftgetränke aus Zitrusfrüchten brennwertreduziert	\N
906	49	2	6777	30	Fruchtsaftgetränke aus exotischen Früchten brennwertreduziert	\N
907	49	2	6778	31	Fruchtsaftgetränke aus mehreren Früchten brennwertreduziert	\N
908	49	2	6779	32	Limonaden mit Fruchtsäften brennwertreduziert	\N
909	49	2	6780	33	Limonaden mit Fruchtgeschmack brennwertreduziert	\N
910	49	2	6781	34	Colalimonaden brennwertreduziert	\N
911	49	2	6786	35	Limonaden chininhaltig u./o. mit a. Bitterstoffen brennwertreduziert	\N
912	49	2	6790	36	Brausen künstliche Kaltgetränke brennwertreduziert	\N
913	49	2	6791	37	Brausen künstl. Kaltget. chininh. u./o. m. a. Bitterstoffen brennwertreduziert	\N
914	49	2	6792	38	Brausen künstl. Kaltget. coffeinhaltige brennwertreduziert	\N
915	49	2	6793	39	Mineralwasser plus Frucht	\N
916	49	2	6796	40	Energy-/Fitnessgetränke	\N
917	49	2	13243	41	Fruchtsaftschorlen	\N
918	49	2	13396	42	Getränke aus/mit Aloe Vera	\N
919	49	2	13397	43	Getränke aus/mit Nonisaft	\N
920	49	2	13543	44	Erfrischungsgetränke mit alkoholfreiem Bier	\N
921	49	2	13544	45	Erfrischungsgetränke mit alkoholfreiem Wein	\N
922	50	2	6798	1	Weine unbekannter Qualität nicht weiter differenzierbar	\N
923	50	2	6802	2	Weine einfacher Qualität (auch Tafelweine) ohne geogr. Herkunft	\N
924	50	2	6830	3	Weine einfacher Qualität (auch Tafelweine) mit geogr. Herkunft	\N
925	50	2	6859	4	Landweine	\N
926	50	2	6888	8	gestrichen jetzt 331230; QUALITAETSW. KABINETT, R/ROSE	\N
927	50	2	6889	10	Weine gehobener Qualität nicht weiter differenzierbar auch Qualitätsweine b. A.	\N
928	50	2	6918	11	Qualitätsweine garantierten Ursprungs	\N
929	50	2	6928	12	Qualitätsweine mit Prädikat Kabinett	\N
930	50	2	6938	13	Qualitätsweine mit Prädikat Spätlese	\N
931	50	2	6948	14	Qualitätsweine mit Prädikat Auslese	\N
932	50	2	6958	15	Qualitätsweine mit Prädikat Beerenauslese	\N
933	50	2	6965	16	Qualitätsweine mit Prädikat Trockenbeerenauslese	\N
934	50	2	6972	17	Qualitätsweine mit Prädikat Eiswein	\N
935	50	2	6979	18	Weine gehobener Qualität mit prädikatsähnlichen Angaben	\N
936	50	2	7007	25	gestrichen, JUNGW., TRAUBENM. TEILW. GEGOR.	\N
937	50	2	7009	30	Perlweine nicht weiter differenzierbar	\N
938	50	2	7013	31	Perlweine mit zugesetzter Kohlensäure	\N
939	50	2	7017	32	Qualitätsperlweine	\N
940	50	2	7021	33	Qualitätsperlweine mit zugesetzter Kohlensäure	\N
941	50	2	7025	34	Qualitätsperlweine b.A.	\N
942	50	2	7053	40	Schaumweine nicht weiter differenzierbar	\N
943	50	2	7057	41	Schaumweine mit zugesetzter Kohlensäure	\N
944	50	2	7061	42	Qualitätsschaumweine	\N
945	50	2	7065	43	Qualitätsschaumweine b.A.	\N
946	50	2	7093	44	Aromatische Qualitätsschaumweine	\N
947	50	2	7097	45	Aromatische Qualitätsschaumweine b.A.	\N
948	50	2	7125	50	Unfertige Weine nicht differenzierbar auch Jungweine	\N
949	50	2	7153	51	Zur Gewinnung von Tafelweinen geeignete Weine auch Jungweine	\N
950	50	2	7181	52	Zur Gewinnung von Landweinen geeignete Weine auch Jungweine	\N
951	50	2	7209	53	Zur Gewinnung von Qualitätsweinen geeignete Weine auch Jungweine	\N
952	50	2	7237	54	Zur Gewinnung von Kabinett geeignete Weine auch Jungweine	\N
953	50	2	7247	55	Zur Gewinnung von Spätlese geeignete Weine auch Jungweine	\N
954	50	2	7257	56	Zur Gewinnung von Auslese geeignete Weine auch Jungweine	\N
955	50	2	7267	57	Zur Gewinnung von Beerenauslese geeignete Weine auch Jungweine	\N
956	50	2	7272	58	Zur Gewinnung von Trockenbeerenauslese geeignete Weine auch Jungweine	\N
957	50	2	7277	59	Zur Gewinnung von Eiswein geeignete Weine auch Jungweine	\N
958	50	2	7282	70	Traubenmoste und Traubenmaischen nicht weiter differenzierbar	\N
959	50	2	7310	71	Zur Herstellung von Tafelwein geeignete Traubenmoste bzw. -maischen	\N
960	50	2	7338	72	Zur Herstellung von Landweinen geeignete Traubenmoste bzw. -maischen	\N
961	50	2	7366	73	Zur Herstellung von Qualitätsweinen b.A. geeignete Traubenmoste bzw. -maischen	\N
962	50	2	7394	74	Zur Herstellung von Kabinett geeignete Traubenmoste bzw. -maischen	\N
963	50	2	7404	75	Zur Herstellung von Spätlesen geeignete Traubenmoste bzw. -maischen	\N
964	50	2	7414	76	Zur Herstellung von Auslese geeignete Traubenmoste bzw. -maischen	\N
965	50	2	7424	77	Zur Herstellung von Beerenauslese geeignete Traubenmoste bzw. -maischen	\N
966	50	2	7429	78	Zur Herst. von Trockenbeerenauslese geeignete Traubenmoste bzw. -maischen	\N
967	50	2	7434	79	Zur Herstellung von Eisweinen geeignete Traubenmoste bzw. -maischen	\N
968	50	2	7439	90	Traubenmoste teilweise gegoren	\N
969	50	2	7467	92	Traubenmoste konzentriert	\N
970	50	2	7471	94	Rektifizierte Traubenmostkonzentrate (RTK)	\N
971	51	2	7473	1	Likörweine nicht weiter differenzierbar	\N
972	51	2	7474	2	Qualitätslikörweine nicht weiter differenzierbar ggf. aus Drittländern	\N
973	51	2	7475	3	Qualitätslikörweine nicht weiter differenzierbar	\N
974	51	2	7484	4	Mit Alkohol stummgemachte Moste von frischen Trauben	\N
975	51	2	7485	8	Brennweine	\N
976	51	2	7486	10	Aromatisierte Weine auch schäumend nicht weiter differenzierbar	\N
977	51	2	7490	11	Aromatisierte weinhaltige Getränke auch schäumend nicht weiter differenzierbar	\N
978	51	2	7499	12	Aromatisierte weinhaltige Cocktails auch schäumend nicht weiter differenzierbar	\N
979	51	2	7502	13	Weinhaltige Getränke nicht aromatisiert nicht weiter differenzierbar	\N
980	51	2	7504	20	Alkoholreduzierte Erzeugnisse aus Wein nicht weiter differenzierbar	\N
981	51	2	7509	30	Mischgetränke weinhaltig	\N
982	51	2	7510	90	Vor- und Nebenprod. der Weinbereitung (auch Keltertrauben und Weinessigweine)	\N
983	52	2	7520	1	Weinähnliche Getränke aus Steinobst nicht weiter differenzierbar	\N
984	52	2	7525	2	Weinähnliche Getränke aus Beerenobst nicht weiter differenzierbar	\N
985	52	2	7533	3	Weinähnliche Getränke aus Kernobst nicht weiter differenzierbar	\N
986	52	2	7540	4	Weinähnliche Getränke aus sonstigen Früchten nicht weiter differenzierbar	\N
987	52	2	7543	5	Weinähnliche Getränke aus sonst. Ausgangsmaterial  nicht weiter differenzierbar	\N
988	52	2	7547	10	Perlweinähnliche Getränke aus Steinobst nicht weiter differenzierbar	\N
989	52	2	7552	15	Perlweinähnliche Getränke aus Beerenobst nicht weiter differenzierbar	\N
990	52	2	7558	20	Perlweinähnliche Getränke aus Kernobst nicht weiter differenzierbar	\N
991	52	2	7564	25	Perlweinähnliche Getränke aus sonstigen Früchten nicht  weiter differenzierbar	\N
992	52	2	7567	30	Perlweinähnliche Getränke aus sonst. Ausgangsm. nicht weiter differenzierbar	\N
993	52	2	7571	35	Schaumweinähnliche Getränke aus Steinobst nicht weiter differenzierbar	\N
994	52	2	7576	40	Schaumweinähnliche Getränke aus Beerenobst nicht weiter differenzierbar	\N
995	52	2	7582	45	Schaumweinähnliche Getränke aus Kernobst nicht weiter differenzierbar	\N
996	52	2	7586	50	Schaumweinähnliche Getränke aus sonst. Früchten nicht weiter differenzierbar	\N
997	52	2	7589	55	Schaumweinähnl. Getränke aus sonst. Ausgangsmaterial nicht weiter differenzierbar	\N
998	52	2	7593	60	Dessertweinähnliche Getränke aus Steinobst nicht weiter differenzierbar	\N
999	52	2	7598	65	Dessertweinähnliche Getränke aus Beerenobst nicht  weiter differenzierbar	\N
1000	52	2	7606	70	Dessertweinähnliche Getränke aus Kernobst nicht weiter differenzierbar	\N
1001	52	2	7611	75	Dessertweinähnliche Getränke aus sonstigen Früchten nicht weiter differenzierbar	\N
1002	52	2	7614	80	Dessertweinähnliche Getränke aus sonstigem Ausgangsmaterial	\N
1003	52	2	7615	90	Weiterverarbeitete Erzeugnisse aus weinähnlichen Getränken	\N
1004	52	2	7616	91	Obst- bzw. fruchtweinhaltige Getränke auch schäumend	\N
1005	52	2	7617	92	Mischgetränke aus Obst- bzw. Fruchtweinen auch schäumend	\N
1006	52	2	7618	93	Alkoholfreie weinähnliche Getränke	\N
1007	52	2	7619	94	Alkoholreduzierte weinähnliche Getränke	\N
1008	53	2	7621	1	Biere mit niedrigem Stammwürzegehalt obergärig	\N
1009	53	2	7624	2	Biere mit niedrigem Stammwürzegehalt untergärig	\N
1010	53	2	7627	3	Schankbiere obergärig	\N
1011	53	2	7633	4	Schankbiere untergärig	\N
1012	53	2	7638	5	Vollbiere obergärig	\N
1013	53	2	7655	6	Vollbiere untergärig	\N
1014	53	2	7672	7	Starkbiere obergärig	\N
1015	53	2	7681	8	Starkbiere untergärig	\N
1016	53	2	7687	9	Malzbiere	\N
1017	53	2	7691	11	Bierähnliche Getränke	\N
1018	53	2	7695	12	Biere mit beigegebenen LM	\N
1019	53	2	7705	13	Biere mit vermindertem Alkoholgehalt und verändertem Nährwert ausgenommen 490105	\N
1020	53	2	7731	14	Rohstoffe zur Bierherstellung	\N
1021	53	2	7752	16	Biere mit Zusatzstoffen	\N
1022	53	2	7753	17	Biere unter Zuckerverwendung hergestellt	\N
1023	53	2	7754	19	Biere mit Malzersatzstoffen	\N
1024	53	2	7763	20	Bierspezialitäten	\N
1025	53	2	7775	21	Färbebiere	\N
1026	54	2	7777	1	Extraktarme Spirituosen ausgenommen 370200 bis 370900	\N
1027	54	2	7785	2	Spirituose aus Zuckerrohr	\N
1028	54	2	7790	3	Getreidespirituosen	\N
1029	54	2	7806	4	Weinspirituosen	\N
1030	54	2	7814	5	Obstbrände/-wasser	\N
1031	54	2	7850	6	Obstgeiste sowie Obstbrände mit zusätzlicher Angabe durch Einmaischen und Destillieren gewonnen	\N
1032	54	2	7864	7	Obstspirituosen	\N
1033	54	2	7872	8	Spirituosen aus bestimmten pflanzlichen Materialien ausg. 370100 bis 370700	\N
1034	54	2	7884	9	Spirituosen mit bestimmten pflanzlichen Materialien ausg. 370100 bis 370700	\N
1035	54	2	7900	10	Liköre mit Fruchtsaft/Fruchtaroma	\N
1036	54	2	7942	11	Kaffee- Tee- und Kakaoliköre ausg. 371300	\N
1037	54	2	7947	12	Kräuter- Gewürz- Blüten- und Bitterliköre	\N
1038	54	2	7963	13	Emulsionsliköre	\N
1039	54	2	7975	14	Besondere Likörarten	\N
1040	54	2	7987	15	Sonstige Spirituosen sowie Mischgetränke aus Spirituosen u. a. Lebensmitteln	\N
1041	54	2	7994	16	Alkoholhaltige Getränke ausg. 370100 bis 371500	\N
1042	54	2	8021	20	Roh- Zwischen- und Nebenprodukte der Alkoholindustrie	\N
1043	55	2	8030	1	Saccharose	\N
1044	55	2	8037	2	Zwischen- u. Nebenprodukte der Saccharoseherstellung	\N
1045	55	2	8045	3	Invertzucker	\N
1046	55	2	8048	4	Stärkeverzuckerungsprodukte	\N
1047	55	2	8057	5	Zucker andere	\N
1048	55	2	8062	6	Zucker mit anderen LM	\N
1049	55	2	8065	7	Süßungsmittel aus Pflanzensäften	\N
1051	56	2	8093	2	Blütenhonigmischungen	\N
1052	56	2	8102	3	Honigtauhonige und Honigmischungen	\N
1053	56	2	8112	4	Industriehonige	\N
1054	56	2	8115	5	Invertzuckerkrems	\N
1055	56	2	8117	6	Brotaufstriche süße	\N
1056	56	2	8146	7	Brotaufstriche nicht süße	\N
1057	56	2	8155	9	Honige	\N
1058	56	2	8156	10	Trockenhonige	\N
1059	56	2	8157	11	Mischerzeugnisse mit Honig Blütenpollen und -zubereitungen	\N
1060	56	2	8162	12	Brotaufstriche süße brennwertreduziert	\N
1061	56	2	8173	20	Wabenhonige	\N
1062	56	2	8175	21	Honige mit zugesetzten Lebensmitteln	\N
1063	57	2	8178	1	Beerenobstkonfitüren extra	\N
1064	57	2	8191	2	Beerenobstkonfitüren	\N
1065	57	2	8204	3	Beerenobstgelees extra	\N
1066	57	2	8212	4	Beerenobstgelees	\N
1067	57	2	8220	5	Kernobstkonfitüren extra	\N
1068	57	2	8222	6	Kernobstkonfitüren	\N
1069	57	2	8224	7	Kernobstgelees extra	\N
1070	57	2	8228	8	Kernobstgelees	\N
1071	57	2	8232	9	Steinobstkonfitüren extra	\N
1072	57	2	8240	10	Steinobstkonfitüren	\N
1073	57	2	8248	11	Steinobstgelees extra	\N
1074	57	2	8250	12	Steinobstgelees	\N
1075	57	2	8252	13	Zitrusfrüchtekonfitüren extra	\N
1076	57	2	8257	14	Zitrusfrüchtekonfitüren	\N
1077	57	2	8262	15	Zitrusfrüchtegelees extra	\N
1078	57	2	8267	16	Zitrusfrüchtegelees	\N
1079	57	2	8272	17	Zitrusfrüchtemarmeladen	\N
1080	57	2	8276	18	Früchte exotische u. Rhabarber Konfitüre extra	\N
1081	57	2	8285	19	Früchte exotische u. Rhabarber Konfitüre	\N
1082	57	2	8294	20	Früchte exotische u. Rhabarber Gelees extra	\N
1083	57	2	8300	21	Früchte exotische u. Rhabarber Gelees	\N
1084	57	2	8306	22	Mischungen Konfitüren extra	\N
1085	57	2	8310	23	Mischungen Gelees	\N
1086	57	2	8312	24	Pflaumenmus	\N
1087	57	2	8317	25	Fruchtzubereitungen	\N
1088	57	2	8321	26	Mischungen Konfitüren	\N
1089	57	2	8325	27	Obstkraut Maronenerzeugnisse süßer Brotaufstrich	\N
1090	57	2	8330	28	Fruchtaufstriche	\N
1091	57	2	8333	30	Beerenobstkonfitüren brennwertreduziert	\N
1092	57	2	8334	31	Kernobstkonfitüren brennwertreduziert	\N
1093	57	2	8335	32	Steinobstkonfitüren brennwertreduziert	\N
1094	57	2	8336	33	Zitrusfrüchtekonfitüren brennwertreduziert	\N
1095	57	2	8337	34	Früchte exotische u. Rhabarber Konfitüre brennwertreduziert	\N
1096	57	2	8338	35	Mischungen Konfitüren brennwertreduziert	\N
1097	58	2	8343	1	Kremeis	\N
1098	58	2	8355	2	Fruchteis	\N
1099	58	2	8377	3	Rahmeis	\N
1100	58	2	8390	4	Milcheis	\N
1101	58	2	8443	5	Eiskrem	\N
1102	58	2	8523	8	Halberzeugnisse für Speiseeis	\N
1103	58	2	8535	9	Eissorten zusammengesetzte auch mit anderen LM	\N
1104	58	2	8543	10	Sorbet Eis	\N
1105	58	2	8552	11	Eisimitate	\N
1106	58	2	8553	12	Eis mit Pflanzenfett	\N
1107	58	2	8562	13	Fruchteiskrem	\N
1108	58	2	13255	14	Eis	\N
1109	58	2	13455	15	Wassereis mit geschmacksgebenden Zutaten	\N
1110	59	2	8564	1	Hartkaramellen	\N
1111	59	2	8580	2	Weichkaramellen	\N
1112	59	2	8595	3	Fondanterzeugnisse	\N
1113	59	2	8604	4	Gelee-Erzeugnisse	\N
1114	59	2	8607	5	Gummibonbons	\N
1115	59	2	8613	6	Schaumzuckerwaren	\N
1116	59	2	8622	7	Türkischer Nougat und ähnliche Erzeugnisse	\N
1117	59	2	8626	8	Lakritzerzeugnisse	\N
1118	59	2	8629	9	Dragees	\N
1119	59	2	8652	10	Presslinge	\N
1120	59	2	8653	11	Kanditen	\N
1121	59	2	8663	12	Brause-/Limonadenpulver und -tabletten zum Essen	\N
1122	59	2	8667	13	Eiskonfekte	\N
1123	59	2	8668	14	Krokant und krokantähnliche Erzeugnisse	\N
1124	59	2	8676	15	Kaugummis	\N
1125	59	2	8684	16	Marzipanerzeugnisse	\N
1126	59	2	8689	17	Persipanerzeugnisse	\N
1127	59	2	8694	18	Nougaterzeugnisse	\N
1128	59	2	8698	19	Süßwaren aus Rohmassen anderer Art	\N
1129	59	2	8704	20	Süßwaren Mischungen mit Schokolade	\N
1130	59	2	8706	21	Früchte mit Zuckerglasur glasiert	\N
1131	59	2	8708	22	Fruchtschnitten	\N
1132	59	2	8724	23	Zuckerwatte	\N
1133	59	2	8725	99	Süßwaren-Mischungen	\N
1134	60	2	8727	1	Schokoladen	\N
1135	60	2	8731	2	Haushaltsschokoladen	\N
1136	60	2	8735	3	Schokoladestreusel Schokoladeflocken	\N
1137	60	2	8736	4	Gianduja-Haselnussschokoladen	\N
1138	60	2	8739	5	Schokoladeüberzugsmassen	\N
1139	60	2	8742	6	Milchschokoladen	\N
1140	60	2	8746	7	Haushaltsmilchschokoladen	\N
1141	60	2	8750	8	Milchschokoladestreusel/-flocken	\N
1142	60	2	8751	9	Gianduja-Haselnussmilchschokoladen	\N
1143	60	2	8754	10	Milchschokoladeüberzugsmassen	\N
1144	60	2	8757	11	Weiße Schokoladen	\N
1145	60	2	8761	12	Sahneschokoladen	\N
1146	60	2	8765	13	Sahneschokoladeüberzugsmassen	\N
1147	60	2	8768	14	Magermilchschokoladen	\N
1148	60	2	8772	15	Mischungen verschiedener Schokoladearten	\N
1149	60	2	8773	16	Pralinen	\N
1150	60	2	8781	18	Früchte mit Schokoladeüberzug	\N
1151	60	2	8792	20	Weiße Schokoladeüberzugsmassen	\N
1152	61	2	8796	1	Rohstoffe für Kakao und kakaohaltige Erzeugnisse	\N
1153	61	2	8802	2	Kakaomassen	\N
1154	61	2	8805	3	Kakaopresskuchen	\N
1155	61	2	8809	4	Kakaopulver	\N
1156	61	2	8812	5	Kakaopulver löslich	\N
1157	61	2	8816	6	Kakaopulver löslich mit Lecithinzusatz	\N
1158	61	2	8820	7	Kakaopulver mit natürlichen Gewürzen	\N
1159	61	2	8823	8	Kakaopulver gezuckert	\N
1160	61	2	8826	9	Haushaltskakaopulver gezuckert	\N
1161	61	2	8829	10	Kakaohaltige Pulver und Zubereitungen	\N
1162	62	2	8833	1	Kaffee roher	\N
1163	62	2	8837	2	Kaffee gerösteter	\N
1164	62	2	8842	3	Kaffeextrakte	\N
1165	62	2	8848	4	Kaffeeersatzrohstoffe	\N
1166	62	2	8853	5	Kaffeeersatzstoffe Kaffeezusätze Kaffeeersatzmischungen	\N
1167	62	2	8865	6	Kaffeeersatzextrakte	\N
1168	62	2	8868	7	Kaffeegetränke	\N
1169	62	2	8873	8	Kaffeeersatzgetränke	\N
1170	62	2	8878	9	Mischungen aus Kaffee u. Kaffeeersatzst. u./o. Kaffeezusätzen	\N
1171	62	2	8879	10	Mischungen aus Kaffeeersatzgetränken m. a. LM	\N
1172	62	2	13476	11	Mischungen aus Kaffee m. a. LM	\N
1173	63	2	8881	1	Tees unfermentierte	\N
1174	63	2	8882	2	Tees halbfermentierte	\N
1175	63	2	8885	3	Tees fermentierte	\N
1176	63	2	8889	4	Teemischungen aus halbfermentiertem und fermentiertem Tee	\N
1177	63	2	8890	5	Tee-Extrakte	\N
1178	63	2	8896	6	Teeähnliche Erzeugnisse	\N
1179	63	2	8917	7	Mischungen aus Tee mit teeähnlichen Erzeugnissen	\N
1180	63	2	8918	8	Teeaufgüsse	\N
1181	63	2	8930	9	Aromatisierte Tees	\N
1182	63	2	8935	10	Aromatisierte teeähnliche Erzeugnisse	\N
1183	63	2	8936	11	Aromatisierte Tee-Extrakte	\N
1184	63	2	8941	12	Extrakte aus teeähnlichen Erzeugnissen	\N
1185	63	2	8954	13	Aromatisierte Extrakte aus teeähnlichen Erzeugnissen	\N
1186	63	2	8967	14	Zubereitungen aus Lebensmitteln mit Tee-Extrakten	\N
1187	63	2	8971	15	Zubereitungen aus Lebensmitteln mit Extrakten aus teeähnl. Erzeugnissen	\N
1188	63	2	8976	16	Tees speziell fermentierte	\N
1189	64	2	8980	1	gestr. jetzt 480000, Säuglings- und Kleinkindernahrung auf Milch- u./o. Sojabasis	\N
1190	64	2	8996	2	gestr. jetzt 481200, Säuglings- und Kleinkindernahrung auf Getreidebasis ohne Milch	\N
1191	64	2	9006	3	gestr. jetzt 481400, Säuglings- und Kleinkindernahrung auf Gemüse- u./o. Obstbasis	\N
1192	64	2	9017	4	gestr. jetzt 481300, Fertigmenüs für Säuglinge	\N
1193	64	2	9027	5	gestr. jetzt 481300, Kleinkindmenüs	\N
1194	64	2	9040	6	gestr. jetzt 482500, Kinderzucker Nähr- und Aufbauzucker	\N
1195	64	2	9041	7	gestr. jetzt 482500, Säuglings- und Kleinkindernahrung andere	\N
1196	64	2	9043	8	gestr. jetzt 482000, Babyteerzeugnisse	\N
1197	64	2	13175	10	Säuglingsanfangsnahrungen	\N
1198	64	2	13176	11	Folgenahrungen für Säuglinge	\N
1199	64	2	13177	12	Getreidebeikost für Säuglinge und Kleinkinder	\N
1200	64	2	13178	13	Komplettmahlzeiten für Säuglinge und Kleinkinder ausgen. 481200	\N
1201	64	2	13179	14	Beikost auf Obst- und/oder Gemüsebasis für Säuglinge und Kleinkinder ausgen. 481200	\N
1202	64	2	13180	20	Teeerzeugnisse für Säuglinge und Kleinkinder	\N
1203	64	2	13181	25	Säuglings- und Kleinkindernahrungen ausgenommen 481000 bis 482000	\N
1204	64	2	13195	26	Tageskost Gesamtnahrung der Warengruppe 480000	\N
1205	65	2	13256	21	gestrichen; Diätetische Lebensmittel für Diabetiker	\N
1206	65	2	13257	22	Diätetische Lebensmittel zur natriumarmen/streng natriumarmen Ernährung	\N
1207	65	2	13258	23	Lebensmittel für besondere medizinische Zwecke (bilanzierte Diäten)	\N
1208	65	2	13259	24	Lebensmittel für kalorienarme Ernährung zur Gewichtsverringerung	\N
1209	65	2	13260	25	Lebensmittel zur glutenfreien Ernährung ausgen. Lebensmittel für Säuglinge und Kleinkinder	\N
1210	65	2	13261	26	Lebensmittel für intensive Muskelanstrengungen vor allem für Sportler	\N
1211	65	2	13262	27	Diätetische Lebensmittel zur fettmodifizierten Ernährung	\N
1212	65	2	13263	28	Sonstige diätetische Lebensmittel	\N
1213	66	2	9123	1	Teilfertiggerichte auch tiefgefroren	\N
1214	66	2	9165	2	Zusammengesetzte Fertiggerichte auch tiefgefroren	\N
1215	66	2	9224	3	Zubereitete Speisen aus Gaststätten Kantinen u.ä.	\N
1216	66	2	9231	4	Teilfertiggerichte Konserven	\N
1217	66	2	9255	5	Zusammengesetzte Fertiggerichte Konserven	\N
1218	66	2	9294	6	Zusammengesetzte Fertiggerichte trocken	\N
1219	66	2	9296	7	Teilfertiggerichte trocken	\N
1220	66	2	9301	90	Tageskost (Gesamtnahrung)	\N
1221	67	2	9344	32	gestrichen, Gelatinepräparate zur Nahrungsergänzung	\N
1222	67	2	13264	40	Nahrungsergänzungsmittel ausgen. 492600	\N
1223	67	2	13265	41	Vitaminpräparate	\N
1224	67	2	13266	42	Mineralstoffpräparate	\N
1225	67	2	13267	43	Vitamin- und Mineralstoffpräparate	\N
1226	67	2	13268	44	Präparate mit speziellen Fettsäuren	\N
1227	67	2	13269	45	Ballaststoffkonzentrate	\N
1228	67	2	13270	46	Pflanzenextrakte sekundäre Pflanzeninhaltsstoffe (SPS)	\N
1229	67	2	13477	47	Nahrungsergänzungsmittel mit Glucosamin u./o. Chondroitin	\N
1230	67	2	13271	50	Ergänzungsnahrung ausgen. für intensive Muskelanstrengungen	\N
1231	67	2	13272	51	Eiweiß- und Aminosäurenpräparate	\N
1232	67	2	13273	52	Hefepräparate ausgen. Elementhefen	\N
1233	67	2	13274	53	Gelatinepräparate	\N
1234	67	2	13275	54	Algenpräparate ausgen. 250600 263000 u. 470618	\N
1235	67	2	13276	55	Präparate aus Bienenprodukten	\N
1236	67	2	13277	56	Enzympräparate	\N
1237	67	2	13278	57	Präparate mit tierischen Konzentraten	\N
1238	68	2	9350	1	Würzsoßen Würzpasten	\N
1239	68	2	9382	2	Speisewürzen flüssig (Eiweißhydrolysate)	\N
1240	68	2	9383	3	Würzmischung mit Glutamat ohne Gewürzzusatz	\N
1241	68	2	9385	4	Säuerungsmittel	\N
1242	68	2	9418	5	Speisesalz	\N
1243	68	2	9427	6	Speisesenf	\N
1244	68	2	9439	7	Meerrettichzubereitungen	\N
1245	68	2	9445	8	Würzmittelzubereitungen	\N
1246	68	2	9458	9	Curry-Pulver	\N
1247	68	2	9459	10	Gewürzzubereitungen Gewürzpräparate	\N
1248	68	2	9471	12	Gewürzsalze	\N
1249	68	2	9483	13	Gewürzextraktsalze	\N
1250	68	2	9491	14	Gewürzaromasalze	\N
1251	68	2	9492	15	Gewürzaromazubereitungen	\N
1252	68	2	9494	16	Gewürzaromapräparate	\N
1253	68	2	9496	17	Präparate mit würzenden Stoffen	\N
1254	68	2	9498	18	Würzer	\N
1255	69	2	9504	1	Gewürze Wurzeln Wurzelstöcke	\N
1256	69	2	9511	2	Gewürze Blätter Kräuter	\N
1257	69	2	9537	3	Gewürze Rinden	\N
1258	69	2	9539	4	Gewürze Blüten Blätter	\N
1259	69	2	9545	5	Gewürze Früchte	\N
1260	69	2	9566	6	Gewürze Samen	\N
1261	69	2	9571	7	Würzpilze	\N
1262	69	2	9574	8	Gewürzmischungen	\N
1263	70	2	9616	6	Aromastoffe naturidentisch	\N
1264	70	2	9623	7	Aromagebende Stoffe und Pflanzenteile	\N
1265	70	2	9632	8	Rauch	\N
1266	70	2	9636	10	Aromen mit natürlichen Aromastoffen Aromaextrakte	\N
1267	70	2	9637	11	Aromen mit naturidentischen Aromastoffen	\N
1268	70	2	9638	12	Aromen mit künstlichen Aromastoffen	\N
1269	70	2	9639	13	Aromen mit natürlichen und naturidentischen Aromastoffen	\N
1270	70	2	9640	14	Aromen mit natürlichen und künstlichen Aromastoffen	\N
1271	70	2	9641	15	Aromen mit naturidentischen und künstlichen Aromastoffen	\N
1272	70	2	9642	16	Aromen mit natürl. naturidentischen und künstl. Aromastoffen	\N
1273	70	2	9643	17	Reaktionsaromen	\N
1274	70	2	9644	18	Raucharomen	\N
1275	71	2	9646	1	Hilfsmittel für Fleisch- und Wurstwaren	\N
1276	71	2	9662	2	Hilfsmittel für Backwaren	\N
1277	71	2	9670	3	Hilfsmittel für Backwarenfüllungen und -überzüge	\N
1278	71	2	9677	4	Hilfsmittel zur Süßung	\N
1279	71	2	9683	5	Hilfsmittel zur Käseherstellung	\N
1280	71	2	9689	6	Hilfsmittel zur Vorratshaltung	\N
1281	71	2	9691	8	Obstgeliermittel	\N
1282	71	2	9692	9	Gelatine	\N
1283	71	2	9693	10	Hilfsmittel zur Speiseeisherstellung	\N
1284	71	2	9694	11	Überzugsmittel ausgen. 571200-571299	\N
1285	71	2	9700	15	Convenience-Produkte für Backwaren	\N
1286	72	2	9705	1	Aminosäuren deren Salze Derivate	\N
1287	72	2	9732	2	Antioxydantien	\N
1288	72	2	9741	3	Aromastoffe künstliche Geschmacksstoffe u. Verstärker	\N
1289	72	2	9767	4	Bleichmittel Entfärbemittel	\N
1290	72	2	9771	5	Dickungsmittel Geliermittel	\N
1291	72	2	9795	6	Emulgatoren	\N
1292	72	2	9803	7	Entkeimungsmittel	\N
1293	72	2	9811	8	Enzyme	\N
1294	72	2	9819	9	Farbstoffe	\N
1295	72	2	9863	10	Genusssäuren Salze	\N
1296	72	2	9881	11	Härtungsmittel	\N
1297	72	2	9885	12	Kaumassen Überzugsmittel	\N
1298	72	2	9904	13	Klärmittel Filterhilfsstoffe	\N
1299	72	2	9925	14	Kochsalzersatz Zusätze für	\N
1300	72	2	9934	15	Konservierungsstoffe	\N
1301	72	2	9951	16	Lösungsmittel Extraktionsmittel Feuchthaltemittel	\N
1302	72	2	9967	17	Oberflächenbehandlungsmittel	\N
1303	72	2	9972	18	Pökel- Umröte- Kutterhilfsmittel ausg. Nitrat/Nitrit	\N
1304	72	2	9974	19	Rieselfähigkeit Mittel zur Erhaltung der	\N
1305	72	2	9977	20	Säuren Basen Salze Oxide anorganische Mineralstoffe	\N
1306	72	2	9997	21	Stabilisatoren	\N
1307	72	2	9998	22	Süßstoffe	\N
1308	72	2	10004	23	Trägerstoffe	\N
1309	72	2	10011	24	Treibgase	\N
1310	72	2	10016	25	Trennmittel außer den bei Kaumassen genannten	\N
1311	72	2	10025	26	Trinkwasseraufbereitungsm. ausgenommen 570700 u. 572000	\N
1312	72	2	10029	27	Vitamine	\N
1313	72	2	10050	28	Zuckeraustauschstoffe ausgenommen 390501	\N
1314	72	2	10059	30	Spurenelementverbindungen	\N
1315	72	2	13545	31	Isolierte Pflanzenfasern	\N
1316	73	2	10070	1	Rohwasser aus Grundwasser	\N
1317	73	2	10083	2	Rohwasser aus Oberflächenwasser	\N
1318	73	2	10090	3	Trinkwasser Zentralversorgung Grundwasser	\N
1319	73	2	10097	4	Trinkwasser Zentralversorgung Oberflächenwasser	\N
1320	73	2	10101	5	Trinkwasser aus Mischwasser Zentralversorgung	\N
1321	73	2	10104	6	Trinkwasser Eigen- und Einzelversorgung	\N
1322	73	2	10107	7	Trinkwasser mobiler Wasserversorgung	\N
1323	73	2	10116	8	Brauchwasser	\N
1324	73	2	10125	9	Eis aus Trinkwasser	\N
1325	73	2	10126	10	Eis aus Brauchwasser	\N
1326	73	2	10127	11	Natürliche Mineralwasser	\N
1327	73	2	10131	12	Quellwasser	\N
1328	73	2	10135	13	Tafelwasser	\N
1329	73	2	10140	15	Abgefüllte Trinkwasser	\N
1330	73	2	10143	17	Physikalisch behandelte Wasser	\N
1331	73	2	13295	80	Trinkwasser aus Ortsnetzen ohne Hausinstallationen	\N
1332	73	2	10146	90	Trinkwasser Hausinstallation	\N
1333	74	2	10188	1	Rohtabake	\N
1334	74	2	10192	2	Rauchtabake	\N
1335	74	2	10197	3	Schnupftabake und andere Mittel zum Schnupfen	\N
1336	74	2	10201	4	Kautabake und andere Mundtabake (z.B. Snus)	\N
1337	74	2	10204	5	Zigaretten	\N
1338	74	2	10212	6	Zigarren/Zigarillos	\N
1339	74	2	10216	7	Tabakhaltige Stoffe ausgen. 600100 - 600600	\N
1340	74	2	10219	8	Zigarettenpapier	\N
1341	74	2	10220	9	Rauchfilter	\N
1342	74	2	10221	10	Rauchfilterumhüllungen	\N
1343	74	2	10222	12	Tabakersatz	\N
1344	74	2	10225	13	Tabakerzeugnisse Stoffe und Gegenstände zu deren Herstellung	\N
1345	75	2	10263	80	Verpackungsmaterialien für kosmetische Mittel	\N
1346	75	2	10279	90	Verpackungsmaterialien für Tabakerzeugnisse	\N
1347	76	2	10296	81	Bekleidung	\N
1348	76	2	10347	82	Kurzwaren/Materialien zur Herstellung von Bekleidung	\N
1349	76	2	10355	83	Accessoires	\N
1350	76	2	10379	84	Hygieneerzeugnisse	\N
1351	76	2	10386	85	Sonstige Bedarfsgegenstände mit Körperkontakt	\N
1352	76	2	10398	91	Bedarfsgegenstände zur Körperpflege	\N
1353	76	2	10405	92	Bedarfsgegenstände mit Mundschleimhautkontakt	\N
1354	77	2	10415	10	Wasch- und Reinigungsmittel für Textilien	\N
1355	77	2	10423	11	Waschhilfsmittel und Enthärter	\N
1356	77	2	10428	12	Fleckenentfernungsmittel und Entfärber für Textilien	\N
1357	77	2	10431	13	Imprägnierungs- und Ausrüstmittel für Textilien	\N
1358	77	2	10440	14	Teppich- und Polsterreiniger	\N
1359	77	2	10441	20	Allzweck-/Universalreiniger für den Haushalt	\N
1360	77	2	10443	21	Fußbodenreiniger/-pflegemittel (ausgen. 831400)	\N
1361	77	2	10449	22	Fenster-/Glasreiniger	\N
1362	77	2	10450	23	Rohr-/Abflussreiniger	\N
1363	77	2	10451	24	Sanitär-/WC-Reiniger	\N
1364	77	2	10452	25	Metallputzmittel (ausgen. 833300)	\N
1365	77	2	10453	26	Stein-/Marmor-/Keramik-/Emaillenreiniger	\N
1366	77	2	10454	27	Möbelpflegemittel	\N
1367	77	2	10455	28	Lederpflegemittel	\N
1368	77	2	10459	30	Geschirrspülmittel	\N
1369	77	2	10464	31	Entkalker für Haushaltsgeräte	\N
1370	77	2	10465	32	Grill-/Backofen-/Mikrowellenreiniger	\N
1371	77	2	10466	33	Metallputzmittel (ausgen.832500)	\N
1372	77	2	10468	34	Desinfektionsmittel	\N
1373	77	2	10471	35	Gewerbliche Reiniger für Lebensmittelbedarfsgegenstände	\N
1374	77	2	10474	40	Spezielle Reinigungsmittel für den häuslichen Bedarf (ausgen.832000-832800)	\N
1375	77	2	10481	41	Reinigungs- und Pflegemittel für den Heimwerker- und Hobbybedarf	\N
1376	77	2	10486	42	Fahrzeugpflege- und -reinigungsmittel	\N
1377	77	2	10495	50	Raumluftverbesserer	\N
1378	77	2	10496	51	WC-Hygieneprodukte	\N
1379	77	2	10499	53	Insektenvertilgungsmittel	\N
1380	78	2	10501	10	Mittel zur Hautreinigung	\N
1381	78	2	10521	11	Mittel zur Hautpflege	\N
1382	78	2	10570	12	Mittel zur Beeinflussung des Aussehens	\N
1383	78	2	10599	13	Mittel zur Haarbehandlung	\N
1384	78	2	10639	14	Nagelkosmetik	\N
1385	78	2	10648	15	Reinigungs- und Pflegemittel für Mund Zähne und Zahnersatz	\N
1386	78	2	10659	16	Mittel zur Beeinflussung des Körpergeruchs und zur Vermittlung von Geruchseindrücken	\N
1387	78	2	10673	17	Stoffe zur Herstellung kosmetischer Mittel	\N
1388	79	2	10686	10	Spielwaren für Kinder unter 36 Monaten (Babyspielzeug etc.)	\N
1389	79	2	10693	11	Modellspielzeug	\N
1390	79	2	10700	12	Mal- und Zeichenbedarf	\N
1391	79	2	10707	13	Gesellschaftsspiele	\N
1392	79	2	10710	14	Bau- und Experimentierkästen Kreativspiele	\N
1393	79	2	10714	15	Modelliermassen	\N
1394	79	2	10718	16	Aktionsspielzeug	\N
1395	79	2	10724	17	Rollenspielzeug	\N
1396	79	2	13315	20	Bilderbücher	\N
1397	79	2	10732	90	Scherzartikel	\N
1398	80	2	10734	10	Verpackungsmaterialien für Lebensmittel	\N
1399	80	2	10750	30	Gegenstände zum Verzehr von Lebensmitteln	\N
1400	80	2	10766	50	Gegenstände zum Kochen/Braten/Backen/Grillen (ausgenommen 869000)	\N
1401	80	2	10782	70	Sonstige Gegenstände zur Herstellung und Behandlung von Lebensmitteln (ausgenommen 869000)	\N
1402	80	2	10798	90	Maschinen zur gewerblichen Herstellung von Lebensmitteln	\N
1403	81	2	10816	1	Knochen (Ossa)	\N
1404	81	2	10827	2	Gelenke (Juncturae)	\N
1405	81	2	10831	3	Muskeln (Musculi)	\N
1406	81	2	10834	4	Knorpel (Cartilagines)	\N
1407	81	2	10841	5	Schleimbeutel (Bursae)	\N
1408	81	2	10842	6	Sehnen (Tendines)	\N
1409	81	2	10843	7	Sehnenscheiden (Vaginae tendines)	\N
1410	81	2	10844	8	Bänder (Ligamenta)	\N
1411	81	2	10845	9	Bindegewebshüllen (Fasciae)	\N
1412	81	2	10846	10	Fettgewebe (Panniculus adiposus)	\N
1413	81	2	10847	11	Bindegewebe	\N
1414	81	2	10848	12	Gelenkflüssigkeit (Synovia)	\N
1415	81	2	10849	15	Respirationstrakt (Apparatus respiratorius)	\N
1416	81	2	10856	16	Lungen (Pulmines)	\N
1417	81	2	10861	17	Schleim, Nase	\N
1418	81	2	10864	20	Herz (Cor)	\N
1419	81	2	10869	21	Blutgefäße (Vasa)	\N
1420	81	2	10875	25	Verdauungssystem (Apparatus digestorius)	\N
1421	81	2	10881	26	Zähne	\N
1422	81	2	10886	27	Speicheldrüsen (Glandulae salivales)	\N
1423	81	2	10890	28	Leber (Hepar)	\N
1424	81	2	10896	29	Bauchspeicheldrüse (Pancreas)	\N
1425	81	2	10897	30	Speichel (Saliva)	\N
1426	81	2	10898	31	Galle (Bilis)	\N
1427	81	2	10899	32	Pankreassaft	\N
1428	81	2	10900	33	Rachen (Pharynx)	\N
1429	81	2	10901	34	Mandeln (Tonsillae)	\N
1430	81	2	10902	35	Speiseröhre (Oesophagus)	\N
1431	81	2	10903	36	Magen (Gaster)	\N
1432	81	2	10904	37	Netz (Omentum)	\N
1433	81	2	10905	38	Dünndarm (Intestinum tenue)	\N
1434	81	2	10906	39	Zwölffingerdarm (Duodenum)	\N
1435	81	2	10907	40	Leerdarm (Jejunum)	\N
1436	81	2	10908	41	Krummdarm (Ileum)	\N
1437	81	2	10909	42	Wurmfortsatz (Appendix)	\N
1438	81	2	10910	43	Dickdarm (Colon)	\N
1439	81	2	10911	44	Blinddarm (Caecum)	\N
1440	81	2	10912	45	Enddarm (Rectum)	\N
1441	81	2	10913	46	After (Anus)	\N
1442	81	2	10914	47	Gewebe, perianales	\N
1443	81	2	10915	48	Magendarmtrakt-Flüssigkeiten	\N
1444	81	2	10923	50	Harntrakt (Apparatus urogenitalis)	\N
1445	81	2	10928	51	Harnleiter (Ureter)	\N
1446	81	2	10929	52	Harnblase (Vesica urenaria)	\N
1447	81	2	10930	53	Harnröhre (Urethra)	\N
1448	81	2	10931	54	Glied männliches (Penis)	\N
1449	81	2	10933	55	Hoden (Testes)	\N
1450	81	2	10936	56	Harntraktflüssigkeiten	\N
1451	81	2	10942	57	Scham (Vulva)	\N
1452	81	2	10943	58	Scheide (Vagina)	\N
1453	81	2	10944	59	Gebärmutter (Uterus)	\N
1454	81	2	10946	60	Eierstock (Ovar)	\N
1455	81	2	10949	61	Nachgeburt (Placenta) (Secundina)	\N
1456	81	2	10950	62	Nabelschnur (Funiculus umbilicalis)	\N
1457	81	2	10951	63	Fötus (Embryo)	\N
1459	81	2	10961	66	Nebenniere (Glandula suprarenalis)	\N
1460	81	2	10964	70	Nervensystem (Systema nervosum)	\N
1461	81	2	10965	71	Gehirn (Encephalon) (Cerebrum)	\N
1462	81	2	10975	72	Cerebrospinale Flüssigkeit (Liquor cerebrospinalis)	\N
1463	81	2	10976	73	Auge (Oculus)	\N
1464	81	2	10983	74	Ohr (Auris)	\N
1465	81	2	10988	80	Haut (Cutis)	\N
1466	81	2	10992	81	Haar (Pilus)	\N
1467	81	2	10998	82	Nagel (Unguis)	\N
1468	81	2	11001	83	Unterhaut (Subcutis)	\N
1469	81	2	11002	84	Brustdrüse (Mamma)	\N
1470	81	2	11007	87	Lymphatisches System (Systema lymphatica)	\N
1471	81	2	11009	88	Hämatopoetisches System (Systema haematopoetica)	\N
1472	81	2	11010	89	Knochenmark (Medulla ossium)	\N
1473	81	2	11012	90	Milz (Lien) (Splen)	\N
1474	81	2	11016	91	Lymphknoten (Nodi lymphatici)	\N
1475	81	2	11026	92	Blut	\N
1476	81	2	11032	95	Hautflüssigkeiten	\N
1477	81	2	11036	96	Frauenmilch	\N
1478	82	2	11052	1	Knochen (Ossa)	\N
1479	82	2	11065	2	Gelenke (Juncturae)	\N
1480	82	2	11070	3	Muskeln (Musculi)	\N
1481	82	2	11073	4	Knorpel (Cartilagines)	\N
1482	82	2	11080	5	Schleimbeutel (Bursae)	\N
1483	82	2	11081	6	Sehnen (Tendines)	\N
1484	82	2	11082	7	Sehnenscheiden (Vaginae tendines)	\N
1485	82	2	11083	8	Bänder (Ligamenta)	\N
1486	82	2	11084	9	Bindegewebshüllen (Fasciae)	\N
1487	82	2	11085	10	Fettgewebe (Panniculus adiposus)	\N
1488	82	2	11086	11	Bindegewebe	\N
1489	82	2	11087	12	Gelenkflüssigkeit (Synovia)	\N
1490	82	2	11088	15	Respirationstrakt (Apparatus respiratorius)	\N
1491	82	2	11096	16	Lungen (Pulmines)	\N
1492	82	2	11100	17	Schleim, Nase	\N
1493	82	2	11104	20	Herz (Cor)	\N
1494	82	2	11109	21	Blutgefäße (Vasa)	\N
1495	82	2	11115	25	Verdauungssystem (Apparatus digestorius)	\N
1496	82	2	11121	26	Zähne	\N
1497	82	2	11130	27	Speicheldrüsen (Glandulae salivales)	\N
1498	82	2	11134	28	Leber (Hepar)	\N
1499	82	2	11140	29	Bauchspeicheldrüse (Pancreas)	\N
1500	82	2	11141	30	Speichel (Saliva)	\N
1501	82	2	11143	31	Galle (Bilis)	\N
1502	82	2	11145	32	Pankreassaft	\N
1503	82	2	11146	33	Rachen (Pharynx)	\N
1504	82	2	11147	34	Mandeln (Tonsillae)	\N
1505	82	2	11148	35	Speiseröhre (Oesophagus)	\N
1506	82	2	11149	36	Magen (Gaster)	\N
1507	82	2	11151	37	Netz (Omentum)	\N
1508	82	2	11152	38	Dünndarm (Intestinum tenue)	\N
1509	82	2	11154	39	Zwölffingerdarm (Duodenum)	\N
1510	82	2	11155	40	Leerdarm (Jejunum)	\N
1511	82	2	11156	41	Krummdarm (Ileum)	\N
1512	82	2	11157	42	Wurmfortsatz (Appendix)	\N
1513	82	2	11158	43	Dickdarm (Colon)	\N
1514	82	2	11159	44	Blinddarm (Caecum)	\N
1515	82	2	11160	45	Enddarm (Rectum)	\N
1516	82	2	11162	46	After (Anus)	\N
1517	82	2	11163	47	Gewebe, perianales	\N
1518	82	2	11164	48	Magendarmtrakt-Flüssigkeiten	\N
1519	82	2	11174	50	Harntrakt (Apparatus urogenitalis)	\N
1520	82	2	11179	51	Harnleiter (Ureter)	\N
1521	82	2	11180	52	Harnblase (Vesica urenaria)	\N
1522	82	2	11181	53	Harnröhre (Urethra)	\N
1523	82	2	11182	54	Glied männliches (Penis)	\N
1524	82	2	11184	55	Hoden (Testes)	\N
1525	82	2	11187	56	Harntraktflüssigkeiten	\N
1526	82	2	11196	57	Scham (Vulva)	\N
1527	82	2	11197	58	Scheide (Vagina)	\N
1528	82	2	11198	59	Gebärmutter (Uterus)	\N
1529	82	2	11200	60	Eierstock (Ovar)	\N
1530	82	2	11203	61	Nachgeburt (Placenta) (Secundina)	\N
1531	82	2	11204	62	Nabelschnur (Funiculus umbilicalis)	\N
1532	82	2	11205	63	Fötus (Embryo)	\N
1533	82	2	11208	65	Endokrine Drüsen	\N
1534	82	2	11215	66	Nebenniere (Glandula suprarenalis)	\N
1535	82	2	11218	70	Nervensystem (Systema nervosum)	\N
1536	82	2	11219	71	Gehirn (Encephalon) (Cerebrum)	\N
1537	82	2	11229	72	Cerebrospinale Flüssigkeit (Liquor cerebrospinalis)	\N
1538	82	2	11230	73	Auge (Oculus)	\N
1539	82	2	11238	74	Ohr (Auris)	\N
1540	82	2	11244	80	Haut (Cutis)	\N
1541	82	2	11249	81	Haar (Pilus)	\N
1542	82	2	11255	82	Nagel (Unguis)	\N
1543	82	2	11258	83	Unterhaut (Subcutis)	\N
1544	82	2	11259	84	Brustdrüse (Mamma)	\N
1545	82	2	11264	87	Lymphatisches System (Systema lymphatica)	\N
1546	82	2	11266	88	Hämatopoetisches System (Systema haematopoetica)	\N
1547	82	2	11267	89	Knochenmark (Medulla ossium)	\N
1548	82	2	11269	90	Milz (Lien) (Splen)	\N
1549	82	2	11273	91	Lymphknoten (Nodi lymphatici)	\N
1550	82	2	11283	92	Blut	\N
1551	82	2	11293	95	Hautflüssigkeiten	\N
1552	82	2	11297	96	Frauenmilch	\N
1553	83	2	11302	1	Knochen (Ossa)	\N
1554	83	2	11313	2	Gelenke (Juncturae)	\N
1555	83	2	11317	3	Muskeln (Musculi)	\N
1556	83	2	11320	4	Knorpel (Cartilagines)	\N
1557	83	2	11327	5	Schleimbeutel (Bursae)	\N
1558	83	2	11328	6	Sehnen (Tendines)	\N
1559	83	2	11329	7	Sehnenscheiden (Vaginae tendines)	\N
1560	83	2	11330	8	Bänder (Ligamenta)	\N
1561	83	2	11331	9	Bindegewebshüllen (Fasciae)	\N
1562	83	2	11332	10	Fettgewebe (Panniculus adiposus)	\N
1563	83	2	11333	11	Bindegewebe	\N
1564	83	2	11334	15	Respirationstrakt (Apparatus respiratorius)	\N
1565	83	2	11341	16	Lungen (Pulmines)	\N
1566	83	2	11345	20	Herz (Cor)	\N
1567	83	2	11350	21	Blutgefäße (Vasa)	\N
1568	83	2	11356	25	Verdauungssystem (Apparatus digestorius)	\N
1569	83	2	11362	26	Zähne	\N
1570	83	2	11367	27	Speicheldrüsen (Glandulae salivales)	\N
1571	83	2	11371	28	Leber (Hepar)	\N
1572	83	2	11377	29	Bauchspeicheldrüse (Pancreas)	\N
1573	83	2	11378	33	Rachen (Pharynx)	\N
1574	83	2	11379	34	Mandeln (Tonsillae)	\N
1575	83	2	11380	35	Speiseröhre (Oesophagus)	\N
1576	83	2	11381	36	Magen (Gaster)	\N
1577	83	2	11382	37	Netz (Omentum)	\N
1578	83	2	11383	38	Dünndarm (Intestinum tenue)	\N
1579	83	2	11384	39	Zwölffingerdarm (Duodenum)	\N
1580	83	2	11385	40	Leerdarm (Jejunum)	\N
1581	83	2	11386	41	Krummdarm (Ileum)	\N
1582	83	2	11387	42	Wurmfortsatz (Appendix)	\N
1583	83	2	11388	43	Dickdarm (Colon)	\N
1584	83	2	11389	44	Blinddarm (Caecum)	\N
1585	83	2	11390	45	Enddarm (Rectum)	\N
1586	83	2	11391	46	After (Anus)	\N
1587	83	2	11392	47	Gewebe, perianales	\N
1588	83	2	11393	50	Harntrakt (Apparatus urogenitalis)	\N
1589	83	2	11398	51	Harnleiter (Ureter)	\N
1590	83	2	11399	52	Harnblase (Vesica urenaria)	\N
1591	83	2	11400	53	Harnröhre (Urethra)	\N
1592	83	2	11401	54	Glied männliches (Penis)	\N
1593	83	2	11403	55	Hoden (Testes)	\N
1594	83	2	11406	57	Scham (Vulva)	\N
1595	83	2	11407	58	Scheide (Vagina)	\N
1596	83	2	11408	59	Gebärmutter (Uterus)	\N
1597	83	2	11410	60	Eierstock (Ovar)	\N
1598	83	2	11413	61	Nachgeburt (Placenta) (Secundina)	\N
1599	83	2	11414	62	Nabelschnur (Funiculus umbilicalis)	\N
1600	83	2	11415	63	Fötus (Embryo)	\N
1601	83	2	11416	65	Endokrine Drüsen	\N
1602	83	2	11423	66	Nebenniere (Glandula suprarenalis)	\N
1603	83	2	11426	70	Nervensystem (Systema nervosum)	\N
1604	83	2	11427	71	Gehirn (Encephalon) (Cerebrum)	\N
1605	83	2	11437	73	Auge (Oculus)	\N
1606	83	2	11443	74	Ohr (Auris)	\N
1607	83	2	11448	80	Haut (Cutis)	\N
1608	83	2	11456	81	Haar (Pilus)	\N
1609	83	2	11462	82	Nagel (Unguis)	\N
1610	83	2	11465	83	Unterhaut (Subcutis)	\N
1611	83	2	11466	84	Brustdrüse (Mamma)	\N
1612	83	2	11471	87	Lymphatisches System (Systema lymphatica)	\N
1613	83	2	11473	88	Hämatopoetisches System (Systema haematopoetica)	\N
1614	83	2	11474	89	Knochenmark (Medulla ossium)	\N
1615	83	2	11476	90	Milz (Lien) (Splen)	\N
1616	83	2	11480	91	Lymphknoten (Nodi lymphatici)	\N
1617	83	2	11490	92	Blut	\N
1618	84	2	11495	1	Knochen (Ossa)	\N
1619	84	2	11506	2	Gelenke (Juncturae)	\N
1620	84	2	11510	3	Muskeln (Musculi)	\N
1621	84	2	11513	4	Knorpel (Cartilagines)	\N
1622	84	2	11520	5	Schleimbeutel (Bursae)	\N
1623	84	2	11521	6	Sehnen (Tendines)	\N
1624	84	2	11522	7	Sehnenscheiden (Vaginae tendines)	\N
1625	84	2	11523	8	Bänder (Ligamenta)	\N
1626	84	2	11524	9	Bindegewebshüllen (Fasciae)	\N
1627	84	2	11525	10	Fettgewebe (Panniculus adiposus)	\N
1628	84	2	11526	11	Bindegewebe	\N
1629	84	2	11527	15	Respirationstrakt (Apparatus respiratorius)	\N
1630	84	2	11534	16	Lungen (Pulmines)	\N
1631	84	2	11538	20	Herz (Cor)	\N
1632	84	2	11543	21	Blutgefäße (Vasa)	\N
1633	84	2	11549	25	Verdauungssystem (Apparatus digestorius)	\N
1634	84	2	11555	26	Zähne	\N
1635	84	2	11560	27	Speicheldrüsen (Glandulae salivales)	\N
1636	84	2	11564	28	Leber (Hepar)	\N
1637	84	2	11570	29	Bauchspeicheldrüse (Pancreas)	\N
1638	84	2	11571	33	Rachen (Pharynx)	\N
1639	84	2	11572	34	Mandeln (Tonsillae)	\N
1640	84	2	11573	35	Speiseröhre (Oesophagus)	\N
1641	84	2	11574	36	Magen (Gaster)	\N
1642	84	2	11575	37	Netz (Omentum)	\N
1643	84	2	11576	38	Dünndarm (Intestinum tenue)	\N
1644	84	2	11577	39	Zwölffingerdarm (Duodenum)	\N
1645	84	2	11578	40	Leerdarm (Jejunum)	\N
1646	84	2	11579	41	Krummdarm (Ileum)	\N
1647	84	2	11580	42	Wurmfortsatz (Appendix)	\N
1648	84	2	11581	43	Dickdarm (Colon)	\N
1649	84	2	11582	44	Blinddarm (Caecum)	\N
1650	84	2	11583	45	Enddarm (Rectum)	\N
1651	84	2	11584	46	After (Anus)	\N
1652	84	2	11585	47	Gewebe, perianales	\N
1653	84	2	11586	50	Harntrakt (Apparatus urogenitalis)	\N
1654	84	2	11591	51	Harnleiter (Ureter)	\N
1655	84	2	11592	52	Harnblase (Vesica urenaria)	\N
1656	84	2	11593	53	Harnröhre (Urethra)	\N
1657	84	2	11594	54	Glied männliches (Penis)	\N
1658	84	2	11596	55	Hoden (Testes)	\N
1659	84	2	11599	57	Scham (Vulva)	\N
1660	84	2	11600	58	Scheide (Vagina)	\N
1661	84	2	11601	59	Gebärmutter (Uterus)	\N
1662	84	2	11603	60	Eierstock (Ovar)	\N
1663	84	2	11605	61	Nachgeburt (Placenta) (Secundina)	\N
1664	84	2	11606	62	Nabelschnur (Funiculus umbilicalis)	\N
1665	84	2	11607	63	Fötus (Embryo)	\N
1666	84	2	11608	65	Endokrine Drüsen	\N
1667	84	2	11615	66	Nebenniere (Glandula suprarenalis)	\N
1668	84	2	11618	70	Nervensystem (Systema nervosum)	\N
1669	84	2	11619	71	Gehirn (Encephalon) (Cerebrum)	\N
1670	84	2	11629	73	Auge (Oculus)	\N
1671	84	2	11635	74	Ohr (Auris)	\N
1672	84	2	11640	80	Haut (Cutis)	\N
1673	84	2	11644	81	Haar (Pilus)	\N
1674	84	2	11650	82	Nagel (Unguis)	\N
1675	84	2	11653	83	Unterhaut (Subcutis)	\N
1676	84	2	11654	84	Brustdrüse (Mamma)	\N
1677	84	2	11659	87	Lymphatisches System (Systema lymphatica)	\N
1678	84	2	11661	88	Hämatopoetisches System (Systema haematopoetica)	\N
1679	84	2	11662	89	Knochenmark (Medulla ossium)	\N
1680	84	2	11664	90	Milz (Lien) (Splen)	\N
1681	84	2	11668	91	Lymphknoten (Nodi lymphatici)	\N
1682	84	2	11678	92	Blut	\N
1683	87	2	11685	0	Sorte	\N
1684	87	2	11686	1	Gras, grün, 1.Schnitt	\N
1811	90	2	11814	48	Senf	\N
1685	87	2	11687	2	Gras, grün, 2.Schnitt	\N
1686	87	2	11688	3	Gras, grün, 3.Schnitt	\N
1687	87	2	11689	5	Weidegras	\N
1688	87	2	11690	10	Grüngetreide	\N
1689	87	2	11691	12	Grüngerste	\N
1690	87	2	11692	14	Grünhafer	\N
1691	87	2	11693	16	Grünroggen	\N
1692	87	2	11694	19	Mais  erntereif, ganze Pflanze	\N
1693	87	2	11695	20	Grünmais	\N
1694	87	2	11696	21	Restpflanze	\N
1695	87	2	11697	23	Hirse	\N
1696	87	2	11698	24	Sonnenblume	\N
1697	87	2	11699	25	Zwischenfrucht allgem.	\N
1698	87	2	11700	26	Grassaatmischung	\N
1699	87	2	11701	30	Hülsenfrucht	\N
1700	87	2	11702	32	Wicke	\N
1701	87	2	11703	33	Erbs-Wicke	\N
1702	87	2	11704	34	Futtererbsen	\N
1703	87	2	11705	36	Futtermischung	\N
1704	87	2	11706	37	Wickroggen	\N
1705	87	2	11707	40	Kreuzblütler	\N
1706	87	2	11708	42	Raps	\N
1707	87	2	11709	43	Perko	\N
1708	87	2	11710	44	Rübe	\N
1709	87	2	11711	46	Senf	\N
1710	87	2	11712	47	Futterkohl	\N
1711	87	2	11713	50	Zuckerrübenblatt	\N
1712	87	2	11714	55	Futterrübenblatt	\N
1713	87	2	11715	60	Klee, grün	\N
1714	87	2	11716	61	Rotklee, 1.Schnitt	\N
1715	87	2	11717	62	Rotklee, 2.Schnitt	\N
1716	87	2	11718	63	Kleegras, 1.Schnitt	\N
1717	87	2	11719	64	Kleegras, 2.Schnitt	\N
1718	87	2	11720	68	Perserklee	\N
1719	87	2	11721	70	Luzerne, grün	\N
1720	87	2	11722	71	Luzerne, 1.Schnitt	\N
1721	87	2	11723	72	Luzerne, 2.Schnitt	\N
1722	87	2	11724	73	Luzernegras, 1.Schnitt	\N
1723	87	2	11725	74	Luzernegras, 2.Schnitt	\N
1724	87	2	11726	80	Grasarten grün allgem.	\N
1725	87	2	11727	81	Weidelgras	\N
1726	87	2	11728	86	Landsberger Gemenge	\N
1727	88	2	11685	0	Sorte	\N
1728	88	2	11730	1	Anwelksilage 1.Schnitt	\N
1729	88	2	11731	2	Anwelksilage 2.Schnitt	\N
1730	88	2	11732	3	Nasssilage 1.Schnitt	\N
1731	88	2	11733	4	Nasssilage 2.Schnitt	\N
1732	88	2	11734	12	Gerstensilage	\N
1733	88	2	11735	13	GPS	Gerste/Weizen Ganzpflanzensilage
1734	88	2	11736	14	Hafersilage	\N
1735	88	2	11737	16	Roggensilage	\N
1736	88	2	11738	20	Maissilage	\N
1737	88	2	11739	23	LKS	\N
1738	88	2	11740	30	Hülsenfr.silage	\N
1739	88	2	11741	34	Futtererbsensilage	\N
1740	88	2	11742	36	Futtermischungsilage	\N
1741	88	2	11743	40	Zwischenfruchtsilage	\N
1742	88	2	11744	42	Rapssilage	\N
1743	88	2	11745	43	Perkosilage	\N
1744	88	2	11746	44	Rübensilage	\N
1745	88	2	11747	48	Markstammkohl	\N
1746	88	2	11748	50	Zuckerrübenblattsilage	\N
1747	88	2	11749	55	Futterrübenblattsilage	\N
1748	88	2	11750	60	Kleesilage	\N
1749	88	2	11751	61	Rotkleesilage, 1.Schnitt	\N
1750	88	2	11752	63	Kleegrassilage, 1.Schnitt	\N
1751	88	2	11753	68	Perserkleesilage, 1.Schnitt	\N
1752	88	2	11754	70	Luzernesilage	\N
1753	88	2	11755	71	Luzernesilage, 1.Schnitt	\N
1754	88	2	11756	73	Luzernegrassilage, 1.Schnitt	\N
1755	88	2	11757	74	Luzernegrassilage, 2.Schnitt	\N
1756	88	2	11758	80	Grasartensilage	\N
1757	88	2	11759	81	Weidegrassilage	\N
1758	88	2	11760	86	Landsberger Gemenge Silage	\N
1759	89	2	11685	0	Sorte	\N
1760	89	2	11762	1	Heu, 1.Schnitt	\N
1761	89	2	11763	2	Heu, 2.Schnitt	\N
1762	89	2	11764	3	UDT-Heu, 1.Schnitt	\N
1763	89	2	11765	4	UDT-Heu, 2.Schnitt	\N
1764	89	2	11766	7	Grascobs, 1.Schnitt	\N
1765	89	2	11767	8	Grascobs, 2.Schnitt	\N
1766	89	2	11768	9	Grasmehl	\N
1767	89	2	11769	10	Stroh	\N
1768	89	2	11770	12	Gerstenstroh	\N
1769	89	2	11771	13	Gerstenstroh aufgeschlossen	\N
1770	89	2	11772	14	Haferstroh	\N
1771	89	2	11773	18	Weizenstroh	\N
1772	89	2	11774	20	Maiscobs	\N
1773	89	2	11775	60	Kleeheu	\N
1774	89	2	11776	61	Rotkleeheu, 1.Schnitt	\N
1775	89	2	11777	62	Rotkleeheu, 2.Schnitt	\N
1776	89	2	11778	63	Kleegrasheu, 1.Schnitt	\N
1777	89	2	11779	64	Kleegrascobs	\N
1778	89	2	11780	67	Rotkleecobs	\N
1779	89	2	11781	70	Luzerneheu	\N
1780	89	2	11782	71	Luzerneheu, 1.Schnitt	\N
1781	89	2	11783	72	Luzerneheu, 2.Schnitt	\N
1782	89	2	11784	73	Luzernegrasheu, 1.Schnitt	\N
1783	89	2	11785	77	Luzernecobs	\N
1784	89	2	11786	78	Grüncobs	\N
1785	89	2	11787	79	Luzernegrünmehl	\N
1786	89	2	11788	80	Weidegrasheu	\N
1787	89	2	11789	86	Heu Landsberger Gemenge	\N
1788	90	2	11791	0	Art	\N
1789	90	2	11792	2	Gerste	\N
1790	90	2	11793	6	Hafer	\N
1791	90	2	11794	10	Nacktgetreide	\N
1792	90	2	11795	11	Roggen	\N
1793	90	2	11796	12	Triticale	\N
1794	90	2	11797	14	Weizen	\N
1795	90	2	11798	20	Maiskörner	\N
1796	90	2	11799	22	Kornspindelgemisch trocken	\N
1797	90	2	11800	28	Hirse, Milo	\N
1798	90	2	11801	30	Körnerleguminosen	\N
1799	90	2	11802	31	Ackerbohnen	\N
1800	90	2	11803	34	Erbsen	\N
1801	90	2	11804	35	Lupinen	\N
1802	90	2	11805	36	Mohn	\N
1803	90	2	11806	40	Ölfrucht	\N
1804	90	2	11807	41	Sojabohne	siehe auch ZEBS
1805	90	2	11808	42	Winterrübe	\N
1806	90	2	11809	43	Sommerrübe	\N
1807	90	2	11810	44	Winterraps	\N
1808	90	2	11811	45	Sommerraps	\N
1809	90	2	11812	46	Sonnenblume	\N
1810	90	2	11813	47	Ölrettich	\N
1812	90	2	11815	49	Leinsamen	\N
1813	90	2	11816	50	Rüben	\N
1814	90	2	11817	51	Zuckerrüben Vollschnitzel	\N
1815	90	2	11818	53	Futterzucker	\N
1816	90	2	11819	60	Kartoffel	Futterkartoffel
1817	90	2	11820	61	Kartoffelschrot	\N
1818	90	2	11821	62	Kartoffelflocken	\N
1819	90	2	11822	63	Kartoffelstärke	\N
1820	90	2	11823	70	Milch, Hefen	\N
1821	90	2	11824	71	Vollmilchpulver	\N
1822	90	2	11825	75	Bakterieneiweiß Milchbasis	\N
1823	90	2	11826	80	Tierkörper	\N
1824	90	2	11827	81	Tiermehl	\N
1825	90	2	11828	83	Blutmehl	\N
1826	90	2	11829	84	Fischmehl	\N
1827	90	2	11830	90	Mineralkomponente	\N
1828	90	2	11831	91	Dikalziumphosphat	\N
1829	90	2	11832	92	Kohlensaurer Kalk	\N
1830	90	2	11833	94	Viehsalz	\N
1831	90	2	11834	95	Gerstenstaub	\N
1832	91	2	11791	0	Art	\N
1833	91	2	11836	1	Feuchtgetreide	\N
1834	91	2	11837	18	Altbrot	\N
1835	91	2	11838	20	Körnermaissilage	\N
1836	91	2	11839	22	Kornspindelgemisch siliert	\N
1837	91	2	11840	23	Maiskolbensilage	\N
1838	91	2	11841	24	Lieschkolbensilage gesiebt	\N
1839	91	2	11842	40	Ölerzeugnis	\N
1840	91	2	13542	41	Palmöl/fett	\N
1841	91	2	11843	43	Sojaöl	\N
1842	91	2	11844	46	Wicke	\N
1843	91	2	11845	50	Zuckerrübe	\N
1844	91	2	13539	51	Futtermöhre	\N
1845	91	2	11846	55	Gehaltsfutterrübe	\N
1846	91	2	11847	56	Massenfutterrübe	\N
1847	91	2	11848	57	Stoppelrübe	\N
1848	91	2	11849	58	Kohlrübe	\N
1849	91	2	11850	60	Kartoffel, roh	Futterkartoffel
1850	91	2	11851	62	Kartoffel, gedämpft	\N
1851	91	2	11852	63	Kartoffel, gedämpft siliert	\N
1852	91	2	11853	68	Apfel	siehe auch ZEBS
1853	91	2	11854	70	Vollmilch	siehe auch ZEBS
1854	91	2	11855	80	Tierfett	\N
1855	91	2	11856	81	Rindertalg	\N
1856	91	2	11857	90	Säure	\N
1857	91	2	11858	93	Propionsäure	\N
1858	92	2	11791	0	Art	\N
1859	92	2	11860	1	Von Spelzgetreide	\N
1860	92	2	11861	4	Biertreber trocken	\N
1861	92	2	11862	5	Bierhefe trocken	\N
1862	92	2	11863	6	Malzkeime	\N
1863	92	2	11864	7	Haferschotenkleie	\N
1864	92	2	11865	10	Mühlennachprodukte	\N
1865	92	2	11866	11	Roggennachmehl	\N
1866	92	2	11867	14	Weizennachmehl	\N
1867	92	2	11868	16	Weizengrieskleie	\N
1868	92	2	11869	18	Backabfall	\N
1869	92	2	11870	20	Maisnachprodukte	\N
1870	92	2	11871	21	Maiskleber	\N
1871	92	2	11872	26	Reisfuttermehl	\N
1872	92	2	11873	30	Maniok	\N
1873	92	2	11874	31	Tapioka	\N
1874	92	2	11875	40	Ölschrot (Ölkuchen)	\N
1875	92	2	11876	41	Leinschrot	\N
1876	92	2	11877	42	Rapsschrot	\N
1877	92	2	11878	43	Sojaschrot	\N
1878	92	2	11879	44	Palmkernschrot	\N
1879	92	2	11880	45	Kokosschrot	\N
1880	92	2	11881	46	Baumwollsaatschrot	\N
1881	92	2	11882	47	Erdnussschrot	\N
1882	92	2	11883	48	Sonnenblumenkernschrot	\N
1883	92	2	11884	50	Rübennebenerz.	\N
1884	92	2	11885	51	Trockenschnitzel	\N
1885	92	2	11886	52	Melasseschnitzel	\N
1886	92	2	11887	60	Kartoffelnebenerzeugnisse	\N
1887	92	2	11888	62	Kartoffeleiweiß	\N
1888	92	2	11889	63	Kartoffelpülpe trocken	\N
1889	92	2	11890	68	Citrustrester (Zitrustrester)	\N
1890	92	2	11891	69	Traubentrester	\N
1891	92	2	11892	70	Milchnebenerzeugnis	\N
1892	92	2	11893	71	Buttermilchpulver	\N
1893	92	2	11894	72	Magermilchpulver	\N
1894	92	2	11895	80	Tiernebenerzeugnis	\N
1895	92	2	11896	81	Fleischmehl	\N
1896	92	2	11897	82	Federmehl	\N
1897	92	2	11898	83	Knochenfuttermehl	\N
1898	92	2	11899	90	Aminosäuren	\N
1899	92	2	11900	91	HCl-Lysin	\N
1900	92	2	11901	92	Methionin	\N
1901	92	2	11902	93	Joghurtpulver	\N
1902	92	2	11903	94	Molkenpulver	\N
1903	93	2	11791	0	Art	\N
1904	93	2	11905	1	Brauereierzeugnisse	\N
1905	93	2	11906	2	Biertreber	\N
1906	93	2	11907	3	Bierhefe	\N
1907	93	2	11908	10	Getreideschlempe	\N
1908	93	2	11909	14	Weizenschlempe	\N
1909	93	2	11910	20	Maisschlempe	\N
1910	93	2	11911	50	Rübennebenerzeugnisse	\N
1911	93	2	11912	51	Nassschnitzel	\N
1912	93	2	11913	52	Pressschnitzel	\N
1913	93	2	11914	54	Melasse	\N
1914	93	2	11887	60	Kartoffelnebenerzeugnisse	\N
1915	93	2	11915	63	Kartoffelpülpe	\N
1916	93	2	11916	64	Kartoffelschlempe	\N
1917	93	2	11917	70	Milchnebenerzeugnisse	\N
1918	93	2	11918	71	Buttermilch.fl	\N
1919	93	2	11919	72	Magermilch	\N
1920	93	2	11920	73	Labmolke	\N
1921	93	2	11921	74	Sauermolke	\N
1922	93	2	11922	80	Abfall, feucht	\N
1923	93	2	11923	81	Schlachtabfall	\N
1924	93	2	11924	82	Küchenabfall	\N
1925	94	2	11791	0	Art	\N
1926	94	2	11926	1	Milchaustauscher Aufzucht	\N
1927	94	2	11927	3	Kälberaufzucht	\N
1928	94	2	11928	4	Rindermastfutter	\N
1929	94	2	11929	8	Rindermast, Mineralfutter	\N
1930	94	2	11930	10	MLF I 13 RP	\N
1931	94	2	11931	11	MLF I	\N
1932	94	2	11932	14	MLF IV	\N
1933	94	2	11933	18	Milchvieh Mineralfutter	\N
1934	94	2	11934	20	Ferkelmastfutter	\N
1935	94	2	11935	24	Ergänzungsfutter I	\N
1936	94	2	11936	25	Ergänzungsfutter II	\N
1937	94	2	11937	26	Eiweiß.r.erg.	\N
1938	94	2	11938	28	Mast, Mineralfutter	\N
1939	94	2	11939	30	Zuchtsauenfutter	\N
1940	102	2	11791	0	Art	\N
1941	102	2	11947	1	Rose	\N
1942	102	2	11948	2	Nelke	\N
1943	102	2	11949	3	Veilchen	\N
1944	103	2	11791	0	Art	\N
1945	103	2	11958	1	Eiche	\N
1946	103	2	11959	2	Buche	\N
1947	103	2	11960	3	Linde	\N
1948	103	2	11961	4	Ahorn	\N
1949	103	2	11962	5	Kastanie	\N
1950	103	2	11963	6	Birke	\N
1951	103	2	11964	7	Pappel	\N
1952	103	2	11965	8	Weide	\N
1953	103	2	11966	9	Erle	\N
1954	103	2	13515	10	Esche	\N
1955	103	2	11967	31	Apfel	\N
1956	103	2	11968	32	Birne	\N
1957	103	2	11969	33	Kirsche	\N
1958	103	2	11970	34	Pflaume	\N
1959	103	2	11971	61	Fichte	\N
1960	103	2	11972	62	Tanne	\N
1961	103	2	11973	63	Kiefer	\N
1962	103	2	11974	64	Lärche	\N
1963	104	2	11791	0	Art	\N
1964	104	2	11978	1	Johannisbeerstrauch	\N
1965	104	2	11979	2	Stachelbeerstrauch	\N
1966	104	2	11980	3	Himbeerstrauch	\N
1967	104	2	11981	5	Brombeerstrauch	\N
1968	105	2	11791	0	Art	\N
1969	105	2	11983	1	Dahlie	\N
1970	106	2	11791	0	Art	\N
1971	106	2	11985	1	Rasen/Gras o.F.	Wiesengras, ohne Futternutzung
1972	106	2	11986	2	Avenella (flex.)	Grasart
1973	106	2	11987	3	Calamagrostis (vill.)	Grasart
1974	106	2	11988	4	Klee o.F.	ohne Futternutzung
1975	106	2	13355	5	Gras/Kräuter (REI)	\N
1976	106	2	13538	20	Hirschtrüffel	Wildschweinkontamination
1977	106	2	11989	30	Moos	\N
1978	106	2	11990	31	Hypnum	Moosart
1979	106	2	11991	40	Farn	\N
1980	106	2	11992	41	Blechnum	Farnart
1981	106	2	11993	50	Flechte	\N
1982	106	2	11994	60	Heide	\N
1983	106	2	11995	61	Calma	Heideart
1984	108	2	11791	0	Art	\N
1985	108	2	11999	10	Sand	\N
1986	108	2	12000	12	Sand, schluffig	\N
1987	108	2	12001	13	Sand, anlehmig	\N
1988	108	2	12002	14	Sand, lehmig	\N
1989	108	2	12003	16	Sand, tonig	\N
1990	108	2	12004	20	Schluff	\N
1991	108	2	12005	21	Schluff, sandig	\N
1992	108	2	12006	24	Schluff, lehmig	\N
1993	108	2	12007	26	Schluff, tonig	\N
1994	108	2	12008	40	Lehm	\N
1995	108	2	12009	41	Lehm, sandig	\N
1996	108	2	12010	42	Lehm, schluffig	\N
1997	108	2	12011	46	Lehm, tonig	\N
1998	108	2	12012	60	Ton	\N
1999	108	2	12013	61	Ton, sandig	\N
2000	108	2	12014	62	Ton, schluffig	\N
2001	108	2	12015	64	Ton, lehmig	\N
2002	108	2	12016	70	Kies	\N
2003	108	2	12017	71	Steine + Fels	\N
2004	108	2	12018	80	Torf	\N
2005	108	2	12019	90	Kompost	\N
2006	108	2	12020	96	Nadelstreu	\N
2007	108	2	12021	97	Laubstreu	\N
2008	108	2	12022	98	organische Auflage	bei Waldboden
2009	108	2	12023	99	mineralischer Unterboden	bei Waldboden
2010	109	2	11791	0	Art	\N
2011	109	2	11999	10	Sand	\N
2012	109	2	12000	12	Sand, schluffig	\N
2013	109	2	12001	13	Sand, anlehmig	\N
2014	109	2	12002	14	Sand, lehmig	\N
2015	109	2	12003	16	Sand, tonig	\N
2016	109	2	12004	20	Schluff	\N
2017	109	2	12005	21	Schluff, sandig	\N
2018	109	2	12006	24	Schluff, lehmig	\N
2019	109	2	12007	26	Schluff, tonig	\N
2020	109	2	12008	40	Lehm	\N
2021	109	2	12009	41	Lehm, sandig	\N
2022	109	2	12010	42	Lehm, schluffig	\N
2023	109	2	12011	46	Lehm, tonig	\N
2024	109	2	12012	60	Ton	\N
2025	109	2	12013	61	Ton, sandig	\N
2026	109	2	12014	62	Ton, schluffig	\N
2027	109	2	12015	64	Ton, lehmig	\N
2028	109	2	12016	70	Kies	\N
2029	109	2	12017	71	Steine + Fels	\N
2030	109	2	12018	80	Torf	\N
2031	109	2	12019	90	Kompost	\N
2032	109	2	12020	96	Nadelstreu	\N
2033	109	2	12021	97	Laubstreu	\N
2034	109	2	12022	98	organische Auflage	bei Waldboden
2035	109	2	12023	99	mineralischer Unterboden	bei Waldboden
2036	110	2	11791	0	Art	\N
2037	110	2	12355	1	Sand	z.B. Sandkasten-Sand
2038	110	2	12356	2	Kies	z.B. Kiesweg
2039	110	2	12357	3	Rasen/Gras	ohne Futternutzung wegen S0 = 02 Futtermittel
2040	110	2	12358	4	Asphalt	\N
2041	110	2	12359	5	Beton	\N
2042	110	2	12360	6	Steinplatte	\N
2043	110	2	12361	7	Holz, roh	\N
2044	110	2	12362	8	Holz, imprägniert	z.B. Spielplatzeinfassungen
2045	110	2	12363	9	Holz, lackiert	z.B. Anlagenbank
2046	110	2	12364	10	Kehrgut	\N
2047	110	2	12365	11	Tartan	\N
2048	110	2	12366	12	Spielgerät	\N
2049	110	2	13540	13	Vaselineplatte	\N
2050	110	2	13541	14	Klebefolie	\N
2051	111	2	12368	0	Bodenart	\N
2052	111	2	12355	1	Sand	z.B. Sandkasten-Sand
2053	111	2	12369	2	Kies, Schotter	\N
2054	111	2	12370	3	Rasen, Gras	\N
2055	111	2	12358	4	Asphalt	\N
2056	111	2	12359	5	Beton	\N
2057	111	2	12371	6	Waldboden	\N
2058	111	2	12372	7	Weideboden	\N
2059	111	2	12373	8	Brachland	\N
2060	111	2	12374	99	Sonstiges	\N
2061	116	2	12378	0	Typ	\N
2062	116	2	12379	1	Kalkammonsalpeter 28,0	\N
2063	116	2	12380	2	Stickstoffmagnesia 20+7+0,2	\N
2064	116	2	12381	3	Ammonsulfatsalpeter 26	\N
2065	116	2	12382	4	Ammonsulfatsalpeter m. Bor (0.2)	\N
2066	116	2	12383	5	Ammonsulfat 21 (Schws.Ammon.)	\N
2067	116	2	12384	6	Kalkstickstoff gemahlen 21	\N
2068	116	2	12385	7	Perlkalkstickstoff 20	\N
2069	116	2	12386	8	Harnstoff 46	\N
2070	116	2	12387	9	Ammonnitrat-Harnst.-Lösung 28	\N
2071	116	2	12388	10	Ammoniakgas 82	\N
2072	116	2	12389	21	Superphosphat gekörnt 18	\N
2073	116	2	12390	22	Triple-Superphosphat 46	\N
2074	116	2	12391	23	Thomasphosphat 15-45 Ca0	\N
2075	116	2	12392	24	Dolophos mit Ca0 15+40	\N
2076	116	2	12393	25	Novaphos 23	\N
2077	116	2	12394	26	Magnesium-Novaphos 17+7	\N
2078	116	2	12395	27	Carolon-Phosphat 26	\N
2079	116	2	12396	28	Hyperphosphat gemahlen 32	\N
2080	116	2	12397	29	Hyperphosphat gekörnt 27	\N
2081	116	2	12398	30	Hyperphos-Magnesia 21+7	\N
2082	116	2	12399	31	40er Kali Standard 40	\N
2083	116	2	12400	32	50er Kali Standard 50	\N
2084	116	2	12401	33	50er Kali grob 50	\N
2085	116	2	12402	34	Kornkali mit Mg0 40+5	\N
2086	116	2	12403	35	Kalimagnesia grob 30+10	\N
2087	116	2	12404	36	Kaliumsulfat 50	\N
2088	116	2	12405	37	Magnesia-Kainit grob 12+6	\N
2089	116	2	12406	38	Kieserit	\N
2090	116	2	12407	39	NPK-Dünger 5+10+16	\N
2091	116	2	12408	40	NPK-Dünger + Mg0 6+10+18+2	\N
2092	116	2	12409	41	NPK-Dünger 6+12+18	\N
2093	116	2	12410	42	NPK-Dünger 10+8+18	\N
2094	116	2	12411	43	NPK-Dünger 10+15+20	\N
2095	116	2	12412	44	NPK-Dünger + Mg0 10+15+20+2	\N
2096	116	2	12413	45	NPK-Dünger + Mg0 12+12+17+2	\N
2097	116	2	12414	46	NPK-Dünger + Mg0 12+12+17+2 Ks	\N
2098	116	2	12415	47	NPK-Dünger 13+13+21	\N
2099	116	2	12416	48	Bor NPK-Dünger 13+13+21+0,1	\N
2100	116	2	12417	49	NPK-Dünger + Mg0 15+5+16+4	\N
2101	116	2	12418	50	NPK-Dünger + Mg0 15+5+20+2	\N
2102	116	2	12419	51	NPK-Dünger + Mg0 15+9+15+2	\N
2103	116	2	12420	52	NPK-Dünger + Mg0 15+9+15+4	\N
2104	116	2	12421	53	NPK-Dünger 15+10+20	\N
2105	116	2	12422	54	NPK-Dünger 15+15+15	\N
2106	116	2	12423	55	NPK-Dünger 24+8+8	\N
2107	116	2	12424	56	NP-Lösung 10+34	\N
2108	116	2	12425	57	Mais-Standard 11+52	\N
2109	116	2	12426	58	Diammonphosphat 16+48	\N
2110	116	2	12427	59	Stickstoffphosphat 20+20	\N
2111	116	2	12428	60	Stickstoffphosphat 26+14	\N
2112	116	2	12429	61	Monoammonphosphat 11+55	\N
2113	116	2	12430	62	Thomasphosphatkali 8+15+5 Mg0	\N
2114	116	2	12431	63	Thomasphosphatkali 10+15	\N
2115	116	2	12432	64	Thomasphosphatkali 11+11+4 Mg0	\N
2116	116	2	12433	65	Thomaskali 10+20+3 Mg0	\N
2117	116	2	12434	66	Thomaskali 12+18+3 Mg0	\N
2118	116	2	12435	67	Magn. Phosphatkali 6+6+10 Mg0	\N
2119	116	2	12436	68	Magn. Phosphatkali 14+14+4	\N
2120	116	2	12437	69	Phosphatkali R 12+19+4 Mg0	\N
2121	116	2	12438	70	Phosphatkali R 12+24	\N
2122	116	2	12439	71	Phosphatkali R 15+20	\N
2123	116	2	12440	72	Phosphatkali R 16.16	\N
2124	116	2	12441	73	Phosphatkali R 18+10	\N
2125	116	2	12442	74	PEKA-Phosphatkali 20+30	\N
2126	116	2	12443	75	Palatia PK 14+24	\N
2127	116	2	12444	76	Palatia PK 16+20	\N
2128	116	2	12445	77	Palatia-Magnesium-PK 15+15+4	\N
2129	116	2	12446	78	Super-Rhe-Phos 9+25	\N
2130	116	2	12447	79	Super-Rhe-Ka-Phos + Mg 9+21+4	\N
2131	116	2	12448	80	Super-Rhe-Ka-Phos 14+24	\N
2132	116	2	12449	81	Super-Rhe-Ka-Phos + Mg 14+8+8	\N
2133	116	2	12450	82	Super-Rhe-Ka-Phos + Mg 15+15+4	\N
2134	116	2	12451	83	Super-Rhe-Ka-Phos 16+20	\N
2135	116	2	12452	84	Super-Rhe-Ka-Phos 18+24	\N
2136	116	2	12453	85	Hyperphos-Kali mit Mg0 14+18+5	\N
2137	116	2	12454	86	Hyperphos-Kali 16+26	\N
2138	116	2	12455	87	Hyperphos-Kali 20+20	\N
2139	116	2	12456	88	Hyperphos-Kali 23+12	\N
2140	116	2	12457	89	Carolon-Kali-Phosphat 14+24	\N
2141	116	2	12458	90	Carolon-Kali-Phosphat 18+18	\N
2142	116	2	12459	91	Magn. Carolon-Kali-Ph. 13+13+5	\N
2143	116	2	12460	92	Carolon-Kali-Phosphat 21+11	\N
2144	116	2	12461	93	Mineralsteinmehl	\N
2145	117	2	12463	0	Typ/Art	\N
2146	117	2	12464	1	Gülle	\N
2147	117	2	12465	2	Jauche	\N
2148	117	2	12466	3	Mist	\N
2149	117	2	12467	4	Kompost	\N
2150	117	2	12468	5	Guano	\N
2151	117	2	12469	6	Kompostbeschleuniger	\N
2152	117	2	12470	7	Rindenmulch	\N
2153	117	2	12471	8	Torf	\N
2154	117	2	12472	50	Zwischenfrüchte zum Unterpflügen	\N
2155	117	2	12473	51	Seradella	\N
2156	117	2	12474	52	Phacelia	\N
2157	117	2	12475	53	Lupine	\N
2158	117	2	12476	54	Esparsette	\N
2159	117	2	12477	55	Ölrettich	\N
2160	117	2	12478	56	Ackersenf	\N
2161	119	2	12486	0	Tiefenbereich	\N
2162	119	2	12487	1	Küstengewässer	\N
2163	119	2	12488	2	Schelfmeer	\N
2164	119	2	12489	3	Tiefsee	\N
2165	122	2	11791	0	Art	\N
2166	122	2	12557	1	Toniges Sediment	\N
2167	122	2	12558	2	Schluffiges Sediment	\N
2168	122	2	12559	3	Ton	\N
2169	122	2	12560	4	Schlick	\N
2170	122	2	12561	5	Schlick mit Sand	\N
2171	122	2	12562	6	Sand	\N
2172	122	2	12563	7	Sand mit Schill	\N
2173	122	2	12564	8	Schill	\N
2174	122	2	12565	9	Kies	\N
2175	122	2	12566	99	Sonstiges Sediment	\N
2176	124	2	11791	0	Art	\N
2177	124	2	12591	1	Kapillarwasser	\N
2178	124	2	12592	2	Lysimeterauslaufwasser	\N
2179	124	2	12593	3	Dränwasser	\N
2180	125	2	11791	0	Art	\N
2181	125	2	12622	1	Pot. natans	Schwimmblattpflanzen
2182	125	2	12623	2	Nuphar lutea	Teichrose
2183	125	2	12624	3	Lemna minor	\N
2184	125	2	12625	4	Fontinalis	\N
2185	125	2	12626	5	Pot. perfoliatus	\N
2186	125	2	12627	6	Pot. crispus	\N
2187	125	2	12628	7	Pot. pectinatus	\N
2188	125	2	12629	8	Ceratophyllum	Unterwasserpflanzen
2189	125	2	12630	9	Myriophyllum	\N
2190	125	2	12631	10	Elodea	\N
2191	125	2	12632	11	Ranunculus fluitans	\N
2192	125	2	12633	12	Callitriche	\N
2193	125	2	12634	13	Mougeotia	Jochalgen
2194	125	2	12635	14	Spirogyra	\N
2195	125	2	12636	15	Cladophora	\N
2196	125	2	12637	16	Ulothrix	Grünalgen
2197	125	2	12638	17	Oedogonium	\N
2198	125	2	12639	18	Phragmites	Schilfrohr
2199	125	2	12640	19	Scirpus	Binsen
2200	125	2	12641	20	Nymphea	Seerose
2201	125	2	12642	21	Hydrokanis	Froschbiss
2202	125	2	12643	22	Ulva	Salzwasser-Algen: Grünalgen
2203	125	2	12644	23	Monostroma	\N
2204	125	2	12645	24	Enteromorpha	\N
2205	125	2	12646	25	Bryopsis	\N
2206	125	2	12647	26	Cladophora	\N
2207	125	2	12648	27	Ulothrix	\N
2208	125	2	12649	28	Codium	\N
2209	125	2	12650	29	Acrosiphonia	\N
2210	125	2	12651	30	Derbesia	\N
2211	125	2	12652	31	Acetabularia	\N
2212	125	2	12653	32	Fucus	Salzwasser-Algen: Braunalgen
2213	125	2	12654	33	Laminaria	\N
2214	125	2	12655	34	Scytosiphon	\N
2215	125	2	12656	35	Ectocarpus	\N
2216	125	2	12657	36	Petalonia	\N
2217	125	2	12658	37	Chordaria	\N
2218	125	2	12659	38	Dictyota	\N
2219	125	2	12660	39	Ilea	\N
2220	125	2	12661	40	Porphyra	Salzwasser-Algen: Rotalgen
2221	125	2	12662	41	Ceramium	\N
2222	125	2	12663	42	Callithamnion	\N
2223	125	2	12664	43	Polysiphonia	\N
2224	125	2	12665	44	Delesseria	\N
2225	125	2	12666	45	Phycodris	\N
2226	125	2	12667	46	Zostera	Salzwasser-Algen: Seegräser
2227	125	2	12668	47	Ruppia	Salzwasser-Algen: Brackwasser-Algen
2228	125	2	12669	48	Zannichellia	\N
2229	125	2	12670	49	Potamogeton	\N
2230	125	2	12671	50	Najas	\N
2231	132	2	12680	0	Filtertyp	\N
2232	132	2	12681	1	Aktivkohlefilter	\N
2233	132	2	12682	2	Schwebstoffilter	\N
2234	132	2	12683	3	Schwebstoff/Kohlefilter	\N
2235	132	2	12684	4	Schwebstoff/Aerosolfilter	\N
2236	132	2	12685	5	Molekularsiebfilter	\N
2237	132	2	12686	6	Aktivkohleband	\N
2238	133	2	11791	0	Art	\N
2239	133	2	12704	1	Regen	Washout
2240	133	2	12705	2	Schnee	\N
2241	133	2	12706	3	Hagel	\N
2242	133	2	12707	4	Feststoffanteil	Fallout
2243	133	2	12708	5	Trockenablagerung	\N
2244	139	2	12718	0	Probeentnahmestelle	\N
2245	139	2	12719	1	Kanalisation	\N
2246	139	2	12720	2	Kläranlagen-Zulauf	\N
2247	139	2	12721	3	Kläranlagen-Ablauf	Klarwasser
2248	139	2	12722	4	Regenrückhaltebecken, Staustufe	\N
2249	139	2	12723	5	Ablauf Rechenanlage, Sandfang	\N
2250	139	2	12724	6	Ablauf Vorklärung	\N
2251	140	2	11791	0	Art	\N
2252	140	2	12735	1	Rohschlamm	Unbehandelter Schlamm, Primärschlamm, Frischschlamm, Flüssigschlamm
2253	140	2	12736	2	Faulschlamm	anaerob stabilisierter Schlamm
2254	140	2	12737	3	Belebtschlamm	Sekundärschlamm, Überschussschlamm
2255	140	2	12738	4	Eingedickter, entwässerter Schlamm	Dickschlamm, zentrifugierter Schlamm, Zentrifugenkuchen, Filterkuchen, u.ä.
2256	140	2	12739	5	Rechengut	\N
2257	140	2	12740	6	Sandfanggut	\N
2258	140	2	12741	7	Schlamm aus Regenrückhaltebecken, Staustufe	\N
2259	140	2	12742	8	Sinkkastenschlamm	Gullyschlamm
2260	140	2	12743	9	Schlamm aus Kleinkläranlagen	siehe ATV-Arbeitsblatt 123
2261	140	2	12744	10	Aerob stabilisierter Schlamm	\N
2262	140	2	12745	11	Schlamm aus der Kanalisation	\N
2263	140	2	12746	12	Fäkalien, Fäkalschlamm aus Senkgruben u. Hauskläranlagen	\N
2264	140	2	12747	13	Tropfkörperschlamm	\N
2265	140	2	12748	14	Schlamm aus weit. Reinigungsstufen	Tertiärschlamm, Nitrifikation, Denitrifikation, Entphosphatisierung und andere Fällungs-, Flockungs- und Flotationsverfahren, usw.
2266	142	2	11791	0	Art	\N
2267	142	2	12761	1	Kesselasche	\N
2268	142	2	12762	2	Elektro-Filterstaub	\N
2269	142	2	12763	3	Gewebefilterstaub	\N
2270	142	2	12764	4	Zyklonstaub	\N
2271	142	2	12765	5	Wäscherschlamm	\N
2272	142	2	12766	6	Roh-Schlacke mit Filterstaub	\N
2273	142	2	12767	7	Roh-Schlacke ohne Filterstaub	\N
2274	142	2	12768	8	Schwelkoks	\N
2275	142	2	12769	9	Trockensorption	\N
2276	142	2	12770	10	Rohgas	\N
2277	142	2	12771	11	Reingas	\N
2278	142	2	12773	13	Ablaufwasser	Sulfatfällung
2279	142	2	12774	14	Waschwasser	\N
2280	142	2	12775	15	Entschlackerwasser	\N
2281	142	2	12776	16	Rückstände aus Schadgasreinigung	Filterstäube u.Absorptionsprodukte
2282	142	2	12777	17	Schlamm aus Schwermetallfällung	\N
2283	142	2	12778	18	Ablaufwasser	Schwermetallfällung
2284	142	2	12779	19	Schlamm aus Sulfatfällung (Gips)	\N
2285	142	2	12780	24	Elektrofilterstaub m. Kesselasche	\N
2286	142	2	12781	25	Elektrofilterstaub mit Trockensorptionsprodukten	\N
2287	142	2	12782	26	Elektrofilterstaub mit Wäscherrückstand	\N
2288	142	2	13546	98	Sonstige flüssige Reststoffe	\N
2289	142	2	12374	99	Sonstiges	\N
2290	143	2	11791	0	Art	\N
2291	143	2	12800	1	Hausmüll und hausmüllähnliche Gewerbeabfälle u. Sperrmüll	\N
2292	143	2	12801	2	Hausmüll und Reststoffe	Reststoffe aus Hausmüllverbrennung
2293	143	2	12802	3	Reststoffe	außer Aschen, Schlacken und Stäuben aus der Müll- und Klärschlammverbrennung
2294	143	2	12803	4	Bauschutt, Straßenaufbruch	\N
2295	143	2	12804	5	Klärschlamm aus kommunalen Anlagen	\N
2296	143	2	12805	6	Sondermüll	\N
2297	143	2	12806	7	Firmeneigene Reststoffe	Firmeneigene Reststoffdeponie
2298	143	2	12807	8	Sickerwasser	\N
2299	143	2	12808	9	Rückstände aus Klärschlammverbrennung	Aschen, Schlacken, Stäuben
2300	143	2	12809	10	Rückstände aus Müllverbrennung	Aschen, Schlacken, Stäuben
2301	143	2	12810	11	Bodenaushub	\N
2302	143	2	12811	12	Grundwasser	\N
2303	143	2	12812	13	Krankenhausabfälle, Abfälle aus Arztpraxen	\N
2304	143	2	12813	14	Straßenkehricht	\N
2305	143	2	12814	15	Kompost	\N
2306	143	2	12815	16	Obst-, Gemüse-, Marktabfälle	\N
2307	143	2	12816	17	Schlachtabfälle	\N
2308	144	2	12835	0	Einsatzgut	\N
2309	144	2	12836	1	Klärschlamm	\N
2310	144	2	12837	2	Gartenabfälle	\N
2311	144	2	12838	3	Organischer Hausmüll	organische Haushaltsabfälle, Biotonne
2312	144	2	12839	4	Gartenabfälle u. org. Hausmüll	\N
2313	144	2	12840	5	Nassmüll	\N
2314	144	2	12841	6	Straßenlaub, Straßenkehricht	\N
2315	144	2	12842	7	Obst-, Gemüse-, Marktabfälle	\N
2316	144	2	12374	99	Sonstiges	\N
2317	145	2	11791	0	Art	\N
2318	145	2	12859	1	Filter	\N
2319	145	2	12860	2	Schutzkleidung	\N
2320	145	2	12861	3	Ionentauscher	\N
2321	145	2	12862	4	Folien	\N
2322	145	2	12863	5	Tierkadaver	\N
2323	145	2	12864	6	Schlachthausabfälle	\N
2324	145	2	12865	7	Kontaminierte Nahrungsmittel	\N
2325	145	2	12866	8	Kontaminierte Futtermittel	\N
2326	145	2	12867	9	Straßenkehricht	\N
2327	145	2	12868	10	Kontaminiertes Laub	\N
2328	145	2	12869	11	Staub	\N
2329	145	2	12870	12	Rohrablagerungen	\N
2330	145	2	12871	13	Gartenabfälle	\N
2331	145	2	12872	99	sonstige Reststoffe und Abfälle	\N
2332	146	2	12378	0	Typ	\N
2333	146	2	12874	1	Zuluftfilter	Klimaanlage, Kraftfahrzeug
2334	146	2	12875	2	Abluftfilter	Klimaanlage
2335	146	2	12876	3	Atemschutzfilter	\N
2336	148	2	11791	0	Art	\N
2337	148	2	12886	1	Ionentauscher, Anionen	\N
2338	148	2	12887	2	Ionentauscher, Kationen	\N
2339	148	2	12888	3	Mischbettionentauscher	\N
2340	150	2	11791	0	Art	\N
2341	150	2	12891	1	Filterrückspülschlamm	\N
2342	150	2	12892	2	Filterkies	\N
2343	150	2	12893	3	Aktivkohle	\N
2344	152	2	11791	0	Art	\N
2345	152	2	12914	1	Bauxit	\N
2346	152	2	12915	2	Rotschlamm	\N
2347	152	2	12559	3	Ton	\N
2348	152	2	12916	4	Lehm	\N
2349	152	2	12917	9	Sonst. Rohstoffe	\N
2350	152	2	12918	10	Zuschläge, Zusätze allg.	\N
2351	152	2	12919	11	Natürlicher Sand	\N
2352	152	2	12920	12	Natürlicher Kies	\N
2353	152	2	12921	13	Blähton	\N
2354	152	2	12922	14	Blähschiefer	\N
2355	152	2	12923	15	Hochofenschlacke	\N
2356	152	2	12924	16	Flugasche	\N
2357	152	2	12925	20	Bindemittel allg.	\N
2358	152	2	12926	21	Portlandzement	\N
2359	152	2	12927	22	Hüttenzement	\N
2360	152	2	12928	23	Tonerdeschmelzzement	\N
2361	152	2	12929	24	Kalk	\N
2362	152	2	12930	25	Naturgips	\N
2363	152	2	12931	26	Chemiegips (Apatit)	\N
2364	152	2	12932	27	Chemiegips (Phosphorit)	\N
2365	152	2	12933	28	REA-Gips	\N
2366	152	2	12934	29	Fertigmörtel	\N
2367	152	2	12935	30	Fertigputz	\N
2368	152	2	12936	31	Bitumen, Teer	\N
2369	152	2	12937	40	Mauersteine	\N
2370	152	2	12938	41	Ziegel, herkömmliche Art ohne Zusätze	\N
2371	152	2	12939	42	Schamotte	\N
2372	152	2	12940	43	Beton- oder zementgebundene Steine mit Bims-Zuschlag	\N
2373	152	2	12941	44	Beton- oder zementgebundene Steine mit Ziegelsplitt-Zuschlag	\N
2374	152	2	12942	45	Beton- oder zementgebundene Steine mit Blähton-Zuschlag	\N
2375	152	2	12943	46	Beton- oder zementgebundene Steine mit Schlacke-Zuschlag	\N
2376	152	2	12944	47	Beton- oder zementgebundene Steine mit Holz-Zuschlag	\N
2377	152	2	12945	48	Beton- oder zementgebundene Steine mit natürlichem Zuschlag	\N
2378	152	2	12946	49	Kalksandsteine	\N
2379	152	2	12947	50	Gasbeton	\N
2380	152	2	12948	51	Asbestzement	\N
2381	152	2	12949	60	Natursteine allg.	\N
2382	152	2	12950	61	Granit	\N
2383	152	2	12951	62	Andere Erstarrungsgesteine	\N
2384	152	2	12952	63	Tuff	\N
2385	152	2	12953	64	Bims	\N
2386	152	2	12954	65	Schiefer	\N
2387	152	2	12955	66	Kalkstein, Marmor	\N
2388	152	2	12956	67	Sandstein	\N
2389	152	2	12957	68	Quarzit	\N
2390	153	2	11791	0	Art	\N
2391	153	2	12959	1	Einfamilienhaus	\N
2392	153	2	12960	2	Zweifamilienhaus	\N
2393	153	2	12961	3	Mehrfamilienhaus	\N
2394	153	2	12962	4	Hochhaus	\N
2395	153	2	12374	99	Sonstiges	\N
2396	154	2	11791	0	Art	\N
2397	154	2	12980	1	Naturholz	\N
2398	154	2	12981	51	verarbeitetes Holz	\N
2399	158	2	11791	0	Art	\N
2400	158	2	12986	1	Rötelmaus	\N
2401	158	2	12987	41	Froschlaich	\N
2402	158	2	12988	51	Amsel	\N
2403	158	2	12989	52	Lachmöve	\N
2404	158	2	12990	71	Carabiden	Insektenart
2405	158	2	12991	91	Weichschnecke	\N
2406	158	2	12992	92	Würmer	\N
2407	161	2	11791	0	Art	\N
2408	161	2	12355	1	Sand	z.B. Sandkasten-Sand
2409	161	2	12356	2	Kies	z.B. Kiesweg
2410	161	2	12357	3	Rasen/Gras	ohne Futternutzung wegen S0 = 02 Futtermittel
2411	161	2	12358	4	Asphalt	\N
2412	161	2	12359	5	Beton	\N
2413	161	2	12360	6	Steinplatte	\N
2414	161	2	12361	7	Holz, roh	\N
2415	161	2	12362	8	Holz, imprägniert	z.B. Spielplatzeinfassungen
2416	161	2	12363	9	Holz, lackiert	z.B. Anlagenbank
2417	161	2	12364	10	Kehrgut	\N
2418	161	2	12365	11	Tartan	\N
2419	161	2	12366	12	Spielgerät	\N
2420	161	2	13001	13	Erde	\N
2421	161	2	13002	14	Waldboden	\N
2422	161	2	12374	99	Sonstiges	\N
2423	163	2	11791	0	Art	\N
2424	163	2	12355	1	Sand	z.B. Sandkasten-Sand
2425	163	2	12356	2	Kies	z.B. Kiesweg
2426	163	2	12357	3	Rasen/Gras	ohne Futternutzung wegen S0 = 02 Futtermittel
2427	163	2	12358	4	Asphalt	\N
2428	163	2	12359	5	Beton	\N
2429	163	2	12360	6	Steinplatte	\N
2430	163	2	12361	7	Holz, roh	\N
2431	163	2	12362	8	Holz, imprägniert	z.B. Spielplatzeinfassungen
2432	163	2	12363	9	Holz, lackiert	z.B. Anlagenbank
2433	163	2	12364	10	Kehrgut	\N
2434	163	2	12365	11	Tartan	\N
2435	163	2	12366	12	Spielgerät	\N
2436	163	2	13001	13	Erde	\N
2437	163	2	13002	14	Waldboden	\N
2438	163	2	12374	99	Sonstiges	\N
2439	166	2	13032	0	Herkunft	\N
2440	166	2	13033	1	Blatt, Nadel	Folia
2441	166	2	13034	2	Blüte	Flores
2442	166	2	13035	3	Rinde	Cortex
2443	166	2	13036	4	Wurzel, Zwiebel	Radix, Rhizoma
2444	166	2	13037	5	Holz, Stengel	Lignum
2445	166	2	13038	6	Frucht, Samen, Pollen	Fructus, Semen
2446	166	2	13039	7	Harz, Säfte	Succus
2447	166	2	13040	8	oberirdisches Pflanzenteil	Herba
2448	166	2	13041	9	ganze Pflanze	\N
2449	166	2	13415	10	Knospe	\N
2450	166	2	13416	11	Algen	\N
2451	166	2	13435	12	Kern	\N
2452	167	2	13032	0	Herkunft	\N
2453	167	2	13141	1	Fett, Öl, Lebertran	\N
2454	167	2	13142	2	Wachs, Lanolin	\N
2455	167	2	13143	3	Serum, Blutbestandteil	\N
2456	167	2	13144	4	Lactose, sonstiges Milchprodukt	\N
2457	167	2	13145	5	Gelatine, Knorpel	\N
2458	167	2	13146	6	Protein	\N
2459	167	2	13147	7	sonstiges Extrakt, Ferment	\N
2460	167	2	13148	8	Einzelorgan und Gewebe	\N
2461	167	2	13149	9	Sonstiges	\N
2462	168	2	13032	0	Herkunft	\N
2463	168	2	13155	1	Moor, Huminstoff	\N
2464	168	2	13156	2	Heilerde, Kieselerde	\N
2465	168	2	13157	3	Teer, Öl	\N
2466	168	2	13158	4	Salz	\N
2467	168	2	12374	99	Sonstiges	\N
2468	170	2	12368	0	Bodenart	\N
2469	170	2	12355	1	Sand	z.B. Sandkasten-Sand
2470	170	2	12369	2	Kies, Schotter	\N
2471	170	2	12370	3	Rasen, Gras	\N
2472	170	2	12358	4	Asphalt	\N
2473	170	2	12359	5	Beton	\N
2474	170	2	12371	6	Waldboden	\N
2475	170	2	12372	7	Weideboden	\N
2476	170	2	12373	8	Brachland	\N
2477	170	2	12374	99	Sonstiges	\N
2478	171	2	12368	0	Bodenart	\N
2479	171	2	12355	1	Sand	z.B. Sandkasten-Sand
2480	171	2	12369	2	Kies, Schotter	\N
2481	171	2	12370	3	Rasen, Gras	\N
2482	171	2	12358	4	Asphalt	\N
2483	171	2	12359	5	Beton	\N
2484	171	2	12371	6	Waldboden	\N
2485	171	2	12372	7	Weideboden	\N
2486	171	2	12373	8	Brachland	\N
2487	171	2	12374	99	Sonstiges	\N
2488	179	2	13215	0	Art des Bodenschatzes	\N
2489	179	2	13173	1	Rohgas	\N
2490	179	2	13174	2	Reingas	\N
2491	155	2	11791	0	Art	\N
2492	155	2	13375	10	überwiegend Sand/Kies	\N
2493	155	2	13376	20	überwiegend Putz/Mörtel	\N
2494	155	2	13377	30	überwiegend Beton	\N
2495	155	2	13378	40	überwiegend Ziegelsteine	\N
2496	155	2	13379	41	überwiegend Dachziegel	\N
2497	155	2	13380	42	überwiegend Natur- und Verbundsteine	\N
2498	155	2	13381	43	überwiegend Kacheln/Fliesen	\N
2499	155	2	13382	50	überwiegend Sanitärkeramik	\N
2500	155	2	13383	60	überwiegend Glas	\N
2501	155	2	13384	70	überwiegend Metallschrott	\N
2502	155	2	13385	80	überwiegend Holz oder organische Stoffe	\N
2503	155	2	13386	90	überwiegend Dämmstoffe	\N
2504	155	2	13387	91	überwiegend Rigipsplatten	\N
2505	155	2	13388	92	überwiegend Kunststoffe	\N
2506	155	2	13389	99	sonstiger Bauschutt	\N
2507	85	2	13536	99	Kein Erzeugnis nach LMBG/LFGB	\N
2508	180	3	4	1	gestrichen jetzt 010106 oder 010107, Sammelmilch	\N
2509	180	3	5	2	gestrichen jetzt 010106 oder 010107, Milch ab Hof	\N
2510	180	3	6	3	Einzelgemelk/Rohmilch	\N
2511	180	3	7	4	Viertelgemelk/Rohmilch	\N
2512	180	3	8	5	Vorzugsmilch/Rohmilch	\N
2513	180	3	13698	6	Sammelmilch eines Einzelbetriebes/Rohmilch	\N
2514	180	3	13699	7	Sammelmilch mehrerer Erzeugerbetriebe/Rohmilch	\N
2515	181	3	10	1	Milch pasteurisiert entrahmt	\N
2516	181	3	11	2	Milch pasteurisiert teilentrahmt	\N
2517	181	3	12	3	Vollmilch pasteurisiert standardisiert	\N
2518	181	3	13	4	Vollmilch pasteurisiert nicht standardisiert	\N
2519	181	3	14	5	Vollmilch ultrahocherhitzt standardisiert	\N
2520	181	3	15	6	Vollmilch ultrahocherhitzt nicht standardisiert	\N
2521	181	3	16	7	Milch ultrahocherhitzt teilentrahmt	\N
2522	181	3	17	8	Milch ultrahocherhitzt entrahmt	\N
2523	181	3	18	9	Vollmilch sterilisiert standardisiert	\N
2524	181	3	19	10	Vollmilch sterilisiert nicht standardisiert	\N
2525	181	3	20	11	Milch sterilisiert teilentrahmt	\N
2526	181	3	21	12	Milch sterilisiert entrahmt	\N
2527	181	3	22	13	Milch gekocht	\N
2528	181	3	13241	20	Vollmilch hocherhitzt standardisiert	\N
2529	181	3	13240	21	Vollmilch hocherhitzt nicht standardisiert	\N
2530	181	3	13239	22	Milch hocherhitzt teilentrahmt	\N
2531	181	3	13238	23	Milch hocherhitzt entrahmt	\N
2532	182	3	24	1	Stutenmilch unbearbeitete/Rohmilch	\N
2533	182	3	25	2	Büffelmilch unbearbeitete/Rohmilch	\N
2534	182	3	26	3	Eselmilch unbearbeitete/Rohmilch	\N
2535	182	3	27	4	Ziegenmilch unbearbeitete/Rohmilch	\N
2536	182	3	28	5	Schafmilch unbearbeitete/Rohmilch	\N
2537	182	3	29	6	Kamelmilch unbearbeitete/Rohmilch	\N
2538	183	3	35	1	Stutenmilch bearbeitet	\N
2539	183	3	36	2	Büffelmilch bearbeitet	\N
2540	183	3	37	3	Eselmilch bearbeitet	\N
2541	183	3	38	4	Ziegenmilch bearbeitet	\N
2542	183	3	39	5	Schafmilch bearbeitet	\N
2543	183	3	40	6	Kamelmilch bearbeitet	\N
2544	185	3	79	1	Sauermilch	\N
2545	185	3	80	2	Sauermilch entrahmt	\N
2546	185	3	81	3	Sauermilch fettarm	\N
2547	185	3	82	4	Sauermilch dickgelegt	\N
2548	185	3	83	5	Sauermilch entrahmt dickgelegt	\N
2549	185	3	84	6	Sauermilch fettarm dickgelegt	\N
2550	185	3	85	7	Sahnedickmilch	\N
2551	185	3	86	8	Sahnesauermilch saure Sahne	\N
2552	185	3	87	9	Creme fraiche	\N
2553	185	3	88	10	Schmand	\N
2554	185	3	89	11	Streichrahm	\N
2555	186	3	91	1	Joghurt	\N
2556	186	3	92	2	Joghurt aus entrahmter Milch	\N
2557	186	3	93	3	Joghurt fettarm	\N
2558	186	3	94	4	Sahnejoghurt	\N
2559	186	3	95	5	Joghurt mild	\N
2560	186	3	96	6	Joghurt mild fettarm	\N
2561	186	3	97	7	Joghurt mild aus entrahmter Milch	\N
2562	186	3	98	8	Sahnejoghurt mild	\N
2563	187	3	100	1	Kefir	\N
2564	187	3	101	2	Kefir aus entrahmter Milch	\N
2565	187	3	102	3	Kefir fettarm	\N
2566	187	3	103	4	Sahnekefir	\N
2567	187	3	104	5	Kefir mild	\N
2568	187	3	105	6	Kefir mild fettarm	\N
2569	187	3	106	7	Kefir mild aus entrahmter Milch	\N
2570	187	3	107	8	Sahnekefir mild	\N
2571	188	3	109	1	Buttermilch	\N
2572	188	3	110	2	Buttermilch reine	\N
2573	189	3	112	1	Kaffeesahne	\N
2574	189	3	114	3	Schlagsahne	\N
2575	189	3	116	5	Schlagsahne ultrahocherhitzt	\N
2576	189	3	119	8	Schlagsahne sterilisiert	\N
2577	189	3	120	9	Sahne mit erhöhtem Fettgehalt	\N
2578	189	3	121	10	Schlagsahne wärmebehandelt	\N
2579	189	3	122	11	Sahne geschlagen ungezuckert	\N
2580	189	3	13242	12	Kaffeesahne wärmebehandelt ultrahocherhitzt sterilisiert	\N
2581	190	3	124	1	Kondensmilch 7,5%	\N
2582	190	3	125	2	Kondensmilch 10%	\N
2583	190	3	126	3	Kondenssahne 15%	\N
2584	190	3	128	5	Kondensmagermilch	\N
2585	190	3	129	6	Kondensmilch teilentrahmt	\N
2586	190	3	130	7	Kondensmilch gezuckert	\N
2587	190	3	131	8	Kondensmilch teilentrahmt gezuckert	\N
2588	190	3	132	9	Kondensmagermilch gezuckert	\N
2589	191	3	135	1	Sahnepulver	\N
2590	191	3	136	2	Vollmilchpulver	\N
2591	191	3	137	3	Magermilchpulver	\N
2592	191	3	138	4	Buttermilchpulver	\N
2593	191	3	139	5	Joghurtpulver	\N
2594	191	3	140	6	Milchpulver teilentrahmt	\N
2595	191	3	141	7	gestrichen jetzt 020830, Süßmolkenpulver	\N
2596	191	3	142	8	gestrichen jetzt 020831, Sauermolkenpulver	\N
2597	191	3	143	9	gestrichen jetzt 020832, Molkenpulver entsalzt	\N
2598	191	3	144	10	gestrichen jetzt 020833, Molkenpulver eiweißangereichert	\N
2599	191	3	147	13	Joghurtpulver mit hohem Fettgehalt	\N
2600	191	3	148	14	Joghurtpulver teilentrahmt	\N
2601	191	3	149	15	Magermilchjoghurtpulver	\N
2602	191	3	150	16	Kefirpulver	\N
2603	191	3	151	17	Kefirpulver mit hohem Fettgehalt	\N
2604	191	3	152	18	Kefirpulver teilentrahmt	\N
2605	191	3	153	19	Magermilchkefirpulver	\N
2606	192	3	155	1	Molkensahne	\N
2607	192	3	156	2	Süßmolke	\N
2608	192	3	157	3	Sauermolke	\N
2609	192	3	158	4	Molke mit and. beigeg. LM	\N
2610	192	3	159	5	Molkenmischerzeugnis aus Molkensahne	\N
2611	192	3	160	6	Molkenmischerzeugnis aus Molkensahne mit a. beigeg. LM	\N
2612	192	3	161	7	Molkenmischerz. aus Molkensahne mit Früchten/Fruchtzuber.	\N
2613	192	3	162	8	Molkenmischerzeugnis aus Molke	\N
2614	192	3	163	20	Molkenmischerzeugnis aus Molke mit a. beigeg. LM	\N
2615	192	3	164	21	Molkenmischerz. aus Molke mit Früchten/Fruchtzubereitung	\N
2616	192	3	165	30	Süßmolkenpulver	\N
2617	192	3	166	31	Sauermolkenpulver	\N
2618	192	3	167	32	Molkenpulver entsalzt	\N
2619	192	3	168	33	Molkenpulver eiweißangereichert	\N
2620	192	3	169	34	Süßmolkenpulver teilentzuckert	\N
2621	192	3	170	35	Sauermolkenpulver teilentzuckert	\N
2622	193	3	172	1	Milcheiweiß	\N
2623	193	3	173	2	Milcheiweiß wasserlöslich	\N
2624	193	3	174	3	Milcheiweiß aufgeschlossen	\N
2625	193	3	175	4	Labnährkasein	\N
2626	193	3	176	5	Labkasein	\N
2627	193	3	177	6	Säure-Nährkasein	\N
2628	193	3	178	7	Molkeneiweiß	\N
2629	194	3	180	1	Milchmischerz. aus Vollmilch	\N
2630	194	3	181	2	Milchmischerz. aus Vollmilch mit Fruchtzuber.	\N
2631	194	3	182	3	Milchmischerz. aus Vollmilch mit Kakao/Schokolade	\N
2632	194	3	183	4	Milchmischerz. aus Vollmilch mit Vanille/Nuss	\N
2633	194	3	184	5	Milchmischerz. aus teilentr. Milch	\N
2634	194	3	185	6	Milchmischerz. aus teilentr. Milch mit Fruchtzuber.	\N
2635	194	3	186	7	Milchmischerz. aus teilentr. Milch mit Kakao/Schokolade	\N
3778	334	3	1526	24	Herz Schwein gegart	\N
2636	194	3	187	8	Milchmischerz. aus teilentr. Milch mit Vanille/Nuss	\N
2637	194	3	188	9	Milchmischerz. aus entr. Milch	\N
2638	194	3	189	10	Milchmischerz. aus entr. Milch mit Fruchtzuber.	\N
2639	194	3	190	11	Milchmischerz. aus entr. Milch mit Kakao/Schokolade	\N
2640	194	3	191	12	Milchmischerz. aus entr. Milch mit Vanille/Nuss	\N
2641	194	3	193	14	Sauermilcherz. mit Fruchtzuber.	\N
2642	194	3	194	15	Sauermilcherz. mit Vanille/Nuss	\N
2643	194	3	196	17	Joghurterz. mit Fruchtzuber.	\N
2644	194	3	197	18	Joghurterz. mit Vanille/Nuss	\N
2645	194	3	199	20	Kefirerz. mit Fruchtzuber.	\N
2646	194	3	200	21	Kefirerz. mit Vanille/Nuss	\N
2647	194	3	202	23	Buttermilcherz. mit Fruchtzuber.	\N
2648	194	3	204	25	Sahneerz. mit Fruchtzuber.	\N
2649	194	3	205	26	Sahneerz. mit Vanille/Nuss	\N
2650	194	3	206	27	Sahneerz. mit Kakao/Schokolade	\N
2651	194	3	207	28	Sauermilch dickgelegt mit Früchten/Fruchtzuber.	\N
2652	194	3	208	29	Joghurt mit Früchten/Fruchtzuber.	\N
2653	194	3	209	30	Joghurt aus entr. Milch mit Früchten/Fruchtzuber.	\N
2654	194	3	210	31	Joghurt fettarm mit Früchten/Fruchtzuber.	\N
2655	194	3	211	32	Sahnejoghurt mit Früchten/Fruchtzuber.	\N
2656	194	3	212	33	Sahne geschlagen gezuckert u./o. m. anderen beigeg. LM	\N
2657	194	3	213	34	Blocksahne	\N
2658	194	3	215	36	Milchmischerz. aus Vollmilch mit a. beigeg. LM	\N
2659	194	3	216	37	Milchmischerz. aus Vollmilch m. Fruchtzuber. u.a. beigeg. LM	\N
2660	194	3	217	38	Milchmischerz. aus Vollmilch mit Kakao/Schokolade u.a. beigeg. LM	\N
2661	194	3	218	39	Milchmischerz. aus Vollmilch mit Vanille/Nuss u.a. beigeg. LM	\N
2662	194	3	219	40	Milchmischerz. aus teilentr. Milch mit a. beigeg. LM	\N
2663	194	3	220	41	Milchmischerz. aus teilentr. Milch mit Fruchtzuber. u.a. beigeg. LM	\N
2664	194	3	221	42	Milchmischerz. aus teilentr. Milch mit Kakao/Schokolade u.a. beigeg. LM	\N
2665	194	3	222	43	Milchmischerz. aus teilentr. Milch mit Vanille/Nuss u.a. beigeg. LM	\N
2666	194	3	223	44	Milchmischerz. aus entr. Milch mit a. beigeg. LM	\N
2667	194	3	224	45	Milchmischerz. aus entr. Milch mit Fruchtzuber. u.a. beigeg. LM	\N
2668	194	3	225	46	Milchmischerz. aus entr. Milch mit Kakao/Schokolade u.a. beigeg. LM	\N
2669	194	3	226	47	Milchmischerz. aus entr. Milch mit Vanille/Nuss u.a. beigeg. LM	\N
2670	194	3	227	48	Sauermilcherz. mit a. beigeg. LM	\N
2671	194	3	228	49	Sauermilcherz. mit Fruchtzuber. u.a. beigeg. LM	\N
2672	194	3	229	50	Sauermilcherz. mit Vanille/Nuss u.a. beigeg. LM	\N
2673	194	3	230	51	Joghurterz. mit a. beigeg. LM	\N
2674	194	3	231	52	Joghurterz. mit Fruchtzuber. u.a. beigeg. LM	\N
2675	194	3	232	53	Joghurterz. mit Vanille/Nuss u.a. beigeg. LM	\N
2676	194	3	233	54	Kefirerz. mit a. beigeg. LM	\N
2677	194	3	234	55	Kefirerz. mit Fruchtzuber. u.a. beigeg. LM	\N
2678	194	3	235	56	Kefirerz. mit Vanille/Nuss u.a. beigeg. LM	\N
2679	194	3	236	57	Buttermilcherz. mit a. beigeg. LM	\N
2680	194	3	237	58	Buttermilcherz. mit Fruchtzuber. u.a. beigeg. LM	\N
2681	194	3	238	59	Sahneerz. u.a. beigeg. LM	\N
2682	194	3	239	60	Sahneerz. mit Fruchtzuber. u.a. beigeg. LM	\N
2683	194	3	240	61	Sahneerz. mit Vanille/Nuss u.a. beigeg. LM	\N
2684	194	3	241	62	Sahneerz. mit Kakao/Schokolade u.a. beigeg. LM	\N
2685	194	3	242	63	Sauermilch dickgelegt mit Früchten/Fruchtzuber. u.a. beigeg. LM	\N
2686	194	3	243	64	Joghurt mit Früchten/Fruchtzuber. u.a. beigeg. LM	\N
2687	194	3	244	65	Joghurt aus entr. Milch mit Früchten/Fruchtzuber. u.a. beigeg. LM	\N
2688	194	3	245	66	Joghurt fettarm mit Früchten/Fruchtzuber. u.a. beigeg. LM	\N
2689	194	3	246	67	Sahnejoghurt mit Früchten/Fruchtzuber. u.a. beigeg. LM	\N
2690	194	3	248	69	Sahnedickmilch mit Früchten/Fruchtzuber.	\N
2691	194	3	249	70	Sahnedickmilch mit Früchten/Fruchtzuber. u.a. beigeg. LM	\N
2692	194	3	250	71	Sahnedickmilch u.a. beigeg. LM	\N
2693	194	3	251	72	Buttermilch mit Früchten/Fruchtzuber.	\N
2694	194	3	252	73	Buttermilch mit Früchten/Fruchtzuber. u.a. beigeg. LM	\N
2695	194	3	253	74	Buttermilch mit a. beigeg. LM	\N
2696	194	3	254	75	Kefir mit Früchten/Fruchtzuber.	\N
2697	194	3	255	76	Kefir mit Früchten/Fruchtzuber. u.a. beigeg. LM	\N
2698	194	3	256	77	Kefir u. a. beigeg. LM	\N
2699	194	3	257	78	Sahnekefir mit Früchten auch Fruchtzuber.	\N
2700	194	3	258	79	Sahnekefir mit Früchten/Fruchtzuber. u.a. beigeg. LM	\N
2701	194	3	259	80	Sahnekefir mit a. beigeg. LM	\N
2702	194	3	260	81	Sahnesauermilch mit Früchten/Fruchtzuber.	\N
2703	194	3	261	82	Sahnesauermilch mit Früchten/Fruchtzuber. u.a. beigeg. LM	\N
2704	194	3	262	83	Sahnesauermilch mit a. beigeg. LM	\N
2705	194	3	263	84	Milchmischerzeugnis aus Joghurt mild	\N
2706	194	3	264	85	Joghurt mild aus entrahmter Milch mit Früchten/Fruchtzubereitung	\N
2707	194	3	265	86	Joghurt mild aus fettarmer Milch mit Früchten/Fruchtzubereitung	\N
2708	194	3	266	87	Joghurt mild mit Früchten/Fruchtzubereitung	\N
2709	194	3	267	88	Milchmischerzeugnis aus Kefir mild	\N
2710	194	3	268	89	Kefir mild fettarm mit Früchten/Fruchtzubereitung	\N
2711	194	3	269	90	Kefir mild mit Früchten/Fruchtzubereitung	\N
2712	194	3	270	91	Sahneerzeugnis im Siphon	\N
2713	194	3	271	92	Sahnejoghurt mild mit Früchten/Fruchtzubereitung	\N
2714	194	3	272	93	Creme fraiche mit Kräutern	\N
2715	194	3	13243	94	Joghurt mit Vanille/Nuss	\N
2716	194	3	13244	95	Sauermilch fettarm dickgelegt mit Früchten/Fruchtzubereitung	\N
2717	194	3	13245	96	Sauermilch entrahmt dickgelegt mit Früchten/Fruchtzubereitung	\N
2718	195	3	274	1	Joghurt aus Ziegenmilch	\N
2719	195	3	275	2	Joghurterzeugnis aus Ziegenmilch	\N
2720	195	3	276	3	Milchmischerzeugnis aus Ziegenmilch	\N
2721	195	3	277	4	Joghurt aus Schafmilch	\N
2722	195	3	278	5	Joghurterzeugnis aus Schafmilch	\N
2723	195	3	279	6	Milchmischerzeugnis aus Schafmilch	\N
2724	195	3	280	7	Milcherzeugnis aus Stutenmilch	\N
2725	196	3	282	1	Stutenmilchpulver	\N
2726	196	3	13246	2	Ziegenmilchpulver	\N
2727	197	3	284	1	Butterreinfett	\N
2728	197	3	285	2	Butterfett	\N
2729	197	3	286	3	Butterfett fraktioniert	\N
2730	197	3	287	4	Milchfettverarbeitungsware	\N
2731	198	3	289	1	Dreiviertelfettbutter	\N
2732	198	3	290	2	Halbfettbutter	\N
2733	198	3	13247	3	Milchstreichfett verschiedener Fettgehaltsstufen	\N
2734	199	3	292	1	Lactose	\N
2735	200	3	294	1	Zaziki aus Joghurt	\N
2736	200	3	13248	2	Zaziki aus Dickmilch	\N
2737	201	3	296	1	Kaffeeweißer-Pulver	\N
2738	201	3	297	2	Kaffeeweißer flüssig	\N
2739	205	3	302	1	Schlagkrem mit Pflanzenfett	\N
2740	206	3	304	1	Krem mit Pflanzenfett und Magermilchjoghurt	\N
2741	207	3	307	1	Chesterkäse Rahmstufe	\N
2742	208	3	309	1	Emmentalerkäse Vollfettstufe	\N
2743	208	3	310	2	Bergkäse Vollfettstufe	\N
2744	208	3	311	3	Chesterkäse Vollfettstufe	\N
2745	209	3	313	1	gestrichen; Parmesankäse Hartkäse andere	\N
2746	209	3	314	2	Greyerzerkäse Hartkäse andere	\N
2747	209	3	315	3	gestrichen jetzt 030911, Raclettekäse Hartkäse andere	\N
2748	209	3	316	4	Viereckhartkäse Hartkäse andere	\N
2749	209	3	317	5	Parmigiano Reggiano Hartkäse andere	\N
2750	209	3	318	6	Grana Padano Hartkäse andere	\N
2751	209	3	319	7	gestrichen jetzt 030909, Leerdamerkäse Hartkäse andere	\N
2752	209	3	320	8	Allgäuer- Illertalerkäse Hartkäse andere	\N
2753	209	3	321	9	Provolonekäse Hartkäse andere	\N
2754	209	3	322	10	Tiefländer Hartkäse andere	\N
2755	210	3	324	1	Tilsiterkäse Doppelrahmstufe	\N
2756	211	3	326	1	Goudakäse Rahmstufe	\N
2757	211	3	327	2	Edamerkäse Rahmstufe	\N
2758	211	3	328	3	Tilsiterkäse Rahmstufe	\N
2759	211	3	330	5	Wilstermarschkäse Rahmstufe	\N
2760	212	3	332	1	Goudakäse Vollfettstufe	\N
2761	212	3	333	2	Edamerkäse Vollfettstufe	\N
2762	212	3	334	3	Tilsiterkäse Vollfettstufe	\N
2763	212	3	335	4	Wilstermarschkäse Vollfettstufe	\N
2764	213	3	337	1	Goudakäse Fettstufe	\N
2765	213	3	338	2	Edamerkäse Fettstufe	\N
2766	213	3	339	3	Tilsiterkäse Fettstufe	\N
2767	214	3	341	1	Goudakäse Dreiviertelfettstufe	\N
2768	214	3	342	2	Edamerkäse Dreiviertelfettstufe	\N
2769	214	3	343	3	Tilsiterkäse Dreiviertelfettstufe	\N
2770	215	3	345	1	Trapistenkäse Schnittkäse andere	\N
2771	215	3	346	2	Danbokäse Schnittkäse andere	\N
2772	215	3	347	3	Esromkäse Schnittkäse andere	\N
2773	215	3	348	4	Havartikäse Schnittkäse andere	\N
2774	215	3	349	5	Amsterdamerkäse Schnittkäse andere	\N
2775	215	3	350	6	gestrichen jetzt 031503,Pyrenäenkäse Schnittkäse andere	\N
2776	215	3	351	7	Steppenkäse Schnittkäse andere	\N
2777	215	3	352	8	Appenzeller Schnittkäse andere	\N
2778	215	3	353	9	Leerdamer Schnittkäse andere	\N
2779	215	3	354	10	Jarlsbergerkäse Schnittkäse andere	\N
2780	215	3	355	11	Raclettekäse Schnittkäse andere	\N
2781	215	3	356	12	Maasdamer Schnittkäse andere	\N
2782	215	3	357	13	Tollenser Schnittkäse andere	\N
2783	215	3	14196	14	Mozzarella Schnittkäse andere	\N
2784	216	3	359	1	Edelpilzkäse Doppelrahmstufe	\N
2785	216	3	360	2	Butterkäse Doppelrahmstufe	\N
2786	217	3	362	1	Steinbuscherkäse Rahmstufe	\N
2787	217	3	363	2	Edelpilzkäse Rahmstufe	\N
2788	217	3	364	3	Butterkäse Rahmstufe	\N
2789	217	3	365	4	Weißlackerkäse Rahmstufe	\N
2790	218	3	367	1	Steinbuscherkäse Vollfettstufe	\N
2791	218	3	368	2	Edelpilzkäse Vollfettstufe	\N
2792	218	3	369	3	Butterkäse Vollfettstufe	\N
2793	218	3	370	4	Weißlackerkäse Vollfettstufe	\N
2794	219	3	372	1	Weißlackerkäse Fettstufe	\N
2795	220	3	374	1	Steinbuscherkäse Dreiviertelfettstufe	\N
2796	221	3	376	1	gestrichen jetzt 030901,Trapistenkäse Schnittkäse halbf. andere	\N
2797	221	3	377	2	gestrichen jetzt 030903,Esromkäse Schnittkäse halbfeste andere	\N
2798	221	3	378	3	Pyrenäenkäse Schnittkäse halbfeste andere	\N
2799	221	3	380	5	Blauschimmelkäse Schnittkäse halbfeste andere	\N
2800	221	3	381	6	Reblochonkäse Schnittkäse halbfeste andere	\N
2801	222	3	383	1	Camembertkäse Doppelrahmstufe	\N
2802	222	3	384	2	Briekäse Doppelrahmstufe	\N
2803	222	3	385	3	Romadurkäse Doppelrahmstufe	\N
2804	223	3	387	1	Camembertkäse Rahmstufe	\N
2805	223	3	388	2	Romadurkäse Rahmstufe	\N
2806	223	3	389	3	Limburgerkäse Rahmstufe	\N
2807	223	3	390	4	Münsterkäse Rahmstufe	\N
2808	223	3	391	5	Briekäse Rahmstufe	\N
2809	224	3	393	1	Camembertkäse Vollfettstufe	\N
2810	224	3	394	2	Briekäse Vollfettstufe	\N
2811	224	3	395	3	Romadurkäse Vollfettstufe	\N
2812	224	3	396	4	Limburgerkäse Vollfettstufe	\N
2813	224	3	397	5	Münsterkäse Vollfettstufe	\N
2814	225	3	399	1	Camembertkäse Fettstufe	\N
2815	225	3	400	2	Romadurkäse Fettstufe	\N
2816	225	3	401	3	Limburgerkäse Fettstufe	\N
2817	226	3	403	1	Camembertkäse Dreiviertelfettstufe	\N
2818	226	3	404	2	Romadurkäse Dreiviertelfettstufe	\N
2819	226	3	405	3	Limburgerkäse Dreiviertelfettstufe	\N
2820	227	3	407	1	Romadurkäse Halbfettstufe	\N
2821	227	3	408	2	Limburgerkäse Halbfettstufe	\N
2822	228	3	411	1	Blauschimmelkäse Halbfettstufe	\N
2823	228	3	412	2	Blauschimmelkäse Doppelrahmstufe	\N
2824	228	3	413	3	Odenwälder Frühstückskäse	\N
2825	228	3	414	4	Sonnenborner Weichkäse	\N
2826	229	3	416	1	Speisequark Doppelrahmstufe auch mit Gewürzen/Kräutern	\N
2827	229	3	417	2	Schichtkäse Doppelrahmstufe auch mit Gewürzen/Kräutern	\N
2828	229	3	418	3	Doppelrahmfrischkäse auch mit Gewürzen/Kräutern	\N
2829	230	3	420	1	Speisequark Rahmstufe auch mit Gewürzen/Kräutern	\N
2830	230	3	421	2	Schichtkäse Rahmstufe auch mit Gewürzen/Kräutern	\N
2831	230	3	422	3	Rahmfrischkäse auch mit Gewürzen/Kräutern	\N
2832	231	3	424	1	Speisequark Vollfettstufe auch mit Gewürzen/Kräutern	\N
2833	231	3	425	2	Schichtkäse Vollfettstufe auch mit Gewürzen/Kräutern	\N
2834	231	3	426	3	Frischkäse Vollfettstufe auch mit Gewürzen/Kräutern	\N
2835	232	3	428	1	Speisequark Fettstufe auch mit Gewürzen/Kräutern	\N
2836	232	3	429	2	Schichtkäse Fettstufe auch mit Gewürzen/Kräutern	\N
2837	232	3	430	3	Frischkäse Fettstufe auch mit Gewürzen/Kräutern	\N
2838	233	3	432	1	Speisequark Dreiviertelfettstufe auch mit Gewürzen/Kräutern	\N
2839	233	3	433	2	Schichtkäse Dreiviertelfettstufe auch mit Gewürzen/Kräutern	\N
2840	233	3	434	3	Frischkäse Dreiviertelfettstufe auch mit Gewürzen/Kräutern	\N
2841	234	3	436	1	Speisequark Halbfettstufe auch mit Gewürzen/Kräutern	\N
2842	234	3	437	2	Schichtkäse Halbfettstufe auch mit Gewürzen/Kräutern	\N
2843	234	3	438	3	Frischkäse Halbfettstufe auch mit Gewürzen/Kräutern	\N
2844	235	3	440	1	Speisequark Viertelfettstufe auch mit Gewürzen/Kräutern	\N
2845	235	3	441	2	Schichtkäse Viertelfettstufe auch mit Gewürzen/Kräutern	\N
2846	235	3	442	3	Frischkäse Viertelfettstufe auch mit Gewürzen/Kräutern	\N
2847	236	3	444	1	Speisequark Magerstufe auch mit Gewürzen/Kräutern	\N
2848	236	3	445	2	Frischkäse Magerstufe auch mit Gewürzen/Kräutern	\N
2849	237	3	447	1	gestrichen jetzt 035304, Mozzarellakäse Frischkäse andere	\N
2850	237	3	448	2	Mascarpone	\N
2851	237	3	449	3	Hüttenkäse	\N
2852	238	3	451	1	Harzerkäse	\N
2853	238	3	452	2	Mainzerkäse	\N
2854	238	3	453	3	Handkäse	\N
2855	238	3	454	4	Korbkäse	\N
2856	238	3	455	5	Stangenkäse	\N
2857	238	3	456	6	Spitzkäse	\N
2858	238	3	457	7	Olmützer Quargel	\N
2859	238	3	458	8	Sauermilchquark mit Kräutern	\N
2860	238	3	459	9	Nieheimerhopfenkäse	\N
2861	238	3	460	10	Sauermilchquark	\N
2862	238	3	461	11	Sauermilchquark getrocknet	\N
2863	238	3	462	12	Bauernhandkäse	\N
2864	238	3	463	13	Buttermilchquark	\N
2865	239	3	465	1	Molkeneiweißkäse	\N
2866	240	3	467	1	Frischkäsezubereitung Doppelrahmstufe mit Früchten/Fruchtzuber.	\N
2867	240	3	468	2	Speisequark Doppelrahmstufe mit Früchten/Fruchtzuber.	\N
2868	240	3	469	3	Speisequark Doppelrahmstufe mit a. beigeg. LM	\N
2869	240	3	470	4	Frischkäsezubereitung Doppelrahmstufe mit a. beigeg. LM	\N
2870	240	3	13249	5	Speisequarkzubereitung alle Fettgehalte mit Früchten/Fruchtzubereitung	\N
2871	240	3	13250	6	Speisequarkzubereitung mit anderen beigegebenen Lebensmitteln	\N
2872	241	3	472	1	Frischkäsezubereitung Rahmstufe mit Früchten/Fruchtzuber.	\N
2873	241	3	473	2	Speisequark Rahmstufe mit Früchten/Fruchtzuber.	\N
2874	241	3	474	3	Speisequark Rahmstufe mit a. beigeg. LM	\N
2875	241	3	475	4	Frischkäsezubereitung Rahmstufe mit a. beigeg. LM	\N
2876	242	3	477	1	Speisequark Vollfettstufe mit Früchten/Fruchtzuber.	\N
2877	242	3	478	2	Speisequark Vollfettstufe mit a. beigeg. LM	\N
2878	242	3	479	3	Frischkäsezubereitung Vollfettstufe mit Früchten/Fruchtzuber.	\N
2879	242	3	480	4	Frischkäsezubereitung Vollfettstufe mit a. beigeg. LM	\N
2880	243	3	482	1	Speisequark Fettstufe mit Früchten/Fruchtzuber.	\N
2881	243	3	483	2	Speisequark Fettstufe mit a. beigeg. LM	\N
2882	243	3	484	3	Frischkäsezubereitung Fettstufe mit Früchten/Fruchtzuber.	\N
2883	243	3	485	4	Frischkäsezubereitung Fettstufe mit a. beigeg. LM	\N
2884	244	3	487	1	Speisequark Dreiviertelfettstufe mit Früchten/Fruchtzuber.	\N
2885	244	3	488	2	Speisequark Dreiviertelfettstufe mit a. beigeg. LM	\N
2886	244	3	489	3	Frischkäsezubereitung Dreiviertelfettst. mit Früchten/Fruchtzuber.	\N
2887	244	3	490	4	Frischkäsezubereitung Dreiviertelfettst. mit a. beigeg. LM	\N
2888	245	3	492	1	Speisequark Halbfettstufe mit Früchten/Fruchtzuber.	\N
2889	245	3	493	2	Speisequark Halbfettstufe mit a. beigeg. LM	\N
2890	245	3	494	3	Frischkäsezubereitung Halbfettst. mit Früchten/Fruchtzuber.	\N
2891	245	3	495	4	Frischkäsezubereitung Halbfettst. mit a. beigeg. LM	\N
2892	246	3	497	1	Speisequark Viertelfettstufe mit Früchten/Fruchtzuber.	\N
2893	246	3	498	2	Speisequark Viertelfettstufe mit a. beigeg. LM	\N
2894	246	3	499	3	Frischkäsezubereitung Viertelfettstufe mit Früchten/Fruchtzuber.	\N
2895	246	3	500	4	Frischkäsezubereitung Viertelfettstufe mit a. beigeg. LM	\N
2896	247	3	502	1	Speisequark Magerstufe mit Früchten/Fruchtzuber.	\N
2897	247	3	503	2	Speisequark Magerstufe mit a. beigeg. LM	\N
2898	247	3	504	3	Frischkäsezubereitung Magerstufe mit Früchten/Fruchtzuber.	\N
2899	247	3	505	4	Frischkäsezubereitung Magerstufe mit a. beigeg. LM	\N
2900	248	3	507	1	Schmelzkäse und -zuber. Doppelrahmstufe mit a. beigeg. LM	\N
2901	248	3	508	2	Schmelzkäse und -zuber. Doppelrahmstufe mit Pfeffer	\N
2902	248	3	509	3	Schmelzkäse und -zuber. Doppelrahmstufe mit Kräutern	\N
2903	248	3	510	4	Schmelzkäse und -zuber. Doppelrahmstufe mit Pilzen	\N
2904	248	3	511	5	Schmelzkäse und -zuber. Doppelrahmstufe mit Schinken	\N
2905	248	3	512	6	Schmelzkäse und -zuber. Doppelrahmstufe mit Salami	\N
2906	248	3	513	7	Schmelzkäse und -zuber. Doppelrahmstufe mit Gurken/Paprika	\N
2907	249	3	515	1	Schmelzkäse und -zuber. Rahmstufe mit a. beigeg. LM	\N
2908	249	3	516	2	Schmelzkäse und -zuber. Rahmstufe mit Pfeffer	\N
3779	334	3	1527	25	Hirn Schwein gegart	\N
2909	249	3	517	3	Schmelzkäse und -zuber. Rahmstufe mit Kräutern	\N
2910	249	3	518	4	Schmelzkäse und -zuber. Rahmstufe mit Schinken	\N
2911	249	3	519	5	Schmelzkäse und -zuber. Rahmstufe mit Salami	\N
2912	249	3	520	6	Schmelzkäse und -zuber. Rahmstufe mit Pilzen	\N
2913	249	3	521	7	Schmelzkäse und -zuber. Rahmstufe mit Gurken/Paprika	\N
2914	249	3	522	8	Schmelzkäse und -zuber. Rahmstufe mit Krabben	\N
2915	249	3	523	9	Schmelzkäse und -zuber. Rahmstufe mit Nüssen	\N
2916	250	3	525	1	Schmelzkäse und -zuber. Vollfettstufe mit a. beigeg. LM	\N
2917	250	3	526	2	Schmelzkäse und -zuber. Vollfettstufe mit Pfeffer	\N
2918	250	3	527	3	Schmelzkäse und -zuber. Vollfettstufe mit Kräutern	\N
2919	250	3	528	4	Schmelzkäse und -zuber. Vollfettstufe mit Schinken	\N
2920	250	3	529	5	Schmelzkäse und -zuber. Vollfettstufe mit Salami	\N
2921	250	3	530	6	Schmelzkäse und -zuber. Vollfettstufe mit Pilzen	\N
2922	250	3	531	7	Schmelzkäse und -zuber. Vollfettstufe mit Krabben	\N
2923	251	3	533	1	Schmelzkäse und -zuber. Fettstufe mit a. beigeg. LM	\N
2924	251	3	534	2	Schmelzkäse und -zuber. Fettstufe mit Pfeffer	\N
2925	251	3	535	3	Schmelzkäse und -zuber. Fettstufe mit Kräutern	\N
2926	251	3	536	4	Schmelzkäse und -zuber. Fettstufe mit Schinken	\N
2927	251	3	537	5	Schmelzkäse und -zuber. Fettstufe mit Salami	\N
2928	251	3	538	6	Schmelzkäse und -zuber. Fettstufe mit Pilzen	\N
2929	251	3	539	7	Schmelzkäse und -zuber. Fettstufe mit Gurken/Paprika	\N
2930	252	3	541	1	Schmelzkäse und -zuber. Dreiviertelfettstufe mit a. beigeg. LM	\N
2931	252	3	542	2	Schmelzkäse und -zuber. Dreiviertelfettstufe mit Pfeffer	\N
2932	252	3	543	3	Schmelzkäse und -zuber. Dreiviertelfettstufe mit Kräutern	\N
2933	252	3	544	4	Schmelzkäse und -zuber. Dreiviertelfettstufe mit Schinken	\N
2934	252	3	545	5	Schmelzkäse und -zuber. Dreiviertelfettstufe mit Salami	\N
2935	252	3	546	6	Schmelzkäse und -zuber. Dreiviertelfettstufe mit Pilzen	\N
2936	252	3	547	7	Schmelzkäse und -zuber. Dreiviertelfettstufe mit Gurken/Paprika	\N
2937	253	3	549	1	Schmelzkäse und -zuber. Halbfettstufe mit a. beigeg. LM	\N
2938	253	3	550	2	Schmelzkäse und -zuber. Halbfettstufe mit Pilzen	\N
2939	253	3	551	3	Schmelzkäse und -zuber. Halbfettstufe mit Schinken	\N
2940	253	3	552	4	Schmelzkäse und -zuber. Halbfettstufe mit Kräutern	\N
2941	253	3	553	5	Schmelzkäse und -zuber. Halbfettstufe mit Pfeffer	\N
2942	253	3	554	6	Schmelzkäse und -zuber. Halbfettstufe mit Salami	\N
2943	253	3	555	7	Schmelzkäse und -zuber. Halbfettstufe mit Gurken/Paprika	\N
2944	254	3	557	1	Schmelzkäse und -zuber. Viertelfettstufe mit a. beigeg. LM	\N
2945	254	3	558	2	Schmelzkäse und -zuber. Viertelfettstufe mit Pfeffer	\N
2946	254	3	559	3	Schmelzkäse und -zuber. Viertelfettstufe mit Kräutern	\N
2947	254	3	560	4	Schmelzkäse und -zuber. Viertelfettstufe mit Schinken	\N
2948	254	3	561	5	Schmelzkäse und -zuber. Viertelfettstufe mit Salami	\N
2949	254	3	562	6	Schmelzkäse und -zuber. Viertelfettstufe mit Pilzen	\N
2950	254	3	563	7	Schmelzkäse und -zuber. Viertelfettstufe mit Gurken/Paprika	\N
2951	255	3	565	1	Schmelzkäse und -zuber. Magerstufe mit a. beigeg. LM	\N
2952	255	3	566	2	Schmelzkäse und -zuber. Magerstufe mit Pfeffer	\N
2953	255	3	567	3	Schmelzkäse und -zuber. Magerstufe mit Kräutern	\N
2954	255	3	568	4	Schmelzkäse und -zuber. Magerstufe mit Schinken	\N
2955	255	3	569	5	Schmelzkäse und -zuber. Magerstufe mit Salami	\N
2956	255	3	570	6	Schmelzkäse und -zuber. Magerstufe mit Pilzen	\N
2957	255	3	571	7	Schmelzkäse und -zuber. Magerstufe mit Gurken/Paprika	\N
2958	256	3	573	1	Fonduekäsemischung auch Konserve	\N
2959	256	3	574	2	Käsekomposition	\N
2960	256	3	575	3	Käseaufschnitt	\N
2961	256	3	576	4	Schmelzkäse verschiedener Fettgehalte in einer Packung	\N
2962	257	3	578	1	Kochkäse Doppelrahmstufe auch mit Gewürzen/Kräutern	\N
2963	257	3	579	2	Kochkäse Rahmstufe auch mit Gewürzen/Kräutern	\N
2964	257	3	580	3	Kochkäse Vollfettstufe auch mit Gewürzen/Kräutern	\N
2965	257	3	581	4	Kochkäse Fettstufe auch mit Gewürzen/Kräutern	\N
2966	257	3	582	5	Kochkäse Dreiviertelfettstufe auch mit Gewürzen/Kräutern	\N
2967	257	3	583	6	Kochkäse Halbfettstufe auch mit Gewürzen/Kräutern	\N
2968	257	3	584	7	Kochkäse Viertelfettstufe auch mit Gewürzen/Kräutern	\N
2969	257	3	585	8	Kochkäse Magerstufe auch mit Gewürzen/Kräutern	\N
2970	258	3	587	1	Ziegenkäse	\N
2971	258	3	588	2	Schafkäse	\N
2972	258	3	589	3	Roquefortkäse	\N
2973	258	3	590	4	Liptauerkäse	\N
2974	258	3	592	6	Käse aus Büffelmilch	\N
2975	258	3	593	7	Altenburger Ziegenkäse	\N
2976	259	3	595	1	Fetakäse Vollfettstufe	\N
2977	259	3	596	2	Fetakäse Rahmstufe	\N
2978	259	3	597	3	Fetakäse andere Fettstufe	\N
2979	259	3	598	4	Mozzarella in Lake	\N
2980	260	3	600	1	Käse in Öl	\N
2981	260	3	601	2	Fetakäse in Öl	\N
2982	260	3	602	3	Käse in Flüssigkeit	\N
2983	261	3	604	1	Zaziki aus Frischkäse	\N
2984	262	3	606	1	Käseimitate mit Pflanzenfett	\N
2985	263	3	608	1	Hartkäse gerieben	\N
2986	263	3	609	2	Schnittkäse gerieben	\N
2987	263	3	610	3	Käse gerieben	\N
2988	263	3	611	4	Käse gewürfelt	\N
2989	263	3	612	5	Käse gestiftelt	\N
2990	265	3	615	1	Sauerrahm-Markenbutter ungesalzen	\N
2991	265	3	616	2	Sauerrahm-Markenbutter gesalzen	\N
2992	265	3	617	3	Sauerrahm-Molkereibutter ungesalzen	\N
2993	265	3	618	4	Sauerrahm-Molkereibutter gesalzen	\N
2994	265	3	621	7	Sauerrahmbutter ungesalzen	\N
2995	265	3	622	8	Sauerrahmbutter gesalzen	\N
2996	266	3	624	1	Süßrahm-Markenbutter ungesalzen	\N
2997	266	3	625	2	Süßrahm-Markenbutter gesalzen	\N
2998	266	3	626	3	Süßrahm-Molkereibutter ungesalzen	\N
2999	266	3	627	4	Süßrahm-Molkereibutter gesalzen	\N
3000	266	3	630	7	Süßrahmbutter ungesalzen	\N
3001	266	3	631	8	Süßrahmbutter gesalzen	\N
3002	267	3	633	1	Markenbutter mild gesäuert gesalzen	\N
3003	267	3	634	2	Markenbutter mild gesäuert ungesalzen	\N
3004	267	3	635	3	Molkereibutter mild gesäuert ungesalzen	\N
3005	267	3	636	4	Molkereibutter mild gesäuert gesalzen	\N
3006	267	3	637	5	Landbutter ungesalzen	\N
3007	267	3	638	6	Landbutter gesalzen	\N
3008	267	3	639	7	Butter mild gesäuert gesalzen	\N
3009	267	3	640	8	Butter mild gesäuert ungesalzen	\N
3010	267	3	641	9	Butter ungesalzen	\N
3011	267	3	642	10	Butter gesalzen	\N
3012	268	3	649	1	Kräuterbutter	\N
3013	268	3	650	2	Sardellenbutter	\N
3014	268	3	651	3	Lachsbutter	\N
3015	268	3	652	4	Butter mit Fruchtzusatz	\N
3016	268	3	653	5	Krebsbutter	\N
3017	268	3	654	6	Pfefferbutter	\N
3018	268	3	655	7	Nussbutter	\N
3019	268	3	656	8	Honigbutter	\N
3020	268	3	657	9	Knoblauchbutter	\N
3021	268	3	658	10	Trüffelbutter	\N
3022	268	3	659	11	Butter mit Röstzwiebeln	\N
3023	268	3	660	12	Butter mit Joghurt	\N
3024	270	3	664	1	ersatzlos gestrichen, Hühnereier Gkl A extra Gewichtsklasse 1	\N
3025	270	3	666	3	ersatzlos gestrichen, Hühnereier Gkl A extra Gewichtsklasse 3	\N
3026	270	3	671	8	ersatzlos gestrichen, Hühnereier Gkl A Gewichtsklasse 1	\N
3027	270	3	672	9	ersatzlos gestrichen, Hühnereier Gkl A Gewichtsklasse 2	\N
3028	270	3	673	10	ersatzlos gestrichen, Hühnereier Gkl A Gewichtsklasse 3	\N
3029	270	3	674	11	ersatzlos gestrichen, Hühnereier Gkl A Gewichtsklasse 4	\N
3030	270	3	675	12	ersatzlos gestrichen, Hühnereier Gkl A Gewichtsklasse 5	\N
3031	270	3	676	13	ersatzlos gestrichen, Hühnereier Gkl A Gewichtsklasse 6	\N
3032	270	3	14274	15	Hühnereier Gkl B nicht gekühlt und nicht haltbar gemacht	\N
3033	270	3	14275	16	Hühnereier Gkl B gekühlt	\N
3034	270	3	14276	17	Hühnereier Gkl B haltbar gemacht	\N
3035	270	3	14277	18	Hühnereier Gkl C	\N
3036	270	3	682	19	Hühnereier unsortierte	\N
3037	270	3	684	21	ersatzlos gestrichen, Hühnereier Gkl A Gewichtsklasse 0	\N
3038	270	3	685	22	Hühnereier Gkl A unbekannter Gewichtsklassen	\N
3039	270	3	686	25	Hühnereier Gkl A extra Gewichtsklasse XL	\N
3040	270	3	687	26	Hühnereier Gkl A extra Gewichtsklasse L	\N
3041	270	3	688	27	Hühnereier Gkl A extra Gewichtsklasse M	\N
3042	270	3	689	28	Hühnereier Gkl A extra Gewichtsklasse S	\N
3043	270	3	690	30	Hühnereier Gkl A Gewichtsklasse XL	\N
3044	270	3	691	31	Hühnereier Gkl A Gewichtsklasse L	\N
3045	270	3	692	32	Hühnereier Gkl A Gewichtsklasse M	\N
3046	270	3	693	33	Hühnereier Gkl A Gewichtsklasse S	\N
3047	271	3	695	1	Vollei flüssig	\N
3048	271	3	696	2	Vollei flüssig mit Zusätzen	\N
3049	271	3	697	3	Vollei gefroren	\N
3050	271	3	698	4	Vollei getrocknet	\N
3051	271	3	699	5	Vollei getrocknet mit Zusätzen	\N
3052	271	3	700	6	Eiklar flüssig	\N
3053	271	3	701	7	Eiklar flüssig mit Zusätzen	\N
3054	271	3	702	8	Eiklar gefroren	\N
3055	271	3	703	9	Eiklar getrocknet	\N
3056	271	3	704	10	Eiklar getrocknet mit Zusätzen	\N
3057	271	3	705	11	Eidotter flüssig	\N
3058	271	3	706	12	Eidotter flüssig mit Zusätzen	\N
3059	271	3	707	13	Eidotter gefroren	\N
3060	271	3	708	14	Eidotter getrocknet	\N
3061	271	3	709	15	Eidotter getrocknet mit Zusätzen	\N
3062	271	3	710	16	Vollei gefroren mit Zusätzen	\N
3063	272	3	712	1	Enteneier	\N
3064	272	3	713	2	Gänseeier	\N
3065	272	3	714	3	Wachteleier	\N
3066	272	3	715	4	Kiebitzeier	\N
3067	272	3	716	5	Möweneier	\N
3068	272	3	717	6	Puteneier	\N
3069	272	3	718	7	Straußeneier	\N
3070	272	3	719	8	Perlhuhneier	\N
3071	272	3	720	9	Silbermöweneier	\N
3072	272	3	721	10	Lachmöweneier	\N
3073	272	3	722	11	Heringsmöweneier	\N
3074	272	3	723	12	Rotschenkelmöweneier	\N
3075	272	3	724	13	Sturmmöweneier	\N
3076	273	3	726	1	Vollei anderer Geflügelarten flüssig	\N
3077	273	3	727	2	Vollei anderer Geflügelarten flüssig mit Zusätzen	\N
3078	273	3	728	3	Vollei anderer Geflügelarten gefroren	\N
3079	273	3	729	4	Vollei anderer Geflügelarten getrocknet	\N
3080	273	3	730	5	Vollei anderer Geflügelarten getrocknet mit Zusätzen	\N
3081	273	3	731	6	Eiklar anderer Geflügelarten flüssig	\N
3082	273	3	732	7	Eiklar anderer Geflügelarten flüssig mit Zusätzen	\N
3083	273	3	733	8	Eiklar anderer Geflügelarten gefroren	\N
3084	273	3	734	9	Eiklar anderer Geflügelarten getrocknet	\N
3085	273	3	735	10	Eiklar anderer Geflügelarten getrocknet mit Zusätzen	\N
3086	273	3	736	11	Eidotter anderer Geflügelarten flüssig	\N
3087	273	3	737	12	Eidotter anderer Geflügelarten flüssig mit Zusätzen	\N
3088	273	3	738	13	Eidotter anderer Geflügelarten gefroren	\N
3089	273	3	739	14	Eidotter anderer Geflügelarten getrocknet	\N
3090	273	3	740	15	Eidotter anderer Geflügelarten getrocknet mit Zusätzen	\N
3091	274	3	742	1	Solei	\N
3092	274	3	743	2	Eierstich	\N
3093	274	3	744	3	Hühnerei fermentiert	\N
3094	274	3	745	4	Hühnerei gekocht	\N
3095	274	3	746	5	Hühnerei gekocht geformt	\N
3096	274	3	747	6	Hühnerei Konserve	\N
3097	274	3	748	7	Hühnerei in Aspik	\N
3098	274	3	749	8	Hühnerei gekocht gefärbt	\N
3099	274	3	750	9	Chinesisches Ei aus Hühnereiern	\N
3100	274	3	751	10	Eierstich Konserve	\N
3101	275	3	753	1	Wachtelei gekocht	\N
3102	275	3	754	2	Wachtelei Konserve	\N
3103	275	3	755	3	Möwenei gekocht	\N
3104	275	3	756	4	Entenei gekocht	\N
3105	275	3	757	5	Chinesisches Ei aus Enteneiern	\N
3106	276	3	767	1	Tierkörper ganzer Rind auch tiefgefroren	\N
3107	276	3	768	2	Tierkörper halber Rind auch tiefgefroren	\N
3108	276	3	769	3	Vorderviertel Rind auch tiefgefroren	\N
3109	276	3	770	4	Hinterviertel Rind auch tiefgefroren	\N
3110	277	3	772	1	Kamm Rind auch tiefgefroren	\N
3111	277	3	773	2	Bug Rind auch tiefgefroren	\N
3112	277	3	774	3	Bugstück dickes Rind auch tiefgefroren	\N
3113	277	3	775	4	Schaufelstück Rind auch tiefgefroren	\N
3114	277	3	776	5	Falsches Filet Rind auch tiefgefroren	\N
3115	277	3	777	6	Schaufeldeckel Rind auch tiefgefroren	\N
3116	277	3	778	7	Fehlrippe Rind auch tiefgefroren	\N
3117	277	3	779	8	Spannrippe Rind auch tiefgefroren	\N
3118	277	3	780	9	Brust Rind auch tiefgefroren	\N
3119	277	3	781	10	Brustbein Rind auch tiefgefroren	\N
3120	277	3	782	11	Nachbrust Rind auch tiefgefroren	\N
3121	277	3	783	12	Mittelbrust Rind auch tiefgefroren	\N
3122	277	3	784	13	Vorderhesse Rind auch tiefgefroren	\N
3123	277	3	785	14	Keule Rind auch tiefgefroren	\N
3124	277	3	786	15	Filet Rind auch tiefgefroren	\N
3125	277	3	787	16	Blume Rind auch tiefgefroren	\N
3126	277	3	788	17	Kugel Rind auch tiefgefroren	\N
3127	277	3	789	18	Schwanzstück Rind auch tiefgefroren	\N
3128	277	3	790	19	Oberschale Rind auch tiefgefroren	\N
3129	277	3	791	20	Roastbeef Rind auch tiefgefroren	\N
3130	277	3	792	21	Hochrippe Rind auch tiefgefroren	\N
3131	277	3	793	22	Knochendünnung Rind auch tiefgefroren	\N
3132	277	3	794	23	Fleischdünnung Rind auch tiefgefroren	\N
3133	277	3	795	24	Kopf Rind auch tiefgefroren	\N
3134	277	3	796	25	Bürgermeisterstück Rind auch tiefgefroren	\N
3135	277	3	797	26	Hinterhesse Rind auch tiefgefroren	\N
3136	277	3	798	27	Kronfleisch Rind auch tiefgefroren	\N
3137	277	3	799	28	Ochsenschwanz auch tiefgefroren	\N
3138	277	3	800	29	Rumpsteak auch tiefgefroren	\N
3139	277	3	801	30	Steak Rind auch tiefgefroren	\N
3140	277	3	802	31	Braten Rind auch tiefgefroren	\N
3141	277	3	803	32	Schnitzel Rind auch tiefgefroren	\N
3142	277	3	804	33	Kotelett Rind auch tiefgefroren	\N
3143	277	3	805	34	Roulade Rind auch tiefgefroren	\N
3144	277	3	806	35	Tafelspitz auch tiefgefroren	\N
3145	277	3	807	36	Fleischstück Rind zusammengesetzt geformt auch tiefgefroren	\N
3146	277	3	808	37	Brustspitze Rind auch tiefgefroren	\N
3147	277	3	809	38	Lende Rind auch tiefgefroren	\N
3148	277	3	810	39	Verarbeitungsfleisch Rind roh auch tiefgefroren	\N
3149	277	3	14268	40	Beinscheibe Rind auch tiefgefroren	\N
3150	277	3	811	99	Mischungen verschiedener Fleischteilstücke Rind auch tiefgefroren	\N
3151	278	3	813	1	Leber Rind auch tiefgefroren	\N
3152	278	3	814	2	Niere Rind auch tiefgefroren	\N
3153	278	3	815	3	Lunge Rind auch tiefgefroren	\N
3154	278	3	816	4	Milz Rind auch tiefgefroren	\N
3155	278	3	817	5	Herz Rind auch tiefgefroren	\N
3156	278	3	818	6	Hirn Rind auch tiefgefroren	\N
3157	278	3	819	7	Euter Rind auch tiefgefroren	\N
3158	278	3	820	8	Vormagen Rind auch tiefgefroren	\N
3159	278	3	821	9	Zunge Rind auch tiefgefroren	\N
3160	278	3	822	10	Labmagen Rind auch tiefgefroren	\N
3161	278	3	823	11	Schlund Rind auch tiefgefroren	\N
3162	279	3	825	1	Darmfett Rind auch tiefgefroren	\N
3163	279	3	826	2	Nierenfett Rind auch tiefgefroren	\N
3164	279	3	827	3	Mark Rind auch tiefgefroren	\N
3165	280	3	829	1	Flotzmaul Rind auch tiefgefroren	\N
3166	280	3	830	2	Fußhaut Rind auch tiefgefroren	\N
3167	282	3	833	1	Flüssigblut Rind auch tiefgefroren	\N
3168	282	3	834	2	Blutserum Rind auch tiefgefroren	\N
3169	282	3	835	3	Blutplasma Rind auch tiefgefroren	\N
3170	282	3	836	4	Trockenblutplasma Rind auch tiefgefroren	\N
3171	283	3	838	1	Tierkörper ganzer Kalb auch tiefgefroren	\N
3172	283	3	839	2	Tierkörper halber Kalb auch tiefgefroren	\N
3173	283	3	840	3	Vorderviertel Kalb auch tiefgefroren	\N
3174	283	3	841	4	Hinterviertel Kalb auch tiefgefroren	\N
3175	284	3	843	1	Bug Kalb auch tiefgefroren	\N
3176	284	3	844	2	Hals Kalb auch tiefgefroren	\N
3177	284	3	845	3	Vorderhaxe Kalb auch tiefgefroren	\N
3178	284	3	846	4	Kotelett Kalb auch tiefgefroren	\N
3179	284	3	847	5	Filet Kalb auch tiefgefroren	\N
3180	284	3	848	6	Brust Kalb auch tiefgefroren	\N
3181	284	3	849	7	Bauch Kalb auch tiefgefroren	\N
3182	284	3	850	8	Keule Kalb auch tiefgefroren	\N
3183	284	3	851	9	Oberschale Kalb auch tiefgefroren	\N
3184	284	3	852	10	Nussstück Kalb auch tiefgefroren	\N
3185	284	3	853	11	Fricandeau Kalb auch tiefgefroren	\N
3186	284	3	854	12	Hinterhaxe Kalb auch tiefgefroren	\N
3187	284	3	855	13	Kopf Kalb auch tiefgefroren	\N
3188	284	3	856	14	Zwerchfell Kalb auch tiefgefroren	\N
3189	284	3	857	15	Nierenbraten Kalb auch tiefgefroren	\N
3190	284	3	858	16	Schnitzel Kalb auch tiefgefroren	\N
3191	284	3	859	17	Braten Kalb auch tiefgefroren	\N
3192	284	3	860	18	Steak Kalb auch tiefgefroren	\N
3193	284	3	861	19	Rollbraten Kalb auch tiefgefroren	\N
3194	284	3	862	20	Kleinfleisch Kalb auch tiefgefroren	\N
3195	284	3	863	21	Fleischstück Kalb zusammengesetzt geformt auch tiefgefroren	\N
3196	284	3	864	22	Lende Kalb auch tiefgefroren	\N
3197	284	3	865	99	Mischungen verschiedener Fleischteilstücke Kalb auch tiefgefroren	\N
3198	285	3	867	1	Leber Kalb auch tiefgefroren	\N
3199	285	3	868	2	Niere Kalb auch tiefgefroren	\N
3200	285	3	869	3	Lunge Kalb auch tiefgefroren	\N
3201	285	3	870	4	Zunge Kalb auch tiefgefroren	\N
3202	285	3	871	5	Milz Kalb auch tiefgefroren	\N
3203	285	3	872	6	Herz Kalb auch tiefgefroren	\N
3204	285	3	873	7	Hirn Kalb auch tiefgefroren	\N
3205	285	3	874	8	Bries Kalb auch tiefgefroren	\N
3206	285	3	875	9	Gekröse Kalb auch tiefgefroren	\N
3207	285	3	876	10	Labmagen Kalb auch tiefgefroren	\N
3208	285	3	877	11	Kutteln Kalb auch tiefgefroren	\N
3209	285	3	878	12	Schlund Kalb auch tiefgefroren	\N
3210	286	3	880	1	Nierenfett Kalb auch tiefgefroren	\N
3211	286	3	881	2	Netz Kalb auch tiefgefroren	\N
3212	286	3	882	3	Mark Kalb auch tiefgefroren	\N
3213	287	3	884	1	Fußhaut Kalb auch tiefgefroren	\N
3214	287	3	885	2	Kopfhaut Kalb auch tiefgefroren	\N
3215	289	3	888	1	Flüssigblut Kalb auch tiefgefroren	\N
3216	289	3	889	2	Trockenblut Kalb	\N
3217	290	3	891	1	Tierkörper ganzer Schwein auch tiefgefroren	\N
3218	290	3	892	2	Spanferkel auch tiefgefroren	\N
3219	290	3	893	3	Tierkörper halber Schwein auch tiefgefroren	\N
3220	291	3	895	1	Kopf Schwein auch tiefgefroren	\N
3221	291	3	896	2	Kamm Schwein auch tiefgefroren	\N
3222	291	3	897	3	Kotelett Schwein auch tiefgefroren	\N
3223	291	3	898	4	Filet Schwein auch tiefgefroren	\N
3224	291	3	899	5	Backe Schwein auch tiefgefroren	\N
3225	291	3	900	6	Schulter Schwein auch tiefgefroren	\N
3226	291	3	901	7	Eisbein auch tiefgefroren	\N
3227	291	3	902	8	Spitzbein auch tiefgefroren	\N
3228	291	3	903	9	Bauch Schwein auch tiefgefroren	\N
3229	291	3	904	10	Schinken Schwein auch tiefgefroren	\N
3230	291	3	905	11	Oberschale Schwein auch tiefgefroren	\N
3231	291	3	906	12	Nussstück Schwein auch tiefgefroren	\N
3232	291	3	907	13	Schinkenspeck Schwein auch tiefgefroren	\N
3233	291	3	908	14	Unterschale Schwein auch tiefgefroren	\N
3234	291	3	909	15	Zwerchfell Schwein auch tiefgefroren	\N
3235	291	3	910	16	Schwanz Schwein auch tiefgefroren	\N
3236	291	3	911	17	Maske Schwein auch tiefgefroren	\N
3237	291	3	912	18	Wamme Schwein auch tiefgefroren	\N
3238	291	3	913	19	Schnitzel Schwein auch tiefgefroren	\N
3239	291	3	914	20	Braten Schwein auch tiefgefroren	\N
3240	291	3	915	21	Rollbraten Schwein auch tiefgefroren	\N
3241	291	3	916	22	Steak Schwein auch tiefgefroren	\N
3242	291	3	917	23	Nierenbraten Schwein auch tiefgefroren	\N
3243	291	3	918	24	Kleinfleisch Schwein auch tiefgefroren	\N
3244	291	3	920	26	Brust Schwein auch tiefgefroren	\N
3245	291	3	921	27	Ohr Schwein auch tiefgefroren	\N
3246	291	3	922	28	Lende Schwein auch tiefgefroren	\N
3247	291	3	923	29	Verarbeitungsfleisch Schwein roh auch tiefgefroren	\N
3248	291	3	14223	30	Rücken Schwein auch tiefgefroren Schweinelachs auch tiefgefroren	\N
3249	291	3	924	99	Mischungen verschiedener Fleischteilstücke Schwein auch tiefgefroren	\N
3250	292	3	926	1	Leber Schwein auch tiefgefroren	\N
3251	292	3	927	2	Niere Schwein auch tiefgefroren	\N
3252	292	3	928	3	Lunge Schwein auch tiefgefroren	\N
3253	292	3	929	4	Zunge Schwein auch tiefgefroren	\N
3254	292	3	930	5	Milz Schwein auch tiefgefroren	\N
3255	292	3	931	6	Herz Schwein auch tiefgefroren	\N
3256	292	3	932	7	Hirn Schwein auch tiefgefroren	\N
3257	292	3	933	8	Schlund Schwein auch tiefgefroren	\N
3258	292	3	934	9	Schilddrüse Schwein auch tiefgefroren	\N
3259	293	3	936	1	Rückenspeck Schwein auch tiefgefroren	\N
3260	293	3	937	2	Bauchspeck Schwein auch tiefgefroren	\N
3261	293	3	938	3	Flomen Schwein auch tiefgefroren	\N
3262	293	3	939	4	Micker Schwein auch tiefgefroren	\N
3263	293	3	940	5	Netz Schwein auch tiefgefroren	\N
3264	293	3	941	6	Nierenfett Schwein auch tiefgefroren	\N
3265	294	3	943	1	Sehne Schwein auch tiefgefroren	\N
3266	294	3	944	2	Schwarte Schwein auch tiefgefroren	\N
3267	294	3	945	3	Schwartenzug Schwein auch tiefgefroren	\N
3268	296	3	948	1	Flüssigblut Schwein auch tiefgefroren	\N
3269	296	3	949	2	Blutserum Schwein auch tiefgefroren	\N
3270	296	3	950	3	Blutplasma Schwein auch tiefgefroren	\N
3271	296	3	951	4	Trockenblut Schwein	\N
3272	296	3	952	5	Trockenblutplasma Schwein	\N
3273	297	3	954	1	Tierkörper ganzer Lamm/Schaf auch tiefgefroren	\N
3274	297	3	955	2	Tierkörper halber Lamm/Schaf auch tiefgefroren	\N
3275	298	3	957	1	Hals Lamm/Schaf auch tiefgefroren	\N
3276	298	3	958	2	Kopf Lamm/Schaf auch tiefgefroren	\N
3277	298	3	959	3	Kotelett Lamm/Schaf auch tiefgefroren	\N
3278	298	3	960	4	Lende Lamm/Schaf auch tiefgefroren	\N
3279	298	3	961	5	Filet Lamm/Schaf auch tiefgefroren	\N
3280	298	3	962	6	Bug Lamm/Schaf auch tiefgefroren	\N
3281	298	3	963	7	Brust Lamm/Schaf auch tiefgefroren	\N
3282	298	3	964	8	Dünnung Lamm/Schaf auch tiefgefroren	\N
3283	298	3	965	9	Keule Lamm/Schaf auch tiefgefroren	\N
3284	299	3	967	1	Leber Lamm/Schaf auch tiefgefroren	\N
3285	299	3	968	2	Niere Lamm/Schaf auch tiefgefroren	\N
3286	299	3	969	3	Lunge Lamm/Schaf auch tiefgefroren	\N
3287	299	3	970	4	Zunge Lamm/Schaf auch tiefgefroren	\N
3288	299	3	971	5	Milz Lamm/Schaf auch tiefgefroren	\N
3289	299	3	972	6	Herz Lamm/Schaf auch tiefgefroren	\N
3290	299	3	973	7	Hirn Lamm/Schaf auch tiefgefroren	\N
3291	299	3	974	8	Bries Lamm auch tiefgefroren	\N
3292	300	3	976	1	Nierenfett Lamm/Schaf auch tiefgefroren	\N
3293	300	3	977	2	Blut Lamm/Schaf auch tiefgefroren	\N
3294	300	3	978	3	Knochen Lamm/Schaf auch tiefgefroren	\N
3295	300	3	979	4	Fettgewebe Lamm/Schaf auch tiefgefroren	\N
3296	301	3	981	1	Tierkörper ganzer Fohlen/Pferd auch tiefgefroren	\N
3297	301	3	982	2	Tierkörper halber Fohlen/Pferd auch tiefgefroren	\N
3298	301	3	983	3	Vorderviertel Fohlen/Pferd auch tiefgefroren	\N
3299	301	3	984	4	Hinterviertel Fohlen/Pferd auch tiefgefroren	\N
3300	303	3	987	1	Niere Fohlen/Pferd auch tiefgefroren	\N
3301	303	3	988	2	Leber Fohlen/Pferd auch tiefgefroren	\N
3302	303	3	989	3	Herz Fohlen/Pferd auch tiefgefroren	\N
3303	304	3	991	1	Fettgewebe Fohlen/Pferd auch tiefgefroren	\N
3304	304	3	992	2	Blut Fohlen/Pferd auch tiefgefroren	\N
3305	304	3	993	3	Knochen Fohlen/Pferd auch tiefgefroren	\N
3306	304	3	994	4	Nierenfett Fohlen/Pferd auch tiefgefroren	\N
3307	305	3	996	1	Ziege auch tiefgefroren	\N
3308	305	3	997	2	Einhufer andere auch tiefgefroren ausgenommen 062600	\N
3309	306	3	999	1	Gulasch Rind auch tiefgefroren	\N
3310	306	3	1000	2	Gulasch Schwein auch tiefgefroren	\N
3311	306	3	1001	3	Gulasch Fleischmischung aus Wild auch tiefgefroren	\N
3312	306	3	1002	4	Gulasch Kalb auch tiefgefroren	\N
3313	306	3	1003	5	Gulasch Rind und Schwein auch tiefgefroren	\N
3314	306	3	1004	6	Gulasch Fohlen/Pferd auch tiefgefroren	\N
3315	306	3	1005	7	Gulasch Hirsch auch tiefgefroren	\N
3316	306	3	1006	8	Gulasch Reh auch tiefgefroren	\N
3317	306	3	1007	9	Gulasch Wildschwein auch tiefgefroren	\N
3318	306	3	1008	10	Gulasch Lamm/Schaf auch tiefgefroren	\N
3319	306	3	1009	11	Gulasch Pute auch tiefgefroren	\N
3320	306	3	13251	12	Gulasch Huhn auch tiefgefroren	\N
3321	306	3	14279	13	Verarbeitungsfleisch Geflügel roh auch tiefgefroren	\N
3322	307	3	1011	1	Hackfleisch Rind auch tiefgefroren	\N
3323	307	3	1012	2	Schabefleisch auch tiefgefroren	\N
3324	307	3	1013	3	Hackfleisch Schwein auch tiefgefroren	\N
3325	307	3	1014	4	Hackfleisch gemischt Rind/Schwein auch tiefgefroren	\N
3326	307	3	1015	5	Hackfleisch Lamm/Schaf auch tiefgefroren	\N
3327	307	3	1016	6	Hackfleisch Kalb auch tiefgefroren	\N
3328	307	3	1017	7	Leberhack gemischt auch tiefgefroren	\N
3329	307	3	1018	8	Hackfleisch Fohlen/Pferd auch tiefgefroren	\N
3330	307	3	1019	9	Geschnetzeltes Schwein auch tiefgefroren	\N
3331	307	3	1020	10	Geschnetzeltes Rind auch tiefgefroren	\N
3332	307	3	1021	11	Geschnetzeltes Kalb auch tiefgefroren	\N
3333	307	3	1022	12	Leberhack Schwein auch tiefgefroren	\N
3334	307	3	1023	13	Leberhack Rind auch tiefgefroren	\N
3335	307	3	1024	14	Leberhack Kalb auch tiefgefroren	\N
3336	307	3	1025	15	Steak Schwein mit Mürbeschneider behandelt roh auch tiefgefroren	\N
3337	307	3	1026	16	Steak Kalb mit Mürbeschneider behandelt roh auch tiefgefroren	\N
3338	307	3	1027	17	Steak Rind mit Mürbeschneider behandelt roh auch tiefgefroren	\N
3339	307	3	1028	18	Schnitzel Schwein mit Mürbeschneider behandelt roh auch tiefgefroren	\N
3340	307	3	1029	19	Schnitzel Kalb mit Mürbeschneider behandelt roh auch tiefgefroren	\N
3341	307	3	1030	20	Schnitzel Rind mit Mürbeschneider behandelt roh auch tiefgefroren	\N
3342	307	3	1031	21	Geschnetzeltes Pute auch tiefgefroren	\N
3343	307	3	1032	22	Geschnetzeltes Geflügel auch tiefgefroren	\N
3344	307	3	14239	23	Hackfleisch Geflügel auch tiefgefroren	\N
3345	308	3	1034	1	Darm Fohlen/Pferd	\N
3346	308	3	1035	2	Darm Lamm/Schaf	\N
3347	308	3	1036	3	Darm Rind	\N
3348	308	3	1037	4	Darm Schwein	\N
3349	308	3	1038	5	Darm Kalb	\N
3350	308	3	1039	6	Magen Schwein	\N
3351	308	3	1040	7	Harnblase Rind	\N
3352	308	3	1041	8	Harnblase Kalb	\N
3353	308	3	1042	9	Harnblase Schwein	\N
3354	308	3	1043	10	Harnblase Lamm/Schaf	\N
3355	308	3	1044	11	Pansenwand	\N
3356	308	3	1045	12	Rinderspalthaut	\N
3357	309	3	1047	1	Tierkörper ganzer Hauskaninchen auch tiefgefroren	\N
3358	309	3	1048	2	Fleischteilstück Hauskaninchen auch tiefgefroren	\N
3359	309	3	1049	3	Leber Hauskaninchen auch tiefgefroren	\N
3360	309	3	1050	4	Niere Hauskaninchen auch tiefgefroren	\N
3361	309	3	1051	5	Herz Hauskaninchen auch tiefgefroren	\N
3362	309	3	1052	6	Knochen Hauskaninchen auch tiefgefroren	\N
3363	309	3	1053	7	Lunge Hauskaninchen auch tiefgefroren	\N
3364	309	3	1054	8	Milz Hauskaninchen auch tiefgefroren	\N
3365	309	3	1055	9	Fettgewebe Hauskaninchen auch tiefgefroren	\N
3366	310	3	1057	1	Poularde auch tiefgefroren	\N
3367	310	3	1058	2	Hähnchen auch tiefgefroren	\N
3368	310	3	1059	3	Junger Hahn auch tiefgefroren	\N
3369	310	3	1060	4	Suppenhuhn auch tiefgefroren	\N
3370	310	3	1061	5	Brust Huhn auch tiefgefroren	\N
3371	310	3	1062	6	Schenkel Huhn auch tiefgefroren	\N
3372	310	3	1063	7	Oberschenkel Huhn auch tiefgefroren	\N
3373	310	3	1064	8	Unterschenkel Huhn auch tiefgefroren	\N
3374	310	3	1065	9	Magen Huhn auch tiefgefroren	\N
3375	310	3	1066	10	Leber Huhn auch tiefgefroren	\N
3376	310	3	1067	11	Herz Huhn auch tiefgefroren	\N
3377	310	3	1068	12	Flügel Huhn auch tiefgefroren	\N
3378	310	3	1069	13	Hals und Rücken Huhn auch tiefgefroren	\N
3379	310	3	1070	14	Hühnerklein auch tiefgefroren	\N
3380	310	3	1071	15	Huhn Hälfte auch tiefgefroren	\N
3381	310	3	1072	16	Niere Huhn auch tiefgefroren	\N
3382	310	3	1073	17	Fettgewebe Huhn auch tiefgefroren	\N
3383	310	3	1074	18	Fleischteilstück Hähnchen/Huhn auch tiefgefroren	\N
3384	310	3	1075	19	Innereienmischung Hähnchen/Huhn auch tiefgefroren	\N
3385	310	3	1077	21	Blut Hähnchen/Huhn auch tiefgefroren	\N
3386	310	3	1078	22	Hähnchenrolle auch tiefgefroren	\N
3387	310	3	13252	23	Stubenküken auch tiefgefroren	\N
3388	310	3	14278	24	Verarbeitungsfleisch Huhn roh auch tiefgefroren	\N
3389	311	3	1080	1	Tierkörper ganzer Ente auch tiefgefroren	\N
3390	311	3	1081	2	Fleischteilstück Ente auch tiefgefroren	\N
3391	311	3	1082	3	Entenklein auch tiefgefroren	\N
3392	311	3	1083	4	Leber Ente auch tiefgefroren	\N
3393	311	3	1084	5	Fettgewebe Ente auch tiefgefroren	\N
3394	311	3	1085	6	Herz Ente auch tiefgefroren	\N
3395	311	3	1086	7	Magen Ente auch tiefgefroren	\N
3396	311	3	1087	8	Niere Ente auch tiefgefroren	\N
3397	311	3	1088	9	Innereienmischung Ente auch tiefgefroren	\N
3398	311	3	1089	10	Blut Ente auch tiefgefroren	\N
3399	311	3	1090	11	Brust Ente auch tiefgefroren	\N
3400	312	3	1092	1	Tierkörper ganzer Gans auch tiefgefroren	\N
3401	312	3	1093	2	Fleischteilstück Gans auch tiefgefroren	\N
3402	312	3	1094	3	Gänseklein auch tiefgefroren	\N
3403	312	3	1095	4	Leber Gans auch tiefgefroren	\N
3404	312	3	1096	5	Fettgewebe Gans auch tiefgefroren	\N
3405	312	3	1097	6	Schenkel Gans auch tiefgefroren	\N
3406	312	3	1098	7	Niere Gans auch tiefgefroren	\N
3407	312	3	1099	8	Herz Gans auch tiefgefroren	\N
3408	312	3	1100	9	Magen Gans auch tiefgefroren	\N
3409	312	3	1101	10	Innereienmischung Gans auch tiefgefroren	\N
3410	312	3	1102	11	Blut Gans auch tiefgefroren	\N
3411	312	3	1103	12	Unterschenkel Gans auch tiefgefroren	\N
3412	312	3	1104	13	Oberschenkel Gans auch tiefgefroren	\N
3413	312	3	1105	14	Brust Gans auch tiefgefroren	\N
3414	313	3	1107	1	Tierkörper ganzer Pute auch tiefgefroren	\N
3415	313	3	1108	2	Fleischteilstück Pute auch tiefgefroren	\N
3416	313	3	1109	3	Schenkel Pute auch tiefgefroren	\N
3417	313	3	1110	4	Oberschenkel Pute auch tiefgefroren	\N
3418	313	3	1111	5	Unterschenkel Pute auch tiefgefroren	\N
3419	313	3	1112	6	Brust Pute auch tiefgefroren	\N
3420	313	3	1113	7	Magen Pute auch tiefgefroren	\N
3421	313	3	1114	8	Leber Pute auch tiefgefroren	\N
3422	313	3	1115	9	Herz Pute auch tiefgefroren	\N
3423	313	3	1116	10	Rollbraten Pute auch tiefgefroren	\N
3424	313	3	1117	11	Rolle Pute auch tiefgefroren	\N
3425	313	3	1118	12	Niere Pute auch tiefgefroren	\N
3426	313	3	1119	13	Fettgewebe Pute auch tiefgefroren	\N
3427	313	3	1120	14	Fleischstück Pute zusammengesetzt geformt auch tiefgefroren	\N
3428	313	3	1121	15	Blut Pute auch tiefgefroren	\N
3429	313	3	1122	16	Innereienmischung Pute auch tiefgefroren	\N
3430	313	3	1123	17	Flügel Pute auch tiefgefroren	\N
3431	313	3	14280	18	Verarbeitungsfleisch Pute roh auch tiefgefroren	\N
3432	314	3	1125	1	Perlhuhn auch tiefgefroren	\N
3433	314	3	1126	2	Taube auch tiefgefroren	\N
3434	314	3	1127	3	Herz Perlhuhn auch tiefgefroren	\N
3435	314	3	1128	4	Magen Perlhuhn auch tiefgefroren	\N
3436	314	3	1129	5	Leber Perlhuhn auch tiefgefroren	\N
3437	314	3	1130	6	Niere Perlhuhn auch tiefgefroren	\N
3438	314	3	1131	7	Fettgewebe Perlhuhn auch tiefgefroren	\N
3439	314	3	1132	8	Herz Taube auch tiefgefroren	\N
3440	314	3	1133	9	Magen Taube auch tiefgefroren	\N
3441	314	3	1134	10	Leber Taube auch tiefgefroren	\N
3442	314	3	1135	11	Niere Taube auch tiefgefroren	\N
3443	314	3	1136	12	Fettgewebe Taube auch tiefgefroren	\N
3444	314	3	1137	13	Wachtel auch tiefgefroren	\N
3445	314	3	1138	14	Herz Wachtel auch tiefgefroren	\N
3446	314	3	1139	15	Magen Wachtel auch tiefgefroren	\N
3447	314	3	1140	16	Leber Wachtel auch tiefgefroren	\N
3448	314	3	1141	17	Niere Wachtel auch tiefgefroren	\N
3449	314	3	1142	18	Fettgewebe Wachtel auch tiefgefroren	\N
3450	315	3	1144	1	gestr. 064043/064046,Tierkörper ganzer Rot/Damwild a. tiefgefr.	\N
3451	315	3	1145	2	gestr. 064044/064047, Fleischteilstück Rot/Damwild a. tiefgefr.	\N
3452	315	3	1146	3	Tierkörper ganzer Reh auch tiefgefroren	\N
3453	315	3	1147	4	Fleischteilstück Reh auch tiefgefroren	\N
3454	315	3	1148	5	Tierkörper ganzer Wildschwein auch tiefgefroren	\N
3455	315	3	1149	6	Fleischteilstück Wildschwein auch tiefgefroren	\N
3456	315	3	1150	7	Tierkörper ganzer Hase auch tiefgefroren	\N
3457	315	3	1151	8	Fleischteilstück Hase auch tiefgefroren	\N
3458	315	3	1152	9	Tierkörper ganzer Wildkaninchen auch tiefgefroren	\N
3459	315	3	1153	10	Fleischteilstück Wildkaninchen auch tiefgefroren	\N
3460	315	3	1154	11	Tierkörper ganzer Ren auch tiefgefroren	\N
3461	315	3	1155	12	Fleischteilstück Ren auch tiefgefroren	\N
3462	315	3	1156	13	Tierkörper ganzer Elch auch tiefgefroren	\N
3463	315	3	1157	14	Fleischteilstück Elch auch tiefgefroren	\N
3464	315	3	1158	15	Tierkörper ganzer Gams auch tiefgefroren	\N
3465	315	3	1159	16	Fleischteilstück Gams auch tiefgefroren	\N
3466	315	3	1160	17	Tierkörper ganzer Känguruh auch tiefgefroren	\N
3467	315	3	1161	18	Fleischteilstück Känguruh auch tiefgefroren	\N
3468	315	3	1162	19	Tierkörper ganzer Mufflon auch tiefgefroren	\N
3469	315	3	1163	20	Fleischteilstück Mufflon auch tiefgefroren	\N
3470	315	3	1165	22	Fettgewebe Reh auch tiefgefroren	\N
3471	315	3	1166	23	Fettgewebe Wildschwein auch tiefgefroren	\N
3472	315	3	1167	24	Fettgewebe Hase auch tiefgefroren	\N
3473	315	3	1168	25	Fettgewebe Wildkaninchen auch tiefgefroren	\N
3474	315	3	1169	26	Fettgewebe Ren auch tiefgefroren	\N
3475	315	3	1170	27	Fettgewebe Elch auch tiefgefroren	\N
3476	315	3	1171	28	Fettgewebe Gams auch tiefgefroren	\N
3477	315	3	1172	29	Fettgewebe Känguruh auch tiefgefroren	\N
3478	315	3	1173	30	Fettgewebe Mufflon auch tiefgefroren	\N
3479	315	3	1174	31	Tierkörper ganzer Bär auch tiefgefroren	\N
3480	315	3	1175	32	Fleischteilstück Bär auch tiefgefroren	\N
3481	315	3	1176	33	Tierkörper ganzer Antilope auch tiefgefroren	\N
3780	334	3	1528	26	Grieben Schwein	\N
3482	315	3	1177	34	Fleischteilstück Antilope auch tiefgefroren	\N
3483	315	3	1178	35	Tierkörper ganzer Gazelle auch tiefgefroren	\N
3484	315	3	1179	36	Fleischteilstück Gazelle auch tiefgefroren	\N
3485	315	3	1180	37	Tierkörper ganzer Springbock auch tiefgefroren	\N
3486	315	3	1181	38	Fleischteilstück Springbock auch tiefgefroren	\N
3487	315	3	1182	39	Fettgewebe Bär auch tiefgefroren	\N
3488	315	3	1183	40	Fettgewebe Antilope auch tiefgefroren	\N
3489	315	3	1184	41	Fettgewebe Gazelle auch tiefgefroren	\N
3490	315	3	1185	42	Fettgewebe Springbock auch tiefgefroren	\N
3491	315	3	1186	43	Tierkörper ganzer Hirsch auch tiefgefroren	\N
3492	315	3	1187	44	Fleischteilstück Hirsch auch tiefgefroren	\N
3493	315	3	1188	45	Fettgewebe Hirsch auch tiefgefroren	\N
3494	315	3	1189	46	Tierkörper ganzer Damwild auch tiefgefroren	\N
3495	315	3	1190	47	Fleischteilstück Damwild auch tiefgefroren	\N
3496	315	3	1191	48	Fettgewebe Damwild auch tiefgefroren	\N
3497	315	3	13253	49	Fleischteilstück Bison auch tiefgefroren	\N
3498	316	3	1195	3	Leber Reh auch tiefgefroren	\N
3499	316	3	1196	4	Niere Reh auch tiefgefroren	\N
3500	316	3	1197	5	Leber Wildschwein auch tiefgefroren	\N
3501	316	3	1198	6	Niere Wildschwein auch tiefgefroren	\N
3502	316	3	1199	7	Leber Hase auch tiefgefroren	\N
3503	316	3	1200	8	Niere Hase auch tiefgefroren	\N
3504	316	3	1201	9	Leber Wildkaninchen auch tiefgefroren	\N
3505	316	3	1202	10	Niere Wildkaninchen auch tiefgefroren	\N
3506	316	3	1203	11	Leber Ren auch tiefgefroren	\N
3507	316	3	1204	12	Niere Ren auch tiefgefroren	\N
3508	316	3	1205	13	Leber Elch auch tiefgefroren	\N
3509	316	3	1206	14	Niere Elch auch tiefgefroren	\N
3510	316	3	1207	15	Leber Gams auch tiefgefroren	\N
3511	316	3	1208	16	Niere Gams auch tiefgefroren	\N
3512	316	3	1209	17	Leber Känguruh auch tiefgefroren	\N
3513	316	3	1210	18	Niere Känguruh auch tiefgefroren	\N
3514	316	3	1211	19	Leber Mufflon auch tiefgefroren	\N
3515	316	3	1212	20	Niere Mufflon auch tiefgefroren	\N
3516	316	3	1217	25	Herz Reh auch tiefgefroren	\N
3517	316	3	1218	26	Magen Reh auch tiefgefroren	\N
3518	316	3	1219	27	Milz Reh auch tiefgefroren	\N
3519	316	3	1220	28	Zwerchfell Reh auch tiefgefroren	\N
3520	316	3	1221	29	Herz Wildschwein auch tiefgefroren	\N
3521	316	3	1222	30	Magen Wildschwein auch tiefgefroren	\N
3522	316	3	1223	31	Milz Wildschwein auch tiefgefroren	\N
3523	316	3	1224	32	Zwerchfell Wildschwein auch tiefgefroren	\N
3524	316	3	1225	33	Herz Hase auch tiefgefroren	\N
3525	316	3	1226	34	Magen Hase auch tiefgefroren	\N
3526	316	3	1227	35	Milz Hase auch tiefgefroren	\N
3527	316	3	1228	36	Zwerchfell Hase auch tiefgefroren	\N
3528	316	3	1229	37	Herz Wildkaninchen auch tiefgefroren	\N
3529	316	3	1230	38	Magen Wildkaninchen auch tiefgefroren	\N
3530	316	3	1231	39	Milz Wildkaninchen auch tiefgefroren	\N
3531	316	3	1232	40	Zwerchfell Wildkaninchen auch tiefgefroren	\N
3532	316	3	1233	41	Herz Gams auch tiefgefroren	\N
3533	316	3	1234	42	Magen Gams auch tiefgefroren	\N
3534	316	3	1235	43	Milz Gams auch tiefgefroren	\N
3535	316	3	1236	44	Zwerchfell Gams auch tiefgefroren	\N
3536	316	3	1237	45	Herz Mufflon auch tiefgefroren	\N
3537	316	3	1238	46	Magen Mufflon auch tiefgefroren	\N
3538	316	3	1239	47	Milz Mufflon auch tiefgefroren	\N
3539	316	3	1240	48	Zwerchfell Mufflon auch tiefgefroren	\N
3540	316	3	1241	49	Herz Ren auch tiefgefroren	\N
3541	316	3	1242	50	Magen Ren auch tiefgefroren	\N
3542	316	3	1243	51	Milz Ren auch tiefgefroren	\N
3543	316	3	1244	52	Zwerchfell Ren auch tiefgefroren	\N
3544	316	3	1245	53	Herz Elch auch tiefgefroren	\N
3545	316	3	1246	54	Magen Elch auch tiefgefroren	\N
3546	316	3	1247	55	Milz Elch auch tiefgefroren	\N
3547	316	3	1248	56	Zwerchfell Elch auch tiefgefroren	\N
3548	316	3	1249	57	Herz Känguruh auch tiefgefroren	\N
3549	316	3	1250	58	Magen Känguruh auch tiefgefroren	\N
3550	316	3	1251	59	Milz Känguruh auch tiefgefroren	\N
3551	316	3	1252	60	Zwerchfell Känguruh auch tiefgefroren	\N
3552	316	3	1253	61	Leber Hirsch auch tiefgefroren	\N
3553	316	3	1254	62	Niere Hirsch auch tiefgefroren	\N
3554	316	3	1255	63	Herz Hirsch auch tiefgefroren	\N
3555	316	3	1256	64	Magen Hirsch auch tiefgefroren	\N
3556	316	3	1257	65	Milz Hirsch auch tiefgefroren	\N
3557	316	3	1258	66	Zwerchfell Hirsch auch tiefgefroren	\N
3558	316	3	1259	67	Leber Damwild auch tiefgefroren	\N
3559	316	3	1260	68	Niere Damwild auch tiefgefroren	\N
3560	316	3	1261	69	Herz Damwild auch tiefgefroren	\N
3561	316	3	1262	70	Magen Damwild auch tiefgefroren	\N
3562	316	3	1263	71	Milz Damwild auch tiefgefroren	\N
3563	316	3	1264	72	Zwerchfell Damwild auch tiefgefroren	\N
3564	317	3	1266	1	Tierkörper ganzer Rebhuhn auch tiefgefroren	\N
3565	317	3	1267	2	Tierkörper ganzer Fasan auch tiefgefroren	\N
3566	317	3	1268	3	Tierkörper ganzer Wildente auch tiefgefroren	\N
3567	317	3	1269	4	Tierkörper ganzer Birkhuhn auch tiefgefroren	\N
3568	317	3	1270	5	Tierkörper ganzer Wildtaube auch tiefgefroren	\N
3569	317	3	1271	6	Tierkörper ganzer Schnepfe auch tiefgefroren	\N
3570	317	3	1272	7	Tierkörper ganzer Schneehuhn auch tiefgefroren	\N
3571	317	3	1274	9	Tierkörper ganzer Wildgans auch tiefgefroren	\N
3572	317	3	1275	10	Fettgewebe Rebhuhn auch tiefgefroren	\N
3573	317	3	1276	11	Fettgewebe Fasan auch tiefgefroren	\N
3574	317	3	1277	12	Fettgewebe Wildente auch tiefgefroren	\N
3575	317	3	1278	13	Fettgewebe Birkhuhn auch tiefgefroren	\N
3576	317	3	1279	14	Fettgewebe Wildtaube auch tiefgefroren	\N
3577	317	3	1280	15	Fettgewebe Schnepfe auch tiefgefroren	\N
3578	317	3	1281	16	Fettgewebe Schneehuhn auch tiefgefroren	\N
3579	317	3	1283	18	Fettgewebe Wildgans auch tiefgefroren	\N
3580	317	3	1284	19	Tierkörper ganzer Strauß auch tiefgefroren	\N
3581	317	3	1285	20	Fettgewebe Strauß auch tiefgefroren	\N
3582	317	3	1286	21	Fleischteilstück Strauß auch tiefgefroren	\N
3583	318	3	1288	1	Leber Rebhuhn auch tiefgefroren	\N
3584	318	3	1289	2	Niere Rebhuhn auch tiefgefroren	\N
3585	318	3	1290	3	Magen Rebhuhn auch tiefgefroren	\N
3586	318	3	1291	4	Herz Rebhuhn auch tiefgefroren	\N
3587	318	3	1292	5	Leber Fasan auch tiefgefroren	\N
3588	318	3	1293	6	Niere Fasan auch tiefgefroren	\N
3589	318	3	1294	7	Magen Fasan auch tiefgefroren	\N
3590	318	3	1295	8	Herz Fasan auch tiefgefroren	\N
3591	318	3	1296	9	Leber Wildente auch tiefgefroren	\N
3592	318	3	1297	10	Niere Wildente auch tiefgefroren	\N
3593	318	3	1298	11	Magen Wildente auch tiefgefroren	\N
3594	318	3	1299	12	Herz Wildente auch tiefgefroren	\N
3595	318	3	1300	13	Leber Birkhuhn auch tiefgefroren	\N
3596	318	3	1301	14	Niere Birkhuhn auch tiefgefroren	\N
3597	318	3	1302	15	Magen Birkhuhn auch tiefgefroren	\N
3598	318	3	1303	16	Herz Birkhuhn auch tiefgefroren	\N
3599	318	3	1304	17	Leber Wildtaube auch tiefgefroren	\N
3600	318	3	1305	18	Niere Wildtaube auch tiefgefroren	\N
3601	318	3	1306	19	Magen Wildtaube auch tiefgefroren	\N
3602	318	3	1307	20	Herz Wildtaube auch tiefgefroren	\N
3603	318	3	1308	21	Leber Schnepfe auch tiefgefroren	\N
3604	318	3	1309	22	Niere Schnepfe auch tiefgefroren	\N
3605	318	3	1310	23	Magen Schnepfe auch tiefgefroren	\N
3606	318	3	1311	24	Herz Schnepfe auch tiefgefroren	\N
3607	318	3	1312	25	Leber Schneehuhn auch tiefgefroren	\N
3608	318	3	1313	26	Niere Schneehuhn auch tiefgefroren	\N
3609	318	3	1314	27	Magen Schneehuhn auch tiefgefroren	\N
3610	318	3	1315	28	Herz Schneehuhn auch tiefgefroren	\N
3611	318	3	1320	33	Leber Wildgans auch tiefgefroren	\N
3612	318	3	1321	34	Niere Wildgans auch tiefgefroren	\N
3613	318	3	1322	35	Magen Wildgans auch tiefgefroren	\N
3614	318	3	1323	36	Herz Wildgans auch tiefgefroren	\N
3615	319	3	1325	1	Walfleisch auch tiefgefroren	\N
3616	320	3	1327	1	Fleischteilstück roh Rind dehydratisiert	\N
3617	321	3	1354	1	Zunge Rind gepökelt roh ungeräuchert	\N
3618	321	3	1355	2	Pökelbrust Rind roh ungeräuchert	\N
3619	321	3	1356	3	Schinken Rind gepökelt roh ungeräuchert	\N
3620	321	3	1357	4	Rindfleisch gepökelt roh ungeräuchert	\N
3621	322	3	1359	1	Rauchfleisch Rind roh	\N
3622	322	3	1360	2	Saftschinken Rind roh geräuchert	\N
3623	323	3	1362	1	Corned Beef	\N
3624	323	3	1364	3	Zunge Rind gepökelt gegart	\N
3625	323	3	1365	4	Corned Beef in Gelee	\N
3626	323	3	1366	5	Corned Beef deutsches	\N
3627	323	3	1367	6	Formfleischschinken Rind gegart	\N
3628	323	3	1368	7	Pökelbrust Rind gegart	\N
3629	323	3	1369	8	Schinkenimitat Rind gegart ungeräuchert	\N
3630	324	3	1371	1	Rauchfleisch Rind gegart	\N
3631	324	3	1372	2	Pökelrinderbrust geräuchert	\N
3632	325	3	1374	1	Fleisch in eigenem Saft Rind Konserve	\N
3633	325	3	1375	2	Corned Beef Konserve	\N
3634	325	3	1376	3	Corned Beef deutsches Konserve	\N
3635	325	3	1377	4	Gulasch Rind Konserve	\N
3636	325	3	1378	5	Ragout Rind Konserve	\N
3637	325	3	1379	6	Roulade Rind Konserve	\N
3638	325	3	1380	7	Schmorbraten Rind Konserve	\N
3639	325	3	1381	8	Zunge Rind Konserve	\N
3640	325	3	1382	9	Sauerbraten Konserve	\N
3641	325	3	1383	10	Fleisch Rind tafelfertig Konserve	\N
3642	325	3	1384	11	Corned Beef mit Gelee Konserve	\N
3643	325	3	1385	12	Ochsenschwanz Konserve	\N
3644	325	3	1386	13	Kutteln Rind Konserve	\N
3645	326	3	1388	1	Bündnerfleisch	\N
3646	326	3	1389	2	Neuenahrer Fleisch	\N
3647	326	3	1390	3	Trockenfleisch Rind	\N
3648	327	3	1392	1	Sauerbraten Rind gegart	\N
3649	327	3	1393	2	Gulasch Rind gegart	\N
3650	327	3	1394	3	Roulade Rind gegart	\N
3651	327	3	1395	4	Filet Rind gegart	\N
3652	327	3	1396	5	Rostbraten Rind gegart	\N
3653	327	3	1397	6	Roastbeef Rind gegart	\N
3654	327	3	1398	7	Lende Rind gegart	\N
3655	327	3	1399	8	Zunge Rind gegart	\N
3656	327	3	1400	9	Leber Rind gegart	\N
3657	327	3	1401	10	Niere Rind gegart	\N
3658	327	3	1402	11	Herz Rind gegart	\N
3659	327	3	1403	12	Ochsenschwanz gegart	\N
3660	327	3	1404	13	Kutteln Rind gegart	\N
3661	327	3	1405	14	Brust Rind gegart	\N
3662	327	3	1406	15	Steak Rind gegart	\N
3663	327	3	1407	16	Lunge Rind gegart	\N
3664	327	3	1408	17	Braten Rind gegart	\N
3665	328	3	1410	1	Pökelfleisch Schwein roh ungeräuchert	\N
3666	328	3	1411	2	Eisbein gepökelt roh ungeräuchert	\N
3667	328	3	1412	3	Frühstücksspeck gepökelt roh ungeräuchert	\N
3668	328	3	1413	4	Schinken gepökelt luftgetrocknet ungeräuchert	\N
3669	328	3	1414	5	Rippchen gepökelt roh ungeräuchert	\N
3670	328	3	1415	6	Zunge Schwein roh ungeräuchert	\N
3671	328	3	1416	7	Fleisch Schwein roh gepökelt zur Schinkenherstellung	\N
3672	328	3	1417	8	Parmaschinken	\N
3673	328	3	1418	9	Halsgrat Schwein gepökelt roh ungeräuchert Coppa luftgetrocknet	\N
3674	328	3	14059	10	Serranoschinken	\N
3675	329	3	1420	1	Frühstücksspeck roh geräuchert	\N
3676	329	3	1421	2	Schinken roh geräuchert	\N
3677	329	3	1422	3	Nussschinken roh geräuchert	\N
3678	329	3	1423	4	Lachsschinken roh geräuchert	\N
3679	329	3	1424	5	Rollschinken roh geräuchert	\N
3680	329	3	1425	6	Pfefferschinken roh geräuchert	\N
3681	329	3	1426	7	Spaltschinken roh geräuchert	\N
3682	329	3	1427	8	Wacholderschinken roh geräuchert	\N
3683	329	3	1428	9	Gewürzschinken roh geräuchert	\N
3684	329	3	1429	10	Knochenschinken roh geräuchert	\N
3685	329	3	1430	11	Kasseler roh geräuchert	\N
3686	329	3	1431	12	Schwarzwälder Schinken roh geräuchert	\N
3687	329	3	1432	13	Schwarzwälder Rauchfleisch roh	\N
3688	329	3	1433	14	Schinkenspeck roh geräuchert	\N
3689	329	3	1434	15	Speck roh geräuchert	\N
3690	329	3	1435	16	Paprikaspeck	\N
3691	329	3	1436	17	Schwarzwälder Speck	\N
3692	329	3	1437	18	Blasenschinken	\N
3693	329	3	1438	19	Bauchspeck roh geräuchert	\N
3694	329	3	1439	20	Schwarzgeräuchertes roh geräuchert	\N
3695	329	3	1440	21	Katenschinken	\N
3696	329	3	1441	22	Ammerländerschinken roh geräuchert	\N
3697	329	3	1442	23	Alemannenschinken roh geräuchert	\N
3698	329	3	1443	24	Karbonadenschinken roh geräuchert	\N
3699	329	3	1444	25	Nackenschinken roh geräuchert	\N
3700	329	3	1445	26	Pariser Lachsschinken roh geräuchert	\N
3701	329	3	1446	27	Lachsfleisch	\N
3702	329	3	1447	28	Bauernschinken roh geräuchert	\N
3703	329	3	1448	29	Kernschinken roh geräuchert	\N
3704	329	3	1449	30	Dörrfleisch	\N
3705	329	3	1450	31	Eisbein roh geräuchert	\N
3706	329	3	14240	32	Lende Schwein gepökelt roh geräuchert	\N
3707	330	3	1452	1	Rippchen Schwein gepökelt gegart ungeräuchert	\N
3708	330	3	1453	2	Eisbein gepökelt gegart ungeräuchert	\N
3709	330	3	1454	3	Rollschinken Schwein gepökelt gegart ungeräuchert	\N
3710	330	3	1455	4	Kleinfleisch Schwein gepökelt gegart ungeräuchert	\N
3711	330	3	1456	5	Kochschinken Vorderschinken gepökelt gegart ungeräuch. auch geformt	\N
3712	330	3	1457	6	Schälrippchen Schwein gepökelt gegart ungeräuchert	\N
3713	330	3	1458	7	Kochschinken Hinterschinken gepökelt gegart ungeräuch. auch geformt	\N
3714	330	3	1459	8	Pressschinken Schwein gepökelt gegart ungeräuchert	\N
3715	330	3	1460	9	Formfleischschinken Schwein aus Schinkenteilen zusammengesetzt gepökelt gegart	\N
3716	330	3	1461	10	Formfleischvorderschinken aus Vorderschinkenteilen zusammengesetzt gepökelt	\N
3717	330	3	1462	11	Corned Porc	\N
3718	330	3	1463	12	Schinkenimitat Schwein gegart ungeräuchert	\N
3719	331	3	1465	1	Kochschinken Vorderschinken gepökelt gegart geräuchert auch geformt	\N
3720	331	3	1466	2	Gewürzschinken gepökelt gegart geräuchert auch geformt	\N
3721	331	3	1467	3	Wacholderschinken gepökelt gegart geräuchert	\N
3722	331	3	1468	4	Rollschinken gepökelt gegart geräuchert	\N
3723	331	3	1469	5	Schweinshaxen gepökelt gegart geräuchert	\N
3724	331	3	1470	6	Schälrippchen Schwein gepökelt gegart geräuchert	\N
3725	331	3	1471	7	Schweinefleisch geselchtes gepökelt gegart geräuchert	\N
3726	331	3	1472	8	Karbonadenschinken gepökelt gegart geräuchert	\N
3727	331	3	1473	9	Kochschinken Hinterschinken gepökelt gegart geräuchert auch geformt	\N
3728	331	3	1474	10	Kleinfleisch Schwein gepökelt gegart geräuchert	\N
3729	331	3	1475	11	Rippchen Schwein gepökelt gegart geräuchert	\N
3730	331	3	1476	12	Brustspitze Schwein gepökelt gegart geräuchert	\N
3731	331	3	1477	13	Pariser Rolle gepökelt gegart geräuchert	\N
3732	331	3	1478	14	Bauchspeck Schwein gepökelt gegart geräuchert	\N
3733	331	3	1479	15	Bauchrolle Schwein gepökelt gegart geräuchert	\N
3734	331	3	1480	16	Bauch Schwein heißgeräuchert gepökelt gegart	\N
3735	331	3	1481	17	Heißgeräuchertes gepökelt gegart	\N
3736	331	3	1482	18	Schinken Schwein schwarzgeräuchert gepökelt gegart	\N
3737	331	3	1483	19	Kasseler gepökelt gegart geräuchert	\N
3738	331	3	1484	20	Frühstücksspeck gepökelt gegart geräuchert	\N
3739	331	3	1485	21	Kaiserfleisch	\N
3740	331	3	1486	22	Nackenkasseler gepökelt gegart geräuchert	\N
3741	331	3	1487	23	Lende Schwein gepökelt gegart geräuchert	\N
3742	331	3	1488	24	Schwarzgeräuchertes gepökelt gegart	\N
3743	332	3	1490	1	Schmalzfleisch Konserve	\N
3744	332	3	1491	2	Fleisch in eigenem Saft Schwein Konserve	\N
3745	332	3	1492	3	Gulasch Schwein Konserve	\N
3746	332	3	1493	4	Ragout Schwein Konserve	\N
3747	332	3	1494	5	Fleisch tafelfertig Schwein Konserve	\N
3748	332	3	1495	6	Braten Schwein Konserve	\N
3749	332	3	1496	7	Roulade Schwein Konserve	\N
3750	332	3	1497	8	Kochschinken Vorderschinken Konserve	\N
3751	332	3	1498	9	Kochschinken Hinterschinken Konserve	\N
3752	332	3	1499	10	Pressschinken Konserve	\N
3753	332	3	1500	11	Corned Porc Konserve	\N
3754	332	3	1501	12	Eisbein Konserve	\N
3755	332	3	13254	13	Formfleisch Konserve	\N
3756	334	3	1504	1	Braten Schwein gegart	\N
3757	334	3	1505	2	Kotelett Schwein auch paniert gegart	\N
3758	334	3	1506	3	Schulter Schwein gegart	\N
3759	334	3	1507	4	Schnitzel Schwein auch paniert gegart	\N
3760	334	3	1508	5	Nacken Schwein gegart	\N
3761	334	3	1509	6	Haxe Schwein gegart	\N
3762	334	3	1510	7	Eisbein gegart	\N
3763	334	3	1511	8	Kesselfleisch Schwein gegart	\N
3764	334	3	1512	9	Spanferkel gegart	\N
3765	334	3	1513	10	Knochen Schwein gegart	\N
3766	334	3	1514	11	Schwanz Schwein gegart	\N
3767	334	3	1515	12	Schwarte Schwein gegart	\N
3768	334	3	1516	14	Rollbraten Schwein gegart	\N
3769	334	3	1517	15	Gulasch Schwein gegart	\N
3770	334	3	1518	16	Fleischspieß Schwein gegart	\N
3771	334	3	1519	17	Roulade Schwein gegart	\N
3772	334	3	1520	18	Bauch Schwein auch paniert gegart	\N
3773	334	3	1521	19	Leber Schwein auch paniert gegart	\N
3774	334	3	1522	20	Niere Schwein gegart	\N
3775	334	3	1523	21	Lunge Schwein gegart	\N
3776	334	3	1524	22	Zunge Schwein gegart	\N
3777	334	3	1525	23	Milz Schwein gegart	\N
3781	334	3	1529	27	Rüssel Schwein gegart	\N
3782	334	3	1530	28	Ohr Schwein gegart	\N
3783	334	3	1531	29	Fleischstück Schwein zusammengesetzt geformt paniert gegart	\N
3784	334	3	1532	30	Filet Schwein gegart	\N
3785	335	3	1534	1	Fleisch in eigenem Saft Kalb Konserve	\N
3786	335	3	1535	2	Gulasch Kalb Konserve	\N
3787	335	3	1536	3	Ragout Kalb Konserve	\N
3788	335	3	1537	4	Frikassee Kalb Konserve	\N
3789	335	3	1538	5	Lunge Kalb Konserve	\N
3790	335	3	1539	6	Kopf Kalb Konserve	\N
3791	335	3	1540	7	Zunge Kalb Konserve	\N
3792	335	3	1541	8	Braten Kalb Konserve	\N
3793	335	3	1542	9	Roulade Kalb Konserve	\N
3794	335	3	1543	10	Ragout fin Kalb Konserve	\N
3795	335	3	1544	11	Ragout fin Kalb/Geflügel Konserve	\N
3796	336	3	1546	1	Braten Kalb gegart	\N
3797	336	3	1547	2	Lende Kalb gegart	\N
3798	336	3	1548	3	Schulter Kalb gegart	\N
3799	336	3	1549	4	Brust Kalb gegart	\N
3800	336	3	1550	5	Haxe Kalb gegart	\N
3801	336	3	1551	6	Kopf Kalb gegart	\N
3802	336	3	1552	7	Kotelett Kalb auch paniert gegart	\N
3803	336	3	1553	8	Gulasch Kalb gegart	\N
3804	336	3	1554	9	Schnitzel Kalb auch paniert gegart	\N
3805	336	3	1555	10	Kalbsnierenbraten gegart	\N
3806	336	3	1556	11	Bries Kalb gegart	\N
3807	336	3	1557	12	Gekröse Kalb gegart	\N
3808	336	3	1558	13	Knochen Kalb gegart	\N
3809	336	3	1559	14	Kopf und Fußhäute Kalb gegart	\N
3810	336	3	1560	15	Ragout Kalb gegart	\N
3811	336	3	1561	16	Fuß Kalb gegart	\N
3812	336	3	1562	17	Leber Kalb auch paniert gegart	\N
3813	336	3	1563	18	Niere Kalb gegart	\N
3814	336	3	1564	19	Lunge Kalb gegart	\N
3815	336	3	1565	20	Zunge Kalb gegart	\N
3816	336	3	1566	21	Milz Kalb gegart	\N
3817	336	3	1567	22	Herz Kalb gegart	\N
3818	336	3	1568	23	Hirn Kalb gegart	\N
3819	336	3	1569	24	Rollbraten Kalb gegart	\N
3820	336	3	1570	25	Ragout fin Kalb gegart	\N
3821	336	3	1571	26	Ragout fin Kalb/Geflügel gegart	\N
3822	337	3	1573	1	Braten Lamm/Schaf gegart	\N
3823	337	3	1576	4	Keule Lamm/Schaf gegart	\N
3824	337	3	1577	5	Schulter Lamm/Schaf gegart	\N
3825	337	3	1578	6	Kamm Lamm/Schaf gegart	\N
3826	337	3	1579	7	Leber Lamm/Schaf gegart	\N
3827	337	3	1580	8	Niere Lamm/Schaf gegart	\N
3828	337	3	1581	9	Zunge Lamm/Schaf gegart	\N
3829	337	3	1582	10	Herz Lamm/Schaf gegart	\N
3830	337	3	1583	11	Hirn Lamm/Schaf gegart	\N
3831	337	3	1584	12	Knochen Lamm/Schaf gegart	\N
3832	337	3	1585	13	Kotelett Lamm/Schaf gegart	\N
3833	338	3	1587	1	Mett roh auch tiefgefroren	\N
3834	338	3	1588	2	Frikadelle roh auch tiefgefroren	\N
3835	338	3	1589	3	Burger roh auch tiefgefroren	\N
3836	338	3	1590	4	Hacksteak roh auch tiefgefroren	\N
3837	338	3	1591	5	Cevapcici roh auch tiefgefroren	\N
3838	338	3	1592	6	Fleischspieß roh auch tiefgefroren	\N
3839	338	3	1593	7	Schaschlik roh auch tiefgefroren	\N
3840	338	3	1594	8	Schinkenmett roh auch tiefgefroren	\N
3841	338	3	1595	9	Netzkotelett roh auch tiefgefroren	\N
3842	338	3	1596	10	Suppenkloß roh auch tiefgefroren	\N
3843	338	3	1597	11	Königsberger Klops roh auch tiefgefroren	\N
3844	338	3	1598	12	Leberknödel roh auch tiefgefroren	\N
3845	338	3	1599	13	Leberspätzle roh auch tiefgefroren	\N
3846	338	3	1600	14	Deutsches Beefsteak roh auch tiefgefroren	\N
3847	338	3	1601	15	Hackbraten roh auch tiefgefroren	\N
3848	338	3	1602	16	Hackfleischfüllung roh auch tiefgefroren	\N
3849	338	3	1603	17	Nierenspieß roh auch tiefgefroren	\N
3850	338	3	1604	18	Fleischklößchen roh auch tiefgefroren	\N
3851	338	3	1605	19	Leberkäsebrät roh auch tiefgefroren	\N
3852	338	3	1606	20	Kalbskäsebrät roh auch tiefgefroren	\N
3853	338	3	1607	21	Bratwurst grob 2.221.11 roh auch tiefgefroren	\N
3854	338	3	1608	22	Schweinsbratwürstchen 2.221.11 roh auch tiefgefroren	\N
3855	338	3	1609	23	Fränkische Bratwurst 2.221.11 roh auch tiefgefroren	\N
3856	338	3	1610	24	Pfälzer Bratwurst 2.221.11 roh auch tiefgefroren	\N
3857	338	3	1611	25	Hessische Bratwurst 2.221.11 roh auch tiefgefroren	\N
3858	338	3	1612	26	Rostbratwurst 2.221.11 roh auch tiefgefroren	\N
3859	338	3	1613	27	Nürnberger Rostbratwurst 2.221.11 roh auch tiefgefroren	\N
3860	338	3	1614	28	Treuchtlinger Bratwurst 2.221.11 roh auch tiefgefroren	\N
3861	338	3	1615	29	Bratwurst feinzerkleinert 2.221.12 roh auch tiefgefroren	\N
3862	338	3	1616	30	Rheinische Bratwurst 2.221.12 roh auch tiefgefroren	\N
3863	338	3	1617	31	Schlesische Bratwurst 2.221.12 roh auch tiefgefroren	\N
3864	338	3	1618	32	Thüringer Bratwurst 2.221.11 roh auch tiefgefroren	\N
3865	338	3	1619	33	Brät roh auch tiefgefroren	\N
3866	338	3	1620	34	Geflügelfleischspieß roh auch tiefgefroren	\N
3867	338	3	1621	35	Geschnetzeltes Schwein roh gewürzt auch tiefgefroren	\N
3868	338	3	1622	36	Geschnetzeltes Rind roh gewürzt auch tiefgefroren	\N
3869	338	3	1623	37	Geschnetzeltes Kalb roh gewürzt auch tiefgefroren	\N
3870	338	3	1624	38	Schweinebauch gefüllt roh auch tiefgefroren	\N
3871	338	3	1625	39	Kalbsbrust gefüllt roh auch tiefgefroren	\N
3872	338	3	1626	40	Spießbraten gefüllt roh auch tiefgefroren	\N
3873	338	3	1627	41	Schweinebrust gefüllt roh auch tiefgefroren	\N
3874	338	3	1628	42	Boulette roh auch tiefgefroren	\N
3875	338	3	1629	43	Markklößchen roh auch tiefgefroren	\N
3876	338	3	1630	44	Döner Kebab Hackspieß oder gleichartige Produkte roh auch tiefgefroren	\N
3877	338	3	1631	45	Fleischstück Schwein zusammengesetzt geformt auch tiefgefroren	\N
3878	338	3	1632	46	Fleischstück Hähnchen/Huhn zusammengesetzt geformt auch tiefgefroren	\N
5169	492	3	3035	35	Kilka Filet	\N
3879	338	3	1633	47	Gyros roh auch tiefgefroren	\N
3880	338	3	1634	48	Geschnetzeltes Pute roh gewürzt auch tiefgefroren	\N
3881	338	3	1635	49	Rindswurst roh auch tiefgefroren	\N
3882	338	3	1636	50	Fleischklößchenspieß roh auch tiefgefroren	\N
3883	338	3	1637	51	Suppenbrät roh auch tiefgefroren	\N
3884	338	3	1638	52	Geflügelhacksteak roh auch tiefgefroren	\N
3885	338	3	1639	54	Geflügelbratwurst roh auch tiefgefroren	\N
3886	338	3	1640	55	Geschnetzeltes Geflügel roh auch tiefgefroren	\N
3887	338	3	1641	56	Fleischzubereitungen anderer Tierarten roh auch tiefgefroren	\N
3888	338	3	1642	57	Kalbsbrät roh auch tiefgefroren	\N
3889	338	3	1643	58	Hackfleischspieß nach Döner Kebab Art roh auch tiefgefroren	\N
3890	338	3	1644	59	Putenfleischspieß roh auch tiefgefroren	\N
3891	338	3	1645	60	Geschnetzeltes Geflügel roh gewürzt auch tiefgefroren	\N
3892	338	3	1646	61	Geschnetzeltes Pute roh auch tiefgefroren	\N
3893	338	3	13255	62	Würzhack (außer  Mett )	\N
3894	338	3	13256	63	Geschnetzeltes Wild roh auch tiefgefroren	\N
3895	338	3	13257	66	Fleischbratling roh auch tiefgefroren	\N
3896	338	3	14281	67	Hähnchenfleischspieß roh auch tiefgefroren	\N
3897	339	3	1648	1	Frikadelle gegart	\N
3898	339	3	1649	2	Netzkotelett gegart	\N
3899	339	3	1650	3	Suppenkloß gegart	\N
3900	339	3	1651	4	Königsberger Klops gegart	\N
3901	339	3	1652	5	Leberknödel gegart	\N
3902	339	3	1653	6	Leberspätzle gegart	\N
3903	339	3	1654	7	Deutsches Beefsteak gegart	\N
3904	339	3	1655	8	Hackbraten gegart	\N
3905	339	3	1656	9	Burger gegart	\N
3906	339	3	1657	10	Cevapcici gegart	\N
3907	339	3	1658	11	Fleischspieß gegart	\N
3908	339	3	1659	12	Schaschlik gegart	\N
3909	339	3	1660	13	Hacksteak gegart	\N
3910	339	3	1661	14	Hackfleischfüllung auch im Fleischstück gegart	\N
3911	339	3	1662	15	Nierenspieß gegart	\N
3912	339	3	1663	16	Fleischklößchen gegart	\N
3913	339	3	1664	17	Markklößchen gegart	\N
3914	339	3	1665	18	Fleischbrät gegart	\N
3915	339	3	1666	19	Boulette gegart	\N
3916	339	3	1667	20	Geschnetzeltes Fleisch gegart	\N
3917	339	3	1668	21	Geflügelfleischspieß gegart	\N
3918	339	3	1669	22	Döner Kebab Hackspieß oder gleichartige Produkte gegart	\N
3919	339	3	1670	23	Gyros gegart	\N
3920	339	3	1671	24	Wurstklops gegart	\N
3921	339	3	1672	25	Bratwurst Hackfleischerzeugnis gegart	\N
3922	339	3	1673	26	Geflügelhacksteak gegart	\N
3923	339	3	1674	27	Geschnetzeltes Geflügelfleisch gegart	\N
3924	339	3	1675	28	Geschnetzeltes vom Schwein gegart	\N
3925	339	3	13258	30	Fleischbratling gegart	\N
3926	339	3	13259	31	Bratrolle (Hackfleischerzeugnis) gegart	\N
3927	339	3	13780	32	Hackfleischerzeugnis gegart mit Füllung	\N
3928	341	3	1678	1	Brust Gans gepökelt roh geräuchert	\N
3929	341	3	1679	2	Schenkel Gans gepökelt roh geräuchert	\N
3930	341	3	1680	3	Brust Pute gepökelt roh geräuchert	\N
3931	341	3	1681	4	Schenkel Pute gepökelt roh geräuchert	\N
3932	342	3	1683	1	Putenformfleisch gepökelt gegart ungeräuchert	\N
3933	342	3	1684	2	Corned Turkey	\N
3934	342	3	1685	3	Hähnchenbrust in Aspik	\N
3935	343	3	1687	1	Huhn gepökelt gegart geräuchert	\N
3936	343	3	1688	2	Brust Pute gepökelt gegart geräuchert	\N
3937	343	3	1689	3	Brust Gans gepökelt gegart geräuchert	\N
3938	343	3	1690	4	Putenformfleisch gepökelt gegart geräuchert	\N
3939	343	3	1691	5	Brust Hähnchen gepökelt gegart geräuchert	\N
3940	344	3	1693	1	Frikassee Huhn Konserve	\N
3941	344	3	1694	2	Huhn ganz Konserve	\N
3942	344	3	1695	3	Huhn Fleischteilstück Konserve	\N
3943	344	3	1696	4	Corned Turkey Konserve	\N
3944	344	3	1697	5	Perlhuhn ganz Konserve	\N
3945	344	3	1698	6	Pute Fleischteilstück Konserve	\N
3946	344	3	13260	7	Putenfleisch im eigenen Saft Konserve	\N
3947	344	3	13261	8	Hähnchenfleisch im eigenen Saft Konserve	\N
3948	344	3	13262	9	Geflügelgulasch Konserve	\N
3949	346	3	1701	1	Huhn gegart	\N
3950	346	3	1702	3	Fleischteilstück Huhn gegart	\N
3951	346	3	1703	4	Frikassee Huhn gegart	\N
3952	346	3	1704	5	Innereien Huhn gegart	\N
3953	346	3	1705	6	Pute gegart	\N
3954	346	3	1706	7	Fleischteilstück Pute gegart	\N
3955	346	3	1707	8	Innereien Pute gegart	\N
3956	346	3	1708	9	Frikassee Pute gegart	\N
3957	346	3	1709	10	Gans gegart	\N
3958	346	3	1710	11	Fleischteilstück Gans gegart	\N
3959	346	3	1711	12	Gänseklein gegart	\N
3960	346	3	1712	13	Ente gegart	\N
3961	346	3	1713	14	Perlhuhn gegart	\N
3962	346	3	1714	15	Taube gegart	\N
3963	346	3	1715	16	Schnitzel Hähnchen auch paniert gegart	\N
3964	346	3	1716	17	Brust Hähnchen auch paniert gegart	\N
3965	346	3	1717	18	Schnitzel Pute auch paniert gegart	\N
3966	346	3	1718	19	Brust Pute auch paniert gegart	\N
3967	346	3	1719	20	Fleischstück Hähnchen/Huhn zusammengesetzt geformt paniert gegart	\N
3968	346	3	1720	21	Fleischstück Pute zusammengesetzt geformt paniert gegart	\N
3969	346	3	13263	22	Fleischstücke Hähnchen gegart paniert	\N
3970	346	3	13264	23	Fleischstücke Pute gegart paniert	\N
3971	346	3	13265	24	Geflügelfleisch zusammengesetzt geformt paniert gegart auch tiefgefroren	\N
3972	348	3	1723	1	Fleischteilstück Reh gepökelt roh geräuchert	\N
3973	348	3	1724	2	Fleischteilstück Hirsch gepökelt roh geräuchert	\N
3974	348	3	1725	3	Fleischteilstück Wildschwein gepökelt roh geräuchert	\N
3975	348	3	1726	4	Fleischteilstück Elch gepökelt roh geräuchert	\N
3976	348	3	1727	5	Fleischteilstück Ren gepökelt roh geräuchert	\N
3977	348	3	1728	6	Fleischteilstück Bär gepökelt roh geräuchert	\N
3978	350	3	1731	1	Fleischteilstück Reh gepökelt gegart geräuchert	\N
3979	350	3	1732	2	Fleischteilstück Hirsch gepökelt gegart geräuchert	\N
3980	350	3	1733	3	Fleischteilstück Elch gepökelt gegart geräuchert	\N
3981	350	3	1734	4	Fleischteilstück Ren gepökelt gegart geräuchert	\N
3982	350	3	1735	5	Fleischteilstück Wildschwein gepökelt gegart geräuchert	\N
3983	351	3	1737	1	Braten Reh Konserve	\N
3984	351	3	1738	2	Ragout Reh Konserve	\N
3985	351	3	1739	3	Gulasch Reh Konserve	\N
3986	351	3	1740	4	Braten Hirsch Konserve	\N
3987	351	3	1741	5	Ragout Hirsch Konserve	\N
3988	351	3	1742	6	Gulasch Hirsch Konserve	\N
3989	351	3	1743	7	Braten Wildschwein Konserve	\N
3990	351	3	1744	8	Ragout Wildschwein Konserve	\N
3991	351	3	1745	9	Gulasch Wildschwein Konserve	\N
3992	351	3	1746	10	Braten Hase Konserve	\N
3993	351	3	1747	11	Hasenpfeffer Konserve	\N
3994	351	3	1748	12	Ragout Hase Konserve	\N
3995	351	3	1749	13	Braten Wildkaninchen Konserve	\N
3996	353	3	1752	1	Fleischteilstück Reh gegart	\N
3997	353	3	1753	2	Ragout Reh gegart	\N
3998	353	3	1754	3	Gulasch Reh gegart	\N
3999	353	3	1755	4	Fleischteilstück Hirsch gegart	\N
4000	353	3	1756	5	Gulasch Hirsch gegart	\N
4001	353	3	1757	6	Fleischteilstück Wildschwein gegart	\N
4002	353	3	1758	7	Ragout Wildschwein gegart	\N
4003	353	3	1759	8	Gulasch Wildschwein gegart	\N
4004	353	3	1760	9	Fleischteilstück Ren gegart	\N
4005	353	3	1761	10	Gulasch Ren gegart	\N
4006	353	3	1762	11	Fleischteilstück Elch gegart	\N
4007	353	3	1763	12	Ragout Hirsch gegart	\N
4008	353	3	1764	13	Fleischteilstück Hase gegart	\N
4009	353	3	1765	14	Hasenpfeffer gegart	\N
4010	353	3	1766	15	Wildkaninchen gegart	\N
4011	353	3	1767	16	Wildgeflügel gegart	\N
4012	353	3	1768	17	Rebhuhn gegart	\N
4013	353	3	1769	18	Fasan gegart	\N
4014	353	3	1770	19	Wachtel gegart	\N
4015	353	3	1771	20	Schnepfe gegart	\N
4016	353	3	1772	21	Fleischteilstück Antilope gegart	\N
4017	353	3	1773	22	Fleischteilstück Gazelle gegart	\N
4018	353	3	1774	23	Fleischteilstück Springbock gegart	\N
4019	353	3	1775	24	Fleischteilstück Känguruh gegart	\N
4020	355	3	13266	1	Pferd Pökelware roh geräuchert	\N
4021	361	3	1784	1	Fleischteilstück Ziege gegart	\N
4022	361	3	1785	2	Gulasch/Ragout Ziege gegart	\N
4023	361	3	1786	3	Fleischteilstück Ziegenkitz gegart	\N
4024	361	3	1787	4	Fleischteilstück Fohlen/Pferd gegart	\N
4025	361	3	1788	5	Innereien Fohlen/Pferd gegart	\N
4026	361	3	1789	6	Hauskaninchen gegart	\N
4027	361	3	1790	7	Sauerbraten Fohlen/Pferd gegart	\N
4028	362	3	1792	1	Weichseparatorenfleisch Rind	\N
4029	362	3	1793	2	Hartseparatorenfleisch Rind	\N
4030	362	3	1794	3	Weichseparatorenfleisch Schwein	\N
4031	362	3	1795	4	Hartseparatorenfleisch Schwein	\N
4032	362	3	1796	5	Weichseparatorenfleisch Geflügel	\N
4033	362	3	1797	6	Hartseparatorenfleisch Geflügel	\N
4034	362	3	1798	7	Knochenputz Schwein	\N
4035	362	3	1799	8	Knochenputz Rind	\N
4036	362	3	1800	99	Separatorenfleischmischung	\N
4037	367	3	1806	1	Mett Konserve	\N
4038	367	3	1807	2	Frikadelle Konserve	\N
4039	367	3	1808	3	Burger Konserve	\N
4040	367	3	1809	4	Cevapcici Konserve	\N
4041	367	3	1810	5	Fleischspieß Konserve	\N
4042	367	3	1811	6	Schaschlik Konserve	\N
4043	367	3	1812	7	Königsberger Klops Konserve	\N
4044	367	3	1813	8	Leberknödel Konserve	\N
4045	367	3	1814	9	Fleischklößchen Konserve	\N
4046	368	3	1817	1	Walfleischerzeugnis	\N
4047	369	3	1819	1	Sauerbraten roh	\N
4048	369	3	1820	2	Fleischteilstück Rind mariniert roh	\N
4049	369	3	1821	3	Roulade Rind roh küchenmäßig vorber. auch tiefgefroren	\N
4050	370	3	1823	1	Schnitzel Kalb roh paniert auch tiefgefroren	\N
4051	370	3	1824	2	Kotelett Kalb roh paniert auch tiefgefroren	\N
4052	370	3	1825	3	Fleischteilstück Kalb mariniert	\N
4053	371	3	1827	1	Kotelett Schwein roh paniert auch tiefgefroren	\N
4054	371	3	1828	2	Schnitzel Schwein roh paniert auch tiefgefroren	\N
4055	371	3	1829	3	Bauch Schwein roh paniert auch tiefgefroren	\N
4056	371	3	1830	4	Fleischteilstück Schwein mariniert	\N
4057	371	3	1831	5	Fleischstück Schwein roh zusammengesetzt geformt paniert auch tiefgefroren	\N
4058	371	3	1832	6	Roulade Schwein roh küchenmäßig vorber. auch tiefgefroren	\N
4059	371	3	1833	7	Lende Schwein gefüllt roh auch tiefgefroren	\N
4060	372	3	1835	1	Schnitzel Hähnchen roh paniert auch tiefgefroren	\N
4061	372	3	1836	2	Schenkel Hähnchen/Huhn roh paniert auch tiefgefroren	\N
4062	372	3	1837	3	Brust Hähnchen roh paniert auch tiefgefroren	\N
4063	372	3	1838	4	Schnitzel Pute roh paniert auch tiefgefroren	\N
4064	372	3	1839	5	Brust Pute roh paniert auch tiefgefroren	\N
4065	372	3	1840	6	Fleischstück Pute roh zusammengesetzt geformt paniert auch tiefgefroren	\N
4066	372	3	1841	7	Fleischstück Hähnchen/Huhn roh zusammengesetzt geformt paniert auch tiefge.	\N
4067	372	3	1842	8	Brust Hähnchen gewürzt	\N
4068	372	3	1843	9	Roulade Pute roh küchenmäßig vorber. auch tiefgefroren	\N
4069	372	3	1844	10	Cordon bleu Pute roh auch tiefgefroren	\N
4070	372	3	1845	11	Schenkel Hähnchen roh gewürzt auch tiefgefroren	\N
4071	372	3	13267	12	Fleischteilstück Hähnchen roh küchenm. vorber. auch tiefgefroren	\N
4072	372	3	13268	13	Fleischteilstück Pute roh küchenm. vorber. auch tiefgefroren	\N
4073	372	3	13269	14	Fleischteilstück Hähnchen roh paniert auch tiefgefroren	\N
4074	372	3	13270	15	Fleischteilstück Pute roh paniert auch tiefgefroren	\N
4075	372	3	13271	16	Brust Pute roh küchenm. vorb. auch tiefgefroren	\N
4076	372	3	13272	17	Putenrollbraten roh küchenm. vorb. auch tiefgefroren	\N
4077	372	3	13273	18	Geflügelfleisch roh zusammengesetzt geformt paniert auch tiefgefroren	\N
4078	372	3	13274	19	Fleischteilstücke Strauß küchenm. vorb. roh auch tiefgefroren	\N
4079	373	3	1847	1	Fleischteilstück Lamm/Schaf mariniert	\N
4080	374	3	1849	1	Ragout Lamm/Schaf Konserve	\N
4081	375	3	1851	1	Ragout Fohlen/Pferd Konserve	\N
4082	376	3	1853	1	Sauerbraten Fohlen/Pferd roh	\N
4083	376	3	1854	2	Fleischteilstück Fohlen/Pferd mariniert roh	\N
4084	377	3	1856	1	Hasenrücken küchenmäßig vorbereitet	\N
4085	377	3	1857	2	Hasenschenkel küchenmäßig vorbereitet	\N
4086	380	3	1860	1	Salami ungarische Art 2.211.01	\N
4087	380	3	1861	2	Salami italienische	\N
4088	380	3	1862	3	Schinkenplockwurst 2.211.03	\N
4089	380	3	1863	4	Schinkenwurst roh 2.211.03	\N
4090	380	3	1864	5	Salami Ia 2.211.04	\N
4091	380	3	1865	6	Salami Kaliber unter 70 mm 2.211.05	\N
4092	380	3	1866	7	Katenrauchwurst 2.211.05 Rohwurst schnittfest	\N
4093	380	3	1867	8	Mettwurst 2.211.05 Rohwurst schnittfest	\N
4094	380	3	1868	9	Salametti 2.211.05 Rohwurst schnittfest	\N
4095	380	3	1869	10	Schlackwurst Kaliber unter 70 mm 2.211.06 Rohwurst schnittfest	\N
4096	380	3	1870	11	Cervelatwurst Ia 2.211.07	\N
4097	380	3	1871	12	Cervelatwurst Kaliber unter 70 mm 2.211.08	\N
4098	380	3	1872	13	Schinkenmettwurst 2.211.09	\N
4099	380	3	1873	14	Mettwurst westfälische grob 2.211.10 Rohwurst schnittfest	\N
4100	380	3	1874	15	Mettwurst luftgetrocknet 2.211.11	\N
4101	380	3	1875	16	Aalrauchmettwurst 2.211.12 Rohwurst schnittfest	\N
4102	380	3	1876	17	Plockwurst 2.211.13	\N
4103	380	3	1877	18	Plockwurst einfach 2.211.14	\N
4104	380	3	1878	19	Knoblauchwurst 2.211.15 Rohwurst schnittfest	\N
4105	380	3	1879	20	Krakauer rohe 2.211.15 Rohwurst schnittfest	\N
4106	380	3	1880	21	Touristenwurst rohe 2.211.15 Rohwurst schnittfest	\N
4107	380	3	1881	22	Mettwurst in Enden 2.211.15 Rohwurst schnittfest	\N
4108	380	3	1882	23	Räucherenden 2.211.15 Rohwurst schnittfest	\N
4109	380	3	1883	24	Colbassa 2.211.15 Rohwurst schnittfest	\N
4110	380	3	1884	25	Knoblauchwurst einfach 2.211.16 Rohwurst schnittfest	\N
4111	380	3	1885	26	Touristenwurst einfach roh 2.211.16 Rohwurst schnittfest	\N
4112	380	3	1886	27	Räucherenden einfach 2.211.16 Rohwurst schnittfest	\N
4113	380	3	1887	28	Mettwurst in Enden einfach 2.211.16 Rohwurst schnittfest	\N
4114	380	3	1888	29	Polnische 2.211.17 Rohwurst schnittfest	\N
4115	380	3	1889	30	Knacker Berliner 2.211.17 Rohwurst schnittfest	\N
4116	380	3	1890	31	Bauernbratwurst 2.211.17 Rohwurst schnittfest	\N
4117	380	3	1891	32	Geräucherte Bratwurst 2.211.17 Rohwurst schnittfest	\N
4118	380	3	1892	33	Debrecziner 2.211.17 Rohwurst schnittfest	\N
4119	380	3	1893	34	Landjäger 2.211.18 Rohwurst schnittfest	\N
4120	380	3	1894	35	Pepperoni 2.211.18 Rohwurst schnittfest	\N
4121	380	3	1895	36	Salami Kaliber über 70 mm 2.211.05	\N
4122	380	3	1896	37	Katenrauchwurst Kaliber über 70 mm 2.211.05	\N
4123	380	3	1897	38	Schlackwurst Kaliber über 70 mm 2.211.06	\N
4124	380	3	1898	39	Cervelatwurst Kaliber über 70 mm 2.211.08	\N
4125	380	3	1899	40	Mettwurst Ia Rohwurst schnittfest	\N
4126	380	3	1900	41	Schinkenwürstchen Rohwurst schnittfest	\N
4127	380	3	1901	42	Salami ungarische	\N
4128	380	3	1902	43	Salami Rind Kaliber unter 70 mm	\N
4129	380	3	1903	44	Salami Rind Kaliber über 70 mm	\N
4130	380	3	1904	45	Rinderwurst 2.211.05 Rohwurst schnittfest	\N
4131	380	3	1905	46	Sommerwurst 2.211.09 Rohwurst schnittfest	\N
4132	380	3	1906	47	Cabanossi Kabanossi 2.211.15 Rohwurst schnittfest	\N
4133	380	3	1907	48	Dürre Runde 2.211.17 Rohwurst schnittfest	\N
4134	380	3	1908	49	Kaminwurzen 2.211.17 Rohwurst schnittfest	\N
4135	380	3	1909	50	Bauernknacker 2.211.17 Rohwurst schnittfest	\N
4136	380	3	1910	51	Bierknacker 2.211.17 Rohwurst schnittfest	\N
4137	380	3	1911	52	Knobelinchen 2.211.17 Rohwurst schnittfest	\N
4138	380	3	1912	53	Pfefferbeißer 2.211.17 Rohwurst schnittfest	\N
4139	380	3	1913	54	Weinbeißer 2.211.17 Rohwurst schnittfest	\N
4140	380	3	1914	55	Bauernschübling 2.211.18 Rohwurst schnittfest	\N
4141	380	3	1915	56	Rauchpeitschen 2.211.18 Rohwurst schnittfest	\N
4142	380	3	1916	57	Karpatensalami Rohwurst schnittfest	\N
4143	380	3	1917	58	Pfeffersalami Rohwurst schnittfest	\N
4144	380	3	1918	59	Pizzasalami Rohwurst schnittfest	\N
4145	380	3	1919	60	Rohwurst mit Einlagen schnittfest	\N
4146	380	3	1920	61	Salami lufttrocken Rohwurst schnittfest	\N
4147	380	3	1921	62	Salami/Salami Ia mit Gelatinehülle	\N
4148	380	3	1922	63	Rohwurst Chorizoerzeugnisse Salchichon	\N
4149	380	3	1923	64	Salami gegart/Salami heißgeräuchert	\N
4150	381	3	1925	1	Rohwurst Hirsch schnittfest	\N
4151	381	3	1926	2	Rohwurst Pute schnittfest	\N
4152	381	3	1927	3	Rohwurst Hähnchen schnittfest	\N
4153	381	3	1928	4	Rohwurst Pferd schnittfest	\N
4154	381	3	1929	5	Rohwurst Lamm/Schaf schnittfest	\N
4155	381	3	1930	6	Rohwurst Geflügel schnittfest	\N
4156	381	3	1931	7	Rohwurst Wildschwein schnittfest	\N
4157	381	3	1932	8	Rohwurst Gemse schnittfest	\N
4158	382	3	1934	1	Teewurst 2.212.1	\N
4159	382	3	1935	2	Teewurst Rügenwalder Art 2.212.1	\N
4160	382	3	1936	3	Teewurst grob 2.212.1	\N
4161	382	3	1937	4	Mettwurst Ia 2.212.1 Rohwurst streichfähig	\N
4162	382	3	1938	5	Mettwurst fein zerkleinert 2.212.2 Rohwurst streichfähig	\N
4163	382	3	1939	6	Mettwurst Braunschweiger 2.212.2 Rohwurst streichfähig	\N
4164	382	3	1940	7	Mettwurst grob 2.212.3 Rohwurst streichfähig	\N
4165	382	3	1941	8	Zwiebelmettwurst 2.212.3 Rohwurst streichfähig	\N
4166	382	3	1942	9	Schmierwurst 2.212.4 Rohwurst streichfähig	\N
4167	382	3	1943	10	Mettwurst einfach 2.212.5 Rohwurst streichfähig	\N
4168	382	3	1944	11	Mettwurst frisch Rohwurst streichfähig	\N
4169	382	3	1945	12	Vesperwurst Rohwurst streichfähig	\N
4170	382	3	1946	13	Kartoffelwurst Rohwurst streichfähig	\N
4171	382	3	1947	14	Bregenwurst Rohwurst streichfähig	\N
4172	382	3	1948	15	Schmorwurst Rohwurst streichfähig	\N
4173	382	3	1949	16	Frühstückswurst Rohwurst streichfähig	\N
4174	382	3	1950	17	Pfeffersäckchen Rohwurst streichfähig	\N
4175	382	3	1951	18	Aalrauchmettwurst Rohwurst streichfähig	\N
4176	382	3	1952	19	Teewurst Rügenwalder Rohwurst streichfähig	\N
4177	382	3	1953	20	Hofer Rindfleischwurst Rohwurst streichfähig	\N
4178	382	3	13280	21	Bauernbratwurst nach Art einer frischen Mettwurst Rohwurst streichfähig	\N
4179	383	3	1955	1	Rohwurst Hirsch streichfähig	\N
4180	383	3	1956	2	Rohwurst Pute streichfähig	\N
4181	383	3	1957	3	Rohwurst Hähnchen streichfähig	\N
4182	383	3	1958	4	Rohwurst Pferd streichfähig	\N
4183	383	3	1959	5	Rohwurst Lamm/Schaf streichfähig	\N
4184	383	3	1960	6	Rohwurst Geflügel streichfähig	\N
4185	383	3	1961	7	Rohwurst Wildschwein streichfähig	\N
4186	383	3	1962	8	Rohwurst Gemse streichfähig	\N
4187	384	3	1964	1	Würstchen nach Frankfurter Art 2.221.01 Brühwürstchen umgerötet	\N
4188	384	3	1965	2	Schinkenwürstchen 2.221.01 Brühwürstchen umgerötet	\N
4189	384	3	1966	3	Delikatesswürstchen 2.221.02 Brühwürstchen umgerötet	\N
4190	384	3	1967	4	Wiener 2.221.03 Brühwürstchen umgerötet	\N
4191	384	3	1968	5	Bockwurst 2.221.03 Brühwürstchen umgerötet	\N
4192	384	3	1969	6	Würstchen 2.221.03 Brühwürstchen umgerötet	\N
4193	384	3	1970	7	Saftwürstchen 2.221.03 Brühwürstchen umgerötet	\N
4194	384	3	1971	8	Cocktailwürstchen 2.221.03 Brühwürstchen umgerötet	\N
4195	384	3	1972	9	Halberstädter Würstchen 2.221.03 Brühwürstchen umgerötet	\N
4196	384	3	1973	10	Dünne 2.221.03 Brühwürstchen umgerötet	\N
4197	384	3	1974	11	Dampfwurst Münchner 2.221.03 Brühwürstchen umgerötet	\N
4198	384	3	1975	12	Saitenwürstchen 2.221.03 Brühwürstchen umgerötet	\N
4199	384	3	1976	13	Bouillonwürstchen 2.221.03 Brühwürstchen umgerötet	\N
4200	384	3	1977	14	Fleischwürstchen 2.221.03 Brühwürstchen umgerötet	\N
4201	384	3	1978	15	Jauersche 2.221.03 Brühwürstchen umgerötet	\N
4202	384	3	1979	16	Pfälzer 2.221.04 Brühwürstchen umgerötet	\N
4203	384	3	1980	17	Augsburger 2.221.04 Brühwürstchen umgerötet	\N
4204	384	3	1981	18	Regensburger 2.221.04 Brühwürstchen umgerötet	\N
4205	384	3	1982	19	Debrecziner 2.221.04 Brühwürstchen umgerötet	\N
4206	384	3	1983	20	Jagdwürstchen 2.221.04 Brühwürstchen umgerötet	\N
4207	384	3	1984	21	Brühpolnische 2.221.04 Brühwürstchen umgerötet	\N
4208	384	3	1985	22	Bauernwürstchen 2.221.04 Brühwürstchen umgerötet	\N
4209	384	3	1986	23	Dicke 2.221.05 Brühwürstchen umgerötet	\N
4210	384	3	1987	24	Knackwurst 2.221.05 Brühwürstchen umgerötet	\N
4211	384	3	1988	25	Rote 2.221.05 Brühwürstchen umgerötet	\N
4212	384	3	1989	26	Servela 2.221.05 Brühwürstchen umgerötet	\N
4213	384	3	1990	27	Klöpfer 2.221.05 Brühwürstchen umgerötet	\N
4214	384	3	1991	28	Rindswurst 2.221.05 Brühwürstchen umgerötet	\N
4215	384	3	1992	29	Schüblinge 2.221.05 Brühwürstchen umgerötet	\N
4216	384	3	1993	30	Knacker einfach 2.221.06 Brühwürstchen umgerötet	\N
4217	384	3	1994	31	Schüblinge einfach 2.221.06 Brühwürstchen umgerötet	\N
4218	384	3	1995	32	Servela einfach 2.221.06 Brühwürstchen umgerötet	\N
4219	384	3	1996	33	Klöpfer einfach 2.221.06 Brühwürstchen umgerötet	\N
4220	384	3	1997	34	Rote einfach 2.221.06 Brühwürstchen umgerötet	\N
4221	384	3	1998	35	Berliner Dampfwurst 2.221.10 Brühwürstchen umgerötet	\N
4222	384	3	1999	36	Frankfurter Würstchen Brühwürstchen umgerötet	\N
4223	384	3	2000	37	Prager Würstchen Brühwürstchen umgerötet	\N
4224	385	3	2002	1	Schinkenwürstchen Geflügel Brühwürstchen umgerötet	\N
4225	385	3	2003	2	Delikatesswürstchen Geflügel Brühwürstchen umgerötet	\N
4226	385	3	2004	3	Wiener Geflügel Brühwürstchen umgerötet	\N
4227	385	3	2005	4	Bockwurst Geflügel Brühwürstchen umgerötet	\N
4228	385	3	2006	5	Fleischwürstchen Geflügel Brühwürstchen umgerötet	\N
4229	385	3	2007	6	Pfälzer Geflügel Brühwürstchen umgerötet	\N
4230	385	3	2008	7	Regensburger Geflügel Brühwürstchen umgerötet	\N
4231	385	3	2009	8	Debrecziner Geflügel Brühwürstchen umgerötet	\N
4232	385	3	2010	9	Dicke Geflügel Brühwürstchen umgerötet	\N
4233	385	3	2011	10	Knackwurst Geflügel Brühwürstchen umgerötet	\N
4234	385	3	2012	11	Schüblinge Geflügel Brühwürstchen umgerötet	\N
4235	385	3	2013	12	Dicke Pferd Brühwürstchen umgerötet	\N
4236	385	3	2014	13	Knackwurst Pferd Brühwürstchen umgerötet	\N
4237	385	3	2015	14	Brühwurst einfach Pferd Brühwürstchen umgerötet	\N
4238	385	3	2016	15	Brühwürstchen Schaf umgerötet	\N
4239	385	3	2017	16	Brühwürstchen Kaninchen umgerötet	\N
4240	386	3	2019	1	Kalbsbratwurst 2.221.07 Brühwürstchen nicht umgerötet	\N
4241	386	3	2020	2	Weißwurst 2.221.07 Brühwürstchen nicht umgerötet	\N
4242	386	3	2021	3	Wollwurst geschwollene 2.221.07 Brühwürstchen nicht umgerötet	\N
4243	386	3	2022	4	Münchner Weißwurst 2.221.09 Brühwürstchen nicht umgerötet	\N
4244	386	3	2023	5	Stockwurst 2.221.10 Brühwürstchen nicht umgerötet	\N
4245	386	3	2024	6	Weißwurst einfach 2.221.10 Brühwürstchen nicht umgerötet	\N
4246	386	3	2025	7	Rindsbratwurst Brühwürstchen nicht umgerötet	\N
4247	386	3	2026	8	Lungenwurst 2.221.10 Brühwürstchen nicht umgerötet	\N
4248	386	3	2027	9	Thüringer Bratwurst 2.221.11 Brühwürstchen nicht umgerötet	\N
4249	386	3	2028	10	Kümmelwurst 2.221.10 Brühwürstchen nicht umgerötet	\N
5170	493	3	3037	5	Meerforelle Filet	\N
4250	386	3	2029	11	Bratwurst grob 2.221.11 Brühwürstchen nicht umgerötet	\N
4251	386	3	2030	12	Schweinsbratwürstchen 2.221.11 Brühwürstchen nicht umgerötet	\N
4252	386	3	2031	13	Fränkische Bratwurst 2.221.11 Brühwürstchen nicht umgerötet	\N
4253	386	3	2032	14	Pfälzer Bratwurst 2.221.11 Brühwürstchen nicht umgerötet	\N
4254	386	3	2033	15	Hessische Bratwurst 2.221.11 Brühwürstchen nicht umgerötet	\N
4255	386	3	2034	16	Rostbratwurst 2.221.11 Brühwürstchen nicht umgerötet	\N
4256	386	3	2035	17	Nürnberger Rostbratwurst 2.221.11 Brühwürstchen nicht umgerötet	\N
4257	386	3	2036	18	Treuchtlinger 2.221.12 Brühwürstchen nicht umgerötet	\N
4258	386	3	2037	19	Bratwurst feinzerkleinert 2.221.12 Brühwürstchen nicht umgerötet	\N
4259	386	3	2038	20	Rheinische Bratwurst 2.221.12 Brühwürstchen nicht umgerötet	\N
4260	386	3	2039	21	Schlesische Bratwurst 2.221.12 Brühwürstchen nicht umgerötet	\N
4261	386	3	2040	22	Grillwürstchen Brühwürstchen nicht umgerötet	\N
4262	386	3	2041	23	Grillwurst 2.221.11 Brühwürstchen nicht umgerötet	\N
4263	386	3	2042	24	Griller Brühwürstchen nicht umgerötet	\N
4264	387	3	2044	1	Wollwurst Geflügel Brühwürstchen nicht umgerötet	\N
4265	387	3	2045	2	Bratwurst Geflügel Brühwürstchen nicht umgerötet	\N
4266	388	3	2047	1	Lyoner 2.222.1 Brühwurst umgerötet feingekuttert	\N
4267	388	3	2048	2	Schinkenwurst 2.222.1 Brühwurst umgerötet feingekuttert	\N
4268	388	3	2049	3	Mortadella Norddeutsche 2.222.1 Brühwurst umgerötet feingekuttert	\N
4269	388	3	2050	4	Pariser Fleischwurst 2.222.1 Brühwurst umgerötet feingekuttert	\N
4270	388	3	2051	5	Rheinische Fleischwurst 2.222.1 Brühwurst umgerötet feingekuttert	\N
4271	388	3	2052	6	Frankfurter Fleischwurst 2.222.1 Brühwurst umgerötet feingekuttert	\N
4272	388	3	2053	7	Kalbfleischwurst 2.222.1 Brühwurst umgerötet feingekuttert	\N
4273	388	3	2054	8	Kalbfleischkäse 2.222.1 Brühwurst umgerötet feingekuttert	\N
4274	388	3	2055	9	Breslauer 2.222.1 Brühwurst umgerötet feingekuttert	\N
4275	388	3	2056	10	Fleischwurst 2.222.2 Brühwurst umgerötet feingekuttert	\N
4276	388	3	2057	11	Stadtwurst 2.222.2 Brühwurst umgerötet feingekuttert	\N
4277	388	3	2058	12	Fleischkäse 2.222.2 Brühwurst umgerötet feingekuttert	\N
4278	388	3	2059	13	Fleischwurst einfach 2.222.3 Brühwurst umgerötet feingekuttert	\N
4279	388	3	2060	14	Stadtwurst einfach 2.222.3 Brühwurst umgerötet feingekuttert	\N
4280	388	3	2061	15	Fleischkäse einfach 2.222.3 Brühwurst umgerötet feingekuttert	\N
4281	388	3	2062	16	Leberkäse 2.222.2 Brühwurst umgerötet feingekuttert	\N
4282	388	3	2063	17	Fleischsalatgrundlage 2.222.5 Brühwurst umgerötet feingekuttert	\N
4283	388	3	2064	18	Knoblauchwurst 2.222.2 Brühwurst umgerötet feingekuttert	\N
4284	388	3	2065	19	Currywurst Brühwurst umgerötet feingekuttert	\N
4285	389	3	2067	1	Lyoner Geflügel Brühwurst umgerötet feingekuttert	\N
4286	389	3	2068	2	Schinkenwurst Geflügel Brühwurst umgerötet feingekuttert	\N
4287	389	3	2069	3	Fleischwurst Geflügel Brühwurst umgerötet feingekuttert	\N
4288	389	3	2070	4	Stadtwurst Geflügel Brühwurst umgerötet feingekuttert	\N
4289	389	3	2071	5	Fleischkäse Geflügel Brühwurst umgerötet feingekuttert	\N
4290	389	3	2072	6	Fleischwurst einf. Geflügel Brühwurst umgerötet feingekuttert	\N
4291	389	3	2073	7	Stadtwurst einf. Geflügel Brühwurst umgerötet feingekuttert	\N
4292	389	3	2074	8	Fleischkäse einf. Geflügel Brühwurst umgerötet feingekuttert	\N
4293	389	3	2075	9	Leberkäse Geflügel Brühwurst umgerötet feingekuttert	\N
4294	389	3	2076	10	Lyoner Pferd Brühwurst umgerötet feingekuttert	\N
4295	389	3	2077	11	Fleischwurst Pferd Brühwurst umgerötet feingekuttert	\N
4296	389	3	2078	12	Leberkäse Pferd Brühwurst umgerötet feingekuttert	\N
4297	389	3	2079	13	Fleischwurst einf. Pferd Brühwurst umgerötet feingekuttert	\N
4298	389	3	2080	14	Mortadella Geflügel Brühwurst umgerötet feingekuttert	\N
4299	390	3	2082	1	Bierwurst Ia 2.223.1 Brühwurst umgerötet grob	\N
4300	390	3	2083	2	Bayerische Bierwurst 2.223.1 Brühwurst umgerötet grob	\N
4301	390	3	2084	3	Göttinger 2.223.1 Brühwurst umgerötet grob	\N
4302	390	3	2085	4	Blasenwurst 2.223.1 Brühwurst umgerötet grob	\N
4303	390	3	2086	5	Kochsalami 2.223.1 Brühwurst umgerötet grob	\N
4304	390	3	2087	6	Tiroler 2.223.1 Brühwurst umgerötet grob	\N
4305	390	3	2088	7	Krakauer 2.223.1 Brühwurst umgerötet grob	\N
4306	390	3	2089	8	Jagdwurst süddeutsche Art 2.223.1 Brühwurst umgerötet grob	\N
4307	390	3	2090	9	Jagdwurst norddeutsche Art 2.223.2 Brühwurst umgerötet grob	\N
4308	390	3	2091	10	Schinkenwurst grob 2.223.2 Brühwurst umgerötet grob	\N
4309	390	3	2092	11	Schweinsbrust gefüllt 2.223.2 Brühwurst umgerötet grob	\N
4310	390	3	2093	12	Schweinsfuß gefüllt 2.223.2 Brühwurst umgerötet grob	\N
4311	390	3	2094	13	Schweinskopf gefüllt 2.223.2 Brühwurst umgerötet grob	\N
4312	390	3	2095	14	Lyoner grob 2.223.2 Brühwurst umgerötet grob	\N
4313	390	3	2096	15	Stuttgarter 2.223.2 Brühwurst umgerötet grob	\N
4314	390	3	2097	16	Bierwurst 2.223.2 Brühwurst umgerötet grob	\N
4315	390	3	2098	17	Hildesheimer 2.223.2 Brühwurst umgerötet grob	\N
4316	390	3	2099	18	Grobe Stadtwurst 2.223.2 Brühwurst umgerötet grob	\N
4317	390	3	2100	19	Nürnberger Stadtwurst 2.223.2 Brühwurst umgerötet grob	\N
4318	390	3	2101	20	Frühstücksfleisch 2.223.2 Brühwurst umgerötet grob	\N
4319	390	3	2102	21	Schweinskäse 2.223.2 Brühwurst umgerötet grob	\N
4320	390	3	2103	22	Stuttgarter Leberkäse 2.223.2 Brühwurst umgerötet grob	\N
4321	390	3	2104	23	Fleischkäse grob 2.223.3 Brühwurst umgerötet grob	\N
4322	390	3	2105	24	Fleischkäse roter 2.223.3 Brühwurst umgerötet grob	\N
4323	390	3	2106	25	Fleischwurst grob 2.223.3 Brühwurst umgerötet grob	\N
4324	390	3	2107	26	Knoblauchwurst gebrüht 2.223.4 Brühwurst umgerötet grob	\N
4325	390	3	2108	27	Krakauer gebrüht 2.223.4 Brühwurst umgerötet grob	\N
5171	493	3	3038	6	Meerforelle Stück	\N
4326	390	3	2109	28	Cabanossi Kabanossi 2.223.4 Brühwurst umgerötet grob	\N
4327	390	3	2110	29	Touristenwurst gebrüht 2.223.4 Brühwurst umgerötet grob	\N
4328	390	3	2111	30	Touristenwurst gebrüht einfach 2.223.5 Brühwurst umgerötet grob	\N
4329	390	3	2112	31	Krakauer gebrüht einfach 2.223.5 Brühwurst umgerötet grob	\N
4330	390	3	2113	32	Schweinskopfwurst 2.223.6 Brühwurst umgerötet grob	\N
4331	390	3	2114	33	Kalbsbrust gefüllt 2.510.14 Brühwurst umgerötet grob	\N
4332	390	3	2115	34	Mettwurst gekocht schnittf. Brühwurst umgerötet grob	\N
4333	390	3	2116	35	Kaiserjagdwurst Brühwurst umgerötet grob	\N
4334	390	3	2117	37	Celler Gekochte 2.223.1 Brühwurst umgerötet grob	\N
4335	390	3	2118	40	Hackbraten 2.223.3 Brühwurst umgerötet grob	\N
4336	390	3	2119	41	Poltawer Brühwurst umgerötet grob	\N
4337	390	3	2120	42	Moskauer Brühwurst umgerötet grob	\N
4338	390	3	2121	43	Ukrainer Brühwurst umgerötet grob	\N
4339	390	3	2122	44	Kawassy Brühwurst umgerötet grob	\N
4340	390	3	2123	45	Römerbraten	\N
4341	390	3	2124	46	Leberkäse Brühwurst umgerötet grob	\N
4342	390	3	2125	47	Presskopf Brühwurst umgerötet grob	\N
4343	391	3	2127	1	Kochsalami Geflügel Brühwurst umgerötet grob	\N
4344	391	3	2128	2	Jagdwurst Geflügel Brühwurst umgerötet grob	\N
4345	391	3	2129	3	Schinkenwurst Geflügel Brühwurst umgerötet grob	\N
4346	391	3	2130	4	Fleischkäse Geflügel Brühwurst umgerötet grob	\N
4347	391	3	2131	5	Fleischwurst Geflügel Brühwurst umgerötet grob	\N
4348	391	3	2132	6	Fleischwurst Pferd Brühwurst umgerötet grob	\N
4349	391	3	2133	7	Kochsalami Schaf Brühwurst umgerötet grob	\N
4350	391	3	2134	8	Fleischwurst Schaf Brühwurst umgerötet grob	\N
4351	391	3	2135	9	Kochsalami Kaninchen Brühwurst umgerötet grob	\N
4352	391	3	2136	10	Fleischwurst Kaninchen Brühwurst umgerötet grob	\N
4353	392	3	2138	1	Bierschinken 2.224.1 Brühwurst umgerötet	\N
4354	392	3	2139	2	Presskopf 2.224.3 Brühwurst umgerötet	\N
4355	392	3	2140	3	Mortadella süddeutsche 2.224.4 Brühwurst umgerötet	\N
4356	392	3	2141	4	Zungenwurst 2.224.4 Brühwurst umgerötet	\N
4357	392	3	2142	5	Herzwurst 2.224.4 Brühwurst umgerötet	\N
4358	392	3	2143	6	Mortadella italienische 2.224.5 Brühwurst umgerötet	\N
4359	392	3	2144	7	Zigeunerwurst 2.224.6 Brühwurst umgerötet	\N
4360	392	3	2145	8	Paprikaspeckwurst 2.224.6 Brühwurst umgerötet	\N
4361	392	3	2147	10	Rinderbierschinken Brühwurst umgerötet	\N
4362	392	3	2148	11	Rinderbierschinken geschnetzelt Brühwurst umgerötet	\N
4363	392	3	2149	12	Mannheimer Brühwurst umgerötet	\N
4364	392	3	2150	13	Ansbacher Brühwurst umgerötet	\N
4365	393	3	2152	1	Bierschinken Geflügel Brühwurst umgerötet	\N
4366	394	3	2155	1	Gelbwurst 2.222.6 Brühwurst nicht umger. feingekuttert	\N
4367	394	3	2156	2	Hirnwurst 2.222.6 Brühwurst nicht umger. feingekuttert	\N
4368	394	3	2157	3	Kalbskäse 2.222.6 Brühwurst nicht umger. feingekuttert	\N
4369	394	3	2158	4	Fleischkäse weißer 2.222.6 Brühwurst nicht umger. feingekuttert	\N
4370	394	3	2159	5	Weiße im Ring 2.222.7 Brühwurst nicht umger. feingekuttert	\N
4371	394	3	2160	6	Lyoner weiße 2.222.7 Brühwurst nicht umger. feingekuttert	\N
4372	395	3	2162	1	Gelbwurst Geflügel Brühwurst nicht umger. feingekuttert	\N
4373	395	3	2163	2	Fleischkäse weißer Geflügel Brühwurst nicht umger. feingekuttert	\N
4374	395	3	2164	3	Weiße im Ring Geflügel Brühwurst nicht umger. feingekuttert	\N
4375	395	3	2165	4	Gelbwurst Kaninchen Brühwurst nicht umgerötet feingekuttert	\N
4376	396	3	2167	1	Schweinskäse weißer 2.223.7 Brühwurst nicht umgerötet grob	\N
4377	396	3	2168	2	Lyoner weiße grob Brühwurst nicht umgerötet grob	\N
4378	396	3	2169	3	Stadtwurst Hausmacher Brühwurst nicht umgerötet	\N
4379	398	3	2172	1	Milzwurst 2.224.7 Brühwurst nicht umgerötet	\N
4380	398	3	2173	2	Bries-/Milzwurst Brühwurst nicht umgerötet	\N
4381	399	3	2175	1	Milzwurst Geflügel Brühwurst nicht umgerötet	\N
4382	400	3	2177	1	Käsewurst Brühwurst	\N
4383	400	3	2178	2	Käsekochsalami Brühwurst	\N
4384	400	3	2179	3	Trüffelwurst Brühwurst	\N
4385	400	3	2180	4	Paprikawurst Brühwurst mit Paprikaeinlage	\N
4386	400	3	2181	5	Champignonwurst Brühwurst mit Champignoneinlage	\N
4387	400	3	2182	6	Pistazienwurst Brühwurst mit Pistazieneinlage	\N
4388	400	3	2183	7	Pfälzer Saumagen Brühwurst	\N
4389	400	3	2184	8	Olivenwurst Brühwurst mit Oliveneinlage	\N
4390	400	3	2185	9	Brühwurst mit grünem Pfeffer	\N
4391	400	3	2186	10	Tomatenwurst Brühwurst mit Tomateneinlage	\N
4392	400	3	2187	11	Gurkenwurst Brühwurst mit Gurkeneinlage	\N
4393	400	3	2188	12	Rosinenwurst Brühwurst mit Rosineneinlage	\N
4394	400	3	2189	13	Mandelwurst Brühwurst mit Mandeleinlage	\N
4395	400	3	2190	14	Nusswurst Brühwurst mit Nusseinlage	\N
4396	400	3	2191	15	Eierwurst Brühwurst mit Eieinlage	\N
4397	400	3	2192	16	Pizza-Fleischkäse Brühwurst mit Einlage	\N
4398	400	3	2193	17	Gemüsewurst Brühwurst mit Gemüseeinlage	\N
4399	400	3	2194	99	Brühwurst mit gemischten Einlagen	\N
4400	402	3	2197	1	Schinkenpastete 2.224.1 Brühwurstpastete	\N
4401	402	3	2198	2	Filetpastete 2.224.2 Brühwurstpastete	\N
4402	402	3	2199	3	Wildschweinpastete imitierte 2.224.2 Brühwurstpastete	\N
4403	402	3	2200	4	Eisbeinpastete 2.224.3 Brühwurstpastete	\N
4404	402	3	2201	5	Zungenpastete 2.222.4 Brühwurstpastete	\N
4405	402	3	2202	6	Zungenroulade 2.222.4 Brühwurstpastete	\N
4406	402	3	2203	7	Schachbrettpastete 2.222.4 Brühwurstpastete	\N
4407	402	3	2204	8	Mosaikpastete 2.222.4 Brühwurstpastete	\N
4408	402	3	2205	9	Leberpastete Brühwurstpastete	\N
4409	403	3	2207	1	Eierpastete Brühwurstpastete	\N
4410	403	3	2208	2	Olivenpastete Brühwurstpastete	\N
4411	403	3	2209	3	Champignonpastete Brühwurstpastete	\N
4412	403	3	2210	4	Paprikapastete Brühwurstpastete	\N
4413	403	3	2211	5	Pfefferpastete Brühwurstpastete	\N
4414	403	3	2212	6	Nusspastete Brühwurstpastete	\N
4415	403	3	2213	7	Käsepastete Brühwurstpastete	\N
4416	404	3	2215	1	Wildschweinpastete Brühwurstpastete	\N
4417	404	3	2216	2	Fasanenpastete Brühwurstpastete	\N
4418	404	3	2217	3	Schinkenpastete Geflügel Brühwurstpastete	\N
4419	405	3	2219	1	Leberwurst fein gekörnt mit hervorhebendem Hinweis	\N
4420	405	3	2220	2	Kalbsleberwurst fein gekörnt	\N
4421	405	3	2221	3	Trüffelleberwurst fein gekörnt	\N
4422	405	3	2222	4	Gänseleberwurst fein gekörnt	\N
4423	405	3	2223	5	Champignonleberwurst fein gekörnt	\N
4424	405	3	2224	6	Schweineleberwurst fein gekörnt	\N
4425	405	3	2225	7	Geflügelleberwurst fein gekörnt	\N
4426	405	3	2226	8	Leberwurst fein gekörnt	\N
4427	405	3	2228	10	Streichleberwurst fein gekörnt	\N
4428	405	3	2229	11	Aachener Leberwurst fein gekörnt	\N
4429	405	3	2230	12	Braunschweiger Leberwurst fein gekörnt	\N
4430	405	3	2231	13	Frankfurter Leberwurst fein gekörnt	\N
4431	405	3	2232	14	Kölner Leberwurst fein gekörnt	\N
4432	405	3	2233	15	Holsteiner Leberwurst fein gekörnt	\N
4433	405	3	2234	16	Hausmacher Leberwurst fein gekörnt	\N
4434	405	3	2235	17	Leberwurst einfach fein gekörnt	\N
4435	405	3	2236	18	Sahneleberwurst fein gekörnt	\N
4436	405	3	2237	19	Sardellenleberwurst fein gekörnt	\N
4437	405	3	2238	20	Hildesheimer Leberwurst fein gekörnt	\N
4438	405	3	2239	21	Berliner feine Leberwurst fein gekörnt	\N
4439	405	3	2240	22	Pfälzer Leberwurst fein gekörnt	\N
4440	405	3	2241	23	Zwiebelleberwurst fein gekörnt	\N
4441	405	3	13281	24	Kräuterleberwurst fein gekörnt	\N
4442	405	3	13282	25	Landleberwurst fein gekörnt	\N
4443	405	3	13283	26	Bauernleberwurst fein gekörnt	\N
4444	405	3	14166	27	Kalbfleisch-Leberwurst fein gekörnt	\N
4445	405	3	14241	28	Thüringer Leberwurst fein gekörnt	\N
4446	406	3	2243	1	Leberwurst grob gekörnt mit hervorhebendem Hinweis	\N
4447	406	3	2244	2	Schweineleberwurst grob gekörnt	\N
4448	406	3	2245	3	Sahneleberwurst grob gekörnt	\N
4449	406	3	2246	4	Bauernleberwurst grob gekörnt	\N
4450	406	3	2247	5	Hausmacher Leberwurst grob gekörnt	\N
4451	406	3	2248	6	Braunschweiger Leberwurst grob gekörnt	\N
4452	406	3	2249	7	Fränkische Leberwurst grob gekörnt	\N
4453	406	3	2250	8	Hessische Leberwurst grob gekörnt	\N
4454	406	3	2251	9	Holsteiner Leberwurst grob gekörnt	\N
4455	406	3	2252	10	Gütersloher Leberwurst grob gekörnt	\N
4456	406	3	2253	11	Thüringer Leberwurst grob gekörnt	\N
4457	406	3	2254	12	Pfälzer Leberwurst grob gekörnt	\N
4458	406	3	2255	13	Gutsleberwurst grob gekörnt	\N
4459	406	3	2256	14	Geflügelleberwurst grob gekörnt	\N
4460	406	3	2257	15	Kräuterleberwurst grob gekörnt	\N
4461	406	3	2258	16	Leberwurst einfach grob gekörnt	\N
4462	406	3	2259	17	Leberwurst grob gekörnt	\N
4463	406	3	2260	18	Zwiebelleberwurst grob gekörnt	\N
4464	406	3	2261	19	Calenberger Leberwurst grob gekörnt	\N
4465	406	3	2262	20	Kölner Leberwurst grob gekörnt	\N
4466	406	3	2263	21	Hausmacher Leberwurst württemberger Art grob gekörnt	\N
4467	406	3	2264	23	Lippische Leberwurst grob gekörnt	\N
4468	406	3	2265	26	Schlachtschüsselleberwurst grob gekörnt	\N
4469	406	3	2266	27	Siedeleberwurst grob gekörnt	\N
4470	406	3	2267	28	Schwäbische Leberwurst grob gekörnt	\N
4471	406	3	2268	29	Schwarzwälder Leberwurst grob gekörnt	\N
4472	406	3	2269	30	Westfälische Leberwurst grob gekörnt	\N
4473	406	3	2270	31	Schalottenleberwurst grob gekörnt	\N
4474	406	3	2271	32	Tomatenleberwurst grob gekörnt	\N
4475	406	3	2272	33	Landleberwurst grob gekörnt	\N
4476	406	3	2273	34	Griebenleberwurst grob gekörnt	\N
4477	406	3	2274	35	Fleischleberwurst grob gekörnt	\N
4478	406	3	2275	36	Schinkenleberwurst grob gekörnt	\N
4479	406	3	2276	37	Putenleberwurst grob gekörnt	\N
4480	406	3	2277	38	Gänseleberwurst grob gekörnt	\N
4481	406	3	2278	39	Pommersche Gänseleberwurst grob gekörnt	\N
4482	406	3	2279	40	Frankfurter Leberwurst grob gekörnt	\N
4483	406	3	2280	41	Hallesche Leberwurst	\N
4484	406	3	2281	42	Hamburger Leberwurst grob gekörnt	\N
4485	406	3	2282	43	Hannoversche Leberwurst grob gekörnt	\N
4486	406	3	2283	44	Kasseler Leberwurst grob gekörnt	\N
4487	406	3	2284	45	Pommersche Leberwurst grob gekörnt	\N
4488	406	3	2285	46	Rheinische Leberwurst grob gekörnt	\N
4489	406	3	2286	47	Sächsische Leberwurst grob gekörnt	\N
4490	406	3	2287	48	Schlesische Leberwurst grob gekörnt	\N
4491	406	3	14167	49	Kalbfleisch-Leberwurst grob gekörnt	\N
4492	407	3	2289	1	Mettwurst gekochte Kochstreichwurst	\N
4493	407	3	2290	2	Zwiebelmettwurst Kochstreichwurst	\N
4494	407	3	2291	3	Hannoversche Weißwurst Kochstreichwurst	\N
4495	407	3	2292	4	Kümmelkochwurst Kochstreichwurst	\N
4496	407	3	2293	5	Bregenwurst Kochstreichwurst	\N
4497	407	3	2294	6	Knappwurst Kochstreichwurst	\N
4498	407	3	2295	7	Pinkel Kochstreichwurst	\N
4499	407	3	2296	8	Knoblauchmettwurst Kochstreichwurst	\N
4500	407	3	2297	9	Lippische Schwartenmettwurst Kochstreichwurst	\N
4501	407	3	2298	10	Harzer Weiße Kochstreichwurst	\N
4502	407	3	2299	11	Schinkenkrem Kochstreichwurst	\N
4503	407	3	2300	12	Westfälische gekochte Mettwurst Kochstreichwurst	\N
4504	407	3	2301	13	Hamburger Gekochte Kochstreichwurst	\N
4505	407	3	2302	15	Gekochte Mettwurst mit Schnauze Kochstreichwurst	\N
4506	407	3	2303	16	Norddeutsche Fleischwurst Kochstreichwurst	\N
4507	407	3	2304	17	Hannoversche Bregenwurst Kochstreichwurst	\N
4508	407	3	2305	18	Norddeutsche Bregenwurst Kochstreichwurst	\N
4509	407	3	2306	19	Rinderwurst Kochstreichwurst	\N
4510	407	3	2307	20	Kohlwurst Kochstreichwurst	\N
4511	407	3	2308	21	Schmorwurst Kochstreichwurst	\N
4512	407	3	2309	22	Zwiebelwurst Kochstreichwurst	\N
4513	407	3	13284	23	Schmalzfleisch Kochstreichwurst	\N
4514	408	3	2311	1	Rotwurst mit hervorhebendem Hinweis	\N
4515	408	3	2312	2	Thüringer Rotwurst	\N
4516	408	3	2313	3	Landrotwurst	\N
4517	408	3	2314	4	Leberrotwurst	\N
4518	408	3	2315	5	Schinkenrotwurst	\N
4519	408	3	2316	6	Fleischrotwurst	\N
4520	408	3	2317	7	Hausmacher Rot-/Blutwurst	\N
4521	408	3	2318	8	Blutwurst/Rotwurst	\N
4522	408	3	2319	9	Blutwurst breite	\N
4523	408	3	2320	10	Landblutwurst	\N
4524	408	3	2321	11	Speckblutwurst	\N
4525	408	3	2322	12	Fleischmagen roter	\N
4526	408	3	2323	13	Blutmagen	\N
4527	408	3	2324	14	Rotgelegter	\N
4528	408	3	2325	15	Blutpresssack	\N
4529	408	3	2326	16	Presskopf rot	\N
4530	408	3	2327	17	Blutwurst einfach	\N
4531	408	3	2328	18	Panhas Rotwurst	\N
4532	408	3	2329	19	Presssack schwarzer	\N
4533	408	3	2330	20	Rheinische Blutwurst	\N
4534	408	3	2331	21	Sächsische Blutwurst	\N
4535	408	3	2332	22	Bauernblutwurst	\N
4536	408	3	2333	23	Griebenblutwurst	\N
4537	408	3	2334	24	Pfefferwurst Blutwurst	\N
4538	408	3	2335	25	Blunzen	\N
4539	408	3	2336	26	Floenz	\N
4540	408	3	2337	27	Schwarzwurst	\N
4541	408	3	2338	30	Gutsrotwurst	\N
4542	408	3	2339	31	Gutsfleischwurst Blutwurst	\N
4543	408	3	2340	32	Thüringer Fleischrotwurst	\N
4544	408	3	2341	33	Berliner Fleischwurst Blutwurst	\N
4545	408	3	2342	34	Pariser Blutwurst	\N
4546	408	3	2343	35	Fleischblutmagen	\N
4547	408	3	2344	36	Dresdener Blutwurst	\N
4548	408	3	2345	37	Schlesische Blutwurst	\N
4549	408	3	2346	38	Calenberger Rotwurst	\N
4550	408	3	2347	39	Bauernrotwurst	\N
4551	408	3	2348	40	Schwartemagen roter	\N
4552	408	3	2349	41	Presssack roter	\N
4553	408	3	2350	42	Blutpresskopf	\N
4554	408	3	2351	43	Presswurst Blutwurst	\N
4555	408	3	2352	44	Berliner Presswurst	\N
4556	409	3	2354	2	Zungenblutwurst	\N
4557	409	3	2355	3	Zungenwurst helle	\N
4558	409	3	2356	4	Filetwurst	\N
4559	409	3	2357	5	Filetrotwurst	\N
4560	409	3	2358	6	Zungenrotwurst	\N
4561	409	3	2359	7	Zungenwurst dunkle	\N
4562	409	3	2360	8	Berliner Zungenwurst	\N
4563	409	3	2361	9	Zungenpastete Zungenwurst	\N
4564	409	3	2362	10	Böhmische Rotwurst	\N
4565	409	3	2363	11	Schlegelwurst	\N
4566	409	3	2364	12	Blutwurst mit Einlage	\N
4567	410	3	2366	1	Sülzwurst mit hervorhebendem Hinweis	\N
4568	410	3	2367	2	Gutssülzwurst	\N
4569	410	3	2368	3	Presswurst Sülzwurst	\N
4570	410	3	2369	4	Presskopf Sülzwurst	\N
4571	410	3	2370	5	Schwartenmagen Sülzwurst	\N
4572	410	3	2371	6	Weißer Magen Sülzwurst	\N
4573	410	3	2372	7	Fleischmagen weißer Sülzwurst	\N
4574	410	3	2373	8	Weißgelegter Sülzwurst	\N
4575	410	3	2374	9	Schweinemagen Sülzwurst	\N
4576	410	3	2375	10	Presssack weißer Sülzwurst	\N
4577	410	3	2376	11	Leberpresssack Sülzwurst	\N
4578	410	3	2377	12	Sülzwurst	\N
4579	410	3	2378	13	Fleischsülzwurst	\N
4580	410	3	2379	14	Schinkensülzwurst	\N
4581	410	3	2380	15	Geflügelsülzwurst	\N
4582	410	3	2381	16	Rheinischer Presskopf Sülzwurst	\N
4583	410	3	2382	17	Bauernsülzwurst	\N
4584	410	3	2383	18	Hausmacher Sülzwurst	\N
4585	410	3	2384	19	Fränkische Schweinskopfsülze Sülzwurst	\N
4586	410	3	2385	20	Schweinskopfsülzwurst	\N
4587	410	3	2386	21	Kümmelmagen Sülzwurst	\N
4588	410	3	2387	22	Schinkenmagen Sülzwurst	\N
4589	410	3	2388	23	Weiße Rollwurst Sülzwurst	\N
4590	410	3	2389	24	Presssack Sülzwurst	\N
4591	410	3	2390	25	Coburger Weißwurst Sülzwurst	\N
4592	410	3	2391	26	Weiße Zungenwurst Sülzwurst	\N
4593	410	3	2392	27	Saure Rolle Sülzwurst	\N
4594	410	3	2393	28	Rollpens Sülzwurst	\N
4595	410	3	2394	29	Salvenatwurst Sülzwurst	\N
4596	410	3	2395	30	Rüsselpressmagen Sülzwurst	\N
4597	410	3	2396	31	Wiener Presskopf Sülzwurst	\N
4598	410	3	2397	32	Schinkenpresswurst Sülzwurst	\N
4599	410	3	2398	33	Schwartenwurst Sülzwurst	\N
4600	410	3	2399	34	Heukäs Sülzwurst	\N
4601	410	3	2400	35	Bauernpresssack Sülzwurst	\N
4602	411	3	2402	1	Schinkensülze	\N
4603	411	3	2403	2	Geflügelfleischsülze	\N
4604	411	3	2404	3	Kalbfleischsülze	\N
4605	411	3	2405	4	Rindfleischsülze	\N
4606	411	3	2406	5	Bratensülze	\N
4607	411	3	2407	6	Eisbeinsülze	\N
4608	411	3	2408	7	Schweinskopfsülze	\N
4609	411	3	2409	8	Sauerfleisch auch in Aspik	\N
4610	411	3	2410	9	Schweinebraten in Aspik	\N
4611	411	3	2411	10	Sülzkotelett	\N
4612	411	3	2412	11	Schweinezunge in Aspik	\N
4613	411	3	2413	12	Rindfleisch in Aspik	\N
4614	411	3	2414	13	Ochsenmaulsülze	\N
4615	411	3	2415	14	Huhn in Aspik	\N
4616	411	3	2416	15	Hühnerfleischsülze	\N
4617	411	3	2417	16	Pute in Aspik	\N
4618	411	3	2418	17	Putenfleischsülze	\N
4619	411	3	2419	18	Gans in Aspik	\N
4620	411	3	2420	19	Sülze	\N
4621	411	3	2421	20	Sülze einfach	\N
4622	411	3	2422	21	Innereiensülze	\N
4623	411	3	2423	22	Würzsülze	\N
4624	411	3	2424	23	Tellersülze/Tellerfleisch	\N
4625	411	3	2425	24	Schweinskopf in Aspik	\N
4626	411	3	2426	25	Zunge in Aspik	\N
4627	411	3	2427	26	Topfsülze	\N
4628	411	3	2428	27	Schinkenröllchen in Aspik	\N
4629	411	3	2429	28	Wurstsülze	\N
4630	411	3	2430	29	Berliner Schinkensülze	\N
4631	411	3	2431	30	Gewürfelter Schinken in Aspik	\N
4632	411	3	2432	31	Schinkensülztorte	\N
4633	411	3	2433	32	Spießbratensülze	\N
4634	411	3	2434	33	Karbonadensülze	\N
4635	411	3	2435	34	Mecklenburger Sülztörtchen	\N
4636	411	3	2436	35	Kalbfleischsülzpastete	\N
4637	411	3	2438	37	Zungensülze	\N
4638	411	3	2439	38	Weinsülze	\N
4639	411	3	2440	39	Bauernsülze	\N
4640	411	3	2441	40	Hausmachersülze	\N
4641	411	3	2442	41	Knöchelsülze	\N
4642	411	3	2443	42	Klauensülze	\N
4643	411	3	2444	43	Ohrensülze	\N
4644	411	3	2445	44	Rüsselsülze	\N
4645	411	3	2446	45	Aufschnittsülze	\N
4646	411	3	2447	46	Wurstsalat in Aspik	\N
4647	411	3	2448	47	Blutwurstsülze	\N
4648	411	3	2449	48	Hackbraten in Aspik	\N
4649	411	3	2450	49	Kopfsülze	\N
4650	412	3	2452	1	Semmelwurst Kochwurst	\N
4651	412	3	2453	2	Wellwurst Kochwurst	\N
4652	412	3	2454	3	Grützwurst Kochwurst	\N
4653	412	3	2455	4	Graupenwurst Kochwurst	\N
4654	412	3	2456	5	Semmelleberwurst Kochwurst	\N
4655	412	3	2457	6	Pfälzer Saumagen Kochwurst	\N
4656	412	3	2458	7	Beutelwurst westfälische Kochwurst	\N
4657	412	3	2459	8	Leberbrot Kochwurst	\N
4658	412	3	2460	9	Wurstebrot Kochwurst	\N
4659	412	3	2461	10	Kochwurst mit Mehl Kochwurst	\N
4660	412	3	2462	11	Kochwurst mit Reis Kochwurst	\N
4661	412	3	2463	12	Calenberger Knappwurst Kochwurst	\N
4662	412	3	2464	13	Berliner frische Leberwurst Kochwurst	\N
4663	412	3	2465	14	Schüsselwurst Kochwurst	\N
4664	412	3	2466	15	Weiße Schlesische Wellwurst Kochwurst	\N
4665	412	3	2467	16	Semmelwürstchen Kochwurst	\N
4666	412	3	2468	17	Grützleberwurst Kochwurst	\N
4667	412	3	2469	18	Krautleberwurst Kochwurst	\N
4668	412	3	2470	19	Mehlleberwurst Kochwurst	\N
4669	412	3	2471	20	Hessische Kartoffelwurst Kochwurst	\N
4670	412	3	2472	21	Knipp Kochwurst	\N
4671	412	3	2473	22	Pfannenschlag Kochwurst	\N
4672	412	3	2474	23	Weckewerk Kochwurst	\N
4673	412	3	2475	24	Würstebrei Kochwurst	\N
4674	412	3	2476	25	Berliner frische Blutwurst Kochwurst	\N
4675	412	3	2477	26	Beutelwurst Kochwurst	\N
4676	412	3	2478	27	Boudin Kochwurst	\N
4677	412	3	2479	28	Schwarze Graupenwürstchen Kochwurst	\N
4678	412	3	2480	29	Grützblutwurst Kochwurst	\N
4679	412	3	2481	30	Rote Grützwurst Kochwurst	\N
4680	412	3	2482	31	Möpkenbrot Kochwurst	\N
4681	412	3	2483	32	Tiegelblutwurst Kochwurst	\N
4682	412	3	2484	33	Tollatschen Kochwurst	\N
4683	412	3	2485	34	Rote Wellwurst Kochwurst	\N
4684	413	3	2487	1	Wildschweinpastete Kochwurstpastete	\N
4685	413	3	2488	2	Hirschpastete Kochwurstpastete	\N
4686	413	3	2489	3	Rehpastete Kochwurstpastete	\N
4687	413	3	2490	4	Hasenpastete Kochwurstpastete	\N
4688	413	3	2491	5	Entenpastete Kochwurstpastete	\N
4689	413	3	2492	6	Gänsepastete Kochwurstpastete	\N
4690	413	3	2493	7	Schnepfenpastete Kochwurstpastete	\N
4691	413	3	2494	8	Gänseleberpastete Kochwurstpastete	\N
4692	413	3	2495	9	Pate	\N
4693	413	3	2496	10	Geflügelpastete Kochwurstpastete	\N
4694	413	3	2497	11	Geflügelleberpastete Kochwurstpastete	\N
4695	413	3	2498	12	Wildpastete Kochwurstpastete	\N
4696	413	3	2499	13	Entenleberpastete Kochwurstpastete	\N
4697	413	3	2500	14	Fasanenpastete Kochwurstpastete	\N
4698	413	3	2501	15	Kaninchenpastete Kochwurstpastete	\N
4699	414	3	2503	1	Leberpastete Kochwurstpastete	\N
4700	414	3	2504	2	Blutwurstpastete Kochwurstpastete	\N
4701	414	3	2505	3	Leberrotwurstpastete Kochwurstpastete	\N
4702	414	3	2506	4	Lebercreme Kochwurstpastete	\N
4703	414	3	2507	5	Leberparfait	\N
4704	414	3	2508	6	Briespastete	\N
4705	414	3	2509	7	Filetpastete Kochwurstpastete	\N
4706	414	3	2510	8	Zungenpastete Kochwurstpastete	\N
4707	415	3	2512	1	Würstchen nach Frankfurter Art 2.221.01 umgerötet in Lake Konserve	\N
4708	415	3	2513	2	Schinkenwürstchen 2.221.01 umgerötet in Lake Konserve	\N
4709	415	3	2514	3	Delikatesswürstchen 2.221.02 umgerötet in Lake Konserve	\N
4710	415	3	2515	4	Wiener Würstchen 2.221.03 umgerötet in Lake Konserve	\N
4711	415	3	2516	5	Bockwurst 2.221.03 umgerötet in Lake Konserve	\N
4712	415	3	2517	6	Würstchen 2.221.03 umgerötet in Lake Konserve	\N
4713	415	3	2518	7	Saftwürstchen 2.221.03 umgerötet in Lake Konserve	\N
4714	415	3	2519	8	Cocktailwürstchen 2.221.03 umgerötet in Lake Konserve	\N
4715	415	3	2520	9	Halberstädter Würstchen 2.221.03 umgerötet in Lake Konserve	\N
4716	415	3	2521	10	Dünne 2.221.03 Würstchen umgerötet in Lake Konserve	\N
4717	415	3	2522	11	Dampfwurst Münchner 2.221.03 umgerötet in Lake Konserve	\N
4718	415	3	2523	12	Saitenwürstchen 2.221.03 umgerötet in Lake Konserve	\N
4719	415	3	2524	13	Bouillonwürstchen 2.221.03 umgerötet in Lake Konserve	\N
4720	415	3	2525	14	Fleischwürstchen 2.221.03 umgerötet in Lake Konserve	\N
4721	415	3	2526	15	Jauersche 2.221.03 Würstchen umgerötet in Lake Konserve	\N
4722	415	3	2527	16	Pfälzer 2.221.04 Würstchen umgerötet in Lake Konserve	\N
4723	415	3	2528	17	Augsburger 2.221.04 Würstchen umgerötet in Lake Konserve	\N
4724	415	3	2529	18	Regensburger 2.221.04 Würstchen umgerötet in Lake Konserve	\N
4725	415	3	2530	19	Debrecziner 2.221.04 Würstchen umgerötet in Lake Konserve	\N
4726	415	3	2531	20	Jagdwürstchen 2.221.04 umgerötet in Lake Konserve	\N
4727	415	3	2532	21	Brühpolnische 2.221.04 Würstchen umgerötet in Lake Konserve	\N
4728	415	3	2533	22	Bauernwürstchen 2.221.04 umgerötet in Lake Konserve	\N
4729	415	3	2534	23	Dicke 2.221.05 Würstchen umgerötet in Lake Konserve	\N
4730	415	3	2535	24	Knackwurst 2.221.05 umgerötet in Lake Konserve	\N
4731	415	3	2536	25	Rote 2.221.05 Würstchen umgerötet in Lake Konserve	\N
4732	415	3	2537	26	Servela 2.221.05 Würstchen umgerötet in Lake Konserve	\N
4733	415	3	2538	27	Klöpfer 2.221.05 Würstchen umgerötet in Lake Konserve	\N
4734	415	3	2539	28	Rindswurst 2.221.05 umgerötet in Lake Konserve	\N
4735	415	3	2540	29	Schüblinge 2.221.05 Würstchen umgerötet in Lake Konserve	\N
4736	415	3	2541	30	Knacker einfach 2.221.06 Würstchen umgerötet in Lake Konserve	\N
4737	415	3	2542	31	Schüblinge einfach 2.221.06 Würstchen umgerötet in Lake Konserve	\N
4738	415	3	2543	32	Servela einfach 2.221.06 Würstchen umgerötet in Lake Konserve	\N
4739	415	3	2544	33	Klöpfer einfach 2.221.06 Würstchen umgerötet in Lake Konserve	\N
4740	415	3	2545	34	Rote einfach 2.221.06 Würstchen umgerötet in Lake Konserve	\N
4741	415	3	2546	35	Berliner Dampfwurst 2.221.10 umgerötet in Lake Konserve	\N
4742	415	3	2547	36	Frankfurter Würstchen umgerötet in Lake Konserve	\N
4743	416	3	2549	1	Weißwurst 2.221.07 Brühwürstchen nicht umgerötet in Lake Konserve	\N
4744	416	3	2550	2	Münchner Weißwurst 2.221.09 Brühwürstchen nicht umger. in Lake Konserve	\N
4745	416	3	2551	3	Weißwurst einfach 2.221.10 Brühwürstchen nicht umgerötet in Lake Konserve	\N
4746	419	3	2555	1	Lyoner 2.222.1 Brühwurst umgerötet feingekuttert Konserve	\N
4747	419	3	2556	2	Schinkenwurst 2.222.1 Brühwurst umgerötet feingekuttert Konserve	\N
4748	419	3	2557	3	Mortadella Norddeutsche 2.222.1 Brühwurst umgerötet feingekuttert Konserve	\N
4749	419	3	2558	4	Pariser Fleischwurst 2.222.1 Brühwurst umgerötet feingekuttert Konserve	\N
4750	419	3	2559	5	Rheinische Fleischwurst 2.222.1 Brühwurst umgerötet feingekuttert Konserve	\N
4751	419	3	2560	6	Frankfurter Fleischwurst 2.222.1 Brühwurst umgerötet feingekuttert Konserve	\N
4752	419	3	2561	7	Kalbfleischwurst 2.222.1 Brühwurst umgerötet feingekuttert Konserve	\N
4753	419	3	2562	8	Kalbfleischkäse 2.222.1 Brühwurst umgerötet feingekuttert Konserve	\N
4754	419	3	2563	9	Breslauer 2.222.1 Brühwurst umgerötet feingekuttert Konserve	\N
4755	419	3	2564	10	Fleischwurst 2.222.2 Brühwurst umgerötet feingekuttert Konserve	\N
4756	419	3	2565	11	Stadtwurst 2.222.2 Brühwurst umgerötet feingekuttert Konserve	\N
4757	419	3	2566	12	Fleischkäse 2.222.2 Brühwurst umgerötet feingekuttert Konserve	\N
4758	419	3	2567	13	Fleischwurst einfach 2.222.3 Brühwurst umgerötet feingekuttert Konserve	\N
4759	419	3	2568	14	Stadtwurst einfach 2.222.3 Brühwurst umgerötet feingekuttert Konserve	\N
4760	419	3	2569	15	Fleischkäse einfach 2.222.3 Brühwurst umgerötet feingekuttert Konserve	\N
4761	419	3	2570	16	Leberkäse 2.222.2 Brühwurst umgerötet feingekuttert Konserve	\N
4762	419	3	2571	17	Fleischsalatgrundlage 2.222.5 Brühwurst umgerötet feingekuttert Konserve	\N
4763	421	3	2574	1	Bierwurst Ia 2.223.1 Brühwurst umgerötet grob Konserve	\N
4764	421	3	2575	2	Bayerische Bierwurst 2.223.1 Brühwurst umgerötet grob Konserve	\N
4765	421	3	2576	3	Göttinger 2.223.1 Brühwurst umgerötet grob Konserve	\N
4766	421	3	2577	4	Blasenwurst 2.223.1 Brühwurst umgerötet grob Konserve	\N
4767	421	3	2578	5	Kochsalami 2.223.1 Brühwurst umgerötet grob Konserve	\N
4768	421	3	2579	6	Tiroler 2.223.1 Brühwurst umgerötet grob Konserve	\N
4769	421	3	2580	7	Krakauer 2.223.1 Brühwurst umgerötet grob Konserve	\N
4770	421	3	2581	8	Jagdwurst süddeutsche Art 2.223.1 Brühwurst umgerötet grob Konserve	\N
4771	421	3	2582	9	Jagdwurst norddeutsche Art 2.223.2 Brühwurst umgerötet grob Konserve	\N
4772	421	3	2583	10	Schinkenwurst grob 2.223.2 Brühwurst umgerötet grob Konserve	\N
4773	421	3	2584	11	Schweinsbrust gefüllt 2.223.2 Brühwurst umgerötet grob Konserve	\N
4774	421	3	2585	12	Schweinsfuß gefüllt 2.223.2 Brühwurst umgerötet grob Konserve	\N
4775	421	3	2586	13	Schweinskopf gefüllt 2.223.2 Brühwurst umgerötet grob Konserve	\N
4776	421	3	2587	14	Lyoner grob 2.223.2 Brühwurst umgerötet grob Konserve	\N
4777	421	3	2588	15	Stuttgarter 2.223.2 Brühwurst umgerötet grob Konserve	\N
4778	421	3	2589	16	Bierwurst 2.223.2 Brühwurst umgerötet grob Konserve	\N
4779	421	3	2590	17	Hildesheimer 2.223.2 Brühwurst umgerötet grob Konserve	\N
4780	421	3	2591	18	Grobe Stadtwurst 2.223.2 Brühwurst umgerötet grob Konserve	\N
4781	421	3	2592	19	Nürnberger Stadtwurst 2.223.2 Brühwurst umgerötet grob Konserve	\N
4782	421	3	2593	20	Frühstücksfleisch 2.223.2 Brühwurst umgerötet grob Konserve	\N
4783	421	3	2594	21	Schweinskäse 2.223.2 Brühwurst umgerötet grob Konserve	\N
4784	421	3	2595	22	Stuttgarter Leberkäse 2.223.2 Brühwurst umgerötet grob Konserve	\N
4785	421	3	2596	23	Fleischkäse grob 2.223.3 Brühwurst umgerötet grob Konserve	\N
4786	421	3	2597	24	Fleischkäse roter 2.223.3 Brühwurst umgerötet grob Konserve	\N
4787	421	3	2598	25	Fleischwurst grob 2.223.3 Brühwurst umgerötet grob Konserve	\N
4788	421	3	2599	26	Knoblauchwurst gebrüht 2.223.4 Brühwurst umgerötet grob Konserve	\N
4789	421	3	2600	27	Krakauer gebrüht 2.223.4 Brühwurst umgerötet grob Konserve	\N
4790	421	3	2601	28	Cabanossi 2.223.4 Brühwurst umgerötet grob Konserve	\N
4791	421	3	2602	29	Touristenwurst gebrüht 2.223.4 Brühwurst umgerötet grob Konserve	\N
4792	421	3	2603	30	Touristenwurst gebrüht einfach 2.223.5 Brühwurst umgerötet grob Konserve	\N
4793	421	3	2604	31	Krakauer gebrüht einfach 2.223.5 Brühwurst umgerötet grob Konserve	\N
4794	421	3	2605	32	Schweinskopfwurst 2.223.6 Brühwurst umgerötet grob Konserve	\N
4795	421	3	2606	33	Kalbsbrust gefüllt 2.510.14 Brühwurst umgerötet grob Konserve	\N
4796	421	3	2607	34	Mettwurst gekocht schnittf. Brühwurst umgerötet grob Konserve	\N
4797	421	3	2608	35	Kaiserjagdwurst Brühwurst umgerötet grob Konserve	\N
4798	423	3	2611	1	Bierschinken 2.224.1 Brühwurst umgerötet Konserve	\N
4799	423	3	2612	2	Presskopf 2.224.3 Brühwurst umgerötet Konserve	\N
4800	423	3	2613	3	Mortadella süddeutsche 2.224.4 Brühwurst umgerötet Konserve	\N
4801	423	3	2614	4	Zungenwurst 2.224.4 Brühwurst umgerötet Konserve	\N
4802	423	3	2615	5	Herzwurst 2.224.4 Brühwurst umgerötet Konserve	\N
4803	423	3	2616	6	Mortadella italienische 2.224.5 Brühwurst umgerötet Konserve	\N
4804	423	3	2617	7	Zigeunerwurst 2.224.6 Brühwurst umgerötet Konserve	\N
4805	423	3	2618	8	Paprikaspeckwurst 2.224.6 Brühwurst umgerötet Konserve	\N
4806	427	3	2623	1	Schweinskäse weißer 2.223.7 Brühwurst nicht umgerötet grob Konserve	\N
4807	427	3	2624	2	Lyoner weiße grob Brühwurst nicht umgerötet grob Konserve	\N
4808	427	3	2625	3	Bratwurstgehäck Brühwurst nicht umgerötet Konserve	\N
4809	433	3	2632	1	Schinkenpastete 2.224.1 Brühwurstpastete Konserve	\N
4810	433	3	2633	2	Filetpastete 2.224.2 Brühwurstpastete Konserve	\N
4811	433	3	2634	3	Wildschweinpastete imitierte 2.224.2 Brühwurstpastete Konserve	\N
4812	433	3	2635	4	Eisbeinpastete 2.224.3 Brühwurstpastete Konserve	\N
4813	433	3	2636	5	Zungenpastete 2.222.4 Brühwurstpastete Konserve	\N
4814	433	3	2637	6	Zungenroulade 2.222.4 Brühwurstpastete Konserve	\N
4815	433	3	2638	7	Schachbrettpastete 2.222.4 Brühwurstpastete Konserve	\N
4816	433	3	2639	8	Mosaikpastete 2.222.4 Brühwurstpastete Konserve	\N
4817	433	3	2640	9	Leberpastete Brühwurstpastete Konserve	\N
4818	435	3	2643	1	Wildschweinpastete Brühwurstpastete Konserve	\N
4819	435	3	2644	2	Fasanenpastete Brühwurstpastete Konserve	\N
4820	436	3	2646	1	Leberwurst fein gekörnt mit hervorhebendem Hinweis Konserve	\N
4821	436	3	2647	2	Kalbsleberwurst fein gekörnt Konserve	\N
4822	436	3	2648	3	Trüffelleberwurst fein gekörnt Konserve	\N
4823	436	3	2649	4	Gänseleberwurst fein gekörnt Konserve	\N
4824	436	3	2650	5	Champignonleberwurst fein gekörnt Konserve	\N
4825	436	3	2651	6	Schweineleberwurst fein gekörnt Konserve	\N
4826	436	3	2652	7	Geflügelleberwurst fein gekörnt Konserve	\N
4827	436	3	2653	8	Leberwurst fein gekörnt Konserve	\N
4828	436	3	2654	10	Streichleberwurst fein gekörnt Konserve	\N
4829	436	3	2655	11	Aachener Leberwurst fein gekörnt Konserve	\N
4830	436	3	2656	12	Braunschweiger Leberwurst fein gekörnt Konserve	\N
4831	436	3	2657	13	Frankfurter Leberwurst fein gekörnt Konserve	\N
4832	436	3	2658	14	Kölner Leberwurst fein gekörnt Konserve	\N
4833	436	3	2659	15	Holsteiner Leberwurst fein gekörnt Konserve	\N
4834	436	3	2660	16	Hausmacher Leberwurst fein gekörnt Konserve	\N
4835	436	3	2661	17	Leberwurst einfach fein gekörnt Konserve	\N
4836	436	3	2662	18	Sahneleberwurst fein gekörnt Konserve	\N
4837	436	3	2663	19	Sardellenleberwurst fein gekörnt Konserve	\N
4838	436	3	14168	20	Kalbfleisch-Leberwurst Konserve	\N
4839	436	3	14242	21	Thüringer Leberwurst fein gekörnt Konserve	\N
4840	437	3	2665	1	Leberwurst grob gekörnt mit hervorhebendem Hinweis Konserve	\N
4841	437	3	2666	2	Schweineleberwurst grob gekörnt Konserve	\N
4842	437	3	2667	3	Sahneleberwurst grob gekörnt Konserve	\N
4843	437	3	2668	4	Bauernleberwurst grob gekörnt Konserve	\N
4844	437	3	2669	5	Hausmacher Leberwurst grob gekörnt Konserve	\N
4845	437	3	2670	6	Braunschweiger Leberwurst grob gekörnt Konserve	\N
4846	437	3	2671	7	Fränkische Leberwurst grob gekörnt Konserve	\N
4847	437	3	2672	8	Hessische Leberwurst grob gekörnt Konserve	\N
4848	437	3	2673	9	Holsteiner Leberwurst grob gekörnt Konserve	\N
4849	437	3	2674	10	Gütersloher Leberwurst grob gekörnt Konserve	\N
4850	437	3	2675	11	Thüringer Leberwurst grob gekörnt Konserve	\N
4851	437	3	2676	12	Pfälzer Leberwurst grob gekörnt Konserve	\N
4852	437	3	2677	13	Gutsleberwurst grob gekörnt Konserve	\N
4853	437	3	2678	14	Geflügelleberwurst grob gekörnt Konserve	\N
4854	437	3	2679	15	Kräuterleberwurst grob gekörnt Konserve	\N
4855	437	3	2680	16	Leberwurst einfach grob gekörnt Konserve	\N
4856	437	3	2681	17	Leberwurst grob gekörnt Konserve	\N
4857	437	3	2682	18	Zwiebelleberwurst grob gekörnt Konserve	\N
4858	437	3	2683	19	Calenberger Leberwurst grob gekörnt Konserve	\N
4859	437	3	2684	20	Kölner Leberwurst grob gekörnt Konserve	\N
4860	438	3	2686	1	Mettwurst gekochte Kochstreichwurst Konserve	\N
4861	438	3	2687	2	Zwiebelwurst Kochstreichwurst Konserve	\N
4862	438	3	2688	3	Hannoversche Weißwurst Kochstreichwurst Konserve	\N
4863	438	3	2689	4	Kümmelkochwurst Kochstreichwurst Konserve	\N
4864	438	3	2690	5	Bregenwurst Kochstreichwurst Konserve	\N
4865	438	3	2691	6	Knappwurst Kochstreichwurst Konserve	\N
4866	438	3	2692	7	Knoblauchmettwurst Kochstreichwurst Konserve	\N
4867	438	3	2693	8	Lippisches Schwartenmett Kochstreichwurst Konserve	\N
4868	439	3	2695	1	Rotwurst mit hervorhebendem Hinweis Konserve	\N
4869	439	3	2696	2	Thüringer Rotwurst Konserve	\N
4870	439	3	2697	3	Landrotwurst Konserve	\N
4871	439	3	2698	4	Leberrotwurst Konserve	\N
4872	439	3	2699	5	Schinkenrotwurst Konserve	\N
4873	439	3	2700	6	Fleischrotwurst Konserve	\N
4874	439	3	2701	7	Hausmacher Rotwurst Konserve	\N
4875	439	3	2702	8	Blutwurst Konserve	\N
4876	439	3	2703	9	Blutwurst breite Konserve	\N
4877	439	3	2704	10	Landblutwurst Konserve	\N
4878	439	3	2705	11	Speckblutwurst Konserve	\N
4879	439	3	2706	12	Fleischmagen roter Konserve	\N
4880	439	3	2707	13	Blutmagen Konserve	\N
4881	439	3	2708	14	Rotgelegter Konserve	\N
4882	439	3	2709	15	Blutpresssack Konserve	\N
4883	439	3	2710	16	Presskopf rot Konserve	\N
4884	439	3	2711	17	Blutwurst einfach Konserve	\N
4885	439	3	2712	18	Panhas Rotwurst Konserve	\N
4886	439	3	13285	19	Blutwurst Rotwurst Konserve	\N
4887	440	3	2714	2	Zungenblutwurst Konserve	\N
4888	440	3	2715	3	Zungenwurst helle Konserve	\N
4889	440	3	2716	4	Filetwurst Konserve	\N
4890	440	3	2717	5	Filetrotwurst Konserve	\N
4891	441	3	2719	1	Sülzwurst mit hervorhebendem Hinweis Konserve	\N
4892	441	3	2720	2	Gutssülzwurst Konserve	\N
4893	441	3	2721	3	Presswurst Sülzwurst Konserve	\N
4894	441	3	2722	4	Presskopf Sülzwurst Konserve	\N
4895	441	3	2723	5	Schwartenmagen Sülzwurst Konserve	\N
4896	441	3	2724	6	Weißer Magen Sülzwurst Konserve	\N
4897	441	3	2725	7	Fleischmagen weißer Sülzwurst Konserve	\N
4898	441	3	2726	8	Weißgelegter Sülzwurst Konserve	\N
4899	441	3	2727	9	Schweinemagen Sülzwurst Konserve	\N
4900	441	3	2728	10	Presssack weißer Sülzwurst Konserve	\N
4901	441	3	2729	11	Leberpresssack Sülzwurst Konserve	\N
4902	441	3	2730	12	Sülzwurst Konserve	\N
4903	442	3	2732	1	Schinkensülze Konserve	\N
4904	442	3	2733	2	Geflügelfleischsülze Konserve	\N
4905	442	3	2734	3	Kalbfleischsülze Konserve	\N
4906	442	3	2735	4	Rindfleischsülze Konserve	\N
4907	442	3	2736	5	Bratensülze Konserve	\N
4908	442	3	2737	6	Eisbeinsülze Konserve	\N
4909	442	3	2738	7	Schweinskopfsülze Konserve	\N
4910	442	3	2739	8	Sauerfleisch auch in Aspik Konserve	\N
4911	442	3	2740	9	Schweinebraten in Aspik Konserve	\N
4912	442	3	2741	10	Sülzkotelett Konserve	\N
4913	442	3	2742	11	Schweinezunge in Aspik Konserve	\N
4914	442	3	2743	12	Rindfleisch in Aspik Konserve	\N
4915	442	3	2744	13	Ochsenmaulsülze Konserve	\N
4916	442	3	2745	14	Huhn in Aspik Konserve	\N
4917	442	3	2746	15	Hühnerfleischsülze Konserve	\N
4918	442	3	2747	16	Pute in Aspik Konserve	\N
4919	442	3	2748	17	Putenfleischsülze Konserve	\N
4920	442	3	2749	18	Gans in Aspik Konserve	\N
4921	442	3	2750	19	Sülze Konserve	\N
4922	442	3	2751	20	Sülze einfach Konserve	\N
4923	442	3	2752	21	Innereiensülze Konserve	\N
4924	442	3	2753	22	Würzsülze Konserve	\N
4925	442	3	2754	23	Tellersülze/Tellerfleisch Konserve	\N
4926	442	3	2755	24	Schweinskopf in Aspik Konserve	\N
4927	442	3	2756	25	Zunge in Aspik Konserve	\N
4928	442	3	2757	26	Topfsülze Konserve	\N
4929	442	3	2758	27	Schinkenröllchen in Aspik Konserve	\N
4930	442	3	2759	28	Wurstsülze Konserve	\N
4931	444	3	2762	1	Wildschweinpastete Kochwurstpastete Konserve	\N
4932	444	3	2763	2	Hirschpastete Kochwurstpastete Konserve	\N
4933	444	3	2764	3	Rehpastete Kochwurstpastete Konserve	\N
4934	444	3	2765	4	Hasenpastete Kochwurstpastete Konserve	\N
4935	444	3	2766	5	Entenpastete Kochwurstpastete Konserve	\N
4936	444	3	2767	6	Gänsepastete Kochwurstpastete Konserve	\N
4937	444	3	2768	7	Schnepfenpastete Kochwurstpastete Konserve	\N
4938	444	3	2769	8	Gänseleberpastete Konserve	\N
4939	444	3	2770	9	Geflügelpastete Konserve	\N
4940	444	3	2771	10	Geflügelleberpastete Konserve	\N
4941	444	3	2772	11	Wildpastete Konserve	\N
4942	444	3	2773	12	Entenleberpastete Kochwurstpastete Konserve	\N
4943	444	3	2774	13	Fasanenpastete Kochwurstpastete Konserve	\N
4944	444	3	2775	14	Kaninchenpastete Kochwurstpastete Konserve	\N
4945	445	3	2777	1	Leberpastete Kochwurstpastete Konserve	\N
4946	445	3	2778	2	Blutwurstpastete Kochwurstpastete Konserve	\N
4947	445	3	2779	3	Leberrotwurstpastete Kochwurstpastete Konserve	\N
4948	445	3	2780	4	Lebercreme Kochwurstpastete Konserve	\N
4949	445	3	2781	5	Leberparfait Konserve	\N
4950	445	3	2782	6	Briespastete Konserve	\N
4951	445	3	2783	7	Filetpastete Kochwurstpastete Konserve	\N
4952	445	3	2784	8	Zungenpastete Kochwurstpastete Konserve	\N
4953	447	3	2787	1	Teewurst fettreduziert	\N
4954	447	3	2788	2	Teewurst Rügenwalder Art fettreduziert	\N
4955	448	3	2790	1	Würstchen Frankfurter Art fettreduziert	\N
4956	448	3	2791	2	Würstchen Wiener Art fettreduziert	\N
4957	448	3	2792	3	Bockwurst fettreduziert	\N
4958	448	3	2793	4	Würstchen fettreduziert	\N
4959	449	3	2795	1	Lyoner fettreduziert	\N
4960	449	3	2796	2	Schinkenwurst fettreduziert	\N
4961	449	3	2797	3	Mortadella Norddeutsche fettreduziert	\N
4962	449	3	2798	4	Fleischwurst fettreduziert	\N
4963	449	3	2799	5	Fleischwurst einfach fettreduziert	\N
4964	450	3	2801	1	Bierwurst Ia fettreduziert	\N
4965	450	3	2802	2	Bierwurst fettreduziert	\N
4966	450	3	2803	3	Jagdwurst süddeutsche Art fettreduziert	\N
4967	450	3	2804	4	Jagdwurst norddeutsche Art fettreduziert	\N
4968	450	3	2805	5	Schinkenwurst grob fettreduziert	\N
4969	451	3	2807	1	Bierschinken fettreduziert	\N
4970	452	3	2809	1	Leberwurst feingekörnt fettreduziert	\N
4971	452	3	2810	2	Kalbsleberwurst feingekörnt fettreduziert	\N
4972	452	3	13286	3	Leberwurst grobgekörnt fettreduziert	\N
4973	454	3	2813	1	Geflügelmortadelle umgerötet fettreduziert	\N
4974	454	3	2814	2	Geflügeljagdwurst umgerötet fettreduziert	\N
4975	454	3	2815	3	Geflügelfleischwurst umgerötet fettreduziert	\N
4976	455	3	2817	1	Würstchen Frankfurter Art fettreduziert Konserve	\N
4977	455	3	2818	2	Würstchen Wiener Art fettreduziert Konserve	\N
4978	455	3	2819	3	Bockwurst fettreduziert Konserve	\N
4979	455	3	2820	4	Würstchen fettreduziert Konserve	\N
4980	456	3	2822	1	Lyoner fettreduziert Konserve	\N
4981	456	3	2823	2	Schinkenwurst fettreduziert Konserve	\N
4982	456	3	2824	3	Mortadella Norddeutsche fettreduziert Konserve	\N
4983	456	3	2825	4	Fleischwurst fettreduziert Konserve	\N
4984	456	3	2826	5	Fleischwurst einfach fettreduziert Konserve	\N
4985	457	3	2828	1	Bierwurst Ia fettreduziert Konserve	\N
4986	457	3	2829	2	Bierwurst fettreduziert Konserve	\N
4987	457	3	2830	3	Jagdwurst süddeutsche Art fettreduziert Konserve	\N
4988	457	3	2831	4	Jagdwurst norddeutsche Art fettreduziert Konserve	\N
4989	457	3	2832	5	Schinkenwurst grob fettreduziert Konserve	\N
4990	458	3	2834	1	Bierschinken fettreduziert Konserve	\N
4991	459	3	2836	1	Leberwurst feingekörnt fettreduziert Konserve	\N
4992	459	3	2837	2	Kalbsleberwurst feingekörnt fettreduziert Konserve	\N
4993	459	3	14169	3	Kalbfleisch-Leberwurst fettreduziert Konserve	\N
4994	460	3	2840	1	Geflügelmortadelle umgerötet fettreduziert Konserve	\N
4995	460	3	2841	2	Geflügeljagdwurst umgerötet fettreduziert Konserve	\N
4996	460	3	2842	3	Geflügelfleischwurst umgerötet fettreduziert Konserve	\N
4997	465	3	2847	1	Mischung aus verschiedenen Wurstwaren Rohwurst	\N
4998	465	3	2848	2	Mischung aus verschiedenen Wurstwaren Brühwurst	\N
4999	465	3	2849	3	Mischung aus verschiedenen Wurstwaren Kochwurst	\N
5000	465	3	2850	4	Mischung aus verschiedenen Wurstwaren Roh- Brüh- Kochwurst	\N
5001	465	3	2851	5	Mischung aus verschiedenen Wurst- und Pökelwaren	\N
5002	465	3	2852	6	Mischung aus Wurstwaren verschiedener Tierarten	\N
5003	466	3	2856	5	Meerneunauge (Petromyzon marinus) Seefisch	\N
5004	467	3	2858	5	Heringshai (Lamna nasus) Seefisch	\N
5005	467	3	2859	10	Riesenhai (Cetorhinus maximus) Seefisch	\N
5006	467	3	2860	15	Dornhai (Squalus acanthias) Seefisch	\N
5007	467	3	2861	20	Eishai (Somniosus microcephalus) Seefisch	\N
5008	467	3	2862	25	Hundshai (Galeorhinus galeus) Seefisch	\N
5009	467	3	2863	30	Katzenhai (Scyliorhinus sp.) Seefisch	\N
5010	468	3	2865	5	Rochen (Raja sp.) Seefisch	\N
5011	469	3	2867	5	Hering (Clupea harengus) Seefisch	\N
5012	469	3	2868	10	Sprotte (Sprattus sprattus) Seefisch	\N
5013	469	3	2869	15	Sardine (Sardina pilchardus) Seefisch	\N
5014	469	3	2870	20	Sardinelle (Sardinella aurita) Seefisch	\N
5015	469	3	2871	25	Sardinops (Sardinops sp.) Seefisch	\N
5016	469	3	2872	30	Sardelle (Engraulis encrasicolus) Seefisch	\N
5017	469	3	2873	35	Kilka (Clupeonella sp.) Seefisch	\N
5018	470	3	2875	5	Meerforelle (Salmo trutta trutta) Seefisch	\N
5019	470	3	2876	10	Lodde (Mallotus villosus) Seefisch	\N
5020	470	3	2877	15	Glasauge (Argentina sphyraena) Seefisch	\N
5021	470	3	2878	20	Goldlachs (Argentina silus) Seefisch	\N
5022	470	3	2879	25	Stint (Osmerus sp.) Seefisch	\N
5023	470	3	2880	26	Pazifiklachs (Oncorhynchus sp.) Seefisch	\N
5024	471	3	2882	5	Angler (Lophius piscatorius) Seefisch	\N
5025	472	3	2884	5	Meeraal (Conger conger) Seefisch	\N
5026	473	3	2886	5	Lumb (Brosme brosme) Seefisch	\N
5027	473	3	2887	10	Leng (Molva molva) Seefisch	\N
5028	473	3	2888	15	Blauleng (Molva dipterygia) Seefisch	\N
5029	473	3	2889	20	Kabeljau (Gadus morhua) Seefisch	\N
5030	473	3	2890	25	Grönland Kabeljau (Gadus ogac) Seefisch	\N
5031	473	3	2891	30	Schellfisch (Melanogrammus aeglefinus) Seefisch	\N
5032	473	3	2892	35	Seelachs (Pollachius virens) Seefisch	\N
5033	473	3	2893	40	Wittling (Merlangius merlangus) Seefisch	\N
5034	473	3	2894	45	Seehecht (Merluccius merluccius) Seefisch	\N
5035	473	3	2895	50	Grenadierfisch (Macrourus sp.) Seefisch	\N
5036	473	3	2896	55	Pollack (Pollachius pollachius) Seefisch	\N
5037	473	3	2897	60	Blauer Wittling (Micromesistius poutassou) Seefisch	\N
5038	473	3	2898	65	Alaska Pollack (Theragra chalcogramma) Seefisch	\N
5039	473	3	13700	70	Whiptail (Macruronus noväzealandica) Seefisch	\N
5040	474	3	2900	5	Hornhecht (Belone belone) Seefisch	\N
5041	474	3	13701	10	Makrelenhecht (Scomberesox saurus) Seefisch	\N
5042	475	3	2902	20	Grauer Knurrhahn (Trigla gurnardus) Seefisch	\N
5043	475	3	2903	25	Roter Knurrhahn (Trigla lucerna) Seefisch	\N
5044	475	3	2904	30	Seehase (Cyclopterus lumpus) Seefisch	\N
5045	475	3	13702	40	Rotbarsch (Sebastes sp.) Seefisch	\N
5046	476	3	14286	2	Wolfsbarsch (Dicentrarchus labrax) Seefisch	\N
5047	476	3	14305	3	Gefleckter Wolfsbarsch (Dicentrarchus punctatus) Seefisch	\N
5048	476	3	2906	5	Stöker (Trachurus sp.) Seefisch	\N
5049	476	3	2907	10	Meerbrasse (Sparidae sp.) Seefisch	\N
5050	476	3	2908	15	Adlerfisch (Jonius hololepidotus) Seefisch	\N
5051	476	3	2909	20	Meerbarbe (Mullus sp.) Seefisch	\N
5052	476	3	2910	25	Buttermakrele Schlangenmakrele Butterfisch (Gempylidae sp.) Seefisch	\N
5053	476	3	2911	30	Katfisch gestreifter (Anarhichas lupus) Seefisch	\N
5054	476	3	2912	35	Katfisch gefleckter (Anarhichas minor) Seefisch	\N
5055	476	3	2913	40	gestr. jetzt 101240, Rotbarsch (Sebastes sp.) Seefisch	\N
5056	476	3	2914	45	Makrele nordatlantische (Scomber scombrus) Seefisch	\N
5057	476	3	2915	50	Makrele pazifische (Scomber japonicus) Seefisch	\N
5058	476	3	2916	55	Thunfisch (Thunnus sp.) Seefisch	\N
5059	476	3	2917	60	Bonito (Euthynnus sp.) Seefisch	\N
5060	476	3	2918	65	Pelamide (Sarda sp.) Seefisch	\N
5061	476	3	2919	70	gestr. jetzt 101505, Kaiserbarsch (Beryx decadactylus) Seefisch	\N
5062	476	3	2920	75	gestr. jetzt 101110, Makrelenhecht (Scomberesox saurus) Seefisch	\N
5063	476	3	2921	80	Brachsenmakrele (Brama brama) Seefisch	\N
5064	476	3	2922	85	Petermännchen (Trachinus draco) Seefisch	\N
5065	476	3	2923	90	Schwertfisch (Xiphiidae sp.) Seefisch	\N
5066	476	3	13704	95	Grouper (Polyprion oxygeneios) Seefisch	\N
5067	476	3	13705	96	Zahnbrasse (Dentex dentex) Seefisch	\N
5068	476	3	13781	97	Roter Fusilierfisch Goldband Fusilier Goldstreifen Fusilier (Caesio chrysozona) Seefisch	\N
5069	477	3	2925	5	Kleist (Scopthalmus rhombus) Seefisch	\N
5070	477	3	2926	10	Steinbutt (Psetta maxima) Seefisch	\N
5071	477	3	2927	15	Flügelbutt (Lepidorhombus whiffiagonis) Seefisch	\N
5072	477	3	2928	20	Lammbutt (Arnoglossus laterna) Seefisch	\N
5073	477	3	2929	25	Scholle atlantische Goldbutt (Pleuronectes platessa) Seefisch	\N
5074	477	3	13839	26	Scholle pazifische (Lepidopsetta bilineata) Seefisch	\N
5075	477	3	2930	30	Heilbutt (Hippoglossus hippoglossus) Seefisch	\N
5076	477	3	2931	35	Schwarzer Heilbutt (Reinhardtius hippoglossoides) Seefisch	\N
5077	477	3	2932	40	Rotzunge Tropenzunge Hundszunge (Cynoglossus spp.) Seefisch	\N
5078	477	3	2933	41	Rotzunge (Glyptocephalus cynoglossus)	\N
5079	477	3	2934	45	Seezunge (Solea solea) Seefisch	\N
5080	477	3	2935	50	Flunder (Platichthys flesus) Seefisch	\N
5081	477	3	2936	55	Kliesche (Limanda limanda) Seefisch	\N
5082	477	3	2937	60	Limande (Microstomus kitt) Seefisch	\N
5083	477	3	2938	65	Doggerscharbe (Hippoglossoides platessoides) Seefisch	\N
5084	479	3	2940	5	gestr. jetzt 101065, Alaska Pollock (Theragra chalcogrammas) Seefisch	\N
5085	479	3	2941	10	gestr. jetzt 101395, Grouper (Polyprion oxygeneios) Seefisch	\N
5086	479	3	2942	15	Meeräsche (Mugil sp.) Seefisch	\N
5087	479	3	2943	20	Kingsclip (Genypterus capensis) Seefisch	\N
5088	479	3	2944	30	gestr. jetzt 101070, Whiptail (Macruronus novaezelandiae) Seefisch	\N
5089	479	3	2945	35	gestr. jetzt 101396, Zahnbrasse (Dentex dentex)	\N
5090	479	3	2946	40	Petersfisch (Zeidae) Seefisch	\N
5091	480	3	2948	5	Flussneunauge (Lampetra fluviatilis) Süßwasserfisch	\N
5092	480	3	2949	10	Bachneunauge (Lampetra planeri) Süßwasserfisch	\N
5093	481	3	2951	5	Lachs (Salmo salar) Süßwasserfisch	\N
5094	481	3	2952	10	Bachforelle (Salmo trutta fario) Süßwasserfisch	\N
5095	481	3	2953	15	Regenbogenforelle (Oncorhynchus mykiss) Süßwasserfisch	\N
5096	481	3	2954	20	Seeforelle (Salmo trutta lacustris) Süßwasserfisch	\N
5097	481	3	2955	25	Binnenstint (Osmerus eperlanus) Süßwasserfisch	\N
5098	481	3	2956	30	Bachsaibling (Salvelinus fontinalis) Süßwasserfisch	\N
5099	481	3	2957	31	Seesaibling (Salvelinus alpinus) Süßwasserfisch	\N
5100	481	3	2958	35	Huchen (Hucho hucho) Süßwasserfisch	\N
5101	481	3	2959	40	Taimen (Hucho taimen) Süßwasserfisch	\N
5102	481	3	2960	45	Renke Maräne Felchen (Coregonus sp.) Süßwasserfisch	\N
5103	481	3	2961	60	Äsche (Thymallus sp.) Süßwasserfisch	\N
5104	481	3	2962	65	Lachsforelle (Salmo sp.)	\N
5105	482	3	2964	5	Maifisch (Alosa alosa) Süßwasserfisch	\N
5106	482	3	2965	10	Finte (Alosa fallax) Süßwasserfisch	\N
5107	483	3	2967	5	Hecht (Esox lucius) Süßwasserfisch	\N
5108	484	3	2969	5	Karausche (Carassius carassius) Süßwasserfisch	\N
5109	484	3	2970	10	Plötze (Rutilus rutilus) Süßwasserfisch	\N
5110	484	3	2971	15	Rotfeder (Scardinius erythrophthalmus) Süßwasserfisch	\N
5111	484	3	2972	20	Graskarpfen (Ctenopharyngodon sp.) Süßwasserfisch	\N
5112	484	3	2973	25	Döbel Aitel (Leuciscus cephalus) Süßwasserfisch	\N
5113	484	3	14039	26	Orfe Aland Nerfling (Leuciscus idus) Süßwasserfisch	\N
5114	484	3	2974	30	Brachsen Brasse Blei (Abramis brama) Süßwasserfisch	\N
5115	484	3	2975	35	Zährte (Vimba vimba) Süßwasserfisch	\N
5116	484	3	2976	40	Nase (Chondrostoma nasus) Süßwasserfisch	\N
5117	484	3	2977	45	Rapfen (Aspius aspius) Süßwasserfisch	\N
5118	484	3	2978	46	Moderlieschen (Leucaspius delineatus) Süßwasserfisch	\N
5119	484	3	2979	50	Schleie (Tinca tinca) Süßwasserfisch	\N
5120	484	3	2980	55	Barbe (Barbus barbus) Süßwasserfisch	\N
5121	484	3	2981	60	Karpfen (Cyprinus carpio) Süßwasserfisch	\N
5122	484	3	2982	65	Weißfisch (Cyprinidae sp.) Süßwasserfisch	\N
5123	484	3	2983	70	Güster (Blicca björkna) Süßwasserfisch	\N
5124	484	3	2984	75	Wels (Silurus glanis) Süßwasserfisch	\N
5125	484	3	13287	76	Schlankwels (Pangasius spp.) Süßwasserfisch	\N
5126	484	3	2985	80	Catfish Katzenwels (Ictalurus furcatus) Süßwasserfisch	\N
5127	484	3	2986	85	Ukelei (Alburnus alburnus)	\N
5128	485	3	2988	5	Aal (Anguilla anguilla) Süßwasserfisch	\N
5129	486	3	2990	5	Flussbarsch (Perca fluviatilis) Süßwasserfisch	\N
5130	486	3	2991	10	Zander (Stizostedion lucioperca) Süßwasserfisch	\N
5131	486	3	2992	15	Kaulbarsch (Gymnocephalus cernua) Süßwasserfisch	\N
5132	486	3	2993	20	Schrätzer (Gymnocephalus schraetser) Süßwasserfisch	\N
5133	486	3	2994	25	Nilbarsch (Lates niloticus) Süßwasserfisch	\N
5134	486	3	2995	30	Victoriasee-Barsch (Lates niloticus) Süßwasserfisch	\N
5135	486	3	13288	35	Tilapia Buntbarsch (Tilapia spp., Oreochromis spp.) Süßwasserfisch	\N
5136	487	3	2997	5	Quappe (Lota lota) Süßwasserfisch	\N
5137	488	3	2999	5	Stör (Acipenser sturio) Süßwasserfisch	\N
5138	489	3	3001	5	Meerneunauge Stück	\N
5139	490	3	3003	5	Heringshai Stück	\N
5140	490	3	3004	6	Heringshai Scheibe	\N
5141	490	3	3005	7	Heringshai Kotelett	\N
5142	490	3	3006	10	Riesenhai Stück	\N
5143	490	3	3007	11	Riesenhai Scheibe	\N
5144	490	3	3008	12	Riesenhai Kotelett	\N
5145	490	3	3009	15	Dornhai Stück	\N
5146	490	3	3010	16	Dornhai Scheibe	\N
5147	490	3	3011	17	Dornhai Kotelett	\N
5148	490	3	3012	20	Eishai Stück	\N
5149	490	3	3013	21	Eishai Scheibe	\N
5150	490	3	3014	22	Eishai Kotelett	\N
5151	490	3	3015	25	Hundshai Stück	\N
5152	490	3	3016	26	Hundshai Scheibe	\N
5153	490	3	3017	27	Hundshai Kotelett	\N
5154	490	3	3018	30	Katzenhai Stück	\N
5155	490	3	3019	31	Katzenhai Scheibe	\N
5156	490	3	3020	32	Katzenhai Kotelett	\N
5157	491	3	3022	5	Rochen Stück	\N
5158	491	3	3023	6	Rochen Filet	\N
5159	492	3	3025	5	Hering Filet	\N
5160	492	3	3026	6	Hering Stück	\N
5161	492	3	3027	7	Hering Happen	\N
5162	492	3	3028	10	Sprotte Filet	\N
5163	492	3	3029	15	Sardine Filet	\N
5164	492	3	3030	16	Sardine Stück	\N
5165	492	3	3031	17	Sardine Happen	\N
5166	492	3	3032	20	Sardinelle Filet	\N
5167	492	3	3033	25	Sardinops Filet	\N
5168	492	3	3034	30	Sardelle Filet	\N
5172	493	3	3039	10	Lodde Filet	\N
5173	493	3	3040	11	Lodde Stück	\N
5174	493	3	3041	15	Glasauge Filet	\N
5175	493	3	3042	16	Glasauge Stück	\N
5176	493	3	3043	20	Goldlachs Filet	\N
5177	493	3	3044	21	Goldlachs Stück	\N
5178	493	3	3045	23	Pazifiklachs Filet	\N
5179	493	3	3046	24	Pazifiklachs Stück	\N
5180	493	3	3047	25	Pazifiklachs Kotelett	\N
5181	494	3	3049	5	Angler Filet	\N
5182	494	3	3050	6	Angler Stück	\N
5183	494	3	3051	7	Angler Kotelett	\N
5184	495	3	3053	5	Meeraal Stück	\N
5185	496	3	3055	5	Lumb Filet	\N
5186	496	3	3056	6	Lumb Stück	\N
5187	496	3	3057	7	Lumb Kotelett	\N
5188	496	3	3058	10	Leng Filet	\N
5189	496	3	3059	11	Leng Stück	\N
5190	496	3	3060	12	Leng Scheibe	\N
5191	496	3	3061	13	Leng Kotelett	\N
5192	496	3	3062	15	Blauleng Filet	\N
5193	496	3	3063	16	Blauleng Stück	\N
5194	496	3	3064	17	Blauleng Scheibe	\N
5195	496	3	3065	18	Blauleng Kotelett	\N
5196	496	3	3066	20	Kabeljau Filet	\N
5197	496	3	3067	21	Kabeljau Stück	\N
5198	496	3	3068	22	Kabeljau Scheibe	\N
5199	496	3	3069	23	Kabeljau Kotelett	\N
5200	496	3	3070	25	Grönland Kabeljau Filet	\N
5201	496	3	3071	26	Grönland Kabeljau Stück	\N
5202	496	3	3072	27	Grönland Kabeljau Scheibe	\N
5203	496	3	3073	28	Grönland Kabeljau Kotelett	\N
5204	496	3	3074	30	Schellfisch Filet	\N
5205	496	3	3075	31	Schellfisch Stück	\N
5206	496	3	3076	32	Schellfisch Scheibe	\N
5207	496	3	3077	33	Schellfisch Kotelett	\N
5208	496	3	3078	35	Seelachs Filet	\N
5209	496	3	3079	36	Seelachs Stück	\N
5210	496	3	3080	37	Seelachs Scheibe	\N
5211	496	3	3081	38	Seelachs Kotelett	\N
5212	496	3	3082	40	Wittling Filet	\N
5213	496	3	3083	41	Wittling Stück	\N
5214	496	3	3084	42	Wittling Scheibe	\N
5215	496	3	3085	43	Wittling Kotelett	\N
5216	496	3	3086	45	Seehecht Filet	\N
5217	496	3	3087	46	Seehecht Stück	\N
5218	496	3	3088	47	Seehecht Scheibe	\N
5219	496	3	3089	48	Seehecht Kotelett	\N
5220	496	3	3090	50	Grenadierfisch Filet	\N
5221	496	3	3091	51	Grenadierfisch Stück	\N
5222	496	3	3092	52	Grenadierfisch Scheibe	\N
5223	496	3	3093	53	Grenadierfisch Kotelett	\N
5224	496	3	3094	55	Pollack Filet	\N
5225	496	3	3095	56	Pollack Stück	\N
5226	496	3	3096	57	Pollack Scheibe	\N
5227	496	3	3097	58	Pollack Kotelett	\N
5228	496	3	3098	60	Blauer Wittling Filet	\N
5229	496	3	3099	61	Blauer Wittling Stück	\N
5230	496	3	3100	62	Blauer Wittling Scheibe	\N
5231	496	3	3101	63	Blauer Wittling Kotelett	\N
5232	496	3	3102	65	Alaska Pollack Filet	\N
5233	496	3	3103	66	Alaska Pollack Stück	\N
5234	496	3	3104	67	Alaska Pollack Scheibe	\N
5235	496	3	3105	68	Alaska Pollack Kotelett	\N
5236	497	3	3107	5	Hornhecht Filet	\N
5237	497	3	3108	6	Hornhecht Stück	\N
5238	498	3	3110	25	Grauer Knurrhahn Filet	\N
5239	498	3	3111	26	Grauer Knurrhahn Stück	\N
5240	498	3	3112	30	Roter Knurrhahn Filet	\N
5241	498	3	3113	31	Roter Knurrhahn Stück	\N
5242	498	3	3114	35	Seehase Filet	\N
5243	498	3	3115	36	Seehase Stück	\N
5244	499	3	14287	2	Wolfsbarsch Filet	\N
5245	499	3	3117	5	Stöker Filet	\N
5246	499	3	3118	10	Meerbrasse Filet	\N
5247	499	3	3119	15	Adlerfisch Filet	\N
5248	499	3	3120	20	Meerbarbe Filet	\N
5249	499	3	3121	25	Butterfisch/Buttermakrele Filet	\N
5250	499	3	3122	26	Butterfisch/Buttermakrele Stück	\N
5251	499	3	3123	27	Butterfisch/Buttermakrele Scheibe	\N
5252	499	3	3124	28	Butterfisch/Buttermakrele Kotelett	\N
5253	499	3	3125	30	Katfisch gestreifter Filet	\N
5254	499	3	3126	35	Katfisch gefleckter Filet	\N
5255	499	3	3127	40	Rotbarsch Filet	\N
5256	499	3	3128	41	Rotbarsch Stück	\N
5257	499	3	3129	42	Rotbarsch Scheibe	\N
5258	499	3	3130	43	Rotbarsch Kotelett	\N
5259	499	3	3131	45	Makrele nordatlantische Filet	\N
5260	499	3	3132	50	Makrele pazifische Filet	\N
5261	499	3	3133	55	Thunfisch Filet	\N
5262	499	3	3134	56	Thunfisch Stück	\N
5263	499	3	3135	57	Thunfisch Scheibe	\N
5264	499	3	3136	58	Thunfisch Kotelett	\N
5265	499	3	3137	60	Bonito Filet	\N
5266	499	3	3138	61	Bonito Stück	\N
5267	499	3	3139	62	Bonito Scheibe	\N
5268	499	3	3140	63	Bonito Kotelett	\N
5269	499	3	3141	65	Pelamide Filet	\N
5270	499	3	3142	66	Pelamide Stück	\N
5271	499	3	3143	67	Pelamide Scheibe	\N
5272	499	3	3144	68	Pelamide Kotelett	\N
5273	499	3	3145	70	Kaiserbarsch Filet	\N
5274	499	3	3146	71	Kaiserbarsch Stück	\N
5275	499	3	3147	72	Kaiserbarsch Scheibe	\N
5276	499	3	3148	73	Kaiserbarsch Kotelett	\N
5277	499	3	3149	75	Makrelenhecht Filet	\N
5278	499	3	3150	80	Brachsenmakrele Filet	\N
5279	499	3	3151	90	Schwertfisch Filet	\N
5280	499	3	3152	91	Schwertfisch Stück	\N
5281	499	3	3153	92	Schwertfisch Scheibe	\N
5282	499	3	3154	93	Schwertfisch Kotelett	\N
5283	499	3	13840	95	Tilapia Buntbarsch Filet	\N
5284	499	3	13841	96	Tilapia Buntbarsch Stück	\N
5285	500	3	3156	5	Kleist Filet	\N
5286	500	3	3157	10	Steinbutt Filet	\N
5287	500	3	3158	15	Flügelbutt Filet	\N
5288	500	3	3159	20	Lammbutt Filet	\N
5289	500	3	3160	25	Scholle Filet	\N
5290	500	3	14236	26	Scholle pazifische (Lepidopsetta bilineata) Filet	\N
5291	500	3	14237	27	Scholle atlantische (Pleuronectes platessa) Filet Goldbutt Filet	\N
5292	500	3	3161	30	Heilbutt Filet	\N
5293	500	3	3162	31	Heilbutt Stück	\N
5294	500	3	3163	32	Heilbutt Scheibe	\N
5295	500	3	3164	33	Heilbutt Kotelett	\N
5296	500	3	3165	35	Schwarzer Heilbutt Filet	\N
5297	500	3	3166	36	Schwarzer Heilbutt Stück	\N
5298	500	3	3167	40	Hundszunge Filet	\N
5299	500	3	3168	41	Rotzunge Filet	\N
5300	500	3	3169	45	Seezunge Filet	\N
5301	500	3	3170	50	Flunder Filet	\N
5302	500	3	3171	55	Klische Filet	\N
5303	500	3	3172	60	Limande Filet	\N
5304	500	3	3173	65	Doggerscharbe Filet	\N
5305	501	3	3179	10	Grouper Filet	\N
5306	501	3	3180	11	Grouper Stück	\N
5307	501	3	3181	12	Grouper Scheibe	\N
5308	501	3	3182	13	Grouper Kotelett	\N
5309	501	3	3183	15	Meeräsche Filet	\N
5310	501	3	3184	30	Whiptail Filet	\N
5311	501	3	3185	31	Whiptail Stück	\N
5312	501	3	3186	32	Whiptail Scheibe	\N
5313	501	3	3187	33	Whiptail Kotelett	\N
5314	502	3	3189	5	Lachs Filet	\N
5315	502	3	3190	6	Lachs Stück/Seite	\N
5316	502	3	3191	7	Lachs Scheibe	\N
5317	502	3	3192	8	Lachs Kotelett	\N
5318	502	3	3193	10	Bachforelle Filet	\N
5319	502	3	3194	15	Regenbogenforelle Filet	\N
5320	502	3	3195	16	Regenbogenforelle Stück	\N
5321	502	3	3196	17	Regenbogenforelle Kotelett	\N
5322	502	3	3197	20	Seeforelle Filet	\N
5323	502	3	3198	30	Forelle Filet	\N
5324	502	3	3199	31	Forelle Stück	\N
5325	502	3	3200	32	Forelle Kotelett	\N
5326	502	3	3201	45	Renke Filet	\N
5327	502	3	3202	65	Lachsforelle Filet	\N
5328	502	3	3203	66	Lachsforelle Stück	\N
5329	502	3	3204	67	Lachsforelle Scheibe	\N
5330	502	3	3205	68	Lachsforelle Kotelett	\N
5331	503	3	3207	5	Hecht Filet	\N
5332	503	3	3208	6	Hecht Stück	\N
5333	503	3	3209	7	Hecht Scheibe	\N
5334	503	3	3210	8	Hecht Kotelett	\N
5335	504	3	3212	5	Karpfen Stück	\N
5336	504	3	3213	6	Karpfen Filet	\N
5337	504	3	3214	7	Karpfen Kotelett	\N
5338	504	3	3215	10	Wels Filet	\N
5339	504	3	3216	11	Wels Stück	\N
5340	504	3	3217	12	Wels Scheibe	\N
5341	504	3	3218	13	Wels Kotelett	\N
5342	504	3	3219	15	Schleie Stück	\N
5343	504	3	3220	16	Schleie Filet	\N
5344	504	3	13842	21	Schlankwels Filet	\N
5345	504	3	13843	22	Schlankwels Stück	\N
5346	505	3	3222	5	Aal Stück	\N
5347	506	3	3224	5	Flussbarsch Filet	\N
5348	506	3	3225	6	Flussbarsch Stück	\N
5349	506	3	3226	10	Zander Filet	\N
5350	506	3	3227	11	Zander Stück	\N
5351	506	3	3228	25	Victoriabarsch Stück	\N
5352	506	3	3229	26	Victoriabarsch Scheibe	\N
5353	506	3	3230	27	Victoriabarsch Filet	\N
5354	506	3	3231	28	Victoriabarsch Kotelett	\N
5355	507	3	3233	5	Stör Filet	\N
5356	507	3	3234	6	Stör Stück	\N
5357	507	3	3235	7	Stör Scheibe	\N
5358	507	3	3236	8	Stör Kotelett	\N
5359	508	3	3238	1	Hering Rogen	\N
5360	508	3	3239	2	Meerforelle Rogen	\N
5361	508	3	3240	3	Blauleng Rogen	\N
5362	508	3	3241	4	Kabeljau Rogen	\N
5363	508	3	3242	5	Grönland Kabeljau Rogen	\N
5364	508	3	3243	6	Schellfisch Rogen	\N
5365	508	3	3244	7	Seelachs Rogen	\N
5366	508	3	3245	8	Wittling Rogen	\N
5367	508	3	3246	9	Seehecht Rogen	\N
5368	508	3	3247	10	Pollack Rogen	\N
5369	508	3	3248	11	Seehase Rogen	\N
5370	509	3	3250	1	Lumb Leber	\N
5371	509	3	3251	2	Leng Leber	\N
5372	509	3	3252	3	Blauleng Leber	\N
5373	509	3	3253	4	Kabeljau Leber	\N
5374	509	3	3254	5	Grönland Kabeljau Leber	\N
5375	509	3	3255	6	Schellfisch Leber	\N
5376	509	3	3256	7	Seelachs Leber	\N
5377	509	3	3257	8	Wittling Leber	\N
5378	509	3	3258	9	Seehecht Leber	\N
5379	509	3	3259	10	Grenadierfisch Leber	\N
5380	509	3	3260	11	Pollack Leber	\N
5381	509	3	3261	12	Thunfisch Leber	\N
5382	509	3	3262	13	Bonito Leber	\N
5383	509	3	3263	14	Pelamide Leber	\N
5384	509	3	3264	15	Alaska Pollack Leber	\N
5385	509	3	3265	16	Heringshai Leber	\N
5386	509	3	3266	17	Riesenhai Leber	\N
5387	509	3	3267	18	Dornhai Leber	\N
5388	509	3	3268	19	Eishai Leber	\N
5389	510	3	3270	1	Lachs Rogen	\N
5390	510	3	3271	2	Bachforelle Rogen	\N
5391	510	3	3272	3	Regenbogenforelle Rogen	\N
5392	510	3	3273	4	Seeforelle Rogen	\N
5393	510	3	3274	5	Karpfen Rogen	\N
5394	510	3	3275	6	Wels Rogen	\N
5395	510	3	3276	7	Katzenwels Rogen	\N
5396	510	3	3277	8	Stör Rogen	\N
5397	511	3	3279	1	Lachs Leber	\N
5398	511	3	3280	2	Karpfen Leber	\N
5399	511	3	3281	3	Wels Leber	\N
5400	511	3	3282	4	Katzenwels Leber	\N
5401	511	3	3283	5	Stör Leber	\N
5402	512	3	13156	1	Fischspieß	\N
5403	513	3	3311	1	Stockfisch	\N
5404	513	3	3312	2	Klippfisch	\N
5405	514	3	3314	1	Sprotte geräuchert	\N
5406	514	3	3315	2	Bückling	\N
5407	514	3	3316	3	Räucherrollmops	\N
5408	514	3	3317	4	Makrele geräuchert	\N
5409	514	3	3318	5	Aal geräuchert	\N
5410	514	3	3319	6	Seeaal geräuchert	\N
5411	514	3	3320	7	Schillerlocke	\N
5412	514	3	3321	8	Bücklingsfilet	\N
5413	514	3	3322	9	Delikatessbückling	\N
5414	514	3	3323	10	Fleckhering	\N
5415	514	3	3324	11	Kipper auf norwegische Art	\N
5416	514	3	3326	13	Fleckmakrele	\N
5417	514	3	3327	14	Kalbfisch geräuchert	\N
5418	514	3	3328	15	Speckfisch geräuchert	\N
5419	514	3	3329	16	Stör geräuchert	\N
5420	514	3	3330	17	Rogen geräuchert	\N
5421	514	3	3331	18	Kabeljau geräuchert	\N
5422	514	3	3332	19	Lachsmakrele	\N
5423	514	3	3333	20	Lachs geräuchert	\N
5424	514	3	3335	22	Lachshering	\N
5425	514	3	3336	23	Lachsheringsfilet	\N
5426	514	3	3337	24	Delikatesslachshering	\N
5427	514	3	3338	25	Kipper	\N
5428	514	3	3340	27	Rollmops geräuchert	\N
5429	514	3	3341	28	Bratbückling geräuchert	\N
5430	514	3	3342	29	Stremellachs	\N
5431	514	3	3343	30	Forelle geräuchert	\N
5432	514	3	3344	31	Forellenfilet geräuchert	\N
5433	514	3	3345	32	Karpfen geräuchert	\N
5434	514	3	3346	33	Makrelenfilet geräuchert	\N
5435	514	3	3347	34	Rotbarsch geräuchert	\N
5436	514	3	3348	35	Heilbutt geräuchert	\N
5437	514	3	3349	36	Schwarzer Heilbutt geräuchert	\N
5438	514	3	3350	37	Scholle geräuchert	\N
5439	514	3	3351	38	Flunder geräuchert	\N
5440	514	3	3352	39	Katfisch geräuchert	\N
5441	514	3	3353	40	Thunfisch geräuchert	\N
5442	514	3	3354	41	Meeraal geräuchert	\N
5443	514	3	3355	42	Stückenfisch geräuchert	\N
5444	514	3	3356	43	Wels geräuchert	\N
5445	514	3	3357	44	Schellfisch geräuchert	\N
5446	514	3	3358	45	Renke geräuchert	\N
5447	514	3	3359	46	Pazifiklachs geräuchert	\N
5448	514	3	3360	47	Kilka geräuchert	\N
5449	514	3	3361	48	Seeforelle geräuchert	\N
5450	514	3	3362	49	Lachsforelle geräuchert	\N
5451	514	3	3363	50	Lachsforellenfilet geräuchert	\N
5452	515	3	3365	1	Salzhering	\N
5453	515	3	3366	2	Fetthering	\N
5454	515	3	3367	3	Vollhering	\N
5455	515	3	3368	4	Vollhering mild gesalzen	\N
5456	515	3	3369	5	Matjeshering	\N
5457	515	3	3370	6	Yhlenhering	\N
5458	515	3	3371	7	Sardelle gesalzen	\N
5459	515	3	3372	9	Pollack gesalzen	\N
5460	515	3	3373	10	Kabeljau gesalzen	\N
5461	515	3	3374	11	Seelachs gesalzen	\N
5462	515	3	3375	12	Leng gesalzen	\N
5463	515	3	3376	13	Lumb gesalzen	\N
5464	515	3	3377	14	Wrackhering	\N
5465	515	3	3378	15	Schellfisch gesalzen	\N
5466	515	3	3379	16	Lachs gesalzen	\N
5467	515	3	3380	17	Fischfilet gesalzen	\N
5468	515	3	3381	18	Pazifiklachs gesalzen	\N
5469	516	3	3383	1	Salzheringsfilet	\N
5470	516	3	3384	2	Sardellenfilet	\N
5471	516	3	3385	3	Sardellenringe	\N
5472	516	3	3386	4	Sardellenpaste	\N
5473	516	3	3387	5	Anchovispaste	\N
5474	516	3	3388	6	Lachspaste	\N
5475	516	3	3389	7	Lachsscheiben in Öl	\N
5476	516	3	3390	8	Seelachsscheiben in Öl	\N
5477	516	3	3391	9	Seelachspaste	\N
5478	516	3	3392	10	Seelachsschnitzel in Öl	\N
5479	516	3	3393	11	Kaviar	\N
5480	516	3	3394	12	Lachskaviar	\N
5481	516	3	3395	13	Deutscher Kaviar	\N
5482	516	3	3397	15	Presskaviar	\N
5483	516	3	3398	16	Lachsschnitzel in Öl	\N
5484	516	3	3399	17	Salzhering entgrätet	\N
5485	516	3	3400	18	Salzhering entgrätet und enthäutet	\N
5486	516	3	3401	19	Salzheringsfilet in Aufguss	\N
5487	516	3	3402	20	Salzheringsfilet in Öl	\N
5488	516	3	3403	21	Kaviarpaste	\N
5489	516	3	3404	22	Aalpaste	\N
5490	516	3	3405	23	Seehasenkaviar ausländischer	\N
5491	516	3	3406	24	Deutscher Kaviar nicht aus Seehasenrogen	\N
5492	516	3	3407	25	Rogenerzeugnis ausländisches	\N
5493	516	3	3408	26	Forellenkaviar	\N
5494	516	3	3409	27	Sardellenringe mit Füllung	\N
5495	516	3	3410	28	Matjesfilet	\N
5496	516	3	3411	29	Matjesfilet in Öl	\N
5497	516	3	14197	30	Kaviarersatz aus Fischmuskeleiweiß	\N
5498	516	3	14224	31	Capelinkaviar isländischer Kaviar Masago	\N
5499	516	3	14225	32	Fliegender Fisch-Kaviar Tobiko	\N
5500	516	3	14226	33	Belugakaviar	\N
5501	516	3	14227	34	Osietrakaviar	\N
5502	516	3	14228	35	Sevrugakaviar	\N
5503	517	3	3413	1	Anchovis Anchose	\N
5504	517	3	3414	2	Appetitsild Anchose	\N
5505	517	3	3415	3	Gabelbissen/-happen Anchose	\N
5506	517	3	3416	4	Matjesfilet Anchose	\N
5507	517	3	3417	5	Kräuter-/Gewürzhering Anchose	\N
5508	517	3	3418	6	Kräuter-/Gewürzheringsfilet Anchose	\N
5509	517	3	3419	7	Matjesfilet nordische Art Anchose	\N
5510	517	3	3420	8	Gabelbissen/-happen süßsauer Anchose	\N
5511	517	3	3421	9	Anchovis süßsauer Anchose	\N
5512	517	3	3422	10	Heringsfilet Matjesart Anchose	\N
5513	517	3	3423	11	Heringsfilet nordische Art Anchose	\N
5514	517	3	3424	12	Kräuter-/Gewürzheringsfilet süßsauer Anchose	\N
5515	517	3	3425	13	Matjesfilet in Öl Anchose	\N
5516	517	3	3426	14	Heringsfilet Matjesart in Öl Anchose	\N
5517	518	3	3428	1	Heringsfleisch gesäuert	\N
5518	518	3	3429	2	Saurer Hering	\N
5519	518	3	3430	3	Hering mariniert	\N
5520	518	3	3431	4	Delikatesshering	\N
5521	518	3	3432	5	Hering eingelegt	\N
5522	518	3	3433	6	Bismarckhering	\N
5523	518	3	3434	7	Heringshappen mariniert	\N
5524	518	3	3435	8	Rollmops	\N
5525	518	3	3436	9	Gabelrollmops	\N
5526	518	3	3437	10	Heringsstip	\N
5527	518	3	3438	11	Kronsild	\N
5528	518	3	3439	12	Heringstopf	\N
5529	518	3	3440	13	Heringsbecher	\N
5530	518	3	3441	14	Hering Hausfrauenart	\N
5531	518	3	3442	15	Makrelenhappen mariniert	\N
5532	518	3	3443	16	Graved Lachs	\N
5533	518	3	3444	17	Heringsfilet eingelegt	\N
5534	519	3	3446	1	Brathering	\N
5535	519	3	3447	2	Bratheringsfilet	\N
5536	519	3	3448	3	Bratheringshappen	\N
5537	519	3	3449	4	Bratheringsstücke	\N
5538	519	3	3450	5	Bratrollmops	\N
5539	519	3	3451	6	Bratschellfisch	\N
5540	519	3	3452	7	Bratheringsröllchen	\N
5541	519	3	3453	8	Bratmakrele	\N
5542	519	3	3454	9	Bratsardine	\N
5543	519	3	3455	10	Aalbricke Bratfisch	\N
5544	519	3	3456	11	Bratbückling	\N
5545	520	3	3458	1	Hering in Gelee Kochfischware	\N
5546	520	3	3459	2	Rollmops in Gelee Kochfischware	\N
5547	520	3	3460	3	Speckrollmops in Gelee Kochfischware	\N
5548	520	3	3461	4	Seeaal in Gelee Kochfischware	\N
5549	520	3	3462	5	Aal in Gelee Kochfischware	\N
5550	520	3	3463	6	Makrele in Gelee Kochfischware	\N
5551	520	3	3464	7	Fischsülze Kochfischware	\N
5552	520	3	3465	8	Forelle in Gelee Kochfischware	\N
5553	521	3	3467	1	Brathering in Gelee	\N
5554	521	3	3468	2	Bratrollmops in Gelee	\N
5555	521	3	3469	3	Schillerlocke in Gelee	\N
5556	521	3	3470	4	Thunfisch in Gelee	\N
5557	521	3	3471	5	Bücklingsfilet in Gelee	\N
5558	521	3	3472	6	Forellenfilet geräuchert in Gelee	\N
5559	521	3	3473	7	Räucheraal in Gelee	\N
5560	521	3	3474	8	Renkenfilet geräuchert in Gelee	\N
5561	522	3	3476	1	Heringsfilet in Aufguss Präserve	\N
5562	522	3	3477	2	Brathering in Aufguss Präserve	\N
5563	522	3	3478	3	Salzheringsfilet Präserve	\N
5564	522	3	3479	4	Sardellenfilet Präserve	\N
5565	522	3	3480	5	Sardellenringe Präserve	\N
5566	522	3	3481	6	Sardellenpaste Präserve	\N
5567	522	3	3482	7	Anchovispaste Präserve	\N
5568	522	3	3483	8	Lachspaste Präserve	\N
5569	522	3	3484	9	Lachsscheiben in Öl Präserve	\N
5570	522	3	3485	10	Seelachsscheiben in Öl Präserve	\N
5571	522	3	3486	11	Seelachspaste Präserve	\N
5572	522	3	3487	12	Seelachsschnitzel in Öl Präserve	\N
5573	522	3	3488	13	Kaviar Präserve	\N
5574	522	3	3489	14	Lachskaviar Präserve	\N
5575	522	3	3490	15	Deutscher Kaviar Präserve	\N
5576	522	3	3492	17	Lachsschnitzel in Öl Präserve	\N
5577	522	3	3493	18	Salzhering entgrätet und enthäutet Präserve	\N
5578	522	3	3494	19	Salzheringsfilet in Aufguss Präserve	\N
5579	522	3	3495	20	Salzheringsfilet in Öl Präserve	\N
5580	522	3	3496	21	Kaviarpaste Präserve	\N
5581	522	3	3497	22	Aalpaste Präserve	\N
5582	522	3	3498	23	Seehasenkaviar ausländischer Präserve	\N
5583	522	3	3499	24	Deutscher Kaviar nicht aus Seehasenrogen Präserve	\N
5584	522	3	3500	25	Rogenerzeugnis ausländisches Präserve	\N
5585	522	3	3501	26	Anchovis Präserve	\N
5586	522	3	3502	27	Appetitsild Präserve	\N
5587	522	3	3503	28	Gabelbissen/-happen Präserve	\N
5588	522	3	3504	29	Matjesfilet Präserve	\N
5589	522	3	3505	30	Kräuter-/Gewürzhering Präserve	\N
5590	522	3	3506	31	Kräuter-/Gewürzheringsfilet Präserve	\N
5591	522	3	3507	32	Matjesfilet nordische Art Präserve	\N
5592	522	3	3508	33	Gabelbissen/-happen süßsauer Präserve	\N
5593	522	3	3509	34	Anchovis süßsauer Präserve	\N
5594	522	3	3510	35	Heringsfilet Matjesart Präserve	\N
5595	522	3	3511	36	Heringsfilet nordische Art Präserve	\N
5596	522	3	3512	37	Kräuter-/Gewürzheringsfilet süßsauer Präserve	\N
5597	522	3	3513	38	Matjesfilet in Öl Präserve	\N
5598	522	3	3514	39	Heringsfilet Matjesart in Öl Präserve	\N
5599	522	3	3515	40	Heringsfleisch gesäuert in Aufguss Präserve	\N
5600	522	3	3516	41	Saurer Hering in Aufguss Präserve	\N
5601	522	3	3517	42	Hering mariniert in Aufguss Präserve	\N
5602	522	3	3518	43	Delikatesshering in Aufguss Präserve	\N
5603	522	3	3519	44	Hering eingelegt Präserve	\N
5604	522	3	3520	45	Bismarckhering in Aufguss Präserve	\N
5605	522	3	3521	46	Heringshappen mariniert in Aufguss Präserve	\N
5606	522	3	3522	47	Rollmops in Aufguss Präserve	\N
5607	522	3	3523	48	Gabelrollmops in Aufguss Präserve	\N
5608	522	3	3524	49	Heringsstip Präserve	\N
5609	522	3	3525	50	Kronsild in Aufguss Präserve	\N
5610	522	3	3526	51	Heringstopf Präserve	\N
5611	522	3	3527	52	Heringsbecher Präserve	\N
5612	522	3	3528	53	Hering Hausfrauenart Präserve	\N
5613	522	3	3529	54	Makrelenhappen mariniert in Aufguss Präserve	\N
5614	522	3	3530	55	Graved Lachs Präserve	\N
5615	522	3	3531	56	Heringsfilet eingelegt Präserve	\N
5616	522	3	3532	57	Bratheringsfilet in Aufguss Präserve	\N
5617	522	3	3533	58	Bratheringshappen in Aufguss Präserve	\N
5618	522	3	3534	59	Bratheringsstücke in Aufguss Präserve	\N
5619	522	3	3535	60	Bratrollmops in Aufguss Präserve	\N
5620	522	3	3536	61	Bratheringsröllchen in Aufguss Präserve	\N
5621	522	3	3537	62	Bratmakrele in Aufguss Präserve	\N
5622	522	3	3538	63	Bratsardine in Aufguss Präserve	\N
5623	522	3	3539	64	Aalbricke in Aufguss Präserve	\N
5624	522	3	3540	65	Hering in Gelee Präserve	\N
5625	522	3	3541	66	Rollmops in Gelee Präserve	\N
5626	522	3	3542	67	Speckrollmops in Gelee Präserve	\N
5627	522	3	3543	68	Seeaal in Gelee Präserve	\N
5628	522	3	3544	69	Aal in Gelee Präserve	\N
5629	522	3	3545	70	Makrele in Gelee Präserve	\N
5630	522	3	3546	71	Fischsülze Präserve	\N
5631	522	3	3547	72	Brathering in Gelee Präserve	\N
5632	522	3	3548	73	Bratrollmops in Gelee Präserve	\N
5633	522	3	3549	74	Schillerlocke in Gelee Präserve	\N
5634	522	3	3550	75	Dorschleberpastete Präserve	\N
5635	522	3	3551	76	Fischpastete Präserve	\N
5636	522	3	3552	77	Fischkloß Präserve	\N
5637	522	3	3553	78	Fischfrikadelle Präserve	\N
5638	522	3	3554	79	Fischvorspeise Präserve	\N
5639	522	3	3555	80	Fischwurst Präserve	\N
5640	522	3	3556	81	Sardellenringe mit Füllung Präserve	\N
5641	522	3	3557	82	Renkenfilet geräuchert in Gelee Präserve	\N
5642	522	3	3558	83	Forellenfilet geräuchert in Gelee Präserve	\N
5643	522	3	3559	84	Forellenkaviar Präserve	\N
5644	522	3	14229	85	Capelinkaviar Präserve	\N
5645	522	3	14230	86	Fliegender Fisch-Kaviar Präserve	\N
5646	522	3	14231	87	Belugakaviar Präserve	\N
5647	522	3	14232	88	Osietrakaviar Präserve	\N
5648	522	3	14233	89	Sevrugakaviar Präserve	\N
5649	523	3	3561	1	Hering in eigenem Saft Konserve	\N
5650	523	3	3562	2	Makrele in eigenem Saft Konserve	\N
5651	523	3	3564	4	Sardine in eigenem Saft Konserve	\N
5652	523	3	3565	5	Sprotte in eigenem Saft Konserve	\N
5653	523	3	3566	6	Sardinops in eigenem Saft Konserve	\N
5654	523	3	3567	7	Hering in eigenem Saft und Aufguss Konserve	\N
5655	523	3	3568	8	Makrele in eigenem Saft und Aufguss Konserve	\N
5656	523	3	3570	10	Sardine in eigenem Saft und Aufguss Konserve	\N
5657	523	3	3571	11	Sprotte in eigenem Saft und Aufguss Konserve	\N
5658	523	3	3572	12	Sardinops in eigenem Saft und Aufguss Konserve	\N
5659	523	3	3573	13	Hering in Aufguss Konserve	\N
5660	523	3	3574	14	Makrele in Aufguss Konserve	\N
5661	523	3	3576	16	Sardine in Aufguss Konserve	\N
5662	523	3	3577	17	Sprotte in Aufguss Konserve	\N
5663	523	3	3578	18	Sardinops in Aufguss Konserve	\N
5664	523	3	3579	19	Hering in Öl Konserve	\N
5665	523	3	3580	20	Makrele in Öl Konserve	\N
5666	523	3	3582	22	Sardine in Öl Konserve	\N
5667	523	3	3583	23	Sprotte in Öl Konserve	\N
5668	523	3	3584	24	Sardinops in Öl Konserve	\N
5669	523	3	3585	25	Thunfisch in Öl Konserve	\N
5670	523	3	3586	26	Hering in Öl und eigenem Saft Konserve	\N
5671	523	3	3587	27	Makrele in Öl und eigenem Saft Konserve	\N
5672	523	3	3589	29	Sardine in Öl und eigenem Saft Konserve	\N
5673	523	3	3590	30	Sprotte in Öl und eigenem Saft Konserve	\N
5674	523	3	3591	31	Sardinops in Öl und eigenem Saft Konserve	\N
5675	523	3	3592	32	Dorschleberpaste Konserve	\N
5676	523	3	3593	33	Seelachspaste Konserve	\N
5677	523	3	3594	34	Dorschleber in Öl Konserve	\N
5678	523	3	3595	35	Thunfisch in eigenem Saft Konserve	\N
5679	523	3	3596	36	Fischsortiment in eigenem Saft Konserve	\N
5680	523	3	3597	37	Thunfisch in Aufguss Konserve	\N
5681	523	3	3598	38	Fischsortiment in Aufguss Konserve	\N
5682	523	3	3599	39	Thunfisch in Öl und eigenem Saft Konserve	\N
5683	523	3	3600	40	Fischsortiment in Öl und eigenem Saft Konserve	\N
5684	523	3	3601	41	Hering in Soße/Krem Konserve	\N
5685	523	3	3602	42	Sardine in Soße/Krem Konserve	\N
5686	523	3	3603	43	Sardinops in Soße/Krem Konserve	\N
5687	523	3	3604	44	Sprotte in Soße/Krem Konserve	\N
5688	523	3	3605	45	Makrele in Soße/Krem Konserve	\N
5689	523	3	3606	46	Thunfisch in Soße/Krem Konserve	\N
5690	523	3	3608	48	Fischsortiment in Soße/Krem Konserve	\N
5691	523	3	3609	49	Lachs in Öl Konserve	\N
5692	523	3	3610	50	Lachs in Öl und eigenem Saft Konserve	\N
5693	523	3	3611	51	Hering in Aufguss oder Öl mit anderen beigegeb. LM Konserve	\N
5694	523	3	3612	52	Sardine in Aufguss oder Öl mit anderen beigegeb. LM Konserve	\N
5695	523	3	3613	53	Sardinops in Aufguss oder Öl mit anderen beigegeb. LM Konserve	\N
5696	523	3	3614	54	Sprotte in Aufguss oder Öl mit anderen beigeg. LM Konserve	\N
5697	523	3	3615	55	Makrele in Aufguss oder Öl mit anderen beigeg. LM Konserve	\N
5698	523	3	3616	56	Thunfisch in Aufguss oder Öl mit anderen beigeg. LM Konserve	\N
5699	523	3	3617	57	Fischsortiment in Aufguss oder Öl mit and. beigeg. LM Konserve	\N
5700	523	3	3618	58	Bratheringsfilet in Aufguss Konserve	\N
5701	523	3	3619	59	Bratheringshappen in Aufguss Konserve	\N
5702	523	3	3620	60	Bratheringsstücke in Aufguss Konserve	\N
5703	523	3	3621	61	Bratrollmops in Aufguss Konserve	\N
5704	523	3	3622	62	Bratheringsröllchen in Aufguss Konserve	\N
5705	523	3	3623	63	Bratmakrele in Aufguss Konserve	\N
5706	523	3	3624	64	Bratsardine in Aufguss Konserve	\N
5707	523	3	3625	65	Aalbricke in Aufguss Konserve	\N
5708	523	3	3626	66	Seelachsscheibe in Öl Konserve	\N
5709	523	3	3627	67	Seelachsschnitzel in Öl Konserve	\N
5710	523	3	3628	70	Lachspaste Konserve	\N
5711	523	3	3629	71	Anchovispaste Konserve	\N
5712	523	3	3630	72	Sardellenpaste Konserve	\N
5713	523	3	3631	73	Kaviarpaste Konserve	\N
5714	523	3	3632	74	Fischkloß Konserve	\N
5715	523	3	3633	75	Fischfrikadelle Konserve	\N
5716	523	3	3634	76	Fischvorspeise Konserve	\N
5717	523	3	3635	77	Aal geräuchert in Öl Konserve	\N
5718	523	3	3636	78	Makrele geräuchert in Öl Konserve	\N
5719	523	3	3637	79	Seeaal in Öl Konserve	\N
5720	523	3	3638	80	Fischpaste Konserve	\N
5721	523	3	3639	81	Fischwurst Konserve	\N
5722	523	3	3640	82	Räucheraalpaste Konserve	\N
5723	523	3	3641	83	Kilka geräuchert in Öl Konserve	\N
5724	523	3	3642	84	Thunfischcocktail Konserve	\N
5725	523	3	13844	85	Thunfisch in Aufguss und eigenem Saft Konserve	\N
5726	524	3	3644	1	Fischfrikadelle auch vorgebraten auch tiefgefroren	\N
5727	524	3	3645	2	Fischstäbchen auch vorgebraten auch tiefgefroren	\N
5728	524	3	3646	3	Fischwurst auch tiefgefroren	\N
5729	524	3	3647	4	Heringsfilet paniert auch tiefgefroren	\N
5730	524	3	3648	5	Sardinenfilet paniert auch tiefgefroren	\N
5731	524	3	3649	6	Lachsfilet paniert auch tiefgefroren	\N
5732	524	3	3650	7	Forellenfilet paniert auch tiefgefroren	\N
5733	524	3	3651	8	Renkenfilet paniert auch tiefgefroren	\N
5734	524	3	3652	9	Seehechtfilet paniert auch tiefgefroren	\N
5735	524	3	3653	10	Kabeljaufilet paniert auch tiefgefroren	\N
5736	524	3	3654	11	Schellfischfilet paniert auch tiefgefroren	\N
5737	524	3	3655	12	Wittlingfilet paniert auch tiefgefroren	\N
5738	524	3	3656	13	Pollackfilet paniert auch tiefgefroren	\N
5739	524	3	3657	14	Seelachsfilet paniert auch tiefgefroren	\N
5740	524	3	3658	15	Lengfilet paniert auch tiefgefroren	\N
5741	524	3	3659	16	Blaulengfilet paniert auch tiefgefroren	\N
5742	524	3	3660	17	Lumbfilet paniert auch tiefgefroren	\N
5743	524	3	3661	18	Katfischfilet paniert auch tiefgefroren	\N
5744	524	3	3662	19	Makrelenfilet paniert auch tiefgefroren	\N
5745	524	3	3663	20	Thunfischfilet paniert auch tiefgefroren	\N
5746	524	3	3664	21	Rotbarschfilet paniert auch tiefgefroren	\N
5747	524	3	3665	22	Seezungenfilet paniert auch tiefgefroren	\N
5748	524	3	3666	23	Flunderfilet paniert auch tiefgefroren	\N
5749	524	3	3667	24	Klischenfilet paniert auch tiefgefroren	\N
5750	524	3	3668	25	Schollenfilet paniert auch tiefgefroren	\N
5751	524	3	3669	26	Limandenfilet paniert auch tiefgefroren	\N
5752	524	3	3670	27	Doggerscharbenfilet paniert auch tiefgefroren	\N
5753	524	3	3671	28	Heilbuttfilet paniert auch tiefgefroren	\N
5754	524	3	3672	29	Kleistfilet paniert auch tiefgefroren	\N
5755	524	3	3673	30	Flügelbutt paniert auch tiefgefroren	\N
5756	524	3	3674	31	Anglerfilet paniert auch tiefgefroren	\N
5757	524	3	3675	32	Kaiserbarschfilet paniert auch tiefgefroren	\N
5758	524	3	3676	33	Haifisch auch Stücke küchenmäßig vorber. auch tiefgefr.	\N
5759	524	3	3677	34	Aal auch Stücke küchenmäßig vorber. auch tiefgefr.	\N
5760	524	3	3678	35	Karpfen auch Stücke küchenmäßig vorber. auch tiefgefr.	\N
5761	524	3	3679	36	Schleie auch Stücke küchenmäßig vorber. auch tiefgefr.	\N
5762	524	3	3680	37	Hering auch Stücke küchenmäßig vorber. auch tiefgefr.	\N
5763	524	3	3681	38	Sardine auch Stücke küchenmäßig vorber. auch tiefgefr.	\N
5764	524	3	3682	39	Lachs auch Stücke küchenmäßig vorber. auch tiefgefr.	\N
5765	524	3	3683	40	Forelle auch Stücke küchenmäßig vorber. auch tiefgefr.	\N
5766	524	3	3684	41	Renke auch Stücke küchenmäßig vorber. auch tiefgefr.	\N
5767	524	3	3685	42	Seehecht auch Stücke küchenmäßig vorber. auch tiefgefr.	\N
5768	524	3	3686	43	Kabeljau auch Stücke küchenmäßig vorber. auch tiefgefr.	\N
5769	524	3	3687	44	Schellfisch auch Stücke küchenmäßig vorber. auch tiefgefr.	\N
5770	524	3	3688	45	Wittling auch Stücke küchenmäßig vorber. auch tiefgefr.	\N
5771	524	3	3689	46	Pollack auch Stücke küchenmäßig vorber. auch tiefgefr.	\N
5772	524	3	3690	47	Seelachs auch Stücke küchenmäßig vorber. auch tiefgefr.	\N
5773	524	3	3691	48	Leng auch Stücke küchenmäßig vorber. auch tiefgefr.	\N
5774	524	3	3692	49	Blauleng auch Stücke küchenmäßig vorber. auch tiefgefr.	\N
5775	524	3	3693	50	Lumb auch Stücke küchenmäßig vorber. auch tiefgefr.	\N
5776	524	3	3694	51	Katfisch auch Stücke küchenmäßig vorber. auch tiefgefr.	\N
5777	524	3	3695	52	Makrele auch Stücke küchenmäßig vorber. auch tiefgefr.	\N
5778	524	3	3696	53	Thunfisch auch Stücke küchenmäßig vorber. auch tiefgefr.	\N
5779	524	3	3697	54	Rotbarsch auch Stücke küchenmäßig vorber. auch tiefgefr.	\N
5780	524	3	3698	55	Seezunge auch Stücke küchenmäßig vorber. auch tiefgefr.	\N
5781	524	3	3699	56	Flunder auch Stücke küchenmäßig vorber. auch tiefgefr.	\N
5782	524	3	3700	57	Klische auch Stücke küchenmäßig vorber. auch tiefgefr.	\N
5783	524	3	3701	58	Scholle auch Stücke küchenmäßig vorber. auch tiefgefr.	\N
5784	524	3	3702	59	Limande auch Stücke küchenmäßig vorber. auch tiefgefr.	\N
5785	524	3	3703	60	Doggerscharbe auch Stücke küchenmäßig vorber. auch tiefgefr.	\N
5786	524	3	3704	61	Heilbutt auch Stücke küchenmäßig vorber. auch tiefgefr.	\N
5787	524	3	3705	62	Kleist auch Stücke küchenmäßig vorber. auch tiefgefr.	\N
5788	524	3	3706	63	Flügelbutt auch Stücke küchenmäßig vorber. auch tiefgefr.	\N
5789	524	3	3707	64	Anglerfisch auch Stücke küchenmäßig vorber. auch tiefgefr.	\N
5790	524	3	3708	65	Kaiserbarsch auch Stücke küchenmäßig vorber. auch tiefgefr.	\N
5791	524	3	3709	66	Rotzungenfilet paniert auch tiefgefroren	\N
5792	524	3	3710	67	Rotzunge auch Stücke küchenmäßig vorber. auch tiefgefr.	\N
5793	524	3	3711	68	Alaska Pollackfilet paniert auch tiefgefr.	\N
5794	524	3	3712	69	Alaska Pollack auch Stücke küchenmäßig vorber. auch tiefgefr.	\N
5795	524	3	3713	70	Lachsforellenfilet paniert auch tiefgefr.	\N
5796	524	3	3714	71	Lachsforelle auch Stücke küchenmäßig vorber. auch tiefgefr.	\N
5797	524	3	13845	72	Tilapia Buntbarsch auch Stücke küchenmäßig vorber. auch tiefgetr.	\N
5798	524	3	13846	73	Schlankwels auch Stücke küchenmäßig vorber. auch tiefgefr.	\N
5799	524	3	14307	74	Wolfsbarsch (Dicentrarchus labrax), auch Stücke küchenmäßig vorber. auch tiefgefr.	\N
5800	524	3	14306	75	Gefleckter Wolfsbarsch (Dicentrarchus punctatus) auch Stücke küchenmäßig vorber. auch tiefgefr.	\N
5801	525	3	3716	1	Hering in Soße/Krem brennwertreduziert Konserve	\N
5802	525	3	3717	2	Sardine in Soße/Krem brennwertreduziert Konserve	\N
5803	525	3	3718	3	Makrele in Soße/Krem brennwertreduziert Konserve	\N
5804	525	3	3719	4	Thunfisch in Soße/Krem brennwertreduziert Konserve	\N
5805	528	3	3722	1	Nordseekrabbe (Crangon crangon)	\N
5806	528	3	3723	2	Shrimps (Metapenaeus sp.)	\N
5807	528	3	3724	3	Prawns (Aristeomorpha sp.)	\N
5808	528	3	3725	4	Hummer (Homarus sp.)	\N
5809	528	3	3726	5	Kaisergranat (Nephrops norvegicus)	\N
5810	528	3	3727	6	Languste (Palinurus sp.)	\N
5811	528	3	3728	7	Kamtschatkakrabbe (Paralithodes camtschaticus)	\N
5812	528	3	3729	8	Königskrabbe (Paralithodes platypus)	\N
5813	528	3	3730	9	Taschenkrebs (Cancer pagurus)	\N
5814	528	3	3731	10	Edelkrebs (Astacus astacus)	\N
5815	528	3	3732	11	Amerikanischer Flusskrebs (Orconectes limosus)	\N
5816	528	3	3733	12	Krill (Euphausia superba)	\N
5817	528	3	3734	13	Tiefseegarnele (Pandalus sp.)	\N
5818	528	3	3735	14	Langustino (Galatheidae sp.)	\N
5819	528	3	3736	15	Langustenartiges Krustentier (Scyllaridae sp.)	\N
5820	528	3	3737	16	Geißelgarnele (Penaeidae sp.)	\N
5821	528	3	3738	17	Felsengarnele (Pelaemonidae)	\N
5822	528	3	3739	18	Rundkrabbe (Atelecyclidae sp.)	\N
5823	528	3	3740	19	Dreieckskrabbe (Majidae sp.)	\N
5824	528	3	3741	20	Seespinne (Maja sp.)	\N
5825	528	3	3742	21	Eismeerkrabbe (Chinoecetes sp.)	\N
5826	528	3	3743	22	Blaukrabbe (Portunidae sp.)	\N
5827	528	3	3744	23	Strandkrabbe (Carcinus maenas)	\N
5828	528	3	3745	24	Galizischer Krebs (Astacus leptodactylus)	\N
5829	528	3	3746	25	Australischer Flusskrebs (Euastacus serratus)	\N
5830	528	3	3747	26	Signalkrebs (Pacifastacus leniusculus)	\N
5831	528	3	3748	27	Steinkrabbe (Lithodidae sp.)	\N
5832	529	3	3750	1	Krabbenpaste	\N
5833	529	3	3751	2	Krabbenklöße	\N
5834	529	3	3752	3	Krabbenextrakt	\N
5835	529	3	3753	4	Krebsextrakt	\N
5836	529	3	3754	5	Crabmeat	\N
5837	529	3	3755	6	Krabben in Gelee	\N
5838	529	3	3756	7	Krabben in div. Tunken oder Mayonnaisen	\N
5839	529	3	3757	8	Hummer in Gelee	\N
5840	529	3	3758	9	Langusten in Gelee	\N
5841	529	3	3759	10	Krebstiere in div. Aufgüssen	\N
5842	529	3	3760	11	Krebsschwänze in div. Tunken	\N
5843	529	3	3761	12	Garnelen getrocknet	\N
5844	529	3	3762	13	Salzgarnelen	\N
5845	529	3	3763	14	Krebsschwänze gesalzen	\N
5846	529	3	3764	15	Garnelen gekocht nicht geschält	\N
5847	529	3	3765	16	Garnelenfleisch	\N
5848	529	3	3766	17	Garnelenfleisch in div. Tunken oder Mayonnaisen	\N
5849	529	3	3767	18	Hummer gekocht in div. Tunken oder Mayonnaisen	\N
5850	529	3	3768	19	Hummerschwänze/-fleisch	\N
5851	529	3	3769	20	Krebsschwänze/-fleisch	\N
5852	529	3	3770	21	Krebsfleisch in Gelee	\N
5853	529	3	3771	22	Krebsfleisch in div. Tunken oder Mayonnaisen	\N
5854	529	3	3772	23	Langustenschwänze/-fleisch	\N
5855	529	3	3773	25	Langusten in div. Tunken oder Mayonnaisen	\N
5856	529	3	3774	27	Krebspaste	\N
5857	529	3	3776	29	Nordseekrabben gekocht nicht geschält	\N
5858	529	3	3777	30	Nordseekrabbenfleisch	\N
5859	529	3	3778	31	Krebstiererzeugnis paniert	\N
5860	529	3	13294	32	Shrimpsmehl	\N
5861	530	3	3780	1	Miesmuschel (Mytilus sp.)	\N
5862	530	3	3781	2	Auster (Ostrea sp.)	\N
5863	530	3	3782	3	Herzmuschel (Cardium sp.)	\N
5864	530	3	3783	4	Jacobsmuschel (Pecten jacobaeus)	\N
5865	530	3	3784	5	Kammmuschel (Pectinidae sp.)	\N
5866	530	3	3785	6	Venusmuschel (Venus sp.)	\N
5867	530	3	3786	7	Klaffmuschel (Mya sp.)	\N
5868	530	3	3787	8	Trogmuschel (Spisula solida)	\N
5869	530	3	14095	9	Felsenauster (Crassostrea spp.)	\N
5870	530	3	14174	10	Grünschalenmuschel (Perna spp.)	\N
5871	530	3	14175	11	Teppichmuschel, Venerupis spp.	\N
5872	530	3	14176	12	Archenmuschel (Anadara spp.)	\N
5873	531	3	3789	1	Muschel in Tunke	\N
5874	531	3	3790	2	Muschel in div. Aufgüssen	\N
5875	531	3	3791	3	Muschel in Öl	\N
5876	531	3	3792	4	Muschel in Gelee	\N
5877	531	3	3793	5	Muschel geräuchert in Öl	\N
5878	531	3	3794	6	Muscheltiererzeugnis paniert	\N
5879	531	3	13847	10	Miesmuschelerzeugnisse	\N
5880	531	3	13848	11	Miesmuschel in Tunke	\N
5881	531	3	13849	12	Miesmuschel in div. Aufgüssen	\N
5882	531	3	13850	13	Miesmuschel in Öl	\N
5883	531	3	13851	14	Miesmuschel in Gelee	\N
5884	531	3	13852	15	Miesmuschel geräuchert in Öl	\N
5885	531	3	13853	16	Miesmuschelerzeugnis paniert	\N
5886	531	3	13854	20	Herzmuschelerzeugnisse	\N
5887	531	3	13855	21	Herzmuschel in Tunke	\N
5888	531	3	13856	22	Herzmuschel in div. Aufgüssen	\N
5889	531	3	13857	23	Herzmuschel in Öl	\N
5890	531	3	13858	24	Herzmuschel in Gelee	\N
5891	531	3	13859	25	Herzmuschel geräuchert in Öl	\N
5892	531	3	13860	26	Herzmuschelerzeugnis paniert	\N
5893	531	3	13861	30	Jakobsmuschelerzeugnisse	\N
5894	531	3	13862	31	Jakobsmuschel in Tunke	\N
5895	531	3	13863	32	Jakobsmuschel in div. Aufgüssen	\N
5896	531	3	13864	33	Jakobsmuschel in Öl	\N
5897	531	3	13865	34	Jakobsmuschel in Gelee	\N
5898	531	3	13866	35	Jakobsmuschel geräuchert in Öl	\N
5899	531	3	13867	36	Jakobsmuschelerzeugnis paniert	\N
5900	531	3	13868	40	Kammmuschelerzeugnisse	\N
5901	531	3	13869	41	Kammmuschel in Tunke	\N
5902	531	3	13870	42	Kammmuschel in div. Aufgüssen	\N
5903	531	3	13871	43	Kammmuschel in Öl	\N
5904	531	3	13872	44	Kammmuschel in Gelee	\N
5905	531	3	13873	45	Kammmuschel geräuchert in Öl	\N
5906	531	3	13874	46	Kammmuschelerzeugnis paniert	\N
5907	531	3	13875	50	Venusmuschelerzeugnisse	\N
5908	531	3	13876	51	Venusmuschel in Tunke	\N
5909	531	3	13877	52	Venusmuschel in div. Aufgüssen	\N
5910	531	3	13878	53	Venusmuschel in Öl	\N
5911	531	3	13879	54	Venusmuschel in Gelee	\N
5912	531	3	13880	55	Venusmuschel geräuchert in Öl	\N
5913	531	3	13881	56	Venusmuschelerzeugnis paniert	\N
5914	531	3	13882	60	Klaffmuschelerzeugnisse	\N
5915	531	3	13883	61	Klaffmuschel in Tunke	\N
5916	531	3	13884	62	Klaffmuschel in div. Aufgüssen	\N
5917	531	3	13885	63	Klaffmuschel in Öl	\N
5918	531	3	13886	64	Klaffmuschel in Gelee	\N
5919	531	3	13887	65	Klaffmuschel geräuchert in Öl	\N
5920	531	3	13888	66	Klaffmuschelerzeugnis paniert	\N
5921	531	3	13889	70	Trogmuschelerzeugnisse	\N
5922	531	3	13890	71	Trogmuschel in Tunke	\N
5923	531	3	13891	72	Trogmuschel in div. Aufgüssen	\N
5924	531	3	13892	73	Trogmuschel in Öl	\N
5925	531	3	13893	74	Trogmuschel in Gelee	\N
5926	531	3	13894	75	Trogmuschel geräuchert in öl	\N
5927	531	3	13895	76	Trogmuschelerzeugnis paniert	\N
5928	532	3	3796	1	Sepia (Sepia sp.)	\N
5929	532	3	3797	2	Krake (Octopus sp.)	\N
5930	532	3	3798	3	Kalmar (Loligo sp.)	\N
5931	533	3	3800	1	Tintenfisch in Öl	\N
5932	533	3	3801	2	Tintenfisch in div. Tunken u. Aufgüssen	\N
5933	533	3	3802	3	Tintenfischringe paniert	\N
5934	533	3	3803	4	Tintenfisch gesalzen	\N
5935	533	3	3804	5	Tintenfisch getrocknet	\N
5936	533	3	3805	7	Tintenfischtinte	\N
5937	534	3	3807	1	Weinbergschnecke (Helix sp.)	\N
5938	534	3	3808	2	Achatschnecke (Achatina sp.)	\N
5939	534	3	3809	4	Strandschnecke (Littorina sp.)	\N
5940	534	3	3810	5	Seeohr (Haliotis sp.)	\N
5941	534	3	3811	6	Wellhornschnecke (Buccinum sp.)	\N
5942	535	3	3813	1	Schneckenkaviar	\N
5943	535	3	3814	2	Weinbergschnecke mit Kräuterbutter auch tiefgefroren	\N
5944	535	3	3815	3	Weinbergschneckenerzeugnis	\N
5945	536	3	3817	1	Froschschenkel	\N
5946	537	3	3819	1	Froschschenkel geräuchert	\N
5947	537	3	3820	2	Froschschenkel mariniert	\N
5948	537	3	3821	3	Froschschenkel gebacken	\N
5949	537	3	3822	4	Froschschenkel in Gelee	\N
5950	537	3	3823	5	Froschschenkel in div. Tunken	\N
5951	537	3	3824	6	Froschschenkel in Öl	\N
5952	538	3	3832	1	Krokodilfleisch auch tiefgefroren	\N
5953	538	3	3833	2	Krokodilfleisch gegart	\N
5954	539	3	3835	1	Ameise geröstet	\N
5955	539	3	3836	2	Seidenraupe geröstet	\N
5956	539	3	3837	3	Heuschrecke geröstet	\N
5957	539	3	3838	4	Jungbiene geröstet	\N
5958	540	3	3840	1	Krabbenpaste Präserve	\N
5959	540	3	3841	2	Krabbenklöße Präserve	\N
5960	540	3	3842	3	Crabmeat Präserve	\N
5961	540	3	3843	4	Krabben in Gelee Präserve	\N
5962	540	3	3844	5	Krabben in div. Tunken oder Mayonnaisen Präserve	\N
5963	540	3	3845	6	Hummer in Gelee Präserve	\N
5964	540	3	3846	7	Langusten in Gelee Präserve	\N
5965	540	3	3847	8	Krebstiere in div. Aufgüssen Präserve	\N
5966	540	3	3848	9	Krebsschwänze in div. Tunken oder Mayonnaisen Präserve	\N
5967	540	3	3849	10	Garnelenfleisch in div. Aufgüssen Präserve	\N
5968	540	3	3850	11	Garnelenfleisch in div. Tunken oder Mayonnaisen Präserve	\N
5969	540	3	3851	12	Hummer gekocht in div. Tunken oder Mayonnaisen Präserve	\N
5970	540	3	3852	13	Hummerschwänze/-fleisch Präserve	\N
5971	540	3	3853	14	Krebsfleisch in Gelee Präserve	\N
5972	540	3	3854	15	Krebsfleisch in div. Tunken oder Mayonnaisen Präserve	\N
5973	540	3	3855	16	Langustenschwänze/-fleisch Präserve	\N
5974	540	3	3856	18	Langusten in div. Tunken oder Mayonnaisen Präserve	\N
5975	540	3	3857	19	Nordseekrabbenfleisch Präserve	\N
5976	541	3	3859	1	Krabbenpaste Konserve	\N
5977	541	3	3860	2	Krabbenklöße Konserve	\N
5978	541	3	3861	3	Crabmeat Konserve	\N
5979	541	3	3862	4	Krabben in div. Tunken Konserve	\N
5980	541	3	3863	5	Krebstiere in div. Aufgüssen Konserve	\N
5981	541	3	3864	6	Krebsschwänze in div. Tunken Konserve	\N
5982	541	3	3865	7	Garnelenfleisch Konserve	\N
5983	541	3	3866	8	Garnelenfleisch in div. Tunken Konserve	\N
5984	541	3	3867	9	Hummer gekocht in div. Tunken Konserve	\N
5985	541	3	3868	10	Hummerschwänze/-fleisch Konserve	\N
5986	541	3	3869	11	Krebsfleisch in div. Tunken Konserve	\N
5987	541	3	3870	12	Langustenschwänze/-fleisch Konserve	\N
5988	541	3	3871	13	Langusten in div. Tunken Konserve	\N
5989	541	3	3872	14	Nordseekrabbenfleisch Konserve	\N
5990	542	3	3874	1	Muschel in Tunke Präserve	\N
5991	542	3	3875	2	Muschel in div. Aufgüssen Präserve	\N
5992	542	3	3876	3	Muschel in Öl Präserve	\N
5993	542	3	3877	4	Muschel in Gelee Präserve	\N
5994	542	3	3878	5	Muschel geräuchert in Öl Präserve	\N
5995	542	3	3879	6	Muschel naturell Präserve	\N
5996	542	3	13896	10	Miesmuschelerzeugnisse pasteurisiert Präserven	\N
5997	542	3	13897	11	Miesmuschel in Tunke Präserve	\N
5998	542	3	13898	12	Miesmuschel in div. Aufgüssen Präserve	\N
5999	542	3	13899	13	Miesmuschel in Öl Präserve	\N
6000	542	3	13900	14	Miesmuschel in Gelee Präserve	\N
6001	542	3	13901	15	Miesmuschel geräuchert in Öl Präserve	\N
6002	542	3	13902	16	Miesmuschel naturell Präserve	\N
6003	542	3	13903	20	Herzmuschelerzeugnisse pasteurisiert Präserven	\N
6004	542	3	13904	21	Herzmuschel in Tunke Präserve	\N
6005	542	3	13905	22	Herzmuschel in div. Aufgüssen Präserve	\N
6006	542	3	13906	23	Herzmuschel in Öl Präserve	\N
6007	542	3	13907	24	Herzmuschel in Gelee Präserve	\N
6008	542	3	13908	25	Herzmuschel geräuchert in Öl Präserve	\N
6009	542	3	13909	26	Herzmuschel naturell Präserve	\N
6010	542	3	13910	30	Jakobsmuschelerzeugnisse pasteurisiert Präserven	\N
6011	542	3	13911	31	Jakobsmuschel in Tunke Präserve	\N
6012	542	3	13912	32	Jakobsmuschel in div. Aufgüssen Präserve	\N
6013	542	3	13913	33	Jakobsmuschel in Öl Präserve	\N
6014	542	3	13914	34	Jakobsmuschel in Gelee Präserve	\N
6015	542	3	13915	35	Jakobsmuschel geräuchert in Öl Präserve	\N
6016	542	3	13916	36	Jakobsmuschel naturell Präserve	\N
6017	542	3	13917	40	Kammmuschelerzeugnisse pasteurisiert Präserven	\N
6018	542	3	13918	41	Kammmuschel in Tunke Präserve	\N
6019	542	3	13919	42	Kammmuschel in div. Aufgüssen Präserve	\N
6020	542	3	13920	43	Kammmuschel in Öl Präserve	\N
6021	542	3	13921	44	Kammmuschel in Gelee Präserve	\N
6022	542	3	13922	45	Kammmuschel geräuchert in Öl Präserve	\N
6023	542	3	13923	46	Kammmuschel naturell Präserve	\N
6024	542	3	13924	50	Venusmuschelerzeugnisse pasteurisiert Präserven	\N
6025	542	3	13925	51	Venusmuschel in Tunke Präserve	\N
6026	542	3	13926	52	Venusmuschel in div. Aufgüssen Präserve	\N
6027	542	3	13927	53	Venusmuschel in Öl Präserve	\N
6028	542	3	13928	54	Venusmuschel in Gelee Präserve	\N
6029	542	3	13929	55	Venusmuschel geräuchert in Öl Präserve	\N
6030	542	3	13930	56	Venusmuschel naturell Präserve	\N
6031	542	3	13931	60	Klaffmuschelerzeugnisse pasteurisiert Präserven	\N
6032	542	3	13932	61	Klaffmuschel in Tunke Präserve	\N
6033	542	3	13933	62	Klaffmuschel in div. Aufgüssen Präserve	\N
6034	542	3	13934	63	Klaffmuschel in Öl Präserve	\N
6035	542	3	13935	64	Klaffmuschel in Gelee Präserve	\N
6036	542	3	13936	65	Klaffmuschel geräuchert in Öl Präserve	\N
6037	542	3	13937	66	Klaffmuschel naturell Präserve	\N
6038	542	3	13938	70	Trogmuschelerzeugnisse pasteurisiert Präserven	\N
6039	542	3	13939	71	Trogmuschel in Tunke Präserve	\N
6040	542	3	13940	72	Trogmuschel in div. Aufgüssen Präserve	\N
6041	542	3	13941	73	Trogmuschel in Öl Präserve	\N
6042	542	3	13942	74	Trogmuschel in Gelee Präserve	\N
6043	542	3	13943	75	Trogmuschel geräuchert in Öl Präserve	\N
6044	542	3	13944	76	Trogmuschel naturell Präserve	\N
6045	543	3	3881	1	Muschel in Tunke Konserve	\N
6046	543	3	3882	2	Muschel in div. Aufgüssen Konserve	\N
6047	543	3	3883	3	Muschel in Öl Konserve	\N
6048	543	3	3884	4	Muschel geräuchert in Öl Konserve	\N
6049	543	3	3885	5	Muschel naturell Konserve	\N
6050	543	3	13945	10	Miesmuschelerzeugnisse Konserven	\N
6051	543	3	13946	11	Miesmuschel in Tunke Konserve	\N
6052	543	3	13947	12	Miesmuschel in div. Aufgüssen Konserve	\N
6053	543	3	13948	13	Miesmuschel in Öl Konserve	\N
6054	543	3	13949	14	Miesmuschel geräuchert in Öl Konserve	\N
6055	543	3	13950	15	Miesmuschel naturell Konserve	\N
6056	543	3	13951	20	Herzmuschelerzeugnisse Konserven	\N
6057	543	3	13952	21	Herzmuschel in Tunke Konserve	\N
6058	543	3	13953	22	Herzmuschel in div. Aufgüssen Konserve	\N
6059	543	3	13954	23	Herzmuschel in Öl Konserve	\N
6060	543	3	13955	24	Herzmuschel geräuchert in Öl Konserve	\N
6061	543	3	13956	25	Herzmuschel naturell Konserve	\N
6062	543	3	13957	30	Jakobsmuschelerzeugnisse Konserven	\N
6063	543	3	13958	31	Jakobsmuschel in Tunke Konserve	\N
6064	543	3	13959	32	Jakobsmuschel in div. Aufgüssen Konserve	\N
6065	543	3	13960	33	Jakobsmuschel in Öl Konserve	\N
6066	543	3	13961	34	Jakobsmuschel geräuchert in Öl Konserve	\N
6067	543	3	13962	35	Jakobsmuschel naturell Konserve	\N
6068	543	3	13963	40	Kammmuschelerzeugnisse Konserven	\N
6069	543	3	13964	41	Kammmuschel in Tunke Konserve	\N
6070	543	3	13965	42	Kammmuschel in div. Aufgüssen Konserve	\N
6071	543	3	13966	43	Kammmuschel in Öl Konserve	\N
6072	543	3	13967	44	Kammmuschel geräuchert in Öl Konserve	\N
6073	543	3	13968	45	Kammmuschel naturell Konserve	\N
6074	543	3	13969	50	Venusmuschelerzeugnisse Konserven	\N
6075	543	3	13970	51	Venusmuschel in Tunke Konserve	\N
6076	543	3	13971	52	Venusmuschel in div. Aufgüssen Konserve	\N
6077	543	3	13972	53	Venusmuschel in Öl Konserve	\N
6078	543	3	13973	54	Venusmuschel geräuchert in Öl Konserve	\N
6079	543	3	13974	55	Venusmuschel naturell Konserve	\N
6080	543	3	13975	60	Klaffmuschelerzeugnisse Konserven	\N
6081	543	3	13976	61	Klaffmuschel in Tunke Konserve	\N
6082	543	3	13977	62	Klaffmuschel in div. Aufgüssen Konserve	\N
6083	543	3	13978	63	Klaffmuschel in Öl Konserve	\N
6084	543	3	13979	64	Klaffmuschel geräuchert in Öl Konserve	\N
6085	543	3	13980	65	Klaffmuschel naturell Konserve	\N
6086	543	3	13981	70	Trogmuschelerzeugnisse Konserven	\N
6087	543	3	13982	71	Trogmuschel in Tunke Konserve	\N
6088	543	3	13983	72	Trogmuschel in div. Aufgüssen Konserve	\N
6089	543	3	13984	73	Trogmuschel in Öl Konserve	\N
6090	543	3	13985	74	Trogmuschel geräuchert in Öl Konserve	\N
6091	543	3	13986	75	Trogmuschel naturell Konserve	\N
6092	544	3	3887	1	Tintenfisch in Öl Präserve	\N
6093	544	3	3888	2	Tintenfisch in div. Tunken oder Aufgüssen Präserve	\N
6094	545	3	3890	1	Tintenfisch in Öl Konserve	\N
6095	545	3	3891	2	Tintenfisch in div. Tunken oder Aufgüssen Konserve	\N
6096	547	3	3894	1	Weinbergschnecke Konserve	\N
6097	547	3	3895	2	Achatschnecke Konserve	\N
6098	548	3	3897	1	Froschschenkel geräuchert Präserve	\N
6099	548	3	3898	2	Froschschenkel mariniert Präserve	\N
6100	548	3	3899	3	Froschschenkel gebacken Präserve	\N
6101	548	3	3900	4	Froschschenkel in Gelee Präserve	\N
6102	548	3	3901	5	Froschschenkel in div. Tunken oder Aufgüssen Präserve	\N
6103	548	3	3902	6	Froschschenkel in Öl Präserve	\N
6104	549	3	3904	1	Froschschenkel geräuchert Konserve	\N
6105	549	3	3905	2	Froschschenkel mariniert Konserve	\N
6106	549	3	3906	3	Froschschenkel gebacken Konserve	\N
6107	549	3	3907	4	Froschschenkel in div. Tunken oder Aufgüssen Konserve	\N
6108	549	3	3908	5	Froschschenkel in Öl Konserve	\N
6109	550	3	3910	1	Surimi	\N
6110	554	3	3915	1	Rindertalg	\N
6111	554	3	3916	2	Schweineschmalz mit Zusätzen	\N
6112	554	3	3917	3	Griebenschmalz mit Zusätzen	\N
6113	554	3	3918	4	Griebenschmalz	\N
6114	554	3	3919	5	Schweineschmalz	\N
6115	554	3	3920	6	Hammeltalg	\N
6116	554	3	3921	7	Gänseschmalz	\N
6117	554	3	3922	8	Gänseschmalz mit Zusätzen	\N
6118	554	3	3923	9	Premier jus	\N
6119	554	3	3924	10	Oleo	\N
6120	554	3	3925	11	Entenfett	\N
6121	554	3	3926	12	Pferdefett	\N
6122	554	3	3927	13	Putenschmalz	\N
6123	554	3	3928	14	Gänseschmalz-/Schweineschmalzmischung	\N
6124	555	3	3930	1	Heringsöl	\N
6125	555	3	3931	2	Dorschleberöl	\N
6126	555	3	3932	3	Waltran	\N
6127	555	3	3933	4	Seetieröl	\N
6128	555	3	3934	5	Fischöl	\N
6129	556	3	3936	1	Erdnussfett	\N
6130	556	3	3937	2	Kokosfett	\N
6131	556	3	3938	3	Palmfett	\N
6132	556	3	3939	4	Palmkernfett	\N
6133	556	3	3940	5	Muskatfett	\N
6134	556	3	3941	6	Kakaobutter	\N
6135	556	3	3942	7	Bassiafett	\N
6136	557	3	3944	1	Olivenöl	\N
6137	557	3	3945	2	Olivenkernöl	\N
6138	557	3	3946	3	Distelöl	\N
6139	557	3	3947	4	Mandelöl	\N
6140	557	3	3948	5	Erdnussöl	\N
6141	557	3	3949	6	Haselnussöl	\N
6142	557	3	3950	7	Senföl	\N
6143	557	3	3951	8	Kapoköl	\N
6144	557	3	3952	9	Rapssaatöl Rapsöl	\N
6145	557	3	3953	10	Sesamöl	\N
6146	557	3	3954	11	Traubenkernöl	\N
6147	557	3	3955	12	Bucheckernöl	\N
6148	557	3	3956	13	Maiskeimöl	\N
6149	557	3	3957	14	Sonnenblumenöl	\N
6150	557	3	3958	15	Sojaöl	\N
6151	557	3	3959	16	Baumwollsaatöl	\N
6152	557	3	3960	17	Weizenkeimöl	\N
6153	557	3	3961	18	Mohnöl	\N
6154	557	3	3962	19	Hanföl	\N
6155	557	3	3963	20	Walnussöl	\N
6156	557	3	3964	21	Leinöl	\N
6157	557	3	3967	24	Kürbiskernöl	\N
6158	557	3	3968	25	Oliventresteröl	\N
6159	557	3	3969	26	Tafelöl	\N
6160	557	3	3970	27	Olivenöl natives	\N
6161	557	3	3971	28	Olivenöl raffiniert	\N
6162	557	3	3972	29	Olivenöl natives extra	\N
6163	557	3	3973	30	Lampantöl	\N
6164	557	3	3974	32	Oliventresteröl rohes	\N
6165	557	3	3975	33	Oliventresteröl raffiniertes	\N
6166	557	3	3976	34	Reiskeimöl	\N
6167	557	3	3977	35	Distelöl kaltgepresst	\N
6168	557	3	3978	36	Schwarzkümmelöl	\N
6169	557	3	3979	37	Erdnussöl kaltgepresst	\N
6170	557	3	3980	38	Haselnussöl kaltgepresst	\N
6171	557	3	3981	39	Rapssaatöl kaltgepresst Rapsöl kaltgepresst	\N
6172	557	3	3982	40	Sesamöl kaltgepresst	\N
6173	557	3	3983	41	Maiskeimöl kaltgepresst	\N
6174	557	3	3984	42	Sonnenblumenöl kaltgepresst	\N
6175	557	3	3985	43	Sojaöl kaltgepresst	\N
6176	557	3	3986	44	Weizenkeimöl kaltgepresst	\N
6177	557	3	3987	45	Walnussöl kaltgepresst	\N
6178	557	3	3988	46	Hanföl kaltgepresst	\N
6179	557	3	3989	47	Leinöl kaltgepresst	\N
6180	557	3	3990	48	Kürbiskernöl kaltgepresst	\N
6181	557	3	3991	49	Mandelöl kaltgepresst	\N
6182	557	3	13706	50	Arganöl	\N
6183	557	3	14170	51	Macadamiaöl	\N
6184	557	3	14171	52	Avocadoöl	\N
6185	557	3	14284	53	Palmöl, roh	\N
6186	557	3	14285	54	Palmöl, raffiniert	\N
6187	558	3	3993	1	Margarine	\N
6188	558	3	3994	2	Margarine vitaminisiert	\N
6189	558	3	3995	3	Margarine mit besonderen Zusätzen	\N
6190	558	3	3996	4	Pflanzenmargarine	\N
6191	558	3	3997	5	Ziehmargarine	\N
6192	558	3	3998	6	Backmargarine	\N
6193	558	3	3999	7	Kremmargarine	\N
6194	558	3	4000	8	Kochmargarine	\N
6195	558	3	4001	9	Margarineschmalz	\N
6196	558	3	4002	10	Pflanzenmargarine vitaminisiert	\N
6197	558	3	4003	11	Margarine linolsäurereich	\N
6198	558	3	4004	12	Margarine mit besonders hohem Anteil an mehrf. unges. Fettsäuren	\N
6199	558	3	4005	13	Dreiviertelfettmargarine	\N
6200	558	3	4006	14	Dreiviertelfettmargarine vitaminisiert	\N
6201	559	3	4008	1	Halbfettmargarine	\N
6202	559	3	4009	2	Halbfettmargarine vitaminisiert	\N
6203	559	3	4010	3	Halbfettmargarine mit besonderen Zusätzen	\N
6204	559	3	4011	4	Halbfettmargarine linolsäurereich	\N
6205	559	3	4012	5	Halbfettmargarine mit bes. hohem Anteil an mehrf. unges. Fettsäuren	\N
6206	559	3	4013	6	Halbfettmargarine natriumarm	\N
6207	560	3	4015	1	Fettkrem	\N
6208	560	3	4016	2	Garnierkrem	\N
6209	560	3	4017	3	Fettglasur	\N
6210	560	3	4018	4	Fettglasur mit anderen LM	\N
6211	560	3	4019	5	Fettglasur mit Kakao	\N
6212	560	3	4020	6	Butterkrem	\N
6213	560	3	4021	7	Gehärtetes Speisefett	\N
6214	560	3	4022	8	Wurstfett	\N
6215	560	3	4024	10	Backfettmischung	\N
6216	560	3	4025	11	Backfettmischung mit anderen Zubereitungen	\N
6217	560	3	4026	12	Fritierfett gebraucht	\N
6218	560	3	4027	13	Fritierfett ungebraucht	\N
6219	560	3	4029	15	Fritieröl gebraucht	\N
6220	560	3	4030	16	Fritieröl ungebraucht	\N
6221	560	3	4031	17	Bratfettmischung	\N
6222	560	3	4037	23	Streichfett  X v. H	\N
6223	560	3	4038	24	Mischstreichfett X v. H	\N
6224	560	3	13295	25	Pflanzenfett-Zubereitung	\N
6225	560	3	13296	26	Würzfett	\N
6226	560	3	14139	27	Sprühfett	\N
6227	560	3	4039	99	Fettmischungen	\N
6228	561	3	4041	1	Halbmischfett	\N
6229	561	3	4042	2	Dreiviertelmischfett	\N
6230	561	3	4043	3	Mischfett	\N
6231	561	3	4044	4	Mischfettschmalz	\N
6232	564	3	4048	1	Brühwürfel	\N
6233	564	3	4049	2	Hühnerbrüherzeugnis	\N
6234	564	3	4050	3	Fleischbrüherzeugnis	\N
6235	564	3	4051	4	Gemüsebrüherzeugnis	\N
6236	564	3	4052	5	Hefebrüherzeugnis	\N
6237	565	3	4054	1	Fleischtrockensuppe klare	\N
6238	565	3	4055	2	Hühnertrockensuppe klare	\N
6239	565	3	4056	3	Ochsenschwanztrockensuppe klare	\N
6240	565	3	4057	4	Frühlingstrockensuppe	\N
6241	565	3	4058	5	Champignontrockensuppe	\N
6242	565	3	4059	6	Pilztrockensuppe	\N
6243	565	3	4060	7	Steinpilztrockensuppe	\N
6244	565	3	4061	8	Tomatentrockensuppe	\N
6245	565	3	4062	9	Kräutertrockensuppe	\N
6246	565	3	4063	10	Fischtrockensuppe	\N
6247	565	3	4064	11	Jägertrockensuppe	\N
6248	565	3	4065	12	Kartoffeltrockensuppe	\N
6249	565	3	4066	13	Spargeltrockensuppe	\N
6250	565	3	4067	14	Blumenkohltrockensuppe	\N
6251	565	3	4068	15	Lauchtrockensuppe	\N
6252	565	3	4069	16	Leberknödeltrockensuppe	\N
6253	565	3	4070	17	Kalbfleischtrockensuppe	\N
6254	565	3	4071	18	Ochsenschwanztrockensuppe gebundene	\N
6255	565	3	4072	19	Gulaschtrockensuppe	\N
6256	565	3	4073	20	Fasanentrockensuppe	\N
6257	565	3	4074	21	Curryhuhntrockensuppe	\N
6258	565	3	4075	22	Mockturtletrockensuppe	\N
6259	565	3	4076	23	Borschtschtrockensuppe	\N
6260	565	3	4077	24	Muscheltrockensuppe	\N
6261	565	3	4078	25	Grießtrockensuppe	\N
6262	565	3	4079	26	Zwiebeltrockensuppe	\N
6263	565	3	4080	27	Linsentrockensuppe	\N
6264	565	3	4081	28	Erbsentrockensuppe	\N
6265	565	3	4082	29	Bohnentrockensuppe	\N
6266	565	3	4083	30	Tapiokatrockensuppe	\N
6267	565	3	4084	31	Zigeunertrockensuppe	\N
6268	565	3	4085	32	Rindfleischtrockensuppe	\N
6269	565	3	4086	33	Nudeltrockensuppe	\N
6270	565	3	4087	34	Gemüsetrockensuppe	\N
6271	565	3	4088	35	Seetangtrockensuppe	\N
6272	565	3	4089	36	Broccolitrockensuppe	\N
6273	565	3	4090	37	Reistrockensuppe	\N
6274	565	3	4091	38	Fleischklößchentrockensuppe	\N
6275	565	3	4092	39	China-/asiatische Trockensuppe	\N
6276	565	3	4093	40	Pansentrockensuppe	\N
6277	565	3	4094	41	Linsenmehltrockensuppe	\N
6278	565	3	4095	42	Weizenmehltrockensuppe	\N
6279	565	3	4096	43	Reisnudeltrockensuppe	\N
6280	565	3	4097	44	Knoblauchkremtrockensuppe	\N
6281	565	3	4098	45	Markklößchentrockensuppe	\N
6282	565	3	4099	46	Hühnertrockensuppe gebundene	\N
6283	565	3	13987	47	Miesmuscheltrockensuppe	\N
6284	567	3	4102	1	Bambussprossensuppe flüssig	\N
6285	567	3	4104	3	Gärtnersuppe flüssig	\N
6286	567	3	4105	4	Currysuppe flüssig	\N
6287	567	3	4106	5	Markklößchensuppe flüssig	\N
6288	567	3	4107	6	Pilzsuppe flüssig	\N
6289	567	3	4108	7	Champignonsuppe flüssig	\N
6290	567	3	4109	8	Steinpilzsuppe flüssig	\N
6291	567	3	4110	9	Tomatensuppe flüssig	\N
6292	567	3	4111	10	Krabbensuppe flüssig	\N
6293	567	3	4112	11	Fischsuppe flüssig	\N
6294	567	3	4113	12	Tintenfischsuppe flüssig	\N
6295	567	3	4114	13	Jägersuppe flüssig	\N
6296	567	3	4115	14	Kartoffelsuppe flüssig	\N
6297	567	3	4116	15	Spargelsuppe flüssig	\N
6298	567	3	4117	17	Blumenkohlsuppe flüssig	\N
6299	567	3	4118	18	Leberknödelsuppe flüssig	\N
6300	567	3	4119	19	Ochsenschwanzsuppe klare flüssig	\N
6301	567	3	4120	20	Gulaschsuppe flüssig	\N
6302	567	3	4121	21	Fasanensuppe flüssig	\N
6303	567	3	4122	22	Curryhuhnsuppe flüssig	\N
6304	567	3	4123	23	Muschelsuppe flüssig	\N
6305	567	3	4124	24	Weinbergschneckensuppe flüssig	\N
6306	567	3	4125	25	Erbsensuppe flüssig	\N
6307	567	3	4126	26	Linsensuppe flüssig	\N
6308	567	3	4127	27	Frühlingssuppe flüssig	\N
6309	567	3	4128	28	Hummersuppe flüssig	\N
6310	567	3	4129	29	Geflügelsuppe flüssig	\N
6311	567	3	4130	30	Zwiebelsuppe flüssig	\N
6312	567	3	4131	31	Kalbfleischsuppe flüssig	\N
6313	567	3	4132	32	Austernsuppe flüssig	\N
6314	567	3	4133	33	Froschschenkelsuppe flüssig	\N
6315	567	3	4134	34	Zigeunersuppe flüssig	\N
6316	567	3	4135	35	Bohnensuppe flüssig	\N
6317	567	3	4136	36	Haifischflossensuppe flüssig	\N
6318	567	3	4137	37	Mockturtlesuppe flüssig	\N
6319	567	3	4138	38	Ochsenschwanzsuppe gebundene flüssig	\N
6320	567	3	4139	39	Wildrahmsuppe flüssig	\N
6321	567	3	4140	40	Pfeffersuppe flüssig	\N
6322	567	3	4141	41	Bihunsuppe flüssig	\N
6323	567	3	4142	42	Rindfleischsuppe flüssig	\N
6324	567	3	4143	43	Graupensuppe flüssig	\N
6325	567	3	4144	44	Gemüsesuppe flüssig	\N
6326	567	3	4145	45	Serbische Bohnensuppe flüssig	\N
6327	567	3	4146	46	Broccolikremsuppe flüssig	\N
6328	567	3	4147	47	Lauchkremsuppe flüssig	\N
6329	567	3	4148	48	Sauer-Scharfsuppe flüssig	\N
6330	567	3	4149	49	Krebssuppe flüssig	\N
6331	567	3	4150	51	Grießsuppe flüssig	\N
6332	567	3	4151	52	Nudelsuppe flüssig	\N
6333	567	3	4152	53	China-/asiatische Suppe flüssig	\N
6334	567	3	4153	54	Soljanka flüssig	\N
6335	567	3	13988	55	Miesmuschelsuppe flüssig	\N
6336	568	3	4155	1	Fruchtkaltschale Trockensuppe	\N
6337	568	3	4156	2	Fruchtkaltschale Trockensuppe mit Zusätzen	\N
6338	568	3	4157	3	Kaltschale Trockensuppe	\N
6339	568	3	4158	4	Kaltschale Trockensuppe mit Zusätzen	\N
6340	570	3	4161	1	Fruchtkaltschale flüssig	\N
6341	570	3	4162	2	Fruchtkaltschale mit Zusätzen flüssig	\N
6342	570	3	4163	3	Kaltschale flüssig	\N
6343	570	3	4164	4	Kaltschale mit Zusätzen flüssig	\N
6344	571	3	4166	1	Soßenpulver weiß	\N
6345	571	3	4167	2	Soßenpulver weiß mit Zusätzen	\N
6346	571	3	4168	3	Bratensoßenpulver mit Zusätzen	\N
6347	571	3	4169	4	Bratensoßenpulver	\N
6348	571	3	4170	5	Tomatensoßenpulver	\N
6349	571	3	4171	6	Rahmsoßenpulver	\N
6350	571	3	4172	7	Jägersoßenpulver	\N
6351	571	3	4173	8	Gulaschsoßenpulver	\N
6352	571	3	4174	9	Currysoßenpulver	\N
6353	571	3	4175	10	Spaghettisoßenpulver	\N
6354	571	3	4176	11	Paprikasoßenpulver	\N
6355	571	3	4177	12	Soßenpulver zu Braten	\N
6356	571	3	4178	13	Soßenpulver zu Gulasch	\N
6357	571	3	4179	14	Pfeffersoßenpulver	\N
6358	571	3	4180	15	Soßenbinder weiß	\N
6359	571	3	4181	16	Soßenbinder braun	\N
6360	571	3	4182	17	Sauce Hollandaise-Pulver	\N
6361	571	3	4183	18	Sauce Bernaise-Pulver	\N
6362	571	3	4184	19	Kräutersoßenpulver	\N
6363	571	3	4185	20	Geflügelsoßenpulver	\N
6364	571	3	4186	21	Pilzsoßenpulver	\N
6365	571	3	4187	22	Chinesisches/asiatisches Soßenpulver	\N
6366	573	3	4190	1	Soße weiß flüssig	\N
6367	573	3	4191	2	Soße weiß mit Zusätzen flüssig	\N
6368	573	3	4192	3	Bratensoße flüssig	\N
6369	573	3	4193	4	Bratensoße mit Zusätzen flüssig	\N
6370	573	3	4194	5	Tomatensoße flüssig	\N
6371	573	3	4195	6	Soße zu Braten flüssig	\N
6372	573	3	4196	7	Soße zu Gulasch flüssig	\N
6373	573	3	4197	8	Spaghettisoße flüssig	\N
6374	573	3	4198	9	Sauce Hollandaise flüssig	\N
6375	573	3	4199	10	Sauce Bernaise flüssig	\N
6376	573	3	4200	11	Soße würzig pikant konzentriert	\N
6377	574	3	4223	1	Weizenkörner	\N
6378	574	3	4224	2	Grünkern	\N
6379	574	3	4225	3	Dinkelkörner	\N
6380	574	3	4226	4	Hartweizenkörner	\N
6381	574	3	4227	5	Emmer	\N
6382	574	3	4228	6	Einkorn	\N
6383	575	3	4230	1	Roggenkörner	\N
6384	576	3	4232	1	Gerstenkörner	\N
6385	577	3	4234	1	Haferkörner	\N
6386	578	3	4236	1	Maiskörner	\N
6387	579	3	4238	1	Rohreis	\N
6388	579	3	4240	3	Langkornreis	\N
6389	579	3	4241	4	Rundkornreis	\N
6390	579	3	4242	5	Kargoreis	\N
6391	579	3	4243	6	Bruchreis	\N
6392	579	3	4244	7	Wildreis	\N
6393	579	3	4245	8	Reis ungeschliffen	\N
6394	579	3	4246	9	Parboiled Reis	\N
6395	579	3	14243	10	Basmatireis	\N
6396	580	3	4248	1	Buchweizenkörner	\N
6397	581	3	4250	1	Hirsekörner	\N
6398	581	3	13297	2	Sorghumkörner	\N
6399	581	3	14177	3	Amaranthuskörner	\N
6400	581	3	14178	4	Quinoakörner	\N
6401	582	3	4252	1	Roggengemenge	\N
6402	582	3	4253	2	Weizengemenge	\N
6403	582	3	4254	3	Getreidemischung mit anderen Samen und Zusätzen	\N
6404	582	3	4255	4	Getreidemischung mit Kleie	\N
6405	582	3	4256	5	Getreidemehrkornmischung	\N
6406	583	3	4258	1	Triticalekörner	\N
6407	584	3	4261	1	Roggenmehl	\N
6408	584	3	4262	2	Roggenmehl Type 815	\N
6409	584	3	4263	3	Roggenmehl Type 997	\N
6410	584	3	4264	4	Roggenmehl Type 1150	\N
6411	584	3	4265	5	Roggenmehl Type 1370	\N
6412	584	3	4267	7	Roggenmehl Type 1740	\N
6413	584	3	4268	8	Roggenvollkornmehl	\N
6414	584	3	4271	11	Weizenmehl	\N
6415	584	3	4272	12	Weizenmehl Type 405	\N
6416	584	3	4273	13	Weizenmehl Type 550	\N
6417	584	3	4275	15	Weizenmehl Type 812	\N
6418	584	3	4276	16	Weizenmehl Type 1050	\N
6419	584	3	4278	18	Weizenmehl Type 1600	\N
6420	584	3	4280	20	Weizenvollkornmehl	\N
6421	584	3	4283	23	Durum-Weizenmehl Type 1600	\N
6422	584	3	4284	24	Gerstenmehl	\N
6423	584	3	4285	25	Hafermehl	\N
6424	584	3	4286	26	Maismehl	\N
6425	584	3	4287	27	Reismehl	\N
6426	584	3	4288	28	Buchweizenmehl	\N
6427	584	3	4289	29	Hirsemehl	\N
6428	584	3	4290	30	Grünkernmehl	\N
6429	584	3	4291	31	Dinkelmehl Type 630	\N
6430	584	3	4292	32	Dinkelmehl Type 812	\N
6431	584	3	4293	33	Dinkelmehl Type 1050	\N
6432	584	3	4294	34	Dinkelvollkornmehl	\N
6433	584	3	4295	35	Quinoamehl	\N
6434	584	3	4296	36	Amaranthusmehl	\N
6435	584	3	14172	37	Dinkelmehl	\N
6436	584	3	14179	38	Teffmehl	\N
6437	584	3	14180	39	Kamutmehl	\N
6438	585	3	4298	1	Weizengrieß	\N
6439	585	3	4299	2	Hartweizengrieß Durum	\N
6440	585	3	4300	3	Weizendunst	\N
6441	585	3	4301	4	Maisgrieß	\N
6442	585	3	4302	5	Reisgrieß	\N
6443	585	3	4303	6	Buchweizengrieß	\N
6444	585	3	4304	7	Hirsegrieß	\N
6445	585	3	14148	8	Dinkelgrieß	\N
6446	585	3	4305	99	Getreidegrießmischung	\N
6447	586	3	4307	1	Roggenbackschrot Type 1800	\N
6448	586	3	4308	2	Roggenvollkornschrot	\N
6449	586	3	4309	3	Weizenbackschrot Type 1700	\N
6450	586	3	4310	4	Weizenvollkornschrot	\N
6451	586	3	4311	5	Gerstenschrot	\N
6452	586	3	4312	6	Haferschrot	\N
6453	586	3	4313	7	Maisschrot	\N
6454	586	3	4314	8	Reisschrot	\N
6455	586	3	4315	9	Buchweizenschrot	\N
6456	586	3	4316	10	Hirseschrot	\N
6457	586	3	4317	11	Grünkernschrot	\N
6458	586	3	4318	12	Dinkelschrot	\N
6459	586	3	4319	13	Dinkelvollkornschrot	\N
6460	586	3	4320	99	Getreideschrotmischung	\N
6461	587	3	4322	1	Reisstärke	\N
6462	587	3	4323	2	Weizenstärke	\N
6463	587	3	4324	3	Maisstärke	\N
6464	587	3	4325	4	Roggenstärke	\N
6465	587	3	4326	5	Dinkelstärke	\N
6466	587	3	4327	6	Gerstestärke	\N
6467	587	3	4328	7	Haferstärke	\N
6468	587	3	4329	99	Getreidestärkemischung	\N
6469	588	3	4331	1	Reiseiweiß	\N
6470	588	3	4332	2	Weizeneiweiß	\N
6471	588	3	4333	3	Maiseiweiß	\N
6472	588	3	4334	4	Dinkeleiweiß	\N
6473	588	3	4335	5	Hafereiweiß	\N
6474	588	3	4336	6	Roggeneiweiß	\N
6475	588	3	4337	99	Getreideeiweißmischung	\N
6476	589	3	4339	1	Maisgrits	\N
6477	589	3	4340	2	Reisgrits	\N
6478	589	3	4341	3	Hirsegrits	\N
6479	589	3	4342	4	Buchweizengrits	\N
6480	589	3	4343	5	Cornflakes	\N
6481	589	3	4344	6	Haferflakes	\N
6482	589	3	4345	7	Bulgur	\N
6483	589	3	14096	8	Weizenflakes	\N
6484	589	3	14097	9	Roggenflakes	\N
6485	589	3	14098	10	Reisflakes	\N
6486	589	3	14099	11	Getreideflakes	\N
6487	589	3	14100	12	Dinkelflakes	\N
6488	590	3	4352	1	Speisekleie aus Weizen	\N
6489	590	3	4353	2	Speisekleie aus Roggen	\N
6490	590	3	4354	3	Speisekleie aus Dinkel	\N
6491	590	3	4355	5	Speisekleie aus Hafer	\N
6492	590	3	4356	99	Speisekleiemischung	\N
6493	591	3	4358	1	Weizenflocken	\N
6494	591	3	4359	2	Roggenflocken	\N
6495	591	3	4360	3	Gerstenflocken	\N
6496	591	3	4361	4	Hirseflocken	\N
6497	591	3	4362	5	Reisflocken	\N
6498	591	3	4363	6	gestrichen jetzt 160907, Haferflocken	\N
6499	591	3	4364	7	Hafervollkornflocken/Haferflocken	\N
6500	591	3	4365	8	Gerstengrütze	\N
6501	591	3	4366	9	Gerstengraupen	\N
6502	591	3	4367	10	Weizenkeimflocken	\N
6503	591	3	4368	11	Hafergrütze	\N
6504	591	3	4369	12	Buchweizengrütze	\N
6505	591	3	4370	13	Weizengrütze	\N
6506	591	3	4371	14	Roggengrütze	\N
6507	591	3	4372	15	Hafer-Instantflocken	\N
6508	591	3	4373	16	Dinkelflocken	\N
6509	591	3	4374	17	Dinkelgrütze	\N
6510	591	3	4375	98	Getreideflockenmischung	\N
6511	591	3	4376	99	Getreidegrützemischung	\N
6512	592	3	4378	1	Puffmais	\N
6513	592	3	4379	2	Puffreis	\N
6514	592	3	4380	3	Puffweizen	\N
6515	592	3	14244	4	Reiswaffel	\N
6516	593	3	4382	1	Grits mit Trockenobst	\N
6517	593	3	4383	2	Haferflocken mit Trockenobst	\N
6518	593	3	4384	3	Puffmais mit Zucker	\N
6519	593	3	4385	4	Puffmais mit Salz	\N
6520	593	3	4386	5	Puffreis mit Zucker	\N
6521	593	3	4387	6	Puffreis mit Salz	\N
6522	593	3	4388	7	Grits mit Zucker	\N
6523	593	3	4389	8	Müsli	\N
6524	593	3	4390	9	Weizenflocken mit Zucker geröstet	\N
6525	593	3	4391	10	Flocken von Mehrkornmischung mit Zucker geröstet	\N
6526	593	3	4392	11	Speisekleie mit Zusätzen	\N
6527	593	3	4393	12	Speisekleietabletten	\N
6528	593	3	4394	13	Müsliriegel/-happen	\N
6529	593	3	4395	14	Früchtemüsli	\N
6530	593	3	4396	15	Müsli mit hervorhebendem Zusatz	\N
6531	593	3	4397	16	Getreideriegel	\N
6532	593	3	4398	17	Getreideriegel mit Früchtezusatz	\N
6533	593	3	4399	18	Cornflakes mit Überzug	\N
6534	593	3	4400	19	Cornflakes mit Ölsamen	\N
6535	593	3	4401	20	Schoko-Müsli	\N
6536	593	3	14181	21	Mais geröstet un-/gesalzen, un-/gewürzt	\N
6537	593	3	14245	22	Reiswaffel mit Zucker	\N
6538	593	3	14246	23	Reiswaffel mit Salz	\N
6539	593	3	14288	24	Puffmais mit Gewürzen	\N
6540	594	3	4403	1	Fertigmehl für Weizenbrot	\N
6541	594	3	4404	2	Fertigmehl für Roggenbrot	\N
6542	594	3	4405	3	Fertigmehl für Weizenmischbrot	\N
6543	594	3	4406	4	Fertigmehl für Roggenmischbrot	\N
6544	594	3	4407	5	Fertigmehl für Weizenschrotbrot	\N
6545	594	3	4408	6	Fertigmehl für Roggenschrotbrot	\N
6546	594	3	4409	7	Fertigmehl aus Mischung v. Mehl u. Backschrot u./o. Vollkornschrot	\N
6547	594	3	4410	8	Fertigmehl für Kleiebrot	\N
6548	594	3	4411	9	Fertigmehl für Sojabrot	\N
6549	594	3	4412	10	Fertigmehl für Sonnenblumenbrot	\N
6550	594	3	4413	11	Fertigmehl für Buttermilchbrot	\N
6551	594	3	4414	12	Fertigmehl für Mehrkornbrot	\N
6552	594	3	4415	13	Fertigmehl für Leinsamenbrot	\N
6553	594	3	14247	14	Fertigmehl für Vollkornbrot	\N
6554	595	3	4417	1	Grundmischung für Feinteige mit Hefe	\N
6555	595	3	4418	2	Grundmischung für Feinteige ohne Hefe	\N
6556	595	3	4419	3	Grundmischung für Masse mit Aufschlag	\N
6557	595	3	4420	4	Grundmischung für Masse ohne Aufschlag	\N
6558	596	3	4422	1	Weizenbrotteig	\N
6559	596	3	4423	2	Weizenschrotbrotteig	\N
6560	596	3	4424	3	Roggenbrotteig	\N
6561	596	3	4425	4	Roggenschrotbrotteig	\N
6562	596	3	4426	5	Weizenmischbrotteig	\N
6563	596	3	4427	6	Roggenmischbrotteig	\N
6564	596	3	4428	7	Teig aus Mischung von Mehl u. Backschrot u./o. Vollkornschrot	\N
6565	596	3	4429	8	Teigling	\N
6566	596	3	13298	9	Teigling für Laugengebäck	\N
6567	596	3	13299	10	Teigblatt	\N
6568	597	3	4431	1	Feinteig mit Hefe	\N
6569	597	3	4432	2	Feinteig ohne Hefe	\N
6570	597	3	4433	3	Feinteig mit Hefe mit Füllung	\N
6571	597	3	4434	4	Feinteig ohne Hefe mit Füllung	\N
6572	597	3	4435	5	Blätterteig	\N
6573	597	3	4436	6	Mürbeteig	\N
6574	597	3	4437	7	Lebkuchenteig	\N
6575	598	3	4439	1	Baumkuchenmasse	\N
6576	598	3	4440	2	Biskuitmasse	\N
6577	598	3	4441	3	Wiener Masse	\N
6578	598	3	4442	4	Rührmasse	\N
6579	598	3	4443	5	Sandmasse	\N
6580	598	3	4444	6	Eiweiß- und Schaummasse	\N
6581	598	3	4445	7	Ölsamenmasse	\N
6582	598	3	4446	8	Waffelmasse	\N
6583	598	3	4447	9	Brandmasse	\N
6584	599	3	4450	1	Gerstenmalz Backmalz	\N
6585	599	3	4451	2	Roggenmalz Backmalz	\N
6586	599	3	4452	3	Roggenmalzextrakt enzymfrei	\N
6587	599	3	4453	4	Backmalz enzymhaltig	\N
6588	600	3	4455	1	Backmischung für Feinteig mit Hefe	\N
6589	600	3	4456	2	Backmischung für Feinteig ohne Hefe	\N
6590	600	3	4457	3	Backmischung für Masse mit Aufschlag	\N
6591	600	3	4458	4	Backmischung für Masse ohne Aufschlag	\N
6592	601	3	4461	1	Weizenbrot	\N
6593	601	3	4462	2	Weizenbrot mit Fett u./o. Zucker	\N
6594	601	3	4463	3	Weizenbrot mit Schrotanteilen	\N
6595	601	3	4464	4	Weizenbrot mit Schrotanteilen u. Fett u./o. Zucker	\N
6596	601	3	4465	5	Weizenschrotbrot	\N
6597	601	3	4466	6	Weizenvollkornbrot	\N
6598	602	3	4468	1	Roggenfeinbrot	\N
6599	602	3	4469	2	Roggenbrot mit Schrotanteilen	\N
6600	602	3	4470	3	Roggenschrotbrot	\N
6601	602	3	4471	4	Roggenvollkornbrot	\N
6602	603	3	4473	1	Weizenmischbrot	\N
6603	603	3	4475	3	Weizenschrotmischbrot	\N
6604	603	3	4476	4	Weizen- Roggenvollkornbrot	\N
6605	603	3	4477	5	Roggenmischbrot	\N
6606	603	3	4479	7	Roggenschrotmischbrot	\N
6607	603	3	4480	8	Roggen- Weizenvollkornbrot	\N
6608	603	3	4481	9	Roggen-Weizenschrotbrot	\N
6609	603	3	4482	10	Weizen-Roggenschrotbrot	\N
6610	604	3	4484	1	Weizentoastbrot auch mit Butter	\N
6611	604	3	4485	2	Weizentoastbrot mit Schrotanteil	\N
6612	604	3	4486	3	Weizenschrottoastbrot	\N
6613	604	3	4487	4	Roggentoastbrot	\N
6614	604	3	4488	5	Roggentoastbrot mit Schrotanteil	\N
6615	604	3	4489	6	Weizenmischtoastbrot	\N
6616	604	3	4490	7	Weizenmischtoastbrot mit Schrotanteil	\N
6617	604	3	4491	8	Weizenschrotmischtoastbrot	\N
6618	604	3	4492	9	Roggenmischtoastbrot	\N
6619	604	3	4493	10	Weizentoastbrot mit Kleiezusatz	\N
6620	604	3	4494	11	Mehrkorntoastbrot	\N
6621	604	3	4495	12	Weizen-Roggenschrottoastbrot	\N
6622	604	3	4496	13	Weizenvollkorntoastbrot	\N
6623	604	3	4497	14	Roggenvollkorntoastbrot	\N
6624	604	3	4498	15	Roggenschrottoastbrot	\N
6625	604	3	4499	16	Weizen-Roggenvollkorntoastbrot	\N
6626	604	3	4500	17	Roggen-Weizenvollkorntoastbrot	\N
6627	604	3	4501	18	Roggenmischtoastbrot mit Schrotanteil	\N
6628	604	3	4502	19	Roggenschrotmischtoastbrot	\N
6629	604	3	14289	20	Dinkeltoastbrot	\N
6630	605	3	4504	1	Schlüterbrot	\N
6631	605	3	4505	2	Steinmetzbrot	\N
6632	605	3	4506	3	Simonsbrot	\N
6633	605	3	4514	11	Grahambrot	\N
6634	606	3	4516	1	Buttermilchbrot	\N
6635	606	3	4517	2	Weizenkeimbrot	\N
6636	606	3	4518	3	Leinsamenbrot	\N
6637	606	3	4519	4	Ölsamenbrot	\N
6638	606	3	4520	5	Milchbrot	\N
6639	606	3	4521	6	Rosinenbrot	\N
6640	606	3	4522	7	Gewürzbrot	\N
6641	606	3	4523	8	Kleiebrot	\N
6642	606	3	4524	9	Zwiebelbrot	\N
6643	606	3	4525	10	Joghurtbrot	\N
6644	606	3	4526	11	Kefirbrot	\N
6645	606	3	4527	12	Molkebrot	\N
6646	606	3	4528	13	Sauermilchbrot	\N
6647	606	3	4529	14	Quarkbrot	\N
6648	606	3	4530	15	Speckbrot	\N
6649	606	3	4531	16	Schinkenbrot	\N
6650	606	3	4532	17	Malzbrot	\N
6651	606	3	4533	18	Spezialbrot mit Fettzusätzen	\N
6652	606	3	4534	19	Sojabrot	\N
6653	606	3	4535	20	Mehrkornbrot	\N
6654	606	3	4536	21	Gerstenbrot	\N
6655	606	3	4537	22	Haferbrot	\N
6656	606	3	4538	23	Maisbrot	\N
6657	606	3	4539	24	Reisbrot	\N
6658	606	3	4540	25	Hirsebrot	\N
6659	606	3	4541	26	Sesambrot	\N
6660	606	3	4542	27	Sonnenblumenkernbrot	\N
6661	606	3	4543	28	Müslibrot	\N
6662	606	3	4544	29	Nussbrot	\N
6663	606	3	4545	30	Kräuterbrot	\N
6664	606	3	4546	32	Dinkelbrot	\N
6665	606	3	4547	33	Pflaumenbrot	\N
6666	606	3	4548	34	Butterbrot	\N
6667	606	3	4549	35	Kartoffelbrot	\N
6668	606	3	4550	36	Kürbisbrot	\N
6669	606	3	4551	37	Mehrkornbrot mit Ölsamen	\N
6670	606	3	13300	38	Möhrenbrot	\N
6671	607	3	4553	1	Pumpernickel	\N
6672	607	3	4554	2	Weizenknäckebrot	\N
6673	607	3	4555	3	Weizenmischknäckebrot	\N
6674	607	3	4556	4	Roggenknäckebrot	\N
6675	607	3	4557	5	Roggenmischknäckebrot	\N
6676	607	3	4558	6	Matze	\N
6677	607	3	4559	7	Fladenbrot	\N
6678	607	3	4560	8	Holzofenbrot	\N
6679	607	3	4561	9	Steinofenbrot	\N
6680	607	3	4562	10	Gersterbrot	\N
6681	608	3	4564	1	Sesamknäckebrot	\N
6682	608	3	4565	2	Milchknäckebrot	\N
6683	608	3	4566	3	Kleieknäckebrot	\N
6684	608	3	4567	4	Kümmelknäckebrot	\N
6685	608	3	4568	5	Knäckebrot mit anderen Zusätzen	\N
6686	608	3	4569	6	Roggenvollkornknäckebrot	\N
6687	608	3	4570	7	Weizenvollkornknäckebrot	\N
6688	608	3	4571	8	Vollkornknäckebrot	\N
6689	608	3	4572	9	Leinsamenknäckebrot	\N
6690	608	3	4573	10	Mehrkornknäckebrot	\N
6691	608	3	4574	11	Roggenflachbrotextrudat	\N
6692	608	3	4575	12	Weizenflachbrotextrudat	\N
6693	608	3	4576	13	Weizenmischflachbrotextrudat	\N
6694	608	3	4577	14	Roggenmischflachbrotextrudat	\N
6695	608	3	4578	15	Flachbrotextrudat mit Kleiezusatz	\N
6696	608	3	4579	16	Flachbrotextrudat mit Vollkornanteil	\N
6697	608	3	13301	17	Brotchips	\N
6698	609	3	4581	1	Kohlenhydratvermindertes Brot	\N
6699	609	3	4582	2	Eiweißangereichertes Brot	\N
6700	609	3	4583	3	Kohlenhydratvermindertes eiweißangereichertes Brot	\N
6701	611	3	4586	1	Brötchen	\N
6702	611	3	4588	3	Kaisersemmel	\N
6703	611	3	4589	4	Knüppel	\N
6704	611	3	4590	5	Softbrötchen	\N
6705	611	3	4591	6	Weizenkleingebäck vorgebacken	\N
6706	611	3	4592	7	Weizenbrötchen mit Schrotanteil	\N
6707	611	3	4593	8	Weizenschrotbrötchen	\N
6708	611	3	4594	9	Weizenvollkornbrötchen	\N
6709	611	3	4595	10	Baguettebrötchen	\N
6710	611	3	4596	11	Dampfbrötchen	\N
6711	612	3	4598	1	Hörnchen	\N
6712	612	3	4599	2	Schnittbrötchen	\N
6713	612	3	4600	3	Milchbrötchen	\N
6714	612	3	4601	4	Buttermilchbrötchen	\N
6715	612	3	4602	5	Joghurtbrötchen	\N
6716	612	3	4603	6	Quarkbrötchen	\N
6717	613	3	4605	1	Mürbebrötchen	\N
6718	613	3	4607	3	Butterbrötchen	\N
6719	614	3	4609	1	Weizenkleingebäck mit Mohn	\N
6720	614	3	4610	2	Weizenkleingebäck mit Rosinen	\N
6721	614	3	4611	3	Weizenkleingebäck mit Kleie	\N
6722	614	3	4612	4	Weizenkleingebäck mit Sesam	\N
6723	614	3	4613	5	Laugenkleingebäck	\N
6724	614	3	4614	6	Weizenkleingebäck mit Röstzwiebeln	\N
6725	614	3	4615	7	Weizenkleingebäck mit Käse	\N
6726	614	3	4616	8	Weizenkleingebäck mit Kümmel	\N
6727	614	3	4619	11	Weizenkleingebäck mit Speck	\N
6728	614	3	4620	12	Weizenkleingebäck mit Schinken	\N
6729	614	3	4621	13	Weizenkleingebäck mit Ölsamen	\N
6730	614	3	4622	14	Weizenkleingebäck mit Zimt	\N
6731	614	3	4623	15	Weizenkleingebäck mit Müsli	\N
6732	614	3	4624	16	Weizenkleingebäck mit Sonnenblumenkernen	\N
6733	614	3	4625	17	Weizenkleingebäck mit Soja	\N
6734	614	3	4626	18	Weizenkleingebäck mit Leinsamen	\N
6735	615	3	4628	1	Roggenbrötchen	\N
6736	615	3	4629	2	Roggenschrotbrötchen	\N
6737	615	3	4630	3	Roggenvollkornbrötchen	\N
6738	615	3	4631	4	Roggenkleingebäck vorgebacken	\N
6739	615	3	4632	5	Roggenbrötchen mit Schrotanteil	\N
6740	615	3	4633	6	Roggenbrötchen in Baguetteform	\N
6741	617	3	4636	1	Roggenkleingebäck mit Speck	\N
6742	617	3	4637	2	Roggenkleingebäck mit Schinken	\N
6743	617	3	4638	3	Roggenkleingebäck mit Ölsamen	\N
6744	617	3	4639	4	Roggenkleingebäck mit Röstzwiebeln	\N
6745	617	3	4640	5	Roggenkleingebäck mit Mohn	\N
6746	617	3	4641	6	Roggenkleingebäck mit Kleie	\N
6747	617	3	4642	7	Roggenkleingebäck mit Müsli	\N
6748	617	3	4643	8	Roggenkleingebäck mit Sonnenblumenkernen	\N
6749	618	3	4645	1	Schusterjunge	\N
6750	618	3	4646	2	Mehrkornbrötchen	\N
6751	618	3	4647	3	Vollkornbrötchen	\N
6752	618	3	4648	4	Schrotbrötchen	\N
6753	618	3	4649	5	Brötchen aus Mehlmischungen	\N
6754	618	3	4650	6	Brötchen aus Mehlmischungen mit Schrotanteil	\N
6755	618	3	4651	7	Brötchen aus Schrotmischungen	\N
6756	618	3	4652	8	Brötchen aus Vollkornmehlmischungen	\N
6757	619	3	4654	1	Kleingebäck aus Mehlmischungen mit Röstzwiebeln	\N
6758	619	3	4655	2	Kleingebäck aus Mehlmischungen mit Schinken	\N
6759	619	3	4656	3	Kleingebäck aus Mehlmischungen mit Speck	\N
6760	619	3	4657	4	Kleingebäck aus Mehlmischungen mit Ölsamen	\N
6761	619	3	4658	5	Kleingebäck aus Mehlmischungen mit Kleie	\N
6762	619	3	4659	6	Kleingebäck aus Mehlmischungen mit Müsli	\N
6763	620	3	4661	1	Paniermehl	\N
6764	620	3	4662	2	Brotkuchen	\N
6765	620	3	4663	3	Semmelknödel	\N
6766	620	3	4664	4	Knödelbrot	\N
6767	620	3	4665	5	Weizenbrotstücke getrocknet	\N
6768	620	3	4666	6	Brotpudding	\N
6769	620	3	4667	7	Restbrot	\N
6770	621	3	4670	1	Baumkuchen	\N
6771	621	3	4671	2	Baumkuchenspitze	\N
6772	621	3	4672	3	Baumkuchentorte	\N
6773	622	3	4674	1	Tortenboden aus Biskuitmasse	\N
6774	622	3	4675	2	Kapsel aus Biskuitmasse	\N
6775	622	3	4676	3	Löffel-Biskuit	\N
6776	622	3	4677	4	Anisplätzchen aus Biskuitmasse	\N
6777	622	3	4678	5	Obstkuchen aus Biskuitmasse	\N
6778	622	3	4679	6	Käsetorte aus Biskuitmasse	\N
6779	622	3	4680	7	Kapsel aus Biskuitmasse gefüllt	\N
6780	622	3	4681	8	Backware aus Biskuitmasse spirituosenhaltig	\N
6781	622	3	4682	9	Rolle aus Biskuitmasse gefüllt	\N
6782	622	3	4683	10	Tiramisu	\N
6783	623	3	4685	1	Tortenboden aus Wiener Masse	\N
6784	623	3	4686	2	Tortelett	\N
6785	624	3	4689	1	Tortenboden aus Rührmasse	\N
6786	624	3	4690	2	Rührkuchen	\N
6787	624	3	4691	3	Nusskuchen aus Rührmasse	\N
6788	624	3	4692	4	Rosinenkuchen aus Rührmasse	\N
6789	624	3	4693	5	Zitronenkuchen aus Rührmasse	\N
6790	624	3	4694	6	Marmorkuchen aus Rührmasse	\N
6791	624	3	4695	7	Amerikaner feine Backware aus Rührmasse	\N
6792	624	3	4696	8	Früchtekuchen	\N
6793	624	3	4697	9	Marzipankuchen	\N
6794	624	3	4698	10	Backware aus Rührmasse spirituosenhaltig	\N
6795	624	3	4699	11	Rolle aus Rührmasse gefüllt	\N
6796	624	3	4700	12	Rührkuchen mit Fruchtfüllung	\N
6797	624	3	13302	13	Muffin	\N
6798	624	3	14101	14	Obstkuchen aus Rührmassen	\N
6799	625	3	4702	1	Sandkuchen auch als Zitronenkuchen	\N
6800	625	3	4703	2	Nusskuchen aus Sandmasse	\N
6801	625	3	4704	3	Rosinenkuchen aus Sandmasse	\N
6802	625	3	4705	4	Marmorkuchen aus Sandmasse	\N
6803	625	3	4706	5	Englischer Kuchen aus Sandmasse	\N
6804	625	3	4707	6	Königskuchen aus Sandmasse	\N
6805	625	3	4708	7	Königskuchen rheinischer Art aus Sandmasse	\N
6806	625	3	4710	9	Teegebäck aus Sandmasse	\N
6807	625	3	4711	10	Petits Fours aus Sandmasse	\N
6808	625	3	4712	11	Eierplätzchen aus Sandmasse	\N
6809	625	3	4713	12	Backware aus Sandmasse spirituosenhaltig	\N
6810	626	3	4715	1	Baiser	\N
6811	626	3	4716	2	Baiser-Boden	\N
6812	626	3	4717	3	Baiser-Torte	\N
6813	626	3	4718	4	Russisch Brot	\N
6814	626	3	4719	5	Zimtstern	\N
6815	626	3	4720	7	Feine Backware aus Baisermasse mit Füllung u./o. Auflage	\N
6816	627	3	4723	1	Nussmakrone	\N
6817	627	3	4724	2	Persipanmakrone	\N
6818	627	3	4725	3	Kokosmakrone	\N
6819	627	3	4726	4	Florentiner	\N
6820	627	3	4727	5	Makrone	\N
6821	627	3	4728	6	Maserine	\N
6822	627	3	4729	7	Mandelhörnchen	\N
6823	627	3	4730	8	Nussknacker	\N
6824	627	3	13303	9	Nussecke	\N
6825	627	3	13304	10	Ochsenauge  (Backware)	\N
6826	627	3	13305	11	Eisenbahnschiene (Backware)	\N
6827	627	3	14102	12	Mandelmakrone	\N
6828	628	3	4732	1	Waffel	\N
6829	628	3	4733	2	Kremwaffel	\N
6830	628	3	4734	3	Karlsbader Oblate	\N
6831	628	3	4735	4	Eiserkuchen	\N
6832	628	3	4736	5	Waffel mit Füllung	\N
6833	628	3	4737	6	Kremwaffel mit Schokoladenüberzugsmasse	\N
6834	628	3	4738	7	Toastwaffel	\N
6835	629	3	4740	1	Fettgebackenes aus Brandmasse	\N
6836	629	3	4741	2	Spritzkuchen	\N
6837	629	3	4742	3	Eclair	\N
6838	629	3	4743	4	Windbeutel	\N
6839	629	3	4744	5	Backerbse aus Brandmasse	\N
6840	630	3	4746	1	Mürbekeks	\N
6841	630	3	4747	2	Spritzgebäck	\N
6842	630	3	4748	3	Tortenboden aus Mürbeteig	\N
6843	630	3	4749	4	Sandgebäck	\N
6844	630	3	4750	5	Käsekuchen aus Mürbeteig	\N
6845	630	3	4751	6	Käsemürbeteiggebäck	\N
6846	630	3	4752	7	Butterkeks	\N
6847	630	3	4753	8	Albertkeks	\N
6848	630	3	4754	9	Obstkuchen aus Mürbeteig	\N
6849	630	3	4755	10	Mandelspekulatius	\N
6850	630	3	4756	11	Butterspekulatius	\N
6851	630	3	4757	12	Spekulatius	\N
6852	630	3	4758	13	Salzgebäck aus Mürbeteig	\N
6853	630	3	4759	14	Mandelschnitte	\N
6854	630	3	4760	15	Vollkornkeks	\N
6855	630	3	4761	16	Backerbse	\N
6856	630	3	4762	17	Teegebäck	\N
6857	630	3	4763	18	Mürbegebäck mit Nougat/-kremfüllung oder -auflage	\N
6858	631	3	4765	1	Pastetenhülsen	\N
6859	631	3	4766	2	Käseblätterteiggebäck	\N
6860	631	3	4767	3	Schweinsohr aus Blätterteig	\N
6861	631	3	4768	4	Mandel-Nuss-Brezel	\N
6862	631	3	4769	5	Blätterteiggebäck mit nicht süßer Füllung oder Auflage	\N
6863	631	3	4770	6	Blätterteiggebäck mit Obstfüllung	\N
6864	631	3	4771	7	Blätterteiggebäck mit Quarkfüllung	\N
6865	631	3	4772	8	Blätterteiggebäck	\N
6866	631	3	4773	9	Blätterteiggebäck mit süßer Füllung oder Auflage	\N
6867	632	3	4775	1	Butterstuten/-zopf	\N
6868	632	3	4776	2	Rosinenstuten/-zopf	\N
6869	632	3	4777	3	Quarkstuten/-zopf	\N
6870	632	3	4779	5	Fettgebäck aus Hefeteig auch mit Füllung	\N
6871	632	3	4780	6	Feinteig mit Hefe und nicht süßer Füllung	\N
6872	632	3	4781	7	Feine Backware aus Hefeteig vorgebacken	\N
6873	632	3	4782	8	Striezel mit Mohn	\N
6874	632	3	4783	9	Striezel mit süßer Füllung oder Auflage	\N
6875	632	3	4784	10	Plundergebäck auch mit Füllung	\N
6876	632	3	4785	11	Nusszopf	\N
6877	632	3	4786	12	Croissant auch mit Füllung	\N
6878	632	3	4787	13	Brioche	\N
6879	632	3	4788	14	Mohnstuten/-zopf	\N
6880	632	3	4789	15	Stuten/Zopf	\N
6881	632	3	4790	16	Feine Backware aus leichtem Feinteig mit Hefe und süßer Füllung	\N
6882	632	3	13306	18	Donut Doughnut	\N
6883	633	3	4792	1	Dresdener Stollen	\N
6884	633	3	4793	2	Butterstollen	\N
6885	633	3	4794	3	Mandelstollen	\N
6886	633	3	4795	4	Quarkstollen	\N
6887	633	3	4796	5	Mohnstollen	\N
6888	633	3	4797	6	Bienenstich	\N
6889	633	3	4798	7	Streuselkuchen	\N
6890	633	3	4799	8	Butterkuchen	\N
6891	633	3	4800	9	Obstkuchen aus Feinteig mit Hefe	\N
6892	633	3	4801	10	Käsekuchen/Käsetorte	\N
6893	633	3	4802	11	Früchtebrot	\N
6894	633	3	4804	13	Stollen einfach	\N
6895	633	3	4805	14	Marzipanstollen	\N
6896	633	3	4806	15	Mohnkuchen	\N
6897	633	3	4807	16	Mandelkuchen	\N
6898	633	3	4808	17	Nougatstollen	\N
6899	633	3	4809	18	Persipanstollen	\N
6900	633	3	4810	19	Zuckerkuchen	\N
6901	633	3	4811	20	Rahmkuchen	\N
6902	633	3	4812	22	Nussring/-kranz	\N
6903	633	3	4813	23	Nussstollen	\N
6904	633	3	4814	24	Butterstreuselkuchen	\N
6905	633	3	4815	25	Rosinenstollen	\N
6906	633	3	4816	26	Eierschecke	\N
6907	634	3	4818	1	Zwieback einfach	\N
6908	634	3	4819	2	Zwieback mit Schokoladenüberzug	\N
6909	634	3	4820	3	Zwieback mit Zuckerüberzug	\N
6910	634	3	4821	4	Zwieback mit Kokosüberzug	\N
6911	634	3	4822	5	Butterzwieback	\N
6912	634	3	4823	6	Butterzwieback mit Schokoladenüberzug	\N
6913	634	3	4824	7	Butterzwieback mit Zuckerüberzug	\N
6914	634	3	4825	8	Butterzwieback mit Kokosüberzug	\N
6915	634	3	4826	9	Milchzwieback	\N
6916	634	3	4827	10	Eierzwieback	\N
6917	634	3	4828	11	Nährzwieback	\N
6918	634	3	4829	12	Zwieback mit Speck	\N
6919	634	3	4830	13	Vollkornzwieback	\N
6920	634	3	14103	99	Anderes doppelt gebackenes Kleingebäck	\N
6921	635	3	4832	1	Curls Getreideknabbererzeugnis	\N
6922	635	3	4833	2	Chips Getreideknabbererzeugnis	\N
6923	635	3	4834	3	Flips Getreideknabbererzeugnis	\N
6924	635	3	4835	4	Reisgebäck	\N
6925	635	3	4836	5	Maisgebäck	\N
6926	636	3	4839	1	Pizza Kräcker	\N
6927	636	3	4840	2	Kräcker mit Fleisch-/Wursterzeugnissen	\N
6928	636	3	4841	3	Sodakräcker	\N
6929	636	3	4842	4	Kremkräcker	\N
6930	636	3	4843	5	Kräcker mit Ölsamen	\N
6931	636	3	4844	6	Kräcker mit Milcherzeugnissen	\N
6932	637	3	4846	1	Laugendauerbrezel	\N
6933	637	3	4847	2	Salzstangen/-sticks	\N
6934	638	3	4858	10	Vollkornlebkuchen	\N
6935	638	3	4859	11	Oblatenlebkuchen	\N
6936	638	3	4860	12	Oblatenlebkuchen fein	\N
6937	638	3	4861	13	Oblatenlebkuchen feinst	\N
6938	638	3	4862	14	Haselnusslebkuchen	\N
6939	638	3	4863	15	Walnusslebkuchen	\N
6940	638	3	4864	16	Nusslebkuchen	\N
6941	638	3	4865	17	Mandellebkuchen	\N
6942	638	3	4866	18	Marzipanlebkuchen	\N
6943	638	3	4867	19	Makronenlebkuchen	\N
6944	638	3	4868	20	Weißer Lebkuchen	\N
6945	638	3	4869	21	Brauner Lebkuchen	\N
6946	638	3	4870	22	Brauner Lebkuchen fein	\N
6947	638	3	4871	23	Brauner Lebkuchen feinst	\N
6948	638	3	4872	24	Brauner Mandellebkuchen	\N
6949	638	3	4873	25	Brauner Nusslebkuchen	\N
6950	638	3	4874	26	Honiglebkuchen	\N
6951	638	3	4875	27	Dominostein	\N
6952	638	3	4876	28	Printe	\N
6953	638	3	4877	29	Spitzkuchen	\N
6954	639	3	4879	1	Gebäckmischung	\N
6955	639	3	4880	2	Süße Brösel	\N
6956	640	3	4882	1	Gebäck mit Makronenauflage	\N
6957	640	3	4883	2	Schwarzwälder Kirschtorte	\N
6958	640	3	4884	3	Sahnetorte	\N
6959	640	3	4885	4	Kremtorte	\N
6960	640	3	4886	5	Gebäck mit Nussauflage	\N
6961	640	3	4887	6	Obstkuchen -torte mit Mürbeteig u./o. Biskuitboden	\N
6962	640	3	4888	7	Obst-Sahne-Torte mit Mürbeteig u./o. Biskuitboden	\N
6963	640	3	4889	8	Käse-Sahne-Torte mit Mürbeteig u./o. Biskuitboden	\N
6964	640	3	4890	9	Feine Backware mit spirituosenhaltiger Sahne	\N
6965	640	3	4891	10	Feine Backware mit Sahne-Nougat-Kremauflage	\N
6966	640	3	4892	11	Feine Backware mit spirituosenhaltiger Füllung u./o. Auflage	\N
6967	640	3	4893	12	Rumkugel feine Backware	\N
6968	640	3	4894	13	Granatsplitter (Nussbackware)	\N
6969	640	3	4895	14	Linzer Torte	\N
6970	640	3	4896	15	Frankfurter Kranz	\N
6971	640	3	4897	16	Sachertorte	\N
6972	640	3	4898	17	Sahnekremtorte	\N
6973	640	3	4899	18	Butterkremtorte	\N
6974	640	3	4900	19	Punschgebäck/-kugel	\N
6975	640	3	4901	20	Gebäck mit Kokosauflage	\N
6976	640	3	4902	21	Gebäck mit Griesauflage	\N
6977	640	3	4903	22	Feine Backware mit Krem auch mit kakaohaltiger Glasur	\N
6978	641	3	4905	1	Esspapier	\N
6979	643	3	4908	1	Mayonnaise 80% Fett	\N
6980	643	3	4909	2	Salatmayonnaise 50% Fett	\N
6981	643	3	4910	3	Salatmayonnaise mit Kräutern	\N
6982	643	3	4911	4	Remoulade	\N
6983	643	3	4912	5	Salatmayonnaise 50-80% Fett	\N
6984	643	3	4913	6	Dänische Remoulade	\N
6985	643	3	4914	7	Mayonnaisenähnliches Erzeugnis	\N
6986	643	3	14173	8	Mayonnaise 70% Fett	\N
6987	644	3	4916	1	Dressing	\N
6988	644	3	4917	2	Salatkrem	\N
6989	644	3	4918	3	Salatsoße	\N
6990	644	3	13307	4	Dip	\N
6991	644	3	13658	5	Sandwichcreme	\N
6992	646	3	4921	1	Geflügelsalat	\N
6993	646	3	4922	2	Fleischsalat	\N
6994	646	3	4923	3	Ochsenmaulsalat	\N
6995	646	3	4924	4	Wurstsalat mit Essig und Öl	\N
6996	646	3	4925	5	Wurstsalat mit Mayonnaise	\N
6997	646	3	4926	6	Wurstsalat mit emulgierter Soße	\N
6998	646	3	4927	7	Italienischer Salat	\N
6999	646	3	4928	8	Wurstsalat mit Salatmayonnaise	\N
7000	646	3	4929	9	Wurst-/Fleischsalat	\N
7001	646	3	4930	10	Rindfleischsalat	\N
7002	646	3	4931	11	Zigeunersalat	\N
7003	646	3	4932	12	Wurst-/Fleischsalat mit Käse	\N
7004	646	3	4933	13	Gemüsesalat mit Wurst	\N
7005	647	3	4935	1	Heringssalat	\N
7006	647	3	4936	2	Heringssalat mit Mayonnaise	\N
7007	647	3	4937	3	Heringssalat mit emulgierter Soße	\N
7008	647	3	4938	4	Matjessalat	\N
7009	647	3	4939	5	Thunfischsalat	\N
7010	647	3	4940	6	Krabbensalat	\N
7011	647	3	4941	7	Langustensalat	\N
7012	647	3	4942	8	Hummersalat	\N
7013	647	3	4943	9	Shrimpssalat	\N
7014	647	3	4944	10	Muschelsalat	\N
7015	647	3	4945	11	Heringssalat mit Salatmayonnaise	\N
7016	647	3	4946	12	Meeresfrüchtecocktail	\N
7017	647	3	13989	13	Miesmuschelsalat	\N
7018	648	3	4948	1	Obstsalat gemischt	\N
7019	648	3	4949	2	Obstsalat mit anderen Zusätzen	\N
7020	648	3	4950	3	Salat obsthaltig mit emulgierter Soße	\N
7021	649	3	4952	1	Kopfsalat zubereitet	\N
7022	649	3	4953	2	Feldsalat zubereitet	\N
7023	649	3	4954	3	Schnittsalat zubereitet	\N
7024	649	3	4955	4	Eisbergsalat zubereitet	\N
7025	649	3	4956	5	Römischer Salat zubereitet	\N
7026	649	3	4957	6	Chicoreesalat zubereitet	\N
7027	649	3	4958	7	Endiviensalat zubereitet	\N
7028	649	3	4959	8	Chinakohlsalat	\N
7029	649	3	4960	9	Löwenzahnsalat	\N
7030	649	3	4961	10	Rotkohlsalat	\N
7031	649	3	4962	11	Weißkohlsalat	\N
7032	649	3	4963	12	Wirsingsalat	\N
7033	649	3	4964	13	Mangoldsalat	\N
7034	649	3	4965	14	Meldesalat	\N
7035	649	3	4966	15	Porreesalat	\N
7036	649	3	4967	16	Radicchiosalat	\N
7037	649	3	4968	17	Broccolisalat	\N
7038	649	3	4969	18	Kohlrabisalat	\N
7039	649	3	4970	19	Blumenkohlsalat	\N
7040	649	3	4971	20	Artischockensalat	\N
7041	649	3	4972	21	Spargelsalat	\N
7042	649	3	4973	22	Fenchelsalat	\N
7043	649	3	4974	23	Sojakeimlingsalat	\N
7044	649	3	4975	24	Tomatensalat	\N
7045	649	3	4976	25	Paprikasalat	\N
7046	649	3	4977	26	Melonensalat	\N
7047	649	3	4978	27	Gurkensalat	\N
7048	649	3	4979	28	Maissalat	\N
7049	649	3	4980	29	Bohnensalat	\N
7050	649	3	4981	30	Erbsensalat	\N
7051	649	3	4982	31	Mohrrübensalat	\N
7052	649	3	4983	32	Selleriesalat	\N
7053	649	3	4984	33	Rettichsalat	\N
7054	649	3	4985	34	Pilzsalat	\N
7055	649	3	4986	35	Mungo-Bohnensalat	\N
7056	649	3	4987	36	Rote Betesalat	\N
7057	649	3	4988	37	Bohnensalat aus Bohnenkernen	\N
7058	649	3	4989	99	Gemüsesalat gemischt	\N
7059	650	3	4991	1	Kartoffelsalat	\N
7060	650	3	4992	2	Kartoffelsalat mit Mayonnaise	\N
7061	650	3	4993	3	Kartoffelsalat mit emulgierter Soße	\N
7062	650	3	4994	4	Kartoffelsalat mit Speck	\N
7063	650	3	4995	5	Kartoffelsalat mit Ei	\N
7064	650	3	4996	6	Kartoffelsalat mit Brät	\N
7065	650	3	4997	7	Kartoffelsalat mit  Zutaten	\N
7066	651	3	4999	1	Eiersalat	\N
7067	651	3	5000	2	Russisch Ei	\N
7068	652	3	5002	1	Straßburger Käsesalat	\N
7069	653	3	5004	1	Reissalat	\N
7070	653	3	5005	2	Waldorfsalat	\N
7071	653	3	5006	3	Nudelsalat	\N
7072	653	3	5007	4	Gemüsesalat mit Brät	\N
7073	654	3	5009	1	Geflügelsalat Präserve	\N
7074	654	3	5010	2	Fleischsalat Präserve	\N
7075	654	3	5011	3	Ochsenmaulsalat Präserve	\N
7076	654	3	5012	4	Wurstsalat mit Essig und Öl Präserve	\N
7077	654	3	5013	5	Wurstsalat mit Mayonnaise Präserve	\N
7078	654	3	5014	6	Wurstsalat mit emulgierter Soße Präserve	\N
7079	654	3	5015	7	Italienischer Salat Präserve	\N
7080	654	3	5016	8	Wurstsalat mit Salatmayonnaise Präserve	\N
7081	654	3	5017	9	Wurst-/Fleischsalat Präserve	\N
7082	654	3	5018	10	Rindfleischsalat Präserve	\N
7083	654	3	5019	11	Zigeunersalat Präserve	\N
7084	654	3	5020	12	Wurstsalat mit Käse Präserve	\N
7085	655	3	5022	1	Heringssalat Präserve	\N
7086	655	3	5023	2	Heringssalat mit Mayonnaise Präserve	\N
7087	655	3	5024	3	Heringssalat mit emulgierter Soße Präserve	\N
7088	655	3	5025	4	Matjessalat Präserve	\N
7089	655	3	5026	5	Thunfischsalat Präserve	\N
7090	655	3	5027	6	Krabbensalat Präserve	\N
7091	655	3	5028	7	Langustensalat Präserve	\N
7092	655	3	5029	8	Hummersalat Präserve	\N
7093	655	3	5030	9	Shrimpssalat Präserve	\N
7094	655	3	5031	10	Muschelsalat Präserve	\N
7095	655	3	5032	11	Heringssalat mit Salatmayonnaise Präserve	\N
7096	655	3	13990	12	Miesmuschelsalat Präserve	\N
7097	656	3	5034	1	Rotkohlsalat Präserve	\N
7098	656	3	5035	2	Weißkohlsalat Präserve	\N
7099	656	3	5036	3	Wirsingsalat Präserve	\N
7100	656	3	5037	4	Mangoldsalat Präserve	\N
7101	656	3	5038	5	Porreesalat Präserve	\N
7102	656	3	5039	6	Radicchiosalat Präserve	\N
7103	656	3	5040	7	Broccolisalat Präserve	\N
7104	656	3	5041	8	Kohlrabisalat Präserve	\N
7105	656	3	5042	9	Blumenkohlsalat Präserve	\N
7106	656	3	5043	10	Artischockensalat Präserve	\N
7107	656	3	5044	11	Spargelsalat Präserve	\N
7108	656	3	5045	12	Fenchelsalat Präserve	\N
7109	656	3	5046	13	Sojakeimlingsalat Präserve	\N
7110	656	3	5047	14	Tomatensalat Präserve	\N
7111	656	3	5048	15	Paprikasalat Präserve	\N
7112	656	3	5049	16	Melonensalat Präserve	\N
7113	656	3	5050	17	Gurkensalat Präserve	\N
7114	656	3	5051	18	Maissalat Präserve	\N
7115	656	3	5052	19	Bohnensalat Präserve	\N
7116	656	3	5053	20	Erbsensalat Präserve	\N
7117	656	3	5054	21	Mohrrübensalat Präserve	\N
7118	656	3	5055	22	Selleriesalat Präserve	\N
7119	656	3	5056	23	Rettichsalat Präserve	\N
7120	656	3	5057	24	Pilzsalat Präserve	\N
7121	656	3	5058	25	Mungo-Bohnensalat Präserve	\N
7122	656	3	5059	26	Bohnensalat aus Bohnenkernen Präserve	\N
7123	656	3	5060	99	Gemüsesalat gemischt Präserve	\N
7124	657	3	5062	1	Kartoffelsalat Präserve	\N
7125	657	3	5063	2	Kartoffelsalat mit Mayonnaise Präserve	\N
7126	657	3	5064	3	Kartoffelsalat mit emulgierter Soße Präserve	\N
7127	657	3	5065	4	Kartoffelsalat mit Speck Präserve	\N
7128	658	3	5067	1	Eiersalat Präserve	\N
7129	659	3	5069	1	Straßburger Käsesalat Präserve	\N
7130	660	3	5071	1	Reissalat Präserve	\N
7131	660	3	5072	2	Waldorfsalat Präserve	\N
7132	660	3	5073	3	Nudelsalat Präserve	\N
7133	661	3	5075	1	Geflügelsalat Konserve	\N
7134	661	3	5076	2	Fleischsalat Konserve	\N
7135	661	3	5077	3	Ochsenmaulsalat Konserve	\N
7136	662	3	5079	1	Heringssalat Konserve	\N
7137	662	3	5080	2	Thunfischsalat Konserve	\N
7138	662	3	5081	3	Muschelsalat Konserve	\N
7139	662	3	13991	4	Miesmuschelsalat Konserve	\N
7140	663	3	5083	1	Rotkohlsalat Konserve	\N
7141	663	3	5084	2	Weißkohlsalat Konserve	\N
7142	663	3	5085	3	Blumenkohlsalat Konserve	\N
7143	663	3	5086	4	Sojakeimlingsalat Konserve	\N
7144	663	3	5087	5	Gurkensalat Konserve	\N
7145	663	3	5088	6	Maissalat Konserve	\N
7146	663	3	5089	7	Bohnensalat Konserve	\N
7147	663	3	5090	8	Erbsensalat Konserve	\N
7148	663	3	5091	9	Mohrrübensalat Konserve	\N
7149	663	3	5092	10	Selleriesalat Konserve	\N
7150	663	3	5093	11	Rote Betesalat Konserve	\N
7151	663	3	5094	12	Bohnensalat aus Bohnenkernen Konserve	\N
7152	664	3	5096	1	Kartoffelsalat Konserve	\N
7153	666	3	5099	1	Geflügelsalat brennwertreduziert	\N
7154	666	3	5100	2	Fleischsalat brennwertreduziert	\N
7155	666	3	5101	3	Ochsenmaulsalat brennwertreduziert	\N
7156	666	3	5102	4	Wurstsalat mit Essig und Öl brennwertreduziert	\N
7157	666	3	5103	5	Wurstsalat mit Mayonnaise brennwertreduziert	\N
7158	666	3	5104	6	Wurstsalat mit emulgierter Soße brennwertreduziert	\N
7159	666	3	5105	7	Italienischer Salat brennwertreduziert	\N
7160	666	3	5106	9	Wurst-/Fleischsalat brennwertreduziert	\N
7161	666	3	5107	10	Rindfleischsalat brennwertreduziert	\N
7162	666	3	5108	11	Zigeunersalat brennwertreduziert	\N
7163	666	3	5109	12	Wurst-/Fleischsalat mit Käse brennwertreduziert	\N
7164	667	3	5111	1	Heringssalat brennwertreduziert	\N
7165	667	3	5112	2	Matjessalat brennwertreduziert	\N
7166	667	3	5113	3	Thunfischsalat brennwertreduziert	\N
7167	667	3	5114	4	Krabbensalat brennwertreduziert	\N
7168	673	3	5121	1	Reissalat brennwertreduziert	\N
7169	673	3	5122	2	Waldorfsalat brennwertreduziert	\N
7170	673	3	5123	3	Nudelsalat brennwertreduziert	\N
7171	674	3	5125	1	Geflügelsalat brennwertreduziert Präserve	\N
7172	674	3	5126	2	Fleischsalat brennwertreduziert Präserve	\N
7173	674	3	5127	3	Ochsenmaulsalat brennwertreduziert Präserve	\N
7174	674	3	5128	4	Wurstsalat mit Essig und Öl brennwertreduziert Präserve	\N
7175	674	3	5129	5	Wurstsalat mit Mayonnaise brennwertreduziert Präserve	\N
7176	674	3	5130	6	Wurstsalat mit emulgierter Soße brennwertreduziert Präserve	\N
7177	674	3	5131	7	Italienischer Salat brennwertreduziert Präserve	\N
7178	674	3	5132	9	Wurst-/Fleischsalat brennwertreduziert Präserve	\N
7179	674	3	5133	10	Rindfleischsalat brennwertreduziert Präserve	\N
7180	674	3	5134	11	Zigeunersalat brennwertreduziert Präserve	\N
7181	674	3	5135	12	Wurst-/Fleischsalat mit Käse brennwertreduziert Präserve	\N
7182	675	3	5137	1	Heringssalat brennwertreduziert Präserve	\N
7183	675	3	5138	2	Matjessalat brennwertreduziert Präserve	\N
7184	675	3	5139	3	Thunfischsalat brennwertreduziert Präserve	\N
7185	675	3	5140	4	Krabbensalat brennwertreduziert Präserve	\N
7186	681	3	5147	1	Reissalat brennwertreduziert Präserve	\N
7187	681	3	5148	2	Waldorfsalat brennwertreduziert Präserve	\N
7188	681	3	5149	3	Nudelsalat brennwertreduziert Präserve	\N
7189	682	3	5152	1	Pudding mit Vanille	\N
7190	682	3	5153	2	Pudding mit Vanillegeschmack	\N
7191	682	3	5154	3	Pudding mit Kaffee	\N
7192	682	3	5155	4	Pudding mit Mokkageschmack	\N
7193	682	3	5156	5	Pudding mit Kakao	\N
7194	682	3	5157	6	Pudding mit Schokoladengeschmack	\N
7195	682	3	5158	7	Pudding mit Karamelgeschmack	\N
7196	682	3	5159	8	Pudding mit Schalenobst	\N
7197	682	3	5160	9	Pudding mit Früchten	\N
7198	682	3	5161	10	Pudding mit Fruchtgeschmack	\N
7199	682	3	5162	11	Rote Grütze zubereitet	\N
7200	682	3	5163	12	Grüne Grütze zubereitet	\N
7201	682	3	5164	13	Gelbe Grütze zubereitet	\N
7202	682	3	5165	14	Karamelpudding	\N
7203	682	3	5166	15	Pudding mit Schalenobstgeschmack	\N
7204	682	3	5167	99	Pudding gemischt zubereitet	\N
7205	683	3	5169	1	Puddingpulver mit Vanille	\N
7206	683	3	5170	2	Puddingpulver mit Vanillegeschmack	\N
7207	683	3	5171	3	Puddingpulver mit Kaffee	\N
7208	683	3	5172	4	Puddingpulver mit Mokkageschmack	\N
7209	683	3	5173	5	Puddingpulver mit Kakao	\N
7210	683	3	5174	6	Puddingpulver mit Schokoladengeschmack	\N
7211	683	3	5175	7	Puddingpulver mit Karamelgeschmack	\N
7212	683	3	5176	8	Puddingpulver mit Schalenobst	\N
7213	683	3	5177	9	Puddingpulver mit Früchten	\N
7214	683	3	5178	10	Puddingpulver mit Fruchtgeschmack	\N
7215	683	3	5179	11	Puddingpulver mit Sahnegeschmack	\N
7216	683	3	5180	12	Rote Grützepulver	\N
7217	683	3	5181	13	Grüne Grützepulver	\N
7218	683	3	5182	14	Gelbe Grützepulver	\N
7219	683	3	5183	15	Karamelpuddingpulver	\N
7220	683	3	5184	16	Puddingpulver mit Schalenobstgeschmack	\N
7221	683	3	5185	99	Puddingpulver gemischt	\N
7222	684	3	5187	1	Kremspeise mit Vanille	\N
7223	684	3	5188	2	Kremspeise mit Vanillegeschmack	\N
7224	684	3	5189	3	Kremspeise mit Kaffee	\N
7225	684	3	5190	4	Kremspeise mit Mokkageschmack	\N
7226	684	3	5191	5	Kremspeise mit Schokoladengeschmack	\N
7227	684	3	5192	6	Kremspeise mit Kakao	\N
7228	684	3	5193	7	Kremspeise mit Schalenobst	\N
7229	684	3	5194	8	Kremspeise mit Früchten	\N
7230	684	3	5195	9	Kremspeise mit Fruchtgeschmack	\N
7231	684	3	5196	10	Kremspeise mit Weinzusatz	\N
7232	684	3	5197	11	Kremspeise mit Spirituosenzusatz	\N
7233	684	3	5198	12	Kremspeise mit Schalenobstgeschmack	\N
7234	684	3	5199	99	Kremspeise gemischt	\N
7235	685	3	5201	1	Kremspeisenpulver mit Vanille	\N
7236	685	3	5202	2	Kremspeisenpulver mit Vanillegeschmack	\N
7237	685	3	5203	3	Kremspeisenpulver mit Kaffee	\N
7238	685	3	5204	4	Kremspeisenpulver mit Mokkageschmack	\N
7239	685	3	5205	5	Kremspeisenpulver mit Schokoladengeschmack	\N
7240	685	3	5206	6	Kremspeisenpulver mit Kakao	\N
7241	685	3	5207	7	Kremspeisenpulver mit Schalenobst	\N
7242	685	3	5208	8	Kremspeisenpulver mit Früchten	\N
7243	685	3	5209	9	Kremspeisenpulver mit Fruchtgeschmack	\N
7244	685	3	5210	10	Kremspeisenpulver mit Weinbeigabe	\N
7245	685	3	5211	11	Kremspeisenpulver mit Spirituosenbeigabe	\N
7246	685	3	5212	12	Kremspeisenpulver mit Schalenobstgeschmack	\N
7247	685	3	5213	99	Kremspeisenpulver gemischt	\N
7248	686	3	5215	1	Geleespeise mit Waldmeistergeschmack	\N
7249	686	3	5216	2	Geleespeise mit Fruchtgeschmack	\N
7250	686	3	5217	3	Geleespeise mit Colageschmack	\N
7251	687	3	5219	1	Geleespeisenpulver mit Waldmeistergeschmack	\N
7252	687	3	5220	2	Geleespeisenpulver mit Fruchtgeschmack	\N
7253	687	3	5221	3	Geleespeisenpulver mit Colageschmack	\N
7254	688	3	5224	1	Milchreis gekocht mit Früchten	\N
7255	688	3	5225	2	Milchreis gekocht mit Zimt	\N
7256	688	3	5226	3	Milchreis gekocht mit Schokoladensoße	\N
7257	689	3	5228	1	Grießpudding	\N
7258	689	3	5229	2	Grießpudding mit Soße	\N
7259	690	3	5231	1	Milchreis Dessertpulver	\N
7260	691	3	5233	1	Schokoladensoßenpulver	\N
7261	691	3	5234	2	Vanillesoßenpulver	\N
7262	691	3	5235	3	Karamelsoßenpulver	\N
7263	691	3	5236	4	Fruchtsoßenpulver	\N
7264	691	3	5237	5	Soßenpulver mit Vanillegeschmack	\N
7265	693	3	5240	1	Vanillesoße flüssig	\N
7266	693	3	5241	2	Schokoladensoße flüssig	\N
7267	693	3	5242	3	Himbeersoße flüssig	\N
7268	693	3	5243	4	Erdbeersoße flüssig	\N
7269	693	3	5244	5	Kirschsoße flüssig	\N
7270	693	3	5245	6	Brombeersoße flüssig	\N
7271	693	3	5246	7	Heidelbeersoße flüssig	\N
7272	693	3	5247	8	Weinsoße flüssig	\N
7273	693	3	5248	9	Moccasoße flüssig	\N
7274	693	3	5249	10	Karamelsoße flüssig	\N
7275	693	3	5250	11	Haselnusssoße flüssig	\N
7276	693	3	5251	12	Kakaosoße flüssig	\N
7277	693	3	5252	13	Fruchtsoße aus mehreren Früchten flüssig	\N
7278	693	3	5253	14	Soße mit Vanillegeschmack flüssig	\N
7279	693	3	5254	15	Soße mit Sahnegeschmack flüssig	\N
7280	693	3	5255	16	Kiwisoße flüssig	\N
7281	693	3	5256	17	Amarettosoße flüssig	\N
7282	693	3	5257	18	Maracujasoße flüssig	\N
7283	694	3	14248	1	Hartweizenteigware	\N
7284	698	3	5264	1	Gemüseteigware	\N
7285	698	3	5265	2	Lecithinteigware	\N
7286	698	3	5266	3	Vollkornteigware	\N
7287	698	3	5267	4	Kleberteigware	\N
7288	698	3	5268	5	Graumehlteigware	\N
7289	698	3	5269	6	Roggenteigware	\N
7290	698	3	5270	7	Sojateigware	\N
7291	698	3	5271	8	Milchteigware	\N
7292	698	3	5272	9	Mehrkornteigware	\N
7293	698	3	5273	10	Reisnudel	\N
7294	698	3	5274	11	Glasnudel	\N
7295	698	3	5275	12	Vollkornteigware mit Eiern	\N
7296	698	3	5276	13	Vollkornteigware eifrei	\N
7297	698	3	5277	14	Dinkelteigware	\N
7298	698	3	5278	15	Dinkelvollkornteigware	\N
7299	698	3	5279	17	Teigware mit Zutaten	\N
7300	698	3	5280	18	Maisteigware	\N
7301	698	3	5281	19	Kräuterteigware	\N
7302	698	3	5282	20	Buchweizenteigware	\N
7303	698	3	5283	21	Hirseteigware	\N
7304	699	3	5285	1	Nudelteig mit normalem Eigehalt	\N
7305	699	3	5286	2	Nudelteig mit hohem Eigehalt	\N
7306	699	3	5287	3	Nudelteig mit besonders hohem Eigehalt	\N
7307	699	3	5288	4	Nudelteig eifrei	\N
7308	699	3	5289	5	Maultaschenteig	\N
7309	700	3	5291	1	Spätzle vorgebrüht feucht	\N
7310	700	3	5292	2	Ramen	\N
7311	700	3	5293	3	Spätzle vorgebrüht getrocknet	\N
7312	700	3	5294	4	Teigware vorgebrüht feucht	\N
7313	700	3	5295	5	Teigware nicht getrocknet	\N
7314	701	3	5298	2	Flädle	\N
7315	701	3	5299	3	Grießkloß	\N
7316	702	3	5301	1	Fertiggrießmischung für Teigwaren	\N
7317	702	3	5302	2	Fertiggrießmischung für Teigwaren eihaltig	\N
7318	703	3	5305	1	Erbse gelb	\N
7319	703	3	5306	2	Erbse grün	\N
7320	703	3	5307	3	Kichererbse	\N
7321	703	3	5308	4	Linse grün	\N
7322	703	3	5309	5	Bohne weiß	\N
7323	703	3	5310	6	Bohne braun	\N
7324	703	3	5311	7	Bohne schwarz	\N
7325	703	3	5312	8	Bohne rot	\N
7326	703	3	5313	9	Süßlupine	\N
7327	703	3	5314	10	Dickbohne	\N
7328	703	3	5319	15	Wachtelbohne	\N
7329	703	3	5320	16	Linse rot	\N
7330	703	3	5321	17	Linse gelb	\N
7331	703	3	5322	18	Mungobohne grün	\N
7332	703	3	5323	19	Erbse grün geschält	\N
7333	703	3	5324	20	Erbse gelb geschält	\N
7334	703	3	5325	21	Augenbohne	\N
7335	703	3	5326	22	Sojabohne	\N
7336	703	3	5327	23	Linse braun	\N
7337	703	3	5328	24	Limabohne	\N
7338	704	3	5330	1	gestrichen jetzt 230122, Sojabohne	\N
7339	704	3	5331	2	Sojakeimmehl	\N
7340	704	3	5332	3	Sojamehl	\N
7341	704	3	5333	4	Sojamehl entbittert	\N
7342	704	3	5334	5	Sojaeiweiß	\N
7343	704	3	5335	6	Sojaeiweiß texturiertes	\N
7344	704	3	5336	7	Sojaerzeugnis weiterverarbeitet	\N
7345	704	3	5337	8	Sojamehl entfettet entbittert	\N
7346	704	3	5338	9	Tofu	\N
7347	704	3	5339	10	Sojatrunk	\N
7348	704	3	5340	11	Sojatrunkpulver	\N
7349	704	3	5341	12	Tofuhaltiges Erzeugnis	\N
7350	704	3	5342	13	Sojagrieß	\N
7351	704	3	5343	14	Sojabratling-Grundmischung	\N
7352	704	3	5344	15	Wurstware auf Sojabasis	\N
7353	704	3	5345	16	Pudding Dessert auf Sojabasis	\N
7354	704	3	14149	17	Sojaflocken	\N
7355	705	3	5347	1	Linsenkonserve	\N
7356	705	3	5348	2	Trockenbohnenkonserve	\N
7357	705	3	5349	3	Trockenerbsenkonserve	\N
7358	705	3	5350	4	Haushaltsmischung aus Hülsenfrüchten Konserve	\N
7359	705	3	5352	6	Lupine gesalzen Konserve	\N
7360	705	3	5353	7	Trockenkichererbsenkonserve	\N
7361	706	3	5355	1	Rübsaat	\N
7362	706	3	5356	2	Mohn	\N
7363	706	3	5357	3	Leinsamen	\N
7364	706	3	5358	4	Sonnenblumenkern	\N
7365	706	3	5359	5	Baumwollsaat	\N
7366	706	3	5360	6	Buchecker	\N
7367	706	3	5361	7	Raps	\N
7368	706	3	5362	8	Sesam	\N
7369	706	3	5363	9	Kürbiskern	\N
7370	706	3	5364	10	Aprikosenkern	\N
7371	706	3	5365	11	Pfirsichkern	\N
7372	706	3	5366	12	Pinienkern	\N
7373	706	3	13308	13	Hanfsaat	\N
7374	706	3	13819	21	Aprikosenkern, süß	\N
7375	706	3	13820	22	Aprikosenkern, bitter	\N
7376	706	3	14060	24	Ogbono Samen Frucht von Irvingio gabonensis/wonbolu	\N
7377	706	3	14119	25	Melonenkern	\N
7378	706	3	13309	97	Mischung aus Ölsamen auch mit anderen LM	\N
7379	707	3	5368	1	Erdnuss	\N
7380	707	3	5369	2	Walnuss	\N
7381	707	3	5370	3	Haselnuss	\N
7382	707	3	5371	4	Hickorynuss	\N
7383	707	3	5372	5	Kokosnuss	\N
7384	707	3	5373	6	Paranuss	\N
7385	707	3	5374	7	Cashewnuss	\N
7386	707	3	5375	8	Mandel süß	\N
7387	707	3	5376	9	Mandel bitter	\N
7388	707	3	5379	12	Pistazie	\N
7389	707	3	5380	13	gestrichen jetzt 230409, Kürbiskern	\N
7390	707	3	5382	15	Pecannuss	\N
7391	707	3	5383	16	Macadamianuss	\N
7392	707	3	5384	17	Erdmandel	\N
7393	707	3	5385	18	Kolanuss	\N
7394	707	3	5386	97	Gemischtes Schalenobst mit anderen LM	\N
7395	707	3	5387	98	Gemischtes Schalenobst mit Trockenobst	\N
7396	707	3	5388	99	Gemischtes Schalenobst	\N
7397	708	3	5390	1	Marone	\N
7398	708	3	5391	2	Marone geröstet	\N
7399	708	3	5392	3	Marone Konserve	\N
7400	708	3	14182	4	Marone gekocht	\N
7401	708	3	14183	5	Marone gemahlen	\N
7402	709	3	5394	1	Erdnuss geröstet un-/gesalzen	\N
7403	709	3	5395	2	Mandel geröstet un-/gesalzen	\N
7404	709	3	5396	3	Cashewnuss geröstet un-/gesalzen	\N
7405	709	3	5397	4	Pistazie geröstet un-/gesalzen	\N
7406	709	3	5398	5	Haselnuss geröstet un-/gesalzen	\N
7407	709	3	5399	6	Gemischtes Schalenobst geröstet un-/gesalzen	\N
7408	709	3	5400	7	Gemischte Nüsse geröstet un-/gesalzen mit Trockenobst	\N
7409	709	3	5401	8	Macadamianuss geröstet un-/gesalzen	\N
7410	709	3	5402	9	Mandel geräuchert	\N
7411	709	3	5403	10	Erdnuss geröstet mit Schale	\N
7412	709	3	5404	11	Mischung aus Ölsamen Schalenobst und anderen LM	\N
7413	709	3	5405	12	Mischung aus Ölsamen Schalenobst und Trockenobst	\N
7414	709	3	5406	13	Studentenfutter	\N
7415	709	3	13992	20	Mandel mit Schokoladen-/Kakaoüberzug	\N
7416	709	3	13993	21	Haselnuss mit Schokoladen-/Kakaoüberzug	\N
7417	709	3	13994	22	Erdnuss mit Schokoladen-/Kakaoüberzug	\N
7418	709	3	13995	23	Macadamianuss mit Schokoladen-/Kakaoüberzug	\N
7419	709	3	14150	24	Erdnuss im Teigmantel	\N
7420	710	3	5408	1	Kokosmilch	\N
7421	710	3	5409	2	Mohn gemahlen	\N
7422	710	3	5410	3	Kokosnuss geraspelt	\N
7423	710	3	5411	4	Haselnuss gemahlen	\N
7424	710	3	5412	5	Haselnuss geraspelt	\N
7425	710	3	5413	6	Mandel gemahlen	\N
7426	710	3	5414	7	Mandel gestiftet	\N
7427	710	3	5415	8	Mandel gehobelt	\N
7428	710	3	5416	9	Mandel gehackt	\N
7429	710	3	5417	10	Haselnuss gehackt	\N
7430	710	3	5419	12	Erdnuss gehobelt	\N
7431	710	3	5420	13	Haselnuss blanchiert	\N
7432	710	3	5421	14	Mandel blanchiert	\N
7433	710	3	5422	15	Cashewnuss gemahlen	\N
7434	710	3	5423	16	Walnuss gemahlen	\N
7435	710	3	5424	17	Kokoschips geröstet/gesüßt	\N
7436	710	3	5425	18	Haselnuss gehobelt	\N
7437	710	3	5426	19	Erdmandel gehobelt	\N
7438	710	3	5427	20	Haselnussmasse	\N
7439	710	3	5428	21	Erdnussmasse	\N
7440	710	3	5429	22	Mandelmasse	\N
7441	710	3	5430	23	Erdnussriegel	\N
7442	710	3	5431	24	Sonnenblumenkern geschält un-/gesalzen	\N
7443	710	3	5432	25	Leinsamen aufgebrochen/geschrotet	\N
7444	710	3	5433	26	Leinsamen mit Zusätzen	\N
7445	710	3	5434	27	Mandel mit Samenschale blanchiert	\N
7446	710	3	5435	28	Kürbiskern geröstet un-/gesalzen	\N
7447	710	3	13310	30	Hanfsamen gemahlen	\N
7448	710	3	14140	31	Pistazienriegel	\N
7449	710	3	14184	32	Sesam geröstet	\N
7450	710	3	14185	33	Sesam gemahlen, un-/gesalzen	\N
7451	710	3	14186	34	Traubenkernmehl	\N
7452	711	3	5437	1	Kichererbsmus	\N
7453	711	3	5438	2	Bohnenmehl	\N
7454	711	3	5439	3	Erbsmehl	\N
7455	711	3	5440	4	Erbspüreepulver	\N
7456	711	3	14187	5	Lupinenmehl	\N
7457	711	3	14188	6	Kichererbsen geröstet, un-/gesalzen	\N
7458	712	3	5451	1	Kartoffel früh	\N
7459	712	3	5452	2	Kartoffel festkochend (Salatware)	\N
7460	712	3	5453	3	Kartoffel vorwiegend festkochend	\N
7461	712	3	5454	4	Kartoffel mehlig festkochend	\N
7462	713	3	5456	1	Kartoffel roh geschält	\N
7463	713	3	5457	2	Kartoffel hitzesterilisiert Konserve	\N
7464	713	3	5458	3	Kartoffel roh tiefgefroren	\N
7465	713	3	5459	4	Kartoffel blanchiert	\N
7466	713	3	5460	5	Kartoffel blanchiert tiefgefroren	\N
7467	714	3	5462	1	Kartoffel gegart mit Schale	\N
7468	714	3	5463	2	Kartoffel gegart ohne Schale	\N
7469	714	3	5464	3	Kartoffel gegart tiefgefroren	\N
7470	714	3	5465	4	Kartoffelkloß gegart	\N
7471	714	3	5466	5	Kartoffelkloß gegart tiefgefroren	\N
7472	714	3	5467	6	Kartoffelpuffer gegart	\N
7473	714	3	5468	7	Kartoffelpuffer gegart tiefgefroren	\N
7474	714	3	5469	8	Kroketten gegart	\N
7475	714	3	5470	9	Kroketten gegart tiefgefroren	\N
7476	714	3	5471	10	Pommes parisienne gegart	\N
7477	714	3	5472	11	Pommes parisienne gegart tiefgefroren	\N
7478	714	3	5473	12	Pommes frites gegart	\N
7479	714	3	5474	13	Pommes frites gegart tiefgefroren	\N
7480	714	3	5475	14	Bratkartoffeln aus rohen Kartoffeln	\N
7481	714	3	5476	15	Bratkartoffeln aus gegarten Kartoffeln	\N
7482	714	3	5477	16	Kartoffelbrei gegart	\N
7483	715	3	5479	1	Rösti vorgebacken	\N
7484	715	3	5480	2	Kartoffelkloß auch vorgegart	\N
7485	715	3	5481	3	Pommes parisienne auch vorfrittiert	\N
7486	715	3	5482	4	Pommes frites auch vorfrittiert	\N
7487	715	3	5483	5	Pommes chips auch vorfrittiert	\N
7488	715	3	5484	6	Bratkartoffeln auch vorgebraten	\N
7489	715	3	5485	7	Rösti vorgebacken tiefgefroren	\N
7490	715	3	5486	8	Kartoffelkloß auch vorgegart tiefgefroren	\N
7491	715	3	5487	9	Pommes parisienne auch vorfrittiert tiefgefroren	\N
7492	715	3	5488	10	Pommes frites auch vorfrittiert tiefgefroren	\N
7493	715	3	5489	11	Pommes chips auch vorfrittiert tiefgefroren	\N
7494	715	3	5490	12	Bratkartoffeln auch vorgebraten tiefgefroren	\N
7495	715	3	5491	15	Gnocchi auch tiefgefroren	\N
7496	715	3	5492	17	Kartoffelschupfnudel auch tiefgefroren	\N
7497	716	3	5494	1	Trockenkartoffeln	\N
7498	716	3	5495	2	Kartoffelflocken	\N
7499	716	3	5496	3	Kartoffelpufferpulver	\N
7500	716	3	5497	4	Kartoffelpufferpulver mit Zusätzen	\N
7501	716	3	5498	5	Röstipulver	\N
7502	716	3	5499	6	Kartoffelbreipulver	\N
7503	716	3	5500	7	Kartoffelkloßpulver	\N
7504	716	3	5501	8	Kartoffelkloßpulver mit Zusätzen	\N
7505	717	3	5503	1	Kartoffelteig aus rohen Kartoffeln	\N
7506	717	3	5504	2	Kartoffelteig aus rohen Kartoffeln tiefgefroren	\N
7507	717	3	5505	3	Kartoffelteig aus gekochten Kartoffeln	\N
7508	717	3	5506	4	Kartoffelteig aus gekochten Kartoffeln tiefgefroren	\N
7509	717	3	5507	5	Kartoffelteig aus rohen und gekochten Kartoffeln tiefgefr.	\N
7510	717	3	5508	6	Kartoffelteig aus rohen und gekochten Kartoffeln	\N
7511	718	3	5510	1	Chips	\N
7512	718	3	5511	2	Sticks	\N
7513	718	3	5512	3	Chipsletten	\N
7514	718	3	5513	4	Knabbererzeugnis aus Kartoffeln	\N
7515	718	3	5514	5	Knabbererzeugnis aus Kartoffelpulver	\N
7516	719	3	5516	1	Native Kartoffelstärke	\N
7517	719	3	5517	2	Quellstärke aus Kartoffeln	\N
7518	719	3	5518	3	Lösliche Kartoffelstärke	\N
7519	719	3	5519	4	Amylopektin	\N
7520	720	3	5521	1	Tapioka	\N
7521	720	3	5522	2	Süßkartoffel	\N
7522	720	3	5523	3	Topinambur	\N
7523	720	3	5524	4	Japanknolle	\N
7524	720	3	5525	5	Yams-Wurzel	\N
7525	720	3	5526	6	Taro	\N
7526	720	3	5527	7	Pfeilwurz Maranta	\N
7527	721	3	5529	1	Batatenstärke	\N
7528	721	3	5530	2	Tapiokastärke	\N
7529	721	3	5531	3	Sago	\N
7530	721	3	5532	4	Marantastärke	\N
7531	723	3	5536	1	Kopfsalat	\N
7532	723	3	5537	2	Feldsalat	\N
7533	723	3	5538	3	Schnittsalat	\N
7534	723	3	5539	4	Römischer Salat	\N
7535	723	3	5540	5	Chicoree	\N
7536	723	3	5541	6	Endivie	\N
7537	723	3	5542	7	Chinakohl	\N
7538	723	3	5543	8	Löwenzahn	\N
7539	723	3	5544	9	Rosenkohl	\N
7540	723	3	5545	10	Rotkohl	\N
7541	723	3	5546	11	Weißkohl, Spitzkohl	\N
7542	723	3	5547	12	Grünkohl	\N
7543	723	3	5548	13	Wirsingkohl	\N
7544	723	3	5549	14	Spinat	\N
7545	723	3	5550	15	Bleich-/Stauden-/Stangensellerie	\N
7546	723	3	5551	16	Knollensellerieblätter	\N
7547	723	3	5552	17	Petersilienblätter	\N
7548	723	3	5553	18	Schnittlauch	\N
7549	723	3	5554	19	Kresse/Garten-/Kapuzinerkresse	\N
7550	723	3	5555	20	Mangold	\N
7551	723	3	5556	21	Melde	\N
7552	723	3	5557	22	Porree	\N
7553	723	3	5558	23	Radiccio	\N
7554	723	3	5559	24	Karde	\N
7555	723	3	5560	25	Küchenkräuter auch Mischung	\N
7556	723	3	5561	26	Eisbergsalat	\N
7557	723	3	5562	27	Stielmus	\N
7558	723	3	5563	28	Zuckerhutsalat	\N
7559	723	3	5564	29	Schnittkohl	\N
7560	723	3	5565	30	Friseesalat	\N
7561	723	3	5566	31	Lauchzwiebel	\N
7562	723	3	5567	32	Brennessel	\N
7563	723	3	5568	33	Weinblätter	\N
7564	723	3	5569	34	Eichblattsalat	\N
7565	723	3	5570	35	Bataviasalat	\N
7566	723	3	5571	36	Dill	\N
7567	723	3	5572	37	Lollo rosso	\N
7568	723	3	5573	38	Lollo bianco	\N
7569	723	3	5574	39	Brunnen-/Wasserkresse	\N
7570	723	3	5575	40	Wurzelspinat	\N
7571	723	3	5576	41	Schnittsellerie	\N
7572	723	3	5577	42	Rucola	\N
7573	723	3	13311	43	Basilikum	\N
7574	723	3	13312	44	Bohnenkraut	\N
7575	723	3	13313	45	Estragon	\N
7576	723	3	13314	46	Kerbel	\N
7577	723	3	13315	47	Koriander	\N
7578	723	3	13316	48	Liebstöckel	\N
7579	723	3	13317	49	Minze	\N
7580	723	3	13318	50	Rosmarin	\N
7581	723	3	13319	51	Salbei	\N
7582	723	3	13320	52	Sauerampfer	\N
7583	723	3	13321	54	Bärlauch Bärenlauch	\N
7584	723	3	13322	55	Melisse/Zitronenmelisse	\N
7585	723	3	13323	56	Thymian	\N
7586	723	3	13678	57	Pak-Choi auch Pak-Choy	\N
7587	723	3	13799	58	Fenchelblätter	\N
7588	723	3	13821	59	Majoran	\N
7589	723	3	13822	60	Oregano, wilder Majoran, echter Dost	\N
7590	723	3	14120	61	Zitronengras Zitronellgras	\N
7591	724	3	5579	1	Broccoli	\N
7592	724	3	5580	2	Kohlrabi	\N
7593	724	3	5581	3	Blumenkohl	\N
7594	724	3	5582	4	Artischocke	\N
8119	792	3	6152	8	Johannisbeere weiß	\N
7595	724	3	5583	5	gestr. jetzt Spargel, weiß oder grün	\N
7596	724	3	5584	6	Knoblauch	\N
7597	724	3	5585	7	Schalotte	\N
7598	724	3	5586	8	Zwiebel	\N
7599	724	3	5587	9	Perlzwiebel	\N
7600	724	3	5588	10	Bambussprössling	\N
7601	724	3	5589	11	Palmherz	\N
7602	724	3	5590	12	Fenchel	\N
7603	724	3	5591	13	Sojakeimling	\N
7604	724	3	5592	14	Weizenkeimling	\N
7605	724	3	5593	15	Linsenkeimling	\N
7606	724	3	5594	16	Senfkeimling	\N
7607	724	3	5595	17	Luzernekeimling	\N
7608	724	3	5596	18	Sonnenblumenkeimling	\N
7609	724	3	5597	19	Mungobohnenkeimling	\N
7610	724	3	5598	20	Kressekeimling	\N
7611	724	3	5599	21	Rettichkeimling	\N
7612	724	3	5600	22	Romanesco	\N
7613	724	3	5601	23	Zucchiniblüte	\N
7614	724	3	5602	24	Cimarapa	\N
7615	724	3	14121	25	Spargel, weiß	\N
7616	724	3	14122	26	Spargel, grün	\N
7617	724	3	14151	27	Getreidekeimling	\N
7618	724	3	14152	28	Roggenkeimling	\N
7619	724	3	14153	29	Gerstenkeimling	\N
7620	724	3	14154	30	Maiskeimling	\N
7621	725	3	5604	1	Tomate	\N
7622	725	3	5605	2	Gemüsepaprika	\N
7623	725	3	5606	3	Pfefferschote/Peperoni	\N
7624	725	3	5607	4	Okraschote	\N
7625	725	3	5608	5	Gurke	\N
7626	725	3	5609	6	Kürbis	\N
7627	725	3	5610	7	Melone/Honigmelone	\N
7628	725	3	5611	8	Aubergine	\N
7629	725	3	5612	9	Zucchini	\N
7630	725	3	5613	10	Zuckermais	\N
7631	725	3	5614	11	Puffbohne mit Schote	\N
7632	725	3	5615	12	Bohne grüne	\N
7633	725	3	5616	13	Erbse mit Schote/Zuckerschote	\N
7634	725	3	5617	14	Erbse ohne Schote	\N
7635	725	3	5618	15	Olive	\N
7636	725	3	5619	16	Wachsbohne	\N
7637	725	3	5620	17	Puffbohne ohne Schote	\N
7638	725	3	5621	18	Chilischote	\N
7639	725	3	5622	19	Wassermelone	\N
7640	725	3	5623	20	Einlegegurke	\N
7641	725	3	5624	21	Netzmelone	\N
7642	725	3	5625	22	Kantalupmelone	\N
7643	725	3	14061	23	Bittere Spring-Gurke; Bittergurke; Balsambirne (Momordica charantia)	\N
7644	725	3	14198	24	Spargelbohne	\N
7645	726	3	5627	1	Mohrrübe; Karotte; Möhre	\N
7646	726	3	5628	2	Pariser Karotte	\N
7647	726	3	5629	3	Knollensellerie	\N
7648	726	3	5630	4	Meerrettich	\N
7649	726	3	5631	5	Rettich schwarz weiß und rot	\N
7650	726	3	5632	6	Radieschen	\N
7651	726	3	5633	7	Kohlrübe	\N
7652	726	3	5634	8	Schwarzwurzel	\N
7653	726	3	5635	9	Rote Bete	\N
7654	726	3	5636	10	Teltower Rübchen	\N
7655	726	3	5637	11	Pastinake	\N
7656	726	3	5638	12	Petersilienwurzel	\N
7657	726	3	5639	13	Zuckerrübe	\N
7658	726	3	5640	14	Rettich japanischer/asiatischer	\N
7659	726	3	14249	15	Ingwer	\N
7660	727	3	5642	1	Suppengrün	\N
7661	727	3	5643	2	Gemüsevormischung für Salat	\N
7662	727	3	5644	3	Gemüsevormischung zerkleinert zum Erhitzen	\N
7663	728	3	13324	1	Hizikia fusiforme	\N
7664	730	3	5648	1	Rosenkohl Konserve	\N
7665	730	3	5649	2	Rotkohl Konserve	\N
7666	730	3	5650	3	Grünkohl Konserve	\N
7667	730	3	5651	4	Wirsingkohl Konserve	\N
7668	730	3	5652	5	Spinat Konserve	\N
7669	730	3	5653	6	Weißkohl Konserve	\N
7670	730	3	5654	7	Bleichsellerie Konserve	\N
7671	730	3	5655	8	Porree Konserve	\N
7672	730	3	5656	9	Weinblätter Konserve	\N
7673	730	3	5657	10	Melde Konserve	\N
7674	730	3	5658	11	Mangold Konserve	\N
7675	731	3	5660	1	Rosenkohl tiefgefroren	\N
7676	731	3	5661	2	Rotkohl tiefgefroren	\N
7677	731	3	5662	3	Grünkohl tiefgefroren	\N
7678	731	3	5663	4	Spinat tiefgefroren	\N
7679	731	3	5664	5	Bleichsellerie tiefgefroren	\N
7680	731	3	5665	6	Petersilie tiefgefroren	\N
7681	731	3	5666	7	Schnittlauch tiefgefroren	\N
7682	731	3	5667	8	Porree tiefgefroren	\N
7683	731	3	5668	9	Küchenkräuter auch Mischung tiefgefroren	\N
7684	731	3	5669	10	Weißkohl tiefgefroren	\N
7685	731	3	5670	11	Wirsing tiefgefroren	\N
7686	731	3	5671	12	Chinakohl tiefgefroren	\N
7687	731	3	5672	13	Dill tiefgefroren	\N
7688	731	3	13325	14	Mangold tiefgefroren	\N
7689	732	3	5674	1	Weißkohl getrocknet	\N
7690	732	3	5675	2	Grünkohl getrocknet	\N
7691	732	3	5676	3	Porree getrocknet	\N
7692	732	3	5677	4	Rosenkohl getrocknet	\N
7693	732	3	5678	5	Rotkohl getrocknet	\N
7694	732	3	5680	7	Spinat getrocknet	\N
7695	732	3	5681	8	Wirsingkohl getrocknet	\N
7696	732	3	5685	12	Radiccio getrocknet	\N
7697	732	3	5686	14	Melde getrocknet	\N
7698	732	3	5687	15	Mangold getrocknet	\N
7699	732	3	5688	16	Kresse getrocknet	\N
7700	732	3	5689	17	Löwenzahn getrocknet	\N
7701	733	3	5691	1	Sauerkraut roh	\N
7702	733	3	5692	2	Sauerkraut Konserve	\N
7703	733	3	5693	3	Weinsauerkraut roh	\N
7704	733	3	5694	4	Weinsauerkraut Konserve	\N
7705	734	3	5696	1	Rosenkohl vor- und zubereitet	\N
7706	734	3	5697	2	Rotkohl vor- und zubereitet	\N
7707	734	3	5698	3	Bayrischkraut zubereitet	\N
7708	734	3	5699	4	Sauerkraut zubereitet	\N
7709	734	3	5700	5	Grünkohl vor- und zubereitet	\N
7710	734	3	5701	6	Spinat vor- und zubereitet	\N
7711	734	3	5702	7	Porree vor- und zubereitet	\N
7712	734	3	5703	8	Wirsingkohl vor- und zubereitet	\N
7713	734	3	5704	9	Chicoree vor- und zubereitet	\N
7714	734	3	5705	10	Löwenzahn vor- und zubereitet	\N
7715	734	3	5706	11	Bleichsellerie vor- und zubereitet	\N
7716	734	3	5707	12	Radiccio vor- und zubereitet	\N
7717	734	3	5708	13	Weißkohl vor- und zubereitet	\N
7718	734	3	5709	14	Weinblätter vor- und zubereitet in Lake	\N
7719	734	3	5710	15	Kopfsalat vor- und zubereitet	\N
7720	734	3	5711	99	Blattgemüsemischung vor- und zubereitet	\N
7721	735	3	5713	1	Kohlrabi Konserve	\N
7722	735	3	5714	2	Spargel Konserve	\N
7723	735	3	5715	3	Brechspargel Konserve	\N
7724	735	3	5716	4	Blumenkohl Konserve	\N
7725	735	3	5717	5	Artischockenherz Konserve	\N
7726	735	3	5718	6	Bambussprosse Konserve	\N
7727	735	3	5719	7	Palmherz Konserve	\N
7728	735	3	5720	8	Fenchel Konserve	\N
7729	735	3	5721	9	Broccoli Konserve	\N
7730	735	3	5722	10	Sojakeimling Konserve	\N
7731	735	3	5723	11	Zwiebel Konserve	\N
7732	735	3	5724	12	Spargelköpfe Konserve	\N
7733	735	3	5725	13	Knoblauch in Öl Konserve	\N
7734	735	3	5726	14	Knoblauchpaste Konserve	\N
7735	735	3	5727	15	Mungobohnenkeimling Konserve	\N
7736	735	3	5728	16	Keimgemüse sonstige Konserve	\N
7737	736	3	5730	1	Broccoli tiefgefroren	\N
7738	736	3	5731	2	Blumenkohl tiefgefroren	\N
7739	736	3	5732	3	Kohlrabi tiefgefroren	\N
7740	736	3	5733	4	Artischockenherz tiefgefroren	\N
7741	736	3	5734	5	Spargel tiefgefroren	\N
7742	736	3	5735	6	Fenchel tiefgefroren	\N
7743	736	3	5736	7	Bambussprosse tiefgefroren	\N
7744	736	3	5737	8	Sojakeimling tiefgefroren	\N
7745	736	3	5738	9	Zwiebel tiefgefroren	\N
7746	736	3	5739	10	Romanesco tiefgefroren	\N
7747	737	3	5741	1	Spargel getrocknet	\N
7748	737	3	5742	2	Blumenkohl getrocknet	\N
7749	737	3	5743	3	Zwiebel getrocknet	\N
7750	737	3	5744	4	Knoblauch getrocknet	\N
7751	737	3	5745	5	Kohlrabi getrocknet	\N
7752	738	3	5747	1	Zwiebel in Essig Konserve	\N
7753	738	3	5748	2	Perlzwiebel Konserve	\N
7754	738	3	5749	3	Knoblauch in Essig Konserve	\N
7755	739	3	5751	1	Blumenkohl vor- und zubereitet	\N
7756	739	3	5752	2	Kohlrabi vor- und zubereitet	\N
7757	739	3	5753	3	Broccoli vor- und zubereitet	\N
7758	739	3	5754	4	Artischockenherz vor- und zubereitet	\N
7759	739	3	5755	5	Spargel vor- und zubereitet	\N
7760	739	3	5756	6	Fenchel vor- und zubereitet	\N
7761	739	3	5757	7	Gemüsezwiebel vor- und zubereitet	\N
7762	739	3	5758	8	Sojakeimling vor- und zubereitet	\N
7763	739	3	5759	9	Palmherz vor- und zubereitet	\N
7764	739	3	5760	10	Keimgemüse sonstiges vor- und zubereitet	\N
7765	740	3	5762	1	Tomate Konserve	\N
7766	740	3	5763	2	Tomate geschält Konserve	\N
7767	740	3	5764	3	Tomatenmark 2fach konzentriert Konserve	\N
7768	740	3	5765	4	Tomatenmark 3fach konzentriert Konserve	\N
7769	740	3	5766	5	Gemüsepaprika Konserve	\N
7770	740	3	5767	6	Pfefferschote/Peperoni Konserve	\N
7771	740	3	5768	7	Mais Konserve	\N
7772	740	3	5769	8	Aubergine Konserve	\N
7773	740	3	5770	9	Erbse Konserve	\N
7774	740	3	5771	10	Grüne Bohne Konserve	\N
7775	740	3	5772	11	Wachsbohne Konserve	\N
7776	740	3	5773	12	Puffbohne Konserve	\N
7777	740	3	5774	13	Olive auch gefüllt Konserve	\N
7778	740	3	5775	14	Paprikamark Konserve	\N
7779	740	3	5776	15	Pfefferschoten-/Peperonimark Konserve	\N
7780	740	3	5777	16	Okraschote Konserve	\N
7781	740	3	5778	17	Tomatenmark 1fach konzentriert Konserve	\N
7782	740	3	5780	19	Melone Konserve	\N
7783	740	3	5781	20	Tomate gestückelt Konserve	\N
7784	740	3	5782	21	Tomate passiert Konserve	\N
7785	741	3	5784	1	Tomate tiefgefroren	\N
7786	741	3	5785	2	Gemüsepaprika tiefgefroren	\N
7787	741	3	5786	3	Aubergine tiefgefroren	\N
7788	741	3	5787	4	Mais tiefgefroren	\N
7789	741	3	5788	5	Erbse tiefgefroren	\N
7790	741	3	5789	6	Erbse mit Schote tiefgefroren	\N
7791	741	3	5790	7	Grüne Bohne tiefgefroren	\N
7792	741	3	5791	9	Puffbohne tiefgefroren	\N
7793	741	3	5792	10	Gurke tiefgefroren	\N
7794	742	3	5794	1	Tomate getrocknet	\N
7795	742	3	14062	2	Paprika getrocknet	\N
7796	743	3	5796	1	Gewürzgurke Konserve	\N
7797	743	3	5797	2	Senfgurke Konserve	\N
7798	743	3	5798	3	Zuckergurke Konserve	\N
7799	743	3	5799	4	Salzgurke Konserve	\N
7800	743	3	5800	5	Mais in Essig Konserve	\N
7801	743	3	5801	6	Kürbis in Essig Konserve	\N
7802	743	3	5802	7	Paprika in Essig Konserve	\N
7803	743	3	5803	8	Pfefferschote/Peperoni in Essig Konserve	\N
7804	743	3	5804	9	Olive auch gefüllt gesäuert Konserve	\N
7805	743	3	5805	10	Grüne Bohne gesäuert Konserve	\N
7806	744	3	5807	1	Gemüsepaprika vor- und zubereitet	\N
7807	744	3	5808	2	Aubergine vor- und zubereitet	\N
7808	744	3	5809	3	Zucchini vor- und zubereitet	\N
7809	744	3	5810	4	Zuckermais vor- und zubereitet	\N
7810	744	3	5811	5	Grüne Bohne vor- und zubereitet	\N
7811	744	3	5812	6	Wachsbohne vor- und zubereitet	\N
7812	744	3	5813	7	Puffbohne vor- und zubereitet	\N
7813	744	3	5814	8	Erbse vor- und zubereitet	\N
7814	744	3	5815	9	Erbse mit Schote vor- und zubereitet	\N
7815	744	3	5816	10	Tomate vor- und zubereitet	\N
7816	744	3	5817	11	Gurke vor- und zubereitet	\N
7817	744	3	5818	12	Olive vor- und zubereitet	\N
7818	744	3	5819	13	Pfefferschote/Peperoni vor- und zubereitet	\N
7819	744	3	13326	14	Auberginen Paste	\N
7820	744	3	5820	99	Fruchtgemüsemischung vor- und zubereitet	\N
7821	745	3	5822	1	Knollensellerie Konserve	\N
7822	745	3	5823	2	Pariser Karotte Konserve	\N
7823	745	3	5824	3	Mohrrübe Konserve	\N
7824	745	3	5825	4	Schwarzwurzel Konserve	\N
7825	745	3	5826	5	Rote Bete Konserve	\N
7826	745	3	5827	6	Pastinaken Konserve	\N
7827	745	3	5828	7	Meerrettich Konserve	\N
7828	745	3	5829	8	Rettich Konserve	\N
7829	745	3	5830	9	Wasserkastanie Konserve	\N
7830	745	3	14250	10	Ingwer Konserve	\N
7831	746	3	5832	1	Mohrrübe tiefgefroren	\N
7832	746	3	5833	2	Pariser Karotte tiefgefroren	\N
7833	746	3	5834	3	Schwarzwurzel tiefgefroren	\N
7834	746	3	5835	4	Kohlrübe tiefgefroren	\N
7835	746	3	5836	5	Pastinake tiefgefroren	\N
7836	746	3	5837	6	Meerrettich tiefgefroren	\N
7837	746	3	5838	7	Knollensellerie tiefgefroren	\N
7838	747	3	5840	1	Mohrrübe getrocknet	\N
7839	747	3	5841	2	Meerrettich getrocknet	\N
7840	747	3	5842	3	Kohlrübe getrocknet	\N
7841	747	3	5843	4	Pastinake getrocknet	\N
7842	747	3	5844	5	Knollensellerie getrocknet	\N
7843	747	3	5845	6	Pariser Karotte getrocknet	\N
7844	748	3	5847	1	Rote Bete gesäuert	\N
7845	748	3	5848	2	Meerrettich gesäuert	\N
7846	748	3	5849	3	Mohrrübe gesäuert	\N
7847	748	3	5850	4	Knollensellerie gesäuert	\N
7848	748	3	5851	5	Pariser Karotte gesäuert	\N
7849	748	3	14251	6	Ingwer gesäuert	\N
7850	749	3	5853	1	Mohrrübe vor- und zubereitet	\N
7851	749	3	5854	2	Pariser Karotte vor- und zubereitet	\N
7852	749	3	5855	3	Schwarzwurzel vor- und zubereitet	\N
7853	749	3	5856	4	Meerrettich vor- und zubereitet	\N
7854	749	3	5857	5	Knollensellerie vor- und zubereitet	\N
7855	749	3	5858	6	Teltower Rübchen vor- und zubereitet	\N
7856	749	3	5859	7	Rote Bete vor- und zubereitet	\N
7857	750	3	5861	1	Leipziger Allerlei Konserve	\N
7858	750	3	5862	2	Ratatouille Konserve	\N
7859	750	3	5863	3	Erbse mit Karotte Konserve	\N
7860	750	3	5864	4	Gemüsemischung exotisch Konserve	\N
7861	750	3	5865	5	Gemüsemischung mit Pilzen Konserve	\N
7862	751	3	5867	1	Leipziger Allerlei tiefgefroren	\N
7863	751	3	5868	2	Balkangemüse tiefgefroren	\N
7864	751	3	5869	3	Suppengemüse tiefgefroren	\N
7865	751	3	5870	4	Mexikanische Gemüsemischung tiefgefroren	\N
7866	751	3	5871	5	Chinesische Gemüsemischung tiefgefroren	\N
7867	751	3	5872	6	Erbsen Spargelmischung tiefgefroren	\N
7868	751	3	5873	7	Erbsen mit Karotten tiefgefroren	\N
7869	753	3	5876	1	Mixed pickles	\N
7870	753	3	5877	2	Chinesische Gemüsemischung gesäuert	\N
7871	753	3	5878	3	Gemüse gesäuert u./o gesalzen in Öl	\N
7872	754	3	5880	1	Mischgemüse vor- und zubereitet	\N
7873	754	3	5881	2	Leipziger Allerlei vor- und zubereitet	\N
7874	754	3	5882	3	Erbsen mit Karotten vor- und zubereitet	\N
7875	754	3	5883	4	Gemüsemischung in Aspik	\N
7876	755	3	5885	1	Tomatensaft	\N
7877	755	3	5886	2	Möhren/Karottensaft	\N
7878	755	3	5887	3	Rote Betesaft	\N
7879	755	3	5888	4	Sauerkrautsaft	\N
7880	755	3	5889	5	Paprikasaft	\N
7881	755	3	5890	6	Selleriesaft	\N
7882	755	3	5891	7	Weißkohlsaft	\N
7883	755	3	5892	8	Gemüsesaft milchsauer vergoren	\N
7884	755	3	5893	99	Gemüsesaftmischung auch mit Fruchterzeugnissen	\N
7885	756	3	5895	1	Tomatentrunk	\N
7886	756	3	5896	2	Möhren/Karottentrunk	\N
7887	756	3	5897	3	Rote Betetrunk	\N
7888	756	3	5898	4	Spargeltrunk	\N
7889	756	3	5899	5	Sellerietrunk	\N
7890	756	3	5900	6	Spinattrunk	\N
7891	756	3	5901	7	Paprikatrunk	\N
7892	756	3	5902	8	Gemüsetrunk milchsauer vergoren	\N
7893	756	3	5903	99	Gemüsetrunkmischung auch mit Fruchterzeugnissen	\N
7894	757	3	5905	1	Zwiebelpulver	\N
7895	757	3	5906	2	Knoblauchpulver	\N
7896	757	3	5907	3	Selleriepulver	\N
7897	757	3	5908	4	Spargelpulver	\N
7898	757	3	5909	99	Gemüsemischung getrocknet auch pulverisiert	\N
7899	759	3	14063	1	Rotalge Nori Seegras (Porphyra ssp.) getrocknet	\N
7900	759	3	14064	2	Grünalge Aonori (Monostroma spp. und Enteromorpha spp.) getrocknet	\N
7901	759	3	14065	3	Braunalge Kombu Haidi Seekohl (Laminaria japonica und Laminaria ssp.) getrocknet	\N
7902	759	3	14066	4	Braunalge Wakame (Undaria pinnatifida) getrocknet	\N
7903	759	3	14067	5	Braunalge Hiziki Hijki (Hizikia fusiforme) getrocknet	\N
7904	759	3	14068	6	Rotalge Dulse (Palmaria palmate) getrocknet	\N
7905	759	3	14069	7	Braunalge Sarumen (Alaria esculenta) getrocknet	\N
7906	759	3	14070	8	Braunalge Arame (Eisenia bicyclis) getrocknet	\N
7907	759	3	14071	9	Braunalge Meeresspaghetti (Himanthalia elongata) getrocknet	\N
7908	759	3	14072	10	Braunalge (Sargassum spp.) getrocknet	\N
7909	759	3	14073	11	Grünalge Meersalat (Ulva lactuca) getrocknet	\N
7910	759	3	14074	99	Algen gemischt getrocknet	\N
7911	762	3	5915	1	Zuchtchampignon (Agaricus bisporus)	\N
7912	762	3	5916	2	Riesenträuschling Zuchtpilz (Stropharia rugosoannulata)	\N
7913	762	3	5917	3	Austernseitling (Pleurotus ostreatus)	\N
7914	762	3	5918	4	Shiitakepilz (Lentinus edodes)	\N
7915	762	3	5919	5	Matsutake (Tricholoma matsutake)	\N
7916	762	3	5920	6	Samtfußrübling (Flammulina velutipes)	\N
7917	762	3	14087	7	Limonenseitling (Pleurotus cornucopiae var. citrinopileatus)	\N
7918	762	3	14088	8	Kräuterseitling (Pleurotus eryngii)	\N
7919	762	3	14290	9	Buchenpilz, Buchenrasling, Shimeji (Hypsizygus tessulatus)	\N
7920	763	3	5922	1	Maipilz (Calocybe gambosa)	\N
7921	763	3	5923	2	Frauentäubling (Russula cyanoxantha)	\N
7922	763	3	5924	3	Speisetäubling (Russula vesca)	\N
7923	763	3	5925	4	Goldtäubling (Russula aurata)	\N
8120	792	3	6153	9	Stachelbeere	\N
7924	763	3	5926	5	Apfeltäubling (Russula paludosa)	\N
7925	763	3	5927	6	Edel-Reizker (Lactarius deliciosus)	\N
7926	763	3	5928	7	Grünling (Tricholoma flavovirens t.equestre)	\N
7927	763	3	5929	8	Hallimasch (Armillariella mellea)	\N
7928	763	3	5930	9	Grünspan-Träuschling (Stropharia äruginosa)	\N
7929	763	3	5931	10	Parasolpilz (Macrolepiota procera)	\N
7930	763	3	5932	11	Waldchampignon (Agaricus silvaticus)	\N
7931	763	3	5933	12	Wiesenchampignon (Agaricus campestris)	\N
7932	763	3	5934	13	Schopftintling (Coprinus comatus)	\N
7933	763	3	5935	14	Ritterling grauer (Tricholoma portentosum)	\N
7934	763	3	5936	15	Ritterling violetter (Lepista nuda)	\N
7935	763	3	5937	16	Stockschwämmchen (Kühneromycetes mutabilis)	\N
7936	763	3	5938	17	Anischampignon (Agaricus arvensis)	\N
7937	763	3	5939	18	Nebelgrauer Trichterling (Clitocybe nebularis)	\N
7938	763	3	5940	19	Nelkenschwindling (Marasmius oreades)	\N
7939	763	3	5941	20	Perlpilz (Amanita rubescens)	\N
7940	763	3	5942	21	Reifpilz (Rozites caperata)	\N
7941	763	3	5943	23	Rauchbl. Schwefelkopf (Hypholoma capnoides)	\N
7942	763	3	5944	24	Mehlpilz (Clitopilus prunulus)	\N
7943	763	3	5945	25	Brätling(Lactarius volemus)	\N
7944	763	3	5946	26	Fichtenreizker (Lactarius deterrimus)	\N
7945	763	3	5947	27	Tannenreizker (Lactarius necator)	\N
7946	763	3	5948	28	Blauer Lacktrichterling (Laccaria amethystina)	\N
7947	763	3	5949	29	Scheidenstreifling (Amanita vaginata)	\N
7948	763	3	5950	30	Schneckling Natterstielig (Hygrophorus olivaceoalbus)	\N
7949	763	3	5951	31	Pfeffermilchling (Lactarius piperatus)	\N
7950	763	3	5952	33	Mohrenkopf (Lactarius lignyotus)	\N
7951	763	3	5953	34	Gedrungener Wulstling (Amanita spissa)	\N
7952	763	3	5954	35	Glimmerschüppling (Phäolepiota aurea)	\N
7953	763	3	5955	36	Weißer Krempentrichterling (Leucopaxillus candidus)	\N
7954	763	3	5956	37	Ockertäubling (Russula ochroleuca)	\N
7955	763	3	5957	38	Weißer Rasling (Lyophyllum connatum)	\N
7956	763	3	5958	39	Grünfelderiger Täubling     (Russula virescens)	\N
7957	763	3	5959	40	Riesenträuschling (Stropharia rugosoannulata)	\N
7958	763	3	5960	41	Butterrübling (Collybia butyracea)	\N
7959	763	3	5961	42	Chinesisches Stockschwämmchen (Pholiota namenko)	\N
7960	763	3	5962	43	Erdritterling (Tricholoma terreum)	\N
7961	763	3	5963	44	Kaiserling (Amanita cäsarea)	\N
7962	763	3	5964	45	Reisstrohpilz (Volvariella volvacea)	\N
7963	763	3	5965	46	Safranpilz (Macrolepiota rhacodes)	\N
7964	763	3	5966	47	Südlicher Schüppling (Agrocybe aegerita)	\N
7965	763	3	5967	48	Heide-Schleimfuß (Cortinarius mucosus)	\N
7966	763	3	5968	49	Rötlicher Holzritterling (Tricholomopsis rutilans)	\N
7967	763	3	5969	50	Orangeroter Graustieltäubling (Russula decolorans)	\N
7968	763	3	5970	51	Rötlicher Lacktrichterling (Laccaria laccata)	\N
7969	763	3	5971	52	Waldfreundrübling (Collybia dryophila)	\N
7970	763	3	5972	53	Rotbrauner Milchling (Lactarius rufus)	\N
7971	763	3	13996	54	Grüner Birkentäubling Grasgrüner Täubling (Russula aeruginea)	\N
7972	763	3	13997	55	Frostschneckling (Hygrophorus hypothejus)	\N
7973	763	3	14291	56	Fuchsiger Rötelritterling (Lepista flaccida)	\N
7974	764	3	5974	1	Butterpilz (Suillus luteus)	\N
7975	764	3	5975	2	Lärchenröhrling (Suillus äruginascens)	\N
7976	764	3	5976	3	Elfenbeinröhrling (Suillus placidus)	\N
7977	764	3	5977	4	Goldröhrling (Suillus grevillei)	\N
7978	764	3	5978	5	Ziegenlippe (Xerocomus subtomentosus)	\N
7979	764	3	5979	6	Maronenröhrling (Xerocomus badius)	\N
7980	764	3	5980	7	Steinpilz (Boletus edulis b.ärens b.pinicola b.ästivalis)	\N
7981	764	3	5981	8	Birken-Rotkappe (Leccinum testaceoscabrum)	\N
7982	764	3	5982	9	Birkenpilz (Leccinum scabrum)	\N
7983	764	3	5983	10	Sandröhrling (Suillus variegatus)	\N
7984	764	3	5984	11	Rotfußröhrling (Xerocomus chrysenteron)	\N
7985	764	3	5985	12	Netzstieliger Hexenröhrling (Boletus luridus)	\N
7986	764	3	5986	13	Flockenstieliger Hexenröhrling (Boletus erytropus)	\N
7987	764	3	5987	15	Schwarzblauender Röhrling (Boletus pulverulentus)	\N
7988	764	3	5988	16	Kuhröhrling (Suillus bovinus)	\N
7989	764	3	5989	17	Körnchenröhrling (Suillus granulatus)	\N
7990	764	3	13998	18	Eichen-Rotkappe (Leccinum quercinum)	\N
7991	765	3	5991	1	Habichtspilz (Sarcodon imbricatum)	\N
7992	765	3	5993	3	Semmel-Stoppelpilz (Hydnum repandum)	\N
7993	766	3	5995	1	Pfifferling (Cantharellus cibarius)	\N
7994	766	3	5996	2	Trompetenpfifferling (Cantharellus tubäformis)	\N
7995	766	3	5997	3	Herbsttrompete (Cantharellus cornucopioides)	\N
7996	767	3	5999	1	Hahnenkamm (Ramaria botrytis)	\N
7997	767	3	6000	2	Herkulesskeule (Clavaria fistulosa)	\N
7998	767	3	6001	3	Eiskoralle (Hericum coralloides)	\N
7999	767	3	6002	4	Krause Glucke (Sparassis crispa)	\N
8000	767	3	6003	5	Igel-Stachelbart (Hericum erinaceus)	\N
8001	768	3	6005	1	Riesenbovist (Lycoperdon giganteum)	\N
8002	768	3	6006	2	Flaschenstäubling (Lycoperdon perlatum)	\N
8003	769	3	6008	1	Trüffel Sommer- (Tuber ästivum)	\N
8004	769	3	6009	2	Trüffel Winter- (Tuber brumale)	\N
8005	769	3	6010	3	Speisemorchel (Morchella esculenta)	\N
8006	769	3	6011	4	Orangebecherling (Aleuria aurantia)	\N
8007	769	3	6012	5	Burgundertrüffel (Tuber unicatum)	\N
8008	769	3	6013	6	Kalahari-Trüffel (Terfezia pfeilii)	\N
8009	769	3	6014	7	Löwentrüffel (Terfezia leonis)	\N
8010	769	3	6015	8	Perigordtrüffel (Tuber melanosporum)	\N
8011	769	3	6016	9	Piemont-Trüffel (Tuber magnatum)	\N
8012	769	3	6017	10	Weiße Trüffel (Choriomyces mäandriformis)	\N
8013	769	3	6018	11	Spitzmorchel (Morchella conica)	\N
8014	769	3	6019	12	Hohe Morchel (Morchella elata)	\N
8015	769	3	13659	13	Trüffel schwarz / chinesisch (Tuber indicum)	\N
8016	771	3	6022	1	Frühlingslorchel (Gyromitra esculenta)	\N
8017	771	3	6023	2	Kahler Krempling (Paxillus involutus)	\N
8018	771	3	6024	3	Samtfuß-Krempling (Paxillus atrotomentosus)	\N
8019	772	3	6026	1	Schafeuter (Albatrellus ovinus)	\N
8020	772	3	6027	2	Semmelporling (Albatrellus conflüns)	\N
8021	772	3	6028	3	Leberreischling (Fistulina hepatica)	\N
8022	772	3	6029	4	Judasohr (Hirneola auricula-judea)	\N
8023	772	3	6030	5	Schwefelporling (Lätiporus sulphureus)	\N
8024	772	3	6031	6	Mu-err-Pilz (Auricularia polytricha)	\N
8025	772	3	6032	7	Silberohr (Tremella fuciformis)	\N
8026	773	3	6040	1	Champignon Konserve	\N
8027	773	3	6041	2	Reisstrohscheidling Konserve	\N
8028	773	3	6042	3	Ostasiatisches Stockschwämmchen Konserve	\N
8029	773	3	6043	4	Austernseitling Konserve	\N
8030	773	3	6044	5	Shiitakepilz Konserve	\N
8031	773	3	6045	99	Kulturpilzmischung Konserve	\N
8032	774	3	6047	1	Champignon tiefgefroren	\N
8033	774	3	13327	2	Kulturpilzmischung tiefgefroren	\N
8034	775	3	6049	1	Champignon getrocknet	\N
8035	775	3	6050	2	Champignon pulverisiert	\N
8036	775	3	6051	3	Shiitakepilz getrocknet	\N
8037	775	3	6052	99	Kulturpilzmischung getrocknet	\N
8038	776	3	6054	1	Champignon in Essig	\N
8039	777	3	6056	1	Champignon vor- und zubereitet	\N
8040	778	3	6059	1	Steinpilz Konserve	\N
8041	778	3	6060	2	Pfifferling Konserve	\N
8042	778	3	6061	3	Trüffel Konserve	\N
8043	778	3	6062	4	Morchel Konserve	\N
8044	778	3	6063	5	Wiesenchampignon Konserve	\N
8045	778	3	6064	6	Apfeltäubling Konserve	\N
8046	778	3	6065	7	Birkenpilz Konserve	\N
8047	778	3	6066	8	Maronenpilz Konserve	\N
8048	778	3	6067	9	Rotkappen Konserve	\N
8049	778	3	6068	10	Butterpilz Konserve	\N
8050	778	3	6069	11	Hallimasch Konserve	\N
8051	778	3	6070	12	Goldröhrling Konserve	\N
8052	778	3	6071	13	Stockschwämmchen Konserve	\N
8053	778	3	6072	99	Wildpilzmischung Konserve	\N
8054	779	3	6074	1	Steinpilz tiefgefroren	\N
8055	779	3	6075	2	Butterpilz tiefgefroren	\N
8056	779	3	6076	3	Birkenpilz tiefgefroren	\N
8057	779	3	6077	4	Maronenpilz tiefgefroren	\N
8058	779	3	6078	5	Wiesenchampignon tiefgefroren	\N
8059	779	3	6079	6	Pfifferling tiefgefroren	\N
8060	779	3	6080	99	Wildpilzmischung tiefgefroren	\N
8061	780	3	6082	1	Maipilz getrocknet/pulverisiert	\N
8062	780	3	6083	2	Frauentäubling getrocknet/pulverisiert	\N
8063	780	3	6084	3	Speisetäubling getrocknet/pulverisiert	\N
8064	780	3	6085	4	Goldtäubling getrocknet/pulverisiert	\N
8065	780	3	6086	5	Apfeltäubling getrocknet/pulverisiert	\N
8066	780	3	6087	6	Edel-Reizker getrocknet/pulverisiert	\N
8067	780	3	6088	7	Grünling getrocknet/pulverisiert	\N
8068	780	3	6089	8	Hallimasch getrocknet/pulverisiert	\N
8069	780	3	6090	9	Grünspan-Träuschling getrocknet/pulverisiert	\N
8070	780	3	6091	10	Parasolpilz getrocknet/pulverisiert	\N
8071	780	3	6092	11	Waldchampignon getrocknet/pulverisiert	\N
8072	780	3	6093	12	Wiesenchampignon getrocknet/pulverisiert	\N
8073	780	3	6094	13	Schopf-Tintling getrocknet/pulverisiert	\N
8074	780	3	6095	14	Butterpilz getrocknet/pulverisiert	\N
8075	780	3	6096	15	Röhrling getrocknet/pulverisiert	\N
8076	780	3	6097	16	Ziegenlippe getrocknet/pulverisiert	\N
8077	780	3	6098	17	Maronenpilz getrocknet/pulverisiert	\N
8078	780	3	6099	18	Steinpilz getrocknet/pulverisiert	\N
8079	780	3	6100	19	Rotkappe getrocknet/pulverisiert	\N
8080	780	3	6101	20	Birkenpilz getrocknet/pulverisiert	\N
8081	780	3	6102	21	Habichtspilz getrocknet/pulverisiert	\N
8082	780	3	6103	22	Eiskoralle getrocknet/pulverisiert	\N
8083	780	3	6104	23	Pfifferling getrocknet/pulverisiert	\N
8084	780	3	6105	24	Trompetenpfifferling getrocknet/pulverisiert	\N
8085	780	3	6106	25	Herbsttrompete getrocknet/pulverisiert	\N
8086	780	3	6107	26	Hahnenkamm getrocknet/pulverisiert	\N
8087	780	3	6108	27	Herkuleskeule getrocknet/pulverisiert	\N
8088	780	3	6109	28	Speisemorchel getrocknet/pulverisiert	\N
8089	780	3	6110	29	Trüffel getrocknet/pulverisiert	\N
8090	780	3	6111	30	Orangebecherling getrocknet/pulverisiert	\N
8091	780	3	6112	31	Samtfußrübling getrocknet/pulverisiert	\N
8092	780	3	6113	32	Körnchenröhrling getrocknet/pulverisiert	\N
8093	780	3	6114	33	Igel-Stachelbart getrocknet/pulverisiert	\N
8094	780	3	6115	34	Spitzmorchel getrocknet/pulverisiert	\N
8095	780	3	6116	35	Hohe Morchel getrocknet/pulverisiert	\N
8096	780	3	6117	36	Silberohr getrocknet/pulverisiert	\N
8097	780	3	6118	37	Mu Err-Pilz getrocknet/pulverisiert	\N
8098	780	3	6119	99	Wildpilzmischung getrocknet/pulverisiert	\N
8099	781	3	6121	1	Steinpilz in Essig	\N
8100	781	3	6122	2	Butterpilz in Essig	\N
8101	781	3	6123	3	Pfifferling in Essig	\N
8102	781	3	6124	4	Trüffel in Essig	\N
8103	781	3	6125	99	Wildpilzmischung in Essig	\N
8104	782	3	6127	1	Wiesenchampignon vor- und zubereitet	\N
8105	782	3	6128	2	Waldchampignon vor- und zubereitet	\N
8106	782	3	6129	3	Pfifferling vor- und zubereitet	\N
8107	782	3	6130	4	Steinpilz vor- und zubereitet	\N
8108	782	3	6131	99	Wildpilzmischung vor- und zubereitet	\N
8109	783	3	6133	1	Pilzkonzentrat	\N
8110	783	3	6134	2	Pilztrockenkonzentrat	\N
8111	783	3	6135	3	Pilzextrakt	\N
8112	792	3	6145	1	Hagebutte	\N
8113	792	3	6146	2	Erdbeere	\N
8114	792	3	6147	3	Himbeere	\N
8115	792	3	6148	4	Brombeere	\N
8116	792	3	6149	5	Sanddornbeere	\N
8117	792	3	6150	6	Johannisbeere rot	\N
8118	792	3	6151	7	Johannisbeere schwarz	\N
8121	792	3	6154	10	Tafelweintraube rot	\N
8122	792	3	6155	11	Tafelweintraube weiß	\N
8123	792	3	6156	12	Preiselbeere	\N
8124	792	3	6157	13	Ebereschenbeere	\N
8125	792	3	6158	14	Moosbeere	\N
8126	792	3	6159	15	Heidelbeere; Blaubeere	\N
8127	792	3	6160	16	Holunderbeere	\N
8128	792	3	6161	17	Boysenbeere	\N
8129	792	3	6162	18	Loganbeere	\N
8130	792	3	6163	19	Arbutus	\N
8131	792	3	6164	20	Maulbeere	\N
8132	792	3	6165	21	Jostabeere	\N
8133	792	3	13823	22	Weißdornbeere	\N
8134	792	3	14234	23	Gojibeere	\N
8135	793	3	6167	1	Apfel	\N
8136	793	3	6168	2	Birne	\N
8137	793	3	6169	3	Quitte	\N
8138	793	3	6170	4	Mispel	\N
8139	794	3	6172	1	Reneclaude	\N
8140	794	3	6173	2	Mirabelle	\N
8141	794	3	6174	3	Pfirsich	\N
8142	794	3	6175	4	Aprikose	\N
8143	794	3	6176	5	Pflaume	\N
8144	794	3	6177	6	Nektarine	\N
8145	794	3	6178	7	Süßkirsche	\N
8146	794	3	6179	8	Sauerkirsche	\N
8147	794	3	6180	9	Avocado	\N
8148	794	3	6181	10	Schlehe	\N
8149	795	3	6183	1	Orange	\N
8150	795	3	6184	2	Mandarine	\N
8151	795	3	6185	3	Clementine	\N
8152	795	3	6186	4	Zitrone	\N
8153	795	3	6187	5	Grapefruit	\N
8154	795	3	6188	6	Bergamotte	\N
8155	795	3	6189	7	Pomeranze	\N
8156	795	3	6190	8	Satsumas	\N
8157	795	3	6191	9	Tangerine	\N
8158	795	3	6192	10	Limette	\N
8159	795	3	6193	11	Kumquat	\N
8160	795	3	6194	12	Pomelo	\N
8161	795	3	6195	13	Mineola	\N
8162	795	3	6196	14	Sweetie	\N
8163	795	3	6197	15	Ugli	\N
8164	796	3	6199	1	Ananas	\N
8165	796	3	6200	2	Banane	\N
8166	796	3	6201	3	Feige	\N
8167	796	3	6202	4	Maracuja Passionsfrucht	\N
8168	796	3	6203	5	Kakifrucht; Sharon	\N
8169	796	3	6204	6	Papaya	\N
8170	796	3	6205	7	Mangostanapfel	\N
8171	796	3	6206	8	Dattel	\N
8172	796	3	6207	9	Mango	\N
8173	796	3	6208	10	Granatapfel	\N
8174	796	3	6209	11	Guave	\N
8175	796	3	6210	12	Litchi	\N
8176	796	3	6211	13	Kiwi	\N
8177	796	3	6212	14	Rhabarber	\N
8178	796	3	6213	15	Affenbrotfrucht	\N
8179	796	3	6214	16	Johannisbrot	\N
8180	796	3	6215	17	Cashewapfel	\N
8181	796	3	6216	18	Kaktusfeige	\N
8182	796	3	6217	19	Temple	\N
8183	796	3	6218	20	Susine	\N
8184	796	3	6219	21	Tamarillo	\N
8185	796	3	6220	22	Kapstachelbeere Physalis	\N
8186	796	3	6221	23	Pitahaya	\N
8187	796	3	6222	24	Longan	\N
8188	796	3	6223	25	Rambutan	\N
8189	796	3	6224	26	Cherimoya	\N
8190	796	3	6225	27	Naranjila	\N
8191	796	3	6226	28	Lotuspflaume	\N
8192	796	3	6227	29	Brotfrucht	\N
8193	796	3	6228	30	Jackfrucht	\N
8194	796	3	6229	31	Durian	\N
8195	796	3	6230	32	Karambole Sternfrucht Baumstachelbeere	\N
8196	796	3	6231	33	gestr. jetzt 290505, Sharon	\N
8197	796	3	6233	36	Guanabanas	\N
8198	796	3	6234	37	Jabotika	\N
8199	796	3	6235	38	Jambolan	\N
8200	796	3	6236	39	Mammey-Äpfel	\N
8201	796	3	6237	40	Pomerac	\N
8202	796	3	6238	41	Apfeljambuse Rosen-Apfel	\N
8203	796	3	6239	42	Sapodilla	\N
8204	796	3	13328	43	Nashi Birne Orientalische Birne (Pyrus Pyrofolia)	\N
8205	798	3	6243	1	Preiselbeere Konserve	\N
8206	798	3	6244	2	Stachelbeere Konserve	\N
8207	798	3	6245	3	Erdbeere Konserve	\N
8208	798	3	6246	4	Heidelbeere Konserve	\N
8209	798	3	6247	5	Himbeere Konserve	\N
8210	798	3	6248	6	Brombeere Konserve	\N
8211	798	3	6249	7	Johannisbeere rot Konserve	\N
8212	798	3	6250	9	Johannisbeere schwarz Konserve	\N
8213	798	3	6251	10	Beerendunstobst Konserve	\N
8214	798	3	6252	11	Boysenbeere Konserve	\N
8215	798	3	6253	12	Johannisbeere weiß Konserve	\N
8216	798	3	6254	13	Moosbeere Konserve	\N
8217	798	3	6255	14	Arbutus Konserve	\N
8218	799	3	6257	1	Erdbeere tiefgefroren	\N
8219	799	3	6258	2	Himbeere tiefgefroren	\N
8220	799	3	6259	3	Heidelbeere tiefgefroren	\N
8221	799	3	6260	4	Preiselbeere tiefgefroren	\N
8222	799	3	6261	5	Brombeere tiefgefroren	\N
8223	799	3	6262	6	Johannisbeere rot tiefgefroren	\N
8224	799	3	6263	7	Johannisbeere schwarz tiefgefroren	\N
8225	799	3	6264	8	Stachelbeere tiefgefroren	\N
8226	799	3	6265	9	Johannisbeere weiß tiefgefroren	\N
8227	800	3	6267	1	Heidelbeere getrocknet	\N
8228	800	3	6268	2	Korinthe	\N
8229	800	3	6269	3	Sultanine	\N
8230	800	3	6270	4	Rosine	\N
8231	800	3	6271	5	Himbeere getrocknet	\N
8232	800	3	6272	6	Brombeere getrocknet	\N
8233	800	3	6273	7	Erdbeere getrocknet	\N
8234	800	3	6274	8	Preiselbeere getrocknet	\N
8235	800	3	6275	9	Johannisbeere rot getrocknet	\N
8236	800	3	6276	10	Johannisbeere schwarz getrocknet	\N
8237	800	3	6277	11	Johannisbeere weiß getrocknet	\N
8238	800	3	6278	12	Sanddornbeere getrocknet	\N
8239	800	3	14189	13	Moosbeere getrocknet	\N
8240	800	3	14190	14	Apfelbeere (Aronia) getrocknet	\N
8241	800	3	14191	15	Maulbeere getrocknet	\N
8242	800	3	14235	16	Gojibeere getrocknet	\N
8243	801	3	6280	1	Erdbeere gezuckert	\N
8244	801	3	6281	2	Erdbeere gezuckert mit Sahne	\N
8245	802	3	6283	1	Himbeere in Alkohol	\N
8246	802	3	6284	2	Brombeere in Alkohol	\N
8247	802	3	6285	3	Rosine in Alkohol	\N
8248	804	3	6288	1	Himbeere in Sirup	\N
8249	805	3	6290	1	Apfelstück Konserve	\N
8250	805	3	6291	2	Apfelmus Konserve	\N
8251	805	3	6292	3	Birne Konserve	\N
8252	805	3	6293	4	Quitte Konserve	\N
8253	805	3	6294	5	Kerndunstobst Konserve	\N
8254	806	3	6296	1	Quitte tiefgefroren	\N
8255	806	3	6297	2	Apfel tiefgefroren	\N
8256	806	3	6298	3	Birne tiefgefroren	\N
8257	807	3	6300	1	Apfelstück getrocknet	\N
8258	807	3	6301	2	Birnenstück getrocknet	\N
8259	807	3	6302	3	Quitte getrocknet	\N
8260	808	3	6304	1	Bratapfel	\N
8261	808	3	6305	2	Apfelkompott	\N
8262	808	3	6306	3	Apfelstück vor- und zubereitet	\N
8263	809	3	6308	1	Apfel in Alkohol	\N
8264	809	3	6309	2	Birne in Alkohol	\N
8265	809	3	6310	3	Quitte in Alkohol	\N
8266	810	3	6312	1	Apfel in Essig	\N
8267	810	3	6313	2	Birne in Essig	\N
8268	810	3	6314	3	Quitte in Essig	\N
8269	811	3	6316	1	Apfel in Sirup	\N
8270	811	3	6317	2	Birne in Sirup	\N
8271	811	3	6318	3	Quitte in Sirup	\N
8272	812	3	6321	1	Reneclaude Konserve	\N
8273	812	3	6322	2	Mirabelle Konserve	\N
8274	812	3	6323	3	Pfirsich Konserve	\N
8275	812	3	6324	4	Aprikose Konserve	\N
8276	812	3	6325	5	Pflaume Konserve	\N
8277	812	3	6326	6	Nektarine Konserve	\N
8278	812	3	6327	7	Süßkirsche Konserve	\N
8279	812	3	6328	8	Sauerkirsche Konserve	\N
8280	812	3	6329	9	Avocado Konserve	\N
8281	812	3	6330	10	Steindunstobst Konserve	\N
8282	813	3	6332	1	Süßkirsche tiefgefroren	\N
8283	813	3	6333	2	Sauerkirsche tiefgefroren	\N
8284	813	3	6334	3	Pflaume tiefgefroren	\N
8285	813	3	6335	4	Aprikose tiefgefroren	\N
8286	813	3	6336	5	Pfirsich tiefgefroren	\N
8287	813	3	6337	6	Mirabelle tiefgefroren	\N
8288	814	3	6339	1	Pfirsich getrocknet	\N
8289	814	3	6340	2	Aprikose getrocknet	\N
8290	814	3	6341	3	Pflaume getrocknet	\N
8291	815	3	6343	1	Kirschkompott	\N
8292	815	3	6344	2	Pflaumenkompott	\N
8293	816	3	6346	1	Süßkirsche in Alkohol	\N
8294	816	3	6347	2	Pflaume in Alkohol	\N
8295	816	3	6348	3	Mirabelle in Alkohol	\N
8296	816	3	6349	4	Aprikose in Alkohol	\N
8297	818	3	6352	1	Aprikose in Sirup	\N
8298	818	3	6353	2	Süßkirsche in Sirup	\N
8299	819	3	6355	1	Mandarine Konserve	\N
8300	819	3	6356	2	Grapefruit Konserve	\N
8301	819	3	6357	3	Kumquat Konserve	\N
8302	819	3	6358	4	Orange Konserve	\N
8303	821	3	6361	1	Orangenpulver	\N
8304	821	3	6362	2	Zitronenpulver	\N
8305	821	3	6363	3	Orangeat	\N
8306	821	3	6364	4	Zitronat	\N
8307	825	3	6369	1	Ananas Konserve	\N
8308	825	3	6370	2	Papaya Konserve	\N
8309	825	3	6371	3	Mango Konserve	\N
8310	825	3	6372	4	Feige Konserve	\N
8311	825	3	6373	5	Litchi Konserve	\N
8312	825	3	6374	6	Dattel Konserve	\N
8313	825	3	6375	7	Rhabarber Konserve	\N
8314	825	3	6376	8	Banane Konserve	\N
8315	825	3	6377	9	Maracuja Passionsfrucht Konserve	\N
8316	825	3	6378	10	Kiwi Konserve	\N
8317	825	3	6379	11	Guaven Konserve	\N
8318	825	3	6380	12	Mangostanapfel Konserve	\N
8319	825	3	6381	13	Tamarillo Konserve	\N
8320	825	3	6382	14	Longan Konserve	\N
8321	827	3	6385	1	Banane getrocknet	\N
8322	827	3	6386	2	Feige getrocknet	\N
8323	827	3	6387	3	Dattel getrocknet	\N
8324	827	3	6388	4	Ananas getrocknet	\N
8325	827	3	6389	5	Litchi getrocknet	\N
8326	827	3	6390	6	Mango getrocknet	\N
8327	827	3	6391	7	Papaya getrocknet	\N
8328	829	3	6394	1	Ananas in Alkohol	\N
8329	829	3	6395	2	Feige in Alkohol	\N
8330	829	3	6396	3	Dattel in Alkohol	\N
8331	831	3	6399	1	Ananas in Sirup	\N
8332	831	3	6400	2	Ingwer in Sirup	\N
8333	832	3	6402	1	Gemischte Früchte Konserve	\N
8334	832	3	6403	2	Obstmischung Dunstobst Konserve	\N
8335	834	3	6406	1	Trockenobstmischung	\N
8336	836	3	6409	1	Rumtopffruchtmischung	\N
8337	839	3	6413	2	Himbeermark	\N
8338	839	3	6414	3	Aprikosenmark	\N
8339	839	3	6415	4	Birnenmark	\N
8340	839	3	6416	5	Hagebuttenmark	\N
8341	839	3	6417	6	Pfirsichmark	\N
8342	839	3	6418	7	Apfelmark	\N
8343	839	3	6419	8	Kirschmark	\N
8344	839	3	6420	9	Bananenmark	\N
8345	839	3	6421	10	Quittenmark	\N
8346	839	3	6422	11	Mangomark	\N
8347	839	3	6423	12	Papayamark	\N
8348	839	3	6424	13	Feigenmark	\N
8349	839	3	6425	14	Dattelmark	\N
8350	839	3	6426	15	Maracujamark Passionsfruchtmark	\N
8351	839	3	6427	16	Guavenmark	\N
8352	839	3	6428	17	Ananasmark	\N
8353	839	3	6429	18	Pflaumenmark	\N
8354	839	3	14089	90	Mehrfruchtmark	\N
8355	840	3	6431	1	Erdbeerpulpe	\N
8356	840	3	6432	2	Himbeerpulpe	\N
8357	840	3	6433	3	Kirschpulpe	\N
8358	840	3	6434	4	Apfelpulpe	\N
8359	840	3	6435	5	Birnenpulpe	\N
8360	840	3	6436	6	Aprikosenpulpe	\N
8361	840	3	6437	7	Orangenpulpe	\N
8362	840	3	6438	8	Zitronenpulpe	\N
8363	840	3	6439	9	Grapefruitpulpe	\N
8364	841	3	6443	1	Traubensaft rot	\N
8365	841	3	6444	2	Traubensaft weiß	\N
8366	841	3	6445	3	Brombeersaft	\N
8367	841	3	6446	4	Erdbeersaft	\N
8368	841	3	6447	5	Ebereschensaft	\N
8369	841	3	6448	7	Heidelbeersaft	\N
8370	841	3	6449	8	Himbeersaft	\N
8371	841	3	6450	9	Holunderbeersaft	\N
8372	841	3	6451	10	Johannisbeersaft rot	\N
8373	841	3	6452	11	Johannisbeersaft schwarz	\N
8374	841	3	6453	12	Sanddornsaft	\N
8375	841	3	6454	13	Stachelbeersaft	\N
8376	841	3	13782	14	Traubensaft	\N
8377	842	3	6456	1	Brombeernektar	\N
8378	842	3	6457	2	Ebereschennektar	\N
8379	842	3	6458	3	Erdbeernektar	\N
8380	842	3	6459	4	Hagebuttennektar	\N
8381	842	3	6460	5	Heidelbeernektar	\N
8382	842	3	6461	6	Himbeernektar	\N
8383	842	3	6462	7	Holunderbeernektar	\N
8384	842	3	6463	8	Johannisbeernektar rot	\N
8385	842	3	6464	9	Johannisbeernektar schwarz	\N
8386	842	3	6465	10	Sanddornnektar	\N
8387	842	3	6466	11	Stachelbeernektar	\N
8388	842	3	6467	12	Traubennektar rot	\N
8389	842	3	6468	13	Traubennektar weiß	\N
8390	842	3	6469	14	Preiselbeernektar	\N
8391	843	3	6471	1	Traubensaftkonzentrat rot	\N
8392	843	3	6472	2	Traubensaftkonzentrat weiß	\N
8393	843	3	6473	3	Brombeersaftkonzentrat	\N
8394	843	3	6474	4	Ebereschensaftkonzentrat	\N
8395	843	3	6475	5	Erdbeersaftkonzentrat	\N
8396	843	3	6476	6	Heidelbeersaftkonzentrat	\N
8397	843	3	6477	7	Himbeersaftkonzentrat	\N
8398	843	3	6478	8	Holunderbeersaftkonzentrat	\N
8399	843	3	6479	9	Johannisbeersaftkonzentrat rot	\N
8400	843	3	6480	10	Johannisbeersaftkonzentrat schwarz	\N
8401	843	3	6481	11	Sanddornsaftkonzentrat	\N
8402	843	3	6482	12	Stachelbeersaftkonzentrat	\N
8403	843	3	6483	13	Hagebuttensaftkonzentrat	\N
8404	843	3	6484	14	Preiselbeersaftkonzentrat	\N
8405	844	3	6486	1	Brombeersirup	\N
8406	844	3	6487	2	Erdbeersirup	\N
8407	844	3	6488	3	Heidelbeersirup	\N
8408	844	3	6489	4	Himbeersirup	\N
8409	844	3	6490	5	Holunderbeersirup	\N
8410	844	3	6491	6	Johannisbeersirup rot	\N
8411	844	3	6492	7	Johannisbeersirup schwarz	\N
8412	844	3	6493	8	Sanddornsirup	\N
8413	844	3	6494	9	Stachelbeersirup	\N
8414	844	3	6495	10	Traubensirup rot	\N
8415	844	3	6496	11	Traubensirup weiß	\N
8416	845	3	6498	1	Brombeersaft getrocknet	\N
8417	845	3	6499	2	Traubensaft getrocknet	\N
8418	845	3	6500	3	Erdbeersaft getrocknet	\N
8419	845	3	6501	4	Heidelbeersaft getrocknet	\N
8420	845	3	6502	6	Holunderbeersaft getrocknet	\N
8421	845	3	6503	7	Johannisbeersaft getrocknet rot	\N
8422	845	3	6504	8	Johannisbeersaft getrocknet schwarz	\N
8423	845	3	6505	9	Sanddornsaft getrocknet	\N
8424	845	3	6506	10	Stachelbeersaft getrocknet	\N
8425	846	3	6508	1	Apfelsaft	\N
8426	846	3	6509	2	Birnensaft	\N
8427	846	3	6510	3	Quittensaft	\N
8428	847	3	6512	1	Apfelnektar	\N
8429	847	3	6513	2	Birnennektar	\N
8430	847	3	6514	3	Quittennektar	\N
8431	848	3	6516	1	Apfelsaftkonzentrat	\N
8432	848	3	6517	2	Birnensaftkonzentrat	\N
8433	848	3	6518	3	Quittensaftkonzentrat	\N
8434	850	3	6521	1	Apfelsaft getrocknet	\N
8435	850	3	6522	2	Birnensaft getrocknet	\N
8436	850	3	6523	3	Quittensaft getrocknet	\N
8437	851	3	6525	1	Aprikosensaft	\N
8438	851	3	6526	2	Süßkirschsaft	\N
8439	851	3	6527	3	Sauerkirschsaft	\N
8440	851	3	6528	4	Pfirsichsaft	\N
8441	851	3	6529	5	Pflaumensaft	\N
8442	851	3	6531	7	Schlehensaft	\N
8443	852	3	6533	1	Aprikosennektar	\N
8444	852	3	6534	2	Süßkirschnektar	\N
8445	852	3	6535	3	Sauerkirschnektar	\N
8446	852	3	6536	4	Pfirsichnektar	\N
8447	852	3	6537	5	Pflaumennektar	\N
8448	852	3	6538	6	Schlehennektar	\N
8449	853	3	6540	1	Süßkirschsaftkonzentrat	\N
8450	853	3	6541	2	Sauerkirschsaftkonzentrat	\N
8451	853	3	6542	3	Pflaumensaftkonzentrat	\N
8452	853	3	6543	4	Aprikosensaftkonzentrat	\N
8453	853	3	6544	5	Schlehensaftkonzentrat	\N
8454	853	3	6545	6	Pfirsichsaftkonzentrat	\N
8455	854	3	6547	1	Süßkirschsirup	\N
8456	854	3	6548	2	Pflaumensirup	\N
8457	854	3	6549	3	Sauerkirschsirup	\N
8458	855	3	6551	1	Süßkirschsaft getrocknet	\N
8459	855	3	6553	3	Sauerkirschsaft getrocknet	\N
8460	855	3	6554	4	Pflaumensaft getrocknet	\N
8461	856	3	6556	1	Grapefruitsaft	\N
8462	856	3	6557	2	Zitronensaft	\N
8463	856	3	6558	3	Orangensaft	\N
8464	856	3	6559	4	Mandarinensaft	\N
8465	856	3	6560	5	Limettensaft	\N
8466	856	3	6561	6	Tangerinensaft	\N
8467	857	3	6563	1	Grapefruitnektar	\N
8468	857	3	6564	2	Zitronennektar	\N
8469	857	3	6565	3	Orangennektar	\N
8470	857	3	6566	4	Mandarinennektar	\N
8471	857	3	6567	5	Limettennektar	\N
8472	858	3	6569	1	Grapefruitsaftkonzentrat	\N
8473	858	3	6570	2	Zitronensaftkonzentrat	\N
8474	858	3	6571	3	Orangensaftkonzentrat	\N
8475	858	3	6572	4	Mandarinensaftkonzentrat	\N
8476	858	3	6573	5	Limettensaftkonzentrat	\N
8477	858	3	6574	6	Tangerinensaftkonzentrat	\N
8478	859	3	6576	1	Grapefruitsirup	\N
8479	859	3	6577	2	Zitronensirup	\N
8480	859	3	6578	3	Orangensirup	\N
8481	859	3	6579	4	Mandarinensirup	\N
8482	859	3	6580	5	Limettensirup	\N
8483	860	3	6582	1	Grapefruitsaft getrocknet	\N
8484	860	3	6583	2	Zitronensaft getrocknet	\N
8485	860	3	6584	3	Orangensaft getrocknet	\N
8486	860	3	6585	4	Mandarinensaft getrocknet	\N
8487	861	3	6587	1	Ananassaft	\N
8488	861	3	6588	2	Mangosaft	\N
8489	861	3	6589	3	Guavensaft	\N
8490	861	3	6590	4	Maracujasaft Passionsfruchtsaft	\N
8491	861	3	6591	5	Papayasaft	\N
8492	861	3	6592	6	Rhabarbersaft	\N
8493	861	3	6593	7	Kiwisaft	\N
8494	861	3	6594	8	Granatapfelsaft	\N
8495	862	3	6596	1	Ananasnektar	\N
8496	862	3	6597	2	Bananennektar	\N
8497	862	3	6598	3	Maracujanektar Passionsfruchtnektar	\N
8498	862	3	6599	4	Mangonektar	\N
8499	862	3	6600	5	Rhabarbertrunk	\N
8500	862	3	6601	6	Guavennektar	\N
8501	862	3	6602	7	Papayanektar	\N
8502	862	3	6603	8	Naranjilanektar	\N
8503	862	3	6604	9	Kiwinektar	\N
8504	862	3	6605	10	Granatapfelnektar	\N
8505	862	3	6606	11	Litschinektar	\N
8506	862	3	6607	12	Azerolanektar	\N
8507	862	3	6608	13	Stachelannonennektar	\N
8508	862	3	6609	14	Netzannonennektar	\N
8509	862	3	6610	15	Cherimoyanektar	\N
8510	862	3	6611	16	Kaschuäpfelnektar	\N
8511	862	3	6612	17	Rote Mombinpflaumennektar	\N
8512	862	3	6613	18	Umbunektar	\N
8513	863	3	6615	1	Ananassaftkonzentrat	\N
8514	863	3	6616	2	Mangosaftkonzentrat	\N
8515	863	3	6617	3	Guavensaftkonzentrat	\N
8516	863	3	6618	4	Maracujasaftkonzentrat Passionsfruchtsaftkonzentrat	\N
8517	863	3	6619	5	Papayasaftkonzentrat	\N
8518	863	3	6620	6	Rhabarbersaftkonzentrat	\N
8519	863	3	6621	7	Kiwisaftkonzentrat	\N
8520	863	3	6622	8	Granatapfelsaftkonzentrat	\N
8521	863	3	6624	10	Naranjilasaftkonzentrat	\N
8522	864	3	6626	1	Ananassirup	\N
8523	864	3	6627	2	Granatapfelsirup	\N
8524	864	3	6628	3	Bananensirup	\N
8525	865	3	6630	1	Mehrfruchtsaft mit Vitaminzusätzen	\N
8526	866	3	6632	1	Mehrfruchtnektar mit Vitaminzusätzen	\N
8527	869	3	6640	1	Ananassaft getrocknet	\N
8528	869	3	6641	2	Mangosaft getrocknet	\N
8529	869	3	6642	3	Guavensaft getrocknet	\N
8530	869	3	6643	4	Maracujasaft Passionsfruchtsaft getrocknet	\N
8531	869	3	6644	5	Papayasaft getrocknet	\N
8532	869	3	6645	6	Rhabarbersaft getrocknet	\N
8533	869	3	6646	7	Kiwisaft getrocknet	\N
8534	869	3	6647	8	Granatapfelsaft getrocknet	\N
8535	877	3	6661	1	Erdbeerfruchtsaftgetränk	\N
8536	877	3	6662	2	Himbeerfruchtsaftgetränk	\N
8537	877	3	6663	3	Brombeerfruchtsaftgetränk	\N
8538	877	3	6664	4	Johannisbeerfruchtsaftgetränk	\N
8539	877	3	6665	5	Traubenfruchtsaftgetränk	\N
8540	878	3	6667	1	Apfelfruchtsaftgetränk	\N
8541	878	3	6668	2	Birnenfruchtsaftgetränk	\N
8542	879	3	6670	1	Pfirsichfruchtsaftgetränk	\N
8543	879	3	6671	2	Aprikosenfruchtsaftgetränk	\N
8544	879	3	6672	3	Kirschfruchtsaftgetränk	\N
8545	879	3	6673	4	Pflaumenfruchtsaftgetränk	\N
8546	880	3	6675	1	Orangenfruchtsaftgetränk	\N
8547	880	3	6676	2	Mandarinenfruchtsaftgetränk	\N
8548	880	3	6677	3	Zitronenfruchtsaftgetränk	\N
8549	880	3	6678	4	Grapefruitfruchtsaftgetränk	\N
8550	880	3	6679	5	Bergamottenfruchtsaftgetränk	\N
8551	880	3	6680	6	Zitrusfruchtsaftgetränk	\N
8552	881	3	6682	1	Ananasfruchtsaftgetränk	\N
8553	881	3	6683	2	Maracujafruchtsaftgetränk Passionsfruchtsaftgetränk	\N
8554	881	3	6684	3	Bananenfruchtsaftgetränk	\N
8555	883	3	6687	1	Limonade mit Orangensaft	\N
8556	883	3	6688	2	Limonade mit Zitronensaft	\N
8557	883	3	6689	3	Limonade mit Grapefruitsaft	\N
8558	883	3	6690	4	Limonade mit Apfelsaft	\N
8559	883	3	6691	5	Limonade mit Kirschsaft	\N
8560	883	3	6692	6	Limonade mit Himbeersaft	\N
8561	883	3	6693	7	Limonade mit mehreren Fruchtsäften	\N
8562	883	3	6694	8	Limonade mit Traubensaft	\N
8563	883	3	6695	9	Limonade mit Fruchtsäften aus exotischen Früchten	\N
8564	884	3	6697	1	Limonade mit Orangengeschmack	\N
8565	884	3	6698	2	Limonade mit Zitronengeschmack	\N
8566	884	3	6699	3	Limonade mit Kirschgeschmack	\N
8567	884	3	6700	4	Limonade mit Apfelgeschmack	\N
8568	884	3	6701	6	Limonade m. verschiedenen Fruchtgeschmacksrichtungen	\N
8569	885	3	6703	1	Colalimonade coffeinhaltig	\N
8570	885	3	6704	2	Colalimonade coffeinhaltig mit Fruchtgeschmack	\N
8571	885	3	6705	3	Colalimonade coffeinfrei	\N
8572	885	3	6706	4	Colalimonade coffeinfrei mit Fruchtgeschmack	\N
8573	886	3	6708	1	Ingwerlimonade	\N
8574	886	3	6709	2	Kräuterlimonade	\N
8575	886	3	6710	3	Bitter Lemon	\N
8576	886	3	6711	4	Tonic-Water	\N
8577	887	3	6713	1	Brause künstliche Kaltgetränk mit Himbeergeschmack	\N
8578	887	3	6714	2	Brause künstliche Kaltgetränk mit Apfelgeschmack	\N
8579	887	3	6715	3	Brause künstliche Kaltgetränk mit Kirschgeschmack	\N
8580	887	3	6716	4	Brause künstliche Kaltgetränk mit Waldmeistergeschmack	\N
8581	887	3	6717	5	Brause künstl. Kaltget. m. verschiedenen Geschmacksrichtungen	\N
8582	887	3	6718	6	Brause künstl. Kaltget. m. Zitrusfruchtgeschmack	\N
8583	889	3	6721	1	Brause künstl. Kaltget. coffeinhaltig mit Guarana	\N
8584	889	3	6722	2	Brause künstl. Kaltget. coffeinhaltig mit Kaffee	\N
8585	890	3	6724	1	Brottrunk milchsauer	\N
8586	890	3	6725	2	Kombuchagetränk	\N
8587	891	3	6727	1	Ansatz und Grundstoff für Beerenobstfruchtsaftgetränk	\N
8588	891	3	6728	2	Ansatz und Grundstoff für Kernobstfruchtsaftgetränk	\N
8589	891	3	6729	3	Ansatz und Grundstoff für Steinobstfruchtsaftgetränk	\N
8590	891	3	6730	4	Ansatz und Grundstoff für Zitrusfruchtsaftgetränk	\N
8591	891	3	6731	5	Ansatz und Grundstoff für Fruchtsaftgetränk aus exotischen Früchten	\N
8592	896	3	6737	1	Getränkepulver für Beerenobstfruchtsaftgetränk	\N
8593	896	3	6738	2	Getränkepulver für Kernobstfruchtsaftgetränk	\N
8594	896	3	6739	3	Getränkepulver für Steinobstfruchtsaftgetränk	\N
8595	896	3	6740	4	Getränkepulver für Zitrusfruchtsaftgetränk	\N
8596	896	3	6741	5	Getränkepulver für Fruchtsaftgetränke aus exotischen Früchten	\N
8597	897	3	6743	1	Getränkepulver für Orangenlimonade	\N
8598	897	3	6744	2	Getränkepulver für Zitronenlimonade	\N
8599	897	3	6745	3	Getränkepulver für Limonade mit Grapefruitgeschmack	\N
8600	897	3	6746	4	Getränkepulver für Limonade mit Apfelgeschmack	\N
8601	897	3	6747	5	Getränkepulver für Limonade mit Kirschgeschmack	\N
8602	897	3	6748	6	Getränkepulver für Limonade mit Himbeergeschmack	\N
8603	897	3	6749	7	Getränkepulver für Limonade mit Mehrfruchtgeschmack	\N
8604	897	3	6750	8	Getränkepulver für Limonade verschiedener Geschmacksrichtungen	\N
8605	898	3	6752	1	Getränkepulver für Brause mit Apfelgeschmack	\N
8606	898	3	6753	2	Getränkepulver für Brause mit Kirschgeschmack	\N
8607	898	3	6754	3	Getränkepulver für Brause mit Waldmeistergeschmack	\N
8608	898	3	6755	4	Getränkepulver für Brause verschiedener Geschmacksrichtungen	\N
8609	899	3	6759	3	Getränkepulver mit Malz	\N
8610	899	3	6760	4	Getränkepulver mit Milch- und Fruchterzeugnissen	\N
8611	899	3	6761	5	Getränkepulver für Eisteegetränke	\N
8612	900	3	6763	1	Getränk aus Trockenpflaumen	\N
8613	901	3	6765	1	Apfelsaft mit Essig	\N
8614	901	3	6766	2	Getränk mit Ballaststoffen	\N
8615	901	3	6767	3	Mischung von Erfrischungsgetränken mit sonstigen LM	\N
8616	901	3	6768	4	Erfrischungsgetränk mit Grünteeextrakt und/oder Apfelessig	\N
8617	901	3	6769	5	Getränk mit Gemüseanteil	\N
8618	901	3	6770	6	Erfrischungsgetränk mit Zusatz von Milchprodukten	\N
8619	901	3	6771	7	Getränk mit Hanf	\N
8620	901	3	6772	8	Erfrischungsgetränk mit Vitaminzusätzen	\N
8621	901	3	13329	9	Eistee	\N
8622	901	3	14200	10	Getränk auf Getreidebasis, nicht gegoren	\N
8623	901	3	14269	11	Eistee brennwertreduziert	\N
8624	910	3	6782	1	Colalimonade coffeinhaltig brennwertreduziert	\N
8625	910	3	6783	2	Colalimonade coffeinhaltig mit Fruchtgeschmack brennwertreduziert	\N
8626	910	3	6784	3	Colalimonade coffeinfrei brennwertreduziert	\N
8627	910	3	6785	4	Colalimonade coffeinfrei mit Fruchtgeschmack brennwertreduziert	\N
8628	911	3	6787	1	Ingwerlimonade brennwertreduziert	\N
8629	911	3	6788	2	Kräuterlimonade brennwertreduziert	\N
8630	911	3	6789	3	Bitter Lemon brennwertreduziert	\N
8631	915	3	6794	1	Mineralwasser plus Fruchtsaft	\N
8632	915	3	6795	2	Mineralwasser aromatisiert	\N
8633	916	3	14270	1	Energydrink	\N
8634	916	3	14271	2	Energydrink brennwertreduziert	\N
8635	916	3	14272	3	Energydrink mit Fruchtsaft	\N
8636	916	3	14273	4	Energydrink mit Kaffee	\N
8637	922	3	6799	10	Wein unbekannter Qualität nicht weiter differenzierbar weiß	\N
8638	922	3	6800	20	Wein unbekannter Qualität nicht weiter differenzierbar rot	\N
8639	922	3	6801	30	Wein unbekannter Qualität nicht weiter differenzierbar rosé	\N
8640	923	3	6803	10	Wein einfacher Qualität (auch Tafelwein) ohne geogr. Herkunft weiß WBZ unbekannt	\N
8641	923	3	6804	11	Wein einfacher Qualität (auch Tafelwein) ohne geogr. Herkunft weiß WBZ A	\N
8642	923	3	6805	12	Wein einfacher Qualität (auch Tafelwein) ohne geogr. Herkunft weiß WBZ B	\N
8643	923	3	6806	13	Wein einfacher Qualität (auch Tafelwein) ohne geogr. Herkunft weiß WBZ C I a	\N
8644	923	3	6807	14	Wein einfacher Qualität (auch Tafelwein) ohne geogr. Herkunft weiß WBZ C I b	\N
8645	923	3	6808	15	Wein einfacher Qualität (auch Tafelwein) ohne geogr. Herkunft weiß WBZ C II	\N
8646	923	3	6809	16	Wein einfacher Qualität (auch Tafelwein) ohne geogr. Herkunft weiß WBZ C IIIa	\N
8647	923	3	6810	17	Wein einfacher Qualität (auch Tafelwein) ohne geogr. Herkunft weiß WBZ C IIIb	\N
8648	923	3	6811	19	Wein einfacher Qualität (auch Tafelwein) ohne geogr. Herkunft weiß Drittländer	\N
8649	923	3	6812	20	Wein einfacher Qualität (auch Tafelwein) ohne geogr. Herkunft rot WBZ unbekannt	\N
8650	923	3	6813	21	Wein einfacher Qualität (auch Tafelwein) ohne geogr. Herkunft rot WBZ A	\N
8651	923	3	6814	22	Wein einfacher Qualität (auch Tafelwein) ohne geogr. Herkunft rot WBZ B	\N
8652	923	3	6815	23	Wein einfacher Qualität (auch Tafelwein) ohne geogr. Herkunft rot WBZ C I a	\N
8653	923	3	6816	24	Wein einfacher Qualität (auch Tafelwein) ohne geogr. Herkunft rot WBZ C I b	\N
8654	923	3	6817	25	Wein einfacher Qualität (auch Tafelwein) ohne geogr. Herkunft rot WBZ C II	\N
8655	923	3	6818	26	Wein einfacher Qualität (auch Tafelwein) ohne geogr. Herkunft rot WBZ C III a	\N
8656	923	3	6819	27	Wein einfacher Qualität (auch Tafelwein) ohne geogr. Herkunft rot WBZ C III b	\N
8657	923	3	6820	29	Wein einfacher Qualität (auch Tafelwein) ohne geogr. Herkunft rot Drittländer	\N
8658	923	3	6821	30	Wein einfacher Qualität (auch Tafelwein) ohne geogr. Herkunft rosé WBZ unbekannt	\N
8659	923	3	6822	31	Wein einfacher Qualität (auch Tafelwein) ohne geogr. Herkunft rosé WBZ A	\N
8660	923	3	6823	32	Wein einfacher Qualität (auch Tafelwein) ohne geogr. Herkunft rosé WBZ B	\N
8661	923	3	6824	33	Wein einfacher Qualität (auch Tafelwein) ohne geogr. Herkunft rosé WBZ C I a	\N
8662	923	3	6825	34	Wein einfacher Qualität (auch Tafelwein) ohne geogr. Herkunft rosé WBZ C I b	\N
8663	923	3	6826	35	Wein einfacher Qualität (auch Tafelwein) ohne geogr. Herkunft rosé WBZ C II	\N
8664	923	3	6827	36	Wein einfacher Qualität (auch Tafelwein) ohne geogr. Herkunft rosé WBZ C IIIa	\N
8665	923	3	6828	37	Wein einfacher Qualität (auch Tafelwein) ohne geogr. Herkunft rosé WBZ C IIIb	\N
8666	923	3	6829	39	Wein einfacher Qualität (auch Tafelwein) ohne geogr. Herkunft rosé Drittländer	\N
8667	924	3	6831	10	Wein einfacher Qualität (auch Tafelwein) mit geogr. Herkunft weiß WBZ unbekannt	\N
8668	924	3	6832	11	Wein einfacher Qualität (auch Tafelwein) mit geogr. Herkunft weiß WBZ A	\N
8669	924	3	6833	12	Wein einfacher Qualität (auch Tafelwein) mit geogr. Herkunft weiß WBZ B	\N
8670	924	3	6834	13	Wein einfacher Qualität (auch Tafelwein) mit geogr. Herkunft weiß WBZ C I a	\N
8671	924	3	6835	14	Wein einfacher Qualität (auch Tafelwein) mit geogr. Herkunft weiß WBZ C I b	\N
8672	924	3	6836	15	Wein einfacher Qualität (auch Tafelwein) mit geogr. Herkunft weiß WBZ C II	\N
8673	924	3	6837	16	Wein einfacher Qualität (auch Tafelwein) mit geogr. Herkunft weiß WBZ C III a	\N
8674	924	3	6838	17	Wein einfacher Qualität (auch Tafelwein) mit geogr. Herkunft weiß WBZ C III b	\N
8675	924	3	6839	19	Wein einfacher Qualität (auch Tafelwein) mit geogr. Herkunft weiß Drittländer	\N
8676	924	3	6840	20	Wein einfacher Qualität (auch Tafelwein) mit geogr. Herkunft rot WBZ unbekannt	\N
8677	924	3	6841	21	Wein einfacher Qualität (auch Tafelwein) mit geogr. Herkunft rot WBZ A	\N
8678	924	3	6842	22	Wein einfacher Qualität (auch Tafelwein) mit geogr. Herkunft rot WBZ B	\N
8679	924	3	6843	23	Wein einfacher Qualität (auch Tafelwein) mit geogr. Herkunft rot WBZ C I a	\N
8680	924	3	6844	24	Wein einfacher Qualität (auch Tafelwein) mit geogr. Herkunft rot WBZ C I b	\N
8681	924	3	6845	25	Wein einfacher Qualität (auch Tafelwein) mit geogr. Herkunft rot WBZ C II	\N
8682	924	3	6846	26	Wein einfacher Qualität (auch Tafelwein) mit geogr. Herkunft rot WBZ C III a	\N
8683	924	3	6847	27	Wein einfacher Qualität (auch Tafelwein) mit geogr. Herkunft rot WBZ C III b	\N
8684	924	3	6848	29	Wein einfacher Qualität (auch Tafelwein) mit geogr. Herkunft rot Drittländer	\N
8685	924	3	6849	30	Wein einfacher Qualität (auch Tafelwein) mit geogr. Herkunft rosé WBZ unbekannt	\N
8686	924	3	6850	31	Wein einfacher Qualität (auch Tafelwein) mit geogr. Herkunft rosé WBZ A	\N
8687	924	3	6851	32	Wein einfacher Qualität (auch Tafelwein) mit geogr. Herkunft rosé WBZ B	\N
8688	924	3	6852	33	Wein einfacher Qualität (auch Tafelwein) mit geogr. Herkunft rosé WBZ C Ia	\N
8689	924	3	6853	34	Wein einfacher Qualität (auch Tafelwein) mit geogr. Herkunft rosé WBZ C I b	\N
8690	924	3	6854	35	Wein einfacher Qualität (auch Tafelwein) mit geogr. Herkunft rosé WBZ C II	\N
8691	924	3	6855	36	Wein einfacher Qualität (auch Tafelwein) mit geogr. Herkunft rosé WBZ C III a	\N
8692	924	3	6856	37	Wein einfacher Qualität (auch Tafelwein) mit geogr. Herkunft rosé WBZ C III b	\N
8693	924	3	6857	39	Wein einfacher Qualität (auch Tafelwein) mit geogr. Herkunft rosé Drittländer	\N
8694	924	3	6858	40	Wein einfacher Qualität (auch Tafelwein) mit geogr. Herkunft Rotling WBZ A	\N
8695	924	3	14090	41	Wein einfacher Qualität (auch Tafelwein) mit geogr. Herkunft Rotling WBZ B	\N
8696	925	3	6860	10	Landwein weiß WBZ unbekannt	\N
8697	925	3	6861	11	Landwein weiß WBZ A	\N
8698	925	3	6862	12	Landwein weiß WBZ B	\N
8699	925	3	6863	13	Landwein weiß WBZ C I a	\N
8700	925	3	6864	14	Landwein weiß WBZ C I b	\N
8701	925	3	6865	15	Landwein weiß WBZ C II	\N
8702	925	3	6866	16	Landwein weiß WBZ C III a	\N
8703	925	3	6867	17	Landwein weiß WBZ C III b	\N
8704	925	3	6868	19	Landwein weiß Drittländer	\N
8705	925	3	6869	20	Landwein rot WBZ unbekannt	\N
8706	925	3	6870	21	Landwein rot WBZ A	\N
8707	925	3	6871	22	Landwein rot WBZ B	\N
8708	925	3	6872	23	Landwein rot WBZ C I a	\N
8709	925	3	6873	24	Landwein rot WBZ C I b	\N
8710	925	3	6874	25	Landwein rot WBZ C II	\N
8711	925	3	6875	26	Landwein rot WBZ C III a	\N
8712	925	3	6876	27	Landwein rot WBZ C III b	\N
8713	925	3	6877	29	Landwein rot Drittländer	\N
8714	925	3	6878	30	Landwein rosé WBZ unbekannt	\N
8715	925	3	6879	31	Landwein rosé WBZ A	\N
8716	925	3	6880	32	Landwein rosé WBZ B	\N
8717	925	3	6881	33	Landwein rosé WBZ C I a	\N
8718	925	3	6882	34	Landwein rosé WBZ C I b	\N
8719	925	3	6883	35	Landwein rosé WBZ C II	\N
8720	925	3	6884	36	Landwein rosé WBZ C III a	\N
8721	925	3	6885	37	Landwein rosé WBZ C III b	\N
8722	925	3	6886	39	Landwein rosé Drittländer	\N
8723	925	3	6887	40	Landwein Rotling WBZ A	\N
8724	927	3	6890	10	Wein gehobener Qualität nicht weiter differenzierbar auch Qualitätswein b. A. weiß WBZ unbekannt	\N
8725	927	3	6891	11	Wein gehobener Qualität nicht weiter differenzierbar auch Qualitätswein b. A. weiß WBZ A	\N
8726	927	3	6892	12	Wein gehobener Qualität nicht weiter differenzierbar auch Qualitätswein b. A. weiß WBZ B	\N
8727	927	3	6893	13	Wein gehobener Qualität nicht weiter differenzierbar auch Qualitätswein b. A. weiß WBZ C I a	\N
8728	927	3	6894	14	Wein gehobener Qualität nicht weiter differenzierbar auch Qualitätswein b. A. weiß WBZ C I b	\N
8729	927	3	6895	15	Wein gehobener Qualität nicht weiter differenzierbar auch Qualitätswein b. A. weiß WBZ C II	\N
8730	927	3	6896	16	Wein gehobener Qualität nicht weiter differenzierbar auch Qualitätswein b. A. weiß WBZ C III a	\N
8731	927	3	6897	17	Wein gehobener Qualität nicht weiter differenzierbar auch Qualitätswein b. A. weiß WBZ C III b	\N
8732	927	3	6898	19	Wein gehobener Qualität nicht weiter differenzierbar auch Qualitätswein b. A. weiß Drittländer	\N
8733	927	3	6899	20	Wein gehobener Qualität nicht weiter differenzierbar auch Qualitätswein b. A. rot WBZ unbekannt	\N
8734	927	3	6900	21	Wein gehobener Qualität nicht weiter differenzierbar auch Qualitätswein b. A. rot WBZ A	\N
8735	927	3	6901	22	Wein gehobener Qualität nicht weiter differenzierbar auch Qualitätswein b. A. rot WBZ B	\N
8736	927	3	6902	23	Wein gehobener Qualität nicht weiter differenzierbar auch Qualitätswein b. A. rot WBZ C I a	\N
8737	927	3	6903	24	Wein gehobener Qualität nicht weiter differenzierbar auch Qualitätswein b. A. rot WBZ C I b	\N
8738	927	3	6904	25	Wein gehobener Qualität nicht weiter differenzierbar auch Qualitätswein b. A. rot WBZ C II	\N
8739	927	3	6905	26	Wein gehobener Qualität nicht weiter differenzierbar auch Qualitätswein b. A. rot WBZ C III a	\N
8740	927	3	6906	27	Wein gehobener Qualität nicht weiter differenzierbar auch Qualitätswein b. A. rot WBZ C III b	\N
9323	988	3	7550	3	Pfirsichperlwein	\N
8741	927	3	6907	29	Wein gehobener Qualität nicht weiter differenzierbar auch Qualitätswein b. A. rot Drittländer	\N
8742	927	3	6908	30	Wein gehobener Qualität nicht weiter differenzierbar auch Qualitätswein b. A. rosé WBZ unbekannt	\N
8743	927	3	6909	31	Wein gehobener Qualität nicht weiter differenzierbar auch Qualitätswein b. A. rosé WBZ A	\N
8744	927	3	6910	32	Wein gehobener Qualität nicht weiter differenzierbar auch Qualitätswein b. A. rosé WBZ B	\N
8745	927	3	6911	33	Wein gehobener Qualität nicht weiter differenzierbar auch Qualitätswein b. A. rosé WBZ C I a	\N
8746	927	3	6912	34	Wein gehobener Qualität nicht weiter differenzierbar auch Qualitätswein b. A. rosé WBZ C I b	\N
8747	927	3	6913	35	Wein gehobener Qualität nicht weiter differenzierbar auch Qualitätswein b. A. rosé WBZ C II	\N
8748	927	3	6914	36	Wein gehobener Qualität nicht weiter differenzierbar auch Qualitätswein b. A. rosé WBZ C III a	\N
8749	927	3	6915	37	Wein gehobener Qualität nicht weiter differenzierbar auch Qualitätswein b. A. rosé WBZ C III b	\N
8750	927	3	6916	39	Wein gehobener Qualität nicht weiter differenzierbar auch Qualitätswein b. A. rosé Drittländer	\N
8751	927	3	6917	40	Wein gehobener Qualität nicht weiter differenzierbar auch Qualitätswein b.A., Rotling WBZ A	\N
8752	928	3	6919	10	Qualitätswein garantierten Ursprungs weiß WBZ unbekannt	\N
8753	928	3	6920	11	Qualitätswein garantierten Ursprungs weiß WBZ A	\N
8754	928	3	6921	12	Qualitätswein garantierten Ursprungs weiß WBZ B	\N
8755	928	3	6922	20	Qualitätswein garantierten Ursprungs rot WBZ unbekannt	\N
8756	928	3	6923	21	Qualitätswein garantierten Ursprungs rot WBZ A	\N
8757	928	3	6924	22	Qualitätswein garantierten Ursprungs rot WBZ B	\N
8758	928	3	6925	30	Qualitätswein garantierten Ursprungs rosé WBZ unbekannt	\N
8759	928	3	6926	31	Qualitätswein garantierten Ursprungs rosé WBZ A	\N
8760	928	3	6927	32	Qualitätswein garantierten Ursprungs rosé WBZ B	\N
8761	929	3	6929	10	Qualitätswein mit Prädikat Kabinett weiß WBZ unbekannt	\N
8762	929	3	6930	11	Qualitätswein mit Prädikat Kabinett weiß WBZ A	\N
8763	929	3	6931	12	Qualitätswein mit Prädikat Kabinett weiß WBZ B	\N
8764	929	3	6932	20	Qualitätswein mit Prädikat Kabinett rot WBZ unbekannt	\N
8765	929	3	6933	21	Qualitätswein mit Prädikat Kabinett rot WBZ A	\N
8766	929	3	6934	22	Qualitätswein mit Prädikat Kabinett rot WBZ B	\N
8767	929	3	6935	30	Qualitätswein mit Prädikat Kabinett rosé WBZ unbekannt	\N
8768	929	3	6936	31	Qualitätswein mit Prädikat Kabinett rosé WBZ A	\N
8769	929	3	6937	32	Qualitätswein mit Prädikat Kabinett rosé WBZ B	\N
8770	930	3	6939	10	Qualitätswein mit Prädikat Spätlese weiß WBZ unbekannt	\N
8771	930	3	6940	11	Qualitätswein mit Prädikat Spätlese weiß WBZ A	\N
8772	930	3	6941	12	Qualitätswein mit Prädikat Spätlese weiß WBZ B	\N
8773	930	3	6942	20	Qualitätswein mit Prädikat Spätlese rot WBZ unbekannt	\N
8774	930	3	6943	21	Qualitätswein mit Prädikat Spätlese rot WBZ A	\N
8775	930	3	6944	22	Qualitätswein mit Prädikat Spätlese rot WBZ B	\N
8776	930	3	6945	30	Qualitätswein mit Prädikat Spätlese rosé WBZ unbekannt	\N
8777	930	3	6946	31	Qualitätswein mit Prädikat Spätlese rosé WBZ A	\N
8778	930	3	6947	32	Qualitätswein mit Prädikat Spätlese rosé WBZ B	\N
8779	931	3	6949	10	Qualitätswein mit Prädikat Auslese weiß WBZ unbekannt	\N
8780	931	3	6950	11	Qualitätswein mit Prädikat Auslese weiß WBZ A	\N
8781	931	3	6951	12	Qualitätswein mit Prädikat Auslese weiß WBZ B	\N
8782	931	3	6952	20	Qualitätswein mit Prädikat Auslese rot WBZ unbekannt	\N
8783	931	3	6953	21	Qualitätswein mit Prädikat Auslese rot WBZ A	\N
8784	931	3	6954	22	Qualitätswein mit Prädikat Auslese rot WBZ B	\N
8785	931	3	6955	30	Qualitätswein mit Prädikat Auslese rosé WBZ unbekannt	\N
8786	931	3	6956	31	Qualitätswein mit Prädikat Auslese rosé WBZ A	\N
8787	931	3	6957	32	Qualitätswein mit Prädikat Auslese rosé WBZ B	\N
8788	932	3	6959	10	Qualitätswein mit Prädikat Beerenauslese weiß WBZ unbekannt	\N
8789	932	3	6960	11	Qualitätswein mit Prädikat Beerenauslese weiß WBZ A	\N
8790	932	3	6961	12	Qualitätswein mit Prädikat Beerenauslese weiß WBZ B	\N
8791	932	3	6962	13	Qualitätswein mit Prädikat Beerenauslese rot WBZ unbekannt	\N
8792	932	3	6963	14	Qualitätswein mit Prädikat Beerenauslese rot WBZ A	\N
8793	932	3	6964	15	Qualitätswein mit Prädikat Beerenauslese rot WBZ B	\N
8794	933	3	6966	10	Qualitätswein mit Prädikat Trockenbeerenauslese weiß WBZ unbekannt	\N
8795	933	3	6967	11	Qualitätswein mit Prädikat Trockenbeerenauslese weiß WBZ A	\N
8796	933	3	6968	12	Qualitätswein mit Prädikat Trockenbeerenauslese weiß WBZ B	\N
8797	933	3	6969	13	Qualitätswein mit Prädikat Trockenbeerenauslese rot WBZ unbekannt	\N
8798	933	3	6970	14	Qualitätswein mit Prädikat Trockenbeerenauslese rot WBZ A	\N
8799	933	3	6971	15	Qualitätswein mit Prädikat Trockenbeerenauslese rot WBZ B	\N
8800	934	3	6973	10	Qualitätswein mit Prädikat Eiswein weiß WBZ unbekannt	\N
8801	934	3	6974	11	Qualitätswein mit Prädikat Eiswein weiß WBZ A	\N
8802	934	3	6975	12	Qualitätswein mit Prädikat Eiswein weiß WBZ B	\N
8803	934	3	6976	13	Qualitätswein mit Prädikat Eiswein rot WBZ unbekannt	\N
8804	934	3	6977	14	Qualitätswein mit Prädikat Eiswein rot WBZ A	\N
8805	934	3	6978	15	Qualitätswein mit Prädikat Eiswein rot WBZ B	\N
8806	935	3	6980	10	Wein gehobener Qualität mit prädikatsähnlichen Angaben weiß WBZ unbekannt	\N
8807	935	3	6981	11	Wein gehobener Qualität mit prädikatsähnlichen Angaben weiß WBZ A	\N
8808	935	3	6982	12	Wein gehobener Qualität mit prädikatsähnlichen Angaben weiß WBZ B	\N
8809	935	3	6983	13	Wein gehobener Qualität mit prädikatsähnlichen Angaben weiß WBZ C I a	\N
8810	935	3	6984	14	Wein gehobener Qualität mit prädikatsähnlichen Angaben weiß WBZ C I b	\N
8811	935	3	6985	15	Wein gehobener Qualität mit prädikatsähnlichen Angaben weiß WBZ C II	\N
8812	935	3	6986	16	Wein gehobener Qualität mit prädikatsähnlichen Angaben weiß WBZ C III a	\N
9324	988	3	7551	4	Pflaumenperlwein	\N
8813	935	3	6987	17	Wein gehobener Qualität mit prädikatsähnlichen Angaben weiß WBZ C III b	\N
8814	935	3	6988	19	Wein gehobener Qualität mit prädikatsähnlichen Angaben weiß Drittländer	\N
8815	935	3	6989	20	Wein gehobener Qualität mit prädikatsähnlichen Angaben rot WBZ unbekannt	\N
8816	935	3	6990	21	Wein gehobener Qualität mit prädikatsähnlichen Angaben rot WBZ A	\N
8817	935	3	6991	22	Wein gehobener Qualität mit prädikatsähnlichen Angaben rot WBZ B	\N
8818	935	3	6992	23	Wein gehobener Qualität mit prädikatsähnlichen Angaben rot WBZ C I a	\N
8819	935	3	6993	24	Wein gehobener Qualität mit prädikatsähnlichen Angaben rot WBZ C I b	\N
8820	935	3	6994	25	Wein gehobener Qualität mit prädikatsähnlichen Angaben rot WBZ C II	\N
8821	935	3	6995	26	Wein gehobener Qualität mit prädikatsähnlichen Angaben rot WBZ C III a	\N
8822	935	3	6996	27	Wein gehobener Qualität mit prädikatsähnlichen Angaben rot WBZ C III b	\N
8823	935	3	6997	29	Wein gehobener Qualität mit prädikatsähnlichen Angaben rot Drittländer	\N
8824	935	3	6998	30	Wein gehobener Qualität mit prädikatsähnlichen Angaben rosé WBZ unbekannt	\N
8825	935	3	6999	31	Wein gehobener Qualität mit prädikatsähnlichen Angaben rosé WBZ A	\N
8826	935	3	7000	32	Wein gehobener Qualität mit prädikatsähnlichen Angaben rosé WBZ B	\N
8827	935	3	7001	33	Wein gehobener Qualität mit prädikatsähnlichen Angaben rosé WBZ C Ia	\N
8828	935	3	7002	34	Wein gehobener Qualität mit prädikatsähnlichen Angaben rosé WBZ C I b	\N
8829	935	3	7003	35	Wein gehobener Qualität mit prädikatsähnlichen Angaben rosé WBZ C II	\N
8830	935	3	7004	36	Wein gehobener Qualität mit prädikatsähnlichen Angaben rosé WBZ C III a	\N
8831	935	3	7005	37	Wein gehobener Qualität mit prädikatsähnlichen Angaben rosé WBZ C III b	\N
8832	935	3	7006	39	Wein gehobener Qualität mit prädikatsähnlichen Angaben rosé Drittländer	\N
8833	936	3	7008	17	gestrichen, JUNGW., TRAUBENM. TEILW. GEGOR., WEISS FRANKEN	\N
8834	937	3	7010	10	Perlwein nicht weiter differenzierbar weiß	\N
8835	937	3	7011	20	Perlwein nicht weiter differenzierbar rot	\N
8836	937	3	7012	30	Perlwein nicht weiter differenzierbar rosé	\N
8837	938	3	7014	10	Perlwein mit zugesetzter Kohlensäure weiß	\N
8838	938	3	7015	20	Perlwein mit zugesetzter Kohlensäure rot	\N
8839	938	3	7016	30	Perlwein mit zugesetzter Kohlensäure rosé	\N
8840	939	3	7018	10	Qualitätsperlwein weiß	\N
8841	939	3	7019	20	Qualitätsperlwein rot	\N
8842	939	3	7020	30	Qualitätsperlwein rosé	\N
8843	940	3	7022	10	Qualitätsperlwein mit zugesetzter Kohlensäure weiß	\N
8844	940	3	7023	20	Qualitätsperlwein mit zugesetzter Kohlensäure rot	\N
8845	940	3	7024	30	Qualitätsperlwein mit zugesetzter Kohlensäure rosé	\N
8846	941	3	7026	10	Qualitätsperlwein b.A. weiß WBZ unbekannt	\N
8847	941	3	7027	11	Qualitätsperlwein b.A. weiß WBZ A	\N
8848	941	3	7028	12	Qualitätsperlwein b.A. weiß WBZ B	\N
8849	941	3	7029	13	Qualitätsperlwein b.A. weiß WBZ C I a	\N
8850	941	3	7030	14	Qualitätsperlwein b.A. weiß WBZ C I b	\N
8851	941	3	7031	15	Qualitätsperlwein b.A. weiß WBZ C II	\N
8852	941	3	7032	16	Qualitätsperlwein b.A. weiß WBZ C III a	\N
8853	941	3	7033	17	Qualitätsperlwein b.A. weiß WBZ C III b	\N
8854	941	3	7034	19	Qualitätsperlwein weiß Drittländer	\N
8855	941	3	7035	20	Qualitätsperlwein b.A. rot WBZ unbekannt	\N
8856	941	3	7036	21	Qualitätsperlwein b.A. rot WBZ A	\N
8857	941	3	7037	22	Qualitätsperlwein b.A. rot WBZ B	\N
8858	941	3	7038	23	Qualitätsperlwein b.A. rot WBZ C I a	\N
8859	941	3	7039	24	Qualitätsperlwein b.A. rot WBZ C I b	\N
8860	941	3	7040	25	Qualitätsperlwein b.A. rot WBZ C II	\N
8861	941	3	7041	26	Qualitätsperlwein b.A. rot WBZ C III a	\N
8862	941	3	7042	27	Qualitätsperlwein b.A. rot WBZ C III b	\N
8863	941	3	7043	29	Qualitätsperlwein rot Drittländer	\N
8864	941	3	7044	30	Qualitätsperlwein b.A. rosé WBZ unbekannt	\N
8865	941	3	7045	31	Qualitätsperlwein b.A. rosé WBZ A	\N
8866	941	3	7046	32	Qualitätsperlwein b.A. rosé WBZ B	\N
8867	941	3	7047	33	Qualitätsperlwein b.A. rosé WBZ C I a	\N
8868	941	3	7048	34	Qualitätsperlwein b.A. rosé WBZ C I b	\N
8869	941	3	7049	35	Qualitätsperlwein b.A. rosé WBZ C II	\N
8870	941	3	7050	36	Qualitätsperlwein b.A. rosé WBZ C III a	\N
8871	941	3	7051	37	Qualitätsperlwein b.A. rosé WBZ C III b	\N
8872	941	3	7052	39	Qualitätsperlwein rosé Drittländer	\N
8873	942	3	7054	10	Schaumwein nicht weiter differenzierbar weiß	\N
8874	942	3	7055	20	Schaumwein nicht weiter differenzierbar rot	\N
8875	942	3	7056	30	Schaumwein nicht weiter differenzierbar rosé	\N
8876	943	3	7058	10	Schaumwein mit zugesetzter Kohlensäure weiß	\N
8877	943	3	7059	20	Schaumwein mit zugesetzter Kohlensäure rot	\N
8878	943	3	7060	30	Schaumwein mit zugesetzter Kohlensäure rosé	\N
8879	944	3	7062	10	Qualitätsschaumwein weiß	\N
8880	944	3	7063	20	Qualitätsschaumwein rot	\N
8881	944	3	7064	30	Qualitätsschaumwein rosé	\N
8882	945	3	7066	10	Qualitätsschaumwein b.A. weiß WBZ unbekannt	\N
8883	945	3	7067	11	Qualitätsschaumwein b.A. weiß WBZ A	\N
8884	945	3	7068	12	Qualitätsschaumwein b.A. weiß WBZ B	\N
8885	945	3	7069	13	Qualitätsschaumwein b.A. weiß WBZ C I a	\N
8886	945	3	7070	14	Qualitätsschaumwein b.A. weiß WBZ C I b	\N
8887	945	3	7071	15	Qualitätsschaumwein b.A. weiß WBZ C II	\N
8888	945	3	7072	16	Qualitätsschaumwein b.A. weiß WBZ C III a	\N
8889	945	3	7073	17	Qualitätsschaumwein b.A. weiß WBZ C III b	\N
8890	945	3	7074	19	Qualitätsschaumwein weiß Drittländer	\N
8891	945	3	7075	20	Qualitätsschaumwein b.A. rot WBZ unbekannt	\N
8892	945	3	7076	21	Qualitätsschaumwein b.A. rot WBZ A	\N
8893	945	3	7077	22	Qualitätsschaumwein b.A. rot WBZ B	\N
8894	945	3	7078	23	Qualitätsschaumwein b.A. rot WBZ C I a	\N
8895	945	3	7079	24	Qualitätsschaumwein b.A. rot WBZ C I b	\N
8896	945	3	7080	25	Qualitätsschaumwein b.A. rot WBZ C II	\N
8897	945	3	7081	26	Qualitätsschaumwein b.A. rot WBZ C III a	\N
8898	945	3	7082	27	Qualitätsschaumwein b.A. rot WBZ C III b	\N
8899	945	3	7083	29	Qualitätsschaumwein rot Drittländer	\N
8900	945	3	7084	30	Qualitätsschaumwein b.A. rosé WBZ unbekannt	\N
8901	945	3	7085	31	Qualitätsschaumwein b.A. rosé WBZ A	\N
8902	945	3	7086	32	Qualitätsschaumwein b.A. rosé WBZ B	\N
8903	945	3	7087	33	Qualitätsschaumwein b.A. rosé WBZ C I a	\N
8904	945	3	7088	34	Qualitätsschaumwein b.A. rosé WBZ C I b	\N
8905	945	3	7089	35	Qualitätsschaumwein b.A. rosé WBZ C II	\N
8906	945	3	7090	36	Qualitätsschaumwein b.A. rosé WBZ C III a	\N
8907	945	3	7091	37	Qualitätsschaumwein b.A. rosé WBZ C III b	\N
8908	945	3	7092	39	Qualitätsschaumwein rosé Drittländer	\N
8909	946	3	7094	10	Aromatischer Qualitätsschaumwein weiß	\N
8910	946	3	7095	20	Aromatischer Qualitätsschaumwein rot	\N
8911	946	3	7096	30	Aromatischer Qualitätsschaumwein rosé	\N
8912	947	3	7098	10	Aromatischer Qualitätsschaumwein b.A. weiß WBZ unbekannt	\N
8913	947	3	7099	11	Aromatischer Qualitätsschaumwein b.A. weiß WBZ A	\N
8914	947	3	7100	12	Aromatischer Qualitätsschaumwein b.A. weiß WBZ B	\N
8915	947	3	7101	13	Aromatischer Qualitätsschaumwein b.A. weiß WBZ C I a	\N
8916	947	3	7102	14	Aromatischer Qualitätsschaumwein b.A. weiß WBZ C I b	\N
8917	947	3	7103	15	Aromatischer Qualitätsschaumwein b.A. weiß WBZ C II	\N
8918	947	3	7104	16	Aromatischer Qualitätsschaumwein b.A. weiß WBZ C III a	\N
8919	947	3	7105	17	Aromatischer Qualitätsschaumwein b.A. weiß WBZ C III b	\N
8920	947	3	7106	19	Aromatischer Qualitätsschaumwein weiß Drittländer	\N
8921	947	3	7107	20	Aromatischer Qualitätsschaumwein b.A. rot WBZ unbekannt	\N
8922	947	3	7108	21	Aromatischer Qualitätsschaumwein b.A. rot WBZ A	\N
8923	947	3	7109	22	Aromatischer Qualitätsschaumwein b.A. rot WBZ B	\N
8924	947	3	7110	23	Aromatischer Qualitätsschaumwein b.A. rot WBZ C I a	\N
8925	947	3	7111	24	Aromatischer Qualitätsschaumwein b.A. rot WBZ C I b	\N
8926	947	3	7112	25	Aromatischer Qualitätsschaumwein b.A. rot WBZ C II	\N
8927	947	3	7113	26	Aromatischer Qualitätsschaumwein b.A. rot WBZ C III a	\N
8928	947	3	7114	27	Aromatischer Qualitätsschaumwein b.A. rot WBZ C III b	\N
8929	947	3	7115	29	Aromatischer Qualitätsschaumwein rot Drittländer	\N
8930	947	3	7116	30	Aromatischer Qualitätsschaumwein b.A. rosé WBZ unbekannt	\N
8931	947	3	7117	31	Aromatischer Qualitätsschaumwein b.A. rosé WBZ A	\N
8932	947	3	7118	32	Aromatischer Qualitätsschaumwein b.A. rosé WBZ B	\N
8933	947	3	7119	33	Aromatischer Qualitätsschaumwein b.A. rosé WBZ C I a	\N
8934	947	3	7120	34	Aromatischer Qualitätsschaumwein b.A. rosé WBZ C I b	\N
8935	947	3	7121	35	Aromatischer Qualitätsschaumwein b.A. rosé WBZ C II	\N
8936	947	3	7122	36	Aromatischer Qualitätsschaumwein b.A. rosé WBZ C III a	\N
8937	947	3	7123	37	Aromatischer Qualitätsschaumwein b.A. rosé WBZ C III b	\N
8938	947	3	7124	39	Aromatischer Qualitätsschaumwein rosé Drittländer	\N
8939	948	3	7126	10	Unfertiger Wein nicht differenzierbar auch Jungwein weiß WBZ unbekannt	\N
8940	948	3	7127	11	Unfertiger Wein nicht differenzierbar auch Jungwein weiß WBZ A	\N
8941	948	3	7128	12	Unfertiger Wein nicht differenzierbar auch Jungwein weiß WBZ B	\N
8942	948	3	7129	13	Unfertiger Wein nicht differenzierbar auch Jungwein weiß WBZ C I a	\N
8943	948	3	7130	14	Unfertiger Wein nicht differenzierbar auch Jungwein weiß WBZ C I b	\N
8944	948	3	7131	15	Unfertiger Wein nicht differenzierbar auch Jungwein weiß WBZ C II	\N
8945	948	3	7132	16	Unfertiger Wein nicht differenzierbar auch Jungwein weiß WBZ C III a	\N
8946	948	3	7133	17	Unfertiger Wein nicht differenzierbar auch Jungwein weiß WBZ C III b	\N
8947	948	3	7134	19	Unfertiger Wein nicht differenzierbar auch Jungwein weiß Drittländer	\N
8948	948	3	7135	20	Unfertiger Wein nicht differenzierbar auch Jungwein rot WBZ unbekannt	\N
8949	948	3	7136	21	Unfertiger Wein nicht differenzierbar auch Jungwein rot WBZ A	\N
8950	948	3	7137	22	Unfertiger Wein nicht differenzierbar auch Jungwein rot WBZ B	\N
8951	948	3	7138	23	Unfertiger Wein nicht differenzierbar auch Jungwein rot WBZ C I a	\N
8952	948	3	7139	24	Unfertiger Wein nicht differenzierbar auch Jungwein rot WBZ C I b	\N
8953	948	3	7140	25	Unfertiger Wein nicht differenzierbar auch Jungwein rot WBZ C II	\N
8954	948	3	7141	26	Unfertiger Wein nicht differenzierbar auch Jungwein rot WBZ C III a	\N
8955	948	3	7142	27	Unfertiger Wein nicht differenzierbar auch Jungwein rot WBZ C III b	\N
8956	948	3	7143	29	Unfertiger Wein nicht differenzierbar auch Jungwein rot Drittländer	\N
8957	948	3	7144	30	Unfertiger Wein nicht differenzierbar auch Jungwein rosé WBZ unbekannt	\N
8958	948	3	7145	31	Unfertiger Wein nicht differenzierbar auch Jungwein rosé WBZ A	\N
8959	948	3	7146	32	Unfertiger Wein nicht differenzierbar auch Jungwein rosé WBZ B	\N
8960	948	3	7147	33	Unfertiger Wein nicht differenzierbar auch Jungwein rosé WBZ C I a	\N
8961	948	3	7148	34	Unfertiger Wein nicht differenzierbar auch Jungwein rosé WBZ C I b	\N
8962	948	3	7149	35	Unfertiger Wein nicht differenzierbar auch Jungwein rosé WBZ C II	\N
8963	948	3	7150	36	Unfertiger Wein nicht differenzierbar auch Jungwein rosé WBZ C III a	\N
8964	948	3	7151	37	Unfertiger Wein nicht differenzierbar auch Jungwein rosé WBZ C III b	\N
8965	948	3	7152	39	Unfertiger Wein nicht differenzierbar auch Jungwein rosé Drittländer	\N
8966	949	3	7154	10	Zur Gewinnung von Tafelwein geeigneter Wein auch Jungwein weiß WBZ unbekannt	\N
8967	949	3	7155	11	Zur Gewinnung von Tafelwein geeigneter Wein auch Jungwein weiß WBZ A	\N
8968	949	3	7156	12	Zur Gewinnung von Tafelwein geeigneter Wein auch Jungwein weiß WBZ B	\N
8969	949	3	7157	13	Zur Gewinnung von Tafelwein geeigneter Wein auch Jungwein weiß WBZ C I a	\N
8970	949	3	7158	14	Zur Gewinnung von Tafelwein geeigneter Wein auch Jungwein weiß WBZ C I b	\N
8971	949	3	7159	15	Zur Gewinnung von Tafelwein geeigneter Wein auch Jungwein weiß WBZ C II	\N
8972	949	3	7160	16	Zur Gewinnung von Tafelwein geeigneter Wein auch Jungwein weiß WBZ C III a	\N
8973	949	3	7161	17	Zur Gewinnung von Tafelwein geeigneter Wein auch Jungwein weiß WBZ C III b	\N
8974	949	3	7162	19	Zur Gewinnung von Tafelwein geeigneter Wein auch Jungwein weiß Drittländer	\N
8975	949	3	7163	20	Zur Gewinnung von Tafelwein geeigneter Wein auch Jungwein rot WBZ unbekannt	\N
8976	949	3	7164	21	Zur Gewinnung von Tafelwein geeigneter Wein auch Jungwein rot WBZ A	\N
8977	949	3	7165	22	Zur Gewinnung von Tafelwein geeigneter Wein auch Jungwein rot WBZ B	\N
8978	949	3	7166	23	Zur Gewinnung von Tafelwein geeigneter Wein auch Jungwein rot WBZ C I a	\N
8979	949	3	7167	24	Zur Gewinnung von Tafelwein geeigneter Wein auch Jungwein rot WBZ C I b	\N
8980	949	3	7168	25	Zur Gewinnung von Tafelwein geeigneter Wein auch Jungwein rot WBZ C II	\N
8981	949	3	7169	26	Zur Gewinnung von Tafelwein geeigneter Wein auch Jungwein rot WBZ C III a	\N
8982	949	3	7170	27	Zur Gewinnung von Tafelwein geeigneter Wein auch Jungwein rot WBZ C III b	\N
8983	949	3	7171	29	Zur Gewinnung von Tafelwein geeigneter Wein auch Jungwein rot Drittländer	\N
8984	949	3	7172	30	Zur Gewinnung von Tafelwein geeigneter Wein auch Jungwein rosé WBZ unbekannt	\N
8985	949	3	7173	31	Zur Gewinnung von Tafelwein geeigneter Wein auch Jungwein rosé WBZ A	\N
8986	949	3	7174	32	Zur Gewinnung von Tafelwein geeigneter Wein auch Jungwein rosé WBZ B	\N
8987	949	3	7175	33	Zur Gewinnung von Tafelwein geeigneter Wein auch Jungwein rosé WBZ C I a	\N
8988	949	3	7176	34	Zur Gewinnung von Tafelwein geeigneter Wein auch Jungwein rosé WBZ C I b	\N
8989	949	3	7177	35	Zur Gewinnung von Tafelwein geeigneter Wein auch Jungwein rosé WBZ C II	\N
8990	949	3	7178	36	Zur Gewinnung von Tafelwein geeigneter Wein auch Jungwein rosé WBZ C III a	\N
8991	949	3	7179	37	Zur Gewinnung von Tafelwein geeigneter Wein auch Jungwein rosé WBZ C III b	\N
8992	949	3	7180	39	Zur Gewinnung von Tafelwein geeigneter Wein auch Jungwein rosé Drittländer	\N
8993	950	3	7182	10	Zur Gewinnung von Landwein geeigneter Wein auch Jungwein weiß WBZ unbekannt	\N
8994	950	3	7183	11	Zur Gewinnung von Landwein geeigneter Wein auch Jungwein weiß WBZ A	\N
8995	950	3	7184	12	Zur Gewinnung von Landwein geeigneter Wein auch Jungwein weiß WBZ B	\N
8996	950	3	7185	13	Zur Gewinnung von Landwein geeigneter Wein auch Jungwein weiß WBZ C I a	\N
8997	950	3	7186	14	Zur Gewinnung von Landwein geeigneter Wein auch Jungwein weiß WBZ C I b	\N
8998	950	3	7187	15	Zur Gewinnung von Landwein geeigneter Wein auch Jungwein weiß WBZ C II	\N
8999	950	3	7188	16	Zur Gewinnung von Landwein geeigneter Wein auch Jungwein weiß WBZ C III a	\N
9000	950	3	7189	17	Zur Gewinnung von Landwein geeigneter Wein auch Jungwein weiß WBZ C III b	\N
9001	950	3	7190	19	Zur Gewinnung von Landwein geeigneter Wein auch Jungwein weiß Drittländer	\N
9002	950	3	7191	20	Zur Gewinnung von Landwein geeigneter Wein auch Jungwein rot WBZ unbekannt	\N
9003	950	3	7192	21	Zur Gewinnung von Landwein geeigneter Wein auch Jungwein rot WBZ A	\N
9004	950	3	7193	22	Zur Gewinnung von Landwein geeigneter Wein auch Jungwein rot WBZ B	\N
9005	950	3	7194	23	Zur Gewinnung von Landwein geeigneter Wein auch Jungwein rot WBZ C I a	\N
9006	950	3	7195	24	Zur Gewinnung von Landwein geeigneter Wein auch Jungwein rot WBZ C I b	\N
9007	950	3	7196	25	Zur Gewinnung von Landwein geeigneter Wein auch Jungwein rot WBZ C II	\N
9008	950	3	7197	26	Zur Gewinnung von Landwein geeigneter Wein auch Jungwein rot WBZ C III a	\N
9009	950	3	7198	27	Zur Gewinnung von Landwein geeigneter Wein auch Jungwein rot WBZ C III b	\N
9010	950	3	7199	29	Zur Gewinnung von Landwein geeigneter Wein auch Jungwein rot Drittländer	\N
9011	950	3	7200	30	Zur Gewinnung von Landwein geeigneter Wein auch Jungwein rosé WBZ unbekannt	\N
9012	950	3	7201	31	Zur Gewinnung von Landwein geeigneter Wein auch Jungwein rosé WBZ A	\N
9013	950	3	7202	32	Zur Gewinnung von Landwein geeigneter Wein auch Jungwein rosé WBZ B	\N
9014	950	3	7203	33	Zur Gewinnung von Landwein geeigneter Wein auch Jungwein rosé WBZ C I a	\N
9015	950	3	7204	34	Zur Gewinnung von Landwein geeigneter Wein auch Jungwein rosé WBZ C I b	\N
9016	950	3	7205	35	Zur Gewinnung von Landwein geeigneter Wein auch Jungwein rosé WBZ C II	\N
9017	950	3	7206	36	Zur Gewinnung von Landwein geeigneter Wein auch Jungwein rosé WBZ C III a	\N
9018	950	3	7207	37	Zur Gewinnung von Landwein geeigneter Wein auch Jungwein rosé WBZ C III b	\N
9019	950	3	7208	39	Zur Gewinnung von Landwein geeigneter Wein auch Jungwein rosé Drittländer	\N
9020	951	3	7210	10	Zur Gewinnung von Qualitätswein geeigneter Wein auch Jungwein weiß WBZ unbekannt	\N
9021	951	3	7211	11	Zur Gewinnung von Qualitätswein geeigneter Wein auch Jungwein weiß WBZ A	\N
9022	951	3	7212	12	Zur Gewinnung von Qualitätswein geeigneter Wein auch Jungwein weiß WBZ B	\N
9023	951	3	7213	13	Zur Gewinnung von Qualitätswein geeigneter Wein auch Jungwein weiß WBZ C I a	\N
9024	951	3	7214	14	Zur Gewinnung von Qualitätswein geeigneter Wein auch Jungwein weiß WBZ C I b	\N
9025	951	3	7215	15	Zur Gewinnung von Qualitätswein geeigneter Wein auch Jungwein weiß WBZ C II	\N
9026	951	3	7216	16	Zur Gewinnung von Qualitätswein geeigneter Wein auch Jungwein weiß WBZ C III a	\N
9027	951	3	7217	17	Zur Gewinnung von Qualitätswein geeigneter Wein auch Jungwein weiß WBZ C III b	\N
9028	951	3	7218	19	Zur Gewinnung von Qualitätswein geeigneter Wein auch Jungwein weiß Drittländer	\N
9029	951	3	7219	20	Zur Gewinnung von Qualitätswein geeigneter Wein auch Jungwein rot WBZ unbekannt	\N
9030	951	3	7220	21	Zur Gewinnung von Qualitätswein geeigneter Wein auch Jungwein rot WBZ A	\N
9031	951	3	7221	22	Zur Gewinnung von Qualitätswein geeigneter Wein auch Jungwein rot WBZ B	\N
9032	951	3	7222	23	Zur Gewinnung von Qualitätswein geeigneter Wein auch Jungwein rot WBZ C I a	\N
9033	951	3	7223	24	Zur Gewinnung von Qualitätswein geeigneter Wein auch Jungwein rot WBZ C I b	\N
9034	951	3	7224	25	Zur Gewinnung von Qualitätswein geeigneter Wein auch Jungwein rot WBZ C II	\N
9035	951	3	7225	26	Zur Gewinnung von Qualitätswein geeigneter Wein auch Jungwein rot WBZ C III a	\N
9036	951	3	7226	27	Zur Gewinnung von Qualitätswein geeigneter Wein auch Jungwein rot WBZ C III b	\N
9037	951	3	7227	29	Zur Gewinnung von Qualitätswein geeigneter Wein auch Jungwein rot Drittländer	\N
9325	989	3	7553	1	Erdbeerperlwein	\N
9038	951	3	7228	30	Zur Gewinnung von Qualitätswein geeigneter Wein auch Jungwein rosé WBZ unbekannt	\N
9039	951	3	7229	31	Zur Gewinnung von Qualitätswein geeigneter Wein auch Jungwein rosé WBZ A	\N
9040	951	3	7230	32	Zur Gewinnung von Qualitätswein geeigneter Wein auch Jungwein rosé WBZ B	\N
9041	951	3	7231	33	Zur Gewinnung von Qualitätswein geeigneter Wein auch Jungwein rosé WBZ C I a	\N
9042	951	3	7232	34	Zur Gewinnung von Qualitätswein geeigneter Wein auch Jungwein rosé WBZ C I b	\N
9043	951	3	7233	35	Zur Gewinnung von Qualitätswein geeigneter Wein auch Jungwein rosé WBZ C II	\N
9044	951	3	7234	36	Zur Gewinnung von Qualitätswein geeigneter Wein auch Jungwein rosé WBZ C IIIa	\N
9045	951	3	7235	37	Zur Gewinnung von Qualitätswein geeigneter Wein auch Jungwein rosé WBZ C IIIb	\N
9046	951	3	7236	39	Zur Gewinnung von Qualitätswein geeigneter Wein auch Jungwein rosé Drittländer	\N
9047	952	3	7238	10	Zur Gewinnung von Kabinett geeigneter Wein auch Jungwein weiß WBZ unbekannt	\N
9048	952	3	7239	11	Zur Gewinnung von Kabinett geeigneter Wein auch Jungwein weiß WBZ A	\N
9049	952	3	7240	12	Zur Gewinnung von Kabinett geeigneter Wein auch Jungwein weiß WBZ B	\N
9050	952	3	7241	20	Zur Gewinnung von Kabinett geeigneter Wein auch Jungwein rot WBZ unbekannt	\N
9051	952	3	7242	21	Zur Gewinnung von Kabinett geeigneter Wein auch Jungwein rot WBZ A	\N
9052	952	3	7243	22	Zur Gewinnung von Kabinett geeigneter Wein auch Jungwein rot WBZ B	\N
9053	952	3	7244	30	Zur Gewinnung von Kabinett geeigneter Wein auch Jungwein rosé WBZ unbekannt	\N
9054	952	3	7245	31	Zur Gewinnung von Kabinett geeigneter Wein auch Jungwein rosé WBZ A	\N
9055	952	3	7246	32	Zur Gewinnung von Kabinett geeigneter Wein auch Jungwein rosé WBZ B	\N
9056	953	3	7248	10	Zur Gewinnung von Spätlese geeigneter Wein auch Jungwein weiß WBZ unbekannt	\N
9057	953	3	7249	11	Zur Gewinnung von Spätlese geeigneter Wein auch Jungwein weiß WBZ A	\N
9058	953	3	7250	12	Zur Gewinnung von Spätlese geeigneter Wein auch Jungwein weiß WBZ B	\N
9059	953	3	7251	20	Zur Gewinnung von Spätlese geeigneter Wein auch Jungwein rot WBZ unbekannt	\N
9060	953	3	7252	21	Zur Gewinnung von Spätlese geeigneter Wein auch Jungwein rot WBZ A	\N
9061	953	3	7253	22	Zur Gewinnung von Spätlese geeigneter Wein auch Jungwein rot WBZ B	\N
9062	953	3	7254	30	Zur Gewinnung von Spätlese geeigneter Wein auch Jungwein rosé WBZ unbekannt	\N
9063	953	3	7255	31	Zur Gewinnung von Spätlese geeigneter Wein auch Jungwein rosé WBZ A	\N
9064	953	3	7256	32	Zur Gewinnung von Spätlese geeigneter Wein auch Jungwein rosé WBZ B	\N
9065	954	3	7258	10	Zur Gewinnung von Auslese geeigneter Wein auch Jungwein weiß WBZ unbekannt	\N
9066	954	3	7259	11	Zur Gewinnung von Auslese geeigneter Wein auch Jungwein weiß WBZ A	\N
9067	954	3	7260	12	Zur Gewinnung von Auslese geeigneter Wein auch Jungwein weiß WBZ B	\N
9068	954	3	7261	20	Zur Gewinnung von Auslese geeigneter Wein auch Jungwein rot WBZ unbekannt	\N
9069	954	3	7262	21	Zur Gewinnung von Auslese geeigneter Wein auch Jungwein rot WBZ A	\N
9070	954	3	7263	22	Zur Gewinnung von Auslese geeigneter Wein auch Jungwein rot WBZ B	\N
9071	954	3	7264	30	Zur Gewinnung von Auslese geeigneter Wein auch Jungwein rosé WBZ unbekannt	\N
9072	954	3	7265	31	Zur Gewinnung von Auslese geeigneter Wein auch Jungwein rosé WBZ A	\N
9073	954	3	7266	32	Zur Gewinnung von Auslese geeigneter Wein auch Jungwein rosé WBZ B	\N
9074	955	3	7268	10	Zur Gewinnung von Beerenauslese geeigneter Wein auch Jungwein weiß WBZ unbekannt	\N
9075	955	3	7269	11	Zur Gewinnung von Beerenauslese geeigneter Wein auch Jungwein weiß WBZ A	\N
9076	955	3	7270	12	Zur Gewinnung von Beerenauslese geeigneter Wein auch Jungwein weiß WBZ B	\N
9077	955	3	7271	13	Zur Gewinnung von Beerenauslese geeigneter Wein auch Jungwein rot WBZ unbekannt	\N
9078	956	3	7273	10	Zur Gewinnung von Trockenbeerenauslese geeigneter Wein auch Jungwein weiß WBZ unbekannt	\N
9079	956	3	7274	11	Zur Gewinnung von Trockenbeerenauslese geeigneter Wein auch Jungwein weiß WBZ A	\N
9080	956	3	7275	12	Zur Gewinnung von Trockenbeerenauslese geeigneter Wein auch Jungwein weiß WBZ B	\N
9081	956	3	7276	13	Zur Gewinnung von Trockenbeerenauslese geeigneter Wein auch Jungwein rot WBZ unbekannt	\N
9082	957	3	7278	10	Zur Gewinnung von Eiswein geeigneter Wein auch Jungwein weiß WBZ unbekannt	\N
9083	957	3	7279	11	Zur Gewinnung von Eiswein geeigneter Wein auch Jungwein weiß WBZ A	\N
9084	957	3	7280	12	Zur Gewinnung von Eiswein geeigneter Wein auch Jungwein weiß WBZ B	\N
9085	957	3	7281	13	Zur Gewinnung von Eiswein geeigneter Wein auch Jungwein rot WBZ unbekannt	\N
9086	958	3	7283	10	Traubenmost und Traubenmaische nicht weiter differenzierbar weiß WBZ unbekannt	\N
9087	958	3	7284	11	Traubenmost und Traubenmaische nicht weiter differenzierbar weiß WBZ A	\N
9088	958	3	7285	12	Traubenmost und Traubenmaische nicht weiter differenzierbar weiß WBZ B	\N
9089	958	3	7286	13	Traubenmost und Traubenmaische nicht weiter differenzierbar weiß WBZ C I a	\N
9090	958	3	7287	14	Traubenmost und Traubenmaische nicht weiter differenzierbar weiß WBZ C I b	\N
9091	958	3	7288	15	Traubenmost und Traubenmaische nicht weiter differenzierbar weiß WBZ C II	\N
9092	958	3	7289	16	Traubenmost und Traubenmaische nicht weiter differenzierbar weiß WBZ C III a	\N
9093	958	3	7290	17	Traubenmost und Traubenmaische nicht weiter differenzierbar weiß WBZ C III b	\N
9094	958	3	7291	19	Traubenmost und Traubenmaische nicht weiter differenzierbar weiß Drittländer	\N
9095	958	3	7292	20	Traubenmost und Traubenmaische nicht weiter differenzierbar rot WBZ unbekannt	\N
9096	958	3	7293	21	Traubenmost und Traubenmaische nicht weiter differenzierbar rot WBZ A	\N
9097	958	3	7294	22	Traubenmost und Traubenmaische nicht weiter differenzierbar rot WBZ B	\N
9098	958	3	7295	23	Traubenmost und Traubenmaische nicht weiter differenzierbar rot WBZ C I a	\N
9099	958	3	7296	24	Traubenmost und Traubenmaische nicht weiter differenzierbar rot WBZ C I b	\N
9100	958	3	7297	25	Traubenmost und Traubenmaische nicht weiter differenzierbar rot WBZ C II	\N
9101	958	3	7298	26	Traubenmost und Traubenmaische nicht weiter differenzierbar rot WBZ C III a	\N
9102	958	3	7299	27	Traubenmost und Traubenmaische nicht weiter differenzierbar rot WBZ C III b	\N
9103	958	3	7300	29	Traubenmost und Traubenmaische nicht weiter differenzierbar rot Drittländer	\N
9104	958	3	7301	30	Traubenmost und Traubenmaische nicht weiter differenzierbar rosé WBZ unbekannt	\N
9105	958	3	7302	31	Traubenmost und Traubenmaische nicht weiter differenzierbar rosé WBZ A	\N
9106	958	3	7303	32	Traubenmost und Traubenmaische nicht weiter differenzierbar rosé WBZ B	\N
9107	958	3	7304	33	Traubenmost und Traubenmaische nicht weiter differenzierbar rosé WBZ C I a	\N
9108	958	3	7305	34	Traubenmost und Traubenmaische nicht weiter differenzierbar rosé WBZ C I b	\N
9109	958	3	7306	35	Traubenmost und Traubenmaische nicht weiter differenzierbar rosé WBZ C II	\N
9110	958	3	7307	36	Traubenmost und Traubenmaische nicht weiter differenzierbar rosé WBZ C III a	\N
9111	958	3	7308	37	Traubenmost und Traubenmaische nicht weiter differenzierbar rosé WBZ C III b	\N
9112	958	3	7309	39	Traubenmost und Traubenmaische nicht weiter differenzierbar rosé Drittländer	\N
9113	959	3	7311	10	Zur Herstellung von Tafelwein geeigneter/e Traubenmost bzw. -maische weiß WBZ unbekannt	\N
9114	959	3	7312	11	Zur Herstellung von Tafelwein geeigneter/e Traubenmost bzw. -maische weiß WBZ A	\N
9115	959	3	7313	12	Zur Herstellung von Tafelwein geeigneter/e Traubenmost bzw. -maische weiß WBZ B	\N
9116	959	3	7314	13	Zur Herstellung von Tafelwein geeigneter/e Traubenmost bzw. -maische weiß WBZ C I a	\N
9117	959	3	7315	14	Zur Herstellung von Tafelwein geeigneter/e Traubenmost bzw. -maische weiß WBZ C I b	\N
9118	959	3	7316	15	Zur Herstellung von Tafelwein geeigneter/e Traubenmost bzw. -maische weiß WBZ C II	\N
9119	959	3	7317	16	Zur Herstellung von Tafelwein geeigneter/e Traubenmost bzw. -maische weiß WBZ C III a	\N
9120	959	3	7318	17	Zur Herstellung von Tafelwein geeigneter/e Traubenmost bzw. -maische weiß WBZ C III b	\N
9121	959	3	7319	19	Zur Herstellung von Tafelwein geeigneter/e Traubenmost bzw. -maische weiß Drittländer	\N
9122	959	3	7320	20	Zur Herstellung von Tafelwein geeigneter/e Traubenmost bzw. -maische rot WBZ unbekannt	\N
9123	959	3	7321	21	Zur Herstellung von Tafelwein geeigneter/e Traubenmost bzw. -maische rot WBZ A	\N
9124	959	3	7322	22	Zur Herstellung von Tafelwein geeigneter/e Traubenmost bzw. -maische rot WBZ B	\N
9125	959	3	7323	23	Zur Herstellung von Tafelwein geeigneter/e Traubenmost bzw. -maische rot WBZ C I a	\N
9126	959	3	7324	24	Zur Herstellung von Tafelwein geeigneter/e Traubenmost bzw. -maische rot WBZ C I b	\N
9127	959	3	7325	25	Zur Herstellung von Tafelwein geeigneter/e Traubenmost bzw. -maische rot WBZ C II	\N
9128	959	3	7326	26	Zur Herstellung von Tafelwein geeigneter/e Traubenmost bzw. -maische rot WBZ C III a	\N
9129	959	3	7327	27	Zur Herstellung von Tafelwein geeigneter/e Traubenmost bzw. -maische rot WBZ C III b	\N
9130	959	3	7328	29	Zur Herstellung von Tafelwein geeigneter/e Traubenmost bzw. -maische rot Drittländer	\N
9131	959	3	7329	30	Zur Herstellung von Tafelwein geeigneter/e Traubenmost bzw. -maische rosé WBZ unbekannt	\N
9132	959	3	7330	31	Zur Herstellung von Tafelwein geeigneter/e Traubenmost bzw. -maische rosé WBZ A	\N
9133	959	3	7331	32	Zur Herstellung von Tafelwein geeigneter/e Traubenmost bzw. -maische rosé WBZ B	\N
9134	959	3	7332	33	Zur Herstellung von Tafelwein geeigneter/e Traubenmost bzw. -maische rosé WBZ C I a	\N
9135	959	3	7333	34	Zur Herstellung von Tafelwein geeigneter/e Traubenmost bzw. -maische rosé WBZ C I b	\N
9136	959	3	7334	35	Zur Herstellung von Tafelwein geeigneter/e Traubenmost bzw. -maische rosé WBZ C II	\N
9137	959	3	7335	36	Zur Herstellung von Tafelwein geeigneter/e Traubenmost bzw. -maische rosé WBZ C III a	\N
9138	959	3	7336	37	Zur Herstellung von Tafelwein geeigneter/e Traubenmost bzw. -maische rosé WBZ C III b	\N
9139	959	3	7337	39	Zur Herstellung von Tafelwein geeigneter/e Traubenmost bzw. -maische rosé Drittländer	\N
9140	960	3	7339	10	Zur Herstellung von Landwein geeigneter/e Traubenmost bzw. -maische weiß WBZ unbekannt	\N
9141	960	3	7340	11	Zur Herstellung von Landwein geeigneter/e Traubenmost bzw. -maische weiß WBZ A	\N
9142	960	3	7341	12	Zur Herstellung von Landwein geeigneter/e Traubenmost bzw. -maische weiß WBZ B	\N
9143	960	3	7342	13	Zur Herstellung von Landwein geeigneter/e Traubenmost bzw. -maische weiß WBZ C I a	\N
9144	960	3	7343	14	Zur Herstellung von Landwein geeigneter/e Traubenmost bzw. -maische weiß WBZ C I b	\N
9145	960	3	7344	15	Zur Herstellung von Landwein geeigneter/e Traubenmost bzw. -maische weiß WBZ C II	\N
9146	960	3	7345	16	Zur Herstellung von Landwein geeigneter/e Traubenmost bzw. -maische weiß WBZ C III a	\N
9147	960	3	7346	17	Zur Herstellung von Landwein geeigneter/e Traubenmost bzw. -maische weiß WBZ C III b	\N
9148	960	3	7347	19	Zur Herstellung von Landwein geeigneter/e Traubenmost bzw. -maische weiß Drittländer	\N
9149	960	3	7348	20	Zur Herstellung von Landwein geeigneter/e Traubenmost bzw. -maische rot WBZ unbekannt	\N
9150	960	3	7349	21	Zur Herstellung von Landwein geeigneter/e Traubenmost bzw. -maische rot WBZ A	\N
9151	960	3	7350	22	Zur Herstellung von Landwein geeigneter/e Traubenmost bzw. -maische rot WBZ B	\N
9152	960	3	7351	23	Zur Herstellung von Landwein geeigneter/e Traubenmost bzw. -maische rot WBZ C I a	\N
9153	960	3	7352	24	Zur Herstellung von Landwein geeigneter/e Traubenmost bzw. -maische rot WBZ C I b	\N
9154	960	3	7353	25	Zur Herstellung von Landwein geeigneter/e Traubenmost bzw. -maische rot WBZ C II	\N
9155	960	3	7354	26	Zur Herstellung von Landwein geeigneter/e Traubenmost bzw. -maische rot WBZ C III a	\N
9156	960	3	7355	27	Zur Herstellung von Landwein geeigneter/e Traubenmost bzw. -maische rot WBZ C III b	\N
9157	960	3	7356	29	Zur Herstellung von Landwein geeigneter/e Traubenmost bzw. -maische rot Drittländer	\N
9158	960	3	7357	30	Zur Herstellung von Landwein geeigneter/e Traubenmost bzw. -maische rosé WBZ unbekannt	\N
9159	960	3	7358	31	Zur Herstellung von Landwein geeigneter/e Traubenmost bzw. -maische rosé WBZ A	\N
9160	960	3	7359	32	Zur Herstellung von Landwein geeigneter/e Traubenmost bzw. -maische rosé WBZ B	\N
9161	960	3	7360	33	Zur Herstellung von Landwein geeigneter/e Traubenmost bzw. -maische rosé WBZ C I a	\N
9162	960	3	7361	34	Zur Herstellung von Landwein geeigneter/e Traubenmost bzw. -maische rosé WBZ C I b	\N
9163	960	3	7362	35	Zur Herstellung von Landwein geeigneter/e Traubenmost bzw. -maische rosé WBZ C II	\N
9164	960	3	7363	36	Zur Herstellung von Landwein geeigneter/e Traubenmost bzw. -maische rosé WBZ C III a	\N
9326	989	3	7554	2	Heidelbeerperlwein	\N
9165	960	3	7364	37	Zur Herstellung von Landwein geeigneter/e Traubenmost bzw. -maische rosé WBZ C III b	\N
9166	960	3	7365	39	Zur Herstellung von Landwein geeigneter/e Traubenmost bzw. -maische rosé Drittländer	\N
9167	961	3	7367	10	Zur Herstellung von Qualitätswein b.A. geeigneter/e Traubenmost bzw. -maische weiß WBZ unbekannt	\N
9168	961	3	7368	11	Zur Herstellung von Qualitätswein b.A. geeigneter/e Traubenmost bzw. -maische weiß WBZ A	\N
9169	961	3	7369	12	Zur Herstellung von Qualitätswein b.A. geeigneter/e Traubenmost bzw. -maische weiß WBZ B	\N
9170	961	3	7370	13	Zur Herstellung von Qualitätswein b.A. geeigneter/e Traubenmost bzw. -maische weiß WBZ C I a	\N
9171	961	3	7371	14	Zur Herstellung von Qualitätswein b.A. geeigneter/e Traubenmost bzw. -maische weiß WBZ C I b	\N
9172	961	3	7372	15	Zur Herstellung von Qualitätswein b.A. geeigneter/e Traubenmost bzw. -maische weiß WBZ C II	\N
9173	961	3	7373	16	Zur Herstellung von Qualitätswein b.A. geeigneter/e Traubenmost bzw. -maische weiß WBZ C III a	\N
9174	961	3	7374	17	Zur Herstellung von Qualitätswein b.A. geeigneter/e Traubenmost bzw. -maische weiß WBZ C III b	\N
9175	961	3	7375	19	Zur Herstellung von Qualitätswein b.A. geeigneter/e Traubenmost bzw. -maische weiß Drittländer	\N
9176	961	3	7376	20	Zur Herstellung von Qualitätswein b.A. geeigneter/e Traubenmost bzw. -maische rot WBZ unbekannt	\N
9177	961	3	7377	21	Zur Herstellung von Qualitätswein b.A. geeigneter/e Traubenmost bzw. -maische rot WBZ A	\N
9178	961	3	7378	22	Zur Herstellung von Qualitätswein b.A. geeigneter/e Traubenmost bzw. -maische rot WBZ B	\N
9179	961	3	7379	23	Zur Herstellung von Qualitätswein b.A. geeigneter/e Traubenmost bzw. -maische rot WBZ C I a	\N
9180	961	3	7380	24	Zur Herstellung von Qualitätswein b.A. geeigneter/e Traubenmost bzw. -maische rot WBZ C I b	\N
9181	961	3	7381	25	Zur Herstellung von Qualitätswein b.A. geeigneter/e Traubenmost bzw. -maische rot WBZ C II	\N
9182	961	3	7382	26	Zur Herstellung von Qualitätswein b.A. geeigneter/e Traubenmost bzw. -maische rot WBZ C III a	\N
9183	961	3	7383	27	Zur Herstellung von Qualitätswein b.A. geeigneter/e Traubenmost bzw. -maische rot WBZ C III b	\N
9184	961	3	7384	29	Zur Herstellung von Qualitätswein b.A. geeigneter/e Traubenmost bzw. -maische rot Drittländer	\N
9185	961	3	7385	30	Zur Herstellung von Qualitätswein b.A. geeigneter/e Traubenmost bzw. -maische rosé WBZ unbekannt	\N
9186	961	3	7386	31	Zur Herstellung von Qualitätswein b.A. geeigneter/e Traubenmost bzw. -maische rosé WBZ A	\N
9187	961	3	7387	32	Zur Herstellung von Qualitätswein b.A. geeigneter/e Traubenmost bzw. -maische rosé WBZ B	\N
9188	961	3	7388	33	Zur Herstellung von Qualitätswein b.A. geeigneter/e Traubenmost bzw. -maische rosé WBZ C I a	\N
9189	961	3	7389	34	Zur Herstellung von Qualitätswein b.A. geeigneter/e Traubenmost bzw. -maische rosé WBZ C I b	\N
9190	961	3	7390	35	Zur Herstellung von Qualitätswein b.A. geeigneter/e Traubenmost bzw. -maische rosé WBZ C II	\N
9191	961	3	7391	36	Zur Herstellung von Qualitätswein b.A. geeigneter/e Traubenmost bzw. -maische rosé WBZ C III a	\N
9192	961	3	7392	37	Zur Herstellung von Qualitätswein b.A. geeigneter/e Traubenmost bzw. -maische rosé WBZ C III b	\N
9193	961	3	7393	39	Zur Herstellung von Qualitätswein b.A. geeigneter/e Traubenmost bzw. -maische rosé Drittländer	\N
9194	962	3	7395	10	Zur Herstellung von Kabinett geeigneter/e Traubenmost bzw. -maische weiß WBZ unbekannt	\N
9195	962	3	7396	11	Zur Herstellung von Kabinett geeigneter/e Traubenmost bzw. -maische weiß WBZ A	\N
9196	962	3	7397	12	Zur Herstellung von Kabinett geeigneter/e Traubenmost bzw. -maische weiß WBZ B	\N
9197	962	3	7398	20	Zur Herstellung von Kabinett geeigneter/e Traubenmost bzw. -maische rot WBZ unbekannt	\N
9198	962	3	7399	21	Zur Herstellung von Kabinett geeigneter/e Traubenmost bzw. -maische rot WBZ A	\N
9199	962	3	7400	22	Zur Herstellung von Kabinett geeigneter/e Traubenmost bzw. -maische rot WBZ B	\N
9200	962	3	7401	30	Zur Herstellung von Kabinett geeigneter/e Traubenmost bzw. -maische rosé WBZ unbekannt	\N
9201	962	3	7402	31	Zur Herstellung von Kabinett geeigneter/e Traubenmost bzw. -maische rosé WBZ A	\N
9202	962	3	7403	32	Zur Herstellung von Kabinett geeigneter/e Traubenmost bzw. -maische rosé WBZ B	\N
9203	963	3	7405	10	Zur Herstellung von Spätlese geeigneter/e Traubenmost bzw. -maische, weiß, WBZ unbekannt	\N
9204	963	3	7406	11	Zur Herstellung von Spätlese geeigneter/e Traubenmost bzw. -maische weiß WBZ A	\N
9205	963	3	7407	12	Zur Herstellung von Spätlese geeigneter/e Traubenmost bzw. -maische weiß WBZ B	\N
9206	963	3	7408	20	Zur Herstellung von Spätlese geeigneter/e Traubenmost bzw. -maische rot WBZ unbekannt	\N
9207	963	3	7409	21	Zur Herstellung von Spätlese geeigneter/e Traubenmost bzw. -maische rot WBZ A	\N
9208	963	3	7410	22	Zur Herstellung von Spätlese geeigneter/e Traubenmost bzw. -maische rot WBZ B	\N
9209	963	3	7411	30	Zur Herstellung von Spätlese geeigneter/e Traubenmost bzw. -maische rosé WBZ unbekannt	\N
9210	963	3	7412	31	Zur Herstellung von Spätlese geeigneter/e Traubenmost bzw. -maische rosé WBZ A	\N
9211	963	3	7413	32	Zur Herstellung von Spätlese geeigneter/e Traubenmost bzw. -maische rosé WBZ B	\N
9212	964	3	7415	10	Zur Herstellung von Auslese geeigneter/e Traubenmost bzw. -maische weiß WBZ unbekannt	\N
9213	964	3	7416	11	Zur Herstellung von Auslese geeigneter/e Traubenmost bzw. -maische weiß WBZ A	\N
9214	964	3	7417	12	Zur Herstellung von Auslese geeigneter/e Traubenmost bzw. -maische weiß WBZ B	\N
9215	964	3	7418	20	Zur Herstellung von Auslese geeigneter/e Traubenmost bzw. -maische rot WBZ unbekannt	\N
9216	964	3	7419	21	Zur Herstellung von Auslese geeigneter/e Traubenmost bzw. -maische rot WBZ A	\N
9217	964	3	7420	22	Zur Herstellung von Auslese geeigneter/e Traubenmost bzw. -maische rot WBZ B	\N
9218	964	3	7421	30	Zur Herstellung von Auslese geeigneter/e Traubenmost bzw. -maische rosé WBZ unbekannt	\N
9219	964	3	7422	31	Zur Herstellung von Auslese geeigneter/e Traubenmost bzw. -maische rosé WBZ A	\N
9220	964	3	7423	32	 Zur Herstellung von Auslese geeigneter/e Traubenmost bzw. -maische rosé WBZ B	\N
9221	965	3	7425	10	Zur Herstellung von Beerenauslese geeigneter/e Traubenmost bzw. -maische weiß WBZ unbekannt	\N
9222	965	3	7426	11	Zur Herstellung von Beerenauslese geeigneter/e Traubenmost bzw. -maische weiß WBZ A	\N
9327	989	3	7555	3	Himbeerperlwein	\N
9223	965	3	7427	12	Zur Herstellung von Beerenauslese geeigneter/e Traubenmost bzw. -maische weiß WBZ B	\N
9224	965	3	7428	13	Zur Herstellung von Beerenauslese geeigneter/e Traubenmost bzw. -maische rot WBZ unbekannt	\N
9225	966	3	7430	10	Zur Herst. von Trockenbeerenauslese geeigneter/e Traubenmost bzw. -maischeweiß WBZ unbekannt	\N
9226	966	3	7431	11	Zur Herst. von Trockenbeerenauslese geeigneter/e Traubenmost bzw. -maische weiß WBZ A	\N
9227	966	3	7432	12	Zur Herst. von Trockenbeerenauslese geeigneter/e Traubenmost bzw. -maische weiß WBZ B	\N
9228	966	3	7433	13	Zur Herstellung von Trockenbeerenauslese geeigneter/e Traubenmost bzw. maische rot WBZ unbekannt	\N
9229	967	3	7435	10	Zur Herstellung von Eiswein geeigneter/e Traubenmost bzw. -maische weiß WBZ unbekannt	\N
9230	967	3	7436	11	Zur Herstellung von Eiswein geeigneter/e Traubenmost bzw. -maische weiß WBZ A	\N
9231	967	3	7437	12	Zur Herstellung von Eiswein geeigneter/e Traubenmost bzw. -maische weiß WBZ B	\N
9232	967	3	7438	13	Zur Herstellung von Eiswein geeigneter/e Traubenmost bzw. -maische rot WBZ unbekannt	\N
9233	968	3	7440	10	Traubenmost teilweise gegoren weiß WBZ unbekannt	\N
9234	968	3	7441	11	Traubenmost teilweise gegoren weiß WBZ A	\N
9235	968	3	7442	12	Traubenmost teilweise gegoren weiß WBZ B	\N
9236	968	3	7443	13	Traubenmost teilweise gegoren weiß WBZ C I a	\N
9237	968	3	7444	14	Traubenmost teilweise gegoren weiß WBZ C I b	\N
9238	968	3	7445	15	Traubenmost teilweise gegoren weiß WBZ C II	\N
9239	968	3	7446	16	Traubenmost teilweise gegoren weiß WBZ C III a	\N
9240	968	3	7447	17	Traubenmost teilweise gegoren weiß WBZ C III b	\N
9241	968	3	7448	19	Traubenmost teilweise gegoren weiß Drittländer	\N
9242	968	3	7449	20	Traubenmost teilweise gegoren rot WBZ unbekannt	\N
9243	968	3	7450	21	Traubenmost teilweise gegoren rot WBZ A	\N
9244	968	3	7451	22	Traubenmost teilweise gegoren rot WBZ B	\N
9245	968	3	7452	23	Traubenmost teilweise gegoren rot WBZ C I a	\N
9246	968	3	7453	24	Traubenmost teilweise gegoren rot WBZ C I b	\N
9247	968	3	7454	25	Traubenmost teilweise gegoren rot WBZ C II	\N
9248	968	3	7455	26	Traubenmost teilweise gegoren rot WBZ C III a	\N
9249	968	3	7456	27	Traubenmost teilweise gegoren rot WBZ C III b	\N
9250	968	3	7457	29	Traubenmost teilweise gegoren rot Drittländer	\N
9251	968	3	7458	30	Traubenmost teilweise gegoren rosé WBZ unbekannt	\N
9252	968	3	7459	31	Traubenmost teilweise gegoren rosé WBZ A	\N
9253	968	3	7460	32	Traubenmost teilweise gegoren rosé WBZ B	\N
9254	968	3	7461	33	Traubenmost teilweise gegoren rosé WBZ C I a	\N
9255	968	3	7462	34	Traubenmost teilweise gegoren rosé WBZ C I b	\N
9256	968	3	7463	35	Traubenmost teilweise gegoren rosé WBZ C II	\N
9257	968	3	7464	36	Traubenmost teilweise gegoren rosé WBZ C III a	\N
9258	968	3	7465	37	Traubenmost teilweise gegoren rosé WBZ C III b	\N
9259	968	3	7466	39	Traubenmost teilweise gegoren rosé Drittländer	\N
9260	969	3	7468	10	Traubenmost konzentriert weiß	\N
9261	969	3	7469	20	Traubenmost konzentriert rot	\N
9262	969	3	7470	30	Traubenmost konzentriert rosé	\N
9263	973	3	7476	1	Madeira	\N
9264	973	3	7477	2	Malaga	\N
9265	973	3	7478	3	Mavrodaphne	\N
9266	973	3	7479	4	Marsala	\N
9267	973	3	7480	5	Pineau de Charantes	\N
9268	973	3	7481	6	Portwein	\N
9269	973	3	7482	7	Samos	\N
9270	973	3	7483	8	Sherry	\N
9271	976	3	7487	1	Wermutwein	\N
9272	976	3	7488	2	Bitterer aromatischer Wein	\N
9273	976	3	7489	3	Aromatisierter Wein mit Ei	\N
9274	977	3	7491	1	Bitter Soda	\N
9275	977	3	7492	2	Clarea	\N
9276	977	3	7493	3	Glühwein	\N
9277	977	3	7494	4	Kalte Ente	\N
9278	977	3	7495	5	Maitrank	\N
9279	977	3	7496	6	Maiwein	\N
9280	977	3	7497	7	Sangria	\N
9281	977	3	7498	8	Zurra	\N
9282	978	3	7500	1	Aromatisierter Traubenperlmost	\N
9283	978	3	7501	2	Weincocktail	\N
9284	979	3	7503	1	Schorle	\N
9285	980	3	7505	1	Alkoholfreier Wein	\N
9286	980	3	7506	2	Alkoholreduzierter Wein	\N
9287	980	3	7507	3	Schäumendes Getränk aus alkoholfreiem Wein	\N
9288	980	3	7508	4	Schäumendes Getränk aus alkoholreduziertem Wein	\N
9289	982	3	7511	1	Keltertrauben ohne nähere Angaben	\N
9290	982	3	7512	2	Traubenmaische (nicht zur Weinbereitung)	\N
9291	982	3	7513	3	Weinhefe	\N
9292	982	3	7514	5	Weintrub	\N
9293	982	3	7515	6	Trester	\N
9294	982	3	7516	7	Tresterwein	\N
9295	982	3	7517	8	Haustrunk	\N
9296	982	3	7518	9	Essiggrundwein	\N
9297	982	3	14201	10	Keltertrauben rot	\N
9298	982	3	14202	11	Keltertrauben weiß	\N
9299	983	3	7521	1	Aprikosenwein	\N
9300	983	3	7522	2	Kirschwein	\N
9301	983	3	7523	3	Pfirsichwein	\N
9302	983	3	7524	4	Pflaumenwein	\N
9303	984	3	7526	1	Brombeerwein	\N
9304	984	3	7527	2	Erdbeerwein	\N
9305	984	3	7528	3	Heidelbeerwein	\N
9306	984	3	7529	4	Himbeerwein	\N
9307	984	3	7530	5	Johannisbeerwein	\N
9308	984	3	7531	6	Preiselbeerwein	\N
9309	984	3	7532	7	Stachelbeerwein	\N
9310	985	3	7534	1	Apfelwein	\N
9311	985	3	7535	2	Apfelwein süß vergoren	\N
9312	985	3	7536	3	Birnenwein	\N
9313	985	3	7537	4	Kernobsttischwein	\N
9314	985	3	7538	5	Most nach Landesbrauch	\N
9315	985	3	7539	6	Quittenwein	\N
9316	986	3	7541	1	Hagebuttenwein	\N
9317	986	3	7542	2	Schlehenwein	\N
9318	987	3	7544	1	Rhabarberwein	\N
9319	987	3	7545	2	Wein aus Malzauszügen	\N
9320	987	3	7546	3	Wein aus Honig	\N
9321	988	3	7548	1	Aprikosenperlwein	\N
9322	988	3	7549	2	Kirschperlwein	\N
9328	989	3	7556	4	Johannisbeerperlwein	\N
9329	989	3	7557	5	Stachelbeerperlwein	\N
9330	990	3	7559	1	Apfel-Cidre	\N
9331	990	3	7560	2	Apfelperlwein	\N
9332	990	3	7561	3	Birnen-Cidre	\N
9333	990	3	7562	4	Birnenperlwein	\N
9334	990	3	7563	5	Quittenperlwein	\N
9335	991	3	7565	1	Hagebuttenperlwein	\N
9336	991	3	7566	2	Schlehenperlwein	\N
9337	992	3	7568	1	Rhabarberperlwein	\N
9338	992	3	7569	2	Perlwein aus Malzauszügen	\N
9339	992	3	7570	3	Perlwein aus Honig	\N
9340	993	3	7572	1	Aprikosenschaumwein	\N
9341	993	3	7573	2	Kirschenschaumwein	\N
9342	993	3	7574	3	Pfirsichschaumwein	\N
9343	993	3	7575	4	Pflaumenschaumwein	\N
9344	994	3	7577	1	Erdbeerschaumwein	\N
9345	994	3	7578	2	Heidelbeerschaumwein	\N
9346	994	3	7579	3	Himbeerschaumwein	\N
9347	994	3	7580	4	Johannisbeerschaumwein	\N
9348	994	3	7581	5	Stachelbeerschaumwein	\N
9349	995	3	7583	1	Apfelschaumwein	\N
9350	995	3	7584	2	Birnenschaumwein	\N
9351	995	3	7585	3	Quittenschaumwein	\N
9352	996	3	7587	1	Hagebuttenschaumwein	\N
9353	996	3	7588	2	Schlehenschaumwein	\N
9354	997	3	7590	1	Rhabarberschaumwein	\N
9355	997	3	7591	2	Schaumwein aus Malzauszügen	\N
9356	997	3	7592	3	Schaumwein aus Honig	\N
9357	998	3	7594	1	Aprikosendessertwein	\N
9358	998	3	7595	2	Kirschdessertwein	\N
9359	998	3	7596	3	Pfirsichdessertwein	\N
9360	998	3	7597	4	Pflaumendessertwein	\N
9361	999	3	7599	1	Brombeerdessertwein	\N
9362	999	3	7600	2	Erdbeerdessertwein	\N
9363	999	3	7601	3	Heidelbeerdessertwein	\N
9364	999	3	7602	4	Himbeerdessertwein	\N
9365	999	3	7603	5	Johannisbeerdessertwein	\N
9366	999	3	7604	6	Preiselbeerdessertwein	\N
9367	999	3	7605	7	Stachelbeerdessertwein	\N
9368	1000	3	7607	1	Apfeldessertwein	\N
9369	1000	3	7608	2	Birnendessertwein	\N
9370	1000	3	7609	3	Cider	\N
9371	1000	3	7610	4	Quittendessertwein	\N
9372	1001	3	7612	1	Hagebuttendessertwein	\N
9373	1001	3	7613	2	Schlehendessertwein	\N
9374	1008	3	7622	1	Bier mit niedrigem Stammwürzegehalt hell obergärig	\N
9375	1008	3	7623	2	Bier mit niedrigem Stammwürzegehalt dunkel obergärig	\N
9376	1009	3	7625	1	Bier mit niedrigem Stammwürzegehalt hell untergärig	\N
9377	1009	3	7626	2	Bier mit niedrigem Stammwürzegehalt dunkel untergärig	\N
9378	1010	3	7628	1	Schankbier hell obergärig	\N
9379	1010	3	7629	2	Schankbier dunkel obergärig	\N
9380	1010	3	7630	3	Berliner Weiße	\N
9381	1010	3	7631	4	Schankbier hefetrüb hell obergärig	\N
9382	1010	3	7632	5	Schankbier hefetrüb dunkel obergärig	\N
9383	1011	3	7634	1	Schankbier hell untergärig	\N
9384	1011	3	7635	2	Schankbier dunkel untergärig	\N
9385	1011	3	7636	3	Schankbier Pilsner Art	\N
9386	1011	3	7637	4	Schankbier Lager Art	\N
9387	1012	3	7639	1	Altbier	\N
9388	1012	3	7640	2	Weizenbier hell	\N
9389	1012	3	7641	3	Weizenbier dunkel	\N
9390	1012	3	7642	4	Weizenbier hell Export	\N
9391	1012	3	7643	5	Weizenbier dunkel Export	\N
9392	1012	3	7644	6	Weizenbier hell spezial	\N
9393	1012	3	7645	7	Weizenbier dunkel spezial	\N
9394	1012	3	7646	8	Vollbier hell obergärig	\N
9395	1012	3	7647	9	Vollbier dunkel obergärig	\N
9396	1012	3	7648	10	Koelsch	\N
9397	1012	3	7649	11	Vollbier mit hervorhebendem Hinweis obergärig	\N
9398	1012	3	7650	12	Roggenbier	\N
9399	1012	3	7651	13	Hefeweizenbier dunkel	\N
9400	1012	3	7652	14	Hefeweizenbier hell	\N
9401	1012	3	7653	15	Dinkelbier hell	\N
9402	1012	3	7654	16	Dinkelbier dunkel	\N
9403	1013	3	7656	1	Vollbier hell untergärig	\N
9404	1013	3	7657	2	Vollbier dunkel untergärig	\N
9405	1013	3	7658	3	Vollbier Pils	\N
9406	1013	3	7659	4	Vollbier hell Export untergärig	\N
9407	1013	3	7660	5	Vollbier Pils-Export	\N
9408	1013	3	7661	6	Vollbier Märzen untergärig	\N
9409	1013	3	7662	7	Vollbier dunkel Export untergärig	\N
9410	1013	3	7663	8	Vollbier Spezialbier	\N
9411	1013	3	7665	10	Vollbier mit hervorhebendem Hinweis untergärig	\N
9412	1013	3	7666	11	Festbier	\N
9413	1013	3	7667	12	Vollbier hefetrüb untergärig	\N
9414	1013	3	7668	13	Lager	\N
9415	1013	3	7669	14	Kellerbier/Zoigl/Zwickelbier	\N
9416	1013	3	7670	15	Schwarzbier	\N
9417	1013	3	7671	16	Eisbier Icebeer	\N
9418	1014	3	7673	1	Starkbier hell obergärig	\N
9419	1014	3	7674	2	Starkbier dunkel obergärig	\N
9420	1014	3	7675	3	Starkbier hell obergärig hefetrüb	\N
9421	1014	3	7676	4	Starkbier dunkel obergärig hefetrüb	\N
9422	1014	3	7677	5	Weizen-Bock hell	\N
9423	1014	3	7678	6	Weizen-Bock dunkel	\N
9424	1014	3	7679	7	Weizen-Doppelbock hell	\N
9425	1014	3	7680	8	Weizen-Doppelbock dunkel	\N
9426	1015	3	7682	1	Starkbier hell Bock untergärig	\N
9427	1015	3	7683	2	Starkbier dunkel Bock untergärig	\N
9428	1015	3	7684	3	Starkbier hell Doppelbock untergärig	\N
9429	1015	3	7685	4	Starkbier dunkel Doppelbock untergärig	\N
9430	1015	3	7686	5	Eisbock	\N
9431	1016	3	7688	1	Malzbier	\N
9432	1016	3	7689	2	Malzbier mit Zucker	\N
9433	1016	3	13338	3	Doppelkaramel (Malzbier)	\N
9434	1017	3	7692	1	Malztrunk mit Zucker	\N
9435	1017	3	7693	2	Malzgetränk alkoholfrei	\N
9436	1017	3	7694	3	Malztrunk	\N
9437	1018	3	7696	1	Bier mit Limonade	\N
9438	1018	3	7697	2	Bier mit Schaumwein	\N
9439	1018	3	7698	3	Altbierbowle	\N
9550	1030	3	7831	17	Hagebuttenbrand	\N
9440	1018	3	7699	4	Berliner Weiße mit Schuss	\N
9441	1018	3	7700	5	Bier mit coffeinhaltiger Limonade	\N
9442	1018	3	7701	6	Bier mit Tafelwasser	\N
9443	1018	3	7702	7	Vollbier untergärig mit klarer Zitronenlimonade (1:1) "Radler"	\N
9444	1018	3	7703	8	Weizenvollbier mit klarer Zitronenlimonade (1:1) "Russ'n"	\N
9445	1018	3	7704	9	Vollbier ohne Weizenbier mit klarer Zitronenlimonade (1:1) "Alsterwasser"	\N
9446	1018	3	14143	10	Biermischgetränk mit Hanf	\N
9447	1019	3	7708	3	Schankbier hell obergärig alkoholarm	\N
9448	1019	3	7709	4	Schankbier hell obergärig alkoholfrei	\N
9449	1019	3	7710	5	Schankbier dunkel untergärig alkoholarm	\N
9450	1019	3	7711	6	Schankbier dunkel untergärig alkoholfrei	\N
9451	1019	3	7712	7	Vollbier hell obergärig alkoholarm	\N
9452	1019	3	7713	8	Vollbier hell obergärig alkoholfrei	\N
9453	1019	3	7714	9	Vollbier dunkel untergärig alkoholarm	\N
9454	1019	3	7715	10	Vollbier dunkel untergärig alkoholfrei	\N
9455	1019	3	7716	11	Schankbier hell untergärig alkoholfrei	\N
9456	1019	3	7717	12	Schankbier dunkel obergärig alkohol-  und nährwertreduziert	\N
9457	1019	3	7718	13	Vollbier hell untergärig alkoholfrei	\N
9458	1019	3	7719	14	Vollbier hell untergärig alkoholarm	\N
9459	1019	3	7720	15	Schankbier dunkel untergärig alkohol- und nährwertreduziert	\N
9460	1019	3	7721	16	Schankbier hell untergärig alkohol- und nährwertreduziert	\N
9461	1019	3	7722	17	Schankbier hell untergärig alkoholarm	\N
9462	1019	3	7723	18	Vollbier dunkel obergärig alkoholfrei	\N
9463	1019	3	7724	19	Vollbier hell obergärig alkohol- und nährwertreduziert	\N
9464	1019	3	7725	20	Schankbier dunkel obergärig alkoholfrei	\N
9465	1019	3	7726	21	Vollbier dunkel obergärig alkoholarm	\N
9466	1019	3	7727	22	Schankbier dunkel obergärig alkoholarm	\N
9467	1019	3	7728	23	Vollbier hell untergärig alkohol- und nährwertreduziert	\N
9468	1019	3	7729	24	Vollbier dunkel obergärig alkohol- und nährwertreduziert	\N
9469	1019	3	7730	26	Schankbier hell obergärig alkohol- und nährwertreduziert	\N
9470	1020	3	7732	1	Gerstenmalz	\N
9471	1020	3	7733	2	Weizenmalz	\N
9472	1020	3	7734	3	Hopfen	\N
9473	1020	3	7735	4	Hopfenextrakt	\N
9474	1020	3	7736	5	Bierhefe	\N
9475	1020	3	7738	7	Bierwürze	\N
9476	1020	3	7739	8	Farbmalz/Röstmalz	\N
9477	1020	3	7740	9	Sauermalz	\N
9478	1020	3	7741	10	Karamelmalz	\N
9479	1020	3	7742	11	Grünmalz	\N
9480	1020	3	7743	12	Braugerste	\N
9481	1020	3	7744	13	Malzersatzstoffe	\N
9482	1020	3	7745	14	Hopfenpulver	\N
9483	1020	3	7746	15	Hopfenpellets	\N
9484	1020	3	7747	16	Roggenmalz	\N
9485	1020	3	7748	17	Dinkelmalz	\N
9486	1020	3	7749	18	Rohfrucht	\N
9487	1020	3	7750	19	Brauzucker	\N
9488	1020	3	7751	20	Hopfenzapfen	\N
9489	1023	3	7755	1	Bier mit Reis	\N
9490	1023	3	7756	2	Bier mit Mais	\N
9491	1023	3	7757	3	Bier mit mehreren Malzersatzstoffen	\N
9492	1023	3	7758	4	Bier mit Hirse	\N
9493	1023	3	7759	5	Bier mit Roggen	\N
9494	1023	3	7760	6	Bier mit Zuckerarten	\N
9495	1023	3	7761	7	Bier mit Malzersatz- und Zusatzstoffen	\N
9496	1023	3	7762	8	Biere nicht nach dem Reinheitsgebot gebraut	\N
9497	1024	3	7764	1	Porter	\N
9498	1024	3	7765	2	Ale	\N
9499	1024	3	7766	3	Stout	\N
9500	1024	3	7767	4	Guinness	\N
9501	1024	3	7768	5	Geuze	\N
9502	1024	3	7769	6	Kriek-Lambic	\N
9503	1024	3	7770	7	Framboise-Lambic	\N
9504	1024	3	7771	8	Köstrizer Schwarzbier	\N
9505	1024	3	7772	9	Leipziger Gose	\N
9506	1024	3	7773	10	Nährbier	\N
9507	1024	3	7774	11	Rauchbier	\N
9508	1026	3	7778	1	Aromatisierte Spirituose	\N
9509	1026	3	7779	2	Bierbrand	\N
9510	1026	3	7780	3	Bitter/Bitterbrand	\N
9511	1026	3	7781	4	Wodka	\N
9512	1026	3	7782	5	Honigbrand	\N
9513	1026	3	7783	6	Wodka aromatisiert	\N
9514	1027	3	7786	1	Rum	\N
9515	1027	3	7787	2	Rumverschnitt	\N
9516	1027	3	7788	3	Spirituose mit Rum	\N
9517	1027	3	7789	4	Spirituose aus Zuckerrohr sonstige	\N
9518	1027	3	14210	5	Cachaca Cachaça	\N
9519	1028	3	7791	1	Korn/Kornbrand	\N
9520	1028	3	7792	2	Getreidebrand	\N
9521	1028	3	7793	3	Whisk(e)y irisch/schottisch	\N
9522	1028	3	7794	4	Whisk(e)y amerikanisch/kanadisch	\N
9523	1028	3	7795	5	Whisk(e)y andere	\N
9524	1028	3	7796	6	Spirituose aus Reis	\N
9525	1028	3	7797	7	Spirituose aus Hirse	\N
9526	1028	3	7798	8	Spirituose aus sonstigen Getreiden	\N
9527	1029	3	7807	1	Brandy/Weinbrand	\N
9528	1029	3	7808	2	Branntwein	\N
9529	1029	3	7809	3	Deutscher Weinbrand	\N
9530	1029	3	7810	4	Spirituose mit Weindestillat	\N
9531	1029	3	7811	5	Traubentresterbrand/Trester/Grappa	\N
9532	1029	3	7812	6	Wein-Hefebrand	\N
9533	1029	3	7813	7	Weindestillat/Rohbrand	\N
9534	1030	3	7815	1	Apfelbrand	\N
9535	1030	3	7816	2	Aprikosenbrand	\N
9536	1030	3	7817	3	Bananenbrand	\N
9537	1030	3	7818	4	Birnenbrand	\N
9538	1030	3	7819	5	Blaubeerbrand Heidelbeerbrand	\N
9539	1030	3	7820	6	Brand aus Apfelwein	\N
9540	1030	3	7821	7	Brand aus Birnenwein	\N
9541	1030	3	7822	8	Brand aus Citrusfrüchten	\N
9542	1030	3	7823	9	Brand aus Erdbeerbaumfrüchten	\N
9543	1030	3	7824	10	Brand aus Obsttrester	\N
9544	1030	3	7825	11	Brand aus Passionsfrüchten	\N
9545	1030	3	7826	12	Brombeerbrand	\N
9546	1030	3	7827	13	Calvados	\N
9547	1030	3	7828	14	Vogelbeerbrand Ebereschenbrand	\N
9548	1030	3	7829	15	Erdbeerbrand	\N
9549	1030	3	7830	16	Feigenbrand	\N
9551	1030	3	7832	19	Himbeerbrand	\N
9552	1030	3	7833	20	Holunderbrand	\N
9553	1030	3	7834	21	Johannisbeerbrand rot/weiß	\N
9554	1030	3	7835	22	Johannisbeerbrand schwarz	\N
9555	1030	3	7836	23	Kirschbrand	\N
9556	1030	3	7837	24	Korinthenbrand	\N
9557	1030	3	7838	25	Maulbeerbrand	\N
9558	1030	3	7839	26	Mirabellenbrand	\N
9559	1030	3	7840	27	Mombinpflaumenbrand	\N
9560	1030	3	7841	28	Obst-Hefebrand	\N
9561	1030	3	7842	29	Pfirsichbrand	\N
9562	1030	3	7843	30	Zwetschgen-/Pflaumen-/Cythera-Pflaumenbrand	\N
9563	1030	3	7844	31	Schlehenbrand	\N
9564	1030	3	7845	32	Stechpalmenbrand	\N
9565	1030	3	7846	33	Traubenbrand	\N
9566	1030	3	7847	34	Williams	\N
9567	1030	3	7848	35	Brand aus sonstigen sortenreinen Früchten	\N
9568	1030	3	7849	36	Brand aus mehreren Früchten	\N
9569	1031	3	7851	1	Aprikosengeist	\N
9570	1031	3	7852	2	Brombeergeist	\N
9571	1031	3	7853	3	Ebereschen- Vogelbeergeist	\N
9572	1031	3	7854	4	Erdbeergeist	\N
9573	1031	3	7855	5	Heidelbeergeist	\N
9574	1031	3	7856	6	Himbeergeist	\N
9575	1031	3	7857	7	Johannisbeergeist	\N
9576	1031	3	7858	8	Mirabellengeist	\N
9577	1031	3	7859	9	Pfirsichgeist	\N
9578	1031	3	7860	10	Quittengeist	\N
9579	1031	3	7861	11	Schlehengeist	\N
9580	1031	3	7862	12	Obstgeist aus sonstigen sortenreinen Früchten	\N
9581	1031	3	7863	13	Obstgeist aus mehreren Früchten	\N
9582	1031	3	14211	14	Mombinpflaumengeist	\N
9583	1031	3	14212	15	Elsbeerengeist	\N
9584	1031	3	14213	16	Holundergeist	\N
9585	1031	3	14214	17	Hagebuttengeist	\N
9586	1031	3	14215	18	Bananengeist	\N
9587	1031	3	14216	19	Passionsfruchtgeist	\N
9588	1031	3	14217	20	Cytherapflaumengeist	\N
9589	1031	3	14218	21	Haselnussgeist	\N
9590	1031	3	14219	22	Walnussgeist	\N
9591	1031	3	14220	23	Geist aus sonstigen Nussarten	\N
9592	1032	3	7865	1	Brombeerspirituose	\N
9593	1032	3	7866	2	Himbeerspirituose	\N
9594	1032	3	7867	3	Johannisbeerspirituose	\N
9595	1032	3	7868	4	Schlehenspirituose	\N
9596	1032	3	7869	5	Obstspirituose sonstige	\N
9597	1033	3	7873	1	Arrak	\N
9598	1033	3	7874	2	Arrak-Verschnitt	\N
9599	1033	3	7875	3	Berenburg/Beerenburg	\N
9600	1033	3	7876	4	Bärwurz	\N
9601	1033	3	7877	5	Enzian	\N
9602	1033	3	7878	6	Kartoffelspirituose	\N
9603	1033	3	7879	7	Medronho	\N
9604	1033	3	7880	8	Spirituose aus Wurzeln	\N
9605	1033	3	7881	9	Tequila	\N
9606	1033	3	7882	10	Spirituose aus Topinambur	\N
9607	1033	3	7883	11	Spirituose aus sonstigem pflanzlichen Ausgangsmaterial	\N
9608	1033	3	14206	12	Gemüsebrand	\N
9609	1033	3	14207	13	Stechpalmengeist	\N
9610	1033	3	14208	14	Gemüsegeist	\N
9611	1033	3	14209	15	Geist aus sonstigem pflanzlichen Ausgangsmaterial	\N
9612	1034	3	7885	1	Anis	\N
9613	1034	3	7886	2	Aquavit	\N
9614	1034	3	7887	3	Destillierter Gin	\N
9615	1034	3	7888	4	Gin	\N
9616	1034	3	7889	5	Genever/Jenever	\N
9617	1034	3	7890	6	Mistra	\N
9618	1034	3	7891	7	Ouzo	\N
9619	1034	3	7892	8	Pastis	\N
9620	1034	3	7893	9	Spirituose aus Anis	\N
9621	1034	3	7894	10	Spirituose mit bitterem Geschmack	\N
9622	1034	3	7895	11	Spirituose mit Kümmel	\N
9623	1034	3	7896	12	Spirituose mit Wacholder	\N
9624	1034	3	7897	13	Spirituose mit Wacholder und/oder Früchten/Pflanzenteilen ausg. 371410	\N
9625	1034	3	7898	14	Väkeräglögli/Spritglögg	\N
9626	1034	3	7899	15	Spirituose mit sonstigem pflanzlichen Ausgangsmaterial	\N
9627	1034	3	13339	16	Spirituose mit Wermut/Absinth	\N
9628	1034	3	14221	17	Kräutergeist	\N
9629	1034	3	14222	18	Rosenblättergeist	\N
9630	1035	3	7901	1	Ananaslikör	\N
9631	1035	3	7902	2	Apfellikör	\N
9632	1035	3	7903	3	Apfel mit Korn	\N
9633	1035	3	7904	4	Apfel mit Weinbrand	\N
9634	1035	3	7905	5	Apricot-brandy	\N
9635	1035	3	7906	6	Aprikosenlikör	\N
9636	1035	3	7907	7	Bananenlikör	\N
9637	1035	3	7908	8	Berberitzenlikör	\N
9638	1035	3	7909	9	Bergamottlikör	\N
9639	1035	3	7910	10	Birnenlikör	\N
9640	1035	3	7911	11	Brombeerlikör	\N
9641	1035	3	7912	12	Cherry-brandy	\N
9642	1035	3	7913	13	Ebereschenlikör Vogelbeerlikör	\N
9643	1035	3	7914	14	Erdbeerlikör	\N
9644	1035	3	7915	15	Feigenlikör	\N
9645	1035	3	7916	16	Hagebuttenlikör	\N
9646	1035	3	7917	17	Heidelbeerlikör	\N
9647	1035	3	7918	18	Himbeerlikör	\N
9648	1035	3	7919	19	Holunderlikör	\N
9649	1035	3	7920	20	Johannisbeerlikör rot/weiß	\N
9650	1035	3	7921	21	Johannisbeerlikör schwarz	\N
9651	1035	3	7922	22	Kirsch mit Rum	\N
9652	1035	3	7923	23	Kirsch mit Whisk(e)y	\N
9653	1035	3	7924	24	Kirschlikör	\N
9654	1035	3	7925	25	Kiwilikör	\N
9655	1035	3	7926	26	Mangolikör	\N
9656	1035	3	7927	27	Maracujalikör	\N
9657	1035	3	7928	28	Maraschino	\N
9658	1035	3	7929	29	Orange-brandy	\N
9659	1035	3	7930	30	Pfirsichlikör	\N
9660	1035	3	7931	31	Pflaumenlikör	\N
9661	1035	3	7932	32	Preiselbeerlikör	\N
9662	1035	3	7933	33	Prune-brandy	\N
9663	1035	3	7934	34	Quittenlikör	\N
9664	1035	3	7935	35	Sanddornlikör	\N
9665	1035	3	7936	36	Schlehenlikör	\N
9666	1035	3	7937	37	Stachelbeerlikör	\N
9667	1035	3	7938	38	Traubenlikör	\N
9668	1035	3	7939	39	Zitruslikör	\N
9669	1035	3	7940	40	Likör aus sonstigen sortenreinen Früchten	\N
9670	1035	3	7941	41	Likör aus mehreren Früchten	\N
9671	1036	3	7943	1	Kaffee- /Mokkalikör	\N
9672	1036	3	7944	2	Kakao mit Nusslikör	\N
9673	1036	3	7945	3	Kakaolikör	\N
9674	1036	3	7946	4	Teelikör	\N
9675	1037	3	7948	1	Anislikör	\N
9676	1037	3	7949	2	Bitterlikör	\N
9677	1037	3	7950	3	Enzianlikör	\N
9678	1037	3	7951	4	Gewürzlikör	\N
9679	1037	3	7952	5	Holunderblütenlikör	\N
9680	1037	3	7953	6	Kräuterlikör	\N
9681	1037	3	7954	7	Kümmellikör	\N
9682	1037	3	7955	8	Likör aus Beifuß	\N
9683	1037	3	7956	9	Likör aus Wundklee	\N
9684	1037	3	7957	10	Mandellikör	\N
9685	1037	3	7958	11	Pfefferminzlikör	\N
9686	1037	3	7959	12	Sambuca	\N
9687	1037	3	7960	13	Vanillelikör	\N
9688	1037	3	7961	14	Likör aus sonstigen Kräutern Gewürzen oder Blüten	\N
9689	1038	3	7964	1	Cocoskremlikör Kokoskremlikör	\N
9690	1038	3	7965	2	Eierlikör	\N
9691	1038	3	7966	3	Haselnusslikör	\N
9692	1038	3	7967	4	Likör mit Eizusatz	\N
9693	1038	3	7968	5	Likör mit Kaffee und Sahne	\N
9694	1038	3	7969	6	Likör Mocca mit Sahne	\N
9695	1038	3	7970	7	Milchlikör	\N
9696	1038	3	7971	8	Sahnelikör	\N
9697	1038	3	7972	9	Schokoladenlikör	\N
9698	1038	3	7973	10	Walnusslikör	\N
9699	1038	3	7974	11	Emulsionsliköre sonstige	\N
9700	1039	3	7976	1	Allasch	\N
9701	1039	3	7977	2	Cordial Medoc	\N
9702	1039	3	7978	3	Goldwasser	\N
9703	1039	3	7979	4	Guignolet	\N
9704	1039	3	7980	5	Honiglikör	\N
9705	1039	3	7981	6	Nocino	\N
9706	1039	3	7982	7	Punch au rhum	\N
9707	1039	3	7983	8	Punschextrakt	\N
9708	1039	3	7984	9	Schwedenpunsch/Arrakpunsch	\N
9709	1039	3	7985	10	Vruchtenjenever oder Jenever mit Vruchten	\N
9710	1039	3	7986	11	Besondere Likörarten sonstige	\N
9711	1040	3	7988	1	Bitterer mit Fruchtsaft	\N
9712	1040	3	7989	2	Kaffee mit Weinbrand	\N
9713	1040	3	7990	3	Kaffee mit Whisk(e)y	\N
9714	1040	3	7991	4	Sonstige Mischgetränke aus Spirituosen	\N
9715	1040	3	7992	5	Whisk(e)y mit Cola	\N
9716	1040	3	7993	6	Rum mit Cola	\N
9717	1041	3	7995	1	Alkoholhaltiges Getränk auf der Basis von Getreide	\N
9718	1041	3	7996	2	Alkoholhaltiges Getränk auf der Basis von Hirse	\N
9719	1041	3	7997	3	Alkoholhaltiges Getränk auf der Basis von Reis	\N
9720	1041	3	7998	4	Alkoholhaltiges Mischgetränk	\N
9721	1041	3	7999	5	Korn-Soda	\N
9722	1041	3	8000	6	Likör-Soda	\N
9723	1041	3	8001	7	Punsch	\N
9724	1041	3	8002	8	Rum Soda	\N
9725	1041	3	8003	9	Wacholder-Soda	\N
9726	1041	3	8004	10	Wodka-Soda	\N
9727	1041	3	8005	11	Alkopop alkoholhaltiges Mischgetränk mit geringem Alkoholgehalt	\N
9728	1042	3	8022	1	Destillationsvorlauf	\N
9729	1042	3	8023	2	Destillationsnachlauf	\N
9730	1042	3	8024	3	Obstmaische	\N
9731	1042	3	8025	4	Maische andere	\N
9732	1042	3	8026	5	Rohbrand aus Obstmaische	\N
9733	1042	3	8027	6	Rohbrand aus anderen Maischen	\N
9734	1043	3	8031	1	Raffinade	\N
9735	1043	3	8032	2	Weißzucker	\N
9736	1043	3	8033	3	Halbweißzucker	\N
9737	1043	3	8034	4	Flüssigzucker	\N
9738	1043	3	8035	5	Kandiszucker	\N
9739	1043	3	8036	6	Farinzucker	\N
9740	1044	3	8038	1	Dünnsaft	\N
9741	1044	3	8039	2	Dicksaft	\N
9742	1044	3	8040	3	Zuckerablaufsirup	\N
9743	1044	3	8041	4	Rübensaft	\N
9744	1044	3	8042	5	Melasse	\N
9745	1044	3	8043	6	Rohzucker aus Rohr	\N
9746	1044	3	8044	7	Rohzucker aus Rüben	\N
9747	1045	3	8046	1	Invertflüssigzucker	\N
9748	1045	3	8047	2	Invertzuckersirup	\N
9749	1046	3	8049	1	Glukosesirup	\N
9750	1046	3	8050	2	Glukosesirup getrocknet	\N
9751	1046	3	8052	4	Glukose kristallwasserhaltig	\N
9752	1046	3	8053	5	Glukose kristallwasserfrei	\N
9753	1046	3	8054	6	Glukose vitaminisiert	\N
9754	1046	3	8055	7	Glukose	\N
9755	1046	3	8056	8	Isoglukosesirup	\N
9756	1047	3	8058	1	Fruktose	\N
9757	1047	3	8059	2	gestrichen jetzt 021701, Lactose	\N
9758	1047	3	8060	3	Maltose	\N
9759	1047	3	8061	4	Maltosesirup	\N
9760	1047	3	14292	5	Palmzucker	\N
9761	1047	3	14293	6	Manna	\N
9762	1047	3	14294	7	Agavensirup Agavendicksaft	\N
9763	1047	3	14295	8	Reissirup	\N
9764	1048	3	8063	1	Vanillezucker	\N
9765	1048	3	8064	2	Zucker und Zimtmischung	\N
9766	1048	3	14203	3	Zuckerdekor	\N
9767	1049	3	8066	1	Ahornsirup	\N
9768	1049	3	8067	2	Ahornzucker	\N
9769	1049	3	8068	3	Hirsesirup	\N
9770	1049	3	8069	4	Hirsezucker	\N
9771	1049	3	8070	5	Palmzucker	\N
9772	1049	3	8071	6	Manna	\N
9773	1049	3	14252	7	Agavensirup; Agavendicksaft	\N
9774	1050	3	8074	1	Orangenblütenhonig	\N
9775	1050	3	8075	2	Rapsblütenhonig	\N
9776	1050	3	8076	3	Lindenhonig	\N
9777	1050	3	8077	4	Kleehonig	\N
9778	1050	3	8078	5	Heidehonig	\N
9779	1050	3	8079	6	Akazienhonig	\N
9780	1050	3	8080	7	Kastanienhonig	\N
9781	1050	3	8081	8	Löwenzahnhonig	\N
9782	1050	3	8082	9	Lavendelhonig	\N
9783	1050	3	8083	10	Sonnenblumenhonig	\N
9784	1050	3	8084	11	Salbeihonig	\N
9785	1050	3	8085	12	Mimosenhonig	\N
9786	1050	3	8086	13	Eukalyptushonig	\N
9787	1050	3	8087	14	Rosmarinhonig	\N
9788	1050	3	8088	15	Tymianhonig	\N
9789	1050	3	8089	16	Buchweizenhonig	\N
9790	1050	3	8090	17	Alpenrosenhonig	\N
9791	1050	3	8091	18	Obstblütenhonig	\N
9792	1050	3	8092	19	Olmohonig	\N
9793	1050	3	14253	20	Manukahonig	\N
9794	1051	3	8094	1	Gebirgsblütenhonig	\N
9795	1051	3	8095	2	Wildblütenhonig	\N
9796	1051	3	8096	3	Sommerblütenhonig	\N
9797	1051	3	8097	4	Klee- und Rapshonig	\N
9798	1051	3	8098	5	Klee- und Lindenhonig	\N
9799	1051	3	8099	6	Wiesenblumenhonig	\N
9800	1051	3	8100	7	Blütenhonig mit Wabenstück	\N
9801	1051	3	8101	8	Frühtrachthonig	\N
9802	1052	3	8103	1	Fichtenhonig	\N
9803	1052	3	8104	2	Tannenhonig	\N
9804	1052	3	8105	3	Waldhonig	\N
9805	1052	3	8106	4	Koniferenhonig	\N
9806	1052	3	8107	5	Blüten- und Honigtauhonig	\N
9807	1052	3	8108	6	Blüten- und Honigtauhonig mit Wabenstück	\N
9808	1053	3	8113	1	Backhonig	\N
9809	1054	3	8116	1	Invertzuckerkrem mit Honig	\N
9810	1055	3	8118	1	Erdnusskrem süßer Brotaufstrich	\N
9811	1055	3	8119	2	Kokosnusskrem süßer Brotaufstrich	\N
9812	1055	3	8120	3	Schokokrem süßer Brotaufstrich	\N
9813	1055	3	8121	4	Nuss-Nougatkrem süßer Brotaufstrich	\N
9814	1055	3	8122	5	Orangen-Kakaokrem süßer Brotaufstrich	\N
9815	1055	3	8123	6	Kakaokrem süßer Brotaufstrich	\N
9816	1055	3	8127	10	Zuckerrübensirup süßer Brotaufstrich	\N
9817	1055	3	8128	11	Brotaufstrich aromatisiert süßer Brotaufstrich	\N
9818	1055	3	8129	13	Mandelmus süßer Brotaufstrich	\N
9819	1055	3	8130	14	Haselnussmus süßer Brotaufstrich	\N
9820	1055	3	8131	15	Mandel-Orangenmus süßer Brotaufstrich	\N
9821	1055	3	8132	16	Sesammus süßer Brotaufstrich	\N
9822	1055	3	8133	17	Dattel-Orangenmus süßer Brotaufstrich	\N
9823	1055	3	8134	18	Holunderbeermus süßer Brotaufstrich	\N
9824	1055	3	8135	19	Sanddornmus süßer Brotaufstrich	\N
9825	1055	3	8136	20	Hagebuttenmus süßer Brotaufstrich	\N
9826	1055	3	8137	21	Cashewnussmus süßer Brotaufstrich	\N
9827	1055	3	8138	22	Mehrfruchtbrotaufstrich süßer Brotaufstrich	\N
9828	1055	3	8139	24	Einfruchtbrotaufstrich süßer Brotaufstrich	\N
9829	1055	3	8140	25	Brotaufstrich aus Früchten und Honig	\N
9830	1055	3	8141	26	Rosenblätter in Sirup süßer Brotaufstrich	\N
9831	1055	3	8142	27	Carob-Nussmus süßer Brotaufstrich	\N
9832	1055	3	8143	28	Erdnuss-Schokokrem süßer Brotaufstrich	\N
9833	1055	3	8144	29	Mandel-Nougatkrem süßer Brotaufstrich	\N
9834	1055	3	8145	30	Milch-Schokokrem	\N
9835	1056	3	8147	1	Erdnussmus/Erdnussbutter nicht süßer Brotaufstrich	\N
9836	1056	3	8148	2	Nuss-Paprikamus nicht süßer Brotaufstrich	\N
9837	1056	3	8149	3	Vegetarischer Brotaufstrich nicht süßer Brotaufstrich	\N
9838	1056	3	8150	4	Brotaufstrich aus Sojaerzeugnis	\N
9839	1056	3	8151	5	Mandelmus nicht süßer Brotaufstrich	\N
9840	1056	3	8152	6	Haselnussmus nicht süßer Brotaufstrich	\N
9841	1056	3	8153	7	Haselnusskrem nicht süßer Brotaufstrich	\N
9842	1056	3	8154	9	Sesammus nicht süßer Brotaufstrich	\N
9843	1059	3	8158	1	Gelee Royal	\N
9844	1059	3	8159	2	Blütenpollen	\N
9845	1059	3	8160	3	Blütenpollen mit Honigzusatz	\N
9846	1059	3	8161	4	Zubereitungen mit Gelee Royal	\N
9847	1060	3	8163	1	Kokosnusskrem süßer Brotaufstrich brennwertreduziert	\N
9848	1060	3	8164	2	Orangen-Kakaokrem süßer Brotaufstrich brennwertreduziert	\N
9849	1060	3	8165	3	Brotaufstrich aromatisiert süßer Brotaufstrich brennwertreduziert	\N
9850	1060	3	8166	4	Mandel-Orangenmus süßer Brotaufstrich brennwertreduziert	\N
9851	1060	3	8167	5	Dattel-Orangenmus süßer Brotaufstrich brennwertreduziert	\N
9852	1060	3	8168	6	Holunderbeermus süßer Brotaufstrich brennwertreduziert	\N
9853	1060	3	8169	7	Sanddornmus süßer Brotaufstrich brennwertreduziert	\N
9854	1060	3	8170	8	Hagebuttenmus süßer Brotaufstrich brennwertreduziert	\N
9855	1060	3	8171	9	Mehrfruchtbrotaufstrich süßer Brotaufstrich brennwertreduziert	\N
9856	1060	3	8172	10	Einfruchtbrotaufstrich süßer Brotaufstrich brennwertreduziert	\N
9857	1061	3	8174	1	Heide-Wabenhonig	\N
9858	1062	3	8176	1	Fruchterzeugnis mit Honig	\N
9859	1063	3	8179	1	Erdbeerkonfitüre extra	\N
9860	1063	3	8180	2	Himbeerkonfitüre extra	\N
9861	1063	3	8181	3	Brombeerkonfitüre extra	\N
9862	1063	3	8182	4	Johannisbeerkonfitüre rot extra	\N
9863	1063	3	8183	5	Johannisbeerkonfitüre schwarz extra	\N
9864	1063	3	8184	6	Stachelbeerkonfitüre extra	\N
9865	1063	3	8185	7	Preiselbeerkonfitüre extra	\N
9866	1063	3	8186	8	Heidelbeerkonfitüre extra	\N
9867	1063	3	8187	9	Schlehenkonfitüre extra	\N
9868	1063	3	8189	11	Holunderkonfitüre extra	\N
9869	1063	3	8190	12	Hagebuttenkonfitüre extra	\N
9870	1064	3	8192	1	Erdbeerkonfitüre	\N
9871	1064	3	8193	2	Himbeerkonfitüre	\N
9872	1064	3	8194	3	Brombeerkonfitüre	\N
9873	1064	3	8195	4	Johannisbeerkonfitüre rot	\N
9874	1064	3	8196	5	Johannisbeerkonfitüre schwarz	\N
9875	1064	3	8197	6	Stachelbeerkonfitüre	\N
9876	1064	3	8198	7	Preiselbeerkonfitüre	\N
9877	1064	3	8199	8	Heidelbeerkonfitüre	\N
9878	1064	3	8200	9	Schlehenkonfitüre	\N
9879	1064	3	8202	11	Holunderkonfitüre	\N
9880	1064	3	8203	12	Hagebuttenkonfitüre	\N
9881	1065	3	8205	1	Himbeergelee extra	\N
9882	1065	3	8206	2	Brombeergelee extra	\N
9883	1065	3	8207	3	Johannisbeergelee rot extra	\N
9884	1065	3	8208	4	Johannisbeergelee schwarz extra	\N
9885	1065	3	8209	5	Stachelbeergelee extra	\N
9886	1065	3	8210	6	Sanddorngelee extra	\N
9887	1065	3	8211	7	Traubengelee extra	\N
9888	1066	3	8213	1	Himbeergelee	\N
9889	1066	3	8214	2	Brombeergelee	\N
9890	1066	3	8215	3	Johannisbeergelee rot	\N
9891	1066	3	8216	4	Johannisbeergelee schwarz	\N
9892	1066	3	8217	5	Stachelbeergelee	\N
9893	1066	3	8218	6	Sanddorngelee	\N
9894	1066	3	8219	7	Traubengelee	\N
9895	1067	3	8221	1	Quittenkonfitüre extra	\N
9896	1068	3	8223	1	Quittenkonfitüre	\N
9897	1069	3	8225	1	Apfelgelee extra	\N
9898	1069	3	8226	2	Birnengelee extra	\N
9899	1069	3	8227	3	Quittengelee extra	\N
9900	1070	3	8229	1	Apfelgelee	\N
9901	1070	3	8230	2	Birnengelee	\N
9902	1070	3	8231	3	Quittengelee	\N
9903	1071	3	8233	1	Reneklaudenkonfitüre extra	\N
9904	1071	3	8234	2	Pfirsichkonfitüre extra	\N
9905	1071	3	8235	3	Aprikosenkonfitüre extra	\N
9906	1071	3	8236	4	Kirschkonfitüre extra	\N
9907	1071	3	8237	5	Pflaumenkonfitüre extra	\N
9908	1071	3	8238	6	Sauerkirschkonfitüre extra	\N
9909	1071	3	8239	7	Schwarzkirschkonfitüre extra	\N
9910	1072	3	8241	1	Reneklaudenkonfitüre	\N
9911	1072	3	8242	2	Pfirsichkonfitüre	\N
9912	1072	3	8243	3	Aprikosenkonfitüre	\N
9913	1072	3	8244	4	Kirschkonfitüre	\N
9914	1072	3	8245	5	Pflaumenkonfitüre	\N
9915	1072	3	8246	6	Sauerkirschkonfitüre	\N
9916	1072	3	8247	7	Schwarzkirschkonfitüre	\N
9917	1073	3	8249	1	Kirschgelee extra	\N
9918	1074	3	8251	1	Kirschgelee	\N
9919	1075	3	8253	1	Mandarinenkonfitüre extra	\N
9920	1075	3	8254	2	Grapefruitkonfitüre extra	\N
9921	1075	3	8255	3	Zitronenkonfitüre extra	\N
9922	1075	3	8256	4	Orangenkonfitüre extra	\N
9923	1076	3	8258	1	Mandarinenkonfitüre	\N
9924	1076	3	8259	2	Grapefruitkonfitüre	\N
9925	1076	3	8260	3	Zitronenkonfitüre	\N
9926	1076	3	8261	4	Orangenkonfitüre	\N
9927	1077	3	8263	1	Orangengelee extra	\N
9928	1077	3	8264	2	Zitronengelee extra	\N
9929	1077	3	8265	3	Limettengelee extra	\N
9930	1077	3	8266	4	Bergamottengelee extra	\N
9931	1078	3	8268	1	Orangengelee	\N
9932	1078	3	8269	2	Zitronengelee	\N
9933	1078	3	8270	3	Limettengelee	\N
9934	1078	3	8271	4	Bergamottengelee	\N
9935	1079	3	8273	1	Orangenmarmelade	\N
9936	1079	3	8274	2	Zitronenmarmelade	\N
9937	1079	3	8275	3	Grapefruitmarmelade	\N
9938	1080	3	8277	1	Ananaskonfitüre extra	\N
9939	1080	3	8278	2	Feigenkonfitüre extra	\N
9940	1080	3	8279	3	Rhabarberkonfitüre extra	\N
9941	1080	3	8280	4	Maracujakonfitüre extra	\N
9942	1080	3	8281	5	Tomatenkonfitüre extra	\N
9943	1080	3	8282	6	Kiwikonfitüre extra	\N
9944	1080	3	8283	7	Guavenkonfitüre extra	\N
9945	1080	3	8284	8	Ingwerkonfitüre extra	\N
9946	1081	3	8286	1	Ananaskonfitüre	\N
9947	1081	3	8287	2	Feigenkonfitüre	\N
9948	1081	3	8288	3	Rhabarberkonfitüre	\N
9949	1081	3	8289	4	Maracujakonfitüre	\N
9950	1081	3	8290	5	Tomatenkonfitüre	\N
9951	1081	3	8291	6	Kiwikonfitüre	\N
9952	1081	3	8292	7	Guavenkonfitüre	\N
9953	1081	3	8293	8	Ingwerkonfitüre	\N
9954	1082	3	8295	1	Ananasgelee extra	\N
9955	1082	3	8296	2	Rhabarbergelee extra	\N
9956	1082	3	8299	5	Ingwergelee extra	\N
9957	1083	3	8301	1	Rhabarbergelee	\N
9958	1083	3	8302	2	Ananasgelee	\N
9959	1083	3	8305	5	Ingwergelee	\N
9960	1084	3	8307	1	Zweifruchtkonfitüre extra	\N
9961	1084	3	8308	2	Dreifruchtkonfitüre extra	\N
9962	1084	3	8309	3	Mehrfruchtkonfitüre extra	\N
9963	1085	3	8311	1	Fruchtgelee	\N
9964	1087	3	8318	1	Fruchtzubereitung für die Spirituosenherstellung	\N
9965	1087	3	8319	2	Fruchtzubereitung für Milchprodukte	\N
9966	1087	3	8320	3	Fruchtzubereitung für Backwaren	\N
9967	1088	3	8322	1	Zweifruchtkonfitüre	\N
9968	1088	3	8323	2	Dreifruchtkonfitüre	\N
9969	1088	3	8324	3	Mehrfruchtkonfitüre	\N
9970	1089	3	8326	1	Maronenkrem süßer Brotaufstrich	\N
9971	1089	3	8327	2	Birnenkraut süßer Brotaufstrich	\N
9972	1089	3	8328	3	Apfelkraut süßer Brotaufstrich	\N
9973	1089	3	8329	4	Obstkraut gemischtes süßer Brotaufstrich	\N
9974	1090	3	8331	1	Erdbeeraufstrich	\N
9975	1090	3	8332	2	Pfirsichaufstrich	\N
9976	1096	3	8339	1	Zweifruchtkonfitüre brennwertreduziert	\N
9977	1096	3	8340	2	Dreifruchtkonfitüre brennwertreduziert	\N
9978	1096	3	8341	3	Mehrfruchtkonfitüre brennwertreduziert	\N
9979	1097	3	8344	1	Kremeis Vanille	\N
9980	1097	3	8345	2	Kremeis Schokolade	\N
9981	1097	3	8346	3	Kremeis Kaffee	\N
9982	1097	3	8347	4	Kremeis Nuss	\N
9983	1097	3	8348	5	Kremeis Malaga	\N
9984	1097	3	8349	6	Kremeis Pistazie	\N
9985	1097	3	8350	7	Kremeis Erdbeer	\N
9986	1097	3	8351	8	Kremeis Walnuss	\N
9987	1097	3	8352	9	Kremeis Haselnuss	\N
9988	1097	3	8353	10	Kremeis Joghurt	\N
9989	1097	3	13340	11	Kremeis Vanillegeschmack	\N
9990	1097	3	8354	99	Kremeismischung	\N
9991	1098	3	8356	1	Fruchteis Apfelsine	\N
9992	1098	3	8357	2	Fruchteis Banane	\N
9993	1098	3	8358	3	Fruchteis Erdbeer	\N
9994	1098	3	8359	4	Fruchteis Himbeer	\N
9995	1098	3	8360	5	Fruchteis Kirsch	\N
9996	1098	3	8361	6	Fruchteis Zitrone	\N
9997	1098	3	8362	7	Fruchteis Ananas	\N
9998	1098	3	8363	8	Fruchteis Heidelbeer	\N
9999	1098	3	8364	9	Fruchteis Apfel	\N
10000	1098	3	8365	10	Fruchteis Maracuja	\N
10001	1098	3	8366	11	Fruchteis Kiwi	\N
10002	1098	3	8367	12	Fruchteis Melone	\N
10003	1098	3	8368	13	Fruchteis Aprikose	\N
10004	1098	3	8369	14	Fruchteis Mango	\N
10005	1098	3	8370	15	Fruchteis Papaya	\N
10006	1098	3	8371	17	Fruchteis Birne	\N
10007	1098	3	8372	18	Fruchteis Pfirsich	\N
10008	1098	3	8373	19	Fruchteis Brombeer	\N
10009	1098	3	8374	20	Fruchteis Erdbeerjoghurt	\N
10010	1098	3	8375	21	Fruchteis Waldbeere	\N
10011	1098	3	13341	23	Fruchteis Mandarine	\N
10012	1098	3	13342	24	Fruchteis Schwarze Johannisbeere	\N
10013	1098	3	8376	99	Fruchteismischung	\N
10014	1099	3	8378	1	Rahmeis Fürst Pückler	\N
10015	1099	3	8379	2	Rahmeis Vanille	\N
10016	1099	3	8380	3	Rahmeis Schokolade	\N
10017	1099	3	8381	4	Rahmeis Kaffee	\N
10018	1099	3	8382	5	Rahmeis Nuss	\N
10019	1099	3	8383	6	Rahmeis Kirsch	\N
10020	1099	3	8384	7	Rahmeis Himbeer	\N
10021	1099	3	8385	8	Rahmeis Erdbeer	\N
10022	1099	3	8386	9	Rahmeis Haselnuss	\N
10023	1099	3	8387	10	Rahmeis Walnuss	\N
10024	1099	3	8388	11	Rahmeis Joghurt	\N
10025	1099	3	13343	12	Rahmeis Vanillegeschmack	\N
10026	1099	3	8389	99	Rahmeismischung	\N
10027	1100	3	8391	1	Milcheis Vanille	\N
10028	1100	3	8392	2	Milcheis Schokolade	\N
10029	1100	3	8393	3	Milcheis Kaffee	\N
10030	1100	3	8394	4	Milcheis Nuss	\N
10031	1100	3	8395	5	Milcheis Erdbeergeschmack	\N
10032	1100	3	8396	6	Milcheis Kirschgeschmack	\N
10033	1100	3	8397	7	Milcheis Himbeergeschmack	\N
10034	1100	3	8398	8	Milcheis Zitronengeschmack	\N
10035	1100	3	8399	9	Milcheis Pistaziengeschmack	\N
10036	1100	3	8400	10	Milcheis Bananengeschmack	\N
10037	1100	3	8401	11	Milcheis Heidelbeergeschmack	\N
10038	1100	3	8402	12	Milcheis Ananasgeschmack	\N
10039	1100	3	8403	13	Milcheis Orangengeschmack	\N
10040	1100	3	8404	14	Milcheis Apfelgeschmack	\N
10041	1100	3	8405	15	Milcheis Malaga	\N
10042	1100	3	8406	16	Milcheis Stracciatella	\N
10043	1100	3	8407	18	Milcheis Kastanie	\N
10044	1100	3	8408	19	Milcheis Eierlikör	\N
10045	1100	3	8409	20	Milcheis Nougat	\N
10046	1100	3	8410	21	Milcheis Mokka	\N
10047	1100	3	8411	23	Milcheis Kokos	\N
10048	1100	3	8412	24	Milcheis Pfirsich	\N
10049	1100	3	8413	25	Milcheis Marzipan	\N
10050	1100	3	8414	26	Milcheis Pfefferminz	\N
10051	1100	3	8415	27	Milcheis Cappuccino	\N
10052	1100	3	8416	28	Milcheis Haselnuss	\N
10053	1100	3	8417	29	Milcheis Walnuss	\N
10054	1100	3	8418	30	Milcheis Amaretto	\N
10055	1100	3	8419	31	Milcheis Tiramisu	\N
10056	1100	3	8420	32	Milcheis Karamel	\N
10057	1100	3	8421	33	Milcheis Trüffel	\N
10058	1100	3	8422	34	Milcheis Krokant	\N
10059	1100	3	8423	35	Milcheis Maracujageschmack	\N
10060	1100	3	8424	37	Milcheis Waldmeistergeschmack	\N
10061	1100	3	8425	38	Milcheis Kiwi	\N
10062	1100	3	8426	39	Milcheis Zabaione	\N
10063	1100	3	8427	40	Milcheis Zimt	\N
10064	1100	3	8428	41	Milcheis Fruchtgeschmack	\N
10065	1100	3	8429	42	Milcheis Marone	\N
10066	1100	3	8430	43	Milcheis Joghurt	\N
10067	1100	3	8431	44	Milcheis Mandel	\N
10068	1100	3	8432	45	Milcheis Sahne-Kirsch	\N
10069	1100	3	8433	46	Milcheis Nuss-Nougatkrem	\N
10070	1100	3	8435	48	Milcheis Amarena-Kirsch	\N
10071	1100	3	8436	49	Milcheis Tartufo	\N
10072	1100	3	8437	50	Milcheis Saure-Sahne-Kirsch	\N
10073	1100	3	8438	51	Milcheis Raffaello	\N
10074	1100	3	8439	52	Milcheis Mozartkugel	\N
10075	1100	3	8440	53	Milcheis Mascarpone	\N
10076	1100	3	8441	54	Milcheis After Eight	\N
10077	1100	3	13344	55	Milcheis Vanillegeschmack	\N
10078	1100	3	13345	56	Milcheis mit Blaufärbung	\N
10079	1100	3	14091	57	Milcheis Latte Macchiato	\N
10080	1100	3	14092	58	Milcheis Espresso	\N
10081	1100	3	14093	59	Milcheis mit Joghurtschokolade	\N
10082	1100	3	14094	60	Milcheis mit Keks	\N
10083	1100	3	13346	91	Milcheis gefärbt	\N
10084	1100	3	8442	99	Milcheismischung	\N
10085	1101	3	8444	1	Eiskrem Vanille	\N
10086	1101	3	8445	2	Eiskrem Schokolade	\N
10087	1101	3	8446	3	Eiskrem Kaffee	\N
10088	1101	3	8447	4	Eiskrem Nuss	\N
10089	1101	3	8448	5	Eiskrem Erdbeergeschmack	\N
10090	1101	3	8449	6	Eiskrem Kirschgeschmack	\N
10091	1101	3	8450	7	Eiskrem Zitronengeschmack	\N
10092	1101	3	8451	8	Eiskrem Himbeergeschmack	\N
10093	1101	3	8452	9	Eiskrem Bananengeschmack	\N
10094	1101	3	8453	10	Eiskrem Heidelbeergeschmack	\N
10095	1101	3	8454	11	Eiskrem Ananasgeschmack	\N
10096	1101	3	8455	12	Eiskrem Orangengeschmack	\N
10097	1101	3	8456	13	Eiskrem Apfelgeschmack	\N
10098	1101	3	8457	14	Eiskrem Malaga	\N
10099	1101	3	8458	15	Eiskrem Waldmeistergeschmack	\N
10100	1101	3	8459	16	Eiskrem Krokant	\N
10101	1101	3	8460	17	Eiskrem Nougat	\N
10102	1101	3	8461	18	Eiskrem Pfirsichgeschmack	\N
10103	1101	3	8462	19	Eiskrem Maracujageschmack	\N
10104	1101	3	8463	20	Eiskrem Pistazie	\N
10105	1101	3	8464	21	Eiskrem Kokos	\N
10106	1101	3	8465	22	Eiskrem Haselnuss	\N
10107	1101	3	8466	23	Eiskrem Walnuss	\N
10108	1101	3	8467	24	Eiskrem Karamel	\N
10109	1101	3	8468	26	Eiskrem Rum-Rosine	\N
10110	1101	3	8469	27	Eiskrem Fruchtgeschmack	\N
10111	1101	3	8470	28	Eiskrem Quark	\N
10112	1101	3	8471	29	Eiskrem Honig-Zimt	\N
10113	1101	3	8472	30	Eiskrem Sanddorn	\N
10114	1101	3	8473	31	Eiskrem Joghurt	\N
10115	1101	3	8474	32	Eiskrem Stracciatella	\N
10116	1101	3	13347	33	Eiskrem Vanillegeschmack	\N
10117	1101	3	13348	34	Eiskrem Sahne	\N
10118	1101	3	13349	91	Eiskrem gefärbt	\N
10119	1101	3	8475	99	Eiskremmischung	\N
10120	1102	3	8524	1	Speiseeiskonserve	\N
10121	1102	3	8525	2	Speiseeispulver	\N
10122	1102	3	8526	3	Speiseeisansatz	\N
10123	1102	3	8527	4	Moccapaste Halberzeugnis für Speiseeis	\N
10124	1102	3	8528	5	Pistazienpaste Halberzeugnis für Speiseeis	\N
10125	1102	3	8529	6	Nusspaste Halberzeugnis für Speiseeis	\N
10126	1102	3	8530	7	Malagapaste Halberzeugnis für Speiseeis	\N
10127	1102	3	8531	8	Giandujapaste Halberzeugnis für Speiseeis	\N
10128	1102	3	8532	9	Kastanienpaste Halberzeugnis für Speiseeis	\N
10129	1102	3	8533	10	Fruchtzubereitung Halberzeugnis für Speiseeis	\N
10130	1102	3	8534	11	Stracciatellapaste Halberzeugnis für Speiseeis	\N
10131	1102	3	13352	12	Vanillearomapaste Halberzeugnis für Speiseeis	\N
10132	1102	3	13353	13	Erdbeereisgrundmasse Halberzeugnis f. Speiseeis	\N
10133	1102	3	13354	14	Bananeneisgrundmasse Halberzeugnis f. Speiseeis	\N
10134	1103	3	8536	1	Eisbecher	\N
10135	1103	3	8537	2	Eisbecher mit Früchten	\N
10136	1103	3	8538	3	Eiskaffee	\N
10137	1103	3	8539	4	Eisschokolade	\N
10138	1103	3	8540	5	Cassata Eis	\N
10139	1103	3	8541	6	Eistorte	\N
10140	1103	3	8542	99	Speiseeissorten gemischt	\N
10141	1104	3	8544	1	Sorbet Zitrone	\N
10142	1104	3	8545	2	Sorbet Orange	\N
10143	1104	3	8546	3	Sorbet Erdbeer	\N
10144	1104	3	8547	4	Sorbet Melone	\N
10145	1104	3	8548	5	Sorbet Apfel	\N
10146	1104	3	8549	6	Sorbet Kiwi	\N
10147	1104	3	8550	7	Sorbet Brombeer	\N
10148	1104	3	8551	8	Sorbet Himbeer	\N
10149	1106	3	8554	1	Eis mit Pflanzenfett Vanille	\N
10150	1106	3	8555	2	Eis mit Pflanzenfett Schokolade	\N
10151	1106	3	8556	3	Eis mit Pflanzenfett Kaffee	\N
10152	1106	3	8557	4	Eis mit Pflanzenfett Nuss	\N
10153	1106	3	8558	5	Eis mit Pflanzenfett Erdbeer	\N
10154	1106	3	8559	6	Eis mit Pflanzenfett Kirsch	\N
10155	1106	3	8560	7	Eis mit Pflanzenfett Banane	\N
10156	1106	3	8561	8	Eis mit Pflanzenfett Vanille Orange	\N
10157	1106	3	13355	9	Eis mit Pflanzenfett Vanillegeschmack	\N
10158	1106	3	13356	10	Eis mit Pflanzenfett Fürst Pückler Art	\N
10159	1110	3	8565	1	Hartkaramelle ungefüllt	\N
10160	1110	3	8566	2	Hartkaramelle ungefüllt mit Frucht- u./o. Kräuteraromen	\N
10161	1110	3	8567	3	Hartkaramelle ungefüllt mit Milch u./o. Sahne u./o. Butter	\N
10162	1110	3	8568	4	Hartkaramelle ungefüllt mit Honig u./o. Malz	\N
10163	1110	3	8569	5	Hartkaramelle ungefüllt mit Colaextrakt	\N
10164	1110	3	8570	6	Hartkaramelle ungefüllt mit Schokoladenüberzug	\N
10165	1110	3	8571	7	Hartkaramelle gefüllt	\N
10166	1110	3	8572	8	Hartkaramelle gefüllt honighaltig u./o. malzhaltig	\N
10167	1110	3	8573	9	Hartkaramelle gefüllt alkoholhaltig	\N
10168	1110	3	8574	10	Hartkaramelle gefüllt mit Colaextrakt u. ä.	\N
10169	1110	3	8575	11	Hartkaramelle gefüllt mit Schokoladenüberzug	\N
10170	1110	3	8576	12	Hartkaramelle gefüllt milchhaltig	\N
10171	1110	3	8577	13	Hartkaramelle gefüllt mit Fruchtfüllungen	\N
10172	1110	3	13824	51	Hartkaramelle ungefüllt brennwertreduziert	\N
10173	1110	3	13825	57	Hartkaramelle gefüllt brennwertreduziert	\N
10174	1110	3	13826	96	Hartkaramellen-Mischung brennwertreduziert	\N
10175	1110	3	13827	97	Hart- und Weichkaramellen-Mischung brennwertreduziert	\N
10176	1110	3	8578	98	Hartkaramellenmischung	\N
10177	1110	3	8579	99	Hart- und Weichkaramellenmischung	\N
10178	1111	3	8581	1	Weichkaramelle ungefüllt	\N
10179	1111	3	8582	2	Weichkaramelle ungefüllt mit Milch u./o. Sahne u./o. Butter	\N
10180	1111	3	8583	3	Weichkaramelle ungefüllt mit Frucht- u./o. Kräuteraromen	\N
10181	1111	3	8584	4	Weichkaramelle ungefüllt mit Honig u./o. Malz	\N
10182	1111	3	8585	5	Weichkaramelle ungefüllt mit Colaextrakt	\N
10183	1111	3	8586	6	Weichkaramelle ungefüllt mit Schokoladenüberzug	\N
10184	1111	3	8587	7	Weichkaramelle gefüllt	\N
10185	1111	3	8588	8	Weichkaramelle gefüllt honighaltig u./o. malzhaltig	\N
10186	1111	3	8589	9	Weichkaramelle gefüllt alkoholhaltig	\N
10187	1111	3	8590	10	Weichkaramelle gefüllt mit Colaextrakt u. ä.	\N
10188	1111	3	8591	11	Weichkaramelle gefüllt mit Schokoladenüberzug	\N
10189	1111	3	8592	12	Weichkaramelle gefüllt milchhaltig	\N
10190	1111	3	8593	13	Weichkaramelle gefüllt mit Fruchtfüllungen	\N
10191	1111	3	13828	51	Weichkaramelle ungefüllt brennwertreduziert	\N
10192	1111	3	13829	57	Weichkaramelle gefüllt brennwertreduziert	\N
10193	1111	3	13830	98	Weichkaramellen-Mischung brennwertreduziert	\N
10194	1111	3	8594	99	Weichkaramellenmischung	\N
10195	1112	3	8596	1	Fondantmasse	\N
10196	1112	3	8597	2	Fondant	\N
10197	1112	3	8598	3	Fondant mit Überzug	\N
10198	1112	3	8599	4	Pfefferminzbruch	\N
10199	1112	3	8600	5	Kokosflocken aus Fondant	\N
10200	1112	3	8601	6	Fondantkonfekt	\N
10201	1112	3	8602	7	Fondantkrem	\N
10202	1112	3	8603	8	Trockenfondant	\N
10203	1113	3	8605	1	Gelee-Erzeugnis mit Fruchtaromen	\N
10204	1113	3	8606	2	Gelee-Erzeugnis mit Pfefferminzaromen	\N
10205	1114	3	8608	1	Gummibonbon mit Fruchtaromen	\N
10206	1114	3	8609	2	Weingummi	\N
10207	1114	3	8610	3	Gummibonbon mit Colaessenz	\N
10208	1114	3	8611	4	Gummibonbon mit Pfefferminzaromen	\N
10209	1114	3	8612	5	Gummibonbon mit Joghurt	\N
10210	1115	3	8614	1	Schaumkuss	\N
10211	1115	3	8615	2	Marshmallow	\N
10212	1115	3	8616	3	Schaumzuckerwaffel	\N
10213	1115	3	8618	5	Schaumzuckerware weiche	\N
10214	1115	3	8619	6	Schaumzuckerware harte	\N
10215	1115	3	8620	7	Hamburger Speck	\N
10216	1115	3	8621	99	Gummi- und Schaumzuckerwarenmischung	\N
10217	1116	3	8623	1	Türkischer Nougat	\N
10218	1116	3	8624	2	Halva	\N
10219	1116	3	8625	3	Nougat Montelimar	\N
10220	1117	3	8627	1	Lakritz	\N
10221	1117	3	8628	2	Lakritzkonfekt	\N
10222	1118	3	8630	1	Nonpareille	\N
10223	1118	3	8631	2	Liebesperle	\N
10224	1118	3	8632	3	Dragee-Streusel	\N
10225	1118	3	8633	4	Schokolade dragiert	\N
10226	1118	3	8634	5	Mandel gebrannt dragiert	\N
10227	1118	3	8635	6	Nuss gebrannt dragiert	\N
10228	1118	3	8636	7	Erdnuss gebrannt dragiert	\N
10229	1118	3	8637	8	Mandel nicht gebrannt dragiert	\N
10230	1118	3	8638	9	Nuss nicht gebrannt dragiert	\N
10231	1118	3	8639	10	Erdnuss nicht gebrannt dragiert	\N
10232	1118	3	8640	11	Dragee mit Zuckerüberzug	\N
10233	1118	3	8641	12	Dragee mit Schokoladenüberzug	\N
10234	1118	3	8642	13	Früchte dragiert	\N
10235	1118	3	8643	14	Walnuss gebrannt dragiert	\N
10236	1118	3	8644	15	Paranuss gebrannt dragiert	\N
10237	1118	3	8645	16	Puffreis dragiert	\N
10238	1118	3	8646	17	Pressling dragiert	\N
10239	1118	3	8647	18	Macadamianuss gebrannt dragiert	\N
10240	1118	3	8648	19	Kürbiskern gebrannt dragiert	\N
10241	1118	3	8649	20	Sonnenblumenkern gebrannt dragiert	\N
10242	1118	3	8650	21	Pistazie gebrannt dragiert	\N
10243	1118	3	13831	51	Dragee mit Zuckerüberzug brennwertreduziert	\N
10244	1118	3	13832	56	Puffreis dragiert brennwertreduziert	\N
10245	1118	3	13833	57	Pressling dragiert brennwertreduziert	\N
10246	1118	3	8651	99	Dragees gemischt	\N
10247	1120	3	8654	1	Kirsche kandiert	\N
10248	1120	3	8655	2	Banane kandiert	\N
10249	1120	3	8656	3	Orange kandiert	\N
10250	1120	3	8657	4	Ingwer kandiert	\N
10251	1120	3	8658	5	Ananas kandiert	\N
10252	1120	3	8659	6	Weintraube kandiert	\N
10253	1120	3	8660	7	Belegkirsche	\N
10254	1120	3	8661	8	Birne kandiert	\N
10255	1120	3	8662	99	Fruchtmischung kandiert	\N
10256	1121	3	8664	1	Schleckpulver	\N
10257	1121	3	8665	2	Limonadenpulver/-tablette	\N
10258	1121	3	8666	3	Brausetablette ohne Zucker	\N
10259	1121	3	13834	51	Schleckpulver brennwertreduziert	\N
10260	1121	3	13835	52	Limonadenpulver/-tablette brennwertreduziert	\N
10261	1123	3	8669	1	Krokant	\N
10262	1123	3	8670	2	Sesamkrokant	\N
10263	1123	3	8671	3	Erdnusskrokant	\N
10264	1123	3	8672	4	Haselnusskrokant	\N
10265	1123	3	8673	5	Kokoskrokant	\N
10266	1123	3	8674	6	Mandelkrokant	\N
10267	1123	3	8675	7	Sojakrokant	\N
10268	1123	3	14238	8	Pistazienkrokant	\N
10269	1124	3	8677	1	Kaugummi	\N
10270	1124	3	8678	2	Kaugummi Ballon	\N
10271	1124	3	8679	3	Kaugummi dragiert	\N
10272	1124	3	8680	4	Kaugummi gefüllt	\N
10273	1124	3	8681	5	Kaugummi gefüllt und dragiert	\N
10274	1124	3	8682	6	Kaumasse	\N
10275	1124	3	8683	7	Kaubonbon	\N
10276	1124	3	13836	57	Kaubonbon brennwertreduziert	\N
10277	1125	3	8685	1	Marzipanrohmasse	\N
10278	1125	3	8686	2	Marzipan	\N
10279	1125	3	8687	3	Marzipan mit Zusätzen	\N
10280	1125	3	8688	4	Marzipan überzogen	\N
10281	1126	3	8690	1	Persipanrohmasse	\N
10282	1126	3	8691	2	Persipan	\N
10283	1126	3	8692	3	Persipan mit Zusätzen	\N
10284	1126	3	8693	4	Persipan überzogen	\N
10285	1127	3	8695	1	Nougatrohmasse	\N
10286	1127	3	8696	2	Nougat	\N
10287	1127	3	8697	3	Nougat mit Zusätzen	\N
10288	1128	3	8699	1	Rumkugel	\N
10289	1128	3	8700	2	Kugel mit Rumgeschmack	\N
10290	1128	3	8701	3	Punschballen	\N
10291	1128	3	8702	4	Granatsplitter	\N
10292	1128	3	8703	5	Schnitte mit Milchkremfüllung	\N
10293	1129	3	8705	1	Mischung aus Marzipan- und Kakaoerzeugnissen	\N
10294	1130	3	8707	1	Paradiesapfel	\N
10295	1131	3	8709	1	Fruchtschnitte Zitrone	\N
10296	1131	3	8710	2	Fruchtschnitte Sanddorn	\N
10297	1131	3	8711	3	Fruchtschnitte Nuss	\N
10298	1131	3	8712	4	Fruchtschnitte Apfel	\N
10299	1131	3	8713	5	Fruchtschnitte Orange	\N
10300	1131	3	8714	6	Fruchtschnitte Banane	\N
10301	1131	3	8715	7	Fruchtschnitte Wildfrüchte	\N
10302	1131	3	8716	8	Fruchtschnitte Quitte	\N
10303	1131	3	8717	9	Fruchtschnitte Birne	\N
10304	1131	3	8718	10	Fruchtschnitte Erdbeere	\N
10305	1131	3	8719	11	Fruchtschnitte Himbeere	\N
10306	1131	3	8720	12	Fruchtschnitte Aprikose	\N
10307	1131	3	8721	13	Fruchtschnitte Pfirsich	\N
10308	1131	3	8722	14	Fruchtschnitte Hagebutte	\N
10309	1131	3	14192	15	Fruchtschnitte Traube	\N
10310	1131	3	14193	16	Fruchtschnitte Feige	\N
10311	1131	3	8723	99	Fruchtschnitte Fruchtmischung	\N
10312	1134	3	8728	1	Schokolade	\N
10313	1134	3	8729	2	Schokolade mit Zusätzen anderer LM	\N
10314	1134	3	8730	3	Schokolade gefüllt	\N
10315	1134	3	13412	4	Schokolade mit Qualitätshinweis	\N
10316	1135	3	8732	1	Haushaltsschokolade	\N
10317	1135	3	8733	2	Haushaltsschokolade mit Zusätzen anderer LM	\N
10318	1135	3	8734	3	Haushaltsschokolade gefüllt	\N
10319	1137	3	8737	1	Gianduja-Haselnussschokolade	\N
10320	1137	3	8738	2	Gianduja-Haselnussschokolade gefüllt	\N
10321	1138	3	8740	1	Schokoladeüberzugsmasse Kuvertüre	\N
10322	1138	3	8741	2	Schokoladeüberzugsmasse mit Zusätzen anderer LM	\N
10323	1139	3	8743	1	Milchschokolade	\N
10324	1139	3	8744	2	Milchschokolade mit Zusätzen anderer LM	\N
10325	1139	3	8745	3	Milchschokolade gefüllt	\N
10326	1139	3	13413	4	Milchschokolade mit Qualitätshinweis	\N
10327	1140	3	8747	1	Haushaltsmilchschokolade	\N
10328	1140	3	8748	2	Haushaltsmilchschokolade mit Zusätzen anderer LM	\N
10329	1140	3	8749	3	Haushaltsmilchschokolade gefüllt	\N
10330	1142	3	8752	1	Gianduja-Haselnussmilchschokolade	\N
10331	1142	3	8753	2	Gianduja-Haselnussmilchschokolade gefüllt	\N
10332	1143	3	8755	1	Milchschokoladeüberzugsmasse	\N
10333	1143	3	8756	2	Milchschokoladeüberzugsmasse mit Zusätzen anderer LM	\N
10334	1144	3	8758	1	Weiße Schokolade	\N
10335	1144	3	8759	2	Weiße Schokolade mit Zusätzen anderer LM	\N
10336	1144	3	8760	3	Weiße Schokolade gefüllt	\N
10337	1145	3	8762	1	Sahneschokolade	\N
10338	1145	3	8763	2	Sahneschokolade mit Zusätzen anderer LM	\N
10339	1145	3	8764	3	Sahneschokolade gefüllt	\N
10340	1145	3	13414	4	Sahneschokolade mit Qualitätshinweis	\N
10341	1146	3	8766	1	Sahneschokoladeüberzugsmasse	\N
10342	1146	3	8767	2	Sahneschokoladeüberzugsmasse mit Zusätzen anderer LM	\N
10343	1147	3	8769	1	Magermilchschokolade	\N
10344	1147	3	8770	2	Magermilchschokolade mit Zusätzen anderer LM	\N
10345	1147	3	8771	3	Magermilchschokolade gefüllt	\N
10346	1149	3	8774	1	Praline mit Kremfüllung	\N
10347	1149	3	8775	2	Praline mit Trüffelfüllung	\N
10348	1149	3	8776	3	Praline mit Ölsamenfüllung	\N
10349	1149	3	8777	4	Praline mit Marzipan- u./o. Nougatfüllung	\N
10350	1149	3	8778	5	Praline mit alkoholfreier flüssiger Füllung	\N
10351	1149	3	8779	6	Praline mit alkoholhaltiger Füllung	\N
10352	1149	3	8780	99	Praline mit gemischten Füllungen	\N
10353	1150	3	8782	1	Ananas mit Schokoladeüberzug	\N
10354	1150	3	8783	2	Banane mit Schokoladeüberzug	\N
10355	1150	3	8784	3	Kandite mit Schokoladeüberzug	\N
10356	1150	3	8785	4	Rosine mit Schokoladeüberzug	\N
10357	1150	3	8786	5	Trockenpflaume mit Schokoladeüberzug	\N
10358	1150	3	8787	6	Aprikose mit Schokoladeüberzug	\N
10359	1150	3	8788	7	Apfel mit Schokoladeüberzug	\N
10360	1150	3	8789	8	Apfelring mit Schokoladeüberzug	\N
10361	1150	3	8790	9	Erdbeere mit Schokoladeüberzug	\N
10362	1150	3	8791	99	Fruchtmischungen mit Schokoladeüberzug	\N
10363	1151	3	8793	1	Weiße Schokoladeüberzugsmasse	\N
10364	1151	3	8794	2	Weiße Schokoladeüberzugsmasse mit Zusätzen anderer LM	\N
10365	1152	3	8797	1	Kakaobohne roh unverlesen	\N
10366	1152	3	8798	2	Kakaobohne roh verlesen	\N
10367	1152	3	8799	3	Kakaobohne ungeröstet gereinigt	\N
10368	1152	3	8800	4	Kakaobohne geröstet gereinigt	\N
10369	1152	3	8801	5	Kakaogrus	\N
10370	1153	3	8803	1	Kakaomasse mit Lecithinzusatz	\N
10371	1153	3	8804	2	Kakaomasse aufgeschlossene	\N
10372	1154	3	8806	1	Kakaopresskuchen	\N
10373	1154	3	8807	2	Kakaopresskuchen stark entölt	\N
10374	1154	3	8808	3	Expeller Kakaopresskuchen	\N
10375	1155	3	8810	1	Kakaopulver schwach entölt	\N
10376	1155	3	8811	2	Kakaopulver stark entölt	\N
10377	1156	3	8813	1	Kakaopulver löslich schwach entölt	\N
10378	1156	3	8814	2	Kakaopulver löslich stark entölt	\N
10379	1156	3	8815	3	Kakaopulver löslich zur Herstellung von Instantgetränken	\N
10380	1157	3	8817	1	Kakaopulver löslich mit Lecithinzusatz schwach entölt	\N
10381	1157	3	8818	2	Kakaopulver löslich mit Lecithinzusatz stark entölt	\N
10382	1157	3	8819	3	Kakaopulver lösl. m. Lecithinz. zur Herst. von Instantgetränken	\N
10383	1158	3	8821	1	Kakaopulver mit natürlichen Gewürzen schwach entölt	\N
10384	1158	3	8822	2	Kakaopulver mit natürlichen Gewürzen stark entölt	\N
10385	1159	3	8824	1	Kakaopulver gezuckert Schokoladenpulver	\N
10386	1159	3	8825	2	Kakaopulver gezuckert stark entölt	\N
10387	1160	3	8827	1	Haushaltskakaopulver gezuckert schwach entölt	\N
10388	1160	3	8828	2	Haushaltskakaopulver gezuckert stark entölt	\N
10389	1161	3	8830	1	Kakaohaltige Getränkepulver	\N
10390	1161	3	8831	99	Kakaohaltige Mischungen	\N
10391	1162	3	8834	1	Kaffee roh	\N
10392	1162	3	8835	2	Kaffee roh entcoffeiniert	\N
10393	1162	3	8836	3	Kaffee roh säurearm	\N
10394	1163	3	8838	1	Kaffee geröstet	\N
10395	1163	3	8839	2	Kaffee geröstet entcoffeiniert	\N
10396	1163	3	8840	3	Kaffee geröstet säurearm	\N
10397	1163	3	8841	4	Kaffee geröstet entcoffeiniert säurearm	\N
10398	1163	3	13999	5	Kaffee geröstet aromatisiert	\N
10399	1163	3	14000	6	Kaffee geröstet entcoffeiniert aromatisiert	\N
10400	1164	3	8843	1	Kaffeextrakt	\N
10401	1164	3	8844	2	Kaffeextrakt entcoffeiniert	\N
10402	1164	3	8845	3	Kaffeextrakt säurearm	\N
10403	1164	3	8846	4	Kaffeextrakt entcoffeiniert säurearm	\N
10404	1164	3	8847	5	Getränkepulver mit Kaffee	\N
10405	1165	3	8849	1	Kaffeeersatzrohstoff aus stärkereichen Früchten	\N
10406	1165	3	8850	2	Kaffeeersatzrohstoff aus Wurzelgewächsen	\N
10407	1165	3	8851	3	Kaffeeersatzrohstoff aus zuckerreichen Früchten	\N
10408	1165	3	8852	4	Kaffeeersatzrohstoff aus gerbstoffreichen Pflanzenteilen	\N
10409	1166	3	8854	1	Gerstenkaffee	\N
10410	1166	3	8855	2	Roggenkaffee	\N
10411	1166	3	8856	3	Weizenkaffee	\N
10412	1166	3	8857	4	Zichorienkaffee	\N
10413	1166	3	8858	5	Malzkaffee	\N
10414	1166	3	8859	6	Feigenkaffee	\N
10415	1166	3	8860	7	Klettenwurzelkaffee	\N
10416	1166	3	8861	8	Lupinenkaffee	\N
10417	1166	3	8862	9	Milokornkaffee	\N
10418	1166	3	8863	10	Zuckerrübenkaffee	\N
10419	1166	3	8864	99	Mischungen aus Ersatzkaffee	\N
10420	1167	3	8866	1	Kaffeeersatzextrakt	\N
10421	1167	3	8867	2	Kaffeeersatzextrakt mit anderen LM	\N
10422	1167	3	14155	3	Getreidekaffeeextrakt	\N
10423	1168	3	8869	1	Kaffeegetränk	\N
10424	1168	3	8870	2	Kaffeegetränk entcoffeiniert	\N
10425	1168	3	8871	3	Kaffeegetränk entcoffeiniert säurearm	\N
10426	1168	3	8872	4	Kaffeegetränk mit anderen LM	\N
10427	1169	3	8874	1	Kaffeeersatzgetränk	\N
10428	1169	3	8875	2	Kaffeeersatzgetränk mit anderen LM	\N
10429	1169	3	8876	3	Malzkaffeegetränk	\N
10430	1169	3	8877	4	Malzkaffeegetränk mit anderen LM	\N
10431	1171	3	14156	1	Mischungen aus Getreidekaffeeextrakt mit Zichorienextrakt	\N
10432	1174	3	8883	1	Oolong Tee	\N
10433	1175	3	8886	1	Tee schwarz	\N
10434	1175	3	8888	3	Tee schwarz entcoffeiniert	\N
10435	1177	3	8891	1	Tee-Extrakt aus fermentiertem Tee	\N
10436	1177	3	8892	2	Tee-Extrakt aus fermentiertem entcoffeiniertem Tee	\N
10437	1177	3	8893	3	Tee-Extrakt aus unfermentiertem Tee	\N
10438	1178	3	8897	1	Brombeerblättertee	\N
10439	1178	3	8898	2	Pfefferminzblättertee	\N
10440	1178	3	8900	4	Kamillenblütentee	\N
10441	1178	3	8901	5	Lindenblütentee	\N
10442	1178	3	8902	6	Hibiskusblütentee	\N
10443	1178	3	8903	7	Hagebuttentee	\N
10444	1178	3	8904	8	Apfeltee	\N
10445	1178	3	8905	9	Matetee	\N
10446	1178	3	8906	10	Fencheltee	\N
10447	1178	3	8907	11	Himbeerblättertee	\N
10448	1178	3	8909	13	Kräutertee	\N
10449	1178	3	8910	15	Eisenkrauttee	\N
10450	1178	3	8911	16	Orangenblättertee	\N
10451	1178	3	8912	17	Krauseminzetee	\N
10452	1178	3	8913	18	Algen zur Teebereitung	\N
10453	1178	3	8914	19	Jasmintee	\N
10454	1178	3	8915	20	Früchtetee	\N
10455	1178	3	13415	21	Hanfblättertee	\N
10456	1178	3	13416	22	Rooibostee	\N
10457	1178	3	13800	23	Brennesseltee	\N
10458	1178	3	14282	24	Melissentee	\N
10459	1178	3	8916	99	Mischungen teeähnlicher Erzeugnisse	\N
10460	1180	3	8919	1	Teeaufguss aus unfermentiertem Tee	\N
10461	1180	3	8920	2	Teeaufguss aus halbfermentiertem Tee	\N
10462	1180	3	8921	3	Teeaufguss aus halbfermentiertem Tee aromatisiert	\N
10463	1180	3	8922	4	Teeaufguss aus fermentiertem Tee	\N
10464	1180	3	8923	5	Teeaufguss aus fermentiertem Tee aromatisiert	\N
10465	1180	3	8924	6	Teeaufguss aus entcoffeiniertem Tee	\N
10466	1180	3	8925	7	Aufguss aus teeähnlichen Erzeugnissen	\N
10467	1180	3	8926	8	Aufguss aus Mischungen v. Tee u. teeähnlichen Erzeugnissen	\N
10468	1180	3	8927	9	Teeaufguss mit anderen LM	\N
10469	1180	3	8928	10	Aufguss aus teeähnlichen Erzeugnissen mit anderen LM	\N
10470	1180	3	8929	11	Zitronenteegetränk	\N
10471	1181	3	8931	1	Aromatisierter Tee unfermentiert	\N
10472	1181	3	8932	2	Aromatisierter Tee halbfermentiert	\N
10473	1181	3	8933	3	Aromatisierter Tee fermentiert	\N
10474	1181	3	8934	4	Aromatisierter Tee entcoffeiniert	\N
10475	1183	3	8937	1	Aromatisierter Tee-Extrakt unfermentiert	\N
10476	1183	3	8938	2	Aromatisierter Tee-Extrakt halbfermentiert	\N
10477	1183	3	8939	3	Aromatisierter Tee-Extrakt fermentiert	\N
10478	1183	3	8940	4	Aromatisierter Tee-Extrakt entcoffeiniert	\N
10479	1184	3	8942	1	Fencheltee-Extrakt	\N
10480	1184	3	8943	2	Pfefferminztee-Extrakt	\N
10481	1184	3	8944	3	Krauseminzetee-Extrakt	\N
10482	1184	3	8945	4	Kamillentee-Extrakt	\N
10483	1184	3	8946	5	Orangenblättertee-Extrakt	\N
10484	1184	3	8947	6	Eisenkrauttee-Extrakt	\N
10485	1184	3	8948	7	Lindenblütentee-Extrakt	\N
10486	1184	3	8949	8	Hagebuttentee-Extrakt	\N
10487	1184	3	8950	9	Hibiskustee-Extrakt	\N
10488	1184	3	8951	10	Matetee-Extrakt	\N
10489	1184	3	8952	11	Brennesseltee-Extrakt	\N
10490	1184	3	8953	12	Kräutertee-Extrakt	\N
10491	1185	3	8955	1	Fencheltee aromatisierter Extrakt	\N
10492	1185	3	8956	2	Pfefferminztee aromatisierter Extrakt	\N
10493	1185	3	8957	3	Krauseminztee aromatisierter Extrakt	\N
10494	1185	3	8958	4	Kamillentee aromatisierter Extrakt	\N
10495	1185	3	8959	5	Orangenblättertee aromatisierter Extrakt	\N
10496	1185	3	8960	6	Eisenkrauttee aromatisierter Extrakt	\N
10497	1185	3	8961	7	Lindenblütentee aromatisierter Extrakt	\N
10498	1185	3	8962	8	Hagebuttentee aromatisierter Extrakt	\N
10499	1185	3	8963	9	Hibiskustee aromatisierter Extrakt	\N
10500	1185	3	8964	10	Matetee aromatisierter Extrakt	\N
10501	1185	3	8965	11	Brennesseltee aromatisierter Extrakt	\N
10502	1185	3	8966	12	Kräutertee aromatisierter Extrakt	\N
10503	1186	3	8968	1	Tee-Extrakt mit Zitronenauszügen oder Zitronenaroma	\N
10504	1186	3	8969	2	Tee-Extrakt mit sonst. geschmacks- oder geruchsgebenden Zusätzen	\N
10505	1186	3	8970	3	Getränkepulver mit Tee-Extrakt	\N
10506	1187	3	8972	1	Zubereitung mit Extrakten aus teeähnl. Erz. für Babys und Kleinkinder	\N
10507	1187	3	8973	2	Zubereitung mit Extrakten aus teeähnl. Erz. für Kinder und Jugendliche	\N
10508	1187	3	8974	3	Zubereitung mit Extrakten aus teeähnl. Erzeugnissen	\N
10509	1187	3	8975	4	Getränkepulver mit teeähnlichen Erzeugnissen	\N
10510	1188	3	8977	1	Pu Erh Tee	\N
10511	1188	3	8978	2	Pu Erh Tee mit anderen Lebensmitteln	\N
10512	1189	3	8981	1	gestr. jetzt 482500, Milchpulverzubereitung für Kleinkinder	\N
10513	1189	3	8982	2	gestr. jetzt 481006, Milchflüssignahrung adaptiert für Säugl.	\N
10514	1189	3	8983	3	gestr. jetzt 481205, Milchfertigbrei mit Obst für Säuglinge und Kleinkinder	\N
10515	1189	3	8984	4	gestr. jetzt 481205, Milchfertigbrei mit sonst. Zusätzen f. Säuglinge und Kleinkinder	\N
10516	1189	3	8985	5	gestr. jetzt 481205, Milchfertigbrei ohne Zusätze	\N
10517	1189	3	8986	6	gestr. jetzt 482500, Milchfreie Säuglingsfertignahrung auf Sojabasis	\N
10518	1189	3	8988	8	gestr. jetzt 481006, Milchpulverzubereitung adaptiert	\N
10519	1189	3	8991	11	gestr. jetzt 481002, Frühgeborenennahrung	\N
10520	1189	3	8992	12	gestr. jetzt 481006, Säuglingsanfangsnahrung flüssig	\N
10521	1189	3	8993	13	gestr. jetzt 481006, Säuglingsanfangsnahrung Pulver	\N
10522	1190	3	8997	1	gestr. jetzt 481200, Reisschleim für Säuglinge und Kleinkinder	\N
10523	1190	3	8998	2	gestr. jetzt 481200, Haferbrei für Säuglinge und Kleinkinder	\N
10524	1190	3	8999	3	gestr. jetzt 481201, Weizengrieß für Säuglinge und Kleinkinder	\N
10525	1190	3	9000	4	gestr. jetzt 481200, Mehrkornerzeugnis für Säuglinge und Kleinkinder	\N
10526	1190	3	9001	5	gestr. jetzt 481200, Getreidebrei mit Obst für Säuglinge und Kleinkinder	\N
10527	1190	3	9002	6	gestr. jetzt 481200, Getreidebrei mit sonst. Zusätzen für Säuglinge und Kleinkinder	\N
10528	1190	3	9004	8	gestr. jetzt 481200, Vollkornerzeugnis für Säuglinge und Kleinkinder	\N
10529	1191	3	9007	1	gestr. jetzt 481403, Karottensaft für Säuglinge und Kleinkinder	\N
10530	1191	3	9008	2	gestr. jetzt 481401, Fruchtsaft für Säuglinge und Kleinkinder	\N
10531	1191	3	9009	3	gestr. jetzt 481405, Karottentrunk für Säuglinge und Kleinkinder	\N
10532	1191	3	9010	4	gestr. jetzt 481405, Obstgetränk für Säuglinge und Kleinkinder	\N
10533	1191	3	9011	5	gestr. jetzt 481407, Gemüsezubereitung andere für Säuglinge und Kleinkinder	\N
10534	1191	3	9012	6	gestr. jetzt 481406, Obstbrei für Säuglinge und Kleinkinder	\N
10535	1191	3	9013	7	gestr. jetzt 481405, Gemüsegetränk für Säuglinge und Kleinkinder	\N
10536	1191	3	9014	8	gestr. jetzt 481405, Obst- und Gemüsegetränk für Säuglinge und Kleinkinder	\N
10537	1191	3	9015	9	gestr. jetzt 481407, Vollkorn-Gemüsezubereitung für Säuglinge	\N
10538	1191	3	9016	10	gestr. jetzt 481408, Vollkorn-Obstzubereitung für Säuglinge	\N
10539	1192	3	9018	1	gestr. jetzt 481302, Fertigmenü für Säuglinge mit Rindfleisch	\N
10540	1192	3	9019	2	gestr. jetzt 481305, Fertigmenü für Säuglinge mit Geflügel	\N
10541	1192	3	9022	5	gestr. jetzt 481308, Fertigmenü für Säuglinge mit Geflügel und Innereien	\N
10542	1192	3	9023	6	gestr. jetzt 481301, Fertigmenü für Säuglinge mit Kalbfleisch	\N
10543	1192	3	9024	7	gestr. jetzt 481303, Fertigmenü für Säuglinge mit Schweinefleisch	\N
10544	1192	3	9025	8	gestr. jetzt 481303, Fertigmenü für Säuglinge mit Schinken	\N
10545	1192	3	9026	9	gestr. jetzt 481308, Fertigmenü für Säuglinge ohne tierische Erzeugnisse	\N
10546	1193	3	9028	1	gestr. jetzt 481320, Kleinkindmenü mit Kalbfleisch	\N
10547	1193	3	9029	2	gestr. jetzt 481321, Kleinkindmenü mit Rindfleisch	\N
10548	1193	3	9030	3	gestr. jetzt 481322, Kleinkindmenü mit Schweinefleisch	\N
10549	1193	3	9031	4	gestr. jetzt 481327, Kleinkindmenü mit Lammfleisch	\N
10550	1193	3	9032	5	gestr. jetzt 481324, Kleinkindmenü mit Geflügel	\N
10551	1193	3	9033	6	gestr. jetzt 481325, Kleinkindmenü mit Fisch	\N
10552	1193	3	9034	7	gestr. jetzt 481323, Kleinkindmenü mit Innereien	\N
10553	1193	3	9035	8	gestr. jetzt 481322, Kleinkindmenü mit Schinken	\N
10554	1193	3	9036	9	gestr. jetzt 481327, Kleinkindmenü mit Wurst	\N
10555	1193	3	9037	10	gestr. jetzt 481327, Kleinkindmenü mit sonst. Zusätzen	\N
10556	1193	3	9038	11	gestr. jetzt 481327, Kleinkindmenü mit Geflügel und Innereien	\N
10557	1193	3	9039	12	gestr. jetzt 481327, Kleinkindmenü mit Ei	\N
10558	1213	3	9124	1	Maultasche auch tiefgefroren	\N
10559	1213	3	9125	2	Ravioli gefüllt auch tiefgefroren	\N
10560	1213	3	9126	3	Kohlroulade auch tiefgefroren	\N
10561	1213	3	9127	4	Gefüllte Paprika auch tiefgefroren	\N
10562	1213	3	9128	5	Gefüllte Ente auch tiefgefroren	\N
10563	1213	3	9129	6	Gefüllte Gans auch tiefgefroren	\N
10564	1213	3	9130	7	Gefülltes Huhn auch tiefgefroren	\N
10565	1213	3	9131	8	Champignon Plätzle auch tiefgefroren	\N
10566	1213	3	9132	9	Jägerklößchen in Pilzsoße auch tiefgefroren	\N
10567	1213	3	9133	10	Cordon bleu vom Hähnchen auch tiefgefroren	\N
10568	1213	3	9134	11	Markklößchen Markerzeugnis Teilfertiggericht auch tiefgefroren	\N
10569	1213	3	9135	12	Omelette auch gefüllt auch tiefgefroren	\N
10570	1213	3	9136	13	Tortellini gefüllt auch tiefgefroren	\N
10571	1213	3	9137	14	Cannelloni gefüllt auch tiefgefroren	\N
10572	1213	3	9138	15	Lasagne gefüllt auch tiefgefroren	\N
10573	1213	3	9139	16	Cordon bleu vom Kalb auch tiefgefroren	\N
10574	1213	3	9140	17	Cordon bleu vom Schwein auch tiefgefroren	\N
10575	1213	3	9141	18	Rahmspinat auch tiefgefroren	\N
10576	1213	3	9142	19	Rahmblumenkohl auch tiefgefroren	\N
10577	1213	3	9143	20	Rahmporree auch tiefgefroren	\N
10578	1213	3	9144	21	Käse Plätzle auch tiefgefroren	\N
10579	1213	3	9145	22	Gefüllte Weinblätter auch tiefgefroren	\N
10580	1213	3	9146	23	Aubergine in Tomatensoße auch tiefgefroren	\N
10581	1213	3	9147	24	Bohnen in Tomatensoße auch tiefgefroren	\N
10582	1213	3	9148	25	Bohnen in Tomatensoße mit Fleisch auch tiefgefroren	\N
10583	1213	3	9149	26	Gemüsebratling/-röstling auch tiefgefroren	\N
10584	1213	3	9150	27	Getreidebratling/-röstling auch tiefgefroren	\N
10585	1213	3	9151	28	Gemüsebratling/-röstlingvormischung	\N
10586	1213	3	9152	29	Getreidebratling/-röstlingvormischung	\N
10587	1213	3	9153	30	Rahmkohlrabi auch tiefgefroren	\N
10588	1213	3	9154	32	Gemüsemischung mit Soße auch tiefgefroren	\N
10589	1213	3	9155	33	Blätterteig mit fleischhaltiger Füllung auch tiefgefroren	\N
10590	1213	3	9156	34	Blätterteig mit gemüsehaltiger Füllung auch tiefgefroren	\N
10591	1213	3	9157	35	Blätterteig mit fischhaltiger Füllung auch tiefgefroren	\N
10592	1213	3	9158	36	Sauerkraut mit Kasseler auch tiefgefroren	\N
10593	1213	3	9159	37	Eintopf ohne Fleisch auch tiefgefroren	\N
10594	1213	3	9160	38	Sojabratling auch tiefgefroren	\N
10595	1213	3	9161	39	Antipasti aus Gemüse auch tiefgefroren	\N
10596	1213	3	9162	40	Gemüse-/Grünkernbratling auch tiefgefroren	\N
10597	1213	3	9163	41	Maisfladen für Tacos auch tiefgefroren	\N
10598	1213	3	9164	42	Speise aus Gemeinschaftsverpflegung auch tiefgefroren	\N
10599	1213	3	13484	43	Würstchen vegetarische auch tiefgefroren	\N
10600	1213	3	13485	44	Antipasti	\N
10601	1213	3	13486	45	Cordon bleu Pute auch tiefgefroren	\N
10602	1213	3	13487	46	Teigware gefüllt auch tiefgefroren	\N
10603	1213	3	13801	47	Schwarzsauer - regionale Spezialität	\N
10604	1214	3	9166	1	Bami Goreng auch tiefgefroren	\N
10605	1214	3	9167	2	Nasi Goreng auch tiefgefroren	\N
10606	1214	3	9168	3	China Gericht süßsauer auch tiefgefroren	\N
10607	1214	3	9169	4	China Gericht Chop Soey auch tiefgefroren	\N
10608	1214	3	9170	5	Pizza auch tiefgefroren	\N
10609	1214	3	9171	6	Gänsebraten mit Rotkohl und Kartoffeln auch tiefgefroren	\N
10610	1214	3	9172	7	Pichelsteiner Eintopf auch tiefgefroren	\N
10611	1214	3	9173	8	Serbisches Reisfleisch auch tiefgefroren	\N
10612	1214	3	9174	9	Ravioli in Tunke auch tiefgefroren	\N
10816	1241	3	9403	18	Bieressig	\N
10613	1214	3	9175	10	Reis mit Huhn auch tiefgefroren	\N
10614	1214	3	9176	11	Gemüseallerlei mit Rindfleisch auch tiefgefroren	\N
10615	1214	3	9177	12	Linseneintopf mit Wurst auch tiefgefroren	\N
10616	1214	3	9178	13	Seelachsfilet mit Gemüse auch tiefgefroren	\N
10617	1214	3	9179	14	Szegediner Gulasch auch tiefgefroren	\N
10618	1214	3	9180	15	Bohneneintopf mit Wurst auch tiefgefroren	\N
10619	1214	3	9181	16	Bohneneintopf mit Speck auch tiefgefroren	\N
10620	1214	3	9182	17	Erbseneintopf mit Wurst auch tiefgefroren	\N
10621	1214	3	9183	18	Erbseneintopf mit Speck auch tiefgefroren	\N
10622	1214	3	9184	19	Labskaus auch tiefgefroren	\N
10623	1214	3	9185	20	Frühlingsrolle auch tiefgefroren	\N
10624	1214	3	9186	21	Nudeleintopf mit Wurst auch tiefgefroren	\N
10625	1214	3	9187	22	Nudeleintopf mit Fleisch auch tiefgefroren	\N
10626	1214	3	9188	23	Nudeleintopf mit Speck auch tiefgefroren	\N
10627	1214	3	9189	24	Kartoffelsuppe mit Wurst auch tiefgefroren	\N
10628	1214	3	9190	25	Kartoffelsuppe mit Fleisch auch tiefgefroren	\N
10629	1214	3	9191	26	Kartoffelsuppe mit Speck auch tiefgefroren	\N
10630	1214	3	9192	27	Gemüseeintopf mit Wurst auch tiefgefroren	\N
10631	1214	3	9193	28	Gemüseeintopf mit Fleisch auch tiefgefroren	\N
10632	1214	3	9194	29	Gemüseeintopf mit Speck auch tiefgefroren	\N
10633	1214	3	9195	30	Pizza Baguette auch tiefgefroren	\N
10634	1214	3	9196	31	Linseneintopf mit Speck auch tiefgefroren	\N
10635	1214	3	9197	32	Seefisch mit Käseeinlage auch tiefgefroren	\N
10636	1214	3	9198	33	Muschelrisotto auch tiefgefroren	\N
10637	1214	3	9199	34	Bratwurst in Soße auch tiefgefroren	\N
10638	1214	3	9200	35	Reis mit Früchten auch tiefgefroren	\N
10639	1214	3	9201	36	Rührei mit Schinken auch tiefgefroren	\N
10640	1214	3	9202	37	Paella auch tiefgefroren	\N
10641	1214	3	9203	38	Pastete gefüllt auch tiefgefroren	\N
10642	1214	3	9204	39	Gemüsetorte auch tiefgefroren	\N
10643	1214	3	9205	40	Speckkuchen auch tiefgefroren	\N
10644	1214	3	9206	41	Zwiebelkuchen auch tiefgefroren	\N
10645	1214	3	9207	42	Teigwaren mit Soße auch tiefgefroren	\N
10646	1214	3	9208	43	Seefisch mit Auflage auch tiefgefroren	\N
10647	1214	3	9209	44	Krautkuchen auch tiefgefroren	\N
10648	1214	3	9210	45	Reis mit Gemüse auch tiefgefroren	\N
10649	1214	3	9211	46	Nudelauflauf auch tiefgefroren	\N
10650	1214	3	9212	47	Asiatische Gerichte auch tiefgefroren	\N
10651	1214	3	9213	48	Cevapcici mit Reis und Balkangemüse auch tiefgefroren	\N
10652	1214	3	9214	49	Rinderroulade mit Kartoffelpüree und Rotkohl auch tiefgefroren	\N
10653	1214	3	9215	50	Sauerkraut mit Eisbein auch tiefgefroren	\N
10654	1214	3	9216	51	Sauerkraut mit Pökelfleisch auch tiefgefroren	\N
10655	1214	3	9217	52	Ragout fin mit Beilage auch tiefgefroren	\N
10656	1214	3	9218	53	Putenragout mit Beilage auch tiefgefroren	\N
10657	1214	3	9219	54	Königsberger Klopse in Soße auch mit anderen Beilagen auch tiefgefroren	\N
10658	1214	3	9220	55	Tacos/Rollos (gefüllte Teigtaschen) auch tiefgefroren	\N
10659	1214	3	9221	56	Sauerbraten mit Beilage auch tiefgefroren	\N
10660	1214	3	9222	57	Pfannengericht nach Gyrosart auch tiefgefroren	\N
10661	1214	3	9223	58	Nudelgericht ohne Fleisch auch tiefgefroren	\N
10662	1214	3	13488	59	Grünkohleintopf mit Wurst auch tiefgefroren	\N
10663	1214	3	13489	60	Grünkohleintopf mit Speck auch tiefgefroren	\N
10664	1214	3	13490	61	Eintopf mit Hühnerfleisch auch tiefgefroren	\N
10665	1214	3	13491	62	Sushi	\N
10666	1214	3	13492	63	Sushi aus Fisch u./o. Krebs- u./o. Weichtieren	\N
10667	1214	3	14002	64	Jakobsmuschelgericht auch tiefgefroren	\N
10668	1214	3	14003	65	Kammmuschelgericht auch tiefgefroren	\N
10669	1214	3	14004	66	Venusmuschelgericht auch tiefgefroren	\N
10670	1214	3	14005	67	Klaffmuschelgericht auch tiefgefroren	\N
10671	1214	3	14006	68	Trogmuschelgericht auch tiefgefroren	\N
10672	1214	3	14007	69	Miesmuschelgericht auch tiefgefroren	\N
10673	1214	3	14008	70	Herzmuschelgericht auch tiefgefroren	\N
10674	1215	3	9225	1	Belegtes Brötchen	\N
10675	1215	3	9226	2	Belegtes Brot	\N
10676	1215	3	9227	3	Belegtes Baguette	\N
10677	1215	3	9228	4	Menü	\N
10678	1215	3	9229	5	Hot Dog	\N
10679	1215	3	9230	8	Cheeseburger	\N
10680	1215	3	13493	9	Hamburger	\N
10681	1215	3	14194	10	Sandwich	\N
10682	1216	3	9232	1	Maultasche Konserve	\N
10683	1216	3	9233	2	Ravioli gefüllt Konserve	\N
10684	1216	3	9234	3	Kohlroulade Konserve	\N
10685	1216	3	9235	4	Gefüllte Paprika Konserve	\N
10686	1216	3	9236	5	Gefüllte Ente Konserve	\N
10687	1216	3	9237	6	Gefüllte Gans Konserve	\N
10688	1216	3	9238	7	Gefülltes Huhn Konserve	\N
10689	1216	3	9239	8	Champignon Plätzle Konserve	\N
10690	1216	3	9240	9	Jägerklößchen in Pilzsoße Konserve	\N
10691	1216	3	9241	11	Markklößchen Markerzeugnis Konserve	\N
10692	1216	3	9242	12	Tortellini gefüllt Konserve	\N
10693	1216	3	9243	13	Cannelloni gefüllt Konserve	\N
10694	1216	3	9244	14	Lasagne gefüllt Konserve	\N
10695	1216	3	9245	17	Rahmspinat Konserve	\N
10696	1216	3	9246	18	Rahmblumenkohl Konserve	\N
10697	1216	3	9247	19	Rahmporree Konserve	\N
10698	1216	3	9248	20	Käse Plätzle Konserve	\N
10699	1216	3	9249	21	Gefüllte Weinblätter Konserve	\N
10700	1216	3	9250	22	Aubergine in Tomatensoße Konserve	\N
10701	1216	3	9251	23	Bohnen in Tomatensoße Konserve	\N
10702	1216	3	9252	24	Bohnen in Tomatensoße mit Fleisch Konserve	\N
10703	1216	3	9253	25	Rahmkohlrabi Konserve	\N
10704	1216	3	9254	26	Gemüsemischung mit Soße Konserve	\N
10705	1217	3	9256	1	Bami Goreng Konserve	\N
10706	1217	3	9257	2	Nasi Goreng Konserve	\N
10707	1217	3	9258	3	China Gericht süßsauer Konserve	\N
10708	1217	3	9259	4	China Gericht Chop Soey Konserve	\N
10709	1217	3	9260	5	Gänsebraten mit Rotkohl und Kartoffeln Konserve	\N
10710	1217	3	9261	6	Pichelsteiner Eintopf Konserve	\N
10711	1217	3	9262	7	Serbisches Reisfleisch Konserve	\N
10712	1217	3	9263	8	Ravioli in Tunke Konserve	\N
10713	1217	3	9264	9	Reis mit Huhn Konserve	\N
10714	1217	3	9265	10	Gemüseallerlei mit Rindfleisch Konserve	\N
10715	1217	3	9266	11	Linseneintopf mit Wurst Konserve	\N
10716	1217	3	9267	12	Seelachsfilet mit chinesischem Gemüse Konserve	\N
10717	1217	3	9268	13	Szegedinger Gulasch Konserve	\N
10718	1217	3	9269	14	Bohneneintopf mit Wurst Konserve	\N
10719	1217	3	9270	15	Bohneneintopf mit Speck Konserve	\N
10720	1217	3	9271	16	Erbseneintopf mit Wurst Konserve	\N
10721	1217	3	9272	17	Erbseneintopf mit Speck Konserve	\N
10722	1217	3	9273	18	Labskaus Konserve	\N
10723	1217	3	9274	19	Nudeleintopf mit Wurst Konserve	\N
10724	1217	3	9275	20	Nudeleintopf mit Fleisch Konserve	\N
10725	1217	3	9276	21	Nudeleintopf mit Speck Konserve	\N
10726	1217	3	9277	22	Kartoffelsuppe mit Wurst Konserve	\N
10727	1217	3	9278	23	Kartoffelsuppe mit Fleisch Konserve	\N
10728	1217	3	9279	24	Kartoffelsuppe mit Speck Konserve	\N
10729	1217	3	9280	25	Gemüseeintopf mit Wurst Konserve	\N
10730	1217	3	9281	26	Gemüseeintopf mit Fleisch Konserve	\N
10731	1217	3	9282	27	Gemüseeintopf mit Speck Konserve	\N
10732	1217	3	9283	28	Linseneintopf mit Speck Konserve	\N
10733	1217	3	9284	29	Muschelrisotto Konserve	\N
10734	1217	3	9285	30	Bratwurst in Soße Konserve	\N
10735	1217	3	9286	31	Reis mit Früchten Konserve	\N
10736	1217	3	9287	32	Paella Konserve	\N
10737	1217	3	9288	33	Teigwaren mit Soße Konserve	\N
10738	1217	3	9289	34	Sauerkraut mit Eisbein Konserve	\N
10739	1217	3	9290	35	Sauerkraut mit Pökelfleisch Konserve	\N
10740	1217	3	9291	36	Cevapcici mit Reis und Balkangemüse Konserve	\N
10741	1217	3	9292	37	Suppentopf ohne Fleischeinlage Konserve	\N
10742	1217	3	9293	38	Gemüsetopf ohne Fleischeinlage Konserve	\N
10743	1217	3	13494	39	Grünkohleintopf mit Wurst Konserve	\N
10744	1217	3	13495	40	Grünkohleintopf mit Speck Konserve	\N
10745	1217	3	13496	41	Eintopf mit Hühnerfleisch Konserve	\N
10746	1217	3	14009	42	Miesmuschelgericht Konserve	\N
10747	1217	3	14010	43	Herzmuschelgericht Konserve	\N
10748	1217	3	14011	44	Jakobsmuschelgericht Konserve	\N
10749	1217	3	14012	45	Kammmuschelgericht Konserve	\N
10750	1217	3	14013	46	Venusmuschelgericht Konserve	\N
10751	1217	3	14014	47	Klaffmuschelgericht Konserve	\N
10752	1217	3	14015	48	Trogmuschelgericht Konserve	\N
10753	1218	3	9295	1	Nudel mit Fleischsoße zusammengesetztes Fertiggericht trocken	\N
10754	1219	3	9297	1	Gemüsebratling trocken	\N
10755	1219	3	9298	2	Grünkernbratling trocken	\N
10756	1219	3	9299	3	Teigware asiatische mit Soßenpulver trocken	\N
10757	1219	3	9300	4	Gemüse-/Grünkernbratling trocken	\N
10758	1220	3	9302	1	Tageskost einschl. flüssiger Bestandteile und Getränke	\N
10759	1220	3	9303	2	Tageskost einschl. flüssiger Bestandteile ohne Getränke	\N
10760	1220	3	9304	3	Tageskost nur feste Bestandteile	\N
10761	1220	3	9305	4	Tageskost nur flüssige Bestandteile und Getränke	\N
10762	1220	3	9306	5	Tageskost nur flüssige Bestandteile	\N
10763	1220	3	9307	6	Tageskost nur Getränke	\N
10764	1220	3	9308	7	Tageskost besondere Ernährungsform	\N
10765	1221	3	9345	1	gestrichen, Gelatinepräparat mit Mineralstoffzusatz	\N
10766	1238	3	9351	1	Tomatenketchup	\N
10767	1238	3	9352	2	Worcestersoße	\N
10768	1238	3	9353	3	Sojasoße	\N
10769	1238	3	9354	4	Chutney	\N
10770	1238	3	9355	5	Tabasko	\N
10771	1238	3	9356	6	Chilisoße	\N
10772	1238	3	9357	7	Jägersoße	\N
10773	1238	3	9358	8	Barbecuesoße	\N
10774	1238	3	9359	9	Schaschliksoße	\N
10775	1238	3	9360	10	Zigeunersoße	\N
10776	1238	3	9361	11	Currysoße	\N
10777	1238	3	9362	12	Relish	\N
10778	1238	3	9363	13	Curryketchup	\N
10779	1238	3	9364	14	Cumberlandsoße	\N
10780	1238	3	9365	15	Sambal	\N
10781	1238	3	9366	16	Gewürzketchup	\N
10782	1238	3	9367	17	Senfsoße	\N
10783	1238	3	9368	18	Meerrettichsoße	\N
10784	1238	3	9369	19	Ajvar	\N
10785	1238	3	9370	20	Knoblauchsoße	\N
10786	1238	3	9371	21	Duvec	\N
10787	1238	3	9372	22	Hoi-Sui-Soße	\N
10788	1238	3	9373	23	Würzpaste mit Öl	\N
10789	1238	3	9374	24	Süß-Sauer-Soße	\N
10790	1238	3	9375	25	Letscho	\N
10791	1238	3	9376	26	Würzpaste aus Tomatenketchup und Mayonnaise	\N
10792	1238	3	9377	27	Pesto	\N
10793	1238	3	9378	28	Hamburger Soße	\N
10794	1238	3	9379	29	Hot-Dog-Soße	\N
10795	1238	3	9380	30	Fischsoße asiatische	\N
10796	1238	3	9381	31	Würzsoße/-paste aus frischen Kräutern in Öl und Essig	\N
10797	1240	3	9384	1	Würzmischung mit Glutamat mit Gewürzzusatz/Gewürzextr.	\N
10798	1240	3	14204	2	Würzmischung ohne Glutamat mit Gewürzzusatz	\N
10799	1241	3	9386	1	Weinessig	\N
10800	1241	3	9387	2	Wein-/Branntweinessig	\N
10801	1241	3	9388	3	Obstessig	\N
10802	1241	3	9389	4	Kräuteressig	\N
10803	1241	3	9390	5	Essig aus Essigsäure	\N
10804	1241	3	9391	6	Essig mit Zitronensaft	\N
10805	1241	3	9392	7	Essighaltige Aufgussflüssigkeit für Sauerkonserven	\N
10806	1241	3	9393	8	Würze zitronensafthaltig	\N
10807	1241	3	9394	9	Weinessig mit beigegebenen sichtbaren LM	\N
10808	1241	3	9395	10	Obstessig mit beigegebenen sichtbaren LM	\N
10809	1241	3	9396	11	Essigessenz	\N
10810	1241	3	9397	12	Branntweinessig	\N
10811	1241	3	9398	13	Balsamessig	\N
10812	1241	3	9399	14	Essig aus Sherry	\N
10813	1241	3	9400	15	Essig aus Essigessenz	\N
10814	1241	3	9401	16	Knoblauchessig	\N
10815	1241	3	9402	17	Dillessig	\N
10817	1241	3	9404	19	Essig aus Traubenmost	\N
10818	1241	3	9405	20	Essig aus Traubenmost und Wein	\N
10819	1241	3	9406	21	Weinessig aromatisiert	\N
10820	1241	3	9407	22	Branntweinessig aromatisiert	\N
10821	1241	3	9408	23	Essig aromatisiert aus verschiedenen Rohstoffen	\N
10822	1241	3	9409	24	Branntweinessig mit beigegebenen sichtbaren LM	\N
10823	1241	3	9410	25	Essig aus verschiedenen Rohstoffen mit beigegebenen sichtbaren LM	\N
10824	1241	3	9411	26	Weinessig mit beigegebenen Säften	\N
10825	1241	3	9412	27	Branntweinessig mit beigegebenen Säften	\N
10826	1241	3	9413	28	Obstessig mit beigegebenen Säften	\N
10827	1241	3	9414	29	Essig aus verschiedenen Rohstoffen mit beigegebenen Säften	\N
10828	1241	3	9415	30	Essig mit Würzen	\N
10829	1241	3	9416	31	Essigspezialitäten	\N
10830	1241	3	9417	99	Essig aus verschiedenen Rohstoffen	\N
10831	1242	3	9419	1	Steinsalz	\N
10832	1242	3	9420	2	Hüttensalz	\N
10833	1242	3	9421	3	Meersalz	\N
10834	1242	3	9422	4	Salinensalz	\N
10835	1242	3	9423	5	Speisesalz jodiert	\N
10836	1242	3	9424	6	Speisesalz fluoridiert	\N
10837	1242	3	9425	7	Speisesalz jodiert und fluoridiert	\N
10838	1242	3	9426	99	Speisesalzmischung	\N
10839	1243	3	9428	1	Speisesenf mild	\N
10840	1243	3	9429	2	Speisesenf mittelscharf	\N
10841	1243	3	9430	3	Speisesenf scharf	\N
10842	1243	3	9431	4	Speisesenf süß	\N
10843	1243	3	9432	5	Gewürzsenf	\N
10844	1243	3	9433	6	Kräutersenf	\N
10845	1243	3	9434	7	Tomatensenf	\N
10846	1243	3	9435	8	Meerrettichsenf	\N
10847	1243	3	9436	9	Sardellensenf	\N
10848	1243	3	9437	10	Speisesenf extra scharf	\N
10849	1243	3	9438	11	Dijon-Senf	\N
10850	1244	3	9440	1	Meerrettich gerieben	\N
10851	1244	3	9441	2	Tafelmeerrettich	\N
10852	1244	3	9442	3	Sahnemeerrettich	\N
10853	1244	3	9443	4	Apfelmeerrettich	\N
10854	1244	3	9444	5	Preiselbeermeerrettich	\N
10855	1245	3	9453	10	Würzmittelzubereitung für Fleischgerichte	\N
10856	1245	3	9454	11	Würzmittelzubereitung für Fischgerichte	\N
10857	1245	3	9455	12	Würzmittelzubereitung für Gerichte ohne Fleisch/Fisch	\N
10858	1245	3	9456	13	Würzmittelzubereitung für warme Soßen	\N
10859	1245	3	9457	14	Würzmittelzubereitung für Salatsoßen	\N
10860	1247	3	9460	1	Zwiebel-Pfeffer-Gewürzzubereitung	\N
10861	1247	3	9461	2	Brathähnchen-Gewürzzubereitung	\N
10862	1247	3	9462	3	Gewürzpräparat für Wurst	\N
10863	1247	3	9463	4	Suppen-Gewürzzubereitung	\N
10864	1247	3	9464	5	Fleisch-Gewürzzubereitung	\N
10865	1247	3	9465	6	Schaschlik-Gewürzzubereitung	\N
10866	1247	3	9466	8	China-Gewürzzubereitung	\N
10867	1247	3	9467	9	Grill-Steak-Gewürzzubereitung	\N
10868	1247	3	9468	10	Bratkartoffel-Gewürzzubereitung	\N
10869	1247	3	9469	11	Würzmarinade	\N
10870	1248	3	9472	1	Brathähnchen-Gewürzsalz	\N
10871	1248	3	9473	2	Hackfleisch-Gewürzsalz	\N
10872	1248	3	9474	3	Kotelett- und Schnitzel-Gewürzsalz	\N
10873	1248	3	9475	4	Gyros-Gewürzsalz	\N
10874	1248	3	9476	5	Steak-Gewürzsalz	\N
10875	1248	3	9477	6	Bratfisch-Gewürzsalz	\N
10876	1248	3	9478	7	Kräuter-Gewürzsalz	\N
10877	1248	3	9479	8	Tomaten-Gewürzsalz	\N
10878	1248	3	9480	9	Zwiebel-Gewürzsalz	\N
10879	1248	3	9481	10	Knoblauch-Gewürzsalz	\N
10880	1248	3	9482	11	Gulasch-Gewürzsalz	\N
10881	1249	3	9484	1	Sellerieextraktsalz	\N
10882	1249	3	9485	2	Knoblauchextraktsalz	\N
10883	1249	3	9486	3	Rauchextraktsalz	\N
10884	1249	3	9487	4	Kräuterextraktsalz	\N
10885	1249	3	9488	5	Zwiebelextraktsalz	\N
10886	1249	3	9489	6	Brathähnchengewürzextraktsalz	\N
10887	1249	3	9490	7	Kräuterextraktsalz jodiert	\N
10888	1251	3	9493	1	Brathähnchen Gewürzaromazubereitung	\N
10889	1252	3	9495	1	Gewürzaromapräparat für Wurst	\N
10890	1253	3	9497	1	Rohwurstreifemittel mit Gewürzen	\N
10891	1254	3	9499	1	Gyros-Würzer	\N
10892	1254	3	9500	2	Hackfleisch-Würzer	\N
10893	1254	3	9501	3	Geflügel-Würzer	\N
10894	1254	3	9502	4	Gulasch-Würzer	\N
10895	1255	3	9505	1	Ingwer Wurzelgewürz	\N
10896	1255	3	9506	2	Kurkuma Wurzelgewürz	\N
10897	1255	3	9507	3	Zitwer Wurzelgewürz	\N
10898	1255	3	9508	4	Galgant Wurzelgewürz	\N
10899	1255	3	9509	5	Kalmus Wurzelgewürz	\N
10900	1255	3	9510	6	Liebstöckelwurzel Wurzelgewürz	\N
10901	1256	3	9512	1	Basilikum Blattgewürz	\N
10902	1256	3	9513	2	Beifuß Blattgewürz	\N
10903	1256	3	9514	3	Bohnenkraut Blattgewürz	\N
10904	1256	3	9515	4	Borretsch Blattgewürz	\N
10905	1256	3	9516	5	Dill Blattgewürz	\N
10906	1256	3	9517	6	Estragon Blattgewürz	\N
10907	1256	3	9518	7	Lorbeerblatt Blattgewürz	\N
10908	1256	3	9519	8	Liebstöckelkraut Blattgewürz	\N
10909	1256	3	9520	9	Majoran Blattgewürz	\N
10910	1256	3	9521	10	Oregano, wilder Majoran, echter Dost Blattgewürz	\N
10911	1256	3	9522	11	Pimpernelle Blattgewürz	\N
10912	1256	3	9523	12	Rosmarin Blattgewürz	\N
10913	1256	3	9524	13	Melisse Blattgewürz	\N
10914	1256	3	9525	14	Salbei Blattgewürz	\N
10915	1256	3	9526	15	Thymian Blattgewürz	\N
10916	1256	3	9527	16	Ysopkraut Blattgewürz	\N
10917	1256	3	9528	17	Wermutkraut Blattgewürz	\N
10918	1256	3	9530	19	Kerbel Blattgewürz	\N
10919	1256	3	9531	20	Weinraute Blattgewürz	\N
10920	1256	3	9532	21	Schabzigerklee Blattgewürz	\N
10921	1256	3	9533	22	Petersilie Blattgewürz	\N
10922	1256	3	9534	23	Schnittlauch Blattgewürz	\N
10923	1256	3	9535	24	Blattsellerie Blattgewürz	\N
10924	1256	3	9536	25	Küchenkräutermischung Blattgewürz	\N
10925	1256	3	14106	26	Koriander Blattgewürz	\N
10926	1256	3	14123	27	Zitronengras Blattgewürz	\N
10927	1257	3	9538	1	Zimt Rindengewürz	\N
10928	1257	3	13717	2	Ceylon-Zimt Rindengewürz	\N
10929	1257	3	13718	3	Cassia-Zimt Rindengewürz	\N
10930	1257	3	13719	4	Pandang-Zimt Rindengewürz	\N
10931	1258	3	9540	1	Kapern Blütengewürz	\N
10932	1258	3	9541	2	Gewürznelken Blütengewürz	\N
10933	1258	3	9543	4	Safran Blütengewürz	\N
10934	1258	3	9544	5	Zimtblüten Blütengewürz	\N
10935	1259	3	9546	1	Paprikapulver Fruchtgewürz	\N
10936	1259	3	9547	2	Chili Fruchtgewürz	\N
10937	1259	3	9548	3	Anis Fruchtgewürz	\N
10938	1259	3	9549	4	Kardamon Fruchtgewürz	\N
10939	1259	3	9550	5	Koriander Fruchtgewürz	\N
10940	1259	3	9551	6	Dillfrucht Fruchtgewürz	\N
10941	1259	3	9552	7	Piment Fruchtgewürz	\N
10942	1259	3	9553	8	Pfeffer weiß Fruchtgewürz	\N
10943	1259	3	9554	9	Pfeffer schwarz Fruchtgewürz	\N
10944	1259	3	9555	10	Pfeffer grün Fruchtgewürz	\N
10945	1259	3	9556	11	Wacholderbeere Fruchtgewürz	\N
10946	1259	3	9557	12	Kümmel Fruchtgewürz	\N
10947	1259	3	9558	13	Zitrusschale getrocknet Fruchtgewürz	\N
10948	1259	3	9559	14	Vanilleschote Fruchtgewürz	\N
10949	1259	3	9560	15	Macis Fruchtgewürz	\N
10950	1259	3	9561	16	Kreuzkümmel Fruchtgewürz	\N
10951	1259	3	9562	17	Sumach	\N
10952	1259	3	9563	18	Fenchelfrucht Fruchtgewürz	\N
10953	1259	3	9564	19	Schwarzkümmel Fruchtgewürz	\N
10954	1259	3	9565	20	Sternanis Fruchtgewürz	\N
10955	1260	3	9567	1	Muskatnuss Samengewürz	\N
10956	1260	3	9568	2	Senfkorn gelb Samengewürz	\N
10957	1260	3	9569	3	Senfkorn braun Samengewürz	\N
10958	1260	3	9570	4	Bockshornkleesamen Samengewürz	\N
10959	1261	3	9572	1	Maggipilz Würzpilz	\N
10960	1261	3	9573	2	Knoblauchschwindling Würzpilz	\N
10961	1262	3	9575	1	Lebkuchengewürzmischung	\N
10962	1262	3	9576	2	Pfefferkuchengewürzmischung	\N
10963	1262	3	9577	3	Spekulatiusgewürzmischung	\N
10964	1262	3	9578	4	Honigkuchengewürzmischung	\N
10965	1262	3	9579	5	Brotgewürzmischung	\N
10966	1262	3	9580	6	Salatgewürzmischung	\N
10967	1262	3	9581	7	Kräutergewürzmischung	\N
10968	1262	3	9582	8	Gurkeneinmachgewürzmischung	\N
10969	1262	3	9583	9	Geflügelgewürzmischung	\N
10970	1262	3	9584	10	Fischgewürzmischung	\N
10971	1262	3	9585	11	Gulaschgewürzmischung	\N
10972	1262	3	9586	12	Grillgewürzmischung	\N
10973	1262	3	9587	13	Sauerbratengewürzmischung	\N
10974	1262	3	9588	14	Bratengewürzmischung	\N
10975	1262	3	9589	15	Wurstgewürzmischung	\N
10976	1262	3	9590	16	Chinagewürzmischung	\N
10977	1262	3	9591	17	Pastetengewürzmischung	\N
10978	1262	3	9592	18	Hackfleischgewürzmischung	\N
10979	1262	3	9593	19	Suppengewürzmischung	\N
10980	1262	3	9594	20	Wildgewürzmischung	\N
10981	1262	3	9595	21	Spaghettigewürzmischung	\N
10982	1262	3	9596	22	Gyrosgewürzmischung	\N
10983	1262	3	9597	23	Kräuter der Provence	\N
10984	1262	3	9598	24	Pizzagewürzmischung	\N
10985	1262	3	9599	25	Schaschlikgewürzmischung	\N
10986	1262	3	9600	26	Steakgewürzmischung	\N
10987	1262	3	9601	27	Garam Masala	\N
10988	1263	3	9617	1	Vanillin	\N
10989	1263	3	9618	2	Himbeerketon	\N
10990	1263	3	9619	3	Transhexenal	\N
10991	1263	3	9620	4	Nootkaton	\N
10992	1263	3	9621	5	Citral	\N
10993	1263	3	9622	6	Benzaldehyd	\N
10994	1264	3	9624	1	Coffein	\N
10995	1264	3	9625	2	Chinarinde	\N
10996	1264	3	9626	3	Chinin	\N
10997	1264	3	9627	4	Quassiaholz	\N
10998	1264	3	9628	5	Calmusöl	\N
10999	1264	3	9629	6	Büffelgras	\N
11000	1264	3	9630	7	Mariengras	\N
11001	1264	3	9631	8	Waldmeister	\N
11002	1265	3	9633	1	Rauch aus Holz/Zweigen/Heidekraut/Nadelholzsamenständen/Gewürzen	\N
11003	1265	3	9634	2	Rauch aus Torf	\N
11004	1265	3	9635	3	Rauchkondensat	\N
11005	1275	3	9647	1	Kutterhilfsmittel auf Phosphatbasis	\N
11006	1275	3	9648	2	Kutterhilfsmittel auf Basis org. Säure	\N
11007	1275	3	9649	3	Pökelhilfsstoff	\N
11008	1275	3	9650	4	Nitritpökelsalz	\N
11009	1275	3	9651	5	Zartmacher	\N
11010	1275	3	9652	6	Rohwurstreifemittel	\N
11011	1275	3	9653	7	Stabilisator auf Säurebasis	\N
11012	1275	3	9655	9	Pökellake	\N
11013	1275	3	9656	10	Beize zum Einlegen von Fleisch	\N
11014	1275	3	9657	11	Aufgussflüssigkeit für Sülze	\N
11015	1275	3	9658	12	Spritzpökellake	\N
11016	1275	3	9659	13	Eigenlake	\N
11017	1275	3	9660	14	Aufgusslake	\N
11018	1275	3	9661	15	Nitritpökelsalz jodiert	\N
11019	1276	3	9663	1	Backtriebmittel	\N
11020	1276	3	9664	2	Presshefe	\N
11021	1276	3	9665	3	Sauerteig	\N
11022	1276	3	9666	4	Backmittel	\N
11023	1276	3	9667	5	Trennmittel für Backformen	\N
11024	1276	3	9668	6	Trockenhefe	\N
11025	1276	3	9669	7	Teigsäuerungsmittel	\N
11026	1276	3	13508	8	Backlauge	\N
11027	1277	3	9671	1	Tortengusspulver	\N
11028	1277	3	9672	2	Tortenkrempulver	\N
11029	1277	3	9673	3	Schlagkrempulver	\N
11030	1277	3	9674	4	Sahnestandmittel	\N
11031	1277	3	9675	5	Überzüge und Verzierungen von Backwaren	\N
11032	1277	3	9676	6	Glasur- Füllungs- und Konfektmassen	\N
11033	1278	3	9678	1	Zucker-Süßstoffgemisch	\N
11034	1278	3	9679	2	Vanillinzucker	\N
11035	1278	3	9680	3	Gelierzucker ohne Farbstoff	\N
11036	1278	3	9681	4	Gelierzucker mit Farbstoff	\N
11037	1278	3	9682	5	Gelierfruktose	\N
11038	1279	3	9684	1	Schmelzsalzgemisch	\N
11039	1279	3	9685	2	Labaustauschstoff	\N
11040	1279	3	9686	3	Schmelzsalz auf Phosphatbasis	\N
11041	1279	3	9687	4	Schmelzsalz auf Basis org. Säuren	\N
11042	1279	3	9688	5	Calciumchlorid	\N
11043	1284	3	9695	1	Überzugsmittel für Kaffee und Kaffeeersatz	\N
11044	1284	3	9696	2	Überzugsmittel für Zuckerwaren	\N
11045	1284	3	9697	3	Überzugsmittel für Käse	\N
11046	1284	3	9698	4	Überzugsmittel für Zitrusfrüchte	\N
11047	1284	3	9699	5	Überzugsmittel für Würste	\N
11048	1285	3	9701	1	Fertig-Mix für Bienenstich	\N
11049	1285	3	9702	2	Backfertige Mohnfüllung	\N
11050	1285	3	9703	3	Backfertige Fruchtfüllung	\N
11051	1286	3	9706	1	L-Alanin	\N
11052	1286	3	9707	2	L-Arginin	\N
11053	1286	3	9708	3	L-Asparaginsäure	\N
11054	1286	3	9709	4	Glutaminsäure und deren Na- und K-Salze	\N
11055	1286	3	9710	5	L-Cystein	\N
11056	1286	3	9711	6	L-Cystin	\N
11057	1286	3	9712	7	Glycin	\N
11058	1286	3	9713	8	L-Histidin	\N
11059	1286	3	9714	9	L-Isoleucin	\N
11060	1286	3	9715	10	L-Leucin	\N
11061	1286	3	9716	11	L-Lysin	\N
11062	1286	3	9717	12	L-Methionin	\N
11063	1286	3	9718	13	L-Phenylalanin	\N
11064	1286	3	9719	14	L-Serin	\N
11065	1286	3	9720	15	Taurin	\N
11066	1286	3	9721	16	L-Threonin	\N
11067	1286	3	9722	17	L-Valin	\N
11068	1286	3	9723	18	Citrullin	\N
11069	1286	3	9724	19	L-Ornithin	\N
11070	1286	3	9725	20	L-Prolin	\N
11071	1286	3	9726	21	L-Tryptophan	\N
11072	1286	3	9727	22	L-Tyrosin	\N
11073	1286	3	9728	23	N-Acetyl-L-Methionin	\N
11074	1286	3	9729	24	N-Acetyl-L-Tyrosin	\N
11075	1286	3	9730	25	L-Carnithin und -hydrochlorid	\N
11076	1286	3	9731	99	Aminosäuregemisch	\N
11077	1287	3	9733	1	Butylhydroxyanisol	\N
11078	1287	3	9734	2	Butylhydroxytoluol	\N
11079	1287	3	9735	3	Dodecylgallat	\N
11080	1287	3	9736	4	Octylgallat	\N
11081	1287	3	9737	5	Tocopherol synth. auch natürlich	\N
11082	1287	3	9738	6	Ascorbylpalmitat	\N
11083	1287	3	9739	7	Propylgallat Ascorbinsäure siehe 572702 Ascorbat siehe 572702	\N
11084	1287	3	9740	99	Gemisch von Antioxydantien	\N
11085	1288	3	9742	1	Äthylmaltol	\N
11086	1288	3	9743	2	Äthylvanillin	\N
11087	1288	3	9744	3	Allylphenoxyacetat	\N
11088	1288	3	9745	4	Amylzimtaldehyd	\N
11089	1288	3	9746	5	Anisylaceton	\N
11090	1288	3	9747	6	Ammoniumchlorid	\N
11091	1288	3	9748	7	Guanylate	\N
11092	1288	3	9749	8	Hydroxycitronellalmethylacetal	\N
11093	1288	3	9750	9	Inosinate	\N
11094	1288	3	9751	10	Maltol	\N
11095	1288	3	9752	11	6-Methylcumarin	\N
11096	1288	3	9753	12	Heptinsäuremethylester	\N
11097	1288	3	9755	14	Naphthylmethylketon	\N
11098	1288	3	9756	15	2-Phenylpropionaldehyd	\N
11099	1288	3	9757	16	Piperonylisobutyrat	\N
11100	1288	3	9758	17	Propenylguaethol	\N
11101	1288	3	9759	18	Vanillinacetat	\N
11102	1288	3	9761	20	Resorcindimethyläther	\N
11103	1288	3	9762	21	Chininsalze	\N
11104	1288	3	9763	22	Hydroxycitronellaldiäthylacetal	\N
11105	1288	3	9764	97	Gemisch von Geschmacksverstärkern	\N
11106	1288	3	9765	98	Gemisch von künstlichen Aromastoffen	\N
11107	1288	3	9766	99	Gemisch v. künstlichen Aromastoffen u. Geschmacksverstärkern	\N
11108	1289	3	9768	1	Wasserstoffperoxid	\N
11109	1289	3	9769	2	Kaliumpermanganat	\N
11110	1289	3	9770	3	Natriumhypochlorit	\N
11111	1290	3	9772	1	Agar-Agar	\N
11112	1290	3	9773	2	Alginsäure	\N
11113	1290	3	9774	3	Alginate	\N
11114	1290	3	9775	4	Carboxymethylcellulose	\N
11115	1290	3	9776	5	Carrageen	\N
11116	1290	3	9777	6	Guargummi	\N
11117	1290	3	9778	7	Gummi arabicum	\N
11118	1290	3	9779	8	Johannisbrotkernmehl	\N
11119	1290	3	9780	9	Methylcellulose	\N
11120	1290	3	9781	10	Pektine	\N
11121	1290	3	9782	11	Propylenglykolalginat	\N
11122	1290	3	9783	12	Stärke auch modifizierte	\N
11123	1290	3	9784	13	Traganth	\N
11124	1290	3	9785	14	Amidiertes Pektin	\N
11125	1290	3	9786	15	Xanthan	\N
11126	1290	3	9787	16	Cellulose	\N
11127	1290	3	9788	17	Guarkernmehl Gelatine siehe 560900	\N
11128	1290	3	9789	18	Distärkeadipat acetyliert	\N
11129	1290	3	9790	19	Stärkeacetat	\N
11130	1290	3	9791	20	Distärkephosphat acetyliert	\N
11131	1290	3	9792	21	Hydroxypropylcellulose	\N
11132	1290	3	9793	22	Hydroxypropylmethylcellulose	\N
11133	1290	3	9794	99	Gemisch von Dickungs- und Geliermitteln	\N
11134	1291	3	9796	1	Lecithin	\N
11135	1291	3	9797	2	Mono- und Diglycerid von Speisefettsäuren	\N
11136	1291	3	9798	3	Polyglycerinester von Speisefettsäuren	\N
11137	1291	3	9799	4	Speisefettsäure und Salz	\N
11138	1291	3	9800	5	Mono- und Diglycerid verestert	\N
11139	1291	3	9801	6	Zuckerglycerid	\N
11140	1291	3	9802	99	Gemisch von Emulgatoren	\N
11141	1292	3	9804	1	Chlorgas	\N
11142	1292	3	9805	2	Chlordioxid	\N
11143	1292	3	9806	3	Chlorkalk	\N
11144	1292	3	9807	4	Ozon	\N
11145	1292	3	9808	5	Hypochlorit	\N
11146	1292	3	9809	6	Silberchlorid	\N
11147	1292	3	9810	7	Silbersulfat (Dimethyldicarbonat siehe 571515)	\N
11148	1293	3	9812	1	Amylolytisches Enzym	\N
11149	1293	3	9813	2	Pektolytisches Enzym	\N
11150	1293	3	9814	3	Proteolytisches Enzym	\N
11151	1293	3	9815	4	Lab und Labaustauschstoff	\N
11152	1293	3	9816	5	Lipase	\N
11153	1293	3	9817	6	Oxidase	\N
11154	1293	3	9818	7	Reduktase	\N
11155	1294	3	9820	1	Stempelfarbstoff	\N
11156	1294	3	9821	2	Aluminium	\N
11157	1294	3	9822	3	Amaranth	\N
11158	1294	3	9823	4	Annatto	\N
11159	1294	3	9824	5	Anthocyane	\N
11160	1294	3	9825	6	Beta-Apo-8-carotinsäureester	\N
11161	1294	3	9826	7	Azorubin	\N
11162	1294	3	9827	8	Betanin	\N
11163	1294	3	9828	9	Grün S; Brillantsäuregrün BS	\N
11164	1294	3	9829	10	Brillantschwarz BN; Schwarz PN	\N
11165	1294	3	9830	11	Paprikaextrakt; Capsanthin; Capsorubin	\N
11166	1294	3	9831	12	Carotin; Beta-carotin	\N
11167	1294	3	9832	13	Chinolingelb	\N
11168	1294	3	9833	14	Chlorophyll	\N
11169	1294	3	9834	15	Cochenillerot A; Ponceau 4R	\N
11170	1294	3	9835	16	Eisenoxide und Eisenhydroxide	\N
11171	1294	3	9836	17	Erythrosin	\N
11172	1294	3	9837	18	Gelborange S	\N
11173	1294	3	9838	19	Gold	\N
11174	1294	3	9839	20	Indigotin	\N
11175	1294	3	9840	21	Echtes Karmin; Karmin; Karminsäure	\N
11176	1294	3	9841	22	Kupfer-Chlorophyll	\N
11177	1294	3	9842	23	Kurkumin	\N
11178	1294	3	9843	24	Beta-Apo-8-carotinal	\N
11179	1294	3	9844	25	Litholrubin BK	\N
11180	1294	3	9846	27	Lycopin	\N
11181	1294	3	9847	28	Patentblau V	\N
11182	1294	3	9848	29	Silber	\N
11183	1294	3	9849	30	Tartrazin	\N
11184	1294	3	9850	31	Titandioxid	\N
11185	1294	3	9851	32	Xanthophyll	\N
11186	1294	3	9852	33	Zuckerkulör	\N
11187	1294	3	9853	34	Pflanzenkohle; Carbo medicinalis vegetabilis	\N
11188	1294	3	9854	35	Calciumcarbonat (Riboflavin siehe 572714 Lactoflavin siehe 572714)	\N
11189	1294	3	9855	36	Ceresgelb	\N
11190	1294	3	9856	37	Ceresrot	\N
11191	1294	3	9857	38	Viktoriablau	\N
11192	1294	3	9858	39	Sudanblau	\N
11193	1294	3	9859	40	Canthaxanthin	\N
11194	1294	3	9860	41	Carotinsäureäthylester	\N
11195	1294	3	9861	42	Bixin	\N
11196	1294	3	14254	43	Allurarot AC	\N
11197	1294	3	14255	44	Brillantblau FCF	\N
11198	1294	3	14256	45	Sulfitlaugen-Zuckerkulör	\N
11199	1294	3	14257	46	Ammonsulfit-Zuckerkulör	\N
11200	1294	3	14258	47	Ammoniak-Zuckerkulör	\N
11201	1294	3	14259	48	Braun HT	\N
11202	1294	3	9862	99	Farbstoffgemisch	\N
11203	1295	3	9864	1	Äpfelsäure	\N
11204	1295	3	9865	2	Malat	\N
11205	1295	3	9866	5	Bernsteinsäure	\N
11206	1295	3	9867	6	Succinat	\N
11207	1295	3	9868	7	Zitronensäure	\N
11208	1295	3	9869	8	Citrat	\N
11209	1295	3	9870	9	Essigsäure	\N
11210	1295	3	9871	10	Acetat	\N
11211	1295	3	9872	11	Fumarsäure	\N
11212	1295	3	9873	12	Gluconsäure und Deltalacton	\N
11213	1295	3	9874	13	Gluconat	\N
11214	1295	3	9875	14	Glucuronat	\N
11215	1295	3	9876	15	Milchsäure	\N
11216	1295	3	9877	16	Laktat	\N
11217	1295	3	9878	17	Weinsäure	\N
11218	1295	3	9879	18	Tartrat (Ascorbinsäure siehe 572701 Ascorbat siehe 572702)	\N
11219	1295	3	9880	99	Gemisch von Genusssäuren u./o. Salzen	\N
11220	1296	3	9882	1	Glyoxal	\N
11221	1296	3	9883	2	Aluminiumsalz	\N
11222	1297	3	9886	1	Gummen	\N
11223	1297	3	9887	2	Harz künstlich	\N
11224	1297	3	9888	3	Harz natürlich	\N
11225	1297	3	9889	4	Kautschuk	\N
11226	1297	3	9890	5	Kunststoff künstlich Polymer	\N
11227	1297	3	9891	6	Montansäureester	\N
11228	1297	3	9892	7	Oleat	\N
11229	1297	3	9894	10	Polyäthylenwachsoxidat	\N
11230	1297	3	9895	11	Polyolefinharz	\N
11231	1297	3	9898	14	Schellack	\N
11232	1297	3	9899	15	Wachs mikrokristallin	\N
11233	1297	3	9900	16	Wachs natürlich	\N
11234	1297	3	9901	97	Gemisch von Kaumassen	\N
11235	1297	3	9902	98	Gemisch von Überzugsmitteln (Paraffine siehe 572507)	\N
11236	1297	3	9903	99	Gemisch von Kaumassen und Überzugsmitteln	\N
11237	1298	3	9905	1	Aktivkohle	\N
11238	1298	3	9906	2	Albumin	\N
11239	1298	3	9907	3	Asbest	\N
11240	1298	3	9909	5	Eiweiß	\N
11241	1298	3	9910	6	Kaolin	\N
11242	1298	3	9911	7	Kieselgur	\N
11243	1298	3	9912	8	Polyamid	\N
11244	1298	3	9913	9	Polyvinylpyrolidon	\N
11245	1298	3	9914	11	Tannin	\N
11246	1298	3	9915	12	Ton	\N
11247	1298	3	9916	13	Wels-/Stör-/Hausenblase	\N
11248	1298	3	9917	14	Bentonit	\N
11249	1298	3	9918	15	Inosithhexaphosphat	\N
11250	1298	3	9919	16	K-Hexacyanoferrat	\N
11251	1298	3	9920	17	Eisen-III-Salz	\N
11252	1298	3	9921	18	Metaweinsäure	\N
11253	1298	3	9922	19	DL-Weinsäure und Salz (Silikate siehe 571902)	\N
11254	1298	3	9923	98	Gemisch von Klärmitteln (Cellulose siehe 570516 Agar-Agar siehe 570501 Aluminiumsalze siehe 571102)	\N
11255	1298	3	9924	99	Gemisch von Filterhilfsstoffen	\N
11256	1299	3	9926	1	Adipinsäure	\N
11257	1299	3	9927	2	Adipat	\N
11258	1299	3	9928	4	Cholinsalz	\N
11259	1299	3	9929	5	Glutamat	\N
11260	1299	3	9930	6	Kaliumguanylat	\N
11261	1299	3	9931	7	Kaliuminosinat	\N
11262	1300	3	9935	1	Ameisensäure	\N
11263	1300	3	9936	2	Formiat	\N
11264	1300	3	9937	3	Benzoesäure	\N
11265	1300	3	9938	4	Benzoat	\N
11266	1300	3	9939	5	Hexamethylentetramin	\N
11267	1300	3	9940	6	Sorbinsäure	\N
11268	1300	3	9941	7	Sorbat	\N
11269	1300	3	9942	8	Hydroxybenzoesäureester und deren Salze	\N
11270	1300	3	9945	11	Schwefeldioxid	\N
11271	1300	3	9946	12	Sulfit	\N
11272	1300	3	9947	13	Lysozym	\N
11273	1300	3	9948	14	Natamycin	\N
11274	1300	3	9949	15	Dimethyldicarbonat	\N
11275	1300	3	9950	99	Gemisch von Konservierungsstoffen	\N
11276	1301	3	9952	1	Äthanol	\N
11277	1301	3	9953	2	Benzylalkohol	\N
11278	1301	3	9954	6	Glycerin	\N
11279	1301	3	9955	7	Glycerinacetat	\N
11280	1301	3	9956	8	Isopropanol	\N
11281	1301	3	9957	9	Propylenglykol 1,2	\N
11282	1301	3	9961	13	Butan	\N
11283	1301	3	9962	14	Ethylacetat	\N
11284	1301	3	9963	15	Butylacetat	\N
11285	1301	3	9964	16	Methylacetat	\N
11286	1301	3	9965	17	Dichlormethan	\N
11287	1301	3	9966	99	Lösungsmittelgemisch	\N
11288	1302	3	9968	1	Diphenyl Biphenyl	\N
11289	1302	3	9969	2	Orthophenylphenol	\N
11290	1302	3	9970	3	Thiabendazol (Wachs siehe 571215 oder 571216)	\N
11291	1302	3	9971	99	Gemisch von Oberflächenbehandlungsmitteln	\N
11292	1303	3	9973	1	Gluconodeltalacton (Ascorbinsäure siehe 572701 Ascorbat siehe 572702)	\N
11293	1304	3	9975	1	Kieselsäure	\N
11294	1304	3	9976	2	Silikat (Ferrocyanid siehe 571316 Carbonat siehe 572023)	\N
11295	1305	3	9979	2	Hydrogenkarbonat	\N
11296	1305	3	9981	4	Kohlensäure	\N
11297	1305	3	9982	6	Salzsäure	\N
11298	1305	3	9983	7	Chlorid	\N
11299	1305	3	9984	8	Schwefelsäure	\N
11300	1305	3	9985	9	Sulfat	\N
11301	1305	3	9986	10	O-Phosphorsäure	\N
11302	1305	3	9987	11	O-Phosphat	\N
11303	1305	3	9988	12	Diphosphat	\N
11304	1305	3	9989	13	Triphosphat	\N
11305	1305	3	9990	14	Polyphosphat	\N
11306	1305	3	9991	18	Nitrat	\N
11307	1305	3	9992	19	Nitrit	\N
11308	1305	3	9993	20	Hydroxid	\N
11309	1305	3	9994	21	Oxid	\N
11310	1305	3	9995	22	Jodid/Jodat	\N
11311	1305	3	9996	23	Carbonat (Sulfit siehe 571512 Silikat siehe 571902 Schweflige Säure siehe 571511)	\N
11312	1307	3	9999	1	Cyclamat	\N
11313	1307	3	10000	2	Saccharin	\N
11314	1307	3	10001	3	Aspartam	\N
11315	1307	3	10002	4	Acesulfam	\N
11316	1307	3	14296	5	Steviolglycoside	\N
11317	1307	3	10003	99	Gemisch von Süßstoffen	\N
11318	1308	3	10005	1	Äthylcellulose	\N
11319	1308	3	10006	2	Äthylcitrat	\N
11320	1308	3	10007	3	Äthyllactat	\N
11321	1308	3	10008	5	Kolophonium	\N
11322	1308	3	10009	6	Kopal (Ammoniumalginat siehe 570503)	\N
11323	1308	3	10010	99	Gemisch von Trägerstoffen	\N
11324	1309	3	10012	1	Distickstoffoxid	\N
11325	1309	3	10013	2	Kohlendioxid	\N
11326	1309	3	10014	3	Luft	\N
11327	1309	3	10015	4	Stickstoff	\N
11328	1310	3	10018	4	Spermöl	\N
11329	1310	3	10019	5	Talkum	\N
11330	1310	3	10020	6	Walrat	\N
11331	1310	3	10021	7	Paraffin	\N
11332	1310	3	10022	8	Stearinsäure/-salz (Magnesiumoxid siehe 57202 Silikat siehe 571902 Cadelillawachs siehe 571216)	\N
11333	1310	3	10023	9	Thermooxidiertes Sojaöl	\N
11334	1310	3	10024	99	Gemisch von Trennmitteln	\N
11335	1311	3	10026	1	Ammoniakgas	\N
11336	1311	3	10027	2	Na-Aluminat	\N
11337	1311	3	10028	3	Na-Thiosulfat Aktivkohle siehe 571301	\N
11338	1312	3	10030	1	Ascorbinsäure	\N
11339	1312	3	10031	2	Ascorbat	\N
11340	1312	3	10032	4	Biotin	\N
11341	1312	3	10033	5	Folsäure	\N
11342	1312	3	10034	6	Nicotinsäure Niacin	\N
11343	1312	3	10035	7	Pantothenat	\N
11344	1312	3	10036	8	Provitamin A	\N
11345	1312	3	10037	9	Tocopherolacetat	\N
11346	1312	3	10038	10	Tocopherolsuccinat	\N
11347	1312	3	10039	11	Vitamin A	\N
11348	1312	3	10040	12	Vitamin A (-palmitat -acetat)	\N
11349	1312	3	10041	13	Vitamin B1	\N
11350	1312	3	10042	14	Vitamin B2	\N
11351	1312	3	10043	15	Vitamin B6	\N
11352	1312	3	10044	16	Vitamin B12	\N
11353	1312	3	10045	17	Vitamin D	\N
11354	1312	3	10046	18	Vitamin D2	\N
11355	1312	3	10047	19	Vitamin K1	\N
11356	1312	3	10048	20	Vitamin D3	\N
11357	1312	3	10049	21	Vitamin K2 (Ascorbylpalmitat siehe 570206 Tocopherol siehe 570205 Carotin siehe 570912)	\N
11358	1313	3	10051	1	Mannit	\N
11359	1313	3	10052	2	Sorbit	\N
11360	1313	3	10053	3	Xylit	\N
11361	1313	3	10054	4	Isomalt	\N
11362	1313	3	10055	5	Maltitsirup	\N
11363	1313	3	14260	6	Erythrit; Erythritol	\N
11364	1313	3	10056	98	Gemisch von Zuckeraustauschstoffen	\N
11365	1313	3	10057	99	Gemisch von Zuckeraustauschstoffen u. Süßstoffen	\N
11366	1314	3	10060	1	Chromsalz	\N
11367	1314	3	10061	2	Kupfersalz	\N
11368	1314	3	10062	3	Mangansalz	\N
11369	1314	3	10063	4	Molybdat	\N
11370	1314	3	10064	5	Zinksalz	\N
11371	1314	3	10065	6	Magnesiumsalz	\N
11372	1314	3	10066	7	Eisensalz	\N
11373	1314	3	10067	8	Calciumsalz	\N
11374	1314	3	10068	9	Fluorid (Jodid siehe 572022)	\N
11375	1316	3	10071	1	Rohwasser aus Grundwasser nicht aufbereitet	\N
11376	1316	3	10072	2	Rohwasser aus Bohrungen nicht aufbereitet	\N
11377	1316	3	10073	3	Rohwasser aus Schachtbrunnen nicht aufbereitet	\N
11378	1316	3	10074	4	Rohwasser aus Quellen nicht aufbereitet	\N
11379	1316	3	10075	5	Rohwasser aus Stollen nicht aufbereitet	\N
11380	1316	3	10076	6	Rohwasser aus Zisternen nicht aufbereitet	\N
11381	1316	3	10077	7	Rohwasser aus Grundwasser aufbereitet	\N
11382	1316	3	10078	8	Rohwasser aus Bohrungen aufbereitet	\N
11383	1316	3	10079	9	Rohwasser aus Schachtbrunnen aufbereitet	\N
11384	1316	3	10080	10	Rohwasser aus Quellen aufbereitet	\N
11385	1316	3	10081	11	Rohwasser aus Stollen aufbereitet	\N
11386	1316	3	10082	12	Rohwasser aus Zisternen aufbereitet	\N
11387	1317	3	10084	1	Rohwasser aus Oberflächenwasser nicht aufbereitet	\N
11388	1317	3	10085	2	Rohwasser aus fließenden Gewässern nicht aufbereitet	\N
11389	1317	3	10086	3	Rohwasser aus stehenden Gewässern nicht aufbereitet	\N
11390	1317	3	10087	4	Rohwasser aus Oberflächenwasser aufbereitet	\N
11391	1317	3	10088	5	Rohwasser aus fließenden Gewässern aufbereitet	\N
11392	1317	3	10089	6	Rohwasser aus stehenden Gewässern aufbereitet	\N
11393	1318	3	10091	1	Trinkwasser Zentralversorgung Grundwasser aufbereitet	\N
11394	1318	3	10092	2	gestr. 590502, Trinkwasser Zentralvers. Grundw. u. Uferfiltrat	\N
11395	1318	3	10093	3	gestr. 590501, Trinkw. Zentralvers. Grundw. Uferfiltr. desinfiz.	\N
11396	1318	3	10094	4	Trinkwasser Zentralversorgung Grundwasser nicht aufbereitet	\N
11397	1318	3	10095	5	Trinkwasser Zentralversorgung aus Quellen nicht aufbereitet	\N
11398	1318	3	10096	6	Trinkwasser Zentralversorgung aus Quellen aufbereitet	\N
11399	1319	3	10098	1	Trinkwasser Zentralversorgung Oberflächenwasser aufbereitet	\N
11400	1319	3	10099	2	gestr. 590501,Trinkw. Zentralver. Oberflächenw. Grundw. desinf.	\N
11401	1319	3	10100	3	Trinkwasser Zentralversorgung Oberflächenwasser nicht aufbereitet	\N
11402	1320	3	10102	1	Trinkwasser aus Mischwasser Zentralversorgung aufbereitet	\N
11403	1320	3	10103	2	Trinkwasser aus Mischwasser Zentralversorgung nicht aufbereitet	\N
11404	1321	3	10105	1	Trinkwasser Eigen- und Einzelversorgung aufbereitet	\N
11405	1321	3	10106	2	Trinkwasser Eigen- und Einzelversorgung nicht aufbereitet	\N
11406	1322	3	10108	1	Trinkwasser mobiler Wasserversorgung Schiff nicht aufbereitet	\N
11407	1322	3	10109	2	Trinkwasser mobiler Wasserversorgung Flugzeug nicht aufbereitet	\N
11408	1322	3	10110	3	Trinkwasser mobiler Wasserversorgung Landfahrzeug nicht aufbereitet	\N
11409	1322	3	10111	4	Trinkwasser mobiler Wasserversorgung geschlossene Behältnisse nicht aufbereitet	\N
11410	1322	3	10112	5	Trinkwasser mobiler Wasserversorgung Schiff aufbereitet	\N
11411	1322	3	10113	6	Trinkwasser mobiler Wasserversorgung Flugzeug aufbereitet	\N
11412	1322	3	10114	7	Trinkwasser mobiler Wasserversorgung Landfahrzeug aufbereitet	\N
11413	1322	3	10115	8	Trinkwasser mobiler Wasserversorgung geschl. Behältnisse aufbereitet	\N
11414	1323	3	10117	1	Brauchwasser Lebensmittelbetriebe	\N
11415	1323	3	10118	2	Brauchwasser pharmazeutische Industrie	\N
11416	1323	3	10119	3	Brauchwasser landwirtschaftlicher Betrieb	\N
11417	1323	3	10120	4	Brauchwasser Fischereifahrzeug	\N
11418	1323	3	10121	5	Brauchwasser metallverarbeitende Industrie	\N
11419	1323	3	10122	6	Brauchwasser Papierindustrie	\N
11420	1323	3	10123	7	Brauchwasser Freibad	\N
11421	1323	3	10124	8	Brauchwasser Hallenbad	\N
11422	1326	3	10128	1	Natürliches Mineralwasser mit Kohlensäure	\N
11423	1326	3	10129	2	Natürliches Mineralwasser ohne Kohlensäure	\N
11424	1326	3	10130	3	Rohwasser für natürliches Mineralwasser	\N
11425	1327	3	10132	2	Rohwasser für Quellwasser	\N
11426	1327	3	10133	3	Quellwasser mit Kohlensäure	\N
11427	1327	3	10134	4	Quellwasser ohne Kohlensäure	\N
11428	1328	3	10136	1	Tafelwasser mit Kohlensäure	\N
11429	1328	3	10137	2	Tafelwasser ohne Kohlensäure	\N
11430	1328	3	10138	3	Sodawasser	\N
11431	1328	3	10139	4	Rohwasser für Tafelwasser	\N
11432	1330	3	10144	1	Levitiertes Wasser	\N
11433	1330	3	10145	2	Grander Wasser	\N
11434	1332	3	10147	1	Trinkwasser Zentralversorgung nicht aufbereitet Hausinstallation nicht aufbereitet	\N
11435	1332	3	10148	2	Trinkwasser Zentralversorgung aufbereitet Hausinstallation nicht aufbereitet	\N
11436	1332	3	13538	3	Trinkwasser Zentralversorgung nicht aufbereitet Hausinstallation aufbereitet	\N
11437	1332	3	13539	4	Trinkwasser Zentralversorgung aufbereitet Hausinstallation aufbereitet	\N
11438	1333	3	10189	1	Rohtabak unfermentiert unbehandelt	\N
11439	1333	3	10190	2	Rohtabak fermentiert unbehandelt	\N
11440	1333	3	10191	3	Rohtabak fermentiert behandelt	\N
11441	1334	3	10193	1	Zigarettentabak lose	\N
11442	1334	3	10194	2	Pfeifentabak	\N
11443	1334	3	10195	3	Zigarettentabak in Strangform mit Umhüllung	\N
11444	1334	3	10196	4	Wasserpfeifentabak	\N
11445	1335	3	10198	1	Schnupftabak (Schmalzlertyp)	\N
11446	1335	3	10199	2	Schnupftabak (Snufftyp)	\N
11447	1335	3	10200	3	Schnupfmittel ohne Tabak	\N
11448	1336	3	10202	1	Kautabak	\N
11449	1336	3	10203	2	Snus lose	\N
11450	1336	3	13558	3	Portions-Snus	\N
11451	1337	3	10205	1	Zigaretten mit Filter	\N
11452	1337	3	10206	3	Zigaretten mit Filter nikotinarm im Rauch	\N
11453	1337	3	10207	4	Zigaretten ohne Filter	\N
11454	1337	3	10208	6	Zigaretten ohne Filter nikotinarm im Rauch	\N
11455	1337	3	10209	7	Mentholzigaretten	\N
11456	1338	3	10213	1	Zigarren/Zigarillos mit Tabakdeckblatt	\N
11457	1338	3	10214	2	Zigarren/Zigarillos mit Foliendeckblatt	\N
11458	1338	3	10215	3	Zigarren/Zigarillos mit Filter	\N
11459	1339	3	10217	1	Kunstumblatt	\N
11460	1339	3	10218	2	Tabakfolie	\N
11461	1343	3	10223	1	Zigarettenersatzprodukt mit Tabak zum Rauchen geeignet	\N
11462	1343	3	10224	2	Zigarettenersatzprodukte ohne Tabak zum Rauchen geeignet	\N
11463	1343	3	14308	3	Flüssigkeiten für elektronische Zigaretten und elektronische Wasserpfeifen, nikotinhaltig	\N
11464	1343	3	14309	4	Flüssigkeiten für elektronische Zigaretten und elektronische Wasserpfeifen, nikotinfrei	\N
11465	1344	3	10226	1	Allgemein zugelassener Stoff für die Herstellung von Tabakerzeugnissen	\N
11466	1344	3	10227	2	Feuchthaltemittel für Rauchtabak/Zigarren/Zigaretten/Tabakfolie/Kunstumfolie/Kautabak u. Schnupftab.	\N
11467	1344	3	10228	9	Weißbrand/Flottbrandmittel	\N
11468	1344	3	10229	10	Stoff für Kunstumblatt und Zigarettenpapier	\N
11469	1344	3	10230	11	Stoff für Filter von Filterzigaretten und Filterzigarren	\N
11470	1344	3	10231	12	Stoff für Filterumhüllungen/ Mundstücke und Filter- /Mundstücksbelag	\N
11471	1344	3	10232	13	Stoff für Heißschmelzstoffe zum Kleben von Filterumhüllungen/ Mundstücken und Filtern	\N
11472	1344	3	10233	14	Konservierungsstoff ausgenommen für Zigarren und Zigaretten	\N
11473	1344	3	10234	20	Weichmacher für Farben/Lacke zum Bedrucken von Zigarettenpapier/Zigarettenfilter/Filterumhüllungen	\N
11474	1344	3	10235	21	Bindemittel für Druckfarben/Lacke von Mundstücken und Filtern	\N
11475	1344	3	10236	22	Stoff für Aufdrucke auf Zigarettenpapier, Mundstücke und Filter	\N
11476	1344	3	10237	23	Zusatz für Kautabak	\N
11477	1344	3	10238	24	Zusatz für Schnupftabak	\N
11753	1381	3	10565	84	Massagegel/Sportgel	\N
11478	1344	3	10239	25	Zusatz für Schnupfmittel	\N
11479	1344	3	10240	26	Geruchs-/Geschmacksstoff für Tabakerzeugnisse	\N
11480	1344	3	10241	30	Klebe-/Haft-/Verdickungsmittel für Zigarren/Strangtabak einschl. schwarzer Rolltabak/Tabakfolien/...	\N
11481	1344	3	10242	31	Farbstoff für Zigarettenpapier/Deckblatt/Tabakfolie/Kunstumblatt von Zigarren/Filterumhüllungen u. .	\N
11482	1344	3	10243	32	Farbstoff für schwarzen Rolltabak/Schnupftabak und für Klebe-/Haft-/Verdickungsmittel von Zigarren	\N
11483	1345	3	10264	10	Verpackungsmaterial für kosmetische Mittel aus Keramik	\N
11484	1345	3	10265	15	Verpackungsmaterial für kosmetische Mittel aus Glas	\N
11485	1345	3	10266	20	Verpackungsmaterial für kosmetische Mittel aus Metall	\N
11486	1345	3	10267	29	Verpackungsmaterial für kosmetische Mittel aus Metall lackiert/beschichtet	\N
11487	1345	3	10268	30	Verpackungsmaterial für kosmetische Mittel aus Kunststoff	\N
11488	1345	3	10269	39	Verpackungsmaterial für kosmetische Mittel aus Ionenaustauscherharz	\N
11489	1345	3	10270	40	Verpackungsmaterial für kosmetische Mittel aus Elastomeren/Kautschuk	\N
11490	1345	3	10271	45	Verpackungsmaterial für kosmetische Mittel mit Kunststoffbeschichtung	\N
11491	1345	3	10272	50	Verpackungsmaterial für kosmetische Mittel aus Papier/Pappe/Karton	\N
11492	1345	3	10273	55	Verpackungsmaterial für kosmetische Mittel aus Zellglas	\N
11493	1345	3	10274	60	Verpackungsmaterial für kosmetische Mittel mit Wachs-/Paraffin-Beschichtung/Überzug	\N
11494	1345	3	10275	70	Verpackungsmaterial für kosmetische Mittel aus textilem Material	\N
11495	1345	3	10276	80	Verpackungsmaterial für kosmetische Mittel aus Holz	\N
11496	1345	3	10277	85	Verpackungsmaterial für kosmetische Mittel aus Kork	\N
11497	1345	3	10278	99	Verpackungsmaterial für kosmetische Mittel aus Materialkombinationen	\N
11498	1346	3	10280	10	Verpackungsmaterial für Tabakerzeugnisse aus Keramik	\N
11499	1346	3	10281	15	Verpackungsmaterial für Tabakerzeugnisse aus Glas	\N
11500	1346	3	10282	20	Verpackungsmaterial für Tabakerzeugnisse aus Metall	\N
11501	1346	3	10283	29	Verpackungsmaterial für Tabakerzeugnisse aus Metall lackiert/beschichtet	\N
11502	1346	3	10284	30	Verpackungsmaterial für Tabakerzeugnisse aus Kunststoff	\N
11503	1346	3	10285	39	Verpackungsmaterial für Tabakerzeugnisse aus Ionenaustauscherharz	\N
11504	1346	3	10286	40	Verpackungsmaterial für Tabakerzeugnisse aus Elastomeren/Kautschuk	\N
11505	1346	3	10287	45	Verpackungsmaterial für Tabakerzeugnisse mit Kunststoffbeschichtung	\N
11506	1346	3	10288	50	Verpackungsmaterial für Tabakerzeugnisse aus Papier/Pappe/Karton	\N
11507	1346	3	10289	55	Verpackungsmaterial für Tabakerzeugnisse aus Zellglas	\N
11508	1346	3	10290	60	Verpackungsmaterial für Tabakerzeugnisse mit Wachs-/Paraffin-Beschichtung/Überzug	\N
11509	1346	3	10291	70	Verpackungsmaterial für Tabakerzeugnisse aus textilem Material	\N
11510	1346	3	10292	80	Verpackungsmaterial für Tabakerzeugnisse aus Holz	\N
11511	1346	3	10293	85	Verpackungsmaterial für Tabakerzeugnisse aus Kork	\N
11512	1346	3	10294	99	Verpackungsmaterial für Tabakerzeugnisse aus Materialkombinationen	\N
11513	1347	3	10297	1	Unterbekleidung (Unterwäsche/Miederwaren/Korsett...) ohne Materialdifferenzierung	\N
11514	1347	3	10298	2	Unterbekleidung (Unterwäsche/Miederwaren/Korsett...) aus textilem Material	\N
11515	1347	3	10299	3	Unterbekleidung (Unterwäsche/Miederwaren/Korsett...) aus Kunststoff	\N
11516	1347	3	10300	4	Unterbekleidung (Unterwäsche/Miederwaren/Korsett...) aus Leder	\N
11517	1347	3	10301	9	Unterbekleidung (Unterwäsche/Miederwaren/Korsett...) aus Materialkombinationen	\N
11518	1347	3	10302	11	Mittelbekleidung (Hemd/Bluse/Kleid) ohne Materialdifferenzierung	\N
11519	1347	3	10303	12	Mittelbekleidung (Hemd/Bluse/Kleid) aus textilem Material	\N
11520	1347	3	10304	13	Mittelbekleidung (Hemd/Bluse/Kleid) aus Kunststoff	\N
11521	1347	3	10305	14	Mittelbekleidung (Hemd/Bluse/Kleid) aus Leder	\N
11522	1347	3	10306	19	Mittelbekleidung (Hemd/Bluse/Kleid) aus Materialkombinationen	\N
11523	1347	3	10307	21	Oberbekleidung (Pullover/Hose/Mantel/Regenbekleidung...) ohne Materialdifferenzierung	\N
11524	1347	3	10308	22	Oberbekleidung (Pullover/Hose/Mantel/Regenbekleidung...) aus textilem Material	\N
11525	1347	3	10309	23	Oberbekleidung (Pullover/Hose/Mantel/Regenbekleidung...) aus Kunststoff	\N
11526	1347	3	10310	24	Oberbekleidung (Pullover/Hose/Mantel/Regenbekleidung...) aus Leder	\N
11527	1347	3	10311	29	Oberbekleidung (Pullover/Hose/Mantel/Regenbekleidung...) aus Materialkombinationen	\N
11528	1347	3	10312	31	Strumpfwaren (Socken/Strümpfe/Strumpfhosen...) ohne Materialdifferenzierung	\N
11529	1347	3	10313	32	Strumpfwaren (Socken/Strümpfe/Strumpfhosen...) aus textilem Material	\N
11530	1347	3	10314	33	Strumpfwaren (Socken/Strümpfe/Strumpfhosen...) aus Kunststoff	\N
11531	1347	3	10315	34	Strumpfwaren (Socken/Strümpfe/Strumpfhosen...) aus Leder	\N
11532	1347	3	10316	39	Strumpfwaren (Socken/Strümpfe/Strumpfhosen...) aus Materialkombinationen	\N
11533	1347	3	10317	41	Kopfbedeckung (Hut/Mütze/Kappe/Schleier/Kopftuch...) ohne Materialdifferenzierung	\N
11534	1347	3	10318	42	Kopfbedeckung (Hut/Mütze/Kappe/Schleier/Kopftuch...) aus textilem Material	\N
11535	1347	3	10319	43	Kopfbedeckung (Hut/Mütze/Kappe/Schleier/Kopftuch...) aus Kunststoff	\N
11536	1347	3	10320	44	Kopfbedeckung (Hut/Mütze/Kappe/Schleier/Kopftuch...) aus Leder	\N
11537	1347	3	10321	49	Kopfbedeckung (Hut/Mütze/Kappe/Schleier/Kopftuch...) aus Materialkombinationen	\N
11538	1347	3	10322	51	Schal/Halstuch/Fliege ohne Materialdifferenzierung	\N
11539	1347	3	10323	52	Schal/Halstuch/Fliege aus textilem Material	\N
11540	1347	3	10324	53	Schal/Halstuch/Fliege aus Kunststoff	\N
11541	1347	3	10325	54	Schal/Halstuch/Fliege aus Leder	\N
11542	1347	3	10326	59	Schal/Halstuch/Fliege aus Materialkombinationen	\N
11543	1347	3	10327	61	Nachtbekleidung (Schlafanzug/Nachthemd...)	\N
11544	1347	3	10328	62	Badekleidung (Badehose/Badeanzug/Bikini...)	\N
11545	1347	3	10329	63	Babybekleidung (Strampelanzug/Hemdhöschen)	\N
11546	1347	3	10330	64	Schutzbekleidung (Motorrad-/Fahrradhelm/Knieschützer)	\N
11547	1347	3	10331	65	Verkleidung/Masken (ausgen. 828301)	\N
11548	1347	3	10332	71	Schuhbekleidung (Stiefel/Sandalen...) ohne Materialdifferenzierung	\N
11549	1347	3	10333	72	Schuhbekleidung (Stiefel/Sandalen...) aus textilem Material	\N
11550	1347	3	10334	73	Schuhbekleidung (Stiefel/Sandalen...) aus Kunststoff	\N
11551	1347	3	10335	74	Schuhbekleidung (Stiefel/Sandalen...) aus Leder	\N
11552	1347	3	10336	79	Schuhbekleidung (Stiefel/Sandalen...) aus Materialkombinationen	\N
11553	1347	3	10337	81	Handschuhe/Fingerlinge ohne Materialdifferenzierung	\N
11554	1347	3	10338	82	Handschuhe/Fingerlinge aus textilem Material	\N
11555	1347	3	10339	83	Handschuhe/Fingerlinge aus Kunststoff	\N
11556	1347	3	10340	84	Handschuhe/Fingerlinge aus Leder	\N
11557	1347	3	13559	85	Handschuhe/Fingerlinge aus Elastomeren/Kautschuk	\N
11558	1347	3	10341	89	Handschuhe/Fingerlinge aus Materialkombinationen	\N
11559	1347	3	10342	91	Arbeitsbekleidung/Berufsbekleidung (Kittel...) ohne Materialdifferenzierung	\N
11560	1347	3	10343	92	Arbeitsbekleidung/Berufsbekleidung (Kittel...) aus textilem Material	\N
11561	1347	3	10344	93	Arbeitsbekleidung/Berufsbekleidung (Kittel...) aus Kunststoff	\N
11562	1347	3	10345	94	Arbeitsbekleidung/Berufsbekleidung (Kittel...) aus Leder	\N
11563	1347	3	10346	99	Arbeitsbekleidung/Berufsbekleidung (Kittel...) aus Materialkombinationen	\N
11564	1348	3	10348	1	Material zur Herstellung von Bekleidung (ausg. Kurzwaren) ohne Materialdifferenzierung	\N
11565	1348	3	10349	2	Material zur Herstellung von Bekleidung (ausgen. Kurzwaren) aus textilem Material	\N
11566	1348	3	10350	3	Material zur Herstellung von Bekleidung (ausgen. Kurzwaren) aus Kunststoff	\N
11567	1348	3	10351	4	Material zur Herstellung von Bekleidung (ausgen. Kurzwaren) aus Leder	\N
11568	1348	3	10352	5	Material zur Herstellung von Bekleidung (ausgen. Kurzwaren) aus Metall	\N
11569	1348	3	10353	9	Material zur Herstellung von Bekleidung (ausgen. Kurzwaren) aus Materialkombinationen	\N
11570	1348	3	10354	11	Kurzware (Knöpfe/Nieten/Reißverschluß/Nähgarn/Schulterpolster/Einlagen) ohne Materialdifferenzierung	\N
11571	1349	3	10356	1	Gegenstand zur Verbesserung des Äußeren (künstl.Wimpern u.Nägel incl.Befestigungsmaterial/Perücke/..	\N
11572	1349	3	10357	5	Brille	\N
11573	1349	3	10358	6	Haarspange/-klemme/-schleife	\N
11574	1349	3	10359	7	Haarband/Stirnband	\N
11575	1349	3	10360	8	Hosenträger/Gürtel	\N
11576	1349	3	10361	11	Rucksack/Koffer/Tasche/Brustbeutel ohne Materialdifferenzierung	\N
11577	1349	3	10362	12	Rucksack/Koffer/Tasche/Brustbeutel aus textilem Material	\N
11578	1349	3	10363	13	Rucksack/Koffer/Tasche/Brustbeutel aus Kunststoff	\N
11579	1349	3	10364	14	Rucksack/Koffer/Tasche/Brustbeutel aus Leder	\N
11580	1349	3	10365	15	Rucksack/Koffer/Tasche/Brustbeutel aus Metall	\N
11581	1349	3	10366	19	Rucksack/Koffer/Tasche/Brustbeutel aus Materialkombinationen	\N
11582	1349	3	10367	21	Uhren- und sonstiges Armband ohne Materialdifferenzierung	\N
11583	1349	3	10368	22	Uhren- und sonstiges Armband aus textilem Material	\N
11584	1349	3	10369	23	Uhren- und sonstiges Armband aus Kunststoff	\N
11585	1349	3	10370	24	Uhren- und sonstiges Armband aus Leder	\N
11586	1349	3	10371	25	Uhren- und sonstiges Armband aus Metall	\N
11587	1349	3	10372	29	Uhren- und sonstiges Armband aus Materialkombinationen	\N
11588	1349	3	10373	31	Schmuck ohne Materialdifferenzierung	\N
11589	1349	3	10374	32	Schmuck aus textilem Material	\N
11590	1349	3	10375	33	Schmuck aus Kunststoff	\N
11591	1349	3	10376	34	Schmuck aus Leder	\N
11592	1349	3	10377	35	Schmuck aus Metall	\N
11593	1349	3	10378	39	Schmuck aus Materialkombinationen	\N
11594	1350	3	10380	1	Slipeinlage/Binde/Tampon	\N
11595	1350	3	10381	2	Windel/Windelhose/-einlage	\N
11596	1350	3	10382	3	Schutzunterlage	\N
11597	1350	3	10383	4	Taschentucherzeugnis	\N
11598	1350	3	10384	5	Toilettenpapiererzeugnis	\N
11599	1350	3	10385	6	Stilleinlage/Warzenhütchen	\N
11600	1350	3	13578	7	Tupfer	\N
11601	1351	3	10387	1	Bettwäsche	\N
11602	1351	3	10388	2	Kissen/Decke/Federbett	\N
11603	1351	3	10389	3	Matraze (incl. Luftmatraze/Strandmatte/Isoliermatte)	\N
11604	1351	3	10390	4	Schlafsack	\N
11605	1351	3	10391	5	Wärmflasche/Heizkissen/-decke	\N
11606	1351	3	10392	6	Teppich/Fußmatte	\N
11607	1351	3	10393	7	Sitz-/Liegemöbel incl. Auflage	\N
11608	1351	3	10394	8	Haltegurt/-leine	\N
11609	1351	3	10395	9	Kontaktteil/-fläche von Sportgeräten und sonst. Bedarfsgegenständen	\N
11610	1351	3	10396	10	Schwimmhilfe	\N
11611	1351	3	10397	11	Schreibgerät	\N
11612	1352	3	10399	1	Gegenstand zur Reinigung und Pflege (Waschlappen/Handtuch/Badetuch/Reinigungstuch)	\N
11613	1352	3	10400	2	Gegenstand zur Haarpflege (Bürste/Kamm/Lockenwickler/Haarnetz...)	\N
11614	1352	3	10401	3	Erzeugnis für die Maniküre/Pediküre (Nagelschere/-feile...)	\N
11615	1352	3	10402	4	Gegenstand zur Bartpflege (Rasierapparat/Bartbinder...)	\N
11616	1352	3	10403	5	Pinsel/Wimpernzange	\N
11617	1352	3	10404	7	Massageartikel	\N
11618	1353	3	10406	1	Mundstücke (für Musikinstrumente/Tabakerzeugnisse/Sportgeräte...)	\N
11619	1353	3	10407	2	Flaschensauger/Trinkschnabel	\N
11620	1353	3	10408	3	Beruhigungssauger	\N
11621	1353	3	10409	4	Beißring	\N
11622	1353	3	10410	5	Gebissschutz	\N
11623	1353	3	10411	6	Künstliches Gebiss (Scherzartikel)	\N
11624	1353	3	10412	7	Gegenstand für die Zahn-/Mundpflege (Zahnbürste/-stocher/-seide...)	\N
11625	1353	3	10413	8	Luftballon/Trillerpfeife	\N
11626	1354	3	10416	1	Vorwaschmittel	\N
11627	1354	3	10417	2	Vollwaschmittel	\N
11628	1354	3	10418	3	Fein-/Bunt-/Spezialwaschmittel	\N
11629	1354	3	10419	4	Wollwaschmittel	\N
11630	1354	3	10420	5	Waschmittelsystemkomponenten	\N
11631	1354	3	10421	6	Gardinenwaschmittel	\N
11632	1354	3	10422	7	Handwaschmittel incl. Waschpaste	\N
11633	1355	3	10424	1	Weich-/Formspüler	\N
11634	1355	3	10425	2	Einweichmittel	\N
11635	1355	3	10426	3	Waschkraftverstärker	\N
11636	1355	3	10427	4	Enthärter für Waschmaschinen	\N
11637	1356	3	10429	1	Erzeugnis auf Basis von Lösungsmittel	\N
11638	1356	3	10430	2	Erzeugnis auf Basis von Seife	\N
11639	1356	3	13579	3	Erzeugnis auf Basis von Dithionit	\N
11640	1356	3	13580	4	Wäschebleichmittel (auf Basis von akt. <O> oder <Cl>)	\N
11641	1357	3	10432	1	Bügelhilfe	\N
11642	1357	3	10433	2	Imprägnierungsmittel (ausgen. 832803)	\N
11643	1357	3	10434	3	Textilfarbe	\N
11644	1357	3	10435	4	Flammschutzmittel	\N
11645	1357	3	10436	5	Aviagemittel und Antistatika	\N
11646	1357	3	10437	6	Baumwollveredler	\N
11647	1357	3	10438	7	Wollveredler	\N
11648	1357	3	10439	8	Mikrobiozide Ausrüstung für Textilien	\N
11649	1359	3	10442	1	Scheuermittel	\N
11650	1360	3	10444	1	Fußbodenreiniger	\N
11651	1360	3	10445	2	Bohnerwachs	\N
11652	1360	3	10446	3	Selbstglanzemulsion	\N
11653	1360	3	10447	4	Parkettpflegemittel	\N
11654	1360	3	10448	5	Pflegemittel für Steinfußböden	\N
11655	1367	3	10456	1	Schuhpflegemittel (ausgen. Imprägnierungsmittel)	\N
11656	1367	3	10457	2	Lederpflegemittel (ausgen. Schuhpflegemittel)	\N
11657	1367	3	10458	3	Lederimprägnierungsmittel	\N
11658	1368	3	10460	1	Handgeschirrspülmittel	\N
11659	1368	3	10461	2	Maschinengeschirrspülmittel	\N
11660	1368	3	10462	3	Klarspülmittel	\N
11661	1368	3	10463	4	Wasserenthärtungsmittel für Spülmaschinen	\N
11662	1371	3	10467	1	Silberputz-/-pflegemittel	\N
11663	1372	3	10469	1	Desinfektionsmittel häuslicher Bedarf	\N
11664	1372	3	10470	2	Desinfektionsmittel gewerblicher Bedarf	\N
11665	1373	3	10472	1	Reiniger ohne desinfizierenden Zusatz	\N
11666	1373	3	10473	2	Reiniger mit desinfizierendem Zusatz	\N
11667	1374	3	10475	1	Kunststoffreinigungs-/-pflegemittel	\N
11668	1374	3	10476	2	Reinigungs-/Pflegemittel für Unterhaltungselektronik/Foto/Optik(Bildschirm/Tonkopf/Schallplatten...)	\N
11669	1374	3	10477	3	Kontaktlinsenreiniger/-pflegemittel	\N
11670	1374	3	10478	4	Schimmelentferner	\N
11671	1374	3	10479	5	Schwimmbeckenreiniger	\N
11672	1374	3	10480	6	Hochdruck-/Dampfstrahlreiniger	\N
11673	1375	3	10482	1	Abbeizmittel	\N
11674	1375	3	10483	2	Pinselreiniger/Nitroverdünner	\N
11675	1375	3	10484	3	Tapetenablöser	\N
11676	1375	3	10485	4	Fassadenreiniger	\N
11677	1376	3	10487	1	Waschmittel für Fahrzeuge	\N
11678	1376	3	10488	2	Lack-/Chrompflegemittel für Fahrzeuge	\N
11679	1376	3	10489	3	Unterbodenschutz	\N
11680	1376	3	10490	4	Innenreiniger für Fahrzeuge	\N
11681	1376	3	10491	5	Scheibenreiniger/Insektenreiniger	\N
11682	1376	3	10492	6	Rostentferner/-lockerungsmittel	\N
11683	1376	3	10493	7	Scheiben-/Türschlossenteiser	\N
11684	1376	3	10494	8	Motorreiniger/Kaltreiniger/Teerentferner	\N
11685	1378	3	10497	1	WC-Duft-/Beckenstein	\N
11686	1378	3	10498	2	WC-Wasserkastenstein	\N
11687	1380	3	10502	10	Reinigungsmittel/Seife	\N
11688	1380	3	10503	11	Seife stückförmig	\N
11689	1380	3	10504	12	Syndet stückförmig	\N
11690	1380	3	10505	13	Handwaschpaste	\N
11691	1380	3	10506	14	Deoseife/Syndet	\N
11692	1380	3	10507	15	Babyseife/Syndet	\N
11693	1380	3	10508	16	Seife flüssig	\N
11694	1380	3	10509	17	Syndet flüssig	\N
11695	1380	3	10510	18	Intimwaschlotion	\N
11696	1380	3	10511	19	Haarfarbenentferner	\N
11697	1380	3	10512	30	Dusch- und Badepräparat	\N
11698	1380	3	10513	31	Duschbad/-gel	\N
11699	1380	3	10514	32	Schaumbad	\N
11700	1380	3	10515	33	Badesalz/-tablette	\N
11701	1380	3	10516	34	Ölbad	\N
11702	1380	3	10517	35	Babybad	\N
11703	1380	3	13598	36	Shampoo und Duschbad	\N
11704	1380	3	13599	37	Duschgel und -creme	\N
11705	1380	3	13600	38	Badezusatz figürlich portioniert z.B. Badekugel	\N
11706	1380	3	10518	50	Abschminkmittel	\N
11707	1380	3	10519	51	Make-up-Entferner	\N
11708	1380	3	10520	52	Augen-make-up-Entferner	\N
11709	1381	3	10522	10	Körperpflegemittel	\N
11710	1381	3	10523	11	Hautcreme	\N
11711	1381	3	10524	12	Haut-/Körperlotion	\N
11712	1381	3	10525	13	Hautgel	\N
11713	1381	3	10526	14	Hautöl	\N
11714	1381	3	10527	15	Körperpuder	\N
11715	1381	3	10528	16	Handschutzmittel (Arbeitsschutz)	\N
11716	1381	3	10529	17	Handpflegemittel	\N
11717	1381	3	10530	18	Babycreme	\N
11718	1381	3	10531	19	Babylotion	\N
11719	1381	3	10532	20	Babyöl	\N
11720	1381	3	10533	21	Babypuder	\N
11721	1381	3	10534	22	Ampullenpräparat zur Hautpflege	\N
11722	1381	3	13601	23	Babypflegetuch	\N
11723	1381	3	10535	30	Gesichtspflegemittel	\N
11724	1381	3	10536	31	Gesichtscreme	\N
11725	1381	3	10537	32	Gesichtslotion/-wasser	\N
11726	1381	3	10538	33	Gesichtsgel	\N
11727	1381	3	10539	34	Gesichtspuder	\N
11728	1381	3	10540	35	Gesichtspackung/-maske	\N
11729	1381	3	10541	36	Ampullenpräparat zur Gesichtspflege	\N
11730	1381	3	10542	50	Pflegemittel zur Anwendung am Auge	\N
11731	1381	3	10543	51	Augencreme	\N
11732	1381	3	10544	52	Augenlotion	\N
11733	1381	3	10545	53	Augengel	\N
11734	1381	3	10546	54	Augenöl	\N
11735	1381	3	10547	55	Ampullenpräparat zur Pflege der Augen	\N
11736	1381	3	10548	60	Rasierhilfsmittel (incl. Reinigungsmittel)	\N
11737	1381	3	10549	61	Rasierseife	\N
11738	1381	3	10550	62	Rasierschaum	\N
11739	1381	3	10551	63	Rasiercreme/-stift	\N
11740	1381	3	10552	64	Rasiergel	\N
11741	1381	3	10553	65	Pre-Shave-Mittel	\N
11742	1381	3	10554	66	After-Shave-Mittel	\N
11743	1381	3	10555	70	Fußpflegemittel	\N
11744	1381	3	10556	71	Fußcreme	\N
11745	1381	3	10557	72	Fußlotion	\N
11746	1381	3	10558	73	Fußgel	\N
11747	1381	3	10559	74	Fußpuder	\N
11748	1381	3	10560	75	Fußbadesalz	\N
11749	1381	3	10561	80	Massagehilfsmittel	\N
11750	1381	3	10562	81	Massageöl	\N
11751	1381	3	10563	82	Massagecreme	\N
11752	1381	3	10564	83	Massagelotion/Sportfluid	\N
11754	1381	3	10566	90	Mittel gegen Hautunreinheiten	\N
11755	1381	3	10567	91	Reinigungslotion	\N
11756	1381	3	10568	92	Reinigungscreme	\N
11757	1381	3	10569	93	Peelingpräparat	\N
11758	1382	3	10571	10	Make-up-Präparat für die Haut	\N
11759	1382	3	10572	11	Creme-make-up/Tönungscreme	\N
11760	1382	3	10573	12	Make-up-Puder	\N
11761	1382	3	10574	13	Rouge	\N
11762	1382	3	10575	14	Schminke	\N
11763	1382	3	10576	15	Theaterschminke/Karnevalsschminke	\N
11764	1382	3	10577	16	Effektenspray	\N
11765	1382	3	13602	17	Camouflage	\N
11766	1382	3	13603	18	Abdeckstift	\N
11767	1382	3	10578	30	Augen-make-up	\N
11768	1382	3	10579	31	Mascara/Wimperntusche	\N
11769	1382	3	10580	32	Lidstrich/Eyeliner/Kajalstift	\N
11770	1382	3	10581	33	Lidschatten	\N
11771	1382	3	13604	34	Augenbrauenstift	\N
11772	1382	3	10582	40	Lippenkosmetik	\N
11773	1382	3	10583	41	Lippenpflegemittel	\N
11774	1382	3	10584	42	Lippenstift/-rouge	\N
11775	1382	3	10585	43	Lippenglanzpräparat/-pomade	\N
11776	1382	3	10586	44	Lippenpuder	\N
11777	1382	3	13605	45	Lippenkonturenstift	\N
11778	1382	3	10587	60	Mittel zur direkten Beeinflussung der Hautfarbe	\N
11779	1382	3	10588	61	Hautbleichmittel	\N
11780	1382	3	10589	62	Hautbräunungsmittel	\N
11781	1382	3	10590	70	Sonnenschutz-/-pflegemittel	\N
11782	1382	3	10591	71	Sonnenschutzcreme	\N
11783	1382	3	10592	72	Sonnenöl	\N
11784	1382	3	10593	73	Sonnenschutzgel	\N
11785	1382	3	10594	74	Sonnenschutzlotion	\N
11786	1382	3	10595	75	Sonnenschutzmittel für Kleinkinder	\N
11787	1382	3	10596	80	Sonnenpflegemittel	\N
11788	1382	3	10597	81	After-sun-Mittel	\N
11789	1382	3	10598	82	Pre-tanning-Mittel/Bräunungsmittel	\N
11790	1382	3	13720	83	Mittel zum Tätowieren, Tattoofarben	\N
11791	1382	3	14261	84	Tätowiermittel für Permanent Make Up (Tattoofarben)	\N
11792	1383	3	10600	10	Mittel zur Haarreinigung	\N
11793	1383	3	10601	11	Haarshampoo	\N
11794	1383	3	10602	12	Trockenshampoo	\N
11795	1383	3	10603	13	Antischuppenshampoo	\N
11796	1383	3	10604	14	Babyshampoo	\N
11797	1383	3	13606	15	Shampoo und Spülung	\N
11798	1383	3	10605	20	Mittel zur Haarpflege	\N
11799	1383	3	10606	21	Haarwasser/-lotion	\N
11800	1383	3	10607	22	Haaröl/-fett	\N
11801	1383	3	10608	23	Haarkur "leave on" (ausgen. 84 13 21)	\N
11802	1383	3	10609	24	Haarspülung "rinse off"	\N
11803	1383	3	10610	25	Frisiercreme/Pomade/Brilliantine	\N
11804	1383	3	13607	26	Haarkur "rinse off"	\N
11805	1383	3	13608	27	Sonnenschutzlotion für Haare	\N
11806	1383	3	13609	28	Haarwachs	\N
11807	1383	3	10611	30	Mittel zur Haarfestigung	\N
11808	1383	3	10612	31	Haargel	\N
11809	1383	3	10613	32	Haarspray/-lack	\N
11810	1383	3	10614	33	Haarfestiger/Fönlotion	\N
11811	1383	3	13618	34	Haarstylingschaum	\N
11812	1383	3	10615	40	Mittel zur Haarverformung	\N
11813	1383	3	10616	41	Dauerwellenpräparat allgemeine Verwendung	\N
11814	1383	3	10617	42	Dauerwellenpräparat gewerbliche Verwendung	\N
11815	1383	3	10618	43	Mittel zur Haarglättung allgemeine Verwendung	\N
11816	1383	3	10619	44	Mittel zur Haarglättung gewerbliche Verwendung	\N
11817	1383	3	10620	45	Fixiermittel allgemeine Verwendung	\N
11818	1383	3	10621	46	Fixiermittel gewerbliche Verwendung	\N
11819	1383	3	10622	50	Mittel zur Haarfärbung	\N
11820	1383	3	10623	51	Oxidationshaarfarbe allgemeine Verwendung	\N
11821	1383	3	10624	52	Oxidationshaarfarbe gewerbliche Verwendung	\N
11822	1383	3	10625	53	Farbentwickler (Oxidationsmittel) allgemeine Verwendung	\N
11823	1383	3	10626	54	Farbentwickler (Oxidationsmittel) gewerbliche Verwendung	\N
11824	1383	3	10627	55	Direktziehende Haarfarbe (Tönung) allgemeine Verwendung	\N
11825	1383	3	10628	56	Direktziehende Haarfarbe (Tönung) gewerbliche Verwendung	\N
11826	1383	3	10629	57	Haarfärbemittel auf pflanzlicher Basis	\N
11827	1383	3	10630	58	Haarbleichmittel allgemeine Verwendung	\N
11828	1383	3	10631	59	Haarbleichmittel gewerbliche Verwendung	\N
11829	1383	3	10632	60	Tönungsfestiger	\N
11830	1383	3	10633	61	Haarentfärbungsmittel	\N
11831	1383	3	10634	62	Augenbrauen-/Wimpernfärbemittel allgemeine Verwendung	\N
11832	1383	3	10635	63	Augenbrauen-/Wimpernfärbemittel gewerbliche Verwendung	\N
11833	1383	3	13619	64	Haarmascara	\N
11834	1383	3	10636	70	Mittel zur Haarentfernung (Depilation)	\N
11835	1383	3	10637	71	Haarentfernungsmittel chemisch	\N
11836	1383	3	10638	72	Haarentfernungsmittel mechanisch	\N
11837	1384	3	10640	10	Nagellack/-unterlack/-decklack	\N
11838	1384	3	10641	11	Nagellackentferner	\N
11839	1384	3	10642	12	Nagellackverdünner	\N
11840	1384	3	10643	13	Nagelhärter/-festiger	\N
11841	1384	3	10644	14	Mittel zur Nagelmodellage und Nagelverlängerung	\N
11842	1384	3	10645	15	Nagelbleichmittel	\N
11843	1384	3	10646	16	Nagelhautentferner/-erweicher	\N
11844	1384	3	10647	17	Nagelcreme/-öl	\N
11845	1385	3	10649	10	Zahncreme/-gel	\N
11846	1385	3	10650	11	Kinderzahncreme/-gel	\N
11847	1385	3	10651	12	Zahnreinigungspulver/-salz	\N
11848	1385	3	10653	14	Reinigungs-/Pflegemittel für Zahnersatz und Zahnspangen	\N
11849	1385	3	10654	15	Mundwasser-Konzentrat	\N
11850	1385	3	10655	16	Mundspray	\N
11851	1385	3	10656	17	Mund-/Zahnspülung	\N
11852	1385	3	10657	18	Mund-/Zahnspülung für Kinder	\N
11853	1385	3	10658	19	Kaudragee/Kaugummi zur Zahnpflege	\N
11854	1385	3	14262	20	Zahnaufheller Zahnbleichmittel häuslicher Bedarf	\N
11855	1385	3	14263	21	Zahnaufheller Zahnbleichmittel gewerblicher Bedarf	\N
11856	1386	3	10660	10	Deodorant/Antitranspirant (ausgen.84 10 14)	\N
11857	1386	3	10661	11	Deospray	\N
11858	1386	3	10662	12	Deoroller/-stift	\N
11859	1386	3	10663	13	Cremedeodorant	\N
11860	1386	3	10664	14	Puderdeodorant	\N
11861	1386	3	10665	15	Intimdeodorant	\N
11862	1386	3	10666	20	Parfüm/-öl	\N
11863	1386	3	10667	30	Toiletten-/Parfümwasser	\N
11864	1386	3	10668	40	Parfümcreme/Festparfüm	\N
11865	1386	3	10669	50	Kosmetisches Mittel das über Hilfsmittel (z.B. Watte/Textil-/Zellstoffunterlage) zur Anwendung kommt	\N
11866	1386	3	10670	51	Erfrischungstuch	\N
11867	1386	3	10671	53	Pads zur Nagellackentfernung	\N
11868	1386	3	10672	54	Pads zur Make-up-Entfernung	\N
11869	1386	3	13620	55	Pads zur Augen-Make-up-Entfernung	\N
11870	1386	3	13621	56	Feuchte Pflegetücher	\N
11871	1386	3	13622	57	Feuchte Reinigungstücher	\N
11872	1386	3	13623	58	Feuchtes Toilettenpapier	\N
11873	1386	3	13624	60	Pads zur Augen- und Hautpflege	\N
11874	1386	3	14264	61	Tuch mit Hautbräunungsmitteln	\N
11875	1387	3	10674	1	Farbstoff zur Herstellung kosmetischer Mittel ausgen. Haarfärbemittel	\N
11876	1387	3	10675	2	Farbstoff für Haarfärbemittel	\N
11877	1387	3	10676	3	Konservierungsmittel zur Herstellung kosmetischer Mittel	\N
11878	1387	3	10677	4	Ultraviolettfilter zur Herstellung kosmetischer Mittel	\N
11879	1387	3	10678	5	Antioxidantie zur Herstellung kosmetischer Mittel	\N
11880	1387	3	10679	6	Oberflächenaktiver Stoff zur Herstellung kosmetischer Mittel	\N
11881	1387	3	10680	7	Fett und Fettaustauschstoff zur Herstellung kosmetischer Mittel	\N
11882	1387	3	10681	8	Lösungsmittel zur Herstellung kosmetischer Mittel	\N
11883	1387	3	10682	9	Mineralischer Stoff zur Herstellung kosmetischer Mittel	\N
11884	1387	3	10683	10	Aroma zur Herstellung kosmetischer Mittel	\N
11885	1387	3	10684	11	Riechstoff zur Herstellung kosmetischer Mittel	\N
11886	1388	3	10687	1	Rassel/Greifling (für Kinder unter 36 Monaten geeignet)	\N
11887	1388	3	10688	2	Bauklotzspiel (für Kinder unter 36 Monaten geeignet)	\N
11888	1388	3	10689	3	Steckspiel (für Kinder unter 36 Monaten geeignet)	\N
11889	1388	3	10690	4	Großteile-Puzzlespiel (für Kinder unter 36 Monaten geeignet)	\N
11890	1388	3	10691	5	Hampelfigur (für Kinder unter 36 Monaten geeignet)	\N
11891	1388	3	10692	6	Ziehfigur (für Kinder unter 36 Monaten geeignet)	\N
11892	1388	3	13625	7	Puppe (für Kinder unter 36 Monaten geeignet)	\N
11893	1388	3	13626	8	Stofftier (für Kinder unter 36 Monaten geeignet)	\N
11894	1388	3	13627	9	Fahrzeug (für Kinder unter 36 Monaten geeignet)	\N
11895	1388	3	13628	10	Bilderbuch (für Kinder unter 36 Monaten geeignet)	\N
11896	1389	3	10694	1	Figur/Puppe	\N
11897	1389	3	10695	2	Stofftier	\N
11898	1389	3	10696	3	Kraftfahrzeug	\N
11899	1389	3	10697	4	Flugzeug	\N
11900	1389	3	10698	5	Eisenbahn	\N
11901	1389	3	10699	6	Schiff/Boot	\N
11902	1389	3	13629	51	Figuren-/Puppenzubehör	\N
11903	1390	3	10701	1	Wasserfarben/Tuschkasten	\N
11904	1390	3	10702	2	Fingerfarben	\N
11905	1390	3	10703	3	Filzstifte/Buntstifte	\N
11906	1390	3	10704	4	Plakatfarben	\N
11907	1390	3	10705	5	Wachsmalstifte	\N
11908	1390	3	10706	6	Kreide	\N
11909	1390	3	13630	7	Spielware mit Schreib-/Malfunktion	\N
11910	1390	3	14283	8	Malbuch	\N
11911	1391	3	10708	1	Geduldsspiel	\N
11912	1391	3	10709	2	Lernspiel (ausgen. 851400)	\N
11913	1392	3	10711	1	Metallbaukasten	\N
11914	1392	3	10712	2	Chemieexperimentierkasten	\N
11915	1392	3	10713	3	Physikexperimentierkasten	\N
11916	1392	3	13631	4	Modellbaukasten und -set	\N
11917	1392	3	13632	5	Holzbaukasten	\N
11918	1392	3	13633	6	Kunststoffbaukasten	\N
11919	1392	3	13634	7	Bausatz für Papp-/Papiermodelle	\N
11920	1392	3	13635	8	Klebstoff für Bastel-/Bauset Spielwarenbereich	\N
11921	1392	3	13636	9	Lack für Bastel-/Bausets Spielwarenbereich	\N
11922	1392	3	13637	20	Perlenspiel	\N
11923	1392	3	13638	21	Steckspiel	\N
11924	1392	3	13639	22	Handarbeitsset für Kinder	\N
11925	1392	3	13640	23	Bastelbögen/Buntpapier	\N
11926	1392	3	13721	24	Puzzlespiel außer 851004	\N
11927	1393	3	10715	1	Knete	\N
11928	1393	3	10716	2	Aushärtbare Knete	\N
11929	1393	3	10717	3	Wabbelmasse	\N
11930	1394	3	10719	1	Pistole/Schwert	\N
11931	1394	3	10720	2	Ballspiel	\N
11932	1394	3	10721	3	Werkzeugkoffer	\N
11933	1394	3	10722	4	Flugdrachen	\N
11934	1394	3	10723	5	Sandspielzeug	\N
11935	1394	3	13641	6	Seifenblasen-Spielzeug	\N
11936	1394	3	13642	7	Wasserspielzeug	\N
11937	1394	3	13643	8	Musikspielzeug	\N
11938	1395	3	10725	1	Arztkoffer	\N
11939	1395	3	10726	2	Kaufmannsladen und Zubehör	\N
11940	1395	3	10727	3	Puppenhaus und Zubehör	\N
11941	1395	3	10728	4	Frisierset	\N
11942	1395	3	10729	5	Kosmetikset	\N
11943	1395	3	10730	6	Spielzeuggeschirr	\N
11944	1395	3	10731	7	Spielzeugkochset	\N
11945	1395	3	13644	8	Magier-/Zauberkasten	\N
11946	1398	3	10735	10	Verpackungsmaterial für Lebensmittel aus Keramik	\N
11947	1398	3	10736	15	Verpackungsmaterial für Lebensmittel aus Glas	\N
11948	1398	3	10737	20	Verpackungsmaterial für Lebensmittel aus Metall	\N
11949	1398	3	10738	29	Verpackungsmaterial für Lebensmittel aus Metall lackiert/beschichtet	\N
11950	1398	3	10739	30	Verpackungsmaterial für Lebensmittel aus Kunststoff	\N
11951	1398	3	10740	39	Verpackungsmaterial für Lebensmittel aus Ionenaustauscherharz	\N
11952	1398	3	10741	40	Verpackungsmaterial für Lebensmittel aus Elastomeren/Kautschuk	\N
11953	1398	3	10742	45	Verpackungsmaterial für Lebensmittel mit Kunststoffbeschichtung	\N
11954	1398	3	10743	50	Verpackungsmaterial für Lebensmittel aus Papier/Pappe/Karton	\N
11955	1398	3	10744	55	Verpackungsmaterial für Lebensmittel aus Zellglas	\N
11956	1398	3	10745	60	Verpackungsmaterial für Lebensmittel mit Wachs-/Paraffin-/Beschichtung/Überzug	\N
11957	1398	3	10746	70	Verpackungsmaterial für Lebensmittel aus textilem Material	\N
11958	1398	3	10747	80	Verpackungsmaterial für Lebensmittel aus Holz	\N
11959	1398	3	10748	85	Verpackungsmaterial für Lebensmittel aus Kork	\N
11960	1398	3	10749	99	Verpackungsmaterial für Lebensmittel aus Materialkombinationen	\N
11961	1399	3	10751	10	Gegenstand zum Verzehr von Lebensmitteln aus Keramik	\N
11962	1399	3	14265	11	Gegenstand aus Keramik zum Verzehr von Lebensmitteln Nicht füllbare Gegenst. oder füllbar bis 25 mm 	\N
11963	1399	3	14266	12	Gegenstand aus Keramik zum Verzehr von Lebensmitteln Füllbare Gegenstände mit einer Fülltiefe >25 mm	\N
11964	1399	3	14267	13	Gegenstand aus Keramik zum Verzehr von Lebensmitteln Koch- und Backgerät; Behältnis > 3 Liter	\N
11965	1399	3	10752	15	Gegenstand zum Verzehr von Lebensmitteln aus Glas	\N
11966	1399	3	10753	20	Gegenstand zum Verzehr von Lebensmitteln aus Metall	\N
11967	1399	3	10754	29	Gegenstand zum Verzehr von Lebensmitteln aus Metall lackiert/beschichtet	\N
11968	1399	3	10755	30	Gegenstand zum Verzehr von Lebensmitteln aus Kunststoff	\N
11969	1399	3	10756	39	Gegenstand zum Verzehr von Lebensmitteln aus Ionenaustauscherharz	\N
11970	1399	3	10757	40	Gegenstand zum Verzehr von Lebensmitteln aus Elastomeren/Kautschuk	\N
11971	1399	3	10758	45	Gegenstand zum Verzehr von Lebensmitteln mit Kunststoffbeschichtung	\N
11972	1399	3	10759	50	Gegenstand zum Verzehr von Lebensmitteln aus Papier/Pappe/Karton	\N
11973	1399	3	10760	55	Gegenstand zum Verzehr von Lebensmitteln aus Zellglas	\N
11974	1399	3	10761	60	Gegenstand zum Verzehr von Lebensmitteln mit Wachs-/Paraffin-Beschichtung/Überzug	\N
11975	1399	3	10762	70	Gegenstand zum Verzehr von Lebensmitteln aus textilem Material	\N
11976	1399	3	10763	80	Gegenstand zum Verzehr von Lebensmitteln aus Holz	\N
11977	1399	3	10764	85	Gegenstand zum Verzehr von Lebensmitteln aus Kork	\N
11978	1399	3	10765	99	Gegenstand zum Verzehr von Lebensmitteln aus Materialkombinationen	\N
11979	1400	3	10767	10	Gegenstand zum Kochen/Braten/Backen/Grillen aus Keramik (ausgenommen 869010)	\N
11980	1400	3	10768	15	Gegenstand zum Kochen/Braten/Backen/Grillen aus Glas (ausgenommen 869015)	\N
11981	1400	3	10769	20	Gegenstand zum Kochen/Braten/Backen/Grillen aus Metall (ausgenommen 869020)	\N
11982	1400	3	10770	29	Gegenstand zum Kochen/Braten/Backen/Grillen aus Metall lackiert/beschichtet (ausgenommen 869029)	\N
11983	1400	3	10771	30	Gegenstand zum Kochen/Braten/Backen/Grillen aus Kunststoff (ausgenommen 869030)	\N
11984	1400	3	10772	39	Gegenstand zum Kochen/Braten/Backen/Grillen aus Ionenaustauscherharz (ausgenommen 869039)	\N
11985	1400	3	10773	40	Gegenstand zum Kochen/Braten/Backen/Grillen aus Elastomeren/Kautschuk (ausgenommen 869040)	\N
11986	1400	3	10774	45	Gegenstand zum Kochen/Braten/Backen/Grillen mit Kunststoffbeschichtung (ausgenommen 869045)	\N
11987	1400	3	10775	50	Gegenstand zum Kochen/Braten/Backen/Grillen aus Papier/Pappe/Karton (ausgenommen 869050)	\N
11988	1400	3	10776	55	Gegenstand zum Kochen/Braten/Backen/Grillen aus Zellglas (ausgenommen 869055)	\N
11989	1400	3	10777	60	Gegenstand zum Kochen/Braten/Backen/Grillen mit Wachs-/Paraffin-Beschichtung/Überzug (ausgenommen 8	\N
11990	1400	3	10778	70	Gegenstand zum Kochen/Braten/Backen/Grillen aus textilem Material (ausgenommen 869070)	\N
11991	1400	3	10779	80	Gegenstand zum Kochen/Braten/Backen/Grillen aus Holz (ausgenommen 869080)	\N
11992	1400	3	10780	85	Gegenstand zum Kochen/Braten/Backen/Grillen aus Kork (ausgenommen 869085)	\N
11993	1400	3	10781	99	Gegenstand zum Kochen/Braten/Backen/Grillen aus Materialkombinationen (ausgenommen 869099)	\N
11994	1401	3	10783	10	Sonstiger Gegenstand zur Herstellung und Behandlung von Lebensmitteln aus Keramik (ausgenom. 869010)	\N
11995	1401	3	10784	15	Sonstiger Gegenstand zur Herstellung und Behandlung von Lebensmitteln aus Glas (ausgenommen 869015)	\N
11996	1401	3	10785	20	Sonstiger Gegenstand zur Herstellung und Behandlung von Lebensmitteln aus Metall (ausgenom. 869020)	\N
11997	1401	3	10786	29	Sonstiger Gegenstand zur Herstellung u. Behandlung von Lebensmitteln aus Metall lackiert/beschichtet	\N
11998	1401	3	10787	30	Sonstiger Gegenstand zur Herstellung und Behandlung von Lebensmitteln aus Kunststoff (ausge. 869030)	\N
11999	1401	3	10788	39	Sonstiger Gegenstand zur Herstellung und Behandlung von Lebensmitteln aus Ionenaustauscherharz	\N
12000	1401	3	10789	40	Sonstiger  Gegenstand zur Herstellung und Behandlung von Lebensmitteln aus Elastomeren/Kautschuk	\N
12001	1401	3	10790	45	Sonstiger Gegenstand zur Herstellung und Behandlung von Lebensmitteln mit Kunststoffbeschichtung	\N
12002	1401	3	10791	50	Sonstiger Gegenstand zur Herstellung und Behandlung von Lebensmitteln aus Papier/Pappe/Karton	\N
12003	1401	3	10792	55	Sonstiger Gegenstand zur Herstellung und Behandlung von Lebensmitteln aus Zellglas (ausgen. 869055)	\N
12004	1401	3	10793	60	Sonstiger Gegenstand zur Herstellung u. Behandlung von Lebensmitteln mit Wachs/Paraffin-Beschichtung	\N
12005	1401	3	10794	70	Sonstiger Gegenstand zur Herstellung und Behandlung von Lebensmitteln aus textilem Material (869070)	\N
12006	1401	3	10795	80	Sonstiger Gegenstand zur Herstellung und Behandlung von Lebensmitteln aus Holz (ausgenommen 869080)	\N
12007	1401	3	10796	85	Sonstiger Gegenstand zur Herstellung und Behandlung von Lebensmitteln aus Kork (ausgenommen 869085)	\N
12008	1401	3	10797	99	Sonstiger Gegenstand zur Herstellung und Behandlung von Lebensmitteln aus Materialkombinationen	\N
12009	1402	3	10799	10	Maschine zur gewerblichen Herstellung von Lebensmitteln aus Keramik	\N
12010	1402	3	10800	15	Maschine zur gewerblichen Herstellung von Lebensmitteln aus Glas	\N
12011	1402	3	10801	20	Maschine zur gewerblichen Herstellung von Lebensmitteln aus Metall	\N
12012	1402	3	10802	29	Maschine zur gewerblichen Herstellung von Lebensmitteln aus Metall lackiert/beschichtet	\N
12013	1402	3	10803	30	Maschine zur gewerblichen Herstellung von Lebensmitteln aus Kunststoff	\N
12014	1402	3	10804	39	Maschine zur gewerblichen Herstellung von Lebensmitteln aus Ionenaustauscherharz	\N
12015	1402	3	10805	40	Maschine zur gewerblichen Herstellung von Lebensmitteln aus Elastomeren/Kautschuk	\N
12016	1402	3	10806	45	Maschine zur gewerblichen Herstellung von Lebensmitteln mit Kunststoffbeschichtung	\N
12017	1402	3	10807	50	Maschine zur gewerblichen Herstellung von Lebensmitteln aus Papier/Pappe/Karton	\N
12018	1402	3	10808	55	Maschine zur gewerblichen Herstellung von Lebensmitteln aus Zellglas	\N
12019	1402	3	10809	60	Maschine zur gewerblichen Herstellung von Lebensmitteln  mit Wachs-/Paraffin-Beschichtung/Überzug	\N
12020	1402	3	10810	70	Maschine zur gewerblichen Herstellung von Lebensmitteln aus textilem Material	\N
12129	1464	3	10984	1	Ohrmuschel (Auricula)	\N
12021	1402	3	10811	80	Maschine zur gewerblichen Herstellung von Lebensmitteln aus Holz	\N
12022	1402	3	10812	85	Maschine zur gewerblichen Herstellung von Lebensmitteln aus Kork	\N
12023	1402	3	10813	99	Maschine zur gewerblichen Herstellung von Lebensmitteln aus Materialkombinationen	\N
12024	1403	3	10817	1	Schädelknochen (Ossa cranii)	\N
12025	1403	3	10818	2	Oberkiefer (Maxilla)	\N
12026	1403	3	10819	3	Unterkiefer (Mandibula)	\N
12027	1403	3	10820	4	Felsenbein (Os petrosum)	\N
12028	1403	3	10821	5	Wirbel (Vertebra)	\N
12029	1403	3	10822	6	Schultergürtelknochen (Cingulum membri superioris)	\N
12030	1403	3	10823	7	Rippen (Costae)	\N
12031	1403	3	10824	8	Beckenknochen (Cingulum membri inferioris)	\N
12032	1403	3	10825	9	Oberarmknochen (Humerus)	\N
12033	1403	3	10826	10	Oberschenkelknochen (Femur)	\N
12034	1404	3	10828	1	Wirbelgelenk (Articulatio intervertebralis)	\N
12035	1404	3	10829	2	Gelenke, Extremität obere (Juncturae membri superioris liberi)	\N
12036	1404	3	10830	3	Gelenke, Extremität untere (Juncturae membri inferioris liberi)	\N
12037	1405	3	10832	1	Skelettmuskeln (Musculi skeleti)	\N
12038	1405	3	10833	2	Zungenmuskel (Musculus linguae)	\N
12039	1406	3	10835	1	Knorpel, hyaliner	\N
12040	1406	3	10836	2	Knorpel, faseriger	\N
12041	1406	3	10837	3	Knorpel, elastischer	\N
12042	1406	3	10838	4	Rippenknorpel (Cartilago costalis)	\N
12043	1406	3	10839	5	Bandscheibe (Diskus intervertebralis)	\N
12044	1406	3	10840	6	Gelenkzwischenscheibe (Meniscus articularis)	\N
12045	1415	3	10850	1	Nase (Nasus)	\N
12046	1415	3	10851	2	Kehldeckel (Epiglottis)	\N
12047	1415	3	10852	3	Stimmband (Plica vocalis)	\N
12048	1415	3	10853	4	Kehlkopf (Larynx)	\N
12049	1415	3	10854	5	Luftröhre (Trachea)	\N
12050	1415	3	10855	6	Bronchus	\N
12051	1416	3	10857	1	Lunge rechte	\N
12052	1416	3	10858	2	Lunge linke	\N
12053	1416	3	10859	3	Brustfell (Pleura)	\N
12054	1417	3	10862	1	Schleim, untere Luftwege (Sputum)	\N
12055	1417	3	10863	2	Pleuraflüssigkeit	\N
12056	1418	3	10865	1	Pericard	\N
12057	1418	3	10866	2	Herzmuskel (Myocard)	\N
12058	1418	3	10867	3	Endocard	\N
12059	1418	3	10868	4	Herzklappen (Valvae cordis)	\N
12060	1419	3	10870	1	Arterien (Arteriae)	\N
12061	1419	3	10871	2	Aorta	\N
12062	1419	3	10872	3	Venen (Venae)	\N
12063	1419	3	10873	4	Hohlvene (Vena cava)	\N
12064	1419	3	10874	5	Pfortader (Vena portae)	\N
12065	1420	3	10876	1	Mund (Os)	\N
12066	1420	3	10877	2	Lippen (Labia)	\N
12067	1420	3	10878	3	Oberlippe (Labium superius)	\N
12068	1420	3	10879	4	Unterlippe (Labium inferius)	\N
12069	1420	3	10880	5	Zunge (Lingua)	\N
12070	1421	3	10882	1	Zähne (Dentes)	\N
12071	1421	3	10883	2	Zähne bleibende (Dentes permanentes)	\N
12072	1421	3	10884	3	Milchzähne (Dentes desidui)	\N
12073	1421	3	10885	4	Zahnfleisch (Gingiva)	\N
12074	1422	3	10887	1	Ohrspeicheldrüse (Glandula parotis)	\N
12075	1422	3	10888	2	Unterzungendrüse (Glandula sublingualis)	\N
12076	1422	3	10889	3	Unterkieferdrüse (Glandula submandibularis)	\N
12077	1423	3	10891	1	Leberkapsel (Tunica fibrosa)	\N
12078	1423	3	10892	2	Leberparenchym	\N
12079	1423	3	10893	3	Lebergallengang	\N
12080	1423	3	10894	4	Gallenblase (Vesica fellea)	\N
12081	1423	3	10895	5	Gallengang (Ductus hepaticus)	\N
12082	1443	3	10916	1	Magensaft	\N
12083	1443	3	10917	2	Darmsaft	\N
12084	1443	3	10918	3	Mageninhalt	\N
12085	1443	3	10919	4	Dünndarminhalt (Chymus)	\N
12086	1443	3	10920	5	Darminhalt	\N
12087	1443	3	10921	6	Kot (Fäces)	\N
12088	1443	3	10922	7	Erbrochenes	\N
12089	1444	3	10924	1	Niere (Ren) (Nephros)	\N
12090	1444	3	10925	2	Nierenrinde (Cortex renalis)	\N
12091	1444	3	10926	3	Nierenmark (Medulla renalis)	\N
12092	1444	3	10927	4	Nierenbecken  (Pelvis renalis)	\N
12093	1448	3	10932	1	Vorsteherdrüse (Prostata)	\N
12094	1449	3	10934	1	Nebenhoden (Epididymis)	\N
12095	1449	3	10935	2	Samenleiter (Ductus deferens)	\N
12096	1450	3	10937	1	Harn (Urin)	\N
12097	1450	3	10938	2	Samenflüssigkeit (Sperma)	\N
12098	1450	3	10939	3	Prostatasekret	\N
12099	1450	3	10940	4	Vorhauttalg (Smegma)	\N
12100	1450	3	10941	5	Wochenfluss (Lochien)	\N
12101	1453	3	10945	1	Uterusschleimhaut (Endometrium)	\N
12102	1454	3	10947	1	Eileiter (Tuba uterina)	\N
12103	1454	3	10948	2	Eileitertrichter (Salpinx)	\N
12104	1457	3	10952	1	Amnion-Flüssigkeit	\N
12105	1457	3	10953	2	Kindspech (Meconium)	\N
12106	1458	3	10955	1	Hirnanhangsdrüse (Hypophyse)	\N
12107	1458	3	10956	2	Zirbeldrüse (Epiphyse)	\N
12108	1458	3	10957	3	Schilddrüse (Glandula thyreoidea)	\N
12109	1458	3	10958	4	Epithelkörperchen (Glandula parathyreoidea)	\N
12110	1458	3	10959	5	Bries (Thymus)	\N
12111	1458	3	10960	6	Pankreas-Inselorgan (Langerhans Inseln)	\N
12112	1459	3	10962	1	Nebennierenrinde	\N
12113	1459	3	10963	2	Nebennierenmark	\N
12114	1461	3	10966	1	Großhirn, graue Substanz (Substantia grisea)	\N
12115	1461	3	10967	2	Großhirn, weiße Substanz (Substantia alba)	\N
12116	1461	3	10968	3	Thalamus	\N
12117	1461	3	10969	4	Mittelhirn (Mesencephalon)	\N
12118	1461	3	10970	5	Kleinhirn (Cerebellum)	\N
12119	1461	3	10971	6	Spinalnerven (Nervi spinales)	\N
12120	1461	3	10972	7	Vegetatives Nervensystem (Systema nervosum autonomicum)	\N
12121	1461	3	10973	8	Rückenmark (Medulla)	\N
12122	1461	3	10974	9	Hirnnerven (Nervi craniales)	\N
12123	1463	3	10977	1	Glaskörper (Corpus vitreum)	\N
12124	1463	3	10978	2	Hornhaut (Cornea)	\N
12125	1463	3	10979	3	Linse (Lens)	\N
12126	1463	3	10980	4	Lid (Palpebra)	\N
12127	1463	3	10981	5	Bindehaut (Conjunctiva)	\N
12128	1463	3	10982	6	Tränen (Lacrimae)	\N
12130	1464	3	10985	2	Mittelohr (Auris media)	\N
12131	1464	3	10986	3	Gehörknöchelchen (Ossicula auditus)	\N
12132	1464	3	10987	4	Innenohr (Auris interna)	\N
12133	1465	3	10989	1	Oberhaut (Epidermis)	\N
12134	1465	3	10990	2	Lederhaut (Corium)	\N
12135	1465	3	10991	3	Hautdrüsen (Glandulae cutis)	\N
12136	1466	3	10993	1	Haupthaar	\N
12137	1466	3	10994	2	Augenbraue (Supercilium)	\N
12138	1466	3	10995	3	Augenwimper (Cilie)	\N
12139	1466	3	10996	4	Bart (Barba)	\N
12140	1466	3	10997	5	Schamhaar (P. pubii)	\N
12141	1467	3	10999	1	Fingernagel	\N
12142	1467	3	11000	2	Zehennagel	\N
12143	1469	3	11003	1	Mamma, weibliche	\N
12144	1469	3	11004	2	Mamma, männliche	\N
12145	1469	3	11005	3	Brustwarze (Papilla mammae)	\N
12146	1469	3	11006	4	Mamma, laktierende	\N
12147	1470	3	11008	1	Lymphatisches Gewebe	\N
12148	1472	3	11011	1	Rotes Knochenmark (Medulla ossium rubra)	\N
12149	1473	3	11013	1	Milz, Pulpa weiße	\N
12150	1473	3	11014	2	Milz, Pulpa rote	\N
12151	1473	3	11015	3	Milz, Malpighi-Körperchen	\N
12152	1474	3	11017	1	Lymphknoten-Kopf	\N
12153	1474	3	11018	2	Lymphknoten-Hals	\N
12154	1474	3	11019	3	Lymphknoten-Thorax	\N
12155	1474	3	11020	4	Lymphknoten-Abdomen	\N
12156	1474	3	11021	5	Lymphknoten-Becken	\N
12157	1474	3	11022	6	Lymphknoten-obere Extremität	\N
12158	1474	3	11023	7	Lymphknoten-untere Extremität	\N
12159	1474	3	11024	8	Lymphgefäße	\N
12160	1474	3	11025	9	Lymphe	\N
12161	1475	3	11027	1	Blutkörperchen, rotes (Erythrocyt)	\N
12162	1475	3	11028	2	Blutkörperchen, weißes (Leukocyt)	\N
12163	1475	3	11029	3	Monocyt	\N
12164	1475	3	11030	4	Plasma	\N
12165	1475	3	11031	5	Serum	\N
12166	1476	3	11033	1	Schweiß (Sudor)	\N
12167	1476	3	11034	2	Hauttalg (Sebum)	\N
12168	1476	3	11035	3	Ohrschmalz (Cerumen)	\N
12169	1477	3	11037	1	Reife Frauenmilch	\N
12170	1477	3	11038	2	Transitorische Milch	\N
12171	1477	3	11039	3	Vormilch (Kolostrum)	\N
12172	1478	3	11053	1	Schädelknochen (Ossa cranii)	\N
12173	1478	3	11054	2	Oberkiefer (Maxilla)	\N
12174	1478	3	11055	3	Unterkiefer (Mandibula)	\N
12175	1478	3	11056	4	Felsenbein (Os petrosum)	\N
12176	1478	3	11057	5	Wirbel (Vertebra)	\N
12177	1478	3	11058	6	Schultergürtelknochen (Cingulum membri superioris)	\N
12178	1478	3	11059	7	Rippen (Costae)	\N
12179	1478	3	11060	8	Beckenknochen (Cingulum membri inferioris)	\N
12180	1478	3	11061	9	Oberarmknochen (Humerus)	\N
12181	1478	3	11062	10	Oberschenkelknochen (Femur)	\N
12182	1478	3	11063	11	Prothesen:Knochen	\N
12183	1478	3	11064	12	Knochennagel, Knochenschraube	\N
12184	1479	3	11066	1	Wirbelgelenk (Articulatio intervertebralis)	\N
12185	1479	3	11067	2	Gelenke, Extremität obere   (Juncturae membri superioris liberi)	\N
12186	1479	3	11068	3	Gelenke, Extremität untere (Juncturae membri inferioris liberi)	\N
12187	1479	3	11069	4	Prothesen:Gelenke	\N
12188	1480	3	11071	1	Skelettmuskeln (Musculi skeleti)	\N
12189	1480	3	11072	2	Zungenmuskel (Musculus linguae)	\N
12190	1481	3	11074	1	Knorpel, hyaliner	\N
12191	1481	3	11075	2	Knorpel, faseriger	\N
12192	1481	3	11076	3	Knorpel, elastischer	\N
12193	1481	3	11077	4	Rippenknorpel (Cartilago costalis)	\N
12194	1481	3	11078	5	Bandscheibe (Diskus intervertebralis)	\N
12195	1481	3	11079	6	Gelenkzwischenscheibe (Meniscus articularis)	\N
12196	1490	3	11089	1	Nase (Nasus)	\N
12197	1490	3	11090	2	Kehldeckel (Epiglottis)	\N
12198	1490	3	11091	3	Stimmband (Plica vocalis)	\N
12199	1490	3	11092	4	Kehlkopf (Larynx)	\N
12200	1490	3	11093	5	Luftröhre (Trachea)	\N
12201	1490	3	11094	6	Bronchus	\N
12202	1490	3	11095	7	Fremdkörper:Luftwege	\N
12203	1491	3	11097	1	Lunge rechte	\N
12204	1491	3	11098	2	Lunge linke	\N
12205	1491	3	11099	3	Brustfell (Pleura)	\N
12206	1492	3	11101	1	Schleim, untere Luftwege (Sputum)	\N
12207	1492	3	11102	2	Pleuraflüssigkeit	\N
12208	1492	3	11103	3	Fremdkörper:Lunge	\N
12209	1493	3	11105	1	Pericard	\N
12210	1493	3	11106	2	Herzmuskel (Myocard)	\N
12211	1493	3	11107	3	Endocard	\N
12212	1493	3	11108	4	Herzklappen (Valvae cordis)	\N
12213	1494	3	11110	1	Arterien (Arteriae)	\N
12214	1494	3	11111	2	Aorta	\N
12215	1494	3	11112	3	Venen (Venae)	\N
12216	1494	3	11113	4	Hohlvene (Vena cava)	\N
12217	1494	3	11114	5	Pfortader (Vena portae)	\N
12218	1495	3	11116	1	Mund (Os)	\N
12219	1495	3	11117	2	Lippen (Labia)	\N
12220	1495	3	11118	3	Oberlippe (Labium superius)	\N
12221	1495	3	11119	4	Unterlippe (Labium inferius)	\N
12222	1495	3	11120	5	Zunge (Lingua)	\N
12223	1496	3	11122	1	Zähne (Dentes)	\N
12224	1496	3	11123	2	Zähne bleibende (Dentes permanentes)	\N
12225	1496	3	11124	3	Milchzähne (Dentes desidui)	\N
12226	1496	3	11125	4	Zahnfleisch (Gingiva)	\N
12227	1496	3	11126	5	Fremdkörper:Mund	\N
12228	1496	3	11127	6	Zahnfüllung (Plombe)	\N
12229	1496	3	11128	7	Zahnkronen	\N
12230	1496	3	11129	8	Zahnprothesen	\N
12231	1497	3	11131	1	Ohrspeicheldrüse (Glandula parotis)	\N
12232	1497	3	11132	2	Unterzungendrüse (Glandula sublingualis)	\N
12233	1497	3	11133	3	Unterkieferdrüse (Glandula submandibularis)	\N
12234	1498	3	11135	1	Leberkapsel (Tunica fibrosa)	\N
12235	1498	3	11136	2	Leberparenchym	\N
12236	1498	3	11137	3	Lebergallengang	\N
12237	1498	3	11138	4	Gallenblase (Vesica fellea)	\N
12238	1498	3	11139	5	Gallengang (Ductus hepaticus)	\N
12239	1500	3	11142	1	Speichelstein	\N
12240	1501	3	11144	1	Gallenstein	\N
12241	1506	3	11150	1	Fremdkörper:Magen	\N
12242	1508	3	11153	1	Fremdkörper:Dünndarm	\N
12243	1515	3	11161	1	Fremdkörper:Enddarm	\N
12244	1518	3	11165	1	Magensaft	\N
12245	1518	3	11166	2	Darmsaft	\N
12246	1518	3	11167	3	Mageninhalt	\N
12247	1518	3	11168	4	Dünndarminhalt (Chymus)	\N
12248	1518	3	11169	5	Darminhalt	\N
12249	1518	3	11170	6	Kot (Fäces)	\N
12250	1518	3	11171	7	Erbrochenes	\N
12251	1518	3	11172	8	Kotstein	\N
12252	1518	3	11173	9	Bezoar	\N
12253	1519	3	11175	1	Niere (Ren) (Nephros)	\N
12254	1519	3	11176	2	Nierenrinde (Cortex renalis)	\N
12255	1519	3	11177	3	Nierenmark (Medulla renalis)	\N
12256	1519	3	11178	4	Nierenbecken  (Pelvis renalis)	\N
12257	1523	3	11183	1	Vorsteherdrüse (Prostata)	\N
12258	1524	3	11185	1	Nebenhoden (Epididymis)	\N
12259	1524	3	11186	2	Samenleiter (Ductus deferens)	\N
12260	1525	3	11188	1	Harn (Urin)	\N
12261	1525	3	11189	2	Samenflüssigkeit (Sperma)	\N
12262	1525	3	11190	3	Prostatasekret	\N
12263	1525	3	11191	4	Vorhauttalg (Smegma)	\N
12264	1525	3	11192	5	Wochenfluss (Lochien)	\N
12265	1525	3	11193	6	Nierenstein	\N
12266	1525	3	11194	7	Harnstein	\N
12267	1525	3	11195	8	Fremdkörper:Harntrakt	\N
12268	1528	3	11199	1	Uterusschleimhaut (Endometrium)	\N
12269	1529	3	11201	1	Eileiter (Tuba uterina)	\N
12270	1529	3	11202	2	Eileitertrichter (Salpinx)	\N
12271	1532	3	11206	1	Amnion-Flüssigkeit	\N
12272	1532	3	11207	2	Kindspech (Meconium)	\N
12273	1533	3	11209	1	Hirnanhangsdrüse (Hypophyse)	\N
12274	1533	3	11210	2	Zirbeldrüse (Epiphyse)	\N
12275	1533	3	11211	3	Schilddrüse (Glandula thyreoidea)	\N
12276	1533	3	11212	4	Epithelkörperchen (Glandula parathyreoidea)	\N
12277	1533	3	11213	5	Bries (Thymus)	\N
12278	1533	3	11214	6	Pankreas-Inselorgan (Langerhans Inseln)	\N
12279	1534	3	11216	1	Nebennierenrinde	\N
12280	1534	3	11217	2	Nebennierenmark	\N
12281	1536	3	11220	1	Großhirn, graue Substanz (Substantia grisea)	\N
12282	1536	3	11221	2	Großhirn, weiße Substanz (Substantia alba)	\N
12283	1536	3	11222	3	Thalamus	\N
12284	1536	3	11223	4	Mittelhirn (Mesencephalon)	\N
12285	1536	3	11224	5	Kleinhirn (Cerebellum)	\N
12286	1536	3	11225	6	Spinalnerven (Nervi spinales)	\N
12287	1536	3	11226	7	Vegetatives Nervensystem (Systema nervosum autonomicum)	\N
12288	1536	3	11227	8	Rückenmark (Medulla)	\N
12289	1536	3	11228	9	Hirnnerven (Nervi craniales)	\N
12290	1538	3	11231	1	Glaskörper (Corpus vitreum)	\N
12291	1538	3	11232	2	Hornhaut (Cornea)	\N
12292	1538	3	11233	3	Linse (Lens)	\N
12293	1538	3	11234	4	Lid (Palpebra)	\N
12294	1538	3	11235	5	Bindehaut (Conjunctiva)	\N
12295	1538	3	11236	6	Tränen (Lacrimae)	\N
12296	1538	3	11237	7	Fremdkörper:Auge	\N
12297	1539	3	11239	1	Ohrmuschel (Auricula)	\N
12298	1539	3	11240	2	Mittelohr (Auris media)	\N
12299	1539	3	11241	3	Gehörknöchelchen (Ossicula auditus)	\N
12300	1539	3	11242	4	Innenohr (Auris interna)	\N
12301	1539	3	11243	5	Fremdkörper:Ohr	\N
12302	1540	3	11245	1	Oberhaut (Epidermis)	\N
12303	1540	3	11246	2	Lederhaut (Corium)	\N
12304	1540	3	11247	3	Hautdrüsen (Glandulae cutis)	\N
12305	1540	3	11248	4	Narbengewebe	\N
12306	1541	3	11250	1	Haupthaar	\N
12307	1541	3	11251	2	Augenbraue (Supercilium)	\N
12308	1541	3	11252	3	Augenwimper (Cilie)	\N
12309	1541	3	11253	4	Bart (Barba)	\N
12310	1541	3	11254	5	Schamhaar (P. pubii)	\N
12311	1542	3	11256	1	Fingernagel	\N
12312	1542	3	11257	2	Zehennagel	\N
12313	1544	3	11260	1	Mamma, weibliche	\N
12314	1544	3	11261	2	Mamma, männliche	\N
12315	1544	3	11262	3	Brustwarze (Papilla mammae)	\N
12316	1544	3	11263	4	Mamma, laktierende	\N
12317	1545	3	11265	1	Lymphatisches Gewebe	\N
12318	1547	3	11268	1	Rotes Knochenmark (Medulla ossium rubra)	\N
12319	1548	3	11270	1	Milz, Pulpa weiße	\N
12320	1548	3	11271	2	Milz, Pulpa rote	\N
12321	1548	3	11272	3	Milz, Malpighi-Körperchen	\N
12322	1549	3	11274	1	Lymphknoten-Kopf	\N
12323	1549	3	11275	2	Lymphknoten-Hals	\N
12324	1549	3	11276	3	Lymphknoten-Thorax	\N
12325	1549	3	11277	4	Lymphknoten-Abdomen	\N
12326	1549	3	11278	5	Lymphknoten-Becken	\N
12327	1549	3	11279	6	Lymphknoten-obere Extremität	\N
12328	1549	3	11280	7	Lymphknoten-untere Extremität	\N
12329	1549	3	11281	8	Lymphgefäße	\N
12330	1549	3	11282	9	Lymphe	\N
12331	1550	3	11284	1	Blutkörperchen, rotes (Erythrocyt)	\N
12332	1550	3	11285	2	Blutkörperchen, weißes (Leukocyt)	\N
12333	1550	3	11286	3	Monocyt	\N
12334	1550	3	11287	4	Plasma	\N
12335	1550	3	11288	5	Serum	\N
12336	1550	3	11289	6	Thrombus	\N
12337	1550	3	11290	7	Coagulum	\N
12338	1550	3	11291	8	Bluterguss	\N
12339	1550	3	11292	9	Embolus	\N
12340	1551	3	11294	1	Schweiß (Sudor)	\N
12341	1551	3	11295	2	Hauttalg (Sebum)	\N
12342	1551	3	11296	3	Ohrschmalz (Cerumen)	\N
12343	1552	3	11298	1	Reife Frauenmilch	\N
12344	1552	3	11299	2	Transitorische Milch	\N
12345	1552	3	11300	3	Vormilch (Kolostrum)	\N
12346	1553	3	11303	1	Schädelknochen (Ossa cranii)	\N
12347	1553	3	11304	2	Oberkiefer (Maxilla)	\N
12348	1553	3	11305	3	Unterkiefer (Mandibula)	\N
12349	1553	3	11306	4	Felsenbein (Os petrosum)	\N
12350	1553	3	11307	5	Wirbel (Vertebra)	\N
12351	1553	3	11308	6	Schultergürtelknochen (Cingulum membri superioris)	\N
12352	1553	3	11309	7	Rippen (Costae)	\N
12353	1553	3	11310	8	Beckenknochen (Cingulum membri inferioris)	\N
12354	1553	3	11311	9	Oberarmknochen (Humerus)	\N
12355	1553	3	11312	10	Oberschenkelknochen (Femur)	\N
12356	1554	3	11314	1	Wirbelgelenk (Articulatio intervertebralis)	\N
12357	1554	3	11315	2	Gelenke, Extremität obere   (Juncturae membri superioris liberi)	\N
12358	1554	3	11316	3	Gelenke, Extremität untere (Juncturae membri inferioris liberi)	\N
12359	1555	3	11318	1	Skelettmuskeln (Musculi skeleti)	\N
12360	1555	3	11319	2	Zungenmuskel (Musculus linguae)	\N
12361	1556	3	11321	1	Knorpel, hyaliner	\N
12362	1556	3	11322	2	Knorpel, faseriger	\N
12363	1556	3	11323	3	Knorpel, elastischer	\N
12364	1556	3	11324	4	Rippenknorpel (Cartilago costalis)	\N
12365	1556	3	11325	5	Bandscheibe (Diskus intervertebralis)	\N
12366	1556	3	11326	6	Gelenkzwischenscheibe (Meniscus articularis)	\N
12367	1564	3	11335	1	Nase (Nasus)	\N
12368	1564	3	11336	2	Kehldeckel (Epiglottis)	\N
12369	1564	3	11337	3	Stimmband (Plica vocalis)	\N
12370	1564	3	11338	4	Kehlkopf (Larynx)	\N
12371	1564	3	11339	5	Luftröhre (Trachea)	\N
12372	1564	3	11340	6	Bronchus	\N
12373	1565	3	11342	1	Lunge rechte	\N
12374	1565	3	11343	2	Lunge linke	\N
12375	1565	3	11344	3	Brustfell (Pleura)	\N
12376	1566	3	11346	1	Pericard	\N
12377	1566	3	11347	2	Herzmuskel (Myocard)	\N
12378	1566	3	11348	3	Endocard	\N
12379	1566	3	11349	4	Herzklappen (Valvae cordis)	\N
12380	1567	3	11351	1	Arterien (Arteriae)	\N
12381	1567	3	11352	2	Aorta	\N
12382	1567	3	11353	3	Venen (Venae)	\N
12383	1567	3	11354	4	Hohlvene (Vena cava)	\N
12384	1567	3	11355	5	Pfortader (Vena portae)	\N
12385	1568	3	11357	1	Mund (Os)	\N
12386	1568	3	11358	2	Lippen (Labia)	\N
12387	1568	3	11359	3	Oberlippe (Labium superius)	\N
12388	1568	3	11360	4	Unterlippe (Labium inferius)	\N
12389	1568	3	11361	5	Zunge (Lingua)	\N
12390	1569	3	11363	1	Zähne (Dentes)	\N
12391	1569	3	11364	2	Zähne bleibende (Dentes permanentes)	\N
12392	1569	3	11365	3	Milchzähne (Dentes desidui)	\N
12393	1569	3	11366	4	Zahnfleisch (Gingiva)	\N
12394	1570	3	11368	1	Ohrspeicheldrüse (Glandula parotis)	\N
12395	1570	3	11369	2	Unterzungendrüse (Glandula sublingualis)	\N
12396	1570	3	11370	3	Unterkieferdrüse (Glandula submandibularis)	\N
12397	1571	3	11372	1	Leberkapsel (Tunica fibrosa)	\N
12398	1571	3	11373	2	Leberparenchym	\N
12399	1571	3	11374	3	Lebergallengang	\N
12400	1571	3	11375	4	Gallenblase (Vesica fellea)	\N
12401	1571	3	11376	5	Gallengang (Ductus hepaticus)	\N
12402	1588	3	11394	1	Niere (Ren) (Nephros)	\N
12403	1588	3	11395	2	Nierenrinde (Cortex renalis)	\N
12404	1588	3	11396	3	Nierenmark (Medulla renalis)	\N
12405	1588	3	11397	4	Nierenbecken  (Pelvis renalis)	\N
12406	1592	3	11402	1	Vorsteherdrüse (Prostata)	\N
12407	1593	3	11404	1	Nebenhoden (Epididymis)	\N
12408	1593	3	11405	2	Samenleiter (Ductus deferens)	\N
12409	1596	3	11409	1	Uterusschleimhaut (Endometrium)	\N
12410	1597	3	11411	1	Eileiter (Tuba uterina)	\N
12411	1597	3	11412	2	Eileitertrichter (Salpinx)	\N
12412	1601	3	11417	1	Hirnanhangsdrüse (Hypophyse)	\N
12413	1601	3	11418	2	Zirbeldrüse (Epiphyse)	\N
12414	1601	3	11419	3	Schilddrüse (Glandula thyreoidea) (Kropf) (Struma)	\N
12415	1601	3	11420	4	Epithelkörperchen (Glandula parathyreoidea)	\N
12416	1601	3	11421	5	Bries (Thymus)	\N
12417	1601	3	11422	6	Pankreas-Inselorgan (Langerhans Inseln)	\N
12418	1602	3	11424	1	Nebennierenrinde	\N
12419	1602	3	11425	2	Nebennierenmark	\N
12420	1604	3	11428	1	Großhirn, graue Substanz (Substantia grisea)	\N
12421	1604	3	11429	2	Großhirn, weiße Substanz (Substantia alba)	\N
12422	1604	3	11430	3	Thalamus	\N
12423	1604	3	11431	4	Mittelhirn (Mesencephalon)	\N
12424	1604	3	11432	5	Kleinhirn (Cerebellum)	\N
12425	1604	3	11433	6	Spinalnerven (Nervi spinales)	\N
12426	1604	3	11434	7	Vegetatives Nervensystem (Systema nervosum autonomicum)	\N
12427	1604	3	11435	8	Rückenmark (Medulla)	\N
12428	1604	3	11436	9	Hirnnerven (Nervi craniales)	\N
12429	1605	3	11438	1	Glaskörper (Corpus vitreum)	\N
12430	1605	3	11439	2	Hornhaut (Cornea)	\N
12431	1605	3	11440	3	Linse (Lens)	\N
12432	1605	3	11441	4	Lid (Palpebra)	\N
12433	1605	3	11442	5	Bindehaut (Conjunctiva)	\N
12434	1606	3	11444	1	Ohrmuschel (Auricula)	\N
12435	1606	3	11445	2	Mittelohr (Auris media)	\N
12436	1606	3	11446	3	Gehörknöchelchen (Ossicula auditus)	\N
12437	1606	3	11447	4	Innenohr (Auris interna)	\N
12438	1607	3	11449	1	Oberhaut (Epidermis)	\N
12439	1607	3	11450	2	Lederhaut (Corium)	\N
12440	1607	3	11451	3	Hautdrüsen (Glandulae cutis)	\N
12441	1607	3	11452	4	Hyperpigmentierung	\N
12442	1607	3	11453	5	Leberfleck	\N
12443	1607	3	11454	6	Warze (Verruca)	\N
12444	1607	3	11455	7	Keloide	\N
12445	1608	3	11457	1	Haupthaar	\N
12446	1608	3	11458	2	Augenbraue (Supercilium)	\N
12447	1608	3	11459	3	Augenwimper (Cilie)	\N
12448	1608	3	11460	4	Bart (Barba)	\N
12449	1608	3	11461	5	Schamhaar (P. pubii)	\N
12450	1609	3	11463	1	Fingernagel	\N
12451	1609	3	11464	2	Zehennagel	\N
12452	1611	3	11467	1	Mamma, weibliche	\N
12453	1611	3	11468	2	Mamma, männliche	\N
12454	1611	3	11469	3	Brustwarze (Papilla mammae)	\N
12455	1611	3	11470	4	Mamma, laktierende	\N
12456	1612	3	11472	1	Lymphatisches Gewebe	\N
12457	1614	3	11475	1	Rotes Knochenmark (Medulla ossium rubra)	\N
12458	1615	3	11477	1	Milz, Pulpa weiße	\N
12459	1615	3	11478	2	Milz, Pulpa rote	\N
12460	1615	3	11479	3	Milz, Malpighi-Körperchen	\N
12461	1616	3	11481	1	Lymphknoten-Kopf	\N
12462	1616	3	11482	2	Lymphknoten-Hals	\N
12463	1616	3	11483	3	Lymphknoten-Thorax	\N
12464	1616	3	11484	4	Lymphknoten-Abdomen	\N
12465	1616	3	11485	5	Lymphknoten-Becken	\N
12466	1616	3	11486	6	Lymphknoten-obere Extremität	\N
12668	1108	3	13363	6	Eis Kirschgeschmack	\N
12467	1616	3	11487	7	Lymphknoten-untere Extremität	\N
12468	1616	3	11488	8	Lymphgefäße	\N
12469	1616	3	11489	9	Lymphe	\N
12470	1617	3	11491	1	Blutkörperchen, rotes (Erythrocyt)	\N
12471	1617	3	11492	2	Blutkörperchen, weißes (Leukocyt)	\N
12472	1617	3	11493	3	Monocyt	\N
12473	1618	3	11496	1	Schädelknochen (Ossa cranii)	\N
12474	1618	3	11497	2	Oberkiefer (Maxilla)	\N
12475	1618	3	11498	3	Unterkiefer (Mandibula)	\N
12476	1618	3	11499	4	Felsenbein (Os petrosum)	\N
12477	1618	3	11500	5	Wirbel (Vertebra)	\N
12478	1618	3	11501	6	Schultergürtelknochen (Cingulum membri superioris)	\N
12479	1618	3	11502	7	Rippen (Costae)	\N
12480	1618	3	11503	8	Beckenknochen (Cingulum membri inferioris)	\N
12481	1618	3	11504	9	Oberarmknochen (Humerus)	\N
12482	1618	3	11505	10	Oberschenkelknochen (Femur)	\N
12483	1619	3	11507	1	Wirbelgelenk (Articulatio intervertebralis)	\N
12484	1619	3	11508	2	Gelenke, Extremität obere (Juncturae membri superioris liberi)	\N
12485	1619	3	11509	3	Gelenke, Extremität untere (Juncturae membri inferioris liberi)	\N
12486	1620	3	11511	1	Skelettmuskeln (Musculi skeleti)	\N
12487	1620	3	11512	2	Zungenmuskel (Musculus linguae)	\N
12488	1621	3	11514	1	Knorpel, hyaliner	\N
12489	1621	3	11515	2	Knorpel, faseriger	\N
12490	1621	3	11516	3	Knorpel, elastischer	\N
12491	1621	3	11517	4	Rippenknorpel (Cartilago costalis)	\N
12492	1621	3	11518	5	Bandscheibe (Diskus intervertebralis)	\N
12493	1621	3	11519	6	Gelenkzwischenscheibe (Meniscus articularis)	\N
12494	1629	3	11528	1	Nase (Nasus)	\N
12495	1629	3	11529	2	Kehldeckel (Epiglottis)	\N
12496	1629	3	11530	3	Stimmband (Plica vocalis)	\N
12497	1629	3	11531	4	Kehlkopf (Larynx)	\N
12498	1629	3	11532	5	Luftröhre (Trachea)	\N
12499	1629	3	11533	6	Bronchus	\N
12500	1630	3	11535	1	Lunge rechte	\N
12501	1630	3	11536	2	Lunge linke	\N
12502	1630	3	11537	3	Brustfell (Pleura)	\N
12503	1631	3	11539	1	Pericard	\N
12504	1631	3	11540	2	Herzmuskel (Myocard)	\N
12505	1631	3	11541	3	Endocard	\N
12506	1631	3	11542	4	Herzklappen (Valvae cordis)	\N
12507	1632	3	11544	1	Arterien (Arteriae)	\N
12508	1632	3	11545	2	Aorta	\N
12509	1632	3	11546	3	Venen (Venae)	\N
12510	1632	3	11547	4	Hohlvene (Vena cava)	\N
12511	1632	3	11548	5	Pfortader (Vena portae)	\N
12512	1633	3	11550	1	Mund (Os)	\N
12513	1633	3	11551	2	Lippen (Labia)	\N
12514	1633	3	11552	3	Oberlippe (Labium superius)	\N
12515	1633	3	11553	4	Unterlippe (Labium inferius)	\N
12516	1633	3	11554	5	Zunge (Lingua)	\N
12517	1634	3	11556	1	Zähne (Dentes)	\N
12518	1634	3	11557	2	Zähne bleibende (Dentes permanentes)	\N
12519	1634	3	11558	3	Milchzähne (Dentes desidui)	\N
12520	1634	3	11559	4	Zahnfleisch (Gingiva)	\N
12521	1635	3	11561	1	Ohrspeicheldrüse (Glandula parotis)	\N
12522	1635	3	11562	2	Unterzungendrüse (Glandula sublingualis)	\N
12523	1635	3	11563	3	Unterkieferdrüse (Glandula submandibularis)	\N
12524	1636	3	11565	1	Leberkapsel (Tunica fibrosa)	\N
12525	1636	3	11566	2	Leberparenchym	\N
12526	1636	3	11567	3	Lebergallengang	\N
12527	1636	3	11568	4	Gallenblase (Vesica fellea)	\N
12528	1636	3	11569	5	Gallengang (Ductus hepaticus)	\N
12529	1653	3	11587	1	Niere (Ren) (Nephros)	\N
12530	1653	3	11588	2	Nierenrinde (Cortex renalis)	\N
12531	1653	3	11589	3	Nierenmark (Medulla renalis)	\N
12532	1653	3	11590	4	Nierenbecken  (Pelvis renalis)	\N
12533	1657	3	11595	1	Vorsteherdrüse (Prostata)	\N
12534	1658	3	11597	1	Nebenhoden (Epididymis)	\N
12535	1658	3	11598	2	Samenleiter (Ductus deferens)	\N
12536	1661	3	11602	1	Uterusschleimhaut (Endometrium)	\N
12537	1662	3	11604	1	Eileiter (Tuba uterina)	\N
12538	1666	3	11609	1	Hirnanhangsdrüse (Hypophyse)	\N
12539	1666	3	11610	2	Zirbeldrüse (Epiphyse)	\N
12540	1666	3	11611	3	Schilddrüse (Glandula thyreoidea)	\N
12541	1666	3	11612	4	Epithelkörperchen (Glandula parathyreoidea)	\N
12542	1666	3	11613	5	Bries (Thymus)	\N
12543	1666	3	11614	6	Pankreas-Inselorgan (Langerhans Inseln)	\N
12544	1667	3	11616	1	Nebennierenrinde	\N
12545	1667	3	11617	2	Nebennierenmark	\N
12546	1669	3	11620	1	Großhirn, graue Substanz (Substantia grisea)	\N
12547	1669	3	11621	2	Großhirn, weiße Substanz (Substantia alba)	\N
12548	1669	3	11622	3	Thalamus	\N
12549	1669	3	11623	4	Mittelhirn (Mesencephalon)	\N
12550	1669	3	11624	5	Kleinhirn (Cerebellum)	\N
12551	1669	3	11625	6	Spinalnerven (Nervi spinales)	\N
12552	1669	3	11626	7	Vegetatives Nervensystem (Systema nervosum autonomicum)	\N
12553	1669	3	11627	8	Rückenmark (Medulla)	\N
12554	1669	3	11628	9	Hirnnerven (Nervi craniales)	\N
12555	1670	3	11630	1	Glaskörper (Corpus vitreum)	\N
12556	1670	3	11631	2	Hornhaut (Cornea)	\N
12557	1670	3	11632	3	Linse (Lens)	\N
12558	1670	3	11633	4	Lid (Palpebra)	\N
12559	1670	3	11634	5	Bindehaut (Conjunctiva)	\N
12560	1671	3	11636	1	Ohrmuschel (Auricula)	\N
12561	1671	3	11637	2	Mittelohr (Auris media)	\N
12562	1671	3	11638	3	Gehörknöchelchen (Ossicula auditus)	\N
12563	1671	3	11639	4	Innenohr (Auris interna)	\N
12564	1672	3	11641	1	Oberhaut (Epidermis)	\N
12565	1672	3	11642	2	Lederhaut (Corium)	\N
12566	1672	3	11643	3	Hautdrüsen (Glandulae cutis)	\N
12567	1673	3	11645	1	Haupthaar	\N
12568	1673	3	11646	2	Augenbraue (Supercilium)	\N
12569	1673	3	11647	3	Augenwimper (Cilie)	\N
12570	1673	3	11648	4	Bart (Barba)	\N
12571	1673	3	11649	5	Schamhaar (P.pubii)	\N
12572	1674	3	11651	1	Fingernagel	\N
12573	1674	3	11652	2	Zehennagel	\N
12574	1676	3	11655	1	Mamma, weibliche	\N
12575	1676	3	11656	2	Mamma, männliche	\N
12576	1676	3	11657	3	Brustwarze (Papilla mammae)	\N
12577	1676	3	11658	4	Mamma, laktierende	\N
12578	1677	3	11660	1	Lymphatisches Gewebe	\N
12579	1679	3	11663	1	Rotes Knochenmark (Medulla ossium rubra)	\N
12580	1680	3	11665	1	Milz, Pulpa weiße	\N
12581	1680	3	11666	2	Milz, Pulpa rote	\N
12582	1680	3	11667	3	Milz, Malpighi-Körperchen	\N
12583	1681	3	11669	1	Lymphknoten-Kopf	\N
12584	1681	3	11670	2	Lymphknoten-Hals	\N
12585	1681	3	11671	3	Lymphknoten-Thorax	\N
12586	1681	3	11672	4	Lymphknoten-Abdomen	\N
12587	1681	3	11673	5	Lymphknoten-Becken	\N
12588	1681	3	11674	6	Lymphknoten-obere Extremität	\N
12589	1681	3	11675	7	Lymphknoten-untere Extremität	\N
12590	1681	3	11676	8	Lymphgefäße	\N
12591	1681	3	11677	9	Lymphe	\N
12592	1682	3	11679	1	Blutkörperchen, rotes (Erythrocyt)	\N
12593	1682	3	11680	2	Blutkörperchen, weißes (Leukocyt)	\N
12594	1682	3	11681	3	Monocyt	\N
12595	1197	3	13178	1	Säuglingsmilchnahrung PRE	\N
12596	1197	3	13179	2	Säuglingsanfangsnahrung PRE	\N
12597	1197	3	13180	3	Säuglingsanfangsnahrung PRE auf Proteinhydrolysatbasis	\N
12598	1197	3	13187	4	Säuglingsanfangsnahrung PRE hypoallergen	\N
12599	1197	3	13186	5	Säuglingsmilchnahrung	\N
12600	1197	3	13185	6	Säuglingsanfangsnahrung	\N
12601	1197	3	13184	7	Säuglingsanfangsnahrung auf Proteinhydrolysatbasis	\N
12602	1197	3	13183	8	Säuglingsanfangsnahrung hypoallergen	\N
12603	1197	3	13182	9	Säuglingsanfangsnahrung aus Sojaprotein in einer Mischung mit Kuhmilchprotein	\N
12604	1197	3	13181	10	Säuglingsanfangsnahrung nur aus Sojaprotein	\N
12605	1198	3	13188	1	Folgemilch für Säuglinge	\N
12606	1198	3	13189	2	Folgenahrung für Säuglinge	\N
12607	1198	3	13190	3	Folgenahrung auf Proteinhydrolysatbasis für Säuglinge	\N
12608	1198	3	13191	4	Folgenahrung hypoallergen für Säuglinge	\N
12609	1198	3	13192	5	Folgenahrung aus Sojaprotein in einer Mischung mit Kuhmilchprotein für Säuglinge	\N
12610	1198	3	13193	6	Folgenahrung nur aus Sojaprotein für Säuglinge	\N
12611	1199	3	13194	1	Getreidebrei mit Milch zuzubereiten für Säuglinge und Kleinkinder	\N
12612	1199	3	13195	2	Getreidebrei mit Milch und anderen Zutaten zuzubereiten für Säuglinge und Kleinkinder	\N
12613	1199	3	13196	3	Getreidebrei glutenfrei mit Milch zuzubereiten für Säuglinge und Kleinkinder	\N
12614	1199	3	13197	4	Getreidebrei glutenfrei mit Milch und anderen Zutaten zuzubereiten für Säuglinge und Kleinkinder	\N
12615	1199	3	13198	5	Getreidebrei mit einem zugesetzten proteinreichen LM mit Wasser zuzubereiten oder verzehrsfertig	\N
12616	1199	3	13199	6	Getreidebrei glutenfrei mit einem zugesetzten proteinreichen LM mit Wasser zuzuber. oder verzehrsf.	\N
12617	1199	3	13200	7	Getreidebrei hypoallergen, mit einem zugesetzten proteinreichen LM mit Wasser zuzuber. o. verzehrsf	\N
12618	1199	3	13201	8	Teigwaren für Säuglinge und Kleinkinder	\N
12619	1199	3	13202	9	Zwieback oder Kekse für Säuglinge und Kleinkinder	\N
12620	1200	3	13203	1	Mahlzeit mit Kalbfleisch für Säuglinge	\N
12621	1200	3	13204	2	Mahlzeit mit Rindfleisch für Säuglinge	\N
12622	1200	3	13205	3	Mahlzeit mit Schweinefleisch für Säuglinge	\N
12623	1200	3	13206	4	Mahlzeit mit Innereien für Säuglinge	\N
12624	1200	3	13207	5	Mahlzeit mit Geflügel für Säuglinge	\N
12625	1200	3	13208	6	Mahlzeit mit Fisch für Säuglinge	\N
12626	1200	3	13209	7	Mahlzeit mit Getreideanteil für Säuglinge	\N
12627	1200	3	13210	8	Mahlzeit mit Anteil an sonstigen LM für Säuglinge	\N
12628	1200	3	13211	20	Mahlzeit mit Kalbfleisch für Kleinkinder	\N
12629	1200	3	13212	21	Mahlzeit mit Rindfleisch für Kleinkinder	\N
12630	1200	3	13213	22	Mahlzeit mit Schweinefleisch für Kleinkinder	\N
12631	1200	3	13214	23	Mahlzeit mit Innereien für Kleinkinder	\N
12632	1200	3	13215	24	Mahlzeit mit Geflügel für Kleinkinder	\N
12633	1200	3	13216	25	Mahlzeit mit Fisch für Kleinkinder	\N
12634	1200	3	13217	26	Mahlzeit mit Getreideanteil für Kleinkinder	\N
12635	1200	3	13218	27	Mahlzeit mit Anteil an sonstigen LM für Kleinkinder	\N
12636	1201	3	13219	1	Fruchtsaft für Säuglinge und Kleinkinder	\N
12637	1201	3	13220	2	Fruchtnektar für Säuglinge und Kleinkinder	\N
12638	1201	3	13221	3	Gemüsesaft für Säuglinge und Kleinkinder	\N
12639	1201	3	13222	4	Gemüsenektar für Säuglinge und Kleinkinder	\N
12640	1201	3	13223	5	Obst- und/oder gemüsehaltiges Getränk für Säuglinge und Kleinkinder	\N
12641	1201	3	13224	6	Obstzubereitung für Säuglinge und Kleinkinder	\N
12642	1201	3	13225	7	Gemüsezubereitung für Säuglinge und Kleinkinder	\N
12643	1201	3	13226	8	Zubereitung mit Obst- und Gemüseanteil für Säuglinge und Kleinkinder	\N
12644	1201	3	13227	9	Tee mit Saft für Säuglinge und Kleinkinder	\N
12645	1203	3	13228	1	Fleischzubereitung für Säuglinge und Kleinkinder	\N
12646	1203	3	13229	2	Dessert und Pudding für Säuglinge und Kleinkinder	\N
12647	1203	3	13230	3	Süßspeise mit Milcherzeugnissen für Säuglinge und Kleinkinder	\N
12648	1203	3	14001	4	Milch für die besondere Ernährung von Kleinkindern	\N
12649	378	3	13275	1	Carpaccio vom Rind	\N
12650	378	3	13276	2	Carpaccio vom Kalb	\N
12651	378	3	13277	3	Carpaccio vom Lamm	\N
12652	378	3	13278	4	Carpaccio vom Hirsch	\N
12653	378	3	13279	99	Carpaccio vom Fleisch gemischt	\N
12654	526	3	13289	1	Carpaccio vom Fisch	\N
12655	526	3	13290	2	Carpaccio vom Lachs	\N
12656	526	3	13291	3	Sashimi	\N
12657	526	3	13292	4	Lachstatar	\N
12658	526	3	13293	99	Carpaccio vom Fisch gemischt	\N
12659	917	3	13330	1	Apfelsaftschorle	\N
12660	917	3	13331	2	Traubensaftschorle weiß	\N
12661	917	3	13332	3	Traubensaftschorle rot	\N
12662	917	3	13333	4	Orangensaftschorle	\N
12663	1108	3	13358	1	Eis Vanille	\N
12664	1108	3	13359	2	Eis Schokolade	\N
12665	1108	3	13360	3	Eis Kaffee	\N
12666	1108	3	13361	4	Eis Nuss	\N
12667	1108	3	13362	5	Eis Erdbeergeschmack	\N
12669	1108	3	13364	7	Eis Himbeergeschmack	\N
12670	1108	3	13365	8	Eis Zitronengeschmack	\N
12671	1108	3	13366	9	Eis Pistaziengeschmack	\N
12672	1108	3	13367	10	Eis Bananengeschmack	\N
12673	1108	3	13368	11	Eis Heidelbeergeschmack	\N
12674	1108	3	13369	12	Eis Ananasgeschmack	\N
12675	1108	3	13370	13	Eis Orangengeschmack	\N
12676	1108	3	13371	14	Eis Apfelgeschmack	\N
12677	1108	3	13372	15	Eis Malaga	\N
12678	1108	3	13373	16	Eis Stracciatella	\N
12679	1108	3	13374	17	Eis Kastanie	\N
12680	1108	3	13375	18	Eis Eierlikör	\N
12681	1108	3	13376	19	Eis Nougat	\N
12682	1108	3	13377	20	Eis Mokka	\N
12683	1108	3	13378	21	Eis Kokos	\N
12684	1108	3	13379	22	Eis Pfirsich	\N
12685	1108	3	13380	23	Eis Marzipan	\N
12686	1108	3	13381	24	Eis Pfefferminz	\N
12687	1108	3	13382	25	Eis Cappuccino	\N
12688	1108	3	13383	26	Eis Haselnuss	\N
12689	1108	3	13384	27	Eis Walnuss	\N
12690	1108	3	13385	28	Eis Amaretto	\N
12691	1108	3	13386	29	Eis Tiramisu	\N
12692	1108	3	13387	30	Eis Karamel	\N
12693	1108	3	13388	31	Eis Trüffel	\N
12694	1108	3	13389	32	Eis Krokant	\N
12695	1108	3	13390	33	Eis Maracujageschmack	\N
12696	1108	3	13391	34	Eis Waldmeistergeschmack	\N
12697	1108	3	13392	35	Eis Kiwi	\N
12698	1108	3	13393	36	Eis Zabaione	\N
12699	1108	3	13394	37	Eis Zimt	\N
12700	1108	3	13395	38	Eis Fruchtgeschmack	\N
12701	1108	3	13396	39	Eis Marone	\N
12702	1108	3	13397	40	Eis Joghurt	\N
12703	1108	3	13398	41	Eis Mandel	\N
12704	1108	3	13399	42	Eis Sahne-Kirsch	\N
12705	1108	3	13400	43	Eis Nuss-Nougatkrem	\N
12706	1108	3	13401	44	Eis Amarena-Kirsch	\N
12707	1108	3	13402	45	Eis Tartufo	\N
12708	1108	3	13403	46	Eis Saure-Sahne-Kirsch	\N
12709	1108	3	13404	47	Eis Raffaello	\N
12710	1108	3	13405	48	Eis Mozartkugel	\N
12711	1108	3	13406	49	Eis Mascarpone	\N
12712	1108	3	13407	50	Eis After Eight	\N
12713	1108	3	13408	51	Eis Vanillegeschmack	\N
12714	1108	3	13409	52	Eis mit Blaufärbung	\N
12715	1108	3	13410	53	Eis gefärbt	\N
12716	1108	3	13411	99	Eismischung	\N
12717	1205	3	13417	1	gestrichen; Backware/Backmischung für Diabetiker	\N
12718	1205	3	13423	7	gestrichen: Konfitüre/Fruchtaufstrich für Diabetiker	\N
12719	1206	3	13434	1	Fleischerzeugnis natriumarm/streng natriumarm	\N
12720	1206	3	13435	2	Fischerzeugnis natriumarm/streng natriumarm	\N
12721	1206	3	13436	3	Käse/Milchprodukt natriumarm/streng natriumarm	\N
12722	1206	3	13437	4	Brotaufstrich natriumarm/streng natriumarm	\N
12723	1206	3	13438	5	Suppe/Brühe/Soße natriumarm/streng natriumarm	\N
12724	1206	3	13439	6	Kochsalzersatz/Würzmittel natriumarm/streng natriumarm	\N
12725	1206	3	13440	7	Backware natriumarm/streng natriumarm	\N
12726	1206	3	13441	8	Streichfett natriumarm/streng natriumarm	\N
12727	1207	3	13442	1	Vollständig bilanzierte Diät ausgen. für Säuglinge	\N
12728	1207	3	13443	2	Vollständig bilanzierte Diät Standardzusammensetzung ausgen. für Säuglinge	\N
12729	1207	3	13445	4	Vollständig bilanzierte Diät bei Niereninsuffizienz ausgen. für Säuglinge	\N
12730	1207	3	13446	10	Vollständig bilanzierte Diät für Säuglinge	\N
12731	1207	3	13447	11	Vollständig bilanzierte Diät für Säuglinge bei Durchfallerkrankungen Heilnahrung	\N
12732	1207	3	13448	12	Vollständig bilanzierte Diät für Säuglinge bei gestörtem Aminosäurenstoffwechsel	\N
12733	1207	3	13449	13	Vollständig bilanzierte Diät für Säuglinge bei Zöliakie	\N
12734	1207	3	13450	14	Vollständig bilanzierte Diät für Säuglinge bei Kuhmilch-Unverträglichkeit	\N
12735	1207	3	13451	15	Vollständig bilanzierte Diät für Säuglinge hypoallergen	\N
12736	1207	3	13452	20	Ergänzende bilanzierte Diät	\N
12737	1207	3	13453	21	Ergänzende bilanzierte Diät bei Resorptions-/Stoffwechselstörungen	\N
12738	1207	3	13454	22	Ergänzende bilanzierte Diät bei Fettstoffwechselstörungen	\N
12739	1207	3	13455	23	Ergänzende bilanzierte Diät bei gestörtem Aminosäurenstoffwechsel	\N
12740	1207	3	13456	24	Ergänzende bilanzierte Diät hochkalorische Aufbaunahrung	\N
12741	1208	3	13457	1	Fertiggericht für kalorienarme Ernährung zur Gewichtsverringerung Tagesration	\N
12742	1208	3	13458	2	Fertiggericht für kalorienarme Ernährung zur Gewichtsverringerung Ersatz einer Mahlzeit	\N
12743	1208	3	13459	3	Pulvernahrung für die Zubereitung kalorienarmer Ernährung zur Gewichtsverringerung Tagesration	\N
12744	1208	3	13460	4	Pulvernahrung für die Zubereitung kalorienarmer Ernährung zur Gewichtsverring. Ersatz einer Mahlzeit	\N
12745	1209	3	13461	1	Backware/Backmischung glutenfrei	\N
12746	1209	3	13462	2	Teigware glutenfrei	\N
12747	1209	3	13463	3	Sonstiges stärkereiches Lebensmittel glutenfrei	\N
12748	1210	3	13464	1	Produkt zur Energiebereitstellung für intensive Muskelanstrengungen vor allem für Sportler	\N
12749	1210	3	13465	2	Eiweißkonzentrat incl. Proteine/Proteinhydrolisate/Aminosäurenmischung für intensive Muskelanstreng.	\N
12750	1210	3	13466	3	Rehydratations-/Mineralstoffgetränk incl. -pulver auch mit Vitaminzusatz  f. intens. Muskelanstreng.	\N
12751	1210	3	13467	4	Kombinationspräparat aus 492601-492603 für intensive Muskelanstrengungen vor allem für Sportler	\N
12752	1210	3	13468	10	Nahrungsergänzung für intensive Muskelanstrengungen vor allem für Sportler	\N
12753	1210	3	13469	11	Vitamin-/Mineralstoffpräparat für intensive Muskelanstrengungen vor allem für Sportler	\N
12754	1210	3	13470	12	Kreatinpräparat	\N
12755	1210	3	13471	13	Carnitinpräparat	\N
12756	1211	3	13472	1	Diätetisches Fett/Öl/Streichfett mit MCT (mittelkettige Triglyceride)	\N
12757	1211	3	13473	2	Diätetisches Fett/Öl/Streichfett mit erhöhtem Anteil essentieller Fettsäuren	\N
12758	1211	3	13474	3	Diätetisches Fett/Öl/Streichfett/sonstige Fettmodifikation	\N
12759	1211	3	13475	4	Diätetisches Fett/Öl/Streichfett mit Phytosterinen	\N
12760	1211	3	13476	5	Fleischerzeugnis mit modifiziertem Fettzusatz	\N
12761	1211	3	13477	6	Milcherzeugnis mit modifiziertem Fettzusatz	\N
12762	1211	3	13478	7	Diätetisches Fett/ÖL/Streichfett mit Phytosterinen/Phytostanolen	\N
12763	1212	3	13479	1	Diätetisches Lebensmittel mit Zusatz von Eisen	\N
12764	1212	3	13480	2	Diätetisches Lebensmittel frei von Milch/Milchbestandteilen	\N
12765	1212	3	13481	3	Diätetisches Lebensmittel eiweißreduziert	\N
12766	1212	3	13482	4	Diätetisches Lebensmittel eiweißangereichert	\N
12767	1222	3	13497	1	Carnitinhaltiges Nahrungsergänzungsmittel	\N
12768	1222	3	13498	2	Q10-Präparat	\N
12769	1225	3	13709	1	Vitamin-E-Präparat	\N
12770	1225	3	13710	2	Vitamin-A-Präparat	\N
12771	1225	3	13711	3	Betacarotinpräparat	\N
12772	1226	3	13499	1	Präparat mit essentiellen Fettsäuren	\N
12773	1226	3	13500	2	Omega-3-ölpräparat	\N
12774	1226	3	13501	3	Gammalinolensäurepräparat	\N
12775	1226	3	13502	4	CLA-Präparat	\N
12776	1226	3	13712	5	Fischölpräparat	\N
12777	1226	3	13713	6	Nachtkerzenölpräparat	\N
12778	1226	3	13714	7	Distelölpräparat	\N
12779	1226	3	13715	8	Borretschölpräparat	\N
12780	1226	3	13716	9	Schwarzkümmelölpräparat	\N
12781	1234	3	13503	10	Mikroalgenpräpat (z.B. Spirulina/Chlorella/AFA)	\N
12782	1234	3	13504	20	Meeresalgenpräparat jodarm	\N
12783	1235	3	13505	1	Gelee Royal-Präparat Nahrungsergänzungsmittel	\N
12784	1235	3	13506	2	Pollen-Präparat Nahrungsergänzungsmittel	\N
12785	1237	3	13507	10	Gelatinepräparate	\N
12786	1331	3	13518	1	Zentralversorgung  nicht aufbereitet	\N
12787	1331	3	13519	2	Zentralversorgung aufbereitet	\N
12788	478	3	13703	5	Kaiserbarsch (Beryx decadactylus) Seefisch	\N
12789	918	3	13707	1	Aloe Verasaft	\N
12790	919	3	13708	1	Nonisaft	\N
12791	729	3	14016	1	Papageienschnabel; Kathurumurunga (Sesbania grandiflora (L.) Pers.)	\N
12792	729	3	14017	2	Gotukola Indischer Wassernabel (Centella asiatica (L.) Urban)	\N
12793	729	3	14018	3	Mukunuwenna (Alternanthera sessilis (L.))	\N
12794	729	3	14019	4	Eiskraut Sodapflanze (Mesembryanthemum crystallinum (L.))	\N
12795	729	3	14105	5	Wasserspinat Kangkung (Ipomea aquatica)	\N
12796	729	3	14205	6	Neuseeländer Spinat (Tetragonia tetragonioides) 	\N
12797	760	3	14075	1	Rotalge; Nori; Seegras (Porphyra ssp.) vor- und zubereitet	\N
12798	760	3	14076	2	Grünalge; Aonori (Monostroma spp. und Enteromorpha spp.) vor- und zubereitet	\N
12799	760	3	14077	3	Braunalge; Kombu; Haidi; Seekohl (Laminaria japonica und Laminaria ssp.) vor- und zubereitet	\N
12800	760	3	14078	4	Braunalge; Wakame (Undaria pinnatifida) vor- und zubereitet	\N
12801	760	3	14079	5	Braunalge; Hiziki; Hijki (Hizikia fusiforme) vor- und zubereitet	\N
12802	760	3	14080	6	Rotalge; Dulse (Palmaria palmate) vor- und zubereitet	\N
12803	760	3	14081	7	Braunalge; Sarumen (Alaria esculenta) vor- und zubereitet	\N
12804	760	3	14082	8	Braunalge; Arame (Eisenia bicyclis) vor- und zubereitet	\N
12805	760	3	14083	9	Braunalge; Meeresspaghetti (Himanthalia elongata) vor- und zubereitet	\N
12806	760	3	14084	10	Braunalge (Sargassum spp.) vor- und zubereitet	\N
12807	760	3	14085	11	Grünalge; Meersalat (Ulva lactuca) vor- und zubereitet	\N
12808	760	3	14199	50	Kaviarersatz aus Algen/Seetang	\N
12809	760	3	14086	99	Algen gemischt vor- und zubereitet	\N
12810	642	3	14104	1	Gebäck aus Quark-Ölteig	\N
12811	2507	3	14142	99	Kein Erzeugnis nach LMBG/LFGB	\N
12812	264	3	14163	1	Camembert paniert auch tiefgefroren	\N
12813	264	3	14164	2	Fetakäse paniert auch tiefgefroren	\N
12814	1315	3	14297	1	Weizenfaser	\N
12815	1315	3	14298	2	Haferfaser	\N
12816	1315	3	14299	3	Bambusfaser	\N
12817	1315	3	14300	4	Citrusfaser	\N
12818	1315	3	14301	5	Apfelfaser	\N
12819	1315	3	14302	6	Kartoffelfaser	\N
12820	1315	3	14303	7	Erbsenfaser	\N
12821	1315	3	14304	8	Karottenfaser	\N
12822	87	3	12479	0	Tierart	\N
12823	87	3	14158	1	Schwein	\N
12824	87	3	14159	2	Geflügel, Lamm, Kalb	\N
12825	90	3	12479	0	Tierart	\N
12826	90	3	14158	1	Schwein	\N
12827	90	3	14159	2	Geflügel, Lamm, Kalb	\N
12828	91	3	12479	0	Tierart	\N
12829	91	3	14158	1	Schwein	\N
12830	91	3	14159	2	Geflügel, Lamm, Kalb	\N
12831	102	3	11950	0	Teil	\N
12832	102	3	11951	1	Blatt	\N
12833	102	3	11952	2	Blüte	\N
12834	102	3	11953	4	Wurzel	\N
12835	102	3	11954	5	Holz/Stengel	\N
12836	102	3	11955	6	Blütenpollen	\N
12837	102	3	11956	8	Nadel	\N
12838	103	3	11950	0	Teil	\N
12839	103	3	11951	1	Blatt	\N
12840	103	3	11952	2	Blüte	\N
12841	103	3	11975	3	Rinde	\N
12842	103	3	11953	4	Wurzel	\N
12843	103	3	11954	5	Holz/Stengel	\N
12844	103	3	11955	6	Blütenpollen	\N
12845	103	3	11976	7	Frucht	nur bei Eichel, Kastanie und Buchecker
12846	103	3	11956	8	Nadel	\N
12847	104	3	11950	0	Teil	\N
12848	104	3	11951	1	Blatt	\N
12849	104	3	11952	2	Blüte	\N
12850	104	3	11975	3	Rinde	\N
12851	104	3	11953	4	Wurzel	\N
12852	104	3	11954	5	Holz/Stengel	\N
12853	104	3	11955	6	Blütenpollen	\N
12854	104	3	11956	8	Nadel	\N
12855	105	3	11950	0	Teil	\N
12856	105	3	11951	1	Blatt	\N
12857	105	3	11952	2	Blüte	\N
12858	105	3	11975	3	Rinde	\N
12859	105	3	11953	4	Wurzel	\N
12860	105	3	11954	5	Holz/Stengel	\N
12861	105	3	11955	6	Blütenpollen	\N
12862	105	3	11956	8	Nadel	\N
12863	108	3	12024	0	Bodentyp	\N
12864	108	3	12025	1	Syrosem	On
12865	108	3	12026	2	Lockersyrosem	OL, OLn
12866	108	3	12027	3	Ranker	Nn und O-N
12867	108	3	12028	4	Braunerde-Ranker	B-N; inkl. N-B
12868	108	3	12029	5	Podsol-Ranker	P-N
12869	108	3	12030	6	Regesol	Qn
12870	108	3	12031	7	Braunerde-Regesol	B-Q; inkl. Q-B
12871	108	3	12032	8	Podsol-Regosol	P-Q
12872	108	3	12033	9	Rendzina	Rn, Ru, Rs, Rb, inkl. O-R, G-R
12873	108	3	12034	10	Alpine Rendzinen	Ra, Rm, Rp, Rt
12874	108	3	12035	11	Braunerde-Rendzina	B-R; inkl. R-B
12875	108	3	12036	12	Terra fusca-Rendzina	CF-R; inkl. CR-R
12876	108	3	12037	13	Pararendzina	Zn, Zs; inkl. O-Z
12877	108	3	12038	14	Braunerde-Pararendzina	B-Z; inkl. Z-B
12878	108	3	12039	15	Hydromorphe Pararendzinen	S-Z, G-Z
12879	108	3	12040	16	Tschernoseme (Schwarzerden)	T ohne Differenzierung
12880	108	3	12041	17	Pelesol	Dn; inkl. B-D und X-D
12881	108	3	12042	18	Kalkhaltiger Pelosol	Dc; inkl. Z-D
12882	108	3	12043	19	Pseudogley-Pelosol	S-D
12883	108	3	12044	20	Ranker-Pelesol	N-D; inkl. Q-D
12884	108	3	12045	21	Braunerde	Bn, Bl, Bf
12885	108	3	12046	22	Pelesol-Braunerde	D-B
12886	108	3	12047	23	Parabraunerde-Braunerde	L-B; inkl. IB
12887	108	3	12048	24	Podsol-Braunerde	P-B
12888	108	3	12049	25	Pseudogley-Braunerde	S-B
12889	108	3	12050	26	Parabraunerde	Ln, BF=Fahlerde, Lb, T-L
12890	108	3	12051	27	Podsol-Parabraunerde	P-L; inkl. L-P
12891	108	3	12052	28	Pseudogley-Parabraunerde	S-L; ink. L-S
12892	108	3	12053	29	Podsol	Pn, Pe, Ph
12893	108	3	12054	30	Braunerde-Podsol	B-P
12894	108	3	12055	31	Pseudogley-Podsol	S-P
12895	108	3	12056	32	Moor-Podsol	H-P
12896	108	3	12057	33	Staupodsol	PSn, PSb
12897	108	3	12058	34	Terra fusca	CFn, CFc; inkl. CR (Terrae calcis-Bezeichnung von 1986 entnommenen Bodenproben wird jetzt zu 34 Terra fusca)
12898	108	3	12059	35	Braunerde-Terra fusca	B-CF
12899	108	3	12060	36	Pseudogley-Terra fusca	S-CF; inkl. CR-S, CF-S
12900	108	3	12061	37	Plastosole	VG, VB, VR
12901	108	3	12062	38	Latosole	WR, WG, WP
12902	108	3	12063	39	Braunerde-Pseudogley	B-S
12903	108	3	12064	40	Pseudogley	Sn, Sc, Sg, Sa
12904	108	3	12065	41	Podsol-Pseudogley	P-S
12905	108	3	12066	42	Haftnässepseudogley	SH ohne Differenzierung
12906	108	3	12067	43	Stagnogly	SS ohne Differenzierung
12907	108	3	12068	44	Kolluvium	Kn
12908	108	3	12069	45	Pseudogley-Kolluvium	S-K; inkl. S-KA
12909	108	3	12070	46	Gley-Kolluvium	G-K; inkl. G-KA
12910	108	3	12071	47	Aeolium	KAn, P-KA
12911	108	3	12072	48	Allochthoner Brauner Auenboden	Am, Ac; inkl. AD-A
12912	108	3	12073	49	Auenspeudogley-Brauner Auenboden	AS-A
12913	108	3	12074	50	Auengley-Brauner Auenboden	AG-A
12914	108	3	12075	51	Auensilikatrohboden (Rambia)	AO
12915	108	3	12076	52	Auenkarbonatrohboden (Kalkrambla)	AC
12916	108	3	12077	53	Auenregosol (Paternia)	AQ
12917	108	3	12078	54	Auenrendzina (Kalkpaternia)	ARn; inkl. AZn
12918	108	3	12079	55	Humusreiche Auenrendzina	ARh, inkl. AZh
12919	108	3	12080	56	Tschernitza (Schwarzerde-Auenb.)	AT
12920	108	3	12081	57	Auenbraunerde	AB ohne Differenzierung
12921	108	3	12082	58	Auenparabraunerde	AL ohne Differenzierung
12922	108	3	12083	59	Auenpodsol	AP
12923	108	3	12084	60	Auenpseudogley	AS ohne Differenzierung
12924	108	3	12085	61	Auenpelosol	AD
12925	108	3	12086	62	Auengley	AG ohne Differenzierung
12926	108	3	12087	63	Gley	Gn; inkl. Gg
12927	108	3	12088	64	Eisenreicher Gley	Ge; inkl. Go
12928	108	3	12089	65	Humusgley	Gh
12929	108	3	12090	66	Kalkhaltiger Gley	Gc; inkl. Gk
12930	108	3	12091	67	Rendzina-Gley	R-G; inkl. G-R
12931	108	3	12092	68	Regosol-Gley	Q-G
12932	108	3	12093	69	Pelosol-Gley	D-G; inkl. G-D
12933	108	3	12094	70	Braunerde-Gley	B-G; inkl. G-B
12934	108	3	12095	71	Parabraunerde-Gley	L-G; inkl. G-L
12935	108	3	12096	72	Podsol-Gley	P-G; inkl. G-P
12936	108	3	12097	73	Pseudogley-Gley	S-G; inkl. G-S
12937	108	3	12098	74	Naßgley	GN; inkl. NN
12938	108	3	12099	75	Anmoorgley	GA ohne Diff.; inkl. NA und QA
12939	108	3	12100	76	Moorgley	GH ohne Diff.; inkl. NH und QH
12940	108	3	12101	77	Quellengley	QG ohne Differenzierung
12941	108	3	12102	78	Niedermoor	HN ohne Differenzierung
12942	108	3	12103	79	Übergangsmoor	HU ohne Differenzierung
12943	108	3	12104	80	Hochmoor	HH ohne Differenzierung
12944	108	3	12105	81	Rigosol	Y; inkl. U
12945	108	3	12106	82	Hortisol	YO
12946	108	3	12107	83	Deckkulturboden	Yd
12947	108	3	12108	84	Auftragsboden	YY
12948	109	3	12024	0	Bodentyp	\N
12949	109	3	12025	1	Syrosem	On
12950	109	3	12026	2	Lockersyrosem	OL, OLn
12951	109	3	12027	3	Ranker	Nn und O-N
12952	109	3	12028	4	Braunerde-Ranker	B-N; inkl. N-B
12953	109	3	12029	5	Podsol-Ranker	P-N
12954	109	3	12030	6	Regesol	Qn
12955	109	3	12031	7	Braunerde-Regesol	B-Q; inkl. Q-B
12956	109	3	12032	8	Podsol-Regosol	P-Q
12957	109	3	12033	9	Rendzina	Rn, Ru, Rs, Rb, inkl. O-R, G-R
12958	109	3	12034	10	Alpine Rendzinen	Ra, Rm, Rp, Rt
12959	109	3	12035	11	Braunerde-Rendzina	B-R; inkl. R-B
12960	109	3	12036	12	Terra fusca-Rendzina	CF-R; inkl. CR-R
12961	109	3	12037	13	Pararendzina	Zn, Zs; inkl. O-Z
12962	109	3	12038	14	Braunerde-Pararendzina	B-Z; inkl. Z-B
12963	109	3	12039	15	Hydromorphe Pararendzinen	S-Z, G-Z
12964	109	3	12040	16	Tschernoseme (Schwarzerden)	T ohne Differenzierung
12965	109	3	12041	17	Pelesol	Dn; inkl. B-D und X-D
12966	109	3	12042	18	Kalkhaltiger Pelosol	Dc; inkl. Z-D
12967	109	3	12043	19	Pseudogley-Pelosol	S-D
12968	109	3	12044	20	Ranker-Pelesol	N-D; inkl. Q-D
12969	109	3	12045	21	Braunerde	Bn, Bl, Bf
12970	109	3	12046	22	Pelesol-Braunerde	D-B
12971	109	3	12047	23	Parabraunerde-Braunerde	L-B; inkl. IB
12972	109	3	12048	24	Podsol-Braunerde	P-B
12973	109	3	12049	25	Pseudogley-Braunerde	S-B
12974	109	3	12050	26	Parabraunerde	Ln, BF=Fahlerde, Lb, T-L
12975	109	3	12051	27	Podsol-Parabraunerde	P-L; inkl. L-P
12976	109	3	12052	28	Pseudogley-Parabraunerde	S-L; ink. L-S
12977	109	3	12053	29	Podsol	Pn, Pe, Ph
12978	109	3	12054	30	Braunerde-Podsol	B-P
12979	109	3	12055	31	Pseudogley-Podsol	S-P
12980	109	3	12056	32	Moor-Podsol	H-P
12981	109	3	12057	33	Staupodsol	PSn, PSb
12982	109	3	12058	34	Terra fusca	CFn, CFc; inkl. CR (Terrae calcis-Bezeichnung von 1986 entnommenen Bodenproben wird jetzt zu 34 Terra fusca)
12983	109	3	12059	35	Braunerde-Terra fusca	B-CF
12984	109	3	12060	36	Pseudogley-Terra fusca	S-CF; inkl. CR-S, CF-S
12985	109	3	12061	37	Plastosole	VG, VB, VR
12986	109	3	12062	38	Latosole	WR, WG, WP
12987	109	3	12063	39	Braunerde-Pseudogley	B-S
12988	109	3	12064	40	Pseudogley	Sn, Sc, Sg, Sa
12989	109	3	12065	41	Podsol-Pseudogley	P-S
12990	109	3	12066	42	Haftnässepseudogley	SH ohne Differenzierung
12991	109	3	12067	43	Stagnogly	SS ohne Differenzierung
12992	109	3	12068	44	Kolluvium	Kn
12993	109	3	12069	45	Pseudogley-Kolluvium	S-K; inkl. S-KA
12994	109	3	12070	46	Gley-Kolluvium	G-K; inkl. G-KA
12995	109	3	12071	47	Aeolium	KAn, P-KA
12996	109	3	12072	48	Allochthoner Brauner Auenboden	Am, Ac; inkl. AD-A
12997	109	3	12073	49	Auenspeudogley-Brauner Auenboden	AS-A
12998	109	3	12074	50	Auengley-Brauner Auenboden	AG-A
12999	109	3	12075	51	Auensilikatrohboden (Rambia)	AO
13000	109	3	12076	52	Auenkarbonatrohboden (Kalkrambla)	AC
13001	109	3	12077	53	Auenregosol (Paternia)	AQ
13002	109	3	12078	54	Auenrendzina (Kalkpaternia)	ARn; inkl. AZn
13003	109	3	12079	55	Humusreiche Auenrendzina	ARh, inkl. AZh
13004	109	3	12080	56	Tschernitza (Schwarzerde-Auenb.)	AT
13005	109	3	12081	57	Auenbraunerde	AB ohne Differenzierung
13006	109	3	12082	58	Auenparabraunerde	AL ohne Differenzierung
13007	109	3	12083	59	Auenpodsol	AP
13008	109	3	12084	60	Auenpseudogley	AS ohne Differenzierung
13009	109	3	12085	61	Auenpelosol	AD
13010	109	3	12086	62	Auengley	AG ohne Differenzierung
13011	109	3	12087	63	Gley	Gn; inkl. Gg
13012	109	3	12088	64	Eisenreicher Gley	Ge; inkl. Go
13013	109	3	12089	65	Humusgley	Gh
13014	109	3	12090	66	Kalkhaltiger Gley	Gc; inkl. Gk
13015	109	3	12091	67	Rendzina-Gley	R-G; inkl. G-R
13016	109	3	12092	68	Regosol-Gley	Q-G
13017	109	3	12093	69	Pelosol-Gley	D-G; inkl. G-D
13018	109	3	12094	70	Braunerde-Gley	B-G; inkl. G-B
13019	109	3	12095	71	Parabraunerde-Gley	L-G; inkl. G-L
13020	109	3	12096	72	Podsol-Gley	P-G; inkl. G-P
13021	109	3	12097	73	Pseudogley-Gley	S-G; inkl. G-S
13022	109	3	12098	74	Naßgley	GN; inkl. NN
13023	109	3	12099	75	Anmoorgley	GA ohne Diff.; inkl. NA und QA
13024	109	3	12100	76	Moorgley	GH ohne Diff.; inkl. NH und QH
13025	109	3	12101	77	Quellengley	QG ohne Differenzierung
13026	109	3	12102	78	Niedermoor	HN ohne Differenzierung
13027	109	3	12103	79	Übergangsmoor	HU ohne Differenzierung
13028	109	3	12104	80	Hochmoor	HH ohne Differenzierung
13029	109	3	12105	81	Rigosol	Y; inkl. U
13030	109	3	12106	82	Hortisol	YO
13031	109	3	12107	83	Deckkulturboden	Yd
13032	109	3	12108	84	Auftragsboden	YY
13033	117	3	12479	0	Tierart	\N
13034	117	3	12480	1	Schweine	\N
13035	117	3	12481	2	Rinder	\N
13036	117	3	12482	3	Geflügel	\N
13037	119	3	12490	0	Gewässer	\N
13038	119	3	12495	5	Nordsee	\N
13039	119	3	12496	6	Ostsee	\N
13040	119	3	12497	7	Atlantik	\N
13041	119	3	12498	8	Ästuar oder Küstenbereich	\N
13042	120	3	12490	0	Gewässer	\N
13043	120	3	12491	1	Fließgewässer, Kanal, Bach	\N
13044	120	3	12492	2	Binnensee	\N
13045	120	3	12527	3	Talsperre, Rückhaltebecken, Staustufe	\N
13046	120	3	12528	4	Teich, Weiher	\N
13047	120	3	12495	5	Nordsee	\N
13048	120	3	12496	6	Ostsee	\N
13049	120	3	12497	7	Atlantik	\N
13050	120	3	12498	8	Ästuar oder Küstenbereich	\N
13051	120	3	12499	9	Teichwirtschaft Quellwasser	\N
13052	120	3	12500	10	Teichwirtschaft Vorfluter	\N
13053	120	3	12529	11	Baggersee	\N
13054	120	3	12501	12	Fluss, freifließend	\N
13055	120	3	12502	13	Fluss, staugeregelt	\N
13056	120	3	12503	14	Bach	\N
13057	120	3	12504	15	Kanal	\N
13058	121	3	12547	0	Wasserfassung	\N
13059	121	3	12548	1	Brunnen	\N
13060	121	3	12549	3	Schachtbrunnen	\N
13061	121	3	12550	4	Quellfassung	\N
13062	121	3	12551	5	Peilrohr, Beobachtungsbrunnen	\N
13063	122	3	12490	0	Gewässer	\N
13064	122	3	12491	1	Fließgewässer, Kanal, Bach	\N
13065	122	3	12492	2	Binnensee	\N
13066	122	3	12527	3	Talsperre, Rückhaltebecken, Staustufe	\N
13067	122	3	12494	4	Teich, Weiher, Baggersee	\N
13068	122	3	12495	5	Nordsee	\N
13069	122	3	12496	6	Ostsee	\N
13070	122	3	12497	7	Atlantik	\N
13071	122	3	12498	8	Ästuar oder Küstenbereich	\N
13072	122	3	12499	9	Teichwirtschaft Quellwasser	\N
13073	122	3	12500	10	Teichwirtschaft Vorfluter	\N
13074	122	3	12529	11	Baggersee	\N
13075	122	3	12501	12	Fluss, freifließend	\N
13076	122	3	12502	13	Fluss, staugeregelt	\N
13077	122	3	12503	14	Bach	\N
13078	122	3	12504	15	Kanal	\N
13079	123	3	12490	0	Gewässer	\N
13080	123	3	12491	1	Fließgewässer, Kanal, Bach	\N
13081	123	3	12492	2	Binnensee	\N
13082	123	3	12527	3	Talsperre, Rückhaltebecken, Staustufe	\N
13083	123	3	12494	4	Teich, Weiher, Baggersee	\N
13084	123	3	12495	5	Nordsee	\N
13085	123	3	12496	6	Ostsee	\N
13086	123	3	12497	7	Atlantik	\N
13087	123	3	12498	8	Ästuar oder Küstenbereich	\N
13088	123	3	12499	9	Teichwirtschaft Quellwasser	\N
13089	123	3	12500	10	Teichwirtschaft Vorfluter	\N
13090	123	3	12529	11	Baggersee	\N
13091	123	3	12501	12	Fluss, freifließend	\N
13092	123	3	12502	13	Fluss, staugeregelt	\N
13093	123	3	12503	14	Bach	\N
13094	123	3	12504	15	Kanal	\N
13095	124	3	12594	0	Bodenart	\N
13096	124	3	12595	10	Sand	\N
13097	124	3	12596	12	Sand, schluffig	\N
13098	124	3	12597	13	Sand, anlehmig	\N
13099	124	3	12598	14	Sand, lehmig	\N
13100	124	3	12599	16	Sand, tonig	\N
13101	124	3	12600	20	Schluff	\N
13102	124	3	12601	21	Schluff, sandig	\N
13103	124	3	12602	24	Schluff, lehmig	\N
13104	124	3	12603	26	Schluff, tonig	\N
13105	124	3	12604	40	Lehm	\N
13106	124	3	12605	41	Lehm, sandig	\N
13107	124	3	12606	42	Lehm, schluffig	\N
13108	124	3	12607	46	Lehm, tonig	\N
13109	124	3	12608	60	Ton	\N
13110	124	3	12609	61	Ton, sandig	\N
13111	124	3	12610	62	Ton, schluffig	\N
13112	124	3	12611	64	Ton, lehmig	\N
13113	124	3	12612	70	Kies	\N
13114	124	3	12613	71	Steine + Fels	\N
13115	124	3	12614	80	Torf	\N
13116	124	3	12615	90	Kompost	\N
13117	124	3	12616	96	Nadelstreu	\N
13118	124	3	12617	97	Laubstreu	\N
13119	124	3	12618	98	Organische Auflage	bei Waldboden
13120	124	3	12619	99	mineralischer Unterboden	bei Waldboden
13121	125	3	12490	0	Gewässer	\N
13122	125	3	12491	1	Fließgewässer, Kanal, Bach	\N
13123	125	3	12492	2	Binnensee	\N
13124	125	3	12527	3	Talsperre, Rückhaltebecken, Staustufe	\N
13125	125	3	12494	4	Teich, Weiher, Baggersee	\N
13126	125	3	12495	5	Nordsee	\N
13127	125	3	12496	6	Ostsee	\N
13128	125	3	12497	7	Atlantik	\N
13129	125	3	12498	8	Ästuar oder Küstenbereich	\N
13130	125	3	12499	9	Teichwirtschaft Quellwasser	\N
13131	125	3	12500	10	Teichwirtschaft Vorfluter	\N
13132	125	3	12529	11	Baggersee	\N
13133	125	3	12501	12	Fluss, freifließend	\N
13134	125	3	12502	13	Fluss, staugeregelt	\N
13135	125	3	12503	14	Bach	\N
13136	125	3	12504	15	Kanal	\N
13137	126	3	12490	0	Gewässer	\N
13138	126	3	12491	1	Fließgewässer, Kanal, Bach	\N
13139	126	3	12492	2	Binnensee	\N
13140	126	3	12527	3	Talsperre, Rückhaltebecken, Staustufe	\N
13141	126	3	12494	4	Teich, Weiher, Baggersee	\N
13142	126	3	12495	5	Nordsee	\N
13143	126	3	12496	6	Ostsee	\N
13144	126	3	12497	7	Atlantik	\N
13145	126	3	12498	8	Ästuar oder Küstenbereich	\N
13146	126	3	12499	9	Teichwirtschaft Quellwasser	\N
13147	126	3	12500	10	Teichwirtschaft Vorfluter	\N
13148	127	3	12490	0	Gewässer	\N
13149	127	3	12491	1	Fließgewässer, Kanal, Bach	\N
13150	127	3	12492	2	Binnensee	\N
13151	127	3	12527	3	Talsperre, Rückhaltebecken, Staustufe	\N
13152	127	3	12494	4	Teich, Weiher, Baggersee	\N
13153	127	3	12495	5	Nordsee	\N
13154	127	3	12496	6	Ostsee	\N
13155	127	3	12497	7	Atlantik	\N
13156	127	3	12498	8	Ästuar oder Küstenbereich	\N
13157	127	3	12499	9	Teichwirtschaft Quellwasser	\N
13158	127	3	12500	10	Teichwirtschaft Vorfluter	\N
13159	132	3	12687	0	Erscheinungsform bzw. Probenart	\N
13160	132	3	12688	1	organisch gebundenes gasförmiges Iod	\N
13161	132	3	12689	2	Elementares Iod	\N
13162	132	3	12690	3	Lungengängige Aerosole	\N
13163	132	3	12691	4	Gase	C 14, Kr 85, Xe 133
13164	132	3	12692	5	gasförmiges Iod	\N
13165	132	3	12693	6	elementares gasförmiges Iod	\N
13166	132	3	12694	7	Co2, Co, CH4	\N
13167	132	3	12695	8	HTO	\N
13168	132	3	12696	9	Edelgase	\N
13169	132	3	12697	10	elementar und organisch gebundenes Iod	\N
13170	133	3	12709	0	Messart	\N
13171	133	3	12710	1	Deposition	Washout
13172	133	3	12711	2	Aktivitätskonzentration	\N
13173	133	3	12712	3	Niederschlagshöhe	\N
13174	133	3	12713	4	Deposition (REI)	\N
13175	139	3	12725	0	Entwässerungssystem	\N
13176	139	3	12726	1	Kommunales Mischsystem	\N
13177	139	3	12727	2	Kommunales Trennsystem - Schmutzwasser	\N
13178	139	3	12728	3	Kommunales Trennsystem - Niederschlagswasser	\N
13179	139	3	12729	4	Betriebliches Entwässerungssystem	\N
13180	139	3	12730	5	nicht kommunale Straßenentwässerung	\N
13181	139	3	12731	6	Senkgrube, Hauskläranlage	\N
13182	139	3	12732	7	ausschl. Deponiedrainage für betriebliche Kläranlage	\N
13183	139	3	14310	8	Abwasser aus Strahlenschutzbereichen	\N
13184	140	3	12725	0	Entwässerungssystem	\N
13185	140	3	12726	1	Kommunales Mischsystem	\N
13186	140	3	12727	2	Kommunales Trennsystem - Schmutzwasser	\N
13187	140	3	12728	3	Kommunales Trennsystem - Niederschlagswasser	\N
13188	140	3	12729	4	Betriebliches Entwässerungssystem	\N
13189	140	3	12730	5	nicht kommunale Straßenentwässerung	\N
13190	140	3	12731	6	Senkgrube, Hauskläranlage	\N
13191	140	3	12732	7	ausschl. Deponiedrainage für betriebliche Kläranlage	\N
13192	142	3	12783	0	Beschickung	\N
13193	142	3	12784	1	100%     Hausmüll	\N
13194	142	3	12785	2	>  0% - 10% Klärschlamm, Rest = Hausmüll	\N
13195	142	3	12786	3	> 10% - 20% Klärschlamm, Rest = Hausmüll	\N
13196	142	3	12787	4	> 20% - 50% Klärschlamm, Rest = Hausmüll	\N
13197	142	3	12788	5	100% Klärschlamm	\N
13198	142	3	12789	6	Kohle	\N
13199	142	3	12790	7	Heizöl	\N
13200	142	3	12791	10	Sondermüll	\N
13201	142	3	12792	61	>  0% - 10% Klärschlamm, Rest = Kohle	\N
13202	142	3	12793	62	> 10% - 20% Klärschlamm, Rest = Kohle	\N
13203	142	3	12794	63	> 20% - 50% Klärschlamm, Rest = Kohle	\N
13204	143	3	12817	0	Hauptdeponiegut	\N
13205	143	3	12818	1	Hauptsächl. Hausmüll und hausmüllähnl. Gewerbeabfälle, Sperrmüll	\N
13206	143	3	12819	2	Hauptsächlich Hausmüll und Reststoffe aus Hausmüllverbrennung	\N
13207	143	3	12820	3	Hauptsächlich Reststoffe	\N
13208	143	3	12821	4	Hauptsächlich Bauschutt, Straßenaufbruch, Bodenaushub	\N
13209	143	3	12822	5	Hauptsächlich Klärschlamm	\N
13210	143	3	12823	6	Hauptsächlich Sondermüll	\N
13211	143	3	12824	8	Hauptsächlich Bodenaushub	\N
13212	144	3	12783	0	Beschickung	\N
13213	144	3	12843	1	   0% -  50% Klärschlamm	\N
13214	144	3	12844	2	> 50% -  80% Klärschlamm	\N
13215	144	3	12845	3	> 80% - 100% Klärschlamm	\N
13216	146	3	12877	0	Anlage	\N
13217	146	3	12878	1	PKW	\N
13218	146	3	12879	2	LKW	\N
13219	146	3	12880	3	Klimaanlage	\N
13220	146	3	12881	4	Müllverbrennung	\N
13221	146	3	12882	5	Milchverarbeitung	\N
13222	146	3	12883	6	Grastrocknung	\N
13223	150	3	12894	0	Eingesetztes Rohwasser	\N
13224	150	3	12895	1	Grundwasser, echt	\N
13225	150	3	12896	2	Grundwasser, künstlich angereichert	\N
13226	150	3	12897	3	Quellwasser	\N
13227	150	3	12898	4	Karst- und Kluftwasser	\N
13228	150	3	12899	5	Uferfiltrat	\N
13229	150	3	12900	6	Fließgewässer (Fluß, Kanal, Bach)	\N
13230	150	3	12901	7	Stauhaltung (Talsperre, Rückhaltebecken)	\N
13231	150	3	12902	10	Stehende Gewässer (Binnensee, Baggersee, Teich, u.a.)	\N
13232	153	3	12963	0	Bauweise	\N
13233	153	3	12964	1	Massivhaus	\N
13234	153	3	12965	2	Fachwerkhaus	\N
13235	153	3	12966	3	Fertighaus (Leichtbau)	\N
13236	153	3	12967	99	Sonstiges	\N
13237	161	3	12996	0	Art der Messung	\N
13238	161	3	12997	1	Zählrohr	\N
13239	161	3	12998	2	Festkörperdosimeter	\N
13240	161	3	12999	3	In-situ-Messung	\N
13241	162	3	12490	0	Gewässer	\N
13242	162	3	12491	1	Fließgewässer, Kanal, Bach	\N
13243	162	3	12492	2	Binnensee	\N
13244	162	3	12527	3	Talsperre, Rückhaltebecken, Staustufe	\N
13245	162	3	12494	4	Teich, Weiher, Baggersee	\N
13246	162	3	12495	5	Nordsee	\N
13247	162	3	12496	6	Ostsee	\N
13248	162	3	12497	7	Atlantik	\N
13249	162	3	12498	8	Ästuar oder Küstenbereich	\N
13250	162	3	12499	9	Teichwirtschaft Quellwasser	\N
13251	162	3	12500	10	Teichwirtschaft Vorfluter	\N
13252	163	3	12996	0	Art der Messung	\N
13253	163	3	12997	1	Zählrohr	\N
13254	163	3	12998	2	Festkörperdosimeter	\N
13255	163	3	12999	3	In-situ-Messung	\N
13256	166	3	13042	0	Art	\N
13257	166	3	13043	1	Baldrian	Valerianae officinalis
13258	166	3	13044	2	Chinarinde	Cortex Chinae succ.
13259	166	3	13045	3	Kamille	M. chamomillae
13260	166	3	13046	4	Melisse	Melissa officinalis
13261	166	3	13047	5	Pfefferminze	Mentha piperita
13262	166	3	13048	6	Ringelblume	Calendula officinalis
13263	166	3	13049	7	Salbei	Salvia officinalis
13264	166	3	13050	8	Süßholz	Glycyrhiza glabra
13265	166	3	13051	9	Weißdorn	Crataegus oxyacantha
13266	166	3	13052	20	Adonisröschen	Adonis vernalis
13267	166	3	13053	21	Agar	\N
13268	166	3	13054	22	Aloe	Aloe capensis
13269	166	3	13055	23	Anis	\N
13270	166	3	13056	24	Arnika	Arnica montana
13271	166	3	13057	25	Augentrost	Herba Euphrasiae
13272	166	3	13058	26	Bärentraube	Arctostaphylos uva-ursi
13273	166	3	13059	27	Benediktenkraut	Herba Cardui benedicti
13274	166	3	13060	28	Birke	Betula
13275	166	3	13061	29	Brennessel	Urtica
13276	166	3	13062	30	Ehrenpreis	Herba Veronicae
13277	166	3	13063	31	Eiche	Quercusilex
13278	166	3	13064	32	Eibisch	Althaea officinalis
13279	166	3	13065	33	Enzian	Gentiana cruciata, G. lutea
13280	166	3	13066	34	Eukalyptus	Eukalyptus globulus
13281	166	3	13067	35	Faulbaum	Frangula alnus
13282	166	3	13068	36	Fenchel	Foeniculum vulgare
13283	166	3	13069	37	Fichte	Picea abies
13284	166	3	13070	38	Hagebutte, Rose	Rosa gallica
13285	166	3	13071	39	Heublume	Flores graminis
13286	166	3	13072	40	Hibiskus	Hibiscus
13287	166	3	13073	41	Holunder	Sambucus
13288	166	3	13074	42	Huflattich	Tussilago farara
13289	166	3	13075	43	Ingwer	Zingiberus
13290	166	3	13076	44	Johanniskraut	Hyperikum perfuratum
13291	166	3	13077	45	Kalmus	Acorus calamus
13292	166	3	13078	46	Kastanie	Aeculus hippocastanum
13293	166	3	13079	47	Katzenpfötchen, gelb	Stoechados citrin
13294	166	3	13080	48	Kiefer	Pinus
13295	166	3	13081	49	Knoblauch	Allium sativum
13296	166	3	13082	50	Kümmel	Carum carvi
13297	166	3	13083	51	Lavendel	Lavandula angustifolia
13298	166	3	13084	52	Lein	Linacea
13299	166	3	13085	53	Linde	Tilia platyphyllos
13300	166	3	13086	54	Löwenzahn	Leontodon hispidus
13301	166	3	13087	55	Malve	Malva verticillata
13302	166	3	13088	56	Mariendistel	Silybium marianum
13303	166	3	13089	57	Mistel	Vicum album
13304	166	3	13090	58	Primel, Schlüsselblume	Primula veris
13305	166	3	13091	59	Rhabarber	Rheum rhabarbarum
13306	166	3	13092	60	Rizinusbaum	Ricinus communis
13307	166	3	13093	61	Rosmarin	Rosmarinus officinalis
13308	166	3	13094	62	Sanddorn	Hippophae rhamnoides
13309	166	3	13095	63	Schachtelhalm, Zinnkraut	Equisetum telmateja
13310	166	3	13096	64	Schafgarbe	Achillea millefolium
13311	166	3	13097	65	Schlehdorn	Prunus spino sae
13312	166	3	13098	66	Schöllkraut	Chelidonium majus
13313	166	3	13099	67	Senega	Polygala senega
13314	166	3	13100	68	Senneskraut	Herba Sennae
13315	166	3	13101	69	Spitzwegerich	Plantago major
13316	166	3	13102	70	Taubnessel	Herba Lamii albi
13317	166	3	13103	71	Tausendgüldenkraut	Centaurium umbellatum
13318	166	3	13104	72	Terpentinöl	\N
13319	166	3	13105	73	Thymian	Thymus vulgaris
13320	166	3	13106	74	Wacholder	Juniperus communis
13321	166	3	13107	75	Walnuss	Juglands
13322	166	3	13108	76	Wegwarte	Herba cichorii
13323	166	3	13109	77	Ysop	Hyssopus officinalis
13324	166	3	13738	78	Ginkgo	\N
13325	166	3	13739	79	Bitterklee	\N
13326	166	3	13740	80	Grüner Tee	\N
13327	166	3	13741	81	Island Moos	\N
13328	166	3	13742	82	Kürbis	\N
13329	166	3	13743	83	Majoran	\N
13330	166	3	13744	84	Mate grün	\N
13331	166	3	13745	85	Pfeffer schwarz	\N
13332	166	3	13746	86	Zimt	\N
13333	166	3	13760	87	Blasentank	\N
13334	166	3	13761	88	Ginseng	\N
13335	166	3	13762	89	Heidelbeeren	\N
13336	166	3	13763	90	Flieder	\N
13337	166	3	13764	91	Passionskraut	\N
13338	166	3	13765	92	Erdbeeren	\N
13339	167	3	13042	0	Art	\N
13340	167	3	13150	1	Säugetier, Stall-/Weidehaltung	\N
13341	167	3	13151	2	Säugetier, Wild	\N
13342	167	3	12482	3	Geflügel	\N
13343	167	3	13152	4	Fisch	\N
13344	167	3	13153	5	Insekten	\N
13345	167	3	12967	99	Sonstiges	\N
13346	135	3	12687	0	Erscheinungsform bzw. Probenart	\N
13347	135	3	12688	1	organisch gebundenes gasförmiges Iod	\N
13348	135	3	12690	3	Lungengängige Aerosole	\N
13349	135	3	12693	6	elementares gasförmiges Iod	\N
13350	135	3	12696	9	Edelgase	\N
13351	135	3	12697	10	elementar und organisch gebundenes Iod	\N
13352	20	4	42	0	Handelsstufe	\N
13353	20	4	43	1	Erzeuger, Importeur	\N
13354	20	4	44	2	Verarb. Betrieb, Hersteller	\N
13355	20	4	45	3	Interventions-,Großlager	\N
13356	20	4	46	4	Großhandel	\N
13357	20	4	47	5	Einzelhandel	\N
13358	20	4	48	6	Verbraucher	\N
13359	20	4	13163	7	Tanksammelwagen	Transportfahrzeug zwischen Erzeuger und verarbeitendem Betrieb
13360	21	4	42	0	Handelsstufe	\N
13361	21	4	43	1	Erzeuger, Importeur	\N
13362	21	4	44	2	Verarb. Betrieb, Hersteller	\N
13363	21	4	45	3	Interventions-,Großlager	\N
13364	21	4	46	4	Großhandel	\N
13365	21	4	47	5	Einzelhandel	\N
13366	21	4	48	6	Verbraucher	\N
13367	22	4	42	0	Handelsstufe	\N
13368	22	4	43	1	Erzeuger, Importeur	\N
13369	22	4	44	2	Verarb. Betrieb, Hersteller	\N
13370	22	4	45	3	Interventions-,Großlager	\N
13371	22	4	46	4	Großhandel	\N
13372	22	4	47	5	Einzelhandel	\N
13373	22	4	48	6	Verbraucher	\N
13374	23	4	42	0	Handelsstufe	\N
13375	23	4	43	1	Erzeuger, Importeur	\N
13376	23	4	44	2	Verarb. Betrieb, Hersteller	\N
13377	23	4	45	3	Interventions-,Großlager	\N
13378	23	4	46	4	Großhandel	\N
13379	23	4	47	5	Einzelhandel	\N
13380	23	4	48	6	Verbraucher	\N
13381	24	4	42	0	Handelsstufe	\N
13382	24	4	43	1	Erzeuger, Importeur	\N
13383	24	4	44	2	Verarb. Betrieb, Hersteller	\N
13384	24	4	45	3	Interventions-,Großlager	\N
13385	24	4	46	4	Großhandel	\N
13386	24	4	47	5	Einzelhandel	\N
13387	24	4	48	6	Verbraucher	\N
13388	25	4	42	0	Handelsstufe	\N
13389	25	4	43	1	Erzeuger, Importeur	\N
13390	25	4	44	2	Verarb. Betrieb, Hersteller	\N
13391	25	4	45	3	Interventions-,Großlager	\N
13392	25	4	46	4	Großhandel	\N
13393	25	4	47	5	Einzelhandel	\N
13394	25	4	48	6	Verbraucher	\N
13395	26	4	42	0	Handelsstufe	\N
13396	26	4	43	1	Erzeuger, Importeur	\N
13397	26	4	44	2	Verarb. Betrieb, Hersteller	\N
13398	26	4	45	3	Interventions-,Großlager	\N
13399	26	4	46	4	Großhandel	\N
13400	26	4	47	5	Einzelhandel	\N
13401	26	4	48	6	Verbraucher	\N
13402	27	4	42	0	Handelsstufe	\N
13403	27	4	43	1	Erzeuger, Importeur	\N
13404	27	4	44	2	Verarb. Betrieb, Hersteller	\N
13405	27	4	45	3	Interventions-,Großlager	\N
13406	27	4	46	4	Großhandel	\N
13407	27	4	47	5	Einzelhandel	\N
13408	27	4	48	6	Verbraucher	\N
13409	28	4	42	0	Handelsstufe	\N
13410	28	4	43	1	Erzeuger, Importeur	\N
13411	28	4	44	2	Verarb. Betrieb, Hersteller	\N
13412	28	4	45	3	Interventions-,Großlager	\N
13413	28	4	46	4	Großhandel	\N
13414	28	4	47	5	Einzelhandel	\N
13415	28	4	48	6	Verbraucher	\N
13416	29	4	42	0	Handelsstufe	\N
13417	29	4	43	1	Erzeuger, Importeur	\N
13418	29	4	44	2	Verarb. Betrieb, Hersteller	\N
13419	29	4	45	3	Interventions-,Großlager	\N
13420	29	4	46	4	Großhandel	\N
13421	29	4	47	5	Einzelhandel	\N
13422	29	4	48	6	Verbraucher	\N
13423	30	4	42	0	Handelsstufe	\N
13424	30	4	43	1	Erzeuger, Importeur	\N
13425	30	4	44	2	Verarb. Betrieb, Hersteller	\N
13426	30	4	45	3	Interventions-,Großlager	\N
13427	30	4	46	4	Großhandel	\N
13428	30	4	47	5	Einzelhandel	\N
13429	30	4	48	6	Verbraucher	\N
13430	31	4	42	0	Handelsstufe	\N
13431	31	4	43	1	Erzeuger, Importeur	\N
13432	31	4	44	2	Verarb. Betrieb, Hersteller	\N
13433	31	4	45	3	Interventions-,Großlager	\N
13434	31	4	46	4	Großhandel	\N
13435	31	4	47	5	Einzelhandel	\N
13436	31	4	48	6	Verbraucher	\N
13437	32	4	42	0	Handelsstufe	\N
13438	32	4	43	1	Erzeuger, Importeur	\N
13439	32	4	44	2	Verarb. Betrieb, Hersteller	\N
13440	32	4	45	3	Interventions-,Großlager	\N
13441	32	4	46	4	Großhandel	\N
13442	32	4	47	5	Einzelhandel	\N
13443	32	4	48	6	Verbraucher	\N
13444	33	4	42	0	Handelsstufe	\N
13445	33	4	43	1	Erzeuger, Importeur	\N
13446	33	4	44	2	Verarb. Betrieb, Hersteller	\N
13447	33	4	45	3	Interventions-,Großlager	\N
13448	33	4	46	4	Großhandel	\N
13449	33	4	47	5	Einzelhandel	\N
13450	33	4	48	6	Verbraucher	\N
13451	34	4	42	0	Handelsstufe	\N
13452	34	4	43	1	Erzeuger, Importeur	\N
13453	34	4	44	2	Verarb. Betrieb, Hersteller	\N
13454	34	4	45	3	Interventions-,Großlager	\N
13455	34	4	46	4	Großhandel	\N
13456	34	4	47	5	Einzelhandel	\N
13457	34	4	48	6	Verbraucher	\N
13458	35	4	42	0	Handelsstufe	\N
13459	35	4	43	1	Erzeuger, Importeur	\N
13460	35	4	44	2	Verarb. Betrieb, Hersteller	\N
13461	35	4	45	3	Interventions-,Großlager	\N
13462	35	4	46	4	Großhandel	\N
13463	35	4	47	5	Einzelhandel	\N
13464	35	4	48	6	Verbraucher	\N
13465	36	4	42	0	Handelsstufe	\N
13466	36	4	43	1	Erzeuger, Importeur	\N
13467	36	4	44	2	Verarb. Betrieb, Hersteller	\N
13468	36	4	45	3	Interventions-,Großlager	\N
13469	36	4	46	4	Großhandel	\N
13470	36	4	47	5	Einzelhandel	\N
13471	36	4	48	6	Verbraucher	\N
13472	37	4	42	0	Handelsstufe	\N
13473	37	4	43	1	Erzeuger, Importeur	\N
13474	37	4	44	2	Verarb. Betrieb, Hersteller	\N
13475	37	4	45	3	Interventions-,Großlager	\N
13476	37	4	46	4	Großhandel	\N
13477	37	4	47	5	Einzelhandel	\N
13478	37	4	48	6	Verbraucher	\N
13479	38	4	42	0	Handelsstufe	\N
13480	38	4	43	1	Erzeuger, Importeur	\N
13481	38	4	44	2	Verarb. Betrieb, Hersteller	\N
13482	38	4	45	3	Interventions-,Großlager	\N
13483	38	4	46	4	Großhandel	\N
13484	38	4	47	5	Einzelhandel	\N
13485	38	4	48	6	Verbraucher	\N
13486	39	4	42	0	Handelsstufe	\N
13487	39	4	43	1	Erzeuger, Importeur	\N
13488	39	4	44	2	Verarb. Betrieb, Hersteller	\N
13489	39	4	45	3	Interventions-,Großlager	\N
13490	39	4	46	4	Großhandel	\N
13491	39	4	47	5	Einzelhandel	\N
13492	39	4	48	6	Verbraucher	\N
13493	40	4	42	0	Handelsstufe	\N
13494	40	4	43	1	Erzeuger, Importeur	\N
13495	40	4	44	2	Verarb. Betrieb, Hersteller	\N
13496	40	4	45	3	Interventions-,Großlager	\N
13497	40	4	46	4	Großhandel	\N
13498	40	4	47	5	Einzelhandel	\N
13499	40	4	48	6	Verbraucher	\N
13500	41	4	42	0	Handelsstufe	\N
13501	41	4	43	1	Erzeuger, Importeur	\N
13502	41	4	44	2	Verarb. Betrieb, Hersteller	\N
13503	41	4	45	3	Interventions-,Großlager	\N
13504	41	4	46	4	Großhandel	\N
13505	41	4	47	5	Einzelhandel	\N
13506	41	4	48	6	Verbraucher	\N
13507	42	4	42	0	Handelsstufe	\N
13508	42	4	43	1	Erzeuger, Importeur	\N
13509	42	4	44	2	Verarb. Betrieb, Hersteller	\N
13510	42	4	45	3	Interventions-,Großlager	\N
13511	42	4	46	4	Großhandel	\N
13512	42	4	47	5	Einzelhandel	\N
13513	42	4	48	6	Verbraucher	\N
13514	43	4	42	0	Handelsstufe	\N
13515	43	4	43	1	Erzeuger, Importeur	\N
13516	43	4	44	2	Verarb. Betrieb, Hersteller	\N
13517	43	4	45	3	Interventions-,Großlager	\N
13518	43	4	46	4	Großhandel	\N
13519	43	4	47	5	Einzelhandel	\N
13520	43	4	48	6	Verbraucher	\N
13521	44	4	42	0	Handelsstufe	\N
13522	44	4	43	1	Erzeuger, Importeur	\N
13523	44	4	44	2	Verarb. Betrieb, Hersteller	\N
13524	44	4	45	3	Interventions-,Großlager	\N
13525	44	4	46	4	Großhandel	\N
13526	44	4	47	5	Einzelhandel	\N
13527	44	4	48	6	Verbraucher	\N
13528	45	4	42	0	Handelsstufe	\N
13529	45	4	43	1	Erzeuger, Importeur	\N
13530	45	4	44	2	Verarb. Betrieb, Hersteller	\N
13531	45	4	45	3	Interventions-,Großlager	\N
13532	45	4	46	4	Großhandel	\N
13533	45	4	47	5	Einzelhandel	\N
13534	45	4	48	6	Verbraucher	\N
13535	46	4	42	0	Handelsstufe	\N
13536	46	4	43	1	Erzeuger, Importeur	\N
13537	46	4	44	2	Verarb. Betrieb, Hersteller	\N
13538	46	4	45	3	Interventions-,Großlager	\N
13539	46	4	46	4	Großhandel	\N
13540	46	4	47	5	Einzelhandel	\N
13541	46	4	48	6	Verbraucher	\N
13542	47	4	42	0	Handelsstufe	\N
13543	47	4	43	1	Erzeuger, Importeur	\N
13544	47	4	44	2	Verarb. Betrieb, Hersteller	\N
13545	47	4	45	3	Interventions-,Großlager	\N
13546	47	4	46	4	Großhandel	\N
13547	47	4	47	5	Einzelhandel	\N
13548	47	4	48	6	Verbraucher	\N
13549	48	4	42	0	Handelsstufe	\N
13550	48	4	43	1	Erzeuger, Importeur	\N
13551	48	4	44	2	Verarb. Betrieb, Hersteller	\N
13552	48	4	45	3	Interventions-,Großlager	\N
13553	48	4	46	4	Großhandel	\N
13554	48	4	47	5	Einzelhandel	\N
13555	48	4	48	6	Verbraucher	\N
13556	49	4	42	0	Handelsstufe	\N
13557	49	4	43	1	Erzeuger, Importeur	\N
13558	49	4	44	2	Verarb. Betrieb, Hersteller	\N
13559	49	4	45	3	Interventions-,Großlager	\N
13560	49	4	46	4	Großhandel	\N
13561	49	4	47	5	Einzelhandel	\N
13562	49	4	48	6	Verbraucher	\N
13563	50	4	42	0	Handelsstufe	\N
13564	50	4	43	1	Erzeuger, Importeur	\N
13565	50	4	44	2	Verarb. Betrieb, Hersteller	\N
13566	50	4	45	3	Interventions-,Großlager	\N
13567	50	4	46	4	Großhandel	\N
13568	50	4	47	5	Einzelhandel	\N
13569	50	4	48	6	Verbraucher	\N
13570	51	4	42	0	Handelsstufe	\N
13571	51	4	43	1	Erzeuger, Importeur	\N
13572	51	4	44	2	Verarb. Betrieb, Hersteller	\N
13573	51	4	45	3	Interventions-,Großlager	\N
13574	51	4	46	4	Großhandel	\N
13575	51	4	47	5	Einzelhandel	\N
13576	51	4	48	6	Verbraucher	\N
13577	52	4	42	0	Handelsstufe	\N
13578	52	4	43	1	Erzeuger, Importeur	\N
13579	52	4	44	2	Verarb. Betrieb, Hersteller	\N
13580	52	4	45	3	Interventions-,Großlager	\N
13581	52	4	46	4	Großhandel	\N
13582	52	4	47	5	Einzelhandel	\N
13583	52	4	48	6	Verbraucher	\N
13584	53	4	42	0	Handelsstufe	\N
13585	53	4	43	1	Erzeuger, Importeur	\N
13586	53	4	44	2	Verarb. Betrieb, Hersteller	\N
13587	53	4	45	3	Interventions-,Großlager	\N
13588	53	4	46	4	Großhandel	\N
13589	53	4	47	5	Einzelhandel	\N
13590	53	4	48	6	Verbraucher	\N
13591	54	4	42	0	Handelsstufe	\N
13592	54	4	43	1	Erzeuger, Importeur	\N
13593	54	4	44	2	Verarb. Betrieb, Hersteller	\N
13594	54	4	45	3	Interventions-,Großlager	\N
13595	54	4	46	4	Großhandel	\N
13596	54	4	47	5	Einzelhandel	\N
13597	54	4	48	6	Verbraucher	\N
13598	55	4	42	0	Handelsstufe	\N
13599	55	4	43	1	Erzeuger, Importeur	\N
13600	55	4	44	2	Verarb. Betrieb, Hersteller	\N
13601	55	4	45	3	Interventions-,Großlager	\N
13602	55	4	46	4	Großhandel	\N
13603	55	4	47	5	Einzelhandel	\N
13604	55	4	48	6	Verbraucher	\N
13605	56	4	42	0	Handelsstufe	\N
13606	56	4	43	1	Erzeuger, Importeur	\N
13607	56	4	44	2	Verarb. Betrieb, Hersteller	\N
13608	56	4	45	3	Interventions-,Großlager	\N
13609	56	4	46	4	Großhandel	\N
13610	56	4	47	5	Einzelhandel	\N
13611	56	4	48	6	Verbraucher	\N
13612	57	4	42	0	Handelsstufe	\N
13613	57	4	43	1	Erzeuger, Importeur	\N
13614	57	4	44	2	Verarb. Betrieb, Hersteller	\N
13615	57	4	45	3	Interventions-,Großlager	\N
13616	57	4	46	4	Großhandel	\N
13617	57	4	47	5	Einzelhandel	\N
13618	57	4	48	6	Verbraucher	\N
13619	58	4	42	0	Handelsstufe	\N
13620	58	4	43	1	Erzeuger, Importeur	\N
13621	58	4	44	2	Verarb. Betrieb, Hersteller	\N
13622	58	4	45	3	Interventions-,Großlager	\N
13623	58	4	46	4	Großhandel	\N
13624	58	4	47	5	Einzelhandel	\N
13625	58	4	48	6	Verbraucher	\N
13626	59	4	42	0	Handelsstufe	\N
13627	59	4	43	1	Erzeuger, Importeur	\N
13628	59	4	44	2	Verarb. Betrieb, Hersteller	\N
13629	59	4	45	3	Interventions-,Großlager	\N
13630	59	4	46	4	Großhandel	\N
13631	59	4	47	5	Einzelhandel	\N
13632	59	4	48	6	Verbraucher	\N
13633	60	4	42	0	Handelsstufe	\N
13634	60	4	43	1	Erzeuger, Importeur	\N
13635	60	4	44	2	Verarb. Betrieb, Hersteller	\N
13636	60	4	45	3	Interventions-,Großlager	\N
13637	60	4	46	4	Großhandel	\N
13638	60	4	47	5	Einzelhandel	\N
13639	60	4	48	6	Verbraucher	\N
13640	61	4	42	0	Handelsstufe	\N
13641	61	4	43	1	Erzeuger, Importeur	\N
13642	61	4	44	2	Verarb. Betrieb, Hersteller	\N
13643	61	4	45	3	Interventions-,Großlager	\N
13644	61	4	46	4	Großhandel	\N
13645	61	4	47	5	Einzelhandel	\N
13646	61	4	48	6	Verbraucher	\N
13647	62	4	42	0	Handelsstufe	\N
13648	62	4	43	1	Erzeuger, Importeur	\N
13649	62	4	44	2	Verarb. Betrieb, Hersteller	\N
13650	62	4	45	3	Interventions-,Großlager	\N
13651	62	4	46	4	Großhandel	\N
13652	62	4	47	5	Einzelhandel	\N
13653	62	4	48	6	Verbraucher	\N
13654	63	4	42	0	Handelsstufe	\N
13655	63	4	43	1	Erzeuger, Importeur	\N
13656	63	4	44	2	Verarb. Betrieb, Hersteller	\N
13657	63	4	45	3	Interventions-,Großlager	\N
13658	63	4	46	4	Großhandel	\N
13659	63	4	47	5	Einzelhandel	\N
13660	63	4	48	6	Verbraucher	\N
13661	64	4	42	0	Handelsstufe	\N
13662	64	4	43	1	Erzeuger, Importeur	\N
13663	64	4	44	2	Verarb. Betrieb, Hersteller	\N
13664	64	4	45	3	Interventions-,Großlager	\N
13665	64	4	46	4	Großhandel	\N
13666	64	4	47	5	Einzelhandel	\N
13667	64	4	48	6	Verbraucher	\N
13668	65	4	42	0	Handelsstufe	\N
13669	65	4	43	1	Erzeuger, Importeur	\N
13670	65	4	44	2	Verarb. Betrieb, Hersteller	\N
13671	65	4	45	3	Interventions-,Großlager	\N
13672	65	4	46	4	Großhandel	\N
13673	65	4	47	5	Einzelhandel	\N
13674	65	4	48	6	Verbraucher	\N
13675	66	4	42	0	Handelsstufe	\N
13676	66	4	43	1	Erzeuger, Importeur	\N
13677	66	4	44	2	Verarb. Betrieb, Hersteller	\N
13678	66	4	45	3	Interventions-,Großlager	\N
13679	66	4	46	4	Großhandel	\N
13680	66	4	47	5	Einzelhandel	\N
13681	66	4	48	6	Verbraucher	\N
13682	68	4	42	0	Handelsstufe	\N
13683	68	4	43	1	Erzeuger, Importeur	\N
13684	68	4	44	2	Verarb. Betrieb, Hersteller	\N
13685	68	4	45	3	Interventions-,Großlager	\N
13686	68	4	46	4	Großhandel	\N
13687	68	4	47	5	Einzelhandel	\N
13688	68	4	48	6	Verbraucher	\N
13689	69	4	42	0	Handelsstufe	\N
13690	69	4	43	1	Erzeuger, Importeur	\N
13691	69	4	44	2	Verarb. Betrieb, Hersteller	\N
13692	69	4	45	3	Interventions-,Großlager	\N
13693	69	4	46	4	Großhandel	\N
13694	69	4	47	5	Einzelhandel	\N
13695	69	4	48	6	Verbraucher	\N
13696	70	4	42	0	Handelsstufe	\N
13697	70	4	43	1	Erzeuger, Importeur	\N
13698	70	4	44	2	Verarb. Betrieb, Hersteller	\N
13699	70	4	45	3	Interventions-,Großlager	\N
13700	70	4	46	4	Großhandel	\N
13701	70	4	47	5	Einzelhandel	\N
13702	70	4	48	6	Verbraucher	\N
13703	71	4	42	0	Handelsstufe	\N
13704	71	4	43	1	Erzeuger, Importeur	\N
13705	71	4	44	2	Verarb. Betrieb, Hersteller	\N
13706	71	4	45	3	Interventions-,Großlager	\N
13707	71	4	46	4	Großhandel	\N
13708	71	4	47	5	Einzelhandel	\N
13709	71	4	48	6	Verbraucher	\N
13710	72	4	42	0	Handelsstufe	\N
13711	72	4	43	1	Erzeuger, Importeur	\N
13712	72	4	44	2	Verarb. Betrieb, Hersteller	\N
13713	72	4	45	3	Interventions-,Großlager	\N
13714	72	4	46	4	Großhandel	\N
13715	72	4	47	5	Einzelhandel	\N
13716	72	4	48	6	Verbraucher	\N
13717	73	4	10149	0	Rohwasser	\N
13718	73	4	10150	1	Grundwasser, echt	\N
13719	73	4	10151	2	Grundwasser, angereichert	künstlich angereichert
13720	73	4	10152	3	Quellwasser	\N
13721	73	4	10153	4	Karst- und Kluftwasser	\N
13722	73	4	10154	5	Uferfiltrat	\N
13723	73	4	10155	6	Fließgewässer (Fluß, Kanal, Bach)	\N
13724	73	4	10156	7	Stauhaltung (Talsperrwasser, Rückhaltebecken)	\N
13725	73	4	10157	8	Zisternenwasser	\N
13726	73	4	13140	9	Schmelzwasser	\N
13727	73	4	13141	10	stehende Gewässer (Binnenseen, Baggerseen, Teiche u.a.)	\N
13728	73	4	13142	11	entsalztes Meerwasser	\N
13729	74	4	10244	0	Sorten	\N
13730	74	4	10245	10	Burley, Blattmischprobe	\N
13731	74	4	10246	11	Burley, Grumpen	\N
13732	74	4	10247	12	Burley, Sandblatt	\N
13733	74	4	10248	13	Burley, Hauptgut	\N
13734	74	4	10249	14	Burley, Obergut	\N
13735	74	4	10250	20	Virgin, Blattmischprobe	\N
13736	74	4	10251	21	Virgin, Grumpen	\N
13737	74	4	10252	22	Virgin, Sandblatt	\N
13738	74	4	10253	23	Virgin, Hauptgut	\N
13739	74	4	10254	24	Virgin, Obergut	\N
13740	74	4	10255	30	Geudertheimer, Blattmischprobe	\N
13741	74	4	10256	31	Geudertheimer, Grumpen	\N
13742	74	4	10257	32	Geudertheimer, Sandblatt	\N
13743	74	4	10258	33	Geudertheimer, Hauptgut	\N
13744	74	4	10259	34	Geudertheimer, Obergut	\N
13745	74	4	10260	40	Orient, Blattmischprobe	\N
13746	74	4	10261	50	Würztabake, sonstige	\N
13747	81	4	11041	0	Geschlecht	\N
13748	81	4	11042	1	Männlich	\N
13749	81	4	11043	2	Weiblich	\N
13750	82	4	11041	0	Geschlecht	\N
13751	82	4	11042	1	Männlich	\N
13752	82	4	11043	2	Weiblich	\N
13753	83	4	11041	0	Geschlecht	\N
13754	83	4	11042	1	Männlich	\N
13755	83	4	11043	2	Weiblich	\N
13756	84	4	11041	0	Geschlecht	\N
13757	84	4	11042	1	Männlich	\N
13758	84	4	11043	2	Weiblich	\N
13759	108	4	12109	0	Humusgehalt	\N
13760	108	4	12110	1	0 - 4 % Humus	  humusarm bis Humus
13761	108	4	12111	2	5 - 8 % Humus	  stark humos
13762	108	4	12112	3	9 - 14 % Humus	  sehr stark humos
13763	108	4	12113	4	15 - 30 % Humus	  anmoorig
13764	108	4	12114	5	>   30 % Humus	  Torf
13765	120	4	13149	0	Entnahmetiefe von	\N
13766	120	4	13150	1	Oberfläche	\N
13767	120	4	13151	2	0,2 m	\N
13768	120	4	13152	3	0,5 m	\N
13769	120	4	13153	4	1 m	\N
13770	120	4	13154	5	2 m	\N
13771	120	4	13155	6	5 m	\N
13772	120	4	13156	7	10 m	\N
13773	120	4	13157	8	20 m	\N
13774	120	4	13158	9	30 m	\N
13775	120	4	13159	10	40 m	\N
13776	120	4	13160	11	50 m	\N
13777	120	4	13161	12	60 m	\N
13778	120	4	13162	13	> 60 m	\N
13779	121	4	13143	0	Grundwasser-Entnahmetiefe	\N
13780	121	4	13144	1	0 - 5 m	\N
13781	121	4	13145	2	> 5 - 20 m	\N
13782	121	4	13146	3	> 20 - 50 m	\N
13783	121	4	13147	4	> 50 - 100 m	\N
13784	121	4	13148	5	> 100 m	\N
13785	132	4	12698	0	Sammeltemperatur	\N
13786	132	4	12699	1	Raumtemperatur	\N
13787	132	4	12700	2	Fl. N2	\N
13788	132	4	12701	3	-78°C	\N
13789	132	4	12702	4	Kühlschrank (ca. 0°C)	\N
13790	142	4	12795	0	Ofenlinie	\N
13791	143	4	12825	0	Deponieabdeckung	\N
13792	143	4	12826	1	Deponieoberfläche, abgedeckt	\N
13793	143	4	12827	2	Deponieoberfläche, nicht abgedeckt	\N
13794	144	4	12846	0	Alter der Miete	\N
13795	144	4	12847	1	0 - 6 Monate	\N
13796	144	4	12848	2	> 6 - 12 Monate	\N
13797	144	4	12849	3	>12 - 18 Monate	\N
13798	144	4	12850	4	>18 Monate	\N
13799	153	4	12968	0	Baujahr	\N
13800	153	4	12969	1	vor 1900	\N
13801	153	4	12970	2	1901 - 1948	\N
13802	153	4	12971	3	nach 1948	\N
13803	153	4	12972	99	nicht bekannt	\N
13804	161	4	13003	0	Abstand	Abstand des Messgerätes von der Bodenoberfläche
13805	161	4	13004	1	Oberfläche	\N
13806	161	4	13005	2	0,5 m	\N
13807	161	4	13006	3	1   m	\N
13808	161	4	13007	4	1,5 m	\N
13809	161	4	13008	5	2   m	\N
13810	161	4	13009	6	2,5 m	\N
13811	161	4	13010	7	3   m	\N
13812	161	4	13011	8	3,5 m	\N
13813	161	4	13012	9	4   m	\N
13814	161	4	13013	10	4,5 m	\N
13815	161	4	13014	11	5   m	\N
13816	161	4	13015	12	5,5 m	\N
13817	161	4	13016	13	6   m	\N
13818	161	4	13017	14	6,5 m	\N
13819	161	4	13018	15	7   m	\N
13820	161	4	13019	16	7,5 m	\N
13821	161	4	13020	17	8   m	\N
13822	161	4	13021	18	8,5 m	\N
13823	161	4	13022	19	9   m	\N
13824	161	4	13023	20	9,5 m	\N
13825	161	4	13024	21	10  m	\N
13826	162	4	13003	0	Abstand	Abstand des Messgerätes von der Bodenoberfläche
13827	162	4	13004	1	Oberfläche	\N
13828	162	4	13005	2	0,5 m	\N
13829	162	4	13006	3	1   m	\N
13830	162	4	13007	4	1,5 m	\N
13831	162	4	13008	5	2   m	\N
13832	162	4	13009	6	2,5 m	\N
13833	162	4	13010	7	3   m	\N
13834	162	4	13011	8	3,5 m	\N
13835	162	4	13012	9	4   m	\N
13836	162	4	13013	10	4,5 m	\N
13837	162	4	13014	11	5   m	\N
13838	162	4	13015	12	5,5 m	\N
13839	162	4	13016	13	6   m	\N
13840	162	4	13017	14	6,5 m	\N
13841	162	4	13018	15	7   m	\N
13842	162	4	13019	16	7,5 m	\N
13843	162	4	13020	17	8   m	\N
13844	162	4	13021	18	8,5 m	\N
13845	162	4	13022	19	9   m	\N
13846	162	4	13023	20	9,5 m	\N
13847	162	4	13024	21	10  m	\N
13848	163	4	13003	0	Abstand	Abstand des Messgerätes von der Bodenoberfläche
13849	163	4	13004	1	Oberfläche	\N
13850	163	4	13005	2	0,5 m	\N
13851	163	4	13006	3	1   m	\N
13852	163	4	13007	4	1,5 m	\N
13853	163	4	13008	5	2   m	\N
13854	163	4	13009	6	2,5 m	\N
13855	163	4	13010	7	3   m	\N
13856	163	4	13011	8	3,5 m	\N
13857	163	4	13012	9	4   m	\N
13858	163	4	13013	10	4,5 m	\N
13859	163	4	13014	11	5   m	\N
13860	163	4	13015	12	5,5 m	\N
13861	163	4	13016	13	6   m	\N
13862	163	4	13017	14	6,5 m	\N
13863	163	4	13018	15	7   m	\N
13864	163	4	13019	16	7,5 m	\N
13865	163	4	13020	17	8   m	\N
13866	163	4	13021	18	8,5 m	\N
13867	163	4	13022	19	9   m	\N
13868	163	4	13023	20	9,5 m	\N
13869	163	4	13024	21	10  m	\N
13870	166	4	13110	0	Verarbeitungsstufe, Produktform	\N
13871	166	4	13111	10	Rohprodukt	\N
13872	166	4	13112	11	Frischsubstanz	\N
13873	166	4	13113	12	Rohprodukt, getrocknet	\N
13874	166	4	13114	13	Rohprodukt, homogenisiert	\N
13875	166	4	13115	14	Rohprodukt, sonstiges	\N
13876	166	4	13116	15	Rohprodukt, getrocknet, Wildsammlung	\N
13877	166	4	13117	16	Frischsubstanz, Wildsammlung	\N
13878	166	4	13118	20	Zwischenprodukt, Konzentrat	\N
13879	166	4	13119	21	Saft, gepresst, unbehandelt	\N
13880	166	4	13120	22	Saft, konzentriert	\N
13881	166	4	13121	23	Öl, Fett, Harz, unbehandelt	\N
13882	166	4	13122	24	Öl, Fett, konzentriert	\N
13883	166	4	13123	25	Extrakt, getrocknet	\N
13884	166	4	13124	26	Extrakt, wässrig	\N
13885	166	4	13125	27	Extrakt, alkoholisch oder ätherisch	\N
13886	166	4	13126	30	Anwendungsfertiges Produkt zur Anwendung im Körper	\N
13887	166	4	13127	31	Tablette, Pulver, Saft, etc. zur Einnahme	\N
13888	166	4	13128	32	Teeaufguss	\N
13889	166	4	13129	33	Infusions-, Injektionslösungen	\N
13890	166	4	13130	34	Lösung, Zusatz zum Erzeugen von Aerosolen	\N
13891	166	4	13131	39	sonstige anwendungsfertige Zubereitung	\N
13892	166	4	13132	40	anwendungsfertiges Produkt zur äußeren Anwendung	\N
13893	166	4	13133	41	Salbe, Lösung etc.	\N
13894	166	4	13134	42	Badezusatz u.ä., Heilmittel	\N
13895	166	4	13135	43	Verbandsmaterial u.ä. Heilmittel	\N
13896	166	4	13136	44	Implantat, Prothese u.a. Gegenstände im ständigen Körperkontakt	\N
13897	166	4	13137	49	Sonstige Zubereitung zur äußeren Anwendung	\N
13898	166	4	13138	50	Messtechnisches Präparat	\N
13899	166	4	13139	99	Sonstiges	\N
13900	167	4	13110	0	Verarbeitungsstufe, Produktform	\N
13901	167	4	13111	10	Rohprodukt	\N
13902	167	4	13112	11	Frischsubstanz	\N
13903	167	4	13113	12	Rohprodukt, getrocknet	\N
13904	167	4	13114	13	Rohprodukt, homogenisiert	\N
13905	167	4	13115	14	Rohprodukt, sonstiges	\N
13906	167	4	13116	15	Rohprodukt, getrocknet, Wildsammlung	\N
13907	167	4	13117	16	Frischsubstanz, Wildsammlung	\N
13908	167	4	13118	20	Zwischenprodukt, Konzentrat	\N
13909	167	4	13119	21	Saft, gepresst, unbehandelt	\N
13910	167	4	13120	22	Saft, konzentriert	\N
13911	167	4	13121	23	Öl, Fett, Harz, unbehandelt	\N
13912	167	4	13122	24	Öl, Fett, konzentriert	\N
13913	167	4	13123	25	Extrakt, getrocknet	\N
13914	167	4	13124	26	Extrakt, wässrig	\N
13915	167	4	13125	27	Extrakt, alkoholisch oder ätherisch	\N
13916	167	4	13126	30	Anwendungsfertiges Produkt zur Anwendung im Körper	\N
13917	167	4	13127	31	Tablette, Pulver, Saft, etc. zur Einnahme	\N
13918	167	4	13128	32	Teeaufguss	\N
13919	167	4	13129	33	Infusions-, Injektionslösungen	\N
13920	167	4	13130	34	Lösung, Zusatz zum Erzeugen von Aerosolen	\N
13921	167	4	13131	39	sonstige anwendungsfertige Zubereitung	\N
13922	167	4	13132	40	anwendungsfertiges Produkt zur äußeren Anwendung	\N
13923	167	4	13133	41	Salbe, Lösung etc.	\N
13924	167	4	13134	42	Badezusatz u.ä., Heilmittel	\N
13925	167	4	13135	43	Verbandsmaterial u.ä. Heilmittel	\N
13926	167	4	13136	44	Implantat, Prothese u.a. Gegenstände im ständigen Körperkontakt	\N
13927	167	4	13137	49	Sonstige Zubereitung zur äußeren Anwendung	\N
13928	167	4	13138	50	Messtechnisches Präparat	\N
13929	167	4	13139	99	Sonstiges	\N
13930	168	4	13110	0	Verarbeitungsstufe, Produktform	\N
13931	168	4	13111	10	Rohprodukt	\N
13932	168	4	13112	11	Frischsubstanz	\N
13933	168	4	13113	12	Rohprodukt, getrocknet	\N
13934	168	4	13114	13	Rohprodukt, homogenisiert	\N
13935	168	4	13115	14	Rohprodukt, sonstiges	\N
13936	168	4	13116	15	Rohprodukt, getrocknet, Wildsammlung	\N
13937	168	4	13117	16	Frischsubstanz, Wildsammlung	\N
13938	168	4	13118	20	Zwischenprodukt, Konzentrat	\N
13939	168	4	13119	21	Saft, gepresst, unbehandelt	\N
13940	168	4	13120	22	Saft, konzentriert	\N
13941	168	4	13121	23	Öl, Fett, Harz, unbehandelt	\N
13942	168	4	13122	24	Öl, Fett, konzentriert	\N
13943	168	4	13123	25	Extrakt, getrocknet	\N
13944	168	4	13124	26	Extrakt, wässrig	\N
13945	168	4	13125	27	Extrakt, alkoholisch oder ätherisch	\N
13946	168	4	13126	30	Anwendungsfertiges Produkt zur Anwendung im Körper	\N
13947	168	4	13127	31	Tablette, Pulver, Saft, etc. zur Einnahme	\N
13948	168	4	13128	32	Teeaufguss	\N
13949	168	4	13129	33	Infusions-, Injektionslösungen	\N
13950	168	4	13130	34	Lösung, Zusatz zum Erzeugen von Aerosolen	\N
13951	168	4	13131	39	sonstige anwendungsfertige Zubereitung	\N
13952	168	4	13132	40	anwendungsfertiges Produkt zur äußeren Anwendung	\N
13953	168	4	13133	41	Salbe, Lösung etc.	\N
13954	168	4	13134	42	Badezusatz u.ä., Heilmittel	\N
13955	168	4	13135	43	Verbandsmaterial u.ä. Heilmittel	\N
13956	168	4	13136	44	Implantat, Prothese u.a. Gegenstände im ständigen Körperkontakt	\N
13957	168	4	13137	49	Sonstige Zubereitung zur äußeren Anwendung	\N
13958	168	4	13138	50	Messtechnisches Präparat	\N
13959	168	4	13139	99	Sonstiges	\N
13960	20	5	49	0	Menge	\N
13961	20	5	50	1	>0 kg - 1 kg	\N
13962	20	5	51	2	>1 kg - 10 kg	\N
13963	20	5	52	3	>10 kg - 100 kg	\N
13964	20	5	53	4	>100 kg - 1.000 kg	\N
13965	20	5	54	5	>1.000 kg - 10.000 kg	\N
13966	20	5	55	6	>10.000 kg - 100.000 kg	\N
13967	20	5	56	7	>100.000 kg	\N
13968	21	5	49	0	Menge	\N
13969	21	5	50	1	>0 kg - 1 kg	\N
13970	21	5	51	2	>1 kg - 10 kg	\N
13971	21	5	52	3	>10 kg - 100 kg	\N
13972	21	5	53	4	>100 kg - 1.000 kg	\N
13973	21	5	54	5	>1.000 kg - 10.000 kg	\N
13974	21	5	55	6	>10.000 kg - 100.000 kg	\N
13975	21	5	56	7	>100.000 kg	\N
13976	22	5	49	0	Menge	\N
13977	22	5	50	1	>0 kg - 1 kg	\N
13978	22	5	51	2	>1 kg - 10 kg	\N
13979	22	5	52	3	>10 kg - 100 kg	\N
13980	22	5	53	4	>100 kg - 1.000 kg	\N
13981	22	5	54	5	>1.000 kg - 10.000 kg	\N
13982	22	5	55	6	>10.000 kg - 100.000 kg	\N
13983	22	5	56	7	>100.000 kg	\N
13984	23	5	49	0	Menge	\N
13985	23	5	50	1	>0 kg - 1 kg	\N
13986	23	5	51	2	>1 kg - 10 kg	\N
13987	23	5	52	3	>10 kg - 100 kg	\N
13988	23	5	53	4	>100 kg - 1.000 kg	\N
13989	23	5	54	5	>1.000 kg - 10.000 kg	\N
13990	23	5	55	6	>10.000 kg - 100.000 kg	\N
13991	23	5	56	7	>100.000 kg	\N
13992	24	5	49	0	Menge	\N
13993	24	5	50	1	>0 kg - 1 kg	\N
13994	24	5	51	2	>1 kg - 10 kg	\N
13995	24	5	52	3	>10 kg - 100 kg	\N
13996	24	5	53	4	>100 kg - 1.000 kg	\N
13997	24	5	54	5	>1.000 kg - 10.000 kg	\N
13998	24	5	55	6	>10.000 kg - 100.000 kg	\N
13999	24	5	56	7	>100.000 kg	\N
14000	25	5	49	0	Menge	\N
14001	25	5	50	1	>0 kg - 1 kg	\N
14002	25	5	51	2	>1 kg - 10 kg	\N
14003	25	5	52	3	>10 kg - 100 kg	\N
14004	25	5	53	4	>100 kg - 1.000 kg	\N
14005	25	5	54	5	>1.000 kg - 10.000 kg	\N
14006	25	5	55	6	>10.000 kg - 100.000 kg	\N
14007	25	5	56	7	>100.000 kg	\N
14008	26	5	49	0	Menge	\N
14009	26	5	50	1	>0 kg - 1 kg	\N
14010	26	5	51	2	>1 kg - 10 kg	\N
14011	26	5	52	3	>10 kg - 100 kg	\N
14012	26	5	53	4	>100 kg - 1.000 kg	\N
14013	26	5	54	5	>1.000 kg - 10.000 kg	\N
14014	26	5	55	6	>10.000 kg - 100.000 kg	\N
14015	26	5	56	7	>100.000 kg	\N
14016	27	5	49	0	Menge	\N
14017	27	5	50	1	>0 kg - 1 kg	\N
14018	27	5	51	2	>1 kg - 10 kg	\N
14019	27	5	52	3	>10 kg - 100 kg	\N
14020	27	5	53	4	>100 kg - 1.000 kg	\N
14021	27	5	54	5	>1.000 kg - 10.000 kg	\N
14022	27	5	55	6	>10.000 kg - 100.000 kg	\N
14023	27	5	56	7	>100.000 kg	\N
14024	28	5	49	0	Menge	\N
14025	28	5	50	1	>0 kg - 1 kg	\N
14026	28	5	51	2	>1 kg - 10 kg	\N
14027	28	5	52	3	>10 kg - 100 kg	\N
14028	28	5	53	4	>100 kg - 1.000 kg	\N
14029	28	5	54	5	>1.000 kg - 10.000 kg	\N
14030	28	5	55	6	>10.000 kg - 100.000 kg	\N
14031	28	5	56	7	>100.000 kg	\N
14032	29	5	49	0	Menge	\N
14033	29	5	50	1	>0 kg - 1 kg	\N
14034	29	5	51	2	>1 kg - 10 kg	\N
14035	29	5	52	3	>10 kg - 100 kg	\N
14036	29	5	53	4	>100 kg - 1.000 kg	\N
14037	29	5	54	5	>1.000 kg - 10.000 kg	\N
14038	29	5	55	6	>10.000 kg - 100.000 kg	\N
14039	29	5	56	7	>100.000 kg	\N
14040	30	5	49	0	Menge	\N
14041	30	5	50	1	>0 kg - 1 kg	\N
14042	30	5	51	2	>1 kg - 10 kg	\N
14043	30	5	52	3	>10 kg - 100 kg	\N
14044	30	5	53	4	>100 kg - 1.000 kg	\N
14045	30	5	54	5	>1.000 kg - 10.000 kg	\N
14173	46	5	54	5	>1.000 kg - 10.000 kg	\N
14046	30	5	55	6	>10.000 kg - 100.000 kg	\N
14047	30	5	56	7	>100.000 kg	\N
14048	31	5	49	0	Menge	\N
14049	31	5	50	1	>0 kg - 1 kg	\N
14050	31	5	51	2	>1 kg - 10 kg	\N
14051	31	5	52	3	>10 kg - 100 kg	\N
14052	31	5	53	4	>100 kg - 1.000 kg	\N
14053	31	5	54	5	>1.000 kg - 10.000 kg	\N
14054	31	5	55	6	>10.000 kg - 100.000 kg	\N
14055	31	5	56	7	>100.000 kg	\N
14056	32	5	49	0	Menge	\N
14057	32	5	50	1	>0 kg - 1 kg	\N
14058	32	5	51	2	>1 kg - 10 kg	\N
14059	32	5	52	3	>10 kg - 100 kg	\N
14060	32	5	53	4	>100 kg - 1.000 kg	\N
14061	32	5	54	5	>1.000 kg - 10.000 kg	\N
14062	32	5	55	6	>10.000 kg - 100.000 kg	\N
14063	32	5	56	7	>100.000 kg	\N
14064	33	5	49	0	Menge	\N
14065	33	5	50	1	>0 kg - 1 kg	\N
14066	33	5	51	2	>1 kg - 10 kg	\N
14067	33	5	52	3	>10 kg - 100 kg	\N
14068	33	5	53	4	>100 kg - 1.000 kg	\N
14069	33	5	54	5	>1.000 kg - 10.000 kg	\N
14070	33	5	55	6	>10.000 kg - 100.000 kg	\N
14071	33	5	56	7	>100.000 kg	\N
14072	34	5	49	0	Menge	\N
14073	34	5	50	1	>0 kg - 1 kg	\N
14074	34	5	51	2	>1 kg - 10 kg	\N
14075	34	5	52	3	>10 kg - 100 kg	\N
14076	34	5	53	4	>100 kg - 1.000 kg	\N
14077	34	5	54	5	>1.000 kg - 10.000 kg	\N
14078	34	5	55	6	>10.000 kg - 100.000 kg	\N
14079	34	5	56	7	>100.000 kg	\N
14080	35	5	49	0	Menge	\N
14081	35	5	50	1	>0 kg - 1 kg	\N
14082	35	5	51	2	>1 kg - 10 kg	\N
14083	35	5	52	3	>10 kg - 100 kg	\N
14084	35	5	53	4	>100 kg - 1.000 kg	\N
14085	35	5	54	5	>1.000 kg - 10.000 kg	\N
14086	35	5	55	6	>10.000 kg - 100.000 kg	\N
14087	35	5	56	7	>100.000 kg	\N
14088	36	5	49	0	Menge	\N
14089	36	5	50	1	>0 kg - 1 kg	\N
14090	36	5	51	2	>1 kg - 10 kg	\N
14091	36	5	52	3	>10 kg - 100 kg	\N
14092	36	5	53	4	>100 kg - 1.000 kg	\N
14093	36	5	54	5	>1.000 kg - 10.000 kg	\N
14094	36	5	55	6	>10.000 kg - 100.000 kg	\N
14095	36	5	56	7	>100.000 kg	\N
14096	37	5	49	0	Menge	\N
14097	37	5	50	1	>0 kg - 1 kg	\N
14098	37	5	51	2	>1 kg - 10 kg	\N
14099	37	5	52	3	>10 kg - 100 kg	\N
14100	37	5	53	4	>100 kg - 1.000 kg	\N
14101	37	5	54	5	>1.000 kg - 10.000 kg	\N
14102	37	5	55	6	>10.000 kg - 100.000 kg	\N
14103	37	5	56	7	>100.000 kg	\N
14104	38	5	49	0	Menge	\N
14105	38	5	50	1	>0 kg - 1 kg	\N
14106	38	5	51	2	>1 kg - 10 kg	\N
14107	38	5	52	3	>10 kg - 100 kg	\N
14108	38	5	53	4	>100 kg - 1.000 kg	\N
14109	38	5	54	5	>1.000 kg - 10.000 kg	\N
14110	38	5	55	6	>10.000 kg - 100.000 kg	\N
14111	38	5	56	7	>100.000 kg	\N
14112	39	5	49	0	Menge	\N
14113	39	5	50	1	>0 kg - 1 kg	\N
14114	39	5	51	2	>1 kg - 10 kg	\N
14115	39	5	52	3	>10 kg - 100 kg	\N
14116	39	5	53	4	>100 kg - 1.000 kg	\N
14117	39	5	54	5	>1.000 kg - 10.000 kg	\N
14118	39	5	55	6	>10.000 kg - 100.000 kg	\N
14119	39	5	56	7	>100.000 kg	\N
14120	40	5	49	0	Menge	\N
14121	40	5	50	1	>0 kg - 1 kg	\N
14122	40	5	51	2	>1 kg - 10 kg	\N
14123	40	5	52	3	>10 kg - 100 kg	\N
14124	40	5	53	4	>100 kg - 1.000 kg	\N
14125	40	5	54	5	>1.000 kg - 10.000 kg	\N
14126	40	5	55	6	>10.000 kg - 100.000 kg	\N
14127	40	5	56	7	>100.000 kg	\N
14128	41	5	49	0	Menge	\N
14129	41	5	50	1	>0 kg - 1 kg	\N
14130	41	5	51	2	>1 kg - 10 kg	\N
14131	41	5	52	3	>10 kg - 100 kg	\N
14132	41	5	53	4	>100 kg - 1.000 kg	\N
14133	41	5	54	5	>1.000 kg - 10.000 kg	\N
14134	41	5	55	6	>10.000 kg - 100.000 kg	\N
14135	41	5	56	7	>100.000 kg	\N
14136	42	5	49	0	Menge	\N
14137	42	5	50	1	>0 kg - 1 kg	\N
14138	42	5	51	2	>1 kg - 10 kg	\N
14139	42	5	52	3	>10 kg - 100 kg	\N
14140	42	5	53	4	>100 kg - 1.000 kg	\N
14141	42	5	54	5	>1.000 kg - 10.000 kg	\N
14142	42	5	55	6	>10.000 kg - 100.000 kg	\N
14143	42	5	56	7	>100.000 kg	\N
14144	43	5	49	0	Menge	\N
14145	43	5	50	1	>0 kg - 1 kg	\N
14146	43	5	51	2	>1 kg - 10 kg	\N
14147	43	5	52	3	>10 kg - 100 kg	\N
14148	43	5	53	4	>100 kg - 1.000 kg	\N
14149	43	5	54	5	>1.000 kg - 10.000 kg	\N
14150	43	5	55	6	>10.000 kg - 100.000 kg	\N
14151	43	5	56	7	>100.000 kg	\N
14152	44	5	49	0	Menge	\N
14153	44	5	50	1	>0 kg - 1 kg	\N
14154	44	5	51	2	>1 kg - 10 kg	\N
14155	44	5	52	3	>10 kg - 100 kg	\N
14156	44	5	53	4	>100 kg - 1.000 kg	\N
14157	44	5	54	5	>1.000 kg - 10.000 kg	\N
14158	44	5	55	6	>10.000 kg - 100.000 kg	\N
14159	44	5	56	7	>100.000 kg	\N
14160	45	5	49	0	Menge	\N
14161	45	5	50	1	>0 kg - 1 kg	\N
14162	45	5	51	2	>1 kg - 10 kg	\N
14163	45	5	52	3	>10 kg - 100 kg	\N
14164	45	5	53	4	>100 kg - 1.000 kg	\N
14165	45	5	54	5	>1.000 kg - 10.000 kg	\N
14166	45	5	55	6	>10.000 kg - 100.000 kg	\N
14167	45	5	56	7	>100.000 kg	\N
14168	46	5	49	0	Menge	\N
14169	46	5	50	1	>0 kg - 1 kg	\N
14170	46	5	51	2	>1 kg - 10 kg	\N
14171	46	5	52	3	>10 kg - 100 kg	\N
14172	46	5	53	4	>100 kg - 1.000 kg	\N
14174	46	5	55	6	>10.000 kg - 100.000 kg	\N
14175	46	5	56	7	>100.000 kg	\N
14176	47	5	49	0	Menge	\N
14177	47	5	50	1	>0 kg - 1 kg	\N
14178	47	5	51	2	>1 kg - 10 kg	\N
14179	47	5	52	3	>10 kg - 100 kg	\N
14180	47	5	53	4	>100 kg - 1.000 kg	\N
14181	47	5	54	5	>1.000 kg - 10.000 kg	\N
14182	47	5	55	6	>10.000 kg - 100.000 kg	\N
14183	47	5	56	7	>100.000 kg	\N
14184	48	5	49	0	Menge	\N
14185	48	5	50	1	>0 kg - 1 kg	\N
14186	48	5	51	2	>1 kg - 10 kg	\N
14187	48	5	52	3	>10 kg - 100 kg	\N
14188	48	5	53	4	>100 kg - 1.000 kg	\N
14189	48	5	54	5	>1.000 kg - 10.000 kg	\N
14190	48	5	55	6	>10.000 kg - 100.000 kg	\N
14191	48	5	56	7	>100.000 kg	\N
14192	49	5	49	0	Menge	\N
14193	49	5	50	1	>0 kg - 1 kg	\N
14194	49	5	51	2	>1 kg - 10 kg	\N
14195	49	5	52	3	>10 kg - 100 kg	\N
14196	49	5	53	4	>100 kg - 1.000 kg	\N
14197	49	5	54	5	>1.000 kg - 10.000 kg	\N
14198	49	5	55	6	>10.000 kg - 100.000 kg	\N
14199	49	5	56	7	>100.000 kg	\N
14200	50	5	49	0	Menge	\N
14201	50	5	50	1	>0 kg - 1 kg	\N
14202	50	5	51	2	>1 kg - 10 kg	\N
14203	50	5	52	3	>10 kg - 100 kg	\N
14204	50	5	53	4	>100 kg - 1.000 kg	\N
14205	50	5	54	5	>1.000 kg - 10.000 kg	\N
14206	50	5	55	6	>10.000 kg - 100.000 kg	\N
14207	50	5	56	7	>100.000 kg	\N
14208	51	5	49	0	Menge	\N
14209	51	5	50	1	>0 kg - 1 kg	\N
14210	51	5	51	2	>1 kg - 10 kg	\N
14211	51	5	52	3	>10 kg - 100 kg	\N
14212	51	5	53	4	>100 kg - 1.000 kg	\N
14213	51	5	54	5	>1.000 kg - 10.000 kg	\N
14214	51	5	55	6	>10.000 kg - 100.000 kg	\N
14215	51	5	56	7	>100.000 kg	\N
14216	52	5	49	0	Menge	\N
14217	52	5	50	1	>0 kg - 1 kg	\N
14218	52	5	51	2	>1 kg - 10 kg	\N
14219	52	5	52	3	>10 kg - 100 kg	\N
14220	52	5	53	4	>100 kg - 1.000 kg	\N
14221	52	5	54	5	>1.000 kg - 10.000 kg	\N
14222	52	5	55	6	>10.000 kg - 100.000 kg	\N
14223	52	5	56	7	>100.000 kg	\N
14224	53	5	49	0	Menge	\N
14225	53	5	50	1	>0 kg - 1 kg	\N
14226	53	5	51	2	>1 kg - 10 kg	\N
14227	53	5	52	3	>10 kg - 100 kg	\N
14228	53	5	53	4	>100 kg - 1.000 kg	\N
14229	53	5	54	5	>1.000 kg - 10.000 kg	\N
14230	53	5	55	6	>10.000 kg - 100.000 kg	\N
14231	53	5	56	7	>100.000 kg	\N
14232	54	5	49	0	Menge	\N
14233	54	5	50	1	>0 kg - 1 kg	\N
14234	54	5	51	2	>1 kg - 10 kg	\N
14235	54	5	52	3	>10 kg - 100 kg	\N
14236	54	5	53	4	>100 kg - 1.000 kg	\N
14237	54	5	54	5	>1.000 kg - 10.000 kg	\N
14238	54	5	55	6	>10.000 kg - 100.000 kg	\N
14239	54	5	56	7	>100.000 kg	\N
14240	55	5	49	0	Menge	\N
14241	55	5	50	1	>0 kg - 1 kg	\N
14242	55	5	51	2	>1 kg - 10 kg	\N
14243	55	5	52	3	>10 kg - 100 kg	\N
14244	55	5	53	4	>100 kg - 1.000 kg	\N
14245	55	5	54	5	>1.000 kg - 10.000 kg	\N
14246	55	5	55	6	>10.000 kg - 100.000 kg	\N
14247	55	5	56	7	>100.000 kg	\N
14248	56	5	49	0	Menge	\N
14249	56	5	50	1	>0 kg - 1 kg	\N
14250	56	5	51	2	>1 kg - 10 kg	\N
14251	56	5	52	3	>10 kg - 100 kg	\N
14252	56	5	53	4	>100 kg - 1.000 kg	\N
14253	56	5	54	5	>1.000 kg - 10.000 kg	\N
14254	56	5	55	6	>10.000 kg - 100.000 kg	\N
14255	56	5	56	7	>100.000 kg	\N
14256	57	5	49	0	Menge	\N
14257	57	5	50	1	>0 kg - 1 kg	\N
14258	57	5	51	2	>1 kg - 10 kg	\N
14259	57	5	52	3	>10 kg - 100 kg	\N
14260	57	5	53	4	>100 kg - 1.000 kg	\N
14261	57	5	54	5	>1.000 kg - 10.000 kg	\N
14262	57	5	55	6	>10.000 kg - 100.000 kg	\N
14263	57	5	56	7	>100.000 kg	\N
14264	58	5	49	0	Menge	\N
14265	58	5	50	1	>0 kg - 1 kg	\N
14266	58	5	51	2	>1 kg - 10 kg	\N
14267	58	5	52	3	>10 kg - 100 kg	\N
14268	58	5	53	4	>100 kg - 1.000 kg	\N
14269	58	5	54	5	>1.000 kg - 10.000 kg	\N
14270	58	5	55	6	>10.000 kg - 100.000 kg	\N
14271	58	5	56	7	>100.000 kg	\N
14272	59	5	49	0	Menge	\N
14273	59	5	50	1	>0 kg - 1 kg	\N
14274	59	5	51	2	>1 kg - 10 kg	\N
14275	59	5	52	3	>10 kg - 100 kg	\N
14276	59	5	53	4	>100 kg - 1.000 kg	\N
14277	59	5	54	5	>1.000 kg - 10.000 kg	\N
14278	59	5	55	6	>10.000 kg - 100.000 kg	\N
14279	59	5	56	7	>100.000 kg	\N
14280	60	5	49	0	Menge	\N
14281	60	5	50	1	>0 kg - 1 kg	\N
14282	60	5	51	2	>1 kg - 10 kg	\N
14283	60	5	52	3	>10 kg - 100 kg	\N
14284	60	5	53	4	>100 kg - 1.000 kg	\N
14285	60	5	54	5	>1.000 kg - 10.000 kg	\N
14286	60	5	55	6	>10.000 kg - 100.000 kg	\N
14287	60	5	56	7	>100.000 kg	\N
14288	61	5	49	0	Menge	\N
14289	61	5	50	1	>0 kg - 1 kg	\N
14290	61	5	51	2	>1 kg - 10 kg	\N
14291	61	5	52	3	>10 kg - 100 kg	\N
14292	61	5	53	4	>100 kg - 1.000 kg	\N
14293	61	5	54	5	>1.000 kg - 10.000 kg	\N
14294	61	5	55	6	>10.000 kg - 100.000 kg	\N
14295	61	5	56	7	>100.000 kg	\N
14296	62	5	49	0	Menge	\N
14297	62	5	50	1	>0 kg - 1 kg	\N
14298	62	5	51	2	>1 kg - 10 kg	\N
14299	62	5	52	3	>10 kg - 100 kg	\N
14300	62	5	53	4	>100 kg - 1.000 kg	\N
14301	62	5	54	5	>1.000 kg - 10.000 kg	\N
14302	62	5	55	6	>10.000 kg - 100.000 kg	\N
14303	62	5	56	7	>100.000 kg	\N
14304	63	5	49	0	Menge	\N
14305	63	5	50	1	>0 kg - 1 kg	\N
14306	63	5	51	2	>1 kg - 10 kg	\N
14307	63	5	52	3	>10 kg - 100 kg	\N
14308	63	5	53	4	>100 kg - 1.000 kg	\N
14309	63	5	54	5	>1.000 kg - 10.000 kg	\N
14310	63	5	55	6	>10.000 kg - 100.000 kg	\N
14311	63	5	56	7	>100.000 kg	\N
14312	64	5	49	0	Menge	\N
14313	64	5	50	1	>0 kg - 1 kg	\N
14314	64	5	51	2	>1 kg - 10 kg	\N
14315	64	5	52	3	>10 kg - 100 kg	\N
14316	64	5	53	4	>100 kg - 1.000 kg	\N
14317	64	5	54	5	>1.000 kg - 10.000 kg	\N
14318	64	5	55	6	>10.000 kg - 100.000 kg	\N
14319	64	5	56	7	>100.000 kg	\N
14320	65	5	49	0	Menge	\N
14321	65	5	50	1	>0 kg - 1 kg	\N
14322	65	5	51	2	>1 kg - 10 kg	\N
14323	65	5	52	3	>10 kg - 100 kg	\N
14324	65	5	53	4	>100 kg - 1.000 kg	\N
14325	65	5	54	5	>1.000 kg - 10.000 kg	\N
14326	65	5	55	6	>10.000 kg - 100.000 kg	\N
14327	65	5	56	7	>100.000 kg	\N
14328	66	5	49	0	Menge	\N
14329	66	5	50	1	>0 kg - 1 kg	\N
14330	66	5	51	2	>1 kg - 10 kg	\N
14331	66	5	52	3	>10 kg - 100 kg	\N
14332	66	5	53	4	>100 kg - 1.000 kg	\N
14333	66	5	54	5	>1.000 kg - 10.000 kg	\N
14334	66	5	55	6	>10.000 kg - 100.000 kg	\N
14335	66	5	56	7	>100.000 kg	\N
14336	68	5	49	0	Menge	\N
14337	68	5	50	1	>0 kg - 1 kg	\N
14338	68	5	51	2	>1 kg - 10 kg	\N
14339	68	5	52	3	>10 kg - 100 kg	\N
14340	68	5	53	4	>100 kg - 1.000 kg	\N
14341	68	5	54	5	>1.000 kg - 10.000 kg	\N
14342	68	5	55	6	>10.000 kg - 100.000 kg	\N
14343	68	5	56	7	>100.000 kg	\N
14344	69	5	49	0	Menge	\N
14345	69	5	50	1	>0 kg - 1 kg	\N
14346	69	5	51	2	>1 kg - 10 kg	\N
14347	69	5	52	3	>10 kg - 100 kg	\N
14348	69	5	53	4	>100 kg - 1.000 kg	\N
14349	69	5	54	5	>1.000 kg - 10.000 kg	\N
14350	69	5	55	6	>10.000 kg - 100.000 kg	\N
14351	69	5	56	7	>100.000 kg	\N
14352	70	5	49	0	Menge	\N
14353	70	5	50	1	>0 kg - 1 kg	\N
14354	70	5	51	2	>1 kg - 10 kg	\N
14355	70	5	52	3	>10 kg - 100 kg	\N
14356	70	5	53	4	>100 kg - 1.000 kg	\N
14357	70	5	54	5	>1.000 kg - 10.000 kg	\N
14358	70	5	55	6	>10.000 kg - 100.000 kg	\N
14359	70	5	56	7	>100.000 kg	\N
14360	71	5	49	0	Menge	\N
14361	71	5	50	1	>0 kg - 1 kg	\N
14362	71	5	51	2	>1 kg - 10 kg	\N
14363	71	5	52	3	>10 kg - 100 kg	\N
14364	71	5	53	4	>100 kg - 1.000 kg	\N
14365	71	5	54	5	>1.000 kg - 10.000 kg	\N
14366	71	5	55	6	>10.000 kg - 100.000 kg	\N
14367	71	5	56	7	>100.000 kg	\N
14368	72	5	49	0	Menge	\N
14369	72	5	50	1	>0 kg - 1 kg	\N
14370	72	5	51	2	>1 kg - 10 kg	\N
14371	72	5	52	3	>10 kg - 100 kg	\N
14372	72	5	53	4	>100 kg - 1.000 kg	\N
14373	72	5	54	5	>1.000 kg - 10.000 kg	\N
14374	72	5	55	6	>10.000 kg - 100.000 kg	\N
14375	72	5	56	7	>100.000 kg	\N
14376	73	5	10158	0	Wasserfassung (bei Eigen-,Einzel-WV)	\N
14377	73	5	10159	1	Flachbrunnen (<20m)	\N
14378	73	5	10160	2	Tiefbrunnen  (>20m)	\N
14379	73	5	10161	3	Schachtbrunnen	\N
14380	73	5	10162	4	Quellfassung	\N
14381	73	5	10163	5	Oberflächenwasserdirektentnahme	\N
14382	81	5	11044	0	Beruf	\N
14383	81	5	11045	1	Referenzgruppe	\N
14384	81	5	11046	2	Beruflich Strahlenexponierte	\N
14385	81	5	11047	3	Beschäftigt bei: Deponie	\N
14386	81	5	11048	4	Beschäftigt bei: Müllverbrennung	\N
14387	81	5	11049	5	Beschäftigt bei: Kläranlage	\N
14388	81	5	11050	6	Beschäftigt bei: Landwirtschaft	\N
14389	82	5	11044	0	Beruf	\N
14390	82	5	11045	1	Referenzgruppe	\N
14391	82	5	11046	2	Beruflich Strahlenexponierte	\N
14392	82	5	11047	3	Beschäftigt bei: Deponie	\N
14393	82	5	11048	4	Beschäftigt bei: Müllverbrennung	\N
14394	82	5	11049	5	Beschäftigt bei: Kläranlage	\N
14395	82	5	11050	6	Beschäftigt bei: Landwirtschaft	\N
14396	83	5	11044	0	Beruf	\N
14397	83	5	11045	1	Referenzgruppe	\N
14398	83	5	11046	2	Beruflich Strahlenexponierte	\N
14399	83	5	11047	3	Beschäftigt bei: Deponie	\N
14400	83	5	11048	4	Beschäftigt bei: Müllverbrennung	\N
14401	83	5	11049	5	Beschäftigt bei: Kläranlage	\N
14402	83	5	11050	6	Beschäftigt bei: Landwirtschaft	\N
14403	84	5	11044	0	Beruf	\N
14404	84	5	11045	1	Referenzgruppe	\N
14405	84	5	11046	2	Beruflich Strahlenexponierte	\N
14406	84	5	11047	3	Beschäftigt bei: Deponie	\N
14407	84	5	11048	4	Beschäftigt bei: Müllverbrennung	\N
14408	84	5	11049	5	Beschäftigt bei: Kläranlage	\N
14409	84	5	11050	6	Beschäftigt bei: Landwirtschaft	\N
14410	87	5	49	0	Menge	\N
14411	87	5	50	1	>0 kg - 1 kg	\N
14412	87	5	51	2	>1 kg - 10 kg	\N
14413	87	5	52	3	>10 kg - 100 kg	\N
14414	87	5	53	4	>100 kg - 1.000 kg	\N
14415	87	5	54	5	>1.000 kg - 10.000 kg	\N
14416	87	5	55	6	>10.000 kg - 100.000 kg	\N
14417	87	5	56	7	>100.000 kg	\N
14418	88	5	49	0	Menge	\N
14419	88	5	50	1	>0 kg - 1 kg	\N
14420	88	5	51	2	>1 kg - 10 kg	\N
14421	88	5	52	3	>10 kg - 100 kg	\N
14422	88	5	53	4	>100 kg - 1.000 kg	\N
14423	88	5	54	5	>1.000 kg - 10.000 kg	\N
14424	88	5	55	6	>10.000 kg - 100.000 kg	\N
14425	88	5	56	7	>100.000 kg	\N
14426	89	5	49	0	Menge	\N
14427	89	5	50	1	>0 kg - 1 kg	\N
14428	89	5	51	2	>1 kg - 10 kg	\N
14429	89	5	52	3	>10 kg - 100 kg	\N
14430	89	5	53	4	>100 kg - 1.000 kg	\N
14431	89	5	54	5	>1.000 kg - 10.000 kg	\N
14432	89	5	55	6	>10.000 kg - 100.000 kg	\N
14433	89	5	56	7	>100.000 kg	\N
14434	90	5	49	0	Menge	\N
14435	90	5	50	1	>0 kg - 1 kg	\N
14436	90	5	51	2	>1 kg - 10 kg	\N
14437	90	5	52	3	>10 kg - 100 kg	\N
14438	90	5	53	4	>100 kg - 1.000 kg	\N
14439	90	5	54	5	>1.000 kg - 10.000 kg	\N
14440	90	5	55	6	>10.000 kg - 100.000 kg	\N
14441	90	5	56	7	>100.000 kg	\N
14442	91	5	49	0	Menge	\N
14443	91	5	50	1	>0 kg - 1 kg	\N
14444	91	5	51	2	>1 kg - 10 kg	\N
14445	91	5	52	3	>10 kg - 100 kg	\N
14446	91	5	53	4	>100 kg - 1.000 kg	\N
14447	91	5	54	5	>1.000 kg - 10.000 kg	\N
14448	91	5	55	6	>10.000 kg - 100.000 kg	\N
14449	91	5	56	7	>100.000 kg	\N
14450	92	5	49	0	Menge	\N
14451	92	5	50	1	>0 kg - 1 kg	\N
14452	92	5	51	2	>1 kg - 10 kg	\N
14453	92	5	52	3	>10 kg - 100 kg	\N
14454	92	5	53	4	>100 kg - 1.000 kg	\N
14455	92	5	54	5	>1.000 kg - 10.000 kg	\N
14456	92	5	55	6	>10.000 kg - 100.000 kg	\N
14457	92	5	56	7	>100.000 kg	\N
14458	93	5	49	0	Menge	\N
14459	93	5	50	1	>0 kg - 1 kg	\N
14460	93	5	51	2	>1 kg - 10 kg	\N
14461	93	5	52	3	>10 kg - 100 kg	\N
14462	93	5	53	4	>100 kg - 1.000 kg	\N
14463	93	5	54	5	>1.000 kg - 10.000 kg	\N
14464	93	5	55	6	>10.000 kg - 100.000 kg	\N
14465	93	5	56	7	>100.000 kg	\N
14466	94	5	49	0	Menge	\N
14467	94	5	50	1	>0 kg - 1 kg	\N
14468	94	5	51	2	>1 kg - 10 kg	\N
14469	94	5	52	3	>10 kg - 100 kg	\N
14470	94	5	53	4	>100 kg - 1.000 kg	\N
14471	94	5	54	5	>1.000 kg - 10.000 kg	\N
14472	94	5	55	6	>10.000 kg - 100.000 kg	\N
14473	94	5	56	7	>100.000 kg	\N
14474	108	5	12854	0	Entnahmetiefe bis	\N
14475	108	5	12868	1	01 cm Tiefe	\N
14476	108	5	12869	2	02 cm Tiefe	\N
14477	108	5	12870	3	03 cm Tiefe	\N
14478	108	5	12871	4	04 cm Tiefe	\N
14479	108	5	12872	5	05 cm Tiefe	\N
14480	108	5	12873	6	06 cm Tiefe	\N
14481	108	5	12874	7	07 cm Tiefe	\N
14482	108	5	12875	8	08 cm Tiefe	\N
14483	108	5	12876	9	09 cm Tiefe	\N
14484	108	5	12877	10	10 cm Tiefe	\N
14485	108	5	12878	11	11 cm Tiefe	\N
14486	108	5	12879	12	12 cm Tiefe	\N
14487	108	5	12880	13	13 cm Tiefe	\N
14488	108	5	12881	14	14 cm Tiefe	\N
14489	108	5	12882	15	15 cm Tiefe	\N
14490	108	5	12883	16	16 cm Tiefe	\N
14491	108	5	12884	17	17 cm Tiefe	\N
14492	108	5	12885	18	18 cm Tiefe	\N
14493	108	5	12886	19	19 cm Tiefe	\N
14494	108	5	12887	20	20 cm Tiefe	\N
14495	108	5	12888	21	21 cm Tiefe	\N
14496	108	5	12889	22	22 cm Tiefe	\N
14497	108	5	12890	23	23 cm Tiefe	\N
14498	108	5	12891	24	24 cm Tiefe	\N
14499	108	5	12892	25	25 cm Tiefe	\N
14500	108	5	12893	26	26 cm Tiefe	\N
14501	108	5	12894	27	27 cm Tiefe	\N
14502	108	5	12895	28	28 cm Tiefe	\N
14503	108	5	12896	29	29 cm Tiefe	\N
14504	108	5	12897	30	30 cm Tiefe	\N
14505	108	5	12898	31	31 cm Tiefe	\N
14506	108	5	12899	32	32 cm Tiefe	\N
14507	108	5	12900	33	33 cm Tiefe	\N
14508	108	5	12901	34	34 cm Tiefe	\N
14509	108	5	12902	35	35 cm Tiefe	\N
14510	108	5	12903	36	36 cm Tiefe	\N
14511	108	5	12904	37	37 cm Tiefe	\N
14512	108	5	12905	38	38 cm Tiefe	\N
14513	108	5	12906	39	39 cm Tiefe	\N
14514	108	5	12907	40	40 cm Tiefe	\N
14515	108	5	12908	41	41 cm Tiefe	\N
14516	108	5	12909	42	42 cm Tiefe	\N
14517	108	5	12910	43	43 cm Tiefe	\N
14518	108	5	12911	44	44 cm Tiefe	\N
14519	108	5	12912	45	45 cm Tiefe	\N
14520	108	5	12913	46	46 cm Tiefe	\N
14521	108	5	12914	47	47 cm Tiefe	\N
14522	108	5	12915	48	48 cm Tiefe	\N
14523	108	5	12916	49	49 cm Tiefe	\N
14524	108	5	12917	50	50 cm Tiefe	\N
14525	108	5	12918	51	51 cm Tiefe	\N
14526	108	5	12919	52	52 cm Tiefe	\N
14527	108	5	12920	53	53 cm Tiefe	\N
14528	108	5	12921	54	54 cm Tiefe	\N
14529	108	5	12922	55	55 cm Tiefe	\N
14530	108	5	12923	56	56 cm Tiefe	\N
14531	108	5	12924	57	57 cm Tiefe	\N
14532	108	5	12925	58	58 cm Tiefe	\N
14533	108	5	12926	59	59 cm Tiefe	\N
14534	108	5	12927	60	60 cm Tiefe	\N
14535	108	5	12928	61	61 cm Tiefe	\N
14536	108	5	12929	62	62 cm Tiefe	\N
14537	108	5	12930	63	63 cm Tiefe	\N
14538	108	5	12931	64	64 cm Tiefe	\N
14539	108	5	12932	65	65 cm Tiefe	\N
14540	108	5	12933	66	66 cm Tiefe	\N
14541	108	5	12934	67	67 cm Tiefe	\N
14542	108	5	12935	68	68 cm Tiefe	\N
14543	108	5	12936	69	69 cm Tiefe	\N
14544	108	5	12937	70	70 cm Tiefe	\N
14545	108	5	12938	71	71 cm Tiefe	\N
14546	108	5	12939	72	72 cm Tiefe	\N
14547	108	5	12940	73	73 cm Tiefe	\N
14548	108	5	12941	74	74 cm Tiefe	\N
14549	108	5	12942	75	75 cm Tiefe	\N
14550	108	5	12943	76	76 cm Tiefe	\N
14551	108	5	12944	77	77 cm Tiefe	\N
14552	108	5	12945	78	78 cm Tiefe	\N
14553	108	5	12946	79	79 cm Tiefe	\N
14554	108	5	12947	80	80 cm Tiefe	\N
14555	108	5	12948	81	81 cm Tiefe	\N
14556	108	5	12949	82	82 cm Tiefe	\N
14557	108	5	12950	83	83 cm Tiefe	\N
14558	108	5	12951	84	84 cm Tiefe	\N
14559	108	5	12952	85	85 cm Tiefe	\N
14560	108	5	12953	86	86 cm Tiefe	\N
14561	108	5	12954	87	87 cm Tiefe	\N
14562	108	5	12955	88	88 cm Tiefe	\N
14563	108	5	12956	89	89 cm Tiefe	\N
14564	108	5	12957	90	90 cm Tiefe	\N
14565	108	5	12958	91	91 cm Tiefe	\N
14566	108	5	12959	92	92 cm Tiefe	\N
14567	108	5	12960	93	93 cm Tiefe	\N
14568	108	5	12961	94	94 cm Tiefe	\N
14569	108	5	12962	95	95 cm Tiefe	\N
14570	108	5	12963	96	96 cm Tiefe	\N
14571	108	5	12964	97	97 cm Tiefe	\N
14572	108	5	12965	98	98 cm Tiefe	\N
14573	108	5	12966	99	Tiefenangabe im Messwertteil	\N
14574	120	5	12854	0	Entnahmetiefe bis	\N
14575	120	5	12855	1	Oberfläche	\N
14576	120	5	12856	2	0,2 m	\N
14577	120	5	12857	3	0,5 m	\N
14578	120	5	12858	4	1 m	\N
14579	120	5	12859	5	2 m	\N
14580	120	5	12860	6	5 m	\N
14581	120	5	12861	7	10 m	\N
14582	120	5	12862	8	20 m	\N
14583	120	5	12863	9	30 m	\N
14584	120	5	12864	10	40 m	\N
14585	120	5	12865	11	50 m	\N
14586	120	5	12866	12	60 m	\N
14587	120	5	12867	13	> 60 m	\N
14588	143	5	12828	0	Grundwasserschranke	\N
14589	143	5	12829	1	Nicht vorhanden	\N
14590	143	5	12830	2	Nicht vorhanden und Grundwasserspiegel angeschnitten	\N
14591	143	5	12831	3	Natürliche Sperrschicht	\N
14592	143	5	12832	4	Bauliche Maßnahmen	Bitumenschicht, Folien, eingebrachte Lehm- und/oder Tonschichten
14593	143	5	12833	5	Bauliche Maßnahmen und Grundwasserspiegel angeschnitten	Bitumenschicht, Folien, eingebrachte Lehm- und/oder Tonschichten
14594	144	5	12851	0	Kompostierungsverfahren	\N
14595	144	5	12852	1	Offen	Unter freiem Himmel
14596	144	5	12853	2	Abgedeckt	Hallen, Überdachungen, usw.
14597	25	6	1328	0	Verarbeitung	\N
14598	25	6	1329	1	unbehandelt	roh, wie eingekauft
14599	25	6	1330	2	küchenfertig	gewaschen, geputzt, nicht essbarer Teil entfernt
14600	25	6	1331	3	garfertig	Lebensmittel werden nur noch gegart
14601	25	6	1332	4	aufbereitfertig	Zutaten werden noch benötigt, z.B, Flüssigkeiten oder Lebensmittel müssen auf Verzehrtemperatur gebracht werden
14602	25	6	1333	5	verzehrsfertig	\N
14603	25	6	1334	6	tiefgefroren	\N
14604	25	6	1335	10	Maische	\N
14605	25	6	1336	11	Trester	\N
14606	25	6	1337	12	Most	\N
14607	25	6	1338	13	Mark	\N
14608	25	6	1339	14	Mus	\N
14609	26	6	1328	0	Verarbeitung	\N
14610	26	6	1329	1	unbehandelt	roh, wie eingekauft
14611	26	6	1330	2	küchenfertig	gewaschen, geputzt, nicht essbarer Teil entfernt
14612	26	6	1331	3	garfertig	Lebensmittel werden nur noch gegart
14613	26	6	1332	4	aufbereitfertig	Zutaten werden noch benötigt, z.B, Flüssigkeiten oder Lebensmittel müssen auf Verzehrtemperatur gebracht werden
14614	26	6	1333	5	verzehrsfertig	\N
14615	26	6	1334	6	tiefgefroren	\N
14616	26	6	1335	10	Maische	\N
14617	26	6	1336	11	Trester	\N
14618	26	6	1337	12	Most	\N
14619	26	6	1338	13	Mark	\N
14620	26	6	1339	14	Mus	\N
14621	27	6	1328	0	Verarbeitung	\N
14622	27	6	1329	1	unbehandelt	roh, wie eingekauft
14623	27	6	1330	2	küchenfertig	gewaschen, geputzt, nicht essbarer Teil entfernt
14624	27	6	1331	3	garfertig	Lebensmittel werden nur noch gegart
14625	27	6	1332	4	aufbereitfertig	Zutaten werden noch benötigt, z.B, Flüssigkeiten oder Lebensmittel müssen auf Verzehrtemperatur gebracht werden
14626	27	6	1333	5	verzehrsfertig	\N
14627	27	6	1334	6	tiefgefroren	\N
14628	27	6	1335	10	Maische	\N
14629	27	6	1336	11	Trester	\N
14630	27	6	1337	12	Most	\N
14631	27	6	1338	13	Mark	\N
14632	27	6	1339	14	Mus	\N
14633	28	6	1328	0	Verarbeitung	\N
14634	28	6	1329	1	unbehandelt	roh, wie eingekauft
14635	28	6	1330	2	küchenfertig	gewaschen, geputzt, nicht essbarer Teil entfernt
14636	28	6	1331	3	garfertig	Lebensmittel werden nur noch gegart
14637	28	6	1332	4	aufbereitfertig	Zutaten werden noch benötigt, z.B, Flüssigkeiten oder Lebensmittel müssen auf Verzehrtemperatur gebracht werden
14638	28	6	1333	5	verzehrsfertig	\N
14639	28	6	1334	6	tiefgefroren	\N
14640	28	6	1335	10	Maische	\N
14641	28	6	1336	11	Trester	\N
14642	28	6	1337	12	Most	\N
14643	28	6	1338	13	Mark	\N
14644	28	6	1339	14	Mus	\N
14645	29	6	1328	0	Verarbeitung	\N
14646	29	6	1329	1	unbehandelt	roh, wie eingekauft
14647	29	6	1330	2	küchenfertig	gewaschen, geputzt, nicht essbarer Teil entfernt
14648	29	6	1331	3	garfertig	Lebensmittel werden nur noch gegart
14649	29	6	1332	4	aufbereitfertig	Zutaten werden noch benötigt, z.B, Flüssigkeiten oder Lebensmittel müssen auf Verzehrtemperatur gebracht werden
14650	29	6	1333	5	verzehrsfertig	\N
14651	29	6	1334	6	tiefgefroren	\N
14652	29	6	1335	10	Maische	\N
14653	29	6	1336	11	Trester	\N
14654	29	6	1337	12	Most	\N
14655	29	6	1338	13	Mark	\N
14656	29	6	1339	14	Mus	\N
14657	30	6	1328	0	Verarbeitung	\N
14658	30	6	1329	1	unbehandelt	roh, wie eingekauft
14659	30	6	1330	2	küchenfertig	gewaschen, geputzt, nicht essbarer Teil entfernt
14660	30	6	1331	3	garfertig	Lebensmittel werden nur noch gegart
14661	30	6	1332	4	aufbereitfertig	Zutaten werden noch benötigt, z.B, Flüssigkeiten oder Lebensmittel müssen auf Verzehrtemperatur gebracht werden
14662	30	6	1333	5	verzehrsfertig	\N
14663	30	6	1334	6	tiefgefroren	\N
14664	30	6	1335	10	Maische	\N
14665	30	6	1336	11	Trester	\N
14666	30	6	1337	12	Most	\N
14667	30	6	1338	13	Mark	\N
14668	30	6	1339	14	Mus	\N
14669	32	6	1328	0	Verarbeitung	\N
14670	32	6	1329	1	unbehandelt	roh, wie eingekauft
14671	32	6	1330	2	küchenfertig	gewaschen, geputzt, nicht essbarer Teil entfernt
14672	32	6	1331	3	garfertig	Lebensmittel werden nur noch gegart
14673	32	6	1332	4	aufbereitfertig	Zutaten werden noch benötigt, z.B, Flüssigkeiten oder Lebensmittel müssen auf Verzehrtemperatur gebracht werden
14674	32	6	1333	5	verzehrsfertig	\N
14675	32	6	1334	6	tiefgefroren	\N
14676	32	6	1335	10	Maische	\N
14677	32	6	1336	11	Trester	\N
14678	32	6	1337	12	Most	\N
14679	32	6	1338	13	Mark	\N
14680	32	6	1339	14	Mus	\N
14681	33	6	1328	0	Verarbeitung	\N
14682	33	6	1329	1	unbehandelt	roh, wie eingekauft
14683	33	6	1330	2	küchenfertig	gewaschen, geputzt, nicht essbarer Teil entfernt
14684	33	6	1331	3	garfertig	Lebensmittel werden nur noch gegart
14685	33	6	1332	4	aufbereitfertig	Zutaten werden noch benötigt, z.B, Flüssigkeiten oder Lebensmittel müssen auf Verzehrtemperatur gebracht werden
14686	33	6	1333	5	verzehrsfertig	\N
14687	33	6	1334	6	tiefgefroren	\N
14688	33	6	1335	10	Maische	\N
14689	33	6	1336	11	Trester	\N
14690	33	6	1337	12	Most	\N
14691	33	6	1338	13	Mark	\N
14692	33	6	1339	14	Mus	\N
14693	34	6	1328	0	Verarbeitung	\N
14694	34	6	1329	1	unbehandelt	roh, wie eingekauft
14695	34	6	1330	2	küchenfertig	gewaschen, geputzt, nicht essbarer Teil entfernt
14696	34	6	1331	3	garfertig	Lebensmittel werden nur noch gegart
14697	34	6	1332	4	aufbereitfertig	Zutaten werden noch benötigt, z.B, Flüssigkeiten oder Lebensmittel müssen auf Verzehrtemperatur gebracht werden
14698	34	6	1333	5	verzehrsfertig	\N
14699	34	6	1334	6	tiefgefroren	\N
14700	34	6	1335	10	Maische	\N
14701	34	6	1336	11	Trester	\N
14702	34	6	1337	12	Most	\N
14703	34	6	1338	13	Mark	\N
14704	34	6	1339	14	Mus	\N
14705	35	6	1328	0	Verarbeitung	\N
14706	35	6	1329	1	unbehandelt	roh, wie eingekauft
14707	35	6	1330	2	küchenfertig	gewaschen, geputzt, nicht essbarer Teil entfernt
14708	35	6	1331	3	garfertig	Lebensmittel werden nur noch gegart
14709	35	6	1332	4	aufbereitfertig	Zutaten werden noch benötigt, z.B, Flüssigkeiten oder Lebensmittel müssen auf Verzehrtemperatur gebracht werden
14710	35	6	1333	5	verzehrsfertig	\N
14711	35	6	1334	6	tiefgefroren	\N
14712	35	6	1335	10	Maische	\N
14713	35	6	1336	11	Trester	\N
14714	35	6	1337	12	Most	\N
14715	35	6	1338	13	Mark	\N
14716	35	6	1339	14	Mus	\N
14717	36	6	1328	0	Verarbeitung	\N
14718	36	6	1329	1	unbehandelt	roh, wie eingekauft
14719	36	6	1330	2	küchenfertig	gewaschen, geputzt, nicht essbarer Teil entfernt
14720	36	6	1331	3	garfertig	Lebensmittel werden nur noch gegart
14721	36	6	1332	4	aufbereitfertig	Zutaten werden noch benötigt, z.B, Flüssigkeiten oder Lebensmittel müssen auf Verzehrtemperatur gebracht werden
14722	36	6	1333	5	verzehrsfertig	\N
14723	36	6	1334	6	tiefgefroren	\N
14724	36	6	1335	10	Maische	\N
14725	36	6	1336	11	Trester	\N
14726	36	6	1337	12	Most	\N
14727	36	6	1338	13	Mark	\N
14728	36	6	1339	14	Mus	\N
14729	37	6	1328	0	Verarbeitung	\N
14730	37	6	1329	1	unbehandelt	roh, wie eingekauft
14731	37	6	1330	2	küchenfertig	gewaschen, geputzt, nicht essbarer Teil entfernt
14732	37	6	1331	3	garfertig	Lebensmittel werden nur noch gegart
14733	37	6	1332	4	aufbereitfertig	Zutaten werden noch benötigt, z.B, Flüssigkeiten oder Lebensmittel müssen auf Verzehrtemperatur gebracht werden
14734	37	6	1333	5	verzehrsfertig	\N
14735	37	6	1334	6	tiefgefroren	\N
14736	37	6	1335	10	Maische	\N
14737	37	6	1336	11	Trester	\N
14738	37	6	1337	12	Most	\N
14739	37	6	1338	13	Mark	\N
14740	37	6	1339	14	Mus	\N
14741	38	6	1328	0	Verarbeitung	\N
14742	38	6	1329	1	unbehandelt	roh, wie eingekauft
14743	38	6	1330	2	küchenfertig	gewaschen, geputzt, nicht essbarer Teil entfernt
14744	38	6	1331	3	garfertig	Lebensmittel werden nur noch gegart
14745	38	6	1332	4	aufbereitfertig	Zutaten werden noch benötigt, z.B, Flüssigkeiten oder Lebensmittel müssen auf Verzehrtemperatur gebracht werden
14746	38	6	1333	5	verzehrsfertig	\N
14747	38	6	1334	6	tiefgefroren	\N
14748	38	6	1335	10	Maische	\N
14749	38	6	1336	11	Trester	\N
14750	38	6	1337	12	Most	\N
14751	38	6	1338	13	Mark	\N
14752	38	6	1339	14	Mus	\N
14753	39	6	1328	0	Verarbeitung	\N
14754	39	6	1329	1	unbehandelt	roh, wie eingekauft
14755	39	6	1330	2	küchenfertig	gewaschen, geputzt, nicht essbarer Teil entfernt
14756	39	6	1331	3	garfertig	Lebensmittel werden nur noch gegart
14757	39	6	1332	4	aufbereitfertig	Zutaten werden noch benötigt, z.B, Flüssigkeiten oder Lebensmittel müssen auf Verzehrtemperatur gebracht werden
14758	39	6	1333	5	verzehrsfertig	\N
14759	39	6	1334	6	tiefgefroren	\N
14760	39	6	1335	10	Maische	\N
14761	39	6	1336	11	Trester	\N
14762	39	6	1337	12	Most	\N
14763	39	6	1338	13	Mark	\N
14764	39	6	1339	14	Mus	\N
14765	40	6	1328	0	Verarbeitung	\N
14766	40	6	1329	1	unbehandelt	roh, wie eingekauft
14767	40	6	1330	2	küchenfertig	gewaschen, geputzt, nicht essbarer Teil entfernt
14768	40	6	1331	3	garfertig	Lebensmittel werden nur noch gegart
14769	40	6	1332	4	aufbereitfertig	Zutaten werden noch benötigt, z.B, Flüssigkeiten oder Lebensmittel müssen auf Verzehrtemperatur gebracht werden
14770	40	6	1333	5	verzehrsfertig	\N
14771	40	6	1334	6	tiefgefroren	\N
14772	40	6	1335	10	Maische	\N
14773	40	6	1336	11	Trester	\N
14774	40	6	1337	12	Most	\N
14775	40	6	1338	13	Mark	\N
14776	40	6	1339	14	Mus	\N
14777	41	6	1328	0	Verarbeitung	\N
14778	41	6	1329	1	unbehandelt	roh, wie eingekauft
14779	41	6	1330	2	küchenfertig	gewaschen, geputzt, nicht essbarer Teil entfernt
14780	41	6	1331	3	garfertig	Lebensmittel werden nur noch gegart
14781	41	6	1332	4	aufbereitfertig	Zutaten werden noch benötigt, z.B, Flüssigkeiten oder Lebensmittel müssen auf Verzehrtemperatur gebracht werden
14782	41	6	1333	5	verzehrsfertig	\N
14783	41	6	1334	6	tiefgefroren	\N
14784	41	6	1335	10	Maische	\N
14785	41	6	1336	11	Trester	\N
14786	41	6	1337	12	Most	\N
14787	41	6	1338	13	Mark	\N
14788	41	6	1339	14	Mus	\N
14789	42	6	1328	0	Verarbeitung	\N
14790	42	6	1329	1	unbehandelt	roh, wie eingekauft
14791	42	6	1330	2	küchenfertig	gewaschen, geputzt, nicht essbarer Teil entfernt
14792	42	6	1331	3	garfertig	Lebensmittel werden nur noch gegart
14793	42	6	1332	4	aufbereitfertig	Zutaten werden noch benötigt, z.B, Flüssigkeiten oder Lebensmittel müssen auf Verzehrtemperatur gebracht werden
14794	42	6	1333	5	verzehrsfertig	\N
14795	42	6	1334	6	tiefgefroren	\N
14796	42	6	1335	10	Maische	\N
14797	42	6	1336	11	Trester	\N
14798	42	6	1337	12	Most	\N
14799	42	6	1338	13	Mark	\N
14800	42	6	1339	14	Mus	\N
14801	43	6	1328	0	Verarbeitung	\N
14802	43	6	1329	1	unbehandelt	roh, wie eingekauft
14803	43	6	1330	2	küchenfertig	gewaschen, geputzt, nicht essbarer Teil entfernt
14804	43	6	1331	3	garfertig	Lebensmittel werden nur noch gegart
14805	43	6	1332	4	aufbereitfertig	Zutaten werden noch benötigt, z.B, Flüssigkeiten oder Lebensmittel müssen auf Verzehrtemperatur gebracht werden
14806	43	6	1333	5	verzehrsfertig	\N
14807	43	6	1334	6	tiefgefroren	\N
14808	43	6	1335	10	Maische	\N
14809	43	6	1336	11	Trester	\N
14810	43	6	1337	12	Most	\N
14811	43	6	1338	13	Mark	\N
14812	43	6	1339	14	Mus	\N
14813	44	6	1328	0	Verarbeitung	\N
14814	44	6	1329	1	unbehandelt	roh, wie eingekauft
14815	44	6	1330	2	küchenfertig	gewaschen, geputzt, nicht essbarer Teil entfernt
14816	44	6	1331	3	garfertig	Lebensmittel werden nur noch gegart
14817	44	6	1332	4	aufbereitfertig	Zutaten werden noch benötigt, z.B, Flüssigkeiten oder Lebensmittel müssen auf Verzehrtemperatur gebracht werden
14818	44	6	1333	5	verzehrsfertig	\N
14819	44	6	1334	6	tiefgefroren	\N
14820	44	6	1335	10	Maische	\N
14821	44	6	1336	11	Trester	\N
14822	44	6	1337	12	Most	\N
14823	44	6	1338	13	Mark	\N
14824	44	6	1339	14	Mus	\N
14825	45	6	1328	0	Verarbeitung	\N
14826	45	6	1329	1	unbehandelt	roh, wie eingekauft
14827	45	6	1330	2	küchenfertig	gewaschen, geputzt, nicht essbarer Teil entfernt
14828	45	6	1331	3	garfertig	Lebensmittel werden nur noch gegart
14829	45	6	1332	4	aufbereitfertig	Zutaten werden noch benötigt, z.B, Flüssigkeiten oder Lebensmittel müssen auf Verzehrtemperatur gebracht werden
14830	45	6	1333	5	verzehrsfertig	\N
14831	45	6	1334	6	tiefgefroren	\N
14832	45	6	1335	10	Maische	\N
14833	45	6	1336	11	Trester	\N
14834	45	6	1337	12	Most	\N
14835	45	6	1338	13	Mark	\N
14836	45	6	1339	14	Mus	\N
14837	46	6	1328	0	Verarbeitung	\N
14838	46	6	1329	1	unbehandelt	roh, wie eingekauft
14839	46	6	1330	2	küchenfertig	gewaschen, geputzt, nicht essbarer Teil entfernt
14840	46	6	1331	3	garfertig	Lebensmittel werden nur noch gegart
14841	46	6	1332	4	aufbereitfertig	Zutaten werden noch benötigt, z.B, Flüssigkeiten oder Lebensmittel müssen auf Verzehrtemperatur gebracht werden
14842	46	6	1333	5	verzehrsfertig	\N
14843	46	6	1334	6	tiefgefroren	\N
14844	46	6	1335	10	Maische	\N
14845	46	6	1336	11	Trester	\N
14846	46	6	1337	12	Most	\N
14847	46	6	1338	13	Mark	\N
14848	46	6	1339	14	Mus	\N
14849	47	6	1328	0	Verarbeitung	\N
14850	47	6	1329	1	unbehandelt	roh, wie eingekauft
14851	47	6	1330	2	küchenfertig	gewaschen, geputzt, nicht essbarer Teil entfernt
14852	47	6	1331	3	garfertig	Lebensmittel werden nur noch gegart
14853	47	6	1332	4	aufbereitfertig	Zutaten werden noch benötigt, z.B, Flüssigkeiten oder Lebensmittel müssen auf Verzehrtemperatur gebracht werden
14854	47	6	1333	5	verzehrsfertig	\N
14855	47	6	1334	6	tiefgefroren	\N
14856	47	6	1335	10	Maische	\N
14857	47	6	1336	11	Trester	\N
14858	47	6	1337	12	Most	\N
14859	47	6	1338	13	Mark	\N
14860	47	6	1339	14	Mus	\N
14861	48	6	1328	0	Verarbeitung	\N
14862	48	6	1329	1	unbehandelt	roh, wie eingekauft
14863	48	6	1330	2	küchenfertig	gewaschen, geputzt, nicht essbarer Teil entfernt
14864	48	6	1331	3	garfertig	Lebensmittel werden nur noch gegart
14865	48	6	1332	4	aufbereitfertig	Zutaten werden noch benötigt, z.B, Flüssigkeiten oder Lebensmittel müssen auf Verzehrtemperatur gebracht werden
14866	48	6	1333	5	verzehrsfertig	\N
14867	48	6	1334	6	tiefgefroren	\N
14868	48	6	1335	10	Maische	\N
14869	48	6	1336	11	Trester	\N
14870	48	6	1337	12	Most	\N
14871	48	6	1338	13	Mark	\N
14872	48	6	1339	14	Mus	\N
14873	49	6	1328	0	Verarbeitung	\N
14874	49	6	1329	1	unbehandelt	roh, wie eingekauft
14875	49	6	1330	2	küchenfertig	gewaschen, geputzt, nicht essbarer Teil entfernt
14876	49	6	1331	3	garfertig	Lebensmittel werden nur noch gegart
14877	49	6	1332	4	aufbereitfertig	Zutaten werden noch benötigt, z.B, Flüssigkeiten oder Lebensmittel müssen auf Verzehrtemperatur gebracht werden
14878	49	6	1333	5	verzehrsfertig	\N
14879	49	6	1334	6	tiefgefroren	\N
14880	49	6	1335	10	Maische	\N
14881	49	6	1336	11	Trester	\N
14882	49	6	1337	12	Most	\N
14883	49	6	1338	13	Mark	\N
14884	49	6	1339	14	Mus	\N
14885	55	6	1328	0	Verarbeitung	\N
14886	55	6	1329	1	unbehandelt	roh, wie eingekauft
14887	55	6	1330	2	küchenfertig	gewaschen, geputzt, nicht essbarer Teil entfernt
14888	55	6	1331	3	garfertig	Lebensmittel werden nur noch gegart
14889	55	6	1332	4	aufbereitfertig	Zutaten werden noch benötigt, z.B, Flüssigkeiten oder Lebensmittel müssen auf Verzehrtemperatur gebracht werden
14890	55	6	1333	5	verzehrsfertig	\N
14891	55	6	1334	6	tiefgefroren	\N
14892	55	6	1335	10	Maische	\N
14893	55	6	1336	11	Trester	\N
14894	55	6	1337	12	Most	\N
14895	55	6	1338	13	Mark	\N
14896	55	6	1339	14	Mus	\N
14897	56	6	1328	0	Verarbeitung	\N
14898	56	6	1329	1	unbehandelt	roh, wie eingekauft
14899	56	6	1330	2	küchenfertig	gewaschen, geputzt, nicht essbarer Teil entfernt
14900	56	6	1331	3	garfertig	Lebensmittel werden nur noch gegart
14901	56	6	1332	4	aufbereitfertig	Zutaten werden noch benötigt, z.B, Flüssigkeiten oder Lebensmittel müssen auf Verzehrtemperatur gebracht werden
14902	56	6	1333	5	verzehrsfertig	\N
14903	56	6	1334	6	tiefgefroren	\N
14904	56	6	1335	10	Maische	\N
14905	56	6	1336	11	Trester	\N
14906	56	6	1337	12	Most	\N
14907	56	6	1338	13	Mark	\N
14908	56	6	1339	14	Mus	\N
14909	59	6	1328	0	Verarbeitung	\N
14910	59	6	1329	1	unbehandelt	roh, wie eingekauft
14911	59	6	1330	2	küchenfertig	gewaschen, geputzt, nicht essbarer Teil entfernt
14912	59	6	1331	3	garfertig	Lebensmittel werden nur noch gegart
14913	59	6	1332	4	aufbereitfertig	Zutaten werden noch benötigt, z.B, Flüssigkeiten oder Lebensmittel müssen auf Verzehrtemperatur gebracht werden
14914	59	6	1333	5	verzehrsfertig	\N
14915	59	6	1334	6	tiefgefroren	\N
14916	59	6	1335	10	Maische	\N
14917	59	6	1336	11	Trester	\N
14918	59	6	1337	12	Most	\N
14919	59	6	1338	13	Mark	\N
14920	59	6	1339	14	Mus	\N
14921	60	6	1328	0	Verarbeitung	\N
14922	60	6	1329	1	unbehandelt	roh, wie eingekauft
14923	60	6	1330	2	küchenfertig	gewaschen, geputzt, nicht essbarer Teil entfernt
14924	60	6	1331	3	garfertig	Lebensmittel werden nur noch gegart
14925	60	6	1332	4	aufbereitfertig	Zutaten werden noch benötigt, z.B, Flüssigkeiten oder Lebensmittel müssen auf Verzehrtemperatur gebracht werden
14926	60	6	1333	5	verzehrsfertig	\N
14927	60	6	1334	6	tiefgefroren	\N
14928	60	6	1335	10	Maische	\N
14929	60	6	1336	11	Trester	\N
14930	60	6	1337	12	Most	\N
14931	60	6	1338	13	Mark	\N
14932	60	6	1339	14	Mus	\N
14933	64	6	1328	0	Verarbeitung	\N
14934	64	6	1329	1	unbehandelt	roh, wie eingekauft
14935	64	6	1330	2	küchenfertig	gewaschen, geputzt, nicht essbarer Teil entfernt
14936	64	6	1331	3	garfertig	Lebensmittel werden nur noch gegart
14937	64	6	1332	4	aufbereitfertig	Zutaten werden noch benötigt, z.B, Flüssigkeiten oder Lebensmittel müssen auf Verzehrtemperatur gebracht werden
14938	64	6	1333	5	verzehrsfertig	\N
14939	64	6	1334	6	tiefgefroren	\N
14940	64	6	1335	10	Maische	\N
14941	64	6	1336	11	Trester	\N
14942	64	6	1337	12	Most	\N
14943	64	6	1338	13	Mark	\N
14944	64	6	1339	14	Mus	\N
14945	65	6	1328	0	Verarbeitung	\N
14946	65	6	1329	1	unbehandelt	roh, wie eingekauft
14947	65	6	1330	2	küchenfertig	gewaschen, geputzt, nicht essbarer Teil entfernt
14948	65	6	1331	3	garfertig	Lebensmittel werden nur noch gegart
14949	65	6	1332	4	aufbereitfertig	Zutaten werden noch benötigt, z.B, Flüssigkeiten oder Lebensmittel müssen auf Verzehrtemperatur gebracht werden
14950	65	6	1333	5	verzehrsfertig	\N
14951	65	6	1334	6	tiefgefroren	\N
14952	65	6	1335	10	Maische	\N
14953	65	6	1336	11	Trester	\N
14954	65	6	1337	12	Most	\N
14955	65	6	1338	13	Mark	\N
14956	65	6	1339	14	Mus	\N
14957	66	6	1328	0	Verarbeitung	\N
14958	66	6	1329	1	unbehandelt	roh, wie eingekauft
14959	66	6	1330	2	küchenfertig	gewaschen, geputzt, nicht essbarer Teil entfernt
14960	66	6	1331	3	garfertig	Lebensmittel werden nur noch gegart
14961	66	6	1332	4	aufbereitfertig	Zutaten werden noch benötigt, z.B, Flüssigkeiten oder Lebensmittel müssen auf Verzehrtemperatur gebracht werden
14962	66	6	1333	5	verzehrsfertig	\N
14963	66	6	1334	6	tiefgefroren	\N
14964	66	6	1335	10	Maische	\N
14965	66	6	1336	11	Trester	\N
14966	66	6	1337	12	Most	\N
14967	66	6	1338	13	Mark	\N
14968	66	6	1339	14	Mus	\N
14969	69	6	1328	0	Verarbeitung	\N
14970	69	6	1329	1	unbehandelt	roh, wie eingekauft
14971	69	6	1330	2	küchenfertig	gewaschen, geputzt, nicht essbarer Teil entfernt
14972	69	6	1331	3	garfertig	Lebensmittel werden nur noch gegart
14973	69	6	1332	4	aufbereitfertig	Zutaten werden noch benötigt, z.B, Flüssigkeiten oder Lebensmittel müssen auf Verzehrtemperatur gebracht werden
14974	69	6	1333	5	verzehrsfertig	\N
14975	69	6	1334	6	tiefgefroren	\N
14976	69	6	1335	10	Maische	\N
14977	69	6	1336	11	Trester	\N
14978	69	6	1337	12	Most	\N
14979	69	6	1338	13	Mark	\N
14980	69	6	1339	14	Mus	\N
14981	73	6	10164	0	Aufbereitung	\N
14982	73	6	10165	1	Keine Aufbereitung	\N
14983	73	6	10166	2	offene Belueftung	\N
14984	73	6	10167	3	offene Belüftung, Filterung über Kies	offene Belüftung, Filterung über Kies
14985	73	6	10168	4	Offene Belüftung, Filterung über Aktivkohle	offene Belüftung, Filterung über Aktivkohle
14986	73	6	10169	5	Offene Belüftung, Filterung über Kies und Aktivkohle	offene Belüftung, Filterung über Kies/Aktivkohle
14987	73	6	10170	6	Filterung über Kies	Filterung über Kies
14988	73	6	10171	7	Filterung über Aktivkohle	Filterung über Aktivkohle
14989	73	6	10172	8	Filterung über Kies und Aktivkohle	Filterung über Kies/Aktivkohle
14990	108	6	12115	0	Nutzung	\N
14991	108	6	12116	1	Ackerland	\N
14992	108	6	12117	10	Grünland, Weide, Wiese	  Weide, Wiese
14993	108	6	12118	20	Wald	\N
14994	108	6	12119	21	Laubwald	\N
14995	108	6	12120	22	Mischwald	\N
14996	108	6	12121	23	Nadelwald	\N
14997	108	6	12122	30	Ödland, Unland	\N
14998	108	6	12123	31	Brache	\N
14999	108	6	12124	40	Kleingarten	\N
15000	108	6	12125	50	Erwerbsgarten	  Gemüsebau, Zierpflanzen
15001	108	6	12126	51	Obstland	\N
15002	108	6	12127	52	Rebland	\N
15003	108	6	12128	53	Hopfengarten	\N
15004	108	6	12129	54	Baumschule	\N
15005	108	6	12130	55	Spargelkultur	\N
15006	108	6	12131	56	Tabakkultur	\N
15007	108	6	12132	57	Sonstige Sonderkulturen	\N
15008	108	6	12133	60	Parkanlage, Grünfläche	\N
15009	108	6	12134	61	Liegewiese	  inkl. Freibad, Campingplatz
15010	108	6	12135	62	Sportplatz	  auch Freizeitgelände
15011	108	6	12136	63	Spielplatz	  auch von Kindergärten und Schulhöfen
15012	108	6	12137	64	Strand	\N
15013	108	6	12138	65	Uferstreifen	\N
15014	108	6	12139	70	Friedhof	\N
15015	109	6	12115	0	Nutzung	\N
15016	109	6	12116	1	Ackerland	\N
15017	109	6	12117	10	Grünland, Weide, Wiese	  Weide, Wiese
15018	109	6	12118	20	Wald	\N
15019	109	6	12119	21	Laubwald	\N
15020	109	6	12120	22	Mischwald	\N
15021	109	6	12121	23	Nadelwald	\N
15022	109	6	12122	30	Ödland, Unland	\N
15023	109	6	12123	31	Brache	\N
15024	109	6	12124	40	Kleingarten	\N
15025	109	6	12125	50	Erwerbsgarten	  Gemüsebau, Zierpflanzen
15026	109	6	12126	51	Obstland	\N
15027	109	6	12127	52	Rebland	\N
15028	109	6	12128	53	Hopfengarten	\N
15029	109	6	12129	54	Baumschule	\N
15030	109	6	12130	55	Spargelkultur	\N
15031	109	6	12131	56	Tabakkultur	\N
15032	109	6	12132	57	Sonstige Sonderkulturen	\N
15033	109	6	12133	60	Parkanlage, Grünfläche	\N
15034	109	6	12134	61	Liegewiese	  inkl. Freibad, Campingplatz
15035	109	6	12135	62	Sportplatz	  auch Freizeitgelände
15036	109	6	12136	63	Spielplatz	  auch von Kindergärten und Schulhöfen
15037	109	6	12139	70	Friedhof	\N
15038	110	6	12115	0	Nutzung	\N
15039	110	6	12116	1	Ackerland	\N
15040	110	6	12117	10	Grünland, Weide, Wiese	  Weide, Wiese
15041	110	6	12118	20	Wald	\N
15042	110	6	12119	21	Laubwald	\N
15043	110	6	12120	22	Mischwald	\N
15044	110	6	12121	23	Nadelwald	\N
15045	110	6	12122	30	Ödland, Unland	\N
15046	110	6	12123	31	Brache	\N
15047	110	6	12124	40	Kleingarten	\N
15048	110	6	12125	50	Erwerbsgarten	  Gemüsebau, Zierpflanzen
15049	110	6	12126	51	Obstland	\N
15050	110	6	12127	52	Rebland	\N
15051	110	6	12128	53	Hopfengarten	\N
15052	110	6	12129	54	Baumschule	\N
15053	110	6	12130	55	Spargelkultur	\N
15054	110	6	12131	56	Tabakkultur	\N
15055	110	6	12132	57	Sonstige Sonderkulturen	\N
15056	110	6	12133	60	Parkanlage, Grünfläche	\N
15057	110	6	12134	61	Liegewiese	  inkl. Freibad, Campingplatz
15058	110	6	12135	62	Sportplatz	  auch Freizeitgelände
15059	110	6	12136	63	Spielplatz	  auch von Kindergärten und Schulhöfen
15060	110	6	12139	70	Friedhof	\N
15061	120	6	12530	0	Nutzung, Verwendung	\N
15062	120	6	12531	1	Keine besondere Nutzung	\N
15063	120	6	12532	2	Betriebswasser	z.B. Kühlwasser
15064	120	6	12533	3	Bewässerung, Beregnung	\N
15065	120	6	12534	4	Badegewässer, Freibad	\N
15066	120	6	12535	5	Hallenbad	\N
15067	120	6	12536	6	Fischgewässer	\N
15068	120	6	12537	7	Naturschutzgebiet	\N
15069	120	6	12538	8	Parkanlage	\N
15070	120	6	12539	9	Vorfluter	\N
15071	120	6	12540	10	Viehtränke	\N
15072	120	6	12541	11	Trinkwassergewinnung: Direktentnahme	\N
15073	120	6	12542	12	Trinkwassergewinnung: Uferfiltration	\N
15074	121	6	12115	0	Nutzung	\N
15075	121	6	12552	1	Keine Nutzung	\N
15076	121	6	12532	2	Betriebswasser	z.B. Kühlwasser
15077	121	6	12533	3	Bewässerung, Beregnung	\N
15078	121	6	12540	10	Viehtränke	\N
15079	121	6	12553	11	Trinkwassergewinnung	Notbrunnen
15080	122	6	12530	0	Nutzung, Verwendung	\N
15081	122	6	12567	1	Verklappung	\N
15082	122	6	12568	2	Aufbringung auf Spülfelder	\N
15083	122	6	12569	3	Aufbringung auf landwirtschaftlich genutzte Flächen	\N
15084	124	6	12620	0	Bodennutzung	\N
15085	124	6	12116	1	Ackerland	\N
15086	124	6	12117	10	Grünland, Weide, Wiese	  Weide, Wiese
15087	124	6	12118	20	Wald	\N
15088	124	6	12119	21	Laubwald	\N
15089	124	6	12120	22	Mischwald	\N
15090	124	6	12121	23	Nadelwald	\N
15091	124	6	12122	30	Ödland, Unland	\N
15092	124	6	12123	31	Brache	\N
15093	124	6	12124	40	Kleingarten	\N
15094	124	6	12125	50	Erwerbsgarten	  Gemüsebau, Zierpflanzen
15095	124	6	12126	51	Obstland	\N
15096	124	6	12127	52	Rebland	\N
15097	124	6	12128	53	Hopfengarten	\N
15098	124	6	12129	54	Baumschule	\N
15099	124	6	12130	55	Spargelkultur	\N
15100	124	6	12131	56	Tabakkultur	\N
15101	124	6	12132	57	Sonstige Sonderkulturen	\N
15102	124	6	12133	60	Parkanlage, Grünfläche	\N
15103	124	6	12134	61	Liegewiese	  inkl. Freibad, Campingplatz
15104	124	6	12135	62	Sportplatz	  auch Freizeitgelände
15105	124	6	12136	63	Spielplatz	  auch von Kindergärten und Schulhöfen
15106	124	6	12139	70	Friedhof	\N
15107	140	6	12750	0	Nutzung, Entsorgung	\N
15108	140	6	12751	1	Landwirtschaft	Landwirtschaftliche Verwertung
15109	140	6	12752	2	Kompostierung	\N
15110	140	6	12753	3	Mono-Deponie	\N
15111	140	6	12754	4	Misch-Deponie	\N
15112	140	6	12755	5	Gesonderte Klärschlammverbrennung	\N
15113	140	6	12756	6	Gemischte Abfallverbrennung	\N
15114	140	6	12757	7	Pyrolyse	\N
15115	144	6	12115	0	Nutzung	\N
15116	144	6	12751	1	Landwirtschaft	Landwirtschaftliche Verwertung
15117	144	6	12854	2	Gartenbau	\N
15118	144	6	12855	3	Landschaftsbau	\N
15119	144	6	12856	99	Sonstiges	\N
15120	150	6	10164	0	Aufbereitung	\N
15121	150	6	10167	3	offene Belüftung, Filterung über Kies	offene Belüftung, Filterung über Kies
15122	150	6	10168	4	Offene Belüftung, Filterung über Aktivkohle	offene Belüftung, Filterung über Aktivkohle
15123	150	6	10169	5	Offene Belüftung, Filterung über Kies und Aktivkohle	offene Belüftung, Filterung über Kies/Aktivkohle
15124	150	6	10170	6	Filterung über Kies	Filterung über Kies
15125	150	6	10171	7	Filterung über Aktivkohle	Filterung über Aktivkohle
15126	150	6	10172	8	Filterung über Kies und Aktivkohle	Filterung über Kies/Aktivkohle
15127	161	6	12115	0	Nutzung	\N
15128	161	6	12116	1	Ackerland	\N
15129	161	6	13025	10	Grünland	Weide, Wiese
15130	161	6	12118	20	Wald	\N
15131	161	6	12119	21	Laubwald	\N
15132	161	6	12120	22	Mischwald	\N
15133	161	6	12121	23	Nadelwald	\N
15134	161	6	12122	30	Ödland, Unland	\N
15135	161	6	12123	31	Brache	\N
15136	161	6	12124	40	Kleingarten	\N
15137	161	6	12125	50	Erwerbsgarten	  Gemüsebau, Zierpflanzen
15138	161	6	12126	51	Obstland	\N
15139	161	6	12127	52	Rebland	\N
15140	161	6	12128	53	Hopfengarten	\N
15141	161	6	12129	54	Baumschule	\N
15142	161	6	12130	55	Spargelkultur	\N
15143	161	6	12131	56	Tabakkultur	\N
15144	161	6	12132	57	Sonstige Sonderkulturen	\N
15145	161	6	12133	60	Parkanlage, Grünfläche	\N
15146	161	6	12134	61	Liegewiese	  inkl. Freibad, Campingplatz
15147	161	6	12135	62	Sportplatz	  auch Freizeitgelände
15148	161	6	12136	63	Spielplatz	  auch von Kindergärten und Schulhöfen
15149	161	6	12139	70	Friedhof	\N
15150	161	6	13026	80	Verkehrsfläche	\N
15151	161	6	12856	99	Sonstiges	\N
15152	163	6	12115	0	Nutzung	\N
15153	163	6	12116	1	Ackerland	\N
15154	163	6	13025	10	Grünland	Weide, Wiese
15155	163	6	12118	20	Wald	\N
15156	163	6	12119	21	Laubwald	\N
15157	163	6	12120	22	Mischwald	\N
15158	163	6	12121	23	Nadelwald	\N
15159	163	6	12122	30	Ödland, Unland	\N
15160	163	6	12123	31	Brache	\N
15161	163	6	12124	40	Kleingarten	\N
15162	163	6	12125	50	Erwerbsgarten	  Gemüsebau, Zierpflanzen
15163	163	6	12126	51	Obstland	\N
15164	163	6	12127	52	Rebland	\N
15165	163	6	12128	53	Hopfengarten	\N
15166	163	6	12129	54	Baumschule	\N
15167	163	6	12130	55	Spargelkultur	\N
15168	163	6	12131	56	Tabakkultur	\N
15169	163	6	12132	57	Sonstige Sonderkulturen	\N
15170	163	6	12133	60	Parkanlage, Grünfläche	\N
15171	163	6	12134	61	Liegewiese	  inkl. Freibad, Campingplatz
15172	163	6	12135	62	Sportplatz	  auch Freizeitgelände
15173	163	6	12136	63	Spielplatz	  auch von Kindergärten und Schulhöfen
15174	163	6	12139	70	Friedhof	\N
15175	163	6	13026	80	Verkehrsfläche	\N
15176	163	6	12856	99	Sonstiges	\N
15177	20	7	57	0	Fütterung-S	\N
15178	20	7	58	1	MAT mit Magermilchpulver	MAT = Milchaustauscher
15179	20	7	59	2	MAT ohne Magermilchpulver	\N
15180	20	7	60	3	Vollmilch	\N
15181	20	7	61	4	Magermilch	\N
15182	20	7	62	10	Grünfutter	überwiegend Grünfutter
15183	20	7	63	11	Weide, Wildäsung	\N
15184	20	7	64	20	Gemischt	\N
15185	20	7	65	30	nur Konserven	\N
15186	20	7	66	40	Trockenfütterung	\N
15187	20	7	67	41	Trockenfütterung (Zukauf)	Tr.f. überwiegend Zukauf
15188	20	7	68	42	Trockenfütterung (eigen G.)	Tr.f. überwiegend eigenes Getreide
15189	20	7	69	50	Flüssigfütterung	\N
15190	20	7	70	51	Flüssigfütterung - Molke	Fl.f. mit Molke
15191	20	7	71	52	Flüssigfütterung - Perm.molke	Fl.f. mit Permeatmolke
15192	20	7	72	53	Flüssigfütterung - eing.Molke	Fl.f. mit eingedickter Molke
15193	20	7	73	54	Flüssigfütterung - Magermilch	Fl.f. mit Magermilch
15194	20	7	74	55	Flüssigfütterung - Molk.erzg.	Fl.f. mit Molkenerzeugnissen und Magermilch
15195	20	7	75	56	Flüssigfütterung - sonstige	Fl.f. mit anderen flüssigen Futtermitteln
15196	20	7	76	60	Feuchtfütterung	\N
15197	21	7	57	0	Fütterung-S	\N
15198	21	7	58	1	MAT mit Magermilchpulver	MAT = Milchaustauscher
15199	21	7	59	2	MAT ohne Magermilchpulver	\N
15200	21	7	60	3	Vollmilch	\N
15201	21	7	61	4	Magermilch	\N
15202	21	7	62	10	Grünfutter	überwiegend Grünfutter
15203	21	7	63	11	Weide, Wildäsung	\N
15204	21	7	64	20	Gemischt	\N
15205	21	7	65	30	nur Konserven	\N
15206	21	7	66	40	Trockenfütterung	\N
15207	21	7	67	41	Trockenfütterung (Zukauf)	Tr.f. überwiegend Zukauf
15208	21	7	68	42	Trockenfütterung (eigen G.)	Tr.f. überwiegend eigenes Getreide
15209	21	7	69	50	Flüssigfütterung	\N
15210	21	7	70	51	Flüssigfütterung - Molke	Fl.f. mit Molke
15211	21	7	71	52	Flüssigfütterung - Perm.molke	Fl.f. mit Permeatmolke
15212	21	7	72	53	Flüssigfütterung - eing.Molke	Fl.f. mit eingedickter Molke
15213	21	7	73	54	Flüssigfütterung - Magermilch	Fl.f. mit Magermilch
15214	21	7	74	55	Flüssigfütterung - Molk.erzg.	Fl.f. mit Molkenerzeugnissen und Magermilch
15215	21	7	75	56	Flüssigfütterung - sonstige	Fl.f. mit anderen flüssigen Futtermitteln
15216	21	7	76	60	Feuchtfütterung	\N
15217	22	7	57	0	Fütterung-S	\N
15218	22	7	58	1	MAT mit Magermilchpulver	MAT = Milchaustauscher
15219	22	7	59	2	MAT ohne Magermilchpulver	\N
15220	22	7	60	3	Vollmilch	\N
15221	22	7	61	4	Magermilch	\N
15222	22	7	62	10	Grünfutter	überwiegend Grünfutter
15223	22	7	63	11	Weide, Wildäsung	\N
15224	22	7	64	20	Gemischt	\N
15225	22	7	65	30	nur Konserven	\N
15226	22	7	66	40	Trockenfütterung	\N
15227	22	7	67	41	Trockenfütterung (Zukauf)	Tr.f. überwiegend Zukauf
15228	22	7	68	42	Trockenfütterung (eigen G.)	Tr.f. überwiegend eigenes Getreide
15229	22	7	69	50	Flüssigfütterung	\N
15230	22	7	70	51	Flüssigfütterung - Molke	Fl.f. mit Molke
15231	22	7	71	52	Flüssigfütterung - Perm.molke	Fl.f. mit Permeatmolke
15232	22	7	72	53	Flüssigfütterung - eing.Molke	Fl.f. mit eingedickter Molke
15233	22	7	73	54	Flüssigfütterung - Magermilch	Fl.f. mit Magermilch
15234	22	7	74	55	Flüssigfütterung - Molk.erzg.	Fl.f. mit Molkenerzeugnissen und Magermilch
15235	22	7	75	56	Flüssigfütterung - sonstige	Fl.f. mit anderen flüssigen Futtermitteln
15236	22	7	76	60	Feuchtfütterung	\N
15237	23	7	57	0	Fütterung-S	\N
15238	23	7	58	1	MAT mit Magermilchpulver	MAT = Milchaustauscher
15239	23	7	59	2	MAT ohne Magermilchpulver	\N
15240	23	7	60	3	Vollmilch	\N
15241	23	7	61	4	Magermilch	\N
15242	23	7	62	10	Grünfutter	überwiegend Grünfutter
15243	23	7	63	11	Weide, Wildäsung	\N
15244	23	7	64	20	Gemischt	\N
15245	23	7	65	30	nur Konserven	\N
15246	23	7	66	40	Trockenfütterung	\N
15247	23	7	67	41	Trockenfütterung (Zukauf)	Tr.f. überwiegend Zukauf
15248	23	7	68	42	Trockenfütterung (eigen G.)	Tr.f. überwiegend eigenes Getreide
15249	23	7	69	50	Flüssigfütterung	\N
15250	23	7	70	51	Flüssigfütterung - Molke	Fl.f. mit Molke
15251	23	7	71	52	Flüssigfütterung - Perm.molke	Fl.f. mit Permeatmolke
15252	23	7	72	53	Flüssigfütterung - eing.Molke	Fl.f. mit eingedickter Molke
15253	23	7	73	54	Flüssigfütterung - Magermilch	Fl.f. mit Magermilch
15254	23	7	74	55	Flüssigfütterung - Molk.erzg.	Fl.f. mit Molkenerzeugnissen und Magermilch
15255	23	7	75	56	Flüssigfütterung - sonstige	Fl.f. mit anderen flüssigen Futtermitteln
15256	23	7	76	60	Feuchtfütterung	\N
15257	24	7	758	0	Fütterung-V	\N
15258	24	7	759	1	Legemehl	\N
15259	25	7	57	0	Fütterung-S	\N
15260	25	7	58	1	MAT mit Magermilchpulver	MAT = Milchaustauscher
15261	25	7	59	2	MAT ohne Magermilchpulver	\N
15262	25	7	60	3	Vollmilch	\N
15263	25	7	61	4	Magermilch	\N
15264	25	7	62	10	Grünfutter	überwiegend Grünfutter
15265	25	7	63	11	Weide, Wildäsung	\N
15266	25	7	64	20	Gemischt	\N
15267	25	7	65	30	nur Konserven	\N
15268	25	7	66	40	Trockenfütterung	\N
15269	25	7	67	41	Trockenfütterung (Zukauf)	Tr.f. überwiegend Zukauf
15270	25	7	68	42	Trockenfütterung (eigen G.)	Tr.f. überwiegend eigenes Getreide
15271	25	7	69	50	Flüssigfütterung	\N
15272	25	7	70	51	Flüssigfütterung - Molke	Fl.f. mit Molke
15273	25	7	71	52	Flüssigfütterung - Perm.molke	Fl.f. mit Permeatmolke
15274	25	7	72	53	Flüssigfütterung - eing.Molke	Fl.f. mit eingedickter Molke
15275	25	7	73	54	Flüssigfütterung - Magermilch	Fl.f. mit Magermilch
15276	25	7	74	55	Flüssigfütterung - Molk.erzg.	Fl.f. mit Molkenerzeugnissen und Magermilch
15277	25	7	75	56	Flüssigfütterung - sonstige	Fl.f. mit anderen flüssigen Futtermitteln
15278	25	7	76	60	Feuchtfütterung	\N
15279	26	7	57	0	Fütterung-S	\N
15280	26	7	58	1	MAT mit Magermilchpulver	MAT = Milchaustauscher
15281	26	7	59	2	MAT ohne Magermilchpulver	\N
15282	26	7	60	3	Vollmilch	\N
15283	26	7	61	4	Magermilch	\N
15284	26	7	62	10	Grünfutter	überwiegend Grünfutter
15285	26	7	63	11	Weide, Wildäsung	\N
15286	26	7	64	20	Gemischt	\N
15287	26	7	65	30	nur Konserven	\N
15288	26	7	66	40	Trockenfütterung	\N
15289	26	7	67	41	Trockenfütterung (Zukauf)	Tr.f. überwiegend Zukauf
15290	26	7	68	42	Trockenfütterung (eigen G.)	Tr.f. überwiegend eigenes Getreide
15291	26	7	69	50	Flüssigfütterung	\N
15292	26	7	70	51	Flüssigfütterung - Molke	Fl.f. mit Molke
15293	26	7	71	52	Flüssigfütterung - Perm.molke	Fl.f. mit Permeatmolke
15294	26	7	72	53	Flüssigfütterung - eing.Molke	Fl.f. mit eingedickter Molke
15295	26	7	73	54	Flüssigfütterung - Magermilch	Fl.f. mit Magermilch
15296	26	7	74	55	Flüssigfütterung - Molk.erzg.	Fl.f. mit Molkenerzeugnissen und Magermilch
15297	26	7	75	56	Flüssigfütterung - sonstige	Fl.f. mit anderen flüssigen Futtermitteln
15298	26	7	76	60	Feuchtfütterung	\N
15299	27	7	57	0	Fütterung-S	\N
15300	27	7	58	1	MAT mit Magermilchpulver	MAT = Milchaustauscher
15301	27	7	59	2	MAT ohne Magermilchpulver	\N
15302	27	7	60	3	Vollmilch	\N
15303	27	7	61	4	Magermilch	\N
15304	27	7	62	10	Grünfutter	überwiegend Grünfutter
15305	27	7	63	11	Weide, Wildäsung	\N
15306	27	7	64	20	Gemischt	\N
15307	27	7	65	30	nur Konserven	\N
15308	27	7	66	40	Trockenfütterung	\N
15309	27	7	67	41	Trockenfütterung (Zukauf)	Tr.f. überwiegend Zukauf
15310	27	7	68	42	Trockenfütterung (eigen G.)	Tr.f. überwiegend eigenes Getreide
15311	27	7	69	50	Flüssigfütterung	\N
15312	27	7	70	51	Flüssigfütterung - Molke	Fl.f. mit Molke
15313	27	7	71	52	Flüssigfütterung - Perm.molke	Fl.f. mit Permeatmolke
15314	27	7	72	53	Flüssigfütterung - eing.Molke	Fl.f. mit eingedickter Molke
15315	27	7	73	54	Flüssigfütterung - Magermilch	Fl.f. mit Magermilch
15316	27	7	74	55	Flüssigfütterung - Molk.erzg.	Fl.f. mit Molkenerzeugnissen und Magermilch
15317	27	7	75	56	Flüssigfütterung - sonstige	Fl.f. mit anderen flüssigen Futtermitteln
15318	27	7	76	60	Feuchtfütterung	\N
15319	28	7	3285	0	Fütterung-F	\N
15320	28	7	3286	1	Naturnahrung	\N
15321	28	7	3287	2	Nat.+Trock. Getreide	\N
15322	28	7	3288	3	Nat.+Trock. Mais	\N
15323	28	7	3289	4	Nat.+Trock. Lupinen	\N
15324	28	7	3290	5	Nat.+Trock. Tiermehl	\N
15325	28	7	3291	6	Nat.+Trock. Sojaschrot	\N
15326	28	7	3292	7	Nat.+Trock. Hülsenfruechte	\N
15327	28	7	3293	8	Nat.+Trock. gemischt	Kombinationen 02-07
15328	28	7	3294	9	Trockenfütterung Fischmehl	100% Fütterung von 09-12 (Salmoniden)
15329	28	7	3295	10	Trockenfütterung Tiermehl	\N
15330	28	7	3296	11	Trockenfütterung gemischt	Kombination 09 u. 10
15331	28	7	3297	12	Naßfütterung Fische	\N
15332	29	7	3285	0	Fütterung-F	\N
15333	29	7	3286	1	Naturnahrung	\N
15334	29	7	3287	2	Nat.+Trock. Getreide	\N
15335	29	7	3288	3	Nat.+Trock. Mais	\N
15336	29	7	3289	4	Nat.+Trock. Lupinen	\N
15337	29	7	3290	5	Nat.+Trock. Tiermehl	\N
15338	29	7	3291	6	Nat.+Trock. Sojaschrot	\N
15339	29	7	3292	7	Nat.+Trock. Hülsenfruechte	\N
15340	29	7	3293	8	Nat.+Trock. gemischt	Kombinationen 02-07
15341	29	7	3294	9	Trockenfütterung Fischmehl	100% Fütterung von 09-12 (Salmoniden)
15342	29	7	3295	10	Trockenfütterung Tiermehl	\N
15343	29	7	3296	11	Trockenfütterung gemischt	Kombination 09 u. 10
15344	29	7	3297	12	Naßfütterung Fische	\N
15345	30	7	3285	0	Fütterung-F	\N
15346	30	7	3286	1	Naturnahrung	\N
15347	30	7	3287	2	Nat.+Trock. Getreide	\N
15348	30	7	3288	3	Nat.+Trock. Mais	\N
15349	30	7	3289	4	Nat.+Trock. Lupinen	\N
15350	30	7	3290	5	Nat.+Trock. Tiermehl	\N
15351	30	7	3291	6	Nat.+Trock. Sojaschrot	\N
15352	30	7	3292	7	Nat.+Trock. Hülsenfruechte	\N
15353	30	7	3293	8	Nat.+Trock. gemischt	Kombinationen 02-07
15354	30	7	3294	9	Trockenfütterung Fischmehl	100% Fütterung von 09-12 (Salmoniden)
15355	30	7	3295	10	Trockenfütterung Tiermehl	\N
15356	30	7	3296	11	Trockenfütterung gemischt	Kombination 09 u. 10
15357	30	7	3297	12	Naßfütterung Fische	\N
15358	73	7	10173	0	Probenahme	\N
15359	73	7	10174	1	Stichprobe	\N
15360	73	7	10175	2	Stichprobe, geschoepft	\N
15361	73	7	10176	3	Stichprobe, gepumpt	\N
15362	73	7	10177	4	Mischprobe aus Stichproben	\N
15363	73	7	10178	5	Mischprobe	\N
15364	73	7	10179	6	Kontinuierliche Sammelprobe	\N
15365	73	7	10180	7	Mischprobe auf Wegstrecke	\N
15366	73	7	12554	9	Zeitproportionale Sammelprobe	\N
15367	73	7	12555	10	Mengenproportionale Sammelprobe	\N
15368	108	7	12140	0	Horizont	\N
15369	108	7	12141	1	nH	Niedermoortorf
15370	108	7	12142	2	uH	Übergangsmoortorf
15371	108	7	12143	3	hH	Hochmoortorf
15372	108	7	12144	4	Hp	Torf, bearbeitet durch Pflügen
15373	108	7	12145	5	L	Nadel-, Laubstreu
15374	108	7	12146	6	Of	Auflagehumus, schwach zersetzt
15375	108	7	12147	7	Oh	Auflagehumus, stark zersetzt
15376	108	7	12148	8	Ah	Mineralischer Oberboden, humos
15377	108	7	12149	9	fAh	f von fossil (begraben, überdeckt)
15378	108	7	12150	10	Ach	c von carbonatangereichert
15379	108	7	12151	11	Aeh	e von eluviert (sauergebleicht)
15380	108	7	12152	12	BvAh	Übergangshorizont
15381	108	7	12153	13	SwAh	Übergangshorizont
15382	108	7	12154	14	GoAh	Übergangshorizont
15383	108	7	12155	15	yYaH	Übergangshorizont
15384	108	7	12156	16	jYAh	Übergangshorizont
15385	108	7	12157	17	Aa	Mineralischer Oberboden, anmoorig
15386	108	7	12158	18	GoAa	Übergangshorizont
15387	108	7	12159	19	GcoAa	Übergangshorizont
15388	108	7	12160	20	Ae	Mineralischer Oberboden, eluviert
15389	108	7	12161	21	Ahe	Min. Oberboden, humos, eluviert
15390	108	7	12162	22	Al	Min. Oberboden, lessiviert (tonausgewaschen)
15391	108	7	12163	23	BvAl	Übergangshorizont
15392	108	7	12164	24	SwAl	Übergangshorizont
15393	108	7	12165	25	Ap	Min. Oberboden, gepflügt
15394	108	7	12166	26	BV	Min. Unterboden, verwittert
15395	108	7	12167	27	fBv	f von fossil (begraben, überdeckt)
15396	108	7	12168	28	Bcv	c von carbonatangereichert
15397	108	7	12169	29	Bsv	s von sesquioxidangereichert
15398	108	7	12170	30	Btv	t von tonangereichert
15399	108	7	12171	31	AhBv	Übergangshorizont
15400	108	7	12172	32	AlBv	Übergangshorizont
15401	108	7	12173	33	MBv	Übergangshorizont
15402	108	7	12174	34	SwBv	Übergangshorizont
15403	108	7	12175	35	GoBv	Übergangshorizont
15404	108	7	12176	36	Bh	Min. Unterboden, huminstoffangereichert
15405	108	7	12177	37	Bsh	s von sesquioxidangereichert
15406	108	7	12178	38	Bs	s von sesquioxidangereichert
15407	108	7	12179	39	Bhs	h von huminstoffangereichert
15408	108	7	12180	40	SwBhs	Übergangshorizont
15409	108	7	12181	41	SdBhs	Übergangshorizont
15410	108	7	12182	42	Bt	Min. Unterboden, tonangereichert
15411	108	7	12183	43	Bvt	v von verwittert
15412	108	7	12184	44	SdBt	Übergangshorizont
15413	108	7	12185	45	Bu	Min. Unterboden, ferrallitisiert
15414	108	7	12186	46	Bj	Min. Unterboden der Plastosole
15415	108	7	12187	47	Cv	Min. Unterboden, verwittert
15416	108	7	12188	48	BvCv	Übergangshorizont
15417	108	7	12189	49	TCv	Übergangshorizont
15418	108	7	12190	50	SdCv	Übergangshorizont
15419	108	7	12191	51	AhCv	Übergangshorizont
15420	108	7	12192	52	Cn	Min. Untergrund, unverwittert
15421	108	7	12193	53	P	Min. Unterboden aus Tonstein
15422	108	7	12194	54	AhP	Übergangshorizont
15423	108	7	12195	55	BvP	Übergangshorizont
15424	108	7	12196	56	CvP	Übergangshorizont
15425	108	7	12197	57	SwP	Übergangshorizont
15426	108	7	12198	58	SdP	Übergangshorizont
15427	108	7	12199	59	Pv	Min. Unterboden, verwittert
15428	108	7	12200	60	T	Min. Unterboden aus Carbonatlösungsrückstand
15429	108	7	12201	61	BvT	Übergangshorizont
15430	108	7	12202	62	Tc	c von carbonatangereichert
15431	108	7	12203	63	Sw	Mineralboden, stauwasserleitend
15432	108	7	12204	64	Sew	e von eluviert (nassgebleicht)
15433	108	7	12205	65	AhSw	Übergangshorizont
15434	108	7	12206	66	AaSw	Übergangshorizont
15435	108	7	12207	67	AlSw	Übergangshorizont
15436	108	7	12208	68	BvSw	Übergangshorizont
15437	108	7	12209	69	BhSw	Übergangshorizont
15438	108	7	12210	70	BsSw	Übergangshorizont
15439	108	7	12211	71	PSw	Übergangshorizont
15440	108	7	12212	72	MSw	Übergangshorizont
15441	108	7	12213	73	Sd	Mineralboden, wasserstauend
15442	108	7	12214	74	Srd	r von reduziert
15443	108	7	12215	75	Sg	Mineralboden, haftnass
15444	108	7	12216	76	PSd	Übergangshorizont
15445	108	7	12217	77	BtSg	Übergangshorizont
15446	108	7	12218	78	BvSg	Übergangshorizont
15447	108	7	12219	79	Go	Mineralboden, oxidiert
15448	108	7	12220	80	fGo	f von fossil (begraben, überdeckt)
15449	108	7	12221	81	GcO	c von carbonatangereichert
15450	108	7	12222	82	Gro	r von reduziert
15451	108	7	12223	83	AhGo	Übergangshorizont
15452	108	7	12224	84	AaGo	Übergangshorizont
15453	108	7	12225	85	AeGo	Übergangshorizont
15454	108	7	12226	86	BvGo	Übergangshorizont
15455	108	7	12227	87	MGo	Übergangshorizont
15456	108	7	12228	88	SdGo	Übergangshorizont
15457	108	7	12229	89	Gr	Mineralboden, reduziert
15458	108	7	12230	90	Gcr	c von carbonatangereichert
15459	108	7	12231	91	Gcor	o von oxidiert
15460	108	7	12232	92	Gor	o von oxidiert
15461	108	7	12233	93	CGr	Übergangshorizont
15462	108	7	12234	94	Mc	c von carbonatangereichert
15463	108	7	12235	95	M	Sedimentierter Mineralboden
15464	108	7	12236	96	yM	Sed. Mineralboden aus künstl. Substrat
15465	108	7	12237	97	jM	Sed. Mineralboden aus natürl. Substrat
15466	108	7	12238	98	jY	Natürliches Substrat anthropogen verlagert
15467	108	7	12239	99	yY	Künstliches Substrat anthropogen verlagert
15468	109	7	12140	0	Horizont	\N
15469	109	7	12141	1	nH	Niedermoortorf
15470	109	7	12142	2	uH	Übergangsmoortorf
15471	109	7	12143	3	hH	Hochmoortorf
15472	109	7	12144	4	Hp	Torf, bearbeitet durch Pflügen
15473	109	7	12145	5	L	Nadel-, Laubstreu
15474	109	7	12146	6	Of	Auflagehumus, schwach zersetzt
15475	109	7	12147	7	Oh	Auflagehumus, stark zersetzt
15476	109	7	12148	8	Ah	Mineralischer Oberboden, humos
15477	109	7	12149	9	fAh	f von fossil (begraben, überdeckt)
15478	109	7	12150	10	Ach	c von carbonatangereichert
15479	109	7	12151	11	Aeh	e von eluviert (sauergebleicht)
15480	109	7	12152	12	BvAh	Übergangshorizont
15481	109	7	12153	13	SwAh	Übergangshorizont
15482	109	7	12154	14	GoAh	Übergangshorizont
15483	109	7	12155	15	yYaH	Übergangshorizont
15484	109	7	12156	16	jYAh	Übergangshorizont
15485	109	7	12157	17	Aa	Mineralischer Oberboden, anmoorig
15486	109	7	12158	18	GoAa	Übergangshorizont
15487	109	7	12159	19	GcoAa	Übergangshorizont
15488	109	7	12160	20	Ae	Mineralischer Oberboden, eluviert
15489	109	7	12161	21	Ahe	Min. Oberboden, humos, eluviert
15490	109	7	12162	22	Al	Min. Oberboden, lessiviert (tonausgewaschen)
15491	109	7	12163	23	BvAl	Übergangshorizont
15492	109	7	12164	24	SwAl	Übergangshorizont
15493	109	7	12165	25	Ap	Min. Oberboden, gepflügt
15494	109	7	12166	26	BV	Min. Unterboden, verwittert
15495	109	7	12167	27	fBv	f von fossil (begraben, überdeckt)
15496	109	7	12168	28	Bcv	c von carbonatangereichert
15497	109	7	12169	29	Bsv	s von sesquioxidangereichert
15498	109	7	12170	30	Btv	t von tonangereichert
15499	109	7	12171	31	AhBv	Übergangshorizont
15500	109	7	12172	32	AlBv	Übergangshorizont
15501	109	7	12173	33	MBv	Übergangshorizont
15502	109	7	12174	34	SwBv	Übergangshorizont
15503	109	7	12175	35	GoBv	Übergangshorizont
15504	109	7	12176	36	Bh	Min. Unterboden, huminstoffangereichert
15505	109	7	12177	37	Bsh	s von sesquioxidangereichert
15506	109	7	12178	38	Bs	s von sesquioxidangereichert
15507	109	7	12179	39	Bhs	h von huminstoffangereichert
15508	109	7	12180	40	SwBhs	Übergangshorizont
15509	109	7	12181	41	SdBhs	Übergangshorizont
15510	109	7	12182	42	Bt	Min. Unterboden, tonangereichert
15511	109	7	12183	43	Bvt	v von verwittert
15512	109	7	12184	44	SdBt	Übergangshorizont
15513	109	7	12185	45	Bu	Min. Unterboden, ferrallitisiert
15514	109	7	12186	46	Bj	Min. Unterboden der Plastosole
15515	109	7	12187	47	Cv	Min. Unterboden, verwittert
15516	109	7	12188	48	BvCv	Übergangshorizont
15517	109	7	12189	49	TCv	Übergangshorizont
15518	109	7	12190	50	SdCv	Übergangshorizont
15519	109	7	12191	51	AhCv	Übergangshorizont
15520	109	7	12192	52	Cn	Min. Untergrund, unverwittert
15521	109	7	12193	53	P	Min. Unterboden aus Tonstein
15522	109	7	12194	54	AhP	Übergangshorizont
15523	109	7	12195	55	BvP	Übergangshorizont
15524	109	7	12196	56	CvP	Übergangshorizont
15525	109	7	12197	57	SwP	Übergangshorizont
15526	109	7	12198	58	SdP	Übergangshorizont
15527	109	7	12199	59	Pv	Min. Unterboden, verwittert
15528	109	7	12200	60	T	Min. Unterboden aus Carbonatlösungsrückstand
15529	109	7	12201	61	BvT	Übergangshorizont
15530	109	7	12202	62	Tc	c von carbonatangereichert
15531	109	7	12203	63	Sw	Mineralboden, stauwasserleitend
15532	109	7	12204	64	Sew	e von eluviert (nassgebleicht)
15533	109	7	12205	65	AhSw	Übergangshorizont
15534	109	7	12206	66	AaSw	Übergangshorizont
15535	109	7	12207	67	AlSw	Übergangshorizont
15536	109	7	12208	68	BvSw	Übergangshorizont
15537	109	7	12209	69	BhSw	Übergangshorizont
15538	109	7	12210	70	BsSw	Übergangshorizont
15539	109	7	12211	71	PSw	Übergangshorizont
15540	109	7	12212	72	MSw	Übergangshorizont
15541	109	7	12213	73	Sd	Mineralboden, wasserstauend
15542	109	7	12214	74	Srd	r von reduziert
15543	109	7	12215	75	Sg	Mineralboden, haftnass
15544	109	7	12216	76	PSd	Übergangshorizont
15545	109	7	12217	77	BtSg	Übergangshorizont
15546	109	7	12218	78	BvSg	Übergangshorizont
15547	109	7	12219	79	Go	Mineralboden, oxidiert
15548	109	7	12220	80	fGo	f von fossil (begraben, überdeckt)
15549	109	7	12221	81	GcO	c von carbonatangereichert
15550	109	7	12222	82	Gro	r von reduziert
15551	109	7	12223	83	AhGo	Übergangshorizont
15552	109	7	12224	84	AaGo	Übergangshorizont
15553	109	7	12225	85	AeGo	Übergangshorizont
15554	109	7	12226	86	BvGo	Übergangshorizont
15555	109	7	12227	87	MGo	Übergangshorizont
15556	109	7	12228	88	SdGo	Übergangshorizont
15557	109	7	12229	89	Gr	Mineralboden, reduziert
15558	109	7	12230	90	Gcr	c von carbonatangereichert
15559	109	7	12231	91	Gcor	o von oxidiert
15560	109	7	12232	92	Gor	o von oxidiert
15561	109	7	12233	93	CGr	Übergangshorizont
15562	109	7	12234	94	Mc	c von carbonatangereichert
15563	109	7	12235	95	M	Sedimentierter Mineralboden
15564	109	7	12236	96	yM	Sed. Mineralboden aus künstl. Substrat
15565	109	7	12237	97	jM	Sed. Mineralboden aus natürl. Substrat
15566	109	7	12238	98	jY	Natürliches Substrat anthropogen verlagert
15567	109	7	12239	99	yY	Künstliches Substrat anthropogen verlagert
15568	119	7	10173	0	Probenahme	\N
15569	119	7	10174	1	Stichprobe	\N
15570	119	7	12505	2	Stichprobe, geschöpft	\N
15571	119	7	10176	3	Stichprobe, gepumpt	\N
15572	119	7	10177	4	Mischprobe aus Stichproben	\N
15573	119	7	10178	5	Mischprobe	\N
15574	119	7	10179	6	Kontinuierliche Sammelprobe	\N
15575	119	7	10180	7	Mischprobe auf Wegstrecke	\N
15576	119	7	10181	8	In Situ-Messung	\N
15577	120	7	10173	0	Probenahme	\N
15578	120	7	10174	1	Stichprobe	\N
15579	120	7	12505	2	Stichprobe, geschöpft	\N
15580	120	7	10176	3	Stichprobe, gepumpt	\N
15581	120	7	10177	4	Mischprobe aus Stichproben	\N
15582	120	7	10178	5	Mischprobe	\N
15583	120	7	10179	6	Kontinuierliche Sammelprobe	\N
15584	120	7	10180	7	Mischprobe auf Wegstrecke	\N
15585	120	7	10181	8	In Situ-Messung	\N
15586	121	7	10173	0	Probenahme	\N
15587	121	7	10174	1	Stichprobe	\N
15588	121	7	12505	2	Stichprobe, geschöpft	\N
15589	121	7	10176	3	Stichprobe, gepumpt	\N
15590	121	7	10177	4	Mischprobe aus Stichproben	\N
15591	121	7	10178	5	Mischprobe	\N
15592	121	7	10179	6	Kontinuierliche Sammelprobe	\N
15593	121	7	10180	7	Mischprobe auf Wegstrecke	\N
15594	121	7	12554	9	Zeitproportionale Sammelprobe	\N
15595	121	7	12555	10	Mengenproportionale Sammelprobe	\N
15596	122	7	10173	0	Probenahme	\N
15597	122	7	10174	1	Stichprobe	\N
15598	122	7	12505	2	Stichprobe, geschöpft	\N
15599	122	7	10176	3	Stichprobe, gepumpt	\N
15600	122	7	10177	4	Mischprobe aus Stichproben	\N
15601	122	7	10178	5	Mischprobe	\N
15602	122	7	10179	6	Kontinuierliche Sammelprobe	\N
15603	122	7	10180	7	Mischprobe auf Wegstrecke	\N
15604	122	7	10181	8	In Situ-Messung	\N
15605	123	7	10173	0	Probenahme	\N
15606	123	7	10174	1	Stichprobe	\N
15607	123	7	12505	2	Stichprobe, geschöpft	\N
15608	123	7	10176	3	Stichprobe, gepumpt	\N
15609	123	7	10177	4	Mischprobe aus Stichproben	\N
15610	123	7	10178	5	Mischprobe	\N
15611	123	7	10179	6	Kontinuierliche Sammelprobe	\N
15612	123	7	10180	7	Mischprobe auf Wegstrecke	\N
15613	123	7	10181	8	In Situ-Messung	\N
15614	124	7	10173	0	Probenahme	\N
15615	124	7	10174	1	Stichprobe	\N
15616	124	7	12505	2	Stichprobe, geschöpft	\N
15617	124	7	10176	3	Stichprobe, gepumpt	\N
15618	124	7	10177	4	Mischprobe aus Stichproben	\N
15619	124	7	10178	5	Mischprobe	\N
15620	124	7	10179	6	Kontinuierliche Sammelprobe	\N
15621	124	7	10180	7	Mischprobe auf Wegstrecke	\N
15622	126	7	10173	0	Probenahme	\N
15623	126	7	12673	1	ca. 1000 Mikrometer Maschenweite des Planktonnetzes	\N
15624	126	7	12674	2	ca.  500 Mikrometer Maschenweite des Planktonnetzes	\N
15625	126	7	12675	3	ca.  250 Mikrometer Maschenweite des Planktonnetzes	\N
15626	127	7	10173	0	Probenahme	\N
15627	127	7	10174	1	Stichprobe	\N
15628	127	7	12505	2	Stichprobe, geschöpft	\N
15629	127	7	10176	3	Stichprobe, gepumpt	\N
15630	127	7	10177	4	Mischprobe aus Stichproben	\N
15631	127	7	10178	5	Mischprobe	\N
15632	127	7	10179	6	Kontinuierliche Sammelprobe	\N
15633	127	7	10180	7	Mischprobe auf Wegstrecke	\N
15634	127	7	10181	8	In Situ-Messung	\N
15635	132	7	10173	0	Probenahme	\N
15636	132	7	10174	1	Stichprobe	\N
15637	132	7	10178	5	Mischprobe	\N
15638	132	7	10179	6	Kontinuierliche Sammelprobe	\N
15639	132	7	12554	9	Zeitproportionale Sammelprobe	\N
15640	132	7	12555	10	Mengenproportionale Sammelprobe	\N
15641	133	7	10173	0	Probenahme	\N
15642	133	7	10174	1	Stichprobe	\N
15643	133	7	12505	2	Stichprobe, geschöpft	\N
15644	133	7	10176	3	Stichprobe, gepumpt	\N
15645	133	7	10177	4	Mischprobe aus Stichproben	\N
15646	133	7	10178	5	Mischprobe	\N
15647	133	7	10179	6	Kontinuierliche Sammelprobe	\N
15648	133	7	10180	7	Mischprobe auf Wegstrecke	\N
15649	133	7	10181	8	In Situ-Messung	\N
15650	139	7	10173	0	Probenahme	\N
15651	139	7	10174	1	Stichprobe	\N
15652	139	7	12505	2	Stichprobe, geschöpft	\N
15653	139	7	10176	3	Stichprobe, gepumpt	\N
15654	139	7	10177	4	Mischprobe aus Stichproben	\N
15655	139	7	10178	5	Mischprobe	\N
15656	139	7	10179	6	Kontinuierliche Sammelprobe	\N
15657	139	7	10180	7	Mischprobe auf Wegstrecke	\N
15658	139	7	12733	8	gestrichen	\N
15659	139	7	12554	9	Zeitproportionale Sammelprobe	\N
15660	139	7	12555	10	Mengenproportionale Sammelprobe	\N
15661	140	7	10173	0	Probenahme	\N
15662	140	7	10174	1	Stichprobe	\N
15663	140	7	12505	2	Stichprobe, geschöpft	\N
15664	140	7	10176	3	Stichprobe, gepumpt	\N
15665	140	7	10177	4	Mischprobe aus Stichproben	\N
15666	140	7	10178	5	Mischprobe	\N
15667	140	7	10179	6	Kontinuierliche Sammelprobe	\N
15668	140	7	10180	7	Mischprobe auf Wegstrecke	\N
15669	140	7	12733	8	gestrichen	\N
15670	140	7	12554	9	Zeitproportionale Sammelprobe	\N
15671	140	7	12555	10	Mengenproportionale Sammelprobe	\N
15672	142	7	10173	0	Probenahme	\N
15673	142	7	10174	1	Stichprobe	\N
15674	142	7	12505	2	Stichprobe, geschöpft	\N
15675	142	7	10176	3	Stichprobe, gepumpt	\N
15676	142	7	10177	4	Mischprobe aus Stichproben	\N
15677	142	7	10178	5	Mischprobe	\N
15678	142	7	10179	6	Kontinuierliche Sammelprobe	\N
15679	142	7	12554	9	Zeitproportionale Sammelprobe	\N
15680	142	7	12555	10	Mengenproportionale Sammelprobe	\N
15681	143	7	10173	0	Probenahme	\N
15682	143	7	10174	1	Stichprobe	\N
15683	143	7	12505	2	Stichprobe, geschöpft	\N
15684	143	7	10176	3	Stichprobe, gepumpt	\N
15685	143	7	10177	4	Mischprobe aus Stichproben	\N
15686	143	7	10178	5	Mischprobe	\N
15687	143	7	10179	6	Kontinuierliche Sammelprobe	\N
15688	143	7	12554	9	Zeitproportionale Sammelprobe	\N
15689	143	7	12555	10	Mengenproportionale Sammelprobe	\N
15690	144	7	10173	0	Probenahme	\N
15691	144	7	10174	1	Stichprobe	\N
15692	144	7	10177	4	Mischprobe aus Stichproben	\N
15693	144	7	10178	5	Mischprobe	\N
15694	144	7	10181	8	In Situ-Messung	\N
15695	150	7	10173	0	Probenahme	\N
15696	150	7	10174	1	Stichprobe	\N
15697	150	7	12909	4	Mischprobe aus Stichprobe	\N
15698	150	7	10178	5	Mischprobe	\N
15699	150	7	12910	6	Sammelprobe	\N
15700	153	7	12973	0	Messverfahren	\N
15701	153	7	12974	1	Lucas-Kammern	\N
15702	153	7	12975	2	Zerfallsproduktmonitor	\N
15703	153	7	12976	3	Radon-Kugel	\N
15704	153	7	12977	4	Aktivkohledosimeter	\N
15705	153	7	12978	5	Foliendosimeter	\N
15706	160	7	12979	0	Art der Messung	\N
15707	160	7	12980	1	Zählrohr	\N
15708	160	7	12981	2	Festkörperdosimeter	\N
15709	160	7	12982	3	In-Situ-Messung	\N
15710	174	7	12979	0	Art der Messung	\N
15711	174	7	12980	1	Zählrohr	\N
15712	174	7	12981	2	Festkörperdosimeter	\N
15713	174	7	12982	3	In-Situ-Messung	\N
15714	24	8	760	0	Haltung-V	\N
15715	24	8	761	1	Boden	\N
15716	24	8	762	2	Käfig, Voliere	\N
15717	24	8	763	3	Freiland	\N
15718	24	8	764	4	Auslauf (intensiv)	\N
15719	25	8	1340	0	Haltung-S	\N
15720	25	8	1341	10	Haustiere	\N
15721	25	8	1342	11	Freiland, Weide	auch Wanderhaltung
15722	25	8	1343	12	Freiland, Koppel	\N
15723	25	8	1344	13	Stall, Boxen, Laufställe	\N
15724	25	8	1345	14	Alm, Fernweide	\N
15725	25	8	1346	50	Freilebende Tiere	\N
15726	25	8	1347	51	Feldtiere	\N
15727	25	8	1348	52	Wechseltiere	\N
15728	25	8	1349	53	Waldtiere	\N
15729	25	8	1350	54	Großgattertiere	\N
15730	25	8	1351	55	Aus kommerziellen Gattern	\N
15731	28	8	3298	0	Fang/Haltung-F	\N
15732	28	8	3299	1	Fließgewässer, Kanal, Bach	\N
15733	28	8	3300	2	Binnensee	\N
15734	28	8	3301	3	Talsperre, Rückhaltebecken, Staustufe	\N
15735	28	8	3302	4	Teich, Weiher, Baggersee	\N
15736	28	8	3303	5	Nordsee	\N
15737	28	8	3304	6	Ostsee	\N
15738	28	8	3305	7	Atlantik	\N
15739	28	8	3306	8	Ästuar oder Küstenbereich	\N
15740	28	8	3307	9	Teichwirtschaft Quellwasser	\N
15741	28	8	3308	10	Teichwirtschaft Vorfluter	\N
15742	29	8	3298	0	Fang/Haltung-F	\N
15743	29	8	3299	1	Fließgewässer, Kanal, Bach	\N
15744	29	8	3300	2	Binnensee	\N
15745	29	8	3301	3	Talsperre, Rückhaltebecken, Staustufe	\N
15746	29	8	3302	4	Teich, Weiher, Baggersee	\N
15747	29	8	3303	5	Nordsee	\N
15748	29	8	3304	6	Ostsee	\N
15749	29	8	3305	7	Atlantik	\N
15750	29	8	3306	8	Ästuar oder Küstenbereich	\N
15751	29	8	3307	9	Teichwirtschaft Quellwasser	\N
15752	29	8	3308	10	Teichwirtschaft Vorfluter	\N
15753	30	8	3298	0	Fang/Haltung-F	\N
15754	30	8	3299	1	Fließgewässer, Kanal, Bach	\N
15755	30	8	3300	2	Binnensee	\N
15756	30	8	3301	3	Talsperre, Rückhaltebecken, Staustufe	\N
15757	30	8	3302	4	Teich, Weiher, Baggersee	\N
15758	30	8	3303	5	Nordsee	\N
15759	30	8	3304	6	Ostsee	\N
15760	30	8	3305	7	Atlantik	\N
15761	30	8	3306	8	Ästuar oder Küstenbereich	\N
15762	30	8	3307	9	Teichwirtschaft Quellwasser	\N
15763	30	8	3308	10	Teichwirtschaft Vorfluter	\N
15764	40	8	5441	0	Anbau	\N
15765	40	8	5442	1	Freiland ,wild	\N
15766	40	8	5443	2	Freiland ,Anbau	\N
15767	40	8	5444	3	Vlies	\N
15768	40	8	5445	4	Folie	\N
15769	40	8	5446	5	Gewächshaus	\N
15770	40	8	5447	6	Niedergewächshaus	\N
15771	40	8	5448	7	Hausgarten	\N
15772	41	8	5441	0	Anbau	\N
15773	41	8	5442	1	Freiland ,wild	\N
15774	41	8	5443	2	Freiland ,Anbau	\N
15775	41	8	5444	3	Vlies	\N
15776	41	8	5445	4	Folie	\N
15777	41	8	5446	5	Gewächshaus	\N
15778	41	8	5447	6	Niedergewächshaus	\N
15779	41	8	5448	7	Hausgarten	\N
15780	42	8	5441	0	Anbau	\N
15781	42	8	5442	1	Freiland ,wild	\N
15782	42	8	5443	2	Freiland ,Anbau	\N
15783	42	8	5444	3	Vlies	\N
15784	42	8	5445	4	Folie	\N
15785	42	8	5446	5	Gewächshaus	\N
15786	42	8	5447	6	Niedergewächshaus	\N
15787	42	8	5448	7	Hausgarten	\N
15788	42	8	12801	9	unbekannter Anbau	\N
15789	43	8	5441	0	Anbau	\N
15790	43	8	5442	1	Freiland ,wild	\N
15791	43	8	5443	2	Freiland ,Anbau	\N
15792	43	8	5444	3	Vlies	\N
15793	43	8	5445	4	Folie	\N
15794	43	8	5446	5	Gewächshaus	\N
15795	43	8	5447	6	Niedergewächshaus	\N
15796	43	8	5448	7	Hausgarten	\N
15797	44	8	6033	0	Kultur	\N
15798	44	8	6034	1	wild	\N
15799	44	8	6035	2	Raumkultur	\N
15800	44	8	6036	3	Freilandkultur	auf Rinde, Stroh
15801	44	8	6037	4	Zucht	\N
15802	45	8	6033	0	Kultur	\N
15803	45	8	6034	1	wild	\N
15804	45	8	6035	2	Raumkultur	\N
15805	45	8	6036	3	Freilandkultur	auf Rinde, Stroh
15806	45	8	6037	4	Zucht	\N
15807	46	8	5441	0	Anbau	\N
15808	46	8	5442	1	Freiland ,wild	\N
15809	46	8	5443	2	Freiland ,Anbau	\N
15810	46	8	5444	3	Vlies	\N
15811	46	8	5445	4	Folie	\N
15812	46	8	5446	5	Gewächshaus	\N
15813	46	8	5447	6	Niedergewächshaus	\N
15814	46	8	5448	7	Hausgarten	\N
15815	46	8	12801	9	unbekannter Anbau	\N
15816	47	8	5441	0	Anbau	\N
15817	47	8	5442	1	Freiland ,wild	\N
15818	47	8	5443	2	Freiland ,Anbau	\N
15819	47	8	5444	3	Vlies	\N
15820	47	8	5445	4	Folie	\N
15821	47	8	5446	5	Gewächshaus	\N
15822	47	8	5447	6	Niedergewächshaus	\N
15823	47	8	5448	7	Hausgarten	\N
15824	69	8	5441	0	Anbau	\N
15825	69	8	5442	1	Freiland ,wild	\N
15826	69	8	5443	2	Freiland ,Anbau	\N
15827	69	8	5444	3	Vlies	\N
15828	69	8	5445	4	Folie	\N
15829	69	8	5446	5	Gewächshaus	\N
15830	69	8	5447	6	Niedergewächshaus	\N
15831	69	8	5448	7	Hausgarten	\N
15832	73	8	10182	0	Versorgung	\N
15833	73	8	10183	1	ZTV, Rohwasser	ZTV = WV,Zentrale Trinkwasserversorgung
15834	73	8	10184	2	ZTV, abgegebenes Trinkwasser	\N
15835	73	8	10185	3	Eigen-,Einzel-WV, Rohwasser	\N
15836	73	8	10186	4	Mineral-,Tafelwasser	\N
15837	73	8	12799	5	zentrale Trinkwasserversorgung	\N
15838	73	8	12800	6	Eigen-, Einzel-Wasserversorgung	\N
15839	119	8	12506	0	Probenart	\N
15840	119	8	12507	1	Filtriert	\N
15841	119	8	12508	2	Unfiltriert	\N
15842	120	8	12543	0	Probenbehandlung	\N
15843	120	8	12544	1	filtrierte Probe	\N
15844	120	8	12545	2	unfiltrierte Probe	\N
15845	122	8	12543	0	Probenbehandlung	\N
15846	122	8	12570	1	Unbehandelte Probe	\N
15847	122	8	12571	2	Grob sortierte Probe	\N
15848	122	8	12572	3	Korngröße 200 Mikrometer	\N
15849	122	8	12573	4	Korngröße 100 Mikrometer	\N
15850	122	8	12574	5	Korngröße  63 Mikrometer	\N
15851	122	8	12575	6	Korngröße  20 Mikrometer	\N
15852	142	8	12543	0	Probenbehandlung	\N
15853	142	8	12796	1	Filterung	\N
15854	142	8	12797	2	Siebung, Sichtung	\N
15855	142	8	12798	3	Zerkleinerung (schreddern, mahlen, zerstoßen, usw.)	\N
15856	143	8	12543	0	Probenbehandlung	\N
15857	143	8	12796	1	Filterung	\N
15858	143	8	12797	2	Siebung, Sichtung	\N
15859	143	8	12798	3	Zerkleinerung (schreddern, mahlen, zerstoßen, usw.)	\N
15860	73	9	12582	0	Untersuchtes Medium	\N
15861	73	9	12583	1	Rohwasser	\N
15862	73	9	12584	2	Reinwasser	\N
15863	108	9	12240	0	Ausgangsgestein (Petrographie)	\N
15864	108	9	12241	1	Ton	\N
15865	108	9	12242	2	Schluff	\N
15866	108	9	12243	3	Sand	\N
15867	108	9	12244	4	Lehm	\N
15868	108	9	12245	5	Schotter	\N
15869	108	9	12246	6	Schutt	\N
15870	108	9	12247	7	Mergel	\N
15871	108	9	12248	8	Tonstein	\N
15872	108	9	12249	9	Schluffstein	\N
15873	108	9	12250	10	Sandstein	\N
15874	108	9	12251	11	Konglomerat und Brekzien	\N
15875	108	9	12252	12	Mergelstein	\N
15876	108	9	12253	13	Tonmergelstein	\N
15877	108	9	12254	14	Kalkmergelstein	\N
15878	108	9	12255	15	Kalksandstein	\N
15879	108	9	12256	16	Kalkstein	\N
15880	108	9	12257	17	Dolomitstein	\N
15881	108	9	12258	18	Gipsstein	\N
15882	108	9	12259	19	Kieselschiefer	Radiolarit
15883	108	9	12260	20	Hochmoortorf	\N
15884	108	9	12261	21	Übergangsmoortorf	\N
15885	108	9	12262	22	Niedermoortorf	\N
15886	108	9	12263	23	Alm, Dauch	lockere Karbonatfällungen
15887	108	9	12264	24	Seekreide	\N
15888	108	9	12265	25	Moränenmaterial	\N
15889	108	9	12266	26	Talfüllung	\N
15890	108	9	12267	27	Terrassensand	\N
15891	108	9	12268	28	Auenlehm	\N
15892	108	9	12269	29	Kolluvium	\N
15893	108	9	12270	30	Sandlöß	\N
15894	108	9	12271	31	Löß, Lößlehm	\N
15895	108	9	12272	32	Lößlehm-Fließerde	mit Beimengungen aus dem geolog. Anstehenden
15896	108	9	12273	33	Hangschutt, Fließerde	\N
15897	108	9	12274	34	Paläboden	\N
15898	108	9	12275	35	Aufschüttung	\N
15899	108	9	12276	50	Granite	\N
15900	108	9	12277	51	Dorite	einschl. Quarz- u. Granodioriten
15901	108	9	12278	52	Gabbro	einschl. Syenit
15902	108	9	12279	53	Quarzporphyr	\N
15903	108	9	12280	54	Diabas	\N
15904	108	9	12281	55	Basalte	\N
15905	108	9	12282	56	Basalttuff	\N
15906	108	9	12283	70	Quarz	\N
15907	108	9	12284	71	Quarzphyllit	\N
15908	108	9	12285	72	Quarzite u. Quarzitschiefer	\N
15909	108	9	12286	73	Gneise	\N
15910	108	9	12287	74	Phyllite	\N
15911	108	9	12288	75	Glimmerschiefer	\N
15912	108	9	12289	76	Tonschiefer	\N
15913	108	9	12290	77	Grünschiefer	\N
15914	108	9	12291	78	Serpentinite	\N
15915	108	9	12292	79	Amphibolite u. Hornblendegneise	\N
15916	108	9	12293	80	Kalksilikatschiefer u. -felsen	\N
15917	108	9	12294	81	Marmor	\N
15918	108	9	12295	82	Mylonit	\N
15919	108	9	12296	83	Aplite	\N
15920	108	9	12297	84	Pegmatite	\N
15921	109	9	12240	0	Ausgangsgestein (Petrographie)	\N
15922	109	9	12241	1	Ton	\N
15923	109	9	12242	2	Schluff	\N
15924	109	9	12243	3	Sand	\N
15925	109	9	12244	4	Lehm	\N
15926	109	9	12245	5	Schotter	\N
15927	109	9	12246	6	Schutt	\N
15928	109	9	12247	7	Mergel	\N
15929	109	9	12248	8	Tonstein	\N
15930	109	9	12249	9	Schluffstein	\N
15931	109	9	12250	10	Sandstein	\N
15932	109	9	12251	11	Konglomerat und Brekzien	\N
15933	109	9	12252	12	Mergelstein	\N
15934	109	9	12253	13	Tonmergelstein	\N
15935	109	9	12254	14	Kalkmergelstein	\N
15936	109	9	12255	15	Kalksandstein	\N
15937	109	9	12256	16	Kalkstein	\N
15938	109	9	12257	17	Dolomitstein	\N
15939	109	9	12258	18	Gipsstein	\N
15940	109	9	12259	19	Kieselschiefer	Radiolarit
15941	109	9	12260	20	Hochmoortorf	\N
15942	109	9	12261	21	Übergangsmoortorf	\N
15943	109	9	12262	22	Niedermoortorf	\N
15944	109	9	12263	23	Alm, Dauch	lockere Karbonatfällungen
15945	109	9	12264	24	Seekreide	\N
15946	109	9	12265	25	Moränenmaterial	\N
15947	109	9	12266	26	Talfüllung	\N
15948	109	9	12267	27	Terrassensand	\N
15949	109	9	12268	28	Auenlehm	\N
15950	109	9	12269	29	Kolluvium	\N
15951	109	9	12270	30	Sandlöß	\N
15952	109	9	12271	31	Löß, Lößlehm	\N
15953	109	9	12272	32	Lößlehm-Fließerde	mit Beimengungen aus dem geolog. Anstehenden
15954	109	9	12273	33	Hangschutt, Fließerde	\N
15955	109	9	12274	34	Paläboden	\N
15956	109	9	12275	35	Aufschüttung	\N
15957	109	9	12276	50	Granite	\N
15958	109	9	12277	51	Dorite	einschl. Quarz- u. Granodioriten
15959	109	9	12278	52	Gabbro	einschl. Syenit
15960	109	9	12279	53	Quarzporphyr	\N
15961	109	9	12280	54	Diabas	\N
15962	109	9	12281	55	Basalte	\N
15963	109	9	12282	56	Basalttuff	\N
15964	109	9	12283	70	Quarz	\N
15965	109	9	12284	71	Quarzphyllit	\N
15966	109	9	12285	72	Quarzite u. Quarzitschiefer	\N
15967	109	9	12286	73	Gneise	\N
15968	109	9	12287	74	Phyllite	\N
15969	109	9	12288	75	Glimmerschiefer	\N
15970	109	9	12289	76	Tonschiefer	\N
15971	109	9	12290	77	Grünschiefer	\N
15972	109	9	12291	78	Serpentinite	\N
15973	109	9	12292	79	Amphibolite u. Hornblendegneise	\N
15974	109	9	12293	80	Kalksilikatschiefer u. -felsen	\N
15975	109	9	12294	81	Marmor	\N
15976	109	9	12295	82	Mylonit	\N
15977	109	9	12296	83	Aplite	\N
15978	109	9	12297	84	Pegmatite	\N
15979	119	9	12509	0	Wetter	\N
15980	119	9	12510	1	Trocken	\N
15981	119	9	12511	2	Nebel	\N
15982	119	9	12512	3	Sprühregen	\N
15983	119	9	12513	4	Regen	\N
15984	119	9	12514	5	Schauer	\N
15985	119	9	12515	6	Schnee	\N
15986	119	9	12516	7	Hagel	\N
15987	122	9	12576	0	Probenahmestelle	\N
15988	122	9	12577	1	Gewässerboden oder -sohle	\N
15989	122	9	12578	2	Ufer-, Böschungsbereich	\N
15990	122	9	12579	3	Überschwemmungsgebiet	\N
15991	122	9	12580	4	Meeressediment	\N
15992	122	9	12581	5	Ästuar- und Küstenbereich	\N
15993	161	9	12509	0	Wetter	\N
15994	161	9	12510	1	Trocken	\N
15995	161	9	12511	2	Nebel	\N
15996	161	9	12512	3	Sprühregen	\N
15997	161	9	12513	4	Regen	\N
15998	161	9	12514	5	Schauer	\N
15999	161	9	12515	6	Schnee	\N
16000	161	9	12516	7	Hagel	\N
16001	162	9	12509	0	Wetter	\N
16002	163	9	12509	0	Wetter	\N
16003	163	9	12510	1	Trocken	\N
16004	163	9	12511	2	Nebel	\N
16005	163	9	12512	3	Sprühregen	\N
16006	163	9	12513	4	Regen	\N
16007	163	9	12514	5	Schauer	\N
16008	163	9	12515	6	Schnee	\N
16009	163	9	12516	7	Hagel	\N
16010	108	10	12298	0	Stratigraphische Einheit	\N
16011	108	10	12299	1	Quartär	ungegliedert
16012	108	10	12300	2	Holozän	\N
16013	108	10	12301	3	Pleistozän	ungegliedert
16014	108	10	12302	4	Würmeiszeit	Niederterrassen
16015	108	10	12303	5	Rißeiszeit	Hochterrassen
16016	108	10	12304	6	Altpleistozän	Mindeleiszeit und älter (Deckenschotter)
16017	108	10	12305	7	Tertiär	ungegliedert
16018	108	10	12306	8	Pliozän	\N
16019	108	10	12307	9	Miozän	(OSM, OMM, SBM)
16020	108	10	12308	10	Oligozän	\N
16021	108	10	12309	11	Alttertiär	Eozän u. Paläozän
16022	108	10	12310	12	Kreide	ungegliedert
16023	108	10	12311	13	Oberkreide	ungegliedert
16024	108	10	12312	14	Höhere Oberkreide	Santon, Campan, Maastricht
16025	108	10	12313	15	Untere Oberkreide	ungegliedert
16026	108	10	12314	16	Coniac	(z.B. Cardienton)
16027	108	10	12315	17	Turon	(z.B. Obere Michelfelder Sch., Pulverturm-Sch., Reinh. Sch., Freihölser Bausand, Großb. Sandst., Weilloher-Mergel, Knollensand)
16028	108	10	12316	18	Cenoman	(z.B. Amberger Erzformation, Untere Michelfelder Schichten, Schutzfelsschichten, Regensburger Grünsandstein, Eibrunner Mergel)
16029	108	10	12317	19	Unterkreide	ungegliedert (nur Alpenraum)
16030	108	10	12318	20	Gault	Höhere Unterkreide: Alb + Apt
16031	108	10	12319	21	Neokom	Untere Unterkreide: Barreme, Hauterive, Valendis
16032	108	10	12320	22	Jura	ungegliedert
16033	108	10	12321	23	Malm	ungegliedert
16034	108	10	12322	24	Tithon	Neuburger-, Rennertshofener-, Usseltal-, Solnhofener Schichten (w Zeta)
16035	108	10	12323	25	Kimmeridge	(w Gamma, Delta, Epsilon); Obere Mergelkalke, Treuchtlinger Marmor
16036	108	10	12324	26	Oxford	(w Alpha, Beta); Untere Mergelkalke, Werkkalke
16037	108	10	12325	27	Dogger	ungegliedert
16038	108	10	12326	28	Bajoc, Callov	Ornatenton (b Zeta)
16039	108	10	12327	29	Eisensandstein	(b Beta)
16040	108	10	12328	30	Opalinuston	(b Alpha)
16041	108	10	12329	31	Schwarzer Jura	Lias; ungegliedert
16042	108	10	12330	32	Toarc	Jurensismergel und Posidonienschiefer; (1 Epsilon + Zeta)
16043	108	10	12331	33	Pliensbach	Numismalismergel, Amaltheenton (1 Gamma + Delta)
16044	108	10	12332	34	Sinemur	Arietensandstein; (1 Alpha 3) Raricostaten (1 Beta)
16045	108	10	12333	35	Hettang	Angulatensandstein (1 Alpha 3)
16046	108	10	12334	36	Keuper	ungegliedert
16047	108	10	12335	37	Oberer Keuper	Rhaet, Rhaetolias
16048	108	10	12336	38	Feuerletten, Knollenmergel	\N
16049	108	10	12337	39	Sandsteinkeuper	Blasensandstein, Coburger Sandstein, Burgsandstein
16050	108	10	12338	40	Gipskeuper	Myophorienschichten, Estherienschichten, Schilfsandstein, Lehrbergschichten, Gips
16051	108	10	12339	41	Unterer Keuper	Lettenkeuper;, Grenzdolomit
16052	108	10	12340	42	Muschelkalk	ungegliedert
16053	108	10	12341	43	Oberer Muschelkalk	Hauptmuschelkalk, Quaderkalk
16054	108	10	12342	44	Mittlerer Muschelkalk	\N
16055	108	10	12343	45	Unterer Muschelkalk	Wellenkalk
16056	108	10	12344	46	Buntsandstein	ungegliedert
16057	108	10	12345	47	Oberer Buntsandstein	Chirotherienschichten, Untere Röttonsteine, Untere Plattensandsteine, Rötquarzit, Obere Röttonsteine
16058	108	10	12346	48	Mittlerer Buntsandstein	Geiersberg-Folge, Felssandstein-Folge, Rohrbrunn-Folge
16059	108	10	12347	49	Unterer Buntsandstein	Bröckelschiefer, Heigenbrücker Sandstein, Geröllsandstein, Miltenberger-Folge
16060	108	10	12348	50	Zechstein	ungegliedert (Z1 - Z4)
16061	108	10	12349	51	Rotliegendes	ungegliedert
16062	108	10	12350	52	Oberrotliegendes	\N
16063	108	10	12351	53	Unterrotliegendes	\N
16064	108	10	12352	54	Palaeozoikum und Praekambrium	ungegliedert, (Magmatische und meta-morphe Serien im Nordbayerischen Kristallin)
16065	109	10	12298	0	Stratigraphische Einheit	\N
16066	109	10	12299	1	Quartär	ungegliedert
16067	109	10	12300	2	Holozän	\N
16068	109	10	12301	3	Pleistozän	ungegliedert
16069	109	10	12302	4	Würmeiszeit	Niederterrassen
16070	109	10	12303	5	Rißeiszeit	Hochterrassen
16071	109	10	12304	6	Altpleistozän	Mindeleiszeit und älter (Deckenschotter)
16072	109	10	12305	7	Tertiär	ungegliedert
16073	109	10	12306	8	Pliozän	\N
16074	109	10	12307	9	Miozän	(OSM, OMM, SBM)
16075	109	10	12308	10	Oligozän	\N
16076	109	10	12309	11	Alttertiär	Eozän u. Paläozän
16077	109	10	12310	12	Kreide	ungegliedert
16078	109	10	12311	13	Oberkreide	ungegliedert
16079	109	10	12312	14	Höhere Oberkreide	Santon, Campan, Maastricht
16080	109	10	12313	15	Untere Oberkreide	ungegliedert
16081	109	10	12314	16	Coniac	(z.B. Cardienton)
16082	109	10	12315	17	Turon	(z.B. Obere Michelfelder Sch., Pulverturm-Sch., Reinh. Sch., Freihölser Bausand, Großb. Sandst., Weilloher-Mergel, Knollensand)
16083	109	10	12316	18	Cenoman	(z.B. Amberger Erzformation, Untere Michelfelder Schichten, Schutzfelsschichten, Regensburger Grünsandstein, Eibrunner Mergel)
16084	109	10	12317	19	Unterkreide	ungegliedert (nur Alpenraum)
16085	109	10	12318	20	Gault	Höhere Unterkreide: Alb + Apt
16086	109	10	12319	21	Neokom	Untere Unterkreide: Barreme, Hauterive, Valendis
16087	109	10	12320	22	Jura	ungegliedert
16088	109	10	12321	23	Malm	ungegliedert
16089	109	10	12322	24	Tithon	Neuburger-, Rennertshofener-, Usseltal-, Solnhofener Schichten (w Zeta)
16090	109	10	12323	25	Kimmeridge	(w Gamma, Delta, Epsilon); Obere Mergelkalke, Treuchtlinger Marmor
16091	109	10	12324	26	Oxford	(w Alpha, Beta); Untere Mergelkalke, Werkkalke
16092	109	10	12325	27	Dogger	ungegliedert
16093	109	10	12326	28	Bajoc, Callov	Ornatenton (b Zeta)
16094	109	10	12327	29	Eisensandstein	(b Beta)
16095	109	10	12328	30	Opalinuston	(b Alpha)
16096	109	10	12329	31	Schwarzer Jura	Lias; ungegliedert
16097	109	10	12330	32	Toarc	Jurensismergel und Posidonienschiefer; (1 Epsilon + Zeta)
16098	109	10	12331	33	Pliensbach	Numismalismergel, Amaltheenton (1 Gamma + Delta)
16099	109	10	12332	34	Sinemur	Arietensandstein; (1 Alpha 3) Raricostaten (1 Beta)
16100	109	10	12333	35	Hettang	Angulatensandstein (1 Alpha 3)
16101	109	10	12334	36	Keuper	ungegliedert
16102	109	10	12335	37	Oberer Keuper	Rhaet, Rhaetolias
16103	109	10	12336	38	Feuerletten, Knollenmergel	\N
16104	109	10	12337	39	Sandsteinkeuper	Blasensandstein, Coburger Sandstein, Burgsandstein
16105	109	10	12338	40	Gipskeuper	Myophorienschichten, Estherienschichten, Schilfsandstein, Lehrbergschichten, Gips
16106	109	10	12339	41	Unterer Keuper	Lettenkeuper;, Grenzdolomit
16107	109	10	12340	42	Muschelkalk	ungegliedert
16108	109	10	12341	43	Oberer Muschelkalk	Hauptmuschelkalk, Quaderkalk
16109	109	10	12342	44	Mittlerer Muschelkalk	\N
16110	109	10	12343	45	Unterer Muschelkalk	Wellenkalk
16111	109	10	12344	46	Buntsandstein	ungegliedert
16112	109	10	12345	47	Oberer Buntsandstein	Chirotherienschichten, Untere Röttonsteine, Untere Plattensandsteine, Rötquarzit, Obere Röttonsteine
16113	109	10	12346	48	Mittlerer Buntsandstein	Geiersberg-Folge, Felssandstein-Folge, Rohrbrunn-Folge
16114	109	10	12347	49	Unterer Buntsandstein	Bröckelschiefer, Heigenbrücker Sandstein, Geröllsandstein, Miltenberger-Folge
16115	109	10	12348	50	Zechstein	ungegliedert (Z1 - Z4)
16116	109	10	12349	51	Rotliegendes	ungegliedert
16117	109	10	12350	52	Oberrotliegendes	\N
16118	109	10	12351	53	Unterrotliegendes	\N
16119	109	10	12352	54	Palaeozoikum und Praekambrium	ungegliedert, (Magmatische und meta-morphe Serien im Nordbayerischen Kristallin)
16120	119	10	12517	0	Seegang	\N
16121	119	10	12518	1	< 0,1  m	\N
16122	119	10	12519	2	0,1  - 0,5  m	\N
16123	119	10	12520	3	0,5  - 1,25 m	\N
16124	119	10	12521	4	1,25 - 2,5  m	\N
16125	119	10	12522	5	2,5  -   4  m	\N
16126	119	10	12523	6	4    -   6  m	\N
16127	119	10	12524	7	6    -   9  m	\N
16128	119	10	12525	8	> 9  m	\N
16129	123	10	12517	0	Seegang	\N
16130	123	10	12583	1	< 0,1 m	\N
16131	123	10	12584	2	0,1  - 0,5 m	\N
16132	123	10	12520	3	0,5  - 1,25 m	\N
16133	123	10	12585	4	1,25 - 2,5 m	\N
16134	123	10	12586	5	2,5  - 4 m	\N
16135	123	10	12587	6	4    - 6 m	\N
16136	123	10	12588	7	6    - 9 m	\N
16137	123	10	12589	8	> 9 m	\N
16138	20	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
16139	20	11	2	10	Erzeuger (Urproduktion)	1000000
16140	20	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
16141	20	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
16142	20	11	5	13	Imkerei	1030000
16143	20	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
16144	20	11	7	20	Hersteller und Abpacker	2000000
16145	20	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
16146	20	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
16147	20	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
16148	20	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
16149	20	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
16150	20	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
16151	20	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
16152	20	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
16153	20	11	16	29	Hersteller von Bedarfsgegenständen	2090000
16154	20	11	17	30	Vertriebsunternehmer und Transporteure	3000000
16155	20	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
16156	20	11	19	32	Lebensmittellager	3020000
16157	20	11	20	33	Umpackbetriebe	3030000
16158	20	11	21	34	Transporteure von Lebensmitteln	3040000
16159	20	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
16160	20	11	23	36	Großhändler von kosmetischen Mitteln	3060000
16161	20	11	24	37	Großhändler von Bedarfsgegenständen	3070000
16162	20	11	25	40	Einzelhändler	4000000
16163	20	11	26	41	Lebensmitteleinzelhandel	4010000
16164	20	11	27	42	Anderer Einzelhandel	4020000
16165	20	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
16166	20	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
16167	20	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
16168	20	11	31	50	Dienstleistungsbetriebe	5000000
16169	20	11	32	51	Küchen und Kantinen	5010000
16170	20	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
16171	20	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
16172	20	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
16173	20	11	36	61	Gewerbebetriebe	6010000
16174	20	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
16175	20	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
16176	20	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
16177	20	11	40	80	Futtermittelbetriebe	8000000
16178	20	11	41	99	keine Angabe	9999999
16179	21	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
16180	21	11	2	10	Erzeuger (Urproduktion)	1000000
16181	21	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
16182	21	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
16183	21	11	5	13	Imkerei	1030000
16184	21	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
16185	21	11	7	20	Hersteller und Abpacker	2000000
16186	21	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
16187	21	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
16188	21	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
16189	21	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
16190	21	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
16191	21	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
16192	21	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
16193	21	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
16194	21	11	16	29	Hersteller von Bedarfsgegenständen	2090000
16195	21	11	17	30	Vertriebsunternehmer und Transporteure	3000000
16196	21	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
16197	21	11	19	32	Lebensmittellager	3020000
16198	21	11	20	33	Umpackbetriebe	3030000
16199	21	11	21	34	Transporteure von Lebensmitteln	3040000
16200	21	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
16201	21	11	23	36	Großhändler von kosmetischen Mitteln	3060000
16202	21	11	24	37	Großhändler von Bedarfsgegenständen	3070000
16203	21	11	25	40	Einzelhändler	4000000
16204	21	11	26	41	Lebensmitteleinzelhandel	4010000
16205	21	11	27	42	Anderer Einzelhandel	4020000
16206	21	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
16207	21	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
16208	21	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
16209	21	11	31	50	Dienstleistungsbetriebe	5000000
16210	21	11	32	51	Küchen und Kantinen	5010000
16211	21	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
16212	21	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
16213	21	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
16214	21	11	36	61	Gewerbebetriebe	6010000
16215	21	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
16216	21	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
16217	21	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
16218	21	11	40	80	Futtermittelbetriebe	8000000
16219	21	11	41	99	keine Angabe	9999999
16220	22	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
16221	22	11	2	10	Erzeuger (Urproduktion)	1000000
16222	22	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
16223	22	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
16224	22	11	5	13	Imkerei	1030000
16225	22	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
16226	22	11	7	20	Hersteller und Abpacker	2000000
16227	22	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
16228	22	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
16229	22	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
16230	22	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
16231	22	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
16232	22	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
16233	22	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
16234	22	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
16235	22	11	16	29	Hersteller von Bedarfsgegenständen	2090000
16236	22	11	17	30	Vertriebsunternehmer und Transporteure	3000000
16237	22	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
16238	22	11	19	32	Lebensmittellager	3020000
16239	22	11	20	33	Umpackbetriebe	3030000
16240	22	11	21	34	Transporteure von Lebensmitteln	3040000
16241	22	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
16242	22	11	23	36	Großhändler von kosmetischen Mitteln	3060000
16243	22	11	24	37	Großhändler von Bedarfsgegenständen	3070000
16244	22	11	25	40	Einzelhändler	4000000
16245	22	11	26	41	Lebensmitteleinzelhandel	4010000
16246	22	11	27	42	Anderer Einzelhandel	4020000
16247	22	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
16248	22	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
16249	22	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
16250	22	11	31	50	Dienstleistungsbetriebe	5000000
16251	22	11	32	51	Küchen und Kantinen	5010000
16252	22	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
16253	22	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
16254	22	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
16255	22	11	36	61	Gewerbebetriebe	6010000
16256	22	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
16257	22	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
16258	22	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
16259	22	11	40	80	Futtermittelbetriebe	8000000
16260	22	11	41	99	keine Angabe	9999999
16261	23	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
16262	23	11	2	10	Erzeuger (Urproduktion)	1000000
16263	23	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
16264	23	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
16265	23	11	5	13	Imkerei	1030000
16266	23	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
16267	23	11	7	20	Hersteller und Abpacker	2000000
16268	23	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
16269	23	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
16270	23	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
16271	23	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
16272	23	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
16273	23	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
16274	23	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
16275	23	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
16276	23	11	16	29	Hersteller von Bedarfsgegenständen	2090000
16277	23	11	17	30	Vertriebsunternehmer und Transporteure	3000000
16278	23	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
16279	23	11	19	32	Lebensmittellager	3020000
16280	23	11	20	33	Umpackbetriebe	3030000
16281	23	11	21	34	Transporteure von Lebensmitteln	3040000
16282	23	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
16283	23	11	23	36	Großhändler von kosmetischen Mitteln	3060000
16284	23	11	24	37	Großhändler von Bedarfsgegenständen	3070000
16285	23	11	25	40	Einzelhändler	4000000
16286	23	11	26	41	Lebensmitteleinzelhandel	4010000
16287	23	11	27	42	Anderer Einzelhandel	4020000
16288	23	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
16289	23	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
16290	23	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
16291	23	11	31	50	Dienstleistungsbetriebe	5000000
16292	23	11	32	51	Küchen und Kantinen	5010000
16293	23	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
16294	23	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
16295	23	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
16296	23	11	36	61	Gewerbebetriebe	6010000
16297	23	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
16298	23	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
16299	23	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
16300	23	11	40	80	Futtermittelbetriebe	8000000
16301	23	11	41	99	keine Angabe	9999999
16302	24	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
16303	24	11	2	10	Erzeuger (Urproduktion)	1000000
16304	24	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
16305	24	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
16306	24	11	5	13	Imkerei	1030000
16307	24	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
16308	24	11	7	20	Hersteller und Abpacker	2000000
16309	24	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
16310	24	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
16311	24	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
16312	24	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
16313	24	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
16314	24	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
16315	24	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
16316	24	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
16317	24	11	16	29	Hersteller von Bedarfsgegenständen	2090000
16318	24	11	17	30	Vertriebsunternehmer und Transporteure	3000000
16319	24	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
16320	24	11	19	32	Lebensmittellager	3020000
16321	24	11	20	33	Umpackbetriebe	3030000
16322	24	11	21	34	Transporteure von Lebensmitteln	3040000
16323	24	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
16324	24	11	23	36	Großhändler von kosmetischen Mitteln	3060000
16325	24	11	24	37	Großhändler von Bedarfsgegenständen	3070000
16326	24	11	25	40	Einzelhändler	4000000
16327	24	11	26	41	Lebensmitteleinzelhandel	4010000
16328	24	11	27	42	Anderer Einzelhandel	4020000
16329	24	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
16330	24	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
16331	24	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
16332	24	11	31	50	Dienstleistungsbetriebe	5000000
16333	24	11	32	51	Küchen und Kantinen	5010000
16334	24	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
16335	24	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
16336	24	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
16337	24	11	36	61	Gewerbebetriebe	6010000
16338	24	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
16339	24	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
16340	24	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
16341	24	11	40	80	Futtermittelbetriebe	8000000
16342	24	11	41	99	keine Angabe	9999999
16343	25	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
16344	25	11	2	10	Erzeuger (Urproduktion)	1000000
16345	25	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
16346	25	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
16347	25	11	5	13	Imkerei	1030000
16348	25	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
16349	25	11	7	20	Hersteller und Abpacker	2000000
16350	25	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
16351	25	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
16352	25	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
16353	25	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
16354	25	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
16355	25	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
16356	25	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
16357	25	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
16358	25	11	16	29	Hersteller von Bedarfsgegenständen	2090000
16359	25	11	17	30	Vertriebsunternehmer und Transporteure	3000000
16360	25	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
16361	25	11	19	32	Lebensmittellager	3020000
16362	25	11	20	33	Umpackbetriebe	3030000
16363	25	11	21	34	Transporteure von Lebensmitteln	3040000
16364	25	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
16365	25	11	23	36	Großhändler von kosmetischen Mitteln	3060000
16366	25	11	24	37	Großhändler von Bedarfsgegenständen	3070000
16367	25	11	25	40	Einzelhändler	4000000
16368	25	11	26	41	Lebensmitteleinzelhandel	4010000
16369	25	11	27	42	Anderer Einzelhandel	4020000
16370	25	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
16371	25	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
16372	25	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
16373	25	11	31	50	Dienstleistungsbetriebe	5000000
16374	25	11	32	51	Küchen und Kantinen	5010000
16375	25	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
16376	25	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
16377	25	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
16378	25	11	36	61	Gewerbebetriebe	6010000
16379	25	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
16380	25	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
16381	25	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
16382	25	11	40	80	Futtermittelbetriebe	8000000
16383	25	11	41	99	keine Angabe	9999999
16384	26	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
16385	26	11	2	10	Erzeuger (Urproduktion)	1000000
16386	26	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
16387	26	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
16388	26	11	5	13	Imkerei	1030000
16389	26	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
16390	26	11	7	20	Hersteller und Abpacker	2000000
16391	26	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
16392	26	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
16393	26	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
16394	26	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
16395	26	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
16396	26	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
16397	26	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
16398	26	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
16399	26	11	16	29	Hersteller von Bedarfsgegenständen	2090000
16400	26	11	17	30	Vertriebsunternehmer und Transporteure	3000000
16401	26	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
16402	26	11	19	32	Lebensmittellager	3020000
16403	26	11	20	33	Umpackbetriebe	3030000
16404	26	11	21	34	Transporteure von Lebensmitteln	3040000
16405	26	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
16406	26	11	23	36	Großhändler von kosmetischen Mitteln	3060000
16407	26	11	24	37	Großhändler von Bedarfsgegenständen	3070000
16408	26	11	25	40	Einzelhändler	4000000
16409	26	11	26	41	Lebensmitteleinzelhandel	4010000
16410	26	11	27	42	Anderer Einzelhandel	4020000
16411	26	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
16412	26	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
16413	26	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
16414	26	11	31	50	Dienstleistungsbetriebe	5000000
16415	26	11	32	51	Küchen und Kantinen	5010000
16416	26	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
16417	26	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
16418	26	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
16419	26	11	36	61	Gewerbebetriebe	6010000
16420	26	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
16421	26	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
16422	26	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
16423	26	11	40	80	Futtermittelbetriebe	8000000
16424	26	11	41	99	keine Angabe	9999999
16425	27	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
16426	27	11	2	10	Erzeuger (Urproduktion)	1000000
16427	27	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
16428	27	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
16429	27	11	5	13	Imkerei	1030000
16430	27	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
16431	27	11	7	20	Hersteller und Abpacker	2000000
16432	27	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
16433	27	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
16434	27	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
16435	27	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
16436	27	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
16437	27	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
16438	27	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
16439	27	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
16440	27	11	16	29	Hersteller von Bedarfsgegenständen	2090000
16441	27	11	17	30	Vertriebsunternehmer und Transporteure	3000000
16442	27	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
16443	27	11	19	32	Lebensmittellager	3020000
16444	27	11	20	33	Umpackbetriebe	3030000
16445	27	11	21	34	Transporteure von Lebensmitteln	3040000
16446	27	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
16447	27	11	23	36	Großhändler von kosmetischen Mitteln	3060000
16448	27	11	24	37	Großhändler von Bedarfsgegenständen	3070000
16449	27	11	25	40	Einzelhändler	4000000
16450	27	11	26	41	Lebensmitteleinzelhandel	4010000
16451	27	11	27	42	Anderer Einzelhandel	4020000
16452	27	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
16453	27	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
16454	27	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
16455	27	11	31	50	Dienstleistungsbetriebe	5000000
16456	27	11	32	51	Küchen und Kantinen	5010000
16457	27	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
16458	27	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
16459	27	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
16460	27	11	36	61	Gewerbebetriebe	6010000
16461	27	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
16462	27	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
16463	27	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
16464	27	11	40	80	Futtermittelbetriebe	8000000
16465	27	11	41	99	keine Angabe	9999999
16466	28	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
16467	28	11	2	10	Erzeuger (Urproduktion)	1000000
16468	28	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
16469	28	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
16470	28	11	5	13	Imkerei	1030000
16471	28	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
16472	28	11	7	20	Hersteller und Abpacker	2000000
16473	28	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
16474	28	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
16475	28	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
16476	28	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
16477	28	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
16478	28	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
16479	28	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
16480	28	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
16481	28	11	16	29	Hersteller von Bedarfsgegenständen	2090000
16482	28	11	17	30	Vertriebsunternehmer und Transporteure	3000000
16483	28	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
16484	28	11	19	32	Lebensmittellager	3020000
16485	28	11	20	33	Umpackbetriebe	3030000
16486	28	11	21	34	Transporteure von Lebensmitteln	3040000
16487	28	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
16488	28	11	23	36	Großhändler von kosmetischen Mitteln	3060000
16489	28	11	24	37	Großhändler von Bedarfsgegenständen	3070000
16490	28	11	25	40	Einzelhändler	4000000
16491	28	11	26	41	Lebensmitteleinzelhandel	4010000
16492	28	11	27	42	Anderer Einzelhandel	4020000
16493	28	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
16494	28	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
16495	28	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
16496	28	11	31	50	Dienstleistungsbetriebe	5000000
16497	28	11	32	51	Küchen und Kantinen	5010000
16498	28	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
16499	28	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
16500	28	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
16501	28	11	36	61	Gewerbebetriebe	6010000
16502	28	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
16503	28	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
16504	28	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
16505	28	11	40	80	Futtermittelbetriebe	8000000
16506	28	11	41	99	keine Angabe	9999999
16507	29	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
16508	29	11	2	10	Erzeuger (Urproduktion)	1000000
16509	29	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
16510	29	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
16511	29	11	5	13	Imkerei	1030000
16512	29	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
16513	29	11	7	20	Hersteller und Abpacker	2000000
16514	29	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
16515	29	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
16516	29	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
16517	29	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
16518	29	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
16519	29	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
16520	29	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
16521	29	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
16522	29	11	16	29	Hersteller von Bedarfsgegenständen	2090000
16523	29	11	17	30	Vertriebsunternehmer und Transporteure	3000000
16524	29	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
16525	29	11	19	32	Lebensmittellager	3020000
16526	29	11	20	33	Umpackbetriebe	3030000
16527	29	11	21	34	Transporteure von Lebensmitteln	3040000
16528	29	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
16529	29	11	23	36	Großhändler von kosmetischen Mitteln	3060000
16530	29	11	24	37	Großhändler von Bedarfsgegenständen	3070000
16531	29	11	25	40	Einzelhändler	4000000
16532	29	11	26	41	Lebensmitteleinzelhandel	4010000
16533	29	11	27	42	Anderer Einzelhandel	4020000
16534	29	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
16535	29	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
16536	29	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
16537	29	11	31	50	Dienstleistungsbetriebe	5000000
16538	29	11	32	51	Küchen und Kantinen	5010000
16539	29	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
16540	29	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
16541	29	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
16542	29	11	36	61	Gewerbebetriebe	6010000
16543	29	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
16544	29	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
16545	29	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
16546	29	11	40	80	Futtermittelbetriebe	8000000
16547	29	11	41	99	keine Angabe	9999999
16548	30	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
16549	30	11	2	10	Erzeuger (Urproduktion)	1000000
16550	30	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
16551	30	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
16552	30	11	5	13	Imkerei	1030000
16553	30	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
16554	30	11	7	20	Hersteller und Abpacker	2000000
16555	30	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
16556	30	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
16557	30	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
16558	30	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
16559	30	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
16560	30	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
16561	30	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
16562	30	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
16563	30	11	16	29	Hersteller von Bedarfsgegenständen	2090000
16564	30	11	17	30	Vertriebsunternehmer und Transporteure	3000000
16565	30	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
16566	30	11	19	32	Lebensmittellager	3020000
16567	30	11	20	33	Umpackbetriebe	3030000
16568	30	11	21	34	Transporteure von Lebensmitteln	3040000
16569	30	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
16570	30	11	23	36	Großhändler von kosmetischen Mitteln	3060000
16571	30	11	24	37	Großhändler von Bedarfsgegenständen	3070000
16572	30	11	25	40	Einzelhändler	4000000
16573	30	11	26	41	Lebensmitteleinzelhandel	4010000
16574	30	11	27	42	Anderer Einzelhandel	4020000
16575	30	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
16576	30	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
16577	30	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
16578	30	11	31	50	Dienstleistungsbetriebe	5000000
16579	30	11	32	51	Küchen und Kantinen	5010000
16580	30	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
16581	30	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
16582	30	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
16583	30	11	36	61	Gewerbebetriebe	6010000
16584	30	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
16585	30	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
16586	30	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
16587	30	11	40	80	Futtermittelbetriebe	8000000
16588	30	11	41	99	keine Angabe	9999999
16589	31	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
16590	31	11	2	10	Erzeuger (Urproduktion)	1000000
16591	31	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
16592	31	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
16593	31	11	5	13	Imkerei	1030000
16594	31	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
16595	31	11	7	20	Hersteller und Abpacker	2000000
16596	31	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
16597	31	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
16598	31	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
16599	31	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
16600	31	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
16601	31	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
16602	31	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
16603	31	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
16604	31	11	16	29	Hersteller von Bedarfsgegenständen	2090000
16605	31	11	17	30	Vertriebsunternehmer und Transporteure	3000000
16606	31	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
16607	31	11	19	32	Lebensmittellager	3020000
16608	31	11	20	33	Umpackbetriebe	3030000
16609	31	11	21	34	Transporteure von Lebensmitteln	3040000
16610	31	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
16611	31	11	23	36	Großhändler von kosmetischen Mitteln	3060000
16612	31	11	24	37	Großhändler von Bedarfsgegenständen	3070000
16613	31	11	25	40	Einzelhändler	4000000
16614	31	11	26	41	Lebensmitteleinzelhandel	4010000
16615	31	11	27	42	Anderer Einzelhandel	4020000
16616	31	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
16617	31	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
16618	31	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
16619	31	11	31	50	Dienstleistungsbetriebe	5000000
16620	31	11	32	51	Küchen und Kantinen	5010000
16621	31	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
16622	31	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
16623	31	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
16624	31	11	36	61	Gewerbebetriebe	6010000
16625	31	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
16626	31	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
16627	31	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
16628	31	11	40	80	Futtermittelbetriebe	8000000
16629	31	11	41	99	keine Angabe	9999999
16630	32	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
16631	32	11	2	10	Erzeuger (Urproduktion)	1000000
16632	32	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
16633	32	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
16634	32	11	5	13	Imkerei	1030000
16635	32	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
16636	32	11	7	20	Hersteller und Abpacker	2000000
16637	32	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
16638	32	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
16639	32	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
16640	32	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
16641	32	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
16642	32	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
16643	32	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
16644	32	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
16645	32	11	16	29	Hersteller von Bedarfsgegenständen	2090000
16646	32	11	17	30	Vertriebsunternehmer und Transporteure	3000000
16647	32	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
16648	32	11	19	32	Lebensmittellager	3020000
16649	32	11	20	33	Umpackbetriebe	3030000
16650	32	11	21	34	Transporteure von Lebensmitteln	3040000
16651	32	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
16652	32	11	23	36	Großhändler von kosmetischen Mitteln	3060000
16653	32	11	24	37	Großhändler von Bedarfsgegenständen	3070000
16654	32	11	25	40	Einzelhändler	4000000
16655	32	11	26	41	Lebensmitteleinzelhandel	4010000
16656	32	11	27	42	Anderer Einzelhandel	4020000
16657	32	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
16658	32	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
16659	32	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
16660	32	11	31	50	Dienstleistungsbetriebe	5000000
16661	32	11	32	51	Küchen und Kantinen	5010000
16662	32	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
16663	32	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
16664	32	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
16665	32	11	36	61	Gewerbebetriebe	6010000
16666	32	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
16667	32	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
16668	32	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
16669	32	11	40	80	Futtermittelbetriebe	8000000
16670	32	11	41	99	keine Angabe	9999999
16671	33	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
16672	33	11	2	10	Erzeuger (Urproduktion)	1000000
16673	33	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
16674	33	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
16675	33	11	5	13	Imkerei	1030000
16676	33	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
16677	33	11	7	20	Hersteller und Abpacker	2000000
16678	33	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
16679	33	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
16680	33	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
16681	33	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
16682	33	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
16683	33	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
16684	33	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
16685	33	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
16686	33	11	16	29	Hersteller von Bedarfsgegenständen	2090000
16687	33	11	17	30	Vertriebsunternehmer und Transporteure	3000000
16688	33	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
16689	33	11	19	32	Lebensmittellager	3020000
16690	33	11	20	33	Umpackbetriebe	3030000
16691	33	11	21	34	Transporteure von Lebensmitteln	3040000
16692	33	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
16693	33	11	23	36	Großhändler von kosmetischen Mitteln	3060000
16694	33	11	24	37	Großhändler von Bedarfsgegenständen	3070000
16695	33	11	25	40	Einzelhändler	4000000
16696	33	11	26	41	Lebensmitteleinzelhandel	4010000
16697	33	11	27	42	Anderer Einzelhandel	4020000
16698	33	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
16699	33	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
16700	33	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
16701	33	11	31	50	Dienstleistungsbetriebe	5000000
16702	33	11	32	51	Küchen und Kantinen	5010000
16703	33	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
16704	33	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
16705	33	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
16706	33	11	36	61	Gewerbebetriebe	6010000
16707	33	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
16708	33	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
16709	33	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
16710	33	11	40	80	Futtermittelbetriebe	8000000
16711	33	11	41	99	keine Angabe	9999999
16712	34	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
16713	34	11	2	10	Erzeuger (Urproduktion)	1000000
16714	34	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
16715	34	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
16716	34	11	5	13	Imkerei	1030000
16717	34	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
16718	34	11	7	20	Hersteller und Abpacker	2000000
16719	34	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
16720	34	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
16721	34	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
16722	34	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
16723	34	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
16724	34	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
16725	34	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
16726	34	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
16727	34	11	16	29	Hersteller von Bedarfsgegenständen	2090000
16728	34	11	17	30	Vertriebsunternehmer und Transporteure	3000000
16729	34	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
16730	34	11	19	32	Lebensmittellager	3020000
16731	34	11	20	33	Umpackbetriebe	3030000
16732	34	11	21	34	Transporteure von Lebensmitteln	3040000
16733	34	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
16734	34	11	23	36	Großhändler von kosmetischen Mitteln	3060000
16735	34	11	24	37	Großhändler von Bedarfsgegenständen	3070000
16736	34	11	25	40	Einzelhändler	4000000
16737	34	11	26	41	Lebensmitteleinzelhandel	4010000
16738	34	11	27	42	Anderer Einzelhandel	4020000
16739	34	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
16740	34	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
16741	34	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
16742	34	11	31	50	Dienstleistungsbetriebe	5000000
16743	34	11	32	51	Küchen und Kantinen	5010000
16744	34	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
16745	34	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
16746	34	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
16747	34	11	36	61	Gewerbebetriebe	6010000
16748	34	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
16749	34	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
16750	34	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
16751	34	11	40	80	Futtermittelbetriebe	8000000
16752	34	11	41	99	keine Angabe	9999999
16753	35	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
16754	35	11	2	10	Erzeuger (Urproduktion)	1000000
16755	35	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
16756	35	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
16757	35	11	5	13	Imkerei	1030000
16758	35	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
16759	35	11	7	20	Hersteller und Abpacker	2000000
16760	35	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
16761	35	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
16762	35	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
16763	35	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
16764	35	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
16765	35	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
16766	35	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
16767	35	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
16768	35	11	16	29	Hersteller von Bedarfsgegenständen	2090000
16769	35	11	17	30	Vertriebsunternehmer und Transporteure	3000000
16770	35	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
16771	35	11	19	32	Lebensmittellager	3020000
16772	35	11	20	33	Umpackbetriebe	3030000
16773	35	11	21	34	Transporteure von Lebensmitteln	3040000
16774	35	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
16775	35	11	23	36	Großhändler von kosmetischen Mitteln	3060000
16776	35	11	24	37	Großhändler von Bedarfsgegenständen	3070000
16777	35	11	25	40	Einzelhändler	4000000
16778	35	11	26	41	Lebensmitteleinzelhandel	4010000
16779	35	11	27	42	Anderer Einzelhandel	4020000
16780	35	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
16781	35	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
16782	35	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
16783	35	11	31	50	Dienstleistungsbetriebe	5000000
16784	35	11	32	51	Küchen und Kantinen	5010000
16785	35	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
16786	35	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
16787	35	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
16788	35	11	36	61	Gewerbebetriebe	6010000
16789	35	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
16790	35	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
16791	35	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
16792	35	11	40	80	Futtermittelbetriebe	8000000
16793	35	11	41	99	keine Angabe	9999999
16794	36	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
16795	36	11	2	10	Erzeuger (Urproduktion)	1000000
16796	36	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
16797	36	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
16798	36	11	5	13	Imkerei	1030000
16799	36	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
16800	36	11	7	20	Hersteller und Abpacker	2000000
16801	36	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
16802	36	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
16803	36	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
16804	36	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
16805	36	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
16806	36	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
16807	36	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
16808	36	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
16809	36	11	16	29	Hersteller von Bedarfsgegenständen	2090000
16810	36	11	17	30	Vertriebsunternehmer und Transporteure	3000000
16811	36	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
16812	36	11	19	32	Lebensmittellager	3020000
16813	36	11	20	33	Umpackbetriebe	3030000
16814	36	11	21	34	Transporteure von Lebensmitteln	3040000
16815	36	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
16816	36	11	23	36	Großhändler von kosmetischen Mitteln	3060000
16817	36	11	24	37	Großhändler von Bedarfsgegenständen	3070000
16818	36	11	25	40	Einzelhändler	4000000
16819	36	11	26	41	Lebensmitteleinzelhandel	4010000
16820	36	11	27	42	Anderer Einzelhandel	4020000
16821	36	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
16822	36	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
16823	36	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
16824	36	11	31	50	Dienstleistungsbetriebe	5000000
16825	36	11	32	51	Küchen und Kantinen	5010000
16826	36	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
16827	36	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
16828	36	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
16829	36	11	36	61	Gewerbebetriebe	6010000
16830	36	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
16831	36	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
16832	36	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
16833	36	11	40	80	Futtermittelbetriebe	8000000
16834	36	11	41	99	keine Angabe	9999999
16835	37	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
16836	37	11	2	10	Erzeuger (Urproduktion)	1000000
16837	37	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
16838	37	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
16839	37	11	5	13	Imkerei	1030000
16840	37	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
16841	37	11	7	20	Hersteller und Abpacker	2000000
16842	37	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
16843	37	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
16844	37	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
16845	37	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
16846	37	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
16847	37	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
16848	37	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
16849	37	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
16850	37	11	16	29	Hersteller von Bedarfsgegenständen	2090000
16851	37	11	17	30	Vertriebsunternehmer und Transporteure	3000000
16852	37	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
16853	37	11	19	32	Lebensmittellager	3020000
16854	37	11	20	33	Umpackbetriebe	3030000
16855	37	11	21	34	Transporteure von Lebensmitteln	3040000
16856	37	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
16857	37	11	23	36	Großhändler von kosmetischen Mitteln	3060000
16858	37	11	24	37	Großhändler von Bedarfsgegenständen	3070000
16859	37	11	25	40	Einzelhändler	4000000
16860	37	11	26	41	Lebensmitteleinzelhandel	4010000
16861	37	11	27	42	Anderer Einzelhandel	4020000
16862	37	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
16863	37	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
16864	37	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
16865	37	11	31	50	Dienstleistungsbetriebe	5000000
16866	37	11	32	51	Küchen und Kantinen	5010000
16867	37	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
16868	37	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
16869	37	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
16870	37	11	36	61	Gewerbebetriebe	6010000
16871	37	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
16872	37	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
16873	37	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
16874	37	11	40	80	Futtermittelbetriebe	8000000
16875	37	11	41	99	keine Angabe	9999999
16876	38	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
16877	38	11	2	10	Erzeuger (Urproduktion)	1000000
16878	38	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
16879	38	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
16880	38	11	5	13	Imkerei	1030000
16881	38	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
16882	38	11	7	20	Hersteller und Abpacker	2000000
16883	38	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
16884	38	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
16885	38	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
16886	38	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
16887	38	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
16888	38	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
16889	38	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
16890	38	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
16891	38	11	16	29	Hersteller von Bedarfsgegenständen	2090000
16892	38	11	17	30	Vertriebsunternehmer und Transporteure	3000000
16893	38	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
16894	38	11	19	32	Lebensmittellager	3020000
16895	38	11	20	33	Umpackbetriebe	3030000
16896	38	11	21	34	Transporteure von Lebensmitteln	3040000
16897	38	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
16898	38	11	23	36	Großhändler von kosmetischen Mitteln	3060000
16899	38	11	24	37	Großhändler von Bedarfsgegenständen	3070000
16900	38	11	25	40	Einzelhändler	4000000
16901	38	11	26	41	Lebensmitteleinzelhandel	4010000
16902	38	11	27	42	Anderer Einzelhandel	4020000
16903	38	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
16904	38	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
16905	38	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
16906	38	11	31	50	Dienstleistungsbetriebe	5000000
16907	38	11	32	51	Küchen und Kantinen	5010000
16908	38	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
16909	38	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
16910	38	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
16911	38	11	36	61	Gewerbebetriebe	6010000
16912	38	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
16913	38	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
16914	38	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
16915	38	11	40	80	Futtermittelbetriebe	8000000
16916	38	11	41	99	keine Angabe	9999999
16917	39	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
16918	39	11	2	10	Erzeuger (Urproduktion)	1000000
16919	39	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
16920	39	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
16921	39	11	5	13	Imkerei	1030000
16922	39	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
16923	39	11	7	20	Hersteller und Abpacker	2000000
16924	39	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
16925	39	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
16926	39	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
16927	39	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
16928	39	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
16929	39	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
16930	39	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
16931	39	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
16932	39	11	16	29	Hersteller von Bedarfsgegenständen	2090000
16933	39	11	17	30	Vertriebsunternehmer und Transporteure	3000000
16934	39	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
16935	39	11	19	32	Lebensmittellager	3020000
16936	39	11	20	33	Umpackbetriebe	3030000
16937	39	11	21	34	Transporteure von Lebensmitteln	3040000
16938	39	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
16939	39	11	23	36	Großhändler von kosmetischen Mitteln	3060000
16940	39	11	24	37	Großhändler von Bedarfsgegenständen	3070000
16941	39	11	25	40	Einzelhändler	4000000
16942	39	11	26	41	Lebensmitteleinzelhandel	4010000
16943	39	11	27	42	Anderer Einzelhandel	4020000
16944	39	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
16945	39	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
16946	39	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
16947	39	11	31	50	Dienstleistungsbetriebe	5000000
16948	39	11	32	51	Küchen und Kantinen	5010000
16949	39	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
16950	39	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
16951	39	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
16952	39	11	36	61	Gewerbebetriebe	6010000
16953	39	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
16954	39	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
16955	39	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
16956	39	11	40	80	Futtermittelbetriebe	8000000
16957	39	11	41	99	keine Angabe	9999999
16958	40	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
16959	40	11	2	10	Erzeuger (Urproduktion)	1000000
16960	40	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
16961	40	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
16962	40	11	5	13	Imkerei	1030000
16963	40	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
16964	40	11	7	20	Hersteller und Abpacker	2000000
16965	40	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
16966	40	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
16967	40	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
16968	40	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
16969	40	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
16970	40	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
16971	40	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
16972	40	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
16973	40	11	16	29	Hersteller von Bedarfsgegenständen	2090000
16974	40	11	17	30	Vertriebsunternehmer und Transporteure	3000000
16975	40	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
16976	40	11	19	32	Lebensmittellager	3020000
16977	40	11	20	33	Umpackbetriebe	3030000
16978	40	11	21	34	Transporteure von Lebensmitteln	3040000
16979	40	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
16980	40	11	23	36	Großhändler von kosmetischen Mitteln	3060000
16981	40	11	24	37	Großhändler von Bedarfsgegenständen	3070000
16982	40	11	25	40	Einzelhändler	4000000
16983	40	11	26	41	Lebensmitteleinzelhandel	4010000
16984	40	11	27	42	Anderer Einzelhandel	4020000
16985	40	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
16986	40	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
16987	40	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
16988	40	11	31	50	Dienstleistungsbetriebe	5000000
16989	40	11	32	51	Küchen und Kantinen	5010000
16990	40	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
16991	40	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
16992	40	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
16993	40	11	36	61	Gewerbebetriebe	6010000
16994	40	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
16995	40	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
16996	40	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
16997	40	11	40	80	Futtermittelbetriebe	8000000
16998	40	11	41	99	keine Angabe	9999999
16999	41	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
17000	41	11	2	10	Erzeuger (Urproduktion)	1000000
17001	41	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
17002	41	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
17003	41	11	5	13	Imkerei	1030000
17004	41	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
17005	41	11	7	20	Hersteller und Abpacker	2000000
17006	41	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
17007	41	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
17008	41	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
17009	41	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
17010	41	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
17011	41	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
17012	41	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
17013	41	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
17014	41	11	16	29	Hersteller von Bedarfsgegenständen	2090000
17015	41	11	17	30	Vertriebsunternehmer und Transporteure	3000000
17016	41	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
17017	41	11	19	32	Lebensmittellager	3020000
17018	41	11	20	33	Umpackbetriebe	3030000
17019	41	11	21	34	Transporteure von Lebensmitteln	3040000
17020	41	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
17021	41	11	23	36	Großhändler von kosmetischen Mitteln	3060000
17022	41	11	24	37	Großhändler von Bedarfsgegenständen	3070000
17023	41	11	25	40	Einzelhändler	4000000
17024	41	11	26	41	Lebensmitteleinzelhandel	4010000
17025	41	11	27	42	Anderer Einzelhandel	4020000
17026	41	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
17027	41	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
17028	41	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
17029	41	11	31	50	Dienstleistungsbetriebe	5000000
17030	41	11	32	51	Küchen und Kantinen	5010000
17031	41	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
17032	41	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
17033	41	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
17034	41	11	36	61	Gewerbebetriebe	6010000
17035	41	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
17036	41	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
17037	41	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
17038	41	11	40	80	Futtermittelbetriebe	8000000
17039	41	11	41	99	keine Angabe	9999999
17040	42	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
17041	42	11	2	10	Erzeuger (Urproduktion)	1000000
17042	42	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
17043	42	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
17044	42	11	5	13	Imkerei	1030000
17045	42	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
17046	42	11	7	20	Hersteller und Abpacker	2000000
17047	42	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
17048	42	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
17049	42	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
17050	42	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
17051	42	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
17052	42	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
17053	42	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
17054	42	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
17055	42	11	16	29	Hersteller von Bedarfsgegenständen	2090000
17056	42	11	17	30	Vertriebsunternehmer und Transporteure	3000000
17057	42	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
17058	42	11	19	32	Lebensmittellager	3020000
17059	42	11	20	33	Umpackbetriebe	3030000
17060	42	11	21	34	Transporteure von Lebensmitteln	3040000
17061	42	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
17062	42	11	23	36	Großhändler von kosmetischen Mitteln	3060000
17063	42	11	24	37	Großhändler von Bedarfsgegenständen	3070000
17064	42	11	25	40	Einzelhändler	4000000
17065	42	11	26	41	Lebensmitteleinzelhandel	4010000
17066	42	11	27	42	Anderer Einzelhandel	4020000
17067	42	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
17068	42	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
17069	42	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
17070	42	11	31	50	Dienstleistungsbetriebe	5000000
17071	42	11	32	51	Küchen und Kantinen	5010000
17072	42	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
17073	42	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
17074	42	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
17075	42	11	36	61	Gewerbebetriebe	6010000
17076	42	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
17077	42	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
17078	42	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
17079	42	11	40	80	Futtermittelbetriebe	8000000
17080	42	11	41	99	keine Angabe	9999999
17081	43	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
17082	43	11	2	10	Erzeuger (Urproduktion)	1000000
17083	43	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
17084	43	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
17085	43	11	5	13	Imkerei	1030000
17086	43	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
17087	43	11	7	20	Hersteller und Abpacker	2000000
17088	43	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
17089	43	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
17090	43	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
17091	43	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
17092	43	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
17093	43	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
17094	43	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
17095	43	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
17096	43	11	16	29	Hersteller von Bedarfsgegenständen	2090000
17097	43	11	17	30	Vertriebsunternehmer und Transporteure	3000000
17098	43	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
17099	43	11	19	32	Lebensmittellager	3020000
17100	43	11	20	33	Umpackbetriebe	3030000
17101	43	11	21	34	Transporteure von Lebensmitteln	3040000
17102	43	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
17103	43	11	23	36	Großhändler von kosmetischen Mitteln	3060000
17104	43	11	24	37	Großhändler von Bedarfsgegenständen	3070000
17105	43	11	25	40	Einzelhändler	4000000
17106	43	11	26	41	Lebensmitteleinzelhandel	4010000
17107	43	11	27	42	Anderer Einzelhandel	4020000
17108	43	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
17109	43	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
17110	43	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
17111	43	11	31	50	Dienstleistungsbetriebe	5000000
17112	43	11	32	51	Küchen und Kantinen	5010000
17113	43	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
17114	43	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
17115	43	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
17116	43	11	36	61	Gewerbebetriebe	6010000
17117	43	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
17118	43	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
17119	43	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
17120	43	11	40	80	Futtermittelbetriebe	8000000
17121	43	11	41	99	keine Angabe	9999999
17122	44	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
17123	44	11	2	10	Erzeuger (Urproduktion)	1000000
17124	44	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
17125	44	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
17126	44	11	5	13	Imkerei	1030000
17127	44	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
17128	44	11	7	20	Hersteller und Abpacker	2000000
17129	44	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
17130	44	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
17131	44	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
17132	44	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
17133	44	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
17134	44	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
17135	44	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
17136	44	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
17137	44	11	16	29	Hersteller von Bedarfsgegenständen	2090000
17138	44	11	17	30	Vertriebsunternehmer und Transporteure	3000000
17139	44	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
17140	44	11	19	32	Lebensmittellager	3020000
17141	44	11	20	33	Umpackbetriebe	3030000
17142	44	11	21	34	Transporteure von Lebensmitteln	3040000
17143	44	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
17144	44	11	23	36	Großhändler von kosmetischen Mitteln	3060000
17145	44	11	24	37	Großhändler von Bedarfsgegenständen	3070000
17146	44	11	25	40	Einzelhändler	4000000
17147	44	11	26	41	Lebensmitteleinzelhandel	4010000
17148	44	11	27	42	Anderer Einzelhandel	4020000
17149	44	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
17150	44	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
17151	44	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
17152	44	11	31	50	Dienstleistungsbetriebe	5000000
17153	44	11	32	51	Küchen und Kantinen	5010000
17154	44	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
17155	44	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
17156	44	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
17157	44	11	36	61	Gewerbebetriebe	6010000
17158	44	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
17159	44	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
17160	44	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
17161	44	11	40	80	Futtermittelbetriebe	8000000
17162	44	11	41	99	keine Angabe	9999999
17163	45	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
17164	45	11	2	10	Erzeuger (Urproduktion)	1000000
17165	45	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
17166	45	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
17167	45	11	5	13	Imkerei	1030000
17168	45	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
17169	45	11	7	20	Hersteller und Abpacker	2000000
17170	45	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
17171	45	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
17172	45	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
17173	45	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
17174	45	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
17175	45	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
17176	45	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
17177	45	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
17178	45	11	16	29	Hersteller von Bedarfsgegenständen	2090000
17179	45	11	17	30	Vertriebsunternehmer und Transporteure	3000000
17180	45	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
17181	45	11	19	32	Lebensmittellager	3020000
17182	45	11	20	33	Umpackbetriebe	3030000
17183	45	11	21	34	Transporteure von Lebensmitteln	3040000
17184	45	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
17185	45	11	23	36	Großhändler von kosmetischen Mitteln	3060000
17186	45	11	24	37	Großhändler von Bedarfsgegenständen	3070000
17187	45	11	25	40	Einzelhändler	4000000
17188	45	11	26	41	Lebensmitteleinzelhandel	4010000
17189	45	11	27	42	Anderer Einzelhandel	4020000
17190	45	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
17191	45	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
17192	45	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
17193	45	11	31	50	Dienstleistungsbetriebe	5000000
17194	45	11	32	51	Küchen und Kantinen	5010000
17195	45	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
17196	45	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
17197	45	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
17198	45	11	36	61	Gewerbebetriebe	6010000
17199	45	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
17200	45	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
17201	45	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
17202	45	11	40	80	Futtermittelbetriebe	8000000
17203	45	11	41	99	keine Angabe	9999999
17204	46	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
17205	46	11	2	10	Erzeuger (Urproduktion)	1000000
17206	46	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
17207	46	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
17208	46	11	5	13	Imkerei	1030000
17209	46	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
17210	46	11	7	20	Hersteller und Abpacker	2000000
17211	46	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
17212	46	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
17213	46	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
17214	46	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
17215	46	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
17216	46	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
17217	46	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
17218	46	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
17219	46	11	16	29	Hersteller von Bedarfsgegenständen	2090000
17220	46	11	17	30	Vertriebsunternehmer und Transporteure	3000000
17221	46	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
17222	46	11	19	32	Lebensmittellager	3020000
17223	46	11	20	33	Umpackbetriebe	3030000
17224	46	11	21	34	Transporteure von Lebensmitteln	3040000
17225	46	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
17226	46	11	23	36	Großhändler von kosmetischen Mitteln	3060000
17227	46	11	24	37	Großhändler von Bedarfsgegenständen	3070000
17228	46	11	25	40	Einzelhändler	4000000
17229	46	11	26	41	Lebensmitteleinzelhandel	4010000
17230	46	11	27	42	Anderer Einzelhandel	4020000
17231	46	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
17232	46	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
17233	46	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
17234	46	11	31	50	Dienstleistungsbetriebe	5000000
17235	46	11	32	51	Küchen und Kantinen	5010000
17236	46	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
17237	46	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
17238	46	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
17239	46	11	36	61	Gewerbebetriebe	6010000
17240	46	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
17241	46	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
17242	46	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
17243	46	11	40	80	Futtermittelbetriebe	8000000
17244	46	11	41	99	keine Angabe	9999999
17245	47	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
17246	47	11	2	10	Erzeuger (Urproduktion)	1000000
17247	47	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
17248	47	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
17249	47	11	5	13	Imkerei	1030000
17250	47	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
17251	47	11	7	20	Hersteller und Abpacker	2000000
17252	47	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
17253	47	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
17254	47	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
17255	47	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
17256	47	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
17257	47	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
17258	47	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
17259	47	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
17260	47	11	16	29	Hersteller von Bedarfsgegenständen	2090000
17261	47	11	17	30	Vertriebsunternehmer und Transporteure	3000000
17262	47	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
17263	47	11	19	32	Lebensmittellager	3020000
17264	47	11	20	33	Umpackbetriebe	3030000
17265	47	11	21	34	Transporteure von Lebensmitteln	3040000
17266	47	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
17267	47	11	23	36	Großhändler von kosmetischen Mitteln	3060000
17268	47	11	24	37	Großhändler von Bedarfsgegenständen	3070000
17269	47	11	25	40	Einzelhändler	4000000
17270	47	11	26	41	Lebensmitteleinzelhandel	4010000
17271	47	11	27	42	Anderer Einzelhandel	4020000
17272	47	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
17273	47	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
17274	47	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
17275	47	11	31	50	Dienstleistungsbetriebe	5000000
17276	47	11	32	51	Küchen und Kantinen	5010000
17277	47	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
17278	47	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
17279	47	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
17280	47	11	36	61	Gewerbebetriebe	6010000
17281	47	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
17282	47	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
17283	47	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
17284	47	11	40	80	Futtermittelbetriebe	8000000
17285	47	11	41	99	keine Angabe	9999999
17286	48	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
17287	48	11	2	10	Erzeuger (Urproduktion)	1000000
17288	48	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
17289	48	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
17290	48	11	5	13	Imkerei	1030000
17291	48	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
17292	48	11	7	20	Hersteller und Abpacker	2000000
17293	48	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
17294	48	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
17295	48	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
17296	48	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
17297	48	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
17298	48	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
17299	48	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
17300	48	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
17301	48	11	16	29	Hersteller von Bedarfsgegenständen	2090000
17302	48	11	17	30	Vertriebsunternehmer und Transporteure	3000000
17303	48	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
17304	48	11	19	32	Lebensmittellager	3020000
17305	48	11	20	33	Umpackbetriebe	3030000
17306	48	11	21	34	Transporteure von Lebensmitteln	3040000
17307	48	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
17308	48	11	23	36	Großhändler von kosmetischen Mitteln	3060000
17309	48	11	24	37	Großhändler von Bedarfsgegenständen	3070000
17310	48	11	25	40	Einzelhändler	4000000
17311	48	11	26	41	Lebensmitteleinzelhandel	4010000
17312	48	11	27	42	Anderer Einzelhandel	4020000
17313	48	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
17314	48	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
17315	48	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
17316	48	11	31	50	Dienstleistungsbetriebe	5000000
17317	48	11	32	51	Küchen und Kantinen	5010000
17318	48	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
17319	48	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
17320	48	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
17321	48	11	36	61	Gewerbebetriebe	6010000
17322	48	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
17323	48	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
17324	48	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
17325	48	11	40	80	Futtermittelbetriebe	8000000
17326	48	11	41	99	keine Angabe	9999999
17327	49	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
17328	49	11	2	10	Erzeuger (Urproduktion)	1000000
17329	49	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
17330	49	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
17331	49	11	5	13	Imkerei	1030000
17332	49	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
17333	49	11	7	20	Hersteller und Abpacker	2000000
17334	49	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
17335	49	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
17336	49	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
17337	49	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
17338	49	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
17339	49	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
17340	49	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
17341	49	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
17342	49	11	16	29	Hersteller von Bedarfsgegenständen	2090000
17343	49	11	17	30	Vertriebsunternehmer und Transporteure	3000000
17344	49	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
17345	49	11	19	32	Lebensmittellager	3020000
17346	49	11	20	33	Umpackbetriebe	3030000
17347	49	11	21	34	Transporteure von Lebensmitteln	3040000
17348	49	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
17349	49	11	23	36	Großhändler von kosmetischen Mitteln	3060000
17350	49	11	24	37	Großhändler von Bedarfsgegenständen	3070000
17351	49	11	25	40	Einzelhändler	4000000
17352	49	11	26	41	Lebensmitteleinzelhandel	4010000
17353	49	11	27	42	Anderer Einzelhandel	4020000
17354	49	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
17355	49	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
17356	49	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
17357	49	11	31	50	Dienstleistungsbetriebe	5000000
17358	49	11	32	51	Küchen und Kantinen	5010000
17359	49	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
17360	49	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
17361	49	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
17362	49	11	36	61	Gewerbebetriebe	6010000
17363	49	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
17364	49	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
17365	49	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
17366	49	11	40	80	Futtermittelbetriebe	8000000
17367	49	11	41	99	keine Angabe	9999999
17368	50	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
17369	50	11	2	10	Erzeuger (Urproduktion)	1000000
17370	50	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
17371	50	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
17372	50	11	5	13	Imkerei	1030000
17373	50	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
17374	50	11	7	20	Hersteller und Abpacker	2000000
17375	50	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
17376	50	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
17377	50	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
17378	50	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
17379	50	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
17380	50	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
17381	50	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
17382	50	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
17383	50	11	16	29	Hersteller von Bedarfsgegenständen	2090000
17384	50	11	17	30	Vertriebsunternehmer und Transporteure	3000000
17385	50	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
17386	50	11	19	32	Lebensmittellager	3020000
17387	50	11	20	33	Umpackbetriebe	3030000
17388	50	11	21	34	Transporteure von Lebensmitteln	3040000
17389	50	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
17390	50	11	23	36	Großhändler von kosmetischen Mitteln	3060000
17391	50	11	24	37	Großhändler von Bedarfsgegenständen	3070000
17392	50	11	25	40	Einzelhändler	4000000
17393	50	11	26	41	Lebensmitteleinzelhandel	4010000
17394	50	11	27	42	Anderer Einzelhandel	4020000
17395	50	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
17396	50	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
17397	50	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
17398	50	11	31	50	Dienstleistungsbetriebe	5000000
17399	50	11	32	51	Küchen und Kantinen	5010000
17400	50	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
17401	50	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
17402	50	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
17403	50	11	36	61	Gewerbebetriebe	6010000
17404	50	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
17405	50	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
17406	50	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
17407	50	11	40	80	Futtermittelbetriebe	8000000
17408	50	11	41	99	keine Angabe	9999999
17409	51	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
17410	51	11	2	10	Erzeuger (Urproduktion)	1000000
17411	51	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
17412	51	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
17413	51	11	5	13	Imkerei	1030000
17414	51	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
17415	51	11	7	20	Hersteller und Abpacker	2000000
17416	51	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
17417	51	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
17418	51	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
17419	51	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
17420	51	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
17421	51	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
17422	51	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
17423	51	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
17424	51	11	16	29	Hersteller von Bedarfsgegenständen	2090000
17425	51	11	17	30	Vertriebsunternehmer und Transporteure	3000000
17426	51	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
17427	51	11	19	32	Lebensmittellager	3020000
17428	51	11	20	33	Umpackbetriebe	3030000
17429	51	11	21	34	Transporteure von Lebensmitteln	3040000
17430	51	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
17431	51	11	23	36	Großhändler von kosmetischen Mitteln	3060000
17432	51	11	24	37	Großhändler von Bedarfsgegenständen	3070000
17433	51	11	25	40	Einzelhändler	4000000
17434	51	11	26	41	Lebensmitteleinzelhandel	4010000
17435	51	11	27	42	Anderer Einzelhandel	4020000
17436	51	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
17437	51	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
17438	51	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
17439	51	11	31	50	Dienstleistungsbetriebe	5000000
17440	51	11	32	51	Küchen und Kantinen	5010000
17441	51	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
17442	51	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
17443	51	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
17444	51	11	36	61	Gewerbebetriebe	6010000
17445	51	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
17446	51	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
17447	51	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
17448	51	11	40	80	Futtermittelbetriebe	8000000
17449	51	11	41	99	keine Angabe	9999999
17450	52	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
17451	52	11	2	10	Erzeuger (Urproduktion)	1000000
17452	52	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
17453	52	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
17454	52	11	5	13	Imkerei	1030000
17455	52	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
17456	52	11	7	20	Hersteller und Abpacker	2000000
17457	52	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
17458	52	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
17459	52	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
17460	52	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
17461	52	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
17462	52	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
17463	52	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
17464	52	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
17465	52	11	16	29	Hersteller von Bedarfsgegenständen	2090000
17466	52	11	17	30	Vertriebsunternehmer und Transporteure	3000000
17467	52	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
17468	52	11	19	32	Lebensmittellager	3020000
17469	52	11	20	33	Umpackbetriebe	3030000
17470	52	11	21	34	Transporteure von Lebensmitteln	3040000
17471	52	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
17472	52	11	23	36	Großhändler von kosmetischen Mitteln	3060000
17473	52	11	24	37	Großhändler von Bedarfsgegenständen	3070000
17474	52	11	25	40	Einzelhändler	4000000
17475	52	11	26	41	Lebensmitteleinzelhandel	4010000
17476	52	11	27	42	Anderer Einzelhandel	4020000
17477	52	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
17478	52	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
17479	52	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
17480	52	11	31	50	Dienstleistungsbetriebe	5000000
17481	52	11	32	51	Küchen und Kantinen	5010000
17482	52	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
17483	52	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
17484	52	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
17485	52	11	36	61	Gewerbebetriebe	6010000
17486	52	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
17487	52	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
17488	52	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
17489	52	11	40	80	Futtermittelbetriebe	8000000
17490	52	11	41	99	keine Angabe	9999999
17491	53	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
17492	53	11	2	10	Erzeuger (Urproduktion)	1000000
17493	53	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
17494	53	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
17495	53	11	5	13	Imkerei	1030000
17496	53	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
17497	53	11	7	20	Hersteller und Abpacker	2000000
17498	53	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
17499	53	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
17500	53	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
17501	53	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
17502	53	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
17503	53	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
17504	53	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
17505	53	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
17506	53	11	16	29	Hersteller von Bedarfsgegenständen	2090000
17507	53	11	17	30	Vertriebsunternehmer und Transporteure	3000000
17508	53	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
17509	53	11	19	32	Lebensmittellager	3020000
17510	53	11	20	33	Umpackbetriebe	3030000
17511	53	11	21	34	Transporteure von Lebensmitteln	3040000
17512	53	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
17513	53	11	23	36	Großhändler von kosmetischen Mitteln	3060000
17514	53	11	24	37	Großhändler von Bedarfsgegenständen	3070000
17515	53	11	25	40	Einzelhändler	4000000
17516	53	11	26	41	Lebensmitteleinzelhandel	4010000
17517	53	11	27	42	Anderer Einzelhandel	4020000
17518	53	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
17519	53	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
17520	53	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
17521	53	11	31	50	Dienstleistungsbetriebe	5000000
17522	53	11	32	51	Küchen und Kantinen	5010000
17523	53	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
17524	53	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
17525	53	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
17526	53	11	36	61	Gewerbebetriebe	6010000
17527	53	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
17528	53	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
17529	53	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
17530	53	11	40	80	Futtermittelbetriebe	8000000
17531	53	11	41	99	keine Angabe	9999999
17532	54	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
17533	54	11	2	10	Erzeuger (Urproduktion)	1000000
17534	54	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
17535	54	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
17536	54	11	5	13	Imkerei	1030000
17537	54	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
17538	54	11	7	20	Hersteller und Abpacker	2000000
17539	54	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
17540	54	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
17541	54	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
17542	54	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
17543	54	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
17544	54	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
17545	54	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
17546	54	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
17547	54	11	16	29	Hersteller von Bedarfsgegenständen	2090000
17548	54	11	17	30	Vertriebsunternehmer und Transporteure	3000000
17549	54	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
17550	54	11	19	32	Lebensmittellager	3020000
17551	54	11	20	33	Umpackbetriebe	3030000
17552	54	11	21	34	Transporteure von Lebensmitteln	3040000
17553	54	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
17554	54	11	23	36	Großhändler von kosmetischen Mitteln	3060000
17555	54	11	24	37	Großhändler von Bedarfsgegenständen	3070000
17556	54	11	25	40	Einzelhändler	4000000
17557	54	11	26	41	Lebensmitteleinzelhandel	4010000
17558	54	11	27	42	Anderer Einzelhandel	4020000
17559	54	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
17560	54	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
17561	54	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
17562	54	11	31	50	Dienstleistungsbetriebe	5000000
17563	54	11	32	51	Küchen und Kantinen	5010000
17564	54	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
17565	54	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
17566	54	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
17567	54	11	36	61	Gewerbebetriebe	6010000
17568	54	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
17569	54	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
17570	54	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
17571	54	11	40	80	Futtermittelbetriebe	8000000
17572	54	11	41	99	keine Angabe	9999999
17573	55	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
17574	55	11	2	10	Erzeuger (Urproduktion)	1000000
17575	55	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
17576	55	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
17577	55	11	5	13	Imkerei	1030000
17578	55	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
17579	55	11	7	20	Hersteller und Abpacker	2000000
17580	55	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
17581	55	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
17582	55	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
17583	55	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
17584	55	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
17585	55	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
17586	55	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
17587	55	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
17588	55	11	16	29	Hersteller von Bedarfsgegenständen	2090000
17589	55	11	17	30	Vertriebsunternehmer und Transporteure	3000000
17590	55	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
17591	55	11	19	32	Lebensmittellager	3020000
17592	55	11	20	33	Umpackbetriebe	3030000
17593	55	11	21	34	Transporteure von Lebensmitteln	3040000
17594	55	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
17595	55	11	23	36	Großhändler von kosmetischen Mitteln	3060000
17596	55	11	24	37	Großhändler von Bedarfsgegenständen	3070000
17597	55	11	25	40	Einzelhändler	4000000
17598	55	11	26	41	Lebensmitteleinzelhandel	4010000
17599	55	11	27	42	Anderer Einzelhandel	4020000
17600	55	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
17601	55	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
17602	55	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
17603	55	11	31	50	Dienstleistungsbetriebe	5000000
17604	55	11	32	51	Küchen und Kantinen	5010000
17605	55	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
17606	55	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
17607	55	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
17608	55	11	36	61	Gewerbebetriebe	6010000
17609	55	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
17610	55	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
17611	55	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
17612	55	11	40	80	Futtermittelbetriebe	8000000
17613	55	11	41	99	keine Angabe	9999999
17614	56	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
17615	56	11	2	10	Erzeuger (Urproduktion)	1000000
17616	56	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
17617	56	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
17618	56	11	5	13	Imkerei	1030000
17619	56	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
17620	56	11	7	20	Hersteller und Abpacker	2000000
17621	56	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
17622	56	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
17623	56	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
17624	56	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
17625	56	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
17626	56	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
17627	56	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
17628	56	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
17629	56	11	16	29	Hersteller von Bedarfsgegenständen	2090000
17630	56	11	17	30	Vertriebsunternehmer und Transporteure	3000000
17631	56	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
17632	56	11	19	32	Lebensmittellager	3020000
17633	56	11	20	33	Umpackbetriebe	3030000
17634	56	11	21	34	Transporteure von Lebensmitteln	3040000
17635	56	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
17636	56	11	23	36	Großhändler von kosmetischen Mitteln	3060000
17637	56	11	24	37	Großhändler von Bedarfsgegenständen	3070000
17638	56	11	25	40	Einzelhändler	4000000
17639	56	11	26	41	Lebensmitteleinzelhandel	4010000
17640	56	11	27	42	Anderer Einzelhandel	4020000
17641	56	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
17642	56	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
17643	56	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
17644	56	11	31	50	Dienstleistungsbetriebe	5000000
17645	56	11	32	51	Küchen und Kantinen	5010000
17646	56	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
17647	56	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
17648	56	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
17649	56	11	36	61	Gewerbebetriebe	6010000
17650	56	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
17651	56	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
17652	56	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
17653	56	11	40	80	Futtermittelbetriebe	8000000
17654	56	11	41	99	keine Angabe	9999999
17655	57	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
17656	57	11	2	10	Erzeuger (Urproduktion)	1000000
17657	57	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
17658	57	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
17659	57	11	5	13	Imkerei	1030000
17660	57	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
17661	57	11	7	20	Hersteller und Abpacker	2000000
17662	57	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
17663	57	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
17664	57	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
17665	57	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
17666	57	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
17667	57	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
17668	57	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
17669	57	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
17670	57	11	16	29	Hersteller von Bedarfsgegenständen	2090000
17671	57	11	17	30	Vertriebsunternehmer und Transporteure	3000000
17672	57	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
17673	57	11	19	32	Lebensmittellager	3020000
17674	57	11	20	33	Umpackbetriebe	3030000
17675	57	11	21	34	Transporteure von Lebensmitteln	3040000
17676	57	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
17677	57	11	23	36	Großhändler von kosmetischen Mitteln	3060000
17678	57	11	24	37	Großhändler von Bedarfsgegenständen	3070000
17679	57	11	25	40	Einzelhändler	4000000
17680	57	11	26	41	Lebensmitteleinzelhandel	4010000
17681	57	11	27	42	Anderer Einzelhandel	4020000
17682	57	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
17683	57	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
17684	57	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
17685	57	11	31	50	Dienstleistungsbetriebe	5000000
17686	57	11	32	51	Küchen und Kantinen	5010000
17687	57	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
17688	57	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
17689	57	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
17690	57	11	36	61	Gewerbebetriebe	6010000
17691	57	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
17692	57	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
17693	57	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
17694	57	11	40	80	Futtermittelbetriebe	8000000
17695	57	11	41	99	keine Angabe	9999999
17696	58	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
17697	58	11	2	10	Erzeuger (Urproduktion)	1000000
17698	58	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
17699	58	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
17700	58	11	5	13	Imkerei	1030000
17701	58	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
17702	58	11	7	20	Hersteller und Abpacker	2000000
17703	58	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
17704	58	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
17705	58	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
17706	58	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
17707	58	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
17708	58	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
17709	58	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
17710	58	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
17711	58	11	16	29	Hersteller von Bedarfsgegenständen	2090000
17712	58	11	17	30	Vertriebsunternehmer und Transporteure	3000000
17713	58	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
17714	58	11	19	32	Lebensmittellager	3020000
17715	58	11	20	33	Umpackbetriebe	3030000
17716	58	11	21	34	Transporteure von Lebensmitteln	3040000
17717	58	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
17718	58	11	23	36	Großhändler von kosmetischen Mitteln	3060000
17719	58	11	24	37	Großhändler von Bedarfsgegenständen	3070000
17720	58	11	25	40	Einzelhändler	4000000
17721	58	11	26	41	Lebensmitteleinzelhandel	4010000
17722	58	11	27	42	Anderer Einzelhandel	4020000
17723	58	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
17724	58	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
17725	58	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
17726	58	11	31	50	Dienstleistungsbetriebe	5000000
17727	58	11	32	51	Küchen und Kantinen	5010000
17728	58	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
17729	58	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
17730	58	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
17731	58	11	36	61	Gewerbebetriebe	6010000
17732	58	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
17733	58	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
17734	58	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
17735	58	11	40	80	Futtermittelbetriebe	8000000
17736	58	11	41	99	keine Angabe	9999999
17737	59	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
17738	59	11	2	10	Erzeuger (Urproduktion)	1000000
17739	59	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
17740	59	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
17741	59	11	5	13	Imkerei	1030000
17742	59	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
17743	59	11	7	20	Hersteller und Abpacker	2000000
17744	59	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
17745	59	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
17746	59	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
17747	59	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
17748	59	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
17749	59	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
17750	59	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
17751	59	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
17752	59	11	16	29	Hersteller von Bedarfsgegenständen	2090000
17753	59	11	17	30	Vertriebsunternehmer und Transporteure	3000000
17754	59	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
17755	59	11	19	32	Lebensmittellager	3020000
17756	59	11	20	33	Umpackbetriebe	3030000
17757	59	11	21	34	Transporteure von Lebensmitteln	3040000
17758	59	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
17759	59	11	23	36	Großhändler von kosmetischen Mitteln	3060000
17760	59	11	24	37	Großhändler von Bedarfsgegenständen	3070000
17761	59	11	25	40	Einzelhändler	4000000
17762	59	11	26	41	Lebensmitteleinzelhandel	4010000
17763	59	11	27	42	Anderer Einzelhandel	4020000
17764	59	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
17765	59	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
17766	59	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
17767	59	11	31	50	Dienstleistungsbetriebe	5000000
17768	59	11	32	51	Küchen und Kantinen	5010000
17769	59	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
17770	59	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
17771	59	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
17772	59	11	36	61	Gewerbebetriebe	6010000
17773	59	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
17774	59	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
17775	59	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
17776	59	11	40	80	Futtermittelbetriebe	8000000
17777	59	11	41	99	keine Angabe	9999999
17778	60	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
17779	60	11	2	10	Erzeuger (Urproduktion)	1000000
17780	60	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
17781	60	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
17782	60	11	5	13	Imkerei	1030000
17783	60	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
17784	60	11	7	20	Hersteller und Abpacker	2000000
17785	60	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
17786	60	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
17787	60	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
17788	60	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
17789	60	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
17790	60	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
17791	60	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
17792	60	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
17793	60	11	16	29	Hersteller von Bedarfsgegenständen	2090000
17794	60	11	17	30	Vertriebsunternehmer und Transporteure	3000000
17795	60	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
17796	60	11	19	32	Lebensmittellager	3020000
17797	60	11	20	33	Umpackbetriebe	3030000
17798	60	11	21	34	Transporteure von Lebensmitteln	3040000
17799	60	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
17800	60	11	23	36	Großhändler von kosmetischen Mitteln	3060000
17801	60	11	24	37	Großhändler von Bedarfsgegenständen	3070000
17802	60	11	25	40	Einzelhändler	4000000
17803	60	11	26	41	Lebensmitteleinzelhandel	4010000
17804	60	11	27	42	Anderer Einzelhandel	4020000
17805	60	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
17806	60	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
17807	60	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
17808	60	11	31	50	Dienstleistungsbetriebe	5000000
17809	60	11	32	51	Küchen und Kantinen	5010000
17810	60	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
17811	60	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
17812	60	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
17813	60	11	36	61	Gewerbebetriebe	6010000
17814	60	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
17815	60	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
17816	60	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
17817	60	11	40	80	Futtermittelbetriebe	8000000
17818	60	11	41	99	keine Angabe	9999999
17819	61	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
17820	61	11	2	10	Erzeuger (Urproduktion)	1000000
17821	61	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
17822	61	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
17823	61	11	5	13	Imkerei	1030000
17824	61	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
17825	61	11	7	20	Hersteller und Abpacker	2000000
17826	61	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
17827	61	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
17828	61	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
17829	61	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
17830	61	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
17831	61	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
17832	61	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
17833	61	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
17834	61	11	16	29	Hersteller von Bedarfsgegenständen	2090000
17835	61	11	17	30	Vertriebsunternehmer und Transporteure	3000000
17836	61	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
17837	61	11	19	32	Lebensmittellager	3020000
17838	61	11	20	33	Umpackbetriebe	3030000
17839	61	11	21	34	Transporteure von Lebensmitteln	3040000
17840	61	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
17841	61	11	23	36	Großhändler von kosmetischen Mitteln	3060000
17842	61	11	24	37	Großhändler von Bedarfsgegenständen	3070000
17843	61	11	25	40	Einzelhändler	4000000
17844	61	11	26	41	Lebensmitteleinzelhandel	4010000
17845	61	11	27	42	Anderer Einzelhandel	4020000
17846	61	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
17847	61	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
17848	61	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
17849	61	11	31	50	Dienstleistungsbetriebe	5000000
17850	61	11	32	51	Küchen und Kantinen	5010000
17851	61	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
17852	61	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
17853	61	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
17854	61	11	36	61	Gewerbebetriebe	6010000
17855	61	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
17856	61	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
17857	61	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
17858	61	11	40	80	Futtermittelbetriebe	8000000
17859	61	11	41	99	keine Angabe	9999999
17860	62	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
17861	62	11	2	10	Erzeuger (Urproduktion)	1000000
17862	62	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
17863	62	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
17864	62	11	5	13	Imkerei	1030000
17865	62	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
17866	62	11	7	20	Hersteller und Abpacker	2000000
17867	62	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
17868	62	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
17869	62	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
17870	62	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
17871	62	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
17872	62	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
17873	62	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
17874	62	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
17875	62	11	16	29	Hersteller von Bedarfsgegenständen	2090000
17876	62	11	17	30	Vertriebsunternehmer und Transporteure	3000000
17877	62	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
17878	62	11	19	32	Lebensmittellager	3020000
17879	62	11	20	33	Umpackbetriebe	3030000
17880	62	11	21	34	Transporteure von Lebensmitteln	3040000
17881	62	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
17882	62	11	23	36	Großhändler von kosmetischen Mitteln	3060000
17883	62	11	24	37	Großhändler von Bedarfsgegenständen	3070000
17884	62	11	25	40	Einzelhändler	4000000
17885	62	11	26	41	Lebensmitteleinzelhandel	4010000
17886	62	11	27	42	Anderer Einzelhandel	4020000
17887	62	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
17888	62	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
17889	62	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
17890	62	11	31	50	Dienstleistungsbetriebe	5000000
17891	62	11	32	51	Küchen und Kantinen	5010000
17892	62	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
17893	62	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
17894	62	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
17895	62	11	36	61	Gewerbebetriebe	6010000
17896	62	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
17897	62	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
17898	62	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
17899	62	11	40	80	Futtermittelbetriebe	8000000
17900	62	11	41	99	keine Angabe	9999999
17901	63	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
17902	63	11	2	10	Erzeuger (Urproduktion)	1000000
17903	63	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
17904	63	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
17905	63	11	5	13	Imkerei	1030000
17906	63	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
17907	63	11	7	20	Hersteller und Abpacker	2000000
17908	63	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
17909	63	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
17910	63	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
17911	63	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
17912	63	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
17913	63	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
17914	63	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
17915	63	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
17916	63	11	16	29	Hersteller von Bedarfsgegenständen	2090000
17917	63	11	17	30	Vertriebsunternehmer und Transporteure	3000000
17918	63	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
17919	63	11	19	32	Lebensmittellager	3020000
17920	63	11	20	33	Umpackbetriebe	3030000
17921	63	11	21	34	Transporteure von Lebensmitteln	3040000
17922	63	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
17923	63	11	23	36	Großhändler von kosmetischen Mitteln	3060000
17924	63	11	24	37	Großhändler von Bedarfsgegenständen	3070000
17925	63	11	25	40	Einzelhändler	4000000
17926	63	11	26	41	Lebensmitteleinzelhandel	4010000
17927	63	11	27	42	Anderer Einzelhandel	4020000
17928	63	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
17929	63	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
17930	63	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
17931	63	11	31	50	Dienstleistungsbetriebe	5000000
17932	63	11	32	51	Küchen und Kantinen	5010000
17933	63	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
17934	63	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
17935	63	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
17936	63	11	36	61	Gewerbebetriebe	6010000
17937	63	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
17938	63	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
17939	63	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
17940	63	11	40	80	Futtermittelbetriebe	8000000
17941	63	11	41	99	keine Angabe	9999999
17942	64	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
17943	64	11	2	10	Erzeuger (Urproduktion)	1000000
17944	64	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
17945	64	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
17946	64	11	5	13	Imkerei	1030000
17947	64	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
17948	64	11	7	20	Hersteller und Abpacker	2000000
17949	64	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
17950	64	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
17951	64	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
17952	64	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
17953	64	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
17954	64	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
17955	64	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
17956	64	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
17957	64	11	16	29	Hersteller von Bedarfsgegenständen	2090000
17958	64	11	17	30	Vertriebsunternehmer und Transporteure	3000000
17959	64	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
17960	64	11	19	32	Lebensmittellager	3020000
17961	64	11	20	33	Umpackbetriebe	3030000
17962	64	11	21	34	Transporteure von Lebensmitteln	3040000
17963	64	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
17964	64	11	23	36	Großhändler von kosmetischen Mitteln	3060000
17965	64	11	24	37	Großhändler von Bedarfsgegenständen	3070000
17966	64	11	25	40	Einzelhändler	4000000
17967	64	11	26	41	Lebensmitteleinzelhandel	4010000
17968	64	11	27	42	Anderer Einzelhandel	4020000
17969	64	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
17970	64	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
17971	64	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
17972	64	11	31	50	Dienstleistungsbetriebe	5000000
17973	64	11	32	51	Küchen und Kantinen	5010000
17974	64	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
17975	64	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
17976	64	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
17977	64	11	36	61	Gewerbebetriebe	6010000
17978	64	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
17979	64	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
17980	64	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
17981	64	11	40	80	Futtermittelbetriebe	8000000
17982	64	11	41	99	keine Angabe	9999999
17983	65	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
17984	65	11	2	10	Erzeuger (Urproduktion)	1000000
17985	65	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
17986	65	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
17987	65	11	5	13	Imkerei	1030000
17988	65	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
17989	65	11	7	20	Hersteller und Abpacker	2000000
17990	65	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
17991	65	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
17992	65	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
17993	65	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
17994	65	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
17995	65	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
17996	65	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
17997	65	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
17998	65	11	16	29	Hersteller von Bedarfsgegenständen	2090000
17999	65	11	17	30	Vertriebsunternehmer und Transporteure	3000000
18000	65	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
18001	65	11	19	32	Lebensmittellager	3020000
18002	65	11	20	33	Umpackbetriebe	3030000
18003	65	11	21	34	Transporteure von Lebensmitteln	3040000
18004	65	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
18005	65	11	23	36	Großhändler von kosmetischen Mitteln	3060000
18006	65	11	24	37	Großhändler von Bedarfsgegenständen	3070000
18007	65	11	25	40	Einzelhändler	4000000
18008	65	11	26	41	Lebensmitteleinzelhandel	4010000
18009	65	11	27	42	Anderer Einzelhandel	4020000
18010	65	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
18011	65	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
18012	65	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
18013	65	11	31	50	Dienstleistungsbetriebe	5000000
18014	65	11	32	51	Küchen und Kantinen	5010000
18015	65	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
18016	65	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
18017	65	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
18018	65	11	36	61	Gewerbebetriebe	6010000
18019	65	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
18020	65	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
18021	65	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
18022	65	11	40	80	Futtermittelbetriebe	8000000
18023	65	11	41	99	keine Angabe	9999999
18024	66	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
18025	66	11	2	10	Erzeuger (Urproduktion)	1000000
18026	66	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
18027	66	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
18028	66	11	5	13	Imkerei	1030000
18029	66	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
18030	66	11	7	20	Hersteller und Abpacker	2000000
18031	66	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
18032	66	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
18033	66	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
18034	66	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
18035	66	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
18036	66	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
18037	66	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
18038	66	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
18039	66	11	16	29	Hersteller von Bedarfsgegenständen	2090000
18040	66	11	17	30	Vertriebsunternehmer und Transporteure	3000000
18041	66	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
18042	66	11	19	32	Lebensmittellager	3020000
18043	66	11	20	33	Umpackbetriebe	3030000
18044	66	11	21	34	Transporteure von Lebensmitteln	3040000
18045	66	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
18046	66	11	23	36	Großhändler von kosmetischen Mitteln	3060000
18047	66	11	24	37	Großhändler von Bedarfsgegenständen	3070000
18048	66	11	25	40	Einzelhändler	4000000
18049	66	11	26	41	Lebensmitteleinzelhandel	4010000
18050	66	11	27	42	Anderer Einzelhandel	4020000
18051	66	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
18052	66	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
18053	66	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
18054	66	11	31	50	Dienstleistungsbetriebe	5000000
18055	66	11	32	51	Küchen und Kantinen	5010000
18056	66	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
18057	66	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
18058	66	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
18059	66	11	36	61	Gewerbebetriebe	6010000
18060	66	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
18061	66	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
18062	66	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
18063	66	11	40	80	Futtermittelbetriebe	8000000
18064	66	11	41	99	keine Angabe	9999999
18065	67	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
18066	67	11	2	10	Erzeuger (Urproduktion)	1000000
18067	67	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
18068	67	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
18069	67	11	5	13	Imkerei	1030000
18070	67	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
18071	67	11	7	20	Hersteller und Abpacker	2000000
18072	67	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
18073	67	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
18074	67	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
18075	67	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
18076	67	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
18077	67	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
18078	67	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
18079	67	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
18080	67	11	16	29	Hersteller von Bedarfsgegenständen	2090000
18081	67	11	17	30	Vertriebsunternehmer und Transporteure	3000000
18082	67	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
18083	67	11	19	32	Lebensmittellager	3020000
18084	67	11	20	33	Umpackbetriebe	3030000
18085	67	11	21	34	Transporteure von Lebensmitteln	3040000
18086	67	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
18087	67	11	23	36	Großhändler von kosmetischen Mitteln	3060000
18088	67	11	24	37	Großhändler von Bedarfsgegenständen	3070000
18089	67	11	25	40	Einzelhändler	4000000
18090	67	11	26	41	Lebensmitteleinzelhandel	4010000
18091	67	11	27	42	Anderer Einzelhandel	4020000
18092	67	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
18093	67	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
18094	67	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
18095	67	11	31	50	Dienstleistungsbetriebe	5000000
18096	67	11	32	51	Küchen und Kantinen	5010000
18097	67	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
18098	67	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
18099	67	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
18100	67	11	36	61	Gewerbebetriebe	6010000
18101	67	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
18102	67	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
18103	67	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
18104	67	11	40	80	Futtermittelbetriebe	8000000
18105	67	11	41	99	keine Angabe	9999999
18106	68	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
18107	68	11	2	10	Erzeuger (Urproduktion)	1000000
18108	68	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
18109	68	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
18110	68	11	5	13	Imkerei	1030000
18111	68	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
18112	68	11	7	20	Hersteller und Abpacker	2000000
18113	68	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
18114	68	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
18115	68	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
18116	68	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
18117	68	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
18118	68	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
18119	68	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
18120	68	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
18121	68	11	16	29	Hersteller von Bedarfsgegenständen	2090000
18122	68	11	17	30	Vertriebsunternehmer und Transporteure	3000000
18123	68	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
18124	68	11	19	32	Lebensmittellager	3020000
18125	68	11	20	33	Umpackbetriebe	3030000
18126	68	11	21	34	Transporteure von Lebensmitteln	3040000
18127	68	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
18128	68	11	23	36	Großhändler von kosmetischen Mitteln	3060000
18129	68	11	24	37	Großhändler von Bedarfsgegenständen	3070000
18130	68	11	25	40	Einzelhändler	4000000
18131	68	11	26	41	Lebensmitteleinzelhandel	4010000
18132	68	11	27	42	Anderer Einzelhandel	4020000
18133	68	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
18134	68	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
18135	68	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
18136	68	11	31	50	Dienstleistungsbetriebe	5000000
18137	68	11	32	51	Küchen und Kantinen	5010000
18138	68	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
18139	68	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
18140	68	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
18141	68	11	36	61	Gewerbebetriebe	6010000
18142	68	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
18143	68	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
18144	68	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
18145	68	11	40	80	Futtermittelbetriebe	8000000
18146	68	11	41	99	keine Angabe	9999999
18147	69	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
18148	69	11	2	10	Erzeuger (Urproduktion)	1000000
18149	69	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
18150	69	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
18151	69	11	5	13	Imkerei	1030000
18152	69	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
18153	69	11	7	20	Hersteller und Abpacker	2000000
18154	69	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
18155	69	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
18156	69	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
18157	69	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
18158	69	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
18159	69	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
18160	69	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
18161	69	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
18162	69	11	16	29	Hersteller von Bedarfsgegenständen	2090000
18163	69	11	17	30	Vertriebsunternehmer und Transporteure	3000000
18164	69	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
18165	69	11	19	32	Lebensmittellager	3020000
18166	69	11	20	33	Umpackbetriebe	3030000
18167	69	11	21	34	Transporteure von Lebensmitteln	3040000
18168	69	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
18169	69	11	23	36	Großhändler von kosmetischen Mitteln	3060000
18170	69	11	24	37	Großhändler von Bedarfsgegenständen	3070000
18171	69	11	25	40	Einzelhändler	4000000
18172	69	11	26	41	Lebensmitteleinzelhandel	4010000
18173	69	11	27	42	Anderer Einzelhandel	4020000
18174	69	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
18175	69	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
18176	69	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
18177	69	11	31	50	Dienstleistungsbetriebe	5000000
18178	69	11	32	51	Küchen und Kantinen	5010000
18179	69	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
18180	69	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
18181	69	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
18182	69	11	36	61	Gewerbebetriebe	6010000
18183	69	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
18184	69	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
18185	69	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
18186	69	11	40	80	Futtermittelbetriebe	8000000
18187	69	11	41	99	keine Angabe	9999999
18188	70	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
18189	70	11	2	10	Erzeuger (Urproduktion)	1000000
18190	70	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
18191	70	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
18192	70	11	5	13	Imkerei	1030000
18193	70	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
18194	70	11	7	20	Hersteller und Abpacker	2000000
18195	70	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
18196	70	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
18197	70	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
18198	70	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
18199	70	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
18200	70	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
18201	70	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
18202	70	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
18203	70	11	16	29	Hersteller von Bedarfsgegenständen	2090000
18204	70	11	17	30	Vertriebsunternehmer und Transporteure	3000000
18205	70	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
18206	70	11	19	32	Lebensmittellager	3020000
18207	70	11	20	33	Umpackbetriebe	3030000
18208	70	11	21	34	Transporteure von Lebensmitteln	3040000
18209	70	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
18210	70	11	23	36	Großhändler von kosmetischen Mitteln	3060000
18211	70	11	24	37	Großhändler von Bedarfsgegenständen	3070000
18212	70	11	25	40	Einzelhändler	4000000
18213	70	11	26	41	Lebensmitteleinzelhandel	4010000
18214	70	11	27	42	Anderer Einzelhandel	4020000
18215	70	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
18216	70	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
18217	70	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
18218	70	11	31	50	Dienstleistungsbetriebe	5000000
18219	70	11	32	51	Küchen und Kantinen	5010000
18220	70	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
18221	70	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
18222	70	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
18223	70	11	36	61	Gewerbebetriebe	6010000
18224	70	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
18225	70	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
18226	70	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
18227	70	11	40	80	Futtermittelbetriebe	8000000
18228	70	11	41	99	keine Angabe	9999999
18229	71	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
18230	71	11	2	10	Erzeuger (Urproduktion)	1000000
18231	71	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
18232	71	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
18233	71	11	5	13	Imkerei	1030000
18234	71	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
18235	71	11	7	20	Hersteller und Abpacker	2000000
18236	71	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
18237	71	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
18238	71	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
18239	71	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
18240	71	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
18241	71	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
18242	71	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
18243	71	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
18244	71	11	16	29	Hersteller von Bedarfsgegenständen	2090000
18245	71	11	17	30	Vertriebsunternehmer und Transporteure	3000000
18246	71	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
18247	71	11	19	32	Lebensmittellager	3020000
18248	71	11	20	33	Umpackbetriebe	3030000
18249	71	11	21	34	Transporteure von Lebensmitteln	3040000
18250	71	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
18251	71	11	23	36	Großhändler von kosmetischen Mitteln	3060000
18252	71	11	24	37	Großhändler von Bedarfsgegenständen	3070000
18253	71	11	25	40	Einzelhändler	4000000
18254	71	11	26	41	Lebensmitteleinzelhandel	4010000
18255	71	11	27	42	Anderer Einzelhandel	4020000
18256	71	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
18257	71	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
18258	71	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
18259	71	11	31	50	Dienstleistungsbetriebe	5000000
18260	71	11	32	51	Küchen und Kantinen	5010000
18261	71	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
18262	71	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
18263	71	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
18264	71	11	36	61	Gewerbebetriebe	6010000
18265	71	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
18266	71	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
18267	71	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
18268	71	11	40	80	Futtermittelbetriebe	8000000
18269	71	11	41	99	keine Angabe	9999999
18270	72	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
18271	72	11	2	10	Erzeuger (Urproduktion)	1000000
18272	72	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
18273	72	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
18274	72	11	5	13	Imkerei	1030000
18275	72	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
18276	72	11	7	20	Hersteller und Abpacker	2000000
18277	72	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
18278	72	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
18279	72	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
18280	72	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
18281	72	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
18282	72	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
18283	72	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
18284	72	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
18285	72	11	16	29	Hersteller von Bedarfsgegenständen	2090000
18286	72	11	17	30	Vertriebsunternehmer und Transporteure	3000000
18287	72	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
18288	72	11	19	32	Lebensmittellager	3020000
18289	72	11	20	33	Umpackbetriebe	3030000
18290	72	11	21	34	Transporteure von Lebensmitteln	3040000
18291	72	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
18292	72	11	23	36	Großhändler von kosmetischen Mitteln	3060000
18293	72	11	24	37	Großhändler von Bedarfsgegenständen	3070000
18294	72	11	25	40	Einzelhändler	4000000
18295	72	11	26	41	Lebensmitteleinzelhandel	4010000
18296	72	11	27	42	Anderer Einzelhandel	4020000
18297	72	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
18298	72	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
18299	72	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
18300	72	11	31	50	Dienstleistungsbetriebe	5000000
18301	72	11	32	51	Küchen und Kantinen	5010000
18302	72	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
18303	72	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
18304	72	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
18305	72	11	36	61	Gewerbebetriebe	6010000
18306	72	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
18307	72	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
18308	72	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
18309	72	11	40	80	Futtermittelbetriebe	8000000
18310	72	11	41	99	keine Angabe	9999999
18311	73	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
18312	73	11	2	10	Erzeuger (Urproduktion)	1000000
18313	73	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
18314	73	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
18315	73	11	5	13	Imkerei	1030000
18316	73	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
18317	73	11	7	20	Hersteller und Abpacker	2000000
18318	73	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
18319	73	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
18320	73	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
18321	73	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
18322	73	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
18323	73	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
18324	73	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
18325	73	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
18326	73	11	16	29	Hersteller von Bedarfsgegenständen	2090000
18327	73	11	17	30	Vertriebsunternehmer und Transporteure	3000000
18328	73	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
18329	73	11	19	32	Lebensmittellager	3020000
18330	73	11	20	33	Umpackbetriebe	3030000
18331	73	11	21	34	Transporteure von Lebensmitteln	3040000
18332	73	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
18333	73	11	23	36	Großhändler von kosmetischen Mitteln	3060000
18334	73	11	24	37	Großhändler von Bedarfsgegenständen	3070000
18335	73	11	25	40	Einzelhändler	4000000
18336	73	11	26	41	Lebensmitteleinzelhandel	4010000
18337	73	11	27	42	Anderer Einzelhandel	4020000
18338	73	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
18339	73	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
18340	73	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
18341	73	11	31	50	Dienstleistungsbetriebe	5000000
18342	73	11	32	51	Küchen und Kantinen	5010000
18343	73	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
18344	73	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
18345	73	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
18346	73	11	36	61	Gewerbebetriebe	6010000
18347	73	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
18348	73	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
18349	73	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
18350	73	11	40	80	Futtermittelbetriebe	8000000
18351	73	11	41	99	keine Angabe	9999999
18352	74	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
18353	74	11	2	10	Erzeuger (Urproduktion)	1000000
18354	74	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
18355	74	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
18356	74	11	5	13	Imkerei	1030000
18357	74	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
18358	74	11	7	20	Hersteller und Abpacker	2000000
18359	74	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
18360	74	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
18361	74	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
18362	74	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
18363	74	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
18364	74	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
18365	74	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
18366	74	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
18367	74	11	16	29	Hersteller von Bedarfsgegenständen	2090000
18368	74	11	17	30	Vertriebsunternehmer und Transporteure	3000000
18369	74	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
18370	74	11	19	32	Lebensmittellager	3020000
18371	74	11	20	33	Umpackbetriebe	3030000
18372	74	11	21	34	Transporteure von Lebensmitteln	3040000
18373	74	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
18374	74	11	23	36	Großhändler von kosmetischen Mitteln	3060000
18375	74	11	24	37	Großhändler von Bedarfsgegenständen	3070000
18376	74	11	25	40	Einzelhändler	4000000
18377	74	11	26	41	Lebensmitteleinzelhandel	4010000
18378	74	11	27	42	Anderer Einzelhandel	4020000
18379	74	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
18380	74	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
18381	74	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
18382	74	11	31	50	Dienstleistungsbetriebe	5000000
18383	74	11	32	51	Küchen und Kantinen	5010000
18384	74	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
18385	74	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
18386	74	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
18387	74	11	36	61	Gewerbebetriebe	6010000
18388	74	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
18389	74	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
18390	74	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
18391	74	11	40	80	Futtermittelbetriebe	8000000
18392	74	11	41	99	keine Angabe	9999999
18393	75	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
18394	75	11	2	10	Erzeuger (Urproduktion)	1000000
18395	75	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
18396	75	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
18397	75	11	5	13	Imkerei	1030000
18398	75	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
18399	75	11	7	20	Hersteller und Abpacker	2000000
18400	75	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
18401	75	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
18402	75	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
18403	75	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
18404	75	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
18405	75	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
18406	75	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
18407	75	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
18408	75	11	16	29	Hersteller von Bedarfsgegenständen	2090000
18409	75	11	17	30	Vertriebsunternehmer und Transporteure	3000000
18410	75	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
18411	75	11	19	32	Lebensmittellager	3020000
18412	75	11	20	33	Umpackbetriebe	3030000
18413	75	11	21	34	Transporteure von Lebensmitteln	3040000
18414	75	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
18415	75	11	23	36	Großhändler von kosmetischen Mitteln	3060000
18416	75	11	24	37	Großhändler von Bedarfsgegenständen	3070000
18417	75	11	25	40	Einzelhändler	4000000
18418	75	11	26	41	Lebensmitteleinzelhandel	4010000
18419	75	11	27	42	Anderer Einzelhandel	4020000
18420	75	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
18421	75	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
18422	75	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
18423	75	11	31	50	Dienstleistungsbetriebe	5000000
18424	75	11	32	51	Küchen und Kantinen	5010000
18425	75	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
18426	75	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
18427	75	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
18428	75	11	36	61	Gewerbebetriebe	6010000
18429	75	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
18430	75	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
18431	75	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
18432	75	11	40	80	Futtermittelbetriebe	8000000
18433	75	11	41	99	keine Angabe	9999999
18434	76	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
18435	76	11	2	10	Erzeuger (Urproduktion)	1000000
18436	76	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
18437	76	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
18438	76	11	5	13	Imkerei	1030000
18439	76	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
18440	76	11	7	20	Hersteller und Abpacker	2000000
18441	76	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
18442	76	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
18443	76	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
18444	76	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
18445	76	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
18446	76	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
18447	76	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
18448	76	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
18449	76	11	16	29	Hersteller von Bedarfsgegenständen	2090000
18450	76	11	17	30	Vertriebsunternehmer und Transporteure	3000000
18451	76	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
18452	76	11	19	32	Lebensmittellager	3020000
18453	76	11	20	33	Umpackbetriebe	3030000
18454	76	11	21	34	Transporteure von Lebensmitteln	3040000
18455	76	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
18456	76	11	23	36	Großhändler von kosmetischen Mitteln	3060000
18457	76	11	24	37	Großhändler von Bedarfsgegenständen	3070000
18458	76	11	25	40	Einzelhändler	4000000
18459	76	11	26	41	Lebensmitteleinzelhandel	4010000
18460	76	11	27	42	Anderer Einzelhandel	4020000
18461	76	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
18462	76	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
18463	76	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
18464	76	11	31	50	Dienstleistungsbetriebe	5000000
18465	76	11	32	51	Küchen und Kantinen	5010000
18466	76	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
18467	76	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
18468	76	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
18469	76	11	36	61	Gewerbebetriebe	6010000
18470	76	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
18471	76	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
18472	76	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
18473	76	11	40	80	Futtermittelbetriebe	8000000
18474	76	11	41	99	keine Angabe	9999999
18475	77	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
18476	77	11	2	10	Erzeuger (Urproduktion)	1000000
18477	77	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
18478	77	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
18479	77	11	5	13	Imkerei	1030000
18480	77	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
18481	77	11	7	20	Hersteller und Abpacker	2000000
18482	77	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
18483	77	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
18484	77	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
18485	77	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
18486	77	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
18487	77	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
18488	77	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
18489	77	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
18490	77	11	16	29	Hersteller von Bedarfsgegenständen	2090000
18491	77	11	17	30	Vertriebsunternehmer und Transporteure	3000000
18492	77	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
18493	77	11	19	32	Lebensmittellager	3020000
18494	77	11	20	33	Umpackbetriebe	3030000
18495	77	11	21	34	Transporteure von Lebensmitteln	3040000
18496	77	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
18497	77	11	23	36	Großhändler von kosmetischen Mitteln	3060000
18498	77	11	24	37	Großhändler von Bedarfsgegenständen	3070000
18499	77	11	25	40	Einzelhändler	4000000
18500	77	11	26	41	Lebensmitteleinzelhandel	4010000
18501	77	11	27	42	Anderer Einzelhandel	4020000
18502	77	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
18503	77	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
18504	77	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
18505	77	11	31	50	Dienstleistungsbetriebe	5000000
18506	77	11	32	51	Küchen und Kantinen	5010000
18507	77	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
18508	77	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
18509	77	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
18510	77	11	36	61	Gewerbebetriebe	6010000
18511	77	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
18512	77	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
18513	77	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
18514	77	11	40	80	Futtermittelbetriebe	8000000
18515	77	11	41	99	keine Angabe	9999999
18516	78	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
18517	78	11	2	10	Erzeuger (Urproduktion)	1000000
18518	78	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
18519	78	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
18520	78	11	5	13	Imkerei	1030000
18521	78	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
18522	78	11	7	20	Hersteller und Abpacker	2000000
18523	78	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
18524	78	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
18525	78	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
18526	78	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
18527	78	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
18528	78	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
18529	78	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
18530	78	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
18531	78	11	16	29	Hersteller von Bedarfsgegenständen	2090000
18532	78	11	17	30	Vertriebsunternehmer und Transporteure	3000000
18533	78	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
18534	78	11	19	32	Lebensmittellager	3020000
18535	78	11	20	33	Umpackbetriebe	3030000
18536	78	11	21	34	Transporteure von Lebensmitteln	3040000
18537	78	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
18538	78	11	23	36	Großhändler von kosmetischen Mitteln	3060000
18539	78	11	24	37	Großhändler von Bedarfsgegenständen	3070000
18540	78	11	25	40	Einzelhändler	4000000
18541	78	11	26	41	Lebensmitteleinzelhandel	4010000
18542	78	11	27	42	Anderer Einzelhandel	4020000
18543	78	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
18544	78	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
18545	78	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
18546	78	11	31	50	Dienstleistungsbetriebe	5000000
18547	78	11	32	51	Küchen und Kantinen	5010000
18548	78	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
18549	78	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
18550	78	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
18551	78	11	36	61	Gewerbebetriebe	6010000
18552	78	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
18553	78	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
18554	78	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
18555	78	11	40	80	Futtermittelbetriebe	8000000
18556	78	11	41	99	keine Angabe	9999999
18557	79	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
18558	79	11	2	10	Erzeuger (Urproduktion)	1000000
18559	79	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
18560	79	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
18561	79	11	5	13	Imkerei	1030000
18562	79	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
18563	79	11	7	20	Hersteller und Abpacker	2000000
18564	79	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
18565	79	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
18566	79	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
18567	79	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
18568	79	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
18569	79	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
18570	79	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
18571	79	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
18572	79	11	16	29	Hersteller von Bedarfsgegenständen	2090000
18573	79	11	17	30	Vertriebsunternehmer und Transporteure	3000000
18574	79	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
18575	79	11	19	32	Lebensmittellager	3020000
18576	79	11	20	33	Umpackbetriebe	3030000
18577	79	11	21	34	Transporteure von Lebensmitteln	3040000
18578	79	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
18579	79	11	23	36	Großhändler von kosmetischen Mitteln	3060000
18580	79	11	24	37	Großhändler von Bedarfsgegenständen	3070000
18581	79	11	25	40	Einzelhändler	4000000
18582	79	11	26	41	Lebensmitteleinzelhandel	4010000
18583	79	11	27	42	Anderer Einzelhandel	4020000
18584	79	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
18585	79	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
18586	79	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
18587	79	11	31	50	Dienstleistungsbetriebe	5000000
18588	79	11	32	51	Küchen und Kantinen	5010000
18589	79	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
18590	79	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
18591	79	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
18592	79	11	36	61	Gewerbebetriebe	6010000
18593	79	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
18594	79	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
18595	79	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
18596	79	11	40	80	Futtermittelbetriebe	8000000
18597	79	11	41	99	keine Angabe	9999999
18598	80	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
18599	80	11	2	10	Erzeuger (Urproduktion)	1000000
18600	80	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
18601	80	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
18602	80	11	5	13	Imkerei	1030000
18603	80	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
18604	80	11	7	20	Hersteller und Abpacker	2000000
18605	80	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
18606	80	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
18607	80	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
18608	80	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
18609	80	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
18610	80	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
18611	80	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
18612	80	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
18613	80	11	16	29	Hersteller von Bedarfsgegenständen	2090000
18614	80	11	17	30	Vertriebsunternehmer und Transporteure	3000000
18615	80	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
18616	80	11	19	32	Lebensmittellager	3020000
18617	80	11	20	33	Umpackbetriebe	3030000
18618	80	11	21	34	Transporteure von Lebensmitteln	3040000
18619	80	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
18620	80	11	23	36	Großhändler von kosmetischen Mitteln	3060000
18621	80	11	24	37	Großhändler von Bedarfsgegenständen	3070000
18622	80	11	25	40	Einzelhändler	4000000
18623	80	11	26	41	Lebensmitteleinzelhandel	4010000
18624	80	11	27	42	Anderer Einzelhandel	4020000
18625	80	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
18626	80	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
18627	80	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
18628	80	11	31	50	Dienstleistungsbetriebe	5000000
18629	80	11	32	51	Küchen und Kantinen	5010000
18630	80	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
18631	80	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
18632	80	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
18633	80	11	36	61	Gewerbebetriebe	6010000
18634	80	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
18635	80	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
18636	80	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
18637	80	11	40	80	Futtermittelbetriebe	8000000
18638	80	11	41	99	keine Angabe	9999999
18639	87	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
18640	87	11	2	10	Erzeuger (Urproduktion)	1000000
18641	87	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
18642	87	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
18643	87	11	5	13	Imkerei	1030000
18644	87	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
18645	87	11	7	20	Hersteller und Abpacker	2000000
18646	87	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
18647	87	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
18648	87	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
18649	87	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
18650	87	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
18651	87	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
18652	87	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
18653	87	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
18654	87	11	16	29	Hersteller von Bedarfsgegenständen	2090000
18655	87	11	17	30	Vertriebsunternehmer und Transporteure	3000000
18656	87	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
18657	87	11	19	32	Lebensmittellager	3020000
18658	87	11	20	33	Umpackbetriebe	3030000
18659	87	11	21	34	Transporteure von Lebensmitteln	3040000
18660	87	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
18661	87	11	23	36	Großhändler von kosmetischen Mitteln	3060000
18662	87	11	24	37	Großhändler von Bedarfsgegenständen	3070000
18663	87	11	25	40	Einzelhändler	4000000
18664	87	11	26	41	Lebensmitteleinzelhandel	4010000
18665	87	11	27	42	Anderer Einzelhandel	4020000
18666	87	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
18667	87	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
18668	87	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
18669	87	11	31	50	Dienstleistungsbetriebe	5000000
18670	87	11	32	51	Küchen und Kantinen	5010000
18671	87	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
18672	87	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
18673	87	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
18674	87	11	36	61	Gewerbebetriebe	6010000
18675	87	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
18676	87	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
18677	87	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
18678	87	11	40	80	Futtermittelbetriebe	8000000
18679	87	11	41	99	keine Angabe	9999999
18680	88	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
18681	88	11	2	10	Erzeuger (Urproduktion)	1000000
18682	88	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
18683	88	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
18684	88	11	5	13	Imkerei	1030000
18685	88	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
18686	88	11	7	20	Hersteller und Abpacker	2000000
18687	88	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
18688	88	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
18689	88	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
18690	88	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
18691	88	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
18692	88	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
18693	88	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
18694	88	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
18695	88	11	16	29	Hersteller von Bedarfsgegenständen	2090000
18696	88	11	17	30	Vertriebsunternehmer und Transporteure	3000000
18697	88	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
18698	88	11	19	32	Lebensmittellager	3020000
18699	88	11	20	33	Umpackbetriebe	3030000
18700	88	11	21	34	Transporteure von Lebensmitteln	3040000
18701	88	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
18702	88	11	23	36	Großhändler von kosmetischen Mitteln	3060000
18703	88	11	24	37	Großhändler von Bedarfsgegenständen	3070000
18704	88	11	25	40	Einzelhändler	4000000
18705	88	11	26	41	Lebensmitteleinzelhandel	4010000
18706	88	11	27	42	Anderer Einzelhandel	4020000
18707	88	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
18708	88	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
18709	88	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
18710	88	11	31	50	Dienstleistungsbetriebe	5000000
18711	88	11	32	51	Küchen und Kantinen	5010000
18712	88	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
18713	88	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
18714	88	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
18715	88	11	36	61	Gewerbebetriebe	6010000
18716	88	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
18717	88	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
18718	88	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
18719	88	11	40	80	Futtermittelbetriebe	8000000
18720	88	11	41	99	keine Angabe	9999999
18721	89	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
18722	89	11	2	10	Erzeuger (Urproduktion)	1000000
18723	89	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
18724	89	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
18725	89	11	5	13	Imkerei	1030000
18726	89	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
18727	89	11	7	20	Hersteller und Abpacker	2000000
18728	89	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
18729	89	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
18730	89	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
18731	89	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
18732	89	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
18733	89	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
18734	89	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
18735	89	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
18736	89	11	16	29	Hersteller von Bedarfsgegenständen	2090000
18737	89	11	17	30	Vertriebsunternehmer und Transporteure	3000000
18738	89	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
18739	89	11	19	32	Lebensmittellager	3020000
18740	89	11	20	33	Umpackbetriebe	3030000
18741	89	11	21	34	Transporteure von Lebensmitteln	3040000
18742	89	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
18743	89	11	23	36	Großhändler von kosmetischen Mitteln	3060000
18744	89	11	24	37	Großhändler von Bedarfsgegenständen	3070000
18745	89	11	25	40	Einzelhändler	4000000
18746	89	11	26	41	Lebensmitteleinzelhandel	4010000
18747	89	11	27	42	Anderer Einzelhandel	4020000
18748	89	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
18749	89	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
18750	89	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
18751	89	11	31	50	Dienstleistungsbetriebe	5000000
18752	89	11	32	51	Küchen und Kantinen	5010000
18753	89	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
18754	89	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
18755	89	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
18756	89	11	36	61	Gewerbebetriebe	6010000
18757	89	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
18758	89	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
18759	89	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
18760	89	11	40	80	Futtermittelbetriebe	8000000
18761	89	11	41	99	keine Angabe	9999999
18762	90	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
18763	90	11	2	10	Erzeuger (Urproduktion)	1000000
18764	90	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
18765	90	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
18766	90	11	5	13	Imkerei	1030000
18767	90	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
18768	90	11	7	20	Hersteller und Abpacker	2000000
18769	90	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
18770	90	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
18771	90	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
18772	90	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
18773	90	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
18774	90	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
18775	90	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
18776	90	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
18777	90	11	16	29	Hersteller von Bedarfsgegenständen	2090000
18778	90	11	17	30	Vertriebsunternehmer und Transporteure	3000000
18779	90	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
18780	90	11	19	32	Lebensmittellager	3020000
18781	90	11	20	33	Umpackbetriebe	3030000
18782	90	11	21	34	Transporteure von Lebensmitteln	3040000
18783	90	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
18784	90	11	23	36	Großhändler von kosmetischen Mitteln	3060000
18785	90	11	24	37	Großhändler von Bedarfsgegenständen	3070000
18786	90	11	25	40	Einzelhändler	4000000
18787	90	11	26	41	Lebensmitteleinzelhandel	4010000
18788	90	11	27	42	Anderer Einzelhandel	4020000
18789	90	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
18790	90	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
18791	90	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
18792	90	11	31	50	Dienstleistungsbetriebe	5000000
18793	90	11	32	51	Küchen und Kantinen	5010000
18794	90	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
18795	90	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
18796	90	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
18797	90	11	36	61	Gewerbebetriebe	6010000
18798	90	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
18799	90	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
18800	90	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
18801	90	11	40	80	Futtermittelbetriebe	8000000
18802	90	11	41	99	keine Angabe	9999999
18803	91	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
18804	91	11	2	10	Erzeuger (Urproduktion)	1000000
18805	91	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
18806	91	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
18807	91	11	5	13	Imkerei	1030000
18808	91	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
18809	91	11	7	20	Hersteller und Abpacker	2000000
18810	91	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
18811	91	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
18812	91	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
18813	91	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
18814	91	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
18815	91	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
18816	91	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
18817	91	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
18818	91	11	16	29	Hersteller von Bedarfsgegenständen	2090000
18819	91	11	17	30	Vertriebsunternehmer und Transporteure	3000000
18820	91	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
18821	91	11	19	32	Lebensmittellager	3020000
18822	91	11	20	33	Umpackbetriebe	3030000
18823	91	11	21	34	Transporteure von Lebensmitteln	3040000
18824	91	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
18825	91	11	23	36	Großhändler von kosmetischen Mitteln	3060000
18826	91	11	24	37	Großhändler von Bedarfsgegenständen	3070000
18827	91	11	25	40	Einzelhändler	4000000
18828	91	11	26	41	Lebensmitteleinzelhandel	4010000
18829	91	11	27	42	Anderer Einzelhandel	4020000
18830	91	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
18831	91	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
18832	91	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
18833	91	11	31	50	Dienstleistungsbetriebe	5000000
18834	91	11	32	51	Küchen und Kantinen	5010000
18835	91	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
18836	91	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
18837	91	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
18838	91	11	36	61	Gewerbebetriebe	6010000
18839	91	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
18840	91	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
18841	91	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
18842	91	11	40	80	Futtermittelbetriebe	8000000
18843	91	11	41	99	keine Angabe	9999999
18844	92	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
18845	92	11	2	10	Erzeuger (Urproduktion)	1000000
18846	92	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
18847	92	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
18848	92	11	5	13	Imkerei	1030000
18849	92	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
18850	92	11	7	20	Hersteller und Abpacker	2000000
18851	92	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
18852	92	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
18853	92	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
18854	92	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
18855	92	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
18856	92	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
18857	92	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
18858	92	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
18859	92	11	16	29	Hersteller von Bedarfsgegenständen	2090000
18860	92	11	17	30	Vertriebsunternehmer und Transporteure	3000000
18861	92	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
18862	92	11	19	32	Lebensmittellager	3020000
18863	92	11	20	33	Umpackbetriebe	3030000
18864	92	11	21	34	Transporteure von Lebensmitteln	3040000
18865	92	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
18866	92	11	23	36	Großhändler von kosmetischen Mitteln	3060000
18867	92	11	24	37	Großhändler von Bedarfsgegenständen	3070000
18868	92	11	25	40	Einzelhändler	4000000
18869	92	11	26	41	Lebensmitteleinzelhandel	4010000
18870	92	11	27	42	Anderer Einzelhandel	4020000
18871	92	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
18872	92	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
18873	92	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
18874	92	11	31	50	Dienstleistungsbetriebe	5000000
18875	92	11	32	51	Küchen und Kantinen	5010000
18876	92	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
18877	92	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
18878	92	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
18879	92	11	36	61	Gewerbebetriebe	6010000
18880	92	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
18881	92	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
18882	92	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
18883	92	11	40	80	Futtermittelbetriebe	8000000
18884	92	11	41	99	keine Angabe	9999999
18885	93	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
18886	93	11	2	10	Erzeuger (Urproduktion)	1000000
18887	93	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
18888	93	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
18889	93	11	5	13	Imkerei	1030000
18890	93	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
18891	93	11	7	20	Hersteller und Abpacker	2000000
18892	93	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
18893	93	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
18894	93	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
18895	93	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
18896	93	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
18897	93	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
18898	93	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
18899	93	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
18900	93	11	16	29	Hersteller von Bedarfsgegenständen	2090000
18901	93	11	17	30	Vertriebsunternehmer und Transporteure	3000000
18902	93	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
18903	93	11	19	32	Lebensmittellager	3020000
18904	93	11	20	33	Umpackbetriebe	3030000
18905	93	11	21	34	Transporteure von Lebensmitteln	3040000
18906	93	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
18907	93	11	23	36	Großhändler von kosmetischen Mitteln	3060000
18908	93	11	24	37	Großhändler von Bedarfsgegenständen	3070000
18909	93	11	25	40	Einzelhändler	4000000
18910	93	11	26	41	Lebensmitteleinzelhandel	4010000
18911	93	11	27	42	Anderer Einzelhandel	4020000
18912	93	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
18913	93	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
18914	93	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
18915	93	11	31	50	Dienstleistungsbetriebe	5000000
18916	93	11	32	51	Küchen und Kantinen	5010000
18917	93	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
18918	93	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
18919	93	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
18920	93	11	36	61	Gewerbebetriebe	6010000
18921	93	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
18922	93	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
18923	93	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
18924	93	11	40	80	Futtermittelbetriebe	8000000
18925	93	11	41	99	keine Angabe	9999999
18926	94	11	1	0	Betriebsarten	Schlüssel aus BVL-Katalog
18927	94	11	2	10	Erzeuger (Urproduktion)	1000000
18928	94	11	3	11	Erzeuger von Lebensmitteln tierischer Herkunft	1010000
18929	94	11	4	12	Fischerei- Krusten- Schalen- und Weichtierbetriebe	1020000
18930	94	11	5	13	Imkerei	1030000
18931	94	11	6	14	Erzeuger von Lebensmitteln pflanzlicher Herkunft	1040000
18932	94	11	7	20	Hersteller und Abpacker	2000000
18933	94	11	8	21	Milchbe- und Milchverarbeitungsbetriebe	2010000
18934	94	11	9	22	Betrieb zur Behandlung von Eiern Hersteller von Eiprodukten	2020000
18935	94	11	10	23	Hersteller von Fleisch und Fleischerzeugnissen	2030000
18936	94	11	11	24	Fisch- Krusten- Schalen- und Weichtierbe- und -verarbeitungsbetriebe	2040000
18937	94	11	12	25	Hersteller von pflanzlichen Lebensmitteln inkl. Abpacker	2050000
18938	94	11	13	26	Hersteller von anderen Lebensmitteln und Zusatzstoffen inkl. Abpacker	2060000
18939	94	11	14	27	Hersteller von Tabak und Tabakerzeugnissen	2070000
18940	94	11	15	28	Hersteller von kosmetischen Mitteln (einschl. Mittel zum Tätowieren)	2080000
18941	94	11	16	29	Hersteller von Bedarfsgegenständen	2090000
18942	94	11	17	30	Vertriebsunternehmer und Transporteure	3000000
18943	94	11	18	31	Großhändler Importeure und Exporteure von Lebensmitteln	3010000
18944	94	11	19	32	Lebensmittellager	3020000
18945	94	11	20	33	Umpackbetriebe	3030000
18946	94	11	21	34	Transporteure von Lebensmitteln	3040000
18947	94	11	22	35	Großhändler von Tabak und -erzeugnissen	3050000
18948	94	11	23	36	Großhändler von kosmetischen Mitteln	3060000
18949	94	11	24	37	Großhändler von Bedarfsgegenständen	3070000
18950	94	11	25	40	Einzelhändler	4000000
18951	94	11	26	41	Lebensmitteleinzelhandel	4010000
18952	94	11	27	42	Anderer Einzelhandel	4020000
18953	94	11	28	43	Einzelhandel von Tabak und Tabakerzeugnissen	4030000
18954	94	11	29	44	Einzelhandel von kosmetischen Mitteln	4040000
18955	94	11	30	45	Einzelhandel von Bedarfsgegenständen	4050000
18956	94	11	31	50	Dienstleistungsbetriebe	5000000
18957	94	11	32	51	Küchen und Kantinen	5010000
18958	94	11	33	52	Gaststätten und Imbisseinrichtungen	5020000
18959	94	11	34	53	Veranstalter von Volksfesten Märkten Messen u. a. öffentl. Veranstaltungen	5030000
18960	94	11	35	60	Hersteller die i. w. auf der Stufe des Einzelhandels verkaufen	6000000
18961	94	11	36	61	Gewerbebetriebe	6010000
18962	94	11	37	62	Direktvermarkter mit eigener Herstellung von LM tierischer Herkunft	6020000
18963	94	11	38	63	Direktvermarkter mit eigener Herstellung von LM pflanzlicher Herkunft	6030000
18964	94	11	39	64	Direktvermarkter mit eigener Herstellung von LM tierischer und pflanzlischer Herkunft	6040000
18965	94	11	40	80	Futtermittelbetriebe	8000000
18966	94	11	41	99	keine Angabe	9999999
\.
