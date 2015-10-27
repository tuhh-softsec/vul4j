--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: bund; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA bund;


--
-- Name: land; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA land;


--
-- Name: stammdaten; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA stammdaten;


--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


--
-- Name: postgis; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS postgis WITH SCHEMA public;


--
-- Name: EXTENSION postgis; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION postgis IS 'PostGIS geometry, geography, and raster spatial types and functions';


SET search_path = bund, pg_catalog;

--
-- Name: is_kommentar_unique(); Type: FUNCTION; Schema: bund; Owner: -
--

CREATE FUNCTION is_kommentar_unique() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    BEGIN
        -- Check that empname and salary are given
        IF NEW.id IN (SELECT id from bund.kommentar) THEN
            RAISE EXCEPTION 'Key (id)=(%) already present in bund.messung', NEW.id;
        ELSE
            RETURN NEW;
        END IF;
    END;
$$;


--
-- Name: is_messung_unique(); Type: FUNCTION; Schema: bund; Owner: -
--

CREATE FUNCTION is_messung_unique() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    BEGIN
        -- Check that empname and salary are given
        IF NEW.id IN (SELECT id from bund.messung) THEN
            RAISE EXCEPTION 'Key (id)=(%) already present in bund.messung', NEW.id;
        ELSE
            RETURN NEW;
        END IF;
    END;
$$;


--
-- Name: is_messwert_unique(); Type: FUNCTION; Schema: bund; Owner: -
--

CREATE FUNCTION is_messwert_unique() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    BEGIN
        -- Check that empname and salary are given
        IF NEW.id IN (SELECT id from bund.messwert) THEN
            RAISE EXCEPTION 'Key (id)=(%) already present in bund.messwert', NEW.id;
        ELSE
            RETURN NEW;
        END IF;
    END;
$$;


--
-- Name: is_ort_unique(); Type: FUNCTION; Schema: bund; Owner: -
--

CREATE FUNCTION is_ort_unique() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    BEGIN
        -- Check that empname and salary are given
        IF NEW.id IN (SELECT id from bund.ort) THEN
            RAISE EXCEPTION 'Key (id)=(%) already present in bund.ort', NEW.id;
        ELSE
            RETURN NEW;
        END IF;
    END;
$$;


--
-- Name: is_probe_unique(); Type: FUNCTION; Schema: bund; Owner: -
--

CREATE FUNCTION is_probe_unique() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    BEGIN
        -- Check that empname and salary are given
        IF NEW.id IN (SELECT id from bund.probe) THEN
            RAISE EXCEPTION 'Key (id)=(%) already present in bund.probe', NEW.id;
        ELSE
            RETURN NEW;
        END IF;
    END;
$$;


--
-- Name: is_status_unique(); Type: FUNCTION; Schema: bund; Owner: -
--

CREATE FUNCTION is_status_unique() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    BEGIN
        -- Check that empname and salary are given
        IF NEW.id IN (SELECT id from bund.status) THEN
            RAISE EXCEPTION 'Key (id)=(%) already present in bund.status', NEW.id;
        ELSE
            RETURN NEW;
        END IF;
    END;
$$;


--
-- Name: is_zusatz_wert_unique(); Type: FUNCTION; Schema: bund; Owner: -
--

CREATE FUNCTION is_zusatz_wert_unique() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    BEGIN
        -- Check that empname and salary are given
        IF NEW.id IN (SELECT id from bund.zusatz_wert) THEN
            RAISE EXCEPTION 'Key (id)=(%) already present in bund.zusatz_wert', NEW.id;
        ELSE
            RETURN NEW;
        END IF;
    END;
$$;


SET search_path = land, pg_catalog;

--
-- Name: is_probe_fertig(integer); Type: FUNCTION; Schema: land; Owner: -
--

CREATE FUNCTION is_probe_fertig(i1 integer) RETURNS boolean
    LANGUAGE plpgsql STABLE SECURITY DEFINER
    AS $_$
DECLARE result BOOLEAN;
BEGIN
        SELECT (count(id) > 0) INTO result from land.messung where probe_id = $1 and fertig = TRUE;
        RETURN result;
END;
$_$;


--
-- Name: update_time_messung(); Type: FUNCTION; Schema: land; Owner: -
--

CREATE FUNCTION update_time_messung() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    BEGIN
        RAISE NOTICE 'messung is %',NEW.id;
        NEW.tree_modified = now();
        UPDATE land.messwert SET tree_modified = now() WHERE messungs_id = NEW.id;
        UPDATE land.status SET tree_modified = now() WHERE messungs_id = NEW.id;
        RETURN NEW;
    END;
$$;


--
-- Name: update_time_messwert(); Type: FUNCTION; Schema: land; Owner: -
--

CREATE FUNCTION update_time_messwert() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    BEGIN
        NEW.tree_modified = now();
        RETURN NEW;
    END;
$$;


--
-- Name: update_time_ort(); Type: FUNCTION; Schema: land; Owner: -
--

CREATE FUNCTION update_time_ort() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    BEGIN
        NEW.tree_modified = now();
        RETURN NEW;
    END;
$$;


--
-- Name: update_time_probe(); Type: FUNCTION; Schema: land; Owner: -
--

CREATE FUNCTION update_time_probe() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    BEGIN
        RAISE NOTICE 'probe is %',NEW.id;
        NEW.tree_modified = now();
        RAISE NOTICE 'updating other rows';
        UPDATE land.messung SET tree_modified = now() WHERE probe_id = NEW.id;
        UPDATE land.ort SET tree_modified = now() WHERE probe_id = NEW.id;
        UPDATE land.zusatz_wert SET tree_modified = now() WHERE probe_id = NEW.id;
        RETURN NEW;
    END;
$$;


--
-- Name: update_time_status(); Type: FUNCTION; Schema: land; Owner: -
--

CREATE FUNCTION update_time_status() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    BEGIN
        NEW.tree_modified = now();
        RETURN NEW;
    END;
$$;


--
-- Name: update_time_zusatzwert(); Type: FUNCTION; Schema: land; Owner: -
--

CREATE FUNCTION update_time_zusatzwert() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    BEGIN
        NEW.tree_modified = now();
        RETURN NEW;
    END;
$$;


SET search_path = stammdaten, pg_catalog;

--
-- Name: get_media_from_media_desk(character varying); Type: FUNCTION; Schema: stammdaten; Owner: -
--

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


SET search_path = bund, pg_catalog;

--
-- Name: kommentar_id_seq; Type: SEQUENCE; Schema: bund; Owner: -
--

CREATE SEQUENCE kommentar_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: kommentar; Type: TABLE; Schema: bund; Owner: -; Tablespace: 
--

CREATE TABLE kommentar (
    id integer DEFAULT nextval('kommentar_id_seq'::regclass) NOT NULL,
    erzeuger character varying(5) NOT NULL,
    datum timestamp without time zone DEFAULT now(),
    text character varying(1024)
);


--
-- Name: kommentar_m; Type: TABLE; Schema: bund; Owner: -; Tablespace: 
--

CREATE TABLE kommentar_m (
    messungs_id integer NOT NULL
)
INHERITS (kommentar);


--
-- Name: kommentar_p; Type: TABLE; Schema: bund; Owner: -; Tablespace: 
--

CREATE TABLE kommentar_p (
    probe_id integer NOT NULL
)
INHERITS (kommentar);


--
-- Name: list; Type: TABLE; Schema: bund; Owner: -; Tablespace: 
--

CREATE TABLE list (
    id integer NOT NULL,
    typ character(1) NOT NULL,
    bezeichnung character varying(20) NOT NULL,
    beschreibuing character varying(512),
    letzte_aenderung timestamp with time zone NOT NULL,
    gueltig_bis timestamp with time zone
);


--
-- Name: list_id_seq; Type: SEQUENCE; Schema: bund; Owner: -
--

CREATE SEQUENCE list_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: list_id_seq; Type: SEQUENCE OWNED BY; Schema: bund; Owner: -
--

ALTER SEQUENCE list_id_seq OWNED BY list.id;


--
-- Name: list_zuordnung; Type: TABLE; Schema: bund; Owner: -; Tablespace: 
--

CREATE TABLE list_zuordnung (
    list_id integer NOT NULL,
    probe_id integer
);


--
-- Name: messung_id_seq; Type: SEQUENCE; Schema: bund; Owner: -
--

CREATE SEQUENCE messung_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: messung; Type: TABLE; Schema: bund; Owner: -; Tablespace: 
--

CREATE TABLE messung (
    id integer DEFAULT nextval('messung_id_seq'::regclass) NOT NULL,
    probe_id integer NOT NULL,
    nebenproben_nr character varying(10),
    mmt_id character varying(2) NOT NULL,
    messdauer integer,
    messzeitpunkt timestamp with time zone,
    fertig boolean DEFAULT false NOT NULL,
    letzte_aenderung timestamp without time zone DEFAULT now()
);


--
-- Name: messung_messung_id_alt_seq; Type: SEQUENCE; Schema: bund; Owner: -
--

CREATE SEQUENCE messung_messung_id_alt_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: messung_messungs_id_seq; Type: SEQUENCE; Schema: bund; Owner: -
--

CREATE SEQUENCE messung_messungs_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: messwert_id_seq; Type: SEQUENCE; Schema: bund; Owner: -
--

CREATE SEQUENCE messwert_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: messwert; Type: TABLE; Schema: bund; Owner: -; Tablespace: 
--

CREATE TABLE messwert (
    id integer DEFAULT nextval('messwert_id_seq'::regclass) NOT NULL,
    messungs_id integer NOT NULL,
    messgroesse_id integer NOT NULL,
    messwert_nwg character varying(1),
    messwert real NOT NULL,
    messfehler real,
    nwg_zu_messwert real,
    meh_id smallint NOT NULL,
    grenzwertueberschreitung boolean DEFAULT false,
    letzte_aenderung timestamp without time zone DEFAULT now()
);


--
-- Name: ort_id_seq; Type: SEQUENCE; Schema: bund; Owner: -
--

CREATE SEQUENCE ort_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: ort; Type: TABLE; Schema: bund; Owner: -; Tablespace: 
--

CREATE TABLE ort (
    id integer DEFAULT nextval('ort_id_seq'::regclass) NOT NULL,
    probe_id integer NOT NULL,
    ort_id bigint NOT NULL,
    orts_typ character varying(1),
    ortszusatztext character varying(100),
    letzte_aenderung timestamp without time zone DEFAULT now()
);


--
-- Name: COLUMN ort.orts_typ; Type: COMMENT; Schema: bund; Owner: -
--

COMMENT ON COLUMN ort.orts_typ IS 'E = Entnahmeport, U = Ursprungsort, Z = Ortszusatz';


--
-- Name: probe_id_seq; Type: SEQUENCE; Schema: bund; Owner: -
--

CREATE SEQUENCE probe_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: probe; Type: TABLE; Schema: bund; Owner: -; Tablespace: 
--

CREATE TABLE probe (
    id integer DEFAULT nextval('probe_id_seq'::regclass) NOT NULL,
    test boolean DEFAULT false NOT NULL,
    netzbetreiber_id character varying(2),
    mst_id character varying(5),
    hauptproben_nr character varying(20),
    datenbasis_id smallint,
    ba_id character varying(1),
    probenart_id smallint NOT NULL,
    media_desk character varying(100),
    media character varying(100),
    umw_id character varying(3),
    probeentnahme_beginn timestamp with time zone,
    probeentnahme_ende timestamp with time zone,
    mittelungsdauer bigint,
    letzte_aenderung timestamp without time zone DEFAULT now()
);


--
-- Name: COLUMN probe.id; Type: COMMENT; Schema: bund; Owner: -
--

COMMENT ON COLUMN probe.id IS 'interner Probenschlüssel';


--
-- Name: COLUMN probe.test; Type: COMMENT; Schema: bund; Owner: -
--

COMMENT ON COLUMN probe.test IS 'Ist Testdatensatz?';


--
-- Name: COLUMN probe.mst_id; Type: COMMENT; Schema: bund; Owner: -
--

COMMENT ON COLUMN probe.mst_id IS 'ID für Messstelle/Messlabor';


--
-- Name: COLUMN probe.hauptproben_nr; Type: COMMENT; Schema: bund; Owner: -
--

COMMENT ON COLUMN probe.hauptproben_nr IS 'externer Probensclüssel';


--
-- Name: COLUMN probe.ba_id; Type: COMMENT; Schema: bund; Owner: -
--

COMMENT ON COLUMN probe.ba_id IS 'ID der Betriebsart (normal/Routine oder Störfall/intensiv)';


--
-- Name: COLUMN probe.probenart_id; Type: COMMENT; Schema: bund; Owner: -
--

COMMENT ON COLUMN probe.probenart_id IS 'ID der Probenart(Einzel-, Sammel-, Misch- ...Probe)';


--
-- Name: COLUMN probe.media_desk; Type: COMMENT; Schema: bund; Owner: -
--

COMMENT ON COLUMN probe.media_desk IS 'Mediencodierung (Deskriptoren oder ADV-Codierung)';


--
-- Name: COLUMN probe.media; Type: COMMENT; Schema: bund; Owner: -
--

COMMENT ON COLUMN probe.media IS 'dekodierte Medienbezeichnung (aus media_desk abgeleitet)';


--
-- Name: COLUMN probe.umw_id; Type: COMMENT; Schema: bund; Owner: -
--

COMMENT ON COLUMN probe.umw_id IS 'ID für Umweltbereich';


--
-- Name: probe_probe_id_seq; Type: SEQUENCE; Schema: bund; Owner: -
--

CREATE SEQUENCE probe_probe_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: status_id_seq; Type: SEQUENCE; Schema: bund; Owner: -
--

CREATE SEQUENCE status_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: status; Type: TABLE; Schema: bund; Owner: -; Tablespace: 
--

CREATE TABLE status (
    id integer DEFAULT nextval('status_id_seq'::regclass) NOT NULL,
    messungs_id integer NOT NULL,
    erzeuger character varying(5) NOT NULL,
    status smallint,
    s_datum timestamp with time zone NOT NULL,
    s_kommentar character varying(1024)
);


--
-- Name: zusatz_wert_id_seq; Type: SEQUENCE; Schema: bund; Owner: -
--

CREATE SEQUENCE zusatz_wert_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: zusatz_wert; Type: TABLE; Schema: bund; Owner: -; Tablespace: 
--

CREATE TABLE zusatz_wert (
    id integer DEFAULT nextval('zusatz_wert_id_seq'::regclass) NOT NULL,
    probe_id integer NOT NULL,
    pzs_id character varying(3) NOT NULL,
    messwert_pzs real,
    messfehler real,
    letzte_aenderung timestamp without time zone DEFAULT now(),
    nwg_zu_messwert real
);


SET search_path = land, pg_catalog;

--
-- Name: kommentar_m; Type: TABLE; Schema: land; Owner: -; Tablespace: 
--

CREATE TABLE kommentar_m (
)
INHERITS (bund.kommentar_m);


--
-- Name: kommentar_p; Type: TABLE; Schema: land; Owner: -; Tablespace: 
--

CREATE TABLE kommentar_p (
)
INHERITS (bund.kommentar_p);


--
-- Name: messprogramm; Type: TABLE; Schema: land; Owner: -; Tablespace: 
--

CREATE TABLE messprogramm (
    id integer NOT NULL,
    name character varying(256),
    test boolean DEFAULT false NOT NULL,
    netzbetreiber_id character varying(2) NOT NULL,
    mst_id character varying(5) NOT NULL,
    datenbasis_id integer NOT NULL,
    ba_id character varying(1),
    gem_id character varying(8),
    ort_id integer,
    media_desk character varying(100),
    umw_id character varying(3),
    probenart_id integer NOT NULL,
    probenintervall character varying(2),
    teilintervall_von integer,
    teilintervall_bis integer,
    intervall_offset integer,
    gueltig_von integer,
    gueltig_bis integer,
    probe_nehmer_id integer,
    probe_kommentar character varying(80),
    letzte_aenderung timestamp without time zone DEFAULT now() NOT NULL
);


--
-- Name: COLUMN messprogramm.media_desk; Type: COMMENT; Schema: land; Owner: -
--

COMMENT ON COLUMN messprogramm.media_desk IS 'dekodierte Medienbezeichnung (aus media_desk abgeleitet)';


--
-- Name: messprogramm_id_seq; Type: SEQUENCE; Schema: land; Owner: -
--

CREATE SEQUENCE messprogramm_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: messprogramm_id_seq; Type: SEQUENCE OWNED BY; Schema: land; Owner: -
--

ALTER SEQUENCE messprogramm_id_seq OWNED BY messprogramm.id;


--
-- Name: messprogramm_mmt; Type: TABLE; Schema: land; Owner: -; Tablespace: 
--

CREATE TABLE messprogramm_mmt (
    id integer NOT NULL,
    messprogramm_id integer NOT NULL,
    mmt_id character varying(2) NOT NULL,
    messgroessen integer[],
    letzte_aenderung timestamp without time zone DEFAULT now()
);


--
-- Name: messprogramm_mmt_id_seq; Type: SEQUENCE; Schema: land; Owner: -
--

CREATE SEQUENCE messprogramm_mmt_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: messprogramm_mmt_id_seq; Type: SEQUENCE OWNED BY; Schema: land; Owner: -
--

ALTER SEQUENCE messprogramm_mmt_id_seq OWNED BY messprogramm_mmt.id;


--
-- Name: messung; Type: TABLE; Schema: land; Owner: -; Tablespace: 
--

CREATE TABLE messung (
    geplant boolean DEFAULT false NOT NULL,
    tree_modified timestamp without time zone DEFAULT now()
)
INHERITS (bund.messung);


--
-- Name: messung_translation; Type: TABLE; Schema: land; Owner: -; Tablespace: 
--

CREATE TABLE messung_translation (
    id integer NOT NULL,
    messungs_id integer NOT NULL,
    messungs_id_alt integer DEFAULT nextval('bund.messung_messung_id_alt_seq'::regclass) NOT NULL
);


--
-- Name: messung_translation_id_seq; Type: SEQUENCE; Schema: land; Owner: -
--

CREATE SEQUENCE messung_translation_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: messung_translation_id_seq; Type: SEQUENCE OWNED BY; Schema: land; Owner: -
--

ALTER SEQUENCE messung_translation_id_seq OWNED BY messung_translation.id;


--
-- Name: messwert; Type: TABLE; Schema: land; Owner: -; Tablespace: 
--

CREATE TABLE messwert (
    tree_modified timestamp without time zone DEFAULT now()
)
INHERITS (bund.messwert);


--
-- Name: ort; Type: TABLE; Schema: land; Owner: -; Tablespace: 
--

CREATE TABLE ort (
    tree_modified timestamp without time zone DEFAULT now()
)
INHERITS (bund.ort);


--
-- Name: probe; Type: TABLE; Schema: land; Owner: -; Tablespace: 
--

CREATE TABLE probe (
    erzeuger_id integer,
    probe_nehmer_id integer,
    mp_kat character(1),
    mpl_id character varying(3),
    mpr_id integer,
    solldatum_beginn timestamp without time zone,
    solldatum_ende timestamp without time zone,
    tree_modified timestamp without time zone DEFAULT now()
)
INHERITS (bund.probe);


--
-- Name: probe_translation; Type: TABLE; Schema: land; Owner: -; Tablespace: 
--

CREATE TABLE probe_translation (
    id integer NOT NULL,
    probe_id integer NOT NULL,
    probe_id_alt character varying(20) DEFAULT (('sss'::text || lpad(((nextval('bund.probe_probe_id_seq'::regclass))::character varying)::text, 12, '0'::text)) || 'Y'::text) NOT NULL
);


--
-- Name: probe_translation_id_seq; Type: SEQUENCE; Schema: land; Owner: -
--

CREATE SEQUENCE probe_translation_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: probe_translation_id_seq; Type: SEQUENCE OWNED BY; Schema: land; Owner: -
--

ALTER SEQUENCE probe_translation_id_seq OWNED BY probe_translation.id;


--
-- Name: status; Type: TABLE; Schema: land; Owner: -; Tablespace: 
--

CREATE TABLE status (
    tree_modified timestamp without time zone DEFAULT now()
)
INHERITS (bund.status);


--
-- Name: zusatz_wert; Type: TABLE; Schema: land; Owner: -; Tablespace: 
--

CREATE TABLE zusatz_wert (
    tree_modified timestamp without time zone DEFAULT now()
)
INHERITS (bund.zusatz_wert);


SET search_path = stammdaten, pg_catalog;

--
-- Name: auth; Type: TABLE; Schema: stammdaten; Owner: -; Tablespace: 
--

CREATE TABLE auth (
    id integer NOT NULL,
    ldap_group character varying(40) NOT NULL,
    netzbetreiber_id character varying(2),
    mst_id character varying(5)
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
    o_typ character varying(1),
    netzbetreiber_id character varying(2),
    bezeichnung character varying(10),
    beschreibung character varying(100),
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
    shape public.geometry(MultiPolygon,4326)
);


--
-- Name: COLUMN ort.o_typ; Type: COMMENT; Schema: stammdaten; Owner: -
--

COMMENT ON COLUMN ort.o_typ IS 'D = dynamischer Messpunkt (nicht vordefiniert)
V = vordefinierter Messpunkt
R = REI-Messpunkt
S = Station
Z = Ortzszusatz';


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


SET search_path = bund, pg_catalog;

--
-- Name: id; Type: DEFAULT; Schema: bund; Owner: -
--

ALTER TABLE ONLY kommentar_m ALTER COLUMN id SET DEFAULT nextval('kommentar_id_seq'::regclass);


--
-- Name: datum; Type: DEFAULT; Schema: bund; Owner: -
--

ALTER TABLE ONLY kommentar_m ALTER COLUMN datum SET DEFAULT now();


--
-- Name: id; Type: DEFAULT; Schema: bund; Owner: -
--

ALTER TABLE ONLY kommentar_p ALTER COLUMN id SET DEFAULT nextval('kommentar_id_seq'::regclass);


--
-- Name: datum; Type: DEFAULT; Schema: bund; Owner: -
--

ALTER TABLE ONLY kommentar_p ALTER COLUMN datum SET DEFAULT now();


--
-- Name: id; Type: DEFAULT; Schema: bund; Owner: -
--

ALTER TABLE ONLY list ALTER COLUMN id SET DEFAULT nextval('list_id_seq'::regclass);


SET search_path = land, pg_catalog;

--
-- Name: id; Type: DEFAULT; Schema: land; Owner: -
--

ALTER TABLE ONLY kommentar_m ALTER COLUMN id SET DEFAULT nextval('bund.kommentar_id_seq'::regclass);


--
-- Name: datum; Type: DEFAULT; Schema: land; Owner: -
--

ALTER TABLE ONLY kommentar_m ALTER COLUMN datum SET DEFAULT now();


--
-- Name: id; Type: DEFAULT; Schema: land; Owner: -
--

ALTER TABLE ONLY kommentar_p ALTER COLUMN id SET DEFAULT nextval('bund.kommentar_id_seq'::regclass);


--
-- Name: datum; Type: DEFAULT; Schema: land; Owner: -
--

ALTER TABLE ONLY kommentar_p ALTER COLUMN datum SET DEFAULT now();


--
-- Name: id; Type: DEFAULT; Schema: land; Owner: -
--

ALTER TABLE ONLY messprogramm ALTER COLUMN id SET DEFAULT nextval('messprogramm_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: land; Owner: -
--

ALTER TABLE ONLY messprogramm_mmt ALTER COLUMN id SET DEFAULT nextval('messprogramm_mmt_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: land; Owner: -
--

ALTER TABLE ONLY messung ALTER COLUMN id SET DEFAULT nextval('bund.messung_id_seq'::regclass);


--
-- Name: fertig; Type: DEFAULT; Schema: land; Owner: -
--

ALTER TABLE ONLY messung ALTER COLUMN fertig SET DEFAULT false;


--
-- Name: letzte_aenderung; Type: DEFAULT; Schema: land; Owner: -
--

ALTER TABLE ONLY messung ALTER COLUMN letzte_aenderung SET DEFAULT now();


--
-- Name: id; Type: DEFAULT; Schema: land; Owner: -
--

ALTER TABLE ONLY messung_translation ALTER COLUMN id SET DEFAULT nextval('messung_translation_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: land; Owner: -
--

ALTER TABLE ONLY messwert ALTER COLUMN id SET DEFAULT nextval('bund.messwert_id_seq'::regclass);


--
-- Name: grenzwertueberschreitung; Type: DEFAULT; Schema: land; Owner: -
--

ALTER TABLE ONLY messwert ALTER COLUMN grenzwertueberschreitung SET DEFAULT false;


--
-- Name: letzte_aenderung; Type: DEFAULT; Schema: land; Owner: -
--

ALTER TABLE ONLY messwert ALTER COLUMN letzte_aenderung SET DEFAULT now();


--
-- Name: id; Type: DEFAULT; Schema: land; Owner: -
--

ALTER TABLE ONLY ort ALTER COLUMN id SET DEFAULT nextval('bund.ort_id_seq'::regclass);


--
-- Name: letzte_aenderung; Type: DEFAULT; Schema: land; Owner: -
--

ALTER TABLE ONLY ort ALTER COLUMN letzte_aenderung SET DEFAULT now();


--
-- Name: id; Type: DEFAULT; Schema: land; Owner: -
--

ALTER TABLE ONLY probe ALTER COLUMN id SET DEFAULT nextval('bund.probe_id_seq'::regclass);


--
-- Name: test; Type: DEFAULT; Schema: land; Owner: -
--

ALTER TABLE ONLY probe ALTER COLUMN test SET DEFAULT false;


--
-- Name: letzte_aenderung; Type: DEFAULT; Schema: land; Owner: -
--

ALTER TABLE ONLY probe ALTER COLUMN letzte_aenderung SET DEFAULT now();


--
-- Name: id; Type: DEFAULT; Schema: land; Owner: -
--

ALTER TABLE ONLY probe_translation ALTER COLUMN id SET DEFAULT nextval('probe_translation_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: land; Owner: -
--

ALTER TABLE ONLY status ALTER COLUMN id SET DEFAULT nextval('bund.status_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: land; Owner: -
--

ALTER TABLE ONLY zusatz_wert ALTER COLUMN id SET DEFAULT nextval('bund.zusatz_wert_id_seq'::regclass);


--
-- Name: letzte_aenderung; Type: DEFAULT; Schema: land; Owner: -
--

ALTER TABLE ONLY zusatz_wert ALTER COLUMN letzte_aenderung SET DEFAULT now();


SET search_path = stammdaten, pg_catalog;

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

ALTER TABLE ONLY de_vg ALTER COLUMN id SET DEFAULT nextval('de_vg_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY deskriptoren ALTER COLUMN id SET DEFAULT nextval('deskriptoren_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY koordinaten_art ALTER COLUMN id SET DEFAULT nextval('koordinaten_art_id_seq'::regclass);


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

ALTER TABLE ONLY staat ALTER COLUMN id SET DEFAULT nextval('staat_id_seq'::regclass);


SET search_path = bund, pg_catalog;

--
-- Name: kommentar_pkey; Type: CONSTRAINT; Schema: bund; Owner: -; Tablespace: 
--

ALTER TABLE ONLY kommentar
    ADD CONSTRAINT kommentar_pkey PRIMARY KEY (id);


--
-- Name: list_pkey; Type: CONSTRAINT; Schema: bund; Owner: -; Tablespace: 
--

ALTER TABLE ONLY list
    ADD CONSTRAINT list_pkey PRIMARY KEY (id);


--
-- Name: messung_pkey; Type: CONSTRAINT; Schema: bund; Owner: -; Tablespace: 
--

ALTER TABLE ONLY messung
    ADD CONSTRAINT messung_pkey PRIMARY KEY (id);


--
-- Name: messwert_messungs_id_messgroesse_id_key; Type: CONSTRAINT; Schema: bund; Owner: -; Tablespace: 
--

ALTER TABLE ONLY messwert
    ADD CONSTRAINT messwert_messungs_id_messgroesse_id_key UNIQUE (messungs_id, messgroesse_id);


--
-- Name: messwert_pkey; Type: CONSTRAINT; Schema: bund; Owner: -; Tablespace: 
--

ALTER TABLE ONLY messwert
    ADD CONSTRAINT messwert_pkey PRIMARY KEY (id);


--
-- Name: ort_pkey; Type: CONSTRAINT; Schema: bund; Owner: -; Tablespace: 
--

ALTER TABLE ONLY ort
    ADD CONSTRAINT ort_pkey PRIMARY KEY (id);


--
-- Name: probe_pkey; Type: CONSTRAINT; Schema: bund; Owner: -; Tablespace: 
--

ALTER TABLE ONLY probe
    ADD CONSTRAINT probe_pkey PRIMARY KEY (id);


--
-- Name: status_messungs_id_key; Type: CONSTRAINT; Schema: bund; Owner: -; Tablespace: 
--

ALTER TABLE ONLY status
    ADD CONSTRAINT status_messungs_id_key UNIQUE (messungs_id);


--
-- Name: status_pkey; Type: CONSTRAINT; Schema: bund; Owner: -; Tablespace: 
--

ALTER TABLE ONLY status
    ADD CONSTRAINT status_pkey PRIMARY KEY (id);


--
-- Name: zusatz_wert_pkey; Type: CONSTRAINT; Schema: bund; Owner: -; Tablespace: 
--

ALTER TABLE ONLY zusatz_wert
    ADD CONSTRAINT zusatz_wert_pkey PRIMARY KEY (id);


--
-- Name: zusatz_wert_probe_id_pzs_id_key; Type: CONSTRAINT; Schema: bund; Owner: -; Tablespace: 
--

ALTER TABLE ONLY zusatz_wert
    ADD CONSTRAINT zusatz_wert_probe_id_pzs_id_key UNIQUE (probe_id, pzs_id);


SET search_path = land, pg_catalog;

--
-- Name: kommentar_m_pkey; Type: CONSTRAINT; Schema: land; Owner: -; Tablespace: 
--

ALTER TABLE ONLY kommentar_m
    ADD CONSTRAINT kommentar_m_pkey PRIMARY KEY (id);


--
-- Name: kommentar_p_pkey; Type: CONSTRAINT; Schema: land; Owner: -; Tablespace: 
--

ALTER TABLE ONLY kommentar_p
    ADD CONSTRAINT kommentar_p_pkey PRIMARY KEY (id);


--
-- Name: messprogramm_mmt_pkey; Type: CONSTRAINT; Schema: land; Owner: -; Tablespace: 
--

ALTER TABLE ONLY messprogramm_mmt
    ADD CONSTRAINT messprogramm_mmt_pkey PRIMARY KEY (id);


--
-- Name: messprogramm_pkey; Type: CONSTRAINT; Schema: land; Owner: -; Tablespace: 
--

ALTER TABLE ONLY messprogramm
    ADD CONSTRAINT messprogramm_pkey PRIMARY KEY (id);


--
-- Name: messung_pkey; Type: CONSTRAINT; Schema: land; Owner: -; Tablespace: 
--

ALTER TABLE ONLY messung
    ADD CONSTRAINT messung_pkey PRIMARY KEY (id);


--
-- Name: messung_translation_messungs_id_key; Type: CONSTRAINT; Schema: land; Owner: -; Tablespace: 
--

ALTER TABLE ONLY messung_translation
    ADD CONSTRAINT messung_translation_messungs_id_key UNIQUE (messungs_id);


--
-- Name: messung_translation_pkey; Type: CONSTRAINT; Schema: land; Owner: -; Tablespace: 
--

ALTER TABLE ONLY messung_translation
    ADD CONSTRAINT messung_translation_pkey PRIMARY KEY (id);


--
-- Name: messwert_messungs_id_messgroesse_id_key; Type: CONSTRAINT; Schema: land; Owner: -; Tablespace: 
--

ALTER TABLE ONLY messwert
    ADD CONSTRAINT messwert_messungs_id_messgroesse_id_key UNIQUE (messungs_id, messgroesse_id);


--
-- Name: messwert_pkey; Type: CONSTRAINT; Schema: land; Owner: -; Tablespace: 
--

ALTER TABLE ONLY messwert
    ADD CONSTRAINT messwert_pkey PRIMARY KEY (id);


--
-- Name: ort_pkey; Type: CONSTRAINT; Schema: land; Owner: -; Tablespace: 
--

ALTER TABLE ONLY ort
    ADD CONSTRAINT ort_pkey PRIMARY KEY (id);


--
-- Name: probe_pkey; Type: CONSTRAINT; Schema: land; Owner: -; Tablespace: 
--

ALTER TABLE ONLY probe
    ADD CONSTRAINT probe_pkey PRIMARY KEY (id);


--
-- Name: probe_translation_pkey; Type: CONSTRAINT; Schema: land; Owner: -; Tablespace: 
--

ALTER TABLE ONLY probe_translation
    ADD CONSTRAINT probe_translation_pkey PRIMARY KEY (id);


--
-- Name: probe_translation_probe_id_key; Type: CONSTRAINT; Schema: land; Owner: -; Tablespace: 
--

ALTER TABLE ONLY probe_translation
    ADD CONSTRAINT probe_translation_probe_id_key UNIQUE (probe_id);


--
-- Name: status_messungs_id_key; Type: CONSTRAINT; Schema: land; Owner: -; Tablespace: 
--

ALTER TABLE ONLY status
    ADD CONSTRAINT status_messungs_id_key UNIQUE (messungs_id);


--
-- Name: status_pkey; Type: CONSTRAINT; Schema: land; Owner: -; Tablespace: 
--

ALTER TABLE ONLY status
    ADD CONSTRAINT status_pkey PRIMARY KEY (id);


--
-- Name: zusatz_wert_pkey; Type: CONSTRAINT; Schema: land; Owner: -; Tablespace: 
--

ALTER TABLE ONLY zusatz_wert
    ADD CONSTRAINT zusatz_wert_pkey PRIMARY KEY (id);


--
-- Name: zusatz_wert_probe_id_pzs_id_key; Type: CONSTRAINT; Schema: land; Owner: -; Tablespace: 
--

ALTER TABLE ONLY zusatz_wert
    ADD CONSTRAINT zusatz_wert_probe_id_pzs_id_key UNIQUE (probe_id, pzs_id);


SET search_path = stammdaten, pg_catalog;

--
-- Name: auth_pkey; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace: 
--

ALTER TABLE ONLY auth
    ADD CONSTRAINT auth_pkey PRIMARY KEY (id);


--
-- Name: datenbasis_pkey; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace: 
--

ALTER TABLE ONLY datenbasis
    ADD CONSTRAINT datenbasis_pkey PRIMARY KEY (id);


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
-- Name: koordinaten_art_pkey; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace: 
--

ALTER TABLE ONLY koordinaten_art
    ADD CONSTRAINT koordinaten_art_pkey PRIMARY KEY (id);


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
-- Name: staat_pkey; Type: CONSTRAINT; Schema: stammdaten; Owner: -; Tablespace: 
--

ALTER TABLE ONLY staat
    ADD CONSTRAINT staat_pkey PRIMARY KEY (id);


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


SET search_path = bund, pg_catalog;

--
-- Name: p_list_zuord1; Type: INDEX; Schema: bund; Owner: -; Tablespace: 
--

CREATE INDEX p_list_zuord1 ON list_zuordnung USING btree (list_id);


--
-- Name: p_list_zuord2; Type: INDEX; Schema: bund; Owner: -; Tablespace: 
--

CREATE INDEX p_list_zuord2 ON list_zuordnung USING hash (list_id);


SET search_path = land, pg_catalog;

--
-- Name: messung_probe_id_idx; Type: INDEX; Schema: land; Owner: -; Tablespace: 
--

CREATE INDEX messung_probe_id_idx ON messung USING btree (probe_id);


--
-- Name: messung_translation_messungs_id_idx; Type: INDEX; Schema: land; Owner: -; Tablespace: 
--

CREATE INDEX messung_translation_messungs_id_idx ON messung_translation USING btree (messungs_id);


--
-- Name: messwert_messungs_id_idx; Type: INDEX; Schema: land; Owner: -; Tablespace: 
--

CREATE INDEX messwert_messungs_id_idx ON messwert USING btree (messungs_id);


--
-- Name: probe_id_idx; Type: INDEX; Schema: land; Owner: -; Tablespace: 
--

CREATE INDEX probe_id_idx ON probe USING btree (id);


--
-- Name: probe_translation_probe_id_idx; Type: INDEX; Schema: land; Owner: -; Tablespace: 
--

CREATE INDEX probe_translation_probe_id_idx ON probe_translation USING btree (probe_id);


SET search_path = stammdaten, pg_catalog;

--
-- Name: de_vg_geom_gist; Type: INDEX; Schema: stammdaten; Owner: -; Tablespace: 
--

CREATE INDEX de_vg_geom_gist ON de_vg USING gist (geom);


--
-- Name: fts_stauts_kooin10001; Type: INDEX; Schema: stammdaten; Owner: -; Tablespace: 
--

CREATE INDEX fts_stauts_kooin10001 ON staat USING btree (kda_id);


SET search_path = land, pg_catalog;

--
-- Name: tree_timestamp_messung; Type: TRIGGER; Schema: land; Owner: -
--

CREATE TRIGGER tree_timestamp_messung BEFORE UPDATE ON messung FOR EACH ROW EXECUTE PROCEDURE update_time_messung();


--
-- Name: tree_timestamp_messwert; Type: TRIGGER; Schema: land; Owner: -
--

CREATE TRIGGER tree_timestamp_messwert BEFORE UPDATE ON messwert FOR EACH ROW EXECUTE PROCEDURE update_time_messwert();


--
-- Name: tree_timestamp_ort; Type: TRIGGER; Schema: land; Owner: -
--

CREATE TRIGGER tree_timestamp_ort BEFORE UPDATE ON ort FOR EACH ROW EXECUTE PROCEDURE update_time_ort();


--
-- Name: tree_timestamp_probe; Type: TRIGGER; Schema: land; Owner: -
--

CREATE TRIGGER tree_timestamp_probe BEFORE UPDATE ON probe FOR EACH ROW EXECUTE PROCEDURE update_time_probe();


--
-- Name: tree_timestamp_status; Type: TRIGGER; Schema: land; Owner: -
--

CREATE TRIGGER tree_timestamp_status BEFORE UPDATE ON status FOR EACH ROW EXECUTE PROCEDURE update_time_status();


--
-- Name: tree_timestamp_zusatzwert; Type: TRIGGER; Schema: land; Owner: -
--

CREATE TRIGGER tree_timestamp_zusatzwert BEFORE UPDATE ON zusatz_wert FOR EACH ROW EXECUTE PROCEDURE update_time_zusatzwert();


--
-- Name: verify_kommentar_m_id; Type: TRIGGER; Schema: land; Owner: -
--

CREATE TRIGGER verify_kommentar_m_id BEFORE INSERT ON kommentar_m FOR EACH ROW EXECUTE PROCEDURE bund.is_kommentar_unique();


--
-- Name: verify_kommentar_p_id; Type: TRIGGER; Schema: land; Owner: -
--

CREATE TRIGGER verify_kommentar_p_id BEFORE INSERT ON kommentar_p FOR EACH ROW EXECUTE PROCEDURE bund.is_kommentar_unique();


--
-- Name: verify_messung_id; Type: TRIGGER; Schema: land; Owner: -
--

CREATE TRIGGER verify_messung_id BEFORE INSERT ON messung FOR EACH ROW EXECUTE PROCEDURE bund.is_messung_unique();


--
-- Name: verify_messwert_id; Type: TRIGGER; Schema: land; Owner: -
--

CREATE TRIGGER verify_messwert_id BEFORE INSERT ON messwert FOR EACH ROW EXECUTE PROCEDURE bund.is_messwert_unique();


--
-- Name: verify_ort_id; Type: TRIGGER; Schema: land; Owner: -
--

CREATE TRIGGER verify_ort_id BEFORE INSERT ON ort FOR EACH ROW EXECUTE PROCEDURE bund.is_ort_unique();


--
-- Name: verify_probe_id; Type: TRIGGER; Schema: land; Owner: -
--

CREATE TRIGGER verify_probe_id BEFORE INSERT ON probe FOR EACH ROW EXECUTE PROCEDURE bund.is_probe_unique();


--
-- Name: verify_status_id; Type: TRIGGER; Schema: land; Owner: -
--

CREATE TRIGGER verify_status_id BEFORE INSERT ON status FOR EACH ROW EXECUTE PROCEDURE bund.is_status_unique();


--
-- Name: verify_zusatz_wert_id; Type: TRIGGER; Schema: land; Owner: -
--

CREATE TRIGGER verify_zusatz_wert_id BEFORE INSERT ON zusatz_wert FOR EACH ROW EXECUTE PROCEDURE bund.is_zusatz_wert_unique();


SET search_path = bund, pg_catalog;

--
-- Name: kommentar_m_messungs_id_fkey; Type: FK CONSTRAINT; Schema: bund; Owner: -
--

ALTER TABLE ONLY kommentar_m
    ADD CONSTRAINT kommentar_m_messungs_id_fkey FOREIGN KEY (messungs_id) REFERENCES messung(id);


--
-- Name: kommentar_p_probe_id_fkey; Type: FK CONSTRAINT; Schema: bund; Owner: -
--

ALTER TABLE ONLY kommentar_p
    ADD CONSTRAINT kommentar_p_probe_id_fkey FOREIGN KEY (probe_id) REFERENCES probe(id);


--
-- Name: messung_mmt_id_fkey; Type: FK CONSTRAINT; Schema: bund; Owner: -
--

ALTER TABLE ONLY messung
    ADD CONSTRAINT messung_mmt_id_fkey FOREIGN KEY (mmt_id) REFERENCES stammdaten.mess_methode(id);


--
-- Name: messung_probe_id_fkey; Type: FK CONSTRAINT; Schema: bund; Owner: -
--

ALTER TABLE ONLY messung
    ADD CONSTRAINT messung_probe_id_fkey FOREIGN KEY (probe_id) REFERENCES probe(id);


--
-- Name: messwert_meh_id_fkey; Type: FK CONSTRAINT; Schema: bund; Owner: -
--

ALTER TABLE ONLY messwert
    ADD CONSTRAINT messwert_meh_id_fkey FOREIGN KEY (meh_id) REFERENCES stammdaten.mess_einheit(id);


--
-- Name: messwert_messgroesse_id_fkey; Type: FK CONSTRAINT; Schema: bund; Owner: -
--

ALTER TABLE ONLY messwert
    ADD CONSTRAINT messwert_messgroesse_id_fkey FOREIGN KEY (messgroesse_id) REFERENCES stammdaten.messgroesse(id);


--
-- Name: messwert_messungs_id_fkey; Type: FK CONSTRAINT; Schema: bund; Owner: -
--

ALTER TABLE ONLY messwert
    ADD CONSTRAINT messwert_messungs_id_fkey FOREIGN KEY (messungs_id) REFERENCES messung(id);


--
-- Name: ort_ort_id_fkey; Type: FK CONSTRAINT; Schema: bund; Owner: -
--

ALTER TABLE ONLY ort
    ADD CONSTRAINT ort_ort_id_fkey FOREIGN KEY (ort_id) REFERENCES stammdaten.ort(id);


--
-- Name: ort_probe_id_fkey; Type: FK CONSTRAINT; Schema: bund; Owner: -
--

ALTER TABLE ONLY ort
    ADD CONSTRAINT ort_probe_id_fkey FOREIGN KEY (probe_id) REFERENCES probe(id);


--
-- Name: probe_datenbasis_id_fkey; Type: FK CONSTRAINT; Schema: bund; Owner: -
--

ALTER TABLE ONLY probe
    ADD CONSTRAINT probe_datenbasis_id_fkey FOREIGN KEY (datenbasis_id) REFERENCES stammdaten.datenbasis(id);


--
-- Name: probe_mst_id_fkey; Type: FK CONSTRAINT; Schema: bund; Owner: -
--

ALTER TABLE ONLY probe
    ADD CONSTRAINT probe_mst_id_fkey FOREIGN KEY (mst_id) REFERENCES stammdaten.mess_stelle(id);


--
-- Name: probe_mst_id_fkey1; Type: FK CONSTRAINT; Schema: bund; Owner: -
--

ALTER TABLE ONLY probe
    ADD CONSTRAINT probe_mst_id_fkey1 FOREIGN KEY (mst_id) REFERENCES stammdaten.mess_stelle(id);


--
-- Name: probe_netzbetreiber_id_fkey; Type: FK CONSTRAINT; Schema: bund; Owner: -
--

ALTER TABLE ONLY probe
    ADD CONSTRAINT probe_netzbetreiber_id_fkey FOREIGN KEY (netzbetreiber_id) REFERENCES stammdaten.netz_betreiber(id);


--
-- Name: probe_probenart_id_fkey; Type: FK CONSTRAINT; Schema: bund; Owner: -
--

ALTER TABLE ONLY probe
    ADD CONSTRAINT probe_probenart_id_fkey FOREIGN KEY (probenart_id) REFERENCES stammdaten.probenart(id);


--
-- Name: probe_umw_id_fkey; Type: FK CONSTRAINT; Schema: bund; Owner: -
--

ALTER TABLE ONLY probe
    ADD CONSTRAINT probe_umw_id_fkey FOREIGN KEY (umw_id) REFERENCES stammdaten.umwelt(id);


--
-- Name: status_erzeuger_fkey; Type: FK CONSTRAINT; Schema: bund; Owner: -
--

ALTER TABLE ONLY status
    ADD CONSTRAINT status_erzeuger_fkey FOREIGN KEY (erzeuger) REFERENCES stammdaten.mess_stelle(id);


--
-- Name: status_messungs_id_fkey; Type: FK CONSTRAINT; Schema: bund; Owner: -
--

ALTER TABLE ONLY status
    ADD CONSTRAINT status_messungs_id_fkey FOREIGN KEY (messungs_id) REFERENCES messung(id);


--
-- Name: zusatz_wert_probe_id_fkey; Type: FK CONSTRAINT; Schema: bund; Owner: -
--

ALTER TABLE ONLY zusatz_wert
    ADD CONSTRAINT zusatz_wert_probe_id_fkey FOREIGN KEY (probe_id) REFERENCES probe(id);


--
-- Name: zusatz_wert_pzs_id_fkey; Type: FK CONSTRAINT; Schema: bund; Owner: -
--

ALTER TABLE ONLY zusatz_wert
    ADD CONSTRAINT zusatz_wert_pzs_id_fkey FOREIGN KEY (pzs_id) REFERENCES stammdaten.proben_zusatz(id);


SET search_path = land, pg_catalog;

--
-- Name: kommentar_m_messungs_id_fkey; Type: FK CONSTRAINT; Schema: land; Owner: -
--

ALTER TABLE ONLY kommentar_m
    ADD CONSTRAINT kommentar_m_messungs_id_fkey FOREIGN KEY (messungs_id) REFERENCES messung(id) ON DELETE CASCADE;


--
-- Name: kommentar_p_probe_id_fkey; Type: FK CONSTRAINT; Schema: land; Owner: -
--

ALTER TABLE ONLY kommentar_p
    ADD CONSTRAINT kommentar_p_probe_id_fkey FOREIGN KEY (probe_id) REFERENCES probe(id) ON DELETE CASCADE;


--
-- Name: messprogramm_datenbasis_id_fkey; Type: FK CONSTRAINT; Schema: land; Owner: -
--

ALTER TABLE ONLY messprogramm
    ADD CONSTRAINT messprogramm_datenbasis_id_fkey FOREIGN KEY (datenbasis_id) REFERENCES stammdaten.datenbasis(id);


--
-- Name: messprogramm_mmt_messprogramm_id_fkey; Type: FK CONSTRAINT; Schema: land; Owner: -
--

ALTER TABLE ONLY messprogramm_mmt
    ADD CONSTRAINT messprogramm_mmt_messprogramm_id_fkey FOREIGN KEY (messprogramm_id) REFERENCES messprogramm(id) ON DELETE CASCADE;


--
-- Name: messprogramm_mmt_mmt_id_fkey; Type: FK CONSTRAINT; Schema: land; Owner: -
--

ALTER TABLE ONLY messprogramm_mmt
    ADD CONSTRAINT messprogramm_mmt_mmt_id_fkey FOREIGN KEY (mmt_id) REFERENCES stammdaten.mess_methode(id);


--
-- Name: messprogramm_mst_id_fkey; Type: FK CONSTRAINT; Schema: land; Owner: -
--

ALTER TABLE ONLY messprogramm
    ADD CONSTRAINT messprogramm_mst_id_fkey FOREIGN KEY (mst_id) REFERENCES stammdaten.mess_stelle(id);


--
-- Name: messprogramm_netzbetreiber_id_fkey; Type: FK CONSTRAINT; Schema: land; Owner: -
--

ALTER TABLE ONLY messprogramm
    ADD CONSTRAINT messprogramm_netzbetreiber_id_fkey FOREIGN KEY (netzbetreiber_id) REFERENCES stammdaten.netz_betreiber(id);


--
-- Name: messprogramm_ort_id_fkey; Type: FK CONSTRAINT; Schema: land; Owner: -
--

ALTER TABLE ONLY messprogramm
    ADD CONSTRAINT messprogramm_ort_id_fkey FOREIGN KEY (ort_id) REFERENCES stammdaten.ort(id);


--
-- Name: messprogramm_probenart_id_fkey; Type: FK CONSTRAINT; Schema: land; Owner: -
--

ALTER TABLE ONLY messprogramm
    ADD CONSTRAINT messprogramm_probenart_id_fkey FOREIGN KEY (probenart_id) REFERENCES stammdaten.probenart(id);


--
-- Name: messprogramm_umw_id_fkey; Type: FK CONSTRAINT; Schema: land; Owner: -
--

ALTER TABLE ONLY messprogramm
    ADD CONSTRAINT messprogramm_umw_id_fkey FOREIGN KEY (umw_id) REFERENCES stammdaten.umwelt(id);


--
-- Name: messung_mmt_id_fkey; Type: FK CONSTRAINT; Schema: land; Owner: -
--

ALTER TABLE ONLY messung
    ADD CONSTRAINT messung_mmt_id_fkey FOREIGN KEY (mmt_id) REFERENCES stammdaten.mess_methode(id) ON DELETE CASCADE;


--
-- Name: messung_probe_id_fkey; Type: FK CONSTRAINT; Schema: land; Owner: -
--

ALTER TABLE ONLY messung
    ADD CONSTRAINT messung_probe_id_fkey FOREIGN KEY (probe_id) REFERENCES probe(id) ON DELETE CASCADE;


--
-- Name: messung_translation_messungs_id_fkey; Type: FK CONSTRAINT; Schema: land; Owner: -
--

ALTER TABLE ONLY messung_translation
    ADD CONSTRAINT messung_translation_messungs_id_fkey FOREIGN KEY (messungs_id) REFERENCES messung(id) ON DELETE CASCADE;


--
-- Name: messwert_meh_id_fkey; Type: FK CONSTRAINT; Schema: land; Owner: -
--

ALTER TABLE ONLY messwert
    ADD CONSTRAINT messwert_meh_id_fkey FOREIGN KEY (meh_id) REFERENCES stammdaten.mess_einheit(id);


--
-- Name: messwert_messgroesse_id_fkey; Type: FK CONSTRAINT; Schema: land; Owner: -
--

ALTER TABLE ONLY messwert
    ADD CONSTRAINT messwert_messgroesse_id_fkey FOREIGN KEY (messgroesse_id) REFERENCES stammdaten.messgroesse(id);


--
-- Name: messwert_messungs_id_fkey; Type: FK CONSTRAINT; Schema: land; Owner: -
--

ALTER TABLE ONLY messwert
    ADD CONSTRAINT messwert_messungs_id_fkey FOREIGN KEY (messungs_id) REFERENCES messung(id) ON DELETE CASCADE;


--
-- Name: ort_ort_id_fkey; Type: FK CONSTRAINT; Schema: land; Owner: -
--

ALTER TABLE ONLY ort
    ADD CONSTRAINT ort_ort_id_fkey FOREIGN KEY (ort_id) REFERENCES stammdaten.ort(id);


--
-- Name: ort_probe_id_fkey; Type: FK CONSTRAINT; Schema: land; Owner: -
--

ALTER TABLE ONLY ort
    ADD CONSTRAINT ort_probe_id_fkey FOREIGN KEY (probe_id) REFERENCES probe(id) ON DELETE CASCADE;


--
-- Name: probe_datenbasis_id_fkey; Type: FK CONSTRAINT; Schema: land; Owner: -
--

ALTER TABLE ONLY probe
    ADD CONSTRAINT probe_datenbasis_id_fkey FOREIGN KEY (datenbasis_id) REFERENCES stammdaten.datenbasis(id);


--
-- Name: probe_mst_id_fkey; Type: FK CONSTRAINT; Schema: land; Owner: -
--

ALTER TABLE ONLY probe
    ADD CONSTRAINT probe_mst_id_fkey FOREIGN KEY (mst_id) REFERENCES stammdaten.mess_stelle(id);


--
-- Name: probe_netzbetreiber_id_fkey; Type: FK CONSTRAINT; Schema: land; Owner: -
--

ALTER TABLE ONLY probe
    ADD CONSTRAINT probe_netzbetreiber_id_fkey FOREIGN KEY (netzbetreiber_id) REFERENCES stammdaten.netz_betreiber(id);


--
-- Name: probe_probenart_id_fkey; Type: FK CONSTRAINT; Schema: land; Owner: -
--

ALTER TABLE ONLY probe
    ADD CONSTRAINT probe_probenart_id_fkey FOREIGN KEY (probenart_id) REFERENCES stammdaten.probenart(id);


--
-- Name: probe_translation_probe_id_fkey; Type: FK CONSTRAINT; Schema: land; Owner: -
--

ALTER TABLE ONLY probe_translation
    ADD CONSTRAINT probe_translation_probe_id_fkey FOREIGN KEY (probe_id) REFERENCES probe(id) ON DELETE CASCADE;


--
-- Name: probe_umw_id_fkey; Type: FK CONSTRAINT; Schema: land; Owner: -
--

ALTER TABLE ONLY probe
    ADD CONSTRAINT probe_umw_id_fkey FOREIGN KEY (umw_id) REFERENCES stammdaten.umwelt(id);


--
-- Name: status_erzeuger_fkey; Type: FK CONSTRAINT; Schema: land; Owner: -
--

ALTER TABLE ONLY status
    ADD CONSTRAINT status_erzeuger_fkey FOREIGN KEY (erzeuger) REFERENCES stammdaten.mess_stelle(id);


--
-- Name: status_messungs_id_fkey; Type: FK CONSTRAINT; Schema: land; Owner: -
--

ALTER TABLE ONLY status
    ADD CONSTRAINT status_messungs_id_fkey FOREIGN KEY (messungs_id) REFERENCES messung(id) ON DELETE CASCADE;


--
-- Name: zusatz_wert_probe_id_fkey; Type: FK CONSTRAINT; Schema: land; Owner: -
--

ALTER TABLE ONLY zusatz_wert
    ADD CONSTRAINT zusatz_wert_probe_id_fkey FOREIGN KEY (probe_id) REFERENCES probe(id) ON DELETE CASCADE;


--
-- Name: zusatz_wert_pzs_id_fkey; Type: FK CONSTRAINT; Schema: land; Owner: -
--

ALTER TABLE ONLY zusatz_wert
    ADD CONSTRAINT zusatz_wert_pzs_id_fkey FOREIGN KEY (pzs_id) REFERENCES stammdaten.proben_zusatz(id);


SET search_path = stammdaten, pg_catalog;

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
-- Name: fk_deskriptoren_vorgaenger; Type: FK CONSTRAINT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY deskriptoren
    ADD CONSTRAINT fk_deskriptoren_vorgaenger FOREIGN KEY (vorgaenger) REFERENCES deskriptoren(id);


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
-- Name: staat_kda_id_fkey; Type: FK CONSTRAINT; Schema: stammdaten; Owner: -
--

ALTER TABLE ONLY staat
    ADD CONSTRAINT staat_kda_id_fkey FOREIGN KEY (kda_id) REFERENCES koordinaten_art(id);


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


--
-- Name: public; Type: ACL; Schema: -; Owner: -
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

