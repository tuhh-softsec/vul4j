\set ON_ERROR_STOP on

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = stammdaten, pg_catalog;

COPY zeitbasis (id, bezeichnung) FROM stdin;
1	MESZ
2	Weltzeit (UTC)
3	MEZ
4	ges. Zeit
\.
