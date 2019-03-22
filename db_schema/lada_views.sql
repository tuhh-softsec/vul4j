--
-- PostgreSQL database dump
--

-- Dumped from database version 10.6
-- Dumped by pg_dump version 10.6

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: lada_messwert; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.lada_messwert AS
 SELECT messwert.id,
    messwert.messungs_id,
    messwert.messgroesse_id,
    messwert.messwert_nwg,
    messwert.messwert,
    messwert.messfehler,
    messwert.nwg_zu_messwert,
    messwert.meh_id,
    messwert.grenzwertueberschreitung,
    status_protokoll.status_kombi,
    messwert.letzte_aenderung
   FROM ((land.messwert
     JOIN land.messung ON ((messwert.messungs_id = messung.id)))
     JOIN land.status_protokoll ON (((messung.status = status_protokoll.id) AND (status_protokoll.status_kombi <> 1))));


ALTER TABLE public.lada_messwert OWNER TO postgres;

--
-- Name: TABLE lada_messwert; Type: ACL; Schema: public; Owner: postgres
--

GRANT SELECT ON TABLE public.lada_messwert TO lada;


--
-- PostgreSQL database dump complete
--

