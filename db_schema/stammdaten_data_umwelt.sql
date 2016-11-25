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
-- TOC entry 4721 (class 0 OID 535858)
-- Dependencies: 292
-- Data for Name: umwelt; Type: TABLE DATA; Schema: stammdaten; Owner: lada
--

COPY umwelt (id, beschreibung, umwelt_bereich, meh_id) FROM stdin;
L6	\N	Spurenmessung Luft	62
I19	\N	Gras (REI)	67
L61	\N	Spurenmessung Luft - Aerosole	62
L62	\N	Spurenmessung Luft - gasförmige Komponenten (einschl. Iod)	62
L63	\N	Spurenmessung Luft - Edelgase	62
A42	\N	organische Düngemittel (aus landwirtschaftlichen Abfällen)	65
A32	\N	Deponieoberfläche	97
G5	\N	Rohwasser zur Trinkwassergewinnung	63
G51	\N	Rohwasser, geschützt, aus Grund- und Tiefenwasser	63
G52	\N	Rohwasser, ungeschützt, aus Oberflächenwasser	63
G53	\N	Zisternenwasser	63
A56	\N	Bauschutt	65
F11	\N	Weide- u. Wiesenbewuchs	67
F21	\N	Mais (ganze Pflanze)	65
F31	\N	Futtergetreide (einschl. Maiskörner)	65
F41	\N	Futterkartoffeln und Futterrüben	65
F61	\N	Maisprodukte	65
F71	\N	Kraftfuttermischungen	65
NZ1	\N	Hühnereier	67
N51	\N	Rindfleisch	67
N52	\N	Kalbfleisch	67
N53	\N	Schweinefleisch	67
N54	\N	Geflügelfleisch	67
N55	\N	Lammfleisch	67
N56	\N	Haarwildfleisch	67
N31	\N	Getreidekörner (Weizen-,Roggen-, Gersten-, Hafer-, Mais-, Triticalenkörner)	67
N21	\N	Blattgemüse, ungeschützter Anbau	67
N22	\N	Wurzelgemüse, ungeschützter Anbau	67
N23	\N	Fruchtgemüse, ungeschützter Anbau	67
N24	\N	Sprossgemüse, ungeschützter Anbau	67
N25	\N	Kartoffeln	67
N26	\N	Blattgemüse, geschützter Anbau	67
N41	\N	Schalenobst (Nüsse)	67
N42	\N	Kernobst	67
N43	\N	Steinobst	67
N44	\N	Beerenobst, ungeschützter Anbau (außer Wald-/Wildbeeren)	67
N45	\N	Beerenobst, geschützter Anbau	67
N46	\N	Wald-/Wildbeeren	67
N2A	\N	Wildpilze	67
N28	\N	Fruchtgemüse, geschützter Anbau	67
N2B	\N	Kulturpilze	67
N85	\N	Getränke, trinkfertig, nicht alkoholisch (einschl. Tee und Kaffee)	63
N86	\N	Getränke, Trockensubstanz, nicht alkoholisch (einschl. Tee und Kaffee)	65
N81	\N	Gesamtnahrung	69
N82	\N	Säuglings- und Kleinkindernahrung	67
N11	\N	Sammelmilch (Kuh-)	63
N12	\N	Hofmilch (Kuh-)	63
N13	\N	bearbeitete Trinkmilch (Kuh-)	63
N14	\N	Milch anderer Tiere (Schaf, Ziege, Stute)	63
N91	\N	Käse aus Kuhmilch	67
N92	\N	Käse aus Milch anderer Tiere	67
N93	\N	Milchprodukte außer Käse, Frischprodukte	67
N94	\N	Milchprodukte außer Käse, Trockenprodukte	64
G21	\N	Wasser in stehenden Gewässern	63
G22	\N	Schwebstoffe in stehenden Gewässern	65
G23	\N	Sedimente in stehenden Gewässern	65
G11	\N	Wasser in Fließgewässern	63
G12	\N	Schwebstoff in Fließgewässern	65
G13	\N	Sediment in Fließgewässern	65
G31	\N	Meerwasser	63
F63	\N	Maniok und Tapioka	65
G32	\N	Schwebstoffe im Meerwasser	65
G33	\N	Sedimente im Meerwasser	65
N61	\N	Süßwasserfisch	67
N62	\N	Seefisch	67
I15	\N	Wasserpflanzen	65
N63	\N	Meeresfrüchte	67
N73	\N	Reinwasser aus Mischrohwasser	63
N74	\N	Rohwasser, geschützt, aus Grund- und Tiefenwasser (ungültig)	63
N71	\N	Reinwasser aus geschützten Rohwasservorkommen	63
N72	\N	Reinwasser aus ungeschützten Rohwasservorkommen	63
A31	\N	Sicker- und Grundwasser	63
A22	\N	Kesselasche, Schlacke	65
A21	\N	Filterstaub, Filterasche	65
A23	\N	Feste Rückstände aus Rauchgaswäsche	65
A24	\N	Flüssige Rückstände aus Rauchgaswäsche	63
A41	\N	Kompost	65
A51	\N	Luftfilter	65
A52	\N	Gartenabfälle	65
A53	\N	Folien	65
A54	\N	sonstige spezielle Reststoffe und Abfälle	65
A55	\N	Reststoffe aus der Trinkwasseraufbereitung	65
A11	\N	Abwasser aus Kläranlagenablauf	63
A12	\N	Wasser aus Kanalisation, Kläranlagenzulauf, Regenrückhaltebecken	63
A13	\N	Klärschlamm	65
I21	\N	Tabakblätter	65
I31	\N	Ausgangsstoffe für Arzneimittel	65
Z31	\N	Bedarfsgegenstände mit Lebensmittelkontakt	64
Z32	\N	Bedarfsgegenstände zur Verpackung von Tabakerz. und kosmet. Mitteln	64
Z33	\N	Bedarfsgegenstände mit Körperkontakt/ Spielwaren/ Kleidung	64
L41	\N	Luft/gasförmiges Iod	62
L42	\N	Luft/Edelgase	62
L43	\N	andere gasförmige Komponenten (außer Iod u. Edelgase)	62
L12	\N	Gamma-Ortsdosisleistung	76
L11	\N	Gamma-Ortsdosis	72
B12	\N	Boden in-situ (flächenbezogene Aktivität); Boden versiegelt	61
L22	\N	Neutronen-Ortsdosisleistung	76
L21	\N	Neutronen-Ortsdosis	72
B22	\N	Boden in-situ (nuklidspez. Dosisleistung),Boden versiegelt	76
B34	\N	Freizeitflächenböden	65
B35	\N	Ödlandböden, Brachen	65
B36	\N	Gartenböden	65
F12	\N	Grünfutterpflanzen (außer Weide- u. Wiesenbewuchs)	65
F51	\N	Heu	65
F52	\N	Stroh, Cobs, Trockenmehle	65
F62	\N	Schrote	65
91	\N	PARK Modellbereiche	\N
A1	\N	Kläranlage	\N
A2	\N	Verbrennungsanlage	\N
A3	\N	Mülldeponie	\N
A4	\N	Kompostierungsanlage	65
A5	\N	Spezielle Reststoffe und Abfälle	65
B1	\N	Boden in-situ (flächenbezogene Aktivität)	61
B2	\N	Boden in-situ (nuklidspezifische Dosisleistung)	76
B3	\N	Weide-/ Acker-/ Wald-/ Freizeitflächen-/ Ödland- und Gartenböden	65
F1	\N	Grünfutter (einschl. Weide- und Wiesenbewuchs)	\N
F2	\N	Mais	65
F3	\N	Futtergetreide	65
F4	\N	Hackfrüchte	65
F5	\N	Heu, Stroh, Cobs, Trockenmehle	65
F6	\N	Mischfuttermittelrohstoffe	65
F7	\N	Mischfuttermittel	65
G1	\N	Fließgewässer	\N
G2	\N	Stehende Gewässer	\N
G3	\N	Meer	\N
G4	\N	Grundwasser	63
GZ	\N	Sonstige Wässer	63
I1	\N	Pflanzliche Indikatoren	\N
I2	\N	Tabak	65
I3	\N	Arzneimittel	65
L1	\N	Luft/Gammastrahlung	\N
L2	\N	Luft/Neutronenstrahlung	\N
L3	\N	Luft/Aerosole	\N
L4	\N	Luft/gasförmige Komponenten (einschl. Iod)	\N
L5	\N	Niederschlag	\N
N1	\N	Milch	63
N2	\N	Frischgemüse (einschl. Kartoffeln und Pilze)	67
N3	\N	Getreide	67
N4	\N	Obst	67
N5	\N	Fleisch	67
N6	\N	Fisch und Meeresfrüchte	67
N7	\N	Trinkwasser	63
N8	\N	Gesamtnahrung, Fertiggerichte und Getränke	\N
N9	\N	Nahrungsmittelprodukte	\N
NZ	\N	Sonstige Nahrungsmittel	67
Z1	\N	Baustoffe	64
Z2	\N	Bodenschätze	62
Z3	\N	Bedarfsgegenstände und Kosmetische Mittel	64
9	\N	PARK	\N
A	\N	Abwasser, Reststoffe  und Abfälle	\N
B	\N	Boden	\N
F	\N	Futtermittel	\N
G	\N	Gewässer	\N
I	\N	Bio-Indikatoren, Tabak und Arzneimittel	\N
L	\N	Luft und Niederschlag	\N
N	\N	Nahrungsmittel (einschl. Trinkwasser)	\N
Z	\N	Sonstige Mediengruppen	\N
L32	\N	Bilanzierungsmessung Aerosole	60
L44	\N	Bilanzierungsmessung Luft/Iod	60
L45	\N	Bilanzierungsmessung Luft/Edelgase	60
L46	\N	Bilanzierungsmessung andere gasförmige Komponenten (außer Iod und Edelgase)	60
N2Y	\N	Sonstiges Gemüse, ungeschützter Anbau	67
N2Z	\N	Sonstiges Gemüse, geschützter Anbau	67
N3Z	\N	Sonstige Getreidearten	67
N4Z	\N	Sonstige Obstarten	67
N5Z	\N	Sonstiges Fleisch	67
NZZ	\N	weitere Nahrungsmittel	67
F5Z	\N	Sonstige Futtermittel	65
N2C	\N	Sonstige Wildpilze	67
G41	\N	Grundwasser (nicht zur Trinkwassergewinnung)	63
GZ1	\N	Wasser zur Viehtränke	63
L31	\N	Aerosole	62
L52	\N	nasse Niederschläge (Deposition)	61
L51	\N	Niederschlag (Aktivitätskonzentration)	63
L54	\N	Niederschlagsmenge	1
L53	\N	trockene Niederschläge (Deposition)	61
N27	\N	Wurzelgemüse, geschützter Anbau	67
N75	\N	Rohwasser, ungeschützt, aus Oberflächenwasser (ungültig)	63
N76	\N	Zisternenwasser (ungültig)	63
N83	\N	Fertiggerichte, verzehrsfertig (einschl. Suppen)	67
N84	\N	Fertiggerichte, Trockensubstanz (einschl. Suppen)	65
N87	\N	Getränke, alkoholisch	63
N95	\N	Milchprodukte außer Käse, haltbar gemacht	67
N96	\N	Gemüseprodukte einschl. Kartoffeln, Frischprodukte auch tiefgefroren	67
N97	\N	Gemüseprodukte einschl. Kartoffeln, Trockenprodukte	64
N98	\N	Gemüseprodukte einschl. Kartoffeln, haltbar gemacht	67
N99	\N	Wildpilzprodukte, Frischprodukte auch tiefgefroren	67
N9A	\N	Wildpilzprodukte, Trockenprodukte	64
N9B	\N	Wildpilzprodukte, haltbar gemacht	67
N9C	\N	Kulturpilzprodukte, Frischprodukte auch tiefgefroren	67
N9D	\N	Kulturpilzprodukte, Trockenprodukte	64
N9E	\N	Kulturpilzprodukte, haltbar gemacht	67
B11	\N	Boden in-situ (flächenbezogene Aktivität); Boden unversiegelt	61
B31	\N	Weideböden	65
B32	\N	Ackerböden	65
B33	\N	Waldböden	65
B21	\N	Boden in-situ (nuklidspez. Dosisleistung),Boden unversiegelt	76
N9F	\N	Getreideprodukte außer Brot	67
N9G	\N	Brote und Gebäcke	67
N9H	\N	Obstprodukte, Frischprodukte auch tiefgefroren	67
N9I	\N	Obstprodukte, Trockenprodukte	64
N9J	\N	Obstprodukte, haltbar gemacht	67
N9K	\N	Fleischprodukte u. Wurstwaren, ohne Wild, Frischprod. auch tiefgefr.	67
N9L	\N	Fleischprodukte u. Wurstwaren, ohne Wild, haltbar gemacht	67
N9M	\N	Wildfleischprodukte u. -wurstwaren, Frischprod. auch tiefgefr.	67
N9N	\N	Wildfleischprodukte u. -wurstwaren, haltbar gemacht	67
N9O	\N	Fischprodukte, Frischprod. auch tiefgefr.	67
N9P	\N	Fischprodukte, haltbar gemacht	67
N9Q	\N	Meeresfrüchteprodukte, Frischprod. auch tiefgefr.	67
N9R	\N	Meeresfrüchteprodukte, haltbar gemacht	67
NZ2	\N	Honig	67
Z11	\N	mineralische Ausgangsstoffe für Baustoffe	64
I11	\N	Blätter	65
I12	\N	Nadeln	65
I13	\N	Gras	65
I14	\N	Moose, Farne, Flechten u. Heidekraut	65
Z12	\N	verarbeitete mineralische Baustoffe	64
Z13	\N	organische Ausgangsstoffe für Baustoffe	64
Z14	\N	verarbeitete organische Baustoffe	64
Z21	\N	Rohgas	62
Z22	\N	Reingas	62
Z34	\N	Bedarfsgegenstände zur Reinigung und Pflege	64
Z35	\N	Kosmetische Mittel und Stoffe zu deren Herstellung	64
I22	\N	Zigaretten, Zigarren	65
I32	\N	Arzneimittelprodukte	65
N29	\N	Sprossgemüse, geschützter Anbau	67
N15	\N	Humanmilch	63
N64	\N	Fischerzeugnisse	67
M	Markiert eine Probe als Meteo-Probe	Meteo-Umweltbereich	\N
N65	\N	Wasserpflanzen, Trockenprodukte	64
B13	\N	Bodenauflage	61
S	\N	Umweltbereiche für Störfall	\N
S1	\N	Luft - Störfall	\N
S11	\N	Luft/äußere Strahlung (Gamma-ODL) - Störfall	76
S12	\N	Luft/Aerosole - Störfall	62
S13	\N	Luft/gasförmiges Iod - Störfall	62
S14	\N	Luft/äußere Strahlung (Gamma-OD) -Störfall	72
S2	\N	Boden/-Oberfläche - Störfall	\N
S21	\N	Bodenoberfläche (unversiegelt, in-situ, flächenbezogene Aktivität) - Störfall	61
S22	\N	Boden - Störfall	65
S23	\N	Bodenoberfläche (versiegelt, in-situ, flächenbezogene Aktivität) - Störfall	61
S3	\N	Pflanzen/Bewuchs - Störfall	\N
S31	\N	Weide- und Wiesenbewuchs - Störfall	67
S32	\N	Grünfutterpflanzen (außer Weide- und Wiesenbewuchs) - Störfall	65
S4	\N	Oberirdische Gewässer - Störfall	63
S41	\N	Oberflächenwasser (Fließgewässer) - Störfall	63
S42	\N	Oberflächenwasser (stehende Gewässer) - Störfall	63
S43	\N	Oberflächenwasser (Viehtränke) - Störfall	63
\.
