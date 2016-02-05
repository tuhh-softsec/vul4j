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


CREATE SEQUENCE auth_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE auth (
    id integer PRIMARY KEY DEFAULT nextval('auth_id_seq'::regclass),
    ldap_group character varying(40) NOT NULL,
    netzbetreiber_id character varying(2),
    mst_id character varying(5),
    labor_mst_id character varying(5),
    funktion_id smallint
);

ALTER SEQUENCE auth_id_seq OWNED BY auth.id;


CREATE TABLE auth_funktion (
    id smallint PRIMARY KEY,
    funktion character varying(40)
);


CREATE SEQUENCE auth_lst_umw_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE auth_lst_umw (
    id integer PRIMARY KEY DEFAULT nextval('auth_lst_umw_id_seq'::regclass),
    lst_id character varying(5),
    umw_id character varying(3)
);

ALTER SEQUENCE auth_lst_umw_id_seq OWNED BY auth_lst_umw.id;


CREATE SEQUENCE datenbasis_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE datenbasis (
    id integer PRIMARY KEY DEFAULT nextval('datenbasis_id_seq'::regclass),
    beschreibung character varying(30),
    datenbasis character varying(6)
);

ALTER SEQUENCE datenbasis_id_seq OWNED BY datenbasis.id;


CREATE SEQUENCE datensatz_erzeuger_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE datensatz_erzeuger (
    id integer PRIMARY KEY
        DEFAULT nextval('datensatz_erzeuger_id_seq'::regclass),
    netzbetreiber_id character varying(2),
    da_erzeuger_id character varying(2),
    mst_id character varying(5),
    bezeichnung character varying(120),
    letzte_aenderung timestamp without time zone
);

ALTER SEQUENCE datensatz_erzeuger_id_seq OWNED BY datensatz_erzeuger.id;


CREATE SEQUENCE de_vg_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE de_vg (
    id integer PRIMARY KEY DEFAULT nextval('de_vg_id_seq'::regclass),
    use double precision,
    rs character varying(12),
    gf double precision,
    rau_rs character varying(12),
    gen character varying(50),
    des character varying(75),
    isn double precision,
    bemerk character varying(75),
    nambild character varying(16),
    ags character varying(12),
    rs_alt character varying(20),
    wirksamkei date,
    debkg_id character varying(16),
    length numeric,
    shape_area numeric,
    geom public.geometry(MultiPolygon,4326)
);

ALTER SEQUENCE de_vg_id_seq OWNED BY de_vg.id;


CREATE TABLE deskriptor_umwelt (
    id integer PRIMARY KEY,
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
    umw_id character varying(3) NOT NULL
);


CREATE SEQUENCE deskriptoren_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE deskriptoren (
    id integer PRIMARY KEY DEFAULT nextval('deskriptoren_id_seq'::regclass),
    vorgaenger integer,
    ebene smallint,
    s_xx integer,
    sn smallint,
    beschreibung character varying(100),
    bedeutung character varying(300)
);

ALTER SEQUENCE deskriptoren_id_seq OWNED BY deskriptoren.id;


CREATE SEQUENCE favorite_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE favorite (
    id integer PRIMARY KEY DEFAULT nextval('favorite_id_seq'::regclass),
    user_id integer NOT NULL,
    query_id integer NOT NULL
);

ALTER SEQUENCE favorite_id_seq OWNED BY favorite.id;


CREATE SEQUENCE filter_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE filter (
    id integer PRIMARY KEY DEFAULT nextval('filter_id_seq'::regclass),
    query_id integer NOT NULL,
    data_index character varying(50) NOT NULL,
    type character varying(10) NOT NULL,
    label character varying(50) NOT NULL,
    multiselect boolean
);

ALTER SEQUENCE filter_id_seq OWNED BY filter.id;


CREATE SEQUENCE filter_value_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE filter_value (
    id integer PRIMARY KEY DEFAULT nextval('filter_value_id_seq'::regclass),
    query_id integer NOT NULL,
    user_id integer NOT NULL,
    filter_id integer NOT NULL,
    value text
);

ALTER SEQUENCE filter_value_id_seq OWNED BY filter_value.id;


CREATE SEQUENCE koordinaten_art_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE koordinaten_art (
    id integer PRIMARY KEY DEFAULT nextval('koordinaten_art_id_seq'::regclass),
    koordinatenart character varying(50),
    idf_geo_key character varying(1)
);

ALTER SEQUENCE koordinaten_art_id_seq OWNED BY koordinaten_art.id;


CREATE SEQUENCE lada_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE lada_user (
    id integer PRIMARY KEY DEFAULT nextval('lada_user_id_seq'::regclass),
    name character varying(80) NOT NULL
);

ALTER SEQUENCE lada_user_id_seq OWNED BY lada_user.id;


CREATE SEQUENCE mess_einheit_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE mess_einheit (
    id integer PRIMARY KEY DEFAULT nextval('mess_einheit_id_seq'::regclass),
    beschreibung character varying(50),
    einheit character varying(12),
    eudf_messeinheit_id character varying(8),
    umrechnungs_faktor_eudf bigint
);

ALTER SEQUENCE mess_einheit_id_seq OWNED BY mess_einheit.id;


CREATE TABLE mess_methode (
    id character varying(2) PRIMARY KEY,
    beschreibung character varying(300),
    messmethode character varying(50)
);


CREATE TABLE mess_stelle (
    id character varying(5) PRIMARY KEY,
    netzbetreiber_id character varying(2),
    beschreibung character varying(300),
    mess_stelle character varying(60),
    mst_typ character varying(1),
    amtskennung character varying(6)
);



CREATE SEQUENCE messgroesse_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE messgroesse (
    id integer PRIMARY KEY DEFAULT nextval('messgroesse_id_seq'::regclass),
    beschreibung character varying(300),
    messgroesse character varying(50) NOT NULL,
    default_farbe character varying(9),
    idf_nuklid_key character varying(6),
    ist_leitnuklid boolean DEFAULT false,
    eudf_nuklid_id bigint,
    kennung_bvl character varying(7)
);

ALTER SEQUENCE messgroesse_id_seq OWNED BY messgroesse.id;


CREATE SEQUENCE messgroessen_gruppe_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE messgroessen_gruppe (
    id integer PRIMARY KEY
        DEFAULT nextval('messgroessen_gruppe_id_seq'::regclass),
    bezeichnung character varying(80),
    ist_leitnuklidgruppe character(1) DEFAULT NULL::bpchar
);

ALTER SEQUENCE messgroessen_gruppe_id_seq OWNED BY messgroessen_gruppe.id;


CREATE SEQUENCE messprogramm_kategorie_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE messprogramm_kategorie (
    id integer PRIMARY KEY
        DEFAULT nextval('messprogramm_kategorie_id_seq'::regclass),
    netzbetreiber_id character varying(2),
    mpl_id character varying(3),
    bezeichnung character varying(120),
    letzte_aenderung timestamp without time zone
);

ALTER SEQUENCE messprogramm_kategorie_id_seq
    OWNED BY messprogramm_kategorie.id;


CREATE TABLE mg_grp (
    messgroessengruppe_id integer NOT NULL,
    messgroesse_id integer NOT NULL
);


CREATE TABLE mmt_messgroesse_grp (
    messgroessengruppe_id integer NOT NULL,
    mmt_id character varying(2) NOT NULL
);


CREATE VIEW mmt_messgroesse AS
 SELECT mmt_messgroesse_grp.mmt_id,
    mg_grp.messgroesse_id
   FROM mmt_messgroesse_grp,
    mg_grp
  WHERE (mg_grp.messgroessengruppe_id = mmt_messgroesse_grp.messgroessengruppe_id);


CREATE TABLE netz_betreiber (
    id character varying(2) PRIMARY KEY,
    netzbetreiber character varying(50),
    idf_netzbetreiber character varying(1),
    is_bmn boolean DEFAULT false,
    mailverteiler character varying(512),
    aktiv boolean DEFAULT false,
    zust_mst_id character varying(5)
);


CREATE SEQUENCE ort_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE ort (
    id integer PRIMARY KEY DEFAULT nextval('ort_id_seq'::regclass),
    netzbetreiber_id character varying(2),
    ort_id character varying(10),
    langtext character varying(100),
    staat_id smallint,
    gem_id character varying(8),
    unscharf character(1) DEFAULT NULL::bpchar,
    nuts_code character varying(10),
    kda_id integer,
    koord_x_extern character varying(22),
    koord_y_extern character varying(22),
    hoehe_land real,
    letzte_aenderung timestamp without time zone DEFAULT now(),
    latitude double precision,
    longitude double precision,
    geom public.geometry(Point,4326),
    shape public.geometry(MultiPolygon,4326),
    ort_typ smallint,
    kurztext character varying(15),
    berichtstext character varying(70),
    zone character varying(1),
    sektor character varying(2),
    zustaendigkeit character varying(10),
    mp_art character varying(10),
    aktiv character(1),
    anlage_id integer,
    oz_id integer
);

ALTER SEQUENCE ort_id_seq OWNED BY ort.id;


CREATE TABLE ort_typ (
    id smallint PRIMARY KEY,
    ort_typ character varying(60)
);


CREATE TABLE ortszuordnung_typ (
    id character(1) PRIMARY KEY,
    ortstyp character varying(60)
);


CREATE SEQUENCE pflicht_messgroesse_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE pflicht_messgroesse (
    id integer PRIMARY KEY
        DEFAULT nextval('pflicht_messgroesse_id_seq'::regclass),
    messgroesse_id integer,
    mmt_id character varying(2),
    umw_id character varying(3),
    datenbasis_id smallint NOT NULL
);

ALTER SEQUENCE pflicht_messgroesse_id_seq OWNED BY pflicht_messgroesse.id;


CREATE TABLE proben_zusatz (
    id character varying(3) PRIMARY KEY,
    meh_id integer,
    beschreibung character varying(50) NOT NULL,
    zusatzwert character varying(7) NOT NULL,
    eudf_keyword character varying(40)
);


CREATE SEQUENCE probenart_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE probenart (
    id integer PRIMARY KEY DEFAULT nextval('probenart_id_seq'::regclass),
    beschreibung character varying(30),
    probenart character varying(5) NOT NULL,
    probenart_eudf_id character varying(1) NOT NULL
);

ALTER SEQUENCE probenart_id_seq OWNED BY probenart.id;


CREATE SEQUENCE probenehmer_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE probenehmer (
    id integer PRIMARY KEY DEFAULT nextval('probenehmer_id_seq'::regclass),
    netzbetreiber_id character varying(2),
    prn_id character varying(9),
    bearbeiter character varying(25),
    bemerkung character varying(60),
    betrieb character varying(80),
    bezeichnung character varying(80),
    kurz_bezeichnung character varying(10),
    ort character varying(20),
    plz character varying(5),
    strasse character varying(30),
    telefon character varying(20),
    tp character varying(3),
    typ character(1),
    letzte_aenderung timestamp without time zone
);

ALTER SEQUENCE probenehmer_id_seq OWNED BY probenehmer.id;


CREATE SEQUENCE query_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE query (
    id integer PRIMARY KEY DEFAULT nextval('query_id_seq'::regclass),
    name character varying(80) NOT NULL,
    type character varying(30) NOT NULL,
    sql character varying(1500) NOT NULL,
    description character varying(100)
);

ALTER SEQUENCE query_id_seq OWNED BY query.id;


CREATE SEQUENCE result_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE result (
    id integer PRIMARY KEY DEFAULT nextval('result_id_seq'::regclass),
    query_id integer NOT NULL,
    data_index character varying(50) NOT NULL,
    header character varying(50) NOT NULL,
    width integer,
    flex boolean,
    index integer
);

ALTER SEQUENCE result_id_seq OWNED BY result.id;



CREATE VIEW s_00_view AS
 SELECT deskriptoren.s_xx AS s00,
    deskriptoren.bedeutung,
    deskriptoren.beschreibung,
    deskriptoren.sn
   FROM deskriptoren
  WHERE (deskriptoren.ebene = 0);



CREATE VIEW s_01_view AS
 SELECT d1.s_xx AS s01,
    d2.s_xx AS s00,
    d1.bedeutung,
    d1.beschreibung,
    d1.sn
   FROM (deskriptoren d1
     JOIN deskriptoren d2 ON ((d1.vorgaenger = d2.id)))
  WHERE (d1.ebene = 1);



CREATE VIEW s_02_s_01_view AS
 SELECT d1.s_xx AS s01,
    d2.s_xx AS s02
   FROM (deskriptoren d1
     JOIN deskriptoren d2 ON ((d2.vorgaenger = d1.id)))
  WHERE (d2.ebene = 2);



CREATE VIEW s_02_view AS
 SELECT DISTINCT deskriptoren.s_xx AS s00,
    deskriptoren.bedeutung,
    deskriptoren.beschreibung,
    deskriptoren.sn
   FROM deskriptoren
  WHERE (deskriptoren.ebene = 2);



CREATE VIEW s_03_s_01_view AS
 SELECT d1.s_xx AS s01,
    d2.s_xx AS s03
   FROM (deskriptoren d1
     JOIN deskriptoren d2 ON ((d2.vorgaenger = d1.id)))
  WHERE ((d2.ebene = 3) AND (d1.ebene = 1));



CREATE VIEW s_03_view AS
 SELECT d1.s_xx AS s03,
    d2.s_xx AS s02,
    d1.bedeutung,
    d1.beschreibung,
    d1.sn
   FROM (deskriptoren d1
     JOIN deskriptoren d2 ON ((d1.vorgaenger = d2.id)))
  WHERE ((d1.ebene = 3) AND (d2.ebene = 2))
UNION
 SELECT d1.s_xx AS s03,
    NULL::integer AS s02,
    d1.bedeutung,
    d1.beschreibung,
    d1.sn
   FROM (deskriptoren d1
     JOIN deskriptoren d2 ON ((d1.vorgaenger = d2.id)))
  WHERE ((d1.ebene = 3) AND (d2.ebene = 1));



CREATE VIEW s_04_s_01_view AS
 SELECT DISTINCT d1.s_xx AS s01,
    d2.s_xx AS s04
   FROM (deskriptoren d1
     JOIN deskriptoren d2 ON ((d2.vorgaenger = d1.id)))
  WHERE (d2.ebene = 4);



CREATE VIEW s_04_view AS
 SELECT DISTINCT deskriptoren.s_xx AS s04,
    deskriptoren.bedeutung,
    deskriptoren.beschreibung,
    deskriptoren.sn
   FROM deskriptoren
  WHERE (deskriptoren.ebene = 4);



CREATE VIEW s_05_s_01_view AS
 SELECT d1.s_xx AS s01,
    d2.s_xx AS s05
   FROM (deskriptoren d1
     JOIN deskriptoren d2 ON ((d2.vorgaenger = d1.id)))
  WHERE (d2.ebene = 5);



CREATE VIEW s_05_view AS
 SELECT DISTINCT deskriptoren.s_xx AS s05,
    deskriptoren.bedeutung,
    deskriptoren.beschreibung,
    deskriptoren.sn
   FROM deskriptoren
  WHERE (deskriptoren.ebene = 5);



CREATE VIEW s_06_s_01_view AS
 SELECT d1.s_xx AS s01,
    d2.s_xx AS s06
   FROM (deskriptoren d1
     JOIN deskriptoren d2 ON ((d2.vorgaenger = d1.id)))
  WHERE (d2.ebene = 6);



CREATE VIEW s_06_view AS
 SELECT DISTINCT deskriptoren.s_xx AS s06,
    deskriptoren.bedeutung,
    deskriptoren.beschreibung,
    deskriptoren.sn
   FROM deskriptoren
  WHERE (deskriptoren.ebene = 6);



CREATE VIEW s_07_s_01_view AS
 SELECT d1.s_xx AS s01,
    d2.s_xx AS s07
   FROM (deskriptoren d1
     JOIN deskriptoren d2 ON ((d2.vorgaenger = d1.id)))
  WHERE (d2.ebene = 7);



CREATE VIEW s_07_view AS
 SELECT DISTINCT deskriptoren.s_xx AS s07,
    deskriptoren.bedeutung,
    deskriptoren.beschreibung,
    deskriptoren.sn
   FROM deskriptoren
  WHERE (deskriptoren.ebene = 7);



CREATE VIEW s_08_s_01_view AS
 SELECT d1.s_xx AS s01,
    d2.s_xx AS s08
   FROM (deskriptoren d1
     JOIN deskriptoren d2 ON ((d2.vorgaenger = d1.id)))
  WHERE (d2.ebene = 8);



CREATE VIEW s_08_view AS
 SELECT DISTINCT deskriptoren.s_xx AS s08,
    deskriptoren.bedeutung,
    deskriptoren.beschreibung,
    deskriptoren.sn
   FROM deskriptoren
  WHERE (deskriptoren.ebene = 8);



CREATE VIEW s_09_s_01_view AS
 SELECT d1.s_xx AS s01,
    d2.s_xx AS s09
   FROM (deskriptoren d1
     JOIN deskriptoren d2 ON ((d2.vorgaenger = d1.id)))
  WHERE (d2.ebene = 9);



CREATE VIEW s_09_view AS
 SELECT DISTINCT deskriptoren.s_xx AS s09,
    deskriptoren.bedeutung,
    deskriptoren.beschreibung,
    deskriptoren.sn
   FROM deskriptoren
  WHERE (deskriptoren.ebene = 9);



CREATE VIEW s_10_s_01_view AS
 SELECT d1.s_xx AS s01,
    d2.s_xx AS s10
   FROM (deskriptoren d1
     JOIN deskriptoren d2 ON ((d2.vorgaenger = d1.id)))
  WHERE (d2.ebene = 10);



CREATE VIEW s_10_view AS
 SELECT DISTINCT deskriptoren.s_xx AS s10,
    deskriptoren.bedeutung,
    deskriptoren.beschreibung,
    deskriptoren.sn
   FROM deskriptoren
  WHERE (deskriptoren.ebene = 10);



CREATE VIEW s_11_s_01_view AS
 SELECT d1.s_xx AS s01,
    d2.s_xx AS s11
   FROM (deskriptoren d1
     JOIN deskriptoren d2 ON ((d2.vorgaenger = d1.id)))
  WHERE (d2.ebene = 11);



CREATE VIEW s_11_view AS
 SELECT DISTINCT deskriptoren.s_xx AS s11,
    deskriptoren.bedeutung,
    deskriptoren.beschreibung,
    deskriptoren.sn
   FROM deskriptoren
  WHERE (deskriptoren.ebene = 11);



CREATE VIEW s_12_s_01_view AS
 SELECT d1.s_xx AS s01,
    d2.s_xx AS s12
   FROM (deskriptoren d1
     JOIN deskriptoren d2 ON ((d2.vorgaenger = d1.id)))
  WHERE (d2.ebene = 12);



CREATE VIEW s_12_view AS
 SELECT DISTINCT deskriptoren.s_xx AS s12,
    deskriptoren.bedeutung,
    deskriptoren.beschreibung,
    deskriptoren.sn
   FROM deskriptoren
  WHERE (deskriptoren.ebene = 12);



CREATE SEQUENCE staat_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE staat (
    id integer PRIMARY KEY DEFAULT nextval('staat_id_seq'::regclass),
    staat character varying(50) NOT NULL,
    hkl_id smallint NOT NULL,
    staat_iso character varying(2) NOT NULL,
    staat_kurz character varying(5),
    eu character(1) DEFAULT NULL::bpchar,
    koord_x_extern character varying(22),
    koord_y_extern character varying(22),
    kda_id integer
);

ALTER SEQUENCE staat_id_seq OWNED BY staat.id;


CREATE TABLE status_kombi (
    id integer PRIMARY KEY,
    stufe_id integer,
    wert_id integer
);

CREATE TABLE status_reihenfolge (
    id integer PRIMARY KEY,
    von_id integer,
    zu_id integer
);

CREATE TABLE status_stufe (
    id integer PRIMARY KEY,
    stufe character varying(50)
);

CREATE TABLE status_wert (
    id integer PRIMARY KEY,
    wert character varying(50)
);


CREATE TABLE umwelt (
    id character varying(3) PRIMARY KEY,
    beschreibung character varying(300),
    umwelt_bereich character varying(80) NOT NULL,
    meh_id integer
);


CREATE TABLE verwaltungseinheit (
    id character varying(8) PRIMARY KEY,
    bundesland character varying(8) NOT NULL,
    kda_id integer,
    kreis character varying(8),
    nuts character varying(10),
    regbezirk character varying(8),
    bezeichnung character varying(80) NOT NULL,
    is_bundesland character(1) NOT NULL,
    is_gemeinde character(1) NOT NULL,
    is_landkreis character(1) NOT NULL,
    is_regbezirk character(1) NOT NULL,
    koord_x_extern character varying(22),
    koord_y_extern character varying(22),
    plz character varying(6),
    longitude double precision,
    latitude double precision
);


CREATE VIEW status_erreichbar AS (
    SELECT DISTINCT k.wert_id,
        j.wert_id AS cur_wert,
        j.stufe_id AS cur_stufe
    FROM stammdaten.status_kombi k
    JOIN (SELECT r.zu_id,
              kom.wert_id,
              kom.stufe_id
          FROM stammdaten.status_reihenfolge r
          JOIN stammdaten.status_kombi kom
          ON kom.id = r.von_id) j
    ON j.zu_id = k.id
);



ALTER TABLE ONLY lada_user
    ADD CONSTRAINT lada_user_name_key UNIQUE (name);


ALTER TABLE ONLY mg_grp
    ADD CONSTRAINT mg_grp_pkey PRIMARY KEY (messgroessengruppe_id, messgroesse_id);


ALTER TABLE ONLY mmt_messgroesse_grp
    ADD CONSTRAINT mmt_messgroesse_grp_pkey PRIMARY KEY (messgroessengruppe_id, mmt_id);


ALTER TABLE ONLY proben_zusatz
    ADD CONSTRAINT proben_zusatz_eudf_keyword_key UNIQUE (eudf_keyword);


ALTER TABLE ONLY umwelt
    ADD CONSTRAINT umwelt_umwelt_bereich_key UNIQUE (umwelt_bereich);


CREATE INDEX de_vg_geom_gist ON de_vg USING gist (geom);


CREATE INDEX fts_stauts_kooin10001 ON staat USING btree (kda_id);



ALTER TABLE ONLY auth
    ADD CONSTRAINT auth_funktion_fkey FOREIGN KEY (funktion_id) REFERENCES auth_funktion(id);



ALTER TABLE ONLY auth
    ADD CONSTRAINT auth_labor_fkey FOREIGN KEY (labor_mst_id) REFERENCES mess_stelle(id);



ALTER TABLE ONLY auth_lst_umw
    ADD CONSTRAINT auth_lst_umw_lst_fkey FOREIGN KEY (lst_id) REFERENCES mess_stelle(id);



ALTER TABLE ONLY auth_lst_umw
    ADD CONSTRAINT auth_lst_umw_umw_fkey FOREIGN KEY (umw_id) REFERENCES umwelt(id);



ALTER TABLE ONLY auth
    ADD CONSTRAINT auth_mst_id_fkey FOREIGN KEY (mst_id) REFERENCES mess_stelle(id);



ALTER TABLE ONLY auth
    ADD CONSTRAINT auth_netzbetreiber_id_fkey FOREIGN KEY (netzbetreiber_id) REFERENCES netz_betreiber(id);



ALTER TABLE ONLY datensatz_erzeuger
    ADD CONSTRAINT datensatz_erzeuger_mst_id_fkey1 FOREIGN KEY (mst_id) REFERENCES mess_stelle(id);



ALTER TABLE ONLY datensatz_erzeuger
    ADD CONSTRAINT datensatz_erzeuger_netzbetreiber_id_fkey FOREIGN KEY (netzbetreiber_id) REFERENCES netz_betreiber(id);



ALTER TABLE ONLY favorite
    ADD CONSTRAINT favorite_query_id_fkey FOREIGN KEY (query_id) REFERENCES query(id);



ALTER TABLE ONLY favorite
    ADD CONSTRAINT favorite_user_id_fkey FOREIGN KEY (user_id) REFERENCES lada_user(id);



ALTER TABLE ONLY filter
    ADD CONSTRAINT filter_query_id_fkey FOREIGN KEY (query_id) REFERENCES query(id);



ALTER TABLE ONLY filter_value
    ADD CONSTRAINT filter_value_filter_id_fkey FOREIGN KEY (filter_id) REFERENCES filter(id);



ALTER TABLE ONLY filter_value
    ADD CONSTRAINT filter_value_query_id_fkey FOREIGN KEY (query_id) REFERENCES query(id);



ALTER TABLE ONLY filter_value
    ADD CONSTRAINT filter_value_user_id_fkey FOREIGN KEY (user_id) REFERENCES lada_user(id);



ALTER TABLE ONLY deskriptoren
    ADD CONSTRAINT fk_deskriptoren_vorgaenger FOREIGN KEY (vorgaenger) REFERENCES deskriptoren(id);



ALTER TABLE ONLY messprogramm_kategorie
    ADD CONSTRAINT messprogramm_kategorie_netzbetreiber_id_fkey FOREIGN KEY (netzbetreiber_id) REFERENCES netz_betreiber(id);



ALTER TABLE ONLY ort
    ADD CONSTRAINT ort_anlage_fkey FOREIGN KEY (anlage_id) REFERENCES ort(id);



ALTER TABLE ONLY ort
    ADD CONSTRAINT ort_gem_id_fkey FOREIGN KEY (gem_id) REFERENCES verwaltungseinheit(id);



ALTER TABLE ONLY ort
    ADD CONSTRAINT ort_kda_id_fkey FOREIGN KEY (kda_id) REFERENCES koordinaten_art(id);



ALTER TABLE ONLY ort
    ADD CONSTRAINT ort_netzbetreiber_id_fkey FOREIGN KEY (netzbetreiber_id) REFERENCES netz_betreiber(id);



ALTER TABLE ONLY ort
    ADD CONSTRAINT ort_ort_typ_fkey FOREIGN KEY (ort_typ) REFERENCES ort_typ(id);



ALTER TABLE ONLY ort
    ADD CONSTRAINT ort_oz_fkey FOREIGN KEY (oz_id) REFERENCES ort(id);



ALTER TABLE ONLY ort
    ADD CONSTRAINT ort_staat_id_fkey FOREIGN KEY (staat_id) REFERENCES staat(id);



ALTER TABLE ONLY pflicht_messgroesse
    ADD CONSTRAINT pflicht_messgroesse_datenbasis_id_fkey FOREIGN KEY (datenbasis_id) REFERENCES datenbasis(id);



ALTER TABLE ONLY pflicht_messgroesse
    ADD CONSTRAINT pflicht_messgroesse_mmt_id_fkey FOREIGN KEY (mmt_id) REFERENCES mess_methode(id);



ALTER TABLE ONLY pflicht_messgroesse
    ADD CONSTRAINT pflicht_messgroesse_umw_id_fkey FOREIGN KEY (umw_id) REFERENCES umwelt(id);



ALTER TABLE ONLY proben_zusatz
    ADD CONSTRAINT proben_zusatz_meh_id_fkey FOREIGN KEY (meh_id) REFERENCES mess_einheit(id);



ALTER TABLE ONLY probenehmer
    ADD CONSTRAINT probenehmer_netzbetreiber_id_fkey FOREIGN KEY (netzbetreiber_id) REFERENCES netz_betreiber(id);



ALTER TABLE ONLY result
    ADD CONSTRAINT result_query_id_fkey FOREIGN KEY (query_id) REFERENCES query(id);



ALTER TABLE ONLY staat
    ADD CONSTRAINT staat_kda_id_fkey FOREIGN KEY (kda_id) REFERENCES koordinaten_art(id);



ALTER TABLE ONLY status_kombi
    ADD CONSTRAINT status_kombi_stufe_id_fkey FOREIGN KEY (stufe_id) REFERENCES status_stufe(id);



ALTER TABLE ONLY status_kombi
    ADD CONSTRAINT status_kombi_wert_id_fkey FOREIGN KEY (wert_id) REFERENCES status_wert(id);



ALTER TABLE ONLY status_reihenfolge
    ADD CONSTRAINT status_reihenfolge_von_id_fkey FOREIGN KEY (von_id) REFERENCES status_kombi(id);



ALTER TABLE ONLY status_reihenfolge
    ADD CONSTRAINT status_reihenfolge_zu_id_fkey FOREIGN KEY (zu_id) REFERENCES status_kombi(id);



ALTER TABLE ONLY umwelt
    ADD CONSTRAINT umwelt_meh_id_fkey FOREIGN KEY (meh_id) REFERENCES mess_einheit(id);



ALTER TABLE ONLY verwaltungseinheit
    ADD CONSTRAINT verwaltungseinheit_kda_id_fkey FOREIGN KEY (kda_id) REFERENCES koordinaten_art(id);


COMMIT;
