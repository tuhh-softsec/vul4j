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
-- TOC entry 4694 (class 0 OID 535751)
-- Dependencies: 259
-- Data for Name: mess_stelle; Type: TABLE DATA; Schema: stammdaten; Owner: lada
--

COPY mess_stelle (id, netzbetreiber_id, beschreibung, mess_stelle, mst_typ, amtskennung) FROM stdin;
03010	03	Niedersächsisches Landesamt für Verbraucherschutz und Lebensmittelsicherheit, Lebensmittelinstitut Braunschweig, Dresdenstraße 2+6, 38124 Braunschweig	nslm31	M	032001
01011	01	Helmholtz-Zentrum Geesthacht, Umgebungsüberwachung, Postfach 11 60, 21494 Geesthacht	KTA - HZG	M	\N
01020	01	Landwirtschaftliche Untersuchungs- und Forschungsanstalt, Dr. Hell-Str. 6, 24107 Kiel	shlm11	M	\N
01030	01	Lebensmittel-, Veterinär- und Umweltuntersuchungsamt des Landes Schleswig-Holstein, Max-Eyth-Str. 5, 24537 Neumünster	\N	M	012001
01051	01	Kernkraftwerk Krümmel (KKK), Informationszentrum, Elbuferstr. 82, 21502 Geesthacht	KKW - KKK	M	\N
01071	01	Kernkraftwerk Brunsbüttel (KKB) GmbH & Co. oHG, Otto-Hahn-Straße, 25541 Brunsbüttel	KKW - KKB	M	\N
01081	01	E.ON Kernkraft GmbH, Kernkraftwerk Brokdorf (KBR), Osterende, 25576 Brokdorf	KKW - KBR	M	\N
02002	02	Behörde für Gesundheit und Verbraucherschutz der Freien und Hansestadt Hamburg, Fachabtlg. Gesundheit und Umwelt (V5), Billstr. 80, 20539 Hamburg	02-Min	M	\N
02010	02	Freie und Hansestadt Hamburg, Behörde für Gesundheit und Verbraucherschutz, Institut für Hygiene und Umwelt, Marckmannstr. 129b Haus 6, 20539 Hamburg	hhlm11	M	022020
02020	02	Freie und Hansestadt Hamburg, Behörde für Gesundheit und Verbraucherschutz, Institut für Hygiene und Umwelt, Marckmannstr. 129b Haus 6, 20539 Hamburg	hhlm21	M	\N
01010	01	Helmholtz-Zentrum Geesthacht, Abteilung KSU, Geb. 46, Max-Planck-Str. 1, 21502 Geesthacht	shlm21 - HZG	M	\N
03040	03	Niedersächsischer Landesbetrieb für Wasserwirtschaft, Küsten- und Naturschutz, Betriebsstelle Hannover-Hildesheim-, An der Scharlake 39, 31135 Hildesheim	nslm11	M	\N
M1602	16	Messfahrzeug / mobiles Messsystem 2 Thüringen	Mobiles Messsystem 2 TH	B	\N
30012	S	Bundesministerium der Verteidigung, WV IV 4, Robert-Schuman-Platz 3, Haus 201, 53123 Bonn	Min	M	\N
01002	01	Ministerium für Energiewende, Landwirtschaft, Umwelt und ländliche Räume (MELUR) des Landes Schleswig-Holstein, Referat Strahlenschutz  V 74, Adolph-Westphal-Str. 4, 24143 Kiel	01-Min, LDZ	M	\N
04002	04	Senator für Bau, Umwelt und Verkehr, Immissionsschutzreferat, Ansgaritorstraße 2, 28195 Bremen	04-Min	M	\N
03132	03	Niedersächsisches Ministerium für Umwelt, Energie und Klimaschutz, Archivstr. 2, 30169 Hannover	03-Min MU	M	\N
03141	03	Kernkraftwerk Lingen GmbH, Schüttorfer Str. 100, 49808 Lingen	KKW - KWL	M	\N
03151	03	E.ON Kernkraftwerk Stade GmbH & Co oHG, 21657 Stade	KKW - KKS	M	\N
03161	03	E.ON Kernkraftwerk Unterweser GmbH, TST / Strahlenschutz, Dedesdorfer Str. 2, 26935 Stadland	KKW - KKU	M	\N
03181	03	Forschungsbergwerk Asse, Institut für Tiefenlagerung, Postfach 1461, 38248 Remlingen	(KTA - Asse)	M	\N
03191	03	Kernkraftwerke Lippe-Ems GmbH, Am Hilgenberg, 49811 Lingen	KKW - KKE	M	\N
03201	03	GNS-Werk Gorleben, Gesellschaft für Nuklear-Service mbH, Lüchower Str. 8, 29475 Gorleben	KTA - PKA	M	\N
03221	03	Advanced Nuclear Fuels GmbH, Am Seitenkanal 1, 49811 Lingen	KTA - ANF	M	\N
04020	04	Landesmessstelle für Radioaktivität, Universität Bremen / FB Physik/Elektrotechnik, Otto-Hahn-Allee 1 (NW 1), 28359 Bremen	brlm11	M	\N
04032	04	Der Senator für Umweltschutz und Stadtentwicklung, Wegesende 23, 28195 Bremen	brldz1	M	\N
05080	05	Landwirtschaftliche Untersuchungs- und Forschungsanstalt, , 4400 Münster	LUFA - NRW	M	\N
05091	05	Forschungszentrum Jülich (FZJ), Postfach 1913, 52428 Jülich	KTA - FZJ	M	\N
05100	05	Chemisches und Veterinäruntersuchungsamt Ostwestfalen-Lippe, Standort Detmold, Westerfeldstr. 1, 32758 Detmold	nwlm41	M	052203
05112	05	Ministerium für Klimaschutz, Umwelt, Landwirtschaft, Natur- und Verbraucherschutz des Landes Nordrhein-Westfalen, Schwannstr. 3, 40476 Düsseldorf	05-Min	M	\N
05122	05	Landesumweltamt, Nordrhein-Westfalen, Postfach 10 23 63, 45023 Essen	nwldz1	M	\N
16010	16	Thüringer Landesanstalt für Umwelt und Geologie, Prüssingstraße 41, 07745 Jena	thlm11	M	\N
05020	05	Landesamt für Natur, Umwelt und Verbraucherschutz Nordrhein-Westfalen, Auf dem Draap 25, 40221 Düsseldorf	nwlm11	M	051002
05030	05	Landesinstitut für Arbeitsgestaltung des Landes Nordrhein-Westfalen, FG 1.3, Gurlittstr. 55, 40223 Düsseldorf	nwlm21	M	\N
05040	05	Ruhrverband Ruhrtalsperrenverein, Chemisches und Biologisches Laboratorium, 4300 Essen 1	\N	M	\N
05050	05	TÜV - Rheinland, Postfach 10 17 50, 5000 Köln	TÜV	M	\N
05060	05	Staatliches Veterinäruntersuchungsamt, , 4150 Krefeld	\N	M	\N
05070	05	Chemisches und Veterinäruntersuchungsamt Münsterland-Emscher-Lippe AÖR,  Joseph-König-Str. 40, 48147 Münster	nwlm51	M	052502
16020	16	Thüringer Landesanstalt für Umwelt und Geologie, Hermann-Drechsler-Str. 1, 07548 Gera	thlm21	M	\N
05161	05	SNR-Versuchskraftwerk Kalkar (SNR-300), Informationszentrum, 47546 Kalkar	KKW - SNR-300	M	\N
08020	08	Chemisches- und Veterinäruntersuchungsamt Stuttgart, Dienstsitz Fellbach, Schaflandstr. 2/3, 70736 Fellbach	bwlm21 - CVUA S	M	082107
08031	08	Universität Hohenheim, Institut für Physik und Meteorologie, 7000 Stuttgart 70	Uni. Hoh.	M	\N
05141	05	Kernkraftwerk Würgassen (KWW), Informationszentrum, 37688 Beverungen	KKW - KWW	M	\N
07010	07	Landesamt für Umwelt, Kaiser-Friedrich-Str. 7, 55118 Mainz	rplm11	M	\N
07020	07	Landesamt für Umwelt, (Abt. 6, Ref. 67), Wallstraße 1, 55122 Mainz	rplm41	M	\N
07030	07	Landesuntersuchungsamt, Institut für Lebensmittelchemie, Nikolaus-von-Weis-Str. 1, 67346 Speyer	rplm21	M	072107
07040	07	Landwirtschaftliche Untersuchungs-und Forschungsanstalt, Obere Langgasse 40, 67346 Speyer	rplm51	M	\N
07002	07	Ministerium für Umwelt, Energie, Ernährung und Forsten Rheinland-Pfalz, Kaiser-Friedrich-Str. 1, 55116 Mainz	rpldz1	M	\N
05171	05	Brennelemente-Zwischenlager Ahaus (TBL), Ammeln 59, 48683 Ahaus	KTA - TBL	M	\N
05181	05	Urananreicherungsanlage Gronau (UAG), Informationszentrum der Urenco Deutschland OHG, Röntgenstr. 4, 48599 Gronau	KTA - UAG	M	\N
06010	06	Hessisches Landesamt für Umwelt und Geologie, Dienststelle Kassel - Dez. I5 -, Ludwig-Mond-Str. 33, 34121 Kassel	helm21-HLUG-Kassel	M	\N
06020	06	Staatliches Medizinal-, Lebensmittel- und, Veterinäruntersuchungsamt Südhessen, Postfach 55 45, 65045 Wiesbaden	(helm41)-MLVA-Wiesbaden	M	\N
06040	06	Staatliches Medizinal-, Lebensmittel- und, Veterinäruntersuchungsamt Mittelhessen, Marburger Str. 54, 35396 Gießen	(helm61)-MLVA-Gießen	M	\N
06050	06	Hessische Landwirtschaftliche Versuchsanstalt, Am Versuchsfeld 13, 34128 Kassel	(helm71)-HLVA-Kassel	M	\N
06060	06	Hessisches Landesamt für Umwelt und Geologie, Dienststelle Darmstadt - Dez. I5 -, Kasinostr. 60, 64293 Darmstadt	helm31-HLUG-Darmstadt	M	\N
06112	06	Hessisches Landesamt für Umwelt und Geologie, Dienststelle Kassel - LDZ StrVG, Ludwig-Mond-Str. 33, 34121 Kassel	heldz1	M	\N
05010	05	Landesbetrieb Mess- und Eichwesen Nordrhein-Westfalen, Betriebsstelle für Sonderaufgaben, Eichamt Dortmund, Marsbruchstr. 186, 44287 Dortmund	nwlm31	M	\N
05151	05	Prototypenkernkraftwerk Hamm-Uentrop (THTR-300), Kraftwerk Westfalen, Siegenbeckstr. 10, 59071 Hamm	KKW - THTR-300	M	\N
14010	14	Staatliche Betriebsgesellschaft für Umwelt und Landwirtschaft (BfUL), Altwahnsdorf 12, 01445 Radebeul	snlm11-BfUL	M	\N
12002	12	Ministerium der Justiz und für Europa und Verbraucherschutz des Landes Brandenburg, Heinrich-Mann-Allee 103, 14473 Potsdam	12-Min	M	\N
13002	13	Ministerium für Inneres und Sport Mecklenburg-Vorpommern, Abt. 2, Referat 250 (Strahlenschutz), Alexandrinenstraße 1, 19055 Schwerin	13-Min	M	\N
14002	14	Sächsisches Staatsministerium für Umwelt und Landwirtschaft, Archivstrasse 1, 01097 Dresden	14-Min	M	\N
15002	15	Ministerium für Landwirtschaft und Umwelt des Landes Sachsen-Anhalt, Leipziger Str. 58, 39112 Magdeburg	15-Min	M	\N
16002	16	Thüringer Ministerium für Umwelt, Energie und Naturschutz, Beethovenstraße 3, 99096 Erfurt	16-Min	M	\N
12032	12	Landesamt für Verbraucherschutz, Landwirtschaft und Flurneuordnung Brandenburg, Referat 25 - Strahlenschutz, Müllroser Chaussee 50, 15236 Frankfurt/Oder	bbldz1	M	\N
12010	12	Landeslabor Berlin-Brandenburg, Fachbereich IV-1 Strahlenschutz, Landesmessstelle Oranienburg, Sachsenhausener Straße 7b, 16515 Oranienburg	bblm11	M	\N
12020	12	Landeslabor Berlin-Brandenburg, Fachbereich IV-1 Strahlenschutz, Landesmessstelle Frankfurt (Oder), Gerhard-Neumann-Straße 2/3, 15236 Frankfurt (Oder)	bblm21	M	122104
13032	13	Landesamt für Umwelt, Naturschutz und Geologie, Mecklenburg-Vorpommern (Außenstelle Stralsund), Badenstr. 18, 18439 Stralsund	mvldz1	M	\N
13010	13	Landesamt für Umwelt, Naturschutz und Geologie Mecklenburg-Vorpommern, Außenstelle Stralsund, Badenstr. 18, 18439 Stralsund	mvlm11	M	\N
13020	13	Landes-Veterinär- und Lebensmitteluntersuchungsamt Rostock, Außenstelle Schwerin, Neumühler Str. 10/12, 19057 Schwerin	(mvlm21)	M	\N
14032	14	Staatliche Betriebsgesellschaft für Umwelt und Landwirtschaft(BfUL), Altwahnsdorf 12, 01445 Radebeul	snldz1-BfUL	M	\N
20070	E	Bundesforschungsanstalt für Ernährung, Zentrallaboratorium für Isotopentechnik (ZIT), Engesserstr. 20, 76131 Karlsruhe	BFE	M	\N
09171	09	Siemens AG, Seligenstädter Str. 100, 63791 Karlstein am Main	KTA - SAGK	M	\N
09181	09	Siemens AG, Brennelementewerk Hanau, Standort Karlstein, An den Schafäckern, 63791 Karlstein	KTA	M	\N
09192	09	Bayerisches Landesamt für Umwelt, Landesdatenzentrale, 86177 Augsburg	byldz1 - LfU	M	\N
11061	11	Helmholtz-Zentrum Berlin für Materialien und Energie GmbH, Glienicker Straße 100, 14109 Berlin	KTA - BERII	M	\N
09060	09	Landesuntersuchungsamt für das Gesundheitswesen, Nordbayern, Eggenreuther Weg 43, 91058 Erlangen	(bylm51)	M	\N
10002	10	Ministerium für Umwelt und Verbraucherschutz, Keplerstr. 18, 66117 Saarbrücken	10-Min	M	\N
10010	10	Landesamt für Umwelt- und Arbeitsschutz, FB 5.4 - Strahlenschutz IMIS, Don-Bosco-Str. 1, 66119 Saarbrücken	sllm11	M	\N
10022	10	Ministerium für Gesundheit und Verbraucherschutz, Ursulinenstr. 8-16, 66111 Saarbrücken	10-Min	M	\N
10030	10	Radioaktivitätsmessstelle der Universität des Saarlandes, Gebäude 76, Universitätsgelände, 66421 Homburg/Saar	sllm21	M	\N
10042	10	Landesamt für Umwelt- und Arbeitsschutz, Don-Bosco-Str.1, 66119 Saarbrücken	slldz1	M	\N
15032	15	Ministerium für Landwirtschaft und Umwelt des Landes Sachsen-Anhalt, Leipziger Str. 58, 39112 Magdeburg	saldz1	M	\N
09051	09	Helmholtz Zentrum München, Deutsches Forschungszentrum für Gesundheit und Umwelt (GmbH), Radioanalytisches Laboratorium, Ingolstädter Landstr. 1, 85764 Oberschleißheim	RADLAB	M	\N
06101	06	RWE-Power AG Kraftwerk Biblis, Strahlenschutz DS, 68643 Biblis	KKW-Biblis	M	\N
05090	05	Materialprüfungsamt des Landes Nordrhein-Westfalen, Marsbruchstr. 186, 44287 Dortmund	MPA	M	\N
30021	19	Bundesamt für Strahlenschutz (BfS), Fachgebiet SW 2.3, Köpenicker Allee 120-130, 10318 Berlin	BfS Berlin	M	113103
15010	15	Landesamt für Umweltschutz Sachsen-Anhalt, Fachbereich Medienübergreifender Umweltschutz, Fachgebiet Umweltradioaktivität, Reideburger Straße 47, 06116 Halle/Saale	salm11	M	\N
09212	09	Bayerisches Staatsministerium für Umwelt und Verbraucherschutz, Rosenkavalierplatz 2, 819251 München	LLZ	M	\N
09222	09	Bayerisches Staatsministerium für Landwirtschaft und Forsten, Postfach 22 00 12, 80535 München	MStZ	M	\N
09231	09	TÜV Industrie Service GmbH, TÜV SÜD Gruppe, IS-ET, Westendstr. 199, 80686 München	TÜV	M	\N
08041	08	Karlsruher Institut für Technologie - Campus Nord, Koordinierungsstelle Abwasser-, Fortluft- und Umgebungsüberwachung (SUM-SK), Hermann-von-Helmholtzplatz 1, 76344 Eggenstein-Leopoldshafen	KIT - CN	M	\N
08050	08	Staatliche Landwirtschaftliche Untersuchungs- und Forschungsanstalt Augustenberg, Postfach 43 02 30, 76217 Karlsruhe	(bwlm41) - LUFA	M	\N
05092	05	Arbeitsgemeinschaft Versuchsreaktor GmbH (AVR), Wilhelm-Johnen-Str., 52428 Jülich	KTA - AVR	M	\N
09141	09	Kernkraftwerk Gundremmingen GmbH,  Dr.-August-Weckesser-Straße 1, 89355 Gundremmingen	KKW - KGG	M	\N
09151	09	Versuchsatomkraftwerk Kahl GmbH (VAK), Postfach 6, 63796 Kahl am Main	KKW - VAK	M	\N
09161	09	Siemens AG, UB KWU, Hammerbachstr. 12/14, 91058 Erlangen	KTA - UB KWU	M	\N
09070	09	Bayerische Landesanstalt für Landwirtschaft, IAB 1e, Menzinger Straße 54, 80638 München	(bylm41)	M	\N
09081	09	Landesgewerbeanstalt Bayern, Gewerbemuseumsplatz 2, 90403 Nürnberg	LGA	M	\N
09091	09	Milchwirtschaftliche Untersuchungs- und Versuchsanstalt, Ignaz-Kiechle-Straße 20-22, 87437 Kempten/Allgäu	MUVA	M	\N
09111	09	Technische Universität München, Institut für Radiochemie, Reaktorstation Garching, Lichtenbergstraße 1, 85748 Garching	TUM - FRM II	M	\N
09121	09	Kernkraftwerk Isar GmbH (KKI), Dammstraße, 84051 Essenbach	KKW - KKI	M	\N
09010	09	Bayerisches Landesamt für Umwelt, Bürgermeister-Ulrich-Str. 160, 86179 Augsburg	(bylm11)	M	\N
09020	09	Bayerisches Landesamt für Umwelt, Bürgermeister-Ulrich-Str. 160, 86179 Augsburg	(bylm21)	M	\N
09030	09	Bayerisches Landesamt für Umwelt, Bürgermeister-Ulrich-Str. 160, 86179 Augsburg	bylm31	M	091800
06122	06	Hessisches Ministerium für Umwelt, Klimaschutz, Landwirtschaft und Verbraucherschutz, Mainzer Straße 80, 65189 Wiesbaden	HE REI-AB	M	\N
11031	11	\N	\N	M	\N
09131	09	Kernkraftwerk Grafenrheinfeld (KKG), Postfach 7, 97506 Grafenrheinfeld	KKW - KKG	M	\N
09040	09	Bayerisches Landesamt für Gesundheit und Lebensmittelsicherheit, Veterinärstr. 2, 85764 Oberschleißheim	(bylm61)	M	\N
08061	08	Staatliche milchwirtschaftliche Lehr- und Forschungsanstalt, , 88239 Wangen im Allgäu	MLF	M	\N
08070	08	Chemisches- und Veterinäruntersuchungsamt Freiburg, Bissierstr. 5, 79114 Freiburg	bwlm31 - CVUA FR	M	082102
08082	08	Landesdatenzentrale Baden-Württemberg (Landesanstalt für Umwelt, Messungen und Naturschutz), Referat Umweltradioaktivität, Strahlenschutz, Hertzstraße 173, 76187 Karlsruhe	bwldz1 - LDZ BW	M	\N
08101	08	Kernkraftwerk Obrigheim, Kraftwerkstr. 1, 74847 Obrigheim	KWO	M	\N
08111	08	EnBW Kernkraft GmbH, Kernkraftwerk Neckarwestheim, Im Steinbruch , 74382 Neckarwestheim	GKN	M	\N
08121	08	EnBW Kernkraft GmbH, Kernkraftwerk Philippsburg, 76652 Philippsburg	KKP	M	\N
08131	08	Deutsches Krebsforschungszentum Heidelberg, Forschungsreaktor Heidelberg, 69000 Heidelberg	DKFZ	M	\N
07050	07	Landesuntersuchungsamt, Institut für Lebensmittelchemie, Maximineracht 11a, 54295 Trier	(rplm31)	M	\N
16032	16	Thüringer Landesanstalt für Umwelt und Geologie, Prüssingstraße 41, 07745 Jena	thldz1	M	\N
07071	07	Johannes Gutenberg Universität, Institut für Kernchemie, Friedrich v. Pfeiffer Weg, 6500 Mainz	KTA	M	\N
07081	07	Anlage Mülheim-Kärlich (KMK), Am Guten Mann, 56218 Mülheim-Kärlich	KKW - KMK	M	\N
08002	08	Ministerium für Umwelt, Klima und Energiewirtschaft Baden-Württemberg, Kernerplatz 9, 701829 Stuttgart	UM BW	M	\N
08010	08	Landesanstalt für Umwelt, Messungen und Naturschutz Baden-Württemberg, Referat Radioaktivität, Strahlenschutz, Hertzstraße 173, 76187 Karlsruhe	bwlm11 - LUBW	M	\N
19001	19	Testmessstelle für IMIS, München und Berlin, Teststraße	MST1-Schulung	M	\N
08201	08	Fachhochschule Ravensburg-Weingarten, Institut für angewandte Forschung/Strahlungsmesstechnik, Doggenriedstraße, 88250 Weingarten	FH RV	M	\N
20080	X	BfS - Kontaktstelle internat. Datenaustausch	BfS - Kontaktstelle internat. Datenaustausch	M	\N
11010	11	Senatsverwaltung für Stadtentwicklung und Umwelt, Rubensstr. 111, 12157 Berlin	belm11	M	\N
11042	11	Senatsverwaltung für Stadtentwicklung und Umwelt, Rubensstraße 111, 12157 Berlin	beldz1	M	\N
20110	I	Bundesamt für Strahlenschutz (BfS), Fachgebiet SW 1.6, Postfach 11 08, 85758 Oberschleißheim	BfS  LSt AB	M	\N
30000	M	Bundesministerium für Umwelt, Naturschutz, Bau und Reaktorsicherheit, Referat RS II 6, Robert-Schuman-Platz 3, 53175 Bonn	Min	M	\N
30031	B	Bundesanstalt für Fleischforschung, Institut für Chemie und Physik, 95326 Kulmbach	\N	M	\N
30041	S	Bundesforschungsanstalt f. Getreide-u. Kartoffelverarbeitung, Institut für Biochemie u. Analytik, 4930 Detmold	\N	M	\N
20040	B	Johann Heinrich von Thünen-Institut, Bundesforschungsinstitut für Ländliche Räume, Wald und Fischerei, Institut für Fischereiökologie, Marckmannstr. 129b, Haus 4, 20539 Hamburg	vTI	M	\N
20050	N	Max Rubner-Institut, Bundesinstitut für Ernährung und Lebensmittel, Institut für Sicherheit und Qualität bei Milch und Fisch am Standort Kiel, Hermann-Weigmann-Str. 1, 24103 Kiel	MRI	M	\N
20060	L	Bundesamt für Strahlenschutz (BfS), Fachgebiet SW 1.5, Köpenicker Allee 120-130, 10318 Berlin	BfS  LSt TW...	M	\N
15041	15	Endlager für radioaktive Abfälle Morsleben (ERAM),  Am Schacht 105, 39343 Morsleben	KTA - ERAM	M	\N
08210	08	Landesfeuerwehrschule Baden-Württemberg, Führung und Ausbildung von Strahlenspürtrupps, Steinacherstraße 47, 76464 Bruchsal	LFS BW	M	\N
08221	08	Kerntechnischer Hilfsdienst, Am Schröcker Tor 1, 76344 Eggenstein-Leopoldshafen	KHG	M	\N
15020	15	Landesamt für Umweltschutz Sachsen-Anhalt, Fachbereich Medienübergreifender Umweltschutz, Fachgebiet Umweltradioaktivität, Ballerstedter Str. 11, 39606 Osterburg	salm21	M	\N
09011	09	Bayerisches Landesamt für Umwelt, Dienststelle Kulmbach, Schloss Steinenhausen, 95326 Kulmbach	bylm71	M	091801
14020	14	Staatliche Betriebsgesellschaft für Umwelt und Landwirtschaft (BfUL), Dresdner Str. 183, 09131 Chemnitz	snlm21-BfUL	M	\N
13031	13	Energiewerke Nord GmbH - Kernkraftwerk Lubmin/Greifswald, Latzower Str. 1, 17509 Lubmin	KKW	M	\N
13033	13	Energiewerke Nord GmbH - Zwischenlager Nord, Postfach 1125, 17507 Lubmin	KTA	M	\N
03122	03	Niedersächsisches Landesamt für Verbraucherschutz und Lebensmittelsicherheit, Lebensmittelinstitut Braunschweig (Messstellen-Zentrale), Postfach 45 18, 38035 Braunschweig	MStZ	M	\N
03171	03	E.ON Kernkraft GmbH, Gemeinschaftskernkraftwerk Grohnde, 31860 Emmerthal	KKW - KWG	M	\N
03211	03	Konrad Schacht II, Eschestr. 55, 31224 Peine	KTA - Konrad	M	\N
03231	03	Standortzwischenlager Lingen, Am Hilgenberg, 49811 Lingen	SZL	M	\N
14041	14	Forschungszentrum Rossendorf e.V., Postfach 510119, 01314 Dresden	KTA	M	\N
30022	19	Bundesamt für Strahlenschutz (BfS), Fachbereich SW 2, Postfach 11 08, 85758 Oberschleißheim	ZDB-TESTMST	M	\N
02041	02	Auswertungsstelle Hamburg, -Messstelle für Strahlenschutz- im Helmholtz Zentrum München, Deutsches Forschungszentrum für Gesundheit und Umwelt, Max-Brauer-Allee 134, 22765 Hamburg	AWST-HH	M	\N
M1901	19	Messfahrzeug / mobiles Messsystem 1 ZdB-Testnetz	Mobiles Messsystem 1 ZdB-Testnetz	B	\N
M0102	01	Messfahrzeug / mobiles Messsystem 2 Schleswig-Holstein	Mobiles Messsystem 2 SH	B	\N
M0202	02	Messfahrzeug / mobiles Messsystem 2 Hamburg	Mobiles Messsystem 2 HH	B	\N
M0302	03	Messfahrzeug / mobiles Messsystem 2 Niedersachsen	Mobiles Messsystem 2 NI	B	\N
M0402	04	Messfahrzeug / mobiles Messsystem 2 Bremen	Mobiles Messsystem 2 HB	B	\N
M0502	05	Messfahrzeug / mobiles Messsystem 2 Nordrhein-Westfalen	Mobiles Messsystem 2 NW	B	\N
M0602	06	Messfahrzeug / mobiles Messsystem 2 Hessen	Mobiles Messsystem 2 HE	B	\N
M0702	07	Messfahrzeug / mobiles Messsystem 2 Rheinland-Pfalz	Mobiles Messsystem 2 RP	B	\N
M0802	08	Messfahrzeug / mobiles Messsystem 2 Baden-Württemberg	Mobiles Messsystem 2 BW	B	\N
M0902	09	Messfahrzeug / mobiles Messsystem 2 Bayern	Mobiles Messsystem 2 BY	B	\N
M1002	10	Messfahrzeug / mobiles Messsystem 2 Saarland	Mobiles Messsystem 2 SL	B	\N
M1102	11	Messfahrzeug / mobiles Messsystem 2 Berlin	Mobiles Messsystem 2 BE	B	\N
M1302	13	Messfahrzeug / mobiles Messsystem 2 Mecklenburg-Vorpommern	Mobiles Messsystem 2 MV	B	\N
03050	03	Niedersächsisches Landesamt für Verbraucherschutz und Lebensmittelsicherheit, Veterinärinstitut Hannover, Eintrachtweg 17, 30173 Hannover	nslm61	M	032005
03060	03	Niedersächsischer Landesbetrieb für Wasserwirtschaft, Küsten- und Naturschutz , Geschäftsbereich III/ Aufgabenbereich 2, An der Scharlake 39, 31135 Hildesheim	nslm21	M	\N
03070	03	LUFA Nord-West, LUFA Institut für Futtermittel, Jägerstr. 23-27, 26121 Oldenburg	nslm71	M	\N
03080	03	Niedersächsisches Landesamt für Verbraucherschutz und Lebensmittelsicherheit, Lebensmittelinstitut Oldenburg, Martin-Niemöller-Str. 2, 26133 Oldenburg	nslm51	M	032002
03090	03	Niedersächsisches Landesamt für Verbraucherschutz und Lebensmittelsicherheit, Veterinärinstitut für Fische und Fischereierzeugnisse Cuxhaven, Schleusenstr. 1, 27472 Cuxhaven	nslm41	M	032003
03102	03	Niedersächsischer Landesbetrieb für Wasserwirtschaft, Küsten- und Naturschutz (NLWKN), -Betriebsstelle Hannover-Hildesheim-, Geschäftsber. III/Aufgabenber. V, Postfach 10 10 62, 31110 Hildesheim	nsldz1	M	\N
02032	02	Freie und Hansestadt Hamburg, Behörde für Gesundheit und Verbraucherschutz, Institut für Hygiene und Umwelt, Marckmannstr. 129b Haus 6, 20539 Hamburg	hhldz1	M	\N
18002	18	Bundesaufsicht für Endlager	Aufsicht Endlager	M	\N
17003	17	Zentrales Institut des Sanitätsdienstes der Bundeswehr Koblenz, LabAbt. III, LabGrp Radiochemie/IMIS-Messstelle, Andernacher Str. 100, 56070 Koblenz	ZinstSanBw - KOB	M	073003
14040	14	IAF - Radioökologie GmbH Dresden, Wilhelm-Rönsch-Str. 9, 01454 Radeberg	IAF	M	\N
18003	18	Asse GmbH, Am Wald 2, 38139 Remlingen	KTA - Asse	M	\N
08106	08	Auswertestelle Karlsruher Institut für Technologie - Campus Nord, Dosimetrie Labore (SUM-ÜM), 76344 Eggenstein-Leopoldshafen	AWST-KIT	M	\N
09052	09	Auswertungsstelle im Helmholtz Zentrum München, Deutsches Forschungszentrum für Gesundheit und Umwelt (GmbH), Ingolstädter Landstr. 1, 85764 Neuherberg	AWST-BY	M	\N
M0603	06	Messfahrzeug / mobiles Messsystem 3 Hessen	Mobiles Messsystem 3 HE	B	\N
09162	09	AREVA GmbH, PTCCR-G, Radiochemie, Strahlungstechnik, Paul-Gossen-Straße 100, 91052 Erlangen	AREVA NP	M	\N
19002	19	Testmessstelle für IMIS extern, Musterort, Musterstraße	MST2-Schulung (extern)	M	\N
20010	W	Deutscher Wetterdienst, Frankfurter Str. 135, 63067 Offenbach	DWD	M	\N
20020	G	Bundesanstalt für Gewässerkunde, Am Mainzer Tor 1, 56068 Koblenz	BfG	M	\N
20030	H	Bundesamt für Seeschiffahrt und Hydrographie, Bernhard-Nocht-Str. 78, 20359 Hamburg	BSH	M	\N
20120	A	Bundesamt für Strahlenschutz (BfS), Spurenmessstelle Freiburg, Rosastr. 9, 79078 Freiburg	BfS - Spurenmessstelle Freiburg	M	\N
20090	Z	Bundesamt für Strahlenschutz (BfS), Fachbereich SW 2, Rosastr. 9, 79078 Freiburg	BfS - ODL Messnetz-Zentrale	M	\N
08211	08	CBRN-Erkunder Baden-Württemberg	CBRN-BW	M	\N
20000	A	Bundesamt für Strahlenschutz (BfS),  Fachbereich SW 2, Postfach 11 08, 85758 Oberschleißheim	BfS - ZdB	M	\N
20001	A	Bundesamt für Strahlenschutz (BfS),  Fachgebiet SW 2.2, Postfach 11 08, 85758 Oberschleißheim	IMIS-PARK ZdB	M	\N
20100	T	Physikalisch-Technische Bundesanstalt (PTB), Fachbereich Radioaktivität, Bundesallee 100, 38166 Braunschweig	PTB	M	\N
30011	T	Physikalisch-Technische Bundesanstalt (PTB), AG 6.12 Umweltradioaktivität, Postfach 33 45, 38023 Braunschweig	PTB	M	\N
01062	01	Datenzentrale Schleswig-Holstein, Altenholzer Str. 10-14, 24161 Altenholz	shldz1	M	\N
M1402	14	Messfahrzeug / mobiles Messsystem 2 Sachsen	Mobiles Messsystem 2 SN	B	\N
M1502	15	Messfahrzeug / mobiles Messsystem 2 Sachsen-Anhalt	Mobiles Messsystem 2 ST	B	\N
M1902	19	Messfahrzeug / mobiles Messsystem 2 ZdB-Testnetz	Mobiles Messsystem 2 ZdB-Testnetz	B	\N
M0101	01	Messfahrzeug / mobiles Messsystem 1 Schleswig-Holstein	Mobiles Messsystem 1 SH	B	\N
M0201	02	Messfahrzeug / mobiles Messsystem 1 Hamburg	Mobiles Messsystem 1 HH	B	\N
M0301	03	Messfahrzeug / mobiles Messsystem 1 Niedersachsen	Mobiles Messsystem 1 NI	B	\N
M0401	04	Messfahrzeug / mobiles Messsystem 1 Bremen	Mobiles Messsystem 1 HB	B	\N
M0501	05	Messfahrzeug / mobiles Messsystem 1 Nordrhein-Westfalen	Mobiles Messsystem 1 NW	B	\N
M0601	06	Messfahrzeug / mobiles Messsystem 1 Hessen	Mobiles Messsystem 1 HE	B	\N
M0701	07	Messfahrzeug / mobiles Messsystem 1 Rheinland-Pfalz	Mobiles Messsystem 1 RP	B	\N
M0801	08	Messfahrzeug / mobiles Messsystem 1 Baden-Württemberg	Mobiles Messsystem 1 BW	B	\N
M0901	09	Messfahrzeug / mobiles Messsystem 1 Bayern	Mobiles Messsystem 1 BY	B	\N
M1001	10	Messfahrzeug / mobiles Messsystem 1 Saarland	Mobiles Messsystem 1 SL	B	\N
M1101	11	Messfahrzeug / mobiles Messsystem 1 Berlin	Mobiles Messsystem 1 BE	B	\N
M1201	12	Messfahrzeug / mobiles Messsystem 1 Brandenburg	Mobiles Messsystem 1 BB	B	\N
M1301	13	Messfahrzeug / mobiles Messsystem 1 Mecklenburg-Vorpommern	Mobiles Messsystem 1 MV	B	\N
M1401	14	Messfahrzeug / mobiles Messsystem 1 Sachsen	Mobiles Messsystem 1 SN	B	\N
M1501	15	Messfahrzeug / mobiles Messsystem 1 Sachsen-Anhalt	Mobiles Messsystem 1 ST	B	\N
M1601	16	Messfahrzeug / mobiles Messsystem 1 Thüringen	Mobiles Messsystem 1 TH	B	\N
M1202	12	Messfahrzeug / mobiles Messsystem 2 Brandenburg	Mobiles Messsystem 2 BB	B	\N
17001	17	Zentrales Institut des Sanitätsdienstes der Bundeswehr München, FGB Radiochemie und Kernstrahlenmesstechnik, Ingolstädter Landstr. 102, 85748 Garching-Hochbrück	ZinstSanBw - MCH	M	093004
09301	09	Universität Regensburg U.R.A.-Laboratorium, Universitätsstr.31, 93040 Regensburg	URA	M	\N
18001	18	Endlager für radioaktive Abfälle Morsleben (ERAM)	KTA - ERAM	M	\N
08083	08	Umweltministerium  Baden-Württemberg, Postfach 10 34 39, 70029 Stuttgart	BW REI-AB	M	\N
17002	17	Zentrales Institut des Sanitätsdienstes der Bundeswehr Kiel, Laborabteilung III, Lebensmittelchemie/Ökochemie, Kopperpahler Allee 120, 24119 Kronshagen	ZinstSanBw - KIEL	M	013002
\.


