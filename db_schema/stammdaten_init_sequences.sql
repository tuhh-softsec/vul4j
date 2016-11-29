SELECT pg_catalog.setval('auth_id_seq', (SELECT max(id) FROM stammdaten.auth), true);

SELECT pg_catalog.setval('auth_lst_umw_id_seq', (SELECT max(id) FROM auth_lst_umw) , true);

SELECT pg_catalog.setval('datenbasis_id_seq', (SELECT max(id) FROM datenbasis), true);

SELECT pg_catalog.setval('datensatz_erzeuger_id_seq', (SELECT max(id) FROM datensatz_erzeuger), true);

SELECT pg_catalog.setval('deskriptor_umwelt_id_seq', (SELECT max(id) FROM deskriptor_umwelt), true);

SELECT pg_catalog.setval('deskriptoren_id_seq', (SELECT max(id) FROM deskriptoren), true);

SELECT pg_catalog.setval('favorite_id_seq', 1, false);

SELECT pg_catalog.setval('filter_id_seq', (SELECT max(id) FROM filter), true);

SELECT pg_catalog.setval('filter_value_id_seq', 1, false);

SELECT pg_catalog.setval('koordinaten_art_id_seq', (SELECT max(id) FROM koordinaten_art), true);

SELECT pg_catalog.setval('kta_id_seq', (SELECT max(id) FROM kta), true);

SELECT pg_catalog.setval('lada_user_id_seq', 1, false);

SELECT pg_catalog.setval('mess_einheit_id_seq', (SELECT max(id) FROM mess_einheit), true);

SELECT pg_catalog.setval('messgroesse_id_seq', (SELECT max(id) FROM messgroesse), true);

SELECT pg_catalog.setval('messgroessen_gruppe_id_seq', (SELECT max(id) FROM messgreossen_gruppe), true);

SELECT pg_catalog.setval('messprogramm_kategorie_id_seq', (SELECT max(id) FROM messprogramm_kategorie), true);

SELECT pg_catalog.setval('ort_id_seq', (SELECT max(id) FROM ort), true);

SELECT pg_catalog.setval('pflicht_messgroesse_id_seq', (SELECT max(id) FROM pflicht_messgroesse), true);

SELECT pg_catalog.setval('probenart_id_seq', (SELECT max(id) FROM probenart), true);

SELECT pg_catalog.setval('probenehmer_id_seq', (SELECT max(id) FROM probenehmer), true);

SELECT pg_catalog.setval('query_id_seq', SELECT max(id) FROM query), true);

SELECT pg_catalog.setval('result_id_seq', (SELECT max(id) FROM result), true);

SELECT pg_catalog.setval('staat_id_seq', (SELECT max(id) FROM staat), true);
