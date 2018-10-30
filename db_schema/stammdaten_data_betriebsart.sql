--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.2
-- Dumped by pg_dump version 10.5

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
-- Data for Name: betriebsart; Type: TABLE DATA; Schema: stamm; Owner: postgres
--

COPY stamm.betriebsart (id, name) FROM stdin;
1	Normal-/Routinebetrieb
2	Störfall-/Intensivbetrieb
3	Übung zum Störfall
\.


--
-- PostgreSQL database dump complete
--

