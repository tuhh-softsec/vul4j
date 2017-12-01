--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.2
-- Dumped by pg_dump version 9.6.5

-- Started on 2017-10-18 08:57:22 CEST

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

SET search_path = stamm, pg_catalog;

--
-- TOC entry 5408 (class 0 OID 671928)
-- Dependencies: 237
-- Data for Name: query; Type: TABLE DATA; Schema: stamm; Owner: postgres
--

COPY query (id, name, type, sql, description) FROM stdin;
10	Orte	3	SELECT ort.id, ort.netzbetreiber_id AS netzbetreiberId, ort.ort_id AS ortId, ort_typ.ort_typ AS ortTyp, ort.kurztext, ort.langtext, verwaltungseinheit.bezeichnung AS verwaltungseinheit, staat.staat_iso AS staat, nuts_code AS nutsCode, oz_id AS ozId, anlage_id AS anlageId, mp_art AS mpArt, zone, sektor, zustaendigkeit, berichtstext, unscharf, koordinaten_art.koordinatenart AS koordinatenArt, ort.koord_x_extern AS koordXExtern, ort.koord_y_extern AS koordYExtern, public.ST_X(ort.geom) AS longitude, public.ST_Y(ort.geom) AS latitude, hoehe_land AS hoeheLand, letzte_aenderung AS letzteAenderung, aktiv, hoehe_ueber_nn AS hoeheUeberNn FROM stamm.ort JOIN stamm.verwaltungseinheit ON ort.gem_id = verwaltungseinheit.id JOIN stamm.staat ON stamm.staat.id = ort.staat_id JOIN stamm.koordinaten_art ON stamm.koordinaten_art.id = ort.kda_id JOIN stamm.ort_typ ON ort.ort_typ = ort_typ.id WHERE (netzbetreiber_id = :netzbetreiberId OR '' = :netzbetreiberId)	Abfrage der Orte
11	Probenehmer	4		Abfrage der Probenehmer
12	Datensatzerzeuger	5		Abfrage der Datensatzerzeuger
14	Messprogrammkategorie	6		Abfrage der Messprogrammkategorien
7	Proben pro Land und UMW (Multiselect)	0	SELECT probe.id AS id, probe.hauptproben_nr AS hpNr, datenbasis.datenbasis AS dBasis, stamm.mess_stelle.netzbetreiber_id AS netzId, probe.mst_id AS mstId, probe.umw_id AS umwId, probenart.probenart AS pArt, to_char(probe.probeentnahme_beginn, 'dd.mm.YYYY hh24:MI') AS peBegin, to_char(probe.probeentnahme_ende, 'dd.mm.YYYY hh24:MI') AS peEnd, ort.ort_id AS ortId, ort.gem_id AS eGemId, verwaltungseinheit.bezeichnung AS eGem, probe.id_alt AS probeId FROM land.probe LEFT JOIN stamm.mess_stelle ON (probe.mst_id = stamm.mess_stelle.id) LEFT JOIN stamm.datenbasis ON (probe.datenbasis_id = datenbasis.id) LEFT JOIN stamm.probenart ON (probe.probenart_id = probenart.id) LEFT OUTER JOIN land.ortszuordnung ON ( probe.id = ortszuordnung.probe_id AND ortszuordnung.ortszuordnung_typ = 'E' ) LEFT OUTER JOIN stamm.ort ON (ortszuordnung.ort_id = ort.id) LEFT OUTER JOIN stamm.verwaltungseinheit ON (ort.gem_id = verwaltungseinheit.id) WHERE (stamm.mess_stelle.netzbetreiber_id = :netzIdFilter OR '' =:netzIdFilter) AND (probe.umw_id SIMILAR TO (:umwIdFilter) OR '' = :umwIdFilter)	Abfrage aller Proben gefiltert pro Land und Umweltbereich (mit Mehrfachauswahl)
9	Messprogramm pro Land	2	SELECT messprogramm.id,\n  messprogramm.id AS mpNr,\n  stamm.mess_stelle.netzbetreiber_id AS netzId,\n  CASE \n    WHEN messprogramm.mst_id = messprogramm.labor_mst_id\n      THEN messprogramm.mst_id\n    ELSE messprogramm.mst_id || '-' || messprogramm.labor_mst_id\n    END AS mstLaborId,\n  datenbasis.datenbasis AS dBasis,\n  CASE \n    WHEN messprogramm.ba_id = '1'\n      THEN 'RB'\n    ELSE 'IB'\n    END AS messRegime,\n  probenart.probenart AS pArt,\n  messprogramm.umw_id AS umwId,\n  messprogramm.media_desk AS deskriptoren,\n  messprogramm.probenintervall AS intervall,\n  ort.ort_id AS ortId,\n  ort.gem_id AS eGemId,\n  verwaltungseinheit.bezeichnung AS eGem\nFROM land.messprogramm\nLEFT JOIN stamm.mess_stelle\n  ON (messprogramm.mst_id = stamm.mess_stelle.id)\nLEFT JOIN stamm.datenbasis\n  ON (messprogramm.datenbasis_id = datenbasis.id)\nLEFT JOIN stamm.probenart\n  ON (messprogramm.probenart_id = probenart.id)\nLEFT JOIN land.ortszuordnung_mp\n  ON (\n      messprogramm.id = ortszuordnung_mp.messprogramm_id\n      AND ortszuordnung_mp.ortszuordnung_typ = 'E'\n      )\nLEFT JOIN stamm.ort\n  ON (ortszuordnung_mp.ort_id = ort.id)\nLEFT JOIN stamm.verwaltungseinheit\n  ON (ort.gem_id = verwaltungseinheit.id)\nWHERE (\n    mess_stelle.netzbetreiber_id = :netzIdFilter\n    OR '' = :netzIdFilter\n    )\n  AND (\n    messprogramm.umw_id SIMILAR TO (:umwIdFilter)\n    OR '' = :umwIdFilter\n    )	Abfrage der Messprogramme ohne Filter
1	Proben	0	SELECT probe.id AS id,\n  probe.hauptproben_nr AS hpNr,\n  datenbasis.datenbasis AS dBasis,\n  stamm.mess_stelle.netzbetreiber_id AS netzId,\n  probe.mst_id AS mstId,\n  probe.umw_id AS umwId,\n  probenart.probenart AS pArt,\n  to_char(probe.probeentnahme_beginn, 'dd.mm.YYYY hh24:MI') AS peBegin,\n  to_char(probe.probeentnahme_ende, 'dd.mm.YYYY hh24:MI') AS peEnd,\n  ort.ort_id AS ortId,\n  ort.gem_id AS eGemId,\n  verwaltungseinheit.bezeichnung AS eGem,\n  probe.id_alt AS probeId\nFROM land.probe\nLEFT JOIN stamm.mess_stelle\n  ON (probe.mst_id = stamm.mess_stelle.id)\nLEFT JOIN stamm.datenbasis\n  ON (probe.datenbasis_id = datenbasis.id)\nLEFT JOIN stamm.probenart\n  ON (probe.probenart_id = probenart.id)\nLEFT JOIN land.ortszuordnung\n  ON (\n      probe.id = ortszuordnung.probe_id\n      AND ortszuordnung.ortszuordnung_typ = 'E'\n      )\nLEFT JOIN stamm.ort\n  ON (ortszuordnung.ort_id = ort.id)\nLEFT JOIN stamm.verwaltungseinheit\n  ON (ort.gem_id = verwaltungseinheit.id)\nWHERE (\n    probe.id_alt LIKE CASE \n      WHEN :probeIdFilter = ''\n        THEN '%'\n      ELSE :probeIdFilter\n      END\n    )\n  AND (\n    coalesce(probe.hauptproben_nr, '') LIKE CASE \n      WHEN :hpNrFilter = ''\n        THEN '%'\n      ELSE :hpNrFilter\n      END\n    )\n  AND (\n    stamm.mess_stelle.netzbetreiber_id = :netzIdFilter\n    OR '' = :netzIdFilter\n    )\n  AND (\n    probe.mst_id = :mstIdFilter\n    OR '' = :mstIdFilter\n    )\n  AND (\n    probe.umw_id = :umwIdFilter\n    OR '' = :umwIdFilter\n    )	Abfrage aller  Proben ohne Filter
16	Land Messungen	1	SELECT messung.id,\n  probe.id AS probeId,\n  probe.hauptproben_nr AS hpNr,\n  messung.nebenproben_nr AS npNr,\n  status_stufe.stufe AS statusSt,\n  status_wert.wert AS statusW,\n  to_char(status_protokoll.datum, 'dd.mm.YYYY hh24:MI') AS statusD,\n  datenbasis.datenbasis AS dBasis,\n  mess_stelle.netzbetreiber_id AS netzId,\n  probe.mst_id AS mstId,\n  probe.umw_id AS umwId,\n  probenart.probenart AS pArt,\n  to_char(probe.probeentnahme_beginn, 'dd.mm.YYYY hh24:MI') AS peBegin,\n  to_char(probe.probeentnahme_ende, 'dd.mm.YYYY hh24:MI') AS peEnd,\n  ort.gem_id AS eGemId,\n  verwaltungseinheit.bezeichnung AS eGem,\n  coalesce(h3.messwert_nwg, ' ') || to_char(h3.messwert, '0.99eeee') AS h3,\n  coalesce(k40.messwert_nwg, ' ') || to_char(k40.messwert, '0.99eeee') AS k40,\n  coalesce(co60.messwert_nwg, ' ') || to_char(co60.messwert, '0.99eeee') AS co60,\n  coalesce(sr89.messwert_nwg, ' ') || to_char(sr89.messwert, '0.99eeee') AS sr89,\n  coalesce(sr90.messwert_nwg, ' ') || to_char(sr90.messwert, '0.99eeee') AS sr90,\n  coalesce(ru103.messwert_nwg, ' ') || to_char(ru103.messwert, '0.99eeee') AS ru103,\n  coalesce(i131.messwert_nwg, ' ') || to_char(i131.messwert, '0.99eeee') AS i131,\n  coalesce(cs134.messwert_nwg, ' ') || to_char(cs134.messwert, '0.99eeee') AS cs134,\n  coalesce(cs137.messwert_nwg, ' ') || to_char(cs137.messwert, '0.99eeee') AS cs137,\n  coalesce(ce144.messwert_nwg, ' ') || to_char(ce144.messwert, '0.99eeee') AS ce144,\n  coalesce(u234.messwert_nwg, ' ') || to_char(u234.messwert, '0.99eeee') AS u234,\n  coalesce(u235.messwert_nwg, ' ') || to_char(u235.messwert, '0.99eeee') AS u235,\n  coalesce(u238.messwert_nwg, ' ') || to_char(u238.messwert, '0.99eeee') AS u238,\n  coalesce(pu238.messwert_nwg, ' ') || to_char(pu238.messwert, '0.99eeee') AS pu238,\n  coalesce(pu239.messwert_nwg, ' ') || to_char(pu239.messwert, '0.99eeee') AS pu239,\n  coalesce(pu23940.messwert_nwg, ' ') || to_char(pu23940.messwert, '0.99eeee') AS pu23940,\n  coalesce(te132.messwert_nwg, ' ') || to_char(te132.messwert, '0.99eeee') AS te132,\n  coalesce(pb212.messwert_nwg, ' ') || to_char(pb212.messwert, '0.99eeee') AS pb212,\n  coalesce(pb214.messwert_nwg, ' ') || to_char(pb214.messwert, '0.99eeee') AS pb214,\n  coalesce(bi212.messwert_nwg, ' ') || to_char(bi212.messwert, '0.99eeee') AS bi212,\n  coalesce(bi214.messwert_nwg, ' ') || to_char(bi214.messwert, '0.99eeee') AS bi214\nFROM land.probe\nLEFT JOIN stamm.mess_stelle\n  ON (probe.mst_id = stamm.mess_stelle.id)\nINNER JOIN land.messung\n  ON probe.id = messung.probe_id\nINNER JOIN land.status_protokoll\n  ON messung.STATUS = status_protokoll.id\nLEFT JOIN stamm.status_kombi\n  ON status_protokoll.status_kombi = stamm.status_kombi.id\nLEFT JOIN stamm.status_wert\n  ON stamm.status_wert.id = stamm.status_kombi.wert_id\nLEFT JOIN stamm.status_stufe\n  ON stamm.status_stufe.id = stamm.status_kombi.stufe_id\nLEFT JOIN stamm.datenbasis\n  ON (probe.datenbasis_id = datenbasis.id)\nLEFT JOIN stamm.probenart\n  ON (probe.probenart_id = probenart.id)\nLEFT JOIN land.ortszuordnung\n  ON (\n      probe.id = ortszuordnung.probe_id\n      AND ortszuordnung.ortszuordnung_typ = 'E'\n      )\nLEFT JOIN stamm.ort\n  ON (ortszuordnung.ort_id = ort.id)\nLEFT JOIN stamm.verwaltungseinheit\n  ON (ort.gem_id = verwaltungseinheit.id)\nLEFT JOIN land.messwert h3\n  ON (h3.messungs_id = messung.id AND h3.messgroesse_id = 1)\nLEFT JOIN land.messwert k40\n  ON (k40.messungs_id = messung.id AND k40.messgroesse_id = 28)\nLEFT JOIN land.messwert co60\n  ON (co60.messungs_id = messung.id AND co60.messgroesse_id = 68)\nLEFT JOIN land.messwert sr89\n  ON (sr89.messungs_id = messung.id AND sr89.messgroesse_id = 164)\nLEFT JOIN land.messwert sr90\n  ON (sr90.messungs_id = messung.id AND sr90.messgroesse_id = 165)\nLEFT JOIN land.messwert ru103\n  ON (ru103.messungs_id = messung.id AND ru103.messgroesse_id = 220)\nLEFT JOIN land.messwert i131\n  ON (i131.messungs_id = messung.id AND i131.messgroesse_id = 340)\nLEFT JOIN land.messwert cs134\n  ON (cs134.messungs_id = messung.id AND cs134.messgroesse_id = 369)\nLEFT JOIN land.messwert cs137\n  ON (cs137.messungs_id = messung.id AND cs137.messgroesse_id = 373)\nLEFT JOIN land.messwert ce144\n  ON (ce144.messungs_id = messung.id AND ce144.messgroesse_id = 404)\nLEFT JOIN land.messwert u234\n  ON (u234.messungs_id = messung.id AND u234.messgroesse_id = 746)\nLEFT JOIN land.messwert u235\n  ON (u235.messungs_id = messung.id AND u235.messgroesse_id = 747)\nLEFT JOIN land.messwert u238\n  ON (u238.messungs_id = messung.id AND u238.messgroesse_id = 750)\nLEFT JOIN land.messwert pu238\n  ON (pu238.messungs_id = messung.id AND pu238.messgroesse_id = 768)\nLEFT JOIN land.messwert pu239\n  ON (pu239.messungs_id = messung.id AND pu239.messgroesse_id = 769)\nLEFT JOIN land.messwert pu23940\n  ON (pu23940.messungs_id = messung.id AND pu23940.messgroesse_id = 850)\nLEFT JOIN land.messwert te132\n  ON (te132.messungs_id = messung.id AND te132.messgroesse_id = 325)\nLEFT JOIN land.messwert pb212\n  ON (pb212.messungs_id = messung.id AND pb212.messgroesse_id = 672)\nLEFT JOIN land.messwert pb214\n  ON (pb214.messungs_id = messung.id AND pb214.messgroesse_id = 673)\nLEFT JOIN land.messwert bi212\n  ON (bi212.messungs_id = messung.id AND bi212.messgroesse_id = 684)\nLEFT JOIN land.messwert bi214\n  ON (bi214.messungs_id = messung.id AND bi214.messgroesse_id = 686)	Messungen zur Statusvergabe auf Land-Ebene
18	MST Messungen	1	SELECT messung.id,\n  probe.id AS probeId,\n  probe.hauptproben_nr AS hpNr,\n  messung.nebenproben_nr AS npNr,\n  status_stufe.stufe AS statusSt,\n  status_wert.wert AS statusW,\n  to_char(status_protokoll.datum, 'dd.mm.YYYY hh24:MI') AS statusD,\n  datenbasis.datenbasis AS dBasis,\n  mess_stelle.netzbetreiber_id AS netzId,\n  probe.mst_id AS mstId,\n  probe.umw_id AS umwId,\n  probenart.probenart AS pArt,\n  to_char(probe.probeentnahme_beginn, 'dd.mm.YYYY hh24:MI') AS peBegin,\n  to_char(probe.probeentnahme_ende, 'dd.mm.YYYY hh24:MI') AS peEnd,\n  ort.gem_id AS eGemId,\n  verwaltungseinheit.bezeichnung AS eGem,\n  mw_pivot.h_3 AS h3,\n  mw_pivot.k_40 AS k40,\n  mw_pivot.co_60 AS co60,\n  mw_pivot.sr_89 AS sr89,\n  mw_pivot.sr_90 AS sr90,\n  mw_pivot.ru_103 AS ru103,\n  mw_pivot.i_131 AS i131,\n  mw_pivot.cs_134 AS cs134,\n  mw_pivot.cs_137 AS cs137,\n  mw_pivot.ce_144 AS ce144,\n  mw_pivot.u_234 AS u234,\n  mw_pivot.u_235 AS u235,\n  mw_pivot.u_238 AS u238,\n  mw_pivot.pu_238 AS pu238,\n  mw_pivot.pu_239 AS pu239,\n  mw_pivot.pu_23940 AS pu23940,\n  mw_pivot.te_132 AS te132,\n  mw_pivot.pb_212 AS pb212,\n  mw_pivot.pb_214 AS pb214,\n  mw_pivot.bi_212 AS bi212,\n  mw_pivot.bi_214 AS bi214\nFROM land.probe\nLEFT JOIN stamm.mess_stelle\n  ON (probe.mst_id = stamm.mess_stelle.id)\nINNER JOIN land.messung\n  ON probe.id = messung.probe_id\nINNER JOIN land.status_protokoll\n  ON messung.STATUS = status_protokoll.id\nLEFT JOIN stamm.status_kombi\n  ON status_protokoll.status_kombi = stamm.status_kombi.id\nLEFT JOIN stamm.status_wert\n  ON stamm.status_wert.id = stamm.status_kombi.wert_id\nLEFT JOIN stamm.status_stufe\n  ON stamm.status_stufe.id = stamm.status_kombi.stufe_id\nLEFT JOIN stamm.datenbasis\n  ON (probe.datenbasis_id = datenbasis.id)\nLEFT JOIN stamm.probenart\n  ON (probe.probenart_id = probenart.id)\nLEFT JOIN land.ortszuordnung\n  ON (\n      probe.id = ortszuordnung.probe_id\n      AND ortszuordnung.ortszuordnung_typ = 'E'\n      )\nLEFT JOIN stamm.ort\n  ON (ortszuordnung.ort_id = ort.id)\nLEFT JOIN stamm.verwaltungseinheit\n  ON (ort.gem_id = verwaltungseinheit.id)\nLEFT JOIN (\n  SELECT\n    messungs_id, meh_id, h_3, k_40, co_60, sr_89, sr_90, ru_103,\n    i_131, cs_134, cs_137, ce_144, u_234, u_235, u_238, pu_238,\n    pu_239, pu_23940, te_132, pb_212, pb_214, bi_212, bi_214\n  FROM crosstab(\n    'SELECT messwert.messungs_id, messwert.meh_id, messwert.messgroesse_id, COALESCE(messwert.messwert_nwg, '' '') || to_char(messwert.messwert, ''9.99eeee'') FROM land.messung INNER JOIN land.messwert ON messung.id = messung.id WHERE messgroesse_id IN (1, 28, 68, 164, 165, 220, 340, 369, 373, 404, 746, 747, 750, 768, 769, 850, 325, 672, 673, 684, 686) ORDER BY 1',\n    'SELECT mg_id FROM (VALUES (1), (28), (68), (164), (165), (220), (340), (369), (373), (404), (746), (747), (750), (768), (769), (850), (325), (672), (673), (684), (686)) AS t (mg_id)'\n  ) AS (\n    messungs_id integer, meh_id integer,\n    h_3 character varying(10), k_40 character varying(10),\n    co_60 character varying(10), sr_89 character varying(10),\n    sr_90 character varying(10), ru_103 character varying(10),\n    i_131 character varying(10), cs_134 character varying(10),\n    cs_137 character varying(10), ce_144 character varying(10),\n    u_234 character varying(10), u_235 character varying(10),\n    u_238 character varying(10), pu_238 character varying(10),\n    pu_239 character varying(10), pu_23940 character varying(10),\n    te_132 character varying(10), pb_212 character varying(10),\n    pb_214 character varying(10), bi_212 character varying(10),\n    bi_214 character varying(10)\n  )\n) AS mw_pivot\n  ON mw_pivot.messungs_id = messung.id\nLEFT JOIN stamm.mess_einheit\n  ON mess_einheit.id = mw_pivot.meh_id	Messungen zur Statusvergabe auf MST-Ebene
13	alle nach Status	1	SELECT messung.id,\n  probe.id AS probeId,\n  probe.hauptproben_nr AS hpNr,\n  messung.nebenproben_nr AS npNr,\n  to_char(status_protokoll.datum, 'dd.mm.YYYY hh24:MI') AS statusD,\n  stamm.status_stufe.stufe AS statusSt,\n  stamm.status_wert.wert AS statusW,\n  datenbasis.datenbasis AS dBasis,\n  stamm.mess_stelle.netzbetreiber_id AS netzId,\n  probe.mst_id AS mstId,\n  probe.umw_id AS umwId,\n  probenart.probenart AS pArt,\n  to_char(probe.probeentnahme_beginn, 'dd.mm.YYYY hh24:MI') AS peBegin,\n  to_char(probe.probeentnahme_ende, 'dd.mm.YYYY hh24:MI') AS peEnd,\n  ort.ort_id AS ortId,\n  ort.gem_id AS eGemId,\n  verwaltungseinheit.bezeichnung AS eGem\nFROM land.probe\nLEFT JOIN stamm.mess_stelle\n  ON (probe.mst_id = stamm.mess_stelle.id)\nINNER JOIN land.messung\n  ON probe.id = messung.probe_id\nINNER JOIN land.status_protokoll\n  ON messung.STATUS = status_protokoll.id\nLEFT JOIN stamm.status_kombi\n  ON status_protokoll.status_kombi = stamm.status_kombi.id\nLEFT JOIN stamm.status_wert\n  ON stamm.status_wert.id = stamm.status_kombi.wert_id\nLEFT JOIN stamm.status_stufe\n  ON stamm.status_stufe.id = stamm.status_kombi.stufe_id\nLEFT JOIN stamm.datenbasis\n  ON (probe.datenbasis_id = datenbasis.id)\nLEFT JOIN stamm.probenart\n  ON (probe.probenart_id = probenart.id)\nLEFT JOIN land.ortszuordnung\n  ON (\n      probe.id = ortszuordnung.probe_id\n      AND ortszuordnung.ortszuordnung_typ = 'E'\n      )\nLEFT JOIN stamm.ort\n  ON (ortszuordnung.ort_id = ort.id)\nLEFT JOIN stamm.verwaltungseinheit\n  ON (ort.gem_id = verwaltungseinheit.id)\nWHERE CAST(stamm.status_wert.id AS TEXT) SIMILAR TO CASE \n    WHEN :statusFilter = ''\n      THEN '%'\n    ELSE :statusFilter\n    END\n  AND (\n    CAST(stamm.status_stufe.id AS TEXT) = :statusStufeFilter\n    OR '' = :statusStufeFilter\n    )\n  AND (\n    probe.umw_id = :umwIdFilter\n    OR '' = :umwIdFilter\n    )	Messungen nach Status
15	LSt G - Messungen	1	SELECT messung.id,\n  probe.id AS probeId,\n  probe.hauptproben_nr AS hpNr,\n  messung.nebenproben_nr AS npNr,\n  stamm.status_stufe.stufe AS statusSt,\n  stamm.status_wert.wert AS statusW,\n  to_char(status_protokoll.datum, 'dd.mm.YYYY hh24:MI') AS statusD,\n  datenbasis.datenbasis AS dBasis,\n  stamm.mess_stelle.netzbetreiber_id AS netzId,\n  probe.mst_id AS mstId,\n  probe.umw_id AS umwId,\n  probenart.probenart AS pArt,\n  to_char(probe.probeentnahme_beginn, 'dd.mm.YYYY hh24:MI') AS peBegin,\n  to_char(probe.probeentnahme_ende, 'dd.mm.YYYY hh24:MI') AS peEnd,\n  ort.gem_id AS eGemId,\n  verwaltungseinheit.bezeichnung AS eGem,\n  coalesce(k40.messwert_nwg, ' ') || to_char(k40.messwert, '0.99eeee') AS k40,\n  coalesce(co60.messwert_nwg, ' ') || to_char(co60.messwert, '0.99eeee') AS co60,\n  coalesce(cs137.messwert_nwg, ' ') || to_char(cs137.messwert, '0.99eeee') AS cs137\nFROM land.probe\nLEFT JOIN stamm.mess_stelle\n  ON (probe.mst_id = stamm.mess_stelle.id)\nINNER JOIN land.messung\n  ON probe.id = messung.probe_id\nINNER JOIN land.status_protokoll\n  ON messung.STATUS = status_protokoll.id\nLEFT JOIN stamm.status_kombi\n  ON status_protokoll.status_kombi = stamm.status_kombi.id\nLEFT JOIN stamm.status_wert\n  ON stamm.status_wert.id = stamm.status_kombi.wert_id\nLEFT JOIN stamm.status_stufe\n  ON stamm.status_stufe.id = stamm.status_kombi.stufe_id\nLEFT JOIN stamm.datenbasis\n  ON (probe.datenbasis_id = datenbasis.id)\nLEFT JOIN stamm.probenart\n  ON (probe.probenart_id = probenart.id)\nLEFT JOIN land.ortszuordnung\n  ON (\n      probe.id = ortszuordnung.probe_id\n      AND ortszuordnung.ortszuordnung_typ = 'E'\n      )\nLEFT JOIN stamm.ort\n  ON (ortszuordnung.ort_id = ort.id)\nLEFT JOIN stamm.verwaltungseinheit\n  ON (ort.gem_id = verwaltungseinheit.id)\nLEFT JOIN land.messwert k40\n  ON (\n      k40.messungs_id = messung.id\n      AND k40.messgroesse_id = 28\n      )\nLEFT JOIN land.messwert co60\n  ON (\n      co60.messungs_id = messung.id\n      AND co60.messgroesse_id = 68\n      )\nLEFT JOIN land.messwert cs137\n  ON (\n      cs137.messungs_id = messung.id\n      AND cs137.messgroesse_id = 373\n      )\nWHERE messung.mmt_id = 'G1'\n  AND (\n       status_stufe.id = 3\n    OR status_stufe.id = 2 AND status_wert.id IN (1, 2, 4)\n    )\n	Gammaspektrometriemessungen zur LSt-Bearbeitung
17	LSt Sr - Messungen	1	SELECT messung.id,\r\n  probe.id AS probeId,\r\n  probe.hauptproben_nr AS hpNr,\r\n  messung.nebenproben_nr AS npNr,\r\n  stamm.status_stufe.stufe AS statusSt,\r\n  stamm.status_wert.wert AS statusW,\r\n  to_char(status_protokoll.datum, 'dd.mm.YYYY hh24:MI') AS statusD,\r\n  datenbasis.datenbasis AS dBasis,\r\n  stamm.mess_stelle.netzbetreiber_id AS netzId,\r\n  probe.mst_id AS mstId,\r\n  probe.umw_id AS umwId,\r\n  probenart.probenart AS pArt,\r\n  to_char(probe.probeentnahme_beginn, 'dd.mm.YYYY hh24:MI') AS peBegin,\r\n  to_char(probe.probeentnahme_ende, 'dd.mm.YYYY hh24:MI') AS peEnd,\r\n  ort.gem_id AS eGemId,\r\n  verwaltungseinheit.bezeichnung AS eGem,\r\n  coalesce(sr89.messwert_nwg, ' ') || to_char(sr89.messwert, '0.99eeee') AS sr89,\r\n  coalesce(sr90.messwert_nwg, ' ') || to_char(sr90.messwert, '0.99eeee') AS sr90\r\nFROM land.probe\r\nLEFT JOIN stamm.mess_stelle\r\n  ON (probe.mst_id = stamm.mess_stelle.id)\r\nINNER JOIN land.messung\r\n  ON probe.id = messung.probe_id\r\nINNER JOIN land.status_protokoll\r\n  ON messung.STATUS = status_protokoll.id\r\nLEFT JOIN stamm.status_kombi\r\n  ON status_protokoll.status_kombi = stamm.status_kombi.id\r\nLEFT JOIN stamm.status_wert\r\n  ON stamm.status_wert.id = stamm.status_kombi.wert_id\r\nLEFT JOIN stamm.status_stufe\r\n  ON stamm.status_stufe.id = stamm.status_kombi.stufe_id\r\nLEFT JOIN stamm.datenbasis\r\n  ON (probe.datenbasis_id = datenbasis.id)\r\nLEFT JOIN stamm.probenart\r\n  ON (probe.probenart_id = probenart.id)\r\nLEFT JOIN land.ortszuordnung\r\n  ON (\r\n      probe.id = ortszuordnung.probe_id\r\n      AND ortszuordnung.ortszuordnung_typ = 'E'\r\n      )\r\nLEFT JOIN stamm.ort\r\n  ON (ortszuordnung.ort_id = ort.id)\r\nLEFT JOIN stamm.verwaltungseinheit\r\n  ON (ort.gem_id = verwaltungseinheit.id)\r\nLEFT JOIN land.messwert sr89\r\n  ON (sr89.messungs_id = messung.id AND sr89.messgroesse_id = 164)\r\nLEFT JOIN land.messwert sr90\r\n  ON (sr90.messungs_id = messung.id AND sr90.messgroesse_id = 165)\r\nWHERE messung.mmt_id = 'BS'\r\n  AND (\r\n       status_stufe.id = 3\r\n    OR status_stufe.id = 2 AND status_wert.id IN (1, 2, 4)\r\n    )	Strontium-Messungen zur LSt-Bearbeitung
\.


--
-- TOC entry 5410 (class 0 OID 671972)
-- Dependencies: 243
-- Data for Name: filter; Type: TABLE DATA; Schema: stamm; Owner: postgres
--

COPY filter (id, query_id, data_index, type, label, multiselect) FROM stdin;
8	7	netzIdFilter	2	Land	f
9	7	umwIdFilter	3	Umweltbereich	t
10	9	netzIdFilter	2	Land	f
14	14	netzbetreiberId	2	Land	t
15	13	statusFilter	4	Status	t
16	10	netzbetreiberId	2	Land	f
17	11	netzbetreiberId	2	Land	f
18	12	netzbetreiberId	2	Land	f
2	1	hpNrFilter	0	HP-Nr-Filter	f
1	1	probeIdFilter	0	Probe_id-Filter	f
3	1	netzIdFilter	2	Land	f
4	1	mstIdFilter	1	Messstelle	f
5	1	umwIdFilter	3	Umweltbereich	f
11	9	umwIdFilter	3	Umweltbereich	t
19	13	statusStufeFilter	5	Statustufe (1, 2,3 oder leer)	f
20	13	umwIdFilter	3	Umweltbereich	t
\.


--
-- TOC entry 5417 (class 0 OID 0)
-- Dependencies: 242
-- Name: filter_id_seq; Type: SEQUENCE SET; Schema: stamm; Owner: postgres
--

SELECT pg_catalog.setval('filter_id_seq', 20, true);


--
-- TOC entry 5418 (class 0 OID 0)
-- Dependencies: 236
-- Name: query_id_seq; Type: SEQUENCE SET; Schema: stamm; Owner: postgres
--

SELECT pg_catalog.setval('query_id_seq', 18, true);


--
-- TOC entry 5412 (class 0 OID 672226)
-- Dependencies: 271
-- Data for Name: result; Type: TABLE DATA; Schema: stamm; Owner: postgres
--

COPY result (id, query_id, data_index, header, width, flex, index) FROM stdin;
1	1	hpNr	Proben-Nr	100	f	0
2	1	dBasis	DB	50	f	1
3	1	netzId	Land	50	f	2
4	1	mstId	MST	60	f	3
5	1	umwId	Umw-ID	55	f	4
6	1	pArt	PA	50	f	5
7	1	peBegin	Entnahme_von	120	f	6
8	1	peEnd	Entnahme_bis	120	f	7
9	1	ortId	Ort_id	100	f	8
10	1	eGemId	E-Gem Id	80	f	9
11	1	eGem	E-Gemeinde	200	f	10
12	1	probeId	Probe_Id	100	f	11
128	9	mpNr	MPR-ID	70	f	0
90	9	deskriptoren	Deskriptoren	220	f	7
84	9	netzId	Land	50	f	1
61	7	hpNr	Proben-Nr	100	f	0
64	7	dBasis	DB	50	f	1
66	7	netzId	Land	50	f	2
67	7	mstId	MST	60	f	3
70	7	umwId	Umw-ID	55	f	4
71	7	pArt	PA	50	f	5
73	7	peBegin	Entnahme_von	120	f	6
76	7	peEnd	Entnahme_bis	120	f	7
77	7	ortId	Ort_id	100	f	8
80	7	eGemId	E-Gem Id	80	f	9
81	7	eGem	E-Gemeinde	200	f	10
82	7	probeId	Probe Id	100	f	11
85	9	mstLaborId	MST/Labor	80	f	2
86	9	dBasis	DB	50	f	3
87	9	messRegime	MR	50	f	4
88	9	pArt	PA	50	f	5
89	9	umwId	Umw-ID	50	f	6
91	9	intervall	PI	50	f	8
92	9	ortId	Ort_id	100	f	9
93	9	eGemId	Gem-Id	80	f	10
94	9	eGem	Gemeinde	200	f	11
95	13	hpNr	Proben-Nr	100	f	1
96	13	npNr	NP-Nr	50	f	2
97	13	statusD	Status-Zeit	120	f	3
98	13	statusSt	Stufe	40	f	4
99	13	statusW	Status	100	f	5
100	13	dBasis	DB	50	f	6
101	13	netzId	Land	50	f	7
102	13	mstId	MST	60	f	8
103	13	umwId	Umw-ID	55	f	9
104	13	pArt	PA	50	f	10
105	13	peBegin	Entnahme_von	120	f	11
106	13	peEnd	Entnahme_bis	120	f	12
107	13	ortId	Ort_id	100	f	13
108	13	eGemId	E-Gem Id	80	f	14
109	13	eGem	E-Gemeinde	200	f	15
110	13	probeId	Probe_Id	100	f	0
111	15	hpNr	Proben-Nr	100	f	1
112	15	npNr	NP-Nr	50	f	2
173	16	ce144	Ce-144	100	f	24
116	15	dBasis	DB	50	f	6
117	15	netzId	Land	50	f	7
118	15	mstId	MST	60	f	8
119	15	umwId	Umw-ID	55	f	9
120	15	pArt	PA	50	f	10
121	15	peBegin	Entnahme_von	120	f	11
122	15	peEnd	Entnahme_bis	120	f	12
123	15	ortId	Ort_id	100	f	13
124	15	eGemId	E-Gem Id	80	f	14
125	15	eGem	E-Gemeinde	200	f	15
126	15	probeId	Probe_Id	100	f	0
230	18	npNr	NP-Nr	50	f	2
231	18	statusSt	Stufe	40	f	3
129	15	k40	K-40	100	f	16
132	15	co60	Co-60	100	f	17
130	15	cs137	Cs-137	100	f	18
232	18	statusW	Status	100	f	4
233	18	statusD	Status-Zeit	120	f	5
234	18	dBasis	DB	50	f	6
114	15	statusSt	Stufe	40	f	3
115	15	statusW	Status	100	f	4
113	15	statusD	Status-Zeit	120	f	5
133	17	probeId	Probe_Id	100	f	0
134	17	hpNr	Proben-Nr	100	f	1
135	17	npNr	NP-Nr	50	f	2
136	17	statusSt	Stufe	40	f	3
137	17	statusW	Status	100	f	4
138	17	statusD	Status-Zeit	120	f	5
139	17	dBasis	DB	50	f	6
140	17	netzId	Land	50	f	7
141	17	mstId	MST	60	f	8
142	17	umwId	Umw-ID	55	f	9
143	17	pArt	PA	50	f	10
144	17	peBegin	Entnahme_von	120	f	11
145	17	peEnd	Entnahme_bis	120	f	12
146	17	ortId	Ort_id	100	f	13
147	17	eGemId	E-Gem Id	80	f	14
148	17	eGem	E-Gemeinde	200	f	15
235	18	netzId	Land	50	f	7
236	18	mstId	MST	60	f	8
237	18	umwId	Umw-ID	55	f	9
149	17	sr89	Sr-89	100	f	16
150	17	sr90	Sr-90	100	f	17
152	16	probeId	Probe_Id	100	f	0
153	16	hpNr	Proben-Nr	100	f	1
154	16	npNr	NP-Nr	50	f	2
155	16	statusSt	Stufe	40	f	3
156	16	statusW	Status	100	f	4
157	16	statusD	Status-Zeit	120	f	5
158	16	dBasis	DB	50	f	6
159	16	netzId	Land	50	f	7
160	16	mstId	MST	60	f	8
161	16	umwId	Umw-ID	55	f	9
162	16	pArt	PA	50	f	10
163	16	peBegin	Entnahme_von	120	f	11
164	16	peEnd	Entnahme_bis	120	f	12
238	18	pArt	PA	50	f	10
239	18	peBegin	Entnahme_von	120	f	11
240	18	peEnd	Entnahme_bis	120	f	12
241	18	eGemId	E-Gem Id	80	f	13
242	18	eGem	E-Gemeinde	200	f	14
243	18	h3	H-3	100	f	15
244	18	k40	K-40	100	f	16
245	18	co60	Co-60	100	f	17
246	18	sr89	Sr-89	100	f	18
247	18	sr90	Sr-90	100	f	19
248	18	ru103	Ru-103	100	f	20
168	16	h3	H-3	100	f	15
169	16	k40	K-40	100	f	16
170	16	co60	Co-60	100	f	17
172	16	sr89	Sr-89	100	f	18
174	16	sr90	Sr-90	100	f	19
175	16	ru103	Ru-103	100	f	20
176	16	i131	I-131	100	f	21
166	16	eGemId	E-Gem Id	80	f	13
167	16	eGem	E-Gemeinde	200	f	14
177	16	cs134	Cs-134	100	f	22
178	16	cs137	Cs-137	100	f	23
189	16	bi214	Bi-214	100	f	35
188	16	bi212	Bi-212	100	f	34
187	16	pd214	Pb-214	100	f	33
186	16	pb212	Pb-212	100	f	32
185	16	te132	Te-132	100	f	31
184	16	pu23940	Pu-239/240	100	f	30
183	16	pu239	Pu-239	100	f	29
182	16	pu238	Pu-238	100	f	28
181	16	u238	U-238	100	f	27
180	16	u235	U-235	100	f	26
179	16	u234	U-234	100	f	25
228	18	probeId	Probe_Id	100	f	0
229	18	hpNr	Proben-Nr	100	f	1
249	18	i131	I-131	100	f	21
250	18	cs134	Cs-134	100	f	22
251	18	cs137	Cs-137	100	f	23
252	18	ce144	Ce-144	100	f	24
253	18	u234	U-234	100	f	25
254	18	u235	U-235	100	f	26
255	18	u238	U-238	100	f	27
256	18	pu238	Pu-238	100	f	28
257	18	pu239	Pu-239	100	f	29
258	18	pu23940	Pu-239/240	100	f	30
259	18	te132	Te-132	100	f	31
260	18	pb212	Pb-212	100	f	32
261	18	pd214	Pb-214	100	f	33
262	18	bi212	Bi-212	100	f	34
263	18	bi214	Bi-214	100	f	35
267	10	netzbetreiberId	Netzbetreiber	100	f	0
268	10	ortId	Ort-ID	50	f	1
269	10	kurztext	kurztext	100	f	3
270	10	langtext	Langtext	150	f	4
271	10	staat	Staat	50	f	6
272	10	verwaltungseinheit	Verwaltungseinheit	100	f	5
273	10	unscharf	Unscharf	100	f	15
274	10	nutsCode	NUTS-Code	50	f	7
275	10	koordinatenArt	Koordinatenart	100	f	16
276	10	koordXExtern	X-Koordinate	100	f	17
277	10	koordYExtern	Y-Koordinate	100	f	18
278	10	hoeheLand	Höhe	100	f	21
279	10	letzteAenderung	letzte Änderung	100	f	22
280	10	ortTyp	Ortstyp	50	f	2
281	10	berichtstext	Berichtstext	100	f	14
282	10	zone	Zone	100	f	11
283	10	sektor	Sektor	100	f	12
284	10	zustaendigkeit	Zuständigkeit	100	f	13
285	10	mpArt	mpArt	100	f	10
286	10	aktiv	Aktiv	50	f	23
287	10	anlageId	Anlage	100	f	9
288	10	ozId	OZ-ID	50	f	8
289	10	hoeheUeberNn	Höhe über NN	100	f	24
290	10	longitude	Geographische Länge	100	f	19
291	10	latitude	Geographische Breite	100	f	20
\.


--
-- TOC entry 5419 (class 0 OID 0)
-- Dependencies: 270
-- Name: result_id_seq; Type: SEQUENCE SET; Schema: stamm; Owner: postgres
--

SELECT pg_catalog.setval('result_id_seq', 263, true);


-- Completed on 2017-10-18 08:57:25 CEST

--
-- PostgreSQL database dump complete
--

