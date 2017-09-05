\set ON_ERROR_STOP on

BEGIN;

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = stammdaten, pg_catalog;
CREATE TABLE zeitbasis (
    id  integer PRIMARY KEY,
    bezeichnung character varying(20) NOT NULL
);

COMMIT;
\i stammdaten_data_zeitbasis.sql
