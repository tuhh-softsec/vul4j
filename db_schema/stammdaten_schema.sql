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


CREATE TABLE auth (
    id integer NOT NULL,
    ldap_group character varying(40) NOT NULL,
    netzbetreiber_id character varying(2),
    mst_id character varying(5),
    labor_mst_id character varying(5),
    funktion_id smallint
);


--
-- Name: auth_funktion; Type: TABLE; Schema: stammdaten; Owner: -; Tablespace:
--

CREATE TABLE auth_funktion (
    id smallint NOT NULL,
    funktion character varying(40)
);


--
-- Name: auth_id_seq; Type: SEQUENCE; Schema: stammdaten; Owner: -
--

CREATE SEQUENCE auth_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: auth_id_seq; Type: SEQUENCE OWNED BY; Schema: stammdaten; Owner: -
--

ALTER SEQUENCE auth_id_seq OWNED BY auth.id;


--
-- Name: auth_id_seq1; Type: SEQUENCE; Schema: stammdaten; Owner: -
--

CREATE SEQUENCE auth_id_seq1
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: auth_id_seq1; Type: SEQUENCE OWNED BY; Schema: stammdaten; Owner: -
--

ALTER SEQUENCE auth_id_seq1 OWNED BY auth.id;


--
-- Name: auth_lst_umw; Type: TABLE; Schema: stammdaten; Owner: -; Tablespace:
--

CREATE TABLE auth_lst_umw (
    id integer NOT NULL,
    lst_id character varying(5),
    umw_id character varying(3)
);


--
-- Name: auth_lst_umw_id_seq; Type: SEQUENCE; Schema: stammdaten; Owner: -
--

CREATE SEQUENCE auth_lst_umw_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: auth_lst_umw_id_seq; Type: SEQUENCE OWNED BY; Schema: stammdaten; Owner: -
--

ALTER SEQUENCE auth_lst_umw_id_seq OWNED BY auth_lst_umw.id;


--
-- Name: datenbasis; Type: TABLE; Schema: stammdaten; Owner: -; Tablespace:
--

CREATE TABLE datenbasis (
    id integer NOT NULL,
    beschreibung character varying(30),
    datenbasis character varying(6)
);


--
-- Name: datenbasis_id_seq; Type: SEQUENCE; Schema: stammdaten; Owner: -
--

CREATE SEQUENCE datenbasis_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: datenbasis_id_seq; Type: SEQUENCE OWNED BY; Schema: stammdaten; Owner: -
--

ALTER SEQUENCE datenbasis_id_seq OWNED BY datenbasis.id;


--
-- Name: datensatz_erzeuger; Type: TABLE; Schema: stammdaten; Owner: -; Tablespace:
--

CREATE TABLE datensatz_erzeuger (
    id integer NOT NULL,
    netzbetreiber_id character varying(2),
    da_erzeuger_id character varying(2),
    mst_id character varying(5),
    bezeichnung character varying(120),
    letzte_aenderung timestamp without time zone
);


--
-- Name: datensatz_erzeuger_id_seq; Type: SEQUENCE; Schema: stammdaten; Owner: -
--

CREATE SEQUENCE datensatz_erzeuger_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: datensatz_erzeuger_id_seq; Type: SEQUENCE OWNED BY; Schema: stammdaten; Owner: -
--

ALTER SEQUENCE datensatz_erzeuger_id_seq OWNED BY datensatz_erzeuger.id;


--
-- Name: de_vg; Type: TABLE; Schema: stammdaten; Owner: -; Tablespace:
--

CREATE TABLE de_vg (
    id integer NOT NULL,
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


--
-- Name: de_vg_id_seq; Type: SEQUENCE; Schema: stammdaten; Owner: -
--

CREATE SEQUENCE de_vg_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: de_vg_id_seq; Type: SEQUENCE OWNED BY; Schema: stammdaten; Owner: -
--

ALTER SEQUENCE de_vg_id_seq OWNED BY de_vg.id;


--
-- Name: deskriptor_umwelt; Type: TABLE; Schema: stammdaten; Owner: -; Tablespace:
--

CREATE TABLE deskriptor_umwelt (
    id integer NOT NULL,
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


--
-- Name: deskriptoren; Type: TABLE; Schema: stammdaten; Owner: -; Tablespace:
--

CREATE TABLE deskriptoren (
    id integer NOT NULL,
    vorgaenger integer,
    ebene smallint,
    s_xx integer,
    sn smallint,
    beschreibung character varying(100),
    bedeutung character varying(300)
);


--
-- Name: deskriptoren_id_seq; Type: SEQUENCE; Schema: stammdaten; Owner: -
--

CREATE SEQUENCE deskriptoren_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: deskriptoren_id_seq; Type: SEQUENCE OWNED BY; Schema: stammdaten; Owner: -
--

ALTER SEQUENCE deskriptoren_id_seq OWNED BY deskriptoren.id;


--
-- Name: favorite; Type: TABLE; Schema: stammdaten; Owner: -; Tablespace: 
--

CREATE TABLE favorite (
    id integer NOT NULL,
    user_id integer NOT NULL,
    query_id integer NOT NULL
);


--
-- Name: favorite_id_seq; Type: SEQUENCE; Schema: stammdaten; Owner: -
--

CREATE SEQUENCE favorite_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: favorite_id_seq; Type: SEQUENCE OWNED BY; Schema: stammdaten; Owner: -
--

ALTER SEQUENCE favorite_id_seq OWNED BY favorite.id;


--
-- Name: filter; Type: TABLE; Schema: stammdaten; Owner: -; Tablespace: 
--

CREATE TABLE filter (
    id integer NOT NULL,
    query_id integer NOT NULL,
    data_index character varying(50) NOT NULL,
    type character varying(10) NOT NULL,
    label character varying(50) NOT NULL,
    multiselect boolean
);


--
-- Name: filter_id_seq; Type: SEQUENCE; Schema: stammdaten; Owner: -
--

CREATE SEQUENCE filter_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: filter_id_seq; Type: SEQUENCE OWNED BY; Schema: stammdaten; Owner: -
--

ALTER SEQUENCE filter_id_seq OWNED BY filter.id;


--
-- Name: filter_value; Type: TABLE; Schema: stammdaten; Owner: -; Tablespace: 
--

CREATE TABLE filter_value (
    id integer NOT NULL,
    query_id integer NOT NULL,
    user_id integer NOT NULL,
    filter_id integer NOT NULL,
    value text
);


--
-- Name: filter_value_id_seq; Type: SEQUENCE; Schema: stammdaten; Owner: -
--

CREATE SEQUENCE filter_value_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: filter_value_id_seq; Type: SEQUENCE OWNED BY; Schema: stammdaten; Owner: -
--

ALTER SEQUENCE filter_value_id_seq OWNED BY filter_value.id;


--
-- Name: koordinaten_art; Type: TABLE; Schema: stammdaten; Owner: -; Tablespace: 
--

CREATE TABLE koordinaten_art (
    id integer NOT NULL,
    koordinatenart character varying(50),
    idf_geo_key character varying(1)
);


--
-- Name: koordinaten_art_id_seq; Type: SEQUENCE; Schema: stammdaten; Owner: -
--

CREATE SEQUENCE koordinaten_art_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: koordinaten_art_id_seq; Type: SEQUENCE OWNED BY; Schema: stammdaten; Owner: -
--

ALTER SEQUENCE koordinaten_art_id_seq OWNED BY koordinaten_art.id;


--
-- Name: lada_user; Type: TABLE; Schema: stammdaten; Owner: -; Tablespace: 
--

CREATE TABLE lada_user (
    id integer NOT NULL,
    name character varying(80) NOT NULL
);


--
-- Name: lada_user_id_seq; Type: SEQUENCE; Schema: stammdaten; Owner: -
--

CREATE SEQUENCE lada_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: lada_user_id_seq; Type: SEQUENCE OWNED BY; Schema: stammdaten; Owner: -
--

ALTER SEQUENCE lada_user_id_seq OWNED BY lada_user.id;


--
-- Name: mess_einheit; Type: TABLE; Schema: stammdaten; Owner: -; Tablespace: 
--

CREATE TABLE mess_einheit (
    id integer NOT NULL,
    beschreibung character varying(50),
    einheit character varying(12),
    eudf_messeinheit_id character varying(8),
    umrechnungs_faktor_eudf bigint
);


--
-- Name: mess_einheit_id_seq; Type: SEQUENCE; Schema: stammdaten; Owner: -
--

CREATE SEQUENCE mess_einheit_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: mess_einheit_id_seq; Type: SEQUENCE OWNED BY; Schema: stammdaten; Owner: -
--

ALTER SEQUENCE mess_einheit_id_seq OWNED BY mess_einheit.id;


--
-- Name: mess_methode; Type: TABLE; Schema: stammdaten; Owner: -; Tablespace:
--

CREATE TABLE mess_methode (
    id character varying(2) NOT NULL,
    beschreibung character varying(300),
    messmethode character varying(50)
);


--
-- Name: mess_stelle; Type: TABLE; Schema: stammdaten; Owner: -; Tablespace:
--

CREATE TABLE mess_stelle (
    id character varying(5) NOT NULL,
    netzbetreiber_id character varying(2),
    beschreibung character varying(300),
    mess_stelle character varying(60),
    mst_typ character varying(1),
    amtskennung character varying(6)
);


--
-- Name: messgroesse; Type: TABLE; Schema: stammdaten; Owner: -; Tablespace:
--

CREATE TABLE messgroesse (
    id integer NOT NULL,
    beschreibung character varying(300),
    messgroesse character varying(50) NOT NULL,
    default_farbe character varying(9),
    idf_nuklid_key character varying(6),
    ist_leitnuklid boolean DEFAULT false,
    eudf_nuklid_id bigint,
    kennung_bvl character varying(7)
);


--
-- Name: messgroesse_id_seq; Type: SEQUENCE; Schema: stammdaten; Owner: -
--

CREATE SEQUENCE messgroesse_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: messgroesse_id_seq; Type: SEQUENCE OWNED BY; Schema: stammdaten; Owner: -
--

ALTER SEQUENCE messgroesse_id_seq OWNED BY messgroesse.id;


--
-- Name: messgroessen_gruppe; Type: TABLE; Schema: stammdaten; Owner: -; Tablespace:
--

CREATE TABLE messgroessen_gruppe (
    id integer NOT NULL,
    bezeichnung character varying(80),
    ist_leitnuklidgruppe character(1) DEFAULT NULL::bpchar
);


--
-- Name: messgroessen_gruppe_id_seq; Type: SEQUENCE; Schema: stammdaten; Owner: -
--

CREATE SEQUENCE messgroessen_gruppe_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: messgroessen_gruppe_id_seq; Type: SEQUENCE OWNED BY; Schema: stammdaten; Owner: -
--

ALTER SEQUENCE messgroessen_gruppe_id_seq OWNED BY messgroessen_gruppe.id;


--
-- Name: messprogramm_kategorie; Type: TABLE; Schema: stammdaten; Owner: -; Tablespace:
--

CREATE TABLE messprogramm_kategorie (
    id integer NOT NULL,
    netzbetreiber_id character varying(2),
    mpl_id character varying(3),
    bezeichnung character varying(120),
    letzte_aenderung timestamp without time zone
);


--
-- Name: messprogramm_kategorie_id_seq; Type: SEQUENCE; Schema: stammdaten; Owner: -
--

CREATE SEQUENCE messprogramm_kategorie_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: messprogramm_kategorie_id_seq; Type: SEQUENCE OWNED BY; Schema: stammdaten; Owner: -
--

ALTER SEQUENCE messprogramm_kategorie_id_seq OWNED BY messprogramm_kategorie.id;


--
-- Name: mg_grp; Type: TABLE; Schema: stammdaten; Owner: -; Tablespace:
--

CREATE TABLE mg_grp (
    messgroessengruppe_id integer NOT NULL,
    messgroesse_id integer NOT NULL
);


--
-- Name: mmt_messgroesse_grp; Type: TABLE; Schema: stammdaten; Owner: -; Tablespace:
--

CREATE TABLE mmt_messgroesse_grp (
    messgroessengruppe_id integer NOT NULL,
    mmt_id character varying(2) NOT NULL
);


--
-- Name: mmt_messgroesse; Type: VIEW; Schema: stammdaten; Owner: -
--

CREATE VIEW mmt_messgroesse AS
 SELECT mmt_messgroesse_grp.mmt_id,
    mg_grp.messgroesse_id
   FROM mmt_messgroesse_grp,
    mg_grp
  WHERE (mg_grp.messgroessengruppe_id = mmt_messgroesse_grp.messgroessengruppe_id);


--
-- Name: netz_betreiber; Type: TABLE; Schema: stammdaten; Owner: -; Tablespace:
--

CREATE TABLE netz_betreiber (
    id character varying(2) NOT NULL,
    netzbetreiber character varying(50),
    idf_netzbetreiber character varying(1),
    is_bmn boolean DEFAULT false,
    mailverteiler character varying(512),
    aktiv boolean DEFAULT false,
    zust_mst_id character varying(5)
);


--
-- Name: ort; Type: TABLE; Schema: stammdaten; Owner: -; Tablespace:
--

CREATE TABLE ort (
    id integer NOT NULL,
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


--
-- Name: ort_id_seq; Type: SEQUENCE; Schema: stammdaten; Owner: -
--

CREATE SEQUENCE ort_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: ort_id_seq; Type: SEQUENCE OWNED BY; Schema: stammdaten; Owner: -
--

ALTER SEQUENCE ort_id_seq OWNED BY ort.id;


--
-- Name: ort_typ; Type: TABLE; Schema: stammdaten; Owner: -; Tablespace:
--

CREATE TABLE ort_typ (
    id smallint NOT NULL,
    ort_typ character varying(60)
);


--
-- Name: ortszuordnung_typ; Type: TABLE; Schema: stammdaten; Owner: -; Tablespace:
--

CREATE TABLE ortszuordnung_typ (
    id character(1) NOT NULL,
    ortstyp character varying(60)
);


--
-- Name: pflicht_messgroesse; Type: TABLE; Schema: stammdaten; Owner: -; Tablespace:
--

CREATE TABLE pflicht_messgroesse (
    id integer NOT NULL,
    messgroesse_id integer,
    mmt_id character varying(2),
    umw_id character varying(3),
    datenbasis_id smallint NOT NULL
);


--
-- Name: pflicht_messgroesse_id_seq; Type: SEQUENCE; Schema: stammdaten; Owner: -
--

CREATE SEQUENCE pflicht_messgroesse_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: pflicht_messgroesse_id_seq; Type: SEQUENCE OWNED BY; Schema: stammdaten; Owner: -
--

ALTER SEQUENCE pflicht_messgroesse_id_seq OWNED BY pflicht_messgroesse.id;


--
-- Name: proben_zusatz; Type: TABLE; Schema: stammdaten; Owner: -; Tablespace:
--

CREATE TABLE proben_zusatz (
    id character varying(3) NOT NULL,
    meh_id integer,
    beschreibung character varying(50) NOT NULL,
    zusatzwert character varying(7) NOT NULL,
    eudf_keyword character varying(40)
);


--
-- Name: probenart; Type: TABLE; Schema: stammdaten; Owner: -; Tablespace:
--

CREATE TABLE probenart (
    id integer NOT NULL,
    beschreibung character varying(30),
    probenart character varying(5) NOT NULL,
    probenart_eudf_id character varying(1) NOT NULL
);


--
-- Name: probenart_id_seq; Type: SEQUENCE; Schema: stammdaten; Owner: -
--

CREATE SEQUENCE probenart_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: probenart_id_seq; Type: SEQUENCE OWNED BY; Schema: stammdaten; Owner: -
--

ALTER SEQUENCE probenart_id_seq OWNED BY probenart.id;


--
-- Name: probenehmer; Type: TABLE; Schema: stammdaten; Owner: -; Tablespace:
--

CREATE TABLE probenehmer (
    id integer NOT NULL,
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


--
-- Name: probenehmer_id_seq; Type: SEQUENCE; Schema: stammdaten; Owner: -
--

CREATE SEQUENCE probenehmer_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: probenehmer_id_seq; Type: SEQUENCE OWNED BY; Schema: stammdaten; Owner: -
--

ALTER SEQUENCE probenehmer_id_seq OWNED BY probenehmer.id;


--
-- Name: query; Type: TABLE; Schema: stammdaten; Owner: -; Tablespace: 
--

CREATE TABLE query (
    id integer NOT NULL,
    name character varying(80) NOT NULL,
    type character varying(30) NOT NULL,
    sql character varying(1500) NOT NULL,
    description character varying(100)
);


--
-- Name: query_id_seq; Type: SEQUENCE; Schema: stammdaten; Owner: -
--

CREATE SEQUENCE query_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: query_id_seq; Type: SEQUENCE OWNED BY; Schema: stammdaten; Owner: -
--

ALTER SEQUENCE query_id_seq OWNED BY query.id;


--
-- Name: result; Type: TABLE; Schema: stammdaten; Owner: -; Tablespace: 
--

CREATE TABLE result (
    id integer NOT NULL,
    query_id integer NOT NULL,
    data_index character varying(50) NOT NULL,
    header character varying(50) NOT NULL,
    width integer,
    flex boolean,
    index integer
);


--
-- Name: result_id_seq; Type: SEQUENCE; Schema: stammdaten; Owner: -
--

CREATE SEQUENCE result_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: result_id_seq; Type: SEQUENCE OWNED BY; Schema: stammdaten; Owner: -
--

ALTER SEQUENCE result_id_seq OWNED BY result.id;


--
-- Name: s_00_view; Type: VIEW; Schema: stammdaten; Owner: -
--

CREATE VIEW s_00_view AS
 SELECT deskriptoren.s_xx AS s00,
    deskriptoren.bedeutung,
    deskriptoren.beschreibung,
    deskriptoren.sn
   FROM deskriptoren
  WHERE (deskriptoren.ebene = 0);


--
-- Name: s_01_view; Type: VIEW; Schema: stammdaten; Owner: -
--

CREATE VIEW s_01_view AS
 SELECT d1.s_xx AS s01,
    d2.s_xx AS s00,
    d1.bedeutung,
    d1.beschreibung,
    d1.sn
   FROM (deskriptoren d1
     JOIN deskriptoren d2 ON ((d1.vorgaenger = d2.id)))
  WHERE (d1.ebene = 1);


--
-- Name: s_02_s_01_view; Type: VIEW; Schema: stammdaten; Owner: -
--

CREATE VIEW s_02_s_01_view AS
 SELECT d1.s_xx AS s01,
    d2.s_xx AS s02
   FROM (deskriptoren d1
     JOIN deskriptoren d2 ON ((d2.vorgaenger = d1.id)))
  WHERE (d2.ebene = 2);


--
-- Name: s_02_view; Type: VIEW; Schema: stammdaten; Owner: -
--

CREATE VIEW s_02_view AS
 SELECT DISTINCT deskriptoren.s_xx AS s00,
    deskriptoren.bedeutung,
    deskriptoren.beschreibung,
    deskriptoren.sn
   FROM deskriptoren
  WHERE (deskriptoren.ebene = 2);


--
-- Name: s_03_s_01_view; Type: VIEW; Schema: stammdaten; Owner: -
--

CREATE VIEW s_03_s_01_view AS
 SELECT d1.s_xx AS s01,
    d2.s_xx AS s03
   FROM (deskriptoren d1
     JOIN deskriptoren d2 ON ((d2.vorgaenger = d1.id)))
  WHERE ((d2.ebene = 3) AND (d1.ebene = 1));


--
-- Name: s_03_view; Type: VIEW; Schema: stammdaten; Owner: -
--

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


--
-- Name: s_04_s_01_view; Type: VIEW; Schema: stammdaten; Owner: -
--

CREATE VIEW s_04_s_01_view AS
 SELECT DISTINCT d1.s_xx AS s01,
    d2.s_xx AS s04
   FROM (deskriptoren d1
     JOIN deskriptoren d2 ON ((d2.vorgaenger = d1.id)))
  WHERE (d2.ebene = 4);


--
-- Name: s_04_view; Type: VIEW; Schema: stammdaten; Owner: -
--

CREATE VIEW s_04_view AS
 SELECT DISTINCT deskriptoren.s_xx AS s04,
    deskriptoren.bedeutung,
    deskriptoren.beschreibung,
    deskriptoren.sn
   FROM deskriptoren
  WHERE (deskriptoren.ebene = 4);


--
-- Name: s_05_s_01_view; Type: VIEW; Schema: stammdaten; Owner: -
--

CREATE VIEW s_05_s_01_view AS
 SELECT d1.s_xx AS s01,
    d2.s_xx AS s05
   FROM (deskriptoren d1
     JOIN deskriptoren d2 ON ((d2.vorgaenger = d1.id)))
  WHERE (d2.ebene = 5);


--
-- Name: s_05_view; Type: VIEW; Schema: stammdaten; Owner: -
--

CREATE VIEW s_05_view AS
 SELECT DISTINCT deskriptoren.s_xx AS s05,
    deskriptoren.bedeutung,
    deskriptoren.beschreibung,
    deskriptoren.sn
   FROM deskriptoren
  WHERE (deskriptoren.ebene = 5);


--
-- Name: s_06_s_01_view; Type: VIEW; Schema: stammdaten; Owner: -
--

CREATE VIEW s_06_s_01_view AS
 SELECT d1.s_xx AS s01,
    d2.s_xx AS s06
   FROM (deskriptoren d1
     JOIN deskriptoren d2 ON ((d2.vorgaenger = d1.id)))
  WHERE (d2.ebene = 6);


--
-- Name: s_06_view; Type: VIEW; Schema: stammdaten; Owner: -
--

CREATE VIEW s_06_view AS
 SELECT DISTINCT deskriptoren.s_xx AS s06,
    deskriptoren.bedeutung,
    deskriptoren.beschreibung,
    deskriptoren.sn
   FROM deskriptoren
  WHERE (deskriptoren.ebene = 6);


--
-- Name: s_07_s_01_view; Type: VIEW; Schema: stammdaten; Owner: -
--

CREATE VIEW s_07_s_01_view AS
 SELECT d1.s_xx AS s01,
    d2.s_xx AS s07
   FROM (deskriptoren d1
     JOIN deskriptoren d2 ON ((d2.vorgaenger = d1.id)))
  WHERE (d2.ebene = 7);


--
-- Name: s_07_view; Type: VIEW; Schema: stammdaten; Owner: -
--

CREATE VIEW s_07_view AS
 SELECT DISTINCT deskriptoren.s_xx AS s07,
    deskriptoren.bedeutung,
    deskriptoren.beschreibung,
    deskriptoren.sn
   FROM deskriptoren
  WHERE (deskriptoren.ebene = 7);


--
-- Name: s_08_s_01_view; Type: VIEW; Schema: stammdaten; Owner: -
--

CREATE VIEW s_08_s_01_view AS
 SELECT d1.s_xx AS s01,
    d2.s_xx AS s08
   FROM (deskriptoren d1
     JOIN deskriptoren d2 ON ((d2.vorgaenger = d1.id)))
  WHERE (d2.ebene = 8);


--
-- Name: s_08_view; Type: VIEW; Schema: stammdaten; Owner: -
--

CREATE VIEW s_08_view AS
 SELECT DISTINCT deskriptoren.s_xx AS s08,
    deskriptoren.bedeutung,
    deskriptoren.beschreibung,
    deskriptoren.sn
   FROM deskriptoren
  WHERE (deskriptoren.ebene = 8);


--
-- Name: s_09_s_01_view; Type: VIEW; Schema: stammdaten; Owner: -
--

CREATE VIEW s_09_s_01_view AS
 SELECT d1.s_xx AS s01,
    d2.s_xx AS s09
   FROM (deskriptoren d1
     JOIN deskriptoren d2 ON ((d2.vorgaenger = d1.id)))
  WHERE (d2.ebene = 9);


--
-- Name: s_09_view; Type: VIEW; Schema: stammdaten; Owner: -
--

CREATE VIEW s_09_view AS
 SELECT DISTINCT deskriptoren.s_xx AS s09,
    deskriptoren.bedeutung,
    deskriptoren.beschreibung,
    deskriptoren.sn
   FROM deskriptoren
  WHERE (deskriptoren.ebene = 9);


--
-- Name: s_10_s_01_view; Type: VIEW; Schema: stammdaten; Owner: -
--

CREATE VIEW s_10_s_01_view AS
 SELECT d1.s_xx AS s01,
    d2.s_xx AS s10
   FROM (deskriptoren d1
     JOIN deskriptoren d2 ON ((d2.vorgaenger = d1.id)))
  WHERE (d2.ebene = 10);


--
-- Name: s_10_view; Type: VIEW; Schema: stammdaten; Owner: -
--

CREATE VIEW s_10_view AS
 SELECT DISTINCT deskriptoren.s_xx AS s10,
    deskriptoren.bedeutung,
    deskriptoren.beschreibung,
    deskriptoren.sn
   FROM deskriptoren
  WHERE (deskriptoren.ebene = 10);


--
-- Name: s_11_s_01_view; Type: VIEW; Schema: stammdaten; Owner: -
--

CREATE VIEW s_11_s_01_view AS
 SELECT d1.s_xx AS s01,
    d2.s_xx AS s11
   FROM (deskriptoren d1
     JOIN deskriptoren d2 ON ((d2.vorgaenger = d1.id)))
  WHERE (d2.ebene = 11);


--
-- Name: s_11_view; Type: VIEW; Schema: stammdaten; Owner: -
--

CREATE VIEW s_11_view AS
 SELECT DISTINCT deskriptoren.s_xx AS s11,
    deskriptoren.bedeutung,
    deskriptoren.beschreibung,
    deskriptoren.sn
   FROM deskriptoren
  WHERE (deskriptoren.ebene = 11);


--
-- Name: s_12_s_01_view; Type: VIEW; Schema: stammdaten; Owner: -
--

CREATE VIEW s_12_s_01_view AS
 SELECT d1.s_xx AS s01,
    d2.s_xx AS s12
   FROM (deskriptoren d1
     JOIN deskriptoren d2 ON ((d2.vorgaenger = d1.id)))
  WHERE (d2.ebene = 12);


--
-- Name: s_12_view; Type: VIEW; Schema: stammdaten; Owner: -
--

CREATE VIEW s_12_view AS
 SELECT DISTINCT deskriptoren.s_xx AS s12,
    deskriptoren.bedeutung,
    deskriptoren.beschreibung,
    deskriptoren.sn
   FROM deskriptoren
  WHERE (deskriptoren.ebene = 12);


--
-- Name: staat; Type: TABLE; Schema: stammdaten; Owner: -; Tablespace:
--

CREATE TABLE staat (
    id integer NOT NULL,
    staat character varying(50) NOT NULL,
    hkl_id smallint NOT NULL,
    staat_iso character varying(2) NOT NULL,
    staat_kurz character varying(5),
    eu character(1) DEFAULT NULL::bpchar,
    koord_x_extern character varying(22),
    koord_y_extern character varying(22),
    kda_id integer
);


--
-- Name: staat_id_seq; Type: SEQUENCE; Schema: stammdaten; Owner: -
--

CREATE SEQUENCE staat_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: staat_id_seq; Type: SEQUENCE OWNED BY; Schema: stammdaten; Owner: -
--

ALTER SEQUENCE staat_id_seq OWNED BY staat.id;


--
-- Name: status_kombi; Type: TABLE; Schema: stammdaten; Owner: -; Tablespace:
--

CREATE TABLE status_kombi (
    id integer NOT NULL,
    stufe_id integer,
    wert_id integer
);


--
-- Name: status_reihenfolge; Type: TABLE; Schema: stammdaten; Owner: -; Tablespace:
--

CREATE TABLE status_reihenfolge (
    id integer NOT NULL,
    von_id integer,
    zu_id integer
);


--
-- Name: status_stufe; Type: TABLE; Schema: stammdaten; Owner: -; Tablespace:
--

CREATE TABLE status_stufe (
    id integer NOT NULL,
    stufe character varying(50)
);


--
-- Name: status_wert; Type: TABLE; Schema: stammdaten; Owner: -; Tablespace:
--

CREATE TABLE status_wert (
    id integer NOT NULL,
    wert character varying(50)
);


--
-- Name: umwelt; Type: TABLE; Schema: stammdaten; Owner: -; Tablespace:
--

CREATE TABLE umwelt (
    id character varying(3) NOT NULL,
    beschreibung character varying(300),
    umwelt_bereich character varying(80) NOT NULL,
    meh_id integer
);


--
-- Name: verwaltungseinheit; Type: TABLE; Schema: stammdaten; Owner: -; Tablespace:
--

CREATE TABLE verwaltungseinheit (
    id character varying(8) NOT NULL,
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

--
-- Name status_erreichbar; Type: VIEW; Schema: stammdaten; Owner: -;
--
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


--
-- Name: id; Type: DEFAULT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY auth ALTER COLUMN id SET DEFAULT nextval('auth_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY datenbasis ALTER COLUMN id SET DEFAULT nextval('datenbasis_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY datensatz_erzeuger ALTER COLUMN id SET DEFAULT nextval('datensatz_erzeuger_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY de_vg ALTER COLUMN id SET DEFAULT nextval('de_vg_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY deskriptoren ALTER COLUMN id SET DEFAULT nextval('deskriptoren_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY favorite ALTER COLUMN id SET DEFAULT nextval('favorite_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY filter ALTER COLUMN id SET DEFAULT nextval('filter_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY filter_value ALTER COLUMN id SET DEFAULT nextval('filter_value_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY koordinaten_art ALTER COLUMN id SET DEFAULT nextval('koordinaten_art_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY lada_user ALTER COLUMN id SET DEFAULT nextval('lada_user_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY mess_einheit ALTER COLUMN id SET DEFAULT nextval('mess_einheit_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY messgroesse ALTER COLUMN id SET DEFAULT nextval('messgroesse_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY messgroessen_gruppe ALTER COLUMN id SET DEFAULT nextval('messgroessen_gruppe_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY messprogramm_kategorie ALTER COLUMN id SET DEFAULT nextval('messprogramm_kategorie_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY ort ALTER COLUMN id SET DEFAULT nextval('ort_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY pflicht_messgroesse ALTER COLUMN id SET DEFAULT nextval('pflicht_messgroesse_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY probenart ALTER COLUMN id SET DEFAULT nextval('probenart_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY probenehmer ALTER COLUMN id SET DEFAULT nextval('probenehmer_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY query ALTER COLUMN id SET DEFAULT nextval('query_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY result ALTER COLUMN id SET DEFAULT nextval('result_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY staat ALTER COLUMN id SET DEFAULT nextval('staat_id_seq'::regclass);


--
-- Name: auth_lst_umw_pkey; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace:
--

ALTER TABLE ONLY auth_lst_umw
    ADD CONSTRAINT auth_lst_umw_pkey PRIMARY KEY (id);


--
-- Name: auth_pkey; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace:
--

ALTER TABLE ONLY auth
    ADD CONSTRAINT auth_pkey PRIMARY KEY (id);


--
-- Name: auth_role_pkey; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace:
--

ALTER TABLE ONLY auth_funktion
    ADD CONSTRAINT auth_role_pkey PRIMARY KEY (id);


--
-- Name: datenbasis_pkey; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace:
--

ALTER TABLE ONLY datenbasis
    ADD CONSTRAINT datenbasis_pkey PRIMARY KEY (id);


--
-- Name: datensatz_erzeuger_pkey; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace:
--

ALTER TABLE ONLY datensatz_erzeuger
    ADD CONSTRAINT datensatz_erzeuger_pkey PRIMARY KEY (id);


--
-- Name: de_vg_pkey; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace:
--

ALTER TABLE ONLY de_vg
    ADD CONSTRAINT de_vg_pkey PRIMARY KEY (id);


--
-- Name: deskriptor_umwelt_pkey; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace:
--

ALTER TABLE ONLY deskriptor_umwelt
    ADD CONSTRAINT deskriptor_umwelt_pkey PRIMARY KEY (id);


--
-- Name: favorite_pkey; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace: 
--

ALTER TABLE ONLY favorite
    ADD CONSTRAINT favorite_pkey PRIMARY KEY (id);


--
-- Name: filter_pkey; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace: 
--

ALTER TABLE ONLY filter
    ADD CONSTRAINT filter_pkey PRIMARY KEY (id);


--
-- Name: filter_value_pkey; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace: 
--

ALTER TABLE ONLY filter_value
    ADD CONSTRAINT filter_value_pkey PRIMARY KEY (id);


--
-- Name: koordinaten_art_pkey; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace: 
--

ALTER TABLE ONLY koordinaten_art
    ADD CONSTRAINT koordinaten_art_pkey PRIMARY KEY (id);


--
-- Name: lada_user_name_key; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace: 
--

ALTER TABLE ONLY lada_user
    ADD CONSTRAINT lada_user_name_key UNIQUE (name);


--
-- Name: lada_user_pkey; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace: 
--

ALTER TABLE ONLY lada_user
    ADD CONSTRAINT lada_user_pkey PRIMARY KEY (id);


--
-- Name: mess_einheit_pkey; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace: 
--

ALTER TABLE ONLY mess_einheit
    ADD CONSTRAINT mess_einheit_pkey PRIMARY KEY (id);


--
-- Name: mess_methode_pkey; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace:
--

ALTER TABLE ONLY mess_methode
    ADD CONSTRAINT mess_methode_pkey PRIMARY KEY (id);


--
-- Name: mess_stelle_pkey; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace:
--

ALTER TABLE ONLY mess_stelle
    ADD CONSTRAINT mess_stelle_pkey PRIMARY KEY (id);


--
-- Name: messgroesse_pkey; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace:
--

ALTER TABLE ONLY messgroesse
    ADD CONSTRAINT messgroesse_pkey PRIMARY KEY (id);


--
-- Name: messgroessen_gruppe_pkey; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace:
--

ALTER TABLE ONLY messgroessen_gruppe
    ADD CONSTRAINT messgroessen_gruppe_pkey PRIMARY KEY (id);


--
-- Name: messprogramm_kategorie_pkey; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace:
--

ALTER TABLE ONLY messprogramm_kategorie
    ADD CONSTRAINT messprogramm_kategorie_pkey PRIMARY KEY (id);


--
-- Name: mg_grp_pkey; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace:
--

ALTER TABLE ONLY mg_grp
    ADD CONSTRAINT mg_grp_pkey PRIMARY KEY (messgroessengruppe_id, messgroesse_id);


--
-- Name: mmt_messgroesse_grp_pkey; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace:
--

ALTER TABLE ONLY mmt_messgroesse_grp
    ADD CONSTRAINT mmt_messgroesse_grp_pkey PRIMARY KEY (messgroessengruppe_id, mmt_id);


--
-- Name: netz_betreiber_pkey; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace:
--

ALTER TABLE ONLY netz_betreiber
    ADD CONSTRAINT netz_betreiber_pkey PRIMARY KEY (id);


--
-- Name: ort_pkey; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace:
--

ALTER TABLE ONLY ort
    ADD CONSTRAINT ort_pkey PRIMARY KEY (id);


--
-- Name: ort_typ_pkey; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace:
--

ALTER TABLE ONLY ort_typ
    ADD CONSTRAINT ort_typ_pkey PRIMARY KEY (id);


--
-- Name: ortszuordnung_typ_pkey; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace:
--

ALTER TABLE ONLY ortszuordnung_typ
    ADD CONSTRAINT ortszuordnung_typ_pkey PRIMARY KEY (id);


--
-- Name: pflicht_messgroesse_pkey; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace:
--

ALTER TABLE ONLY pflicht_messgroesse
    ADD CONSTRAINT pflicht_messgroesse_pkey PRIMARY KEY (id);


--
-- Name: pk_deskriptoren; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace:
--

ALTER TABLE ONLY deskriptoren
    ADD CONSTRAINT pk_deskriptoren PRIMARY KEY (id);


--
-- Name: proben_zusatz_eudf_keyword_key; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace:
--

ALTER TABLE ONLY proben_zusatz
    ADD CONSTRAINT proben_zusatz_eudf_keyword_key UNIQUE (eudf_keyword);


--
-- Name: proben_zusatz_pkey; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace:
--

ALTER TABLE ONLY proben_zusatz
    ADD CONSTRAINT proben_zusatz_pkey PRIMARY KEY (id);


--
-- Name: probenart_pkey; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace:
--

ALTER TABLE ONLY probenart
    ADD CONSTRAINT probenart_pkey PRIMARY KEY (id);


--
-- Name: probenehmer_pkey; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace:
--

ALTER TABLE ONLY probenehmer
    ADD CONSTRAINT probenehmer_pkey PRIMARY KEY (id);


--
-- Name: query_pkey; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace: 
--

ALTER TABLE ONLY query
    ADD CONSTRAINT query_pkey PRIMARY KEY (id);


--
-- Name: result_pkey; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace: 
--

ALTER TABLE ONLY result
    ADD CONSTRAINT result_pkey PRIMARY KEY (id);


--
-- Name: staat_pkey; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace: 
--

ALTER TABLE ONLY staat
    ADD CONSTRAINT staat_pkey PRIMARY KEY (id);


--
-- Name: status_kombi_pkey; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace:
--

ALTER TABLE ONLY status_kombi
    ADD CONSTRAINT status_kombi_pkey PRIMARY KEY (id);


--
-- Name: status_reihenfolge_pkey; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace:
--

ALTER TABLE ONLY status_reihenfolge
    ADD CONSTRAINT status_reihenfolge_pkey PRIMARY KEY (id);


--
-- Name: status_stufe_pkey; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace:
--

ALTER TABLE ONLY status_stufe
    ADD CONSTRAINT status_stufe_pkey PRIMARY KEY (id);


--
-- Name: status_wert_pkey; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace:
--

ALTER TABLE ONLY status_wert
    ADD CONSTRAINT status_wert_pkey PRIMARY KEY (id);


--
-- Name: umwelt_pkey; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace:
--

ALTER TABLE ONLY umwelt
    ADD CONSTRAINT umwelt_pkey PRIMARY KEY (id);


--
-- Name: umwelt_umwelt_bereich_key; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace:
--

ALTER TABLE ONLY umwelt
    ADD CONSTRAINT umwelt_umwelt_bereich_key UNIQUE (umwelt_bereich);


--
-- Name: verwaltungseinheit_pkey; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace:
--

ALTER TABLE ONLY verwaltungseinheit
    ADD CONSTRAINT verwaltungseinheit_pkey PRIMARY KEY (id);


--
-- Name: de_vg_geom_gist; Type: INDEX; Schema: stammdaten; Owner: -; Tablespace:
--

CREATE INDEX de_vg_geom_gist ON de_vg USING gist (geom);


--
-- Name: fts_stauts_kooin10001; Type: INDEX; Schema: stammdaten; Owner: -; Tablespace:
--

CREATE INDEX fts_stauts_kooin10001 ON staat USING btree (kda_id);


--
-- Name: auth_funktion_fkey; Type: FK CONSTRAINT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY auth
    ADD CONSTRAINT auth_funktion_fkey FOREIGN KEY (funktion_id) REFERENCES auth_funktion(id);


--
-- Name: auth_labor_fkey; Type: FK CONSTRAINT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY auth
    ADD CONSTRAINT auth_labor_fkey FOREIGN KEY (labor_mst_id) REFERENCES mess_stelle(id);


--
-- Name: auth_lst_umw_lst_fkey; Type: FK CONSTRAINT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY auth_lst_umw
    ADD CONSTRAINT auth_lst_umw_lst_fkey FOREIGN KEY (lst_id) REFERENCES mess_stelle(id);


--
-- Name: auth_lst_umw_umw_fkey; Type: FK CONSTRAINT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY auth_lst_umw
    ADD CONSTRAINT auth_lst_umw_umw_fkey FOREIGN KEY (umw_id) REFERENCES umwelt(id);


--
-- Name: auth_mst_id_fkey; Type: FK CONSTRAINT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY auth
    ADD CONSTRAINT auth_mst_id_fkey FOREIGN KEY (mst_id) REFERENCES mess_stelle(id);


--
-- Name: auth_netzbetreiber_id_fkey; Type: FK CONSTRAINT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY auth
    ADD CONSTRAINT auth_netzbetreiber_id_fkey FOREIGN KEY (netzbetreiber_id) REFERENCES netz_betreiber(id);


--
-- Name: datensatz_erzeuger_mst_id_fkey1; Type: FK CONSTRAINT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY datensatz_erzeuger
    ADD CONSTRAINT datensatz_erzeuger_mst_id_fkey1 FOREIGN KEY (mst_id) REFERENCES mess_stelle(id);


--
-- Name: datensatz_erzeuger_netzbetreiber_id_fkey; Type: FK CONSTRAINT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY datensatz_erzeuger
    ADD CONSTRAINT datensatz_erzeuger_netzbetreiber_id_fkey FOREIGN KEY (netzbetreiber_id) REFERENCES netz_betreiber(id);


--
-- Name: favorite_query_id_fkey; Type: FK CONSTRAINT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY favorite
    ADD CONSTRAINT favorite_query_id_fkey FOREIGN KEY (query_id) REFERENCES query(id);


--
-- Name: favorite_user_id_fkey; Type: FK CONSTRAINT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY favorite
    ADD CONSTRAINT favorite_user_id_fkey FOREIGN KEY (user_id) REFERENCES lada_user(id);


--
-- Name: filter_query_id_fkey; Type: FK CONSTRAINT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY filter
    ADD CONSTRAINT filter_query_id_fkey FOREIGN KEY (query_id) REFERENCES query(id);


--
-- Name: filter_value_filter_id_fkey; Type: FK CONSTRAINT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY filter_value
    ADD CONSTRAINT filter_value_filter_id_fkey FOREIGN KEY (filter_id) REFERENCES filter(id);


--
-- Name: filter_value_query_id_fkey; Type: FK CONSTRAINT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY filter_value
    ADD CONSTRAINT filter_value_query_id_fkey FOREIGN KEY (query_id) REFERENCES query(id);


--
-- Name: filter_value_user_id_fkey; Type: FK CONSTRAINT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY filter_value
    ADD CONSTRAINT filter_value_user_id_fkey FOREIGN KEY (user_id) REFERENCES lada_user(id);


--
-- Name: fk_deskriptoren_vorgaenger; Type: FK CONSTRAINT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY deskriptoren
    ADD CONSTRAINT fk_deskriptoren_vorgaenger FOREIGN KEY (vorgaenger) REFERENCES deskriptoren(id);


--
-- Name: messprogramm_kategorie_netzbetreiber_id_fkey; Type: FK CONSTRAINT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY messprogramm_kategorie
    ADD CONSTRAINT messprogramm_kategorie_netzbetreiber_id_fkey FOREIGN KEY (netzbetreiber_id) REFERENCES netz_betreiber(id);


--
-- Name: ort_anlage_fkey; Type: FK CONSTRAINT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY ort
    ADD CONSTRAINT ort_anlage_fkey FOREIGN KEY (anlage_id) REFERENCES ort(id);


--
-- Name: ort_gem_id_fkey; Type: FK CONSTRAINT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY ort
    ADD CONSTRAINT ort_gem_id_fkey FOREIGN KEY (gem_id) REFERENCES verwaltungseinheit(id);


--
-- Name: ort_kda_id_fkey; Type: FK CONSTRAINT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY ort
    ADD CONSTRAINT ort_kda_id_fkey FOREIGN KEY (kda_id) REFERENCES koordinaten_art(id);


--
-- Name: ort_netzbetreiber_id_fkey; Type: FK CONSTRAINT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY ort
    ADD CONSTRAINT ort_netzbetreiber_id_fkey FOREIGN KEY (netzbetreiber_id) REFERENCES netz_betreiber(id);


--
-- Name: ort_ort_typ_fkey; Type: FK CONSTRAINT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY ort
    ADD CONSTRAINT ort_ort_typ_fkey FOREIGN KEY (ort_typ) REFERENCES ort_typ(id);


--
-- Name: ort_oz_fkey; Type: FK CONSTRAINT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY ort
    ADD CONSTRAINT ort_oz_fkey FOREIGN KEY (oz_id) REFERENCES ort(id);


--
-- Name: ort_staat_id_fkey; Type: FK CONSTRAINT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY ort
    ADD CONSTRAINT ort_staat_id_fkey FOREIGN KEY (staat_id) REFERENCES staat(id);


--
-- Name: pflicht_messgroesse_datenbasis_id_fkey; Type: FK CONSTRAINT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY pflicht_messgroesse
    ADD CONSTRAINT pflicht_messgroesse_datenbasis_id_fkey FOREIGN KEY (datenbasis_id) REFERENCES datenbasis(id);


--
-- Name: pflicht_messgroesse_mmt_id_fkey; Type: FK CONSTRAINT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY pflicht_messgroesse
    ADD CONSTRAINT pflicht_messgroesse_mmt_id_fkey FOREIGN KEY (mmt_id) REFERENCES mess_methode(id);


--
-- Name: pflicht_messgroesse_umw_id_fkey; Type: FK CONSTRAINT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY pflicht_messgroesse
    ADD CONSTRAINT pflicht_messgroesse_umw_id_fkey FOREIGN KEY (umw_id) REFERENCES umwelt(id);


--
-- Name: proben_zusatz_meh_id_fkey; Type: FK CONSTRAINT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY proben_zusatz
    ADD CONSTRAINT proben_zusatz_meh_id_fkey FOREIGN KEY (meh_id) REFERENCES mess_einheit(id);


--
-- Name: probenehmer_netzbetreiber_id_fkey; Type: FK CONSTRAINT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY probenehmer
    ADD CONSTRAINT probenehmer_netzbetreiber_id_fkey FOREIGN KEY (netzbetreiber_id) REFERENCES netz_betreiber(id);


--
-- Name: result_query_id_fkey; Type: FK CONSTRAINT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY result
    ADD CONSTRAINT result_query_id_fkey FOREIGN KEY (query_id) REFERENCES query(id);


--
-- Name: staat_kda_id_fkey; Type: FK CONSTRAINT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY staat
    ADD CONSTRAINT staat_kda_id_fkey FOREIGN KEY (kda_id) REFERENCES koordinaten_art(id);


--
-- Name: status_kombi_stufe_id_fkey; Type: FK CONSTRAINT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY status_kombi
    ADD CONSTRAINT status_kombi_stufe_id_fkey FOREIGN KEY (stufe_id) REFERENCES status_stufe(id);


--
-- Name: status_kombi_wert_id_fkey; Type: FK CONSTRAINT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY status_kombi
    ADD CONSTRAINT status_kombi_wert_id_fkey FOREIGN KEY (wert_id) REFERENCES status_wert(id);


--
-- Name: status_reihenfolge_von_id_fkey; Type: FK CONSTRAINT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY status_reihenfolge
    ADD CONSTRAINT status_reihenfolge_von_id_fkey FOREIGN KEY (von_id) REFERENCES status_kombi(id);


--
-- Name: status_reihenfolge_zu_id_fkey; Type: FK CONSTRAINT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY status_reihenfolge
    ADD CONSTRAINT status_reihenfolge_zu_id_fkey FOREIGN KEY (zu_id) REFERENCES status_kombi(id);


--
-- Name: umwelt_meh_id_fkey; Type: FK CONSTRAINT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY umwelt
    ADD CONSTRAINT umwelt_meh_id_fkey FOREIGN KEY (meh_id) REFERENCES mess_einheit(id);


--
-- Name: verwaltungseinheit_kda_id_fkey; Type: FK CONSTRAINT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY verwaltungseinheit
    ADD CONSTRAINT verwaltungseinheit_kda_id_fkey FOREIGN KEY (kda_id) REFERENCES koordinaten_art(id);


COMMIT;
