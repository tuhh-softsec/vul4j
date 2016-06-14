SET search_path TO stammdaten;

-- minimal master data to make interface tests runnable
DELETE FROM auth;
DELETE FROM ort;
DELETE FROM ort_typ;
DELETE FROM pflicht_messgroesse;
DELETE FROM datenbasis;
DELETE FROM mess_einheit;
DELETE FROM messgroesse;
DELETE FROM mess_methode;
DELETE FROM datensatz_erzeuger;
DELETE FROM mess_stelle;
DELETE FROM probenehmer;
DELETE FROM messprogramm_kategorie;
DELETE FROM netz_betreiber;
DELETE FROM probenart;
DELETE FROM proben_zusatz;
DELETE FROM koordinaten_art;
DELETE FROM staat;
DELETE FROM umwelt;
DELETE FROM verwaltungseinheit;

-- seed
-- minimal master data to make interface tests runnable
INSERT INTO ort_typ (id) VALUES (1);
INSERT INTO datenbasis (id) VALUES (9);
INSERT INTO mess_einheit (id) VALUES (207);
INSERT INTO messgroesse (id, messgroesse) VALUES (56, 'Mangan');
INSERT INTO mess_methode (id) VALUES ('GI');
INSERT INTO netz_betreiber (id) VALUES ('06');
INSERT INTO mess_stelle (id, netzbetreiber_id) VALUES ('06010', '06');
INSERT INTO pflicht_messgroesse (id, datenbasis_id) VALUES (33, 9);
INSERT INTO probenart (id, probenart, probenart_eudf_id) VALUES (1, 'E', 'A');
INSERT INTO proben_zusatz (id, beschreibung, zusatzwert)
       VALUES ('A74', 'Volumenstrom', 'VOLSTR');
INSERT INTO koordinaten_art (id) VALUES (5);
INSERT INTO staat (id, staat, hkl_id, staat_iso)
       VALUES (0, 'Deutschland', 0, 'DE');
INSERT INTO umwelt (id, umwelt_bereich) VALUES ('L6', 'Spurenmessung Luft');
INSERT INTO verwaltungseinheit (
            id, bundesland, bezeichnung,
            is_bundesland, is_gemeinde, is_landkreis, is_regbezirk)
       VALUES ('11000000', '11000000', 'Berlin', 1, 1, 1, 0);

-- authorization data needed for tests
INSERT INTO auth (ldap_group, netzbetreiber_id, mst_id, funktion_id)
       VALUES ('mst_06_status', '06', '06010', 1);
INSERT INTO auth (ldap_group, netzbetreiber_id, funktion_id)
       VALUES ('land_06_stamm', '06', 4);
