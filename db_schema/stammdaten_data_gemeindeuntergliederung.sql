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
-- Data for Name: gemeindeuntergliederung; Type: TABLE DATA; Schema: stamm; Owner: postgres
--

COPY stamm.gemeindeuntergliederung (id, netzbetreiber_id, gem_id, ozk_id, gemeindeuntergliederung, kda_id, koord_x_extern, koord_y_extern, geom, shape, letzte_aenderung) FROM stdin;
1	11	11000000	28	Johannisthal	1	5398276	5813574	0101000020E61000008CB96B09F9002B405BD3BCE314394A40	\N	2011-11-21 10:35:17
\.


--
-- Name: gemeindeuntergliederung_id_seq; Type: SEQUENCE SET; Schema: stamm; Owner: postgres
--

SELECT pg_catalog.setval('stamm.gemeindeuntergliederung_id_seq', 1, false);


--
-- PostgreSQL database dump complete
--

