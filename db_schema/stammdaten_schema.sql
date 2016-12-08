\set ON_ERROR_STOP on

BEGIN;

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;


CREATE SCHEMA stammdaten;

SET search_path = stammdaten, pg_catalog;

CREATE FUNCTION set_ort_id() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    DECLARE value text;
    BEGIN
        value = '#'::text || lpad((NEW.id::character varying)::text, 9, '0'::text);
        IF NEW.ort_id IS NULL THEN
            NEW.ort_id = value;
        END IF;
        IF NEW.langtext IS NULL THEN
            NEW.langtext = value;
        END IF;
        IF NEW.kurztext IS NULL THEN
            NEW.kurztext = value;
        END IF;
        RETURN NEW;
    END;
$$;

CREATE FUNCTION update_letzte_aenderung() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    BEGIN
        NEW.letzte_aenderung = now();
        RETURN NEW;
    END;
$$;

CREATE FUNCTION get_media_from_media_desk(media_desk character varying) RETURNS character varying
    LANGUAGE plpgsql
    AS $$
declare
  result character varying(100);
  d00 smallint;
  d01 smallint;
  d02 smallint;
  d03 smallint;
begin
  if media_desk like 'D: %' then
    d00 := substring(media_desk,4,2);
    d01 := substring(media_desk,7,2);
    d02 := substring(media_desk,10,2);
    d03 := substring(media_desk,13,2);
    if d00 = '00' then
      result := null;
    else
      if d01 = '00' then
        select s00.beschreibung into result FROM stammdaten.deskriptoren s00
        where s00.ebene = 0 and s00.sn = d00::smallint;
      else
        if d02 = '00' or d00 <> '01' then
          select s01.beschreibung into result FROM stammdaten.deskriptoren s01
          where s01.ebene = 1 and s01.sn = d01::smallint
            and s01.vorgaenger =
              (select s00.id FROM stammdaten.deskriptoren s00
               where s00.ebene = 0 and s00.sn = d00::smallint);
        else
          if d03 = '00' then
            select s02.beschreibung into result FROM stammdaten.deskriptoren s02
            where s02.ebene = 2 and s02.sn = d02::smallint
              and s02.vorgaenger =
                (select s01.id FROM stammdaten.deskriptoren s01
                 where s01.ebene = 1 and s01.sn = d01::smallint
                   and s01.vorgaenger =
                     (select s00.id FROM stammdaten.deskriptoren s00
                      where s00.ebene = 0 and s00.sn = d00::smallint));
          else
            select s03.beschreibung into result FROM stammdaten.deskriptoren s03
            where s03.ebene = 3 and s03.sn = d03::smallint
              and s03.vorgaenger =
              (select s02.id FROM stammdaten.deskriptoren s02
              where s02.ebene = 2 and s02.sn = d02::smallint
                and s02.vorgaenger =
                  (select s01.id FROM stammdaten.deskriptoren s01
                  where s01.ebene = 1 and s01.sn = d01::smallint
                    and s01.vorgaenger =
                      (select s00.id FROM stammdaten.deskriptoren s00
                      where s00.ebene = 0 and s00.sn = d00::smallint)));
          end if;
        end if;
      end if;
    end if;
  else
    result := null;
  end if;
  return (result);
end;
$$;


CREATE TABLE koordinaten_art (
    id serial PRIMARY KEY,
    koordinatenart character varying(50),
    idf_geo_key character varying(1)
);


CREATE TABLE mess_einheit (
    id serial PRIMARY KEY,
    beschreibung character varying(50),
    einheit character varying(12),
    eudf_messeinheit_id character varying(8),
    umrechnungs_faktor_eudf bigint
);


CREATE TABLE umwelt (
    id character varying(3) PRIMARY KEY,
    beschreibung character varying(300),
    umwelt_bereich character varying(80) NOT NULL,
    meh_id integer REFERENCES mess_einheit,
    UNIQUE (umwelt_bereich)
);


CREATE TABLE betriebsart (
    id smallint PRIMARY KEY,
    name character varying(30) NOT NULL
);
INSERT INTO betriebsart VALUES(1, 'Normal-/Routinebetrieb');
INSERT INTO betriebsart VALUES(2, 'Störfall-/Intensivbetrieb');


CREATE TABLE staat (
    id serial PRIMARY KEY,
    staat character varying(50) NOT NULL,
    hkl_id smallint NOT NULL,
    staat_iso character varying(2) NOT NULL,
    staat_kurz character varying(5),
    eu character(1) DEFAULT NULL::bpchar,
    koord_x_extern character varying(22),
    koord_y_extern character varying(22),
    kda_id integer REFERENCES koordinaten_art
);

CREATE TABLE verwaltungseinheit (
    id character varying(8) NOT NULL PRIMARY KEY,
    bezeichnung character varying(80) NOT NULL,
    regbezirk character varying(8),
    kreis character varying(8),
    bundesland character varying(8) NOT NULL,
    is_gemeinde boolean DEFAULT false NOT NULL,
    is_landkreis boolean DEFAULT false NOT NULL,
    is_regbezirk boolean DEFAULT false NOT NULL,
    is_bundesland boolean DEFAULT false NOT NULL,
    plz character varying(6),
    nuts character varying(10),
    mittelpunkt public.geometry(Point)
);

CREATE TABLE verwaltungsgrenze (
    id serial PRIMARY KEY,
    gem_id character varying(8) NOT NULL REFERENCES verwaltungseinheit,
    shape public.geometry(MultiPolygon, 4326)
);
CREATE INDEX verwaltungsgrenze_sp_idx ON verwaltungsgrenze USING gist (shape);

CREATE TABLE netz_betreiber (
    id character varying(2) PRIMARY KEY,
    netzbetreiber character varying(50),
    idf_netzbetreiber character varying(1),
    is_bmn boolean DEFAULT false,
    mailverteiler character varying(512),
    aktiv boolean DEFAULT false
);


CREATE TABLE mess_stelle (
    id character varying(5) PRIMARY KEY,
    netzbetreiber_id character varying(2) NOT NULL REFERENCES netz_betreiber,
    beschreibung character varying(300),
    mess_stelle character varying(60),
    mst_typ character varying(1),
    amtskennung character varying(6)
);


CREATE TABLE auth_funktion (
    id smallint PRIMARY KEY,
    funktion character varying(40) UNIQUE NOT NULL
);
INSERT INTO auth_funktion VALUES (0, 'Erfasser');
INSERT INTO auth_funktion VALUES (1, 'Status-Erfasser');
INSERT INTO auth_funktion VALUES (2, 'Status-Land');
INSERT INTO auth_funktion VALUES (3, 'Status-Leitstelle');
INSERT INTO auth_funktion VALUES (4, 'Stammdatenpflege-Land');


CREATE TABLE auth (
    id serial PRIMARY KEY,
    ldap_group character varying(40) NOT NULL,
    netzbetreiber_id character varying(2) REFERENCES netz_betreiber,
    mst_id character varying(5) REFERENCES mess_stelle,
    labor_mst_id character varying(5) REFERENCES mess_stelle,
    funktion_id smallint REFERENCES auth_funktion
);


CREATE TABLE auth_lst_umw (
    id serial PRIMARY KEY,
    mst_id character varying(5) REFERENCES mess_stelle,
    umw_id character varying(3) REFERENCES umwelt
);


CREATE TABLE datenbasis (
    id serial PRIMARY KEY,
    beschreibung character varying(30),
    datenbasis character varying(6)
);


CREATE TABLE datensatz_erzeuger (
    id serial PRIMARY KEY,
    netzbetreiber_id character varying(2) NOT NULL REFERENCES netz_betreiber,
    datensatz_erzeuger_id character varying(2) NOT NULL,
    mst_id character varying(5) NOT NULL REFERENCES mess_stelle,
    bezeichnung character varying(120) NOT NULL,
    letzte_aenderung timestamp without time zone,
    UNIQUE(datensatz_erzeuger_id, netzbetreiber_id)
);
CREATE TRIGGER letzte_aenderung_datensatz_erzeuger BEFORE UPDATE ON datensatz_erzeuger FOR EACH ROW EXECUTE PROCEDURE update_letzte_aenderung();


CREATE TABLE deskriptor_umwelt (
    id serial PRIMARY KEY,
    s00 integer NOT NULL,
    s01 integer NOT NULL,
    s02 integer,
    s03 integer,
    s04 integer,
    s05 integer,
    s06 integer,
    s07 integer,
    s08 integer,
    s09 integer,
    s10 integer,
    s11 integer,
    s12 integer,
    umw_id character varying(3) NOT NULL REFERENCES umwelt
);


CREATE TABLE deskriptoren (
    id serial PRIMARY KEY,
    vorgaenger integer REFERENCES deskriptoren,
    ebene smallint,
    s_xx integer,
    sn smallint,
    beschreibung character varying(100),
    bedeutung character varying(300)
);


CREATE TABLE lada_user (
    id serial PRIMARY KEY,
    name character varying(80) NOT NULL,
    UNIQUE (name)
);


CREATE TABLE query_type (
    id serial PRIMARY KEY,
    type character varying(30) NOT NULL
);
INSERT INTO query_type VALUES(0, 'probe');
INSERT INTO query_type VALUES(1, 'messung');
INSERT INTO query_type VALUES(2, 'messprogramm');
INSERT INTO query_type VALUES(3, 'ort');
INSERT INTO query_type VALUES(4, 'probenehmer');
INSERT INTO query_type VALUES(5, 'datensatzerzeuger');
INSERT INTO query_type VALUES(6, 'messprogrammkategorie');


CREATE TABLE query (
    id serial PRIMARY KEY,
    name character varying(80) NOT NULL,
    type integer NOT NULL REFERENCES query_type,
    sql character varying(2500) NOT NULL,
    description character varying(100),
    UNIQUE (name, type)
);


CREATE TABLE favorite (
    id serial PRIMARY KEY,
    user_id integer NOT NULL REFERENCES lada_user,
    query_id integer NOT NULL REFERENCES query ON DELETE CASCADE
);


CREATE TABLE filter_type (
    id serial PRIMARY KEY,
    type character varying(10) NOT NULL
);
INSERT INTO filter_type VALUES(0, 'text');
INSERT INTO filter_type VALUES(1, 'listmst');
INSERT INTO filter_type VALUES(2, 'listnetz');
INSERT INTO filter_type VALUES(3, 'listumw');
INSERT INTO filter_type VALUES(4, 'liststatus');
INSERT INTO filter_type VALUES(5, 'number');


CREATE TABLE filter (
    id serial PRIMARY KEY,
    query_id integer NOT NULL REFERENCES query ON DELETE CASCADE,
    data_index character varying(50) NOT NULL,
    type integer NOT NULL REFERENCES filter_type,
    label character varying(50) NOT NULL,
    multiselect boolean
);


CREATE TABLE filter_value (
    id serial PRIMARY KEY,
    user_id integer NOT NULL REFERENCES lada_user,
    filter_id integer NOT NULL REFERENCES filter ON DELETE CASCADE,
    value text
);


CREATE TABLE mess_methode (
    id character varying(2) PRIMARY KEY,
    beschreibung character varying(300),
    messmethode character varying(50)
);


CREATE TABLE messgroesse (
    id serial PRIMARY KEY,
    beschreibung character varying(300),
    messgroesse character varying(50) NOT NULL,
    default_farbe character varying(9),
    idf_nuklid_key character varying(6),
    ist_leitnuklid boolean DEFAULT false,
    eudf_nuklid_id bigint,
    kennung_bvl character varying(7)
);


CREATE TABLE messgroessen_gruppe (
    id serial PRIMARY KEY,
    bezeichnung character varying(80),
    ist_leitnuklidgruppe character(1) DEFAULT NULL::bpchar
);


CREATE TABLE messprogramm_kategorie (
    id serial PRIMARY KEY,
    netzbetreiber_id character varying(2) NOT NULL REFERENCES netz_betreiber,
    code character varying(3) NOT NULL,
    bezeichnung character varying(120) NOT NULL,
    letzte_aenderung timestamp without time zone,
    UNIQUE(code, netzbetreiber_id)
);
CREATE TRIGGER letzte_aenderung_messprogramm_kategorie BEFORE UPDATE ON messprogramm_kategorie FOR EACH ROW EXECUTE PROCEDURE update_letzte_aenderung();


CREATE TABLE mg_grp (
    messgroessengruppe_id integer NOT NULL REFERENCES messgroessen_gruppe,
    messgroesse_id integer NOT NULL REFERENCES messgroesse
);
ALTER TABLE ONLY mg_grp
    ADD CONSTRAINT mg_grp_pkey PRIMARY KEY (messgroessengruppe_id, messgroesse_id);


CREATE TABLE mmt_messgroesse_grp (
    messgroessengruppe_id integer NOT NULL REFERENCES messgroessen_gruppe,
    mmt_id character varying(2) NOT NULL REFERENCES mess_methode
);
ALTER TABLE ONLY mmt_messgroesse_grp
    ADD CONSTRAINT mmt_messgroesse_grp_pkey PRIMARY KEY (messgroessengruppe_id, mmt_id);




CREATE VIEW mmt_messgroesse AS
 SELECT mmt_messgroesse_grp.mmt_id,
    mg_grp.messgroesse_id
   FROM mmt_messgroesse_grp,
    mg_grp
  WHERE (mg_grp.messgroessengruppe_id = mmt_messgroesse_grp.messgroessengruppe_id);


CREATE TABLE ort_typ (
    id smallint PRIMARY KEY,
    ort_typ character varying(60)
);

CREATE TABLE kta (
  id serial NOT NULL,
  code character varying(7),
  bezeichnung character varying(80),
  CONSTRAINT kta_pkey PRIMARY KEY (id)
);
COMMENT ON TABLE kta
  IS 'kernteschnische Anlagen';

CREATE TABLE ortszusatz (
    ozs_id character varying(7) PRIMARY KEY,
    ortszusatz character varying(80) NOT NULL
);

CREATE TABLE ort (
    id serial PRIMARY KEY,
    netzbetreiber_id character varying(2) NOT NULL REFERENCES netz_betreiber,
    ort_id character varying(10) NOT NULL,
    langtext character varying(100) NOT NULL,
    staat_id smallint REFERENCES staat,
    gem_id character varying(8) REFERENCES verwaltungseinheit,
    unscharf character(1) DEFAULT NULL::bpchar,
    nuts_code character varying(10),
    kda_id integer NOT NULL REFERENCES koordinaten_art,
    koord_x_extern character varying(22) NOT NULL,
    koord_y_extern character varying(22) NOT NULL,
    hoehe_land real,
    letzte_aenderung timestamp without time zone DEFAULT now(),
    geom public.geometry(Point,4326) NOT NULL,
    shape public.geometry(MultiPolygon,4326),
    ort_typ smallint REFERENCES ort_typ,
    kurztext character varying(15) NOT NULL,
    berichtstext character varying(70),
    zone character varying(1),
    sektor character varying(2),
    zustaendigkeit character varying(10),
    mp_art character varying(10),
    aktiv character(1),
    anlage_id integer,
    oz_id character varying(7) REFERENCES ortszusatz(ozs_id),
    hoehe_ueber_nn real,
    UNIQUE(ort_id, netzbetreiber_id)
);

CREATE TRIGGER letzte_aenderung_ort BEFORE UPDATE ON ort FOR EACH ROW EXECUTE PROCEDURE update_letzte_aenderung();
CREATE TRIGGER set_ort_id_ort BEFORE INSERT ON ort FOR EACH ROW EXECUTE PROCEDURE set_ort_id();

ALTER TABLE ONLY ort
    ADD CONSTRAINT ort_kta_fkey FOREIGN KEY (anlage_id) REFERENCES kta(id);


CREATE TABLE ortszuordnung_typ (
    id character(1) PRIMARY KEY,
    ortstyp character varying(60)
);


CREATE TABLE pflicht_messgroesse (
    id serial PRIMARY KEY,
    messgroesse_id integer,
    mmt_id character varying(2) REFERENCES mess_methode,
    umw_id character varying(3) REFERENCES umwelt,
    datenbasis_id smallint NOT NULL REFERENCES datenbasis
);


CREATE TABLE proben_zusatz (
    id character varying(3) PRIMARY KEY,
    meh_id integer REFERENCES mess_einheit,
    beschreibung character varying(50) NOT NULL,
    zusatzwert character varying(7) NOT NULL,
    eudf_keyword character varying(40),
    UNIQUE (eudf_keyword)
);


CREATE TABLE probenart (
    id serial PRIMARY KEY,
    beschreibung character varying(30),
    probenart character varying(5) NOT NULL,
    probenart_eudf_id character varying(1) NOT NULL
);


CREATE TABLE probenehmer (
    id serial PRIMARY KEY,
    netzbetreiber_id character varying(2) NOT NULL REFERENCES netz_betreiber,
    prn_id character varying(9) NOT NULL,
    bearbeiter character varying(25),
    bemerkung character varying(60),
    betrieb character varying(80),
    bezeichnung character varying(80) NOT NULL,
    kurz_bezeichnung character varying(10) NOT NULL,
    ort character varying(20),
    plz character varying(5),
    strasse character varying(30),
    telefon character varying(20),
    tp character varying(3),
    typ character(1),
    letzte_aenderung timestamp without time zone,
    UNIQUE(prn_id, netzbetreiber_id)
);
CREATE TRIGGER letzte_aenderung_probenehmer BEFORE UPDATE ON probenehmer FOR EACH ROW EXECUTE PROCEDURE update_letzte_aenderung();


CREATE TABLE result (
    id serial PRIMARY KEY,
    query_id integer NOT NULL REFERENCES query ON DELETE CASCADE,
    data_index character varying(50) NOT NULL,
    header character varying(50) NOT NULL,
    width integer,
    flex boolean,
    index integer NOT NULL,
    UNIQUE (query_id, index),
    UNIQUE (query_id, data_index)
);


-- Status workflow
CREATE TABLE status_stufe (
    id integer PRIMARY KEY,
    stufe character varying(50) UNIQUE NOT NULL
);
INSERT INTO status_stufe VALUES (1, 'MST');
INSERT INTO status_stufe VALUES (2, 'LAND');
INSERT INTO status_stufe VALUES (3, 'LST');


CREATE TABLE status_wert (
    id integer PRIMARY KEY,
    wert character varying(50) UNIQUE NOT NULL
);
INSERT INTO status_wert VALUES (0, 'nicht vergeben');
INSERT INTO status_wert VALUES (1, 'plausibel');
INSERT INTO status_wert VALUES (2, 'nicht repräsentativ');
INSERT INTO status_wert VALUES (3, 'nicht plausibel');
INSERT INTO status_wert VALUES (4, 'Rückfrage');
INSERT INTO status_wert VALUES (7, 'nicht lieferbar');
INSERT INTO status_wert VALUES (8, 'zurückgesetzt');


CREATE TABLE status_kombi (
    id integer PRIMARY KEY,
    stufe_id integer REFERENCES status_stufe NOT NULL,
    wert_id integer REFERENCES status_wert NOT NULL,
    UNIQUE(stufe_id, wert_id)
);
INSERT INTO status_kombi VALUES (1, 1, 0);
INSERT INTO status_kombi VALUES (2, 1, 1);
INSERT INTO status_kombi VALUES (3, 1, 2);
INSERT INTO status_kombi VALUES (4, 1, 3);
INSERT INTO status_kombi VALUES (5, 1, 7);
INSERT INTO status_kombi VALUES (6, 2, 1);
INSERT INTO status_kombi VALUES (7, 2, 2);
INSERT INTO status_kombi VALUES (8, 2, 3);
INSERT INTO status_kombi VALUES (9, 2, 4);
INSERT INTO status_kombi VALUES (10, 3, 1);
INSERT INTO status_kombi VALUES (11, 3, 2);
INSERT INTO status_kombi VALUES (12, 3, 3);
INSERT INTO status_kombi VALUES (13, 3, 4);
INSERT INTO status_kombi VALUES (14, 1, 8);
INSERT INTO status_kombi VALUES (15, 2, 8);
INSERT INTO status_kombi VALUES (16, 3, 8);


CREATE TABLE status_reihenfolge (
    id serial PRIMARY KEY,
    von_id integer REFERENCES status_kombi NOT NULL,
    zu_id integer REFERENCES status_kombi NOT NULL,
    UNIQUE(von_id, zu_id)
);

CREATE FUNCTION populate_status_reihenfolge() RETURNS void AS $$
DECLARE kombi_from RECORD;
DECLARE s_from integer;
DECLARE w_from integer;
DECLARE kombi_to RECORD;
DECLARE s_to integer;
DECLARE w_to integer;

BEGIN
FOR kombi_from IN SELECT * FROM status_kombi LOOP
    s_from := kombi_from.stufe_id;
    w_from := kombi_from.wert_id;

    FOR kombi_to IN SELECT * FROM status_kombi LOOP
        s_to := kombi_to.stufe_id;
        w_to := kombi_to.wert_id;

        IF s_from = s_to AND w_to <> 0 THEN
           -- At the same 'stufe', all permutations occur,
           -- but 'nicht vergeben' is only allowed for von_id
           INSERT INTO status_reihenfolge (von_id, zu_id)
                  VALUES (kombi_from.id, kombi_to.id);

        ELSEIF s_to = s_from + 1
               AND w_from <> 0 AND w_from <> 4
               AND w_from <> 8 AND w_to <> 8 THEN
           -- Going to the next 'stufe' all available status_kombi are allowed
           -- in case current wert is not 'nicht vergeben', 'Rückfrage' or
           -- 'zurückgesetzt' and we are not trying to set 'zurückgesetzt'
           INSERT INTO status_reihenfolge (von_id, zu_id)
                  VALUES (kombi_from.id, kombi_to.id);

        ELSEIF w_from = 4 AND s_to = 1 AND w_to >= 1 AND w_to <= 3 THEN
           -- After 'Rückfrage' follows 'MST' with
           -- 'plausibel', 'nicht plausibel' or 'nicht repräsentativ'
           INSERT INTO status_reihenfolge (von_id, zu_id)
                  VALUES (kombi_from.id, kombi_to.id);

        ELSEIF w_to = 8 AND s_from = s_to THEN
           -- 'zurückgesetzt' can only be set on the same 'stufe'
           INSERT INTO status_reihenfolge (von_id, zu_id)
                  VALUES (kombi_from.id, kombi_to.id);

        ELSEIF w_from = 8 AND s_to = s_from - 1 THEN
           -- after 'zurückgesetzt' always follows the next lower 'stufe'
           INSERT INTO status_reihenfolge (von_id, zu_id)
                  VALUES (kombi_from.id, kombi_to.id);

        END IF;
    END LOOP;
END LOOP;
END;
$$ LANGUAGE plpgsql;

SELECT populate_status_reihenfolge();
DROP FUNCTION populate_status_reihenfolge();
ALTER TABLE status_reihenfolge ALTER COLUMN id DROP DEFAULT;
DROP SEQUENCE status_reihenfolge_id_seq;


CREATE VIEW status_erreichbar AS (
    SELECT r.id,
           zu.wert_id,
           zu.stufe_id,
           von.wert_id AS cur_wert,
           von.stufe_id AS cur_stufe
    FROM stammdaten.status_reihenfolge r
        JOIN stammdaten.status_kombi von
            ON von.id = r.von_id
        JOIN stammdaten.status_kombi zu
            ON zu.id = r.zu_id
);
-- Status workflow

-- Mappings for import

CREATE TABLE messprogramm_transfer (
    id serial PRIMARY KEY,
    messprogramm_s character varying(1) NOT NULL,
    messprogramm_c character varying(100) NOT NULL,
    ba_id integer NOT NULL REFERENCES betriebsart,
    UNIQUE (messprogramm_s)
);

-- Mappings for import

CREATE INDEX fts_stauts_kooin10001 ON staat USING btree (kda_id);


COMMIT;
