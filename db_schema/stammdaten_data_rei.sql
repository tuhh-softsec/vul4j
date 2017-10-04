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

COPY rei_progpunkt (id, reiid, rei_prog_punkt) FROM stdin;
1	A1:1.1	Luft/äußere Strahlung: KKW, best.gem. Betrieb, Gen.inhaber                                                              
2	A1:1.1a	Luft/äußere Strahlung: Gamma-Ortsdosisleistung; KKW, best.gem. Betrieb, Gen.inhaber                                     
3	A1:1.1b	Luft/äußere Strahlung: Gamma-Ortsdosis; KKW, best.gem. Betrieb, Gen.inhaber                                             
4	A1:1.1c	Luft/äußere Strahlung: Gamma-Ortsdosis in der Umgebung; KKW, best.gem. Betrieb, Gen.inhaber
5	A1:1.1d	Luft/äußere Strahlung: Neutronen-Ortsdosis; KKW, best.gem. Betrieb, Gen.inhaber
6	A1:1.2	Luft/Aerosole: KKW, best.gem. Betrieb, Gen.inhaber                                                                      
7	A1:1.3	Luft/gasförmiges Iod: KKW, best.gem. Betrieb, Gen.inhaber                                                               
8	A1:2.0	Niederschlag: KKW, best.gem. Betrieb, Gen.inhaber                                                                       
9	A1:3.0	Boden: KKW, best.gem. Betrieb, Gen.inhaber                                                                              
10	A1:4.0	Pflanzen/Bewuchs: KKW, best.gem. Betrieb, Gen.inhaber                                                                   
11	A1:5.0	Oberflächenwasser: KKW, best.gem. Betrieb, Gen.inhaber                                                                  
12	A1:5.0a	Oberflächenwasser: Gammaspektrometrie, Aktivitätskonzentr. einzelner Nuklide; KKW, best.gem. Betrieb, Gen.inhaber       
13	A1:5.0b	Oberflächenwasser: Tritium-Aktivitätskonzentration; KKW, best.gem. Betrieb, Gen.inhaber                                 
14	A1:6.0	Grundwasser: KKW, best.gem. Betrieb, Gen.inhaber                                                                        
15	A1:6.0a	Grundwasser: Gammaspektrometrie, Aktivitätskonzentr. einzelner Nuklide; KKW, best.gem. Betrieb, Gen.inhaber             
16	A1:6.0b	Grundwasser: Tritium-Aktivitätskonzentration; KKW, best.gem. Betr., Gen.inhaber                                         
17	A1:7.2	Sediment: KKW, best.gem. Betrieb, Gen.inhaber
18	A1:8.0	Nahrungsmittel pflanzl. Herkunft: KKW, best.gem. Betrieb, Gen.inhaber
19	A2:1.1	Luft/äußere Strahlung: Gamma-Ortsdosis; KKW, best.gem. Betrieb, unabh. Messstelle                                       
20	A2:1.1b	Luft/äußere Strahlung: Gamma-Ortsdosis am Anlagenzaun; KKW, best.gem. Betrieb, unabh. Messstelle
21	A2:1.1c	Luft/äußere Strahlung: Gamma-Ortsdosis in der Umgebung; KKW, best.gem. Betrieb, unabh. Messstelle
22	A2:1.1d	Luft/äußere Strahlung: Neutronen-Ortsdosis; KKW, best.gem. Betrieb, unabh. Messstelle
23	A2:1.2	Luft/Aerosole: KKW, best.gem. Betrieb, unabh. Messstelle                                                                
24	A2:2.0	Niederschlag: KKW, best.gem. Betrieb, unabh. Messstelle                                                                 
25	A2:3.0	Boden: KKW, best.gem. Betrieb, unabh. Messstelle                                                                        
26	A2:4.0	Weide-/Wiesenbewuchs: KKW, best.gem. Betrieb, unabh. Messstelle                                                         
27	A2:5.0	Nahrungsmittel pflanzl. Herkunft: KKW, best.gem. Betrieb, unabh. Messstelle                                             
28	A2:5.0a	Nahrungsmittel pflanzl. Herkunft: Gammaspektro., spezif. Einzelradionuklidaktivität; KKW, best.gem. Betr., unabh. MST  
29	A2:5.0b	Nahrungsmittel pflanzl. Herkunft: spezifische Sr-90-Aktivität; KKW, best.gem. Betrieb, unabh. Messstelle                
30	A2:6.0	Kuhmilch: KKW, best.gem. Betrieb, unabh. Messstelle                                                                     
31	A2:6.0a	Kuhmilch: Gammaspektrometrie, Aktivitätskonzentr. einzelner Nuklide; KKW, best.gem. Betrieb, unabh. Messstelle          
32	A2:6.0b	Kuhmilch: Sr-90-Aktivitätskonzentration; KKW, best.gem. Betrieb, unabh. Messstelle                                      
33	A2:6.0c	Kuhmilch: I-131-Aktivitätskonzentration; KKW, best.gem. Betrieb, unabh. Messstelle                                      
34	A2:7.1	Oberflächenwasser: KKW, best.gem. Betrieb, unabh. Messstelle                                                            
35	A2:7.1a	Oberflächenwasser: Gammaspektrometrie, Aktivitätskonzentr. einzelner Nuklide; KKW, best.gem. Betrieb, unabh. Messstelle 
36	A2:7.1b	Oberflächenwasser: Tritium-Aktivitätskonzentration; KKW, best.gem. Betrieb, unabh. Messstelle                           
37	A2:7.1c	Oberflächenwasser: I131-Aktivitätskonzentration; KKW, best.gem. Betrieb, unabh. Messstelle
38	A2:7.2	Sediment: KKW, best.gem. Betrieb, unabh. Messstelle                                                                     
39	A2:8.0	Fisch: KKW, best.gem. Betrieb, unabh. Messstelle                                                                        
40	A2:8.1	Wasserpflanzen: KKW, best.gem. Betrieb, unabh. Messstelle
41	A2:9.0	Trinkwasser: KKW, best.gem. Betrieb, unabh. Messstelle                                                                  
42	A2:9.0a	Trinkwasser (Brunnen): KKW, best.gem. Betrieb, unabh. Messstelle                                                        
43	A2:9.0b	Trinkwasser: Sr-90-Aktivitätskonzentration; KKW, best.gem. Betrieb, unabh. Messstelle                                   
44	A2:9.0c	Trinkwasser (Wasserwerk): KKW, best.gem. Betrieb, unabh. Messstelle                                                     
45	A2:9.0d	Trinkwasser: Tritium-Aktivitätskonzentration; KKW, best.gem. Betrieb, unabh. Messstelle                                 
46	A3:1.1	Luft/äußere Strahlung: KKW, Störfall/Unfall, Gen.inhaber     
47	A3:1.1a	Luft/äußere Strahlung: Gamma-Ortsdosisleistung; KKW, Störfall/Unfall, Gen.inhaber                                       
48	A3:1.1b	Luft/äußere Strahlung: Gamma-Ortsdosis; KKW, Störfall/Unfall, Gen.inhaber                                               
49	A3:1.2	Luft/Aerosole: KKW, Störfall/Unfall, Gen.inhaber                                                                        
50	A3:1.3	Luft/gasförmiges Iod: KKW, Störfall/Unfall, Gen.inhaber                                                                 
51	A3:2.1	Bodenoberfläche: KKW, Störfall/Unfall, Gen.inhaber                                                                      
52	A3:2.2	Boden: KKW, Störfall/Unfall, Gen.inhaber                                                                                
53	A3:3.0	Weide-/Wiesenbewuchs: KKW, Störfall/Unfall, Gen.inhaber                                                                 
54	A3:4.0	Oberflächenwasser::KKW, Störfall/Unfall, Gen.inhaber                                                                    
55	A4:1.1	Luft/äußere Strahlung: KKW, Störfall/Unfall, unabh. Messstelle
56	A4:1.1a	Luft/äußere Strahlung: Gamma-Ortsdosisleistung; KKW, Störfall/Unfall, unabh. Messstelle                                 
57	A4:1.1b	Luft/äußere Strahlung: Gamma-Ortsdosis; KKW, Störfall/Unfall, unabh. Messstelle                                         
58	A4:1.2	Luft/Aerosole: KKW, Störfall/Unfall, unabh. Messstelle                                                                  
59	A4:1.3	Luft/gasförmiges Iod: KKW, Störfall/Unfall, unabh. Messstelle                                                           
60	A4:2.1	Bodenoberfläche: KKW, Störfall/Unfall, unabh. Messstelle                                                                
61	A4:2.2	Boden: KKW, Störfall/Unfall, unabh. Messstelle                                                                          
62	A4:3.0	Weide-/Wiesenbewuchs: KKW, Störfall/Unfall, unabh. Messstelle                                                           
63	A4:4.0	Kuhmilch: KKW, Störfall/Unfall, unabh. Messstelle                                                                       
64	A4:5.1	Nahrungsmittel pflanzlicher Herkunft: KKW, Störfall/Unfall, unabh. Messstelle
65	A4:5.2	Nahrungsmittel tierischer Herkunft: KKW, Störfall/Unfall, unabh. Messstelle                                             
66	A4:6.0	Oberflächenwasser: KKW, Störfall/Unfall, unabh. Messstelle                                                              
67	A4:7.0	Fisch: KKW, Störfall/Unfall, unabh. Messstelle                                                                          
68	A4:8.0	Trinkwasser: KKW, Störfall/Unfall, unabh. Messstelle                                                                    
69	A5:1.4	Niederschlag: KKW, best.gem. Betrieb, Behördenmessprogramm
70	A5:3.0	Boden: KKW, best.gem. Betrieb, Behördenmessprogramm
71	A5:4.0	Weide-/Wiesenbewuchs: KKW, best.gem. Betrieb, Behördenmessprogramm
72	A5:5.0	Nahrungsmittel außer Milch: KKW, best.gem. Betrieb, Behördenmessprogramm
73	A5:6.0	Kuhmilch: KKW, best.gem. Betrieb, Behördenmessprogramm
74	A5:7.1	Oberflächenwasser: KKW, best.gem. Betrieb, Behördenmessprogramm
75	A5:7.2	Sediment: KKW, best.gem. Betrieb, Behördenmessprogramm
76	A5:7.3	Schwebstoffe: KKW, best.gem. Betrieb, Behördenmessprogramm
77	A5:9.0	Trinkwasser: KKW, best.gem. Betrieb, Behördenmessprogramm
78	B1:1.1	Luft/äußere Strahlung: Gamma-Ortsdosis; Brennelementfabrik, best.gem, Betrieb, Gen.inhaber                              
79	B1:1.2	Neutronenstrahlung: Neutronen-Ortsdosis; Brennelementfabrik, best.gem. Betrieb, Gen.inhaber                             
80	B1:1.3	Luft/Aerosole: Brennelementfabrik, best.gem. Betrieb, Gen.inhaber                                                       
81	B1:2.0	Niederschlag: Brennelementfabrik, best.gem. Betrieb, Gen.inhaber                                                        
82	B1:3.0	Grundwasser: Brennelementfabrik, best.gem. Betrieb, Gen.inhaber                                                         
83	B2:1.1	Luft/äußere Strahlung: Gamma-Ortsdosis; Brennelementfabrik, best.gem. Betrieb, unabh. Messstelle                        
84	B2:1.2	Neutronenstrahlung: Neutronen-Ortsdosis; Brennelementfabrik, best.gem. Betrieb, unabh. Messstelle                       
85	B2:1.3	Luft/Aerosole: Brennelementfabrik, best.gem. Betrieb, unabh. Messstelle  
86	B2:1.3a	Luft/Aerosole: Gesamt-Alpha-Aktivitätskonzentration; Brennelementfabrik, best.gem. Betrieb, unabh. Messstelle           
87	B2:1.3b	Luft/Aerosole: alphanuklidspez. Messung, Aktivitätskonz. einz. Nuklide; Brennelementfabrik, best.gem. Betr.,unabh. MST  
88	B2:2.0	Niederschlag: Brennelementfabrik, best.gem. Betrieb, unabh. Messstelle                                                  
89	B2:3.0	Weide-/Wiesenbewuchs: Brennelementfabrik, best.gem. Betrieb, unabh. Messstelle                                          
90	B2:4.1	Oberflächenwasser: Brennelementfabrik, best.gem. Betrieb, unabh. Messstelle                                             
91	B2:4.2	Klärschlamm: Brennelementfabrik, best.gem. Betrieb, unabh. Messstelle                                                   
92	B2:5.0	Grundwasser: Brennelementfabrik, best.gem. Betrieb, unabh. Messstelle                                                   
93	B2:6.0	unbearbeiteter Boden: Brennelementfabrik, best.gem. Betrieb, unabh. Messstelle
94	B3:1.1	Luft/äußere Strahlung: Brennelementfabrik, Störfall/Unfall, Gen.inhaber 
95	B3:1.1a	Luft/äußere Strahlung: Gamma-Ortsdosisleistung; Brennelementfabrik, Störfall/Unfall, Gen.inhaber                        
96	B3:1.1b	Luft/äußere Strahlung: Gamma-Ortsdosis; Brennelementfabrik, Störfall/Unfall, Gen.inhaber                                
97	B3:1.2	Luft/Aerosole: Brennelementfabrik, Störfall/Unfall, Gen.inhaber  
98	B3:1.2a	Luft/Aerosole: Gammaspektrometrie, Aktivitätskonz. einzelner Nuklide; Brennelementfabrik, Störfall/Unfall, Gen.inhaber  
99	B3:1.2b	Luft/Aerosole: alphanuklidspez. Messung,Aktivitätskonz. einz. Nuklide;  Brennelementfabrik, Störfall/Unfall, Gen.inhaber
100	B3:1.3	Luft/gasförmiges Iod: Brennelementfabrik, Störfall/Unfall, Gen.inhaber                                                  
101	B3:2.0	Bodenoberfläche: Brennelementfabrik, Störfall/Unfall, Gen.inhab.
102	B3:2.0a	Bodenoberfläche: Gesamt-Alpha-Kontaminationsmessung auf vorber. Flächen; Brennelementfabrik, Störfall/Unfall, Gen.inhab.
103	B3:2.0b	Bodenoberfläche: alphanuklidspez. Messung,Aktivitätskonz. einz. Nuklide; Brennelementfabrik, Störfall/Unfall, Gen.inhab.
104	B3:3.0	Weide-/Wiesenbewuchs: Brennelementfabrik, Störfall/Unfall, Gen.inhaber 
105	B3:3.0a	Weide-/Wiesenbewuchs: Gammaspektrometrie, spezifische I-131-Aktivität; Brennelementfabrik, Störfall/Unfall, Gen.inhaber 
106	B3:3.0b	Weide-/Wiesenbewuchs: alphanuklidspez. Messung, spezif. Aktivität einz. Nuklide; Brennelementfabrik, Störfall/Unf., Gen.
107	B3:4.0	Oberflächenwasser: Brennelementfabrik, Störfall/Unfall, Gen.inhaber
267	D3:1.1	Luft/äußere Strahlung: Sonderfälle, Störfall/Unfall, Gen.inhaber  
108	B3:4.0a	Oberflächenwasser: Gammaspektrometrie, Aktivitätskonz. einz. Nuklide; Brennelementfabrik, Störfall/Unfall, Gen.inhaber  
109	B3:4.0b	Oberflächenwasser: Gesamt-Alpha-Aktivitätskonzentration, Brennelementfabrik, Störfall/Unfall, Gen.inhaber               
110	B4:1.1	Luft/äußere Strahlung: Brennelementfabrik, Störfall/Unfall, unabh. Messstelle 
111	B4:1.1a	Luft/äußere Strahlung: Gamma-Ortsdosisleistung; Brennelementfabrik, Störfall/Unfall, unabh. Messstelle                  
112	B4:1.1b	Luft/äußere Strahlung: Gamma-Ortsdosis; Brennelementfabrik, Störfall/Unfall, unabh. Messstelle                          
113	B4:1.2	Luft/Aerosole: Brennelementfabrik, Störfall/Unfall, unabh. Messstelle
114	B4:1.2a	Luft/Aerosole: Gammaspektrometrie, Aktivitätskonz. einz. Nuklide; Brennelementfabrik, Störfall/Unfall, unabh. Messstelle
115	B4:1.2b	Luft/Aerosole: alphanuklidspez. Messung, Aktivitätskonz. einz. Nuklide; Brennelementfabrik, Störfall/Unfall, unabh. MST 
116	B4:1.3	Luft/gasförmiges Iod: Brennelementfabrik, Störfall/Unfall, unabh. Messstelle                                            
117	B4:2.0	Bodenoberfläche: Brennelementfabrik, Störfall/Unfall, unabh. Messstelle
118	B4:2.0a	Bodenoberfläche: Gesamt-Alpha-Kontaminationsmessung auf vorber. Flächen; Brennelementfabrik, Störfall/Unfall, unabh. MST
119	B4:2.0b	Bodenoberfläche: alphanuklidspez. Messung,spezif. Aktivität einz. Nuklide; Brennelementfabrik, Störfall/Unf., unabh. MST
120	B4:3.0	Weide-/Wiesenbewuchs: Brennelementfabrik, Störfall/Unfall, unabh. Messstelle  
121	B4:3.0a	Weide-/Wiesenbewuchs: Gammaspektrometrie, spezif. I-131-Aktivität; Brennelementfabrik, Störfall/Unfall, unabh. MST      
122	B4:3.0b	Weide-/Wiesenbewuchs: alphanuklidspez. Messung, Aktivität einz. Nuklide; Brennelementfabrik, Störfall/Unfall, unabh. MST
123	B4:4.0	Oberflächenwasser: Brennelementfabrik, Störfall/Unfall, unabh. Messstelle  
124	B4:4.0a	Oberflächenwasser: Gammaspektrometrie, Aktivitätskonz. einz. Nuklide; Brennelementfabrik, Störfall/Unfall, unabh. MST   
125	B4:4.0b	Oberflächenwasser: Gesamt-Alpha-Aktivitätskonzentration, Brennelementfabrik, Störfall/Unfall, unabh. Messstelle         
126	B4:5.0	Boden: Brennelementfabrik, Störfall/Unfall, unabh. Messstelle
127	C1.1:1.1	Luft/Gamma-Strahlung: Gamma-Ortsdosisleistung; Brennelementzwischenlager, best.gem. Betrieb, Gen.inhaber                
128	C1.1:1.2	Luft/Gamma-Strahlung: Gamma-Ortsdosis; Brennelementzwischenlager, best.gem. Betrieb, Gen.inhaber                        
129	C1.1:1.3	Luft/Neutronenstrahlung: Neutronen-Ortsdosisleistung; Brennelementzwischenlager, best.gem. Betrieb, Gen.inhaber         
130	C1.1:1.4	Luft/Neutronenstrahlung: Neutronen-Ortsdosis; Brennelementzwischenlager, best.gem. Betrieb, Gen.inhaber                 
131	C1.1:1.5	Luft/Aerosole: Brennelementzwischenlager, best.gem. Betrieb, Gen.inhaber
132	C1.1:2.1	Abwasser: Brennelementzwischenlager, best.gem. Betrieb, Gen.inhaber
133	C1.1:2.2	Klärschlamm: Brennelementzwischenlager, best.gem. Betrieb, Gen.inhaber
134	C1.2:1.1	Luft/Gamma-Strahlung: Gamma-Ortsdosis; Brennelementzwischenlager, best.gem. Betrieb, unabh. Messstelle                  
135	C1.2:1.2	Luft/Neutronenstrahlung: Neutronen-Ortsdosis; Brennelementzwischenlager, best.gem. Betrieb, unabh. Messstelle           
136	C1.2:1.3	Luft/Aerosole: Brennelementzwischenlager, best.gem. Betrieb, unabh. Messstelle
137	C1.2:2.0	Boden: Brennelementzwischenlager, best.gem. Betrieb, unabh. Messstelle
138	C1.2:3.0	Weide-/Wiesenbewuchs: Brennelementzwischenlager, best.gem. Betrieb, unabh. Messstelle
139	C1.2:4.0	Oberflächenwasser: Brennelementzwischenlager, best.gem. Betrieb, unabh. Messstelle
140	C1.2:4.0a	Oberflächenwasser: Gammaspektr.,Aktivitätskonz. einzelner Nuklide, Brennelementzwischenl., best.gem. Betrieb, unabh. MST
141	C1.2:4.0b	Oberflächenwasser: Tritium-Aktivitätskonzentration, Brennelementzwischenlager, best.gem. Betrieb, unabh. Messstelle
142	C1.2:4.0c	Oberflächenwasser: I131-Aktivitätskonzentration, Brennelementzwischenlager, best.gem. Betrieb, unabh. Messstelle
143	C1.2:5.0	Sediment: Brennelementzwischenlager, best.gem. Betrieb, unabh. Messstelle
144	C1.3:1.1	Luft/äußere Strahlung: Brennelementzwischenlager, Störfall/Unfall, Gen.inhaber                                          
145	C1.3:1.1a	Luft/äußere Strahlung: Gamma-Ortsdosisleistung; Brennelementzwischenlager, Störfall/Unfall, Gen.inhaber                 
146	C1.3:1.1b	Luft/äußere Strahlung: Gamma-Ortsdosis; Brennelementzwischenlager, Störfall/Unfall, Gen.inhaber                         
147	C1.3:1.2	Luft/Aerosole: Brennelementzwischenlager, Störfall/Unfall, Gen.inhaber                                                  
148	C1.3:2.1	Bewuchs: Brennelementzwischenlager, Störfall/Unfall, Gen.inhaber                                                        
149	C1.3:2.2	Boden: Brennelementzwischenlager, Störfall/Unfall, Gen.inhaber
150	C1.4:1.1	Luft/äußere Strahlung: Brennelementzwischenlager, Störfall/Unfall, unabh. Messstelle 
151	C1.4:1.1a	Luft/äußere Strahlung: Gamma-Ortsdosisleistung; Brennelementzwischenlager, Störfall/Unfall, unabh. Messstelle           
152	C1.4:1.1b	Luft/äußere Strahlung: Gamma-Ortsdosis; Brennelementzwischenlager, Störfall/Unfall, unabh. Messstelle                   
153	C1.4:1.1c	Luft/äußere Strahlung: Neutronen-Ortsdosisleistung; Brennelementzwischenlager; Störfall/Unfall, unabh. Messstelle
154	C1.4:1.2	Luft/Aerosole: Brennelementzwischenlager, Störfall/Unfall, unabh. Messstelle                                            
155	C1.4:2.1	Bodenoberfläche: Brennelementzwischenlager, Störfall/Unfall,  unabh. Messstelle                                         
156	C1.4:3.1	Bewuchs: Brennelementzwischenlager, Störfall/Unfall, unabh. Messstelle                                                  
157	C1.4:4.1	Sediment: Brennelementzwischenlager, Störfall/Unfall, unabh. Messstelle                                                 
158	C2.1:1.1	Luft/Gamma-Strahlung: Gamma-Ortsdosis; Endlager, best.gem. Betrieb, Gen.inhaber                                         
159	C2.1:1.2	Luft/Neutronenstrahlung: Neutronen-Ortsdosis; Endlager, best.gem. Betrieb, Gen.inhaber                                  
160	C2.1:1.3	Luft/Aerosole: Endlager, best.gem. Betrieb, Gen.inhaber                                                                 
161	C2.1:1.3a	Luft/Aerosole: Gammaspektrometrie, Aktivitätskonzentration einzelner Nuklide; Endlager, best.gem. Betrieb, Gen.inhaber  
162	C2.1:1.3b	Luft/Aerosole: Gesamt-Alpha-Aktivitätskonzentration; Endlager, best.gem. Betrieb, Gen.inhaber                           
163	C2.1:2.0	Niederschlag: Endlager, best.gem. Betrieb, Gen.inhaber                                                                  
164	C2.1:3.0	Boden: Endlager, best.gem. Betrieb, Gen.inhaber                                                                         
165	C2.1:3.1	Bodenoberfläche: Endlager, Sondermessungen, Gen.inhaber 
166	C2.1:4.0	Gras: Endlager, best.gem. Betrieb, Gen.inhaber                                                                          
167	C2.1:5.0	Oberflächenwasser: Endlager, best.gem. Betrieb, Gen.inhaber                                                             
168	C2.1:5.0a	Oberflächenwasser: Tritium-Aktivitätskonzentration; Endlager, best.gem. Betrieb, Gen.inhaber                            
169	C2.1:5.0b	Oberflächenwasser: Gammaspektrometrie, Aktivitätskonzentr. einzelner Nuklide; Endlager, best.gem. Betrieb, Gen.inhaber  
170	C2.2:1.1	Luft/Gamma-Strahlung: Gamma-Ortsdosis; Endlager, best.gem. Betrieb, unabh. Messstelle                                   
171	C2.2:1.2	Luft/Neutronenstrahlung: Neutronen-Ortsdosis; Endlager, best.gem. Betrieb,  unabh. Messstelle                           
172	C2.2:1.3	Luft/Aerosole: Endlager, best.gem. Betrieb, unabh. Messstelle                                                           
173	C2.2:1.3a	Luft/Aerosole: Gammaspektrometrie, Aktivitätskonzentr. einzelner Nuklide; Endlager, best.gem. Betrieb, unabh. Messstelle
174	C2.2:1.3b	Luft/Aerosole: alphanuklidspezif. Messung, Aktivitätskonz. einz. Nuklide; Endlager, best.gem. Betrieb, unabh. Messstelle
175	C2.2:2.0	Boden: Endlager, best.gem. Betrieb, unabh. Messstelle                                                                   
176	C2.2:3.0	Weide-/Wiesenbewuchs: Endlager, best.gem. Betrieb, unabh. Messstelle                                                    
177	C2.2:3.0a	Weide-/Wiesenbewuchs: spezifische Tritium-Aktivität; Endlager, best.gem. Betrieb, unabh. Messstelle                     
178	C2.2:3.0b	Weide-/Wiesenbewuchs: spezifische C-14-Aktivität; Endlager, best.gem. Betrieb, unabh. Messstelle                        
179	C2.2:3.0c	Weide-/Wiesenbewuchs: Gammaspektrometrie, spez. Aktivität einz. Nuklide; Endlager, best.gem. Betrieb, unabh. Messstelle 
180	C2.2:4.0	Nahrungsmittel pflanzl. Herkunft: Endlager, best.gem. Betrieb, unabh. Messstelle                                        
181	C2.2:4.0a	Nahrungsmittel pflanzl. Herkunft: Gammaspektr., spez. Aktivität einz. Nuklide; Endlager, best.gem. Betrieb, unabh. MST  
182	C2.2:4.0b	Nahrungsmittel pflanzlicher Herkunft: spezif. Sr-90-Aktivität; Endlager, best.gem. Betrieb, unabh. Messstelle           
183	C2.2:5.0	Kuhmilch: Endlager, best.gem. Betrieb, unabh. Messstelle                                                                
184	C2.2:6.1	Sediment: Endlager, best.gem. Betrieb, unabh. Messstelle                                                                
185	C2.2:6.2	Grundwasser: Endlager, best.gem. Betrieb, unabh. Messstelle                                                             
186	C2.3:1.1	Luft/äußere Strahlung: Endlager, Störfall/Unfall, Gen.inhaber 
187	C2.3:1.1a	Luft/äußere Strahlung: Gamma-Ortsdosisleistung; Endlager, Störfall/Unfall, Gen.inhaber                                  
188	C2.3:1.1b	Luft/äußere Strahlung: Gamma-Ortsdosis; Endlager, Störfall/Unfall, Gen.inhaber                                          
189	C2.3:1.2	Luft/Aerosole: Endlager, Störfall/Unfall, Gen.inhaber 
190	C2.3:1.2a	Luft/Aerosole: Gammaspektrometrie, Aktivitätskonzentration einzelner Nuklide; Endlager, Störfall/Unfall, Gen.inhaber    
191	C2.3:1.2b	Luft/Aerosole: Gesamt-Alpha-Aktivitätskonzentration; Endlager, Störfall/Unfall, Gen.inhaber                             
192	C2.3:1.2c	Luft/Aerosole: Gesamt-Beta-Aktivitätskonzentration; Endlager, Störfall/Unfall, Gen.inhaber                              
193	C2.3:1.3	Luft/I-129: Endlager, Störfall/Unfall, Gen.inhaber                                                                      
194	C2.3:2.0	Bodenoberfläche: Endlager, Störfall/Unfall, Gen.inhaber 
195	C2.3:2.0a	Bodenoberfläche: Kontaminationsmessung durch In-situ-Gammaspektrometrie; Endlager, Störfall/Unfall, Gen.inhaber         
196	C2.3:2.0b	Bodenoberfläche: Gamma-Alpha-Kontaminationsmessung auf vorbereiteten Flächen; Endlager, Störfall/Unfall, Gen.inhaber    
197	C2.3:2.0c	Bodenoberfläche: Gesamt-Beta-Kontaminationsmessung auf vorbereiteten Flächen; Endlager, Störfall/Unfall, Gen.inhaber    
198	C2.3:3.0	Weide-/Wiesenbewuchs: Endlager, Störfall/Unfall, Gen.inhaber 
199	C2.3:3.0a	Weide-/Wiesenbewuchs: Gammaspektrometrie, spez. Aktivität einz. Nuklide; Endlager, Störfall/Unfall, Gen.inhaber         
200	C2.3:3.0b	Weide-/Wiesenbewuchs: spezifische Gesamt-Alpha-Aktivität; Endlager, Störfall/Unfall, Gen.inhaber                        
201	C2.4:1.1	Luft/äußere Strahlung: Endlager, Störfall/Unfall, unabh. Messstelle  
202	C2.4:1.1a	Luft/äußere Strahlung: Gamma-Ortsdosisleistung; Endlager, Störfall/Unfall, unabh. Messstelle                            
203	C2.4:1.1b	Luft/äußere Strahlung: Gamma-Ortsdosis; Endlager, Störfall/Unfall, unabh. Messstelle                                    
204	C2.4:1.2	Luft/Aerosole: Endlager, Störfall/Unfall, unabh. Messstelle
205	C2.4:1.2a	Luft/Aerosole: Gammaspektrometrie, Aktivitätskonzentration einz. Nuklide; Endlager, Störfall/Unfall, unabh. Messstelle  
206	C2.4:1.2b	Luft/Aerosole: Gesamt-Alpha-Aktivitätskonzentration; Endlager, Störfall/Unfall, unabh. Messstelle                       
207	C2.4:1.2c	Luft/Aerosole: Gesamt-Beta-Aktivitätskonzentration; Endlager, Störfall/Unfall, unabh. Messstelle                        
208	C2.4:1.3	Luft/I-129: Endlager, Störfall/Unfall, unabh. Messstelle                                                                
209	C2.4:2.0	Bodenoberfläche: Endlager, Störfall/Unfall, unabh. Messstelle                                                           
210	C2.4:3.0	Weide-/Wiesenbewuchs: Endlager, Störfall/Unfall, unabh. Messstelle
211	C2.4:3.0a	Weide-/Wiesenbewuchs: Gammaspektrometrie, spez. Aktivität einzeln. Nuklide; Endlager, Störfall/Unfall, unabh. Messstelle
212	C2.4:3.0b	Weide-/Wiesenbewuchs: spezifische Gesamt-Alpha-Aktivität; Endlager, Störfall/Unfall, unabh. Messstelle                  
213	C2.4:3.0c	Weide-/Wiesenbewuchs: Tritium-Aktivitätskonzentration; Endlager, Störfall/Unfall, unabh. Messstelle                     
214	C2.4:4.0	Kuhmilch: Endlager, Störfall/Unfall, unabh. Messstelle  
215	C2.4:4.0a	Kuhmilch: Gammaspektrometrie, Aktivitätskonzentration einzelner Nuklide; Endlager, Störfall/Unfall, unabh. Messstelle   
216	C2.4:4.0b	Kuhmilch: Sr-90-Aktivitätskonzentration; Endlager, Störfall/Unfall, unabh. Messstelle                                   
217	C2.4:4.0c	Kuhmilch: I-129-Aktivitätskonzentration; Endlager, Störfall/Unfall, unabh. Messstelle                                   
218	C2.4:5.0	Oberflächenwasser: Endlager, Störfall/Unfall, unabh. Messstelle                                                         
219	C2.4:6.0	Trinkwasser: Endlager, Störfall/Unfall, unabh. Messstelle                                                               
220	C2.5:2.0	Boden: Genehmigungsinhaber, Sondermessprogramm
221	C2.5:3.0	Bewuchs:; Genehmigungsinhaber, Sondermessprogramm
222	C2.5:4.0	Nahrungsmittel pflanzl. Herkunft: Genehmigungsinhaber, Sondermessprogramm
223	C2.5:5.0	Milch: Genehmigungsinhaber, Sondermessprogramm
224	C2.5:6.0	Wasser: Genehmigungsinhaber, Sondermessprogramm
225	D1:1.1	Luft/äußere Strahlung: Sonderfälle, best.gem. Betrieb, Gen.inhaber 
226	D1:1.1a	Luft/äußere Strahlung: Gamma-Ortsdosisleistung; Sonderfälle, best.gem. Betrieb, Gen.inhaber                             
227	D1:1.1b	Luft/äußere Strahlung: Gamma-Ortsdosis; Sonderfälle, best.gem. Betrieb, Gen.inhaber                                     
228	D1:1.1c	Luft/äußere Strahlung: Gamma-Ortsdosis in der Umgebung; Sonderfälle, best.gem. Betrieb, Gen.inhaber   
229	D1:1.2	Luft/Aerosole: Sonderfälle, best.gem. Betrieb, Gen.inhaber                                                              
230	D1:1.3	Luft/gasförmiges Iod: Sonderfälle, best.gem. Betrieb, Gen.inhaber                                                       
231	D1:2.0	Niederschlag: Sonderfälle, best.gem. Betrieb, Gen.inhaber                                                               
232	D1:3.0	Boden: Sonderfälle, best.gem. Betrieb, Gen.inhaber                                                                      
233	D1:3.1	Bodenoberfläche: Sonderfälle, best.gem. Betrieb, Gen.inhaber
234	D1:4.0	Pflanzen/Bewuchs: Sonderfälle, best.gem. Betrieb, Gen.inhaber                                                           
235	D1:5.0	Oberflächenwasser: Sonderfälle, best.gem. Betr., Gen.inhaber 
236	D1:5.0a	Oberflächenwasser: Gammaspektrometrie, Aktivitätskonzentr. einzelner Nuklide; Sonderfälle, best.gem. Betr., Gen.inhaber 
237	D1:5.0b	Oberflächenwasser: Tritium-Aktivitätskonzentration; Sonderfälle, best.gem. Betrieb, Gen.inhaber                         
238	D1:6.0	Grundwasser: Sonderfälle, best.gem. Betrieb, Gen.inhaber 
239	D1:6.0a	Grundwasser: Gammaspektrometrie, Aktivitätskonzentration einzelner Nuklide; Sonderfälle, best.gem. Betrieb, Gen.inhaber 
240	D1:6.0b	Grundwasser: Tritium-Aktivitätskonzentration; Sonderfälle, best.gem. Betrieb, Gen.inhaber                               
241	D1:7.2	Sediment: Sonderfälle, best.gem. Betrieb, Gen.inhaber 
242	D1:8.0	Nahrungsmittel pflanzl. Herkunft:Sonderfälle, best.gem. Betrieb, Gen.inhaber 
243	D2:1.1	Luft/äußere Strahlung: Gamma-Ortsdosis; Sonderfälle, best.gem. Betrieb, unabh. Messstelle                               
244	D2:1.1b	Luft/äußere Strahlung: Gamma-Ortsdosis am Anlagenzaun; Sonderfälle, best.gem. Betrieb, unabh. Messstelle
245	D2:1.1c	Luft/äußere Strahlung: Gamma-Ortsdosis in der Umgebung; Sonderfälle, best.gem. Betrieb, unabh. Messstelle
246	D2:1.2	Luft/Aerosole: Sonderfälle, best.gem. Betrieb, unabh. Messstelle                                                        
247	D2:2.0	Niederschlag: Sonderfälle, best.gem. Betrieb, unabh. Messstelle                                                         
248	D2:3.0	Boden: Sonderfälle, best.gem. Betrieb, unabh. Messstelle                                                                
249	D2:4.0	Weide-/Wiesenbewuchs: Sonderfälle, best.gem. Betrieb, unabh. Messstelle                                                 
250	D2:5.0	Nahrungsmittel pflanzlicher Herkunft: Sonderfälle, best.gem. Betrieb, unabh. Messstelle
251	D2:5.0a	Nahrungsmittel pflanzl. Herkunft: Gammaspektr., Aktivitätskonz. einz. Nuklide; Sonderfälle, best.gem. Betr., unabh. MST 
252	D2:5.0b	Nahrungsmittel pflanzlicher Herkunft: spezifische Sr-90-Aktivität; Sonderfälle, best.gem. Betrieb, unabh. Messstelle    
253	D2:6.0	Kuhmilch: Sonderfälle, best.gem. Betrieb, unabh. Messstelle 
254	D2:6.0a	Kuhmilch: Gammaspektrometrie, Aktivitätskonzentration einz. Nuklide; Sonderfälle, best.gem. Betrieb, unabh. Messstelle  
255	D2:6.0b	Kuhmilch: Sr-90-Aktivitätskonzentration; Sonderfälle, best.gem. Betrieb, unabh. Messstelle                              
256	D2:6.0c	Kuhmilch: I-131-Aktivitätskonzentration; Sonderfälle, best.gem. Betrieb, unabh. Messstelle                              
257	D2:7.1	Oberflächenwasser: Sonderfälle, best.gem. Betrieb, unabh. Messstelle
258	D2:7.1a	Oberflächenwasser: Gammaspektrometrie, Aktivitätskonzentration einz. Nuklide; Sonderfälle, best.gem. Betrieb, unabh. MST
259	D2:7.1b	Oberflächenwasser: Tritium-Aktivitätskonzentration; Sonderfälle, best.gem. Betrieb, unabh. Messstelle                   
260	D2:7.2	Sediment: Sonderfälle, best.gem. Betrieb, unabh. Messstelle                                                             
261	D2:8.0	Fisch: Sonderfälle, best.gem. Betrieb, unabh. Messstelle                                                                
262	D2:9.0	Trinkwasser: Sonderfälle, best.gem. Betrieb, unabh. Messstelle   
263	D2:9.0a	Trinkwasser (Brunnen): Sonderfälle, best.gem. Betrieb, unabh. Messstelle                                                
264	D2:9.0b	Trinkwasser: Sr-90-Aktivitätskonzentration; Sonderfälle, best.gem. Betrieb, unabh. Messstelle                           
265	D2:9.0c	Trinkwasser (Wasserwerk): Sonderfälle, best.gem. Betrieb, unabh. Messstelle                                             
266	D2:9.0d	Trinkwasser: Tritium-Aktivitätskonzentration; Sonderfälle, best.gem. Betrieb, unabh. Messstelle                         
268	D3:1.1a	Luft/äußere Strahlung: Gamma-Ortsdosisleistung; Sonderfälle, Störfall/Unfall, Gen.inhaber                               
269	D3:1.1b	Luft/äußere Strahlung: Gamma-Ortsdosis; Sonderfälle, Störfall/Unfall, Gen.inhaber                                       
270	D3:1.2	Luft/Aerosole: Sonderfälle, Störfall/Unfall, Gen.inhaber                                                                
271	D3:1.3	Luft/gasförmiges Iod: Sonderfälle, Störfall/Unfall, Gen.inhaber                                                         
272	D3:2.1	Bodenoberfläche: Sonderfälle, Störfall/Unfall, Gen.inhaber                                                              
273	D3:2.1b	Bodenoberfläche: Beta-Handmessungen; Sonderfälle, Störfall/Unfall, Gen.inhaber
274	D3:2.2	Boden: Sonderfälle, Störfall/Unfall, Gen.inhaber                                                                        
275	D3:3.0	Weide-/Wiesenbewuchs: Sonderfälle, Störfall/Unfall, Gen.inhaber                                                         
276	D3:4.0	Oberflächenwasser: Sonderfälle, Störfall/Unfall, Gen.inhaber                                                            
277	D4:1.1	Luft/äußere Strahlung: Sonderfälle, Störfall/Unfall, unabh. Messstelle 
278	D4:1.1a	Luft/äußere Strahlung: Gamma-Ortsdosisleistung; Sonderfälle, Störfall/Unfall, unabh. Messstelle                         
279	D4:1.1b	Luft/äußere Strahlung: Gamma-Ortsdosis; Sonderfälle, Störfall/Unfall, unabh. Messstelle                                 
280	D4:1.2	Luft/Aerosole: Sonderfälle, Störfall/Unfall, unabh. Messstelle                                                          
281	D4:1.3	Luft/gasförmiges Iod: Sonderfälle, Störfall/Unfall, unabh. Messstelle                                                   
282	D4:2.1	Bodenoberfläche: Sonderfälle, Störfall/Unfall, unabh. Messstelle                                                        
283	D4:2.2	Boden: Sonderfälle, Störfall/Unfall, unabh. Messstelle                                                                  
284	D4:3.0	Weide-/Wiesenbewuchs: Sonderfälle, Störfall/Unfall, unabh. Messstelle                                                   
285	D4:4.0	Kuhmilch: Sonderfälle, Störfall/Unfall, unabh. Messstelle                                                               
286	D4:5.1	Nahrungsmittel pflanzlicher Herkunft: Sonderfälle, Störfall/Unfall, unabh. Messstelle                                   
287	D4:5.2	Nahrungsmittel tierischer Herkunft: Sonderfälle, Störfall/Unfall, unabh. Messstelle                                     
288	D4:6.0	Oberflächenwasser: Sonderfälle, Störfall/Unfall, unabh. Messstelle                                                      
289	D4:7.0	Fisch: Sonderfälle, Störfall/Unfall, unabh. Messstelle                                                                  
290	D4:8.0	Trinkwasser: Sonderfälle, Störfall/Unfall, unabh. Messstelle                                                            
\.

SELECT pg_catalog.setval('rei_progpunkt_id_seq', (SELECT max(id) FROM rei_progpunkt), false);


COPY rei_progpunkt_gruppe (id, rei_prog_punkt_gruppe, beschreibung) FROM stdin;
1	D1:1.3	Luft/gasförmiges Iod: Sonderfälle, best.gem. Betrieb, Gen.inhaber
2	D1:2.0	Niederschlag: Sonderfälle, best.gem. Betrieb, Gen.inhaber
3	D1:3.0	Boden: Sonderfälle, best.gem. Betrieb, Gen.inhaber
4	D1:4.0	Pflanzen/Bewuchs: Sonderfälle, best.gem. Betrieb, Gen.inhaber
5	D1:6.0a	Grundwasser: Gammaspektrometrie, Aktivitätskonzentration einzelner Nuklide; Sonderfälle, best.gem. Betrieb, Gen.inhaber
6	D1:6.0b	Grundwasser: Tritium-Aktivitätskonzentration; Sonderfälle, best.gem. Betrieb, Gen.inhaber
7	D2:1.1	Luft/äußere Strahlung: Gamma-Ortsdosis; Sonderfälle, best.gem. Betrieb, unabh. Messstelle
8	D2:1.2	Luft/Aerosole: Sonderfälle, best.gem. Betrieb, unabh. Messstelle
9	D2:2.0	Niederschlag: Sonderfälle, best.gem. Betrieb, unabh. Messstelle
10	D2:3.0	Boden: Sonderfälle, best.gem. Betrieb, unabh. Messstelle
11	D2:4.0	Weide-/Wiesenbewuchs: Sonderfälle, best.gem. Betrieb, unabh. Messstelle
12	D2:5.0a	Nahrungsmittel pflanzl. Herkunft: Gammaspektr., Aktivitätskonz. einz. Nuklide; Sonderfälle, best.gem. Betr., unabh. MST
13	D2:5.0b	Nahrungsmittel pflanzlicher Herkunft: spezifische Sr-90-Aktivität; Sonderfälle, best.gem. Betrieb, unabh. Messstelle
14	D2:6.0a	Kuhmilch: Gammaspektrometrie, Aktivitätskonzentration einz. Nuklide; Sonderfälle, best.gem. Betrieb, unabh. Messstelle
15	D2:6.0b	Kuhmilch: Sr-90-Aktivitätskonzentration; Sonderfälle, best.gem. Betrieb, unabh. Messstelle
16	D2:6.0c	Kuhmilch: I-131-Aktivitätskonzentration; Sonderfälle, best.gem. Betrieb, unabh. Messstelle
17	D2:7.1a	Oberflächenwasser: Gammaspektrometrie, Aktivitätskonzentration einz. Nuklide; Sonderfälle, best.gem. Betrieb, unabh. MST
18	D2:7.1b	Oberflächenwasser: Tritium-Aktivitätskonzentration; Sonderfälle, best.gem. Betrieb, unabh. Messstelle
19	D2:7.2	Sediment: Sonderfälle, best.gem. Betrieb, unabh. Messstelle
20	D2:8.0	Fisch: Sonderfälle, best.gem. Betrieb, unabh. Messstelle
21	D2:9.0a	Trinkwasser (Brunnen): Sonderfälle, best.gem. Betrieb, unabh. Messstelle
22	D2:9.0b	Trinkwasser: Sr-90-Aktivitätskonzentration; Sonderfälle, best.gem. Betrieb, unabh. Messstelle
23	D2:9.0c	Trinkwasser (Wasserwerk): Sonderfälle, best.gem. Betrieb, unabh. Messstelle
24	D2:9.0d	Trinkwasser: Tritium-Aktivitätskonzentration; Sonderfälle, best.gem. Betrieb, unabh. Messstelle
25	D3:1.1a	Luft/äußere Strahlung: Gamma-Ortsdosisleistung; Sonderfälle, Störfall/Unfall, Gen.inhaber
26	D3:1.1b	Luft/äußere Strahlung: Gamma-Ortsdosis; Sonderfälle, Störfall/Unfall, Gen.inhaber
27	D3:1.2	Luft/Aerosole: Sonderfälle, Störfall/Unfall, Gen.inhaber
28	D3:1.3	Luft/gasförmiges Iod: Sonderfälle, Störfall/Unfall, Gen.inhaber
29	D3:2.1	Bodenoberfläche: Sonderfälle, Störfall/Unfall, Gen.inhaber
30	D3:2.2	Boden: Sonderfälle, Störfall/Unfall, Gen.inhaber
31	D3:3.0	Weide-/Wiesenbewuchs: Sonderfälle, Störfall/Unfall, Gen.inhaber
32	D3:4.0	Oberflächenwasser: Sonderfälle, Störfall/Unfall, Gen.inhaber
33	A1:1.1	Luft/äußere Strahlung: KKW, best.gem. Betrieb, Gen.inhaber
34	A1:5.0	Oberflächenwasser: KKW, best.gem. Betrieb, Gen.inhaber
35	A1:6.0	Grundwasser: KKW, best.gem. Betrieb, Gen.inhaber
36	A2:5.0	Nahrungsmittel pflanzl. Herkunft: KKW, best.gem. Betrieb, unabh. Messstelle
37	A2:6.0	Kuhmilch: KKW, best.gem. Betrieb, unabh. Messstelle
38	A2:7.1	Oberflächenwasser: KKW, best.gem. Betrieb, unabh. Messstelle
39	A2:9.0	Trinkwasser: KKW, best.gem. Betrieb, unabh. Messstelle
40	C1.3:1.1	Luft/äußere Strahlung: Brennelementzwischenlager, Störfall/Unfall, Gen.inhaber
41	C2.2:1.3	Luft/Aerosole: Endlager, best.gem. Betrieb, unabh. Messstelle
42	C2.1:1.3	Luft/Aerosole: Endlager, best.gem. Betrieb, Gen.inhaber
43	C2.1:5.0	Oberflächenwasser: Endlager, best.gem. Betrieb, Gen.inhaber
44	D4:1.1a	Luft/äußere Strahlung: Gamma-Ortsdosisleistung; Sonderfälle, Störfall/Unfall, unabh. Messstelle
45	D4:1.1b	Luft/äußere Strahlung: Gamma-Ortsdosis; Sonderfälle, Störfall/Unfall, unabh. Messstelle
46	D4:1.2	Luft/Aerosole: Sonderfälle, Störfall/Unfall, unabh. Messstelle
47	D4:1.3	Luft/gasförmiges Iod: Sonderfälle, Störfall/Unfall, unabh. Messstelle
48	D4:2.1	Bodenoberfläche: Sonderfälle, Störfall/Unfall, unabh. Messstelle
49	D4:2.2	Boden: Sonderfälle, Störfall/Unfall, unabh. Messstelle
50	D4:3.0	Weide-/Wiesenbewuchs: Sonderfälle, Störfall/Unfall, unabh. Messstelle
51	D4:4.0	Kuhmilch: Sonderfälle, Störfall/Unfall, unabh. Messstelle
52	D4:5.1	Nahrungsmittel pflanzlicher Herkunft: Sonderfälle, Störfall/Unfall, unabh. Messstelle
53	D4:5.2	Nahrungsmittel tierischer Herkunft: Sonderfälle, Störfall/Unfall, unabh. Messstelle
54	D4:6.0	Oberflächenwasser: Sonderfälle, Störfall/Unfall, unabh. Messstelle
55	D4:7.0	Fisch: Sonderfälle, Störfall/Unfall, unabh. Messstelle
56	D4:8.0	Trinkwasser: Sonderfälle, Störfall/Unfall, unabh. Messstelle
57	B1:1.1	Luft/äußere Strahlung: Gamma-Ortsdosis; Brennelementfabrik, best.gem, Betrieb, Gen.inhaber
58	B1:1.3	Luft/Aerosole: Brennelementfabrik, best.gem. Betrieb, Gen.inhaber
59	B1:2.0	Niederschlag: Brennelementfabrik, best.gem. Betrieb, Gen.inhaber
60	B1:3.0	Grundwasser: Brennelementfabrik, best.gem. Betrieb, Gen.inhaber
61	B2:1.1	Luft/äußere Strahlung: Gamma-Ortsdosis; Brennelementfabrik, best.gem. Betrieb, unabh. Messstelle
62	B2:1.3a	Luft/Aerosole: Gesamt-Alpha-Aktivitätskonzentration; Brennelementfabrik, best.gem. Betrieb, unabh. Messstelle
63	B2:1.3b	Luft/Aerosole: alphanuklidspez. Messung, Aktivitätskonz. einz. Nuklide; Brennelementfabrik, best.gem. Betr.,unabh. MST
64	B2:2.0	Niederschlag: Brennelementfabrik, best.gem. Betrieb, unabh. Messstelle
65	B2:3.0	Weide-/Wiesenbewuchs: Brennelementfabrik, best.gem. Betrieb, unabh. Messstelle
66	B2:4.1	Oberflächenwasser: Brennelementfabrik, best.gem. Betrieb, unabh. Messstelle
67	B2:4.2	Klärschlamm: Brennelementfabrik, best.gem. Betrieb, unabh. Messstelle
68	B2:5.0	Grundwasser: Brennelementfabrik, best.gem. Betrieb, unabh. Messstelle
69	B3:1.1a	Luft/äußere Strahlung: Gamma-Ortsdosisleistung; Brennelementfabrik, Störfall/Unfall, Gen.inhaber
70	B3:1.1b	Luft/äußere Strahlung: Gamma-Ortsdosis; Brennelementfabrik, Störfall/Unfall, Gen.inhaber
71	B3:1.3	Luft/gasförmiges Iod: Brennelementfabrik, Störfall/Unfall, Gen.inhaber
72	B3:3.0a	Weide-/Wiesenbewuchs: Gammaspektrometrie, spezifische I-131-Aktivität; Brennelementfabrik, Störfall/Unfall, Gen.inhaber
73	B3:3.0b	Weide-/Wiesenbewuchs: alphanuklidspez. Messung, spezif. Aktivität einz. Nuklide; Brennelementfabrik, Störfall/Unf., Gen.
74	B3:4.0a	Oberflächenwasser: Gammaspektrometrie, Aktivitätskonz. einz. Nuklide; Brennelementfabrik, Störfall/Unfall, Gen.inhaber
75	B3:4.0b	Oberflächenwasser: Gesamt-Alpha-Aktivitätskonzentration, Brennelementfabrik, Störfall/Unfall, Gen.inhaber
76	B4:1.1a	Luft/äußere Strahlung: Gamma-Ortsdosisleistung; Brennelementfabrik, Störfall/Unfall, unabh. Messstelle
77	B4:1.1b	Luft/äußere Strahlung: Gamma-Ortsdosis; Brennelementfabrik, Störfall/Unfall, unabh. Messstelle
78	B4:1.3	Luft/gasförmiges Iod: Brennelementfabrik, Störfall/Unfall, unabh. Messstelle
79	B4:2.0a	Bodenoberfläche: Gesamt-Alpha-Kontaminationsmessung auf vorber. Flächen; Brennelementfabrik, Störfall/Unfall, unabh. MST
80	B4:2.0b	Bodenoberfläche: alphanuklidspez. Messung,spezif. Aktivität einz. Nuklide; Brennelementfabrik, Störfall/Unf., unabh. MST
81	B4:3.0a	Weide-/Wiesenbewuchs: Gammaspektrometrie, spezif. I-131-Aktivität; Brennelementfabrik, Störfall/Unfall, unabh. MST
82	B4:3.0b	Weide-/Wiesenbewuchs: alphanuklidspez. Messung, Aktivität einz. Nuklide; Brennelementfabrik, Störfall/Unfall, unabh. MST
83	B4:4.0a	Oberflächenwasser: Gammaspektrometrie, Aktivitätskonz. einz. Nuklide; Brennelementfabrik, Störfall/Unfall, unabh. MST
84	B4:4.0b	Oberflächenwasser: Gesamt-Alpha-Aktivitätskonzentration, Brennelementfabrik, Störfall/Unfall, unabh. Messstelle
85	C1.1:1.1	Luft/Gamma-Strahlung: Gamma-Ortsdosisleistung; Brennelementzwischenlager, best.gem. Betrieb, Gen.inhaber
86	C1.1:1.2	Luft/Gamma-Strahlung: Gamma-Ortsdosis; Brennelementzwischenlager, best.gem. Betrieb, Gen.inhaber
87	C1.4:1.1b	Luft/äußere Strahlung: Gamma-Ortsdosis; Brennelementzwischenlager, Störfall/Unfall, unabh. Messstelle
88	C2.1:5.0a	Oberflächenwasser: Tritium-Aktivitätskonzentration; Endlager, best.gem. Betrieb, Gen.inhaber
89	C1.4:4.1	Sediment: Brennelementzwischenlager, Störfall/Unfall, unabh. Messstelle
90	C2.1:1.1	Luft/Gamma-Strahlung: Gamma-Ortsdosis; Endlager, best.gem. Betrieb, Gen.inhaber
91	C2.1:3.0	Boden: Endlager, best.gem. Betrieb, Gen.inhaber
92	C2.1:4.0	Gras: Endlager, best.gem. Betrieb, Gen.inhaber
93	C2.2:1.3b	Luft/Aerosole: alphanuklidspezif. Messung, Aktivitätskonz. einz. Nuklide; Endlager, best.gem. Betrieb, unabh. Messstelle
94	C2.2:1.1	Luft/Gamma-Strahlung: Gamma-Ortsdosis; Endlager, best.gem. Betrieb, unabh. Messstelle
95	C2.1:2.0	Niederschlag: Endlager, best.gem. Betrieb, Gen.inhaber
96	C2.4:1.1a	Luft/äußere Strahlung: Gamma-Ortsdosisleistung; Endlager, Störfall/Unfall, unabh. Messstelle
97	C2.2:3.0a	Weide-/Wiesenbewuchs: spezifische Tritium-Aktivität; Endlager, best.gem. Betrieb, unabh. Messstelle
98	C2.2:2.0	Boden: Endlager, best.gem. Betrieb, unabh. Messstelle
99	C2.2:4.0a	Nahrungsmittel pflanzl. Herkunft: Gammaspektr., spez. Aktivität einz. Nuklide; Endlager, best.gem. Betrieb, unabh. MST
100	C2.2:5.0	Kuhmilch: Endlager, best.gem. Betrieb, unabh. Messstelle
101	C2.2:6.1	Sediment: Endlager, best.gem. Betrieb, unabh. Messstelle
102	C2.2:6.2	Grundwasser: Endlager, best.gem. Betrieb, unabh. Messstelle
103	C2.3:1.2b	Luft/Aerosole: Gesamt-Alpha-Aktivitätskonzentration; Endlager, Störfall/Unfall, Gen.inhaber
104	C2.3:2.0b	Bodenoberfläche: Gamma-Alpha-Kontaminationsmessung auf vorbereiteten Flächen; Endlager, Störfall/Unfall, Gen.inhaber
105	A1:5.0a	Oberflächenwasser: Gammaspektrometrie, Aktivitätskonzentr. einzelner Nuklide; KKW, best.gem. Betrieb, Gen.inhaber
106	A1:5.0b	Oberflächenwasser: Tritium-Aktivitätskonzentration; KKW, best.gem. Betrieb, Gen.inhaber
107	B3:1.2a	Luft/Aerosole: Gammaspektrometrie, Aktivitätskonz. einzelner Nuklide; Brennelementfabrik, Störfall/Unfall, Gen.inhaber
108	B3:1.2b	Luft/Aerosole: alphanuklidspez. Messung,Aktivitätskonz. einz. Nuklide;  Brennelementfabrik, Störfall/Unfall, Gen.inhaber
109	B3:2.0a	Bodenoberfläche: Gesamt-Alpha-Kontaminationsmessung auf vorber. Flächen; Brennelementfabrik, Störfall/Unfall, Gen.inhab.
110	B3:2.0b	Bodenoberfläche: alphanuklidspez. Messung,Aktivitätskonz. einz. Nuklide; Brennelementfabrik, Störfall/Unfall, Gen.inhab.
111	B4:1.2a	Luft/Aerosole: Gammaspektrometrie, Aktivitätskonz. einz. Nuklide; Brennelementfabrik, Störfall/Unfall, unabh. Messstelle
112	B4:1.2b	Luft/Aerosole: alphanuklidspez. Messung, Aktivitätskonz. einz. Nuklide; Brennelementfabrik, Störfall/Unfall, unabh. MST
113	C1.3:1.1a	Luft/äußere Strahlung: Gamma-Ortsdosisleistung; Brennelementzwischenlager, Störfall/Unfall, Gen.inhaber
114	C1.3:1.1b	Luft/äußere Strahlung: Gamma-Ortsdosis; Brennelementzwischenlager, Störfall/Unfall, Gen.inhaber
115	C1.4:1.1a	Luft/äußere Strahlung: Gamma-Ortsdosisleistung; Brennelementzwischenlager, Störfall/Unfall, unabh. Messstelle
116	C2.1:5.0b	Oberflächenwasser: Gammaspektrometrie, Aktivitätskonzentr. einzelner Nuklide; Endlager, best.gem. Betrieb, Gen.inhaber
117	C2.2:1.3a	Luft/Aerosole: Gammaspektrometrie, Aktivitätskonzentr. einzelner Nuklide; Endlager, best.gem. Betrieb, unabh. Messstelle
118	C2.2:3.0b	Weide-/Wiesenbewuchs: spezifische C-14-Aktivität; Endlager, best.gem. Betrieb, unabh. Messstelle
119	C2.2:3.0c	Weide-/Wiesenbewuchs: Gammaspektrometrie, spez. Aktivität einz. Nuklide; Endlager, best.gem. Betrieb, unabh. Messstelle
120	C2.2:4.0b	Nahrungsmittel pflanzlicher Herkunft: spezif. Sr-90-Aktivität; Endlager, best.gem. Betrieb, unabh. Messstelle
121	C2.3:1.1a	Luft/äußere Strahlung: Gamma-Ortsdosisleistung; Endlager, Störfall/Unfall, Gen.inhaber
122	C2.3:1.1b	Luft/äußere Strahlung: Gamma-Ortsdosis; Endlager, Störfall/Unfall, Gen.inhaber
123	C2.3:1.2a	Luft/Aerosole: Gammaspektrometrie, Aktivitätskonzentration einzelner Nuklide; Endlager, Störfall/Unfall, Gen.inhaber
124	C2.3:1.2c	Luft/Aerosole: Gesamt-Beta-Aktivitätskonzentration; Endlager, Störfall/Unfall, Gen.inhaber
125	C2.3:2.0a	Bodenoberfläche: Kontaminationsmessung durch In-situ-Gammaspektrometrie; Endlager, Störfall/Unfall, Gen.inhaber
126	C2.3:2.0c	Bodenoberfläche: Gesamt-Beta-Kontaminationsmessung auf vorbereiteten Flächen; Endlager, Störfall/Unfall, Gen.inhaber
127	C2.3:3.0a	Weide-/Wiesenbewuchs: Gammaspektrometrie, spez. Aktivität einz. Nuklide; Endlager, Störfall/Unfall, Gen.inhaber
128	C2.3:3.0b	Weide-/Wiesenbewuchs: spezifische Gesamt-Alpha-Aktivität; Endlager, Störfall/Unfall, Gen.inhaber
129	C2.4:1.1b	Luft/äußere Strahlung: Gamma-Ortsdosis; Endlager, Störfall/Unfall, unabh. Messstelle
130	C2.4:1.2a	Luft/Aerosole: Gammaspektrometrie, Aktivitätskonzentration einz. Nuklide; Endlager, Störfall/Unfall, unabh. Messstelle
131	C2.4:1.2b	Luft/Aerosole: Gesamt-Alpha-Aktivitätskonzentration; Endlager, Störfall/Unfall, unabh. Messstelle
132	C2.4:1.2c	Luft/Aerosole: Gesamt-Beta-Aktivitätskonzentration; Endlager, Störfall/Unfall, unabh. Messstelle
133	C2.4:2.0	Bodenoberfläche: Endlager, Störfall/Unfall, unabh. Messstelle
134	C2.4:3.0a	Weide-/Wiesenbewuchs: Gammaspektrometrie, spez. Aktivität einzeln. Nuklide; Endlager, Störfall/Unfall, unabh. Messstelle
135	C2.4:3.0b	Weide-/Wiesenbewuchs: spezifische Gesamt-Alpha-Aktivität; Endlager, Störfall/Unfall, unabh. Messstelle
136	C2.4:3.0c	Weide-/Wiesenbewuchs: Tritium-Aktivitätskonzentration; Endlager, Störfall/Unfall, unabh. Messstelle
137	C2.4:4.0a	Kuhmilch: Gammaspektrometrie, Aktivitätskonzentration einzelner Nuklide; Endlager, Störfall/Unfall, unabh. Messstelle
138	C2.4:4.0b	Kuhmilch: Sr-90-Aktivitätskonzentration; Endlager, Störfall/Unfall, unabh. Messstelle
139	C2.4:4.0c	Kuhmilch: I-129-Aktivitätskonzentration; Endlager, Störfall/Unfall, unabh. Messstelle
140	C2.4:5.0	Oberflächenwasser: Endlager, Störfall/Unfall, unabh. Messstelle
141	C2.4:6.0	Trinkwasser: Endlager, Störfall/Unfall, unabh. Messstelle
142	C2.1:1.3a	Luft/Aerosole: Gammaspektrometrie, Aktivitätskonzentration einzelner Nuklide; Endlager, best.gem. Betrieb, Gen.inhaber
143	C2.1:1.3b	Luft/Aerosole: Gesamt-Alpha-Aktivitätskonzentration; Endlager, best.gem. Betrieb, Gen.inhaber
144	D1:5.0a	Oberflächenwasser: Gammaspektrometrie, Aktivitätskonzentr. einzelner Nuklide; Sonderfälle, best.gem. Betr., Gen.inhaber
145	D1:5.0b	Oberflächenwasser: Tritium-Aktivitätskonzentration; Sonderfälle, best.gem. Betrieb, Gen.inhaber
146	A1:1.3	Luft/gasförmiges Iod: KKW, best.gem. Betrieb, Gen.inhaber
147	A1:2.0	Niederschlag: KKW, best.gem. Betrieb, Gen.inhaber
148	A1:3.0	Boden: KKW, best.gem. Betrieb, Gen.inhaber
149	A1:6.0a	Grundwasser: Gammaspektrometrie, Aktivitätskonzentr. einzelner Nuklide; KKW, best.gem. Betrieb, Gen.inhaber
150	A2:4.0	Weide-/Wiesenbewuchs: KKW, best.gem. Betrieb, unabh. Messstelle
151	A3:1.1a	Luft/äußere Strahlung: Gamma-Ortsdosisleistung; KKW, Störfall/Unfall, Gen.inhaber
152	A3:1.1b	Luft/äußere Strahlung: Gamma-Ortsdosis; KKW, Störfall/Unfall, Gen.inhaber
153	A3:1.2	Luft/Aerosole: KKW, Störfall/Unfall, Gen.inhaber
154	A3:3.0	Weide-/Wiesenbewuchs: KKW, Störfall/Unfall, Gen.inhaber
155	A3:4.0	Oberflächenwasser::KKW, Störfall/Unfall, Gen.inhaber
156	A4:1.1a	Luft/äußere Strahlung: Gamma-Ortsdosisleistung; KKW, Störfall/Unfall, unabh. Messstelle
157	A4:1.1b	Luft/äußere Strahlung: Gamma-Ortsdosis; KKW, Störfall/Unfall, unabh. Messstelle
158	A4:1.2	Luft/Aerosole: KKW, Störfall/Unfall, unabh. Messstelle
159	A4:3.0	Weide-/Wiesenbewuchs: KKW, Störfall/Unfall, unabh. Messstelle
160	D1:3.1	Bodenoberfläche: Sonderfälle, best.gem. Betrieb, Gen.inhaber
161	A4:6.0	Oberflächenwasser: KKW, Störfall/Unfall, unabh. Messstelle
162	B1:1.2	Neutronenstrahlung: Neutronen-Ortsdosis; Brennelementfabrik, best.gem. Betrieb, Gen.inhaber
163	B2:1.2	Neutronenstrahlung: Neutronen-Ortsdosis; Brennelementfabrik, best.gem. Betrieb, unabh. Messstelle
164	C1.1:1.3	Luft/Neutronenstrahlung: Neutronen-Ortsdosisleistung; Brennelementzwischenlager, best.gem. Betrieb, Gen.inhaber
165	C1.1:1.4	Luft/Neutronenstrahlung: Neutronen-Ortsdosis; Brennelementzwischenlager, best.gem. Betrieb, Gen.inhaber
166	C1.2:1.1	Luft/Gamma-Strahlung: Gamma-Ortsdosis; Brennelementzwischenlager, best.gem. Betrieb, unabh. Messstelle
167	C1.2:1.2	Luft/Neutronenstrahlung: Neutronen-Ortsdosis; Brennelementzwischenlager, best.gem. Betrieb, unabh. Messstelle
168	C1.3:1.2	Luft/Aerosole: Brennelementzwischenlager, Störfall/Unfall, Gen.inhaber
169	C1.3:2.1	Bewuchs: Brennelementzwischenlager, Störfall/Unfall, Gen.inhaber
170	C1.4:1.2	Luft/Aerosole: Brennelementzwischenlager, Störfall/Unfall, unabh. Messstelle
171	C1.4:2.1	Bodenoberfläche: Brennelementzwischenlager, Störfall/Unfall,  unabh. Messstelle
172	C1.4:3.1	Bewuchs: Brennelementzwischenlager, Störfall/Unfall, unabh. Messstelle
173	C2.1:1.2	Luft/Neutronenstrahlung: Neutronen-Ortsdosis; Endlager, best.gem. Betrieb, Gen.inhaber
174	C2.2:1.2	Luft/Neutronenstrahlung: Neutronen-Ortsdosis; Endlager, best.gem. Betrieb,  unabh. Messstelle
175	C2.3:1.3	Luft/I-129: Endlager, Störfall/Unfall, Gen.inhaber
176	C2.4:1.3	Luft/I-129: Endlager, Störfall/Unfall, unabh. Messstelle
177	A2:7.1b	Oberflächenwasser: Tritium-Aktivitätskonzentration; KKW, best.gem. Betrieb, unabh. Messstelle
178	A2:7.2	Sediment: KKW, best.gem. Betrieb, unabh. Messstelle
179	A2:8.0	Fisch: KKW, best.gem. Betrieb, unabh. Messstelle
180	A2:9.0a	Trinkwasser (Brunnen): KKW, best.gem. Betrieb, unabh. Messstelle
181	A2:9.0b	Trinkwasser: Sr-90-Aktivitätskonzentration; KKW, best.gem. Betrieb, unabh. Messstelle
182	A2:9.0c	Trinkwasser (Wasserwerk): KKW, best.gem. Betrieb, unabh. Messstelle
183	A2:9.0d	Trinkwasser: Tritium-Aktivitätskonzentration; KKW, best.gem. Betrieb, unabh. Messstelle
184	A3:1.3	Luft/gasförmiges Iod: KKW, Störfall/Unfall, Gen.inhaber
185	A3:2.1	Bodenoberfläche: KKW, Störfall/Unfall, Gen.inhaber
186	A3:2.2	Boden: KKW, Störfall/Unfall, Gen.inhaber
187	A2:8.1	Wasserpflanzen: KKW, best.gem. Betrieb, unabh. Messstelle
188	A5:7.3	Schwebstoffe: KKW, best.gem. Betrieb, Behördenmessprogramm
189	A2:7.1c	Oberflächenwasser: I131-Aktivitätskonzentration; KKW, best.gem. Betrieb, unabh. Messstelle
190	D3:2.1b	Bodenoberfläche: Beta-Handmessungen; Sonderfälle, Störfall/Unfall, Gen.inhaber
191	A1:6.0b	Grundwasser: Tritium-Aktivitätskonzentration; KKW, best.gem. Betr., Gen.inhaber
192	A2:2.0	Niederschlag: KKW, best.gem. Betrieb, unabh. Messstelle
193	A2:3.0	Boden: KKW, best.gem. Betrieb, unabh. Messstelle
194	A2:5.0b	Nahrungsmittel pflanzl. Herkunft: spezifische Sr-90-Aktivität; KKW, best.gem. Betrieb, unabh. Messstelle
195	A2:5.0a	Nahrungsmittel pflanzl. Herkunft: Gammaspektro., spezif. Einzelradionuklidaktivität; KKW, best.gem. Betr., unabh. MST
196	A2:6.0a	Kuhmilch: Gammaspektrometrie, Aktivitätskonzentr. einzelner Nuklide; KKW, best.gem. Betrieb, unabh. Messstelle
197	A2:6.0b	Kuhmilch: Sr-90-Aktivitätskonzentration; KKW, best.gem. Betrieb, unabh. Messstelle
198	A2:6.0c	Kuhmilch: I-131-Aktivitätskonzentration; KKW, best.gem. Betrieb, unabh. Messstelle
199	A2:7.1a	Oberflächenwasser: Gammaspektrometrie, Aktivitätskonzentr. einzelner Nuklide; KKW, best.gem. Betrieb, unabh. Messstelle
200	A1:1.1a	Luft/äußere Strahlung: Gamma-Ortsdosisleistung; KKW, best.gem. Betrieb, Gen.inhaber
201	A1:1.1b	Luft/äußere Strahlung: Gamma-Ortsdosis; KKW, best.gem. Betrieb, Gen.inhaber
202	A1:1.2	Luft/Aerosole: KKW, best.gem. Betrieb, Gen.inhaber
203	A1:4.0	Pflanzen/Bewuchs: KKW, best.gem. Betrieb, Gen.inhaber
204	A2:1.1	Luft/äußere Strahlung: Gamma-Ortsdosis; KKW, best.gem. Betrieb, unabh. Messstelle
205	A2:1.2	Luft/Aerosole: KKW, best.gem. Betrieb, unabh. Messstelle
206	A1:1.1c	Luft/äußere Strahlung: Gamma-Ortsdosis in der Umgebung; KKW, best.gem. Betrieb, Gen.inhaber
207	A1:7.2	Sediment: KKW, best.gem. Betrieb, Gen.inhaber
208	C2.2:3.0	Weide-/Wiesenbewuchs: Endlager, best.gem. Betrieb, unabh. Messstelle
209	C2.2:4.0	Nahrungsmittel pflanzl. Herkunft: Endlager, best.gem. Betrieb, unabh. Messstelle
210	A1:8.0	Nahrungsmittel pflanzl. Herkunft: KKW, best.gem. Betrieb, Gen.inhaber
211	A2:1.1b	Luft/äußere Strahlung: Gamma-Ortsdosis am Anlagenzaun; KKW, best.gem. Betrieb, unabh. Messstelle
212	A2:1.1c	Luft/äußere Strahlung: Gamma-Ortsdosis in der Umgebung; KKW, best.gem. Betrieb, unabh. Messstelle
213	A5:3.0	Boden: KKW, best.gem. Betrieb, Behördenmessprogramm
214	A5:4.0	Weide-/Wiesenbewuchs: KKW, best.gem. Betrieb, Behördenmessprogramm
215	A5:5.0	Nahrungsmittel außer Milch: KKW, best.gem. Betrieb, Behördenmessprogramm
216	A5:6.0	Kuhmilch: KKW, best.gem. Betrieb, Behördenmessprogramm
217	A5:7.1	Oberflächenwasser: KKW, best.gem. Betrieb, Behördenmessprogramm
218	A5:7.2	Sediment: KKW, best.gem. Betrieb, Behördenmessprogramm
219	A5:9.0	Trinkwasser: KKW, best.gem. Betrieb, Behördenmessprogramm
220	A4:5.1	Nahrungsmittel pflanzlicher Herkunft: KKW, Störfall/Unfall, unabh. Messstelle
221	B3:1.1	Luft/äußere Strahlung: Brennelementfabrik, Störfall/Unfall, Gen.inhaber
222	B3:1.2	Luft/Aerosole: Brennelementfabrik, Störfall/Unfall, Gen.inhaber
223	B3:2.0	Bodenoberfläche: Brennelementfabrik, Störfall/Unfall, Gen.inhab.
224	B3:3.0	Weide-/Wiesenbewuchs: Brennelementfabrik, Störfall/Unfall, Gen.inhaber
225	B3:4.0	Oberflächenwasser: Brennelementfabrik, Störfall/Unfall, Gen.inhaber
226	B4:1.1	Luft/äußere Strahlung: Brennelementfabrik, Störfall/Unfall, unabh. Messstelle
227	B4:1.2	Luft/Aerosole: Brennelementfabrik, Störfall/Unfall, unabh. Messstelle
228	B4:2.0	Bodenoberfläche: Brennelementfabrik, Störfall/Unfall, unabh. Messstelle
229	B4:3.0	Weide-/Wiesenbewuchs: Brennelementfabrik, Störfall/Unfall, unabh. Messstelle
230	B4:4.0	Oberflächenwasser: Brennelementfabrik, Störfall/Unfall, unabh. Messstelle
231	C1.4:1.1	Luft/äußere Strahlung: Brennelementzwischenlager, Störfall/Unfall, unabh. Messstelle
232	C2.3:1.1	Luft/äußere Strahlung: Endlager, Störfall/Unfall, Gen.inhaber
233	C2.3:1.2	Luft/Aerosole: Endlager, Störfall/Unfall, Gen.inhaber
234	C2.3:2.0	Bodenoberfläche: Endlager, Störfall/Unfall, Gen.inhaber
235	C2.3:3.0	Weide-/Wiesenbewuchs: Endlager, Störfall/Unfall, Gen.inhaber
236	C2.4:1.1	Luft/äußere Strahlung: Endlager, Störfall/Unfall, unabh. Messstelle
237	C2.4:1.2	Luft/Aerosole: Endlager, Störfall/Unfall, unabh. Messstelle
238	C2.4:3.0	Weide-/Wiesenbewuchs: Endlager, Störfall/Unfall, unabh. Messstelle
239	C2.4:4.0	Kuhmilch: Endlager, Störfall/Unfall, unabh. Messstelle
240	D1:1.1	Luft/äußere Strahlung: Sonderfälle, best.gem. Betrieb, Gen.inhaber
241	D1:1.1c	Luft/äußere Strahlung: Gamma-Ortsdosis in der Umgebung; Sonderfälle, best.gem. Betrieb, Gen.inhaber
242	D1:5.0	Oberflächenwasser: Sonderfälle, best.gem. Betr., Gen.inhaber
243	D1:6.0	Grundwasser: Sonderfälle, best.gem. Betrieb, Gen.inhaber
244	D2:5.0	Nahrungsmittel pflanzlicher Herkunft: Sonderfälle, best.gem. Betrieb, unabh. Messstelle
245	D2:6.0	Kuhmilch: Sonderfälle, best.gem. Betrieb, unabh. Messstelle
246	D2:7.1	Oberflächenwasser: Sonderfälle, best.gem. Betrieb, unabh. Messstelle
247	D2:9.0	Trinkwasser: Sonderfälle, best.gem. Betrieb, unabh. Messstelle
248	D2:1.1b	Luft/äußere Strahlung: Gamma-Ortsdosis am Anlagenzaun; Sonderfälle, best.gem. Betrieb, unabh. Messstelle
249	D2:1.1c	Luft/äußere Strahlung: Gamma-Ortsdosis in der Umgebung; Sonderfälle, best.gem. Betrieb, unabh. Messstelle
250	D1:7.2	Sediment: Sonderfälle, best.gem. Betrieb, Gen.inhaber
251	D1:8.0	Nahrungsmittel pflanzl. Herkunft:Sonderfälle, best.gem. Betrieb, Gen.inhaber
252	A3:1.1	Luft/äußere Strahlung: KKW, Störfall/Unfall, Gen.inhaber
253	A4:1.1	Luft/äußere Strahlung: KKW, Störfall/Unfall, unabh. Messstelle
254	B2:1.3	Luft/Aerosole: Brennelementfabrik, best.gem. Betrieb, unabh. Messstelle
255	D3:1.1	Luft/äußere Strahlung: Sonderfälle, Störfall/Unfall, Gen.inhaber
256	D4:1.1	Luft/äußere Strahlung: Sonderfälle, Störfall/Unfall, unabh. Messstelle
257	C1.4:1.1c	Luft/äußere Strahlung: Neutronen-Ortsdosisleistung; Brennelementzwischenlager; Störfall/Unfall, unabh. Messstelle
258	C1.2:1.3	Luft/Aerosole: Brennelementzwischenlager, best.gem. Betrieb, unabh. Messstelle
259	C1.2:2.0	Boden: Brennelementzwischenlager, best.gem. Betrieb, unabh. Messstelle
260	C1.2:3.0	Weide-/Wiesenbewuchs: Brennelementzwischenlager, best.gem. Betrieb, unabh. Messstelle
261	C1.2:4.0	Oberflächenwasser: Brennelementzwischenlager, best.gem. Betrieb, unabh. Messstelle
262	C1.2:4.0a	Oberflächenwasser: Gammaspektr.,Aktivitätskonz. einzelner Nuklide, Brennelementzwischenl., best.gem. Betrieb, unabh. MST
263	C1.2:4.0b	Oberflächenwasser: Tritium-Aktivitätskonzentration, Brennelementzwischenlager, best.gem. Betrieb, unabh. Messstelle
264	C1.2:4.0c	Oberflächenwasser: I131-Aktivitätskonzentration, Brennelementzwischenlager, best.gem. Betrieb, unabh. Messstelle
265	C1.2:5.0	Sediment: Brennelementzwischenlager, best.gem. Betrieb, unabh. Messstelle
266	C1.1:1.5	Luft/Aerosole: Brennelementzwischenlager, best.gem. Betrieb, Gen.inhaber
267	C1.1:2.1	Abwasser: Brennelementzwischenlager, best.gem. Betrieb, Gen.inhaber
268	C1.1:2.2	Klärschlamm: Brennelementzwischenlager, best.gem. Betrieb, Gen.inhaber
269	C1.3:2.2	Boden: Brennelementzwischenlager, Störfall/Unfall, Gen.inhaber
270	A4:1.3	Luft/gasförmiges Iod: KKW, Störfall/Unfall, unabh. Messstelle
271	A4:2.1	Bodenoberfläche: KKW, Störfall/Unfall, unabh. Messstelle
272	A4:2.2	Boden: KKW, Störfall/Unfall, unabh. Messstelle
273	A4:4.0	Kuhmilch: KKW, Störfall/Unfall, unabh. Messstelle
274	A4:5.2	Nahrungsmittel tierischer Herkunft: KKW, Störfall/Unfall, unabh. Messstelle
275	A4:7.0	Fisch: KKW, Störfall/Unfall, unabh. Messstelle
276	A4:8.0	Trinkwasser: KKW, Störfall/Unfall, unabh. Messstelle
277	D1:1.1a	Luft/äußere Strahlung: Gamma-Ortsdosisleistung; Sonderfälle, best.gem. Betrieb, Gen.inhaber
278	D1:1.1b	Luft/äußere Strahlung: Gamma-Ortsdosis; Sonderfälle, best.gem. Betrieb, Gen.inhaber
279	D1:1.2	Luft/Aerosole: Sonderfälle, best.gem. Betrieb, Gen.inhaber
302	A1:1.1a/C1.1:1.1	Luft/Gamma-Strahlung: Gamma-Ortsdosisleistung, Umgebung KKW/Zwischenlager
321	A1:1.1b/C1.1:1.2	Luft/Gamma-Strahlung: Gamma-Ortsdosis, Umgebung KKW/Zwischenlager
341	A2:1.1/C1.2:1.1	Luft/Gamma-Strahlung: Gamma-Ortsdosis; Umgebung KKW/Zwischenlager
361	A1:1.1d	Luft/äußere Strahlung: Neutronen-Ortsdosis; KKW, best.gem. Betrieb, Gen.inhaber
362	A2:1.1d	Luft/äußere Strahlung: Neutronen-Ortsdosis; KKW, best.gem. Betrieb, unabh. Messstelle
381	B4:5.0	Boden: Brennelementfabrik, Störfall/Unfall, unabh. Messstelle
401	B2:6.0	unbearbeiteter Boden: Brennelementfabrik, best.gem. Betrieb, unabh. Messstelle
421	C2.5:2.0	Boden: Genehmigungsinhaber, Sondermessprogramm
422	C2.5:3.0	Bewuchs:; Genehmigungsinhaber, Sondermessprogramm
423	C2.5:4.0	Nahrungsmittel pflanzl. Herkunft: Genehmigungsinhaber, Sondermessprogramm
424	C2.5:5.0	Milch: Genehmigungsinhaber, Sondermessprogramm
425	C2.5:6.0	Wasser: Genehmigungsinhaber, Sondermessprogramm
441	A5:1.4	Niederschlag: KKW, best.gem. Betrieb, Behördenmessprogramm
443	C2.1:3.1	Bodenoberfläche: Endlager, Sondermessungen, Gen.inhaber 
\.

SELECT pg_catalog.setval('rei_progpunkt_gruppe_id_seq', (SELECT max(id) FROM rei_progpunkt_gruppe), false);


COPY rei_progpunkt_grp_zuord (id, rei_progpunkt_grp_id, rei_progpunkt_id) FROM stdin;
1	1	230
2	2	231
3	3	232
4	4	234
5	5	239
6	6	240
7	7	243
8	8	246
9	9	247
10	10	248
11	11	249
12	12	251
13	13	252
14	14	254
15	15	255
16	16	256
17	17	258
18	18	259
19	19	260
20	20	261
21	21	263
22	22	264
23	23	265
24	24	266
25	25	268
26	26	269
27	27	270
28	28	271
29	29	272
30	30	274
31	31	275
32	32	276
33	33	1
34	34	11
35	35	14
36	36	27
37	37	30
38	38	34
39	39	41
40	40	144
41	41	172
42	42	160
43	43	167
44	44	278
45	45	279
46	46	280
47	47	281
48	48	282
49	49	283
50	50	284
51	51	285
52	52	286
53	53	287
54	54	288
55	55	289
56	56	290
57	57	78
58	58	80
59	59	81
60	60	82
61	61	83
62	62	86
63	63	87
64	64	88
65	65	89
66	66	90
67	67	91
68	68	92
69	69	95
70	70	96
71	71	100
72	72	105
73	73	106
74	74	108
75	75	109
76	76	111
77	77	112
78	78	116
79	79	118
80	80	119
81	81	121
82	82	122
83	83	124
84	84	125
85	85	127
86	86	128
87	87	152
88	88	168
89	89	157
90	90	158
91	91	164
92	92	166
93	93	174
94	94	170
95	95	163
96	96	202
97	97	177
98	98	175
99	99	181
100	100	183
101	101	184
102	102	185
103	103	191
104	104	196
105	105	12
106	106	13
107	107	98
108	108	99
109	109	102
110	110	103
111	111	114
112	112	115
113	113	145
114	114	146
115	115	151
116	116	169
117	117	173
118	118	178
119	119	179
120	120	182
121	121	187
122	122	188
123	123	190
124	124	192
125	125	195
126	126	197
127	127	199
128	128	200
129	129	203
130	130	205
131	131	206
132	132	207
133	133	209
134	134	211
135	135	212
136	136	213
137	137	215
138	138	216
139	139	217
140	140	218
141	141	219
142	142	161
143	143	162
144	144	236
145	145	237
146	146	7
147	147	8
148	148	9
149	149	15
150	150	26
151	151	47
152	152	48
153	153	49
154	154	53
155	155	54
156	156	56
157	157	57
158	158	58
159	159	62
160	160	233
161	161	66
162	162	79
163	163	84
164	164	129
165	165	130
166	166	134
167	167	135
168	168	147
169	169	148
170	170	154
171	171	155
172	172	156
173	173	159
174	174	171
175	175	193
176	176	208
177	177	36
178	178	38
179	179	39
180	180	42
181	181	43
182	182	44
183	183	45
184	184	50
185	185	51
186	186	52
187	187	40
188	188	76
189	189	37
190	190	273
191	191	16
192	192	24
193	193	25
194	194	29
195	195	28
196	196	31
197	197	32
198	198	33
199	199	35
200	200	2
201	201	3
202	202	6
203	203	10
204	204	19
205	205	23
206	206	4
207	207	17
208	208	176
209	209	180
210	210	18
211	211	20
212	212	21
213	213	70
214	214	71
215	215	72
216	216	73
217	217	74
218	218	75
219	219	77
220	220	64
221	221	94
222	222	97
223	223	101
224	224	104
225	225	107
226	226	110
227	227	113
228	228	117
229	229	120
230	230	123
231	231	150
232	232	186
233	233	189
234	234	194
235	235	198
236	236	201
237	237	204
238	238	210
239	239	214
240	240	225
241	241	228
242	242	235
243	243	238
244	244	250
245	245	253
246	246	257
247	247	262
248	248	244
249	249	245
250	250	241
251	251	242
252	252	46
253	253	55
254	254	85
255	255	267
256	256	277
257	257	153
258	258	136
259	259	137
260	260	138
261	261	139
262	262	140
263	263	141
264	264	142
265	265	143
266	266	131
267	267	132
268	268	133
269	269	149
270	270	59
271	271	60
272	272	61
273	273	63
274	274	65
275	275	67
276	276	68
277	277	226
278	278	227
279	279	229
280	302	2
281	302	127
282	321	3
283	321	128
284	341	19
285	341	134
286	361	5
287	362	22
288	381	126
289	401	93
290	421	220
291	422	221
292	423	222
293	424	223
294	425	224
295	441	69
296	443	165
\.

SELECT pg_catalog.setval('rei_progpunkt_grp_zuord_id_seq', (SELECT max(id) FROM rei_progpunkt_grp_zuord), false);


COPY rei_progpunkt_grp_umw_zuord (id, rei_progpunkt_grp_id, umw_id) FROM stdin;
1	1	L41
2	2	L5
3	2	L51
4	2	L52
5	3	B3
6	3	B31
7	3	B32
8	3	B33
9	3	B34
10	3	B35
11	3	B36
12	4	F11
13	4	F12
14	5	G41
15	5	G5
16	5	G51
17	5	G52
18	5	G53
19	5	N7
20	5	N71
21	5	N72
22	5	N73
23	6	G41
24	6	G5
25	6	G51
26	6	G52
27	6	G53
28	6	N7
29	6	N71
30	6	N72
31	6	N73
32	7	L11
33	8	L31
34	9	L5
35	9	L51
36	9	L52
37	10	B3
38	10	B31
39	10	B32
40	10	B33
41	10	B34
42	10	B35
43	10	B36
44	11	F1
45	11	F11
46	11	F12
47	12	F2
48	12	F4
49	12	N
50	12	N2
51	12	N21
52	12	N22
53	12	N23
54	12	N24
55	12	N25
56	12	N26
57	12	N27
58	12	N28
59	12	N29
60	12	N2A
61	12	N2Y
62	12	N2Z
63	12	N31
64	12	N4
65	12	N41
66	12	N42
67	12	N43
68	12	N44
69	12	N45
70	12	N46
71	12	N4Z
72	13	N
73	13	N2
74	13	N21
75	13	N22
76	13	N23
77	13	N24
78	13	N25
79	13	N26
80	13	N27
81	13	N28
82	13	N29
83	13	N2A
84	13	N2Y
85	13	N2Z
86	13	N31
87	13	N4
88	13	N41
89	13	N42
90	13	N43
91	13	N44
92	13	N45
93	13	N46
94	13	N4Z
95	14	N11
96	14	N12
97	15	N11
98	15	N12
99	16	N11
100	16	N12
101	17	G11
102	17	G21
103	17	GZ1
104	18	G11
105	18	G21
106	18	GZ1
107	19	G12
108	19	G13
109	19	G23
110	20	N61
111	20	N62
112	21	G51
113	21	G52
114	21	G53
115	21	N71
116	21	N72
117	21	N73
118	22	G51
119	22	G52
120	22	G53
121	22	N71
122	22	N72
123	22	N73
124	23	G51
125	23	G52
126	23	G53
127	23	N71
128	23	N72
129	23	N73
130	24	G51
131	24	G52
132	24	G53
133	24	N71
134	24	N72
135	24	N73
136	25	L12
137	26	L11
138	27	L31
139	28	L41
140	29	B1
141	29	B11
142	29	B12
143	30	B3
144	30	B31
145	30	B32
146	30	B33
147	30	B34
148	30	B35
149	30	B36
150	31	F1
151	31	F11
152	31	F12
153	32	G11
154	32	G21
155	32	GZ1
156	33	L1
157	33	L11
158	33	L12
159	34	G11
160	34	G21
161	34	GZ1
162	35	G41
163	35	G51
164	35	G53
165	35	N7
166	35	N71
167	35	N72
168	35	N73
169	36	F2
170	36	F4
171	36	I21
172	36	N
173	36	N2
174	36	N21
175	36	N22
176	36	N23
177	36	N24
178	36	N25
179	36	N26
180	36	N27
181	36	N28
182	36	N29
183	36	N2A
184	36	N2Y
185	36	N2Z
186	36	N31
187	36	N4
188	36	N41
189	36	N42
190	36	N43
191	36	N44
192	36	N45
193	36	N46
194	36	N4Z
195	36	N84
196	36	N85
197	36	N87
198	36	N96
199	36	NZ2
200	36	NZZ
201	37	N11
202	37	N12
203	38	A11
204	38	A12
205	38	G11
206	38	G21
207	38	GZ1
208	39	G41
209	39	G51
210	39	G52
211	39	G53
212	39	N71
213	39	N72
214	39	N73
215	40	L1
216	40	L11
217	40	L12
218	41	L31
219	42	L31
220	43	G11
221	43	G21
222	43	GZ1
223	44	L12
224	45	L11
225	46	L31
226	47	L41
227	48	B1
228	48	B11
229	48	B12
230	49	B3
231	49	B31
232	49	B32
233	49	B33
234	49	B34
235	49	B35
236	49	B36
237	50	F1
238	50	F11
239	50	F12
240	51	N11
241	51	N12
242	52	N
243	52	N2
244	52	N21
245	52	N22
246	52	N23
247	52	N24
248	52	N25
249	52	N26
250	52	N27
251	52	N28
252	52	N29
253	52	N2A
254	52	N2Y
255	52	N2Z
256	52	N31
257	52	N4
258	52	N41
259	52	N42
260	52	N43
261	52	N44
262	52	N45
263	52	N46
264	52	N4Z
265	53	N
266	53	N5
267	53	N51
268	53	N52
269	53	N53
270	53	N54
271	53	N55
272	54	G11
273	54	G21
274	54	GZ1
275	55	N61
276	55	N62
277	56	G51
278	56	G52
279	56	G53
280	56	N71
281	56	N72
282	56	N73
283	57	L11
284	58	L31
285	59	L5
286	59	L51
287	59	L52
288	60	G41
289	60	G51
290	60	G53
291	61	L11
292	62	L31
293	63	L31
294	64	L5
295	64	L51
296	64	L52
297	65	F1
298	65	F11
299	65	F12
300	66	G11
301	66	G21
302	66	GZ1
303	67	A13
304	68	G41
305	68	G51
306	68	G53
307	69	L12
308	70	L11
309	71	L41
310	72	F1
311	72	F11
312	72	F12
313	73	F1
314	73	F11
315	73	F12
316	74	G11
317	74	G21
318	74	GZ1
319	75	G11
320	75	G21
321	75	GZ1
322	76	L12
323	77	L11
324	78	L41
325	79	B1
326	79	B11
327	79	B12
328	79	B13
329	80	B1
330	80	B11
331	80	B12
332	80	B13
333	81	F11
334	81	F12
335	82	F11
336	82	F12
337	83	G11
338	83	G21
339	83	GZ1
340	84	G11
341	84	G21
342	84	GZ1
343	85	L12
344	86	L11
345	86	L22
346	87	L11
347	88	G11
348	88	G21
349	88	GZ1
350	89	G13
351	89	G23
352	90	L1
353	90	L11
354	90	L12
355	91	B3
356	91	B31
357	91	B32
358	91	B33
359	91	B34
360	91	B35
361	91	B36
362	92	F11
363	92	F12
364	92	I19
365	93	L31
366	94	L11
367	95	L5
368	95	L51
369	95	L52
370	96	L12
371	97	F11
372	97	F12
373	98	B3
374	98	B36
375	98	B35
376	98	B34
377	98	B33
378	98	B32
379	98	B31
380	99	N4Z
381	99	N27
382	99	N28
383	99	N29
384	99	N2A
385	99	N2Y
386	99	N2Z
387	99	N31
388	99	N4
389	99	N41
390	99	N42
391	99	N43
392	99	N44
393	99	N45
394	99	N46
395	99	N25
396	99	N24
397	99	N23
398	99	N22
399	99	N21
400	99	N2
401	99	N
402	99	N26
403	100	N11
404	100	N12
405	101	G13
406	101	G23
407	102	G41
408	102	G51
409	102	G53
410	103	L31
411	104	B11
412	104	B12
413	104	B1
414	105	G11
415	105	G21
416	105	GZ1
417	106	G11
418	106	G21
419	106	GZ1
420	107	L31
421	108	L31
422	109	B11
423	109	B12
424	109	B13
425	109	B1
426	110	B11
427	110	B12
428	110	B13
429	110	B1
430	111	L31
431	112	L31
432	113	L12
433	114	L11
434	115	L12
435	116	G11
436	116	GZ1
437	116	G21
438	117	L31
439	118	F12
440	118	F11
441	119	F12
442	119	F11
443	120	N
444	120	N45
445	120	N26
446	120	N27
447	120	N28
448	120	N29
449	120	N2A
450	120	N2Y
451	120	N2Z
452	120	N2
453	120	N21
454	120	N22
455	120	N23
456	120	N24
457	120	N25
458	120	N46
459	120	N4Z
460	120	N31
461	120	N4
462	120	N41
463	120	N42
464	120	N43
465	120	N44
466	121	L12
467	122	L11
468	123	L31
469	124	L31
470	125	B11
471	125	B12
472	125	B1
473	126	B11
474	126	B12
475	126	B1
476	127	F12
477	127	F11
478	128	F12
479	128	F11
480	129	L11
481	130	L31
482	131	L31
483	132	L31
484	133	B11
485	133	B12
486	133	B1
487	134	F12
488	134	F11
489	135	F11
490	135	F12
491	136	F11
492	136	F12
493	137	N1
494	137	N11
495	137	N12
496	137	N13
497	137	N14
498	138	N1
499	138	N11
500	138	N12
501	138	N13
502	138	N14
503	139	N1
504	139	N11
505	139	N12
506	139	N13
507	139	N14
508	140	GZ1
509	140	G11
510	140	G21
511	141	G51
512	141	G52
513	141	G53
514	141	N71
515	141	N72
516	141	N73
517	142	L31
518	143	L31
519	144	GZ1
520	144	G11
521	144	G21
522	145	GZ1
523	145	G11
524	145	G21
525	146	L54
526	146	L41
527	147	L53
528	147	L54
529	147	L5
530	147	L51
531	147	L52
532	148	B3
533	148	B31
534	148	B32
535	148	B33
536	148	B34
537	148	B35
538	148	B36
539	149	G41
540	149	G51
541	149	G53
542	150	F11
543	150	F12
544	150	I13
545	150	F1
546	151	L12
547	151	S11
548	152	L11
549	152	S14
550	153	L31
551	153	S12
552	154	F11
553	154	F12
554	154	F1
555	154	S3
556	154	S31
557	154	S32
558	155	GZ1
559	155	G11
560	155	G21
561	155	S41
562	155	S42
563	155	S43
564	156	L12
565	156	S11
566	157	L11
567	157	S14
568	158	L31
569	158	S12
570	159	F11
571	159	F12
572	159	F1
573	159	S3
574	159	S31
575	159	S32
576	160	B2
577	160	B21
578	160	B22
579	160	B3
580	160	B31
581	160	B32
582	160	B33
583	160	B34
584	160	B35
585	160	B36
586	160	B1
587	161	GZ1
588	161	G11
589	161	G21
590	161	S41
591	161	S42
592	161	S43
593	162	L21
594	163	L21
595	164	L22
596	165	L21
597	165	L22
598	166	L11
599	166	L12
600	167	L21
601	167	L22
602	168	L31
603	169	F11
604	169	F12
605	170	L31
606	171	B11
607	171	B12
608	171	B1
609	172	F11
610	172	F12
611	173	L21
612	174	L21
613	175	L41
614	176	L41
615	177	A11
616	177	GZ1
617	177	G11
618	177	G21
619	178	A13
620	178	G12
621	178	G13
622	178	G23
623	179	N61
624	179	N62
625	180	G51
626	180	G52
627	180	G53
628	180	N71
629	180	N72
630	180	N73
631	181	G51
632	181	G52
633	181	G53
634	181	N71
635	181	N72
636	181	N73
637	182	G51
638	182	G52
639	182	G53
640	182	N71
641	182	N72
642	182	N73
643	183	G51
644	183	G52
645	183	G53
646	183	N71
647	183	N72
648	183	N73
649	184	L41
650	184	S13
651	185	B11
652	185	B12
653	185	B1
654	185	S2
655	185	S21
656	185	S23
657	186	B3
658	186	B31
659	186	B32
660	186	B33
661	186	B34
662	186	B35
663	186	B36
664	186	S22
665	187	I15
666	188	G12
667	188	G22
668	189	A11
669	189	GZ1
670	189	G11
671	189	G21
672	190	B11
673	190	B12
674	191	G41
675	191	G51
676	191	G53
677	192	L5
678	192	L51
679	192	L52
680	193	B31
681	193	B32
682	193	B33
683	193	B34
684	193	B35
685	193	B36
686	194	F2
687	194	F4
688	194	I21
689	194	N
690	194	N45
691	194	N26
692	194	N27
693	194	N28
694	194	N29
695	194	N2A
696	194	N2Y
697	194	N2Z
698	194	N2
699	194	N21
700	194	N22
701	194	N23
702	194	N24
703	194	N25
704	194	N46
705	194	N4Z
706	194	N31
707	194	N4
708	194	N41
709	194	N42
710	194	N43
711	194	N44
712	195	F2
713	195	F4
714	195	I21
715	195	N
716	195	N45
717	195	N26
718	195	N27
719	195	N28
720	195	N29
721	195	N2A
722	195	N2Y
723	195	N2Z
724	195	N2
725	195	N21
726	195	N22
727	195	N23
728	195	N24
729	195	N25
730	195	N46
731	195	N4Z
732	195	N31
733	195	N4
734	195	N41
735	195	N42
736	195	N43
737	195	N44
738	196	N11
739	196	N12
740	197	N11
741	197	N12
742	198	N11
743	198	N12
744	199	A11
745	199	GZ1
746	199	G11
747	199	G21
748	200	L12
749	201	L11
750	202	L31
751	203	F5
752	203	F12
753	203	I13
754	203	F3
755	203	F51
756	203	F11
757	203	F1
758	204	L11
759	204	L12
760	205	L31
761	206	L11
762	207	G13
763	207	G12
764	207	G23
765	208	F11
766	208	F12
767	209	N46
768	209	N4Z
769	209	N31
770	209	N4
771	209	N41
772	209	N42
773	209	N43
774	209	N44
775	209	N45
776	209	N25
777	209	N26
778	209	N27
779	209	N28
780	209	N29
781	209	N2A
782	209	N2Y
783	209	N2Z
784	209	N2
785	209	N21
786	209	N22
787	209	N23
788	209	N24
789	209	N
790	210	N26
791	210	N43
792	210	N42
793	210	N41
794	210	N4
795	210	N31
796	210	N4Z
797	210	N46
798	210	N24
799	210	N23
800	210	N22
801	210	N21
802	210	N2
803	210	N2Z
804	210	N2Y
805	210	N2A
806	210	N29
807	210	N28
808	210	N27
809	210	N44
810	210	N25
811	210	N45
812	210	N
813	211	L11
814	212	L11
815	213	B31
816	213	B3
817	213	B32
818	213	B33
819	213	B34
820	213	B35
821	213	B36
822	214	F12
823	214	F1
824	214	F11
825	215	N
826	215	N55
827	215	N21
828	215	N42
829	216	N11
830	216	N12
831	217	G11
832	217	GZ1
833	217	G21
834	218	G13
835	218	G23
836	219	N73
837	219	G51
838	219	G41
839	219	N71
840	219	N72
841	219	G53
842	219	G52
843	220	N
844	220	N45
845	220	N25
846	220	N26
847	220	N27
848	220	N28
849	220	N29
850	220	N2A
851	220	N2Y
852	220	N2Z
853	220	N2
854	220	N21
855	220	N22
856	220	N23
857	220	N24
858	220	N46
859	220	N4Z
860	220	N31
861	220	N4
862	220	N41
863	220	N42
864	220	N43
865	220	N44
866	221	L12
867	222	L31
868	223	B1
869	223	B11
870	223	B12
871	223	B13
872	224	F1
873	224	F11
874	224	F12
875	225	G11
876	225	G21
877	225	GZ1
878	226	L11
879	226	L12
880	226	L1
881	227	L31
882	228	B1
883	228	B11
884	228	B12
885	228	B13
886	229	F11
887	229	F12
888	230	G11
889	230	G21
890	230	GZ1
891	231	L11
892	231	L12
893	231	L1
894	232	L11
895	232	L12
896	232	L1
897	233	L31
898	234	B1
899	234	B11
900	234	B12
901	235	F11
902	235	F12
903	236	L11
904	236	L12
905	236	L1
906	237	L31
907	238	F11
908	238	F12
909	239	N1
910	239	N11
911	239	N12
912	239	N13
913	239	N14
914	240	L1
915	240	L11
916	240	L12
917	241	L11
918	242	G11
919	242	G21
920	242	GZ1
921	243	G41
922	243	G5
923	243	G51
924	243	G52
925	243	G53
926	243	N7
927	243	N71
928	243	N72
929	243	N73
930	244	N
931	244	N44
932	244	N45
933	244	N25
934	244	N26
935	244	N27
936	244	N28
937	244	N29
938	244	N2A
939	244	N2Y
940	244	N2Z
941	244	N2
942	244	N21
943	244	N22
944	244	N23
945	244	N24
946	244	N46
947	244	N4Z
948	244	N31
949	244	N4
950	244	N41
951	244	N42
952	244	N43
953	245	N11
954	245	N12
955	246	G11
956	246	G21
957	246	GZ1
958	247	G51
959	247	G52
960	247	G53
961	247	N71
962	247	N72
963	247	N73
964	248	L11
965	249	L11
966	250	G12
967	250	G13
968	250	G23
969	251	N
970	251	N44
971	251	N25
972	251	N26
973	251	N27
974	251	N28
975	251	N29
976	251	N2A
977	251	N2Y
978	251	N2
979	251	N21
980	251	N22
981	251	N23
982	251	N24
983	251	N45
984	251	N46
985	251	N4Z
986	251	N2Z
987	251	N31
988	251	N4
989	251	N41
990	251	N42
991	251	N43
992	252	L11
993	252	L12
994	252	S14
995	252	S11
996	253	L11
997	253	L12
998	253	S11
999	253	S14
1000	254	L31
1001	255	L1
1002	255	L11
1003	255	L12
1004	256	L1
1005	256	L11
1006	256	L12
1007	257	L22
1008	258	L31
1009	259	B31
1010	259	B32
1011	259	B33
1012	259	B34
1013	259	B35
1014	259	B36
1015	260	F1
1016	260	F11
1017	260	F12
1018	261	A11
1019	261	A12
1020	262	A11
1021	262	A12
1022	263	A11
1023	263	A12
1024	264	A11
1025	264	A12
1026	265	A13
1027	266	L31
1028	267	A11
1029	268	A13
1030	269	B31
1031	269	B32
1032	269	B33
1033	269	B34
1034	269	B35
1035	269	B36
1036	270	L41
1037	270	S13
1038	271	B1
1039	271	B11
1040	271	B12
1041	271	S2
1042	271	S21
1043	271	S23
1044	272	B36
1045	272	B3
1046	272	B31
1047	272	B32
1048	272	B33
1049	272	B34
1050	272	B35
1051	272	S22
1052	273	N11
1053	273	N12
1054	274	N
1055	274	N5
1056	274	N51
1057	274	N52
1058	274	N53
1059	274	N54
1060	274	N55
1061	275	N61
1062	275	N62
1063	276	G51
1064	276	G52
1065	276	G53
1066	276	N71
1067	276	N72
1068	276	N73
1069	277	L12
1070	278	L11
1071	279	L31
1072	302	L12
1073	321	L11
1074	341	L12
1075	341	L11
1076	361	L21
1077	362	L21
1078	381	B34
1079	381	B33
1080	381	B32
1081	381	B31
1082	381	B3
1083	381	B35
1084	381	B36
1085	401	B35
1086	421	B32
1087	421	B3
1088	421	B33
1089	421	B35
1090	421	B36
1091	421	B31
1092	421	B34
1093	422	I11
1094	422	I19
1095	422	I13
1096	422	I12
1097	423	N2
1098	423	N21
1099	423	N22
1100	423	N23
1101	423	N24
1102	423	N29
1103	423	N28
1104	423	N27
1105	423	N26
1106	423	N44
1107	423	N43
1108	423	N42
1109	423	N41
1110	423	N4
1111	423	N31
1112	423	N3
1113	423	N45
1114	423	N46
1115	423	F5Z
1116	423	F31
1117	423	F3
1118	423	F21
1119	423	F2
1120	424	N14
1121	424	N13
1122	424	N11
1123	424	N12
1124	425	N71
1125	425	N72
1126	425	N73
1127	425	GZ1
1128	425	G21
1129	425	G41
1130	425	G53
1131	425	G11
1132	425	G51
1133	425	G52
1134	441	L53
1135	443	B21
\.

SELECT pg_catalog.setval('rei_progpunkt_grp_umw_zuord_id_seq', (SELECT max(id) FROM rei_progpunkt_grp_umw_zuord), false);
