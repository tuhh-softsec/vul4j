\set ON_ERROR_STOP on

BEGIN;


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


SET search_path = bund, pg_catalog;

--
-- Name: update_time_status(); Type: FUNCTION; Schema: bund; Owner: -
--

CREATE FUNCTION update_time_status() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    BEGIN
        NEW.tree_modified = now();
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

SET search_path = land, pg_catalog;

CREATE FUNCTION update_letzte_aenderung() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    BEGIN
        NEW.letzte_aenderung = now();
        RETURN NEW;
    END;
$$;

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
        UPDATE bund.status_protokoll SET tree_modified = now() WHERE messungs_id = NEW.id;
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
        UPDATE land.ortszuordnung SET tree_modified = now() WHERE probe_id = NEW.id;
        UPDATE land.zusatz_wert SET tree_modified = now() WHERE probe_id = NEW.id;
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
CREATE TRIGGER letzte_aenderung_list BEFORE UPDATE ON list FOR EACH ROW EXECUTE PROCEDURE update_letzte_aenderung();


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
    status integer,
    letzte_aenderung timestamp without time zone DEFAULT now()
);
CREATE TRIGGER letzte_aenderung_messung BEFORE UPDATE ON messung FOR EACH ROW EXECUTE PROCEDURE update_letzte_aenderung();


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
    messwert double precision NOT NULL,
    messfehler real,
    nwg_zu_messwert double precision,
    meh_id smallint NOT NULL,
    grenzwertueberschreitung boolean DEFAULT false,
    letzte_aenderung timestamp without time zone DEFAULT now()
);
CREATE TRIGGER letzte_aenderung_messwert BEFORE UPDATE ON messwert FOR EACH ROW EXECUTE PROCEDURE update_letzte_aenderung();


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
-- Name: ortszuordnung; Type: TABLE; Schema: bund; Owner: -; Tablespace:
--

CREATE TABLE ortszuordnung (
    id integer DEFAULT nextval('ort_id_seq'::regclass) NOT NULL,
    probe_id integer NOT NULL,
    ort_id bigint NOT NULL,
    ortszuordnung_typ character varying(1),
    ortszusatztext character varying(100),
    letzte_aenderung timestamp without time zone DEFAULT now()
);
CREATE TRIGGER letzte_aenderung_ortszuordnung BEFORE UPDATE ON ortszuordnung FOR EACH ROW EXECUTE PROCEDURE update_letzte_aenderung();


--
-- Name: COLUMN ortszuordnung.ortszuordnung_typ; Type: COMMENT; Schema: bund; Owner: -
--

COMMENT ON COLUMN ortszuordnung.ortszuordnung_typ IS 'E = Entnahmeport, U = Ursprungsort, Z = Ortszusatz';


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
    id integer PRIMARY KEY DEFAULT nextval('probe_id_seq'::regclass),
    test boolean DEFAULT false NOT NULL,
    netzbetreiber_id character varying(2) REFERENCES stammdaten.netz_betreiber,
    mst_id character varying(5) NOT NULL
        REFERENCES stammdaten.mess_stelle,
    labor_mst_id character varying(5) NOT NULL
        REFERENCES stammdaten.mess_stelle,
    hauptproben_nr character varying(20),
    datenbasis_id smallint REFERENCES stammdaten.datenbasis,
    ba_id character varying(1),
    probenart_id smallint NOT NULL REFERENCES stammdaten.probenart,
    media_desk character varying(100),
    media character varying(100),
    umw_id character varying(3) REFERENCES stammdaten.umwelt,
    probeentnahme_beginn timestamp with time zone,
    probeentnahme_ende timestamp with time zone,
    mittelungsdauer bigint,
    letzte_aenderung timestamp without time zone DEFAULT now(),
    UNIQUE (mst_id, hauptproben_nr)
);

CREATE TRIGGER letzte_aenderung_probe BEFORE UPDATE ON probe FOR EACH ROW EXECUTE PROCEDURE update_letzte_aenderung();

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

COMMENT ON COLUMN probe.mst_id IS 'ID für Messstelle';


--
-- Name: COLUMN probe.labor_mst_id; Type: COMMENT; Schema: bund; Owner: -
--

COMMENT ON COLUMN probe.labor_mst_id IS '-- ID für Messlabor';


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
-- Name: status_protokoll; Type: TABLE; Schema: bund; Owner: -; Tablespace:
--

CREATE TABLE status_protokoll (
    status_stufe integer NOT NULL,
    status_wert integer NOT NULL,
    tree_modified timestamp without time zone DEFAULT now()
)
INHERITS (kommentar_m);


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
    messwert_pzs double precision,
    messfehler real,
    letzte_aenderung timestamp without time zone DEFAULT now(),
    nwg_zu_messwert double precision
);
CREATE TRIGGER letzte_aenderung_zusatzwert BEFORE UPDATE ON zusatz_wert FOR EACH ROW EXECUTE PROCEDURE update_letzte_aenderung();


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
-- Name: messprogramm_id_seq; Type: SEQUENCE; Schema: land; Owner: -
--

CREATE SEQUENCE messprogramm_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: messprogramm; Type: TABLE; Schema: land; Owner: -; Tablespace:
--

CREATE TABLE messprogramm (
    id integer PRIMARY KEY DEFAULT nextval('messprogramm_id_seq'::regclass),
    name character varying(256),
    test boolean DEFAULT false NOT NULL,
    netzbetreiber_id character varying(2) NOT NULL
        REFERENCES stammdaten.netz_betreiber,
    mst_id character varying(5) NOT NULL REFERENCES stammdaten.mess_stelle,
    labor_mst_id character varying(5) NOT NULL
        REFERENCES stammdaten.mess_stelle,
    datenbasis_id integer NOT NULL REFERENCES stammdaten.datenbasis,
    ba_id character varying(1),
    gem_id character varying(8),
    ort_id integer NOT NULL REFERENCES stammdaten.ort,
    media_desk character varying(100),
    umw_id character varying(3) REFERENCES stammdaten.umwelt,
    probenart_id integer NOT NULL REFERENCES stammdaten.probenart,
    probenintervall character varying(2) NOT NULL,
    teilintervall_von integer NOT NULL,
    teilintervall_bis integer NOT NULL,
    intervall_offset integer,
    gueltig_von integer NOT NULL,
    gueltig_bis integer NOT NULL,
    probe_nehmer_id integer,
    probe_kommentar character varying(80),
    letzte_aenderung timestamp without time zone DEFAULT now() NOT NULL
);
CREATE TRIGGER letzte_aenderung_messprogramm BEFORE UPDATE ON messprogramm FOR EACH ROW EXECUTE PROCEDURE update_letzte_aenderung();

--
-- Name: messprogramm_id_seq; Type: SEQUENCE OWNED BY; Schema: land; Owner: -
--

ALTER SEQUENCE messprogramm_id_seq OWNED BY messprogramm.id;


--
-- Name: COLUMN messprogramm.media_desk; Type: COMMENT; Schema: land; Owner: -
--

COMMENT ON COLUMN messprogramm.media_desk IS 'dekodierte Medienbezeichnung (aus media_desk abgeleitet)';


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
CREATE TRIGGER letzte_aenderung_messprogramm_mmt BEFORE UPDATE ON messprogramm_mmt FOR EACH ROW EXECUTE PROCEDURE update_letzte_aenderung();


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
-- Name: ortszuordnung; Type: TABLE; Schema: land; Owner: -; Tablespace:
--

CREATE TABLE ortszuordnung (
    tree_modified timestamp without time zone DEFAULT now()
)
INHERITS (bund.ortszuordnung);


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

ALTER TABLE ONLY probe
    ADD CONSTRAINT probe_pkey PRIMARY KEY (id);

ALTER TABLE ONLY probe
    ADD CONSTRAINT probe_datenbasis_id_fkey
    FOREIGN KEY (datenbasis_id) REFERENCES stammdaten.datenbasis(id);

ALTER TABLE ONLY probe
    ADD CONSTRAINT probe_mst_id_fkey
    FOREIGN KEY (mst_id) REFERENCES stammdaten.mess_stelle(id);

ALTER TABLE ONLY probe
    ADD CONSTRAINT probe_labor_mst_id_fkey
    FOREIGN KEY (labor_mst_id) REFERENCES stammdaten.mess_stelle(id);

ALTER TABLE ONLY probe
    ADD CONSTRAINT probe_netzbetreiber_id_fkey
    FOREIGN KEY (netzbetreiber_id) REFERENCES stammdaten.netz_betreiber(id);

ALTER TABLE ONLY probe
    ADD CONSTRAINT probe_probenart_id_fkey
    FOREIGN KEY (probenart_id) REFERENCES stammdaten.probenart(id);

ALTER TABLE ONLY probe
    ADD CONSTRAINT probe_umw_id_fkey
    FOREIGN KEY (umw_id) REFERENCES stammdaten.umwelt(id);


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
-- Name: status_protokoll; Type: TABLE; Schema: land; Owner: -; Tablespace:
--

CREATE TABLE status_protokoll (
)
INHERITS (bund.status_protokoll);


--
-- Name: zusatz_wert; Type: TABLE; Schema: land; Owner: -; Tablespace:
--

CREATE TABLE zusatz_wert (
    tree_modified timestamp without time zone DEFAULT now()
)
INHERITS (bund.zusatz_wert);


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


--
-- Name: id; Type: DEFAULT; Schema: bund; Owner: -
--

ALTER TABLE ONLY status_protokoll ALTER COLUMN id SET DEFAULT nextval('kommentar_id_seq'::regclass);


--
-- Name: datum; Type: DEFAULT; Schema: bund; Owner: -
--

ALTER TABLE ONLY status_protokoll ALTER COLUMN datum SET DEFAULT now();


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

ALTER TABLE ONLY ortszuordnung ALTER COLUMN id SET DEFAULT nextval('bund.ort_id_seq'::regclass);


--
-- Name: letzte_aenderung; Type: DEFAULT; Schema: land; Owner: -
--

ALTER TABLE ONLY ortszuordnung ALTER COLUMN letzte_aenderung SET DEFAULT now();


--
-- Name: id; Type: DEFAULT; Schema: land; Owner: -
--

ALTER TABLE ONLY probe_translation ALTER COLUMN id SET DEFAULT nextval('probe_translation_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: land; Owner: -
--

ALTER TABLE ONLY status_protokoll ALTER COLUMN id SET DEFAULT nextval('bund.kommentar_id_seq'::regclass);


--
-- Name: datum; Type: DEFAULT; Schema: land; Owner: -
--

ALTER TABLE ONLY status_protokoll ALTER COLUMN datum SET DEFAULT now();


--
-- Name: tree_modified; Type: DEFAULT; Schema: land; Owner: -
--

ALTER TABLE ONLY status_protokoll ALTER COLUMN tree_modified SET DEFAULT now();


--
-- Name: id; Type: DEFAULT; Schema: land; Owner: -
--

ALTER TABLE ONLY zusatz_wert ALTER COLUMN id SET DEFAULT nextval('bund.zusatz_wert_id_seq'::regclass);


--
-- Name: letzte_aenderung; Type: DEFAULT; Schema: land; Owner: -
--

ALTER TABLE ONLY zusatz_wert ALTER COLUMN letzte_aenderung SET DEFAULT now();


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
-- Name: ortszuordnung_pkey; Type: CONSTRAINT; Schema: bund; Owner: -; Tablespace:
--

ALTER TABLE ONLY ortszuordnung
    ADD CONSTRAINT ortszuordnung_pkey PRIMARY KEY (id);


--
-- Name: status_protokoll_pkey; Type: CONSTRAINT; Schema: bund; Owner: -; Tablespace:
--

ALTER TABLE ONLY status_protokoll
    ADD CONSTRAINT status_protokoll_pkey PRIMARY KEY (id);


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
-- Name: ortszuordnung_pkey; Type: CONSTRAINT; Schema: land; Owner: -; Tablespace:
--

ALTER TABLE ONLY ortszuordnung
    ADD CONSTRAINT ortszuordnung_pkey PRIMARY KEY (id);


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
-- Name: status_protokoll_pkey; Type: CONSTRAINT; Schema: land; Owner: -; Tablespace:
--

ALTER TABLE ONLY status_protokoll
    ADD CONSTRAINT status_protokoll_pkey PRIMARY KEY (id);


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


SET search_path = bund, pg_catalog;

--
-- Name: tree_timestamp_status; Type: TRIGGER; Schema: bund; Owner: -
--

CREATE TRIGGER tree_timestamp_status BEFORE UPDATE ON status_protokoll FOR EACH ROW EXECUTE PROCEDURE update_time_status();


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

CREATE TRIGGER tree_timestamp_ort BEFORE UPDATE ON ortszuordnung FOR EACH ROW EXECUTE PROCEDURE update_time_ort();


--
-- Name: tree_timestamp_probe; Type: TRIGGER; Schema: land; Owner: -
--

CREATE TRIGGER tree_timestamp_probe BEFORE UPDATE ON probe FOR EACH ROW EXECUTE PROCEDURE update_time_probe();


--
-- Name: tree_timestamp_zusatzwert; Type: TRIGGER; Schema: land; Owner: -
--

CREATE TRIGGER tree_timestamp_zusatzwert BEFORE UPDATE ON zusatz_wert FOR EACH ROW EXECUTE PROCEDURE update_time_zusatzwert();


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
-- Name: messung_status_protokoll_id_fkey; Type: FK CONSTRAINT; Schema: bund; Owner: -
--

ALTER TABLE ONLY messung
    ADD CONSTRAINT messung_status_protokoll_id_fkey FOREIGN KEY (status) REFERENCES status_protokoll(id);


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
-- Name: ortszuordnung_ort_id_fkey; Type: FK CONSTRAINT; Schema: bund; Owner: -
--

ALTER TABLE ONLY ortszuordnung
    ADD CONSTRAINT ortszuordnung_ort_id_fkey FOREIGN KEY (ort_id) REFERENCES stammdaten.ort(id);


--
-- Name: ortszuordnung_ortszuordnung_typ_fkey; Type: FK CONSTRAINT; Schema: bund; Owner: -
--

ALTER TABLE ONLY ortszuordnung
    ADD CONSTRAINT ortszuordnung_ortszuordnung_typ_fkey FOREIGN KEY (ortszuordnung_typ) REFERENCES stammdaten.ortszuordnung_typ(id);


--
-- Name: ortszuordnung_probe_id_fkey; Type: FK CONSTRAINT; Schema: bund; Owner: -
--

ALTER TABLE ONLY ortszuordnung
    ADD CONSTRAINT ortszuordnung_probe_id_fkey FOREIGN KEY (probe_id) REFERENCES probe(id);


--
-- Name: status_protokoll_status_stufe_fkey; Type: FK CONSTRAINT; Schema: bund; Owner: -
--

ALTER TABLE ONLY status_protokoll
    ADD CONSTRAINT status_protokoll_status_stufe_fkey FOREIGN KEY (status_stufe) REFERENCES stammdaten.status_stufe(id);


--
-- Name: status_protokoll_status_wert_fkey; Type: FK CONSTRAINT; Schema: bund; Owner: -
--

ALTER TABLE ONLY status_protokoll
    ADD CONSTRAINT status_protokoll_status_wert_fkey FOREIGN KEY (status_wert) REFERENCES stammdaten.status_wert(id);


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
-- Name: messung_status_protokoll_id_fkey; Type: FK CONSTRAINT; Schema: land; Owner: -
--

ALTER TABLE ONLY messung
    ADD CONSTRAINT messung_status_protokoll_id_fkey FOREIGN KEY (status) REFERENCES status_protokoll(id);


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
-- Name: ortszuordnung_ort_id_fkey; Type: FK CONSTRAINT; Schema: land; Owner: -
--

ALTER TABLE ONLY ortszuordnung
    ADD CONSTRAINT ortszuordnung_ort_id_fkey FOREIGN KEY (ort_id) REFERENCES stammdaten.ort(id);


--
-- Name: ortszuordnung_ortszuordnung_typ_fkey; Type: FK CONSTRAINT; Schema: land; Owner: -
--

ALTER TABLE ONLY ortszuordnung
    ADD CONSTRAINT ortszuordnung_ortszuordnung_typ_fkey FOREIGN KEY (ortszuordnung_typ) REFERENCES stammdaten.ortszuordnung_typ(id);


--
-- Name: ortszuordnung_probe_id_fkey; Type: FK CONSTRAINT; Schema: land; Owner: -
--

ALTER TABLE ONLY ortszuordnung
    ADD CONSTRAINT ortszuordnung_probe_id_fkey FOREIGN KEY (probe_id) REFERENCES probe(id) ON DELETE CASCADE;


--
-- Name: probe_translation_probe_id_fkey; Type: FK CONSTRAINT; Schema: land; Owner: -
--

ALTER TABLE ONLY probe_translation
    ADD CONSTRAINT probe_translation_probe_id_fkey FOREIGN KEY (probe_id) REFERENCES probe(id) ON DELETE CASCADE;


--
-- Name: status_protokoll_messungs_id_fkey; Type: FK CONSTRAINT; Schema: land; Owner: -
--

ALTER TABLE ONLY status_protokoll
    ADD CONSTRAINT status_protokoll_messungs_id_fkey FOREIGN KEY (messungs_id) REFERENCES messung(id) ON DELETE CASCADE;


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


COMMIT;
