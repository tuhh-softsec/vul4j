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

COPY ortszusatz (ozs_id, ortszusatz) FROM stdin;
FNE1291	Neckar (Einlaufkanal GKN), ca. bei km 129,1; Neckarwestheim
FPM0000	Prüm
FNE1998	Neckar km 199,8; Deizisau
FNE0030	Neckar km  3,0; Mannheim
FDO5826	Donau km 2582,60; Ulm
FRH1727	Rhein km 172,90; Weil
FRH2488	Rhein km 248,80; Weisweil
FRH0229	Rhein km 22,90; Öhningen
FRH3592	Rhein km 359,20; Karlsruhe
FRH4247	Rhein km 424,70; Mannheim
FRH3897	Rhein (Einlaufkanal KKP), ca. bei km 389,20; Philippsburg
FRH1080	Rhein (Einlaufkanal KKL), ca. bei km 108,00; Leibstadt (CH)
FRK0004	Rheinniederungskanal; Leopoldshafen; PE-Stelle oberhalb Einleitung KfK
FHK0005	Hirschkanal; Linkenheim-Hochstetten; PE-Stelle unterhalb KIT-CN
FRH1703	Rhein km 170,30; Weil
FRH1704	Rhein km 170,30; Weil
FRH1728	Rhein km 172,97; Weil
FRH1729	Rhein km 172,97; Weil
FRH2276	Rhein km 227,60; Breisach
FSP0006	Spree km 0,60; Berlin; PE-Stelle Nr. 11009, Sophienwerder
FGV0005	Gnevsdorfer Vorfluter; Rühstädt; PE-Stelle in Höhe Gemarkung Abbendorf
FOC0005	Ochtum; Bremen
FGE0005	Geestezufluss; Bremerhaven
FEL6098	Elbe km 609,80; Hamburg; PE-Ort Bunthaus
FAT0005	Alster; Hamburg; PE-Stelle bei Krugkoppelbrücke
FBI0005	Bille; Hamburg; PE-Stelle Sperrwerk
FRH4780	Rhein km 478,00; Riedstadt
FMA0280	Main km 28,00; Frankfurt am Main
FLH0050	Lahn km 5,00; Lahnau
FEZ0256	Elz km 25,60; Kenzingen
FEC0005	Eschach; Zimmern ob Rottweil
FLE0015	Lech km 1,50; Niederschönenfeld; PE-Ort Feldheim; PE-Stelle rechtes Ufer
FMA0670	Main km 67,00; Kahl am Main; Messstation, rechtes Ufer
FMA3560	Main km 356,00; Haßfurt
FSC0005	Seibertsbach; Mitterteich
FIR1246	Isar km 124,60; Neufahrn; PE-Ort Grüneck; PE-Stelle unter der Isarbrücke
FIR2600	Isar km 260,00; Mittenwald
FIR1295	Isar km 129,50; Garching; PE-Stelle am linken Ufer
FIR1337	Isar km 133,70; Ismaning; PE-Stelle unter der Brücke der B471
FDO2700	Donau km 2270,00; Osterhofen
FDO2098	Donau km 2209,80; Obernzell; PE-Stelle am linken Ufer
FDO2038	Donau km 2203,80; Untergriesbach; Messstation, Trenndamm, KW Jochenstein
FRE0986	Regen km 98,60; Chamerau
FNA0180	Naab km 18,00; Duggendorf
FDO3540	Donau km 2354,00; Pfatter
FRE0165	Regen km 16,50; Regenstauf; PE-Stelle am linken Ufer
FVI0062	Vils km 6,20; Burglengenfeld; PE-Ort Dietldorf
FNA0368	Naab km 36,80; Teublitz
FPN0065	Pegnitz km 6,50; Nürnberg; PE-Stelle am Ledersteg
FRN0070	Rednitz km 7,00; Nürnberg
FMA2412	Main km 241,20; Erlabrunn; PE-Stelle bei Staustufe am linken Ufer
FMA2590	Main km 259,00; Randersacker; Kraftwerk-OW
FIK0049	Illerkanal km 4,90; Neu-Ulm
FDO5814	Donau km 2581,40; Neu-Ulm; PE-Stelle am li. Ufer, Böfinger Halde, KW-UW
FLE1672	Lech km 167,20; Füssen
FTA0620	Tauber km 62,00; Weikersheim
FKO0204	Kocher km 20,37; Hardthausen am Kocher
FMU0116	Murg km 11,62; Rastatt
FNE2080	Neckar km 208,0; Wendlingen
FDO5908	Donau km 2590,80; Ulm-Wiblingen
T030013	TWV Kissenbrueck, Schachtanlage Asse II
T030014	TWV Moenchenvahlberg, Schachtanlage Asse II
T030015	TWV Remlingen, Schachtanlage Asse II
T030016	TWV Falkenheim, Schachtanlage Asse II
T040001	WW  Stadtwerke Bremen
T050001	WW  Städtische Werke Essen
T050002	WW  Beverungen
T050003	WW  Bielefeld
T050004	WW  Breitenbachtalsperre
T050005	WW  Dreilagerbachtalsperre
T050006	WW  Dueren
T050007	WW  Ennepetalsperre
T050008	WW  Genkeltalsperre
T020019	WW  Walssoerfer
T020020	WW  Wilhelmsburg
T030001	WW  Stadtwerke Helmstedt
T050014	WW  Münster
M090018	MVA  Schweinfurt
M090019	MVA  Schwabach
M090020	MKW  München-Nord Block 1+2
FSH0475	Salzach km 47,50; Laufen; Messstation am linken Ufer
FIR1620	Isar km 162,00; Pullach
FIN1945	Inn km 194,50; Raubling; PE-Ort Kirchdorf;PE-Stelle recht.Ufer b. Brücke
FIN0042	Inn km 4,20; Passau; PE-Stelle bei Trennpfeiler Kraftwerk Ingling
FIR0091	Isar km 9,10; Plattling; PE-Stelle bei Brücke über B8 i. H. Brückenmitte
FAM0360	Altmühl km 36,00; Dietfurt/Altmühl;PE-Ort Grögling,PE-Stelle Brück.mitte
FNA0872	Naab km 87,20; Wernberg-Koeblitz;PE-Stelle unterh. Brücke der B14, Mitte
FMA3882	Main km 388,20; Hallstadt; PE-Stelle bei Brücke, Mitte
FRZ0324	Regnitz km 32,40; Hausen; Messstation, Kraftwerk-OW, rechtes Ufer
FSL0241	Saale km  24,1; Koeditz; PE-Stelle Joditz Mühle
FIL0021	Iller km 2,14; Neu-Ulm
FWT0438	Wertach km 43,80; Ettringen
T030011	Überlauf der TWV Grossvahlberg Schachtanlage Asse II
T030012	TWV Schacht I, Schachtanlage Asse II
T020010	WW  Grosshansdorf
T020011	WW  Kaltehofe
T020012	WW  Langenhorn
T020013	WW  Lohbruegge
T020014	WW  Neugraben
T020016	WW  Schnelsen
T020017	WW  Stellingen
T020018	WW  Suederelbmarsch
T070003	WW  Kaiserslautern
T070004	WW  Koblenz
T070005	WW  Landau-Arzheim (alter Hochbehälter)
T070006	WW  Mainz-Petersau
T050013	WW  Krefeld
T080003	WW  Dogern, KKW Beznau, KKW Leibstadt (CH)
T080004	WW  Albbruck, KKW Beznau, KKW Leibstadt (CH)
T050009	WW  Hagen
T050010	WW  Haltern
T050011	WW  Hemer
T050012	WW  Kerspe-, Herburghauser Talsperre
R1W0224	Referenzbauernhof 1 zu W0224
R2W0224	Referenzbauernhof 2 zu W0224
R1W0868	Referenzbauernhof 1 zu W0868
R2W0868	Referenzbauernhof 2 zu W0868
R1W0501	Referenzbauernhof 1 zu W0501
R2W0501	Referenzbauernhof 2 zu W0501
SHA1300	Haussee
C1W0035	Klimagarten zu W0035
SHA1301	Haussee, Feldberg
T030002	WW  Mischstation Goettingen
T030003	WW  Helmstedt, Endlager Bartensleben (DDR)
T030004	WW  Kirchohsen, Kernkraftwerk Grohnde
T030005	WW  Hagenohsen, Kernkraftwerk Grohnde
T030006	WW  Hameln, Kernkraftwerk Grohnde
T030007	WW  Rehme, Kernkraftwerk Grohnde
T030008	WW  Lingen, Kernkraftwerk Lingen
T030009	WW  Dollern, Kernkraftwerk Stade
T030010	WW  Maiweise, Landessammelstelle Steyerberg
T060011	WW  Hattersheim
T060012	WW  Hergershausen
T060013	WW  Inheiden
T060014	WW  Kassel
T060015	WW  Kressenborn
T020015	WW  Nordheide
FPM0321	Prüm; km 32,1; Wiersdorf
SSH1300	Stettiner Haff
FNE0773	Neckar (Einlaufkanal KWO), km 77,35; Obrigheim
FDN0000	Dhron
FDN0320	Dhron; km 32; Neumagen-Dhron
FWI0000	Wied
FWI0990	Wied; km 99; Neuwied-Niederbieber
SKT0700	Krombachtalsperre
SKT0701	Krombachtalsperre; Rehe
SSH1301	Stettiner Haff; Mönkebude
SSH1302	Stettiner Haff; Ueckermünde
SLE1300	Lewitzer Teiche
SLE1301	Lewitzer Teiche; Neuhof
SFC1300	Fischteiche Canow
M130002	MVA Stavenhagen
D030003	Hannover-Altwarmbüchen
D030002	Braunschweig-Watenbüttel
D030001	Brake
KO03004	KOA Brake
KO03003	KOA Hildesheim
KO03002	KOA Dransfeld
KO03001	KOA Braunschweig
M030002	KW Buschhaus Helmstedt Offleben
M030001	MVA Hameln
T070031	WW Dudenhofen
T070032	WW Germersheim
FNE0770	Neckar (Auslaufkanal KWO), km 77,00; Obrigheim
T020006	WW  Südliche Fassung Curslack
T020007	WW  Nördliche Fassung Curslack
T020008	WW  Falkenstein
T020009	WW  Grossensee
T070002	WW  Hoppstaedten
T080001	WW  Neckartailfingen
T080002	WW  Waldshut, KKW Beznau, KKW Leibstadt (CH)
T090023	WW  Marktsteft, Kernkraftwerk Grafenrheinfeld
T090024	WW  Weyer, Kernkraftwerk Grafenrheinfeld
T090025	WW  Volkach, Kernkraftwerk Grafenrheinfeld
T090029	WW  Marksteft, Kernkraftwerk Grafenrheinfeld
T090030	WW  Buchmuehle, Kernkraftwerk Gundremmingen
T050015	WW  Oleftalsperre
T050016	WW  Paderborn
T050017	WW  Perlenbachtalsperre
T050018	WW  Sengbachtalsperre
T050019	WW  Wahnbachtalsperre
T050020	WW  Juelich, Kernforschungsanlage Juelich
T050021	WW  Hambach, Kernforschungsanlage Juelich
T050022	WW  KFA Juelich, Kernforschungsanlage Juelich
T050023	WW  Kalkar, SNR Kalkar
T050024	WW  Niedermoermter, SNR Kalkar
T050025	WW  Haltern, THTR-300 Hamm-Uentrop
T050026	WW  Hambach, Kernforschungsanlage
T050027	WW  Juelich, Kernforschungsanlage
T060001	WW  Allendorf
T060002	WW  Biedenkopf
T060003	WW  Dientzenbach
T060004	WW  Dillenburg
T060005	WW  Eschollbruecken
T060006	WW  Fulda
T060007	WW  Goldstein
T060008	WW  Haarhausen
T060009	WW  Hanau
T060025	Gruppen-WW Allmendfeld, Hess. Kernkraftwerk Biblis A/B
T070001	WW  Gillenfeld
T090010	TWV Maxhuette-Haidhof
T090011	TWV Mitwitz-Steinach
T090012	TWV Bayerischer Wald FBW GPW   Moos
T090013	WW  München-Schwabing
T090018	TWV Schwarzenkopfhütte
T090019	TWV Franken Uehlfeld
T090020	GWV Unterköblitz
T090021	WW  Wuerzburg
T080005	WW  Laufenburg, KKW Beznau, KKW Leibstadt (CH)
T080006	WW  KFK Karlsruhe, Kernforschungszentrum Karlsruhe
T080007	WW  Leopoldshafen, Kernforschungszentrum Karlsruhe
T080008	WW  Linkenheim-Hochstetten, Kenrforschungszentrum Karlsruhe
T080009	WW  Friedrichstal, Kernforschungszentrum Karlsruhe
T080010	WW  Linkenheim-Hochstetten, Kernforschungszentrum Karlsruhe
T080011	WW  Breisach am Rhein, Kernkraftwerk Fessenheim
T080012	WW  Hartheim, Kernkraftwerk Fessnheim
T080013	WW  Neuenburg am Rhein, Kernkraftwerk Fessenheim
T080014	WW  Hausen a. d. Mur, Kernkraftwerk Fessenheim
T080015	WW  Weisweil, Kernkraftwerk KWS 1 Wyhl
T080016	WW  Wyhl, Kernkraftwerk KWS 1 Wyhl
T080017	WW  Rheinhauser-Obernhausen, Kernkraftwerk Neckarwestheim
T060016	WW  Lauterbach
T060017	WW  Petersaue
T060018	WW  Praunheim
T060019	WW  Schoenauer Hof
T060020	WW  Wiesbaden-Schierstein
T060021	WW  Wattenheim-Nordheim, Kernkraftwerk Biblis A/B
T060022	WW  Ried, Kernkraftwerk Biblis A/B
T060023	WW  Eich, Kernkraftwerk Biblis A/B
T060024	WW  Guntersblum, Kernkraftwerk Biblis A/B
T090007	TWV Kirchenlamitz
T090008	Zentrale WV Lauben
T090009	TWV Martinlamitz
K050008	KLA Gütersloh
K050009	KLA Herford
K050010	KLA Köln
K050011	KLA Münster
K050012	KLA Wuppertal-Buchenhofen
K060001	KLA Bad Hersfeld, Kreisstadt
T090031	WW  Dillingen a. d. Donau, Kernkraftwerk Gundremmingen
T090032	WW  Niederstotzingen 1, Kernkraftwerk Gundremmingen
T090033	WW  Niederstotzingen 4, Kernkraftwerk Gundremmingen
T090034	WW  Niederstotzingen 6, Kernkraftwerk Gundremmingen
T090035	WW  Schotthof 2, Kernkraftwerk Gundremmingen
T090036	WW  Schotthof 3, Kernkraftwerk Gundremmingen
T090037	WW  Schotthof 5, Kernkraftwerk Gundremmingen
T090038	TVA Landau a. d. Isar, Kernkraftwerk Isar 1+2
T090039	TVA Niederaichbach, Kernkraftwerk Isar 2
T080018	WW  Moertelstein, Kernkraftwerk Obrigheim
T080019	WW  Talheim, Kernkraftwerk Obrigheim
T080020	WW  Obrigheim, Kernkraftwerk Obrigheim
T070007	WW  Montabaur
T070008	WW  Neuwied
T070009	WW  Osthofen
T070010	WW  Pruem
T070011	WW  Riveristalsperre
T070012	WW  Speyer
T070013	WW  Steinbachtalsperre
T070014	WW  Worms
T070015	WW  Eich, Kernkraftwerk Biblis A/B
T070016	WW  Guntersblum, Kernkraftwerk Biblis A/B
T070017	WW  Berghausen, Kernkraftwerk Philippsburg
T070018	WW  Speyer, Kernkraftwerk Philippsburg
T070019	WW  Harthausen, Kernkraftwerk Philippsburg
T090005	WW  Duernsricht Schmidgaden
T090006	FWV Franken
K090022	KLA Eichstätt
K090023	KLA Erbendorf
K050007	KLA Düsseldorf-Süd
K090077	KLA Schweinfurt
K090079	KLA Steinwiesen
K090080	KLA Straubing
T090022	WW  Mainau-Sennfeld, Kernkraftwerk Grafenrheinfeld
K030006	KLA Goslar
K030007	KLA Guemmerwald
K030008	KLA Hameln
K030009	KLA Hannover
K030010	KLA Hildesheim
K030011	KLA Holzminden
K030012	KLA Katlenburg
K030013	KLA Northeim
K030014	KLA Osnabrück
K030015	KLA Osteroda
K030016	KLA Soltau
K030017	KLA Spechtshorn
K030018	KLA Uelzen
K030019	KLA Wolfenbuettel
K030020	KLA Wolfsburg
K040001	KLA Farge
K050001	KLA Aachen
K050002	KLA Bielefeld
K050003	KLA Bonn
K050004	KLA Bonn-Lessenich
K050005	KLA Bottrop-Bernemuendung
K050006	KLA Düsseldorf-Nord
T090014	WW  Nürnberg Muehlhof
T090015	FWV Oberfranken
T090016	WW  Regensburg
T090017	TWV Rieblich-Zeyern
T080021	WW  Oberhausen, Kernkraftwerk Philippsburg 1 + 2
T080024	WW  Linkenheim, Kernforschungszentrum Karlsruhe
T090001	WW  Aschaffenburg
T090002	WW  Stadtwerke Augsburg
T090003	WV  Beidl
T090004	WW  Burglengenfeld
K090013	KLA Bamberg
K090014	KLA Bayreuth
K090015	KLA Buchloe
K090016	KLA Cham
K090017	KLA Deggendorf
K090018	KLA Dillingen
K090019	KLA Dinkelsbühl
K090020	KLA Donauwörth
K090021	KLA Eggenfelden
K090073	KLA Schnaittenbach
K090074	KLA Schrobenhausen
K090075	KLA Schwandorf
K090076	KLA Schwarzach
M090013	MHKW Würzburg
K060002	KLA Bad Nauheim
K060003	KLA Ebsdorfergrund
K060004	KLA Frankfurt (Main)- Niederrad
K060005	KLA Friedberg
T110001	WW  Beelitzhof, Berlin
T110002	WW  Frohnau, Berlin
T110003	WW  Jungfernheide, Berlin
T110004	WW  Kladow, Berlin
T110005	WW  Riemenmeisterfenn, Berlin
T110006	WW  Spandau, Berlin
SNE1202	Nehmitzsee; Rheinsberg; PE-Stelle am Einlaufbauwerk des KKR
FSP0092	Spree km 9,20; PE-Stelle Nr. 11002, Einmündung Landwehrkanal
T110016	Grundwasser Köpenick ( Florian-Geyer-Straße )
FDO5517	Donau (Einlaufkanal KRBII), ca. bei km 2551,60; Gundelfingen
FDO5485	Donau km 2548,50; Gundelfingen
FDO5490	Donau (Auslaufkanal 1 KRBII), ca. bei km 2549,00; Gundremmingen
FDO5503	Donau (Auslaufkanal 2 KRBII), ca. bei km 2550,30; Gundremmingen
FIR1303	Isar km 130,30; Garching
FIR1298	Isar km 129,80; Garching
FMO0975	Mosel km 97,50; Reil
FRH0901	Rhein km 90,10; Küssaberg-Reckingen; PE-Stelle oberhalb KKL
FLP1424	Lippe km 142,40; Lippetal; PE-Stelle oberhalb THTR
FLP1367	Lippe (Einlaufbauwerk THTR), ca. bei km 136,70; Hamm
T110007	WW  Steinstücken, Berlin
T110008	WW  Tegel, Berlin
T110009	WW  Tiefwerder, Berlin
T110010	WW  Beelitzhof, Forschungsreaktor HMI
K020001	KLA Hamburg-West
K020002	KLA Koehlbrandhoeft
K020003	KLA Stellinger Moor
K020004	KLA Volksdorf
K030001	KLA Aurich
K030002	KLA Braunschweig
K030003	KLA Celle
K030004	KLA Gifhorn
K030005	KLA Goettingen
K090004	KLA Ansbach
K090005	KLA Augsburg
K090006	KLA Bad Aibling
K090007	KLA Bad Königshofen
K090008	KLA Bad Kissingen
K090009	KLA Bad Reichenhall
K090010	KLA Bad Steben
K090011	KLA Bad Wörishofen
K090012	KLA Bad Windsheim
K090002	KLA Abwasserverband Isar-Loisach Gruppe
K090003	KLA Aichach
K090065	KLA Parkstein
K090066	KLA Passau
K090067	KLA Pfaffenhofen
K090068	KLA Röttenbach-Hemhofen
K090069	KLA Regensburg
K090070	KLA Rosenheim
K090071	KLA Ruhstorf
K090072	KLA Schierling
K060021	Betriebs-KLA Hofgut, Wickstadt
K060022	KLA Wiesbaden
K060023	KLA Wiesbaden-Biebrich
K070001	KLA Hauenstein
K070002	KLA Kaiserslautern
K070003	KLA Landstuhl
K070004	KLA Mainz
K070005	KLA Trier
K090001	KLA Abensberg
K090048	KLA Marktredwitz
K090049	KLA Memmelsdorf
K090050	KLA Memmingen
K090051	KLA Miesbach
K090052	KLA Miltenberg
K090053	KLA Mittlere Schmuttertalgruppe
K090054	KLA Murnau
K090055	KLA München I
K090056	KLA München II
K090057	KLA Nabburg
K090058	KLA Nersingen
K090059	KLA Nersingen/Unterfahlheim
K090060	KLA Neu/Altötting
K090061	KLA Neumarkt
K090062	KLA Neustadt
K090063	KLA Nürnberg I
K090064	KLA Nürnberg II
M090012	MHKW Rosenheim
K090081	KLA Thüngersheim
K090082	KLA Tirschenreuth
K090083	KLA Traunstein
K090024	KLA Erlangen
K090025	KLA Forchheim
K090026	KLA Freising
K060006	Betriebs-KLA Gemüseverarbeit.  Fritzlar
K060007	KLA Fulda
K060008	KLA Fulda, Stadt
K060009	Betriebs-KLA Zuckerfabrik, Gross-Gerau
K060010	KLA Hachborn
K060011	KLA Hanau
K060012	Betriebs-KLA Gemüseverarbeit.  Huenfeld
K060013	KLA Kassel
K060014	KLA Klein-Welzheim
K060015	KLA Limburg
K060016	KLA Marburg
K060017	Betriebs-KLA Tierkörperbeseit. Niederwoellstadt
K060018	Betriebs-KLA Tierkörperbeseit. Schwalmtal-Hofgarten
K060019	Betriebs-KLA Zuckerfabrik, Wabern
K060020	KLA Wetzlar
K110001	KW  Marienfelde
K110002	KW  Ruhleben
K110003	KA  Schönerlinde
K110004	KA  Falkenberg
D110001	Wannsee
D110002	Lübars
M110001	MVA  Ruhleben
T110012	Grundwasser Tempelhof ( M. von Richthofen Straße )
T110013	Grundwasser Wilmersdorf ( Am Volkspark )
T110014	Grundwasser Köpenick ( Oberspreestraße )
M110002	Klärschlammverbrennung Marienfelde
M110003	Klärschlammverbrennung Ruhleben
D090014	Starkershofen
D090015	Großlappen
D090016	Stockstadt
D090017	Außernzell
D090018	Aurach
D090019	Diespeck-Dettendorf
D090020	Asbach
D090021	Wörth
D090022	Karlstadt
D090023	Gallenbach
D090024	Rothmühle
M090014	MKW  Schwandorf
M090015	MHW  Neurahrn
M090016	MHW  Zirndorf
M090017	SVM  Ebenhausen
FNH1215	Nahe km 121,5; Bingen-Dietersheim
SSR1400	Speicher Radeburg 2
T010001	WW  Kirchohsen, Kernkraftwerk Grohnde
T010002	WW  Geesthacht, Kernkraftwerk Kruemmel
T010003	WW  Tesperhude, Kernkraftwerk Kruemmel
T010004	WW  Dollern, Kernkraftwerk Stade
T020001	WW  Bauersberg
T050028	WW  Aggertalsperre
T110011	WW  Friedrichshagen
D050002	Detmold
D050003	Neuss
D050004	Hürth
D050005	Münster
M090001	MHKW Bamberg
M090002	MPA  Burgau
M090003	MHKW Geiselbullach
M090004	MHKW Ingolstadt
M090005	MKW  Landshut
M090006	MVA  Marktoberdorf
M090007	MKW  München-Nord
M090008	MKW  München-Süd
M090009	MVA  Nürnberg
M090010	MHKW Kempten
M090011	KVA  Neu-Ulm
T010005	WW  Himmelpforten, Kernkraftwerk Stade
K090085	KLA Weiden
K090094	KLA ZV Obere Werntalgemeinden
K090078	KLA Starnberg
M080004	HMVA Göppingen
M070001	RVA Ludwigshafen
M060001	(SEVA) Frankfurt/M. Sindlingen
M060002	(MVA)  Frankfurt/M.
M060003	(MVA)  Kassel
M050001	(MVA)  Iserlohn
M050002	(MVA)  Bielefeld
M050003	(MVA)  Essen
M050004	(MVA)  Herten
K090095	KLA ZV Ottobeuren-Hawangen
D090001	Burgau
D090002	Gosberg
D090003	Grossmehring
D090004	Hopferstadt
D090005	Mathiaszeche
T020002	WW  Bergedorf
T020003	WW  Billbrook
T020004	WW  Billwerder Insel
T020005	WW  Bostalbek
FNE0736	Neckar km 73,6; Guttenbach
FNE0746	Neckar km 74,6; Binau
FNE0757	Neckar km 75,7; Binau
FNE0765	Neckar km 76,5; Binau
FNE0772	Neckar km 77,2; Binau
FNE0858	Neckar km 85,8; Neckarzimmern
FNE0860	Neckar km 86,0; Neckarzimmern
FNE0862	Neckar km 86,2; Neckarzimmern
FNE0938	Neckar km 93,9; Gundelsheim
FNE0939	Neckar km 93,9; Gundelsheim
FNE0941	Neckar km 94,1; Gundelsheim
FNE1040	Neckar km 104,0; Kochendorf
FNE1041	Neckar km 104,0; Kochendorf
FNE1042	Neckar km 104,2; Kochendorf
FNE1078	Neckar km 107,8; Heilbronn
FNE1079	Neckar km 107,9; Heilbronn
FNE1106	Neckar km 110,6; Heilbronn
FOD5532	Oder km 553,20; Eisenhüttenstadt
FOD5533	Oder km 553,20; Eisenhüttenstadt
FOD5534	Oder km 553,20; Eisenhüttenstadt
FOD6675	Oder km 667,50; Hohensaaten
FOD6676	Oder km 667,50; Hohensaaten
FOD6677	Oder km 667,50; Hohensaaten
FPE0961	Peene km 96,10; Anklam
FPE0962	Peene km 96,10; Anklam
FPE0963	Peene km 96,10; Anklam
FOS0001	Ostsee, Mündung Nord-Ostsee-Kanal; Kiel-Holtenau
M130001	MVA Ludwigslust
FOS0002	Ostsee, Mündung Nord-Ostsee-Kanal; Kiel-Holtenau
FOS0268	Untertrave (Ostsee) km 26,90; Travemünde
FSL1045	Saale km 104,5; Halle/Planena
FSL1046	Saale km 104,5; Halle/Planena
FSL1047	Saale km 104,5; Halle/Planena
FSA0050	Saar km 5,0; Kanzem
FSP0272	Spree km 27,20; Berlin
FSP0273	Spree km 27,20; Berlin
FSP0274	Spree km 27,20; Berlin
FSP0746	Spree km 74,70; Fürstenwalde
FSP0747	Spree km 74,70; Fürstenwalde
FSP0748	Spree km 74,70; Fürstenwalde
FWE1632	Weser km 163,20; Rinteln
FWE1633	Weser km 163,20; Rinteln
FWE3294	Weser km 329,40; Langwedel
FWE3295	Weser km 329,40; Langwedel
FWE0691	Weser km 69,00; Höxter
FWE0690	Weser km 69,00; Höxter
FWE4300	Weser (Unterweser) km 430,00; Blexen
FRH2347	Rhein km 234,75; Breisach
FRH2348	Rhein km 234,75; Breisach
FRH4435	Rhein km 443,40; Worms
FRH4445	Rhein km 444,50; Worms
FRH4466	Rhein km 446,60; Worms
FRH5903	Rhein km 590,30; Koblenz
FRH5904	Rhein km 590,30; Koblenz
FRH5054	Rhein km 505,40; Schierstein
FRH5055	Rhein km 505,40; Schierstein
FRH6382	Rhein km 638,20; Oberwinter
FRH6383	Rhein km 638,20; Oberwinter
FRH7403	Rhein km 740,30; Bockum
FRH7431	Rhein km 743,10; Bockum
FRH7468	Rhein km 746,80; Bockum
FRH7489	Rhein km 748,90; Bockum
FRH8140	Rhein km 814,00; Wesel
FRH8141	Rhein km 814,00; Wesel
FRH6084	Rhein km 608,40; Weissenthurm
FRH6118	Rhein km 611,80; Andernach
FRH6210	Rhein km 621,00; Brohl; PE-Stelle Brohler Hafen
FJD0005	Jadebusen; Wilhelmshaven; PE-Stelle Ostmole
FEL6288	Elbe km 628,80; Hamburg; PE-Ort Seemannshöft
FEL6298	Elbe km 629,80; Hamburg; PE-Stelle Steendiekkanal
FEM2172	Ems km 217,20; Greven
FKI0005	Kinzig; Hanau; PE-Stelle kurz vor Mündung in Main
FNI0005	Nidda; Frankfurt am Main; PE-Ort Niederrad;PE-Stelle vor Mündung in Main
FSD0005	Schwarzbach/Hessen; Trebur; PE-Ort Trebur-Astheim
FSP0039	Spree km 3,90; Berlin; PE-Stelle Nr. 11004, WW Jungfernheide
FHA0005	Havel; Steinförde
FMH0005	Müritz-Havel-Kanal; Strasen; PE-Stelle 50 m oberhalb Einleitung KKR
FMH0006	Müritz-Havel-Kanal; Strasen; PE-Stelle 50 m unterhalb Einleitung KKR
FSP0141	Spree km 14,10; Berlin; PE-Stelle Nr. 11001,ca. 50m unterh. Moltkebrücke
FLI0755	Leine km 75; Northeim; PE-Ort Leineturm
FRH3925	Rhein km 392; Römerberg (bei Philippsburg); PE-Stelle unterhalb KKP
FRH4515	Rhein km 451; Worms; PE-Ort Rheindürkheim; PE-Stelle oberhalb KKB
FRH4575	Rhein km 457; Hamm; PE-Stelle unterhalb KKB
FRH6025	Rhein km 602; Urmitz; PE-Stelle oberhalb KKM
FRH6083	Rhein km 608; Neuwied; PE-Stelle unterhalb KKM
FMO2375	Mosel km 234 - 240 (Mittelwert km 237); Perl; PE-Ort Besch
FSN0005	Steinaubach; Ellweiler;PE-Stelle 1km unterh. ehem. Uranaufbereitungsanl.
FZI0045	Ziese (-> Dänische Wiek) km 4; Neu Boltenhagen; PE-Ort Lodmannshagen
FZI0055	Ziese (-> Dänische Wiek) km 5; Kemnitz
FZI0085	Ziese (-> Peene-Strom) km 8; Groß Ernsthof
FEI1099	Eider km 109,9; Eidersperrwerk
FWE1225	Weser km 122; Emmerthal; PE-Ort Grohnde
FWE1255	Weser km 125; Emmerthal; PE-Stelle oberhalb KWG
FWE1256	Weser km 125; Emmerthal; PE-Stelle unterhalb Auslauf KWG
FWE1275	Weser km 127; Emmerthal; PE-Ort Kirchohsen
FWE1355	Weser km 135; Hameln
FWE1475	Weser km 147; Hessisch-Oldendorf
FWE4105	Weser km 410; Brake; PE-Ort Golzwarden
FWE4115	Weser km 411; Brake; PE-Ort Golzwarden
FWE4116	Weser km 411; Brake; PE-Ort Fähre Golzwarden
FWE4155	Weser km 415; Stadland; PE-Ort Schweiburg
FWE4175	Weser km 417; Stadland; PE-Ort Esensham
FWE4194	Weser km 419; Stadland; PE-Ort Esensham
FWE4195	Weser km 419; Stadland; PE-Stelle oberhalb KKU
FWE4196	Weser km 419; Stadland; PE-Stelle unterhalb Auslauf KKU
FWE4245	Weser km 424; Nordenham
FWE4265	Weser km 426; Nordenham
FEM0842	Ems km 84,20; Lingen; PE-Stelle oberhalb KKE
FEM0847	Ems km 84,70; Lingen; PE-Ort Hanekenfähr
FEM0862	Ems km 86,20; Lingen; PE-Stelle unterhalb Auslauf KKE
FEM0966	Ems km 96,60; Lingen; PE-Ort Altenlingen
FEM1064	Ems km 106,30; Geeste; PE-Ort Dalum
FEM1795	Ems km 179; Haren
FEL6435	Elbe km 643; Wedel
FEL6455	Elbe km 645; Steinkirchen; PE-Ort Fährmannssand
FEL6545	Elbe km 654; Stade; PE-Stelle unterhalb Auslauf KKS
FEL6605	Elbe km 660; Stade; PE-Ort Pagensand
FSG0165	Seege km 16; Gartow; PE-Ort Meetschow
FSK0005	Stichkanal; Salzgitter; PE-Stelle Hafen
FSA1045	Saar km 104; Kleinblittersdorf; PE-Ort Hanweiler
FRH2277	Rhein km 227,60; Breisach
FOS0269	Untertrave (Ostsee) km 26,90; Travemünde
FIR0529	Isar km 52,86; Niederviehbach; PE-Stelle Staustufe Gummering
FIR0605	Isar km 60,60; Essenbach;PE-Stelle Staustufe Niederaichbach -Einlauf KKI
FIR0600	Isar km 60,00; Niederaichbach
FIR0609	Isar (Einlaufbauwerk KKI-1), ca. bei km 60,85; Essenbach
FIR0608	Isar (Einlaufkanal KKI-2), ca. bei km 60,68; Essenbach
FIR0607	Isar (Auslaufkanal KKI-2), ca. bei km 60,63; Essenbach
FIR0606	Isar (Auslaufkanal KKI-1), ca. bei km 60,62; Niederaichbach
FIS0005	linker Isarseitenkanal bei Staustufe Niederaichbach; Niederaichbach
FLM0005	Längenmühlbach; Niederaichbach
FMA3246	Main km 324,60; Grafenrheinfeld; PE-Stelle bei Einlauf KKG
FMA3242	Main km 324,20; Grafenrheinfeld; PE-Stelle bei Auslauf KKG
FMA3235	Main km 323,60; Bergrheinfeld; PE-Stelle bei Staustufe Garstadt
FMA3160	Main km 316,10; Wipfeld; PE-Stelle bei Staustufe Wipfeld
FMA0680	Main (Einlaufkanal VAK), ca. bei km 68,00; Kahl am Main
FMA0678	Main (Auslaufkanal VAK), ca. bei km 67,80; Kahl am Main
FMA0696	Main km 69,64; Karlstein am Main; PE-Ort Seligenstadt-Hena
FKW0005	Sammelkanal KWUK; Karlstein am Main
FDO5520	Donau km 2552,00; Gundelfingen
FDO5516	Donau (Einlaufkanal KRBII), ca. bei km 2551,60; Gundremmingen
FDO5460	Donau km 2546,00; Lauingen
M080001	KSVA KA- Neureut
M080002	HMVA Mannheim
M080003	KSVA Stuttgart-Mühlhausen
K090084	KLA Vilsbiburg
K090086	KLA Weilheim
K090087	KLA Weissenburg
K090088	KLA Würzburg
K090089	KLA Zwiesel
K090090	KLA ZV Burtenbach-Münsterhausen
K090091	KLA ZV Erdinger Moos
K090092	KLA ZV Obere Iller
K090093	KLA ZV Obere Leiblach
K090031	KLA Gefrees
K090032	KLA Greding
K090033	KLA Grossostheim
K090034	KLA Haag
K090035	KLA Hassfurt
K090036	KLA Hersbruck
K090037	KLA Ingolstadt
K090038	KLA Ismaning
K090027	KLA Freyung
K090028	KLA Furth
K090029	KLA Fürstenfeldbruck
K090030	KLA Garching
K090039	KLA Kulmbach
K090040	KLA Landau
K090041	KLA Landsberg
K090042	KLA Langenzenn
K090043	KLA Lichtenfels
K090044	KLA Lindau
K090045	KLA Mallersdorf-Pfaffenberg
K090046	KLA Markt Indersdorf
K090047	KLA Marktheidenfeld
D090006	München Nord
D090007	München Nord-West
D090008	Nürnberg Süd
D090009	Oberglaim
D090010	Schafweide
D090011	Stephanskirchen, BA I
D090012	Ursulasried
D090013	Pfuhl
D080001	Ehingen-Sontheim
D080002	RV-Eschach
D080003	Wieslet
D080004	KA-Knielingen
D080005	LB-Poppenweiler
D080006	Hechingen
D080007	Buchen
D080008	FDS-Musbach
D070001	Deponie Framersheim
D070002	Mertersdorf
D070003	Deponie Meudt
D060001	Buchschlag
D060002	Kalbach
D060003	Uttershausen
D050001	Zt. Hattingen
FPR0085	Prims km 8; Nalbach
FSA0315	Saar km 31; Mettlach
FND0015	Nied km 1; Rehlingen
FMO2385	Mosel km 238; Perl
FSL1090	Saale km 109,0; Hohenweiden
FML0005	Mulde km 0,50; Dessau
FWP0160	Wipper km 16,00; Groß Schierstedt
FAE2503	Altarm der Elbe km 250,30; Vockerode
FHA1200	Havel km 120,00; Havelberg
FST0005	Stör; Schoepfwerk Bekau (Probenahme in der Bekau)
FSL1870	Saale km 187,0; Camburg; PE-Ort Stöben
FWS1260	Weiße Elster km 126,0; Bad Köstritz
FPL0620	Pleiße km 62,00; Gössnitz
FIM0100	Ilm km 10,00; Niedertrebra
FWA1375	Werra km 137,50; Gerstungen
FNE1289	Neckar (Auslaufkanal GKN), ca. bei km 128,9; Neckarwestheim
FKY0000	Kyll
FRH3898	Rhein (Auslaufkanal KKP), ca. bei km 389,70; Philippsburg
FRH1081	Rhein (Auslaufkanal KKL), ca. bei km 108,10; Leibstadt (CH)
FRK0005	Rheinniederungskanal; Leopoldshafen; PE-Stelle unterhalb Einleitung KfK
FDO2491	Donau km 2249,00; Vilshofen
FDO2490	Donau km 2249,00; Vilshofen
FDO3543	Donau km 2354,28; Geisling
FDO3815	Donau km 2381,30; Regensburg
FDO3813	Donau km 2381,26; Regensburg
FDO3814	Donau km 2381,26; Regensburg
FDO3811	Donau km 2381,10; Regensburg
FDO3812	Donau km 2381,10; Regensburg
FDO3963	Donau km 2396,35; Bad Abbach; PE-Stelle Schleuse
FDO3964	Donau km 2396,35; Bad Abbach; PE-Stelle Schleuse
FOG0005	Oldenburger Graben (Kanal); Gaarz
FDO2027	Donau km 2202,70; Jochenstein; PE-Stelle Schleuse
FDO2028	Donau km 2202,70; Jochenstein; PE-Stelle Schleuse
FDO2100	Donau km 2210,00; Obernzell; PE-Ort Erlau
FDO2152	Donau km 2215,20; Obernzell; PE-Ort Erlau
FDO2153	Donau km 2215,20; Obernzell; PE-Ort Erlau
FDO2303	Donau km 2230,28; Kachlet
FDO2304	Donau km 2230,28; Kachlet
FDO2311	Donau km 2231,10; Kachlet
FDO2312	Donau km 2231,10; Kachlet
FDO2839	Donau km 2283,90; Deggendorf
FDO2840	Donau km 2284,03; Deggendorf
FDO3208	Donau km 2320,90; Straubing
FDO3209	Donau km 2320,93; Straubing
FDO3731	Donau km 2373,10; Kreuzhof; PE-Stelle Osthafen
FDO3732	Donau km 2373,10; Kreuzhof; PE-Stelle Osthafen
FDO3791	Donau km 2379,10; Regensburg
FDO3792	Donau km 2379,10; Regensburg
FDO3975	Donau km 2397,50; Bad Abbach; PE-Stelle Bootsschleuse
FDO3976	Donau km 2397,50; Bad Abbach; PE-Stelle Bootsschleuse
FDO4001	Donau km 2400,10; Bad Abbach; PE-Stelle Schleusenkanal
FDO4002	Donau km 2400,10; Bad Abbach; PE-Stelle Schleusenkanal
FEL2612	Elbe km 261,20; Dessau
FEL2613	Elbe km 261,20; Dessau
FEL2614	Elbe km 261,20; Dessau
FEL3891	Elbe km 389,10; Tangermünde
FEL3892	Elbe km 389,10; Tangermünde
FEL3893	Elbe km 389,10; Tangermünde
FEL0580	Elbe km 58,00; Dresden
FEL0581	Elbe km 58,00; Dresden
FEL0582	Elbe km 58,00; Dresden
FEL5860	Elbe km 586,00; Geesthacht
FEL5861	Elbe km 586,00; Geesthacht
FEL6420	Elbe km 642,00; Wedel
FEL6421	Elbe km 642,00; Wedel
FEL7245	Elbe km 724,5; Cuxhaven
FEL7246	Elbe km 724,5; Cuxhaven
FED0725	Elde km 72,50; Parchim
FED0726	Elde km 72,50; Parchim
FED0727	Elde km 72,50; Parchim
FEM1063	Ems km 106,30; Geeste
FEM0246	Ems km 24,64; Terborg
FEM0476	Ems km 47,60; Rheine
FHA0151	Havel km 15,10; Zehdenick
FHA0152	Havel km 15,10; Zehdenick
FHA0153	Havel km 15,10; Zehdenick
FHA0340	Havel km 34,00; Ketzin
FHA0341	Havel km 34,00; Ketzin
FHA0342	Havel km 34,00; Ketzin
FEI1098	Eider km 109,9; Tönning; PE-Stelle Eidersperrwerk
FMA3230	Main km 323,00; Garstadt
FMA3236	Main km 323,70; Garstadt
FMA3237	Main km 323,80; Garstadt
FMA3248	Main km 324,80; Garstadt
FMA3238	Main km 323,80; Garstadt
FMA3162	Main km 316,20; Garstadt
FMA0153	Main km 15,30; Eddersheim
FMA3161	Main km 316,10; Wipfeld
FMO1425	Mosel km 142,50; Wintrich
FMO1414	Mosel km 141,40; Wintrich
FMO1440	Mosel km 144,00; Wintrich
FMO1841	Mosel km 184,10; Trier
FMO1960	Mosel km 196,00; Trier
FMO1961	Mosel km 196,10; Trier
FMO2299	Mosel km 229,87; Palzem
FMO2386	Mosel km 238,6; Perl
FMO2301	Mosel km 230,10; Palzem
FMO2397	Mosel km 239,7; Perl
FMO2419	Mosel km 241,96; Perl
FMO2399	Mosel km 239,9; Perl
FMO2420	Mosel km 242,00; Perl
FMO0207	Mosel km 20,83; Lehmen
FMO0208	Mosel km 20,83; Lehmen
FMO2222	Mosel km 222,20; Wincheringen
FMO0594	Mosel km 59,38; Fankel
FMO0596	Mosel km 59,60; Fankel
FMO0609	Mosel km 60,95; Fankel
FMO0040	Mosel km 4,05; Güls
FMO0043	Mosel km 4,30; Güls
FNE1895	Neckar km 189,5; Obertürkheim
FNE0614	Neckar km 61,4; Rockenau
FNE0615	Neckar km 61,4; Rockenau
FNE1252	Neckar km 125,2; Lauffen
FNE1253	Neckar km 125,2; Lauffen
FNE1254	Neckar km 125,2; Lauffen
FNE1265	Neckar km 126,5; Lauffen
FNE1275	Neckar km 127,5; Neckarwestheim
FNE1276	Neckar km 127,5; Neckarwestheim
FNE1283	Neckar km 128,25; Neckarwestheim
FNE1285	Neckar km 128,45; Neckarwestheim
FNE1288	Neckar km 128,8; Neckarwestheim
FNE1299	Neckar km 129,9; Kirchheim a. N.
FNE1301	Neckar km 130,1; Kirchheim a. N.
FNE1369	Neckar km 136,95; Besigheim
FNE1370	Neckar km 137,0; Besigheim
FNE1371	Neckar km 137,1; Besigheim
FNE0722	Neckar km 72,2; Guttenbach
FNE0726	Neckar km 72,6; Guttenbach
FNE0727	Neckar km 72,6; Guttenbach
FNE0730	Neckar km 73,0; Guttenbach
FRH5913	Rhein km 591,30; Koblenz
FBL0015	Blies km 1; Mandelbachtal
FWE1165	Weser km 116; Bodenwerder; PE-Ort Hehlen
FAL0435	Aller km 43; Grafhorst
FOR0895	Oker km 89; Meine; PE-Ort Groß Schwülper
FLI0375	Leine km 37; Friedland; PE-Ort Reckershausen
FEE0055	Erse km 5; Salzgitter; PE-Ort Üfingen
FBO0625	Böhme km 62; Rethem/Aller; PE-Ort Böhme
FGA0725	Große Aue km 72; Steyerberg
FWM0895	Wümme km 89; Lilienthal; PE-Ort Truperdeich
FBU0005	Butjadinger Bewässerungssystem; Butjadingen; PE-Ort Eckwarden
FEM0735	Ems km 73,50; Emsbüren; PE-Ort Leschede
FHS1475	Hase km 147; Meppen; PE-Ort Bokeloh
FVE0965	Vechte km 96; Laar
FEL6355	Elbe km 635; Hamburg; PE-Ort Blankenese
FJE0405	Jeetzel km 40; Lüchow; PE-Ort Teplingen
FSG0105	Seege km 10; Gartow
FDL0065	Dannenberger Landgraben km 6; Dannenberg; PE-Ort Siemen
FLP0146	Lippe km 14,60; Hünxe
FLP0028	Lippe km 2,80; Wesel
FKY0393	Kyll; km 39,3; Bitburg-Erdorf
FKY1012	Kyll; km 101,2; Birgel
FRH6399	Rhein km 639,90; Bad Honnef
FRH7803	Rhein km 780,30; Duisburg
FRH8541	Rhein km 854,10; Emmerich
FRH8647	Rhein km 864,70; Kleve
FRH6875	Rhein km 687,50; Köln
FRH7931	Rhein km 793,10; Rheinberg
FRU0229	Ruhr km 22,90; Essen
FRR0535	Rur km 53,50; Jülich; PE-Stelle unterhalb KFA
FSI0096	Sieg km 9,60; Troisdorf
FWE2044	Weser km 204,40; Minden
FWU0312	Wupper km 31,20; Solingen
FGL0710	Glan km 71,00; Odenbach
FNH1126	Nahe km 112,6; Grolsheim
FSR0800	Sauer km 80,00; Bollendorf
FMO2300	Mosel km 230,00; Palzem
FSB0395	Schwarzbach/Rheinland-Pfalz km 39,50; Contwig
FRH4434	Rhein km 443,40; Worms
FRH4463	Rhein km 446,30; Worms
FLA0065	Lauchert km 6,45; Bingen
FAB0005	Alb; Albbruck
FAR0058	Argen km 5,84; Kressbronn am Bodensee
STS1201	Teschendorfer See (Dretzsee); Teschendorf
SUN1201	Unterückersee; Prenzlau
SHO0201	Hower Angelsee; Hamburg
SNM0601	Niedermooser See; Freiensteinau
SMA0601	Marbach-Talsperre; Erbach
SED0601	Edersee; Edertal; PE-Ort Hemfurth
SWO1301	Woblitzsee; Wesenberg
SSL1301	Schmollensee; Benz; PE-Ort Stoben
SBW1301	Borgwallsee; Lüssow
SSM1301	Schmachter See; Binz
SMR1301	Müritz-See; Waren
SKU1301	Kummerower See; Gorschendorf; PE-Ort Salem
SFL1301	Fleesensee; Silz; PE-Ort Nossentin
STO1301	Tollensesee; Alt Rehse; PE-Ort Wustrow
SGO1301	Goldberger See; Goldberg
SSW1301	Schweriner See; Schwerin
SIN1301	Inselsee; Güstrow
SGN1301	Großer Sternberger See; Sternberg
SSP0301	Sösetalsperre; Osterode am Harz
SSH0301	Steinhuder Meer; Wunstorf
SZW0301	Zwischenahner Meer; Bad Zwischenahn
SBE0301	Bederkesa See; Bederkesa
SGM0301	Großes Meer; Südbrookmerland
SAL0301	Alfsee; Alfhausen
SAS0301	Aschauteiche; Habighorst
SER0701	Erlenhofsee; Ransbach-Baumbach
SLA0701	Laacher See; Maria Laach
SNO1001	Stausee Nonnweiler; Nonnweiler
SPO1401	Talsperre Pöhl; Thoßfell
SRI1401	Staubecken; Reichenbach
SGL1401	Dorfteich; Glesien
SGZ1401	Speicherbecken; Görlitz
SOB1401	Teich; Oberschindmaas
SSE1501	Scholenesee; Schollene
SSU1501	Süßer See; Lüttchendorf
SBS0101	Bottschlotter See; Bottschlott
SSA0101	Schaalsee; Seedorf, Bad
SLG0101	Langsee; Waldlust
SWI0101	Wittensee; Sande
SOR0101	Ostroher Moor, Angelteich
SLK0101	Lanker See; Freudenholm
SMZ0101	Mözener See; Wittenborn, Bad
SGS0101	Großensee; Strandhalle
SND1201	Neuendorfer See; Alt Schadow; Durchfluß der Spree
SZE1201	Großer Zernsee; Werder; Durchfluß der Havel
SSC1202	Stechlinsee; Neuglobsow; PE-Stelle auf dem UBA-Gelände
SSC1203	Stechlinsee; Rheinsberg; KKR Kühlwasserauslaufkanal in den Stechlinsee
SGD0601	Fischteich; Lichtenfels; PE-Ort Goddelsheim
SNM0602	Niedermooser See; Freiensteinau
STG1102	Tegeler See Kanal-km 2,10; Berlin; PE-Stelle Nr. 23008, vor Strandbad
STG1103	Tegeler See Kanal-km 0,30; Berlin; PE-Stelle Nr. 23004, Valentinswerder
SAD1501	Arendsee; Arendsee
SDB1601	Talsperre Deesbach; Deesbach
SNS1601	Talsperre Neustadt/Harz; Neustadt/Harz
SML1301	Malchiner See; Dahmen
SKR1301	Krakower See; Krakow am See
SRA1301	Radener See; Lalendorf; PE-Ort Raden
SSZ1202	Scharmützelsee; Bad Saarow-Pieskow
SSZ1203	Scharmützelsee; Wendisch Rietz
SLT1201	Groß Leuthener See; Groß Leuthen
SFU0301	Kiesteich; Salzgitter; PE-Ort Fümmelse
SWA0902	Walchensee; Kochel
SKO0902	Kochelsee; Kochel
SSQ0902	Badesee; Schweinfurt
SGU0901	Gustavsee; Kahl am Main; PE-Ort Aschaffenburg
SNF0901	Badesee; Lauingen; PE-Ort südlich von Nenningshof
SNI0902	Baggersee; Niederaichbach; PE-Ort bei Isar km 60,00
SBL0501	Baldeneysee; Essen
SBB0501	Breitenbach (Stausee); Hilchenbach
SDR0501	Dreilägerbach-Talsperre; Roetgen
SEM0501	Emmersee (Stausee); Schieder-Schwalenberg
SHU0501	Hullernersee; Haltern
SMO0501	Möhne-Stausee; Möhnesee
SSV0501	Stever-Stausee; Haltern
SWB0501	Wahnbach-Stausee; Siegburg
SAD1500	Arendsee
SAL0300	Alfsee
SAM0900	Ammersee
SAR0900	Arbersee
SAS0300	Aschauteiche
SAT0900	Altmühlstausee
SBB0500	Breitenbach (Stausee)
SBC1200	Bückwitzer See
SBE0300	Bederkesa See
SBL0500	Baldeneysee
SBO0800	Bodensee
SBO0900	Bodensee
SBR1200	Brieskower See
SBS0100	Bottschlotter See
SBW1300	Borgwallsee
SCH0900	Chiemsee
SDB1600	Talsperre Deesbach
SDR0500	Dreilägerbach-Talsperre
SEB0900	Badesee; Erlabrunn
SED0600	Edersee
SEM0500	Emmersee (Stausee)
SER0700	Erlenhofsee
SFE0900	Feldmochinger See
SFG0900	Badesee; Kahl am Main
SFI0900	Frillensee
SFL1300	Fleesensee
SFR0900	Talsperre Frauenau
SFU0300	Kiesteich; Salzgitter
SGA0900	Großer Alpsee
SGD0600	Fischteich; Lichtenfels
SGL1400	Dorfteich; Glesien
SGM0300	Großes Meer
SGN1300	Großer Sternberger See
SGO1300	Goldberger See
SGR1200	Grimnitzsee
SGS0100	Großensee
SGU0900	Gustavsee
SGZ1400	Speicherbecken; Görlitz
SHO0200	Hower Angelsee
SHU0500	Hullernersee
SIN1300	Inselsee
SJU1100	Jungfernsee
SKI1200	Kietzer See
SKL0900	Klausensee
SKO0900	Kochelsee
SKR1300	Krakower See
SKS0900	Königssee
SKU1300	Kummerower See
SLA0700	Laacher See
SLG0100	Langsee
SLK0100	Lanker See
SLT1200	Groß Leuthener See
SMA0600	Marbach-Talsperre
SMD1200	Großer Maasdorfer Teich
SMG1100	Müggelsee
SMH0900	Talsperre Mauthaus
SML1300	Malchiner See
SMO0500	Möhne-Stausee
SMR1300	Müritz-See
SMZ0100	Mözener See
SND1200	Neuendorfer See
SNE1200	Nehmitzsee
SNF0900	Badesee; Lauingen
SNI0900	Baggersee; Niederaichbach
SNM0600	Niedermooser See
SNN1100	Niederneuendorfer See
SNO1000	Stausee Nonnweiler
SNS1600	Talsperre Neustadt/Harz
SOB1400	Teich; Oberschindmaas
SOF0900	Osterseen-Fohnsee
SOR0100	Teichanlage; Ostrohe
SPE1200	Peitzer Teiche
SPO1400	Talsperre Pöhl
SPW0900	Badesee; Gundelfingen
SRA1300	Radener See
SRI1400	Staubecken; Reichenbach
SSA0100	Schaalsee
SSC1200	Stechlinsee
SSE1500	Scholenesee
SSF0900	Staffelsee
SSH0300	Steinhuder Meer
SSI0900	Simssee
SSK0900	Schnaitseen-Kratzsee
SSL1300	Schmollensee
SSM1300	Schmachter See
SSP0300	Sösetalsperre
SSQ0900	Badesee; Schweinfurt
SSR0900	Schliersee
SSS1100	Stößensee
SST0900	Starnberger See
SSU1500	Süßer See
SSV0500	Stever-Stausee
SSW1300	Schweriner See
SSZ1200	Scharmützelsee
STE0900	Tegernsee
STG1100	Tegeler See
STO1300	Tollensesee
STS1200	Teschendorfer See
SUF0900	Unterföhringer Weiher
SUN1200	Unterückersee
SWA0900	Walchensee
SWB0500	Wahnbach-Stausee
SWE1200	Werbellinsee
SWG0900	Waginger See
SWI0100	Wittensee
SWO1300	Woblitzsee
SZE1200	Großer Zernsee
SZW0300	Zwischenahner Meer
M150001	MHKW Magdeburg - Rothensee
M150002	MVV - TREA Leuna
M150003	SITA Abfallverwertung GmbH Zorbau
D150001	Deponie Lochau
D150002	Deponie Burg
D150003	Deponie Demker, Fa. Bickmeyer
0100	Belastetes Gebiet (Emittent)
D130005	Deponie Ihlenberg
K110005	Kläranlage Münchehofe
FNE1281	Neckar km 128,05; Neckarwestheim
SSJ1200	Schwielochsee
SSJ1201	Schwielochsee; Leißnitz, Ortsteil Sarkow
FWE2685	Weser (Str. km 268,50); Nienburg
FWE0285	Weser km 28; Bodenfelde; PE-Ort Gieselwerder
FWE0905	Weser km 90; Heinsen
FWE1257	Weser km 125; Emmerthal; PE-Stelle Nr.2 unterhalb Auslauf KWG
FWE1476	Weser km 147; Hessisch-Oldendorf
FWE4106	Weser km 410; Hagen im Bremischen; PE-Ort Sandstedt
FWE4185	Weser km 418; Stadland; PE-Stelle unterhalb Auslauf KKU
FEL6544	Elbe km 654; Stade; PE-Stelle oberhalb Einlauf KKS
FEL6655	Elbe km 665; Stade; PE-Ort Schwarztonnensand
FEL6745	Elbe km 674; Freiburg (Elbe); PE-Ort Glückstadt
FEM0551	Ems (Str. km 55,10); Salzbergen
FEM0900	Ems km 90,00; Lingen
FEM1865	Ems km 186 (Fluss-km des Dortmund-Ems-Kanal); Lathen; PE-Ort Hilter
FLI1985	Leine km 198; Hannover
FBA0000	Burgdorfer Aue
FBA0300	Burgdorfer Aue km 30,00; Uetze; PE-Ort Hänigsen
SFO0300	Flögelner See
SFO0301	Flögelner See; Landkreis Cuxhaven
SME0300	Meißendorfer Teiche
SME0301	Meißendorfer Teiche; Landkreis Celle
SGT0300	Gartower See
SGT0301	Gartower See; Landkreis Lüchow-Dannenberg
SLS0300	Laascher See
SLS0301	Laascher See; Landkreis Lüchow-Dannenberg
SBO0802	Bodensee; Langenargen; PE in 0 m Tiefe
SBO0803	Bodensee; Langenargen; PE in 100 m Tiefe
SSA0102	Schaalsee; Gross Zecher
SSN0101	Selenter See; Bellin
FSE0030	Schlei; Schleswig
SAA0500	Aabach-Talsperre
FAL1125	Aller km 112,50; Verden
FEM0675	Ems km 67,50; Listrup
FWE2545	Weser km 254; Landesbergen
FWE2785	Weser km 278; Drakenburg
FSP1685	Spree km 168; Schlepzig
SLP1200	Hälterteich; Lübben-Petkamsberg
SLP1201	Hälterteich; Lübben-Petkamsberg; Teich verbunden mit Spree
SEL1200	Ellbogensee
SEL1201	Ellbogensee; Priepert; Durchfluß der Havel
SHL1600	Stausee Hohenleuben
SHL1601	Stausee Hohenleuben; Hohenleuben
SWL1600	Talsperre Windischleuba
SWL1601	Talsperre Windischleuba (Pleiße); Windischleuba
D090025	Stephanskirchen, BA II
SSL1302	Schmollensee; Bansin; PE-Ort Sellin
FEL0103	Elbe km 10,30; Bad Schandau
FEL0885	Elbe km 88,50; Zehren
FEL1729	Elbe km 172,90; Dommitzsch
FSL1920	Saale km 192,0; Dorndorf-Steudnitz
SSX1600	Talsperre Schmalwasser
SSX1601	Talsperre Schmalwasser; Tambach-Dietharz
SPT1600	Plothener Teiche
SPT1601	Plothener Teiche; Knau
STI0800	Titisee; Südschwarzwald
SSY0800	Schluchsee; Südschwarzwald
FEL4745	Elbe km 474,5; Schnackenburg
FEL4920	Elbe km 492,0; Gorleben
FEL4930	Elbe km 493,0; Gorleben unterhalb Einleitung PKA
FEL5045	Elbe km 504,5; Dömitz
FEL5222	Elbe km 522,2; Hitzacker
FEL5843	Elbe km 584,3; Geesthacht
FEL4940	Elbe km 494,0; Gorleben
FEL5660	Elbe Bereich km 564-568;              KKK  Lauenburg
FEL5689	Elbe bei Lauenburg km 568,9;          KKK  Lauenburg
FEL5786	Elbe oberhalb GKSS km 578,6;          GKSS Geesthacht
FEL5791	Elbe Einleitstelle der GKSS km 579,1; GKSS Geesthacht
FEL5796	Elbe unterhalb GKSS km 579,6;         GKSS Geesthacht
FEL5800	Elbe oberhalb KKK km 580;             KKK  Geesthacht
FEL5807	Elbe Einleitstelle des KKK km 580,7;  KKK  Geesthacht
FEL5810	Elbe unterhalb KKK km 581;            KKK  Geesthacht
FEL5817	Elbe Bereich km 580,5-582;            KKK  Geesthacht
FEL5819	Elbe km 581,9;                        KKK  Geesthacht
FEL5820	Elbe km 582,0;                        KKK  Geesthacht
FEL5822	Elbe km 582,2;                        KKK  Geesthacht
FEL5832	Elbe km 583,2;                        KKK  Geesthacht
FEL5834	Elbe km 583,4;                        KKK  Geesthacht
FEL5844	Elbe km 584,4;                        KKK  Geesthacht
FEL5850	Elbe oberer Schleusenkanal km 585;    KKK  Geesthacht
FEL5865	Elbe unterhalb Staustufe km 586,5;    KKK  Marschacht
FEL5870	Elbe Bereich km 586-588,5;            KKK  Marschacht
FEL5883	Elbe bei Altengamme km 588,3;         KKK  Hamburg
FEL5980	Elbe Bereich km 597-599;              KKK  Hamburg
FEL6730	Elbe Bereich km 670-675;              KKB  Glückstadt
FEL6770	Elbe Bereich km 675-680;              KBR  Wewelsfleth
FEL6780	Elbe km 678;                          KBR  Wewelsfleth
FEL6790	Elbe km 679;                          KBR  Wewelsfleth
FEL6817	Elbe oberhalb KBR km 681,7;           KBR  Brokdorf
FEL6825	Elbe unterhalb KBR km 682,5;          KBR  Brokdorf
FEL6830	Elbe Bereich km 680-685;              KBR  Brokdorf
FEL6833	Elbe km 683,3;                        KBR  Brokdorf
FEL6838	Elbe Brokdorfer Hafen km 683,8;       KBR  Brokdorf
FEL6870	Elbe Bereich km 685-690;              KBR  St. Margarethen
FEL6888	Elbe km 688,8;                        KBR  St. Margarethen
FEL6890	Elbe km 689;                          KKB  St. Margarethen
FEL6900	Elbe km 690, ablaufendes Wasser;      KKB  St. Margarethen
FEL6901	Elbe km 690, auflaufendes Wasser;     KKB  St. Margarethen
FEL6919	Elbe oberhalb KKB km 692;             KKB  Brunsbüttel
FEL6920	Elbe Einleitstelle des KKB km 692;    KKB  Brunsbüttel
FEL6921	Elbe unterhalb KKB km 692;            KKB  Brunsbüttel
FEL6923	Elbe Bereich km 690-695;              KKB  Brunsbüttel
FEL6925	Elbe Bereich km 670 bis Gelbsand;     KKB  Brunsbüttel
FEL6930	Elbe km 693;                          KKB  Brunsbüttel
FEL7070	Elbe Bereich km 705-710;              KKB  Otterndorf
FEL7270	Elbe Bereich km 725-730;              KKB  Cuxhaven
FEL7350	Gelbsand (vor Elbmündung);            KKB  Cuxhaven
FEL6980	Elbe km 698;                          KKB  Brunsbüttel
SWL1201	Wolzensee; Rathenow
SSC1204	Stechlinsee; Rheinsberg; PE-Stelle am Auslaufbauwerk des KKR
T110015	Grundwasser Tempelhof ( Ringbahnstraße )
SST1200	Stolpsee
SST1201	Stolpsee; Himmelpfort
FWE1990	Weser km 199,00; Porta Westfalica
FRH6600	Rhein km 660,00; Niederkassel
FWT0420	Wertach km 40,20; Ettringen
WATL-NW	Nordwestatlantik, FAO 21
WATL-NO	Nordostatlantik ohne Ostsee, FAO 27
T070020	WW Clausen (Quelle Schwarzbachtal)
T070021	WW Mehren
K110006	KA  Waßmannsdorf
FDO2055	Donau km 2205,50; Grünau; PE-Stelle Sportbootshafen
FDO2114	Donau km 2211,37; Obernzell
FDO2284	Donau km 2228,40; Passau
FDO3266	Donau km 2326,65; Straubing; PE-Stelle unterer Vorhafen
FDO3267	Donau km 2326,70; Straubing; PE-Stelle unterer Vorhafen
FDO3613	Donau km 2361,26; Regensburg; Kreuzhof
FDO3816	Donau km 2381,42; Regensburg
FDO4027	Donau km 2402,70; Bad Abbach; PE-Stelle Altarm
FDO4032	Donau km 2403,20; Bad Abbach; PE-Stelle Altarm
FEL6535	Elbe km 653,5; Twielenfleth; Juelssand
FEM2128	Ems km 212,75; Herbrum
FJD0010	Jadebusen; Wilhelmshaven; PE-Stelle Neuer Vorhafen
FWE3740	Weser km 373,97; Bremen; Insel
SRZ0100	Ratzeburger See
SRZ0101	Ratzeburger See; Schloßwiese
FPL0060	Pleiße km 6,0; Brücke bei Markleeberg
SQU1400	Talsperre Quitzdorf
FWS0667	Weiße Elster km 66,7; Brücke bei Pegau
FFM0000	Freiberger Mulde
FFM0003	Freiberger Mulde km 0,3; Mdg. in Erlln
FTR0000	Triebisch
FTR0081	Triebisch km 8,1; in Munzig an Unterquerung der S83
SKU1302	Kummerower See; PE-Ort Kummerow
FML0670	Mulde km 67,0; AMB Bad Düben
SLM1400	Talsperre Lehnmühle
FEL0039	Elbe km 3,9; Schmilka
FZM0000	Zwickauer Mulde
FZM0720	Zwickauer Mulde km 72,0; Schlunzig
FWS1970	Weiße Elster km 197,0; unterhalb Elsterberg
SPO1402	Talsperre Pöhl, Vorsperre Thoßfell
FLN0000	Lausitzer Neiße
FLN1610	Lausitzer Neiße km 161; AMB Görlitz
SSO1400	Talsperre Sosa
FEL2166	Elbe km 216,6; Wittenberg
FEL4035	Elbe km 403,5; Arneburg
FEL7263	Elbe km 726,3; Cuxhaven
FMO0020	Mosel km 2,0; Koblenz
FRH2328	Rhein km 232,8; Breisach
FWE4229	Weser (Unterweser) km 422,9; Nordenham
SPL0100	Großer Plöner See
SPL0101	Großer Plöner See, Plön, Eutiner Straße
FUN0000	Unstrut
FUN1060	Unstrut km 106,0; Wundersleben
FSA0066	Saar km 6,6; Kanzem
FRH5965	Rhein km 596,50; Koblenz-Kesselheim
FUN1020	Unstrut km 102,0; Sömmerda-Schallenburg
M080006	HMVA Stuttgart-Münster
SCK0800	Schreckensee; Wolpertswende
SJU1102	Jungfernsee; Glienicker Brücke
SPO1100	Pohlesee
SGR1100	Griebnitzsee
SST1100	Stölpchensee
SWA1100	Wannsee
FBS0001	Bütteler Sieltief; Büttel
FBU0001	Butjadinger Bewässerungssystem; Rodenkirchen
FEL5500	Elbe km 550; Bleckede
FEL6606	Elbe km 660,6; Grauerort
FSG0085	Seege km 8; Nienwalde
FWE1247	Weser km 124,7; KWG Einlauf
FWE1248	Weser km 124,8; KWG Auslauf
FWE1380	Weser km 138; Hameln unterhalb
FWE3790	Weser km 379; Mittelsbüren
FWE4050	Weser km 405; Brake
FWE4110	Weser km 411; Sandstedt
FWE4186	Weser km 418,6; KKU Einlauf
FWE4187	Weser km 418,7; KKU Auslauf
FWE4205	Weser km 420,5; Kleinensiel
FWE4270	Weser km 427; Blexersande
FWN0150	Weschnitz, Straßenbrücke bei Biblis
FRH4590	Rhein km 459 - 461, unterhalb KKW Biblis
FRH4560	Rhein km 456 - 458, unterhalb KKW Biblis
FRH4500	Rhein km 450 - 452, oberhalb KKW Biblis
FRH4530	Rhein; KKW Biblis, Bereich Auslaufbauwerk
FRH4620	Rhein km 462; unterhalb KKW Biblis
FRH4390	Rhein km 439; oberhalb KKW Biblis
FHA0140	Havel, Höhe Pfaueninsel
FHA0147	Havel, Bootsanleger Pfaueninsel
FHA0161	Havel, Krughorn
FTK0001	Teltowkanal, Parkbrücke
FTK0038	Teltowkanal, Nathanbrücke
SSA1200	Sacrower See
T070023	WW Koblenz-Oberwerth
T070024	WW Kaiserslautern, Quelle Espensteig
T070025	Gerolstein, Quelle Müllenborn
T070026	Rennerod, Quelle Hahneck
T070027	Nittel, GW-Messstelle 4147 I
T070028	Wasserliesch, GW-Messstelle 4149
SWD1600	Talsperre Weida
SWD1601	Talsperre Weida; Staitz
SLB1600	Talsperre Leibis
SLB1601	Talsperre Leibis; Unterweißbach
SSB1600	Talsperre Schönbrunn
SSB1601	Talsperre Schönbrunn; Schleusegrund
FNE1330	Neckar ca. bei km 133,0; Gemmrigheim/Kirchheim a. N.
FNE1245	Neckar ca. bei km 124,5; Lauffen
FNE1140	Neckar ca. bei km 114,0; Heilbronn (linker Neckarzweig)
FNE1080	Neckar ca. bei km 108,0; Neckarsulm (linker Neckarzweig)
FNE1000	Neckar ca. bei km 100,0; Bad Wimpfen
FRH4010	Rhein km 401; Speyer
FRH3894	Rhein km 389,4; Römerberg-Mechtersheim
SAB0700	Altrhein
SAB0701	Altrhein; Römerberg-Berghausen
FSY0000	Speyerbach
FSY0600	Speyerbach; km 60,0 Mündung; Speyer
SBM0700	Baggersee
SBM0701	Baggersee; Römerberg-Mechtersheim
T070029	Römerberg-Berghausen, Quelle Sportplatz
D080009	Vaihingen
SHT0100	Hülltofttief
SHT0101	Hülltofttief, Badestelle
FTR0139	Triebisch km 13,9; Munzig
FTR0141	Triebisch km 14,1; Munzig
FEL1160	Elbe km 116,0; Strehla
FEL1484	Elbe km 148,4; Mehderitzsch
FZM0759	Zwickauer Mulde km 75,9; Schlunzig
FWS1688	Weiße Elster km 168,8; unterhalb Elsterberg
FTI0000	Tidekanal
FTI0010	Tidekanal; Hamburg PE-Stelle Brücke Halskestr.
FIR1300	Isar km 130,00; oberhalb Einmündung Mühlbach
T070030	Worms-Ibersheim (Beregnungsbrunnen Nr. 46)
SGB1300	Greifswalder Bodden
SGB1301	Greifswalder Bodden; Einlaufkanal, KGR
SGB1302	Greifswalder Bodden; Hafenbecken, KGR
FRH6053	Rhein km 605,31; (Einlaufkanal KKM), Mühlheim-Kärlich
FRH6055	Rhein km 605,50; (Auslaufkanal KKM), Mühlheim-Kärlich
FEL580E	Elbe Kühlwasserentnahme des KKK; KKK Geesthacht
FEL682E	Elbe Kühlwasserentnahme des KBR; KBR Brokdorf
FEL580A	Elbe Kühlwasserauslauf des KKK; KKK Geesthacht
FEL692E	Elbe Kühlwasserentnahme des KKB;  KKB  Brunsbüttel
FEL692A	Elbe Kühlwasserauslauf des KKB;  KKB  Brunsbüttel
FEL6575	Elbe km 657,5; Bützfleth
FOD6906	Oder km 690,55; Schwedt
T070022	WW St. Sebastian, ehemals WW Koblenz-Kesselheim
M080005	HMVA Freiburg-Eschbach
FEL682A	Elbe Kühlwasserauslauf des KBR; KBR Brokdorf
SOH1600	Talsperre Ohra
SOH1601	Talsperre Ohra; Luisenthal
SST0700	Steinbachtalsperre
SST0701	Steinbachtalsperre; WW, PE-Stelle Filterschlamm
T090040	Groß-Rohrheim, Kernkraftwerk Biblis
T090041	WW Biblis, Kernkraftwerk Biblis
SJW0700	Jungfernweiher
SJW0701	Jungfernweiher; Ulmen
FAP0000	Amper
FAP0992	Amper km 99,20; Grafrath
SPL1300	Plauer See
SPL1301	Plauer See; Plau am See
WOSTSEE	Ostsee, FAO 27.IIId
WATL-MW	Mittlerer Westatlantik, FAO 31
WATL-MO	Mittlerer Ostatlantik, FAO 34
WATL-SW	Südwestatlantik, FAO 41
WATL-SO	Südostatlantik, FAO 47
WMITT.M	Mittelmeer, FAO 37.1, 37.2 und 37.3
WSCHW.M	Schwarzes Meer, FAO 37.4
WIND.OZ	Indischer Ozean, FAO 51 und 57
WPAZ.OZ	Pazifischer Ozean, FAO 61, 67, 71, 77, 81 und 87
WANTARK	Antarktis, FAO 48, 58 und 88
SGR0300	Granetalsperre
K130001	KLA Anklam
K130002	KLA Lubmin
K130003	KLA Neubrandenburg
SKY1200	Kyritzer See
SKY1201	Kyritzer See; Untersee bei Bantikow
K130004	KLA Rostock
K130005	KLA Schwerin
K130006	KLA Stralsund
T130001	WW Andershof, Stralsund
T130002	WW Friedland
T130003	WW Lodmannshagen
T130004	WW Lüssow
T130005	WW Müggenwalde
T130006	WW Neubrandenburg
T130007	WW Rakow
T130008	WW Rostock
T130009	WW Schwerin
T130010	WW Staphel, Neu Mukran
KO13003	KOA Schwerin
KO13002	KOA Reinberg
KO13001	KOA Bartenshagen
M160001	RABA Erfurt
M160002	RABA Zella-Mehlis
SHE0300	Heerter See
T050029	WW Köln (Weißer Bogen)
K070006	KLA Bitburg
D140001	DE Cröbern
D140002	DE Gröbern
D140003	DE Kunnersdorf
M140001	TA Lauta
T140001	TWA Klingenberg
T140002	TWA Klipphausen
T140003	TWA Weinhübel
T140004	TWA Sosa
FDO5824	Donau km 2582,40; Ulm-Böfingen
FDO5542	Donau km 2554,20; Gundelfingen
STE1300	Tempziner See
SET1400	Erzengler Teich
FDO5383	Donau km 2538,30; Dillingen
SEC0300	Eckertalsperre
STW1002	Zuchtteich Wadrill
FAH0000	Ahr
FAH0560	Ahr; km 56; Mayschoß-Laach
FNT0000	Nette
FNT0580	Nette; km 58; Weißenthurm
FMF0000	Mangfall
FMF0547	Mangfall km 54,7; KLA Louisenthal
FNE2022	Neckar km 202,2; Plochingen
D130001	Dennin, Deponie Stern
D130002	Glasewitz
D130003	Rosenow
D130004	Stralendorf
K160001	KLA Erfurt-Kühnhausen
K160002	KLA Gera
K160003	KLA Jena
K160004	KLA Sonneberg-Heubisch
K160005	KLA Leinefelde
T160001	TWA Luisenthal
T160002	TWA Dörtendorf
T160003	TWA Schönbrunn/Schleusegrund
T160004	TWA Zeigerheim
K070007	KLA Koblenz
K070008	KLA Ludwigshafen
M070002	MHKW Pirmasens
K140001	KA Plauen
K140002	KA Görlitz - Nord
K140003	KA Leipzig - Rosenthal
K140004	KA Kaditz
K140005	KA Zwickau
T030017	Harzwasserwerke, Granetalsperre
K150001	KA Gerwisch
K150002	KA Halle - Nord
K150003	KA Silstedt
K150004	KA Dessau
K150005	KA Salzwedel
0101	Belastung durch Störfall
0102	Belastung durch Emittenteneinfluss/Industrienähe
0103	Belastung durch Bodenkontamination
0104	Belastung durch Deponie/Halde
0105	Belastung durch Futtermittel
0111	Ländliches Gebiet
0112	Städtisches Gebiet
0113	Industriegebiet
0114	Hafenregion
0200	Unbelastetes Gebiet
FLP1362	Lippe km 136,20; Hamm; PE-Stelle im Bereich der Einleitung des THTR
FLP1350	Lippe km 135,00; Hamm; PE-Stelle unterhalb THTR
FGT0084	Geithe km 8,40; Hamm
FDH0371	Datteln-Hamm-Kanal km 37,10; Hamm
FRR0653	Rur km 65,30; Niederzier; PE-Stelle oberhalb KFA
FRR0594	Rur km 59,40; Jülich; PE-Stelle unterhalb KFA
FMB0049	Moorbach km 4,90; Ahaus; Nähe Brennelementzwischenlager Ahaus
FAA0727	Ahauser Aa km 72,70; Ahaus; Nähe Brennelementzwischenlager Ahaus
FGO0074	Goorbach km 7,40; Gronau; PE-Stelle oberhalb UAG
FGO0069	Goorbach km 6,90; Gronau; PE-Stelle unterhalb UAG
FDI0536	Dinkel km 53,60; Gronau
FWE0471	Weser km 47,10; Beverungen; PE-Stelle oberhalb KWW
FWE0494	Weser (Einlaufbauwerk KWW), ca. bei km 49,40; Beverungen
FWE0496	Weser (Auslaufbauwerk KWW), ca. bei km 49,60; Beverungen
FWE0602	Weser km 60,20; Beverungen; PE-Stelle unterhalb KWW
FAA0000	Ahauser Aa
FAB0000	Alb
FAL0000	Aller
FAT0000	Alster
FAE0000	Altarm der Elbe
FAM0000	Altmühl
FAR0000	Argen
FBI0000	Bille
FBL0000	Blies
FBO0000	Böhme
FBU0000	Butjadinger Bewässerungssystem
FDH0000	Datteln-Hamm-Kanal
FDI0000	Dinkel
FDO0000	Donau
FDL0000	Dannenberger Landgraben
FEI0000	Eider
FEL0000	Elbe
FED0000	Elde
FEZ0000	Elz
FEM0000	Ems
FEE0000	Erse
FEC0000	Eschach
FGE0000	Geestezufluss
FGT0000	Geithe
FGL0000	Glan
FGO0000	Goorbach
FGA0000	Große Aue
FHS0000	Hase
FHA0000	Havel
FHK0000	Hirschkanal
FIL0000	Iller
FIK0000	Illerkanal
FIM0000	Ilm
FIN0000	Inn
FIR0000	Isar
FJD0000	Jadebusen
FJE0000	Jeetzel
FKI0000	Kinzig
FKO0000	Kocher
FLH0000	Lahn
FLM0000	Längenmühlbach
FLA0000	Lauchert
FLE0000	Lech
FLI0000	Leine
FIS0000	linker Isarseitenkanal
FLP0000	Lippe
FMA0000	Main
FMB0000	Moorbach
FMO0000	Mosel
FML0000	Mulde
FMU0000	Murg
FMH0000	Müritz-Havel-Kanal
FNA0000	Naab
FNH0000	Nahe
FNE0000	Neckar
FNI0000	Nidda
FND0000	Nied
FOS0000	Ostsee
FOC0000	Ochtum
FOD0000	Oder
FOR0000	Oker
FOG0000	Oldenburger Graben
FPE0000	Peene
FPN0000	Pegnitz
FPL0000	Pleiße
FPR0000	Prims
FRN0000	Rednitz
FRE0000	Regen
FRZ0000	Regnitz
FRH0000	Rhein
FRK0000	Rheinniederungskanal
FRU0000	Ruhr
FRR0000	Rur
FSL0000	Saale
FSA0000	Saar
FSH0000	Salzach
FSR0000	Sauer
FSD0000	Schwarzbach/Hessen
FSB0000	Schwarzbach/Rheinland-Pfalz
FSG0000	Seege
FSC0000	Seibertsbach
FSI0000	Sieg
FSP0000	Spree
FSN0000	Steinaubach
FSK0000	Stichkanal
FST0000	Stör
FTA0000	Tauber
FVE0000	Vechte
FVI0000	Vils
FWS0000	Weiße Elster
FWA0000	Werra
FWT0000	Wertach
FWE0000	Weser
FWP0000	Wipper
FWM0000	Wümme
FWU0000	Wupper
FZI0000	Ziese
FEM0247	Ems km 24,64; Terborg
FNE0001	Neckar
FNE0002	Neckar
FNE0003	Neckar
FNE0004	Neckar
FNE0005	Neckar
FNE0006	Neckar
FNE0007	Neckar
FNE0008	Neckar
FNE0009	Neckar
FRH0001	Rhein
FRH0002	Rhein
FRH0003	Rhein
FRH0004	Rhein
FRH0005	Rhein
FRH0006	Rhein
FRH0007	Rhein
FRH0008	Rhein
FRH0009	Rhein
FDO0001	Donau
FDO0002	Donau
FDO0003	Donau
FDO0004	Donau
FDO0005	Donau
FDO0006	Donau
FDO0007	Donau
FDO0008	Donau
FDO0009	Donau
FEL0001	Elbe
FEL0002	Elbe
FEL0003	Elbe
FEL0004	Elbe
FEL0005	Elbe
FEL0006	Elbe
FEL0007	Elbe
FEL0008	Elbe
FEL0009	Elbe
FEM0001	Ems
FEM0002	Ems
FEM0003	Ems
FEM0004	Ems
FEM0005	Ems
FEM0006	Ems
FEM0007	Ems
FEM0008	Ems
FEM0009	Ems
FIR0001	Isar
FIR0002	Isar
FIR0003	Isar
FIR0004	Isar
FIR0005	Isar
FIR0006	Isar
FIR0007	Isar
FIR0008	Isar
FIR0009	Isar
FMA0001	Main
FMA0002	Main
FMA0003	Main
FMA0004	Main
FMA0005	Main
FMA0006	Main
FMA0007	Main
FMA0008	Main
FMA0009	Main
FLP0001	Lippe
FLP0002	Lippe
FLP0003	Lippe
FLP0004	Lippe
FLP0005	Lippe
FLP0006	Lippe
FLP0007	Lippe
FLP0008	Lippe
FLP0009	Lippe
FWE0001	Weser
FWE0002	Weser
FWE0003	Weser
FWE0004	Weser
FWE0005	Weser
FWE0006	Weser
FWE0007	Weser
FWE0008	Weser
FWE0009	Weser
FGV0000	Gnevsdorfer Vorfluter
FMO0001	Mosel
FMO0002	Mosel
FMO0003	Mosel
FMO0004	Mosel
FMO0005	Mosel
FMO0006	Mosel
FMO0007	Mosel
FMO0008	Mosel
FMO0009	Mosel
FKW0000	Sammelkanal KWUK
SAM0901	Ammersee; Eching
STE0901	Tegernsee; Gmund
SST0901	Starnberger See; Starnberg
SCH0901	Chiemsee; Seeon-Seebruck
SFR0901	Talsperre Frauenau; Frauenau
SMH0901	Talsperre Mauthaus; Nordhalben
SBO0901	Bodensee; Nonnenhorn
SKS0901	Königssee; Schönau
SKS0902	Königssee; Schönau
SKO0901	Kochelsee; Kochel
SWA0901	Walchensee; Kochel
STE0902	Tegernsee; Tegernsee
SSI0901	Simssee; Stephanskirchen
SWG0901	Waginger See; Waging
SFE0901	Feldmochinger See; München
SSF0901	Staffelsee; Uffing
SSR0901	Schliersee; Schliersee
SUF0901	Unterföhringer Weiher; Unterföhring
SFI0901	Frillensee; Inzell
SSK0901	Schnaitseen-Kratzsee; Schnaitsee
SOF0901	Osterseen-Fohnsee; Iffeldorf
SAR0901	Arbersee; Bayrisch Eisenstein
SKL0901	Klausensee; Schwandorf
SAT0901	Altmühlstausee; Muhr am See
SSQ0901	Badesee; Schweinfurt
SFG0901	Badesee; Kahl am Main; PE-Ort Freigericht-West
SEB0901	Badesee; Erlabrunn
SPW0901	Badesee; Gundelfingen; PE-Ort Peterswörth
SGA0901	Großer Alpsee; Immenstadt
SBO0801	Bodensee; Fischbach; PE-Stelle mitten im See
SJU1101	Jungfernsee; Berlin; PE-Stelle Nr. 21006
SNN1101	Niederneuendorfer See; Berlin
SMG1101	Müggelsee; Berlin; PE-Stelle Nr. 41035
SSS1101	Stößensee Kanal-km 3,80; Berlin; PE-Stelle Nr.25003, Höhe Siemenswerder
STG1101	Tegeler See Kanal-km 3,00; Berlin; PE-Stelle Nr. 23009, WW Tegel
SPE1201	Peitzer Teiche (Hälterteich); Peitz
SKI1201	Kietzer See; Altfriedland
SMD1201	Großer Maasdorfer Teich; Prestewitz
SWE1201	Werbellinsee; Altenhof
SGR1201	Grimnitzsee; Joachimsthal
SSZ1201	Scharmützelsee; Bad Saarow-Pieskow
SBR1201	Brieskower See; Brieskow-Finkenheerd
SSC1201	Stechlinsee; Neuglobsow; PE-Stelle bei Fischerei
SNE1201	Nehmitzsee; Rheinsberg; KKR Kühlwassereinlaufkanal aus dem Nehmitzsee
SBC1201	Bückwitzer See; Bückwitz
\.

