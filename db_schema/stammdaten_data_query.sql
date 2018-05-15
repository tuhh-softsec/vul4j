--
-- PostgreSQL database dump
--

-- Dumped from database version 10.1
-- Dumped by pg_dump version 10.1

-- Started on 2018-01-22 13:18:10 CET

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

SET search_path = stamm, pg_catalog;

--
-- TOC entry 5408 (class 0 OID 3061575)
-- Dependencies: 246
-- Data for Name: base_query; Type: TABLE DATA; Schema: stamm; Owner: postgres
--

COPY base_query (id, sql) FROM stdin;
1	SELECT probe.id AS probeId, probe.hauptproben_nr AS hauptprobenNr, datenbasis.datenbasis AS dBasis, stamm.mess_stelle.netzbetreiber_id AS netzId, probe.mst_id AS mstId, probe.umw_id AS umwId, probenart.probenart AS pArt, probe.probeentnahme_beginn AS peBegin, probe.probeentnahme_ende AS peEnd, ort.ort_id AS ortId, ort.gem_id AS eGemId, verwaltungseinheit.bezeichnung AS eGem, probe.id_alt AS idAlt FROM land.probe LEFT JOIN stamm.mess_stelle ON (probe.mst_id = stamm.mess_stelle.id) LEFT JOIN stamm.datenbasis ON (probe.datenbasis_id = datenbasis.id) LEFT JOIN stamm.probenart ON (probe.probenart_id = probenart.id) LEFT JOIN land.ortszuordnung ON (probe.id = ortszuordnung.probe_id AND ortszuordnung.ortszuordnung_typ = 'E') LEFT JOIN stamm.ort ON (ortszuordnung.ort_id = ort.id) LEFT JOIN stamm.verwaltungseinheit ON (ort.gem_id = verwaltungseinheit.id)
7	SELECT probe.id AS id,\r\n  probe.hauptproben_nr AS hpNr,\r\n  datenbasis.datenbasis AS dBasis,\r\n  stamm.mess_stelle.netzbetreiber_id AS netzId,\r\n  probe.mst_id AS mstId,\r\n  probe.umw_id AS umwId,\r\n  probenart.probenart AS pArt,\r\n  probe.probeentnahme_beginn AS peBegin,\r\n  probe.probeentnahme_ende AS peEnd,\r\n  ort.ort_id AS ortId,\r\n  ort.gem_id AS eGemId,\r\n  verwaltungseinheit.bezeichnung AS eGem,\r\n  probe.id_alt AS idAlt\r\n FROM land.probe\r\nLEFT JOIN stamm.mess_stelle\r\n  ON (probe.mst_id = stamm.mess_stelle.id)\r\nLEFT JOIN stamm.datenbasis\r\n  ON (probe.datenbasis_id = datenbasis.id)\r\nLEFT JOIN stamm.probenart\r\n  ON (probe.probenart_id = probenart.id)\r\nLEFT JOIN land.ortszuordnung\r\n  ON (\r\n      probe.id = ortszuordnung.probe_id\r\n      AND ortszuordnung.ortszuordnung_typ = 'E'\r\n      )\r\nLEFT JOIN stamm.ort\r\n  ON (ortszuordnung.ort_id = ort.id)\r\nLEFT JOIN stamm.verwaltungseinheit\r\n  ON (ort.gem_id = verwaltungseinheit.id)\r\n
9	SELECT messprogramm.id,\n  messprogramm.id AS mpNr,\n  stamm.mess_stelle.netzbetreiber_id AS netzId,\n  CASE \n    WHEN messprogramm.mst_id = messprogramm.labor_mst_id\n      THEN messprogramm.mst_id\n    ELSE messprogramm.mst_id || '-' || messprogramm.labor_mst_id\n    END AS mstLaborId,\n  datenbasis.datenbasis AS dBasis,\n  CASE \n    WHEN messprogramm.ba_id = '1'\n      THEN 'RB'\n    ELSE 'IB'\n    END AS messRegime,\n  probenart.probenart AS pArt,\n  messprogramm.umw_id AS umwId,\n  messprogramm.media_desk AS deskriptoren,\n  messprogramm.probenintervall AS intervall,\n  ort.ort_id AS ortId,\n  ort.gem_id AS eGemId,\n  verwaltungseinheit.bezeichnung AS eGem\nFROM land.messprogramm\nLEFT JOIN stamm.mess_stelle\n  ON (messprogramm.mst_id = stamm.mess_stelle.id)\nLEFT JOIN stamm.datenbasis\n  ON (messprogramm.datenbasis_id = datenbasis.id)\nLEFT JOIN stamm.probenart\n  ON (messprogramm.probenart_id = probenart.id)\nLEFT JOIN land.ortszuordnung_mp\n  ON (\n      messprogramm.id = ortszuordnung_mp.messprogramm_id\n      AND ortszuordnung_mp.ortszuordnung_typ = 'E'\n      )\nLEFT JOIN stamm.ort\n  ON (ortszuordnung_mp.ort_id = ort.id)\nLEFT JOIN stamm.verwaltungseinheit\n  ON (ort.gem_id = verwaltungseinheit.id)\n
10	SELECT ort.id,\n  ort.netzbetreiber_id AS netzbetreiberId,\n  ort.ort_id AS ortId,\n  ort_typ.code AS ortTyp,\n  ort.kurztext,\n  ort.langtext,\n  staat.staat_iso AS staat,\n  verwaltungseinheit.bezeichnung AS verwaltungseinheit,\n  nuts_code AS nutsCode,\n  oz_id AS ozId,\n  kta_gruppe AS anlageId,\n  mp_art AS mpArt,\n  zone,\n  sektor,\n  zustaendigkeit,\n  berichtstext,\n  unscharf,\n  koordinaten_art.koordinatenart AS koordinatenArt,\n  ort.koord_x_extern AS koordXExtern,\n  ort.koord_y_extern AS koordYExtern,\n  PUBLIC.ST_X(ort.geom) AS longitude,\n  PUBLIC.ST_Y(ort.geom) AS latitude,\n  hoehe_ueber_nn AS hoeheUeberNn,\n  hoehe_land AS hoeheLand,\n  aktiv,\n  letzte_aenderung AS letzteAenderung\nFROM stamm.ort\nLEFT JOIN stamm.verwaltungseinheit\n  ON ort.gem_id = verwaltungseinheit.id\nLEFT JOIN stamm.staat\n  ON stamm.staat.id = ort.staat_id\nINNER JOIN stamm.koordinaten_art\n  ON stamm.koordinaten_art.id = ort.kda_id\nLEFT JOIN stamm.ort_typ\n  ON ort.ort_typ = ort_typ.id\nLEFT JOIN stamm.kta_gruppe\n  ON kta_gruppe.id = ort.kta_gruppe_id\n
11	SELECT id, netzbetreiber_id AS netzbetreiberId, prn_id AS prnId, bearbeiter, bemerkung, betrieb, bezeichung, kurz_bezeichnung AS kurzBezeichnung, ort, plz, strasse, telefon, tp, typ, letzte_aenderung AS letzteAenderung FROM stamm.probenehmer
12	SELECT id, netzbetreiber_id AS netzbetreiberId, datensatz_erzeuger_id AS datensatzErzeugerId, mst_id AS mstId, bezeichung, letzte_aenderung AS letzteAenderung FROM stamm.datensatz_erzeuger
13	SELECT messung.id,\n  probe.id AS probeId,\n  probe.hauptproben_nr AS hpNr,\n  messung.nebenproben_nr AS npNr,\n  status_protokoll.datum AS statusD,\n  stamm.status_stufe.stufe AS statusSt,\n  stamm.status_wert.wert AS statusW,\n  datenbasis.datenbasis AS dBasis,\n  stamm.mess_stelle.netzbetreiber_id AS netzId,\n  probe.mst_id AS mstId,\n  probe.umw_id AS umwId,\n  probenart.probenart AS pArt,\n  probe.probeentnahme_beginn AS peBegin,\n  probe.probeentnahme_ende AS peEnd,\n  ort.ort_id AS ortId,\n  ort.gem_id AS eGemId,\n  verwaltungseinheit.bezeichnung AS eGem\nFROM land.probe\nLEFT JOIN stamm.mess_stelle\n  ON (probe.mst_id = stamm.mess_stelle.id)\nINNER JOIN land.messung\n  ON probe.id = messung.probe_id\nINNER JOIN land.status_protokoll\n  ON messung.STATUS = status_protokoll.id\nLEFT JOIN stamm.status_kombi\n  ON status_protokoll.status_kombi = stamm.status_kombi.id\nLEFT JOIN stamm.status_wert\n  ON stamm.status_wert.id = stamm.status_kombi.wert_id\nLEFT JOIN stamm.status_stufe\n  ON stamm.status_stufe.id = stamm.status_kombi.stufe_id\nLEFT JOIN stamm.datenbasis\n  ON (probe.datenbasis_id = datenbasis.id)\nLEFT JOIN stamm.probenart\n  ON (probe.probenart_id = probenart.id)\nLEFT JOIN land.ortszuordnung\n  ON (\n      probe.id = ortszuordnung.probe_id\n      AND ortszuordnung.ortszuordnung_typ = 'E'\n      )\nLEFT JOIN stamm.ort\n  ON (ortszuordnung.ort_id = ort.id)\nLEFT JOIN stamm.verwaltungseinheit\n  ON (ort.gem_id = verwaltungseinheit.id)\n
14	SELECT id, netzbetreiber_id AS netzbetreiberId, code, bezeichnung, letzte_aenderung AS letzteAenderung FROM stamm.messprogramm_kategorie
15	SELECT messung.id,\n  probe.id AS probeId,\n  probe.hauptproben_nr AS hpNr,\n  messung.nebenproben_nr AS npNr,\n  stamm.status_stufe.stufe AS statusSt,\n  stamm.status_wert.wert AS statusW,\n  status_protokoll.datum AS statusD,\n  datenbasis.datenbasis AS dBasis,\n  stamm.mess_stelle.netzbetreiber_id AS netzId,\n  probe.mst_id AS mstId,\n  probe.umw_id AS umwId,\n  probenart.probenart AS pArt,\n  probe.probeentnahme_beginn AS peBegin,\n  probe.probeentnahme_ende AS peEnd,\n  ort.gem_id AS eGemId,\n  verwaltungseinheit.bezeichnung AS eGem,\n  coalesce(k40.messwert_nwg, ' ') || to_char(k40.messwert, '0.99eeee') AS k40,\n  coalesce(co60.messwert_nwg, ' ') || to_char(co60.messwert, '0.99eeee') AS co60,\n  coalesce(cs137.messwert_nwg, ' ') || to_char(cs137.messwert, '0.99eeee') AS cs137\nFROM land.probe\nLEFT JOIN stamm.mess_stelle\n  ON (probe.mst_id = stamm.mess_stelle.id)\nINNER JOIN land.messung\n  ON probe.id = messung.probe_id\nINNER JOIN land.status_protokoll\n  ON messung.STATUS = status_protokoll.id\nLEFT JOIN stamm.status_kombi\n  ON status_protokoll.status_kombi = stamm.status_kombi.id\nLEFT JOIN stamm.status_wert\n  ON stamm.status_wert.id = stamm.status_kombi.wert_id\nLEFT JOIN stamm.status_stufe\n  ON stamm.status_stufe.id = stamm.status_kombi.stufe_id\nLEFT JOIN stamm.datenbasis\n  ON (probe.datenbasis_id = datenbasis.id)\nLEFT JOIN stamm.probenart\n  ON (probe.probenart_id = probenart.id)\nLEFT JOIN land.ortszuordnung\n  ON (\n      probe.id = ortszuordnung.probe_id\n      AND ortszuordnung.ortszuordnung_typ = 'E'\n      )\nLEFT JOIN stamm.ort\n  ON (ortszuordnung.ort_id = ort.id)\nLEFT JOIN stamm.verwaltungseinheit\n  ON (ort.gem_id = verwaltungseinheit.id)\nLEFT JOIN land.messwert k40\n  ON (\n      k40.messungs_id = messung.id\n      AND k40.messgroesse_id = 28\n      )\nLEFT JOIN land.messwert co60\n  ON (\n      co60.messungs_id = messung.id\n      AND co60.messgroesse_id = 68\n      )\nLEFT JOIN land.messwert cs137\n  ON (\n      cs137.messungs_id = messung.id\n      AND cs137.messgroesse_id = 373\n      )\n
16	SELECT messung.id,\n  probe.id AS probeId,\n  probe.hauptproben_nr AS hpNr,\n  messung.nebenproben_nr AS npNr,\n  status_stufe.stufe AS statusSt,\n  status_wert.wert AS statusW,\n  status_protokoll.datum AS statusD,\n  datenbasis.datenbasis AS dBasis,\n  mess_stelle.netzbetreiber_id AS netzId,\n  probe.mst_id AS mstId,\n  probe.umw_id AS umwId,\n  probenart.probenart AS pArt,\n  probe.probeentnahme_beginn AS peBegin,\n  probe.probeentnahme_ende AS peEnd,\n  ort.gem_id AS eGemId,\n  verwaltungseinheit.bezeichnung AS eGem,\n  coalesce(h3.messwert_nwg, ' ') || to_char(h3.messwert, '0.99eeee') AS h3,\n  coalesce(k40.messwert_nwg, ' ') || to_char(k40.messwert, '0.99eeee') AS k40,\n  coalesce(co60.messwert_nwg, ' ') || to_char(co60.messwert, '0.99eeee') AS co60,\n  coalesce(sr89.messwert_nwg, ' ') || to_char(sr89.messwert, '0.99eeee') AS sr89,\n  coalesce(sr90.messwert_nwg, ' ') || to_char(sr90.messwert, '0.99eeee') AS sr90,\n  coalesce(ru103.messwert_nwg, ' ') || to_char(ru103.messwert, '0.99eeee') AS ru103,\n  coalesce(i131.messwert_nwg, ' ') || to_char(i131.messwert, '0.99eeee') AS i131,\n  coalesce(cs134.messwert_nwg, ' ') || to_char(cs134.messwert, '0.99eeee') AS cs134,\n  coalesce(cs137.messwert_nwg, ' ') || to_char(cs137.messwert, '0.99eeee') AS cs137,\n  coalesce(ce144.messwert_nwg, ' ') || to_char(ce144.messwert, '0.99eeee') AS ce144,\n  coalesce(u234.messwert_nwg, ' ') || to_char(u234.messwert, '0.99eeee') AS u234,\n  coalesce(u235.messwert_nwg, ' ') || to_char(u235.messwert, '0.99eeee') AS u235,\n  coalesce(u238.messwert_nwg, ' ') || to_char(u238.messwert, '0.99eeee') AS u238,\n  coalesce(pu238.messwert_nwg, ' ') || to_char(pu238.messwert, '0.99eeee') AS pu238,\n  coalesce(pu239.messwert_nwg, ' ') || to_char(pu239.messwert, '0.99eeee') AS pu239,\n  coalesce(pu23940.messwert_nwg, ' ') || to_char(pu23940.messwert, '0.99eeee') AS pu23940,\n  coalesce(te132.messwert_nwg, ' ') || to_char(te132.messwert, '0.99eeee') AS te132,\n  coalesce(pb212.messwert_nwg, ' ') || to_char(pb212.messwert, '0.99eeee') AS pb212,\n  coalesce(pb214.messwert_nwg, ' ') || to_char(pb214.messwert, '0.99eeee') AS pb214,\n  coalesce(bi212.messwert_nwg, ' ') || to_char(bi212.messwert, '0.99eeee') AS bi212,\n  coalesce(bi214.messwert_nwg, ' ') || to_char(bi214.messwert, '0.99eeee') AS bi214\nFROM land.probe\nLEFT JOIN stamm.mess_stelle\n  ON (probe.mst_id = stamm.mess_stelle.id)\nINNER JOIN land.messung\n  ON probe.id = messung.probe_id\nINNER JOIN land.status_protokoll\n  ON messung.STATUS = status_protokoll.id\nLEFT JOIN stamm.status_kombi\n  ON status_protokoll.status_kombi = stamm.status_kombi.id\nLEFT JOIN stamm.status_wert\n  ON stamm.status_wert.id = stamm.status_kombi.wert_id\nLEFT JOIN stamm.status_stufe\n  ON stamm.status_stufe.id = stamm.status_kombi.stufe_id\nLEFT JOIN stamm.datenbasis\n  ON (probe.datenbasis_id = datenbasis.id)\nLEFT JOIN stamm.probenart\n  ON (probe.probenart_id = probenart.id)\nLEFT JOIN land.ortszuordnung\n  ON (\n      probe.id = ortszuordnung.probe_id\n      AND ortszuordnung.ortszuordnung_typ = 'E'\n      )\nLEFT JOIN stamm.ort\n  ON (ortszuordnung.ort_id = ort.id)\nLEFT JOIN stamm.verwaltungseinheit\n  ON (ort.gem_id = verwaltungseinheit.id)\nLEFT JOIN land.messwert h3\n  ON (h3.messungs_id = messung.id AND h3.messgroesse_id = 1)\nLEFT JOIN land.messwert k40\n  ON (k40.messungs_id = messung.id AND k40.messgroesse_id = 28)\nLEFT JOIN land.messwert co60\n  ON (co60.messungs_id = messung.id AND co60.messgroesse_id = 68)\nLEFT JOIN land.messwert sr89\n  ON (sr89.messungs_id = messung.id AND sr89.messgroesse_id = 164)\nLEFT JOIN land.messwert sr90\n  ON (sr90.messungs_id = messung.id AND sr90.messgroesse_id = 165)\nLEFT JOIN land.messwert ru103\n  ON (ru103.messungs_id = messung.id AND ru103.messgroesse_id = 220)\nLEFT JOIN land.messwert i131\n  ON (i131.messungs_id = messung.id AND i131.messgroesse_id = 340)\nLEFT JOIN land.messwert cs134\n  ON (cs134.messungs_id = messung.id AND cs134.messgroesse_id = 369)\nLEFT JOIN land.messwert cs137\n  ON (cs137.messungs_id = messung.id AND cs137.messgroesse_id = 373)\nLEFT JOIN land.messwert ce144\n  ON (ce144.messungs_id = messung.id AND ce144.messgroesse_id = 404)\nLEFT JOIN land.messwert u234\n  ON (u234.messungs_id = messung.id AND u234.messgroesse_id = 746)\nLEFT JOIN land.messwert u235\n  ON (u235.messungs_id = messung.id AND u235.messgroesse_id = 747)\nLEFT JOIN land.messwert u238\n  ON (u238.messungs_id = messung.id AND u238.messgroesse_id = 750)\nLEFT JOIN land.messwert pu238\n  ON (pu238.messungs_id = messung.id AND pu238.messgroesse_id = 768)\nLEFT JOIN land.messwert pu239\n  ON (pu239.messungs_id = messung.id AND pu239.messgroesse_id = 769)\nLEFT JOIN land.messwert pu23940\n  ON (pu23940.messungs_id = messung.id AND pu23940.messgroesse_id = 850)\nLEFT JOIN land.messwert te132\n  ON (te132.messungs_id = messung.id AND te132.messgroesse_id = 325)\nLEFT JOIN land.messwert pb212\n  ON (pb212.messungs_id = messung.id AND pb212.messgroesse_id = 672)\nLEFT JOIN land.messwert pb214\n  ON (pb214.messungs_id = messung.id AND pb214.messgroesse_id = 673)\nLEFT JOIN land.messwert bi212\n  ON (bi212.messungs_id = messung.id AND bi212.messgroesse_id = 684)\nLEFT JOIN land.messwert bi214\n  ON (bi214.messungs_id = messung.id AND bi214.messgroesse_id = 686)
17	SELECT messung.id,\r\n  probe.id AS probeId,\r\n  probe.hauptproben_nr AS hpNr,\r\n  messung.nebenproben_nr AS npNr,\r\n  stamm.status_stufe.stufe AS statusSt,\r\n  stamm.status_wert.wert AS statusW,\r\n  status_protokoll.datum AS statusD,\r\n  datenbasis.datenbasis AS dBasis,\r\n  stamm.mess_stelle.netzbetreiber_id AS netzId,\r\n  probe.mst_id AS mstId,\r\n  probe.umw_id AS umwId,\r\n  probenart.probenart AS pArt,\r\n  probe.probeentnahme_beginn AS peBegin,\r\n  probe.probeentnahme_ende AS peEnd,\r\n  ort.gem_id AS eGemId,\r\n  verwaltungseinheit.bezeichnung AS eGem,\r\n  coalesce(sr89.messwert_nwg, ' ') || to_char(sr89.messwert, '0.99eeee') AS sr89,\r\n  coalesce(sr90.messwert_nwg, ' ') || to_char(sr90.messwert, '0.99eeee') AS sr90\r\nFROM land.probe\r\nLEFT JOIN stamm.mess_stelle\r\n  ON (probe.mst_id = stamm.mess_stelle.id)\r\nINNER JOIN land.messung\r\n  ON probe.id = messung.probe_id\r\nINNER JOIN land.status_protokoll\r\n  ON messung.STATUS = status_protokoll.id\r\nLEFT JOIN stamm.status_kombi\r\n  ON status_protokoll.status_kombi = stamm.status_kombi.id\r\nLEFT JOIN stamm.status_wert\r\n  ON stamm.status_wert.id = stamm.status_kombi.wert_id\r\nLEFT JOIN stamm.status_stufe\r\n  ON stamm.status_stufe.id = stamm.status_kombi.stufe_id\r\nLEFT JOIN stamm.datenbasis\r\n  ON (probe.datenbasis_id = datenbasis.id)\r\nLEFT JOIN stamm.probenart\r\n  ON (probe.probenart_id = probenart.id)\r\nLEFT JOIN land.ortszuordnung\r\n  ON (\r\n      probe.id = ortszuordnung.probe_id\r\n      AND ortszuordnung.ortszuordnung_typ = 'E'\r\n      )\r\nLEFT JOIN stamm.ort\r\n  ON (ortszuordnung.ort_id = ort.id)\r\nLEFT JOIN stamm.verwaltungseinheit\r\n  ON (ort.gem_id = verwaltungseinheit.id)\r\nLEFT JOIN land.messwert sr89\r\n  ON (sr89.messungs_id = messung.id AND sr89.messgroesse_id = 164)\r\nLEFT JOIN land.messwert sr90\r\n  ON (sr90.messungs_id = messung.id AND sr90.messgroesse_id = 165)\r\n
18	SELECT messung.id,\n  probe.id AS probeId,\n  probe.hauptproben_nr AS hpNr,\n  messung.nebenproben_nr AS npNr,\n  status_stufe.stufe AS statusSt,\n  status_wert.wert AS statusW,\n  status_protokoll.datum AS statusD,\n  datenbasis.datenbasis AS dBasis,\n  mess_stelle.netzbetreiber_id AS netzId,\n  probe.mst_id AS mstId,\n  probe.umw_id AS umwId,\n  probenart.probenart AS pArt,\n  probe.probeentnahme_beginn AS peBegin,\n probe.probeentnahme_ende AS peEnd,\n  ort.gem_id AS eGemId,\n  verwaltungseinheit.bezeichnung AS eGem,\n  mw_pivot.h_3 AS h3,\n  mw_pivot.k_40 AS k40,\n  mw_pivot.co_60 AS co60,\n  mw_pivot.sr_89 AS sr89,\n  mw_pivot.sr_90 AS sr90,\n  mw_pivot.ru_103 AS ru103,\n  mw_pivot.i_131 AS i131,\n  mw_pivot.cs_134 AS cs134,\n  mw_pivot.cs_137 AS cs137,\n  mw_pivot.ce_144 AS ce144,\n  mw_pivot.u_234 AS u234,\n  mw_pivot.u_235 AS u235,\n  mw_pivot.u_238 AS u238,\n  mw_pivot.pu_238 AS pu238,\n  mw_pivot.pu_239 AS pu239,\n  mw_pivot.pu_23940 AS pu23940,\n  mw_pivot.te_132 AS te132,\n  mw_pivot.pb_212 AS pb212,\n  mw_pivot.pb_214 AS pb214,\n  mw_pivot.bi_212 AS bi212,\n  mw_pivot.bi_214 AS bi214\nFROM land.probe\nLEFT JOIN stamm.mess_stelle\n  ON (probe.mst_id = stamm.mess_stelle.id)\nINNER JOIN land.messung\n  ON probe.id = messung.probe_id\nINNER JOIN land.status_protokoll\n  ON messung.STATUS = status_protokoll.id\nLEFT JOIN stamm.status_kombi\n  ON status_protokoll.status_kombi = stamm.status_kombi.id\nLEFT JOIN stamm.status_wert\n  ON stamm.status_wert.id = stamm.status_kombi.wert_id\nLEFT JOIN stamm.status_stufe\n  ON stamm.status_stufe.id = stamm.status_kombi.stufe_id\nLEFT JOIN stamm.datenbasis\n  ON (probe.datenbasis_id = datenbasis.id)\nLEFT JOIN stamm.probenart\n  ON (probe.probenart_id = probenart.id)\nLEFT JOIN land.ortszuordnung\n  ON (\n      probe.id = ortszuordnung.probe_id\n      AND ortszuordnung.ortszuordnung_typ = 'E'\n      )\nLEFT JOIN stamm.ort\n  ON (ortszuordnung.ort_id = ort.id)\nLEFT JOIN stamm.verwaltungseinheit\n  ON (ort.gem_id = verwaltungseinheit.id)\nLEFT JOIN (\n  SELECT\n    messungs_id, meh_id, h_3, k_40, co_60, sr_89, sr_90, ru_103,\n    i_131, cs_134, cs_137, ce_144, u_234, u_235, u_238, pu_238,\n    pu_239, pu_23940, te_132, pb_212, pb_214, bi_212, bi_214\n  FROM crosstab(\n    'SELECT messwert.messungs_id, messwert.meh_id, messwert.messgroesse_id, COALESCE(messwert.messwert_nwg, '' '') || to_char(messwert.messwert, ''9.99eeee'') FROM land.messung INNER JOIN land.messwert ON messung.id = messung.id WHERE messgroesse_id IN (1, 28, 68, 164, 165, 220, 340, 369, 373, 404, 746, 747, 750, 768, 769, 850, 325, 672, 673, 684, 686) ORDER BY 1',\n    'SELECT mg_id FROM (VALUES (1), (28), (68), (164), (165), (220), (340), (369), (373), (404), (746), (747), (750), (768), (769), (850), (325), (672), (673), (684), (686)) AS t (mg_id)'\n  ) AS (\n    messungs_id integer, meh_id integer,\n    h_3 character varying(10), k_40 character varying(10),\n    co_60 character varying(10), sr_89 character varying(10),\n    sr_90 character varying(10), ru_103 character varying(10),\n    i_131 character varying(10), cs_134 character varying(10),\n    cs_137 character varying(10), ce_144 character varying(10),\n    u_234 character varying(10), u_235 character varying(10),\n    u_238 character varying(10), pu_238 character varying(10),\n    pu_239 character varying(10), pu_23940 character varying(10),\n    te_132 character varying(10), pb_212 character varying(10),\n    pb_214 character varying(10), bi_212 character varying(10),\n    bi_214 character varying(10)\n  )\n) AS mw_pivot\n  ON mw_pivot.messungs_id = messung.id\nLEFT JOIN stamm.mess_einheit\n  ON mess_einheit.id = mw_pivot.meh_id
\.

COPY query_user (id, name, user_id, base_query, description) FROM stdin;
1	Proben	\N	1	Alle Proben
7	Proben pro Land und UMW (Multiselect)	\N	7	Abfrage aller Proben gefiltert pro Land und Umweltbereich (mit Mehrfachauswahl)
9	Messprogramme	\N	9	Abfrage der Messprogramme ohne Filter
10	Orte	\N	10	Abfrage der Orte
11	Probenehmer	\N	11	Abfrage der Probenehmer
12	Datensatzerzeuger	\N	12	Abfrage der Datensatzerzeuger
13	alle nach Status	\N	13	Messungen nach Status
14	Messprogrammkategorie	\N	14	Abfrage der Messprogrammkategorien
15	LSt G - Messungen	\N	15	Gammaspektrometriemessungen zur LSt-Bearbeitung
16	Land Messungen 	\N	16	Messungen zur Statusvergabe auf Land-Ebene
17	LSt Sr - Messungen	\N	17	Strontium-Messungen zur LSt-Bearbeitung
18	MST Messungen	\N	18	Messungen zur Statusvergabe auf MST-Ebene
\.

COPY filter (id, sql, parameter, type, name) FROM stdin;
1	probe.id_alt IN :idAlt	idAlt	0	probe_id_alt
2	probe.hauptproben_nr IN :hauptprobenNr	hauptprobenNr	0	probe_hauptproben_nr
3	probe.mst_id = :mstId	mstId	5	probe_mst_id
4	probe.umw_id = :umwId	umwId	9	probe_umw_id
5	probe.test = cast(:test AS boolean)	test	2	probe_test
6	probe.probeentnahme_beginn >= to_timestamp(cast(:timeBegin AS double_precision))	timeBegin	3	probe_entnahme_beginn
7	probe.probeentnahme_ende <= to_timestamp(cast(:timeEnd AS double_precision))	timeEnd	3	probe_entnahme_beginn
8	datenbasis.datenbasis = :datenbasis	datenbasis	0	datenbasis
9	probenart.probenart = :probenart	probenart	0	probenart
10	ort.gem_id = :gemId	gemId	0	ort_gem_id
11	ort.ort_id = :ortId	ortId	0	ort_ort_id
12	verwaltungseinheit.bezeichnung IN :bezeichnung	bezeichnung	0	verwaltungseinheit_bezeichnung
13	stamm.mess_stelle.netzbetreiber_id = :netzbetreiberId	netzbetreiberId	7	netzbetreiber_id
\.

--
-- TOC entry 5410 (class 0 OID 3061619)
-- Dependencies: 252
-- Data for Name: filter; Type: TABLE DATA; Schema: stamm; Owner: postgres
--

--
-- TOC entry 5412 (class 0 OID 3061892)
-- Dependencies: 284
-- Data for Name: result_type; Type: TABLE DATA; Schema: stamm; Owner: postgres
--

COPY result_type (id, name, format) FROM stdin;
1	text	\N
2	date	d.m.Y H:i
3	number	\N
4	probeId	\N
5	messungId	\N
6	ortId	\N
7	geom	\N
\.


--
-- TOC entry 5414 (class 0 OID 3061900)
-- Dependencies: 286
-- Data for Name: result; Type: TABLE DATA; Schema: stamm; Owner: postgres
--

COPY grid_column (id, base_query, name, data_index, position, filter, data_type) FROM stdin;
1	1	Id	probeId	1	\N	4
2	1	Hauptproben Nummer	hauptprobenNr	2	2	1
3	1	Datenbasis	dBasis	3	8	1
4	1	Land	netzId	4	13	1
5	1	Messstelle	mstId	5	3	1
6	1	Umweltbereich	umwId	6	9	1
7	1	Probenart	pArt	7	1	1
8	1	Entnahme von	peBegin	8	3	2
9	1	Entnahme bis	peEnd	9	3	2
10	1	Ort	ortId	10	1	1
11	1	Gemeinde Id	eGemId	11	1	1
12	1	Gemeinde	eGem	12	1	1
13	1	Probennummer	idAlt	13	1	1
14	7	Id	probeId	1	\N	4
15	7	Hauptproben Nummer 	hpNr	2	2	1
16	7	Datenbasis	dBasis	3	8	1
17	7	Netzbetreiber	netzId	4	13	1
18 	7	Messstelle	mstId	5	3	1
19	7	Umweltbereich	umdId	6	4	1
20	7	Probenart	pArt	7	9	1
21	7	Entnahme von	peBegin	8	3	2
22	7	Entnahme bis	peEnd	9	3	2
23	7	Ort	ortId	10	11	1
24	7	Gemeinde Id	eGemId	11	\N	1
25	7	Probennummer	idAlt	12	1	1
26	9	Id	id	1	\N	1
27	9	Messprogramm 	mpNr	2	\N	1
28	9	netzbetreiber	netzId 	3	13	1
29	9	Messstelle	mstLaborId	4	\N	1
30	9	Datenbasis	dBasis	5	8	1
31	9	Messregime	messRegime	6	\N	1
32	9	Probenart	pArt	7	\N	1
33	9	Umweltbereich	umwId	8	\N	1
34	9	Deskriptoren	deskriptoren	9	\N	1
35	9	Probenintervall	intervall	10	\N	1
36	9	Ort	ortId	11	\N	1
37	9	Gemeinde ID	eGemId	12	\N	1
38	9	Verwaltungseinheit	eGem	13	\N	1
39	10	Id	id	1	11	6
40	10	Netzbetreiber	netzbetreiberId	2	13	1
41	10	Ort	ortId	3	11	6
42	10	Ort Typ	ortTyp	4	\N	1
43	10	Kurtext	kurztext	5	\N	1
44	10	Langtext	langtext	6	\N	1
45	10	Staat	staat	7	\N	1
46	10	Verwaltungseinheit	verwaltungseinheit	8	12	1
47	10	NUTS Code	nutsCode	9	\N	1
48	10	Ortszuordnung	ozId	10	\N	1
49	10	KTA Gruppe	anlageId	11	\N	1
50	10	Messprogramm	mpArt	12	\N	1
51	10	Koordinatenart	koordinatenArt	13	\N	1
52	10	Koordinate X extern	koordXExtern	14	\N	1
53	10 	Koordinate Y extern	koordYExtern	15	\N	1
54	10	Longitude	longitude	16	\N	1
55	10	Latitude	latitude	17	\N	1
56	10	Hoehe ueber NN	hoeheUeberNn	18	\N	1
57	10	Hoehe Land	hoeheLand	19	\N	1
58	10	Aktiv	aktiv	20	\N	1
59	10	Letzte Anderung	letzteAenderung	21	\N	2
60	11	Id	id	1	\N	1
61	11	Netzbetreiber	netzbetreiberId	2	13	1
62	11	prn	prnId	3	\N	1
63	11	Bearbeiter	bearbeiter	4	\N	1
64	11	Bemerkung	bemerkung	5	\N	1
65	11	Betrieb	betrieb	6	\N	1
66	11	Bezeichnung	bezeichnung	7	\N	1
67	11	Kurzbezeichnung	kurzBezeichnung	8	\N	1
68	11	Ort	ort	9	\N	1
69	11	PLZ	plz	10	\N	1
70	11	Strasse	strasse	11	\N	1
71	11	Telefon	telefon	12	\N	1
72	11	tp	tp	13	\N	1
73	11	Typ	typ	14	\N	1
74	11	Letzte Aenderung	letzteAenderung	15	\N	2
75	12	Id	id	1	\N	1
76	12	Datensatzerzeuger	datensatzErzeugerId	2	\N	1
77	12	Messstelle	mstId	3	3	1
78	12	Bezeichnung	bezeichnung	4	\N	1
79	12	Letzte Aenderung	letzteAenderung	5	\N	2
80	13	Id	id	1	\N	5
81	13	Probe	probeId	2	\N	4
82	13	Probennummer	hpNr	3	2	1
83	13	Nebenprobennummer	npNr	4	\N	1
84	13	Status Datum	statusD	5	\N	2
85	13	Statusstufe	statusSt	6	\N	1
86	13	Statuswert	statusW	7	\N	1
87	13	Datenbasis	dBasis	8	8	1
88	13	Netzbetreiber	netzId	9	13	1
89	13	Messstelle	mstId	10	3	1
90	13	Umweltbereich	umwId	11	4	1
91	13	Probenart	pArt	12	9	1
92	13	Probenentnahme beginn	peBegin	13	6	2
93	13	Probenentnahme ende	peEnd	15	7	2
94	13	Ort	ortId	16	11	6
95	13	Gemeinde	eGemId	17	\N	1
96	13	Bezeichnung	eGem	18	\N	1
97	14	Id	id	1	\N	1
98	14	Netzbetreiber	netzbetreiberId	2	13	1
99	14	Code	code	3	\N	1
100	14	Bezeichnung	bezeichnung	4	\N	1
101	14	Letzte Aenderung	letzteAenderung	5	\N	2
102	15	Id	id	1	\N	5
103	15	Probe	probeId	2	\N	4
104	15	Probennummer	hpNr	3	2	1
105	15	Nebenprobennummer	npNr	4	\N	1
106	15	Status Stufe	statusSt	5	\N	1
107	15	Status Wert	statusW	6	\N	1
108	15	Status Datum	statusD	7	\N	2
109	15	Datenbasis	dBasis	8	8	1
110	15	Netzbetreiber	netzId	9	13	1
111	15	Messstelle	mstId	10	3	1
112	15	Umweltbereich	umwId	11	\N	1
113	15	Probenart	pArt	12	9	1
114	15	Probenentnahme beginn	peBegin	13	6	2
115	15	Probenentnahme ende	peEnd	14	7	2
116	15	Gemeinde	eGem	15	\N	1
117	15	k40	k40	16	\N	3
118	15	co60	co60	17	\N	3
119	15	cs137	cs137	18	\N	3
\.

COPY grid_column_values (id, user_id, grid_column, sort, sort_index, filter_value, filter_active, visible, column_index, width) FROM stdin;
1	\N	1	\N	\N	\N	f	f	-1	0
2	\N	2	\N	\N	\N	f	f	0	100
3	\N	3	\N	\N	\N	f	f	1	100
4	\N	4	\N	\N	\N	f	f	2	100
5	\N	5	\N	\N	\N	f	f	3	100
6	\N	6	\N	\N	\N	f	f	4	100
7	\N	7	\N	\N	\N	f	f	5	100
8	\N	8	\N	\N	\N	f	f	6	100
9	\N	9	\N	\N	\N	f	f	7	100
10	\N	10	\N	\N	\N	f	f	8	100
11	\N	11	\N	\N	\N	f	f	9	100
12	\N	12	\N	\N	\N	f	f	10	100
13	\N	13	\N	\N	\N	f	f	11	100
\.

--
-- TOC entry 5419 (class 0 OID 0)
-- Dependencies: 251
-- Name: filter_id_seq; Type: SEQUENCE SET; Schema: stamm; Owner: postgres
--

SELECT pg_catalog.setval('filter_id_seq', 26, true);


--
-- TOC entry 5420 (class 0 OID 0)
-- Dependencies: 245
-- Name: query_id_seq; Type: SEQUENCE SET; Schema: stamm; Owner: postgres
--

SELECT pg_catalog.setval('base_query_id_seq', 18, true);


--
-- TOC entry 5421 (class 0 OID 0)
-- Dependencies: 285
-- Name: result_id_seq; Type: SEQUENCE SET; Schema: stamm; Owner: postgres
--

SELECT pg_catalog.setval('result_id_seq', 291, true);


--
-- TOC entry 5422 (class 0 OID 0)
-- Dependencies: 283
-- Name: result_type_id_seq; Type: SEQUENCE SET; Schema: stamm; Owner: postgres
--

SELECT pg_catalog.setval('result_type_id_seq', 1, false);


-- Completed on 2018-01-22 13:18:15 CET

--
-- PostgreSQL database dump complete
--

