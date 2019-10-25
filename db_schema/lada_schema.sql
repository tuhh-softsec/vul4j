\set ON_ERROR_STOP on

BEGIN;


SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: land; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA land;

SET search_path = land, pg_catalog;

CREATE FUNCTION set_messung_ext_id() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    BEGIN
        IF NEW.ext_id IS NULL THEN
            NEW.ext_id = (
                SELECT coalesce(max(ext_id),0)
                   FROM land.messung
                   WHERE probe_id = NEW.probe_id) + 1;
        END IF;
        RETURN NEW;
    END;
$$;

CREATE FUNCTION set_messung_status() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    DECLARE status_id integer;
    BEGIN
        INSERT INTO land.status_protokoll
            (mst_id, datum, text, messungs_id, status_kombi)
        VALUES ((SELECT mst_id
                     FROM land.probe
                     WHERE id = NEW.probe_id),
                now(), '', NEW.id, 1)
        RETURNING id into status_id;
        UPDATE land.messung SET status = status_id where id = NEW.id;
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


--
-- Name: update_time_status(); Type: FUNCTION; Schema: land; Owner: -
--

CREATE FUNCTION update_tree_modified() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    BEGIN
        NEW.tree_modified = now();
        RETURN NEW;
    END;
$$;


--
-- Name: update_time_messung(); Type: FUNCTION; Schema: land; Owner: -
--

CREATE FUNCTION update_tree_modified_messung() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
    BEGIN
        RAISE NOTICE 'messung is %',NEW.id;
        NEW.tree_modified = now();
        UPDATE land.messwert SET tree_modified = now() WHERE messungs_id = NEW.id;
        UPDATE land.status_protokoll SET tree_modified = now() WHERE messungs_id = NEW.id;
        RETURN NEW;
    END;
$$;


--
-- Name: update_time_probe(); Type: FUNCTION; Schema: land; Owner: -
--

CREATE FUNCTION update_tree_modified_probe() RETURNS trigger
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


SET default_tablespace = '';

SET default_with_oids = false;


--
-- Name: messung_messung_ext_id; Type: SEQUENCE; Schema: land; Owner: -
--

CREATE SEQUENCE messung_messung_ext_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: probe_probe_id_seq; Type: SEQUENCE; Schema: land; Owner: -
--

CREATE SEQUENCE probe_probe_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: messprogramm; Type: TABLE; Schema: land; Owner: -; Tablespace:
--

CREATE TABLE messprogramm (
    id serial PRIMARY KEY,
    kommentar character varying(1000),
    test boolean DEFAULT false NOT NULL,
    aktiv boolean DEFAULT true NOT NULL,
    mst_id character varying(5) NOT NULL REFERENCES stamm.mess_stelle,
    labor_mst_id character varying(5) NOT NULL REFERENCES stamm.mess_stelle,
    datenbasis_id integer NOT NULL REFERENCES stamm.datenbasis,
    ba_id integer DEFAULT 1 REFERENCES stamm.betriebsart,
    gem_id character varying(8) REFERENCES stamm.verwaltungseinheit,
    media_desk character varying(100) CHECK(media_desk LIKE '% %'),
    umw_id character varying(3) REFERENCES stamm.umwelt,
    probenart_id integer NOT NULL REFERENCES stamm.probenart,
    probenintervall character varying(2) NOT NULL,
    teilintervall_von integer NOT NULL,
    teilintervall_bis integer NOT NULL,
    intervall_offset integer NOT NULL DEFAULT 0,
    gueltig_von integer NOT NULL CHECK(gueltig_von BETWEEN 1 AND 365),
    gueltig_bis integer NOT NULL CHECK(gueltig_bis BETWEEN 1 AND 365),
    probe_nehmer_id integer REFERENCES stamm.probenehmer,
    mpl_id integer REFERENCES stamm.messprogramm_kategorie,
    probe_kommentar character varying(80),
    rei_progpunkt_grp_id integer REFERENCES stamm.rei_progpunkt_gruppe,
    kta_gruppe_id integer REFERENCES stamm.kta_gruppe,
    meh_id smallint REFERENCES stamm.mess_einheit,
    probenahmemenge character varying(90),
    letzte_aenderung timestamp without time zone DEFAULT now() NOT NULL,
    CHECK (probenintervall = 'J'
               AND teilintervall_von BETWEEN gueltig_von AND gueltig_bis
               AND teilintervall_bis BETWEEN gueltig_von AND gueltig_bis
               AND intervall_offset BETWEEN 0 AND 364
           OR probenintervall = 'H'
               AND teilintervall_von BETWEEN 1 AND 184
               AND teilintervall_bis BETWEEN 1 AND 184
               AND intervall_offset BETWEEN 0 AND 183
           OR probenintervall = 'Q'
               AND teilintervall_von BETWEEN 1 AND 92
               AND teilintervall_bis BETWEEN 1 AND 92
               AND intervall_offset BETWEEN 0 AND 91
           OR probenintervall = 'M'
               AND teilintervall_von BETWEEN 1 AND 31
               AND teilintervall_bis BETWEEN 1 AND 31
               AND intervall_offset BETWEEN 0 AND 30
           OR probenintervall = 'W4'
               AND teilintervall_von BETWEEN 1 AND 28
               AND teilintervall_bis BETWEEN 1 AND 28
               AND intervall_offset BETWEEN 0 AND 27
           OR probenintervall = 'W2'
               AND teilintervall_von BETWEEN 1 AND 14
               AND teilintervall_bis BETWEEN 1 AND 14
               AND intervall_offset BETWEEN 0 AND 13
           OR probenintervall = 'W'
               AND teilintervall_von BETWEEN 1 AND 7
               AND teilintervall_bis BETWEEN 1 AND 7
               AND intervall_offset BETWEEN 0 AND 6
           OR probenintervall = 'T'
               AND teilintervall_von = 1
               AND teilintervall_bis = 1
               AND intervall_offset = 0
           ),
    CHECK (teilintervall_von <= teilintervall_bis)
);
CREATE TRIGGER letzte_aenderung_messprogramm BEFORE UPDATE ON messprogramm FOR EACH ROW EXECUTE PROCEDURE update_letzte_aenderung();


--
-- Name: messprogramm_mmt; Type: TABLE; Schema: land; Owner: -; Tablespace:
--

CREATE TABLE messprogramm_mmt (
    id serial PRIMARY KEY,
    messprogramm_id integer NOT NULL REFERENCES messprogramm ON DELETE CASCADE,
    mmt_id character varying(2) NOT NULL REFERENCES stamm.mess_methode,
    messgroessen integer[],
    letzte_aenderung timestamp without time zone DEFAULT now()
);
CREATE TRIGGER letzte_aenderung_messprogramm_mmt BEFORE UPDATE ON messprogramm_mmt FOR EACH ROW EXECUTE PROCEDURE update_letzte_aenderung();


--
-- Name: probe; Type: TABLE; Schema: land; Owner: -; Tablespace:
--

CREATE TABLE probe (
    id serial PRIMARY KEY,
    ext_id character varying(16) UNIQUE NOT NULL
        DEFAULT 'sss'
            || lpad(nextval('land.probe_probe_id_seq')::varchar, 12, '0')
            || 'Y',
    test boolean DEFAULT false NOT NULL,
    mst_id character varying(5) NOT NULL REFERENCES stamm.mess_stelle,
    labor_mst_id character varying(5) NOT NULL REFERENCES stamm.mess_stelle,
    hauptproben_nr character varying(20),
    datenbasis_id smallint REFERENCES stamm.datenbasis,
    ba_id integer REFERENCES stamm.betriebsart,
    probenart_id smallint REFERENCES stamm.probenart,
    media_desk character varying(100) CHECK(media_desk LIKE '% %'),
    media character varying(100),
    umw_id character varying(3) REFERENCES stamm.umwelt,
    probeentnahme_beginn timestamp without time zone,
    probeentnahme_ende timestamp without time zone,
    mittelungsdauer bigint,
    letzte_aenderung timestamp without time zone DEFAULT now(),
    erzeuger_id integer REFERENCES stamm.datensatz_erzeuger,
    probe_nehmer_id integer REFERENCES stamm.probenehmer,
    mpl_id integer REFERENCES stamm.messprogramm_kategorie,
    mpr_id integer REFERENCES messprogramm,
    solldatum_beginn timestamp without time zone,
    solldatum_ende timestamp without time zone,
    tree_modified timestamp without time zone DEFAULT now(),
    rei_progpunkt_grp_id integer REFERENCES stamm.rei_progpunkt_gruppe,
    kta_gruppe_id integer REFERENCES stamm.kta_gruppe,
    UNIQUE (test, mst_id, hauptproben_nr),
    CHECK(solldatum_beginn <= solldatum_ende)
);
CREATE TRIGGER letzte_aenderung_probe BEFORE UPDATE ON probe FOR EACH ROW EXECUTE PROCEDURE update_letzte_aenderung();
CREATE TRIGGER tree_modified_probe BEFORE UPDATE ON probe FOR EACH ROW EXECUTE PROCEDURE update_tree_modified_probe();


--
-- Name: kommentar_p; Type: TABLE; Schema: land; Owner: -; Tablespace:
--

CREATE TABLE kommentar_p (
    id serial PRIMARY KEY,
    mst_id character varying(5) NOT NULL REFERENCES stamm.mess_stelle,
    datum timestamp without time zone DEFAULT now(),
    text character varying(1024),
    probe_id integer NOT NULL REFERENCES probe ON DELETE CASCADE
);


--
-- Name: ortszuordnung; Type: TABLE; Schema: land; Owner: -; Tablespace:
--

CREATE TABLE ortszuordnung (
    id serial PRIMARY KEY,
    probe_id integer NOT NULL REFERENCES probe ON DELETE CASCADE,
    ort_id integer NOT NULL REFERENCES stamm.ort,
    ortszuordnung_typ character varying(1) REFERENCES stamm.ortszuordnung_typ,
    ortszusatztext character varying(100),
    letzte_aenderung timestamp without time zone DEFAULT now(),
    tree_modified timestamp without time zone DEFAULT now(),
    EXCLUDE (probe_id WITH =) WHERE (ortszuordnung_typ = 'E')
);
CREATE TRIGGER letzte_aenderung_ortszuordnung BEFORE UPDATE ON ortszuordnung FOR EACH ROW EXECUTE PROCEDURE update_letzte_aenderung();
CREATE TRIGGER tree_modified_ortszuordnung BEFORE UPDATE ON ortszuordnung FOR EACH ROW EXECUTE PROCEDURE update_tree_modified();

--
-- Name: ortszuordnung_mp; Type: TABLE; Schema: land; Owner: -; Tablespace:
--

CREATE TABLE ortszuordnung_mp (
    id serial PRIMARY KEY,
    messprogramm_id integer NOT NULL REFERENCES messprogramm ON DELETE CASCADE,
    ort_id integer NOT NULL REFERENCES stamm.ort,
    ortszuordnung_typ character varying(1) REFERENCES stamm.ortszuordnung_typ,
    ortszusatztext character varying(100),
    letzte_aenderung timestamp without time zone DEFAULT now(),
    tree_modified timestamp without time zone DEFAULT now(),
    EXCLUDE (messprogramm_id WITH =) WHERE (ortszuordnung_typ = 'E')
);
CREATE TRIGGER letzte_aenderung_ortszuordnung_mp BEFORE UPDATE ON ortszuordnung_mp FOR EACH ROW EXECUTE PROCEDURE update_letzte_aenderung();

--
-- Name: zusatz_wert; Type: TABLE; Schema: land; Owner: -; Tablespace:
--

CREATE TABLE zusatz_wert (
    id serial PRIMARY KEY,
    probe_id integer NOT NULL REFERENCES probe ON DELETE CASCADE,
    pzs_id character varying(3) NOT NULL REFERENCES stamm.proben_zusatz,
    messwert_pzs double precision,
    messfehler real,
    letzte_aenderung timestamp without time zone DEFAULT now(),
    nwg_zu_messwert double precision,
    kleiner_als character varying(1),
    tree_modified timestamp without time zone DEFAULT now(),
    UNIQUE (probe_id, pzs_id)
);
CREATE TRIGGER letzte_aenderung_zusatzwert BEFORE UPDATE ON zusatz_wert FOR EACH ROW EXECUTE PROCEDURE update_letzte_aenderung();
CREATE TRIGGER tree_modified_zusatzwert BEFORE UPDATE ON zusatz_wert FOR EACH ROW EXECUTE PROCEDURE update_tree_modified();


--
-- Name: messung; Type: TABLE; Schema: land; Owner: -; Tablespace:
--

CREATE TABLE messung (
    id serial PRIMARY KEY,
    ext_id integer NOT NULL,
    probe_id integer NOT NULL REFERENCES probe ON DELETE CASCADE,
    nebenproben_nr character varying(4),
    mmt_id character varying(2) NOT NULL REFERENCES stamm.mess_methode ON DELETE CASCADE,
    messdauer integer,
    messzeitpunkt timestamp without time zone,
    fertig boolean DEFAULT false NOT NULL,
    status integer,
    letzte_aenderung timestamp without time zone DEFAULT now(),
    geplant boolean DEFAULT false NOT NULL,
    tree_modified timestamp without time zone DEFAULT now(),
    UNIQUE (id, ext_id),
    UNIQUE (id, nebenproben_nr)
);
CREATE TRIGGER letzte_aenderung_messung BEFORE UPDATE ON messung FOR EACH ROW EXECUTE PROCEDURE update_letzte_aenderung();
CREATE TRIGGER tree_modified_messung BEFORE UPDATE ON messung FOR EACH ROW EXECUTE PROCEDURE update_tree_modified_messung();
CREATE TRIGGER ext_id BEFORE INSERT ON land.messung FOR EACH ROW EXECUTE PROCEDURE set_messung_ext_id();
CREATE TRIGGER status_messung AFTER INSERT ON land.messung FOR EACH ROW EXECUTE PROCEDURE set_messung_status();

--
-- Name: kommentar_m; Type: TABLE; Schema: land; Owner: -; Tablespace:
--

CREATE TABLE kommentar_m (
    id serial PRIMARY KEY,
    mst_id character varying(5) NOT NULL REFERENCES stamm.mess_stelle,
    datum timestamp without time zone DEFAULT now(),
    text character varying(1024),
    messungs_id integer NOT NULL REFERENCES messung ON DELETE CASCADE
);


--
-- Name: messwert; Type: TABLE; Schema: land; Owner: -; Tablespace:
--

CREATE TABLE messwert (
    id serial PRIMARY KEY,
    messungs_id integer NOT NULL REFERENCES messung ON DELETE CASCADE,
    messgroesse_id integer NOT NULL REFERENCES stamm.messgroesse,
    messwert_nwg character varying(1),
    messwert double precision,
    messfehler real,
    nwg_zu_messwert double precision,
    meh_id smallint NOT NULL REFERENCES stamm.mess_einheit,
    grenzwertueberschreitung boolean DEFAULT false,
    letzte_aenderung timestamp without time zone DEFAULT now(),
    tree_modified timestamp without time zone DEFAULT now(),
    UNIQUE (messungs_id, messgroesse_id)
);
CREATE TRIGGER letzte_aenderung_messwert BEFORE UPDATE ON messwert FOR EACH ROW EXECUTE PROCEDURE update_letzte_aenderung();
CREATE TRIGGER tree_modified_messwert BEFORE UPDATE ON messwert FOR EACH ROW EXECUTE PROCEDURE update_tree_modified();

--
-- Name: status_protokoll; Type: TABLE; Schema: land; Owner: -; Tablespace:
--

CREATE TABLE status_protokoll (
    id serial PRIMARY KEY,
    mst_id character varying(5) NOT NULL REFERENCES stamm.mess_stelle,
    datum timestamp without time zone DEFAULT now(),
    text character varying(1024),
    messungs_id integer NOT NULL REFERENCES messung ON DELETE CASCADE,
    status_kombi integer NOT NULL REFERENCES stamm.status_kombi,
    tree_modified timestamp without time zone DEFAULT now()
);
CREATE TRIGGER tree_modified_status_protokoll BEFORE UPDATE ON status_protokoll FOR EACH ROW EXECUTE PROCEDURE update_tree_modified();

ALTER TABLE ONLY messung
    ADD CONSTRAINT messung_status_protokoll_id_fkey FOREIGN KEY (status) REFERENCES status_protokoll(id);

CREATE TABLE tagzuordnung
(
    id serial PRIMARY KEY,
    probe_id integer,
    tag_id integer,
    datum timestamp without time zone DEFAULT now(),
    CONSTRAINT tagzuordnung_tag_fkey FOREIGN KEY (tag_id)
        REFERENCES stamm.tag (id) MATCH SIMPLE
        ON DELETE CASCADE,
    CONSTRAINT tagzuordnung_probe_fkey FOREIGN KEY (probe_id)
        REFERENCES land.probe (id) MATCH SIMPLE
        ON DELETE CASCADE,
    UNIQUE (probe_id, tag_id)
);
--
-- Name: messung_probe_id_idx; Type: INDEX; Schema: land; Owner: -; Tablespace:
--

CREATE INDEX messung_probe_id_idx ON messung USING btree (probe_id);


--
-- Name: ort_probe_id_idx; Type: INDEX; Schema: land; Owner: -; Tablespace:
--

CREATE INDEX ort_probe_id_idx ON ortszuordnung USING btree (probe_id);


--
-- Name: zusatz_wert_probe_id_idx; Type: INDEX; Schema: land; Owner: -; Tablespace:
--

CREATE INDEX zusatz_wert_probe_id_idx ON zusatz_wert USING btree (probe_id);


--
-- Name: kommentar_probe_id_idx; Type: INDEX; Schema: land; Owner: -; Tablespace:
--

CREATE INDEX kommentar_probe_id_idx ON kommentar_p USING btree (probe_id);


--
-- Name: messwert_messungs_id_idx; Type: INDEX; Schema: land; Owner: -; Tablespace:
--

CREATE INDEX messwert_messungs_id_idx ON messwert USING btree (messungs_id);


--
-- Name: status_messungs_id_idx; Type: INDEX; Schema: land; Owner: -; Tablespace:
--

CREATE INDEX status_messungs_id_idx ON status_protokoll USING btree (messungs_id);


--
-- Name: kommentar_messungs_id_idx; Type: INDEX; Schema: land; Owner: -; Tablespace:
--

CREATE INDEX kommentar_messungs_id_idx ON kommentar_m USING btree (messungs_id);


--
-- Name: COLUMN ortszuordnung.ortszuordnung_typ; Type: COMMENT; Schema: land; Owner: -
--

COMMENT ON COLUMN ortszuordnung.ortszuordnung_typ IS 'E = Entnahmeport, U = Ursprungsort, Z = Ortszusatz';


--
-- Name: COLUMN probe.id; Type: COMMENT; Schema: land; Owner: -
--

COMMENT ON COLUMN probe.id IS 'interner Probenschlüssel';


--
-- Name: COLUMN probe.test; Type: COMMENT; Schema: land; Owner: -
--

COMMENT ON COLUMN probe.test IS 'Ist Testdatensatz?';


--
-- Name: COLUMN probe.mst_id; Type: COMMENT; Schema: land; Owner: -
--

COMMENT ON COLUMN probe.mst_id IS 'ID für Messstelle';


--
-- Name: COLUMN probe.labor_mst_id; Type: COMMENT; Schema: land; Owner: -
--

COMMENT ON COLUMN probe.labor_mst_id IS '-- ID für Messlabor';


--
-- Name: COLUMN probe.hauptproben_nr; Type: COMMENT; Schema: land; Owner: -
--

COMMENT ON COLUMN probe.hauptproben_nr IS 'externer Probensclüssel';


--
-- Name: COLUMN probe.ba_id; Type: COMMENT; Schema: land; Owner: -
--

COMMENT ON COLUMN probe.ba_id IS 'ID der Betriebsart (normal/Routine oder Störfall/intensiv)';


--
-- Name: COLUMN probe.probenart_id; Type: COMMENT; Schema: land; Owner: -
--

COMMENT ON COLUMN probe.probenart_id IS 'ID der Probenart(Einzel-, Sammel-, Misch- ...Probe)';


--
-- Name: COLUMN probe.media_desk; Type: COMMENT; Schema: land; Owner: -
--

COMMENT ON COLUMN probe.media_desk IS 'Mediencodierung (Deskriptoren oder ADV-Codierung)';


--
-- Name: COLUMN probe.media; Type: COMMENT; Schema: land; Owner: -
--

COMMENT ON COLUMN probe.media IS 'dekodierte Medienbezeichnung (aus media_desk abgeleitet)';


--
-- Name: COLUMN probe.umw_id; Type: COMMENT; Schema: land; Owner: -
--

COMMENT ON COLUMN probe.umw_id IS 'ID für Umweltbereich';


--
-- Name: COLUMN messprogramm.media_desk; Type: COMMENT; Schema: land; Owner: -
--

COMMENT ON COLUMN messprogramm.media_desk IS 'dekodierte Medienbezeichnung (aus media_desk abgeleitet)';


COMMIT;
